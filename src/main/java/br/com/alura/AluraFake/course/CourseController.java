package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Autowired
    public CourseController(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    @PostMapping("/new")
    public ResponseEntity createCourse(@Valid @RequestBody NewCourseDTO newCourse) {

        //Caso implemente o bonus, pegue o instrutor logado
        Optional<User> possibleAuthor = userRepository
                .findByEmail(newCourse.getEmailInstructor())
                .filter(User::isInstructor);

        if (possibleAuthor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("emailInstructor", "Usuário não é um instrutor"));
        }

        Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), possibleAuthor.get());

        courseRepository.save(course);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseListItemDTO>> listAll() {
        List<CourseListItemDTO> courses = courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity publish(@PathVariable("id") Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        course.publishCourse();
        courseRepository.save(course);
        return ResponseEntity.ok().build();
    }
}
