/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003-2007  Laurie Heyer
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
 *   Davidson, NC 28035-6959
 *   UNITED STATES
 */

package magictool;


/**
 * Gene is a class which holds all the characteristics of a gene
 */
public class Gene {

  /**name of the gene*/
  protected String name=null;
  /**name of gene's chromosome*/
  protected String chromo=null;
  /**molecular function of the gene*/
  protected String function=null;
  /**gene alias*/
  protected String alias=null;
  /**location of the gene in the chromosome*/
  protected String location=null;
  /**biological process of the gene*/
  protected String process=null;
  /**cellular component of the gene*/
  protected String component=null;
  /**any comments*/
  protected String comments=null;
  /**gene data*/
  protected double[] data;
  /**standard deviation of gene data*/
  protected double stddev;
  /**average of gene data*/
  protected double avg;
  /** Grid number */
  protected int grid;
  /** Grid Row */
  protected int row;
  /** Grid Column*/
  protected int col;
  
  /**
   * Default gene constructor - everything is null
   */
  public Gene() {}

  /**
   * Construct a gene with given name and data
   * @param name gene name
   * @param data gene data
   */
  public Gene(String name, double[] data) {
    this.name=name;
    this.data=data;
  }


  /**
   * Construct a gene with given name, chromosome, and data
   * @param name gene name
   * @param data gene data
   * @param chromo gene's chromosome
   */
  public Gene(String name, double[] data, String chromo) {
    this.name=name;
    this.data=data;
    this.chromo=chromo;
  }

  /**
   * Construct a gene with given name, chromosome, function and data
   * @param name gene name
   * @param data gene data
   * @param chromo gene's chromosome
   * @param location location of gene in chromosome
   */
  public Gene(String name, double[] data, String chromo, String location) {
    this.name=name;
    this.data=data;
    this.chromo=chromo;
    this.location=location;
  }

  /**
   * Construct a gene with given name, chromosome, function and data
   * @param name gene name
   * @param data gene data
   * @param chromo gene's chromosome
   * @param location location of gene in chromosome
   * @param alias gene alias
   * @param process biological process of the gene
   * @param function molecular function of the gene
   * @param component cellular component of the gene
   */
  public Gene(String name, double[] data, String chromo, String location, String alias, String process, String function, String component) {
    this.name=name;
    this.data=data;
    this.chromo=chromo;
    this.location=location;
    this.alias=alias;
    this.process=process;
    this.function=function;
    this.component=component;
  }

  /**
   * Copy constructor for Gene
   * @param oldG Gene to copy
   */
  public Gene(Gene oldG) {
	  this.name=oldG.name;
	  this.chromo=oldG.chromo;
	  this.function=oldG.function;
	  this.alias=oldG.alias;
	  this.location=oldG.location;
	  this.process=oldG.process;
	  this.component=oldG.component;
	  this.comments=oldG.comments;
	  this.data = oldG.data;
	  this.stddev = oldG.stddev;
	  this.avg = oldG.avg;
	  this.grid = oldG.grid;
	  this.row = oldG.row;
	  this.col = oldG.col;
  }

  /**
   * sets the name of the gene
   * @param name gene name
   */
  public void setName(String name){
    this.name=name;
  }

  /**
   * sets the data for the gene
   * @param data gene data
   */
  public void setData(double data[]){
    this.data=data;
  }

  /**
   * returns the maximum value of the gene's data
   * @return maximum value of the gene's data
   */
  public double getMaxValue(){
    int pos=0;
    while((data[pos]==Double.POSITIVE_INFINITY||data[pos]==Double.NEGATIVE_INFINITY||data[pos]==Double.NaN)&&pos<data.length) pos++;
    double max;
    if(pos<data.length) max = data[pos];
    else max = Double.POSITIVE_INFINITY;
    for(int i=pos+1; i<data.length; i++){
      if(data[i]!=Double.POSITIVE_INFINITY&&data[i]!=Double.NEGATIVE_INFINITY&&data[i]!=Double.NaN){
        if(data[i]>max) max=data[i];
      }
    }
    return max;
  }

  /**
   * returns the minimum value of the gene's data
   * @return minimum value of the gene's data
   */
  public double getMinValue(){
    int pos=0;
    while((data[pos]==Double.POSITIVE_INFINITY||data[pos]==Double.NEGATIVE_INFINITY||data[pos]==Double.NaN)&&pos<data.length) pos++;
    double min;
    if(pos<data.length) min = data[pos];
    else min = Double.POSITIVE_INFINITY;
    for(int i=pos+1; i<data.length; i++){
      if(data[i]!=Double.POSITIVE_INFINITY&&data[i]!=Double.NEGATIVE_INFINITY&&data[i]!=Double.NaN){
        if(data[i]<min) min=data[i];
      }
    }
    return min;
  }

