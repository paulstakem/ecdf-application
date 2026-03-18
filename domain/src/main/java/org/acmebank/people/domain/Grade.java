package org.acmebank.people.domain;

import java.util.Map;
import java.util.UUID;

public record Grade(UUID id, String name, String role, Map<Pillar, Score> expectations) {

    public Grade {
        if (expectations == null) {
            expectations = new java.util.HashMap<>();
        } else {
            // copy to allow mutation and prevent immutable map errors if passed empty map
            expectations = new java.util.HashMap<>(expectations);
        }

        if ("Vice President".equalsIgnoreCase(name)) {
            for (Pillar p : Pillar.values()) {
                expectations.put(p, new Score(3));
            }
        } else if ("Director".equalsIgnoreCase(name)) {
            for (Pillar p : Pillar.values()) {
                expectations.put(p, new Score(4));
            }
        }
        
        expectations = java.util.Collections.unmodifiableMap(expectations);
    }

    public Score getExpectationFor(Pillar pillar) {
        return expectations.getOrDefault(pillar, new Score(1));
    }
}
