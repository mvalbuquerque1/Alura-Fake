package br.com.alura.AluraFake.instructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/instructor")
public class InstructorController {

    private final InstructorReportService reportService;

    public InstructorController(InstructorReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/{id}/courses")
    public ResponseEntity<?> getInstructorCourses(@PathVariable Long id) {
        try {
            var report = reportService.generateReport(id);
            return ResponseEntity.ok(report);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
