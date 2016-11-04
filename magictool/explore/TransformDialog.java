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

package magictool.explore;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JOptionPane;
import javax.swing.border.TitledBorder;

import magictool.ExpFile;
import magictool.MagicToolApp;
import magictool.VerticalLayout;

/**
 * TransformDialog is a JDialog which allows a user to perform either a log transform
 * or an exponential transform of the data in a specified expression file and saves
 * the new expression file at the user specified location
 */
public class TransformDialog extends JDialog {

  private JPanel titlePanel = new JPanel();
  private JPanel powerPanel = new JPanel();
  private JPanel logPanel = new JPanel();
  private JPanel bPanel = new JPanel();
  private JPanel confirmPanel = new JPanel();
  private JButton transokButton = new JButton("OK");
  private JButton transcancelButton = new JButton("Cancel");
  private JRadioButton powerButton = new JRadioButton();
  private JRadioButton logButton = new JRadioButton();
  private ButtonGroup transButtonGroup = new ButtonGroup();
  private JLabel powerLabel = new JLabel();
  private JLabel logLabel = new JLabel();
  private JLabel bLabel = new JLabel();
  private JTextField bField = new JTextField();
  private VerticalLayout verticalLayout1 = new VerticalLayout();
  private FlowLayout flowLayout1 = new FlowLayout();
  private FlowLayout flowLayout2 = new FlowLayout();
  private FlowLayout flowLayout3 = new FlowLayout();
  private TitledBorder titledBorder1;
  private JRadioButton powerButton1 = new JRadioButton();
  private JLabel powerLabel1 = new JLabel();
  private VerticalLayout verticalLayout2 = new VerticalLayout();
  /**This variable holds what we should do with a zero case.*/
  private int zeroResult=0; 
  /**whether or not the ok button was pressed*/
  protected boolean ok=false;
  /**expression file to transform*/
  protected ExpFile exp;

  /**
   * Constructs the dialog for the user to transform the data for the specified
   * expression file
   * @param exp expression file to transform
   * @param parent parent frame
   */
  public TransformDialog (ExpFile exp, Frame parent) {
    super(parent);
    try {
        this.exp = exp;
       jbInit();
    } catch (Exception e) {
        e.printStackTrace();
    }
  }

  //initializes the dialog
  private void jbInit () throws Exception {
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Transform Data");
    this.getContentPane().setBackground(new Color(204, 204, 204));
    this.setModal(true);
    this.setResizable(false);
    this.setSize(180,270);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    Dimension frameSize = this.getSize();

    this.setLocation((screenSize.width - frameSize.width)/2, (screenSize.height - frameSize.height)/2);

    flowLayout1.setAlignment(FlowLayout.LEFT);
    flowLayout1.setHgap(0);
    this.getContentPane().setLayout(verticalLayout1);
    logPanel.setLayout(flowLayout1);
    powerPanel.setLayout(flowLayout1);
    confirmPanel.setLayout(flowLayout3);
    ImageIcon bx = new ImageIcon(MagicToolApp.class.getResource("gifs/bx.gif"));
    ImageIcon logbx = new ImageIcon(MagicToolApp.class.getResource("gifs/logbx.gif"));
    bPanel.setLayout(flowLayout2);
    flowLayout2.setAlignment(FlowLayout.LEFT);
    flowLayout2.setHgap(0);
    verticalLayout1.setHgap(10);
    verticalLayout1.setVgap(3);
    powerLabel.setBackground(Color.lightGray);
    titlePanel.setBorder(titledBorder1);
    titlePanel.setLayout(verticalLayout2);
    this.getContentPane().add(titlePanel, null);

    titlePanel.add(powerPanel, null);
    titlePanel.add(logPanel, null);
    titlePanel.add(bPanel, null);
    this.getContentPane().add(confirmPanel, null);
    powerPanel.add(powerButton, null);
    powerPanel.add(powerLabel, null);
    powerLabel.setIcon(bx);
    powerPanel.setToolTipText("remember, 1^x = 1, 0^x = 0.");
    logPanel.add(logButton, null);
    logPanel.add(logLabel, null);
    logLabel.setIcon(logbx);
    logPanel.setToolTipText("remember, LOGb(x) is only defined for positive b & x, excluding b = 1");
    transButtonGroup.add(powerButton);
    transButtonGroup.add(logButton);
    logButton.setSelected(true);
    bPanel.setPreferredSize(new Dimension(400, 40));
    bLabel.setText("b = ");
    bPanel.add(bLabel);
    bPanel.add(bField);
    bField.setMinimumSize(new Dimension(10, 21));
    bField.setPreferredSize(new Dimension(100, 21));	//changed 40 to 100; MC:11/3/05
    bField.setText("2");
    bField.setToolTipText("setting b to 1, 0, or anything negative will usually result in a meaningless transformation.");
    confirmPanel.add(transokButton, null);

    transokButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed (ActionEvent e) {
          transokButton_actionPerformed(e);
      }
    });

    confirmPanel.add(transcancelButton, null);

    transcancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed (ActionEvent e)  {
          transcancelButton_actionPerformed(e);
      }
    });

    rootPane.setDefaultButton(transokButton);
    this.show();

  }

  //transforms the data if the user presses ok
  private void transokButton_actionPerformed (ActionEvent e) {
	  
	  Thread thread = new Thread(){
		  public void run(){
			  try{
				  double b;
				  setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				  b = Double.parseDouble(bField.getText().trim());
				  if(powerButton.isSelected())     	
					  exp.transformBX(b);
				  else if(logButton.isSelected())
					  if (b < 0 || b == 1){
						  throw new Exception ("Invalid b Value: b = " + b +"!");	//experimental code 10/3/05
					  }else{
						  //Get the result, if the zero result is a 1 we will abort soon.
						  zeroResult=exp.transformLOGBX(b);
                      }
				  ok=true;
				  dispose();
				  setCursor(Cursor.getDefaultCursor());
			  } catch(Exception e2) {
                             JOptionPane.showMessageDialog(null, e2);
				  //transformStatus.setText(""+e2);		//TODO provide more information about log transformation errors
				  setCursor(Cursor.getDefaultCursor());
			  }
		  }
	  };
	  thread.start();
  }
  
  /**
   * Returns what to do with the zero case.
   */
  public int getZeroResult(){
	  return zeroResult;
  }

  //closes the dialog if the user cancels
  private void transcancelButton_actionPerformed (ActionEvent e) {
    ok=false;
    this.dispose();
  }

  /**
   * returns the expression file to transform
   * @return expression file to transform
   */
  public ExpFile getExpFile(){
    return exp;
  }

  /**
   * returns whether or not the ok button was pressed
   * @return whether or not the ok button was pressed
   */
  public boolean getOK(){
    return ok;
  }

  /**
   * returns the string respresentation of the transform
   * @return string respresentation of the transform
   */
  public String getTransform(){
    if(powerButton.isSelected()) return "x" + bField.getText();
    else if(logButton.isSelected()) return "log" + bField.getText();
    else return "";
  }

}





