/*
Listener for any of the buttons clicked that aren't tile/object buttons
*/
import javax.swing.*;
import java.awt.event.*;
import java.lang.Thread;

public class modifyGrid implements MouseListener
{
  //needed for access to gridPanel's methods
  private gridCanvas gridPanel;
  
  //needed to be able to make the JScrollPane update if the grid becomes larger than the JPanel size
  private JScrollPane scrollPanel;
  
  public modifyGrid(gridCanvas gridPanel, JScrollPane scrollPanel)
  {
    this.gridPanel = gridPanel;
    this.scrollPanel = scrollPanel;
  }
  
  @Override
  public void mousePressed(MouseEvent event)
  {
    
  }
  
  //checks the text to determine which method to call from gridPanel
  @Override
  public void mouseReleased(MouseEvent event)
  {
    if(((JButton)event.getSource()).getText().equals("Top++"))
    {
      gridPanel.getRowsColumns().incrementTopRows(true, 1);
    }
    
    if(((JButton)event.getSource()).getText().equals("Top--"))
    {
      gridPanel.getRowsColumns().decrementTopRows(true);
    }
    
    if(((JButton)event.getSource()).getText().equals("Right++"))
    {
      gridPanel.getRowsColumns().incrementRightColumns(true, 1);
    }
    
    if(((JButton)event.getSource()).getText().equals("Right--"))
    {
      gridPanel.getRowsColumns().decrementRightColumns(true);
    }
    
    if(((JButton)event.getSource()).getText().equals("Bottom++"))
    {
      gridPanel.getRowsColumns().incrementBottomRows(true, 1);
    }
    
    if(((JButton)event.getSource()).getText().equals("Bottom--"))
    {
      gridPanel.getRowsColumns().decrementBottomRows(true);
    }
    
    if(((JButton)event.getSource()).getText().equals("Left++"))
    {
      gridPanel.getRowsColumns().incrementLeftColumns(true, 1);
    }
    
    if(((JButton)event.getSource()).getText().equals("Left--"))
    {
      gridPanel.getRowsColumns().decrementLeftColumns(true);
    }
    
    if(((JButton)event.getSource()).getText().equals("Reset Grid"))
    {
      Thread gridThread = new Thread(new gridThread(gridPanel, scrollPanel, 1));
      gridThread.start();
    }
      
    if(((JButton)event.getSource()).getText().equals("Reset Tiles"))
    {
      Thread gridThread = new Thread(new gridThread(gridPanel, scrollPanel, 2));
      gridThread.start();  
    }
      
    if(((JButton)event.getSource()).getText().equals("Reset Layer"))
    {
      Thread gridThread = new Thread(new gridThread(gridPanel, scrollPanel, 3));
      gridThread.start();
    }
      
    if
    (
      ((JButton)event.getSource()).getText().equals("Foreground") || 
      ((JButton)event.getSource()).getText().equals("Background")
    )
    {
      gridPanel.switchLayer();
      
      if(((JButton)event.getSource()).getText().equals("Foreground"))
      {
        ((JButton)event.getSource()).setText("Background");
      }
      else
      {
        ((JButton)event.getSource()).setText("Foreground");
      }
    }
            
    if(((JButton)event.getSource()).getText().equals("Delete"))
    {
      Thread gridThread = new Thread(new gridThread(gridPanel, scrollPanel, 5));
      gridThread.start();
    }
    
    if(((JButton)event.getSource()).getText().equals("Load"))
    {
      Thread gridThread = new Thread(new gridThread(gridPanel, scrollPanel, 6));
      gridThread.start();
    }

    if(((JButton)event.getSource()).getText().equals("Save"))
    {
      Thread gridThread = new Thread(new gridThread(gridPanel, scrollPanel, 7));
      gridThread.start();
    }
      
    if(
      ((JButton)event.getSource()).getText().equals("Below") || 
      ((JButton)event.getSource()).getText().equals("Above")
    )
    {
      staticData.buildBelow();
      
      if(((JButton)event.getSource()).getText().equals("Above"))
      {
        ((JButton)event.getSource()).setText("Below");
      }
      else
      {
        ((JButton)event.getSource()).setText("Above");
      }
    }
    
    //used to make the scrollpane's scrollbar appear for the custom paintcomponent
    scrollPanel.setViewport(scrollPanel.getViewport());
    
    //refocus on gridPanel so hotkeys work
    gridPanel.requestFocus();
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
  public void mouseClicked(MouseEvent event)
  {
    
  }
}