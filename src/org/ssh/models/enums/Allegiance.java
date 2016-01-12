package org.ssh.models.enums;

/**
 * Enum which describes wheter a team is {@link #ALLY} or {@link #OPPONENT}
 *
 * @author Jeroen de Jong
 */
public enum Allegiance {
    ALLY, OPPONENT;

    /**
     * @return the opposite {@link Allegiance}.
     */
    public Allegiance opposite() {
        return this.equals(ALLY) ? OPPONENT : ALLY;
    }

    public String identifier() {
        return this.name().substring(0, 1);
    }
}
