package application.interfaces;

import java.util.List;

import application.model.Mail;

public interface MailDAOinterface {
    public List<Mail> getAllMail();
    public boolean scriviMail(Mail mail);
}