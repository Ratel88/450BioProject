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
import java.awt.Image;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.image.PixelGrabber;

import javax.swing.JPanel;


/**
 * SegementPanel is a panel which display a microarray image with the boundaries
 * of the each gridded spot. The panel is also capable of visually displaying the
 * boundaries of the foreground pixels for each spot.
 */
public class SegmentDisplay extends JPanel {

  private Image im, original;
  private double zoomed=1.0;
  private BorderLayout borderLayout1 = new BorderLayout();
  private BorderLayout borderLayout2 = new BorderLayout();
  private int screenTopLeftX, screenTopLeftY;
  private Polygon cell;

  private int seededRegion[][];
  private int radius = 5;
  private Point center=null;
  private int shapeDrawn = SingleGeneImage.FIXED_CIRCLE;

  /**microarray image for segmentation*/
  protected ImagePlus ip;
  /**canvas to paint image*/
  protected ImageCanvas ic;
  /**grid manager for microarray image*/
  protected GridManager manager;
  /*
   * WTH 7/24/06 Make Raw (16-bit) pixels available for calculations
   */
  /* Raw pixel image values */
  public int RawPixels[][];

  /**
   * Constructs the display for the given image and grid manager
   * @param im microarray image to be displayed for segmentation
   * @param manager grid manager for microarray image
   */
  public SegmentDisplay(Image im, GridManager manager) {
    this.manager=manager;
    manager.setCurrentGrid(0);
    manager.getCurrentGrid().setCurrentSpot(0);
    ip = new ImagePlus("Overlayed",im);
    ic = new ImageCanvas(ip){
      public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.white);
        drawCell(g);
        drawShape(g);
      }
    };



    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
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

  /**
   * paints the panel
   * @param g grpahics to paint the panel on
   */
  public void paintComponent(Graphics g) {
    g.setColor(Color.white);
    g.fillRect(0,0,this.getHeight(),this.getWidth());
    g.setColor(Color.white);
    super.paintComponent(g);
  }

  /**
   * zooms on the image by the given factor
   * @param zoomFactor factor to zoom the image by
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
   * gets the magnification level
   * @return magnification level
   */
  public double getZoom(){
    return zoomed;
  }

  /**
   * gets the image canvas that the image is painted on
   * @return image canvas that the image is painted on
   */
  public ImageCanvas getCanvas() {
    return ic;
  }

  //changes the cursor to hand cursor
  private void this_mouseEntered(MouseEvent e) {
    ic.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
  }

  //draws the grid around the spot
  private void drawCell(Graphics g) {
      g.setColor(Color.yellow);
      Polygon p = manager.getCurrentGrid().getTranslatedPolygon();
      Polygon q = new Polygon();
      if(p!=null) {
        for(int j=0; j<p.xpoints.length; j++){
            //q.xpoints[j]=screenX((int)(p.xpoints[j]/this.getZoom()));
            //q.ypoints[j]=screenY((int)(p.ypoints[j]/this.getZoom()));
          q.xpoints[j]=(int)((screenX(p.xpoints[j]))/getZoom());
          q.ypoints[j]=(int)((screenY(p.ypoints[j]))/getZoom());
        }
      manager.getCurrentGrid().setSpots(q);
      setCell(manager.getCurrentGrid().getCurrentSpot());
      g.drawPolygon(cell);
      }
      g.setColor(Color.white);
  }

  //draws the shape around the foreground pixels
  private void drawShape(Graphics g) {
      g.setColor(Color.red);
      if(shapeDrawn == SingleGeneImage.FIXED_CIRCLE){

        int x = (int)((double)cell.xpoints[0] + (Math.abs(cell.xpoints[1]-cell.xpoints[0]))/2.0 - radius*getZoom());
        int y = (int)((double)cell.ypoints[0] + (Math.abs(cell.ypoints[3]-cell.ypoints[0]))/2.0 - radius*getZoom());
        int r = (int)(((radius*2)+1)*getZoom());
        g.drawOval(x,y,r,r);

      }
      else if(shapeDrawn == SingleGeneImage.SEEDED_REGION){
        if(seededRegion.length>1){
          double spotWidth = ((double)Math.abs(cell.xpoints[1]-cell.xpoints[0]))/(getCellWidth());
          double spotHeight = ((double)Math.abs(cell.ypoints[3]-cell.ypoints[0]))/(getCellHeight());
          //System.out.println("x1:"  + cell.xpoints[0] + "y1:"  + cell.ypoints[0] + "x2:"  + cell.xpoints[1] + "y2:"  + cell.ypoints[3]);
          //System.out.println("w:"  + spotWidth + "cols:"  + seededRegion[0][0] + "h:"  + spotHeight + "rows:"  + seededRegion[0][1]);
          for(int i=0; i<seededRegion.length; i++){
            Polygon p = new Polygon();
            p.addPoint((int)Math.round(((double)cell.xpoints[0] + spotWidth*(seededRegion[i][0]))), (int)Math.round(((double)cell.ypoints[0] + spotHeight*(seededRegion[i][1]))));
            p.addPoint((int)Math.round(((double)cell.xpoints[0] + spotWidth*(seededRegion[i][0]+1)))-1, (int)Math.round(((double)cell.ypoints[0] + spotHeight*(seededRegion[i][1]))));
            p.addPoint((int)Math.round(((double)cell.xpoints[0] + spotWidth*(seededRegion[i][0]+1)))-1, (int)Math.round(((double)cell.ypoints[0] + spotHeight*(seededRegion[i][1]+1)))-1);
            p.addPoint((int)Math.round(((double)cell.xpoints[0] + spotWidth*(seededRegion[i][0]))), (int)Math.round(((double)cell.ypoints[0] + spotHeight*(seededRegion[i][1]+1)))-1);
            g.drawPolygon(p);
            //System.out.println("("+seededRegion[i][0] +"," + seededRegion[i][1] +")");
          }
        }
      }
      else if(shapeDrawn == SingleGeneImage.ADAPTIVE_CIRCLE){
          double spotWidth = ((double)Math.abs(cell.xpoints[1]-cell.xpoints[0]))/(getCellWidth());
          double spotHeight = ((double)Math.abs(cell.ypoints[3]-cell.ypoints[0]))/(getCellHeight());

          int x = (int)((double)cell.xpoints[0] + ((.5+center.x - radius)*getZoom()));
          int y = (int)((double)cell.ypoints[0] + ((.5+center.y - radius)*getZoom()));
          int r = (int)(((radius*2)+1)*getZoom());
          g.drawOval(x,y,r,r);

      }
      g.setColor(Color.white);
  }

  /**
   * sets the drawing to fixed circle with given radius
   * @param radius radius of the fixed circle
   */
  public void setFixedCircle(int radius){
    shapeDrawn = SingleGeneImage.FIXED_CIRCLE;
    this.radius=radius;
  }

  /**
   * sets the drawing to seeded region growing with given foreground pixels
   * @param cells foreground pixels from seeded region growing method
   */
  public void setSeededRegion(int[][] cells){
    shapeDrawn = SingleGeneImage.SEEDED_REGION;
    seededRegion = cells;
  }

  /**
   * sets the drawing to adaptive circle with given center position and radius
   * @param x x-coordinate of center
   * @param y y-coordinate of center
   * @param radius radius of the adaptive circle
   */
  public void setAdaptiveCircle(int x, int y, int radius){
    shapeDrawn = SingleGeneImage.ADAPTIVE_CIRCLE;
    this.radius=radius;
    center = new Point(x,y);
  }


  /**
   * sets the drawing to adaptive circle with given center position and radius
   * @param parameters array of parameters where [0] = x-coordinate of center, [1] = y-coordinate of center, and [2] = radius of the adaptive circle
   */
  public void setAdaptiveCircle(int[] parameters){
    if(parameters.length==3){
      setAdaptiveCircle(parameters[0], parameters[1], parameters[2]);
    }
  }

  /**
   * sets the spot cell coordinates
   * @param p polygon containing the coordinates of the spot
   */
  public void setCell(Polygon p) {
      cell = new Polygon(p.xpoints,p.ypoints,4);
      for(int j=0; j<cell.xpoints.length; j++){
        cell.xpoints[j]=screenX(cell.xpoints[j]);
        cell.ypoints[j]=screenY(cell.ypoints[j]);
      }
  }

  /**
   * sets the spots for the specified spots
   * @param grid grid number
   */
  public void setSpots(int grid){
    Polygon p = manager.getGrid(grid).getTranslatedPolygon();
    Polygon q = new Polygon();
    if(p!=null) {
      for(int j=0; j<p.xpoints.length; j++){
        q.xpoints[j]=(int)((screenX(p.xpoints[j]))/getZoom());
        q.ypoints[j]=(int)((screenY(p.ypoints[j]))/getZoom());
      }
      manager.getGrid(grid).setSpots(q);
    }
  }

  /**
   * gets the coordinates of the current spot
   * @return polygon containing the coordinates of the current spot
   */
  public Polygon getCell() {
      return cell;
  }


  /**
   * gets the width of the current cell
   * @return width of the current cell
   */
  public int getCellWidth(){
      Polygon p = manager.getCurrentGrid().getCurrentSpot();
      return Math.abs(p.xpoints[1]-p.xpoints[0]);
  }

  /**
   * gets the height of the current cell
   * @return height of the current cell
   */
  public int getCellHeight(){
      Polygon p = manager.getCurrentGrid().getCurrentSpot();
      return Math.abs(p.ypoints[2]-p.ypoints[0]);
  }

  /**
   * gets the array of pixels in the current cell
   * @return array of pixels in the current cell
   */
  public int[] getCellPixels(){
      Polygon p = manager.getCurrentGrid().getCurrentSpot();
      if(p!=null){
        int w = Math.abs(p.xpoints[1]-p.xpoints[0]);
        int h = Math.abs(p.ypoints[2]-p.ypoints[0]);
        int[] pixels = new int[w*h];

        /**WTH 7/24/06 replaced with raw pixels
        PixelGrabber pg = new PixelGrabber(ip.getImage(), p.xpoints[0],p.ypoints[0],w,h,pixels,0,w);

           try{
              pg.grabPixels();
              for(int i=0; i<pixels.length; i++){
                pixels[i] = (pixels[i] >>  8) & 0xFF;
              }
              return pixels;

           }catch(Exception e3){System.out.print("Error");}
         */
        try{
           int k = 0;
           for(int y = p.ypoints[0]; y < p.ypoints[2]; y++) {
              for(int x =p.xpoints[0]; x < p.xpoints[1]; x++) {
                 pixels[k++] = this.RawPixels[y][x];
              }
           }
           return pixels;
        }catch(Exception e3){System.out.print("Error");}

      }
        
      return null;
  }

  /**
   * gets the array of pixels in the specified cell
   * @param grid grid number
   * @param spotNum spot number
   * @return array of pixels in the specified cell
   */
  public int[] getCellPixels(int grid, int spotNum){
      Polygon p = manager.getGrid(grid).getSpot(spotNum);
      int w = Math.abs(p.xpoints[1]-p.xpoints[0]);
      int h = Math.abs(p.ypoints[2]-p.ypoints[0]);
      int[] pixels = new int[w*h];

      PixelGrabber pg = new PixelGrabber(ip.getImage(), p.xpoints[0],p.ypoints[0],w,h,pixels,0,w);

         try{
        	 /*
            pg.grabPixels();
            for(int i=0; i<pixels.length; i++){
              pixels[i] = (pixels[i] >>  8) & 0xFF;*/
             int k = 0;
             for(int y = p.ypoints[0]; y < p.ypoints[2]; y++) {
                for(int x =p.xpoints[0]; x < p.xpoints[1]; x++) {
                   pixels[k++] = this.RawPixels[y][x];
                }
             
            }
            return pixels;

         }catch(Exception e3){System.out.print("Error");}

      return null;
  }

  /**
   * gets the width of the current cell
   * @param grid grid number
   * @param spotNum spot number
   * @return width of the current cell
   */
  public int getCellWidth(int grid, int spotNum){
      Polygon p = manager.getGrid(grid).getSpot(spotNum);
      return Math.abs(p.xpoints[1]-p.xpoints[0]);
  }

  /**
   * gets the height of the current cell
   * @param grid grid number
   * @param spotNum spot number
   * @return height of the current cell
   */
  public int getCellHeight(int grid, int spotNum){
      Polygon p = manager.getGrid(grid).getSpot(spotNum);
      return Math.abs(p.ypoints[2]-p.ypoints[0]);
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

}