package ema.dve.workload.mobility;

import java.awt.geom.Point2D;

public class Direction
{
	private final Point2D.Double origin;
	private final Point2D.Double dest;
	private final Double speed;
	private final int issue_time; // reference time of creation

	private final Double distance;
	private final Double travelTime;

	public Direction(final Point2D.Double origin, final Point2D.Double dest, final int issue_time, final Double speed)
	{
		this.origin = origin;
		this.dest = dest;
		this.speed = speed;
		this.issue_time = issue_time;

		this.distance = origin.distance(dest);
		this.travelTime = distance / this.speed;
	}

	/**
	 * Returns the position at time time t.
	 * @param t
	 * @return
	 */
	public Point2D.Double getPositionAt(final int t)
	{
		return this.getPositionAt(new Double(t));
	}

	/**
	 * Returns the position at time time t.
	 * @param t
	 * @return
	 */
	public Point2D.Double getPositionAt(final double t)
	{
		// the origin and destination collapse
		if (this.travelTime == 0)
		{
			return this.origin;
		}

		// I'm not sure why there was this + 1. Comment didnt help
		// OLD COMMENT: the +1 is because otherwise it doesnt start to
		// move when the new dest is chosen.
		final Double time = t - this.issue_time; // + 1;

		final Double x_t = origin.x + ((dest.x - origin.x) * (time/travelTime));
		final Double y_t = origin.y + ((dest.y - origin.y) * (time/travelTime));

		return new Point2D.Double(x_t, y_t);
	}

	public Point2D.Double getDestination()
	{
		return dest;
	}

	/**
	 * Travel time is computed as distance / speed.
	 * @return
	 */
	public Double getTravelTime()
	{
		return this.travelTime;
	}

	/**
	 * Issue time is the time the direction has been created.
	 * @return
	 */
	public int getIssueTime()
	{
		return this.issue_time;
	}

	public double getSpeed()
	{
		return speed;
	}

	/**
	 * Arrival time is the sum of issue time plus travel time.
	 * @return
	 */
	public Double getArrivalTime()
	{
		return this.getIssueTime() + this.getTravelTime();
	}
}
