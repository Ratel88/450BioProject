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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.text.DecimalFormat;

import javax.swing.JLabel;


/**
 * ColorLabel creates a gradient label containing three colors. The gradient changes from the left
 * color to the center color and then from the center color to the right color. The location of
 * the left, center, and right colors may be changed so as to change the speed of the color change.
 */
public class ColorLabel extends JLabel {

  private DecimalFormat format = new DecimalFormat("0.##");

  /**value for far left position*/
  protected double min;
  /**value for far right position*/
  protected double max;
  /**value for center position and location of center color*/
  protected double center;
  /**value for location of left color*/
  protected double beginValue;
  /**value for location of right color*/
  protected double endValue;
  /**left color*/
  protected Color c1;
  /**center color*/
  protected Color c2;
  /**right color*/
  protected Color c3;
  /**whether or not to print labels for min and max values*/
  protected boolean showLabels=false;

  /**
   * Constructs the color label with the initial parameters provided
   * @param beginValue value for location of left color
   * @param endValue value for location of right color
   * @param min value for far left position
   * @param max value for far right position
   * @param center value for center position and location of center color
   * @param c1 left color
   * @param c2 center color
   * @param c3 right color
   */
  public ColorLabel(double beginValue, double endValue, double min, double max, double center, Color c1, Color c2, Color c3) {
    this.min=min;
    this.max=max;
    this.center=center;
    this.c1=c1;
    this.c2=c2;
    this.c3=c3;
    this.beginValue=beginValue;
    this.endValue=endValue;
  }

  /**
   * changes the values for the locations of the left and right colors
   * @param beginValue value for location of left color
   * @param endValue value for location of right color
   */
  public void setBeginEndValues(double beginValue, double endValue){
    this.beginValue=beginValue;
    this.endValue=endValue;
    this.repaint();
  }

  /**
   * sets the value for center position and location of center color
   * @param center value for center position and location of center color
   */
  public void setCenter(double center){
    this.center=center;
    this.repaint();
  }

  /**
   * sets the min and max values for the label
   * @param min value for far left position
   * @param max value for far right position
   */
  public void setMinMax(double min, double max){
    this.min=min;
    this.max=max;
    this.repaint();
  }

  /**
   * sets the colors for the label
   * @param c1 left color
   * @param c2 center color
   * @param c3 right color
   */
  public void setColors(Color c1, Color c2,Color c3){
    this.c1=c1;
    this.c2=c2;
    this.c3=c3;
    this.repaint();
  }

  /**
   * prints the labels for the min and max values at the ends of the label
   */
  public void showLabels(){
    showLabels=true;
    repaint();
  }

  /**
   * hides the labels for the min and max values
   */
  public void hideLabels(){
    showLabels=false;
    repaint();
  }

  /**
   * Paints the color label
   * @param g graphics to paint the label on
   */
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    int width=this.getWidth();
    int height=this.getHeight();
    int beginx = valueToPos(beginValue);
    int endx = valueToPos(endValue);
    int centerx = valueToPos(center);

    g.setColor(c1);
    g.fillRect(0,0,beginx,height);
    g.setColor(c3);
    g.fillRect(endx,0,width-endx,height);

    for(int i=beginx; i<endx; i++){
      g.setColor(getColorAt(i,beginx,endx,centerx));
      g.drawLine(i,0,i,height);
    }

    if(showLabels){
      Font f = new Font("Dialog", Font.BOLD, 12);
      FontMetrics fm = g.getFontMetrics(f);
      g.setFont(f);
      g.setColor(c3);
      g.drawString(format.format(min), 2, (this.getHeight()-fm.getHeight())/2+fm.getHeight()-fm.getDescent());
      g.setXORMode(c1);
      g.drawString(format.format(max), this.getWidth()-fm.stringWidth(format.format(max))-2, (this.getHeight()-fm.getHeight())/2+fm.getHeight()-fm.getDescent());
    }
  }

  /**
   * changes the values of the label to pixel positions within the label
   * @param value label value
   * @return pixel position
   */
  protected int valueToPos(double value){
    double delta = this.getWidth()/((max-min));
    return (int)Math.round(((value-min))*delta);
  }

  /**
   * gets the color at a specific pixel position in the label
   * @param xpos current pixel location
   * @param beginx pixel location for left color
   * @param endx pixel location for right color
   * @param centerx pixel location for center color
   * @return the color for the current pixel location
   */
  public Color getColorAt(int xpos, int beginx, int endx, int centerx){
    double deltared,deltagreen,deltablue;
    int r, g, b;
    if(xpos<centerx){
       deltared = ((float)(c2.getRed()-c1.getRed()))/(centerx-beginx);
       deltagreen = ((float)(c2.getGreen()-c1.getGreen()))/(centerx-beginx);
       deltablue = ((float)(c2.getBlue()-c1.getBlue()))/(centerx-beginx);

       r = (int)Math.round(c1.getRed()+deltared*(xpos-beginx));
       g = (int)Math.round(c1.getGreen()+deltagreen*(xpos-beginx));
       b = (int)Math.round(c1.getBlue()+deltablue*(xpos-beginx));
    }

    else{
       deltared = ((float)(c3.getRed()-c2.getRed()))/(endx-centerx);
       deltagreen = ((float)(c3.getGreen()-c2.getGreen()))/(endx-centerx);
       deltablue = ((float)(c3.getBlue()-c2.getBlue()))/(endx-centerx);

       r = (int)Math.round(c2.getRed()+deltared*(xpos-centerx));
       g = (int)Math.round(c2.getGreen()+deltagreen*(xpos-centerx));
       b = (int)Math.round(c2.getBlue()+deltablue*(xpos-centerx));


    }


    if(r<0) r=0;
    if(r>255) r=255;
    if(g<0) g=0;
    if(g>255) g=255;
    if(b<0) b=0;
    if(b>255) b=255;
    return new Color(r,g,b);
  }

}