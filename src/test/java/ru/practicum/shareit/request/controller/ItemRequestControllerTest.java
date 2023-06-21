package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    ItemRequestService itemRequestService;

    ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("Какое-то описание").build();

    @SneakyThrows
    @Test
    @DisplayName("Добавление запроса на вещь")
    void saveNewRequest() {
        when(itemRequestService.saveItemRequest(anyInt(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));

        verify(itemRequestService, times(1)).saveItemRequest(anyInt(), any(ItemRequestDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение списка собственных запросов")
    void getOwnRequests() {
        when(itemRequestService.getOwnRequests(anyInt())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestService, times(1)).getOwnRequests(anyInt());
    }

    @SneakyThrows

    @Test
    @DisplayName("Получение списка всех запросов")
    void getAllRequests() {
        when(itemRequestService.getAllRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(itemRequestDto));

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(List.of(itemRequestDto))));

        verify(itemRequestService, times(1)).getAllRequests(anyInt(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение запроса по id")
    void getRequestById() {
        when(itemRequestService.getById(anyInt(), anyInt())).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemRequestDto)));

        verify(itemRequestService, times(1)).getById(anyInt(), anyInt());
    }
}