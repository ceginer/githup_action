package com.crudPost.Post.Service;

import com.crudPost.Post.Dao.BoardDao;
import com.crudPost.Post.Dto.Board;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardDao boardDao;


    @Transactional
    public void addBoard(int userId, String title, String content) {
        boardDao.addBoard(userId, title, content);
    }

    @Transactional
    public int getTotalCount(){
        return boardDao.getTotalCount();
    }

    @Transactional
    public List<Board> getBoards(int page){
        return boardDao.getBoards(page);
    }

    @Transactional // 한개의 board 상세페이지?
    public Board getBoard(int boardId) {
        // id 에 해당하는 board 객체 가져오기
        Board board = boardDao.getBoard(boardId);
        // 조회수 1 늘려주기
        boardDao.updateViewCnt(boardId);

        return board;
    }

    @Transactional // 한개의 board 상세페이지?
    public Board getBoard(int boardId, boolean updateViewCnt) {
        // id 에 해당하는 board 객체 가져오기
        Board board = boardDao.getBoard(boardId);
        if (updateViewCnt){
            // 조회수 1 늘려주기
            boardDao.updateViewCnt(boardId);
        }
        return board;
    }

    @Transactional
    public void deleteBoard(int userId, int boardId){
        Board board = boardDao.getBoard(boardId);
        if (board.getUserId() == userId){
            boardDao.deleteBoard(boardId);
        }
    }

    @Transactional
    public void updateBoard(int boardId, String title, String content) {
        boardDao.updateBoard(boardId, title, content);
    }
}
