package uk.ac.ed.inf.sdp.group4.test;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitResultFormatter;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitVersionHelper;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;

import java.io.*;
import java.text.NumberFormat;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Prints a single lines of tests to a specified Writer.
 * Inspired by the BriefJUnitResultFormatter and
 * XMLJUnitResultFormatter.
 *
 * @see FormatterElement
 * @see BriefJUnitResultFormatter
 * @see XMLJUnitResultFormatter
 */

public class OneLinerFormatter implements JUnitResultFormatter
{

	private final String TAB_STR = "    ";

	private final boolean showCausesLines = true;
	// (\w+\.)+(\w+)\((\w+).(?:\w+):(\d+)\)
	private final Pattern traceLinePattern = Pattern.compile("(\\w+\\.)+(\\w+)\\((\\w+).(?:\\w+):(\\d+)\\)");

	/**
	 * Where to write the log to.
	 */
	private OutputStream out;

	/**
	 * Used for writing the results.
	 */
	private PrintWriter output;

	/**
	 * Used as part of formatting the results.
	 */
	private StringWriter results;

	/**
	 * Used for writing formatted results to.
	 */
	private PrintWriter resultWriter;

	/**
	 * Formatter for timings.
	 */
	private NumberFormat numberFormat = NumberFormat.getInstance();

	/**
	 * Output suite has written to System.out
	 */
	private String systemOutput = null;

	/**
	 * Output suite has written to System.err
	 */
	private String systemError = null;

	/**
	 * tests that failed.
	 */
	private Hashtable<Test, Test> failedTests = new Hashtable<Test, Test>();
	/**
	 * Timing helper.
	 */
	private Hashtable<Test, Long> testStarts = new Hashtable<Test, Long>();

	/**
	 * Constructor for OneLinerFormatter.
	 */
	public OneLinerFormatter()
	{
		results = new StringWriter();
		resultWriter = new PrintWriter(results);
	}

	/**
	 * Sets the stream the formatter is supposed to write its results to.
	 * @param out the output stream to write to
	 */
	public void setOutput(OutputStream out)
	{
		this.out = out;
		output = new PrintWriter(out);
	}

	/**
	 * @see JUnitResultFormatter#setSystemOutput(String)
	 */
	public void setSystemOutput(String out)
	{
		systemOutput = out;
	}

	/**
	 * @see JUnitResultFormatter#setSystemError(String)
	 */
	public void setSystemError(String err)
	{
		systemError = err;
	}

	/**
	 * The whole testsuite started.
	 * @param suite the test suite
	 */
	public void startTestSuite(JUnitTest suite)
	{
		if (output == null)
		{
			return; // Quick return - no output do nothing.
		}
		StringBuffer sb = new StringBuffer();
		sb.append("----------------------------------------------------------");
		sb.append(StringUtils.LINE_SEP);
		sb.append("Testsuite: ");
		sb.append(suite.getName());
		sb.append(StringUtils.LINE_SEP);
		output.write(sb.toString());
		output.flush();
	}

	/**
	 * The whole testsuite ended.
	 * @param suite the test suite
	 */
	public void endTestSuite(JUnitTest suite)
	{
		StringBuffer sb = new StringBuffer("Tests run: ");
		sb.append(suite.runCount());
		sb.append(", Failures: ");
		sb.append(suite.failureCount());
		sb.append(", Errors: ");
		sb.append(suite.errorCount());
		sb.append(", Time elapsed: ");
		sb.append(numberFormat.format(suite.getRunTime() / 1000.0));
		sb.append(" sec");
		sb.append(StringUtils.LINE_SEP);
		sb.append(StringUtils.LINE_SEP);

		// append the err and output streams to the log
		if (systemOutput != null && systemOutput.length() > 0)
		{
			sb.append("------------- Standard Output ---------------")
			.append(StringUtils.LINE_SEP)
			.append(systemOutput)
			.append("------------- ---------------- ---------------")
			.append(StringUtils.LINE_SEP);
		}

		if (systemError != null && systemError.length() > 0)
		{
			sb.append("------------- Standard Error -----------------")
			.append(StringUtils.LINE_SEP)
			.append(systemError)
			.append("------------- ---------------- ---------------")
			.append(StringUtils.LINE_SEP);
		}

		if (output != null)
		{
			try
			{
				output.write(sb.toString());
				resultWriter.close();
				output.write(results.toString());
				output.flush();
			}
			finally
			{
				if (out != System.out && out != System.err)
				{
					FileUtils.close(out);
				}
			}
		}
	}

	/**
	 * A test started.
	 * @param test a test
	 */
	public void startTest(Test test)
	{
		testStarts.put(test, new Long(System.currentTimeMillis()));
	}

