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
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import magictool.Spot;

/**
 * GridManager is a class which holds all of the grids for a microarray images. It also
 * holds the gene list as well as information about the number of grids and the placement
 * of spots within grids. GridManager manages changes within all of the grids and ensures
 * that all grids have been specified before segmentation.
 */
public class GridManager {

  /**number of grids*/
  protected int numGrids;
  /**whether spots are numbered from left to right*/
  protected boolean leftRight;
  /**whether spots are numbered from top to bottom*/
  protected boolean topBottom;
  /**whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1*/
  protected boolean spotDirection;
  /**array of grids from microarray image*/
  protected Grid[] grids;
  /**current grid number*/
  protected int currentGridNum = -1;
  /**gene list containing the names of the genes from the godlist*/
  protected GeneList geneList = null;
  /**spot data containing the name, grid, and transformed spot number of all spots*/
  protected Spot[] spots = null;

  /**
   * Constructs an empty grid manager with no grids.
   */
  public GridManager(){
    this(0,true, true, true);
  }

  /**
   * Constructs a grid manager with specified number of grids and grid properties
   * @param numGrids number of grids
   * @param leftRight whether spots are numbered from left to right
   * @param topBottom whether spots are numbered from top to bottom
   * @param spotDirection whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   */
  public GridManager(int numGrids, boolean leftRight, boolean topBottom, boolean spotDirection) {
    this.numGrids = numGrids;
    setLeftRight(leftRight);
    setTopBottom(topBottom);
    setSpotDirection(spotDirection);
    grids = new Grid[numGrids];
    for(int i=0; i<grids.length; i++){
      grids[i] = new Grid();
    }
    if (numGrids > 0) {
    	spots = new Spot[getTotalSpots()];
    }
  }

  /**
   * gets the number of grids
   * @return number of grids
   */
  public int getNumGrids(){
    return numGrids;
  }

  /**
   * gets whether spots are numbered from left to right
   * @return whether spots are numbered from left to right
   */
  public boolean getLeftRight() {
    return leftRight;
  }

  /**
   * gets whether spots are numbered from top to bottom
   * @return whether spots are numbered from top to bottom
   */
  public boolean getTopBottom() {
    return topBottom;
  }

  /**
   * gets whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   * @return whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   */
  public boolean getSpotDirection() {
    return spotDirection;
  }

  /**
   * sets the number of grids
   * @param gridNum number of grids
   */
  public void setGridNum(int gridNum){
    this.numGrids = gridNum;
    Grid[] copygrids = new Grid[grids.length];
    for(int i=0; i<grids.length; i++){
      copygrids[i] = grids[i];
    }
    grids = new Grid[gridNum];
    for(int i=0; i<grids.length; i++){
      if(i<copygrids.length) grids[i] = copygrids[i];
      else grids[i] = new Grid();
    }
    setUpSpots();
  }

  /**
   * sets whether spots are numbered from left to right
   * @param leftRight whether spots are numbered from left to right
   */
  public void setLeftRight(boolean leftRight) {
    this.leftRight = leftRight;
  }

  /**
   * sets whether spots are numbered from top to bottom
   * @param topBottom whether spots are numbered from top to bottom
   */
  public void setTopBottom(boolean topBottom) {
    this.topBottom = topBottom;
    setUpSpots();
  }

  /**
   * sets whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   * @param spotDirection whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   */
  public void setSpotDirection(boolean spotDirection) {
    this.spotDirection = spotDirection;
    setUpSpots();
  }

  /**
   * sets the grids at a specified index
   * @param num index of grid to set
   * @param newGrid new grid to set at specified index
   */
  public void setGrid(int num, Grid newGrid) {
    if(num>=0&&num<grids.length) grids[num] = newGrid;
  }

  /**
   * gets the grid at the specified index and returns null if that grid does not exist
   * @param num index of grid to return
   * @return grid at the specified index and returns null if that grid does not exist
   */
  public Grid getGrid(int num) {
    if(num>=0&&num<grids.length) return grids[num];
    return null;
  }

