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

import java.io.File;

/**
 * JpegFilter is a file filter used to select jpeg files in a file chooser
 */
public class JpegFilter extends javax.swing.filechooser.FileFilter {

  /**Constucts the jpeg file filter*/
  public JpegFilter(){}

  /**
   * returns whether or not file contains the jpeg file extension (.jpeg)
   * @param fileobj file to test
   * @return whether or not file contains the jpeg file extension (.jpeg)
   */
  public boolean accept(File fileobj) {
    String extension = "";

    if(fileobj.getPath().lastIndexOf('.') > 0)
    extension = fileobj.getPath().substring(fileobj.getPath().lastIndexOf('.') + 1).toLowerCase();

    if(extension != "")
      return extension.equals("jpeg")||extension.equals("jpg");
    else
      return fileobj.isDirectory();
  }

  /**
   * returns string description of file format
   * @return string description of file format
   */
  public String getDescription() {
    return "Jpeg Files (*.jpeg)";
  }
}