  /**
   * sets the molecular function of the gene
   * @param function molecular function of the gene
   */
  public void setFunction(String function){
    this.function=function;
  }

  /**
   * sets the chromosome of the gene
   * @param chromo chromosome of the gene
   */
  public void setChromo(String chromo){
    this.chromo=chromo;
  }

  /**
   * returns the gene name
   * @return gene name
   */
  public String getName(){
    return name;
  }

  /**
   * returns the molecular function of the gene
   * @return gene molecular function
   */
  public String getFunction(){
    return function;
  }
  
  /**
   * Returns the function without " characters.
   */
  public String getFunctionBasic(){
	  if(function!=null){
		  return function.replace("\"", "");
	  }else{
		  return "";
	  }
	 
  }

  /**
   * Returns the prcess without " characters.
   */
  public String getProcessBasic(){
	  if(process!=null){
		  return process.replace("\"", "");
	  }else{
		  return ""; 
	  }
  }

  /**
   * returns the chromosome of the gene
   * @return gene's chromosome
   */
  public String getChromo(){
    return chromo;
  }

  /**
   * returns the gene data
   * @return gene data
   */
  public double[] getData(){
    return data;
  }

  /**
   * returns the data value for a given column
   * @param col column numbers
   * @return data value for a given column
   */
  public double getDataPoint(int col){
    if(col<=data.length&&col>=0) return data[col];
    else return Double.POSITIVE_INFINITY;
  }

  /**
   * returns the standard deviation of the gene data
   * @return standard deviation of the gene data
   */
  public double getSD(){
    return stddev;
  }

  /**
   * returns the average of the gene data
   * @return average of the gene data
   */
  public double getAvg(){
    return avg;
  }

  /**
   * sets the standard deviation of the gene data
   * @param stddev standard deviation of the gene data
   */
  public void setSD(double stddev){
    this.stddev=stddev;
  }

  /**
   * sets the average of the gene data
   * @param avg average of the gene data
   */
  public void setAvg(double avg){
    this.avg=avg;
  }

  /**
   * sets comments about the gene
   * @param comments comments about the gene
   */
  public void setComments(String comments){
    this.comments=comments;
  }

  /**
   * returns any comments about the gene
   * @return comments about the gene
   */
  public String getComments(){
     return comments;
  }

  /**
   * sets the gene alias
   * @param alias gene alias
   */
  public void setAlias(String alias){
    this.alias=alias;
  }

  /**
   * returns the gene alias
   * @return gene alias
   */
  public String getAlias(){
     return alias;
  }

  /**
   * sets the location of the gene in the chromosome
   * @param location location of the gene in the chromosome
   */
  public void setLocation(String location){
    this.location=location;
  }

  /**
   * returns the location of the gene in the chromosome
   * @return location of the gene in the chromosome
   */
  public String getLocation(){
     return location;
  }

  /**
   * sets the biological process of the gene
   * @param process biological process of the gene
   */
  public void setProcess(String process){
    this.process=process;
  }

  /**
   * returns the biological process of the gene
   * @return biological process of the gene
   */
  public String getProcess(){
     return process;
  }

  /**
   * sets the cellular component of the gene
   * @param component cellular component of the gene
   */
  public void setComponent(String component){
    this.component=component;
  }

  /**
   * returns the cellular component of the gene
   * @return cellular component of the gene
   */
  public String getComponent(){
     return component;
  }
  
  /**
   * Returns the Component without the " character.
   */
  public String getComponentBasic(){
	     if(component!=null){
	    	 return component.replace("\"", "");
	     }else{
	    	 return "";
	     }
	  }
	  
  
  public void setPosition(int gr, int r, int c) {
	  grid = gr;
	  row = r;
	  col = c;
  }
  
  public int[] getPosition() {
	  int[] retVal = new int[3];
	  retVal[0] = grid;
	  retVal[1] = row;
	  retVal[2] = col;
	  return retVal;
  }
  
  public int getGridNum() {
	  return grid;
  }
  
  public int getRowNum() {
	  return row;
  }
  
  public int getColNum() {
	  return col;
  }

}