package com.crudPost.Post.Dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

// DTO, Data Transport Object 로써, 그냥 UserDao 테이블 의미
@Getter
@Setter
@NoArgsConstructor
public class LoginUserDto {
    private String email;
    private String password;
}
/*
'user_id', 'int', 'NO', 'PRI', NULL, 'auto_increment'
'email', 'varchar(255)', 'NO', '', NULL, ''
'name', 'varchar(50)', 'NO', '', NULL, ''
'password', 'varchar(500)', 'NO', '', NULL, ''
'regdate', 'timestamp', 'YES', '', 'CURRENT_TIMESTAMP', 'DEFAULT_GENERATED'
 */
