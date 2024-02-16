package com.project.notice.auth;

import com.project.notice.auth.entity.Login;
import com.project.notice.auth.entity.LoginRepository;
import com.project.notice.profile.Profile;
import com.project.notice.profile.ProfileRepository;
import com.project.notice.auth.util.HashUtil;
import com.project.notice.auth.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Optional;


@Controller
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AuthService service;

    @Autowired
    private HashUtil hashUtil;

    @Autowired
    private JwtUtil jwtUtil;


    @PostMapping("/signup")
    public ResponseEntity createUser(@RequestBody SignupRequest req) {
        long profileNo = service.createIdentity(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileNo);
    }

    @PostMapping("/signin")
    public ResponseEntity signIn(@RequestParam String userId, @RequestParam String userPw,
                                 HttpServletResponse res) {

        Optional<Login> login = loginRepository.findByUserId(userId);
        if (!login.isPresent()) {
            System.out.println("아이디 에러");
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND).body("아이디가 존재하지 않습니다.");

//                    .location(ServletUriComponentsBuilder
//                            .fromHttpUrl("http://localhost:3000/login.html?err=Unauthorized")
//                            .build().toUri())
//                    .build();
        }
        boolean isVerified = hashUtil.verifyHash(userPw, login.get().getUserPw());
        if (!isVerified) {
            System.out.println("패스워드 에러");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("패스워드가 일치하지 않습니다");
//                    .location(ServletUriComponentsBuilder
//                            .fromHttpUrl("http://localhost:3000/login.html?err=Unauthorized")
//                            .build().toUri())
//                    .build();
        }
        Login l = login.get();

        Optional<Profile> profile = profileRepository.findByNo(l.getNo());

        if (!profile.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
//                    .location(ServletUriComponentsBuilder
//                            .fromHttpUrl("http://localhost:3000")
//                            .build().toUri())
                    .build();
        }
        String token = jwtUtil.createToken(
                l.getNo(), l.getUserId(),
                profile.get().getUserName());

        Cookie cookie = new Cookie("token",token);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtUtil.TOKEN_TIMEOUT / 1000L));
        cookie.setDomain("localhost");
        System.out.println(cookie);


        res.addCookie(cookie);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .build();
    }
}
