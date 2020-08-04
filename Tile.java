/*
 * 
 * Term Project
 * 
 * Zeynep Ferah Akkurt - 150119824
 * Merve Rana Kýzýl - 150119825
 * 
 *	Tile.java 
 *  This class creates an object that has a image of tile for game board.
 * 
 */

package application;

import javafx.scene.image.ImageView;

public class Tile{
	private int id; // tile's id number
	private String type; // tile's type
	private String property;// tile's property
	private ImageView image;// the image of the tile

	
	//default constructor
	public Tile() {}
	// constructor 
	public Tile(Tile tile) {
		setTileId(tile.id);
		this.type = tile.type;
		this.property = tile.property;
		this.image = tile.image;
	}
	// constructor 
	public Tile(int id, String type, String property) {
		setTileId(id);
		this.type = type;
		this.property = property;
		try {
		determineImage();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	//getter-setters
	public int getTileId() {
		return id;
	}

	public void setTileId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getProperty() {
		return property;
	}

	public void setProperty(String property) {
		this.property = property;
	}

	public void setImage(ImageView image) {
		this.image = image;
	}

	public ImageView getImage() {
		return image;
	}
	
	//setting the image from a file of a tile
	public void determineImage() {

		if (type.equalsIgnoreCase("Starter")) { // blue starter
			if (property.equalsIgnoreCase("Vertical")) {
				image = new ImageView("blueVer.png");
			
			} else if (property.equalsIgnoreCase("Horizontal")) {
				image = new ImageView("blueHor.png");
				
			}
			
		} else if (type.equalsIgnoreCase("End")) { // red end
			if (property.equalsIgnoreCase("Vertical")) {
				image = new ImageView("redVer.png");
				
			} else if (property.equalsIgnoreCase("Horizontal")) {
				image = new ImageView("redHor.png");
				
			}
		} else if (type.equalsIgnoreCase("Empty")) {
			if (property.equalsIgnoreCase("none")) { // grey pile
				image = new ImageView("empty.png");
			} else if (property.equalsIgnoreCase("Free")) {
				image = new ImageView("free.png");
			
			}
		} else if (type.equalsIgnoreCase("PipeStatic")) { // fix blue pile
			if (property.equalsIgnoreCase("Vertical")) {
				image = new ImageView("blueStaticVer.png");
			
			} else if (property.equalsIgnoreCase("Horizontal")) {
				image = new ImageView("blueStaticHor.png");
			
			}else if (property.equalsIgnoreCase("00")) {
				image = new ImageView("static00.png");
			}else if (property.equalsIgnoreCase("01")) {
				image = new ImageView("static01.png");
			}else if (property.equalsIgnoreCase("10")) {
				image = new ImageView("static10.png");
			}else if (property.equalsIgnoreCase("11")) {
				image = new ImageView("static11.png");
			}		
			
		} else if (type.equalsIgnoreCase("Pipe")) {
			if (property.equalsIgnoreCase("Vertical")) {
				image = new ImageView("vertical.png");

			} else if (property.equalsIgnoreCase("Horizontal")) {
				image = new ImageView("horizontal.png");
			}else if (property.equalsIgnoreCase("00")) {
				image = new ImageView("00.png");
			}else if (property.equalsIgnoreCase("01")) {
				image = new ImageView("01.png");
			}else if (property.equalsIgnoreCase("10")) {
				image = new ImageView("10.png");
			}else if (property.equalsIgnoreCase("11")) {
				image = new ImageView("11.png");
			}
		}
	}
	
	//information about a tile
	@Override
	public String toString() {
		return "Id: " + id + " Property: " + property + "Type: " + type ;
	}
}

