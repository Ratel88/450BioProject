package ij.plugin.frame;
import ij.IJ;
import ij.ImageJ;
import ij.Menus;
import ij.WindowManager;
import ij.plugin.PlugIn;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**  This is a closeable window that plugins can extend. */
public class PlugInFrame extends Frame implements PlugIn, WindowListener, FocusListener {

	String title;
	
	public PlugInFrame(String title) {
		super(title);
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.title = title;
		ImageJ ij = IJ.getInstance();
		addWindowListener(this);
 		addFocusListener(this);
		if (ij!=null) {
			Image img = ij.getIconImage();
			if (img!=null) setIconImage(img);
		}
		if (IJ.debugMode) IJ.log("opening "+title);
	}
	
	public void run(String arg) {
	}
	
    public void windowClosing(WindowEvent e) {
		setVisible(false);
		dispose();
		WindowManager.removeWindow(this);
		if (IJ.debugMode) IJ.log("closing "+title);
    }

    public void windowActivated(WindowEvent e) {
		if (IJ.isMacintosh() && IJ.getInstance()!=null)
			setMenuBar(Menus.getMenuBar());
	}

	public void focusGained(FocusEvent e) {
		WindowManager.setWindow(this);
	}

    public void windowOpened(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
	public void focusLost(FocusEvent e) {}
}