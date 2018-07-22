// Copyright <2018> <Arpitos>

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
package com.arpitos.interfaces;

import java.util.ArrayList;

/**
 * 
 * @author ArpitS
 *
 */
public interface TestRunnable {

	/**
	 * Execute the tests in testList sequentially
	 * 
	 * @param testList
	 *            List of TestExecutors to run
	 * @param loopCount
	 *            Number of times each TestExecutor will run
	 * @throws Exception
	 *             exceptions that happened within the tests
	 */
	public void executeTest(ArrayList<TestExecutable> testList, Class<?> cls, String serialNumber, int loopCount) throws Exception;
}
