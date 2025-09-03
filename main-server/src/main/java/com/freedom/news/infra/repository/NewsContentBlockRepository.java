package com.freedom.news.infra.repository;

import com.freedom.news.domain.entity.NewsContentBlock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NewsContentBlockRepository extends JpaRepository<NewsContentBlock, Long> {

}
