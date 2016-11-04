package magictool.image;

import ij.ImagePlus;
import ij.io.FileSaver;
import ij.io.Opener;
import ij.process.ImageConverter;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.text.DecimalFormat;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import magictool.MagicToolApp;
import magictool.MainFrame;
import magictool.Project;
import magictool.image.MetaGridPanel;

/**
 * GriddingFrame is an internal frame which allows a user to set all the grids and
 * gridding properties for a microarray image.
 */
public class MetaGriddingFrame extends JInternalFrame {

  private JScrollPane scrollRight;
  private JScrollPane scrollLeft;
  private ImageDisplayPanel imageDisplayPanel;
  private JToolBar imageToolBar = new JToolBar();
  private JToggleButton zoomIn = new JToggleButton();
  private JToggleButton zoomOut = new JToggleButton();
  private JButton reset = new JButton();
  private JButton done = new JButton();
  private DecimalFormat df = new DecimalFormat("###.#");
  private JLabel statusBar = new JLabel();
  private TitledBorder titledBorder1;
  private JSplitPane jSplitPane1 = new JSplitPane();
  private JPanel jPanel1 = new JPanel();
  private JTabbedPane gridTabs = new JTabbedPane();
  private JDialog contrastDialog = new JDialog();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JLabel contrastLabel = new JLabel();
  private JSlider contrastSlider = new JSlider();
  private JTextField contrastInputField = new JTextField(4);
  private JLabel contrastInputFieldLabel = new JLabel();
  private JPanel jPanel2 = new JPanel();
  private Border border1;
  private Border border2;
  private JMenuBar menuBar = new JMenuBar();
  private JMenu fileMenu = new JMenu();
  private JMenuItem loadGridMenu = new JMenuItem();
  private JMenuItem saveGridMenu = new JMenuItem();
  private JMenuItem saveAsGridMenu = new JMenuItem();
  private JMenuItem finishMenu = new JMenuItem();
  private JMenu optionsMenu = new JMenu();
  private JMenuItem undoMenu = new JMenuItem();
  private JMenuItem rotateClockMenu = new JMenuItem();
  private JMenuItem rotateCounterClockMenu = new JMenuItem();
  private JMenuItem propertiesMenu = new JMenuItem();
  private JMenu saveImageMenu = new JMenu();
  private JMenuItem tiffMenu = new JMenuItem();
  private JMenuItem jpegMenu = new JMenuItem();
  private JMenuItem gifMenu = new JMenuItem();

  private MainFrame main; //parent frame
  private boolean doFinal=false; //whether or not to do the final initiation
  private String[] titles; //titles of the grid tabs
  private MetaGridPanel[] metaGridPanel; //panels in the grid tabs
  private int lastUpdateGrid = -1; //last updated grid (for undo)
  private boolean needSaved = false; //whether or not the grids need to be saved
  //private int newTopLeftX, newTopLeftY; //used for setting new positions after zooming
  //private int w, h;
  

  /**width of the image*/
  protected int width;
  /**height of the image*/
  protected int height;
  /**grid manager for the microarray image*/
  protected MetaGridManager manager;
  /**overlayed image*/
  protected Image image;
  /**project associated with this gridding frame*/
  protected Project project;
  /**file path for open grid file*/
  protected String gridFilePath = null;
  /**file path for red image*/
  protected String redPath = null;
  /**file path for green image*/
  protected String greenPath = null;


