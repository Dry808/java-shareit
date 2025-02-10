package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingCreateDtoTest {
    private final JacksonTester<BookingCreateDto> json;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    void testBookingCreateDto() throws Exception {
        // Создаем объект BookingCreateDto
        Instant start = Instant.parse("2023-10-01T10:00:00Z");
        Instant end = Instant.parse("2023-10-01T12:00:00Z");
        BookingCreateDto bookingCreateDto = new BookingCreateDto(1, start, end, 1, 1, "APPROVED");

        // Сериализуем объект в JSON
        JsonContent<BookingCreateDto> result = json.write(bookingCreateDto);

        // Проверяем, что JSON содержит ожидаемые значения
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2023-10-01T10:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2023-10-01T12:00:00");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("APPROVED");
    }
}

