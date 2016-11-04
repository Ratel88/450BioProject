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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

/**
 * GenePanel is a panel which contains a printable table displaying information
 * about selected genes
 */
public class GenePanel extends JPanel {

  private TitledBorder titledBorder1;
  private BorderLayout borderLayout1 = new BorderLayout();
  private JPanel mainPanel = new JPanel();
  private CardLayout geneCard = new CardLayout();
  private JPanel tablePanel = new JPanel();
  private JPanel noPanel = new JPanel();
  private JScrollPane scroll;
  private GridLayout gridLayout1 = new GridLayout();
  private GridLayout gridLayout2 = new GridLayout();
  private JLabel noGeneLabel = new JLabel();

  /**vector of all genes displayed in the table*/
  protected Vector genes = new Vector();
  /**expression file whose genes are displayed in the table*/
  protected ExpFile exp;
  /**table displaying gene information*/
  protected PrintableTable geneTable;

  /**
   * Constructs the gene panel for the given expression file and given intial selected genes
   * @param exp expression file whose genes are displayed in the table
   * @param genes genes to be displayed in the table
   */
  public GenePanel(ExpFile exp, Vector genes) {
    this.genes=genes;
    this.exp=exp;
    this.setMinimumSize(new Dimension(0,0));
    geneTable = new PrintableTable(exp,genes,PrintableTable.NORMAL,false,true);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
  }

  private void jbInit() throws Exception {
    titledBorder1 = new TitledBorder(BorderFactory.createLineBorder(new Color(153, 153, 153),1),"Gene Info");
    noGeneLabel.setHorizontalAlignment(SwingConstants.CENTER);
    noGeneLabel.setText("No Genes Selected");
    noPanel.setBackground(new Color(204, 204, 204));
    noPanel.setLayout(gridLayout2);
    tablePanel.setBackground(new Color(204, 204, 204));
    tablePanel.setLayout(gridLayout1);
    scroll = new JScrollPane(geneTable);
    scroll.setBorder(null);
    scroll.getViewport().setBackground(new Color(204, 204, 204));
    geneTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    this.setBackground(new Color(204, 204, 204));
    this.setBorder(titledBorder1);
    this.setLayout(borderLayout1);

    mainPanel.setBackground(new Color(204, 204, 204));
    mainPanel.setLayout(geneCard);
    this.add(mainPanel,  BorderLayout.CENTER);
    mainPanel.add(tablePanel,   "tablePanel");
    tablePanel.add(scroll, null);
    mainPanel.add(noPanel,   "noPanel");
    noPanel.add(noGeneLabel);
    if(genes.size()>0) geneCard.show(mainPanel,"tablePanel");
    else geneCard.show(mainPanel,"noPanel");
  }

  /**
   * sets the genes displayed in the table and repaints the table
   * @param genes vector of genes to be displayed in the table
   */
  public void setGene(Vector genes){
    this.genes=genes;
    geneTable.setGroup(genes);
    geneTable.setColumnsToFit();
    geneTable.repaint();
    if(genes.size()>0) geneCard.show(mainPanel,"tablePanel");
    else geneCard.show(mainPanel,"noPanel");
  }

  /**
   * returns a vector of all the genes displayed in the table
   * @return vector of all the genes displayed in the table
   */
  public Vector getGenes(){
    return genes;
  }
}