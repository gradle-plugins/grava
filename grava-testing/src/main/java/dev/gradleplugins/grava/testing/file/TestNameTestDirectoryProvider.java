package dev.gradleplugins.grava.testing.file;

import lombok.val;

import java.io.File;
import java.nio.file.Path;

/**
 * A JUnit rule which provides a unique temporary folder for the test.
 */
public final class TestNameTestDirectoryProvider extends AbstractTestDirectoryProvider {
	public TestNameTestDirectoryProvider(Class<?> klass) {
		// NOTE: the space in the directory name is intentional
		super(new File("build/tmp/test files").toPath(), klass);
	}

	public TestNameTestDirectoryProvider(Path root, Class<?> klass) {
		super(root, klass);
	}

	public static TestNameTestDirectoryProvider newInstance(Class<?> testClass) {
		return new TestNameTestDirectoryProvider(testClass);
	}

	public static TestNameTestDirectoryProvider newInstance(String methodName, Object target) {
		val testDirectoryProvider =	new TestNameTestDirectoryProvider(target.getClass());
		testDirectoryProvider.init(methodName);
		return testDirectoryProvider;
	}
}
