package ema.dve.workload.mobility.latp;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
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
import ema.dve.workload.geom.Hotspot;
import ema.dve.workload.geom.MapVirtualEnvironment;
import ema.dve.workload.mobility.AMobilityModel;
import ema.dve.workload.mobility.Direction;

/**
 * This class implements the LAPT model, as defined in the paper:
 * LEE, Kyunghan, et al. Slaw: A new mobility model for human walks.
 * In: INFOCOM 2009, IEEE. IEEE, 2009. p. 855-863.
 *
 * @author carlini
 *
 */

// ---***---
// TODO: we have to makw sure we have at least ONE hotspot in this model
// ***---***

public class Lapt extends AMobilityModel
{
	// here saves the list of _visited_ hotspot for each avatar
	private final Map<Avatar, List<Hotspot>> visited;
	private final Map<Avatar, Hotspot> lastVisited;

	// directions for avatars
	private Map<Avatar, Direction> directions;

	private final double alpha;
	private final double velocity;

	public Lapt(final PropertiesPersonalized configuration)
	{
		super(configuration);
		this.alpha = Double.parseDouble(configuration.getProperty("ALPHA"));
		this.velocity = Double.parseDouble(configuration.getProperty("VELOCITY"));

		visited = new ConcurrentHashMap<>();
		directions = new ConcurrentHashMap<>();
		lastVisited = new ConcurrentHashMap<>();
	}

	@Override
	public void move(final int time, final MapVirtualEnvironment map, final ExecutorService ex_)
	{
		final List<Future<Integer>> w = new ArrayList<>();

		// here saves new direction for the avatars
		final Map<Avatar, Direction> new_directions = new ConcurrentHashMap<>();

		for (final Avatar a: map.getAllAvatar())
		{
			w.add(ex_.submit(new Callable<Integer>()
			{

				@Override
				public Integer call() throws Exception
				{
					final Direction direction = directions.get(a);

					// first time (no direction ever selected)
					if (direction == null)
					{
						// choose a random waypoint at start
						// TODO: this maybe not correct with the assumption of the original paper
						final int index = RandomSingleton.getRandom().nextInt(map.getHotspots().length);
						final Hotspot starting = map.getHotspots()[index];

						final Direction next = new Direction(starting.getPosition(), starting.getPosition(), time, velocity);
						new_directions.put(a, next);

						// update the map
						a.setPosition(starting.getPosition());

						// update visited hotspot
						addHotspotToVisited(a, starting);
					}
					// destination is reached
					else if ((direction.getArrivalTime() < time) || (direction.getTravelTime() == 0))
					{
						// --- choosing a new direction ---
						// 1. Take unvisited hotspot for a
						final List<Hotspot> unvisited = new ArrayList<Hotspot>();
						for (final Hotspot h: map.getHotspots())
						{
							if (visited.get(a).contains(h) == false)
							{
								unvisited.add(h);
							}
						}

						// if no more hotspot to visit, start over
						if (unvisited.size() == 0)
						{
							final List<Hotspot> list = visited.get(a);
							list.clear();
							list.add(lastVisited.get(a));

							final Direction next = new Direction(lastVisited.get(a).getPosition(), lastVisited.get(a).getPosition(), time, velocity);
							new_directions.put(a, next);

						} else
						{

							// 2. Calculate the "visiting factor" for each unvisited vertex
							// the visiting factor is computed as (1/distance)^alpha
							final Point2D.Double current_position = direction.getPositionAt(time);
							final HashMap<Hotspot, Double> factors = new HashMap<Hotspot, Double>();

							for (final Hotspot h: unvisited)
							{
								final Double distance = h.getPosition().distance(current_position);
								final Double factor = Math.pow(1/distance, alpha);
								factors.put(h, factor);
							}

							// 3. Calculate the visiting probability for each unvisited vertex
							final HashMap<Hotspot, Double> probabilities = new HashMap<Hotspot, Double>();

							for (final Hotspot h: unvisited)
							{
								Double factorSum = 0d;
								for (final Hotspot h2: unvisited)
								{
									if (h.equals(h2) == false)
									{
										factorSum += factors.get(h2);
									}
								}

								final Double probability = factors.get(h) / factorSum;
								probabilities.put(h, probability);
							}

							// 4. choose according probabilities
							final double rand = RandomSingleton.getRandom().nextDouble();
							double cumulativeProbability = 0.0;
							Hotspot selected = null;

							for (final Hotspot h : unvisited)
							{
							    cumulativeProbability += probabilities.get(h);
							    if ((rand <= cumulativeProbability) && (probabilities.get(h) != 0))
							    {
							        selected = h;
							        break;
							    }
							}

							// 5. Add the vertex to visited
							addHotspotToVisited(a, selected);

							// 6. Compute the new direction according to the hotspot
							final Direction next = new Direction(current_position, selected.getPosition(), time, velocity);

							// 7. Update direction and avatar position
							a.setPosition(next.getPositionAt(time));
							new_directions.put(a, next);
						}
					}
					// still moving toward destination
					else
					{
						// just update the position on the map
						a.setPosition(direction.getPositionAt(time));

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

		// update the directions
		directions = new_directions;
	}

	private void addHotspotToVisited(final Avatar a, final Hotspot h)
	{
		lastVisited.put(a, h);
		List<Hotspot> visitedHotspot = visited.get(a);
		if (visitedHotspot == null)
		{
			visitedHotspot = new ArrayList<Hotspot>();
			visited.put(a, visitedHotspot);
		}

		visitedHotspot.add(h);
	}


	@Override
	public String getName()
	{
		return "Lapt";
	}
}
