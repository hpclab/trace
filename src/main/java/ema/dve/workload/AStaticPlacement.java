package ema.dve.workload;

import ema.dve.workload.configuration.PropertiesPersonalized;
import ema.dve.workload.geom.Entity;
import ema.dve.workload.geom.IdProvider;
import ema.dve.workload.geom.MapVirtualEnvironment;

public abstract class AStaticPlacement
{
	protected PropertiesPersonalized conf;
	protected IdProvider idProvider;

	public AStaticPlacement(final PropertiesPersonalized conf, final IdProvider idProvider)
	{
		this.conf = conf;
		this.idProvider = idProvider;
	}

	public abstract Entity[] place(MapVirtualEnvironment map);
}
