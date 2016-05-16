package ema.dve.workload.visual;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

public class EventManger implements WindowListener 
{
	JFrame mainFrame;
	
	public EventManger(JFrame mainFrame)
	{
		this.mainFrame = mainFrame;
	}
	
	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) 
	{
		// send a message to the server that the client is leaving
		try
		{
			mainFrame.dispose();
			System.exit(0);
		}
		catch (Exception ex) {ex.printStackTrace();}
	}

	@Override
	public void windowDeactivated(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowOpened(WindowEvent e) {}

}
