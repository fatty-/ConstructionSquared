/*
Main file, contains all the GIU and general setup
*/
import javax.swing.*;
import java.awt.event.*;
import java.awt.Point;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;


//main class
public class programGUI
{
  public static void main(String args[])
  {
    //logs any unexpected exceptions
    universalCatch lastThreadAction = new universalCatch();
    Thread.setDefaultUncaughtExceptionHandler(lastThreadAction);
    
    //delete the debug log
    try
    {
      if(Files.exists(Paths.get("logs", "ConstructionDebug.txt")))
      {
        Files.delete(Paths.get("logs", "ConstructionDebug.txt"));
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to delete the debug file", IOe));
    }

    //update log files
    //if there's the --append argument
    for(int i = 0; i < args.length; i++)
    {
      if(args[i].equals("--append"))
      {
        try
        (
          //reader for the log from last run
          BufferedReader logReader = new BufferedReader(new FileReader(staticData.buildPath(new String[]{"logs", "ConstructionLog.txt"})));
          
          //writer for the file that holds all previous logs
          FileWriter appendLogWriter = new FileWriter(staticData.buildPath(new String[]{"logs", "ConstructionLogOld.txt"}), true);
        )
        {
          //writer to clear the current log file
          FileWriter clearLogWriter = null;
          
          //arraylist to hold the log file
          ArrayList<String> logList = new ArrayList<String>();
          
          //holds a single line
          String line = logReader.readLine();
          
          //read the file
          while(line != null)
          {
            logList.add(line);
            line = logReader.readLine();
          }
          
          //append the log from the last run
          for(int k = 0; k < logList.size(); k++)
          {
            appendLogWriter.write(logList.get(k) + "\n");
          }
          
          //clear the log file for the current run
          clearLogWriter = new FileWriter(staticData.buildPath(new String[]{"logs", "ConstructionLog.txt"}));
          clearLogWriter.write("");
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Could not write to log file!", IOe));
        }
      }
    }
    
    //the version of the program
    String version = "0.9.426";
    
    //ArrayList to keep track of where the different pane buttons begin/end
    ArrayList<Integer> listOfSizes = new ArrayList<Integer>();
    
    //bimap to hold the image locations
    //when storing the size() in listOfSizes, it is incremented by 1 since the ids start at 1 rather than 0
    biMap<Integer, String> tileIcons = new biMap<Integer, String>();
    
    //array of Files to hold all the files found in the directory
    Path listFolder = null;

    //JFrame to hold everything
    JFrame gridFrame = new JFrame();
    
    //JPanel to hold the JToolBar and the custom paintComponent
    JPanel gridToolPanel = new JPanel();
    
    //JPanel to hold all the buttons
    JPanel tileObjectPanel = new JPanel();
    
    //The JToolBar to hold the non-tile buttons
    JToolBar gridBar = new JToolBar();
    JToolBar functionBar = new JToolBar();
    
    //menubar for some of the less visible program functions
    JMenuBar dropDownBar = new JMenuBar();
    JMenu miscMenu = new JMenu("Misc");
    JMenu optionsMenu = new JMenu("Options");
    JMenu zipMenu = new JMenu("Packaging");
    JMenu imageMenu = new JMenu("Images");
    JMenu aboutMenu = new JMenu("About");
    JMenu helpMenu = new JMenu("Help");
    
    //add all the drop down menus
    dropDownBar.add(miscMenu);
    dropDownBar.add(optionsMenu);
    dropDownBar.add(zipMenu);
    dropDownBar.add(imageMenu);
    dropDownBar.add(aboutMenu);
    dropDownBar.add(helpMenu);
    
    //the options in the miscMenu
    JMenuItem undoMenu = new JMenuItem("Undo (z)");
    JMenuItem redoMenu = new JMenuItem("Redo (y)");
    JMenuItem paintMenu = new JMenuItem("Paint Bucket (p)");
    JMenuItem showHideMenu = new JMenuItem("Add/Remove Bottom (spacebar)");
    JMenuItem incBrushMenu = new JMenuItem("Brush++ (=)");
    JMenuItem decBrushMenu = new JMenuItem("Brush-- (-)");
    JMenuItem swapLayerMenu = new JMenuItem("Swap Layers (s)");
    
    //options in the optionsMenu
    JMenuItem highlightMenu = new JMenuItem("Highlighting Off");
    JMenuItem cellOverwriteMenu = new JMenuItem("Disallow Overwrite");
    JMenuItem highlightColorMenu = new JMenuItem("Highlight Color");
    JMenuItem resetColorMenu = new JMenuItem("Reset Highlight Color");
    JMenuItem autosaveMenu = new JMenuItem("Autosave On");
    JMenuItem clearUrdo = new JMenuItem("Clear Undo/Redo");
    JMenuItem toggleGrid = new JMenuItem("Toggle Grid");
    
    //package menu
    JMenuItem importStructureMenu = new JMenuItem("Import Structure");
    JMenuItem packageStructureMenu = new JMenuItem("Package Structure");
    JMenuItem importTilesetMenu = new JMenuItem("Import Tileset");
    JMenuItem removeTilesetMenu = new JMenuItem("Remove Tileset");
    
    //the images menu
    JMenuItem loadImage = new JMenuItem("Load Image");
    JMenuItem saveImage = new JMenuItem("Save as Image");
    JMenuItem deleteImage = new JMenuItem("Delete Image");
    JMenuItem updateMappings = new JMenuItem("Update Mappings");
    JMenuItem resetMappings = new JMenuItem("Reset Mappings");
    
    //about menu items
    JMenuItem modVersionMenu = new JMenuItem("Mod Version");
    JMenuItem javaVersionMenu = new JMenuItem("Java Version");
    JMenuItem memoryMenu = new JMenuItem("Memory Usage");
    JMenuItem forumMenu = new JMenuItem("Forum Thread");
    JMenuItem downloadMenu = new JMenuItem("Mod Download");
    
    //help menu
    JMenuItem indexMenu = new JMenuItem("Index");

    //add the options to the menus
    miscMenu.add(undoMenu);
    miscMenu.add(redoMenu);
    miscMenu.add(showHideMenu);
    miscMenu.add(paintMenu);
    miscMenu.add(incBrushMenu);
    miscMenu.add(decBrushMenu);
    miscMenu.add(swapLayerMenu);
    
    optionsMenu.add(highlightMenu);
    optionsMenu.add(cellOverwriteMenu);
    optionsMenu.add(highlightColorMenu);
    optionsMenu.add(resetColorMenu);
    optionsMenu.add(autosaveMenu);
    optionsMenu.add(clearUrdo);
    optionsMenu.add(toggleGrid);
    
    zipMenu.add(importStructureMenu);
    zipMenu.add(packageStructureMenu);
    zipMenu.add(importTilesetMenu);
    zipMenu.add(removeTilesetMenu);
    
    imageMenu.add(loadImage);
    imageMenu.add(saveImage);
    imageMenu.add(deleteImage);
    imageMenu.add(updateMappings);
    imageMenu.add(resetMappings);
    
    aboutMenu.add(modVersionMenu);
    aboutMenu.add(javaVersionMenu);
    aboutMenu.add(memoryMenu);
    aboutMenu.add(forumMenu);
    aboutMenu.add(downloadMenu);
    
    helpMenu.add(indexMenu);
    
    //set the JToolBar to GridLayout, if only for the ease of spacing things out
    gridBar.setLayout(new GridLayout());
    functionBar.setLayout(new GridLayout());
    
    //JTabbedPanes for objects/tiles
    JTabbedPane tilePanes = new JTabbedPane();
    JTabbedPane objectPane = new JTabbedPane();
    
    //set the layout of all the JPanels used for tiles/objects to GridLayout
    tileObjectPanel.setLayout(new GridLayout(1, 2));

    //holds the JLabels
    //[0] = rows, [1] = columns, [2] = below
    JLabel[] infoLabels = 
    { 
      new JLabel("Columns: 1/25"),
      new JLabel("Rows: 1/25")
    };

    //create all the non-tile buttons
    JButton addRowTopButton = new JButton("Top++");
    JButton subRowTopButton = new JButton("Top--");
    JButton addColumnRightButton = new JButton("Right++");
    JButton subColumnRightButton = new JButton("Right--");
    JButton addRowBottomButton = new JButton("Bottom++");
    JButton subRowBottomButton = new JButton("Bottom--");
    JButton addColumnLeftButton = new JButton("Left++");
    JButton subColumnLeftButton = new JButton("Left--");
    JButton resetGridButton = new JButton("Reset Grid");
    JButton resetTilesButton = new JButton("Reset Tiles");
    JButton resetLayerButton = new JButton("Reset Layer");
    JButton switchLayersButton = new JButton("Foreground");
    JButton deleteButton = new JButton("Delete");
    JButton loadButton = new JButton("Load");
    JButton saveButton = new JButton("Save");
    JButton buildLeftButton = new JButton("Left");
    JButton buildBelowButton = new JButton("Above");
    
    //adds button tooltips with shortcuts
    addRowTopButton.setToolTipText("Increment Top (T)");
    subRowTopButton.setToolTipText("Decrement Top (t)");
    addColumnRightButton.setToolTipText("Increment Right (R)");
    subColumnRightButton.setToolTipText("Decrement Right (r)");
    addRowBottomButton.setToolTipText("Increment Bottom (B)");
    subRowBottomButton.setToolTipText("Decrement Bottom (b)");
    addColumnLeftButton.setToolTipText("Increment Left (L)");
    subColumnLeftButton.setToolTipText("Decrement Left (l)");
    resetGridButton.setToolTipText("Reset Everything (1)");
    resetTilesButton.setToolTipText("Reset Only Tiles (2)");
    resetLayerButton.setToolTipText("Reset Current Layer (3)");
    switchLayersButton.setToolTipText("Switch Active Layer (4)");
    deleteButton.setToolTipText("Delete Item (5)");
    loadButton.setToolTipText("Load Item (6)");
    saveButton.setToolTipText("Save Item (7)");
    buildBelowButton.setToolTipText("Build below the object (8)");
//
//begin setting up all the JTabbedPanes
//
    //list of file paths
    ArrayList<String> listOfPaths = new ArrayList<String>();
    
    //starting directory for where all the images are contained
    listFolder = Paths.get("images", "buttons");
    
    //iterate over all subdirectories to get all images and the paths to them
    try
    (
      DirectoryStream<Path> firstDir = Files.newDirectoryStream(listFolder);
    )
    {
      iterateFolder(listOfSizes, listOfPaths, firstDir, tileIcons);
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed entry to iterate over the tiles", IOe));
    }
    
    //variable to keep track of where the id should start from
    int id = 0;
    
    //arraylist of all the tabs to add to the JTabbedPane
    ArrayList<buttonTab> tabArray = new ArrayList<buttonTab>();
    
    //splits up the number if images to be put on each tab
    for(int i = 0; i < listOfSizes.size(); i++)
    {
      //if this is the first time, set to 0
      //otherwise set to the most recent index
      if(i == 0)
      {
        id = 0;
      }
      else
      {
        id = listOfSizes.get(i-1);
      }
      
      //keep track of when the object ids start and the tile ids end
      if(listOfPaths.contains(staticData.buildPath(new String[]{"objects"})))
      {
        staticData.setEndOfObjects(i);
      }
      
      //counter to keep track of how many tabs the current folder has
      int imageIndex = 0;
      
      //while there are tabs to add
      while(id < listOfSizes.get(i))
      {
        //if there are more buttons than can fit on the tab, add the max number
        //otherwise, add however many are left
        if(listOfSizes.get(i)/(id+(buttonTab.width*buttonTab.height)) > 0)
        {
          tabArray.add(new buttonTab(imageIndex*144, buttonTab.width*buttonTab.height, listOfPaths.get(i)));
        }
        else
        {
          tabArray.add(new buttonTab(imageIndex*144, listOfSizes.get(i) - id, listOfPaths.get(i)));
        }
        
        //increment the id by the number of buttons on a tab
        id += buttonTab.width*buttonTab.height;
        
        //increment the tabs-per-folder counter
        imageIndex++;
      }
    }
    
    //add the tabs to the JTabbedPane
    for(int i = 0; i < tabArray.size(); i++)
    {
      //holds the path to the current tab's images for easier string manipulation
      String containingFolder = tabArray.get(i).getTabPath();
      
      //get the name of the folder
      containingFolder = containingFolder.substring(0, containingFolder.lastIndexOf(File.separator));
      containingFolder = containingFolder.substring(containingFolder.lastIndexOf(File.separator)+1, containingFolder.length());
      
      //put the tabs in either the tile or object JTabbedPane
      if(tabArray.get(i).getTabPath().contains("tiles"))
      {
        tilePanes.addTab(containingFolder, tabArray.get(i));
      }
      else
      {
        objectPane.addTab(containingFolder, tabArray.get(i));
      }
    }
//
//end JTabbedPane setup
//
    //cast everything as an array of Objects to make passing nicer
    Object[] parameterList =
    {
      tileIcons, 
      infoLabels, 
      listOfSizes, 
      buildBelowButton, 
      switchLayersButton, 
      listOfPaths
    };
    
    //extended JPanel with the grid where 95% of everything is done
    gridCanvas gridPanel = new gridCanvas(parameterList);
    
    //update the options menu text now that this is initialized.
    if(!staticData.getHighlightCells())
    {
      highlightMenu.setText("Highlighting On");
    }

    if(!staticData.getCellOverwrite())
    {
      cellOverwriteMenu.setText("Allow Overwrite");
    }
    
    //JScrollPanel to allow the grid to be larger than the gridCanvas
    JScrollPane scrollPanel = new JScrollPane(gridPanel);
    
    //make the scrollbars not scroll really really slowly
    scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
    scrollPanel.getHorizontalScrollBar().setUnitIncrement(16);
    scrollPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    scrollPanel.setWheelScrollingEnabled(true); 
    
    //add to the non-tile components to the JToolBar
    gridBar.add(addRowTopButton);
    gridBar.add(subRowTopButton);
    gridBar.add(addColumnRightButton);
    gridBar.add(subColumnRightButton);
    gridBar.add(addRowBottomButton);
    gridBar.add(subRowBottomButton);
    gridBar.add(addColumnLeftButton);
    gridBar.add(subColumnLeftButton);
    functionBar.add(infoLabels[0]);
    functionBar.add(infoLabels[1]);
    functionBar.add(resetGridButton);
    functionBar.add(resetTilesButton);
    functionBar.add(resetLayerButton);
    functionBar.add(switchLayersButton);
    functionBar.add(deleteButton);
    functionBar.add(loadButton);
    functionBar.add(saveButton);
    //functionBar.add(buildLeftButton);
    functionBar.add(buildBelowButton);
    
    //add the JToolBar and extended JPanel to a container JPanel
    gridToolPanel.setLayout(new BorderLayout());
    gridToolPanel.add("North", gridBar);
    gridToolPanel.add("South", functionBar);
    gridToolPanel.add("Center", scrollPanel);
    
    //add the tile/object JPanels
    tileObjectPanel.add(tilePanes);
    tileObjectPanel.add(objectPane);

    //add the JPanels to the JFrame
    gridFrame.add(gridToolPanel);
    gridFrame.add(tileObjectPanel);

    //add the JMenuBar
    gridFrame.setJMenuBar(dropDownBar);

    //set JFrame behavior
    gridFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    gridFrame.setTitle("ConstructionSquared Beta v" + version);
    gridFrame.setSize(1000, 1000);
    gridFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
    gridFrame.setLayout(new GridLayout(2, 1));
    gridFrame.setVisible(true);
    
    //give this JFrame to the gridPanel after it's been added
    //if given before, it has no parent window which results in a null value
    gridPanel.setGridParent();
//end GUI setup
//
//start event handling
    //mouse listeners for the tile JButtons
    int setId = 0;
    for(int i = 0; i != tabArray.size(); i++)
    {
      setId = tabArray.get(i).setButtonListeners(setId, gridPanel);
    }
    
    //mouse listeners for the non-tile JButtons
    modifyGrid mouseListener = new modifyGrid(gridPanel, scrollPanel);
    addRowTopButton.addMouseListener(mouseListener);
    subRowTopButton.addMouseListener(mouseListener);
    addColumnRightButton.addMouseListener(mouseListener);
    subColumnRightButton.addMouseListener(mouseListener);
    addRowBottomButton.addMouseListener(mouseListener);
    subRowBottomButton.addMouseListener(mouseListener);
    addColumnLeftButton.addMouseListener(mouseListener);
    subColumnLeftButton.addMouseListener(mouseListener);
    resetGridButton.addMouseListener(mouseListener);
    resetTilesButton.addMouseListener(mouseListener);
    resetLayerButton.addMouseListener(mouseListener);
    switchLayersButton.addMouseListener(mouseListener);
    saveButton.addMouseListener(mouseListener);
    loadButton.addMouseListener(mouseListener);
    deleteButton.addMouseListener(mouseListener);
    buildLeftButton.addMouseListener(mouseListener);
    buildBelowButton.addMouseListener(mouseListener);
    
    //mouse listener for the center JPanel
    gridPanel.addMouseListener(gridPanel);
    gridPanel.addMouseMotionListener(gridPanel);
    
    //add the mouse listener for the jtabbed panes
    tilePanes.addMouseListener(new tabClick(gridPanel, tilePanes));
    objectPane.addMouseListener(new tabClick(gridPanel, objectPane));
    
    //mouse wheel listener to the to the center grid panel
    gridPanel.addMouseWheelListener(gridPanel);
    
    //keybindings for things without menus
    gridPanel.addKeyListener(new hotkey(gridPanel, scrollPanel, switchLayersButton, buildBelowButton));
    
    //keybindings for things with menus, since they need Actions
    Action keyUndo = new keyTyped(gridPanel, "z", scrollPanel);
    gridPanel.getInputMap().put(KeyStroke.getKeyStroke('z'), "keyUndo");
    gridPanel.getActionMap().put("keyUndo", keyUndo);
    
    Action keyRedo = new keyTyped(gridPanel, "y", scrollPanel);
    gridPanel.getInputMap().put(KeyStroke.getKeyStroke('y'), "keyRedo");
    gridPanel.getActionMap().put("keyRedo", keyRedo);
    
    Action keyPaint = new keyTyped(gridPanel, "p", scrollPanel);
    gridPanel.getInputMap().put(KeyStroke.getKeyStroke('p'), "keyPaint");
    gridPanel.getActionMap().put("keyPaint", keyPaint);
    
    Action keyIncBrush = new keyTyped(gridPanel, "=", scrollPanel);
    gridPanel.getInputMap().put(KeyStroke.getKeyStroke('='), "keyIncBrush");
    gridPanel.getActionMap().put("keyIncBrush", keyIncBrush);
    
    Action keyDecBrush = new keyTyped(gridPanel, "-", scrollPanel);
    gridPanel.getInputMap().put(KeyStroke.getKeyStroke('-'), "keyDecBrush");
    gridPanel.getActionMap().put("keyDecBrush", keyDecBrush);
    
    Action keyMinMax = new keyTyped(gridPanel, " ", scrollPanel);
    gridPanel.getInputMap().put(KeyStroke.getKeyStroke(' '), "keyMinMax");
    gridPanel.getActionMap().put("keyMinMax", keyMinMax);
    
    Action keySwap = new keyTyped(gridPanel, "s", scrollPanel);
    gridPanel.getInputMap().put(KeyStroke.getKeyStroke('s'), "keySwap");
    gridPanel.getActionMap().put("keySwap", keySwap);
    
    //add the menu actions
    //menuClick is for clicking the actual menu, otherwise it's for a hotkey
    undoMenu.addActionListener(keyUndo);
    redoMenu.addActionListener(keyRedo);
    paintMenu.addActionListener(keyPaint);
    showHideMenu.addActionListener(keyMinMax);
    incBrushMenu.addActionListener(keyIncBrush);
    decBrushMenu.addActionListener(keyDecBrush);
    swapLayerMenu.addActionListener(keySwap);
    
    menuClick actionListener = new menuClick(gridPanel, scrollPanel, version);
    highlightMenu.addActionListener(actionListener);
    cellOverwriteMenu.addActionListener(actionListener);
    highlightColorMenu.addActionListener(actionListener);
    resetColorMenu.addActionListener(actionListener);
    importStructureMenu.addActionListener(actionListener);
    packageStructureMenu.addActionListener(actionListener);
    importTilesetMenu.addActionListener(actionListener);
    removeTilesetMenu.addActionListener(actionListener);
    autosaveMenu.addActionListener(actionListener);
    loadImage.addActionListener(actionListener);
    saveImage.addActionListener(actionListener);
    deleteImage.addActionListener(actionListener);
    updateMappings.addActionListener(actionListener);
    resetMappings.addActionListener(actionListener);
    modVersionMenu.addActionListener(actionListener);
    javaVersionMenu.addActionListener(actionListener);
    memoryMenu.addActionListener(actionListener);
    clearUrdo.addActionListener(actionListener);
    toggleGrid.addActionListener(actionListener);
    forumMenu.addActionListener(actionListener);
    downloadMenu.addActionListener(actionListener);
    indexMenu.addActionListener(actionListener);

    //make hotkeys work on startup
    gridPanel.requestFocus();
  }
  
  //static to allow it's use in the main class
  private static void iterateFolder(ArrayList<Integer> listOfSizes, ArrayList<String> listOfPaths, DirectoryStream<Path> listFolder, biMap<Integer, String> tileIcons)
  {
    //to hold the current last index in listOfSizes so that tileIcons can be initialized
    //outside of the loop so it isn't local to the loop and get reset to 0 each time
    int currentSize = 0;
    
    //keep track of which file is being looked at
    int i = 0;
    
    //loop over the current directory
    for(Path image : listFolder)
    {
      //if it's a directory, enter it and begin again, otherwise update listOfPaths and listOfSizes
      if(Files.isDirectory(image))
      {
        //if the directory does not contain the full sized images of the icons
        if(!image.toString().contains("FullObjects"))
        {
          //call itself recursively to iterate over all subdirectories
          try
          (
            DirectoryStream<Path> subDir = Files.newDirectoryStream(image);
          )
          {
            iterateFolder(listOfSizes, listOfPaths, subDir, tileIcons);
          }
          catch(IOException IOe)
          {
            j.err(new Exception("Failed recursive iteration on " + image.toString(), IOe));
          }
        }
      }
      else
      {
        //only add new sizes the first time
        if(i == 0)
        {
          //temporary variable to hold the total number of images found so far
          //needed to allow checking if any entries exist in listOfSizes yet
          int totalSize = 0;
          try
          {
            totalSize = (int)Files.list(image.subpath(0, image.getNameCount()-1)).count();
          }
          catch(IOException IOe)
          {
            j.err(new Exception("Failed to get the number of files in " + image.toString(), IOe));
          }
        
          //if this isn't the first time, add the most recent size to the number of files found in this folder to get the cumulative number of files
          if(listOfSizes.size() > 0)
          {
            totalSize += listOfSizes.get(listOfSizes.size()-1);
            currentSize = listOfSizes.get(listOfSizes.size()-1);
          }
        
          //add the new entries to the arraylists
          //+1 to get the last folder separator character
          listOfPaths.add(image.toString().substring(0, image.toString().lastIndexOf(File.separator)+1));
          listOfSizes.add(totalSize);
        }
        
        //add images/ids to the biMap
        tileIcons.put(currentSize+i, image.getFileName().toString());
        
        //crop the image to the cell width/cell height and save back to disk
        staticData.resizeToGrid(image);
        
        //increment the counter
        i++;
      }
    }
  }
}