package com.sergei.logger.service;

import com.sergei.logger.TimestampRepository;
import com.sergei.logger.domain.Timestamp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;

@Service
public class TimestampLoggerService implements CommandLineRunner {

    @Autowired
    private TimestampRepository timestampRepository;

    @Override
    public void run(String... args) throws Exception {
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
        List<Timestamp> timestamps = Collections.synchronizedList(new ArrayList<>());

//        Runnable saver = () -> {
//
//        }

        Runnable loggerTask = () -> {
            Timestamp timestamp = new Timestamp();
            timestamp.setValue(LocalDateTime.now());

            timestamps.add(timestamp);

            try {
                timestampRepository.saveAll(timestamps);
                System.out.println("Timestamp saved (" + timestamp + ")");
            } catch (Exception e) {
                System.out.println("Error saving timestamp! Retrying...");
            }

            timestamps.remove(timestamp);
        };

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        ScheduledFuture<?> scheduledFuture = executorService.scheduleAtFixedRate(loggerTask, 1, 1, TimeUnit.SECONDS);
    }
}
