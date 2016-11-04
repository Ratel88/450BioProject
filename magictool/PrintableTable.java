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

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageConverter;

import java.awt.Component;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.*;

/**
 * PrintableTable is a printable JTable which displays data from an expression file.
 * The background can be a normal white background or can be colored based on the data
 * in either a red-green scale or in grayscale. The data can be limited to a group of genes
 * from a group file within an expression file and the option exists to show or hide both
 * the data and information about the genes.
 */
@SuppressWarnings("unchecked")
public class PrintableTable extends JTable implements Printable {

  /**
	 * 
	 */
	private static final long serialVersionUID = -6445126734452500927L;
/**normal white background for the table*/
  public static final int NORMAL=0;
  /**grayscale shaded background for the tabel depending on the data values*/
  public static final int GRAYSCALE = 1;
  /**red-green scale shaded background for the tabel depending on the data values*/
  public static final int REDGREEN=2;
  /**table model which holds the data for the table*/
  protected DefaultTableModel defaulttablemodel = new DefaultTableModel();
  /**list of genes displayed in the table in the order they are displayed*/
  protected Vector group;
  /**type of background for the table*/
  protected int type;
  /**expression file of data in the table*/
  protected ExpFile exp;
  /**whether or not the expression file data is shown for the genes*/
  protected boolean showData=true;
  /**whether or not the information about the genes*/
  protected boolean showInfo=false;
  /**component to be placed above the printable table (for printing and saving purposes)*/
  protected JComponent header=null;
  /**component to be placed to the left of the printable table (for printing and saving purposes)*/
  protected JComponent sidebar=null;
  /**decimal format for the table*/
  protected DecimalFormat df = new DecimalFormat("0.################");

  /**
   * Constructs a printable table for all the genes in the expression file showing only
   * the gene data and not the gene information. The type of background must be specified
   * by the user.
   * @param expMain expression file to be displayed in the table
   * @param type type of background for the table
   */
  public PrintableTable(ExpFile expMain, int type){
    this(expMain, new Vector(),type,true,false);
  }

  /**
   * Constructs a printable table for user specified list of genes in the expression file showing only
   * the gene data and not the gene information. The type of background must also be specified
   * by the user.
   * @param expMain expression file to be displayed in the table
   * @param group list of genes from the expression file to be displayed in order in the table
   * @param type type of background for the table
   */
  public PrintableTable(ExpFile expMain, Vector group, int type){
    this(expMain, group,type,true,false);
  }

  /**
   * Constructs a printable table for user specified list of genes in the expression file.
   * The gene data and gene information are shown depending about the user specified parameters.
   * The type of background must also be specified by the user.
   * @param expMain expression file to be displayed in the table
   * @param group list of genes from the expression file to be displayed in order in the table
   * @param type type of background for the table
   * @param showData whether or not to show the gene data in the table
   * @param showInfo whether or not to show the gene information in the table
   */
  public PrintableTable(ExpFile expMain, Vector group,int type, boolean showData, boolean showInfo){
    super();
    this.exp=expMain;
    this.group=group;
    this.setModel(defaulttablemodel);
    this.type=type;
    this.showInfo=showInfo;
    this.showData=showData;
    this.getTableHeader().setReorderingAllowed(false);
    this.setRowSelectionAllowed(true);


    defaulttablemodel.addColumn(new String("Gene Name"));
    Object genes[] = expMain.getGeneVector();
    Object labels[] = expMain.getLabelArray();

    if(showData){
      for(int count = 0; count<labels.length; count++){
        defaulttablemodel.addColumn(labels[count]);
      }
    }

    if(showInfo){
      defaulttablemodel.addColumn("Comments");
      defaulttablemodel.addColumn("Alias");
      defaulttablemodel.addColumn("Chromosome");
      defaulttablemodel.addColumn("Location");
      defaulttablemodel.addColumn("Biological Process");
      defaulttablemodel.addColumn("Molecular Function");
      defaulttablemodel.addColumn("Cellular Component");
    }

    setGroup(group);
    setDoubleBuffered(false);
  }
  
