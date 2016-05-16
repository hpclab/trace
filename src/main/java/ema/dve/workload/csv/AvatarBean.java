package ema.dve.workload.csv;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
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
import ema.dve.workload.geom.Avatar;
import ema.dve.workload.geom.Entity;
import ema.dve.workload.geom.MapVirtualEnvironment;

public class AvatarBean
{
	private Integer time;
	private int id;
	private double x;
	private double y;

	// the header elements are used to map the bean values to each column (names must match with cell processor)
	protected static final String[] header = new String[] { "time", "id", "x", "y"};

	protected static final CellProcessor[] processors = new CellProcessor[]
		 	{
					new ParseInt(), // time
		            new ParseInt(), // id
		            new ParseDouble(), // x
		            new ParseDouble() // y
		    };


	public AvatarBean(final Integer time, final int id, final double x, final double y)
	{
		this.time = time;
		this.id = id;
		this.x = x;
		this.y = y;
	}

	/**
	 * Default empty constructor
	 */
	public AvatarBean()
	{
	}

	public void setTime(final int time)
	{
		this.time = time;
	}

	public Integer getTime()
	{
		return this.time;
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


	protected static AvatarBean fromEntity(final int time, final Entity avatar, final Dimension dimension_)
	{
		final Point2D.Double pos = avatar.getPositionOutput(dimension_);
		return new AvatarBean(time, avatar.getID(), pos.x, pos.y);
	}


	protected static void read(final Reader reader, final MapVirtualEnvironment map, final Oracle oracle) throws IOException
	{
		final ICsvBeanReader beanReader = new CsvBeanReader(reader,
				CsvPreference.STANDARD_PREFERENCE);
		beanReader.getHeader(true);

		AvatarBean bean = beanReader.read(AvatarBean.class, AvatarBean.header, AvatarBean.processors);
		int current_time = 0;
		List<Entity> list = new ArrayList<Entity>();

		// while ((bean = beanReader.read(AvatarBean.class, this.header, this.processors)) != null)
		while (bean != null)
		{
			final Entity a = new Entity(bean.getId(), new Point2D.Double(bean.getX(), bean.getY()));

			// while the iteration remains the same..
			if (bean.getTime() == current_time)
			{
				list.add(a);
			}
			else // iteration has changed!
			{
				// store the list in the oracle
				Entity[] avatars = new Entity[list.size()];
				avatars = list.toArray(avatars);

				// add the avatars to the map, and add it to the oracle
				map.setAvatars(avatars);
				oracle.setMapAt(current_time, map);

				// create a new list and add the avatar
				list = new ArrayList<Entity>();
				list.add(a);

				// update old_time
				current_time = bean.getTime();
			}

			//read the next
			bean = beanReader.read(AvatarBean.class, AvatarBean.header, AvatarBean.processors);
		}

		// flush the data
		Entity[] avatars = new Entity[list.size()];
		avatars = list.toArray(avatars);
		map.setAvatars(avatars);
		oracle.setMapAt(current_time, map);

		//close reader
		beanReader.close();
	}

	protected static void write(final int time, final File file, final OracleIncremental oracle, final Dimension dimension_) throws IOException
	{
		ICsvBeanWriter beanWriter = null;

		try
		{
			// init the avatar writer
			beanWriter = new CsvBeanWriter(new FileWriter(file, true),
					CsvPreference.STANDARD_PREFERENCE);

			// write the header
			if(time == 1)
			{
				beanWriter.writeHeader(AvatarBean.header);
			}

			// get all the content from the oracle
				final MapVirtualEnvironment map = oracle.getMapAt(time);
				final Collection<Avatar> avatars = map.getAllAvatar();

				for (final Avatar a: avatars)
				{
	            	beanWriter.write(AvatarBean.fromEntity(time, a, dimension_), AvatarBean.header, AvatarBean.processors);
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

	protected static void write(final File file, final Oracle oracle, final Dimension dimension_) throws IOException
	{
		ICsvBeanWriter beanWriter = null;

		try
		{
			// init the avatar writer
			beanWriter = new CsvBeanWriter(new FileWriter(file, true),
					CsvPreference.STANDARD_PREFERENCE);

			// write the header
			beanWriter.writeHeader(AvatarBean.header);

			// get all the content from the oracle
			for (int time=0; time<oracle.getIterations(); time++)
			{
				final MapVirtualEnvironment map = oracle.getMapAt(time);
				final Collection<Avatar> avatars = map.getAllAvatar();

				for (final Avatar a: avatars)
				{
	            	beanWriter.write(AvatarBean.fromEntity(time, a, dimension_), AvatarBean.header, AvatarBean.processors);
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
}
