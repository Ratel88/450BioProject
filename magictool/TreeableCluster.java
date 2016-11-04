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

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * TreeableCluster is an interface to return a DefaultMutableTreeNode which is the top node of
 * a single cluster of all the elements.
 */
public interface TreeableCluster {

  /**
   * returns the top node of a single cluster which contains all the elements
   * @param clustFile cluster file to get the data from
   * @return top node of a single cluster which contains all the elements
   */
  public DefaultMutableTreeNode getDataInTree(File clustFile);
}