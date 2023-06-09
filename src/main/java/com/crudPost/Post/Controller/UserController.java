package com.crudPost.Post.Controller;

import com.crudPost.Post.Dto.LoginInfo;
import com.crudPost.Post.Dto.User;
import com.crudPost.Post.Service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
// 이걸 통해서 @Autowired 처럼 의존성 주입해준거임! - notion 스프링 MVC 참조
@RequiredArgsConstructor
public class UserController {
    // userService 와의 관계 형성! ->  userService.addUser 사용
    private final UserService userService;

    // 회원가입 폼
    @GetMapping("/userRegForm")
    public String userRegForm(){
        return "userRegForm";
    }
    // 회원가입 POST
    @PostMapping("/userReg")
    public String userReg(
            // post 방식의 파람들 받아오기
            @RequestParam("name") String name,
            @RequestParam("email")String email,
            @RequestParam("password")String password){

        userService.addUser(name, email, password);

        return "redirect:/welcome";

        // 받은 파라미터 회원 정보 저장하기
    }
    @GetMapping("/welcome")
    public String welcome(){
        return "welcome";
    }
    // 로그인폼 GET
    @GetMapping("/loginform")
    public String loginform(){
        return "loginform";
    }
    // 로그인폼 POST
    @PostMapping("/login")
    public String login(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession httpSession // spring이 자동으로 session을 처리하는 HttpSession객체를 넣어준다.
    ){
        // email에 해당하는 회원 정보를 읽어온 후
        // 아이디 암호가 맞다면 세션에 회원정보를 저장한다.
        System.out.println("email : " + email);
        System.out.println("password : " + password);

        try{
            User user = userService.getUser(email);
            if(user.getPassword().equals(password)){
                System.out.println("암호가 같습니다.");
                LoginInfo loginInfo = new LoginInfo(user.getUserId(), user.getEmail(), user.getName());
                httpSession.setAttribute("loginInfo", loginInfo); // 첫번째 파라미터가 key, 두번째 파라미터가 값.
                System.out.println("세션에 로그인 정보가 저장된다.");
            }else{
                throw new RuntimeException("암호가 일치하지 않음.");
            }
        }catch(Exception ex){
            return "redirect:/loginform?error=true"; // 이메일이 존재하지 않을 때
        }
        return "redirect:/";
    }

    // 로그아웃
    @GetMapping("/logout")
    public String logout(HttpSession httpSession){
        // 세션에서 회원정보를 삭제한다.
        httpSession.removeAttribute("loginInfo");
        return "redirect:/";
    }

}
