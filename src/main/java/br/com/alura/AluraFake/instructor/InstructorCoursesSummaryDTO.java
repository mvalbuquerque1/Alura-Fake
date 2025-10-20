package br.com.alura.AluraFake.instructor;

import java.util.List;

public record InstructorCoursesSummaryDTO(
        String instructorName,
        String instructorEmail,
        List<InstructorCoursesReportDTO> courses,
        long totalPublished
) { }
