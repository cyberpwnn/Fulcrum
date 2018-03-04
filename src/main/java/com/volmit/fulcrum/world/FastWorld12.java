package com.volmit.fulcrum.world;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.BlockChangeDelegate;
import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Difficulty;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.WorldType;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LightningStrike;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Consumer;
import org.bukkit.util.Vector;

import com.volmit.fulcrum.Fulcrum;

@SuppressWarnings("deprecation")
public class FastWorld12 implements FastWorld
{
	private World w;

	public FastWorld12(World w)
	{
		this.w = w;
	}

	@Override
	public FastBlock getBlockAt(int x, int y, int z)
	{
		return new FastBlock12(w.getBlockAt(x, y, z));
	}

	@Override
	public FastBlock getBlockAt(Location location)
	{
		return new FastBlock12(w.getBlockAt(location));
	}

	@Override
	public int getBlockTypeIdAt(int x, int y, int z)
	{
		return w.getBlockTypeIdAt(x, y, z);
	}

	@Override
	public int getBlockTypeIdAt(Location location)
	{
		return w.getBlockTypeIdAt(location);
	}

	@Override
	public int getHighestBlockYAt(int x, int z)
	{
		return w.getHighestBlockYAt(x, z);
	}

	@Override
	public int getHighestBlockYAt(Location location)
	{
		return w.getHighestBlockYAt(location);
	}

	@Override
	public FastBlock getHighestBlockAt(int x, int z)
	{
		return new FastBlock12(w.getHighestBlockAt(x, z));
	}

	@Override
	public FastBlock getHighestBlockAt(Location location)
	{
		return new FastBlock12(w.getHighestBlockAt(location));
	}

	@Override
	public FastChunk getChunkAt(int x, int z)
	{
		return new FastChunk12(w.getChunkAt(x, z));
	}

	@Override
	public FastChunk getChunkAt(Location location)
	{
		return new FastChunk12(w.getChunkAt(location));
	}

	@Override
	public FastChunk getChunkAt(Block block)
	{
		return new FastChunk12(w.getChunkAt(block));
	}

	@Override
	public boolean isChunkLoaded(Chunk chunk)
	{
		return w.isChunkLoaded(chunk);
	}

	@Override
	public FastChunk[] getLoadedChunks()
	{
		Chunk[] c = w.getLoadedChunks();
		FastChunk[] fc = new FastChunk[c.length];

		for(int i = 0; i < c.length; i++)
		{
			fc[i] = new FastChunk12(c[i]);
		}

		return fc;
	}

	@Override
	public void loadChunk(Chunk chunk)
	{
		w.loadChunk(chunk);
	}

	@Override
	public boolean isChunkLoaded(int x, int z)
	{
		return w.isChunkLoaded(x, z);
	}

	@Override
	public boolean isChunkInUse(int x, int z)
	{
		return w.isChunkInUse(x, z);
	}

	@Override
	public void loadChunk(int x, int z)
	{
		w.loadChunk(x, z);
	}

	@Override
	public boolean loadChunk(int x, int z, boolean generate)
	{
		return w.loadChunk(x, z, generate);
	}

	@Override
	public boolean unloadChunk(Chunk chunk)
	{
		return w.unloadChunk(chunk);
	}

	@Override
	public boolean unloadChunk(int x, int z)
	{
		return w.unloadChunk(x, z);
	}

	@Override
	public boolean unloadChunk(int x, int z, boolean save)
	{
		return w.unloadChunk(x, z, save);
	}

	@Override
	public boolean unloadChunk(int x, int z, boolean save, boolean safe)
	{
		return w.unloadChunk(x, z, save, safe);
	}

	@Override
	public boolean unloadChunkRequest(int x, int z)
	{
		return w.unloadChunkRequest(x, z);
	}

	@Override
	public boolean unloadChunkRequest(int x, int z, boolean safe)
	{
		return w.unloadChunkRequest(x, z, safe);
	}

	@Override
	public boolean regenerateChunk(int x, int z)
	{
		return w.regenerateChunk(x, z);
	}

	@Override
	public boolean refreshChunk(int x, int z)
	{
		Fulcrum.adapter.sendReload(getChunkAt(x, z));
		return true;
	}

	@Override
	public Item dropItem(Location location, ItemStack item)
	{
		return w.dropItem(location, item);
	}

	@Override
	public Item dropItemNaturally(Location location, ItemStack item)
	{
		return w.dropItemNaturally(location, item);
	}

	@Override
	public Arrow spawnArrow(Location location, Vector direction, float speed, float spread)
	{
		return w.spawnArrow(location, direction, speed, spread);
	}

