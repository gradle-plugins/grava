package dev.gradleplugins.grava.testing;

import dev.gradleplugins.grava.testing.fixtures.ResolveTaskEarlyTestPlugin;
import dev.gradleplugins.grava.testing.fixtures.ThrowingTestPlugin;
import dev.gradleplugins.grava.testing.fixtures.WellBehavedTestPlugin;
import dev.gradleplugins.grava.testing.util.TestCaseUtils;
import lombok.val;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.opentest4j.MultipleFailuresError;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WellBehavedPluginTesterIntegrationTest {
	@Test
	void failsIfPluginIdPointsToMissingClass() {
		val ex = assertThrows(MultipleFailuresError.class,
			new WellBehavedPluginTester().qualifiedPluginId("gravatesting.missing-class")::testWellBehavedPlugin);
		assertThat(ex.getMessage(), allOf(startsWith("Plugin is not well-behaved"), containsString("can apply plugin by id using project.apply(plugin: <id>)")));
	}

	@Test
	void failsIfPluginThrowsException() {
		val ex = assertThrows(MultipleFailuresError.class,
			new WellBehavedPluginTester().pluginType(ThrowingTestPlugin.class)::testWellBehavedPlugin);
		assertThat(ex.getMessage(), allOf(startsWith("Plugin is not well-behaved"), containsString("can apply plugin type type using project.apply(plugin: <class>)")));
	}

	@Test
	void failsIfPluginResolveTasks() {
		val ex = assertThrows(MultipleFailuresError.class,
			new WellBehavedPluginTester().qualifiedPluginId("gradletesting.resolve-task-early").pluginType(ResolveTaskEarlyTestPlugin.class)::testWellBehavedPlugin);
		assertThat(ex.getMessage(), allOf(startsWith("Plugin is not well-behaved"), containsString("does not realize task")));
	}

	@TestFactory
	Stream<DynamicTest> canUseTesterWithJUnit5TestFactory() {
		return new WellBehavedPluginTester().pluginType(WellBehavedTestPlugin.class).qualifiedPluginId("gravatesting.well-behaved-plugin").stream().map(TestCaseUtils::toJUnit5DynamicTest);
	}
}
