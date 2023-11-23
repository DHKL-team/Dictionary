package Commandline;

import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class Main extends Application {
    private double x = 0;
    private double y = 0;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root_load = FXMLLoader.load(getClass().getResource("LoadGui.fxml"));
        Scene scene_load = new Scene(root_load,788,550);
        stage.setScene(scene_load);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.show();
        Parent root = FXMLLoader.load(getClass().getResource("DictionaryGui.fxml"));


        root.setOnMousePressed(MouseEvent -> {
            x = MouseEvent.getSceneX();
            y = MouseEvent.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - x);
            stage.setY(event.getScreenY() - y);

        });



        Task<Scene> renderTask = new Task<>() {
            @Override
            protected Scene call() throws Exception {
                // Simulate a long-running task by pausing for a few seconds
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // Create the main scene
                return new Scene(root, 1030, 679);
            }
        };
        renderTask.setOnSucceeded(event -> stage.setScene(renderTask.getValue()));
        //stage.setScene(scene);

        new Thread(renderTask).start();
    }

    public static void main(String[] args) {
        launch();
    }
}