package newgui;

import java.awt.*;
import java.awt.event.*;
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
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MicroArray extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private JProgressBar exportBar;
	private JProgressBar importBar;
	private JProgressBar openBar;
	private JProgressBar saveAsBar;

	private String saveFileNamePath;
	private String openFileNamePath;
	private String saveAsFileNamePath;
	private String importFileNamePath;
	private String exportFileNamePath;

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
						String greenPath = "";
						String redPath = "";
						boolean pass = false;

						@Override
						public Void doInBackground() throws Exception {

							try {
								filter = new FileNameExtensionFilter("TIF file", "tif");
								chooser.setFileFilter(filter);
								File f;
								chooser.setDialogTitle("Load Red Image File...");
								chooser.showOpenDialog(null);
								f = chooser.getSelectedFile();
								importFileNamePath = f.getAbsolutePath();

								chooser.setDialogTitle("Load Green Image File...");
								chooser.showOpenDialog(null);
								f = chooser.getSelectedFile();
								greenPath = f.getAbsolutePath();
								redPath = importFileNamePath;

								pass = true;
								// TODO file importing code goes here.

							} catch (NullPointerException ex) {

								JOptionPane.showMessageDialog(null, "Pair selection canceled.", "File warning",
										JOptionPane.WARNING_MESSAGE);

							}
							if (pass) {
								MATabPanel panel = new MATabPanel(greenPath, redPath);
								tabbedPane.addTab("Sample " + counterSample++, null, panel, null);
								panelArrayList.add(panel);
								tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
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

	// Use to test open and import data integrity.
	private void testData() {

		for (int i = 0; i < dataArray.length; i++) {

			System.out.println(dataArray[i]);
		}

	}

}
