package com.arpitos.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class UtilsFile {

	/**
	 * Copy Directory from source to destination location
	 * 
	 * @param src
	 *            source file location
	 * @param dest
	 *            destination file location
	 * @param overwrite
	 *            enable overwrite true|false
	 */
	public static void copyDir(String src, String dest, boolean overwrite) {
		try {
			Files.walk(Paths.get(src)).forEach(a -> {
				Path b = Paths.get(dest, a.toString().substring(src.length()));
				try {
					if (!a.toString().equals(src))
						Files.copy(a, b, overwrite ? new CopyOption[] { StandardCopyOption.REPLACE_EXISTING } : new CopyOption[] {});
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		} catch (IOException e) {
			// permission issue
			e.printStackTrace();
		}
	}

	/**
	 * Copy File from source to destination location
	 * 
	 * @param source
	 *            source file location
	 * @param dest
	 *            destination file location
	 * @param overwrite
	 *            enable overwrite true|false
	 * @throws Exception
	 *             if file io fails
	 */
	public static void copyFile(File source, File dest, boolean overwrite) throws Exception {
		Files.copy(source.toPath(), dest.toPath(), overwrite ? new CopyOption[] { StandardCopyOption.REPLACE_EXISTING } : new CopyOption[] {});
	}

}
