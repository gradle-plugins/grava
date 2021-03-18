package dev.gradleplugins.grava.testing.util;

import dev.gradleplugins.grava.testing.TestCase;
import org.junit.jupiter.api.DynamicTest;

public final class TestCaseUtils {
	private TestCaseUtils() {}

	public static DynamicTest toJUnit5DynamicTest(TestCase testCase) {
		return DynamicTest.dynamicTest(testCase.getDisplayName(), () -> {
			testCase.setUp();
			try {
				testCase.execute();
			} finally {
				testCase.tearDown();
			}
		});
	}
}
