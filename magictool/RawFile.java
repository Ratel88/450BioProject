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
 *   Dept. of Mathematics
 *   PO Box 6959
 *   Davidson, NC 28035
 *   USA
 */

/*RawFile developed by Michael Gordon Sept-Oct 2006*/

package magictool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * Reads and stores raw data for comparison later.
 * If raw data is detected, this should be called to have access
 * to comparison methods for raw data.
 * It is crucial to automatic data flagging. 
*/
public class RawFile {
	
	/**vector of the each gene's raw data*/
	Vector<GeneRawData> geneVector = new Vector<GeneRawData>();
	/**use this to make sure we have a good RawFile*/
	boolean validity = true;
	double minValue = Double.NEGATIVE_INFINITY;
	double maxValue = Double.POSITIVE_INFINITY;
	
	/**
	 * Main Constructor for a RawFile
	 * @param myRawFile filename of the raw data file
	 */
	@SuppressWarnings("unchecked")
	public RawFile(File myRawFile){
		String line;
		try {
			FileReader filereader = new FileReader(myRawFile);
			BufferedReader bufferedreader = new BufferedReader(filereader);
			//first, we need to get rid of the header line - don't need it, don't want it, don't care what it is
			line = bufferedreader.readLine();
			while ((line = bufferedreader.readLine()) != null) {	//get each line
				line.trim(); //not 100% sure what that does, but it seemed like a good idea at the time.
				String name = null;
				int redfgtot, redbgtot, grnfgtot, grnbgtot;
				double redfgavg, redbgavg, greenfgavg, greenbgavg;
				Vector tokens = createTokens(line, 9, false);
				name = (String)tokens.elementAt(0);
				try{
					redfgtot = new Integer(Integer.parseInt((String)tokens.elementAt(1)));
					redbgtot = new Integer(Integer.parseInt((String)tokens.elementAt(2)));
					grnfgtot = new Integer(Integer.parseInt((String)tokens.elementAt(3)));
					grnbgtot = new Integer(Integer.parseInt((String)tokens.elementAt(4)));
					redfgavg = new Double(Double.parseDouble((String)tokens.elementAt(5)));
					redbgavg = new Double(Double.parseDouble((String)tokens.elementAt(6)));
					greenfgavg = new Double(Double.parseDouble((String)tokens.elementAt(7)));
					greenbgavg = new Double(Double.parseDouble((String)tokens.elementAt(8)));
				}
				catch(NumberFormatException e)
				{
					validity = false;
					System.out.println("Unable to parse number properly.");
					break;
				}
				GeneRawData currGeneData = new GeneRawData(name, redfgtot, redbgtot, grnfgtot, grnbgtot, redfgavg, redbgavg, greenfgavg, greenbgavg);
				geneVector.add(currGeneData); //slam that current gene in that vector!
			}
		} catch (Exception e) {
			validity = false;
			System.out.println("reading raw file failed");
		}
	}
	/**
	  * Creates a vector of as many tab-delimited tokens as exist and appends as many blank tokens
	  * as necessary to return at least total number of tokens - blank tokens are stored as empty strings.
      * Method will skip empty tokens at the start of the string is skipFirstBlanks is true.
	  * @param string full string to be broken into tab-delimited tokens
	  * @param total minimum number of tokens to return
	  * @param skipFirstBlanks whether to skip blank tokens at the beginning of the string
	  * @return a vector of as many tab-delimited tokens as exist and appends as many blank tokens as necessary to return at least total number of tokens
	  */
	@SuppressWarnings("unchecked")
	  public Vector createTokens(String string, int total, boolean skipFirstBlanks){
	     StringTokenizer st = new StringTokenizer(string,"\t",true);
	     Vector tokens = new Vector();
	     boolean next=true;
	     boolean first=true;
	     while(st.hasMoreTokens()){
	        if(next){
	           String s = st.nextToken();
	           if(s.equals("\t")){
	              if(!skipFirstBlanks||!first)tokens.addElement(new String(""));
	              next=true;
	           } else {
	              tokens.addElement(s);
	              next=false;
	           }
	            if(first&&tokens.size()>0) first=false;
	        } else{
	           st.nextToken();
	           next=true;
	        }
	     }
	     while(tokens.size()<total) tokens.addElement(new String(""));
	      return tokens;
	  }
	  
