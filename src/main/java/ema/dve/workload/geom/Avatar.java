package ema.dve.workload.geom;

import java.awt.geom.Point2D;

public class Avatar extends EntityWithAoi
{		
	public Avatar(int ID, Point2D.Double pos, double radius)
	{
		super(ID, pos, radius);
	}
	
	public Avatar(int ID, double radius)
	{
		super(ID, new Point2D.Double(0,0), radius);
	}
}