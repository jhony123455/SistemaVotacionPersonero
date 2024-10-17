/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package SistemaVotacion;

import SistemaVotacion.Modelos.Voto;
import SistemaVotacion.Modelos.Grados;
import SistemaVotacion.Modelos.Estudiantes;
import SistemaVotacion.Modelos.Eleccion;
import SistemaVotacion.Modelos.Candidatos;
import SistemaVotacion.ConexionBD.DAO.CandidatosDAO;
import SistemaVotacion.ConexionBD.DAO.EleccionDAO;
import SistemaVotacion.ConexionBD.DAO.EstudiantesDAO;
import SistemaVotacion.ConexionBD.DAO.GradosDAO;
import SistemaVotacion.ConexionBD.DAO.VotoDAO;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;



/**
 *
 * @author HP
 */
public class Votarr extends javax.swing.JFrame {

    private boolean votando = true;
    private Timer timer;
    private static final Logger LOGGER = Logger.getLogger(Votarr.class.getName());
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private VotoDAO votoDAO;
    private EstudiantesDAO estudianteDAO;
    private CandidatosDAO candidatoDAO;
    private EleccionDAO elecciondao;
    private List<Estudiantes> listaEstudiantes;
    private List<Candidatos> listaCandidatos;
     private FrmPrincipal formularioPrincipal;
    
    private boolean votacionIniciada = false; 
    private final LocalDateTime fechaFinalizacion;
    private boolean votacionFinalizada = false;

