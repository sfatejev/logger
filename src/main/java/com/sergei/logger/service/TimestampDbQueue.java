package com.sergei.logger.service;

import com.sergei.logger.TimestampRepository;
import com.sergei.logger.domain.Timestamp;

import java.util.Queue;
import java.util.Random;
import java.util.concurrent.*;

public class TimestampDbQueue  {

    private TimestampRepository timestampRepository;

    private Integer counter = 0;
    private Integer saved = 0;

    private Queue<Timestamp> queue = new LinkedBlockingQueue<>();

    public TimestampDbQueue(TimestampRepository timestampRepository) {
        this.timestampRepository = timestampRepository;
    }

    public void add(Timestamp item) {
        queue.add(item);
        counter++;
    }

    public void startSaving(Integer interval, TimeUnit timeUnit) {
        Random random = new Random();

        Runnable savingTask = () -> {
            if (queue.size() > 0) {
                try {
                    timestampRepository.save(queue.peek());

                    Integer rnd = random.nextInt(100);
                    if (rnd > 90) {
                        throw new Exception();
                    }

                    System.out.println("Timestamp saved (" + queue.peek() + ")" + "[total: " + counter + ", saved: " + ++saved + "]");

                    queue.poll();
                } catch (Exception e) {
                    System.out.println("Error saving timestamp, retrying in 5sec...");

                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(savingTask, 1, interval, timeUnit);
    }

}
