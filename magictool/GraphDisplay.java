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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 * GraphDisplay displays a printable graph of data specified by a user in a JPanel.
 * GraphDisplay is capable of zooming, undoing zooming, selecting specific data,
 * and drawing best fit least squares regression lines.
 */
public class GraphDisplay extends JPanel implements Printable {

  /**
	 * WTH
	 */
	private static final long serialVersionUID = 1L;
/**x-axis label*/
  protected String labelX;
  /**y-axis label*/
  protected String labelY;
  /**title label*/
  protected String title;
  /**vector of data sets*/
  protected Vector data;
  /**vector of data points on the screen*/
  protected Vector graphPoints;
  /**vector of all regression lines*/
  protected Vector regressions;
  /**vector of selected data sets*/
  protected Vector selectedData;
  /**visible screen edge point*/
  protected double xmin=-1,xmax=-1,ymin=-1,ymax=-1;
  /**last screen edge point*/
  protected double xlmin=-1,xlmax=-1,ylmin=-1,ylmax=-1;
  /**original screen edge point*/
  protected double xomin=-1,xomax=-1,yomin=-1,yomax=-1;
  /**x-axis labels in place of numbers*/
  protected String xMarks[];
  /**size of graph data point*/
  protected int pointSize=6;
  /**shape of graph data point*/
  protected int pointShape=GraphPoint.SQUARE;
  /**whether to use the x-axis labels or to use numbers*/
  protected boolean useXLabels=false;
  private Font titleFont = new Font("Dialog", Font.BOLD, 12);
  private Font axisFont = new Font("Dialog", Font.PLAIN, 10);
  /**y-axis width*/
  protected int yaxisWidth=0;
  /**y-axis height*/
  protected int yaxisHeight=0;
  /**height of the title area*/
  protected int titleHeight=0;
  /**x-axis width*/
  protected int xaxisWidth=0;
  /**width of the area to the right of the graph*/
  protected int sideWidth=20;
  /**edge points of zoom box*/
  protected int xBoxEnd=-1, yBoxEnd=-1, xBoxStart=-1, yBoxStart=-1;
  /**whether or not the graph is currently zooming*/
  protected boolean zooming=false;
  /**whether or not the user can undo the last zoom*/
  protected boolean canUndo=false;
  /**name of the expression file*/
  protected String expFileName=null;
  /**name of the selection*/
  protected String selectedGene=null;
  /**color of the regression lines*/
  protected Color regressionColor = Color.blue;
  /**plot frame wherethe GraphDisplay is shown*/
  protected PlotFrame plot=null;

  /**
   * Default contructor of the GraphDisplay
   */
  public GraphDisplay(){
    this("","","");

  }

  /**
   * Constructs a graph with given title, x-axis label, and y-axis label
   * @param title title label
   * @param xLabel x-axis label
   * @param yLabel y-axis label
   */
  public GraphDisplay(String title, String xLabel, String yLabel) {
      labelX=xLabel;
      labelY=yLabel;
      this.title=title;
      data = new Vector();
      regressions = new Vector();
      selectedData = new Vector();
      this.setMinimumSize(new Dimension(250,250));

      try {
        jbInit();
      }
      catch(Exception e) {
        e.printStackTrace();
      }
  }

  /**
   * sets the parent plot frame
   * @param plot parent plot frame
   */
  public void setPlotFrame(PlotFrame plot){
    this.plot=plot;
  }

  /**
   * adds a data set to the graph
   * @param add data set to add
   */
// WTH
@SuppressWarnings("unchecked")
public void addData(GraphData add){
    data.add(add);
  }

   /**
    * adds a data set to the graph
    * @param name name of the data set or gene
    * @param values data values
    */
// WTH
@SuppressWarnings("unchecked")
public void addData(String name, int[] values){
    GraphData add = new GraphData(name, values);
    data.add(add);
  }

  /**
    * adds a data set to the graph
    * @param name name of the data set or gene
    * @param values data values
    */
// WTH
@SuppressWarnings("unchecked")
public void addData(String name, double[] values){
    GraphData add = new GraphData(name, values);
    data.add(add);
  }

  /**
    * adds a data set to the graph
    * @param name name of the data set or gene
    * @param xvalues data x-values
    * @param yvalues data y-values
    */
//WTH
@SuppressWarnings("unchecked")
 public void addData(String name, double[] xvalues, double[] yvalues){
    DPoint points[] = new DPoint[Math.min(xvalues.length,yvalues.length)];
    for(int i=0; i<Math.min(xvalues.length,yvalues.length); i++){
      points[i] = new DPoint(xvalues[i],yvalues[i]);
    }
    GraphData add = new GraphData(name, points);
    data.add(add);
  }

  /**
    * adds a data set to the graph
    * @param name name of the data set or gene
    * @param values data values
    */
//WTH
@SuppressWarnings("unchecked")
  public void addData(String name, DPoint[] values){
    GraphData add = new GraphData(name, values);
    data.add(add);
  }

  /**
   * selects a data set with the given name
   * @param dataName name of the data set
   */
//WTH
@SuppressWarnings("unchecked")
  public void selectData(String dataName){
    selectedData = new Vector();
    for(int pos=0; pos<data.size(); pos++){
      GraphData gd = (GraphData)data.elementAt(pos);
      if(gd.getName().equalsIgnoreCase(dataName)){
        gd.setSelected(true);
        selectedData.addElement(gd);
        data.setElementAt(gd,pos);
        for(int j=0; j<data.size(); j++){
          if(j!=pos){
            GraphData gd2 = (GraphData)data.elementAt(j);
            if(gd2.isSelected()){
              gd2.setSelected(false);
              data.setElementAt(gd2,j);
            }
          }
        }
        this.repaint();
        break;
      }
    }
  }

  public double[] getRegressionData(int number){
    if(regressions.size()>number){
      Regression r = (Regression)regressions.get(number);
      double rd[] = {r.getIntercept(), r.getSlope(), r.getR2()};
      return rd;
    }
    else return null;
  }

  /**
   * gets the array of selected genes
   * @return array of selected genes
   */
  public String[] getSelectedData(){
    String[] selectedGenes = new String[selectedData.size()];
    for(int i=0; i<selectedGenes.length; i++)
      selectedGenes[i] = ((GraphData)selectedData.get(i)).getName();
    return selectedGenes;
  }

