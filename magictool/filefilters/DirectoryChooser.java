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

import javax.swing.JFileChooser;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;

/**
 * DirectoryChooser is a file chooser which allows a user to only select
 * a directory and not any files
 */
public class DirectoryChooser extends NoEditFileChooser {

  /**
   * Constructs the chooser
   * @param startDir beginning directory location
   */
  public DirectoryChooser(File startDir) {
    super(startDir);
    this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
  }

  /**
   * returns the selected file - in this case the file is a directory
   * @return selected file - in this case the file is a directory
   */
  public File getSelectedFile() {
    File file = super.getSelectedFile();
    if (file == null) {
      return file;
    }
    String dirName = super.getSelectedFile().getAbsolutePath();
    String fileSep = System.getProperty("file.separator", "\\");
    if (dirName.endsWith(fileSep + ".")) {
      dirName = dirName.substring(0, dirName.length() - 2);
      file = new File(dirName);
    }

    return file;
  }

  /**
   * sets the current directory to a file
   * @param file file to set current directory to
   */
  public void setCurrentDirectory(File file) {
    super.setCurrentDirectory(file);
    FileChooserUI ui = getUI();
    if (ui instanceof BasicFileChooserUI) {
      ((BasicFileChooserUI)ui).setFileName(".");
    }
  }

}