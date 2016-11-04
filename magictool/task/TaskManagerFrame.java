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

package magictool.task;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.BevelBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;

import magictool.VerticalLayout;

/**
 * TaskManagerFrame is a frame which displays for the user a list of all the scheduled
 * tasks and their current status. It allows the user to add or remove tasks and to
 * change the order of the tasks. The frame also provides the place where the user can
 * start and stop the execution of the tasks.
 */
public class TaskManagerFrame extends JInternalFrame implements KeyListener{

  private JPanel mainPanel = new JPanel();
  private JPanel buttonPanel = new JPanel();
  private JScrollPane taskScroll = new JScrollPane();
  private JList taskList = new JList();
  private JPanel eastPanel = new JPanel();
  private JButton runButton = new JButton();
  private JButton stopButton = new JButton();
  private JButton closeButton = new JButton();
  private JButton moveUpButton = new JButton();
  private JButton moveDownButton = new JButton();
  private VerticalLayout verticalLayout1 = new VerticalLayout();
  private JButton removeButton = new JButton();
  private JButton clearButton = new JButton();
  private JButton addButton = new JButton();
  private JButton removeCButton = new JButton();

  /**task manager which managers the scheduled tasks*/
  protected TaskManager manager;
  /**whether or not the scheduled tasks are currently being executed*/
  protected boolean running=false;
  private JDesktopPane desk; //desktop
  /**parent frame*/
  protected Frame parentFrame;

  /**
   * Constructs the frame for given taskmanager
   * @param manager taskmanager which manages the scheduled tasks
   * @param desk desktop
   * @param parentFrame parent frame
   */
  public TaskManagerFrame(TaskManager manager, JDesktopPane desk, Frame parentFrame) {
    this.manager=manager;
    this.desk=desk;
    this.parentFrame=parentFrame;
    try {
      jbInit();
      this.addKeyListenerRecursively(this);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  //initializes the frame
  private void jbInit() throws Exception {
    taskList.setModel(manager);
    taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    taskList.setCellRenderer(new TwoLabelRenderer());
    taskScroll = new JScrollPane(taskList);

    runButton.setEnabled(false);
    runButton.setText("Run");
    runButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        runButton_actionPerformed(e);
      }
    });

