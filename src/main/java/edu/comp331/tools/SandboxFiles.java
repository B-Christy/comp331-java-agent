package edu.comp331.tools;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SandboxFiles {

    List<FileInfo> list(Path relativeDirectory) throws IOException;

    String read(Path relativeFile, int maxCharacters) throws IOException;
}
