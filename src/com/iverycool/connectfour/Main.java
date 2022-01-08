package com.iverycool.connectfour;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    
    private Controller controller;
    
    
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
        GridPane rootGridPane = loader.load();
        //giving Control
        controller = loader.getController();// giving access of control to fxml file
        controller.createPlayground();
        
        MenuBar menuBar = createMenu();
        Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
        menuPane.getChildren().add(menuBar);
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        
        //menuBar.prefHeightProperty().bind(menuPane.heightProperty());
        
    
        Scene scene = new Scene(rootGridPane); //defining the scene
    
        primaryStage.setScene(scene);  //assign stage to scene
        primaryStage.setTitle("Connect 4");
        primaryStage.setResizable(false);
        primaryStage.show();
        
        
    }
    private MenuBar createMenu() {
        
        Menu fileMenu = new Menu("File");
        
        MenuItem newGame = new MenuItem("New Game");
        newGame.setOnAction(event -> { resetGame(); });
        
        MenuItem resetGame = new MenuItem("Reset Game");
        resetGame.setOnAction(event -> { resetGame(); });
        
        SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
        MenuItem exitGame = new MenuItem("Exit Game");
        exitGame.setOnAction(event ->{ exitGame(); });
        fileMenu.getItems().addAll(newGame,resetGame,separatorMenuItem,exitGame);
    
        Menu helpMenu = new Menu("Help");
    
        MenuItem aboutGame = new MenuItem("About Connect 4");
        aboutGame.setOnAction(event -> gameInfo());
        
        MenuItem aboutMe = new MenuItem("About Me");
        aboutMe.setOnAction(event -> meInfo());
        helpMenu.getItems().addAll(aboutGame,separatorMenuItem,aboutMe);
        
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu,helpMenu);
        return menuBar;
    }
    
    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }
    
    private void meInfo() {
        Alert meInfo = new Alert(Alert.AlertType.INFORMATION);
        meInfo.setTitle("About Me");
        meInfo.setHeaderText("WHo AM MEE ??????");
        meInfo.setContentText("What can I say about me"+"\n"+
                "Its just.... there is soooo much to tell "+ "\n" +
                "But in brief, iVERYcool" + "\n"+
                "Thank You :) ");
        meInfo.show();
    }
    
    private void gameInfo() {
    
        Alert gameInfo = new Alert(Alert.AlertType.INFORMATION);
        gameInfo.setTitle("About Connect 4");
        gameInfo.setHeaderText("How to Play");
        gameInfo.setContentText("Connect Four is a two-player connection game in which the players first choose a color and then take turns dropping colored "+
                "discs from the top into a seven-column, six-row vertically suspended grid. " +
                "The pieces fall straight down, occupying the next available space within the column. The objective of the game is"+
                "to be the first to form a horizontal, vertical, or diagonal line of four of one's own discs. Connect Four is a solved game."+
                "The first player can always win by playing the right moves.");
        gameInfo.show();
        
    }
    
    private void resetGame() {
        controller.resetGame();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
}
