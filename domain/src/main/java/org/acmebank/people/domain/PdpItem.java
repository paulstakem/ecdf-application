package org.acmebank.people.domain;

import java.time.LocalDate;
import java.util.UUID;

public record PdpItem(
        UUID id,
        UUID userId,
        UUID checkInId,
        Pillar targetedPillar,
        String gapDescription,
        String actionablePlan,
        String learningJourneyLink,
        boolean isCompleted,
        LocalDate createdDate,
        LocalDate updatedDate) {
}