  /**
   * Constructs a gridding frame based on the file paths for the red and green images
   * as well as the specified project and grid manager.
   * @param project project associated with gridding frame
   * @param m grid manager for the mircroarray images
   * @param redPath file path for the red image
   * @param greenPath file path for the green image
   * @param main parent mainframe
   */
  public MetaGriddingFrame(Project project, MetaGridManager m, String redPath, String greenPath, MainFrame main) {
    this.manager = m;
    this.main=main;
    this.project = project;
    this.greenPath = greenPath;
    this.redPath = redPath;
    
    if(manager.getMetaGridNum()==0){
      manager.setMetaGridNum(1);
      doFinal=true;
    }
    else doFinal=false;
    titles = new String[manager.getMetaGridNum()];
    metaGridPanel = new MetaGridPanel[manager.getMetaGridNum()];

    imageInit();


    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }


  }

  private void jbInit() throws Exception {

    titledBorder1 = new TitledBorder("");
    border1 = BorderFactory.createLineBorder(Color.white,1);
    border2 = BorderFactory.createLineBorder(Color.black,2);
    zoomIn.setText("Zoom In");
    zoomOut.setText("Zoom Out");
    zoomIn.setMaximumSize(new Dimension(85, 30));
    zoomIn.setMinimumSize(new Dimension(85, 30));
    zoomIn.setPreferredSize(new Dimension(85, 30));
    zoomOut.setMaximumSize(new Dimension(90, 30));
    zoomOut.setMinimumSize(new Dimension(90, 30));
    zoomOut.setPreferredSize(new Dimension(90, 30));

    for (int i=0; i<titles.length; i++) {
        titles[i] = String.valueOf(i+1);
        metaGridPanel[i] = new MetaGridPanel(manager.getMetaGrid(i), this.getDisplay());
        gridTabs.add(metaGridPanel[i], titles[i]);
    }

    gridTabs.addChangeListener(new ChangeListener() {
      public void stateChanged(ChangeEvent ce) {
        JTabbedPane source = (JTabbedPane)ce.getSource();
        manager.setCurrentMetaGrid(source.getSelectedIndex());
        imageDisplayPanel.repaint();
        addButtonListeners();
      }
    });


    reset.setMaximumSize(new Dimension(85, 30));
    reset.setMinimumSize(new Dimension(85, 30));
    reset.setPreferredSize(new Dimension(85, 30));
    reset.setText("Fit to Screen");
    reset.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fitToScreen_actionPerformed(e);
        }
    });

    done.setAlignmentX((float) 5.0);
    done.setMaximumSize(new Dimension(85, 30));
    done.setMinimumSize(new Dimension(85, 30));
    done.setPreferredSize(new Dimension(85, 30));
    done.setText("Done!");
    done.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        done_actionPerformed(e);
        }
    });


    statusBar.setBorder(BorderFactory.createLoweredBevelBorder());

    this.setClosable(true);
    this.setIconifiable(true);
    this.setMaximizable(true);
    this.setResizable(true);
    this.getContentPane().setBackground(Color.lightGray);
    this.setToolTipText("");
    
    this.getContentPane().setLayout(borderLayout1);
    jSplitPane1.setOneTouchExpandable(true);
    contrastLabel.setText("Percent contrast change:");
    //contrastInputFieldLabel.setText("manual input");		//not in use
    contrastSlider.setMinimum(0);
    contrastSlider.setMaximum(2000);
    contrastSlider.setMajorTickSpacing(500);
    contrastSlider.setMinorTickSpacing(250);
    contrastSlider.setPaintTicks(true);
    contrastSlider.setDoubleBuffered(true);
    contrastSlider.setMinimumSize(new Dimension(400, 49));		//should this coincide with the max size of the panel?
    contrastSlider.setPaintLabels(true);
    contrastSlider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(ChangeEvent e) {
        contrastSlider_stateChanged(e);
      }
    });
    contrastInputField.setToolTipText("type in a new contrast percentage without\n" +
    									"the percentage sign (i.e. 3500) and hit\n" +
    									"enter.  100 is the natural contrast value.");
    contrastInputField.addActionListener(new java.awt.event.ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			contrastInputField_actionPerformed(e);
    		}
    });
    jPanel2.setBorder(BorderFactory.createEtchedBorder());
    jPanel2.setMaximumSize(new Dimension(300, 110));		//changed dimensionds MC-9/1/05
    jPanel2.setMinimumSize(new Dimension(200, 55));
    jPanel2.setPreferredSize(new Dimension(250, 70));
    
    
    loadGridMenu.setText("Load Saved Grid...");
    loadGridMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
//        loadGridMenu_actionPerformed(e);
      }
    });

    saveGridMenu.setText("Save Current Grid");
    saveGridMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
//        saveGridMenu_actionPerformed(e);
      }
    });

    
    saveAsGridMenu.setText("Save Current Grid As...");
    saveAsGridMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
