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

import java.util.ArrayList;
import java.util.List;

/**
 * BDDFeature Pojo
 * 
 * @author ArpitShah
 *
 */
public class BDDFeature {

	List<BDDScenario> Scenarios = new ArrayList<>();
	BDDScenario background = null;

	/**
	 * Default Constructor
	 */
	public BDDFeature() {
	}

	/**
	 * Returns the list of {@link BDDScenario} objects
	 * 
	 * @return List of {@link BDDScenario}
	 */
	public List<BDDScenario> getScenarios() {
		return Scenarios;
	}

	/**
	 * Sets the list of {@link BDDScenario} objects
	 * 
	 * @param scenarios List of {@link BDDScenario}
	 */
	public void setScenarios(List<BDDScenario> scenarios) {
		Scenarios = scenarios;
	}

	/**
	 * Returns the {@link BDDScenario} object representing the background for this feature.
	 * 
	 * @return {@link BDDScenario}
	 */
	public BDDScenario getBackground() {
		return background;
	}

	/**
	 * Sets the {@link BDDScenario} object representing the background for this feature.
	 * 
	 * @param background {@link BDDScenario}
	 */
	public void setBackground(BDDScenario background) {
		this.background = background;
	}

}
