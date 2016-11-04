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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import javax.swing.BorderFactory;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;

import magictool.*;

import magictool.image.FlagManager;


/**
 * SegmentFrame is an internal frame which provides the graphical user interface that
 * allows users to complete the segmentation of microarray spots. It allows users to select the
 * desired segementation method and parameters and to save the generated data into a new
 * or appended expression file.
 */
public class SegmentFrame extends JInternalFrame {
   
   private JSplitPane jSplitPaneHoriz = new JSplitPane();
   public JSplitPane jSplitPaneVert = new JSplitPane();
   private JScrollPane scrollLeft;
   public JScrollPane scrollGreen;
   private JScrollPane scrollRed;
   private SegmentPanel infoPanel;
   private JLabel statusBar = new JLabel();
   public SegmentDisplay idGreen;
   public SegmentDisplay idRed;
   private BorderLayout borderLayout1 = new BorderLayout();
   private JPanel greenPanel = new JPanel();
   private JPanel redPanel = new JPanel();
   private JLabel greenTitle = new JLabel("Green Image: ");
   private JLabel redTitle = new JLabel("Red Image: ");
   private BorderLayout borderLayout2 = new BorderLayout();
   private BorderLayout borderLayout3 = new BorderLayout();
   
   private int w,h,newTopLeftX,newTopLeftY;
   private ProgressFrame progress = null;
   
   /**contains the data for all genes, once calculated*/
   protected AllGeneData allGeneData;
   /**contains the options for automatic flagging*/
   protected AutoFlaggingOptionsDialog afod;
   /**individual spot cell height*/
   protected double cellHeight;
   /**green image*/
   protected Image imageGreen;
   /**red image*/
   protected Image imageRed;
   /**grid manager for the microarray images*/
   protected GridManager manager;
   /**flag manager for the grid manager for the microarray images*/
   protected FlagManager flagman;
   /**flag manager for the automatic flags*/
   protected FlagManager autoflagman;
   /**parent main frame*/
   protected MainFrame main;
   /**open project from the main frame*/
   protected Project project=null;
   /**polygon containing the coordinates of current spot cell*/
   protected Polygon cell;
   
   
   /**
    * Constructs the segment frame with the specified microarray images, grid manager, project, and main frame.
    * @param imageGreen green microarray image
    * @param imageRed red microarray image
    * @param m grid manager for the microarray images
    * @param project open project to place new expression files
    * @param main parent main frame
    */
   public SegmentFrame(Image imageRed, Image imageGreen, int redPixels[][], int grnPixels[][], GridManager m, FlagManager fgm, FlagManager afgm, Project project, MainFrame main) {
      
      this.imageGreen=imageGreen;
      this.imageRed=imageRed;
      this.manager = m;
      this.flagman = fgm;
      this.autoflagman = afgm;
      this.project = project;
      this.main=main;
      idGreen = new SegmentDisplay(imageGreen, m);
      idRed = new SegmentDisplay(imageRed, m);
      afod = new AutoFlaggingOptionsDialog(this.main, this.project, this.manager, this);	//added 6/11/2007 by Michael Gordon
   
    /*
     * WTH 7/24/06 Retain raw pixel values (ie 16-bit values)
     */
      idRed.RawPixels = redPixels;
      idGreen.RawPixels = grnPixels;
      infoPanel = new SegmentPanel(this.manager, this.flagman, this.idRed, this.idGreen, this, afod); //modified 6/11/2007 by Michael Gordon
      
      this.setClosable(true);
      this.setTitle("SEGMENTATION");
      
      try {
         jbInit();
         
      }  catch(Exception e) {
         e.printStackTrace();
      }
      setCurrentCell();
      showCurrentCell();
   }
   
   
   private void jbInit() throws Exception {
      greenTitle.setForeground(Color.green);
      greenTitle.setLabelFor(scrollGreen);
      redTitle.setForeground(Color.red);
      redTitle.setLabelFor(scrollRed);
      this.setIconifiable(true);
      this.setMaximizable(true);
      this.setResizable(true);
      
      scrollLeft = new JScrollPane(infoPanel);
      scrollGreen = new JScrollPane(idGreen);
      scrollRed = new JScrollPane(idRed);
      
      scrollLeft.getVerticalScrollBar().setMaximum(scrollLeft.getVerticalScrollBar().getMaximum()+75);	//added 6/12/2007 by Michael Gordon, then modified 6/20/07
      Dimension scrlLeftPrefSz = scrollLeft.getPreferredSize();
      scrlLeftPrefSz.setSize(scrlLeftPrefSz.getWidth()+57, scrlLeftPrefSz.getHeight());
      scrollLeft.setPreferredSize(scrlLeftPrefSz);
      
      jSplitPaneHoriz.setOneTouchExpandable(true);
      jSplitPaneVert.setOneTouchExpandable(true);

      statusBar.setBorder(BorderFactory.createLoweredBevelBorder());
      greenPanel.setLayout(borderLayout2);
      greenPanel.add(greenTitle, BorderLayout.NORTH);
      greenPanel.add(scrollGreen, BorderLayout.CENTER);
      
      redPanel.setLayout(borderLayout3);
      redPanel.add(redTitle, BorderLayout.NORTH);
      redPanel.add(scrollRed, BorderLayout.CENTER);
      
      scrollGreen.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scrollGreen.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
      scrollRed.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      scrollRed.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
      
      jSplitPaneVert.add(greenPanel,JSplitPane.BOTTOM);
      jSplitPaneVert.add(redPanel, JSplitPane.TOP);
      jSplitPaneVert.setPreferredSize(new Dimension(800,400));
      
      jSplitPaneHoriz.add(scrollLeft,JSplitPane.LEFT);
      jSplitPaneVert.setOrientation(JSplitPane.VERTICAL_SPLIT);
      jSplitPaneHoriz.add(jSplitPaneVert,JSplitPane.RIGHT);
      jSplitPaneHoriz.setDividerLocation(scrollLeft.getPreferredSize().width+20);
      
      this.getContentPane().setBackground(Color.lightGray);
      this.getContentPane().add(statusBar,BorderLayout.SOUTH);
      this.getContentPane().add(jSplitPaneHoriz, BorderLayout.CENTER);
   }
   
