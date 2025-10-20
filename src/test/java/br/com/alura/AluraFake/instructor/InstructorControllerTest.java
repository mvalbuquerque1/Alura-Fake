package br.com.alura.AluraFake.instructor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("InstructorController unit tests")
class InstructorControllerTest {

    private InstructorReportService reportService;
    private InstructorController controller;

    @BeforeEach
    void setUp() {
        reportService = mock(InstructorReportService.class);
        controller = new InstructorController(reportService);
    }

    @Test
    @DisplayName("GET /instructor/{id}/courses - returns 200 with summary")
    void getInstructorCourses_success() {
        var dto1 = new InstructorCoursesReportDTO(1L, "C1", br.com.alura.AluraFake.course.Status.PUBLISHED, LocalDateTime.now(), 2);
        var dto2 = new InstructorCoursesReportDTO(2L, "C2", br.com.alura.AluraFake.course.Status.BUILDING, null, 1);
        var summary = new InstructorCoursesSummaryDTO("Inst", "inst@example.com", List.of(dto1, dto2), 1);

        when(reportService.generateReport(10L)).thenReturn(summary);

        ResponseEntity<?> response = controller.getInstructorCourses(10L);

        assertEquals(200, response.getStatusCodeValue());
        assertSame(summary, response.getBody());
    }

    @Test
    @DisplayName("GET /instructor/{id}/courses - returns 404 when instructor not found")
    void getInstructorCourses_notFound() {
        when(reportService.generateReport(11L)).thenThrow(new NoSuchElementException("Instrutor não encontrado"));

        ResponseEntity<?> response = controller.getInstructorCourses(11L);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof java.util.Map);
        var body = (java.util.Map<?,?>) response.getBody();
        assertTrue(body.containsKey("error"));
    }

    @Test
    @DisplayName("GET /instructor/{id}/courses - returns 400 when user is not instructor")
    void getInstructorCourses_badRequest() {
        when(reportService.generateReport(12L)).thenThrow(new IllegalArgumentException("Usuario nao é um instrutor"));

        ResponseEntity<?> response = controller.getInstructorCourses(12L);

        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof java.util.Map);
        var body = (java.util.Map<?,?>) response.getBody();
        assertTrue(body.containsKey("error"));
    }
}