	@Override
	public <T extends Arrow> T spawnArrow(Location location, Vector direction, float speed, float spread, Class<T> clazz)
	{
		return w.spawnArrow(location, direction, speed, spread, clazz);
	}

	@Override
	public boolean generateTree(Location location, TreeType type)
	{
		return w.generateTree(location, type);
		// TODO FASTER
	}

	@Override
	public boolean generateTree(Location loc, TreeType type, BlockChangeDelegate delegate)
	{
		return w.generateTree(loc, type, delegate);
		// TODO FASTER
	}

	@Override
	public Entity spawnEntity(Location loc, EntityType type)
	{
		return w.spawnEntity(loc, type);
	}

	@Override
	public LightningStrike strikeLightning(Location loc)
	{
		return w.strikeLightning(loc);
	}

	@Override
	public LightningStrike strikeLightningEffect(Location loc)
	{
		return w.strikeLightningEffect(loc);
	}

	@Override
	public List<Entity> getEntities()
	{
		return w.getEntities();
	}

	@Override
	public List<LivingEntity> getLivingEntities()
	{
		return w.getLivingEntities();
	}

	@Override
	public <T extends Entity> Collection<T> getEntitiesByClass(@SuppressWarnings("unchecked") Class<T>... classes)
	{
		return w.getEntitiesByClass(classes);
	}

	@Override
	public <T extends Entity> Collection<T> getEntitiesByClass(Class<T> cls)
	{
		return w.getEntitiesByClass(cls);
	}

	@Override
	public Collection<Entity> getEntitiesByClasses(Class<?>... classes)
	{
		return w.getEntitiesByClasses(classes);
	}

	@Override
	public List<Player> getPlayers()
	{
		return w.getPlayers();
	}

	@Override
	public Collection<Entity> getNearbyEntities(Location location, double x, double y, double z)
	{
		return w.getNearbyEntities(location, x, y, z);
	}

	@Override
	public String getName()
	{
		return w.getName();
	}

	@Override
	public UUID getUID()
	{
		return w.getUID();
	}

	@Override
	public Location getSpawnLocation()
	{
		return w.getSpawnLocation();
	}

	@Override
	public boolean setSpawnLocation(Location location)
	{
		return w.setSpawnLocation(location);
	}

	@Override
	public boolean setSpawnLocation(int x, int y, int z)
	{
		return w.setSpawnLocation(x, y, z);
	}

	@Override
	public long getTime()
	{
		return w.getTime();
	}

	@Override
	public void setTime(long time)
	{
		w.setTime(time);
	}

	@Override
	public long getFullTime()
	{
		return w.getFullTime();
	}

	@Override
	public void setFullTime(long time)
	{
		w.setFullTime(time);
	}

	@Override
	public boolean hasStorm()
	{
		return w.hasStorm();
	}

	@Override
	public void setStorm(boolean hasStorm)
	{
		w.setStorm(hasStorm);
	}

	@Override
	public int getWeatherDuration()
	{
		return w.getWeatherDuration();
	}

	@Override
	public void setWeatherDuration(int duration)
	{
		w.setWeatherDuration(duration);
	}

	@Override
	public boolean isThundering()
	{
		return w.isThundering();
	}

	@Override
	public void setThundering(boolean thundering)
	{
		w.setThundering(thundering);
	}

	@Override
	public int getThunderDuration()
	{
		return w.getThunderDuration();
	}

	@Override
	public void setThunderDuration(int duration)
	{
		w.setThunderDuration(duration);
	}

	@Override
	public boolean createExplosion(double x, double y, double z, float power)
	{
		return w.createExplosion(x, y, z, power);
		// TODO FAST
	}

	@Override
	public boolean createExplosion(double x, double y, double z, float power, boolean setFire)
	{
		return w.createExplosion(x, y, z, power, setFire);
		// TODO FAST
	}

	@Override
	public boolean createExplosion(double x, double y, double z, float power, boolean setFire, boolean breakBlocks)
	{
		return w.createExplosion(x, y, z, power, setFire, breakBlocks);
		// TODO FAST
	}

	@Override
	public boolean createExplosion(Location loc, float power)
	{
		return w.createExplosion(loc, power);
		// TODO FAST
	}

	@Override
	public boolean createExplosion(Location loc, float power, boolean setFire)
	{
		return w.createExplosion(loc, power, setFire);
		// TODO FAST
	}

	@Override
	public Environment getEnvironment()
	{
		return w.getEnvironment();
	}

