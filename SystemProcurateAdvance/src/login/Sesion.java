/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login;

/**
 *
 * @author HP
 */
public class Sesion {

    public Sesion() {
    }

    
    public static Usuario usuarioActual = null;

    
    public static void iniciarSesion(Usuario usuario) {
        usuarioActual = usuario;  
    }

  
    public static void cerrarSesion() {
        usuarioActual = null;  
    }

    
    public static boolean haySesionActiva() {
        return usuarioActual != null;
    }

    
    public static boolean esAdmin() {
        return usuarioActual != null && usuarioActual.getIdRol() == 1;
    }
}

