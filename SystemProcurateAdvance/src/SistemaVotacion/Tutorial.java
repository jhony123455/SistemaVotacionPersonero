/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package SistemaVotacion;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author HP
 */
public class Tutorial {
    private static Tutorial instance;
    private Map<String, TutorialInfo> tutoriales;
    private JDialog tutorialDialog;

    private Tutorial() {
        initializeTutorials();
    }

    public static Tutorial getInstance() {
        if (instance == null) {
            instance = new Tutorial();
        }
        return instance;
    }

    private void initializeTutorials() {
        tutoriales = new HashMap<>();

        tutoriales.put("FrmRegistroEstudiantes", new TutorialInfo(
                "Registra un nuevo estudiante",
                new TutorialStep[]{
                    new TutorialStep(
                            "1. Llena el nombre y Apellido",
                            "/SistemaVotacion/Recursos/REGISTRO-UNO.png"
                    ),
                    new TutorialStep(
                            "2. Llena el campo del documento (tiene que ser 10 digitos)",
                            "/SistemaVotacion/Recursos/REGISTRO-DOS.png"
                    ),
                    new TutorialStep(
                            "3. Selecciona el grado del estudiante",
                            "/SistemaVotacion/Recursos/REGISTRO-TRES.png"
                    ),
                    new TutorialStep(
                            "4. Presione guardar y si todo funciono guardará exitosamente",
                            "/SistemaVotacion/Recursos/REGISTRO-CUATRO.png"
                    ),}
        ));

        tutoriales.put("RegistroCandidatos", new TutorialInfo(
                "Registra un candidato",
                new TutorialStep[]{
                    new TutorialStep(
                            "1. Presiona el boton para poder llenar la lista con los estudiantes que registraste (grado 11º)",
                            "/SistemaVotacion/Recursos/CANDIDATOS-UNO.png"
                    ),
                    new TutorialStep(
                            "2.Una vez hecho eso puedes seleccionar los estudiantes para registrarlos como candidatos",
                            "/SistemaVotacion/Recursos/CANDIDATOS-DOS.png"
                    ),
                    new TutorialStep(
                            "3. Se le pedira al candidato una propuesta, puedes colocar lo que quieras aqui",
                            "/SistemaVotacion/Recursos/CANDIDATOS-TRES.png"
                    ),
                    new TutorialStep(
                            "4. Al finalizar se asignará el candidato al campo de abajo el orden sera (01, 02 y 03)",
                            "/SistemaVotacion/Recursos/CANDIDATOS-CUATRO.png"
                    ),
                    new TutorialStep("5. Si tratas de registrar al mismo candidato no podrás, asi que no lo intentes",
                            "/SistemaVotacion/Recursos/CANDIDATOS-CINCO.png"),}
        ));

        tutoriales.put("MostrarEstudiantes", new TutorialInfo("Filtrar",
                new TutorialStep[]{
                    new TutorialStep("1. En este formulario podras ver los estudiantes y candidatos, tambien con posibilidad de exportar los "
                            + "resultados a excel, pero dependiendo del usuario esté pedirá clave",
                             "/SistemaVotacion/Recursos/FORMULARIO FILTRO.png"),
                    new TutorialStep("2. Al filtrar y buscar por grado solo aparecerán los estudiantes de dicho grado (cuando selecciones 11º saldran los candidatos)",
                            "/SistemaVotacion/Recursos/FILTRO-UNO.png"),}
        ));
        
         tutoriales.put("FrmCrearVotacion", new TutorialInfo(
                "Crea una Eleccion",
                new TutorialStep[]{
                    new TutorialStep(
                            "1. Este es el formulario donde seteas la eleccion aqui una vez pulses el boton para iniciar podrás votar",
                            "/SistemaVotacion/Recursos/FORMULARIO CREAR.png"
                    ),
                    new TutorialStep(
                            "2.Lo primero que vamos a hacer es seleccionar la fecha en la que queremos votar (no puedes crear elecciones en fechas anteriores a la actual)",
                            "/SistemaVotacion/Recursos/CREAR-UNO.png"
                    ),
                    new TutorialStep(
                            "3. Para setear la hora tienes opcion de formato 12h o 24h",
                            "/SistemaVotacion/Recursos/CREAR-DOS.png"
                    ),
                    new TutorialStep(
                            "4. En la vista 12h (defualt) tienes que seleccionar el uso horario AM/PM para poder realizar la eleccion, por ende no puedes crear elecciones en horas anteriores",
                            "/SistemaVotacion/Recursos/CREAR-TRES.png"
                    ),
                    new TutorialStep("5. Asi es la vista 24h, una vez seteado ambas horas (inicio y fin) podras iniciar la votacion",
                            "/SistemaVotacion/Recursos/CREAR-CUATRO.png"),}
        ));
         
         
        tutoriales.put("Votarr", new TutorialInfo(
                "Votando",
                new TutorialStep[]{
                    new TutorialStep(
                            "1. En este formulario estas votando por ende el tiempo que le asignaste es clave, en este formulario vas a tener deshabilitado el menu principal hasta que termine la votacion"
                                    + "tienes dos items para poder seleccionar el grado y los estudiantes a la izquierda y a la derecha los candidatos que registraste sumado al voto en blanco como opcion",
                            "/SistemaVotacion/Recursos/FORMULARIO VOTAR.png"
                    ),
                    new TutorialStep(
                            "2.La ventana solo se cerrará cuando finalice el periodo asignado",
                            "/SistemaVotacion/Recursos/VOTAR-UNO.png"
                    ),
                    new TutorialStep(
                            "3. Cuando Presiones el botón guardar si haz seleccionado al candidato tu voto se guardará",
                            "/SistemaVotacion/Recursos/VOTO-DOS.png"
                    ),
                    new TutorialStep(
                            "4. Un mismo Estudiante no puede participar en la misma eleccion 2 veces",
                            "/SistemaVotacion/Recursos/VOTO-TRES.png"
                    ),
                    new TutorialStep("5.Una vez finalizada la primera eleccion aparecerá este recuadro para decirte que ya ha acabó la votacion las siguientes ocasiones, sera al cerrar la ventana ",
                            "/SistemaVotacion/Recursos/VOTO-CUATRO.png"),}
        ));
        
        
          tutoriales.put("FrmResultados", new TutorialInfo(
                "Ver Resultados",
                new TutorialStep[]{
                    new TutorialStep(
                            "1. Una vez que finalice la primera eleccion tendrás acceso a esta ventana\n, tendras 3 formas de poder ver los registros\n, pero primero tendras que cargar los resultados de las elecciones que haz hecho\n"
                                    + "y despues seleccionar una, con eso los botones se habilitaran (dependiendo del tipo de usuario tendras accesso uno o más resultados)"
                                    + "además de poder exportar a excel dichos resultados",
                            "/SistemaVotacion/Recursos/FORMULARIO RESULTADOS.png"
                    ),
                    new TutorialStep(
                            "2.La primera forma de resultados es por candidato, aqui la tabla cambiará mostrando los votos de cada candidato incluyendo el voto en blanco",
                            "/SistemaVotacion/Recursos/RESULTADOS-UNO.png"
                    ),
                    new TutorialStep(
                            "3. La segunda es por grado, se habilitará un item para poder cambiar el grado y cada vez que presiones el boton se actualizará mostrando cuantos votos hubo en cada grado",
                            "/SistemaVotacion/Recursos/RESULTADOS-DOS.png"
                    ),
                    new TutorialStep(
                            "4. La ultima simplemente muestra la eleccion y los votos totales de la misma, además de ver la fecha en la cual se realizo",
                            "/SistemaVotacion/Recursos/RESULTADOS-TRES.png"
                    ),
                  }
        ));

        
        
        

    }

