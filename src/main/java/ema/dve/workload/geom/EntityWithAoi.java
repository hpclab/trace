package ema.dve.workload.geom;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class EntityWithAoi extends Entity
{
	private double radius;
	
	public EntityWithAoi(int ID, Point2D.Double position, double radius) 
	{
		super(ID, position);
		
		this.radius = radius;
	}
	
	public Shape getAOI()
	{
		return new Ellipse2D.Double(position.x - radius, position.y - radius,
				radius * 2, radius * 2);
	}
	
	public double getAOIRadius()
	{
		return this.radius;
	}

}
