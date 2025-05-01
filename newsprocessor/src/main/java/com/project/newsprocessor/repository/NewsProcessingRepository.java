package com.project.newsprocessor.repository;

import com.project.newsprocessor.entity.NewsArticle;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsProcessingRepository extends MongoRepository<NewsArticle,String> {
}