	@Override
	public long getSeed()
	{
		return w.getSeed();
	}

	@Override
	public boolean getPVP()
	{
		return w.getPVP();
	}

	@Override
	public void setPVP(boolean pvp)
	{
		w.setPVP(pvp);
	}

	@Override
	public ChunkGenerator getGenerator()
	{
		return w.getGenerator();
	}

	@Override
	public void save()
	{
		w.save();
	}

	@Override
	public List<BlockPopulator> getPopulators()
	{
		return w.getPopulators();
	}

	@Override
	public <T extends Entity> T spawn(Location location, Class<T> clazz) throws IllegalArgumentException
	{
		return w.spawn(location, clazz);
	}

	@Override
	public <T extends Entity> T spawn(Location location, Class<T> clazz, Consumer<T> function) throws IllegalArgumentException
	{
		return w.spawn(location, clazz, function);
	}

	@Override
	public FallingBlock spawnFallingBlock(Location location, MaterialData data) throws IllegalArgumentException
	{
		return w.spawnFallingBlock(location, data);
	}

	@Override
	public FallingBlock spawnFallingBlock(Location location, Material material, byte data) throws IllegalArgumentException
	{
		return w.spawnFallingBlock(location, material, data);
	}

	@Override
	public FallingBlock spawnFallingBlock(Location location, int blockId, byte blockData) throws IllegalArgumentException
	{
		return w.spawnFallingBlock(location, blockId, blockData);
	}

	@Override
	public void playEffect(Location location, Effect effect, int data)
	{
		w.playEffect(location, effect, data);
	}

	@Override
	public void playEffect(Location location, Effect effect, int data, int radius)
	{
		w.playEffect(location, effect, data, radius);
	}

	@Override
	public <T> void playEffect(Location location, Effect effect, T data)
	{
		w.playEffect(location, effect, data);
	}

	@Override
	public <T> void playEffect(Location location, Effect effect, T data, int radius)
	{
		w.playEffect(location, effect, data, radius);
	}

	@Override
	public ChunkSnapshot getEmptyChunkSnapshot(int x, int z, boolean includeBiome, boolean includeBiomeTempRain)
	{
		return w.getEmptyChunkSnapshot(x, z, includeBiome, includeBiomeTempRain);
	}

	@Override
	public void setSpawnFlags(boolean allowMonsters, boolean allowAnimals)
	{
		w.setSpawnFlags(allowMonsters, allowAnimals);
	}

	@Override
	public boolean getAllowAnimals()
	{
		return w.getAllowAnimals();
	}

	@Override
	public boolean getAllowMonsters()
	{
		return w.getAllowMonsters();
	}

	@Override
	public Biome getBiome(int x, int z)
	{
		return w.getBiome(x, z);
	}

	@Override
	public void setBiome(int x, int z, Biome bio)
	{
		Fulcrum.adapter.setBiome(this, x, z, bio);
	}

	@Override
	public double getTemperature(int x, int z)
	{
		return w.getTemperature(x, z);
	}

	@Override
	public double getHumidity(int x, int z)
	{
		return w.getHumidity(x, z);
	}

	@Override
	public int getMaxHeight()
	{
		return w.getMaxHeight();
	}

	@Override
	public int getSeaLevel()
	{
		return w.getSeaLevel();
	}

	@Override
	public boolean getKeepSpawnInMemory()
	{
		return w.getKeepSpawnInMemory();
	}

	@Override
	public void setKeepSpawnInMemory(boolean keepLoaded)
	{
		w.setKeepSpawnInMemory(keepLoaded);
	}

	@Override
	public boolean isAutoSave()
	{
		return w.isAutoSave();
	}

	@Override
	public void setAutoSave(boolean value)
	{
		w.setAutoSave(value);
	}

	@Override
	public void setDifficulty(Difficulty difficulty)
	{
		w.setDifficulty(difficulty);
	}

	@Override
	public Difficulty getDifficulty()
	{
		return w.getDifficulty();
	}

	@Override
	public File getWorldFolder()
	{
		return w.getWorldFolder();
	}

	@Override
	public WorldType getWorldType()
	{
		return w.getWorldType();
	}

	@Override
	public boolean canGenerateStructures()
	{
		return w.canGenerateStructures();
	}

	@Override
	public long getTicksPerAnimalSpawns()
	{
		return w.getTicksPerAnimalSpawns();
	}

	@Override
	public void setTicksPerAnimalSpawns(int ticksPerAnimalSpawns)
	{
		w.setTicksPerAnimalSpawns(ticksPerAnimalSpawns);
	}

