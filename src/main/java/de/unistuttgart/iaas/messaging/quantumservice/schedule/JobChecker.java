package de.unistuttgart.iaas.messaging.quantumservice.schedule;

import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.api.IBMQClient;
import de.unistuttgart.iaas.messaging.quantumservice.messaging.JobResultSender;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.messaging.quantumservice.model.ibmq.IBMQJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobChecker {

    private final IBMQClient ibmqClient;
    private final JobRepository jobRepository;
    private final JobResultSender jobResultSender;

    @Transactional
    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void checkJobStatus() {
        Set<Job> runningJobs = jobRepository.findRunningJobs();
        log.info("Checking " + runningJobs.size() + " running jobs...");
        for (Job runningJob : runningJobs) {
            IBMQJob ibmqJob = ibmqClient.getJob("ibm-q", "open", "main", runningJob.getIbmqId());
            runningJob.setStatus(JobStatus.valueOf(ibmqJob.getStatus()));
            runningJob.setCreationDate(ibmqJob.getCreationDate());

            if (ibmqJob.getStatus().equals("COMPLETED")) {
                runningJob.setEndDate(ibmqJob.getEndDate());
                runningJob.setResult(ibmqClient.getJobResult("ibm-q", "open", "main", runningJob.getIbmqId()));
                runningJob.setSuccess(ibmqJob.getSummaryData().getSuccess());

                jobResultSender.sendJobResult(runningJob.getResult());
            }

            jobRepository.save(runningJob);
        }
    }
}
