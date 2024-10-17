/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.ConexionBD.DAO;

import SistemaVotacion.ConexionBD.Conexion;
import SistemaVotacion.Modelos.Voto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author HP
 */
public class VotoDAO {
    private Conexion conexion;

    public VotoDAO() {
        this.conexion = new Conexion();
    }
    
    public void guardarVoto(Voto voto) throws SQLException {
        String sql = "{CALL guardarVoto(?, ?, ?)}";

        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, voto.getEleccion().getIdeleccion());

            if (voto.getCandidatos() != null) {
                ps.setInt(2, voto.getCandidatos().getIdCandidato());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);  
            }

            ps.setInt(3, voto.getEstudiantes().getId());
            ps.executeUpdate();
        }
    }
    
     public boolean estudianteYaVoto(int idEstudiante, int idEleccion) throws SQLException {
        String sql = "{CALL estudianteYaVoto(?, ?)}";
        try (Connection conn = conexion.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idEstudiante);
            stmt.setInt(2, idEleccion);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
     
      public boolean hayVotosRegistrados() throws SQLException {
        boolean hayVotos = false;
        String sql = "SELECT COUNT(*) FROM Votos"; 

        try (Connection conn = conexion.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                int conteoVotos = rs.getInt(1); 
                hayVotos = conteoVotos > 0; 
            }
        }

        return hayVotos;
    }
    
   
}