    public void mostrarTutorial(JFrame parentFrame, String nombreFormulario) {
        TutorialInfo info = tutoriales.get(nombreFormulario);
        if (info == null) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Tutorial no disponible para este formulario",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (tutorialDialog != null && tutorialDialog.isVisible()) {
            tutorialDialog.dispose();
        }

        tutorialDialog = new JDialog(parentFrame, "Tutorial: " + info.titulo, true);
        tutorialDialog.setLayout(new BorderLayout(10, 10));
        tutorialDialog.getContentPane().setBackground(new Color(26, 28, 44));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(new Color(26, 28, 44));

        // Título
        JLabel titleLabel = new JLabel(info.titulo);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

      
        for (TutorialStep paso : info.pasos) {
            
            JPanel stepPanel = new JPanel();
            stepPanel.setLayout(new BoxLayout(stepPanel, BoxLayout.Y_AXIS));
            stepPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(76, 106, 145), 1),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            stepPanel.setBackground(new Color(26, 28, 44));
            stepPanel.setMaximumSize(new Dimension(800, 1200)); 

            // Panel para el texto
            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BorderLayout());
            textPanel.setBackground(new Color(26, 28, 44));
            textPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

            JTextArea stepText = new JTextArea(paso.texto);
            stepText.setWrapStyleWord(true);
            stepText.setLineWrap(true);
            stepText.setEditable(false);
            stepText.setBackground(new Color(26, 28, 44));
            stepText.setForeground(Color.WHITE);
            stepText.setFont(new Font("Arial", Font.PLAIN, 14));

            textPanel.add(stepText, BorderLayout.CENTER);
            stepPanel.add(textPanel);

            // Panel para la imagen
            JPanel imagePanel = new JPanel();
            imagePanel.setBackground(new Color(26, 28, 44));
            imagePanel.setLayout(new BorderLayout());

            try {
                ImageIcon originalIcon = new ImageIcon(getClass().getResource(paso.imagePath));
                if (originalIcon.getIconWidth() <= 0) {
                    throw new Exception("Imagen no encontrada: " + paso.imagePath);
                }

                int maxWidth = 750;
                int maxHeight = 500;
                double scale = Math.min(
                        (double) maxWidth / originalIcon.getIconWidth(),
                        (double) maxHeight / originalIcon.getIconHeight()
                );
                int scaledWidth = (int) (originalIcon.getIconWidth() * scale);
                int scaledHeight = (int) (originalIcon.getIconHeight() * scale);

                Image scaledImage = originalIcon.getImage().getScaledInstance(
                        scaledWidth, scaledHeight, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                imagePanel.add(imageLabel, BorderLayout.CENTER);

            } catch (Exception e) {
                JLabel errorLabel = new JLabel("Error al cargar la imagen: " + e.getMessage());
                errorLabel.setForeground(Color.RED);
                imagePanel.add(errorLabel, BorderLayout.CENTER);
            }

            stepPanel.add(imagePanel);
            mainPanel.add(stepPanel);
            mainPanel.add(Box.createVerticalStrut(15));
        }

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(new Color(26, 28, 44));
        scrollPane.setBorder(null);

        JButton closeButton = new JButton("Entendido");
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setBackground(new Color(76, 106, 145));
        closeButton.setForeground(Color.WHITE);
        closeButton.addActionListener(e -> tutorialDialog.dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(new Color(26, 28, 44));
        buttonPanel.add(closeButton);

        tutorialDialog.add(scrollPane, BorderLayout.CENTER);
        tutorialDialog.add(buttonPanel, BorderLayout.SOUTH);

        tutorialDialog.setSize(850, 700);
        tutorialDialog.setLocationRelativeTo(parentFrame);
        tutorialDialog.setVisible(true);
    }
  
    private static class TutorialStep {
        String texto;
        String imagePath;

        TutorialStep(String texto, String imagePath) {
            this.texto = texto;
            this.imagePath = imagePath;
        }
    }


    private static class TutorialInfo {
        String titulo;
        TutorialStep[] pasos;

        TutorialInfo(String titulo, TutorialStep[] pasos) {
            this.titulo = titulo;
            this.pasos = pasos;
        }
    }
}
