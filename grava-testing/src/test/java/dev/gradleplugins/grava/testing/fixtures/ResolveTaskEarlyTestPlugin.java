package dev.gradleplugins.grava.testing.fixtures;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class ResolveTaskEarlyTestPlugin implements Plugin<Project> {
	@Override
	public void apply(Project project) {
		project.getTasks().create("foo");
	}
}