  private void makeNewTableModel() {
	  DefaultTableModel newDTM = new DefaultTableModel();
	  newDTM.addColumn(new String("Gene Name"));
	  Object genes[] = exp.getGeneVector();
	  Object labels[] = exp.getLabelArray();
	  
	  if (showData) {
		  for (int count = 0; count < labels.length; count++) {
			  newDTM.addColumn(labels[count]);
		  }
	  }
	  
	  if (showInfo) {
	      newDTM.addColumn("Comments");
	      newDTM.addColumn("Alias");
	      newDTM.addColumn("Chromosome");
	      newDTM.addColumn("Location");
	      newDTM.addColumn("Biological Process");
	      newDTM.addColumn("Molecular Function");
	      newDTM.addColumn("Cellular Component");
	  }
	  newDTM.getDataVector().removeAllElements();
      if(group.isEmpty()){
        for(int count = 0; count<exp.numGenes(); count++){
            Gene g = exp.getGene(count);
            int total=1;
            double[] nums = g.getData();
            if(showData) total+=nums.length;
            if(showInfo) total+=7;
            Object[] data = new Object[total];
            data[0] = g.getName();
            int pos=1;
            if(showData){
              for(int count2 = 0; count2<nums.length; count2++){
                if(nums[count2]!=Double.POSITIVE_INFINITY&&nums[count2]!=Double.NEGATIVE_INFINITY&&nums[count2]!=Double.NaN){
                  data[count2+1] = new Double(df.format(nums[count2]));
                }
                else data[count2+1]="Missing";
                pos++;
              }
            }
            if(showInfo){
              data[pos]=(g.getComments()!=null?g.getComments():"");
              data[pos+1]=(g.getAlias()!=null?g.getAlias():"");
              data[pos+2]=(g.getChromo()!=null?g.getChromo():"");
              data[pos+3]=(g.getLocation()!=null?g.getLocation():"");
              data[pos+4]=(g.getProcess()!=null?g.getProcess():"");
              data[pos+5]=(g.getFunction()!=null?g.getFunction():"");
              data[pos+6]=(g.getComponent()!=null?g.getComponent():"");
            }
            newDTM.addRow(data);
        }
      }

      else{
        for(int count = 0; count<group.size(); count++){
        		// the gene names in the vector "group" have a trailing whitespace char.
        		// added ".trim()" method to fix it... for now.  Why is it there, though?
        		// Think it was there as a delimiter for a StringTokenizer that was replaced
        		// by a readUTF() method.
             // int pos = exp.findGeneName(group.elementAt(count).toString().trim());
            int pos = exp.findGeneName(group.elementAt(count).toString());
            if(pos!=-1){
              Gene g = exp.getGene(pos);
              int total=1;
              double[] nums = g.getData();
              if(showData) total+=nums.length;
              if(showInfo) total+=7;
              Object[] data = new Object[total];
              data[0] = g.getName();
              int p=1;
              if(showData){
                for(int count2 = 0; count2<nums.length; count2++){
                  if(nums[count2]!=Double.POSITIVE_INFINITY&&nums[count2]!=Double.NEGATIVE_INFINITY&&nums[count2]!=Double.NaN){
                    data[count2+1] = new Double(df.format(nums[count2]));
                  }
                  else data[count2+1]="Missing";
                  p++;
                }
              }
              if(showInfo){
                data[p]=(g.getComments()!=null?g.getComments():"");
                data[p+1]=(g.getAlias()!=null?g.getAlias():"");
                data[p+2]=(g.getChromo()!=null?g.getChromo():"");
                data[p+3]=(g.getLocation()!=null?g.getLocation():"");
                data[p+4]=(g.getProcess()!=null?g.getProcess():"");
                data[p+5]=(g.getFunction()!=null?g.getFunction():"");
                data[p+6]=(g.getComponent()!=null?g.getComponent():"");
              }
              newDTM.addRow(data);
            }
        }

      }
	  defaulttablemodel = newDTM;
	  defaulttablemodel.fireTableStructureChanged();
	  defaulttablemodel.fireTableDataChanged();
	  this.setModel(defaulttablemodel);
  }

/**
 * returns the default table model for this table
 * @return the default table model for this table
 */
  public DefaultTableModel getDefaultTableModel() {
	  return defaulttablemodel;
  }
  
