package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Создание пользователя
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    // Обновление пользователя
    @Override
    public UserDto updateUser(int id, UserDto userDto) {
        User oldUser = userRepository.findById(id).orElseThrow(()
                -> new NotFoundException("Пользователь не найден"));;

        if (userDto.getEmail() != null) {
            oldUser.setEmail(userDto.getEmail());
        }

        if (userDto.getName() != null) {
            oldUser.setName(userDto.getName());
        }

        userRepository.save(oldUser);
        return UserMapper.toUserDto(oldUser);
    }

    // Получить пользователя по ID
    @Override
    public UserDto getUser(int id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        return UserMapper.toUserDto(user);
    }

    // Удалить пользователя
    @Override
    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    // Метод для проверки уникальности email
    @Deprecated
    private void validateEmail(String email) {
        boolean emailExists = userRepository.findAll().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));

        if (emailExists) {
            throw new IllegalArgumentException("Пользователь с данным email уже существует.");
        }
    }
}