  /**
   * selects all the data sets with the given names in the arrays
   * @param dataNames data set names to select
   */
//WTH
  @SuppressWarnings("unchecked")
  public void selectData(String[] dataNames){
    selectedData = new Vector();
    for(int pos=0; pos<data.size(); pos++){
      GraphData gd = (GraphData)data.elementAt(pos);
      boolean s= false;
      for(int i=0; i<dataNames.length; i++){
        if(gd.getName().equalsIgnoreCase(dataNames[i])){
          gd.setSelected(true);
          selectedData.addElement(gd);
          data.setElementAt(gd,pos);
          s=true;
          break;
        }
      }
      if(!s) gd.setSelected(false);
    }
    this.repaint();
  }

  /**
   * paints the graph on the screen
   * @param g graphics to paint the graph on
   */
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    calculateCoords(g);
    drawGraph(g);
    drawYAxis(g);
    drawXAxis(g);
    drawTitle(g);
    g.setColor(Color.blue);
    if(xBoxStart!=xBoxEnd&&yBoxStart!=yBoxEnd){
      g.drawRect(Math.min(xBoxStart,xBoxEnd),Math.min(yBoxStart,yBoxEnd),Math.abs(xBoxEnd-xBoxStart),Math.abs(yBoxEnd-yBoxStart));
    }
  }

  /**
   * returns whether there is an undoable zoom
   * @return whether there is an undoable zoom
   */
  public boolean canUndo(){
    return canUndo;
  }

  /**
   * sets the shape of a graph data points
   * @param shape shape of a graph data points
   */
  public void setPointShape(int shape){
    if(shape==1) this.pointShape=shape;
    else this.pointShape=0;
  }

  /**
   * sets the size of a graph data points
   * @param size size of a graph data points
   */
  public void setPointSize(int size){
    pointSize=size;
  }

  /**
   * sets the name of the expression file
   * @param name name of the expression file
   */
  public void setExpFileName(String name){
    this.expFileName=name;
  }

  /**
   * saves a gif image of the graph to the file specified - the image is an exact duplicate of the screen image
   * @param filename full file path for new image
   * @throws IOException if the image could not be saved to the disk
   */
  public void saveImage(String filename) throws IOException{
    int h = this.getHeight();
    int w = this.getWidth();

    int lines=0;
    if(expFileName!=null) lines++;
    if(selectedGene!=null) lines++;



    Font f = new Font("Dialog", Font.PLAIN, 10);
    int biheight = h+(this.getGraphics().getFontMetrics(f).getHeight())*lines;
    Image bi = createImage(w, biheight);//, BufferedImage.TYPE_INT_RGB);
    Graphics image = bi.getGraphics();
    image.setFont(f);
    FontMetrics fm = image.getFontMetrics();

    GraphDisplay gd = new GraphDisplay(title,labelX,labelY);
    gd.setSize(w,h);
    if(useXLabels) gd.setXLabels(xMarks);
    gd.setGraphSize(xmin,xmax,ymin,ymax);
    gd.setPointShape(this.pointShape);
    gd.setPointSize(this.pointSize);
    for(int i=0; i<data.size(); i++){
      gd.addData((GraphData)data.elementAt(i));
    }
    gd.regressions=regressions;
    gd.regressionColor=regressionColor;
    gd.selectedData=selectedData;
    image.setColor(Color.white);
    image.fillRect(0,0,w,h);
    gd.paintComponent(image);

    image.setColor(Color.white);
    image.fillRect(0,h,w,biheight-h);

    image.setColor(new Color(0,0,0));
    image.setFont(f);
    if(expFileName!=null)image.drawString("Expression File: " + expFileName,5,h+fm.getHeight()-fm.getDescent());
    if(selectedGene!=null)image.drawString(selectedGene,5,h+fm.getHeight()*2-fm.getDescent());


    ImagePlus gifImage = new ImagePlus("NewGif", bi);
    ImageConverter converter = new ImageConverter(gifImage);
    converter.convertRGBtoIndexedColor(256);
    FileSaver fileSaver = new FileSaver(gifImage);
    try{

        fileSaver.saveAsGif(filename);

    }
    catch(Exception e2){
      JOptionPane.showMessageDialog(null, "Error Writing .gif File - "+e2);
    }


  }

  /**
   * clears all the data on the graph
   */
  public void clearData(){
    data=new Vector();
    graphPoints = new Vector();
    regressions = new Vector();
    xmin=xmax=ymin=ymax=-1;
    xomin=xomax=yomin=yomax=-1;
    xlmin=xlmax=ylmin=ylmax=-1;
  }

  /**
   * adds a regression line to the graph
   * @param r regression line to add
   */
//WTH
  @SuppressWarnings("unchecked")
  public void addRegression(Regression r){
    regressions.addElement(r);
  }

  /**
   * adds a regression line to the graph
   * @param gdata array of data sets to create a regression line from
   */
//WTH
  @SuppressWarnings("unchecked")
  public void addRegression(GraphData[] gdata){
    Regression r = new Regression(gdata);
    regressions.addElement(r);
  }

  /**
   * adds a regression line to the graph
   * @param gdata array of data points to create a regression line from
   */
//WTH
  @SuppressWarnings("unchecked")
  public void addRegression(DPoint[] gdata){
    Regression r = new Regression(gdata);
    regressions.addElement(r);
  }

  /**
   * adds a regression line of all the data on the graph
   */