	@Override
	public long getTicksPerMonsterSpawns()
	{
		return w.getTicksPerMonsterSpawns();
	}

	@Override
	public void setTicksPerMonsterSpawns(int ticksPerMonsterSpawns)
	{
		w.setTicksPerMonsterSpawns(ticksPerMonsterSpawns);
	}

	@Override
	public int getMonsterSpawnLimit()
	{
		return w.getMonsterSpawnLimit();
	}

	@Override
	public void setMonsterSpawnLimit(int limit)
	{
		w.setMonsterSpawnLimit(limit);
	}

	@Override
	public int getAnimalSpawnLimit()
	{
		return w.getAnimalSpawnLimit();
	}

	@Override
	public void setAnimalSpawnLimit(int limit)
	{
		w.setAnimalSpawnLimit(limit);
	}

	@Override
	public int getWaterAnimalSpawnLimit()
	{
		return w.getWaterAnimalSpawnLimit();
	}

	@Override
	public void setWaterAnimalSpawnLimit(int limit)
	{
		w.setWaterAnimalSpawnLimit(limit);
	}

	@Override
	public int getAmbientSpawnLimit()
	{
		return w.getAmbientSpawnLimit();
	}

	@Override
	public void setAmbientSpawnLimit(int limit)
	{
		w.setAmbientSpawnLimit(limit);
	}

	@Override
	public void playSound(Location location, Sound sound, float volume, float pitch)
	{
		w.playSound(location, sound, volume, pitch);
	}

	@Override
	public void playSound(Location location, String sound, float volume, float pitch)
	{
		w.playSound(location, sound, volume, pitch);
	}

	@Override
	public void playSound(Location location, Sound sound, SoundCategory category, float volume, float pitch)
	{
		w.playSound(location, sound, category, volume, pitch);
	}

	@Override
	public void playSound(Location location, String sound, SoundCategory category, float volume, float pitch)
	{
		w.playSound(location, sound, category, volume, pitch);
	}

	@Override
	public String[] getGameRules()
	{
		return w.getGameRules();
	}

	@Override
	public String getGameRuleValue(String rule)
	{
		return w.getGameRuleValue(rule);
	}

	@Override
	public boolean setGameRuleValue(String rule, String value)
	{
		return w.setGameRuleValue(rule, value);
	}

	@Override
	public boolean isGameRule(String rule)
	{
		return w.isGameRule(rule);
	}

	@Override
	public WorldBorder getWorldBorder()
	{
		return w.getWorldBorder();
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int count)
	{
		w.spawnParticle(particle, location, count);
	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int count)
	{
		w.spawnParticle(particle, x, y, z, count);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, T data)
	{
		w.spawnParticle(particle, location, count, data);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, T data)
	{
		w.spawnParticle(particle, x, y, z, count, data);
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ)
	{
		w.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ);
	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ)
	{
		w.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, T data)
	{
		w.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, data);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, T data)
	{
		w.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, data);
	}

	@Override
	public void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra)
	{
		w.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, extra);
	}

	@Override
	public void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra)
	{
		w.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, extra);
	}

	@Override
	public <T> void spawnParticle(Particle particle, Location location, int count, double offsetX, double offsetY, double offsetZ, double extra, T data)
	{
		w.spawnParticle(particle, location, count, offsetX, offsetY, offsetZ, data);
	}

	@Override
	public <T> void spawnParticle(Particle particle, double x, double y, double z, int count, double offsetX, double offsetY, double offsetZ, double extra, T data)
	{
		w.spawnParticle(particle, x, y, z, count, offsetX, offsetY, offsetZ, data);
	}

	@Override
	public Spigot spigot()
	{
		return w.spigot();
	}

	@Override
	public void sendPluginMessage(Plugin source, String channel, byte[] message)
	{
		w.sendPluginMessage(source, channel, message);
	}

	@Override
	public Set<String> getListeningPluginChannels()
	{
		return w.getListeningPluginChannels();
	}

	@Override
	public void setMetadata(String metadataKey, MetadataValue newMetadataValue)
	{
		w.setMetadata(metadataKey, newMetadataValue);
	}

	@Override
	public List<MetadataValue> getMetadata(String metadataKey)
	{
		return w.getMetadata(metadataKey);
	}

	@Override
	public boolean hasMetadata(String metadataKey)
	{
		return w.hasMetadata(metadataKey);
	}

	@Override
	public void removeMetadata(String metadataKey, Plugin owningPlugin)
	{
		w.removeMetadata(metadataKey, owningPlugin);
	}
}
