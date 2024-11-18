/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.ConexionBD.DAO;


import SistemaVotacion.ConexionBD.Conexion;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import login.Sesion;
import login.Usuario;

/**
 *
 * @author joiser
 */
public class UsuarioDAO {

    private Conexion conexion;

    public UsuarioDAO() {
        this.conexion = new Conexion();
    }

    public Usuario iniciarSesion(String user, String pass) {
        String sql = "call iniciarSesion(?,?)";
        Usuario usuario = null;

        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user);
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    int idUsuario = rs.getInt("Id_Usuario");
                    String nombreUsuario = rs.getString("Nombre_Usuario");
                    String contrasena = rs.getString("Contrasena");
                    int idRol = rs.getInt("Id_Rol");
                    boolean estado = rs.getBoolean("Estado");

                    if (estado) {

                        usuario = new Usuario(idUsuario, nombreUsuario, contrasena, idRol, estado);
                    } else {
                        System.out.println("El usuario está deshabilitado.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al intentar iniciar sesión: " + e.getMessage());
        }

        return usuario;
    }

    public boolean insertar(Usuario usuario) {
        Connection conn = null;
        CallableStatement cs = null;
        boolean resultado = false;
        try {
            conn = conexion.getConnection();
            String sql = "CALL registrarUsuario(?, ?)";
            cs = conn.prepareCall(sql);
            cs.setString(1, usuario.getUser());
            cs.setString(2, usuario.getPass());
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                String mensaje = rs.getString(1);
                System.out.println("Mensaje del procedimiento: " + mensaje);
                resultado = "Usuario registrado exitosamente".equals(mensaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cs != null) {
                try {
                    cs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultado;
    }



    
    public Usuario autenticar(String user, String pass) {
        String sql = "call  autenticarUsuario(?, ?)";
        Usuario usuario = null;

        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user);
            ps.setString(2, pass);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreUsuario = rs.getString("Nombre_Usuario");
                    String contrasenaBD = rs.getString("Contrasena");
                    int idRol = rs.getInt("Id_Rol");
                    boolean estado = rs.getBoolean("Estado");

                    if (pass.equals(contrasenaBD)) {

                        if (estado) {
                            int idUsuario = rs.getInt("Id_Usuario");

                            usuario = new Usuario(idUsuario, nombreUsuario, contrasenaBD, idRol, estado);

                            Sesion.iniciarSesion(usuario);
                        } else {
                            System.out.println("El usuario está deshabilitado.");
                        }
                    } else {
                        System.out.println("Contraseña incorrecta.");
                    }
                } else {
                    System.out.println("Usuario no encontrado.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al intentar autenticar: " + e.getMessage());
        }

        return usuario;
    }
   
    public boolean desactivarUsuario(String usuario) {
        Connection conn = null;
        CallableStatement cs = null;
        boolean resultado = false;
        try {
            conn = conexion.getConnection();
            String sql = "CALL desactivarUsuarioProc(?)";
            cs = conn.prepareCall(sql);
            cs.setString(1, usuario);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                String mensaje = rs.getString(1);
                System.out.println("Mensaje del procedimiento: " + mensaje);
                resultado = "Usuario desactivado exitosamente".equals(mensaje);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (cs != null) {
                try {
                    cs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return resultado;
    }



     
     public List<Usuario> obtenerUsuariosRol2() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "call obtenerUsuariosRol2()";
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Usuario usuario = new Usuario(
                        rs.getInt("Id_Usuario"),
                        rs.getString("Nombre_Usuario"),
                        rs.getString("Contrasena"),
                        rs.getInt("Id_Rol"),
                        rs.getBoolean("Estado")
                );
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

     
     public boolean existeUsuario(String nombreUsuario) {
        String sql = "call existeUsuario(?,?)";
        try (Connection conn = conexion.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombreUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }




  
}