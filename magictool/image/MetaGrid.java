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
import java.awt.Polygon;

/**
 * Grid is class which holds the coordinates, rows, and columns for a single grid from a microarray
 * image. The Grid class contains information about the coordinates of individual
 * spots as well as their row and column numbers.
 */
public class MetaGrid {

  /**x-coordinate of top left corner*/
  protected int topLeftX;
  /**y-coordinate of top left corner*/
  protected int topLeftY;
  /**x-coordinate of top right corner*/
  protected int topRightX;
  /**y-coordinate of top right corner*/
  protected int topRightY;
  /**x-coordinate of bottom left corner*/
  protected int bottomLeftX;
  /**y-coordinate of bottom left corner*/
  protected int bottomLeftY;
  /**x-coordinate of bottom right corner*/
  protected int bottomRightX;
  /**y-coordinate of bottom right corner*/
  protected int bottomRightY;
  /**number of rows of spots*/
  protected int rows;
  /**number of columns of spots*/
  protected int columns;
  /**total number of grids*/
  private int numOfGrids;
  /**current grid number*/
  protected int currentGridNum;

  /**
   * Null constructor which creates an empty grid
   */
  public MetaGrid() {
    this(0,0,0,0,0,0,0,0,0,0);
  }


  /**
   * Constructs a new grid with the specified coordinates and number of rows and columns
   * @param tLeftX x-coordinate of top left corner
   * @param tLeftY y-coordinate of top left corner
   * @param tRightX x-coordinate of top right corner
   * @param tRightY y-coordinate of top right corner
   * @param bLeftX x-coordinate of bottom left corner
   * @param bLeftY y-coordinate of bottom left corner
   * @param bRightX x-coordinate of bottom right corner
   * @param bRightY y-coordinate of bottom right corner
   * @param rows number of rows of spots
   * @param columns number of columns of spots
   */
  public MetaGrid(int tLeftX, int tLeftY, int tRightX, int tRightY, int bLeftX, int bLeftY, int bRightX, int bRightY, int rows, int columns) {
    setTopLeftX(tLeftX);
    setTopLeftY(tLeftY);
    setTopRightX(tRightX);
    setTopRightY(tRightY);
    setBottomLeftX(bLeftX);
    setBottomLeftY(bLeftY);
    setBottomRightX(bRightX);
    setBottomRightY(bRightY);
    setRows(rows);
    setColumns(columns);
  }

  /**
   * gets the x-coordinate of top left corner
   * @return x-coordinate of top left corner
   */
  public int getTopLeftX() {
    return topLeftX;
  }

  /**
   * gets the y-coordinate of top left corner
   * @return y-coordinate of top left corner
   */
  public int getTopLeftY() {
    return topLeftY;
  }

  /**
   * gets the x-coordinate of top right corner
   * @return x-coordinate of top left corner
   */
  public int getTopRightX() {
    return topRightX;
  }

  /**
   * gets the y-coordinate of top right corner
   * @return y-coordinate of top right corner
   */
  public int getTopRightY() {
    return topRightY;
  }

  /**
   * gets the x-coordinate of bottom left corner
   * @return x-coordinate of bottom left corner
   */
  public int getBottomLeftX() {
    return bottomLeftX;
  }

  /**
   * gets the y-coordinate of bottom left corner
   * @return y-coordinate of bottom left corner
   */
  public int getBottomLeftY() {
    return bottomLeftY;
  }

  /**
   * gets the x-coordinate of bottom right corner
   * @return x-coordinate of bottom right corner
   */
  public int getBottomRightX() {
    return bottomRightX;
  }

  /**
   * gets the y-coordinate of bottom right corner
   * @return y-coordinate of bottom right corner
   */
  public int getBottomRightY() {
    return bottomRightY;
  }

  /**
   * gets the number of rows of spots
   * @return number of rows of spots
   */
  public int getRows() {
    return rows;
  }

  /**
   * gets the number of columns of spots
   * @return number of columns of spots
   */
  public int getColumns() {
    return columns;
  }

  /**
   * gets the number of spots
   * @return number of spots
   */
  public int getNumOfGrids() {
    return numOfGrids;
  }


  /**
   * sets the x-coordinate of top left corner
   * @param topLeftX x-coordinate of top left corner
   */
  public void setTopLeftX(int topLeftX) {
    this.topLeftX = topLeftX;
  }

