package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final BookingCreateDto bookingCreate = new BookingCreateDto(
            1,
            Instant.now(),
            Instant.now().plusSeconds(88000),
            2,
            3,
            "WAITING");

    private final BookingDto booking = new BookingDto(
            1,
            Instant.now(),
            Instant.now().plusSeconds(88000),
            new ItemDto(2, "ItemTest", "test", true, 1, null),
            new UserDto(3, "Name Surname", "mail@example.com"),
            "WAITING");

    @Test
    void testCreateBooking() throws Exception {
        when(bookingService.createBooking(any(BookingCreateDto.class), anyInt())).thenReturn(booking);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 3)
                        .content(mapper.writeValueAsString(bookingCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId())))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.item.available", is(booking.getItem().getAvailable())))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId())))
                .andExpect(jsonPath("$.booker.email", is(booking.getBooker().getEmail())))
                .andExpect(jsonPath("$.status", is(booking.getStatus())));
    }

    @Test
    void testApproveBooking() throws Exception {
        int userId = 1;
        int bookingId = 1;
        boolean approved = true;
        BookingDto bookingResponse = new BookingDto(
                1,
                Instant.now(),
                Instant.now().plusSeconds(88000),
                new ItemDto(2, "ItemTest", "test", true, 1, null),
                new UserDto(3, "Name Surname", "mail@example.com"),
                "WAITING");

        when(bookingService.approveBooking(anyInt(), anyBoolean(), anyInt())).thenReturn(bookingResponse);

        mvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .param("approved", "false")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId())))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingResponse.getItem().getName())))
                .andExpect(jsonPath("$.item.available", is(bookingResponse.getItem().getAvailable())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId())))
                .andExpect(jsonPath("$.booker.email", is(bookingResponse.getBooker().getEmail())))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus())));
    }

    @Test
    void testGetBooking() throws Exception {
        int userId = 1;
        int bookingId = 1;
        BookingDto bookingResponse = new BookingDto(
                1,
                Instant.now(),
                Instant.now().plusSeconds(88000),
                new ItemDto(2, "ItemTest", "test", true, 1, null),
                new UserDto(3, "Name Surname", "mail@example.com"),
                "WAITING");

        when(bookingService.getBooking(anyInt(), anyInt())).thenReturn(bookingResponse);

        mvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponse.getId()), Integer.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponse.getItem().getId())))
                .andExpect(jsonPath("$.item.name", is(bookingResponse.getItem().getName())))
                .andExpect(jsonPath("$.item.available", is(bookingResponse.getItem().getAvailable())))
                .andExpect(jsonPath("$.booker.id", is(bookingResponse.getBooker().getId())))
                .andExpect(jsonPath("$.booker.email", is(bookingResponse.getBooker().getEmail())))
                .andExpect(jsonPath("$.status", is(bookingResponse.getStatus())));
    }

    @Test
    void testGetAllBooking() throws Exception {
        int userId = 1;
        BookingState state = BookingState.ALL;
        List<BookingDto> bookingResponse = Arrays.asList(
                new BookingDto(1, Instant.now(), Instant.now().plusSeconds(88000), new ItemDto(2, "ItemTest", "test", true, 1, null), new UserDto(3, "Name Surname", "mail@example.com"), "WAITING"),
                new BookingDto(2, Instant.now().plusSeconds(88000), Instant.now().plusSeconds(176000), new ItemDto(3, "ItemTest2", "test2", true, 1, null), new UserDto(3, "Name Surname", "mail@example.com"), "APPROVED")
        );

        when(bookingService.getAll(any(BookingState.class), anyInt())).thenReturn(bookingResponse);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingResponse.get(0).getId()), Integer.class))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.get(0).getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponse.get(0).getItem().getName())))
                .andExpect(jsonPath("$[0].item.available", is(bookingResponse.get(0).getItem().getAvailable())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.get(0).getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.email", is(bookingResponse.get(0).getBooker().getEmail())))
                .andExpect(jsonPath("$[0].status", is(bookingResponse.get(0).getStatus())))
                .andExpect(jsonPath("$[1].id", is(bookingResponse.get(1).getId()), Integer.class))
                .andExpect(jsonPath("$[1].item.id", is(bookingResponse.get(1).getItem().getId())))
                .andExpect(jsonPath("$[1].item.name", is(bookingResponse.get(1).getItem().getName())))
                .andExpect(jsonPath("$[1].item.available", is(bookingResponse.get(1).getItem().getAvailable())))
                .andExpect(jsonPath("$[1].booker.id", is(bookingResponse.get(1).getBooker().getId())))
                .andExpect(jsonPath("$[1].booker.email", is(bookingResponse.get(1).getBooker().getEmail())))
                .andExpect(jsonPath("$[1].status", is(bookingResponse.get(1).getStatus())));
    }

    @Test
    void testGetAllBookingsByOwner() throws Exception {
        int userId = 1;
        BookingState state = BookingState.ALL;
        List<BookingDto> bookingResponse = Arrays.asList(
                new BookingDto(1, Instant.now(), Instant.now().plusSeconds(88000), new ItemDto(2, "ItemTest", "test", true, 1, null), new UserDto(3, "Name Surname", "mail@example.com"), "WAITING"),
                new BookingDto(2, Instant.now().plusSeconds(88000), Instant.now().plusSeconds(176000), new ItemDto(3, "ItemTest2", "test2", true, 1, null), new UserDto(3, "Name Surname", "mail@example.com"), "APPROVED")
        );

        when(bookingService.getAllByOwners(any(BookingState.class), anyInt())).thenReturn(bookingResponse);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", state.name())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookingResponse.get(0).getId())))
                .andExpect(jsonPath("$[0].item.id", is(bookingResponse.get(0).getItem().getId())))
                .andExpect(jsonPath("$[0].item.name", is(bookingResponse.get(0).getItem().getName())))
                .andExpect(jsonPath("$[0].item.available", is(bookingResponse.get(0).getItem().getAvailable())))
                .andExpect(jsonPath("$[0].booker.id", is(bookingResponse.get(0).getBooker().getId())))
                .andExpect(jsonPath("$[0].booker.email", is(bookingResponse.get(0).getBooker().getEmail())))
                .andExpect(jsonPath("$[0].status", is(bookingResponse.get(0).getStatus())))
                .andExpect(jsonPath("$[1].id", is(bookingResponse.get(1).getId()), Integer.class))
                .andExpect(jsonPath("$[1].item.id", is(bookingResponse.get(1).getItem().getId())))
                .andExpect(jsonPath("$[1].item.name", is(bookingResponse.get(1).getItem().getName())))
                .andExpect(jsonPath("$[1].item.available", is(bookingResponse.get(1).getItem().getAvailable())))
                .andExpect(jsonPath("$[1].booker.id", is(bookingResponse.get(1).getBooker().getId())))
                .andExpect(jsonPath("$[1].booker.email", is(bookingResponse.get(1).getBooker().getEmail())));
    }
}