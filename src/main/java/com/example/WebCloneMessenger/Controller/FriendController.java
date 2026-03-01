package com.example.WebCloneMessenger.Controller;

import com.example.WebCloneMessenger.DTO.FriendDTO;
import com.example.WebCloneMessenger.DTO.FriendDetailDTO;
import com.example.WebCloneMessenger.DTO.UserDTO;
import com.example.WebCloneMessenger.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/friends")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FriendController {

    @Autowired
    private FriendService friendService;

    /**
     * Add a new friend relationship
     * POST /api/friends/add?userID1=1&userID2=2
     */
    @PostMapping("/add")
    public ResponseEntity<FriendDTO> addFriend(
            @RequestParam Integer userID1,
            @RequestParam Integer userID2) {
        try {
            FriendDTO friendDTO = friendService.addFriend(userID1, userID2);
            return ResponseEntity.status(HttpStatus.CREATED).body(friendDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Remove a friend relationship
     * DELETE /api/friends/remove?userID1=1&userID2=2
     */
    @DeleteMapping("/remove")
    public ResponseEntity<Void> removeFriend(
            @RequestParam Integer userID1,
            @RequestParam Integer userID2) {
        try {
            friendService.removeFriend(userID1, userID2);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get all friends of the authenticated user with details
     * GET /api/friends/list
     */
    @GetMapping("/list")
    public ResponseEntity<List<FriendDetailDTO>> getFriends(Authentication authentication) {
        try {
            String username = authentication.getName();
            Integer userId = Integer.parseInt(username);
            List<FriendDetailDTO> friends = friendService.getFriendsWithDetails(userId);
            return ResponseEntity.ok(friends);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get all friends of a user by ID
     * GET /api/friends/list/{userId}
     */
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<FriendDetailDTO>> getFriendsByUserId(@PathVariable Integer userId) {
        try {
            List<FriendDetailDTO> friends = friendService.getFriendsWithDetails(userId);
            return ResponseEntity.ok(friends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Check if two users are friends
     * GET /api/friends/check?userID1=1&userID2=2
     */
    @GetMapping("/check")
    public ResponseEntity<Boolean> areFriends(Authentication authentication,
            @RequestParam Integer userID2) {
        try {
            Integer userID1 = Integer.parseInt(authentication.getName());
            boolean result = friendService.areFriends(userID1, userID2);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get a specific friend relationship
     * GET /api/friends/get?userID1=1&userID2=2
     */
    @GetMapping("/get")
    public ResponseEntity<FriendDTO> getFriendship(
            @RequestParam Integer userID1,
            @RequestParam Integer userID2) {
        try {
            Optional<FriendDTO> friendDTO = friendService.getFriendship(userID1, userID2);
            return friendDTO.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Count friends of a user
     * GET /api/friends/count/1
     */
    @GetMapping("/count/{userId}")
    public ResponseEntity<Long> countFriends(@PathVariable Integer userId) {
        try {
            long count = friendService.countFriends(userId);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * Get mutual friends between two users
     * GET /api/friends/mutual?userID1=1&userID2=2
     */
    @GetMapping("/mutual")
    public ResponseEntity<List<UserDTO>> getMutualFriends(Authentication authentication,
            @RequestParam Integer userID2) {
        try {
            System.out.println("start get mutual friends for user: " + authentication.getName() + " and user: " + userID2);
            int authUserId = Integer.parseInt(authentication.getName());
            System.out.println(authUserId);
            List<com.example.WebCloneMessenger.DTO.UserDTO> mutualFriends = friendService.getMutualFriends(authUserId, userID2);
            return ResponseEntity.ok(mutualFriends);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}

