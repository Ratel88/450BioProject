import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MicroArray extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextField textField;
	private Border blackline = BorderFactory.createLineBorder(Color.black);
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private ButtonGroup group1;
	private ButtonGroup group2;
	private JProgressBar exportProgress;

	private String openFileNamePath;
	private String saveFileNamePath;
	private String saveAsFileNamePath;
	private String importFileNamePath;
	private String exportFileNamePath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MicroArray frame = new MicroArray();
					frame.pack();
					frame.setVisible(true);

					// Causes frame to open in the center of the screen.
					frame.setLocationRelativeTo(null);

					// Sets the frame to maximum size on start
					frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MicroArray() {

		super("Improved Magic Tool");

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1200, 910);

		// MenuBar stuff here

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		menuBar.setBorder(new BevelBorder(BevelBorder.RAISED));

		// File menu

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmNewProject = new JMenuItem("New Project....");
		mntmNewProject.setMnemonic(KeyEvent.VK_N);
		mntmNewProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		mntmNewProject.addActionListener(pickedNew -> {

			// TODO New project code goes here.

		});
		mnFile.add(mntmNewProject);

		JMenuItem mntmOpenProject = new JMenuItem("Open Project....");
		mntmOpenProject.setMnemonic(KeyEvent.VK_O);
		mntmOpenProject.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		mntmOpenProject.addActionListener(pickedOpen -> {

			Thread openThread = new Thread() {

				public void run() {

					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter;

					try {

						filter = new FileNameExtensionFilter("Text file", "txt");
						chooser.setFileFilter(filter);

						chooser.showOpenDialog(null);
						File f = chooser.getSelectedFile();
						openFileNamePath = f.getAbsolutePath();

						// TODO project loading code goes here

					} catch (NullPointerException ex) {

						JOptionPane.showMessageDialog(null, "No file selected.", "File warning",
								JOptionPane.WARNING_MESSAGE);

					}

				}

			};

			openThread.start();

		});
		mnFile.add(mntmOpenProject);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.setMnemonic(KeyEvent.VK_S);
		mntmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mntmSave.addActionListener(pickedSave -> {

			Thread saveTread = new Thread() {

				public void run() {

					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter;

					try {

						filter = new FileNameExtensionFilter("Text file", "txt");
						chooser.setFileFilter(filter);

						chooser.showSaveDialog(null);
						File f = chooser.getSelectedFile();
						saveFileNamePath = f.getAbsolutePath();

						// TODO file save code goes here.

						FileWriter fw = new FileWriter(saveFileNamePath);

					} catch (NullPointerException | IOException ex) {

						JOptionPane.showMessageDialog(null, "No file selected.", "File warning",
								JOptionPane.WARNING_MESSAGE);

					}

				}

			};

			saveTread.start();

		});
		mnFile.add(mntmSave);

		JMenuItem mntmSaveAs = new JMenuItem("Save as....");
		mntmSaveAs.addActionListener(pickedSaveAs -> {

			Thread saveAsThread = new Thread() {

				public void run() {

					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter;

					try {

						filter = new FileNameExtensionFilter("Text file", "txt");
						chooser.setFileFilter(filter);

						chooser.showSaveDialog(null);
						File f = chooser.getSelectedFile();
						saveAsFileNamePath = f.getAbsolutePath();

						// TODO file save code goes here.

						FileWriter fw = new FileWriter(saveAsFileNamePath);

					} catch (NullPointerException | IOException ex) {

						JOptionPane.showMessageDialog(null, "No file selected.", "File warning",
								JOptionPane.WARNING_MESSAGE);

					}

				}

			};

			saveAsThread.start();

		});
		mnFile.add(mntmSaveAs);

		JSeparator separator_1 = new JSeparator();
		mnFile.add(separator_1);

		JMenuItem mntmImportSampleImage = new JMenuItem("Import Sample Image Pair....");
		mntmImportSampleImage.setMnemonic(KeyEvent.VK_I);
		mntmImportSampleImage.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.CTRL_MASK));
		mntmImportSampleImage.addActionListener(pickedImport -> {

			Thread importThread = new Thread() {

				public void run() {

					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter;

					try {

						filter = new FileNameExtensionFilter("Text file", "txt");
						chooser.setFileFilter(filter);

						chooser.showOpenDialog(null);
						File f = chooser.getSelectedFile();
						importFileNamePath = f.getAbsolutePath();

						// TODO file importing code goes here.

					} catch (NullPointerException ex) {

						JOptionPane.showMessageDialog(null, "No file selected.", "File warning",
								JOptionPane.WARNING_MESSAGE);

					}

				}

			};

			importThread.start();

		});
		mnFile.add(mntmImportSampleImage);

		JMenuItem mntmExportGeneExpression = new JMenuItem("Export Gene Expression Data");
		mntmExportGeneExpression.setMnemonic(KeyEvent.VK_E);
		mntmExportGeneExpression.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.CTRL_MASK));
		mntmExportGeneExpression.addActionListener(pickedExportGene -> {

			Thread exportThread = new Thread() {

				public void run() {

					JFileChooser chooser = new JFileChooser();
					FileNameExtensionFilter filter;

					try {

						filter = new FileNameExtensionFilter("Text file", "txt");
						chooser.setFileFilter(filter);

						chooser.showSaveDialog(null);
						File f = chooser.getSelectedFile();
						exportFileNamePath = f.getAbsolutePath();

						// TODO file export code goes here.

						FileWriter fw = new FileWriter(exportFileNamePath);

					} catch (NullPointerException | IOException ex) {

						JOptionPane.showMessageDialog(null, "No file selected.", "File warning",
								JOptionPane.WARNING_MESSAGE);

					}

				}

			};

			exportThread.start();

		});
		mnFile.add(mntmExportGeneExpression);

		JSeparator separator_2 = new JSeparator();
		mnFile.add(separator_2);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.setMnemonic(KeyEvent.VK_Q);
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		mntmExit.addActionListener(pickedExit -> {
			System.exit(0);
		});
		mnFile.add(mntmExit);

		// Edit menu

		JMenu mnEdit = new JMenu("Edit");
		menuBar.add(mnEdit);

		JMenuItem mntmUndo = new JMenuItem("Undo");
		mntmUndo.setMnemonic(KeyEvent.VK_Z);
		mntmUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
		mntmUndo.addActionListener(pickedUndo -> {

			// TODO Undo code goes here.

		});
		mnEdit.add(mntmUndo);

		JMenuItem mntmRedo = new JMenuItem("Redo");
		mntmRedo.setMnemonic(KeyEvent.VK_Y);
		mntmRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK));
		mntmRedo.addActionListener(pickedRedo -> {

			// TODO Redo code goes here.

		});
		mnEdit.add(mntmRedo);

		// Window menu

		JMenu mnWindow = new JMenu("Window");
		menuBar.add(mnWindow);

		JMenuItem mntmMinimize = new JMenuItem("Minimize");
		mntmMinimize.setMnemonic(KeyEvent.VK_M);
		mntmMinimize.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		mntmMinimize.addActionListener(pickedMini -> {

			// TODO Minimize window code goes here.

		});
		mnWindow.add(mntmMinimize);

		JMenuItem mntmZoom = new JMenuItem("Zoom");
		mntmZoom.addActionListener(pickedZoom -> {

			// TODO Zoom code goes here.

		});
		mnWindow.add(mntmZoom);

		// Help menu

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmUserGuide = new JMenuItem("User Guide");
		mntmUserGuide.setMnemonic(KeyEvent.VK_U);
		mntmUserGuide.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, ActionEvent.CTRL_MASK));
		mntmUserGuide.addActionListener(pickedGuide -> {

			// TODO User guide code goes here.

		});
		mnHelp.add(mntmUserGuide);

		JSeparator separator_3 = new JSeparator();
		mnHelp.add(separator_3);

		JMenuItem mntmAbout = new JMenuItem("About");
		mntmAbout.addActionListener(pickedAbout -> {
			JOptionPane.showMessageDialog(null,
					"CPSC 450: Bioinformatics team:\n Lee Callaghan\n Edward Greenop\n Darren Hendrickson\n Lukas Pihl\n",
					"About us", JOptionPane.INFORMATION_MESSAGE);
		});
		mnHelp.add(mntmAbout);

		// Panel stuff starts here
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 0, 0 };
		gbl_contentPane.rowHeights = new int[] { 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		contentPane.add(tabbedPane, gbc_tabbedPane);

		// Sample 1 tab

		JPanel panel = new JPanel();
		panel.setBorder(blackline);
		tabbedPane.addTab("Sample 1", null, panel, null);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] { 550, 325, 325 };
		gbl_panel.rowHeights = new int[] { 5, 290, 290, 290, 35 };
		gbl_panel.columnWeights = new double[] { 1.0 };
		gbl_panel.rowWeights = new double[] { Double.MIN_VALUE };
		panel.setLayout(gbl_panel);

		textField = new JTextField();
		JScrollPane scroll = new JScrollPane(textField);
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
		gbc_scrollField.insets = new Insets(5, 5, 0, 0); // top, left, bottom,
															// right
		panel.add(scroll, gbc_scrollField);
		textField.setColumns(10);

		JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
		slider.setMajorTickSpacing(10);
		slider.setMinorTickSpacing(1);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		GridBagConstraints gbc_slider = new GridBagConstraints();
		gbc_slider.fill = GridBagConstraints.HORIZONTAL;
		gbc_slider.insets = new Insets(0, 0, 0, 5);
		gbc_slider.gridx = 0;
		gbc_slider.gridy = 4;
		panel.add(slider, gbc_slider);

		JLabel lblZoomLevel = new JLabel("Zoom Level");
		GridBagConstraints gbc_lblZoomLevel = new GridBagConstraints();
		gbc_lblZoomLevel.anchor = GridBagConstraints.WEST;
		gbc_lblZoomLevel.insets = new Insets(0, 0, 0, 5);
		gbc_lblZoomLevel.gridx = 1;
		gbc_lblZoomLevel.gridy = 4;
		panel.add(lblZoomLevel, gbc_lblZoomLevel);

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
		panel.add(gridding, gbc_gridding);

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

		JLabel lblGrid = new JLabel("Grid 1");
		lblGrid.setBounds(10, 80, 40, 14);
		gridding.add(lblGrid);

		JLabel lblGrid_1 = new JLabel("Grid 2");
		lblGrid_1.setBounds(10, 110, 40, 14);
		gridding.add(lblGrid_1);

		JLabel lblGrid_2 = new JLabel("Grid 3");
		lblGrid_2.setBounds(10, 140, 40, 14);
		gridding.add(lblGrid_2);

		JLabel lblGrid_3 = new JLabel("Grid 4");
		lblGrid_3.setBounds(10, 170, 40, 14);
		gridding.add(lblGrid_3);

		JLabel lblClickTheCenter = new JLabel("To cancel click the center of the top left spot");
		lblClickTheCenter.setBounds(10, 205, 300, 14);
		gridding.add(lblClickTheCenter);

		JButton btnSet = new JButton("Set");
		btnSet.addActionListener(setBut -> {

			// TODO Set button code goes here.

		});
		btnSet.setBounds(60, 75, 55, 23);
		gridding.add(btnSet);

		JButton btnAdvanced = new JButton("Advanced");
		btnAdvanced.addActionListener(advBut -> {

			// TODO Advanced button code goes here.

		});
		btnAdvanced.setBounds(125, 75, 90, 23);
		gridding.add(btnAdvanced);

		JButton btnSet_1 = new JButton("Set");
		btnSet_1.addActionListener(set1 -> {

			// TODO Set button code goes here.

		});
		btnSet_1.setBounds(60, 105, 55, 23);
		gridding.add(btnSet_1);

		JButton btnAdvanced_1 = new JButton("Advanced");
		btnAdvanced_1.addActionListener(adv -> {

			// TODO Advanced button code goes here.

		});
		btnAdvanced_1.setBounds(125, 105, 90, 23);
		gridding.add(btnAdvanced_1);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(can -> {

			// TODO Cancel button code goes here.

		});
		btnCancel.setBounds(60, 135, 75, 23);
		gridding.add(btnCancel);

		JButton btnSet_2 = new JButton("Set");
		btnSet_2.addActionListener(set2 -> {

			// TODO Set button code goes here.

		});
		btnSet_2.setBounds(60, 165, 55, 23);
		gridding.add(btnSet_2);

		JLabel lblGrid_4 = new JLabel("Grid 5");
		lblGrid_4.setBounds(240, 80, 40, 14);
		gridding.add(lblGrid_4);

		JLabel lblGrid_5 = new JLabel("Grid 6");
		lblGrid_5.setBounds(240, 110, 40, 14);
		gridding.add(lblGrid_5);

		JLabel lblGrid_6 = new JLabel("Grid 7");
		lblGrid_6.setBounds(240, 140, 40, 14);
		gridding.add(lblGrid_6);

		JLabel lblGrid_7 = new JLabel("Grid 8");
		lblGrid_7.setBounds(240, 170, 40, 14);
		gridding.add(lblGrid_7);

		JButton btnSet_3 = new JButton("Set");
		btnSet_3.addActionListener(set3 -> {

			// TODO Set button code goes here.

		});
		btnSet_3.setBounds(290, 75, 55, 23);
		gridding.add(btnSet_3);

		JButton btnSet_4 = new JButton("Set");
		btnSet_4.addActionListener(set4 -> {

			// TODO Set button code goes here.

		});
		btnSet_4.setBounds(290, 105, 55, 23);
		gridding.add(btnSet_4);

		JButton btnSet_5 = new JButton("Set");
		btnSet_5.addActionListener(set5 -> {

			// TODO Set button code goes here.

		});
		btnSet_5.setBounds(290, 135, 55, 23);
		gridding.add(btnSet_5);

		JButton btnSet_6 = new JButton("Set");
		btnSet_6.addActionListener(set6 -> {

			// TODO Set button code goes here.

		});
		btnSet_6.setBounds(290, 165, 55, 23);
		gridding.add(btnSet_6);

		JLabel lblGrid_8 = new JLabel("Grid 9");
		lblGrid_8.setBounds(470, 80, 40, 14);
		gridding.add(lblGrid_8);

		JLabel lblGrid_9 = new JLabel("Grid 10");
		lblGrid_9.setBounds(470, 110, 40, 14);
		gridding.add(lblGrid_9);

		JLabel lblGrid_10 = new JLabel("Grid 11");
		lblGrid_10.setBounds(470, 140, 40, 14);
		gridding.add(lblGrid_10);

		JLabel lblGrid_11 = new JLabel("Grid 12");
		lblGrid_11.setBounds(470, 170, 40, 14);
		gridding.add(lblGrid_11);

		JButton btnSet_7 = new JButton("Set");
		btnSet_7.addActionListener(set7 -> {

			// TODO Set button code goes here.

		});
		btnSet_7.setBounds(520, 75, 55, 23);
		gridding.add(btnSet_7);

		JButton btnSet_8 = new JButton("Set");
		btnSet_8.addActionListener(set8 -> {

			// TODO Set button code goes here.

		});
		btnSet_8.setBounds(520, 105, 55, 23);
		gridding.add(btnSet_8);

		JButton btnSet_9 = new JButton("Set");
		btnSet_9.addActionListener(set9 -> {

			// TODO Set button code goes here.

		});
		btnSet_9.setBounds(520, 135, 55, 23);
		gridding.add(btnSet_9);

		JButton btnSet_10 = new JButton("Set");
		btnSet_10.addActionListener(set10 -> {

			// TODO Set button code goes here.

		});
		btnSet_10.setBounds(520, 165, 55, 23);
		gridding.add(btnSet_10);

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
		gbc_segment.insets = new Insets(0, 5, 5, 5); // top, left, bottom, right
		panel.add(segment, gbc_segment);

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

		SpinnerModel spinnerModel = new SpinnerNumberModel(10, // initial value
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
		gbc_expression.insets = new Insets(0, 5, 0, 5); // top, left, bottom,
														// right
		panel.add(expression, gbc_expression);

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

		// Sample 2 tab

		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Sample 2", null, panel_1, null);

		// TODO Clone Sample 1 and rename everything.

		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Sample 3", null, panel_2, null);

		// TODO Clone Sample 1 and rename everything.

		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Sample 4", null, panel_3, null);

		// TODO Clone Sample 1 and rename everything.

		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Sample 5", null, panel_4, null);

		// TODO Clone Sample 1 and rename everything.

		JPanel panel_5 = new JPanel();
		tabbedPane.addTab("Sample 6", null, panel_5, null);

		// TODO Clone Sample 1 and rename everything.

		JPanel panel_6 = new JPanel();
		tabbedPane.addTab("Sample 7", null, panel_6, null);

		// TODO Clone Sample 1 and rename everything.

		JPanel panel_7 = new JPanel();
		tabbedPane.addTab("Sample 8", null, panel_7, null);

		// TODO Clone Sample 1 and rename everything.

		JPanel panel_8 = new JPanel();
		tabbedPane.addTab("Sample 9", null, panel_8, null);

		// TODO Clone Sample 1 and rename everything.

		JPanel panel_9 = new JPanel();
		tabbedPane.addTab("Sample 10", null, panel_9, null);

		// TODO Clone Sample 1 and rename everything.
	}
}
