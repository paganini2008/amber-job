package com.github.doodler.amber.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.slf4j.Marker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import com.github.doodler.common.context.ManagedBeanLifeCycle;
import lombok.extern.slf4j.Slf4j;

/**
 * @Description: JobLogCleanTask
 * @Author: Fred Feng
 * @Date: 31/10/2023
 * @Version 1.0.0
 */
@Slf4j
@Profile({"dev", "test", "prod"})
@Component
public class JobLogCleanTask extends TimerTask implements ManagedBeanLifeCycle {

	private static final int DEFAULT_RETAIN_DAYS = 14;
	private static final String SQL_CLEAN_JOB_LOG = "delete from qrtz_job_run_log where start_time<:startTime";

	@Autowired
	private DataSource dataSource;

	@Autowired
	private Marker marker;

	private NamedParameterJdbcTemplate jdbcTemplate;
	private Timer timer;

	@Override
	public void afterPropertiesSet() throws Exception {
		jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);

		timer = new Timer();

		LocalDateTime startTime = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(1, 0, 0));
		long initialDelay = ChronoUnit.MILLIS.between(LocalDateTime.now(), startTime);
		long interval = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS);
		timer.schedule(this, initialDelay, interval);
		if(initialDelay > 0) {
			if(log.isInfoEnabled()) {
				log.info(marker, "JobLogCleanTask will start after {} ", DurationFormatUtils.formatDuration(initialDelay, "H'H'm'm's's'"));
			}
		}
	}

	@Override
	public void run() {
		try {
			LocalDateTime startTime = LocalDateTime.of(LocalDate.now().minusDays(DEFAULT_RETAIN_DAYS),
					LocalTime.of(0, 0, 0));
			int deletedRows = jdbcTemplate.update(SQL_CLEAN_JOB_LOG, Collections.singletonMap("startTime", startTime));
			if (log.isInfoEnabled()) {
				log.info(marker, "Clean job log {} items", deletedRows);
			}
		} catch (Exception e) {
			if (log.isErrorEnabled()) {
				log.error(e.getMessage(), e);
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		if (timer != null) {
			timer.cancel();
		}
	}
}