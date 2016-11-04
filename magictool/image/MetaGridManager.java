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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * GridManager is a class which holds all of the grids for a microarray images. It also
 * holds the gene list as well as information about the number of grids and the placement
 * of spots within grids. GridManager manages changes within all of the grids and ensures
 * that all grids have been specified before segmentation.
 */
public class MetaGridManager {

  /**number of metagrids*/
  protected int numMetaGrids;
  /**whether grids are numbered from left to right*/
  protected boolean leftRight;
  /**whether grids are numbered from top to bottom*/
  protected boolean topBottom;
  /**whether grid 2 is horizontal (true) or vertical (false) in relationship to grid 1*/
  protected boolean gridDirection;
  /**array of grids fro microarray image*/
  protected MetaGrid[] metaGrids;
  /**current metaGrid number*/
  protected int currentMetaGridNum = -1;
  

  /**
   * Constructs an empty meta grid manager with no grids.
   */
  public MetaGridManager(){
    this(0,true, true, true);
  }

  /**
   * Constructs a grid manager with specified number of meta grids and metagrid properties
   * @param numMetaGrids number of meta grids
   * @param leftRight whether spots are numbered from left to right
   * @param topBottom whether spots are numbered from top to bottom
   * @param spotDirection whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   */
  public MetaGridManager(int numMetaGrids, boolean leftRight, boolean topBottom, boolean gridDirection) {
    this.numMetaGrids = numMetaGrids;
    setLeftRight(leftRight);
    setTopBottom(topBottom);
    setGridDirection(gridDirection);
    metaGrids = new MetaGrid[numMetaGrids];
    for(int i=0; i<metaGrids.length; i++){
      metaGrids[i] = new MetaGrid();
    }
  }

