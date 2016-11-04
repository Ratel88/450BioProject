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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

/**
 * RemoveDialog is a dialog window which allows users to remove files from the project specified
 */
public class RemoveDialog extends JDialog implements KeyListener{
  private JList fileList = new JList();
  private JScrollPane scrollPane = new JScrollPane(fileList);
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel buttonPanel = new JPanel();
  private JButton removeButton = new JButton();
  private JButton cancelButton = new JButton();

  /**project to remove files from*/
  protected Project project;
  /**parent frame*/
  protected MainFrame mainFrame;

  /**
   * Constructs a a dialog window to allow users to remove files from the specified project.
   * @param parent parent frame
   * @param project project to remove files from
   */
  public RemoveDialog (MainFrame parent, Project project) {
    super(parent);
    this.project = project;
    this.mainFrame = parent;
    try {
      jbInit();
      this.addKeyListenerRecursively(this);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //initializes the dialog
  private void jbInit () throws Exception {

    this.getContentPane().setLayout(borderLayout1);
    fileList.setListData(project.getAllFiles());
    removeButton.setText("Remove");
    removeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        removeButton_actionPerformed(e);
      }
    });
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });


    this.getContentPane().add(scrollPane,  BorderLayout.CENTER);
    this.getContentPane().add(buttonPanel,  BorderLayout.SOUTH);
    buttonPanel.add(removeButton, null);
    buttonPanel.add(cancelButton, null);
    fileList.setAutoscrolls(true);
    this.setModal(true);
    this.setTitle("Select File(s) To Remove...");
    this.setSize(new Dimension(250,300));
  }
  private void cancelButton_actionPerformed(ActionEvent e){
    this.dispose();
  }
  private void removeButton_actionPerformed(ActionEvent e){
    Object[] objects = fileList.getSelectedValues();
    String[] files = new String[objects.length];
    for(int i=0; i<files.length; i++)
      files[i] = objects[i].toString();
    if(files.length>0){
      boolean remove = (JOptionPane.showConfirmDialog(mainFrame, "Do You Wish To Permanently Delete Selected File(s)?","",JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION);
      if(remove){
        for(int i=0; i<files.length; i++){
          String result = files[i];
          File f = new File(project.getPath()+result);
          if(result.endsWith(".exp")){
            if(JOptionPane.showConfirmDialog(mainFrame, "Removing This Expression File Will Cause All Files Dependent Upon It To Be Removed As Well\nDo You Wish To Continue?")==JOptionPane.OK_OPTION){
              mainFrame.removeExpFile(f.getPath());
              File[] expF = f.getParentFile().listFiles();
              for(int j=0; j<expF.length; j++){
                project.removeFile(result.substring(0,result.lastIndexOf(File.separator)+1)+expF[j].getName());
              }


              for(int j=0; j<expF.length; j++){
                if (!expF[j].delete())
                  JOptionPane.showMessageDialog(this, "Error Removing " + expF[j].getName()+"\nA Sharing Violation May Exist For This File");
              }
              f.getParentFile().delete();
            }
          }

          else {
            project.removeFile(result);
            if (!f.delete())
              JOptionPane.showMessageDialog(this, "Error Removing " + f.getName()+"\nA Sharing Violation May Exist For This File");
          }
        }
      }
    }
    this.dispose();
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