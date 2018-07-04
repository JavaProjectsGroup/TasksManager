package com.projects.tasksmanager;

import com.projects.tasksmanager.usermodel.Task;
import com.projects.tasksmanager.usermodel.UserData;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;

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

    private UserData user = new UserData();

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

//        Filtering list of tasks to show only the ones from current day of the week
//        wantDailyTasks = new Predicate<Task>() {
//            @Override
//            public boolean test(Task task) {
//                return (task.getDayOfWeek() == Calendar.getInstance().get(Calendar.DAY_OF_WEEK));
//            }
//        };
//        filteredList = new FilteredList<>(user.getTasks(),wantDailyTasks);

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
        Timeline durationTimeline = new Timeline(new KeyFrame(Duration.millis(1000), event -> {
            Task task = tasksList.getSelectionModel().getSelectedItem();
            if(task.getDuration().toSeconds() < 86399) {
                task.setDuration(task.getDuration().plusSeconds(1));
                refreshDurationLabel(task);
            }
        }));
        durationTimeline.setCycleCount(Animation.INDEFINITE);

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
                durationTimeline.stop();
            }
        });

        addTaskButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                durationTimeline.pause();

                Dialog<ButtonType> dialog = new Dialog<>();
                dialog.initOwner(mainPane.getScene().getWindow());
                dialog.setTitle("Add new task");

                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("createTask.fxml"));
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
                    CreateTask createTask = fxmlLoader.getController();
                    Task newTask = createTask.loadResult();
                    user.addTaskToList(newTask);
                    tasksList.getSelectionModel().select(newTask);
                }
            }
        });

        //Creating ContextMenu for adding/deleting tasks
        MenuItem deleteMenuItem = new MenuItem("Delete task");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                durationTimeline.pause();
                deleteTask(tasksList.getSelectionModel().getSelectedItem());
            }
        });
        MenuItem editMenuItem = new MenuItem("Edit task");
        editMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                durationTimeline.pause();
                editTask(tasksList.getSelectionModel().getSelectedItem());
            }
        });
        cellContextMenu.getItems().addAll(deleteMenuItem,editMenuItem);


        //Refreshing the duration in Center property of BorderPane after clicking on another task on ListView
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

        //Formatting and displaying current time
        DateFormat timeFormat = new SimpleDateFormat("EEEE, HH:mm:ss");
        Timeline clockTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> {
                                timeLabel.setText(timeFormat.format(System.currentTimeMillis()));
                        }));
        clockTimeline.setCycleCount(Animation.INDEFINITE);
        clockTimeline.play();
    }

    private void editTask(Task selectedTask) {
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
            user.editTaskOnList(selectedTask,editedTask);
            tasksList.getSelectionModel().select(editedTask);
        }
    }

    private void deleteTask(Task selectedTask) {
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

    @FXML
    public void createUserDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Create new user");
        dialog.setHeaderText("Please input your username");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("createUser.fxml"));
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
            CreateUser createUser = fxmlLoader.getController();

            user = createUser.loadResult();
            user.clearTasks();

            //Displaying current user's name
            userLabel.setText("Hello, "+user.getUsername());
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

    @FXML
    public void saveUserDialog() {
        try {
            user.saveUser();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void loadUserDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainPane.getScene().getWindow());
        dialog.setTitle("Load user");
        dialog.setHeaderText("Please select user from the list below");

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("loadUser.fxml"));
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
            LoadUser loadUserFile = fxmlLoader.getController();
            File userFile = loadUserFile.loadFile();
            if(userFile == null) {
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

//        FileChooser chooser = new FileChooser();
//        File defaultDirectory = new File(System.getProperty("user.dir")+"/UsersDatabase/");
//        chooser.setInitialDirectory(defaultDirectory);
//        File file = chooser.showOpenDialog(mainPane.getScene().getWindow());
//        if(file != null) {
//            String fileName = file.getName();
//            if(fileName.endsWith(".bin")) {
//                try {
//                    user.loadUser(file.getPath());
//                } catch (IOException | ClassNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//                //Displaying current user's name
//                userLabel.setText("Hello, " + user.getUsername());
//                //Changing the temporary label after loading an user
//                tempLabel.setText("Select the task you want to perform");
//                addTaskButton.setDisable(false);
//                saveMenuItem.setDisable(false);
//
//                //Binding data from ListView with user's tasks
//                tasksList.getSelectionModel().clearSelection();
//                tasksList.setItems(user.getTasks());
//                tasksList.setDisable(false);
//                tasksList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//            } else {
//                Alert alert = new Alert(Alert.AlertType.ERROR);
//                alert.setTitle("Error");
//                alert.setContentText("Invalid filetype!");
//                alert.show();
//            }
//        }
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

    public void changesNotSavedAlert(String problemType) {
        //TODO: Alert for closing app/creating new user/loading new user without saving changes
    }

    public void refreshDurationLabel(Task selectedTask) {
        long sec = selectedTask.getDuration().getSeconds();
        durationLabel.setText(String.format("%02d:%02d",sec/60,sec%60));
    }
}
