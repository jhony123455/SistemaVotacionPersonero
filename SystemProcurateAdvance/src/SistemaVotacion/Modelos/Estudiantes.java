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
public class Estudiantes {
    private int id;
    private String nombre, documento, apellido; 
    private Grados grado;
    public static final ArrayList<Estudiantes> listaEstudiantes = new ArrayList<>();
   

    public Estudiantes(int id,String nombre, String documento, String apellido) {
        this.id = id;
        this.nombre = nombre;
        this.documento = documento;
        this.apellido = apellido;
       
       
    }

    public int getId() {
        return id;
    }
    public String datoContacto () {
      return("Nombre: " + nombre + "\nApellido: " + apellido + "\nDocumento: " + documento + "\nGrado:" + grado);
  }
    
    
    public void AsignarGrado(Grados grado){
        this.grado=grado;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDocumento() {
        return documento;
    }

    public String getApellido() {
        return apellido;
    }

    public Grados getGrado() {
        return grado;
    }
    
    
    
    
    
}
