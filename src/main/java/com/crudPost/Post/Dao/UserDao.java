package com.crudPost.Post.Dao;

import com.crudPost.Post.Dto.User;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;

// DAO, Data Access Object 로써, 사실상 Repository 의미
@Repository
// 이 아이는 @RequiredArgsConstructor 안씀
public class UserDao {
    // 그냥 jdbcTemplate 의 경우는 sql 에서 비어있는 곳을 ?로 하는 반면,
    // NamedParameterJdbcTemplate 는 sql 에서 ?대신 :변수명 을 이용하여 처리함으로써 순서에 강제를 받지 않는다.


    // SimpleJdbcInsertOperations 는 내 DTO 필드에 어떻게 매핑할지를 설계하는 거 (인터페이스임)
    private SimpleJdbcInsertOperations insertUser;
   // jdbcTemplate 는 db에 저장하기 위해 필요한 거
    private final NamedParameterJdbcTemplate jdbcTemplate;
    /////////////////////////////


    // DAO 가 db와 직접적으로 연동하므로 DAO 에서 datasource 사용
    public UserDao(DataSource dataSource){
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        /////////////////////////////////////////
        // SimpleJdbcInsertOperations(인터페이스) 를 생성될 때 부터 어느 테이블에 넣을까? 를 미리 설계
        insertUser = new SimpleJdbcInsert(dataSource)
                .withTableName("user")
                .usingGeneratedKeyColumns("user_id");
                // -> 자동으로 증가되는 id 설정(보통 id는 이걸 사용하는 듯)
                //   mysql에서의 autoincrement 설정인데,
                //   왠만하면 id는 mysql에서도 autoincrement 사용한다고 함.

    }
    //Spring JDBC를 이용한 코드.
    @Transactional
    public User addUser(String email, String name, String password){
        // Service에서 이미 트랜잭션이 시작했기 때문에, 그 트랜잭션에 포함된다.
        // insert into user (email, name, password, regdate) values (?, ?, ?, now()); # user_id auto gen
        // ->>> // insert into user (email, name, password, regdate) values (:email, :name, :password, :regdate); # user_id auto gen
        // insert into user 까지의 과정을 생성자에서,
        // 이후에 (email, name, password, regdate) 과정이 addUser 메서드에서 실행되는 것. 그리고
        // (:email, :name, :password, :regdate) 부분이 sqlparam 으로 받아들여지는 것임.
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setRegDate(LocalDateTime.now());  // regDate
        SqlParameterSource param = new BeanPropertySqlParameterSource(user);
        // execute 는 전달된 값을 받아 그냥 실행
        // executeAndReturnKey 전달된 값을 삽입하고 생성된 키를 반환
        Number number = insertUser.executeAndReturnKey(param);
        int userId = number.intValue();
        user.setUserId(userId);
        return user;
    }

//    public int getLastInsertId(){
//        return 0; // 임시
//    }

    // 접근 권한과 관련된 메서드
    // 위에 썻던 SimpleJdbcInsert 써도 되지만,
    // 이번에는 직접 sql 작성해서 해볼 것임 -> NamedParameterJdbcTemplate jdbcTemplate 사용
    @Transactional
    public void mappingUserRole(int userId){
        // Service에서 이미 트랜잭션이 시작했기 때문에, 그 트랜잭션에 포함된다.
        // insert into user_role( user_id, role_id ) values ( ?, 1);
        String sql = "insert into user_role( user_id, role_id ) values (:userId, 1)";
        SqlParameterSource params = new MapSqlParameterSource("userId", userId);

        // 위에서 values (:userId, 1) <- 여기서 ? 를 사용하지 않은 이유가
        // 이 jdbcTemplate는 그냥 JdbcTemplate 가 아닌 NamedParameterJdbcTemplate 를 사용했기 때문
        jdbcTemplate.update(sql, params);
    }

    @Transactional
    public User getUser(String email) {
        // db에 접근해서 있는 이메일인지, 있는 이메일이면 암호와 맞는지 판단할 것.-> try catch 사용
        try {
            String sql = "select user_id, email,name,password,regdate from user where email = :email";
            // 위의 sql문의 :email 에 파라미터 email 을 매핑한다는 뜻
            // 파라미터 하나이므로 MapSqlParameterSource 사용
            SqlParameterSource param = new MapSqlParameterSource("email",email);
            // User 클래스의 필드와 모두 일치하므로 BeanProperty 를 사용하면
            // user_id => setUserId , email => setEmail ... 처럼 각각의 칼럼에 필드를 매핑하게 됨.
            // rowMapper -> 각각의 칼럼들을 어떤 DTO에 담아줄거냐를 의미함.
            RowMapper<User> rowMapper = BeanPropertyRowMapper.newInstance(User.class);
            // email이 일치하지 않을 수도 있으므로, 1개 또는 0개의 칼럼 -> queryForObject 사용
            // sql 문을 rowMapper 를 이용해서 datasource 를 받은 jdbcTemplate 한테 넣으면 db에서 가져와서
            // user_id, email ,name .. 들을 User에 매핑한 채로 반환하게 됨.
            User user = jdbcTemplate.queryForObject(sql,param, rowMapper);
            // -> 위에서 만약 email 이 없다면 에러 반환하므로 controller 에서 에러처리 또한 필수!
            return user;

        }catch (Exception ex){
            return null;
        }


    }
}
