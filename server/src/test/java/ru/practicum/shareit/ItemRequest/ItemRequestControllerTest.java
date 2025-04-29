package ru.practicum.shareit.ItemRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService requestService;

    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto request = new ItemRequestDto(
            1,
            "Description",
            new User(1,"Done", "exemple@mail.ru"),
            Instant.now(),
            new ArrayList<>());

    @Test
    void testCreate() throws Exception {
        when(requestService.createRequest(anyInt(), any(ItemRequestDto.class))).thenReturn(request);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(request))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(request.getDescription()), String.class))
                .andExpect(jsonPath("$.requestor.id", is(request.getRequestor().getId())))
                .andExpect(jsonPath("$.created", is(request.getCreated().toString()), String.class));
    }

    @Test
    void testGetFromUser() throws Exception {
        when(requestService.getRequest(anyInt())).thenReturn(List.of(request));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(request.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestor.id", is(request.getRequestor().getId())))
                .andExpect(jsonPath("$[0].created", is(request.getCreated().toString()), String.class));
    }

    @Test
    void testGetAll() throws Exception {
        when(requestService.getAllRequest()).thenReturn(List.of(request));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(request.getId()), Integer.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription()), String.class))
                .andExpect(jsonPath("$[0].requestor.id", is(request.getRequestor().getId())))
                .andExpect(jsonPath("$[0].created", is(request.getCreated().toString()), String.class));
    }

    @Test
    void testGet() throws Exception {
        when(requestService.getRequestById(anyInt())).thenReturn(request);

        mvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Integer.class))
                .andExpect(jsonPath("$.description", is(request.getDescription()), String.class))
                .andExpect(jsonPath("$.requestor.id", is(request.getRequestor().getId())))
                .andExpect(jsonPath("$.created", is(request.getCreated().toString()), String.class));
    }
}
