package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;

import lombok.*;


import java.time.Instant;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class BookingCreateDto {
        private int id;
        @FutureOrPresent
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
        private Instant start;
        @Future
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
        private Instant end;
        private Integer itemId;
        private Integer booker;
        private String status;
}