   /**
    * sets the segement displays to show the current spot cells for both the red and green image
    */
   public void showCurrentCell() {
      setCurrentCell();
      newTopLeftX = ((int)(idGreen.getZoom()*cell.xpoints[0]))-4;
      newTopLeftY = ((int)(idGreen.getZoom()*cell.ypoints[0]))-4;
      scrollGreen.getViewport().setViewPosition(new Point(newTopLeftX, newTopLeftY));
      scrollRed.getViewport().setViewPosition(new Point(newTopLeftX,newTopLeftY));
   }
   
   /**
    * sets the magnification in both segment displays to the spot level
    */
   public void zoomToCell(){
      idGreen.zoom(((jSplitPaneVert.getHeight()-jSplitPaneVert.getDividerSize()-(2*redPanel.getHeight()))/2)/cellHeight);
      idRed.zoom(((jSplitPaneVert.getHeight()-jSplitPaneVert.getDividerSize()-(2*redPanel.getHeight()))/2)/cellHeight);
      showCurrentCell();
   }
   
   /**
    * sets the current cell coordinates
    */
   public void setCurrentCell() {
      Polygon p = manager.getCurrentGrid().getTranslatedPolygon();
      Polygon q = new Polygon();
      if(p!=null) {
         for(int j=0; j<p.xpoints.length; j++){
            q.xpoints[j]=(int)((idGreen.screenX(p.xpoints[j]))/idGreen.getZoom());
            q.ypoints[j]=(int)((idGreen.screenX(p.ypoints[j]))/idGreen.getZoom());
         }
         manager.getCurrentGrid().setSpots(q);
         cell = manager.getCurrentGrid().getCurrentSpot();
      }
      cellHeight = cell.ypoints[3]-cell.ypoints[0];
   }
   
   /**
    * sets the current spot
    * @param grid grid number
    * @param spot spot number
    */
   public void setSpot(int grid, int spot){
      infoPanel.setSpot(grid,spot);
   }
   
  
   //XXX SegmentFrame: create new .exp file
   /**
    * creates or appends an expression file with the generated data based on the user specified
    * segementation method and parameters
    * @param name name of the new expression file
    * @param colName data column name (AKA microarray designator)
    * @param newFile whether creating entirely new file or appending existing expression file
    * @param appendname name of the expression file to append
    * @param byname whether to append by gene name or by list order
    * @param method segmentation method
    * @param ratiomethod ratio method
    * @param params other segmentation parameters
    */
   
