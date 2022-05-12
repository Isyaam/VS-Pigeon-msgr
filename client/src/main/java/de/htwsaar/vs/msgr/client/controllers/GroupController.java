package de.htwsaar.vs.msgr.client.controllers;

import de.htwsaar.vs.msgr.client.DataModel;
import de.htwsaar.vs.msgr.client.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;


public class GroupController {

    private static final Logger log = LogManager.getLogger(Main.class);

    /** Represents findContactGroup */
    final ObservableList<String> friendList =
            FXCollections.observableArrayList();

    /** Represents allUsersGroup */
    final ObservableList<String> groupList =
            FXCollections.observableArrayList();

    @FXML
    private TextField createNewGroupName;

    @FXML
    private TextField searchBoxGroup;

    /** Represents users from contact list. */
    @FXML
    private ListView<String> findContactGroup;

    /** Represents selected users for GroupCreate. */
    @FXML
    private ListView<String> allUsersGroup;

    private DataModel _model;


    public void init(DataModel model) {
        _model = model;
        initGroup();
    }

    private void initGroup() {
        groupList();
        findContactGroup.setCellFactory(param -> new ListCell<String>() {
            public void updateItem(String name, boolean empty) {
                super.updateItem(name, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(name);
                }
            }
        });

        findContactGroup.setItems(friendList);
        allUsersGroup.setItems(groupList);

        // Search Field
        FilteredList<String> filteredData = new FilteredList<>(friendList,
                s -> true);
        findContactGroup.setItems(filteredData);
        searchBoxGroup.textProperty().addListener((observable, oldValue,
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
            findContactGroup.setItems(filteredData);
        });

        findContactGroup.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        if (!groupList.contains(findContactGroup
                                .getSelectionModel().getSelectedItem())) {
                            groupList.add(findContactGroup
                                    .getSelectionModel().getSelectedItem());
                        }
                    }
                }
            }
        });
    }

    /**Load the Users from Database*/
    private void groupList() {
        String user;
        for (String name : _model.retrieveUserList()) {
            user = _model.filterUser(name);
            friendList.add(user);
        }
    }


    /** Creates a new group. */
    @FXML
    void createGroup(ActionEvent event) {
        log.debug("open create group window");
        ArrayList<String> group = new ArrayList<>();
        for (String user : groupList) {
            System.out.println(user);
            group.add(user);
        }
        _model.addGroup(createNewGroupName.getText(), group);
    }

}
