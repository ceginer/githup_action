package com.crudPost.Post.Controller;

import com.crudPost.Post.Dto.Board;
import com.crudPost.Post.Dto.LoginInfo;
import com.crudPost.Post.Dto.User;
import com.crudPost.Post.Service.BoardService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PostController {
    private final BoardService boardService;
    // 게시물 목록을 보여준다.
    // 컨트롤러의 메소드가 리턴하는 문자열은 템플릿 이름이다.
    // http://localhost:8080/-> "list" 0 (forward) 800
    // classpath: /templates/list.html|
    // 뒤에 .html 은 알아서 붙혀준다.
    // 맨 처음 리스트
    @GetMapping("/")
    public String list(@RequestParam(name="page", defaultValue = "1") int page,
                       HttpSession httpSession, Model model){
        // 세션으로 넘겨받은 로그인 정보를 getAttribute 하여 사용
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        log.info("httpSession : "+ httpSession);
        // 템플릿으로 전달..
        model.addAttribute("loginInfo",loginInfo);

        int totalCount = boardService.getTotalCount(); // 11
        List<Board> list = boardService.getBoards(page); // page가 1,2,3,4 ....
        int pageCount = totalCount / 10; // 1
        if(totalCount % 10 > 0){ // 나머지가 있을 경우 1page를 추가
            pageCount++;
        }
        int currentPage = page;
//        System.out.println("totalCount : " + totalCount);
//        for(Board board : list){
//            System.out.println(board);
//        }
        model.addAttribute("list", list);
        model.addAttribute("pageCount", pageCount);
        model.addAttribute("currentPage", currentPage);
        return "list";
    }

    // /board?id=3 // 파라미터 id , 파라미터 id의 값은 3
    // /board?id=2
    // /board?id=1

    // 각 게시물 id 에 따른 GET
    @GetMapping("/board")
    public String board(@RequestParam("boardId") int boardId,
                        Model model,
                        HttpSession httpSession){
        System.out.println("boardId : " + boardId);

        // id에 해당하는 게시물을 읽어온다.
        Board board = boardService.getBoard(boardId);

        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);

        // id에 해당하는 게시물의 조회수도 1증가한다.

        return "board";
    }

    // 삭제한다. 관리자는 모든 글을 삭제할 수 있다.
    // 수정한다.

    // 로그인한 사용자가 글쓰기 GET
    @GetMapping("/writeForm")
    public String writeForm(
            HttpSession httpSession,
            Model model
    ){
        // 로그인한 사용자만 글을 써야한다.
        // 세션에서 로그인한 정보를 읽어들인다. 로그인을 하지 않았다면 리스트보기로 자동 이동 시킨다.
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) {
            return "redirect:/loginform";
        }
        model.addAttribute("loginInfo",loginInfo);

        return "writeForm";
    }

    // 로그인한 사용자가 글쓰기 POST
    @PostMapping("/write")
    public String write(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            HttpSession httpSession
    ){
        System.out.println("title : " + title);
        // 로그인 한 회원 정보 + 제목, 내용을 저장한다.System.out.println("content : " + content);
        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        boardService.addBoard(loginInfo.getUserId(), title, content);

        return "redirect:/"; // 리스트 보기로 리다이렉트한다.
    }

    @GetMapping("/delete")
    public String delete(
            @RequestParam("boardId") int boardId,
            HttpSession session
    ) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }
        // loginInfo.getUserId() 사용자가 쓴 글일 경우에만 삭제한다.
        boardService.deleteBoard(loginInfo.getUserId(), boardId);

        return "redirect:/"; // 리스트 보기로 리다이렉트한다.
    }

    @GetMapping("/updateform")
    public String updateform(@RequestParam("boardId") int boardId,
                             Model model,
                             HttpSession session){
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }
        // boardId에 해당하는 정보를 읽어와서 updateform 템플릿에게 전달한다.
        // false 이므로 조회수는 올라가지 않는다.
        Board board = boardService.getBoard(boardId, false);
        model.addAttribute("board", board);
        model.addAttribute("loginInfo", loginInfo);
        return "updateform";
    }

    @PostMapping("/update")
    public String update(@RequestParam("boardId") int boardId,
                         @RequestParam("title") String title,
                         @RequestParam("content") String content,
                         HttpSession session
    ){

        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return "redirect:/loginform";
        }

        Board board = boardService.getBoard(boardId, false);
        if(board.getUserId() != loginInfo.getUserId()){
            return "redirect:/board?boardId=" + boardId; // 글보기로 이동한다.
        }
        // boardId에 해당하는 글의 제목과 내용을 수정한다.
        boardService.updateBoard(boardId, title, content);
        return "redirect:/board?boardId=" + boardId; // 수정된 글 보기로 리다이렉트한다.
    }




}
