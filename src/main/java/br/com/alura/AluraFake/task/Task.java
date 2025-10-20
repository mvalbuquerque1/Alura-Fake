package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;

@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "task_type", discriminatorType = DiscriminatorType.STRING)
@Entity
@Table(name = "Task")
public abstract class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String statement;

    @Column(name = "order_in_course", nullable = false)
    private int orderInCourse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    protected Task() {
    }

    protected Task(String statement, int orderInCourse) {
        validate(statement, orderInCourse);
        this.statement = statement.trim();
        this.orderInCourse = orderInCourse;
    }

    private void validate(String statement, int order) {
        if (statement == null || statement.isBlank())
            throw new IllegalArgumentException("Enunciado não pode ser nulo ou vazio");
        int length = statement.trim().length();
        if (length < 4 || length > 255)
            throw new IllegalArgumentException("Enunciado deve ter entre 4 e 255 caracteres");
        if (order <= 0)
            throw new IllegalArgumentException("Ordem deve ser positiva");
    }

    public void incrementOrder() {
        this.orderInCourse++;
    }
    public void attachTo(Course c) {
        this.course = c;
    }

    public Long getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public int getOrderInCourse() {
        return orderInCourse;
    }

    public Course getCourse() {
        return course;
    }
}