//WTH
  @SuppressWarnings("unchecked")
  public void addRegression(){
    GraphData[] gdata = new GraphData[data.size()];
    for(int i=0; i<data.size(); i++){
      gdata[i] = (GraphData)data.elementAt(i);
    }
    Regression r = new Regression(gdata);
    regressions.addElement(r);
  }

  /**
   * sets the x-axis label
   * @param name x-axis label
   */
  public void setXLabel(String name){
    this.labelX=name;
  }

  /**
   * sets the y-axis label
   * @param name y-axis label
   */
  public void setYLabel(String name){
    this.labelY=name;
  }


  /**
   * sets the title label
   * @param name title label
   */
  public void setTitle(String name){
    this.title=name;
  }


  /**
   * sets the size of the window display for the graph
   * @param xmin minimum x-value displayed
   * @param xmax maximum x-value displayed
   * @param ymin minimum y-value displayed
   * @param ymax maximum y-value displayed
   */
  public void setGraphSize(double xmin, double xmax, double ymin, double ymax){

    //stores old values for undoing
    this.xlmin=this.xmin;
    this.xlmax=this.xmax;
    this.ylmin=this.ymin;
    this.ylmax=this.ymax;


    this.xmin=xmin;
    this.xmax=xmax;
    this.ymin=ymin;
    this.ymax=ymax;

    //sets original values if they were not previously set
    if(xomin==-1&&xomax==-1&&yomin==-1&&yomax==-1){
      this.xomin=xmin;
      this.xomax=xmax;
      this.yomin=ymin;
      this.yomax=ymax;
    }
    canUndo=false;
  }

  /**
   * undo the last change in the graph window
   */
  public void undoGraphSizeChange(){
    if(xlmin!=-1||xlmax!=-1||ylmin!=-1||ylmax!=-1){
        this.xmin=xlmin;
        this.xmax=xlmax;
        this.ymin=ylmin;
        this.ymax=ylmax;
    }
    canUndo=false;
  }

  /**
   * resets the graph to the original size
   */
  public void resetGraph(){
    setGraphSize(xomin,xomax,yomin,yomax);
  }

  /**
   * sets labels for the x-axis
   * @param labels x-axis label names
   */
  public void setXLabels(String[] labels){
    this.xMarks=labels;
    useXLabels=true;
  }

  /**
   * clears the x-axis labels
   */
  public void clearXLabels(){
    this.xMarks=null;
    useXLabels=false;
  }

  /**
   * returns whether or not a point is in the graph
   * @param x x position
   * @param y y position
   * @return whether or not a point is in the graph
   */
  public boolean inGraph(int x, int y){
      return(x>=yaxisWidth&&x<=yaxisWidth+xaxisWidth&&y>=titleHeight&&y<=yaxisHeight+titleHeight);
  }

  /**
   * draws the x-axis
   * @param g graphics to draw the axis on
   */
  protected void drawXAxis(Graphics g){
    xaxisWidth=this.getWidth()-yaxisWidth-sideWidth;
    FontMetrics fm1 = g.getFontMetrics(titleFont);
    int h1 = fm1.getHeight()+4;
    FontMetrics fm2 = g.getFontMetrics(axisFont);
    int h2 = fm2.getHeight()+2;
    int height = h1+h2;
    g.setColor(Color.white);
    g.fillRect(yaxisWidth,this.getHeight()-height,this.getWidth()-yaxisWidth,height);
    g.setColor(Color.black);
    g.setFont(axisFont);
    if(useXLabels){

      //calculates number of labels to show
      int w = getMaxStringWidth(g,axisFont, xMarks);
      int start=(int)Math.ceil(xmin);
      if(start<0) start=0;
      int end = (int)Math.floor(xmax);
      if(end>xMarks.length) end=xMarks.length;
      int num=end-start+1;
      int total=1;
      for(total=1; total<num; total++){
        if(xValueToPos(start+total)-xValueToPos(start)>w) break;
      }

      //draws the axis line
      g.drawLine(yaxisWidth,this.getHeight()-height,this.getWidth()-sideWidth,this.getHeight()-height);

      //draws the labels and tick marks
      for(int i=start; i<=end; i+=total){
        int center = xValueToPos(i);
        g.drawString(xMarks[i],center-fm2.stringWidth(xMarks[i])/2,this.getHeight()-height+h2);
        g.drawLine(center,this.getHeight()-height,center,this.getHeight()-height+2);
      }
    }
    else{
      DecimalFormat df = getDecimalFormat(xmin, xmax);
      int num=10;
      String labels[]= new String[num];

      //calculates the number of labels shown (10 or less)
      for(; num>0; num--){
        labels = new String[num];
        double delta = (xmax-xmin)/(num-1);
        for(int i=0; i<num; i++){
          labels[i] = df.format(xmin+delta*i);
        }
        int w = getMaxStringWidth(g,axisFont,labels);
        if((w*num)<(this.getWidth()-yaxisWidth)) break;
      }
      float deltawidth=(float)(this.getWidth()-yaxisWidth-sideWidth)/(num-1);

      //draws the axis line
      g.drawLine(yaxisWidth,this.getHeight()-height,this.getWidth()-sideWidth,this.getHeight()-height);

      //draws the axis labels and tick marks
      for(int i=0; i<num; i++){
        int center = yaxisWidth + Math.round(i*deltawidth);
        g.drawString(labels[i],center-fm2.stringWidth(labels[i])/2,this.getHeight()-height+h2);
        g.drawLine(center,this.getHeight()-height,center,this.getHeight()-height+2);
      }
    }

    //draws the axis label
    g.setFont(titleFont);
    g.drawString(labelX, yaxisWidth+xaxisWidth/2-fm1.stringWidth(labelX)/2,this.getHeight()-fm1.getDescent()-1);
  }

  /**
   * calculates the widths and heights of the y-axis, x-axis, and title bar
   * @param g screen graphics
   */
  protected void calculateCoords(Graphics g){
    FontMetrics fm1 = g.getFontMetrics(titleFont);
    int h1 = fm1.getHeight()+4;
    FontMetrics fm2 = g.getFontMetrics(axisFont);
    int h2 = fm2.getHeight()+2;
    yaxisHeight = this.getSize().height-h1*2-h2;
    titleHeight=h1;
    int numShow = yaxisHeight/(h2+3);
// WTH
    @SuppressWarnings("unused") 
    float deltaheight = -((float)yaxisHeight)/numShow;
    DecimalFormat df = getDecimalFormat(ymin, ymax);
    double delta = (ymax-ymin)/numShow;
    String labels[] = new String[numShow+1];
    for(int i=0; i<=numShow; i++){
      labels[i]=df.format(ymin+delta*i);
    }
    int w = getMaxStringWidth(g,axisFont,labels);
    yaxisWidth=h1+w+2;
    xaxisWidth=this.getWidth()-yaxisWidth-sideWidth;
  }


  /**
   * draws the y-axis
   * @param g graphics to draw the axis on
   */
  protected void drawYAxis(Graphics g){
    g.setColor(Color.black);
    FontMetrics fm1 = g.getFontMetrics(titleFont);
    int h1 = fm1.getHeight()+4;
    FontMetrics fm2 = g.getFontMetrics(axisFont);
    int h2 = fm2.getHeight()+2;
    yaxisHeight = this.getSize().height-h1*2-h2;

    //calculate number of labels to show
    int numShow = yaxisHeight/(h2+3);
    float deltaheight = -((float)yaxisHeight)/numShow;
    DecimalFormat df = getDecimalFormat(ymin, ymax);
    double delta = (ymax-ymin)/numShow;

    //gets the labels in strings
    String labels[] = new String[numShow+1];
    for(int i=0; i<=numShow; i++){
      labels[i]=df.format(ymin+delta*i);
    }
    int w = getMaxStringWidth(g,axisFont,labels); //calculates longest string
    int width=h1+w+2;

    g.setColor(Color.white);
    g.fillRect(0,0,width,this.getHeight());
    g.setColor(Color.black);

    //draws the axis line
    g.drawLine(width,h1,width,h1+yaxisHeight);

    //draws the labels and tickmarks
    g.setFont(axisFont);
    for(int i=0; i<=numShow; i++){
      int midheight=h1+yaxisHeight+Math.round(deltaheight*i);
      g.drawString(labels[i],width-fm1.stringWidth(labels[i])-2,midheight+(fm2.getHeight())/2-fm2.getDescent());
      g.drawLine(width-2,midheight,width,midheight);
    }

    //draws the axis label
    Graphics2D newg = (Graphics2D) g;
    newg.setColor(Color.black);
    AffineTransform af1 = newg.getTransform();
    newg.setFont(titleFont);
    newg.setFont(newg.getFont().deriveFont(Font.BOLD,12));
    float xp = (float) (fm1.getHeight()/2+4);
    float yp = (float) (h1+yaxisHeight/2+fm1.stringWidth(labelY)/2);
    AffineTransform af2 = AffineTransform.getRotateInstance(-Math.PI/2,xp,yp);
    newg.transform(af2);
    newg.drawString(labelY, xp, yp);
    newg.setTransform(af1);

    yaxisWidth=width;

  }

  /**
   * draws the title bar
   * @param g graphics to draw the title on
   */
  protected void drawTitle(Graphics g){
    g.setFont(titleFont);
    FontMetrics fm = g.getFontMetrics(titleFont);
    titleHeight=fm.getHeight()+4;
    g.setColor(Color.white);
    g.fillRect(yaxisWidth,0,this.getWidth()-yaxisWidth,fm.getHeight()+4);
    g.fillRect(yaxisWidth+xaxisWidth,fm.getHeight()+4,sideWidth,yaxisHeight);
    g.setColor(Color.black);
    g.drawString(title,yaxisWidth+(this.getWidth()-sideWidth-yaxisWidth)/2-fm.stringWidth(title)/2, fm.getHeight()-fm.getDescent()+2);
  }

  /**
   * draws the graph
   * @param g graphics to draw the graph on
   */
