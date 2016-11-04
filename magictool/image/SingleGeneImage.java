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
 * SingleGeneImage holds the pixels for a single spot and runs a particular datagenerator to create
 * the expression data.
 */
public class SingleGeneImage {


  private int maxRed; //highest red pixel
  private int maxGreen; //highest green pixel
  private int minRed; //lowest red pixel
  private int minGreen; //lowest green pixel
  private float avgRed; //average red pixel
  private float avgGreen; //average green pixel

  private SeededRegion sr; //SeededRegion
  private FixedCircle fc; //FixedCircle
  private AdaptiveCircle ac; //AdaptiveCircle

  /**Average Signal Ratio Calculation Method*/
  public static final int AVG_SIGNAL = 2;
  /**Background Subtraction Ratio Calculation Method*/
  public static final int AVG_SUBTRACT_BG = 4;
  /**Total Signal Ratio Calculation Method*/
  public static final int TOTAL_SIGNAL = 1;
  /**Total Signal Background Subtraction Ratio Calculation Method*/
  public static final int TOTAL_SUBTRACT_BG = 3;
  /**Fixed Circle Method*/
  public static final int FIXED_CIRCLE = 101;
  /**Seeded Region Growing Method*/
  public static final int SEEDED_REGION = 102;
  /**Adaptive Circle Method*/
  public static final int ADAPTIVE_CIRCLE = 103;

  /**red pixels*/
  protected int redpixels[];
  /**green pixels*/
  protected int greenpixels[];
  /**rows of pixels*/
  protected int rows;
  /**columns of pixels*/
  protected int cols;

  /**
   * Constructs a SingleGeneImage for the spot with the given red and green pixels
   * @param red red pixels
   * @param green green pixels
   * @param rows rows of pixels
   * @param cols columns of pixels
   */
  public SingleGeneImage(int[] red, int[] green, int rows, int cols) {
    this.redpixels = red;
    this.greenpixels = green;
    this.rows = rows;
    this.cols = cols;
    getInfo();
  }

  /**
   * Constructs a SingleGeneImage for the spot with the given red and green pixels
   * @param red red pixels
   * @param green green pixels
   */
  public SingleGeneImage(int[][] red, int[][] green){
    redpixels = new int[red.length*red[0].length];
    rows = 0;
    cols = red[0].length;
    for(int i=0; i<red.length; i++){
      for(int j=0; j<red[i].length; j++){
        redpixels[(i*red[0].length) + j]=red[i][j];
      }
      rows++;
    }
    greenpixels = new int[green.length*green[0].length];
    rows = 0;
    cols = green[0].length;
    for(int i=0; i<green.length; i++){
      for(int j=0; j<green[i].length; j++){
        redpixels[(i*green[0].length) + j]=green[i][j];
      }
      rows++;
    }
    getInfo();
  }

  /**
   * finds the minimum, maximum, and average values for the red and green pixels
   */
  public void getInfo(){
    int totalRed = redpixels[0];
    int totalGreen = greenpixels[0];
    maxRed = redpixels[0];
    maxGreen = greenpixels[0];
    minRed = redpixels[0];
    minGreen = greenpixels[0];
    for(int i=0; i<redpixels.length&&i<greenpixels.length; i++){
      if(redpixels[i]>maxRed) maxRed=redpixels[i];
      else if(redpixels[i]<minRed) minRed = redpixels[i];
      if(greenpixels[i]>maxGreen) maxGreen=greenpixels[i];
      else if(greenpixels[i]<minGreen) minGreen = greenpixels[i];
      totalRed+=redpixels[i];
      totalGreen+=greenpixels[i];
    }
    avgRed = ((float)totalRed)/redpixels.length;
    avgGreen = ((float)totalGreen)/greenpixels.length;
  }


  /**
   * gets the gene data for the spot using the given method
   * @param bgMethod method to identify foreground and background pixels
   * @return gene data for the spot using the given method
   */
  public GeneData getData(int bgMethod){
    return getData(bgMethod,null);
  }

  /**
   * gets the gene data for the spot using the given method
   * @param bgMethod method to identify foreground and background pixels
   * @param params parameters used by the given method
   * @return gene data for the spot using the given method
   */
  public GeneData getData(int bgMethod, Object[] params){

    if(bgMethod==FIXED_CIRCLE){
      try{
        fc = new FixedCircle(redpixels, greenpixels, rows, cols, Integer.parseInt(params[0].toString()));
        return fc.generateData();
      }catch(Exception e){return null;}
    }
    else if(bgMethod==SEEDED_REGION){
      try{
        sr = new SeededRegion(redpixels, greenpixels, rows, cols);
        sr.setMinMax(minRed, maxRed, minGreen, maxGreen);
        sr.setThreshold(Integer.parseInt(params[0].toString()));
        return sr.generateData();
      }catch(Exception e){return null;}
    }
    else if(bgMethod==ADAPTIVE_CIRCLE){
      try{
        ac = new AdaptiveCircle(redpixels, greenpixels, rows, cols,Integer.parseInt(params[0].toString()),Integer.parseInt(params[1].toString()),Integer.parseInt(params[2].toString()));
        ac.setMinMax(minRed, maxRed, minGreen, maxGreen);
        return ac.generateData();
      }catch(Exception e){return null;}
    }
    return null;
  }

  /**
   * gets the foreground pixels using the SeededRegion growing method
   * @param redCells whether the foreground is for the red image
   * @return an array of center spots (which is an array consisting of [0] = column and [1] = row
   */
  public int[][] getCenterSpots(boolean redCells){
    if(sr!=null)return sr.getCenterSpots(redCells);
    return null;
  }

  /**
   * gets the center and radius of the adaptive circle
   * @return an array with [0] = x position of center, [1] = y position of center, [2] = radius
   */
  public int[] getCenterAndRadius(){
    if(ac!=null){
     int center[] = new int[3];
     center[0] = ac.getCenter().x;
     center[1] = ac.getCenter().y;
     center[2] = ac.getRadius();
     return center;
    }
    return null;
  }


}