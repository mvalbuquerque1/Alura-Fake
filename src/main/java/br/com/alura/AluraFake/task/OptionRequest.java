package br.com.alura.AluraFake.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OptionRequest(
        @NotBlank @Size(min = 4, max = 80) String option,
        boolean isCorrect
) {
}
