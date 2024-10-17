/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package SistemaVotacion;


import SistemaVotacion.Modelos.Grados;
import SistemaVotacion.Modelos.Estudiantes;
import SistemaVotacion.Modelos.Candidatos;
import SistemaVotacion.ConexionBD.DAO.EstudiantesDAO;
import SistemaVotacion.ConexionBD.DAO.GradosDAO;
import static java.awt.image.ImageObserver.ERROR;


import javax.swing.JOptionPane;
import com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme;
import java.util.List;
import javax.swing.*;

/**
 *
 * @author HP
 */
public class FrmRegistroEstudiantes extends javax.swing.JFrame {

    /**
     * Creates new form FrmRegistroEstudiantes
     */
   String nombre, documento, apellido, grado; 
    Validaciones val = new Validaciones(); 
    private FrmPrincipal formularioPrincipal;

    public FrmRegistroEstudiantes() {
        initComponents();
        setContentPane(PanelRegistro);
        pack();
        estilos();    
        TxtNombre.setText("");
        TxtApellido.setText("");
        TxtDocumento.setText("");
        llenarGrados();
    }

    public void setFormularioPrincipal(FrmPrincipal formularioPrincipal) {
        this.formularioPrincipal = formularioPrincipal;
    }

    public void mostrarConsola() {
        for(int i = 0; i < Estudiantes.listaEstudiantes.size(); i++) {
            System.out.println(Estudiantes.listaEstudiantes.get(i).datoContacto());
        } 
    }

    public void llenarGrados() {
        GradosDAO gradosDAO = new GradosDAO();
        List<Grados> listaGrados = gradosDAO.obtenerGrados();

        ComboGrado.removeAllItems();
        ComboGrado.addItem("Selecciona");

        for (Grados grado : listaGrados) {
            ComboGrado.addItem(grado.getNombreGrado());
        }
    }

    public void GuardarEstudiante() {
        mensajeMostrado = false;
        BarraProgreso.setIndeterminate(true);
        BarraProgreso.setString("Cargando...");
        BarraProgreso.setStringPainted(true);
        BarraProgreso.setVisible(true);
        PanelRegistro.add(BarraProgreso);
        PanelRegistro.revalidate();
        PanelRegistro.repaint();
        
        try {
            nombre = TxtNombre.getText();
            apellido = TxtApellido.getText();
            documento = TxtDocumento.getText();
            String gradoSeleccionado = ComboGrado.getSelectedItem().toString();
            
            val.validarDatos(nombre, apellido, documento, gradoSeleccionado);
            GradosDAO gradosDAO = new GradosDAO();
            int idGrado = gradosDAO.buscarIdGrado(gradoSeleccionado);
           
            Estudiantes estudiante = new Estudiantes(1,nombre, documento, apellido);
            estudiante.AsignarGrado(new Grados(idGrado, gradoSeleccionado));
            EstudiantesDAO estudiantesDAO = new EstudiantesDAO();
            boolean guardadoEnBD = estudiantesDAO.saveStudent(estudiante);

            if (!guardadoEnBD) {
                mostrarMensaje("Error al guardar el estudiante.", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (gradoSeleccionado.equals("11º")) {
                Candidatos.listaCandidatos.add(new Candidatos(1, estudiante, ""));
                
               
            }
            
           
            

            JOptionPane.showMessageDialog(rootPane, "Estudiante guardado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            formularioPrincipal.actualizarEstado();
            Borrar();
            System.out.println(estudiante.datoContacto());

        } catch (Validaciones.ValidacionException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
           
            JOptionPane.showMessageDialog(rootPane, "Ingrese un valor válido", "Error", JOptionPane.ERROR_MESSAGE);
            System.out.println(e.toString());
        } finally {
            BarraProgreso.setVisible(false);
            PanelRegistro.remove(BarraProgreso);
            PanelRegistro.revalidate();
            PanelRegistro.repaint();
        }
    }

    private void estilos() {
        BtSave.putClientProperty("JButton.buttonType", "roundRect");
    }

    public JPanel getPanel() {
        return PanelRegistro;
    }
    private boolean mensajeMostrado = false;

    private void mostrarMensaje(String mensaje, int tipo) {
    if (!mensajeMostrado) {
        JOptionPane.showMessageDialog(rootPane, mensaje, "Información", tipo);
        mensajeMostrado = true;
    }
    }
    
    public void Borrar(){
        TxtNombre.setText("");
        TxtApellido.setText("");
        TxtDocumento.setText("");
        ComboGrado.setSelectedItem(ComboGrado.getItemAt(0));
    }

  

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PanelRegistro = new javax.swing.JPanel();
        TxtNombre = new javax.swing.JTextField();
        LbNombre = new javax.swing.JLabel();
        TxtApellido = new javax.swing.JTextField();
        LbApellido = new javax.swing.JLabel();
        TxtDocumento = new javax.swing.JTextField();
        LbDocumento = new javax.swing.JLabel();
        ComboGrado = new javax.swing.JComboBox<>();
        LbGrado = new javax.swing.JLabel();
        BtSave = new javax.swing.JButton();
        BarraProgreso = new javax.swing.JProgressBar();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        TxtNombre.setText("jTextField1");
        TxtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TxtNombreKeyTyped(evt);
            }
        });

