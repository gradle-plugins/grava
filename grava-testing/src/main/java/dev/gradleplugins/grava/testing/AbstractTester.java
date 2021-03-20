package dev.gradleplugins.grava.testing;

import lombok.val;
import org.opentest4j.MultipleFailuresError;
import org.opentest4j.TestAbortedException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class AbstractTester {
	protected final List<TestCase> getTesters() {
		val result = new ArrayList<TestCase>();
		collectTesters(result);
		return result;
	}

	protected abstract void collectTesters(List<TestCase> testers);

	protected final void executeAllTestCases() {
		val failures = new ArrayList<TestCaseFailure>();
		stream().forEach(testCase -> {
			try {
				testCase.setUp();
				try {
					testCase.execute();
				} finally {
					testCase.tearDown();
				}
			} catch (TestAbortedException ex) {
				// ignore test
			} catch (Throwable throwable) {
				failures.add(new TestCaseFailure(testCase.getDisplayName(), throwable));
			}
		});
		if (!failures.isEmpty()) {
			throw new MultipleFailuresError("Plugin is not well-behaved", failures);
		}
	}

	private static final class TestCaseFailure extends RuntimeException {
		public TestCaseFailure(String displayName, Throwable throwable) {
			super(displayName, throwable);
		}
	}

	public final Stream<TestCase> stream() {
		return getTesters().stream();
	}
}
