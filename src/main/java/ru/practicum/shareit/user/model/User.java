package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
public class User {
    private int id;

    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;
}
