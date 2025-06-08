package com.project.user_feed_personalization.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegisterRequest {
    private String email;
    private String lastName;
    private String firstName;
    private String password;
}
