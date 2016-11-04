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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * TwoListPanel is a JPanel which contains two seperate lists. Items on the lists can be moved
 * from one list to the other or around within their current list.
 */
public class TwoListPanel extends JPanel {

  private JPanel jPanel1 = new JPanel();
  private JScrollPane secondScroll;
  private JScrollPane firstScroll;
  private GridLayout gridLayout1 = new GridLayout();
  private VerticalLayout verticalLayout2 = new VerticalLayout();
  private JPanel jPanel2 = new JPanel();
  private JButton addButton = new JButton();
  private JButton removeButton = new JButton();
  private VerticalLayout verticalLayout1 = new VerticalLayout();
  private JLabel firstLabel = new JLabel();
  private GridLayout gridLayout2 = new GridLayout();
  private JPanel jPanel3 = new JPanel();
  private JLabel secondLabel = new JLabel();
  private JLabel jLabel2 = new JLabel();
  private JButton moveUp = new JButton();
  private JButton moveDown = new JButton();
  private JButton restore = new JButton();

  /**values in original first list*/
  protected String origFirst[];
  /**values in original second list*/
  protected String origSecond[];

  /**first list*/
  protected JList firstList = new JList();
  /**second list*/
  protected JList secondList = new JList();
  /**whether or not items can be added or removed from the lists*/
  protected boolean addRemove=false;
  /**model for list containing list elements*/
  protected DefaultListModel firstModel, secondModel;

  /**
   * Constructs the TwoListPanel from specified first and second elements.
   * The add and remove buttons are not visible.
   * @param firstElements elements for first list
   * @param secondElements elements for second list
   */
  public TwoListPanel(String[] firstElements, String[] secondElements){
    this(firstElements,secondElements, "", "",false);
  }

  /**
   * Constructs the TwoListPanel from specified first and second elements with labels above the lists.
   * The add and remove buttons are not visible.
   * @param firstElements elements for first list
   * @param secondElements elements for second list
   * @param label1 label above first list
   * @param label2 label above second list
   */
  public TwoListPanel(String[] firstElements, String[] secondElements, String label1, String label2) {
    this(firstElements,secondElements, label1, label2,false);
  }

