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

import java.awt.Point;

/**
 * AdaptiveCircle takes pixels from a red and green a microarray image and generates ratio data using the adaptive circle method.
 * The class implements the DataGenerator interface and outputs an instance of the GeneData class.
 */
public class AdaptiveCircle implements DataGenerator {

  /**red pixels*/
  protected int[] redpixels;
  /**green pixels*/
  protected int[] greenpixels;
  /**rows of pixels*/
  protected int rows;
  /**columns of pixels*/
  protected int cols;
  /**adaptive circle radius*/
  protected int radius=0;
  /**minimum adaptive circle radius*/
  protected int minRadius;
  /**maximum adaptive circle radius*/
  protected int maxRadius;
  /**threshold for the pixels*/
  protected int threshold=-1;

  private int redMax=256, redMin=0, greenMax=256, greenMin=0;
  private double centerX; //red center column
  private double centerY; //red center row
  private int combinedThresh[]; //=1 if above red threshold for each pixel



  /**
   * Constructs a AdaptiveCircle which is able to output microarray data for the given
   * red and green image pixels using the adaptive circle method with a default minimum radius of 3
   * and maximum radius of 8.
   * @param red red pixels
   * @param green green pixels
   * @param rows number of rows
   * @param cols number of columns
   * @param threshold pixel threshold
   */
  public AdaptiveCircle(int[] red, int[] green, int rows, int cols, int threshold){
    this(red,green,rows,cols,3,8, threshold);
  }

  /**
   * Constructs an AdaptiveCircle which is able to output microarray data for the given
   * red and green image pixels using the adaptive circle method with a given radius.
   * @param red red pixels
   * @param green green pixels
   * @param rows number of rows
   * @param cols number of columns
   * @param minRadius minimum adaptive circle radius
   * @param maxRadius maximum adaptive circle radius
   * @param threshold pixel threshold
   */
  public AdaptiveCircle(int[] red, int[] green, int rows, int cols, int minRadius, int maxRadius, int threshold) {
    redpixels=red;
    greenpixels=green;
    this.rows=rows;
    this.cols=cols;
    this.minRadius=minRadius;
    if(minRadius<1) minRadius=1;
    this.maxRadius=maxRadius;
    if((maxRadius*2)+1>cols||(maxRadius*2)+1>rows) maxRadius = Math.min((cols-1)/2, (rows-1)/2);
    this.threshold=threshold;
    if(threshold<25) threshold=25;
    centerX = (cols-1)/2.0;
    centerY = (rows-1)/2.0;
    setPixelThreshold();

  }

  /**
   * sets the minimum radius of the adaptive circle
   * @param minRadius minimum radius of the adaptive circle
   */
  public void setMinRadius(int minRadius){
    this.minRadius=minRadius;
  }

  /**
   * sets the maximum radius of the adaptive circle
   * @param maxRadius maximum radius of the adaptive circle
   */
  public void setMaxRadius(int maxRadius){
    this.maxRadius=maxRadius;
  }

  /**
   * returns the radius of the adaptive circle
   * @return redius of the adaptive circle
   */
  public int getRadius(){
    return radius;
  }

  /**
   * returns the center of the adaptive circle
   * @return center of the adaptive circle
   */
  public Point getCenter(){
    return new Point((int)Math.round(centerX), (int)Math.round(centerY));
  }

  /**
   * returns the data for the red and green image pixels in order
   * to create the ratio data for the particular gene using the adaptive circle method
   * @return gene data containing the totals of the foreground and background for both the red and green image
   */
  public GeneData generateData(){
    findCenterAndRadius();

    int bgnum=0, redbgtot=0, redfgtot=0, fgnum=0,greenbgtot=0, greenfgtot=0;
    int row=0, col=0;

    for(int i=0; i<redpixels.length; i++){
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
   * finds the center and radius of the adaptive circle
   */
  public void findCenterAndRadius(){
    double maxpct=-1;
    for(int rad=maxRadius; rad>=minRadius&&maxpct<1; rad--){
      int plus=0;
      for(int rw = rows/2; rw>=0&&rw<rows&&maxpct<1; rw+=plus){ // full circle requirement: for(int rw = rows/2; rw-radius>=0&&rw+radius<rows&&maxpct<1; rw+=plus){
        int coplus=0;
        for(int co = cols/2; co>=0&&co<cols&&maxpct<1; co+=coplus){  // full circle requirement:  for(int co = cols/2; co-radius>=0&&co+radius<cols&&maxpct<1; co+=coplus)
          double pct = pct(rw,co,rad);
          if(pct>maxpct){
            maxpct=pct;
            centerX = co;
            centerY = rw;
            radius = rad;

          }
          coplus = (coplus>=0? (coplus*-1)-1:(coplus*-1)+1);
        }
        plus = (plus>=0? (plus*-1)-1:(plus*-1)+1);
      }
    }

  }

  /**
   * sets the minimum and maximum pixels for the red and green image
   * @param redMin red minimum pixel
   * @param redMax red maximum pixel
   * @param greenMin green minimum pixel
   * @param greenMax green maximum pixel
   */
  public void setMinMax(int redMin, int redMax, int greenMin, int greenMax){
    this.redMin=redMin;
    this.redMax=redMax;
    this.greenMin=greenMin;
    this.greenMax=greenMax;
  }

  /**
   * returns whether a given pixel is within the radius of the adaptive circle
   * @param col column number of the pixel
   * @param row row number of the pixel
   * @return whether a given pixel is within the radius of the adaptive circle
   */
  public boolean withinRadius(int col, int row){
    return (distance(col,row)<=radius);
  }

  private boolean withinRadius(int col, int row, int centerx, int centery, int rad){
    return (distance(col,row, centerx, centery)<=rad);
  }

  //returns the distance between a pixel in a given column and row is to the center of the circle
  private double distance(int col, int row) {

    double dx = Math.abs((double)(col)-centerX);
    double dy = Math.abs((double)(row)-centerY);
    double d2 = dx*dx+dy*dy;
    return Math.sqrt(d2);
  }



  //returns the distance between a pixel in a given column and row is to the center of the circle
  private double distance(int col, int row, int centerx, int centery) {

    double dx = Math.abs((double)(col)-centerx);
    double dy = Math.abs((double)(row)-centery);
    double d2 = dx*dx+dy*dy;
    return Math.sqrt(d2);
  }

  //sets the pixel threshold numbers
  private void setPixelThreshold(){
    int rt = (int)(redMax - Math.round((redMax-redMin)*(threshold/100.0)));
    int gt = (int)(greenMax - Math.round((greenMax-greenMin)*(threshold/100.0)));

    combinedThresh = new int[redpixels.length];
    for(int i=0; i<combinedThresh.length; i++){
      combinedThresh[i] = (redpixels[i]>=rt||greenpixels[i]>=gt?1:0);
      //System.out.println("col:" + i%cols + " row:" + i/cols + " pix:" + redpixels[i] + ":" + redThresh[i]);
    }

  }

  //sums the thresholds from start row and column with given radius
  private double pct(int row, int col, int radius){

    int sum=0;
    int total=0;

    for(int i=row-radius; i<=row+radius&&i<rows; i++){
      while(i<0){i++;}
      for(int j=col-radius; j<=col+radius&&j<cols; j++){
        while(j<0){ j++;}
        if(withinRadius(j,i,col, row, radius)){
          sum+=(combinedThresh[i*cols+j]);
          total++;
        }
      }
    }

    return ((double)sum)/total;
  }

}