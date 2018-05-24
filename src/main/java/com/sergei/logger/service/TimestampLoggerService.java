package com.sergei.logger.service;

import com.sergei.logger.TimestampRepository;
import com.sergei.logger.domain.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.*;

@Service
public class TimestampLoggerService implements CommandLineRunner {

    @Autowired
    private TimestampRepository timestampRepository;

    @Override
    public void run(String... args) {
//        args[0] = "-p";

        if (args.length > 0) {
            if ("-p".equals(args[0])) {
                printAllTimestamps();
            } else if ("-d".equals(args[0])) {
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

        Runnable loggerTask = () -> {
            Timestamp timestamp = new Timestamp();
            timestamp.setValue(LocalDateTime.now());

            queue.add(timestamp);

            System.out.println("Timestamp added to queue (" + timestamp + ")");
        };

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(loggerTask, 1, 1, TimeUnit.SECONDS);

        queue.startSaving(10, TimeUnit.MILLISECONDS);
    }
}
