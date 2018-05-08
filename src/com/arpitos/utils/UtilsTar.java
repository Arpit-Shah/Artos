package com.arpitos.utils;

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
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class UtilsTar {

	public static void tar(File source, File destFile) throws IOException {
		List<File> files = new ArrayList<File>();
		files.add(source);
		tar(files, destFile);
	}

	public static void tar(List<File> files, File destFile) throws IOException {
		try (TarArchiveOutputStream out = getTarArchiveOutputStream(destFile)) {
			for (File file : files) {
				addToArchiveCompression(out, file, destFile.getName());
			}
		}
	}
	
	public static void tarGZ(File source, File destFile) throws IOException {
		List<File> files = new ArrayList<File>();
		files.add(source);
		tarGZ(files, destFile);
	}
	
	public static void tarGZ(List<File> files, File destFile) throws IOException {
		try (TarArchiveOutputStream out = getTgzArchiveOutputStream(destFile)) {
			for (File file : files) {
				addToArchiveCompression(out, file, destFile.getName());
			}
		}
	}

	/**
	 *
	 * @param tarFile
	 * @param destFile
	 * @throws IOException
	 */
	public static void untar(File tarFile, File destFile) throws Exception {
		FileInputStream fis = new FileInputStream(tarFile);
		TarArchiveInputStream tis = new TarArchiveInputStream(fis);
		TarArchiveEntry tarEntry = null;

		// tarIn is a TarArchiveInputStream
		while ((tarEntry = tis.getNextTarEntry()) != null) {
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
	 * @param gZippedFile
	 * @param tarFile
	 * @throws IOException
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
