package org.ssh.expressions.languages;

import org.ssh.expressions.RegularLanguage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The Class Meme.
 * <p>
 * Meme is a regular language whose words are Strings. Meme stands for "Meme: Evaluates Manageable Expressions".
 * Meme is a purpose-specific language made for looking up objects in a ConcurrentHashMap by their key (the symbolic
 * token) and resolving to their values (the associated meaning).
 *
 * @author Rimon Oz
 */
public class Meme {

    private RegularLanguage<String> regularLanguage;

    /**
     * Instantiates a new Meme.
     */
    public Meme(Function<String, String> resolvingOperation) {
        // create the language with ">" as the concatenation operator
        this.regularLanguage = new RegularLanguage<>(resolvingOperation)
                .addConcatenator(">", (leftMember, rightMember) ->
                        new ArrayList<>(Collections.singletonList(leftMember.concat(rightMember))))
                // adds the range function for numeric ranges
                .addFunction("range", argument -> argument.stream().map(potentialRange -> {
                    // if there's a range operator
                    if (potentialRange.contains("-")) {
                        // split around the operator
                        String[] range = potentialRange.split("-");
                        // create a stream of numbers within the range
                        return IntStream.range(Integer.parseInt(range[0]), Integer.parseInt(range[1]) + 1)
                                // map them to strings
                                .mapToObj(Integer::toString).collect(Collectors.toList());
                    } else {
                        // otherwise return the original input
                        return Collections.singletonList(potentialRange);
                    }
                    // turn the list of lists into a single list
                }).flatMap(Collection::stream).collect(Collectors.toList()))
                // add the parallel operator
                .addOperator("|", true, 2, (leftMember, rightMember) -> new ArrayList<>(Arrays.asList(leftMember, rightMember)))
                // and build the language
                .build();
    }

    /**
     * Evaluates the given pattern and returns a list of all possible model names generated by the pattern.
     *
     * @param pattern The pattern to be evaluated.
     * @return A list of model names generated by the evaluation.
     */
    public List<String> evaluate(String pattern) {
        return this.regularLanguage.evaluate(pattern.replace("[", ">range(").replaceFirst("]$", ")").replace("]", ")>"));
    }

}
