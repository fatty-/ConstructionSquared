--splits the string with the tile data into a table using any non-alphanumeric characters as the separator
--%w+ means all alphanumeric characters until a non-alphanumeric character or end of line is reached
--%W means a single non-alphanumeric character
function split(dataString)
  --create table to be returned
  local splitTable = {  }
  
  --insert everything into the table
  for i in string.gmatch(dataString, "[-]?%w+") do
    table.insert(splitTable, i)
  end
  
  --return table of tile data
  --each tile is represented by 4 entries
  return splitTable
end

--attempts to place the structure
function makeStructure()  
  --whether or not the tile was actually placed
  local tileSuccess = true;
  
  --split the data string into a table
  --build() is where the string is initialized and returned
  --"pi" is the default value the table is initialized to, so the table doesn't split() a giant string every scriptDelta
  if self.creationTable[1] == "pi" then
    table.remove(self.creationTable)
    self.creationTable = split(build())
    self.objectTable = split(place())
	  self.destructionLoop = #self.creationTable
    self.destroy = true
    self.create = false
    self.place = false
  end
  
  --attempts to destroy all tiles that exist in locations where new tiles will be placed
  --destroys both the foreground and background of all coordinates
  if self.destroy == true and self.create == false and self.place == false then
    world.damageTiles( {entity.toAbsolutePosition({self.creationTable[self.destructionLoop-3]+self.xOffset, tonumber(self.creationTable[self.destructionLoop-2])})}, "foreground", entity.toAbsolutePosition({self.creationTable[self.destructionLoop-3]+self.xOffset, tonumber(self.creationTable[self.destructionLoop-2])}), "crushing", 1000000)
    
    world.damageTiles( {entity.toAbsolutePosition({self.creationTable[self.destructionLoop-3]+self.xOffset, tonumber(self.creationTable[self.destructionLoop-2])})}, "background", entity.toAbsolutePosition({self.creationTable[self.destructionLoop-3]+self.xOffset, tonumber(self.creationTable[self.destructionLoop-2])}), "crushing", 1000000)
    
	self.destructionLoop = self.destructionLoop - 4
    
    --change flags when destruction is done
    if self.destructionLoop <= 0 then
      self.destroy = false
      self.create = true
    end
  end
  
  --after destruction is finished, attempts to place all the new tiles
  if self.destroy == false and self.create == true and self.place == false then
    --make sure there's data in the table, otherwise set to true to force an exit
    if #self.creationTable > 1 then
      tileSuccess = world.placeMaterial(entity.toAbsolutePosition( {self.creationTable[self.creationLoop]+self.xOffset, tonumber(self.creationTable[self.creationLoop+1])} ), self.creationTable[self.creationLoop+2], self.creationTable[self.creationLoop+3], nil, true)
    else
      tileSuccess = true
    end
    
    --if this is the first run, reset the other table
    if self.creationLoop == 1 then
      self.placementTable = {  }
      self.placementLoop = 1
    end
	
    --if placement failed, put the failed location in the other table
	if tileSuccess == false then
	  table.insert(self.placementTable, self.creationTable[self.creationLoop])
	  table.insert(self.placementTable, self.creationTable[self.creationLoop+1])
	  table.insert(self.placementTable, self.creationTable[self.creationLoop+2])
	  table.insert(self.placementTable, self.creationTable[self.creationLoop+3])
	  self.placementLoop = self.placementLoop + 4
	end
    
    --update loop index
	self.creationLoop = self.creationLoop + 4
    
    --if the loop is over, update flags
    if self.creationLoop > #self.creationTable then
      self.create = false
      self.place = true
    end
  end
  
  --hopefully temporary fix for getting all the blocks to be placed until an API call exists to force blocks to be placed even if they couldn't be placed like that ingame
  if self.destroy == false and self.create == false and self.place == true then
    --make sure there's data in the table, otherwise set to true to force an exit
    if self.placementLoop > 1 then
      tileSuccess = world.placeMaterial(entity.toAbsolutePosition( {self.placementTable[self.placementLoop-4]+self.xOffset, tonumber(self.placementTable[self.placementLoop-3])} ), self.placementTable[self.placementLoop-2], self.placementTable[self.placementLoop-1], nil, true)
    else
      tileSuccess = true
    end
    
    --if this is the first run, reset the other table
    if self.placementLoop-1 == #self.placementTable then
      self.creationTable = {  }
      self.creationLoop = 1
    end
    
    --if placement failed, put the failed location in the other table
    if tileSuccess == false then
      table.insert(self.creationTable, self.placementTable[self.placementLoop-4])
      table.insert(self.creationTable, self.placementTable[self.placementLoop-3])
      table.insert(self.creationTable, self.placementTable[self.placementLoop-2])
      table.insert(self.creationTable, self.placementTable[self.placementLoop-1])
    end
    
    --update loop index
	self.placementLoop = self.placementLoop - 4
    
    --if the loop is over, update flags
    if self.placementLoop <= 1 then
      self.create = true
      self.place = false
    end
  end
  
  --if the tables are the same size, then all attempts to place tiles in the last loop failed and there's no point to continue
  --placeObjects twice in case one object is placed on top of another
  if #self.creationTable == #self.placementTable then
    if self.objectLoop >= #self.objectTable then
      entity.smash()
    else
      world.placeObject(self.objectTable[self.objectLoop], entity.toAbsolutePosition({tonumber(self.objectTable[self.objectLoop+1])+self.xOffset, tonumber(self.objectTable[self.objectLoop+2])}))
    end
    
    if self.objectLoop == 1 then
      self.destroy = false
      self.create = false
      self.place = false
    end
  
	  self.objectLoop = self.objectLoop + 3
  end
end