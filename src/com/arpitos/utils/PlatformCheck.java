package com.arpitos.utils;

public class PlatformCheck {

	private static String OS = System.getProperty("os.name").toLowerCase();
	
	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static boolean isWindows10() {
		return OS.equals("windows 10");
	}

	public static boolean isMac() {
		return (OS.indexOf("mac") >= 0);
	}

	public static boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0);
	}

	public static boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0);
	}

	public static String getOS() {
		return OS;
	}
}
