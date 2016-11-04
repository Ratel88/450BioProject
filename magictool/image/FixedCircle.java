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

package magictool.image;


/**
 * AdaptiveCircle takes pixels from a red and green a microarray image and generates ratio data using the fixed circle method.
 * The class implements the DataGenerator interface and outputs an instance of the GeneData class.
 */
public class FixedCircle implements DataGenerator {

  /**red pixels*/
  protected int[] redpixels;
  /**green pixels*/
  protected int[] greenpixels;
  /**rows of pixels*/
  protected int rows;
  /**columns of pixels*/
  protected int cols;
  /**fixed circle radius*/
  protected int radius;

  private double centerx; //center column
  private double centery; //center row

  /**
   * Constructs a FixedCircle which is able to output microarray data for the given
   * red and green image pixels using the fixed circle method with a default radius of 5.
   * @param red red pixels
   * @param green green pixels
   * @param rows number of rows
   * @param cols number of columns
   */
  public FixedCircle(int[] red, int[] green, int rows, int cols){
    this(red,green,rows,cols,5);
  }

  /**
   * Constructs a FixedCircle which is able to output microarray data for the given
   * red and green image pixels using the fixed circle method with a given radius.
   * @param red red pixels
   * @param green green pixels
   * @param rows number of rows
   * @param cols number of columns
   * @param radius fixed circle radius
   */
  public FixedCircle(int[] red, int[] green, int rows, int cols, int radius) {
    redpixels=red;
    greenpixels=green;
    this.rows=rows;
    this.cols=cols;
    this.radius=radius;
    centerx = (cols-1)/2.0;
    centery = (rows-1)/2.0;
  }

  /**
   * sets the radius of the fixed circle
   * @param radius radius of the fixed circle
   */
  public void setRadius(int radius){
    this.radius=radius;
  }

  /**
   * rturns the radius of the fixed circle
   * @return radius of the fixed circle
   */
  public int getRadius(){
    return radius;
  }

  /**
   * returns the data for the from the red and green image pixels to be used
   * to create the ratio data for the particular gene using the fixed circle method
   * @return gene data containing the averages of the foreground and background for both the red and green image
   */
  public GeneData generateData(){
    int bgnum=0, redbgtot=0, redfgtot=0, fgnum=0, greenbgtot=0, greenfgtot=0;
    int row=0, col=0;
    for(int i=0; i<redpixels.length&&i<greenpixels.length; i++){
      if(withinRadius(col,row)){
        redfgtot+=redpixels[i];
        greenfgtot+=greenpixels[i];
        fgnum++;
      }
      else{
        redbgtot+=redpixels[i];
        greenbgtot+=greenpixels[i];
        bgnum++;
      }
      col++;
      if(col>=cols){
        col=0;
        row++;
      }
    }

    return new GeneData(redfgtot, redbgtot, greenfgtot, greenbgtot,fgnum, bgnum, fgnum, bgnum);
  }

  /**
   * returns whether a given pixel is within the radius of the fixed circle
   * @param col column number of the pixel
   * @param row row number of the pixel
   * @return whether a given pixel is within the radius of the fixed circle
   */
  public boolean withinRadius(int col, int row){
    return (distance(col,row)<=radius);
  }

  //returns the distance between a pixel in a given column and row is to the center of the circle
  private double distance(int col, int row) {
    double dx = Math.abs((double)(col)-centerx);
    double dy = Math.abs((double)(row)-centery);
    double d2 = dx*dx+dy*dy;
    return Math.sqrt(d2);
  }

}