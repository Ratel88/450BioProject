/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003-2007  Laurie Heyer
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
 *   Davidson College
 *   PO Box 6959
 *   Davidson, NC 28035-6959
 *   UNITED STATES
 */

package magictool.explore;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.SwingUtilities;

import magictool.*;

public class ReplaceNamesWithAliasesDialog extends JDialog {
	private final static long serialVersionUID = 42L;
	private JPanel outerInPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	private JPanel inPanel = new JPanel();
	private Border titledBorder1Border;
	private TitledBorder titledBorder1;
	private VerticalLayout verticalLayout1 = new VerticalLayout();
	private JLabel inLabel = new JLabel();
	private FileComboBox filebox1;
	private JButton okButton = new JButton();
	private JButton cancelButton = new JButton();
	private BorderLayout borderLayout1 = new BorderLayout();
	private JPanel outerOutFilePanel = new JPanel();
	private JPanel outFilePanel = new JPanel();
	private JLabel newExpressionFileLabel = new JLabel();
	private Border newExpressionFileBorder;
	private JTextField outFileField = new JTextField();
	private BorderLayout borderLayout3 = new BorderLayout();
	private VerticalLayout verticalLayout5 = new VerticalLayout();
	private BorderLayout borderLayout4 = new BorderLayout();
	
	/**filepath of the expression file to be name-replaced*/
	protected String expFilePath = null;
	/**project whose expression files can be name-replaced*/
	protected Project project;
	/**desktop pane on which this dialog is displayed*/
	protected JDesktopPane desktop;
	/**whether or not the name-replacement has finished*/
	protected boolean finished = false;
	/**new expression filename*/
	protected String filename = null;
	/**parent frame*/
	protected Frame parent;
	/**the thread that performs the process*/
	protected NameReplaceThread processThread;
	
	/**
	 * Constructs the ReplaceNamesWithAliases dialog and sets up the available expression files to be name-replaced
	 * @param p project whose expression files can be operated upon
	 * @param pf parent frame
	 * @param desktop desktop pane on which this dialog is displayed
	 */
	public ReplaceNamesWithAliasesDialog(Project p, Frame pf, JDesktopPane desktop) {
		super(pf);
		this.project = p;
		this.parent = pf;
		this.desktop = desktop;
		filebox1 = new FileComboBox(p, Project.EXP, "all", true, "Expression");
		
		try {
			initUI();
		}
		catch (Exception e) {
			System.out.println("Initializing ReplaceNamesWithAliasesDialog failed.");
			e.printStackTrace();
		}
		processThread = new NameReplaceThread(this);
	}
	
