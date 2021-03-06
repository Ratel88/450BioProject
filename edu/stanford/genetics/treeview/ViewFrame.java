/* BEGIN_HEADER                                              Java TreeView
 *
 * $Author: nidovidio $
 * $RCSfile: ViewFrame.java,v $
 * $Revision: 1.3 $
 * $Date: 2007/06/05 14:52:10 $
 * $Name:  $
 *
 * This file is part of Java TreeView
 * Copyright (C) 2001-2003 Alok Saldanha, All Rights Reserved. Modified by Alex Segal 2004/08/13. Modifications Copyright (C) Lawrence Berkeley Lab.
 *
 * This software is provided under the GNU GPL Version 2. In particular,
 *
 * 1) If you modify a source file, make a comment in it containing your name and the date.
 * 2) If you distribute a modified version, you must do it under the GPL 2.
 * 3) Developers are encouraged but not required to notify the Java TreeView maintainers at alok@genome.stanford.edu when they make a useful addition. It would be nice if significant contributions could be merged into the main distribution.
 *
 * A full copy of the license can be found in gpl.txt or online at
 * 
 * http://www.gnu.org/licenses/gpl.txt *
 * END_HEADER
 */

package edu.stanford.genetics.treeview;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import edu.stanford.genetics.treeview.core.FileMru;
import edu.stanford.genetics.treeview.model.DataModelWriter;
import edu.stanford.genetics.treeview.model.ReorderedDataModel;





/**
 *  Any frame that wants to contain MainPanels must extend this.
 *
 * @author     Alok Saldanha <alok@genome.stanford.edu>
 * @version    @version $Revision: 1.3 $ $Date: 2007/06/05 14:52:10 $
 */
public abstract class ViewFrame extends JFrame implements Observer {
	// must override in subclass...
	/**
	 *  This is to ensure that we can observe the MainPanels when they change.
	 *
	 * @param  observable  The MainPanel or other thing which changed.
	 * @param  object      Generally null.
	 */
	public abstract void update(Observable observable, Object object);


	/**
	 * This routine should return any instances of the plugin
	 * of the indicated name (i.e. it will loop over all instantiated
	 * MainPanel calling their getName() properties, find all that are
	 * equal to the indicated string, and return all matching ones
	 */
	public abstract MainPanel[] getMainPanelsByName(String name);
	
	/**
	 * 
	 * @return all mainPanels managed by this viewFrame
	 */
	public abstract MainPanel[] getMainPanels();
	
	/**
	 *  Sets up a <code>FileMru</code> using a particular config node.
	 * This <code>FileMru</code> can later be edited or used to show a mru menu.
	 *
	 * @param  fileMruNode  Node which will be bound to the FileMru
	 */
	protected void setupFileMru(ConfigNode fileMruNode) {
		fileMru = new FileMru();
		fileMru.bindConfig(fileMruNode);
		try {
			fileMru.removeMoved();
		} catch (Exception e) {
			LogBuffer.println("problem checking MRU in ViewFrame constructor: " + e.toString());
			e.printStackTrace();
		}

		fileMru.addObserver(this);
		fileMru.notifyObservers();//sends us message
	}


	/**
	 *  Centers the frame onscreen.
	 *
	 * @param  rectangle  A rectangle describing the outlines of the screen.
	 */
	private void center(Rectangle rectangle) {
		Dimension dimension  = getSize();
		setLocation((rectangle.width - dimension.width) / 3 + rectangle.x, (rectangle.height - dimension.height) / 3 + rectangle.y);
	}


	/**  Determines dimension of screen and centers frame onscreen. */
	public void centerOnscreen() {
		// trying this for mac...
		Toolkit toolkit      = Toolkit.getDefaultToolkit();
		Dimension dimension  = toolkit.getScreenSize();
		Rectangle rectangle  = new Rectangle(dimension);

		// XXX should drag out of global config
		setSize(rectangle.width * 3 / 4, rectangle.height * 4 / 5);
		center(rectangle);
	}


