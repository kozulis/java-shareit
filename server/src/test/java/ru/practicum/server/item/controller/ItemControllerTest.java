package ru.practicum.server.item.controller;

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
import ru.practicum.server.item.dto.CommentDto;
import ru.practicum.server.item.dto.ItemDto;
import ru.practicum.server.item.service.CommentService;
import ru.practicum.server.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;

    private final ItemDto goodItemDto = ItemDto.builder().name("Весчь").description("Замечательная весчь")
            .available(true).build();
    private final ItemDto badItemDto = ItemDto.builder().name("Вещъ").description("Ужасная вещъ")
            .available(true).build();
    private final CommentDto commentDto = CommentDto.builder().text("Комментарий").itemId(1).authorName("Серёжа").build();


    @SneakyThrows
    @Test
    @DisplayName("Добавление вещи")
    void saveNewItem() {
        when(itemService.saveItem(anyInt(), any(ItemDto.class))).thenReturn(goodItemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(goodItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(goodItemDto)));

        verify(itemService, times(1)).saveItem(anyInt(), any(ItemDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение списка вещей пользователя")
    void getAllItemsByUserId() {
        when(itemService.getAllByUserId(anyInt(), anyInt(), anyInt())).thenReturn(List.of(goodItemDto, badItemDto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        List.of(goodItemDto, badItemDto))));

        verify(itemService).getAllByUserId(anyInt(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение вещи по id")
    void getItem() {
        when(itemService.getById(anyInt(), anyInt())).thenReturn(goodItemDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(goodItemDto)));

        verify(itemService, times(1)).getById(anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    @DisplayName("Обновление параметров вещи")
    void updateItem() {
        when(itemService.updateItem(anyInt(), anyInt(), any(ItemDto.class))).thenReturn(goodItemDto);
        goodItemDto.setDescription("Бомбезная весчь!");

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(goodItemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is("Бомбезная весчь!")))
                .andExpect(content().json(objectMapper.writeValueAsString(goodItemDto)));

        verify(itemService, times(1)).updateItem(anyInt(), anyInt(), any(ItemDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("Удаление вещи по id")
    void deleteItem() {
        mockMvc.perform(delete("/items/{itemId}", 1))
                .andExpect(status().isOk());

        verify(itemService).deleteById(anyInt());
    }

    @SneakyThrows
    @Test
    @DisplayName("Поиск вещи по названию или описанию")
    void searchItem() {
        when(itemService.searchItem(anyInt(), anyString(), anyInt(), anyInt())).thenReturn((List.of(goodItemDto)));

        mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "весчь"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString((List.of(goodItemDto)))));

        verify(itemService, times(1)).searchItem(anyInt(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    @DisplayName("Добавление комментария к вещи после бронирования")
    void saveNewComment() {
        when(commentService.saveComment(anyInt(), anyInt(), any(CommentDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));

        verify(commentService, times(1)).saveComment(anyInt(), anyInt(), any(CommentDto.class));
    }
}