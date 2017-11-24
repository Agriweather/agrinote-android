package tw.com.agrinote.model;

import java.util.List;

/**
 * Created by orc59 on 2017/11/11.
 */

public class Problem {

    private String question;
    private Boolean input;
    private int type;
    private String hint;
    private int inputType;

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getInput() {
        return input;
    }

    public void setInput(Boolean input) {
        this.input = input;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }

    @Override
    public String toString() {
        return "Problem{" +
                "question='" + question + '\'' +
                ", input=" + input +
                ", type=" + type +
                ", hint='" + hint + '\'' +
                ", inputType=" + inputType +
                '}';
    }
}
