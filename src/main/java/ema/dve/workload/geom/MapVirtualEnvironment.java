package ema.dve.workload.geom;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.rits.cloning.Cloner;

import ema.dve.workload.configuration.PropertiesPersonalized;

public class MapVirtualEnvironment
{
	private final Dimension map_size;
	private final Shape myShape;
	private final double aoi_radius;

	private Hotspot[] hotspots;
	private final Map<Integer, Avatar> _avatarMap;

	private PassiveObject[] objects;


	private MapVirtualEnvironment(final Dimension map_size, final double aoi_radius)
	{
		this.aoi_radius = aoi_radius;
		this.map_size = map_size;
		this.myShape = new Rectangle2D.Double(0, 0, map_size.getWidth(), map_size.getHeight());

		this._avatarMap = new HashMap<>();
	}

	public MapVirtualEnvironment(final PropertiesPersonalized conf)
	{
		this(new Dimension(conf.getPropertyInt("MAP_WIDTH"),
				conf.getPropertyInt("MAP_HEIGHT")),
				conf.getPropertyDouble("AOI_RADIUS"));
	}

	/**
	 * Copy constructor
	 */
	public MapVirtualEnvironment(final MapVirtualEnvironment map)
	{
		this.aoi_radius = map.aoi_radius;
		this.map_size = map.map_size;
		this.myShape = new Rectangle2D.Double(0, 0, map.getDimension().getWidth(), map.getDimension().getHeight());

		final Cloner cloner= new Cloner();

		this._avatarMap = cloner.deepClone(map._avatarMap); //ListUtils.copyOf(map.avatars);
		this.hotspots = cloner.deepClone(map.hotspots); //Arrays.copyOf(map.hotspots, map.hotspots.length);
		this.objects = cloner.deepClone(map.objects); //Arrays.copyOf(map.objects, map.objects.length);
	}


	public Dimension getDimension()
	{
		return this.map_size;
	}

	public Hotspot[] getHotspots()
	{
		return hotspots;
	}

	public Point2D.Double getPosition(final Entity e_)
	 {
		 return getPosition(e_.getPosition());
	 }

	public Point2D.Double getPosition(final Point2D.Double e_)
	 {
		 return new Point2D.Double((((e_.x % map_size.getWidth())+map_size.getWidth())%map_size.getWidth()),
				 (((e_.y % map_size.getHeight())+map_size.getHeight())%map_size.getHeight()));
	 }

	public void setHotspots(final Hotspot[] hotspots)
	{
		this.hotspots = hotspots;
	}

	public void setAvatars(final Avatar[] avatars_)
	{
		for(final Avatar a : avatars_)
		{
			_avatarMap.put(a.getID(), a);
		}
	}

	public void setAvatars(final Entity[] ents)
	{
		final List<Avatar> list = new ArrayList<Avatar>();
		for (final Entity e: ents)
		{
			_avatarMap.put(e.getID(), new Avatar(e.getID(), e.getPosition(), aoi_radius));
		}
	}

	public Collection<Avatar> getAllAvatar()
	{
		return _avatarMap.values();
	}

	public Avatar getAvatar(final int id_)
	{
		return _avatarMap.get(id_);
	}

	public PassiveObject[] getObjects()
	{
		return this.objects;
	}

	public void setObjects(final PassiveObject[] objects)
	{
		this.objects = objects;
	}

	public Shape myShape()
	{
		return myShape;
	}

	public Set<Integer> getAllIds()
	{
		final Set<Integer> set = new HashSet<Integer>();

		for (final Avatar a: _avatarMap.values())
		{
			set.add(a.getID());
		}

		for (final Hotspot h: hotspots)
		{
			set.add(h.getID());
		}

		for (final PassiveObject o: objects)
		{
			set.add(o.getID());
		}

		return set;
	}

}
