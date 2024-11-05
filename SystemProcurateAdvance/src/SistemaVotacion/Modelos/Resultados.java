/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.Modelos;

import java.time.LocalDateTime;
import java.util.List;

public class Resultados {
    private Eleccion eleccion;
    private List<Candidatos> candidatos;
    private int totalVotos;
    private LocalDateTime fechaConteo;

    public Resultados(Eleccion eleccion, List<Candidatos> candidatos, int totalVotos) {
        this.eleccion = eleccion;
        this.candidatos = candidatos;
        this.totalVotos = totalVotos;
        this.fechaConteo = LocalDateTime.now();
    }

    public Resultados(Eleccion eleccion, List<Candidatos> candidatos, int totalVotos, LocalDateTime fechaConteo) {
        this.eleccion = eleccion;
        this.candidatos = candidatos;
        this.totalVotos = totalVotos;
        this.fechaConteo = fechaConteo;
    }

    public Eleccion getEleccion() {
        return eleccion;
    }

    public void setEleccion(Eleccion eleccion) {
        this.eleccion = eleccion;
    }

    public List<Candidatos> getCandidatos() {
        return candidatos;
    }

    public void setCandidatos(List<Candidatos> candidatos) {
        this.candidatos = candidatos;
    }

    public int getTotalVotos() {
        return totalVotos;
    }

    public void setTotalVotos(int totalVotos) {
        this.totalVotos = totalVotos;
    }

    public LocalDateTime getFechaConteo() {
        return fechaConteo;
    }

    public void setFechaConteo(LocalDateTime fechaConteo) {
        this.fechaConteo = fechaConteo;
    }
}
