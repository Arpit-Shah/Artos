/*******************************************************************************
 * Copyright (C) 2018 Arpit Shah
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
package com.artos.framework;

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

import com.artos.framework.infra.TestContext;
import com.artos.interfaces.TestRunnable;

public class GUITestSelector {

	private TestContext context;
	private JFrame container;
	private TestRunnerDataModel testRunnerDataModel;
	private JTextField loopCountField;
	private TestRunnable testRunner;
	private ArrayList<TestObjectWrapper> selectedTests;
	// Enables if package name column appears in selector
	boolean enablePackgeName = false;

	/**
	 * TestRunnerGui constructor
	 * 
	 * @param context TestContext
	 * @param testList List of Tests defined in Main class
	 * @param testRunner A TestRunner implementation that will execute the tests
	 * @throws Exception if gui could not launch
	 */
	public GUITestSelector(TestContext context, List<TestObjectWrapper> testList, TestRunnable testRunner) throws Exception {
		// UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		this.context = context;
		testRunnerDataModel = new TestRunnerDataModel(testList, enablePackgeName);
		this.testRunner = testRunner;
		selectedTests = new ArrayList<TestObjectWrapper>();

		// If more than one test cases to select then only show GUI otherwise
		// just run the suit
		String packageName = "Default";
		if (testList.size() > 1) {
			// get the package name from testList
			String fullPackageName = testList.get(0).getTestClassObject().getPackage().toString();
			int last = fullPackageName.lastIndexOf(".") + 1;
			packageName = fullPackageName.substring(last);

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
		container = new JFrame("Test Selector"/* - packageName : " + packageName */);
		container.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// This is to ensure that thread lock is released and framework naturally exits
		container.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				System.out.println("User Closed GUI Test Selector Window");
				// to release a thread lock
				context.getThreadLatch().countDown();
			}
		});

		container.setSize(new Dimension(500, 550));
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
		loopCountField = new JTextField(Integer.toString(context.getTotalLoopCount()), 5);
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

		// Enable package name column if required
		if (enablePackgeName) {
			// set column widths, we know we only have 2 columns for now
			TableColumn col = testTableView.getColumnModel().getColumn(0);
			col.setPreferredWidth(35);
			// set column0 text to center align
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(JLabel.CENTER);
			col.setCellRenderer(renderer);

			col = testTableView.getColumnModel().getColumn(1);
			col.setPreferredWidth(280);

			col = testTableView.getColumnModel().getColumn(2);
			col.setPreferredWidth(200);
		} else {
			// set column widths, we know we only have 2 columns for now
			TableColumn col = testTableView.getColumnModel().getColumn(0);
			col.setPreferredWidth(55);
			// set column0 text to center align
			DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(JLabel.CENTER);
			col.setCellRenderer(renderer);

			col = testTableView.getColumnModel().getColumn(1);
			col.setPreferredWidth(445);
		}
	}

	/**
	 * Call the delegate to execute the test
	 * 
	 * @param selectedOnly True if we want to run the selected tests only
	 */
	private void execTest(final boolean selectedOnly) {
		// fail silently if loopCountField value is not a valid integer
		try {
			context.setTotalLoopCount(Integer.valueOf(loopCountField.getText()));
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
	private List<TestObjectWrapper> testList;
	private String[] columnNames;
	private String[][] displayData;
	boolean enablePackgeName = false;

	public TestRunnerDataModel(List<TestObjectWrapper> testList, boolean enablePackgeName) {
		this.testList = testList;
		this.enablePackgeName = enablePackgeName;

		// Enable if package name column is required
		if (enablePackgeName) {
			columnNames = new String[] { "#", "Test Name", "Package" };
		} else {
			columnNames = new String[] { "#", "Test Name" };
		}
		populateDisplayData();
	}

	/**
	 * A 2d array to be used as display data
	 */
	private void populateDisplayData() {
		displayData = new String[testList.size()][columnNames.length];

		for (int index = 0; index < testList.size(); ++index) {
			// get only the actual test name
			String fullTestName = testList.get(index).getTestClassObject().getName();
			int last = fullTestName.lastIndexOf(".") + 1;
			String testName = fullTestName.substring(last);

			// column0 - test number
			displayData[index][0] = String.valueOf(index);
			// column1 - test name
			displayData[index][1] = testName;

			// Enable package name column if required
			if (enablePackgeName) {
				// column2 - package name
				if (null == testList.get(index).getTestClassObject().getPackage()) {
					displayData[index][2] = "";
				} else {
					displayData[index][2] = testList.get(index).getTestClassObject().getPackage().getName();
				}
			}
		}
	}

	@Override
	public int getRowCount() {
		return testList.size();
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
		return testList;
	}

	public TestObjectWrapper getTestAt(int rowIndex) {
		return testList.get(rowIndex);
	}
}