  /**
   * Constructs the TwoListPanel from specified first and second elements with labels above the lists.
   * The add and remove buttons are available dependent upon the user specified value.
   * @param firstElements elements for first list
   * @param secondElements elements for second list
   * @param label1 label above first list
   * @param label2 label above second list
   * @param addRemove whether or not the add and remove buttons should be visible
   */
  public TwoListPanel(String[] firstElements, String[] secondElements, String label1, String label2, boolean addRemove) {
    this.addRemove=addRemove;
    firstModel = new DefaultListModel();
    secondModel = new DefaultListModel();
    secondList.setModel(secondModel);
    firstList.setModel(firstModel);
    firstLabel.setText(label1);
    secondLabel.setText(label2);
    origFirst=firstElements;
    origSecond=secondElements;
    if (firstElements!=null) {
      for (int i=0; i<firstElements.length; i++) {
       firstModel.addElement(firstElements[i]);
      }
    }
    if (secondElements!=null) {
      for (int i=0; i<secondElements.length; i++) {
       secondModel.addElement(secondElements[i]);
      }
    }

    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  //initializes the panel
  private void jbInit() throws Exception {
    jPanel1.setLayout(gridLayout1);

    firstScroll = new JScrollPane(firstList);
    secondScroll = new JScrollPane(secondList);

    if(!firstLabel.getText().equals("")&&!secondLabel.getText().equals(""))this.add(jPanel3, BorderLayout.NORTH);
    this.setLayout(verticalLayout2);
    jPanel2.setLayout(verticalLayout1);
    addButton.setText("<<Add");
    addButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addButton_actionPerformed(e);
      }
    });
    removeButton.setText("Remove>>");
    removeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeButton_actionPerformed(e);
      }
    });
    verticalLayout1.setAlignment(VerticalLayout.MIDDLE);
    jPanel3.setLayout(gridLayout2);
    if(!firstLabel.getText().trim().equals("")&&!secondLabel.getText().trim().equals(""))this.add(jPanel3, null);
    moveUp.setText("Move Up");
    moveUp.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moveUp_actionPerformed(e);
      }
    });
    moveDown.setText("Move Down");
    moveDown.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moveDown_actionPerformed(e);
      }
    });
    restore.setText("Restore Original");
    restore.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        restore_actionPerformed(e);
      }
    });
    firstList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    secondList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    this.add(jPanel1, BorderLayout.CENTER);



    jPanel1.add(firstScroll, null);
    jPanel1.add(jPanel2, null);

    if (addRemove) {
      jPanel2.add(addButton, null);
    }
    if (addRemove) {
      jPanel2.add(removeButton, null);
    }

    jPanel2.add(moveUp, null);
    jPanel2.add(moveDown, null);
    jPanel2.add(restore, null);


    jPanel1.add(secondScroll, null);
    jPanel3.add(firstLabel, null);
    jPanel3.add(jLabel2, null);
    jPanel3.add(secondLabel, null);

    ListSelectionModel lsm1 = firstList.getSelectionModel();
    lsm1.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e){
        secondList.clearSelection();
      }
    });
    ListSelectionModel lsm2 = secondList.getSelectionModel();
    lsm2.addListSelectionListener(new ListSelectionListener(){
      public void valueChanged(ListSelectionEvent e){
        firstList.clearSelection();
      }
    });
  }

  //adds elements to the first list and removes them from the second list
  private void addButton_actionPerformed(ActionEvent e) {
    int pos[] = secondList.getSelectedIndices();
    for (int i=0; i<pos.length; i++) {
      firstModel.addElement(secondModel.elementAt(pos[i]-i));
      secondModel.removeElementAt(pos[i]-i);
    }
    firstList.clearSelection();
    secondList.clearSelection();
  }

  //removes elements to the first list and adds them from the second list
  private void removeButton_actionPerformed(ActionEvent e) {
    int pos[] = firstList.getSelectedIndices();
    for (int i=0; i<pos.length; i++) {
      secondModel.addElement(firstModel.elementAt(pos[i]-i));
      firstModel.removeElementAt(pos[i]-i);
    }
    firstList.clearSelection();
    secondList.clearSelection();
  }

  /**
   * returns an array of the first elements
   * @return array of the first elements
   */
  public String[] getFirstElements() {
    Object o[]= firstModel.toArray();
    String s[] = new String[o.length];
    for (int i=0; i<s.length; i++) {
      s[i]=o[i].toString();
    }
    return s;
  }

  /**
   * returns an array of the second elements
   * @return array of the second elements
   */
  public String[] getSecondElements() {
    Object o[]= secondModel.toArray();
    String s[] = new String[o.length];
    for(int i=0; i<s.length; i++){
      s[i]=o[i].toString();
    }
    return s;
  }

  //restores original lists
  private void restore_actionPerformed(ActionEvent e) {
    firstModel.removeAllElements();
    secondModel.removeAllElements();
    if (origFirst!=null) {
      for(int i=0; i<origFirst.length; i++){
       firstModel.addElement(origFirst[i]);
      }
    }
    if (origSecond!=null) {
      for (int i=0; i<origSecond.length; i++) {
       secondModel.addElement(origSecond[i]);
      }
    }
  }

  //moves element down
  private void moveDown_actionPerformed(ActionEvent e) {
    if (!firstList.isSelectionEmpty()) {
      int start = firstList.getSelectedIndex();
      int end = start + firstList.getSelectedIndices().length-1;
      if (end+1<firstModel.getSize()) {
        String temp = firstModel.getElementAt(end+1).toString();
        firstModel.removeElementAt(end+1);
        firstModel.insertElementAt(temp,start);
        int[] index = new int[end-start+1];
        for (int i=0; i<index.length; i++) {
          index[i]=start+i+1;
        }
        firstList.setSelectedIndices(index);
      }
      else JOptionPane.showMessageDialog(null, "Could Not Perform Desired Function");
    }
    else if (!secondList.isSelectionEmpty()) {
      int start = secondList.getSelectedIndex();
      int end = start + secondList.getSelectedIndices().length-1;
      if (end+1<secondModel.getSize()) {
        String temp = secondModel.getElementAt(end+1).toString();
        secondModel.removeElementAt(end+1);
        secondModel.insertElementAt(temp,start);
      }
      else JOptionPane.showMessageDialog(null, "Could Not Perform Desired Function");
    }
    else JOptionPane.showMessageDialog(null, "Could Not Perform Desired Function");
  }

  //moves elements up
  private void moveUp_actionPerformed(ActionEvent e) {
    if (!firstList.isSelectionEmpty()) {
      int start = firstList.getSelectedIndex();
      int end = start + firstList.getSelectedIndices().length-1;
      if (start>0) {
        String temp = firstModel.getElementAt(start-1).toString();
        firstModel.removeElementAt(start-1);
        firstModel.insertElementAt(temp,end);
      }
      else JOptionPane.showMessageDialog(null, "Could Not Perform Desired Function");
    }
    else if (!secondList.isSelectionEmpty()) {
      int start = secondList.getSelectedIndex();
      int end = start + secondList.getSelectedIndices().length-1;
      if (start>0) {
        String temp = secondModel.getElementAt(start-1).toString();
        secondModel.removeElementAt(start-1);
        secondModel.insertElementAt(temp,end);
      }
      else JOptionPane.showMessageDialog(null, "Could Not Perform Desired Function");
    }
    else JOptionPane.showMessageDialog(null, "Could Not Perform Desired Function");
  }









}

