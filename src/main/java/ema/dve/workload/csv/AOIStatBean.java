package ema.dve.workload.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import ema.dve.workload.Oracle;
import ema.dve.workload.OracleIncremental;

public class AOIStatBean
{
	private int time;
	private int id;
	private int avatarInAoi;

	protected static final String[] header= new String[] {"time", "id", "avatarInAOI"};

	protected static final CellProcessor[] processors = new CellProcessor[]
		 	{
					new ParseInt(), // time
		            new ParseInt(), // id
		            new ParseInt(), // x
		    };

	public AOIStatBean(final int time, final int id, final int avatarInAoi)
	{
		super();
		this.time = time;
		this.id = id;
		this.avatarInAoi = avatarInAoi;
	}

	// empty constructor
	public AOIStatBean()
	{

	}


	public int getTime()
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


	public int getAvatarInAOI()
	{
		return avatarInAoi;
	}


	public void setAvatarInAOI(final int a)
	{
		this.avatarInAoi = a;
	}

	protected static AOIStatBean fromData(final int time_, final int id_, final int avatarInAoi_)
	{
		return new AOIStatBean(time_, id_, avatarInAoi_);
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
			beanWriter.writeHeader(AOIStatBean.header);

			// write the content
			for (int time=1; time<oracle.getIterations(); time++)
			{
				final Map<Integer, Integer> binfoAll = oracle.getAOIStatAt(time);

				for (final Entry<Integer, Integer> po: binfoAll.entrySet())
				{
					final AOIStatBean bb = AOIStatBean.fromData(time, po.getKey(), po.getValue());
					beanWriter.write(bb, AOIStatBean.header, AOIStatBean.processors);
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
				beanWriter.writeHeader(AOIStatBean.header);
			}

			// write the content
				final Map<Integer, Integer> binfoAll = oracle.getAOIStatAt(time);

				for (final Entry<Integer, Integer> po: binfoAll.entrySet())
				{
					final AOIStatBean bb = AOIStatBean.fromData(time, po.getKey(), po.getValue());
					beanWriter.write(bb, AOIStatBean.header, AOIStatBean.processors);
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
