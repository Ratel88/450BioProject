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

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JOptionPane;
import javax.swing.UIManager;



/**
  *MagicToolApp is the main class for the MAGIC Tool application. It displays the splashscreen
  *before opening the main frame.
 */
public class MagicToolApp {

    public static String versionString = "2.0";
    public static double versionDouble = 2.0;
    public static String copyrightYears = "2003-2007";
    public static int copyrightEndYear = 2007; 
	
	private boolean packFrame = false;


    /**Construct the application*/
    public MagicToolApp () {

        MainFrame frame = new MainFrame();

        //Validate frames that have preset sizes
        //Pack frames that have useful preferred size info, e.g. from their layout
        if (packFrame) {
            frame.pack();
        }
        else {
            frame.validate();
        }

        //Center the window
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        if (versionString.contains("b")) {	//if this is a beta version
        	Dimension frameSize = frame.getSize();
        	if (frameSize.height > screenSize.height) {
        		frameSize.height = screenSize.height;
        	}
        	if (frameSize.width > screenSize.width) {
        		frameSize.width = screenSize.width;
        	}
        	frame.setLocation((screenSize.width - frameSize.width)/2, 0);
        }
        else frame.setSize(screenSize);
        
        
        frame.setVisible(true);
    }

    /**Main method which constructs the application
     * @param args arguments
     * */
    public static void main (String[] args) {
       try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());  //MC: code to enable local OS GUI
        } catch (Exception e) {
            e.printStackTrace();
        }
        Thread r = new Thread(){

          public void run(){
            try{
              SplashScreen splash = new SplashScreen();
              sleep(3000);
              splash.dispose();
              new MagicToolApp();
            }catch(InterruptedException e){
            	e.printStackTrace();
            	System.out.println("Error in MAGIC Tool main thread");
            }
          }

        };
        r.start();
    }

}



