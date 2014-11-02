/*
Used for the buttons on the bottom half to set what the brush is placing
*/
import javax.swing.*;
import java.awt.event.*;

public class clickTile implements MouseListener
{
  //JButton to compare the clicked JButton to
  private JButton buttonCompare;
  
  //this JButton's id
  private int id;
  
  //needed for access to gridPanel's methods
  private gridCanvas gridPanel;
  
  public clickTile(JButton buttonCompare, int id, gridCanvas gridPanel)
  {
    this.buttonCompare = buttonCompare;
    this.id = id;
    this.gridPanel = gridPanel;
    
    //set the initial tile/icon to the final tile loaded
    gridPanel.setPicClicked(id);
  }
  
  @Override
  public void mousePressed(MouseEvent event)
  {
    
  }
  
  //call to public method to set which button was clicked if this button was the event source
  @Override
  public void mouseReleased(MouseEvent event)
  {
    if(event.getSource() == buttonCompare)
    {
      gridPanel.setPicClicked(id);
    }
    
    //reset gridPanel to have focus, so the keyboard shortcuts work
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