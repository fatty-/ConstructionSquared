/*
Listener for the buttons in the load/delete popup
*/
import javax.swing.*;
import java.awt.event.*;
import java.awt.Window;

public class loadButton implements MouseListener
{
  //the button's id
  private int id;
  
  //set values
  public loadButton(int id)
  {
    this.id = id;
  }
  
  //set the structure to load and close the popup
  @Override
  public void mousePressed(MouseEvent event)
  {
    staticData.setId(id);
    JOptionPane.getRootFrame().dispose(); 
  }
  
  @Override
  public void mouseReleased(MouseEvent event)
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
  public void mouseClicked(MouseEvent event)
  {
    
  }
}