  public void createNewExpRawFiles(boolean doExp, boolean doRaw, String expFileName, String rawFileName, String colName, boolean newExpFile, String appendname, boolean byname, int method, int ratiomethod, Object params[]){
      this.setTitle("SEGMENTATION (working)");
	  System.out.println(doRaw);
      if(project!=null){
         try{
            File expFile=null, rawFile;
            BufferedWriter expFileWriter=null, rawFileWriter=null;
            boolean goExp=false, goRaw=false;
            File temp=null;
            String file=expFileName;
            String rfile=rawFileName;
            if (doExp){
               if(file.toLowerCase().endsWith(".exp")) file=file.substring(0,file.lastIndexOf("."));
               expFile = new File(project.getPath()+file+File.separator+file+".exp");
               System.out.println(expFile.getAbsolutePath());
               int deleteFiles = JOptionPane.CANCEL_OPTION;
               if(!expFile.exists()||(deleteFiles=JOptionPane.showConfirmDialog(null, "Expression File Already Exists! Do You Wish To Overwrite?\nOverwriting The File Will Delete All Files Which Used The Previous File"))==JOptionPane.OK_OPTION){
                  if(deleteFiles==JOptionPane.OK_OPTION) expFile.getParentFile().delete();
                  //setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  expFile.getParentFile().mkdirs();
                  if(!newExpFile) {
                     temp = File.createTempFile(file, null);
                     expFileWriter=new BufferedWriter(new FileWriter(temp.getPath()), 4096);
                  } else expFileWriter=new BufferedWriter(new FileWriter(expFile.getPath()));
                  expFileWriter.write(colName);
                  expFileWriter.write("\n");
                  goExp=true;
               }
            }
            if (doRaw){
               rawFileName=colName;
               if(rawFileName.toLowerCase().endsWith(".raw")) rawFileName=rawFileName.substring(0,rawFileName.lastIndexOf("."));
               //String file=rawFileName;
               rawFile = new File(project.getPath()+file+File.separator+rawFileName+".raw");
               System.out.println(rawFile.getAbsolutePath());
               //System.exit(1);
               int deleteFiles = JOptionPane.CANCEL_OPTION;
               if(!rawFile.exists()||(deleteFiles=JOptionPane.showConfirmDialog(null, "Raw File Already Exists! Do You Wish To Overwrite?\nOverwriting The File Will Delete All Files Which Used The Previous File"))==JOptionPane.OK_OPTION){
                  if(deleteFiles==JOptionPane.OK_OPTION) rawFile.getParentFile().delete();
                  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                  rawFile.getParentFile().mkdirs();
                  rawFileWriter=new BufferedWriter(new FileWriter(rawFile.getPath()), 4096);
                  rawFileWriter.write(expFileName+":"+colName+"\tRedFGtot\tRedBGtot\tGrnFGtot\tGrnBGtot\tRedFGavg\tRedBGavg\tGrnFGavg\tGrnBGavg");
                  rawFileWriter.write("\n");
                  //goRaw=true;
                  
               }
               
               goRaw=true;
            }
            if(goExp||goRaw){
               progress=new ProgressFrame("Creating New Files");
               this.add(progress);
               progress.setVisible(true);
               int totalNumSpots=0;
               for(int i=0; i<manager.getNumGrids(); i++){
                  totalNumSpots+=manager.getGrid(i).getNumOfSpots();
               }
               progress.setMaximum(totalNumSpots);
               progress.show();
               /*GeneData gd = null;
               SingleGeneImage currentGene;
               int[] autoThresh;
               if (afod.getOK()) autoThresh = afod.getThresholds();
               else{
            	   autoThresh = new int[4];
            	   autoThresh[0] = -1;
            	   autoThresh[1] = Integer.MAX_VALUE;
            	   autoThresh[2] = -1;
            	   autoThresh[3] = Integer.MAX_VALUE;
               }
               for(int i=0; i<manager.getNumGrids(); i++){
                  Grid g=manager.getGrid(i);
                  idRed.setSpots(i);
                  int aspot;
                  String gname;
                  boolean flagStatus = false; //used to store the state of the flag
                  for (int j=0; j<g.getNumOfSpots(); j++){
                     aspot=manager.getActualSpotNum(i,j);
                     gname=manager.getGeneName(i,aspot);
                     // begin modified by Michael Gordon for flagging
                     //note that j is the transformed spot number
                     flagStatus = flagman.checkFlag(i, j);
                     if(!gname.equalsIgnoreCase("empty")&&!gname.equalsIgnoreCase("blank")&&!gname.equalsIgnoreCase("missing")&&!gname.equalsIgnoreCase("none")&&!gname.equalsIgnoreCase("No Gene Specified")){
                    	 //modified to check spot j for flag status
                    // end modified by Michael Gordon for flagging (last 6/11/2007) - latest modification changes the way that flagged spots are dealt with 
                        currentGene=new SingleGeneImage(idRed.getCellPixels(i,aspot),idGreen.getCellPixels(i,aspot),idRed.getCellHeight(i,aspot), idRed.getCellWidth(i,aspot));
                        gd=currentGene.getData(method, params);
                        if(goExp){
                           expFileWriter.write(gname+'\t');
                           if(gd!=null && !flagStatus){	//modified 6/11/2007 by Michael Gordon
                        	   							//this makes it so that the gene name is written to the file but not the data if the spot is flagged
                        	   if ((gd.getRedForegroundTotal() >= autoThresh[0]) && (gd.getRedBackgroundTotal() <= autoThresh[1]) && (gd.getGreenForegroundTotal() >= autoThresh[2]) && (gd.getGreenBackgroundTotal() <= autoThresh[3])) {
                        		   expFileWriter.write(String.valueOf(gd.getRatio(ratiomethod)));
                        	   }
                        	   else {
                        		   expFileWriter.write("\t");
                        		   flagStatus = true;	//this spot has been auto-flagged
                        	   }
                              
                           } else{
                              expFileWriter.write("\t");
                           }
                           expFileWriter.write('\n');
                        }
                        if(goRaw){
                           rawFileWriter.write(gname+'\t');
                           if(gd!=null && !flagStatus){	//modified 6/11/2007 by Michael Gordon
                        	   							//see above
                              rawFileWriter.write(String.valueOf(gd.getRedForegroundTotal()) + "\t");
                              rawFileWriter.write(String.valueOf(gd.getRedBackgroundTotal()) + "\t");
                              rawFileWriter.write(String.valueOf(gd.getGreenForegroundTotal()) + "\t");
                              rawFileWriter.write(String.valueOf(gd.getGreenBackgroundTotal()) + "\t");
                              rawFileWriter.write(String.valueOf(gd.getRedForegroundAvg()) + "\t");
                              rawFileWriter.write(String.valueOf(gd.getRedBackgroundAvg()) + "\t");
                              rawFileWriter.write(String.valueOf(gd.getGreenForegroundAvg()) + "\t");
                              rawFileWriter.write(String.valueOf(gd.getGreenBackgroundAvg()));
                           } else{
                              rawFileWriter.write("\t");
                           }
                           rawFileWriter.write("\n");
                        }
                        progress.addValue(1);
                     }
                  }
               }*/
               if (allGeneData == null) allGeneData = new AllGeneData(manager, flagman, autoflagman, idRed, idGreen, method, ratiomethod, params, afod, this );
               if (!allGeneData.valid) allGeneData.calculate();
               for (int i = 0; i < allGeneData.nSpots; i++) {
            	   String gname = allGeneData.getGeneName(i);
            	   boolean flagStatus = allGeneData.getFlagStatus(i);
            	   if(!gname.equalsIgnoreCase("empty")&&!gname.equalsIgnoreCase("blank")&&!gname.equalsIgnoreCase("missing")&&!gname.equalsIgnoreCase("none")&&!gname.equalsIgnoreCase("No Gene Specified")){
            		   if(goExp) {
            			   expFileWriter.write(gname+'\t');
            			   if(!flagStatus) {
            				   expFileWriter.write(String.valueOf(allGeneData.getRatio(i)));
            			   }
            			   else expFileWriter.write("\t");
            			   expFileWriter.write("\n");
            		   }
            		   if (goRaw) {
            			   rawFileWriter.write(gname+'\t');
            			   if(!flagStatus) {
            				   rawFileWriter.write(String.valueOf(allGeneData.getRedFGTotal(i))+"\t");
            				   rawFileWriter.write(String.valueOf(allGeneData.getRedBGTotal(i))+"\t");
            				   rawFileWriter.write(String.valueOf(allGeneData.getGreenFGTotal(i))+"\t");
            				   rawFileWriter.write(String.valueOf(allGeneData.getGreenBGTotal(i))+"\t");
            				   rawFileWriter.write(String.valueOf(allGeneData.getRedFGAvg(i))+"\t");
            				   rawFileWriter.write(String.valueOf(allGeneData.getRedBGAvg(i))+"\t");
            				   rawFileWriter.write(String.valueOf(allGeneData.getGreenFGAvg(i))+"\t");
            				   rawFileWriter.write(String.valueOf(allGeneData.getGreenBGAvg(i)));
            			   }
            			   else {
            				   rawFileWriter.write("\t\t\t\t\t\t\t");
            			   }
            			   rawFileWriter.write("\n");
            		   }
            	   }
            	   progress.addValue(1);
               }
               if (doExp){
                  expFileWriter.close();
               }
               if (doRaw){
                  rawFileWriter.close();
               }
               if(goExp){
                  boolean add=true;
                  String app=appendname;
                  if(!newExpFile&&appendname.toLowerCase().endsWith(".exp")) app=appendname.substring(0,appendname.lastIndexOf("."));
//START WORKING HERE!!!
//Whose note is that? --MRG 10/9/2006
                  if(!newExpFile) add=mergeFiles(expFile, new File(project.getPath()+app+File.separator+app+".exp"),temp, byname);
                  
                  if(add) project.addFile(file + File.separator + file + ".exp");
                  progress.dispose();
                  if(add) main.addExpFile(expFile.getPath());
                  if(!newExpFile&&add&&project.getGroupMethod()==Project.ALWAYS_CREATE){
                     
                     String shortfile = expFileName;
                     if(shortfile.toLowerCase().endsWith(".exp")) shortfile = shortfile.substring(0, shortfile.toLowerCase().lastIndexOf(".exp"));
                     if(shortfile.lastIndexOf(File.separator)!=-1) shortfile = shortfile.substring(shortfile.lastIndexOf(File.separator)+1);
                     
                     String old=appendname;
                     if(old!=null&&old.endsWith(".exp")) old = old.substring(0,old.lastIndexOf(".exp"));
                     if(old!=null&&old.lastIndexOf(File.separator)!=-1) old = old.substring(old.lastIndexOf(File.separator)+1);
                     
                     String groupFiles[] = project.getGroupFiles(old);
                     for(int i=0; i<groupFiles.length; i++){
                        GrpFile gf = new GrpFile(new File(project.getPath()+groupFiles[i]));
                        gf.setExpFile(shortfile);
                        try{
                           gf.writeGrpFile(project.getPath()+shortfile+File.separator+gf.getTitle());
                           project.addFile(shortfile + File.separator + gf.getTitle());
                        }catch(DidNotFinishException e3){}
                     }
                  }
               }
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         } catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error Writing Files");
            if(progress!=null)progress.dispose();
            e.printStackTrace();
            this.setTitle("SEGMENTATION");
         }
      }
      this.setTitle("SEGMENTATION");
      allGeneData.invalidate();
      autoflagman.clearAllFlags();
      afod = new AutoFlaggingOptionsDialog(this.main, this.project, this.manager, this);
   }
   /**
  public void createNewExpressionFile(String name, String colName, boolean newFile, String appendname, boolean byname, int method, int ratiomethod, Object params[]){
          final String theName = name;
          final String colname = colName;
          final String appendName = appendname;
          final boolean byName=byname;
          final int meth = method;
          final int ratioMethod = ratiomethod;
          final Object[] par = params;
          final boolean createNew = newFile;
   
          if(project!=null){
                  String file=theName;
                  if(file.toLowerCase().endsWith(".exp")) file=file.substring(0,file.lastIndexOf("."));
                  File f = new File(project.getPath()+file+File.separator+file+".exp");
                  System.out.println(f.getAbsolutePath());
                  System.exit(1);
                  int deleteFiles = JOptionPane.CANCEL_OPTION;
                  if(!f.exists()||(deleteFiles=JOptionPane.showConfirmDialog(null, "File Already Exists! Do You Wish To Overwrite?\nOverwriting The File Will Delete All Files Which Used The Previous File"))==JOptionPane.OK_OPTION){
                          try{
                                  if(deleteFiles==JOptionPane.OK_OPTION) f.getParentFile().delete();
                                  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                                  f.getParentFile().mkdirs();
   
                                  File temp = null;
                                  if(!createNew) temp = File.createTempFile(file, null);
   
                                  BufferedWriter bw;
                                  if(createNew) bw = new BufferedWriter(new FileWriter(f.getPath()));
                                  else bw = new BufferedWriter(new FileWriter(temp.getPath()));
                                  bw.write(colname);
   
                                  bw.write("\n");
   
                                  progress = new ProgressFrame("Creating New Expression File: " + file + ".exp");
                                  if(!createNew) progress.setTitle("Generating Expression Data");
                                  getDesktopPane().add(progress);
                                  progress.show();
                                  int totalNumSpots=0;
                                  for(int i=0; i<manager.getGridNum(); i++){
                                          totalNumSpots+=manager.getGrid(i).getNumOfSpots();	//gosh, why isn't there just constant somwhere with this defined!  MC 10/05
                                  }
                                  progress.setMaximum(totalNumSpots);
                                  //int num=0;
   
   
                                  for(int i=0; i<manager.getGridNum(); i++){
                                          Grid g = manager.getGrid(i);
                                          idRed.setSpots(i);
   
                                          for(int j=0; j<g.getNumOfSpots(); j++){
   
                                                  int aspot = manager.getActualSpotNum(i,j);
                                                  String gname = manager.getGeneName(i,aspot);
   
                                                  if(!gname.equalsIgnoreCase("empty")&&!gname.equalsIgnoreCase("blank")&&!gname.equalsIgnoreCase("missing")&&!gname.equalsIgnoreCase("none")&&!gname.equalsIgnoreCase("No Gene Specified")){
   
                                                          SingleGeneImage currentGene = new SingleGeneImage(idRed.getCellPixels(i,aspot),idGreen.getCellPixels(i,aspot),idRed.getCellHeight(i,aspot), idRed.getCellWidth(i,aspot));
   
                                                          GeneData gd = null;
   
                                                          gd = currentGene.getData(meth,par);
   
                                                          bw.write(gname + "\t");
   
                                                          if(gd!=null){
                                                                  bw.write(String.valueOf(gd.getRatio(ratioMethod)));
                                                          }
                                                          else{
                                                                  bw.write("\t");
                                                          }
                                                          bw.write("\n");
                                                          progress.addValue(1);
//							  num++;
                                                  }
                                          }
                                  }
   
                                  bw.close();
                                  boolean add=true;
   
                                  String app=appendName;
                                  if(!createNew&&appendName.toLowerCase().endsWith(".exp")) app=appendName.substring(0,appendName.lastIndexOf("."));
   
                                  if(!createNew) add=mergeFiles(f, new File(project.getPath()+app+File.separator+app+".exp"),temp, byName);
   
                                  if(add) project.addFile(file + File.separator + file + ".exp");
                                  progress.dispose();
                                  if(add) main.addExpFile(f.getPath());
                                  if(!createNew&&add&&project.getGroupMethod()==Project.ALWAYS_CREATE){
   
                                          String shortfile = theName;
                                          if(shortfile.toLowerCase().endsWith(".exp")) shortfile = shortfile.substring(0, shortfile.toLowerCase().lastIndexOf(".exp"));
                                          if(shortfile.lastIndexOf(File.separator)!=-1) shortfile = shortfile.substring(shortfile.lastIndexOf(File.separator)+1);
   
                                          String old=appendName;
                                          if(old!=null&&old.endsWith(".exp")) old = old.substring(0,old.lastIndexOf(".exp"));
                                          if(old!=null&&old.lastIndexOf(File.separator)!=-1) old = old.substring(old.lastIndexOf(File.separator)+1);
   
                                          String groupFiles[] = project.getGroupFiles(old);
                                          for(int i=0; i<groupFiles.length; i++){
                                                  GrpFile gf = new GrpFile(new File(project.getPath()+groupFiles[i]));
                                                  gf.setExpFile(shortfile);
                                                  try{
                                                          gf.writeGrpFile(project.getPath()+shortfile+File.separator+gf.getTitle());
                                                          project.addFile(shortfile + File.separator + gf.getTitle());
                                                  }catch(DidNotFinishException e3){}
                                          }
                                  }
                          }
                          catch(Exception e2){
                                  JOptionPane.showMessageDialog(null, "Error Writing Exp File");
                                  f.delete();
                                  if(progress!=null)progress.dispose();
                                  e2.printStackTrace();
   
                          }
                          setCursor(Cursor.getDefaultCursor());
                  }
          }
  }*/
   
