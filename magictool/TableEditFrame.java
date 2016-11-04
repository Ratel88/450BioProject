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

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.print.PrinterJob;
import java.awt.Rectangle;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.*;


/**
 * TableEditFrame is a frame which displays an editable table of data which a user can save
 * either as the same file or as a new file. This frame is currently used to display gene data
 * and or gene information and saves the changed information in the appropriate expression
 * file.
 */
public class TableEditFrame extends JInternalFrame{

  /**
	 * 
	 */
	private static final long serialVersionUID = 161363130400654295L;
private JMenuBar jMenuBar = new JMenuBar();
  private JMenu filemenu = new JMenu();
  private JMenuItem save = new JMenuItem();
  private JMenuItem saveas = new JMenuItem();
  private JMenuItem print = new JMenuItem();
  private JMenuItem close = new JMenuItem();
  private JScrollPane jScrollPane1;
  private JMenu editMenu = new JMenu();
  private JMenuItem findMenu = new JMenuItem();
  private JMenuItem decimalMenu = new JMenuItem();
  private JCheckBoxMenuItem highlightMenu = new JCheckBoxMenuItem();

  ///**table model*/
  //private DefaultTableModel defaulttablemodel = new DefaultTableModel();
  /**editable and printable table displaying the data and or information about the genes*/
  protected PrintableTable jTable;
  /**expression file displayed*/
  protected ExpFile expMain;
  /**project associated with the expression file*/
  protected Project project=null;
  /**parent frame*/
  protected MainFrame parent=null;
  /**options dialog for highlighting cells*/
  HighlightTopAndBottomOptionsDialog htabod = new HighlightTopAndBottomOptionsDialog(parent, true);
  /**whether or not we should be able to highlight the top and bottom <i>n</i> cells*/
  protected boolean highlightable;


  /**
   * Constructor creates the frame with the given expression file and project showing
   * the gene data but not the gene information
   * @param exp expression file displayed
   * @param project project associated with the expression file
   */
  public TableEditFrame(ExpFile exp, Project project) {
    this(exp,project,true,false);
  }


  
  public TableEditFrame(ExpFile exp, Project project, boolean showData, boolean showInfo) {
	  this(exp,project,showData,showInfo,false);
  }
  
