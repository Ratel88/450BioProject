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

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * VerticalLayout is similar to java.awt.FlowLayout except that the flow of the
 * components is placed vertically instead of horizontally. Components are required
 * to fill the entire width of the parent container.
 */
public class VerticalLayout implements LayoutManager {

  /**top alignment*/
  public static final int TOP=0;
  /**middle alignment*/
  public static final int MIDDLE=1;
  /**bottom alignment*/
  public static final int BOTTOM=2;

  /**alignment value*/
  protected int align;
  /**horizontal gap between components*/
  protected int hgap;
  /**vertical gap between components*/
  protected int vgap;


  /**
   * Constructs of VerticalLayout with top alignment of horizontal and vertical gaps of 5
   */
  public VerticalLayout() {
    this(TOP,5,5);
  }

  /**
   * Constructs of VerticalLayout with the given alignment of horizontal and vertical gaps of 5
   * @param align alignment for layout
   */
  public VerticalLayout(int align) {
    this(align,5,5);
  }

  /**
   * Constructs of VerticalLayout with the given alignments and gaps specified by the user
   * @param align alignment for the layout
   * @param hgap horizontal gap
   * @param vgap vertical gap
   */
  public VerticalLayout(int align, int hgap, int vgap) {
    this.align=align;
    this.hgap=hgap;
    this.vgap=vgap;
  }

  /**
   * lays the components out in the parent container
   * @param parent container to place components in
   */
  public void layoutContainer(Container parent){
    Insets insets = parent.getInsets();
    int maxwidth = parent.getSize().width - (insets.left + insets.right + hgap*2);
    int numcomp = parent.getComponentCount();
    int x = insets.left + hgap;
    int y = insets.top + vgap;

    for (int i=0 ; i<numcomp; i++) {
      Component m = parent.getComponent(i);
      if (m.isVisible()) {
        Dimension d = m.getPreferredSize();

        m.setSize(maxwidth, d.height);
        d.width = maxwidth;

        y+= d.height + vgap;
      }
    }

    int leftOver = parent.getSize().height - y - insets.bottom - vgap;
    if(leftOver<0) leftOver = 0;
    int y2 = insets.top + vgap;
    if (getAlignment()==MIDDLE) y2 += leftOver  / 2;
    else if (getAlignment()==BOTTOM) y2 += leftOver;

    for (int i=0; i <numcomp; i++) {
      Component c = parent.getComponent(i);
      Dimension dim = c.getSize();
      if (c.isVisible()) {
        c.setLocation(x, y2);
        y2 += vgap + dim.height;
      }
    }
  }

   /**
     * gets the alignment for this layout.
     * @return alignment value for this layout.
     */
    public int getAlignment() {
	return align;
    }

  /**
     * sets the alignment for this layout.
     * @param align alignment value.
     */
    public void setAlignment(int align) {
	this.align = align;
    }

  /**
     * gets the horizontal gap between components.
     * @return horizontal gap between components.
     */
    public int getHgap() {
	return hgap;
    }

    /**
     * sets the horizontal gap between components.
     * @param hgap horizontal gap between components
     */
    public void setHgap(int hgap) {
	this.hgap = hgap;
    }

    /**
     * gets the vertical gap between components.
     * @return vertical gap between components.
     */
    public int getVgap() {
	return vgap;
    }

    /**
     * sets the vertical gap between components.
     * @param vgap vertical gap between components
     */
    public void setVgap(int vgap) {
	this.vgap = vgap;
    }

    /**
     * adds the specified component to the layout. Not used by this class.
     * @param name the name of the component
     * @param comp the component to be added
     */
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * removes the specified component from the layout. Not used by
     * this class.
     * @param comp the component to remove
     */
    public void removeLayoutComponent(Component comp) {
    }

  /**
   * gets the minimum size of the layout
   * @param parent parent container
   * @return minimum size of the layout
   */
  public Dimension minimumLayoutSize(Container parent){
    return getLayoutSize(parent,true);
  }

  /**
   * gets the preferred size of the layout
   * @param parent parent container
   * @return preferred size of the layout
   */
  public Dimension preferredLayoutSize(Container parent){
    return getLayoutSize(parent,false);
  }

  //sets the layout size
  //method returns minimum size when minimum is true
  //method returns perferred size when minimum is false
  private Dimension getLayoutSize(Container parent,boolean minimum){
    Dimension d = new Dimension(0, 0);
    for (int i=0; i<parent.getComponentCount(); i++) {
      Component c=parent.getComponent(i);
      if (c.isVisible()) {
        Dimension temp=(minimum?c.getMinimumSize():c.getPreferredSize());
        d.width=Math.max(temp.width, d.width);
        d.height+=temp.height+vgap;
      }
    }
    Insets insets = parent.getInsets();
    d.width = d.width + insets.left + insets.right + hgap*2;
    d.height = d.height + insets.top + insets.bottom + vgap;
    return d;
  }

}