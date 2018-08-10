# Arots Test Sequence and Skipping

## Pass test execution list to test Runner
There are three options.

|Priority | Type 				|Method										|
|---------|-----------------|-----------------------------------------------|
|1        |XML Test Script	|pass xml test script via command line			|
|2        |Test List 		|pass `List<TestExecutable>` in Runner object	|
|3        |Reflection		|pass **empty** `List<TestExecutable>` in Runner obj|

## XML Test Script Method
* Test Sequence
	* Test will be executed in same sequence as provided in xml file.
* Skipping test cases
	*  Ommit test case name from xml file.
OR
	*  Comment out test name line.

## Test List Method
* Test Sequence
	* Test will be executed in same sequence as provided in test list.
* There are two ways test case can be skipped in Test List method
	*  Ommit test case name from list.
OR
	*  Comment out test case line.

## Reflection Method
* Test Sequence
	* Test will be executed using sequence provided in `@TestCase` attribute.
	* If same sequence number is provided then scan order will be used.
* Test cases can be skipped in Reflection method following way
	* Update `@TestCase` attribute and mark `skip = true`.

```
@TestCase(skip = false, sequence = 1, label = "abc")
@TestCase(skip = false, sequence = 1, label = "opq")
@TestCase(skip = false, sequence = 2, label = "xyz")
```