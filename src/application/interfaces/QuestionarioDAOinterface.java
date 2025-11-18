package application.interfaces;

import java.util.List;

import application.model.Questionario;

public interface QuestionarioDAOinterface {
    public List<Questionario> getAllQuestionario();
    public boolean creaQuestionario(Questionario questionario);
} 