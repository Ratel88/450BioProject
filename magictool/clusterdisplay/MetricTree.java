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

package magictool.clusterdisplay;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.process.ImageConverter;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import magictool.GrpFile;
import magictool.ProcessTimer;
import magictool.TreeableCluster;
import magictool.cluster.HiClust;
import magictool.cluster.NodeInfo;

/**
 * MetricTree draws a metric tree of a hierarchical cluster from a given cluster file.
 * The tree allows the user to select given nodes and highlights the path to the genes.
 * The class works along with the MetricRuler class to provide a ruler fit to scale with
 * the tree.
 * NOTE: This class should be reimplemented so that the lines and boxes are themselves JComponents. MC:11/11/05
 */
public class MetricTree extends JPanel implements Printable{

  private Font f;
  /**cluster file to to draw tree from*/
  protected File clustFile;
  /**group of selected gene*/
  protected GrpFile group;
  private DefaultMutableTreeNode theNode, theLeaves[]; //thePath[];	//thePath[] was hiding local varaibles of the same name & was never referenced.
  private Dimension ps = new Dimension(0,0);
  private float max=0,min=0, multiplier=0;
  private Vector lines, linebars;
  private int maxwidth, regwidth, numberoftimes=0;
  /**line height for each gene*/
  protected int height;
  /**width of the ruler*/
  protected int width;
  /**index of selected genes*/
  protected Vector selectedIndex;
  /**ruler for the metric tree*/
  protected MetricRuler ruler;
  /**whether or not to show the names of the genes*/
  protected boolean showNames;
  /**whether or not genes are selectable*/
  protected boolean selectable;

  /**
   * Constructs a MetricTree from specified cluster file with the default dimensions. Genes are set
   * to selectable and gene names are shown.
   * @param clustFile cluster file to create tree from
   */
  public MetricTree(File clustFile){
    this(clustFile,true,true,10, 400);
  }

  /**
   * Constructs a MetricTree from specified cluster file with the given dimensions.
   * @param clustFile cluster file to create tree from
   * @param showNames whether gene names are displayed
   * @param selectable whether genes are selectable
   * @param height height of each gene box
   * @param width width of the metric tree
   */
  public MetricTree(File clustFile, boolean showNames, boolean selectable, int height, int width) {
    this.clustFile=clustFile;
    this.showNames=showNames;
    this.selectable=selectable;
    this.height=height;
    this.regwidth=width;
    f = new Font("Times New Roman", Font.PLAIN, 10);
    this.setDoubleBuffered(true);

    TreeableCluster tc=null;
    tc = new HiClust();

    theNode = tc.getDataInTree(clustFile);
    theLeaves = getLeafOrder(theNode);	//turn this into a treemap or vector of geneNames?  This is the problem!
    
    //initialize tree
    init();
    lines = new Vector();
    if(selectable) selectedIndex = new Vector();

    //setting up the ruler for the tree
    ProcessTimer getRulerTimer = new ProcessTimer("MetricTree.init():getRuler");
    ruler = new MetricRuler();
    ruler.setPreferredSize(new Dimension((int)getPS().getWidth(),40));
    ruler.setMax(this.max);
    ruler.setMin(this.min);
    ruler.setWidth(width-maxwidth);
    getRulerTimer.finish();
  }

