/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003-2007 Laurie Heyer
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
 *   Dept. of Mathematics
 *   PO Box 6959
 *   Davidson, NC 28035
 *   UNITED STATES
 */


package magictool.image;

import magictool.image.FlagList;
import magictool.image.GridMatchException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * FlagManager is the equivalent of GridManager for flags.
 * It is aware of how many grids there are and the number of spots in each grid.
 * It is also able to read and write flag files (*.flag).
 * @author Laurie Heyer
 *
 */
public class FlagManager {

	/* VARIABLES */ 
	/**number of grids
	 * note that this is also stored in the grid manager, but we'll keep it locally for easier access
	 */
	protected int numGrids;
	/**number of spots in each grid
	 * again, also stored in GridManager
	 */
	@Deprecated
	protected int numSpotsPerGrid;
	/**the grid manager, which contains all the grids*/
	protected GridManager manager;
	/**array of flag lists, one FlagList for each grid*/ 
	protected FlagList[] flags;
	
	/**
	 * Constructs an empty flag manager with no grids and no spots.
	 * The manager and flags are set to <b><FONT color=#009900>null</font></b>.
	 *
	 */
	public FlagManager(){
		numGrids = 0;
		numSpotsPerGrid = 0;
		manager = null;
		flags = null;
	}
	
	/**
	 * Constructs a FlagManager with the specifications of the given GridManager
	 * @param gm GridManager on which to base the FlagManager
	 */
	public FlagManager(GridManager gm){
		manager = gm;
		numGrids = manager.getNumGrids();
		if (numGrids > 0) {
			numSpotsPerGrid = (manager.getTotalSpots())/numGrids;
			flags = new FlagList[numGrids];
			for (int i = 0; i < numGrids; i++)
			{
				flags[i] = new FlagList(manager.getGrid(i).getNumOfSpots()); 
			}
		}
	}
	
	/**
	 * Sets the flag on the spot to be true, meaning that the spot is flagged not to be used.
	 * @param grid grid number
	 * @param spot spot number
	 */
	public void flagSpot(int grid, int spot) {
		if (isValid()) flags[grid].setStatus(spot, true);
	}
	/**
	 * Sets the flag on the spot to be false, meaning that the spot is to be used.
	 * @param grid grid number
	 * @param spot spot number
	 */	
	public void unflagSpot(int grid, int spot) {
		if (isValid()) flags[grid].setStatus(spot, false);
	}
	
	public void toggleFlag(int grid, int spot) {
		if (checkFlag(grid,spot)) unflagSpot(grid,spot);
		else flagSpot(grid,spot);
	}
	
	/**
	 * Checks the flag on the given spot
	 * @param grid grid number
	 * @param spot spot number
	 * @return true if the spot is flagged to not be used, false otherwise
	 */
	public boolean checkFlag(int grid, int spot) {
		if (isValid()) return flags[grid].getStatus(spot);
		else return false;
	}
	
	/**
	 * internal method to verify validity of FlagManager to avoid dereferencing null
	 * @return true if this FlagManager is valid, false if it is not
	 */
	private boolean isValid() {
		if ((manager == null) || (numGrids == 0) || (flags == null)) return false;
		else return true;
	}
	
	/**
	 * Sets the GridManager on which this FlagManager is based to gm
	 * <b>WARNING!</b> This will erase all data currently in the FlagManager's FlagLists.
	 * @param gm GridManager on which to base this FlagManager
	 */
	public void setGridManager(GridManager gm) {
		manager = gm;
		numGrids = manager.getNumGrids();
		numSpotsPerGrid = (manager.getTotalSpots())/numGrids;
		flags = new FlagList[numGrids];
		for (int i = 0; i < numGrids; i++)
		{
			flags[i] = new FlagList(manager.getGrid(i).getNumOfSpots()); 
		}
	}
	
	public void checkForGridManagerChanges() {
		boolean changes = false;
		int currGridNum = 0;
		do {
			if (flags != null) changes |= (!(manager.getGrid(currGridNum).getNumOfSpots() == flags[currGridNum].getSize()));	//that's an or-equals operator (see http://www.unix.org.ua/orelly/java-ent/jnut/ch02_05.htm)
			else changes = true;
			currGridNum++;
		}while((!changes) && currGridNum < manager.getNumGrids());
		if (changes) {
			//changes have occurred
			numGrids = manager.getNumGrids();
			numSpotsPerGrid = (manager.getTotalSpots())/numGrids;
			flags = new FlagList[numGrids];
			for (int i = 0; i < numGrids; i++)
			{
				flags[i] = new FlagList(manager.getGrid(i).getNumOfSpots()); 
			}
		}
	}
	
	/**
	 * Gets the GridManager on which this FlagManager is based.
	 * This is most likely useful for comparing to see if this FlagManager is based on the GridManager you think it's based on.
	 * @return the GridManager on which this FlagManager is based
	 */
	public GridManager getGridManager() {
		return manager;
	}
	
	/**
	 * Gets the array of FlagLists that the FlagManager manages
	 * @return an array of size numGrids
	 */
	public FlagList[] getFlagListArray() {
		return flags;
	}
	
	/**
	 * Gets the FlagList for the specified grid
	 * @param grid the specified grid
	 * @return the FlagList for the specified grid
	 */
	public FlagList getFlagList(int grid) {
		if ((isValid()) && (grid<numGrids)) return flags[grid];
		else return null;
	}
	
