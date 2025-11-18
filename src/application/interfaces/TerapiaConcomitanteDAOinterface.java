package application.interfaces;

import java.util.List;

import application.model.TerapiaConcomitante;

public interface TerapiaConcomitanteDAOinterface {
    public List<TerapiaConcomitante> getAllTerapieConcomitanti();
    public boolean creaTerapiaConcomitante(TerapiaConcomitante terapiaConcomitante);
    public boolean eliminaTerapiaConcomitante(TerapiaConcomitante terapiaConcomitante);
}