  /**
   * Constructor creates the frame with the given expression file and project showing
   * the gene data and or the gene information depending upon the specifications
   * @param exp expression file displayed
   * @param project project associated with the expression file
   * @param showData whether or not to display the gene data
   * @param showInfo whether or not to display the gene info
   * @param highlight whether or not we should be able to highlight the top and bottom <i>n</i> cells
   */
  @SuppressWarnings("unchecked")
  public TableEditFrame(ExpFile exp, Project project, boolean showData, boolean showInfo, boolean highlight) {
    this.expMain = exp;
    this.project=project;
    this.highlightable = highlight;
    jTable = new PrintableTable(expMain,new Vector(), PrintableTable.NORMAL,showData,showInfo);
    jScrollPane1 = new JScrollPane(jTable);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  //initializes the frame
  private void jbInit() throws Exception {

    this.setClosable(true);
    this.setJMenuBar(jMenuBar);
    this.setMaximizable(true);
    this.setResizable(true);
    this.setSize(500,300);
    this.setVisible(true);

    if(expMain!=null)this.setTitle("Editing " + expMain.getExpFile().getName());
    else this.setTitle("Editing Temporary File");


    jTable.setDoubleBuffered(false);
    filemenu.setText("File");
    save.setText("Save");
    save.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK, false));
    save.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        save_actionPerformed(e);
      }
    });
    saveas.setText("Save as...");
    saveas.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveas_actionPerformed(e);
      }
    });
    print.setText("Print...");
    print.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK, false));
    print.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        print_actionPerformed(e);
      }
    });
    close.setText("Close");
    close.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK, false));
    close.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        close_actionPerformed(e);
      }
    });
    jTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    jTable.setCellSelectionEnabled(true);
    editMenu.setText("Edit");
    
    findMenu.setText("Find Gene...");
    findMenu.addActionListener(new java.awt.event.ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		findMenu_actionPerformed(e);
    	}
    });
    
    decimalMenu.setText("Decimal Places");
    decimalMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        decimalMenu_actionPerformed(e);
      }
    });
    highlightMenu.setText("Highlight Top and Bottom Ratios");
    highlightMenu.setState(false);
    highlightMenu.addActionListener(new java.awt.event.ActionListener() {
    	public void actionPerformed(ActionEvent e){
    		highlightMenu_actionPerformed(e);
    	}
    });
    jMenuBar.add(filemenu);
    jMenuBar.add(editMenu);
    filemenu.add(save);
    filemenu.add(saveas);
    filemenu.addSeparator();
    filemenu.add(print);
    filemenu.addSeparator();
    filemenu.add(close);

    this.getContentPane().add(jScrollPane1);
    editMenu.add(findMenu);
    editMenu.add(decimalMenu);
    if(highlightable)editMenu.add(highlightMenu);

  }

  /**
   * sets the parent frame
   * @param parent parent frame
   */
  public void setParent(MainFrame parent){
    this.parent=parent;
  }


  //returns the table as one long text string - useful for saving the file to disk
  private String getTableText(){
    String theText = new String();
    int row=jTable.getRowCount();
    int col=jTable.getColumnCount();
    int datalength=expMain.getColumns();
    if(jTable.showData()){
      for(int i=1; i<(datalength+1)&&i<col; i++){
        theText+="\t"+jTable.getColumnName(i);
      }
      theText+="\n";
      DecimalFormat df = jTable.getDecimalFormat();
      for(int i=0; i<row; i++){
        int index = expMain.findGeneName(jTable.getValueAt(i,0).toString());
        if(index==-1){
          for(int j=0; j<(datalength+1)&&j<col; j++){
            theText+=jTable.getValueAt(i,j).toString()+"\t";
          }
        }
        else{
          Gene g = expMain.getGene(index);
          theText+=jTable.getValueAt(i,0).toString()+"\t";
          for(int j=1; j<(datalength+1)&&j<col; j++){
            try{
              if(df.format(g.getDataPoint(j-1)).equals(jTable.getValueAt(i,j).toString())){
                theText+=g.getDataPoint(j-1)+"\t";
              }
              else theText+=jTable.getValueAt(i,j).toString()+"\t";
            }catch(Exception e){
              theText+="\t";
            }
         }
        }
        if(jTable.showInfo()){
          theText+=jTable.getValueAt(i,1+datalength);
        }
        else{
          String comment = expMain.getGene(expMain.findGeneName(jTable.getValueAt(i,0).toString())).getComments();
          if(comment!=null) theText+=comment;

        }
        theText+="\n";
      }
    }
    else{
      datalength=0;
      for(int i=0; i<expMain.getColumns(); i++){
        theText+="\t"+expMain.getLabel(i);
      }
      theText+="\n";
      for(int i=0; i<row; i++){
        String ge =jTable.getValueAt(i,0).toString();
        int pos = expMain.findGeneName(ge);
        if(pos!=-1){
          Gene gene = expMain.getGene(pos);
          theText+=gene.getName()+"\t";
          for(int j=0; j<gene.getData().length; j++){
            theText+=gene.getDataPoint(j)+"\t";
          }
          if(jTable.showInfo()){
            theText+=jTable.getValueAt(i,1+datalength);
          }
          else{
            String comment = gene.getComments();
            if(comment!=null) theText+=comment;

          }
          theText+="\n";
        }
      }
    }

    theText+="/**Gene Info**/"+"\n";
    if(jTable.showInfo()){
      for(int i=0; i<row; i++){
        String n = jTable.getValueAt(i,0).toString();
        String a = jTable.getValueAt(i,1+datalength+1).toString();
        String c = jTable.getValueAt(i,1+datalength+2).toString();
        String l = jTable.getValueAt(i,1+datalength+3).toString();
        String p = jTable.getValueAt(i,1+datalength+4).toString();
        String f = jTable.getValueAt(i,1+datalength+5).toString();
        String co = jTable.getValueAt(i,1+datalength+6).toString();
        if(n!=null) theText+= n+"\t" + (a!=null?a:" ")+"\t" + (c!=null?c:" ")+"\t" + (l!=null?l:" ")+"\t" + (p!=null?p:" ")+"\t" + (f!=null?f:" ")+"\t" +(co!=null?co:" ")+"\n";
      }
    }
    else{
      for(int i=0; i<expMain.numGenes(); i++){
        Gene g = expMain.getGene(i);
        String n = g.getName();
        String a = g.getAlias();
        String c = g.getChromo();
        String l = g.getLocation();
        String p = g.getProcess();
        String f = g.getFunction();
        String co = g.getComponent();
        if(n!=null) theText+= n+"\t" + (a!=null?a:" ")+"\t" + (c!=null?c:" ")+"\t" + (l!=null?l:" ")+"\t" + (p!=null?p:" ")+"\t" + (f!=null?f:" ")+"\t" +(co!=null?co:" ")+"\n";
      }
    }
    return theText;
  }

  //saves file over current expression file
  private void saveTextFile (String text, String currFileName) throws IOException{
        try {
            // Open a file of the current name.
            File file = new File(currFileName);
            // Create an output writer that will write to that file.
            // FileWriter handles international characters encoding conversions.
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(text);
            out.close();

            if(parent!=null) MainFrame.expMain = new ExpFile(file);
            this.setTitle("Editing " + expMain.getExpFile().getName());

        } catch (IOException e) {
            throw new IOException();
        }
    }

    /**
     * sets the columns to fit all the information in them
     */
    public void setColumnsToFit(){
      jTable.setColumnsToFit();
    }

    //saves expression file
    private void saveTextFile (String text) {
        saveAsFile(text);
    }

    //saves expression file with user specified name
    private void saveAsFile (String text) {
        String s = (String)JOptionPane.showInputDialog(this,"Enter New File Name:");
        if(s!=null&&s.indexOf(File.separator)==-1){
              if(s.endsWith(".exp")) s=s.substring(0,s.lastIndexOf("."));
              String currFileName = project.getPath()+s+File.separator+s+".exp";

              if (saveFileIsValid(currFileName)){
                  try{

                    saveTextFile(text, currFileName);
                    this.setTitle(s + ".exp");
                    if(parent!=null) {
                     parent.addExpFile(currFileName);

                    }
                    if (project!=null) project.addFile(s+File.separator+s+".exp");
                  }catch(IOException e){
                    JOptionPane.showMessageDialog(this, "Error Saving File");
                  }
              }
        }
        else if(s.indexOf(File.separator)!=-1){
          JOptionPane.showMessageDialog(this, "Error! You Must Save File In Current Directory.\n File name must contain no file seperator characters");
          saveAsFile(text);
        }
    }



    //checks to ensure the file path is correct and no file exists there
    private boolean saveFileIsValid (String outfile) {
        outfile.trim();
        File outFile = new File(outfile);
        if (outFile.isDirectory()) {
            JOptionPane.showMessageDialog(this, "The output file path is a directory.  Please add a file name.",
                    "Directory Found", JOptionPane.OK_OPTION);
            return  false;
        }
        else if (outFile.exists()) {
            String[] options = new String[2];
          options[0] = UIManager.getString("OptionPane.yesButtonText");
          options[1] = UIManager.getString("OptionPane.noButtonText");
          int result=JOptionPane.showOptionDialog(parent, "The file "
                    + outFile.getPath() + " already exists.  Overwrite this file?",
                    "Overwrite File?",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[1]);

            if (result == JOptionPane.YES_OPTION) {
                outFile.delete();
                return  true;
            }
            else
                return  false;
        }
        else {
          outFile.getParentFile().mkdirs();
          return true;
        }
    }


  //saves over the current expression file if the user approves
  private void save_actionPerformed(ActionEvent e) {
        File f;
        if((f=expMain.getExpFile())!=null){
          int result=JOptionPane.CANCEL_OPTION;
          String[] options = new String[2];
          options[0] = UIManager.getString("OptionPane.yesButtonText");
          options[1] = UIManager.getString("OptionPane.noButtonText");
          result=JOptionPane.showOptionDialog(parent, "Saving this file will alter the data created and invalidate all files made previously with it. Do you wish to continue?","Save File?",JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,null,options,options[1]);
          if(result==JOptionPane.OK_OPTION){
            try{
              saveTextFile(getTableText(), f.getPath());
            }
            catch(IOException e1){JOptionPane.showMessageDialog(parent, "Error Saving File");}
          }
        }
        else
        saveTextFile(getTableText());
  }

  //saves new text file specified by user
  private void saveas_actionPerformed(ActionEvent e) {
        saveTextFile(getTableText());
  }

  //prints the table
  private void print_actionPerformed(ActionEvent e) {
      PrinterJob pj=PrinterJob.getPrinterJob();

      pj.setPrintable(jTable);
      if(pj.printDialog()){

        try{
          pj.print();
        }catch (Exception PrintException) {}

      }
  }

  //closes the window
  private void close_actionPerformed(ActionEvent e) {
    dispose();
  }

  private void decimalMenu_actionPerformed(ActionEvent e) {
    try{
      String number="";
      number=JOptionPane.showInputDialog(this,"Please Enter Decimal Places To Show");
      if(number!=null){
        int n = Integer.parseInt(number);
        if(n>=1){
          String form = "####.#";
          for(int i=1; i<n; i++){
            form+="#";
          }
          DecimalFormat df = new DecimalFormat(form);
          jTable.setDecimalFormat(df);
        }
        else JOptionPane.showMessageDialog(this,"Error! You Must Enter An Integer Value Greater Than Or Equal To 1.","Error!", JOptionPane.ERROR_MESSAGE);
      }

    }catch(Exception e2){
      JOptionPane.showMessageDialog(this,"Error! You Must Enter An Integer Value Greater Than Or Equal To 1.","Error!", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void highlightMenu_actionPerformed(ActionEvent e) {
	  if(!highlightMenu.getState()) {
		  //it WAS true, NOW it should go to false
		  highlightMenu.setState(false);
		  //set all columns to their default renderers
		  setAllColumnsToDefaultRenderers();
		  jTable.repaint();
	  }
	  else {
		  //it WAS false, NOW it should go to true
		  highlightMenu.setState(true);
		 try {
			  htabod = new HighlightTopAndBottomOptionsDialog(parent, true);
			  int numOfCellsToHighlight = getNumOfCellsToHighlight();
			  boolean blueOrange = getBlueOrange();
			  highlightTopAndBottomN(numOfCellsToHighlight,blueOrange);
		  }
		  catch(NegativeNumberException nne) {
			  JOptionPane.showMessageDialog(this, "<html>Please enter a <b>positive</b> number of cells to highlight.</html>","MAGIC Tool Error",JOptionPane.ERROR_MESSAGE);
			  highlightMenu.setState(false);
		  }
		  catch(BadCellNumberException bcne) {
			JOptionPane.showMessageDialog(this, "You can only highlight up to half the number of genes in the expression.","MAGIC Tool Error",JOptionPane.ERROR_MESSAGE);
			highlightMenu.setState(false);
		  }
		  catch(NumberFormatException nfe) {
			  JOptionPane.showMessageDialog(this, "Your entry did not appear to be a whole number greater than zero.","MAGIC Tool Error",JOptionPane.ERROR_MESSAGE);
			  highlightMenu.setState(false);
		  }
		  catch(BadColorSchemeException bcse) {
			  JOptionPane.showMessageDialog(this, "For some reason, an invalid color scheme was selected. Please try again.\nIf you continue to have problems, please e-mail magictool.help@gmail.com.","MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
			  highlightMenu.setState(false);
		  }
		  finally {
			  htabod.dispose();
		  }
		  
	  }
  }
  
  /**
   * Gets whether or not the color scheme selected in htabod is blue/orange
   * @return whether or not the color scheme selected in htabod is blue/orange
   * @throws BadColorSchemeException if an invalid color scheme is selected
   */
  private boolean getBlueOrange() throws BadColorSchemeException {
	  boolean blueOrange;
	  int colorSchemeCode = htabod.getColorScheme();
	  if (colorSchemeCode == HighlightTopAndBottomOptionsDialog.RED_GREEN_SCHEME) blueOrange = false;
	  else if (colorSchemeCode == HighlightTopAndBottomOptionsDialog.BLUE_ORANGE_SCHEME) blueOrange = true;
	  else throw new BadColorSchemeException();
	  return blueOrange;
  }
  
  /**
   * Gets the number of cells to highlight (top and bottom)
   * @return the number of cells to highlight
   * @throws NegativeNumberException if the entry is <= 0
   * @throws BadCellNumberException if the user entered more than half the number of rows in the table
   * @throws NumberFormatException if the string entered in the box is not in the appropriate format to be converted to an integer 
   */
  private int getNumOfCellsToHighlight() throws NegativeNumberException, BadCellNumberException, NumberFormatException {
	  //String inputString = JOptionPane.showInputDialog(this, "Please enter the number of top/bottom cells you wish to be highlighted: ");
	  int x = this.getX();
	  int y = this.getY();
	  htabod.setLocation(x+(this.getWidth()/3), y+(this.getHeight()/3));
	  htabod.setVisible(true);
	  try {
		  int input = htabod.getNumOfCells();
		  if (input <= 0) throw new NegativeNumberException();	//okay, so it's not strictly negative, but whatever.
		  //now we check to see if that was too many
		  if (input > ((jTable.getDefaultTableModel().getRowCount())/2)) throw new BadCellNumberException();
		  return input;
	  }
	  catch (NumberFormatException nfe) {
		  throw nfe;
	  }
  }
  
  private void setAllColumnsToDefaultRenderers() {
	  TableColumnModel tcm = jTable.getColumnModel();
	  for(int c = 1; c < tcm.getColumnCount(); c++) {
		  TableColumn tc = tcm.getColumn(c);
		  tc.setCellRenderer(new DefaultTableCellRenderer());
	  }
  }
  
  @SuppressWarnings("unchecked")
  private void findMenu_actionPerformed(ActionEvent e) {
	  try{
		  String geneName = "";
		  geneName = JOptionPane.showInputDialog(this,"Please enter the gene name: ");
		  if (geneName != null) {
			  //ListSelectionModel selectionModel = jTable.getSelectionModel();
			  DefaultTableModel dtm = jTable.getDefaultTableModel();
			  Vector vofv = dtm.getDataVector();
			  for(int i = 0; i < vofv.size(); i++) {
				  Vector curr = (Vector)vofv.elementAt(i);
				  Object nameObj = curr.elementAt(0);
				  if(nameObj instanceof String)
				  {
					  String name = (String)nameObj;
					  if(name.equalsIgnoreCase(geneName)) {
						  //System.out.println("Attempting to select row " + i);
						  //selectionModel.setSelectionInterval(i, i+1);
						  jTable.setColumnSelectionInterval(0, 0);
						  jTable.setRowSelectionInterval(i, i);
						  //System.out.println(jTable.isCellSelected(i, 0));
						  Rectangle theSelectedCell = jTable.getCellRect(i, 0, false);
						  jTable.scrollRectToVisible(theSelectedCell);
						  
						  break;
					  }
				  }
				  else {
					  System.out.println("nameObj was not String");
				  }
			  }
		  }
	  }catch(Exception e2){
		  System.out.println("try in findMenu_actionPerformed failed");
		  e2.printStackTrace();
	  }
  }
  
  /**
   * Highlights the top 10 entries in each data column of the table.<br/>
   * <b>Deprecated.</b> Use <code>highlightTopN(int n)</code> instead.
   */
  @Deprecated
  protected void highlightTopAndBottomN() {
	  highlightTopAndBottomN(10,true);
  }
  
  /**
   * Marks the top <i>n</i> entries in the <i>m</i>th column of the table blue
   * @param n how many entries to mark
   * @param m the column to mark
   * @param blueOrange whether or not to use the blue/orange color scheme (if false, use the red/green color scheme)
   */
  @SuppressWarnings("unchecked")
  protected void highlightTopAndBottomNInColumnM(int n, int m, boolean blueOrange) {
	  DefaultTableModel defaulttablemodel = jTable.getDefaultTableModel();
	  Vector<GeneNameRowAndRatio> ratioDataVector = new Vector<GeneNameRowAndRatio>();
	  Vector tableDataVector = defaulttablemodel.getDataVector();
	  for(int i = 0; i < tableDataVector.size(); i++) {
		  if (tableDataVector.elementAt(i) instanceof Vector) {
			  Vector currentRow = (Vector)tableDataVector.elementAt(i);
			  String dataPointString = currentRow.elementAt(m).toString();
			  String name = currentRow.elementAt(0).toString();
			  try{
				  double dataPoint = Double.parseDouble(dataPointString);
				  GeneNameRowAndRatio curr = new GeneNameRowAndRatio(name, i, dataPoint);
				  ratioDataVector.add(curr);
			  }
			  catch(NumberFormatException e) {
				  System.out.println("Unable to parse element "+i+" as a double. The element was " + dataPointString);
				  return;
			  }
		  }
		  else{
			  System.out.println("tableDataVector.elementAt(" + i + ") is not instanceof Vector. How odd.");
			  return;
		  }
	  }
	  GeneNameRowAndRatio[] ratioDataArray = new GeneNameRowAndRatio[ratioDataVector.size()];
	  ratioDataArray = ratioDataVector.toArray(ratioDataArray);
	  Arrays.sort(ratioDataArray, GeneNameRowAndRatio.GENENAMEROWANDRATIO_COMPARE);
	  int arrSz = ratioDataArray.length;
	  int [] topN = new int[n];
	  int counter = 0;
	  /*for (int i = arrSz - n; i < arrSz; i++) {
		topN[counter] = ratioDataArray[i].row;
		counter++;
	  }*/
	  int arrCount = arrSz-1;
	  while((counter < n) && (arrCount >= 0)) {
		  if(ratioDataArray[arrCount].ratio < 997.0) {
			  //This is a legal ratio
			  topN[counter] = ratioDataArray[arrCount].row;
			  counter++;
		  }
		  arrCount--;
	  }
	  //now, for the bottom n
	  arrCount = 0;
	  counter = 0;
	  int [] bottomN = new int[n];
	  while((counter < n) && (arrCount < arrSz)) {
		  if(ratioDataArray[arrCount].ratio < 997.0) {	//this check shouldn't be necessary, but we'll do it anyway
			  //this is a legal ratio
			  bottomN[counter] = ratioDataArray[arrCount].row;
			  counter++;
		  }
		  arrCount++;
	  }
	  /*
	  //begin silly output things 
	  System.out.print("Ratio Rows: ");
	  for(GeneNameRowAndRatio i : ratioDataArray)System.out.print(i.row + " ");
	  System.out.print("\n");
	  System.out.print("Top " + n + " Rows: ");
	  for(int i : topN) System.out.print(i + " ");
	  System.out.print("\n");
	  System.out.print("Bottom " + n + " Rows :");
	  for(int i : bottomN) System.out.print(i + " ");
	  System.out.print("\n");
	  //end silly output things
	  */
	  TableColumnModel colModel = jTable.getColumnModel();
	  TableColumn column = colModel.getColumn(m);
	  column.setCellRenderer(new HighlightTopAndBottomCellsRenderer(topN,bottomN,blueOrange));
	  jTable.repaint();
  }
  
  /**
   * Marks the top and bottom <i>n</i> entries in the table blue
   * @param n how many entries to mark
   * @param blueOrange whether or not to use the blue/orange color scheme (if false, use the red/green color scheme)
   */
  protected void highlightTopAndBottomN(int n, boolean blueOrange) {
	  for(int i = 1; i < jTable.getColumnCount(); i++) highlightTopAndBottomNInColumnM(n,i, blueOrange);	  
  }
  
  protected class BadCellNumberException extends IllegalArgumentException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1119840852366732162L;
	
	public BadCellNumberException() {}
	  
  }
  
  protected class NegativeNumberException extends IllegalArgumentException {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 7420794514592396659L;

	public NegativeNumberException() {}
  }
  
  protected class BadColorSchemeException extends IllegalArgumentException {
	  /**
	   * 
	   */
	  private static final long serialVersionUID = 42L;
	  
	  public BadColorSchemeException() {}
  }
}