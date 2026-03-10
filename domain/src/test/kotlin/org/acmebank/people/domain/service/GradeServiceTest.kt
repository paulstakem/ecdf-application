package org.acmebank.people.domain.service

import org.acmebank.people.domain.Grade
import org.acmebank.people.domain.port.GradeRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension
import java.util.Optional
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class GradeServiceTest {

    @Mock
    private lateinit var gradeRepository: GradeRepository

    @InjectMocks
    private lateinit var gradeService: GradeService

    @Test
    fun `should find grade by id`() {
        // Given
        val gradeId = UUID.randomUUID()
        val expectedGrade = Grade(gradeId, "Senior", "Software Engineer", mapOf())

        `when`(gradeRepository.findById(gradeId)).thenReturn(Optional.of(expectedGrade))

        // When
        val result = gradeService.getGradeById(gradeId)

        // Then
        assertTrue(result.isPresent)
        assertEquals(expectedGrade, result.get())
        verify(gradeRepository).findById(gradeId)
    }

    @Test
    fun `should get all grades`() {
        // Given
        val grade1 = Grade(UUID.randomUUID(), "Senior", "Software Engineer", mapOf())
        val grade2 = Grade(UUID.randomUUID(), "Principal", "Software Engineer", mapOf())
        val expectedGrades = listOf(grade1, grade2)

        `when`(gradeRepository.findAll()).thenReturn(expectedGrades)

        // When
        val result = gradeService.getAllGrades()

        // Then
        assertEquals(2, result.size)
        assertEquals(expectedGrades, result)
        verify(gradeRepository).findAll()
    }
}
