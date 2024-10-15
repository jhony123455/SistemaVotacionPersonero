/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.Modelos;


/**
 *
 * @author HP
 */
public class Voto {
    private int idvoto;
    private Eleccion eleccion;
    private Candidatos candidatos;
    private Estudiantes estudiantes;

    public Voto( Eleccion eleccion, Candidatos candidatos, Estudiantes estudiantes) {
        this.eleccion = eleccion;
        this.candidatos = candidatos;
        this.estudiantes = estudiantes;
    }

    public int getIdvoto() {
        return idvoto;
    }

    public Eleccion getEleccion() {
        return eleccion;
    }

    public Candidatos getCandidatos() {
        return candidatos;
    }

    public Estudiantes getEstudiantes() {
        return estudiantes;
    }
    
    
    
}
