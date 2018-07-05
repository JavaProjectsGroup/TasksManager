package com.projects.tasksmanager;

import com.projects.tasksmanager.usermodel.Task;
import com.projects.tasksmanager.usermodel.UserData;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Optional;

public class Controller {
    @FXML
    private BorderPane mainPane;
    @FXML
    private Label userLabel;
    @FXML
    private Label timeLabel;
    @FXML
    private Label tempLabel;
    @FXML
    private Button addTaskButton;
    @FXML
    private Button moveUpTaskButton;
    @FXML
    private Button moveDownTaskButton;
    @FXML
    private MenuItem saveMenuItem;
    @FXML
    private ListView<Task> tasksList;

    //Variables for current UserData and saved UserData
    private UserData user = new UserData();
    private UserData savedUser = new UserData();

    private Timeline durationTimeline;

    private VBox containerBox = new VBox();
    private HBox buttonsBox = new HBox();
    private Label durationLabel = new Label("mm:ss");
    private Button contButton = new Button();
    private Button stopButton = new Button();
    private ContextMenu cellContextMenu = new ContextMenu();

    //private FilteredList<Task> filteredList;
    //private Predicate<Task> wantDailyTasks;

    public void initialize() {
        //Modifying look of Labels and Buttons
        stopButton.setFont(new Font("Arial bold",20));
        contButton.setFont(new Font("Arial bold",20));
        stopButton.setGraphic(new ImageView(new Image("/toolbarButtonGraphics/media/Stop24.gif")));
        contButton.setGraphic(new ImageView(new Image("/toolbarButtonGraphics/media/Play24.gif")));

        durationLabel.setFont(new Font("Arial bold",60));
        buttonsBox.setAlignment(Pos.CENTER);
        buttonsBox.setSpacing(30);
        containerBox.setAlignment(Pos.CENTER);

        //Creating a VBox container with displayed data
        containerBox.getChildren().add(durationLabel);
        buttonsBox.getChildren().addAll(contButton,stopButton);
        containerBox.getChildren().add(buttonsBox);

        //Filtering list of tasks to show only the ones from current day of the week
        //wantDailyTasks = new Predicate<Task>() {
        //  @Override
        //  public boolean test(Task task) {
        //      return (task.getDayOfWeek() == Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
        //  }
        //};
        //filteredList = new FilteredList<>(user.getTasks(),wantDailyTasks);

        //Modifying look of displayed text in a cell
        //Adding ContextMenu to ListView
        tasksList.setCellFactory(new Callback<ListView<Task>, ListCell<Task>>() {
            @Override
            public ListCell<Task> call(ListView<Task> param) {
                ListCell<Task> listCell = new ListCell<Task>() {
                    @Override
                    protected void updateItem(Task item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty) {
                            setText(null);
                        } else {
                            setText(item.getName());//Display task's name
                        }
                    }
                };
                listCell.emptyProperty().addListener(
                        (obs, wasEmpty, isNowEmpty) -> {
                            if(isNowEmpty) {
                                listCell.setContextMenu(null);
                            } else {
                                listCell.setContextMenu(cellContextMenu);
                            }
                        });
                return listCell;
            }
        });