    stopButton.setEnabled(false);
    stopButton.setText("Stop");
    stopButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        stopButton_actionPerformed(e);
      }
    });

    closeButton.setText("Close");
    closeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        closeButton_actionPerformed(e);
      }
    });

    moveUpButton.setEnabled(false);
    moveUpButton.setText("Move Up");
    moveUpButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moveUpButton_actionPerformed(e);
      }
    });

    moveDownButton.setEnabled(false);
    moveDownButton.setText("Move Down");
    moveDownButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        moveDownButton_actionPerformed(e);
      }
    });

    eastPanel.setLayout(verticalLayout1);

    removeButton.setEnabled(false);
    removeButton.setText("Remove");
    removeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeButton_actionPerformed(e);
      }
    });

    clearButton.setText("Clear All");
    clearButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        clearButton_actionPerformed(e);
      }
    });

    clearButton.setEnabled(false);
    addButton.setText("Add Task");
    addButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        addButton_actionPerformed(e);
      }
    });

    removeCButton.setText("Remove Completed");
    removeCButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeCButton_actionPerformed(e);
      }
    });

    taskList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
      public void valueChanged(ListSelectionEvent e) {
        taskList_valueChanged(e);
      }
    });

    this.getContentPane().add(mainPanel, BorderLayout.CENTER);
    mainPanel.add(taskScroll, null);
    this.getContentPane().add(buttonPanel,  BorderLayout.SOUTH);

    buttonPanel.add(addButton, null);
    buttonPanel.add(runButton, null);
    buttonPanel.add(stopButton, null);
    buttonPanel.add(removeCButton, null);
    buttonPanel.add(closeButton, null);

    this.getContentPane().add(eastPanel, BorderLayout.EAST);
    eastPanel.add(moveUpButton, null);
    eastPanel.add(moveDownButton, null);
    eastPanel.add(removeButton, null);
    eastPanel.add(clearButton, null);

    JPanel titlePanel = new JPanel(new GridLayout(1,2));
    JLabel nameLabel = new JLabel("Name");
    JLabel statusLabel = new JLabel("Status");
    statusLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
    nameLabel.setBorder(new BevelBorder(BevelBorder.RAISED));
    titlePanel.add(nameLabel);
    titlePanel.add(statusLabel);
    taskScroll.setColumnHeaderView(titlePanel);

    taskList.setFixedCellWidth(300);
    taskList.setFixedCellHeight(15);

    taskList.getModel().addListDataListener(new ListDataListener(){
       public void contentsChanged(ListDataEvent e){
        if(!running){
          if(taskList.getModel().getSize()==0) runButton.setEnabled(false);
          else runButton.setEnabled(true);
        }
       }
       public void intervalAdded(ListDataEvent e){
        contentsChanged(e);
       }
       public void intervalRemoved(ListDataEvent e){
        contentsChanged(e);
       }
    });
  }

  //adds a task to the taskmanager which is displayed in the frame - opens the taskbuilderframe
  private void addButton_actionPerformed(ActionEvent e) {
    Thread thread = new Thread(){
      public void run(){
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        TaskBuilderFrame taskdisplay = new TaskBuilderFrame(manager.project,manager,parentFrame);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        taskdisplay.pack();
        desk.add(taskdisplay);
        taskdisplay.setLocation((screen.width-taskdisplay.getWidth())/2,(screen.height-taskdisplay.getHeight())/2);
        taskdisplay.show();
        setCursor(Cursor.getDefaultCursor());
      }
    };
    thread.start();
  }

  //runs through all of the scheduled tasks
  private void runButton_actionPerformed(ActionEvent e) {
    Thread thread = new Thread(){
      public void run(){
        moveUpButton.setEnabled(false);
        moveDownButton.setEnabled(false);
        removeButton.setEnabled(false);
        clearButton.setEnabled(false);
        addButton.setEnabled(false);
        removeCButton.setEnabled(false);
        stopButton.setEnabled(true);
        running=true;
        manager.start();
        while(!manager.isFinished()){}
        JOptionPane.showMessageDialog(null,"The TaskManager Has Successfully Completed " + manager.getSuccessful() + " Out Of " + manager.getSize() + " Scheduled Tasks");
        grabFocus();
        if(taskList.getSelectedIndex()==0||taskList.getSelectedIndex()==-1) moveUpButton.setEnabled(false);
        else moveUpButton.setEnabled(true);
        if(taskList.getSelectedIndex()==manager.getSize()-1||taskList.getSelectedIndex()==-1) moveDownButton.setEnabled(false);
        else moveDownButton.setEnabled(true);
        if(taskList.getSelectedIndex()!=-1) removeButton.setEnabled(true);
        else removeButton.setEnabled(false);
        clearButton.setEnabled(true);
        addButton.setEnabled(true);
        removeCButton.setEnabled(true);
        stopButton.setEnabled(false);
        running=false;
      }
     };
     thread.start();
  }

  //stops the execution of the scheduled tasks
  private void stopButton_actionPerformed(ActionEvent e) {
    manager.cancel();
    if(taskList.getSelectedIndex()==0||taskList.getSelectedIndex()==-1) moveUpButton.setEnabled(false);
    else moveUpButton.setEnabled(true);
    if(taskList.getSelectedIndex()==manager.getSize()-1||taskList.getSelectedIndex()==-1) moveDownButton.setEnabled(false);
    else moveDownButton.setEnabled(true);
    if(taskList.getSelectedIndex()!=-1) removeButton.setEnabled(true);
    else removeButton.setEnabled(false);
    clearButton.setEnabled(true);
    addButton.setEnabled(true);
    removeCButton.setEnabled(true);
    stopButton.setEnabled(false);
    running=false;
    manager.returnToReady();
  }

  //removes completed tasks from the list
  private void removeCButton_actionPerformed(ActionEvent e) {
    manager.removeCompleted();
    taskList.clearSelection();
  }

  //closes the window
  private void closeButton_actionPerformed(ActionEvent e) {
    this.setVisible(false);
  }

  //moves a task up the list if it is possible
  private void moveUpButton_actionPerformed(ActionEvent e) {
    System.out.println("In TaskManagerFrame move up method...");  //Unused?
	if(manager.canMoveUp(taskList.getSelectedIndex())){
      manager.moveTaskUp(taskList.getSelectedIndex());
      taskList.setSelectedIndex(taskList.getSelectedIndex()-1);
    }
    else JOptionPane.showMessageDialog(this, "Error! This Task Requires The File Created By The Task Above");
  }

  //moves a task down the list if it is possible
  private void moveDownButton_actionPerformed(ActionEvent e) {
    if(manager.canMoveDown(taskList.getSelectedIndex())){
      manager.moveTaskDown(taskList.getSelectedIndex());
      taskList.setSelectedIndex(taskList.getSelectedIndex()+1);
    }
    else JOptionPane.showMessageDialog(this, "Error! The Task Above Requires The File Created By This Task");
  }

  //used to determine which buttons should be enabled
  private void taskList_valueChanged(ListSelectionEvent e) {
    this.grabFocus();
    if(!running){
      if(taskList.getSelectedIndex()==0||taskList.getSelectedIndex()==-1) moveUpButton.setEnabled(false);
      else moveUpButton.setEnabled(true);
      if(taskList.getSelectedIndex()==manager.getSize()-1||taskList.getSelectedIndex()==-1) moveDownButton.setEnabled(false);
      else moveDownButton.setEnabled(true);
      if(taskList.getSelectedIndex()!=-1) removeButton.setEnabled(true);
      else removeButton.setEnabled(false);
    }
  }

  //removes a task from the list and possbly other tasks which require that task
  private void removeButton_actionPerformed(ActionEvent e) {
    int num = taskList.getSelectedIndex();
    if(!manager.removesRequiredFile(num)||JOptionPane.showConfirmDialog(this, "Removing This Task Will Also Remove One Or More Tasks Which Require Its Created File.\nDo You Wish To Continue?")==JOptionPane.OK_OPTION){
      manager.removeTask(num);
      taskList.clearSelection();
    }
    removeButton.setEnabled(false);
    moveUpButton.setEnabled(false);
    moveDownButton.setEnabled(false);
    if(manager.getSize()<1){
      clearButton.setEnabled(false);
    }
  }

  //clears the taskmanager of all tasks
  private void clearButton_actionPerformed(ActionEvent e) {
    manager.removeAll();
    taskList.clearSelection();
    removeButton.setEnabled(false);
    moveUpButton.setEnabled(false);
    moveDownButton.setEnabled(false);
    clearButton.setEnabled(false);
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


}