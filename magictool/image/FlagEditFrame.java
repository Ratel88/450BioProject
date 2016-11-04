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

import java.util.Vector;
import java.text.Collator;
import java.util.Comparator;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Point;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import magictool.DidNotFinishException;
import magictool.ExpFile;
import magictool.Project;
import magictool.VerticalLayout;
import magictool.SortedListModel;
import magictool.Spot;

/**
 * The FlagEditFrame is a class that enables the user to add and remove genes from the flag list by gene name.
 * @author Laurie Heyer
 *
 */
public class FlagEditFrame extends JInternalFrame {
	private JPanel jPanel1 = new JPanel();
	private JScrollPane notFlaggedScroll;
	private JPanel jPanel2 = new JPanel();
	private JButton addButton = new JButton();
	private JButton removeButton = new JButton();
	private JButton doneButton = new JButton();
	private VerticalLayout verticalLayout1 = new VerticalLayout();
	private JScrollPane flaggedScroll;
	private GridLayout gridLayout1 = new GridLayout();
	private JPanel jPanel3 = new JPanel();
	private JLabel jLabel1 = new JLabel();
	private JLabel jLabel2 = new JLabel();
	private JLabel jLabel3 = new JLabel();
	private GridLayout gridLayout2 = new GridLayout();
	
	/**list of genes that are flagged*/
	protected JList flaggedGenes = new JList();
	/**list of genes that aren't flagged*/
	protected JList notFlaggedGenes = new JList();
	/**model holds the list of genes that are flagged*/
	protected SortedListModel flaggedModel;
	/**model holds the list of genes that are not flagged*/
	protected SortedListModel notFlaggedModel;
	/**grid manager associated with this flag editing frame*/
	protected GridManager manager;
	/**flag manager associated with this flag editing frame*/
	protected FlagManager flagman;
	/**project associated with this flag editing frame*/
	protected Project p;
	/**parent frame*/
	protected FlagFrame parent = null;
	/**parent frame*/
	protected Frame parentFrame;
	
