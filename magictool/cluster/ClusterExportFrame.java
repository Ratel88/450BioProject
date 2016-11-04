package magictool.cluster;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import magictool.CallJTreeView;
import magictool.ExpFile;
import magictool.FileComboBox;
import magictool.Gene;
import magictool.ProgressFrame;
import magictool.Project;
import magictool.TreeableCluster;
import magictool.VerticalLayout;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class ClusterExportFrame extends JInternalFrame{
	protected JDesktopPane desktop;
	protected Project project;
	private FileComboBox clustBox;
	private String[] info;
	private String writeDirectory;
	private JLabel chooserLabel;
	private JButton cancelButton;
	private JButton exportButton;
	private JPanel buttonPanel;
	private JTextArea infoTextArea;
	private JPanel chooserPanel;
	private JPanel topPanel;
	private JInternalFrame thisFrame;
	private ProgressFrame progress;

	public ClusterExportFrame(Project p, JDesktopPane desktopFrame) {
		
		project = p;
		this.desktop = desktopFrame;
		thisFrame = this;
		clustBox = new FileComboBox(project, Project.CLUST, "all", true, "Cluster");
		//probably need to run this in its own thread...
		initGUI();
	}
	
	public void exportCDTandGTR() {
		// use the header of the selected .clust file to determine what kind it is and what .exp file it came from.
		// instantiate the .exp file as [ef], and create a tree of DefaultMutableTreeNodes from the .clust file,
		// rooted at [rootNode].  
		
		if( clustBox.getFilePath() != null ) {
			File clustFile = new File(clustBox.getFilePath());
		
		try {
			String[] info = AbstractCluster.readHeaders(clustBox.getFilePath());
			writeDirectory = project.getPath() + info[1].substring(0,info[1].lastIndexOf(".")) + File.separator; 	 // complicated way of determining .exp filepath
			ExpFile ef = new ExpFile( new File( writeDirectory + info[1] ) );
			
		     TreeableCluster tc=null;
		     if(info[5].equals("QTCLUST")) tc = new QTClust();
		     else if(info[5].equals("KMEANS")) tc = new KMeansClust();
		     else tc = new HiClust();
		     DefaultMutableTreeNode rootNode = null;
		     if(tc!=null) rootNode = tc.getDataInTree(clustFile);
		     int numberOfClusters = Integer.parseInt(info[0]) - 1;
		     
		     // set up the writers and write the headers
		     String pathMinusExtension = clustFile.getPath().substring( 0,clustFile.getPath().lastIndexOf(".") );		    	 
		     BufferedWriter cdt = new BufferedWriter(new FileWriter(pathMinusExtension + ".cdt"), 4096);
		     BufferedWriter gtr = new BufferedWriter(new FileWriter(pathMinusExtension + ".gtr"), 4096);
		     String[] preordered = new String[ numberOfClusters ];				// uses java.lang.Integer to convert from string to int
		     																// should never need more than number of genes - 1.  Some clustering methods use less.
		     cdt.write("GID\tORF\tNAME\tGWEIGHT\t");
		     Object[] columnLabels = ef.getLabelArray();
		     int ratioColumns = columnLabels.length;
		     for (int i=0; i < ratioColumns - 1; i++)
		    	 	cdt.write(columnLabels[i] + "\t");
		     cdt.write(columnLabels[ratioColumns - 1] + "\nEWEIGHT\t\t\t");
		     for (int i=0; i < ratioColumns; i++)
		    	 	cdt.write("1\t");
		     cdt.write("1\n");

		     // loops to get and write the genes and expression ratios in the correct order
		    	 Enumeration preorder = rootNode.preorderEnumeration();		// ultimately need to organize data as if by postorder, 
		    	 														// but the current implementation of postorder is recursive
		    	 														// and causes stackoverflows.  Just reversing a preorder is ok.
		    	 
		    	 progress = new ProgressFrame( "writing .cdt file to " + writeDirectory );
		    	 desktop.add(progress);
		    	 progress.setValue(0);
		    	 progress.setMaximum( numberOfClusters );
		    	 progress.show();		// it isn't appearing right now; I think because this code is all executed on the swing Event thread (BAD!)
		    	 
		    	 int position = numberOfClusters;
		    	 DefaultMutableTreeNode node;
	    		 String nodeID = null;
	    		 String child1 = null;
	    		 String child2 = null;
	    		 Gene gene = null;
		    	 while (preorder.hasMoreElements()) {
		    		 node = (DefaultMutableTreeNode)preorder.nextElement();
		    		 if ( !node.isLeaf() ) {
		    			 nodeID = "NODE" + node + "X\t";
		    			 if( !node.getFirstChild().isLeaf() )
		    				 child1 = "NODE" + node.getFirstChild() + "X\t";
		    			 else
		    				 child1 = node.getFirstChild() + "\t";
		    			 if( !node.getLastChild().isLeaf() )
		    				 child2 = "NODE" + node.getLastChild() + "X\t";
		    			 else
		    				 child2 = node.getLastChild() + "\t";
		    			 position--;
		    			 preordered[position] = nodeID + child1 + child2 + ( 1 - ((NodeInfo)node.getUserObject()).getDistance() ) + "\n";
		    			 
		    		 } else {
		    			 nodeID = node.toString();
		    			 gene = ef.getGene( ef.findGeneName(nodeID) );
		    			 cdt.write( nodeID + "\t" + nodeID + "\t" + gene.getFunctionBasic() + "    " + gene.getProcessBasic() +
		    					 "    " + gene.getComponentBasic() + "\t" + "1" + "\t" );
		    			 for( int i=0; i < ratioColumns; i++ )
		    				 cdt.write( gene.getDataPoint(i) + "\t" );
		    			 cdt.write("\n");
		    		 }
		    		 progress.addValue(1);
		    	 }
		     
		    	 progress.setValue(0);
		    	 progress.setTitle( "writing .gtr file to " + writeDirectory );
		    	 
		    	 for( ; position < numberOfClusters; position++ ){
		    		 gtr.write( preordered[position] );
		    		 progress.addValue(1);
		    	 }
			     //Now that we have written the file we can display it in JTreeView...
			  String [] f = new String[2];
			  f[0] = "-r";
			  f[1] = pathMinusExtension + ".cdt"; //Has full path.
			//  f[1] = f[1].replace("exp", "cdt");
	     	  System.out.println(f[0]);		    		 
	     	  cdt.close();
	     	  gtr.close();
	     	  progress.dispose();
		     

		      CallJTreeView.callJTreeView(f);
		     
		} catch (Exception e) {
			progress.dispose();
			e.printStackTrace();
		}
		} else {
			infoTextArea.setText("Please select a .clust file from the\ndrop-down menu.");
			//this.dispose();
		}
	}
	
	private void initGUI() {
		try {
			{
				this.setTitle("JTreeView Export Information");
				topPanel = new JPanel();
				getContentPane().add(topPanel, BorderLayout.CENTER);
				BorderLayout topPanelLayout = new BorderLayout();
				topPanel.setLayout(topPanelLayout);
				topPanel.setFocusable(false);
				{
					chooserPanel = new JPanel();
					BorderLayout chooserPanelLayout = new BorderLayout();
					topPanel.add(chooserPanel, BorderLayout.NORTH);
					chooserPanel.setLayout(chooserPanelLayout);
					{
						chooserLabel = new JLabel();
						chooserPanel.add(chooserLabel, BorderLayout.WEST);
						chooserLabel.setText(".clust file:");
						chooserPanel.add(clustBox, BorderLayout.CENTER);
						clustBox.addItemListener(new java.awt.event.ItemListener() {
						      public void itemStateChanged(ItemEvent e) {
						        clustBox_itemStateChanged(e);
						      }

							private void clustBox_itemStateChanged(ItemEvent e) {
								// TODO Auto-generated method stub
								//update infoTextArea
							}
						    });
					}
				}
				{
					infoTextArea = new JTextArea();
					topPanel.add(infoTextArea, BorderLayout.CENTER);
					infoTextArea.setText("Select a .clust file.  It would be wise to choose " +
							"\none that was generated from an .exp file that " +
							"\ncontained gene annotation data (gene info) " +
							"\nand had been log transformed. The data is outputed in .cdt" +
							"\nand .gtr format. This data will then be read into JTreeView,"+
							"\nwhich will display a Dendrogram.");
					infoTextArea.setEditable(false);
				}
				{
					buttonPanel = new JPanel();
					topPanel.add(buttonPanel, BorderLayout.SOUTH);
					{
						exportButton = new JButton();
						buttonPanel.add(exportButton);
						exportButton.setText("export");
						exportButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								//System.out.println("exportButton.actionPerformed, event="+ evt);
								exportCDTandGTR();
								thisFrame.dispose();
							}
						});
					}
					{
						cancelButton = new JButton();
						buttonPanel.add(cancelButton);
						cancelButton.setText("cancel");
						cancelButton.addActionListener(new ActionListener() {
							public void actionPerformed(ActionEvent evt) {
								//System.out.println("cancelButton.actionPerformed, event="+ evt);
								thisFrame.dispose();
							}
						});
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
