package com.sergei.logger.service;

import com.sergei.logger.repository.TimestampRepository;
import com.sergei.logger.domain.Timestamp;
import lombok.extern.slf4j.Slf4j;

import java.util.Queue;
import java.util.concurrent.*;

@Slf4j
public class TimestampDbQueue implements Runnable {

	private static final Integer SAVE_RETRY_INTERVAL = 5;

	private Queue<Timestamp> queue = new LinkedBlockingQueue<>();
	private TimestampRepository timestampRepository;

	//Counters used only for debugging purposes
	private Integer saved = 0;
	private Integer counter = 0;

    public TimestampDbQueue(TimestampRepository timestampRepository) {
        this.timestampRepository = timestampRepository;
    }

	@Override
	public void run() {
		if (queue.size() > 0) {
			try {
				timestampRepository.save(queue.peek());

				log.info("{} saved [total: {}, counter {}]", queue.peek(), counter, ++saved);

				queue.poll();
			} catch (Exception e) {
				log.error("Error saving timestamp, retrying in {} seconds...", SAVE_RETRY_INTERVAL, e);

				try {
					TimeUnit.SECONDS.sleep(SAVE_RETRY_INTERVAL);
				} catch (InterruptedException ie) {
					log.error("", e);
				}
			}
		}
	}

    public void add(Timestamp item) {
        queue.offer(item);
        counter++;
    }

	public void startSaving(Integer interval, TimeUnit timeUnit) {
    	Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(this, 1, interval, timeUnit);
	}
}
