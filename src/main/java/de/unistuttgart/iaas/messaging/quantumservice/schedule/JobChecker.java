package de.unistuttgart.iaas.messaging.quantumservice.schedule;

import java.util.Set;

import de.unistuttgart.iaas.messaging.quantumservice.api.IBMQClient;
import de.unistuttgart.iaas.messaging.quantumservice.messaging.JobResultSender;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.Job;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobRepository;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.job.JobStatus;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplication;
import de.unistuttgart.iaas.messaging.quantumservice.model.entity.quantumapplication.QuantumApplicationRepository;
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
    private final QuantumApplicationRepository quantumApplicationRepository;
    private final JobResultSender jobResultSender;

    /**
     * This method is performed on a schedule. It checks all IBMQ-Jobs that are currently running and updates their
     * status. It also retrieves the results of completed jobs from the IBMQ-Servers and forwards them to the defined
     * Reply-To destination of the job for further processing.
     */
    @Transactional
    @Scheduled(initialDelay = 5000, fixedDelay = 10000)
    public void checkJobStatus() {
        Set<Job> runningJobs = jobRepository.findRunningJobs();
        log.info("Checking " + runningJobs.size() + " running jobs...");
        for (Job runningJob : runningJobs) {
            IBMQJob ibmqJob = ibmqClient.getJob("ibm-q", "open", "main", runningJob.getIbmqId());
            runningJob.setStatusDetails(ibmqJob.getTimePerStep());
            runningJob.setStatus(JobStatus.valueOf(ibmqJob.getStatus()));
            runningJob.setCreationDate(ibmqJob.getCreationDate());

            // Get result of completed jobs
            if (ibmqJob.getStatus().equals("COMPLETED")) {
                runningJob.setEndDate(ibmqJob.getEndDate());
                runningJob.setResult(ibmqClient.getJobResult("ibm-q", "open", "main", runningJob.getIbmqId()));
                runningJob.setSuccess(ibmqJob.getSummaryData().getSuccess());
            }

            // Send job status changed event
            jobResultSender.sendJobStatusReachedEvent(runningJob);

            runningJob = jobRepository.save(runningJob);

            // Reactivate application so it can be executed again after completion
            if (runningJob.getStatus() == JobStatus.COMPLETED) {
                QuantumApplication jobApplication = runningJob.getQuantumApplication();
                log.info("Reactivating application '{}' after successfully executed job...", jobApplication.getName());
                jobApplication.setExecutionEnabled(true);
                quantumApplicationRepository.save(jobApplication);
            }
        }
    }
}
