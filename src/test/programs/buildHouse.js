// Inventory Manipulator module
var invMod = computer.bindModuleOfType("Inventory");
// Internal Storage module
var storeMod = computer.bindModuleOfType("Storage");
// Mobility module
var moveMod = computer.bindModuleOfType("Mobility");
// World Interaction module
var worldMod = computer.bindModuleOfType("World");

// Bind access to inventory above computer
// for input (adding items to it)
var upInputInv = invMod.getInputInventoryBinding("up");
var upOutputInv = invMod.getOutputInventoryBinding("up");

// Bind access to internal inventory in
// the computer
var inInv = storeMod.getInputInventoryBinding();
var outInv = storeMod.getOutputInventoryBinding();

var sizeX = os.parseInt(args[0]);
var sizeZ = os.parseInt(args[1]);
var sizeY = os.parseInt(args[2]);

// Dump all items from chest into local storage
invMod.dump(upOutputInv, inInv);

worldMod.destroyBlockToInventory("up", inInv);

function buildWall(size, direction, moveDirection) {
    worldMod.placeBlock(direction, outInv, 0);
    for (var i=0; i<size-1; i++) {
        moveMod.move(moveDirection);
        worldMod.placeBlock(direction, outInv, 0);
    }
}

function buildLevel(x, z) {
    buildWall(z, "east", "north");
    buildWall(x, "north", "west");
    buildWall(z, "west", "south");
    buildWall(x, "south", "east");
}

function buildFloor(x, z, direction) {
    for (var i=0; i<x-1; i++) {
        worldMod.placeBlock(direction, outInv, 0);
        moveMod.move("west");
    }
    for (var i=0; i<z-1; i++) {
        worldMod.placeBlock(direction, outInv, 0);
        moveMod.move("north");
        for (var j=0; j<x-2; j++) {
            worldMod.placeBlock(direction, outInv, 0);
            var dir;
            if (i%2 == 0)
                dir = "east";
            else
                dir = "west";
            moveMod.move(dir);
        }
    }
    worldMod.placeBlock(direction, outInv, 0);
    if (z%2 == 1) {
        for (var i=0; i<x-1; i++) {
            moveMod.move("east");
        }
    } else {
        moveMod.move("east");
    }

    for (var i=0; i<z-1; i++) {
        worldMod.placeBlock(direction, outInv, 0);
        moveMod.move("south");
    }
}

for (var i=0; i<sizeY; i++) {
    buildLevel(sizeX, sizeZ);
    // Don't move after last level
    if (i<sizeY-1)
        moveMod.move("up");
}

buildFloor(sizeX, sizeZ, "up");

for (var i=0; i<sizeY-1; i++) {
    moveMod.move("down");
}

worldMod.placeBlock("up", outInv, 1);
invMod.dump(outInv, upInputInv);