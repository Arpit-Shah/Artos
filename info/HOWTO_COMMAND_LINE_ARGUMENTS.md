# Artos command line parameters

Artos command line parameters are optional

| Keyword					| Description 									|
|---------------------------|-----------------------------------------------|
|`-h,--help `    			|Command line help						       	|
|`-v,--version`				|Artos version						       	   	|
|`-c,--contributors` 		|Project Contributors					       	|
|`-t,--testscript <arg>` 	|xml base testscript path				       	|

>Example 1: User can see help menu by requesting following via command line:
>>`--help`
>>OR
>>`-h`

>Example 2: User can pass multiple parameter(s) by passing multiple parameter(s) via command line:
>>`--version --contributors`
>>OR
>>`-v -c"`

>Example 3: User can pass Test Script as shown below:
>>`--testscript="./script/sampletest.xml"`
>>OR
>>`-t="./script/sampletest.xml"`