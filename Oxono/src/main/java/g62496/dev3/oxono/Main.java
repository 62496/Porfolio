package g62496.dev3.oxono;
import g62496.dev3.oxono.view.MainView;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main class of the application. It extends the JavaFX Application class to launch the application.
 */
public class Main extends Application {
    /**
     * The main entry point of the Java application.
     * This method launches the JavaFX application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        launch(args);
    }
    /**
     * This method is automatically called when the application starts.
     * It creates an instance of MainView and sets the primary stage.
     *
     * @param stage The primary stage for this application.
     */
    @Override
    public void start(Stage stage) {
        MainView mainView = new MainView(stage);
    }
}
