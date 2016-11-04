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

package magictool.task;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.plaf.ColorUIResource;


/**
 * TwoLabelRenderer is a JPanel which implements the ListCellRender interface.
 * The renderer takes two labels and places them together for placement in a list.
 */
public class TwoLabelRenderer extends JPanel implements ListCellRenderer {

  private JLabel label1, label2;
  private Color foreground, background, sforeground, sbackground;

  /**
   * Constructs the renderer and sets up the color scheme.
   */
  public TwoLabelRenderer() {
    setLayout(new GridLayout(1,2));
    foreground = UIManager.getColor("List.textForeground");
    background = UIManager.getColor("List.textBackground");
    sforeground = UIManager.getColor("List.selectionForeground");
    sbackground = Color.yellow;
    this.setBackground(background);

    this.add(label1 = new JLabel());
    this.add(label2 = new JLabel());
    label1.setBorder(new LineBorder(Color.lightGray,1));
    label2.setBorder(new LineBorder(Color.lightGray,1));
  }

  /**
   * returns the appropriate panel based on the two string value in the list
   * @param list list where rendering is to take place
   * @param value two string value to make into panel
   * @param index index of the item to be rendered
   * @param isSelected whether or not the item is selected
   * @param hasFocus whether or not the list has focus
   * @return appropriate panel based on the two string value in the list
   */
  public Component getListCellRendererComponent(JList list, Object value,
               int index, boolean isSelected, boolean hasFocus) {
      if(value instanceof TwoStringValue){
        label1.setText(((TwoStringValue)value).getString1());
        label2.setText(((TwoStringValue)value).getString2());
      }
      else{
        label1.setText(value.toString());
        label2.setText("");
      }
      if(isSelected){
        label1.setForeground(sforeground);
        label1.setBackground(sbackground);
        label2.setForeground(sforeground);
        label2.setBackground(sbackground);
        this.setBackground(sbackground);
      }
      else{
        label1.setForeground(foreground);
        label1.setBackground(background);
        label2.setForeground(foreground);
        label2.setBackground(background);
        this.setBackground(background);
      }
      return this;
    }

  /**
   * sets the background of the panel
   * @param color background color
   */
  public void setBackground(Color color) {
    if (color instanceof ColorUIResource)
      color = null;
    super.setBackground(color);
  }


}