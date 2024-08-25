package ru.practicum.shareit.user.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository inMemoryStorage;

    @Autowired
    public UserServiceImpl(UserRepository inMemoryStorage) {
        this.inMemoryStorage = inMemoryStorage;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmail(userDto.getEmail());
        User user = inMemoryStorage.createUser(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        User oldUser = inMemoryStorage.getUser(id);

        if (userDto.getEmail() != null) {
            validateEmail(userDto.getEmail());
            oldUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }

        inMemoryStorage.updateUser(oldUser);
        return UserMapper.toUserDto(oldUser);
    }

    @Override
    public UserDto getUser(int id) {
        User user = inMemoryStorage.getUser(id);
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto deleteUser(int id) {
        User user = inMemoryStorage.deleteUser(id);
        return UserMapper.toUserDto(user);
    }

    private void validateEmail(String email) {
        boolean emailExists = inMemoryStorage.getAllUsers().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));

        if (emailExists) {
            throw new IllegalArgumentException("Пользователь с данным email уже существует.");
        }
    }
}
