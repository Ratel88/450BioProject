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
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;


/**
 * SplashScreen is a window which displays a the logo and information about the software
 */
public class SplashScreen extends JWindow {

  private JLabel splash;
  private ImageIcon icon;
  private JPanel info = new JPanel();
  private JPanel all = new JPanel(new BorderLayout());
  private ImageIcon allIcons[];
  private int waitImage=0, imageNum=1;
  private AbstractAction updateAction;
  private Timer timer=null;

  /**
   * Constructs the window with the logo and information and displays it in the center
   * of the screen.
   */
  public SplashScreen() {

    allIcons = new ImageIcon[15];
    for(int i=1; i<=15; i++){
      allIcons[i-1] = new ImageIcon(this.getClass().getResource("gifs/wand_f" + (i<10?"0":"") + i +".gif"));
    }
    icon = allIcons[0];//new ImageIcon(this.getClass().getResource("gifs/logo.gif"));
    splash = new JLabel(icon);
    splash.setOpaque(true);
    splash.setBackground(Color.white);

    info.setBackground(Color.white);
    info.setLayout(new VerticalLayout(VerticalLayout.TOP,3,2));
    info.add(new JLabel("MAGIC Tool, version " + MagicToolApp.versionString +", Copyright (C) 2005-" + MagicToolApp.copyrightEndYear +" Laurie Heyer", JLabel.CENTER));
    JLabel info2 = new JLabel("MAGIC Tool comes with ABSOLUTELY NO WARRANTY.", JLabel.CENTER);
    JLabel info3 = new JLabel("This is free software, and you are welcome to redistribute it under certain conditions.", JLabel.CENTER);
    JLabel info4 = new JLabel("See 'License Info' under the Help menu for further details.", JLabel.CENTER);
    Font f = new Font(info2.getFont().getName(),Font.PLAIN, 10);
    info2.setFont(f);
    info3.setFont(f);
    info4.setFont(f);
    info.add(info2);
    info.add(info3);
    info.add(info4);
    all.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.lightGray, Color.lightGray));
    all.add(splash, BorderLayout.CENTER);
    all.add(info, BorderLayout.SOUTH);
    this.getContentPane().add(all);
    this.pack();
    this.setSize(icon.getIconWidth(), icon.getIconHeight()+info.getHeight());
    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
    this.setLocation((screen.width-getWidth())/2,(screen.height-getHeight())/2);
    this.setVisible(true);

    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    beginAnimation();
  }

  //initializes the focus listener
  private void jbInit() throws Exception {
    this.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        this_focusLost(e);
      }
    });


  }

  //animates the names
  private void beginAnimation(){
    imageNum=1;
    waitImage=0;

    updateAction = new AbstractAction() {

        public void actionPerformed(ActionEvent e) {

          if(waitImage==0){
              icon = allIcons[imageNum];
              splash.setIcon(icon);
              imageNum++;
              if(imageNum>=allIcons.length){
                waitImage=100;
              }
          }
      }

    };

    timer = new Timer(75, updateAction);
    timer.start();

  }


  //disposes the window when focus is lost
  private void this_focusLost(FocusEvent e) {
    this.dispose();
  }
}