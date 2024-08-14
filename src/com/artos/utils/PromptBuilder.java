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
package com.artos.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;

import com.artos.framework.infra.TestContext;

/**
 * The GUI is intended to offer a user interface that can assist users in
 * executing tests. It can be used in a blocking or non-blocking manner, and it
 * offers several features that enable multiple uses. Count-down timer is the
 * mandatory inclusion which encourages good testing practices by ensuring that
 * no action is endless.
 * 
 * <PRE>
 * 1. Display Count down Timer (Mandatory)
 * 2. Display instruction and action (As many as user can fit in one screen, Ownership is on user to scale for resolution of the screen)
 * 3. Display images (As many as user can fit in one screen, Image will be auto-scaled to fit between 700x500px)
 * 4. Display/hide 2 buttons
 * 5. Display input box (Only one per instance)
 * </PRE>
 * 
 * <PRE>
 * // (Non-blocking) Count down timer + 2 button + no image + no text
 * PromptBuilder pb1 = new PromptBuilder(context, 5000, ButtonOptions.PASS_FAIL, false);
 * pb1.start();
 * 
 * // (Blocking) Count down timer + no button + no image + no text
 * PromptBuilder pb2 = new PromptBuilder(context, 5000, ButtonOptions.PASS_FAIL, true);
 * pb2.start();
 * 
 * // (Blocking) Count down timer + 2 button + no image + 4 line text
 * PromptBuilder pb3 = new PromptBuilder(context, 5000, ButtonOptions.PASS_FAIL, true);
 * pb3.addString("Count down Timer : Blocking");
 * pb3.addString("Text 2");
 * pb3.addString("Text 3");
 * pb3.addString("Text 4");
 * pb3.start();
 * 
 * // (Non-blocking) Count down timer + hide button + image + 2
 * // line text
 * PromptBuilder pb4 = new PromptBuilder(context, 5000, ButtonOptions.PASS_FAIL, false);
 * pb4.setImage(new File("./assets/images/test.jpg"));
 * pb4.addString("Text 1");
 * pb4.addString("Text 2");
 * pb4.hideButtons(true);
 * pb4.start();
 * {
 * 	// Do something here
 * }
 * pb4.stop();
 * </PRE>
 *
 */
public class PromptBuilder implements ItemListener {

	int PANEL_MAX_WIDTH = 700;
	int fontSize = 17;
	String title = "CountDown Timer";
	String btnYesText = "Yes";
	String btnNoText = "No";
	File image = null;
	boolean showInputBox = false;
	boolean hideButtons = false;
	boolean leftButtonPressed = false;
	boolean rightButtonPressed = false;
	boolean isBlocking = false;
	String inputBoxText = "";
	List<String[]> ActionList = new ArrayList<String[]>();
	List<JPanel> JPanelImageList = new ArrayList<JPanel>();
	TestContext context;

	private CountDownLatch ctdwnLatch = null;

	JLabel jltime;
	JLabel jl;
	NumberFormat format;
	long countdownTime = 60 * 1000;
	private Timer timer;
	private long initial;
	private long remaining;
	private JButton jbtnYes;
	private JButton jbtnNo;
	JTextField textField;
	JFrame jf = new JFrame();

	/**
	 * Set time for count down timer in non-blocking mode
	 * 
	 * @param context    {@link TestContext}
	 * @param millis     time in milliseconds for count down timer
	 * @param btnOptions button options
	 * @param isBlocking true = execution will be blocked until user press button,
	 *                   false = execution will continue while prompt is shown
	 */
	public PromptBuilder(TestContext context, long millis, ButtonOptions btnOptions, boolean isBlocking) {
		this.context = context;
		this.ctdwnLatch = null;
		this.countdownTime = millis;
		setButtonOptions(btnOptions);
		setCtdwnLatch(new CountDownLatch(1));
		this.isBlocking = isBlocking;

		// Set UI Title
		if (null != context.getCurrentTestCase()) {
			this.title = context.getCurrentTestCase().getTestClassObject().getSimpleName();
			if (null != context.getCurrentTestUnit()) {
				this.title = this.title + " - " + context.getCurrentTestUnit().getTestUnitMethod().getName() + "()";
			}
		} else if (null != context.getCurrentTestScenario()) {
			this.title = context.getCurrentTestScenario().getClass().getSimpleName();
		} else {
			// use default name
		}
	}

