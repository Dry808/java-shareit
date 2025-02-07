package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;



import lombok.*;


import java.time.Instant;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class BookingCreateDto {
        private int id;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
        private Instant start;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
        private Instant end;
        private Integer itemId;
        private Integer booker;
        private String status;
}
