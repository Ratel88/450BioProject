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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.*;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import magictool.filefilters.GifFilter;
import magictool.filefilters.NoEditFileChooser;
import slider.MThumbSlider;
import slider.MetalMThumbSliderUI;

/**
 * TableFrame is a frame which displays colored table containing the data from an expression file.
 * The genes shown may be limited to a group file within an expression file. The color scale can be
 * altered by the user.
 */
public class TableFrame extends JInternalFrame {

  private JPanel contentPane = new JPanel();
  private JScrollPane jScrollPane1;
  private DefaultTableCellRenderer tablecellrenderer = new DefaultTableCellRenderer();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel paramsPanel = new JPanel();
  private JPanel pixPanel = new JPanel();
  private JLabel pixLabel = new JLabel();
  private JTextField pixTextField = new JTextField();
  private VerticalLayout verticalLayout1 = new VerticalLayout();
  private JButton heightButton = new JButton();
  private JPanel sliderPanel = new JPanel();
  private VerticalLayout verticalLayout2 = new VerticalLayout();
  private JLabel black, centerLabel, white;
  private JPanel labelsPanel;
  /*begin edit 6-6-2007 by Michael Gordon*/
  private JTextField whiteTextField = new JTextField();
  private JTextField centerTextField = new JTextField();
  private JTextField blackTextField = new JTextField();
  /*end edit 6-6-2007 by Michael Gordon*/
  private JMenuBar jMenuBar1 = new JMenuBar();
  private JMenu filemenu = new JMenu();
  private JMenuItem print = new JMenuItem();
  private JMenuItem saveMenu = new JMenuItem();
  private JMenuItem close = new JMenuItem();
  private JMenu colormenu = new JMenu();
  private JCheckBoxMenuItem graymenu = new JCheckBoxMenuItem();
  private JCheckBoxMenuItem rgmenu = new JCheckBoxMenuItem();
  private JMenu editMenu = new JMenu();
  private JMenuItem findMenu = new JMenuItem();
  private JMenuItem decimalMenu = new JMenuItem();
  private JCheckBoxMenuItem infoMenu = new JCheckBoxMenuItem();
  private DecimalFormat labelFormat = new DecimalFormat("0.##");
  private Project project;

  private ColorLabel colorLabel; //color gradient label
  /**table displayed containing the data from the expression file*/
  protected PrintableTable printableTable;
  /**expression file displayed in the table*/
  protected ExpFile expMain;
  /**group file displayed in the table*/
  protected GrpFile grpFile;
  /**whether or not the frame has been initialized*/
  private boolean init = false;
  /**a ComponentListener for resizing the columns automatically when the frame is resized*/
  protected PrintableTableColumnAdjuster ptca;

  //value for determining color scale
  private float minvalue;
  private float maxvalue;
  private float radius;
  private float center;
  private float actualmax, actualmin;

  /**number of pixels per line in the table*/
  protected int pixPerLine;

  private MThumbSlider mSlider; //slider for color scale

  /**
   * Constructs a frame containing a table of all genes in the specified expression file
   * @param expMain expression file to display
   * @param project project associated with the table
   */
  public TableFrame(ExpFile expMain, Project project) {
    this(expMain, new GrpFile(), project);
  }

