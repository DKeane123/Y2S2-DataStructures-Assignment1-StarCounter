package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import main.HelloApplication;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

public class HelloController extends HelloApplication {


    //Stores the width of the image
    int width;

    //Stores the height of the image
    int height;

    //Stores the amount of pixels of the biggest set in the picture
    int biggestSet;

    int object;

    //The setArray array stores the value of each pixel in the picture
    public int[] setArray;

    //The pixelCount array uses the index of each array position as the root and the value stored is the amount
    //of pixels that are of that root
    public int[] pixelCount;

    //Stores a backup of the image imported
    Image backupImage;

    //Is the image that functions are allowed to edit
    Image unionImage;

    //Stores a backup of the coloured image
    Image colourImage;

    //Button to reset the whole program to remove circles
    @FXML
    Button resetButton;

    @FXML
    Text starCount;

    //ImageView to display the original image
    @FXML
    private ImageView originalImage = new ImageView();

    //ImageView to display the edited image
    @FXML
    private ImageView editImage = new ImageView();

    //Slider that allows the changing of the accepted pixel brightness
    @FXML
    Slider setBrightness;

    //Slider to choose how many pixels need to be in a set to be counted
    @FXML
    Slider pixelCutoff;


    //---------//
    // Methods //
    //---------//

    //Method to open and load images
    public void loadEventHandler(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        //Show open file dialog
        File file = fileChooser.showOpenDialog(null);

        //Check to make sure the chosen file is valid
        if (file != null) {
            //If the selected item is valid the variable image is set to the item
            Image image = new Image(file.toURI().toString(), 400, 400, false,false);
            originalImage.setImage(image);
            backupImage = image;
            //changePictureInfo();
        }
    }

