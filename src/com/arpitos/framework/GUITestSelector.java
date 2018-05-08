package com.arpitos.framework;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
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
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import com.arpitos.interfaces.TestExecutable;
import com.arpitos.interfaces.TestRunnable;

public class GUITestSelector {

	private JFrame container;
	private Class<?> cls;
	String serialNumber;
	private TestRunnerDataModel testRunnerDataModel;
	private int loopCount;
	private JTextField loopCountField;
	private TestRunnable testRunner;
	private ArrayList<TestExecutable> selectedTests;

	/**
	 * TestRunnerGui constructor
	 * 
	 * @param testList
	 *            List of TestExecutors defined in Main class
	 * @param loopCount
	 *            Number of times each test will execute
	 * @param testRunner
	 *            A TestRunner implementation that will execute the tests
	 * @throws UnsupportedLookAndFeelException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws Exception
	 */
	public GUITestSelector(ArrayList<TestExecutable> testList, Class<?> cls, String serialNumber, int loopCount, TestRunnable testRunner)
			throws Exception {
		// UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		testRunnerDataModel = new TestRunnerDataModel(testList);
		this.cls = cls;
		this.serialNumber = serialNumber;
		this.loopCount = loopCount;
		this.testRunner = testRunner;
		selectedTests = new ArrayList<TestExecutable>();

		// If more than one test cases to select then only show GUI otherwise
		// just run the suit
		String packageName = "Default";
		if (testList.size() > 1) {
			// get the package name from testList
			String fullPackageName = testList.get(0).getClass().getPackage().toString();
			int last = fullPackageName.lastIndexOf(".") + 1;
			packageName = fullPackageName.substring(last);

			initMainFrame(packageName);
			initMainViewComponents();
			// System.out.println(getClass().getResource("../icons/arpitos_icon64x64.png"));
			BufferedImage img64x64 = ImageIO.read(getClass().getResource("../icons/arpitos_icon64x64.png"));
			BufferedImage img32x32 = ImageIO.read(getClass().getResource("../icons/arpitos_icon32x32.png"));
			BufferedImage img16x16 = ImageIO.read(getClass().getResource("../icons/arpitos_icon16x16.png"));
			List<BufferedImage> imgList = new ArrayList<>();
			imgList.add(img64x64);
			imgList.add(img32x32);
			imgList.add(img16x16);
			container.setIconImages(imgList);
			container.setVisible(true);
		} else {
			try {
				testRunner.executeTest(testRunnerDataModel.getTestList(), cls, serialNumber, loopCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * TestRunnerGui constructor This is for CI
	 * 
	 * @param testList
	 *            List of TestExecutors defined in Main class
	 * @param loopCount
	 *            Number of times each test will execute
	 * @param testRunner
	 *            A TestRunner implementation that will execute the tests
	 * @throws IOException
	 */
	public GUITestSelector(ArrayList<TestExecutable> testList, int loopCount, TestRunnable testRunner, boolean forceRunAll) throws IOException {
		testRunnerDataModel = new TestRunnerDataModel(testList);
		this.loopCount = loopCount;
		this.testRunner = testRunner;

		selectedTests = new ArrayList<TestExecutable>();

		String packageName = "Default";
		if (testList.size() > 1 && !forceRunAll) {
			// get the package name from testList
			String fullPackageName = testList.get(0).getClass().getPackage().toString();
			int last = fullPackageName.lastIndexOf(".") + 1;
			packageName = fullPackageName.substring(last);

			initMainFrame(packageName);
			initMainViewComponents();

			Image img = ImageIO.read(getClass().getResource("icons/arpitos_icon64x64.png"));
			container.setIconImage(img);
			container.setVisible(true);
		} else {
			try {
				testRunner.executeTest(testRunnerDataModel.getTestList(), cls, serialNumber, loopCount);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Initialise the main container
	 * 
	 * @param packageName
	 *            The package that TestRunnerHelper will run
	 */
	private void initMainFrame(String packageName) {
		container = new JFrame(
				"Test Selector"/* - packageName : " + packageName */);
		container.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		container.setSize(new Dimension(480, 515));
		container.setResizable(false);
		container.setLocation(new Point(100, 50));
	}

	/**
	 * Initialise all the components that will be placed in the main container
	 * (including listeners)
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
		loopCountField = new JTextField("1", 5);
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
	 * Set column widths and text alignment (and other style attributes, if
	 * needed)
	 * 
	 * @param testTableView
	 *            the table to style
	 */
	private void setTableStyle(JTable testTableView) {
		// set column widths, we know we only have 2 columns for now
		TableColumn col = testTableView.getColumnModel().getColumn(0);
		col.setPreferredWidth(35);
		// set column0 text to center align
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(JLabel.CENTER);
		col.setCellRenderer(renderer);

		col = testTableView.getColumnModel().getColumn(1);
		col.setPreferredWidth(445);
	}

	/**
	 * Call the delegate to execute the test
	 * 
	 * @param selectedOnly
	 *            True if we want to run the selected tests only
	 */
	private void execTest(final boolean selectedOnly) {
		// fail silently if loopCountField value is not a valid integer
		try {
			this.loopCount = Integer.valueOf(loopCountField.getText());
		} catch (Exception e) {
			// use default value
		}

		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(new Runnable() {
			@Override
			public void run() {
				try {
					if (selectedOnly) {
						testRunner.executeTest(selectedTests, cls, serialNumber, loopCount);
					} else {
						testRunner.executeTest(testRunnerDataModel.getTestList(), cls, serialNumber, loopCount);
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
	private ArrayList<TestExecutable> testList;
	private String[] columnNames = { "#", "Test Name" };
	private String[][] displayData;

	public TestRunnerDataModel(ArrayList<TestExecutable> testList) {
		this.testList = testList;
		populateDisplayData();
	}

	/**
	 * A 2d array to be used as display data
	 */
	private void populateDisplayData() {
		displayData = new String[testList.size()][columnNames.length];

		for (int index = 0; index < testList.size(); ++index) {
			// get only the actual test name
			String fullTestName = testList.get(index).getClass().getName();
			int last = fullTestName.lastIndexOf(".") + 1;
			String testName = fullTestName.substring(last);

			// column0 - test number
			displayData[index][0] = String.valueOf(index);
			// column1 - test name
			displayData[index][1] = testName;
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

	public ArrayList<TestExecutable> getTestList() {
		return testList;
	}

	public TestExecutable getTestAt(int rowIndex) {
		return testList.get(rowIndex);
	}
}
