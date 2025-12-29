package app.controller;

import org.junit.jupiter.api.Test;

public class OcrControllerTest {
    @Test
    void testSetUserName() {
        OcrController controller = new OcrController();
        controller.setUserName("Tester");
    }
}