  /**
   * sets the type of background for the table
   * @param type type of background for the table
   */
  public void setType(int type){
    this.type=type;
  }

  /**
   * returns the type of background for the table
   * @return type of background for the table
   */
  public int getType(){
    return type;
  }

  /**
   * gets the component to be placed above the printable table (for printing and saving purposes)
   * @return component to be placed above the printable table (for printing and saving purposes)
   */
  public JComponent getHeader(){
    return header;
  }

   /**
   * gets the component to be placed to the left of the printable table (for printing and saving purposes)
   * @return component to be placed to the left of the printable table (for printing and saving purposes)
   */
  public JComponent getSideBar(){
    return sidebar;
  }

  /**
   * sets the component to be placed above the printable table (for printing and saving purposes)
   * @param header component to be placed above the printable table (for printing and saving purposes)
   */
  public void setHeader(JComponent header){
    this.header = header;
  }

  /**
   * sets the component to be placed to the left of the printable table (for printing and saving purposes)
   * @param sidebar component to be placed to the left of the printable table (for printing and saving purposes)
   */
  public void setSidebar(JComponent sidebar){
    this.sidebar = sidebar;
  }

  /**
   * returns whether or not the gene data is shown in the table
   * @return whether or not the gene data is shown in the table
   */
  public boolean showData(){
    return showData;
  }

  /**
   * returns whether or not the gene information is shown in the table
   * @return whether or not the gene information is shown in the table
   */
  public boolean showInfo(){
    return showInfo;
  }

  public void setShowInfo(boolean info) {
	  showInfo = info;
	  makeNewTableModel();
  }
  
  /**
   * paints an all white background table for desired height of each cell
   * @param pix height of each cell
   */
  public void paintTable(int pix){
    paintTable(0, 0, 0, pix);
  }

  /**
   * paints the table using the appropriate background based on the desired values for each color
   * in the gradient and the desired height of each cell
   * @param minvalue value for first color in the background gradient
   * @param center value for center color in the background gradient
   * @param maxvalue value for last color in the background gradient
   * @param pix height of each cell
   */
  public void paintTable(float minvalue, float center, float maxvalue, int pix){
    float range = maxvalue - minvalue;
    setRowHeight(pix);
    grayRenderer grenderer = new grayRenderer(minvalue,center, maxvalue);
    redGreenRenderer rgrenderer = new redGreenRenderer(minvalue,center, maxvalue);

      TableColumnModel tcm = getColumnModel ();

      // For each table column, sets its renderer to the previously
      // created header renderer.

        for (int c = 1; c < tcm.getColumnCount()-(showInfo?3:0); c++)
        {
             TableColumn tc = tcm.getColumn (c);
             if(type==GRAYSCALE) tc.setCellRenderer (grenderer);
             else if(type==REDGREEN) tc.setCellRenderer (rgrenderer);
             else tc.setCellRenderer(new DefaultTableCellRenderer());
        }

      repaint();

  }

  /**
   * sets the size of each column to fit all the information in each column
   */
  public void setColumnsToFit(){
    this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    FontMetrics fm = this.getFontMetrics(this.getFont());
    int width = 0;
    for (int i=0; i<getColumnCount(); i++) {
      TableColumn tc = getColumn(getColumnName(i));
      width = maxWidth(tc,fm);
      tc.setPreferredWidth(width);
    }
  }