	/**
	 * A test ended.
	 * @param test a test
	 */
	public void endTest(Test test)
	{
		// Fix for bug #5637 - if a junit.extensions.TestSetup is
		// used and throws an exception during setUp then startTest
		// would never have been called
		if (!testStarts.containsKey(test))
		{
			startTest(test);
		}

		boolean failed = failedTests.containsKey(test);

		Long l = testStarts.get(test);

		String timeNow = ((System.currentTimeMillis() - l.longValue()) / 1000.0) + "] ";

		output.write("Ran [");
		output.write(timeNow);

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < Math.abs(getTestName(test).length() - 45 + timeNow.length()); i++)
		{
			sb.append(" ");
		}

		output.write(String.format("%s%s%s", getTestName(test), sb.toString(),
		                           (failed ? "\u001b[;31mFAILED\u001b[m" : "\u001b[;32mPASSED\u001b[m")));
		output.write(StringUtils.LINE_SEP);
		output.flush();
	}

	/**
	 * Interface TestListener for JUnit &lt;= 3.4.
	 *
	 * <p>A Test failed.
	 * @param test a test
	 * @param t    the exception thrown by the test
	 */
	public void addFailure(Test test, Throwable t)
	{
		formatError("\tFAILED", test, t);
	}

	/**
	 * Interface TestListener for JUnit &gt; 3.4.
	 *
	 * <p>A Test failed.
	 * @param test a test
	 * @param t    the assertion failed by the test
	 */
	public void addFailure(Test test, AssertionFailedError t)
	{
		addFailure(test, (Throwable) t);
	}

	/**
	 * A test caused an error.
	 * @param test  a test
	 * @param error the error thrown by the test
	 */
	public void addError(Test test, Throwable error)
	{
		formatError("\tCaused an ERROR", test, error);
	}

	/**
	 * Get test name
	 *
	 * @param test a test
	 * @return test name
	 */
	protected String getTestName(Test test)
	{
		if (test == null)
		{
			return "null";
		}
		else
		{
			return /* JUnitVersionHelper.getTestCaseClassName(test) + ": " + */
			    JUnitVersionHelper.getTestCaseName(test);
		}
	}

	/**
	 * Get test case full class name
	 *
	 * @param test a test
	 * @return test full class name
	 */
	protected String getTestCaseClassName(Test test)
	{
		if (test == null)
		{
			return "null";
		}
		else
		{
			return JUnitVersionHelper.getTestCaseClassName(test);
		}
	}

	/**
	 * Format the test for printing..
	 * @param test a test
	 * @return the formatted testname
	 */
	protected String formatTest(Test test)
	{
		if (test == null)
		{
			return "Null Test: ";
		}
		else
		{
			return "Testcase: " + test.toString() + ":";
		}
	}

	/**
	 * Format an error and print it.
	 * @param type the type of error
	 * @param test the test that failed
	 * @param error the exception that the test threw
	 */
	protected synchronized void formatError(String type, Test test,
	                                        Throwable error)
	{
		if (test != null)
		{
			failedTests.put(test, test);
			endTest(test);
		}

		resultWriter.println(formatTest(test) + type);
		resultWriter.println(TAB_STR + "(" + error.getClass().getSimpleName() + "): " +
		                     ((error.getMessage() != null) ? error.getMessage() : error));

		if (showCausesLines)
		{
			// resultWriter.append(StringUtils.LINE_SEP);
			resultWriter.println(filterErrorTrace(test, error));
		}

		resultWriter.println();

		/* String strace = JUnitTestRunner.getFilteredTrace(error);
		   resultWriter.println(strace);
		   resultWriter.println(); */
	}

	protected String filterErrorTrace(Test test, Throwable error)
	{
		String trace = StringUtils.getStackTrace(error);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		StringReader sr = new StringReader(trace);
		BufferedReader br = new BufferedReader(sr);

		String line;
		try
		{
			while ((line = br.readLine()) != null)
			{
				if (line.indexOf(getTestCaseClassName(test)) != -1)
				{
					Matcher matcher = traceLinePattern.matcher(line);
					// pw.println(matcher + ": " + matcher.find());
					if (matcher.find())
					{
						pw.print(TAB_STR);
						pw.print("(" + matcher.group(3) + ") ");
						pw.print(matcher.group(2) + ": ");
						pw.println(matcher.group(4));
					}
					else
					{
						pw.println(line);
					}

				}
			}
		}
		catch (Exception e)
		{
			return trace; // return the treca unfiltered
		}

		return sw.toString();

	}
}