	  /**
	   * returns whether or not the raw data file is valid
	   * @return whether or not the raw data file is valid
	   */
	  public boolean isValid() {
		  return validity;
	  }
	  
	  /**
	   * find the location of a gene in the raw data
	   * @param name name of the gene
	   * @return location of the gene (if found, else -1)
	   */
	  public int findGeneName(String name) {
		  for (int i=0; i<geneVector.size(); i++)
		  {
			  if (geneVector.elementAt(i).getName() == name) return i;
		  }
		  return -1;
	  }
	  
	  /**
	   * gets the GeneRawData for the specified gene number
	   * @param num gene index number (starting at zero; it may be useful to use findGeneName first
	   * @return GeneRawData for specified gene
	   */
	  public GeneRawData getGeneRawData(int num) {
		  return geneVector.elementAt(num);
	  }
	  
	  /**
	   * gets the Vector<GeneRawData> for all the genes in the raw data file
	   * @return vector of type GeneRawData for all genes in the raw data file
	   */
	  public Vector<GeneRawData> getAllRawData()
	  {
		  return geneVector;
	  }
	  
	  /**
	   * gets the average of the total red foreground values
	   * @return the average of the total red foreground values 
	   */
	  public double getAvgTotalRedFG() {
		  double mean = 0;
		  int n = 0;
		  for(GeneRawData i : geneVector) {
			  int x = i.getRedForegroundTotal();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
		  }
		  return mean;
	  }
	  
	  /**
	   * gets the average of the total red background values
	   * @return the average of the total red background values
	   */
	  public double getAvgTotalRedBG() {
		  double mean = 0;
		  int n = 0;
		  for(GeneRawData i : geneVector) {
			  int x = i.getRedBackgroundTotal();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
		  }
		  return mean;
		  
	  }
	  
	  /**
	   * gets the average of the total green foreground values
	   * @return the average of the total green foreground values
	   */
	  public double getAvgTotalGreenFG() {
		  double mean = 0;
		  int n = 0;
		  for(GeneRawData i : geneVector) {
			  int x = i.getGreenForegroundTotal();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
		  }
		  return mean;
	  }
	  
	  /**
	   * gets the average of the total green background values
	   * @return the average of the total green background values
	   */
	  public double getAvgTotalGreenBG() {
		  double mean = 0;
		  int n = 0;
		  for(GeneRawData i : geneVector) {
			  int x = i.getGreenBackgroundTotal();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
		  }
		  return mean;
	  }
	  
	  /**
	   * gets the average of the average red foreground values
	   * @return average of the average red foreground values
	   */
	  public double getAvgAvgRedFG()
	  {
		  double retVal = 0;
		  for (int i = 0; i < geneVector.size(); i++)
		  {
			  retVal += geneVector.elementAt(i).getRedForegroundAvg();
		  }
		  retVal = retVal / ((geneVector.size())+1);
		  return retVal;
	  }
	  /**
	   * gets the average of the average red background values
	   * @return average of the average red background values
	   */
	  public double getAvgAvgRedBG()
	  {
		  double retVal = 0;
		  for (int i = 0; i < geneVector.size(); i++)
		  {
			  retVal += geneVector.elementAt(i).getRedBackgroundAvg();
		  }
		  retVal = retVal / ((geneVector.size())+1);
		  return retVal;
	  }
	  
	  /**
	   * gets the average of the average green foreground values
	   * @return average of the average green foreground values
	   */
	  public double getAvgAvgGreenFG()
	  {
		  double retVal = 0;
		  for (int i = 0; i < geneVector.size(); i++)
		  {
			  retVal += geneVector.elementAt(i).getGreenForegroundAvg();
		  }
		  retVal = retVal / ((geneVector.size())+1);
		  return retVal;
	  }
	  
	  /**
	   * gets the average of the average green background values
	   * @return average of the average green background values
	   */
	  public double getAvgAvgGreenBG()
	  {
		  double retVal = 0;
		  for (int i = 0; i < geneVector.size(); i++)
		  {
			  retVal += geneVector.elementAt(i).getGreenBackgroundAvg();
		  }
		  retVal = retVal / ((geneVector.size())+1);
		  return retVal;
	  }
	  
