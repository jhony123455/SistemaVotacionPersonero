/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.ConexionBD.DAO;

import SistemaVotacion.Modelos.Candidatos;
import SistemaVotacion.ConexionBD.Conexion;
import SistemaVotacion.Modelos.Estudiantes;
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
public class CandidatosDAO {
    private final Conexion conexion;
    private final EstudiantesDAO estudiantesDAO;
    
    public CandidatosDAO() {
        this.conexion = new Conexion();
        this.estudiantesDAO = new EstudiantesDAO(); 
    }
   
    public boolean agregarCandidato(Candidatos candidato) {
        String sql = "{CALL agregarCandidato(?, ?)}";
        try (Connection con = conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            Estudiantes estudiante = candidato.getEstudiante();
            Grados grado = estudiante.getGrado();

            if (grado == null || !"11º".equals(grado.getNombreGrado())) {
                System.out.println("Solo los estudiantes de grado 11 pueden ser candidatos.");
                return false;
            }

            int idEstudiante = estudiante.getId();
            if (idEstudiante == -1) {
                System.out.println("No se encontró el estudiante en la base de datos.");
                return false;
            }

            ps.setInt(1, idEstudiante);
            ps.setString(2, candidato.getPropuesta());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al guardar candidato: " + e.getMessage());
            return false;
        }
    }

    public List<Candidatos> obtenerTodosLosCandidatos() {
        List<Candidatos> candidatos = new ArrayList<>();
        String sql = "{CALL obtenerCandidatos()}";
        try (Connection con = conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                candidatos.add(crearCandidatoDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener todos los candidatos: " + e.getMessage());
        }
        return candidatos;
    }

    private Candidatos crearCandidatoDesdeResultSet(ResultSet rs) throws SQLException {
        int idCandidato = rs.getInt("Id_Candidato");
        String propuesta = rs.getString("Propuesta");
        int idEstudiante = rs.getInt("FkEstudiante");
        String nombre = rs.getString("Nombre");
        String apellido = rs.getString("Apellido");
        String documento = rs.getString("Documento");
        String nombreGrado = rs.getString("NombreGrado");

        Grados grado = new Grados(0, nombreGrado);  // El ID del grado no es necesario aquí
        Estudiantes estudiante = new Estudiantes(idEstudiante, nombre, documento, apellido);
        estudiante.AsignarGrado(grado);

        return new Candidatos(idCandidato, estudiante, propuesta);
    }
    
    
    public int contarCandidatos() {
    int contador = 0;
    String sql = "{CALL contarCandidatos()}";

    try (Connection con = conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            contador = rs.getInt(1);
        }
    } catch (SQLException e) {
        System.out.println("Error al contar candidatos: " + e.getMessage());
    }

    return contador;
}
    
    public boolean borrarTodosLosCandidatos() {
    String sql = "{CALL borrarCandidatos()}";
    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        int filasAfectadas = ps.executeUpdate();
        System.out.println("Se han borrado " + filasAfectadas + " candidatos.");
        return true;
    } catch (SQLException e) {
        System.out.println("Error al borrar todos los candidatos: " + e.getMessage());
        return false;
    }
}
}
