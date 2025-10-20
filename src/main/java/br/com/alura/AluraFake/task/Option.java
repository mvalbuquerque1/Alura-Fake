package br.com.alura.AluraFake.task;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Size;

@Embeddable
public class Option {

    @Size(min = 4, max = 80)
    private String text;

    private boolean correct;

    protected Option() {
    }

    public Option(String text, boolean correct) {
        if (text == null || text.trim().length() < 4 || text.trim().length() > 80) {
            throw new IllegalArgumentException("O texto da opção deve ter entre 4 e 80 caracteres");
        }
        this.text = text.trim();
        this.correct = correct;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return correct;
    }
}
