package com.doodler.quartzadmin;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import com.github.doodler.common.quartz.scheduler.JdbcJobLogService;
import com.github.doodler.common.quartz.scheduler.JobLogService;

/**
 * 
 * @Description: QuartzAdminConfig
 * @Author: Fred Feng
 * @Date: 01/01/2025
 * @Version 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class QuartzAdminConfig {

    @Primary
    @Bean
    public JobLogService jdbcJobLogService(DataSource dataSource) {
        return new JdbcJobLogService(dataSource);
    }

    @Bean
    public PlatformTransactionManager dataSourceTransactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
