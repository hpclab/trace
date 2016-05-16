package ema.dve.workload.mobility.random;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import ema.dve.workload.RandomSingleton;
import ema.dve.workload.configuration.PropertiesPersonalized;
import ema.dve.workload.geom.Avatar;
import ema.dve.workload.geom.MapVirtualEnvironment;
import ema.dve.workload.mobility.AMobilityModel;
import ema.dve.workload.mobility.Direction;
import ema.dve.workload.mobility.PlacementUtils;

public class RandomWalk extends AMobilityModel
{
	private final Map<Avatar, Direction> directions;

	private final double max_velocity;

	public RandomWalk(final PropertiesPersonalized configuration)
	{
		super(configuration);
		directions = new ConcurrentHashMap<>();
		this.max_velocity = Double.parseDouble(configuration.getProperty("VELOCITY_MAX"));
	}

	@Override
	public void move(final int time, final MapVirtualEnvironment map, final ExecutorService ex_)
	{
		final List<Future<Integer>> w = new ArrayList<>();

		for (final Avatar a: map.getAllAvatar())
		{
			w.add(ex_.submit(new Callable<Integer>()
			{

				@Override
				public Integer call() throws Exception
				{
					Point2D.Double origin = null;

					final Direction dir = directions.get(a);
					if (dir == null)
					{
						origin = PlacementUtils.randomPoint(map);
					} else
					{
						origin = directions.get(a).getPositionAt(new Double(time));
					}

					// choose a new arrival point
					// NOTE: may be not correct. The best would be to use vectors

					final Point2D.Double destination = PlacementUtils.randomPoint(map);
					final boolean vertical = RandomSingleton.getRandom().nextBoolean();
					final boolean horizontal = RandomSingleton.getRandom().nextBoolean();

					if (vertical)
					{
						destination.y += map.getDimension().getHeight() * 2d;
					} else
					{
						destination.y -= map.getDimension().getHeight() * 2d;
					}

					if (horizontal)
					{
						destination.x += map.getDimension().getWidth() * 2d;
					} else
					{
						destination.x -= map.getDimension().getWidth() * 2d;
					}

					// choose a new velocity
					final double velocity = 0.1 + ((max_velocity - 0.1) * RandomSingleton.getRandom().nextDouble());

					// create the new direction
					final Direction direction = new Direction(origin, destination, time, velocity);

					// if the point is outside the boundaries of the map, rebound
					// TOOD: the definition on the paper is not clear, check other sources

					// update the position of the avatars
					a.setPosition(direction.getPositionAt(new Double(time)));

					// update the registry of the directions
					directions.put(a, direction);

					return 0;
				}
			}));
		}

		for(final Future<Integer> f : w)
		{
			try
			{
				f.get();
			} catch (final InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final ExecutionException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}


	@Override
	public String getName()
	{
		return "RandomWalk";
	}
}
