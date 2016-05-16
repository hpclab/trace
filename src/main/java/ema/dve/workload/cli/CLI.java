//package ema.dve.workload.cli;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import org.apache.commons.cli.HelpFormatter;
//import org.apache.commons.cli.OptionBuilder;
//import org.apache.commons.cli.Options;
//
//import com.beust.jcommander.DynamicParameter;
//import com.beust.jcommander.JCommander;
//import com.beust.jcommander.Parameter;
//import com.beust.jcommander.Parameters;
//
//import ema.dve.workload.AvatarSteadyNumber;
//import ema.dve.workload.GeneralConfiguration;
//import ema.dve.workload.WorkloadGenerator;
//import ema.dve.workload.configuration.AConfiguration;
//import ema.dve.workload.mobility.AMobilityModel;
//import ema.dve.workload.mobility.latp.Lapt;
//import ema.dve.workload.mobility.latp.LaptConfiguration;
//import ema.dve.workload.mobility.random.RandomWalk;
//import ema.dve.workload.mobility.random.RandomWalkConfiguration;
//import ema.dve.workload.mobility.random.RandomWayPoint;
//import ema.dve.workload.mobility.random.RandomWayPointConfiguration;
//import ema.dve.workload.mobility.secondlife.BlueBanana;
//import ema.dve.workload.mobility.secondlife.BlueBananaConfiguration;
//
//
///**
// * This class defines a command line interface for the trace generator.
// * From this class is possible to launch the trace generator without
// * writing java code.
// * With this interface is NOT possible to run a trace with a variable number
// * of avatars. In addition there is no possibility to have personalized
// * hotspots and objects placements.
// *
// * @author carlini
// *
// */
//public class CLI
//{
//
//	static private HashMap<Class<? extends AMobilityModel>, Class> registry =
//			new HashMap<Class<? extends AMobilityModel>, Class>();
//
//	static
//	{
//		registry.put(BlueBanana.class, BlueBananaConfiguration.class);
//		registry.put(RandomWalk.class, RandomWalkConfiguration.class);
//		registry.put(RandomWayPoint.class, RandomWayPointConfiguration.class);
//		registry.put(Lapt.class, LaptConfiguration.class);
//	}
//
//
//	public static void main(String[] args)
//	{
//		JCommander jc = new JCommander();
//
//		CommandList list = new CommandList();
//		jc.addCommand("list-model", list);
//
//		CommandConfiguration configuration = new CommandConfiguration();
//		jc.addCommand("list-configuration", configuration);
//
//		CommandRun run = new CommandRun();
//		jc.addCommand("run", run);
//
//
//		// jc.parse("configuration", "-m", "Lapt");
//		// jc.parse("run", "-m", "Lapt", "-DALPHA=2.0");
//
//		if (args == null || args.length == 0)
//		{
//			jc.usage(); // print the usage
//			System.exit(0);
//		}
//		else
//			jc.parse(args);
//
//		String parsedCommand = jc.getParsedCommand();
//
//		if (parsedCommand.equals("list-model"))
//		{
//			list.run();
//		}
//		else if (parsedCommand.equals("list-configuration"))
//		{
//			configuration.run();
//		}
//		else if (parsedCommand.equals("run"))
//		{
//			run.run();
//		}
//	}
//
//	/**
//	 * List the available mobility models
//	 * @author carlini
//	 *
//	 */
//	@Parameters(commandDescription = "Lists the available models")
//	private static class CommandList
//	{
//		@Parameter(names = "-m")
//		private String model;
//
//		private void run()
//		{
//			printListModels();
//		}
//	}
//
//	/**
//	 * List the configuration parameter for the given mobility model.
//	 * If no mobility model is given, list the parameter for the general
//	 * configuration.
//	 * @author carlini
//	 *
//	 */
//	@Parameters(commandDescription = "Lists the configuration parameter for the given mobility model")
//	private static class CommandConfiguration
//	{
//		@Parameter(names = "-m", description = "a mobility model; if no model specified, the general configuration will be shown")
//		private String model;
//
//		private void run()
//		{
//			// no model, printing general configuration
//			if (model == null || model == "")
//			{
//				printOptionsFromClass(GeneralConfiguration.class);
//				return;
//			}
//
//			Class<? extends AMobilityModel> clazz = getFromRegistry(model);
//			if (clazz != null)
//				printOptionsFromClass(registry.get(clazz));
//			else
//			{
//				System.out.println("Unknown model: "+model);
//				printListModels();
//			}
//		}
//	}
//
//	/**
//	 * Run the given mobility model.
//	 * @author carlini
//	 *
//	 */
//	@Parameters(commandDescription = "Runs a mobility model")
//	public static class CommandRun
//	{
//		@Parameter(names = "-m", description = "a mbility models")
//		private String model;
//
//		@Parameter(names = "-a", description = "the number of avatars")
//		private String avatars;
//
//		@DynamicParameter(names = "-D", description = "Configuration parameters")
//		private Map<String, String> params = new HashMap<String, String>();
//
//
//		private void run()
//		{
//			try
//			{
//				// ----- PARAMETERS CHECK ------
//
//				// check if the model exists
//				if (model == null || getFromRegistry(model) == null)
//				{
//					System.out.println("Unknown model: "+model);
//					printListModels();
//					System.exit(1);
//				}
//
//				// check the number of avatars
//				int avatar_num = 0;
//				boolean exc = false;
//
//				try
//				{
//					avatar_num = Integer.parseInt(avatars);
//				}
//				catch (NumberFormatException e)
//				{
//					exc = true;
//				}
//
//				if (exc || avatar_num <= 0)
//				{
//					System.out.println("FATAL: wrong number of avatars");
//					System.exit(1);
//				}
//
//				// ----- END PARAMETERS CHECK ------
//
//				Class<? extends AMobilityModel> modelClass = getFromRegistry(model);
//
//				// managing and populating configuration
//				GeneralConfiguration genConf = new GeneralConfiguration();
//				Class confClass = registry.get(modelClass);
//				AConfiguration modelConf = (AConfiguration) confClass.newInstance();
//
//				for (String param: params.keySet())
//				{
//					String value = params.get(param);
//
//					// check for the general configuration
//					if (genConf.getParamFromString(param) != null)
//					{
//						genConf.set(genConf.getParamFromString(param), value);
//					}
//
//					// check for the model configuration
//					if (modelConf.getParamFromString(param) != null)
//					{
//						modelConf.set(modelConf.getParamFromString(param), value);
//					}
//				}
//
//				// create the mobility model
//				AMobilityModel model = (AMobilityModel) modelClass.getConstructors()[0].newInstance(modelConf);
//
//				// create the generator
//				WorkloadGenerator wg = new WorkloadGenerator(genConf, model);
//				wg.setAvatarNumberFunction(new AvatarSteadyNumber(avatar_num));
//
//				wg.process();
//			}
//			catch (Exception e) {e.printStackTrace();}
//		}
//	}
//
//	/**
//	 * Return an object containing the list of parameters for a given
//	 * configuration class.
//	 * @param configurationClazz
//	 * @return
//	 */
//	private static HelpFormatter printOptionsFromClass(Class configurationClazz)
//	{
//		Options tmp = new Options();
//
//		AConfiguration instance = null;
//
//		try { instance = (AConfiguration) configurationClazz.newInstance(); }
//		catch (Exception e) { e.printStackTrace();}
//
//
//		Class param = configurationClazz.getClasses()[0];
//
//		Object[] opt = param.getEnumConstants();
//		for (Object o: opt)
//		{
//			tmp.addOption(OptionBuilder.withLongOpt(o.toString())
//					.withDescription("["+instance.get((Enum) o)+"]")
//					.create());
//		}
//
//		String header = "";
//		String footer = "To setup a property from the command line: -D<name>=value";
//
//		HelpFormatter formatter = new HelpFormatter();
//		formatter.setLongOptPrefix("");
//		formatter.setSyntaxPrefix("");
//		formatter.printHelp("Name [Default Value]", header, tmp, footer, false);
//
//		return formatter;
//	}
//
//	/**
//	 * Prints on screen the available mobility models.
//	 */
//	private static void printListModels()
//	{
//		StringBuilder sb = new StringBuilder();
//		sb.append("List of available mobility models: ").append("\n");
//
//		for (Class<? extends AMobilityModel> clazz: registry.keySet())
//			sb.append("\t").append(clazz.getSimpleName()).append("\n");
//
//		System.out.println(sb.toString());
//	}
//
//	/**
//	 * Get abstract configuration class given the name of a model.
//	 * @param model
//	 * @return
//	 */
//	private static Class<? extends AMobilityModel> getFromRegistry(String model)
//	{
//		for (Class<? extends AMobilityModel> clazz: registry.keySet())
//		{
//			if (clazz.getSimpleName().equals(model))
//				return clazz;
//		}
//
//		return null;
//	}
//
//
//}
//
//
