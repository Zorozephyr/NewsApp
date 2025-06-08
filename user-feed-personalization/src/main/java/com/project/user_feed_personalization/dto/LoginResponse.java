package com.project.user_feed_personalization.dto;

import com.project.user_feed_personalization.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private User user;
    private String message;
}
