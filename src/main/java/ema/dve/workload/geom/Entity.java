package ema.dve.workload.geom;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.geom.Point2D;

public class Entity
{
	Point2D.Double position;
	int ID;


	public Entity(final int ID, final Point2D.Double position)
	 {
		 this.position = position;
		 this.ID = ID;
	 }

	 public int getID()
	 {
		 return this.ID;
	 }

	 public void setPosition(final Point2D.Double position)
	 {
		 this.position = position;
	 }

	 public void setPositionInteger(final Point position)
	 {
		 this.position = new Point2D.Double(new Integer(position.x).doubleValue(),
				 new Integer(position.y).doubleValue());
	 }

	 public Point2D.Double getPosition()
	 {
		 return this.position;
	 }

	 public Point2D.Double getPositionOutput(final Dimension mapDimension_)
	 {
		 return new Point2D.Double((((position.x % mapDimension_.getWidth())+mapDimension_.getWidth())%mapDimension_.getWidth()),
				 (((position.y % mapDimension_.getHeight())+mapDimension_.getHeight())%mapDimension_.getHeight()));
	 }

	 public Point getPositionInteger()
	 {
		 final Point2D.Double pos = this.getPosition();
		 return new Point(new Double(pos.x).intValue(),
				 new Double(pos.y).intValue());
	 }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ID;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		final Entity other = (Entity) obj;
		if (ID != other.ID)
		{
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Entity [ID=" + ID + " position=" + position + "]";
	}

}