// WTH
@SuppressWarnings("unchecked")
protected void drawGraph(Graphics g){
    g.setColor(Color.white);
    g.fillRect(0,0,this.getWidth(), this.getHeight());
    g.setColor(Color.black);
    graphPoints = new Vector();

    //draws each of the graph data points and connects the lines between them
    for(int i=0; i<data.size(); i++){
      GraphData grd = (GraphData)data.elementAt(i);
      GraphPoint last=null;
      for(int j=0; j<grd.dataPoints(); j++){
        if((grd.getX(j)!=Double.POSITIVE_INFINITY&&grd.getX(j)!=Double.NEGATIVE_INFINITY&&grd.getX(j)!=Double.NaN)
          &&(grd.getY(j)!=Double.POSITIVE_INFINITY&&grd.getY(j)!=Double.NEGATIVE_INFINITY&&grd.getY(j)!=Double.NaN)){
              GraphPoint grp = new GraphPoint(xValueToPos(grd.getX(j)),yValueToPos(grd.getY(j)),pointSize,i,j);
              grp.drawPoint(g,(grd.isSelected()?Color.red:Color.black));
              g.setColor((grd.isSelected()?Color.red:Color.black));
              if(j!=0&&last!=null) g.drawLine(grp.x,grp.y,last.x,last.y);
              last=grp;
              graphPoints.add(grp);
        }

        else last=null;
      }
    }

    //draws each of the selected graph data points and connects the lines between them on top and in red
    for(int i=0; i<selectedData.size(); i++){
      GraphData grd = (GraphData)selectedData.elementAt(i);
      GraphPoint last=null;
      for(int j=0; j<grd.dataPoints(); j++){
        if((grd.getX(j)!=Double.POSITIVE_INFINITY&&grd.getX(j)!=Double.NEGATIVE_INFINITY&&grd.getX(j)!=Double.NaN)
          &&(grd.getY(j)!=Double.POSITIVE_INFINITY&&grd.getY(j)!=Double.NEGATIVE_INFINITY&&grd.getY(j)!=Double.NaN)){
              GraphPoint grp = new GraphPoint(xValueToPos(grd.getX(j)),yValueToPos(grd.getY(j)),pointSize,i,j);
              grp.drawPoint(g,(grd.isSelected()?Color.red:Color.black));
              g.setColor((grd.isSelected()?Color.red:Color.black));
              if(j!=0&&last!=null) g.drawLine(grp.x,grp.y,last.x,last.y);
              last=grp;
              graphPoints.add(grp);
        }

        else last=null;
      }
    }

    //draws all the regression lines
    drawRegressionLines(g);
  }

  /**
   * draws the regression lines on the graph
   * @param g graphics to draw the regression lines on
   */
  protected void drawRegressionLines(Graphics g){
    for(int i=0; i<regressions.size(); i++){
      ((Regression)regressions.elementAt(i)).drawRegressionLine(g);
    }
  }

  /**
   * returns the maximum width of an individual string from an array of strings
   * @param g graphics
   * @param f font for strings
   * @param strings array of strings
   * @return maximum width of an individual string from an array of strings
   */
  public int getMaxStringWidth(Graphics g, Font f, String[] strings){
    FontMetrics fm = g.getFontMetrics(f);
    int longest = fm.stringWidth(strings[0]);
    for(int i=1; i<strings.length; i++){
      if(fm.stringWidth(strings[i])>longest) longest=fm.stringWidth(strings[i]);
    }

    return longest;
  }

  /**
   * returns the proper decimal format based on the maximum and minimum size
   * @param min minimum size
   * @param max maximum size
   * @return proper decimal format based on the maximum and minimum size
   */
  protected DecimalFormat getDecimalFormat(double min, double max){
    double dif=Math.abs(max-min);
    int dec=0;
    if(dif<100) dec++;
    if (dif<1){
      while(dif<1){
        dec++;
        dif=dif*10;
      }
    }
    String s = "#";
    for(int i=0; i<dec; i++){
      if(i==0) s+=".";
      s+="#";
    }

    return new DecimalFormat(s);
  }

  /**
   * clears the selected data
   */
