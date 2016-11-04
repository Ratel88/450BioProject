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

package magictool.image;

import ij.ImagePlus;
import ij.gui.ImageCanvas;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.Point;

import javax.swing.JPanel;

public class FlagImageDisplayPanel extends JPanel {

	  /**
	 * So that Eclipse doesn't complain
	 */
	private static final long serialVersionUID = 1L;
	
	private double zoomed=1.0; //magnification
	  private BorderLayout borderLayout1 = new BorderLayout();
	  private BorderLayout borderLayout2 = new BorderLayout();
	  private Image im; //original image

	  /**microarray image displayed in the panel*/
	  protected ImagePlus ip;
	  /**canvas displaying microarray image*/
	  protected ImageCanvas ic;
	  /**grid manager for the microarray image*/
	  protected GridManager manager;
	  /**flag manager for the microarray image*/
	  protected FlagManager flagman;
	  /**flag manager for automatic flags*/
	  protected FlagManager autoflagman;


	  /**
	   * Constructs an ImageDisplayPanel with the specified image and grid manager
	   * @param im image to display in the panel
	   * @param manager grid manager for the microarray image
	   */
	  public FlagImageDisplayPanel(Image im, GridManager manager, FlagManager fm, FlagManager afm) {
	    this.manager=manager;
	    this.flagman = fm;
	    this.autoflagman = afm;
	    ip = new ImagePlus("Overlayed",im);
	    ic = new ImageCanvas(ip){
	      public void paintComponent(Graphics g){
	        super.paintComponent(g);
	        g.setColor(Color.white);
	        drawGrids(g);
	        
	        drawAllFlags(g);
	      }
	    };

	    try {
	      jbInit();
	    }
	    catch(Exception e) {
	      e.printStackTrace();
	    }
	  }

	  /**
	   * Paints the panel on the specified graphics
	   * @param g graphics to paint the panel on
	   */
	  public void paintComponent(Graphics g) {
	    g.setColor(Color.white);
	    g.fillRect(0,0,this.getHeight(),this.getWidth());
	    g.setColor(Color.white);
	    super.paintComponent(g);

	  }

	  /**
	   * magnifies the zoom by the given factor
	   * @param zoomFactor factor to zoom by
	   */
	  public void zoom(double zoomFactor) {
	    zoomed = zoomed*zoomFactor;
	    ic.setMagnification(zoomed);
	    ic.setImageUpdated();
	    this.setPreferredSize(new Dimension(Math.round((float)(ip.getWidth()*zoomed)),Math.round((float)(ip.getHeight()*zoomed))));
	    ic.repaint();
	    this.repaint();
	  }

	  /**
	   * sets the magnification level
	   * @param magnification magnification level
	   */
	  public void setMagnification(double magnification) {
	    ic.setMagnification(magnification);
	    zoomed = magnification;
	    ic.setImageUpdated();
	    this.setPreferredSize(new Dimension(Math.round((float)(ip.getWidth()*zoomed)),Math.round((float)(ip.getHeight()*zoomed))));
	    ic.repaint();
	    this.repaint();
	  }

	  /**
	   * gets the magnification
	   * @return magnification level
	   */
	  public double getZoom(){
	    return zoomed;
	  }

	  /**
	   * gets the canvas the microarray image is painted on
	   * @return canvas the microarray image is painted on
	   */
	  public ImageCanvas getCanvas() {
	    return ic;
	  }

	  /**
	   * gets the screen x-coordinate from a canvas x-coordinate
	   * @param canvasX canvas x-coordinate
	   * @return screen x-coordinate
	   */
	  public int screenX(int canvasX) {
	    return (Math.round((float)this.getZoom()*(canvasX-this.ic.getSrcRect().x)));
	  }

	  /**
	   * gets the screen y-coordinate from a canvas y-coordinate
	   * @param canvasY canvas y-coordinate
	   * @return screen y-coordinate
	   */
	  public int screenY(int canvasY) {
	    return (Math.round((float)this.getZoom()*(canvasY-this.ic.getSrcRect().y)));
	  }

	  private void jbInit() throws Exception {
	    this.addMouseListener(new java.awt.event.MouseAdapter() {
	      public void mouseEntered(MouseEvent e) {
	        this_mouseEntered(e);
	      }
	    });
	    this.setBackground(Color.white);
	    this.setLayout(borderLayout2);
	    ic.setSrcRectPos(0,0);
	    this.add(ic,BorderLayout.CENTER);
	  }

	  //sets the cursor when mouse enter the panel
	  private void this_mouseEntered(MouseEvent e) {
	    ic.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
	  }
	  
	  /**
	   * Draws a 2-px line of the specified color on the panel from (x1,y1) to (x2,y2) - coordinates are on the <b>image</b>
	   * @param x1 first x-coordinate on the image
	   * @param y1 first y-coordinate on the image
	   * @param x2 first x-coordinate on the image
	   * @param y2 first y-coordinate on the image
	   * @param color color to draw the line
	   */
	  public void drawLine(int x1, int y1, int x2, int y2, Color color) {
		  Graphics g = ic.getGraphics();
		  Graphics2D g2d = (Graphics2D)g;
		  g2d.setPaint(color);
		  g2d.setStroke(new BasicStroke(2));
		  g2d.drawLine(screenX(x1), screenY(y1), screenX(x2), screenY(y2));
	  }
	  
