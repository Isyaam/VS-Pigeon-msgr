package de.htwsaar.vs.msgr.client.controllers;

import de.htwsaar.vs.msgr.client.DataModel;
import de.htwsaar.vs.msgr.client.helper.Cryptography;
import de.htwsaar.vs.msgr.client.helper.WindowOpener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.NoSuchAlgorithmException;

public class LoginController {

    private static final Logger log =
            LogManager.getLogger(LoginController.class);

    private DataModel _model;

    @FXML
    private PasswordField pwField;

    @FXML
    private TextField nameField;

    @FXML
    private Label errorLabel;

    public void init(DataModel model) {
        _model = model;
    }

    @FXML
    void login(ActionEvent event) {
        try {
            if (pwField.getText().isEmpty() || nameField.getText().isEmpty()) {
                errorLabel.setText("Bitte Username und Passwort eingeben");
            } else {
                String pwHash = Cryptography.toHexString(
                        Cryptography.getSHA(pwField.getText()));

                _model.setPwHash(pwHash);
                _model.setOwnName(nameField.getText());
                int ret = _model.loginToServer(nameField.getText());
                if (ret == 1) {
                    _model.loadUserInfo(nameField.getText());
                    Stage stage =
                            (Stage) ((Node) event.getSource()).getScene()
                                    .getWindow();
                    WindowOpener.openMainStage(stage, _model);
                } else if (ret == 0) {
                    errorLabel.setText("Username und Passwort stimmen nicht" +
                            "überein");
                } else {
                    errorLabel.setText("Keine Verbindung zum Server");
                }
            }
        }
        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            log.error("Exception thrown for incorrect algorithm: " + e);
            e.printStackTrace();
        }
    }

    @FXML
    void registerMenuButton(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        WindowOpener.changeScene(stage, WindowOpener.getRegisterScene(_model));
    }

    // TODO is duplicated code, can be shrunk.
    @FXML
    void pressEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            try {
                if (pwField.getText().isEmpty() ||
                        nameField.getText().isEmpty()) {
                    errorLabel.setText("Bitte Username und Passwort eingeben");
                } else {
                    String pwHash = Cryptography.toHexString(
                            Cryptography.getSHA(pwField.getText()));

                    _model.setPwHash(pwHash);
                    _model.setOwnName(nameField.getText());
                    int ret = _model.loginToServer(nameField.getText());
                    if (ret == 1) {
                        _model.loadUserInfo(nameField.getText());
                        Stage stage =
                                (Stage) ((Node) event.getSource()).getScene()
                                        .getWindow();
                        WindowOpener.openMainStage(stage, _model);
                    } else if (ret == 0) {
                        errorLabel.setText("Username und Passwort stimmen " +
                                "nicht" +
                                "überein");
                    } else {
                        errorLabel.setText("Keine Verbindung zum Server");
                    }
                }
            }
            // For specifying wrong message digest algorithms
            catch (NoSuchAlgorithmException e) {
                log.error("Exception thrown for incorrect algorithm: " + e);
                e.printStackTrace();
            }
        }
    }
}