   /**writes the raw expression data to disk in a tab-delimited format.  The .exp file and column name are stored as well as headers.
    *
    * @param rawFileName name to save the .raw file as
    * @param expFileName name of the corresponding .exp file (defaults to unknown)
    * @param colName name of column of corresponding .exp file (defaults to unknown)
    * @param method segmentation method
    * @param ratioMethod ratio method
    * @param params segmentation parameters
    */
   /**public void createNewRawDataFile(String rawFileName, String expFileName, String colName, int method, int ratioMethod, Object params[]){
          System.out.println("top of createNewRawDataFile");
   
          if(rawFileName.toLowerCase().endsWith(".raw")) rawFileName=rawFileName.substring(0,rawFileName.lastIndexOf("."));
          final String file=rawFileName;
          File f = new File(project.getPath()+file+File.separator+file+".raw");
          int deleteFiles = JOptionPane.CANCEL_OPTION;
          if(!f.exists()||(deleteFiles=JOptionPane.showConfirmDialog(null, "File Already Exists! Do You Wish To Overwrite?\nOverwriting The File Will Delete All Files Which Used The Previous File"))==JOptionPane.OK_OPTION){
                  try{
                          if(deleteFiles==JOptionPane.OK_OPTION) f.getParentFile().delete();
                          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                          f.getParentFile().mkdirs();
   
                          System.out.println("file: " + f);
                          SwingUtilities.invokeLater(new Runnable() {
                                  public void run() {
                                          progress = new ProgressFrame("creating new raw data file: " + file + ".raw");
                                  }
                          });
                          getDesktopPane().add(progress);
                          progress.show();
                          int totalNumSpots=0;
                          for(int i=0; i<manager.getGridNum(); i++)
                                 totalNumSpots+=manager.getGrid(i).getNumOfSpots();
                          progress.setMaximum(totalNumSpots);
   
                          BufferedWriter bw = new BufferedWriter(new FileWriter(f.getPath()), 4096);
                          bw.write(expFileName+ ":" +colName + "\tRedFGtot\tRedBGtot\tGrnFGtot\tGrnBGtot\tRedFGavg\tRedBGavg\tGrnFGavg\tGrnBGavg");
                          bw.write("\n");
   
                          //int num=0;
                          GeneData gd = null;
                          SingleGeneImage currentGene;
                          for(int i=0; i<manager.getGridNum(); i++){
                                  Grid g = manager.getGrid(i);
                                  idRed.setSpots(i);
   
                                  for(int j=0; j<g.getNumOfSpots(); j++){
                                          int aspot = manager.getActualSpotNum(i,j);
                                          String gname = manager.getGeneName(i,aspot);
   
                                          if(!gname.equalsIgnoreCase("empty")&&!gname.equalsIgnoreCase("blank")&&!gname.equalsIgnoreCase("missing")&&!gname.equalsIgnoreCase("none")&&!gname.equalsIgnoreCase("No Gene Specified")){
                                                  //SingleGeneImage currentGene = new SingleGeneImage(idRed.getCellPixels(i,aspot),idGreen.getCellPixels(i,aspot),idRed.getCellHeight(i,aspot), idRed.getCellWidth(i,aspot));
                                                  currentGene = new SingleGeneImage(idRed.getCellPixels(i,aspot),idGreen.getCellPixels(i,aspot),idRed.getCellHeight(i,aspot), idRed.getCellWidth(i,aspot));
                                                  //GeneData gd = null;
                                                  gd = currentGene.getData(method, params);
   
                                                  bw.write(gname + "\t");
                                                  if(gd!=null){
                                                          bw.write(String.valueOf(gd.getRedForegroundTotal()) + "\t");
                                                          bw.write(String.valueOf(gd.getRedBackgroundTotal()) + "\t");
                                                          bw.write(String.valueOf(gd.getGreenForegroundTotal()) + "\t");
                                                          bw.write(String.valueOf(gd.getGreenBackgroundTotal()) + "\t");
                                                          bw.write(String.valueOf(gd.getRedForegroundAvg()) + "\t");
                                                          bw.write(String.valueOf(gd.getRedBackgroundAvg()) + "\t");
                                                          bw.write(String.valueOf(gd.getGreenForegroundAvg()) + "\t");
                                                          bw.write(String.valueOf(gd.getGreenBackgroundAvg()));
                                                  }
                                                  else{
                                                          bw.write("\t");
                                                  }
                                                  bw.write("\n");
                                                  progress.addValue(1);
                                                  progress.dispose();
                                                  //num++;
                                          }
                                  }
                          }
                          bw.close();
                          System.out.println("bottom of createNewRawDataFile");
                  }
                  catch(Exception e2){
                          JOptionPane.showMessageDialog(null, "Error Writing .raw File");
                          f.delete();
                          if(progress!=null)progress.dispose();
                          e2.printStackTrace();
                  }
                  setCursor(Cursor.getDefaultCursor());
          }
  }	  */
   
