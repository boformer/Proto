package com.github.boformer.proto.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections4.map.LRUMap;

import com.github.boformer.proto.ProtoPlugin;
import com.github.boformer.proto.access.PlayerPlotAccess;
import com.github.boformer.proto.access.PlayerWorldAccess;
import com.github.boformer.proto.config.PluginConfig;

/**
 * Provides and manages the data of the plugin.
 * 
 * <p>The data is stored in a database.</p>
 * 
 * <p>The data is modified by the plugin at runtime (e.g. table of plots), 
 * while the configuration managed by {@link com.github.boformer.proto.config.ConfigManager} should only be edited by the server admin (e.g. plot size).</p>
 */
public class DataManager
{
	//TODO don't print stack trace/warning message when exception is passed up
	
	private final ProtoPlugin plugin;
	
	private String databaseTablePrefix;
	private Connection databaseConnection = null;
	
	private LRUMap<PlotID, PlotData> plotDataCache;
	private LRUMap<PlayerPlotAccess, Boolean> playerPlotAccessCache;
	private LRUMap<PlayerWorldAccess, Boolean> playerWorldAccessCache;
	
	//Prepared Statements Array. Statements can be re-used while the connection is open
	private PreparedStatement statements[] = new PreparedStatement[13];
	
	/** 
	 * <i>Internal constructor: Create a new data manager.</i>
	 * 
	 * @param plugin The plugin
	 */
	public DataManager(ProtoPlugin plugin)
	{
		this.plugin = plugin;
	}
	
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
		for(PreparedStatement stmt : statements) 
		{
			if(stmt != null) stmt.close();
		}
		
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
	 * Gets the data of a world.
	 * @param worldName The world name
	 * @return The world data
	 * @throws Exception Database exception
	 */
	public WorldData getWorldData(String worldName) throws Exception
	{
		return new WorldData(worldName);
		
		//TODO right now there is no data to fetch from db
		/*
		try
		{
			refreshDatabaseConnection();
			
			PreparedStatement statement = databaseConnection.prepareStatement(
					  "SELECT <idk> " 
					+ "FROM " + databaseTablePrefix + "worlds world "
					+ "WHERE world.name = ?"); //1
			
			statement.setString(1, worldName.toString());
			
			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.next()) 
			{
				WorldData worldData = new WorldData(worldName);
				
				String <idk> = resultSet.getString(<idk>");
				
				worldData.set<idk>(<idk>);
				
				
				return worldData;
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
		*/
		
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
			
			if(statements[0] == null || statements[0].isClosed()) 
			{
				statements[0] = databaseConnection.prepareStatement(
						  "SELECT name "
						+ "FROM " + databaseTablePrefix + "players player"
						+ "WHERE player.uuid = ?"); //1
			}
			
			statements[0].setString(1, playerID.toString());
			
			ResultSet resultSet = statements[0].executeQuery();
			
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
			
			if(statements[1] == null || statements[1].isClosed()) 
			{
				statements[1] = databaseConnection.prepareStatement(
						  "SELECT plot.name, plot.state, plot.creation_date, plot.last_mod_date "
						+ "FROM " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world "
						+ "WHERE plot.world_id = world.id "
						+ "AND world.name = ? " //1
						+ "AND plot.x = ? " //2
						+ "AND plot.z = ?"); //3
			}
			
			statements[1].setString(1, plotID.getWorldName());
			statements[1].setInt(2, plotID.getX());
			statements[1].setInt(3, plotID.getZ());

			ResultSet resultSet = statements[1].executeQuery();
			
			if(resultSet.next()) 
			{
				PlotData plotData = new PlotData(plotID);
				
				plotData.setName(resultSet.getString("plot.name"));
				plotData.setState(PlotState.byId(resultSet.getInt("plot.state")));
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
			
			if(statements[2] == null || statements[2].isClosed()) 
			{
				statements[2] = databaseConnection.prepareStatement(
						  "SELECT group.name "
						+ "FROM " + databaseTablePrefix + "groups group"
						+ "WHERE group.id = ?"); //1
			}
			
			statements[2].setInt(1, groupID);
			
			ResultSet resultSet = statements[2].executeQuery();
			
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
	 * @param worldName The world name
	 * @param permission The permission
	 * @return {@code true} if player has permission, {@code false} if not
	 * @throws Exception Database Exception
	 */
	public boolean hasWorldAccess(UUID playerID, String worldName, String permission) throws Exception
	{
		PlayerWorldAccess access = new PlayerWorldAccess(playerID, worldName, permission);
		
		//first check cache
		if(playerWorldAccessCache.containsKey(access)) 
		{
			return playerWorldAccessCache.get(access);
		}
		
		try
		{
			refreshDatabaseConnection();
			
			if(statements[3] == null || statements[3].isClosed()) 
			{
				statements[3] = databaseConnection.prepareStatement(
						  "SELECT 1 "
						+ "FROM " + databaseTablePrefix + "player_world_access access, " + databaseTablePrefix + "players player, " + databaseTablePrefix + "worlds world "
						+ "WHERE access.world_id = world.id "
						+ "AND access.player_id = player.id "
						+ "AND player.uuid = ? " //1
						+ "AND world.name = ? " //2
						+ "AND access.permission = ?"); //3
			}

			statements[3].setString(1, playerID.toString());
			statements[3].setString(2, worldName);
			statements[3].setString(3, permission);

			ResultSet resultSet = statements[3].executeQuery();
			
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
			
			if(statements[4] == null || statements[4].isClosed()) 
			{
				statements[4] = databaseConnection.prepareStatement(
						  "SELECT 1 "
						+ "FROM " + databaseTablePrefix + "player_plot_access access, " + databaseTablePrefix + "players player, " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world "
						+ "WHERE access.plot_id = plot.id "
						+ "AND access.player_id = player.id "
						+ "AND player.uuid = ? " //1
						+ "AND plot.world_id = world.id "
						+ "AND world.name = ? " //2
						+ "AND plot.x = ? " //3
						+ "AND plot.z = ? " //4
						+ "AND access.permission = ?"); //5
			}
		
			statements[4].setString(1, playerID.toString());
			statements[4].setString(2, plotID.getWorldName());
			statements[4].setInt(3, plotID.getX());
			statements[4].setInt(4, plotID.getZ());
			statements[4].setString(5, permission);

			ResultSet resultSet = statements[4].executeQuery();
			
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
	
	/**
	 * Gets a list of player names with a certain permission in a plot.
	 * 
	 * @param plotID The plot ID
	 * @param permission The permission
	 * @return A list of player names
	 * @throws Exception Database Exception
	 */
	public List<String> getPlayerNamesByPlotPermission(PlotID plotID, String permission) throws Exception
	{

		try
		{
			refreshDatabaseConnection();
			
			if(statements[5] == null || statements[5].isClosed()) 
			{
				statements[5] = databaseConnection.prepareStatement(
						  "SELECT DISTINCT player.name"
						+ "FROM " + databaseTablePrefix + "player_plot_access access, " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "players player, " + databaseTablePrefix + "worlds world "
						+ "WHERE plot.world_id = world.id "
						+ "AND plot.id = access.plot_id "
						+ "AND player.id = access.player_id "
						+ "AND plot.x = ? " //1
						+ "AND plot.z = ? " //2
						+ "AND world.name = ? " //3
						+ "AND access.permission = ? "); //4
			}

			statements[5].setInt(1, plotID.getX());
			statements[5].setInt(2, plotID.getZ());
			statements[5].setString(3, plotID.getWorldName());
			statements[5].setString(4, permission);

			
			ResultSet resultSet = statements[5].executeQuery();
			
			List<String> playerNameList = new ArrayList<>();
			
			while(resultSet.next()) 
			{
				playerNameList.add(resultSet.getString("player.name"));
			}
			
			return playerNameList;
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load player list: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}
	
	/**
	 * Gets a list of player names with a certain permission in a world.
	 * 
	 * @param worldName The world name
	 * @param permission The permission
	 * @return A list of player names
	 * @throws Exception Database Exception
	 */
	public List<String> getPlayerNamesByWorldPermission(String worldName, String permission) throws Exception
	{
		try
		{
			refreshDatabaseConnection();
			
			if(statements[6] == null || statements[6].isClosed()) 
			{
				statements[6] = databaseConnection.prepareStatement(
						  "SELECT DISTINCT player.name"
						+ "FROM " + databaseTablePrefix + "player_world_access access, " + databaseTablePrefix + "players player, " + databaseTablePrefix + "worlds world "
						+ "WHERE world.id = access.world_id "
						+ "AND player.id = access.player_id "
						+ "AND world.name = ? " //1
						+ "AND access.permission = ? "); //2
			}
					
			statements[6].setString(1, worldName);
			statements[6].setString(2, permission);

			
			ResultSet resultSet = statements[6].executeQuery();
			
			List<String> playerNameList = new ArrayList<>();
			
			while(resultSet.next()) 
			{
				playerNameList.add(resultSet.getString("player.name"));
			}
			
			return playerNameList;
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load player list: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}
		
		
	

	//method for plotcheck
	/**
	 * Gets a list of plots with a certain {@link PlotState}, plot creation date and world name.
	 * 
	 * @param state The plot state
	 * @param latestCreationDate The latest creation date: Only plots that were created before that date are included.
	 * @param worldName The world name
	 * @return A list of plot IDs
	 * @throws Exception Database Exception
	 */
	public List<PlotID> getPlotsByLatestCreationDate(PlotState state, Date latestCreationDate, String worldName) throws Exception
	{
		try
		{
			refreshDatabaseConnection();
			
			if(statements[7] == null || statements[7].isClosed()) 
			{
				statements[7] = databaseConnection.prepareStatement(
						  "SELECT DISTINCT plot.x, plot.z "
						+ "FROM " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world "
						+ "WHERE plot.world_id = world.id "
						+ "AND plot.state = ? " //1
						+ "AND plot.creation_date < ? " //2
						+ "AND world.name = ?"); //3
			}
			
			statements[7].setInt(1, state.getId());
			statements[7].setTimestamp(2, new Timestamp(latestCreationDate.getTime()));
			statements[7].setString(3, worldName);
			
			ResultSet resultSet = statements[7].executeQuery();
			
			List<PlotID> plotList = new ArrayList<>();
			
			while(resultSet.next()) 
			{
				int x = resultSet.getInt("plot.x");
				int z = resultSet.getInt("plot.x");
				
				plotList.add(new PlotID(x, z, worldName));
			}
			
			return plotList;
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load plot data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}
	
	//method for plotcheck

	/**
	 * Gets a list of plots with a certain {@link PlotState}, a latest login of a player with a certain permission (e.g. owner) and world name.
	 * 
	 * @param state The plot state
	 * @param latestPlayerLoginDate The player login date: Only plots of players who did not log in after that date are included.
	 * @param permission The permission
	 * @param worldName The world name
	 * @return A list of plot IDs
	 * @throws Exception Database Exception
	 */
	public List<PlotID> getPlotsByLatestPlayerLoginDate(PlotState state, Date latestPlayerLoginDate, String permission, String worldName) throws Exception
	{
		try
		{
			refreshDatabaseConnection();
			
			if(statements[8] == null || statements[8].isClosed()) 
			{
				statements[8] = databaseConnection.prepareStatement(
						  "SELECT DISTINCT plot.x, plot.z "
						+ "FROM " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world, " + databaseTablePrefix + "player_plot_access access, " + databaseTablePrefix + "players player "
						+ "WHERE plot.world_id = world.id "
						+ "AND plot.state = ? " //1
						+ "AND plot.id = access.plot_id "
						+ "AND player.id = access.player_id "
						+ "AND player.last_login_date < ? " //2
						+ "AND access.permission = ? " //3
						+ "AND world.name = ?"); //4
			}
			
			statements[8].setInt(1, state.getId());
			statements[8].setTimestamp(2, new Timestamp(latestPlayerLoginDate.getTime()));
			statements[8].setString(3, permission);
			statements[8].setString(4, worldName);
			
			ResultSet resultSet = statements[8].executeQuery();
			
			List<PlotID> plotList = new ArrayList<>();
			
			while(resultSet.next()) 
			{
				int x = resultSet.getInt("plot.x");
				int z = resultSet.getInt("plot.x");
				
				plotList.add(new PlotID(x, z, worldName));
			}
			
			return plotList;
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load plot data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}
	
	//method for plotcheck
	/**
	 * Gets a list of plots with a certain {@link PlotState}, a certain modification date and world name.
	 * 
	 * @param state The plot state
	 * @param latestModificationDate The latest modification date: Only plots that were last modified before that date are included.
	 * @param worldName The world name
	 * @return A list of plot IDs
	 * @throws Exception Database Exception
	 */
	public List<PlotID> getPlotsByLatestModificationDate(PlotState state, Date latestModificationDate, String worldName) throws Exception
	{
		try
		{
			refreshDatabaseConnection();
			
			if(statements[9] == null || statements[9].isClosed()) 
			{
				statements[9] = databaseConnection.prepareStatement(
						  "SELECT DISTINCT plot.x, plot.z "
						+ "FROM " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world "
						+ "WHERE plot.world_id = world.id "
						+ "AND plot.state = ? " //1
						+ "AND plot.modification_date < ? " //2
						+ "AND world.name = ?"); //3
			}
			
			statements[9].setInt(1, state.getId());
			statements[9].setTimestamp(2, new Timestamp(latestModificationDate.getTime()));
			statements[9].setString(3, worldName);
			
			ResultSet resultSet = statements[9].executeQuery();
			
			List<PlotID> plotList = new ArrayList<>();
			
			while(resultSet.next()) 
			{
				int x = resultSet.getInt("plot.x");
				int z = resultSet.getInt("plot.x");
				
				plotList.add(new PlotID(x, z, worldName));
			}
			
			return plotList;
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load plot data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
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
	public List<PlotID> getPlotsByState(PlotState state) throws Exception
	{
		try
		{
			refreshDatabaseConnection();
			
			if(statements[10] == null || statements[10].isClosed()) 
			{
				statements[10] = databaseConnection.prepareStatement(
						  "SELECT DISTINCT world.name, plot.x, plot.z "
						+ "FROM " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world "
						+ "WHERE plot.world_id = world.id "
						+ "AND plot.state = ?"); //1
			}
			
			statements[10].setInt(1, state.getId());
			
			ResultSet resultSet = statements[10].executeQuery();
			
			List<PlotID> plotList = new ArrayList<>();
			
			while(resultSet.next()) 
			{
				String worldName = resultSet.getString("world.name");
				
				int x = resultSet.getInt("plot.x");
				int z = resultSet.getInt("plot.x");
				
				plotList.add(new PlotID(x, z, worldName));
			}
			
			return plotList;
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load plot data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}

	/**
	 * Gets a list of plots in a world where a player has a certain permission.
	 * 
	 * @param playerID The player UUID
	 * @param worldName The world name
	 * @param permission The permission
	 * @return  A list of plot IDs
	 * @throws Exception Database Exception
	 */
	public List<PlotID> getPlotsByPermission(UUID playerID, String worldName, String permission) throws Exception
	{
		try
		{
			refreshDatabaseConnection();
			
			if(statements[11] == null || statements[11].isClosed()) 
			{
				statements[11] = databaseConnection.prepareStatement(
						  "SELECT DISTINCT plot.x, plot.z "
						+ "FROM " + databaseTablePrefix + "plots plot, " + databaseTablePrefix + "worlds world, " + databaseTablePrefix + "player_plot_access access, " + databaseTablePrefix + "players player "
						+ "WHERE plot.world_id = world.id "
						+ "AND plot.id = access.plot_id "
						+ "AND player.id = access.player_id "
						+ "AND player.uuid = ? " //1
						+ "AND world.name = ? " //2
						+ "AND access.permission = ? "); //3
			}
			
			statements[11].setString(1, playerID.toString());
			statements[11].setString(2, worldName);
			statements[11].setString(3, permission);
			
			ResultSet resultSet = statements[11].executeQuery();
			
			List<PlotID> plotList = new ArrayList<>();
			
			while(resultSet.next()) 
			{
				int x = resultSet.getInt("plot.x");
				int z = resultSet.getInt("plot.x");
				
				plotList.add(new PlotID(x, z, worldName));
			}
			
			return plotList;
		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to load plot data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
	}

	public void preparePlotsForDeletion(List<PlotID> plotList) throws Exception
	{
		for(PlotID plotID : plotList) 
		{
			PlotData plotData = null;
			
			try
			{
				plotData = getPlotData(plotID);
			}
			catch (Exception e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if(plotData != null) 
			{
				plotData.setState(PlotState.LOCKED_FOR_DELETION);
				plotData.setName(null);
			}
			
			save(plotData);
			
			//remove access
			removeAllPlotAccess(plotID);
		}
	}
	
	private void save(PlotData plotData)
	{
		// TODO Auto-generated method stub
		
	}

	public void removeAllPlotAccess(PlotID plotID) throws Exception
	{
		try
		{
			refreshDatabaseConnection();
			
			if(statements[12] == null || statements[12].isClosed()) 
			{
				//TODO
				statements[12] = databaseConnection.prepareStatement(
						  "DELETE "
						+ "FROM " + databaseTablePrefix + "player_plot_access access"
						+ "JOIN " + databaseTablePrefix + "plots plot"
						+ "ON access.plot_id = plot.id"
						+ "WHERE plot.world_id = ("
							+ "SELECT DISTINCT world.id from " + databaseTablePrefix + "worlds world"
							+ "WHERE world.name = ?" //1
						+ ") "
						+ "AND plot.x = ? " //2
						+ "AND plot.z = ? "); //3
			}
		
			statements[4].setString(1, plotID.getWorldName());
			statements[4].setInt(2, plotID.getX());
			statements[4].setInt(3, plotID.getZ());

			statements[12].execute();

		}
		catch(Exception e)
		{
			plugin.getLogger().error("Unable to delete access data: " + e.getMessage());
			e.printStackTrace();
			
			throw e;
		}
		
		//remove access from cache
		for(PlayerPlotAccess access : playerPlotAccessCache.keySet()) 
		{
			if(access.getPlotID().equals(plotID)) playerPlotAccessCache.put(access, false);
		}
	}

	public void removePlotData(PlotID plotID)
	{
		// TODO Auto-generated method stub
	}
}
