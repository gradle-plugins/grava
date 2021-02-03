package dev.gradleplugins.grava.publish.metadata;

import com.google.gson.GsonBuilder;
import lombok.val;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;

public final class GradleModuleMetadataWriter implements Closeable {
	private final Writer writer;

	public GradleModuleMetadataWriter(Writer writer) {
		this.writer = writer;
	}

	public void write(GradleModuleMetadata metadata) throws IOException {
		val gson = new GsonBuilder().setPrettyPrinting().create();
		writer.write(gson.toJson(metadata));
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}
