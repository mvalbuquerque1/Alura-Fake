package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.MultipleChoiceTask;
import br.com.alura.AluraFake.task.OpenTextTask;
import br.com.alura.AluraFake.task.SingleChoiceTask;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.user.User;
import jakarta.persistence.*;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Entity
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String title;
    private String description;
    @ManyToOne
    private User instructor;
    @Enumerated(EnumType.STRING)
    private Status status;
    private LocalDateTime publishedAt;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderInCourse ASC")
    private List<Task> tasks = new ArrayList<>();

    @Deprecated
    public Course() {
    }

    public Course(String title, String description, User instructor) {
        Assert.isTrue(instructor.isInstructor(), "Usuario deve ser um instrutor");
        this.title = title;
        this.instructor = instructor;
        this.description = description;
        this.status = Status.BUILDING;
    }

    public void addTask(Task newTask) {
        // delegate validations to the validator utility
        CourseTaskValidator.validateAdd(this, newTask);

        int newOrder = newTask.getOrderInCourse();

        // shift existing tasks' orders in reverse to avoid temporary collisions
        for (int i = tasks.size() - 1; i >= 0; i--) {
            Task task = tasks.get(i);
            if (task.getOrderInCourse() >= newOrder) {
                task.incrementOrder();
            }
        }

        newTask.attachTo(this);
        tasks.add(newTask);
        tasks.sort(Comparator.comparingInt(Task::getOrderInCourse));
    }

    public void publishCourse() {
        validatePublishPreconditions();

        this.status = Status.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
    }

    private void validatePublishPreconditions() {
        if (status != Status.BUILDING)
            throw new IllegalArgumentException("Curso deve estar em estado BUILDING para ser publicado");

        if (tasks == null || tasks.isEmpty())
            throw new IllegalArgumentException("Curso deve conter ao menos uma tarefa para ser publicado");

        boolean hasOpen = tasks.stream().anyMatch(t -> t instanceof OpenTextTask);
        boolean hasSingle = tasks.stream().anyMatch(t -> t instanceof SingleChoiceTask);
        boolean hasMulti = tasks.stream().anyMatch(t -> t instanceof MultipleChoiceTask);
        if (!(hasOpen && hasSingle && hasMulti))
            throw new IllegalArgumentException("Curso deve conter ao menos uma tarefa de cada tipo para ser publicado");

        var orders = tasks.stream().map(Task::getOrderInCourse).sorted().toList();
        for (int i = 0; i < orders.size(); i++) {
            int expected = i + 1;
            if (orders.get(i) != expected)
                throw new IllegalArgumentException("Ordem das tarefas deve ser sequencial");
        }
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getTitle() {
        return title;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public User getInstructor() {
        return instructor;
    }

    public String getDescription() {
        return description;
    }

    public Status getStatus() {
        return status;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public List<Task> getTasks() {
        return List.copyOf(tasks);
    }

}
