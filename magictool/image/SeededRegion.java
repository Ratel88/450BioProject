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

import java.util.Vector;

/**
 * SeededRegion takes pixels from a red and green a microarray image and generates ratio data using the seeded region growing method.
 * The class implements the DataGenerator interface and outputs an instance of the GeneData class.
 */
public class SeededRegion implements DataGenerator {

  private int redMax=256, redMin=0, greenMax=256, greenMin=0;
  /**regions for the red image*/
  protected Vector redRegions;
  /**regions for the green image*/
  protected Vector greenRegions;
  /**red pixels*/
  protected Cell[] redPixelCells;
  /**green pixels*/
  protected Cell[] greenPixelCells;
  /**rows of pixels*/
  protected int rows;
  /**columns of pixels*/
  protected int cols;
  /**threshold percent for selecting seeds*/
  protected int threshold=10;


  /**
   * Constructs a SeededRegion which is able to output microarray data for the given
   * red and green image pixels using the seeded region growing method.
   * @param redpixels red pixels
   * @param greenpixels green pixels
   * @param rows number of rows
   * @param cols number of columns
   */
  public SeededRegion(int[] redpixels, int[] greenpixels,int rows, int cols) {
    redRegions = new Vector();
    greenRegions = new Vector();
    redPixelCells = new Cell[redpixels.length];
    greenPixelCells = new Cell[greenpixels.length];
    this.rows=rows;
    this.cols=cols;

    int col=0;
    int row=0;

    for(int i=0; i<redpixels.length; i++){
        redPixelCells[i] = new Cell(col,row,redpixels[i],false);
        greenPixelCells[i] = new Cell(col,row,greenpixels[i],false);
        col++;

        if(col>=cols){
          col=0;
          row++;
        }
      }
  }


