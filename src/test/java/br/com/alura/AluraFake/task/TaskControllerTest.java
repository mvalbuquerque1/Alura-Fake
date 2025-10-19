package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("TaskController unit tests")
class TaskControllerTest {

    private CourseRepository repository;
    private TaskController controller;

    @BeforeEach
    void setup() {
        repository = mock(CourseRepository.class);
        controller = new TaskController(repository);
    }

    @Test
    @DisplayName("POST /task/new/opentext - returns 201 and saves course with new task when course exists and is BUILDING")
    void newOpenTextExercise_success() throws Exception {
        var instructor = new User("Inst", "i@ex.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);

        when(repository.findById(1L)).thenReturn(Optional.of(course));

        var request = new OpenTextTaskRequest(1L, "A valid statement", 1);

        var response = controller.newOpenTextExercise(request);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());

        // capture saved course and inspect its tasks via reflection (no public getter)
        ArgumentCaptor<Course> captor = ArgumentCaptor.forClass(Course.class);
        verify(repository, times(1)).save(captor.capture());
        Course saved = captor.getValue();

        Field tasksField = Course.class.getDeclaredField("tasks");
        tasksField.setAccessible(true);
        @SuppressWarnings("unchecked")
        List<Task> tasks = (List<Task>) tasksField.get(saved);

        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        Task t = tasks.get(0);
        assertEquals("A valid statement", t.getStatement());
        assertEquals(1, t.getOrderInCourse());
        assertEquals(saved, t.getCourse());
    }

    @Test
    @DisplayName("POST /task/new/opentext - throws when course not found")
    void newOpenTextExercise_courseNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        var request = new OpenTextTaskRequest(99L, "Valid statement", 1);

        var ex = assertThrows(IllegalArgumentException.class, () -> controller.newOpenTextExercise(request));
        assertTrue(ex.getMessage().toLowerCase().contains("curso") || ex.getMessage().toLowerCase().contains("not found"));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("POST /task/new/opentext - throws when course is not in BUILDING status")
    void newOpenTextExercise_courseNotBuilding() {
        var instructor = new User("Inst", "i@ex.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);
        course.setStatus(Status.PUBLISHED);

        when(repository.findById(2L)).thenReturn(Optional.of(course));

        var request = new OpenTextTaskRequest(2L, "Valid statement", 1);

        var ex = assertThrows(IllegalArgumentException.class, () -> controller.newOpenTextExercise(request));
        assertTrue(ex.getMessage().toLowerCase().contains("constru") || ex.getMessage().toLowerCase().contains("building"));

        verify(repository, never()).save(any());
    }
}