	  /**
	   * Draws a blue 2-px line on the panel from (x1,y1) to (x2,y2) - coordinates are on the <b>image</b> 
	   * @param x1 first x-coordinate on the image
	   * @param y1 first y-coordinate on the image
	   * @param x2 second x-coordinate on the image
	   * @param y2 second y-coordinate on the image
	   */
	  public void drawLine(int x1, int y1, int x2, int y2)
	  {
		  /*Graphics g = ic.getGraphics();
		  //super.paintComponent(g);
		  //paintComponent(g);
		  Graphics2D g2d = (Graphics2D)g;
		  g2d.setPaint(Color.blue);
		  g2d.setStroke(new BasicStroke(2));
		  g2d.drawLine(screenX(x1), screenY(y1), screenX(x2), screenY(y2));*/
		  drawLine(x1,y1,x2,y2,Color.blue);
	  }
	  
	  private void drawLine(int x1, int y1, int x2, int y2, Graphics2D g2d, Color color) {
		  g2d.setPaint(color);
		  g2d.setStroke(new BasicStroke(2));
		  g2d.drawLine(screenX(x1), screenY(y1), screenX(x2), screenY(y2));
	  }
	  
	  private void drawLine(int x1, int y1, int x2, int y2, Graphics2D g2d)
	  {
		  /*g2d.setPaint(Color.blue);
		  g2d.setStroke(new BasicStroke(2));
		  g2d.drawLine(screenX(x1), screenY(y1), screenX(x2), screenY(y2));*/
		  drawLine(x1,y1,x2,y2,g2d,Color.blue);
	  }
	  
	  private void drawAllFlags(Graphics g)
	  {
		  Graphics2D g2d = (Graphics2D)g;
		  //int nspg = flagman.getNumSpotsPerGrid();
		  int numGrids = manager.getNumGrids();
		  for (int i = 0; i < numGrids; i++) {
			  for (int j = 0; j < manager.getGrid(i).getNumOfSpots(); j++) {
				  if (flagman.checkFlag(i, j)) {
					  //draw the X
					  Point[] linePoints = manager.getGridCornersFromTransformedSpotNumber(i, manager.getTransformedSpotNum(i, j));
					  if (linePoints != null) {
						  drawLine(linePoints[0].x, linePoints[0].y, linePoints[2].x, linePoints[2].y, g2d);	//drawLine converts points on the IMAGE to points on the SCREEN
						  drawLine(linePoints[1].x, linePoints[1].y, linePoints[3].x, linePoints[3].y, g2d);
					  }
				  }
				  else if (autoflagman.checkFlag(i, j)) {
					  //draw the X in orange
					  Point[] linePoints = manager.getGridCornersFromTransformedSpotNumber(i, manager.getTransformedSpotNum(i, j));
					  if (linePoints != null) {
						  drawLine(linePoints[0].x, linePoints[0].y, linePoints[2].x, linePoints[2].y, g2d, Color.orange);	//drawLine converts points on the IMAGE to points on the SCREEN
						  drawLine(linePoints[1].x, linePoints[1].y, linePoints[3].x, linePoints[3].y, g2d, Color.orange);
					  }
				  }
			  }
		  }
	  }
	  
	  //draws the grids on the panel
	  private void drawGrids(Graphics g) {
	    for(int i=0; i<manager.getNumGrids(); i++){
	      Grid grid = manager.getGrid(i);
	      Polygon p = grid.getPolygon();
	      if(p!=null){
	        Polygon newP = grid.getTranslatedPolygon();
	        for(int j=0; j<p.xpoints.length; j++){
	            p.xpoints[j]=screenX(p.xpoints[j]);
	            p.ypoints[j]=screenY(p.ypoints[j]);
	            newP.xpoints[j]=screenX(newP.xpoints[j]);
	            newP.ypoints[j]=screenY(newP.ypoints[j]);
	        }
	        Polygon[] vertLines = grid.getVertLines(newP);
	        Polygon[] horiLines = grid.getHoriLines(newP);
	        if(grid.equals(manager.getCurrentGrid())) {
	          //g.setColor(Color.yellow);
	        	g.setColor(Color.white);
	          g.drawPolygon(newP);
	          for(int v = 0;v<vertLines.length;v++) {
	            g.drawPolygon(vertLines[v]);
	          }
	          for(int h = 0;h<horiLines.length;h++) {
	            g.drawPolygon(horiLines[h]);
	          }
	          g.setColor(Color.white);
	        } else {
	          g.drawPolygon(newP);
	          for(int v = 0;v<vertLines.length;v++) {
	            g.drawPolygon(vertLines[v]);
	          }
	          for(int h = 0;h<horiLines.length;h++) {
	            g.drawPolygon(horiLines[h]);
	          }
	        }
	      }
	    }
	  }
}
