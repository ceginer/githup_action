package com.crudPost.Post.Controller;


import com.crudPost.Post.Dto.*;
import com.crudPost.Post.Service.BoardService;
import com.crudPost.Post.Service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiController {

    private final BoardService boardService;
    private final UserService userService;


    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody SignupUserDto signupUserDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            log.error("binding result 에러 : " + bindingResult.getAllErrors());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        userService.addUser(signupUserDto.getName(),signupUserDto.getEmail(),signupUserDto.getPassword());
        log.info("User : " + signupUserDto.toString());

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginUserDto loginUserDto, BindingResult bindingResult, HttpSession httpSession){
        if(bindingResult.hasErrors()){
            log.error("binding result 에러 : "+ bindingResult.getAllErrors());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        try{
            User user = userService.getUser(loginUserDto.getEmail());
            log.info("User : " + user.toString());

            if(user.getPassword().equals(loginUserDto.getPassword())){
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getEmail(), user.getName());
                httpSession.setAttribute("loginInfo", loginInfo);
                return new ResponseEntity("Login Success", HttpStatus.OK);
            } else{
                return new ResponseEntity("password isn't match",HttpStatus.UNAUTHORIZED);
            }
        } catch(Exception e){
            return new ResponseEntity("email is already exists",HttpStatus.BAD_REQUEST);
        }

    }

    @GetMapping("/board")
    public ResponseEntity board(@RequestParam("boardId") int boardId,
                               HttpServletRequest request, HttpServletResponse response){

        Board board = boardService.getBoard(boardId);
        return new ResponseEntity(board, HttpStatus.OK);
    }

    @PostMapping("/write")
    public ResponseEntity write(@RequestBody WriteBoardDto writeBoardDto,BindingResult bindingResult,HttpSession httpSession){
        if(bindingResult.hasErrors()){
            log.error("binding result 에러 :"+ bindingResult.getAllErrors());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if(loginInfo == null){
            return new ResponseEntity("로그인하지 않으셨습니다." ,HttpStatus.UNAUTHORIZED);
        }

        boardService.addBoard(loginInfo.getUserId(), writeBoardDto.getTitle(), writeBoardDto.getContent());

        return new ResponseEntity(writeBoardDto, HttpStatus.OK);
    }

    @PostMapping("/update/{boardId}")
    public ResponseEntity updateform(@PathVariable("boardId") int boardId,
                                     @RequestBody WriteBoardDto writeBoardDto,
                             BindingResult bindingResult, HttpSession httpSession){
        if(bindingResult.hasErrors()){
            log.error("binding result 에러 :"+ bindingResult.getAllErrors());
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        LoginInfo loginInfo = (LoginInfo) httpSession.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return new ResponseEntity("로그인하지 않으셨습니다." ,HttpStatus.UNAUTHORIZED);
        }

        // boardId에 해당하는 정보를 읽어와서 updateform 템플릿에게 전달한다.
        // false 이므로 조회수는 올라가지 않는다.
        Board board = boardService.getBoard(boardId, false);
        board.setTitle(writeBoardDto.getTitle());
        board.setContent(writeBoardDto.getContent());

        boardService.updateBoard(boardId, writeBoardDto.getTitle(), writeBoardDto.getContent());

        return new ResponseEntity(board, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{boardId}")
    public ResponseEntity delete(
            @PathVariable("boardId") int boardId,
            HttpSession session
    ) {
        LoginInfo loginInfo = (LoginInfo) session.getAttribute("loginInfo");
        if (loginInfo == null) { // 세션에 로그인 정보가 없으면 /loginform으로 redirect
            return new ResponseEntity("로그인하지 않으셨습니다." ,HttpStatus.UNAUTHORIZED);
        }
        // loginInfo.getUserId() 사용자가 쓴 글일 경우에만 삭제한다.
        boardService.deleteBoard(loginInfo.getUserId(), boardId);

        return new ResponseEntity("Delete Success",HttpStatus.OK); // 리스트 보기로 리다이렉트한다.
    }

    @GetMapping("/logout")
    public ResponseEntity logout(HttpSession httpSession){
        // 세션에서 회원정보를 삭제한다.
        httpSession.removeAttribute("loginInfo");
        return new ResponseEntity("Logout Success",HttpStatus.OK);
    }
}
