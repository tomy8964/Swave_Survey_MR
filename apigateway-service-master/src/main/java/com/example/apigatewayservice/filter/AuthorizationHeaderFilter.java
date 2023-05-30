package com.example.apigatewayservice.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.apigatewayservice.OAuth.JwtProperties;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {
    Environment env;

//    @Autowired
//    UserRepository userRepository;

    public AuthorizationHeaderFilter(Environment env) {
        super(Config.class);
        this.env = env;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            Long userCode = null;
            String nickname = null;
            ServerHttpRequest request = exchange.getRequest();

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "no authorizition header", HttpStatus.UNAUTHORIZED);
            }

            String authorizationHeader = request.getHeaders().get(AUTHORIZATION).get(0);
            System.out.println(authorizationHeader);
            String jwt = authorizationHeader.replace("Bearer ", "");
            System.out.println(jwt);
            userCode = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwt).getClaim("id").asLong();
            nickname = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwt).getClaim("nickname").asString();
            System.out.println(userCode);
            System.out.println(nickname);
            if (!isJwtValid(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
        };
    }


    private boolean isJwtValid(String jwt) {
        Long userCode = null;
        boolean returnValue = true;
        userCode = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwt).getClaim("id").asLong();
        if(userCode!=0?true:false){
            returnValue = true;
        }else {
            returnValue = false;
        }
//        String subject = null;
//        Long userCode = null;
//        try {
//            subject = Jwts.parserBuilder().setSigningKey(env.getProperty("token.secret")).build()
//                    .parseClaimsJws(jwt).getBody()
//                    .getSubject();
//            userCode = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET)).build().verify(jwt).getClaim("id").asLong();
//            System.out.println(userCode);
//        } catch (Exception exception) {
//            returnValue = false;
//        }
//        if (subject == null || subject.isEmpty()) {
//            returnValue = false;
//        }

        return returnValue;
    }

    // Mono, Flux -> Spring WebFlux
    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);

        log.error(err);
        return response.setComplete();
    }

    public static class Config{

    }
}
