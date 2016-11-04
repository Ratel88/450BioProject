/*
 *   MAGIC Tool, A microarray image and data analysis program
 *   Copyright (C) 2003-2007  Laurie Heyer
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
 *   Davidson College
 *   PO Box 6959
 *   Davidson, NC 28035-6959
 *   UNITED STATES
 */

package magictool.image;

import java.text.DecimalFormat;

public class SummaryStatsPanel extends javax.swing.JPanel {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = -3738330818811600871L;
	private SegmentPanel parentPanel;
	private SegmentFrame segframe;
    // Variables declaration - do not modify                     
    private javax.swing.JLabel greenBGAvgLabel;
    private javax.swing.JLabel greenBGStDevLabel;
    private javax.swing.JLabel greenFGAvgLabel;
    private javax.swing.JLabel greenFGStDevLabel;
    private javax.swing.JLabel redBGAvgLabel;
    private javax.swing.JLabel redBGStDevLabel;
    private javax.swing.JLabel redFGAvgLabel;
    private javax.swing.JLabel redFGStDevLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration      
	/** Creates new form SummaryStatsPanel */
    public SummaryStatsPanel(SegmentPanel ppanel, SegmentFrame psframe) {
        this.parentPanel = ppanel;
        this.segframe = psframe;
    	initComponents();
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">                          
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        redFGAvgLabel = new javax.swing.JLabel();
        redFGStDevLabel = new javax.swing.JLabel();
        redBGAvgLabel = new javax.swing.JLabel();
        redBGStDevLabel = new javax.swing.JLabel();
        greenFGAvgLabel = new javax.swing.JLabel();
        greenFGStDevLabel = new javax.swing.JLabel();
        greenBGAvgLabel = new javax.swing.JLabel();
        greenBGStDevLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0))));
        jLabel1.setText("Summary Statistics");

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        redFGAvgLabel.setText(" Red FG Average : N/A");

        redFGStDevLabel.setText(" Red FG Std. Dev.: N/A");

        redBGAvgLabel.setText(" Red BG Average: N/A");

        redBGStDevLabel.setText(" Red BG Std. Dev.: N/A");

        greenFGAvgLabel.setText(" Green FG Average: N/A");

        greenFGStDevLabel.setText(" Green FG Std. Dev: N/A");

        greenBGAvgLabel.setText(" Green BG Average: N/A");

        greenBGStDevLabel.setText(" Green BG Std. Dev: N/A");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(redFGAvgLabel)
                    .add(redFGStDevLabel)
                    .add(redBGStDevLabel)
                    .add(greenFGAvgLabel)
                    .add(greenFGStDevLabel)
                    .add(greenBGAvgLabel)
                    .add(greenBGStDevLabel)
                    .add(redBGAvgLabel))
                .addContainerGap(91, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(redFGAvgLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(redFGStDevLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(redBGAvgLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(redBGStDevLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(greenFGAvgLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(greenFGStDevLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(greenBGAvgLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(greenBGStDevLabel))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel1)
                    .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>                        

    public void setStats() {
    	DecimalFormat df = new DecimalFormat("0.0###");
    	if (segframe.allGeneData.valid) {
    		double[] averages = segframe.allGeneData.getAverages();
    		double[] stdevs = segframe.allGeneData.getStDevs();
    		if(parentPanel.ratioMethod==SingleGeneImage.TOTAL_SIGNAL||parentPanel.ratioMethod==SingleGeneImage.TOTAL_SUBTRACT_BG){
    			redFGAvgLabel.setText(" Red FG Average: " + df.format(averages[0]));
    			redBGAvgLabel.setText(" Red BG Average: " + df.format(averages[1]));
    			greenFGAvgLabel.setText(" Green FG Average: " + df.format(averages[2]));
    			greenBGAvgLabel.setText(" Green BG Average: " + df.format(averages[3]));
    			redFGStDevLabel.setText(" Red FG Std. Dev.: " + df.format(stdevs[0]));
    			redBGStDevLabel.setText(" Red BG Std. Dev.: " + df.format(stdevs[1]));
    			greenFGStDevLabel.setText(" Green FG Std. Dev.: " + df.format(stdevs[2]));
    			greenBGStDevLabel.setText(" Green BG Std. Dev.: " + df.format(stdevs[3]));
    		}
    		else {
    			redFGAvgLabel.setText(" Red FG Average: " + df.format(averages[4]));
    			redBGAvgLabel.setText(" Red BG Average: " + df.format(averages[5]));
    			greenFGAvgLabel.setText(" Green FG Average: " + df.format(averages[6]));
    			greenBGAvgLabel.setText(" Green BG Average: " + df.format(averages[7]));
    			redFGStDevLabel.setText(" Red FG Std. Dev.: " + df.format(stdevs[4]));
    			redBGStDevLabel.setText(" Red BG Std. Dev.: " + df.format(stdevs[5]));
    			greenFGStDevLabel.setText(" Green FG Std. Dev.: " + df.format(stdevs[6]));
    			greenBGStDevLabel.setText(" Green BG Std. Dev.: " + df.format(stdevs[7]));
    		}
    	}
    	else {	//invalid gene data
            redFGAvgLabel.setText(" Red FG Average : N/A");
            redFGStDevLabel.setText(" Red FG Std. Dev.: N/A");
            redBGAvgLabel.setText(" Red BG Average: N/A");
            redBGStDevLabel.setText(" Red BG Std. Dev.: N/A");
            greenFGAvgLabel.setText(" Green FG Average: N/A");
            greenFGStDevLabel.setText(" Green FG Std. Dev: N/A");
            greenBGAvgLabel.setText(" Green BG Average: N/A");
            greenBGStDevLabel.setText(" Green BG Std. Dev: N/A");
    	}
    }
    public void clearStats() {
        redFGAvgLabel.setText(" Red FG Average : N/A");
        redFGStDevLabel.setText(" Red FG Std. Dev.: N/A");
        redBGAvgLabel.setText(" Red BG Average: N/A");
        redBGStDevLabel.setText(" Red BG Std. Dev.: N/A");
        greenFGAvgLabel.setText(" Green FG Average: N/A");
        greenFGStDevLabel.setText(" Green FG Std. Dev: N/A");
        greenBGAvgLabel.setText(" Green BG Average: N/A");
        greenBGStDevLabel.setText(" Green BG Std. Dev: N/A");
    }
}
