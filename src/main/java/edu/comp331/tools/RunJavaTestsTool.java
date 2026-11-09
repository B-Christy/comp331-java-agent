package edu.comp331.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public final class RunJavaTestsTool implements AgentTool<String, String> {

    private static final List<String> COMMAND = System.getProperty("os.name")
            .toLowerCase(Locale.ROOT)
            .startsWith("windows")
                    ? List.of("cmd.exe", "/d", "/s", "/c", "mvn.cmd test")
                    : List.of("mvn", "test");
    private static final Duration TIMEOUT = Duration.ofMinutes(5);
    private static final int MAX_OUTPUT_CHARACTERS = 100_000;

    private final SandboxPaths paths;
    private final ProcessRunner processRunner;

    public RunJavaTestsTool(Path sandboxRoot) throws IOException {
        this(new SandboxPaths(sandboxRoot), new MavenProcessRunner());
    }

    RunJavaTestsTool(SandboxPaths paths, ProcessRunner processRunner) {
        this.paths = paths;
        this.processRunner = processRunner;
    }

    @Override
    public String name() {
        return "run_java_tests";
    }

    @Override
    public String description() {
        return "Runs the Maven test suite in a sandbox project directory.";
    }

    @Override
    public String execute(String relativeProjectDirectory) throws ToolException {
        if (relativeProjectDirectory == null || relativeProjectDirectory.isBlank()) {
            throw new ToolException("A relative project directory is required.");
        }

        try {
            Path requested = Path.of(relativeProjectDirectory);
            if (requested.isAbsolute()) {
                throw new ToolException("The project directory must be inside the sandbox.");
            }

            Path project = paths.resolveExisting(requested);
            if (!Files.isDirectory(project)) {
                throw new IOException("The requested project path is not a directory.");
            }

            Path pom = paths.resolveExisting(requested.resolve("pom.xml"));
            if (!Files.isRegularFile(pom)) {
                throw new IOException("The project directory must contain a pom.xml file.");
            }

            ProcessResult result = processRunner.run(COMMAND, project, TIMEOUT);
            String status = result.timedOut
                    ? "Maven tests timed out after " + TIMEOUT.toMinutes() + " minutes."
                    : "Maven tests exited with code " + result.exitCode + ".";
            return status + System.lineSeparator() + result.output;
        } catch (IOException | InvalidPathException e) {
            throw new ToolException("Could not run tests in the requested project.", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ToolException("Running the requested tests was interrupted.", e);
        }
    }

    @FunctionalInterface
    interface ProcessRunner {
        ProcessResult run(List<String> command, Path workingDirectory, Duration timeout)
                throws IOException, InterruptedException;
    }

    static final class ProcessResult {
        private final int exitCode;
        private final String output;
        private final boolean timedOut;

        ProcessResult(int exitCode, String output, boolean timedOut) {
            this.exitCode = exitCode;
            this.output = output;
            this.timedOut = timedOut;
        }
    }

    private static final class MavenProcessRunner implements ProcessRunner {
        @Override
        public ProcessResult run(List<String> command, Path workingDirectory, Duration timeout)
                throws IOException, InterruptedException {
            Process process = new ProcessBuilder(command)
                    .directory(workingDirectory.toFile())
                    .redirectErrorStream(true)
                    .start();

            StringBuilder output = new StringBuilder();
            Thread outputReader = new Thread(() -> readOutput(process, output), "maven-test-output");
            outputReader.setDaemon(true);
            outputReader.start();

            boolean completed = process.waitFor(timeout.toMillis(), TimeUnit.MILLISECONDS);
            if (!completed) {
                process.descendants().forEach(ProcessHandle::destroyForcibly);
                process.destroyForcibly();
                process.waitFor();
            }
            outputReader.join();

            return new ProcessResult(completed ? process.exitValue() : -1, output.toString(), !completed);
        }

        private static void readOutput(Process process, StringBuilder output) {
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
                char[] buffer = new char[4096];
                int count;
                while ((count = reader.read(buffer)) != -1) {
                    synchronized (output) {
                        int remaining = MAX_OUTPUT_CHARACTERS - output.length();
                        if (remaining > 0) {
                            output.append(buffer, 0, Math.min(remaining, count));
                        }
                    }
                }
            } catch (IOException e) {
                synchronized (output) {
                    if (output.length() < MAX_OUTPUT_CHARACTERS) {
                        output.append(System.lineSeparator())
                                .append("Could not read all Maven output: ")
                                .append(e.getMessage());
                    }
                }
            }
        }
    }
}
