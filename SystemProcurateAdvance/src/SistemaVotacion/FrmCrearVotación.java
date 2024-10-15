/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package SistemaVotacion;

import SistemaVotacion.ConexionBD.DAO.EleccionDAO;
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
import raven.datetime.component.date.DatePicker;

/**
 *
 * @author HP
 */
public class FrmCrearVotación extends javax.swing.JFrame {

    private static final Logger LOGGER = Logger.getLogger(FrmCrearVotación.class.getName());
    EleccionDAO elecciondao;
    Votarr vota = new Votarr();
    private Votarr formularioVotacion;
    private FrmPrincipal formularioPrincipal;

    public FrmCrearVotación() {
        initComponents();
      
        SetEleccion();
        elecciondao = new EleccionDAO();

    }
    public void setFormularioPrincipal(FrmPrincipal formularioPrincipal) {
        this.formularioPrincipal = formularioPrincipal;
    }
    
    public void estilos(){
        
    }

    private void SetEleccion() {
        DatePicker1.setSeparator("to date");
        DatePicker1.setUsePanelOption(true);
        DatePicker1.setDateSelectionAble(localDate -> !localDate.isBefore(LocalDate.now()));
        DatePicker1.setEditor(FormatDay);
        DatePicker1.setDateSelectionMode(DatePicker.DateSelectionMode.SINGLE_DATE_SELECTED);

        TimePicker.set24HourView(true);
        TimePicker.setSelectedTime(LocalTime.of(8, 0));
        TimePicker.setEditor(FormatHour);

        TimePickerFin.set24HourView(true);
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
        return true;
    } catch (SQLException ex) {
        LOGGER.log(Level.SEVERE, "Error al guardar la elección", ex);
        JOptionPane.showMessageDialog(this, "Error al guardar la elección: " + ex.getMessage());
        return false;
    }
}
    private void limpiarCampos() {
        if (DatePicker1 != null) {
            DatePicker1.setSelectedDate(null);
        }
        if (TimePicker != null) {
            TimePicker.setSelectedTime(LocalTime.of(8, 0));
        }
        if (TimePickerFin != null) {
            TimePickerFin.setSelectedTime(LocalTime.of(9, 0));
        }
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        BtnVotacion.setText("Iniciar Votacion");
        BtnVotacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtnVotacionActionPerformed(evt);
            }
        });

        jLabel1.setText("Escoge el dia de la eleccion: ");

        jLabel2.setText("Setea la Hora: ");

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
                                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FormatDay, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PanelVotacionLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FormatHour, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(FormatHourFin, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(PanelVotacionLayout.createSequentialGroup()
                        .addGap(245, 245, 245)
                        .addComponent(BtnVotacion, javax.swing.GroupLayout.PREFERRED_SIZE, 225, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(254, Short.MAX_VALUE))
        );
        PanelVotacionLayout.setVerticalGroup(
            PanelVotacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelVotacionLayout.createSequentialGroup()
                .addGap(89, 89, 89)
                .addGroup(PanelVotacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(FormatDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 172, Short.MAX_VALUE)
                .addGroup(PanelVotacionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(FormatHour, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(FormatHourFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(80, 80, 80)
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
            if (formularioVotacion == null) {
                formularioVotacion = new Votarr(); 
            }

           
            formularioVotacion.setLocationRelativeTo(this);
            formularioVotacion.setVisible(true);
            formularioVotacion.iniciarProcesoDeMostraryVotacion();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Por favor, completa todos los campos antes de iniciar la votación.",
                "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    }//GEN-LAST:event_BtnVotacionActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatXcodeDarkIJTheme.setup();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmCrearVotación().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
