package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(int userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getRequest(int userId);

    List<ItemRequestDto> getAllRequest();

    ItemRequestDto getRequestById(int requestId);
}
