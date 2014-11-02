/*
Contains all the data that tends to get passed around a lot, but doesn't deserve it's own class
*/
import javax.swing.*;
import java.awt.Image;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Cursor;
import java.awt.Color;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;


public final class staticData
{
  //width/height of the grid cells
  private static final int cellHeightPX = 16;
  private static final int cellWidthPX = 16;
  
  //starting number of rows/coumns
  private static final int defaultNumRows = 25;
  private static final int defaultNumColumns = 25;
  
  //the font to use when drawing the number increments on the side of the grid
  private static Font helveticaFont = new Font("Helvetica", Font.PLAIN, 12);
  
  //the font metrics used to determine how much to offset the numbers
  private static FontMetrics helveticaMetrics = null;
  
  //custom cursor to visually show when the paint bucket is active
  private static Cursor paintingCursor = Toolkit.getDefaultToolkit().createCustomCursor(new ImageIcon(buildPath(new String[]{"images", "cursor.png"})).getImage(), new Point(10, 6), "paint cursor");

  //variable to determine if the area should be painted or not
  private static Boolean isPainting = false;

  //number of rows/columns, rows = x and columns = y
  private static int numRows = 25;
  private static int numColumns = 25;
  
  //whether the structure will be built below y=0 ingame or not
  private static Boolean below = false;

  //most recently clicked row/column minus 1
  private static int lastRow = 0;
  private static int lastColumn = 0;
  
  //which JButton was clicked
  private static int picClicked = -1;
  
  //3D array of ints, representing the [layer][x][y]
  //did not use a 3D ArrayList due to ease of use and because ArrayLists require Integers which take up 4x the memory
  private static int picLocations[][][] = new int[2][numColumns][numRows];

  //array of Images to be placed in the grid
  private static BufferedImage drawImage[] = null;
  
  //array of icons to show on the buttons and under the mouse
  private static Image iconImage[] = null;

  //used to switch whether the foreground or background is the active layer
  private static int layerIndex = 0;

  //the id of the button that was clicked
  private static int id = staticData.emptyCell;
  
  //array of Strings for convenience when writing the dataString to file so I can use the layerIndex rather than an if/then/else statement every time
  private static String layers[] = 
  {
    "foreground",
    "background"
  };
  
  //current location of the mouse on the grid
  private static int currentX = -1;
  private static int currentY = -1;
  
  //the x/y coordinates of the last click/move in pixels
  private static int pixelsX = 0;
  private static int pixelsY = 0;

  //the highlight overlay
  private static BufferedImage highlight[] = null;

  //how many cells around the center to paint
  private static int brushWidth = 1;
  private static int brushHeight = 1;

  //string to hold the name of the structure that's currently being edited
  private static String currentStructure = "";
  
  //whether or not to highlight the cells that have tiles in the inactive layer
  private static Boolean highlightCells = true;
  
  //whether or not to allow cells to be overwritten
  private static Boolean cellOverwrite = true;
  
  //the background color
  private static Color backgroundColor = new Color(150, 75, 0);
  
  //whether control is being held or not
  private static Boolean isControl = false;
  
  //whether shift is being held or not
  private static Boolean isShift = false;
  
  //whether "c" is pressed or not
  private static Boolean isCopy = false;
  
  //whether "v" is pressed or not
  private static Boolean isPaste = false;
  
  //the starting/ending custom brush locations
  private static Point startShift = new Point(0, 0);
  private static Point endShift = new Point(0, 0);
  
  //how far complete it is
  private static double percent = 0;
  
  //whether to autosave or not
  private static Boolean autoSave = true;
  
  //stores the index where the object image ids end and the tile ids begin, so images with ids less than this (aka objects) can't be placed in the background
  private static int endOfObjects = 0;
  
  //if the grid lines should be draw
  private static Boolean gridEnabled = true;
  
  //the id used to represent and empty cell
  public static final int emptyCell = -1;
  
  //the value to use for cells that are blocked by a multi-cell object
  public static final int blockedByObject = -2;
  
  //the value passed to paintArea to bypass an issue with painting -1 (empty)
  public static final int paintEmpty = -100;
  
  
  private staticData()
  {
    
  }
  
