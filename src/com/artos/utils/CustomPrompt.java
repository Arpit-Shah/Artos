// Copyright <2018> <Artos>

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
// OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package com.artos.utils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.artos.framework.Enums.ExceptionValue;

/**
 * Designed for providing GUI which can guide user during test execution, GUI
 * can be used in blocking or nonblocking manner. GUI can be used multiple ways
 * using following features.
 * 
 * <PRE>
 * 1. Display Countdown Timer
 * 2. Display upto 10 line of text which can be updated after GUI is launched
 * 3. Display image
 * 4. Display/hide 2 buttons
 * 5. Override button text
 * </PRE>
 * 
 * <PRE>
 * // (Non-blocking) Count down timer + 2 button + no image + no text
 * CustomPrompt cntdwn1 = new CustomPrompt(5000);
 * cntdwn1.start();
 * 
 * // (Blocking) Count down timer + no button + no image + no text
 * CountDownLatch cntdwnltch1 = new CountDownLatch(1);
 * CustomPrompt cntdwn1 = new CustomPrompt(5000);
 * cntdwn1.start();
 * cntdwnltch1.await();
 * cntdwn1.stop();
 * 
 * // (Blocking) Count down timer + 2 button + no image + 4 line text
 * CountDownLatch cntdwnltch1 = new CountDownLatch(1);
 * CustomPrompt cntdwn1 = new CustomPrompt(cntdwnltch1, 5000);
 * cntdwn1.setStr1("Count down Timer : Blocking");
 * cntdwn1.setStr2("Text 2");
 * cntdwn1.setStr3("Text 3");
 * cntdwn1.setStr4("Text 4");
 * cntdwn1.start();
 * cntdwnltch1.await();
 * cntdwn1.stop();
 * 
 * // (Blocking) Count down timer + 2 buttons with text override + no image + 4
 * // line text
 * CountDownLatch cntdwnltch2 = new CountDownLatch(1);
 * CustomPrompt cntdwn2 = new CustomPrompt(cntdwnltch2, 5000);
 * cntdwn2.setBtnYesText("????");
 * cntdwn2.setBtnNoText("****");
 * cntdwn2.setStr1("Countdown Timer : Blocking");
 * cntdwn2.setStr2("Button Text override : true");
 * cntdwn2.setStr3("Text 3");
 * cntdwn2.setStr4("Text 4");
 * cntdwn2.start();
 * cntdwnltch2.await();
 * 
 * // (Non-blocking) Count down timer + hide button + image + 2
 * // line text
 * CountDownLatch cntdwnltch5 = new CountDownLatch(1);
 * CustomPrompt cntdwn5 = new CustomPrompt(cntdwnltch5, 10000);
 * cntdwn5.showImage(new File("./assets/images/test.jpg"));
 * cntdwn5.setStr1("Text 1");
 * cntdwn5.setStr2("Text 2");
 * cntdwn3.setHideButtons(true);
 * cntdwn5.start();
 * cntdwnltch5.await();
 * cntdwn5.stop();
 * </PRE>
 *
 */
public class CustomPrompt implements ItemListener {

	String title = "";
	String str1 = "";
	String str2 = "";
	String str3 = "";
	String str4 = "";
	String str5 = "";
	String str6 = "";
	String str7 = "";
	String str8 = "";
	String str9 = "";
	String str10 = "";
	String btnYesText = "Yes";
	String btnNoText = "No";
	File image = null;
	boolean hideButtons = false;
	boolean buttonYesPressed = false;
	boolean buttonNoPressed = false;

	private CountDownLatch ctdwnLatch = null;

