/*
Implements the key listener for the various hotkeys
*/
import javax.swing.*;
import java.awt.event.*;
import java.awt.Point;


public class hotkey implements KeyListener
{
  gridCanvas gridPanel;
  
  JScrollPane scrollPanel;
  
  JButton switchButton;
  JButton belowButton;
  
  public hotkey(gridCanvas gridPanel, JScrollPane scrollPanel, JButton switchButton, JButton belowButton)
  {
    this.gridPanel = gridPanel;
    this.scrollPanel = scrollPanel;
    this.switchButton = switchButton;
    this.belowButton = belowButton;
  }
  
  @Override
  public void keyTyped(KeyEvent event)
  {
    if(event.getKeyChar() == 'T')
    {
      gridPanel.getRowsColumns().incrementTopRows(true, 1);
    }
    
    if(event.getKeyChar() == 't')
    {
      gridPanel.getRowsColumns().decrementTopRows(true);
    }
    
    if(event.getKeyChar() == 'R')
    {
      gridPanel.getRowsColumns().incrementRightColumns(true, 1);
    }
    
    if(event.getKeyChar() == 'r')
    {
      gridPanel.getRowsColumns().decrementRightColumns(true);
    }
    
    if(event.getKeyChar() == 'B')
    {
      gridPanel.getRowsColumns().incrementBottomRows(true, 1);
    }
    
    if(event.getKeyChar() == 'b')
    {
      gridPanel.getRowsColumns().decrementBottomRows(true);
    }
    
    if(event.getKeyChar() == 'L')
    {
      gridPanel.getRowsColumns().incrementLeftColumns(true, 1);
    }
    
    if(event.getKeyChar() == 'l')
    {
      gridPanel.getRowsColumns().decrementLeftColumns(true);
    }
    
    if(event.getKeyChar() == '1')
    {
      Thread testThread = new Thread(new gridThread(gridPanel, scrollPanel, 1));
      testThread.start();
    }
    
    if(event.getKeyChar() == 'g')
    {
      staticData.toggleGrid();
      gridPanel.repaint();
    }
      
    if(event.getKeyChar() == '2')
    {
      Thread testThread = new Thread(new gridThread(gridPanel, scrollPanel, 2));
      testThread.start();
    }
    
    if(event.getKeyChar() == '3')
    {
      Thread testThread = new Thread(new gridThread(gridPanel, scrollPanel, 3));
      testThread.start();
    }
    
    if(event.getKeyChar() == '4')
    {
      gridPanel.switchLayer();
      
      if(switchButton.getText().equals("Foreground"))
      {
        switchButton.setText("Background");
      }
      else
      {
        switchButton.setText("Foreground");
      }
    }
    
    if(event.getKeyChar() == '5')
    {
      Thread testThread = new Thread(new gridThread(gridPanel, scrollPanel, 5));
      testThread.start();
    }
    
    if(event.getKeyChar() == '6')
    {
      Thread testThread = new Thread(new gridThread(gridPanel, scrollPanel, 6));
      testThread.start();
    }
    
    if(event.getKeyChar() == '7')
    {
      Thread testThread = new Thread(new gridThread(gridPanel, scrollPanel, 7));
      testThread.start();
    }
          
    if(event.getKeyChar() == '8')
    {
      staticData.buildBelow();
      
      if(belowButton.getText().equals("Above"))
      {
        belowButton.setText("Below");
      }
      else
      {
        belowButton.setText("Above");
      }
    }
    
    //used to make the scrollpane's scrollbar appear for the custom paintcomponent
    scrollPanel.setViewport(scrollPanel.getViewport());
    
    //refocus on gridPanel so hotkeys work
    gridPanel.requestFocus();
  }
  
  @Override
  public void keyPressed(KeyEvent event)
  {
    //reset brush size, set control flag, disable wheel scrolling
    if(event.getKeyCode() == KeyEvent.VK_CONTROL)
    {
      staticData.setIsControl(true);
      staticData.setBrushWidth(1);
      staticData.setBrushHeight(1);
      scrollPanel.setWheelScrollingEnabled(false);
      gridPanel.repaint();
    }
    
    //toggle shift flag and reset brush size
    if(event.getKeyCode() == KeyEvent.VK_SHIFT)
    {
      if(!staticData.getIsShift())
      {
        staticData.setIsShift(true);
        staticData.setBrushWidth(1);
        staticData.setBrushHeight(1);
        gridPanel.repaint();
      }
    }
  }
  
  @Override
  public void keyReleased(KeyEvent event)
  {
    //if the arrow keys were pressed, mirror some direction
    if(event.getKeyCode() == KeyEvent.VK_UP)
    {
      staticData.mirrorUp(gridPanel);
      gridPanel.repaint();
    }
    
    if(event.getKeyCode() == KeyEvent.VK_DOWN)
    {
      staticData.mirrorDown(gridPanel);
      gridPanel.repaint();
    }
    
    if(event.getKeyCode() == KeyEvent.VK_LEFT)
    {
      staticData.mirrorLeft(gridPanel);
      gridPanel.repaint();
    }
    
    if(event.getKeyCode() == KeyEvent.VK_RIGHT)
    {
      staticData.mirrorRight(gridPanel);
      gridPanel.repaint();
    }
    
    //set control flag, enable wheel scrolling
    if(event.getKeyCode() == KeyEvent.VK_CONTROL)
    {
      staticData.setIsControl(false);
      scrollPanel.setWheelScrollingEnabled(true);
    }
    
    //update the brush size and toggle shift flag
    if(event.getKeyCode() == KeyEvent.VK_SHIFT)
    {
      int newBrushWidth = Math.abs(staticData.getStartShift().x - staticData.getEndShift().x) + 1;
      int newBrushHeight = Math.abs(staticData.getStartShift().y - staticData.getEndShift().y) + 1;
      
      staticData.setIsShift(false);
      staticData.setBrushWidth(newBrushWidth);
      staticData.setBrushHeight(newBrushHeight);
      gridPanel.repaint();
    }
  }
}