//        saveAsGridMenu_actionPerformed(e);
      }
    });

    
    fileMenu.setText("File");
    finishMenu.setText("Finished");
    finishMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        finishMenu_actionPerformed(e);
      }
    });
    optionsMenu.setText("Image Options");
    undoMenu.setText("Undo Last Update");
    undoMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        undoMenu_actionPerformed(e);
      }
    });
    rotateClockMenu.setEnabled(false);
    rotateClockMenu.setText("Rotate Clockwise");
    rotateClockMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        rotateClockMenu_actionPerformed(e);
      }
    });
    rotateCounterClockMenu.setEnabled(false);
    rotateCounterClockMenu.setText("Rotate Counterclockwise");
    rotateCounterClockMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        rotateCounterClockMenu_actionPerformed(e);
      }
    });
    propertiesMenu.setText("Grid Properties...");
    propertiesMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        propertiesMenu_actionPerformed(e);
      }
    });
    saveImageMenu.setText("Save Image...");
    tiffMenu.setText("Tiff File");
    tiffMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        tiffMenu_actionPerformed(e);
      }
    });
    jpegMenu.setText("Jpeg File");
    jpegMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        jpegMenu_actionPerformed(e);
      }
    });
    gifMenu.setText("Gif File");
    gifMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        gifMenu_actionPerformed(e);
      }
    });
    this.getContentPane().add(imageToolBar, BorderLayout.NORTH);
    imageToolBar.add(zoomIn, null);
    imageToolBar.add(zoomOut, null);
    imageToolBar.add(reset, null);
    imageToolBar.add(done, null);
    imageToolBar.add(jPanel2, null);
    jPanel2.add(contrastLabel, null);
 //   jPanel2.add(contrastInputFieldLabel, null);
    jPanel2.add(contrastSlider, null);
    jPanel2.add(contrastInputField, null);

    scrollRight = new JScrollPane(imageDisplayPanel);
    scrollRight.getHorizontalScrollBar().setUnitIncrement(10);
    scrollRight.getVerticalScrollBar().setUnitIncrement(10);
    scrollLeft = new JScrollPane(gridTabs);
    scrollLeft.setPreferredSize(new Dimension((gridTabs.getPreferredSize().width+(4*scrollLeft.getVerticalScrollBar().getWidth())),gridTabs.getPreferredSize().height));
    scrollRight.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener(){
      public void adjustmentValueChanged(AdjustmentEvent e){
        if(scrollRight.getHorizontalScrollBar().getValueIsAdjusting()){
          statusBar.setText("Leftmost Pixel: "+xCoordinate(e.getValue()));
        }
        else statusBar.setText(" ");
      }

    });
    scrollRight.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener(){
      public void adjustmentValueChanged(AdjustmentEvent e){
        if(scrollRight.getVerticalScrollBar().getValueIsAdjusting()){
          statusBar.setText("Topmost Pixel: "+yCoordinate(e.getValue()));
        }
        else statusBar.setText(" ");
      }

    });

    this.getContentPane().add(statusBar, BorderLayout.SOUTH);
    this.getContentPane().add(jSplitPane1, BorderLayout.CENTER);
    jSplitPane1.add(scrollRight, JSplitPane.RIGHT);
    jSplitPane1.add(scrollLeft, JSplitPane.LEFT);
    menuBar.add(fileMenu);
    menuBar.add(optionsMenu);
    fileMenu.add(loadGridMenu);
    fileMenu.addSeparator();
    fileMenu.add(saveGridMenu);
    fileMenu.add(saveAsGridMenu);
    fileMenu.addSeparator();
    fileMenu.add(saveImageMenu);
    fileMenu.addSeparator();
    fileMenu.add(propertiesMenu);
    fileMenu.addSeparator();
    fileMenu.add(finishMenu);
    optionsMenu.add(undoMenu);
    optionsMenu.addSeparator();
    optionsMenu.add(rotateClockMenu);
    optionsMenu.add(rotateCounterClockMenu);
    saveImageMenu.add(tiffMenu);
    saveImageMenu.add(jpegMenu);
    saveImageMenu.add(gifMenu);

    this.addButtonListeners();
    this.setJMenuBar(menuBar);

    undoMenu.setEnabled(false);


    //adds change listeners
    gridTabs.addChangeListener(new ChangeListener(){

        public void stateChanged(ChangeEvent e){
          ((MetaGridPanel)gridTabs.getSelectedComponent()).resetButtons();
          ((MetaGridPanel)gridTabs.getSelectedComponent()).updateApplyFromBox();
        }
    });


    //adds mouse listeners
    imageDisplayPanel.ic.addMouseMotionListener(new MouseMotionListener(){
      public void mouseMoved(MouseEvent mm){
        int xco = xCoordinate(mm.getX());
        int yco = yCoordinate(mm.getY());
        statusBar.setText("X:" + xco + " Y:" + yco);
      }
      public void mouseDragged(MouseEvent md){}
    });

    imageDisplayPanel.ic.addMouseListener(new MouseAdapter(){
      public void mouseClicked(MouseEvent mc){
       if(currentGridPanel()!=null){
          if (currentGridPanel().tlButton.isSelected()) {
            currentGridPanel().topleftX.setText(String.valueOf(xCoordinate(mc.getX())));
            currentGridPanel().topleftY.setText(String.valueOf(yCoordinate(mc.getY())));
            currentGridPanel().tlButton.setSelected(false);
            currentGridPanel().updateApplyFromBox();
          }
          if (currentGridPanel().trButton.isSelected()) {
            currentGridPanel().toprightX.setText(String.valueOf(xCoordinate(mc.getX())));
            currentGridPanel().toprightY.setText(String.valueOf(yCoordinate(mc.getY())));
            currentGridPanel().trButton.setSelected(false);
            currentGridPanel().updateApplyFromBox();
          }
          if (currentGridPanel().bottomRowPt.isSelected()) {
            currentGridPanel().bRowPtX.setText(String.valueOf(xCoordinate(mc.getX())));
            currentGridPanel().bRowPtY.setText(String.valueOf(yCoordinate(mc.getY())));
            currentGridPanel().bottomRowPt.setSelected(false);
          }
        }
        if (zoomIn.isSelected()) {
          int clickedX = mc.getX();
          int clickedY = mc.getY();
          imageDisplayPanel.zoom(1.25);
          int w = (scrollRight.getViewport().getWidth());
          int h = (scrollRight.getViewport().getHeight());
          int newTopLeftX = (int)((1.25*clickedX)-(w/2));
          int newTopLeftY = (int)((1.25*clickedY)-(h/2));
          scrollRight.getViewport().setViewPosition(new Point(newTopLeftX, newTopLeftY));
          setTitle("Overlayed Image (" + df.format(imageDisplayPanel.getZoom()*100) + "%)" + " - " + width + " x " + height);
        }

        if (zoomOut.isSelected()) {
          int clickedX = mc.getX();
          int clickedY = mc.getY();
          imageDisplayPanel.zoom(0.75);
          int w = (scrollRight.getViewport().getWidth());
          int h = (scrollRight.getViewport().getHeight());
          int newTopLeftX = (int)((0.75*clickedX)-(w/2));
          int newTopLeftY = (int)((0.75*clickedY)-(h/2));
          scrollRight.getViewport().setViewPosition(new Point(newTopLeftX, newTopLeftY));
          setTitle("Overlayed Image (" + df.format(imageDisplayPanel.getZoom()*100) + "%)" + " - " + width + " x " + height);
        }
      }
    });

    jSplitPane1.setDividerLocation(scrollLeft.getPreferredSize().width+20);

    if (this.gridFilePath==null) saveGridMenu.setEnabled(false);

  }

  /**
   * Makes the gridding frame visible
   */
  public void show(){
    super.show();
    contrastSlider.setSize(contrastSlider.getWidth()+1, contrastSlider.getHeight());
    //used because of java bug with jsliders
  }


  /**
   * gets the panel where the image is displayed
   * @return panel where the image is displayed
   */
  public ImageDisplayPanel getDisplay() {
    return imageDisplayPanel;
  }

  /**
   * sets the image to fill the screen
   */
  public void fillScreen(){
    double level = (double)scrollRight.getWidth()/width;
    double level2 = (double)scrollRight.getHeight()/height;
    if(level2<level) level = level2;
    imageDisplayPanel.setMagnification(level);
    imageDisplayPanel.zoom(1.0);
    this.setTitle("Overlayed Image (" + df.format(imageDisplayPanel.getZoom()*100) + "%)" + " - " + width + " x " + height);
  }

  /**
   * gets the actual x-coordinate on the image based on the screen x-coordinate
   * @param ex screen x-coordinate
   * @return actual x-coordinate on the image based on the screen x-coordinate
   */
  public int xCoordinate(int ex) {
    return ((imageDisplayPanel.ic.getSrcRect().x+Math.round((float)((ex)/imageDisplayPanel.getZoom()))));
  }

  /**
   * gets the actual y-coordinate on the image based on the screen y-coordinate
   * @param ey screen y-coordinate
   * @return actual y-coordinate on the image based on the screen y-coordinate
   */
  public int yCoordinate(int ey) {
    return ((imageDisplayPanel.ic.getSrcRect().y+Math.round((float)((ey)/imageDisplayPanel.getZoom()))));
  }

  /**
   * gets the current grid panel
   * @return current grid panel (null of there is none)
   */
  public MetaGridPanel currentGridPanel() {
    if (gridTabs.getSelectedIndex()>=0 && gridTabs.getSelectedIndex()<metaGridPanel.length) return metaGridPanel[gridTabs.getSelectedIndex()];
    else return null;
  }

  /**
   * returns whether or not all grids have been completed and segmentation can begin
   * @return whether or not all grids have been completed and segmentation can begin
   */
  public boolean canSegment(){
    return manager.isValid();
   }

  /**
   * sets whether or not to allow modifications to any of the grids or the grid properties
   * @param allow whether or not to allow modifications to any of the grids or the grid properties
   */
  public void setAllowChanges(boolean allow){
    if(allow){
      fileMenu.setEnabled(true);
      optionsMenu.setEnabled(true);
      scrollLeft.setVisible(true);
      jSplitPane1.setDividerLocation(scrollLeft.getPreferredSize().width+20);
    }
    else{
      fileMenu.setEnabled(false);
      optionsMenu.setEnabled(false);
      scrollLeft.setVisible(false);
    }
  }


  /**
   * brings a menu to allow users to change the metagrid properties
   */
  public void changeProperties(){
      MetaGridOptionsDialog god = new MetaGridOptionsDialog(main);
      god.setOptions(manager.getMetaGridNum(), manager.getLeftRight(), manager.getTopBottom(), manager.getGridDirection());
      god.setModal(true);
      god.pack();
      god.setVisible(true);
      if(god.getOK()){
        int numDelete = manager.getMetaGridNum() - god.getGridNum();
        int cont = JOptionPane.YES_OPTION;
        if(numDelete>0) cont = JOptionPane.showConfirmDialog(this.getDesktopPane(), "Warning! You Have Selected " + numDelete + " Fewer Grids For Your Image.\nDo You Wish To Delete " + (numDelete==1? "This Grid And All Data Related To It?":"These " + numDelete + " Grids And All Data Related To Them"), "Warning! You May Be Deleting Important Data", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if(cont==JOptionPane.YES_OPTION){
          manager.setMetaGridNum(god.getGridNum());
          manager.setLeftRight(god.getHorizontal());
          manager.setTopBottom(god.getVertical());
          manager.setGridDirection(god.getFirstGrid());
          needSaved=true;


          metaGridPanel = new MetaGridPanel[manager.numMetaGrids];
          titles = new String[manager.numMetaGrids];
          for(int i=0; i<manager.numMetaGrids; i++){
            titles[i] = "" + (i+1);
            metaGridPanel[i] = new MetaGridPanel(manager.getMetaGrid(i),this.getDisplay());
            if (i<gridTabs.getTabCount()) gridTabs.setComponentAt(i,metaGridPanel[i]);
            else gridTabs.add(metaGridPanel[i], titles[i]);
          }
          for(int j=manager.numMetaGrids; j<gridTabs.getTabCount();){
            gridTabs.remove(j);
          }
          gridTabs.setSelectedIndex(0);
          addButtonListeners();


        }
        else changeProperties();
      }
  }

  /**
   * rotates the microarray images 90 degrees and saves the new tif files. (not functional yet)
   * @param clock whether to rotate clockwise or counterclockwise
   */
  public void rotate(boolean clock){
      final boolean direction = clock;

      Thread thread = new Thread(){
        public void run(){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

            int w = width;
            int h = height;
            int[] pixels = new int[w*h];

            int[] newpixels = new int[w*h];

             PixelGrabber pg = new PixelGrabber(image, 0,0,w,h,pixels,0,w);

               try{
                  pg.grabPixels();

               }catch(Exception e3){System.out.print("Error");}

              if(direction){
                 for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        newpixels[(h-y-1)+h*x] = pixels[y*w + x];
                    }
                 }
              }

              else{
                 for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        newpixels[h*(w-x-1)+y] = pixels[y*w + x];
                    }
                 }
              }

              image = createImage(new MemoryImageSource(h,w,newpixels,0,h));
              imageDisplayPanel.ip.setImage(image);
              //ic.setDrawingSize(h,w);
              //ic.setImageUpdated();

              //ic.revalidate();

              //this.setPreferredSize(new Dimension(Math.round((float)(ip.getWidth()*zoomed)),Math.round((float)(ip.getHeight()*zoomed))));
              //System.out.println("" + ip.getWidth() + "," + ip.getHeight() + " - " + ic.getWidth() + "," + ic.getHeight());
              //ic.repaint();
              //this.repaint();



          int temp = width;
          width = height;
          height = temp;
          scrollRight.getViewport().setViewPosition(new Point(0, 0));
          fillScreen();
          scrollRight.getViewport().setViewSize(imageDisplayPanel.getPreferredSize());
          System.out.println("" + imageDisplayPanel.getZoom() + ":" + imageDisplayPanel.getPreferredSize().width + "-" + imageDisplayPanel.getPreferredSize().height);
          setTitle("Overlayed Image (" + df.format(imageDisplayPanel.getZoom()*100) + "%)" + " - " + width + " x " + height);
          setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();

  }

  /* Andrew Martens */

  
  /**
   * This is for metaGridding
   * prepares the final initializations for the gridding frame (if necessary)
   */
  public void finalMetaInit(){
      
	  if(doFinal) {
		
        String multiLineWarningMsg[] = {"You must understand your genelist configuration before you start metagridding.","The first metagrid you create must contain spot number 1, corresponding to the first gene in the genelist."};
        JOptionPane.showMessageDialog(this.getDesktopPane(), multiLineWarningMsg, "Warning!", JOptionPane.INFORMATION_MESSAGE);
        statusBar.setText("Specify Metagrid Options...");
        
        final MetaGridOptionsDialog mgod = new MetaGridOptionsDialog(main);
        
        mgod.setOptions(manager.getMetaGridNum(), manager.getLeftRight(), manager.getTopBottom(), manager.getGridDirection());
        mgod.setModal(true);
        mgod.pack();
        mgod.setVisible(true);
        
        if(mgod.getOK()){
            manager.setMetaGridNum(mgod.getGridNum());
            manager.setLeftRight(mgod.getHorizontal());
            manager.setTopBottom(mgod.getVertical());
            manager.setGridDirection(mgod.getFirstGrid());
            needSaved=true;

            metaGridPanel = new MetaGridPanel[manager.numMetaGrids];
            titles = new String[manager.numMetaGrids];
            for(int i=0; i<manager.numMetaGrids; i++){
              titles[i] = "" + (i+1);
              metaGridPanel[i] = new MetaGridPanel(manager.getMetaGrid(i),this.getDisplay());
              if (i<gridTabs.getTabCount()) gridTabs.setComponentAt(i,metaGridPanel[i]);
              else gridTabs.add(metaGridPanel[i], titles[i]);
            }
            for(int j=manager.numMetaGrids; j<gridTabs.getTabCount();){
              gridTabs.remove(j);
            }
            gridTabs.setSelectedIndex(0);
            addButtonListeners();
        }
        
        else {
           manager.setMetaGridNum(0);
           this.setVisible(false);
        }
     }
	  
  }
  
  
  /* End Andrew Martens */


  //menu item to fill the screen
  private void fitToScreen_actionPerformed(ActionEvent e) {
    fillScreen();
    scrollRight.repaint();
  }

  //for closing the window: right now all we do is just close it
  private void done_actionPerformed(ActionEvent e) {
	  this.dispose();
  }

  //manually inputting a contrast fold-increase
  private void contrastInputField_actionPerformed(ActionEvent e) {
	  int inputContrast = (int)Double.parseDouble(contrastInputField.getText());		//XXX add warning dialog about recklessness of 5 million
	  contrastSlider.setMajorTickSpacing(inputContrast/4);
	  contrastSlider.setMinorTickSpacing(inputContrast/8);
	  contrastSlider.createStandardLabels(inputContrast/4);
	  contrastSlider.setMaximum(inputContrast);
	  contrastSlider.repaint();
	  contrastSlider.setValue(inputContrast);
  }

  //for adjusting the contrast
  private void contrastSlider_stateChanged(ChangeEvent e) {

    if(!contrastSlider.getValueIsAdjusting()){
      Thread thread = new Thread(){
        public void run(){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          double contrast = (double)contrastSlider.getValue();
          contrast/=100;
          int w = image.getWidth(null);
          int h = image.getHeight(null);
          int[] pixels = new int[w*h];


           PixelGrabber pg = new PixelGrabber(image, 0,0,w,h,pixels,0,w);

             try{
                pg.grabPixels();

             }catch(Exception e3){System.out.print("Error");}

             for(int i=0; i<pixels.length; i++){
                int p = pixels[i];

                int a = (p >> 24) & 0xFF;
                int r = (p >> 16) & 0xFF;
                int b = 0;
                int g = (p >>  8) & 0xFF;
                r=Math.round((float)(r*contrast));
                //b=Math.round((float)(b*contrast));
                g=Math.round((float)(g*contrast));
                if(r>255) r=255;
                //if(b>255) b=255;
                if(g>255) g=255;

                pixels[i] = (a << 24 | r << 16 | g << 8 | b);
             }
             imageDisplayPanel.ip.setImage(createImage(new MemoryImageSource(w,h,pixels,0,w)));
             imageDisplayPanel.repaint();
             setCursor(Cursor.getDefaultCursor());
          }
      };
      thread.start();
    }
  }

  //adds the button listeners
  private void addButtonListeners() {
    zoomIn.removeAll();
    zoomIn.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent ezoomIn) {
          if (zoomIn.isSelected()) {
            if(currentGridPanel()!=null){
              currentGridPanel().resetButtons();
            }
            zoomOut.setSelected(false);

            ImageIcon icon = new ImageIcon(MagicToolApp.class.getResource("gifs/zoomin.gif"));
            Image cursorImage = icon.getImage();

            Cursor magnifier = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(14,14), "magnifier");
            imageDisplayPanel.setCursor(magnifier);



            //setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

          }
          else if(!zoomIn.isSelected() && !zoomOut.isSelected() &&(currentGridPanel()==null || (!currentGridPanel().tlButton.isSelected() && !currentGridPanel().trButton.isSelected() && !currentGridPanel().bottomRowPt.isSelected()))) imageDisplayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        }
      });

      zoomOut.removeAll();
      zoomOut.addChangeListener(new ChangeListener() {
        public void stateChanged(ChangeEvent ezoomOut) {
          if (zoomOut.isSelected()) {
            if(currentGridPanel()!=null){
              currentGridPanel().resetButtons();
            }
            zoomIn.setSelected(false);
            ImageIcon icon = new ImageIcon(MagicToolApp.class.getResource("gifs/zoomout.gif"));
            Image cursorImage = icon.getImage();

            Cursor magnifier = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(13,15), "magnifier");
            imageDisplayPanel.setCursor(magnifier);

          }
          else if(!zoomIn.isSelected() && !zoomOut.isSelected() && !currentGridPanel().tlButton.isSelected() && !currentGridPanel().trButton.isSelected() && !currentGridPanel().bottomRowPt.isSelected()) imageDisplayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      });

      if(currentGridPanel()!=null){
        currentGridPanel().tlButton.removeAll();
        currentGridPanel().tlButton.addChangeListener(new ChangeListener() {
          public void stateChanged(ChangeEvent etlButton) {
              if (currentGridPanel().tlButton.isSelected()) {
                currentGridPanel().trButton.setSelected(false);
                currentGridPanel().bottomRowPt.setSelected(false);
                zoomIn.setSelected(false);
                zoomOut.setSelected(false);
                imageDisplayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
              }
              else if(!zoomIn.isSelected() && !zoomOut.isSelected() && !currentGridPanel().tlButton.isSelected() && !currentGridPanel().trButton.isSelected() && !currentGridPanel().bottomRowPt.isSelected()) imageDisplayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
        });

        currentGridPanel().trButton.removeAll();
        currentGridPanel().trButton.addChangeListener(new ChangeListener() {
          public void stateChanged(ChangeEvent etrButton) {
            if (currentGridPanel().trButton.isSelected()) {
              currentGridPanel().tlButton.setSelected(false);
              currentGridPanel().bottomRowPt.setSelected(false);
              zoomIn.setSelected(false);
              zoomOut.setSelected(false);
              imageDisplayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
            else if(!zoomIn.isSelected() && !zoomOut.isSelected() && !currentGridPanel().tlButton.isSelected() && !currentGridPanel().trButton.isSelected() && !currentGridPanel().bottomRowPt.isSelected()) imageDisplayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
        });

        currentGridPanel().bottomRowPt.removeAll();
        currentGridPanel().bottomRowPt.addChangeListener(new ChangeListener() {
          public void stateChanged(ChangeEvent ebottomRowPt) {
            if (currentGridPanel().bottomRowPt.isSelected()) {
              currentGridPanel().tlButton.setSelected(false);
              currentGridPanel().trButton.setSelected(false);
              zoomIn.setSelected(false);
              zoomOut.setSelected(false);
              imageDisplayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
            else if(!zoomIn.isSelected() && !zoomOut.isSelected() && !currentGridPanel().tlButton.isSelected() && !currentGridPanel().trButton.isSelected() && !currentGridPanel().bottomRowPt.isSelected()) imageDisplayPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
          }
        });

        currentGridPanel().updateGridData.addActionListener(new ActionListener() {
          public void actionPerformed(ActionEvent e) {
             needSaved=true;
             lastUpdateGrid = gridTabs.getSelectedIndex();
             undoMenu.setEnabled(true);
          }
        });
      }
  }

//  //menu item to load a saved grid
//  private void loadGridMenu_actionPerformed(ActionEvent e) {
//      loadGrid(true);
//  }
//
//  //loads a grid
//  private void loadGrid(){
//	  loadGrid(false);
//   }
//  
//  /* Andrew Martens - use for when we want to load a grid but not display it. open is true */
//  public void newLoadGrid() {
//	  loadGrid(true);
//  }

  /* End Andrew Martens */
  
//  //loads a grid specified by the user
//  private void loadGrid(boolean open) {
//      MainFrame.fileLoader.setFileFilter(MainFrame.fileLoader.gridFilter);
//      MainFrame.fileLoader.setDialogTitle("Load Grid File...");
//      MainFrame.fileLoader.setApproveButtonText("Select");
//      File f = new File(project.getPath() + "grids" + File.separator);
//      if(!f.exists()) f.mkdirs();
//      MainFrame.fileLoader.setCurrentDirectory(f);
//      MainFrame.fileLoader.setSelectedFile(null);
//
//      int result = MainFrame.fileLoader.showOpenDialog(null);
//
//      if (result == JFileChooser.APPROVE_OPTION) {
//
//          try{
//              File fileobj = MainFrame.fileLoader.getSelectedFile();
//              String path = fileobj.getAbsolutePath();
//              manager.openGridManager(path);
//              this.gridFilePath = path;
//              saveGridMenu.setEnabled(true);
//              //if(!open){
//                gridPanel = new GridPanel[manager.numGrids];
//                titles = new String[manager.numGrids];
//                for(int i=0; i<manager.numGrids; i++){
//                  titles[i] = "" + (i+1);
//                  gridPanel[i] = new GridPanel(manager.getGrid(i),this.getDisplay());
//                  if (i<gridTabs.getTabCount()) gridTabs.setComponentAt(i,gridPanel[i]);
//                  else gridTabs.add(gridPanel[i], titles[i]);
//                }
//                for(int j=manager.numGrids; j<gridTabs.getTabCount();){
//                  gridTabs.remove(j);
//                }
//                jSplitPane1.setDividerLocation(scrollLeft.getPreferredSize().width+20);
//                manager.setCurrentGrid(0);
//                imageDisplayPanel.repaint();
//                undoMenu.setEnabled(false);
//                needSaved=false;
//                addButtonListeners();
//              //}
//
//          }
//          catch(Exception e1){
//            JOptionPane.showMessageDialog(null, "Error! Could Not Open Grid File");
//            if(!open) finalInit();
//          }
//      }
//      else{
//        if(!open){
//          manager.setGridNum(0);
//          this.dispose();
//        }
//      }
//  }
//
//  //saves an open grid
//  private void saveGridMenu_actionPerformed(ActionEvent e) {
//    if(this.gridFilePath!=null){
//      try{
//        manager.writeGridManager(gridFilePath);
//        needSaved=false;
//      }
//      catch(Exception e1){
//        JOptionPane.showMessageDialog(null, "Error! Could Not Save Grid File");
//      }
//    }
//    else saveAsGridMenu_actionPerformed(new ActionEvent(null,0,""));
//  }
//
//
//  //saves an open grid to file specified
//  private void saveAsGridMenu_actionPerformed(ActionEvent e) {
//         MainFrame.fileLoader.setFileFilter(MainFrame.fileLoader.gridFilter);
//      MainFrame.fileLoader.setDialogTitle("Create New Grid File...");
//      MainFrame.fileLoader.setApproveButtonText("Select");
//      File f = new File(project.getPath() + "grids" + File.separator);
//      if(!f.exists()) f.mkdirs();
//      MainFrame.fileLoader.setCurrentDirectory(f);
//      MainFrame.fileLoader.setSelectedFile(new File(""));
//
//      int result = MainFrame.fileLoader.showSaveDialog(null);
//
//      if (result == JFileChooser.APPROVE_OPTION) {
//          try{
//            File fileobj = MainFrame.fileLoader.getSelectedFile();
//            String path = fileobj.getAbsolutePath();
//            if(!path.toLowerCase().endsWith(".grid")) path+=".grid";
//            manager.writeGridManager(path);
//            this.gridFilePath = path;
//            saveGridMenu.setEnabled(true);
//            needSaved=false;
//          }
//          catch(Exception e1){
//            JOptionPane.showMessageDialog(null, "Error! Could Not Save Grid File");
//          }
//      }
//  }

  //closes window
  private void finishMenu_actionPerformed(ActionEvent e) {
    done_actionPerformed(e);
  }

  //rotate menu (not implemented)
  private void rotateCounterClockMenu_actionPerformed(ActionEvent e) {
      rotate(false);
  }

  //allows users to undo last update
  private void undoMenu_actionPerformed(ActionEvent e) {
    if(lastUpdateGrid>=0&&lastUpdateGrid<manager.getMetaGridNum()){
      manager.setMetaGrid(lastUpdateGrid, new MetaGrid(GridPanel.lastUpdate[0], GridPanel.lastUpdate[1],GridPanel.lastUpdate[2], GridPanel.lastUpdate[3],GridPanel.lastUpdate[4], GridPanel.lastUpdate[5],GridPanel.lastUpdate[6], GridPanel.lastUpdate[7],GridPanel.lastUpdate[8], GridPanel.lastUpdate[9]));
      metaGridPanel[lastUpdateGrid].setDataFromGrid(manager.getMetaGrid(lastUpdateGrid));
      manager.setCurrentMetaGrid(lastUpdateGrid);
      imageDisplayPanel.repaint();
    }
    undoMenu.setEnabled(false);
    lastUpdateGrid=-1;
    needSaved=true;
  }

  //rotates image menu (not implemented)
  private void rotateClockMenu_actionPerformed(ActionEvent e) {
      rotate(true);
  }

  //brings up change properties window
  private void propertiesMenu_actionPerformed(ActionEvent e) {
      changeProperties();
  }


  //initializes the image
  private void imageInit(){

          statusBar.setText("Loading Overlayed Image For Gridding...");

          Opener greenImage = new Opener();
          Opener redImage = new Opener();
          Image combined = greenImage.openImage(greenPath).getImage();
          Image red = redImage.openImage(redPath).getImage();

          Dimension redDim = new Dimension(red.getWidth(null),red.getHeight(null));
          Dimension greenDim = new Dimension(combined.getWidth(null),combined.getHeight(null));
          int w = greenDim.width;
          int h = greenDim.height;

          int[] pixels = new int[w*h];
          int[] redpixels = new int[w*h];

          PixelGrabber pg = new PixelGrabber(combined, 0,0,w,h,pixels,0,w);
          PixelGrabber redpg = new PixelGrabber(red, 0,0,w,h,redpixels,0,w);
          try{
            pg.grabPixels();
            redpg.grabPixels();
          }
          catch(Exception e3){
            System.out.print("(Error Grabbing Pixels) "+e3);
          }

          for(int i=0; i<pixels.length; i++){
            int p = pixels[i];
            int redp = redpixels[i];
            int a = (p >> 24) & 0xFF;
            int r = (redp >> 16) & 0xFF;
            int b = 0;
            int g = (p >>  8) & 0xFF;

            pixels[i] = (a << 24 | r << 16 | g << 8 | b);
          }
          image = createImage(new MemoryImageSource(w,h,pixels,0,w));
          imageDisplayPanel = new ImageDisplayPanel(image, manager);
          statusBar.setText("Image  Overlayed");
          setClosable(true);
          width=w;
          height=h;

          setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));


  }




  //displays the save as menu
  private void tiffMenu_actionPerformed(ActionEvent e) {
    Thread thread = new Thread(){
        public void run(){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

          int idWidth = (int)(imageDisplayPanel.ip.getWidth()*imageDisplayPanel.getZoom());
          int idHeight = (int)(imageDisplayPanel.ip.getHeight()*imageDisplayPanel.getZoom());
          Image i = createImage(Math.min(idWidth,scrollRight.getViewport().getExtentSize().width), Math.min(idHeight,scrollRight.getViewport().getExtentSize().height));
          Graphics graph = i.getGraphics();
          scrollRight.paint(graph);

          FileSaver fileSaver = new FileSaver(new ImagePlus("NewTiff",i));
          if(project!=null){
            MainFrame.fileLoader.setFileFilter(MainFrame.fileLoader.tifFilter);
            MainFrame.fileLoader.setDialogTitle("Create New Tiff File...");
            MainFrame.fileLoader.setApproveButtonText("Select");
            File f = new File(project.getPath() + "images" + File.separator);
            if(!f.exists()) f.mkdirs();
            MainFrame.fileLoader.setCurrentDirectory(f);
            MainFrame.fileLoader.setSelectedFile(null);

            int result = MainFrame.fileLoader.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                try{
                  File fileobj = MainFrame.fileLoader.getSelectedFile();
                  String path = fileobj.getAbsolutePath();
                  if(!path.toLowerCase().endsWith(".tiff")) path+=".tiff";
                  try{

                      fileSaver.saveAsTiff(path);

                  }
                  catch(Exception e2){
                    JOptionPane.showMessageDialog(getDesktopPane(), "Error Writing .tiff File - "+e2);
                  }

                }
                catch(Exception e1){
                  JOptionPane.showMessageDialog(null, "Error! Could Not Save Image File");
                }
            }
          }

          setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();
  }

  //saves images as jpeg
  private void jpegMenu_actionPerformed(ActionEvent e) {
    Thread thread = new Thread(){
        public void run(){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

          int idWidth = (int)(imageDisplayPanel.ip.getWidth()*imageDisplayPanel.getZoom());
          int idHeight = (int)(imageDisplayPanel.ip.getHeight()*imageDisplayPanel.getZoom());
          Image i = createImage(Math.min(idWidth,scrollRight.getViewport().getExtentSize().width), Math.min(idHeight,scrollRight.getViewport().getExtentSize().height));
          Graphics graph = i.getGraphics();
          scrollRight.paint(graph);

          FileSaver fileSaver = new FileSaver(new ImagePlus("NewJpg",i));
          if(project!=null){
            MainFrame.fileLoader.setFileFilter(MainFrame.fileLoader.jpegFilter);
            MainFrame.fileLoader.setDialogTitle("Create New Jpeg File...");
            MainFrame.fileLoader.setApproveButtonText("Select");
            File f = new File(project.getPath() + "images" + File.separator);
            if(!f.exists()) f.mkdirs();
            MainFrame.fileLoader.setCurrentDirectory(f);
            MainFrame.fileLoader.setSelectedFile(null);

            int result = MainFrame.fileLoader.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                try{
                  File fileobj = MainFrame.fileLoader.getSelectedFile();
                  String path = fileobj.getAbsolutePath();
                  if(!path.toLowerCase().endsWith(".jpg")||!path.toLowerCase().endsWith(".jpeg")) path+=".jpg";
                  try{

                      fileSaver.saveAsJpeg(path);

                  }
                  catch(Exception e2){
                    JOptionPane.showMessageDialog(getDesktopPane(), "Error Writing .jpeg File - "+e2);
                  }

                }
                catch(Exception e1){
                  JOptionPane.showMessageDialog(null, "Error! Could Not Save Image File");
                }
            }
          }

          setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();
  }

  //saves images as gif
  private void gifMenu_actionPerformed(ActionEvent e) {
    Thread thread = new Thread(){
        public void run(){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

          int idWidth = (int)(imageDisplayPanel.ip.getWidth()*imageDisplayPanel.getZoom());
          int idHeight = (int)(imageDisplayPanel.ip.getHeight()*imageDisplayPanel.getZoom());
          Image i = createImage(Math.min(idWidth,scrollRight.getViewport().getExtentSize().width), Math.min(idHeight,scrollRight.getViewport().getExtentSize().height));
          Graphics graph = i.getGraphics();
          scrollRight.paint(graph);

          ImagePlus gifImage = new ImagePlus("NewGif", i);
          ImageConverter converter = new ImageConverter(gifImage);
          converter.convertRGBtoIndexedColor(256);
          FileSaver fileSaver = new FileSaver(gifImage);
          if(project!=null){
            MainFrame.fileLoader.setFileFilter(MainFrame.fileLoader.gifFilter);
            MainFrame.fileLoader.setDialogTitle("Create New Gif File...");
            MainFrame.fileLoader.setApproveButtonText("Select");
            File f = new File(project.getPath() + "images" + File.separator);
            if(!f.exists()) f.mkdirs();
            MainFrame.fileLoader.setCurrentDirectory(f);
            MainFrame.fileLoader.setSelectedFile(null);

            int result = MainFrame.fileLoader.showSaveDialog(null);

            if (result == JFileChooser.APPROVE_OPTION) {
                try{
                  File fileobj = MainFrame.fileLoader.getSelectedFile();
                  String path = fileobj.getAbsolutePath();
                  if(!path.toLowerCase().endsWith(".gif")) path+=".gif";
                  try{

                      fileSaver.saveAsGif(path);

                  }
                  catch(Exception e2){
                    JOptionPane.showMessageDialog(getDesktopPane(), "Error Writing .gif File - "+e2);
                  }

                }
                catch(Exception e1){
                  JOptionPane.showMessageDialog(null, "Error! Could Not Save Image File");
                }
            }
          }

          setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();

  }

}  
  