	/**
	 * Add and remove genes from the flag list by gene name
	 * @param prj project associated with this flag editing frame
	 * @param gman grid manager associated with this flag editing frame
	 * @param fman flag manager associated with this flag editing frame
	 * @param pFrame parent frame
	 */
	public FlagEditFrame(Project prj, GridManager gman, FlagManager fman, Frame pFrame) {
		//first, set all local variables to the arguments
		this.p = prj;
		this.manager = gman;
		this.flagman = fman;
		this.parentFrame = pFrame;
		
		//flaggedModel = new SortedListModel();
		//notFlaggedModel = new SortedListModel();
		
		/*
		 * Now we need to build a list of Points that are flagged and a list of Points that are unflagged
		 * The specifications for this will be the following:
		 * 		* Point gene(grid, transformed_spot_num)
		*/
		/*Vector<Point> flaggedSpotsVector = new Vector<Point>();
		Vector<Point> unflaggedSpotsVector = new Vector<Point>();
		
		for (int i = 0; i < flagman.numGrids; i++) {
			for (int j = 0; j < flagman.numSpotsPerGrid; j++) {
				Point currPt = new Point(i,j);
				if (flagman.checkFlag(i, j)) flaggedSpotsVector.add(currPt);
				else unflaggedSpotsVector.add(currPt);
			}
		}*/	//not used after the switch to Spot objects
		
		Spot[] spots = manager.getSpots();	//get the Spot objects from the manager
		Vector<Spot> flaggedVector = new Vector<Spot>();	//create the vectors to store the ones that are flagged and not flagged
		Vector<Spot> notFlaggedVector = new Vector<Spot>();
		
		for (int i = 0; i < manager.getTotalSpots(); i++) {
			Spot currSpot = spots[i];
			if (currSpot == null) {
				System.out.println("spots[" + i + "] is null.");	//this should never happen, we'll be told if it does.
			}
			if (flagman == null) {
				System.out.println("flagman is null.");
			}
			/** this tells us if the current spot is flagged */
			boolean currSpotIsFlagged = flagman.checkFlag(currSpot.getGrid(), currSpot.getTSN());
			if (currSpotIsFlagged) {
				flaggedVector.add(currSpot);
			}
			else {
				notFlaggedVector.add(currSpot);
			}
		}//end for

		//the next four lines convert each vector of Spots to an array of Spots to be passed in to create the SortedListModels.
		Spot[] flaggedArray = new Spot[flaggedVector.size()];
		flaggedVector.toArray(flaggedArray);
		Spot[] notFlaggedArray = new Spot[notFlaggedVector.size()];
		notFlaggedVector.toArray(notFlaggedArray);
		
		if (flaggedVector.size() > 0) flaggedModel = new SortedListModel(flaggedArray, Spot.SPOT_COMPARE);
		else flaggedModel = new SortedListModel();
		if (notFlaggedVector.size() > 0) notFlaggedModel = new SortedListModel(notFlaggedArray, Spot.SPOT_COMPARE);
		else notFlaggedModel = new SortedListModel();
		//System.out.println(flaggedModel.getSize() + " elements in flaggedModel.");
		//System.out.println(notFlaggedModel.getSize() + " elements in notFlaggedModel.");
		
		
		/*
		 * Now we construct the flaggedModel and notFlaggedModel using gene names retrieved from the manager
		 */
		
		/*if (flaggedSpotsVector.size() > 0) {
			String[] flaggedSpotsNamesArray = new String[flaggedSpotsVector.size()];
			for (int i = 0; i < flaggedSpotsVector.size(); i++) {
				//System.out.print("Attempting to get element " + i + " of flaggedSpotsVector...");
				try{
					//System.out.println(flaggedSpotsVector.size() + " i:" + i);
					//System.out.println(flaggedSpotsVector.get(i).toString());
					Point currPt = flaggedSpotsVector.get(i);
					//System.out.println("Success!");
					int grid = currPt.x;
					int transspot = currPt.y;
					int actspot = manager.getActualSpotNum(grid, transspot);
					String name = manager.getGeneName(grid, actspot);
					flaggedSpotsNamesArray[i] = name;
					//flaggedModel.addElement(name);
				}
				catch (Exception e) {
					System.out.println("Getting element " + i + " of flaggedSpotsVector failed!");
					System.out.println("Stack Trace:");
					e.printStackTrace();
				}
			}
			flaggedModel = new SortedListModel(flaggedSpotsNamesArray, String.CASE_INSENSITIVE_ORDER);
		}
		else {
			flaggedModel = new SortedListModel();
		}
		
		if (unflaggedSpotsVector.size() > 0) {
			String[] unflaggedSpotsNamesArray = new String[unflaggedSpotsVector.size()];
			for (int i = 0; i < unflaggedSpotsVector.size(); i++) {
				//System.out.print("Attempting to get element " + i + " of unflaggedSpotsVector...");
				try {
					//System.out.println(unflaggedSpotsVector.size() + " i:" + i);
					//System.out.println(unflaggedSpotsVector.get(i).toString());
					Point currPt = unflaggedSpotsVector.get(i);
					//System.out.println("Success!");
					int grid = currPt.x;
					int transspot = currPt.y;
					int actspot = manager.getActualSpotNum(grid, transspot);
					String name = manager.getGeneName(grid, actspot);
					//notFlaggedModel.addElement(name);
					unflaggedSpotsNamesArray[i] = name;
				}
				catch (Exception e) {
					System.out.println("Getting element " + i + " of unflaggedSpotsVector failed!");
					System.out.println("Stack Trace:");
					e.printStackTrace();
				}
			}
			notFlaggedModel = new SortedListModel(unflaggedSpotsNamesArray, String.CASE_INSENSITIVE_ORDER);
		}
		else {
			notFlaggedModel = new SortedListModel();
		}
		*/
		flaggedGenes.setModel(flaggedModel);
		flaggedGenes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		notFlaggedGenes.setModel(notFlaggedModel);
		notFlaggedGenes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		
		try {
			jbInit();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	private void jbInit() throws Exception {
		jPanel1.setLayout(gridLayout1);
		jPanel2.setLayout(verticalLayout1);
		addButton.setText("Add>>");
		addButton.addActionListener(new java.awt.event.ActionListener() {
			//adds the selected gene to the flag list
			public void actionPerformed(ActionEvent e) {
				addButton_actionPerformed(e);
			}
		});
		removeButton.setText("<<Remove");
		removeButton.addActionListener(new java.awt.event.ActionListener() {
			//removes the selected gene from the flag list
			public void actionPerformed(ActionEvent e) {
				removeButton_actionPerformed(e);
			}
		});
		doneButton.setText("DONE!");
		doneButton.addActionListener(new java.awt.event.ActionListener () {
			//closes the window
			public void actionPerformed(ActionEvent e) {
				doneButton_actionPerformed(e);
			}
		});
		verticalLayout1.setAlignment(VerticalLayout.MIDDLE);
		this.setClosable(true);
		this.setMaximizable(true);
		this.setResizable(true);
		this.setTitle("Flagging by Gene Name");
		
		notFlaggedScroll = new JScrollPane(notFlaggedGenes);
		flaggedScroll = new JScrollPane(flaggedGenes);
		jLabel1.setText("Not Flagged Genes");
		jLabel3.setText("Flagged Genes");
		jPanel3.setLayout(gridLayout2);
		
		this.getContentPane().add(jPanel1, BorderLayout.CENTER);
		jPanel2.add(addButton, null);
		jPanel2.add(removeButton, null);
		jPanel2.add(doneButton, null);
		jPanel1.add(notFlaggedScroll, null);
		jPanel1.add(jPanel2, null);
		jPanel1.add(flaggedScroll, null);
		
		this.getContentPane().add(jPanel3, BorderLayout.NORTH);
		jPanel3.add(jLabel1, null);
		jPanel3.add(jLabel2,null);
		jPanel3.add(jLabel3,null);
	}
	
	/**
	 * sets the parent FlagFrame for no apparent reason
	 * @param f parent FlagFrame
	 */
	public void setParentFrame(FlagFrame f) {
		parent = f;
	}
	
	/**
	 * Method to force the FlagFrame to redraw itself
	 *
	 */
	private void updateParentFrame() {
		parent.refreshImage();
	}
	/*
	 * Adds genes selected in the Not Flagged list to the Flagged list and flags them in the FlagManager
	 * @param e Not used
	 */
	/*private void old_addButton_actionPerformed(ActionEvent e) {
		int pos[] = notFlaggedGenes.getSelectedIndices(); //these are the indices of the selected elements in the Not Flagged list
		String names[] = new String[pos.length];	//these will be the names of the selected genes in the Not Flagged list
		Point locations[] = new Point[pos.length];	//these will be the locations (grid, transformed spot number) of the selected genes in the Not Flagged list 
		for (int i = 0; i < pos.length; i++) {
			names[i] = (String)notFlaggedModel.elementAt(pos[i]-i);
			locations[i] = manager.findGeneLocation(names[i]);	//findGeneLocation returns a point with the grid as its x coordinate and the TRANSFORMED spot num as its y coordinate
			//System.out.println("Flagging Grid " + locations[i].x + ", Spot " +locations[i].y + " which is " + i + ": " + names[i]);
			flagman.flagSpot(locations[i].x, locations[i].y);	//make the flagging happen
			notFlaggedModel.removeElementAt(pos[i]-i);	//this is no longer flagged so make it so
			flaggedModel.addElement(names[i]);	//and it is now flagged
			updateParentFrame(); //redraw the FlagFrame
		}
	}*/
	
	/**
	 * Adds genes selected in the Not Flagged list to the Flagged list and flags them in the FlagManager
	 * @param e Not used
	 */
	private void addButton_actionPerformed(ActionEvent e) {
		int pos[] = notFlaggedGenes.getSelectedIndices();	//these are the indices of the selected elements in the Not Flagged list
		Spot spots[] = new Spot[pos.length];				//these will be the spots on the grid of the selected genes in the Not Flagged list
		for (int i = 0; i < spots.length; i++) {
			spots[i] = (Spot)notFlaggedModel.elementAt(pos[i]-i);	//get the spot
			flagman.flagSpot(spots[i].getGrid(), spots[i].getTSN());	//flag the spot
			notFlaggedModel.removeElementAt(pos[i]-i);	//remove it from the not flagged list
			flaggedModel.addElement(spots[i]);			//add it to the flagged list
			updateParentFrame();	//redraw the FlagFrame
		}
	}
	
	/*
	 * Removes genes selected in the Flagged list from the Flagged list and unflags them in the FlagManager
	 * @param e Not used
	 */
	/*private void old_removeButton_actionPerformed(ActionEvent e) {
		int pos[] = flaggedGenes.getSelectedIndices(); 	//these are the indices of the selected elements in the Flagged list
		String names[] = new String[pos.length]; 		//these will be the names of the selected genes in the Flagged list
		Point locations[] = new Point[pos.length];		//these will be the locations (grid, transformed spot number) of the selected genes in the Flagged list
		for (int i = 0; i < pos.length; i++) {
			names[i] = (String)flaggedModel.elementAt(pos[i]-i);	//the name of each selected gene
			locations[i] = manager.findGeneLocation(names[i]);
			//System.out.println("Unflagging Grid " + locations[i].x + ", Spot " +locations[i].y);
			flagman.unflagSpot(locations[i].x, locations[i].y);	//deflag
			flaggedModel.removeElementAt(pos[i]-i);
			notFlaggedModel.addElement(names[i]);
			updateParentFrame(); //redraw the FlagFrame
		}
	}*/
	
	/**
	 * Removes genes selected in the Flagged list from the Flagged list and unflags them in the FlagManager
	 * @param e Not used
	 */
	private void removeButton_actionPerformed(ActionEvent e) {
		int pos[] = flaggedGenes.getSelectedIndices();	//these are the indices of the selected elements in the Flagged list
		Spot spots[] = new Spot[pos.length];	//these will be the spots on the grid of the selected genes in the Flagged list
		for (int i = 0; i < spots.length; i++) {
			spots[i] = (Spot)flaggedModel.elementAt(pos[i]-i);	//get the spot
			flagman.unflagSpot(spots[i].getGrid(), spots[i].getTSN());	//unflag the spot
			flaggedModel.removeElementAt(pos[i]-i);	//remove it from the flagged list
			notFlaggedModel.addElement(spots[i]);	//add it to the not flagged list
			updateParentFrame();	//redraw FlagFrame
		}
	}
	
	/**
	 * Closes the window
	 * @param e Not used
	 */
	private void doneButton_actionPerformed(ActionEvent e) {
		updateParentFrame();	//redraw the FlagFrame
		this.dispose();	//close
	}
}
