//loads a structure from an image
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class imageStructure
{
  //contains the mappings from integer color codes to tiles
  private biMap<Integer, String> colorMappings;
  
  //contains the mappings from images to the ids used in the grid
  private biMap<Integer, String> tileIcons;
  
  //the number of colors in a png color layer
  private int colorRange = 16777216;
  
  //the gap between color mappings
  private int mappingGap = 1000;
  
  //to access the gridCanvas' methods
  private gridCanvas gridPanel;
  
  //the timing variables
  long start = 0;
  long end = 0;
  
  public imageStructure(gridCanvas gridPanel, biMap<Integer, String> tileIcons)
  {
    this.tileIcons = tileIcons;
    this.gridPanel = gridPanel;
    
    //load the mappings
    loadMappings();
  }
  
  //creates and saves, or loads the color mappings
  public void loadMappings()
  {
    //the hashmap linking the color codes to the tile names
    colorMappings = new biMap<Integer, String>();
    
    //if the mappings file exists, get the data from there, otherwise generate the default mappings
    if(Files.exists(Paths.get("configuration", "mappings.txt")))
    {
      //try to read the mappings file
      try
      (
        BufferedReader mappingsReader = new BufferedReader(new FileReader(staticData.buildPath(new String[]{"configuration", "mappings.txt"})))
      )
      {
        //the current line
        String line = mappingsReader.readLine();
        
        //while the line is not null, there's a mapping to read
        while(line != null)
        {
          //split the line into the color code:tile name
          String[] splitLine = line.split(":");
          
          //split the line into the id and tile name
          splitLine = line.split(":");
          
          //add the new mapping if tileIcons contains the tile
          if(tileIcons.getReverse(splitLine[1]) != null)
          {
            colorMappings.put(Integer.parseInt(splitLine[0]), splitLine[1]);
          }
          else
          {
            //put in debug, since it may not be significant
            j.debug("The tile " + splitLine[1] + " does not exist in tileIcons.");
          }
          
          //get the next line
          line = mappingsReader.readLine();
        }
      }
      catch(IOException IOe)
      {
        j.err(new Exception("Failed to read mappings file", IOe));
      }
    }
    else
    {
      //assign the color codes to the tiles
      for(int i = 0; i < tileIcons.size(); i++)
      {
        //check to make sure the range of possible colors hasn't been exceeded
        if(i*mappingGap < colorRange)
        {
          colorMappings.put(i*mappingGap, tileIcons.getNormal(i));
        }
        else
        {
          j.err(new Exception("Exceeded maximum number of color mappings"));
          return;
        }
      }
      
      //write them to the file
      try
      (
        FileWriter mappingsWriter = new FileWriter(staticData.buildPath(new String[]{"configuration", "mappings.txt"}), true)
      )
      {
        for(int i = 0; i < colorMappings.size(); i++)
        {
          mappingsWriter.write(Integer.toString(i*mappingGap) + ":" + colorMappings.getNormal(i*mappingGap) + "\n");
        }
      }
      catch(IOException IOe)
      {
        j.err(new Exception("Failed to write the color mappings", IOe));
      }
    }
  }
  
  //update the mappings, dont' over-write any existing ones
  public void updateMappings()
  {
    //check that there are enough colors
    if(tileIcons.size() > colorRange/mappingGap)
    {
      j.err(new Exception("There are too many tiles to map!"));
      return;
    }
    
    //try to read the mappings file
    try
    (
      FileWriter mappingsWriter = new FileWriter(staticData.buildPath(new String[]{"configuration", "mappings.txt"}), true)
    )
    {
      //look at everything in the tileIcons map
      for(int i = 0; i < tileIcons.size(); i++)
      {
        //if it isn't contained in colorMappings, put it there and update the mappings file
        if(colorMappings.getReverse(tileIcons.getNormal(i)) == null)
        {
          //loop through the entire color range
          for(int k = 0; k < colorRange; k += 1000)
          {
            //find the first empty color code and insert the tile there
            if(colorMappings.getNormal(k) == null)
            {
              colorMappings.put(k, tileIcons.getNormal(i));
              mappingsWriter.write(Integer.toString(k) + ":" + tileIcons.getNormal(i) + "\n");
              
              //break from the loop so it doesn't insert the tile into every available color code
              break;
            }
          }
        }
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Could not update the color mappings file", IOe));
    }
  }
  
  //reset the mappings from scratch
  //will likely cause incompatibility when mappings change
  public void resetMappings()
  {
    //delete the old mappings file
    try
    {
      if(Files.exists(Paths.get("configuration", "mappings.txt")))
      {
        Files.delete(Paths.get("configuration", "mappings.txt"));
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to delete mappings file", IOe));
    }
    
    //create a new one
    loadMappings();
  }
  
  //returns the int[][] array of the color codes for a layer
  public void loadImageStructure(String name)
  {
    //output that it's starting
    j.out("Loading Image...");
    
    //start of operation
    start = System.nanoTime();
    
    //try to create the images
    try
    {
      //array for the images, to make up with the layer counter
      BufferedImage[] image = 
      {
        ImageIO.read(Paths.get("images", "structures", name + "Foreground.png").toFile()),
        ImageIO.read(Paths.get("images", "structures", name + "Background.png").toFile())
      };
      
      //get the image's height and width
      int height = image[0].getHeight();
      int width = image[0].getWidth();
      
      //set the grid size
      staticData.setPicLocations(new int[2][width][height]);
      staticData.setNumColumns(width);
      staticData.setNumRows(height);
      
      //set the whole grid to -1 so it doesn't think there's a ton of id=0 cells
      for(int index = 0; index != 2; index++)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int k = 0; k < staticData.getNumRows(); k++)
          {
            staticData.setPicLocations(index, i, k, staticData.emptyCell);
          }
        }
      }
      
      //loop over the two layers
      for(int i = 0; i != 2; i++)
      {
        //the row/column to store the pixel at
        int x = 0;
        int y = 0;
        
        //get the data for the current layer's image
        byte[] byteImage = ((DataBufferByte) image[i].getRaster().getDataBuffer()).getData();
        
        //output an exception if the alpha channel is missing
        if(image[i].getAlphaRaster() == null)
        {
          j.err(new Exception("Image " + name + " does not support transparency"));
          return;
        }
      
        //loop over the byte array to convert the rgb into a single int
        for(int k = 0; k < byteImage.length; k += 4)
        {
          //calculate the pixel as an int
          int pixel = 0;
          pixel += (((int)byteImage[k+1]) & 0xFF);
          pixel += (((int)byteImage[k+2]) & 0xFF) << 8;
          pixel += (((int)byteImage[k+3]) & 0xFF) << 16;
          
          //convert the pixel int into a tile id
          pixel = tileIcons.getReverse(colorMappings.getNormal(pixel));
          
          //fully opaque is an alpha value of 255, which in a signed byte is -1
          //so anything that is not -1 has some transparency
          //transparency is used as the flag for an empty cell
          //so if it's not an empty cell, set the tile/object
          if(byteImage[k] == -1)
          {
            staticData.setPicLocations(i, x, y, pixel);
          }
          
          //update the column
          x++;
          
          //update the row if it's the last column
          if(x == width)
          {
            x = 0;
            y++;
          }
        }
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to read image " + name, IOe));
    }
    catch(NullPointerException NPe)
    {
      j.err(new Exception("Structure " + name + " tried to load an non-existent tile", NPe));
      return;
    }
    
    //set the current name
    staticData.setCurrentStructure(name);
    
    //calls to reset other things off the grid
    staticData.setLayerIndex(0);
    staticData.setLastRow(0);
    staticData.setLastColumn(0);
    gridPanel.getUrdo().clearUndo();
    gridPanel.getUrdo().clearRedo();
    gridPanel.updateGridSize();
    gridPanel.updateRowsColumns();
    gridPanel.repaint();
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Image Load Ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
  }
  
  //save the current structure as an image
  public void saveImageStructure()
  {
    //the name of the structure to save
    String name = (String)JOptionPane.showInputDialog(null, "Enter the item's name:", "Save Image", JOptionPane.QUESTION_MESSAGE, null, null, staticData.getCurrentStructure());
    
    //if the user x'd out
    if(name == null)
    {
      return;
    }
    
    //if an empty name was entered
    if(name.equals(""))
    {
      JOptionPane.showMessageDialog(null, "No name was entered.");
      return;
    }
    
    //check if saving would overwrite anything
    if(Files.exists(Paths.get("images", "structures", name + "Foreground.png")))
    {
      if(JOptionPane.showConfirmDialog(null, "Structure already exists, overwrite?", "Save", JOptionPane.YES_NO_OPTION) != 0)
      {
        return;
      }
    }
    
    //output that it's starting
    j.out("Saving Image...");
    
    //start of operation
    start = System.nanoTime();
    
    //holds the background/foreground images to write to disk
    BufferedImage newStructure = new BufferedImage(staticData.getNumColumns(), staticData.getNumRows(), BufferedImage.TYPE_4BYTE_ABGR);
    
    //loop over the foreground
    for(int i = 0; i < staticData.getNumColumns(); i++)
    {
      for(int k = 0; k < staticData.getNumRows(); k++)
      {
        //the current cell
        int cell = staticData.getPicLocations(0, i, k);
        
        //if the cell is empty, use a default value, otherwise get it from the mappings
        if(cell == -1 || cell == -2)
        {
          newStructure.setRGB(i, k, colorRange);
        }
        else
        {
          //0xFF000000 necessary to set the alpha channel to fully non-transparent
          //the alpha channel is then used later, anything fully transparent is treated as an empty cell
          newStructure.setRGB(i, k, colorMappings.getReverse(tileIcons.getNormal(cell)) | 0xFF000000);
        }
      }
    }
    
    //try to write the foreground image
    try
    {
      ImageIO.write(newStructure, "png", Paths.get("images", "structures", name + "Foreground.png").toFile());
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to write " + name, IOe));
    }
    
    //reset the image to write the background
    newStructure = new BufferedImage(staticData.getNumColumns(), staticData.getNumRows(), BufferedImage.TYPE_4BYTE_ABGR);
    
    //loop over the backgroundImage
    for(int i = 0; i < staticData.getNumColumns(); i++)
    {
      for(int k = 0; k < staticData.getNumRows(); k++)
      {
        //the current cell
        int cell = staticData.getPicLocations(1, i, k);
        
        //if the cell is empty, use a default value, otherwise get it from the mappings
        if(cell == -1)
        {
          newStructure.setRGB(i, k, colorRange);
        }
        else
        {
          newStructure.setRGB(i, k, colorMappings.getReverse(tileIcons.getNormal(cell)) | 0xFF000000);
        }
      }
    }
    
    //try to write the background image
    try
    {
      ImageIO.write(newStructure, "png", Paths.get("images", "structures", name + "Background.png").toFile());
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to write " + name, IOe));
    }
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Image Save Ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
  }
  
  //delete an image structure
  public void deleteImageStructure(String name)
  {
    //output that it's starting
    j.out("Deleting Image...");
    
    //start of operation
    start = System.nanoTime();
    
    try
    {
      //delete the foreground image
      if(Files.exists(Paths.get("images", "structures", name + "Foreground.png")))
      {
        Files.delete(Paths.get("images", "structures", name + "Foreground.png"));
      }
      
      //delete the background image
      if(Files.exists(Paths.get("images", "structures", name + "Background.png")))
      {
        Files.delete(Paths.get("images", "structures", name + "Background.png"));
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to delete structure images", IOe));
    }
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Image Load Ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
  }
}