package org.acmebank.people.application.adapter.out.persistence

import org.acmebank.people.application.adapter.out.persistence.entity.AssessmentEntity
import org.acmebank.people.application.adapter.out.persistence.entity.EvidenceEntity
import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity
import org.acmebank.people.application.adapter.out.persistence.repository.AssessmentJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.EvidenceJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.GradeJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.UserJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDate

@DataJpaTest
@ActiveProfiles("test")
class AssessmentRepositoryIntegrationTest {

    @Autowired
    private lateinit var assessmentRepository: AssessmentJpaRepository

    @Autowired
    private lateinit var evidenceRepository: EvidenceJpaRepository

    @Autowired
    private lateinit var userRepository: UserJpaRepository

    @Autowired
    private lateinit var gradeRepository: GradeJpaRepository

    @Test
    fun `should save and find assessment by evidence id`() {
        // Given
        val grade = GradeEntity().apply {
            name = "Senior"
            role = "Software Engineer"
        }
        val savedGrade = gradeRepository.save(grade)

        val dev = UserEntity().apply {
            email = "dev@acmebank.com"
            fullName = "Jane Dev"
            isIta = false
            this.grade = savedGrade
        }
        val savedDev = userRepository.save(dev)

        val manager = UserEntity().apply {
            email = "mgr@acmebank.com"
            fullName = "Bob Manager"
            isIta = true
            this.grade = savedGrade
        }
        val savedManager = userRepository.save(manager)

        val evidence = EvidenceEntity().apply {
            this.user = savedDev
            title = "Project Y Refactoring"
            impact = "Reduced tech debt"
            complexity = "Medium"
            contribution = "Sole contributor"
            status = "SUBMITTED"
            createdDate = LocalDate.now()
            lastModifiedDate = LocalDate.now()
        }
        val savedEvidence = evidenceRepository.save(evidence)

        val assessment = AssessmentEntity().apply {
            this.evidence = savedEvidence
            this.assessor = savedManager
            reviewSummary = "Great work!"
            isThirdParty = false
            isThirdParty = false
            assessmentDate = LocalDate.now()
        }

        // When
        val savedAssessment = assessmentRepository.save(assessment)
        val foundAssessment = assessmentRepository.findByEvidenceId(savedEvidence.id)

        // Then
        assertThat(savedAssessment.id).isNotNull()
        assertThat(foundAssessment).isPresent()
        assertThat(foundAssessment.get().reviewSummary).isEqualTo("Great work!")
        assertThat(foundAssessment.get().assessor.email).isEqualTo("mgr@acmebank.com")
    }
}