  //finds the maximum width of a column for use in fitting all the columns
  private int maxWidth(TableColumn col, FontMetrics fm) {
    int headerWidth = fm.stringWidth((String)col.getHeaderValue());
    int columnNumber = col.getModelIndex();
    int max = headerWidth;
    int columnWidth = 0;
    String cell;
    for (int i=0; i<this.getRowCount(); i++) {
      cell = getValueAt(i,columnNumber).toString();
      if(cell!=null){
        columnWidth = fm.stringWidth(cell)+5;
        if (columnWidth > max) max = columnWidth;
      }
    }

    return max+5;
  }

  /**
   * sets the list of genes displayed in the table in order
   * @param group list of genes displayed in the table in order
   */
  public void setGroup(Vector group){

      defaulttablemodel.getDataVector().removeAllElements();
      if(group.isEmpty()){
        for(int count = 0; count<exp.numGenes(); count++){
            Gene g = exp.getGene(count);
            int total=1;
            double[] nums = g.getData();
            if(showData) total+=nums.length;
            if(showInfo) total+=7;
            Object[] data = new Object[total];
            data[0] = g.getName();
            int pos=1;
            if(showData){
              for(int count2 = 0; count2<nums.length; count2++){
                if(nums[count2]!=Double.POSITIVE_INFINITY&&nums[count2]!=Double.NEGATIVE_INFINITY&&nums[count2]!=Double.NaN){
                  data[count2+1] = new Double(df.format(nums[count2]));
                }
                else data[count2+1]="Missing";
                pos++;
              }
            }
            if(showInfo){
              data[pos]=(g.getComments()!=null?g.getComments():"");
              data[pos+1]=(g.getAlias()!=null?g.getAlias():"");
              data[pos+2]=(g.getChromo()!=null?g.getChromo():"");
              data[pos+3]=(g.getLocation()!=null?g.getLocation():"");
              data[pos+4]=(g.getProcess()!=null?g.getProcess():"");
              data[pos+5]=(g.getFunction()!=null?g.getFunction():"");
              data[pos+6]=(g.getComponent()!=null?g.getComponent():"");
            }
            defaulttablemodel.addRow(data);
        }
      }

      else{
        for(int count = 0; count<group.size(); count++){
        		// the gene names in the vector "group" have a trailing whitespace char.
        		// added ".trim()" method to fix it... for now.  Why is it there, though?
        		// Think it was there as a delimiter for a StringTokenizer that was replaced
        		// by a readUTF() method.
             // int pos = exp.findGeneName(group.elementAt(count).toString().trim());
            int pos = exp.findGeneName(group.elementAt(count).toString());
            if(pos!=-1){
              Gene g = exp.getGene(pos);
              int total=1;
              double[] nums = g.getData();
              if(showData) total+=nums.length;
              if(showInfo) total+=7;
              Object[] data = new Object[total];
              data[0] = g.getName();
              int p=1;
              if(showData){
                for(int count2 = 0; count2<nums.length; count2++){
                  if(nums[count2]!=Double.POSITIVE_INFINITY&&nums[count2]!=Double.NEGATIVE_INFINITY&&nums[count2]!=Double.NaN){
                    data[count2+1] = new Double(df.format(nums[count2]));
                  }
                  else data[count2+1]="Missing";
                  p++;
                }
              }
              if(showInfo){
                data[p]=(g.getComments()!=null?g.getComments():"");
                data[p+1]=(g.getAlias()!=null?g.getAlias():"");
                data[p+2]=(g.getChromo()!=null?g.getChromo():"");
                data[p+3]=(g.getLocation()!=null?g.getLocation():"");
                data[p+4]=(g.getProcess()!=null?g.getProcess():"");
                data[p+5]=(g.getFunction()!=null?g.getFunction():"");
                data[p+6]=(g.getComponent()!=null?g.getComponent():"");
              }
              defaulttablemodel.addRow(data);
            }
        }

      }
  }

