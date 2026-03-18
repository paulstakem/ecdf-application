package org.acmebank.people.web

import io.kotest.matchers.shouldBe
import org.acmebank.people.domain.*
import org.acmebank.people.domain.port.CheckInRepository
import org.acmebank.people.domain.port.GradeRepository
import org.acmebank.people.domain.port.UserRepository
import org.acmebank.people.domain.service.CheckInService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.ArgumentMatchers.eq
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate
import java.util.*

@WebMvcTest(CheckInController::class)
class CheckInControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var checkInService: CheckInService

    @MockitoBean
    private lateinit var checkInRepository: CheckInRepository

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @MockitoBean
    private lateinit var gradeRepository: GradeRepository

    private val userId = UUID.randomUUID()
    private val managerId = UUID.randomUUID()
    private val checkInId = UUID.randomUUID()

    private val mockUser = User(
        userId, "dev@example.com", "Jane Dev",
        Grade(UUID.randomUUID(), "Senior Engineer", "Engineering", emptyMap()),
        managerId, false
    )

    private val mockManager = User(
        managerId, "mgr@example.com", "Manager Bob",
        Grade(UUID.randomUUID(), "Manager", "Engineering", emptyMap()),
        null, true
    )

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should show check-in history for user`() {
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(checkInRepository.findByUserId(userId)).thenReturn(emptyList())

        mockMvc.perform(get("/checkins/user/$userId"))
            .andExpect(status().isOk)
            .andExpect(view().name("checkin-list"))
            .andExpect(model().attributeExists("developer", "checkins"))
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should show new check-in form`() {
        `when`(userRepository.findById(userId)).thenReturn(Optional.of(mockUser))
        `when`(gradeRepository.findAll()).thenReturn(emptyList())
        `when`(checkInService.getAggregatedScores(userId)).thenReturn(emptyMap())

        mockMvc.perform(get("/checkins/new/$userId"))
            .andExpect(status().isOk)
            .andExpect(view().name("checkin-form"))
            .andExpect(model().attributeExists("developer", "grades", "pillars", "actualScores"))
    }

    @Test
    @WithMockUser(username = "mgr@example.com")
    fun `should submit new check-in and redirect`() {
        val targetGradeId = UUID.randomUUID()
        val targetGrade = Grade(targetGradeId, "Staff Engineer", "Engineering", emptyMap())

        `when`(userRepository.findByEmail("mgr@example.com")).thenReturn(Optional.of(mockManager))
        `when`(gradeRepository.findById(targetGradeId)).thenReturn(Optional.of(targetGrade))
        `when`(checkInService.createCheckIn(eq(userId), eq(managerId), any(), eq(targetGrade)))
            .thenReturn(CheckIn(checkInId, userId, managerId, LocalDate.now(), LocalDate.now(), emptyMap(), "Good", CheckInStatus.ON_TRACK, LocalDate.now()))

        mockMvc.perform(
            post("/checkins/new/$userId")
                .param("targetGradeId", targetGradeId.toString())
                .param("managerNotes", "Excellent progress this quarter.")
                .with(csrf())
        )
            .andExpect(status().is3xxRedirection)
            .andExpect(redirectedUrl("/checkins/user/$userId"))
    }
}
