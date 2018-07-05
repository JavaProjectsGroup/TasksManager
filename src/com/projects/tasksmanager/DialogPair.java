package com.projects.tasksmanager;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

public class DialogPair {
    private Dialog<ButtonType> dialog;
    private FXMLLoader fxmlLoader;

    public DialogPair(Dialog<ButtonType> dialog, FXMLLoader fxmlLoader) {
        this.dialog = dialog;
        this.fxmlLoader = fxmlLoader;
    }

    public Dialog<ButtonType> getDialog() {
        return dialog;
    }

    public void setDialog(Dialog<ButtonType> dialog) {
        this.dialog = dialog;
    }

    public FXMLLoader getFxmlLoader() {
        return fxmlLoader;
    }

    public void setFxmlLoader(FXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }
}
