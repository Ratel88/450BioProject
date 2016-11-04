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

package  magictool.explore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.SwingUtilities;

import magictool.DidNotFinishException;
import magictool.ExpFile;
import magictool.Gene;
//import magictool.FileComboBox;
import magictool.GrpFile;
import magictool.PlotFrame;
import magictool.Project;
import magictool.TableFrame;
import magictool.VerticalLayout;
import magictool.Cancelable;
import magictool.ProgressFrame;
import magictool.MainFrame;
import magictool.explore.ExploreComboBox;
import magictool.groupdisplay.CircleDisplayFrame;
import magictool.groupdisplay.ColumnChooser;



/**
 * ExploreFrame displays options to load existing gene groups, or form new gene groups
 * based on specified criteria.  With the groups the ExploreFrame can create a grayscale
 * table to display the gene data.
 */

public class ExploreFrame extends JInternalFrame implements KeyListener{
    private JPanel selectPane = new JPanel();
    private JPanel currentPane = new JPanel();
    private JPanel newGroupPanel = new JPanel();
    private JPanel selectedPanel = new JPanel();
    private JPanel plotPanel = new JPanel();
    private TitledBorder titledBorder1;
    private JButton useCritButton = new JButton();
    private JButton editGrpButton = new JButton();
    private JButton plotGroupButton = new JButton();
    private JLabel selLabel = new JLabel();
    private JLabel grpLabel = new JLabel();
    private JButton tableButton = new JButton();
    private ExpFile expMain;
    private GrpFile grpMain=null;
    private TitledBorder titledBorder2;
    private TitledBorder titledBorder3;
    private GridLayout gridLayout1 = new GridLayout(1,2);
    private VerticalLayout verticalLayout1 = new VerticalLayout();
    private BorderLayout borderLayout1 = new BorderLayout();
    private VerticalLayout verticalLayout2 = new VerticalLayout();
    private JLabel grpLabel2 = new JLabel();
    private VerticalLayout verticalLayout3 = new VerticalLayout();
    private JButton circleDisplayButton = new JButton();
    private JPanel jPanel1 = new JPanel();
    private JButton saveGrpButton = new JButton();
    private JButton saveExpButton = new JButton();

    /**JComboBox holding list of group files*/
    protected ExploreComboBox groupBox;
    private BorderLayout borderLayout2 = new BorderLayout();
    private Border border1;
    private TitledBorder titledBorder4;
    private Project project;
    private JButton scatterButton = new JButton();
    private VerticalLayout verticalLayout4 = new VerticalLayout();
    private CritDialog.Criteria criteria=null;


    /**parent frame*/
    protected Frame parentFrame;
    /**MainFrame*/
    protected MainFrame mainFrame;


    /**
     * Initializes the exploreFrame or throws an exception
     * @param expMain Loads expression file
     * @param p project associated with expression file that is being explored
     * @param parentFrame parent frame
     */
    public ExploreFrame (ExpFile expMain, Project p, MainFrame mf, Frame parentFrame) {
        this.project=p;
        this.expMain = expMain;
        this.mainFrame = mf;
        this.parentFrame=parentFrame;
        groupBox = new ExploreComboBox(project, expMain.getName());
        try {
            jbInit();
            addKeyListenerRecursively(this);
        } catch (Exception e) {
            e.printStackTrace();
          }
    }

