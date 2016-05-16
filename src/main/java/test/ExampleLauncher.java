//package test;
//
//import ema.dve.workload.AvatarSteadyNumber;
//import ema.dve.workload.GeneralConfiguration;
//import ema.dve.workload.GeneralConfiguration.Param;
//import ema.dve.workload.WorkloadGenerator;
//import ema.dve.workload.mobility.latp.Lapt;
//import ema.dve.workload.mobility.latp.LaptConfiguration;
//
//public class ExampleLauncher
//{
//	public static void main(String[] args) throws Exception
//	{
//		GeneralConfiguration conf = new GeneralConfiguration();
//
//		conf.set(Param.ITERATION_NUM, "100");
//		conf.set(Param.HOTSPOT_NUM, "3");
//		conf.set(Param.HOTSPOT_RADIUS, "10");
//		conf.set(Param.OBJECTS_NUM, "0");
//		conf.set(Param.RANDOM_SEED, "15555534");
//		conf.set(Param.ENABLE_GRAPHIC, "true");
//		conf.set(Param.ENABLE_DUMP, "true");
//
//		LaptConfiguration laptconf = new LaptConfiguration();
//		laptconf.set(LaptConfiguration.Param.VELOCITY, "4.0");
//		laptconf.set(LaptConfiguration.Param.ALPHA, "3.0");
//		Lapt lapt = new Lapt(laptconf);
//
//
//		// create the generator
//		WorkloadGenerator wg = new WorkloadGenerator(conf, lapt);
//		wg.setAvatarNumberFunction(new AvatarSteadyNumber(5));
//		wg.process();
//
//		System.exit(0);
//	}
//}
