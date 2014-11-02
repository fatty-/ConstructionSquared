//extended JPanel to simplify placing the buttons on the JTabbedPane
//
//don't assign mouselistener in here, provide access method
//
//need a way to know where the previous buttonTab left off if one folder need multiple

import javax.swing.*;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.DirectoryStream;

public class buttonTab extends JPanel
{
  //number of buttons in a row and a column, public to allow access when figuring out the numButtons in programGUI
  public static final int width = 12;
  public static final int height = 12;
  
  //never used since this class is never serialized, but necessary to suppress a warning
  private static final long serialVersionUID = 789;
  
  //the buttons attached to the JPanel
  private JButton[] buttonArray;
  
  //labels to fill in the empty space and force the gridlayout to honor the width and height I give it
  private JLabel[] emptyLabels;
  
  //the path to the images this tab uses
  String path;
  
  public buttonTab(int startingImage, int numButtons, String path)
  {
    //set the layout
    this.setLayout(new GridLayout(width, height));
    
    //the path
    this.path = path;
    
    //try to get the stream
    //wrapped tons of stuff in the try block to use auto-resource management to avoid having to deal with close() and it throwing checked exceptions that I can't do anything about anyway
    //this assumes that the inner IOExceptions will get caught before the outer one
    try
    (
      //iterate over the image files
      DirectoryStream<Path> images = Files.newDirectoryStream(Paths.get(path));
    )
    {
      //increments the iterator to the starting point if this tab contains overflow from a previous tab, aka a tile/object folder had more than width*height images
      for(int i = 0; i < startingImage; i++)
      {
        j.out(images.iterator().next());
      }
      
      //the number of files in the directory
      int fileCount = 0;
      try
      {
        fileCount = (int)Files.list(Paths.get(path)).count();
      }
      catch(IOException IOe)
      {
        j.err(new Exception("Failed to get file count for " + path, IOe));
      }
      
      //if too many buttons were passed in
      if(numButtons > width*height || numButtons > fileCount)
      {
        j.err(new Exception("Too many buttons added to JPanel: " + Integer.toString(fileCount) + "\nfor directory " + path));
        return;
      }
      
      //declare the buttons/labels
      buttonArray = new JButton[numButtons];
      emptyLabels = new JLabel[(width*height)-numButtons];
      
      //keep track of where we are in the loop
      int i = 0;
      
      //create the buttons
      for(Path image : images)
      {
        //store the value here for easier typing
        String buttonImagePath = image.toString();
        
        //set the button's image
        buttonArray[i] = new JButton(new ImageIcon(buttonImagePath));
        
        //how to chop off the unnecessary text if it's a tile or object
        if(buttonImagePath.contains(File.separator + "tiles" + File.separator))
        {
          try
          {
            //set the tooltip text to not have the .png extension on the end, and add the button to the JPanel
            buttonArray[i].setToolTipText(image.getFileName().toString().substring(0, image.getFileName().toString().indexOf(".png")));
          }
          catch(StringIndexOutOfBoundsException SIOOBe)
          {
            j.err(new Exception("Bad image name for: " + buttonImagePath, SIOOBe));
          }
          
          this.add(buttonArray[i]);
        }
        else if(buttonImagePath.contains(File.separator + "objects" + File.separator))
        {
          try
          {
            //set the tooltip text to not have the icon.png text on the end, and add the button to the JPanel
            buttonArray[i].setToolTipText(image.getFileName().toString().substring(0, image.getFileName().toString().indexOf("icon.png")));
          }
          catch(StringIndexOutOfBoundsException SIOOBe)
          {
            j.err(new Exception("Bad image name for: " + buttonImagePath, SIOOBe));
          }
          
          this.add(buttonArray[i]);
        }
        else
        {
          //if there's a folder in the wrong place, since it can't be determined to be a tile or an object from it's path
          j.err(new Exception("Invalid path to image: " + buttonImagePath));
        }
        
        //increment the counter
        i++;
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to get directory stream for " + path, IOe));
    }
    catch(NullPointerException NPe)
    {
      j.err(new Exception("Directory stream is null, likely the folder does not exist at " + path, NPe));
    }
    
    //create the labels
    for(int i = numButtons; i < width*height; i++)
    {
        emptyLabels[i-numButtons] = new JLabel();
        this.add(emptyLabels[i-numButtons]);
    }
  }
  
  //set the mouselisteners for the buttons to set the clicked tile
  public int setButtonListeners(int setId, gridCanvas gridPanel)
  {
    //add the listener to each button in this tab
    for(int i = 0; i < buttonArray.length; i++)
    {
      buttonArray[i].addMouseListener(new clickTile(buttonArray[i], setId, gridPanel));
      setId++;
    }
    
    //return the starting point for the next set of buttons
    return setId;
  }
  
  //returns the path where this tab's images are located
  public String getTabPath()
  {
    return path;
  }
  
  //remove buttons that don't match the filter
  public void filterButtons(String filter)
  {
    //loop over the buttons
    for(int i = 0; i < buttonArray.length; i++)
    {
      //if the button contains the filter text
      if(!buttonArray[i].getToolTipText().contains(filter))
      {
        buttonArray[i].setVisible(false);
      }
      else
      {
        buttonArray[i].setVisible(true);
      }
    }
  }
}