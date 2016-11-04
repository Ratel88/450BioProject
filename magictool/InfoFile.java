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
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * InfoFile reads an info file of gene information and places that data into instances
 * of the Gene class, which may be later combined with an expression file.
 */
public class InfoFile {

    /**vector of genes that compose the info file*/
    protected Vector genes = new Vector();
    /**vector of genes that exist in the info file*/
    protected Vector chromosomes = new Vector();
    /**ArrayList of data labels*/
    protected ArrayList labels = new ArrayList();
    private StringBuffer comments = new StringBuffer();
    private StringBuffer header = new StringBuffer();

    private boolean isValid = true; //if there are errors in the file
    /**shortened name of info file*/
    protected String name;


    /**
     * Construct a new info file containing gene information.
     * @param newinfofile name of the new info file
     */
    public InfoFile (File newinfofile) {
        this.name=newinfofile.getName();
        name = name.substring(0, name.lastIndexOf(".")); //sets the shortened name
        //check for formatting
        try {
            boolean foundData = false;
            boolean geneInfo=true;
            String line;
            FileReader filereader = new FileReader(newinfofile);
            BufferedReader bufferedreader = new BufferedReader(filereader);
            while ((line = bufferedreader.readLine()) != null) {
                line.trim();
                //handles header comments
                if(line.startsWith("//")){
                    header.append(line + "\n");
                }
                else if (line.startsWith("/*")){
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


                    Gene g = new Gene(name,null,chrom,loc,alias,proc,func,comp);
                    //g.setChromo(chrom);
                    if(chrom!=null&&!chrom.trim().equals("")&&!chromosomes.contains(chrom)) chromosomes.add(chrom);
                    //g.setAlias(alias);
                    //g.setLocation(loc);
                    //g.setProcess(proc);
                    //g.setFunction(func);
                    //g.setComponent(comp);
                    genes.add(g);

                }

            }
            //expFile = newexpfile;
        } catch (Exception e) {
            e.printStackTrace();
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
     * Method will skip empty tokens at the start of the string is skipFirstBlanks is true.
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
            }
            else {
              tokens.addElement(s);
              next=false;
            }

            if(first&&tokens.size()>0) first=false;
          }
          else{
            st.nextToken();
            next=true;
          }
        }
        while(tokens.size()<total) tokens.addElement(new String(""));

        return tokens;
    }

    /**
     * returns whether or not the info file is valid
     * @return whether or not the info file is valid
     */
    public boolean isValid () {
        return  isValid;
    }

    /**
     * returns the location of the gene if it exists and otherwise returns -1
     * @param name name of the gene
     * @return the location of the gene if it exists and otherwise returns -1
     */
    public int findGeneName(String name){
      for(int line=0; line<genes.size(); line++){
        if(((Gene)genes.elementAt(line)).getName().equalsIgnoreCase(name)) return line;
      }
      return -1;
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
     * returns the gene at the specified location
     * @param num location of gene
     * @return the gene at the specified location
     */
    public Gene getGene(int num){
      return (Gene)genes.elementAt(num);
    }








    /**
     * returns a string all gene names
     * @return a string all gene names
     */
    public String getAllGenes () {
        StringBuffer allgenes = new StringBuffer();
        for (int count = 0; count < genes.size(); count++)
            allgenes.append(((Gene)genes.elementAt(count)).getName() + " ");
        return  allgenes.toString();
    }

    /**
     * returns the gene name at a given location
     * @param line location of the gene
     * @return the gene name at a given location
     */
    public String getGeneName (int line) {
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
     * returns the number of columns
     * @return number of columns
     */
    public int getColumns(){
      return labels.size();
    }






    /**
     * returns the number of genes
     * @return number of genes
     */
    public int numGenes () {
        return  genes.size();
    }




    /**
     * returns a text representation of the file
     * @return a text representation of the file
     */
    public String getAsText () {
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
     * returns a string of all data labels
     * @return string of all data labels
     */
    private String labelString () {
        String labelstring = labels.toString();
        labelstring = labelstring.replace(',', ' ');
        labelstring = labelstring.replace('[', ' ');
        labelstring = labelstring.replace(']', ' ');
        labelstring = labelstring.trim();
        return  labelstring;
    }

    //returns string of data
    private String rowString (int row) {
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
     * gets the info file name
     * @return info file name
     */
    public String getName(){
      return name;
    }

}