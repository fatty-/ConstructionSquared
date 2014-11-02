//holds the undo/redo stacks and operations
import java.util.ArrayList;

public class urdoStack
{
  //holds the list of thing that can be undone/redone
  private ArrayList<ArrayList<Integer>> undoStack;
  private ArrayList<ArrayList<Integer>> redoStack;
  
  //the gridPanel
  private gridCanvas gridPanel;
  
  public urdoStack(gridCanvas gridPanel)
  {
    undoStack = new ArrayList<ArrayList<Integer>>();
    redoStack = new ArrayList<ArrayList<Integer>>();
    this.gridPanel = gridPanel;
  }
  
  //add an undo to the stack
  public void addUndo(ArrayList<Integer> newAction)
  {
    undoStack.add(newAction);
  }
  
  //add a redo to the stack
  public void addRedo(ArrayList<Integer> newAction)
  {
    redoStack.add(newAction);
  }
  
  //clear the undo stack
  public void clearUndo()
  {
    undoStack.clear();
  }
  
  //clear the redo stack
  public void clearRedo()
  {
    redoStack.clear();
  }
  
  //gets the undo at the specified index
  public ArrayList<Integer> getUndo(int index)
  {
    return undoStack.get(index);
  }
  
  //gets the redo at the specified index
  public ArrayList<Integer> getRedo(int index)
  {
    return redoStack.get(index);
  }
  
  //get the size of the undo stack
  public int getUndoStackSize()
  {
    return undoStack.size();
  }
  
  //get the size of the redo stack
  public int getRedoStackSize()
  {
    return redoStack.size();
  }
  
  //remove an undo from the stack
  public void removeUndo(int index)
  {
    undoStack.remove(index);
  }
  
  //remove a redo from the stack
  public void removeRedo(int index)
  {
    redoStack.remove(index);
  }
  
  ////start undo////
  //undo an action
  public void undo()
  {
    //catch for if the undoStack is empty
    try
    {
      //get the most recent addition
      ArrayList<Integer> lastAction = getUndo(getUndoStackSize()-1);
      
      //where to look in lastAction[] for the cell contents
      int cellLoc = 1;
      
      //check the type, negatives mean something was added, positives means something was removed
      switch(lastAction.get(0))
      {
        //re-fill the area
        case -9:
          //set the tiles
          for(int i = 2; i < lastAction.size(); i+=4)
          {
            staticData.setPicLocations(lastAction.get(1), lastAction.get(i), lastAction.get(i+1), lastAction.get(i+2));
          }
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
           break;
        
        //put everything back
        case -8:
          //set grid size
          staticData.setNumColumns(lastAction.get(1));
          staticData.setNumRows(lastAction.get(2));
          staticData.setPicLocations(new int[2][staticData.getNumColumns()][staticData.getNumRows()]);
          
          //set default value of grid to -1 rather than 0
          for(int index = 0; index != 2; index++)
          {
            for(int x = 0; x < staticData.getNumColumns(); x++)
            {
              for(int y = 0; y < staticData.getNumRows(); y++)
              {
                staticData.setPicLocations(index, x, y, staticData.emptyCell);
              }
            }
          }
          
          //set tiles
          for(int i = 3; i < lastAction.size(); i+=4)
          {
            staticData.setPicLocations(lastAction.get(i), lastAction.get(i+1), lastAction.get(i+2), lastAction.get(i+3));
          }
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
        
        //put both layers back
        case -7:
          //set tiles
          for(int i = 1; i < lastAction.size(); i+=4)
          {
            staticData.setPicLocations(lastAction.get(i), lastAction.get(i+1), lastAction.get(i+2), lastAction.get(i+3));
          }
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
        
        //put the layer back
        case -6:
          //set tiles
          for(int i = 2; i < lastAction.size(); i+=3)
          {
            staticData.setPicLocations(lastAction.get(1), lastAction.get(i), lastAction.get(i+1), lastAction.get(i+2));
          }
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
        
        //put the left column back
        case -5:
          //where to look in lastAction[] for the cell contents
          cellLoc = 1;
          
          //add the column back
          gridPanel.getRowsColumns().incrementLeftColumns(false, 1);
          
          //add the tiles back to the new row
          for(int index = 0; index != 2; index++)
          {
            for(int y = 0; y < staticData.getNumRows(); y++)
            {
              staticData.setPicLocations(index, 0, y, lastAction.get(cellLoc));
              cellLoc++;
            }
          }
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
        
        //put the right column back
        case -4:
          //where to look in lastAction[] for the cell contents
          cellLoc = 1;
          
          //add the column back
          gridPanel.getRowsColumns().incrementRightColumns(false, 1);
          
          //add the tiles back to the new row
          for(int index = 0; index != 2; index++)
          {
            for(int y = 0; y < staticData.getNumRows(); y++)
            {
              staticData.setPicLocations(index, staticData.getNumColumns()-1, y, lastAction.get(cellLoc));
              cellLoc++;
            }
          }
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
        
        //put the bottom row back
        case -3:
          //where to look in lastAction[] for the cell contents
          cellLoc = 1;
          
          //add the row back
          gridPanel.getRowsColumns().incrementBottomRows(false, 1);
          
          //add the tiles back to the new row
          for(int index = 0; index != 2; index++)
          {
            for(int x = 0; x < staticData.getNumColumns(); x++)
            {
              staticData.setPicLocations(index, x, staticData.getNumRows()-1, lastAction.get(cellLoc));
              cellLoc++;
            }
          }
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
        
        //put the top row back
        case -2:
          //where to look in lastAction[] for the cell contents
          cellLoc = 1;
          
          //add the row back
          gridPanel.getRowsColumns().incrementTopRows(false, 1);
          
          //add the tiles back to the new row
          for(int index = 0; index != 2; index++)
          {
            for(int x = 0; x < staticData.getNumColumns(); x++)
            {
              staticData.setPicLocations(index, x, 0, lastAction.get(cellLoc));
              cellLoc++;
            }
          }
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
    
        //put the tile back
        case -1:
          for(int i = 1; i < lastAction.size(); i+=4)
          {
            staticData.setPicLocations(lastAction.get(i), lastAction.get(i+1), lastAction.get(i+2), lastAction.get(i+3));
          }
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
    
        //remove the tile
        case 1:
          for(int i = 1; i < lastAction.size(); i+=5)
          {
            staticData.setPicLocations(lastAction.get(i), lastAction.get(i+1), lastAction.get(i+2), lastAction.get(i+4));
          }
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;

        //remove the top row
        case 2:
          gridPanel.getRowsColumns().decrementTopRows(false);
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
    
        //remove the bottom row
        case 3:
          gridPanel.getRowsColumns().decrementBottomRows(false);
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
          
        //remove the right column
        case 4:
          gridPanel.getRowsColumns().decrementRightColumns(false);
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
          
        //remove the left column
        case 5:
          gridPanel.getRowsColumns().decrementLeftColumns(false);
          
          //add this to the redo stack
          addRedo(getUndo(getUndoStackSize()-1));
          
          //remove this from the undo stack
          removeUndo(getUndoStackSize()-1);
          break;
          
        //if the type is somehow incorrectly set
        default: 
          j.out("Error: unknown undo action type");
          break;
      }
    }
    catch(ArrayIndexOutOfBoundsException AIOOBe)
    {
      j.out("Undo empty");
    }
    
    //update grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
  }
  ////end undo////

