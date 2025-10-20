package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.OpenTextTask;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import br.com.alura.AluraFake.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("InstructorReportService unit tests")
class InstructorReportServiceTest {

    private UserRepository userRepository;
    private br.com.alura.AluraFake.course.CourseRepository courseRepository;
    private InstructorReportService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        courseRepository = mock(br.com.alura.AluraFake.course.CourseRepository.class);
        service = new InstructorReportService(userRepository, courseRepository);
    }

    @Test
    @DisplayName("generateReport - success with courses and activity counts")
    void generateReport_success() {
        // arrange
        var instructor = new User("Inst", "inst@example.com", Role.INSTRUCTOR);
        when(userRepository.findById(42L)).thenReturn(Optional.of(instructor));

        Course c1 = new Course("C1", "D1", instructor);
        c1.addTask(new OpenTextTask("Task A1", 1));
        c1.addTask(new OpenTextTask("Task A2", 2));
        c1.setStatus(Status.PUBLISHED);

        Course c2 = new Course("C2", "D2", instructor);
        c2.addTask(new OpenTextTask("Task B1", 1));
        c2.setStatus(Status.BUILDING);

        when(courseRepository.findAll()).thenReturn(List.of(c1, c2));

        // act
        var summary = service.generateReport(42L);

        // debug print
        System.out.println("DEBUG summary.courses(): " + summary.courses());

        // assert
        assertNotNull(summary);
        assertEquals("Inst", summary.instructorName());
        assertEquals("inst@example.com", summary.instructorEmail());
        assertEquals(2, summary.courses().size());
        // find published count
        assertEquals(1, summary.totalPublished());

        var reportForC1 = summary.courses().stream().filter(r -> Objects.equals(r.title(), c1.getTitle())).findFirst().orElseThrow();
        assertEquals(2, reportForC1.activityCount());
    }

    @Test
    @DisplayName("generateReport - throws when user not found")
    void generateReport_userNotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.generateReport(100L));
    }

    @Test
    @DisplayName("generateReport - throws when user is not an instructor")
    void generateReport_userNotInstructor() {
        var user = new User("Student", "s@ex.com", Role.STUDENT);
        when(userRepository.findById(7L)).thenReturn(Optional.of(user));
        assertThrows(IllegalArgumentException.class, () -> service.generateReport(7L));
    }
}
