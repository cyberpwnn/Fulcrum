# Fulcrum
The old neglected engine out back.

## Fast World Modification & World packets
No, it's not async, it's just fast. Going async adds additional overhead and is only effective in large modifications (which is why FAWE makes use of it)
```java
World bworld = Bukkit.getWorld("someWorld");

// Access the FastWorld instance
FastWorld world = Fulcrum.faster(bworld);

// Make changes
world.getBlockAt(4, 5, 6).setType(Material.AIR);
world.getBlockAt(4, 6, 6).setType(Material.AIR);
world.getBlockAt(4, 7, 6).setType(Material.AIR);

// This is still a FastChunk. you don't need to cast it unless you need
// something from FastChunk
Chunk chunk = world.getChunkAt(4, 5);
chunk.getBlock(4, 3, 7).setType(Material.AIR);

// Blocks too
Block slowBlock = bworld.getBlockAt(32, 4, 5);
FastBlock block = Fulcrum.faster(slowBlock);
block.setType(Material.GLASS);

// Make boom go faster
world.createExplosion(block.getLocation(), 50f);

// Set biomes and have them updated automatically
block.setBiome(Biome.HELL);

// Queue manually
IAdapter a = Fulcrum.adapter;
a.setBlock(block.getLocation(), new BlockType(Material.AIR));

// Update some blocks when you actually need to
a.applyPhysics(block);

// Request a chunk update anyways on a section
a.makeDirty(chunk, block.getY() >> 4);

// Update all non-air sections of the chunk
a.makeDirty(chunk);

// Destroy bandwidth by resending the whole thing
a.makeFullyDirty(chunk);

// Request resending some blocks with BlockChange and MultiChange
a.makeDirty(block.getLocation());
a.makeDirty(block.getRelative(BlockFace.UP).getLocation());

// Send unload packets
a.sendUnload(chunk);

// Reload a chunk (unload then send sections + light)
a.sendReload(chunk);
```

## Store data in blocks, chunks, and worlds
```java
// This time we need FastBlock, not just Block
FastBlock fb = (FastBlock) Fulcrum.faster("myWorld").getBlockAt(1, 2, 3);

// Read data from a custom "section"
// If it doesnt exist, an empty one will be created
// It's good practice to read in a pull push way
DataCluster cc = fb.readData("my-custom-category");
cc.put("integer", 3);
cc.put("double", 3.4D);
cc.put("float", 3f);
cc.put("string", "ffff");
cc.put("boolean", false);
cc.put("long", M.ms());
cc.put("string-list", new GList<String>().qadd("alpha").qadd("bravo"));

// Then push the data back to the block to read later on
fb.writeData("my-custom-category", cc);
```
