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
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.BasicSliderUI;

/**
 *This frame allows the user to create an imaginary gene by selecting expression files
 *for each data column.  This gene, along with a dissimilarity file, can then be used to
 *create a cluster file using the supervised K-Means method.
 */
public class GeneCreatorFrame extends JInternalFrame implements KeyListener{

  private JSlider[] sliders;
  private JLabel[] labels;
  private JLabel[] toplabels;
  private JPanel[] panels;
  private JPanel sliderPanel = new JPanel();
  private JScrollPane jScrollPane1 = new JScrollPane();
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel jPanel1 = new JPanel();
  private JPanel jPanel2 = new JPanel();
  private JButton okButton = new JButton();
  private FlowLayout flowLayout1 = new FlowLayout();
  private JButton cancelButton = new JButton();

  /**converts the decimal format of the expression levels*/
  protected DecimalFormat df = new DecimalFormat("###.##");
  /**parent frame*/
  protected GeneCreator parent = null;
  /**the converted expression values based on the respective columns*/
  protected double values[];
  /**parent frame*/
  protected Frame parentFrame;
  /**stores the minimum expression level for each column*/
  protected float min[];
  /**stores the maximum expression level for each column*/
  protected float max[];

  /**
   * Constructs a frame of sliders which allow the user to choose values
   * for every column of data for a gene. The default value is the average of
   * the minimum and maximum value of a column
   * @param exp expression file
   * @param parentFrame parent frame
   */
  public GeneCreatorFrame(ExpFile exp, Frame parentFrame) {
    this.parentFrame=parentFrame;
    min = new float[exp.getLabelArray().length];
    max = new float[min.length];
    sliders = new JSlider[min.length];
    toplabels = new JLabel[min.length];
    labels = new JLabel[min.length];
    panels = new JPanel[min.length];
    for(int i=0; i<min.length; i++){
    min[i] = exp.getMinColumnValue(i);
    max[i] = exp.getMaxColumnValue(i);
    sliders[i] = new JSlider(JSlider.VERTICAL, 0, 100, 50);
    sliders[i].setUI(new BasicSliderUI(sliders[i]));
    sliders[i].setPaintTicks(true);
    sliders[i].setMajorTickSpacing(10);
    toplabels[i] = new JLabel(exp.getLabel(i),labels[i].CENTER);
    labels[i] = new JLabel(""+df.format((min[i] + max[i])/2),labels[i].CENTER);
    labels[i].setFont(new Font(labels[i].getFont().getName(), Font.PLAIN, 10));
    FontMetrics fm = labels[i].getFontMetrics(labels[i].getFont());
    labels[i].setPreferredSize(new Dimension(fm.stringWidth("00000.00"), labels[i].getPreferredSize().height));
    panels[i] = new JPanel(new VerticalLayout());
    panels[i].add(toplabels[i]);
    panels[i].add(sliders[i]);
    panels[i].add(labels[i]);
    final int num = i;
    sliders[i].addChangeListener(new ChangeListener(){
        //sets the offered expression levels based on the values in each column of the expression file
        public void stateChanged(ChangeEvent e){
          labels[num].setText("" + df.format(getConverted(num, sliders[num].getValue())));
          repaint();
        }
      });
    }


    try {
      jbInit();
      this.addKeyListenerRecursively(this);
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }


  /**
   * Initializes the frame for the creation of a new gene
   * @throws Exception exception
   */
  private void jbInit() throws Exception {
    sliderPanel.setLayout(borderLayout1);
    jPanel1.setLayout(flowLayout1);
    this.setClosable(true);
    this.setResizable(true);
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    rootPane.setDefaultButton(okButton);
    cancelButton.setText("Cancel");
    cancelButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        cancelButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(sliderPanel, BorderLayout.CENTER);
    sliderPanel.add(jScrollPane1,  BorderLayout.CENTER);
    jPanel2.setPreferredSize(new Dimension((panels[0].getPreferredSize().width)*panels.length, panels[0].getPreferredSize().height));
    this.setMaximumSize(new Dimension(jPanel2.getPreferredSize().width+30, + jPanel2.getPreferredSize().height+90+jPanel1.getPreferredSize().height));
    this.setMinimumSize(new Dimension((400>getMaximumSize().width?getMaximumSize().width:400), getMaximumSize().height));
    jScrollPane1.getViewport().add(jPanel2, null);
    sliderPanel.add(jPanel1,  BorderLayout.SOUTH);
    jPanel1.add(okButton, null);
    jPanel1.add(cancelButton, null);
    jPanel2.setLayout(new GridLayout(1,min.length));
    for(int i=0; i<min.length; i++){
      jPanel2.add(panels[i]);
    }
  }

  //calculates the expression levels that the user will be able to choose from
  private double getConverted(int num, int value){
    double interval = (max[num] - min[num])/100;
    int subtract = 100 - value;
    double conversion = max[num] - (interval*subtract);
    return conversion;

  }

  //applies the calculated expression levels to the slider bars
  private int convertToSlider(double value, int num){
    double interval = (max[num] - min[num])/100;
    double subtract = value-min[num];
    if(subtract<0) subtract=min[num];
    return Math.round((float)(subtract/interval));
  }

  /**
   * gets the array of values for the created gene
   * @return array of data values for the created gene
   */
  public double [] getCreatedValues() {
    values = new double [sliders.length];
    for(int k=0; k<sliders.length; k++) {
      values[k]=getConverted(k, sliders[k].getValue());
    }
    return values;
  }

  /**
   * sets the values of the sliders
   * @param values data values
   */
  public void setValues(double[] values){
    for(int i=0; i<values.length&&i<sliders.length; i++){
      sliders[i].setValue(convertToSlider(values[i],i));
    }
  }

  /**
   * sets the parent gene creator
   * @param parent class where gene is going to be created
   */
  public void setParent(GeneCreator parent){
    this.parent=parent;
  }


  //finalizes the selected values
  private void okButton_actionPerformed(ActionEvent e) {
    if(parent!=null) parent.setGeneValues(this.getCreatedValues());
    dispose();
  }

  //closes the window
  private void cancelButton_actionPerformed(ActionEvent e) {
    dispose();
  }
  private void addKeyListenerRecursively(Component c){
  c.removeKeyListener(this);
  c.addKeyListener(this);
  if(c instanceof Container){
    Container cont = (Container)c;
    Component[] children = cont.getComponents();
    for(int i = 0; i < children.length; i++){
      addKeyListenerRecursively(children[i]);
    }
  }
}

    /**
     * Closes the frame when user press control + 'w'
     * @param e key event
     */
   public void keyPressed(KeyEvent e){
        if(e.getKeyCode()== KeyStroke.getKeyStroke(KeyEvent.VK_W,KeyEvent.CTRL_MASK).getKeyCode()&&e.isControlDown()){
          this.dispose();
        }
   }

    /**
     * Not implemented in this frame
     * @param e key event
     */
    public void keyReleased(KeyEvent e){}

    /**
     * Not implemented in this frame
     * @param e key event
     */
    public void keyTyped(KeyEvent e){}

}