	  /**
	   * gets the standard deviation of the total red foreground values
	   * @return the standard deviation of the total red foreground values
	   */
	  public double getStDevTotalRedFG() {
		  int n = 0;
		  double mean = 0;
		  double S = 0;
		  for (GeneRawData i : geneVector) {
			  int x = i.getRedForegroundTotal();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
			  S = S + delta*(x-mean); //This expression uses the new value of mean
		  }
		  double variance = S/(n-1);
		  double stdev = Math.sqrt(variance);
		  return stdev;
	  }
	  
	  /**
	   * gets the standard deviation of the total red background values
	   * @return the standard deviation of the total red background values
	   */
	  public double getStDevTotalRedBG() {
		  int n = 0;
		  double mean = 0;
		  double S = 0;
		  for (GeneRawData i : geneVector) {
			  int x = i.getRedBackgroundTotal();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
			  S = S + delta*(x-mean); //This expression uses the new value of mean
		  }
		  double variance = S/(n-1);
		  double stdev = Math.sqrt(variance);
		  return stdev;
	  }
	  
	  /**
	   * gets the standard deviation of the total green foreground values
	   * @return the standard deviation of the total green foreground values
	   */
	  public double getStDevTotalGreenFG() {
		  int n = 0;
		  double mean = 0;
		  double S = 0;
		  for (GeneRawData i : geneVector) {
			  int x = i.getGreenForegroundTotal();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
			  S = S + delta*(x-mean); //This expression uses the new value of mean
		  }
		  double variance = S/(n-1);
		  double stdev = Math.sqrt(variance);
		  return stdev;
	  }
	  
	  /**
	   * gets the standard deviation of the total green background values
	   * @return the standard deviation of the total green background values
	   */
	  public double getStDevTotalGreenBG() {
		  int n = 0;
		  double mean = 0;
		  double S = 0;
		  for (GeneRawData i : geneVector) {
			  int x = i.getRedForegroundTotal();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
			  S = S + delta*(x-mean); //This expression uses the new value of mean
		  }
		  double variance = S/(n-1);
		  double stdev = Math.sqrt(variance);
		  return stdev;
	  }
	  
	  /**
	   * gets the standard deviation of the average red foreground values
	   * @return the standard deviation of the average red foreground values
	   */
	  public double getStDevAvgRedFG() {
		  int n = 0;
		  double mean = 0;
		  double S = 0;
		  for (GeneRawData i : geneVector) {
			  double x = i.getRedForegroundAvg();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
			  S = S + delta*(x-mean);
		  }
		  double variance = S/(n-1);
		  double stdev = Math.sqrt(variance);
		  return stdev;
	  }
	  
	  /**
	   * gets the standard deviation of the average red background values
	   * @return the standard deviation of the average red background values
	   */
	  public double getStDevAvgRedBG() {
		  int n = 0;
		  double mean = 0;
		  double S = 0;
		  for (GeneRawData i : geneVector) {
			  double x = i.getRedBackgroundAvg();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
			  S = S + delta*(x-mean);
		  }
		  double variance = S/(n-1);
		  double stdev = Math.sqrt(variance);
		  return stdev;
	  }
	  
	  /**
	   * gets the standard deviation of the average green foreground values
	   * @return the standard deviation of the average green foreground values
	   */
	  public double getStDevAvgGreenFG() {
		  int n = 0;
		  double mean = 0;
		  double S = 0;
		  for (GeneRawData i : geneVector) {
			  double x = i.getGreenForegroundAvg();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
			  S = S + delta*(x-mean);
		  }
		  double variance = S/(n-1);
		  double stdev = Math.sqrt(variance);
		  return stdev;
	  }
	  
	  /**
	   * gets the standard deviation of the average green background values
	   * @return the standard deviation of the average green background values
	   */
	  public double getStDevAvgGreenBG() {
		  int n = 0;
		  double mean = 0;
		  double S = 0;
		  for (GeneRawData i : geneVector) {
			  double x = i.getGreenBackgroundAvg();
			  n++;
			  double delta = x - mean;
			  mean = mean + delta/n;
			  S = S + delta*(x-mean);
		  }
		  double variance = S/(n-1);
		  double stdev = Math.sqrt(variance);
		  return stdev;
	  }
}
