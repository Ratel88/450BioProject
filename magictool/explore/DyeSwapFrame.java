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

package magictool.explore;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.Arrays;
import magictool.ExpFile;
import magictool.Gene;
import magictool.Project;
import magictool.TwoListPanelNoReorder;
import magictool.VerticalLayout;

public class DyeSwapFrame extends JDialog implements ContainerListener, KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8685117081676237248L;
	private JPanel jPanel2 = new JPanel();
	private JPanel jPanel3 = new JPanel();
	private JPanel jCheckBoxJPanel = new JPanel();
	private JLabel jLabel1 = new JLabel();
	private JTextField fileField = new JTextField();
	private Border border1;
	private Border border2;
	private BorderLayout borderLayout1 = new BorderLayout();
	private VerticalLayout verticalLayout1 = new VerticalLayout();
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	private JCheckBox logCheckBox;
	private TitledBorder titledBorder1;
	
	/**project associated with the expression file that is being modified*/
	protected Project project;
	/**panels of the columns to be added or removed*/
	protected TwoListPanelNoReorder twoPanel;
	/**expression file that is begin modified*/
	protected ExpFile exp;
	/**name of expression file*/
	 protected String filename="";
	 /**whether or not the modification is complete*/
	 protected boolean finished = false;
	 
	 /**
	  * Constructs a frame with all the columns of the expression file in the unchanged columns list and a blank list of 
	  * columns to be modified
	  * @param e expression file being modified
	  * @param prj project associated with the expression file
	  * @param parent parent frame
	  */
	 public DyeSwapFrame(ExpFile e, Project prj, Frame parent) {
		 super(parent);
		 this.exp=e;
		 this.project=prj;
		 Object o[] = exp.getLabelArray();
		 String s[] = new String[o.length];
		 for (int i = 0; i<s.length; i++) {
			 s[i] = o[i].toString();
		 }
		 twoPanel = new TwoListPanelNoReorder(s,null,"Unchanged Columns",
				 "Columns To Be Dye Swapped");
		 this.setTitle("Dye Swapping " + exp.getName());
		 try {
			 jbInit();
			 addKeyAndContainerListenerRecursively(this);
		 }
		 catch (Exception ex) {
			 System.out.println("Error loading DyeSwapFrame.");
			 System.out.println("Stack Trace:");
			 ex.printStackTrace();
		 }
	 }
	 
	 private void jbInit() throws Exception {
		 border1 = BorderFactory.createEmptyBorder(3,3,3,3);
		 border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(148,145,140),new Color(103,101,98)), BorderFactory.createEmptyBorder(3,3,3,3));
		 titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new Color(153,153,153),2), "Select Columns");
		 jLabel1.setBorder(border2);
		 jLabel1.setText("New Expression File");
		 String name = exp.getName();
		 if(name.endsWith(".exp")) name=name.substring(0,name.lastIndexOf("."));
		 name+="_dyeswap.exp";
		 fileField.setText(name);
		 jPanel2.setLayout(borderLayout1);
		 this.getContentPane().setLayout(verticalLayout1);
		 okButton.setText("OK");
		 okButton.addActionListener(new java.awt.event.ActionListener() {
			 //completes the action, makes the expression file, and closes the dialog box
			 public void actionPerformed(ActionEvent e) {
				 okButton_actionPerformed(e);
			 }
		 });
		 rootPane.setDefaultButton(okButton);
		 cancelButton.setText("Cancel");
		 cancelButton.addActionListener(new java.awt.event.ActionListener() {
			 //closes the dialog box without making the new expression file
			 public void actionPerformed(ActionEvent e) {
				 cancelButton_actionPerformed(e);
			 }
		 });
		 
		 //jCheckBoxJPanel.setLayout(borderLayout1);
		 //let's find out if we think we're log.
		 File expfile = exp.getExpFile();
		 String expfilename = expfile.getName();
		 boolean isLog;
		 if (expfilename.contains("tlog")) isLog = true;
		 else isLog = false;
		 logCheckBox = new JCheckBox("Data is Log Transformed",isLog);
		 
		 twoPanel.setBorder(titledBorder1);
		 this.getContentPane().add(twoPanel,null);
		 jCheckBoxJPanel.add(logCheckBox);
		 this.getContentPane().add(jCheckBoxJPanel,null);
		 this.getContentPane().add(jPanel2, null);
		 jPanel2.add(jLabel1, BorderLayout.WEST);
		 jPanel2.add(fileField, BorderLayout.CENTER);
		 this.getContentPane().add(jPanel2,null);
		 jPanel3.add(okButton,null);
		 jPanel3.add(cancelButton,null);
		 this.getContentPane().add(jPanel3,null);
	 }
	 
	 private void okButton_actionPerformed(ActionEvent e) {
		 String file = fileField.getText().trim();	//get the name of the file we're dealing with
		 if((file.endsWith(".exp"))) file=file.substring(0, file.lastIndexOf("."));	//remove the .exp if it's there
		 File expfile = exp.getExpFile();
		 //String expfilename = expfile.getName();
		 boolean isLog = logCheckBox.isSelected();
		 File f = new File(project.getPath()+file+File.separator+file+".exp");	//create the new file
		 int deleteFiles = JOptionPane.NO_OPTION;
		 if ( (!f.exists()) || (deleteFiles=JOptionPane.showConfirmDialog(null, "File Already Exists!\nDo you wish to overwrite the existing file?\nWARNING: Overwriting the file will delete all files related to the overwritten file!","MAGIC Tool Warning",JOptionPane.YES_NO_OPTION))==JOptionPane.OK_OPTION) {
			 try {
				 if (deleteFiles == JOptionPane.YES_OPTION) f.getParentFile().delete();
				 f.getParentFile().mkdirs();
				 String[] cols = twoPanel.getSecondElements();	//these are the elements in the dye-swap column
				 //now, let's get the column numbers in the exp file that correspond to these strings.
				 Object[] colNames_obj = exp.getLabelArray();
				 String[] colNames = new String[colNames_obj.length];
				 boolean[] dyeSwap = new boolean[colNames_obj.length];
				 for (int i = 0; i < colNames_obj.length; i++) {
					 colNames[i] = (String)colNames_obj[i];
					 //find out if colNames[i] is in i
					 if (this.isInArray(cols, colNames[i])) dyeSwap[i] = true;
					 else dyeSwap[i] = false;
				 }
				 
				 int numColumns = exp.getColumns();
				 int numGenes = exp.numGenes();
				 BufferedWriter bw = new BufferedWriter(new FileWriter(f.getPath()));
				 //first, write column headers
				 for (int i = 0; i < numColumns; i++) {
					 bw.write(exp.getLabel(i)+"\t");
				 }
				 bw.write("\n");
				 //next, for each gene in exp file...
				 for (int g = 0; g < numGenes; g++) {
					 double data[] = exp.getData(g);
					 //write gene name
					 bw.write(exp.getGeneName(g));
					 //for each column in exp file...
					 for (int col = 0; col < numColumns; col++) {
						 if (dyeSwap[col]) {	//if this column is marked to be dye-swapped
							 if (isLog) { //if this data is log
								 //WRITE \t + reciprocal of datapt
								 double ndatapt = 1/data[col];
								 bw.write("\t" + ndatapt);
							 }
							 else {
								 //WRITE \t + negative of datapt
								 double ndatapt = -data[col];
								 bw.write("\t" + ndatapt);
							 }
						 }
						 else {
							 //WRITE datapt
							 bw.write("\t" + data[col]);
						 }
					 }
					 //WRITE comments if they exist
					 String comments;
					 if ((comments=exp.getGene(g).getComments())!=null) bw.write("\t"+comments);
					 //WRITE \n
					 bw.write("\n");
				 }
				 
				 //Now we print the Gene Information.
				 bw.write("/**Gene Info**/"+"\n");
				 for (int i = 0; i < numGenes; i++) {
					 Gene g = exp.getGene(i);
					 String n = g.getName();
					 String a = g.getAlias();
					 String c = g.getChromo();
					 String l = g.getLocation();
					 String p = g.getProcess();
					 String fl = g.getFunction();
					 String co = g.getComponent();
					 if(n!=null) bw.write(n+"\t"+ (a!=null?a:" ") + "\t" + (c!=null?c:" ") + "\t" + (l!=null?l:" ") +"\t" + (p!=null?p:" ") + "\t" + (fl!=null?fl:" ") +"\t" + (co!=null?co:" ")+ "\n");
				 }
				 bw.close();
				 finished = true;
				 filename = file + File.separator + file + ".exp";
				 this.dispose();
			 }
			 catch (Exception ex) {
				 JOptionPane.showInternalMessageDialog(this, "Error writing exp file!", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
			 }
		 }
	 }
	 
	 /**
	  * Searches an array of Strings sequentially to find the index of a target string
	  * @param arr the array of Strings to search 
	  * @param targ the target string
	  * @return the index in the array of the target string if found, -1 if not
	  */
	 private int locInArray(String[] arr, String targ) {
		 int retVal = -1;
		 for (int i = 0; i <arr.length; i++) {
			 if(arr[i].equalsIgnoreCase(targ)){
				 retVal = i;
				 break;
			 }
		 }
		 return retVal;
	 }
	 
	 /**
	  * Searches an array of Strings sequentially to find the target String
	  * @param arr the array of strings to search
	  * @param targ the target string
	  * @return <b>true</b> if the string is in the array; <b>false</b> otherwise 
	  */
	 private boolean isInArray(String[] arr, String targ) {
		 String[] arr2 = new String[arr.length];
		 System.arraycopy(arr,0,arr2,0,arr.length);
		 //first, sort arr
		 Arrays.sort(arr2, String.CASE_INSENSITIVE_ORDER);
		 int loc = Arrays.binarySearch(arr2, targ, String.CASE_INSENSITIVE_ORDER);
		 if (loc>=0) return true;
		 else return false;
		 
	 }
	 
	 private void cancelButton_actionPerformed(ActionEvent e) {
		 finished = false;
		 this.dispose();
	 }
	 
	 public String getValue() {
		 if(finished) return filename;
		 return null;
	 }
	 
	 private void addKeyAndContainerListenerRecursively(Component c) {
		 c.removeKeyListener(this);
		 c.addKeyListener(this);
		 if(c instanceof Container) {
			 Container cont = (Container)c;
			 cont.removeContainerListener(this);
			 cont.addContainerListener(this);
			 Component[] children = cont.getComponents();
			 for (int i = 0; i < children.length; i++) {
				 addKeyAndContainerListenerRecursively(children[i]);
			 }
		 }
	 }
	 
	 private void removeKeyAndContainerListenerRecursively(Component c) {
		 c.removeKeyListener(this);
		 if(c instanceof Container){
			 Container cont = (Container)c;
			 cont.removeContainerListener(this);
			 Component[] children = cont.getComponents();
			 for (int i = 0; i < children.length; i++) {
				 removeKeyAndContainerListenerRecursively(children[i]);
			 }
		 }
	 }
	 
	 /**
	  * adds key and container listeners when a component or container is added
	  * @param e container event
	  */
	 public void componentAdded(ContainerEvent e){
		 addKeyAndContainerListenerRecursively(e.getChild());
	 }
	 
	 /**
	  * removes key and container listeners when a component or container is removed
	  * @param e container event
	  */
	 public void componentRemoved(ContainerEvent e){
		 removeKeyAndContainerListenerRecursively(e.getChild());
	 }
	 
	 /**
	  * Checks for all key presses.
	  * Currently, this just checks keypresses for CTRL+W and closes the frame on CTRL+W
	  * @param e key event to check
	  */
	 public void keyPressed(KeyEvent e) {
		 //check for CTRL+W
		 if ( (e.getKeyCode() == KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK).getKeyCode()) && (e.isControlDown()) ) this.dispose();
	 }
	 
	 /** 
	  * Does absolutely nothing, but has to be there since this is a special sort of fram
	  * @param e key event
	  */
	 public void keyReleased(KeyEvent e){}
	 
	 /** 
	  * Does absolutely nothing, but has to be there since this is a special sort of fram
	  * @param e key event
	  */
	 public void keyTyped(KeyEvent e){}
}
