/*
A separate thread to run things that take a while time to run, so the entire program doesn't freeze up
*/
import javax.swing.*;
import java.lang.Thread;


public class gridThread implements Runnable
{
  //needed for access to gridPanel's methods
  private gridCanvas gridPanel;
  
  //needed to update the scrollbar on resize
  private JScrollPane scrollPanel;
  
  //the method to run
  private int method;
  
  //variables to keep track of the time threads take to finish
  private long start = 0;
  private long end = 0;

  public gridThread(gridCanvas gridPanel, JScrollPane scrollPanel, int method)
  {
    this.gridPanel = gridPanel;
    this.scrollPanel = scrollPanel;
    this.method = method;
  }
  
  @Override
  public void run()
  {
    //output that the thread is starting
    j.out("Thread Starting");
    
    switch(method)
    {
      //reset the grid
      case 1:
        synchronized(staticData.class)
        {
          gridPanel.getReset().resetGrid(true);
          gridPanel.setLayersButton("Foreground");
          gridPanel.setBelowButton("Above");
        }
      break;
      
      //reset the tiles
      case 2:
        synchronized(staticData.class)
        {
          gridPanel.getReset().resetTiles(true);
        }
      break;
      
      //reset the current layer
      case 3:
        synchronized(staticData.class)
        {
          gridPanel.getReset().resetLayer(true);
        }
      break;
      
      //delete a structure
      case 5:
        synchronized(staticData.class)
        {
          gridPanel.getDiskStructure().deleteStructure();
        }
        break;
      
      //load a structure
      case 6:
        synchronized(staticData.class)
        {
          gridPanel.getDiskStructure().loadStructure();
          gridPanel.setLayersButton("Foreground");
        }
        break;
      
      //save a structure
      case 7:
        synchronized(staticData.class)
        {
          if(!staticData.isGridEmpty(staticData.getGrid()))
          {
            gridPanel.getDiskStructure().saveStructure(true);
          }
          else
          {
            j.out("Grid is empty, aborting save");
            JOptionPane.showMessageDialog(null, "Grid is empty!");
          }
        }
        break;
      
      //autosave a structure
      case 8:
        synchronized(staticData.class)
        {
          if(!staticData.isGridEmpty(staticData.getGrid()) && staticData.getAutoSave())
          {
            gridPanel.getDiskStructure().saveStructure(false);
          }
          else
          {
            j.out("Aborting Autosave");
          }
        }
        break;
        
      //package a structure for easy copying
      case 9:
        synchronized(staticData.class)
        {
          gridPanel.getPackager().packageStructure();
        }
        break;
      
      //import a structure
      case 10:
        synchronized(staticData.class)
        {
          gridPanel.getPackager().importStructure();
        }
        break;
      
      //load an image
      case 11:
        synchronized(staticData.class)
        {
          //the name of the structure to load
          String name = staticData.showAvailableStructures(0, "Image", "Select the image to load");
          
          //check to make sure that an empty string wasn't returned
          if(!name.equals(""))
          {
            //load the image and update the title
            gridPanel.getImageStructure().loadImageStructure(name);
            gridPanel.setGridTitle();
          }
        }
        break;
      
      //save an image
      case 12:
        synchronized(staticData.class)
        {
          gridPanel.getImageStructure().saveImageStructure();
        }
        break;
        
      //save an image
      case 13:
        synchronized(staticData.class)
        {
          //the name of the structure to load
          String name = staticData.showAvailableStructures(0, "Image", "Select the name of the image to delete");
          
          //check to make sure that an empty string wasn't returned
          if(!name.equals(""))
          {
            gridPanel.getImageStructure().deleteImageStructure(name);
          }
        }
        break;
      
      //import a tileset
      case 14:
        synchronized(staticData.class)
        {
          gridPanel.getTilesets().importTileset();
        }
        break;
      
      //remove a tileset
      case 15:
        synchronized(staticData.class)
        {
          gridPanel.getTilesets().removeTileset();
        }
        break;
    }
    
    //output that the thread is ending
    j.out("Thread Ending");
    
    //if the scrollpane isn't null, aka wasn't necessary or possible to pass
    if(scrollPanel != null)
    {
      //used to make the scrollpane's scrollbar appear for the custom paintcomponent
      scrollPanel.setViewport(scrollPanel.getViewport());
    
      //refocus on gridPanel so hotkeys work
      gridPanel.requestFocus();
    }
    
    //the thread is over
    j.out("Thread Done\n");
  }
}