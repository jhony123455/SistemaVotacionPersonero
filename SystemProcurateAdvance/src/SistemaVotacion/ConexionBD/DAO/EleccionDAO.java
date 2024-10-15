/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.ConexionBD.DAO;

import SistemaVotacion.ConexionBD.Conexion;
import SistemaVotacion.Modelos.Eleccion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class EleccionDAO {
    
     private Conexion conexion;

    public EleccionDAO() {
        this.conexion = new Conexion();
    }
 
    public void guardarEleccion(Eleccion eleccion) throws SQLException {
        String sql = "{CALL guardarEleccion(?, ?)}";

        try (Connection con = conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(eleccion.getFechainicio()));
            stmt.setTimestamp(2, Timestamp.valueOf(eleccion.getFechafin()));
            stmt.executeUpdate();
        }
    }

    public Eleccion obtenerEleccionActual() throws SQLException {
        String sql = "{CALL obtenerEleccionActual(?)}";
        LocalDateTime now = LocalDateTime.now();

        try (Connection con = conexion.getConnection(); PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(now));
           

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("Id_Eleccion");
                    LocalDateTime fechaHoraInicio = rs.getTimestamp("fecha_Inicio").toLocalDateTime();
                    LocalDateTime fechaHoraFin = rs.getTimestamp("fecha_Fin").toLocalDateTime();
                    return new Eleccion(id, fechaHoraInicio, fechaHoraFin);
                }
            }
        }
        return null;
    }
    
    public void insertarEleccion(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws SQLException {
        if (hayEleccionActiva()) {
            throw new SQLException("No se puede crear una nueva elección mientras haya una activa");
        }

        String sql = "{CALL insertarEleccion(?, ?)}";
        try (Connection con = conexion.getConnection(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setTimestamp(1, Timestamp.valueOf(fechaInicio));
            cs.setTimestamp(2, Timestamp.valueOf(fechaFin));
            cs.execute();
        }
    }

    private static final Logger LOGGER = Logger.getLogger(EleccionDAO.class.getName());

    public boolean hayEleccionActiva() throws SQLException {
        String sql = "{CALL hayEleccionActiva(?)}";
        try (Connection conn = conexion.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            LocalDateTime ahora = LocalDateTime.now();
            pstmt.setTimestamp(1, Timestamp.valueOf(ahora));


            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    LOGGER.info("Número de elecciones activas encontradas: " + count);
                    return count > 0;
                }
            }
        }
        LOGGER.info("No se encontraron elecciones activas");
        return false;
    }
    
    public boolean hayVotosRegistrados() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Voto";
        try (Connection conn = conexion.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
        }
        return false;
    }

    
   public List<Eleccion> obtenerElecciones() throws SQLException {
    List<Eleccion> elecciones = new ArrayList<>();
    String query = "{ CALL ObtenerElecciones() }";
    try (Connection conn = conexion.getConnection(); CallableStatement cs = conn.prepareCall(query)) {
        ResultSet rs = cs.executeQuery();
        while (rs.next()) {
            int idEleccion = rs.getInt("Id_Eleccion");
            LocalDate fechaInicio = rs.getDate("Fecha_Inicio").toLocalDate();
            LocalDate fechaFin = rs.getDate("Fecha_Fin").toLocalDate();
            Eleccion eleccion = new Eleccion(idEleccion, fechaInicio.atStartOfDay(), fechaFin.atStartOfDay());
            elecciones.add(eleccion);
        }
    }
    return elecciones;
}



}



