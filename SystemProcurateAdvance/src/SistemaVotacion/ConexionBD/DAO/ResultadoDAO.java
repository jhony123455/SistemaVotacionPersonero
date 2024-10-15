
package SistemaVotacion.ConexionBD.DAO;

import SistemaVotacion.ConexionBD.Conexion;
import SistemaVotacion.Modelos.Candidatos;
import SistemaVotacion.Modelos.Eleccion;
import SistemaVotacion.Modelos.Estudiantes;
import SistemaVotacion.Modelos.Resultados;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author HP
 */
public class ResultadoDAO {
    private Conexion conexion; 

    public ResultadoDAO() {
        this.conexion = new Conexion(); 
    }

    public List<Resultados> obtenerResultadosPorCandidato(int eleccionId) throws SQLException {
        List<Resultados> resultados = new ArrayList<>();
        String sql = "{ CALL ObtenerResultadosPorCandidato(?) }";
        try (Connection conn = conexion.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, eleccionId);
            ResultSet rs = cs.executeQuery();
            List<Candidatos> listaCandidatos = new ArrayList<>();
            while (rs.next()) {
                int idCandidato = rs.getInt("ID_Candidato");
                String nombre = rs.getString("Nombre");
                String apellido = rs.getString("Apellido");
                int votosObtenidos = rs.getInt("Votos_Obtenidos");

                Estudiantes estudiante;
                Candidatos candidato;

                if (idCandidato == 0) {
                    // Caso especial para votos nulos
                    estudiante = new Estudiantes(0, "Voto en Blanco", "", "");
                    candidato = new Candidatos(0, estudiante, "");
                } else {
                    estudiante = new Estudiantes(idCandidato, nombre, "", apellido);
                    candidato = new Candidatos(idCandidato, estudiante, "");
                }

                candidato.setVotosObtenidos(votosObtenidos);
                listaCandidatos.add(candidato);
            }
            LocalDateTime fechaInicio = LocalDateTime.now();
            LocalDateTime fechaFin = LocalDateTime.now();
            Eleccion eleccion = new Eleccion(eleccionId, fechaInicio, fechaFin);
            Resultados resultadoFinal = new Resultados(eleccion, listaCandidatos, 0);
            resultados.add(resultadoFinal);
        }
        return resultados;
    }

    public List<Resultados> obtenerResultadosPorGrado(int eleccionId, int gradoId) throws SQLException {
        List<Resultados> resultados = new ArrayList<>();
        String sql = "{ CALL ObtenerResultadosPorGrado(?, ?) }";
        try (Connection conn = conexion.getConnection(); CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, eleccionId);
            cs.setInt(2, gradoId);
            ResultSet rs = cs.executeQuery();
            List<Candidatos> listaCandidatos = new ArrayList<>();
            while (rs.next()) {
                int idCandidato = rs.getInt("ID_Candidato");
                String nombre = rs.getString("Nombre");
                String apellido = rs.getString("Apellido");
                String grado = rs.getString("Grado");
                int votosObtenidos = rs.getInt("Votos_Obtenidos");

                Estudiantes estudiante;
                Candidatos candidato;

                if (idCandidato == 0) {
                    // Caso especial para votos nulos
                    estudiante = new Estudiantes(0, "Voto en Blanco", "", "");
                    candidato = new Candidatos(0, estudiante, "");
                } else {
                    estudiante = new Estudiantes(idCandidato, nombre, "", apellido);
                    candidato = new Candidatos(idCandidato, estudiante, "");
                }

                candidato.setVotosObtenidos(votosObtenidos);

                listaCandidatos.add(candidato);
            }
            LocalDateTime fechaInicio = LocalDateTime.now();
            LocalDateTime fechaFin = LocalDateTime.now();
            Eleccion eleccion = new Eleccion(eleccionId, fechaInicio, fechaFin);
            Resultados resultadoFinal = new Resultados(eleccion, listaCandidatos, 0);
            resultados.add(resultadoFinal);
        }
        return resultados;
    }

    public void llenarResultados(int eleccionId) throws SQLException {
        try (Connection con = conexion.getConnection(); CallableStatement cs = con.prepareCall("{ CALL LlenarResultados(?) }")) {
            cs.setInt(1, eleccionId);
            cs.execute();
            System.out.println("Resultados llenados correctamente para la elección ID: " + eleccionId);
        }
    }

   public List<Resultados> obtenerResultadosTotales(int eleccionId) throws SQLException {
    List<Resultados> resultados = new ArrayList<>();
    String sql = "{ CALL ObtenerResultadosTotales(?) }";
    try (Connection conn = conexion.getConnection();
         CallableStatement cs = conn.prepareCall(sql)) {
        cs.setInt(1, eleccionId);
        try (ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                int totalVotos = rs.getInt("Total_Votos");
                LocalDateTime fechaConteo = rs.getTimestamp("Fecha_Conteo").toLocalDateTime();
                Eleccion eleccion = new Eleccion(eleccionId, fechaConteo, fechaConteo);
                Resultados resultado = new Resultados(eleccion, new ArrayList<>(), totalVotos, fechaConteo);
                resultado.setFechaConteo(fechaConteo); 
                resultados.add(resultado);
            }
        }
    }
    return resultados;
}

}
