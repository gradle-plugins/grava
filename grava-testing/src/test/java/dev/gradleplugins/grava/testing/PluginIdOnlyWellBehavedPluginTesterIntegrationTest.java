package dev.gradleplugins.grava.testing;

import lombok.val;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Sometime, a plugin maybe injected in-between the plugin id and the plugin type for backward compatibility reason.
 * In those cases, it is important the plugin is always applied via its id and not by type.
 * In other cases, we may not really care about the plugin type being applied and may only want to test the plugin by id.
 */
class PluginIdOnlyWellBehavedPluginTesterIntegrationTest {
	@Test
	void failsWhenMisbehavePluginByIdOnly() {
		val ex = assertThrows(AssertionError.class, new WellBehavedPluginTester().qualifiedPluginId("dev.gradleplugins.gravatesting.misbehaved-plugin")::testWellBehavedPlugin);
		assertThat(ex.getMessage(), allOf(startsWith("Plugin is not well-behaved"), containsString("does not resolve configuration")));
	}
}
