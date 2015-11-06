package org.ssh.models.enums;

/**
 * A number of commands that will be send by the Referee during a match, For documentation see
 * "Eindverslag versie 1.0.pdf" chapter "6.7 Referee".
 * 
 * @see {@link protobuf.RefereeOuterClass.Referee Referee} (protobuf)
 * @see {@link org.ssh.models.Referee } (models implementation)
 * @author Jeroen de Jong
 */
public enum RefereeCommand {
    HALT,
    STOP,
    NORMAL_START,
    FORCE_START,
    PREPARE_KICKOFF_YELLOW,
    PREPARE_KICKOFF_BLUE,
    PREPARE_PENALTY_YELLOW,
    PREPARE_PENALTY_BLUE,
    DIRECT_FREE_YELLOW,
    DIRECT_FREE_BLUE,
    INDIRECT_FREE_YELLOW,
    INDIRECT_FREE_BLUE,
    TIMEOUT_YELLOW,
    TIMEOUT_BLUE,
    GOAL_YELLOW,
    GOAL_BLUE;
}