package main.java.com.gearrentpro;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/login.fxml"));
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        
        stage.setTitle("GearRent Pro - Login");
        stage.setScene(scene);
        stage.show();
    }
    
    public static void switchScene(String fxmlFile, String title) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource("/fxml/" + fxmlFile));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Main.class.getResource("/css/styles.css").toExternalForm());
            primaryStage.setTitle("GearRent Pro - " + title);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}