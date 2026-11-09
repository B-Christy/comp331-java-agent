package edu.comp331.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class WriteFileToolTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void createsFileInsideSandbox() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        WriteFileTool tool = new WriteFileTool(new SandboxFileStore(sandbox));
        String content = "Hello, sandbox!\nこんにちは";

        tool.execute(new WriteFileTool.Request("new.txt", content));

        assertEquals(content, Files.readString(sandbox.resolve("new.txt")));
    }

    @Test
    public void overwritesFileInsideSandbox() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Path file = sandbox.resolve("existing.txt");
        Files.writeString(file, "old content");
        WriteFileTool tool = new WriteFileTool(new SandboxFileStore(sandbox));

        tool.execute(new WriteFileTool.Request("existing.txt", "new content"));

        assertEquals("new content", Files.readString(file));
    }

    @Test
    public void rejectsMissingParentDirectory() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        WriteFileTool tool = new WriteFileTool(new SandboxFileStore(sandbox));

        assertThrows(
                ToolException.class,
                () -> tool.execute(new WriteFileTool.Request("missing/file.txt", "content")));
    }

    @Test
    public void rejectsPathThatTraversesOutsideSandbox() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Path outsideFile = temporaryFolder.getRoot().toPath().resolve("outside.txt");
        Files.writeString(outsideFile, "unchanged");
        WriteFileTool tool = new WriteFileTool(new SandboxFileStore(sandbox));

        assertThrows(
                ToolException.class,
                () -> tool.execute(new WriteFileTool.Request("../outside.txt", "changed")));

        assertEquals("unchanged", Files.readString(outsideFile));
    }

    @Test
    public void rejectsAbsolutePath() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Path outsideFile = temporaryFolder.newFile("outside.txt").toPath();
        Files.writeString(outsideFile, "unchanged");
        WriteFileTool tool = new WriteFileTool(new SandboxFileStore(sandbox));

        assertThrows(
                ToolException.class,
                () -> tool.execute(new WriteFileTool.Request(outsideFile.toString(), "changed")));

        assertEquals("unchanged", Files.readString(outsideFile));
    }
}
