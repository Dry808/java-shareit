package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserRepository {
    private Map<Integer, User> users = new HashMap<>();
    private int usersId = 0;

    // создать пользователя
    @Override
    public User createUser(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        return user;
    }

    // обновить пользователя
    @Override
    public User updateUser(User user) {
        return users.put(user.getId(), user);
    }

    // получить пользователя по Id
    @Override
    public User getUser(int id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID = " + id + "не найден");
        }
        return users.get(id);
    }

    // удалить пользователя
    @Override
    public User deleteUser(int id) {
        return users.remove(id);
    }

    // получить список всех пользователей
    @Override
    public List<User> getAllUsers() {
        if (users.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(users.values());
    }

    // генерация ID пользователя
    private int generateId() {
        return usersId++;
    }
}
