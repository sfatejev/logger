package com.sergei.logger.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "timestamp")
public class Timestamp {

    @Id
    @Getter
    public String id;

    @Getter
    @Setter
    public LocalDateTime value;
}
