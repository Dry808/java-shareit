package ru.practicum.shareit.item;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithDate;
import ru.practicum.shareit.item.service.ItemService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final CommentDto commentDto = new CommentDto(1, "Good", "John Doe", LocalDateTime.parse("2024-01-01T12:34:56"));

    private final ItemDto itemDto = new ItemDto(
            1,
            "Item",
            "TestDescr",
            true,
            1,
            List.of(commentDto));

    private final ItemDtoWithDate itemDtoWithDate = new ItemDtoWithDate(
            1,
            "Item with dates",
            "TestDescr",
            true,
            1,
            LocalDateTime.parse("2024-11-03T11:11:11"),
            LocalDateTime.parse("2024-12-03T11:11:11"),
            List.of(commentDto)
    );

    @Test
    public void testCreateItem() throws Exception {
        when(itemService.createItem(anyInt(), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    public void testCreateComment() throws Exception {
        when(itemService.createComment(anyInt(), anyInt(), any(CommentDto.class))).thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));
    }

    @Test
    public void testUpdateItem() throws Exception {
        when(itemService.updateItem(anyInt(), anyInt(), any(ItemDto.class))).thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));
    }

    @Test
    public void testGetItem() throws Exception {
        when(itemService.getItem(anyInt())).thenReturn(itemDtoWithDate);

        mvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(itemDtoWithDate)));
    }

    @Test
    public void testGetAllItems() throws Exception {
        List<ItemDtoWithDate> items = Arrays.asList(
                new ItemDtoWithDate(1, "Item1", "Description1", true, 2, LocalDateTime.now(), LocalDateTime.now(), List.of(commentDto)),
                new ItemDtoWithDate(2, "Item2", "Description2", true, 2, LocalDateTime.now(), LocalDateTime.now(), List.of(commentDto))
        );
        when(itemService.getAllItems(anyInt())).thenReturn(items);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    @Test
    void testSearch() throws Exception {
        when(itemService.searchItem(anyString())).thenReturn(List.of(itemDto));

        mvc.perform(get("/items/search")
                        .param("text", "name")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(itemDto.getId()), Integer.class))
                .andExpect(jsonPath("$[0].name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$[0].description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$[0].available", is(itemDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$[0].comments[0].text", is(itemDto.getComments().getFirst().getText()), String.class))
                .andExpect(jsonPath("$[0].id", is(itemDto.getRequest()), Integer.class));
    }
}

