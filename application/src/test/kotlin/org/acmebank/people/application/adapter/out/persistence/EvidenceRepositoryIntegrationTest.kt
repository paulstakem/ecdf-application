package org.acmebank.people.application.adapter.out.persistence

import org.acmebank.people.application.adapter.out.persistence.entity.EvidenceEntity
import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity
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
class EvidenceRepositoryIntegrationTest {

    @Autowired
    private lateinit var evidenceRepository: EvidenceJpaRepository

    @Autowired
    private lateinit var userRepository: UserJpaRepository

    @Autowired
    private lateinit var gradeRepository: GradeJpaRepository

    @Test
    fun `should save and find evidence by user id`() {
        // Given
        val grade = GradeEntity().apply {
            name = "Senior"
            role = "Software Engineer"
        }
        val savedGrade = gradeRepository.save(grade)

        val user = UserEntity().apply {
            email = "dev@acmebank.com"
            fullName = "Jane Dev"
            isIta = false
            this.grade = savedGrade
        }
        val savedUser = userRepository.save(user)

        val evidence = EvidenceEntity().apply {
            this.user = savedUser
            title = "Delivered Project X"
            impact = "High impact on business revenue"
            complexity = "Complex architecture changes"
            contribution = "Led the backend implementation"
            status = "DRAFT"
            createdDate = LocalDate.now()
            lastModifiedDate = LocalDate.now()
            links = mutableListOf("https://github.com/org/repo/pull/1")
        }

        // When
        val savedEvidence = evidenceRepository.save(evidence)
        val foundEvidenceList = evidenceRepository.findByUserId(savedUser.id)

        // Then
        assertThat(savedEvidence.id).isNotNull()
        assertThat(foundEvidenceList).hasSize(1)
        assertThat(foundEvidenceList[0].title).isEqualTo("Delivered Project X")
        assertThat(foundEvidenceList[0].links).containsExactly("https://github.com/org/repo/pull/1")
    }
}
