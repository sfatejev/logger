package com.sergei.logger.repository;

import com.sergei.logger.domain.Timestamp;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimestampRepository extends MongoRepository<Timestamp, String>{
}
