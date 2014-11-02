/*
Contains all the operations regarding disk access - load/save/delete
*/
import javax.swing.*;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.awt.image.BufferedImage;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.awt.Dimension;
import java.awt.Color;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.FileNotFoundException;
import static javax.swing.JOptionPane.showMessageDialog;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class objectDisk
{
  //the object with the grid's data
  private gridCanvas gridPanel;
  
  //JButtons whose text changes, may switch to JButton array if more end up needing to be passed in
  private JButton buildBelowButton;
  private JButton switchLayersButton;

  //JLabels displaying the number of rows/columns
  private JLabel[] infoLabels;

  //array of Strings of the image locations
  private biMap<Integer, String> tileIcons;

  //holds the list of where the buttons for JPanels begin/end
  private ArrayList<Integer> listOfSizes;

  //variables to keep track of the time threads take to finish
  private long start = 0;
  private long end = 0;
  
  @SuppressWarnings("unchecked")
  public objectDisk(gridCanvas gridPanel, Object[] argumentList)
  {
    this.gridPanel = gridPanel;
    
    //assign the parameter list
    //set the tileIcons array
    this.tileIcons = (biMap<Integer, String>)argumentList[0];

    //set the JLabels
    this.infoLabels = (JLabel[])argumentList[1];

    //set the ArrayList
    this.listOfSizes = (ArrayList<Integer>)argumentList[2];
    
    //set the buttons
    this.buildBelowButton = (JButton)argumentList[3];
    this.switchLayersButton = (JButton)argumentList[4];
  }

  //save the structure that was drawn into a String and write to disk
  public void saveStructure(Boolean askForName)
  {
    //the maximum height/width of the structure
    int highestY = 0;
    
    //used to make the structure always start being built from x=0 in the actual game
    int lowestX = 0;
    
    //loop from the top down until the is found
    int highestRealY = 0;
    
    //loop from the bottom up until tile is found
    int lowestRealY = 0;
    
    //loop from the left to the right until tile is found
    int highestRealX = 0;

    //loop from the right to the left until the tile is found
    int lowestRealX = 0;

    //will contain the structure
    String dataString = "";
    
    //will hold the data to place objects
    String objectString = "";
    
    //name of the structure
    String itemName = "";
    
    //get the grid for easier manipulation
    int[][][] savedGrid = staticData.getGrid();
    
    //name of the item
    if(askForName)
    {
      itemName = (String)JOptionPane.showInputDialog(null, "Enter the item's name:", "Save", JOptionPane.QUESTION_MESSAGE, null, null, staticData.getCurrentStructure());
    }
    else
    {
      itemName = "CS_" + staticData.getCurrentStructure();
    }
    
    //if they X'd out, don't save
    if(itemName == null)
    {
      return;
    }
    
    //if the itemName would overwrite the custom crafting station, don't save and warn the user
    if(itemName.equals("ConstructionTable"))
    {
      //alert the user not to use that name
      JOptionPane.showMessageDialog(null, "That name is reserved, do not use!");
      
      return;
    }
    
    //output that it's starting
    j.out("Saving...");
    j.setDrawString("Saving...");
    gridPanel.repaint();
    
    //start of operation
    start = System.nanoTime();
    
    //path to the file
    String fileLocation = null;
    
    //holds holds the player.config file
    ArrayList<String> configArray = new ArrayList<String>();
    
    //String to hold the contents of the file to be written out
    String fileString = "";
    
    //used to write to disk
    //tileWriter writes tiles, objectWriter writes objects, itemWriter writes the main/init file
    FileWriter tileWriter = null;
    FileWriter objectWriter = null;
    FileWriter itemWriter = null;
    PrintWriter writeString = null;
    BufferedReader readConfig = null;
    
    //if the structure is below ground, remove empty rows below the structure
    if(!staticData.getBelow())
    {
      //if all cells found so far are empty
      Boolean isEmpty = true;
      
      //loop over the grid, starting at the bottom and going up
      for(int k = staticData.getNumRows()-1; k > -1; k--)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          for(int index = 1; index != -1; index--)
          {
            //if an empty cell if found, set the flag to false
            if(savedGrid[index][i][k] != staticData.emptyCell)
            {
              isEmpty = false;
            }
          }
        }
        
        //if no cells were found in that row, remove it
        if(isEmpty)
        {
          gridPanel.getRowsColumns().decrementBottomRows(false);
        }
        else
        //otherwise, get the new grid and leave the loop
        {
          savedGrid = staticData.getGrid();
          break;
        }
      }
    }

    //find the lowest x coordinate, so empty cells can be cropped out of the grid
    //necessary to make the structure be built adjacent to the placed item ingame
    for(int i = 0; i < staticData.getNumColumns(); i++)
    {
      if
      (
        (
          savedGrid[0][i][staticData.getNumRows()-1] != staticData.emptyCell || 
          savedGrid[1][i][staticData.getNumRows()-1] != staticData.emptyCell
        ) 
        && 
        !staticData.getBelow()
      )
      {
        lowestX = i;
        break;
      }
    }
    
    //Add CS_ to the itemName if it's not there already
    if
    (
      itemName.indexOf("CS_") == -1 && 
      !itemName.equals("")
    )
    {
      itemName = "CS_" + itemName;
    }

    try
    {
      //set the new file's location
      //if itemName is an empty string, it's to be saved in item.lua
      if(!itemName.equals(""))
      {
        fileLocation = staticData.buildPath(new String[]{"..", "scripts", itemName});
      }
      else
      {
        fileLocation = "item";
      }
      
      //make any directories
      if(!fileLocation.equals("item"))
      {
        Files.createDirectories(Paths.get(fileLocation));
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to create the directory " + fileLocation + itemName, IOe));
    }
    
    //delete the old files to avoid concatenation if they exist
    if(Files.exists(Paths.get(fileLocation + itemName + "0.lua")))
    {
      try
      {
        Files.delete(Paths.get(fileLocation + itemName + "0.lua"));
      }
      catch(IOException IOe)
      {
        j.err(new Exception(itemName + "0.lua exists, failed to delete", IOe));
      }
    }
    
    if(Files.exists(Paths.get(fileLocation + itemName + "1.lua")))
    {
      try
      {
        Files.delete(Paths.get(fileLocation + itemName + "1.lua"));
      }
      catch(IOException IOe)
      {
        j.err(new Exception(itemName + "1.lua exists, failed to delete", IOe));
      }
    }
    
    try
    {
      //reopen them for writing
      tileWriter = new FileWriter(fileLocation + itemName + "0.lua", true);
      objectWriter = new FileWriter(fileLocation + itemName + "1.lua", true);
      
      //start write the beginning of the file
      tileWriter.write("function build()\n");
      tileWriter.write("  local dataString = \"");
      
      objectWriter.write("function place()\n");
      objectWriter.write("  local objectString = \"");
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to write 0.lua/1.lua for " + itemName, IOe));
    }
    //find the highest y value
    for(int index = 1; index != -1; index--)
    {
      for(int k = staticData.getNumRows()-1; k > -1; k--)
      {
        for(int i = 0; i < staticData.getNumColumns(); i++)
        {
          if
          (
            highestY < k && 
            savedGrid[index][i][k] != staticData.emptyCell && 
            staticData.getBelow()
          )
          {
            highestY = k;
          }
        }
      }
    }
    
    //offset to make the bottom line up properly
    if(staticData.getBelow())
    {
      highestY += 1;
    }
    
    //start concatenating everything onto the dataString
    //loop order was adjusted to make sure that when used in-game it would draw the background first and start at the lower-left corner
    //j uses Math.abs() in order to reverse the y-coordinate (start at 0 instead of y)
    try
    {
      for(int index = 1; index != -1; index--)
      {
        for(int k = staticData.getNumRows()-1; k > -1; k--)
        {
          for(int i = 0; i < staticData.getNumColumns(); i++)
          {
            if(savedGrid[index][i][k] != staticData.emptyCell)
            {
              int x = i-lowestX;
              int y = Math.abs(k-staticData.getNumRows()+1)-highestY;
              
              //skip unknown or occupied (-1) tiles
              if(tileIcons.getNormal(savedGrid[index][i][k]) == null)
              {
                if(savedGrid[index][i][k] != -2)
                {
                  j.out("Unknown tile id of " + savedGrid[index][i][k]);
                }
                continue;
              }
              
              //check if it's a tile or an object
              if(tileIcons.getNormal(savedGrid[index][i][k]).indexOf("icon.png") == -1)
              {
                tileWriter.write(Integer.toString(x) + ":" + Integer.toString(y) + ":" + staticData.getLayers(index) + ":" + tileIcons.getNormal(savedGrid[index][i][k]).substring(0, tileIcons.getNormal(savedGrid[index][i][k]).indexOf(".png")) + ":");
                
                if(highestRealX < x)
                {
                  highestRealX = x;
                }
                
                if(lowestRealX > x)
                {
                  lowestRealX = x;
                }
                  
                if(highestRealY < y)
                {
                  highestRealY = y;
                }
                  
                if(lowestRealY > y)
                {
                  lowestRealY = y;
                }
              }
              else
              {
                objectWriter.write(tileIcons.getNormal(savedGrid[index][i][k]).substring(0, tileIcons.getNormal(savedGrid[index][i][k]).indexOf("icon.png")) + ":" + Integer.toString(i-lowestX) + ":" + Integer.toString(Math.abs(k-staticData.getNumRows()+1)-highestY) + ":");
              }
            }
          }
        }
      }
      
      //write the end of the file
      tileWriter.write("\"\n");
      tileWriter.write("  return dataString\n");
      tileWriter.write("end");
        
      objectWriter.write("\"\n");
      objectWriter.write("  return objectString\n");
      objectWriter.write("end");
        
      tileWriter.close();
      objectWriter.close();
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to write item0/1.lua", IOe));
    }
    
    //try opening the file with main/init
    try
    {
      if(!itemName.equals(""))
      {
        //delete previous file, since file concatenation is enabled
        if(Files.exists(Paths.get(fileLocation + itemName + ".lua")))
        {
          Files.delete(Paths.get(fileLocation + itemName + ".lua"));
        }
        
        itemWriter = new FileWriter(fileLocation + itemName + ".lua", true);
      }
      else
      {
        //delete previous file, since file concatenation is enabled
        if(Files.exists(Paths.get("item.lua")))
        {
          Files.delete(Paths.get("item.lua"));
        }
      
        itemWriter = new FileWriter("item.lua", true);
      }
      
      //add the rest of the file
      //builds the string to write
      StringBuilder builder = new StringBuilder();
      
      builder.append("function init(args)\n");
      builder.append("  --variable to hold the tiles\n");
      builder.append("  self.creationTable = { \"pi\" }\n");
      builder.append("  --variable to hold the objects\n");
      builder.append("  self.objectTable = {  }\n");
      builder.append("  --controls object placement\n");
      builder.append("  self.objectLoop = 1\n");
      builder.append("  --variable to determine when to stop removing tiles\n");
      builder.append("  self.destructionLoop = 1\n");
      builder.append("  --variable to place the tiles\n");
      builder.append("  self.creationLoop = 1\n");
      builder.append("  --loop to control the reverse placement\n");
      builder.append("  self.placementLoop = 1\n");
      builder.append("  --table to hold all the tiles that failed to be placed\n");
      builder.append("  self.placementTable = {  }\n");
      builder.append("  --the x offset\n");
      builder.append("  self.xOffset = 2\n");
      builder.append("  --the lowest x coordinate where something will be placed\n");
      builder.append("  self.leftX = " + Integer.toString(lowestRealX) + "\n");
      builder.append("  --the lowest y coordinate where something will be placed\n");
      builder.append("  self.rightX = " + Integer.toString(highestRealX) + "\n");
      builder.append("  --the highest y coordinate where something will be placed\n");
      builder.append("  self.bottomY = " + Integer.toString(lowestRealY) + "\n");
      builder.append("  --the highest x coordinate where something will be placed\n");
      builder.append("  self.topY = " + Integer.toString(highestRealY) + "\n");
      builder.append("  --let people \"e\" it\n");
      builder.append("  entity.setInteractive(true)\n");
      builder.append("  --if it has been interacted with\n");
      builder.append("  self.isActive = false\n");
      builder.append("end\n");
      builder.append("\n");
      builder.append("function main()\n");
      builder.append("  --keeps the area around the structure loaded as long as someone remains on the planet\n");
      builder.append("  local bottom = entity.toAbsolutePosition({-2, -2})\n");
      builder.append("  local top = entity.toAbsolutePosition({2, 2})\n");
      builder.append("  world.loadRegion({bottom[1], bottom[2], top[1], top[2]})\n");
      builder.append("\n");
      builder.append("  --if the structure has been activated\n");
      builder.append("  if self.isActive == true then\n");
      builder.append("    --place a background tile to make sure everything else can be placed\n");
      builder.append("    world.placeMaterial(entity.toAbsolutePosition( {-1 + self.xOffset, -1} ), \"background\", \"dirt\")\n");
      builder.append("    --place everything\n");
      builder.append("    makeStructure()\n");
      builder.append("  else\n");
      builder.append("    --spawn projectiles to indicate structure bounds if inactive\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.bottomY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.leftX+1+self.xOffset+0.5, self.bottomY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.leftX+2+self.xOffset+0.5, self.bottomY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.bottomY+2-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.bottomY+3-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.rightX-1+self.xOffset-0.5, self.bottomY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.rightX+self.xOffset-0.5, self.bottomY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.bottomY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.bottomY+2-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.bottomY+3-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.rightX-1+self.xOffset-0.5, self.topY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.rightX+self.xOffset-0.5, self.topY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.topY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.topY-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.topY-1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.topY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.leftX+1+self.xOffset+0.5, self.topY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.leftX+2+self.xOffset+0.5, self.topY+1-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.topY-0.5 }))\n");
      builder.append("    world.spawnProjectile(\"corner\", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.topY-1-0.5 }))\n");
      builder.append("  end\n");
      builder.append("end\n");
      builder.append("\n");
      builder.append("function onInteraction(args)\n");
      builder.append("  --invert the state of activity\n");
      builder.append("  self.isActive = not self.isActive\n");
      builder.append("end");
      
      //writes the string to the file
      itemWriter.write(builder.toString());
      
      itemWriter.close();
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed opening itemName.lua", IOe));
      return;
    }
    
    //if it's item.lua, quit here
    if(itemName.equals(""))
    {
      //end of operation
      end = System.nanoTime();
      
      //output the time it took
      j.out("Save Ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
      
      //clear the string showing the status
      j.setDrawString("");
      return;
    }
    
    //if the itemname was NOT empty
    //add to the player.config
    try
    {
      //reads player.config
      readConfig = new BufferedReader(new FileReader(staticData.buildPath(new String[]{"..", "player.config"})));
      configArray = new ArrayList<String>();
      
      //boolean for if the item was found to already exist
      Boolean foundItem = false;
      
      //reads the file
      for(String configLine = readConfig.readLine(); configLine != null; configLine = readConfig.readLine())
      {
        //if the item name wasn't found, add to ArrayList
        if(configLine.contains("\"" + itemName + "\""))
        {
          foundItem = true;
          
          //confirm overwriting the existing grid if this is a manual save
          if(askForName)
          {
            if(JOptionPane.showConfirmDialog(null, "Structure already exists, overwrite?", "Save", JOptionPane.YES_NO_OPTION) != 0)
            {
              readConfig.close();
              return;
            }
          }
        }
        
        //add the line
        configArray.add(configLine);
        
        //add the item if it wasn't found
        if
        (
          !foundItem && configLine.contains("\" }") && 
          !configLine.contains("\" },")
        )
        {
          configArray.set(configArray.size()-1, configArray.get(configArray.size()-1).replace("}", "},"));
          configArray.add("      { \"item\" : \"" + itemName + "\" }");
        }
      }
      
      readConfig.close();
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to open player.config", IOe));
      return;
    }
    
    //try to open the file for writing
    try
    {
      writeString = new PrintWriter(staticData.buildPath(new String[]{"..", "player.config"}));
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
    
    //try opening the recipe file
    try
    {
      fileLocation = staticData.buildPath(new String[]{"..", "recipes", "objects"});
      writeString = new PrintWriter(fileLocation + itemName + ".recipe");
    }
    catch(FileNotFoundException FNFe)
    {
      j.err(new Exception("Failed to open the recipe", FNFe));
      return;
    }
    
    //add the rest of the file
    fileString = new StringBuilder()
      .append("{\n")
      .append("  \"input\" : [\n")
      .append("    { \"item\" : \"money\", \"count\" : 1 }\n")
      .append("  ],\n")
      .append("  \"output\" : {\n")
      .append("    \"item\" : \"" + itemName + "\",\n")
      .append("    \"count\" : 1\n")
      .append("  },\n")
      .append("  \"groups\" : [ \"ConstructionTable\", \"mod\" ]\n")
      .append("}\n")
      .toString();
    
    writeString.print(fileString);
    writeString.close();
    
    //try opening the object file
    try
    {
      fileLocation = staticData.buildPath(new String[]{"..", "objects", "generic", itemName});
      
      try
      {
        Files.createDirectories(Paths.get(fileLocation));
      }
      catch(IOException IOe)
      {
        j.err(new Exception("Failed to create directories for " + fileLocation, IOe));
      }
      
      
      writeString = new PrintWriter(fileLocation + itemName + ".object");
    }
    catch(FileNotFoundException FNFe)
    {
      j.err(new Exception("Failed to open the object file", FNFe));
      return;
    }
    
    //add the rest of the file
    fileString = new StringBuilder()
      .append("{\n")
      .append("  \"objectName\" : \"" + itemName + "\",\n")
      .append("  \"rarity\" : \"Common\",\n")
      .append("  \"description\" : \"Builds something.\",\n")
      .append("  \"shortdescription\" : \"Builds " + itemName.substring(3) + ".\",\n")
      .append("  \"race\" : \"generic\",\n")
      .append("  \"category\" : \"wire\",\n")
      .append("  \"objectType\" : \"wire\",\n")
      .append("  \"printable\" : false,\n")
      .append("  \"price\" : 1,\n")
      .append("\n")
      .append("  \"inventoryIcon\" : \"/objects/generic/" + itemName + "/" + itemName + "Icon.png\",\n")
      .append("  \"orientations\" : [\n")
      .append("    {\n")
      .append("      \"image\" : \"/objects/generic/" + itemName + "/" + itemName + ".png\",\n")
      .append("      \"imagePosition\" : [-16, 0],\n")
      .append("      \"frames\" : 1,\n")
      .append("      \"animationCycle\" : 1,\n")
      .append("\n")
      .append("      \"spaceScan\" : 0.1,\n")
      .append("      \"anchors\" : [ \"bottom\" ],\n")
      .append("      \"collision\" : \"platform\"\n")
      .append("    }\n")
      .append("  ],\n")
      .append("\n")
      .append("  \"animation\" : \"/objects/generic/" + itemName + "/" + itemName + ".animation\",\n")
      .append("  \"animationParts\" : {\n")
      .append("    \"beacon\" : \"/objects/generic/" + itemName + "/" + itemName + ".png\"\n")
      .append("  },\n")
      .append("  \"animationPosition\" : [-16, 0],\n")
      .append("\n")
      .append("  \"scripts\" : \n")
      .append("  [ \n")
      .append("    \"/scripts/reusableBits.lua\",\n")
      .append("    \"/scripts/" + itemName + "/" + itemName + ".lua\", \n")
      .append("    \"/scripts/" + itemName + "/" + itemName + "0.lua\", \n")
      .append("    \"/scripts/" + itemName + "/" + itemName + "1.lua\"\n")
      .append("  ],\n")
      .append("  \"scriptDelta\" : 1\n")
      .append("}")
      .toString();
    
    writeString.print(fileString);
    writeString.close();

    //try opening the frames file
    try
    {
      fileLocation = staticData.buildPath(new String[]{"..", "objects", "generic", itemName});
      writeString = new PrintWriter(fileLocation + itemName + ".frames");
    }
    catch(FileNotFoundException FNFe)
    {
      j.err(new Exception("Failed to open the frames file", FNFe));
      return;
    }
    
    //add the rest of the file
    fileString = new StringBuilder()
      .append("{\n")
      .append("  \"frameGrid\" : {\n")
      .append("    \"size\" : [32, 32],\n")
      .append("    \"dimensions\" : [1, 1],\n")
      .append("    \"names\" : [\n")
      .append("      [ \"default\" ]\n")
      .append("    ]\n")
      .append("  }\n")
      .append("}")
      .toString();
    
    writeString.print(fileString);
    writeString.close();

    //try opening the animations file
    try
    {
      fileLocation = staticData.buildPath(new String[]{"..", "objects", "generic", itemName});
      writeString = new PrintWriter(fileLocation + itemName + ".animation");
    }
    catch(FileNotFoundException FNFe)
    {
      j.err(new Exception("Failed to open the animations file", FNFe));
      return;
    }
    
    //add the rest of the file
    fileString = new StringBuilder()
      .append("{\n")
      .append("  \"animatedParts\" : {\n")
      .append("    \"stateTypes\" : {\n")
      .append("      \"beaconState\" : {\n")
      .append("        \"default\" : \"idle\",\n")
      .append("        \"states\" : {\n")
      .append("          \"idle\" : {\n")
      .append("            \"frames\" : 1,\n")
      .append("            \"cycle\" : 0.15\n")
      .append("          },\n")
      .append("          \"active\" : {\n")
      .append("            \"frames\" : 1,\n")
      .append("            \"cycle\" : 0.7,\n")
      .append("            \"mode\" : \"loop\"\n")
      .append("          }\n")
      .append("        }\n")
      .append("      }\n")
      .append("    },\n")
      .append("\n")
      .append("    \"parts\" : {\n")
      .append("      \"beacon\" : {\n")
      .append("        \"properties\" : {\n")
      .append("          \"centered\" : false\n")
      .append("        },\n")
      .append("\n")
      .append("        \"partStates\" : {\n")
      .append("          \"beaconState\" : {\n")
      .append("            \"idle\" : {\n")
      .append("              \"properties\" : {\n")
      .append("                \"image\" : \"" + itemName + ".png\"\n")
      .append("              }\n")
      .append("            },\n")
      .append("\n")
      .append("            \"active\" : {\n")
      .append("              \"properties\" : {\n")
      .append("                \"image\" : \"" + itemName + ".png\"\n")
      .append("              }\n")
      .append("            }\n")
      .append("          }\n")
      .append("        }\n")
      .append("      }\n")
      .append("    }\n")
      .append("  }\n")
      .append("}")
      .toString();
    
    writeString.print(fileString);
    writeString.close();

    //try to copy images over
    BufferedImage itemImage = null;
    
    //try to copy the icon image files
    try
    {
      Files.copy(Paths.get("..", "objects", "generic", "Default.png"), Paths.get("..", "objects", "generic", itemName, itemName + ".png"), StandardCopyOption.REPLACE_EXISTING);
      
      Files.copy(Paths.get("..", "objects", "generic", "DefaultIcon.png"), Paths.get("..", "objects", "generic", itemName, itemName + "Icon.png"), StandardCopyOption.REPLACE_EXISTING);
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to copy image files", IOe));
      return;
    }

    //set the current structure and jframe title
    staticData.setCurrentStructure(itemName.substring(3));
    gridPanel.setGridTitle();
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Save Ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
    
    //clear the string showing the status
    j.setDrawString("");
  }
  
  //load an exists structure
  public void loadStructure()
  {
    //reset id
    staticData.setId(staticData.emptyCell);
    
    //confirm overwriting the existing grid
    if(JOptionPane.showConfirmDialog(null, "Load Structure? Current data will be lost.", "Load", JOptionPane.YES_NO_OPTION) != 0)
    {
      return;
    }
    
    //get the name of the structure chosen for deletion
    String itemName = staticData.showAvailableStructures(1, "Load", "Select the structure to load");
    
    //used to shorten typing out the base path to the structure every time
    String itemPath = "";
    
    //if they x'ed out, stop.  else concatenate the CS_ prefix to the front
    if(itemName.equals(""))
    {
      return;
    }
    else if(!itemName.equals("item.lua"))
    {
      itemName = "CS_" + itemName;
    }
    
    //output that it's starting
    j.out("Loading...");
    j.setDrawString("Loading...");
    gridPanel.repaint();
    
    //start of operation
    start = System.nanoTime();

    //if it's not empty, set the path to the lua file with the data, otherwise set the path to item.lua
    //also set the name of the current structure
    if(!itemName.equals("item.lua"))
    {
      itemPath = staticData.buildPath(new String[]{"..", "scripts", itemName, itemName + "0.lua"});
      staticData.setCurrentStructure(itemName.substring(3));
      
      //put the structure's name in the application's title
      gridPanel.setGridTitle();
    }
    else
    {
      itemPath = "item0.lua";
      staticData.setCurrentStructure("");
      
      //put the structure's name in the application's title
      gridPanel.setGridTitle();
    }
    
    //holds the item data
    ArrayList<String> itemArray = null;
    //highest/lowest coordinates to size the new grid and offset the x-coordinates back to 0
    int highestX = 0;
    int lowestX = 0;
    int highestY = 0;
    int lowestY = 0;
    //used to hold the y coordinates, now that the value is conditional on if below is true or false
    int yCoord = 0;
    int xCoord = 0;
    //determines foreground or background tile placement in the array
    int layer = 0;
    //holds the tile index
    int tile = staticData.emptyCell;
    //reset the build direction
    staticData.setBelow(false);
    

    //try to read and split the item data
    try(BufferedReader itemReader = new BufferedReader(new FileReader(itemPath));)
    {
      //skip first line
      itemReader.readLine();
      
      //split the second line by quotes
      itemArray = new ArrayList<String>(Arrays.asList(itemReader.readLine().split("\"")));
      
      //split the second piece by colons
      itemArray = new ArrayList<String>(Arrays.asList(itemArray.get(1).split(":")));
    }
    catch(FileNotFoundException FNFe)
    {
      j.err(new Exception("Item name not found.", FNFe));
      return;
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Some other read error.", IOe));
      return;
    }
    catch(ArrayIndexOutOfBoundsException AIOOBe)
    {
      j.err(new Exception("The item contains no tiles.", AIOOBe));
      return;
    }
    catch(IndexOutOfBoundsException IOOBe)
    {
      j.err(new Exception("The item contains no tiles.", IOOBe));
      return;
    }
    catch(NullPointerException NPe)
    {
      j.err(new Exception("item.lua is completely empty", NPe));
      return;
    }

    //adjust path if item.lua or not
    if(!itemName.equals("item.lua"))
    {
      itemPath = staticData.buildPath(new String[]{"..", "scripts", itemName, itemName + ".lua"});
    }
    else
    {
      itemPath = "item.lua";
    }
    
    //adjust path if item.lua or not
    if(!itemName.equals("item.lua"))
    {
      itemPath = staticData.buildPath(new String[]{"..", "scripts", itemName, itemName + "1.lua"});
    }
    else
    {
      itemPath = "item1.lua";
    }
    
    //try to open the file with the object data
    try(BufferedReader itemReader = new BufferedReader(new FileReader(itemPath));)
    {
      //skip first line
      itemReader.readLine();
      
      //split the second line by quotes
      ArrayList<String> tempArray = new ArrayList<String>(Arrays.asList(itemReader.readLine().split("\"")));
      
      //split the second piece by colons
      tempArray = new ArrayList<String>(Arrays.asList(tempArray.get(1).split(":")));
      
      for(int i = 0; i < tempArray.size(); i += 3)
      {
        //x
        itemArray.add(tempArray.get(i+1));
        //y
        itemArray.add(tempArray.get(i+2));
        //foreground
        itemArray.add("foreground");
        //name
        itemArray.add(tempArray.get(i) + "icon");
      }
    }  
    catch(IOException IOe)
    {
      j.err(new Exception("Some other read error.", IOe));
    }
    catch(IndexOutOfBoundsException IOOBe)
    {
      j.err(new Exception("The structure contains no objects", IOOBe));
    }

    //find the highest/lowest coordinates
    for(int i = 0; i < itemArray.size(); i+=4)
    {
      if(highestX < Integer.parseInt(itemArray.get(i)))
      {
        highestX = Integer.parseInt(itemArray.get(i));
      }
      
      if(lowestX > Integer.parseInt(itemArray.get(i)))
      {
        lowestX = Integer.parseInt(itemArray.get(i));
      }
      
      if(highestY < Integer.parseInt(itemArray.get(i+1)))
      {
        highestY = Integer.parseInt(itemArray.get(i+1));
      }
      
      if(lowestY > Integer.parseInt(itemArray.get(i+1)))
      {
        lowestY = Integer.parseInt(itemArray.get(i+1));
      }
    }
    
    //if lowest y value is less than 0, it's built below
    if(lowestY < 0)
    {
      staticData.setBelow(true);
    }
    
    //make lowestX positive
    lowestX = 0 - lowestX;

    //set the row/column sizes and button text
    if(staticData.getBelow())
    {
      staticData.setNumRows((1 - lowestY) + highestY*2+1);
      buildBelowButton.setText("Below");
    }
    else
    {
      staticData.setNumRows(highestY + 1);
      buildBelowButton.setText("Above");
    }
    staticData.setNumColumns(highestX + lowestX + 1);

    //renew the array
    staticData.setPicLocations(new int[2][staticData.getNumColumns()][staticData.getNumRows()]);
    
    //set the default values
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
    
    //flag for if an image was misnamed
    boolean badImage = false;
    
    //set all the non-empty tile
    for(int i = 0; i < itemArray.size(); i+=4)
    {
      //reset tile
      tile = staticData.emptyCell;
      
      //set the layer
      if(itemArray.get(i+2).equals("foreground"))
      {
        layer = 0;
      }
      else
      {
        layer = 1;
      }
      
      //catch any invalid tile names
      //they won't exist, so the hashmap with return null
      try
      {
        //find out which tile it is
        tile = tileIcons.getReverse(itemArray.get(i+3) + ".png");
      }
      catch(NullPointerException NPe)
      {
        j.err(new Exception("Failed to locate tile " + itemArray.get(i+3) + ".png", NPe));
        badImage = true;
        continue;
      }
      
      //set the grid coordinate to the tile's value
      //get yCoord
      if(staticData.getBelow())
      {
        yCoord = 0-(Integer.parseInt(itemArray.get(i+1))-highestY);
      }
      else
      {
        yCoord = 0-(Integer.parseInt(itemArray.get(i+1))-staticData.getNumRows())-1;
      }
      
      //set the cell value
      staticData.setPicLocations(layer, Integer.parseInt(itemArray.get(i))+lowestX, yCoord, tile);
    }
    
    //if a bad image was found, popup a warning and disable autosaving
    if(badImage)
    {
      staticData.toggleAutoSave();
      showMessageDialog(null, "Some images failed to load, see the log for details");
    }
    
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
    j.out("Load Ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
    
    //clear the string showing the status
    j.setDrawString("");
  }
  
  //deletes an item
  public void deleteStructure()
  {
    //confirm deletion
    if(JOptionPane.showConfirmDialog(null, "Delete item? This can't be undone.", "Delete", JOptionPane.YES_NO_OPTION) != 0)
    {
      return;
    }
    
    //reset id
    staticData.setId(staticData.emptyCell);
    
    //get the name of the structure chosen for deletion
    String itemName = staticData.showAvailableStructures(0, "Delete", "Select the structure to delete");
    
    //if they x'ed out, stop.  else concatenate the CS_ prefix to the front
    if(itemName.equals(""))
    {
      return;
    }
    else
    {
      itemName = "CS_" + itemName;
    }
    
    //output that it's starting
    j.out("Deleting...");
    j.setDrawString("Deleting...");
    gridPanel.repaint();
    
    //start of operation
    start = System.nanoTime();
    
    //holds the player.config
    ArrayList<String> configArray = null;
    
    //writes the new player.config
    PrintWriter writeConfig = null;
    
    //delete everything
    try
    {
      if(Files.exists(Paths.get("..", "objects", "generic", itemName, itemName + ".animation")))
      {
        Files.delete(Paths.get("..", "objects", "generic", itemName, itemName + ".animation"));
      }
      
      if(Files.exists(Paths.get("..", "objects", "generic", itemName, itemName + ".frames")))
      {
        Files.delete(Paths.get("..", "objects", "generic", itemName, itemName + ".frames"));
      }
      
      if(Files.exists(Paths.get("..", "objects", "generic", itemName, itemName + ".object")))
      {
        Files.delete(Paths.get("..", "objects", "generic", itemName, itemName + ".object"));
      }
      
      if(Files.exists(Paths.get("..", "objects", "generic", itemName, itemName + ".png")))
      {
        Files.delete(Paths.get("..", "objects", "generic", itemName, itemName + ".png"));
      }
      
      if(Files.exists(Paths.get("..", "objects", "generic", itemName, itemName + "Icon.png")))
      {
        Files.delete(Paths.get("..", "objects", "generic", itemName, itemName + "Icon.png"));
      }
      
      if(Files.exists(Paths.get("..", "objects", "generic", itemName)))
      {
        Files.delete(Paths.get("..", "objects", "generic", itemName));
      }
      
      if(Files.exists(Paths.get("..", "recipes", "objects", itemName + ".recipe")))
      {
        Files.delete(Paths.get("..", "recipes", "objects", itemName + ".recipe"));
      }
      
      if(Files.exists(Paths.get("..", "scripts", itemName, itemName + "0.lua")))
      {
        Files.delete(Paths.get("..", "scripts", itemName, itemName + "0.lua"));
      }
      
      if(Files.exists(Paths.get("..", "scripts", itemName, itemName + "1.lua")))
      {
        Files.delete(Paths.get("..", "scripts", itemName, itemName + "1.lua"));
      }
      
      if(Files.exists(Paths.get("..", "scripts", itemName, itemName + ".lua")))
      {
        Files.delete(Paths.get("..", "scripts", itemName, itemName + ".lua"));
      }
      
      if(Files.exists(Paths.get("..", "scripts", itemName)))
      {
        Files.delete(Paths.get("..", "scripts", itemName));
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to delete the object " + itemName, IOe));
    }

    //try to open the player.config
    try
    (
      BufferedReader readConfig = new BufferedReader(new FileReader(staticData.buildPath(new String[]{"..", "player.config"})))
    )
    {
      configArray = new ArrayList<String>();
      
      //reads the file
      for(String configLine = readConfig.readLine(); configLine != null; configLine = readConfig.readLine())
      {
        //if the item name wasn't found, add to ArrayList
        if(!configLine.contains("\"" + itemName + "\""))
        {
          configArray.add(configLine);
        }
        
        //if it was found and it's the last item in the file, remove the comma from the last item in player.config
        if
        (
          configLine.contains("\"" + itemName + "\"") && 
          configLine.contains("}") && 
          !configLine.contains("},")
        )
        {
          configArray.set(configArray.size()-1, configArray.get(configArray.size()-1).replace(",", ""));
        }
      }
    }
    catch(IOException IOe)
    {
      j.err(new Exception("Failed to open player.config", IOe));
      return;
    }
    
    //try to open the file for writing
    try
    {
      writeConfig = new PrintWriter(staticData.buildPath(new String[]{"..", "player.config"}));
    }
    catch(FileNotFoundException FNFe)
    {
      j.err(new Exception("Failed to write the player.config", FNFe));
      return;
    }
      
    //write everything and close
    for(int i = 0; i < configArray.size(); i++)
    {
      writeConfig.println(configArray.get(i));
    }
    
    writeConfig.close();
    
    //end of operation
    end = System.nanoTime();
    
    //output the time it took
    j.out("Delete Ending, took " + Float.toString((float)(end-start)/1000000000) + " second(s)");
    
    //clear the string showing the status
    j.setDrawString("");
  }
}