import ij.io.Opener;
import magictool.image.Grid;
import magictool.image.GridManager;
import magictool.image.ImageDisplayPanel;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import MicroArray.ExportGene;

public class MicroArray extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private ImageDisplayPanel imageDisplayPanel;
	private Border blackline = BorderFactory.createLineBorder(Color.black);
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	private JTextField textField_5;
	private ButtonGroup group1;
	private ButtonGroup group2;

	private JProgressBar exportBar;
	private JProgressBar importBar;
	private JProgressBar openBar;
	private JProgressBar saveAsBar;

	private String saveFileNamePath;

	String[] dataArray;

	private ArrayList<MATabPanel> panelArrayList;
	private JTabbedPane tabbedPane;
	private int counterSample = 1;

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

					openBar = new JProgressBar();
					openBar.setVisible(true);
					openBar.setIndeterminate(true);

					class OpenProject extends SwingWorker<Void, Void> {

						JFileChooser chooser = new JFileChooser();
						FileNameExtensionFilter filter;

						@Override
						public Void doInBackground() throws Exception {

							try {

								filter = new FileNameExtensionFilter("Text file", "txt");
								chooser.setFileFilter(filter);

								chooser.showSaveDialog(null);
								File f = chooser.getSelectedFile();
								String openFileNamePath = f.getAbsolutePath();

								BufferedReader readData = null;
								List<String> lines = new ArrayList<String>();
								String entry;

								try {

									readData = new BufferedReader(new FileReader(openFileNamePath));

									while ((entry = readData.readLine()) != null) {

										lines.add(entry);

									}

								} catch (FileNotFoundException e) {

									System.out.println("File not found");

								} finally {

									dataArray = lines.toArray(new String[0]);

									readData.close();
									// testData();

								}

							} catch (NullPointerException ex) {

								JOptionPane.showMessageDialog(null, "No file selected.", "File warning",
										JOptionPane.WARNING_MESSAGE);

							}

							return null;
						}

						@Override
						public void done() {

							openBar.setIndeterminate(false);
							JOptionPane.showMessageDialog(null, "Project Open complete.");

						}

					}

					new OpenProject().execute();

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
					saveAsBar = new JProgressBar();
					saveAsBar.setVisible(true);
					saveAsBar.setIndeterminate(true);

					class SaveAs extends SwingWorker<Void, Void> {

						JFileChooser chooser = new JFileChooser();
						FileNameExtensionFilter filter;

						@Override
						public Void doInBackground() throws Exception {

							try {

								filter = new FileNameExtensionFilter("Text file", "txt");
								chooser.setFileFilter(filter);

								chooser.showSaveDialog(null);
								File f = chooser.getSelectedFile();
								String saveAsFileNamePath = f.getAbsolutePath();

								FileWriter writeCSV = new FileWriter(saveAsFileNamePath);

								try {

									for (int i = 0; i < dataArray.length; i++) {

										writeCSV.write(dataArray[i] + "\n");

									}

								} catch (IOException e) {

									e.printStackTrace();

								}

								try {

									writeCSV.flush();
									writeCSV.close();

								} catch (IOException e) {

									e.printStackTrace();
								}

							} catch (NullPointerException | IOException ex) {

								JOptionPane.showMessageDialog(null, "No file selected.", "File warning",
										JOptionPane.WARNING_MESSAGE);

							}

							return null;
						}

						@Override
						public void done() {

							saveAsBar.setIndeterminate(false);
							JOptionPane.showMessageDialog(null, "Save Complete.");

						}

					}

					new SaveAs().execute();

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

					importBar = new JProgressBar();
					importBar.setVisible(true);
					importBar.setIndeterminate(true);

					class ImportGene extends SwingWorker<Void, Void> {

						JFileChooser chooser = new JFileChooser();
						FileNameExtensionFilter filter;

						@Override
						public Void doInBackground() throws Exception {

							try {

								filter = new FileNameExtensionFilter("Text file", "txt");
								chooser.setFileFilter(filter);

								chooser.showSaveDialog(null);
								File f = chooser.getSelectedFile();
								String importFileNamePath = f.getAbsolutePath();

								BufferedReader readData = null;
								List<String> lines = new ArrayList<String>();
								String entry;

								try {

									readData = new BufferedReader(new FileReader(importFileNamePath));

									while ((entry = readData.readLine()) != null) {

										lines.add(entry);

									}

								} catch (FileNotFoundException e) {

									System.out.println("File not found");

								} finally {

									dataArray = lines.toArray(new String[0]);

									readData.close();
									// testData();

								}

							} catch (NullPointerException ex) {

								JOptionPane.showMessageDialog(null, "No file selected.", "File warning",
										JOptionPane.WARNING_MESSAGE);

							}

							return null;
						}

						@Override
						public void done() {

							importBar.setIndeterminate(false);
							JOptionPane.showMessageDialog(null, "Import Complete.");

						}

					}

					new ImportGene().execute();

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
					exportBar = new JProgressBar();
					exportBar.setVisible(true);
					exportBar.setIndeterminate(true);

					class ExportGene extends SwingWorker<Void, Void> {

						JFileChooser chooser = new JFileChooser();
						FileNameExtensionFilter filter;

						@Override
						public Void doInBackground() throws Exception {

							try {

								filter = new FileNameExtensionFilter("Text file", "txt");
								chooser.setFileFilter(filter);

								chooser.showSaveDialog(null);
								File f = chooser.getSelectedFile();
								String exportFileNamePath = f.getAbsolutePath();

								FileWriter writeCSV = new FileWriter(exportFileNamePath);

								try {

									for (int i = 0; i < dataArray.length; i++) {

										writeCSV.write(dataArray[i] + "\n");

									}

								} catch (IOException e) {

									e.printStackTrace();

								}

								try {

									writeCSV.flush();
									writeCSV.close();

								} catch (IOException e) {

									e.printStackTrace();
								}

							} catch (NullPointerException | IOException ex) {

								JOptionPane.showMessageDialog(null, "No file selected.", "File warning",
										JOptionPane.WARNING_MESSAGE);

							}

							return null;
						}

						@Override
						public void done() {

							exportBar.setIndeterminate(false);
							JOptionPane.showMessageDialog(null, "Export Complete.");

						}

					}

					new ExportGene().execute();

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

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		contentPane.add(tabbedPane, gbc_tabbedPane);

		// initialize tab list
		panelArrayList = new ArrayList<MATabPanel>();
	}

	public class MATabPanel extends JPanel {
		public MATabPanel(String greenPath, String redPath) {
			setup(greenPath, redPath);
		}

		private JScrollPane gridScrollPane;
		private JPanel gridScrollPanePanel;
		private ArrayList<GridPanel> gridPanelsList = new ArrayList<>();
		private GridManager manager = new GridManager();

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
				GridPanel gp = new GridPanel(i + 1);
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

		private void drawGrid(int num, int tlX, int tlY, int trX, int trY, int bX, int bY, int row, int col) {
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
			for (GridPanel gp : gridPanelsList) {
				if (gp.awaitingData) {
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

		private class GridPanel extends JPanel {
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

			public GridPanel(int number) {
				myNumber = number;
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
				drawGrid(myNumber, topLeftX_true, topLeftY_true, topRightX_true, topRightY_true, bottomX_true,
						bottomY_true, rows_true, columns_true);
			}
		}
	}

	// Use to test open and import data integrity.
	private void testData() {

		for (int i = 0; i < dataArray.length; i++) {

			System.out.println(dataArray[i]);
		}

	}

}
