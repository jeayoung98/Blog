package org.example.blog.controller.user;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.blog.domain.user.RefreshToken;
import org.example.blog.domain.user.Role;
import org.example.blog.domain.user.User;
import org.example.blog.domain.user.UserRoleType;
import org.example.blog.dto.UserLoginResponseDto;
import org.example.blog.jwt.jwtUtil.JwtTokenizer;
import org.example.blog.security.dto.UserLoginDto;
import org.example.blog.service.blog.BlogService;
import org.example.blog.service.user.RefreshTokenService;
import org.example.blog.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserApiController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final RefreshTokenService refreshTokenService;
    private final BlogService blogService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid UserLoginDto userLoginDto,
                                BindingResult bindingResult, HttpServletResponse response){

        // username,password 가 null 일때..(정해진 형식에 맞지 않을때..)
        if(bindingResult.hasErrors()){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }

        // 일단은 username 과 password 값을 잘 받아왔다면..
        // 우리 서비스에 가입한 사용자 인지..
        User user = userService.findByUsername(userLoginDto.getUsername());
        // 요청정보에서 얻어온 비밀번호와 우리 서비스가 갖고있는 비밀번호가 일치하는지확인.
        if(!passwordEncoder.matches(userLoginDto.getPassword(),user.getPassword())) {
            //비밀번호가 일치하지않을때!!
            return new ResponseEntity("비밀번호가 올바르지 않습니다.",HttpStatus.UNAUTHORIZED);
        }
        // 여기까지 왔다는것은..  유저도있고, 비밀번호도 맞다.
        // 롤객체를 꺼내서 롤의 이름만 리스트로 얻어온다.
        List<UserRoleType> roles = user.getRoles().stream().map(Role::getRoleType).collect(Collectors.toList());

        // 토큰 발급
        String accessToken = jwtTokenizer.createAccessToken(
                user.getId(), user.getEmail(), user.getName(), user.getUsername(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(
                user.getId(), user.getEmail(), user.getName(), user.getUsername(), roles);

        //리프레시토큰을 디비에 저장.
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setUserId(user.getId());

        refreshTokenService.addRefreshToken(refreshTokenEntity);

        //응답으로 보낼 값들을 준비.
        UserLoginResponseDto loginResponseDto = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .name(user.getName())
                .blogId(blogService.getBlogId(user.getId()))
                .build();

        Cookie accessTokenCookie = new Cookie("accessToken",accessToken);
        accessTokenCookie.setHttpOnly(true);  // 보안 (쿠키값을 자바스크립트같은곳에서는 접근할수 없어요.)
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT/1000)); // 30분 쿠키의 유지시간 단위는 초 ,  JWT의 시간단위는 밀리세컨드

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT/1000)); // 7일

//        Cookie userId = new Cookie("userId",String.valueOf(user.getId()));
//        userId.setPath("/");
//        userId.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT/1000));

        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
//        response.addCookie(userId);

        return new ResponseEntity(loginResponseDto, HttpStatus.OK);
    }
    @GetMapping("/authtest")
    public String authTest(){
        return "authTest";
    }

    @PostMapping("/refreshToken")
    public ResponseEntity refreshToken(HttpServletRequest request, HttpServletResponse response){
        // 할일!!
        // 1. 쿠키로부터 리프레시토큰을 얻어온다.
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if("refreshToken".equals(cookie.getName())){
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }
        // 2-1. 없다.  (오류로  응담.
        if(refreshToken == null){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        // 2-2. 있을때.
        // 3. 토큰으로부터 정보를얻어와요.
        Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
        Long userId = Long.valueOf ((Integer)claims.get("userId"));

        User user = userService.findUserById(userId);


        // 4. accessToken 생성
        List roles = (List)claims.get("roles");


        String accessToken = jwtTokenizer.createAccessToken(userId, user.getEmail(),
                user.getName(), user.getUsername(), roles);

        // 5. 쿠키 생성 response로 보내고.
        Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(Math.toIntExact( JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));

        response.addCookie(accessTokenCookie);

        // 6. 적절한 응답결과(ResponseEntity)를 생성해서 응답.
        UserLoginResponseDto responseDto = UserLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .name(user.getName())
                .userId(user.getId())
                .build();


        return new ResponseEntity(responseDto, HttpStatus.OK);
    }
}
