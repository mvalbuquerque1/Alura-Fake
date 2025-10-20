package br.com.alura.AluraFake.instructor;

import br.com.alura.AluraFake.course.Status;

import java.time.LocalDateTime;

public record InstructorCoursesReportDTO(
        Long courseId,
        String title,
        Status status,
        LocalDateTime publishedAt,
        int activityCount
) { }
