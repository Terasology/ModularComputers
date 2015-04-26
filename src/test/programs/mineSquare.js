// Inventory Manipulator module
var invMod = computer.bindModule(0);
// Internal Storage module
var storeMod = computer.bindModule(1);
// Mobility module
var moveMod = computer.bindModule(2);
// Harvest module
var harvestMod = computer.bindModule(3);

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

var movementMap =
    ["east", "north", "west", "south"];
var mapSize = movementMap.getSize();
for (var i=0; i<mapSize; i++) {
    harvestToInventory("down");
    move(movementMap[i]);
}

// Dump everything from internal inventory
// to the inventory above
invMod.dump(outInv, upInv);



