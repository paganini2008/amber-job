package com.github.doodler.amber;

import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import com.github.doodler.common.quartz.EnableQuartz;
import com.github.doodler.common.quartz.EnableQuartz.Mode;

/**
 * 
 * @Description: AmberJobApplicationMain
 * @Author: Fred Feng
 * @Date: 01/01/2025
 * @Version 1.0.0
 */
@EnableQuartz(Mode.SCHEDULER)
@EnableDiscoveryClient
@SpringBootApplication
public class AmberJobApplicationMain {

    static {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
    }

    public static void main(String[] args) {
        SpringApplication sa = new SpringApplication(AmberJobApplicationMain.class);
        sa.addListeners(new ApplicationPidFileWriter());
        sa.run(args);
    }
}
