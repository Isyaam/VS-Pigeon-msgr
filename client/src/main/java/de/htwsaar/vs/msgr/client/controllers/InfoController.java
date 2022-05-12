package de.htwsaar.vs.msgr.client.controllers;

import de.htwsaar.vs.msgr.client.DataModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class InfoController {

    private static final Logger log =
            LogManager.getLogger(InfoController.class);

    private final ObservableList<String> gList =
            FXCollections.observableArrayList();

    private final ObservableList<String> fList =
            FXCollections.observableArrayList();

    private DataModel _model;

    @FXML
    private ImageView userProfilePicture;

    @FXML
    private ImageView groupProfilePicture;

    @FXML
    private Label userName;

    @FXML
    private Label groupName;


    @FXML
    private Label nuMember;

    @FXML
    private Label showAdminName;

    /** Represents the search box. */
    @FXML
    private TextField newMember;

    @FXML
    private ListView<String> memberList;

    @FXML
    private ListView<String> contactList;

    /** Loads the group / user information. */
    public void init(DataModel model) {
        _model = model;
        initGroup();
    }


    /* *************************************************************************
     * USER INFO
     * ************************************************************************/

    /** Changes the user name. */
    @FXML
    void changeUserName(MouseEvent event) {
        userName.setText(_model.getChatName());
    }

    //TODO: to be implemented
    @FXML
    void showUserProfilePicture(MouseEvent event) {

    }


    /* *************************************************************************
     * GROUP INFO
     * ************************************************************************/

    public void initGroup() {


        char equal = _model.getChatID().charAt(0);
        if ((_model.getChatID().isEmpty())) {
            log.debug("nothing selected");
        } else if (equal == 'G') {
            groupName.setText(_model.getChatName());
            String currentItemSelected =
                    memberList.getSelectionModel().getSelectedItem();
            memberList.setItems(gList);


            for (int i = 0; i < _model.getCurrentGroupMembers().size(); i++) {
                gList.add(_model.getCurrentGroupMembers().get(i));
            }
            memberList.setItems(gList);
            int numberOfNumbers = gList.size();
            nuMember.setText(String.valueOf(numberOfNumbers));
            String adminUID = _model.getAdminUID();
            showAdminName.setText(_model.findAdmin(adminUID));

            groupList();
            contactList.setCellFactory(param -> new ListCell<String>() {
                public void updateItem(String name, boolean empty) {
                    super.updateItem(name, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(name);
                    }
                }
            });

            contactList.setItems(fList);
            memberList.setItems(gList);

            // Search Field
            FilteredList<String> filteredData = new FilteredList<>(fList,
                    s -> true);
            contactList.setItems(filteredData);
            newMember.textProperty().addListener((observable, oldValue,
                                                  newValue) -> {
                filteredData.setPredicate(element -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    if (element.contains(newValue)) {
                        return true;
                    }
                    return false;
                });
                contactList.setItems(filteredData);
            });

            contactList.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (event.getButton().equals(MouseButton.PRIMARY)) {
                        if (event.getClickCount() == 2) {
                            if (!gList.contains(contactList
                                    .getSelectionModel().getSelectedItem())) {
                                gList.add(contactList
                                        .getSelectionModel().getSelectedItem());
                            }
                        }
                    }
                }
            });

        } else {
            userName.setText(_model.getChatName());
        }

    }


    /** Adds Users from Database in Friend List */
    private void groupList() {
        String user;
        for (String name : _model.retrieveUserList()) {
            user = _model.filterUser(name);
            fList.add(user);
        }
    }

    //TODO: to be implemented
    @FXML
    void leaveGroup(ActionEvent event) {

    }

    @FXML
    void changeGroupName(MouseEvent event) {

        groupName.setText(_model.getChatName());
    }

    //TODO: to be implemented
    @FXML
    void showGroupProfilePicture(MouseEvent event) {

    }
}