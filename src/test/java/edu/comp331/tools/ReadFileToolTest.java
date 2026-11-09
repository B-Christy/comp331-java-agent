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
        ReadFileTool tool = new ReadFileTool(new SandboxFileStore(sandbox));

        assertEquals(contents, tool.execute("message.txt"));
    }

    @Test
    public void limitsReadToTenThousandCharacters() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        String contents = "a".repeat(10_001);
        Files.writeString(sandbox.resolve("message.txt"), contents);
        ReadFileTool tool = new ReadFileTool(new SandboxFileStore(sandbox));

        String result = tool.execute("message.txt");

        assertEquals(10_000, result.length());
        assertEquals(contents.substring(0, 10_000), result);
    }

    @Test
    public void rejectsMissingFile() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        ReadFileTool tool = new ReadFileTool(new SandboxFileStore(sandbox));

        ToolException exception = assertThrows(
                ToolException.class, () -> tool.execute("missing.txt"));

        assertTrue(exception.getCause() instanceof java.io.IOException);
    }

    @Test
    public void rejectsDirectoryPassedAsFile() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Files.createDirectory(sandbox.resolve("folder"));
        ReadFileTool tool = new ReadFileTool(new SandboxFileStore(sandbox));

        assertThrows(ToolException.class, () -> tool.execute("folder"));
    }

    @Test
    public void rejectsPathThatTraversesOutsideSandbox() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Files.writeString(temporaryFolder.getRoot().toPath().resolve("outside.txt"), "outside");
        ReadFileTool tool = new ReadFileTool(new SandboxFileStore(sandbox));

        assertThrows(ToolException.class, () -> tool.execute("../outside.txt"));
    }

    @Test
    public void rejectsAbsolutePath() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Path outsideFile = temporaryFolder.newFile("outside.txt").toPath();
        ReadFileTool tool = new ReadFileTool(new SandboxFileStore(sandbox));

        assertThrows(ToolException.class, () -> tool.execute(outsideFile.toString()));
    }

    @Test
    public void rejectsNullAndBlankPaths() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        ReadFileTool tool = new ReadFileTool(new SandboxFileStore(sandbox));

        assertThrows(ToolException.class, () -> tool.execute(null));
        assertThrows(ToolException.class, () -> tool.execute(" \t\n"));
    }

    @Test
    public void exposesToolMetadata() throws Exception {
        ReadFileTool tool = new ReadFileTool(
                new SandboxFileStore(temporaryFolder.getRoot().toPath()));

        assertEquals("read_file", tool.name());
        assertEquals("Reads up to 10,000 characters from a file in the sandbox.", tool.description());
    }
}
