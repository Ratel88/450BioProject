/////////////////////////////////////////////////////////
//  Bare Bones Browser Launch                          //
//  Version 1.5                                        //
//  December 10, 2005           					   //
//  From http://www.centerkey.com/java/browser/		   //
//  Slightly modified for MAGIC Tool				   //
//  Supports: Mac OS X, GNU/Linux, Unix, Windows XP    //
//  Example Usage:                                     //
//     String url = "http://www.centerkey.com/";       //
//     BareBonesBrowserLaunch.openURL(url);            //
//  Public Domain Software -- Free to Use as You Like  //
/////////////////////////////////////////////////////////

package magictool;

import java.lang.reflect.Method;
import javax.swing.JOptionPane;
import java.net.URL;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class BareBonesBrowserLaunchListener implements HyperlinkListener {

   private static final String errMsg = "Error attempting to launch web browser";

   public void hyperlinkUpdate(HyperlinkEvent e) {
	   if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
		   //this is a "hyperlink activated" event
		   try {
			   URL url = e.getURL();
			   openURL(url);
		   }
		   catch(Exception ex) {
			   System.out.println("Otherwise unhandled exception at magictool.BareBonesBrowserLaunchListener.hyperlinkUpdate");
			   ex.printStackTrace();
		   }
	   }
   }
   
   public void openURL(URL url) {
	   openURL(url.toString());
   }
   
   public void openURL(String url) {
      String osName = System.getProperty("os.name");
      try {
         if (osName.startsWith("Mac OS")) {
            Class fileMgr = Class.forName("com.apple.eio.FileManager");
            Method openURL = fileMgr.getDeclaredMethod("openURL",
               new Class[] {String.class});
            openURL.invoke(null, new Object[] {url});
            }
         else if (osName.startsWith("Windows"))
            Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + url);
         else { //assume Unix or Linux
            String[] browsers = {
               "firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };
            String browser = null;
            for (int count = 0; count < browsers.length && browser == null; count++)
               if (Runtime.getRuntime().exec(
                     new String[] {"which", browsers[count]}).waitFor() == 0)
                  browser = browsers[count];
            if (browser == null)
               throw new Exception("Could not find web browser");
            else
               Runtime.getRuntime().exec(new String[] {browser, url});
            }
         }
      catch (Exception e) {
         JOptionPane.showMessageDialog(null, errMsg + ":\n" + e.getLocalizedMessage());
         }
      }

   }
