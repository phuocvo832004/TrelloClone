package com.example.taskservice.kafka;

import com.example.commonevents.events.TaskEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TaskEventProducer {
    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public TaskEventProducer(KafkaTemplate<String, TaskEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTaskEvent(TaskEvent event) {
        kafkaTemplate.send("task-events", event);
    }
}
