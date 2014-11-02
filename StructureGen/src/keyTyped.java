/*
Implements action listener for some of the dropdown menu buttons
*/
import javax.swing.*;
import java.awt.event.*;
import java.lang.Thread;


public class keyTyped extends AbstractAction implements ActionListener
{
  //needed for access to gridPanel's methods
  private gridCanvas gridPanel;
  
  //String for the pressed key
  private String keyPressed;
  
  //needed to update the scrollbar on resize
  private JScrollPane scrollPanel;
  
  //never used since this class is never serialized, but necessary to suppress a warning
  private static final long serialVersionUID = 123;

  keyTyped(gridCanvas gridPanel, String keyPressed, JScrollPane scrollPanel)
  {
    this.gridPanel = gridPanel;
    this.keyPressed = keyPressed;
    this.scrollPanel = scrollPanel;
  }
  
  //Do an action when certain keys are hit
  @Override
  public void actionPerformed(ActionEvent event)
  {
    if(keyPressed.equals("z"))
    {
      gridPanel.getUrdo().undo();
    }
    
    if(keyPressed.equals("y"))
    {
      gridPanel.getUrdo().redo();
    }
    
    if(keyPressed.equals("p"))
    {
      gridPanel.setPaintArea();
    }
    
    if(keyPressed.equals("="))
    {
      staticData.incrementBrushWidth();
      staticData.incrementBrushHeight();
    }
    
    if(keyPressed.equals("-"))
    {
      staticData.decrementBrushWidth();
      staticData.decrementBrushHeight();
    }
    
    //adds/removes the bottom JPanel with the objects/tile buttons
    if(keyPressed.equals(" "))
    {
      gridPanel.minMaxBottom();
    }
    
    //swaps the layers, NOT the active layer
    if(keyPressed.equals("s"))
    {
      staticData.swapLayer();
    }
    
    //force the jscrollpane to update the scrollbars
    scrollPanel.setViewport(scrollPanel.getViewport());
    
    //apparently setting the viewport also sets the focus to the jscrollpane, so this is needed to make the hotkeys work again
    gridPanel.requestFocus();
  }
}