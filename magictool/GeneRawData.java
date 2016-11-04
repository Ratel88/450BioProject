/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003-2006  Laurie Heyer
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
 *   Dept. of Mathematics, Davidson College
 *   PO Box 6959
 *   Davidson, NC 28035
 */

package magictool;

import magictool.image.*;

/**
 * GeneRawData is a class which holds the data from a raw data file for a spot in a microarray
 */
public class GeneRawData {

  /**gene name*/
  protected String name;
  /**red foreground total*/
  protected int redfg;
  /**red background total*/
  protected int redbg;
  /**green foreground total*/
  protected int greenfg;
  /**green background total*/
  protected int greenbg;

  /**red foreground average*/
  protected double redfgavg;
  /**red background average*/
  protected double redbgavg;
  /**green foreground average*/
  protected double greenfgavg;
  /**green background average*/
  protected double greenbgavg;

  /**Null constructor
   */
  public GeneRawData() {
  }

  /**
   * Constructs an instance of the GeneData class containing given data
   * @param name gene name
   * @param redfg red foreground total
   * @param redbg red background total
   * @param greenfg green foreground total
   * @param greenbg green background total
   * @param redfgavg red foreground avg
   * @param redbgavg red background avg
   * @param greenfgavg green foreground avg
   * @param greenbgavg green background avg
   */
  public GeneRawData(String name, int redfg, int redbg, int greenfg, int greenbg, double redfgavg, double redbgavg, double greenfgavg, double greenbgavg) {
    this.name=name;
	this.redfg=redfg;
    this.greenfg=greenfg;
    this.redbg=redbg;
    this.greenbg=greenbg;
    this.redfgavg=redfgavg;
    this.greenfgavg=greenfgavg;
    this.redbgavg=redbgavg;
    this.greenbgavg=greenbgavg;
  }

  /**
   * sets the gene name
   * @param newName new gene name
   */
  public void setName(String newName){
	  this.name=newName;
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
   * @param redfgavg red foreground average
   */
  public void setRedForegroundAverage(double redfgavg){
    this.redfgavg=redfgavg;
  }

  /**
   * sets the red background total
   * @param redbg red background total
   */
  public void setRedBackgroundTotal(int redbg){
    this.redbg=redbg;
  }

  /**
   * sets the red background average
   * @param redbgavg red background average
   */
  public void setRedBackgroundAverage(double redbgavg){
    this.redbgavg=redbgavg;
  }

  /**
   * sets the green foreground total
   * @param greenfg green foreground total
   */
  public void setGreenForegroundTotal(int greenfg){
    this.greenfg=greenfg;
  }

  /**
   * sets the green foreground average
   * @param greenfgavg green foreground average
   */
  public void setGreenForegroundAverage(double greenfgavg){
    this.greenfgavg=greenfgavg;
  }

  /**
   * sets the green background total
   * @param greenbg green background total
   */
  public void setGreenBackgroundTotal(int greenbg){
    this.greenbg=greenbg;
  }

  /**
   * sets the green background average
   * @param greenbgavg green background average
   */
  public void setGreenBackgroundTotalSpots(double greenbgavg){
    this.greenbgavg=greenbgavg;
  }

  /**
   * gets name of gene
   * @return name of gene
   */
  public String getName(){
	  return name;
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
    return redfgavg;
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
    return redbgavg;
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
    return greenfgavg;
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
    return greenbgavg;
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