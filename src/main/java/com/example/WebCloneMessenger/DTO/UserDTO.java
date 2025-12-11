package com.example.WebCloneMessenger.DTO;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // Bao gồm @Getter, @Setter, @ToString, @EqualsAndHashCode
@Builder
@NoArgsConstructor  // QUAN TRỌNG cho Jackson deserialize
@AllArgsConstructor // QUAN TRỌNG cho Builder
public class UserDTO {

    private Integer id;

    @NotNull
    @Size(max = 100)
    private String name;

    @NotNull
    @Size(max = 255)
    private String email;

    @NotNull
    @Size(max = 255)
    @JsonIgnore  // KHÔNG trả về password trong response
    private String password;

    @JsonProperty("isOnline")
    private Boolean isOnline;

    @Size(max = 500)
    private String avatarUrl;

    // Thêm role nếu có
    private String role;
}