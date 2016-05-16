package ema.dve.workload;

public class BandwidthInfo 
{
	private int numInitialTransfers = 0;
	private int numRegularTransfers = 0;
	
	public BandwidthInfo() {} // empty constructor;
	
	public void increaseNumInitialTransfers()
	{
		numInitialTransfers++;
	}
	
	public void increaseNumRegularTransfers()
	{
		numRegularTransfers++;
	}
	
	public int getNumInitialTransfers() 
	{
		return numInitialTransfers;
	}
	
	public void setNumInitialTransfers(int numInitialTransfers) 
	{
		this.numInitialTransfers = numInitialTransfers;
	}
	
	public int getNumRegularTransfers() 
	{
		return numRegularTransfers;
	}
	
	public void setNumRegularTransfers(int numRegularTransfers) 
	{
		this.numRegularTransfers = numRegularTransfers;
	}
	
	@Override
	public String toString() {
		return "BandwidthInfo [numInitialTransfers=" + numInitialTransfers
				+ ", numRegularTransfers=" + numRegularTransfers + "]";
	}
}
