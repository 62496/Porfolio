package app.model;

import app.dto.ViewWithController;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViewWithControllerTest {

    @Test
    void testViewAndControllerAccessors() {
        Parent view = new Pane();
        Object controller = new Object();

        ViewWithController vwc = new ViewWithController(view, controller);

        assertEquals(view, vwc.view());
        assertEquals(controller, vwc.controller());
    }

    @Test
    void testEqualsAndHashCode() {
        Parent view = new Pane();
        Object controller = new Object();

        ViewWithController vwc1 = new ViewWithController(view, controller);
        ViewWithController vwc2 = new ViewWithController(view, controller);

        assertEquals(vwc1, vwc2);
        assertEquals(vwc1.hashCode(), vwc2.hashCode());
    }
}

