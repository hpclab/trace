package ema.dve.workload.csv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;

import org.supercsv.cellprocessor.ParseBool;
import org.supercsv.cellprocessor.ParseDouble;
import org.supercsv.cellprocessor.ParseInt;
import org.supercsv.cellprocessor.ParseLong;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanReader;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import ema.dve.workload.Oracle;
import ema.dve.workload.OracleIncremental;
import ema.dve.workload.configuration.PropertiesPersonalized;


public class ConfigurationBean
{
	private int hotspot_num;
	private int objects_num;
	private int map_width;
	private int map_height;
	private double hotspot_radius;
	private double aoi_radius;
	private int iteration_num;
	private long randomSeed;
	private boolean enableGraphic;
	private boolean enableDump;

	protected static final String[] confHeader = new String[] {"iterationNum","hotspotsNum", "objectsNum", "mapHeight", "mapWidth",
			"hotspotRadius", "avatarRadius", "randomSeed", "enableGraphic", "enableDump"};

	protected static final CellProcessor[] confProcessors = new CellProcessor[]
    {
			new ParseInt(), // iteration_num
			new ParseInt(), // hotspots_num
			new ParseInt(), // objects_num
			new ParseInt(), // mapHeight
			new ParseInt(), // mapWidth
			new ParseDouble(), // hotspot_radius
			new ParseDouble(),  // avatar_radius
			new ParseLong(), // random seed
			new ParseBool(), // enableGraphic
			new ParseBool(), // enableDump
   };

	public ConfigurationBean(final int iteration_num, final int hotspot_num, final int objects_num, final int map_width,
			final int map_height, final double hotspot_radius, final double avatar_radius, final long randomSeed, final boolean enableGraphic, final boolean enableDump)
	{
		this.iteration_num = iteration_num;
		this.hotspot_num = hotspot_num;
		this.objects_num = objects_num;
		this.map_width = map_width;
		this.map_height = map_height;
		this.hotspot_radius = hotspot_radius;
		this.aoi_radius = avatar_radius;
		this.randomSeed = randomSeed;
		this.enableDump = enableDump;
		this.enableGraphic = enableGraphic;
	}

	/**
	 * Default empty constructor
	 */
	public ConfigurationBean()
	{
	}

//	public GeneralConfiguration toConfiguration()
//	{
//		final GeneralConfiguration gc = new GeneralConfiguration();
//		gc.set("AOI_RADIUS, this.aoi_radius+"");
//		gc.set("ENABLE_DUMP, this.enableDump+"");
//		gc.set("ENABLE_GRAPHIC, this.enableGraphic+"");
//		gc.set("HOTSPOT_NUM, this.hotspot_num+"");
//		gc.set("HOTSPOT_RADIUS, this.hotspot_radius+"");
//		gc.set("ITERATION_NUM, this.iteration_num+"");
//		gc.set("MAP_HEIGHT, this.map_height+"");
//		gc.set("MAP_WIDTH, this.map_width+"");
//		final gc.sepublic Oracle readOracle() throws final Exception
//	{
//		System.out.println("CSV: reading from file: "+this.getFileName());
//
//		// init
//		Oracle oracle = null;
//		ZipFacility zf = new ZipFacility();
//		java.util.Map<String,String> zipData = zf.readZip(this.getFileName());
//
//		// read configuration first
//		ConfigurationBean cb = ConfigurationBean.read(new StringReader(zipData.get("configuration")));
//		oracle = new Oracle(cb.toConfiguration());
//
//
//		// create the map with the configuration just read
//		MapVirtualEnvironment map = new MapVirtualEnvironment(cb.toConfiguration());
//
//		// reading hotspots
//		Hotspot[] hotspots = HotspotBean.read(new StringReader(zipData.get("hotspots")), cb.getHotspotRadius());
//		map.setHotspots(hotspots);
//
//		// reading objects
//		PassiveObject[] objects = ObjectBean.read(new StringReader(zipData.get("objects")));
//		map.setObjects(objects);
//
//
//		// read avatars
//		AvatarBean.read(new StringReader(zipData.get("avatars")), map, oracle);
//
//		// read bandwidth
//		BandwidthBean.read(new StringReader(zipData.get("bandwidth")), oracle);
//
//
//		return oracle;
//	}

	public int getIterationNum()
	{
		return this.iteration_num;
	}

