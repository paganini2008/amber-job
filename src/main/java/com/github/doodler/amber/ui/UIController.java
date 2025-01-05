package com.github.doodler.amber.ui;

import static com.github.doodler.common.quartz.JobConstants.DEFAULT_JOB_GROUP_NAME;
import static com.github.doodler.common.quartz.JobConstants.DEFAULT_JOB_TRIGGER_GROUP_NAME;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.github.doodler.amber.utils.Tip;
import com.github.doodler.amber.utils.TipContext;
import com.github.doodler.common.quartz.executor.JobDefination;
import com.github.doodler.common.quartz.executor.TriggerDefination;
import com.github.doodler.common.quartz.scheduler.JobGroupStatusVo;
import com.github.doodler.common.quartz.scheduler.JobLog;
import com.github.doodler.common.quartz.scheduler.JobLogQuery;
import com.github.doodler.common.quartz.scheduler.JobLogService;
import com.github.doodler.common.quartz.scheduler.JobManager;
import com.github.doodler.common.quartz.scheduler.JobStatusVo;
import com.github.doodler.common.quartz.scheduler.PageVo;
import com.github.doodler.common.quartz.scheduler.TriggerGroupStatusVo;
import com.github.doodler.common.quartz.scheduler.TriggerStatusVo;
import com.github.doodler.common.utils.BeanCopyUtils;

/**
 * @Description: UIController
 * @Author: Fred Feng
 * @Date: 20/10/2023
 * @Version 1.0.0
 */
@RequestMapping("/ui")
//@Profile({"dev", "test", "prod"})
@ConditionalOnProperty("quartz.admin.ui.enabled")
@Controller
public class UIController {

    @Autowired
    private JobManager jobManager;

    @Autowired
    private JobLogService jobLogService;

    @GetMapping("/")
    public String index() {
        return "forward:/ui/job/group/";
    }

    @GetMapping("/job/new/")
    public String createNewJob(Model model) throws Exception {
        Tip tip = TipContext.getTip();
        if (tip != null) {
            model.addAttribute("tip", tip);
        }
        return "job/new";
    }

    @PostMapping("/job/save/")
    public String saveJob(@ModelAttribute JobDefination jobRequest, Model model) throws Exception {
        JobDefination jobDefination = BeanCopyUtils.copyBean(jobRequest, JobDefination.class);
        setDefaultValue(jobDefination);
        List<String> errors = checkRequiredParameters(jobDefination);
        if (CollectionUtils.isNotEmpty(errors)) {
            Tip tip = new Tip(errors);
            TipContext.setTip(tip);
            return "redirect:/ui/job/new/";
        }

        if (jobManager.isJobExists(jobDefination.getJobName(), jobDefination.getJobGroup())) {
            if (StringUtils.isNotBlank(jobDefination.getTriggerDefination().getCron())) {
                jobManager.modifyCronJob(jobDefination);
            } else {
                jobManager.modifyJob(jobDefination);
            }
        } else {
            if (StringUtils.isNotBlank(jobDefination.getTriggerDefination().getCron())) {
                jobManager.addCronJob(jobDefination);
            } else {
                jobManager.addJob(jobDefination);
            }
        }
        return "redirect:/ui/job/list/" + jobDefination.getJobGroup() + "/";
    }

    @PostMapping("/trigger/edit/{triggerName}/{triggerGroup}/")
    public String editTrigger(@PathVariable("triggerName") String triggerName,
                              @PathVariable("triggerGroup") String triggerGroup, Model model) throws Exception {
        TriggerDefination triggerDefination = jobManager.queryForOneTrigger(triggerName, triggerGroup);
        model.addAttribute("trigger", triggerDefination);
        return "/ui/trigger/edit";
    }

