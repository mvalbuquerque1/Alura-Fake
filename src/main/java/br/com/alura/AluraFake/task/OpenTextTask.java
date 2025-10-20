package br.com.alura.AluraFake.task;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("OPEN_TEXT")
public class OpenTextTask extends Task {
    protected OpenTextTask() {
    }

    public OpenTextTask(String statement, int orderInCourse) {
        super(statement, orderInCourse);
    }

}
