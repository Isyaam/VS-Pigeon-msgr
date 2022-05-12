package de.htwsaar.vs.msgr.client.helper;

import de.htwsaar.vs.msgr.client.DataModel;
import de.htwsaar.vs.msgr.client.controllers.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;


public class WindowOpener {

    private static final Logger log = LogManager.getLogger(WindowOpener.class);

    /**
     * Creates a window linked to the main stage and with an .fxml defined
     * AnchorPane as background.
     *
     * @param originStage The origin stage
     * @param page        The .fxml defined AnchorPane
     * @return The stage of the new window
     */
    private static Stage createWindow(Stage originStage, AnchorPane page,
                                      String title) {

        return createWindow(originStage, page, title, Modality.NONE,
                true);
    }

    /**
     * Creates a window linked to another stage and with an .fxml defined
     * AnchorPane as background.
     *
     * @param originStage The origin stage
     * @param page        The .fxml defined AnchorPane
     * @param title       The title of the window
     * @param modality    The modality, defines if input for other windows is
     *                    blocked
     * @param resizable   Forbids scaling of window
     * @return The stage of the new window
     */
    private static Stage createWindow(Stage originStage, AnchorPane page,
                                      String title, Modality modality,
                                      boolean resizable) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.initModality(modality);
        stage.initOwner(originStage);

        stage.setScene(new Scene(page));
        stage.setResizable(resizable);
        stage.setOnCloseRequest(value -> stage.close());