        LbNombre.setText("Nombre: ");

        TxtApellido.setText("jTextField1");
        TxtApellido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TxtApellidoKeyTyped(evt);
            }
        });

        LbApellido.setText("Apellido:");

        TxtDocumento.setText("jTextField1");
        TxtDocumento.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                TxtDocumentoKeyTyped(evt);
            }
        });

        LbDocumento.setText("Documento:");

        ComboGrado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        LbGrado.setText("Grado del Estudiante: ");

        BtSave.setText("Guardar");
        BtSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelRegistroLayout = new javax.swing.GroupLayout(PanelRegistro);
        PanelRegistro.setLayout(PanelRegistroLayout);
        PanelRegistroLayout.setHorizontalGroup(
            PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelRegistroLayout.createSequentialGroup()
                .addGap(59, 59, 59)
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LbDocumento)
                    .addComponent(LbApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LbGrado)
                    .addComponent(LbNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 117, Short.MAX_VALUE)
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BtSave, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(TxtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(TxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(TxtDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(ComboGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(396, 396, 396))
            .addGroup(PanelRegistroLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BarraProgreso, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelRegistroLayout.setVerticalGroup(
            PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelRegistroLayout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LbNombre)
                    .addComponent(TxtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LbApellido)
                    .addComponent(TxtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LbDocumento)
                    .addComponent(TxtDocumento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(PanelRegistroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LbGrado)
                    .addComponent(ComboGrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(84, 84, 84)
                .addComponent(BtSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153, Short.MAX_VALUE)
                .addComponent(BarraProgreso, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelRegistro, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void TxtNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtNombreKeyTyped
        // TODO add your handling code here:
        if(TxtNombre.getText().length() >= 25)
    {
        evt.consume();
    }
    }//GEN-LAST:event_TxtNombreKeyTyped

    private void TxtDocumentoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtDocumentoKeyTyped
        // TODO add your handling code here:
        if(TxtDocumento.getText().length() >= 10)
    {
        evt.consume();
    }
    }//GEN-LAST:event_TxtDocumentoKeyTyped

    private void TxtApellidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_TxtApellidoKeyTyped
        // TODO add your handling code here:
        if(TxtApellido.getText().length() >= 25)
    {
        evt.consume();
       
    }
    }//GEN-LAST:event_TxtApellidoKeyTyped

    private void BtSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtSaveActionPerformed
        // TODO add your handling code here:
        GuardarEstudiante();
        
    }//GEN-LAST:event_BtSaveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
         try {
            FlatGradiantoDarkFuchsiaIJTheme.setup(); 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /* Set the Nimbus look and feel */
        ////<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        /*try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(FrmRegistroEstudiantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(FrmRegistroEstudiantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(FrmRegistroEstudiantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(FrmRegistroEstudiantes.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            
            public void run() {
                new FrmRegistroEstudiantes().setVisible(true);
            }
        });
       

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar BarraProgreso;
    private javax.swing.JButton BtSave;
    private javax.swing.JComboBox<String> ComboGrado;
    private javax.swing.JLabel LbApellido;
    private javax.swing.JLabel LbDocumento;
    private javax.swing.JLabel LbGrado;
    private javax.swing.JLabel LbNombre;
    private javax.swing.JPanel PanelRegistro;
    private javax.swing.JTextField TxtApellido;
    private javax.swing.JTextField TxtDocumento;
    private javax.swing.JTextField TxtNombre;
    // End of variables declaration//GEN-END:variables
}
