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

import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;
import java.awt.BorderLayout;
import java.io.IOException;



public class HelpFrame extends JFrame implements HyperlinkListener {
	/**
	 * This class displays the help for MAGIC tool. To do this a contents pane is displayed on the left.
	 * This panel is a series of HTML links to pages to display on the right. 
	 * Referenced: http://www.apl.jhu.edu/~hall/java/Swing-Tutorial/Swing-Tutorial-JEditorPane.html.
     */
	public JEditorPane helpPane = new JEditorPane();
	public JEditorPane contentPane = new JEditorPane();
	public BorderLayout borderLayout = new BorderLayout();
	public JScrollPane contentScrollPane = new JScrollPane();
	public JScrollPane helpScrollPane = new JScrollPane();
	private URL url;
	private BareBonesBrowserLaunchListener browserlauncher = new BareBonesBrowserLaunchListener();
	/**
	 * @param args
	 */
	
	/**
	 * Constructs a new HelpFrame, this frame displays the help properties.
	 */
	public HelpFrame() {
		//Set the properties for the frame.
		this.setLayout(borderLayout);
		helpPane.setEditable(false);
		contentPane.setEditable(false);
		contentPane.setSize(700,200);
		
		this.add(helpScrollPane, java.awt.BorderLayout.CENTER);
		this.add(contentScrollPane, java.awt.BorderLayout.WEST);
		this.setVisible(true);

		this.setLocation(0,0);
		this.setTitle("MAGIC Tool Help");

	    contentScrollPane.setVerticalScrollBarPolicy(JScrollPane.
                VERTICAL_SCROLLBAR_ALWAYS);
	    contentScrollPane.getViewport().add(contentPane);

	    helpScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	    helpScrollPane.getViewport().add(helpPane);
	    //Try to display the information properly.
		try {
			url = this.getClass().getResource("/MAGICHelp/contents.html");
			contentPane.setContentType( "text/html" );
			contentPane.setPage(url);
			contentPane.addHyperlinkListener(this);
			
			url = this.getClass().getResource("/MAGICHelp/titlepage.html");
			helpPane.setContentType( "text/html" );
			helpPane.setPage(url);
			helpPane.addHyperlinkListener(browserlauncher);
			//helpPane.addHyperlinkListener(this);
		} catch (Exception excep) {
			System.out.println(excep.getMessage());
			System.out.println("An error has occured displaying the help documentation.");
		}
		this.setSize(1000,1000); 
	}
	
	/**
	 * This listener listens for hyprlinks to update. 
	 */
	public void hyperlinkUpdate(HyperlinkEvent event) {
	    if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
	      try {
		    url = event.getURL();
	        helpPane.setPage(url);
	        helpPane.repaint();
	      } catch(IOException ioe) {
	        System.out.println("Error updating help page.");
	      }
	    }
	  }
	

	   class Hyperactive implements HyperlinkListener {
		   
	       public void hyperlinkUpdate(HyperlinkEvent e) {
		          if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			      JEditorPane pane = (JEditorPane) e.getSource();
			      if (e instanceof HTMLFrameHyperlinkEvent) {
			          HTMLFrameHyperlinkEvent  evt = (HTMLFrameHyperlinkEvent)e;
			          HTMLDocument doc = (HTMLDocument)pane.getDocument();
			          doc.processHTMLFrameHyperlinkEvent(evt);
			      } else {
			          try {
				      pane.setPage(e.getURL());
			          } catch (Throwable t) {
				      t.printStackTrace();
			          }
			      }
		          }
		      }
	   }
}