  public static void init()
  {
    //make sure this isn't called twice
    if(highlight == null)
    {
      //initialize the highlight images
      try
      {
        highlight = new BufferedImage[]
        {
          ImageIO.read(Paths.get("images", "highlightTop.png").toFile()),
          ImageIO.read(Paths.get("images", "highlightRight.png").toFile()),
          ImageIO.read(Paths.get("images", "highlightBot.png").toFile()),
          ImageIO.read(Paths.get("images", "highlightLeft.png").toFile())
        };
      }
      catch(IOException IOe)
      {
        j.err(new Exception("Failed to initialize the highlight images", IOe));
      }
      
      //initialize grid values to empty
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < numColumns; i++)
        {
          for(int k = 0; k < numRows; k++)
          {
            picLocations[index][i][k] = staticData.emptyCell;
          }
        }
      }
    }
    else
    {
      j.err(new Exception("Called staticData.init() twice!"));
    }
  }
  
  //return the width or height of the cells
  public static int getCellHeightPX()
  {
    return cellHeightPX;
  }
  
  public static int getCellWidthPX()
  {
    return cellWidthPX;
  }
  
  //return the default number of rows or columns
  public static int getDefaultNumRows()
  {
    return defaultNumRows;
  }
  
  public static int getDefaultNumColumns()
  {
    return defaultNumColumns;
  }
  
  //return font data and initialize the font metrics
  public static Font getHelveticaFont()
  {
    return helveticaFont;
  }
  
  public static FontMetrics getHelveticaMetrics()
  {
    return helveticaMetrics;
  }
  
  public static void setHelveticaMetrics(JPanel gridPanel)
  {
    helveticaMetrics = gridPanel.getFontMetrics(helveticaFont);
  }
  
  //return the cursor to use when painting is enabled
  public static Cursor getPaintingCursor()
  {
    return paintingCursor;
  }
  
  //get and set whether painting is enabled
  public static Boolean getIsPainting()
  {
    return isPainting;
  }
  
  public static void setIsPainting(Boolean isPainting)
  {
    staticData.isPainting = isPainting;
  }
  
  //get and set the number of rows and columns
  public static int getNumRows()
  {
    return numRows;
  }
  
  public static void setNumRows(int numRows)
  {
    if(numRows > 0)
    {
      staticData.numRows = numRows;
    }
    else
    {
      j.err(new Exception("numRows was not greater than 0"));
    }
  }
  
  public static int getNumColumns()
  {
    return numColumns;
  }
  
  public static void setNumColumns(int numColumns)
  {
    if(numColumns > 0)
    {
      staticData.numColumns = numColumns;
    }
    else
    {
      j.err(new Exception("numColumns was not greater than 0"));
    }
  }
  
  //get, set, and reverse the below variable
  public static Boolean getBelow()
  {
    return below;
  }
  
  public static void setBelow(Boolean below)
  {
    staticData.below = below;
  }
  
  public static void buildBelow()
  {
    if(below)
    {
      below = false;
    }
    else
    {
      below = true;
    }
  }
  
  //get and set the last clicked row and column
  public static int getLastRow()
  {
    return lastRow;
  }
  
  public static void setLastRow(int lastRow)
  {
    if(lastRow >= 0)
    {
      staticData.lastRow = lastRow;
    }
  }
  
  public static int getLastColumn()
  {
    return lastColumn;
  }
  
  public static void setLastColumn(int lastColumn)
  {
    if(lastColumn >= 0)
    {
      staticData.lastColumn = lastColumn;
    }
  }
  
  //get and set picClicked
  public static int getPicClicked()
  {
    return picClicked;
  }
  
  public static void setPicClicked(int picClicked)
  {
    staticData.picClicked = picClicked;
  }
  
  //get and set picLocations as a whole and at a specific cell
  public static int[][][] getPicLocations()
  {
    return picLocations;
  }
  
  public static int getPicLocations(int index, int x, int y)
  {
    return picLocations[index][x][y];
  }
  
  public static int getCurrentLocation()
  {
    return picLocations[layerIndex][currentX][currentY];
  }
  
  public static void setPicLocations(int[][][] picLocations)
  {
    staticData.picLocations = picLocations;
  }
  
  //extra logic needed to prevent tiles/objects from overlapping placed objects
  //undo/redo also calls this method, so no further updates are needed for that to work
  public static void setPicLocations(int index, int x, int y, int cellValue)
  {
    //if the current location isn't occupied by an object, over-write the new cell
    if(picLocations[index][x][y] != blockedByObject)
    {
      //if they aren't erasing, set the value
      //otherwise, check if an object's occupied area needs to be cleared
      if(cellValue != staticData.emptyCell)
      {
        //the height and width of the image at the location
        double width = drawImage[cellValue].getWidth();
        double height = drawImage[cellValue].getHeight();
        
        //convert to the number of cells that covers
        width = Math.ceil(width/cellWidthPX);
        height = Math.ceil(height/cellHeightPX);
        
        //check if this would overwrite anything else
        for(int i = 0; i < width; i++)
        {
          for(int k = 0; k < height; k++)
          {
            //if the cell is on the grid
            if(x-i >= 0 && y-k >= 0)
            {
              //if its not empty, don't place it
              if(picLocations[index][x-i][y-k] != emptyCell)
              {
                return;
              }
            }
          }
        }
        
        //mark the cells as blocked by the object
        for(int i = 0; i < width; i++)
        {
          for(int k = 0; k < height; k++)
          {
            //if the cell is on the grid
            if(x-i >= 0 && y-k >= 0)
            {
              //set as blocked by an object
              picLocations[index][x-i][y-k] = staticData.blockedByObject;
            }
          }
        }
        
        //set the originally clicked cell to the object
        picLocations[index][x][y] = cellValue;
      }
      else
      {
        //if they aren't trying to erase an empty cell
        if(picLocations[index][x][y] != emptyCell)
        {
          //the height and width of the image at the location
          double width = drawImage[picLocations[index][x][y]].getWidth();
          double height = drawImage[picLocations[index][x][y]].getHeight();
          
          //convert to the number of cells that covers
          width = Math.ceil(width/cellWidthPX);
          height = Math.ceil(height/cellHeightPX);
          
          //unblock the cells that used to be blocked by an object
          for(int i = 0; i < width; i++)
          {
            for(int k = 0; k < height; k++)
            {
              //if the cell is on the grid
              if(x-i >= 0 && y-k >= 0)
              {
                //set it to empty
                picLocations[index][x-i][y-k] = emptyCell;
              }
            }
          }
          
          //set the originally clicked cell to the object
          picLocations[index][x][y] = cellValue;
        }
      }
    }
  }
  
  //set and get drawImage
  public static BufferedImage getDrawImage(int index)
  {
    try
    {
      return drawImage[index];
    }
    catch(ArrayIndexOutOfBoundsException AIOOBe)
    {
      j.err(new Exception("Invalid image index passed", AIOOBe));
      return null;
    }
  }
  
  public static void initDrawImage(int tileIconsSize)
  {
    drawImage = new BufferedImage[tileIconsSize];
  }
  
  public static void setDrawImage(int index, String indexImagePath)
  {
    try
    {
      //if the image is a tile, leave it as it is
      if(!indexImagePath.contains("FullObjects"))
      {
        drawImage[index] = ImageIO.read(Paths.get(indexImagePath).toFile());
      }
      //if it's an object, scale it to 2x it's size on disk
      else
      {
        //the original image
        BufferedImage unscaledImage = ImageIO.read(Paths.get(indexImagePath).toFile());
        
        //store the resized image
        drawImage[index] = new BufferedImage(unscaledImage.getWidth()*2, unscaledImage.getHeight()*2, BufferedImage.TRANSLUCENT);
        
        //graphics2d object to do the resizing
        Graphics2D imageGraphics = drawImage[index].createGraphics();
        
        //the rendering hints for how to handle the image
        imageGraphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
        
        //resize the image
        imageGraphics.drawImage(unscaledImage, 0, 0, unscaledImage.getWidth()*2, unscaledImage.getHeight()*2, null);
        imageGraphics.dispose();
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to initialize image " + indexImagePath, IOe));
    }
  }
  
  //get and set the images used for the mouse/button icons
  public static Image getIconImage(int index)
  {
    try
    {
      return iconImage[index];
    }
    catch(ArrayIndexOutOfBoundsException AIOOBe)
    {
      j.err(new Exception("Invalid icon index passed " + Integer.toString(index), AIOOBe));
      return null;
    }
  }
  
  public static void initIconImage(int tileIconsSize)
  {
    iconImage = new Image[tileIconsSize];
  }
  
  public static void setIconImage(int index, String indexImagePath)
  {
    if(Files.exists(Paths.get(indexImagePath)))
    {
      iconImage[index] = new ImageIcon(indexImagePath).getImage();
    }
    else
    {
      j.err(new Exception("Failed to initialize " + indexImagePath));
    }
  }
  
  //gets and sets the layerIndex
  public static int getLayerIndex()
  {
    return layerIndex;
  }
  
  public static void setLayerIndex(int layerIndex)
  {
    if(layerIndex == 0 || layerIndex == 1)
    {
      staticData.layerIndex = layerIndex;
    }
    else
    {
      j.err(new Exception("layerIndex not set to 0 or 1"));
    }
  }
  
  //get and set id
  public static int getId()
  {
    return id;
  }
  
  public static void setId(int id)
  {
    staticData.id = id;
  }
  
  //get the current layer in String form for the lua script file
  public static String getLayers(int layer)
  {
    return layers[layer];
  }
  
  //get and set the currentX/Y on the grid
  public static int getCurrentX()
  {
    return currentX;
  }
  
  public static void setCurrentX(int currentX)
  {
    staticData.currentX = currentX;
  }
  
  public static int getCurrentY()
  {
    return currentY;
  }
  
  public static void setCurrentY(int currentY)
  {
    staticData.currentY = currentY;
  }
  
  //get/set the location of the mouse in pixels
  public static int getPixelsX()
  {
    return pixelsX;
  }
  
  public static void setPixelsX(int x)
  {
    pixelsX = x;
  }
  
  public static int getPixelsY()
  {
    return pixelsY;
  }
  
  public static void setPixelsY(int y)
  {
    pixelsY = y;
  }
  
  //get the highlight image
  public static BufferedImage getHighlight(int index)
  {
    return highlight[index];
  }
  
  //get, set, increment, and decrement the brushWidth
  public static int getBrushWidth()
  {
    return brushWidth;
  }
  
  public static void setBrushWidth(int brushWidth)
  {
    if(brushWidth > 0)
    {
      staticData.brushWidth = brushWidth;
    }
    else
    {
      j.err(new Exception("brushWidth less than 1"));
    }
  }
  
  //+1 to brush size
  public static void incrementBrushWidth()
  {
    if
    (
      !isControl && 
      !isShift && 
      !isPainting
    )
    {
      brushWidth++;
    }
  }
  
  //get, set, increment, and decrement the brushHeight
  public static int getBrushHeight()
  {
    return brushHeight;
  }
  
  public static void setBrushHeight(int brushHeight)
  {
    if(brushHeight > 0)
    {
      staticData.brushHeight = brushHeight;
    }
    else
    {
      j.err(new Exception("brushHeight less than 1"));
    }
  }
  
  //+1 to brush size
  public static void incrementBrushHeight()
  {
    if
    (
      !isControl && 
      !isShift && 
      !isPainting
    )
    {
      brushHeight++;
    }
  }
  
  //-1 to brush size
  public static void decrementBrushWidth()
  {
    if(brushWidth > 1)
    {
      brushWidth--;
    }
  }
  
  public static void decrementBrushHeight()
  {
    if(brushHeight > 1)
    {
      brushHeight--;
    }
  }
  
  //get and set the current structure
  public static String getCurrentStructure()
  {
    return currentStructure;
  }
  
  public static void setCurrentStructure(String currentStructure)
  {
    staticData.currentStructure = currentStructure;
  }
  
  //get and set the cell highlighting
  public static Boolean getHighlightCells()
  {
    return highlightCells;
  }
  
  public static void setHighlightCells(Boolean highlightCells)
  {
    staticData.highlightCells = highlightCells;
  }
  
  //get and set cell overwriting
  public static Boolean getCellOverwrite()
  {
    return cellOverwrite;
  }
  
  public static void setCellOverwrite(Boolean cellOverwrite)
  {
    staticData.cellOverwrite = cellOverwrite;
  }
  
  //set the background color
  public static void setBackgroundColor(String enteredColor)
  {
    //remove the spaces
    enteredColor = enteredColor.replaceAll("/s+", "");
    
    //split it based on non-digit characters
    String[] newColor = enteredColor.split("/D+");
    
    //check to make sure that gave 3 values
    if(newColor.length == 3)
    {
      //turn them into ints
      int[] newColorValues = 
      { 
        Integer.parseInt(newColor[0]), 
        Integer.parseInt(newColor[1]), 
        Integer.parseInt(newColor[2])
      };
    
      //try to set the color
      try
      {
        backgroundColor = new Color(newColorValues[0], newColorValues[1], newColorValues[2]);
      }
      catch(IllegalArgumentException IAe)
      {
        j.err(new Exception("Invalid color value", IAe));
        JOptionPane.showMessageDialog(null, "All color values must be between 0 and 255.");
      }
    }
  }
  
  public static Color getBackgroundColor()
  {
    return backgroundColor;
  }
  
  //gets the current state of the grid, so when saving you can't affect the grid mid-save
  public static int[][][] getGrid()
  {
    int [][][] savedGrid = new int[2][numColumns][numRows];
    
    for(int index = 0; index != 2; index++)
    {
      for(int x = 0; x < numColumns; x++)
      {
        for(int y = 0; y < numRows; y++)
        {
          savedGrid[index][x][y] = picLocations[index][x][y];
        }
      }
    }
    
    return savedGrid;
  }
  
  //get/set the control key boolean
  public static Boolean getIsControl()
  {
    return isControl;
  }
  
  public static void setIsControl(Boolean isControl)
  {
    staticData.isControl = isControl;
  }
  
  //get/set the shift key boolean
  public static Boolean getIsShift()
  {
    return isShift;
  }
  
  public static void setIsShift(Boolean isShift)
  {
    staticData.isShift = isShift;
  }
  
  //set the cells for the shift-dragging
  public static void setStartShift(int x, int y)
  {
    startShift = new Point(x, y);
  }
  
  public static Point getStartShift()
  {
    return startShift;
  }
  
  public static void setEndShift(int x, int y)
  {
    endShift = new Point(x, y);
  }
  
  public static Point getEndShift()
  {
    return endShift;
  }
  
  //swaps foreground and background
  public static void swapLayer()
  {
    int[][] swapLayer = picLocations[0];
    picLocations[0] = picLocations[1];
    picLocations[1] = swapLayer;
  }
  
  //toggles autosaving/gets the flag
  public static void toggleAutoSave()
  {
    if(autoSave)
    {
      autoSave = false;
    }
    else
    {
      autoSave = true;
    }
  }
  
  public static Boolean getAutoSave()
  {
    return autoSave;
  }
  
  //gets/sets the index in listOfSizes where the ids start representing tiles rather than objects
  public static int getEndOfObjects()
  {
    return endOfObjects;
  }
  
  public static void setEndOfObjects(int endOfObjects)
  {
    staticData.endOfObjects = endOfObjects;
  }
  
  //get/set if the grid lines are drawn
  public static Boolean getGridEnabled()
  {
    return gridEnabled;
  }
  
  public static void toggleGrid()
  {
    if(gridEnabled)
    {
      gridEnabled = false;
    }
    else
    {
      gridEnabled = true;
    }
  }
  
  //updating the config file
  public static void updateConfig()
  {
    //try to change the color
    try(PrintWriter saveColor = new PrintWriter(buildPath(new String[]{"configuration", "config.txt"}));)
    {
      //save the Color as a string
      saveColor.println("color: " + Integer.toString(backgroundColor.getRed()) + ", " + Integer.toString(backgroundColor.getGreen()) + ", " + Integer.toString(backgroundColor.getBlue()));

      //save the booleans
      saveColor.println("overwrite: " + cellOverwrite.toString());
      saveColor.println("highlight: " + highlightCells.toString());
    }
    catch(FileNotFoundException FNFe)
    {
      j.err(new Exception("Failed to save the new color", FNFe));
      return;
    }
  }
  
  //check if the entire grid is empty or not
  public static Boolean isGridEmpty(int[][][] checkGrid)
  {
    for(int index = 0; index != 2; index++)
    {
      for(int x = 0; x < checkGrid[index].length; x++)
      {
        for(int y = 0; y < checkGrid[index][x].length; y++)
        {
          //found something not empty
          if(checkGrid[index][x][y] != emptyCell)
          {
            return false;
          }
        }
      }
    }
    
    //the entire thing was empty
    return true;
  }
  
  //check if the grid contains a certain tile/object
  public static Boolean searchGrid(int search)
  {
    for(int index = 0; index != 2; index++)
    {
      for(int x = 0; x < picLocations[index].length; x++)
      {
        for(int y = 0; y < picLocations[index][x].length; y++)
        {
          //found something not empty
          if(picLocations[index][x][y] == search)
          {
            return true;
          }
        }
      }
    }
    
    //the entire thing was empty
    return false;
  }
  
  //mirror stuff
  public static void mirrorUp(gridCanvas gridPanel)
  {
    mirror(0, gridPanel);
  }
  
  public static void mirrorDown(gridCanvas gridPanel)
  {
    mirror(2, gridPanel);
  }

  public static void mirrorLeft(gridCanvas gridPanel)
  {
    mirror(3, gridPanel);
  }
  
  public static void mirrorRight(gridCanvas gridPanel)
  {
    mirror(1, gridPanel);
  }
  
  //core code behind mirroring
  private static void mirror(int direction, gridCanvas gridPanel)
  {
    //add to undoStack
    ArrayList<Integer> clickInfo = new ArrayList<Integer>();
    clickInfo.add(1);
    
    //if the current mouse locations is on the grid
    if
    (
      currentX >= 0 && 
      currentX < numColumns && 
      currentY >= 0 && 
      currentY < numRows
    )
    {
      //adjust the location of the brush
      //without staticData it would expand to the upper-right from the mouse rather than keeping the mouse centered
      int centerX = currentX - ((brushWidth-1)/2);
      int centerY = currentY + ((brushHeight-1)/2);
      
      //draw the area of the brush
      for(int x = 0; x < brushWidth; x++)
      {
        for(int y = 0; y < brushHeight; y++)
        {
          int xOffset = x;
          int yOffset = y;
          
          if(direction == 0)
          {
            yOffset = brushHeight+(brushHeight-y-1);
          }
          else if(direction == 1)
          {
            xOffset = brushWidth+(brushWidth-x-1);
          }
          else if(direction == 2)
          {
            yOffset = -(y+1);
          }
          else if(direction == 3)
          {
            xOffset = -(x+1);
          }
          
          //make sure the squares are drawn on the grid
          if
          (
            centerX+x < numColumns && 
            centerX+x >= 0 && 
            centerX+xOffset < numColumns && 
            centerX+xOffset >= 0 && 
            centerY-yOffset >= 0 && 
            centerY-yOffset < numRows && 
            centerY-y >= 0 && 
            centerY-y < numRows
          )
          {
            //if overwriting is allowed, place the tile
            //if it isn't allowed but the cell is empty, place the tile
            if
            (
              cellOverwrite
              || 
              (
                !cellOverwrite && 
                picLocations[layerIndex][centerX+xOffset][centerY-yOffset] == staticData.emptyCell
              )
            )
            {
              clickInfo.add(layerIndex);
              clickInfo.add(centerX+xOffset);
              clickInfo.add(centerY-yOffset);
              clickInfo.add(picLocations[layerIndex][centerX+x][centerY-y]);
              clickInfo.add(picLocations[layerIndex][centerX+xOffset][centerY-yOffset]);
              picLocations[layerIndex][centerX+xOffset][centerY-yOffset] = picLocations[layerIndex][centerX+x][centerY-y];
            }
          }
        }
      }
      
      //add everything to the undo stack if there's anything to add
      if(clickInfo.size() != 1)
      {
        gridPanel.getUrdo().addUndo(clickInfo);
      }
    }
  }
  
  //pops up a list of structures to do something with
  //hasItemTxt is used as a flag to check if item.lua is needed and as the offset if it is
  //operation is the text to display for what is being done
  public static String showAvailableStructures(int hasItemTxt, String operation, String popupText)
  {
    //ArrayList to hold the potential structures to be deleted
    ArrayList<String> listOfStructures = new ArrayList<String>();
    
    //if item.lua is valid, add it
    if(hasItemTxt == 1)
    {
      listOfStructures.add("item.lua");
    }
    
    //holds the max size of the scroll pane
    int maxPaneSize = 0;
    
    //where to look for the structures
    String location = buildPath(new String[]{"..", "scripts"});
    
    //change the location if it's not a normal load/delete
    if(operation.equals("Import"))
    {
      location = buildPath(new String[]{"packages", "structures"});
    }
    if(operation.equals("Image"))
    {
      location = buildPath(new String[]{"images", "structures"});
    }
    if(operation.equals("Tilesets"))
    {
      location = buildPath(new String[]{"packages", "tilesets"});
    }
    
    //the arraylist of the buttons read from the directory stream
    ArrayList<Path> buttonList = new ArrayList<Path>();
    
    try
    (
      //get the DirectoryStream to the Path
      DirectoryStream<Path> structureFiles = Files.newDirectoryStream(Paths.get(location));
    )
    {
      //remove the reusableBits.lua file from the array, if it exists
      for(Path path : structureFiles)
      {
        String checkFile = path.toString();
        if(!checkFile.contains(".lua") && !checkFile.contains("Background.png"))
        {
          buttonList.add(path);
        }
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to get the directory stream for " + location, IOe));
      return "";
    }
    
    //JPanel to hold all the buttons in a GridLayout
    JPanel structurePanel = new JPanel();
    structurePanel.setLayout(new GridLayout(0, 4));
    
    //put the JPanel in a scrollpane
    Object[] structurePane = { new JScrollPane(structurePanel) };
    
    //set the size
    maxPaneSize = (int)Math.ceil((buttonList.size()+hasItemTxt)/4.0)*30;
    if(maxPaneSize > 450)
    {
      maxPaneSize = 450;
    }
    
    //set the size of the JScrollPane
    ((JScrollPane)structurePane[0]).setPreferredSize(new Dimension(750, maxPaneSize));
    ((JScrollPane)structurePane[0]).getVerticalScrollBar().setUnitIncrement(16);

    //array of objects for the popup
    JButton[] loadButtons = new JButton[buttonList.size()+1];
    
    //if there's an item.lua
    if(hasItemTxt == 1)
    {
      //add the item.lua button
      loadButtons[0] = new JButton("item.lua");
      loadButtons[0].addMouseListener(new loadButton(0));
      structurePanel.add(loadButtons[0]);
    }

    //create the buttons and add the listener
    for(int i = 0; i < buttonList.size(); i++)
    {
      //the name of the structure
      String structureName = buttonList.get(i).getFileName().toString();
      
      //remove wanted text from the name
      if(structureName.contains(".zip"))
      {
        structureName = structureName.substring(0, structureName.length()-4);
      }
      if(structureName.contains("CS_"))
      {
        structureName = structureName.substring(3);
      }
      if(structureName.contains("Foreground.png"))
      {
        structureName = structureName.substring(0, structureName.length()-14);
      }
      
      loadButtons[i+hasItemTxt] = new JButton(structureName);
      loadButtons[i+hasItemTxt].setPreferredSize(new Dimension(25, 25));
      loadButtons[i+hasItemTxt].addMouseListener(new loadButton(i+hasItemTxt));
      loadButtons[i+hasItemTxt].setToolTipText(structureName);
      
      listOfStructures.add(structureName);
      
      structurePanel.add(loadButtons[i+hasItemTxt]);
    }
    
    //the popup
    JOptionPane.showOptionDialog(null, popupText, operation, JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.DEFAULT_OPTION, null, structurePane, null);
    
    //return what was chosen, return an empty string if the user x'd out
    if(id == -1 || listOfStructures.size() == 0)
    {
      return "";
    }
    
    //catch for random exception that happened once. not sure why or how, but return an empty string if it happens again
    try
    {
      return listOfStructures.get(id);
    }
    catch(IndexOutOfBoundsException IOOBe)
    {
      j.err(new Exception("The id of " + Integer.toString(id) + " returned was outside the range of the arraylist.", IOOBe));
      return "";
    }
  }
  
  //takes a list of folders and returns a path string with system-specific separators
  public static String buildPath(String[] parts)
  {
    //holds the created path
    StringBuilder pathBuilder = new StringBuilder();
    
    //loop over the array and create the path
    for(int i = 0; i < parts.length; i++)
    {
      pathBuilder.append(parts[i]);
      pathBuilder.append(File.separator);
    }
    
    //return the path string with the correct file separator
    return pathBuilder.toString();
  }
  
  //resize the images to 14x14 if they aren't already and save back to the disk
  public static void resizeToGrid(Path path)
  {
    try
    {
      //the original image
      BufferedImage original = ImageIO.read(path.toFile());
      
      //if there is no need to resize
      if(original.getWidth() == cellWidthPX && original.getHeight() == cellHeightPX)
      {
        return;
      }
      
      //the resized image
      BufferedImage resized = new BufferedImage(cellWidthPX, cellHeightPX, BufferedImage.TRANSLUCENT);
      
      //graphics2d object to do the resizing
      Graphics2D imageGraphics = resized.createGraphics();
      
      //the rendering hints for how to handle the image
      imageGraphics.addRenderingHints(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
      
      //resize the image
      imageGraphics.drawImage(original, 0, 0, cellWidthPX, cellHeightPX, null);
      imageGraphics.dispose();
      
      //write it back to disk
      ImageIO.write(resized, "png", path.toFile());
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to read image to be resized " + path.toString(), IOe));
    }
  }
}