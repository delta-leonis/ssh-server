package org.ssh.expressions.tokens;

import org.ssh.expressions.RegularLanguage;
import org.ssh.expressions.Token;

import java.util.List;
import java.util.function.Function;

/**
 * The Class FunctionToken.
 * <p>
 * A FunctionToken is a word used by {@link RegularLanguage} that represents a function
 * with a specific number of arguments.
 *
 * @param <O> The Type of object encompassed by the Token.
 * @author Rimon Oz
 */
public class FunctionToken<O> extends Token<O> {

    /**
     * The function represented by the FunctionToken as a lambda.
     */
    private Function<List<String>, List<O>> transferFunction;

    /**
     * Constructs a FunctionToken represented by the supplied string with the specified number of arguments
     * and the supplied transfer function.
     *
     * @param functionToken    The String representation of the function.
     * @param transferFunction The function represented by the current class.
     */
    public FunctionToken(String functionToken, Function<List<String>, List<O>> transferFunction) {
        super("f", functionToken);
        this.transferFunction = transferFunction;
    }

    /**
     * Applies the function represented by this class to the supplied arguments.
     *
     * @param functionArguments The arguments as a List.
     * @return The result of applying the function represented by this class to the supplied arguments.
     */
    public List<O> apply(List<String> functionArguments) {
        return this.transferFunction.apply(functionArguments);
    }

}