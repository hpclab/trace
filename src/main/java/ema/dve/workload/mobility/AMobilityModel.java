package ema.dve.workload.mobility;

import java.util.concurrent.ExecutorService;

import ema.dve.workload.configuration.PropertiesPersonalized;
import ema.dve.workload.geom.MapVirtualEnvironment;

public abstract class AMobilityModel
{
	PropertiesPersonalized configuration;

	public AMobilityModel(final PropertiesPersonalized configuration)
	{
		this.configuration = configuration;
	}

	public PropertiesPersonalized getConfig()
	{
		return configuration;
	}

	public abstract void move(int time, MapVirtualEnvironment map, ExecutorService ex_);

	public abstract String getName();
}
