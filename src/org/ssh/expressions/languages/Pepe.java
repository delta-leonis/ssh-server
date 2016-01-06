package org.ssh.expressions.languages;

import org.ssh.expressions.RegularLanguage;
import org.ssh.pipelines.AbstractPipelinePacket;

import java.util.*;
import java.util.function.Function;

/**
 * The Class Pepe.
 * <p>
 * PEPE: Evaluates Manageable Expressions
 * <p>
 * Pepe is a regular language whose words are lambdas.
 * Pepe is a purpose-specific language made for currying lambdas according to a provided expression
 *
 * @author Rimon Oz
 */
public class Pepe {

    /**
     * The internal representation of the language.
     */
    private RegularLanguage<Function<AbstractPipelinePacket<?>, AbstractPipelinePacket<?>>> regularLanguage;

    /**
     * Instantiates a new Pepe.
     */
    public Pepe(Function<String, Function<AbstractPipelinePacket<?>, AbstractPipelinePacket<?>>> resolvingOperation) {

        // create the language with the following lookup function
        this.regularLanguage = new RegularLanguage<>(resolvingOperation)
        // add the concatenation operator
        .addConcatenator(">",
                (leftMember, rightMember) -> {
                    return new ArrayList<>(Collections.singletonList(input -> rightMember.apply(leftMember.apply(input))));
                })
        // add the parallel operator
        .addOperator("|", true, 2,
                (leftMember, rightMember) -> new ArrayList<>(Arrays.asList(leftMember::apply, rightMember::apply)))
        // build the language
        .build();
    }

    /**
     * Evaluates the given pattern and returns a list of all possible pipeline routes generated by the pattern.
     *
     * @param pattern The pattern to be evaluated.
     * @return A list of routes generated by the evaluation.
     */
    public List<Function<AbstractPipelinePacket<?>, AbstractPipelinePacket<?>>> evaluate(String pattern) {
        return this.regularLanguage.evaluate(pattern);
    }

}