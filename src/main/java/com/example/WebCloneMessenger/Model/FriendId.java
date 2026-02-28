package com.example.WebCloneMessenger.Model;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FriendId implements Serializable {

    private Integer userID1;
    private Integer userID2;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendId friendId = (FriendId) o;
        return Objects.equals(userID1, friendId.userID1) &&
               Objects.equals(userID2, friendId.userID2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID1, userID2);
    }
}

