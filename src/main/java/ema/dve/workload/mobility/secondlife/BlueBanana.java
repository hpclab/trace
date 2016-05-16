package ema.dve.workload.mobility.secondlife;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ExecutorService;

import org.apache.commons.math3.distribution.ZipfDistribution;

import ema.dve.workload.GlobalTime;
import ema.dve.workload.RandomSingleton;
import ema.dve.workload.configuration.PropertiesPersonalized;
import ema.dve.workload.geom.Avatar;
import ema.dve.workload.geom.Hotspot;
import ema.dve.workload.geom.MapVirtualEnvironment;
import ema.dve.workload.mobility.AMobilityModel;
import ema.dve.workload.mobility.Direction;
import ema.dve.workload.mobility.PlacementUtils;
import ema.dve.workload.mobility.secondlife.Automaton.State;

/**
 * This class implements the BlueBanana mobility model.
 * Blue Banana: resilience to avatar mobility in distributed MMOGs, S Legtchenko et al.
 *
 * The class assigns to each avatar a direction and an automata, and updates them each time
 * the method move is called.
 * @author emanuele
 *
 */
public class BlueBanana extends AMobilityModel
{
	private HashMap<Avatar, Direction> directions;
	private HashMap<Avatar, Automaton> automata;

	private final double hotspot_perc;
	private final double avatar_speed;

	private final ZipfDistribution zd;
	private final double zipf_population = 1000d;

	public BlueBanana(final PropertiesPersonalized configuration)
	{
		super(configuration);
		this.hotspot_perc = Double.parseDouble(configuration.getProperty("HOTSPOT_IN_PROB"));
		this.avatar_speed = Double.parseDouble(configuration.getProperty("AVATAR_SPEED"));

		// setup the zipfian generator
		final double exponent = Double.parseDouble(configuration.getProperty("ZIPF_EXPONENT"));
		final long zipf_seed = Long.parseLong(configuration.getProperty("ZIPF_RANDOM_SEED"));
		this.zd = new ZipfDistribution((int) zipf_population, exponent);
		zd.reseedRandomGenerator(zipf_seed);

		directions = new HashMap<Avatar, Direction>();
		automata = new HashMap<Avatar, Automaton>();
	}

	@Override
	public void move(final int time, final MapVirtualEnvironment map, final ExecutorService ex_)
	{
		final Collection<Avatar> avatars = map.getAllAvatar();

		final HashMap<Avatar, Direction> new_directions = new HashMap<Avatar, Direction>();
		final HashMap<Avatar, Automaton> new_automata = new HashMap<Avatar, Automaton>();

		for (final Avatar a : avatars)
		{
			// get automata and direction
			Automaton automaton = automata.get(a);
			Direction direction = directions.get(a);


			// check if the avatar has a direction and an automata.
			if ((automaton == null) || (direction == null))
			{
				// set up the direction
				final Point2D.Double position = placeAvatar(map);
				direction = new Direction(position, position, time, avatar_speed);
				new_directions.put(a, direction);

				// set up the automaton
				automaton = new Automaton();
				new_automata.put(a, automaton);

				// update the avatar position
				a.setPosition(filterPosition(direction.getPositionAt(new Double(time)),map));
			}
			else
			{
				// let's dance!
				final State previous = automaton.currentState();
				final State current = automaton.nextState();

				// update the direction
				direction = this.computeDirection(map, a, direction, previous, current, time);

				// update the position of the avatar
				a.setPosition(filterPosition(direction.getPositionAt(new Double(time)),map));

				// update the data structures
				new_directions.put(a, direction);
				new_automata.put(a, automaton);
			}
		}

		directions = new_directions;
		automata = new_automata;
	}


	/**
	 * Assures that the positions remains inside the Map.
	 */
	private Point2D.Double filterPosition(final Point2D.Double position, final MapVirtualEnvironment map)
	{
		double mod_x = position.x % map.getDimension().getWidth();
		double mod_y = position.y % map.getDimension().getHeight();

		if (mod_x < 0)
		{
			mod_x = mod_x + map.getDimension().getWidth();
		}

		if (mod_y < 0)
		{
			mod_y = mod_y + map.getDimension().getHeight();
		}

		return new Point2D.Double(mod_x, mod_y);
	}

