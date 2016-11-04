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

package magictool.groupdisplay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import magictool.ExpFile;
import magictool.VerticalLayout;

/**
 *ColumnChooser is a JDialog which prompts the user to select two columns from an expression file
 */
public class ColumnChooser extends JDialog {


  private JPanel choosePanel = new JPanel();
  private JPanel buttonPanel = new JPanel();
  private JButton okButton = new JButton();
  private JButton cancelButton = new JButton();
  private TitledBorder titledBorder1;
  private VerticalLayout verticalLayout1 = new VerticalLayout();
  private JPanel firstPanel = new JPanel();
  private JPanel secondPanel = new JPanel();
  private JLabel jLabel1 = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JComboBox colBox1 = new JComboBox();
  private JComboBox colBox2 = new JComboBox();
  private BorderLayout borderLayout1 = new BorderLayout();
  private BorderLayout borderLayout2 = new BorderLayout();
  private Border border1;
  private Border border2;


  private boolean ok=false;
  /**parent frame*/
  protected Frame parent;
  /**expression file*/
  protected ExpFile exp;

  /**
   * Constructs the dialog and finds the list of column available in the expression file
   * @param exp expression file
   * @param parent parent frame
   */
  public ColumnChooser(ExpFile exp, Frame parent) {
    super(parent);
    this.exp=exp;

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  private void jbInit() throws Exception {

    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),2),"Select Columns To Graph");
    border1 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(148, 145, 140),new Color(103, 101, 98)),BorderFactory.createEmptyBorder(2,2,2,2));
    border2 = BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED,Color.white,Color.white,new Color(148, 145, 140),new Color(103, 101, 98)),BorderFactory.createEmptyBorder(2,2,2,2));

    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    choosePanel.setBorder(titledBorder1);
    choosePanel.setLayout(verticalLayout1);
    jLabel1.setBorder(border1);
    jLabel1.setText("First Column");
    jLabel2.setBorder(border2);
    jLabel2.setText("Second Column");
    firstPanel.setLayout(borderLayout1);
    secondPanel.setLayout(borderLayout2);
    this.getContentPane().add(choosePanel, BorderLayout.CENTER);
    choosePanel.add(firstPanel, null);
    choosePanel.add(secondPanel, null);
    secondPanel.add(jLabel2, BorderLayout.WEST);
    secondPanel.add(colBox2, BorderLayout.CENTER);
    this.getContentPane().add(buttonPanel,  BorderLayout.SOUTH);
    buttonPanel.add(okButton, null);
    buttonPanel.add(cancelButton, null);
    firstPanel.add(jLabel1, BorderLayout.WEST);
    firstPanel.add(colBox1, BorderLayout.CENTER);
    for(int i=0; i<exp.getColumns(); i++){
      colBox1.addItem(exp.getLabel(i));
      colBox2.addItem(exp.getLabel(i));
    }
  }

  private void okButton_actionPerformed(ActionEvent e) {
    ok=true;
    this.dispose();
  }

  private void cancelButton_actionPerformed(ActionEvent e) {
    this.dispose();
  }

  /**
   * return first column selection
   * @return first column selection
   */
  public int getColumn1(){
    return colBox1.getSelectedIndex();
  }

  /**
   * returns second column selection
   * @return second column selection
   */
  public int getColumn2(){
    return colBox2.getSelectedIndex();
  }


   /**
    * whether or not ok has been pushed
    * @return whether or not ok has been pushed
    */
  public boolean getOK(){
    return ok;
  }
}