  /**
   * sets the decimal format
   * @param format decimal format for the table
   */
  public void setDecimalFormat(DecimalFormat format){
      this.df=format;
      setGroup(group);
      setColumnsToFit();
  }

  /**
   * gets the current decimal format
   * @return current decimal format
   */
  public DecimalFormat getDecimalFormat(){
    return df;
  }

  /**
   * prints the graph
   * @param g printer graphics
   * @param pageFormat page format of printing
   * @param pageIndex page number
   * @return whether or not the page number exists
   * @throws PrinterException when printing failed
   */
   public int print(Graphics g, PageFormat pageFormat,
    int pageIndex) throws PrinterException {

    Graphics2D g2 = (Graphics2D) g;
    g2.setFont(new Font("Times New Roman", Font.PLAIN, 10));
    g2.setColor(Color.black);
    int fontHeight=g2.getFontMetrics().getHeight();
    int fontDesent=g2.getFontMetrics().getDescent();


    double pageHeight = pageFormat.getImageableHeight();

    double pageWidth = pageFormat.getImageableWidth();
    if(sidebar!=null) pageWidth-=(sidebar.getWidth()+1);

    double tableWidth =(double) getColumnModel().getTotalColumnWidth();
    double scale = 1;
    if (tableWidth >= pageWidth) {
    scale = pageWidth / tableWidth;
    }

    double headerHeightOnPage=getTableHeader().getHeight()*scale+fontHeight+5;
    if(header!=null) headerHeightOnPage+=header.getHeight()+2;
    double tableWidthOnPage=tableWidth*scale;

    double oneRowHeight=(getRowHeight()+getRowMargin())*scale;
    int numRowsOnAPage=(int)Math.floor((pageHeight-headerHeightOnPage)/oneRowHeight);
    double pageHeightForTable=oneRowHeight*numRowsOnAPage;
    int totalNumPages= (int)Math.ceil(((double)getRowCount())/numRowsOnAPage);
    if(pageIndex>=totalNumPages) {
    return NO_SUCH_PAGE;
    }

    g2.translate(pageFormat.getImageableX(),pageFormat.getImageableY());

    String s="";
    g2.drawString((s="Page: "+(pageIndex+1)),(int)pageWidth-g2.getFontMetrics().stringWidth(s),
    (int)(fontHeight-fontDesent));//top right

    s=exp.getExpFile().getName() + " - " + exp.numGenes() + " Genes " + (group.size()>0?" - Group Of " + group.size() + " Genes":"");
    g2.drawString(s,0,(int)(fontHeight-fontDesent));//top left

    if(header!=null){
      g2.translate(0f, fontHeight+3);
      g2.scale(pageWidth/header.getWidth(),1);
      header.paint(g2);
      g2.translate(0f, -fontHeight-3);
      g2.scale(header.getWidth()/pageWidth,1);
    }

    g2.translate(0f,headerHeightOnPage);
    g2.translate(0f,-pageIndex*pageHeightForTable);

    if(sidebar!=null){
      g2.setClip(0, (int)(pageHeightForTable*pageIndex),sidebar.getWidth(),(int) Math.ceil(pageHeightForTable));

      g2.scale(1,scale);
      sidebar.paint(g2);
      g2.scale(1,1/scale);
    }

    g2.setClip((sidebar==null?0:sidebar.getWidth()), (int)(pageHeightForTable*pageIndex),(int)
    Math.ceil(tableWidthOnPage),(int) Math.ceil(pageHeightForTable));

    g2.scale(scale,scale);
    paint(g2);
    g2.scale(1/scale,1/scale);
    g2.translate(0f,pageIndex*pageHeightForTable);
    g2.translate(0f, -headerHeightOnPage+fontHeight+5+(header==null?0:header.getHeight())+2);
    g2.setClip(0, 0,(int) Math.ceil(tableWidthOnPage),(int)Math.ceil(headerHeightOnPage-fontHeight-5));
    g2.scale(scale,scale);
    getTableHeader().paint(g2);//paint header at top

    return Printable.PAGE_EXISTS;
    }