//WTH
  @SuppressWarnings("unchecked")
  public void clearSelectedGene(){
      boolean shouldrepaint=false;
      selectedData = new Vector();
      for(int j=0; j<data.size(); j++){
          GraphData gd2 = (GraphData)data.elementAt(j);
          if(gd2.isSelected()){
            shouldrepaint=true;
            gd2.setSelected(false);
            data.setElementAt(gd2,j);
          }
      }
      selectedGene=null;
      if(shouldrepaint) this.repaint();
  }
  
//WTH
  @SuppressWarnings("unchecked")
  public void selectAllGenes(){
	  selectedData=new Vector();
	  for(int i=0; i<data.size(); i++){
		  GraphData gd3=(GraphData)data.elementAt(i);
		  gd3.setSelected(true);
		  data.setElementAt(gd3, i);
		  selectedData.addElement(gd3);
	  }
	  this.repaint();
  }

  /**
   * converts an x-value to a screen coordinate
   * @param value x-value to convert
   * @return screen coordinate of converted x-value
   */
  public int xValueToPos(double value){
    double val = (xmax-xmin)/xaxisWidth;
    double dif = value-xmin;
    return yaxisWidth+Math.round((float)(dif/val));
  }

  /**
   * converts an y-value to a screen coordinate
   * @param value y-value to convert
   * @return screen coordinate of converted y-value
   */
  public int yValueToPos(double value){
    double val = (ymax-ymin)/yaxisHeight;
    double dif = value-ymin;
    return yaxisHeight+titleHeight-Math.round((float)(dif/val));
  }

  /**
   * converts an x-coordinate from the screen into an x-value
   * @param xpos x-coordinate from the screen to convert
   * @return x-value converted from the x-coordinate from the screen
   */
  public double xPosToValue(int xpos){
    double val = (xmax-xmin)/xaxisWidth;
    int dif = xpos-yaxisWidth;
    return xmin+val*dif;
  }

  /**
   * converts an y-coordinate from the screen into an y-value
   * @param ypos y-coordinate from the screen to convert
   * @return y-value converted from the y-coordinate from the screen
   */
  public double yPosToValue(int ypos){
    double val = (ymax-ymin)/yaxisHeight;
    int dif = ypos-titleHeight;
    return ymax-val*dif;
  }

  /**
   * prints the graph - fitting it to a full page
   * @param g print graphics
   * @param pf page formate
   * @param pi page number
   * @return whether or not page index exists
   */
  public int print(Graphics g, PageFormat pf, int pi) {
    if (pi != 0) {
        return Printable.NO_SUCH_PAGE;
      }

      Graphics2D g2 = (Graphics2D) g;

      g2.setFont(new Font("Dialog",Font.PLAIN,10));

      FontMetrics fm = g2.getFontMetrics();


      double pageHeight = pf.getImageableHeight()-fm.getHeight()-5;
      double pageWidth = pf.getImageableWidth();

      GraphDisplay gd = new GraphDisplay(title,labelX,labelY);
      gd.setSize((int)pageWidth,(int)pageHeight);
      if(useXLabels) gd.setXLabels(xMarks);
      gd.setGraphSize(xmin,xmax,ymin,ymax);
      gd.setPointShape(this.pointShape);
      gd.setPointSize(this.pointSize);
      gd.regressions=regressions;
      gd.selectedData=selectedData;
      gd.regressionColor=regressionColor;
      for(int i=0; i<data.size(); i++){
        gd.addData((GraphData)data.elementAt(i));
      }


      g2.translate(pf.getImageableX(), pf.getImageableY());


      if(expFileName!=null)g2.drawString(expFileName,0,fm.getHeight());
      String s="";
      if(selectedGene!=null)g2.drawString((s=selectedGene),(int)pageWidth-fm.stringWidth(s),fm.getHeight());

      if(selectedGene!=null||expFileName!=null)g2.translate(0f,fm.getHeight()+5);

      gd.paintComponent(g2);

      return Printable.PAGE_EXISTS;
  }


  private void jbInit() throws Exception {
    //adds the mouse listeners
    this.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        this_mouseClicked(e);
      }
      public void mouseEntered(MouseEvent e) {
        this_mouseEntered(e);
      }
      public void mouseExited(MouseEvent e) {
        this_mouseExited(e);
      }
      public void mouseReleased(MouseEvent e) {
        this_mouseReleased(e);
      }
      public void mousePressed(MouseEvent e) {
        this_mousePressed(e);
      }
    });
    this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
      public void mouseDragged(MouseEvent e) {
        this_mouseDragged(e);
      }
      public void mouseMoved(MouseEvent e) {
        this_mouseMoved(e);
      }
    });
    this.addKeyListener(new java.awt.event.KeyListener() {
    	public void keyPressed(KeyEvent e) {
    		if (e.getKeyCode() == KeyEvent.VK_CONTROL) zooming = true;
    		//System.out.println("keyPressed fired with keyCode " + e.getKeyCode());
    	}
    	public void keyTyped(KeyEvent e) {
    		
    	}
    	public void keyReleased(KeyEvent e) {
    		this_keyReleased(e);
    		//System.out.println("keyReleased fired with keyCode " + e.getKeyCode());
    	}
    });
  }


  //used for selecting data points
