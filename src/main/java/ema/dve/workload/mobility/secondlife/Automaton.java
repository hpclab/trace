package ema.dve.workload.mobility.secondlife;

import java.util.Random;

import ema.dve.workload.RandomSingleton;

public class Automaton 
{

	public enum State 
	{
		HALT,
		TRAVELLING,
		EXPLORING,
		EXP2
	}
	
	private State current;
	private Random r;
	
	// these array store the probability of H, E, E2, T
	private final static double[] Hto = {0.809, 0.182, 0.000, 0.009};
	private final static double[] Tto = {0.010, 0.023, 0.000, 0.967};
	private final static double[] Eto = {0.006, 0.685, 0.305, 0.004};
	private final static State[] states = {State.HALT, State.EXPLORING, State.EXP2, State.TRAVELLING};
	
	
	
	public Automaton()
	{
		r = RandomSingleton.getRandom();
		init();
	}
	
	public void init()
	{
		current = State.HALT;
	}
	
	public State currentState()
	{
		return current;
	}
	
	
	public State nextState()
	{
		double value = r.nextDouble();
		double cumulativeProbability = 0.0;
	
		if (current == State.HALT)
		{
			for (int i=0; i<Hto.length; i++)
			{
				cumulativeProbability += Hto[i];
				// System.out.println("Hto[i]"+ Hto[i]);
				// System.out.println("cum"+cumulativeProbability);
				if (value < cumulativeProbability)
				{
					// System.out.println(value);
					current =  states[i];
					return states[i];
				}
			}
		}
		else if (current == State.TRAVELLING)
		{
			for (int i=0; i<Tto.length; i++)
			{
				cumulativeProbability += Tto[i];
				if (value < cumulativeProbability)
				{
					current = states[i];
					return states[i];
				}
			}
		}
		else if ((current == State.EXPLORING) || (current == State.EXP2))
		{
			for (int i=0; i<Eto.length; i++)
			{
				cumulativeProbability += Eto[i];
				if (value < cumulativeProbability)
				{
					current = states[i];
					return states[i];
				}
			}
		}
		
		return State.HALT;
	}
	
}
