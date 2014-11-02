import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.DirectoryStream;


public class tilesets
{
  //variables to keep track of the time threads take to finish
  private long start = 0;
  private long end = 0;
  
  //the gridPanel, to repaint and get the text to show up
  private gridCanvas gridPanel;
  
  
  public tilesets(gridCanvas gridPanel)
  {
    this.gridPanel = gridPanel;
  }
  
  //import a tileset from packages/tilesets/
  public void importTileset()
  {
    //the name of the tileset to import
    String name = staticData.showAvailableStructures(0, "Tilesets", "Select the tileset to import");
    
    //if they x'd out
    if(name.equals(""))
    {
      return;
    }
    
    //output that it's starting
    j.out("Importing...");
    j.setDrawString("Importing...");
    gridPanel.repaint();
    
    //start of operation
    start = System.nanoTime();
    
    //the location of the object icons
    Path source = Paths.get("packages", "tilesets", name, name);
    
    try
    (
      //try to get the directory stream of the object icons
      DirectoryStream<Path> objectIcons = Files.newDirectoryStream(source);
    )
    {
      //if there are object icons
      if(Files.exists(source))
      {
        //the path to the new directory
        Path newDir = Paths.get("images", "buttons", "objects", name);
        
        //make the new directory for the objects
        try
        {
          Files.createDirectory(newDir);
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to create directory " + newDir.toString(), IOe));
        }
        
        try
        {
          //move them all to the images directory
          for(Path image : objectIcons)
          {
            Files.copy(image, Paths.get(newDir.toString(), image.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
          }
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to copy file to " + newDir.toString(), IOe));
        }
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to get the directory stream for " + source.toString(), IOe));
    }
    
    //the location of the object full images
    source = Paths.get("packages", "tilesets", name, name + "FullObjects");
    
    try
    (
      //the directory stream of the object full images
      DirectoryStream<Path> objectFullImages = Files.newDirectoryStream(source);
    )
    {
      //if there are object full images
      if(Files.exists(source))
      {
        //the path to the new directory
        Path newDir = Paths.get("images", "buttons", "objects", name + "FullObjects");
        
        //make the new directory for the objects
        try
        {
          Files.createDirectory(newDir);
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to create directory " + newDir.toString(), IOe));
        }
        
        try
        {
          //move them all to the images directory
          for(Path image : objectFullImages)
          {
            Files.copy(image, Paths.get(newDir.toString(), image.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
          }
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to copy file to " + newDir.toString(), IOe));
        }
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to get stream for directory " + source.toString(), IOe));
    }
    
    //the location of the objects
    source = Paths.get("packages", "tilesets", name, "tiles");
    
    try
    (
      //get the directory stream for the tiles
      DirectoryStream<Path> tiles = Files.newDirectoryStream(source);
    )
    {
      //if there are objects
      if(Files.exists(source))
      {
        //the location of the new directory
        Path newDir = Paths.get("images", "buttons", "tiles", name);
        
        //make the new directory for the objects
        try
        {
          Files.createDirectory(newDir);
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to create directory " + newDir.toString(), IOe));
        }
        
        try
        {
          //move them all to the images directory
          for(Path image : tiles)
          {
            Files.copy(image, Paths.get(newDir.toString(), image.getFileName().toString()), StandardCopyOption.REPLACE_EXISTING);
          }
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to copy file to " + newDir.toString(), IOe));
        }
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to get directory stream for " + source.toString(), IOe));
    }
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Importing ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
    
    //clear the string showing the status
    j.setDrawString("");
    gridPanel.repaint();
  }
  
  //remove a tileset from images/buttons/tiles/ and images/buttons/objects/
  public void removeTileset()
  {
    //the name of the tileset to import
    String name = staticData.showAvailableStructures(0, "Tilesets", "Select the tileset to remove");
    
    //if they x'd out
    if(name.equals(""))
    {
      return;
    }
    
    //output that it's starting
    j.out("Removing...");
    j.setDrawString("Removing...");
    gridPanel.repaint();
    
    //start of operation
    start = System.nanoTime();
    
    //the path to the object icons to delete
    Path deletePath = Paths.get("images", "buttons", "objects", name);
    
    try
    (
      //the directory to clear and then delete object icons from
      DirectoryStream<Path> deleteDir = Files.newDirectoryStream(deletePath);
    )
    {
      //if it exists
      if(Files.exists(deletePath))
      {
        //delete the images
        try
        {
          for(Path image : deleteDir)
          {
            Files.delete(image);
          }
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to delete image in " + deletePath.toString(), IOe));
        }
        
        //delete the directory
        try
        {
          Files.delete(deletePath);
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to delete directory " + deletePath.toString(), IOe));
        }
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Fails to create directory stream for " + deletePath.toString(), IOe));
    }
    
    //the directory to clear and then delete object from
    deletePath = Paths.get("images", "buttons", "objects", name + "FullObjects");
    
    try
    (
      //the path to the object to delete
      DirectoryStream<Path> deleteDir = Files.newDirectoryStream(deletePath);
    )
    {
      //if it exists
      if(Files.exists(deletePath))
      {
        //delete the images
        try
        {
          for(Path image : deleteDir)
          {
            Files.delete(image);
          }
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to delete image in " + deletePath.toString(), IOe));
        }
        
        //delete the directory
        try
        {
          Files.delete(deletePath);
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to delete directory " + deletePath.toString(), IOe));
        }
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to create directory stream for " + deletePath.toString(), IOe));
    }
    
    //directory to delete the tiles from
    deletePath = Paths.get("images", "buttons", "tiles", name);
    
    
    try
    (
      //the path to the object to delete
      DirectoryStream<Path> deleteDir = Files.newDirectoryStream(deletePath);
    )
    {
      //if the directory exists
      if(Files.exists(deletePath))
      {
        try
        {
          //delete the images
          for(Path image : deleteDir)
          {
            Files.delete(image);
          }
        }
        catch(IOException IOe)
        {
          j.err(new Exception("Failed to delete image in " + deletePath.toString(), IOe));
        }
      }
      
      //delete the directory
      try
      {
        Files.delete(deletePath);
      }
      catch(IOException IOe)
      {
        j.err(new Exception("Failed to delete directory " + deletePath.toString(), IOe));
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to create directory stream for  " + deletePath.toString(), IOe));
    }
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Removing ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
    
    //clear the string showing the status
    j.setDrawString("");
    gridPanel.repaint();
  }
}