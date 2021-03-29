package dev.gradleplugins.grava.testing;

import dev.gradleplugins.grava.testing.fixtures.WellBehavedTestPlugin;
import dev.gradleplugins.grava.testing.util.TestCaseUtils;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

class JUnit5TestFactoryWellBehavedPluginTesterIntegrationTest {
	@TestFactory
	Stream<DynamicTest> canUseTesterWithJUnit5TestFactory() {
		return new WellBehavedPluginTester().pluginClass(WellBehavedTestPlugin.class).qualifiedPluginId("dev.gradleplugins.gravatesting.well-behaved-plugin").stream().map(TestCaseUtils::toJUnit5DynamicTest);
	}
}
