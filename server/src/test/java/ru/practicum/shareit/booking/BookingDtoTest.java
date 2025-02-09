package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingDtoTest {
    private final JacksonTester<BookingDto> json;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    void testBookingDto() throws Exception {
        // Создаем объекты ItemDto и UserDto
        ItemDto itemDto = new ItemDto(1, "Item Name", "Item Description", true, null, new ArrayList<>());
        UserDto userDto = new UserDto(1, "User Name", "user@example.com");

        // Создаем объект BookingDto
        Instant start = Instant.parse("2023-10-01T10:00:00Z");
        Instant end = Instant.parse("2023-10-01T12:00:00Z");
        BookingDto bookingDto = new BookingDto(1, start, end, itemDto, userDto, "APPROVED");

        // Сериализуем объект в JSON
        JsonContent<BookingDto> result = json.write(bookingDto);

        // Проверяем, что JSON содержит ожидаемые значения
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-10-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-10-01T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("Item Name");
        assertThat(result).extractingJsonPathStringValue("$.item.description").isEqualTo("Item Description");
        assertThat(result).extractingJsonPathBooleanValue("$.item.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("User Name");
        assertThat(result).extractingJsonPathStringValue("$.booker.email").isEqualTo("user@example.com");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}
