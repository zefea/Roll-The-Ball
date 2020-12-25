/*
 * 
 * Term Project
 * 
 * Zeynep Ferah Akkurt - 150119824
 * Merve Rana Kýzýl - 150119825
 * 
 * GameBoard.java
 * This class is for creating the game board, mouse events, animations. 
 * 
 */

package application;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.animation.KeyFrame;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class GameBoard {

	private final int N = 5; // number of levels
	private String fileName; // file name, ex: "level4"
	private BorderPane game = new BorderPane(); // border pane object
	private GridPane pane = new GridPane(); // grid pane object
	private ArrayList<Tile> tiles = new ArrayList<>(); // list for tiles of the game
	private Label move = new Label(); // label object for number of moves
	private Circle gameCircle = new Circle(); // circle object for ball
	private MediaPlayer mediaPlayer; // mediaPlayer object for the sound of winning
	private Tile source; // the source tile
	private int moveNumber = 0; // number of moves that the player made
	private double ycoordinate; // tile's y coordinate on the pane
	private double xcoordinate; // tile's x coordinate on the pane

	// default constructor
	public GameBoard() {

		fileName = "level1";
		setBoard(fileName, tiles);
		checkCondition();
	}

	// setter getters
	public BorderPane getGame() {
		return game;
	}

	public void setGame(BorderPane game) {
		this.game = game;
	}

	public GridPane getPane() {
		return pane;
	}

	public void setPane(GridPane pane) {
		this.pane = pane;
	}

	public Label getMove() {
		return move;
	}

	public void setMove(Label move) {
		this.move = move;
	}

	public ArrayList<Tile> getTiles() {
		return tiles;
	}

	public void setTiles(ArrayList<Tile> tiles) {
		this.tiles = tiles;
	}

	// starts the game
	private void checkCondition() {

		ImageView image = new ImageView();
		for (Tile tile : tiles) {
			String type = tile.getType();
			String property = tile.getProperty();

			// Choose the tiles that are not static
			if (!type.equalsIgnoreCase("Starter") && !type.equalsIgnoreCase("End")
					&& !type.equalsIgnoreCase("PipeStatic") && !property.equalsIgnoreCase("Free")) {

				image = tile.getImage();
				// Pass the image of the tiles
				setOnDragDetected(image);
			}
		}
	}

	// Detect which image is dragged
	// and set the source tile according to that image
	public void setOnDragDetected(ImageView image) {

		image.setOnDragDetected(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
				for (Tile tile : tiles) {
					if (tile.getImage().equals(image)) {
						source = tile; // Set source tile
					}
				}

				setOnDragOver();

				// Allow any transfer mode
				Dragboard db = image.startDragAndDrop(TransferMode.ANY);
				// Put the image on a dragboard
				ClipboardContent content = new ClipboardContent();
				content.putImage(image.getImage());
				db.setContent(content);
				event.consume();

			}
		});

	}

	// Check whether the source tile is dragged over the targets
	public void setOnDragOver() {

		ArrayList<Tile> correctTargets = new ArrayList<Tile>();
		ArrayList<Tile> targetTiles = new ArrayList<Tile>(); // Keeps target tiles
		checkTargets(targetTiles); // Sets target tiles

		for (Tile tile : targetTiles) {

			tile.getImage().setOnDragOver(new EventHandler<DragEvent>() {

				public void handle(DragEvent event) {

					Tile targetReal;
					targetReal = tile;
					correctTargets.add(targetReal);

					// Accept it only if it is not dragged from the same node
					// and it has an Image
					if (event.getGestureSource() != tile.getImage() && event.getDragboard().hasImage()) {
						// Allow for both copying and moving
						// Chooses the default mode that is supported by gesture source
						// and accepted by gesture target
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
					}

					for (int i = 0; i < correctTargets.size(); i++) {
						if (targetTiles.contains(correctTargets.get(i))) {
							targetReal = correctTargets.get(i);
							setOnDragDropped(targetTiles, targetReal);
						}
					}

					event.consume();
				}
			});

		}

	}

	// Set the target tile's image as source tile's image
	// when the source tile is dropped on a target tile
	public void setOnDragDropped(ArrayList<Tile> targetTiles, Tile targetReal) {

		for (Tile tile : targetTiles) {

			if (tile.getProperty().equalsIgnoreCase("Free")) {

				tile.getImage().setOnDragDropped(new EventHandler<DragEvent>() {
					public void handle(DragEvent event) {

						// If there is an Image on dragboard, use that Image
						Dragboard db = event.getDragboard();

						boolean success = false;

						if (db.hasImage() && tile == targetReal) {

							// Set the the target image as the image on dragboard
							// which is the image of the source tile
							tile.getImage().setImage(db.getImage());
							success = true;

						}
						setOnDragDone(targetTiles, targetReal);
						// Let the source know whether the Image was successfully transferred and used
						event.setDropCompleted(success);
						event.consume();
					}
				});
			}

		}

	}

	// If the source tile is successfully moved, 
	//swap the source tile and the target tile
	public void setOnDragDone(ArrayList<Tile> targetTiles, Tile targetReal) {

		source.getImage().setOnDragDone(new EventHandler<DragEvent>() {
			public void handle(DragEvent event) {

				if (event.getTransferMode() == TransferMode.MOVE) {

					for (Tile tile : targetTiles) {

						if (tile.getProperty().equalsIgnoreCase("Free") && tile == targetReal) {

							// Swaps target tile and source tile

							// Create a Tile object for target tile using source's type and property
							Tile targetTile = new Tile(tile.getTileId(), source.getType(), source.getProperty());
							targetTile.determineImage();
							ImageView targetImg = targetTile.getImage();
							// Remove the target tile from tiles ArrayList
							tiles.remove(tile.getTileId() - 1);
							// Add the targetTile(source) to tiles ArrayList
							tiles.add(tile.getTileId() - 1, targetTile);
							// Add the image to the GridPane
							pane.add(targetImg, columnNumber(tile.getTileId()), rowNumber(tile.getTileId()));

							ImageView freeImg = new ImageView("free.png");
							// Create a Tile object for free tile
							Tile freeTile = new Tile(source.getTileId(), "Empty", "Free");
							// Remove the source tile from tiles ArrayList
							tiles.remove(source.getTileId() - 1);
							// Add the freeTile to tiles ArrayList
							tiles.add(source.getTileId() - 1, freeTile);
							// Set the image of freeTile
							freeTile.setImage(freeImg);
							// Add the image to the GridPane
							pane.add(freeImg, columnNumber(source.getTileId()), rowNumber(source.getTileId()));

							// Increment number of moves
							moveNumber++;
							move.setText(String.format("Number of moves: %d", moveNumber));
							move.setFont(Font.font("Times New Roman", FontWeight.BOLD, 25));

							Road road = new Road(tiles);

							// Check whether there is an appropriate path
							if (road.levelRoad()) {
								System.out.println(fileName);
								whichLevel(fileName);
								ArrayList<LineTo> lines = new ArrayList<LineTo>();

								double x = pane.getWidth() / 8;
								double y = pane.getHeight() / 8;

								double xcoordinate = 0;
								double ycoordinate = 0;

								// Create a path using the tiles in roadTiles ArrayList
								for (Tile tile1 : road.getRoadTiles()) {

									if (!tile1.getType().equalsIgnoreCase("Empty")) {

										int id = tile1.getTileId();

										xcoordinate = getXCoordinates(id, x);
										ycoordinate = getYCoordinates(id, y);

										// Create a lineTo using a and y coordinates
										LineTo line = new LineTo(xcoordinate, ycoordinate);
										// Keep the lines in lines ArrayList
										lines.add(line);

									}

								}
								// Get the starter tile's id and set a starting point
								double startx = getXCoordinates(road.getStart().getTileId(), x);
								double endx = getYCoordinates(road.getStart().getTileId(), y);

								Path path = new Path();
								MoveTo moveTo = new MoveTo(startx, endx);

								path.getElements().add(moveTo);

								for (LineTo line1 : lines) {

									// Add each element of lines ArrayList to path
									path.getElements().add(line1);
								}

								// Create a circle (ball) for the animation
								Circle circle = new Circle(startx, endx, 12, Color.ORANGERED);
								PathTransition pt = new PathTransition();
								// Set the duration of the animation
								pt.setDuration(Duration.millis(2000));
								pt.setNode(circle);
								// Set the path which is created using lineTo
								pt.setPath(path);
								pt.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
								pane.getChildren().remove(gameCircle);
								circle.setManaged(false);
								// Add the circle(ball) to pane
								pane.getChildren().add(circle);
								pt.play();

								// Create a timeline for the end of the level
								Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000)));

								timeline.play();
								timeline.setOnFinished(e -> {
									System.out.println("Number of moves: " + moveNumber);
									System.out.println("You made it!! Time for " + fileName + " now!");
									game.setTop(new Label(fileName));
									// Reset number of moves
									moveNumber = 0;
									pane.getChildren().clear();
									game.getChildren().remove(pane);
									tiles.clear();
									setBoard(fileName, tiles);

								});
							}

							checkCondition();
						}

					}

				}

				targetTiles.clear();
				event.consume();
			}
		});
	}

	// finds the targets of the source
	private void checkTargets(ArrayList<Tile> targets) {

		targets.clear();
		int sourceId = source.getTileId();
		ImageView image = source.getImage();
		//setting the targets with if there is a one distance(right-left-up-down) free tile.
		for (int i = 0; i < tiles.size(); ++i) {
			if (tiles.get(i).getImage().equals(image)) {
				if (columnNumber(sourceId) < 3)
					if (tiles.get(i + 1).getProperty().equalsIgnoreCase("Free")) {//checking that right side of the source is free or not 
						targets.add(tiles.get(i + 1));
					}
				if (columnNumber(sourceId) > 0)
					if (tiles.get(i - 1).getProperty().equalsIgnoreCase("Free")) {//checking that left side of the source is free or not
						targets.add(tiles.get(i - 1));
					}
				if (rowNumber(sourceId) < 3)
					if (tiles.get(i + 4).getProperty().equalsIgnoreCase("Free")) {//checking that down of the source is free or not
						targets.add(tiles.get(i + 4));
					}
				if (rowNumber(sourceId) > 0)
					if (tiles.get(i - 4).getProperty().equalsIgnoreCase("Free")) {//checking that up of the source is free or not 
						targets.add(tiles.get(i - 4));
					}
			}
		}
	}

	// return column number of given id number of a tile
	private int columnNumber(int sourceId) {
		if (sourceId % 4 == 0)
			return 3;
		else
			return sourceId % 4 - 1;
	}

	// return row number of given id number of a tile
	private int rowNumber(int sourceId) {

		int row;
		if (sourceId >= 1 && sourceId <= 4) {
			row = 0;
		} else if (sourceId >= 5 && sourceId <= 8) {
			row = 1;
		} else if (sourceId >= 9 && sourceId <= 12) {
			row = 2;
		} else
			row = 3;

		return row;
	}

	// sets the game board, tiles list
	public void setBoard(String fileName, ArrayList<Tile> temp) {
		game.setTop(new Label(fileName));		//setting top of the board pane with a label of a file name(level name).
		this.fileName = fileName;

		if (!fileName.equalsIgnoreCase("THE END")) {
			try {
				temp = readLevel(fileName + ".txt"); // reading tiles
				int id = 0;
				// creating the grid pane with tile's image
				for (int row = 0; row < 4; ++row) {
					for (int column = id % 4; column < temp.size() / 4; ++column) {
						try {
							pane.add((temp.get(id)).getImage(), (id % 4), row);
						} catch (Exception h) {
							System.out.println("could not add the pane: " + h);
						}
						id++;
					}
				}

			} catch (Exception e) {
				System.out.println("error: " + e);
			}
			// seting tiles
			tiles = temp;
			// to find start point
			Tile start = new Tile();
			for (int i = 0; i < tiles.size(); i++) {
				if (tiles.get(i).getType().equalsIgnoreCase("Starter")) {
					start = tiles.get(i);
				}
			}

			setCircleGame(start); // setting a circle with its method
			//setting the label object move with some properties.
			move.setText(String.format("Number of moves: %d", moveNumber));
			move.setFont(Font.font("Times New Roman", FontWeight.BOLD, 25));
			//setting the location of nodes in the border pane
			game.setTop(new Label(fileName));
			game.setBottom(getMove());
			game.setCenter(pane);
			//starts the game
			checkCondition();

		} else { // if there is no levels left
			// game.getChildren().clear();
			// game.setCenter(new Label("YOU WON"));
			game.getChildren().clear();//clear all nodes in border pane. 
			ImageView youwin = new ImageView("youwin.gif");
			game.setCenter(youwin);//adding a .gif to border pane
			//adding a mediaPlayer to end of the game
			String source = new File("winner.mp3").toURI().toString();
			Media media = null;
			media = new Media(source);
			mediaPlayer = new MediaPlayer(media);
			mediaPlayer.play();
		}
	}

	// creates a circle (ball) for start point
	private void setCircleGame(Tile start) {

		pane.layout();
		double x = pane.getBoundsInLocal().getWidth() / 8; // x width of a tile
		double y = pane.getBoundsInLocal().getHeight() / 8; // y height of a tile
		double startx = getXCoordinates(start.getTileId(), x); // start x coordinate
		double endx = getYCoordinates(start.getTileId(), y); // start y coordinates
		gameCircle = new Circle(startx, endx, 12, Color.ORANGERED);//setting the circle object
		gameCircle.setManaged(false);
		pane.getChildren().add(gameCircle);
	}

	// reads from a file and returns as an arrayList
	public ArrayList<Tile> readLevel(String fileName) throws Exception {
		java.io.File file = new java.io.File(fileName);
		Scanner input = new Scanner(file);
		ArrayList<Tile> tiles = new ArrayList<>(); // keeps tiles
		try {
			while (input.hasNext()) {
				String line = input.nextLine();
				if (!line.isEmpty()) {
					String[] in = line.split(",");
					int row = Integer.parseInt(in[0]);
					String type = in[1];
					String property = in[2];
					Tile image = new Tile(row, type, property); // creating a Tile object based on the information from
					// a file
					tiles.add(image);
				}
			}
		} catch (Exception e) {
			System.out.println("File couldn't read: " + e);
		}

		input.close();
		return tiles;
	}

	/* sets the fileName for each level
	 * level name must be start with "level", then the number. Ex: level5
	 */
	private void whichLevel(String fileName) {
		//according to given N number of levels, setting the fileName
		for (int i = 1; i < N; ++i) {
			if (fileName.equalsIgnoreCase("level" + i)) {
				i = i + 1;
				this.fileName = "level" + i;
				break;
			} else //all levels are completed
				this.fileName = "THE END";
		}
	}

	// returns the x coordinate for given tile with its id
	private double getXCoordinates(int id, double x) {

		if (id % 4 == 1)
			xcoordinate = x;
		else if (id % 4 == 2)
			xcoordinate = 3 * x;
		else if (id % 4 == 3)
			xcoordinate = 5 * x;
		else if (id % 4 == 0)
			xcoordinate = 7 * x;

		return xcoordinate;
	}

	// returns the y coordinate for given tile with its id
	private double getYCoordinates(int id, double y) {

		if (id <= 4)
			ycoordinate = y;
		else if (id > 4 && id <= 8)
			ycoordinate = 3 * y;
		else if (id > 8 && id <= 12)
			ycoordinate = 5 * y;
		else if (id > 12 && id <= 16)
			ycoordinate = 7 * y;
		return ycoordinate;
	}

	// Information about the pane
	@Override
	public String toString() {
		String s = "";
		for (Tile tile : tiles)
			s += "Id: " + tile.getTileId() + "Type: " + tile.getType() + " Property: " + tile.getProperty() + "\n";

		return s;
	}

}
