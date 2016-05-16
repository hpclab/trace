package ema.dve.workload.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import ema.dve.workload.Oracle;
import ema.dve.workload.OracleIncremental;

public class AOIStatAVGBean
{
	private int time;
	private double avatarInAoi;

	protected static final String[] header= new String[] {"time", "avatarInAOI"};

	protected static final CellProcessor[] processors = new CellProcessor[]
		 	{
					new ParseInt(), // time
		            new ParseDouble(), // id
		    };

	public AOIStatAVGBean(final int time, final double avatarInAoi)
	{
		super();
		this.time = time;
		this.avatarInAoi = avatarInAoi;
	}

	// empty constructor
	public AOIStatAVGBean()
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

	public double getAvatarInAOI()
	{
		return avatarInAoi;
	}


	public void setAvatarInAOI(final double a)
	{
		this.avatarInAoi = a;
	}

	protected static AOIStatAVGBean fromData(final int time_, final double avatarInAoi_)
	{
		return new AOIStatAVGBean(time_, avatarInAoi_);
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
			beanWriter.writeHeader(AOIStatAVGBean.header);

			// write the content
			double totalAvg = 0;
			for (int time=1; time<oracle.getIterations(); time++)
			{
				final Map<Integer, Integer> binfoAll = oracle.getAOIStatAt(time);

				double sum = 0;
				for (final Entry<Integer, Integer> po: binfoAll.entrySet())
				{
					sum += po.getValue();
				}
				totalAvg += (sum / binfoAll.size());

				final AOIStatAVGBean bb = AOIStatAVGBean.fromData(time, sum / binfoAll.size());
				beanWriter.write(bb, AOIStatAVGBean.header, AOIStatAVGBean.processors);
			}
			System.out.println("AVG Avatar in AOI: "+(totalAvg/oracle.getIterations()));

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
				beanWriter.writeHeader(AOIStatAVGBean.header);
			}

			// write the content
			double totalAvg = 0;
				final Map<Integer, Integer> binfoAll = oracle.getAOIStatAt(time);

				double sum = 0;
				for (final Entry<Integer, Integer> po: binfoAll.entrySet())
				{
					sum += po.getValue();
				}
				totalAvg += (sum / binfoAll.size());

				final AOIStatAVGBean bb = AOIStatAVGBean.fromData(time, sum / binfoAll.size());
				beanWriter.write(bb, AOIStatAVGBean.header, AOIStatAVGBean.processors);

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
