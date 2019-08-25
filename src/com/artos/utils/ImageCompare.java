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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.google.common.io.Files;

/**
 * This code will compare pixel from base image with other image.
 * 
 * <PRE>
 * 1) If both pixel at location (x,y) are same then add same pixel in result image without change. Otherwise it modifies that pixel and add to our result
 * image. 
 * 2) In case of baseline image height or width is larger than other image then it will add red colour for extra pixel which is not available in
 * other images. 
 * 3) Both image file format should be same for comparison. Code will use base image file format to create resultant image file.
 * </PRE>
 * 
 */
public class ImageCompare {

	private int percentageMatch = 0;
	private File resultImage = null;

	/**
	 * compare image with reference image and return percentage of match.
	 * 
	 * @param referenceImage = golden sample, reference image
	 * @param targetImage = image which is required to be tested
	 * @throws Exception Exception in case image does not exist or extension does not match
	 */
	public void compare(File referenceImage, File targetImage) throws Exception {
		compare(referenceImage, targetImage, null, null);
	}

	/**
	 * compare image with reference image and return percentage of match. Image must be of same type. Any mismatch of pixels will be highlighted in
	 * red colour and result image will be stored at desired location.
	 * 
	 * @param referenceImage = golden sample, reference image
	 * @param targetImage = image which is required to be tested
	 * @param resultDir = result destination directory name
	 * @param resultImageName = result image name without an extension
	 * @throws Exception in case image does not exist or extension does not match
	 */
	public void compare(File referenceImage, File targetImage, File resultDir, String resultImageName) throws Exception {

		String fileExtenstion = Files.getFileExtension(referenceImage.getName());

		if (!(referenceImage.exists() && targetImage.exists() && referenceImage.isFile() && targetImage.isFile())) {
			throw new FileNotFoundException();
		}

		if (!(Files.getFileExtension(referenceImage.getName()).equalsIgnoreCase(Files.getFileExtension(targetImage.getName())))) {
			throw new Exception("File extensions are not the same");
		}

		if (null != resultDir) {
			if (!resultDir.exists() || !resultDir.isDirectory()) {
				resultDir.mkdirs();
			}
		}

		long matchCount = 0;
		BufferedImage refImage = ImageIO.read(referenceImage);
		BufferedImage testImage = ImageIO.read(targetImage);
		int height = refImage.getHeight();
		int width = refImage.getWidth();

		BufferedImage rImage;
		if (fileExtenstion.toUpperCase().contains("PNG")) {
			rImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		} else {
			// Assume jpg, it does not have alpha
			rImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		}
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				try {
					int pixelC = testImage.getRGB(x, y);
					int pixelB = refImage.getRGB(x, y);
					if (pixelB == pixelC) {
						rImage.setRGB(x, y, refImage.getRGB(x, y));
						// Add one to increase percentage of match
						matchCount++;
					} else {
						int a = 0xff | refImage.getRGB(x, y) >> 24, r = 0xff & refImage.getRGB(x, y) >> 16, g = 0x00 & refImage.getRGB(x, y) >> 8,
								b = 0x00 & refImage.getRGB(x, y);

						int modifiedRGB = a << 24 | r << 16 | g << 8 | b;
						rImage.setRGB(x, y, modifiedRGB);
					}
				} catch (Exception e) {
					// handled height or width mismatch
					rImage.setRGB(x, y, 0x80ff0000);
				}
			}
		}

		if (null != resultDir && null != resultImageName) {
			setResultImage(new File(resultDir, resultImageName + "." + fileExtenstion));
			if (fileExtenstion.toUpperCase().contains("PNG")) {
				createPngImage(rImage);
			} else {
				createJpgImage(rImage);
			}
		}

		long totalPixel = height * width;
		setPercentageMatch((int) (matchCount * 100 / totalPixel));
	}

	private void createPngImage(BufferedImage image) throws IOException {
		ImageIO.write(image, "png", getResultImage());
	}

	private void createJpgImage(BufferedImage image) throws IOException {
		ImageIO.write(image, "jpg", getResultImage());
	}

	/**
	 * Returns image match percentage. 100 if image matches completely.
	 * 
	 * @return image match percentage
	 */
	public int getPercentageMatch() {
		return percentageMatch;
	}

	private void setPercentageMatch(int percentageMatch) {
		this.percentageMatch = percentageMatch;
	}

	/**
	 * Returns result image file, null if result image is not specified.
	 * 
	 * @return result image file or null if result image is not specified.
	 */
	public File getResultImage() {
		return resultImage;
	}

	private void setResultImage(File resultImage) {
		this.resultImage = resultImage;
	}
}
