package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoTest {

    @Autowired
    private JacksonTester<ItemDto> json;

    @BeforeEach
    public void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonTester.initFields(this, objectMapper);
    }

    @Test
    void testSerialize() throws IOException {
        CommentDto commentDto1 = new CommentDto(1, "Comment 1", "Author 1", null);
        CommentDto commentDto2 = new CommentDto(2, "Comment 2", "Author 2", null);
        ItemDto itemDto = new ItemDto(1, "Item Name", "Item Description", true, 10, Arrays.asList(commentDto1, commentDto2));

        String jsonContent = json.write(itemDto).getJson();

        // Проверяем, что JSON содержит ожидаемые значения
        assertThat(jsonContent).contains("\"id\":1");
        assertThat(jsonContent).contains("\"name\":\"Item Name\"");
        assertThat(jsonContent).contains("\"description\":\"Item Description\"");
        assertThat(jsonContent).contains("\"available\":true");
        assertThat(jsonContent).contains("\"requestId\":10");
    }

    @Test
    void testDeserialize() throws IOException {
        String jsonContent = "{" +
                "\"id\":1," +
                "\"name\":\"Item Name\"," +
                "\"description\":\"Item Description\"," +
                "\"available\":true," +
                "\"requestId\":10," +
                "\"comments\":[{\"id\":1,\"text\":\"Comment 1\",\"authorName\":\"Author 1\"},{\"id\":2,\"text\":\"Comment 2\",\"authorName\":\"Author 2\"}]" +
                "}";
        ItemDto itemDto = json.parse(jsonContent).getObject();

        // Проверяем что объект содержит ожидаемые значения
        assertThat(itemDto.getId()).isEqualTo(1);
        assertThat(itemDto.getName()).isEqualTo("Item Name");
        assertThat(itemDto.getDescription()).isEqualTo("Item Description");
        assertThat(itemDto.getAvailable()).isTrue();
        assertThat(itemDto.getRequest()).isEqualTo(10);
        assertThat(itemDto.getComments()).hasSize(2);
        assertThat(itemDto.getComments().get(0).getText()).isEqualTo("Comment 1");
        assertThat(itemDto.getComments().get(0).getAuthorName()).isEqualTo("Author 1");
        assertThat(itemDto.getComments().get(1).getText()).isEqualTo("Comment 2");
        assertThat(itemDto.getComments().get(1).getAuthorName()).isEqualTo("Author 2");
    }
}
