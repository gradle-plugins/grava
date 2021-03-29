package dev.gradleplugins.grava.testing;

import dev.gradleplugins.grava.testing.fixtures.WellBehavedTestPlugin;
import org.gradle.api.Plugin;
import org.gradle.api.invocation.Gradle;
import org.junit.jupiter.api.Test;

import static dev.gradleplugins.grava.testing.WellBehavedPluginTester.SupportedTarget.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class WellBehavedInitPluginTesterIntegrationTest {
	@Test
	void checkWellBehavedPlugin() {
		assertDoesNotThrow(new WellBehavedPluginTester()
			.pluginClass(InitPlugin.class)
			.supportedTarget(Init)
			.doesNotWellBehaveWhenAppliedToUnsupportedTarget()::testWellBehavedPlugin
		);
	}

	@Test
	void skipsQualifiedPluginIdChecksForInitTarget() {
		assertDoesNotThrow(new WellBehavedPluginTester()
			.qualifiedPluginId("dev.gradleplugins.gravatesting.well-behaved-plugin")
			.pluginClass(WellBehavedTestPlugin.class)
			.supportedTarget(Init, Project, Settings)::testWellBehavedPlugin);
	}

	public static class InitPlugin implements Plugin<Gradle> {
		@Override
		public void apply(Gradle target) {}
	}
}
