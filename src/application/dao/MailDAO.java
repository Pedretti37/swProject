package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import application.admin.Amministratore;
import application.admin.Database;
import application.model.Mail;

public class MailDAO implements application.interfaces.MailDAOinterface {
    public List<Mail> getAllMail() {
        List<Mail> lista = new ArrayList<>();
        String query = "SELECT * FROM mail ORDER BY giorno DESC, orario DESC";
		try(Connection conn = Database.getConnection();
				PreparedStatement stmt = conn.prepareStatement(query);
				ResultSet rs = stmt.executeQuery()) {
			
			while(rs.next()) {
                Mail mail = new Mail(
                    rs.getInt("id"),
                    rs.getString("mittente"),
                    rs.getString("destinatario"),
                    rs.getString("oggetto"),
                    rs.getString("corpo"),
                    rs.getDate("giorno").toLocalDate(),
                    rs.getTime("orario").toLocalTime(),
                    rs.getBoolean("letta"));
                lista.add(mail);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
        return lista;
    }

    public boolean scriviMail(Mail m) {
        String query = "INSERT INTO mail (mittente, destinatario, oggetto, corpo, giorno, orario, letta) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try (Connection conn = Database.getConnection(); 
	    		PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, m.getMittente());
            stmt.setString(2, m.getDestinatario());
            stmt.setString(3, m.getOggetto());
            stmt.setString(4, m.getCorpo());
            stmt.setDate(5, java.sql.Date.valueOf(LocalDate.now()));
            stmt.setTime(6, java.sql.Time.valueOf(LocalTime.now()));
            stmt.setBoolean(7, false);
                    
            int rows = stmt.executeUpdate();

            if (rows > 0) {
                Amministratore.loadMailFromDatabase();
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