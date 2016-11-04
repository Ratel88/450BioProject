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

package magictool;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.Vector;
import java.lang.Math;
import javax.swing.JOptionPane;

/**
 * ExpFile reads an expression file of data and places the data into Genes.
 * ExpFile also calculates data about the genes including averages, standard deviations,
 * correlations, covariances, weight lp, jacknife correlations, minumums, and maximums.
 * The class is also used to get any desired information about a specific gene.
 */
@SuppressWarnings("unchecked")
public class ExpFile {

   /**vector of genes that compose the expression file*/
   protected Vector genes = new Vector();
   /**vector of genes that exist in the expression file*/
   protected Vector chromosomes = new Vector();
   /**ArrayList of data labels*/
   protected ArrayList labels = new ArrayList();
   private StringBuffer comments = new StringBuffer();
   private StringBuffer header = new StringBuffer();
   private File expFile; //expression file
   private double maxExpValue = Double.NEGATIVE_INFINITY; //max value
   private double minExpValue = Double.POSITIVE_INFINITY; //min value
   private boolean isValid = true; //if there are errors in the file
   /**shortened name of expression file*/
   protected String name;
   /**method of handling missing data*/
   protected static int missingStyle;
   /**percentage of data acceptable to be missing when missing data method is ignore*/
   protected static int missingThresh;
   /**remove any gene missing data*/
   public static final int REMOVE=0;
   /**ignore missing data in a gene up to the threshold after which gene is removed*/
   public static final int IGNORE=1;
   /**number of genes missing data*/
   protected int genesMissingData=0;
   /**total pieces of data missing*/
   protected int totalMissing=0;
   /**total number of gene removed*/
   protected int removedGenes=0;
   /**scramble order of genes*/
   public static final int SCRAMBLE_ORDER=0;
   /**scramble data within genes*/
   public static final int SCRAMBLE_WITHIN_GENE=1;
   /**scramble data within columns*/
   public static final int SCRAMBLE_WITHIN_COLUMN=2;
   /**scramble data within genes and columns*/
   public static final int SCRAMBLE_WITHIN_BOTH=3;
   /**This is used to determine if we have displayed the zero message before*/
   private boolean logZeroMessage = false;

   /**
    * Construct a new expression file, gathering the data, calculating the averages, and standard deviations
    * and placing the information in instances of the gene class. This handles the missing data acoording to
    * the style specified.
    * @param newexpfile
    */
   public ExpFile(File newexpfile) {
       //most in-code commenting by Michael Gordon 9/13/2006
      this.name=newexpfile.getName();
      name = name.substring(0, name.lastIndexOf(".")); //sets the shortened name
      //check for formatting
      try {
         boolean foundData = false;
         boolean geneInfo = false;
         String line;
         FileReader filereader = new FileReader(newexpfile);
         BufferedReader bufferedreader = new BufferedReader(filereader);
         while ((line = bufferedreader.readLine()) != null) {	//get the line from the file
            line.trim();

            //handles header comments
            if(line.startsWith("/**")&&line.toLowerCase().indexOf("gene info")!=-1) geneInfo=true;//marker for gene info
            else if(line.startsWith("//")){
               header.append(line + "\n");
            } else if (line.startsWith("/*")){
               header.append(line + "\n");
               if (!line.endsWith("*/")) {
                  boolean go = true;
                  while (go) {
                     char ch = (char)bufferedreader.read();
                     header.append(ch);
                     if (ch == '*') {
                        ch = (char)bufferedreader.read();
                        header.append(ch);
                        if (ch == '/') {
                           header.append("\n");

                           go = false;
                           bufferedreader.readLine();
                        }
                     }
                  }
               }
            }

            //handles blank lines
            else if(line.equals("")){}

            //handles lines containg gene info - chromosome, function
            else if(geneInfo){
               String name=null, alias=null, chrom=null, loc=null, proc=null, func=null, comp=null;
               Vector tokens = createTokens(line, 7);

               name=(String)tokens.elementAt(0);
               alias=(String)tokens.elementAt(1);
               chrom=(String)tokens.elementAt(2);
               loc=(String)tokens.elementAt(3);
               proc=(String)tokens.elementAt(4);
               func=(String)tokens.elementAt(5);
               comp=(String)tokens.elementAt(6);

               int num = findGeneName(name);
               if(num!=-1) {//if we found some sort of gene info
                  Gene g = (Gene)genes.elementAt(num); //huh? why are we casting?
                  g.setChromo(chrom);
                  if(chrom!=null&&!chrom.trim().equals("")&&!chromosomes.contains(chrom)) chromosomes.add(chrom);
                  g.setAlias(alias);
                  g.setLocation(loc);
                  g.setProcess(proc);
                  g.setFunction(func);
                  g.setComponent(comp);
               }
            }

            //handles adding genes and their data
            else{
               if (foundData == false){
                  Vector tokens = createTokens(line,true);  //make the tokens
                  for(int i=0; i<tokens.size(); i++){ //for each token...
                     labels.add((String)tokens.elementAt(i)); //add the token to the labels - how does this work
                  }
                  genes = new Vector(); //why are we doing this again?
                  foundData = true; //tell everyone we're set up for receiving data

               } else { //we're set up for receiving data
                  boolean addData=true;
                  boolean hasMissed=false;
                  Vector tokens = createTokens(line,labels.size()+2);
                  String geneName = (String)tokens.elementAt(0);
                  if(geneName.equals("")) { //so this particular gene doesn't have a name; it's missing DATA.
                     addData=false; //don't add
                     hasMissed=true; //note that we have missed something
                     genesMissingData++; //I'm assuming this count will go to the client
                  }

                  //now we do genes
                  double tempData[] = new double[labels.size()];
                  int m=0;

                  for (int column = 0; column < labels.size(); column++) { //for each column 
                     try{
                        tempData[column] = Double.parseDouble((String)tokens.elementAt(column+1)); //if we've got good data
                     } catch(NumberFormatException e){ //we've got missing gene data because we were unable to parse the double
                        if(missingStyle==IGNORE){
                           tempData[column]=Double.POSITIVE_INFINITY; //we ignore data with a Double.POSITIVE_INFINITY
                           m++; //m is the number of missed data points
                        } else addData=false; //if we have another sort of missing data style, don't add data at all
                        if(!hasMissed) genesMissingData++; //if we haven't missed anything yet, increment genesMissingData (huh?)
                        hasMissed=true; //then we already have missed something
                        totalMissing++; //and we increase the number of things that are missing
                     }//end catch block
                  } //end for block

                  if(addData&&((double)m)/((double)labels.size())<=((double)missingThresh)/100.0){
                  //if there's data to add AND the percentage of data we don't have is less than the missing threshold over 100
                     Gene tempGene = new Gene(geneName,tempData); //gene has name and data, the data we read in
                     tempGene.setAvg(calcAvg(tempData)); //calculate the average
                     tempGene.setSD(calcSD(tempData, tempGene.getAvg())); //calculate the standard deviation
                     double d;
                     if((d=tempGene.getMaxValue())!=Double.POSITIVE_INFINITY){ //if it has absolutely no missing data points
                        maxExpValue=Math.max(maxExpValue,d); //to find our maximum value in the expression
                     }
                     if((d=tempGene.getMinValue())!=Double.POSITIVE_INFINITY){ //same for min
                        minExpValue=Math.min(minExpValue,d);
                     }

                     //handles comments
                     String commenttag = (String)tokens.elementAt(labels.size()+1);
                     if (commenttag.startsWith("/*")) commenttag=commenttag.substring(2,commenttag.length());
                     else if (commenttag.startsWith("//")) commenttag=commenttag.substring(2,commenttag.length());
                     if (commenttag.endsWith("*/")) commenttag=commenttag.substring(0,commenttag.length()-2);
                     if(!commenttag.equals("")) tempGene.setComments(commenttag);

                     genes.addElement(tempGene);
                  } else removedGenes++;
               }
            }
         }
         expFile = newexpfile;
      } catch (Exception e) {
         isValid = false; //sets file to invalid if there is an exception
      }
   }

