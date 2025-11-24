package com.jobify.interview.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailsDTO {
    private String id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String profilePicture;
}