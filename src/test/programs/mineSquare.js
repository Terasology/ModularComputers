// Inventory Manipulator module
var invMod = computer.bindModuleOfType("Inventory");
// Internal Storage module
var storeMod = computer.bindModuleOfType("Storage");
// Mobility module
var moveMod = computer.bindModuleOfType("Mobility");
// World Interaction module
var harvestMod = computer.bindModuleOfType("World");

// Bind access to inventory above computer
// for input (adding items to it)
var upInv = invMod.getInputInventoryBinding("up");

// Bind access to internal inventory in
// the computer
var inInv = storeMod.getInputInventoryBinding();
var outInv = storeMod.getOutputInventoryBinding();

function harvestToInventory(direction) {
    harvestMod.destroyBlockToInventory(direction,
        inInv);
}

function move(direction) {
    moveMod.move(direction);
}

var sizeX = os.parseInt(args[0]);
var sizeZ = os.parseInt(args[1]);

var movementMap =
    ["east", "north", "west", "south"];

for (var i=0; i<sizeX-1; i++) {
    harvestToInventory("down");
    move(movementMap[0]);
}
for (var i=0; i<sizeZ-1; i++) {
    harvestToInventory("down");
    move(movementMap[1]);
    for (var j=0; j<sizeX-2; j++) {
        harvestToInventory("down");
        var dir;
        if (i%2 == 0)
          dir = 2;
        else
          dir = 0;
        move(movementMap[dir]);
    }
}
harvestToInventory("down");
if (sizeZ%2 == 1) {
    for (var i=0; i<sizeX-1; i++) {
        move(movementMap[2]);
    }
} else {
    move(movementMap[2]);
}

for (var i=0; i<sizeZ-1; i++) {
    harvestToInventory("down");
    move(movementMap[3]);
}

// Dump everything from internal inventory
// to the inventory above
invMod.dump(outInv, upInv);



