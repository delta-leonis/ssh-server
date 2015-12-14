package org.ssh.expressions;

/**
 * The Class Token.
 * <p>
 * A Token is a word used by {@link RegularLanguage} that can either be a regular
 * word in the language, an {@link org.ssh.expressions.tokens.OperatorToken operator}, or a {@link org.ssh.expressions.tokens.FunctionToken function}.
 *
 * @param <O> The Type of object encompassed by the Token.
 * @author Rimon Oz
 */
public class Token<O> {
    /**
     * The type of the token ('w' for a word, 'o' for an operator,
     * 'f' for a function, and 'c' for a concatenator.
     */
    private String tokenType;
    /**
     * The String representation of the Token.
     */
    private String symbol;

    /**
     * The content of the Token.
     */
    private O meaning;

    /**
     * Constructs a Token with the supplied type and String representation.
     *
     * @param tokenType The type of the Token.
     * @param symbol    The String representation of the Token.
     */
    public Token(String tokenType, String symbol) {
        this.tokenType = tokenType;
        this.symbol = symbol;
    }

    /**
     * Constructs a Token with the supplied meaning.
     *
     * @param meaning The meaning to be embedded in the Token.
     */
    public Token(O meaning) {
        this.tokenType = "w";
        this.setMeaning(meaning);
    }

    /**
     * Returns the type of the Token.
     *
     * @return The type of the Token as a String.
     */
    public String getType() {
        return this.tokenType;
    }

    /**
     * Returns the String representation of the Token.
     *
     * @return The String representation of the Token.
     */
    public String getSymbol() {
        return this.symbol;
    }

    /**
     * Returns the content of the Token.
     *
     * @return The content of the Token.
     */
    public O getMeaning() {
        return meaning;
    }

    /**
     * Sets the content of the Token.
     *
     * @param meaning The new content to be set.
     */
    public void setMeaning(O meaning) {
        this.meaning = meaning;
    }

    /**
     * Returns true if the Token has a meaning (content), false otherwise.
     *
     * @return True when succesful, false otherwise
     */
    public boolean hasMeaning() {
        return this.meaning != null;
    }
}