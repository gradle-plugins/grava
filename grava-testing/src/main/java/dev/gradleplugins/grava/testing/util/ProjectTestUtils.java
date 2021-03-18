package dev.gradleplugins.grava.testing.util;

import dev.gradleplugins.grava.testing.file.TestNameTestDirectoryProvider;
import lombok.val;
import org.gradle.api.Project;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.internal.project.ProjectInternal;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ProviderFactory;
import org.gradle.testfixtures.ProjectBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Test utilities to access various Gradle services useful during testing as well as Project instances.
 */
public final class ProjectTestUtils {
	private static final String CLEANUP_THREAD_NAME = "project-test-utils-cleanup";
	private static final AtomicBoolean SHUTDOWN_REGISTERED = new AtomicBoolean();
	private static final List<TestNameTestDirectoryProvider> PROJECT_DIRECTORIES_TO_CLEANUP = Collections.synchronizedList(new ArrayList<>());
	private static Project _use_project_method = null;
	private ProjectTestUtils() {}

	private static void maybeRegisterCleanup() {
		if (SHUTDOWN_REGISTERED.compareAndSet(false, true)) {
			Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						synchronized (PROJECT_DIRECTORIES_TO_CLEANUP) {
							for (TestNameTestDirectoryProvider testDirectory : PROJECT_DIRECTORIES_TO_CLEANUP) {
								testDirectory.cleanup();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, CLEANUP_THREAD_NAME));
		}
	}

	private static Project project() {
		if (_use_project_method == null) {
			_use_project_method = ProjectBuilder.builder().build();
		}
		return _use_project_method;
	}

	public static ObjectFactory objectFactory() {
		return project().getObjects();
	}

	public static ProviderFactory providerFactory() {
		return project().getProviders();
	}

	public static Dependency createDependency(Object notation) {
		return project().getDependencies().create(notation);
	}

	public static Project rootProject() {
		maybeRegisterCleanup();
		val testDirectory = new TestNameTestDirectoryProvider(ProjectTestUtils.class);
		PROJECT_DIRECTORIES_TO_CLEANUP.add(testDirectory);
		return ProjectBuilder.builder().withProjectDir(testDirectory.getTestDirectory().toFile()).build();
	}

	public static Project createRootProject(File rootDirectory) {
		return ProjectBuilder
			.builder()
			.withProjectDir(rootDirectory)
			.build();
	}

	public static Project createChildProject(Project parent) {
		return ProjectBuilder
			.builder()
			.withParent(parent)
			.build();
	}

	public static Project createChildProject(Project parent, String name) {
		return ProjectBuilder
			.builder()
			.withName(name)
			.withParent(parent)
			.build();
	}

	public static Project createChildProject(Project parent, String name, File projectDirectory) {
		return ProjectBuilder
			.builder()
			.withName(name)
			.withParent(parent)
			.withProjectDir(projectDirectory)
			.build();
	}

	public static Project evaluate(Project project) {
		return ((ProjectInternal) project).evaluate();
	}
}