   /**
    * Creates a vector of as many tab-delimited tokens as exist - blank tokens are stored as empty strings
    * @param string full string to be broken into tab-delimited tokens
    * @return a vector of as many tab-delimited tokens as exist
    */
   public Vector createTokens(String string){
      return createTokens(string,0,false);
   }

   /**
    * Creates a vector of as many tab-delimited tokens as exist - blank tokens are stored as empty strings.
    * Method will skip empty tokens at the start of the string if skipFirstBlanks is true.
    * @param string full string to be broken into tab-delimited tokens
    * @param skipFirstBlanks whether to skip blank tokens at the beggining of the string
    * @return a vector of as many tab-delimited tokens as exist
    */
   public Vector createTokens(String string, boolean skipFirstBlanks){
      return createTokens(string,0,skipFirstBlanks);
   }

   /**
    * Creates a vector of as many tab-delimited tokens as exist and appends as many blank tokens
    * as necessary to return at least total number of tokens - blank tokens are stored as empty strings.
    * @param string full string to be broken into tab-delimited tokens
    * @param total minimum number of tokens to return
    * @return a vector of as many tab-delimited tokens as exist and appends as many blank tokens as necessary to return at least total number of tokens
    */
   public Vector createTokens(String string, int total){
      return createTokens(string,total,false);
   }

   /**
    * Creates a vector of as many tab-delimited tokens as exist and appends as many blank tokens
    * as necessary to return at least total number of tokens - blank tokens are stored as empty strings.
    * Method will skip empty tokens at the start of the string is skipFirstBlanks is true.
    * @param string full string to be broken into tab-delimited tokens
    * @param total minimum number of tokens to return
    * @param skipFirstBlanks whether to skip blank tokens at the beggining of the string
    * @return a vector of as many tab-delimited tokens as exist and appends as many blank tokens as necessary to return at least total number of tokens
    */
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
    * returns whether or not the expression file is valid
    * @return whether or not the expression file is valid
    */
   public boolean isValid() {
      return  isValid;
   }

   /**
    * returns the location of the gene if it exists and otherwise returns -1
    * @param name name of the gene
    * @return the location of the gene if it exists and otherwise returns -1
    */
   public int findGeneName(String name){
      for(int line=0; line<genes.size(); line++){
         //when this method is called from PrintableTable.setGroup, the genename passed
         //in has an extra space character at its end - I think that is screwing it up.
         if(((Gene)genes.elementAt(line)).getName().equalsIgnoreCase(name)) return line;
      }
      return -1;
   }

   /**
    * normalizes the data for the genes and recalculates standard deviations and averages
    */
   public void normalize() {
      for (int row = 0; row < genes.size(); row++) {
         Gene g = (Gene)genes.elementAt(row);
         double tempData[] = g.getData();
         for (int column = 0; column < tempData.length; column++) {
            if(tempData[column]!=Double.POSITIVE_INFINITY&&tempData[column]!=Double.NaN&&tempData[column]!=Double.NEGATIVE_INFINITY){
               tempData[column] = (tempData[column] - g.getAvg())/g.getSD();
            }
         }
         g.setData(tempData);
         g.setAvg(calcAvg(tempData));
         g.setSD(calcSD(tempData, g.getAvg()));
      }
      recalcMinMax();
   }

