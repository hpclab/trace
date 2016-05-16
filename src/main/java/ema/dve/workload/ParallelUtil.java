package ema.dve.workload;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ParallelUtil
{
	public static <T> void waitCompletion(final List<Future<T>> result_)
	{
		for(final Future<T> f : result_)
		{
			try
			{
				f.get();
			} catch (final InterruptedException e)
			{
				e.printStackTrace();
			} catch (final ExecutionException e)
			{
				e.printStackTrace();
			}
		}
	}

}
