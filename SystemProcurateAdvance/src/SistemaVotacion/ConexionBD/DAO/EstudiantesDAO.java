/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.ConexionBD.DAO;

import SistemaVotacion.ConexionBD.Conexion;
import SistemaVotacion.Modelos.Estudiantes;
import SistemaVotacion.Modelos.Grados;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author HP
 */
public class EstudiantesDAO {
    private final Conexion conexion;
    private final GradosDAO gradosDAO;

    public EstudiantesDAO() {
        this.conexion = new Conexion();
        this.gradosDAO = new GradosDAO();
    }

    public boolean saveStudent(Estudiantes student) {
        String sql = "{CALL insertarEstudiante(?, ?, ?, ?)}";
        Connection con = null;
        PreparedStatement stmt = null;

        try {
            con = conexion.getConnection();
            stmt = con.prepareStatement(sql); 

            
            stmt.setString(1, student.getNombre());
            stmt.setString(2, student.getApellido());
            stmt.setString(3, student.getDocumento());

          
            Grados grado = student.getGrado();
            int idGrado = gradosDAO.buscarIdGrado(grado.getNombreGrado());

            if (idGrado == -1) {
                System.out.println("Error: No se encontró el ID del grado.");
                return false; 
            }

            stmt.setInt(4, idGrado); 

            return stmt.executeUpdate() > 0; 

        } catch (SQLException e) {
            System.out.println("Error al guardar estudiante: " + e.getMessage());
            return false; 
        } finally {
     
            try {
                if (stmt != null) {
                    stmt.close(); 
                }
                if (con != null) {
                    con.close();   
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
    }
    

    public Estudiantes buscarPorId(int idEstudiante) {
        String sql = "{CALL buscarEstudiantePorId(?)}";
        try (Connection con = conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("Nombre");
                    String apellido = rs.getString("Apellido");
                    String documento = rs.getString("Documento");
                    int idGrado = rs.getInt("FkGrado");

                    Grados grado = gradosDAO.obtenerGradoPorId(idGrado);
                    Estudiantes estudiante = new Estudiantes(idEstudiante, nombre, documento, apellido);
                    estudiante.AsignarGrado(grado);
                    return estudiante;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar estudiante por ID: " + e.getMessage());
        }
        return null;
    }

    public Estudiantes buscarPorNombreYApellido(String nombre, String apellido) {
        String sql = "{CALL buscarEstudiantePorNombreApellido(?,?)}";
        try (Connection con = conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setString(2, apellido);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return crearEstudianteDesdeResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar estudiante por nombre y apellido: " + e.getMessage());
        }
        return null;
    }

    public List<Estudiantes> obtenerTodosLosEstudiantes() {
        List<Estudiantes> estudiantes = new ArrayList<>();
        String sql = "call obtenerTodosLosEstudiantes()";
        try (Connection con = conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                estudiantes.add(crearEstudianteDesdeResultSet(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener todos los estudiantes: " + e.getMessage());
        }
        return estudiantes;
    }

    private Estudiantes crearEstudianteDesdeResultSet(ResultSet rs) throws SQLException {
        int idEstudiante = rs.getInt("Id_Estudiante");
        String nombre = rs.getString("Nombre");
        String apellido = rs.getString("Apellido");
        String documento = rs.getString("Documento");
        int idGrado = rs.getInt("FkGrado");

        Grados grado = gradosDAO.obtenerGradoPorId(idGrado);
        Estudiantes estudiante = new Estudiantes(idEstudiante, nombre, documento, apellido);
        estudiante.AsignarGrado(grado);
        return estudiante;
    }
    
    public List<Estudiantes> obtenerEstudiantesPorGrado(String nombreGrado) throws SQLException {
        List<Estudiantes> estudiantes = new ArrayList<>();
        String sql = "{CALL obtenerEstudiantesPorGrado(?)}";

        try (Connection con = conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nombreGrado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    estudiantes.add(crearEstudianteDesdeResultSet(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener estudiantes por grado: " + e.getMessage());
        }
        return estudiantes;
    }

    
    public Estudiantes buscarPorDocumento(String documento) {
        Estudiantes estudiante = null;
        String sql = "{CALL buscarEstudiantePorDocumento(?)}";

        try (Connection con = conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, documento);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombre = rs.getString("Nombre");
                    String apellido = rs.getString("Apellido");
                    documento = rs.getString("Documento");

                    estudiante = new Estudiantes(1, nombre, documento, apellido);

                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar estudiante por documento: " + e.getMessage());
        }

        return estudiante;
    }
    
    public int contarEstudiantesGrado11() {
        int contador = 0;
        String sql = "{CALL contarEstudiantesGrado11()}";

        try (Connection con = conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                contador = rs.getInt(1); 
            }

        } catch (SQLException e) {
            System.out.println("Error al contar estudiantes en grado 11: " + e.getMessage());
        }

        return contador;
    }
    
    public int contarTodosEstudiantes() {
    int contador = 0;
    String sql = "{CALL  contarTodosEstudiantes()}";

    try (Connection con = conexion.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        if (rs.next()) {
            contador = rs.getInt(1);
        }

    } catch (SQLException e) {
        System.out.println("Error al contar todos los estudiantes: " + e.getMessage());
    }

    return contador;
}
    
    public int contarEstudiantesGrado(int grado) {
    int contador = 0;
    String sql = "{CALL contarEstudiantesPorGrado(?)}";

    try (Connection con = conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, grado);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                contador = rs.getInt(1);
            }
        }
    } catch (SQLException e) {
        System.out.println("Error al contar estudiantes en grado " + grado + ": " + e.getMessage());
    }

    return contador;
}


}