   //merges files for appending expression files
   private boolean mergeFiles(File output, File append, File temp, boolean byName){
      if(temp!=null&&append.exists()&&temp.exists()){
         try{
            
            output.getParentFile().mkdirs();
            
            ExpFile exp1 = new ExpFile(append);
            ExpFile exp2 = new ExpFile(temp);
            
            progress.setTitle("Appending Expression File");
            progress.setValue(0);
            
            BufferedWriter bw = new BufferedWriter(new FileWriter(output));
            for(int i=0; i<exp1.getColumns(); i++){
               bw.write(exp1.getLabel(i)+"\t");
            }
            bw.write(exp2.getLabel(0));
            
            bw.write("\n");
            
            if(byName){
               progress.setMaximum(Math.max(exp1.numGenes(),exp2.numGenes())+exp1.numGenes());
               boolean[] usedNew = new boolean[exp2.numGenes()];
               for(int j=0; j<usedNew.length; j++){
                  usedNew[j] = false;
               }
               
               for(int i=0; i<exp1.numGenes(); i++){
                  int pos;
                  String comments="", comments1=null, comments2=null;
                  bw.write(exp1.getGeneName(i)+"\t");
                  double data[]=exp1.getData(i);
                  for(int j=0; j<data.length; j++){
                     bw.write(""+data[j]+"\t");
                  }
                  comments1=exp1.getGene(i).getComments();
                  
                  if((pos=exp2.findGeneName(exp1.getGeneName(i)))!=-1){
                     double data2[]=exp2.getData(pos);
                     bw.write(""+data2[0]+"\t");
                     comments2=exp2.getGene(pos).getComments();
                     usedNew[pos]=true;
                  } else bw.write(""+Double.POSITIVE_INFINITY+ "\t");
                  
                  if(comments1!=null) comments+=comments1 + " ";
                  if(comments2!=null) comments+=comments2;
                  bw.write(comments);
                  bw.write("\n");
                  progress.addValue(1);
                  
               }
               
               for(int j=0; j<usedNew.length; j++){
                  if(usedNew[j] == false){
                     bw.write(exp2.getGeneName(j)+"\t");
                     double data[]=exp1.getData(0);
                     for(int p=0; p<data.length; p++){
                        bw.write(""+Double.POSITIVE_INFINITY+"\t");
                     }
                     double data2[]=exp2.getData(j);
                     bw.write(""+data2[0]+"\t");
                     String comments="",comments2;
                     comments2=exp2.getGene(j).getComments();
                     if(comments2!=null) comments=comments2;
                     bw.write(comments);
                     bw.write("\n");
                     progress.addValue(1);
                  }
               }
            } else{
               for(int i=0; i<exp1.numGenes(); i++){
                  String comments="", comments1=null, comments2=null;
                  bw.write(exp1.getGeneName(i)+"\t");
                  double data[]=exp1.getData(i);
                  for(int j=0; j<data.length; j++){
                     bw.write(""+data[j]+"\t");
                  }
                  comments1=exp1.getGene(i).getComments();
                  
                  if(exp2.numGenes()>i){
                     double data2[]=exp2.getData(i);
                     bw.write(""+data2[0]+"\t");
                     comments2=exp2.getGene(i).getComments();
                  } else bw.write(""+Double.POSITIVE_INFINITY+ "\t");
                  
                  if(comments1!=null) comments+=comments1 + " ";
                  if(comments2!=null) comments+=comments2;
                  bw.write(comments);
                  bw.write("\n");
                  progress.addValue(1);
               }
               
            }
            
            bw.write("/**Gene Info**/"+"\n");
            for(int i=0; i<exp1.numGenes(); i++){
               Gene g = exp1.getGene(i);
               String n = g.getName();
               String a = g.getAlias();
               String c = g.getChromo();
               String l = g.getLocation();
               String p = g.getProcess();
               String fl = g.getFunction();
               String co = g.getComponent();
               if(n!=null) bw.write(n+"\t" + (a!=null?a:" ")+"\t" + (c!=null?c:" ")+"\t" + (l!=null?l:" ")+"\t" + (p!=null?p:" ")+"\t" + (fl!=null?fl:" ")+"\t" +(co!=null?co:" ")+"\n");
               progress.addValue(1);
            }
            
            bw.close();
            return true;
            
         } catch(Exception e){
            JOptionPane.showMessageDialog(this,"Error! Could Not Append File");
            e.printStackTrace();
            temp.delete();
            return false;
         }
      } else{
         JOptionPane.showMessageDialog(this,"Error! Could Not Append File");
         if(temp!=null&&temp.exists()) temp.delete();
         return false;
      }
   }
   
   protected SegmentPanel getSegmentPanel() {
	   return infoPanel;
   }
   
   /**
    * a public function causing SegmentFramel to jump to a particular spot on the grid
    * @param grid indexed grid number (0 &lt;= grid &lt; manager.getNumGrids()) 
    * @param spot indexed spot number (0 &lt;= spot &lt; manager.getGrid(grid).getNumOfSpots())
    */
   public void jumpToSpot(int grid, int spot) {
	   infoPanel.jumpToSpot(grid, spot);
   }
   
   public void doClickOnChangeButton() {
	   infoPanel.doClickOnChangeButton();
   }   
}