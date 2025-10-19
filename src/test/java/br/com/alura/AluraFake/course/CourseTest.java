package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.OpenTextTask;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Course domain tests")
class CourseTest {

    @Test
    @DisplayName("should attach task to course when course is in BUILDING")
    void addTaskAssignsCourseWhenBuilding() {
        var instructor = new User("Instructor", "inst@example.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);

        var task = new OpenTextTask("Statement 1", 1);

        assertDoesNotThrow(() -> course.addTask(task));
        assertNotNull(task.getCourse());
        assertEquals(course, task.getCourse());
    }

    @Test
    @DisplayName("should not allow adding task when course is not in BUILDING")
    void addTaskFailsWhenNotBuilding() {
        var instructor = new User("Instructor", "inst@example.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);
        course.setStatus(Status.PUBLISHED);

        var task = new OpenTextTask("Statement 1", 1);

        var ex = assertThrows(IllegalArgumentException.class, () -> course.addTask(task));
        assertTrue(ex.getMessage().toLowerCase().contains("construção") || ex.getMessage().toLowerCase().contains("constru"));
    }

    @Test
    @DisplayName("should not allow duplicate statements within the same course (case insensitive)")
    void duplicateStatementIsRejected() {
        var instructor = new User("Instructor", "inst@example.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);

        var t1 = new OpenTextTask("Same Statement", 1);
        var t2 = new OpenTextTask("same statement", 2);

        course.addTask(t1);
        var ex = assertThrows(IllegalArgumentException.class, () -> course.addTask(t2));
        assertTrue(ex.getMessage().toLowerCase().contains("já contém") || ex.getMessage().toLowerCase().contains("já cont"));
    }
}

