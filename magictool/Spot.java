/*   MAGIC Tool, A microarray image and data analysis program
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
 *   Davidson College
 *   Dept. of Mathematics
 *   PO Box 6959
 *   Davidson, NC 28035-5959
 *   USA
 */

package magictool;

import java.util.*;


/**
 * The Spot class stores the name, grid number, and transformed spot number of each spot in the grid.
 * It is used <b>before</b> segmentation.
 * It is the primary data type for the list in FlagEditFrame.
 * @author Laurie Heyer
 *
 */
public class Spot implements Comparable {
	/** name of the gene in this spot */
	String name;
	/** the grid number in which this spot lives */
	int grid;
	/** this spot's transformed spot number in the grid */
	int TSN;
	/**Comparator for this object*/
	public static final Comparator SPOT_COMPARE = new SpotComparator();
	
	/**
	 * Default constructor.
	 * name is initialized to null, grid and TSN to -1
	 *
	 */
	public Spot() {
		name = null;
		grid = -1;
		TSN = -1;
	}
	
	/**
	 * Constructor taking the name of the spot as an argument
	 * @param n name of gene at the spot
	 */
	public Spot(String n) {
		name = n;
		grid = -1;
		TSN = -1;
	}
	
	/**
	 * Constructor taking all the information that Spot stores
	 * @param n name of the gene at the spot
	 * @param g grid number of the gene
	 * @param t transformed spot number of the gene
	 */
	public Spot(String n, int g, int t) {
		name = n;
		grid = g;
		TSN = t;
	}
	
	/**
	 * Constructor taking the location information of the Spot
	 * @param g grid number of the gene
	 * @param t transformed spot number of the gene
	 */
	public Spot(int g, int t) {
		name = null;
		grid = g;
		TSN = t;
	}
	
	/**
	 * Returns the name of this spot
	 * @return the name of this spot
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the grid number of this spot
	 * @return the grid number of this spot
	 */
	public int getGrid() {
		return grid;
	}
	
	/**
	 * Returns the transformed spot number of this spot
	 * @return the transformed spot number of this spot
	 */
	public int getTSN() {
		return TSN;
	}
	
	/**
	 * Sets the name of this spot
	 * @param n the name of this spot
	 */
	public void setName(String n) {
		name = n;
	}
	
	/**
	 * Sets the grid number of this spot
	 * @param g the grid number of this spot
	 */
	public void setGrid(int g) {
		grid = g;
	}
	
	/**
	 * Sets the transformed spot number of this spot
	 * @param t the transformed spot number of this spot
	 */
	public void setTSN(int t) {
		this.TSN = t;
	}
	
	/**
	 * Standard comparison method.
	 * Sorts first by name, then by grid, then by transformed spot number.
	 * @param b Spot to compare this spot with
	 * @return -1 if b is greater than this or equal to this (equality should never occur), or 1 if b is less than this 
	 */
	public int compareTo(Object b) {
		Spot be = (Spot)b;
		int string_comparison_result = this.getName().compareToIgnoreCase(be.getName());
		if (string_comparison_result < 0) return -1;
		else if (string_comparison_result > 0) return 1;
		//else...
		//strings are equal. Sort by grid, then by TSN.
		if (this.getGrid() > be.getGrid()) {
			//then b is greater
			return 1;
		}
		else if (this.getGrid() < be.getGrid() ) {
			//then a is greater
			return -1;
		}
		else {
			//a.getGrid() == b.getGrid()
			//sort by TSN
			if (this.getTSN() > be.getTSN()) return 1;
			else return -1;
		}
	}
	
	/**
	 * Converts this item to a string
	 * @return the name of this spot
	 */
	public String toString() {
		return name;
	}
}

/**
 * Basic Comparator implementation for class Spot.
 * @author Laurie Heyer
 *
 */
class SpotComparator implements Comparator<Spot> {
	
	/**
	 * Compare method for the Comparator
	 * @param a first spot
	 * @param b second spot
	 * @return -1 if b is greater than a or equal to a (equality should never occur), or 1 if b is less than a
	 */
	public int compare(Spot a, Spot b) {
		return a.compareTo(b);
	}
}