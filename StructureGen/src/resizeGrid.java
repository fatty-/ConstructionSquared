/*
Contains all the functionality for adding/removing rows/columns
*/
import java.util.ArrayList;


public class resizeGrid
{
  //the object with the grid's data
  private gridCanvas gridPanel;
  
  public resizeGrid(gridCanvas gridPanel)
  {
    this.gridPanel = gridPanel;
  }
  
  ////begin row/column methods////
  //adds a row to the grid
  //Boolean used to prevent things like an undo re-adding itself
  public void incrementTopRows(Boolean addToStack, int size)
  {
    for(int s = 0; s < size; s++)
    {
      //make a temporary int array to hold the current values
      int tempStorage[][][] = new int[2][staticData.getNumColumns()][staticData.getNumRows()];
      
      //copy them over
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            tempStorage[index][i][k] = staticData.getPicLocations(index, i, k);
          }
        }
      }
      
      //increment the row counter
      staticData.setNumRows(staticData.getNumRows()+1);
      
      //resize the int array
      staticData.setPicLocations(new int[2][staticData.getNumColumns()][staticData.getNumRows()]);
      
      //put the previous values back in and set the new row to -1
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            //if it's the new row, initialize to empty
            if(k == 0)
            {
              staticData.setPicLocations(index, i, k, staticData.emptyCell);
            }
            
            //otherwise, re-set the tile values
            if(k < staticData.getNumRows()-1)
            {
              staticData.setPicLocations(index, i, k+1, tempStorage[index][i][k]);
            }
          }
        }
      }
      
      //arraylist to pass to undo
      ArrayList<Integer> undo = new ArrayList<Integer>();
      undo.add(2);
      
      //if this wasn't called by undo/redo, add to the undo stack
      if(addToStack)
      {
        gridPanel.getUrdo().addUndo(undo);
      }
    }
    
    //calls to reset other things off the grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
  }
  
  //remove a row.
  //Boolean used to prevent things like an undo re-adding itself
  public void decrementTopRows(Boolean addToStack)
  {
    //string to hold the row info
    ArrayList<Integer> lostRow = new ArrayList<Integer>();
    lostRow.add(-2);
    
    //if removing a row won't result in 0 rows
    if(staticData.getNumRows() > 1)
    {
      //make a temporary int array to hold the current values
      int tempStorage[][][] = new int[2][staticData.getNumColumns()][staticData.getNumRows()];
    
      //copy them over
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            tempStorage[index][i][k] = staticData.getPicLocations(index, i, k);
          }
        }
      }
    
      //if this wasn't called by undo/redo
      if(addToStack)
      {
        //get the row that is going to be deleted
        for(int index = 0; index != 2; index++)
        {
          for(int i = 0; i < staticData.getNumColumns(); i++)
          {
            //add the row data
            lostRow.add(tempStorage[index][i][0]);
          }
        }
      
        //send to the undo stack
        gridPanel.getUrdo().addUndo(lostRow);
      }
    
      //decrement the row counter
      staticData.setNumRows(staticData.getNumRows()-1);
    
      //resize the int array
      staticData.setPicLocations(new int[2][staticData.getNumColumns()][staticData.getNumRows()]);
    
      //put the previous values back in
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            staticData.setPicLocations(index, i, k, tempStorage[index][i][k+1]);
          }
        }
      }
    }
    
    //calls to reset other things off the grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
  }
  
  //adds a row to the grid
  public void incrementBottomRows(Boolean addToStack, int size)
  {
    for(int s = 0; s < size; s++)
    {
      //make a temporary int array to hold the current values
      int tempStorage[][][] = new int[2][staticData.getNumColumns()][staticData.getNumRows()];
      
      //copy them over
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            tempStorage[index][i][k] = staticData.getPicLocations(index, i, k);
          }
        }
      }
      
      //increment the row counter
      staticData.setNumRows(staticData.getNumRows()+1);
      
      //resize the int array
      staticData.setPicLocations(new int[2][staticData.getNumColumns()][staticData.getNumRows()]);
      
      //put the previous values back in and set the new row to -1
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            //if this isn't the new row, re-set old values
            //otherwise set the new row to empty
            if(k < staticData.getNumRows()-1)
            {
              staticData.setPicLocations(index, i, k, tempStorage[index][i][k]);
            }
            else
            {
              staticData.setPicLocations(index, i, k, staticData.emptyCell);
            }
          }
        }
      }
      
      //add to undostack if not called by undo/redo
      ArrayList<Integer> undo = new ArrayList<Integer>();
      undo.add(3);
      if(addToStack)
      {
        gridPanel.getUrdo().addUndo(undo);
      }
    }
    
    //calls to reset other things off the grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
  }
  
  //remove a row.
  public void decrementBottomRows(Boolean addToStack)
  {
    //string to hold the row info
    ArrayList<Integer> lostRow = new ArrayList<Integer>();
    lostRow.add(-3);
    
    if(staticData.getNumRows() > 1)
    {
      //make a temporary int array to hold the current values
      int tempStorage[][][] = new int[2][staticData.getNumColumns()][staticData.getNumRows()];
    
      //copy them over
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            tempStorage[index][i][k] = staticData.getPicLocations(index, i, k);
          }
        }
      }
    
      //add the row to the undo stack
      if(addToStack)
      {
        //get the row that is going to be deleted
        for(int index = 0; index != 2; index++)
        {
          for(int i = 0; i < staticData.getNumColumns(); i++)
          {
            //create string containing the data to be lost
            lostRow.add(tempStorage[index][i][staticData.getNumRows()-1]);
          }
        }
      
        //send to the undostack
        gridPanel.getUrdo().addUndo(lostRow);
      }
    
      //decrement the row counter
      staticData.setNumRows(staticData.getNumRows()-1);
    
      //resize the int array
      staticData.setPicLocations(new int[2][staticData.getNumColumns()][staticData.getNumRows()]);
    
      //put the previous values back in and set the new row to -1
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            staticData.setPicLocations(index, i, k, tempStorage[index][i][k]);
          }
        }
      }
    }
    
    //calls to reset other things off the grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
  }

  //same as with rows, but for the columns
  public void incrementRightColumns(Boolean addToStack, int size)
  {
    for(int s = 0; s < size; s++)
    {
      int tempStorage[][][] = new int[2][staticData.getNumColumns()][staticData.getNumRows()];
      
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            tempStorage[index][i][k] = staticData.getPicLocations(index, i, k);
          }
        }
      }
      
      staticData.setNumColumns(staticData.getNumColumns()+1);
      
      ArrayList<Integer> undo = new ArrayList<Integer>();
      undo.add(4);
      if(addToStack)
      {
        gridPanel.getUrdo().addUndo(undo);
      }
      
      staticData.setPicLocations(new int[2][staticData.getNumColumns()][staticData.getNumRows()]);
      
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            if(i < staticData.getNumColumns()-1)
            {
              staticData.setPicLocations(index, i, k, tempStorage[index][i][k]);
            }
            else
            {
              staticData.setPicLocations(index, i, k, staticData.emptyCell);
            }
          }
        }
      }
    }
    
    //calls to reset other things off the grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
  }
  
  //same as with rows, but for the columns
  public void decrementRightColumns(Boolean addToStack)
  {
    ArrayList<Integer> lostRow = new ArrayList<Integer>();
    lostRow.add(-4);
    
    if(staticData.getNumColumns() > 1)
    {
      int tempStorage[][][] = new int[2][staticData.getNumColumns()][staticData.getNumRows()];
    
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            tempStorage[index][i][k] = staticData.getPicLocations(index, i, k);
          }
        }
      }
    
      //add the column to the undo stack
      if(addToStack)
      {
        //get the row that is going to be deleted
        for(int index = 0; index != 2; index++)
        {
          for(int i = 0; i < staticData.getNumRows(); i++)
          {
            lostRow.add(tempStorage[index][staticData.getNumColumns()-1][i]);
          }
        }
    
        gridPanel.getUrdo().addUndo(lostRow);
      }
    
      staticData.setNumColumns(staticData.getNumColumns()-1);
    
      staticData.setPicLocations(new int[2][staticData.getNumColumns()][staticData.getNumRows()]);
    
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            staticData.setPicLocations(index, i, k, tempStorage[index][i][k]);
          }
        }
      }
    }
    
    //calls to reset other things off the grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
  }

  //same as with rows, but for the columns
  public void incrementLeftColumns(Boolean addToStack, int size)
  {
    for(int s = 0; s < size; s++)
    {
      int tempStorage[][][] = new int[2][staticData.getNumColumns()][staticData.getNumRows()];
      
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            tempStorage[index][i][k] = staticData.getPicLocations(index, i, k);
          }
        }
      }
      
      staticData.setNumColumns(staticData.getNumColumns()+1);
      
      ArrayList<Integer> undo = new ArrayList<Integer>();
      undo.add(5);
      if(addToStack)
      {
        gridPanel.getUrdo().addUndo(undo);
      }
      
      staticData.setPicLocations(new int[2][staticData.getNumColumns()][staticData.getNumRows()]);
      
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            if(i == 0)
            {
              staticData.setPicLocations(index, i, k, staticData.emptyCell);
            }
            
            if(i < staticData.getNumColumns()-1)
            {
              staticData.setPicLocations(index, i+1, k, tempStorage[index][i][k]);
            }
          }
        }
      }
    }
    
    //calls to reset other things off the grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
  }

  //same as with rows, but for the columns
  public void decrementLeftColumns(Boolean addToStack)
  {
    ArrayList<Integer> lostRow = new ArrayList<Integer>();
    lostRow.add(-5);
    
    if(staticData.getNumColumns() > 1)
    {
      int tempStorage[][][] = new int[2][staticData.getNumColumns()][staticData.getNumRows()];
    
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            tempStorage[index][i][k] = staticData.getPicLocations(index, i, k);
          }
        }
      }
    
      //add the column to the undo stack
      if(addToStack)
      {
        //get the row that is going to be deleted
        for(int index = 0; index != 2; index++)
        {
          for(int i = 0; i < staticData.getNumRows(); i++)
          {
            lostRow.add(tempStorage[index][0][i]);
          }
        }
    
        gridPanel.getUrdo().addUndo(lostRow);
      }
    
      staticData.setNumColumns(staticData.getNumColumns()-1);
    
      staticData.setPicLocations(new int[2][staticData.getNumColumns()][staticData.getNumRows()]);
    
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            staticData.setPicLocations(index, i, k, tempStorage[index][i+1][k]);
          }
        }
      }
    }
      
    //calls to reset other things off the grid
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
  }
}