    public Votarr(FrmPrincipal principal, LocalDateTime fechaFinalizacion) {
        initComponents();
        this.formularioPrincipal = principal;
        this.fechaFinalizacion = fechaFinalizacion;
        Reloj reloj = new Reloj(lbreloj);
        reloj.start();
        stlyes();
        votoDAO = new VotoDAO();
        estudianteDAO = new EstudiantesDAO();
        candidatoDAO = new CandidatosDAO();
        elecciondao = new EleccionDAO();
        listaCandidatos = new CandidatosDAO().obtenerTodosLosCandidatos();
        cargarGrados();
        cargarEstudiantes();
        llenarRadioButtonsConCandidatos();
        CbEstudiante.setEnabled(false);
        
        

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                verificarYCerrar();
                 if (formularioPrincipal != null) {
                    formularioPrincipal.setEnabled(true);  
                    formularioPrincipal.actualizarEstado();  
                } 
            }
        });

        iconosRb();

    }

    public void stlyes() {
        lbreloj.putClientProperty(FlatClientProperties.STYLE, ""
                + "font: bold 48 $h1.font;"
                + "foreground: #00FF00;"
                + "background: #1E1E1E;"
                + "opaque: true");
    }

    public void iniciarProcesoDeMostraryVotacion() {
        if (!votacionIniciada) {
            inicializarEstadoEleccion();
            votacionIniciada = true;
        }
    }

    private void inicializarEstadoEleccion() {
        try {
            LOGGER.info("Verificando estado inicial de elección");
            boolean eleccionActiva = elecciondao.hayEleccionActiva();
            LOGGER.info("Estado inicial de la elección: " + (eleccionActiva ? "Activa" : "Inactiva"));

            if (eleccionActiva) {
                LOGGER.info("Elección activa encontrada. Iniciando votación.");
                iniciarVotacion();
                iniciarVerificacionPeriodica();
            } else {
                LOGGER.info("No hay elección activa al inicio.");
                actualizarUINoEleccion();

                iniciarVerificacionPeriodica();
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "Error al verificar el estado inicial de la elección", ex);
        }
    }

    private void actualizarUINoEleccion() {
        SwingUtilities.invokeLater(() -> {
            LOGGER.log(Level.SEVERE, "No hay elección activa en este momento. La ventana permanecerá abierta.");
            setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        });
    }

    private void iniciarVerificacionPeriodica() {
        scheduler.scheduleAtFixedRate(this::verificarEstadoEleccion, 0, 1, TimeUnit.SECONDS);
    }

    private void verificarEstadoEleccion() {
        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isAfter(fechaFinalizacion) && votando) {
            SwingUtilities.invokeLater(this::finalizarVotacion);
        } else if (ahora.isBefore(fechaFinalizacion) && !votando) {
            SwingUtilities.invokeLater(this::iniciarVotacion);
        }
    }

    private void iniciarVotacion() {
        votando = true;
        votacionFinalizada = false;
        SwingUtilities.invokeLater(() -> {
            CbEstudiante.setEnabled(true);
        });
    }

    private void finalizarVotacion() {
        votando = false;
        votacionFinalizada = true;
        scheduler.shutdown();
        SwingUtilities.invokeLater(() -> {
            mostrarMensajeFinalizacion();
            setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            if (formularioPrincipal != null) {
                formularioPrincipal.actualizarEstado();
            }
        });
    }

    private void mostrarMensajeFinalizacion() {
        JOptionPane.showMessageDialog(this, "La elección ha terminado. Puedes cerrar la ventana.");
    }

    private void verificarYCerrar() {
        if (votando) {
            JOptionPane.showMessageDialog(this, "La votación está en curso. No puedes salir.");
        } else if (votacionFinalizada) {
            mostrarMensajeFinalizacion();
            dispose();
        } else {
            dispose();
        }
    }

    @Override
    public void dispose() {
        scheduler.shutdownNow();
        super.dispose();
         if (formularioPrincipal != null) {
            formularioPrincipal.setEnabled(true);
            formularioPrincipal.actualizarEstado();
        }
    }

    @Override
    protected void processMouseEvent(MouseEvent e) {
        if (votacionFinalizada) {
            mostrarMensajeFinalizacion();
        }
        super.processMouseEvent(e);
    }

    @Override
    protected void processKeyEvent(KeyEvent e) {
        if (votacionFinalizada) {
            mostrarMensajeFinalizacion();
        }
        super.processKeyEvent(e);
    }
    
    private void cargarGrados() {
        GradosDAO gradosDAO = new GradosDAO();
        List<Grados> listaGrados = gradosDAO.obtenerGrados();

        CbGrado.removeAllItems();
        for (Grados grado : listaGrados) {
            CbGrado.addItem(grado.getNombreGrado());
        }
    }

    private void cargarEstudiantes() {
        CbEstudiante.removeAllItems();
        listaEstudiantes = estudianteDAO.obtenerTodosLosEstudiantes();
        for (Estudiantes est : listaEstudiantes) {
            CbEstudiante.addItem(est.getNombre() + " " + est.getApellido());
        }
    }

    private void filtrarEstudiantesPorGrado() {
        String gradoSeleccionado = (String) CbGrado.getSelectedItem();
        CbEstudiante.removeAllItems();

        try {
            listaEstudiantes = estudianteDAO.obtenerEstudiantesPorGrado(gradoSeleccionado);
            for (Estudiantes est : listaEstudiantes) {
                CbEstudiante.addItem(est.getNombre() + " " + est.getApellido());
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al filtrar estudiantes: " + ex.getMessage());
        }
    }

    private void guardarVoto() {
        try {
            Eleccion eleccionActual = elecciondao.obtenerEleccionActual();
            if (eleccionActual == null) {
               
                return;
            }

            LocalDateTime ahora = LocalDateTime.now();
            if (ahora.isBefore(eleccionActual.getFechainicio()) || ahora.isAfter(eleccionActual.getFechafin())) {
                JOptionPane.showMessageDialog(this, "La elección no está activa en este momento.");
                return;
            }

            if (!RbC1.isSelected() && !RbC2.isSelected() && !RbC3.isSelected() && !RbVotoenBlanco.isSelected()) {
                JOptionPane.showMessageDialog(this, "Por favor, seleccione un candidato.");
                return;
            }

            if (listaCandidatos.size() < 3) {
                
                return;
            }

            Candidatos candidatoSeleccionado = null;
            if (RbC1.isSelected()) {
                candidatoSeleccionado = listaCandidatos.get(0);
            } else if (RbC2.isSelected()) {
                candidatoSeleccionado = listaCandidatos.get(1);
            } else if (RbC3.isSelected()) {
                candidatoSeleccionado = listaCandidatos.get(2);
            }else if(RbVotoenBlanco.isSelected()){
                candidatoSeleccionado = null;
            }

            Estudiantes estudianteSeleccionado = listaEstudiantes.get(CbEstudiante.getSelectedIndex());

            if (votoDAO.estudianteYaVoto(estudianteSeleccionado.getId(), eleccionActual.getIdeleccion())) {
                JOptionPane.showMessageDialog(this, "Este estudiante ya ha votado en esta elección.");
                return;
            }

            Voto voto = new Voto(eleccionActual, candidatoSeleccionado, estudianteSeleccionado);
            votoDAO.guardarVoto(voto);
            JOptionPane.showMessageDialog(this, "Voto guardado exitosamente.");

            limpiarSeleccion();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error al guardar el voto: " + ex.getMessage());
        }
    }

    private void limpiarSeleccion() {
        RbC1.setSelected(false);
        RbC2.setSelected(false);
        RbC3.setSelected(false);
        RbVotoenBlanco.setSelected(false);
        CbEstudiante.setSelectedIndex(0);
    }

    private void llenarRadioButtonsConCandidatos() {

        Candidato.clearSelection();

        int size = listaCandidatos.size();

        if (size > 3) {
            size = 3;
        }

        if (size > 0) {
            RbC1.setText(listaCandidatos.get(0).toString());
            Candidato.add(RbC1);

            if (size > 1) {
                RbC2.setText(listaCandidatos.get(1).toString());
                Candidato.add(RbC2);
            }

            if (size > 2) {
                RbC3.setText(listaCandidatos.get(2).toString());
                Candidato.add(RbC3);
            }
            
        }
        RbVotoenBlanco.setText("Voto en Blanco");
         Candidato.add(RbVotoenBlanco);

    }
    
    private void iconosRb() {
        try {
            
            ImageIcon iconCandidato1 = redimensionarIcono(cargarIcono("/SistemaVotacion/Recursos/numero-1.png"), 30, 30);
            ImageIcon iconCandidato2 = redimensionarIcono(cargarIcono("/SistemaVotacion/Recursos/numero-2.png"), 30, 30);
            ImageIcon iconCandidato3 = redimensionarIcono(cargarIcono("/SistemaVotacion/Recursos/numero-3.png"), 30, 30);
            ImageIcon iconVotoEnBlanco = redimensionarIcono(cargarIcono("/SistemaVotacion/Recursos/delete_10080337.png"), 30, 30);

            if (iconCandidato1 != null) {
                setRadioButtonIcon(RbC1, iconCandidato1);
            }
            if (iconCandidato2 != null) {
                setRadioButtonIcon(RbC2, iconCandidato2);
            }
            if (iconCandidato3 != null) {
                setRadioButtonIcon(RbC3, iconCandidato3);
            }
            if (iconVotoEnBlanco != null) {
                setRadioButtonIcon(RbVotoenBlanco, iconVotoEnBlanco);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al cargar los iconos: " + e.getMessage());
        }
    }

    private ImageIcon cargarIcono(String ruta) {
        try {
            return new ImageIcon(getClass().getResource(ruta));
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono de la ruta: " + ruta);
            return null;
        }
    }

    private void setRadioButtonIcon(JRadioButton radioButton, ImageIcon icon) {
        if (radioButton != null && icon != null) {
            radioButton.setIcon(icon);
            radioButton.setSelectedIcon(createSelectedIcon(icon));
            radioButton.setHorizontalTextPosition(JRadioButton.RIGHT);
            radioButton.setVerticalTextPosition(JRadioButton.CENTER);

            radioButton.setContentAreaFilled(false);
            radioButton.setBorderPainted(false);
            radioButton.setOpaque(false);
            radioButton.setHorizontalTextPosition(JRadioButton.RIGHT);
            radioButton.setVerticalTextPosition(JRadioButton.CENTER);
        }
    }

    private ImageIcon createSelectedIcon(ImageIcon originalIcon) {
        Image img = originalIcon.getImage();
        BufferedImage bufferedImage = new BufferedImage(
                img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(img, 0, 0, null);

        g2d.setColor(new Color(57, 255, 20, 150));
        g2d.setStroke(new BasicStroke(7));
        g2d.drawRect(0, 0, bufferedImage.getWidth() - 1, bufferedImage.getHeight() - 1);

        g2d.dispose();

        return new ImageIcon(bufferedImage);
    }

    private ImageIcon redimensionarIcono(ImageIcon icon, int ancho, int alto) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(ancho, alto, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Candidato = new javax.swing.ButtonGroup();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        SaveVoto = new javax.swing.JButton();
        RbC1 = new javax.swing.JRadioButton();
        RbC2 = new javax.swing.JRadioButton();
        RbC3 = new javax.swing.JRadioButton();
        CbGrado = new javax.swing.JComboBox<>();
        CbEstudiante = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        RbVotoenBlanco = new javax.swing.JRadioButton();
        lbreloj = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel2.setText("Grado:");

        jLabel3.setText("Seleccione su candidato");

        SaveVoto.setText("Guardar");
        SaveVoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SaveVotoActionPerformed(evt);
            }
        });

        Candidato.add(RbC1);
        RbC1.setText("jRadioButton1");

        Candidato.add(RbC2);
        RbC2.setText("jRadioButton2");

        Candidato.add(RbC3);
        RbC3.setText("jRadioButton3");

        CbGrado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CbGrado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CbGradoActionPerformed(evt);
            }
        });

        CbEstudiante.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Estudiantes:");

        Candidato.add(RbVotoenBlanco);
        RbVotoenBlanco.setText("jRadioButton1");

        lbreloj.setText("jLabel4");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(CbGrado, 0, 148, Short.MAX_VALUE)
                                .addComponent(CbEstudiante, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 283, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(RbC3)
                            .addComponent(RbC2)
                            .addComponent(RbC1)
                            .addComponent(jLabel3)
                            .addComponent(RbVotoenBlanco, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbreloj, javax.swing.GroupLayout.PREFERRED_SIZE, 279, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(37, 37, 37))))
            .addGroup(layout.createSequentialGroup()
                .addGap(230, 230, 230)
                .addComponent(SaveVoto, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 420, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(CbGrado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addGap(15, 15, 15)
                        .addComponent(CbEstudiante, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(104, 104, 104)
                        .addComponent(SaveVoto))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(RbC1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(RbC2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RbC3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RbVotoenBlanco)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addComponent(lbreloj, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void CbGradoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CbGradoActionPerformed
        // TODO add your handling code here:
        filtrarEstudiantesPorGrado();
        CbEstudiante.setEnabled(true);
       
        
    }//GEN-LAST:event_CbGradoActionPerformed

    private void SaveVotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SaveVotoActionPerformed
        // TODO add your handling code here:
        guardarVoto();
    }//GEN-LAST:event_SaveVotoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        FlatXcodeDarkIJTheme.setup();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
            LocalDateTime fechaFin = LocalDateTime.now();
                new Votarr(null, fechaFin).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup Candidato;
    private javax.swing.JComboBox<String> CbEstudiante;
    private javax.swing.JComboBox<String> CbGrado;
    private javax.swing.JRadioButton RbC1;
    private javax.swing.JRadioButton RbC2;
    private javax.swing.JRadioButton RbC3;
    private javax.swing.JRadioButton RbVotoenBlanco;
    private javax.swing.JButton SaveVoto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel lbreloj;
    // End of variables declaration//GEN-END:variables

   
}
