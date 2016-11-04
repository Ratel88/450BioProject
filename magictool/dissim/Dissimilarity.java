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

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import magictool.Cancelable;
import magictool.DidNotFinishException;
import magictool.Executable;
import magictool.ExpFile;
import magictool.ProcessTimer;
import magictool.ProgressFrame;
import magictool.Project;

/**
 * Dissimilarity creates a dissimilarity file with one of three methods - Correlation, Jacknife, Weighted LP.
 * The process is cancelable and can be executed as a task.
 */
public class Dissimilarity extends Thread implements Cancelable, Executable {
   
   /**expression file to create dissimlarity file from*/
   protected ExpFile expfile;
   private SerialDiss serial;
   private String outfile;
   /**dissimlarity method*/
   protected int disType;
   /**other parameters*/
   protected String modifiers;
   private ProgressFrame progress;
   private JDesktopPane desktop;
   /**whether process successfully completed*/
   protected boolean completed=false;
   /**whether process is over*/
   protected boolean over=false;
   /**project to place dissimlarity file in*/
   protected Project project=null;
   /**whether process has been canceled*/
   protected boolean cancel=false;
   /**1-Correlation Coefficent method*/
   public static final int COR=0;
   /**Weighted LP method*/
   public static final int LP=1;
   /**Jacknife Correlation method*/
   public static final int JACK=2;
   /**1-abs(Correlation Coefficient) method*/
   public static final int ABSCORR=3;
   /**whether or not to show error messages to the user*/
   protected boolean showMessages=true;
   
   
   /**
    * constructs the dissimilarity based on the method and files but does not start the creation of a file
    *
    * @param exp expression file to create dissimlarity file from
    * @param outfile dissimilarity filename
    * @param disType dissimilarity method
    * @param modifiers other parameters
    * @param desktop desktoppane to draw progress bar on
    */
   public Dissimilarity(String exp, String outfile, int disType, String modifiers, JDesktopPane desktop){
      this.expfile = new ExpFile(new File(exp));
      this.outfile = outfile;
      this.disType = disType;
      this.modifiers = modifiers;
      this.desktop = desktop;
   }
   
   /**
    * sets the project associated with the dissimilarity file
    * @param p project associated with the dissimilarity file
    */
   public void setProject(Project p){
      this.project=p;
   }
   
   /**
    * Overrides the run method of the thread to create the dissimilarity file.
    */
   public void run(){
      
      //creates the progress bar
      progress = new ProgressFrame("Calculating a Matrix of Dissimilarity scores\nfrom "+expfile.getExpFile().getName(), true, this);
      desktop.add(progress);
      progress.show();
      progress.setMaximum(((expfile.numGenes()-1)*(expfile.numGenes()))/2);
      
      //creates the file
      if (disType == COR) {
         writeCorrelation();
      } else if (disType == LP) {
         writeLP();
      } else if (disType == JACK) {
         writeJacknife();
      }
      else if (disType == ABSCORR) {
    	  writeAbsCorrelation();
      }
      
      //disposes the progress bar when completed	//MC:7/18/05 - it was causing a bunch of ArrayIndexOutofBound exceptions - changed it to be GUI thread safe
      SwingUtilities.invokeLater( new Runnable() {
         public void run(){
            progress.dispose();
         }
      });
      
      //adds the file to the project if completed
      if(completed==true&&!cancel){
         if(project!=null)project.addFile(expfile.getName()+File.separator+outfile.substring(outfile.lastIndexOf(File.separator)+1));
      }
      
      //displays error message if it failed
      else if(completed==false&&!cancel){
         if(showMessages)JOptionPane.showMessageDialog(null, "Error Writing Dissimilarity File");
      }
      
      over=true;
   }
   