    public String getGeneName(int row){
      return group.get(row).toString();
    }

    /**
     * gets the number of megapixels for a saved image
     * @return number of megapixels for a saved image
     */
    public double getMegaPixels(){
      Font f = new Font("Dialog", Font.PLAIN, 10);
      int h = this.getRowHeight();
      int headerHeight=getTableHeader().getHeight()+this.getGraphics().getFontMetrics(f).getHeight()+5;
      if(header!=null) headerHeight+=header.getHeight()+2;
      int w = getColumnModel().getTotalColumnWidth();
      if(sidebar!=null) w+=sidebar.getWidth();

      int th = h*this.getRowCount()+headerHeight;

      return(((double)w)/1000)*(((double)th)/1000);
    }

  /**
   * saves a gif image of the display
   * @param name new filename
   * @param number number of images
   * @throws IOException if error creating gif image
   */
  public void saveImage(String name, int number) throws IOException{
      Font f = new Font("Dialog", Font.PLAIN, 10);
      int h = this.getRowHeight();
      int headerHeight=getTableHeader().getHeight()+this.getGraphics().getFontMetrics(f).getHeight()+5;
      if(header!=null) headerHeight+=header.getHeight()+2;
      int w = getColumnModel().getTotalColumnWidth();
      if(sidebar!=null) w+=sidebar.getWidth();

      int th = h*this.getRowCount()+headerHeight;

      int biheight = (int)Math.ceil((double)th/number);

      Image bi = createImage(w, biheight);
      Graphics image = bi.getGraphics();
      Graphics2D image2 = (Graphics2D) image;

      try{
            String n="";
            if (number>1){
              n = name.substring(0, name.lastIndexOf(".gif"));
              String na = n.substring(n.lastIndexOf(File.separator));
              n = n + "_images";
              File f1 = new File(n);
              f1.mkdir();
              n+=na;

            }

        for(int i=0; i<number; i++){

          image2.setFont(f);

          image2.setColor(Color.white);
          image2.fillRect(0,0,w,biheight);

          image2.translate(0,-i*biheight);

          FontMetrics fm = image2.getFontMetrics();
          image2.setColor(new Color(0,0,0));


          image2.drawString(exp.getExpFile().getName() + " - " + exp.numGenes() + " Genes " + (group.size()>0?" - Group Of " + group.size() + " Genes":""),5,fm.getHeight()-fm.getDescent());


          image2.translate(0f, fm.getHeight()+3);
          if(header!=null){
            header.paint(image2);
            image2.translate(0f, header.getHeight()+3);
          }

          if(sidebar!=null){
            image2.translate(0f,getTableHeader().getHeight());
            sidebar.paint(image2);
            image2.translate(sidebar.getWidth()+1, -getTableHeader().getHeight());
          }

          getTableHeader().paint(image2);
          image2.translate(0f, getTableHeader().getHeight());
          paint(image2);

          image2.translate(0f, -(fm.getHeight()+3+getTableHeader().getHeight()));
          image2.translate(0,i*biheight);

          if(header!=null){
            image2.translate(0f, -(header.getHeight()+3));
          }

          if(sidebar!=null){
            image2.translate(-(sidebar.getWidth()+1), 0);
          }

          File theFile;
          if(number==1)theFile = new File(name);
          else theFile = new File(n + i + ".gif");


          ImagePlus gifImage = new ImagePlus("NewGif", bi);
          ImageConverter converter = new ImageConverter(gifImage);
          converter.convertRGBtoIndexedColor(256);
          FileSaver fileSaver = new FileSaver(gifImage);
          try{

              fileSaver.saveAsGif(theFile.getPath());

          }
          catch(Exception e2){
            JOptionPane.showMessageDialog(null, "Error Writing .gif File - "+e2);
          }
        }
      }
      catch(Exception e){
        e.printStackTrace();
        throw new IOException();
      }
  }



