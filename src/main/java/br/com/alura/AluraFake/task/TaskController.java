package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/task/new")
public class TaskController {

    private final CourseRepository repository;

    public TaskController(CourseRepository repository) {
        this.repository = repository;
    }

    @Transactional
    @PostMapping("/opentext")
    public ResponseEntity newOpenTextExercise(@RequestBody @Valid OpenTextTaskRequest request) {
        Course course = repository.findById(request.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        validateCourseStatus(course);
        course.addTask(new OpenTextTask(request.statement(), request.order()));
        repository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/singlechoice")
    public ResponseEntity newSingleChoice(@RequestBody @Valid SingleChoiceTaskRequest request) {
        Course course = repository.findById(request.courseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        validateCourseStatus(course);

        List<Option> options = request.options()
                .stream()
                .map(o -> new Option(o.option(), o.isCorrect()))
                .toList();

        course.addTask(new SingleChoiceTask(request.statement(), request.order(), options));
        repository.save(course);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/task/new/multiplechoice")
    public ResponseEntity newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

    private static void validateCourseStatus(Course course) {
        if (course.getStatus() != Status.BUILDING)
            throw new IllegalArgumentException("Apenas cursos em construção podem receber tarefas");
    }

}