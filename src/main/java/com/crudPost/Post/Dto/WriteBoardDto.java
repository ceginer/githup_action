package com.crudPost.Post.Dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class WriteBoardDto {
    private String title;
    private String content;
}

