package ru.practicum.shareit.ItemRequest.dto;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestDtoTest {
    private final JacksonTester<ItemRequestDto> json;

    @Test
    void testItemRequestDto() throws Exception {
        User user = new User(1, "name", "exe@mail.ru");
        List<ItemDto> items = new ArrayList<>();
        items.add(new ItemDto(1, "Item 1", "Description 1", true, null, new ArrayList<>()));
        items.add(new ItemDto(2, "Item 2", "Description 2", false, null, new ArrayList<>()));

        ItemRequestDto request = new ItemRequestDto(
                1,
                "Description",
                user,
                Instant.now(),
                items);

        JsonContent<ItemRequestDto> result = json.write(request);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Description");
        assertThat(result).extractingJsonPathNumberValue("$.requestor.id").isEqualTo(user.getId());
        assertThat(result).extractingJsonPathStringValue("$.requestor.name").isEqualTo(user.getName());
        assertThat(result).extractingJsonPathStringValue("$.requestor.email").isEqualTo(user.getEmail());
        assertThat(result).extractingJsonPathStringValue("$.created").isNotEmpty();
        assertThat(result).extractingJsonPathArrayValue("$.items").hasSize(2);
        assertThat(result).extractingJsonPathNumberValue("$.items[0].id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.items[0].name").isEqualTo("Item 1");
        assertThat(result).extractingJsonPathStringValue("$.items[0].description").isEqualTo("Description 1");
        assertThat(result).extractingJsonPathBooleanValue("$.items[0].available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.items[1].id").isEqualTo(2);
        assertThat(result).extractingJsonPathStringValue("$.items[1].name").isEqualTo("Item 2");
        assertThat(result).extractingJsonPathStringValue("$.items[1].description").isEqualTo("Description 2");
        assertThat(result).extractingJsonPathBooleanValue("$.items[1].available").isFalse();
    }
}
