package de.htwsaar.vs.msgr.client;

import de.htwsaar.vs.msgr.client.helper.WindowOpener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Main extends Application {

    private static final Logger log = LogManager.getLogger(Main.class);

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Correctly exits this JavaFX application.
     */
    public static void exitPigeon() {
        Platform.exit();
    }

    /**
     * Starts this application and loads e.g. the GUI.
     *
     * @param stage The default stage
     */
    @Override
    public void start(Stage stage) throws IOException {

        /* Initialize data model */
        DataModel model = new DataModel();

        /* Prepare GUI (login window) */
        Parent content = WindowOpener.createLoginWindow(model);

        /* Show GUI. */
        stage.setScene(new Scene(content));
        stage.show();
    }

}