   /**
    * scrambles the order of the genes
    * @param type type of scrambling
    */
   public void scramble(int type) {
      Random random = new Random();

      if(type==ExpFile.SCRAMBLE_WITHIN_GENE){
         for (int i=0; i<numGenes(); i++) {
            Gene p = (Gene)genes.elementAt(i);

            for(int j=0; j<p.data.length; j++){
               int pos = random.nextInt(p.data.length);
               double temp = p.data[j];
               p.data[j] = p.data[pos];
               p.data[pos] = temp;
            }
         }
      } else if(type==ExpFile.SCRAMBLE_WITHIN_COLUMN){
         for(int j=0; j<getColumns(); j++){
            for (int i=0; i<numGenes(); i++) {
               int pos = random.nextInt(numGenes());

               Gene g = (Gene)genes.elementAt(pos);
               Gene p = (Gene)genes.elementAt(i);
               double temp = p.data[j];
               p.data[j] = g.data[j];
               g.data[j] = temp;
            }
         }
      } else if(type==ExpFile.SCRAMBLE_WITHIN_BOTH){
         for(int j=0; j<getColumns(); j++){
            for (int i=0; i<numGenes(); i++) {
               int pos = random.nextInt(numGenes());
               int colpos = random.nextInt(getColumns());

               Gene g = (Gene)genes.elementAt(pos);
               Gene p = (Gene)genes.elementAt(i);
               double temp = p.data[j];
               p.data[j] = g.data[colpos];
               g.data[colpos] = temp;
            }
         }
      } else{
         for (int i=0; i<numGenes(); i++) {
            int pos = random.nextInt(numGenes());

            Gene g = (Gene)genes.elementAt(pos);
            Gene p = (Gene)genes.elementAt(i);
            genes.setElementAt(p,pos);
            genes.setElementAt(g,i);
         }
      }

   }

   /**
    * returns an object array of gene names in the order they exist
    * @return an object array of gene names in the order they exist
    */
   public Object[] getGeneVector(){
      Vector v = new Vector();
      for(int i=0; i<genes.size(); i++){
         v.addElement(((Gene)genes.elementAt(i)).getName());
      }
      return v.toArray();
   }

   /**
    * returns an array of chromosomes that exist
    * @return an array of chromosomes that exist
    */
   public String[] getChromosomes(){
      String[] c = new String[chromosomes.size()];
      for(int i=0; i<chromosomes.size(); i++){
         c[i]=((chromosomes.elementAt(i)).toString());
      }
      return c;
   }

   /**
    * returns the method to handle missing data
    * @return the method to handle missing data
    */
   public static int getMissingDataStyle(){
      return missingStyle;
   }

   /**
    * returns the percentage of data acceptable to be missing when missing data method is ignore
    * @return percentage of data acceptable to be missing when missing data method is ignore
    */
   public static int getMissingThresh(){
      return missingThresh;
   }

   /**
    * returns the gene at the specified location
    * @param num location of gene
    * @return the gene at the specified location
    */
   public Gene getGene(int num){
      return (Gene)genes.elementAt(num);
   }

   /**
    * sets the method for handling missing data
    * @param style method for handling missing data
    */
   public static void setMissingDataStyle(int style){
      missingStyle=style;
      if(missingStyle>IGNORE) missingStyle=REMOVE;
   }

   /**
    * sets the percentage of data acceptable to be missing when missing data method is ignore
    * @param thresh percentage of data acceptable to be missing when missing data method is ignore
    */
   public static void setMissingThreshold(int thresh){
      missingThresh=thresh;
   }


   /**
    * recalculates the minimum and maximum of the data
    */
   public void recalcMinMax(){
      double min = Double.POSITIVE_INFINITY;
      double max = Double.NEGATIVE_INFINITY;
      for(int count = 0; count<genes.size(); count++){
         Gene g = (Gene)genes.elementAt(count);
         double d;
         if((d=g.getMaxValue())!=Double.POSITIVE_INFINITY){
            max=Math.max(max,d);
         }
         if((d=g.getMinValue())!=Double.POSITIVE_INFINITY){
            min=Math.min(min,d);
         }
      }

      this.minExpValue = min;
      this.maxExpValue = max;
   }

   /**
    * transforms the data
    * @param b constant
    */
   public void transformBX(double b){
      for (int row = 0; row < genes.size(); row++) {
         Gene g = (Gene)genes.elementAt(row);
         double tempData[] = g.getData();
         for (int column = 0; column < tempData.length; column++) {
            if(tempData[column]!=Double.POSITIVE_INFINITY&&tempData[column]!=Double.NaN&&tempData[column]!=Double.NEGATIVE_INFINITY){
               tempData[column] = Math.pow(b,(double)tempData[column]);
            }
         }
         g.setData(tempData);
         g.setAvg(calcAvg(tempData));
         g.setSD(calcSD(tempData, g.getAvg()));
      }
      recalcMinMax();
   }

