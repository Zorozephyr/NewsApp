package com.project.user_feed_personalization.repository;

import com.project.user_feed_personalization.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {

    User findByEmail(String email);
}
