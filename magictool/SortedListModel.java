/*
 *   MAGIC Tool, A microarray image and data analysis program
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
 *   Dept. of Mathematics
 *   PO Box 6959
 */

package magictool;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import javax.swing.DefaultListModel;
import javax.swing.AbstractListModel;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 * SortedListModel automatically sorts the items put into it in ascending order. It is almost identical to a DefaultListModel in all other ways.
 * @author Laurie Heyer
 */
public class SortedListModel extends DefaultListModel {
	
	public SortedListModel() { super(); }
	
	public SortedListModel(Object[] objs, Comparator c) {
		Arrays.sort(objs, c);
		for (int i = 0; i < objs.length; i++) super.addElement(objs[i]);
	}
	
	public void addElement(Comparable obj) {
		//System.out.println("Entering SortedListModel.addElement(Comparable obj)!");
		if (this.size() == 0) super.addElement(obj);
		else {
			int i = 0;
			while ((i < this.getSize()) && (obj.compareTo(this.get(i))>0)) i++;
			super.add(i, obj);
		}
	}
	
}
