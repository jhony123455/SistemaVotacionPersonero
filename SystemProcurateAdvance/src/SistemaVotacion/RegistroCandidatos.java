/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package SistemaVotacion;


import SistemaVotacion.Modelos.Estudiantes;
import SistemaVotacion.Modelos.Candidatos;
import SistemaVotacion.ConexionBD.DAO.CandidatosDAO;
import SistemaVotacion.ConexionBD.DAO.EstudiantesDAO;
import com.formdev.flatlaf.FlatClientProperties;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


/**
 *
 * @author HP
 */
public class RegistroCandidatos extends javax.swing.JFrame {

    /**
     * Creates new form RegistroCandidatos
     */
    String propuesta;
    private CandidatosDAO candidatosdao;
    private EstudiantesDAO estudiantesdao;
    private FrmPrincipal formularioPrincipal;
    int count = 0;
    
    public RegistroCandidatos() {
        initComponents();
        setContentPane(PanelCandidatos);
        pack();
        candidatosdao= new CandidatosDAO();
        estudiantesdao = new EstudiantesDAO();
        TxtCandidato1.setText("");
        TxtCandidato2.setText("");
        TxtCandidato3.setText("");
        llenarEstudiante();
        BtEliminarCandidatos.setEnabled(false);
        actualizarInterfazCandidatos();
        estilos();
   
        
    }
    
    public void setFormularioPrincipal(FrmPrincipal formularioPrincipal) {
        this.formularioPrincipal = formularioPrincipal;
    }
    
    public void actualizarDespuesDeBorrado() {
        TxtCandidato1.setText("");
        TxtCandidato2.setText("");
        TxtCandidato3.setText("");
        llenarEstudiante();
    }
    
    public void llenarEstudiante() {
       ComboEstudiantes11.removeAllItems();
    ComboEstudiantes11.addItem("Selecciona Estudiante");
    
    List<Estudiantes> todosLosEstudiantes = estudiantesdao.obtenerTodosLosEstudiantes();
    for (Estudiantes estudiante : todosLosEstudiantes) {
        if (estudiante.getGrado().getNombreGrado().equals("11º")) {
            String nombreCompleto = estudiante.getNombre() + " " + estudiante.getApellido();
            ComboEstudiantes11.addItem(nombreCompleto);
            System.out.println("Estudiante agregado: " + nombreCompleto);
        }
    }
    }
    