    /*
     *Prints the header at the top of the dissimilarity file.
     *All the DataOutputStream write methods except for writeUTF(string)
     *output their information in binary.  writeUTF outputs "a java string
     *of Unicode characters using a slightly-modified version of the UTF-8
     *transformation format." An earlier implementation worked with a method
     *in magictool.expfile that provided a string of all gene names.
     */
   private void printHeader(DataOutputStream stream, ExpFile expfile, int disType, String modifiers) throws Exception{
      stream.writeInt(expfile.numGenes());//write number of genes
      stream.writeBoolean(true);//there is an expression file
      stream.writeUTF(expfile.getName() + (expfile.getName().endsWith(".exp")?"":".exp"));//write expression file path
      stream.writeInt(disType);//write dissimilarity method
      if(disType==1)
         stream.writeUTF(modifiers);//write modifiers if necessary
      //stream.writeUTF(expfile.getAllGenes());//write gene labels -Fails with GeneName Lists > 64Kb
      expfile.getAllGenes(stream);//write gene labels
   }
   
   /**
    * starts the thread and the creation of the dissimilarity file
    */
   public void start(){
      cancel=false;
      completed=false;
      over=false;
      super.start();
   }
   
   /**
    * cancels the process
    */
   public void cancel(){
      cancel=true;
      
      //deletes the file if it exists
      File f = new File(outfile);
      if(f.exists()){
         while(f.exists()){
            f.delete();
         }
         String name = f.getAbsolutePath();
         int current=-1;
         int slast=-1,last=-1;
         while((current=name.indexOf(File.separator,last+1))!=-1){
            slast=last;
            last=current;
         }
         if(project!=null&&project.fileExists(name)) project.removeFile(name);
      }
      
   }
   
   /**
    * writes the dissimilarity file using the 1-Correlation Coefficient method
    */
   protected void writeCorrelation(){
      try {         
         /**
          *CHANGES MADE FOR SERIALIZATION!!!!
          *Below is old code, commented out
          */
         
         
         /**FileChannel disOutChannel = new RandomAccessFile(outfile,"rw").getChannel();
          * DataOutputStream stream = new DataOutputStream( Channels.newOutputStream(disOutChannel) );
          * printHeader(stream, expfile, disType, null);
          *
          * MappedByteBuffer disMappedByteBuffer = disOutChannel.map(FileChannel.MapMode.READ_WRITE, disOutChannel.position(), ( expfile.numGenes()*(expfile.numGenes()-1)*2 ) );
          * for (int row = 1; row < expfile.numGenes() && !cancel; row++) {
          * for (int column = 0; column < row; column++) {
          * disMappedByteBuffer.putFloat(expfile.correlation(row, column));	//changed from stream.writeFloat(...) to disMappedByteBuffer.putFloat(...)
          * progress.addValue(1);
          * }
          * //progress.addValue(expfile.numGenes()-(row+1));		//TODO fix progress bar increment so it increases linearly
          * }
          * stream.close();
          * corrWriteTimer.finish();
          */
         
         serial=new SerialDiss();
         String exppath = expfile.getName() + (expfile.getName().endsWith(".exp")?"":".exp");
         serial.writeHeader(expfile.numGenes(), true, exppath, disType, expfile.getGeneNames(), null);
         float[][] floaters = new float[expfile.numGenes()][expfile.numGenes()];
         int pos = 0;
         for (int row=1; row<expfile.numGenes() && !cancel; row++){
            for (int column = 0; column < row; column++){
               floaters[row-1][column] = expfile.correlation(row, column);
               progress.addValue(1);
               pos++;
            }
            progress.addValue(expfile.numGenes()-(row+1));
         }
         serial.writeFloats(floaters);
         ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outfile));
         out.writeObject(serial);
         
