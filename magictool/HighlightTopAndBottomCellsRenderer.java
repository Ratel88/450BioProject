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
 *   Dept. of Mathematics
 *   Davidson College
 *   PO Box 6959
 *   Davidson, NC 28035-6959
 *   UNITED STATES
 */

package magictool;

import java.awt.Color;
import java.awt.Component;
import javax.swing.table.*;
import javax.swing.JTable;

public class HighlightTopAndBottomCellsRenderer extends DefaultTableCellRenderer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 42L;
	private static final Color DARK_GREEN = new Color(0f, 0.39125f, 0f);
	private static final Color FOREST_GREEN = new Color(0.13333f, 0.54509f, 0.13333f); //RGB 34 139 34

	private int[] cellsToMarkBlue = null;
	private int[] cellsToMarkOrange = null;
	private boolean blueOrange = false;

	/**
	 * Constructor giving only top cells to highlight
	 * @param n the array of row numbers of the cells to highlight (blue/red)
	 */
	public HighlightTopAndBottomCellsRenderer(int [] n) {
		this.cellsToMarkBlue = n;
	}
	
	/**
	 * Constructor giving both top and bottom cells to highlight
	 * @param top the array of cells to highlight their rows with the first color (blue/red)
	 * @param bottom the array of cells to highlight with the second color (orange/green)
	 */
	public HighlightTopAndBottomCellsRenderer(int [] top, int [] bottom) {
		this.cellsToMarkBlue = top;
		this.cellsToMarkOrange = bottom;
	}
	
	/**
	 * Constructor giving both top and bottom cells to highlight, as well as a color scheme definition
	 * @param top the array of cells to highlight their rows with the first color (blue/red)
	 * @param bottom the array of cells to highlight their rows with the second color (orange/green)
	 * @param bO whether or not to use the blue/orange color scheme
	 */
	public HighlightTopAndBottomCellsRenderer(int [] top, int [] bottom, boolean bO) {
		this.cellsToMarkBlue = top;
		this.cellsToMarkOrange = bottom;
		this.blueOrange = bO;
	}

	/*public void setValue(Object v) {
		  super.setValue(v);
		  if (v == null) {
			  setForeground(Color.white);
			  setBackground(Color.black);
			  return;
		  }

	  }*/

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		setText(String.valueOf(value));
		boolean markItBlue = false;
		boolean markItOrange = false;
		if (cellsToMarkBlue != null){
			for(int i : cellsToMarkBlue) {	//for each integer i in cellsToMarkBlue
				if (i == row) {
					markItBlue = true;
					//System.out.println("Attempting to mark row " + row + " blue");
					break;
				}
			}
		}
		if (cellsToMarkOrange != null) {
			for(int i : cellsToMarkOrange) {
				if (i == row) {
					markItOrange = true;
					//System.out.println("Attempting to mark row " + row + " orange");
				}
			}
		}
		
		if(markItBlue){
			if (blueOrange) {
				this.setBackground(Color.BLUE);
				this.setForeground(Color.WHITE);
			}
			else {
				this.setBackground(Color.RED);
				this.setForeground(Color.WHITE);
			}
		}
		else if (markItOrange){
			if (blueOrange) {
				this.setBackground(Color.ORANGE);
				this.setForeground(Color.WHITE);
			}
			else {
				this.setBackground(FOREST_GREEN);
				this.setForeground(Color.WHITE);
			}
		}
		else {
			this.setBackground(Color.WHITE);
			this.setForeground(Color.BLACK);
		}
		return this;
	}

}