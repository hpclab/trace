package ema.dve.workload.visual;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ema.dve.workload.geom.Avatar;
import ema.dve.workload.geom.Hotspot;
import ema.dve.workload.geom.MapVirtualEnvironment;
import ema.dve.workload.geom.PassiveObject;

public class MapPanel extends JPanel
{
	private static final long serialVersionUID = 3701368508224024534L;

	private final Timer refreshTimer;
	private final MapVirtualEnvironment map;

	public MapPanel(final MapVirtualEnvironment map)
	{
		refreshTimer = new Timer();
		refreshTimer.schedule(new RefreshTask(this), 200, 200);

		this.map = map;
	}

	@Override
	public void paint(final Graphics g)
	{
		// dimension of the panel
		final Dimension d = this.getSize();
		final double mult_y = new Double(d.height) / new Double(map.getDimension().height);
		final double mult_x = new Double(d.width) / new Double(map.getDimension().width);

		// clear the screen
	    g.setColor(Color.WHITE);
	    g.fillRect(0, 0, d.width, d.height);

		// drawing hotspots
	    g.setColor(Color.BLACK);
		for(final Hotspot h: map.getHotspots())
		{
			final Point2D.Double center = new Point2D.Double(h.getPosition().x * mult_x, h.getPosition().y * mult_y);
			final Double radius_x = new Double(h.getRadius()) * mult_x;
			final Double radius_y = new Double(h.getRadius()) * mult_y;

			g.drawOval(new Double(center.x - radius_x).intValue(),
					new Double(center.y - radius_y).intValue(),
					radius_x.intValue() * 2,
					radius_y.intValue() * 2);
		}

		// drawing players
		for (final Avatar a: map.getAllAvatar())
		{
			g.setColor(Color.BLACK);

			final Point2D.Double original = a.getPositionOutput(map.getDimension());

			final Point2D.Double pos = new Point2D.Double(original.x * mult_x, original.y * mult_y);

		//	g.drawString(Integer.toString(a.getID()), new Double(pos.x - 2).intValue(), new Double(pos.y - 2).intValue());
			g.fillOval(new Double(pos.x - 2).intValue(),
					new Double(pos.y - 2).intValue(),
					2 * 2,
					2 * 2);

			// draw AOIs
			g.setColor(Color.RED);

			final Double radius_x = new Double(a.getAOIRadius()) * mult_x;
			final Double radius_y = new Double(a.getAOIRadius()) * mult_y;

			g.drawOval(new Double(pos.x - radius_x).intValue(),
					new Double(pos.y - radius_y).intValue(),
					radius_x.intValue() * 2,
					radius_y.intValue() * 2);
		}


		// drawing objects
		g.setColor(Color.BLUE);
		for (final PassiveObject po : map.getObjects())
		{
			final Point2D.Double pos = new Point2D.Double(po.getPosition().x * mult_x, po.getPosition().y * mult_y);
			g.fillOval(new Double(pos.x).intValue(), new Double(pos.y).intValue(), 5, 5);
		}

	}

	public static void initGraphics(final MapVirtualEnvironment map)
	{
		final MapPanel panel = new MapPanel(map);
		panel.setSize(500, 500);
		panel.setVisible(true);

		final JFrame f = new JFrame("Workload");
		f.setSize(500, 500);
		// f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.addWindowListener(new EventManger(f));
		f.add(panel);
		f.requestFocus();
		f.setLocationRelativeTo(null);
		// f.pack();


		f.setVisible(true);
	}


	class RefreshTask extends TimerTask
	{
		MapPanel map;

		public RefreshTask(final MapPanel map)
		{
			this.map = map;
		}

		@Override
		public void run()
		{
			map.repaint();
		}

	}

}