  ////start redo////
  //redo actions that were undone
  //values are reverse from undo, but the actions stay the same
  public void redo()
  {
    //catch for if the redoStack is empty
    try
    {
      //get the most recent addition
      ArrayList<Integer> lastAction = getRedo(getRedoStackSize()-1);
      
      //check the type, negatives mean something was removed, positives means something was added (opposite of undo)
      switch(lastAction.get(0))
      {
        //put the left column back
        case 5:
          gridPanel.getRowsColumns().incrementLeftColumns(false, 1);
          
          //add this to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
        
        //put the right column back
        case 4:
          gridPanel.getRowsColumns().incrementRightColumns(false, 1);
          
          //add this to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
        
        //put the bottom row back
        case 3:
          gridPanel.getRowsColumns().incrementBottomRows(false, 1);
          
          //add this to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
        
        //put the top row back
        case 2:
          gridPanel.getRowsColumns().incrementTopRows(false, 1);
          
          //add this to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
    
        //put the tile back
        case 1:
          for(int i = 1; i < lastAction.size(); i+=5)
          {
            staticData.setPicLocations(lastAction.get(i), lastAction.get(i+1), lastAction.get(i+2), lastAction.get(i+3));
          }
          
          //add this to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
    
        //remove the tile again
        case -1:
          for(int i = 1; i < lastAction.size(); i+=4)
          {
            staticData.setPicLocations(lastAction.get(i), lastAction.get(i+1), lastAction.get(i+2), staticData.emptyCell);
          }
          
          //add this to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
    
        //remove the top row
        case -2:
          gridPanel.getRowsColumns().decrementTopRows(false);
          
          //send the row back over to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
    
        //remove the bottom row
        case -3:
          gridPanel.getRowsColumns().decrementBottomRows(false);
          
          //send the row back over to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
      
        //remove the right column
        case -4:
          gridPanel.getRowsColumns().decrementRightColumns(false);
          
          //send the row back over to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
      
        //remove the left column
        case -5:
          gridPanel.getRowsColumns().decrementLeftColumns(false);
          
          //send the row back over to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
          
        //clear the layer
        case -6:
          for(int i = 2; i < lastAction.size(); i+=3)
          {
            staticData.setPicLocations(lastAction.get(1), lastAction.get(i), lastAction.get(i+1), staticData.emptyCell);
          }
          
          //send the row back over to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
        
        //clear both layers
        case -7:
          for(int i = 1; i < lastAction.size(); i+=4)
          {
            staticData.setPicLocations(lastAction.get(i), lastAction.get(i+1), lastAction.get(i+2), staticData.emptyCell);
          }
          
          //send the row back over to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
        
        //remove everything
        case -8:
          gridPanel.getReset().resetGrid(false);
          
          //send the row back over to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
        
        //un-fill the area
        case -9:
          //set the tiles
          for(int i = 2; i < lastAction.size(); i+=4)
          {
            staticData.setPicLocations(lastAction.get(1), lastAction.get(i), lastAction.get(i+1), lastAction.get(i+3));
          }
          
          //send the row back over to the undo stack
          addUndo(getRedo(getRedoStackSize()-1));
          
          //remove this from the redo stack
          removeRedo(getRedoStackSize()-1);
          break;
      
        //if the type is somehow incorrectly set
        default: 
          j.out("Error: unknown undo action type");
          break;
      }
    }
    catch(ArrayIndexOutOfBoundsException AIOOBe)
    {
      j.out("Redo empty");
    }

    //update grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
  }
  ////end redo////
}