package ema.dve.workload;

import ema.dve.workload.configuration.PropertiesPersonalized;
import ema.dve.workload.geom.Entity;
import ema.dve.workload.geom.IdProvider;
import ema.dve.workload.geom.MapVirtualEnvironment;
import ema.dve.workload.geom.PassiveObject;
import ema.dve.workload.mobility.PlacementUtils;

public class RandomObjectPlacement extends AStaticPlacement
{

	private final int objectNumber;

	public RandomObjectPlacement(final PropertiesPersonalized conf, final IdProvider idProvider)
	{
		super(conf, idProvider);
		this.objectNumber = conf.getPropertyInt("OBJECTS_NUM");
	}

	@Override
	public Entity[] place(final MapVirtualEnvironment map)
	{
		final PassiveObject[] objects = new PassiveObject[objectNumber];

		// create and place the objects
		for (int i=0; i<objectNumber; i++)
		{
			objects[i] = new PassiveObject(idProvider.addOne(),
					PlacementUtils.randomPoint(map));
		}

		return objects;
	}

}
