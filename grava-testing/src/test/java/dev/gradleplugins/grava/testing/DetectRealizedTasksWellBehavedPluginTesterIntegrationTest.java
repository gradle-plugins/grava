package dev.gradleplugins.grava.testing;

import lombok.val;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DetectRealizedTasksWellBehavedPluginTesterIntegrationTest {
	@Test
	void failsIfProjectPluginRealizedTasks() {
		val ex = assertThrows(AssertionError.class, new WellBehavedPluginTester().pluginClass(RealizedTasksProjectPlugin.class).supportedTarget(WellBehavedPluginTester.SupportedTarget.Project).doesNotWellBehaveWhenAppliedToUnsupportedTarget()::testWellBehavedPlugin);
		assertThat(ex.getMessage(), allOf(startsWith("Plugin is not well-behaved"), containsString("does not realize task")));
	}

	@Test
	void failsIfSettingsPluginRealizedTasks() {
		val ex = assertThrows(AssertionError.class, new WellBehavedPluginTester().pluginClass(RealizedTasksSettingsPlugin.class).supportedTarget(WellBehavedPluginTester.SupportedTarget.Settings).doesNotWellBehaveWhenAppliedToUnsupportedTarget()::testWellBehavedPlugin);
		assertThat(ex.getMessage(), allOf(startsWith("Plugin is not well-behaved"), containsString("does not realize task")));
	}

	@Test
	void failsIfInitPluginRealizedTasks() {
		val ex = assertThrows(AssertionError.class, new WellBehavedPluginTester().pluginClass(RealizedTasksInitPlugin.class).supportedTarget(WellBehavedPluginTester.SupportedTarget.Init).doesNotWellBehaveWhenAppliedToUnsupportedTarget()::testWellBehavedPlugin);
		assertThat(ex.getMessage(), allOf(startsWith("Plugin is not well-behaved"), containsString("does not realize task")));
	}

	@Test
	void failsIfPluginRealizedTasksInSubprojects() {
		val ex = assertThrows(AssertionError.class, new WellBehavedPluginTester().pluginClass(RealizedSubprojectsTasksPlugin.class).supportedTarget(WellBehavedPluginTester.SupportedTarget.Project).doesNotWellBehaveWhenAppliedToUnsupportedTarget()::testWellBehavedPlugin);
		assertThat(ex.getMessage(), allOf(startsWith("Plugin is not well-behaved"), containsString("does not realize task")));
	}

	public static class RealizedSubprojectsTasksPlugin implements Plugin<Project> {
		@Override
		public void apply(Project target) {
			target.subprojects(proj -> proj.getTasks().create("foo"));
		}
	}

	public static class RealizedTasksProjectPlugin implements Plugin<Project> {
		@Override
		public void apply(Project target) {
			target.getTasks().create("foo");
		}
	}

	public static class RealizedTasksSettingsPlugin implements Plugin<Settings> {
		@Override
		public void apply(Settings target) {
			target.getGradle().rootProject(it -> it.getTasks().create("foo"));
		}
	}

	public static class RealizedTasksInitPlugin implements Plugin<Gradle> {
		@Override
		public void apply(Gradle target) {
			target.rootProject(it -> it.getTasks().create("foo"));
		}
	}
}
