/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003-2007  Laurie Heyer
 *
 *   This program is free software; you can redistribute it and/or
 *   modify it under the terms of the GNU General Public License
 *   as published by the Free Software Foundation; either version 2
 *   of the License, or (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 *   Contact Information:
 *   Laurie Heyer
 *   Davidson College Dept. of Mathematics
 *   PO Box 6959
 *   Davidson, NC 28035
 *   UNITED STATES
 */

package magictool.image;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.process.ImageConverter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.SwingUtilities;

import magictool.DidNotFinishException;
import magictool.ExpFile;
import magictool.Gene;
import magictool.GrpFile;
import magictool.MainFrame;
import magictool.ProgressFrame;
import magictool.Project;


public class FlagFrame extends JInternalFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane scrollRight;
	private JScrollPane scrollLeft;
	private FlagImageDisplayPanel imageDisplayPanel;
	private JToolBar imageToolBar = new JToolBar();
	private JToggleButton zoomIn = new JToggleButton();
	private JToggleButton zoomOut = new JToggleButton();
	private JButton reset = new JButton();
	private JButton done = new JButton();
	//test
	//private JButton drawaline = new JButton();
	//END test
	private DecimalFormat df = new DecimalFormat();
	private JLabel statusBar = new JLabel();
	private TitledBorder titledBorder1;
	private JSplitPane jSplitPane1 = new JSplitPane();
	private JPanel jPanel1 = new JPanel();
	private JDialog contrastDialog = new JDialog();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JLabel contrastLabel = new JLabel();
	private JSlider contrastSlider = new JSlider();
	private JTextField contrastInputField = new JTextField();
	private JPanel jPanel2 = new JPanel();
	private Border border1;
	private Border border2;
	
	//begin menu
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu();
	private JMenuItem loadFlags = new JMenuItem();
	private JMenuItem saveFlags = new JMenuItem();
	private JMenuItem saveAsFlags = new JMenuItem();
	private JMenu flaggingMenu = new JMenu();
	private JMenuItem flagMenuItem = new JMenuItem();
	private JMenuItem flagGridMenuItem = new JMenuItem();
	private JMenuItem unflagGridMenuItem = new JMenuItem();
	private JMenuItem toggleFlagGridMenuItem = new JMenuItem();
	private JMenuItem clearFlags = new JMenuItem();
	
	private MainFrame main; //parent frame
	
	/**if the flags need saving*/
	private boolean needSaved = false;
	/**width of the image*/
	protected int width;
	/**height of the image*/
	protected int height;
	/**grid manager*/
	protected GridManager manager;
	/**flag manager*/
	protected FlagManager flagman;
	/**automatic flag manager*/
	protected FlagManager autoflagman;
	/**overlayed image*/
	protected Image image;
	/**project associated with this flagging frame*/
	protected Project project;
	/**file path for open grid file*/
	protected String gridFilePath = null;
	/**file path for open flag file*/
	protected String flagFilePath = null;
	/**file path for red image*/
	protected String redPath = null;
	/**file path for green image*/
	protected String greenPath = null;
	
	public FlagFrame(Project prj, GridManager m, FlagManager fm, FlagManager afm, String redPath, String greenPath, MainFrame main) {
		this.project=prj;
		this.manager = m;
		this.flagman = fm;
		this.autoflagman = afm;
		this.main = main;
		this.greenPath = greenPath;
		this.redPath = redPath;
		
		imageInit();
		
		try {
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void jbInit() throws Exception {
		
		titledBorder1 = new TitledBorder("");
		border1 = BorderFactory.createLineBorder(Color.white,1);
		border2 = BorderFactory.createLineBorder(Color.black,2);
		zoomIn.setText("Zoom In");
		zoomOut.setText("Zoom Out");
		zoomIn.setMaximumSize(new Dimension(85,30));
		zoomIn.setMinimumSize(new Dimension(85,30));
		zoomIn.setPreferredSize(new Dimension(85,30));
		zoomOut.setMaximumSize(new Dimension(85,30));
		zoomOut.setMinimumSize(new Dimension(85,30));
		zoomOut.setPreferredSize(new Dimension(85,30));
		
		reset.setMaximumSize(new Dimension(100,30));
		reset.setMinimumSize(new Dimension(100,30));
		reset.setPreferredSize(new Dimension(85,30));
		reset.setText("Fit to Screen");
		reset.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				fitToScreen_actionPerformed(e);
			}
		});
		
		done.setAlignmentX((float) 5.0);
		done.setMaximumSize(new Dimension(65, 30));
		done.setMinimumSize(new Dimension(65, 30));
		done.setPreferredSize(new Dimension(65, 30));
		done.setText("Done!");
		done.addActionListener(new java.awt.event.ActionListener() {
		  public void actionPerformed(ActionEvent e) {
		    done_actionPerformed(e);
		    }
		});
		
		statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
		
		this.setClosable(true);
		this.setIconifiable(true);
		this.setMaximizable(true);
		this.setResizable(true);
		this.getContentPane().setBackground(Color.lightGray);
		
		this.getContentPane().setLayout(borderLayout1);
		jSplitPane1.setOneTouchExpandable(true);
		contrastLabel.setText("Percent contrast change:");
		
		contrastSlider.setMinimum(0);
		contrastSlider.setMaximum(2000);
		contrastSlider.setMajorTickSpacing(500);
		contrastSlider.setMinorTickSpacing(250);
		contrastSlider.setPaintTicks(true);
		contrastSlider.setDoubleBuffered(true);
		contrastSlider.setMinimumSize(new Dimension(400, 49));
		contrastSlider.setPaintLabels(true);
		contrastSlider.addChangeListener(new javax.swing.event.ChangeListener(){
			public void stateChanged(ChangeEvent e) {
				contrastSlider_stateChanged(e);
			}
		});
		contrastInputField.setToolTipText("type in a new contrast percentage without\n" +
    									"the percentage sign (i.e. 3500) and hit\n" +
    									"enter.  100 is the natural contrast value.");
		contrastInputField.addActionListener(new java.awt.event.ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			contrastInputField_actionPerformed(e);
    		}
		});
		
		jPanel2.setBorder(BorderFactory.createEtchedBorder());
		jPanel2.setMaximumSize(new Dimension(300,110));
		jPanel2.setMinimumSize(new Dimension(200,55));
		jPanel2.setPreferredSize(new Dimension(250,80));
		
		this.getContentPane().add(imageToolBar, BorderLayout.NORTH);
		imageToolBar.add(zoomIn, null);
		imageToolBar.add(zoomOut, null);
		imageToolBar.add(reset, null);
		imageToolBar.add(done, null);
		imageToolBar.addSeparator(new Dimension(20,20));
		imageToolBar.add(jPanel2, null);
		imageToolBar.setFloatable(false);
		jPanel2.add(contrastLabel, null);
		jPanel2.add(contrastSlider, null);
		jPanel2.add(contrastInputField, null);
		
		//set up menu
		loadFlags.setText("Load Saved Flags...");
		loadFlags.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadFlags_actionPerformed(e);
			}
		});
		
		saveFlags.setText("Save Current Flags");
		saveFlags.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFlags_actionPerformed(e);
			}
		});
		saveFlags.setEnabled(false);
		
		saveAsFlags.setText("Save Current Flags As...");
		saveAsFlags.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveAsFlags_actionPerformed(e);
			}
		});
		
		fileMenu.setText("File");
		
		flagMenuItem.setText("Flagging by Gene Name...");
		flagMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				flagMenuItem_actionPerformed(e);
			}
		});
		
		flagGridMenuItem.setText("Flag an Entire Grid...");
		flagGridMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				flagGridMenuItem_actionPerformed(e);
			}
		});
		
		unflagGridMenuItem.setText("Unflag an Entire Grid...");
		unflagGridMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				unflagGridMenuItem_actionPerformed(e);
			}
		});

		toggleFlagGridMenuItem.setText("Toggle Flags on an Entire Grid...");
		toggleFlagGridMenuItem.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				toggleFlagGridMenuItem_actionPerformed(e);
			}
		});
		
		clearFlags.setText("Clear All Flags");
		clearFlags.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clearFlags_actionPerformed(e);
			}
		});
		
		flaggingMenu.setText("Flagging");
		
		menuBar.add(fileMenu);
		menuBar.add(flaggingMenu);
		fileMenu.add(loadFlags);
		fileMenu.addSeparator();
		fileMenu.add(saveFlags);
		fileMenu.add(saveAsFlags);
		flaggingMenu.add(flagMenuItem);
		flaggingMenu.add(flagGridMenuItem);
		flaggingMenu.add(unflagGridMenuItem);
		flaggingMenu.add(toggleFlagGridMenuItem);
		flaggingMenu.add(clearFlags);
		this.setJMenuBar(menuBar);
		
		scrollRight = new JScrollPane(imageDisplayPanel);
		scrollRight.getHorizontalScrollBar().setUnitIncrement(10);
		scrollRight.getVerticalScrollBar().setUnitIncrement(10);
		scrollRight.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
		    public void adjustmentValueChanged(AdjustmentEvent e){
		      if(scrollRight.getVerticalScrollBar().getValueIsAdjusting()){
		        statusBar.setText("Topmost Pixel: "+yCoordinate(e.getValue()));
		      }
		      else statusBar.setText(" ");
		    }
		});
		
		this.getContentPane().add(statusBar, BorderLayout.SOUTH);
		this.getContentPane().add(scrollRight, BorderLayout.CENTER);
		//this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
		//jSplitPane1.add(scrollRight, JSplitPane.RIGHT);

		imageDisplayPanel.ic.addMouseMotionListener(new MouseMotionListener(){
			public void mouseMoved(MouseEvent mm) {
				int xco = xCoordinate(mm.getX());
				int yco = yCoordinate(mm.getY());
				statusBar.setText("X: " + xco + " Y: " + yco);
				int gridloc = manager.getGridAtPoint(xco, yco);
				if (gridloc>=0){
					Point location = manager.getGrid(gridloc).getColRow(xco, yco);
					if(location!=null) statusBar.setText("X: " + xco + " Y: " + yco + " Gene: " + manager.getGeneName(gridloc, location.x, location.y) + " (Grid: " + (gridloc+1) + " Col: " + (manager.getTransformedColNum(gridloc, location.x)+1) + " Row: " + (manager.getTransformedRowNum(gridloc, location.y)+1) + " Spot Number: " + (manager.getTransformedSpotNum(gridloc, location.x, location.y)+1) + ")" );
				}
			}
			public void mouseDragged(MouseEvent md){}
		});
		
		imageDisplayPanel.ic.addMouseListener(new MouseAdapter(){
			//public void mouseClicked(MouseEvent mc) {
			public void mouseReleased(MouseEvent mc) {
				if (zoomIn.isSelected()) {
					int clickedX = mc.getX();
					int clickedY = mc.getY();
					imageDisplayPanel.zoom(1.25);
					int w = (scrollRight.getViewport().getWidth());
					int h = (scrollRight.getViewport().getHeight());
					int newTopLeftX = (int)((1.25*clickedX)-(w/2));
					int newTopLeftY = (int)((1.25*clickedY)-(h/2));
					scrollRight.getViewport().setViewPosition(new Point(newTopLeftX, newTopLeftY));
					setTitle("Overlayed Image (" + df.format(imageDisplayPanel.getZoom()*100) + "%)" + " - " + width + " x " + height);				
				}
				
				else if (zoomOut.isSelected()) {
					int clickedX = mc.getX();
					int clickedY = mc.getY();
					imageDisplayPanel.zoom(0.75);
					int w = (scrollRight.getViewport().getWidth());
					int h = (scrollRight.getViewport().getHeight());
					int newTopLeftX = (int)((0.75*clickedX)-(w/2));
			        int newTopLeftY = (int)((0.75*clickedY)-(h/2));
			        scrollRight.getViewport().setViewPosition(new Point(newTopLeftX, newTopLeftY));
			        setTitle("Overlayed Image (" + df.format(imageDisplayPanel.getZoom()*100) + "%)" + " - " + width + " x " + height);
				}
				else {
					//code for flagging spot
					int xco = xCoordinate(mc.getX());
					int yco = yCoordinate(mc.getY());
					int gridNum = manager.getGridAtPoint(xco, yco);
					if (gridNum >= 0) {
						Grid currGrid = manager.getGrid(gridNum);
						Point colRow = currGrid.getColRow(xco, yco);
						int transSpotNum = manager.getTransformedSpotNum(gridNum, colRow.x, colRow.y);
						flagman.toggleFlag(gridNum, transSpotNum);
						needSaved = true;
					}
					imageDisplayPanel.repaint();					
				}
			}
		});
	}
	
	/**
	 * Makes the flagging frame visible
	 */
	public void show(){
		super.show();
		contrastSlider.setSize(contrastSlider.getWidth()+1, contrastSlider.getHeight());
	}
	
	/**
	 * gets the panel where the image is displayed
	 * @return panel where the image is displayed
	 */
	public FlagImageDisplayPanel getDisplay() {
		return imageDisplayPanel;
	}
	
	/**
	 * sets the image to fill the screen
	 */
	public void fillScreen() {
		double level = (double)scrollRight.getWidth()/width;
		double level2 = (double)scrollRight.getHeight()/height;
		if(level2<level) level = level2;
		imageDisplayPanel.setMagnification(level);
		imageDisplayPanel.zoom(1.0);
		this.setTitle("Overlayed Image (" + df.format(imageDisplayPanel.getZoom()*100) + "%)" + " - " + width + " x " + height);
	}
	
	/**
	 * gets the actual x-coordinate on the image
	 * @param ex screen x-coordinate
	 * @return actual x-coordinate on the image
	 */
	public int xCoordinate(int ex) {
		return ((imageDisplayPanel.ic.getSrcRect().x+Math.round((float)((ex)/imageDisplayPanel.getZoom()))));
	}
	
	/**
	 * gets the actual y-coordinate on the image
	 * @param ey screen y-coordinate
	 * @returnactual y-coordinate on the image
	 */
	public int yCoordinate(int ey) {
		return ((imageDisplayPanel.ic.getSrcRect().y+Math.round((float)((ey)/imageDisplayPanel.getZoom()))));
	}
	
	private void done_actionPerformed(ActionEvent e) {
		this.dispose();
	}

	/*
	private void drawaline_actionPerformed(ActionEvent e) {
		imageDisplayPanel.drawLine(5,5,75,75);
	}
	*/
	
	private void contrastInputField_actionPerformed(ActionEvent e) {
		int inputContrast = (int)Double.parseDouble(contrastInputField.getText());		//XXX add warning dialog about recklessness of 5 million
		contrastSlider.setMajorTickSpacing(inputContrast/4);
		contrastSlider.setMinorTickSpacing(inputContrast/8);
		contrastSlider.createStandardLabels(inputContrast/4);
		contrastSlider.setMaximum(inputContrast);
		contrastSlider.repaint();
		contrastSlider.setValue(inputContrast);
	}
	
	private void contrastSlider_stateChanged(ChangeEvent e) {
		if(!contrastSlider.getValueIsAdjusting()){
	      Thread thread = new Thread(){
	        public void run(){
	          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
	          double contrast = (double)contrastSlider.getValue();
	          contrast/=100;
	          int w = image.getWidth(null);
	          int h = image.getHeight(null);
	          int[] pixels = new int[w*h];


	           PixelGrabber pg = new PixelGrabber(image, 0,0,w,h,pixels,0,w);

	             try{
	                pg.grabPixels();

	             }catch(Exception e3){System.out.print("Error");}

	             for(int i=0; i<pixels.length; i++){
	                int p = pixels[i];

	                int a = (p >> 24) & 0xFF;
	                int r = (p >> 16) & 0xFF;
	                int b = 0;
	                int g = (p >>  8) & 0xFF;
	                r=Math.round((float)(r*contrast));
	                //b=Math.round((float)(b*contrast));
	                g=Math.round((float)(g*contrast));
	                if(r>255) r=255;
	                //if(b>255) b=255;
	                if(g>255) g=255;

	                pixels[i] = (a << 24 | r << 16 | g << 8 | b);
	             }
	             imageDisplayPanel.ip.setImage(createImage(new MemoryImageSource(w,h,pixels,0,w)));
	             imageDisplayPanel.repaint();
	             setCursor(Cursor.getDefaultCursor());
	          }
	      };
	      thread.start();
	    }
	  }
	
	private void addButtonListeners() {
		//XXX next is addButtonListeners()
		//TODO: Figure out what it does.
	}
	
	private void fitToScreen_actionPerformed(ActionEvent e) {
		fillScreen();
		scrollRight.repaint();
	}
	
	/**
	 * initializes the image
	 */
	private void imageInit() {
		
		statusBar.setText("Loading Overlay Images...");
		
		Opener greenImage = new Opener();
		Opener redImage = new Opener();
		Image combined = greenImage.openImage(greenPath).getImage();
		Image red = redImage.openImage(redPath).getImage();
		
		//Dimension redDim = new Dimension(red.getWidth(null),red.getHeight(null));
		Dimension greenDim = new Dimension(combined.getWidth(null), combined.getHeight(null));
		int w = greenDim.width;
		int h = greenDim.height;
		
		int[] pixels = new int[w*h];
		int[] redpixels = new int[w*h];
		
		PixelGrabber pg = new PixelGrabber(combined,0,0,w,h,pixels,0,w);
		PixelGrabber redpg = new PixelGrabber(red,0,0,w,h,redpixels,0,w);
		try {
			pg.grabPixels();
			redpg.grabPixels();
		}
		catch(Exception e3)
		{
			System.out.print("(Error Grabbing Pixels!) " + e3);
		}
		
		for (int i = 0; i < pixels.length; i++) {
			int p = pixels[i];
			int redp = redpixels[i];
			int a = (p >> 24) & 0xFF;
			int r = (redp >> 16) & 0xFF;
			int b = 0;
			int g = (p >> 8) & 0xFF;
			
			pixels[i] = (a << 24 | r << 16 | g << 8 | b);
		}
		
		image = createImage(new MemoryImageSource(w,h,pixels,0,w));
		imageDisplayPanel = new FlagImageDisplayPanel(image, manager, flagman, autoflagman);
		statusBar.setText("Image overlayed successfully!");
		setClosable(true);
		width = w;
		height = h;
		
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	private void saveFlags_actionPerformed(ActionEvent e) {
		if(this.flagFilePath != null){
			try{
				flagman.writeFlagManager(flagFilePath);
				needSaved = false;
			}
			catch(Exception e1){
				JOptionPane.showMessageDialog(null, "MAGIC Tool Error!", "Error: Could not save flag file.",JOptionPane.ERROR_MESSAGE);
			}
		}
		else saveAsFlags_actionPerformed(e);
	}
	
	private void saveAsFlags_actionPerformed(ActionEvent e) {
		MainFrame.fileLoader.setFileFilter(MainFrame.fileLoader.flagFilter);
		MainFrame.fileLoader.setDialogTitle("Create New Flag File...");
		MainFrame.fileLoader.setApproveButtonText("Select");
		File f = new File(project.getPath()+"flags"+File.separator);
		if(!f.exists()) f.mkdirs();
		MainFrame.fileLoader.setCurrentDirectory(f);
		MainFrame.fileLoader.setSelectedFile(new File(""));
		
		int result = MainFrame.fileLoader.showSaveDialog(null);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				File fileobj = MainFrame.fileLoader.getSelectedFile();
				String path = fileobj.getAbsolutePath();
				if(!path.toLowerCase().endsWith(".flag")) path += ".flag";
				flagman.writeFlagManager(path);
				this.flagFilePath = path;
				saveFlags.setEnabled(true);
				needSaved = false;
			}
			catch(Exception e1){
				JOptionPane.showMessageDialog(null, "MAGIC Tool Error!", "Error: Could not save flag file.",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void loadFlags_actionPerformed(ActionEvent e) {
		MainFrame.fileLoader.setFileFilter(MainFrame.fileLoader.flagFilter);
		MainFrame.fileLoader.setDialogTitle("Load Flag File...");
		MainFrame.fileLoader.setApproveButtonText("Select");
		//System.out.println("About to execute problem line...");
		//System.out.println("My path will be... " + project.getPath() + "flags" + File.separator);
		File f = new File(project.getPath() + "flags" + File.separator); //I f1x0r3d this by actually getting the project as opposed to dereferencing null 
		//System.out.println("My file is" + f.getAbsolutePath());
		if(!f.exists()) f.mkdirs();
		MainFrame.fileLoader.setCurrentDirectory(f);
		MainFrame.fileLoader.setSelectedFile(null);
		
		int result = MainFrame.fileLoader.showOpenDialog(null);
		
		if (result == JFileChooser.APPROVE_OPTION) {
			try {
				File fileobj = MainFrame.fileLoader.getSelectedFile();
				String path = fileobj.getAbsolutePath();
				flagman.openFlagManager(path);
				saveFlags.setEnabled(true);
				imageDisplayPanel.repaint();
				addButtonListeners();
			}
			catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "Error: Could not load flag file.","MAGIC Tool Error!",JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void clearFlags_actionPerformed(ActionEvent e) {
		int goAhead = JOptionPane.showConfirmDialog(null,"Do you really want to erase all flags?", "MAGIC Tool Question", JOptionPane.YES_NO_OPTION);
		//System.out.println("My goAhead state is " + goAhead + ". NO state is " + JOptionPane.NO_OPTION + " and YES state is " + JOptionPane.YES_OPTION +".");
		if (goAhead == JOptionPane.YES_OPTION)
		{
			flagman.clearAllFlags();
			imageDisplayPanel.repaint();
			needSaved = false;
			saveFlags.setEnabled(false);
		}
	}
	
	private void flagMenuItem_actionPerformed(ActionEvent e) {
		FlagEditFrame fef = new FlagEditFrame(project, manager, flagman, main);
		fef.setParentFrame(this);
		this.getDesktopPane().add(fef);
		fef.setSize(400,400);
		fef.setLocation(100, 100);
		fef.show();
		fef.toFront();
	}
	
	private void flagGridMenuItem_actionPerformed(ActionEvent e) {
		String gridNumString;
		//get the grid number
		gridNumString = JOptionPane.showInputDialog(null, "Please enter the grid number (1-" + manager.getNumGrids() + ") for which all spots should be flagged:");
		try {
			int gridNum = Integer.parseInt(gridNumString);
			if (gridNum > manager.getNumGrids()) throw new IllegalGridNumberException();
			gridNum--;	//the user enters a grid number between 1 and numGrids, but we index from 0 to numGrids-1. Subtract 1 from gridNum to get what we want.
			//this is valid, let's do the thing.
			for (int i = 0; i < manager.getGrid(gridNum).getNumOfSpots(); i++) {
				flagman.flagSpot(gridNum, i);
			}
			this.repaint();
		}
		catch(NumberFormatException ex1) {
			JOptionPane.showMessageDialog(this, "Error: The value entered for the grid number does not appear to be a number.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(IllegalGridNumberException ex2) {
			JOptionPane.showMessageDialog(this, "Error: The grid number you entered is not a valid grid number. Please enter a grid number between 1 and " + manager.getNumGrids() + ".", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception ex3) {
			JOptionPane.showMessageDialog(this, "Error: Something unexpected happened.", "MAGIC Tool Unexpected Error", JOptionPane.ERROR_MESSAGE);
			ex3.printStackTrace();
		}
		
	}
	
	private void unflagGridMenuItem_actionPerformed(ActionEvent e) {
		String gridNumString;
		//get the grid number
		gridNumString = JOptionPane.showInputDialog(null, "Please enter the grid number (1-" + manager.getNumGrids() + ") for which all spots should be unflagged:");
		try {
			int gridNum = Integer.parseInt(gridNumString);
			if (gridNum > manager.getNumGrids()) throw new IllegalGridNumberException();
			gridNum--;	//the user enters a grid number between 1 and numGrids, but we index from 0 to numGrids-1. Subtract 1 from gridNum to get what we want.
			//this is valid, let's do the thing.
			for (int i = 0; i < manager.getGrid(gridNum).getNumOfSpots(); i++) {
				flagman.unflagSpot(gridNum, i);
			}
			this.repaint();
		}
		catch(NumberFormatException ex1) {
			JOptionPane.showMessageDialog(this, "Error: The value entered for the grid number does not appear to be a number.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(IllegalGridNumberException ex2) {
			JOptionPane.showMessageDialog(this, "Error: The grid number you entered is not a valid grid number. Please enter a grid number between 1 and " + manager.getNumGrids() + ".", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception ex3) {
			JOptionPane.showMessageDialog(this, "Error: Something unexpected happened.", "MAGIC Tool Unexpected Error", JOptionPane.ERROR_MESSAGE);
			ex3.printStackTrace();
		}
	}
	
	private void toggleFlagGridMenuItem_actionPerformed(ActionEvent e) {
		String gridNumString;
		//get the grid number
		gridNumString = JOptionPane.showInputDialog(null, "Please enter the grid number (1-" + manager.getNumGrids() + ") for which all spots should be toggled:");
		try {
			int gridNum = Integer.parseInt(gridNumString);
			if (gridNum > manager.getNumGrids()) throw new IllegalGridNumberException();
			gridNum--;	//the user enters a grid number between 1 and numGrids, but we index from 0 to numGrids-1. Subtract 1 from gridNum to get what we want.
			//this is valid, let's do the thing.
			for (int i = 0; i < manager.getGrid(gridNum).getNumOfSpots(); i++) {
				flagman.toggleFlag(gridNum, i);
			}
			this.repaint();
		}
		catch(NumberFormatException ex1) {
			JOptionPane.showMessageDialog(this, "Error: The value entered for the grid number does not appear to be a number.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(IllegalGridNumberException ex2) {
			JOptionPane.showMessageDialog(this, "Error: The grid number you entered is not a valid grid number. Please enter a grid number between 1 and " + manager.getNumGrids() + ".", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
		}
		catch(Exception ex3) {
			JOptionPane.showMessageDialog(this, "Error: Something unexpected happened.", "MAGIC Tool Unexpected Error", JOptionPane.ERROR_MESSAGE);
			ex3.printStackTrace();
		}
		
	}
	
	protected void refreshImage() {
		imageDisplayPanel.repaint();
	}
	
	public class IllegalGridNumberException extends IllegalArgumentException {
		/**
		 * 
		 */
		private static final long serialVersionUID = 392463211392265662L;
		public IllegalGridNumberException(){}
	}
}
