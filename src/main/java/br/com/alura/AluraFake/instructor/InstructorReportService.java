package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.user.UserRepository;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Objects;

@Service
public class InstructorReportService {

    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public InstructorReportService(UserRepository userRepository, CourseRepository courseRepository) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    public InstructorCoursesSummaryDTO generateReport(Long instructorId) {
        var user = userRepository.findById(instructorId)
                .orElseThrow(() -> new NoSuchElementException("Instrutor não encontrado"));

        if (!user.isInstructor()) {
            throw new IllegalArgumentException("Usuario não é um instrutor");
        }

        var courses = courseRepository.findAll().stream()
                // handle cases where User.id may be null in tests by also comparing instance
                .filter(c -> c.getInstructor() == user || Objects.equals(c.getInstructor().getId(), instructorId))
                .map(c -> new InstructorCoursesReportDTO(
                        c.getId(),
                        c.getTitle(),
                        c.getStatus(),
                        c.getPublishedAt(),
                        c.getTasks().size()
                ))
                .toList();

        long totalPublished = courses.stream()
                .filter(c -> c.status() == Status.PUBLISHED)
                .count();

        return new InstructorCoursesSummaryDTO(
                user.getName(),
                user.getEmail(),
                courses,
                totalPublished
        );
    }
}
