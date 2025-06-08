package com.project.user_feed_personalization.repository;

import com.project.user_feed_personalization.entity.Tag;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends MongoRepository<Tag, String> {

    Optional<Tag> findByName(String name);
    List<Tag> findByNameIn(List<String> name);
    List<Tag> findAllByName(List<String> tagIds);
    List<Tag> findTop20ByOrderByUsageCountDesc();
}
