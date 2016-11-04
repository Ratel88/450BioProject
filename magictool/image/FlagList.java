/*   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003  Laurie Heyer
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

package magictool.image;

/**
 * 
 * FlagList keeps track of the flags (as to whether or not to use a spot) for one grid.
 * <p>
 * For convention, <b>true</b> means the element is flagged (i.e. do not use it) and <b>false</b> if the element is not flagged (OK to use it).
 * <p>
 * We will number from <b>0</b>
 * @author Laurie Heyer
 *
 */
public class FlagList {
	/**the actual array of flags*/
	private boolean[] flags;
	/**size of array - note that for ease, this will be set to the number of spots that can be stored in the array, i.e. the size of the array minus 1*/
	private int sizeOfFlags;
	
	/**
	 * Default constructor
	 *
	 */
	FlagList() {
		this(0);
		sizeOfFlags = 0;
		flags = new boolean[1];
		flags[0] = false; 
	}
	
	/**
	 * Constructor with size parameter
	 * @param size The number of spots we have.
	 */
	FlagList(int size) {
		sizeOfFlags = size;
		flags = new boolean[sizeOfFlags];
		//initialize to false
		for (int i = 0; i < sizeOfFlags; i++)
		{
			flags[i] = false;
		}
	}
	
	/**
	 * Sets the size of the flag list
	 * @param newSize New number of spots we have
	 */
	public void setSize(int newSize) {
		boolean[] temp = new boolean[newSize+1];
		for (int i = 0; i < sizeOfFlags; i++)
		{
			temp[i] = flags[i];
		}
		flags = temp;
		sizeOfFlags = newSize;
	}
	
	/**
	 * Gets the number of spots the flag list is capable of storing data for
	 * @return Number of spots the flag list is capable of storing data for
	 */
	public int getSize() {
		return sizeOfFlags;
	}
	
	/**
	 * gets whether or not a particular spot is flagged
	 * @param spotNum number of spot to test
	 * @return status of spot (true if we should not use it)
	 */
	public boolean getStatus(int spotNum) {
		if ((spotNum >= 0) && (spotNum < sizeOfFlags)) return flags[spotNum];
		else return false; //false if we have a bad spot number
	}
	
	/**
	 * Sets whether or not a particular spot is flagged
	 * @param spotNum number of spot to set status of
	 * @param status status to set the spot to (true if we should not use it, i.e. it is flagged)
	 */
	public void setStatus(int spotNum, boolean status) {
		if ((spotNum >= 0) && (spotNum <= sizeOfFlags)) flags[spotNum] = status;
	}
	
	/**
	 * gets the whole array of flags
	 * @return array of flags
	 */
	public boolean[] getArray() {
		return flags;
	}
	
	public void clearAllFlags() {
		for (int i = 0; i < sizeOfFlags; i++)
		{
			flags[i] = false;
		}
	}

}
