package edu.comp331.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ReadFileToolTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void readsUtf8TextFileInsideSandbox() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        String contents = "Hello, sandbox!\nこんにちは";
        Files.writeString(sandbox.resolve("message.txt"), contents);
        ReadFileTool tool = new ReadFileTool(sandbox);

        assertEquals(contents, tool.execute("message.txt"));
    }

    @Test
    public void rejectsMissingFile() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        ReadFileTool tool = new ReadFileTool(sandbox);

        ToolException exception = assertThrows(
                ToolException.class, () -> tool.execute("missing.txt"));

        assertTrue(exception.getCause() instanceof java.io.IOException);
    }

    @Test
    public void rejectsDirectoryPassedAsFile() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Files.createDirectory(sandbox.resolve("folder"));
        ReadFileTool tool = new ReadFileTool(sandbox);

        assertThrows(ToolException.class, () -> tool.execute("folder"));
    }

    @Test
    public void rejectsPathThatTraversesOutsideSandbox() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        ReadFileTool tool = new ReadFileTool(sandbox);

        assertThrows(ToolException.class, () -> tool.execute("../outside.txt"));
    }

    @Test
    public void rejectsAbsolutePath() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Path outsideFile = temporaryFolder.newFile("outside.txt").toPath();
        ReadFileTool tool = new ReadFileTool(sandbox);

        assertThrows(ToolException.class, () -> tool.execute(outsideFile.toString()));
    }

    @Test
    public void rejectsNullAndBlankPaths() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        ReadFileTool tool = new ReadFileTool(sandbox);

        assertThrows(ToolException.class, () -> tool.execute(null));
        assertThrows(ToolException.class, () -> tool.execute(" \t\n"));
    }

    @Test
    public void exposesToolMetadata() {
        ReadFileTool tool = new ReadFileTool(temporaryFolder.getRoot().toPath());

        assertEquals("read_file", tool.name());
        assertEquals("Reads a file from the sandbox directory.", tool.description());
    }
}
