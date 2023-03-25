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
package com.artos.framework.infra;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.artos.framework.FWStaticStore;

/**
 * Process Command Line arguments
 * 
 * @author ArpitShah
 *
 */
public class CliProcessor {

	private static File testScriptFile = null;
	private static String profile = null;

	/**
	 * Default constructor
	 */
	public CliProcessor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * Processes command line arguments
	 * 
	 * @param args Command line arguments
	 */
	protected static void proessCommandLine(String[] args) {

		if (args.length <= 0) {
			return;
		}

		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// Create Option
		Option testScript = Option.builder("t").required(false).longOpt("testscript")
				.desc("use test script to drive test : --testscript=\"./scripts/sampletest.xml\"").hasArg().build();
		Option version = Option.builder("v").required(false).longOpt("version").desc("Version of artos").build();
		Option help = Option.builder("h").required(false).longOpt("help")
				.desc("Command line help. Please visit www.theartos.com for more info").build();
		Option contributors = Option.builder("c").required(false).longOpt("contributors")
				.desc("Project Contributors name").build();
		Option testProfile = Option.builder("p").required(false).longOpt("profile").desc("Test Configuration Profile")
				.hasArg().build();

		// Add Option
		Options options = new Options();
		options.addOption(testScript);
		options.addOption(version);
		options.addOption(help);
		options.addOption(contributors);
		options.addOption(testProfile);

		// Process Options
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			// validate that block-size has been set
			if (line.hasOption("version")) {
				PrintWriter pw = new PrintWriter(System.out);
				System.out.println("Artos version : \"" + FWStaticStore.ARTOS_BUILD_VERSION + "\"");
				pw.flush();
			}
			if (line.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("artos", options);
			}
			if (line.hasOption("contributors")) {
				PrintWriter pw = new PrintWriter(System.out);
				System.out.println("Project Contributors : " + "Arpit_Shah, Shobhit_Bhatnagar, Swapna_Soni");
				pw.flush();
			}
			if (line.hasOption("profile")) {
				profile = line.getOptionValue("profile");
			}
			// Store test script path if provided
			if (line.hasOption("testscript")) {
				PrintWriter pw = new PrintWriter(System.out);
				// TestScript path should be "./script/testscript.xml"
				String testScriptPath = "." + File.separator + "script" + File.separator
						+ line.getOptionValue("testscript");
				File tscriptFile = new File(testScriptPath);
				if (tscriptFile.exists() && tscriptFile.isFile()) {
					testScriptFile = tscriptFile;
					System.err.println("TestScript found : " + tscriptFile.getAbsolutePath());
					pw.flush();
				} else {
					System.err.println("TestScript missing : " + tscriptFile.getAbsolutePath());
					pw.flush();
					System.exit(1);
				}

			}
		} catch (ParseException exp) {
			// Do not do anything, just continue
			System.out.println(exp.getMessage());
		}
	}

	/**
	 * Return Test script file
	 * 
	 * @return test script file
	 */
	public static File getTestScriptFile() {
		return testScriptFile;
	}

	/**
	 * Set test script file
	 * 
	 * @param testScriptFile test script file
	 */
	protected static void setTestScriptFile(File testScriptFile) {
		CliProcessor.testScriptFile = testScriptFile;
	}

	/**
	 * Get profile value
	 * 
	 * @return profile value
	 */
	public static String getProfile() {
		return profile;
	}

	/**
	 * Set profile value
	 * 
	 * @param profile profile value
	 */
	protected static void setProfile(String profile) {
		CliProcessor.profile = profile;
	}

}
