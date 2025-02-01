package ru.practicum.shareit.booking.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

@Component
@RequiredArgsConstructor
public class BookingMapper {

    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus().toString()
        );
    }

    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(ItemMapper.toItem(bookingDto.getItem()))
                .booker(UserMapper.toUser(bookingDto.getBooker()))
                .status(BookingStatus.WAITING)
                .build();
    }

    public static Booking toBooking(BookingCreateDto bookingCreateDto) {
        return Booking.builder()
                .id(bookingCreateDto.getId())
                .start(Instant.from(bookingCreateDto.getStart()))
                .end(Instant.from((bookingCreateDto.getEnd())))
                .item(new Item())
                .booker(new User())
                .status(BookingStatus.WAITING)
                .build();
    }



}