  /**
   * renderer for a grayscale background dependent upon the values of the data
   */
  private class grayRenderer extends DefaultTableCellRenderer{
	  /**
	   * 
	   */
	  private static final long serialVersionUID = 2857719053996140395L;
	  private float center;
	  private float maxvalue;
	  private float unit1, unit2;
	  private float minvalue;

	  public grayRenderer(){
		  this(0,500,1000);
	  }

	  public grayRenderer(float minvalue, float center,float maxvalue){
		  this.center = center;
		  this.maxvalue = maxvalue;
		  this.minvalue = minvalue;
		  unit1 = 128/(center-minvalue);
		  unit2 = 127/(maxvalue-center);
	  }
	  public void setValue (Object v)
	  {
		  super.setValue (v);
		  if (v == null)
		  {
			  setForeground (Color.white);
			  setBackground (Color.black);
			  return;
		  }
		  int grayValue;
		  try{
			  Double s = (Double) v;
			  float cellValue = s.floatValue();
			  if(cellValue<center) grayValue = 255 - Math.round((cellValue-minvalue)*unit1);
			  else grayValue = Math.round((maxvalue-cellValue)*unit2);
			  if(grayValue>255)grayValue = 255;
			  if(grayValue<0)grayValue = 0;

			  setBackground(new Color(grayValue,grayValue,grayValue));
		  }
		  catch(Exception e1){
			  grayValue=255;
			  //setBackground(Color.yellow);	//removed 6-21-2007 by Michael Gordon
			  setBackground(Color.white);
		  }
		  if(getRowHeight()>=10) setForeground((grayValue>255/2?Color.black:Color.white));
		  else setForeground(getBackground());
	  }
  }
  
  /**
   * renderer for a red-green scale background dependent upon the values of the data
   */
  private class redGreenRenderer extends DefaultTableCellRenderer{
	  /**
	   * 
	   */
	  private static final long serialVersionUID = 3012358057213983345L;
	  private float center;
	  private float maxvalue;
	  private float unit1, unit2;
	  private float minvalue;

	  public redGreenRenderer(){
		  this(0,500,1000);
	  }

	  public redGreenRenderer(float minvalue, float center,float maxvalue){
		  this.center = center;
		  this.maxvalue = maxvalue;
		  this.minvalue = minvalue;
		  unit1 = 255/(center-minvalue);
		  unit2 = 255/(maxvalue-center);
	  }
	  
	  public void setValue (Object v)
	  {
		  super.setValue (v);

		  if (v == null)
		  {
			  setForeground (Color.white);
			  setBackground (Color.black);

			  return;
		  }

		  // Extract the cell's data.

		  int colorValue;
		  try{
			  Double s = (Double) v;
			  float cellValue = s.floatValue();

			  if(cellValue<center) colorValue = 255 - Math.round((cellValue-minvalue)*unit1);
			  else colorValue = 255 - Math.round((maxvalue - cellValue)*unit2);
			  if(colorValue>255)colorValue = 255;
			  if(colorValue<0)colorValue = 0;

			  if(cellValue<center) setBackground(new Color(0,colorValue,0));
			  else setBackground(new Color(colorValue,0,0));

		  }catch(Exception e1){
			  colorValue=153;
			  //setBackground(Color.yellow);	//removed 6-21-07 by Michael Gordon
			  setBackground(Color.white);
		  }

		  if(getRowHeight()>=10) setForeground((colorValue>=255/2?Color.black:Color.white));
		  else setForeground(this.getBackground());
	  }
  }
}