package ru.practicum.shareit.booking.controller;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingService bookingService;

    private final UserDto bookerDto = UserDto.builder().id(1).name("user").email("user@user.com").build();
    private final ItemDto itemDto = ItemDto.builder().id(1).name("drill").description("simple drill")
            .available(true).build();
    private final BookingDto bookingDto = BookingDto.builder().start(LocalDateTime.now().plusHours(1))
            .end(LocalDateTime.now().plusHours(2)).itemId(1).build();
    private final BookingResponseDto bookingResponseDto = BookingResponseDto.builder().id(1).start(bookingDto.getStart())
            .end(bookingDto.getEnd()).item(itemDto).booker(bookerDto).status(BookingStatus.WAITING).build();
    private final BookingResponseDto approvedBookingResponseDto = BookingResponseDto.builder().id(1).start(bookingDto
                    .getStart()).end(bookingDto.getEnd()).item(itemDto).booker(bookerDto)
            .status(BookingStatus.APPROVED).build();

    @SneakyThrows
    @Test
    @DisplayName("Добавление бронирования")
    void saveNewBooking() {
        when(bookingService.saveBooking(anyInt(), any(BookingDto.class))).thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingResponseDto)));

        verify(bookingService, times(1)).saveBooking(anyInt(), any(BookingDto.class));
    }

    @SneakyThrows
    @Test
    @DisplayName("Подтверждение бронирования")
    void approveBooking() {
        when(bookingService.approveBooking(anyInt(), anyInt(), anyBoolean())).thenReturn(approvedBookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("approved", "true"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(approvedBookingResponseDto)));

        verify(bookingService, times(1)).approveBooking(anyInt(), anyInt(), anyBoolean());
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение бронирования по id")
    void getBookingById() {
        when(bookingService.getById(anyInt(), anyInt())).thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(bookingResponseDto)));

        verify(bookingService, times(1)).getById(anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение списка бронирований пользователя")
    void getBookingsByUser() {
        when(bookingService.getByUser(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto, approvedBookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        List.of(bookingResponseDto, approvedBookingResponseDto))));

        verify(bookingService, times(1)).getByUser(anyInt(), anyString(), anyInt(), anyInt());
    }

    @SneakyThrows
    @Test
    @DisplayName("Получение списка бронирований владельца вещи")
    void getBookingsByItemsOwner() {
        when(bookingService.getByItemsOwner(anyInt(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(content().json(objectMapper.writeValueAsString(
                        List.of(bookingResponseDto))));

        verify(bookingService, times(1)).getByItemsOwner(anyInt(), anyString(), anyInt(), anyInt());
    }
}