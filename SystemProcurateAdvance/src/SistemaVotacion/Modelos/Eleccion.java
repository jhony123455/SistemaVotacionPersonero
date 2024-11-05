/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.Modelos;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 *
 * @author HP
 */
public class Eleccion {
   
    private int ideleccion;
    private LocalDateTime fechainicio;
    private LocalDateTime fechafin;

    public Eleccion(int ideleccion, LocalDateTime fechainicio, LocalDateTime fechafin) {
        this.ideleccion = ideleccion;
        this.fechainicio = fechainicio;
        this.fechafin = fechafin;
    }

    

    public int getIdeleccion() {
        return ideleccion;
    }

    public LocalDateTime getFechainicio() {
        return fechainicio;
    }

    public LocalDateTime getFechafin() {
        return fechafin;
    }

    public void setIdeleccion(int ideleccion) {
        this.ideleccion = ideleccion;
    }

    public void setFechainicio(LocalDateTime fechainicio) {
        this.fechainicio = fechainicio;
    }

    public void setFechafin(LocalDateTime fechafin) {
        this.fechafin = fechafin;
    }
    
   
    
    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "ID: " + ideleccion + " - Inicio: " + fechainicio.format(formatter) + " - Fin: " + fechafin.format(formatter);
    }

}
