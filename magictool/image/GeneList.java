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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 * GeneList opens text files containing the godlist of gene names and holds the names
 * to create expression files.
 */
@SuppressWarnings("unchecked")
public class GeneList {

  /**list of genes*/
  protected Vector list = new Vector();
  /**file path of the gene list*/
  protected String filepath;
  /**number of writeable gene name*/
  protected int writeable=0;

  /**
   * Constructs an empty gene list
   */
  public GeneList() {
  }

  /**
   * Constructs a gene list from the given file
   * @param filepath file path of the godlist
   */
  public GeneList(File filepath) {
    this(filepath.getAbsolutePath());
  }

  /**
   * Constructs a gene list from the given file
   * @param filepath file path of the godlist
   */
  public GeneList(String filepath) {
    this.filepath = filepath;
    getGeneList();
  }

  /**
   * Gets the gene list from the file specified
   */
  @SuppressWarnings("unchecked")
  private void getGeneList(){
    try{
      BufferedReader in = new BufferedReader(new FileReader(filepath));
      String line;

      while((line=in.readLine())!=null){
        StringTokenizer st = new StringTokenizer(line,"\t");
        list.add(st.nextToken());
      }

      for(int i=0; i<list.size(); i++){
        String name = list.elementAt(i).toString();
        if(!name.equalsIgnoreCase("empty")&&!name.equalsIgnoreCase("blank")&&!name.equalsIgnoreCase("missing")&&!name.equalsIgnoreCase("none")){
          int replicate=0;
          writeable++;
          if(name.toLowerCase().indexOf("_rep")==-1){
            for(int j=i+1; j<list.size(); j++){
              String otherName = list.elementAt(j).toString();
              if(name.equals(otherName)){
                replicate++;
                otherName = otherName + "_rep" + (replicate+1);
                list.setElementAt(otherName, j);
              }
            }
            if(replicate!=0) list.setElementAt(new String(name + "_rep1"), i);
          }
        }
      }


      in.close();
    } catch(Exception e){}
  }

  /**
   * sets the new godlist file and opens the new gene list
   * @param filepath file path of the godlist
   */
  public void setGeneFile(String filepath){
    this.filepath = filepath;
    getGeneList();
  }

  /**
   * returns the file path of the godlist
   * @return file path of the godlist
   */
  public String getGeneFile(){
    return filepath;
  }

  /**
   * returns the gene name at the specified index
   * @param index index of the gene
   * @return gene name at the specified index
   */
  public String getGene(int index){
    if(index<list.size()) return (String)list.get(index);
    return "No Gene Specified";
  }

  /**
   * returns the number of genes in the list
   * @return number of genes in the list
   */
  public int getNumGenes(){
    return list.size();
  }

  /**
   * returns the number of writeable genes in the list. A gene name is writeable if
   * it is not empty, blank, missing or none.
   * @return number of writeable genes in the list
   */
  public int getNumWriteableGenes(){
    return writeable;
  }






}