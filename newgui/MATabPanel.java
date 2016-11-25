package newgui;

import ij.io.Opener;
import magictool.image.Grid;
import magictool.image.GridManager;
import magictool.image.ImageDisplayPanel;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.ArrayList;

class MATabPanel extends JPanel {
    public MATabPanel(String greenPath, String redPath) {
        setup(greenPath, redPath);
    }

    private JScrollPane gridScrollPane;
    private JPanel gridScrollPanePanel;
    private ArrayList<MAGridPanel> gridPanelsList = new ArrayList<>();
    private GridManager manager = new GridManager();
    private Border blackline = BorderFactory.createLineBorder(Color.black);
    private JTextField textField_1;
    private JTextField textField_2;
    private JTextField textField_3;
    private JTextField textField_4;
    private JTextField textField_5;
    private ButtonGroup group1;
    private ButtonGroup group2;
    private ImageDisplayPanel imageDisplayPanel;

    // TODO remove
    private int NUMBER_OF_GRIDS = 20;

    private void setup(String greenPath, String redPath) {
        this.setBorder(blackline);
        GridBagLayout gbl_panel = new GridBagLayout();
        gbl_panel.columnWidths = new int[] { 550, 325, 325 };
        gbl_panel.rowHeights = new int[] { 5, 290, 290, 290, 35 };
        gbl_panel.columnWeights = new double[] { 1.0 };
        gbl_panel.rowWeights = new double[] { Double.MIN_VALUE };
        this.setLayout(gbl_panel);

        imageDisplayPanel = new ImageDisplayPanel(buildImage(greenPath, redPath), manager);
        imageDisplayPanel.getCanvas().addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                coordinateFound(xCoordinate(e.getX()), yCoordinate(e.getY()));
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });
        JScrollPane scroll = new JScrollPane(imageDisplayPanel);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        GridBagConstraints gbc_scrollField = new GridBagConstraints();
        gbc_scrollField.insets = new Insets(5, 5, 5, 5);
        gbc_scrollField.fill = GridBagConstraints.BOTH;
        gbc_scrollField.gridx = 0;
        gbc_scrollField.gridy = 0;
        gbc_scrollField.gridheight = 4;
        gbc_scrollField.weightx = 1.0;
        gbc_scrollField.weighty = 1.0;
        gbc_scrollField.insets = new Insets(5, 5, 0, 0); // top, left,
        // bottom,
        // right
        this.add(scroll, gbc_scrollField);
        // textField.setColumns(10);

        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);
        GridBagConstraints gbc_slider = new GridBagConstraints();
        gbc_slider.fill = GridBagConstraints.HORIZONTAL;
        gbc_slider.insets = new Insets(0, 0, 0, 5);
        gbc_slider.gridx = 0;
        gbc_slider.gridy = 4;
        this.add(slider, gbc_slider);

        JLabel lblZoomLevel = new JLabel("Zoom Level");
        GridBagConstraints gbc_lblZoomLevel = new GridBagConstraints();
        gbc_lblZoomLevel.anchor = GridBagConstraints.WEST;
        gbc_lblZoomLevel.insets = new Insets(0, 0, 0, 5);
        gbc_lblZoomLevel.gridx = 1;
        gbc_lblZoomLevel.gridy = 4;
        this.add(lblZoomLevel, gbc_lblZoomLevel);

        // Gridding panel starts here
        JPanel gridding = new JPanel();
        TitledBorder grid_title = BorderFactory.createTitledBorder(blackline, "Gridding");
        grid_title.setTitleJustification(TitledBorder.LEFT);
        gridding.setBorder(grid_title);
        gridding.setLayout(null);
        GridBagConstraints gbc_gridding = new GridBagConstraints();
        gbc_gridding.insets = new Insets(0, 5, 5, 0);
        gbc_gridding.fill = GridBagConstraints.BOTH;
        gbc_gridding.gridx = 1;
        gbc_gridding.gridy = 1;
        gbc_gridding.gridheight = 1;
        gbc_gridding.gridwidth = 2;
        gbc_gridding.weightx = 1.0;
        gbc_gridding.weighty = 1.0;
        gbc_gridding.insets = new Insets(0, 5, 5, 5); // top, left, bottom,
        // right
        this.add(gridding, gbc_gridding);

        JLabel lblNewLabel = new JLabel("Select a previously saved grid or click Add to create a new one.");
        lblNewLabel.setBounds(10, 20, 360, 14);
        gridding.add(lblNewLabel);

        String[] grids = { "Previously saved grid." };
        JComboBox<String> comboBox = new JComboBox<String>(grids);
        comboBox.setBounds(10, 45, 150, 20);
        gridding.add(comboBox);

        JButton btnAdd = new JButton("Add");
        btnAdd.addActionListener(addBut -> {

            // TODO Add button code goes here.

        });
        btnAdd.setBounds(169, 45, 60, 23);
        gridding.add(btnAdd);

        JButton btnModify = new JButton("Modify");
        btnModify.addActionListener(modBut -> {

            // TODO Modify button code goes here.

        });
        btnModify.setBounds(240, 45, 75, 23);
        gridding.add(btnModify);

        JButton btnDelete = new JButton("Delete");
        btnDelete.addActionListener(deleteBut -> {

            // TODO Delete button code goes here.

        });
        btnDelete.setBounds(324, 45, 70, 23);
        gridding.add(btnDelete);

        gridScrollPanePanel = new JPanel();
        gridScrollPane = new JScrollPane(gridScrollPanePanel);
        gridScrollPane.setBounds(10, 70, 600, 150);
        gridding.add(gridScrollPane);

        // TODO remove this test code
        manager.setGridNum(NUMBER_OF_GRIDS);
        for (int i = 0; i < NUMBER_OF_GRIDS; i++) {
            MAGridPanel gp = new MAGridPanel(i + 1, this);
            gridPanelsList.add(gp);
            gridScrollPanePanel.add(gp);
        }

        // Segmentation panel starts here

        JPanel segment = new JPanel();
        TitledBorder seg_title = BorderFactory.createTitledBorder(blackline, "Segmentation");
        seg_title.setTitleJustification(TitledBorder.LEFT);
        segment.setBorder(seg_title);
        segment.setLayout(null);
        GridBagConstraints gbc_segment = new GridBagConstraints();
        gbc_segment.insets = new Insets(0, 5, 5, 0);
        gbc_segment.fill = GridBagConstraints.BOTH;
        gbc_segment.gridx = 1;
        gbc_segment.gridy = 2;
        gbc_segment.gridheight = 1;
        gbc_segment.gridwidth = 2;
        gbc_segment.weightx = 1.0;
        gbc_segment.weighty = 1.0;
        gbc_segment.insets = new Insets(0, 5, 5, 5); // top, left, bottom,
        // right
        this.add(segment, gbc_segment);

        JLabel lblNewLabel_1 = new JLabel("Choose one of the following segmentation options:");
        lblNewLabel_1.setBounds(10, 20, 300, 14);
        segment.add(lblNewLabel_1);

        JRadioButton rdbtnNewRadioButton = new JRadioButton("Adaptive Circle");
        rdbtnNewRadioButton.setBounds(310, 16, 110, 23);
        segment.add(rdbtnNewRadioButton);

        JRadioButton rdbtnNewRadioButton_1 = new JRadioButton("Seeded Region Growing");
        rdbtnNewRadioButton_1.setBounds(420, 16, 160, 23);
        segment.add(rdbtnNewRadioButton_1);

        // Button group so only one radio button can be active at one time.

        group1 = new ButtonGroup();
        group1.add(rdbtnNewRadioButton);
        group1.add(rdbtnNewRadioButton_1);

        JLabel lblThreshold = new JLabel("Threshold");
        lblThreshold.setBounds(430, 50, 60, 14);
        segment.add(lblThreshold);

        JLabel lblSpinnerGrid = new JLabel("Grid");
        lblSpinnerGrid.setBounds(430, 110, 40, 14);
        segment.add(lblSpinnerGrid);

        SpinnerModel spinnerModel = new SpinnerNumberModel(10, // initial
                // value
                0, // min
                100, // max
                1);// step

        JSpinner spinner = new JSpinner(spinnerModel);
        spinner.setBounds(480, 110, 40, 20);
        segment.add(spinner);

        JLabel lblSpinnerSpot = new JLabel("Spot");
        lblSpinnerSpot.setBounds(430, 150, 40, 14);
        segment.add(lblSpinnerSpot);

        SpinnerModel spinnerModel_1 = new SpinnerNumberModel(10, // initial
                // value
                0, // min
                100, // max
                1);// step

        JSpinner spinner_1 = new JSpinner(spinnerModel_1);
        spinner_1.setBounds(480, 150, 40, 20);
        segment.add(spinner_1);

        JCheckBox chckbxNewCheckBox = new JCheckBox("Flag spot");
        chckbxNewCheckBox.setBounds(530, 150, 97, 23);
        segment.add(chckbxNewCheckBox);

        JSlider slider_1 = new JSlider(JSlider.HORIZONTAL, 0, 10, 0);
        slider_1.setMajorTickSpacing(10);
        slider_1.setMinorTickSpacing(1);
        slider_1.setPaintTicks(true);
        slider_1.setPaintLabels(true);
        slider_1.setBounds(500, 50, 100, 40);
        segment.add(slider_1);

        textField_1 = new JTextField();
        textField_1.setBounds(10, 45, 200, 200);
        segment.add(textField_1);
        textField_1.setColumns(10);

        textField_2 = new JTextField();
        textField_2.setBounds(215, 45, 200, 200);
        segment.add(textField_2);
        textField_2.setColumns(10);

        JLabel lblGreen = new JLabel("Green");
        lblGreen.setBounds(90, 250, 40, 14);
        segment.add(lblGreen);

        JLabel lblRed = new JLabel("Red");
        lblRed.setBounds(300, 250, 40, 14);
        segment.add(lblRed);

        // Expression panel starts here

        JPanel expression = new JPanel();
        TitledBorder exp_title = BorderFactory.createTitledBorder(blackline, "Gene Expression Ratios");
        exp_title.setTitleJustification(TitledBorder.LEFT);
        expression.setBorder(exp_title);
        expression.setLayout(null);
        GridBagConstraints gbc_expression = new GridBagConstraints();
        gbc_expression.insets = new Insets(0, 5, 5, 0);
        gbc_expression.fill = GridBagConstraints.BOTH;
        gbc_expression.gridx = 1;
        gbc_expression.gridy = 3;
        gbc_expression.gridheight = 1;
        gbc_expression.gridwidth = 2;
        gbc_expression.weightx = 1.0;
        gbc_expression.weighty = 1.0;
        gbc_expression.insets = new Insets(0, 5, 0, 5); // top, left,
        // bottom,
        // right
        this.add(expression, gbc_expression);

        JLabel lblNewLabel_2 = new JLabel("Select the colour representing the control in this microarray:");
        lblNewLabel_2.setBounds(10, 20, 350, 14);
        expression.add(lblNewLabel_2);

        JRadioButton rdbtnGreen = new JRadioButton("Green");
        rdbtnGreen.setBounds(370, 16, 60, 23);
        expression.add(rdbtnGreen);

        JRadioButton rdbtnRed = new JRadioButton("Red");
        rdbtnRed.setBounds(460, 16, 60, 23);
        expression.add(rdbtnRed);

        // Button group so only one radio button can be active at one time.

        group2 = new ButtonGroup();
        group2.add(rdbtnGreen);
        group2.add(rdbtnRed);

        JLabel lblNewLabel_3 = new JLabel("Select a method for calculating the gene expression levels:");
        lblNewLabel_3.setBounds(10, 60, 350, 14);
        expression.add(lblNewLabel_3);

        String[] signal = { "Total signal." };
        JComboBox<String> comboBox_1 = new JComboBox<String>(signal);
        comboBox_1.setBounds(360, 58, 100, 20);
        expression.add(comboBox_1);

        JLabel lblSpinnerGrid_2 = new JLabel("Grid:");
        lblSpinnerGrid_2.setBounds(470, 58, 35, 14);
        expression.add(lblSpinnerGrid_2);

        SpinnerModel spinnerModel_2 = new SpinnerNumberModel(10, // initial
                // value
                0, // min
                100, // max
                1);// step

        JSpinner spinner_2 = new JSpinner(spinnerModel_2);
        spinner_2.setBounds(500, 58, 40, 20);
        expression.add(spinner_2);

        JLabel lblSpinnerSpot_2 = new JLabel("Spot:");
        lblSpinnerSpot_2.setBounds(545, 58, 35, 14);
        expression.add(lblSpinnerSpot_2);

        SpinnerModel spinnerModel_3 = new SpinnerNumberModel(10, // initial
                // value
                0, // min
                100, // max
                1);// step

        JSpinner spinner_3 = new JSpinner(spinnerModel_3);
        spinner_3.setBounds(578, 58, 40, 20);
        expression.add(spinner_3);

        JCheckBox chckbxNewCheckBox_1 = new JCheckBox("Flag spot");
        chckbxNewCheckBox_1.setBounds(560, 150, 97, 23);
        expression.add(chckbxNewCheckBox_1);

        textField_3 = new JTextField();
        textField_3.setBounds(10, 105, 140, 140);
        expression.add(textField_3);
        textField_3.setColumns(10);

        JLabel lblGreen_1 = new JLabel("Green");
        lblGreen_1.setBounds(60, 250, 40, 14);
        expression.add(lblGreen_1);

        textField_4 = new JTextField();
        textField_4.setBounds(215, 105, 140, 140);
        expression.add(textField_4);
        textField_4.setColumns(10);

        JLabel lblRed_1 = new JLabel("Red");
        lblRed_1.setBounds(280, 250, 40, 14);
        expression.add(lblRed_1);

        textField_5 = new JTextField();
        textField_5.setBounds(410, 105, 140, 140);
        expression.add(textField_5);
        textField_5.setColumns(10);

        JLabel lblCombined = new JLabel("Combined");
        lblCombined.setBounds(450, 250, 100, 14);
        expression.add(lblCombined);
    }

    private Image buildImage(String greenPath, String redPath) {
        Opener greenImage = new Opener();
        Opener redImage = new Opener();
        Image green = greenImage.openImage(greenPath).getImage();
        Image red = redImage.openImage(redPath).getImage();

        Dimension redDim = new Dimension(red.getWidth(null), red.getHeight(null));
        Dimension greenDim = new Dimension(green.getWidth(null), green.getHeight(null));
        int w = greenDim.width;
        int h = greenDim.height;

        // Use green as base.
        int[] pixels = new int[w * h];
        int[] redpixels = new int[w * h];

        PixelGrabber pg = new PixelGrabber(green, 0, 0, w, h, pixels, 0, w);
        PixelGrabber redpg = new PixelGrabber(red, 0, 0, w, h, redpixels, 0, w);
        try {
            pg.grabPixels();
            redpg.grabPixels();
        } catch (Exception e) {
            System.out.print("(Error Grabbing Pixels) " + e);
        }

        for (int i = 0; i < pixels.length; i++) {
            int p = pixels[i];
            int redp = redpixels[i];
            int a = (p >> 24) & 0xFF;
            int r = (redp >> 16) & 0xFF;
            int b = 0;
            int g = (p >> 8) & 0xFF;

            pixels[i] = (a << 24 | r << 16 | g << 8 | b);
        }
        return createImage(new MemoryImageSource(w, h, pixels, 0, w));
    }

    protected void drawGrid(int num, int tlX, int tlY, int trX, int trY, int bX, int bY, int row, int col) {
        Grid grid = manager.getGrid(num - 1);
        if (grid == null) {
            grid = new Grid();
        }
        int tY;
        if (tlY > trY) {
            tY = tlY;
        } else {
            tY = trY;
        }
        grid.setTopLeftX(tlX);
        grid.setTopLeftY(tY);
        grid.setTopRightX(trX);
        grid.setTopRightY(tY);
        grid.setBottomLeftX(tlX);
        grid.setBottomLeftY(bY);
        grid.setBottomRightX(trX);
        grid.setBottomRightY(bY);
        grid.setRows(row);
        grid.setColumns(col);
        manager.setGrid(num - 1, grid);

        imageDisplayPanel.repaint();
    }

    private void coordinateFound(int x, int y) {
        for (MAGridPanel gp : gridPanelsList) {
            if (gp.isAwaitingData()) {
                gp.addCoordinates(x, y);
            }
        }
    }

    /**
     * gets the actual x-coordinate on the image based on the screen
     * x-coordinate
     *
     * @param ex
     *            screen x-coordinate
     * @return actual x-coordinate on the image based on the screen
     *         x-coordinate
     */
    public int xCoordinate(int ex) {
        return ((imageDisplayPanel.getCanvas().getSrcRect().x
                + Math.round((float) ((ex) / imageDisplayPanel.getZoom()))));
    }

    /**
     * gets the actual y-coordinate on the image based on the screen
     * y-coordinate
     *
     * @param ey
     *            screen y-coordinate
     * @return actual y-coordinate on the image based on the screen
     *         y-coordinate
     */
    public int yCoordinate(int ey) {
        return ((imageDisplayPanel.getCanvas().getSrcRect().y
                + Math.round((float) ((ey) / imageDisplayPanel.getZoom()))));
    }
}