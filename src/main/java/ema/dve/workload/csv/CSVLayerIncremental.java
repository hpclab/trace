package ema.dve.workload.csv;

import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import ema.dve.workload.OracleIncremental;
import ema.dve.workload.configuration.PropertiesPersonalized;

public class CSVLayerIncremental
{

	private final String fileName;
	private final Dimension _dimension;

	final File tmp_avatars = new File("tmp_avatars");
	final File tmp_configuration = new File("tmp_configuration");
	final File tmp_hotspots = new File("tmp_hotspots");
	final File tmp_objects = new File("tmp_objects");
	final File tmp_bandwidth = new File("tmp_bandwidth");
	final File tmp_aoiStat = new File("tmp_aoiStat");
	final File tmp_aoiStatAVG = new File("tmp_aoiStatAVG");

	/**
	 * Public constructor
	 */
	public CSVLayerIncremental(final PropertiesPersonalized property_, final Dimension dimension_)
	{
		final Date date = new Date() ;
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy_HH-mm") ;
		this.fileName = property_.getModelName()+"_"+ dateFormat.format(date) + ".zip";

		_dimension = dimension_;
	}
//
//	public CSVLayer(final String filename)
//	{
//		this.fileName = filename;
//	}

//	public void setFileName(final String filename)
//	{
//		this.fileName = filename;
//	}

	public String getFileName()
	{
		return this.fileName;
	}

	public void writeZip()
	{
		final ZipFacility zf = new ZipFacility();

		try
		{
			// these strings will store the content
			final String configuration_string = this.fileToStringAndDelete(tmp_configuration);
			final String avatars_string = this.fileToStringAndDelete(tmp_avatars);
			final String hotspot_string = this.fileToStringAndDelete(tmp_hotspots);
			final String objects_string = this.fileToStringAndDelete(tmp_objects);
			final String bandwidth_string = this.fileToStringAndDelete(tmp_bandwidth);
			final String aoiStat_string = this.fileToStringAndDelete(tmp_aoiStat);
			final String aoiStatAVG_string = this.fileToStringAndDelete(tmp_aoiStatAVG);

			final HashMap<String, String> data = new HashMap<String, String>();
			data.put("avatars", avatars_string);
			data.put("configuration", configuration_string);
			data.put("objects", objects_string);
			data.put("hotspots", hotspot_string);
			data.put("bandwidth", bandwidth_string);
			data.put("aoiStat", aoiStat_string);
			data.put("aoiStatAVG", aoiStatAVG_string);

			zf.createZip(this.fileName, data);
		}
		catch (final Exception e) { e.printStackTrace();}
	}

	public void writeStatic(final OracleIncremental oracle) throws IOException
	{
		System.out.println("CSV: writing on file: "+this.getFileName());

		// *** WRITE THE HOTSPOT POSITION
		HotspotBean.write(tmp_hotspots, oracle);

		// *** WRITE THE OBJECTS
		ObjectBean.write(tmp_objects, oracle);
		// *** WRITE THE CONFIGURATION
		ConfigurationBean.write(tmp_configuration, oracle);

	}

	public void writeOracle(final int time, final OracleIncremental oracle) throws IOException
	{
		System.out.println("CSV: writing on file: "+this.getFileName());

		// *** WRITE THE AVATARS POSITION
		AvatarBean.write(time, tmp_avatars, oracle, _dimension);

		// *** WRITE THE BANDWIDTH
		BandwidthBean.write(time, tmp_bandwidth, oracle);

		AOIStatBean.write(time, tmp_aoiStat, oracle);

		AOIStatAVGBean.write(time, tmp_aoiStatAVG, oracle);
	}

	private String fileToStringAndDelete(final File file) throws IOException
	{
		final BufferedReader br = new BufferedReader(new FileReader(file));
		final StringBuilder sb = new StringBuilder();
		while (br.ready())
		{
			sb.append(br.readLine()).append("\n");
		}

		br.close();
		file.delete();
		return sb.toString();
	}

//	public Oracle readOracle() throws Exception
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
}
