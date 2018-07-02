package com.projects.tasksmanager;

import com.projects.tasksmanager.usermodel.UserData;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class CreateUser {
    @FXML
    private TextField usernameText;

    public UserData loadResult() {
        String usernameString = usernameText.getText().trim();
        UserData newUser = new UserData(usernameString);
        return newUser;
    }
}
