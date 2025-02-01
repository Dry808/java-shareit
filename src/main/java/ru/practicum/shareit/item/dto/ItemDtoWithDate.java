package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemDtoWithDate {
    private int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private Boolean available;
    private Integer request;
    private LocalDateTime lastBooking;
    private LocalDateTime nextBooking;
}