/*
Packages and imports structures to/from the zip archives
*/
import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

import java.nio.file.Paths;
import java.nio.file.Files;


//puts all necessary structure data into a zip archive
public class structureZip
{
  //the gridPanel, for access to the current structure aka what to package
  private gridCanvas gridPanel;
  
  //variables to keep track of the time threads take to finish
  private long start = 0;
  private long end = 0;
  
  public structureZip(gridCanvas gridPanel)
  {
    this.gridPanel = gridPanel;
  }
  
  //puts the current structure in a zip archive
  public void packageStructure()
  {
    //the item to package, do nothing if empty
    String itemName = staticData.getCurrentStructure();
    if(itemName.equals(""))
    {
      return;
    }
    
    //check if the grid is empty
    if(staticData.isGridEmpty(staticData.getGrid()))
    {
      return;
    }
    
    //output that it's starting
    j.out("Packaging...");
    j.setDrawString("Packaging...");
    gridPanel.repaint();
    
    //start of operation
    start = System.nanoTime();
    
    //if not, save it as the current structure before adding it to the package
    gridPanel.getDiskStructure().saveStructure(false);
    
    //try to copy the files
    try
    (
      ZipOutputStream writeZip = new ZipOutputStream(new FileOutputStream(staticData.buildPath(new String[]{"packages", "structures", itemName + ".zip"})))
    )
    {
      //zip the animation file
      addZipEntry(writeZip, staticData.buildPath(new String[]{"objects", "generic", "CS_" + itemName, "CS_" + itemName + ".animation"}));
      
      //zip the frames file
      addZipEntry(writeZip, staticData.buildPath(new String[]{"objects", "generic", "CS_" + itemName, "CS_" + itemName + ".frames"}));
      
      //zip the object file
      addZipEntry(writeZip, staticData.buildPath(new String[]{"objects", "generic", "CS_" + itemName, "CS_" + itemName + ".object"}));
      
      //zip the image file
      addZipEntry(writeZip, staticData.buildPath(new String[]{"objects", "generic", "CS_" + itemName, "CS_" + itemName + ".png"}));
      
      //zip the image icon
      addZipEntry(writeZip, staticData.buildPath(new String[]{"objects", "generic", "CS_" + itemName, "CS_" + itemName + "icon.png"}));
      
      //zip the main lua
      addZipEntry(writeZip, staticData.buildPath(new String[]{"scripts", "CS_" + itemName, "CS_" + itemName + ".lua"}));
      
      //zip the data lua file
      addZipEntry(writeZip, staticData.buildPath(new String[]{"scripts", "CS_" + itemName, "CS_" + itemName + "0.lua"}));
      
      //zip the object lua file
      addZipEntry(writeZip, staticData.buildPath(new String[]{"scripts", "CS_" + itemName, "CS_" + itemName + "1.lua"}));
      
      //zip the recipe file
      addZipEntry(writeZip, staticData.buildPath(new String[]{"recipes", "objects", "CS_" + itemName + ".recipe"}));
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to copy file", IOe));
      return;
    }
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Packaging ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
    
    //clear the string showing the status
    j.setDrawString("");
    gridPanel.repaint();
  }
  
  //import a structure from a zip archive
  public void importStructure()
  {
    //reset id
    staticData.setId(staticData.emptyCell);
    
    //get the name of the structure chosen for deletion
    String itemName = staticData.showAvailableStructures(0, "Import", "Select the structure to import");
    
    //if they x'ed out, stop
    if(itemName.equals(""))
    {
      return;
    }
    
    //add to the player.config
    ArrayList<String> configArray = null;
    
    try
    (
      BufferedReader readConfig = new BufferedReader(new FileReader(staticData.buildPath(new String[]{"..", "player.config"})))
    )
    {
      //reads player.config
      configArray = new ArrayList<String>();
      
      //boolean for if the item was found to already exist
      Boolean foundItem = false;
      
      //reads the file
      for(String configLine = readConfig.readLine(); configLine != null; configLine = readConfig.readLine())
      {
        //if the item name wasn't found, add to ArrayList
        if(configLine.contains("\"CS_" + itemName + "\""))
        {
          foundItem = true;
          if(JOptionPane.showConfirmDialog(null, "Structure already exists, overwrite?", "Overwrite", JOptionPane.YES_NO_OPTION) != 0)
          {
            return;
          }
        }
        
        //add the line
        configArray.add(configLine);
        
        //add the item if it wasn't found
        if(!foundItem && configLine.contains("\" }") && !configLine.contains("\" },"))
        {
          configArray.set(configArray.size()-1, configArray.get(configArray.size()-1).replace("}", "},"));
          configArray.add("      { \"item\" : \"CS_" + itemName + "\" }");
        }
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to open player.config", IOe));
      return;
    }
    
    //output that it's starting
    j.out("Importing...");
    j.setDrawString("Importing...");
    gridPanel.repaint();
    
    //start of operation
    start = System.nanoTime();
    
    //try to open the file for writing
    PrintWriter writeString = null;
    
    try
    {
      writeString = new PrintWriter("../player.config");
    }
    catch(FileNotFoundException FNFe)
    {
      j.err(new Exception("Failed to write the player.config", FNFe));
      return;
    }
      
    //write everything and close
    for(int i = 0; i < configArray.size(); i++)
    {
      writeString.println(configArray.get(i));
    }
    
    writeString.close();
    
    //try to read fom the zip archive
    try
    (
      ZipInputStream readZip = new ZipInputStream(new FileInputStream(staticData.buildPath(new String[]{"packages", "structures", itemName + ".zip"})))
    )
    {
      //the data read from the zip
      byte[] fileData = new byte[1];
      
      //the current file to read
      ZipEntry nextFile = readZip.getNextEntry();
      
      //while there's another file to read
      while(nextFile != null)
      {
        //get the current folder of the file to be written
        String currentFolder = nextFile.getName().substring(0, nextFile.getName().lastIndexOf(File.separator + "CS_"));
        
        //shift it into the correct directory and make any missing folders
        Files.createDirectories(Paths.get("..", currentFolder));
        
        //used to write the new file
        FileOutputStream writeFile = new FileOutputStream(staticData.buildPath(new String[]{"..", nextFile.getName()}));
        
        //read/write the data while there's still data left
        int dataRead = 0;
        while((dataRead = readZip.read(fileData)) > 0)
        {
          writeFile.write(fileData, 0, dataRead);
        }
        
        writeFile.close();
        
        //get the next file to read
        nextFile = readZip.getNextEntry();
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to read from zip file", IOe));
      return;
    }
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Import ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
    
    //clear the string showing the status
    j.setDrawString("");
    gridPanel.repaint();
  }
  
  //adds a file entry to the zip archive
  private void addZipEntry(ZipOutputStream writeZip, String source)
  {
    //try to add the entry
    //open an input stream to the normal file
    try
    (
      FileInputStream textFile = new FileInputStream(staticData.buildPath(new String[]{"..", source}))
    )
    {
      //create the entry
      ZipEntry zipFileEntry = new ZipEntry(source);
      
      //add it to the zip
      writeZip.putNextEntry(zipFileEntry);
      
      //number of bytes read
      int bytesRead = 0;
      
      //hole the data that's read
      byte[] theData = new byte[1];
      
      //read/write the file from the source into the zip
      while((bytesRead = textFile.read(theData)) > 0)
      {
        writeZip.write(theData, 0, bytesRead);
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Zip creation of " + source + " failed", IOe));
      return;
    }
  }
}