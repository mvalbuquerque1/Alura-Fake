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

    @Test
    @DisplayName("POST /task/new/singlechoice - returns 201 and saves course with new single-choice task when course exists and is BUILDING")
    void newSingleChoice_success() throws Exception {
        var instructor = new User("Inst", "i@ex.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);

        when(repository.findById(10L)).thenReturn(Optional.of(course));

        var options = List.of(new OptionRequest("Option A", false), new OptionRequest("Option B", true));
        var request = new SingleChoiceTaskRequest(10L, "Choose one", 1, options);

        var response = controller.newSingleChoice(request);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());

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
        assertEquals("Choose one", t.getStatement());
        assertEquals(1, t.getOrderInCourse());
        assertEquals(saved, t.getCourse());

        assertTrue(t instanceof SingleChoiceTask);
        SingleChoiceTask s = (SingleChoiceTask) t;
        assertEquals(2, s.getOptions().size());
        assertTrue(s.getOptions().stream().anyMatch(o -> o.getText().equals("Option B") && o.isCorrect()));
    }

    @Test
    @DisplayName("POST /task/new/singlechoice - throws when course not found")
    void newSingleChoice_courseNotFound() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        var options = List.of(new OptionRequest("Option A", false), new OptionRequest("Option B", true));
        var request = new SingleChoiceTaskRequest(99L, "Choose one", 1, options);

        var ex = assertThrows(IllegalArgumentException.class, () -> controller.newSingleChoice(request));
        assertTrue(ex.getMessage().toLowerCase().contains("curso") || ex.getMessage().toLowerCase().contains("not found"));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("POST /task/new/singlechoice - throws when course is not in BUILDING status")
    void newSingleChoice_courseNotBuilding() {
        var instructor = new User("Inst", "i@ex.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);
        course.setStatus(Status.PUBLISHED);

        when(repository.findById(2L)).thenReturn(Optional.of(course));

        var options = List.of(new OptionRequest("Option A", false), new OptionRequest("Option B", true));
        var request = new SingleChoiceTaskRequest(2L, "Choose one", 1, options);

        var ex = assertThrows(IllegalArgumentException.class, () -> controller.newSingleChoice(request));
        assertTrue(ex.getMessage().toLowerCase().contains("constru") || ex.getMessage().toLowerCase().contains("building"));

        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("POST /task/new/singlechoice - throws when statement already exists in course")
    void newSingleChoice_duplicateStatement() {
        var instructor = new User("Inst", "i@ex.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);
        // add an existing task with same statement
        var existingOptions = List.of(new Option("Option A", false), new Option("Option B", true));
        var existing = new SingleChoiceTask("Duplicate", 1, existingOptions);
        course.addTask(existing);

        when(repository.findById(5L)).thenReturn(Optional.of(course));

        var options = List.of(new OptionRequest("Option X", false), new OptionRequest("Option Y", true));
        var request = new SingleChoiceTaskRequest(5L, "Duplicate", 2, options);

        var ex = assertThrows(IllegalArgumentException.class, () -> controller.newSingleChoice(request));
        assertTrue(ex.getMessage().toLowerCase().contains("já contém") || ex.getMessage().toLowerCase().contains("duplicate"));

        verify(repository, never()).save(any());
    }
}