        return stage;
    }

    /**
     * Replaces the current stage with another one. Useful when logging in or
     * out.
     */
    public static void replaceStage(Stage stage, Scene newScene) {
        stage.hide();

        Stage newStage = new Stage();
        newStage.setScene(newScene);
        newStage.show();
    }

    /**
     * Changes the scene of the current stage.
     * Useful for switching between login and register window.
     */
    public static void changeScene(Stage stage, Scene newScene) {
        stage.setScene(newScene);
    }



    /* *************************************************************************
     * LOGIN WINDOW
     * ************************************************************************/

    /** Switches to the login window. */
    public static Scene getLoginScene(DataModel model) {
        Parent content = createLoginWindow(model);
        if (content == null) {
            log.error("Something went wrong while switching to the login " +
                    "window.");
            return null;
        }

        return new Scene(content);
    }

    /** Creates the login window. */
    public static Parent createLoginWindow(DataModel model) {

        /* Load .fxml file */
        Parent content;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(WindowOpener.class.getClassLoader()
                    .getResource("fxml/login.fxml"));
            content = loader.load();
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }

        /* Set controller */
        LoginController controller = loader.getController();
        controller.init(model);

        return content;
    }


    /* *************************************************************************
     * REGISTER WINDOW
     * ************************************************************************/

    /** Switches to the login window. */
    public static Scene getRegisterScene(DataModel model) {
        Parent content = createRegisterWindow(model);
        if (content == null) {
            log.error("Something went wrong while switching to the " +
                    "register window.");
            return null;
        }
        return new Scene(content);
    }

    /** Creates the login window. */
    private static Parent createRegisterWindow(DataModel model) {

        /* Load .fxml file */
        Parent content;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(WindowOpener.class.getClassLoader()
                    .getResource("fxml/register.fxml"));
            content = loader.load();
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }

        /* Set controller */
        RegisterController controller = loader.getController();
        controller.init(model);

        return content;
    }


    /* *************************************************************************
     * MAIN (CHAT) WINDOW
     * ************************************************************************/

    /** Opens the mainStage and fixes its size. */
    public static void openMainStage(Stage stage, DataModel model) {
        stage.hide();

        Scene mainScene = getMainScene(model);

        Stage newStage = new Stage();
        newStage.setScene(mainScene);

        /* Avoid scaling problems when window becomes too small. */
        newStage.setMinWidth(800);
        newStage.setMinHeight(600);

        newStage.show();
    }


    /** Switches to the main window. */
    private static Scene getMainScene(DataModel model) {
        Parent content = createMainWindow(model);
        if (content == null) {
            log.error("Something went wrong while switching to the " +
                    "main window.");
            return null;
        }

        return new Scene(content);
    }

    /** Creates the login window. */
    private static Parent createMainWindow(DataModel model) {

        /* Load .fxml file */
        Parent content;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(WindowOpener.class.getClassLoader()
                    .getResource("fxml/chat.fxml"));
            content = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.toString());
            return null;
        }

        /* Set controller */
        ChatController controller = loader.getController();
        controller.init(model);

        return content;
    }


    /* *************************************************************************
     * SETTINGS WINDOW
     * ************************************************************************/

    /** Shows the settings window (and creates it if needed). */
    public static void showSettingsWindow(Stage mainStage, DataModel model) {
        Stage settingsWindow = createSettingsWindow(mainStage, model);
        if (settingsWindow == null) {
            log.error("Something went wrong while opening the settings " +
                    "window.");
            return;
        }
        settingsWindow.show();
    }

    /** Creates a new settings window. */
    private static Stage createSettingsWindow(Stage mainStage,
                                              DataModel model) {

        /* Load .fxml file */
        AnchorPane page;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(WindowOpener.class.getClassLoader()
                    .getResource("fxml/settings.fxml"));
            page = (AnchorPane) loader.load();
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }

        /* Set controller */
        SettingsController controller = loader.getController();
        controller.init(model);

        /* Set different characteristics of window */
        boolean resizable = false;
        Modality modality = Modality.APPLICATION_MODAL;

        return createWindow(mainStage, page, "Settings", modality,
                resizable);
    }


    /* *************************************************************************
     * INFO WINDOW
     * ************************************************************************/

    /** Shows the info window (and creates it if needed). */
    public static void showInfoWindow(Stage mainStage, DataModel model) {
        Stage infoWindow = createInfoWindow(mainStage, model);
        if (infoWindow == null) {
            log.error("Something went wrong while opening the info " +
                    "window.");
            return;
        }
        infoWindow.show();
    }

    /** Creates a new info window. */
    private static Stage createInfoWindow(Stage mainStage,
                                          DataModel model) {
        if (model.getChatID() == null) {
            log.info("No chat selected");
            return null;
        }

        char equal = model.getChatID().charAt(0);

        /* Used to distinguish between infoGroup and infoUser. */
        URL fxmlInfoFile;

        if (equal == 'G') {
            fxmlInfoFile = WindowOpener.class.getClassLoader().getResource(
                    "fxml/infoGroup.fxml");
        } else {
            fxmlInfoFile = WindowOpener.class.getClassLoader().getResource(
                    "fxml/infoUser.fxml");
        }

        /* Load .fxml file */
        AnchorPane page;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(fxmlInfoFile);
            page = (AnchorPane) loader.load();
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }

        /* Set controller */
        InfoController controller = loader.getController();
        controller.init(model);

        /* Set different characteristics of window */
        boolean resizable = false;
        Modality modality = Modality.APPLICATION_MODAL;

        return createWindow(mainStage, page, "Info", modality,
                resizable);
    }

    /* *************************************************************************
     * CREATE GROUP WINDOW
     * ************************************************************************/

    /** Shows the createGroup window (and creates it if needed). */
    public static void showCreateGroupWindow(Stage mainStage, DataModel model) {
        Stage createGroupWindow = createCreateGroupWindow(mainStage, model);
        if (createGroupWindow == null) {
            log.error("Something went wrong while opening the createGroup " +
                    "window.");
            return;
        }
        createGroupWindow.show();
    }

    /** Creates a new createGroup window. */
    private static Stage createCreateGroupWindow(Stage mainStage,
                                                 DataModel model) {

        /* Load .fxml file */
        AnchorPane page;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(WindowOpener.class.getClassLoader()
                    .getResource("fxml/createGroup.fxml"));
            page = (AnchorPane) loader.load();
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }

        /* Set controller */
        GroupController controller = loader.getController();
        controller.init(model);

        /* Set different characteristics of window */
        boolean resizable = false;
        Modality modality = Modality.APPLICATION_MODAL;

        return createWindow(mainStage, page, "Gruppe erstellen", modality,
                resizable);
    }

    public static void showAddUserWindow(Stage mainStage, DataModel model) {
        Stage createAddUserWindow = createAddUserWindow(mainStage, model);
        if (createAddUserWindow == null) {
            log.error("Something went wrong while opening the createGroup " +
                    "window.");
            return;
        }
        createAddUserWindow.show();
    }

    /** Creates a new createGroup window. */
    private static Stage createAddUserWindow(Stage mainStage,
                                                 DataModel model) {

        /* Load .fxml file */
        AnchorPane page;
        FXMLLoader loader;
        try {
            loader = new FXMLLoader(WindowOpener.class.getClassLoader()
                    .getResource("fxml/addfriend.fxml"));
            page = (AnchorPane) loader.load();
        } catch (IOException e) {
            log.error(e.toString());
            return null;
        }

        /* Set controller */
        AddFriendController controller = loader.getController();
        controller.init(model);

        /* Set different characteristics of window */
        boolean resizable = false;
        Modality modality = Modality.APPLICATION_MODAL;

        return createWindow(mainStage, page, "Settings", modality,
                resizable);
    }
}