  /**
   * sets the y-coordinate of top left corner
   * @param topLeftY y-coordinate of top left corner
   */
  public void setTopLeftY(int topLeftY) {
    this.topLeftY = topLeftY;
  }

  /**
   * sets the x-coordinate of top right corner
   * @param topRightX x-coordinate of top right corner
   */
  public void setTopRightX(int topRightX) {
    this.topRightX = topRightX;
  }

  /**
   * sets y-coordinate of top right corner
   * @param topRightY y-coordinate of top right corner
   */
  public void setTopRightY(int topRightY) {
    this.topRightY = topRightY;
  }

  /**
   * sets the x-coordinate of bottom left corner
   * @param bottomLeftX x-coordinate of bottom left corner
   */
  public void setBottomLeftX(int bottomLeftX) {
    this.bottomLeftX = bottomLeftX;
  }

  /**
   * sets the y-coordinate of bottom left corner
   * @param bottomLeftY y-coordinate of bottom left corner
   */
  public void setBottomLeftY(int bottomLeftY) {
    this.bottomLeftY = bottomLeftY;
  }

  /**
   * sets the x-coordinate of bottom right corner
   * @param bottomRightX x-coordinate of bottom right corner
   */
  public void setBottomRightX(int bottomRightX) {
    this.bottomRightX = bottomRightX;
  }

  /**
   * sets the y-coordinate of bottom right corner
   * @param bottomRightY y-coordinate of bottom right corner
   */
  public void setBottomRightY(int bottomRightY) {
    this.bottomRightY = bottomRightY;
  }

  /**
   * sets the number of rows of grids
   * @param rows number of rows of grids
   */
  public void setRows(int rows) {
    this.rows = rows;
    setNumOfGrids();
  }


  /**
   * sets the number of columns of grids
   * @param columns number of columns of grids
   */
  public void setColumns(int columns) {
    this.columns = columns;
    setNumOfGrids();
  }

  /**
   * sets the number of grids
   */
  public void setNumOfGrids() {
    this.numOfGrids = getColumns()*getRows();
  }


  /**
   * sets the current grid number
   * @param currentNum current grid number
   */
  public void setCurrentGrid(int currentNum){
    currentGridNum = currentNum;
    // check here ! ! !!! if(grids!=null&&currentNum<grids.length)currentGrid = grids[currentNum];
  }

   /**
   * gets the current grid number
   * @return current grid number
   */
  public int getCurrentGridNum() {
    return currentGridNum;
  }

  /*
  public void setCurrentSpotNum(int currentNum){
    currentSpotNum = currentNum;
  }*/

  /**
   * gets the actual current column and row numbers of the spot. (This does not
   * account for various possible user specifications of spot ordering).
   * @return point containing current column (x-coordinate) and row (y-coordinate) numbers
   */
  public Point getCurrentColRow(){
    return new Point((currentGridNum)%columns,(currentGridNum)/columns);
  }

  /**
   * gets the polygon coordinates of the grid (based on center points specified by
   * the user). This does not encompass the entire spot for any spot which is at the
   * edge of the grid
   * @return polgon containing the coordinates of the grid
   */
  public Polygon getPolygon(){
    if((topRightX==0&&topRightY==0&&topLeftX==0&&topLeftY==0)&&
      (bottomRightX==0&&bottomRightY==0&&bottomLeftX==0&&bottomLeftY==0))
        return null;
    else{
      int x[]={topLeftX, topRightX, bottomRightX, bottomLeftX};
      int y[]={topLeftY, topRightY, bottomRightY, bottomLeftY};
      return new Polygon(x,y,4);
    }
  }

  /**
   * Gets an array of polygons containing the vertical lines to draw in the grid seperating
   * columns of spots based on a given translated polygon
   * @param translatedPolygon translated polygon to create vertical lines from
   * @return array of polygons containing the vertical lines to draw in the grid
   */
  public Polygon[] getVertLines(Polygon translatedPolygon) {
    Polygon[] vertLines = new Polygon[getColumns()];
    double dy1 = (double)(translatedPolygon.ypoints[1]-(translatedPolygon.ypoints[0]))/(double)getColumns();
    double dx1 = (double)(translatedPolygon.xpoints[1]-(translatedPolygon.xpoints[0]))/(double)getColumns();
    for(int j = 0;j<vertLines.length;j++) {
      vertLines[j] = new Polygon();
      vertLines[j].addPoint(translatedPolygon.xpoints[0]+(int)((j+1)*dx1),translatedPolygon.ypoints[0]+(int)((j+1)*dy1));
      vertLines[j].addPoint(translatedPolygon.xpoints[3]+(int)((j+1)*dx1),translatedPolygon.ypoints[3]+(int)((j+1)*dy1));

      vertLines[j].addPoint(translatedPolygon.xpoints[3]+(int)((j)*dx1),translatedPolygon.ypoints[3]+(int)((j)*dy1));
      vertLines[j].addPoint(translatedPolygon.xpoints[0]+(int)((j)*dx1),translatedPolygon.ypoints[0]+(int)((j)*dy1));

    }
    return vertLines;
  }

