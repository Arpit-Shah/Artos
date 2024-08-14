/*******************************************************************************
 * Copyright (C) 2018-2019 Arpit Shah and Artos Contributors
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.artos.framework.infra;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.artos.exception.InvalidDataException;
import com.artos.framework.FWStaticStore;
import com.artos.framework.parser.TestScriptParser;
import com.artos.framework.parser.TestSuite;
import com.artos.interfaces.TestRunnable;

/**
 * UI Test Selector for testcases
 * 
 * @author ArpitShah
 *
 */
public class GUITestSelector {

	private TestContext context;
	private JFrame container;
	private TestRunnerDataModel testRunnerDataModel;
	private JTextField loopCountField;
	private TestRunnable testRunner;
	private ArrayList<TestObjectWrapper> selectedTests;
	private JTextField searchField; // Search bar text field

	/**
	 * TestRunnerGui constructor
	 * 
	 * @param context    TestContext
	 * @param testList   List of Tests defined in Main class
	 * @param testRunner A TestRunner implementation that will execute the tests
	 * @throws Exception if gui could not launch
	 */
	public GUITestSelector(TestContext context, List<TestObjectWrapper> testList, TestRunnable testRunner)
			throws Exception {
		// UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		this.context = context;
		testRunnerDataModel = new TestRunnerDataModel(context, testList);
		this.testRunner = testRunner;
		selectedTests = new ArrayList<TestObjectWrapper>();

		// If more than one test cases to select then only show GUI otherwise
		// just run the suit

		if (testList.size() > 1) {
			String packageName = "ProjectRoot";
			// get the package name from testList
			if (null != testList.get(0).getTestClassObject().getPackage()) {
				String fullPackageName = testList.get(0).getTestClassObject().getPackage().toString();
				int last = fullPackageName.lastIndexOf(".") + 1;
				packageName = fullPackageName.substring(last);
			}

			initMainFrame(packageName);
			initMainViewComponents();

			try {
				URL pix64 = getClass().getResource("/com/artos/icons/artos_icon64x64.png");
				URL pix32 = getClass().getResource("/com/artos/icons/artos_icon32x32.png");
				URL pix16 = getClass().getResource("/com/artos/icons/artos_icon16x16.png");

				BufferedImage img64x64 = ImageIO.read(pix64);
				BufferedImage img32x32 = ImageIO.read(pix32);
				BufferedImage img16x16 = ImageIO.read(pix16);
				List<BufferedImage> imgList = new ArrayList<>();
				imgList.add(img64x64);
				imgList.add(img32x32);
				imgList.add(img16x16);
				container.setIconImages(imgList);
			} catch (Exception IllegalArgumentException) {
				System.err.println("Icons can not be found");
			}
			container.setAlwaysOnTop(true);
			container.setVisible(true);
		} else {
			try {
				testRunner.executeTest(context, testRunnerDataModel.getTestList());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initialise the main container
	 * 
	 * @param packageName The package that TestRunnerHelper will run
	 */
	private void initMainFrame(String packageName) {
		if (null != context.getTestSuite()) {
			container = new JFrame("Test Selector" + " (" + context.getTestSuite().getSuiteName() + ")");
		}
		container.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// This is to ensure that thread lock is released and framework naturally exits
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				if (null != context.getTestSuite()) {
					System.out.println("User Closed GUI Test Selector Window " + context.getTestSuite().getSuiteName());
				}
				// to release a thread lock
				context.getThreadLatch().countDown();
			}
		});

		container.setSize(new Dimension(500, 600));
		container.setResizable(false);
		container.setLocation(new Point(100, 50));
	}

