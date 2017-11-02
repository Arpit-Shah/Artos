package com.arpit.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zip {

	List<String> fileList;
	File sourceDir;
	File destinationDir;
	String zipFileName = "test.zip";

	/**
	 * Zips all components inside source directory and Output to
	 * destinationDirectory location with given file name
	 * 
	 * @param sourceDir
	 *            Source directory
	 * @param destinationDir
	 *            Destination directory
	 * @param zipFileName
	 *            zip File name
	 * @throws Exception
	 */
	public Zip(File sourceDir, File destinationDir, String zipFileName) throws Exception {
		fileList = new ArrayList<String>();

		if (!sourceDir.exists()) {
			throw new Exception("Directory location not found" + sourceDir.getAbsolutePath());
		}
		if (!destinationDir.exists()) {
			throw new Exception("Directory location not found" + sourceDir.getAbsolutePath());
		}

		this.sourceDir = sourceDir;
		this.destinationDir = destinationDir;
		this.zipFileName = zipFileName;

		generateFileList(this.sourceDir);
		zipIt(this.destinationDir.getAbsolutePath() + File.separator + this.zipFileName);
	}

	/**
	 * Zip it
	 * 
	 * @param zipFile
	 *            output ZIP file location
	 */
	private void zipIt(String zipFile) {

		byte[] buffer = new byte[1024];

		try {

			FileOutputStream fos = new FileOutputStream(zipFile);
			ZipOutputStream zos = new ZipOutputStream(fos);

			System.out.println("Output to Zip : " + zipFile);

			for (String file : this.fileList) {

				System.out.println("File Added : " + file);
				ZipEntry ze = new ZipEntry(file);
				zos.putNextEntry(ze);

				FileInputStream in = new FileInputStream(sourceDir.getAbsolutePath() + File.separator + file);

				int len;
				while ((len = in.read(buffer)) > 0) {
					zos.write(buffer, 0, len);
				}

				in.close();
			}

			zos.closeEntry();
			// remember close it
			zos.close();

			System.out.println("Done");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Traverse a directory and get all files, and add the file into fileList
	 * 
	 * @param node
	 *            file or directory
	 */
	private void generateFileList(File node) {

		// add file only
		if (node.isFile()) {
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
		}

		if (node.isDirectory()) {
			String[] subNote = node.list();
			for (String filename : subNote) {
				generateFileList(new File(node, filename));
			}
		}

	}

	/**
	 * Format the file path for ZIP
	 * 
	 * @param file
	 *            file path
	 * @return Formatted file path
	 */
	private String generateZipEntry(String file) {
		return file.substring(sourceDir.getAbsolutePath().length() + 1, file.length());
	}
}
