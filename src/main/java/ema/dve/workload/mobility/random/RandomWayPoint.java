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

/**
 * This class implements the random way point model as described in the paper:
 * A SURVEY OF MOBILITY MODELS in Wireless Adhoc Networks, Fan Bai and Ahmed Helmy
 * @author emanuele
 *
 */
public class RandomWayPoint extends AMobilityModel
{
	private Map<Avatar, Direction> directions;
	private final Map<Avatar, Integer> waitUntil;

	private final double max_velocity;
	private final int pause_time;

	private final double MIN_VEL = 0.1;

	public RandomWayPoint(final PropertiesPersonalized configuration)
	{
		super(configuration);
		directions = new ConcurrentHashMap<>();
		waitUntil = new ConcurrentHashMap<>();

		this.max_velocity = Double.parseDouble(configuration.getProperty("VELOCITY_MAX"));
		this.pause_time = Integer.parseInt(configuration.getProperty("PAUSE_TIME"));
	}

	@Override
	public void move(final int time, final MapVirtualEnvironment map, final ExecutorService ex_)
	{
		final Map<Avatar, Direction> new_directions = new ConcurrentHashMap<>();
		final List<Future<Integer>> w = new ArrayList<>();

		for (final Avatar a: map.getAllAvatar())
		{
			w.add(ex_.submit(new Callable<Integer>()
			{

				@Override
				public Integer call() throws Exception
				{
					final Direction direction = directions.get(a);

					// no direction for the avatars
					if (direction == null)
					{
						// choose a random place in the map
						final Point2D.Double origin = PlacementUtils.randomPoint(map);
						final Point2D.Double destination = PlacementUtils.randomPoint(map);

						// create the new direction
						// NOTE: here we consider a min velocity of 0.1
						final double velocity = MIN_VEL + ((max_velocity - MIN_VEL) * RandomSingleton.getRandom().nextDouble());
						final Direction new_dir = new Direction(origin, destination, time, velocity);
						new_directions.put(a, new_dir);

						//update the map
						a.setPosition(origin);
					}
					// reached the destination
					else if ((direction.getIssueTime() + direction.getTravelTime()) < time)
					{
						final Point2D.Double current = direction.getPositionAt(new Double(time));
						final double velocity = MIN_VEL + ((max_velocity - MIN_VEL) * RandomSingleton.getRandom().nextDouble());
						Direction next = null;

						final Integer wait_until = waitUntil.get(a);
						if ((wait_until == null) || (wait_until < time))
						{
							// issue a new waiting time
							waitUntil.put(a, time + pause_time);
							next = new Direction(current, current, time, velocity);

							//System.out.println(time+ " Issuead a new waiting time: "+(time + pause_time));
						}
						else if (wait_until > time)
						{
							// do nothing, just stay in place
							next = new Direction(current, current, time, velocity);

							//System.out.println(time+" waiting for the pause time to finish: "+wait_until);
						}
						else if (wait_until == time)
						{
							// compute a new direction
							final Point2D.Double destination = PlacementUtils.randomPoint(map);

							// create the new direction
							next = new Direction(current, destination, time, velocity);

							//System.out.println(time+" waiting time finished. im moving.");
						}

						//update the map
						a.setPosition(current);

						// update directions map
						new_directions.put(a, next);
					}
					// still moving toward destination
					else
					{
						// just update the position on the map
						a.setPosition(direction.getPositionAt(new Double(time)));

						// keep the old direction
						new_directions.put(a, direction);
					}
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

		directions = new_directions;
	}


	@Override
	public String getName()
	{
		return "RandomWayPoint";
	}
}
