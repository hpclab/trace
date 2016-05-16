package ema.dve.workload;

import java.awt.geom.Point2D;

import ema.dve.workload.configuration.PropertiesPersonalized;
import ema.dve.workload.geom.Entity;
import ema.dve.workload.geom.Hotspot;
import ema.dve.workload.geom.IdProvider;
import ema.dve.workload.geom.MapVirtualEnvironment;
import ema.dve.workload.mobility.PlacementUtils;

public class RandomHotspotPlacement extends AStaticPlacement
{
	private final int hotspotNumber;
	private final double hotspotRadius;

	public RandomHotspotPlacement(final PropertiesPersonalized conf, final IdProvider idProvider)
	{
		super(conf, idProvider);

		this.hotspotNumber = conf.getPropertyInt("HOTSPOT_NUM");
		this.hotspotRadius = conf.getPropertyInt("HOTSPOT_RADIUS");
	}

	@Override
	public Entity[] place(final MapVirtualEnvironment map)
	{
		final Hotspot[] hotspots = new Hotspot[hotspotNumber];

		// create and place the hotspots
		for (int i=0; i<hotspotNumber; i++)
		{
			hotspots[i] = new Hotspot(idProvider.addOne(),
					placeHotspot(map), hotspotRadius);
		}

		return hotspots;
	}

	/**
	 * Place a single hotspot in the map
	 * @param map
	 * @return
	 */
	private Point2D.Double placeHotspot(final MapVirtualEnvironment map)
	{
		boolean contains = false;
		Point2D.Double candidate = null;

		while (contains == false)
		{
			candidate = PlacementUtils.randomPoint(map);
			final double x_max = candidate.x + hotspotRadius;
			final double x_min = candidate.x - hotspotRadius;
			final double y_max = candidate.y + hotspotRadius;
			final double y_min = candidate.y - hotspotRadius;

			contains = map.myShape().contains(x_max, y_max) && map.myShape().contains(x_min, y_min);
		}

		return candidate;
	}
}
