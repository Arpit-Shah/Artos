// Copyright <2018> <Artos>

// Permission is hereby granted, free of charge, to any person obtaining a copy of this software
// and associated documentation files (the "Software"), to deal in the Software without restriction,
// including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
// and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
// WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE
// OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
package com.artos.framework.infra;

import java.io.PrintWriter;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.artos.framework.FWStatic_Store;
import com.artos.framework.Version;

/** Process Command Line arguments */
public class CliProcessor {

	/**
	 * Processes command line arguments
	 * 
	 * @param args
	 *            Command line arguments
	 */
	public static void proessCommandLine(String[] args) {

		if (args.length <= 0) {
			return;
		}

		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// Create Option
		// Option subDir =
		// Option.builder("s").required(false).longOpt("subdir").desc("use given
		// name as log sub directory name Example : --subdir=test")
		// .hasArg().build();
		Option version = Option.builder("v").required(false).longOpt("version").desc("Version of artos test tool").build();
		Option help = Option.builder("h").required(false).longOpt("help").desc("Command line help. Please visit www.artos.com for more info").build();
		Option contributors = Option.builder("c").required(false).longOpt("contributors").desc("Project Contributors name").build();

		// Add Option
		Options options = new Options();
		options.addOption(help);
		// options.addOption(subDir);
		options.addOption(version);
		options.addOption(contributors);

		// Process Options
		try {
			// parse the command line arguments
			CommandLine line = parser.parse(options, args);

			// validate that block-size has been set
			if (line.hasOption("version")) {
				PrintWriter pw = new PrintWriter(System.out);
				System.out.println("Artots version : \"" + Version.id() + "\"");
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
			// If command line argument is provided then change logging
			// sub-directory name
			// if (line.hasOption("subdir")) {
			// PrintWriter pw = new PrintWriter(System.out);
			// FWStatic_Store.context.getFrameworkConfig().setLogSubDir(line.getOptionValue("subdir"));
			// pw.flush();
			// }
		} catch (ParseException exp) {
			// Do not do anything, just continue
			System.out.println(exp.getMessage());
		}
	}

}
