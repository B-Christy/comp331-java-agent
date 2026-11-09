package edu.comp331;

import java.nio.file.Path;

import edu.comp331.tools.FileInfo;
import edu.comp331.tools.ListFilesTool;
import edu.comp331.tools.ReadFileTool;
import edu.comp331.tools.RunJavaTestsTool;
import edu.comp331.tools.SandboxFileStore;
import edu.comp331.tools.WriteFileTool;

public final class ToolDemo {

    private ToolDemo() {
    }

    public static void main(String[] args) throws Exception {
        Path sandboxRoot = Path.of("workspace");
        SandboxFileStore files = new SandboxFileStore(sandboxRoot);
        ListFilesTool listFiles = new ListFilesTool(files);
        ReadFileTool readFile = new ReadFileTool(files);
        WriteFileTool writeFile = new WriteFileTool(files);
        RunJavaTestsTool runJavaTests = new RunJavaTestsTool(sandboxRoot);

        System.out.println("== List calculator files ==");
        for (FileInfo entry : listFiles.execute("calculator")) {
            System.out.printf("%s | %s | %d bytes%n",
                    entry.isDirectory() ? "directory" : "file",
                    entry.relativePath(),
                    entry.size());
        }

        System.out.println();
        System.out.println("== Read Calculator.java ==");
        System.out.println(readFile.execute(
                "calculator/src/main/java/calculator/Calculator.java"));

        System.out.println("== Write harmless demo file ==");
        System.out.println(writeFile.execute(new WriteFileTool.Request(
                "tool-demo-output.txt",
                "Created by the sandbox tool demo.")));
        System.out.println("The file remains in workspace after the demo.");

        System.out.println();
        System.out.println("== Run calculator tests ==");
        String testResult = runJavaTests.execute("calculator");
        System.out.println(testResult);
        if (!testResult.startsWith("Maven tests exited with code 0.")) {
            throw new IllegalStateException("The calculator Maven tests did not pass.");
        }
    }
}
