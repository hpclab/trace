package ema.dve.workload.mobility;

import java.awt.geom.Point2D;
import java.util.Random;

import ema.dve.workload.RandomSingleton;
import ema.dve.workload.geom.Hotspot;
import ema.dve.workload.geom.MapVirtualEnvironment;

public class PlacementUtils
{
	protected static Random r = RandomSingleton.getRandom();

	public static Point2D.Double randomPoint(final MapVirtualEnvironment map)
	{
		final double x = new Double(r.nextInt(map.getDimension().width)).intValue();
		final double y = new Double(r.nextInt(map.getDimension().height)).intValue();

		return new Point2D.Double(x,y);
	}

	public static Point2D.Double pointInHotspotProbability(final MapVirtualEnvironment map, final double probability_)
	{
		final Random r = RandomSingleton.getRandom();
		final boolean hs = r.nextDouble() < probability_;

		if (hs == false)
		{
			return PlacementUtils.randomPoint(map);
		} else
		{
			final int index = r.nextInt(map.getHotspots().length);
			final Point2D.Double offset = chooseOffsetInsideHotspot(map.getHotspots()[index]);

			final double x = map.getHotspots()[index].getPosition().x + offset.x;
			final double y = map.getHotspots()[index].getPosition().y + offset.y;

			return new Point2D.Double(x,y);
		}
	}

	public static Point2D.Double chooseOffsetInsideHotspot(final Hotspot hotspot)
	{
		// choose an angle (uniformly)
		final double angle = RandomSingleton.getRandom().nextDouble() * Math.PI * 2;

		final double length = r.nextInt((int)hotspot.getRadius());
		// choose a length between 0 and one(zipf)

		// compute!
		final double x = Math.cos(angle) * hotspot.getRadius() * length;
		final double y = Math.sin(angle) * hotspot.getRadius() * length;

		// consider the point to add as offset
		return new Point2D.Double(x,y);
	}
}
