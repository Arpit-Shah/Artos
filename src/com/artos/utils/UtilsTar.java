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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.IOUtils;

public class UtilsTar {

	/**
	 * Tar File
	 * 
	 * @param source = Source File object
	 * @param destFile = Tar destination File object
	 * @throws IOException If IO operation failed
	 */
	public static void tar(File source, File destFile) throws IOException {
		List<File> files = new ArrayList<File>();
		files.add(source);
		tar(files, destFile);
	}

	/**
	 * Tar list of files
	 * 
	 * @param files = List of files to be tarred
	 * @param destFile Tar destination File object
	 * @throws IOException If IP operation failed
	 */
	public static void tar(List<File> files, File destFile) throws IOException {
		try (TarArchiveOutputStream out = getTarArchiveOutputStream(destFile)) {
			for (File file : files) {
				addToArchiveCompression(out, file, destFile.getName());
			}
		}
	}

	/**
	 * Tar and Gzip file
	 * 
	 * @param source = Source file which requires Tarring and Gzipping
	 * @param destFile Destination location of tgz file
	 * @throws IOException If IO operation failed
	 */
	public static void tarGZ(File source, File destFile) throws IOException {
		List<File> files = new ArrayList<File>();
		files.add(source);
		tarGZ(files, destFile);
	}

	/**
	 * Tar and Gzip List of Files
	 * 
	 * @param files = List of files which requires Tarring and Gzipping
	 * @param destFile Destination location of tgz file
	 * @throws IOException if an I/O error has occurred
	 */
	public static void tarGZ(List<File> files, File destFile) throws IOException {
		try (TarArchiveOutputStream out = getTgzArchiveOutputStream(destFile)) {
			for (File file : files) {
				addToArchiveCompression(out, file, destFile.getName());
			}
		}
	}

	/**
	 * Untar file
	 * 
	 * @param tarFile tar File object which requires untaring
	 * @param destFile destination File object
	 * @throws IOException if an I/O error has occurred
	 */
	public static void untar(File tarFile, File destFile) throws Exception {
		FileInputStream fis = new FileInputStream(tarFile);
		TarArchiveInputStream tis = new TarArchiveInputStream(fis);
		TarArchiveEntry tarEntry = null;

		// tarIn is a TarArchiveInputStream
		while ((tarEntry = tis.getNextEntry()) != null) {
			File outputFile = new File(destFile + File.separator + tarEntry.getName());

			if (tarEntry.isDirectory()) {
				// System.out.println("outputFile Directory ---- " +
				// outputFile.getAbsolutePath());
				if (!outputFile.exists()) {
					outputFile.mkdirs();
				}
			} else {
				// File outputFile = new File(destFile + File.separator +
				// tarEntry.getName());
				// System.out.println("outputFile File ---- " +
				// outputFile.getAbsolutePath());
				outputFile.getParentFile().mkdirs();
				// outputFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(outputFile);
				IOUtils.copy(tis, fos);
				fos.close();
			}
		}
		tis.close();
	}

	/**
	 * Untar GZIP file
	 * 
	 * @param tarFile tar File object which requires untaring
	 * @param destFile destination File object
	 * @throws IOException if an I/O error has occurred
	 */
	public static void untarGZIP(File tarFile, File destFile) throws Exception {
		FileInputStream fis = new FileInputStream(tarFile);
		GzipCompressorInputStream gzipis = new GzipCompressorInputStream(new BufferedInputStream(fis));
		TarArchiveInputStream tis = new TarArchiveInputStream(gzipis);
		TarArchiveEntry tarEntry = null;

		// tarIn is a TarArchiveInputStream
		while ((tarEntry = tis.getNextEntry()) != null) {
			File outputFile = new File(destFile + File.separator + tarEntry.getName());

			if (tarEntry.isDirectory()) {
				// System.out.println("outputFile Directory ---- " +
				// outputFile.getAbsolutePath());
				if (!outputFile.exists()) {
					outputFile.mkdirs();
				}
			} else {
				// File outputFile = new File(destFile + File.separator +
				// tarEntry.getName());
				// System.out.println("outputFile File ---- " +
				// outputFile.getAbsolutePath());
				outputFile.getParentFile().mkdirs();
				// outputFile.createNewFile();
				FileOutputStream fos = new FileOutputStream(outputFile);
				IOUtils.copy(tis, fos);
				fos.close();
			}
		}
		tis.close();
	}

	private static TarArchiveOutputStream getTarArchiveOutputStream(File file) throws IOException {
		TarArchiveOutputStream taos = new TarArchiveOutputStream(new FileOutputStream(file));
		// TAR has an 8 gig file limit by default, this gets around that
		taos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
		// TAR originally didn't support long file names, so enable the support
		// for it
		taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
		taos.setAddPaxHeadersForNonAsciiNames(true);
		return taos;
	}

	private static TarArchiveOutputStream getTgzArchiveOutputStream(File file) throws IOException {
		TarArchiveOutputStream taos = new TarArchiveOutputStream(new GzipCompressorOutputStream(new FileOutputStream(file)));
		// TAR has an 8 gig file limit by default, this gets around that
		taos.setBigNumberMode(TarArchiveOutputStream.BIGNUMBER_STAR);
		// TAR originally didn't support long file names, so enable the support
		// for it
		taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
		taos.setAddPaxHeadersForNonAsciiNames(true);
		return taos;
	}

	private static void addToArchiveCompression(TarArchiveOutputStream out, File sourceFile, String dir) throws IOException {
		String entry = dir + File.separator + sourceFile.getName();
		if (sourceFile.isFile()) {
			out.putArchiveEntry(new TarArchiveEntry(sourceFile, entry));
			try (FileInputStream in = new FileInputStream(sourceFile)) {
				IOUtils.copy(in, out);
			}
			out.closeArchiveEntry();
		} else if (sourceFile.isDirectory()) {
			File[] children = sourceFile.listFiles();
			if (children != null) {
				for (File child : children) {
					addToArchiveCompression(out, child, entry);
				}
			}
		} else {
			System.out.println(sourceFile.getName() + " is not supported");
		}
	}

	/**
	 * Method to decompress a gzip file
	 *
	 * @param gZippedFile gzipped File object
	 * @param tarFile destination Tar File object
	 * @return Tar File object
	 * @throws IOException if an I/O error has occurred
	 */
	public File deCompressGZipFile(File gZippedFile, File tarFile) throws Exception {
		FileInputStream fis = new FileInputStream(gZippedFile);
		GZIPInputStream gZIPInputStream = new GZIPInputStream(fis);

		FileOutputStream fos = new FileOutputStream(tarFile);
		byte[] buffer = new byte[1024];
		int len;
		while ((len = gZIPInputStream.read(buffer)) > 0) {
			fos.write(buffer, 0, len);
		}

		fos.close();
		gZIPInputStream.close();

		return tarFile;
	}

}