        //Creating a Timeline for changing tasks durations every second
        durationTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
            Task task = tasksList.getSelectionModel().getSelectedItem();
            if(task.getDuration().toSeconds() < 86399) {
                task.setDuration(task.getDuration().plusSeconds(1));
                refreshDurationLabel(task);
            }
        }));
        durationTimeline.setCycleCount(Animation.INDEFINITE);

        //Binding status of Timeline with displayed image (pause or play)
        durationTimeline.statusProperty().addListener(new ChangeListener<Animation.Status>() {
            @Override
            public void changed(ObservableValue<? extends Animation.Status> observable, Animation.Status oldValue, Animation.Status newValue) {
                if(newValue==Animation.Status.PAUSED) {
                    contButton.setGraphic(new ImageView(new Image("/toolbarButtonGraphics/media/Play24.gif")));
                } else if (newValue==Animation.Status.RUNNING) {
                    contButton.setGraphic(new ImageView(new Image("/toolbarButtonGraphics/media/Pause24.gif")));
                } else if (newValue==Animation.Status.STOPPED) {
                    contButton.setGraphic(new ImageView(new Image("/toolbarButtonGraphics/media/Play24.gif")));
                }
            }
        });

        //Adding setOnAction events to Buttons
        contButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if(durationTimeline.getStatus().equals(Animation.Status.STOPPED) || durationTimeline.getStatus().equals(Animation.Status.PAUSED)) {
                    durationTimeline.play();
                } else {
                    durationTimeline.pause();
                }
            }
        });
        stopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                //TODO: Adding comment when stopped, disabling buttons (play/pause and stop)
                durationTimeline.stop();
            }
        });
        addTaskButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                durationTimeline.pause();

                //Creating DialogPair to return default Dialog and FXMLLoader
                DialogPair dialogPair = dialogCreation("Add new task", "Please input task's name", "createTask.fxml");

                Optional<ButtonType> clickResult = dialogPair.getDialog().showAndWait();
                if(clickResult.isPresent() && clickResult.get() == ButtonType.OK) {
                    CreateTask createTask = dialogPair.getFxmlLoader().getController();
                    Task newTask = createTask.loadResult();
                    user.addTaskToList(newTask);
                    tasksList.getSelectionModel().select(newTask);
                }
            }
        });

        //Creating ContextMenu for editing/deleting tasks
        MenuItem deleteMenuItem = new MenuItem("Delete task");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                deleteTaskDialog(tasksList.getSelectionModel().getSelectedItem());
            }
        });
        MenuItem editMenuItem = new MenuItem("Edit task");
        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editTaskDialog(tasksList.getSelectionModel().getSelectedItem());
            }
        });
        cellContextMenu.getItems().addAll(deleteMenuItem,editMenuItem);

        //Refreshing the duration in Center property of BorderPane
        tasksList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Task>() {
            @Override
            public void changed(ObservableValue<? extends Task> observable, Task oldValue, Task newValue) {
                if(!tasksList.getSelectionModel().isEmpty()) {
                    mainPane.setCenter(containerBox);
                    refreshDurationLabel(newValue);
                    moveUpTaskButton.setDisable(false);
                    moveDownTaskButton.setDisable(false);
                } else {
                    mainPane.setCenter(tempLabel);
                    moveUpTaskButton.setDisable(true);
                    moveDownTaskButton.setDisable(true);
                }
                durationTimeline.pause();
            }
        });

        //Adding setOnCloseRequest to main window
        //Stage is not yet created -> using runLater to avoid NullPointerException
        mainPane.sceneProperty().addListener(new ChangeListener<Scene>() {
            @Override
            public void changed(ObservableValue<? extends Scene> observable, Scene oldScene, Scene newScene) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        Stage stage = (Stage) newScene.getWindow();
                        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent event) {
                                durationTimeline.pause();
                                changesNotSavedAlert();
                                Platform.exit();
                            }
                        });
                    }
                });
            }
        });

        //Formatting and displaying current time
        DateFormat timeFormat = new SimpleDateFormat("EEEE, HH:mm:ss");
        Timeline clockTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
                                timeLabel.setText(timeFormat.format(System.currentTimeMillis()));
                        }));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    @FXML
    public void createUserDialog() {
        durationTimeline.pause();
        changesNotSavedAlert();

        //Creating DialogPair to return default Dialog and FXMLLoader
        DialogPair dialogPair = dialogCreation("Create new user", "Please input your username", "createUser.fxml");

        Optional<ButtonType> clickResult = dialogPair.getDialog().showAndWait();
        if(clickResult.isPresent() && clickResult.get() == ButtonType.OK) {
            CreateUser createUser = dialogPair.getFxmlLoader().getController();
            UserData newUser = createUser.loadResult();
            if(!isUsernameValid(newUser.getUsername())) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Invalid username!");
                alert.show();
            } else {
                user = newUser;
                user.clearTasks();

                //Copying user's data
                try {
                    savedUser.copyUser(user);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                //Displaying current user's name
                userLabel.setText("Hello, " + user.getUsername());
                //Changing the temporary label after creating an user
                tempLabel.setText("Select the task you want to perform");
                addTaskButton.setDisable(false);
                saveMenuItem.setDisable(false);

                //Binding data from ListView with user's tasks
                tasksList.getSelectionModel().clearSelection();
                tasksList.setItems(user.getTasks());
                tasksList.setDisable(false);
                tasksList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            }
        }
    }

    @FXML
    public void saveUserDialog() {
        durationTimeline.pause();

        try {
            user.saveUser();
            try {
                savedUser.copyUser(user);
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadUserDialog() {
        durationTimeline.pause();
        changesNotSavedAlert();

        DialogPair dialogPair = dialogCreation("Load user", "Please select user from the list below", "loadUser.fxml");

        Optional<ButtonType> clickResult = dialogPair.getDialog().showAndWait();
        if (clickResult.isPresent() && clickResult.get() == ButtonType.OK) {
            LoadUser loadUserFile = dialogPair.getFxmlLoader().getController();
            File userFile = loadUserFile.loadFile();
            if (userFile == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Please select user!");
                alert.show();
            } else {
                try {
                    user.loadUser(userFile.getPath());
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }

                try {
                    savedUser.copyUser(user);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                //Displaying current user's name
                userLabel.setText("Hello, " + user.getUsername());
                //Changing the temporary label after loading an user
                tempLabel.setText("Select the task you want to perform");
                addTaskButton.setDisable(false);
                saveMenuItem.setDisable(false);

                //Binding data from ListView with user's tasks
                tasksList.getSelectionModel().clearSelection();
                tasksList.setItems(user.getTasks());
                tasksList.setDisable(false);
                tasksList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            }
        }
    }

    @FXML
    public void moveTask(ActionEvent e) {
        if(!tasksList.getSelectionModel().isEmpty()) {
            if (e.getSource().equals(moveUpTaskButton)) {
                if (tasksList.getSelectionModel().getSelectedIndex() > 0) {
                    Collections.swap(user.getTasks(), tasksList.getSelectionModel().getSelectedIndex(), tasksList.getSelectionModel().getSelectedIndex() - 1);
                    tasksList.getSelectionModel().selectPrevious();
                }
            } else if (e.getSource().equals(moveDownTaskButton)) {
                if (tasksList.getSelectionModel().getSelectedIndex() < user.getTasks().size() - 1) {
                    Collections.swap(user.getTasks(), tasksList.getSelectionModel().getSelectedIndex(), tasksList.getSelectionModel().getSelectedIndex() + 1);
                    tasksList.getSelectionModel().selectNext();
                }
            }
        }
    }

    private void changesNotSavedAlert() {
        if(!user.equals(savedUser)) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Save changes?");
            alert.setHeaderText("Do you want to save changes?");
            alert.setContentText("Press OK to save changes, or close the window if you want to exit without saving");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && (result.get() == ButtonType.OK)) {
                saveUserDialog();
            }
        }
    }

    private void refreshDurationLabel(Task selectedTask) {
        long sec = selectedTask.getDuration().getSeconds();
        durationLabel.setText(String.format("%02d:%02d",sec/60,sec%60));
    }

    private void editTaskDialog(Task selectedTask) {
        durationTimeline.pause();

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Edit task");
        dialog.setHeaderText("Change task's name and task's time");

        PassTask.getInstance().setTaskName(selectedTask.getName());
        PassTask.getInstance().setTaskTime(selectedTask.getDuration());

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("editTask.fxml"));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e) {
            System.out.println("Couldn't load the dialog!");
            e.printStackTrace();
            return;
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> clickResult = dialog.showAndWait();
        if(clickResult.isPresent() && clickResult.get() == ButtonType.OK) {
            EditTask editTask = fxmlLoader.getController();
            Task editedTask = editTask.loadResult();
            user.editTaskOnList(selectedTask, editedTask);
            tasksList.getSelectionModel().select(editedTask);
        }
    }

    private void deleteTaskDialog(Task selectedTask) {
        durationTimeline.pause();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete task?");
        alert.setHeaderText("Delete item: "+selectedTask.getName());
        alert.setContentText("Press OK to confirm, or Cancel to return.");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && (result.get() == ButtonType.OK)) {
            user.removeTaskFromList(selectedTask);
            tasksList.getSelectionModel().clearSelection();
        }
    }

    private DialogPair dialogCreation(String title, String header, String fxml) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(fxml));
        try {
            dialog.getDialogPane().setContent(fxmlLoader.load());
        } catch(IOException e) {
            System.out.println("Couldn't load the dialog!");
            e.printStackTrace();
        }
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        DialogPair newPair = new DialogPair(dialog,fxmlLoader);

        return newPair;
    }

    private boolean isUsernameValid(String newUsername) {
        File folder = new File(System.getProperty("user.dir")+"\\UsersDatabase\\");
        File[] listOfFiles = folder.listFiles();

        if(newUsername.isEmpty()) {
            return false;
        }
        for (int i=0; i<listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().toLowerCase().equals(newUsername.toLowerCase()+".bin")) {
                return false;
            }
        }
        return true;
    }
}
