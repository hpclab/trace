package ema.dve.workload;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ema.dve.workload.configuration.PropertiesPersonalized;
import ema.dve.workload.geom.Avatar;
import ema.dve.workload.geom.Entity;
import ema.dve.workload.geom.MapVirtualEnvironment;
import ema.dve.workload.geom.PassiveObject;

/** interfaccia pubblica TODO
 * - public Objects[] getObjectsAt(int time);
 * - public Objects[] getObjectsAt(int time, Object[] filter)
 * - public Entity[] getEntitiesAt(int time)
 * - public Entity[] getEntitiesAt(int time, Entities[] filter)
 */

public class Oracle
{
	private final Map<Integer, MapVirtualEnvironment> data;
	private final Map<Integer, Map<Integer,BandwidthInfo>> bandwidth;
	private final Map<Integer, Map<Integer,Integer>> aoiStat;

	private final PropertiesPersonalized configuration;
	private final Set<Integer> idSet;
	private final Dimension _dimension;

	public Oracle(final PropertiesPersonalized configuration)
	{
		data = new HashMap<Integer, MapVirtualEnvironment>();
		bandwidth = new HashMap<Integer, java.util.Map<Integer,BandwidthInfo>>();
		aoiStat = new HashMap<>();
		idSet = new HashSet<Integer>();
		this.configuration = configuration;
		_dimension = new Dimension(configuration.getPropertyInt("MAP_WIDTH"), configuration.getPropertyInt("MAP_HEIGHT"));
	}

	public void setMapAt(final int time, final MapVirtualEnvironment map)
	{
		// NOTE: make sure you add a copy of the element of the array
		final MapVirtualEnvironment copy = new MapVirtualEnvironment(map);
		data.put(time, copy);

		idSet.addAll(copy.getAllIds());
	}

	/**
	 * Return the map at the given time
	 */
	public MapVirtualEnvironment getMapAt(final int time)
	{
		return data.get(time);
	}

	public int getIterations()
	{
		return data.size();
	}

	public PropertiesPersonalized getConfiguration()
	{
		return this.configuration;
	}

	public Set<Integer> getIds()
	{
		return idSet;
	}

	public Set<Integer> getAvatarIds()
	{
		final HashSet<Integer> aids = new HashSet<Integer>();
		for (final Integer i: idSet)
		{
			if ((i+"").startsWith("1"))
			{
				aids.add(i);
			}
		}
		return aids;
	}

	public void setBandAt(final int time, final java.util.Map<Integer,BandwidthInfo> info)
	{
		bandwidth.put(time, info);
	}

	public void setAOIStatAt(final int time, final java.util.Map<Integer,Integer> info)
	{
		aoiStat.put(time, info);
	}

	public java.util.Map<Integer,BandwidthInfo> getBandAt(final int time)
	{
		return bandwidth.get(time);
	}

	public java.util.Map<Integer,Integer> getAOIStatAt(final int time)
	{
		return aoiStat.get(time);
	}

	// **** UTILITY METHODS BELOW ****

	/*
	 * Returns the entities that are in the AOI of the given
	 * avatar; it does not count the avatar itself.
	*/
	public List<Entity> getEntitiesAOI(final Avatar avatar, final int iteration)
	{
		final List<Entity> list = new ArrayList<Entity>();
		final Collection<Avatar> avatars = getMapAt(iteration).getAllAvatar();
		final PassiveObject[] objects = getMapAt(iteration).getObjects();

		for (final Avatar a: avatars)
		{
			// check if it is not the caller avatar
			if ((avatar.equals(a) == false) && (avatar.getAOI().contains(a.getPositionOutput(_dimension))))
			{
				list.add(a);
			}
		}

		for (final PassiveObject o: objects)
		{
			if (avatar.getAOI().contains(o.getPosition()))
			{
				list.add(o);
			}
		}

		return list;
	}

	/*
	 * Returns the Avatar corresponding to the id
	 * at the give iteration
	 */
	public Avatar getAvatarFromId(final int id, final int iteration)
	{
		final Collection<Avatar> avatars = getMapAt(iteration).getAllAvatar();
		for (final Avatar a: avatars)
		{
			if (a.getID() == id)
			{
				return a;
			}
		}

		return null;
	}
}
