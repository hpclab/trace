package ema.dve.workload.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import ema.dve.workload.BandwidthInfo;
import ema.dve.workload.Oracle;
import ema.dve.workload.OracleIncremental;

public class BandwidthBean
{
	private Integer time;
	private int id;
	private int numInitialTransfers;
	private int numRegularTransfers;

	protected static final String[] header= new String[] {"time", "id", "numInitialTransfers", "numRegularTransfers"};

	protected static final CellProcessor[] processors = new CellProcessor[]
		 	{
					new ParseInt(), // time
		            new ParseInt(), // id
		            new ParseInt(), // x
		            new ParseInt() // y
		    };

	public BandwidthBean(final Integer time, final int id, final int numInitialTransfers, final int numRegularTransfers)
	{
		super();
		this.time = time;
		this.id = id;
		this.numInitialTransfers = numInitialTransfers;
		this.numRegularTransfers = numRegularTransfers;
	}

	// empty constructor
	public BandwidthBean()
	{

	}


	public Integer getTime()
	{
		return time;
	}


	public void setTime(final Integer time)
	{
		this.time = time;
	}


	public int getId()
	{
		return id;
	}


	public void setId(final int id)
	{
		this.id = id;
	}


	public int getNumInitialTransfers()
	{
		return numInitialTransfers;
	}


	public void setNumInitialTransfers(final int numInitialTransfers)
	{
		this.numInitialTransfers = numInitialTransfers;
	}


	public int getNumRegularTransfers()
	{
		return numRegularTransfers;
	}


	public void setNumRegularTransfers(final int numRegularTransfers)
	{
		this.numRegularTransfers = numRegularTransfers;
	}

	protected static BandwidthBean fromData(final int time, final Integer poid, final BandwidthInfo info)
	{
		return new BandwidthBean(time, poid, info.getNumInitialTransfers(), info.getNumRegularTransfers());
	}

	protected static void read(final Reader reader, final Oracle oracle) throws IOException
	{
		final ICsvBeanReader beanReader = new CsvBeanReader(reader,
				CsvPreference.STANDARD_PREFERENCE);
		beanReader.getHeader(true);

		HashMap<Integer, BandwidthInfo> data =
				new HashMap<Integer, BandwidthInfo>();

		int current_time = 1;
		BandwidthBean bbean =
				beanReader.read(BandwidthBean.class, BandwidthBean.header, BandwidthBean.processors);

		while (bbean != null)
		{
			final BandwidthInfo b = new BandwidthInfo();
			b.setNumInitialTransfers(bbean.getNumInitialTransfers());
			b.setNumRegularTransfers(bbean.getNumRegularTransfers());

			// same iteration
			if (bbean.getTime() == current_time)
			{
				data.put(bbean.getId(), b);
			}
			else
			{
				// store the band in the oracle
				oracle.setBandAt(current_time, data);

				// create a new list and add the avatar
				data = new HashMap<Integer, BandwidthInfo>();
				data.put(bbean.getId(), b);

				// update old_time
				current_time = bbean.getTime();
			}

			bbean =	beanReader.read(BandwidthBean.class, BandwidthBean.header, BandwidthBean.processors);
		}

		// flush the data!
		oracle.setBandAt(current_time, data);

		//close reader
		beanReader.close();
	}

	protected static void write(final File file, final Oracle oracle) throws IOException
	{
		ICsvBeanWriter beanWriter = null;

		try
		{
			// init the writer (im writing on a string)
			beanWriter = new CsvBeanWriter(new FileWriter(file, true),
					CsvPreference.STANDARD_PREFERENCE);

			// write the header
			beanWriter.writeHeader(BandwidthBean.header);

			// write the content
			for (int time=1; time<oracle.getIterations(); time++)
			{
				final java.util.Map<Integer, BandwidthInfo> binfoAll = oracle.getBandAt(time);

				for (final Integer po: binfoAll.keySet())
				{
					final BandwidthInfo bi = binfoAll.get(po);
					final BandwidthBean bb = BandwidthBean.fromData(time, po, bi);
					beanWriter.write(bb, BandwidthBean.header, BandwidthBean.processors);
				}
			}

		}
		finally
        {
			if( beanWriter != null )
        	{
				beanWriter.close();
        	}
        }
	}

	protected static void write(final int time, final File file, final OracleIncremental oracle) throws IOException
	{
		ICsvBeanWriter beanWriter = null;

		try
		{
			// init the writer (im writing on a string)
			beanWriter = new CsvBeanWriter(new FileWriter(file, true),
					CsvPreference.STANDARD_PREFERENCE);

			// write the header
			if(time == 1)
			{
				beanWriter.writeHeader(BandwidthBean.header);
			}

			// write the content
				final java.util.Map<Integer, BandwidthInfo> binfoAll = oracle.getBandAt(time);

				for (final Integer po: binfoAll.keySet())
				{
					final BandwidthInfo bi = binfoAll.get(po);
					final BandwidthBean bb = BandwidthBean.fromData(time, po, bi);
					beanWriter.write(bb, BandwidthBean.header, BandwidthBean.processors);
				}

		}
		finally
        {
			if( beanWriter != null )
        	{
				beanWriter.close();
        	}
        }
	}
}
