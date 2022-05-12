package de.htwsaar.vs.msgr.client.controllers;

import de.htwsaar.vs.msgr.client.DataModel;
import de.htwsaar.vs.msgr.client.Main;
import de.htwsaar.vs.msgr.client.helper.FileOpener;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class SettingsController {

    private static final Logger log = LogManager.getLogger(Main.class);

    private DataModel _model;

    /** Represents the big profile picture shown in the header. */
    @FXML
    private ImageView profilePicture;


    /* *************************************************************************
     * HEADER
     * Information shown above the accordion menu.
     * ************************************************************************/

    /** Represents the userID. */
    @FXML
    private Label userId;

    /** Represents the userName. */
    @FXML
    private Label userNameHeader;

    /** Represents how long the user account exists. */
    @FXML
    private Label userSince;

    /** Represents the profile picture shown under various options. */
    @FXML
    private ImageView profilePictureSmall;


    /* *************************************************************************
     * VARIOUS OPTIONS
     * ************************************************************************/

    /** Represents the user name shown under various options. */
    @FXML
    private Label userNameVariousOptions;

    /** Represents the list of blocked users. */
    @FXML
    private ListView blockedUsersListView;

    /** Represents the currently used server address. */
    @FXML
    private Label serverAddress;

    private StringProperty userName = new SimpleStringProperty();


    /* *************************************************************************
     * INITIALIZERS
     * ************************************************************************/

    public void init(DataModel model) {

        _model = model;

        /* Bind userName textProperties to each other. */
        userName.bindBidirectional(userNameHeader.textProperty());
        userName.bindBidirectional(userNameVariousOptions.textProperty());
    }


    /* *************************************************************************
     * MISCELLANEOUS
     * ************************************************************************/

    /**
     * Called to change the profile picture, either by clicking on the profile
     * picture or the menu entry.
     */
    @FXML
    private void changeProfilePicture(MouseEvent event) {
        File f = FileOpener.chooseProfilePicture();
        if (f == null) {
            log.info("No new profile picture has been selected.");
            return;
        }
        setProfilePicture(f);
        setProfilePictureSmall(f);
    }

    /** Changes the username of the user. */
    @FXML
    private void changeUserName(MouseEvent event) {
        log.debug("To be implemented yet.");
        userName.set("yoshegg");
    }


    /* *************************************************************************
     * INTERNAL METHODS & FIELDS
     * Fields and short methods that are used by other methods.
     * ************************************************************************/

    /** Changes the server address. */
    @FXML
    private void changeServerAddress(MouseEvent event) {
        log.debug("To be implemented yet.");
    }

    private void setProfilePicture(File f) {
        Image i = new Image(f.toURI().toString(),
                100,
                100,
                true,
                true,
                true);
        profilePicture.setImage(i);
    }

    private void setProfilePictureSmall(File f) {
        Image i = new Image(f.toURI().toString(),
                40,
                40,
                true,
                true,
                true);
        profilePictureSmall.setImage(i);
    }

}
