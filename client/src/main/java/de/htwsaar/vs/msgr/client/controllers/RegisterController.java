package de.htwsaar.vs.msgr.client.controllers;

import de.htwsaar.vs.msgr.client.DataModel;
import de.htwsaar.vs.msgr.client.helper.Cryptography;
import de.htwsaar.vs.msgr.client.helper.WindowOpener;
import de.htwsaar.vs.msgr.client.rest.RestClient;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.security.NoSuchAlgorithmException;

public class RegisterController {

    /** Represents the model. */
    private DataModel _model;


    /* *************************************************************************
     * GUI ITEMS
     * ************************************************************************/

    @FXML
    private TextField name;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField confirmPw;

    @FXML
    private Label errorLabel;


    /* *************************************************************************
     * INITIALIZERS / CONSTRUCTORS
     * ************************************************************************/

    public void init(DataModel model) {
        _model = model;
    }

    /** Creates a new user. */
    @FXML
    void registerButton(ActionEvent event) {
        try {
            if (name.getText().isEmpty()
                    || password.getText().isEmpty()
                    || confirmPw.getText().isEmpty()) {
                name.setPromptText("Bitte einen Namen eingeben");
                password.setPromptText("Bitte Passwort eingeben");
                confirmPw.setPromptText("Bitte Passwort eingeben");
            } else if (!password.getText().equals(confirmPw.getText())) {
                errorLabel.setText("Passwort stimmt nicht überein");
            } else {
                String pwHash = Cryptography.toHexString(
                        Cryptography.getSHA(password.getText()));

                /* connect to Server and create a new User */
                if (_model.registerUser(name.getText(), pwHash) == 0) {
                    errorLabel.setText("Name bereits vergeben oder du bist " +
                            "Offline");
                } else {
                    Stage stage = (Stage) ((Node) event.getSource()).getScene()
                            .getWindow();
                    WindowOpener.changeScene(stage,
                            WindowOpener.getRegisterScene(_model));
                }
            }
        } catch (NoSuchAlgorithmException e) {
            System.out.println("Exception thrown for incorrect algorithm:" +
                    " " + e);
        }
    }

    @FXML
    void backButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        WindowOpener.changeScene(stage, WindowOpener.getLoginScene(_model));
    }

    // TODO is duplicated code, can be shrunk.
    @FXML
    void pressEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                if (name.getText().isEmpty()
                        || password.getText().isEmpty()
                        || confirmPw.getText().isEmpty()) {
                    name.setPromptText("Bitte einen Namen eingeben");
                    password.setPromptText("Bitte Passwort eingeben");
                    confirmPw.setPromptText("Bitte Passwort eingeben");
                } else if (!password.getText().equals(confirmPw.getText())) {
                    errorLabel.setText("Passwort stimmt nicht überein");
                } else {
                    String pwHash = Cryptography.toHexString(
                            Cryptography.getSHA(password.getText()));

                    /* connect to Server and create a new User */
                    if (_model.registerUser(name.getText(), pwHash) == 0) {
                        errorLabel.setText("Name bereits vergeben oder du " +
                                "bist Offline");
                    } else {
                        Stage stage =
                                (Stage) ((Node) event.getSource()).getScene()
                                .getWindow();
                        WindowOpener.replaceStage(stage,
                                WindowOpener.getLoginScene(_model));
                    }
                }
            } catch (NoSuchAlgorithmException e) {
                System.out.println("Exception thrown for incorrect algorithm:" +
                        " " + e);
            }
        }
    }
}