  /**
   * gets the current grid if it exists
   * @return current grid
   */
  public Grid getCurrentGrid() {
    if(currentGridNum>=0&&currentGridNum<grids.length) return grids[currentGridNum];
    else return null;
  }

  /**
   * sets the current grid number
   * @param currentNum new current grid numbers
   */
  public void setCurrentGrid(int currentNum){
    this.currentGridNum=currentNum;
  }


  /**
   * opens a grid file at the specified file path
   * @param filepath file path to open grid file at
   * @throws Exception when the grid manager cannot open the selected file
   */
  public void openGridManager(File filepath) throws Exception{
     openGridManager(filepath.getAbsolutePath());
  }

  /**
   * opens a grid file at the specified file path
   * @param filepath file path to open grid file at
   * @throws Exception when the grid manager cannot open the selected file
   */
  public void openGridManager(String filepath) throws Exception{
    GridManager gm = new GridManager();

    try{
      BufferedReader in = new BufferedReader(new FileReader(filepath));
      String line;
      String prj = in.readLine();
      if(prj.toLowerCase().indexOf("gridmanager file")==-1) throw new Exception();
      else{
        gm.setGridNum(Integer.parseInt(in.readLine()));
        gm.leftRight=in.readLine().toLowerCase().equals("true");
        gm.topBottom=in.readLine().toLowerCase().equals("true");
        gm.spotDirection=in.readLine().toLowerCase().equals("true");

        in.readLine();


        for(int i=0; (i<gm.numGrids && (line=in.readLine())!=null && line.toLowerCase().indexOf("end*******/")==-1); i++){

          StringTokenizer st = new StringTokenizer(line,"\t");
          Grid tempGrid = new Grid(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
          gm.setGrid(i, tempGrid);
          in.readLine();
        }
      }
      in.close();

      setGridNum(gm.getNumGrids());
      leftRight = gm.leftRight;
      topBottom = gm.topBottom;
      spotDirection = gm.spotDirection;
      for(int i=0; i<this.getNumGrids(); i++){
        grids[i] = gm.grids[i];
      }
      setUpSpots();
    } catch(Exception e){ throw new Exception();}

  }

  /**
   * writes a grid file at the specified file path
   * @param filepath file path to write grid file at
   */
  public void writeGridManager(File filepath){
    this.writeGridManager(filepath.getAbsolutePath());
  }

 /**
   * writes a grid file at the specified file path
   * @param filepath file path to write grid file at
   */
  public void writeGridManager(String filepath){
    try{
	if (!filepath.endsWith(".grid")) filepath+=".grid";
        File f = new File(filepath);
        File parent = f.getParentFile();

	if(!parent.exists()) parent.mkdirs();
	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
	out.println("/****GridManager File");
        out.println(numGrids);
        out.println(leftRight);
        out.println(topBottom);
        out.println(spotDirection);
        out.println("********/");



        for(int i=0; i<numGrids; i++){
          Grid temp = this.getGrid(i);
          out.println("" + temp.getTopLeftX() + "\t" + temp.getTopLeftY() + "\t" + temp.getTopRightX() + "\t" + temp.getTopRightY() + "\t" + temp.getBottomLeftX() + "\t" + temp.getBottomLeftY() + "\t" + temp.getBottomRightX() + "\t" + temp.getBottomRightY() + "\t" + temp.getRows()+ "\t" + temp.getColumns());
          out.println("********/");

        }

        out.println("End********/");
        out.close();
        } catch(IOException e){}
  }

  /**
   * sets the gene list
   * @param gl gene list with all the gene names from the godlist
   */
  public void setGeneList(GeneList gl){
    geneList = gl;
  }

  /**
   * gets the gene list
   * @return gene list with all the gene names from the godlist
   */
  public GeneList getGeneList(){
    return geneList;
  }

  /**
   * gets the name of the current gene
   * @return current gene name
   */
  public String getCurrentGeneName(){
    Point p = this.getCurrentGrid().getCurrentColRow();
    return getGeneName(currentGridNum, p.x, p.y);
  }

  /**
   * gets the total number of spots in all the grids
   * @return total number of spots in all the grids
   */
  public int getTotalSpots(){
    int s = 0;
    for(int i=0; i<grids.length; i++){
      s+=grids[i].getNumOfSpots();
    }
    return s;
  }

  /**
   * gets the number of genes in the gene list
   * @return number of genes in the gene list
   */
  public int getGeneListSize(){
    return geneList.getNumGenes();
  }

  /**
   * gets whether or not all of the grids have been set. 
   * @return whether or not all of the grids have been set
   */
  public boolean gridsSet(){
	for(int i=0; i<grids.length; i++){
      if(!grids[i].isValid()) return false;
    }
    return true;
  }

  /**
   * gets whether or not the segementation phase can begin (all grids set and gene list
   * size matches the total number of spots)
   * @return whether or not the segementation phase can begin
   */
  public boolean isValid(){
    return(gridsSet()&&getTotalSpots()==getGeneListSize());
  }

  /**
   * gets the gene name for the spot at the actual spot number specified by the user
   * @param grid grid number
   * @param spotNum actual spot number
   * @return gene name for the spot at the actual spot number specified by the user
   */
  public String getGeneName(int grid, int spotNum){
    int cols = getGrid(grid).columns;
    return getGeneName(grid, spotNum%cols, spotNum/cols);
  }

  /**
   * gets the gene name for the spot at the actual column and row number specified by the user
   * @param grid grid number
   * @param col actual column number
   * @param row actual row number
   * @return gene name for the spot at the actual column and row number specified by the user
   */
  public String getGeneName(int grid, int col, int row){
    int index=0;
    for(int i=0; i<grid && i<getNumGrids(); i++){
      index += (grids[i].columns*grids[i].rows);
    }

    if(leftRight&&topBottom&&spotDirection){
      index += (row*grids[grid].columns + col);
    }
    else if(!leftRight&&topBottom&&spotDirection){
      index += (row*grids[grid].columns + grids[grid].columns-col-1);
    }
    else if(leftRight&&!topBottom&&spotDirection){
      index += ((grids[grid].rows-row-1)*grids[grid].columns + col);
    }
    else if(!leftRight&&!topBottom&&spotDirection){
      index += ((grids[grid].rows-row-1)*grids[grid].columns + grids[grid].columns-col-1);
    }
    else if(leftRight&&topBottom&&!spotDirection){
      index += (col*grids[grid].rows + row);
    }
    else if(!leftRight&&topBottom&&!spotDirection){
      index += ((grids[grid].columns-col-1)*grids[grid].rows + row);
    }
    else if(leftRight&&!topBottom&&!spotDirection){
      index += (col*grids[grid].rows + grids[grid].rows-row-1);
    }
    else if(!leftRight&&!topBottom&&!spotDirection){
      index += ((grids[grid].columns-col-1)*grids[grid].rows + grids[grid].rows-row-1);
    }

    return geneList.getGene(index);
  }

  /**
   * Finds the location of a gene and returns a point containing the grid number (x) and transformed spot number (y).
   * Null is returned if the gene does not exist.
   * @param name gene name
   * @return point containing the grid number (x) and transformed spot number (y). Null is returned if the gene does not exist.
   */
  public Point findGeneLocation(String name){
    int index=0;
    while(index<geneList.getNumGenes()){
      if(geneList.getGene(index).equalsIgnoreCase(name)) break;
      else index++;
    }
    if(index>=geneList.getNumGenes()) return null;
    
    //g will hold the grid.
    int g=0;
    //Find total genes per grid.
    int GenesPerGrid = geneList.getNumGenes()/this.getNumGrids();
    for(; g<=this.getNumGrids();g++){
    	//Keep comparing to index until we are too big, we are then on the prior grid.
    	if(g*GenesPerGrid>index) {
    		//Set g properly. Substract the number of Genes g times from index.
    		g=g-1;
    		index = index - g*GenesPerGrid;
    		break;
    	}
    }
    return new Point(g, index);
  }

  /**
   * gets the actual spot number from a transformed spot number. An actual spot number is based
   * on left to right, top to bottom, and horizontal (spot 2 to spot 1) placement. A transformed
   * spot number is based on user specified spot placement parameters.
   * @param grid grid number
   * @param spot transformed spot number
   * @return actual spot number
   */
  public int getActualSpotNum(int grid, int spot){

      int row = spot/grids[grid].columns;
      int col = spot%grids[grid].columns;

      if(!spotDirection){
        row = spot%grids[grid].rows;
        col = spot/grids[grid].rows;
      }

      if(leftRight){
        if(topBottom){
          return spot;
        }
        else{
          return ((grids[grid].rows-row-1)*grids[grid].columns + col);
        }
      }
      else{
        if(topBottom){
          return (row*grids[grid].columns + (grids[grid].columns-col-1));
        }
        else{
          return ((grids[grid].rows-row-1)*grids[grid].columns + (grids[grid].columns-col-1));
        }
      }
  }

  /**
   * gets the transformed spot number based on an actual spot number. An actual spot number is based
   * on left to right, top to bottom, and horizontal (spot 2 to spot 1) placement. A transformed
   * spot number is based on user specified spot placement parameters.
   * @param grid grid number
   * @param spot actual spot number
   * @return transformed spot number
   */
  public int getTransformedSpotNum(int grid, int spot){
    return getTransformedSpotNum(grid,spot%grids[grid].columns,spot/grids[grid].columns);
  }

  /**
   * gets the transformed column number based on an actual column number. An actual column number is based
   * on left to right placement. A transformed column number is based on the user specified spot placement parameter.
   * @param grid grid number
   * @param col actual column number
   * @return transformed column number
   */
  public int getTransformedColNum(int grid, int col){
    if(leftRight) return col;
    return grids[grid].columns-col-1;
  }

  /**
   * gets the transformed row number based on an actual row number. An actual row number is based
   * on top to bottom placement. A transformed column number is based on the user specified spot placement parameter.
   * @param grid grid number
   * @param row actual row number
   * @return transformed row number
   */
  public int getTransformedRowNum(int grid, int row){
    if(topBottom) return row;
    return grids[grid].rows-row-1;
  }

/**
   * gets the transformed spot number based on an actual spot number. An actual spot number is based
   * on left to right, top to bottom, and horizontal (spot 2 to spot 1) placement. A transformed
   * spot number is based on user specified spot placement parameters.
   * @param grid grid number
   * @param col actual column number
   * @param row actual row number
   * @return transformed spot number
   */
  public int getTransformedSpotNum(int grid, int col, int row){
    int index=0;

    if(leftRight&&topBottom&&spotDirection){
      index += (row*grids[grid].columns + col);
    }
    else if(!leftRight&&topBottom&&spotDirection){
      index += (row*grids[grid].columns + grids[grid].columns-col-1);
    }
    else if(leftRight&&!topBottom&&spotDirection){
      index += ((grids[grid].rows-row-1)*grids[grid].columns + col);
    }
    else if(!leftRight&&!topBottom&&spotDirection){
      index += ((grids[grid].rows-row-1)*grids[grid].columns + grids[grid].columns-col-1);
    }
    else if(leftRight&&topBottom&&!spotDirection){
      index += (col*grids[grid].rows + row);
    }
    else if(!leftRight&&topBottom&&!spotDirection){
      index += ((grids[grid].columns-col-1)*grids[grid].rows + row);
    }
    else if(leftRight&&!topBottom&&!spotDirection){
      index += (col*grids[grid].rows + grids[grid].rows-row-1);
    }
    else if(!leftRight&&!topBottom&&!spotDirection){
      index += ((grids[grid].columns-col-1)*grids[grid].rows + grids[grid].rows-row-1);
    }

    return index;
  }

  /**
   * gets the grid number at a given point and return -1 if no grid contains the given point
   * @param x x-coordinate
   * @param y y-coordinate
   * @return grid number at a given point and return -1 if no grid contains the given point
   */
  public int getGridAtPoint(int x, int y){
    for(int i=0; i<grids.length; i++){
      if(grids[i].getTranslatedPolygon()!=null && grids[i].getTranslatedPolygon().contains(x,y)) return i;
    }
    return -1;
  }
  
  public Point[] getGridCorners(int x, int y) {
	  int currGridNum = getGridAtPoint(x,y);
	  Grid currGrid = getGrid(currGridNum);
	  Point colRow = currGrid.getColRow(x,y);
	  int transSpotNum = getTransformedSpotNum(currGridNum, colRow.x, colRow.y);
	  //int actualSpotNum = getActualSpotNum(currGridNum, transSpotNum);
	  return getGridCornersFromTransformedSpotNumber(currGridNum, transSpotNum);
  }
  
  public Point[] getGridCornersFromTransformedSpotNumber(int currGridNum, int transSpotNum) {
	  Grid currGrid = getGrid(currGridNum);
	  Point[] retVal = new Point[4];
	  Polygon translatedPolygon = currGrid.getTranslatedPolygon();
	  boolean found = false;
	  //double currX = 0.0;
	  //double currY = 0.0;
	  double origX = translatedPolygon.xpoints[0];
	  double origY = translatedPolygon.ypoints[0];
	  double currX = origX;
	  double currY = origY;
	  double dydown = (double)(translatedPolygon.ypoints[3]-(translatedPolygon.ypoints[0]))/(double)currGrid.getRows();
	  double dxdown = (double)(translatedPolygon.xpoints[3]-(translatedPolygon.xpoints[0]))/(double)currGrid.getRows();
      double dyright = (double)(translatedPolygon.ypoints[1]-(translatedPolygon.ypoints[0]))/(double)currGrid.getColumns();
	  double dxright = (double)(translatedPolygon.xpoints[1]-(translatedPolygon.xpoints[0]))/(double)currGrid.getColumns();
	  //each time we go down, add dydown to y and dxdown to x
	  //each time we go right, add dyright to y and dxright to x
	  for (int currRow = 0; (currRow < currGrid.getRows() && !found) ; currRow++) {
		  for (int currCol = 0; (currCol < currGrid.getColumns() && !found); currCol++){
			  Point currColRow = currGrid.getColRow((int)currX+3, (int)currY+3); //move us into the grid square just a tiny bit and find the colrow
			  /*System.out.print("Got colrow at ");
			  System.out.print((int)currX+1);
			  System.out.print(", ");
			  System.out.print((int)currY+1);
			  System.out.print(" which was ");
			  System.out.print(currColRow.x);
			  System.out.print(".");
			  System.out.print(currColRow.y);
			  System.out.print("\n");*/
			  if (currColRow != null) {
				  int currTransSpotNum = getTransformedSpotNum(currGridNum, currColRow.x, currColRow.y);
				  if (currTransSpotNum == transSpotNum)//we found it!
				  {
					  found = true;
					  retVal[0] = new Point((int)currX, (int)currY); //top left
					  retVal[1] = new Point((int)(currX+dxright), (int)(currY+dyright)); //top right
					  retVal[2] = new Point((int)(currX+dxright+dxdown),(int)(currY+dyright+dydown)); //bottom right
					  retVal[3] = new Point((int)(currX+dxdown), (int)(currY+dydown)); //bottom left
				  }
				  else {	//we didn't find it
					  //last step - go to the next col
					  currY += dyright;
					  currX += dxright;
				  }
			  }
			  else {	//we didn't find it
				  //last step - go to the next col
				  currY += dyright;
				  currX += dxright;
			  }
		  }

		  if (!found) {	//we still haven't found it
			  //last step: go to the next row
			  currY = (origY)+(dydown)*(currRow+1);	//go to the next row in y-coords
			  currX = (origX)+(dxdown)*(currRow+1);	//go all the way back to the beginning, then go down currRow+1 rows
		  }
		  
	  }
	if (!found) retVal = null;
	return retVal;
  }
  
  /**
   * Returns the number of spots on each grid
   * Deprecated. DO NOT USE!
   * @return number of spots on each grid
   */
  @Deprecated
  public int getNumSpotsPerGrid() {
	  return getTotalSpots()/getNumGrids();
  }
  
  public Spot[] getSpots() {
	  if ((spots == null) || (spots.length < getTotalSpots())) setUpSpots();
	  return spots;
  }
  
  public void setUpSpots(){
	  if ( (getNumGrids() > 0)) spots = new Spot[getTotalSpots()];
  }
}