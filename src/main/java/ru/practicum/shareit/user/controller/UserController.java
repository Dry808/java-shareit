package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

/**
 * Контроллер user
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        log.info("Создаём пользователя");
        return userService.createUser(UserMapper.toUserDto(user));
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable int id) {
        log.info("Получение пользователя с ID=" + id);
        return userService.getUser(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable int id, @RequestBody UserDto user) {
        log.info("Обновление данных пользователя с ID=" + id);
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable int id) {
        log.info("Удаление пользователя с ID=" + id);
        return userService.deleteUser(id);
    }
}
