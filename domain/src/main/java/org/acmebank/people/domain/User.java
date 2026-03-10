package org.acmebank.people.domain;

import java.util.UUID;

public record User(UUID id, String email, String fullName, Grade grade, UUID managerId, boolean isIta) {
}
