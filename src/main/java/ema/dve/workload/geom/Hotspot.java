package ema.dve.workload.geom;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;

public class Hotspot extends Entity
{
	private double radius;
	private Shape myShape;
	
	public Hotspot(int ID, Point2D.Double position, double radius)
	{
		super(ID, position);
		this.radius = radius;
		this.myShape =  new Ellipse2D.Double(position.x - radius, position.y - radius,
				radius * 2, radius * 2);
	}
	
	public double getRadius()
	{
		return radius;
	}
	
	public Shape getShape()
	{
		return myShape;
	}
}
