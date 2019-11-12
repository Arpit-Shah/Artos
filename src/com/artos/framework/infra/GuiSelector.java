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

import com.artos.exception.InvalidDataException;
import com.artos.framework.FWStaticStore;
import com.artos.framework.parser.TestScriptParser;
import com.artos.framework.parser.TestSuite;
import com.artos.interfaces.TestRunnable;
import org.xml.sax.SAXException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
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

public class GuiSelector {
    // public fields for unit test;
    public JButton btnExecuteSelected;
    public JTable testTableView;
    public JTextField loopCountField;

    private TestContext context;
    private TestCaseTable testCaseTable;
    private TestRunnable testRunner;
    private ArrayList<TestObjectWrapper> selectedTests;

    /**
     * TestRunnerGui constructor
     *
     * @param context    TestContext
     * @param testList   List of Tests defined in Main class
     * @param testRunner A TestRunner implementation that will execute the tests
     * @throws Exception if gui could not launch
     */
    public GuiSelector(TestContext context, List<TestObjectWrapper> testList, TestRunnable testRunner) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        this.context = context;
        testCaseTable = new TestCaseTable(context, testList);
        this.testRunner = testRunner;
        selectedTests = new ArrayList<>();

        // If more than one test cases to select then only show GUI otherwise
        // just run the suit
        if (testList.size() > 1) {
            JFrame frame = initMainFrame();
            initMainViewComponents(frame);
            setIcon(frame);

            frame.setAlwaysOnTop(true);
            frame.setVisible(true);
        } else {
            try {
                testRunner.executeTest(context, testList);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setIcon(JFrame frame) {
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
            frame.setIconImages(imgList);
        } catch (Exception IllegalArgumentException) {
            System.err.println("Icons can not be found");
        }
    }

    private JFrame initMainFrame() {
        String title = "Test Selector";
        if (null != context.getTestSuite()) {
            title += " (" + context.getTestSuite().getSuiteName() + ")";
        }
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // This is to ensure that thread lock is released and framework naturally exits
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                if (null != context.getTestSuite()) {
                    System.out.println("User Closed GUI Test Selector Window " + context.getTestSuite().getSuiteName());
                }
                // to release a thread lock
                context.getThreadLatch().countDown();
            }
        });

        frame.setSize(new Dimension(500, 550));
        frame.setResizable(true);
        frame.setLocation(new Point(100, 50));
        return frame;
    }

    /**
     * Initialise all the components that will be placed in the main container (including listeners)
     */
    private void initMainViewComponents(JFrame frame) {
        // to execute all tests
        JButton btnExecAll = new JButton("Execute all");
        btnExecAll.setSize(80, 20);
        btnExecAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                // Tester.run would exit after running the test, so there's no
                // point in keeping the dialog open
                frame.dispose();
                execTest(false);
            }
        });

        // to execute tests selected in the table view
        btnExecuteSelected = new JButton("Execute selected");
        btnExecuteSelected.setSize(120, 20);
        btnExecuteSelected.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                ListSelectionModel selection = testTableView.getSelectionModel();
                if (!selection.isSelectionEmpty()) {
                    selectedTests.clear();
                    for (int sel = selection.getMinSelectionIndex();
                         sel <= selection.getMaxSelectionIndex();
                         ++sel) {
                        if (selection.isSelectedIndex(sel)) {
                            selectedTests.add(testCaseTable.getTestAt(sel));
                        }
                    }
                    // see comment ^ in btnExecAll
                    frame.dispose();
                    execTest(true);
                }
            }
        });

        // loop count panel
        JLabel loopLabel = new JLabel("Loop count:");
        loopCountField = new JTextField(Integer.toString(context.getTestSuite().getLoopCount()), 5);

        JPanel loopPanel = new JPanel(new BorderLayout());
        loopPanel.add(loopLabel, BorderLayout.WEST);
        loopPanel.add(loopCountField, BorderLayout.EAST);

        // table view that displays all tests
        testTableView = new JTable(testCaseTable);
        testTableView.setFillsViewportHeight(true);
        // define how table view will handle selections
        testTableView.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        setTableStyle(testTableView);

        // so we can scroll the table view if there are a lot of tests
        JScrollPane scrollPane = new JScrollPane(testTableView);

        // basic layout, no constraints (any suggestions here?)
        BorderLayout layout = new BorderLayout();
        layout.setVgap(15);
        frame.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout());
        topPanel.add(btnExecAll, BorderLayout.WEST);
        topPanel.add(btnExecuteSelected, BorderLayout.WEST);
        topPanel.add(loopPanel, BorderLayout.EAST);
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
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
                        testRunner.executeTest(context, testCaseTable.getTestList());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}

@SuppressWarnings("serial")
class TestCaseTable extends AbstractTableModel {
    private List<TestObjectWrapper> testList;
    private String[] columnNames;
    private String[][] displayData;
    private TestContext context;

    public TestCaseTable(TestContext context, List<TestObjectWrapper> testList) {
        this.testList = testList;
        this.context = context;

        // Enable if package name column is required
        if (FWStaticStore.frameworkConfig.isEnableGUITestSelectorSeqNumber()) {
            columnNames = new String[]{"F", " # ", "Seq", "Test Name"};
        } else {
            columnNames = new String[]{"F", " # ", "Test Name"};
        }
        displayData = populateDisplayData(testList);
    }

    /**
     * A 2d array to be used as display data
     */
    private String[][] populateDisplayData(List<TestObjectWrapper> testList) {

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

        String[][] displayData = new String[testList.size()][columnNames.length];

        for (int index = 0; index < testList.size(); ++index) {
            // get only the actual test name
            String fullTestName = testList.get(index).getTestClassObject().getName();
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
                displayData[index][2] = Integer.toString(testList.get(index).getTestsequence());
                // column3 - test name
                displayData[index][3] = testName;
            } else {
                // column2 - test name
                displayData[index][2] = testName;
            }
        }
        return displayData;
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

    TestObjectWrapper getTestAt(int rowIndex) {
        return testList.get(rowIndex);
    }
}
