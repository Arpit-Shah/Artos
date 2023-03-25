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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.artos.interfaces.TestScenarioRunnable;

/**
 * UI test selector for BDD test cases
 * @author ArpitShah
 *
 */
public class BDDGUITestSelector {

	private TestContext context;
	private JFrame container;
	private TestBDDRunnerDataModel testRunnerDataModel;
	private JTextField loopCountField;
	private TestScenarioRunnable testScenarioRunner;
	private ArrayList<BDDScenario> selectedTests;

	/**
	 * TestRunnerGui constructor
	 * 
	 * @param context TestContext
	 * @param testScenarios List of Test scenarios
	 * @param testScenarioRunner A TestRunner implementation that will execute the scenarios
	 * @throws Exception if GUI could not launch
	 */
	public BDDGUITestSelector(TestContext context, List<BDDScenario> testScenarios, TestScenarioRunnable testScenarioRunner) throws Exception {
		// UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		this.context = context;
		testRunnerDataModel = new TestBDDRunnerDataModel(testScenarios);
		this.testScenarioRunner = testScenarioRunner;
		selectedTests = new ArrayList<BDDScenario>();

		// If more than one test cases to select then only show GUI otherwise
		// just run the suit
		if (testScenarios.size() > 1) {

			initMainFrame();
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
				testScenarioRunner.executeTest(context, testScenarios);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initialise the main container
	 * 
	 */
	private void initMainFrame() {
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

		container.setSize(new Dimension(700, 550));
		container.setResizable(false);
		container.setLocation(new Point(100, 50));
	}

	/**
	 * Initialise all the components that will be placed in the main container (including listeners)
	 */
	private void initMainViewComponents() {
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
		testTableView.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JScrollPane scrollPane = new JScrollPane(testTableView);
		Dimension preferredSize = new Dimension(650, 450);
		scrollPane.setPreferredSize(preferredSize);

		// basic layout, no constraints (any suggestions here?)
		FlowLayout layout = new FlowLayout();
		layout.setVgap(10);
		container.setLayout(layout);

		// add all components to the main container
		container.getContentPane().add(execAll);
		container.getContentPane().add(execSelected);
		container.getContentPane().add(loopPanel);
		container.getContentPane().add(scrollPane);

	}

	/**
	 * Set column widths and text alignment (and other style attributes, if needed)
	 * 
	 * @param testTableView the table to style
	 */
	private void setTableStyle(JTable testTableView) {

		// set column widths, we know we only have 2 columns for now
		TableColumn col = testTableView.getColumnModel().getColumn(0);
		col.setPreferredWidth(35);
		// set column0 text to centre align
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		col.setCellRenderer(renderer);

		col = testTableView.getColumnModel().getColumn(1);
		if (testRunnerDataModel.getTestList().size() > 25) {
			col.setPreferredWidth(615 - 20);
		} else {
			col.setPreferredWidth(615 - 2);
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
						testScenarioRunner.executeTest(context, selectedTests);
					} else {
						testScenarioRunner.executeTest(context, testRunnerDataModel.getTestList());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

@SuppressWarnings("serial")
class TestBDDRunnerDataModel extends AbstractTableModel {
	private List<BDDScenario> testScenarioList;
	private String[] columnNames;
	private String[][] displayData;

	public TestBDDRunnerDataModel(List<BDDScenario> testScenarioList) {
		this.testScenarioList = testScenarioList;

		columnNames = new String[] { "#", "Scenario" };
		populateDisplayData();
	}

	/**
	 * A 2d array to be used as display data
	 */
	private void populateDisplayData() {
		displayData = new String[testScenarioList.size()][columnNames.length];

		for (int index = 0; index < testScenarioList.size(); ++index) {
			// get only the actual test name
			String fullTestName = testScenarioList.get(index).getScenarioDescription();
			String testName = fullTestName.trim();

			// column0 - test number
			displayData[index][0] = String.valueOf(index);

			// column1 - test name
			displayData[index][1] = testName;
		}
	}

	@Override
	public int getRowCount() {
		return testScenarioList.size();
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

	public List<BDDScenario> getTestList() {
		return testScenarioList;
	}

	public BDDScenario getTestAt(int rowIndex) {
		return testScenarioList.get(rowIndex);
	}
}
