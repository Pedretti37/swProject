package application.interfaces;

import java.util.List;

import application.model.Terapia;

public interface TerapiaDAOinterface {
    public boolean creaTerapia(Terapia terapia);
    public boolean eliminaTerapia(Terapia terapia);
    public boolean modificaTerapia(Terapia terapia);
    public List<Terapia> getAllTerapie();
}