    /**
   * Gets an array of polygons containing the horizontal lines to draw in the grid seperating
   * rows of spots based on a given translated polygon
   * @param translatedPolygon translated polygon to create vertical lines from
   * @return array of polygons containing the horizontal lines to draw in the grid
   */
  public Polygon[] getHoriLines(Polygon translatedPolygon) {
    Polygon[] horiLines = new Polygon[getRows()];
    double dy2 = (double)(translatedPolygon.ypoints[3]-(translatedPolygon.ypoints[0]))/(double)getRows();
    double dx2 = (double)(translatedPolygon.xpoints[3]-(translatedPolygon.xpoints[0]))/(double)getRows();
    for(int k = 0;k<horiLines.length;k++) {
      horiLines[k] = new Polygon();
      horiLines[k].addPoint(translatedPolygon.xpoints[0]+(int)((k+1)*dx2),translatedPolygon.ypoints[0]+(int)((k+1)*dy2));
      horiLines[k].addPoint(translatedPolygon.xpoints[1]+(int)((k+1)*dx2),translatedPolygon.ypoints[1]+(int)((k+1)*dy2));

      horiLines[k].addPoint(translatedPolygon.xpoints[1]+(int)((k)*dx2),translatedPolygon.ypoints[1]+(int)((k)*dy2));
      horiLines[k].addPoint(translatedPolygon.xpoints[0]+(int)((k)*dx2),translatedPolygon.ypoints[0]+(int)((k)*dy2));

    }
    return horiLines;
  }

//  /**
//   * Gets a point containing the column and row of the spot at a given point
//   * @param x x-coordinate
//   * @param y y-coordinate
//   * @return point containing the column (x-coordinate) and row (y-coordinate) of the spot at a given point
//   */
//  public Point getColRow(int x, int y){
//    int row =-1;
//    int col =-1;
//
//    Polygon vert[] = getVertLines(getTranslatedPolygon());
//    for(int i=0; i<vert.length; i++){
//      if(vert[i].contains(x,y)){
//        col = i;
//        break;
//      }
//    }
//    Polygon hor[] = getHoriLines(getTranslatedPolygon());
//    for(int i=0; i<hor.length; i++){
//      if(hor[i].contains(x,y)){
//        row = i;
//        break;
//      }
//
//    }
//    if(row>=0 && col>=0) return new Point(col, row);
//    return null;
//  }

  //returns the tilt of the polygon
  private double getTilt() {
    double dy = (double)(Math.abs(this.getPolygon().ypoints[0]-this.getPolygon().ypoints[1]));
    double dx = (double)(Math.abs(this.getPolygon().xpoints[1]-this.getPolygon().xpoints[0]));
    return Math.atan(dy/dx);
  }

  //returns the width of grid (not from the translated polygon)
  private double getWidth() {
    Polygon p = this.getPolygon();
    return (distance(p.xpoints[1],p.ypoints[1],p.xpoints[0],p.ypoints[0]));
  }

  //returns the height of grid (not from the translated polygon)
  private double getHeight() {
    Polygon p = this.getPolygon();
    return (distance(p.xpoints[3],p.ypoints[3],p.xpoints[0],p.ypoints[0]));
  }

  //returns the distance between two points
  private double distance(int x1, int y1, int x2, int y2) {
    long dx = Math.abs((long)(x2-x1));
    long dy = Math.abs((long)(y2-y1));
    double d2 = dx*dx+dy*dy;
    return Math.sqrt(d2);
  }

  /**
   * returns whether or not the grid has been validly specified
   * @return whether or not the grid has been validly specified
   */
  public boolean isValid(){
    if(rows==0||columns==0||bottomLeftX==bottomRightX||topLeftX==topRightX||topLeftY==bottomRightY||topRightY==bottomRightY) return false;
    return true;
  }
}