	/**  Sets a listener on self, so taht we can grab focus when activated, and close ourselves when closed.*/
	private void setupWindowListener() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(
			new WindowAdapter() {
				public void windowActivated(WindowEvent windowEvent) {
					setWindowActive(true);
				}


				public void windowClosing(WindowEvent windowEvent) {
					closeWindow();
				}


				public void windowDeactivated(WindowEvent windowEvent) {
					setWindowActive(false);
				}
			});
	}


	/**
	 *  Constructor for the ViewFrame object
	 * Sets title and window listeners
	 *
	 * @param  title  Title for the viewframe.
	 */
	public ViewFrame(String title) {
		super(title);
		setupWindowListener();
	}


	/**  construts an untitled <code>ViewFrame</code> */
	public ViewFrame() {
		super();
		setupWindowListener();
	}


	/**
	 *  Keep track of when active, so that clicks don't get passed through too much.
	 *
	 * @param  flag  The new windowActive value
	 */
	protected void setWindowActive(boolean flag) {
		windowActive = flag;
	}


	/**
	 *  Keep track of when active, so that clicks don't get passed through too much.
	 *
	 * @return    True if window is active.
	 */
	public boolean windowActive() {
		return windowActive;
	}


	/**  Keep track of when active, so that clicks don't get passed through too much. */
	private boolean windowActive;


	/**  close window cleanly. 
	* causes documentConfig to be stored. 
	*/
	public void closeWindow() {
		try {
			DataModel dataModel  = getDataModel();
			if (dataModel != null) {
			  	if (dataModel.getModified()) {
			  		int option = JOptionPane.showConfirmDialog(this, "DataModel is modified. Do you wish to save?");
			  		switch (option) {
			  			case JOptionPane.YES_OPTION:
			  		  		DataModelWriter writer = new DataModelWriter(getDataModel());
			  				writer.writeIncremental(getDataModel().getFileSet());
			  				break;
			  			case JOptionPane.CANCEL_OPTION:
			  				return;
			  			case JOptionPane.NO_OPTION:
			  				break;
			  		}
			  	}				
				
				ConfigNode documentConfig  = dataModel.getDocumentConfig();
				if (documentConfig != null) {
					documentConfig.store();
				}
			}
		} catch (Exception e) {
			System.out.println("ViewFrame.closeWindow() Got exception: " + e);
		}
		
		//This may not be what we want for MAGIC tool...
		this.setDefaultCloseOperation(this.HIDE_ON_CLOSE);
	}


	/**
	 *  required by all <code>ModelPanel</code>s
	 *
	 * @return   The shared TreeSelection object for genes.
	 */
	public TreeSelectionI getGeneSelection() {
		return geneSelection;
	}
	protected void setGeneSelection(TreeSelectionI newSelection) {
		geneSelection = newSelection;
	}

	/**
	 *  required by all <code>ModelPanel</code>s
	 *
	 * @return   The shared TreeSelection object for arrays.
	 */
	public TreeSelectionI getArraySelection() {
		return arraySelection;
	}
	protected void setArraySelection(TreeSelectionI newSelection) {
		arraySelection = newSelection;
	}

	/**
	 *  used by data model to signal completion of loading.
	 * The <code>ViewFrame</code> will react by reconfiguring it's widgets.
	 *
	 * @param  b  The new loaded value
	 */
	public abstract void setLoaded(boolean b);


	/**
	 *  returns special nodata value.
	 *  generally, just cribs from the <code>DataModel</code>
	 *
	 * @return    A special double which means nodata available.
	 */

	public abstract double noData();


	/**
	 *  returns the UrlPresets for the views to make use of when configuring linking
	 *  for genes
	 *
	 * @return    The shared <code>UrlPresets</code> object for genes
	 */
	public abstract UrlPresets getGeneUrlPresets();


	/**
	 *  returns the UrlPresets for the views to make use of when configuring linking
	 *  for arrays
	 *
	 * @return    The shared <code>UrlPresets</code> object for arrays
	 */
	public abstract UrlPresets getArrayUrlPresets();

	/**
	 *  Gets the loaded attribute of the ViewFrame object
	 *
	 * @return    True if there is currently a model loaded.
	 */
	public abstract boolean getLoaded();


	/**
	 *  Gets the shared <code>DataModel</code>
	 *
	 * @return    Gets the shared <code>DataModel</code>
	 */
	public abstract DataModel getDataModel();

	/**
	 *  Sets the shared <code>DataModel</code>
	 *
	 * @return    Sets the shared <code>DataModel</code>
	 * @throws LoadException
	 */
	public abstract void setDataModel(DataModel model);

	/**
	 *  Should scroll all MainPanels in this view frame to the specified gene.
	 *  The index provided is respect to the TreeSelection object.
	 *
	 * @param  i  gene index in model to scroll the mainpanel to.
	 */
	public abstract void scrollToGene(int i);
	public abstract void scrollToArray(int i);

	/**  The shared selection objects */
	TreeSelectionI geneSelection = null;
	TreeSelectionI arraySelection = null;
	public void deselectAll() {
	  geneSelection.deselectAllIndexes();
	  arraySelection.deselectAllIndexes();
	}
	/***
	* This routine causes all data views to 
	* select and scroll to a particular gene.
	*/
	public void seekGene(int i) {
	  geneSelection.deselectAllIndexes();
	  geneSelection.setIndex(i, true);
	  geneSelection.notifyObservers();
	  scrollToGene(i);
    }
	/***
	* This routine causes all data views to 
	* select and scroll to a particular array.
	*/
	public void seekArray(int i) {
	  arraySelection.deselectAllIndexes();
	  arraySelection.setIndex(i, true);
	  arraySelection.notifyObservers();
	  scrollToGene(i);
    }
	
	/**
	* This routine extends the selected range to include the index 
	* i.
	*/
	public void extendRange(int i) {
	  if (geneSelection.getMinIndex() == -1)
		seekGene(i);
	  geneSelection.setIndex(i, true);
	  geneSelection.notifyObservers();

	  scrollToGene(i);
    }
	
    public boolean geneIsSelected(int i) {
	  return getGeneSelection().isIndexSelected(i);
    }


	/**
	 *  url linking support
	 *
	 * @param  i  index of gene who's url you would like to display.
	 */
	public void displayURL(int i) {
		displayURL(getUrl(i));
	}


	/**
	 *  Gets the url for a particular gene.
	 *
	 * @param  i  index of the gene, for the gene's <code>UrlExtractor</code>
	 * @return    A string representation of the url
	 */
	public String getUrl(int i) {
		if (urlExtractor == null) {
			return null;
		}
		return urlExtractor.getUrl(i);
	}


	/**
	 *  Gets the url for a particular array.
	 *
	 * @param  i  index of the array, for the array's <code>UrlExtractor</code>
	 * @return    A string representation of the url
	 */
	public String getArrayUrl(int i) {
		if (arrayUrlExtractor == null) {
			return null;
		}
		return arrayUrlExtractor.getUrl(i);
	}


	/**
	 *  Pops up a browser window with the specified url
	 *
	 * @param  string  String representation of the url.
	 */
	public void displayURL(String string) {
		if (string == null) {
			return;
		}
		try {
			if (browserControl == null) {
				browserControl = BrowserControl.getBrowserControl();
			}
			browserControl.displayURL(string);
		} catch (MalformedURLException e) {
			String message = new StringBuffer("Problem loading url: ").append(e).toString();
			LogBuffer.println(message);
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,message);
		} catch (IOException e) {
			String message = new StringBuffer("Could not load url: ").append(e).toString();
			LogBuffer.println(message);
			e.printStackTrace();
			JOptionPane.showMessageDialog(this,message);
		}

	}


	/**
	 *  Gets the UrlExtractor for the arrays.
	 *
	 * This object is used to convert a given array index into a url string. It can be configured to do this in multiple ways.
	 *
	 * @return    The UrlExtractor for the arrays
	 */
	public UrlExtractor getArrayUrlExtractor() {
		return arrayUrlExtractor;
	}


	/**
	 *  Gets the UrlExtractor for the genes.
	 *
	 * This object is used to convert a given gene index into a url string. It can be configured to do this in multiple ways.
	 *
	 * @return    The UrlExtractor for the genes
	 */
	public UrlExtractor getUrlExtractor() {
		return urlExtractor;
	}


	/**
	 *  Sets the arrayUrlExtractor attribute of the ViewFrame object
	 *
	 * @param  ue  The new arrayUrlExtractor value
	 */
	public void setArrayUrlExtractor(UrlExtractor ue) {
		arrayUrlExtractor = ue;
	}


	/**
	 *  Sets the urlExtractor attribute of the ViewFrame object
	 *
	 * @param  ue  The new urlExtractor value
	 */
	public void setUrlExtractor(UrlExtractor ue) {
		urlExtractor = ue;
	}

	abstract public HeaderFinder getGeneFinder();

