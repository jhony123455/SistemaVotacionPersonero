/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package SistemaVotacion;

import SistemaVotacion.ConexionBD.DAO.EleccionDAO;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import raven.datetime.component.date.DatePicker;

/**
 *
 * @author HP
 */
public class FrmCrearVotación extends javax.swing.JFrame {

    private static final Logger LOGGER = Logger.getLogger(FrmCrearVotación.class.getName());
    EleccionDAO elecciondao;
    Votarr vota;
    private Votarr formularioVotacion;
    private FrmPrincipal formularioPrincipal;

    public FrmCrearVotación(FrmPrincipal principal) {
        initComponents();
      
        SetEleccion();
        elecciondao = new EleccionDAO();
        this.formularioPrincipal = principal;
        estilos();

    }
    public void setFormularioPrincipal(FrmPrincipal formularioPrincipal) {
        this.formularioPrincipal = formularioPrincipal;
    }
    
    public void estilos() {
       
        String labelStyle = ""
                + "font: 14 'Segoe UI';"
                + "foreground: #B0B0B0";

        jLabel1.putClientProperty(FlatClientProperties.STYLE, labelStyle);
        jLabel2.putClientProperty(FlatClientProperties.STYLE, labelStyle);

        
        String dateTimeStyle = ""
                + "background: #2C3E50;"
                + "foreground: #FFFFFF;"
          
                + "arc: 5";

        DatePicker1.putClientProperty(FlatClientProperties.STYLE, dateTimeStyle);
        TimePicker.putClientProperty(FlatClientProperties.STYLE, dateTimeStyle);
        TimePickerFin.putClientProperty(FlatClientProperties.STYLE, dateTimeStyle);

        // Estilo para los botones de vista
        String viewButtonStyle = ""
                + "background: #34495E;"
                + "foreground: #8395A7;"
                + "focusedBackground: darken(#34495E,10%);"
                + "hoverBackground: darken(#34495E,5%);"
                + "pressedBackground: darken(#34495E,15%);"
                + "font: bold 12 'Segoe UI';"
              
                + "arc: 8";

        Bt24.putClientProperty(FlatClientProperties.STYLE, viewButtonStyle);
        Bt12.putClientProperty(FlatClientProperties.STYLE, viewButtonStyle);

        // Estilo para el botón principal
        BtnVotacion.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #3498DB;"
                + "foreground: #FFFFFF;"
                + "focusedBackground: darken(#3498DB,10%);"
                + "hoverBackground: darken(#3498DB,5%);"
                + "pressedBackground: darken(#3498DB,15%);"
                + "font: bold 14 'Segoe UI';"
              
                + "arc: 10");
    }

    private void SetEleccion() {
        DatePicker1.setSeparator("to date");
        DatePicker1.setUsePanelOption(true);
        DatePicker1.setDateSelectionAble(localDate -> !localDate.isBefore(LocalDate.now()));
        DatePicker1.setEditor(FormatDay);
        DatePicker1.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);

        TimePicker.set24HourView(false);
        TimePicker.setSelectedTime(LocalTime.of(8, 0));
        TimePicker.setEditor(FormatHour);

        TimePickerFin.set24HourView(false);
        TimePickerFin.setSelectedTime(LocalTime.of(9, 0));
        TimePickerFin.setEditor(FormatHourFin);

        DatePicker1.addPropertyChangeListener("date", e -> verificarCampos());
        TimePicker.addPropertyChangeListener("time", e -> {
            verificarCampos();
            ajustarHoraFin();
        });
        TimePickerFin.addPropertyChangeListener("time", e -> verificarCampos());
    }

    private void ajustarHoraFin() {
        LocalTime inicioTime = TimePicker.getSelectedTime();
        LocalTime finTime = TimePickerFin.getSelectedTime();
        if (finTime != null && (finTime.equals(inicioTime) || finTime.isBefore(inicioTime))) {
            TimePickerFin.setSelectedTime(inicioTime.plusHours(1));
        }
    }

    private boolean guardarEleccion() {
        LocalDate fecha = DatePicker1.getSelectedDate();
        LocalTime horaInicio = TimePicker.getSelectedTime();
        LocalTime horaFin = TimePickerFin.getSelectedTime();

        if (fecha == null || horaInicio == null || horaFin == null) {
            JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos.");
            return false;
        }

        LocalDateTime fechaHoraInicio = LocalDateTime.of(fecha, horaInicio);
        LocalDateTime fechaHoraFin = LocalDateTime.of(fecha, horaFin);

        LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        fechaHoraInicio = fechaHoraInicio.truncatedTo(ChronoUnit.MINUTES);
        fechaHoraFin = fechaHoraFin.truncatedTo(ChronoUnit.MINUTES);

        if (fechaHoraInicio.isBefore(ahora)) {
            JOptionPane.showMessageDialog(this, "La fecha de inicio debe ser futura.");
            return false;
        }

        if (fechaHoraFin.isBefore(fechaHoraInicio) || fechaHoraFin.equals(fechaHoraInicio)) {
            JOptionPane.showMessageDialog(this, "La hora de fin debe ser posterior a la hora de inicio.");
            return false;
        }

        try {
            elecciondao.insertarEleccion(fechaHoraInicio, fechaHoraFin);
            JOptionPane.showMessageDialog(this, "Elección guardada exitosamente.");

            
            if (formularioPrincipal != null) {
                formularioPrincipal.actualizarEstado();
            }

           
            if (fechaHoraInicio.equals(ahora) || fechaHoraInicio.isBefore(ahora)) {
                iniciarProcesoVotacion(fechaHoraFin);
            } else {
                programarInicioVotacion(fechaHoraInicio, fechaHoraFin);
            }

            return true;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al guardar la elección", ex);
            JOptionPane.showMessageDialog(this, "Error al guardar la elección: " + ex.getMessage());
            return false;
        }
    }
    
     private void iniciarProcesoVotacion(LocalDateTime fechaHoraFin) {
        if (formularioVotacion == null) {
            formularioVotacion = new Votarr(formularioPrincipal, fechaHoraFin);
        }
        formularioVotacion.iniciarProcesoDeMostraryVotacion();
        formularioVotacion.llenarRadioButtonsConCandidatos();
        formularioVotacion.setVisible(true);
    }
     
    private void programarInicioVotacion(LocalDateTime fechaHoraInicio, LocalDateTime fechaHoraFin) {
        long delay = LocalDateTime.now().until(fechaHoraInicio, ChronoUnit.MILLIS);
        Timer timer = new Timer((int) delay, e -> {
            iniciarProcesoVotacion(fechaHoraFin);
        });
        timer.setRepeats(false);
        timer.start();
    }
    private boolean verificarCampos() {
        LocalDate fechaSeleccionada = DatePicker1.getSelectedDate();
        LocalTime horaInicio = TimePicker.getSelectedTime();
        LocalTime horaFin = TimePickerFin.getSelectedTime();

        boolean isValid = true;
        StringBuilder errorMsg = new StringBuilder("Por favor, completa los siguientes campos:\n");

        if (fechaSeleccionada == null) {
            isValid = false;
            errorMsg.append("- Fecha seleccionada\n");
        }
        if (horaInicio == null) {
            isValid = false;
            errorMsg.append("- Hora de inicio\n");
        }
        if (horaFin == null) {
            isValid = false;
            errorMsg.append("- Hora de fin\n");
        }

        if (!isValid) {
            JOptionPane.showMessageDialog(this, errorMsg.toString(), "Campos incompletos", JOptionPane.ERROR_MESSAGE);
        }

        return isValid;
    }
    
     public JPanel getPanel() {
        return PanelVotacion;
    }
     
     
    



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        DatePicker1 = new raven.datetime.component.date.DatePicker();
        TimePicker = new raven.datetime.component.time.TimePicker();
        TimePickerFin = new raven.datetime.component.time.TimePicker();
        PanelVotacion = new javax.swing.JPanel();
        BtnVotacion = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        FormatDay = new javax.swing.JFormattedTextField();
        jLabel2 = new javax.swing.JLabel();
        FormatHour = new javax.swing.JFormattedTextField();
        FormatHourFin = new javax.swing.JFormattedTextField();
        Bt24 = new javax.swing.JButton();
        Bt12 = new javax.swing.JButton();
        BtHelp = new SistemaVotacion.MyButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        BtnVotacion.setText("Iniciar Votacion");
        BtnVotacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnVotacionActionPerformed(evt);
            }
        });

        jLabel1.setText("Escoge el dia de la eleccion: ");

        jLabel2.setText("Setea la Hora: ");

        Bt24.setText("Vista 24h");
        Bt24.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Bt24ActionPerformed(evt);
            }
        });

        Bt12.setText("Vista12h");
        Bt12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Bt12ActionPerformed(evt);
            }
        });

        BtHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/signo-de-interrogacion.png"))); // NOI18N
        BtHelp.setRadius(500);
        BtHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtHelpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelVotacionLayout = new javax.swing.GroupLayout(PanelVotacion);
        PanelVotacion.setLayout(PanelVotacionLayout);
        PanelVotacionLayout.setHorizontalGroup(
            PanelVotacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVotacionLayout.createSequentialGroup()
                .addGroup(PanelVotacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelVotacionLayout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addGroup(PanelVotacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelVotacionLayout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FormatDay, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PanelVotacionLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FormatHour, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(FormatHourFin, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(PanelVotacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(Bt12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(Bt24, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)))))
                    .addGroup(PanelVotacionLayout.createSequentialGroup()
                        .addGap(245, 245, 245)
                        .addComponent(BtnVotacion, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(105, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelVotacionLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(BtHelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
        PanelVotacionLayout.setVerticalGroup(
            PanelVotacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVotacionLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BtHelp, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(43, 43, 43)
                .addGroup(PanelVotacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(FormatDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 184, Short.MAX_VALUE)
                .addGroup(PanelVotacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(FormatHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FormatHourFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(Bt24))
                .addGap(18, 18, 18)
                .addComponent(Bt12)
                .addGap(22, 22, 22)
                .addComponent(BtnVotacion, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(97, 97, 97))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelVotacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelVotacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void BtnVotacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtnVotacionActionPerformed
       if (verificarCampos()) {
        if (guardarEleccion()) {
            LOGGER.info("Votación iniciada correctamente.");
           
            formularioPrincipal.setEnabled(false);
          
          
     
        }
    } else {
        JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos antes de iniciar la votación.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    }//GEN-LAST:event_BtnVotacionActionPerformed

    private void Bt24ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Bt24ActionPerformed
        // TODO add your handling code here:
        TimePicker.set24HourView(true);
        TimePickerFin.set24HourView(true);
    }//GEN-LAST:event_Bt24ActionPerformed

    private void Bt12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Bt12ActionPerformed
        // TODO add your handling code here:
        TimePicker.set24HourView(false);
        TimePickerFin.set24HourView(false);
    }//GEN-LAST:event_Bt12ActionPerformed

    private void BtHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtHelpActionPerformed
        // TODO add your handling code here:
        Tutorial.getInstance().mostrarTutorial(this, "FrmCrearVotacion");
    }//GEN-LAST:event_BtHelpActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatXcodeDarkIJTheme.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmCrearVotación(null).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Bt12;
    private javax.swing.JButton Bt24;
    private SistemaVotacion.MyButton BtHelp;
    private javax.swing.JButton BtnVotacion;
    private raven.datetime.component.date.DatePicker DatePicker1;
    private javax.swing.JFormattedTextField FormatDay;
    private javax.swing.JFormattedTextField FormatHour;
    private javax.swing.JFormattedTextField FormatHourFin;
    private javax.swing.JPanel PanelVotacion;
    private raven.datetime.component.time.TimePicker TimePicker;
    private raven.datetime.component.time.TimePicker TimePickerFin;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables
}
