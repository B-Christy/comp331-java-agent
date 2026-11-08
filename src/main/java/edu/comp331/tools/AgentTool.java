package edu.comp331.tools;

/**
 * A named operation that an agent can invoke.
 *
 * @param <I> the input type
 * @param <O> the result type
 */
public interface AgentTool<I, O> {

    String name();

    String description();

    O execute(I input) throws ToolException;
}
