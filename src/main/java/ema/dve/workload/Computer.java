package ema.dve.workload;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

import ema.dve.workload.geom.Avatar;
import ema.dve.workload.geom.MapVirtualEnvironment;
import ema.dve.workload.geom.PassiveObject;

public class Computer
{
	Logger logger = Logger.getLogger(Computer.class.getName());

	// This data structure keeps the info about what AOI Passive object are in
	Map<PassiveObject,List<Avatar>> inAOI;

	public Computer()
	{
		this.inAOI = new ConcurrentHashMap<>();
	}

	public Map<Integer,Integer> computeAOI(final MapVirtualEnvironment map, final ExecutorService ex_)
	{
		final Map<Integer,Integer> avatarInAOIMap = new ConcurrentHashMap<>();

		final List<Future<Integer>> result = new ArrayList<>();

		for (final Avatar a: map.getAllAvatar())
		{
			result.add(ex_.submit(new Callable<Integer>()
			{
				@Override
				public Integer call() throws Exception
				{
					int count = 0;
					for (final Avatar b: map.getAllAvatar())
					{
						if((a.getID() != b.getID()) && a.getAOI().contains(b.getPositionOutput(map.getDimension())))
						{
							count ++;
						}
					}
					avatarInAOIMap.put(a.getID(), count);

					return count;
				}
			}));
		}

		ParallelUtil.waitCompletion(result);

		return avatarInAOIMap;
	}

	public Map<Integer,BandwidthInfo> compute(final MapVirtualEnvironment map, final ExecutorService ex_)
	{
		// keeps the info about the bandwidth consumption
		final Map<Integer,BandwidthInfo> binfo = new ConcurrentHashMap<>();

		final List<Future<Integer>> result = new ArrayList<>();

		// for every object
		for (final PassiveObject po: map.getObjects())
		{
			result.add(ex_.submit(new Callable<Integer>()
			{
				@Override
				public Integer call() throws Exception
				{
					final BandwidthInfo info = new BandwidthInfo();
					List<Avatar> inAOIList = inAOI.get(po);

					binfo.put(po.getID(), info);
					if (inAOIList == null)
					{
						inAOIList = new ArrayList<>();
						inAOI.put(po, inAOIList);
					}

					// for every avatar
					for (final Avatar a: map.getAllAvatar())
					{
						if (a.getAOI().contains(po.getPosition())) // in AOI
						{
							if (inAOIList.contains(a)) // already in AOI
							{
								logger.finest(a + " has "+po+" already in AOI");
								info.increaseNumRegularTransfers();
							}
							else // first time in AOI
							{
								logger.finest(a + " has "+po+" first time in AOI");
								inAOIList.add(a);
								info.increaseNumInitialTransfers();
							}
						}
						else // no more in AOI
						{
							inAOIList.remove(a);
						}
					}

					return 0;
				}

			}));

		}

		ParallelUtil.waitCompletion(result);

		return binfo;
	}

}