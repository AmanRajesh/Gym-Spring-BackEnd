package com.automlhybrid.gymspringbackend.services;



import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class GpuJobProducer {

    // private final RabbitTemplate rabbitTemplate; // Uncomment when RabbitMQ is ready

    public void submitJob(Map<String, Object> jobData) {
        // TODO: Uncomment when RabbitMQ is running
        // rabbitTemplate.convertAndSend("gpu_training_queue", jobData);

        System.out.println("⚠️ MOCK DISPATCH: Sent job to GPU Queue -> " + jobData);
    }
}
