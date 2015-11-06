package org.ssh.models.enums;

/**
 * These are the "coarse" stages of the game. as used by SSL_refbox for use in {@link org.ssh.models.Referee Referee}.
 *
 * @author Jeroen de Jong
 * @see https://github.com/RoboCup-SSL/ssl-refbox
 * @see {@link protobuf.RefereeOuterClass.Referee Referee} (protobuf)
 */
public enum Stage {
    NORMAL_FIRST_HALF_PRE,
    NORMAL_FIRST_HALF,
    NORMAL_HALF_TIME,
    NORMAL_SECOND_HALF_PRE,
    NORMAL_SECOND_HALF,
    EXTRA_TIME_BREAK,
    EXTRA_FIRST_HALF_PRE,
    EXTRA_FIRST_HALF,
    EXTRA_HALF_TIME,
    EXTRA_SECOND_HALF_PRE,
    EXTRA_SECOND_HALF,
    PENALTY_SHOOTOUT_BREAK,
    PENALTY_SHOOTOUT,
    POST_GAME;
}