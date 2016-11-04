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

public class GridMatchException extends Exception {
	
	/**
	 * SerialVersionUID because Eclipse complains
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructs generic GridMatchException
	 *
	 */
	public GridMatchException() {
		super();
	}
	
	/**
	 * Constructs GridMatchException with specific information
	 * @param s string containing specific information
	 */
	public GridMatchException(String s) {
		super(s);
	}

}
