package com.projects.tasksmanager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;

import java.io.File;
import java.util.Collections;

public class LoadUser {
    @FXML
    private ListView<String> usersListView;
    private ObservableList<String> usersList = FXCollections.observableArrayList();
    private final String pathName = System.getProperty("user.dir")+"\\UsersDatabase\\";

    public void initialize() {
        usersListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        File folder = new File(pathName);
        File[] listOfFiles = folder.listFiles();
        if(listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".bin")) {
                    usersList.add(file.getName().replace(".bin", ""));
                }
            }
        }
        usersListView.setItems(usersList);
        Collections.sort(usersList);
    }

    public File loadFile() {
        if (!usersListView.getSelectionModel().isEmpty()) {
            String userName = usersListView.getSelectionModel().getSelectedItem();
            return new File(pathName + File.separator + userName + ".bin");
        } else {
            return null;
        }
    }
}
