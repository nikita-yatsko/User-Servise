package com.user.servise.User.Servise.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.userservise.app.UserServiseApplication;
import com.userservise.app.model.entity.User;
import com.userservise.app.model.enums.ActiveStatus;
import com.userservise.app.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;


@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(classes = UserServiseApplication.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User savedUser;

    @BeforeAll
    void setUp() {
        savedUser = new User();
        savedUser.setName("Test");
        savedUser.setSurname("User");
        savedUser.setEmail("testUserMail@mail.com");
        savedUser.setBirthDate(LocalDate.now());
        savedUser.setActive(ActiveStatus.ACTIVE);

        savedUser = userRepository.saveAndFlush(savedUser);
    }

    @AfterAll
    void tearDown() {
        if (savedUser != null && savedUser.getId() != null)
            userRepository.deleteById(savedUser.getId());
    }

    @Test
    public void getUserById_200_Ok() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                    .get("/api/user/" + savedUser.getId())
                    .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getUserById_404_NotFound() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void getAllUsers_200_Ok() throws Exception {
        String firstName = "Test";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/user/all")
                        .param("firstName", firstName)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value(savedUser.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].surname").value(savedUser.getSurname()));

    }

    @Test
    public void createUser_200_Ok() throws Exception {

    }
}
