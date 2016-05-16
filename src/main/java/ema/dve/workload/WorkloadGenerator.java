package ema.dve.workload;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ema.dve.workload.configuration.PropertiesPersonalized;
import ema.dve.workload.csv.CSVLayer;
import ema.dve.workload.geom.Avatar;
import ema.dve.workload.geom.Hotspot;
import ema.dve.workload.geom.IdProvider;
import ema.dve.workload.geom.MapVirtualEnvironment;
import ema.dve.workload.geom.PassiveObject;
import ema.dve.workload.mobility.AMobilityModel;
import ema.dve.workload.visual.MapPanel;

public class WorkloadGenerator
{
	private final PropertiesPersonalized conf;
	private final MapVirtualEnvironment map;
	private final CSVLayer csv;
	private final AMobilityModel mobility;
	private final int iterations;
	private final Computer computer;

	private boolean graphic = false;
	private boolean dump = false;

	private AStaticPlacement hotspotPlacement;
	private AStaticPlacement objectPlacement;
	private IAvatarNumberFunction avatarNumber;
	private final IdProvider avatarID;

	public WorkloadGenerator(final PropertiesPersonalized conf, final AMobilityModel mobility)
	{
		this.conf = conf;
		this.computer = new Computer();

		// set up defaults function
		this.hotspotPlacement = new RandomHotspotPlacement(conf, new IdProvider(3));
		this.objectPlacement = new RandomObjectPlacement(conf, new IdProvider(2));
		this.avatarNumber = new AvatarSteadyNumber(0);
		this.avatarID = new IdProvider(1);

		// set up wide variables
		this.iterations = conf.getPropertyInt("ITERATION_NUM");
		this.graphic = conf.getPropertyBoolean("ENABLE_GRAPHIC");
		this.dump = conf.getPropertyBoolean("ENABLE_DUMP");

		// init random
		RandomSingleton.init(conf.getRandomSeed());

		// create the map
		this.map = new MapVirtualEnvironment(conf);

		// create the mobility model
		this.mobility = mobility;

		// instantiate the writer
		this.csv = new CSVLayer(conf, map.getDimension());
//		this.csv.setFileName(conf.getProperty("OUTPUT_FILE"));

	}

	public String getFileName()
	{
		return this.csv.getFileName();
	}

	public void setHotspotPlacementFunction(final AStaticPlacement function)
	{
		this.hotspotPlacement = function;
	}

	public void setObjectPlacementFunction(final AStaticPlacement function)
	{
		this.objectPlacement = function;
	}

	public void setAvatarNumberFunction(final IAvatarNumberFunction function)
	{
		this.avatarNumber = function;
	}

	public Oracle process() throws Exception
	{
		final ExecutorService ex = Executors.newFixedThreadPool(conf.getPropertyInt("THREAD_NUMBER", 4));
		final long start = System.currentTimeMillis();
		// reset the global time
		GlobalTime.reset();

		// setup map hotspots and objects;
		map.setHotspots((Hotspot[])hotspotPlacement.place(map));
		map.setObjects((PassiveObject[])objectPlacement.place(map));

		// init avtars on map
		map.setAvatars(computeAvatarNumber(map, 0));
		mobility.move(0, map, ex);

		// init the oracle
		final Oracle oracle = new Oracle(conf);

		// add the iteration 0
		oracle.setMapAt(GlobalTime.getCurrent(), map);

		// initialize the graphic
		if (this.graphic)
		{
			MapPanel.initGraphics(map);
		}


		// start the simulation
		for (int i=1; i<iterations; i++)
		{
			GlobalTime.increment();

			// change the number of avatars
			map.setAvatars(computeAvatarNumber(map, i));

			// move the avatars
			mobility.move(i, map, ex);

			// compute the bandwidth for passive objects
			oracle.setBandAt(GlobalTime.getCurrent(), computer.compute(map, ex));

			oracle.setAOIStatAt(GlobalTime.getCurrent(), computer.computeAOI(map, ex));

			// update the oracle
			// Map copy = new Map(map);
			oracle.setMapAt(GlobalTime.getCurrent(), new MapVirtualEnvironment(map));

			// add delay only for visualization purposes
			if (this.graphic)
			{
				Thread.sleep(100);
			}
		}
		ex.shutdown();
		final long end = System.currentTimeMillis();
		System.out.println("Time: "+(end-start));

		final FileWriter stats = new FileWriter(new File("stats.txt"), true);
		stats.write(mobility.getName()+","+avatarNumber.getMaxNumber()+","+(end-start)+"\n");
		stats.flush();
		stats.close();

		// log the positions
		if (this.dump)
		{
			csv.writeOracle(oracle);
		}

		// return the oracle
		return oracle;
	}

	private Avatar[] computeAvatarNumber(final MapVirtualEnvironment map, final int time)
	{
		// take the current avatars from the map
		List<Avatar> avatars = new ArrayList<Avatar>();
		avatars.addAll(map.getAllAvatar());

		// compute the number of avatars and update the array
		final int new_number = avatarNumber.getNumber(time);

		// get the aoi radius
		final double aoi_radius = conf.getPropertyDouble("AOI_RADIUS");

		// do the math
		if (new_number > avatars.size())
		{
			final int diff = new_number - avatars.size();
			final List<Integer> idList = avatarID.add(diff);

			for (final Integer id : idList)
			{
				avatars.add(new Avatar(id, aoi_radius));
			}
		}
		else if (new_number < avatars.size())
		{
			int diff = 0;
			if (new_number <= 0)
			{
				diff = avatars.size();
			} else
			{
				diff = avatars.size() - new_number;
			}

			final List<Integer> removedId = avatarID.remove(diff);
			final List<Avatar> newList = new ArrayList<Avatar>();

			for (final Avatar a: avatars)
			{
				if (removedId.contains(a.getID()) == false)
				{ // if this avatar does NOT have to be removed
					newList.add(a);
				}
			}

			// overwrite the old array
			avatars = newList;
		}

		// transform and return
		Avatar[] toReturn = new Avatar[avatars.size()];
		toReturn = avatars.toArray(toReturn);
		return toReturn;
	}

//	/**
//	 * Load from a previously written archive
//	 * @param filename
//	 * @return
//	 * @throws Exception
//	 */
//	public static Oracle loadFromFile(final String filename) throws Exception
//	{
//		final CSVLayer csv = new CSVLayer();
//		csv.setFileName(filename);
//		final Oracle oracle = csv.readOracle();
//		return oracle;
//	}
}
