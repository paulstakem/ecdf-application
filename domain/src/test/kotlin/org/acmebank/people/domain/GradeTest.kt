package org.acmebank.people.domain

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.UUID

class GradeTest {

    @Test
    fun `Grade named Vice President must have an expected score of 3 across all pillars`() {
        // Given
        val expectations = mutableMapOf<Pillar, Score>()
        
        // When
        val grade = Grade(UUID.randomUUID(), "Vice President", "Management", expectations)

        // Then
        Pillar.values().forEach { pillar ->
            grade.getExpectationFor(pillar).value() shouldBe 3
        }
    }

    @Test
    fun `Grade named Director must have an expected score of 4 across all pillars`() {
        // Given
        val expectations = mutableMapOf<Pillar, Score>()

        // When
        val grade = Grade(UUID.randomUUID(), "Director", "Management", expectations)

        // Then
        Pillar.values().forEach { pillar ->
            grade.getExpectationFor(pillar).value() shouldBe 4
        }
    }
}
