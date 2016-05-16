package ema.dve.workload.mobility.rpgm;

import java.awt.geom.Point2D;

import ema.dve.workload.RandomSingleton;
import ema.dve.workload.geom.MapVirtualEnvironment;
import ema.dve.workload.mobility.PlacementUtils;

public class RpgmDestination
{
	private final Point2D.Double _current;
	private final Point2D.Double _next;
	private final int _nextTime;
	private final double _velocity;
	private final int _pauseTime;

	public RpgmDestination(final Point2D.Double current_, final Point2D.Double next_, final int nextTime_, final double velocity_, final int pauseTime_)
	{
		_current = current_;
		_next = next_;
		_nextTime = nextTime_;
		_velocity = velocity_;
		_pauseTime = pauseTime_;
	}

	public RpgmDestination(final MapVirtualEnvironment map_, final double minVelocity_, final double maxVelocity_, final int pauseTime_)
	{
		_current = PlacementUtils.randomPoint(map_);
		_next = _current;
		_nextTime = -1;
		_velocity = minVelocity_ + ((maxVelocity_ - minVelocity_) * RandomSingleton.getRandom().nextDouble());
		_pauseTime = pauseTime_;
	}

	public Point2D.Double getDestination(final int time_)
	{
		if(time_ > _nextTime)
		{
			return _next;
		} else
		{
			return _current;
		}
	}

	public RpgmDestination update(final MapVirtualEnvironment map_, final int time_, final double minVelocity_, final double maxVelocity_)
	{
		if(time_ > _nextTime)
		{
			final double velocity = minVelocity_ + ((maxVelocity_ - minVelocity_) * RandomSingleton.getRandom().nextDouble());
			return new RpgmDestination(_next, PlacementUtils.randomPoint(map_), time_ + _pauseTime, velocity, _pauseTime);
		} else
		{
			return this;
		}
	}

	public double getSpeed()
	{
		return _velocity;
	}

}