  /**
   * gets the number of grids
   * @return number of grids
   */
  public int getMetaGridNum(){
    return numMetaGrids;
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
  public boolean getGridDirection() {
    return gridDirection;
  }

  /**
   * sets the number of grids
   * @param gridNum number of grids
   */
  public void setMetaGridNum(int metaGridNum){
    this.numMetaGrids = metaGridNum;
    MetaGrid[] copygrids = new MetaGrid[metaGrids.length];
    for(int i=0; i<metaGrids.length; i++){
      copygrids[i] = metaGrids[i];
    }
    metaGrids = new MetaGrid[metaGridNum];
    for(int i=0; i<metaGrids.length; i++){
      if(i<copygrids.length) metaGrids[i] = copygrids[i];
      else metaGrids[i] = new MetaGrid();
    }
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
  }

  /**
   * sets whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   * @param spotDirection whether spot 2 is horizontal (true) or vertical (false) in relationship to spot 1
   */
  public void setGridDirection(boolean gridDirection) {
    this.gridDirection = gridDirection;
  }

  /**
   * sets the grids at a specified index
   * @param num index of grid to set
   * @param newGrid new grid to set at specified index
   */
  public void setMetaGrid(int num, MetaGrid newMetaGrid) {
    if(num>=0&&num<metaGrids.length) metaGrids[num] = newMetaGrid;
  }

  /**
   * gets the grid at the specified index and returns null if that grid does not exist
   * @param num index of grid to return
   * @return grid at the specified index and returns null if that grid does not exist
   */
  public MetaGrid getMetaGrid(int num) {
    if(num>=0&&num<metaGrids.length) return metaGrids[num];
    return null;
  }

  /**
   * gets the current grid if it exists
   * @return current grid
   */
  public MetaGrid getCurrentMetaGrid() {
    if(currentMetaGridNum>=0&&currentMetaGridNum<metaGrids.length) return metaGrids[currentMetaGridNum];
    else return null;
  }

  /**
   * sets the current grid number
   * @param currentNum new current grid numbers
   */
  public void setCurrentMetaGrid(int currentNum){
    this.currentMetaGridNum=currentNum;
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
    MetaGridManager gm = new MetaGridManager();

    try{
      BufferedReader in = new BufferedReader(new FileReader(filepath));
      String line;
      String prj = in.readLine();
      if(prj.toLowerCase().indexOf("gridmanager file")==-1) throw new Exception();
      else{
        gm.setMetaGridNum(Integer.parseInt(in.readLine()));
        gm.leftRight=in.readLine().toLowerCase().equals("true");
        gm.topBottom=in.readLine().toLowerCase().equals("true");
        gm.gridDirection=in.readLine().toLowerCase().equals("true");

        in.readLine();


        for(int i=0; (i<gm.numMetaGrids && (line=in.readLine())!=null && line.toLowerCase().indexOf("end*******/")==-1); i++){

          StringTokenizer st = new StringTokenizer(line,"\t");
          MetaGrid tempGrid = new MetaGrid(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
          gm.setMetaGrid(i, tempGrid);
          in.readLine();
        }
      }
      in.close();

      setMetaGridNum(gm.getMetaGridNum());
      leftRight = gm.leftRight;
      topBottom = gm.topBottom;
      gridDirection = gm.gridDirection;
      for(int i=0; i<this.getMetaGridNum(); i++){
        metaGrids[i] = gm.metaGrids[i];
      }
    } catch(Exception e){ throw new Exception();}

  }

  /**
   * writes a grid file at the specified file path
   * @param filepath file path to write grid file at
   */
  public void writeGridManager(File filepath){
  //  this.writeGridManager(filepath.getAbsolutePath());
  }

// /**
//   * writes a grid file at the specified file path
//   * @param filepath file path to write grid file at
//   */
//  public void writeGridManager(String filepath){
//    try{
//	if (!filepath.endsWith(".grid")) filepath+=".grid";
//        File f = new File(filepath);
//        File parent = f.getParentFile();
//
//	if(!parent.exists()) parent.mkdirs();
//	PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(f)));
//	out.println("/****GridManager File");
//        out.println(numGrids);
//        out.println(leftRight);
//        out.println(topBottom);
//        out.println(spotDirection);
//        out.println("********/");
//
//
//
//        for(int i=0; i<numGrids; i++){
//          Grid temp = this.getGrid(i);
//          out.println("" + temp.getTopLeftX() + "\t" + temp.getTopLeftY() + "\t" + temp.getTopRightX() + "\t" + temp.getTopRightY() + "\t" + temp.getBottomLeftX() + "\t" + temp.getBottomLeftY() + "\t" + temp.getBottomRightX() + "\t" + temp.getBottomRightY() + "\t" + temp.getRows()+ "\t" + temp.getColumns());
//          out.println("********/");
//
//        }
//
//        out.println("End********/");
//        out.close();
//        } catch(IOException e){}
//  }

  /**
   * gets the total number of grids in all the metagrids
   * @return total number of grids in all the metagrids
   */
  public int getTotalGrids(){
    int s = 0;
    for(int i=0; i<metaGrids.length; i++){
      s+=metaGrids[i].getNumOfGrids();
    }
    return s;
  }


  /**
   * gets whether or not all of the grids have been set
   * @return whether or not all of the grids have been set
   */
  public boolean gridsSet(){
    for(int i=0; i<metaGrids.length; i++){
      if(!metaGrids[i].isValid()) return false;
    }
    return true;
  }

  /**
   * gets whether or not the segementation phase can begin (all grids set and gene list
   * size matches the total number of spots)
   * @return whether or not the segementation phase can begin
   */
  public boolean isValid(){
   //check here!!!!! return(gridsSet()&&getTotalSpots()==getGeneListSize());
	  return true;
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
    return metaGrids[grid].columns-col-1;
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
    return metaGrids[grid].rows-row-1;
  }



  /**
   * gets the grid number at a given point and return -1 if no grid contains the given point
   * @param x x-coordinate
   * @param y y-coordinate
   * @return grid number at a given point and return -1 if no grid contains the given point
   */
  public int getGridAtPoint(int x, int y){
    //for(int i=0; i<metaGrids.length; i++){
      //if(metaGrids[i].getTranslatedPolygon()!=null && metaGrids[i].getTranslatedPolygon().contains(x,y)) return i;
    //}
    return -1;
  }
}