	JLabel jltime;
	JLabel jl;
	NumberFormat format;
	long countdownTime = 60 * 1000;
	private Timer timer;
	private Timer timer2;
	private long initial;
	private long remaining;
	private JButton jbtnYes;
	private JButton jbtnNo;
	private JLabel jlbl1;
	private JLabel jlbl2;
	private JLabel jlbl3;
	private JLabel jlbl4;
	private JLabel jlbl5;
	private JLabel jlbl6;
	private JLabel jlbl7;
	private JLabel jlbl8;
	private JLabel jlbl9;
	private JLabel jlbl10;
	JFrame jf = new JFrame();

	/**
	 * Set time for count down timer in non-blocking mode
	 * 
	 * @param millis
	 *            time in milliseconds for count down timer
	 */
	public CustomPrompt(long millis) {
		this.ctdwnLatch = null;
		this.countdownTime = millis;
	}

	/**
	 * Set time for count down timer in blocking mode. If CountdownLatch object
	 * is null then non-blocking mode will be assumed.
	 * 
	 * @param ctdwnLatch
	 *            CountdownLatch object or null
	 * @param millis
	 *            time in milliseconds for count down timer
	 */
	public CustomPrompt(CountDownLatch ctdwnLatch, long millis) {
		this.ctdwnLatch = ctdwnLatch;
		this.countdownTime = millis;
	}

