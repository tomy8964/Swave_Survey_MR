package com.example.surveydocument.user.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.surveydocument.survey.domain.Survey;
import com.example.surveydocument.survey.domain.SurveyDocument;
import com.example.surveydocument.survey.exception.InvalidSurveyException;
import com.example.surveydocument.survey.exception.InvalidTokenException;
import com.example.surveydocument.survey.response.SurveyMyPageDto;
import com.example.surveydocument.user.domain.User;
import com.example.surveydocument.user.exception.UserNotFoundException;
import com.example.surveydocument.user.repository.UserRepository;
import com.example.surveydocument.user.request.UserUpdateRequest;
import com.example.surveydocument.util.OAuth.JwtProperties;
import com.example.surveydocument.util.OAuth.OauthToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserService2 {
    @Autowired
    OAuthService oAuthService;
    @Autowired
    UserRepository userRepository;

    public List<SurveyMyPageDto> mySurveyList(HttpServletRequest request) throws InvalidTokenException {
        checkInvalidToken(request);
        List<SurveyMyPageDto> surveyMyPageDtos = new ArrayList<>();

        Survey survey= getUser(request).getSurvey();
        System.out.println(survey);
        for(SurveyDocument surveyDocument:survey.getSurveyDocumentList()){
            SurveyMyPageDto surveyMyPageDto = new SurveyMyPageDto();
            surveyMyPageDto.setId(surveyDocument.getId());
            surveyMyPageDto.setDescription(surveyDocument.getDescription());
            surveyMyPageDto.setTitle(surveyDocument.getTitle());
            surveyMyPageDto.setDeadline(surveyDocument.getDeadline());
            surveyMyPageDto.setStartDate(surveyDocument.getStartDate());
            surveyMyPageDtos.add(surveyMyPageDto);

        }
        return surveyMyPageDtos;
    }

    public User getUser(HttpServletRequest request) { //(1)

        String jwtHeader = ((HttpServletRequest)request).getHeader(JwtProperties.HEADER_STRING);
        String token = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, "");
        Long userCode = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(token).getClaim("id").asLong();
//        System.out.println(userCode);

//        userCode = (Long) request.getAttribute("userCode");
        log.info(String.valueOf(userCode));
        User user = userRepository.findByUserCode(userCode).orElseThrow(UserNotFoundException::new);
        return user;
    }

    public ResponseEntity<Object> getCurrentUser(HttpServletRequest request) throws Exception {
        checkInvalidToken(request);
        User user = getUser(request);
        System.out.println(user.getEmail());
        return ResponseEntity.ok().body(user);
    }

    public ResponseEntity getLogin(String code,String provider){
        OauthToken oauthToken = oAuthService.getAccessToken(code, provider);
        String jwtToken = oAuthService.SaveUserAndGetToken(oauthToken.getAccess_token(), provider);
        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX + jwtToken);
        return ResponseEntity.ok().headers(headers).body("\"success\"");
    }
    public String updateMyPage(HttpServletRequest request, UserUpdateRequest userUpdateRequest) throws InvalidTokenException {
        checkInvalidToken(request);
        Long userId =getUser(request).getUserCode();
        Optional<User> optionalUser = userRepository.findByUserCode(userId);
        if(optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setNickname(userUpdateRequest.getNickname());
            user.setDescription(userUpdateRequest.getDescription());
            userRepository.save(user);
        }else {
            throw new InvalidSurveyException();
        }
        return "success";
    }

    private static void checkInvalidToken(HttpServletRequest request) throws InvalidTokenException {
        if(request.getHeader("Authorization") == null) {
            log.info("error");
            throw new InvalidTokenException();
        }
        log.info("토큰 체크 완료");
    }


}
