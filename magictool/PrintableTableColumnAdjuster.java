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

import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.lang.IllegalStateException;

/**
 * This class automatically sets the columns to be of equal width and fill the JScrollPane when the table to which it is attached is resized.
 * Its adjustColumns method can be called whenever the columns should be manually reset to be of equal width and fill the JScrollPane.
 * <br>Modified from <a href="http://forums.java.net/jive/message.jspa?messageID=129332">http://forums.java.net/jive/message.jspa?messageID=129332</a>
 * @author Laurie Heyer
 *
 */
public class PrintableTableColumnAdjuster implements ComponentListener{
	private PrintableTable table;
	int oldWidth = -1;
	int oldHeight = -1;
	
	public PrintableTableColumnAdjuster(PrintableTable table) {
		if (table.getParent() == null) {
			throw new IllegalStateException("add table to JScrollPane before constructing TableColumnAdjuster");
		}
		table.getParent().getParent().addComponentListener(this);
		this.table = table;
		table.setAutoResizeMode(PrintableTable.AUTO_RESIZE_OFF);
		adjustColumns();
	}
	
	/**
	 * implements the componentResized event of the ComponentListener
	 * @param e ComponentEvent that caused this method to be fired
	 */
	public void componentResized(ComponentEvent e) {
		if (oldWidth == table.getParent().getWidth() && oldHeight == table.getParent().getHeight()) return;
		adjustColumns();
		oldWidth = table.getParent().getWidth();
		oldHeight = table.getParent().getHeight();
	}
	public void componentHidden(ComponentEvent e){}
	public void componentMoved(ComponentEvent e){}
	public void componentShown(ComponentEvent e){}
	
	/**
	 * Adjusts the columns of the table to which this PrintableColumnListener is attached to be of equal width and fill the screen.
	 */
	public void adjustColumns() {
		int averageWidth = table.getParent().getWidth() / table.getColumnCount();
		for(int c = 0; c < table.getColumnCount(); c++) {
			table.getColumnModel().getColumn(c).setPreferredWidth(averageWidth);
			table.getColumnModel().getColumn(c).setWidth(averageWidth);
		}
	}

}
