package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test2")
@ExtendWith(SpringExtension.class)
public class UserServiceImplTest {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testCreateUserAndGet() {
        // Создаем пользователя
       // UserDto userDto = new UserDto(1, "User Name", "users@example.com");
        User user = new User(5, "Name", "exemple@mail.ru");
        UserDto createdUser = userService.createUser(UserMapper.toUserDto(user));

        // Проверяем, что пользователь создан
        assertEquals(createdUser.getId(), userService.getUser(createdUser.getId()).getId());
        assertEquals("Name", createdUser.getName());
        assertEquals("exemple@mail.ru", createdUser.getEmail());

    }


    @Test
    public void testUpdateUser_Success() {
        // Создаем пользователя
        UserDto userDto = new UserDto(2, "User Name", "user@example.com");
        UserDto createdUser = userService.createUser(userDto);

        // Обновляем пользователя
        UserDto updatedUserDto = new UserDto(1, "New User Name", "newuser@example.com");
        UserDto updatedUser = userService.updateUser(createdUser.getId(), updatedUserDto);

        // Проверяем, что пользователь обновлен
        assertNotNull(updatedUser);
        assertEquals("New User Name", updatedUser.getName());
        assertEquals("newuser@example.com", updatedUser.getEmail());
    }


    @Test
    public void testGetUser_Success() {
        // Создаем пользователя
        UserDto userDto = new UserDto(1, "User Name", "user@example.com");
        UserDto createdUser = userService.createUser(userDto);

        // Получаем пользователя по ID
        UserDto retrievedUser = userService.getUser(createdUser.getId());

        // Проверяем, что пользователь получен
        assertNotNull(retrievedUser);
        assertEquals("User Name", retrievedUser.getName());
        assertEquals("user@example.com", retrievedUser.getEmail());
    }


    @Test
    public void testDeleteUser_Success() {
        // Создаем пользователя
        UserDto userDto = new UserDto(1, "User Name", "user@example.com");
        UserDto createdUser = userService.createUser(userDto);

        // Удаляем пользователя
        userService.deleteUser(createdUser.getId());

        // Проверяем, что пользователь удален
        assertThrows(NotFoundException.class, () -> userService.getUser(createdUser.getId()));
    }

    @Test
    public void testDeleteUser_UserNotFound() {
        // Пытаемся удалить пользователя с несуществующим ID
        userService.deleteUser(999);

        // Проверяем, что нет исключения при удалении несуществующего пользователя
        assertTrue(true);
    }
}