	/**
	 * Starts GUI with count down timer
	 * 
	 * @throws Exception
	 *             if any IO error occurred.
	 */
	public void start() throws Exception {

		// UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
		// UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		// Image panel
		JPanel imagePanel = new JPanel();
		JLabel iconlabel = null;
		if (null != getImage()) {
			ImageIcon icon = new ImageIcon(getImage().getAbsolutePath());
			BufferedImage bimg = ImageIO.read(getImage());
			int width = bimg.getWidth();
			int height = bimg.getHeight();
			if (width > 1000 || height > 1000) {
				throw new Exception(ExceptionValue.OVERSIZE_OBJECT.getValue() + " : " + getImage().getAbsolutePath());
			}
			iconlabel = new JLabel();
			iconlabel.setIcon(icon);
			imagePanel.add(iconlabel);
			imagePanel.setPreferredSize(new Dimension(width, height));
		}

		// Time Panel
		Dimension timePanelDim = new Dimension(1000, 70);
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
		buttonPanel.add(jbtnYes);
		buttonPanel.add(jbtnNo);
		Event e = new Event();
		jbtnYes.addActionListener(e);
		jbtnNo.addActionListener(e);

		// Info Panel
		Dimension infoPanelLabelDim = new Dimension(1000, 35);
		Font font = new Font("Arial", Font.BOLD, 20);

		JPanel infoPanel1 = new JPanel();
		jlbl1 = new JLabel(str1, SwingConstants.CENTER);
		jlbl1.setFont(font);
		jlbl1.setPreferredSize(infoPanelLabelDim);
		infoPanel1.add(jlbl1);
		infoPanel1.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel2 = new JPanel();
		jlbl2 = new JLabel(str2, SwingConstants.CENTER);
		jlbl2.setFont(font);
		jlbl2.setPreferredSize(infoPanelLabelDim);
		infoPanel2.add(jlbl2);
		infoPanel2.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel3 = new JPanel();
		jlbl3 = new JLabel(str3, SwingConstants.CENTER);
		jlbl3.setFont(font);
		jlbl3.setPreferredSize(infoPanelLabelDim);
		infoPanel3.add(jlbl3);
		infoPanel3.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel4 = new JPanel();
		jlbl4 = new JLabel(str4, SwingConstants.CENTER);
		jlbl4.setFont(font);
		jlbl4.setPreferredSize(infoPanelLabelDim);
		infoPanel4.add(jlbl4);
		infoPanel4.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel5 = new JPanel();
		jlbl5 = new JLabel(str5, SwingConstants.CENTER);
		jlbl5.setFont(font);
		jlbl5.setPreferredSize(infoPanelLabelDim);
		infoPanel5.add(jlbl5);
		infoPanel5.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel6 = new JPanel();
		jlbl6 = new JLabel(str6, SwingConstants.CENTER);
		jlbl6.setFont(font);
		jlbl6.setPreferredSize(infoPanelLabelDim);
		infoPanel6.add(jlbl6);
		infoPanel6.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel7 = new JPanel();
		jlbl7 = new JLabel(str7, SwingConstants.CENTER);
		jlbl7.setFont(font);
		jlbl7.setPreferredSize(infoPanelLabelDim);
		infoPanel7.add(jlbl7);
		infoPanel7.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel8 = new JPanel();
		jlbl8 = new JLabel(str8, SwingConstants.CENTER);
		jlbl8.setFont(font);
		jlbl8.setPreferredSize(infoPanelLabelDim);
		infoPanel8.add(jlbl8);
		infoPanel8.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel9 = new JPanel();
		jlbl9 = new JLabel(str9, SwingConstants.CENTER);
		jlbl9.setFont(font);
		jlbl9.setPreferredSize(infoPanelLabelDim);
		infoPanel9.add(jlbl9);
		infoPanel9.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel10 = new JPanel();
		jlbl10 = new JLabel(str10, SwingConstants.CENTER);
		jlbl10.setFont(font);
		jlbl10.setPreferredSize(infoPanelLabelDim);
		infoPanel10.add(jlbl10);
		infoPanel10.setPreferredSize(infoPanelLabelDim);

		// Frame Layout
		BoxLayout boxlayt = new BoxLayout(jf.getContentPane(), BoxLayout.Y_AXIS);
		jf.getContentPane().setLayout(boxlayt);
		jf.getContentPane().add(timePanel);
		if (null == str1 || "".equals(str1)) {
		} else {
			jf.getContentPane().add(infoPanel1);
		}
		if (null == str2 || "".equals(str2)) {
		} else {
			jf.getContentPane().add(infoPanel2);
		}
		if (null == str3 || "".equals(str3)) {
		} else {
			jf.getContentPane().add(infoPanel3);
		}
		if (null == str4 || "".equals(str4)) {
		} else {
			jf.getContentPane().add(infoPanel4);
		}
		if (null == str5 || "".equals(str5)) {
		} else {
			jf.getContentPane().add(infoPanel5);
		}
		if (null == str6 || "".equals(str6)) {
		} else {
			jf.getContentPane().add(infoPanel6);
		}
		if (null == str7 || "".equals(str7)) {
		} else {
			jf.getContentPane().add(infoPanel7);
		}
		if (null == str8 || "".equals(str8)) {
		} else {
			jf.getContentPane().add(infoPanel8);
		}
		if (null == str9 || "".equals(str9)) {
		} else {
			jf.getContentPane().add(infoPanel9);
		}
		if (null == str10 || "".equals(str10)) {
		} else {
			jf.getContentPane().add(infoPanel10);
		}
		if (null != getImage()) {
			jf.getContentPane().add(imagePanel);
		}
		if (!isHideButtons()) {
			jf.getContentPane().add(buttonPanel);
		}
		jf.setBackground(Color.BLACK);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setTitle("CountDown Timer");
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

		// Start Timer
		updateDisplay();
	}

	// Force stops GUI
	public void stop() {
		disposeGUI();
	}

	// this method will run when user presses the start button
	void updateDisplay() {
		Timeclass tc = new Timeclass();
		timer = new Timer(1000, tc);
		initial = System.currentTimeMillis();
		timer.start();

		TextUpdateclass textupdate = new TextUpdateclass();
		timer2 = new Timer(100, textupdate);
		timer2.start();
	}

