package de.htwsaar.vs.msgr.client.controllers;

import de.htwsaar.vs.msgr.client.DataModel;
import de.htwsaar.vs.msgr.client.helper.WindowOpener;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ChatController {

    private static final Logger log
            = LogManager.getLogger(ChatController.class);


    /* *************************************************************************
     * PROPERTIES & FIELDS
     * ************************************************************************/

    /** Represents the friend list. */
    private ObservableList<String> fList
            = FXCollections.observableArrayList();

    /** Represents the chat. */
    private ObservableList<String> messageList
            = FXCollections.observableArrayList();

    /** Represents the model. */
    private DataModel _model;


    /* *************************************************************************
     * GUI ITEMS
     * ************************************************************************/

    @FXML
    private ListView<String> chat;

    @FXML
    private TextArea messageField;

    @FXML
    private ListView<String> friendList = new ListView<>(fList);

    @FXML
    private TextField searchBox;

    @FXML
    private ImageView profilePicture;

    @FXML
    private Label userName;

    @FXML
    private MenuButton menuBar;

    @FXML
    private Circle onlineStatusBadge;

    @FXML
    private Label onlineStatusLabel;


    /* *************************************************************************
     * CONSTRUCTOR & INIT METHODS
     * ************************************************************************/

    public void init(DataModel model) {
        _model = model;

        /* Bind lists from model to lists of controller. */
        Bindings.bindContentBidirectional(model.messageList, messageList);
        Bindings.bindContentBidirectional(model.contactList, fList);

        initFriendList();

        _model.isUserOnlineProperty().addListener((l, o, n) -> {
            Platform.runLater(() -> {
                if (n) {
                    onlineStatusBadge.setFill(Color.LIGHTGREEN);
                    onlineStatusLabel.setText("Online");
                } else {
                    onlineStatusBadge.setFill(Color.RED);
                    onlineStatusLabel.setText("Offline");
                }
            });
        });
    }

    /** Initializes the friend list. */
    private void initFriendList() {

        // Load user list from database
        userList();

        // Friend list change cell in ListView: add Image
        friendList.setItems(fList);
        Image image = new Image("icons/genericAvatar.png",
                40, // requested width
                40, // requested height
                true, // preserve ratio
                true, // smooth rescaling
                true // load in background
        );
        friendList.setCellFactory(param -> new ListCell<String>() {
            private ImageView imageView = new ImageView();

            @Override
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    imageView.setImage(image);
                    setGraphic(imageView);
                    setText(name);
                }
            }
        });
        // if click Item in Listview then open the chat
        friendList.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                messageList.clear();
                String currentItemSelected =
                        friendList.getSelectionModel().getSelectedItem();
                userName.setText(currentItemSelected);
                _model.loadChatInfo(currentItemSelected);
                loadMessageList();
                chat.setItems(messageList);
            }
        });


        // Searchbox for Users
        FilteredList<String> filteredData = new FilteredList<>(fList,
                s -> true);
        friendList.setItems(filteredData);
        searchBox.textProperty()
                .addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(element -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                if (element.contains(newValue)) {
                    return true;
                }
                return false;
            });
            friendList.setItems(filteredData);
        });
    }


    /* *************************************************************************
     * METHODS
     * ************************************************************************/

    /** Loads ands fills the friend list. */
    private void userList() {
        for (String name : _model.retrieveUserList()) {
            fList.add(name);
        }
    }

    /** Loads messages from database and fills the chat. */
    private void loadMessageList() {
        for (String message : _model.retrieveMessageList(_model.getChatID())) {
            messageList.add(message);
        }
    }


    /* *************************************************************************
     * GUI METHODS
     * ************************************************************************/

    @FXML
    void newContact(ActionEvent event) {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        WindowOpener.showAddUserWindow(stage, _model);
    }

    @FXML
    void newGroup(ActionEvent event) {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        WindowOpener.showCreateGroupWindow(stage, _model);
    }

    @FXML
    private void showInfo(MouseEvent event) {
        Stage stage = (Stage) messageField.getScene().getWindow();
        WindowOpener.showInfoWindow(stage, _model);
    }

    @FXML
    void newChat(ActionEvent event) {
        log.debug("To be implemented yet");
        log.debug("Retrieve messages");
    }

    @FXML
    void memo(ActionEvent event) {
        log.debug("To be implemented yet");
        log.debug("Send message");
    }

    @FXML
    private void openSettings(ActionEvent event) {
        Stage stage = (Stage) menuBar.getScene().getWindow();
        WindowOpener.showSettingsWindow(stage, _model);
    }

    @FXML
    void logout(ActionEvent event) {
        Stage stage = (Stage) menuBar.getScene()
                .getWindow();
        WindowOpener.replaceStage(stage, WindowOpener.getLoginScene(_model));
    }

    @FXML
    void attach(MouseEvent event) {
        log.debug("To be implemented yet");
    }

    @FXML
    void sendMessage(MouseEvent event) {
        if (userName.getText().equals("")) {
            messageList.add("Bitte ein Chatfenster wählen");
            chat.setItems(messageList);
        } else if (messageField.getText().isEmpty()) {

        } else {
            _model.sendMessage(_model.getOwnName() + ": " +
                    messageField.getText());
            messageList.add(_model.getOwnName() + ": " +
                    messageField.getText());
            messageField.clear();
            log.debug("Sent a message");
        }
    }
}


