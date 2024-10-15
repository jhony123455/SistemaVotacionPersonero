/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion.Modelos;



/**
 *
 * @author HP
 */
public class Grados {
    
    private String nombreGrado;
     private int idGrado;
  

    public Grados(int idGrado,String nombreGrado) {
        this.idGrado=idGrado;
        this.nombreGrado = nombreGrado;
    }

    public String getNombreGrado() {
        return nombreGrado;
    }

    public int getIdGrado() {
        return idGrado;
    }
    
   
    
    
}
