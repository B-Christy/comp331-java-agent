package edu.comp331.tools;

import static org.junit.Assert.assertThrows;

import java.nio.file.Path;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ListFilesToolTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

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
