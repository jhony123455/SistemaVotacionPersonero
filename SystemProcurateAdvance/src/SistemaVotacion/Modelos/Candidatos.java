/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.Modelos;

import java.util.ArrayList;

/**
 *
 * @author HP
 */
public class Candidatos {
  private Estudiantes estudiante;
  private String propuesta;
  private int idCandidato;
  private int votosObtenidos;

    public Candidatos(int idCandidato, Estudiantes estudiante, String propuesta) {
        this.idCandidato=idCandidato;
        this.estudiante = estudiante;
        this.propuesta = propuesta;
        this.votosObtenidos = 0;
    }

    public int getIdCandidato() {
        return idCandidato;
    }
     public static final ArrayList<Candidatos> listaCandidatos = new ArrayList<>();


    public Estudiantes getEstudiante() {
        return estudiante;
    }

    public String getPropuesta() {
        return propuesta;
    }

    public void setPropuesta(String propuesta) {
        this.propuesta = propuesta;
    }
    public int getVotosObtenidos() {
        return votosObtenidos;
    }

    public void setVotosObtenidos(int votosObtenidos) {
        this.votosObtenidos = votosObtenidos;
    }
  
    @Override
    public String toString() {
        return estudiante.getNombre() + " " + estudiante.getApellido();
    }
    
}
