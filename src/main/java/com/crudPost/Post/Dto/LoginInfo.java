package com.crudPost.Post.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginInfo {
    private int userId;
    private String email;
    private String name;
}