    //Method to change the selected image to black and white
    public void ImageToBlackOrWhite() {
        //The image that is currently in the editImage ImageView is removed
        resetImage();
        WritableImage writableImage = new WritableImage((int) originalImage.getImage().getWidth(), (int) originalImage.getImage().getHeight());
        PixelReader pixelReader = originalImage.getImage().getPixelReader();
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        int picSize = (int) (originalImage.getImage().getWidth() * originalImage.getImage().getHeight());
        setArray = new int[picSize];
        pixelCount = new int[picSize];
        width = (int) originalImage.getImage().getWidth();
        height = (int) originalImage.getImage().getHeight();
        int arrayPosition = 0;
        //For loops that go through each pixel of the image
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                //Declaring local rgb variables and a brightness variable from the pixel
                double red;
                double green;
                double blue;
                Double brightness = pixelReader.getColor(j, i).getBrightness();
                //Checks if the brightness of a pixel is greater than the value of the slider
                if(brightness > setBrightness.getValue()) {
                    //If this is true the rgb variables are set to 1 making the colour white
                    red = 1;
                    green = 1;
                    blue = 1;
                    //The value of the pixel is set to the same as its index in the array
                    setArray[arrayPosition] = arrayPosition;
                } else {
                    //If this is false the rgb variables are set to 0 to make the colour black
                    red = 0;
                    green = 0;
                    blue = 0;
                    //The value of the pixel is set to -1
                    setArray[arrayPosition] = -1;
                }
                arrayPosition++;
                //The pixel is set to either black or white
                Color newBrightness = Color.color(red, green, blue);
                pixelWriter.setColor(j, i, newBrightness);
                unionImage = writableImage;
                editImage.setImage(writableImage);
            }
        }
        //The union function joins all pixels that are in direct contact with each-other
        union();
        //The number assigned to each pixel is printed to the console
        for(int n = 0; n < setArray.length; n++) {
            //System.out.print(find(setArray,n) + (((n+1)%unionImage.getWidth() == 0) ? "\n" : " "));
        }
    }

    //Method to count the amount of celestial objects and the amount of pixels in each object
    public void pixelNum() {
        int value;
        int num = 0;
        object = 0;
        biggestSet = 0;
        //The pixelCount array is filled with zeros
        Arrays.fill(pixelCount, 0);
        for(int i = 0; i < setArray.length; i++) {
            //The value variable is set as the number in each setArray index
            value = find(setArray,i);
            //If this number is not -1 then the equivalent index in the pixelCount array is increased by 1
            if (value >= 0) {
                pixelCount[value] = pixelCount[value] + 1;
            }
        }
        for (int b = 0;b < pixelCount.length;b++)
            if (pixelCount[b] > biggestSet) {
                biggestSet = pixelCount[b];
            }
        //The amount of celestial bodies and pixels is then printed to the console
        for(int j = 0; j < pixelCount.length; j++) {
            if (pixelCount[num] >= pixelCutoff.getValue()) {
                //System.out.println("Celestial Object [" + object + "] with root " + j + " has " + pixelCount[num] + " pixels.");
                object++;
            }
            num++;
        }
        starCount.setText("There are " + object + " stars in this image");
//        System.out.println(biggestSet);
    }

    //Method to scan the image for sets and apply a random colour to each one
    public void colorAllObjects() {
        int root;
        int numberStore[] = new int[setArray.length];
        WritableImage writableImage = new WritableImage((int) editImage.getImage().getWidth(), (int) editImage.getImage().getHeight());
        PixelWriter pixelWriter = writableImage.getPixelWriter();
        for (root = 0; root < pixelCount.length; root++) {
            //This code creates a random colour that changes every time a new root number is checked
            Random random = new Random();
            float red = random.nextFloat(0.25f, 0.90f);
            float green = random.nextFloat(0.25f,0.90f);
            float blue = random.nextFloat(0.25f,0.90f);
            Color rColour =Color.color(red, green, blue);
            //Checks if the amount of pixels associated with a root number is greater than the user specified amount
            if (pixelCount[root] >= pixelCutoff.getValue()) {
                for (int i = 0; i < setArray.length; i++) {
                    //Scans the setArray array for pixels with the root value
                    if(find(setArray,i) == root) {
                        //If this is true each pixel with a value equal to the root number is set to rColour
                        int xCoord = i % width;
                        int yCoord = i/ width;
                        pixelWriter.setColor(xCoord, yCoord, rColour);
                        unionImage = writableImage;
                        editImage.setImage(writableImage);
                    }
                    //If this is false then each pixel with a value equal to -1 is reset to black
                    else if (setArray[i] == -1) {
                        int xCoord = i % width;
                        int yCoord = i/ width;
                        Color black = Color.BLACK;
                        pixelWriter.setColor(xCoord, yCoord, black);
                        unionImage = writableImage;
                        editImage.setImage(writableImage);
                    }
                }
            }
            //If a set is too small each pixel of the set is set to black
            if ((pixelCount[root] < pixelCutoff.getValue()) && pixelCount[root] > 0) {
                for (int i = 0; i < setArray.length; i++) {
                    //Scans the setArray array for pixels with the root value
                    if (find(setArray, i) == root) {
                        //If this is true each pixel with a value equal to the root number is set to black
                        int xCoord = i % width;
                        int yCoord = i / width;
                        Color black = Color.BLACK;
                        pixelWriter.setColor(xCoord, yCoord, black);
                        unionImage = writableImage;
                        editImage.setImage(writableImage);
                    }
                }
            }
        }
    }

    public void addCirclesAndText() {
        //The count variable is used to number each set
        int count = 1;
        PixelReader pixelReader = originalImage.getImage().getPixelReader();
        //The for loop starts at the biggest number stored as biggestSet and decreases until it is equal to the pixel
        //size specified by the user
        for (int b = biggestSet; b >= pixelCutoff.getValue(); b--) {
            //The for loop goes through the pixelCount array
            for (int i = 0; i < pixelCount.length; i++) {
                //Each value in the pixelCount array is checked to see if it is equal to the biggestSet number
                if (pixelCount[i] == b) {
                    Circle circle = new Circle();
                    //These four variables are used to store the furthest points of each set
                    int littleX = width;
                    int littleY = height;
                    int bigX = 0;
                    int bigY = 0;
                    int radius = 0;
                    //These three variables are used to store the total amount of each RGB value
                    double sulpherSum = 0;
                    double hydrogenSum = 0;
                    double oxygenSum = 0;
                    for (int j = 0; j < setArray.length; j++) {
                        //Scans the setArray array for pixels with the root value
                        if (find(setArray, j) == i) {
                            int xCoord = j % width;
                            int yCoord = j / width;
                            //Every pixel of a set is scanned to find the RGB value which is added to the variables
                            sulpherSum = sulpherSum + pixelReader.getColor(xCoord, yCoord).getRed();
                            hydrogenSum = hydrogenSum + pixelReader.getColor(xCoord, yCoord).getGreen();
                            oxygenSum = oxygenSum + pixelReader.getColor(xCoord, yCoord).getBlue();
                            //Finds the left-most point of a set
                            if (xCoord < littleX) {
                                littleX = xCoord;
                            }
                            //Finds the right-most point of a set
                            if (xCoord > bigX) {
                                bigX = xCoord;
                            }
                            //Finds the highest point of a set
                            if (yCoord < littleY) {
                                littleY = yCoord;
                            }
                            //Finds the lowest point of a set
                            if (yCoord > bigY) {
                                bigY = yCoord;
                            }
                            //Makes the radius by checking if left to right or top to bottom is longer
                            int sizeA = (bigX - littleX);
                            int sizeB = (bigY - littleY);
                            if (sizeA > sizeB) {
                                radius = sizeA + 3;
                            } else {
                                radius = sizeB + 3;
                            }
                        }
                    }
                    //The above values are read in to create a circle at the centre of the set with a tooltip that
                    //contains the relevant data
                    circle.setCenterX((int) ((littleX + bigX) / 2) + 15);
                    circle.setCenterY((int) ((littleY + bigY) / 2) + 35);
                    circle.setRadius(radius);
                    circle.setFill(Color.TRANSPARENT);
                    circle.setStroke(Color.BLUE);
                    circle.setStrokeWidth(1);
                    circle.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent mouseEvent) {
                            int root = 0;
                            WritableImage writableImage = new WritableImage((int) editImage.getImage().getWidth(), (int) editImage.getImage().getHeight());
                            PixelWriter pixelWriter = writableImage.getPixelWriter();
                            colourImage = editImage.getImage();
                            ImageToBlackOrWhite();
                            int xCoord = (int) circle.getCenterX();
                            int yCoord = (int) circle.getCenterY();
                            for (int i = 0;i < setArray.length;i++) {
                                if((i % width == xCoord) && (i / width == yCoord)) {
                                    if (setArray[i] > -1) {
                                        root = setArray[i];
                                    }
                                }
                            }
                            for (int j = 0; j <setArray.length;j++) {
                                if (setArray[root] == j) {
                                    int editxCoord = j % width;
                                    int edityCoord = j/ width;
                                    pixelWriter.setColor(editxCoord, edityCoord, Color.ORANGE);
                                    unionImage = writableImage;
                                    editImage.setImage(writableImage);
                                }
                            }
                        }
                    });
                    Tooltip tooltip = new Tooltip(
                            "Celestial Object Number: " + count + "\n"
                                    + "Root Number: " + i + "\n"
                                    + "Estimate Size in Pixel Units: " + pixelCount[i] + "\n"
                                    + "Estimated Sulphur: " + sulpherSum / pixelCount[i] + "\n"
                                    + "Estimated Hydrogen: " + hydrogenSum / pixelCount[i] + "\n"
                                    + "Estimated Oxygen: " + oxygenSum / pixelCount[i]);
                    Tooltip.install(circle, tooltip);
                    //A number is added above the image to help identify the biggest/smallest/etc
                    Text text = new Text(((littleX + bigX)/2+15)+radius, ((littleY + bigY)/2+35)-radius, String.valueOf(count));
                    text.setStroke(Color.RED);
                    count++;
                    //The circle is added to the imageView
                    ((Pane) originalImage.getParent()).getChildren().add(circle);
                    ((Pane) originalImage.getParent()).getChildren().add(text);
                }
            }
        }
    }

    public void undoSelection() {
        editImage.setImage(colourImage);
    }

    public void union() {
        int width = (int) originalImage.getImage().getWidth();
        for (int i = 0; i < setArray.length-1; i++) {
            if (setArray[i] != -1 && setArray[i+1] != -1) {
                setArray[find(setArray,i+1)] = find(setArray,i);
            }
        }
        for (int i = 0; i < setArray.length-width; i++) {
            if (setArray[i] != -1 && setArray[i+width] != -1) {
                setArray[find(setArray,i+width)] = find(setArray,i);
            }
        }
    }

    public static int find(int[] a, int id) {
        if(a[id]==id) return id;
        if(a[id]==-1) return -1;
        else return find(a,a[id]);
    }

    //Sets the picture stored in the ImageView to the backup image as if it was imported again
    public void resetImage() {
        originalImage.setImage(backupImage);
    }

    public void resetProgram() throws IOException {
        reset();
    }

    //Ends the program
    public void endProgram() {
        primaryStage.close();
    }

    //--------------------------//
    // Helpful Print Statements //
    //--------------------------//

    // line 131 - System.out.print(find(setArray,n) + (((n+1)%unionImage.getWidth() == 0) ? "\n" : " "));
    // line 158 - System.out.println("Celestial Object [" + object + "] with root " + j + " has " + pixelCount[num] + " pixels.");
    // line 163 - System.out.println(biggestSet);
}