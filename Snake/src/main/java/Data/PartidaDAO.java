package Data;

import Entities.Partida;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PartidaDAO {
    private static final String INSERT_SQL = "INSERT INTO Partidas (IdJugador, FechaHoraInicio) VALUES (?, ?)";
    private static final String UPDATE_SQL = "UPDATE Partidas SET FechaHoraFin = ?, Puntaje = ? WHERE IdPartida = ?";
    private static final String SELECT_BY_JUGADOR_SQL = "SELECT * FROM Partidas WHERE IdJugador = ? ORDER BY FechaHoraInicio DESC";
    private static final String SELECT_TOP_SCORES_SQL = "SELECT TOP 10 p.*, j.Pseudonimo FROM Partidas p JOIN Jugadores j ON p.IdJugador = j.IdJugador ORDER BY p.Puntaje DESC";

    /**
     * Inserta una nueva partida en la base de datos
     * @param partida la partida a insertar
     * @return el ID generado para la partida, o -1 si falla
     * @throws SQLException si ocurre un error de base de datos
     */
    public int insert(Partida partida) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, partida.getIdJugador());
            pstmt.setTimestamp(2, Timestamp.valueOf(partida.getFechaHoraInicio()));
            pstmt.executeUpdate();
            
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    /**
     * Actualiza una partida existente con fecha de fin y puntaje
     * @param partida la partida a actualizar
     * @throws SQLException si ocurre un error de base de datos
     */
    public void update(Partida partida) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SQL)) {
            
            pstmt.setTimestamp(1, Timestamp.valueOf(partida.getFechaHoraFin()));
            pstmt.setInt(2, partida.getPuntaje());
            pstmt.setInt(3, partida.getId());
            pstmt.executeUpdate();
        }
    }

    /**
     * Busca todas las partidas de un jugador específico
     * @param idJugador el ID del jugador
     * @return lista de partidas del jugador ordenadas por fecha descendente
     * @throws SQLException si ocurre un error de base de datos
     */
    public List<Partida> findByJugador(int idJugador) throws SQLException {
        List<Partida> partidas = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SELECT_BY_JUGADOR_SQL)) {
            
            pstmt.setInt(1, idJugador);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Partida partida = new Partida();
                    partida.setId(rs.getInt("IdPartida"));
                    partida.setIdJugador(rs.getInt("IdJugador"));
                    partida.setFechaHoraInicio(rs.getTimestamp("FechaHoraInicio").toLocalDateTime());
                    // Manejo seguro de FechaHoraFin que puede ser null
                    Timestamp fechaFin = rs.getTimestamp("FechaHoraFin");
                    if (fechaFin != null) {
                        partida.setFechaHoraFin(fechaFin.toLocalDateTime());
                    }
                    partida.setPuntaje(rs.getInt("Puntaje"));
                    partidas.add(partida);
                }
            }
        }
        return partidas;
    }

    /**
     * Obtiene las 10 mejores puntuaciones con información del jugador
     * @return lista de las mejores partidas ordenadas por puntaje descendente
     * @throws SQLException si ocurre un error de base de datos
     */
    public List<Partida> getTopScores() throws SQLException {
        List<Partida> partidas = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_TOP_SCORES_SQL)) {
            
            while (rs.next()) {
                Partida partida = new Partida();
                partida.setId(rs.getInt("IdPartida"));
                partida.setIdJugador(rs.getInt("IdJugador"));
                partida.setFechaHoraInicio(rs.getTimestamp("FechaHoraInicio").toLocalDateTime());
                partida.setFechaHoraFin(rs.getTimestamp("FechaHoraFin").toLocalDateTime());
                partida.setPuntaje(rs.getInt("Puntaje"));
                partida.setPseudonimoJugador(rs.getString("Pseudonimo")); // Necesitarás agregar este campo en la entidad
                partidas.add(partida);
            }
        }
        return partidas;
    }
}