//WTH
  @SuppressWarnings("unchecked")
  private void this_mouseClicked(MouseEvent e) {
    int x = e.getX();
    int y = e.getY();
    if(!e.isShiftDown()) selectedData = new Vector();
      if(inGraph(x,y)){
        for(int i=0; i<graphPoints.size(); i++){
          GraphPoint gp = (GraphPoint)graphPoints.elementAt(i);
          if(gp.contains(x,y)) {
            GraphData gd = (GraphData)data.elementAt(gp.set);
            if(e.isShiftDown()&&gd.isSelected()){

              selectedData.removeElement(gd);
              gd.setSelected(false);
              if(selectedData.size()==0) clearSelectedGene();
              else if(selectedData.size()==1)selectedGene="Selected Gene: " + selectedData.elementAt(0).toString();
              else selectedGene="Selected Genes: " + selectedData.size();
              repaint();
              if(plot!=null)plot.pp_mouseClicked();
              return;
            }
            else{
              gd.setSelected(true);
              data.setElementAt(gd,gp.set);
              selectedData.addElement(gd);
              if(selectedData.size()==1)selectedGene="Selected Gene: " + gd.getName();
              else selectedGene="Selected Genes: " + selectedData.size();

              for(int j=0; j<data.size(); j++){
                if(j!=gp.set){
                  GraphData gd2 = (GraphData)data.elementAt(j);
                  if(gd2.isSelected()&&!e.isShiftDown()){
                    gd2.setSelected(false);
                    data.setElementAt(gd2,j);
                  }
                }
              }
              this.repaint();
              if(plot!=null)plot.pp_mouseClicked();
              return;
            }

          }
        }
      }
      if(!e.isShiftDown()) clearSelectedGene();
      if(plot!=null)plot.pp_mouseClicked();
  }


  //used to display screen coordinates
  private void this_mouseMoved(MouseEvent e) {
	  //System.out.println("mousemoved fired");
    if(!zooming){
        int x = e.getX();
        int y = e.getY();
        if(inGraph(x,y)){
          int i=0;
          for(i=0; i<graphPoints.size(); i++){
            GraphPoint gp = (GraphPoint)graphPoints.elementAt(i);
            if(gp.contains(x,y)&&gp.set<data.size()) {
              GraphData gd = (GraphData)data.elementAt(gp.set);
              if(gd!=null) this.setToolTipText(gd.getName()+ " - " +(!useXLabels?""+gd.getX(gp.pos)+":":((xMarks.length>gp.pos&&xMarks[gp.pos]!=null)?xMarks[gp.pos]+":":""))+gd.getY(gp.pos));

              break;
            }
          }
          if(i==graphPoints.size()&&i!=0) this.setToolTipText("");
        }
    }
	  if(e.isControlDown()&&!zooming){
	    	zooming=true;
	    	xBoxStart = e.getX();
	        yBoxStart = e.getY();

	    }
	    if(zooming && (xBoxStart != -1) && (yBoxStart != 1)){
	      //Set the current end of the box.
	      xBoxEnd=e.getX();
	      yBoxEnd=e.getY();
	      //System.out.println("End set to (" + xBoxEnd + "," + yBoxEnd + ")");
	      
	      //These statements test if we are outside the screen region. 
	      if(xBoxEnd>this.getWidth()-sideWidth)xBoxEnd=this.getWidth()-sideWidth;
	      if(yBoxEnd>titleHeight+yaxisHeight) yBoxEnd=titleHeight+yaxisHeight;
	      if(xBoxEnd<yaxisWidth)xBoxEnd=yaxisWidth;
	      if(yBoxEnd<titleHeight) yBoxEnd=titleHeight;

	      if(xBoxStart>this.getWidth()-sideWidth)xBoxStart=this.getWidth()-sideWidth;
	      if(yBoxStart>titleHeight+yaxisHeight) yBoxStart=titleHeight+yaxisHeight;
	      if(xBoxStart<yaxisWidth)xBoxStart=yaxisWidth;
	      if(yBoxStart<titleHeight) yBoxStart=titleHeight;
	      
	      this.repaint();
	    }
  }

  //chages the cursor to crosshair
  private void this_mouseEntered(MouseEvent e) {
    this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
  }

  //changes the cursor back to default
  private void this_mouseExited(MouseEvent e) {
    this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

  /**
   * GraphData is data set to be displayed in a GraphDisplay
   */
  protected class GraphData{

    /**name of the data set*/
    protected String name;
    /**data points for the set*/
    protected DPoint[] points;
    /**whether or not the points are two values or only one*/
    protected boolean truePoints;
    /**whether or not the data set is selected*/
    protected boolean selected=false;

    /**
     * Default constructor
     */
    public GraphData(){
      this("");
    }

    /**
     * Constructs simply the name of data set
     * @param name name of data set
     */
    public GraphData(String name){
      this.name=name;
      points=null;
      truePoints=false;
    }

    /**
     * Constructs the name of the data set and its data points
     * @param name name of data set
     * @param points data points
     */
    public GraphData(String name, DPoint points[]){
      this.name=name;
      this.points=points;
      truePoints=true;
    }

    /**
     * Constructs the name of the data set and its data points
     * @param name name of data set
     * @param values data points
     */
    public GraphData(String name, int values[]){
      this.name=name;
      points = new DPoint[values.length];
      for(int i=0; i<values.length; i++){
        points[i]=new DPoint((double)i,(double)values[i]);
      }
      truePoints=false;

    }

    /**
     * Constructs the name of the data set and its data points
     * @param name name of data set
     * @param values data points
     */
    public GraphData(String name, double values[]){
      this.name=name;
      points = new DPoint[values.length];
      for(int i=0; i<values.length; i++){
        this.points[i]=new DPoint((double)i,values[i]);
      }
      truePoints=false;
    }

    /**
     * set whether or not the data set is selected
     * @param selected whether or not the data set is selected
     */
    public void setSelected(boolean selected){
      this.selected=selected;
    }


    /**
     * whether or not the data set is selected
     * @return whether or not the data set is selected
     */
    public boolean isSelected(){
      return selected;
    }

    /**
     * returns the data set name
     * @return data set name
     */
    public String getName(){
      return name;
    }

    /**
     * sets the data set name
     * @param name data set name
     */
    public void setName(String name){
      this.name=name;
    }

    /**
     * sets the data points
     * @param values data points
     */
    public void setValues(int values[]){
      points = new DPoint[values.length];
      for(int i=0; i<values.length; i++){
        this.points[i]=new DPoint((double)i,(double)values[i]);
      }
      truePoints=false;
    }

    /**
     * sets the data points
     * @param values data points
     */
    public void setValues(double values[]){
      points = new DPoint[values.length];
      for(int i=0; i<values.length; i++){
        this.points[i]=new DPoint((double)i,values[i]);
      }
      truePoints=false;
    }

    /**
     * sets the data points
     * @param points data points
     */
    public void setValues(DPoint points[]){
      this.points = points;
    }

    /**
     * returns the data points
     * @return data points
     */
    public DPoint[] getValues(){
      return points;
    }

    /**
     * returns the ith data point
     * @param i data point to return
     * @return ith data point
     */
    public DPoint getValue(int i){
      return points[i];
    }

    /**
     * returns the ith x-value
     * @param i data point to return
     * @return ith x-value
     */
    public double getX(int i){
      return points[i].x;
    }

    /**
     * returns the ith y-value
     * @param i data point to return
     * @return ith y-value
     */
    public double getY(int i){
      return points[i].y;
    }

    /**
     * returns the number of data points
     * @return number of data points
     */
    public int dataPoints(){
      return points.length;
    }


    /**
     * returns the data set name
     * @return data set name
     */
    public String toString(){
      return name;
    }


  }

  //class that draws individual points on the graph
  private class GraphPoint{

    private int x,y,size,set,pos,type;
    public static final int CIRCLE=1,SQUARE=0;

    public GraphPoint(int x, int y, int size, int setNum, int posNum){
      this.x=x;
      this.y=y;
      this.size=size;
      this.set=setNum;
      this.pos=posNum;
      this.type=pointShape;
    }

    public void drawPoint(Graphics g, Color c){
      g.setColor(c);
      if(type==CIRCLE) g.fillOval(x-size/2,y-size/2,size,size);
      else g.fillRect(x-size/2,y-size/2,size,size);
    }

    public boolean contains(int xpos, int ypos){
      return (xpos>=x-size/2&&xpos<=x+size/2&&ypos>=y-size/2&&ypos<=y+size/2);
    }

    public void setSize(int size){
      this.size=size;
    }

    public void setType(int type){
      this.type=type;
    }

    public int getX(){
      return x;
    }

    public int getY(){
      return y;
    }

  }

  /**
   * DPoint stores an x and y value in a double
   */
  protected class DPoint{

    /**x-value*/
    public double x;
    /**y-value*/
    public double y;

    /**
     * Constructs the data point
     * @param x x-value
     * @param y y-value
     */
    public DPoint(double x, double y){
      this.x=x;
      this.y=y;
    }

    /**
     * returns the x-value
     * @return x-value
     */
    public double getX(){
      return x;
    }

    /**
     * returns the y-value
     * @return y-value
     */
    public double getY(){
      return y;
    }

    /**
     * sets the x-value
     * @param x x-value
     */
    public void setX(double x){
      this.x=x;
    }

    /**
     * sets the y-value
     * @param y y-value
     */
    public void setY(double y){
      this.y=y;
    }

    /**
     * sets the data point
     * @param x x-value
     * @param y y-value
     */
    public void setPoint(double x, double y){
      this.x=x;
      this.y=y;
    }
  }

  /**
   * When the mouse is clicked and then control zooming can begin. The order is important.
   * We find our initial coordinate and save it. Then we update the other coordinates as
   * we drag the mouse.
   */
  private void this_mouseDragged(MouseEvent e) {    
    //System.out.println("mouseDragged fired");
	  if(e.isControlDown()&& (xBoxStart == -1) && (yBoxStart == -1)){
    	zooming=true;
    	xBoxStart = e.getX();
        yBoxStart = e.getY();

    }
    if(zooming){
      //Set the current end of the box.
      xBoxEnd=e.getX();
      yBoxEnd=e.getY();
      //System.out.println("End set to (" + xBoxEnd + "," + yBoxEnd + ")");
      
      //These statements test if we are outside the screen region. 
      if(xBoxEnd>this.getWidth()-sideWidth)xBoxEnd=this.getWidth()-sideWidth;
      if(yBoxEnd>titleHeight+yaxisHeight) yBoxEnd=titleHeight+yaxisHeight;
      if(xBoxEnd<yaxisWidth)xBoxEnd=yaxisWidth;
      if(yBoxEnd<titleHeight) yBoxEnd=titleHeight;

      if(xBoxStart>this.getWidth()-sideWidth)xBoxStart=this.getWidth()-sideWidth;
      if(yBoxStart>titleHeight+yaxisHeight) yBoxStart=titleHeight+yaxisHeight;
      if(xBoxStart<yaxisWidth)xBoxStart=yaxisWidth;
      if(yBoxStart<titleHeight) yBoxStart=titleHeight;
      
      this.repaint();
    }
  }

  private void this_keyReleased(KeyEvent e) {
	  if (e.getKeyCode() == KeyEvent.VK_CONTROL){
		  //that is, if we just released control
		  if(zooming && (xBoxStart != -1) && (yBoxStart != -1) && (xBoxEnd != -1) && (yBoxEnd != -1) && (xBoxStart != xBoxEnd) && (yBoxStart != yBoxEnd)){
		    	//System.out.println("Start: (" + xBoxStart + "," + yBoxStart + ")");
		    	//System.out.println("End: (" + xBoxEnd + "," + yBoxEnd + ")");
		      if(xBoxEnd<xBoxStart){
		        int xtemp=xBoxEnd;
		        xBoxEnd=xBoxStart;
		        xBoxStart=xtemp;
		      }
		      if(yBoxEnd<yBoxStart){
		        int ytemp=yBoxEnd;
		        yBoxEnd=yBoxStart;
		        yBoxStart=ytemp;
		      }
		      setGraphSize(xPosToValue(xBoxStart),xPosToValue(xBoxEnd),yPosToValue(yBoxEnd),yPosToValue(yBoxStart));
		      canUndo=true;
		    }
		    zooming=false;
		    xBoxStart=xBoxEnd=-1;
		    yBoxStart=yBoxEnd=-1;
		    this.repaint();
	  }
  }
  
  
  /**
   * When we release the mouse button this event is fired. If we are in zoom mode
   * it zooms to the appropriate region.
   */
  private void this_mouseReleased(MouseEvent e) {
    if(zooming && (xBoxStart != xBoxEnd) && (yBoxStart != yBoxEnd)){
    	//System.out.println("Start: (" + xBoxStart + "," + yBoxStart + ")");
    	//System.out.println("End: (" + xBoxEnd + "," + yBoxEnd + ")");
      if(xBoxEnd<xBoxStart){
        int xtemp=xBoxEnd;
        xBoxEnd=xBoxStart;
        xBoxStart=xtemp;
      }
      if(yBoxEnd<yBoxStart){
        int ytemp=yBoxEnd;
        yBoxEnd=yBoxStart;
        yBoxStart=ytemp;
      }
      setGraphSize(xPosToValue(xBoxStart),xPosToValue(xBoxEnd),yPosToValue(yBoxEnd),yPosToValue(yBoxStart));
      canUndo=true;
    }
    zooming=false;
    xBoxStart=xBoxEnd=-1;
    yBoxStart=yBoxEnd=-1;
    this.repaint();
  }

  /**
   * Obsolete.
   */
    private void this_mousePressed(MouseEvent e) {
    	//System.out.println("mousePressed fired");
    	if(zooming) {
    		//System.out.println("we are zooming in mousePressed");
    		xBoxStart = e.getX();
    		yBoxStart = e.getY();
    		//System.out.println("(" +xBoxStart+","+yBoxStart+")");
    	}
    }

  /**
   * Regression takes in an array of data and calculates a best fit regression line which
   * it is able to draw on a GraphDisplay
   */
  protected class Regression{

    private double slope=0, intercept=0, r2=0;
    private Vector data;

    /**
     * Default coonstructor with no data
     */
    public Regression(){
      data = new Vector();
    }

    /**
     * Constructs a regression line based on the given graph data sets
     * @param gdata data sets to create regression line from
     */
//  WTH
    @SuppressWarnings("unchecked")
    public Regression(GraphData[] gdata){
      data = new Vector();
      for(int i=0; i<gdata.length; i++){
        for(int j=0; j<gdata[i].points.length; j++){
          data.addElement(gdata[i].getValue(j));
        }
      }
      calculateRegression();
    }

    /**
     * Constructs a regression line based on the given graph data points
     * @param gdata data points to create regression line from
     */
//  WTH
    @SuppressWarnings("unchecked")
   public Regression(DPoint[] gdata){
      data = new Vector();
      for(int i=0; i<gdata.length; i++){
        data.addElement(gdata[i]);
      }
      calculateRegression();
    }

    /**
     * adds the data point to the regression and recalculates the line
     * @param d data point to add to the regression
     */
//  WTH
    @SuppressWarnings("unchecked")
   public void addData(DPoint d){
      data.addElement(d);
      calculateRegression();
    }

    /**
     * adds the data sets to the regression and recalculates the line
     * @param d data set to add to the regression
     */
//  WTH
    @SuppressWarnings("unchecked")
    public void addData(GraphData d){
      for(int j=0; j<d.points.length; j++){
          data.addElement(d.getValue(j));
      }
      calculateRegression();
    }

    /**
     * calculates the regression slope and intercept
     */
    public void calculateRegression(){
// WTH
    	  @SuppressWarnings("unused") 
      double firstterm=0,secondterm=0,thirdterm=0,fourthterm=0,fifthterm=0;
      int n = data.size();
      for(int i=0; i<n; i++){
        if((((DPoint)data.elementAt(i)).getX()!=Double.POSITIVE_INFINITY&&((DPoint)data.elementAt(i)).getX()!=Double.NEGATIVE_INFINITY&&((DPoint)data.elementAt(i)).getX()!=Double.NaN)
          &&(((DPoint)data.elementAt(i)).getY()!=Double.POSITIVE_INFINITY&&((DPoint)data.elementAt(i)).getY()!=Double.NEGATIVE_INFINITY&&((DPoint)data.elementAt(i)).getY()!=Double.NaN)){
            firstterm += ((((DPoint)data.elementAt(i)).getX())*(((DPoint)data.elementAt(i)).getY()));
            secondterm += (((DPoint)data.elementAt(i)).getX());
            thirdterm += (((DPoint)data.elementAt(i)).getY());
            fourthterm += Math.pow((((DPoint)data.elementAt(i)).getX()),2);
        }
        else n--;
      }
      slope = (((n*firstterm) - (secondterm)*(thirdterm)) / ((n*fourthterm) - (Math.pow(secondterm,2))));

      intercept = (thirdterm - (slope*secondterm))/n;

      double tss=0, ess=0;
      for(int i=0; i<data.size(); i++){
        if((((DPoint)data.elementAt(i)).getX()!=Double.POSITIVE_INFINITY&&((DPoint)data.elementAt(i)).getX()!=Double.NEGATIVE_INFINITY&&((DPoint)data.elementAt(i)).getX()!=Double.NaN)
          &&(((DPoint)data.elementAt(i)).getY()!=Double.POSITIVE_INFINITY&&((DPoint)data.elementAt(i)).getY()!=Double.NEGATIVE_INFINITY&&((DPoint)data.elementAt(i)).getY()!=Double.NaN)){
            ess+=Math.pow(intercept + slope*(((DPoint)data.elementAt(i)).getX()),2);
            tss+=Math.pow((((DPoint)data.elementAt(i)).getY()),2);
        }
      }

      r2 = ess/tss;

    }

    /**
     * draws the regression line on the graph display
     * @param g graph display graphics
     */
    public void drawRegressionLine(Graphics g){
      g.setColor(regressionColor);
      double endy = intercept + slope * xmax;
      double starty = intercept + slope * xmin;
      if(xmax>0) g.drawLine(xValueToPos(0),yValueToPos(intercept),xValueToPos(xmax),yValueToPos(endy));
      if(xmin<0) g.drawLine(xValueToPos(0),yValueToPos(intercept),xValueToPos(xmin),yValueToPos(starty));
    }

    /**
     * gets the slope of the regression
     * @return slope of the regression
     */
    public double getSlope(){
      return slope;
    }


    /**
     * gets the intercept of the regression
     * @return intercept of the regression
     */
    public double getIntercept(){
      return intercept;
    }

    /**
     * gets the r2 of the regression
     * @return 42 of the regression
     */
    public double getR2(){
      return r2;
    }



  }


}