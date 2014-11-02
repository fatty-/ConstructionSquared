/*
Contains all the functionality for resetting
*/
import javax.swing.*;
import java.util.ArrayList;

public class objectReset
{
  //the object with the grid's data
  private gridCanvas gridPanel;
  
  //keeps and sends a copy of all the data that's being erased to the undo stack
  private ArrayList<Integer> lostData = null;
  
  //variables to keep track of the time threads take to finish
  private long start = 0;
  private long end = 0;
  
  public objectReset(gridCanvas gridPanel)
  {
    this.gridPanel = gridPanel;
  }

  //resets the grid to empty and default number of rows/columns
  public void resetGrid(Boolean addToStack)
  {
    //if reset is happening via redo, don't ask for confirmation
    if(addToStack)
    {
      //confirm reset to avoid accidents
      if(JOptionPane.showConfirmDialog(null, "Reset the grid? Current data will be lost.", "Reset Grid", JOptionPane.YES_NO_OPTION) != 0)
      {
        return;
      }
    }
    
    //output that it's starting
    j.out("Resetting grid...");
    j.setDrawString("Resetting grid...");
    gridPanel.repaint();
    
    //start of operation
    start = System.nanoTime();
    
    //where to store the data
    lostData = new ArrayList<Integer>();
    lostData.add(-8);
    lostData.add(staticData.getNumColumns());
    lostData.add(staticData.getNumRows());
    
    //store all tiles/objects before deletion
    for(int index = 0; index != 2; index++)
    {
      for(int i = 0; i < staticData.getNumColumns(); i++)
      {
        for(int k = 0; k < staticData.getNumRows(); k++)
        {
          //add tile data if addToStack is true and the tile is not empty
          if
          (
            addToStack && 
            staticData.getPicLocations(index, i, k) != staticData.emptyCell
          )
          {
            lostData.add(index);
            lostData.add(i);
            lostData.add(k);
            lostData.add(staticData.getPicLocations(index, i, k));
            staticData.setPicLocations(index, i, k, staticData.emptyCell);
          }
        }
      }
    }

    //if addToStack is true, actually add it to the stack
    if(addToStack)
    {
      gridPanel.getUrdo().addUndo(lostData);
    }
    
    //reset grid size and data
    staticData.setNumColumns(staticData.getDefaultNumColumns());
    staticData.setNumRows(staticData.getDefaultNumRows());
    
    //reset the layer to foreground and not building below y=0
    staticData.setLayerIndex(0);
    staticData.setBelow(false);
    staticData.setCurrentStructure("");
    gridPanel.setGridTitle();
    
    //reset last clicked cell
    staticData.setLastRow(0);
    staticData.setLastColumn(0);

    //calls to reset other things off the grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Grid reset ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
    
    //clear the string showing the status
    j.setDrawString("");
  }
  
  //reset all tiles, but leave the grid size intact
  public void resetTiles(Boolean addToStack)
  {
    //confirm reset to avoid accidents
    if(JOptionPane.showConfirmDialog(null, "Reset the tiles? Current data will be lost.", "Reset Tiles", JOptionPane.YES_NO_OPTION) != 0)
    {
      return;
    }
    
    //output that it's starting
    j.out("Resetting tiles...");
    j.setDrawString("Resetting tiles...");
    gridPanel.repaint();
    
    //start of operation
    start = System.nanoTime();
    
    //string to hold the tile data
    ArrayList<Integer> lostData = new ArrayList<Integer>();
    lostData.add(-7);
    
    //reset grid data
    for(int index = 0; index != 2; index++)
    {
      for(int i = 0; i < staticData.getNumColumns(); i++)
      {
        for(int k = 0; k < staticData.getNumRows(); k++)
        {
          //add tile data if addToStack is true and the tile is not empty
          if
          (
            addToStack && 
            staticData.getPicLocations(index, i, k) != staticData.emptyCell
          )
          {
            lostData.add(index);
            lostData.add(i);
            lostData.add(k);
            lostData.add(staticData.getPicLocations(index, i, k));
          }
          
          staticData.setPicLocations(index, i, k, staticData.emptyCell);
        }
      }
    }
    
    //if addToStack is true, actually add it to the stack
    if(addToStack)
    {
      gridPanel.getUrdo().addUndo(lostData);
    }
    
    //calls to reset other things off the grid
    staticData.setCurrentStructure("");
    gridPanel.setGridTitle();
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
    
    //end of operation
    end = System.nanoTime();
    
    j.out("Tile reset ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
    
    //clear the string showing the status
    j.setDrawString("");
  }
  
  //reset the current layer, but leave the grid size intact
  public void resetLayer(Boolean addToStack)
  {
    //confirm reset to avoid accidents
    if(JOptionPane.showConfirmDialog(null, "Reset the layer? Current data will be lost.", "Reset Layer", JOptionPane.YES_NO_OPTION) != 0)
    {
      return;
    }
    
    //output that it's starting
    j.out("Resetting layer...");
    j.setDrawString("Resetting layer...");
    gridPanel.repaint();
    
    //start of operation
    start = System.nanoTime();
    
    //string to hold the tile data
    ArrayList<Integer> lostData = new ArrayList<Integer>();
    lostData.add(-6);
    lostData.add(staticData.getLayerIndex());
    
    //reset active layer data
    for(int i = 0; i < staticData.getNumColumns(); i++)
    {
      for(int k = 0; k < staticData.getNumRows(); k++)
      {
        //add tile data if addToStack is true and the tile is not empty
        if
        (
          addToStack && 
          staticData.getPicLocations(staticData.getLayerIndex(), i, k) != staticData.emptyCell
        )
        {
          lostData.add(i);
          lostData.add(k);
          lostData.add(staticData.getPicLocations(staticData.getLayerIndex(), i, k));
        }
        
        staticData.setPicLocations(staticData.getLayerIndex(), i, k, staticData.emptyCell);
      }
    }
    
    //if addToStack is true, actually add it to the stack
    if(addToStack)
    {
      gridPanel.getUrdo().addUndo(lostData);
    }
    
    //calls to reset other things off the grid
    staticData.setCurrentStructure("");
    gridPanel.setGridTitle();
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Layer reset ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
    
    //clear the string showing the status
    j.setDrawString("");
  }
}