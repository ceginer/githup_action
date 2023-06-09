package com.crudPost.Post.Dao;

import com.crudPost.Post.Dto.Board;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcInsertOperations;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class BoardDao {
    // 데이터 넣을 db인 jdbctemplate
    private final NamedParameterJdbcTemplate jdbcTemplate;
    // 데이터 받을 insertBoard
    private final SimpleJdbcInsertOperations insertBoard;

    public BoardDao(DataSource dataSource){
        jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        insertBoard = new SimpleJdbcInsert(dataSource) {
        }
                .withTableName("board")
                .usingGeneratedKeyColumns("board_id");
    }

    @Transactional
    public void addBoard(int userId, String title, String content) {
        Board board = new Board();
        board.setUserId(userId);
        board.setTitle(title);
        board.setContent(content);
        board.setRegdate(LocalDateTime.now());
        SqlParameterSource params = new BeanPropertySqlParameterSource(board);
        insertBoard.execute(params); // jdbctemplate.update 와 똑같음
    }

    @Transactional (readOnly = true) // 조회할 때 이거 써주면 성능 향상
    public int getTotalCount() {
        String sql = "select count(*) as total_count from board";
        // 1개만 돌려줌 -> queryForObject
        // sql 문에 :email 과 같은 ? 타입이 없으므로 굳이 parameter 를 만들어줄 필요가 없음.->Map.of()
        // 딱히 넣을만한 클래스도 존재하지 않음 ->그냥 정수 = Integer.class
        Integer totalCount = jdbcTemplate.queryForObject(sql, Map.of(), Integer.class);
        return totalCount.intValue();
    }

    @Transactional(readOnly = true)
    public List<Board> getBoards(int page) {
        int start = (page - 1) * 10;
        String sql ="select b.title,b.content, b.user_id,b.board_id, b.regdate,b.view_cnt,u.name from board b, user u where b.user_id = u.user_id order by board_id desc limit :start, 10;";
        // 값 여러개 -> query
        // 여기서 join 한 sql 이므로 BeanBeanPropertySqlParameterSource을 통해 하려면
        // Board 클래스에 필드 추가해야함
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        List<Board> list = jdbcTemplate.query(sql, Map.of("start", start), rowMapper);
        return list;

    }

    public Board getBoard(int boardId) {
        String sql = "select b.title,b.content,b.user_id,b.regdate,b.view_cnt,u.name,b.board_id " +
                "from board b, user u " +
                "where b.user_id = u.user_id and b.board_id = :boardId";
        RowMapper<Board> rowMapper = BeanPropertyRowMapper.newInstance(Board.class);
        Board board = jdbcTemplate.queryForObject(sql, Map.of("boardId", boardId), rowMapper);
        return board;
    }

    @Transactional
    public void updateViewCnt(int boardId) {
        String sql = "update board\n" +
                "set view_cnt = view_cnt + 1\n" +
                "where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    @Transactional
    public void deleteBoard(int boardId) {
        String sql = "delete from board where board_id = :boardId";
        jdbcTemplate.update(sql, Map.of("boardId", boardId));
    }

    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        String sql = "update board\n" +
                "set title = :title , content = :content\n" +
                "where board_id = :boardId";
        Board board = new Board();
        board.setBoardId(boardId);
        board.setTitle(title);
        board.setContent(content);
        SqlParameterSource params =  new BeanPropertySqlParameterSource(board);
        jdbcTemplate.update(sql, params);
//        jdbcTemplate.update(sql, Map.of("boardId", boardId, "title", title, "content", content));
    }
}
