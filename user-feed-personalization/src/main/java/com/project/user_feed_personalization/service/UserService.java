package com.project.user_feed_personalization.service;

import com.project.user_feed_personalization.entity.Tag;
import com.project.user_feed_personalization.entity.User;
import com.project.user_feed_personalization.repository.ArticleRepository;
import com.project.user_feed_personalization.repository.TagRepository;
import com.project.user_feed_personalization.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private TagRepository tagRepository;

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email);
    }

    public void updateUserTagsPreferences(String email, List<String> tagIds){
        User user = findByEmail(email);
        if(user != null ){
            List<Tag> tags = tagRepository.findAllByName(tagIds);
            user.setInterestedTags(tags);
            userRepository.save(user);
        }
    }


}
