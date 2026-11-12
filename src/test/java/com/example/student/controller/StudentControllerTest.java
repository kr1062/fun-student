package com.example.student.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void findAll_WithoutAuth_ShouldReturn401Or403() throws Exception {
        mockMvc.perform(get("/api/v1/student"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void findAll_AsTeacher_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/api/v1/student"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "TEACHER")
    void createStudent_AsTeacher_ShouldReturn403() throws Exception {
        String json = "{\"name\":\"Test\", \"surname\":\"Test\"}";

        mockMvc.perform(post("/api/v1/student/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStudent_AsAdmin_ShouldReturn200() throws Exception {
        String json = "{\"name\":\"Test\", \"surname\":\"Test\"}";

        mockMvc.perform(post("/api/v1/student/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());
    }
}