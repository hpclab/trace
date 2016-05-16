package test;

import ema.dve.workload.geom.MapVirtualEnvironment;

public class ConsistencyChecks 
{
	public static boolean check(MapVirtualEnvironment map1, MapVirtualEnvironment map2)
	{
		return false;
	}
	
	/*
	public static boolean check(Configuration c1, Configuration c2)
	{
		for (Param p : Configuration.Param.values())
		{
			System.out.println(p);
			if (c1.get(p).equals(c2.get(p)) == false)
				return false;
		}
		
		return true;
	}
	*/
}