	// code that is invoked by swing timer for every second passed
	public class Timeclass implements ActionListener {

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
				disposeGUI();
			}
		}
	}

	// code that is invoked by swing timer for every second passed
	public class TextUpdateclass implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			jlbl1.setText(str1);
			jlbl2.setText(str2);
			jlbl3.setText(str3);
			jlbl4.setText(str4);
			jlbl5.setText(str5);
			jlbl6.setText(str6);
			jlbl7.setText(str7);
			jlbl8.setText(str8);
			jlbl9.setText(str9);
			jlbl10.setText(str10);
		}
	}

	private void disposeGUI() {
		// jltime.setText("Stop");
		jf.setVisible(false);
		timer.stop();
		jf.dispose();
		if (ctdwnLatch != null) {
			ctdwnLatch.countDown();
		}
	}

	@Override
	public void itemStateChanged(ItemEvent arg0) {
		// TODO Auto-generated method stub

	}

	// code for what happens when user presses the start or reset button
	public class Event implements ActionListener {

		public void actionPerformed(ActionEvent e) {
			String bname = e.getActionCommand();
			if (bname.equals(btnYesText)) {
				setButtonYesPressed(true);
				disposeGUI();
			} else if (bname.equals(btnNoText)) {
				setButtonNoPressed(true);
				disposeGUI();
			} else {
				// Do nothing
			}
		}
	}

	/**
	 * Sets text of the button on the left hand side
	 * 
	 * @param btnYesText
	 *            button label
	 */
	public void setBtnYesText(String btnYesText) {
		this.btnYesText = btnYesText.toUpperCase();
	}

	/**
	 * Sets text of the button on the right hand side
	 * 
	 * @param btnNoText
	 *            button label
	 */
	public void setBtnNoText(String btnNoText) {
		this.btnNoText = btnNoText.toUpperCase();
	}

	/**
	 * Sets object of Countdown latch. This object must be set before start()
	 * method is executed, otherwise it will ignored.
	 * 
	 * @param ctdwnLatch
	 *            CountDownLatch object
	 */
	public void setCtdwnLatch(CountDownLatch ctdwnLatch) {
		this.ctdwnLatch = ctdwnLatch;
	}

	/**
	 * Checks if button is set to be hide
	 * 
	 * @return True or False
	 */
	public boolean isHideButtons() {
		return hideButtons;
	}

	/**
	 * Hide button if set to true
	 * 
	 * @param hideButtons
	 *            true = hide button, false = unhide
	 */
	public void setHideButtons(boolean hideButtons) {
		this.hideButtons = hideButtons;
	}

	/**
	 * Set title of the GUI
	 * 
	 * @param title
	 *            title of GUI window
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Allows user to query if Yes button was pressed
	 * 
	 * @return true if yes button is pressed (Left most button)
	 */
	public boolean isButtonYesPressed() {
		return buttonYesPressed;
	}

	private void setButtonYesPressed(boolean buttonYesPressed) {
		this.buttonYesPressed = buttonYesPressed;
	}

	/**
	 * Allows user to query if No button was pressed
	 * 
	 * @return true if no button is pressed (Right most button)
	 */
	public boolean isButtonNoPressed() {
		return buttonNoPressed;
	}

	private void setButtonNoPressed(boolean buttonNoPressed) {
		this.buttonNoPressed = buttonNoPressed;
	}

	public File getImage() {
		return image;
	}

	public void showImage(File image) {
		this.image = image;
	}

	public void setStr1(String str1) {
		this.str1 = str1;
	}

	public void setStr2(String str2) {
		this.str2 = str2;
	}

	public void setStr3(String str3) {
		this.str3 = str3;
	}

	public void setStr4(String str4) {
		this.str4 = str4;
	}

	public void setTimer(Timer timer) {
		this.timer = timer;
	}

	public void setStr5(String str5) {
		this.str5 = str5;
	}

	public void setStr6(String str6) {
		this.str6 = str6;
	}

	public void setStr7(String str7) {
		this.str7 = str7;
	}

	public void setStr8(String str8) {
		this.str8 = str8;
	}

	public void setStr9(String str9) {
		this.str9 = str9;
	}

	public void setStr10(String str10) {
		this.str10 = str10;
	}
}