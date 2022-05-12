package de.htwsaar.vs.msgr.client.controllers;

import de.htwsaar.vs.msgr.client.DataModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class AddFriendController implements Initializable {

    private static final Logger log =
            LogManager.getLogger(AddFriendController.class);

    private DataModel _model;

    private String uid;

    private String currentlySelectedItem;

    private ObservableList<String> userList
            = FXCollections.observableArrayList();

    @FXML
    private TextField searchFriend;

    @FXML
    private ListView<String> findUsers;

    @FXML
    private Button cancelButton;

    @FXML
    private Button okButton;

    public void init(DataModel model) {
        _model = model;
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {

        findUsers.setCellFactory(param -> new ListCell<String>() {
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(name);
                }
            }
        });

        findUsers.setItems(userList);
        findUsers.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                currentlySelectedItem = findUsers.getSelectionModel()
                        .getSelectedItem();

            }
        });
    }

    /** Adds the user to the database. */
    @FXML
    void okButton(MouseEvent event) {

        _model.addUser(uid , currentlySelectedItem);
        Stage stage = (Stage) okButton.getScene().getWindow();
        stage.close();
    }

    /** Cancels adding a new user to the database. */
    @FXML
    void cancelButton(MouseEvent event) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    /** Connects to the server and loads the userId. */
    @FXML
    void pressEnter(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            uid = _model.searchUser(searchFriend.getText());
            if (uid == null) {
                log.error("Could not find user. ");
            } else {
                userList.add(searchFriend.getText());
            }
        }
    }
}
