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

package magictool.filefilters;

import java.awt.Component;
import java.awt.Container;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.filechooser.FileSystemView;

/**
 * NoEditFileChooser creates a JFileChooser where renaming files and directories is disabled
 */
public class NoEditFileChooser extends JFileChooser {

  /**
   * Constructs the file chooser
   */
  public NoEditFileChooser() {
    super();
    removeEdit();
  }

  /**
   * Constructs the file chooser
   * @param fsv FileSystemView for the file chooser
   */
  public NoEditFileChooser(FileSystemView fsv) {
    super(fsv);
    removeEdit();
  }

  /**
   * Constructs the file chooser
   * @param f File for the file chooser
   */
  public NoEditFileChooser(File f) {
    super(f);
    removeEdit();
  }

    //removes the ability to rename files and directories
    private void removeEdit(){
      removeEdit(this);
    }

    //removes the ability to rename files and directories
    private void removeEdit(Component c) {
      if (c instanceof JList){
           java.util.EventListener[] listeners=c.getListeners(java.awt.event.MouseListener.class);
           for(int i=0; i<listeners.length; i++) {
           //System.err.println("listeners[i]="+listeners[i]);
               if (listeners[i].toString().indexOf("SingleClickListener") != -1) {
                   c.removeMouseListener((java.awt.event.MouseListener)listeners[i]);
               }
           }
       }
       Component[] children = null;
       if (c instanceof Container) {
           children = ((Container)c).getComponents();
       }
       if (children != null) {
           for(int i = 0; i < children.length; i++) {
               removeEdit(children[i]);
           }
       }
   }



}

