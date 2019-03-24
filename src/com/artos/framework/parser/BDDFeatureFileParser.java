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
package com.artos.framework.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.artos.framework.infra.BDDFeature;
import com.artos.framework.infra.BDDScenario;
import com.artos.framework.infra.BDDStep;
import com.google.common.collect.Lists;

/**
 * Feature File Parser
 * 
 * @author Arpit Shah
 *
 */
public class BDDFeatureFileParser {

	File featureFile;
	List<String> unitGroupList;
	BDDScenario scenario = null;
	List<String> currentTableHeader = null;
	List<BDDScenario> scenarioList;
	BDDFeature feature;
	boolean globalTable = false;

	/**
	 * Responsible for parsing feature file.
	 * 
	 * @param featureFile file with extension ".feature"
	 * @param unitGroupList list of groups which required to be executed
	 */
	public BDDFeatureFileParser(File featureFile, List<String> unitGroupList) {
		// Create empty scenario list
		scenarioList = new ArrayList<>();

		// Add scenarioList to Feature
		feature = new BDDFeature();
		feature.setScenarios(scenarioList);

		// Parse File
		this.featureFile = featureFile;
		this.unitGroupList = unitGroupList;
		parse();
	}

	private void parse() {

		try (BufferedReader br = new BufferedReader(new FileReader(featureFile))) {
			String previousLine = "";
			String currentLine = "";
			while ((currentLine = br.readLine()) != null) {
				currentLine = currentLine.trim();

				// Skip empty lines or comments
				if (currentLine.isEmpty() || currentLine.startsWith("#")) {
					continue;
				}

				// Process Scenario Description
				// "Scenario: " indicates that Scenario starts from current line
				// "Scenario Outline: " indicates that Scenario with an example table starts from current line (Artos treat this same as "Scenario: ")
				// "Background: " indicates that given scenario should be executed prior to each scenarios.
				if (currentLine.startsWith("Scenario: ") || currentLine.startsWith("Scenario Outline: ") || currentLine.startsWith("Background: ")) {

					// Create new Scenario object
					scenario = new BDDScenario();
					// Set Empty data-table HashMap to avoid null pointer exception
					scenario.setGlobalDataTable(new LinkedHashMap<>());

					// globalTable indicates if provided table is an ExampleTable, reset variable when new scenario starts
					globalTable = false;

					// Remove Unwanted text
					String scenarioDescription = currentLine.replaceFirst("Scenario Outline: ", "");
					scenarioDescription = scenarioDescription.replaceFirst("Scenario: ", "");
					scenarioDescription = scenarioDescription.replaceFirst("Background: ", "");
					scenario.setScenarioDescription(scenarioDescription);

					// Every test belongs to * group
					scenario.setGroupList(Lists.newArrayList("*"));

					// Look for specified Groups
					if (previousLine.startsWith("@")) {
						List<String> groupList = Arrays.asList(previousLine.split(" "));
						// Trim all element
						groupList.replaceAll(String::trim);
						// Store groups as upper case to avoid case in-sensitivity
						groupList.replaceAll(String::toUpperCase);

						groupList.replaceAll(s -> s.replaceAll("@", ""));
						// remove all empty string from list
						groupList.removeIf(item -> item == null);
						// Append all the list to existing list
						scenario.getGroupList().addAll(groupList);
					}

					// Background Scenario runs before each non-background scenarios, store it separately
					if (isBackground(currentLine)) {
						// set scenario background flag
						scenario.setBackground(true);
						// store scenario object as a background object
						feature.setBackground(scenario);
					} else {
						if (belongsToApprovedGroup(unitGroupList, scenario.getGroupList())) {

							// If present, Add background before each scenario
							if (null != feature.getBackground()) {
								scenarioList.add(feature.getBackground());
							}

							// Add scenario to list
							scenarioList.add(scenario);
						}
					}
				} else if (currentLine.startsWith("Examples:")) {
					globalTable = true;
				} else if (!currentLine.startsWith("|")) {

					// Find keyword (Given, And, then, When, But)
					String keyWord = currentLine.contains(" ") ? currentLine.split(" ")[0] : currentLine;

					if (isGherikinKeyWord(keyWord)) {

						// Remove keyword and isolate the step description
						String stepDescription = currentLine.replaceFirst(keyWord, "").trim();

						// create new step and add to list
						BDDStep step = new BDDStep(keyWord, stepDescription, new LinkedHashMap<>());
						scenario.getSteplist().add(step);

						// Find all words between quotes and store them in a list for later use
						Matcher m = Pattern.compile("\\\"(.*?)\\\"").matcher(stepDescription);
						while (m.find()) {
							step.getInlineParameterList().add(m.group(1));

							// If any data values have <> then flag as global reference
							// global reference can be in-line or in localDataTable
							if (m.group(1).startsWith("<") && m.group(1).endsWith(">")) {
								step.setHasGlobalReference(true);
							}
						}
					}
				} else if (currentLine.startsWith("|")) {

					// Remove first char so we can split correctly
					String tableLine = currentLine.substring(1, currentLine.length());
					// Split table columns
					List<String> tableData = Arrays.asList(tableLine.split("\\|"));
					// Trim each item within a table
					tableData.replaceAll(String::trim);

					// If previous line has pipe then this must be second line or later
					// If previous line does not have pipe then it must be header row
					if (!previousLine.startsWith("|")) {
						setCurrentTableHeader(tableData);
					} else {
						updateTableData(tableData);
					}
				}

				// Store previous line for later use
				previousLine = currentLine;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Checks if provided keyword belongs to Gherkin keyword. GIVEN, AND, WHEN, THEN, BUT are considered to be the keywords
	 * 
	 * @param keyWord string keyword
	 * @return true if keyword is Gherkin keyword, otherwise false
	 */
	private boolean isGherikinKeyWord(String keyWord) {
		// Make keyword upper-case for easy comparison
		String keyWordUpper = keyWord.trim().toUpperCase();
		if (keyWordUpper.equals("GIVEN") || keyWordUpper.equals("AND") || keyWordUpper.equals("WHEN") || keyWordUpper.equals("THEN")
				|| keyWordUpper.equals("BUT")) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if provided line is providing feature background
	 * 
	 * @param currentLine String line of feature file
	 * @return true if line indicates a BackGround, otherwise false
	 */
	private boolean isBackground(String currentLine) {
		if (currentLine.startsWith("Background: ")) {
			return true;
		}
		return false;
	}

	/**
	 * Preserves Headers to map it to HashMap<headerTag, headerValueList>. Each table values are represented as a ArrayList<String> which is stored in
	 * HashMap against a key which is a table header tag
	 * 
	 * <PRE>
	 * Table Data is stored against a HashMap Key (which is a column header)
	 * | header1 | header2 | header3 |
	 * | data 1	 | data 1  | data 1  |
	 * | data 2	 | data 2  | data 2  |
	 * </PRE>
	 * 
	 * @param tableData split table row using pipe (|) delimiter
	 */
	private void setCurrentTableHeader(List<String> tableData) {

		// Preserve header as it is used for HashMap key
		currentTableHeader = tableData;

		for (String s : currentTableHeader) {

			// If this is a global table then store it within a scenario
			if (globalTable) {

				// Add new key to table if similar key is not available
				if (!scenario.getGlobalDataTable().containsKey(s)) {
					scenario.getGlobalDataTable().put(s, new ArrayList<>());
				}

			} else { // If local table then store it within a step

				// Find last step from step list
				List<BDDStep> stepList = scenario.getSteplist();
				BDDStep step = stepList.get(stepList.size() - 1);

				// Add new key to table if similar key is not available
				if (!step.getLocalDataTable().containsKey(s)) {
					step.getLocalDataTable().put(s, new ArrayList<>());
				}

			}
		}
	}

	/**
	 * <PRE>
	 * Table Data is stored against a HashMap Key (which is a column header)
	 * | header1 | header2 | header3 |
	 * | data 1	 | data 1  | data 1  |
	 * | data 2	 | data 2  | data 2  |
	 * </PRE>
	 * 
	 * @param tableData split table row using pipe (|) delimiter
	 */
	private void updateTableData(List<String> tableData) {
		for (int i = 0; i < currentTableHeader.size(); i++) {
			if (globalTable) {

				// Find arrayList attached to table header
				List<String> list = scenario.getGlobalDataTable().get(currentTableHeader.get(i));

				// Add values against table
				list.add(tableData.get(i));
			} else {
				// Find last step
				List<BDDStep> stepList = scenario.getSteplist();
				BDDStep step = stepList.get(stepList.size() - 1);

				List<String> list = step.getLocalDataTable().get(currentTableHeader.get(i));
				String dataValue = tableData.get(i);
				list.add(dataValue);

				// If any data values have <> then we flag it for later use
				if (dataValue.startsWith("<") && dataValue.endsWith(">")) {
					step.setHasGlobalReference(true);
				}
			}
		}
	}

	/**
	 * Returns {@code BDDFeature} object
	 * 
	 * @return {@code BDDFeature} object
	 * @see BDDFeature
	 */
	public BDDFeature getFeature() {
		return feature;
	}

	/**
	 * Validate if test scenario belongs to any user defined group(s)
	 * 
	 * @param refGroupList list of user defined group (list is made up of group name or regular expression)
	 * @param testGroupList list of group test case belong to
	 * @return true if test case belongs to at least one of the user defined groups, false if test case does not belong to any user defined groups
	 */
	private boolean belongsToApprovedGroup(List<String> refGroupList, List<String> testGroupList) {

		if (null != refGroupList && null != testGroupList) {

			// Check if string matches
			if (refGroupList.stream().anyMatch(num -> testGroupList.contains(num))) {
				return true;
			} else {
				// Check if group matches regular expression
				for (String refGroup : refGroupList) {
					for (String testcaseGroup : testGroupList) {
						if (testcaseGroup.matches(refGroup)) {
							return true;
						}
					}
				}
			}
		}
		return false;

	}
}
