package ema.dve.workload;

public class GlobalTime 
{
	private static int current = 0;
	
	public static void increment()
	{
		current ++;
	}
	
	public static void setTime(int time)
	{
		current = time;
	}
	
	public static int getCurrent()
	{
		return current;
	}
	
	public static void reset()
	{
		current = 0;
	}
	
}