  /**
   * Constructs a SeededRegion which is able to output microarray data for the given
   * red and green image pixels using the seeded region growing method.
   * @param redpixels red pixels
   * @param greenpixels green pixels
   */
  public SeededRegion(int[][] redpixels, int[][] greenpixels) {
    redRegions = new Vector();
    greenRegions = new Vector();
    redPixelCells = new Cell[redpixels.length*redpixels[0].length];
    greenPixelCells = new Cell[redpixels.length*redpixels[0].length];
    rows = 0;
    cols = redpixels[0].length;
    for(int i=0; i<redpixels.length; i++){
      for(int j=0; j<redpixels[i].length; j++){
        redPixelCells[(i*redpixels[0].length) + j]=new Cell(j,i,redpixels[i][j],false);
        greenPixelCells[(i*greenpixels[0].length) + j]=new Cell(j,i,greenpixels[i][j],false);
      }
      rows++;
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
   * sets the threshold for selecting seeds
   * @param threshold percent for selecting seeds
   */
  public void setThreshold(int threshold){
    this.threshold=threshold;
  }

  /**
   * gets the threshold for selecting seeds
   * @return threshold percent for selecting seeds
   */
  public int getThreshold(){
    return threshold;
  }

  /**
   * gets the cell value for the pixel at the given row and column in the specified image
   * @param col column number
   * @param row row number
   * @param redCells whether the pixel is in the red image
   * @return cell value for the specified pixel
   */
  public int getCellValue(int col, int row, boolean redCells){
    if(redCells) return redPixelCells[row*cols + col].value;
    return greenPixelCells[row*cols + col].value;
  }


  /**
   * creates a new region adding the specified cell as the first seed
   * @param col pixel column
   * @param row pixel row
   * @param redCells whether pixel is in red image
   */
  public void addSeed(int col, int row, boolean redCells){
    if(redCells){
      if(!redPixelCells[row*cols + col].taken){
        redPixelCells[row*cols + col].taken=true;
        Region r = new Region(redPixelCells[row*cols + col], true);
        redRegions.addElement(r);

        for(int i=0; i<redRegions.size(); i++){
          ((Region)redRegions.elementAt(i)).getNeighbors();
        }
      }
    }
    else{
      if(!greenPixelCells[row*cols + col].taken){
        greenPixelCells[row*cols + col].taken=true;
        Region r = new Region(greenPixelCells[row*cols + col], false);
        greenRegions.addElement(r);

        for(int i=0; i<greenRegions.size(); i++){
          ((Region)greenRegions.elementAt(i)).getNeighbors();
        }
      }

    }
  }

  /**
   * adds a specified cell as a new seed to the specified region number
   * @param col pixel column
   * @param row pixel row
   * @param region region number to add seeds
   * @param redCells whether pixel is in red image
   */
  public void addSeed(int col, int row, int region, boolean redCells){
    if(redCells){
      if(!redPixelCells[row*cols + col].taken){
        redPixelCells[row*cols + col].taken=true;
        Region r = (Region)redRegions.elementAt(region);
        r.addCell(redPixelCells[row*cols + col]);
        redRegions.setElementAt(r,region);

        for(int i=0; i<redRegions.size(); i++){
          ((Region)redRegions.elementAt(i)).getNeighbors();
        }
      }
    }
    else{
      if(!greenPixelCells[row*cols + col].taken){
        greenPixelCells[row*cols + col].taken=true;
        Region r = (Region)greenRegions.elementAt(region);
        r.addCell(greenPixelCells[row*cols + col]);
        greenRegions.setElementAt(r,region);

        for(int i=0; i<greenRegions.size(); i++){
          ((Region)greenRegions.elementAt(i)).getNeighbors();
        }
      }
    }
  }

  /**
   * gets the pixels from the foreground region
   * @param redCells whether the foreground is for the red image
   * @return an array of center spots (which is an array consisting of [0] = column and [1] = row
   */
  public int[][] getCenterSpots(boolean redCells){
    if(redCells){
      if (redRegions.size()>0){
        Region r = ((Region)redRegions.elementAt(0));
        int[][] temp = new int[r.getRegionSize()][2];
        for(int i=0; i<temp.length; i++){
          temp[i][0] = ((Cell)r.seeds.elementAt(i)).col;
          temp[i][1] = ((Cell)r.seeds.elementAt(i)).row;
        }
        return temp;
      }
      return null;
    }
    else{
      if (greenRegions.size()>0){
        Region r = ((Region)greenRegions.elementAt(0));
        int[][] temp = new int[r.getRegionSize()][2];
        for(int i=0; i<temp.length; i++){
          temp[i][0] = ((Cell)r.seeds.elementAt(i)).col;
          temp[i][1] = ((Cell)r.seeds.elementAt(i)).row;
        }
        return temp;
      }
      return null;
    }
  }

  /**
   * returns the data for the red and green image pixels in order
   * to create the ratio data for the particular gene using the seeded region growing method
   * @return gene data containing the totals of the foreground and background for both the red and green image
   */
  public GeneData generateData(){

    findSeeds(true);
    assignCells(redPixelCells, redRegions, true);

    findSeeds(false);
    assignCells(greenPixelCells, greenRegions, false);

    if(redRegions.size()>0&&greenRegions.size()>0){
      int redfgtot = ((Region)redRegions.elementAt(0)).getRegionTotal();
      int redfgnum = ((Region)redRegions.elementAt(0)).getRegionSize();

      int redbgtot=0, redbgnum=0;
      for(int i=1; i<redRegions.size(); i++){
        redbgtot+=((Region)redRegions.elementAt(i)).getRegionTotal();
        redbgnum+=((Region)redRegions.elementAt(i)).getRegionSize();
      }



      int greenfgtot = ((Region)greenRegions.elementAt(0)).getRegionTotal();
      int greenfgnum = ((Region)greenRegions.elementAt(0)).getRegionSize();

      int greenbgtot=0, greenbgnum=0;
      for(int i=1; i<greenRegions.size(); i++){
        greenbgtot+=((Region)greenRegions.elementAt(i)).getRegionTotal();
        greenbgnum+=((Region)greenRegions.elementAt(i)).getRegionSize();
      }

      return new GeneData(redfgtot, redbgtot, greenfgtot, greenbgtot,redfgnum, redbgnum, greenfgnum, greenbgnum);
    }

    return null;
  }

  /**
   * finds the seeds for the foreground and background regions based on the threshold for the given image
   * @param redCells whether to find seeds for the red image or green image
   */
  public void findSeeds(boolean redCells){
     int cellThresh;
     if (redCells)
    	 	cellThresh= (int)(redMax - Math.round((redMax-redMin)*(threshold/100.0)));
     else
    	 	cellThresh = (int)(greenMax - Math.round((greenMax-greenMin)*(threshold/100.0)));

     boolean notFound=true;
     int startRow = (rows-1)/2;
     int endRow = startRow;
     int startCol = (cols-1)/2;
     int endCol = startCol;
     while(notFound){
      for(int j=startRow; j<=endRow; j++){
        for(int i=startCol; i<=endCol; i++){
          if(getCellValue(i,j, redCells) >= cellThresh){
            addSeed(i,j, redCells);
            notFound=false;

            break;
          }
        }
        if(!notFound) break;

      }

      if(notFound){
          if(startRow>0) startRow--;
          if(startCol>0) startCol--;
          if(endRow<rows-1) endRow++;
          if(endCol<cols-1) endCol++;

        }
     }

     if(redCells) cellThresh = (int)(redMin + Math.round((redMax-redMin)*(threshold/100.0)));
     else cellThresh = (int)(greenMin + Math.round((greenMax-greenMin)*(threshold/100.0)));

     int testRows=3;
     int testCols=3;

     if(rows<9) testRows=2;
     if(cols<9) testCols=2;

     if(rows<4) testRows=1;
     if(cols<4) testCols=1;


     //top left corner
     notFound = true;

     for(int j=0; j<testRows; j++){
      for(int i=0; i<testCols; i++){
        if(getCellValue(i,j,redCells) <= cellThresh){
            addSeed(i,j, redCells);
            notFound=false;
            break;
        }
      }
        if(!notFound) break;
     }

     //top right corner
     notFound = true;

     for(int j=0; j<testRows; j++){
      for(int i=0; i<testCols; i++){
        if(getCellValue(cols-i-1,j,redCells) <= cellThresh){
            addSeed(cols-i-1,j, redCells);
            notFound=false;
            break;
        }
      }
        if(!notFound) break;
     }

     //bottom left corner
     notFound = true;

     for(int j=0; j<testRows; j++){
      for(int i=0; i<testCols; i++){
        if(getCellValue(i,rows-j-1, redCells) <= cellThresh){
            addSeed(i,rows-j-1,redCells);
            notFound=false;
            break;
        }
      }
        if(!notFound) break;
     }

     //bottom right corner
     notFound = true;

     for(int j=0; j<testRows; j++){
      for(int i=0; i<testCols; i++){
        if(getCellValue(cols-i-1,rows-j-1, redCells) <= cellThresh){
            addSeed(cols-i-1,rows-j-1, redCells);
            notFound=false;
            break;
        }
      }
        if(!notFound) break;
     }


  }

  /**
   * assigns the pixels to the proper region (actual implementation of the seeded region growing)
   * @param pixelCells pixels to place in regions
   * @param regions regions to place pixels in
   * @param redCells whether the pxels are in the red orgreen image
   */
  public void assignCells(Cell[] pixelCells, Vector regions, boolean redCells){

    boolean allTaken=true;
    for(int i=0; i<pixelCells.length; i++){
      if(!pixelCells[i].taken){
       allTaken=false;
       break;
      }
    }

    if(regions.size()==0){
        return;
    }

    while(!allTaken){
      int closest, region=0;
      float distance;
      while((closest=((Region)regions.elementAt(region)).getClosetNeighbor())==-1){
        region++;
        if (region>=regions.size()) break;
      }

      if(closest==-1){
        break;
      }

      else{
        distance = ((Region)regions.elementAt(region)).getDistance(closest);

        for(int p = region+1; p<regions.size(); p++){
          int tempClose = ((Region)regions.elementAt(p)).getClosetNeighbor();
          float tempdist;
          if(tempClose!=-1){
            tempdist = ((Region)regions.elementAt(p)).getDistance(tempClose);
            if(tempdist<distance){
              region=p;
              closest=tempClose;
              distance=tempdist;
            }
          }
        }

        addSeed(pixelCells[closest].col,pixelCells[closest].row, region, redCells);

      }

      allTaken = true;
      for(int i=0; i<pixelCells.length; i++){
        if(!pixelCells[i].taken){
          allTaken=false;
          break;
        }
      }

    }
  }

  /**
   * Region is an inner class which holds a number of seeds that form a region for
   * the seeded region growing method.
   */
  protected class Region{

    /**seeds for the region*/
    protected Vector seeds;
    /**neighbor pixels*/
    protected Vector neighbors;
    /**whether the region is in the red or green image*/
    protected boolean redCells;

    /**
     * Constructs a new region with the given seed
     * @param regioncell first region seed
     * @param redCells whether the region is in the red or green image
     */
    public Region(Cell regioncell, boolean redCells){
      seeds = new Vector();
      seeds.addElement(regioncell);
      this.redCells=redCells;
      getNeighbors();
    }

    /**
     * Constructs a new region with the given seeds
     * @param regioncells first region seeds
     * @param redCells whether the region is in the red or green image
     */
    public Region(Cell[] regioncells, boolean redCells){
      seeds = new Vector();
      this.redCells=redCells;
      for(int i=0; i<regioncells.length; i++){
        seeds.addElement(regioncells[i]);
      }
      getNeighbors();
    }

    /**
     * adds a pixel to the region
     * @param regioncell pixel to add to the region
     */
    public void addCell(Cell regioncell){
      seeds.addElement(regioncell);
    }

    /**
     * gets the neighbor pixels of the region
     */
    public void getNeighbors(){
      neighbors = new Vector();
      for(int i=0; i<(redCells?redPixelCells.length:greenPixelCells.length); i++){
        if(!(redCells?redPixelCells[i].taken:greenPixelCells[i].taken)){
          Cell c = (redCells?redPixelCells[i]:greenPixelCells[i]);
          for(int j=0; j<seeds.size(); j++){
            Cell s = (Cell)seeds.elementAt(j);
            if((c.row==s.row||(c.row-1)==s.row||(c.row+1)==s.row)&&
              (c.col==s.col||(c.col-1)==s.col||(c.col+1)==s.col)){
                neighbors.addElement(new Integer(i));
                break;
            }
          }
        }
      }
    }

    /**
     * gets the neighbor pixel which is closest to the region
     * @return neighbor pixel which is closest to the region
     */
    public int getClosetNeighbor(){
      if(neighbors.size()==0) return -1;
      int closest = ((Integer)neighbors.elementAt(0)).intValue();
      float distance = getDistance(closest);
      for(int i=1; i<neighbors.size(); i++){
        int num = ((Integer)neighbors.elementAt(i)).intValue();
        float tempdist;
        if((tempdist=getDistance(num))<distance){
          distance=tempdist;
          closest=num;
        }

      }

      return closest;
    }

    /**
     * gets the distance between a specified pixel value and the region average value
     * @param number pixel number
     * @return distance between a specified pixel value and the region average value
     */
    public float getDistance(int number){
      return Math.abs((float)((redCells?redPixelCells[number].value:greenPixelCells[number].value))-(float)getRegionAverage());
    }

    /**
     * gets the average value of the pixels in the region
     * @return average value of the pixels in the region
     */
    public double getRegionAverage(){
      double sum=0.0;
      for(int i=0; i<seeds.size(); i++){
        Cell tempCell = (Cell)seeds.elementAt(i);
        sum+=(double)tempCell.value;
      }

      return sum/seeds.size();
    }

    /**
     * gets the total value of the pixels in the region
     * @return total value of the pixels in the region
     */
    public int getRegionTotal(){
      int sum=0;
      for(int i=0; i<seeds.size(); i++){
        Cell tempCell = (Cell)seeds.elementAt(i);
        sum+=tempCell.value;
      }

      return sum;
    }

    /**
     * gets the number of pixels in the region
     * @return number of pixels in the region
     */
    public int getRegionSize(){
      return seeds.size();
    }

    /**
     * gets the String representation of the region
     * @return String representation of the region
     */
    public String toString(){
      String all="Cells:" + seeds.size() + "\n";
      for(int i=0; i<seeds.size(); i++){
        Cell c = (Cell) seeds.elementAt(i);
        all+="Col:" + c.col + " Row:" + c.row + "\n";
      }

      return all;
    }

  }

  private class Cell{

    public Cell(int col, int row, int value, boolean taken){
      this.col=col;
      this.row=row;
      this.value=value;
      this.taken=taken;
    }

    protected boolean taken; //whether cell has been taken
    protected int col, row, value;
  }
}