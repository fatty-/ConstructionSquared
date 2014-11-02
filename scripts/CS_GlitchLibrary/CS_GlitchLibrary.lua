function init(args)
  --variable to hold the tiles
  self.creationTable = { "pi" }
  --variable to hold the objects
  self.objectTable = {  }
  --controls object placement
  self.objectLoop = 1
  --variable to determine when to stop removing tiles
  self.destructionLoop = 1
  --variable to place the tiles
  self.creationLoop = 1
  --loop to control the reverse placement
  self.placementLoop = 1
  --table to hold all the tiles that failed to be placed
  self.placementTable = {  }
  --the x offset
  self.xOffset = 2
  --the lowest x coordinate where something will be placed
  self.leftX = 0
  --the lowest y coordinate where something will be placed
  self.rightX = 67
  --the highest y coordinate where something will be placed
  self.bottomY = 0
  --the highest x coordinate where something will be placed
  self.topY = 30
  --let people "e" it
  entity.setInteractive(true)
  --if it has been interacted with
  self.isActive = false
end

function main()
  --keeps the area around the structure loaded as long as someone remains on the planet
  local bottom = entity.toAbsolutePosition({-2, -2})
  local top = entity.toAbsolutePosition({2, 2})
  world.loadRegion({bottom[1], bottom[2], top[1], top[2]})

  --if the structure has been activated
  if self.isActive == true then
    --place a background tile to make sure everything else can be placed
    world.placeMaterial(entity.toAbsolutePosition( {-1 + self.xOffset, -1} ), "background", "dirt")
    --place everything
    makeStructure()
  else
    --spawn projectiles to indicate structure bounds if inactive
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.bottomY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.leftX+1+self.xOffset+0.5, self.bottomY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.leftX+2+self.xOffset+0.5, self.bottomY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.bottomY+2-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.bottomY+3-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.rightX-1+self.xOffset-0.5, self.bottomY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.rightX+self.xOffset-0.5, self.bottomY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.bottomY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.bottomY+2-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.bottomY+3-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.rightX-1+self.xOffset-0.5, self.topY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.rightX+self.xOffset-0.5, self.topY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.topY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.topY-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.rightX+1+self.xOffset-0.5, self.topY-1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.topY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.leftX+1+self.xOffset+0.5, self.topY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.leftX+2+self.xOffset+0.5, self.topY+1-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.topY-0.5 }))
    world.spawnProjectile("corner", entity.toAbsolutePosition({ self.leftX+self.xOffset+0.5, self.topY-1-0.5 }))
  end
end

function onInteraction(args)
  --invert the state of activity
  self.isActive = not self.isActive
end