	/**
	 * Starts GUI with count down timer
	 */
	public void start() {

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

			Font font = new Font("Arial", Font.BOLD, fontSize);

			// Table
			JPanel infoPanel = new JPanel();
			String[] columnNames = { "Action", "Description" };
			String[][] tableData = new String[ActionList.size()][2];
			for (int i = 0; i < ActionList.size(); i++) {
				tableData[i][0] = ActionList.get(i)[0];
				tableData[i][1] = ActionList.get(i)[1];
			}
			JTable testTableView = new JTable(tableData, columnNames);
			testTableView.setDefaultRenderer(Object.class, new PaintTableCellRenderer());
			testTableView.setFont(font);
			testTableView.setFillsViewportHeight(true);
			testTableView.getColumnModel().getColumn(0).setPreferredWidth(100);
			testTableView.getColumnModel().getColumn(1).setPreferredWidth(PANEL_MAX_WIDTH - 100);
			testTableView.setRowHeight(30);
			testTableView.setShowGrid(false);
			testTableView.setBackground(jf.getBackground());
			infoPanel.setPreferredSize(new Dimension(700, ActionList.size() * 35));
			infoPanel.add(testTableView);

			// Input box panel
			Dimension ibim = new Dimension(PANEL_MAX_WIDTH, 35);
			JPanel inputBoxPanel = new JPanel();
			textField = new JTextField(16);
			textField.setPreferredSize(ibim);
			textField.setHorizontalAlignment(SwingConstants.LEFT);
			textField.setFont(font);
			inputBoxPanel.add(textField);

			// Time Panel
			Dimension timePanelDim = new Dimension(PANEL_MAX_WIDTH + 50, 70);
			jltime = new JLabel("00:00:00", SwingConstants.CENTER);
			jltime.setForeground(Color.WHITE);
			jltime.setBackground(Color.BLACK);
			jltime.setOpaque(true);
			jltime.setFont(new Font("Arial", Font.BOLD, 50));
			jltime.setPreferredSize(timePanelDim);
			JPanel timePanel = new JPanel();
			timePanel.setForeground(Color.BLACK);
			timePanel.add(jltime);
			timePanel.setPreferredSize(timePanelDim);

			// Button Panel
			JPanel buttonPanel = new JPanel();
			buttonPanel.setLayout(new FlowLayout());
			jbtnYes = new JButton(btnYesText);
			jbtnNo = new JButton(btnNoText);
			Dimension buttondim = new Dimension(100, 40);
			jbtnYes.setPreferredSize(buttondim);
			jbtnNo.setPreferredSize(buttondim);
			jbtnYes.setFont(new Font("Arial", Font.BOLD, 12));
			jbtnNo.setFont(new Font("Arial", Font.BOLD, 12));
			buttonPanel.add(jbtnYes);
			buttonPanel.add(jbtnNo);
			Event e = new Event();
			jbtnYes.addActionListener(e);
			jbtnNo.addActionListener(e);

			// Frame Layout
			BoxLayout boxlayt = new BoxLayout(jf.getContentPane(), BoxLayout.Y_AXIS);
			jf.getContentPane().setLayout(boxlayt);
			jf.getContentPane().add(timePanel);

			jf.getContentPane().add(infoPanel);

			if (isShowInputBox()) {
				jf.getContentPane().add(inputBoxPanel);
			}

			for (JPanel jp : JPanelImageList) {
				jf.getContentPane().add(jp);
			}

			if (!isHideButtons()) {
				jf.getContentPane().add(buttonPanel);
			}
			jf.setBackground(Color.BLACK);
			jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jf.setTitle(title);
			jf.pack();
			jf.setLocationByPlatform(true);

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
				jf.setIconImages(imgList);
			} catch (Exception IllegalArgumentException) {
				System.err.println("Icons can not be found");
			}
			jf.setVisible(true);

			// Ensure first button is in focus
			jf.getRootPane().setDefaultButton(jbtnYes);
			jbtnYes.requestFocus();

			// Start Timer
			updateDisplay();

