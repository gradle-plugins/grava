package dev.gradleplugins.grava.testing;

import org.gradle.api.Plugin;
import org.gradle.api.plugins.PluginAware;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class GenericPluginWellBehavedPluginTesterIntegrationTest {
	@Test
	void canTestGenericPlugin() {
		assertDoesNotThrow(new WellBehavedPluginTester()
			.pluginClass(GenericPlugin.class)::testWellBehavedPlugin);
	}

	public static class GenericPlugin<T extends PluginAware> implements Plugin<T> {
		@Override
		public void apply(T target) {
			// do something
		}
	}
}
