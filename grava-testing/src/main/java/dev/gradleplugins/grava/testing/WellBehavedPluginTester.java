package dev.gradleplugins.grava.testing;

import dev.gradleplugins.grava.testing.file.TestNameTestDirectoryProvider;
import dev.gradleplugins.grava.testing.util.ProjectTestUtils;
import dev.gradleplugins.runnerkit.GradleExecutor;
import dev.gradleplugins.runnerkit.GradleRunner;
import lombok.val;
import org.gradle.api.Plugin;
import org.opentest4j.MultipleFailuresError;
import org.opentest4j.TestAbortedException;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.join;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isA;

public final class WellBehavedPluginTester {
	private String qualifiedPluginId;
	private Class<? extends Plugin<?>> pluginType;

	private String getQualifiedPluginIdUnderTest() {
		if (qualifiedPluginId == null) {
			throw new TestAbortedException();
		}
		return qualifiedPluginId;
	}

	private Class<? extends Plugin<?>> getPluginTypeUnderTest() {
		if (pluginType == null) {
			throw new TestAbortedException();
		}
		return pluginType;
	}

	public WellBehavedPluginTester qualifiedPluginId(String qualifiedPluginId) {
		this.qualifiedPluginId = qualifiedPluginId;
		return this;
	}

	public WellBehavedPluginTester pluginType(Class<? extends Plugin<?>> pluginType) {
		this.pluginType = pluginType;
		return this;
	}

	private Collection<TestCase> getTesters() {
		val testers = new ArrayList<TestCase>();
		testers.add(new AppliedPluginIdIsExpectedType());
		testers.add(new CanApplyByIdViaPluginDsl());
		testers.add(new CanApplyPluginByIdUsingProjectApply());
		testers.add(new CanApplyPluginByTypeUsingProjectApply());
		testers.add(new CanExecuteHelpTask());
		testers.add(new CanExecuteTasksTask());
		testers.add(new DoesNotRealizeTask());
		return testers;
	}

	public void testWellBehavedPlugin() {
		if (qualifiedPluginId == null && pluginType == null) {
			throw new AssertionError("Missing qualified plugin id and/or plugin type");
		}
		val failures = new ArrayList<TestCaseFailure>();
		stream().forEach(testCase -> {
			try {
				testCase.setUp();
				try {
					testCase.execute();
				} finally {
					testCase.tearDown();
				}
			} catch (TestAbortedException ex) {
				// ignore test
			} catch (Throwable throwable) {
				failures.add(new TestCaseFailure(testCase.getDisplayName(), throwable));
			}
		});
		if (!failures.isEmpty()) {
			throw new MultipleFailuresError("Plugin is not well-behaved", failures);
		}
	}

	public static final class TestCaseFailure extends RuntimeException {
		public TestCaseFailure(String displayName, Throwable throwable) {
			super(displayName, throwable);
		}
	}

	public Stream<TestCase> stream() {
		return getTesters().stream();
	}

	private abstract class FileTesterTestCase implements TestCase {
		private final TestNameTestDirectoryProvider testDirectory = TestNameTestDirectoryProvider.newInstance(getDisplayName(), WellBehavedPluginTester.this);

		protected File getWorkingDirectory() {
			return testDirectory.getTestDirectory().toFile();
		}

		@Override
		public void tearDown() throws Throwable {
			testDirectory.cleanup();
		}
	}

	private final class CanApplyPluginByIdUsingProjectApply implements TestCase {
		@Override
		public String getDisplayName() {
			return "can apply plugin by id using project.apply(plugin: <id>)";
		}

		@Override
		public void execute() throws Throwable {
			val project = ProjectTestUtils.rootProject();
			project.apply(singletonMap("plugin", getQualifiedPluginIdUnderTest()));
			// assert no-throw
		}
	}

	private final class AppliedPluginIdIsExpectedType implements TestCase {
		@Override
		public String getDisplayName() {
			return "applied plugin id has expected type";
		}

		@Override
		public void execute() throws Throwable {
			val project = ProjectTestUtils.rootProject();
			project.apply(singletonMap("plugin", getQualifiedPluginIdUnderTest()));
			Plugin<?> pluginById = project.getPlugins().findPlugin(getQualifiedPluginIdUnderTest());
			assertThat(pluginById, isA(getPluginTypeUnderTest()));
		}
	}

	public class CanApplyPluginByTypeUsingProjectApply implements TestCase {
		@Override
		public String getDisplayName() {
			return "can apply plugin type type using project.apply(plugin: <class>)";
		}

		@Override
		public void execute() throws Throwable {
			val project = ProjectTestUtils.rootProject();
			project.apply(singletonMap("plugin", getPluginTypeUnderTest()));
		}
	}

	public class CanApplyByIdViaPluginDsl extends FileTesterTestCase {
		@Override
		public String getDisplayName() {
			return "can apply by id via plugin DSL";
		}

