package edu.comp331.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class RunJavaTestsToolTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void runsOnlyTheAllowedMavenTestCommandInTheProjectDirectory() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Path project = Files.createDirectory(sandbox.resolve("project"));
        Files.writeString(project.resolve("pom.xml"), "<project/>");
        AtomicReference<Path> workingDirectory = new AtomicReference<>();
        RunJavaTestsTool tool = new RunJavaTestsTool(
                new SandboxPaths(sandbox),
                (command, directory, timeout) -> {
                    assertEquals(expectedMavenCommand(), command);
                    assertEquals(Duration.ofMinutes(5), timeout);
                    workingDirectory.set(directory);
                    return new RunJavaTestsTool.ProcessResult(0, "BUILD SUCCESS", false);
                });

        String result = tool.execute("project");

        assertEquals(project.toRealPath(), workingDirectory.get());
        assertTrue(result.contains("Maven tests exited with code 0."));
        assertTrue(result.contains("BUILD SUCCESS"));
    }

    @Test
    public void rejectsMissingProjectDirectory() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        RunJavaTestsTool tool = toolWithUnusedRunner(sandbox);

        assertThrows(ToolException.class, () -> tool.execute("missing"));
    }

    @Test
    public void rejectsDirectoryWithoutPom() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Files.createDirectory(sandbox.resolve("not-a-project"));
        RunJavaTestsTool tool = toolWithUnusedRunner(sandbox);

        assertThrows(ToolException.class, () -> tool.execute("not-a-project"));
    }

    @Test
    public void rejectsFileInsteadOfProjectDirectory() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Files.writeString(sandbox.resolve("not-a-directory"), "not a directory");
        RunJavaTestsTool tool = toolWithUnusedRunner(sandbox);

        assertThrows(ToolException.class, () -> tool.execute("not-a-directory"));
    }

    @Test
    public void rejectsPathThatTraversesOutsideSandbox() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Path outsideProject = temporaryFolder.newFolder("outside-project").toPath();
        Files.writeString(outsideProject.resolve("pom.xml"), "<project/>");
        RunJavaTestsTool tool = toolWithUnusedRunner(sandbox);

        assertThrows(ToolException.class, () -> tool.execute("../outside-project"));
    }

    @Test
    public void rejectsAbsoluteProjectPath() throws Exception {
        Path sandbox = temporaryFolder.newFolder("sandbox").toPath();
        Path outsideProject = temporaryFolder.newFolder("outside-project").toPath();
        Files.writeString(outsideProject.resolve("pom.xml"), "<project/>");
        RunJavaTestsTool tool = toolWithUnusedRunner(sandbox);

        assertThrows(ToolException.class, () -> tool.execute(outsideProject.toString()));
    }

    private static RunJavaTestsTool toolWithUnusedRunner(Path sandbox) throws Exception {
        return new RunJavaTestsTool(
                new SandboxPaths(sandbox),
                (command, directory, timeout) -> {
                    throw new AssertionError("The process runner must not be called.");
                });
    }

    private static List<String> expectedMavenCommand() {
        return System.getProperty("os.name").toLowerCase(Locale.ROOT).startsWith("windows")
                ? List.of("cmd.exe", "/d", "/s", "/c", "mvn.cmd test")
                : List.of("mvn", "test");
    }
}