  /**
   * Constructs a frame containing a table of the genes in the group file from the specified expression file
   * @param expMain expression file to display
   * @param grp group file containing group of genes to be displayed
   * @param project project associated with the table
   */
  public TableFrame(ExpFile expMain, GrpFile grp, Project project) {
    this.expMain = expMain;
    this.project=project;
    this.grpFile = grp;
    
    constructTable(false);

    //sets the default values for the color scale
    minvalue = actualmin = expMain.getMinExpValue();
    maxvalue = actualmax = expMain.getMaxExpValue();
    center = minvalue + (maxvalue-minvalue)/2;

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  //inititalizes the frame
  @SuppressWarnings("unchecked")
  private void jbInit() throws Exception {
	  this.getRootPane().setDefaultButton(heightButton);	//added 6/14/2007 by Michael Gordon
    contentPane.setLayout(borderLayout1);
    this.setClosable(true);
    this.setIconifiable(true);
    this.setJMenuBar(jMenuBar1);
    this.setMaximizable(true);
    this.setResizable(true);
    this.setTitle("Data Table");
    int n = 3;
    int min=(int)minvalue;
    int max=(int)maxvalue;
    mSlider = new MThumbSlider(n, 0, 1000);
    mSlider.setUI(new MetalMThumbSliderUI());
    colorLabel = new ColorLabel((double)minvalue,(double)maxvalue,(double)minvalue,(double)maxvalue,(double)((minvalue+maxvalue)/2),Color.white,new Color(153,153,153),Color.black);

    white = new JLabel("White", JLabel.LEFT);
    white.setForeground(Color.white);

    black = new JLabel("Black", JLabel.CENTER);
    black.setForeground(Color.black);
    black.setHorizontalAlignment(SwingConstants.RIGHT);

    centerLabel = new JLabel("Center", JLabel.RIGHT);
    centerLabel.setForeground(new Color(255/2,255/2,255/2));
    centerLabel.setHorizontalAlignment(SwingConstants.CENTER);

    /*begin edit 6-6-2007 by Michael Gordon*/ 
    KeyListener colorKeyListener = new KeyListener() {
    	public void keyPressed(KeyEvent keyEvent) {
    	//	color_keyPressed(keyEvent);
    	}
    	public void keyReleased(KeyEvent keyEvent) {
    		color_keyTyped(keyEvent);
    	}
    	public void keyTyped(KeyEvent keyEvent) {
    		//color_keyTyped(keyEvent);
    	}
    };
    
    whiteTextField.addKeyListener(colorKeyListener);
    centerTextField.addKeyListener(colorKeyListener);
    blackTextField.addKeyListener(colorKeyListener);
    Dimension textFieldPreferredDimension = new Dimension(35,21);
    whiteTextField.setPreferredSize(textFieldPreferredDimension);
    whiteTextField.setMinimumSize(textFieldPreferredDimension);
    whiteTextField.setMaximumSize(textFieldPreferredDimension);
    centerTextField.setPreferredSize(textFieldPreferredDimension);
    centerTextField.setMinimumSize(textFieldPreferredDimension);
    centerTextField.setMaximumSize(textFieldPreferredDimension);
    blackTextField.setPreferredSize(textFieldPreferredDimension);
    blackTextField.setMinimumSize(textFieldPreferredDimension);
    blackTextField.setMaximumSize(textFieldPreferredDimension);
    whiteTextField.setHorizontalAlignment(SwingConstants.LEFT);
    centerTextField.setHorizontalAlignment(SwingConstants.CENTER);
    blackTextField.setHorizontalAlignment(SwingConstants.RIGHT);
    /*end edit 6-6-2007 by Michael Gordon*/
    
    
    pixLabel.setText("Pixels Per Line:");
    paramsPanel.setPreferredSize(new Dimension(582, 150));
    paramsPanel.setLayout(verticalLayout1);
    pixTextField.setPreferredSize(new Dimension(45, 21));
    heightButton.setText("Update Line Height");
    heightButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        heightButton_actionPerformed(e);
      }
    });
    rootPane.setDefaultButton(heightButton);
    sliderPanel.setBorder(BorderFactory.createEtchedBorder());
    pixPanel.setBorder(BorderFactory.createEtchedBorder());
    print.setText("Print...");
    print.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK, false));
    print.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        print_actionPerformed(e);
      }
    });
    saveMenu.setText("Save Image...");
    saveMenu.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_MASK, false));
    saveMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        saveMenu_actionPerformed(e);
      }
    });
    close.setText("Close");
    close.setAccelerator(javax.swing.KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.CTRL_MASK, false));
    close.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        close_actionPerformed(e);
      }
    });
    filemenu.setText("File");
    colormenu.setText("Color");
    graymenu.setText("Grayscale");
    graymenu.setState(true);
    graymenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        graymenu_actionPerformed(e);
      }
    });
    rgmenu.setText("Red/Green");
    rgmenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        rgmenu_actionPerformed(e);
      }
    });
    colorLabel.setText("Color Label");
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
    infoMenu.setText("Show Gene Info");
    infoMenu.setState(false);
    infoMenu.addActionListener(new java.awt.event.ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		infoMenu_actionPerformed(e);
    	}
    });
    this.getContentPane().add(contentPane,  BorderLayout.CENTER);
    this.setSize(new Dimension(500, 580));
    this.setLocation(100,100);
    contentPane.add(jScrollPane1, BorderLayout.CENTER);
    this.getContentPane().add(paramsPanel, BorderLayout.SOUTH);
    paramsPanel.add(sliderPanel, null);
    pixPanel.add(pixLabel, null);
    pixPanel.add(pixTextField, null);
    pixPanel.add(heightButton, null);
    paramsPanel.add(pixPanel, null);
    jMenuBar1.add(filemenu);
    jMenuBar1.add(editMenu);
    jMenuBar1.add(colormenu);
    filemenu.add(saveMenu);
    filemenu.add(print);
    filemenu.addSeparator();
    filemenu.add(close);
    colormenu.add(rgmenu);
    colormenu.add(graymenu);
    editMenu.add(findMenu);
    editMenu.add(decimalMenu);
    editMenu.add(infoMenu);

    printableTable.paintTable(minvalue,center,maxvalue,printableTable.getRowHeight());
    setParams();
    init = true;
  }

  //sets up the parameters
  @SuppressWarnings("unchecked")
  private void setParams(){
    pixTextField.setText(Integer.toString(printableTable.getRowHeight()));

    mSlider = new MThumbSlider(3, 0, 1000);
    mSlider.setUI(new MetalMThumbSliderUI());
    mSlider.setValueAt(0, 0);
    mSlider.setValueAt(1000, 1);	//should be 1000
    mSlider.setValueAt(500, 2);		//should be 500
    mSlider.setFillColorAt(Color.white,  0);
    mSlider.setFillColorAt(new Color(255/2,255/2,255/2), 1);
    mSlider.setTrackFillColor(Color.black);

    Hashtable imageDictionary = new Hashtable();
    imageDictionary.put(new Integer(0), new JLabel(labelFormat.format(convertSlider(0))));
    imageDictionary.put(new Integer(250), new JLabel(labelFormat.format(convertSlider(250))));
    imageDictionary.put(new Integer(500), new JLabel(labelFormat.format(convertSlider(500))));
    imageDictionary.put(new Integer(750), new JLabel(labelFormat.format(convertSlider(750))));
    imageDictionary.put(new Integer(1000), new JLabel(labelFormat.format(convertSlider(1000))));

    mSlider.setMinorTickSpacing(1000/8);
    mSlider.setMajorTickSpacing(1000/4);
    mSlider.setPaintTicks(true);
    mSlider.setPaintLabels(true);
    mSlider.setLabelTable(imageDictionary);

    labelsPanel = new JPanel(new GridLayout(1,3));	//changed to "6" from "3" 6-6-2007 MRG
    colorLabel.setBeginEndValues((double)minvalue,(double) maxvalue);
    colorLabel.setMinMax((double)minvalue,(double) maxvalue);
    colorLabel.setCenter((double)center);
    colorLabel.setColors(Color.white, new Color(153,153,153),Color.black);
    colorLabel.showLabels();

    sliderPanel.setLayout(verticalLayout2);
    sliderPanel.add(labelsPanel);
    sliderPanel.add(mSlider);
    sliderPanel.add(colorLabel, null);

    labelsPanel.add(white);
    labelsPanel.add(whiteTextField);	//added 6-6-2007 by Michael Gordon
    labelsPanel.add(centerLabel);
    labelsPanel.add(centerTextField);	//added 6-6-2007 by Michael Gordon
    labelsPanel.add(black);
    labelsPanel.add(blackTextField);	//added 6-6-2007 by Michael Gordon

    mSlider.setMiddleRange();

    mSlider.addChangeListener(new ChangeListener(){
        public void stateChanged(ChangeEvent e){
          mSlider_stateChanged(e);
        }
    });

      centerLabel.setText("Center: ");
      centerTextField.setText(labelFormat.format(convertSlider(mSlider.getValueAt(2)))); //+
      white.setText("White: ");
      whiteTextField.setText(labelFormat.format(convertSlider(mSlider.getValueAt(0))));//
      black.setText("Black: ");
      blackTextField.setText(labelFormat.format(convertSlider(mSlider.getValueAt(1))));//

  }

  private double convertSlider(int val){
    return actualmin + (val/1000.0)*(actualmax-actualmin);
  }
  
  private int convertToSlider(double actual) {
	  return (int)((1000*(actual-actualmin))/(actualmax-actualmin));
  }

  //changes the color scale when user moves the sliders
  private void mSlider_stateChanged(ChangeEvent e){
      mSlider.setMiddleRange();
      /*the following three lines are no longer necessary due to the text field type change
      centerLabel.setText("Center: "+labelFormat.format(convertSlider(mSlider.getValueAt(2))));
      white.setText((jTable.getType()==jTable.GRAYSCALE?"White: ":"Green: ")+labelFormat.format(convertSlider(mSlider.getValueAt(0))));
      black.setText((jTable.getType()==jTable.GRAYSCALE?"Black: ":"Red: ")+labelFormat.format(convertSlider(mSlider.getValueAt(1))));
      */
      centerTextField.setText(labelFormat.format(convertSlider(mSlider.getValueAt(2))));
      whiteTextField.setText(labelFormat.format(convertSlider(mSlider.getValueAt(0))));
      blackTextField.setText(labelFormat.format(convertSlider(mSlider.getValueAt(1))));
      
      minvalue=(float)convertSlider(mSlider.getValueAt(0));
      center=(float)convertSlider(mSlider.getValueAt(2));
      maxvalue=(float)convertSlider(mSlider.getValueAt(1));
      printableTable.paintTable(minvalue, center,maxvalue, printableTable.getRowHeight());
      colorLabel.setBeginEndValues((double)minvalue,(double)maxvalue);
      colorLabel.setCenter((double)center);

  }
  
  private void color_keyTyped(KeyEvent keyEvent) {
	  
	  if ((keyEvent.getKeyCode() != KeyEvent.VK_ENTER) && (keyEvent.getKeyCode() != KeyEvent.VK_BACK_SPACE) && (keyEvent.getKeyCode() != KeyEvent.VK_DELETE)) {
	  
		  //get all the text field values
		  String centerText = centerTextField.getText();
		  String whiteText = whiteTextField.getText();
		  String blackText = blackTextField.getText();
		  
		  //parse these values to doubles
		  double centerv, white, black;
		  try {
			  centerv = Double.parseDouble(centerText);
			  if (centerv < 0.0) throw new NegativeNumberException();
		  } catch (NumberFormatException e) {
			  JOptionPane.showMessageDialog(this, "Error: The value for the center color does not appear to be a number.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
			  return;
		  } catch (NegativeNumberException e2) {
			  JOptionPane.showMessageDialog(this, "Error: The value for the center color must be a number greater than zero.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
			  return;
		  }
		  try {
			  white = Double.parseDouble(whiteText);
			  if (white < 0.0) throw new NegativeNumberException();
		  } catch (NumberFormatException e) {
			  JOptionPane.showMessageDialog(this, "Error: The value for the white color does not appear to be a number.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
			  return;
		  } catch (NegativeNumberException e2) {
			  JOptionPane.showMessageDialog(this, "Error: The value for the white color must be a number greater than zero.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
			  return;
		  }
		  try {
			  black = Double.parseDouble(blackText);
			  if (black < 0.0) throw new NegativeNumberException();
		  } catch (NumberFormatException e) {
			  JOptionPane.showMessageDialog(this, "Error: The value for the black color does not appear to be a number.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
			  return;
		  } catch (NegativeNumberException e2) {
			  JOptionPane.showMessageDialog(this, "Error: The value for the black color must be a number greater than zero.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
			  return;
		  }
	  
		  //set slider values
		  int sliderCenter = convertToSlider(centerv);
		  int sliderWhite = convertToSlider(white);
		  int sliderBlack = convertToSlider(black);
		  mSlider.setValueAt(sliderCenter, 2);
		  mSlider.setValueAt(sliderWhite, 0);
		  mSlider.setValueAt(sliderBlack, 1);
		  
		  //set the values in the program
		  minvalue = (float)white;
		  center = (float)centerv;
		  maxvalue = (float)black;
	  
		  //redraw table
		  printableTable.paintTable(minvalue, center,maxvalue, printableTable.getRowHeight());
		  colorLabel.setBeginEndValues((double)minvalue,(double)maxvalue);
		  colorLabel.setMinMax((double)minvalue,(double) maxvalue);
		  colorLabel.setCenter((double)center);
		  this.repaint();
	  }
  }

  //changes the height of each cell in the table
  private void heightButton_actionPerformed(ActionEvent e) {
      try{
        pixPerLine = Integer.parseInt(pixTextField.getText().trim());
        minvalue=(float)convertSlider(mSlider.getValueAt(0));
        center=(float)convertSlider(mSlider.getValueAt(2));
        maxvalue=(float)convertSlider(mSlider.getValueAt(1));
        colorLabel.setBeginEndValues((double)minvalue,(double)maxvalue);
        colorLabel.setCenter((double)center);
        printableTable.paintTable(minvalue, center,maxvalue, pixPerLine);
      }
      catch(Exception e1){
        pixTextField.setText(""+printableTable.getRowHeight());
      }
  }

  //prints the table
  private void print_actionPerformed(ActionEvent e) {

    Thread thread = new Thread(){
      public void run(){
        PrinterJob pj=PrinterJob.getPrinterJob();
        PageFormat pf = pj.pageDialog(pj.defaultPage());
        printableTable.setDoubleBuffered(false);
        pj.setPrintable(printableTable, pf);
        colorLabel.showLabels();
        printableTable.header=colorLabel;
        if(pj.printDialog()){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          try{
            pj.print();
          }catch (Exception PrintException) {}
          setCursor(Cursor.getDefaultCursor());
        }
        colorLabel.showLabels();
        printableTable.setDoubleBuffered(true);
      }
    };
    thread.start();

  }

   private void saveMenu_actionPerformed(ActionEvent e) {
    Thread thread = new Thread(){
      public void run(){
        try{
          NoEditFileChooser jfc = new NoEditFileChooser(MainFrame.fileLoader.getFileSystemView());
          jfc.setFileFilter(new GifFilter());
          jfc.setDialogTitle("Create New Gif File...");
          jfc.setApproveButtonText("Select");
          File direct = new File(project.getPath() + "images" + File.separator);

          if(!direct.exists()) direct.mkdirs();
          jfc.setCurrentDirectory(direct);
          int result = jfc.showSaveDialog(null);

          if (result == JFileChooser.APPROVE_OPTION) {
            File fileobj = jfc.getSelectedFile();
            String name = fileobj.getPath();
            if(!name.endsWith(".gif")) name+=".gif";

            printableTable.setHeader(colorLabel);
            saveImage(name);
          }
        }
        catch(Exception e1){
          JOptionPane.showMessageDialog(null,"Failed To Create Image");
          e1.printStackTrace();
        }
      }
    };
    thread.start();
  }

  private void saveImage(String name){
    try{
      int number=1;
      printableTable.saveImage(name, number=(int)Math.ceil(printableTable.getMegaPixels()/project.getImageSize()));

      if(number>1){
        String tn = name.substring(name.lastIndexOf(File.separator), name.lastIndexOf("."));
        String tempname = name.substring(0, name.lastIndexOf(File.separator)) + tn + ".html";
        BufferedWriter bw = new BufferedWriter(new FileWriter(tempname));
        bw.write("<html><header><title>" + name + "</title></header>");
        bw.write("<body>");
        bw.write("<table cellpadding=0 cellspacing=0 border=0");
        for(int i=0; i<number; i++){
          bw.write("<tr><td><img src=" + tn.substring(1) + "_images" + tn + i + ".gif border=0></td></tr>");
        }
        bw.write("</table></body></html>");
        bw.close();
      }
    }
    catch(Exception e){
      JOptionPane.showMessageDialog(this,"Failed To Create Image");
    }


  }

  //closes the winodw
  private void close_actionPerformed(ActionEvent e) {
    dispose();
  }

  //moves the color scale to red-green
  private void rgmenu_actionPerformed(ActionEvent e) {
      graymenu.setState(false);
      rgmenu.setState(true);
      printableTable.setType(PrintableTable.REDGREEN);
      printableTable.paintTable(minvalue, center,maxvalue, printableTable.getRowHeight());
      //centerLabel.setText("Center: "+labelFormat.format(convertSlider(mSlider.getValueAt(2))));
      centerLabel.setText("Center: ");
      //white.setText("Green: "+labelFormat.format(convertSlider(mSlider.getValueAt(0))));
      white.setText("Green: ");
      white.setForeground(new Color(51,153,51));
      //black.setText("Red: "+labelFormat.format(convertSlider(mSlider.getValueAt(1))));
      black.setText("Red: ");
      black.setForeground(new Color(255,0,0));
      centerLabel.setForeground(new Color(0,0,0));
      colorLabel.setColors(Color.green,Color.black, Color.red);
      mSlider.setFillColorAt(Color.green,  0);
      mSlider.setFillColorAt(Color.black, 1);
      mSlider.setTrackFillColor(Color.red);
  }

  //moves the color scale to grey
  private void graymenu_actionPerformed(ActionEvent e) {
      graymenu.setState(true);
      rgmenu.setState(false);
      printableTable.setType(PrintableTable.GRAYSCALE);
      printableTable.paintTable(minvalue, center,maxvalue, printableTable.getRowHeight());
      //centerLabel.setText("Center: "+labelFormat.format(convertSlider(mSlider.getValueAt(2))));
      centerLabel.setText("Center: ");
      //white.setText("White: "+labelFormat.format(convertSlider(mSlider.getValueAt(0))));
      white.setText("White: ");
      //black.setText("Black: "+labelFormat.format(convertSlider(mSlider.getValueAt(1))));
      black.setText("Black: ");
      black.setForeground(Color.black);
      white.setForeground(Color.white);
      centerLabel.setForeground(new Color(255/2,255/2,255/2));
      colorLabel.setColors(Color.white,new Color(255/2,255/2,255/2), Color.black);
      mSlider.setFillColorAt(Color.white,  0);
      mSlider.setFillColorAt(new Color(255/2,255/2,255/2), 1);
      mSlider.setTrackFillColor(Color.black);
  }
  
  @SuppressWarnings("unchecked")
  private void infoMenu_actionPerformed(ActionEvent e) {
	  if (!infoMenu.getState()) {
		  //it WAS true, now it should be false
		  infoMenu.setState(false);
		  printableTable.setShowInfo(false);
		  printableTable.repaint();
		  jScrollPane1.repaint();
		  jScrollPane1.revalidate();
		  printableTable.paintTable(minvalue,center,maxvalue,printableTable.getRowHeight());
		  ptca.adjustColumns();
		  //System.out.println("I have "+ printableTable.getDefaultTableModel().getColumnCount() + " columns.");
	  }
	  else {
		  //it WAS false, now it should be true
		  infoMenu.setState(true);
		  printableTable.setShowInfo(true);
		  printableTable.repaint();
		  jScrollPane1.repaint();
		  jScrollPane1.revalidate();
		  jScrollPane1.updateUI();
		  printableTable.paintTable(minvalue,center,maxvalue,printableTable.getRowHeight());
		  printableTable.updateUI();
		  ptca.adjustColumns();
		  //System.out.println("I have "+ printableTable.getDefaultTableModel().getColumnCount() + " columns.");
	  }
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
          printableTable.setDecimalFormat(df);
        }
        else JOptionPane.showMessageDialog(this,"Error! You Must Enter An Integer Value Greater Than Or Equal To 1.","Error!", JOptionPane.ERROR_MESSAGE);
      }

    }catch(Exception e2){
      JOptionPane.showMessageDialog(this,"Error! You Must Enter An Integer Value Greater Than Or Equal To 1.","Error!", JOptionPane.ERROR_MESSAGE);
    }
  }
  
  private void findMenu_actionPerformed(ActionEvent e) {
	  try{
		  String geneName = "";
		  geneName = JOptionPane.showInputDialog(this,"Please enter the gene name: ");
		  if (geneName != null) {
			  //ListSelectionModel selectionModel = jTable.getSelectionModel();
			  DefaultTableModel dtm = printableTable.getDefaultTableModel();
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
						  printableTable.setColumnSelectionInterval(0, 0);
						  printableTable.setRowSelectionInterval(i, i);
						  //System.out.println(jTable.isCellSelected(i, 0));
						  Rectangle theSelectedCell = printableTable.getCellRect(i, 0, false);
						  printableTable.scrollRectToVisible(theSelectedCell);
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
   * Constructs a table from the expression file, group file, and project stored in the member variables of the class
   * @param info whether or not to include gene info in the table
   */
  @SuppressWarnings("unchecked")
  private void constructTable(boolean info) {
	    Vector v = new Vector();
	    Object o[] = grpFile.getGroup();
	    for(int i=0; i<o.length; i++){
	      v.addElement(o[i]);
	    }
	    if(v.size()==0) printableTable = new PrintableTable(expMain, new Vector(), PrintableTable.GRAYSCALE, true, info);
	    else printableTable = new PrintableTable(expMain, v,PrintableTable.GRAYSCALE, true, info);
	    printableTable.setAutoResizeMode(PrintableTable.AUTO_RESIZE_OFF);
	    jScrollPane1 = new JScrollPane(printableTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    	printableTable.paintTable(minvalue,center,maxvalue,printableTable.getRowHeight());
    	ptca = new PrintableTableColumnAdjuster(printableTable);
	    if (init){
	    	contentPane.remove(jScrollPane1);
	    	printableTable.repaint();
	    	contentPane.add(jScrollPane1, BorderLayout.CENTER);
	    	jScrollPane1.setViewportView(printableTable);
	    	jScrollPane1.revalidate();
	    	jScrollPane1.repaint();
	    	this.repaint();
	    }
  }
  
  public class NegativeNumberException extends IllegalArgumentException {
	  /**
	 * 
	 */
	private static final long serialVersionUID = 392463211392265662L;

		public NegativeNumberException(){}
  }

}

