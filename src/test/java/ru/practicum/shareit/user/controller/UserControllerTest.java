package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    UserService userService;

    private final UserDto userDto = UserDto.builder().name("user").email("user@user.com").build();

    @SneakyThrows
    @Test
    void saveNewUser() {
        when(userService.saveUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(post("/users")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService, times(1)).saveUser(any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void getAllUsers() {
        when(userService.getAll()).thenReturn(List.of(userDto));

        mockMvc.perform(get("/users")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(userDto))));

        verify(userService, times(1)).getAll();
    }

    @SneakyThrows
    @Test
    void getUser() {
        when(userService.getById(anyInt())).thenReturn(userDto);

        mockMvc.perform(get("/users/{userId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService, times(1)).getById(anyInt());
    }

    @SneakyThrows
    @Test
    void updateUser() {
        userDto.setName("updateUser");
        when(userService.updateUser(anyInt(), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(patch("/users/{userId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("updateUser")))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)));

        verify(userService, times(1)).updateUser(anyInt(), any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mockMvc.perform(delete("/users/{userId}", 1))
                .andExpect(status().isOk());

        verify(userService).delete(anyInt());
    }
}