    /**
     * Initializes the frame
     * @exception Exception
     */
    private void jbInit () throws Exception {
    	verticalLayout4.setVgap(12);
    	titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Form New Group");
    	titledBorder2 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Group Info");
    	titledBorder3 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Group Options");
    	border1 = BorderFactory.createEmptyBorder();
    	titledBorder4 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Select Existing Group");
    	this.getContentPane().setLayout(borderLayout1);
    	this.setClosable(true);
    	this.getContentPane().setBackground(new Color(204, 204, 204));
    	this.setTitle("Exploring "+expMain.getName());
    	selectPane.setLayout(verticalLayout1);
    	useCritButton.setText("Find Genes Matching Criteria...");
    	useCritButton.addActionListener(new java.awt.event.ActionListener() {
    		//selects a criteria for grouping
    		public void actionPerformed (ActionEvent e) {
    			useCritButton_actionPerformed(e);
    		}
    	});
    	newGroupPanel.setBorder(titledBorder1);
    	newGroupPanel.setLayout(verticalLayout2);
    	currentPane.setLayout(gridLayout1);
    	selLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	selLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    	selLabel.setText("Selected Group");
    	grpLabel.setFont(new java.awt.Font("Dialog", 1, 12));
    	grpLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	grpLabel.setText("Entire Expression File");
    	editGrpButton.setText("View / Edit File");
    	editGrpButton.addActionListener(new java.awt.event.ActionListener() {
    		//enables the user to add/remove genes from the group file
    		public void actionPerformed (ActionEvent e) {
    			editGrpButton_actionPerformed(e);
    		}
    	});
    	plotGroupButton.setText("Plot Selected Group");
    	plotGroupButton.addActionListener(new java.awt.event.ActionListener() {
    		//plots the group file
    		public void actionPerformed (ActionEvent e) {
    			plotGroupButton_actionPerformed(e);
    		}
    	});

    	tableButton.setText("Create Table");
    	tableButton.addActionListener(new java.awt.event.ActionListener() {
    		//displays table for the group file
    		public void actionPerformed(ActionEvent e) {
    			tableButton_actionPerformed(e);
    		}
    	});
    	selectedPanel.setBorder(titledBorder2);
    	selectedPanel.setLayout(verticalLayout3);
    	plotPanel.setBorder(titledBorder3);
    	plotPanel.setLayout(verticalLayout4);
    	gridLayout1.setColumns(2);
    	grpLabel2.setHorizontalAlignment(SwingConstants.CENTER);
    	circleDisplayButton.setText("Circular Display");
    	circleDisplayButton.addActionListener(new java.awt.event.ActionListener() {
    		//displays the circle diagram for the group file
    		public void actionPerformed(ActionEvent e) {
    			circleDisplayButton_actionPerformed(e);
    		}
    	});

    	jPanel1.setLayout(borderLayout2);
    	jPanel1.setBorder(titledBorder4);
    	groupBox.addItemListener(new java.awt.event.ItemListener() {
    		//select an existing group file to analyze
    		public void itemStateChanged(ItemEvent e) {
    			groupBox_itemStateChanged(e);
    		}
    	});

    	scatterButton.setText("Two Column Plot");
    	scatterButton.addActionListener(new java.awt.event.ActionListener() {
    		//displays a scatter plot for two existing group files
    		public void actionPerformed(ActionEvent e) {
    			scatterButton_actionPerformed(e);
    		}
    	});
    	saveGrpButton.addActionListener(new java.awt.event.ActionListener() {
    		//enables the user to add/remove genes from the group file
    		public void actionPerformed (ActionEvent e) {
    			saveGrpButton_actionPerformed(e);
    		}
    	});
    	saveGrpButton.setText("Save Group File");
    	saveGrpButton.setEnabled(false);

    	saveExpButton.setText("Save Expression File");;
    	saveExpButton.setEnabled(false);
    	saveExpButton.addActionListener(new java.awt.event.ActionListener() {
    		public void actionPerformed(ActionEvent e) {
    			saveExpButton_actionPerformed(e);
    		}
    	});

    	
    	selectPane.add(newGroupPanel, null);
    	newGroupPanel.add(useCritButton, null);
    	selectPane.add(jPanel1, null);
    	jPanel1.add(groupBox,  BorderLayout.CENTER);

    	this.getContentPane().add(currentPane, BorderLayout.CENTER);
    	currentPane.add(plotPanel, null);
    	this.getContentPane().add(selectPane, BorderLayout.NORTH);
    	plotPanel.add(plotGroupButton, null);
    	plotPanel.add(tableButton, null);
    	plotPanel.add(scatterButton, null);
    	plotPanel.add(circleDisplayButton, null);
    	
    	currentPane.add(selectedPanel, null);
    	selectedPanel.add(selLabel, null);
    	selectedPanel.add(grpLabel, null);
    	selectedPanel.add(grpLabel2, null);
    	selectedPanel.add(editGrpButton, null);
    	selectedPanel.add(saveGrpButton, null);
    	selectedPanel.add(saveExpButton, null);

    	Dimension selectedPanelSize = selectedPanel.getSize();
    	selectedPanelSize.setSize(selectedPanelSize.getWidth(), selectedPanelSize.getHeight()+180);
    	selectedPanel.setSize(selectedPanelSize);
    	selectedPanel.setMinimumSize(selectedPanelSize);
    	selectedPanel.setMaximumSize(selectedPanelSize);
    	selectedPanel.setPreferredSize(selectedPanelSize);
    	

    	this.addFocusListener(new FocusAdapter(){
    		public void focusGained(FocusEvent e){reload();}
    	});
    	Dimension mySize = new Dimension(380,350);
    	this.setMinimumSize(mySize);
    	this.setMaximumSize(mySize);
    	this.setPreferredSize(mySize);
    	this.setSize(mySize);
    }

