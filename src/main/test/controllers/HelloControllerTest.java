package controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloControllerTest {

    private HelloController helloController;

    public int[] setArray = {0, -1, -1, 3, 3, 3, 3, -1, 8, 8, -1, 11, -1, -1, -1, 15};

    public int[] pixelCount;

    public int count;

    @BeforeEach
    void setUp() {
        helloController = new HelloController();
        int value = 0;
        pixelCount = new int[setArray.length];
        for (int i = 0; i < setArray.length; i++) {
            value = helloController.find(setArray, i);
            if (value >= 0) {
                pixelCount[value] = pixelCount[value] + 1;
            }
            if (pixelCount[i] > -1) {
                count++;
            }
        }
    }

    @AfterEach
    void tearDown() {
        setArray = null;
        pixelCount = null;
    }

    @Test
    public void testArrayContents() {
        assertEquals(1,pixelCount[0]);
        assertEquals(4,pixelCount[3]);
        assertEquals(2,pixelCount[8]);
        assertNotEquals(2,pixelCount[11]);
        assertNotEquals(1,pixelCount[14]);
    }
    @Test
    public void testArraySize() {
        assertEquals(16, setArray.length);
    }

    @Test
    public void testArrayCount() {
        assertEquals(5, count);
    }
}