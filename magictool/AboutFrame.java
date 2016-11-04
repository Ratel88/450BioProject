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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;


/**
 * AboutFrame is a JDialog which displays the logo and information about the software
 */
public class AboutFrame extends JDialog {

  private JLabel splash;
  private ImageIcon icon;
  private JPanel info = new JPanel();
  private JPanel all = new JPanel(new BorderLayout());
  private ImageIcon allIcons[];
  private int interval=1000;
  private JLabel info1 = new JLabel();
  private JLabel info2 = new JLabel();
  private JLabel info3 = new JLabel();
  private JLabel info4 = new JLabel();
  private String messageString1[] = {"MAGIC Tool, version " + MagicToolApp.versionString + ", Copyright (C) 2005-"+ MagicToolApp.copyrightEndYear + " Laurie Heyer"};
  private String messageString2[] = {"Portions copyright (c) 2007 Alok Saldanha. MAGIC Tool comes with ABSOLUTELY NO WARRANTY.","MicroArray Genome Imaging and Clustering (MAGIC) Tool"};
  private String messageString3[] = {"This is free software, and you are welcome to redistribute it under certain conditions.", "Developed by Dr. Laurie J. Heyer, Dr. A. Malcolm Campbell, and Danielle Choi"};
  private String messageString4[] = {"See 'License Info' under the Help menu for further details.", "Programmed by David Moskowitz, Adam Abele, Parul Karnik, Brian Akin, Mac Cowell, Gavin Taylor, Nicholas Dovidio, and Michael Gordon"};
  private int wait=0, waitImage=0, imageNum=0, red=144, green=112;
  private int message2=0,message3=0,message4=0;
  private boolean darker=false;
  private Timer timer=null;
  private Action updateAction;

  /**
   * Constructs the window with the logo and information and displays it in the center
   * of the screen.
   * @param owner parent frame
   */
  public AboutFrame(Frame owner) {
    super(owner);
    this.setModal(true);


    allIcons = new ImageIcon[15];
    for(int i=1; i<=15; i++){
      allIcons[i-1] = new ImageIcon(this.getClass().getResource("gifs/wand_f" + (i<10?"0":"") + i +".gif"));
    }
    icon = allIcons[0];
    splash = new JLabel(icon);
    splash.setOpaque(true);
    splash.setBackground(Color.white);

    info.setBackground(Color.white);
    info.setLayout(new VerticalLayout(VerticalLayout.TOP,3,2));
    info1.setText(messageString1[0]);
    info2.setText(messageString2[0]);
    info3.setText(messageString3[0]);
    info4.setText(messageString4[0]);

    info.add(info1);
    info.add(info2);
    info.add(info3);
    info.add(info4);
    all.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.lightGray, Color.lightGray));
    all.add(splash, BorderLayout.CENTER);
    all.add(info, BorderLayout.SOUTH);
    this.getContentPane().add(all);
    beginAnimation();


    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    this.pack();
    this.setSize(icon.getIconWidth()+160, icon.getIconHeight()+info.getHeight());
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation((screen.width-getWidth())/2,(screen.height-getHeight())/2);
    this.setVisible(true);


  }

  //initializes the focus listener
  private void jbInit() throws Exception {
    Font f = new Font(info2.getFont().getName(), Font.PLAIN, 10);
    info2.setForeground(new Color(144, 112, 255));
    info1.setForeground(new Color(144, 112, 255));
    info3.setForeground(new Color(144, 112, 255));
    info4.setForeground(new Color(144, 112, 255));
    info2.setFont(f);
    info3.setFont(f);
    info4.setFont(f);
    info1.setHorizontalAlignment(JLabel.CENTER);
    info2.setHorizontalAlignment(JLabel.CENTER);
    info3.setHorizontalAlignment(JLabel.CENTER);
    info4.setHorizontalAlignment(JLabel.CENTER);
    this.setResizable(false);
    this.setTitle("About MAGIC Tool...");
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosed(WindowEvent e) {
        this_windowClosed(e);
      }
    });

  }

   //disposes animation when window closed
  private void this_windowClosed(WindowEvent e) {
    if(timer!=null){
      timer.removeActionListener(updateAction);
      timer.stop();
      timer=null;

    }
    this.dispose();
  }

  //animates the names
  private void beginAnimation(){
    wait=50;
    imageNum=1;
    waitImage=0;
    final int dred = (256-red)/15;
    final int dgreen = (256-green)/15;



    updateAction = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {
          int tempred = info3.getForeground().getRed();
          int tempgreen = info3.getForeground().getGreen();

          if(wait==0){

              if(darker){
                tempred-=dred;
                tempgreen-=dgreen;
                if(tempred<red) tempred=red;
                if(tempgreen<green) tempgreen=green;
              }
              else{
                tempgreen+=dgreen;
                tempred+=dred;
                if(tempred>255) tempred=255;
                if(tempgreen>255) tempgreen=255;
              }
              info2.setForeground(new Color(tempred, tempgreen, 255));
              info3.setForeground(new Color(tempred, tempgreen, 255));
              info4.setForeground(new Color(tempred, tempgreen, 255));

              if(tempred==red&&tempgreen==green){
                darker=false;
                wait=50;
              }
              else if(tempred==255&&tempgreen==255){
                darker=true;
                message2++;
                message3++;
                message4++;
                if(message2>=messageString2.length) message2=0;
                if(message3>=messageString3.length) message3=0;
                if(message4>=messageString4.length) message4=0;
                info2.setText(messageString2[message2]);
                info3.setText(messageString3[message3]);
                info4.setText(messageString4[message4]);
              }
          }
          else wait--;

          if(waitImage==0){
              icon = allIcons[imageNum];
              splash.setIcon(icon);
              imageNum++;
              if(imageNum>=allIcons.length){
                imageNum=0;
                waitImage=100;
              }
          }
          else waitImage--;
      }

    };

    timer = new Timer(75, updateAction);
    timer.start();

  }

}