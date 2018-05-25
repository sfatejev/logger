package com.sergei.logger.service;

import com.sergei.logger.config.ParameterSettings;
import com.sergei.logger.repository.TimestampRepository;
import com.sergei.logger.domain.Timestamp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.*;

@Slf4j
@Service
public class TimestampLoggerService implements CommandLineRunner {

    @Autowired
    private TimestampRepository timestampRepository;

	@Autowired
	private ParameterSettings parameterSettings;

    @Override
    public void run(String... args) {
        if (args.length > 0) {
            if (parameterSettings.getShow().equals(args[0])) {
                printAllTimestamps();
            } else if (parameterSettings.getDelete().equals(args[0])) {
                timestampRepository.deleteAll();
            }
        } else {
            startLogging();
        }
    }

    private void printAllTimestamps() {
        timestampRepository.findAll().stream()
                .map(Timestamp::getValue)
                .forEach(System.out::println);
    }

    private void startLogging() {
        TimestampDbQueue queue = new TimestampDbQueue(timestampRepository);

        Runnable timestampAddingTask = () -> {
            Timestamp timestamp = new Timestamp();
            timestamp.setValue(LocalDateTime.now());

            queue.add(timestamp);

            log.info("Timestamp added to queue {}", timestamp);
        };

		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(timestampAddingTask, 1, 1, TimeUnit.SECONDS);

        queue.startSaving(10, TimeUnit.MILLISECONDS);
    }
}