   /**
    * log transforms the data
    * If we abort early this will return false and stop future calculations.
    * Else return true.
    * @param b log constant
    */
   public int transformLOGBX(double b) throws Exception{
	  int result=0; //Used to store option for log 0.
      for (int row = 0; row < genes.size(); row++) {
         Gene g = (Gene)genes.elementAt(row);
         double tempData[] = g.getData();
         for (int column = 0; column < tempData.length; column++) {
            if(tempData[column]!=Double.POSITIVE_INFINITY&&tempData[column]!=Double.NaN&&tempData[column]!=Double.NEGATIVE_INFINITY){
               if (tempData[column] == 0){
                  tempData[column] = .0001;
                  if(!logZeroMessage){
                	  
                	  Object[] options = {"Set Such Values To 0.0001", "Abort"};
                	  result = JOptionPane.showOptionDialog(null, "0 appears in expression file.\nWhat action should we take?", 
                			  "Log(0) Error", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                	  if(result==1) return result; //We are aborting.
                	  logZeroMessage=true;
                	  //JOptionPane.showMessageDialog(null, "0 appears in expression file; filtering recommended.\n0 has been changed to .0001");
                	  
                  }
               }
               tempData[column] = (Math.log(tempData[column])/Math.log(b));
            }
         }
         g.setData(tempData);
         g.setAvg(calcAvg(tempData));
         g.setSD(calcSD(tempData, g.getAvg()));
      }
      recalcMinMax();
      return result; //Worked as intended or picked .0001 for bad values.
   }

   /**
    * returns the data for a given gene location
    * @param num gene location
    * @return data for given gene location
    */
   public double[] getData(int num) {
      return ((Gene)genes.elementAt(num)).getData();
   }


   /**
    * returns the maximum value for a given column
    * @param col column number
    * @return the maximum value for a given column
    */
   public float getMaxColumnValue(int col){
      double max = Double.NEGATIVE_INFINITY;
      for(int i=0; i<genes.size(); i++){
         double gvals[] = ((Gene)genes.elementAt(i)).getData();
         if(gvals.length>col&&gvals[col]>max&&gvals[col]!=Double.POSITIVE_INFINITY) max = gvals[col];
      }
      return (float)max;
   }

   /**
    * returns the minimum value for a given column
    * @param col column number
    * @return the minimum value for a given column
    */
   public float getMinColumnValue(int col){
      double min = Double.POSITIVE_INFINITY;
      for(int i=0; i<genes.size(); i++){
         double gvals[] = ((Gene)genes.elementAt(i)).getData();
         if(gvals.length>col&&gvals[col]<min&&gvals[col]!=Double.POSITIVE_INFINITY) min = gvals[col];
      }
      return (float)min;
   }

   /**
    * returns the data for a given gene name
    * @param geneName gene name
    * @return data for given gene name
    */
   public double[] getData(String geneName) {
      int num = findGeneName(geneName);
      if(num==-1) return null;
      return ((Gene)genes.elementAt(num)).getData();
   }

//    /**
//     * returns a string of all gene names.  A string buffer is initially created
//     * and appended to avoid costly concatenation operations on string objects, which
//     * are immutable.  After all the gene names have been added to the StringBuffer,
//     * it is converted to a string, which is supposedly a very efficient operation.
//     * This string is passed to a writeUTF() method, and this was the problem.<b>
//     * writeUTF() inherently cannot deal with more than 64Kb of data.</b>
//     * @return a string all gene names
//     */
//    public String getAllGenes () {
//        StringBuffer allgenes = new StringBuffer();
//        for (int count = 0; count < genes.size(); count++)
//            allgenes.append(((Gene)genes.elementAt(count)).getName() + " ");
//        return allgenes.toString();
//    }

   /**
    * Writes all the gene names to a specified data stream in UTF-8 format.
    * @param DataOutputStream stream to which data should be added
    * @throws IOException
    */
   public void getAllGenes(DataOutputStream stream) throws IOException {
      ProcessTimer getAllGenesTimer = new ProcessTimer("expfile.getAllGenes()");
      for (int count = 0; count < genes.size(); count++)
         //stream.writeUTF(((Gene)genes.elementAt(count)).getName() + " ");  //unchanged the change - 7/05/05
         stream.writeUTF(((Gene)genes.elementAt(count)).getName()); //changed back to adding space - 7/01/05
      getAllGenesTimer.finish();
   }

   /**
    * returns the gene name at a given location
    * @param line location of the gene
    * @return the gene name at a given location
    */
   public String getGeneName(int line) {
      if(line>=genes.size()) return null;
      return  ((Gene)genes.elementAt(line)).getName();
   }

   /**
    * returns an array of all the gene names
    * @return an array of all the gene names
    */
   public String[] getGeneNames(){
      String names[] = new String[genes.size()];
      for(int i=0; i<genes.size(); i++){
         names[i]=((Gene)genes.elementAt(i)).getName();
      }
      return names;

   }

   /**
    * gets the data label for a given column
    * @param column column number
    * @return data label for a given column
    */
   public String getLabel(int column) {
      return  labels.get(column).toString();
   }

   /**
    * returns the number of columns
    * @return number of columns
    */
   public int getColumns(){
      return labels.size();
   }


   /**
    * returns the location of a data label
    * @param label name of the data label
    * @return location of a data label
    */
   public int getLabelNum(Object label) {
      return  labels.indexOf(label);
   }


   /**
    * returns data for a given columns and gene location
    * @param line gene location
    * @param column column number
    * @return data for a given columns and gene location
    */
   public double getDataPoint(int line, int column) {
      if(line>=genes.size()) return Double.POSITIVE_INFINITY;
      return  ((Gene)genes.elementAt(line)).getDataPoint(column);
   }

   /**
    * returns the number of genes
    * @return number of genes
    */
   public int numGenes() {
      return  genes.size();
   }

   /**
    * returns if comments exist for the file
    * @return if comments exist for the file
    */
   public boolean hasComments() {
      if (comments != null)
         return  true;
      else
         return  false;
   }

   /**
    * returns an array of the data labels
    * @return an array of the data labels
    */
   public Object[] getLabelArray() {
      return  labels.toArray();
   }



   /**
    * returns a text representation of the file
    * @return a text representation of the file
    */
   public String getAsText() {
      StringBuffer text = new StringBuffer();
      if (header != null)
         text.append(header);
      text.append(labelString());
      text.append("\n");
      for (int row = 0; row < genes.size(); row++) {
         text.append(((Gene)genes.elementAt(row)).getName()+"\t");
         text.append(rowString(row));
         String com;
         if((com =((Gene)genes.elementAt(row)).getComments())!=null){
            text.append(com);
         }
         text.append("\n");
      }
      return  text.toString();
   }

   /**
    * returns the expression file
    * @return the expression file
    */
   public String getPath() {
      return  expFile.getPath();
   }

   /**
    * returns a string of all data labels
    * @return string of all data labels
    */
   private String labelString() {
      String labelstring = labels.toString();
      labelstring = labelstring.replace(',', ' ');
      labelstring = labelstring.replace('[', ' ');
      labelstring = labelstring.replace(']', ' ');
      labelstring = labelstring.trim();
      return  labelstring;
   }

   //returns string of data
   private String rowString(int row) {
      StringBuffer rowString = new StringBuffer();
      for (int column = 0; column < genes.size(); column++) {
         double[] tempData = ((Gene)genes.elementAt(row)).getData();
         for(int i=0; i<tempData.length; i++){
            rowString.append(String.valueOf(tempData[i])+"\t");
         }
      }
      return  rowString.toString();
   }

   /**
    * returns the minimum value for a specific gene number
    * @param line gene number
    * @return minimum value for a specific gene number
    */
   public double getMin(int line) {
      if(line>=genes.size()) return Double.POSITIVE_INFINITY;
      return ((Gene)genes.elementAt(line)).getMinValue();
   }

   /**
    * returns the minimum jump between data values for a gene
    * @param line gene number
    * @return minimum jump between data values for a gene
    */
   public double getMinJump(int line) {
      double minJump = 0;
      double data[] = ((Gene)genes.elementAt(line)).getData();
      int pos=0;
      while(data[pos]==Double.POSITIVE_INFINITY&&pos<data.length-1) pos++;
      if(pos==data.length-1) return Double.POSITIVE_INFINITY;
      for (int column = pos; column < data.length - 1; column++)
         if (((column == pos) || (Math.abs((data[column + 1]) - (data[column])) < minJump))&&data[column]!=Double.POSITIVE_INFINITY&&data[column+1]!=Double.POSITIVE_INFINITY)
            minJump = Math.abs((data[column + 1]) - (data[column]));
      return  minJump;
   }

   /**
    * returns the maximum value for a specific gene number
    * @param line gene number
    * @return maximum value for a specific gene number
    */
   public double getMax(int line) {
      if(line>=genes.size()) return Double.POSITIVE_INFINITY;
      return ((Gene)genes.elementAt(line)).getMaxValue();
   }

   /**
    * returns the maximum jump between data values for a gene
    * @param line gene number
    * @return maximum jump between data values for a gene
    */
   public double getMaxJump(int line) {
      double maxJump = 0;
      double data[] = ((Gene)genes.elementAt(line)).getData();
      int pos=0;
      while(data[pos]==Double.POSITIVE_INFINITY&&pos<data.length-1) pos++;
      if(pos==data.length-1) return Double.POSITIVE_INFINITY;
      for (int column = pos; column < data.length - 1; column++)
         if (((column == pos) || (Math.abs((data[column + 1]) - (data[column])) > maxJump))&&data[column]!=Double.POSITIVE_INFINITY&&data[column+1]!=Double.POSITIVE_INFINITY)
            maxJump = Math.abs((data[column + 1]) - (data[column]));
      return  maxJump;
   }

   /**
    * calculates the average data value for a given gene number
    * @param row gene number
    * @return average data value for a given gene number
    */
   public double calcAvg(int row) {
      return calcAvg(((Gene)genes.elementAt(row)).getData());
   }

   /**
    * calculates the average data value for a given gene number excluding a missing term
    * @param row gene number
    * @param missing missing term number
    * @return average data value for a given gene number excluding a missing term
    */
   public double calcAvg(int row, int missing) {
      return calcAvg(((Gene)genes.elementAt(row)).getData(),missing);
   }

   /**
    * calculates the average value for an array of doubles excluding a missing term
    * @param gene data values
    * @param missing missing term number
    * @return average value for an array of doubles excluding a missing term
    */
   public static double calcAvg(double[] gene, int missing) {
      double avg = 0;
      int m=0;
      for (int column = 0; column < gene.length; column++) {
         if (column != missing){
            if(gene[column]!=Double.POSITIVE_INFINITY&&gene[column]!=Double.NaN&&gene[column]!=Double.NEGATIVE_INFINITY){
               avg += gene[column];
            } else m++;
         }
      }
      avg = avg/(gene.length - 1 - m);
      return  avg;
   }

   /**
    * gets the average data value for a given gene number
    * @param row gene number
    * @return average data value for a given gene number
    */
   public double getAvg(int row) {
      return  ((Gene)genes.elementAt(row)).getAvg();
   }

   /**
    * returns the average jump between data values for a gene
    * @param line gene number
    * @return average jump between data values for a gene
    */
   public double getAvgJump(int line) {
      double avgJump = 0;
      double data[] = ((Gene)genes.elementAt(line)).getData();
      int removed=0;
      for (int column = 0; column < data.length - 1; column++){
         if(data[column]!=Double.POSITIVE_INFINITY&&data[column+1]!=Double.POSITIVE_INFINITY){
            avgJump += Math.abs((data[column + 1]) - (data[column]));
         } else removed++;
      }
      if(avgJump!=0) avgJump = avgJump/(data.length - 1-removed);
      return  avgJump;
   }

   /**
    * gets the standard deviation for a gene number
    * @param line gene numbers
    * @return standard deviation for a gene number
    */
   public double getSD(int line) {
      return  ((Gene)genes.elementAt(line)).getSD();
   }

   /**
    * returns the largest standard deviation from the gene data
    * @return largest standard deviation
    */
   public double getMaxSD(){
      if (genes.size()==0) return 0.0;
      double max = ((Gene)genes.elementAt(0)).getSD();
      for(int j=1; j<genes.size(); j++){
         double sd = ((Gene)genes.elementAt(j)).getSD();
         if(sd>max) max=sd;
      }
      return max;
   }

   /**
    * calculates the standard deviation for a gene number
    * @param line gene numbers
    * @return standard deviation for a gene number
    */
   public double calcSD(int line) {
      return calcSD(((Gene)genes.elementAt(line)).getData(),((Gene)genes.elementAt(line)).getAvg());
   }

   /**
    * calculates the standard deviation for an array of doubles
    * @param gene data values
    * @return standard deviation for an array of doubles
    */
   public static double calcSD(double[] gene){
      return calcSD(gene,calcAvg(gene));
   }

   /**
    * calculates the standard deviation for an array of doubles
    * @param gene data values
    * @param avg average value of the data
    * @return standard deviation for an array of doubles
    */
   public static double calcSD(double[] gene,double avg) {
      double sum = 0;
      int m = 0;
      for (int column = 0; column < gene.length; column++){
         if(gene[column]!=Double.POSITIVE_INFINITY&&gene[column]!=Double.NaN&&gene[column]!=Double.NEGATIVE_INFINITY){
            sum += Math.pow(gene[column] - avg, 2);
         } else m++;
      }
      double stddev = Math.sqrt((sum/(gene.length-m)));

      return  stddev;
   }

   /**
    * calculates the standard deviation for a gene number excluding a missing term
    * @param line gene number
    * @param missing missing term
    * @return standard deviation for a gene number excluding a missing term
    */
   public double calcSD(int line, int missing) {
      return calcSD(((Gene)genes.elementAt(line)).getData(),((Gene)genes.elementAt(line)).getAvg(),missing);
   }

   /**
    * calculates the standard deviation for an array of doubles excluding a missing term
    * @param gene1 data values
    * @param missing missing term
    * @return standard deviation for an array of doubles excluding a missing term
    */
   public static double calcSD(double[] gene1, int missing) {
      return calcSD(gene1,calcAvg(gene1),missing);
   }


   /**
    * calculates the standard deviation for an array of doubles excluding a missing term
    * @param gene data values
    * @param missing missing term
    * @param avg average of the data values excluding the missing term
    * @return standard deviation for an array of doubles excluding a missing term
    */
   public static double calcSD(double[] gene, double avg, int missing) {
      double sum = 0;
      int m=0;
      for (int column = 0; column < gene.length; column++){
         if (column != missing){
            if(gene[column]!=Double.POSITIVE_INFINITY&&gene[column]!=Double.NaN&&gene[column]!=Double.NEGATIVE_INFINITY){
               sum+= Math.pow((gene[column] - avg),2);
            } else m++;
         }
      }
      return Math.sqrt(sum/(gene.length - 1- m));
   }


   /**
    * returns the covariance for two gene locations
    * @param gene1 gene location number one
    * @param gene2 gene location number two
    * @return covariance for two gene locations
    */
   public float cov(int gene1, int gene2) {
      return cov(	((Gene)genes.elementAt(gene1)).getData(),
              ((Gene)genes.elementAt(gene1)).getAvg(),
              ((Gene)genes.elementAt(gene2)).getData(),
              ((Gene)genes.elementAt(gene2)).getAvg() );
   }

   /**
    * returns the covariance for a gene location and an array of data
    * @param gene1 gene location number one
    * @param gene2 data values
    * @return covariance for two gene location and an array of data
    */
   public float cov(int gene1, double[] gene2) {
      return cov(	((Gene)genes.elementAt(gene1)).getData(),
              ((Gene)genes.elementAt(gene1)).getAvg(),
              gene2, calcAvg(gene2) );
   }

   /**
    * returns the covariance for two arrays of data
    * @param gene1 data values
    * @param gene2 data values
    * @return the covariance for two arrays of data
    */
   public static float cov(double[] gene1, double[] gene2) {
      return cov(gene1,calcAvg(gene1),gene2,calcAvg(gene2));
   }

   /**
    * returns the covariance for two arrays of data
    * @param gene1 data values
    * @param avg1 average of first data values
    * @param gene2 data values
    * @param avg2 average of second data values
    * @return the covariance for two arrays of data
    */
   public static float cov(double[] gene1, double avg1, double[] gene2, double avg2) {	//TODO move data validity test to method that populates the gene arrays
      float sum = 0;
      int m = 0;
      for (int count = 0; count < gene2.length; count++) {
         if((gene1[count]!=Double.POSITIVE_INFINITY && gene1[count]!=Double.NaN && gene1[count]!=Double.NEGATIVE_INFINITY)
         && (gene2[count]!=Double.POSITIVE_INFINITY && gene2[count]!=Double.NaN && gene2[count]!=Double.NEGATIVE_INFINITY)){
            sum += (float)(((float)gene1[count] - avg1)*((float)gene2[count] - avg2));
         } else m++;
      }
      sum = sum/(gene1.length-m);
      return  sum;
   }

   /**
    * returns the average for an array of data
    * @param d array of data values
    * @return average for an array of data
    */
   public static double calcAvg(double[] d){
      int m=0;
      double total=0.0;
      for(int i=0; i<d.length; i++){
         if(d[i]!=Double.POSITIVE_INFINITY&&d[i]!=Double.NaN&&d[i]!=Double.NEGATIVE_INFINITY)total+=d[i];
         else m++;
      }

      double avg = total/(d.length-m);

      return avg;
   }

   /**
    * returns the covariance for two gene locations excluding a missing term
    * @param gene1 gene location number one
    * @param gene2 gene location number two
    * @param missing term number
    * @return covariance for two gene locations excluding a missing term
    */
   public float cov(int gene1, int gene2, int missing) {
      return cov(((Gene)genes.elementAt(gene1)).getData(),((Gene)genes.elementAt(gene2)).getData(),missing);

   }

   /**
    * returns the covariance for a gene location and an array of data excluding a missing term
    * @param gene1 gene location number one
    * @param gene2 data values
    * @param missing term number
    * @return covariance for a gene location and an array of data excluding a missing term
    */
   public float cov(int gene1, double[] gene2, int missing) {
      return cov(((Gene)genes.elementAt(gene1)).getData(),gene2,missing);

   }

   /**
    * returns the covariance for two arrays of data excluding a missing term
    * @param gene1 data values
    * @param gene2 data values
    * @param missing term number
    * @return covariance for two arrays of data excluding a missing term
    */
   public static float cov(double[] gene1, double[] gene2, int missing) {
      int m=0;
      float sum = 0;
      for (int count = 0; count < gene1.length; count++) {
         if (count != missing){
            if((gene1[count]!=Double.POSITIVE_INFINITY&&gene1[count]!=Double.NaN&&gene1[count]!=Double.NEGATIVE_INFINITY)
            &&(gene2[count]!=Double.POSITIVE_INFINITY&&gene2[count]!=Double.NaN&&gene2[count]!=Double.NEGATIVE_INFINITY)){
               sum = sum + (float)(((float)gene1[count] - calcAvg(gene1,
                       missing))*((float)gene2[count] - calcAvg(gene2,
                       missing)));
            } else m++;
         }
      }
      sum = sum/(gene1.length - 1 - m);
      return  sum;
   }

   /**
    * returns the weighted lp for two gene locations and a given p value
    * @param gene1 gene location number one
    * @param gene2 gene location number two
    * @param p p-value
    * @return weighted lp for two gene locations and a given p values
    */
   public float weightedlp(int gene1, int gene2, int p) {
      return weightedlp(((Gene)genes.elementAt(gene1)).getData(),((Gene)genes.elementAt(gene2)).getData(),p);
   }


   /**
    * returns the weighted lp for a gene location and an array of data with a given p value
    * @param gene1 gene location number one
    * @param gene2 data values
    * @param p p-value
    * @return weighted lp for a gene location and an array of data with a given p value
    */
   public float weightedlp(int gene1, double[] gene2, int p) {
      return weightedlp(((Gene)genes.elementAt(gene1)).getData(),gene2,p);
   }


   /**
    * returns the weighted lp for two arrays of data with a given p value
    * @param gene1 data values
    * @param gene2 data values
    * @param p p-value
    * @return weighted lp for two arrays of data with a given p value
    */
   public static float weightedlp(double[] gene1, double[] gene2, int p) {
      double sum = 0;
      double power = (double)p;
      for (int count = 0; count < gene1.length; count++) {
         if((gene1[count]!=Double.POSITIVE_INFINITY&&gene1[count]!=Double.NaN&&gene1[count]!=Double.NEGATIVE_INFINITY)
         &&(gene2[count]!=Double.POSITIVE_INFINITY&&gene2[count]!=Double.NaN&&gene2[count]!=Double.NEGATIVE_INFINITY)){
            sum += Math.pow(Math.abs((gene1[count])- (gene2[count])), power);
         }
      }
      sum = Math.pow(sum, 1/power);
      return  (float)sum;
   }

   /**
    * returns the weighted lp for two gene locations and a p value of infinity
    * @param gene1 gene location number one
    * @param gene2 gene location number two
    * @return weighted lp for two gene locations and a p value of infinity
    */
   public float weightedlp(int gene1, int gene2) {
      return weightedlp(((Gene)genes.elementAt(gene1)).getData(),((Gene)genes.elementAt(gene2)).getData());
   }


   /**
    * returns the weighted lp for a gene location and an array of data with a p value of infinity
    * @param gene1 gene location number one
    * @param gene2 data values
    * @return weighted lp for a gene location and an array of data with a p value of infinity
    */
   public float weightedlp(int gene1, double[] gene2) {
      return weightedlp(((Gene)genes.elementAt(gene1)).getData(),gene2);
   }


   /**
    * returns the weighted lp for two arrays of data with a p value of infinity
    * @param gene1 data values
    * @param gene2 data values
    * @return weighted lp for two arrays of data with a p value of infinity
    */
   public float weightedlp(double[] gene1, double[] gene2) {
      float max = 0;
      for (int count = 0; count <gene1.length; count++) {

         if((gene1[count]!=Double.POSITIVE_INFINITY&&gene1[count]!=Double.NaN&&gene1[count]!=Double.NEGATIVE_INFINITY)
         &&(gene2[count]!=Double.POSITIVE_INFINITY&&gene2[count]!=Double.NaN&&gene2[count]!=Double.NEGATIVE_INFINITY)){
            max = Math.max(max, Math.abs((float)gene1[count] - (float)gene2[count]));
         }
      }
      return  max;
   }

   /**
    * returns 1-correlation coefficient for two gene locations
    * @param gene1 gene location number one
    * @param gene2 gene location number two
    * @return 1-correlation coefficient for two gene locations
    */
   public float correlation(int gene1, int gene2) {
      float cov = cov(gene1, gene2);
      double sd1 = getSD(gene1);
      double sd2 = getSD(gene2);
      double maxsd = getMaxSD();
      if(sd1>maxsd) maxsd = sd1;
      if(sd2>maxsd) maxsd = sd2;
      if(sd1==0){
         if(sd2==0) return 0f;
         else return (float)(1-2*((sd2)/maxsd));
      } else if(sd2==0) return (float)(1-2*((sd1)/maxsd));
      else return  (float)(1 - (cov)/((sd1)*(sd2)));
      // return  (float)(1 - ((cov(gene1, gene2))/((getSD(gene1))*(getSD(gene2)))));
   }
   
   public float absCorrelation(int gene1, int gene2) {
	   float cov = cov(gene1, gene2);
	   double sd1 = getSD(gene1);
	   double sd2 = getSD(gene2);
	   double maxsd = getMaxSD();
	   if(sd1>maxsd) maxsd=sd1;
	   if(sd2>maxsd) maxsd=sd2;
	   if ((sd1==0) && (sd2==0)) return 0f;
	   else if (sd1 == 0) //sd2 != 0
		   return (float)(1-Math.abs((float)(2*((sd2)/maxsd))));
	   else if (sd2 == 0) //sd1 != 0
		   return (float)(1-Math.abs((float)(2*((sd1)/maxsd))));
	   else
		   return (float)(1-Math.abs((float)((cov)/(sd1*sd2))));
   }
   
   /**
    * returns 1-correlation coefficient for a gene location and an array of data
    * @param gene1 gene location number one
    * @param gene2 data values
    * @return 1-correlation coefficient for a gene location and an array of data
    */
   public float correlation(int gene1, double[] gene2) {
      float cov = cov(gene1, gene2);
      double sd1 = getSD(gene1);
      double sd2 = calcSD(gene2);
      double maxsd = getMaxSD();
      if(sd1>maxsd) maxsd = sd1;
      if(sd2>maxsd) maxsd = sd2;
      if(sd1==0){
         if(sd2==0) return 0f;
         else return (float)(1-2*((sd2)/maxsd));
      } else if(sd2==0) return (float)(1-2*((sd1)/maxsd));
      else return  (float)(1 - (cov)/((sd1)*(sd2)));
      //return  (float)(1 - ((cov(gene1, gene2))/((getSD(gene1))*(calcSD(gene2)))));
   }


   /**
    * returns 1-correlation coefficient for two arrays of data
    * @param gene1 data values
    * @param gene2 data values
    * @return 1-correlation coefficient for two arrays of data
    */
   public float correlation(double[] gene1, double[] gene2) {
      float cov = cov(gene1,gene2);
      double sd1 = calcSD(gene1);
      double sd2 = calcSD(gene2);
      double maxsd = getMaxSD();
      if(sd1>maxsd) maxsd = sd1;
      if(sd2>maxsd) maxsd = sd2;
      if(sd1==0){
         if(sd2==0) return 0f;
         else return (float)(1-2*((sd2)/maxsd));
      } else if(sd2==0) return (float)(1-2*((sd1)/maxsd));
      else return (float)(1 - (cov)/((sd1)*(sd2)));
   }


   /**
    * returns 1-correlation coefficient for two gene locations excluding a missing term
    * @param gene1 gene location number one
    * @param gene2 gene location number two
    * @param missing missing term location
    * @return 1-correlation coefficient for two gene locations excluding a missing term
    */
   public float correlation(int gene1, int gene2, int missing) {
      float cov = cov(gene1, gene2, missing);
      double sd1 = calcSD(gene1, missing);
      double sd2 = calcSD(gene2, missing);
      double maxsd = getMaxSD();
      if(sd1>maxsd) maxsd = sd1;
      if(sd2>maxsd) maxsd = sd2;
      if(sd1==0){
         if(sd2==0) return 0f;
         else return (float)(1-2*((sd2)/maxsd));
      } else if(sd2==0) return (float)(1-2*((sd1)/maxsd));
      else return  (float)(1 - (cov)/((sd1)*(sd2)));
   }


   /**
    * returns 1-correlation coefficient for a gene location and an array of data excluding a missing term
    * @param gene1 gene location number one
    * @param gene2 data values
    * @param missing missing term location
    * @return 1-correlation coefficient for a gene location and an array of data excluding a missing term
    */
   public float correlation(int gene1, double[] gene2, int missing) {
      float cov = cov(gene1, gene2, missing);
      double sd1 = calcSD(gene1, missing);
      double sd2 = calcSD(gene2, missing);
      double maxsd = getMaxSD();
      if(sd1>maxsd) maxsd = sd1;
      if(sd2>maxsd) maxsd = sd2;
      if(sd1==0){
         if(sd2==0) return 0f;
         else return (float)(1-2*((sd2)/maxsd));
      } else if(sd2==0) return (float)(1-2*((sd1)/maxsd));
      else return  (float)(1 - (cov)/((sd1)*(sd2)));
   }


   /**
    * returns 1-correlation coefficient for two arrays of data excluding a missing term
    * @param gene1 data values
    * @param gene2 data values
    * @param missing missing term location
    * @return 1-correlation coefficient for two arrays of data excluding a missing term
    */
   public float correlation(double[] gene1, double[] gene2, int missing) {
      float cov = cov(gene1, gene2, missing);
      double sd1 = calcSD(gene1, missing);
      double sd2 = calcSD(gene2, missing);
      double maxsd = getMaxSD();
      if(sd1>maxsd) maxsd = sd1;
      if(sd2>maxsd) maxsd = sd2;
      if(sd1==0){
         if(sd2==0) return 0f;
         else return (float)(1-2*((sd2)/maxsd));
      } else if(sd2==0) return (float)(1-2*((sd1)/maxsd));
      else return  (float)(1 - (cov)/((sd1)*(sd2)));
   }

   /**
    * returns the jacknife correlation for two gene locations
    * @param gene1 gene location number one
    * @param gene2 gene location number two
    * @return jacknife correlation for two gene locations
    */
   public float jackknife(int gene1, int gene2) {
      float j = 0;
      for (int missing = 0; missing < genes.size(); missing++) {
         if (missing == 0)
            j = correlation(gene1, gene2, missing);
         else
            j = Math.max(j, correlation(gene1, gene2, missing));
      }
      return  j;
   }

   /**
    * returns the jacknife correlation for a gene location and an array of data
    * @param gene1 gene location number one
    * @param gene2 data values
    * @return jacknife correlation for a gene location and an array of data
    */
   public float jackknife(int gene1, double[] gene2) {
      float j = 0;
      for (int missing = 0; missing < genes.size(); missing++) {
         if (missing == 0)
            j = correlation(gene1, gene2, missing);
         else
            j = Math.max(j, correlation(gene1, gene2, missing));
      }
      return  j;
   }

   /**
    * returns the jacknife correlation two arraya of data
    * @param gene1 data values
    * @param gene2 data values
    * @return jacknife correlation for two arrays of data
    */
   public float jackknife(double[] gene1, double[] gene2) {
      float j = 0;
      for (int missing = 0; missing < gene1.length; missing++) {
         if (missing == 0)
            j = correlation(gene1, gene2, missing);
         else
            j = Math.max(j, correlation(gene1, gene2, missing));
      }
      return  j;
   }

   /**
    * returns the maximum value of the expression file data
    * @return the maximum value of the expression file data
    */
   public float getMaxExpValue(){
      return (float)maxExpValue;
   }

   /**
    * the minimum value of the expression file data
    * @return the minimum value of the expression file data
    */
   public float getMinExpValue(){
      return (float)minExpValue;
   }

   /**
    * returns the name of the expression file
    * @return name of the expression file
    */
   public String getName(){
      return name;
   }

   /**
    * sets the name of the expression file
    * @return name of the expression file
    */
   public void setName(String n){
      name=n;
   }

   /**
    * returns the expression file
    * @return expression file
    */
   public File getExpFile(){
      return expFile;
   }
}
