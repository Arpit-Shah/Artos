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
 * 
 * 
 *
 */
public class CustomPrompt implements ItemListener {

	String title = "";
	String str1 = "";
	String str2 = "";
	String str3 = "";
	String str4 = "";
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
	private long initial;
	private long remaining;
	private JButton jbtnYes;
	private JButton jbtnNo;
	JFrame jf = new JFrame();

	public CustomPrompt(long millis) {
		this.ctdwnLatch = null;
		this.countdownTime = millis;
	}

	public CustomPrompt(CountDownLatch ctdwnLatch, long millis) {
		this.ctdwnLatch = ctdwnLatch;
		this.countdownTime = millis;
	}

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
			if (width > 700 || height > 700) {
				throw new Exception(ExceptionValue.OVERSIZE_OBJECT.getValue() + " : " + getImage().getAbsolutePath());
			}
			iconlabel = new JLabel();
			iconlabel.setIcon(icon);
			imagePanel.add(iconlabel);
			imagePanel.setPreferredSize(new Dimension(width, height));
		}

		// Time Panel
		Dimension timePanelDim = new Dimension(700, 70);
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
		Dimension infoPanelLabelDim = new Dimension(700, 35);
		Font font = new Font("Arial", Font.BOLD, 20);

		JPanel infoPanel1 = new JPanel();
		JLabel jlbl1 = new JLabel(str1, SwingConstants.CENTER);
		jlbl1.setFont(font);
		jlbl1.setPreferredSize(infoPanelLabelDim);
		infoPanel1.add(jlbl1);
		infoPanel1.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel2 = new JPanel();
		JLabel jlbl2 = new JLabel(str2, SwingConstants.CENTER);
		jlbl2.setFont(font);
		jlbl2.setPreferredSize(infoPanelLabelDim);
		infoPanel2.add(jlbl2);
		infoPanel2.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel3 = new JPanel();
		JLabel jlbl3 = new JLabel(str3, SwingConstants.CENTER);
		jlbl3.setFont(font);
		jlbl3.setPreferredSize(infoPanelLabelDim);
		infoPanel3.add(jlbl3);
		infoPanel3.setPreferredSize(infoPanelLabelDim);

		JPanel infoPanel4 = new JPanel();
		JLabel jlbl4 = new JLabel(str4, SwingConstants.CENTER);
		jlbl4.setFont(font);
		jlbl4.setPreferredSize(infoPanelLabelDim);
		infoPanel4.add(jlbl4);
		infoPanel4.setPreferredSize(infoPanelLabelDim);

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

	public void stop() {
		disposeGUI();
	}

	// this method will run when user presses the start button
	void updateDisplay() {
		Timeclass tc = new Timeclass();
		timer = new Timer(1000, tc);
		initial = System.currentTimeMillis();
		timer.start();
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

	public void setBtnYesText(String btnYesText) {
		this.btnYesText = btnYesText.toUpperCase();
	}

	public void setBtnNoText(String btnNoText) {
		this.btnNoText = btnNoText.toUpperCase();
	}

	public void setCtdwnLatch(CountDownLatch ctdwnLatch) {
		this.ctdwnLatch = ctdwnLatch;
	}

	public boolean isHideButtons() {
		return hideButtons;
	}

	public void setHideButtons(boolean hideButtons) {
		this.hideButtons = hideButtons;
	}

	public void setTitle(String title) {
		this.title = title;
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

	public boolean isButtonYesPressed() {
		return buttonYesPressed;
	}

	private void setButtonYesPressed(boolean buttonYesPressed) {
		this.buttonYesPressed = buttonYesPressed;
	}

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
}