		@Override
		public void execute() throws Throwable {
			File buildFile = new File(getWorkingDirectory(), "build.gradle");
			StringWriter content = new StringWriter();
			PrintWriter out = new PrintWriter(content);
			out.println("plugins {");
			out.println("    id '" + getQualifiedPluginIdUnderTest() + "'");
			out.println("}");
			Files.write(buildFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));

			configureRunner(GradleRunner.create(GradleExecutor.gradleTestKit()).inDirectory(getWorkingDirectory())).build();
		}

		private GradleRunner configureRunner(GradleRunner runner) {
			// TODO: Should this be a feature of Runner Kit
			if (this.getClass().getResource("plugin-under-test-metadata.properties") == null) {
				return runner.withPluginClasspath(Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator)).map(File::new).collect(Collectors.toList()));
			}
			return runner.withPluginClasspath();
		}
	}

	private void executeTask(String task, File workingDirectory) {
		GradleRunner runner = GradleRunner.create(GradleExecutor.gradleTestKit()).inDirectory(workingDirectory).withTasks(task);

		// TODO: Should this also be a feature of Runner Kit
		//		Assume.assumeNotNull(this.getClass().getResource("plugin-under-test-metadata.properties"));

		// TODO: Should this be a feature of Runner Kit
		if (this.getClass().getResource("plugin-under-test-metadata.properties") == null) {
			runner = runner.withPluginClasspath(Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator)).map(File::new).collect(Collectors.toList()));
		} else {
			runner = runner.withPluginClasspath();
		}

		runner.build();
	}

	private void aBuildScriptApplyingPlugin(File workingDirectory) throws Exception {
		File buildFile = new File(workingDirectory, "build.gradle");
		StringWriter content = new StringWriter();
		PrintWriter out = new PrintWriter(content);
		out.println("plugins {");
		out.println("    id '" + getQualifiedPluginIdUnderTest() + "'");
		out.println("}");
		Files.write(buildFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));
	}

	private class CanExecuteHelpTask extends FileTesterTestCase {
		@Override
		public String getDisplayName() {
			return "can execute help task";
		}

		@Override
		public void execute() throws Throwable {
			aBuildScriptApplyingPlugin(getWorkingDirectory());
			executeTask("help", getWorkingDirectory());
		}
	}

	private class CanExecuteTasksTask extends FileTesterTestCase {
		@Override
		public String getDisplayName() {
			return "can execute tasks task";
		}

		@Override
		public void execute() throws Throwable {
			aBuildScriptApplyingPlugin(getWorkingDirectory());
			executeTask("tasks", getWorkingDirectory());
		}
	}

	private class DoesNotRealizeTask extends FileTesterTestCase {
		protected List<String> getRealizedTaskPaths() {
			return Collections.singletonList(":help");
		}

		private GradleRunner configureRunner(GradleRunner runner) {
			// TODO: Should this be a feature of Runner Kit
			if (this.getClass().getResource("plugin-under-test-metadata.properties") == null) {
				return runner.withPluginClasspath(Arrays.stream(System.getProperty("java.class.path").split(File.pathSeparator)).map(File::new).collect(Collectors.toList()));
			}
			return runner.withPluginClasspath();
		}

		private List<String> getRealizedQuotedTaskPaths() {
			return getRealizedTaskPaths().stream().map(this::quoted).collect(Collectors.toList());
		}

		private String quoted(String s) {
			return "'" + s + "'";
		}

		@Override
		public String getDisplayName() {
			return "does not realize task";
		}

		@Override
		public void execute() throws Throwable {
			StringWriter content = new StringWriter();
			PrintWriter out = new PrintWriter(content);
			out.println("plugins {");
			out.println("    id '" + getQualifiedPluginIdUnderTest() + "'");
			out.println("}");
			out.println();
			out.println("def configuredTasks = []");
			out.println("tasks.configureEach {");
			out.println("    configuredTasks << it");
			out.println("}");
			out.println();
			out.println("gradle.buildFinished {");
			out.println(" println 'wat'");
			out.println("    def configuredTaskPaths = configuredTasks*.path");
			out.println();
			out.println("    // TODO: Log warning if getRealizedTaskPaths() is different than ':help'");
			out.println("    configuredTaskPaths.removeAll([" + join(", ", getRealizedQuotedTaskPaths()) + "])");
			out.println("    assert configuredTaskPaths == []");
			out.println("}");

			File buildFile = new File(getWorkingDirectory(), "build.gradle");
			Files.write(buildFile.toPath(), content.toString().getBytes(StandardCharsets.UTF_8));

			GradleRunner.create(GradleExecutor.gradleTestKit()).inDirectory(getWorkingDirectory()).configure(this::configureRunner).withTasks("help").build();
		}
	}
}
