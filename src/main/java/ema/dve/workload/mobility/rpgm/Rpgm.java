package ema.dve.workload.mobility.rpgm;

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

public class Rpgm extends AMobilityModel
{
	private final Map<Integer, Integer> _mapAvatarToLeader;
	private final Map<Integer,RpgmDestination> _leaderSet;
	private final int _leaderNumber;

	private Map<Integer, Direction> _directions;

	private final double _maxVelocity;
	private final double _adr;
	private final double _sdr;
	private final double _maxAngle;
	private final int _radiusDistanceInit;
	private final double _minVelocity;
	private final int _pauseTime;

	public Rpgm(final PropertiesPersonalized configuration_)
	{
		super(configuration_);

		_mapAvatarToLeader = new ConcurrentHashMap<>();
		_leaderSet = new ConcurrentHashMap<>();

		_directions = new ConcurrentHashMap<>();

		_maxVelocity = Double.parseDouble(configuration_.getProperty("VELOCITY_MAX", "0.4"));
		_minVelocity = Double.parseDouble(configuration_.getProperty("VELOCITY_MIN", "0.4"));
		_adr = Double.parseDouble(configuration_.getProperty("ADR", "0.1")); // check
		_sdr = Double.parseDouble(configuration_.getProperty("SDR", "0.1")); // check
		_maxAngle = Double.parseDouble(configuration_.getProperty("MAX_ANGLE", "0.1")); // check
		_leaderNumber = Integer.parseInt(configuration_.getProperty("LEADER_NUMBER"));
		_pauseTime = Integer.parseInt(configuration_.getProperty("PAUSE_TIME", "30"));
		_radiusDistanceInit = configuration_.getPropertyInt("RADIUS_DISTANCE_INIT", 40); // check
	}

	private boolean isNearDestination(final Point2D.Double dir_, final Point2D.Double position_)
	{
		return (Math.abs(dir_.x - position_.x) < _radiusDistanceInit) &&
				(Math.abs(dir_.y - position_.y) < _radiusDistanceInit);
	}

	private int getLeaderId(final Avatar a_)
	{
		return _mapAvatarToLeader.get(a_.getID());
	}

	private void init(final MapVirtualEnvironment map_)
	{
		if(_leaderSet.size() < _leaderNumber)
		{
			for(int i = 0; i < _leaderNumber; i++)
			{
				_leaderSet.put(i, new RpgmDestination(map_, _minVelocity, _maxVelocity, _pauseTime));
			}

			for(final Avatar a : map_.getAllAvatar())
			{
				final int leaderId = RandomSingleton.getRandom().nextInt(_leaderNumber);
				_mapAvatarToLeader.put(a.getID(), leaderId);
			}
		}
	}

	@Override
	public void move(final int time, final MapVirtualEnvironment map, final ExecutorService ex_)
	{
		init(map);

		final Map<Integer, Direction> newDirections = new ConcurrentHashMap<>();
		final List<Future<Integer>> w = new ArrayList<>();

		for (final Avatar avatar: map.getAllAvatar())
		{
			w.add(ex_.submit(new Callable<Integer>()
			{

				@Override
				public Integer call() throws Exception
				{
					final Direction direction = _directions.get(avatar.getID());

					if (direction == null)
					{
						final RpgmDestination destination = _leaderSet.get(getLeaderId(avatar));

						final Point2D.Double position = new Point2D.Double(
								(destination.getDestination(time).x + RandomSingleton.getRandom().nextInt(_radiusDistanceInit)) - (_radiusDistanceInit / 2),
								(destination.getDestination(time).y + RandomSingleton.getRandom().nextInt(_radiusDistanceInit)) - (_radiusDistanceInit / 2));
						final double velocity = _minVelocity + ((_maxVelocity - _minVelocity) * RandomSingleton.getRandom().nextDouble());
						final Direction new_dir = new Direction(position, position, time, velocity);

						newDirections.put(avatar.getID(), new_dir);
					}
					else
					{
						final Point2D.Double avatarCurrentPosition = map.getPosition(direction.getPositionAt(time));
						RpgmDestination leaderDestination = _leaderSet.get(getLeaderId(avatar));

						double speed = leaderDestination.getSpeed();
						if(isNearDestination(leaderDestination.getDestination(time), avatarCurrentPosition))
						{
							speed = 0;
							leaderDestination = leaderDestination.update(map, time, _minVelocity, _maxVelocity);
							_leaderSet.put(getLeaderId(avatar), leaderDestination);
						}

						final double y = avatarCurrentPosition.y - leaderDestination.getDestination(time).y;
						final double x = avatarCurrentPosition.x - leaderDestination.getDestination(time).x;
						final double alphaLeader = Math.atan(x/y);
						final double alpha = 	alphaLeader +
												(RandomSingleton.getRandom().nextDouble() *
												_adr *
												_maxAngle);

						final double step = speed
								+
										(RandomSingleton.getRandom().nextDouble() *
										_sdr *
										_maxAngle)
								;

						final double nextX = step * Math.sin(alpha);
						final double nextY = step * Math.cos(alpha);

						final Point2D.Double nextPosition = new Point2D.Double(avatarCurrentPosition.x + nextX, avatarCurrentPosition.y + nextY);
						final Direction next = new Direction(	nextPosition, nextPosition, time, step);

						avatar.setPosition(nextPosition);
						newDirections.put(avatar.getID(), next);
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

		_directions = newDirections;
	}


	@Override
	public String getName()
	{
		return "Rpgm";
	}
}
