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

package magictool.dissim;

import java.io.Serializable;

/**
 * This class' purpose is to provide a new way of saving .dis files, as a Serialized instance of a class,
 * rather than making the computer slog through the various Streams involved in the old method.  This should
 * greatly speed up the process and make it simpler to understand for the coder, as well.  The primary advantage
 * has proven to be in reading the file; saving is somewhat faster, but reading is dramatically improved, to the
 * point of being trivial.
 * @author Gavin Taylor, for Dr. Laurie Heyer, Davidson College
 */
public class SerialDiss implements Serializable{
   
   private float[][] floaters; //list of float values - replaces body of old .dis files
   private int numGenes; //in header of old .dis files, number of genes
   private boolean isExpression; //in header of old .dis files
   private String expFilePath;
   private int disMethod;
   private String[] geneLabels;
   private String modifiers;
   
   private int retPosition=0;
   
   public SerialDiss() {
   }
   
   /**
    *"Header" is now a mostly irrelevant term, but it refers to the old construction of the .dis files.  Its use is still convenient,
    *but it doesn't actually mean anything.
    */
   public void writeHeader(int numGenes, boolean isExpression, String expFilePath, int disMethod, String[] geneLabels, String modifiers){
      this.numGenes=numGenes;
      this.isExpression=isExpression;
      this.expFilePath = expFilePath;
      this.disMethod=disMethod;
      this.geneLabels=geneLabels;
      this.modifiers=modifiers;
   }
   
   public void writeFloats(float[][] floaters){
      this.floaters=floaters;
   }
   
   /*
   public float getFloat(){
      retPosition++;
      return floaters[retPosition - 1];
   }
    */
   
   public float[][] getFloats(){
      return floaters;
   }
   
   public int getNumGenes(){
      return numGenes;
   }
   
   public boolean getIsExpression(){
      return isExpression;
   }
   
   public String getExpFilePath(){
      return expFilePath;
   }
   
   public int getDisMethod(){
      return disMethod;
   }
   
   public String[] getGeneLabels(){
      return geneLabels;
   }
   
   public String getModifiers(){
      return modifiers;
   }
}
