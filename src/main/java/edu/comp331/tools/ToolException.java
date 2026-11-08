package edu.comp331.tools;

/**
 * Indicates that an agent tool could not complete its operation.
 */
public class ToolException extends Exception {

    public ToolException(String message) {
        super(message);
    }

    public ToolException(String message, Throwable cause) {
        super(message, cause);
    }
}
