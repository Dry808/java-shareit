package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping(path = "/users")
public class UserController {
    UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @PostMapping
    public UserDto createUser(@Valid @RequestBody User user) {
        return userService.createUser(UserMapper.toUserDto(user));
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable int id) {
        return userService.getUser(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable int id, @RequestBody UserDto user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable int id) {
        return userService.deleteUser(id);
    }
}
