package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.OpenTextTask;
import br.com.alura.AluraFake.task.Task;
import br.com.alura.AluraFake.user.Role;
import br.com.alura.AluraFake.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Course domain ordering and validation tests")
class CourseDomainTest {

    @Test
    @DisplayName("addTask appends at end when order is max + 1")
    void addTask_appendsAtEnd() {
        var instructor = new User("Inst", "i@ex.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);

        var t1 = new OpenTextTask("Task 1", 1);
        var t2 = new OpenTextTask("Task 2", 2);
        course.addTask(t1);
        course.addTask(t2);

        var newTask = new OpenTextTask("Task 3", 3);
        course.addTask(newTask);

        List<Task> tasks = course.getTasks();
        assertEquals(3, tasks.size());
        assertEquals(1, tasks.get(0).getOrderInCourse());
        assertEquals(2, tasks.get(1).getOrderInCourse());
        assertEquals(3, tasks.get(2).getOrderInCourse());
        assertEquals("Task 3", tasks.get(2).getStatement());
    }

    @Test
    @DisplayName("addTask inserts at beginning and shifts existing orders")
    void addTask_insertsAtBeginning_shifts() {
        var instructor = new User("Inst", "i@ex.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);

        var t1 = new OpenTextTask("FirstTask", 1);
        var t2 = new OpenTextTask("SecondTask", 2);
        var t3 = new OpenTextTask("ThirdTask", 3);
        course.addTask(t1);
        course.addTask(t2);
        course.addTask(t3);

        var newTask = new OpenTextTask("NewFirst", 1);
        course.addTask(newTask);

        List<Task> tasks = course.getTasks();
        assertEquals(4, tasks.size());
        assertEquals("NewFirst", tasks.get(0).getStatement());
        assertEquals(1, tasks.get(0).getOrderInCourse());
        assertEquals(2, tasks.get(1).getOrderInCourse());
        assertEquals(3, tasks.get(2).getOrderInCourse());
        assertEquals(4, tasks.get(3).getOrderInCourse());
    }

    @Test
    @DisplayName("addTask inserts in middle and shifts existing orders")
    void addTask_insertsInMiddle_shifts() {
        var instructor = new User("Inst", "i@ex.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);

        var t1 = new OpenTextTask("Alpha", 1);
        var t2 = new OpenTextTask("Beta", 2);
        var t3 = new OpenTextTask("Gamma", 3);
        course.addTask(t1);
        course.addTask(t2);
        course.addTask(t3);

        var newTask = new OpenTextTask("BetweenTask", 2);
        course.addTask(newTask);

        List<Task> tasks = course.getTasks();
        assertEquals(4, tasks.size());
        assertEquals(1, tasks.get(0).getOrderInCourse());
        assertEquals("BetweenTask", tasks.get(1).getStatement());
        assertEquals(2, tasks.get(1).getOrderInCourse());
        assertEquals(3, tasks.get(2).getOrderInCourse());
        assertEquals(4, tasks.get(3).getOrderInCourse());
    }

    @Test
    @DisplayName("addTask rejects when creating a hole in the sequence")
    void addTask_rejectsHole() {
        var instructor = new User("Inst", "i@ex.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);

        var t1 = new OpenTextTask("FirstOne", 1);
        var t2 = new OpenTextTask("SecondOne", 2);
        course.addTask(t1);
        course.addTask(t2);

        var newTask = new OpenTextTask("HoleTask", 4);
        var ex = assertThrows(IllegalArgumentException.class, () -> course.addTask(newTask));
        assertTrue(ex.getMessage().toLowerCase().contains("lacuna") || ex.getMessage().toLowerCase().contains("sequencia") || ex.getMessage().toLowerCase().contains("continua"));
    }

    @Test
    @DisplayName("getTasks returns an unmodifiable copy")
    void getTasks_isImmutable() {
        var instructor = new User("Inst", "i@ex.com", Role.INSTRUCTOR);
        var course = new Course("Title", "Desc", instructor);

        var t1 = new OpenTextTask("OneTask", 1);
        course.addTask(t1);

        List<Task> tasks = course.getTasks();
        assertThrows(UnsupportedOperationException.class, () -> tasks.add(new OpenTextTask("ExtraTask", 2)));
    }
}
