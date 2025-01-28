package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;

@AllArgsConstructor
@Data
@Getter
public class BookingDto {
        private int id;
        private Instant start;
        private Instant end;
        private ItemDto item;
        private UserDto booker;
        private String status;
}
