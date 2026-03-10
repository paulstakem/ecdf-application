package org.acmebank.people.application.adapter.out.persistence

import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity
import org.acmebank.people.application.adapter.out.persistence.entity.GradeExpectationEntity
import org.acmebank.people.application.adapter.out.persistence.entity.GradeExpectationId
import org.acmebank.people.application.adapter.out.persistence.repository.GradeJpaRepository
import org.acmebank.people.domain.Pillar
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class GradeRepositoryIntegrationTest {

    @Autowired
    private lateinit var gradeRepository: GradeJpaRepository

    @Test
    fun `should save grade and its expectations`() {
        // Given
        val grade = GradeEntity().apply {
            name = "Senior"
            role = "Software Engineer"
        }

        val expectation = GradeExpectationEntity().apply {
            expectedScore = 3
            this.grade = grade
        }
        
        val savedGrade = gradeRepository.save(grade)
        
        expectation.id = GradeExpectationId(savedGrade.id, Pillar.DEFINES.name)
        savedGrade.expectations.add(expectation)
        val finalGrade = gradeRepository.save(savedGrade)

        // When
        val foundGrade = gradeRepository.findById(finalGrade.id).orElse(null)

        // Then
        assertThat(foundGrade).isNotNull()
        assertThat(foundGrade.name).isEqualTo("Senior")
        assertThat(foundGrade.expectations).hasSize(1)
        assertThat(foundGrade.expectations[0].expectedScore).isEqualTo(3)
        assertThat(foundGrade.expectations[0].id.pillar).isEqualTo(Pillar.DEFINES.name)
    }

    @Test
    fun `should find grade by name and role`() {
        // Given
        val grade = GradeEntity().apply {
            name = "Staff"
            role = "Software Engineer"
        }
        gradeRepository.save(grade)

        // When
        val foundGrade = gradeRepository.findByNameAndRole("Staff", "Software Engineer")

        // Then
        assertThat(foundGrade.isPresent).isTrue()
        assertThat(foundGrade.get().name).isEqualTo("Staff")
    }

}