         /**
          *new code ends here
          */
         if(!cancel) completed=true;
      } catch (Exception e2) {}
   }
   
   /**
    * Writes the dissimilarity file using the 1 - abs(Correlation Coefficient) method
    *
    */
   protected void writeAbsCorrelation() {
	   //System.out.println("Entering writeAbsCorrelation()...");
	   try{
		   serial = new SerialDiss();
		   String exppath = expfile.getName() + (expfile.getName().endsWith(".exp")?"":".exp");
		   serial.writeHeader(expfile.numGenes(), true, exppath, disType, expfile.getGeneNames(), null);
		   float[][] floaters = new float[expfile.numGenes()][expfile.numGenes()];
		   int pos = 0;
		   for (int row = 1; row < expfile.numGenes() && !cancel; row++) {
			   for (int column = 0; column < row; column++) {
				   floaters[row-1][column] = expfile.absCorrelation(row, column);
				   if (floaters[row-1][column] > 1){
					   System.out.println(floaters[row-1][column] + " is greater than 1.");
					   floaters[row-1][column] = 1;
					   System.out.println("It is now " + floaters[row-1][column]);
				   }
				   //System.out.println("Just calculated " + floaters[row-1][column]);
				   progress.addValue(1);
				   pos++;
				   //System.out.println("Position " + pos);
			   }
			   progress.addValue(expfile.numGenes()-(row+1));
		   }
		   serial.writeFloats(floaters);
		   ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outfile));
		   out.writeObject(serial);
		   if (!cancel) completed = true;
	   }
	   catch (Exception e2) {
		   System.out.println("Error writing 1-abs(corr) dissims.");
		   System.out.println("Stack Trace: ");
		   e2.printStackTrace();
	   }
   }
   
   /**
    * writes the dissimilarity file using the Weighted LP method
    */
   protected void writeLP(){
      try {
         /**
          * DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outfile)));
          * printHeader(stream, expfile, disType, modifiers);
          * for (int row = 1; row < expfile.numGenes(); row++) {
          * for (int column = 0; column < row && !cancel; column++) {
          * float lp = 0;
          * if (modifiers.toLowerCase().equals("i")) {
          * lp = expfile.weightedlp(row, column);
          * }
          * else {
          * int p = Integer.parseInt(modifiers);
          * lp = expfile.weightedlp(row, column, p);
          * }
          * stream.writeFloat(lp);
          * }
          * progress.addValue(expfile.numGenes()-(row+1));
          * }
          * stream.close();
          */
         serial = new SerialDiss();
         String exppath = expfile.getName() + (expfile.getName().endsWith(".exp")?"":".exp");
         serial.writeHeader(expfile.numGenes(), true, exppath, disType, expfile.getGeneNames(), modifiers);
         float[][] floaters = new float[expfile.numGenes()][expfile.numGenes()];
         int pos=0;
         for (int row=1; row<expfile.numGenes(); row++){
            for (int column=0; column < row && !cancel; column++){
               float lp=0;
               if (modifiers.toLowerCase().equals("i")){
                  floaters[row-1][column]=expfile.weightedlp(row, column);
               } else{
                  floaters[row-1][column]=expfile.weightedlp(row, column, Integer.parseInt(modifiers));
               }
               pos++;
            }
            progress.addValue(expfile.numGenes()-(row+1));
         }
         serial.writeFloats(floaters);
         ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outfile));
         out.writeObject(serial);
         
         if(!cancel) completed=true;
      } catch (Exception e2) {}
   }
   
   /**
    * writes the dissimilarity file using the Jacknife Correlation method
    */
   protected void writeJacknife(){
      try {
         /**
          * DataOutputStream stream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(outfile)));
          * printHeader(stream, expfile, disType, null);
          * for (int row = 1; row < expfile.numGenes(); row++) {
          * for (int column = 0; column < row && !cancel; column++) {
          * stream.writeFloat(expfile.jackknife(row, column));
          * }
          * progress.addValue(expfile.numGenes()-(row+1));
          * }
          * stream.close();
          **/
         serial = new SerialDiss();
         String exppath = expfile.getName() + (expfile.getName().endsWith(".exp")?"":".exp");
         serial.writeHeader(expfile.numGenes(), true, exppath, disType, expfile.getGeneNames(), modifiers);
         float[][] floaters = new float[expfile.numGenes()][expfile.numGenes()];
         for (int row=1; row<expfile.numGenes(); row++){
            for (int column=0; column < row && !cancel; column++){
               floaters[row-1][column]=expfile.jackknife(row, column);
            }
            progress.addValue(expfile.numGenes()-(row+1));
         }
         serial.writeFloats(floaters);
         ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outfile));
         out.writeObject(serial);
         if(!cancel)completed=true;
      } catch (Exception e2) {}
   }
   
   /**
    * executes the task of creating a dissimilarity file
    * @throws DidNotFinishException if the process failed
    */
   public void execute() throws DidNotFinishException{
      start();
      while(!over){}
      if(cancel||!completed) throw new DidNotFinishException();
   }
   
   /**
    * whether the process is finished
    * @return whether the process is finished
    */
   public boolean isFinished(){
      return over;
   }
}