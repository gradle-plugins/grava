package dev.gradleplugins.grava.testing;

import dev.gradleplugins.grava.testing.util.TestCaseUtils;
import org.gradle.api.Plugin;
import org.gradle.api.initialization.Settings;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.stream.Stream;

import static dev.gradleplugins.grava.testing.WellBehavedPluginTester.SupportedTarget.Settings;

class WellBehavedSettingsPluginTesterIntegrationTest {
	@TestFactory
	Stream<DynamicTest> checkWellBehavedPlugin() {
		return new WellBehavedPluginTester()
			.pluginClass(SettingsPlugin.class)
			.supportedTarget(Settings)
			.doesNotWellBehaveWhenAppliedToUnsupportedTarget()
			.stream().map(TestCaseUtils::toJUnit5DynamicTest);
	}

	public static class SettingsPlugin implements Plugin<Settings> {
		@Override
		public void apply(Settings target) {}
	}
}