    public boolean registrarCandidatos() {
        try {
            List<Candidatos> candidatosRegistrados = candidatosdao.obtenerCandidatosActivos();
            if (candidatosRegistrados.size() >= 3) {
                BtEliminarCandidatos.setEnabled(true);
                JOptionPane.showMessageDialog(this, "Ya se han registrado 3 candidatos activos. No se pueden agregar más.");
                return false;
            }

            String estudianteSeleccionado = (String) ComboEstudiantes11.getSelectedItem();
            if (estudianteSeleccionado == null || estudianteSeleccionado.equals("Selecciona Estudiante")) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un estudiante de 11°.");
                return false;
            }

            Estudiantes estudiante = buscarEstudiantePorNombreCompleto(estudianteSeleccionado);
            if (estudiante == null) {
                JOptionPane.showMessageDialog(this, "El estudiante seleccionado no es válido.");
                return false;
            }

            if (candidatoYaRegistrado(estudiante)) {
                JOptionPane.showMessageDialog(this, "Este estudiante ya está registrado como candidato.");
                return false;
            }

            propuesta = JOptionPane.showInputDialog(this, "Ingrese la propuesta para " + estudianteSeleccionado + ":");
            if (propuesta == null || propuesta.trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe ingresar una propuesta válida.");
                return false;
            }

            Candidatos nuevoCandidato = new Candidatos(1, estudiante, propuesta);
            boolean registroExitoso = candidatosdao.agregarCandidato(nuevoCandidato);
            
            if (registroExitoso) {
                actualizarInterfazCandidatos();
                
                return true;
                
            } else {
                JOptionPane.showMessageDialog(this, "Error al registrar el candidato en la base de datos.");
                return false;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el candidato: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    public JPanel getPanel() {
        return PanelCandidatos;
    }

    private Estudiantes buscarEstudiantePorNombreCompleto(String nombreCompleto) {
        String[] partes = nombreCompleto.split(" ");
        if (partes.length < 2) {
            return null;
        }

        String nombre = partes[0];
        String apellido = partes[1];

        return estudiantesdao.buscarPorNombreYApellido(nombre, apellido);
    }

    private boolean candidatoYaRegistrado(Estudiantes estudiante) {
        try {
            List<Candidatos> candidatos = candidatosdao.obtenerCandidatosActivos();
            for (Candidatos candidato : candidatos) {
                if (candidato.getEstudiante().getId() == estudiante.getId()) {
                    return true;
                }
            }
            return false;
        } catch (SQLException ex) {
            Logger.getLogger(RegistroCandidatos.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }


    private void actualizarInterfazCandidatos() {
        try {
            List<Candidatos> candidatosRegistrados = candidatosdao.obtenerCandidatosActivos();

           
            TxtCandidato1.setText("");
            TxtCandidato2.setText("");
            TxtCandidato3.setText("");

            int index = 0;
            for (Candidatos candidato : candidatosRegistrados) {
                if (candidato.isActivo() && index < 3) {
                    switch (index) {
                        case 0:
                            TxtCandidato1.setText(candidato.getEstudiante().getNombre() + " " + candidato.getEstudiante().getApellido());
                            break;
                        case 1:
                            TxtCandidato2.setText(candidato.getEstudiante().getNombre() + " " + candidato.getEstudiante().getApellido());
                            break;
                        case 2:
                            TxtCandidato3.setText(candidato.getEstudiante().getNombre() + " " + candidato.getEstudiante().getApellido());
                            break;
                    }
                    index++;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al actualizar la interfaz de candidatos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    public void estilos() {
        // Estilo para el título "Estudiantes de 11°"
        LbEstudiantes11.putClientProperty(FlatClientProperties.STYLE, ""
                + "font: bold 16 'Segoe UI';"
                + "foreground: #E0E0E0");

        // Estilo común para los labels de candidatos
        String labelCandidatoStyle = ""
                + "font: 14 'Segoe UI';"
                + "foreground: #B0B0B0";

        jLabel1.putClientProperty(FlatClientProperties.STYLE, labelCandidatoStyle);
        jLabel2.putClientProperty(FlatClientProperties.STYLE, labelCandidatoStyle);
        jLabel3.putClientProperty(FlatClientProperties.STYLE, labelCandidatoStyle);

        // Estilo para los campos de texto
        String textFieldStyle = ""
                + "background: #34495E;"
                + "foreground: #FFFFFF;"
                + "caretColor: #FFFFFF;"
             
                + "arc: 5";

        TxtCandidato1.putClientProperty(FlatClientProperties.STYLE, textFieldStyle);
        TxtCandidato2.putClientProperty(FlatClientProperties.STYLE, textFieldStyle);
        TxtCandidato3.putClientProperty(FlatClientProperties.STYLE, textFieldStyle);

        // Estilo para el ComboBox
        ComboEstudiantes11.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #34495E;"
                + "foreground: #FFFFFF;"

                + "arc: 5");

        // Estilo para los botones
        String buttonStyle = ""
                + "background: #2C3E50;"
                + "foreground: #FFFFFF;"
                + "focusedBackground: darken(#2C3E50,10%);"
                + "hoverBackground: darken(#2C3E50,5%);"
                + "pressedBackground: darken(#2C3E50,15%);"
                + "font: bold 12 'Segoe UI';"
          
                + "arc: 8";

        BtCarga.putClientProperty(FlatClientProperties.STYLE, buttonStyle);
        BtRegistar.putClientProperty(FlatClientProperties.STYLE, buttonStyle);

        
        BtEliminarCandidatos.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #C0392B;"
                + "foreground: #FFFFFF;"
                + "focusedBackground: darken(#C0392B,10%);"
                + "hoverBackground: darken(#C0392B,5%);"
                + "pressedBackground: darken(#C0392B,15%);"
                + "font: bold 12 'Segoe UI';"
         
                + "arc: 8");
    }

    
    
    
    
    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelCandidatos = new javax.swing.JPanel();
        BtRegistar = new javax.swing.JButton();
        ComboEstudiantes11 = new javax.swing.JComboBox<>();
        LbEstudiantes11 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        TxtCandidato3 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        TxtCandidato2 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        TxtCandidato1 = new javax.swing.JTextField();
        BtCarga = new javax.swing.JButton();
        BtEliminarCandidatos = new javax.swing.JButton();
        BtHelp = new SistemaVotacion.MyButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        BtRegistar.setText("Registrar");
        BtRegistar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtRegistarActionPerformed(evt);
            }
        });

        ComboEstudiantes11.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        LbEstudiantes11.setText("Estudiantes de 11º: ");

        jLabel3.setText("Candidato 3: ");

        TxtCandidato3.setEditable(false);
        TxtCandidato3.setText("jTextField2");

        jLabel2.setText("Candidato 2: ");

        TxtCandidato2.setEditable(false);
        TxtCandidato2.setText("jTextField1");

        jLabel1.setText("Candidato 1: ");

        TxtCandidato1.setEditable(false);
        TxtCandidato1.setText("jTextField1");

        BtCarga.setText("Cargar ComboBox");
        BtCarga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtCargaActionPerformed(evt);
            }
        });

