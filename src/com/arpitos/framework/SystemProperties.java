package com.arpitos.framework;

import org.apache.logging.log4j.Logger;

/**
 * This class is a contained which holds all System properties
 * 
 * @author arpit
 *
 */
public class SystemProperties {

	private final String JavaRuntimeEnvironmentVersion = System.getProperty("java.version");
	private final String JavaVirtualMachineSpecificationVersion = System.getProperty("java.vm.specification.version");
	private final String JavaRuntimeEnvironmentSpecificationVersion = System.getProperty("java.specification.version");
	private final String JavaClassPath = System.getProperty("java.class.path");
	private final String ListofPathstoSearchWhenLoadingLibraries = System.getProperty("java.library.path");
	private final String OperatingSystemName = System.getProperty("os.name");
	private final String OperatingSystemArchitecture = System.getProperty("os.arch");
	private final String OperatingSystemVersion = System.getProperty("os.version");
	private final String FileSeparator = System.getProperty("file.separator");
	private final String PathSeparator = System.getProperty("path.separator");
	private final String UserAccountName = System.getProperty("user.name");
	private final String UserHomeDir = System.getProperty("user.home");
	private final String JavaInstallationDirectory = System.getProperty("java.home");
	private final String JavaVirtualMachineSpecificationVendor = System.getProperty("java.vm.specification.vendor");
	private final String JavaVirtualMachineSpecificationName = System.getProperty("java.vm.specification.name");
	private final String JavaVirtualMachineImplementationVersion = System.getProperty("java.vm.version");
	private final String JavaVirtualMachineImplementationVendor = System.getProperty("java.vm.vendor");
	private final String JavaVirtualMachineImplementationName = System.getProperty("java.vm.name");
	private final String JavaRuntimeEnvironmentSpecificationVendor = System.getProperty("java.specification.vendor");
	private final String JavaRuntimeEnvironmentSpecificationName = System.getProperty("java.specification.name");
	private final String JavaClassFormatVersionNumber = System.getProperty("java.class.version");
	private final String DefaultTempFilePath = System.getProperty("java.io.tmpdir");
	private final String NameOfJITCompilerToUse = System.getProperty("java.compiler");
	private final String PathOfExtensionDir = System.getProperty("java.ext.dirs");
	private final String UserCurrentWorkingDirectory = System.getProperty("user.dir");

	/**
	 * Print selected System Info to log file
	 * 
	 * @param logger
	 */
	public void printUsefulInfo(Logger logger) {

		// @formatter:off
		
		logger.debug("\nTest FrameWork Info");
		logger.debug("* Arpitos version => " + Version.id());
		logger.debug("* Java Runtime Environment version => " + getJavaRuntimeEnvironmentVersion()); 
		logger.debug("* Java Virtual Machine specification version => " + getJavaVirtualMachineSpecificationVersion());
		logger.debug("* Java Runtime Environment specification version => " + getJavaRuntimeEnvironmentSpecificationVersion());
		logger.debug("* Java class path => " + getJavaClassPath());
		logger.debug("* List of paths to search when loading libraries => " + getListofPathstoSearchWhenLoadingLibraries());
		logger.debug("* Operating system name => " + getOperatingSystemName());
		logger.debug("* Operating system architecture => " + getOperatingSystemArchitecture());
		logger.debug("* Operating system version => " + getOperatingSystemVersion());
		logger.debug("* File separator (\"/\" on UNIX) => " + getFileSeparator());
		logger.debug("* Path separator (\":\" on UNIX) => " + getPathSeparator());
		logger.debug("* User's account name => " + getUserAccountName());
		logger.debug("* User's home directory => " + getUserHomeDir());
		
		// @formatter:on
	}

	public String getJavaRuntimeEnvironmentVersion() {
		return JavaRuntimeEnvironmentVersion;
	}

	public String getJavaVirtualMachineSpecificationVersion() {
		return JavaVirtualMachineSpecificationVersion;
	}

	public String getJavaRuntimeEnvironmentSpecificationVersion() {
		return JavaRuntimeEnvironmentSpecificationVersion;
	}

	public String getJavaClassPath() {
		return JavaClassPath;
	}

	public String getListofPathstoSearchWhenLoadingLibraries() {
		return ListofPathstoSearchWhenLoadingLibraries;
	}

	public String getOperatingSystemName() {
		return OperatingSystemName;
	}

	public String getOperatingSystemArchitecture() {
		return OperatingSystemArchitecture;
	}

	public String getOperatingSystemVersion() {
		return OperatingSystemVersion;
	}

	public String getFileSeparator() {
		return FileSeparator;
	}

	public String getPathSeparator() {
		return PathSeparator;
	}

	public String getUserAccountName() {
		return UserAccountName;
	}

	public String getUserHomeDir() {
		return UserHomeDir;
	}

	public String getJavaInstallationDirectory() {
		return JavaInstallationDirectory;
	}

	public String getJavaVirtualMachineSpecificationVendor() {
		return JavaVirtualMachineSpecificationVendor;
	}

	public String getJavaVirtualMachineSpecificationName() {
		return JavaVirtualMachineSpecificationName;
	}

	public String getJavaVirtualMachineImplementationVersion() {
		return JavaVirtualMachineImplementationVersion;
	}

	public String getJavaVirtualMachineImplementationVendor() {
		return JavaVirtualMachineImplementationVendor;
	}

	public String getJavaVirtualMachineImplementationName() {
		return JavaVirtualMachineImplementationName;
	}

	public String getJavaRuntimeEnvironmentSpecificationVendor() {
		return JavaRuntimeEnvironmentSpecificationVendor;
	}

	public String getJavaRuntimeEnvironmentSpecificationName() {
		return JavaRuntimeEnvironmentSpecificationName;
	}

	public String getJavaClassFormatVersionNumber() {
		return JavaClassFormatVersionNumber;
	}

	public String getDefaultTempFilePath() {
		return DefaultTempFilePath;
	}

	public String getNameOfJITCompilerToUse() {
		return NameOfJITCompilerToUse;
	}

	public String getPathOfExtensionDir() {
		return PathOfExtensionDir;
	}

	public String getUserCurrentWorkingDirectory() {
		return UserCurrentWorkingDirectory;
	}

}