	/**
	 * Initialise all the components that will be placed in the main container
	 * (including listeners)
	 */
	private void initMainViewComponents() {
		// Create search bar

		searchField = new JTextField(20) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				if (getText().isEmpty()) {
					Graphics2D g2d = (Graphics2D) g.create();
					g2d.setColor(Color.GRAY);
					g2d.drawString("Search...", 5, g.getFontMetrics().getAscent() + 5);
					g2d.dispose();
				}
			}
		};
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {
				filterTable();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				filterTable();
			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				filterTable();
			}
		});

		// to execute all tests
		JButton execAll = new JButton("Execute all");
		execAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				// Tester.run would exit after running the test, so there's no
				// point in keeping the dialog open
				container.dispose();
				execTest(false);
			}
		});

		// to execute tests selected in the table view
		JButton execSelected = new JButton("Execute selected");
		execSelected.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				// if nothing is selected, don't do anything
				if (selectedTests.size() > 0) {
					// see comment ^ in execAll
					container.dispose();
					execTest(true);
				}
			}
		});

		// loop count panel
		JLabel loopLabel = new JLabel("Loop count:");
		loopCountField = new JTextField(Integer.toString(context.getTestSuite().getLoopCount()), 5);

		JPanel loopPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		loopPanel.add(loopLabel);
		loopPanel.add(loopCountField);

		// table view that displays all tests
		JTable testTableView = new JTable(testRunnerDataModel);
		testTableView.setFillsViewportHeight(true);
		// define how table view will handle selections
		testTableView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		ListSelectionModel selectionModel = testTableView.getSelectionModel();
		selectionModel.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting()) {
					// changes are still being made, don't do anything yet
					return;
				}
				ListSelectionModel sm = (ListSelectionModel) e.getSource();
				if (!sm.isSelectionEmpty()) {
					selectedTests.clear();
					int min = sm.getMinSelectionIndex();
					int max = sm.getMaxSelectionIndex();
					for (int sel = min; sel <= max; ++sel) {
						if (sm.isSelectedIndex(sel)) {
							selectedTests.add(testRunnerDataModel.getTestAt(sel));
						}
					}
				}
			}
		});
		setTableStyle(testTableView);

		// so we can scroll the table view if there are a lot of tests
		JScrollPane scrollPane = new JScrollPane(testTableView);

		// Use GridBagLayout for more control over component placement
		GridBagLayout layout = new GridBagLayout();
		container.setLayout(layout);
		GridBagConstraints gbc = new GridBagConstraints();

		// Add search field
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5); // Padding
		gbc.gridwidth = 4; // Span across all columns
		container.add(searchField, gbc);

		// Add buttons and loop count panel
		gbc.gridy = 1;
		gbc.gridx = 0;
		gbc.gridwidth = 1; // Reset gridwidth to 1
		gbc.fill = GridBagConstraints.NONE;
		container.add(execAll, gbc);

		gbc.gridx = 1;
		container.add(execSelected, gbc);

		gbc.gridx = 2;
		gbc.gridwidth = 1; // Span across columns 2 and 3
		container.add(loopPanel, gbc);

		// Add table
		gbc.gridy = 2;
		gbc.gridx = 0;
		gbc.gridwidth = 4; // Span across all columns
		gbc.weighty = 1.0; // Allow table to expand vertically
		gbc.fill = GridBagConstraints.BOTH; // Allow table to expand both horizontally and vertically
		container.add(scrollPane, gbc);

	}

	/**
	 * Filters the table based on the search field input
	 */
	private void filterTable() {
		String searchText = searchField.getText();
		testRunnerDataModel.filter(searchText);
	}

	/**
	 * Set column widths and text alignment (and other style attributes, if needed)
	 * 
	 * @param testTableView the table to style
	 */
	private void setTableStyle(JTable testTableView) {

		// set column0 text to centre align
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);

		// set column widths, we know we only have 2 columns for now
		TableColumn col = testTableView.getColumnModel().getColumn(0);
		col.setPreferredWidth(20);
		col.setCellRenderer(renderer);

		// set column widths, we know we only have 2 columns for now
		col = testTableView.getColumnModel().getColumn(1);
		col.setPreferredWidth(35);
		col.setCellRenderer(renderer);

		if (FWStaticStore.frameworkConfig.isEnableGUITestSelectorSeqNumber()) {
			// set column widths, we know we only have 2 columns for now
			col = testTableView.getColumnModel().getColumn(2);
			col.setPreferredWidth(45);
			col.setCellRenderer(renderer);

			col = testTableView.getColumnModel().getColumn(3);
			col.setPreferredWidth(410);
		} else {
			col = testTableView.getColumnModel().getColumn(2);
			col.setPreferredWidth(465);
		}
	}

	/**
	 * Call the delegate to execute the test
	 * 
	 * @param selectedOnly True if we want to run the selected tests only
	 */
	private void execTest(final boolean selectedOnly) {
		// Override loop count using what is passed by GUI
		// fail silently if loopCountField value is not a valid integer
		try {
			context.getTestSuite().setLoopCount(Integer.valueOf(loopCountField.getText()));
		} catch (Exception e) {
			// use default value
		}

		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					if (selectedOnly) {
						testRunner.executeTest(context, selectedTests);
					} else {
						testRunner.executeTest(context, testRunnerDataModel.getTestList());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

@SuppressWarnings("serial")
class TestRunnerDataModel extends AbstractTableModel {
	private List<TestObjectWrapper> originalTestList;
	private List<TestObjectWrapper> filteredTestList;
	private String[] columnNames;
	private String[][] displayData;
	private TestContext context;

	public TestRunnerDataModel(TestContext context, List<TestObjectWrapper> testList) {
		this.originalTestList = new ArrayList<>(testList);
		this.filteredTestList = new ArrayList<>(testList);
		this.context = context;
		initialiseColumnNames();
		populateDisplayData();
	}

	private void initialiseColumnNames() {
		// Enable if package name column is required
		if (FWStaticStore.frameworkConfig.isEnableGUITestSelectorSeqNumber()) {
			columnNames = new String[] { "F", " # ", "Seq", "Test Name" };
		} else {
			columnNames = new String[] { "F", " # ", "Test Name" };
		}
	}

	/**
	 * A 2d array to be used as display data
	 */
	private void populateDisplayData() {

		List<String> failedTestCaseFQCNList = new ArrayList<>();
		if (FWStaticStore.frameworkConfig.isGenerateTestScript()) {

			String packageName;

			// Package name will be null if Runner is in root package
			packageName = (null == context.getPrePostRunnableObj().getPackage() ? "ProjectRoot"
					: context.getPrePostRunnableObj().getPackage().getName());

			// Read fail test script
			try {
				File failTestScript = new File(FWStaticStore.TESTSCRIPT_BASE_DIR + "FAIL_" + packageName + ".xml");
				if (failTestScript.exists() && failTestScript.isFile()) {
					List<TestSuite> testSuiteList = new TestScriptParser().readTestScript(failTestScript);
					failedTestCaseFQCNList = testSuiteList.get(0).getTestFQCNList();
				}
			} catch (ParserConfigurationException | SAXException | IOException | InvalidDataException e) {
				e.printStackTrace();
			}
		}

		displayData = new String[filteredTestList.size()][columnNames.length];

		for (int index = 0; index < filteredTestList.size(); ++index) {
			// get only the actual test name
			String fullTestName = filteredTestList.get(index).getTestClassObject().getName();
			int last = fullTestName.lastIndexOf(".") + 1;
			String testName = fullTestName.substring(last);

			if (failedTestCaseFQCNList.contains(fullTestName)) {
				// column0 - Previous Failure Highlight
				displayData[index][0] = String.valueOf("F");
			}

			// column1 - test number
			displayData[index][1] = String.valueOf(index);

			if (FWStaticStore.frameworkConfig.isEnableGUITestSelectorSeqNumber()) {
				// column2 - test seq
				displayData[index][2] = Integer.toString(filteredTestList.get(index).getTestsequence());
				// column3 - test name
				displayData[index][3] = testName;
			} else {
				// column2 - test name
				displayData[index][2] = testName;
			}
		}
	}

	public void filter(String query) {
		filteredTestList.clear();
		if (query.isEmpty()) {
			filteredTestList.addAll(originalTestList);
		} else {
			String lowerCaseQuery = query.toLowerCase();
			for (TestObjectWrapper test : originalTestList) {
				String testName = test.getTestClassObject().getName().toLowerCase();
				if (testName.contains(lowerCaseQuery)) {
					filteredTestList.add(test);
				}
			}
		}
		populateDisplayData();
		fireTableDataChanged();
	}

	@Override
	public int getRowCount() {
		return filteredTestList.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return columnNames[columnIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return displayData[rowIndex][columnIndex];
	}

	public List<TestObjectWrapper> getTestList() {
		return filteredTestList;
	}

	public TestObjectWrapper getTestAt(int rowIndex) {
		return filteredTestList.get(rowIndex);
	}
}
