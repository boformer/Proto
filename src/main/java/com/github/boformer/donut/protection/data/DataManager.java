package com.github.boformer.donut.protection.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections4.map.LRUMap;

import com.github.boformer.donut.protection.DonutProtectionPlugin;
import com.github.boformer.donut.protection.access.PlayerPlotAccess;
import com.github.boformer.donut.protection.access.PlayerWorldAccess;
import com.github.boformer.donut.protection.config.PluginConfig;

/**
 * Provides and manages the data of the plugin.
 * 
 * <p>The data is stored in a database.</p>
 * 
 * <p>The data is modified by the plugin at runtime (e.g. table of plots), 
 * while the configuration managed by {@link com.github.boformer.donut.protection.config.ConfigManager} should only be edited by the server admin (e.g. plot size).</p>
 */
public class DataManager
{
	private final DonutProtectionPlugin plugin;
	
	private String databaseTablePrefix;
	private Connection databaseConnection = null;
	
	private LRUMap<PlotID, PlotData> plotDataCache;
	private LRUMap<PlayerPlotAccess, Boolean> playerPlotAccessCache;
	private LRUMap<PlayerWorldAccess, Boolean> playerWorldAccessCache;
	
	
	/** 
	 * <i>Internal constructor: Create a new data manager.</i>
	 * 
	 * @param plugin The plugin
	 */
	public DataManager(DonutProtectionPlugin plugin)
	{
		this.plugin = plugin;
	}
	
	//TODO don't print stack trace/warning message when exception is passed up

	/** 
	 * <i>Internal method: Initializes the data manager when the server starts up. Creates the database tables on first startup.</i>
	 */
	public void initialize() throws Exception
	{
		//init cache
		plotDataCache = new LRUMap<>(50);
		playerPlotAccessCache = new LRUMap<>(50);
		playerWorldAccessCache = new LRUMap<>(50);
		
		
		//load the java driver for mySQL
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load Java's mySQL database driver. Check to make sure you've installed it properly.");
			throw e;
		}
		
		
		//refresh connection
		try
		{
			refreshDatabaseConnection();
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to connect to database. Check your config file settings.");
			throw e;
		}
		
		
		//create tables (fresh installation)
		databaseTablePrefix = plugin.getConfigManager().getPluginConfig().databaseTablePrefix;
		
