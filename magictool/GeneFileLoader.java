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

import javax.swing.filechooser.FileFilter;

import magictool.filefilters.ClustFilter;
import magictool.filefilters.DisFilter;
import magictool.filefilters.ExpFilter;
import magictool.filefilters.GifFilter;
import magictool.filefilters.GprjFilter;
import magictool.filefilters.GridFilter;
import magictool.filefilters.GrpFilter;
import magictool.filefilters.InfoFilter;
import magictool.filefilters.JpegFilter;
import magictool.filefilters.NoEditFileChooser;
import magictool.filefilters.TifFilter;
import magictool.filefilters.TxtFilter;
import magictool.filefilters.FlagFilter;

/**
 *GeneFileLoader creates a file choose menu for file types used in the application
 */
public class GeneFileLoader extends NoEditFileChooser {

    /**expression file filter*/
    public FileFilter expFilter = new ExpFilter();
    /**cluster file filter*/
    public FileFilter clustFilter = new ClustFilter();
    /**group file filter*/
    public FileFilter grpFilter = new GrpFilter();
    /**dissimilarity file filter*/
    public FileFilter disFilter = new DisFilter();
    /**gene project file filter*/
    public FileFilter gprjFilter = new GprjFilter();
    /**gif file filter*/
    public FileFilter gifFilter = new GifFilter();
    /**jpeg file filter*/
    public FileFilter jpegFilter = new JpegFilter();
    /**text file filter*/
    public FileFilter txtFilter = new TxtFilter();
    /**tif file filter*/
    public FileFilter tifFilter = new TifFilter();
    /**grid file filter*/
    public FileFilter gridFilter = new GridFilter();
    /**info file filter*/
    public FileFilter infoFilter =  new InfoFilter();
    /**flag file filter*/
    public FileFilter flagFilter = new FlagFilter();

  /**
   * Constructs the file chooser
   */
  public GeneFileLoader() {
    super();
    this.setApproveButtonToolTipText(null);
    this.setToolTipText(null);
    this.addChoosableFileFilter(expFilter);
    this.addChoosableFileFilter(grpFilter);
    this.addChoosableFileFilter(disFilter);
    this.addChoosableFileFilter(clustFilter);
    this.addChoosableFileFilter(gprjFilter);
    this.addChoosableFileFilter(txtFilter);
    this.addChoosableFileFilter(tifFilter);
    this.addChoosableFileFilter(gifFilter);
    this.addChoosableFileFilter(jpegFilter);
    this.addChoosableFileFilter(gridFilter);
    this.addChoosableFileFilter(infoFilter);
    this.addChoosableFileFilter(flagFilter);

  }
}