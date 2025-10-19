package br.com.alura.AluraFake.task;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("SINGLE_CHOICE")
public class SingleChoiceTask extends Task {

    @ElementCollection
    @CollectionTable(name = "task_single_options", joinColumns = @JoinColumn(name = "task_id"))

    private List<Option> options;

    protected SingleChoiceTask() {
    }

    public SingleChoiceTask(String statement, int orderInCourse, List<Option> options) {
        super(statement, orderInCourse);
        validateOptions(options, statement);
        this.options = List.copyOf(options);
    }

    private void validateOptions(List<Option> options, String statement) {
        if (options == null || options.size() < 2 || options.size() > 5)
            throw new IllegalArgumentException("A atividade deve ter entre 2 e 5 opcoes.");
        long correctOption = options.stream().filter(Option::isCorrect).count();
        if (correctOption != 1)
            throw new IllegalArgumentException("A atividade deve ter apenas uma alteranativa correta.");

        Set<String> texts = options.stream().map(Option::getText).map(String::toLowerCase).collect(Collectors.toSet());
        if (texts.size() != options.size())
            throw new IllegalArgumentException("As alternativas não podem ser iguais entre si.");

        if (texts.contains(statement.toLowerCase()))
            throw new IllegalArgumentException("As alternativas não podem ser iguais ao enunciado da questão.");
    }

    public List<Option> getOptions() {
        return List.copyOf(options);
    }
}