	private void initUI() throws Exception {
		titledBorder1Border = BorderFactory.createLineBorder(new Color(153,153,153),2);
		titledBorder1 = new TitledBorder(titledBorder1Border, "Select File to Name-Replace");
		newExpressionFileBorder = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(148,145,140),new Color(103,101,98)),BorderFactory.createEmptyBorder(3, 3, 3, 3));
		outerInPanel.setBorder(titledBorder1);
		outerInPanel.setLayout(verticalLayout1);
		inLabel.setText("Select Expression File: ");
		inPanel.setLayout(borderLayout1);
		okButton.setEnabled(false);
		okButton.setText("OK");
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});
		rootPane.setDefaultButton(okButton);
		cancelButton.setText("Cancel");
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButton_actionPerformed(e);
			}
		});
		filebox1.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				filebox1_itemStateChanged(e);
			}
		});
		newExpressionFileLabel.setBorder(newExpressionFileBorder);
		newExpressionFileLabel.setText("New Expression File");
		outFilePanel.setBorder(BorderFactory.createEtchedBorder());
		outFilePanel.setLayout(borderLayout3);
		this.getContentPane().setLayout(borderLayout4);
		outerOutFilePanel.setLayout(verticalLayout5);
		//titledBorder1.setTitle("Select File to Name-Replace:");
		this.getContentPane().add(outerInPanel,BorderLayout.NORTH);
		outerInPanel.add(inPanel, null);
		inPanel.add(inLabel, BorderLayout.WEST);
		inPanel.add(filebox1, BorderLayout.CENTER);
		this.getContentPane().add(outerOutFilePanel, BorderLayout.CENTER);
		outerOutFilePanel.add(outFilePanel, null);
		outFilePanel.add(newExpressionFileLabel, BorderLayout.WEST);
		outFilePanel.add(outFileField, BorderLayout.SOUTH);
		this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.add(okButton, null);
		buttonPanel.add(cancelButton, null);
		this.setSize(425,200);
		this.setTitle("Replace Names With Aliases");
		this.setModal(true);
		this.pack();
	}
	
	private void checkIfOKable() {
		if (filebox1.getSelectedIndex() > 0 ) okButton.setEnabled(true);
		else okButton.setEnabled(false);
	}
	
	private void filebox1_itemStateChanged(ItemEvent e) {
		outFileField.setText(filebox1.getSimpleName()+"_ren.exp");
		checkIfOKable();
	}
	
	private void okButton_actionPerformed(ActionEvent e) {
		checkIfOKable();
		if (okButton.isEnabled()) {
			try {
				processThread.run();
			}
			catch (Exception dnfe) {
				JOptionPane.showMessageDialog(this, "Error! The writing process did not finish.","MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
				dnfe.printStackTrace();
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "Somehow, you clicked OK without OK being enabled.\nYou're a very, very bad person.","MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void cancelButton_actionPerformed(ActionEvent e) {
		this.dispose();
	}
	
	public String getValue() {
		if(finished) return filename;
		else return null;
	}
	
	public String getExpFilePath() {
		if(finished) return expFilePath;
		else return null;
	}
	
	/**
	 * The <code>NameReplaceThread</code> class contains the thread for doing the actual name replacement.<br>
	 * It should be executed upon the user clicking OK after settings are verified.
	 * @author Laurie Heyer
	 *
	 */
	public class NameReplaceThread extends Thread implements Cancelable {
		final ProgressFrame progress;
		final ReplaceNamesWithAliasesDialog rnwad;
		boolean cancel = false;
		boolean completed = false;
		boolean over = false;
		
		/**
		 * Constructor for the <code>NameReplaceThread</code>.
		 * @param r the <code>ReplaceNamesWithAliasesDialog</code> within which this thread lives. It contains the settings.
		 */
		public NameReplaceThread(ReplaceNamesWithAliasesDialog r) {
			this.rnwad = r;
			this.progress = new ProgressFrame("Renaming genes...", false, this);
		}
		
		public void start() {
			cancel = false;
			completed = false;
			over = false;
			super.start();
		}
		
		/**
		 * Cancelation method.
		 */
		public void cancel() {
			//doesn't actually do anything right now.
		}
		
		/**
		 * Default execution method
		 */
		public void run() {
			rnwad.expFilePath = rnwad.filebox1.getFilePath();
			ExpFile exp1 = new ExpFile(new File(rnwad.filebox1.getFilePath()));
			rnwad.desktop.add(progress);
			progress.show();
			progress.setMaximum(exp1.numGenes()*4);
			String file = outFileField.getText().trim();
			if (file.toLowerCase().endsWith(".exp")) file = file.substring(0, file.lastIndexOf("."));
			String newFileName = project.getPath()+file+File.separator+file+".exp"; 
			File f = new File(newFileName);
			int deleteFiles = JOptionPane.CANCEL_OPTION;
			if (!f.exists() || (deleteFiles = JOptionPane.showConfirmDialog(rnwad.parent, "File already exists! Do you wish to overwrite?\nOverwriting the file will delete all files that used the overwritten file."))==JOptionPane.OK_OPTION) {
				try {
					if (deleteFiles == JOptionPane.OK_OPTION) f.getParentFile().delete();
					setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					f.getParentFile().mkdirs();
					
					BufferedWriter bw = new BufferedWriter(new FileWriter(f.getPath()));
					//write column headers from expression file
					for (int i = 0; i < exp1.getColumns(); i++) {
						bw.write(exp1.getLabel(i)+"\t");
					}
					bw.write("\n");
					//begin writing genes
					Vector<Gene> allGenes = new Vector<Gene>(exp1.numGenes());	//all genes as they currently exist
					Vector<Gene> newGenes = new Vector<Gene>(exp1.numGenes());	//all genes as they will exist
					Vector<String> allAliases = new Vector<String>();			//all aliases of genes
					Vector<NameAndReps> allReps = new Vector<NameAndReps>();	//all aliases with their rep info
					for(int i = 0; i < exp1.numGenes(); i++){
						allGenes.add(new Gene(exp1.getGene(i)));
						newGenes.add(new Gene(exp1.getGene(i)));
						progress.addValue(1);
					}
					for(int i = 0; i < exp1.numGenes(); i++) {
						Gene currGene = allGenes.get(i);
						if ((currGene.getAlias() != null) && (!currGene.getAlias().equals( "")) && (!currGene.getAlias().equals(" "))) {
							String alias = currGene.getAlias();
							if (allAliases.contains(alias)) {
								//it's already there -- we need to mark it as something with reps
								allReps.add(new NameAndReps(alias));
							}
							else allAliases.add(alias);
						}//end if
						progress.addValue(1);
					}//end for
					//all of our aliases are now in the places. we can start writing and replacing
					for(int i = 0; i < exp1.numGenes(); i++) {
						Gene currGene = allGenes.get(i);
						String alias = currGene.getAlias();
						String newname, oldname;
						if ((alias != null) && (!alias.equals("")) && (!alias.equals(" "))) {
							//this gene has an alias
							boolean isRep = false;
							NameAndReps currNAR = null;
							for(int j = 0; j < allReps.size(); j++) {
								currNAR = allReps.get(j);
								if (currNAR.name.equals(alias)) {
									//we've found a rep
									isRep = true;
									break;
								}
							}
							if (isRep) {
								//this one has a rep
								currNAR.increment();	//this is rep +1
								oldname = currGene.getName();
								newname = currNAR.name + "_rep" + currNAR.reps + "\t";
								//System.out.println("Writing rep NAR " + newname);
								bw.write(newname);
							}
							else {
								//no rep
								oldname = currGene.getName();
								newname = alias;
								//System.out.println("Writing no-rep " + newname);
								bw.write(alias+"\t");
							}
							//swap old and new name
							newGenes.get(i).setAlias(oldname.trim());
							newGenes.get(i).setName(newname.trim());
						}
						else {
							//no alias
							//System.out.println("Writing gene name no alias " + currGene.getName());
							bw.write(currGene.getName()+"\t");
						}
						//now write gene data
						double data[] = exp1.getData(i);
						for(int j = 0; j < data.length; j++) {
							bw.write(""+data[j]+"\t");
						}
						String comments = currGene.getComments();
						if (comments != null) bw.write("\t"+comments);
						bw.write("\n");
						progress.addValue(1);
					}//end for - all data is written
					bw.write("/**Gene Info**/" + "\n");
					//for(int i = 0; i < exp1.numGenes(); i++) {
					for(Gene g : newGenes) {
						//note that name and alias have already been swapped in newGenes
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
					rnwad.filename = newFileName;
					rnwad.finished = true;
				}
				catch (Exception e2) {
					completed = false;
					e2.printStackTrace();
					JOptionPane.showMessageDialog(rnwad.parent, "Error! Unable to write new expression file.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
				}
				finally {
					//do ending stuff
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							progress.dispose();
						}
					});
					rnwad.dispose();
					over = true;
					setCursor(Cursor.getDefaultCursor());
				}
				completed = true;
				if(completed&&!cancel) {
					if(rnwad.project!=null) project.addFile(newFileName);
				}
			}
			synchronized(this) {
				notify();
				notifyAll();
			}
		}

		/**
		 * This class allows us to keep track of each gene and the number of times it occurs
		 * @author Laurie Heyer
		 *
		 */
		private class NameAndReps {
			public String name = null;
			public int reps = 0;
			public NameAndReps() {}
			public NameAndReps(String n, int r) {
				this.name = n;
				this.reps = r;
			}
			public NameAndReps(String n) {
				this(n,0);
			}
			public boolean equals(NameAndReps r) {
				if (r.name == this.name) return true;
				else return false;
			}
			public void increment() { reps++; }
			public String toString() {
				if(name==null) return "Null Name!!!";
				return "Name: " + name + " Reps: " + reps;
			}
		}
	}
}
