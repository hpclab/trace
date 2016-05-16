package ema.dve.workload;


public class AvatarSteadyNumber implements IAvatarNumberFunction
{
	private int number;
	
	public AvatarSteadyNumber(int number)
	{
		this.number = number;
	}
	
	@Override
	public int getNumber(int t) 
	{
		return number;
	}

	@Override
	public int getMaxNumber() 
	{
		return number;
	}

}
