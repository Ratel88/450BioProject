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

package magictool.clusterdisplay;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

 /**
  * MetricRuler draws a ruler to display over a MetricTree specifying dissimilarity distances.
  */
 public class MetricRuler extends JPanel {

  /**width pf the ruler*/
  protected int width;
  private int largeunit;
  private int smallunit;
  /**number of large marks*/
  protected int lt;
  /**number of small marks*/
  protected int st;
  /**maximum value on ruler*/
  protected float max;
  /**minimum value on ruler*/
  protected float min;
  private Font f;

  /**
   * Default contructor creates a ruler of width 400, a maximum of 0 and a minimum of 2.
   */
  public MetricRuler(){
    this(400,(float)0,(float)2);
  }

  /**
   * Constructs a metric ruler with specified width, maximum dissimilarity, and minimum dissimilarity.
   * The constructor sets up default number of marks on the ruler.
   * @param width width of ruler
   * @param max maximum dissimilarity
   * @param min minimum dissimilarity
   */
  public MetricRuler(int width, float max, float min) {
    this.max=max;
    this.min=min;
    this.width=width;
    lt=8;
    st=32;
    largeunit=Math.round(width/(float)lt);
    smallunit = Math.round(width/(float)st);
    f= new Font("Times New Roman", Font.BOLD, 12);



  }

  /**
   * paints the ruler on the specified graphics
   * @param g graphics to paint the ruler on
   */
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    String theString = new String();
    g.setFont(f);
    FontMetrics fm = g.getFontMetrics(f);
    g.setColor(Color.blue);

    for(int i=0; i<=lt; i++){
      //g.drawLine((i==0?1:i*largeunit),20,(i==0?1:i*largeunit),this.getHeight());
      g.fillRect((i==0?0:(i*largeunit)-1),20,3,this.getHeight()-20);
      theString = ""+(max-((max-min)/lt*i));

      theString=theString.substring(0,Math.min(theString.length(),4));
      if(i==0)g.drawString(theString,0,18);
      else if(i==lt)g.drawString(theString,i*largeunit-fm.stringWidth(theString),18);
      else g.drawString(theString,i*largeunit-fm.stringWidth(theString)/2,18);

    }

    int minus=0;
    for(int i=1; i<=st; i++){
      int t;
      if(i%(st/lt)!=0)g.drawLine(t=i*smallunit-minus,30,t,this.getHeight());
      else minus=(i*smallunit)-((i/(st/lt))*largeunit);
    }


  }

  /**
   * sets the number of large marks on the ruler
   * @param lt number of large marks on the ruler
   */
  public void setLarge(int lt){
    this.lt=lt;
    largeunit = Math.round(width/lt);
    repaint();
  }

  /**
   * sets the number of small marks on the ruler
   * @param st number of small marks on the ruler
   */
  public void setSmall(int st){
    this.st=st;
    smallunit = Math.round(width/st);
    repaint();
  }

  /**
   * sets the maximum dissimilarity
   * @param max maximum dissimilarity
   */
  public void setMax(float max){
    this.max=max;
    repaint();
  }

  /**
   * sets the minimum dissimilarity
   * @param min minimum dissimilarity
   */
  public void setMin(float min){
    this.min=min;
    repaint();
  }

  /**
   * sets the width of the ruler
   * @param w width of the ruler
   */
  public void setWidth(int w){
    this.width=w;
    largeunit=Math.round(width/(float)lt);
    smallunit = Math.round(width/(float)st);
    repaint();
  }

}

