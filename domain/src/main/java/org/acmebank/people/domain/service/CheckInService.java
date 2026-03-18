package org.acmebank.people.domain.service;

import org.acmebank.people.domain.Assessment;
import org.acmebank.people.domain.CheckIn;
import org.acmebank.people.domain.CheckInStatus;
import org.acmebank.people.domain.Evidence;
import org.acmebank.people.domain.EvidenceStatus;
import org.acmebank.people.domain.Grade;
import org.acmebank.people.domain.Pillar;
import org.acmebank.people.domain.Score;
import org.acmebank.people.domain.port.AssessmentRepository;
import org.acmebank.people.domain.port.CheckInRepository;
import org.acmebank.people.domain.port.EvidenceRepository;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CheckInService {

    private final CheckInRepository checkInRepository;
    private final EvidenceRepository evidenceRepository;
    private final AssessmentRepository assessmentRepository;

    public CheckInService(
            CheckInRepository checkInRepository,
            EvidenceRepository evidenceRepository,
            AssessmentRepository assessmentRepository) {
        this.checkInRepository = checkInRepository;
        this.evidenceRepository = evidenceRepository;
        this.assessmentRepository = assessmentRepository;
    }

    public CheckIn createCheckIn(UUID userId, UUID managerId, String managerNotes, Grade targetGrade) {
        LocalDate now = LocalDate.now();
        
        Map<Pillar, Score> aggregatedScores = getAggregatedScores(userId);
        
        // Note: hasItaAssessment check needs to be refactored or kept here
        // For simplicity, let's keep the existing logic and just use it to evaluate status.
        boolean hasItaAssessment = checkHasItaAssessment(userId);
        CheckInStatus status = evaluateStatus(aggregatedScores, targetGrade, hasItaAssessment);

        CheckIn checkIn = new CheckIn(
                null,
                userId,
                managerId,
                now.minusMonths(3), // Example period start
                now, // Example period end
                aggregatedScores,
                managerNotes,
                status,
                now
        );

        return checkInRepository.save(checkIn);
    }

    private boolean checkHasItaAssessment(UUID userId) {
        LocalDate threeYearsAgo = LocalDate.now().minusYears(3);
        List<Evidence> assessedEvidence = new java.util.ArrayList<>(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED));
        assessedEvidence.addAll(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED));
        
        for (Evidence evidence : assessedEvidence) {
            if (evidence.createdDate().isBefore(threeYearsAgo)) continue;
            Optional<Assessment> assessmentOpt = assessmentRepository.findByEvidenceId(evidence.id());
            if (assessmentOpt.isPresent()) {
                Assessment assessment = assessmentOpt.get();
                if (assessment.assessmentDate() != null && assessment.assessmentDate().isBefore(threeYearsAgo)) continue;
                if (assessment.isThirdParty()) return true;
            }
        }
        return false;
    }

    public Map<Pillar, Score> getAggregatedScores(UUID userId) {
        LocalDate threeYearsAgo = LocalDate.now().minusYears(3);
        List<Evidence> assessedEvidence = new java.util.ArrayList<>(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.MANAGER_ASSESSED));
        assessedEvidence.addAll(evidenceRepository.findByUserIdAndStatus(userId, EvidenceStatus.ASSESSED));

        Map<Pillar, Score> aggregatedScores = new EnumMap<>(Pillar.class);

        for (Evidence evidence : assessedEvidence) {
            if (evidence.createdDate().isBefore(threeYearsAgo)) {
                continue;
            }

            Optional<Assessment> assessmentOpt = assessmentRepository.findByEvidenceId(evidence.id());
            if (assessmentOpt.isPresent()) {
                Assessment assessment = assessmentOpt.get();

                if (assessment.assessmentDate() != null && assessment.assessmentDate().isBefore(threeYearsAgo)) {
                    continue;
                }

                if (assessment.assessedScores() != null) {
                    for (Map.Entry<Pillar, Score> entry : assessment.assessedScores().entrySet()) {
                        Pillar pillar = entry.getKey();
                        Score currentScore = entry.getValue();
                        Score existingScore = aggregatedScores.get(pillar);

                        if (existingScore == null || currentScore.value() > existingScore.value()) {
                            aggregatedScores.put(pillar, currentScore);
                        }
                    }
                }
            }
        }
        return aggregatedScores;
    }

    private CheckInStatus evaluateStatus(Map<Pillar, Score> aggregatedScores, Grade targetGrade, boolean hasItaAssessment) {
        int belowExpectationsCount = 0;
        int aboveExpectationsCount = 0;

        for (Map.Entry<Pillar, Score> entry : targetGrade.expectations().entrySet()) {
            Pillar pillar = entry.getKey();
            Score expected = entry.getValue();
            Score actual = aggregatedScores.get(pillar);

            if (actual == null || actual.value() < expected.value()) {
                belowExpectationsCount++;
            } else if (actual.value() > expected.value()) {
                aboveExpectationsCount++;
            }
        }

        if (belowExpectationsCount > 3) {
            return CheckInStatus.UNDERPERFORMING;
        }

        if (aboveExpectationsCount > 3) {
            return CheckInStatus.OVER_PERFORMING;
        }

        if (hasItaAssessment && belowExpectationsCount == 0) {
            return CheckInStatus.READY_FOR_PROMOTION;
        }

        return CheckInStatus.ON_TRACK;
    }
}
