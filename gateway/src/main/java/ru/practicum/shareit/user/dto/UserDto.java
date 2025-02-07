package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO user
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private int id;
    @NotNull
    private String name;

    @NotNull
    @Email
    private String email;
}
