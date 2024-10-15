/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package login;

/**
 *
 * @author joiser
 */
public class Usuario {
    private int idUsuario;
    private String user;
    private String pass;
    private int idRol;
    private boolean estado;

    public Usuario(String user, String pass) {
        this.user = user;
        this.pass = pass;
    }

    
    public Usuario(int idUsuario, String user, String pass, int idRol, boolean estado) {
        this.idUsuario = idUsuario;
        this.user = user;
        this.pass = pass;
        this.idRol = idRol;
        this.estado = estado;
    }

    
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}