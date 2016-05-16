package ema.dve.workload;

import java.util.Random;

public class RandomSingleton
{
	private static Random rand;

	public static void init(final long seed)
	{
		rand = new Random(seed);
	}

	public static Random getRandom()
	{
		return rand;
	}

}
