package dev.gradleplugins.grava.testing.util;

import dev.gradleplugins.grava.testing.TestCase;
import org.junit.jupiter.api.DynamicTest;

public final class TestCaseUtils {
	private TestCaseUtils() {}

	/**
	 * Convert Grava test case to JUnit's dynamic test to use within a test factory scenario.
	 *
	 * <pre>
	 * class FooTest {
	 *     @TestFactory
	 *     Stream<DynamicTest> checkWellBehavedPlugin() {
	 *         return new WellBehavedPluginTester()
	 *             .pluginType(WellBehavedTestPlugin.class)
	 *             .qualifiedPluginId("gravatesting.well-behaved-plugin")
	 *             .stream()
	 *             .map(TestCaseUtils::toJUnit5DynamicTest);
	 *     }
	 * }
	 * </pre>
	 * @param testCase  a test case to convert into {@link DynamicTest}, must not be null
	 * @return a JUnit 5 dynamic test, never null
	 */
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