    private void setDefaultValue(JobDefination jobDefination) {
        if (StringUtils.isBlank(jobDefination.getJobGroup())) {
            if (StringUtils.isNotBlank(jobDefination.getApplicationName())) {
                jobDefination.setJobGroup(jobDefination.getApplicationName().toUpperCase());
            } else {
                jobDefination.setJobGroup(DEFAULT_JOB_GROUP_NAME);
            }
        }
        if (StringUtils.isBlank(jobDefination.getTriggerDefination().getTriggerName())) {
            jobDefination.getTriggerDefination().setTriggerName(String.format("%s-TRIGGER", jobDefination.getJobName()));
        }
        if (StringUtils.isBlank(jobDefination.getTriggerDefination().getTriggerGroup())) {
            if (StringUtils.isNotBlank(jobDefination.getApplicationName())) {
                jobDefination.getTriggerDefination().setTriggerGroup(String.format(
                        "%s-TRIGGER-GROUP", jobDefination.getApplicationName().toUpperCase()));
            } else {
                jobDefination.getTriggerDefination().setTriggerGroup(DEFAULT_JOB_TRIGGER_GROUP_NAME);
            }
        }
        if (StringUtils.isNotBlank(jobDefination.getUrl()) && StringUtils.isBlank(jobDefination.getMethod())) {
            jobDefination.setMethod("GET");
        }
    }

    private List<String> checkRequiredParameters(JobDefination jobDefination) {
        List<String> list = new ArrayList<>();
        if (StringUtils.isBlank(jobDefination.getJobName())) {
            list.add("Please specify job name");
        }
        if (StringUtils.isBlank(jobDefination.getUrl()) && StringUtils.isBlank(jobDefination.getClassName())) {
            list.add("Please specify url or class name");
        } else if (StringUtils.isNotBlank(jobDefination.getUrl()) && StringUtils.isNotBlank(jobDefination.getClassName())) {
            list.add("Please specify url or class name");
        }
        if (StringUtils.isNotBlank(jobDefination.getClassName()) && StringUtils.isBlank(jobDefination.getMethod())) {
            list.add("Please specify method name in class '" + jobDefination.getClassName() + "'");
        }
        return list;
    }

    @GetMapping("/job/group/")
    public String indexForJob(Model model) throws Exception {
        List<JobGroupStatusVo> groups = jobManager.queryForJobGroupStatus();
        model.addAttribute("jobGroups", groups);
        return "job/group";
    }

    @GetMapping("/trigger/group/")
    public String indexForTrigger(Model model) throws Exception {
        List<TriggerGroupStatusVo> groups = jobManager.queryForTriggerGroupStatus();
        model.addAttribute("triggerGroups", groups);
        return "trigger/group";
    }

    @GetMapping("/job/list/{jobGroup}/")
    public String pageForJob(@PathVariable("jobGroup") String jobGroup,
                             @RequestParam(name = "jobNamePattern", required = false) String jobNamePattern,
                             @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                             @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                             Model model) throws Exception {
        PageVo<JobStatusVo> pageVo = jobManager.pageForJobStatus(jobGroup, jobNamePattern, pageNumber, pageSize);
        model.addAttribute("page", pageVo);
        if (StringUtils.isNotBlank(jobNamePattern)) {
            model.addAttribute("jobName", jobNamePattern);
        }
        return "job/list";
    }

    @GetMapping("/job/detail/{jobName}/{jobGroup}/")
    public String queryForJobDetail(@PathVariable("jobName") String jobName,
                                    @PathVariable("jobGroup") String jobGroup, Model model) throws Exception {
        JobDefination jobDefination = jobManager.queryForOneJob(jobName, jobGroup);
        model.addAttribute("jobDefination", jobDefination);
        List<TriggerDefination> triggerDefinations = jobManager.queryForTriggerOfJob(jobName, jobGroup);
        model.addAttribute("triggerDefinations", triggerDefinations);
        List<TriggerStatusVo> triggerStatusVos = jobManager.queryForTriggerStatusOfJob(jobName, jobGroup);
        model.addAttribute("triggerStatusVos", triggerStatusVos);
        return "job/detail";
    }

