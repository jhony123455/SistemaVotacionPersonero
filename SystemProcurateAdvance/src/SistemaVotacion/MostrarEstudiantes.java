package SistemaVotacion;

import SistemaVotacion.Modelos.Grados;
import SistemaVotacion.Modelos.Estudiantes;
import SistemaVotacion.Modelos.Candidatos;
import SistemaVotacion.ConexionBD.DAO.CandidatosDAO;
import SistemaVotacion.ConexionBD.DAO.EstudiantesDAO;
import SistemaVotacion.ConexionBD.DAO.GradosDAO;
import SistemaVotacion.ConexionBD.DAO.UsuarioDAO;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import login.Sesion;
import login.Usuario;


/**
 *
 * @author HP
 */
public class MostrarEstudiantes extends javax.swing.JFrame {

   
    String [] encabezado = {"Nombre", "Apellido", "Documento", "Grado"};
    String [] encabezado2 = {"Candidato", "Propuesta"};
    private final EstudiantesDAO estudiantesdao;
    private final GradosDAO gradosdao;
    private final CandidatosDAO candidatosdao;

    public MostrarEstudiantes() {
        initComponents();
        setContentPane(PanelMostrar);
        pack();
        estudiantesdao = new EstudiantesDAO();
        gradosdao= new GradosDAO();
        candidatosdao= new CandidatosDAO();
        llenarComboGrados();
        try {
            FiltroCandidatos();
        } catch (SQLException ex) {
            Logger.getLogger(MostrarEstudiantes.class.getName()).log(Level.SEVERE, null, ex);
        }

      
    }
    public void llenarComboGrados() {
        ComboEstudiantes.removeAllItems();
        ComboEstudiantes.addItem("Selecciona");

        List<Grados> grados = gradosdao.obtenerGrados();
        for (Grados grado : grados) {
            ComboEstudiantes.addItem(grado.getNombreGrado());
        }
    }
 
    public void Filtro() throws SQLException {
        String gradoSeleccionado = (String) ComboEstudiantes.getSelectedItem();

        if (gradoSeleccionado == null || gradoSeleccionado.equals("Selecciona")) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un grado válido.");
            return;
        }

        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(encabezado);
        List<Estudiantes> estudiantes = estudiantesdao.obtenerEstudiantesPorGrado(gradoSeleccionado);
        if (estudiantes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No se encontraron estudiantes en el grado " + gradoSeleccionado);
        } else {
            for (Estudiantes estudiante : estudiantes) {
                model.addRow(new Object[]{
                    estudiante.getNombre(),
                    estudiante.getApellido(),
                    estudiante.getDocumento(),
                    estudiante.getGrado().getNombreGrado()
                });
            }
        }
        TableEstudiantes.setModel(model);

