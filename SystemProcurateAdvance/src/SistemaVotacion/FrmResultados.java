
package SistemaVotacion;


import SistemaVotacion.ConexionBD.DAO.EleccionDAO;
import SistemaVotacion.ConexionBD.DAO.GradosDAO;
import SistemaVotacion.ConexionBD.DAO.ResultadoDAO;
import SistemaVotacion.ConexionBD.DAO.UsuarioDAO;
import SistemaVotacion.Modelos.Candidatos;
import SistemaVotacion.Modelos.Eleccion;
import SistemaVotacion.Modelos.Grados;
import SistemaVotacion.Modelos.Resultados;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
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
import login.Usuario;


public class FrmResultados extends javax.swing.JFrame {
    
  String [] encabezado = {"Nombre", "Apellido", "Votos Obtenidos"};  
  String[] encabezado2 = {"NºEleccion", "Total_Votos", "Fecha_Conteo"};
    private FrmPrincipal frmPrincipal;
   GradosDAO gradosdao;
    public FrmResultados(FrmPrincipal principal) {
        initComponents();
        this.frmPrincipal = principal;
        gradosdao = new GradosDAO();
        cargarElecciones();
        llenarComboGrados();
        CbGrados.setVisible(false);
    
       actualizarEstadoBotonResultados(); 
        
    }
    private void llenarComboGrados() {
        CbGrados.removeAllItems();
        CbGrados.addItem("Selecciona");
        List<Grados> grados = gradosdao.obtenerGrados();
        for (Grados grado : grados) {
            CbGrados.addItem(grado.getNombreGrado());
        }
        if (CbGrados.getItemCount() > 1) {
            CbGrados.setSelectedIndex(1);
        }
    }
    
    
    private void actualizarEstadoBotonResultados() {
        try {
            EleccionDAO eleccionDAO = new EleccionDAO();
            boolean hayVotos = eleccionDAO.hayVotosRegistrados();
            frmPrincipal.actualizarEstadoBotonResultados(hayVotos);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al verificar los votos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    

    private void cargarElecciones() {
    try {
        EleccionDAO eleccionDAO = new EleccionDAO();
        List<Eleccion> elecciones = eleccionDAO.obtenerElecciones();
        CbElecciones.removeAllItems();
        for (Eleccion eleccion : elecciones) {
            CbElecciones.addItem("ID: " + eleccion.getIdeleccion() + " - Inicio: " + eleccion.getFechainicio().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        }
        boolean hayElecciones = !elecciones.isEmpty();
        BtTotal.setEnabled(hayElecciones);
        BtPorGrado.setEnabled(hayElecciones);
        BtExportar.setEnabled(hayElecciones);
        if (!hayElecciones) {
            System.out.println( "No hay elecciones disponibles");
        }
        actualizarEstadoBotonResultados();  
    } catch (SQLException e) {
        JOptionPane.showMessageDialog(this, "Error al cargar las elecciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}


    
    private void cargarResultadosPorCandidato(int eleccionId) {
        try {
            ResultadoDAO resultadosdao = new ResultadoDAO();
            List<Resultados> resultados = resultadosdao.obtenerResultadosPorCandidato(eleccionId);
            DefaultTableModel model = new DefaultTableModel(encabezado, 0);

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay resultados disponibles para esta elección", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            for (Resultados resultado : resultados) {
                for (Candidatos candidato : resultado.getCandidatos()) {
                    String nombre = "Voto Nulo".equals(candidato.getEstudiante().getNombre()) ? "Voto Nulo" : candidato.getEstudiante().getNombre();
                    String apellido = candidato.getEstudiante().getApellido();
                    int votosObtenidos = candidato.getVotosObtenidos();
                    model.addRow(new Object[]{nombre, apellido, votosObtenidos});
                }
            }

            TableResultados.setModel(model);
            TableResultados.setVisible(true);
            jScrollPane1.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los resultados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    
    
    
    
    private void cargarResultadosPorGrado(int eleccionId, int gradoId) throws SQLException {
        ResultadoDAO resultadosdao = new ResultadoDAO();
        List<Resultados> resultados = resultadosdao.obtenerResultadosPorGrado(eleccionId, gradoId);
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(encabezado);
        for (Resultados resultado : resultados) {
            for (Candidatos candidato : resultado.getCandidatos()) {
                String nombre = "Voto Nulo".equals(candidato.getEstudiante().getNombre()) ? "Voto Nulo" : candidato.getEstudiante().getNombre();
                String apellido = candidato.getEstudiante().getApellido();
                int votosObtenidos = candidato.getVotosObtenidos();
                model.addRow(new Object[]{
                    nombre,
                    apellido,
                    votosObtenidos
                });
            }
        }
        TableResultados.setModel(model);
        TableResultados.setVisible(true);
    }




    private void cargarResultadosTotales(int eleccionId) {
        try {
            ResultadoDAO resultadosdao = new ResultadoDAO();
            resultadosdao.llenarResultados(eleccionId);

            List<Resultados> resultados = resultadosdao.obtenerResultadosTotales(eleccionId);
            DefaultTableModel model = new DefaultTableModel(encabezado2, 0);

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay resultados disponibles para esta elección", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

           
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Resultados resultado : resultados) {
                model.addRow(new Object[]{
                    resultado.getEleccion().getIdeleccion(),
                    resultado.getTotalVotos(),
                    resultado.getFechaConteo().format(formatter)
                });
            }

            TableResultados.setModel(model);
            TableResultados.setVisible(true);
            jScrollPane1.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los resultados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    private int extraerIdEleccion(String eleccionSeleccionada) {
        String[] partes = eleccionSeleccionada.split(" - ");
        return Integer.parseInt(partes[0].replace("ID: ", ""));
    }

    private void manejarError(String mensaje, Exception ex) {
        Logger.getLogger(FrmResultados.class.getName()).log(Level.SEVERE, mensaje, ex);
        JOptionPane.showMessageDialog(this, mensaje + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void mostrarMensaje(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    


public static void ExportToExcel(JTable tabla, String rutaBase, String[] encabezado) throws WriteException {
    try {
       
        String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String rutaProyecto = System.getProperty("user.dir");
        String rutaCompleta = rutaProyecto + File.separator + rutaBase + "_" + timestamp + ".xls";
        
        WritableWorkbook workbook = jxl.Workbook.createWorkbook(new File(rutaCompleta));
        WritableSheet sheet = workbook.createSheet("Hoja 1", 0);

        // Escribir encabezados
        for (int i = 0; i < encabezado.length; i++) {
            Label label = new Label(i, 0, encabezado[i]);
            sheet.addCell(label);
        }

        // Escribir datos de la tabla
        for (int row = 0; row < tabla.getRowCount(); row++) {
            for (int col = 0; col < tabla.getColumnCount(); col++) {
                Object value = tabla.getValueAt(row, col);
                Label label = new Label(col, row + 1, value != null ? value.toString() : "");
                sheet.addCell(label);
            }
        }

        workbook.write();
        workbook.close();
        JOptionPane.showMessageDialog(null, "Informe exportado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null, "Error al guardar", "Error", JOptionPane.ERROR_MESSAGE);
    }
}

    
    
    public void login(JTable tabla, String ruta, String[] encabezado) {
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
                Usuario user = usuarioDAO.autentificar(usuario, pass);
                if (user != null && user.getIdRol() == 1) { 
                    try {
                        ExportToExcel(tabla, ruta, encabezado);
                    } catch (WriteException e) {
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No eres el Admin, no puedes exportar", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    
      public JPanel getPanel() {
        return PanelResult;
    }





    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelResult = new javax.swing.JPanel();
        CbElecciones = new javax.swing.JComboBox<>();
        BtTotal = new javax.swing.JButton();
        BtPorGrado = new javax.swing.JButton();
        BtGeneral = new javax.swing.JButton();
        BtExportar = new javax.swing.JButton();
        CbGrados = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        TableResultados = new javax.swing.JTable();
        BtCargar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        CbElecciones.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        BtTotal.setText("Votos por Candidato");
        BtTotal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtTotalActionPerformed(evt);
            }
        });

        BtPorGrado.setText("Por Grado");
        BtPorGrado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtPorGradoActionPerformed(evt);
            }
        });

        BtGeneral.setText("En General");
        BtGeneral.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtGeneralActionPerformed(evt);
            }
        });

        BtExportar.setText("Exportar");
        BtExportar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtExportarActionPerformed(evt);
            }
        });

        CbGrados.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CbGrados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CbGradosActionPerformed(evt);
            }
        });

