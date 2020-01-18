package com.github.chaitriplez.openstreaming.controller;

import com.github.chaitriplez.openstreaming.component.Tuple;
import com.github.chaitriplez.openstreaming.repository.Job;
import com.github.chaitriplez.openstreaming.repository.JobDetail;
import com.github.chaitriplez.openstreaming.service.JobService;
import io.swagger.annotations.ApiParam;
import java.time.Duration;
import java.util.List;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Setter
@RestController
public class JobController {

  @Autowired private JobService jobService;

  @GetMapping("/api-os/job/v1/jobs/{jobId}")
  public Tuple<Job, List<JobDetail>> job(
      @PathVariable("jobId") Long jobId,
      @ApiParam(
              name = "wait",
              value = "Maximum waiting time for job done in millisecond(s).",
              example = "0")
          @RequestParam(name = "wait", required = false)
          Long wait) {
    if (wait == null || wait == 0) {
      return jobService.getJob(jobId);
    } else {
      return jobService.waitJob(jobId, Duration.ofMillis(wait));
    }
  }

  @DeleteMapping("/api-os/job/v1/jobs/{jobId}")
  public Tuple<Job, List<JobDetail>> cancelJob(@PathVariable("jobId") Long jobId) {
    return jobService.cancelJob(jobId);
  }

  @PostMapping("/api-os/job/v1/reset")
  public void reset() {
    jobService.cancelAllJobAndResetSequence();
  }
}