        if (gradoSeleccionado.equals("11º")) {
            FiltroCandidatos();
        } else {
            DefaultTableModel emptyModel = new DefaultTableModel();
            emptyModel.setColumnIdentifiers(encabezado2);
            TableCandidatos.setModel(emptyModel);
        }

    }

    public void FiltroCandidatos() throws SQLException {
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(encabezado2);

        List<Candidatos> candidatos = candidatosdao.obtenerCandidatosActivos();

        for (Candidatos candidato : candidatos) {
            String nombreCompleto = candidato.getEstudiante().getNombre() + " " + candidato.getEstudiante().getApellido();
            String propuesta = candidato.getPropuesta();
            if (!propuesta.trim().isEmpty()) {
                model.addRow(new Object[]{nombreCompleto, propuesta});
            }
        }

        TableCandidatos.setModel(model);
    }
    
    public static void ExportarTablasAExcel(JTable tablaEstudiantes, JTable tablaCandidatos, String rutaBase) throws WriteException {
        try {
            EstudiantesDAO estudiantesDAO = new EstudiantesDAO();
            List<Estudiantes> todosEstudiantes = estudiantesDAO.obtenerTodosLosEstudiantes();

            CandidatosDAO candidatosDAO = new CandidatosDAO();
            List<Candidatos> todosCandidatos = candidatosDAO.obtenerTodosLosCandidatos();

            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String rutaProyecto = System.getProperty("user.dir");
            String rutaCompleta = rutaProyecto + File.separator + rutaBase + "_" + timestamp + ".xls";

            WritableWorkbook workbook = jxl.Workbook.createWorkbook(new File(rutaCompleta));

            // Hoja para estudiantes
            WritableSheet sheetEstudiantes = workbook.createSheet("Estudiantes", 0);
            String[] encabezadoEstudiantes = {"Nombre", "Apellido", "Documento", "Grado"};
            for (int i = 0; i < encabezadoEstudiantes.length; i++) {
                Label label = new Label(i, 0, encabezadoEstudiantes[i]);
                sheetEstudiantes.addCell(label);
            }
            int rowEstudiantes = 1;
            for (Estudiantes estudiante : todosEstudiantes) {
                sheetEstudiantes.addCell(new Label(0, rowEstudiantes, estudiante.getNombre()));
                sheetEstudiantes.addCell(new Label(1, rowEstudiantes, estudiante.getApellido()));
                sheetEstudiantes.addCell(new Label(2, rowEstudiantes, estudiante.getDocumento()));
                sheetEstudiantes.addCell(new Label(3, rowEstudiantes, estudiante.getGrado().getNombreGrado()));
                rowEstudiantes++;
            }

            // Hoja para candidatos
            WritableSheet sheetCandidatos = workbook.createSheet("Candidatos", 1);
            String[] encabezadoCandidatos = {"Nombre Completo", "Propuesta"};
            for (int i = 0; i < encabezadoCandidatos.length; i++) {
                Label label = new Label(i, 0, encabezadoCandidatos[i]);
                sheetCandidatos.addCell(label);
            }
            int rowCandidatos = 1;
            for (Candidatos candidato : todosCandidatos) {
                String nombreCompleto = candidato.getEstudiante().getNombre() + " " + candidato.getEstudiante().getApellido();
                String propuesta = candidato.getPropuesta();
                sheetCandidatos.addCell(new Label(0, rowCandidatos, nombreCompleto));
                sheetCandidatos.addCell(new Label(1, rowCandidatos, propuesta));
                rowCandidatos++;
            }

            workbook.write();
            workbook.close();
            JOptionPane.showMessageDialog(null, "Informes exportados", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loginParaExportarTablas(JTable tablaEstudiantes, JTable tablaCandidatos, String rutaBase) {
        JTextField usr = new JTextField();
        int actionLogin = JOptionPane.showConfirmDialog(null, usr, "Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (actionLogin > 0) {
            JOptionPane.showMessageDialog(null, "Operación cancelada");
        } else {
            JPasswordField pwd = new JPasswordField();
            int action = JOptionPane.showConfirmDialog(null, pwd, "Contraseña", JOptionPane.OK_CANCEL_OPTION);
            if (action > 0) {
                JOptionPane.showMessageDialog(null, "Operación cancelada");
            } else {
                String usuario = usr.getText();
                String pass = new String(pwd.getPassword());
                UsuarioDAO usuarioDAO = new UsuarioDAO();
                Usuario user = usuarioDAO.autenticar(usuario, pass);  

                if (user != null && Sesion.esAdmin()) {
                    try {
                        ExportarTablasAExcel(tablaEstudiantes, tablaCandidatos, rutaBase);
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No eres el Admin, no puedes exportar", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }


    public void verificarYExportar(JTable tablaEstudiantes, JTable tablaCandidatos, String rutaBase) {
       
        if (Sesion.haySesionActiva()) {
            if (Sesion.esAdmin()) {
                
                try {
                    ExportarTablasAExcel(tablaEstudiantes, tablaCandidatos, rutaBase);
                    return;
                } catch (WriteException e) {
                    e.printStackTrace();
                }
            } else {
                
                loginParaExportarTablas(tablaEstudiantes, tablaCandidatos, rutaBase);
            }
        } else {
           
            loginParaExportarTablas(tablaEstudiantes, tablaCandidatos, rutaBase);
        }
    }





    
    
      public JPanel getPanel() {
        return PanelMostrar;
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelMostrar = new javax.swing.JPanel();
        ComboEstudiantes = new javax.swing.JComboBox<>();
        BtSearch = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableEstudiantes = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        TableCandidatos = new javax.swing.JTable();
        BtExportar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        ComboEstudiantes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        BtSearch.setText("Buscar");
        BtSearch.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtSearchActionPerformed(evt);
            }
        });

        TableEstudiantes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(TableEstudiantes);

        TableCandidatos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(TableCandidatos);

        BtExportar.setText("Exportar");
        BtExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtExportarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelMostrarLayout = new javax.swing.GroupLayout(PanelMostrar);
        PanelMostrar.setLayout(PanelMostrarLayout);
        PanelMostrarLayout.setHorizontalGroup(
            PanelMostrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMostrarLayout.createSequentialGroup()
                .addGroup(PanelMostrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelMostrarLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(ComboEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelMostrarLayout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addGroup(PanelMostrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(BtSearch, javax.swing.GroupLayout.DEFAULT_SIZE, 66, Short.MAX_VALUE)
                            .addComponent(BtExportar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addGap(24, 24, 24)
                .addGroup(PanelMostrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE))
                .addGap(66, 66, 66))
        );
        PanelMostrarLayout.setVerticalGroup(
            PanelMostrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelMostrarLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(PanelMostrarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelMostrarLayout.createSequentialGroup()
                        .addComponent(ComboEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(53, 53, 53)
                        .addComponent(BtSearch)
                        .addGap(18, 18, 18)
                        .addComponent(BtExportar)))
                .addGap(36, 36, 36)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(164, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelMostrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelMostrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtSearchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtSearchActionPerformed
        try {
           
            Filtro();
        } catch (SQLException ex) {
            Logger.getLogger(MostrarEstudiantes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_BtSearchActionPerformed

    private void BtExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtExportarActionPerformed
        // TODO add your handling code here:
        verificarYExportar(TableEstudiantes, TableCandidatos,"Tablas");
        


    }//GEN-LAST:event_BtExportarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MostrarEstudiantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MostrarEstudiantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MostrarEstudiantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MostrarEstudiantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MostrarEstudiantes().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtExportar;
    private javax.swing.JButton BtSearch;
    private javax.swing.JComboBox<String> ComboEstudiantes;
    private javax.swing.JPanel PanelMostrar;
    private javax.swing.JTable TableCandidatos;
    private javax.swing.JTable TableEstudiantes;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables
}
