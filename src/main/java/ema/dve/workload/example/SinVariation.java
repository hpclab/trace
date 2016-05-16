package ema.dve.workload.example;

import ema.dve.workload.IAvatarNumberFunction;

public class SinVariation implements IAvatarNumberFunction
{
	@Override
	public int getNumber(int t) 
	{
		Double avatars = Math.sin(new Integer(t).doubleValue() * Math.PI / 100) * 100; 
		return avatars.intValue();
	}

	@Override
	/**
	 * It reaches the maximum avatar (100) at iteration 50
	 */
	public int getMaxNumber()
	{
		return getNumber(50); // i know my funnction
	}
	
	
	// for testing purposes....
	public static void main (String[] args)
	{
		SinVariation var = new SinVariation();
		for (int i=0; i<100; i++)
		{
			System.out.println(var.getNumber(i));
		}
	}
}