package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.admin.Amministratore;
import application.admin.Database;
import application.model.Questionario;

public class QuestionarioDAO implements application.interfaces.QuestionarioDAOinterface {
    
    public List<Questionario> getAllQuestionario() {
        List<Questionario> lista = new ArrayList<>();
        String query = "SELECT * FROM questionario ORDER BY giornoCompilazione DESC";
		try(Connection conn = Database.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			
			while(rs.next()) {
				Questionario q = new Questionario(
						rs.getString("CF"),
						rs.getDate("giornoCompilazione").toLocalDate(),
						rs.getString("nomeFarmaco"),
						rs.getInt("dosiGiornaliere"),
						rs.getInt("quantità"),
						rs.getString("sintomi"),
						rs.getBoolean("controllato")
					);
                lista.add(q);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return lista;
    }

    public boolean creaQuestionario(Questionario q) {
        String query = "INSERT INTO questionario (CF, giornoCompilazione, nomeFarmaco, dosiGiornaliere, quantità, sintomi, controllato) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = Database.getConnection(); 
			PreparedStatement stmt = conn.prepareStatement(query)) {

			stmt.setString(1, q.getCf());
			stmt.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
			stmt.setString(3, q.getNomeFarmaco());
			stmt.setInt(4, q.getDosiGiornaliere());
			stmt.setInt(5, q.getQuantità());
			stmt.setString(6, q.getSintomi());
			stmt.setBoolean(7, false);

			int rows = stmt.executeUpdate();

			if (rows > 0) {
				Amministratore.loadQuestionarioFromDatabase();
                return true;
			} else {
				return false;
			}

		} catch (SQLException e) {
			e.printStackTrace();
            return false;
		}
    }
}