    @GetMapping("/trigger/detail/{triggerName}/{triggerGroup}/")
    public String queryForOneTrigger(@PathVariable("triggerName") String triggerName,
                                     @PathVariable("triggerGroup") String triggerGroup, Model model) throws Exception {
        TriggerDefination triggerDefination = jobManager.queryForOneTrigger(triggerName, triggerGroup);
        model.addAttribute("triggerDefination", triggerDefination);
        return "trigger/detail";
    }

    @GetMapping("/trigger/list/{triggerGroup}/")
    public String pageForTrigger(@PathVariable("triggerGroup") String triggerGroup,
                                 @RequestParam(name = "triggerNamePattern", required = false) String triggerNamePattern,
                                 @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                 @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                                 Model model) throws Exception {
        PageVo<TriggerDefination> pageVo = jobManager.pageForTrigger(triggerGroup, triggerNamePattern, pageNumber,
                pageSize);
        model.addAttribute("page", pageVo);
        return "trigger/list";
    }

    @GetMapping("/trigger/status/{triggerName}/{triggerGroup}/")
    public String queryForOneTriggerStatus(@PathVariable("triggerName") String triggerName,
                                           @PathVariable("triggerGroup") String triggerGroup,
                                           Model model) throws Exception {
        TriggerStatusVo triggerStatusVo = jobManager.queryForOneTriggerStatus(triggerName, triggerGroup);
        model.addAttribute("triggerStatus", triggerStatusVo);
        return "trigger/status/detail";
    }

    @GetMapping("/trigger/status/list/{triggerGroup}/")
    public String pageForTriggerStatus(@PathVariable("triggerGroup") String triggerGroup,
                                       @RequestParam(name = "triggerNamePattern", required = false) String triggerNamePattern,
                                       @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                                       @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                                       Model model) throws Exception {
        PageVo<TriggerStatusVo> pageVo = jobManager.pageForTriggerStatus(triggerGroup,
                triggerNamePattern, pageNumber, pageSize);
        model.addAttribute("page", pageVo);
        if (StringUtils.isNotBlank(triggerNamePattern)) {
            model.addAttribute("triggerName", triggerNamePattern);
        }
        return "trigger/status/list";
    }

    @GetMapping(value = {"/log/", "/job/log/{jobGroup}/", "/job/log/{jobName}/{jobGroup}/",
            "/job/log/{jobName}/{jobGroup}/{triggerName}/{triggerGroup}/",
            "/trigger/log/{triggerGroup}/", "/trigger/log/{triggerName}/{triggerGroup}/"})
    public String pageForLog(@PathVariable(name = "jobName", required = false) String jobName,
                             @PathVariable(name = "jobGroup", required = false) String jobGroup,
                             @PathVariable(name = "triggerName", required = false) String triggerName,
                             @PathVariable(name = "triggerGroup", required = false) String triggerGroup,
                             @RequestParam(name = "status", required = false, defaultValue = "-1") int status,
                             @RequestParam(name = "pageNumber", required = false, defaultValue = "1") int pageNumber,
                             @RequestParam(name = "pageSize", required = false, defaultValue = "10") int pageSize,
                             Model model) throws Exception {
        JobLogQuery logQuery = new JobLogQuery();
        logQuery.setJobGroup(jobGroup);
        logQuery.setJobName(jobName);
        logQuery.setTriggerGroup(triggerGroup);
        logQuery.setTriggerName(triggerName);
        logQuery.setStatus(status);
        logQuery.setPageSize(pageSize);
        logQuery.setPageNumber(pageNumber);
        PageVo<JobLog> pageVo = jobLogService.readLog(logQuery);
        model.addAttribute("page", pageVo);
        if (StringUtils.isNotBlank(jobName)) {
            model.addAttribute("jobName", jobName);
        }
        if (StringUtils.isNotBlank(jobGroup)) {
            model.addAttribute("jobGroup", jobGroup);
        }
        if (StringUtils.isNotBlank(triggerName)) {
            model.addAttribute("triggerName", triggerName);
        }
        if (StringUtils.isNotBlank(triggerGroup)) {
            model.addAttribute("triggerGroup", triggerGroup);
        }
        model.addAttribute("status", status);
        return "log/list";
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "common/about";
    }
}