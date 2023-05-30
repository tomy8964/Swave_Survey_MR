//package com.example.apigatewayservice.repository;
//
//
//
//import com.example.apigatewayservice.domain.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface UserRepository extends JpaRepository<User,Long> {
//    User findByEmail(String email);
//
//    Optional<User> findByUserCode(Long userCode);
//
//    User findByEmailAndProvider(String email, String provider);
//
//
//}
