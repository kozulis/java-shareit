package ru.practicum.server.booking.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.server.booking.dto.BookingDto;
import ru.practicum.server.booking.dto.BookingMapper;
import ru.practicum.server.booking.dto.BookingResponseDto;
import ru.practicum.server.booking.model.Booking;
import ru.practicum.server.booking.model.BookingStatus;
import ru.practicum.server.booking.repository.BookingRepository;
import ru.practicum.server.exception.NotFoundException;
import ru.practicum.server.exception.UnknownBookingStateException;
import ru.practicum.server.exception.ValidationException;
import ru.practicum.server.item.model.Item;
import ru.practicum.server.item.repository.ItemRepository;
import ru.practicum.server.user.model.User;
import ru.practicum.server.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private BookingServiceImpl bookingService;

    LocalDateTime now = LocalDateTime.now();

    private final User booker = User.builder().id(1).name("booker").email("booker@user.com").build();
    private final User owner = User.builder().id(2).name("owner").email("owner@user.com").build();
    private final Item item = Item.builder().id(3).name("itemName").description("itemDescription").available(true)
            .owner(owner).requestId(1).build();
    private final BookingDto bookingDto = BookingDto.builder().id(1).start(now.plusHours(1)).end(now.plusHours(2))
            .itemId(1).bookerId(1).build();
    private final Booking booking = BookingMapper.toBooking(bookingDto, item, booker);

    @Test
    @DisplayName("Добавление бронирования")
    void saveBooking_returnBooking() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto actualBookingResponseDto = bookingService.saveBooking(booker.getId(), bookingDto);
        BookingResponseDto expectBookingResponseDto = BookingMapper.toBookingResponseDto(booking);

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Ошибка при добавлении бронирования, если пользователь не найден")
    void saveBooking_whenWrongUserId_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.saveBooking(booker.getId(), bookingDto));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, never()).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка при добавлении бронирования, если вещь не найдена")
    void saveBooking_whenWrongItemId_thanNotFoundExceptionThrown() {
        int wrongItemId = 100;
        bookingDto.setItemId(wrongItemId);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(wrongItemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.saveBooking(wrongItemId, bookingDto));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).findById(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка при добавлении бронирования, если вещь не доступна для бронирования")
    void saveBooking_whenItemNotAvailable_thanValidationExceptionThrown() {
        item.setAvailable(false);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(ValidationException.class, () -> bookingService.saveBooking(item.getId(), bookingDto));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).findById(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка при добавлении бронирования при попытке забронировать собственную вещь")
    void saveBooking_whenOwnerEqualUser_thanNotFoundExceptionThrown() {
        item.setOwner(booker);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> bookingService.saveBooking(booker.getId(), bookingDto));
        verify(userRepository, times(1)).findById(anyInt());
        verify(itemRepository, times(1)).findById(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка при добавлении бронирования, если задано некорректное время")
    void saveBooking_whenIncorrectBookingTime_thanNotValidationExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        bookingDto.setStart(now);
        bookingDto.setEnd(now.minusHours(1));
        assertThrows(ValidationException.class, () -> bookingService.saveBooking(booker.getId(), bookingDto));
        bookingDto.setStart(now);
        bookingDto.setEnd(now);
        assertThrows(ValidationException.class, () -> bookingService.saveBooking(booker.getId(), bookingDto));

        verify(userRepository, times(2)).findById(anyInt());
        verify(itemRepository, times(2)).findById(anyInt());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Подтверждение бронирования")
    void approveBooking_returnApprove() {
        Integer bookerId = booking.getItem().getOwner().getId();
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto actualBookingResponseDto = bookingService.approveBooking(bookerId,
                booking.getId(), true);
        BookingResponseDto expectBookingResponseDto = BookingMapper.toBookingResponseDto(booking);

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Ошибка подтверждения бронирования, если бронирование не найдено")
    void approveBooking_whenWrongBookingId_thanNotFoundExceptionThrown() {
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(booking.getId(),
                booking.getId(), true));
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Отклонение запроса на бронирование")
    void approveBooking_whenBookingStatusSwitchToRejected_thanReturnApprove() {
        Integer bookerId = booking.getItem().getOwner().getId();
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingResponseDto actualBookingResponseDto = bookingService.approveBooking(bookerId,
                booking.getId(), false);

        assertEquals(actualBookingResponseDto.getStatus(), BookingStatus.REJECTED);
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Ошибка подтверждения бронирования, если бронирование отклонено")
    void approveBooking_whenBookingStatusIsRejected_thanValidationExceptionThrown() {
        Integer bookerId = booking.getItem().getOwner().getId();
        booking.setStatus(BookingStatus.REJECTED);
        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(ValidationException.class, () -> bookingService.approveBooking(bookerId,
                booking.getId(), true));
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ошибка подтверждения бронирования при попытке забронировать собственную вещь")
    void approveBooking_whenBookingOwnerEqualsItemOwner_thanNotFoundExceptionThrown() {
        Integer bookerId = booking.getItem().getOwner().getId() + 1;

        when(bookingRepository.findById(anyInt())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.approveBooking(bookerId,
                booking.getId(), true));
        verify(bookingRepository, times(1)).findById(any());
        verify(bookingRepository, never()).save(any());
    }


    @Test
    @DisplayName("Получение бронирования по id")
    void getById_whenBookingFound_returnBooking() {
        Integer itemOwnerId = booking.getItem().getOwner().getId();
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        BookingResponseDto actualBookingResponseDto = bookingService.getById(itemOwnerId, booking.getId());
        BookingResponseDto expectBookingResponseDto = BookingMapper.toBookingResponseDto(booking);

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(bookingRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Ошибка получения бронирования по id, если владелец бронирования не найден")
    void getById_whenBookerFound_thanNotFoundExceptionThrown() {
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getById(booker.getId(), booking.getId()));

        verify(bookingRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Ошибка получения бронирования по id при запросе пользователем, не являющимся " +
            "владельцем вещи или владельцем бронирования")
    void getById_whenBookerOrItemOwnerNotEqualsBookingOwner_thanNotFoundExceptionThrown() {
        Integer bookerId = booking.getBooker().getId() + 1;
        Integer itemOwnerId = booking.getItem().getOwner().getId() + 1;
        bookingDto.setBookerId(bookerId + 1);
        owner.setId(itemOwnerId);
        when(bookingRepository.findById(booking.getId())).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getById(bookerId, booking.getId()));

        verify(bookingRepository, times(1)).findById(anyInt());
    }

    @Test
    @DisplayName("Получения списка бронирований пользователя")
    void getByUser_returnListOfBookings() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByBooker(any(), any())).thenReturn(List.of(booking));

        List<BookingResponseDto> actualBookingResponseDto = bookingService.getByUser(
                booker.getId(), "ALL", 0, 10);
        List<BookingResponseDto> expectBookingResponseDto = List.of(BookingMapper.toBookingResponseDto(booking));

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1)).findByBooker(any(), any());
    }

    @Test
    @DisplayName("Ошибка получения бронирования, если владелец бронирования не найден")
    void getByUser_whenBookerNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getByUser(booker.getId(), "ALL", 0, 10));

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, never()).findByBooker(any(), any());
    }


    @Test
    @DisplayName("Получения списка бронирований владельца вещи")
    void getByItemsOwner_returnListOfBookings() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItem_Owner(any(User.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingResponseDto> actualBookingResponseDto = bookingService.getByItemsOwner(
                owner.getId(), "ALL", 0, 10);
        List<BookingResponseDto> expectBookingResponseDto = List.of(BookingMapper.toBookingResponseDto(booking));

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1)).findByItem_Owner(any(User.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Ошибка получения бронирования, если владелец вещи не найден")
    void getByItemsOwner_whenItemOwnerNotFound_thanNotFoundExceptionThrown() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getByItemsOwner(owner.getId(), "ALL", 0, 10));

        verify(userRepository, times(1)).findById(any());
        verify(bookingRepository, never()).findByItem_Owner(any(User.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Получение списка бронирований со любым статусом")
    void getByItemsOwner_whenBookingStateIsALL_thanReturnListOfBookings() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItem_Owner(any(User.class), any(Pageable.class))).thenReturn(List.of(booking));

        List<BookingResponseDto> actualBookingResponseDto = bookingService.getByItemsOwner(
                owner.getId(), "ALL", 0, 10);
        List<BookingResponseDto> expectBookingResponseDto = List.of(BookingMapper.toBookingResponseDto(booking));

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1)).findByItem_Owner(any(User.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Ошибка получения бронирований с неизвестным статусом ")
    void getByItemsOwner_whenBookingByStateIsWrong_thanReturnListOfBookings() {
        assertThrows(UnknownBookingStateException.class, () -> bookingService.getByItemsOwner(
                owner.getId(), "WRONG", 0, 10));
        verify(userRepository, never()).findById(anyInt());
        verify(bookingRepository, never()).findByItem_Owner(any(User.class), any(Pageable.class));
    }


    @Test
    @DisplayName("Получение списка бронирований со статусом 'Текущие'")
    void getByItemsOwner_whenBookingByStateIsCurrent_thanReturnListOfBookings() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItem_Owner(any(User.class), any(Pageable.class))).thenReturn(List.of(booking));

        booking.setStart(now.minusHours(1));
        booking.setEnd(now.plusHours(1));
        List<BookingResponseDto> actualBookingResponseDto = bookingService.getByItemsOwner(
                owner.getId(), "CURRENT", 0, 10);
        List<BookingResponseDto> expectBookingResponseDto = List.of(BookingMapper.toBookingResponseDto(booking));

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1)).findByItem_Owner(any(User.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Получение списка бронирований со статусом 'Прошедние'")
    void getByItemsOwner_whenBookingByStateIsPAST_thanReturnListOfBookings() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItem_Owner(any(User.class), any(Pageable.class))).thenReturn(List.of(booking));

        booking.setStart(now.minusHours(2));
        booking.setEnd(now.minusHours(1));
        List<BookingResponseDto> actualBookingResponseDto = bookingService.getByItemsOwner(
                owner.getId(), "PAST", 0, 10);
        List<BookingResponseDto> expectBookingResponseDto = List.of(BookingMapper.toBookingResponseDto(booking));

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1)).findByItem_Owner(any(User.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Получение списка бронирований со статусом 'Будущие'")
    void getByItemsOwner_whenBookingByStateIsFUTURE_thanReturnListOfBookings() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItem_Owner(any(User.class), any(Pageable.class))).thenReturn(List.of(booking));

        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        List<BookingResponseDto> actualBookingResponseDto = bookingService.getByItemsOwner(
                owner.getId(), "FUTURE", 0, 10);
        List<BookingResponseDto> expectBookingResponseDto = List.of(BookingMapper.toBookingResponseDto(booking));

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1)).findByItem_Owner(any(User.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Получение списка бронирований со статусом 'В ожидании'")
    void getByItemsOwner_whenBookingByStateIsWAITING_thanReturnListOfBookings() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItem_Owner(any(User.class), any(Pageable.class))).thenReturn(List.of(booking));

        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        List<BookingResponseDto> actualBookingResponseDto = bookingService.getByItemsOwner(
                owner.getId(), "WAITING", 0, 10);
        List<BookingResponseDto> expectBookingResponseDto = List.of(BookingMapper.toBookingResponseDto(booking));

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1)).findByItem_Owner(any(User.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Получение списка бронирований со статусом 'Отклонены'")
    void getByItemsOwner_whenBookingByStateIsREJECTED_thanReturnListOfBookings() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(booker));
        when(bookingRepository.findByItem_Owner(any(User.class), any(Pageable.class))).thenReturn(List.of(booking));

        booking.setStart(now.plusHours(1));
        booking.setEnd(now.plusHours(2));
        booking.setStatus(BookingStatus.REJECTED);
        List<BookingResponseDto> actualBookingResponseDto = bookingService.getByItemsOwner(
                owner.getId(), "REJECTED", 0, 10);
        List<BookingResponseDto> expectBookingResponseDto = List.of(BookingMapper.toBookingResponseDto(booking));

        assertEquals(actualBookingResponseDto, expectBookingResponseDto);
        verify(userRepository, times(1)).findById(anyInt());
        verify(bookingRepository, times(1)).findByItem_Owner(any(User.class), any(Pageable.class));
    }

}