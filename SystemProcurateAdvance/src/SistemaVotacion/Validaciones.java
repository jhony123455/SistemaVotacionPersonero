/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion;

import SistemaVotacion.ConexionBD.DAO.EstudiantesDAO;

/**
 *
 * @author HP
 */
  public class Validaciones {

    class ValidacionException extends Exception {

        public ValidacionException(String message) {
            super(message);
        }
    }
    private EstudiantesDAO estudiantesdao;

    public Validaciones() {
        this.estudiantesdao = new EstudiantesDAO();
    }

    public boolean esNombreValido(String nombre) {
        return nombre != null && nombre.length() >= 3 && nombre.matches("^[a-zA-ZñÑ ]+$");
    }

    public boolean esDocumentoValido(String documento) {
        return documento != null && documento.matches("\\d{10}") && !esSecuenciaRepetitiva(documento);
    }

    public boolean esDocumentoUnico(String documento) {
        return estudiantesdao.buscarPorDocumento(documento) == null;
    }

    public boolean esNombreUnico(String nombre, String apellido) {
        return estudiantesdao.buscarPorNombreYApellido(nombre, apellido) == null;
    }

    private boolean esSecuenciaRepetitiva(String documento) {
        char primerDigito = documento.charAt(0);
        for (int i = 1; i < documento.length(); i++) {
            if (documento.charAt(i) != primerDigito) {
                return false;
            }
        }
        return true;
    }

    public void validarDatos(String nombre, String apellido, String documento, String gradoSeleccionado) throws ValidacionException {
        if (nombre.isEmpty() || apellido.isEmpty() || documento.isEmpty() || gradoSeleccionado.equals("Selecciona")) {
            throw new ValidacionException("Datos Incompletos o grado no seleccionado");
        }
        if (!esNombreValido(nombre)) {
            throw new ValidacionException("El nombre debe tener al menos 3 caracteres y solo puede contener letras.");
        }
        if (!esNombreValido(apellido)) {
            throw new ValidacionException("El apellido debe tener al menos 3 caracteres y solo puede contener letras.");
        }
        if (!esDocumentoValido(documento)) {
            throw new ValidacionException("El documento debe contener exactamente 10 dígitos numéricos y no puede tener un solo numero.");
        }
        if (!esDocumentoUnico(documento)) {
            throw new ValidacionException("Este número de documento ya está registrado.");
        }

        if (!esNombreUnico(nombre, apellido)) {
            throw new ValidacionException("Este nombre ya está registrado.");
        }

        
    }

}

