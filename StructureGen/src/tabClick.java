import javax.swing.*;
import java.awt.event.*;


public class tabClick implements MouseListener
{
  //reference to the center grid
  private gridCanvas gridPanel;
  
  //reference to the tab it's listening to
  private JTabbedPane clickedPane;
  
  public tabClick(gridCanvas gridPanel, JTabbedPane clickedPane)
  {
    this.gridPanel = gridPanel;
    this.clickedPane = clickedPane;
  }
  
  @Override
  public void mousePressed(MouseEvent event)
  {
    
  }
  
  @Override
  public void mouseReleased(MouseEvent event)
  {
    //if it was a double click
    if(event.getClickCount() == 2)
    {
      //get the filter, treated as *term*
      String filter = (String)JOptionPane.showInputDialog(null, "Enter the term to filter by:", "Save", JOptionPane.QUESTION_MESSAGE, null, null, "");
      
      //get the buttonTab for the clicked tab
      buttonTab clickedTab = (buttonTab)clickedPane.getSelectedComponent();
      
      //they x'd out of the popup
      if(filter != null)
      {
        //filter the buttons
        clickedTab.filterButtons(filter);
      }
    }
    
    //focus on gridPanel so hotkeys work
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