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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * TextViewer is a dialog which displays a textfile for the user
 * to read. Users may not edit the text.
 */
public class TextViewer extends JDialog {
  private JScrollPane textScroll = new JScrollPane();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel buttonPanel = new JPanel();
  private JButton okButton = new JButton();
  private JTextArea textArea = new JTextArea();
  private Frame parent;

  /**
   * Constructs a TextViewer for the given input stream and parent frame.
   * The title of the dialog window is blank.
   * @param parent parent frame
   * @param text input stream to display
   */
  public TextViewer(Frame parent, InputStream text){
    this(parent,text,"");
  }

  /**
   * Constructs a TextViewer for the given text string and parent frame.
   * The title of the dialog window is blank.
   * @param parent parent frame
   * @param text text to display
   */
  public TextViewer(Frame parent, String text){
    this(parent,text,"");
  }

  /**
   * Constructs a TextViewer for the given text file and parent frame.
   * The title of the dialog window is blank.
   * @param parent parent frame
   * @param text text file to display
   */
  public TextViewer(Frame parent, File text){
    this(parent,text,"");
  }

  /**
   * Constructs a TextViewer for the given text string and parent frame
   * with the specified title.
   * @param parent parent frame
   * @param text text to display
   * @param title dialog title name
   */
  public TextViewer(Frame parent, String text, String title) {
    super(parent);
    this.parent=parent;
    setText(text);
    this.setTitle(title);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Constructs a TextViewer for the given text file and parent frame
   * with the specified title.
   * @param parent parent frame
   * @param text text file to display
   * @param title dialog title name
   */
  public TextViewer(Frame parent, File text, String title) {
    super(parent);
    this.parent=parent;
    setText(text);
    this.setTitle(title);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

   /**
   * Constructs a TextViewer for the given input stream and parent frame
   * with the specified title.
   * @param parent parent frame
   * @param text input stream to display
   * @param title dialog title name
   */
  public TextViewer(Frame parent, InputStream text, String title) {
    super(parent);
    this.parent=parent;
    setText(text);
    this.setTitle(title);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    this.getContentPane().setLayout(borderLayout1);
    this.setModal(true);
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    textArea.setEditable(false);
    textArea.setCaretPosition(0);

    buttonPanel.add(okButton, null);
    textScroll.getViewport().add(textArea, null);

    this.getContentPane().add(textScroll,  BorderLayout.CENTER);
    this.getContentPane().add(buttonPanel,  BorderLayout.SOUTH);


    this.pack();
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    this.setSize(this.getWidth()+textScroll.getVerticalScrollBar().getWidth(), Math.min(this.getHeight(), parent.getHeight()-100));
    this.setLocation(screen.width/2-this.getWidth()/2,screen.height/2-this.getHeight()/2);
    this.show();
  }

  /**
   * sets the displayed text to the given text string
   * @param text text string to display
   */
  public void setText(String text){
    textArea.setText(text);
    textArea.setCaretPosition(0);
  }

  /**
   * sets the displayed text to the given text file
   * @param text input stream to display
   */
  public void setText(InputStream text){
    try{
      int lines=0;
      int maxchar=0;
      String theText="";
      DataInputStream in = new DataInputStream(new BufferedInputStream(text));
      String line;

      while((line=in.readLine())!=null){
        theText+=(line+"\n");
        maxchar = Math.max(line.length(),maxchar);
      }
      in.close();
      textArea.setText(theText);
    }catch(Exception e){
      textArea.setText("");
    }
    textArea.setCaretPosition(0);
  }

  /**
   * sets the displayed text to the given text file
   * @param text text file to display
   */
  public void setText(File text){
    try{
      int lines=0;
      int maxchar=0;
      String theText="";
      BufferedReader in = new BufferedReader(new FileReader(text));
      String line;

      while((line=in.readLine())!=null){
        theText+=(line+"\n");
        maxchar = Math.max(line.length(),maxchar);
      }
      in.close();
      textArea.setText(theText);
    }catch(Exception e){
      textArea.setText(text.getPath());
    }
    textArea.setCaretPosition(0);
  }

  //displays the dialog when user hits ok
  private void okButton_actionPerformed(ActionEvent e) {
    this.dispose();
  }
}