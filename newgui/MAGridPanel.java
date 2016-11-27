package newgui;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

class MAGridPanel extends JPanel {
    private JButton btnSet;
    private JButton btnAdvanced;
    private JButton btnCancel;
    private JLabel lblName;
    private JLabel lblClickTopLeft;
    private JLabel lblClickTopRight;
    private JLabel lblClickBottom;
    private JLabel lblNumberRows;
    private JLabel lblNumberColumns;
    private JFormattedTextField tfRows;
    private JFormattedTextField tfColumns;

    private int myNumber;
    private int mode = 0;
    private boolean setFlag = false;
    private boolean awaitingData = false;

    private int topLeftX_true;
    private int topLeftY_true;
    private int topLeftX_temp;
    private int topLeftY_temp;
    private int topRightX_true;
    private int topRightY_true;
    private int topRightX_temp;
    private int topRightY_temp;
    private int bottomX_true;
    private int bottomY_true;
    private int bottomX_temp;
    private int bottomY_temp;
    private int rows_true;
    private int rows_temp;
    private int columns_true;
    private int columns_temp;

    private MATabPanel myTabPanel;

    private static int number = 0;

    public MAGridPanel(MATabPanel tab_panel) {
        myNumber = ++number;
        myTabPanel = tab_panel;
        setup();
    }

    private void setup() {
        this.setMinimumSize(new Dimension(40, 300));
        btnSet = new JButton("Set");
        btnSet.addActionListener(setButton -> {
            progressMode();
        });
        btnAdvanced = new JButton("Advanced");
        btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(cancelButton -> {
            cancelSetting();
        });
        lblName = new JLabel("Grid " + myNumber);
        lblClickTopLeft = new JLabel("Click the centre of the top left spot.");
        lblClickTopRight = new JLabel("Click the centre of the top right spot.");
        lblClickBottom = new JLabel("Click the centre of a spot in the bottommost row.");
        lblNumberRows = new JLabel("Enter the number of rows.");
        lblNumberColumns = new JLabel("Enter the number of columns.");
        tfRows = new JFormattedTextField(new NumberFormatter());
        tfRows.setMinimumSize(new Dimension(50, 20));
        tfRows.setPreferredSize(new Dimension(50, 20));
        tfColumns = new JFormattedTextField();
        tfColumns.setMinimumSize(new Dimension(50, 20));
        tfColumns.setPreferredSize(new Dimension(50, 20));

        tfRows.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "rowInput");
        tfRows.getActionMap().put("rowInput", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rows_temp = Integer.parseInt(tfRows.getText());
                progressMode();
            }
        });
        tfColumns.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "columnInput");
        tfColumns.getActionMap().put("columnInput", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                columns_temp = Integer.parseInt(tfColumns.getText());
                progressMode();
            }
        });

        add(lblName);
        add(btnSet);
        this.setVisible(true);
    }

    public boolean isAwaitingData() {
        return awaitingData;
    }

    public void addCoordinates(int x, int y) {
        switch (mode) {
            case 1:
                topLeftX_temp = x;
                topLeftY_temp = y;
                break;
            case 2:
                topRightX_temp = x;
                topRightY_temp = y;
                break;
            case 3:
                bottomX_temp = x;
                bottomY_temp = y;
                break;
            default:
                break;
        }
        progressMode();
    }

    public void progressMode() {
        mode++;
        removeAll();
        add(lblName);

        switch (mode) {
            case 1:
                add(btnCancel);
                add(lblClickTopLeft);
                awaitingData = true;
                break;
            case 2:
                add(btnCancel);
                add(lblClickTopRight);
                break;
            case 3:
                add(btnCancel);
                add(lblClickBottom);
                break;
            case 4:
                add(btnCancel);
                add(tfRows);
                add(lblNumberRows);
                break;
            case 5:
                add(btnCancel);
                add(tfColumns);
                add(lblNumberColumns);
                awaitingData = false;
                break;
            case 6:
                add(btnSet);
                add(btnAdvanced);
                setFlag = true;
                commitValues();
                break;
            case 7:
                mode = 0;
                progressMode();
                break;
            default:
                mode = 0;
                progressMode();
                break;
        }
        updateUI();
    }

    private void cancelSetting() {
        mode = 0;
        removeAll();
        add(lblName);
        add(btnSet);
        awaitingData = false;
        if (setFlag) {
            add(btnAdvanced);
        }
    }

    private void commitValues() {
        topLeftX_true = topLeftX_temp;
        topLeftY_true = topLeftY_temp;
        topRightX_true = topRightX_temp;
        topRightY_true = topRightY_temp;
        bottomX_true = bottomX_temp;
        bottomY_true = bottomY_temp;
        rows_true = rows_temp;
        columns_true = columns_temp;
        myTabPanel.drawGrid(myNumber, topLeftX_true, topLeftY_true, topRightX_true, topRightY_true, bottomX_true,
                bottomY_true, rows_true, columns_true);
    }

    public static void removedOne()
    {
        --number;
    }
}