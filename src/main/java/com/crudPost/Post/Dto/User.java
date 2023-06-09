package com.crudPost.Post.Dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.time.LocalDateTime;

// DTO, Data Transport Object 로써, 그냥 UserDao 테이블 의미
@Getter
@Setter
// 밑의 sql문과 같이 table 을 만드는 작업이고,

// @NoArgsConstructor 사용하면
// AccessLevel.PROTECTED 속성을 부여해주어 무분별한 기본 생성자의 생성을 방지하고
// 지정한 생성자를 사용하도록 강제하여 무조건 완전한 상태의 객체를 생성할 수 있도록 도움을 준다
// --->예로, 만약 다른 건 모두 setter로 의존성 주입을 지정했지만 한가지 name만 지정하지 않았다면,
// ----@NoArgsConstructor 가 ide 차원에서 오류가 있음을 나타내준다.
@NoArgsConstructor
public class User {
    private int userId;
    private String name;
    private String email;
    private String password;
    private LocalDateTime regDate; // 원래는 날짜 type으로 읽어온 후 문자열로 변환
}
/*
'user_id', 'int', 'NO', 'PRI', NULL, 'auto_increment'
'email', 'varchar(255)', 'NO', '', NULL, ''
'name', 'varchar(50)', 'NO', '', NULL, ''
'password', 'varchar(500)', 'NO', '', NULL, ''
'regdate', 'timestamp', 'YES', '', 'CURRENT_TIMESTAMP', 'DEFAULT_GENERATED'
 */