	/**
	 * gets the number of spots in each grid
	 * Deprecated. DO NOT USE.
	 * @return the number of spots in each grid
	 */
	@Deprecated
	public int getNumSpotsPerGrid() {
		return numSpotsPerGrid;
	}
	
	/**
	 * clears all flags
	 */
	public void clearAllFlags() {
		for (int i = 0; i < numGrids; i++) {
			flags[i].clearAllFlags();
		}
	}
	
	/**
	 * Reads the specified .flag file into the current FlagManager, overwriting any data that may be in this FlagManager.
	 * The sizes of the grids must match for the .flag file to be opened.
	 * If you're using a different GridManager, specify it with setGridManager(GridManager gm) first.
	 * @param filepath File object containing the .flag file to open
	 * @throws Exception generic exception
	 * @throws IOException a problem with the file
	 * @throws GridMatchException the grid parameters in the file do not match those of the current manager; change the manager first
	 */
	public void openFlagManager(File filepath) throws Exception, IOException, GridMatchException {
		openFlagManager(filepath.getAbsolutePath());
	}
	
	/**
	 * Reads the specified .flag file into the current FlagManager, overwriting any data that may be in this FlagManager.
	 * The sizes of the grids must match for the .flag file to be opened.
	 * If you're using a different GridManager, specify it with setGridManager(GridManager gm) first.
	 * @param filename filename of the .flag file to open
	 * @throws Exception generic exception
	 * @throws IOException a problem with the file
	 * @throws GridMatchException the grid parameters in the file do not match those of the current manager; change the manager first
	 */
	public void openFlagManager(String filename) throws Exception, IOException, GridMatchException {
		FlagManager fm = new FlagManager(manager);
		System.out.println("New FlagManager fm created!");
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			System.out.println("New BufferedReader in created!");
			String line;
			String prj = in.readLine();
			System.out.println("Read first line!");
			if(prj.toLowerCase().indexOf("flagmanager file")==-1) throw new Exception();
			else {	//so far it looks valid
				int fileNumGrids = Integer.parseInt(in.readLine());
				//System.out.println("We have " + fileNumGrids + " grids!");
				if (fileNumGrids != fm.numGrids) throw new GridMatchException();
				System.out.println("Number of grids match!");
				line = in.readLine();
				StringTokenizer stgs = new StringTokenizer(line, " ");
				int currGridNum = 0;
				while(stgs.hasMoreTokens()) {
					int currToken = Integer.parseInt(stgs.nextToken());
					//System.out.println("Grid " + currGridNum + " has " + currToken + " spots in the file versus " + manager.getGrid(currGridNum).getNumOfSpots() + " in the manager.");
					if (currToken != manager.getGrid(currGridNum).getNumOfSpots()) throw new GridMatchException();
					currGridNum++;
				}
				//int fileNumSpotsPerGrid = Integer.parseInt(in.readLine());
				System.out.println("Grids dimensions match!");
				
				in.readLine(); 	//"Begin Flags" line
				
				for(int i = 0; (i<fm.numGrids && (line=in.readLine())!=null && line.toLowerCase().indexOf("end flagfile")==-1); i++) {
					//while we're still in the body of the file
					StringTokenizer st = new StringTokenizer(line," ");
					//System.out.println("Line " + i + " StringTokenized!");
					for (int k = 0; ((k < manager.getGrid(i).getNumOfSpots()) && (st.hasMoreTokens())); k++) {
						int currToken = Integer.parseInt(st.nextToken());
						if (currToken != 0) fm.flagSpot(i, k);
						//System.out.println("Spot [" + i + "," + k + "] set!");
						//else we need do nothing, as the spot is already marked false
					}
				}
			}
			in.close();
			for (int i = 0; i < fm.numGrids; i++) {
				flags[i] = fm.flags[i];
			}
			System.out.println("Apparent success!");
		}
		catch (GridMatchException e)
		{
			System.out.println("Grid match exception...");
			throw e;
		}
		catch(IOException e) {
			System.out.println("Problem reading file...");
			throw e;
		}
		catch(Exception e) {
			throw e;
		}
	}
	
	/**
	 * Writes the current FlagManager to the given File
	 * @param filepath File to which to write
	 * @throws IOException there was a problem with the writing
	 */
	public void writeFlagManager(File filepath) throws IOException {
		this.writeFlagManager(filepath.getAbsolutePath());
	}
	/**
	 * Writes the current FlagManager to a file at the given path
	 * @param filepath path of the file to which to write
	 * @throws IOException there was a problem with the writing
	 */	
	public void writeFlagManager(String filepath) throws IOException {
		try {
			if (!filepath.endsWith(".flag")) filepath += ".flag";
			File f = new File(filepath);
			File parent = f.getParentFile();
			
			if (!parent.exists()) parent.mkdirs();
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
			out.println("MAGIC Tool FlagManager File BEGIN (first two lines are grid data)");
			out.println(numGrids);
			//out.println(numSpotsPerGrid);
			for (int i = 0; i < manager.getNumGrids(); i++) {
				out.print(String.valueOf(manager.getGrid(i).getNumOfSpots()) + " ");
			}
			out.print("\n");
			out.println("Begin Flags");
			for (int i = 0; i < numGrids; i++) {
				for (int j = 0; j < flags[i].getSize()-1; j++) {
					if (checkFlag(i,j)) out.print("1 ");
					else out.print("0 ");
				}
				//boundary case - last element
				if (checkFlag(i,flags[i].getSize()-1)) out.print("1");
				else out.print("0");
				out.print("\n");
			}
			out.println("END FlagFile");
			out.close();
		}
		catch(IOException e) { throw e; }
	}
}