	/**
	 * Compute the avatar direction
	 */
	private Direction computeDirection(final MapVirtualEnvironment map, final Avatar avatar, Direction direction, final State previous, final State current, final int time)
	{
		// if anything is not modified, the direction remains the same (as long as result is then rewrote as "new Direction()"
		Direction result = direction;

		// if Halt --> stop
		if (current == State.HALT) // TtoH, EtoH, HtoH
		{
			result = new Direction(avatar.getPosition(), avatar.getPosition(), time, avatar_speed);
		}
		else if (current == State.TRAVELLING)
		{
			if ((previous == State.HALT) || (previous == State.EXPLORING)) // HtoT, EtoT
			{
				result = new Direction(avatar.getPosition(), placeAvatar(map),
						time, avatar_speed);
			}
			else if (previous == State.TRAVELLING){} //TtoT
		}
		else if (current == State.EXPLORING)
		{
			if ((previous == State.TRAVELLING) || (previous == State.HALT)) //HtoE, TtoE
			{
				boolean isH = false; // whether it's in hotspot or not

				// check whether im in hotspot
				for (int i=0; i<map.getHotspots().length; i++)
				{
					if (map.getHotspots()[i].getShape().contains(avatar.getPosition()))
					{
						final Point2D.Double offset = chooseOffsetInsideHotspot(map.getHotspots()[i]);
						final double x = map.getHotspots()[i].getPosition().x + offset.x;
						final double y = map.getHotspots()[i].getPosition().y + offset.y;

						result = new Direction(avatar.getPosition(), new Point2D.Double(x,y),
								time, avatar_speed);
						isH = true;
						break;
					}
				}

				// arriving here: im not in hotspot
				if (isH == false)
				{
					result = new Direction(avatar.getPosition(), PlacementUtils.randomPoint(map),
							GlobalTime.getCurrent(), avatar_speed);
				}

			}
			else if (previous == State.EXPLORING) {} //EtoE
		}
		else if (current == State.EXP2)
		{
			boolean isH = false; // whether it's in hotspot or not

			// check whether im in hotspot
			for (int i=0; i<map.getHotspots().length; i++)
			{
				if (map.getHotspots()[i].getShape().contains(avatar.getPosition()))
				{
					final Point2D.Double offset = chooseOffsetInsideHotspot(map.getHotspots()[i]);
					final double x = map.getHotspots()[i].getPosition().x + offset.x;
					final double y = map.getHotspots()[i].getPosition().y + offset.y;

					result = new Direction(avatar.getPosition(), new Point2D.Double(x,y),
							time, avatar_speed);
					isH = true; // im in hotspost
					break;
				}
			}

			if (isH == false) //im not in hotspot
			{
				// FIXME: here to put result and not direction?
				direction = new Direction(avatar.getPosition(), PlacementUtils.randomPoint(map),
						GlobalTime.getCurrent(), avatar_speed);
			}
		}


		// System.out.println("["+GlobalTime.getCurrent()+ "] Position:" + this.getLocation());
		return result;
	}

	public Point2D.Double placeAvatar(final MapVirtualEnvironment map)
	{
		final Random r = RandomSingleton.getRandom();
		final boolean hs = r.nextDouble() < hotspot_perc;

		if (hs == false)
		{
			return PlacementUtils.randomPoint(map);
		}
		else // inside hotspot
		{
			final int index = r.nextInt(map.getHotspots().length);
			final Point2D.Double offset = chooseOffsetInsideHotspot(map.getHotspots()[index]);

			final double x = map.getHotspots()[index].getPosition().x + offset.x;
			final double y = map.getHotspots()[index].getPosition().y + offset.y;

			return new Point2D.Double(x,y);
		}
	}

	public Point2D.Double chooseOffsetInsideHotspot(final Hotspot hotspot)
	{
		// choose an angle (uniformly)
		final double angle = RandomSingleton.getRandom().nextDouble() * Math.PI * 2;

		// choose a length between 0 and one(zipf)
		final double length = zd.sample() / this.zipf_population;

		// compute!
		final double x = Math.cos(angle) * hotspot.getRadius() * length;
		final double y = Math.sin(angle) * hotspot.getRadius() * length;

		// consider the point to add as offset
		return new Point2D.Double(x,y);
	}

	@Override
	public String getName()
	{
		return "BlueBanana";
	}
}
