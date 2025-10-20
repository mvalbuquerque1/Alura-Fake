package br.com.alura.AluraFake.task;

import jakarta.persistence.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
public class MultipleChoiceTask extends Task {


    @ElementCollection
    @CollectionTable(name = "task_multiple_options", joinColumns = @JoinColumn(name = "task_id"))
    @OrderColumn(name = "option_index")
    private List<Option> options;

    protected MultipleChoiceTask() {
    }

    public MultipleChoiceTask(String statement, int orderInCourse, List<Option> options) {
        super(statement, orderInCourse);
        validateOptions(options, statement);
        this.options = List.copyOf(options);
    }

    private void validateOptions(List<Option> options, String statement) {
        if (options == null || options.size() < 3 || options.size() > 5)
            throw new IllegalArgumentException("A atividade deve ter entre 3 e 5 alternativas.");

        long correctOptions = options.stream().filter(Option::isCorrect).count();
        long incorrect = options.size() - correctOptions;

        if (correctOptions < 2)
            throw new IllegalArgumentException("A atividade deve ter pelo menos duas alternativas corretas.");

        if (incorrect < 1)
            throw new IllegalArgumentException("A atividade deve ter pelo menos uma alternativa incorreta.");


        Set<String> texts = options.stream().map(Option::getText).map(String::toLowerCase).collect(Collectors.toSet());
        if (texts.size() != options.size())
            throw new IllegalArgumentException("As alternativas não podem ser iguais entre si.");


        if (texts.contains(statement.toLowerCase()))
            throw new IllegalArgumentException("As alternativas não podem ser iguais ao enunciado da atividade.");
    }

    public List<Option> getOptions() {
        return List.copyOf(options);
    }
}
