package com.arpitos.utils;

import java.io.File;

public class Tree {

	/**
	 * Print the directory tree and its file names.
	 * 
	 * @param folder
	 *            directory
	 * @return
	 */
	public static String printDirectoryTree(File dir) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("folder is not a Directory");
		}
		int indent = 0;
		StringBuilder sb = new StringBuilder();
		printDirectoryTree(dir, indent, sb);
		return sb.toString();
	}

	private static void printDirectoryTree(File dir, int indent, StringBuilder sb) {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("file is not a Directory");
		}
		sb.append(getIndentString(indent));
		sb.append("+--");
		sb.append(dir.getName());
		sb.append("/");
		sb.append("\n");
		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				printDirectoryTree(file, indent + 1, sb);
			} else {
				printFile(file, indent + 1, sb);
			}
		}

	}

	private static void printFile(File file, int indent, StringBuilder sb) {
		sb.append(getIndentString(indent));
		sb.append("+--");
		sb.append(file.getName());
		sb.append("\n");
	}

	private static String getIndentString(int indent) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < indent; i++) {
			sb.append("|  ");
		}
		return sb.toString();
	}
}