        TableResultados.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(TableResultados);

        BtCargar.setText("Cargar Elecciones");
        BtCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtCargarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelResultLayout = new javax.swing.GroupLayout(PanelResult);
        PanelResult.setLayout(PanelResultLayout);
        PanelResultLayout.setHorizontalGroup(
            PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelResultLayout.createSequentialGroup()
                .addGroup(PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelResultLayout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BtGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BtPorGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BtExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CbGrados, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(PanelResultLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(CbElecciones, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelResultLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(BtCargar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 159, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PanelResultLayout.setVerticalGroup(
            PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelResultLayout.createSequentialGroup()
                .addGroup(PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelResultLayout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(BtCargar)
                        .addGap(18, 18, 18)
                        .addComponent(CbElecciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(29, 29, 29)
                        .addComponent(BtTotal)
                        .addGap(18, 18, 18)
                        .addComponent(BtPorGrado)
                        .addGap(18, 18, 18)
                        .addComponent(BtGeneral)
                        .addGap(18, 18, 18)
                        .addComponent(BtExportar)
                        .addGap(18, 18, 18)
                        .addComponent(CbGrados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelResultLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(38, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelResult, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelResult, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtTotalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtTotalActionPerformed
        // TODO add your handling code here:
          String eleccionSeleccionada = (String) CbElecciones.getSelectedItem();
    if (eleccionSeleccionada != null && !eleccionSeleccionada.equals("Selecciona")) {
        try {
            int eleccionId = extraerIdEleccion(eleccionSeleccionada);
            cargarResultadosPorCandidato(eleccionId);
            CbGrados.setVisible(false);
            jScrollPane1.setVisible(true);
        } catch (NumberFormatException ex) {
            manejarError("Error al procesar el ID de la elección", ex);
        }
    } else {
        mostrarMensaje("Por favor, seleccione una elección válida.");
    }

       
    }//GEN-LAST:event_BtTotalActionPerformed

    private void BtPorGradoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtPorGradoActionPerformed
        String eleccionSeleccionada = (String) CbElecciones.getSelectedItem();
        String gradoSeleccionado = (String) CbGrados.getSelectedItem();
        if (eleccionSeleccionada != null && !eleccionSeleccionada.equals("Selecciona")
                && gradoSeleccionado != null && !gradoSeleccionado.equals("Selecciona")) {
            try {
                int eleccionId = extraerIdEleccion(eleccionSeleccionada);
                int gradoId = gradosdao.buscarIdGrado(gradoSeleccionado);
                cargarResultadosPorGrado(eleccionId, gradoId);
                jScrollPane1.setVisible(true);
                CbGrados.setVisible(true);
            } catch (SQLException ex) {
                manejarError("Error al cargar resultados por grado", ex);
            } catch (NumberFormatException ex) {
                manejarError("Error al procesar el ID de la elección o grado", ex);
            }
        } else {
            mostrarMensaje("Por favor, seleccione una elección y un grado válidos.");
        }
    }//GEN-LAST:event_BtPorGradoActionPerformed

    private void BtGeneralActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtGeneralActionPerformed
        // TODO add your handling code here:
      String eleccionSeleccionada = (String) CbElecciones.getSelectedItem();
        if (eleccionSeleccionada != null && !eleccionSeleccionada.equals("Selecciona")) {
            try {
                int eleccionId = extraerIdEleccion(eleccionSeleccionada);
                cargarResultadosTotales(eleccionId);
                CbGrados.setVisible(false);
                jScrollPane1.setVisible(true);
            } catch (NumberFormatException ex) {
                manejarError("Error al procesar el ID de la elección", ex);
            }
        } else {
            mostrarMensaje("Por favor, seleccione una elección válida.");
        }

    }//GEN-LAST:event_BtGeneralActionPerformed

    private void CbGradosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CbGradosActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CbGradosActionPerformed

    private void BtExportarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtExportarActionPerformed
        // TODO add your handling code here:
        login(TableResultados, "resultados.xls", encabezado);
        
    }//GEN-LAST:event_BtExportarActionPerformed

    private void BtCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtCargarActionPerformed
        // TODO add your handling code here:
        cargarElecciones();
    }//GEN-LAST:event_BtCargarActionPerformed

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
            java.util.logging.Logger.getLogger(FrmResultados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmResultados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmResultados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmResultados.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmResultados(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtCargar;
    private javax.swing.JButton BtExportar;
    private javax.swing.JButton BtGeneral;
    private javax.swing.JButton BtPorGrado;
    private javax.swing.JButton BtTotal;
    private javax.swing.JComboBox<String> CbElecciones;
    private javax.swing.JComboBox<String> CbGrados;
    private javax.swing.JPanel PanelResult;
    private javax.swing.JTable TableResultados;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
