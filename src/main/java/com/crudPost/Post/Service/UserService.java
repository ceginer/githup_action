package com.crudPost.Post.Service;

import com.crudPost.Post.Dao.UserDao;
import com.crudPost.Post.Dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// final 필드는 초기화가 필수이다.
// 그러기 위해 우리는 롬복의 @RequiredArgsConstructor 를 사용한다.
// -> final 필드의 생성자를 자동 생성해준다.
// -> 클래스 위에 붙여줘야 한다.
@RequiredArgsConstructor
public class UserService {
    private final UserDao userDao;

    // 보통 서비스에서는 @Transactional 을 붙여서 하나의 트랜잭션으로 처리하게 한다.
    // Spring Boot는 트랜잭션을 처리해주는 트랜잭션 관리자를 가지고 있다.
    @Transactional
    public User addUser(String name, String email, String password){
        // 이메일 중복 검사 후, 중복있으면 반환가능, 없으면 null 반환
        // 트랜잭션이 시작된다.
        User user1 = userDao.getUser(email);
        if (user1 != null){
            throw new RuntimeException("이미 가입된 이메일입니다");
        }
        User user = userDao.addUser(email, name, password);
        userDao.mappingUserRole(user.getUserId()); // 권한을 부여한다.
        return user;
        // 트랜잭션이 끝난다.
    }

    @Transactional
    public User getUser(String email){
        return userDao.getUser(email);
    }



}
