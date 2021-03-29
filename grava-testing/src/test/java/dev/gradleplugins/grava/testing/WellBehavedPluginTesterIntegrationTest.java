package dev.gradleplugins.grava.testing;

import dev.gradleplugins.grava.testing.fixtures.ThrowingTestPlugin;
import dev.gradleplugins.grava.testing.fixtures.WellBehavedTestPlugin;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.opentest4j.MultipleFailuresError;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WellBehavedPluginTesterIntegrationTest {
	@Test
	void failsIfPluginIdPointsToMissingClass() {
		val ex = assertThrows(MultipleFailuresError.class,
			new WellBehavedPluginTester().qualifiedPluginId("dev.gradleplugins.gravatesting.missing-class").pluginClass(WellBehavedTestPlugin.class)::testWellBehavedPlugin);
		assertThat(ex.getMessage(), allOf(startsWith("Plugin is not well-behaved"), containsString("can apply plugin by id using apply(plugin: <id>)")));
	}

	@Test
	void failsIfPluginThrowsException() {
		val ex = assertThrows(MultipleFailuresError.class,
			new WellBehavedPluginTester().pluginClass(ThrowingTestPlugin.class)::testWellBehavedPlugin);
		assertThat(ex.getMessage(), allOf(startsWith("Plugin is not well-behaved"), containsString("can apply plugin by id using apply(plugin: <class>)")));
	}
}
