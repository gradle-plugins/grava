package dev.gradleplugins.grava.testing.fixtures;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ThrowingTestPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		throw new RuntimeException("boom");
	}
}
