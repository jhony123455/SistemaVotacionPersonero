/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login;

import SistemaVotacion.ConexionBD.DAO.UsuarioDAO;

/**
 *
 * @author joiser
 */
public class UsuarioLogic {

    private static UsuarioDAO usuariodao = new UsuarioDAO();

    public static boolean autentificar(String user, String pass) {

        Usuario usuario = usuariodao.iniciarSesion(user, pass);

        return usuario != null;
    }

    public static boolean insertar(Usuario usuario) {
        boolean existe = usuariodao.existeUsuario(usuario.getUser());
        System.out.println("¿Existe el usuario?: " + existe);
        if (existe) {
            return false; 
        }
        return usuariodao.insertar(usuario);
    }


   /* public static boolean modificar(Usuario usuario) {
        return usuariodao.modificar(usuario);
    }

    public static boolean eliminar(String usuario) {
        return usuariodao.eliminar(usuario);
    }*/

}