  //initializes the tree
  private void init(){
	  
	  //handles the width and height of the screen
	  int lineheight=height;
	  if(showNames){
		  ProcessTimer showNamesTimer = new ProcessTimer("MetricTree.init():showNames");
		  FontMetrics fm = Toolkit.getDefaultToolkit().getFontMetrics(f);
		  
		  lineheight=fm.getHeight()+5;
		  height=lineheight;
		  
		  maxwidth = 0;
		  for(int i=0; i<theLeaves.length; i++){
			  int twidth = fm.stringWidth(theLeaves[i].toString());		//TODO identify the longest gene name when building the expfile
			  if(twidth>maxwidth) maxwidth=twidth;
		  }
		  maxwidth+=3;
		  showNamesTimer.finish();
	  }
	  
	  int totalheight=lineheight*theLeaves.length+5;		//TODO theLeaves.length really is just numGenes... make this an instance variable
	  this.setSize(maxwidth+regwidth, totalheight);
	  setPS(maxwidth+regwidth,totalheight);
	  this.setPreferredSize(ps);
	  
	  // sets up the min and max
	  // TODO the min is the distance between the first two nodes clustered, the max, the last two.  Store these data when building the cluster file.
	  ProcessTimer minmaxTimer = new ProcessTimer("MetricTree.init():minmax");
//	  for(int i=0; i<theLeaves.length; i++){
//		  TreeNode thePath[] = theLeaves[i].getPath();	//TODO always get a StackOverflowError here...
//		  float verytemp = ((NodeInfo)((DefaultMutableTreeNode)thePath[thePath.length-2]).getUserObject()).getDistance();
//		  float othertemp = ((NodeInfo)((DefaultMutableTreeNode)thePath[0]).getUserObject()).getDistance();
//		  if(i==0) {
//			  min=verytemp;
//			  max=othertemp;
//		  }
//		  else{
//			  if(verytemp<min) min = verytemp;
//			  if(othertemp>max) max = othertemp;
//		  }
//	  }
//	  
//	  if(min<.01) min=0;
//	  else min-=.01;
//	  
//	  if(max>1.99&&max<=2) max=2;
//	  else max+=.01;
	  
	  min = 0;
	  max = 2;
	  
	  multiplier=(2/(max-min))*200;
	  minmaxTimer.finish();
	  
	  if(selectable){
		  
		  //handles mouse events
		  this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
			  public void mouseMoved(MouseEvent e) {
				  this_mouseMoved(e);
			  }
		  });
		  
