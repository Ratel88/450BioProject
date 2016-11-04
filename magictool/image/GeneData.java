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
 * GeneData is a class which holds the data for a spot in a microarray
 */
public class GeneData {

  /**red foreground total*/
  protected int redfg;
  /**red background total*/
  protected int redbg;
  /**green foreground total*/
  protected int greenfg;
  /**green background total*/
  protected int greenbg;

  /**red foreground total spots*/
  protected int redfgnum;
  /**red background total spots*/
  protected int redbgnum;
  /**green foreground total spots*/
  protected int greenfgnum;
  /**green background total spots*/
  protected int greenbgnum;

  /**Null constructor
   */
  public GeneData() {
  }

  /**
   * Constructs an instance of the GeneData class containing given data
   * @param redfg red foreground total
   * @param redbg red background total
   * @param greenfg green foreground total
   * @param greenbg green background total
   * @param redfgnum red foreground total spots
   * @param redbgnum red background total spots
   * @param greenfgnum green foreground total spots
   * @param greenbgnum green background total spots
   */
  public GeneData(int redfg, int redbg, int greenfg, int greenbg,int redfgnum, int redbgnum, int greenfgnum, int greenbgnum) {
    this.redfg=redfg;
    this.greenfg=greenfg;
    this.redbg=redbg;
    this.greenbg=greenbg;
    this.redfgnum=redfgnum;
    this.greenfgnum=greenfgnum;
    this.redbgnum=redbgnum;
    this.greenbgnum=greenbgnum;
  }

  /**
   * sets the red foreground total
   * @param redfg red foreground total
   */
  public void setRedForegroundTotal(int redfg){
    this.redfg=redfg;
  }

  /**
   * sets the red foreground total spots
   * @param redfgnum red foreground total spots
   */
  public void setRedForegroundTotalSpots(int redfgnum){
    this.redfgnum=redfgnum;
  }

  /**
   * sets the red background total
   * @param redbg red background total
   */
  public void setRedBackgroundTotal(int redbg){
    this.redbg=redbg;
  }

  /**
   * sets the red background total spots
   * @param redbgnum red background total spots
   */
  public void setRedBackgroundTotalSpots(int redbgnum){
    this.redbgnum=redbgnum;
  }

  /**
   * sets the green foreground total
   * @param greenfg green foreground total
   */
  public void setGreenForegroundTotal(int greenfg){
    this.greenfg=greenfg;
  }

  /**
   * sets the green foreground total spots
   * @param greenfgnum green foreground total spots
   */
  public void setGreenForegroundTotalSpots(int greenfgnum){
    this.greenfgnum=greenfgnum;
  }

  /**
   * sets the green background total
   * @param greenbg green background total
   */
  public void setGreenBackgroundTotal(int greenbg){
    this.greenbg=greenbg;
  }

  /**
   * sets the green background total spots
   * @param greenbgnum green background total spots
   */
  public void setGreenBackgroundTotalSpots(int greenbgnum){
    this.greenbgnum=greenbgnum;
  }

  /**
   * gets the red foreground total
   * @return red foreground total
   */
  public int getRedForegroundTotal(){
    return redfg;
  }

/**
   * gets the red foreground average
   * @return red foreground average
   */
  public double getRedForegroundAvg(){
	  return ((double)redfg)/redfgnum;
  }

  /**
   * gets the red background total
   * @return red background total
   */
  public int getRedBackgroundTotal(){
    return redbg;
  }

  /**
   * gets the red background average
   * @return red background average
   */
  public double getRedBackgroundAvg(){
    return ((double)redbg)/redbgnum;
  }

  /**
   * gets the green foreground total
   * @return green foreground total
   */
  public int getGreenForegroundTotal(){
    return greenfg;
  }

  /**
   * gets the green foreground average
   * @return green foreground average
   */
  public double getGreenForegroundAvg(){
    return ((double)greenfg)/greenfgnum;
  }

  /**
   * gets the green background total
   * @return green background total
   */
  public int getGreenBackgroundTotal(){
    return greenbg;
  }

  /**
   * gets the green background average
   * @return green background average
   */
  public double getGreenBackgroundAvg(){
    return ((double)greenbg)/greenbgnum;
  }

  /**
   * returns the ratio using the given method
   * @param ratioMethod method to calculate ratio
   * @return ratio
   */
  public double getRatio(int ratioMethod){
    if (greenfg==0) return 999;		//XXX GeneData: this is where the 999 expression ratio is set
    double ratio = getRedForegroundAvg()/getGreenForegroundAvg();
    if(ratioMethod==SingleGeneImage.AVG_SUBTRACT_BG){
      if(getGreenForegroundAvg()-getGreenBackgroundAvg()<=0){
        if(getRedForegroundAvg()-getRedBackgroundAvg()<=0) return 998;
        return 999;
      }
      ratio = (Math.max(getRedForegroundAvg()-getRedBackgroundAvg(),0))/(Math.max(getGreenForegroundAvg()-getGreenBackgroundAvg(),0));
    }
    else if(ratioMethod==SingleGeneImage.TOTAL_SUBTRACT_BG){
      if(getGreenForegroundTotal()-getGreenBackgroundTotal()<=0){
        if(getRedForegroundTotal()-getRedBackgroundTotal()<=0) return 998;
        return 999;
      }
      ratio = ((double)(Math.max(getRedForegroundTotal()-getRedBackgroundTotal(),0)))/(Math.max(getGreenForegroundTotal()-getGreenBackgroundTotal(),0));
    }
    else if(ratioMethod==SingleGeneImage.TOTAL_SIGNAL) ratio=((double)getRedForegroundTotal())/getGreenForegroundTotal();
    return ratio;
  }
}