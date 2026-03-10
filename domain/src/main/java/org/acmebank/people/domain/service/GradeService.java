package org.acmebank.people.domain.service;

import org.acmebank.people.domain.Grade;
import org.acmebank.people.domain.port.GradeRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GradeService {

    private final GradeRepository gradeRepository;

    public GradeService(GradeRepository gradeRepository) {
        this.gradeRepository = gradeRepository;
    }

    public Optional<Grade> getGradeById(UUID id) {
        return gradeRepository.findById(id);
    }

    public List<Grade> getAllGrades() {
        return gradeRepository.findAll();
    }
}
