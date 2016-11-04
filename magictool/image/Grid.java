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
import java.awt.geom.Point2D;

/**
 * Grid is class which holds the coordinates, rows, and columns for a single grid from a microarray
 * image. The Grid class contains information about the coordinates of individual
 * spots as well as their row and column numbers.
 */
public class Grid {

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
  /**total number of spots*/
  private int numOfSpots;
  /**array of spot polygons*/
  protected Polygon[] spots;
  /**current spot polygon*/
  protected Polygon currentSpot;
  /**current spot number*/
  protected int currentSpotNum;
  /**list of flags, i.e. whether or not a spot is flagged*/
  protected FlagList myFlagList;

  /**
   * Null constructor which creates an empty grid
   */
  public Grid() {
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
  public Grid(int tLeftX, int tLeftY, int tRightX, int tRightY, int bLeftX, int bLeftY, int bRightX, int bRightY, int rows, int columns) {
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
    //myFlagList = new FlagList(getNumOfSpots());
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
  public int getNumOfSpots() {
    return numOfSpots;
  }

  /**
   * gets the polygon of the specified spot
   * @param i index of the spot
   * @return polygon of the specified spot
   */
  public Polygon getSpot(int i) {
    return spots[i];
  }

  /**
   * gets the polygon of the current spot
   * @return polygon of the current spot
   */
  public Polygon getCurrentSpot() {
    return this.getSpot(this.getCurrentSpotNum());
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
   * sets the number of rows of spots
   * @param rows number of rows of spots
   */
  public void setRows(int rows) {
    this.rows = rows;
    setNumOfSpots();
  }


  /**
   * sets the number of columns of spots
   * @param columns number of columns of spots
   */
  public void setColumns(int columns) {
    this.columns = columns;
    setNumOfSpots();
  }

  /**
   * sets the number of spots
   */
  public void setNumOfSpots() {
    this.numOfSpots = getColumns()*getRows();
//    if (numOfSpots != 0) myFlagList.setSize(this.numOfSpots); //this is broken; I don't know why //TODO: fix
  }

  /**
   * Sets the coordinates for the spots based on the coordinates of the grid
   * and the number of rows and columns of spots.
   */
  public void setSpots(){
    setSpots(getTranslatedPolygon());
  }

  /**
   * Sets the spots coordinates for the spots based on a translated polygon and the
   * number of rows and columns of spots. A translated polygon is a polygon which encompasses
   * every spots in the grid (since the grid coordinates are based on the center of a spot).
   * @param transP translated polygon to set the spots from
   */
  public void setSpots(Polygon transP) {
    spots = new Polygon[getNumOfSpots()];
    for(int i=0; i<spots.length; i++){
      setSpot(i,transP);
    }
  }

  /**
   * Sets the coordinates for the specified spot based on the coordinates of the grid
   * and the number of rows and columns of spots.
   * @param i index of the spot to set
   */
  public void setSpot(int i){
    setSpot(i,getTranslatedPolygon());
  }

  /**
   * Sets the spots coordinates for the specified spot based on a translated polygon and the
   * number of rows and columns of spots. A translated polygon is a polygon which encompasses
   * every spots in the grid (since the grid coordinates are based on the center of a spot).
   * @param transP translated polygon to set the spots from
   * @param i index of the spot to set
   */
  public void setSpot(int i,Polygon transP) {
    Polygon p = transP;
    if(p!=null){
        Polygon[] vertLines = getVertLines(p);
        Polygon[] horiLines = getHoriLines(p);
        //int spotNum = getCurrentSpotNum()+1;
        int spotNum = i + 1;
        int spotRow = (int)Math.ceil( spotNum/(double)getColumns() );
        int spotColumn =  spotNum - ((spotRow-1)*getColumns());
        double widthdx = (double)(p.xpoints[1] - p.xpoints[0])/(double)getColumns();
        double widthdy = (double)(p.ypoints[0] - p.ypoints[1])/(double)getColumns();;
        double heightdx = (double)(p.xpoints[3]-p.xpoints[0])/(double)getRows();
        double heightdy = (double)(p.ypoints[3]-p.ypoints[0])/(double)getRows();
        int topLeftX, topLeftY, topRightX, topRightY, botRightX, botRightY, botLeftX, botLeftY;
        if (spotColumn == 1) {
            topLeftX = p.xpoints[0]+(int)((spotRow-1)*heightdx);
            topLeftY = p.ypoints[0]+(int)((spotRow-1)*heightdy);
        } else {
            topLeftX = vertLines[spotColumn-2].xpoints[0]+(int)((spotRow-1)*heightdx);
            topLeftY = vertLines[spotColumn-2].ypoints[0]+(int)((spotRow-1)*heightdy);
        }
        topRightX = (int)(topLeftX + widthdx);
        topRightY = (int)(topLeftY - widthdy);
        botRightX = (int)(topRightX + heightdx);
        botRightY = (int)(topRightY + heightdy);
        botLeftX = (int)(topLeftX + heightdx);
        botRightY = (int)(topLeftY + heightdy);
        int actualX[] = {topLeftX, topRightX, botRightX, botLeftX};
        int actualY[] = {topLeftY, topRightY, botRightY, botRightY};
        int cellX[] = { (actualX[0]+actualX[3])/2 , (actualX[1]+actualX[2])/2 , (actualX[1]+actualX[2])/2 , (actualX[0]+actualX[3])/2 };
        int cellY[] = { (actualY[0]+actualY[1])/2 , (actualY[0]+actualY[1])/2 , (actualY[2]+actualY[3])/2 , (actualY[2]+actualY[3])/2 };
        spots[i] = new Polygon(cellX,cellY,4);
    }
  }

  /**
   * sets the current spot number
   * @param currentNum current spot number
   */
  public void setCurrentSpot(int currentNum){
    currentSpotNum = currentNum;
    if(spots!=null&&currentNum<spots.length)currentSpot = spots[currentNum];
  }

   /**
   * gets the current spot number
   * @return current spot number
   */
  public int getCurrentSpotNum() {
    return currentSpotNum;
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
    return new Point((currentSpotNum)%columns,(currentSpotNum)/columns);
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
   * Gets the translated polygon of the grid based on the coordinates specified and the
   * number of rows and columns in the grid. A Translated Polygon is one which takes into
   * account that the grid coordinates specified by the user are at the center of the spots.
   * These polygons contain the entirety of every spot in the grid.
   * @return translated polygon of the grid specified by the user
   */
  public Polygon getTranslatedPolygon() {
    Polygon original = getPolygon();

    if (original==null) return null;
    else {
        double w = getWidth()/getColumns();
        double h = getHeight()/getRows();
        double alpha = this.getTilt();
        double d = (w*Math.tan(alpha))/2;
        double b1 = Math.sin(alpha)*((h/2)-d);
        double c = Math.sqrt((d*d)+((w/2)*(w/2)));
        double deltaX = c + b1;
        double deltaY = Math.sqrt( Math.pow(((h/2)-d),2) - (b1*b1) );
        int translatedTopLeftX = topLeftX-(int)deltaX;
        int translatedTopLeftY = topLeftY-(int)deltaY;
        int translatedBottomRightX = bottomRightX+(int)deltaX;
        int translatedBottomRightY = bottomRightY+(int)deltaY;
        double d2 = (h*Math.tan(alpha))/2;
        double d1 = (w/2)-2;
        double deltaX2 = d1*Math.cos(alpha);
        double d3 = d1*Math.sin(alpha);
        double d4 = h/(2*Math.cos(alpha));
        double deltaY2 = d3+d4;
        int translatedTopRightX = topRightX+(int)deltaX2;
        int translatedTopRightY = topRightY-(int)deltaY2;
        int translatedBottomLeftX = bottomLeftX-(int)deltaX2;
        int translatedBottomLeftY = bottomLeftY+(int)deltaY2;
        int translatedX[]={translatedTopLeftX,translatedTopRightX,translatedBottomRightX,translatedBottomLeftX};
        int translatedY[]={translatedTopLeftY,translatedTopRightY,translatedBottomRightY,translatedBottomLeftY};
        Polygon transP = new Polygon(translatedX,translatedY,4);
        return transP;
    }
  }
  
  /**
   * This method returns an array of points representing the corners of our grid. These
   * are translated points and represent the actual corners of our grid. Note they are organized
   * in  clockwise order starting with the upper left corner.
   * @return
   */
  public Point2D[] getTranslatedVertices(){
	    Polygon original = getPolygon();
	    Point [] points = new Point[4];
	    if (original==null) return null;
	    else {
	        double w = getWidth()/getColumns();
	        double h = getHeight()/getRows();
	        double alpha = this.getTilt();
	        double d = (w*Math.tan(alpha))/2;
	        double b1 = Math.sin(alpha)*((h/2)-d);
	        double c = Math.sqrt((d*d)+((w/2)*(w/2)));
	        double deltaX = c + b1;
	        double deltaY = Math.sqrt( Math.pow(((h/2)-d),2) - (b1*b1) );

	        double d1 = (w/2)-2;
	        double deltaX2 = d1*Math.cos(alpha);
	        double d3 = d1*Math.sin(alpha);
	        double d4 = h/(2*Math.cos(alpha));
	        double deltaY2 = d3+d4;
	        points[0] = new Point(topLeftX-(int)deltaX, topLeftY-(int)deltaY);
	        points[1] = new Point(topRightX+(int)deltaX2, topRightY-(int)deltaY2);
	        points[2]= new Point(bottomRightX+(int)deltaX, bottomRightY+(int)deltaY);
	        points[3] = new Point(bottomLeftX-(int)deltaX2, bottomLeftY+(int)deltaY2);
	    }
	   return points; 
  }
  
  /**
   * This method takes in two grids and tests to see if they intersect. The algorithm
   * used is based off of: http://processinghacks.com/hacks:detecting-line-to-line-intersection#source-code
   * @param g1 First grid to test for intersection.
   * @param g2 Second grid.
   * @return True if they intersect, false if they do not.
   */
  public static boolean gridIntersections(Grid g1, Grid g2){
	  Point2D[] pointsBoundingGrid1 = g1.getTranslatedVertices();
	  Point2D[] pointsBoundingGrid2 = g2.getTranslatedVertices();
	  boolean solution;
	  for(int i =0; i<4; i++){
		  for(int j=0; j<4; j++){
			  //Wrap for lower left corner to top left spot with the mod operator.
			  solution = lineSegmentIntersection(pointsBoundingGrid1[i],pointsBoundingGrid1[(i+1)%4],
					  pointsBoundingGrid2[j],pointsBoundingGrid2[(j+1)%4]);
			  if(solution==true){
				  return solution;
			  }
		  }
	  }
	  /*Check if a grid is contained inside of another grid. This is not perfect but is 
	  computationally cheap. It will catch most grid inside of grid problems. */
	  if(lineSegmentIntersection(pointsBoundingGrid1[0],pointsBoundingGrid1[2],
					  pointsBoundingGrid2[1],pointsBoundingGrid2[3])) return true;
	  
	  if(lineSegmentIntersection(pointsBoundingGrid1[1],pointsBoundingGrid1[3],
			  pointsBoundingGrid2[0],pointsBoundingGrid2[2])) return true;
	  
	  return false;
  }
  

  
  /**
   * This method tests if two line segments are intersecting. The first two points define
   * the first line segement. The second two points define the second line segment.
   * @return false if they do not intersect, true if they do intersect.
   */
  public static boolean lineSegmentIntersection(Point2D p1, Point2D p2, Point2D  p3, Point2D p4){
	  double x1, x2, x3, x4, y1, y2, y3, y4;
	  x1 = p1.getX(); y1 = p1.getY();
	  x2 = p2.getX(); y2 = p2.getY();
	  x3 = p3.getX(); y3 = p3.getY();
	  x4 = p4.getX(); y4 = p4.getY();
	  
	  double bx = x2 - x1;
	  double by = y2 - y1;
	  double dx = x4 - x3;
	  double dy = y4 - y3;
	 
	  double b_dot_d_perp = bx * dy - by * dx;
	 
	  if(b_dot_d_perp == 0) return false;
	 
	  double cx = x3 - x1;
	  double cy = y3 - y1;
	 
	  double t = (cx * dy - cy * dx) / b_dot_d_perp;
	  if(t < 0 || t > 1) return false;
	 
	  double u = (cx * by - cy * bx) / b_dot_d_perp;
	  if(u < 0 || u > 1) return false;	  
	  return true;
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

  /**
   * Gets a point containing the column and row of the spot at a given point
   * @param x x-coordinate
   * @param y y-coordinate
   * @return point containing the column (x-coordinate) and row (y-coordinate) of the spot at a given point
   */
  public Point getColRow(int x, int y){
    int row =-1;
    int col =-1;

    Polygon vert[] = getVertLines(getTranslatedPolygon());
    for(int i=0; i<vert.length; i++){
      if(vert[i].contains(x,y)){
        col = i;
        break;
      }
    }
    Polygon hor[] = getHoriLines(getTranslatedPolygon());
    for(int i=0; i<hor.length; i++){
      if(hor[i].contains(x,y)){
        row = i;
        break;
      }

    }
    if(row>=0 && col>=0) return new Point(col, row);
    return null;
  }

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
  
  /**
   * gets the array of booleans that are flags
   * @return array of booleans that are flags
   */
  /*public boolean[] getFlags() {
	  return myFlagList.getArray();
  }
  */
  /**
   * checks to see if a spot is flagged
   * @param spotNum spot number to check
   * @return status of spots (true if we don't use it)
   */
  /*public boolean checkFlag(int spotNum) {
	  return myFlagList.getStatus(spotNum);
  }
  */
  /**
   * sets the flag status of a spot
   * @param spotNum spot number to set
   * @param flag status of spot (true if we're not to use it)
   */
  /*public void setFlag(int spotNum, boolean flag) {
	  myFlagList.setStatus(spotNum, flag);
  }
  */
}