			if (isBlocking) {
				getCtdwnLatch().await();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private class PaintTableCellRenderer extends DefaultTableCellRenderer {

		private static final long serialVersionUID = 1L;

		// You should override getTableCellRendererComponent
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {

			Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

			// Check the column name, if it is "version"
			if (table.getColumnName(column).compareToIgnoreCase("Action") == 0) {
				// You know version column includes string
				String versionVal = (String) value;

				if (versionVal.contains("Validate")) {
					c.setForeground(Color.BLUE);
					c.setFont(new Font("Arial", Font.BOLD, fontSize));
				} else if (versionVal.contains("Action")) {
					c.setForeground(Color.BLACK);
					c.setFont(new Font("Arial", Font.BOLD, fontSize));
				} else if (versionVal.contains("Important")) {
					c.setForeground(Color.RED);
					c.setFont(new Font("Arial", Font.BOLD, fontSize));
				} else if (versionVal.contains("Warning")) {
					c.setForeground(Color.RED);
					c.setFont(new Font("Arial", Font.BOLD, fontSize));
				} else {
					c.setForeground(Color.BLACK);
					c.setFont(new Font("Arial", Font.BOLD, fontSize));
				}
			} else {
				// Here you should also stay at default
				// stay at default
				c.setForeground(Color.BLACK);
				c.setFont(new Font("Arial", Font.TRUETYPE_FONT, fontSize));
			}
			return c;
		}

	}

	/*
	 * Force stops GUI
	 */
	public void stop() {
		if (null != ctdwnLatch) {
			while (ctdwnLatch.getCount() > 0) {
				ctdwnLatch.countDown();
			}
		}
		disposeGUI();
	}

	// this method will run when user presses the start button
	void updateDisplay() {
		Timeclass tc = new Timeclass();
		timer = new Timer(500, tc);
		initial = System.currentTimeMillis();
		timer.start();
	}

	// code that is invoked by swing timer for every second passed
	private class Timeclass implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			remaining = countdownTime;
			long current = System.currentTimeMillis();
			long elapsed = current - initial;
			remaining -= elapsed;

			format = NumberFormat.getNumberInstance();
			format.setMinimumIntegerDigits(2);

			if (remaining < 0) {
				remaining = (long) 0;
			}

			int hours = (int) ((remaining / 1000) / 3600);
			int minutes = (int) (((remaining / 1000) / 60) % 60);
			int seconds = (int) ((remaining / 1000) % 60);
			jltime.setText(format.format(hours) + ":" + format.format(minutes) + ":" + format.format(seconds));

			if (remaining == 0) {
				stop();
			}
		}
	}

	/*
	 * dispose call has to be synchronised. if called in async manner then it may
	 * get into thread deadlock
	 */
	synchronized private void disposeGUI() {

		// jltime.setText("Stop");
		jf.setVisible(false);
		if (null != timer) {
			timer.stop();
		}
		jf.dispose();
		if (null != ctdwnLatch) {
			ctdwnLatch.countDown();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub
	}

	// code for what happens when user presses the left or right button
	private class Event implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String bname = e.getActionCommand();

			if (null != textField && null != textField.getText()) {
				setInputBoxText(textField.getText());
			}

			if (bname.equals(btnYesText)) {
				setLeftButtonPressed(true);
				context.getLogger().info("Instructions:");
				for (int i = 0; i < ActionList.size(); i++) {
					context.getLogger().info("(" + ActionList.get(i)[0] + ") " + ActionList.get(i)[1]);
				}
				context.getLogger().info("\nUser Action:");
				context.getLogger().info("\"" + btnYesText + "\" button was pressed\n");
				stop();
			} else if (bname.equals(btnNoText)) {
				setButtonNoPressed(true);
				context.getLogger().info("User Action:");
				for (int i = 0; i < ActionList.size(); i++) {
					context.getLogger().info("(" + ActionList.get(i)[0] + ") " + ActionList.get(i)[1]);
				}
				context.getLogger().info("\nUser Action:");
				context.getLogger().info("\"" + btnNoText + "\" button was pressed\n");
				stop();
			} else {
				// Do nothing
			}
		}
	}

	private void setBtnYesText(String btnYesText) {
		this.btnYesText = btnYesText.toUpperCase();
	}

	private void setBtnNoText(String btnNoText) {
		this.btnNoText = btnNoText.toUpperCase();
	}

	public void setButtonOptions(ButtonOptions btnOptions) {
		switch (btnOptions) {
		case DISABLE:
			hideButtons(true);
			break;
		case YES_NO:
			setBtnYesText("YES");
			setBtnNoText("NO");
			break;
		case OK_CANCEL:
			setBtnYesText("OK");
			setBtnNoText("CANCEL");
			break;
		case PASS_FAIL:
			setBtnYesText("PASS");
			setBtnNoText("FAIL");
			break;
		case CONTINUE_STOP:
			setBtnYesText("CONTINUE");
			setBtnNoText("STOP");
			break;
		case GOOD_BAD:
			setBtnYesText("GOOD");
			setBtnNoText("BAD");
			break;
		case ACCEPT_REJECT:
			setBtnYesText("ACCEPT");
			setBtnNoText("REJECT");
			break;
		case NEXT_CANCEL:
			setBtnYesText("NEXT");
			setBtnNoText("CANCEL");
			break;
		case START_STOP:
			setBtnYesText("START");
			setBtnNoText("STOP");
			break;
		default:
			break;
		}
	}

	public enum ButtonOptions {
		DISABLE, YES_NO, OK_CANCEL, PASS_FAIL, CONTINUE_STOP, GOOD_BAD, ACCEPT_REJECT, NEXT_CANCEL, START_STOP
	}

	public void setCtdwnLatch(CountDownLatch ctdwnLatch) {
		this.ctdwnLatch = ctdwnLatch;
	}

	public CountDownLatch getCtdwnLatch() {
		return ctdwnLatch;
	}

	public boolean isHideButtons() {
		return hideButtons;
	}

	public void hideButtons(boolean hideButtons) {
		this.hideButtons = hideButtons;
	}

	public boolean isShowInputBox() {
		return showInputBox;
	}

	public void showInputBox(boolean showInputBox) {
		this.showInputBox = showInputBox;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isLeftButtonPressed() {
		return leftButtonPressed;
	}

	private void setLeftButtonPressed(boolean leftButtonPressed) {
		this.leftButtonPressed = leftButtonPressed;
	}

	public boolean isRightButtonPressed() {
		return rightButtonPressed;
	}

	private void setButtonNoPressed(boolean rightButtonPressed) {
		this.rightButtonPressed = rightButtonPressed;
	}

	public String getInputBoxText() {
		return inputBoxText;
	}

	private void setInputBoxText(String inputBoxText) {
		this.inputBoxText = inputBoxText;
	}

	/**
	 * Add action and string to display instruction for user. User can add as many
	 * line of text that can fit in one screen, Text is trimmed if goes above 700px
	 * width panel. Ownership is on user to ensure content fit on the screen.
	 * Different resolution of screens will have different experience
	 * 
	 * @param actionKeyWord action keyword
	 * @param instruction instruction
	 */
	public void addText(AKW actionKeyWord, String instruction) {
		if (null != instruction) {
			ActionList.add(new String[] { actionKeyWord.toString(), instruction });
		}
	}

	/**
	 * Add string to display instruction for user. User can add as many
	 * line of text that can fit in one screen, Text is trimmed if goes above 700px
	 * width panel. Ownership is on user to ensure content fit on the screen.
	 * Different resolution of screens will have different experience
	 * 
	 * @param instruction instruction
	 */
	public void addText(String instruction) {
		addText(AKW.NONE, instruction);
	}

	public enum AKW {
		NONE("  -  "), ACTION("Action"), VALIDATE("Validate"), INFO("Info"), PREP("Preparation"), NOTE("Note"),
		ENSURE("Ensure"), IMPORTANT("Important"), WARNING("Warning");

		private final String text;

		AKW(final String text) {
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
	}

	/**
	 * Display image. User can add as many images that can fit in one screen, Image
	 * is auto-scaled to fit between 700x500px panel. Ownership is on user to ensure
	 * content fit on the screen. Different resolution of screens will have
	 * different experience
	 * 
	 * @param image = Image file
	 */
	public void addImage(File image) {
		try {
			if (null != image) {
				JPanel imagePanel = new JPanel();
				JLabel iconlabel = null;

				ImageIcon icon = new ImageIcon(image.getAbsolutePath());
				BufferedImage bimg = ImageIO.read(image);
				int width = bimg.getWidth();
				int height = bimg.getHeight();

				Image img = icon.getImage(); // transform it
				Image scaledImage;

				if (width <= PANEL_MAX_WIDTH && height <= 500) {
					scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
				} else {

					// Scale down width and height equally until it fits
					scaledImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
					for (int i = 1; i < 100; i++) {
						int newWidth = width - (width * i / 100);
						int newHeight = height - (height * i / 100);
						if (newWidth < 700 && newHeight < 500) {
							scaledImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
							break;
						}
					}
				}

				icon = new ImageIcon(scaledImage);

				iconlabel = new JLabel();
				iconlabel.setIcon(icon);
				imagePanel.add(iconlabel);

				JPanelImageList.add(imagePanel);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
