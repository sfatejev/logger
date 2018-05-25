package com.sergei.logger.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.param")
@Getter
@Setter
public class ParameterSettings {

	private String show;

	private String delete;
}
