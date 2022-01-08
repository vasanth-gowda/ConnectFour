package com.iverycool.connectfour;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable {
	
	private static final int COLUMNS = 7;
	private static final int ROWS = 6;
	private static final int CIRCLE_DIAMETER= 100;
	private static final String discColor2 = "#006666";
	private static final String discColor1 = "#09021C";
	
	private static String PLAYER_ONE = "Player One's";
	private static String PLAYER_TWO = "Player Two's";
	private static boolean isPlayerOne = true;
	private Disc[][] insertedDiscArray = new Disc[ROWS][COLUMNS];
	
	@FXML
	public GridPane rootGridPane;
	
	@FXML
	public Pane insertedDiscPane;
	
	@FXML
	public Label playerName;
	
	@FXML
	public TextField playerOneTextField, playerTwoTextField;
	
	@FXML
	public Button setNamesButton;
	
	
	
	private boolean isAllowedToInsert = true;
	
	public void createPlayground(){
		
		
		setNamesButton.setOnAction(event -> setNames());
		//game space is a rectangle with circular holes
		Shape gameSpace = createRectangleGrid();
		rootGridPane.add(gameSpace,0,1);
		
		List<Rectangle> rectangleList = clickableColumns();
		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle,0,1);
		}
		/*
		Or
		for(int col = 0; col<7; col++)
			rootGridPane.add(rectangleList.get(col), 0, 1); */
	}
	
	private void setNames() {
		
		String name1 = playerOneTextField.getText();
		String name2 = playerTwoTextField.getText();
		
		PLAYER_ONE=(name1+"'s");
		PLAYER_TWO=(name2+"'s");
	}
	
	
	private Shape createRectangleGrid() {
		
		//game space is a rectangle with circular holes
		Shape gameSpace = new Rectangle(CIRCLE_DIAMETER*(COLUMNS+1), CIRCLE_DIAMETER*(ROWS+1) );
		
		
		for (int row=0; row<ROWS;row++) {
			for (int col = 0; col < COLUMNS; col++) {
				
				Circle circle = new Circle();
				circle.setRadius(CIRCLE_DIAMETER/2);
				circle.setCenterX(circle.getRadius());
				circle.setCenterY(circle.getRadius());
				circle.setSmooth(true);
				
				circle.setTranslateX(col*(CIRCLE_DIAMETER+8) + 28);
				circle.setTranslateY(row*(CIRCLE_DIAMETER+8) + 28);
				
				gameSpace = Shape.subtract(gameSpace,circle);
				
			}
		}
		gameSpace.setFill(Color.WHITE);
		return gameSpace;
	}
	
	private List<Rectangle> clickableColumns(){
		
		List<Rectangle> rectanglesList = new ArrayList<>();
		
		for (int col = 0 ; col<7 ; col++) {
			Rectangle rectangle = new Rectangle(CIRCLE_DIAMETER, CIRCLE_DIAMETER * (ROWS + 1));
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col*(CIRCLE_DIAMETER+8) + 28);
			
			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#00000007")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));
			
			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if(isAllowedToInsert) {
					isAllowedToInsert=false;
					insertDisc(new Disc(isPlayerOne), column);
					
				}
			
			});
			
			rectanglesList.add(rectangle);
		}
		return rectanglesList;
	}
	
	private void insertDisc(Disc disc , int col)  {
		
		int row = ROWS-1;
		
		while (row >=0){
			if(insertedDiscArray[row][col] == null)
				break;
			--row;
		}
		if(row<0) {
			return ;
		}
		insertedDiscArray [row][col] = disc ;
		insertedDiscPane.getChildren().add(disc);
		
		int currentRow = row;
		disc.setTranslateX(col*(CIRCLE_DIAMETER+8) + 28);
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
		translateTransition.setByY(row*(CIRCLE_DIAMETER+8) + 28);
		
		translateTransition.setOnFinished(event-> {
			        isAllowedToInsert=true;
					if (gameEnded(currentRow, col)) {
						gameOver();
						return;
					}
					
					
					isPlayerOne = !isPlayerOne;
					
					playerName.setText(isPlayerOne ?( PLAYER_ONE ):( PLAYER_TWO));
				});
		
		
		translateTransition.play();
	}
	
	private void gameOver() {
		String winner = (isPlayerOne? PLAYER_ONE: PLAYER_TWO);
		System.out.println("Winner is "+ winner);
		
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("Connect Four");
		alert.setHeaderText(winner + "the WINNER");
		alert.setContentText("Want to play again ? ");
		ButtonType yesBtn = new ButtonType("Yes");
		ButtonType noBtn = new ButtonType("No, Exit");
		alert.getButtonTypes().setAll(yesBtn,noBtn);
		
		Platform.runLater(() -> {
			Optional<ButtonType> btnClicked = alert.showAndWait();
			if (btnClicked.isPresent() && btnClicked.get() == yesBtn) {
				resetGame();
				
			}else {
				Platform.exit();
				System.exit(0);
			}
		});
		
		
		
	}
	
	public void resetGame() {
		insertedDiscPane.getChildren().clear();
		
		for(int row=0; row<insertedDiscArray.length;row++){
			for (int col=0 ; col<insertedDiscArray[row].length;col++){
				insertedDiscArray[row][col]= null;
			}
		}
		isPlayerOne=true;
		playerName.setText(PLAYER_ONE);
		createPlayground();
	}
	
	private boolean gameEnded(int row, int col) {
		
		//Vertical Points
		int chain=0;
		
		List<Point2D> verticalPoints = IntStream.rangeClosed(row-3,row+3).mapToObj(r -> new Point2D(r,col)).collect(Collectors.toList());
		
		//horizontal points
		List<Point2D> horizontalPoints = IntStream.rangeClosed(col-3,col+3).mapToObj(c -> new Point2D(row,c)).collect(Collectors.toList());
		
		Point2D startPoint1 = new Point2D(row-3,col+3);
		List<Point2D> diagonal1Points = IntStream.rangeClosed(0,6).mapToObj(i -> startPoint1.add(i,-i)).collect(Collectors.toList());
		
		Point2D startPoint2 = new Point2D(row-3,col-3);
		List<Point2D> diagonal2Points = IntStream.rangeClosed(0,6).mapToObj(i -> startPoint2.add(i,i)).collect(Collectors.toList());
		
		 boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
							|| checkCombinations(diagonal1Points) || checkCombinations(diagonal2Points);
		
		return isEnded;
	}
	
	 private boolean checkCombinations(List<Point2D> points) {
		
		int chain=0;
		for (Point2D point:points) {
			
			int rowIndexForArray = (int) point.getX();
			int columnIndexOfArray = (int) point.getY();
			
			Disc disc = getDiscIfPresent(rowIndexForArray,columnIndexOfArray);
			
			if(disc != null && disc.isPlayerOneMove == isPlayerOne) {
				chain++;
				if (chain==4)
					return true;
			}else
				chain=0;
			
		}
		return false;
	}
	
	private Disc getDiscIfPresent(int row, int col){
		
		if (row>=ROWS || row<0 || col >=COLUMNS || col<0)
			return null;
		
		return insertedDiscArray[row][col];
	}
	
	private static class Disc extends Circle {
		
		private final boolean isPlayerOneMove ;
		public Disc(boolean isPlayerOneMove){
			
			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(CIRCLE_DIAMETER/2);
			setCenterX(CIRCLE_DIAMETER/2);
			setCenterY(CIRCLE_DIAMETER/2);
			setFill(isPlayerOneMove? Color.valueOf(discColor1) : Color.valueOf(discColor2));
			
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
	
	}
}
