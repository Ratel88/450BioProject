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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;



/**
 * ProgressFrame is a frame which can be used to display the progress of a process.
 * If the process implements the cancelable interface the frame can be used to cancel
 * the process at anytime.
 */
public class ProgressFrame extends JInternalFrame {

  private JTextArea progressLabel = new JTextArea();
  private JPanel barPane = new JPanel();
  private JProgressBar progressBar = new JProgressBar();
  private VerticalLayout verticalLayout1 = new VerticalLayout();
  private JProgressBar bottomProgressBar;
  private JButton cancelButton = new JButton();

  /** cancelable process that can be canceled by the user*/
  protected Cancelable c;
  /**whether or not the user can cancel a process*/
  protected boolean cancel;

  /**
   * Constructs a progess frame with given title where the user may not cancel
   * a process
   * @param title title to be displayed
   */
  public ProgressFrame(String title){
    this(title,false,null);
  }

  /**
   * Constructs a progess frame with given title where the user may possibly cancel the process
   * in the middle
   * @param title title to be displayed
   * @param cancel whether or not the user may cancel a process
   * @param c cancelable to be canceled if so permitted
   */
  public ProgressFrame(String title, boolean cancel, Cancelable c) {
    progressLabel.setText(title);
    this.cancel=cancel;
    this.c=c;
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  //initializes the frame
  private void jbInit() throws Exception {
	//Referenced forum.java.sun.com.
    this.setLayout(verticalLayout1);
    
    //We are going to make our progressLabel look like a JLabel.
    progressLabel.setFont(new java.awt.Font("Dialog", 1, 13));
    progressLabel.setLineWrap(true);
    progressLabel.setWrapStyleWord(true);
    progressLabel.setEditable(false);
    progressLabel.setBackground ((Color)UIManager.get ("Label.background"));
    progressLabel.setForeground ((Color)UIManager.get ("Label.foreground"));
    progressLabel.setBorder (null);
    
    
    progressBar.setStringPainted(true);
    this.setResizable(false);
    if(cancel&&c!=null) {
    	this.setBounds(100,100,400,150);
    }else{
    	this.setBounds(100,100,400,110);
    }
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    barPane.add(progressBar, null);
    JPanel cancelPanel = new JPanel(new FlowLayout());
    cancelPanel.add(cancelButton);



    this.add(progressLabel,null);
    this.add(progressBar,null);
    if(cancel&&c!=null) this.add(cancelButton,null);
    
    
    //labelPane.add(progressLabel, null);	//TODO some text labels are too big for the width of the progressframe window...
  }

  /**
   * sets the value for current status of the process
   * @param n value for current status of the process
   */
  public void setValue(int n){
    progressBar.setValue(n);
  }

  /**
   * sets the maximum value for a process
   * @param n maximum value for a process
   */
  public void setMaximum(int n){
    progressBar.setMaximum(n);
  }

  /**
   * sets the minimum value for a process
   * @param n minimum value for a process
   */
  public void setMinimum(int n){
    progressBar.setMinimum(n);
  }


  /**
   * adds an amount to the value for the current status of the process
   * @param n amount to add to the value for the current status of the process
   */
  public void addValue(int n){
    progressBar.setValue(progressBar.getValue()+n);
  }

  /**
   * sets the title to be displayed for the current status of the process
   * @param title title to be displayed for the current status of the process
   */
  public void setTitle(String title){
    progressLabel.setText(title);
  }

  /**
   * adds a bottom status bar in the frame
   */
  public void addBottomBar(){
    JPanel bottomBarPane = new JPanel();
    bottomProgressBar = new JProgressBar();
    this.add(bottomBarPane, null);
    bottomBarPane.add(bottomProgressBar, null);
  }

  /**
   * sets the value for the current status of the bottom bar process
   * @param n value for the current status of the bottom bar process
   */
  public void bottomBarSetValue(int n){
    bottomProgressBar.setValue(n);
  }

  /**
   * sets the maximum value for the bottom bar process
   * @param n maximum value for the bottom bar process
   */
  public void bottomBarSetMaximum(int n){
    bottomProgressBar.setMaximum(n);
  }

  /**
   * sets the minimum value for the bottom bar process
   * @param n minimum value for the bottom bar process
   */
  public void bottomBarSetMinimum(int n){
    bottomProgressBar.setMinimum(n);
  }

  /**
   * adds an amount to the value for the current status of the bottom bar process
   * @param n amount to add to the value for the current status of the bottom bar process
   */
  public void bottomBarAddValue(int n){
    bottomProgressBar.setValue(progressBar.getValue()+n);
  }

  //cancels the process if possible
  private void cancelButton_actionPerformed(ActionEvent e) {
    if(cancel&&c!=null) {
      c.cancel();
      this.dispose();
    }
  }

}