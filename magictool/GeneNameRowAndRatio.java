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

import java.util.*;

public class GeneNameRowAndRatio implements Comparable<GeneNameRowAndRatio> {
	  protected String name;
	  protected int row;
	  protected double ratio;
	  public static final Comparator<GeneNameRowAndRatio> GENENAMEROWANDRATIO_COMPARE = new GeneNameRowAndRatioComparator();
	  
	  public GeneNameRowAndRatio(String n, int r, double rat) {
		  this.name = n;
		  this.row = r;
		  this.ratio = rat;
	  }
	  
	  public int compareTo(GeneNameRowAndRatio b) {
		  if (this.ratio > b.ratio) return 1;
		  else return -1;
	  }
	  
	  public String toString() {
		  return "Name: " + name + "\nRow: " + row + "\nRatio: " + ratio;
	  }
}
class GeneNameRowAndRatioComparator implements Comparator<GeneNameRowAndRatio> {
	  public int compare(GeneNameRowAndRatio a, GeneNameRowAndRatio b) {
		  return a.compareTo(b);
	  }
}
