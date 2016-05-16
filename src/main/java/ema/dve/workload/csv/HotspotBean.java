package ema.dve.workload.csv;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import ema.dve.workload.Oracle;
import ema.dve.workload.OracleIncremental;
import ema.dve.workload.geom.Hotspot;
import ema.dve.workload.geom.MapVirtualEnvironment;

public class HotspotBean
{
	private int id;
	private double x;
	private double y;

	// the header elements are used to map the bean values to each column (names must match with cell processor)
	protected static final String[] header = new String[] {"id", "x", "y"};

	protected static final CellProcessor[] processors = new CellProcessor[]
		 	{
		            new ParseInt(), // id
		            new ParseDouble(), // x
		            new ParseDouble() // y
		    };


	public HotspotBean(final int id, final double x, final double y)
	{
		this.id = id;
		this.x = x;
		this.y = y;
	}

	/**
	 * Default empty constructor
	 */
	public HotspotBean()
	{
	}

	public void setId(final int id)
	{
		this.id = id;
	}

	public int getId()
	{
		return this.id;
	}

	public void setX(final double x)
	{
		this.x = x;
	}

	public double getX()
	{
		return this.x;
	}

	public void setY(final double y)
	{
		this.y = y;
	}

	public double getY()
	{
		return this.y;
	}

	protected static HotspotBean fromHotspot(final int time, final Hotspot hotspot)
	{
		return new HotspotBean(hotspot.getID(), hotspot.getPosition().x, hotspot.getPosition().y);
	}

	protected static void write(final File file, final Oracle oracle) throws IOException
	{
		ICsvBeanWriter beanWriter = null;

		try
		{
			// init the avatar writer
			beanWriter = new CsvBeanWriter(new FileWriter(file, true),
					CsvPreference.STANDARD_PREFERENCE);

			// write the header
			beanWriter.writeHeader(HotspotBean.header);

			// get all the content from the oracle
			final int time = 0; // we consider time zero since hotspots dont move
			final MapVirtualEnvironment map = oracle.getMapAt(time);
			final Hotspot[] hotspots = map.getHotspots();

			// create and write the beans
			for (int i=0; i<hotspots.length; i++)
			{
            	beanWriter.write(HotspotBean.fromHotspot(time, hotspots[i]),
            			HotspotBean.header, HotspotBean.processors);
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

	protected static void write(final File file, final OracleIncremental oracle) throws IOException
	{
		ICsvBeanWriter beanWriter = null;

		try
		{
			// init the avatar writer
			beanWriter = new CsvBeanWriter(new FileWriter(file, true),
					CsvPreference.STANDARD_PREFERENCE);

			// write the header
			beanWriter.writeHeader(HotspotBean.header);

			// get all the content from the oracle
			final int time = 0; // we consider time zero since hotspots dont move
			final MapVirtualEnvironment map = oracle.getMapAt(time);
			final Hotspot[] hotspots = map.getHotspots();

			// create and write the beans
			for (int i=0; i<hotspots.length; i++)
			{
            	beanWriter.write(HotspotBean.fromHotspot(time, hotspots[i]),
            			HotspotBean.header, HotspotBean.processors);
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

	protected static Hotspot[] read(final Reader reader, final double radius) throws IOException
	{
		final ICsvBeanReader beanReader = new CsvBeanReader(reader,
				CsvPreference.STANDARD_PREFERENCE);
		beanReader.getHeader(true);

		HotspotBean hotspotBean = beanReader.read(
				HotspotBean.class, HotspotBean.header, HotspotBean.processors);
		final List<Hotspot> hotspotList = new ArrayList<Hotspot>();

		while (hotspotBean != null)
		{
			final Hotspot h = new Hotspot(hotspotBean.getId(), new Point2D.Double(hotspotBean.getX(),
					hotspotBean.getY()), radius);
			hotspotList.add(h);

			 hotspotBean = beanReader.read(
						HotspotBean.class, HotspotBean.header, HotspotBean.processors);
		}

		beanReader.close();

		Hotspot[] hotspots = new Hotspot[hotspotList.size()];
		hotspots = hotspotList.toArray(hotspots);
		return hotspots;
	}
}
