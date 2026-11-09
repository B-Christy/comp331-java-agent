package edu.comp331.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ListFilesToolTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void listsFileAndDirectoryWithRelativePathsAndMetadata() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Path file = sandbox.resolve("notes.txt");
        Files.writeString(file, "hello");
        Path directory = Files.createDirectory(sandbox.resolve("documents"));
        ListFilesTool tool = new ListFilesTool(new SandboxFileStore(sandbox));

        List<FileInfo> entries = tool.execute(".");

        assertEquals(2, entries.size());
        FileInfo directoryInfo = entries.get(0);
        assertEquals(Path.of("documents"), directoryInfo.relativePath());
        assertEquals(Files.size(directory), directoryInfo.size());
        assertTrue(directoryInfo.isDirectory());

        FileInfo fileInfo = entries.get(1);
        assertEquals(Path.of("notes.txt"), fileInfo.relativePath());
        assertEquals(Files.size(file), fileInfo.size());
        assertFalse(fileInfo.isDirectory());
    }

    @Test
    public void rejectsMissingDirectory() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        ListFilesTool tool = new ListFilesTool(new SandboxFileStore(sandbox));

        assertThrows(ToolException.class, () -> tool.execute("missing"));
    }

    @Test
    public void rejectsPathThatTraversesOutsideSandbox() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        temporaryFolder.newFolder("outside");
        ListFilesTool tool = new ListFilesTool(new SandboxFileStore(sandbox));

        assertThrows(ToolException.class, () -> tool.execute("../outside"));
    }

    @Test
    public void rejectsAbsolutePath() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Path outsideDirectory = temporaryFolder.newFolder("outside").toPath();
        ListFilesTool tool = new ListFilesTool(new SandboxFileStore(sandbox));

        assertThrows(ToolException.class, () -> tool.execute(outsideDirectory.toString()));
    }
}