/**
* Open a dialog which allows the user to select a new data file
*
* @return The fileset corresponding to the dataset.
*/
		 protected FileSet offerSelection()
	 throws LoadException
	 {
		 FileSet fileSet1; // will be chosen...
		 
		 JFileChooser fileDialog = new JFileChooser();
		 setupFileDialog(fileDialog);
		 int retVal = fileDialog.showOpenDialog(this);
		 if (retVal == JFileChooser.APPROVE_OPTION) {
			 File chosen = fileDialog.getSelectedFile();
			 
			 fileSet1 = new FileSet(chosen.getName(), chosen.getParent()+File.separator);
/*			 
			 // check existing file nodes...
			 ConfigNode aconfigNode[] = fileMru.getConfigs();
			 for (int i = 0; i < aconfigNode.length; i++) {
				 FileSet fileSet2 = new FileSet(aconfigNode[i]);
				 if (fileSet2.equals(fileSet1)) {
					 LogPanel.println("Found Existing node in MRU list for " + fileSet1);
					 return fileSet2;
				 }
			 }
*/
		 } else {
			 throw new LoadException("File Dialog closed without selection...", LoadException.NOFILE);
		 }
		 
		  /* Don't enforce suffixes...
		 // see if we match at all...
		 try {
			 if (!ff.accept(null, fileSet1.getCdt()))
				 throw new LoadException(fileSet1.getCdt() + 
				 " did not end in .cdt or .pcl", LoadException.EXT);
		 } catch (NullPointerException e) {
			 throw new LoadException(e + ",most likely, no file selected so cdt is null", LoadException.NOFILE);
		 }
		 */

		  /*
		  ConfigNode configNode = fileMru.createSubNode();
		  fileMru.setLast(configNode);
		  FileSet fileSet3 = new FileSet(configNode);
		  fileSet3.copyState(fileSet1);
		  fileMru.notifyObservers();
		  */
		 return fileSet1;
	 }

	 protected void setupFileDialog(JFileChooser fileDialog) {
		CdtFilter ff = new CdtFilter();
		try {
			fileDialog.addChoosableFileFilter(ff);
			// will fail on pre-1.3 swings
			fileDialog.setAcceptAllFileFilterUsed(true);
		} catch (Exception e) {
			// hmm... I'll just assume that there's no accept all.
			fileDialog.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {
				public boolean accept (File f) {
					return true;
				}
				public String getDescription () {
					return "All Files";
				}
			});
		}
		fileDialog.setFileFilter(ff);
		fileDialog.setFileSelectionMode(JFileChooser.FILES_ONLY);
		String string = fileMru.getMostRecentDir();
		if (string != null) {
			fileDialog.setCurrentDirectory(new File(string));
		}
	 }
	 
	 
	/**
	 *  Rebuild a particular window menu.
	 *
	 * @param  windows  the list of windows to add elements to.
	 *
	 * Add a menu item for each window which grants that window the foreground when selected.
	 */
	 public void rebuildWindowMenu(Vector windows) {
		 windowMenu.removeAll();
		 MenuItem closeItem = new MenuItem("Close Window", new MenuShortcut(KeyEvent.VK_W));
		 closeItem.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent actionEvent) {
				 closeWindow();
			 }
		 });
		 MenuItem newItem = new MenuItem("New Window", new MenuShortcut(KeyEvent.VK_N));
		 newItem.addActionListener(new ActionListener() {
			 public void actionPerformed(ActionEvent actionEvent)
			 {
				 createNewFrame().show();
			 }
		 });

		 int max  = windows.size();
		 for (int i = 0; i < max; i++) {
			 if (i > 8) {
				 return;
			 }// just want first 9 windows...
			 MenuItem focusItem  = getFocusItem(windows, i);
			 windowMenu.add(focusItem);
		 }
		windowMenu.addSeparator();
		windowMenu.add(newItem);
		windowMenu.add(closeItem);
	 }
	 
	 /**
	 * currenlty, only the concrete subclass has a reference to the application, and hence can create new frames. 
	 * perhaps this will change if I add an interface for the App classes.
	 */
	 public ViewFrame createNewFrame() {
	 	return getApp().openNew();
	 }
	 
	 public abstract TreeViewApp getApp();
	 /**
	 *  Constructs a MenuItem which causes the i'th window to be moved to the front.
	 *
	 * @param  windows  a list of windows
	 * @param  i  which window to move to the front.
	 * @return    a menuItem which focuses the i'th window, or null if more than 9 windows.
	 */
	 private MenuItem getFocusItem(Vector windows, int i) {
		 int p1                  = i + 1;
		 if (p1 > 9) {
			 return null;
		 }
		 final ViewFrame source  = (ViewFrame) windows.elementAt(i);
		 String name;
		 if (source.getLoaded()) {
			 name = source.getDataModel().getName();
		 } else {
			 name = "Not Loaded";
		 }
		 MenuItem focusItem      = new MenuItem(name, new MenuShortcut(getKey(p1)));
		 focusItem.addActionListener(
		 new ActionListener() {
			 public void actionPerformed(ActionEvent e) {
				 source.toFront();
			 }
		 });
		 return focusItem;
	 }
	 
	 
	 /**
	 *  Gets the key corresponding to a particular number.
	 *
	 * @param  i  The number
	 * @return    The VK_blah key value
	 */
	 protected int getKey(int i) {
		 switch (i) {
			case 0:
				return KeyEvent.VK_0;
		    case 1:
				return KeyEvent.VK_1;
			case 2:
				return KeyEvent.VK_2;
			case 3:
				return KeyEvent.VK_3;
			case 4:
				return KeyEvent.VK_4;
			case 5:
				return KeyEvent.VK_5;
			case 6:
				return KeyEvent.VK_6;
			case 7:
				return KeyEvent.VK_7;
			case 8:
				return KeyEvent.VK_8;
			case 9:
				return KeyEvent.VK_9;
		 }
		 return 0;
	 }
	 
	 protected Menu windowMenu;

	  public void showSubDataModel(int[] indexes, String source, String name) {
		  	if (indexes.length == 0) {
		  		JOptionPane.showMessageDialog(this, "No Genes to show summary of!");
		  		return;
		  	}
		  	ReorderedDataModel dataModel = new ReorderedDataModel(getDataModel(), indexes);
		  	if (source != null) dataModel.setSource(source);
		  	if (name != null) dataModel.setName(name);
		  	ViewFrame window = getApp().openNew();
		  	window.setDataModel(dataModel);
		  	window.setLoaded(true);
		  	window.setVisible(true);
		  }	 
	 
	 /**  The global most recently used object. */
	 public FileMru fileMru;
	 /**  allows opening of urls in external browser */
	 protected BrowserControl browserControl = null;
	 /**  url extractor for genes */
	 private UrlExtractor urlExtractor;
	 /**  url extractor for arrays */
	 private UrlExtractor arrayUrlExtractor;


}

