/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.ConexionBD.DAO;

import SistemaVotacion.ConexionBD.Conexion;
import SistemaVotacion.Modelos.Grados;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author HP
 */
public class GradosDAO {
    private Conexion conexion;

    public GradosDAO() {
        this.conexion = new Conexion();
    }

    public List<Grados> obtenerGrados() {
        List<Grados> listaGrados = new ArrayList<>();
        String sql = "call obtenerTodosLosGrados()";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("Id_Grado");
                String nombre = rs.getString("NombreGrado");
                listaGrados.add(new Grados(id, nombre));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener grados: " + e.getMessage());
        }
        return listaGrados;
    }

    public Grados obtenerGradoPorId(int idGrado) {
        String sql = "{CALL ObtenerGradoPorId(?)}";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idGrado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("NombreGrado");
                    return new Grados(idGrado, nombre);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener grado por ID: " + e.getMessage());
        }
        return null;
    }

    public int buscarIdGrado(String nombreGrado) {
        String sql = "{CALL BuscarIdGradoPorNombre(?)}";
        try (Connection con = conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nombreGrado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Id_Grado");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar ID de grado: " + e.getMessage());
        }
        return -1;
    }
}

