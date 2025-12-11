package com.example.WebCloneMessenger.DTO;

import com.example.WebCloneMessenger.Model.User;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder(toBuilder = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class AuthResponse  {
    String token;
    UserDTO user;
}
