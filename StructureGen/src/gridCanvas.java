/*
Class that does 95% of the work, contains the grid
*/
import javax.swing.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.event.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.nio.file.Files;
import java.nio.file.Paths;


public class gridCanvas extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
  //never used since this class is never serialized, but necessary to suppress a warning
  private static final long serialVersionUID = 456;  
  
  //JButtons whose text changes, may switch to JButton array if more end up needing to be passed in
  private JButton buildBelowButton;
  private JButton switchLayersButton;

  //JLabels displaying the number of rows/columns
  private JLabel[] infoLabels;

  //array of Strings of the image locations
  private biMap<Integer, String> tileIcons;

  //holds the list of where the buttons for JPanels begin/end
  private ArrayList<Integer> listOfSizes;

  //holds the list of the paths to the images for the buttons
  private ArrayList<String> listOfPaths;
  
  //top JFrame of this jpanel, and the string to hold it's title
  private JFrame parentFrame = null;
  private String parentTitle = "";
  
  //control the autosave timing
  private ScheduledExecutorService executeTimer = Executors.newScheduledThreadPool(1);
  
  //resets stuff
  private objectReset gridReset;
  
  //increment/decrements stuff
  private resizeGrid rowsColumns;
  
  //save/load/delete stuff
  private objectDisk diskStructure;
  
  //package structures
  private structureZip structurePackager;
  
  //holds the bottom panel
  private Component bottomPanel = null;
  
  //the undo/redo stack variable
  private urdoStack urdo;
  
  //the structure-as-image object
  private imageStructure imageMaker;
  
  //used to import/remove tilesets
  private tilesets moveTiles;
  
  //constructor, needs to SuppressWarnings for unchecked to make compiler be quiet about cast Object to ArrayList
  @SuppressWarnings("unchecked")
  public gridCanvas(Object[] argumentList)
  {
    //assign the parameter list
    //set the tileIcons array
    this.tileIcons = (biMap<Integer, String>)argumentList[0];

    //set the JLabels
    this.infoLabels = (JLabel[])argumentList[1];

    //set the ArrayList of how many images each folder has
    this.listOfSizes = (ArrayList<Integer>)argumentList[2];

    //set the buttons
    this.buildBelowButton = (JButton)argumentList[3];
    this.switchLayersButton = (JButton)argumentList[4];

    //set the ArrayList of the paths to the image folders
    this.listOfPaths = (ArrayList<String>)argumentList[5];
    
    //initialize helper classes
    //initialize the grid in the staticData class
    staticData.init();
    
    //set the font, needs to be separate since it depends on the jpanel graphics which staticData doesn't have
    staticData.setHelveticaMetrics(this);
    
    //set the image array size, needs to be here since tileIcons is an argument to gridCanvas
    staticData.initDrawImage(tileIcons.size());
    staticData.initIconImage(tileIcons.size());
    
    //create the undo/redo stack
    urdo = new urdoStack(this);
    
    //create the object to load/save structures as images
    //also creates the mappings.txt file if it doesn't exist
    imageMaker = new imageStructure(this, tileIcons);
    
    //create the tile mover
    moveTiles = new tilesets(this);
    
    //try to read the config file
    try
    (
      BufferedReader configReader = new BufferedReader(new FileReader(staticData.buildPath(new String[]{"configuration", "config.txt"})))
    )
    {
      //the line read
      String line = configReader.readLine();
      
      //read the first line and assign if not null
      if
      (
        line != null && 
        line != ""
      )
      {
        staticData.setBackgroundColor(line.substring(line.lastIndexOf(": ")+2, line.length()));
      }
      else
      {
        staticData.setBackgroundColor("150, 75, 0");
      }
      
      line = configReader.readLine();
      
      if
      (
        line != null && 
        line != ""
      )
      {
        staticData.setCellOverwrite(Boolean.valueOf(line.substring(line.lastIndexOf(": ")+2, line.length())));
      }
      else
      {
        staticData.setCellOverwrite(true);
      }
      
      line = configReader.readLine();
      
      if
      (
        line != null && 
        line != ""
      )
      {
        staticData.setHighlightCells(Boolean.valueOf(line.substring(line.lastIndexOf(": ")+2, line.length())));
      }
      else
      {
        staticData.setHighlightCells(true);
      }
      
      //update the config file
      staticData.updateConfig();
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to read saved color", IOe));
      
      //file missing, set default values for everything
      staticData.setBackgroundColor("150, 75, 0");
      staticData.setCellOverwrite(true);
      staticData.setHighlightCells(true);
      
      //recreate the file
      staticData.updateConfig();
    }
    
    //initialize the reset object
    gridReset = new objectReset(this);
    
    //initialize the increment/decrement object
    rowsColumns = new resizeGrid(this);
    
    //initialize the save/load/delete object
    diskStructure = new objectDisk(this, argumentList);
    
    //packager to put structure files in one place for easy upload/download/copying
    structurePackager = new structureZip(this);
    
    //initialize other stuff
    //start the autosave timer
    executeTimer.scheduleAtFixedRate(new gridThread(this, null, 8), 5, 5, TimeUnit.MINUTES);
    
    //assign the Image array
    int tabIndex = 0;
    for(int i = 0; i != tileIcons.size(); i++)
    {
      //if it's at the platforms
      if(i == listOfSizes.get(tabIndex))
      {
        tabIndex++;
      }

      //create the image of the tile/object
      //ImageIcon will not complain if the path is invalid, so checking manually
      if(Files.exists(Paths.get(listOfPaths.get(tabIndex) + tileIcons.getNormal(i))))
      {
        //all icons loaded regardless of object or tile
        staticData.setIconImage(i, listOfPaths.get(tabIndex) + tileIcons.getNormal(i));
        
        //if it's a tile, load from one location
        if(listOfPaths.get(tabIndex).contains("tiles"))
        {
          staticData.setDrawImage(i, listOfPaths.get(tabIndex) + tileIcons.getNormal(i));
        }
        //otherwise, load the full-sized image from FullObjects
        else
        {
          //getting the path to the full-sized image rather than the icon
          //the current path
          String tabPath = listOfPaths.get(tabIndex);
          
          //getting the current races FullObjects folder
          String tabRace = tabPath.substring(0, tabPath.lastIndexOf(File.separator)) + "FullObjects";
          
          //getting the current image
          String tabImage = tileIcons.getNormal(i).substring(0, tileIcons.getNormal(i).indexOf("icon.png")) + ".png";
          
          //loading the image if it exists
          if(Files.exists(Paths.get(tabRace + File.separator + tabImage)))
          {
            staticData.setDrawImage(i, tabRace + File.separator + tabImage);
          }
          else
          {
            j.err(new Exception("Failed to locate the full sized image for object: " + tabRace + File.separator + tabImage));
          }
        }
      }
      else
      {
        j.err(new Exception("Bad path to ImageIcon " + listOfPaths.get(tabIndex) + tileIcons.getNormal(i)));
      }
    }
    
    //call to activate the scrollbar if necessary
    updateGridSize();
  }
  
  @Override
  public void mouseClicked(MouseEvent event)
  {
    
  }
  
  @Override
  public void mouseEntered(MouseEvent event)
  {
    
  }
  
  @Override
  public void mouseExited(MouseEvent event)
  {
    
  }
  
  @Override
  public void mouseReleased(MouseEvent event)
  {
    
  }
  
  //if the mouse was clicked...
  @Override
  public void mousePressed(MouseEvent event)
  {
    //roundabout way to getting the starting point of the grid.  Putting everything in one statement didn't work for some reason.
    //get the x-coordinate of the click
    staticData.setPixelsX(event.getX());
    
    //get the middle of the JPanel by width and subtract half of the grid's width, which results in the x-coordinate of the left side of the grid
    int clickx = (this.getWidth()/2)-(staticData.getCellWidthPX()*staticData.getNumColumns()/2);
    
    //get the y-coordinate of the click
    staticData.setPixelsY(event.getY());
    
    //get the middle of the JPanel by height and subtract half of the grid's height, which results in the y-coordinate of the top side of the grid
    int clicky = (this.getHeight()/2)-(staticData.getCellHeightPX()*staticData.getNumRows()/2);
    
    //subtract where the event was clicked from the minimum valid x,y values of where it could have occurred
    //so instead of containing the x,y offset from the upper left corner of the JPanel, it's the offset from the upper left corner of the grid
    staticData.setCurrentX(staticData.getPixelsX() - clickx);
    staticData.setCurrentY(staticData.getPixelsY() - clicky);
    
    //if x or y is 0 or less, then they clicked off the grid
    //if x or y is greater than the width or height of the grid, then they clicked off the grid
    //otherwise the click is valid
    if
    (
      staticData.getCurrentX() > 0 && 
      staticData.getCurrentY() > 0 && 
      staticData.getCurrentX() < staticData.getNumColumns()*staticData.getCellWidthPX() && 
      staticData.getCurrentY() < staticData.getNumRows()*staticData.getCellHeightPX()
    )
    {
      //integer divide by the cell width/height to get the cell that was clicked
      staticData.setCurrentX(staticData.getCurrentX()/staticData.getCellWidthPX());
      staticData.setCurrentY(staticData.getCurrentY()/staticData.getCellHeightPX());
      
      //assign the cell that was left-clicked the value of the most recent tile JButton that was clicked
      if(SwingUtilities.isLeftMouseButton(event))
      {
        leftClick();
      }
      
      //remove the tile from the cell if left-clicked
      if(SwingUtilities.isRightMouseButton(event))
      {
        rightClick();
      }
    }
    
    //update grid
    updateRowsColumns();
    repaint();
  }
  
  //or if it was dragged...
  @Override
  public void mouseDragged(MouseEvent event)
  {
    //roundabout way to getting the starting point of the grid.  Putting everything in one statement didn't work for some reason.
    //get the x-coordinate of the click
    staticData.setPixelsX(event.getX());
    
    //get the middle of the JPanel by width and subtract half of the grid's width, which results in the x-coordinate of the left side of the grid
    int clickx = (this.getWidth()/2)-(staticData.getCellWidthPX()*staticData.getNumColumns()/2);
    
    //get the y-coordinate of the click
    staticData.setPixelsY(event.getY());
    
    //get the middle of the JPanel by height and subtract half of the grid's height, which results in the y-coordinate of the top side of the grid
    int clicky = (this.getHeight()/2)-(staticData.getCellHeightPX()*staticData.getNumRows()/2);
    
    //subtract where the event was clicked from the minimum valid x,y values of where it could have occurred
    //so instead of containing the x,y offset from the upper left corner of the JPanel, it's the offset from the upper left corner of the grid
    staticData.setCurrentX(staticData.getPixelsX() - clickx);
    staticData.setCurrentY(staticData.getPixelsY() - clicky);
    
    //if x or y is 0 or less, then they clicked off the grid
    //if x or y is greater than the width or height of the grid, then they clicked off the grid
    //otherwise the click is valid
    if
    (
      staticData.getCurrentX() > 0 && 
      staticData.getCurrentY() > 0 && 
      staticData.getCurrentX() < staticData.getNumColumns()*staticData.getCellWidthPX() && 
      staticData.getCurrentY() < staticData.getNumRows()*staticData.getCellHeightPX()
    )
    {
      //integer divide by the cell width/height to get the cell that was clicked
      staticData.setCurrentX(staticData.getCurrentX()/staticData.getCellWidthPX());
      staticData.setCurrentY(staticData.getCurrentY()/staticData.getCellHeightPX());

      //assign the cell that was clicked the value of the most recent tile JButton that was left clicked
      if(SwingUtilities.isLeftMouseButton(event))
      {
        //don't let objects be placed in the background
        //also only if the mouse got dragged onto a new cell
        if
        (
          (
            staticData.getLayerIndex() != 1 || 
            staticData.getPicClicked() > listOfSizes.get(staticData.getEndOfObjects())
          ) 
          && 
          (
            staticData.getLastRow() != staticData.getCurrentY() || 
            staticData.getLastColumn() != staticData.getCurrentX()
          )
        )
        {
            //add to undoStack
            ArrayList<Integer> clickInfo = new ArrayList<Integer>();
            clickInfo.add(1);

            //if the current mouse locations is on the grid
            if
            (
              staticData.getCurrentX() >= 0 && 
              staticData.getCurrentX() < staticData.getNumColumns() && 
              staticData.getCurrentY() >= 0 && 
              staticData.getCurrentY() < staticData.getNumRows()
            )
            {
              //adjust the location of the brush
              //without this it would expand to the upper-right from the mouse rather than keeping the mouse centered
              int centerX = staticData.getCurrentX() - ((staticData.getBrushWidth()-1)/2);
              int centerY = staticData.getCurrentY() + ((staticData.getBrushHeight()-1)/2);
      
              //draw the area of the brush
              for(int x = 0; x < staticData.getBrushWidth(); x++)
              {
                for(int y = 0; y < staticData.getBrushHeight(); y++)
                {
                  //make sure the squares are drawn on the grid
                  if
                  (
                    centerX+x < staticData.getNumColumns() && 
                    centerX+x >= 0 && 
                    centerY-y >= 0 && 
                    centerY-y < staticData.getNumRows()
                  )
                  {
                    //if overwriting is allowed, place the tile
                    //if it isn't allowed but the cell is empty, place the tile
                    if
                    (
                      staticData.getCellOverwrite() == true || 
                      (
                        staticData.getCellOverwrite() == false && 
                        staticData.getPicLocations(staticData.getLayerIndex(), centerX+x, centerY-y) == staticData.emptyCell
                      )
                    )
                    {
                      //control key is pressed, doing other things
                      if
                      (
                        !staticData.getIsControl() && 
                        !staticData.getIsShift()
                      )
                      {
                        clickInfo.add(staticData.getLayerIndex());
                        clickInfo.add(centerX+x);
                        clickInfo.add(centerY-y);
                        clickInfo.add(staticData.getPicClicked());
                        clickInfo.add(staticData.getPicLocations(staticData.getLayerIndex(), centerX+x, centerY-y));
                        staticData.setPicLocations(staticData.getLayerIndex(), centerX+x, centerY-y, staticData.getPicClicked());
                      }
                    }
                  }
                }
              }
              
              //if shift is being held, update the current cell
              //DO NOT use centerX/Y, that's the middle of the brush not where the mouse is
              if(staticData.getIsShift())
              {
                staticData.setEndShift(staticData.getCurrentX(), staticData.getCurrentY());
                staticData.setBrushWidth(Math.abs(staticData.getStartShift().x - staticData.getEndShift().x) + 1);
                staticData.setBrushHeight(Math.abs(staticData.getStartShift().y - staticData.getEndShift().y) + 1);
              }
              
              //add to the undo stack
              if
              (
                !staticData.getIsControl() && 
                !staticData.getIsShift()
              )
              {
                urdo.addUndo(clickInfo);
              }
          
              //update last clicked cell
              staticData.setLastColumn(staticData.getCurrentX());
              staticData.setLastRow(staticData.getCurrentY());
            }
        }
      }
      
      //remove since right click
      if(SwingUtilities.isRightMouseButton(event))
      {
        //only do stuff if the mouse was dragged onto a new cell
        if
        (
          staticData.getLastRow() != staticData.getCurrentY() || 
          staticData.getLastColumn() != staticData.getCurrentX()
        )
        {
          //add to undoStack
          ArrayList<Integer> clickInfo = new ArrayList<Integer>();
          clickInfo.add(-1);

          //if the current mouse locations is on the grid
          if
          (
            staticData.getCurrentX() >= 0 && 
            staticData.getCurrentX() < staticData.getNumColumns() && 
            staticData.getCurrentY() >= 0 && 
            staticData.getCurrentY() < staticData.getNumRows()
          )
          {
            //adjust the location of the brush
            //without this it would expand to the upper-right from the mouse rather than keeping the mouse centered
            int centerX = staticData.getCurrentX() - ((staticData.getBrushWidth()-1)/2);
            int centerY = staticData.getCurrentY() + ((staticData.getBrushHeight()-1)/2);
      
            //draw the area of the brush
            for(int x = 0; x < staticData.getBrushWidth(); x++)
            {
              for(int y = 0; y < staticData.getBrushHeight(); y++)
              {
                //make sure the squares are drawn on the grid
                if
                (
                  centerX+x < staticData.getNumColumns() && 
                  centerX+x >= 0 && 
                  centerY-y >= 0 && 
                  centerY-y < staticData.getNumRows()
                )
                {
                  //control key is pressed, doing other things
                  if
                  (
                    !staticData.getIsControl() && 
                    !staticData.getIsShift()
                  )
                  {
                    clickInfo.add(staticData.getLayerIndex());
                    clickInfo.add(centerX+x);
                    clickInfo.add(centerY-y);
                    clickInfo.add(staticData.getPicLocations(staticData.getLayerIndex(), centerX+x, centerY-y));
                    staticData.setPicLocations(staticData.getLayerIndex(), centerX+x, centerY-y, staticData.emptyCell);
                  }
                }
              }
            }
            
            //add to the undo stack
            if
            (
              !staticData.getIsControl() && 
              !staticData.getIsShift()
            )
            {
              urdo.addUndo(clickInfo);
            }
        
            staticData.setLastColumn(staticData.getCurrentX());
            staticData.setLastRow(staticData.getCurrentY());
          }
        }
      }
    }

    //update grid
    updateRowsColumns();
    repaint();
  }
  
  @Override
  public void mouseMoved(MouseEvent event)
  {
    //roundabout way to getting the starting point of the grid.  Putting everything in one statement didn't work for some reason.
    //get the x-coordinate of the click
    staticData.setPixelsX(event.getX());
    
    //get the middle of the JPanel by width and subtract half of the grid's width, which results in the x-coordinate of the left side of the grid
    int clickX = (this.getWidth()/2)-(staticData.getCellWidthPX()*staticData.getNumColumns()/2);
    
    //get the y-coordinate of the click
    staticData.setPixelsY(event.getY());
    
    //get the middle of the JPanel by height and subtract half of the grid's height, which results in the y-coordinate of the top side of the grid
    int clickY = (this.getHeight()/2)-(staticData.getCellHeightPX()*staticData.getNumRows()/2);
    
    //subtract where the event was clicked from the minimum valid x,y values of where it could have occurred
    //so instead of containing the x,y offset from the upper left corner of the JPanel, it's the offset from the upper left corner of the grid
    staticData.setCurrentX(staticData.getPixelsX() - clickX);
    staticData.setCurrentY(staticData.getPixelsY() - clickY);
    
    //if x or y is 0 or less, then they clicked off the grid
    //if x or y is greater than the width or height of the grid, then they clicked off the grid
    //otherwise the click is valid
    if
    (
      staticData.getCurrentX() > 0 && 
      staticData.getCurrentY() > 0 && 
      staticData.getCurrentX() < staticData.getNumColumns()*staticData.getCellWidthPX() && 
      staticData.getCurrentY() < staticData.getNumRows()*staticData.getCellHeightPX()
    )
    {
      //integer divide by the cell width/height to get the cell that was clicked
      staticData.setCurrentX(staticData.getCurrentX()/staticData.getCellWidthPX());
      staticData.setCurrentY(staticData.getCurrentY()/staticData.getCellHeightPX());
      
      //update the current cell
      staticData.setLastColumn(staticData.getCurrentX());
      staticData.setLastRow(staticData.getCurrentY());
      
      //set the hover
      String hover = tileIcons.getNormal(staticData.getCurrentLocation());
      if(hover != null)
      {
        this.setToolTipText(hover.substring(0, hover.indexOf(".")));
      }
      else
      {
        this.setToolTipText(null);
      }
    }
    
    updateRowsColumns();
    repaint();
  }
  
  //if the mouse wheel moves, increment/decrement the picClicked
  @Override
  public void mouseWheelMoved(MouseWheelEvent event)
  {
    //if they scrolled down, check it doesn't go negative
    if
    (
      event.getWheelRotation() < 0 && 
      staticData.getPicClicked() > 0 && 
      staticData.getIsControl()
    )
    {
      staticData.setPicClicked(staticData.getPicClicked()-1);
    }
    //if they scrolled up, check to make sure it doesn't go too high
    else if
    (
      event.getWheelRotation() > 0 && 
      staticData.getPicClicked() < listOfSizes.get(listOfSizes.size()-1)-1  && 
      staticData.getIsControl()
    )
    {
      staticData.setPicClicked(staticData.getPicClicked()+1);
    }
    
    //pass event to the jscrollpane to get scrolling working
    this.getParent().getParent().dispatchEvent(event);
    
    //update the grid
    updateRowsColumns();
    repaint();
  }
  
  //override paintComponent
  @Override
  public void paintComponent(Graphics g)
  {
    //convert the graphics to a graphics2d
    Graphics2D canvas = (Graphics2D) g;
    
    //call paintComponent parent
    super.paintComponent(canvas);
    
    //draw the status of loading/saving/etc
    canvas.drawString(j.getDrawString(), 0, staticData.getHelveticaMetrics().getHeight());
    
    //set the font
    canvas.setFont(staticData.getHelveticaFont());
    
    //set grid color
    canvas.setColor(Color.BLACK);
    
    //get the center, then subtract/add half of the width/height to get the starting and ending coordinates for the lines
    int x1 = (getWidth()/2)-(staticData.getCellWidthPX()*staticData.getNumColumns()/2);
    int y1 = (getHeight()/2)-(staticData.getCellHeightPX()*staticData.getNumRows()/2);
    int x2 = (getWidth()/2)+(staticData.getCellWidthPX()*staticData.getNumColumns()/2);
    int y2 = (getHeight()/2)+(staticData.getCellHeightPX()*staticData.getNumRows()/2);
    
    //color tiles in the inactive layer to brown
    if(staticData.getLayerIndex() == 1)
    {
      //set grid fill color
      canvas.setColor(staticData.getBackgroundColor());
      
      for(int x = 0; x < staticData.getNumColumns(); x++)
      {
        for(int y = 0; y < staticData.getNumRows(); y++)
        {
          //if there is a tile in the foreground and not the background, then fill it
          if
          (
            staticData.getPicLocations(0, x, y) != staticData.emptyCell && 
            staticData.getPicLocations(1, x, y) == staticData.emptyCell
          )
          {
            if(staticData.getHighlightCells())
            {
              canvas.fillRect(x1+(staticData.getCellWidthPX()*x), y1+(staticData.getCellHeightPX()*y), staticData.getCellWidthPX(), staticData.getCellHeightPX());
            }
          }
        }
      }
    }
    else
    {
      //set grid fill color
      canvas.setColor(staticData.getBackgroundColor());
      
      for(int x = 0; x < staticData.getNumColumns(); x++)
      {
        for(int y = 0; y < staticData.getNumRows(); y++)
        {
          //if there is a tile in the foreground and not the background, then fill it
          if
          (
            staticData.getPicLocations(1, x, y) != staticData.emptyCell && 
            staticData.getPicLocations(0, x, y) == staticData.emptyCell
          )
          {
            if(staticData.getHighlightCells())
            {
              canvas.fillRect(x1+(staticData.getCellWidthPX()*x), y1+(staticData.getCellHeightPX()*y), staticData.getCellWidthPX(), staticData.getCellHeightPX());
            }
          }
        }
      }
    }
    
    //draw the vertical lines and text
    for(int i = 0; i != staticData.getNumColumns()+1; i++)
    {
      //draw the row/column number every 5th row/column
      if(i%5 == 0)
      {
        //but not at 0
        if(i != 0)
        {
          //if the grid is being draw
          if(staticData.getGridEnabled())
          {
            canvas.drawString(Integer.toString(i), x1+(staticData.getCellWidthPX()*i)-staticData.getCellWidthPX(), y1);
          }
        }
        
        //set the line color to blue
        canvas.setColor(Color.BLUE);
      }
      
      //draw the line if drawing the grid is enabled
      if(staticData.getGridEnabled())
      {
        canvas.drawLine(x1+(staticData.getCellWidthPX()*i), y1, x1+(staticData.getCellWidthPX()*i), y2);
      }
      
      //set the color back to black
      canvas.setColor(Color.BLACK);
    }
    
    //draw the horizontal lines and text
    //same logic as vertical lines
    for(int i = 0; i != staticData.getNumRows()+1; i++)
    {
      if(i%5 == 0)
      {
        if(i != 0)
        {
          //if the grid is being draw
          if(staticData.getGridEnabled())
          {
            canvas.drawString(Integer.toString(i), x1-staticData.getHelveticaMetrics().stringWidth(Integer.toString(i)), y1+(staticData.getCellHeightPX()*i));
          }
        }
        
        canvas.setColor(Color.BLUE);
      }
      
      //draw the line if drawing the grid is enabled
      if(staticData.getGridEnabled())
      {
        canvas.drawLine(x1, y1+(staticData.getCellHeightPX()*i), x2, y1+(staticData.getCellHeightPX()*i));
      }
      
      canvas.setColor(Color.BLACK);
    }
    
    //loop over the grid array and draw images when necessary (when not -1)
    for(int x = 0; x < staticData.getNumColumns(); x++)
    {
      for(int y = 0; y < staticData.getNumRows(); y++)
      {
        if
        (
          staticData.getPicLocations(staticData.getLayerIndex(), x, y) != staticData.emptyCell &&
          staticData.getPicLocations(staticData.getLayerIndex(), x, y) != staticData.blockedByObject
        )
        {
          //shorthand for getting width/height
          BufferedImage objectImage = staticData.getDrawImage(staticData.getPicLocations(staticData.getLayerIndex(), x, y));
          
          //check if null was returned
          //should never happen, but somehow it is
          if(objectImage == null)
          {
            j.err(new Exception("objectImage was returned as null of cell " + Integer.toString(x) + ", " + Integer.toString(y)));
            continue;
          }
          
          //offset by the width height, so the cell clicked has the lower right of the object instead of the upper left
          canvas.drawImage(objectImage, x1+(staticData.getCellWidthPX()*x) - objectImage.getWidth() + staticData.getCellHeightPX(), y1+(staticData.getCellWidthPX()*y) - objectImage.getHeight() + staticData.getCellHeightPX(), null);
        }
      }
    }
    
    //if the current mouse locations is on the grid
    if
    (
      staticData.getCurrentX() >= 0 && 
      staticData.getCurrentX() < staticData.getNumColumns() && 
      staticData.getCurrentY() >= 0 && 
      staticData.getCurrentY() < staticData.getNumRows()
    )
    {
      //variables to control where the mouse appears relative to the brush area
      int centerX = 0;
      int centerY = 0;
      
      //adjust the location of the brush
      //without this it would expand to the upper-right from the mouse rather than keeping the mouse centered
      if(!staticData.getIsShift())
      {
        centerX = staticData.getCurrentX() - ((staticData.getBrushWidth()-1)/2);
        centerY = staticData.getCurrentY() + ((staticData.getBrushHeight()-1)/2);
        
        //draw the area of the brush if it's not just a single cell
        if
        (
          staticData.getBrushWidth() > 1 || 
          staticData.getBrushHeight() > 1
        )
        {
          for(int x = 0; x < staticData.getBrushWidth(); x++)
          {
            for(int y = 0; y < staticData.getBrushHeight(); y++)
            {
              //make sure the squares are drawn on the grid
              if
              (
                centerX+x < staticData.getNumColumns() && 
                centerX+x >= 0 && 
                centerY-y >= 0 && 
                centerY-y < staticData.getNumRows()
              )
              {
                //calculate the location of the square
                int xLoc = x1+(staticData.getCellWidthPX()*(centerX+x));
                int yLoc = y1+(staticData.getCellHeightPX()*(centerY-y));
              
                //draw the square on the canvas
                if(x == 0)
                {
                  canvas.drawImage(staticData.getHighlight(3), xLoc, yLoc, null);
                }

                if(x == staticData.getBrushWidth()-1)
                {
                  canvas.drawImage(staticData.getHighlight(1), xLoc, yLoc, null);
                }

                if(y == 0)
                {
                  canvas.drawImage(staticData.getHighlight(2), xLoc, yLoc, null);
                }

                if(y == staticData.getBrushHeight()-1)
                {
                  canvas.drawImage(staticData.getHighlight(0), xLoc, yLoc, null);
                }
              }
            }
          }
        }
      }
      else
      {
        centerX = staticData.getStartShift().x;
        centerY = staticData.getStartShift().y;
        
        //draw the area of the brush if it's not just a single cell
        if
        (
          staticData.getBrushWidth() > 1 || staticData.getBrushHeight() > 1
        )
        {
          for(int x = 0; x < staticData.getBrushWidth(); x++)
          {
            for(int y = 0; y < staticData.getBrushHeight(); y++)
            {
              //invert the x/y if they dragged it not towards the upper-right corner
              if(staticData.getEndShift().x < staticData.getStartShift().x)
              {
                x *= -1;
              }
              if(staticData.getEndShift().y > staticData.getStartShift().y)
              {
                y *= -1;
              }
              
              //make sure the squares are drawn on the grid
              if
              (
                centerX+x < staticData.getNumColumns() && 
                centerX+x >= 0 && 
                centerY-y >= 0 && 
                centerY-y < staticData.getNumRows()
              )
              {
                //calculate the location of the square
                int xLoc = x1+(staticData.getCellWidthPX()*(centerX+x));
                int yLoc = y1+(staticData.getCellHeightPX()*(centerY-y));
              
                //if we're on the left-most column
                if(x == 0)
                {
                  //check if the cell is to the right or left of where it started
                  if(staticData.getEndShift().x > staticData.getStartShift().x)
                  {
                    canvas.drawImage(staticData.getHighlight(3), xLoc, yLoc, null);
                  }
                  else
                  {
                    canvas.drawImage(staticData.getHighlight(1), xLoc, yLoc, null);
                  }
                }

                //if we're on the right-most column
                if(Math.abs(x) == staticData.getBrushWidth()-1)
                {
                  //check if the cell is to the right or left of where it started
                  if(staticData.getEndShift().x > staticData.getStartShift().x)
                  {
                    canvas.drawImage(staticData.getHighlight(1), xLoc, yLoc, null);
                  }
                  else
                  {
                    canvas.drawImage(staticData.getHighlight(3), xLoc, yLoc, null);
                  }
                }

                //if we're on the bottom row
                if(y == 0)
                {
                  //check if the mouse is above or below where it started
                  if(staticData.getEndShift().y < staticData.getStartShift().y)
                  {
                    canvas.drawImage(staticData.getHighlight(2), xLoc, yLoc, null);
                  }
                  else
                  {
                    canvas.drawImage(staticData.getHighlight(0), xLoc, yLoc, null);
                  }
                }

                //if we're on the top row
                if(Math.abs(y) == staticData.getBrushHeight()-1)
                {
                  //check if the mouse is above or below where it started
                  if(staticData.getEndShift().y < staticData.getStartShift().y)
                  {
                    canvas.drawImage(staticData.getHighlight(0), xLoc, yLoc, null);
                  }
                  else
                  {
                    canvas.drawImage(staticData.getHighlight(2), xLoc, yLoc, null);
                  }
                }
              }
              
              //un-invert anything so that the loop still works
              if(x < 0)
              {
                x *= -1;
              }              
              if(y < 0)
              {
                y *= -1;
              }
            }
          }
        }
      }
      
      //overlay the image at the current cell
      canvas.drawImage(staticData.getIconImage(staticData.getPicClicked()), x1+(staticData.getCellWidthPX()*staticData.getCurrentX()), y1+(staticData.getCellHeightPX()*staticData.getCurrentY()), null);
    }
  }

  //start paintbucket////
  //paint all the tiles of the same type
  public void paintArea(int tileToReplace)
  {
    //store the tile was last clicked
    //used to restore the value if the user uses the paint bucket to delete, since that sets picClicked to -1
    int picClickedStorage = staticData.getPicClicked();
    
    //HashSet of points to hold the cells
    HashSet<Point> hashCells = new HashSet<Point>();

    //the first point
    hashCells.add(new Point(staticData.getLastColumn(), staticData.getLastRow()));
    
    //return if the tile clicked was the same as the tile to replace it with
    //otherwise it causes a near-infinite loop
    if
    (
      (
        tileToReplace == staticData.getPicClicked()
      ) 
      || 
      (
        tileToReplace == staticData.paintEmpty && 
        staticData.getPicLocations(staticData.getLayerIndex(), staticData.getCurrentX(), staticData.getCurrentY()) == staticData.emptyCell
      )
    )
    {
      return;
    }
    
    //setup for if using paint to remove cells
    if(tileToReplace == staticData.paintEmpty)
    {
      staticData.setPicClicked(staticData.emptyCell);
      tileToReplace = staticData.getPicLocations(staticData.getLayerIndex(), staticData.getLastColumn(), staticData.getLastRow());
    }
    
    //get the first point
    Point hashPoint = hashCells.iterator().next();
    
    //set the first values for undo
    ArrayList<Integer> lostTiles = new ArrayList<Integer>();
    lostTiles.add(-9);
    lostTiles.add(staticData.getLayerIndex());
    lostTiles.add(hashPoint.x);
    lostTiles.add(hashPoint.y);
    lostTiles.add(staticData.getPicLocations(staticData.getLayerIndex(), hashPoint.x, hashPoint.y));
    lostTiles.add(staticData.getPicClicked());
    
    //while there are cells the be changed, keep adding them and iterating over them
    while(hashCells.iterator().hasNext())
    {
      Point point = hashCells.iterator().next();
      //convert the cell
      staticData.setPicLocations(staticData.getLayerIndex(), point.x, point.y, staticData.getPicClicked());
        
      //check all adjacent cells, if they are within the grid
      if(point.x+1 < staticData.getNumColumns())
      {
        if(staticData.getPicLocations(staticData.getLayerIndex(), point.x+1, point.y) == tileToReplace)
        {
          //returns true if the point was added to the hashset
          if(hashCells.add(new Point(point.x+1, point.y)))
          {
            //store the previous state of the tiles getting painted
            lostTiles.add(point.x+1);
            lostTiles.add(point.y);
            lostTiles.add(staticData.getPicLocations(staticData.getLayerIndex(), point.x+1, point.y));
            lostTiles.add(staticData.getPicClicked());
          }
        }
      }

      if(point.x-1 >= 0)
      {
        if(staticData.getPicLocations(staticData.getLayerIndex(), point.x-1, point.y) == tileToReplace)
        {
          if(hashCells.add(new Point(point.x-1, point.y)))
          {
            //store the previous state of the tiles getting painted
            lostTiles.add(point.x-1);
            lostTiles.add(point.y);
            lostTiles.add(staticData.getPicLocations(staticData.getLayerIndex(), point.x-1, point.y));
            lostTiles.add(staticData.getPicClicked());
          }
        }
      }
        
      if(point.y+1 < staticData.getNumRows())
      {
        if(staticData.getPicLocations(staticData.getLayerIndex(), point.x, point.y+1) == tileToReplace)
        {
          if(hashCells.add(new Point(point.x, point.y+1)))
          {
            //store the previous state of the tiles getting painted
            lostTiles.add(point.x);
            lostTiles.add(point.y+1);
            lostTiles.add(staticData.getPicLocations(staticData.getLayerIndex(), point.x, point.y+1));
            lostTiles.add(staticData.getPicClicked());
          }
        }
      }
        
      if(point.y-1 >= 0)
      {
        if(staticData.getPicLocations(staticData.getLayerIndex(), point.x, point.y-1) == tileToReplace)
        {
          if(hashCells.add(new Point(point.x, point.y-1)))
          {
            //store the previous state of the tiles getting painted
            lostTiles.add(point.x);
            lostTiles.add(point.y-1);
            lostTiles.add(staticData.getPicLocations(staticData.getLayerIndex(), point.x, point.y-1));
            lostTiles.add(staticData.getPicClicked());
          }
        }
      }
        
      //remove the cell that was converted
      hashCells.remove(point);
    }
    
    urdo.addUndo(lostTiles);
    staticData.setPicClicked(picClickedStorage);
  }
  ////end paintbucket////
  
  //if there was a left click
  //can be called to simulate a click
  public void leftClick()
  {
    //don't let objects be placed in the background
    if
    (
      staticData.getLayerIndex() != 1 || 
      staticData.getPicClicked() > listOfSizes.get(staticData.getEndOfObjects())
    )
    {
      //update last clicked cell
      staticData.setLastColumn(staticData.getCurrentX());
      staticData.setLastRow(staticData.getCurrentY());
      
      //what to do if the paintbucket is enabled or not
      if(!staticData.getIsPainting())
      {
        //add to undoStack
        ArrayList<Integer> clickInfo = new ArrayList<Integer>();
        clickInfo.add(1);
        
        //if the current mouse locations is on the grid
        if
        (
          staticData.getCurrentX() >= 0 && 
          staticData.getCurrentX() < staticData.getNumColumns() && 
          staticData.getCurrentY() >= 0 && 
          staticData.getCurrentY() < staticData.getNumRows()
        )
        {
          //adjust the location of the brush
          //without this it would expand to the upper-right from the mouse rather than keeping the mouse centered
          int centerX = staticData.getCurrentX() - ((staticData.getBrushWidth()-1)/2);
          int centerY = staticData.getCurrentY() + ((staticData.getBrushHeight()-1)/2);
  
          //draw the area of the brush
          for(int x = 0; x < staticData.getBrushWidth(); x++)
          {
            for(int y = 0; y < staticData.getBrushHeight(); y++)
            {
              //make sure the squares are drawn on the grid
              if
              (
                centerX+x < staticData.getNumColumns() && 
                centerX+x >= 0 && 
                centerY-y >= 0 && 
                centerY-y < staticData.getNumRows()
              )
              {
                //if overwriting is allowed, place the tile
                //if it isn't allowed but the cell is empty, place the tile
                if
                (
                  staticData.getCellOverwrite() == true || 
                  (
                    staticData.getCellOverwrite() == false && 
                    staticData.getPicLocations(staticData.getLayerIndex(), centerX+x, centerY-y) == staticData.emptyCell
                  )
                )
                {
                  //control key is pressed, doing other things
                  if
                  (
                    !staticData.getIsControl() && 
                    !staticData.getIsShift()
                  )
                  {
                    clickInfo.add(staticData.getLayerIndex());
                    clickInfo.add(centerX+x);
                    clickInfo.add(centerY-y);
                    clickInfo.add(staticData.getPicClicked());
                    clickInfo.add(staticData.getPicLocations(staticData.getLayerIndex(), centerX+x, centerY-y));
                    staticData.setPicLocations(staticData.getLayerIndex(), centerX+x, centerY-y, staticData.getPicClicked());
                  }
                  else if(staticData.getIsControl())
                  {
                    //if the cell isn't empty, set picClicked to the cell's tile/object
                    if(staticData.getPicLocations(staticData.getLayerIndex(), centerX, centerY) != staticData.emptyCell)
                    {
                      staticData.setPicClicked(staticData.getPicLocations(staticData.getLayerIndex(), centerX, centerY));
                    }
                  }
                }
              }
            }
          }
        
          //add everything to the undo stack if there's anything to add
          if
          (
            clickInfo.size() != 1 && 
            !staticData.getIsControl() && 
            !staticData.getIsShift()
          )
          {
            urdo.addUndo(clickInfo);
          }
          
          //if shift is held, update the cell
          if(staticData.getIsShift())
          {
            staticData.setStartShift(staticData.getCurrentX(), staticData.getCurrentY());
          }
        }
      }
      else
      {
        //don't use paintbucket when it's an object
        if(!tileIcons.getNormal(staticData.getPicClicked()).contains("icon"))
        {
          paintArea(staticData.getPicLocations(staticData.getLayerIndex(), staticData.getCurrentX(), staticData.getCurrentY()));
        }
      }
    }
  }
  
  //if there was a right click
  //can be called to simulate a click
  public void rightClick()
  {
    //update last clicked cell
    staticData.setLastColumn(staticData.getCurrentX());
    staticData.setLastRow(staticData.getCurrentY());
    
    //if the paint bucket isn't active
    if(!staticData.getIsPainting())
    {
      //add to undo stack
      ArrayList<Integer> clickInfo = new ArrayList<Integer>();
      clickInfo.add(-1);
      
      //if the current mouse locations is on the grid
      if
      (
        staticData.getCurrentX() >= 0 && 
        staticData.getCurrentX() < staticData.getNumColumns() && 
        staticData.getCurrentY() >= 0 && 
        staticData.getCurrentY() < staticData.getNumRows()
      )
      {
        //adjust the location of the brush
        //without this it would expand to the upper-right from the mouse rather than keeping the mouse centered
        int centerX = staticData.getCurrentX() - ((staticData.getBrushWidth()-1)/2);
        int centerY = staticData.getCurrentY() + ((staticData.getBrushHeight()-1)/2);
  
        //draw the area of the brush
        for(int x = 0; x < staticData.getBrushWidth(); x++)
        {
          for(int y = 0; y < staticData.getBrushHeight(); y++)
          {
            //make sure the squares are drawn on the grid
            if
            (
              centerX+x < staticData.getNumColumns() && 
              centerX+x >= 0 && 
              centerY-y >= 0 && 
              centerY-y < staticData.getNumRows()
            )
            {
              if
              (
                !staticData.getIsControl() && 
                !staticData.getIsShift()
              )
              {
                clickInfo.add(staticData.getLayerIndex());
                clickInfo.add(centerX+x);
                clickInfo.add(centerY-y);
                clickInfo.add(staticData.getPicLocations(staticData.getLayerIndex(), centerX+x, centerY-y));
                staticData.setPicLocations(staticData.getLayerIndex(), centerX+x, centerY-y, staticData.emptyCell);
              }
            }
          }
        }
        
        //add to the undo stack
        if
        (
          !staticData.getIsControl() && 
          !staticData.getIsShift()
        )
        {
          urdo.addUndo(clickInfo);
        }
      }
    }
    else
    {
      paintArea(staticData.paintEmpty);
    }
  }
  
  ////begin helper methods
  //used by the button to tell which button was most recently clicked
  public gridCanvas setPicClicked(int picClicked)
  {
    if(staticData.getPicClicked() < tileIcons.size()+1)
    {
      staticData.setPicClicked(picClicked);
    }
    else
    {
      staticData.setPicClicked(staticData.emptyCell);
    }
    
    return this;
  }
  
  //switch the active layer
  public gridCanvas switchLayer()
  {
    if(staticData.getLayerIndex() == 0)
    {
      staticData.setLayerIndex(1);
    }
    else
    {
      staticData.setLayerIndex(0);
    }
    
    //calls to reset other things off the grid
    updateGridSize();
    updateRowsColumns();
    repaint();
    
    return this;
  }
  
  //set the preferredSize of this object based on the size of the grid, so that the JScrollPane knows when to put a scrollbar down
  public void updateGridSize()
  {
    this.setPreferredSize(new Dimension(staticData.getCellWidthPX()*(staticData.getNumColumns()+3), staticData.getCellHeightPX()*(staticData.getNumRows()+3)));
  }
  
  //update the number of rows/columns in the grid
  public void updateRowsColumns()
  {
    infoLabels[0].setText("Rows: " + Integer.toString(staticData.getLastRow()+1) + "/" + Integer.toString(staticData.getNumRows()));
    infoLabels[1].setText("Columns: " + Integer.toString(staticData.getLastColumn()+1) + "/" + Integer.toString(staticData.getNumColumns()));
  }

  //swap the isPainting value
  public void setPaintArea()
  {
    if(staticData.getIsPainting())
    {
      staticData.setIsPainting(false);
      this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    else
    {
      staticData.setIsPainting(true);
      this.setCursor(staticData.getPaintingCursor());
    }
    
    //reset brush width because a wider brush doesn't do anything while painting and is distracting
    staticData.setBrushWidth(1);
    staticData.setBrushHeight(1);
  }

  //get set the parent frame to the top jframe
  public void setGridParent()
  {
    parentFrame = (JFrame)SwingUtilities.windowForComponent(this);
    bottomPanel = parentFrame.getContentPane().getComponents()[1];
    parentTitle = parentFrame.getTitle();
  }
  
  //used to set the title, since the object that saves/loads doesn't have access to it
  public void setGridTitle()
  {
    if(!staticData.getCurrentStructure().equals(""))
    {
      parentFrame.setTitle(parentTitle + " - " + staticData.getCurrentStructure());
    }
    else
    {
      parentFrame.setTitle(parentTitle);
    }
  }
  
  //add/remove the panel with tiles/objects
  public void minMaxBottom()
  {
    //if it's been removed, re-add the bottom panel
    if(parentFrame.getContentPane().getComponents().length == 1)
    {
      parentFrame.add(bottomPanel);
      parentFrame.setLayout(new GridLayout(2, 1));
    }
    else
    //otherwise, remove it
    {
      parentFrame.remove(bottomPanel);
      parentFrame.setLayout(new GridLayout(1, 1));
    }
    
    //get the GUI to update
    parentFrame.revalidate();
    
    //recalculate which cell the mouse is in
    //get the middle of the JPanel by width and subtract half of the grid's width, which results in the x-coordinate of the left side of the grid
    int clickX = (this.getWidth()/2)-(staticData.getCellWidthPX()*staticData.getNumColumns()/2);
    
    //get the middle of the JPanel by height and subtract half of the grid's height, which results in the y-coordinate of the top side of the grid
    int clickY = (this.getHeight()/2)-(staticData.getCellHeightPX()*staticData.getNumRows()/2);
    
    //subtract where the event was clicked from the minimum valid x,y values of where it could have occurred
    //so instead of containing the x,y offset from the upper left corner of the JPanel, it's the offset from the upper left corner of the grid
    staticData.setCurrentX(staticData.getPixelsX() - clickX);
    staticData.setCurrentY(staticData.getPixelsY() - clickY);
    
    //if x or y is 0 or less, then they clicked off the grid
    //if x or y is greater than the width or height of the grid, then they clicked off the grid
    //otherwise the click is valid
    if
    (
      staticData.getCurrentX() > 0 && 
      staticData.getCurrentY() > 0 && 
      staticData.getCurrentX() < staticData.getNumColumns()*staticData.getCellWidthPX() && 
      staticData.getCurrentY() < staticData.getNumRows()*staticData.getCellHeightPX()
    )
    {
      //integer divide by the cell width/height to get the cell that was clicked
      staticData.setCurrentX(staticData.getCurrentX()/staticData.getCellWidthPX());
      staticData.setCurrentY(staticData.getCurrentY()/staticData.getCellHeightPX());
      
      //update the current cell
      staticData.setLastColumn(staticData.getCurrentX());
      staticData.setLastRow(staticData.getCurrentY());
    }
    
    updateRowsColumns();
  }
  
  //get the object responsible for save/load/deleting
  public objectDisk getDiskStructure()
  {
    return diskStructure;
  }
  
  //get the object responsible for resetting
  public objectReset getReset()
  {
    return gridReset;
  }
  
  //get the object responsible for the rows/columns
  public resizeGrid getRowsColumns()
  {
    return rowsColumns;
  }
  
  //get the packager
  public structureZip getPackager()
  {
    return structurePackager;
  }
  
  //get the switch layers button
  public gridCanvas setLayersButton(String text)
  {
    switchLayersButton.setText(text);
    
    return this;
  }
  
  //get the below button
  public gridCanvas setBelowButton(String text)
  {
    buildBelowButton.setText(text);
    
    return this;
  }
  
  //get the undo/redo stacks
  public urdoStack getUrdo()
  {
     return urdo;
  }
  
  //get the imageStructure object
  public imageStructure getImageStructure()
  {
    return imageMaker;
  }
  
  //get the tilesets object
  public tilesets getTilesets()
  {
    return moveTiles;
  }
  ////end helper methods////
}