package com.arpitos.utils;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class UtilsZip {

	/**
	 * 
	 * @param source
	 *            = source file or directory which requires to be zipped
	 * @param destFile
	 *            = destination file (Example : new File("./test/test.zip"))
	 * @throws Exception
	 */
	public static void zip(File source, File destFile) throws Exception {
		FileOutputStream fos = new FileOutputStream(destFile);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		zipFile(source, source.getName(), zipOut);
		zipOut.close();
		fos.close();
	}

	/**
	 * 
	 * @param source
	 *            = List of source files or directories which requires to be zipped
	 * @param destFile
	 *            = destination file (Example : new File("./test/test.zip"))
	 * @throws Exception
	 */
	public static void zip(List<File> source, File destFile) throws Exception {
		FileOutputStream fos = new FileOutputStream(destFile);
		ZipOutputStream zipOut = new ZipOutputStream(fos);

		for (File f : source) {
			zipFile(f, f.getName(), zipOut);
		}
		zipOut.close();
		fos.close();
	}

	private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
		if (fileToZip.isHidden()) {
			return;
		}
		if (fileToZip.isDirectory()) {
			File[] children = fileToZip.listFiles();
			for (File childFile : children) {
				zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
			}
			return;
		}
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileName);
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		fis.close();
	}

	/**
	 * Traverse a directory and get all files, and add the file into fileList
	 * 
	 * @param file
	 *            file or directory
	 * @return
	 */
	private static List<String> generateFileList(File source) {
		List<String> zipList = new ArrayList<>();
		List<String> fileList = new ArrayList<>();
		walk(fileList, source);

		for (String path : fileList) {
			String str = path.substring(source.getAbsolutePath().length() + 1, path.length());
			zipList.add(str);
		}

		for (String st : zipList) {
			System.out.println(st);
		}

		return zipList;
	}

	public static void walk(List<String> fileList, File source) {

		File[] list = source.listFiles();

		if (list == null) {
			return;
		}

		for (File f : list) {
			if (f.isDirectory()) {
				fileList.add(f.getAbsolutePath());
				walk(fileList, f);
			} else {
				fileList.add(f.getAbsolutePath());
			}
		}
		System.out.println("----------- walk Finished --------------");
	}

	/**
	 * Format the file path for ZIP
	 * 
	 * @param file
	 *            file path
	 * @return Formatted file path
	 */
	private static String generateZipEntry(File source, String file) {
		return file.substring(source.getAbsolutePath().length() + 1, file.length());
	}

	/**
	 * Unzip file
	 * 
	 * @param zipFilePath
	 * @param destDir
	 * @throws Exception
	 */
	public static void unzip(File zipFile, File destDir) throws Exception {

		if (!isZipFile(zipFile)) {
			throw new Exception("Not a zip File");
		}

		// create output directory if it doesn't exist
		if (!destDir.exists() || !destDir.isDirectory()) {
			destDir.mkdirs();
		}

		FileInputStream fis;
		// buffer for read and write data to file
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFile);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir.getAbsolutePath() + File.separator + fileName);
				System.out.println("Unzipping to " + newFile.getAbsolutePath());

				if (ze.isDirectory()) {
					newFile.mkdirs();
				} else {
					// create directories for sub directories in zip
					new File(newFile.getParent()).mkdirs();

					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				// close this ZipEntry
				zis.closeEntry();
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Determine whether a file is a ZIP File.
	 */
	public static boolean isZipFile(File file) throws IOException {
		if (file.isDirectory()) {
			return false;
		}
		if (!file.canRead()) {
			throw new IOException("Cannot read file " + file.getAbsolutePath());
		}
		if (file.length() < 4) {
			return false;
		}
		DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
		int test = in.readInt();
		in.close();
		return test == 0x504b0304;
	}
}
