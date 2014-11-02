/*
Listener for when certain menu buttons are clicked
*/
import javax.swing.*;
import java.awt.event.*;
import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;


//do stuff when the options menu gets clicked
public class menuClick implements ActionListener
{
  //the gridpanel to access it's data
  private gridCanvas gridPanel;
  
  //the version string
  private String version;
  
  //the scrollPane to pass to the image loading
  private JScrollPane scrollPanel;
  
  public menuClick(gridCanvas gridPanel, JScrollPane scrollPanel, String version)
  {
    this.gridPanel = gridPanel;
    this.version = version;
    this.scrollPanel = scrollPanel;
  }
  
  //where the actions are done
  @Override
  public void actionPerformed(ActionEvent event)
  {
    //get the event source
    JMenuItem clickMenu = (JMenuItem)event.getSource();
    
    //turn highlighting on
    if(clickMenu.getText().equals("Highlighting Off"))
    {
      //change the text
      clickMenu.setText("Highlighting On");
      
      //update the variable
      staticData.setHighlightCells(false);
      
      //update the config file
      staticData.updateConfig();
      
      //repaint and end
      gridPanel.repaint();
      return;
    }
    
    //turn highlighting off
    if(clickMenu.getText().equals("Highlighting On"))
    {
      //change the text
      clickMenu.setText("Highlighting Off");
      
      //update the variable
      staticData.setHighlightCells(true);
      
      //update the config file
      staticData.updateConfig();
      
      //repaint and end
      gridPanel.repaint();
      return;
    }
    
    //allow overwriting of tiles
    if(clickMenu.getText().equals("Disallow Overwrite"))
    {
      //change the text
      clickMenu.setText("Allow Overwrite");
      
      //update variable and end
      staticData.setCellOverwrite(false);
      
      //update the config file
      staticData.updateConfig();
      
      return;
    }
    
    //disallow overwriting tiles
    if(clickMenu.getText().equals("Allow Overwrite"))
    {
      //update text
      clickMenu.setText("Disallow Overwrite");
      
      //update variable and end
      staticData.setCellOverwrite(true);
      
      //update the config file
      staticData.updateConfig();
      
      return;
    }
    
    //change the color of the highlighting
    if(clickMenu.getText().equals("Highlight Color"))
    {
      //get the user input
      String newColor = JOptionPane.showInputDialog(null, "Enter the new color.  All values must be between 0 and 255.\nFor example: 100, 50, 50", "Color", JOptionPane.QUESTION_MESSAGE);
      
      //if they entered something, send the variable and repaint
      if(newColor != null)
      {
        staticData.setBackgroundColor(newColor);
        
        //update the config file
        staticData.updateConfig();
        
        gridPanel.repaint();
      }
      
      return;
    }
    
    //reset the background color
    if(clickMenu.getText().equals("Reset Highlight Color"))
    {
      staticData.setBackgroundColor("150, 75, 0");
      
      //update the config file
      staticData.updateConfig();
      
      gridPanel.repaint();
      return;
    }
    
    //package the current structure
    if(clickMenu.getText().equals("Package Structure"))
    {
      Thread gridThread = new Thread(new gridThread(gridPanel, null, 9));
      gridThread.start();
      
      return;
    }
    
    //import a structure
    if(clickMenu.getText().equals("Import Structure"))
    {
      Thread gridThread = new Thread(new gridThread(gridPanel, null, 10));
      gridThread.start();
    }
    
    //turn autosaving on
    if(clickMenu.getText().equals("Autosave Off"))
    {
      staticData.toggleAutoSave();
      clickMenu.setText("Autosave On");
    }
    
    //turn autosaving off
    if(clickMenu.getText().equals("Autosave On"))
    {
      staticData.toggleAutoSave();
      clickMenu.setText("Autosave Off");
    }
    
    //display the mod version
    if(clickMenu.getText().equals("Mod Version"))
    {
      JOptionPane.showMessageDialog(null, version);
    }
    
    //display the java version
    if(clickMenu.getText().equals("Java Version"))
    {
      JOptionPane.showMessageDialog(null, System.getProperty("java.version"));
    }
    
    //display the memory details
    if(clickMenu.getText().equals("Memory Usage"))
    {
      //description of the current program instance
      Runtime current = Runtime.getRuntime();
      
      //get memory stats
      long max = current.maxMemory();
      long total = current.totalMemory();
      long used = total - current.freeMemory();
      
      //memory stat modifiers
      String maxMod = "";
      String totalMod = "";
      String usedMod = "";
      
      if(max / 1024 < 1024)
      {
        max /= 1024;
        maxMod = "Available memory: " + Long.toString(max) + " KB";
      }
      else if(max / 1048576 < 1024)
      {
        max /= 1048576;
        maxMod = "Available memory: " + Long.toString(max) + " MB";
      }
      else
      {
        max /= 1073741824;
        maxMod = "Available memory: " + Long.toString(max) + " GB";
      }
      
      if(total / 1024 < 1024)
      {
        total /= 1024;
        totalMod = "Allocated memory: " + Long.toString(total) + " KB";
      }
      else if(total / 1048576 < 1024)
      {
        total /= 1048576;
        totalMod = "Allocated memory: " + Long.toString(total) + " MB";
      }
      else
      {
        total /= 1073741824;
        totalMod = "Allocated memory: " + Long.toString(total) + " GB";
      }
      
      if(used / 1024 < 1024)
      {
        used /= 1024;
        usedMod = "Memory in use: " + Long.toString(used) + " KB";
      }
      else if(used / 1048576 < 1024)
      {
        used /= 1048576;
        usedMod = "Memory in use: " + Long.toString(used) + " MB";
      }
      else
      {
        used /= 1073741824;
        usedMod = "Memory in use: " + Long.toString(used) + " GB";
      }
      
      JOptionPane.showMessageDialog(null, maxMod + "\n" + totalMod + "\n" + usedMod);
    }
    
    //open the mod's forum thread
    if(clickMenu.getText().equals("Forum Thread"))
    {
      try
      {
        Desktop.getDesktop().browse(new URI("http://community.playstarbound.com/index.php?threads/construction-squared.69274/"));
      }
      catch(IOException IOe)
      {
        j.err(new Exception("Error opening mod thread", IOe));
      }
      catch(URISyntaxException USe)
      {
        j.err(new Exception("Error opening mod thread", USe));
      }
    }
    
    //opens the mod's download page
    if(clickMenu.getText().equals("Mod Download"))
    {
      try
      {
        Desktop.getDesktop().browse(new URI("http://www.mediafire.com/download/y9u5dpq387249o9/ConstructionSquared.7z"));
      }
      catch(IOException IOe)
      {
        j.err(new Exception("Error opening download thread", IOe));
      }
      catch(URISyntaxException USe)
      {
        j.err(new Exception("Error opening download thread", USe));
      }
    }
    
    //opens the mod's download page
    if(clickMenu.getText().equals("Index"))
    {
      try
      {
        Desktop.getDesktop().browse(Paths.get("tutorial", "index.html").toUri());
      }
      catch(IOException IOe)
      {
        j.err(new Exception("Error opening tutorial", IOe));
      }
    }
    
    if(clickMenu.getText().equals("Clear Undo/Redo"))
    {
      gridPanel.getUrdo().clearUndo();
      gridPanel.getUrdo().clearRedo();
    }
    
    //load a structure from an image
    if(clickMenu.getText().equals("Load Image"))
    {
      //start the image load as a separate thread
      Thread gridThread = new Thread(new gridThread(gridPanel, scrollPanel, 11));
      gridThread.start();
    }
  
    //save a structure as an image
    if(clickMenu.getText().equals("Save as Image"))
    {
      //start the image save as a separate thread
      Thread gridThread = new Thread(new gridThread(gridPanel, null, 12));
      gridThread.start();
    }
    
    //delete an image structure
    if(clickMenu.getText().equals("Delete Image"))
    {
      //start the image delete as a separate thread
      Thread gridThread = new Thread(new gridThread(gridPanel, null, 13));
      gridThread.start();
    }
  
    //update the color mappings
    if(clickMenu.getText().equals("Update Mappings"))
    {
      gridPanel.getImageStructure().updateMappings();
    }
  
    //reset the mappings from scratch
    if(clickMenu.getText().equals("Reset Mappings"))
    {
      gridPanel.getImageStructure().resetMappings();
    }
    
    //import a tileset
    if(clickMenu.getText().equals("Import Tileset"))
    {
      //start the image delete as a separate thread
      Thread gridThread = new Thread(new gridThread(gridPanel, null, 14));
      gridThread.start();
    }
    
    //delete a tileset
    if(clickMenu.getText().equals("Remove Tileset"))
    {
      //start the image delete as a separate thread
      Thread gridThread = new Thread(new gridThread(gridPanel, null, 15));
      gridThread.start();
    }
    
    //toggle showing the grid lines
    if(clickMenu.getText().equals("Toggle Grid"))
    {
      staticData.toggleGrid();
      gridPanel.repaint();
    }
  }
}