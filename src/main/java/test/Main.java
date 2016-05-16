package test;

import ema.dve.workload.AvatarSteadyNumber;
import ema.dve.workload.RandomSingleton;
import ema.dve.workload.WorkloadGeneratorIncremental;
import ema.dve.workload.configuration.PropertiesPersonalized;
import ema.dve.workload.mobility.AMobilityModel;

public class Main
{
	public static void main(final String[] args_) throws Exception
	{
		final String configFile = args_[0];

		final PropertiesPersonalized property = new PropertiesPersonalized(configFile);
		RandomSingleton.init(property.getRandomSeed());

		final AMobilityModel model = property.getMobilityModel();

		final WorkloadGeneratorIncremental wg = new WorkloadGeneratorIncremental(property, model);
		wg.setAvatarNumberFunction(new AvatarSteadyNumber(Integer.parseInt(property.getProperty("AVATAR"))));
		wg.process();
	}
}