        BtEliminarCandidatos.setText("Eliminar Candidatos");
        BtEliminarCandidatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtEliminarCandidatosActionPerformed(evt);
            }
        });

        BtHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/signo-de-interrogacion.png"))); // NOI18N
        BtHelp.setRadius(500);
        BtHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtHelpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelCandidatosLayout = new javax.swing.GroupLayout(PanelCandidatos);
        PanelCandidatos.setLayout(PanelCandidatosLayout);
        PanelCandidatosLayout.setHorizontalGroup(
            PanelCandidatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCandidatosLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(PanelCandidatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCandidatosLayout.createSequentialGroup()
                        .addComponent(LbEstudiantes11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(ComboEstudiantes11, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BtCarga)
                        .addGap(18, 18, 18)
                        .addComponent(BtRegistar, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BtEliminarCandidatos, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PanelCandidatosLayout.createSequentialGroup()
                        .addGroup(PanelCandidatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtCandidato1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(47, 47, 47)
                        .addGroup(PanelCandidatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TxtCandidato2, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(30, 30, 30)
                        .addGroup(PanelCandidatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TxtCandidato3, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(61, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCandidatosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BtHelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );
        PanelCandidatosLayout.setVerticalGroup(
            PanelCandidatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCandidatosLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(BtHelp, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(PanelCandidatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LbEstudiantes11)
                    .addComponent(ComboEstudiantes11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtCarga)
                    .addComponent(BtRegistar)
                    .addComponent(BtEliminarCandidatos))
                .addGap(18, 18, 18)
                .addGroup(PanelCandidatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(PanelCandidatosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TxtCandidato1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TxtCandidato2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TxtCandidato3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(126, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelCandidatos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelCandidatos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtRegistarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtRegistarActionPerformed
        // TODO add your handling code here:
        boolean registroExitoso = registrarCandidatos();
        if (registroExitoso) {
            formularioPrincipal.actualizarEstado();
            formularioPrincipal.verificarCondicionesVotacion();
        }
    }//GEN-LAST:event_BtRegistarActionPerformed

    private void BtCargaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtCargaActionPerformed
        // TODO add your handling code here:
        llenarEstudiante();
      
    }//GEN-LAST:event_BtCargaActionPerformed

    private void BtEliminarCandidatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtEliminarCandidatosActionPerformed
        // TODO add your handling code here:
                
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea eliminar todos los candidatos?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion == JOptionPane.YES_OPTION) {
            try {
                boolean eliminacionExitosa = candidatosdao.desactivarTodosLosCandidatos();
                if (eliminacionExitosa) {
                    JOptionPane.showMessageDialog(this,
                            "Todos los candidatos han sido eliminados.",
                            "Eliminación exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                    actualizarInterfazCandidatos();
                    formularioPrincipal.actualizarEstado();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Hubo un problema al eliminar los candidatos.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error al eliminar los candidatos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_BtEliminarCandidatosActionPerformed

    private void BtHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtHelpActionPerformed
        // TODO add your handling code here:
        Tutorial.getInstance().mostrarTutorial(this, "RegistroCandidatos");
    }//GEN-LAST:event_BtHelpActionPerformed

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
            java.util.logging.Logger.getLogger(RegistroCandidatos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(RegistroCandidatos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(RegistroCandidatos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(RegistroCandidatos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RegistroCandidatos().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtCarga;
    private javax.swing.JButton BtEliminarCandidatos;
    private SistemaVotacion.MyButton BtHelp;
    private javax.swing.JButton BtRegistar;
    private javax.swing.JComboBox<String> ComboEstudiantes11;
    private javax.swing.JLabel LbEstudiantes11;
    private javax.swing.JPanel PanelCandidatos;
    private javax.swing.JTextField TxtCandidato1;
    private javax.swing.JTextField TxtCandidato2;
    private javax.swing.JTextField TxtCandidato3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    // End of variables declaration//GEN-END:variables
}
