package com.arpitos.framework.infra;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.arpitos.framework.FWStatic_Store;
import com.arpitos.framework.Version;

public class CliProcessor {

	public static boolean proessCommandLine(String[] args) {
		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// Create Option
		Option subDir = Option.builder("s").required(false).longOpt("subdir").desc("use given name as log sub directory name Example : --subdir=test")
				.hasArg().build();
		Option version = Option.builder("v").required(false).longOpt("version").desc("Version of arpitos test tool").build();
		Option help = Option.builder("h").required(false).longOpt("help").desc("Command line help. Please visit www.arpitos.com for more info")
				.build();
		Option contributors = Option.builder("c").required(false).longOpt("contributors").desc("Project Contributors name").build();

		// Add Option
		Options options = new Options();
		options.addOption(help);
		options.addOption(subDir);
		options.addOption(version);
		options.addOption(contributors);

		// Process Options
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			// validate that block-size has been set
			if (line.hasOption("version")) {
				PrintWriter pw = new PrintWriter(System.out);
				System.out.println("Arpiots version : \"" + Version.id() + "\"");
				pw.flush();
			}
			if (line.hasOption("help")) {
				// automatically generate the help statement
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("arpitos", options);
			}
			if (line.hasOption("contributors")) {
				PrintWriter pw = new PrintWriter(System.out);
				System.out.println("Project Contributors : " + "Arpit_Shah, Shobhit_Bhatnagar, Swapna_Soni");
				pw.flush();
			}
			// If command line argument is provided then change logging
			// sub-directory name
			if (line.hasOption("subdir")) {
				PrintWriter pw = new PrintWriter(System.out);
				FWStatic_Store.FWConfig.setLogSubDir(line.getOptionValue("subdir"));
				pw.flush();
			}

			return true;
		} catch (ParseException exp) {
			System.out.println(exp.getMessage());
		}
		return false;
	}

}