		try 
		{
			//ensure the data tables exist
			Statement statement = databaseConnection.createStatement();
			
			//plots
			statement.execute("CREATE TABLE IF NOT EXISTS " + databaseTablePrefix + "plots ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "world_id INT(15) NOT NULL, "
					+ "x INT(15) NOT NULL, "
					+ "z INT(15) NOT NULL, "
					+ "name VARCHAR(16), "
					+ "state INT(5) NOT NULL, "
					+ "creation_date TIMESTAMP, "
					+ "last_mod_date TIMESTAMP)");
			
			//players
			statement.execute("CREATE TABLE IF NOT EXISTS " + databaseTablePrefix + "players ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid CHAR(36) NOT NULL, "
					+ "name VARCHAR(16) NOT NULL)");
			
			//worlds
			statement.execute("CREATE TABLE IF NOT EXISTS " + databaseTablePrefix + "worlds ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "uuid CHAR(36) NOT NULL, "
					+ "name VARCHAR(50) NOT NULL)");
			
			//groups
			statement.execute("CREATE TABLE IF NOT EXISTS " + databaseTablePrefix + "groups ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "name VARCHAR(16) NOT NULL");
			
			//world access
			statement.execute("CREATE TABLE IF NOT EXISTS " + databaseTablePrefix + "player_world_access ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "player_id INT(15) NOT NULL, "
					+ "world_id INT(15) NOT NULL, "
					+ "permission VARCHAR(50) NOT NULL)");
			
			//plot access
			statement.execute("CREATE TABLE IF NOT EXISTS " + databaseTablePrefix + "player_plot_access ("
					+ "id INT NOT NULL AUTO_INCREMENT, "
					+ "player_id INT(15) NOT NULL, "
					+ "plot_id INT(15) NOT NULL, "
					+ "permission VARCHAR(50) NOT NULL)");
		}
		catch(Exception e) 
		{
			plugin.getLogger().error("Unable to create the necessary database table. Details: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
		
	}
	
	/** 
	 * <i>Internal method: Closes the database connection when the server shuts down.</i>
	 */
	public void stop() throws Exception
	{
		if(databaseConnection != null) 
		{
			databaseConnection.close();
		}
	}
	
	private void refreshDatabaseConnection() throws Exception
	{
		if(databaseConnection == null || databaseConnection.isClosed()) 
		{
			PluginConfig config = plugin.getConfigManager().getPluginConfig();
			
			databaseConnection = DriverManager.getConnection(config.databaseUrl, config.databaseUser, config.databasePassword);
		}
	}


	/**
	 * Gets the data of a world. Use it to get the name of an unloaded world.
	 * @param worldID The world UUID
	 * @return The world data
	 * @throws Exception Database exception
	 */
	public WorldData getWorldData(UUID worldID) throws Exception
	{
		try
		{
			refreshDatabaseConnection();
			
			PreparedStatement statement = databaseConnection.prepareStatement(
					  "SELECT name "
					+ "FROM " + databaseTablePrefix + "worlds world "
					+ "WHERE world.uuid = ?"); //1
			
			statement.setString(1, worldID.toString());
			
			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.next()) 
			{
				String name = resultSet.getString("world.name");
				return new WorldData(name, worldID);
			}
			else 
			{
				//world does not exist in database
				return null;
			}
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load world data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}

	/**
	 * Gets the data of a player, including the last known name or the last login dater.
	 * @param playerID The player UUID
	 * @return The player data
	 * @throws Exception Database exception
	 */
	public PlayerData getPlayerData(UUID playerID) throws Exception
	{
		try
		{
			refreshDatabaseConnection();
			
			PreparedStatement statement = databaseConnection.prepareStatement(
					  "SELECT name "
					+ "FROM " + databaseTablePrefix + "players player"
					+ "WHERE player.uuid = ?"); //1
			
			statement.setString(1, playerID.toString());
			
			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.next()) 
			{
				String name = resultSet.getString("player.name");
				return new PlayerData(name, playerID);
			}
			else 
			{
				//player does not exist in database
				return null;
			}
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load player data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}

	/**
	 * Gets the data of a plot, including the name, the state and various dates.
	 * @param plotID The Plot ID
	 * @return The plot data
	 * @throws Exception Database exception
	 */
	public PlotData getPlotData(PlotID plotID) throws Exception
	{
		//first check cache
		if(plotDataCache.containsKey(plotID)) 
		{
			return plotDataCache.get(plotID);
		}
		
		try
		{
			refreshDatabaseConnection();
			
			PreparedStatement statement = databaseConnection.prepareStatement(
					  "SELECT plot.name, plot.state, plot.creation_date, plot.last_mod_date "
					+ "FROM " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world "
					+ "WHERE plot.world_id = world.id "
					+ "AND world.uuid = ? " //1
					+ "AND plot.x = ? " //2
					+ "AND plot.z = ?"); //3
			
			statement.setString(1, plotID.getWorldID().toString());
			statement.setInt(2, plotID.getX());
			statement.setInt(3, plotID.getZ());

			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.next()) 
			{
				PlotData plotData = new PlotData(plotID);
				
				plotData.setName(resultSet.getString("plot.name"));
				plotData.setState(resultSet.getInt("plot.state"));
				plotData.setCreationDate(resultSet.getTimestamp("plot.creation_date"));
				plotData.setLastModificationDate(resultSet.getTimestamp("plot.last_mod_date"));
				
				//update cache
				plotDataCache.put(plotID, plotData);
				
				return plotData;
			}
			else 
			{
				//plot does not exist in database
				return null;
			}
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load plot data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}

	//TODO implement or drop groups
	
	/**
	 * Gets the data of a group, including the name.
	 * @param groupID The group ID
	 * @return The group data
	 * @throws Exception Database exception
	 */
	public GroupData getGroupData(int groupID) throws Exception
	{
		try
		{
			refreshDatabaseConnection();
			
			PreparedStatement statement = databaseConnection.prepareStatement(
					  "SELECT group.name "
					+ "FROM " + databaseTablePrefix + "groups group"
					+ "WHERE group.id = ?"); //1
			
			statement.setInt(1, groupID);
			
			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.next()) 
			{
				String name = resultSet.getString("group.name");
				
				GroupData groupData = new GroupData();
				
				groupData.setId(groupID);
				groupData.setName(name);
				
				return groupData;
			}
			else 
			{
				//player does not exist in database
				return null;
			}
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load group data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}
	
	/**
	 * Checks if a player has access to a <u>world</u>, using the plugin's internal permission system.
	 * 
	 * @param playerID The player UUID
	 * @param worldID The world UUID
	 * @param permission The permission
	 * @return {@code true} if player has permission, {@code false} if not
	 * @throws Exception Database Exception
	 */
	public boolean hasWorldAccess(UUID playerID, UUID worldID, String permission) throws Exception
	{
		PlayerWorldAccess access = new PlayerWorldAccess(playerID, worldID, permission);
		
		//first check cache
		if(playerWorldAccessCache.containsKey(access)) 
		{
			return playerWorldAccessCache.get(access);
		}
		
		try
		{
			refreshDatabaseConnection();
			
			PreparedStatement statement = databaseConnection.prepareStatement(
					  "SELECT 1 "
					+ "FROM " + databaseTablePrefix + "player_world_access access, " + databaseTablePrefix + "players player, " + databaseTablePrefix + "worlds world "
					+ "WHERE access.world_id = world.id "
					+ "AND access.player_id = player.id "
					+ "AND player.uuid = ? " //1
					+ "AND world.uuid = ? " //2
					+ "AND access.permission = ?"); //3
			
			statement.setString(1, playerID.toString());
			statement.setString(2, worldID.toString());
			statement.setString(3, permission);

			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.next()) 
			{
				//record exists: player has permission
				
				//update cache
				playerWorldAccessCache.put(access, true);
				
				return true;
			}
			else 
			{
				//player has no permission
				
				//update cache
				playerWorldAccessCache.put(access, false);
				
				return false;
			}
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load world access data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}
	/**
	 * Checks if a player has access to a <u>plot</u>, using the plugin's internal permission system.
	 * 
	 * @param playerID The player UUID
	 * @param plotID The plot ID
	 * @param permission The permission
	 * @return {@code true} if player has permission, {@code false} if not
	 * @throws Exception Database Exception
	 */
	public boolean hasPlotAccess(UUID playerID, PlotID plotID, String permission) throws Exception
	{
		PlayerPlotAccess access = new PlayerPlotAccess(playerID, plotID, permission);
		
		//first check cache
		if(playerPlotAccessCache.containsKey(access)) 
		{
			return playerPlotAccessCache.get(access);
		}
		
		try
		{
			refreshDatabaseConnection();
			
			PreparedStatement statement = databaseConnection.prepareStatement(
					  "SELECT 1 "
					+ "FROM " + databaseTablePrefix + "player_plot_access access, " + databaseTablePrefix + "players player, " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world "
					+ "WHERE access.plot_id = plot.id "
					+ "AND access.player_id = player.id "
					+ "AND player.uuid = ? " //1
					+ "AND plot.world_id = world.id "
					+ "AND world.uuid = ? " //2
					+ "AND plot.x = ? " //3
					+ "AND plot.z = ? " //4
					+ "AND access.permission = ?"); //5
			
			statement.setString(1, playerID.toString());
			statement.setString(2, plotID.getWorldID().toString());
			statement.setInt(3, plotID.getX());
			statement.setInt(4, plotID.getZ());
			statement.setString(5, permission);

			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.next()) 
			{
				//record exists: player has permission
				
				//update cache
				playerPlotAccessCache.put(access, true);
				
				return true;
			}
			else 
			{
				//player has no permission
				
				//update cache
				playerPlotAccessCache.put(access, false);
				
				return false;
			}
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load plot access data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}

	//method for plotcheck
	/**
	 * Gets a list of plots with a certain {@link PlotState}, creation date and world UUID.
	 * 
	 * @param state The plot state
	 * @param latestCreationDate The latest creation date: Only plots that were created before that date are included.
	 * @param worldID The world UUID
	 * @return A list of plot IDs
	 * @throws Exception Database Exception
	 */
	public List<PlotID> getPlotsByPermission(int state, Date latestCreationDate, UUID worldID) throws Exception
	{
		PreparedStatement statement = null;
		
		try
		{
			refreshDatabaseConnection();
			
			statement = databaseConnection.prepareStatement(
					  "SELECT plot.x, plot.z "
					+ "FROM " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world "
					+ "WHERE plot.world_id = world.id "
					+ "AND plot.state = ? " //1
					+ "AND plot.creation_date < ? " //2
					+ "AND world.uuid = ?"); //3

			
			statement.setInt(1, state);
			statement.setTimestamp(2, new Timestamp(latestCreationDate.getTime()));
			statement.setString(3, worldID.toString());
			
			ResultSet resultSet = statement.executeQuery();
			
			List<PlotID> plotList = new ArrayList<>();
			
			while(resultSet.next()) 
			{
				int x = resultSet.getInt("plot.x");
				int z = resultSet.getInt("plot.x");
				
				plotList.add(new PlotID(x, z, worldID));
			}
			
			return plotList;
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load plot data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
		finally
		{
			statement.close();
		}
	}
	
	//method for plotcheck
	/**
	 * Gets a list of plots with a certain {@link PlotState}.
	 * 
	 * @param state The plot state
	 * @return A list of plot IDs
	 * @throws Exception Database Exception
	 */
	public List<PlotID> getPlotsByState(int state) throws Exception
	{
		PreparedStatement statement = null;
		
		try
		{
			refreshDatabaseConnection();
			
			statement = databaseConnection.prepareStatement(
					  "SELECT world.uuid, plot.x, plot.z "
					+ "FROM " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world "
					+ "WHERE plot.world_id = world.id "
					+ "AND plot.state = ?"); //1


			
			statement.setInt(1, state);
			
			ResultSet resultSet = statement.executeQuery();
			
			List<PlotID> plotList = new ArrayList<>();
			
			while(resultSet.next()) 
			{
				UUID worldID = UUID.fromString(resultSet.getString("world.uuid"));
				
				int x = resultSet.getInt("plot.x");
				int z = resultSet.getInt("plot.x");
				
				plotList.add(new PlotID(x, z, worldID));
			}
			
			return plotList;
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load plot data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
		finally
		{
			statement.close();
		}
	}

	/**
	 * Gets a list of plots in a world where a player has a certain permission.
	 * 
	 * @param playerID The player UUID
	 * @param worldID The world UUID
	 * @param permission The permission
	 * @return  A list of plot IDs
	 */
	public Set<PlotID> getPlotsByPermission(UUID playerID, UUID worldID, String permission)
	{
		// TODO Auto-generated method stub
		return null;
	}
}
