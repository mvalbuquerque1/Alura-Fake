package br.com.alura.AluraFake.task;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task domain tests")
class TaskTest {

    @Nested
    @DisplayName("OpenTextTask constructor validation")
    class OpenTextValidation {

        @Test
        @DisplayName("should create OpenTextTask with valid statement and positive order")
        void createValidOpenTextTask() {
            var task = new OpenTextTask("This is a valid statement", 1);
            assertEquals("This is a valid statement", task.getStatement());
            assertEquals(1, task.getOrderInCourse());
        }

        @Test
        @DisplayName("should fail when statement is null")
        void nullStatementThrows() {
            var ex = assertThrows(IllegalArgumentException.class, () -> new OpenTextTask(null, 1));
            assertTrue(ex.getMessage().toLowerCase().contains("nulo") || ex.getMessage().toLowerCase().contains("null") );
        }

        @Test
        @DisplayName("should fail when statement is too short")
        void shortStatementThrows() {
            var ex = assertThrows(IllegalArgumentException.class, () -> new OpenTextTask("abc", 1));
            assertTrue(ex.getMessage().toLowerCase().contains("entre 4 e 255") || ex.getMessage().toLowerCase().contains("entre 4"));
        }

        @Test
        @DisplayName("should fail when order is not positive")
        void nonPositiveOrderThrows() {
            var ex = assertThrows(IllegalArgumentException.class, () -> new OpenTextTask("valid statement", 0));
            assertTrue(ex.getMessage().toLowerCase().contains("ordem") );
        }
    }
}

