package org.acmebank.people.application.adapter.out.persistence

import org.acmebank.people.application.adapter.out.persistence.entity.GradeEntity
import org.acmebank.people.application.adapter.out.persistence.entity.UserEntity
import org.acmebank.people.application.adapter.out.persistence.repository.GradeJpaRepository
import org.acmebank.people.application.adapter.out.persistence.repository.UserJpaRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.ActiveProfiles

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryIntegrationTest {

    @Autowired
    private lateinit var userRepository: UserJpaRepository
    
    @Autowired
    private lateinit var gradeRepository: GradeJpaRepository

    @Test
    fun `should save and find user by email`() {
        // Given
        val grade = GradeEntity().apply {
            name = "Senior"
            role = "Software Engineer"
        }
        val savedGrade = gradeRepository.save(grade)

        val user = UserEntity().apply {
            email = "john.doe@acmebank.com"
            fullName = "John Doe"
            isIta = false
            this.grade = savedGrade
        }

        // When
        val savedUser = userRepository.save(user)
        val foundUser = userRepository.findByEmail("john.doe@acmebank.com")

        // Then
        assertThat(savedUser.id).isNotNull()
        assertThat(foundUser.isPresent).isTrue()
        assertThat(foundUser.get().fullName).isEqualTo("John Doe")
        assertThat(foundUser.get().grade.name).isEqualTo("Senior")
    }

    @Test
    fun `should find users by manager id`() {
        // Given
        val grade = GradeEntity().apply {
            name = "Principal"
            role = "Software Engineer"
        }
        val savedGrade = gradeRepository.save(grade)

        val manager = UserEntity().apply {
            email = "manager@acmebank.com"
            fullName = "Jane Smith"
            isIta = true
            this.grade = savedGrade
        }
        val savedManager = userRepository.save(manager)

        val employee = UserEntity().apply {
            email = "emp@acmebank.com"
            fullName = "Bob Employee"
            isIta = false
            this.grade = savedGrade
            this.manager = savedManager
        }
        userRepository.save(employee)

        // When
        val foundEmployees = userRepository.findByManagerId(savedManager.id)

        // Then
        assertThat(foundEmployees).hasSize(1)
        assertThat(foundEmployees[0].email).isEqualTo("emp@acmebank.com")
    }

}