    //adds/removes selected genes from the group file
   private void editGrpButton_actionPerformed (ActionEvent e) {
        GroupEditFrame gef = new GroupEditFrame((grpMain==null?new GrpFile():grpMain), expMain, project,parentFrame);
        gef.setParentFrame(this);
        this.getDesktopPane().add(gef);
        gef.setSize(400,400);
        gef.setLocation(100,100);
        gef.show();
        gef.toFront();
    }

    //creates the graph for the group file
    private void plotGroupButton_actionPerformed (ActionEvent e) {
      Thread thread = new Thread(){
        public void run(){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          PlotFrame plotframe = new PlotFrame((grpMain==null?new GrpFile():grpMain), expMain, parentFrame, project);
          getDesktopPane().add(plotframe);
          plotframe.pack();
          plotframe.show();
          plotframe.toFront();
          setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();
    }

    //creates the group file based on the selected criteria
    private void useCritButton_actionPerformed (ActionEvent e) {
        CritDialog critdialog;
        if(criteria==null) critdialog = new CritDialog(expMain, parentFrame);
        else critdialog = new CritDialog(expMain, parentFrame, criteria);
        critdialog.setModal(true);
        critdialog.show();
        GrpFile temp = critdialog.getValue();
        CritDialog.Criteria c = critdialog.getCriteria();
        if(temp!=null){
          temp.setTitle("Temporary Group");	//set the title of the temporary group
          this.setGrpFile(temp);
          criteria = c;
        }
    }

    //displays table for the group file
    private void tableButton_actionPerformed(ActionEvent e) {
      Thread thread= new Thread(){
        public void run(){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          TableFrame tableframe = new TableFrame(expMain, (grpMain==null?new GrpFile():grpMain),project);
          getDesktopPane().add(tableframe);
          tableframe.show();
          tableframe.toFront();
          tableframe.setSize(tableframe.getWidth()+1, tableframe.getHeight());
          setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();
    }

    /**
     * sets the select group file
     * @param grpMain select group file
     */
    public void setGrpFile(GrpFile grpMain){
      this.grpMain = grpMain;
      grpLabel.setForeground(Color.red);
      grpLabel.setText(grpMain.getTitle());
      grpLabel2.setForeground(Color.red);
      grpLabel2.setText(grpMain.getNumGenes()+" genes");
      editGrpButton.setEnabled(true);
      plotGroupButton.setEnabled(true);
      circleDisplayButton.setEnabled(true);

      if(grpMain.getNumGenes()>0){
        saveGrpButton.setEnabled(true);
        saveExpButton.setEnabled(true);
      }
      else{
        saveGrpButton.setEnabled(false);
        saveExpButton.setEnabled(false);
      }
      if(grpMain.getTitle().equals("Temporary Group")) groupBox.goTemporaryGroup();
    }

    /**
     * sets the selected group files
     * @param fileobj select group file
     */
    public void setGrpFile(File fileobj){
      grpMain = new GrpFile(fileobj);
      setGrpFile(grpMain);
    }

    //displays the circle diagram for the selected group file
    private void circleDisplayButton_actionPerformed(ActionEvent e) {
      Thread thread = new Thread(){
        public void run(){
          setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
          CircleDisplayFrame cdframe = new CircleDisplayFrame(expMain, (grpMain==null?new GrpFile():grpMain), parentFrame, project);
          Dimension d = getToolkit().getScreenSize();
          getDesktopPane().add(cdframe);
          cdframe.setSize(getDesktopPane().getSize());
          cdframe.setLocation(0, 0);
          cdframe.show();
          cdframe.toFront();
          setCursor(Cursor.getDefaultCursor());
        }
      };
      thread.start();
    }

    //changes to the selected group file
    private void groupBox_itemStateChanged(ItemEvent e) {
    	//TODO: Edit this section!!!
    	/*	//Edited and removed by Michael Gordon 5/31/2007
      if(groupBox.getSelectedIndex()!=0&&groupBox.getSelectedIndex()!=-1) {
        setGrpFile(new File(groupBox.getFilePath()));
        criteria=null;
      }*/
    	if ((groupBox.getSelectedIndex() != -1) && !(groupBox.getSelectedItem().toString().equals("Select Existing Group File"))) {
    		if (groupBox.getSelectedItem().toString().equals("Entire Expression File")) {
    			grpMain = new GrpFile();
    			//String expShortName = expMain.getName().substring(0, expMain.getName().lastIndexOf("."));
    			System.out.println(expMain.getName());
    			grpMain.setTitle(expMain.getName());
    			criteria = null;
    			grpLabel.setForeground(Color.black);
    		    grpLabel.setText("Entire Expression File");
    		    grpLabel2.setForeground(Color.black);
    		    grpLabel2.setText(expMain.getGeneNames().length + " genes");
    		    editGrpButton.setEnabled(true);
    		    plotGroupButton.setEnabled(true);
    		    circleDisplayButton.setEnabled(true);
    		    if(grpMain != null && grpMain.getNumGenes()>0){
    		    	saveGrpButton.setEnabled(true);
    		    	saveExpButton.setEnabled(true);
    		    }
    		    else{
    		        saveGrpButton.setEnabled(false);
    		        saveExpButton.setEnabled(false);
    		    }
    		    groupBox.removeTemporaryGroup();
    		    
    		}
    		else if (!(groupBox.getSelectedItem().toString().equals("Temporary Group"))) {
    			setGrpFile(new File(groupBox.getFilePath()));
    			criteria = null;
    			groupBox.removeTemporaryGroup();
    		}
    	}
    }

    //creates the scatter plot for the two existing group files
    private void scatterButton_actionPerformed(ActionEvent e) {
        final ColumnChooser chooser = new ColumnChooser(expMain, parentFrame);
        chooser.setModal(true);
        chooser.pack();
        chooser.setSize((chooser.getWidth()<300?300:chooser.getWidth()),(chooser.getHeight()<150?150:chooser.getHeight()));
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        chooser.setLocation((screen.width-chooser.getWidth())/2,(screen.height-chooser.getHeight())/2);
        chooser.setVisible(true);

        if(chooser.getOK()){
          Thread thread = new Thread(){
            public void run(){
              setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
              PlotFrame plotframe = new PlotFrame((grpMain==null?new GrpFile():grpMain), expMain, parentFrame, project);
              getDesktopPane().add(plotframe);
              plotframe.setColumns(chooser.getColumn1(), chooser.getColumn2());
              plotframe.showRegression(true);
              plotframe.pack();
              plotframe.show();
              plotframe.toFront();
              setCursor(Cursor.getDefaultCursor());
            }
          };
          thread.start();
        }
    }


  private void saveGrpButton_actionPerformed(ActionEvent e) {
     DefaultListModel groupModel = new DefaultListModel();
     JList groupGenes = new JList();
     groupGenes.setModel(groupModel);
     Object[] o = grpMain.getGroup();
     if(o.length>0){
       for(int i=0; i<o.length; i++){
        groupModel.addElement(o[i].toString());
       }
       String s = JOptionPane.showInputDialog(parentFrame,"Enter The Group Name:");
       if(s!=null){
         GrpFile newGrp = new GrpFile(s);
         for(int i=0; i<groupModel.size(); i++){
           newGrp.addOne(groupModel.elementAt(i));
         }
         if(!s.endsWith(".grp")) s+=".grp";
         newGrp.setExpFile(expMain.getName());
         try{
           File file =new File(project.getPath()+expMain.getName()+File.separator+s);
           int result = JOptionPane.YES_OPTION;
           if (file.exists()) {
             result = JOptionPane.showConfirmDialog(parentFrame, "The file "
                 + file.getPath() + " already exists.  Overwrite this file?",
                 "Overwrite File?", JOptionPane.YES_NO_OPTION);
             if (result == JOptionPane.YES_OPTION)
               file.delete();
           }
           if(result == JOptionPane.YES_OPTION)
             newGrp.writeGrpFile(project.getPath()+expMain.getName()+File.separator+s);
         }
         catch(DidNotFinishException e2){
           JOptionPane.showMessageDialog(parentFrame, "Error Writing Group File");
           System.out.println("DidNotFinishException Caught!");
           e2.printStackTrace();
         }
         project.addFile(expMain.getName()+File.separator+s);
         groupBox.reload();
         groupBox.setSelectedIndex(groupBox.getItemCount()-1);
       }
     }
      else{JOptionPane.showMessageDialog(parentFrame, "No Genes Selected");}
  }

  private void saveExpButton_actionPerformed(ActionEvent e) {
	  //JOptionPane.showMessageDialog(parentFrame, "Spawn3d saveExpButton.");
	  Object o[] = grpMain.getGroup();
	  if(o.length>0) {
		  String nfn = JOptionPane.showInputDialog(parentFrame,"Enter a name for the new expression file.", expMain.getName()+"_clim.exp");
		WriteNewExpFileThread processThread = new WriteNewExpFileThread(nfn, expMain, grpMain, this); 
		try {
			processThread.run();
		}
		catch (Exception dnfe) {
			JOptionPane.showMessageDialog(this, "Error! The writing process did not finish.","MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
			dnfe.printStackTrace();
		}
	  }
	  else{//empty group
		  JOptionPane.showMessageDialog(parentFrame, "No genes selected.\nPlease create a new group with at least one gene in it before trying to write a new expression file.","MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
	  }
  }

  /**
   * reloads the list of group files
   */
  public void reload(){
    groupBox.reload();
  }
  
  public void gwtreload(){
     reload();
  }


  private void addKeyListenerRecursively(Component c){
      c.removeKeyListener(this);
      c.addKeyListener(this);
      if(c instanceof Container){
        Container cont = (Container)c;
        Component[] children = cont.getComponents();
        for(int i = 0; i < children.length; i++){
          addKeyListenerRecursively(children[i]);
        }
      }
  }

    /**
     * Closes the frame when user press control + 'w'
     * @param e key event
     */
   public void keyPressed(KeyEvent e){
        if(e.getKeyCode()== KeyStroke.getKeyStroke(KeyEvent.VK_W,KeyEvent.CTRL_MASK).getKeyCode()&&e.isControlDown()){
          this.dispose();
        }
   }

   /**
     * Not implemented in this frame
     * @param e key event
     */
   public void keyReleased(KeyEvent e){}

   /**
     * Not implemented in this frame
     * @param e key event
     */
   public void keyTyped(KeyEvent e){}
   
   private class WriteNewExpFileThread extends Thread implements Cancelable {
	   private String newfilename;
	   private final ExpFile expFile;
	   private final GrpFile grpFile;
	   private final ProgressFrame progress;
	   private final ExploreFrame exploreFrame;
	   boolean cancel = false;
	   boolean completed = false;
	   boolean over = false;
	   
	   /**
	    * Constructor for the <code>WriteNewExpFileThread</code>
	    * @param newfilename filename of the new expression file
	    * @param oldfilename filename of the old expression file
	    * @param expF old ExpFile
	    * @param grpF GrpFile on which the new ExpFile should be based
	    */
	   public WriteNewExpFileThread(String newfilename, ExpFile expF, GrpFile grpF, ExploreFrame exF) {
		   this.newfilename = newfilename;
		   this.expFile = expF;
		   this.grpFile = grpF;
		   this.exploreFrame = exF;
		   this.progress = new ProgressFrame("Writing new expression file...", false, this);
	   }
	   
	   public void start() {
		   cancel = false;
		   completed = false;
		   over = false;
		   super.start();
	   }
	   
	   public void run() {
		   exploreFrame.getDesktopPane().add(progress);
		   progress.show();
		   progress.toFront();
		   progress.setMaximum(expFile.numGenes()*2);
		   String file = newfilename.trim();
		   if (file.toLowerCase().endsWith(".exp")) file = file.substring(0, file.lastIndexOf("."));
		   newfilename = project.getPath()+file+File.separator+file+".exp";
		   File f = new File(newfilename);
		   int deleteFiles = JOptionPane.CANCEL_OPTION;
		   if (!f.exists() || (deleteFiles = JOptionPane.showConfirmDialog(exploreFrame.parentFrame, "File already exists! Do you wish to overwrite?\nOverwriting the file will delete all files that used the overwritten file."))==JOptionPane.OK_OPTION) {
			   try {
				   if (deleteFiles == JOptionPane.OK_OPTION) f.getParentFile().delete();
				   setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				   f.getParentFile().mkdirs();
				   
				   BufferedWriter bw = new BufferedWriter(new FileWriter(f.getPath()));
				   //write column headers from expression file
				   for(int i = 0; i < expFile.getColumns(); i++) {
					   bw.write(expFile.getLabel(i) + "\t");
				   }
				   bw.write("\n");
				   //begin writing genes
				   Vector<Gene> allGenes = new Vector<Gene>(expFile.numGenes());	//all genes as they currently exist
				   for(int i = 0; i < expFile.numGenes(); i++) {
					   allGenes.add(new Gene(expFile.getGene(i)));
				   }
				   Vector<String> genesInGroup = new Vector<String>(grpFile.getNumGenes());
				   Object[] o = grpFile.getGroup();
				   for(int i = 0; i < grpFile.getNumGenes(); i++) {
					   genesInGroup.add(o[i].toString());					   
				   }
				   //okay, now we have all our genes in an easy-to-use Vector and the names of all the genes in our group in another easy-to-use Vector
				   for(Gene currGene : allGenes) {	//for each Gene currGene in allGenes...
					   if(genesInGroup.contains(currGene.getName())) {
						   //write it
						   bw.write(currGene.getName()+"\t");
						   double data[] = currGene.getData();
						   for(int j = 0; j < data.length; j++) {
							   bw.write(""+data[j]+"\t");
						   }
						   String comments = currGene.getComments();
						   if (comments==null) comments = "";
						   bw.write(""+comments);
						   bw.write("\n");
						   progress.addValue(1);
					   }//if it's not in the group, don't.
				   }
				   //all genes are written
				   bw.write("/**Gene Info**/"+"\n");
				   for(Gene g : allGenes) {
					   if(genesInGroup.contains(g.getName())) {
						   //note that name and alias have already been swapped in newGenes
						   String n = g.getName();
						   String a = g.getAlias();
						   String c = g.getChromo();
						   String l = g.getLocation();
						   String p = g.getProcess();
						   String fl = g.getFunction();
						   String co = g.getComponent();
						   if(n!=null) bw.write(n+"\t" + (a!=null?a:" ")+"\t" + (c!=null?c:" ")+"\t" + (l!=null?l:" ")+"\t" + (p!=null?p:" ")+"\t" + (fl!=null?fl:" ")+"\t" +(co!=null?co:" ")+"\n");
					   }
					   progress.addValue(1);
				   }
				   bw.close();
				   completed = true;
			   }
			   catch (Exception e2) {
				   completed = false;
				   e2.printStackTrace();
				   JOptionPane.showMessageDialog(exploreFrame.parentFrame, "Error! Unable to write new expression file.", "MAGIC Tool Error",JOptionPane.ERROR_MESSAGE);
			   }
			   finally {
				   //do ending stuff
				   SwingUtilities.invokeLater(new Runnable() {
					   public void run() {
						   progress.dispose();
					   }
				   });
				   over = true;
				   setCursor(Cursor.getDefaultCursor());
			   }
		   }
		   if(completed && !cancel) {
			   //add stuff to project
			   String shortfile = newfilename;
			   if(shortfile.toLowerCase().endsWith(".exp")) shortfile = shortfile.substring(0, shortfile.toLowerCase().lastIndexOf(".exp"));
			   if(shortfile.lastIndexOf(File.separator)!=-1) shortfile = shortfile.substring(shortfile.lastIndexOf(File.separator)+1);
			   
			   exploreFrame.project.addFile(newfilename);
			   exploreFrame.mainFrame.addExpFile(newfilename);
			   //MainFrame.expMain = new ExpFile(new File(newfilename));	//this is now done for us at MainFrame.destroyAndRecreateExploreFrame(String)
			   //exploreFrame.reInit(MainFrame.expMain, exploreFrame.project, exploreFrame.mainFrame, exploreFrame.parentFrame);	//this doesn't work like I want. we use MainFrame.destroyAndRecreateExploreFrame(String) instead.
			   Object[] options = { "Open New Expression File","Keep Exploring Old Expression File" }; 
			   int response = JOptionPane.showInternalOptionDialog(exploreFrame, "Do you want to explore the new expression file or keep exploring the old one?", "MAGIC Tool Question",JOptionPane.DEFAULT_OPTION,JOptionPane.QUESTION_MESSAGE,null,options,options[0]);
			   if (response == 0) {
				   exploreFrame.setVisible(false);
				   mainFrame.destroyAndRecreateExploreFrame(newfilename);
			   }
			   else {
				   //construct the exp filename
				   String expFileName = project.getPath()+expMain.getName()+File.separator+expMain.getName()+".exp";
				   mainFrame.rescanProject(expFileName);
			   }
		   }
		   synchronized(this){
			   notify();
			   notifyAll();
		   }
	   }
	   
	   public void cancel() {
		   //do nothing
	   }
   }

}
