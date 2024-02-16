package com.project.notice.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupRequest {
    private Long id;
    private String userId;
    private String userPw;
    private String userName;
}
