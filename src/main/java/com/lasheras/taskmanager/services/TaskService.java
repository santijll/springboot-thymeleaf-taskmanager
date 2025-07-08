package com.lasheras.taskmanager.services;

import com.lasheras.taskmanager.models.Task;
import com.lasheras.taskmanager.repositories.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // retrieves the task from database
    // or throws an error if there is no task matching the id
    private Task findTaskById(Long id) {
        return taskRepository
                .findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + id + " does not exist."));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        return findTaskById(id);
    }

    public Task createTask(String title, String description) {
        // validates title
        if (!StringUtils.hasText(title)) {
            throw new IllegalArgumentException("Title is mandatory");
        }

        // creates a new task
        Task task = new Task(title, description);

        logger.info("Created new task with title: {}", title);

        // saves the task in the database and returns the persisted entity
        return taskRepository.save(task);
    }

    @Transactional
    public Task toggleTaskCompleted(Long id) {
        Task task = findTaskById(id);

        // toggles the completed status
        task.setCompleted(!task.isCompleted());

        logger.info("Toggled task completion status. ID: {}, new status: {}", id, task.isCompleted());

        // save and return updated task
        return taskRepository.save(task);
    }

    @Transactional
    public Task updateTask(Long id, String newTitle, String newDescription) {
        Task task = findTaskById(id);

        if (StringUtils.hasText(newTitle)) {
            task.setTitle(newTitle);
        }

        if (StringUtils.hasText(newDescription)) {
            task.setDescription(newDescription);
        }

        logger.info("Updated task with ID: {}", id);

        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(Long id) {
        Task task = findTaskById(id);
        logger.info("Deleted task with ID: {}, title: {}", id, task.getTitle());
        taskRepository.delete(task);
    }
}
