package org.acmebank.people.domain;

public record Score(int value) {
    public Score {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5 (Dreyfus scale)");
        }
    }

    public boolean meetsExpectation(Score expectation) {
        return this.value >= expectation.value();
    }
}
