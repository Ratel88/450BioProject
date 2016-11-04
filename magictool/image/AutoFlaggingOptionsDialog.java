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

package magictool.image;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import magictool.Project;
import magictool.VerticalLayout;
import magictool.image.GridManager;


public class AutoFlaggingOptionsDialog extends JDialog implements KeyListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1926708051504859417L;
	//first, the checkboxes
	private JCheckBox redFGCheckBox = new JCheckBox();
	private JCheckBox redBGCheckBox = new JCheckBox();
	private JCheckBox greenFGCheckBox = new JCheckBox();
	private JCheckBox greenBGCheckBox = new JCheckBox();
	
	//then the text fields
	private JTextField redFGTextField = new JTextField();
	private JTextField redBGTextField = new JTextField();
	private JTextField greenFGTextField = new JTextField();
	private JTextField greenBGTextField = new JTextField();
	
	//and the labels
	private JLabel redFGLabel = new JLabel();
	private JLabel redBGLabel = new JLabel();
	private JLabel greenFGLabel = new JLabel();
	private JLabel greenBGLabel = new JLabel();
	private JLabel informationalLabel1 = new JLabel();
	private JLabel informationalLabel2 = new JLabel();

	private TitledBorder titledBorder1;
	private VerticalLayout verticalLayout1 = new VerticalLayout();
	private VerticalLayout dialogVFlowLayout1 = new VerticalLayout();
	private FlowLayout critFlowLayout = new FlowLayout();
	
	private JPanel dialogContentPane = new JPanel();
	private JPanel redFGPanel = new JPanel();
	private JPanel redBGPanel = new JPanel();
	private JPanel greenFGPanel = new JPanel();
	private JPanel greenBGPanel = new JPanel();
	private JPanel buttonPanel = new JPanel();
	
	private JButton okButton = new JButton("OK");	
	
	/**this determines if we have anything in the AutoFlaggingOptionsDialog that we should bother to check*/
	private boolean ok = false;
	
	/**the project associated with these flags*/
	protected Project project;
	/**the value of the red foreground threshold -- -1 means criteria disabled*/
	protected int redFG = -1;
	/**the value of the red background threshold -- MAX_VALUE means criteria disabled*/
	protected int redBG = Integer.MAX_VALUE;
	/**the value of the green foreground threshold -- -1 means criteria disabled*/
	protected int greenFG = -1;
	/**the value of the green background threshold -- MAX_VALUE means criteria disabled*/
	protected int greenBG = Integer.MAX_VALUE;
	/**active grid manager*/
	protected GridManager manager;
	/**the SegmentFrame with which this AutoFlaggingOptionsDialog is associated*/
	protected SegmentFrame segmentFrame;
	
	public AutoFlaggingOptionsDialog(Frame owner, Project p, GridManager gm, SegmentFrame sf)	{
		super(owner);
		project = p;
		manager = gm;
		segmentFrame = sf;

		try {
			initUI();
		}
		catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error: Unfortunately, the Automatic Flagging Options Dialog was unable to be initialized for some unforseen reason.\nPlease contact magictool.help@gmail.com with a copy of the output of the command window, if applicable.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void initUI() throws Exception {
		this.setTitle("Automatic Flagging Options");
		titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new Color(153,153,153),2), "Select Criteria for Automatic Flagging");
		this.setModal(true);
		this.setResizable(false);
		this.getContentPane().setLayout(verticalLayout1);
		dialogContentPane.setBorder(titledBorder1);
		dialogContentPane.setLayout(dialogVFlowLayout1);
		okButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed (ActionEvent e) {
				okButton_actionPerformed(e);
			}
		});
		
		//now set up the labels
		redFGLabel.setText("Red Foreground < ");
		redBGLabel.setText("Red Background > ");
		greenFGLabel.setText("Green Foreground < ");
		greenBGLabel.setText("Green Background > ");
		informationalLabel1.setText("<html><i>If any one of the below criteria is met for the</i></html>");
		informationalLabel1.setHorizontalAlignment(JLabel.CENTER);
		informationalLabel2.setText("<html><i>totals of a spot, that spot will be flagged.</i></html>");
		informationalLabel2.setHorizontalAlignment(JLabel.CENTER);
		
		//now the text fields
		redFGTextField.setPreferredSize(new Dimension(70,21));
		redFGTextField.setOpaque(false);
		redFGTextField.setEnabled(false);
		redBGTextField.setPreferredSize(new Dimension(70,21));
		redBGTextField.setOpaque(false);
		redBGTextField.setEnabled(false);
		greenFGTextField.setPreferredSize(new Dimension(70,21));
		greenFGTextField.setOpaque(false);
		greenFGTextField.setEnabled(false);
		greenBGTextField.setPreferredSize(new Dimension(70,21));
		greenBGTextField.setOpaque(false);
		greenBGTextField.setEnabled(false);

		//next the checkboxes
		redFGCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redFGCheckBox_actionPerformed(e);
			}
		});
		redBGCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				redBGCheckBox_actionPerformed(e);
			}
		});
		greenFGCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				greenFGCheckBox_actionPerformed(e);
			}
		});
		greenBGCheckBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				greenBGCheckBox_actionPerformed(e);
			}
		});
		
		//make MouseListeners on the JLabels
		redFGLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				redFGCheckBox.doClick();
			}
		});
		redBGLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				redBGCheckBox.doClick();
			}
		});
		greenFGLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				greenFGCheckBox.doClick();
			}
		});
		greenBGLabel.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				greenBGCheckBox.doClick();
			}
		});
		
		//set up the panels
		critFlowLayout.setAlignment(FlowLayout.LEFT);
		redFGPanel.setLayout(critFlowLayout);
		redBGPanel.setLayout(critFlowLayout);
		greenFGPanel.setLayout(critFlowLayout);
		greenBGPanel.setLayout(critFlowLayout);
		
		//add items to the panels
		redFGPanel.add(redFGCheckBox);
		redFGPanel.add(redFGLabel);
		redFGPanel.add(redFGTextField, null);
		
		redBGPanel.add(redBGCheckBox);
		redBGPanel.add(redBGLabel);
		redBGPanel.add(redBGTextField, null);
		
		greenFGPanel.add(greenFGCheckBox);
		greenFGPanel.add(greenFGLabel);
		greenFGPanel.add(greenFGTextField, null);
		
		greenBGPanel.add(greenBGCheckBox);
		greenBGPanel.add(greenBGLabel);
		greenBGPanel.add(greenBGTextField, null);
		
		this.getContentPane().add(informationalLabel1, null);
		this.getContentPane().add(informationalLabel2, null);
		this.getContentPane().add(dialogContentPane, null);
		this.getContentPane().add(buttonPanel, BorderLayout.CENTER);
		
		Point segframelocation = segmentFrame.getLocation();
		Point thisLocation = new Point();
		thisLocation.setLocation(segframelocation.getX()+segmentFrame.getWidth()/3.0, segframelocation.getY()+segmentFrame.getHeight()/3.0);
		this.setLocation(thisLocation);
		//System.out.println((int)thisLocation.getX());
		//System.out.println((int)thisLocation.getY());
		this.setBounds((int)thisLocation.getX(), (int)thisLocation.getY() ,400, 300);
		
		buttonPanel.add(okButton);
		
		dialogContentPane.add(redFGPanel, null);
		dialogContentPane.add(redBGPanel, null);
		dialogContentPane.add(greenFGPanel, null);
		dialogContentPane.add(greenBGPanel, null);
		this.getRootPane().setDefaultButton(okButton);
		this.addKeyListener(this);
	}
	
	public void okButton_actionPerformed(ActionEvent e) {
		if (redFGCheckBox.isSelected())
		{
			try {
				redFG = Integer.parseInt(redFGTextField.getText());
				if (redFG < 0) throw new NegativeNumberException();
			}
			catch (NumberFormatException ex1) {
				  JOptionPane.showMessageDialog(this, "Error: The value for the red foreground threshold does not appear to be a number.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
				  redFG = -1;	//disable due to invalid value
				  return;
			} catch (NegativeNumberException ex2) {
				  JOptionPane.showMessageDialog(this, "Error: The value for the red foreground threshold must be a number greater than zero.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
				  redFG = -1;	//disable due to invalid value
				  return;
			}
		}
		else redFG = -1;
		
		if (redBGCheckBox.isSelected())
		{
			try {
				redBG = Integer.parseInt(redBGTextField.getText());
				if (redBG < 0) throw new NegativeNumberException();
			}
			catch (NumberFormatException ex1) {
				  JOptionPane.showMessageDialog(this, "Error: The value for the red background threshold does not appear to be a number.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
				  redBG = Integer.MAX_VALUE;	//disable due to invalid value
				  return;
			} catch (NegativeNumberException ex2) {
				  JOptionPane.showMessageDialog(this, "Error: The value for the red background threshold must be a number greater than zero.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
				  redBG = Integer.MAX_VALUE;	//disable due to invalid value
				  return;
			}
		}
		else redBG = Integer.MAX_VALUE;
		
		if (greenFGCheckBox.isSelected())
		{
			try {
				greenFG = Integer.parseInt(greenFGTextField.getText());
				if (greenFG < 0) throw new NegativeNumberException();
			}
			catch (NumberFormatException ex1) {
				  JOptionPane.showMessageDialog(this, "Error: The value for the red foreground threshold does not appear to be a number.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
				  greenFG = -1;	//disable due to invalid value
				  return;
			} catch (NegativeNumberException ex2) {
				  JOptionPane.showMessageDialog(this, "Error: The value for the red foreground threshold must be a number greater than zero.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
				  greenFG = -1;	//disable due to invalid value
				  return;
			}
		}
		else greenFG = -1;
		
		if (greenBGCheckBox.isSelected())
		{
			try {
				greenBG = Integer.parseInt(greenBGTextField.getText());
				if (greenBG < 0) throw new NegativeNumberException();
			}
			catch (NumberFormatException ex1) {
				  JOptionPane.showMessageDialog(this, "Error: The value for the red foreground threshold does not appear to be a number.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
				  greenBG = Integer.MAX_VALUE;	//disable due to invalid value
				  return;
			} catch (NegativeNumberException ex2) {
				  JOptionPane.showMessageDialog(this, "Error: The value for the red foreground threshold must be a number greater than zero.", "MAGIC Tool Error", JOptionPane.ERROR_MESSAGE);
				  greenBG = Integer.MAX_VALUE;	//disable due to invalid value
				  return;
			}
		}
		else greenBG = Integer.MAX_VALUE;
		
		//hooray, we passed!
		ok = true;
		segmentFrame.getSegmentPanel().updateGeneInfo();
		this.dispose();
	}
	
	private void redFGCheckBox_actionPerformed(ActionEvent e) {
		if (redFGCheckBox.isSelected()) redFGTextField.setEnabled(true);
		else redFGTextField.setEnabled(false);
	}
	
	private void redBGCheckBox_actionPerformed(ActionEvent e) {
		if (redBGCheckBox.isSelected()) redBGTextField.setEnabled(true);
		else redBGTextField.setEnabled(false);
	}
	
	private void greenFGCheckBox_actionPerformed(ActionEvent e) {
		if (greenFGCheckBox.isSelected()) greenFGTextField.setEnabled(true);
		else greenFGTextField.setEnabled(false);
	}
	
	private void greenBGCheckBox_actionPerformed(ActionEvent e) {
		if (greenBGCheckBox.isSelected()) greenBGTextField.setEnabled(true);
		else greenBGTextField.setEnabled(false);
	}
	
	/**
	 * checks to see if there's some data here, i.e. if any flagging params have ever been set
	 * @return whether or not there are any autoflagging params
	 */
	public boolean getOK() {
		return ok;
	}
	
	/**
	 * returns the thresholds set in this dialog
	 * @return the thresholds set in this dialog in the format {redFG, redBG, greenFG, greenBG}
	 */
	public int[] getThresholds(){
		int[] r = {redFG, redBG, greenFG, greenBG};
		return r;
	}
	
	 public class NegativeNumberException extends IllegalArgumentException {
		  /**
		 * 
		 */
		private static final long serialVersionUID = 392463211392265662L;

			public NegativeNumberException(){}
	  }
	 
	 public void keyTyped(KeyEvent e) { }
	 public void keyReleased(KeyEvent e) { }
	 public void keyPressed(KeyEvent e) {
		 if (e.getID() == KeyEvent.VK_ENTER) okButton.doClick();
	 }
}