	public void setIterationNum(final int iteration_num)
	{
		this.iteration_num = iteration_num;
	}

	public int getHotspotsNum()
	{
		return this.hotspot_num;
	}

	public void setHotspotsNum(final int hotspots)
	{
		this.hotspot_num = hotspots;
	}

	public int getObjectsNum()
	{
		return this.objects_num;
	}

	public void setObjectsNum(final int objects)
	{
		this.objects_num = objects;
	}

	public int getMapWidth()
	{
		return this.map_width;
	}

	public void setMapWidth(final int width)
	{
		this.map_width = width;
	}

	public int getMapHeight()
	{
		return this.map_height;
	}

	public void setMapHeight(final int height)
	{
		this.map_height = height;
	}

	public double getHotspotRadius()
	{
		return this.hotspot_radius;
	}

	public void setHotspotRadius(final double radius)
	{
		this.hotspot_radius = radius;
	}

	public double getAvatarRadius()
	{
		return this.aoi_radius;
	}

	public void setAvatarRadius(final double radius)
	{
		this.aoi_radius = radius;
	}

	public long getRandomSeed()
	{
		return this.randomSeed;
	}

	public void setRandomSeed(final long seed)
	{
		this.randomSeed = seed;
	}

	public String getEnableGraphic()
	{
		return this.enableGraphic+"";
	}

	public void setEnableGraphic(final boolean enableGraphic)
	{
		this.enableGraphic = enableGraphic;
	}

	public String getEnableDump()
	{
		return this.enableDump+"";
	}

	public void setEnableDump(final boolean enableDump)
	{
		this.enableDump = enableDump;
	}

	protected static ConfigurationBean fromConfiguration(final PropertiesPersonalized conf)
	{
		final int iteration_num = conf.getPropertyInt("ITERATION_NUM");
		final int objects = conf.getPropertyInt("OBJECTS_NUM");
		final int hotspots = conf.getPropertyInt("HOTSPOT_NUM");
		final double hotspot_radius = conf.getPropertyDouble("HOTSPOT_RADIUS");
		final double avatar_radius = conf.getPropertyDouble("AOI_RADIUS");
		final int map_width = conf.getPropertyInt("MAP_WIDTH");
		final int map_height = conf.getPropertyInt("MAP_HEIGHT");
		final long randomseed = conf.getPropertyLong("RANDOM_SEED");
		final boolean enableDump = conf.getPropertyBoolean("ENABLE_DUMP");
		final boolean enableGraphic = conf.getPropertyBoolean("ENABLE_GRAPHIC");

		final ConfigurationBean cb = new ConfigurationBean(iteration_num, hotspots, objects, map_width,
				map_height, hotspot_radius, avatar_radius, randomseed, enableGraphic, enableDump);

		return cb;
	}

	protected static ConfigurationBean read(final Reader reader) throws IOException
	{
		final ICsvBeanReader beanReader = new CsvBeanReader(reader,
				CsvPreference.STANDARD_PREFERENCE);

		beanReader.getHeader(true);
		final ConfigurationBean cb = beanReader.read(ConfigurationBean.class,
				ConfigurationBean.confHeader, ConfigurationBean.confProcessors);

		beanReader.close();
		return cb;
	}

	protected static void write(final File file, final Oracle oracle) throws IOException
	{
		ICsvBeanWriter beanWriter = null;

		try
		{
			// init the writer
			beanWriter = new CsvBeanWriter(new FileWriter(file, true),
					CsvPreference.STANDARD_PREFERENCE);

			// write the header
			beanWriter.writeHeader(ConfigurationBean.confHeader);

			// write the configuration
			final ConfigurationBean cb = ConfigurationBean.fromConfiguration(oracle.getConfiguration());
			beanWriter.write(cb, ConfigurationBean.confHeader, ConfigurationBean.confProcessors);
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
			// init the writer
			beanWriter = new CsvBeanWriter(new FileWriter(file, true),
					CsvPreference.STANDARD_PREFERENCE);

			// write the header
			beanWriter.writeHeader(ConfigurationBean.confHeader);

			// write the configuration
			final ConfigurationBean cb = ConfigurationBean.fromConfiguration(oracle.getConfiguration());
			beanWriter.write(cb, ConfigurationBean.confHeader, ConfigurationBean.confProcessors);
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