		  this.addMouseListener(new java.awt.event.MouseAdapter() {
			  public void mouseClicked(MouseEvent e) {
				  this_mouseClicked(e);
			  }
		  });
	  }
  }

  /**
   * returns ruler for the MetricTree
   * @return ruler for the MetricTree
   */
  public MetricRuler getRuler(){
    return ruler;
  }

  /**
   * paints the tree onto the specified graphics
   * @param g graphics to paint the tree on
   */
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    linebars = new Vector();
    g.setColor(Color.black);
    g.setFont(f);
    FontMetrics fm = g.getFontMetrics(f);

    //handles the height
    int lineheight;
    if(showNames) lineheight=fm.getHeight()+5;
    else lineheight=height;
    if(lineheight==0) lineheight=5;

    int totalheight=lineheight*theLeaves.length+5;	//TODO theLeaves.length really is just numGenes... make this a class variable

    //handles the width
    if(showNames){
      int maxwidth = 0;
      for(int i=0; i<theLeaves.length; i++){	//TODO replace with numGenes
        int twidth = fm.stringWidth(theLeaves[i].toString());
        if(twidth>maxwidth) maxwidth=twidth;
      }
      maxwidth+=3;
    }
    else maxwidth=0;

    width=this.getWidth();
    ruler.setWidth(width-maxwidth);
    multiplier=(2/(max-min))*((width-maxwidth)/2);

    int h=lineheight;
    int maxPath=0;

    group = new GrpFile(clustFile.getName());

    //special case if genes can be selected
    if(selectable){
      for(int i=0; i<theLeaves.length; i++){
        TreeNode thePath[] = theLeaves[i].getPath();
        boolean inpath=false;

        for(int j=0; j<thePath.length-2; j++){
          NodeInfo ni= (NodeInfo)((DefaultMutableTreeNode)thePath[thePath.length-2-j]).getUserObject();
          for(int qp=0; qp<selectedIndex.size(); qp++){
            if(((Integer)selectedIndex.elementAt(qp)).intValue()==1||Integer.parseInt(ni.toString())==((Integer)selectedIndex.elementAt(qp)).intValue()){
              inpath=true;
              break;
            }
          }
          if(inpath) break;
        }

      //adds gene if it is selected
      if(inpath){
        group.addOne(theLeaves[i].toString());
      }

      //draws gene names
      if(showNames) drawGeneName(g,theLeaves[i].toString(),inpath,h,lineheight);

       h+=lineheight;
       //int tpath = theLeaves[i].getPath().length-2;
       //if(tpath>maxPath) maxPath=tpath;
    }
  }

        if(showNames) h=lineheight-fm.getHeight()/2;
        else h=lineheight/2;

        for(int i=0; i<theLeaves.length; i++){
          TreeNode thePath[] = theLeaves[i].getPath();

              NodeInfo ni= (NodeInfo)((DefaultMutableTreeNode)thePath[thePath.length-2]).getUserObject();
              float verytemp = ni.getDistance();
              int templength = Math.round((verytemp-min)*multiplier);
              drawLine(g, width-maxwidth, h, thePath, 0);
              h+=lineheight;
        }
  }

  /**
   * draws a box around a gene name and fills it when selected
   * @param g graphics to draw gene name and box
   * @param name name of the gene
   * @param filled whether or not the box is filled
   * @param h top position of the box
   * @param lineheight height of the line
   */
  public void drawGeneName(Graphics g, String name, boolean filled, int h, int lineheight){
    if(filled){
        g.setColor(Color.yellow);
        g.fillRect(width-maxwidth, h-lineheight+4, maxwidth-1, lineheight-3);
        g.setColor(Color.black);
      }

      g.drawRect(width-maxwidth, h-lineheight+4, maxwidth-1, lineheight-3);
      g.drawString(name, width-maxwidth+2, h);

    }

  /**
   * returns the group of selected genes
   * @return group of selected genes
   */
  public GrpFile getGroup(){
    return group;
  }

  /**
   * Draws the lines that make up the metric tree
   * @param g graphics to draw the line on
   * @param xpos rightmost position
   * @param h top position
   * @param thePath array of nodes containing the gene path
   * @param num position in the gene path
   */
  public void drawLine(Graphics g, int xpos, int h, TreeNode[] thePath, int num){
        NodeInfo ni= (NodeInfo)((DefaultMutableTreeNode)thePath[thePath.length-2-num]).getUserObject();
        int na=Integer.parseInt(ni.toString());
        boolean inpath=false;
        if(selectable&&selectedIndex.size()!=0){
          for(int i=0; i<selectedIndex.size(); i++){
            if(na==((Integer)selectedIndex.elementAt(i)).intValue()){
              inpath=true;
              break;
            }
          }
        }
        if(selectable&&!inpath&&selectedIndex.size()!=0){
           for(int j=0; j<thePath.length-2-num; j++){
              NodeInfo ni1= (NodeInfo)((DefaultMutableTreeNode)thePath[j]).getUserObject();
                for(int qp=0; qp<selectedIndex.size(); qp++){
                  if(Integer.parseInt(ni1.toString())==((Integer)selectedIndex.elementAt(qp)).intValue()){
                    inpath=true;
                    break;
                }
              }
              if(inpath)break;
            }
        }

        float verytemp = ni.getDistance();
        int templength=Math.round((verytemp-min)*multiplier);

        int xpos1=0;
        if(inpath&&selectable){
          g.setColor(Color.red);
          g.drawLine(xpos1=(width-maxwidth)-templength,h+1,xpos,h+1);
          g.drawLine(xpos1=(width-maxwidth)-templength,h-1,xpos,h-1);
          g.setColor(Color.black);
        }

        g.drawLine(xpos1=(width-maxwidth)-templength,h,xpos,h);

        boolean drawn=false;

        for(int j=0; j<lines.size(); j++){
          Line li = (Line)lines.elementAt(j);
          if(li.getNumber()==Integer.parseInt(ni.toString())){
            LineBar lb = new LineBar(xpos1,Math.min(li.getYPos(),h),Math.abs(li.getYPos()-h),li.getNumber(), li.getDissimilarity(), showNames);
            if(inpath&&selectable)lb.setSelected(true);
            if(!inpath&&selectable){
                for(int q=0; q<selectedIndex.size(); q++){

                  if(((Integer)selectedIndex.elementAt(q)).intValue()==li.getNumber()){
                    lb.setSelected(true);
                    break;
                  }
                }
            }

            lb.paint(g);
            linebars.add(lb);
            drawn=true;
            int he=Math.min(li.getYPos(),h)+(Math.abs(li.getYPos()-h))/2;

            lines.remove(j);
            if(num<thePath.length-2)drawLine(g,xpos1,he, thePath, num=num+1);
            break;
          }
        }
        if(!drawn)lines.add(new Line(xpos,h,Integer.parseInt(ni.toString()),ni.getDistance()));
  }

  /**
   * returns a line of the specified group number if it exists
   * @param groupNum cluster number
   * @return line of the spcecified group number if it exists
   */
  public Line findLine(int groupNum){
    for(int j=0; j<lines.size(); j++){
      Line li = (Line)lines.elementAt(j);
      if(li.getNumber()==groupNum){
        return li;
      }
    }
    return null;
  }

  /**
   * sets the line height
   * @param height line height
   */
  public void setLineHeight(int height){
    this.height=height;
  }

  /**
   * returns the line height
   * @return line height
   */
  public int getLineHeight(){
    return height;
  }



  /**
   * returns the order of the names of the leaves
   * @param dmtn top node of the tree
   * @return order of the names of the leaves
   */
  public DefaultMutableTreeNode[] getLeafOrder(DefaultMutableTreeNode dmtn){ 
	  int num = dmtn.getLeafCount();	//TODO replace with numGenes, passing it in if necessary
	  
	  DefaultMutableTreeNode[] names = new DefaultMutableTreeNode[num];
	  //Enumeration m = dmtn.depthFirstEnumeration();	//depthFirst is recursive for DefaultMutableTrees, and was causing StackOverflowErrors
	  Enumeration m = dmtn.preorderEnumeration();
	  int i=0;
	  DefaultMutableTreeNode t;
	  
	  while(m.hasMoreElements()){	//Dies in here
		  t=(DefaultMutableTreeNode)m.nextElement();
		  if(t.isLeaf()){
			  names[i]=t;
			  i++;
		  }
	  }
	  return names;
  }

    /**
     * returns a vector of leaves in order
     * @return vector of leaves in order
     */
    public Vector getGroupOfLeaves(){
      Vector gr = new Vector();
      for(int i=0; i<theLeaves.length; i++){
        gr.addElement(theLeaves[i].toString());
      }

      return gr;
    }

    /**
     * returns an array of selected gene numbers
     * @return array of selected gene numbers
     */
    public int[] getSelectedIndex(){
      int tempIndex[] = new int[selectedIndex.size()];
      for(int i=0; i<selectedIndex.size(); i++){
        tempIndex[i]=((Integer)selectedIndex.elementAt(i)).intValue();
      }
      return tempIndex;
    }


    /**
     * returns the preferred size
     * @return preferred size
     */
    public Dimension getPS(){
      return ps;
    }

    /**
     * sets the preferred size
     * @param d preferred size
     */
    public void setPS(Dimension d){
      ps=d;
    }

    /**
     * sets the preferred size
     * @param w preferred width
     * @param h preferred height
     */
    public void setPS(int w, int h){
      setPS(new Dimension(w,h));
    }

    /**
     * returns the start position of the metric tree lines
     * @return start position of the metric tree lines
     */
    public int getLineStartPos(){
      return Math.max(regwidth, width-maxwidth);
    }

    /**
     * returns the maximum dissimilarity
     * @return maximum dissimilarity
     */
    public float getMax(){
      return max;
    }

    /**
     * returns the minimum dissimilarity
     * @return minimum dissimilarity
     */
    public float getMin(){
      return min;
    }

   /**
     * gets the number of megapixels for a saved image
     * @return number of megapixels for a saved image
     */
    public double getMegaPixels(){
      int h = this.getHeight();
      int w = this.getWidth();
      Font f = new Font("Dialog", Font.PLAIN, 10);

      int li=1;
      int th = this.getPS().height+ruler.getHeight()+(this.getGraphics().getFontMetrics(f).getHeight())*li;

      return (((double)w)/1000)*(((double)th)/1000);
    }


  /**
   * saves of gif image of the metric tree
   * @param filename full path of the new gif image
   * @param number number of pictures
   * @throws IOException when failed to save the image
   */
  public void saveImage(String filename, int number) throws IOException{
    int h = this.getHeight();
    int w = this.getWidth();
    Font f = new Font("Dialog", Font.PLAIN, 10);

    int li=1;
    int th = this.getPS().height+ruler.getHeight()+(this.getGraphics().getFontMetrics(f).getHeight())*li;

    MetricTree tree = new MetricTree(clustFile, showNames, selectable, height, w-maxwidth);
    tree.setBackground(Color.white);
    tree.selectedIndex=selectedIndex;
    tree.getRuler().setWidth(w-maxwidth);
    tree.getRuler().setSize(ruler.getSize());

    setDoubleBuffered(false);

    int height = (int)Math.ceil((double)th/number);

    Image bi = createImage(w, height);//, BufferedImage.TYPE_INT_RGB);
    Graphics image = bi.getGraphics();

    try{
            String n="";
            if (number>1){
              n = filename.substring(0, filename.lastIndexOf(".gif"));
              String na = n.substring(n.lastIndexOf(File.separator));
              n = n + "_images";
              File f1 = new File(n);
              f1.mkdir();
              n+=na;

            }

      for(int i=0; i<number; i++){

        image.setFont(f);
        FontMetrics fm = image.getFontMetrics();

        image.setColor(Color.white);
        image.fillRect(0,0,w,height);
        Graphics2D image2 = (Graphics2D)image;

        image2.translate(0,-i*height);

        tree.getRuler().paintComponent(image2);

        image2.translate(0,ruler.getHeight());
        tree.paintComponent(image2);

        image2.setColor(new Color(0,0,0));
        image2.setFont(f);
        image2.drawString("Cluster File: " + clustFile.getName(),5,this.getPS().height+fm.getHeight()-fm.getDescent());

        image2.translate(0,-ruler.getHeight());
        image2.translate(0,i*height);

        File theFile;
        if(number==1)theFile = new File(filename);
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
    catch(Exception e1){
      throw new IOException();
    }
  }

  /**
   * prints the metric tree - fitting the width to a page or less
   * @param g print graphics
   * @param pageFormat page formate
   * @param pageIndex page number
   * @return whether or not page index exists
   * @throws PrinterException when does not print
   */
  public int print(Graphics g, PageFormat pageFormat,
    int pageIndex) throws PrinterException {

    int h = this.getHeight();
    int w = this.getWidth();
    MetricTree tree = new MetricTree(clustFile, showNames, selectable, height, w-maxwidth);
    tree.setBackground(Color.white);
    tree.selectedIndex=selectedIndex;
    tree.getRuler().setWidth(w-maxwidth);
    tree.getRuler().setSize(ruler.getSize());

    Graphics2D g2 = (Graphics2D) g;
    g2.setFont(new Font("Times New Roman", Font.PLAIN, 10));
    g2.setColor(Color.black);
    int fontHeight=g2.getFontMetrics().getHeight();
    int fontDesent=g2.getFontMetrics().getDescent();


    double pageHeight = pageFormat.getImageableHeight();

    double pageWidth = pageFormat.getImageableWidth();
    double treeWidth = tree.getWidth();
    double scale = 1;
    if (treeWidth >= pageWidth) {
    scale = pageWidth / treeWidth;
    }

    double headerHeightOnPage=tree.getRuler().getSize().height*scale+fontHeight+5;
    double treeWidthOnPage=treeWidth*scale;

    double oneRowHeight=height*scale;
    int numRowsOnAPage=(int)Math.floor((pageHeight-headerHeightOnPage)/oneRowHeight);
    double pageHeightForTree=oneRowHeight*numRowsOnAPage;
    int totalNumPages= (int)Math.ceil(((double)theLeaves.length)/numRowsOnAPage);	
    if(pageIndex>=totalNumPages) {
    return NO_SUCH_PAGE;
    }

    g2.translate(pageFormat.getImageableX(),pageFormat.getImageableY());

    String s="";
    g2.drawString((s="Page: "+(pageIndex+1)),(int)pageWidth-g2.getFontMetrics().stringWidth(s),
    (int)(fontHeight-fontDesent));//top right

    s="Cluster File: " + clustFile.getName();
    g2.drawString(s,0,
    (int)(fontHeight-fontDesent));//top left

    g2.translate(0f,headerHeightOnPage);
    g2.translate(0f,-pageIndex*pageHeightForTree);

    g2.setClip(0, (int)(pageHeightForTree*pageIndex)+1,(int)
    Math.ceil(treeWidthOnPage),(int) Math.ceil(pageHeightForTree));

    g2.scale(scale,scale);
    tree.paint(g2);
    g2.scale(1/scale,1/scale);
    g2.translate(0f,pageIndex*pageHeightForTree);
    g2.translate(0f, -headerHeightOnPage+fontHeight+5);
    g2.setClip(0, 0,(int) Math.ceil(treeWidthOnPage),(int)Math.ceil(headerHeightOnPage-fontHeight-5));
    g2.scale(scale,scale);
    tree.getRuler().paint(g2);//paint header at top

    return Printable.PAGE_EXISTS;
    }

   //creates tool tip texts when mouse is over a cluster
   // this is stupid - swing JComponents all have a ToolTipManager that automatically takes care
   // of scanning for a mouseover and timing it and whatnot; the boxes and lines should have been
   // implemented as JComponents themselves, I think.
   //XXX changed so dissim tooltips would show instead of tiny empty box.  MC:11/11/05
    private void this_mouseMoved(MouseEvent e) {
      int x = e.getX();
      int y =e.getY();
      for(int i=0; i<linebars.size(); i++){
        LineBar lb = (LineBar)linebars.elementAt(i);
        if(lb.isOver(x,y)){
          this.setToolTipText("Cluster " + lb.getNumber() + ":" + lb.getDissimilarity());
          return;
        }
      }
      //this.setToolTipText("");	// dissimilarity tooltips in MetricTree view
    }
  
    //allows user to select clusters and genes
    private void this_mouseClicked(MouseEvent e) {

      int x = e.getX();
      int y =e.getY();
      for(int i=0; i<linebars.size(); i++){
        LineBar lb = (LineBar)linebars.elementAt(i);
        if(lb.isOver(x,y)){
          boolean done=false;
          if(!e.isShiftDown()) selectedIndex.removeAllElements();

          else {
            for(int j=0; j<selectedIndex.size(); j++){
              int num = ((Integer)selectedIndex.elementAt(j)).intValue();
              if(num==lb.getNumber()){
                selectedIndex.removeElementAt(j);
                done=true;
                break;
              }
            }
        }

          if(!done) selectedIndex.add(new Integer(lb.getNumber()));
          repaint();
          return;
        }
      }
      selectedIndex.removeAllElements();
      repaint();
    }


    //holds data about a line
    private class Line{

      public Line(){

      }

      public Line(int x, int y, int num, float dis){
        this.x=x;
        this.y=y;
        this.num=num;
        this.dis=dis;
      }

      public void setNumber(int num){
        this.num=num;
      }

      public void setYPos(int y){
        this.y=y;
      }

      public void setXPos(int x){
        this.x=x;
      }

      public int getXPos(){
        return x;
      }

      public int getYPos(){
        return y;
      }

      public int getNumber(){
        return num;
      }

      public float getDissimilarity(){
        return dis;
      }

      int x,y,num;
      float dis;



    }

    //holds data about a line bar which connects a line
    private class LineBar{

      public LineBar(int x, int y, int length, int num, float dis){
        this(x,y,length,num,dis,true);
      }

      public LineBar(int x, int y, int length, int num, float dis, boolean drawoval){
        this.x=x;
        this.y=y;
        this.length=length;
        this.num=num;
        this.dis=dis;
        this.drawoval=drawoval;
      }

      public void paint(Graphics g){
        g.setColor(Color.black);
        if(selected){
          g.setColor(Color.red);
          g.fillRect(x-1,y-1,3,length+2);
        }

        g.setColor(Color.black);
        if(drawoval) g.fillOval(x-3,y+(length/2)-3,6,6);
        g.drawLine(x,y, x,y+length);
        
      }

      public int getNumber(){
        return num;
      }

      public boolean isOver(int xpos, int ypos){

        return(xpos<=x+3&&xpos>=x-3&&ypos<=y+(length/2)+3&&ypos>=y+(length/2)-3);

      }

      public void setSelected(boolean b){
        selected=b;
      }

      public boolean isSelected(){
        return selected;
      }

      public float getDissimilarity(){
        return dis;
      }

      public boolean getDrawOval(){
        return drawoval;
      }

      public void setDrawOval(boolean drawoval){
        this.drawoval=drawoval;
      }

      private int x,y,length, num;
      private boolean selected=false, drawoval;
      private float dis;
    }

}
