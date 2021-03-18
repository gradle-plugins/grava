package dev.gradleplugins.grava.testing;

import org.gradle.util.Path;

public interface TestCase {
	default String getDisplayName() {
		return this.getClass().getSimpleName();
	}

	default void setUp() throws Throwable {}

	void execute() throws Throwable;

	default void tearDown() throws Throwable {}
}
