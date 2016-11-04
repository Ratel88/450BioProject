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

package  magictool;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Collection;
import java.util.TreeSet;


/**
 * GrpFile holds a list of genes from an expression file that form a group file.
 * GrpFile can read and write group files to store and retain group.
 */
public class GrpFile {

    /**group file title*/
    protected String title = new String();
    /**genes that compose the group file*/
    protected TreeSet group = new TreeSet();
    /**group file itself*/
    protected File grpFile;
    /**expression file where group file genes are located*/
    protected String exp=null;
    /**Special case of doing itersection with on an empty set of a new list.**/
    protected boolean newList=true;
    
    /**
     * Default Constructor - creates a group file with no genes and no title.
     */
    public GrpFile () {}

    /**
     * Constructs a group file with a title but no genes
     * @param title group file title
     */
    public GrpFile(String title){
      this.title = title;
    }

    /**
     * Constructs a group file from a stored group file
     * @param newgrpfile group file where the group is store
     */
    public GrpFile (File newgrpfile) {
        this.title = newgrpfile.getName();
        this.grpFile = newgrpfile;
        int lineCount = 0;
        String line;
        try {
            BufferedReader bufferedreader = new BufferedReader(new FileReader(newgrpfile));
            exp = bufferedreader.readLine();
            while ((line = bufferedreader.readLine()) != null) {
                line.trim();
                group.add(line);
            }
        } catch (Exception e) {

        }
    }

    /**
     * returns the expression file where the group file genes exists
     * @return expression file where the group file genes exists
     */
    public String getExpFile(){
      return exp;
    }

    /**
     * sets the expression file where the group file genes exists
     * @param exp expression file where the group file genes exists
     */
    public void setExpFile(String exp){
      this.exp=exp;
    }

    /**
     * clears the group file of all genes
     */
    public void clearGrp () {
        group.clear();
    }

    /**
     * sets the group file title
     * @param title group file title
     */
    public void setTitle (String title) {
        this.title = title;
    }

    /**
     * returns whether or not group file has a title
     * @return whether or not group file has a title
     */
    public boolean hasTitle(){
      return(title!=null);
    }

    /**
     * returns the group file title
     * @return group file title
     */
    public String getTitle () {
        return  title;
    }

    /**
     * returns the group file
     * @return group file
     */
    public File getGrpFile(){
      return grpFile;
    }

    /**
     * adds the collection to the group
     * @param c collection of genes to add to the group
     */
    public void meetAny (Collection c) {
        group.addAll(c);
    }

    /**
     * Use for criteria involving intersection. The frist time we add something we simply add
     * it. From then onward we intersect with the original list and keep the intersection.
     */
    public void meetAll (Collection c) {
    	if (newList==true){
    		newList=false;
        	group.addAll(c);
        }else {
            group.retainAll(c);
        }
    }

    /**
     * adds a gene
     * @param o gene to add
     */
    public void addOne(Object o){
      group.add(o);

    }

    /**
     * writes a group file to the specified location
     * @param name new group filename
     * @throws DidNotFinishException if process did not complete
     */
    public void writeGrpFile(String name) throws DidNotFinishException{
       try{
        BufferedWriter bw = new BufferedWriter(new FileWriter(name));
        Object o[] = this.getGroup();
        bw.write(exp+"\n");
        for(int i=0; i<o.length; i++){
          bw.write((String)o[i]+"\n");
        }
        bw.close();
      }
      catch(Exception e){
        throw new DidNotFinishException();
      }
    }

    /**
     * removes a gene from the group
     * @param o gene to remove from the group
     */
    void remove(Object o){
      group.remove(o);

    }

    /**
     * returns the number of genes in the group
     * @return number of genes in the group
     */
    public int getNumGenes () {
        return  group.size();
    }

    /**
     * returns an array of genes in the group
     * @return array of genes in the group
     */
    public Object[] getGroup(){
      return group.toArray();
    }

    /**
     * returns a string list of genes in the group
     * @return string list of genes in the group
     */
    public String getAsText () {
        String grpString = group.toString();
        grpString = grpString.replace(' ', '\n');
        grpString = grpString.replace(',', ' ');
        grpString = grpString.replace('[', ' ');
        grpString = grpString.replace(']', ' ');
        grpString = grpString.trim();
        return  grpString;
    }
}









