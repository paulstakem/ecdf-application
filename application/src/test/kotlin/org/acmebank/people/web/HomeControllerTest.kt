package org.acmebank.people.web

import org.junit.jupiter.api.Test
import org.acmebank.people.domain.port.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import java.util.Optional
import java.util.UUID
import org.acmebank.people.domain.User
import org.acmebank.people.domain.Grade
import org.mockito.Mockito.`when`

@WebMvcTest(HomeController::class)
class HomeControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var userRepository: UserRepository

    @Test
    @WithMockUser(username = "user@example.com")
    fun `should return index view on root request`() {
        val mockUser = User(
            UUID.randomUUID(), "user@example.com", "Engineer Bob",
            Grade(UUID.randomUUID(), "Software Engineer", "Engineering", emptyMap()),
            null, false
        )
        `when`(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(mockUser))

        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
            .andExpect(view().name("index"))
    }
}
