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

package newgui;

import magictool.VerticalLayout;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * GridOptionsDialog is a JDialog which allows user to set various grid properties
 * for gridding a microarray image.
 */
public class MAGridAdvancedDialog extends JDialog{
    private JPanel topLeftPanel = new JPanel();
    private JLabel tlXLabel = new JLabel();
    private JLabel tlYLabel = new JLabel();
    private JTextField tlXField = new JTextField();
    private JTextField tlYField = new JTextField();
    private JPanel topRightPanel = new JPanel();
    private JLabel trXLabel = new JLabel();
    private JLabel trYLabel = new JLabel();
    private JTextField trXField = new JTextField();
    private JTextField trYField = new JTextField();
    private JPanel bottomPanel = new JPanel();
    private JLabel bXLabel = new JLabel();
    private JLabel bYLabel = new JLabel();
    private JTextField bXField = new JTextField();
    private JTextField bYField = new JTextField();
    private JPanel colRowPanel = new JPanel();
    private JLabel colLabel = new JLabel();
    private JLabel rowLabel = new JLabel();
    private JTextField colField = new JTextField();
    private JTextField rowField = new JTextField();
    private TitledBorder titledBorder1;
    private TitledBorder titledBorder2;
    private TitledBorder titledBorder3;
    private VerticalLayout mainLayout = new VerticalLayout();
    private FlowLayout topLeftLayout = new FlowLayout();
    private FlowLayout topRightLayout = new FlowLayout();
    private FlowLayout bottomLayout = new FlowLayout();
    private FlowLayout colRowLayout = new FlowLayout();
    private JPanel confirmPanel = new JPanel();

    private JButton okButton = new JButton();
    private JButton cancelButton = new JButton();

    private boolean ok=false;



    protected int tlX;
    protected int tlY;
    protected int trX;
    protected int trY;
    protected int bX;
    protected int bY;
    protected int columns;
    protected int rows;

    /**
     * Constructs a dialog for users to set grid properties
     * @param owner parent frame
     */
    public MAGridAdvancedDialog(Frame owner) {
        super(owner);
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void jbInit() throws Exception {
        titledBorder1 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(178, 178, 178)),
                "Top Left");
        titledBorder2 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(178, 178, 178)),
                "Top Right");
        titledBorder3 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(178, 178, 178)),
                "Bottom");

        tlXLabel.setText("X:");
        tlYLabel.setText("Y:");
        tlXField.setColumns(10);
        tlYField.setColumns(10);
        trXLabel.setText("X:");
        trYLabel.setText("Y:");
        trXField.setColumns(10);
        trYField.setColumns(10);
        bXLabel.setText("X:");
        bYLabel.setText("Y:");
        bXField.setColumns(10);
        bYField.setColumns(10);
        colLabel.setText("Columns:");
        rowLabel.setText("Rows:");
        colField.setColumns(6);
        rowField.setColumns(6);

        okButton.setNextFocusableComponent(cancelButton);
        okButton.setText("OK");
        this.getRootPane().setDefaultButton(okButton);
        okButton.addActionListener(okPressed-> okButton_actionPerformed());
        cancelButton.setRequestFocusEnabled(false);
        cancelButton.setFocusPainted(false);
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(cancelPressed-> cancelButton_actionPerformed());

        topLeftPanel.setBorder(titledBorder1);
        topLeftPanel.setLayout(topLeftLayout);
        topLeftPanel.add(tlXLabel);
        topLeftPanel.add(tlXField);
        topLeftPanel.add(tlYLabel);
        topLeftPanel.add(tlYField);

        topRightPanel.setBorder(titledBorder2);
        topRightPanel.setLayout(topRightLayout);
        topRightPanel.add(trXLabel);
        topRightPanel.add(trXField);
        topRightPanel.add(trYLabel);
        topRightPanel.add(trYField);

        bottomPanel.setBorder(titledBorder3);
        bottomPanel.setLayout(bottomLayout);
        bottomPanel.add(bXLabel);
        bottomPanel.add(bXField);
        bottomPanel.add(bYLabel);
        bottomPanel.add(bYField);

        colRowPanel.setBorder(BorderFactory.createEtchedBorder());
        colRowPanel.setLayout(colRowLayout);
        colRowPanel.add(colLabel);
        colRowPanel.add(colField);
        colRowPanel.add(rowLabel);
        colRowPanel.add(rowField);

        this.setTitle("Advanced Grid Setup");
        this.getContentPane().setLayout(mainLayout);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screen.width)/2-200,(screen.height)/2-150);

        this.getContentPane().add(topLeftPanel,  BorderLayout.NORTH);
        this.getContentPane().add(topRightPanel,  BorderLayout.CENTER);
        this.getContentPane().add(bottomPanel,  BorderLayout.CENTER);
        this.getContentPane().add(colRowPanel,  BorderLayout.CENTER);
        this.getContentPane().add(confirmPanel, BorderLayout.SOUTH);

        confirmPanel.add(okButton, null);
        confirmPanel.add(cancelButton, null);
        this.getContentPane().add(confirmPanel, BorderLayout.SOUTH);
    }

    public void setOptions(int tlX, int tlY, int trX, int trY, int bX, int bY, int col, int row){
        this.tlX = tlX;
        this.tlY = tlY;
        this.trX = trX;
        this.trY = trY;
        this.bX = bX;
        this.bY = bY;
        this.columns = col;
        this.rows = row;
        tlXField.setText(""+tlX);
        tlYField.setText(""+tlY);
        trXField.setText(""+trX);
        trYField.setText(""+trY);
        bXField.setText(""+bX);
        bYField.setText(""+bY);
        colField.setText(""+col);
        rowField.setText(""+row);
    }

    /**
     * returns whether or not the user has hit the ok button
     * @return whether or not the user has hit the ok button
     */
    public boolean getOK() {
        return ok;
    }

    //handles the ok button and ensures all fields entered correctly
    private void okButton_actionPerformed() {
        try{
            tlX=Integer.parseInt(tlXField.getText().trim());
            tlY=Integer.parseInt(tlYField.getText().trim());
            trX=Integer.parseInt(trXField.getText().trim());
            trY=Integer.parseInt(trYField.getText().trim());
            bX=Integer.parseInt(bXField.getText().trim());
            bY=Integer.parseInt(bYField.getText().trim());
            columns=Integer.parseInt(colField.getText().trim());
            rows=Integer.parseInt(rowField.getText().trim());
            ok = true;
            this.dispose();
        }
        catch(NumberFormatException e1){
            JOptionPane.showMessageDialog(this, "Please Enter Integer Value For Each Field");
        }
    }

    //handles the cancel button
    private void cancelButton_actionPerformed() {
        this.dispose();
    }
}