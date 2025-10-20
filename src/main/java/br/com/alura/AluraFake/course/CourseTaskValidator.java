package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.Task;

import java.util.Objects;

public final class CourseTaskValidator {

    private CourseTaskValidator() {
    }

    public static void validateAdd(Course course, Task newTask) {
        Objects.requireNonNull(course, "Curso não pode ser nulo");

        if (newTask == null)
            throw new IllegalArgumentException("Tarefa não pode ser nula");

        if (course.getStatus() != Status.BUILDING)
            throw new IllegalArgumentException("Apenas cursos em construção podem receber tarefas");

        boolean duplicateStatement = course.getTasks().stream()
                .anyMatch(t -> t.getStatement().equalsIgnoreCase(newTask.getStatement()));
        if (duplicateStatement)
            throw new IllegalArgumentException("Curso já contém uma tarefa com este enunciado");

        int newOrder = newTask.getOrderInCourse();
        if (newOrder <= 0)
            throw new IllegalArgumentException("Ordem deve ser positiva");

        int maxOrder = course.getTasks().stream()
                .mapToInt(Task::getOrderInCourse)
                .max()
                .orElse(0);

        if (newOrder > maxOrder + 1)
            throw new IllegalArgumentException("Sequência deve ser contínua");
    }
}

