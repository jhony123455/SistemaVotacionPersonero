/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package SistemaVotacion;


import SistemaVotacion.ConexionBD.DAO.CandidatosDAO;
import SistemaVotacion.ConexionBD.DAO.EleccionDAO;
import SistemaVotacion.ConexionBD.DAO.EstudiantesDAO;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.Toolkit;
import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import login.FrmLogin;
import login.Usuario;


/**
 *
 * @author Villar Manotas
 */
public class FrmPrincipal extends javax.swing.JFrame {

    
    private Map<String, FlatLaf> temasDisponibles;
    
    private int estudiantesGrado11;
    private int estudianteV;
    private EstudiantesDAO estudiantesdao;
    private CandidatosDAO candidatosdao;
    private FrmContenidoPrincipal contenidoPrincipal;
    private FrmRegistroEstudiantes registroEstudiantes;
    private MostrarEstudiantes mostrarEstudiantes;
    private RegistroCandidatos registroCandidatos;
    private FrmCrearVotación crearVotacion;
    private Usuario usuarioActual;
    private FrmLogin login;
    private FrmResultados resultados;
    private Votarr vota;
          

    public FrmPrincipal(Usuario user) {
        initComponents();
        
        setupLayout();
        icon();
        inicializarFormularios();
        estilos();
        this.usuarioActual = user; 
        btnRegistrarCandidatos.setEnabled(false);
        btnFiltrar.setEnabled(false);
        btnVotacion.setEnabled(false);
        btnResultados.setEnabled(false);
       
        estudiantesdao = new EstudiantesDAO();
        candidatosdao= new CandidatosDAO();
        actualizarEstado();
        registrarTemas();
        crearBotonCambiarTema();
        configurarInterfazUsuario();

        
          addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarSesion();
            }
        });

    }
    
    
       public void actualizarEstadoBotonResultados(boolean hayVotos) {
        btnResultados.setEnabled(hayVotos);
    }

   

    
     private void configurarInterfazUsuario() {
        if (usuarioActual != null) {
       
            verificarPrivilegios();
        } else {
           
            JOptionPane.showMessageDialog(this, "Error: No se ha iniciado sesión correctamente");
            this.dispose(); 
         
        }
    }

   
    
     private void verificarPrivilegios() {
        if (usuarioActual != null) {
            int idRol = usuarioActual.getIdRol();
            if(idRol==2){
                btnRegistrarEstudiantes.setEnabled(false);
            }
        } else {
            
            JOptionPane.showMessageDialog(this, "Error: Usuario no inicializado");
        }
    }


    private void setupLayout() {
        background.setLayout(new BorderLayout());
        header.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #0078D7;"
                + "arc: 0");
        background.add(header, BorderLayout.NORTH);
        content.setLayout(new BorderLayout());
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(menu, BorderLayout.WEST);
        centerPanel.add(content, BorderLayout.CENTER);
        background.add(centerPanel, BorderLayout.CENTER);
    }
    
   

    public void estilos() {
        lbTitulo.putClientProperty(FlatClientProperties.STYLE, ""
                + "font: bold 24 $h1.font;"
                + "foreground: #FFFFFF");

        lbMenu.putClientProperty(FlatClientProperties.STYLE, ""
                + "font: Garamond $h3.font");
        String buttonStyle = ""
                + "background: $Menu.background;"
                + "foreground: $Menu.foreground;"
                + "focusedBackground: darken($Menu.background,10%);"
                + "hoverBackground: darken($Menu.background,5%);"
                + "pressedBackground: darken($Menu.background,15%);"
                + "font: bold $h4.font;"
                + "margin: 5,10,5,10;"
                + "focusable: false";

        btnRegistrarEstudiantes.putClientProperty(FlatClientProperties.STYLE, buttonStyle);
        btnRegistrarCandidatos.putClientProperty(FlatClientProperties.STYLE, buttonStyle);
        btnFiltrar.putClientProperty(FlatClientProperties.STYLE, buttonStyle);
        btnVotacion.putClientProperty(FlatClientProperties.STYLE, buttonStyle);
        btnResultados.putClientProperty(FlatClientProperties.STYLE, buttonStyle);
        BtTheme.putClientProperty(FlatClientProperties.STYLE, buttonStyle);
        BtCerrar.putClientProperty(FlatClientProperties.STYLE, buttonStyle);

    }

    public void actualizarEstado() {
        estudiantesGrado11 = estudiantesdao.contarEstudiantesGrado11();
        estudianteV = estudiantesdao.contarTodosEstudiantes();
        int[] conteoEstudiantes = new int[11];
        for (int grado = 1; grado <= 11; grado++) {
            conteoEstudiantes[grado - 1] = estudiantesdao.contarEstudiantesGrado(grado);
        }
        btnRegistrarCandidatos.setEnabled(estudiantesGrado11 >= 3);
        btnFiltrar.setEnabled(estudianteV > 0);

        verificarCondicionesVotacion();
        
        
        try {
            EleccionDAO eleccionDAO = new EleccionDAO();
            boolean hayVotos = eleccionDAO.hayVotosRegistrados();
            if (hayVotos) {
                btnResultados.setEnabled(true);
            } else {
                btnResultados.setEnabled(false);
            }
        } catch (SQLException ex) {
            System.out.println("Error al verificar los votos: " + ex.getMessage());
        }

    }



    public void verificarCondicionesVotacion() {
        int candidatosRegistrados = candidatosdao.contarCandidatos();
       
        boolean hayEstudiantesEnCadaGrado = true;
        
        for (int grado = 1; grado <= 11; grado++) {
            if (estudiantesdao.contarEstudiantesGrado(grado) == 0) {
                hayEstudiantesEnCadaGrado = false;
                break;
            }
        }

        btnVotacion.setEnabled(hayEstudiantesEnCadaGrado && candidatosRegistrados >= 3);
        /*btnEliminarCandidatos.setEnabled(candidatosRegistrados > 0);*/
        
        System.out.println("Candidatos registrados: " + candidatosRegistrados);
        System.out.println("Botón de votación habilitado: " + btnVotacion.isEnabled());
    }

    private void inicializarFormularios() {
        contenidoPrincipal = new FrmContenidoPrincipal();
        registroEstudiantes = new FrmRegistroEstudiantes();
        registroEstudiantes.setFormularioPrincipal(this);
        mostrarEstudiantes = new MostrarEstudiantes();
        registroCandidatos = new RegistroCandidatos();
        registroCandidatos.setFormularioPrincipal(this);
        crearVotacion = new FrmCrearVotación(this);
        crearVotacion.setFormularioPrincipal(this);
        resultados = new FrmResultados(this);
        content.setLayout(new CardLayout());
        content.add(contenidoPrincipal.getBgPanel(), "contenidoPrincipal");
        content.add(registroEstudiantes.getPanel(), "registroEstudiantes");
        content.add(mostrarEstudiantes.getPanel(), "mostrarEstudiantes");
        content.add(registroCandidatos.getPanel(), "registroCandidatos");
        content.add(crearVotacion.getPanel(),       "crearVotacion");
        content.add(resultados.getPanel(),"resultados");
    }

    private void mostrarPanel(String nombrePanel) {
        CardLayout cl = (CardLayout) (content.getLayout());
        cl.show(content, nombrePanel);
    }

    private void icon() {
        Image icon = Toolkit.getDefaultToolkit().getImage(getClass().getResource("/SistemaVotacion/Recursos/colegio.png"));
        setIconImage(icon);
    }

    public void cargarContenidoPrincipal() {
        mostrarPanel("contenidoPrincipal");
    }

    public void cargarPanelRegistroEstudiantes() {
        mostrarPanel("registroEstudiantes");
    }

    public void cargarTablas() {
        mostrarPanel("mostrarEstudiantes");
    }

    public void cargarCandidatos() {
        mostrarPanel("registroCandidatos");
    }
    
    public void cargarVotacion(){
        mostrarPanel("crearVotacion");
    }
    
    public void mostrarResultados(){
        mostrarPanel("resultados");
    }
    
    private void registrarTemas() {
        temasDisponibles = new LinkedHashMap<>();
        temasDisponibles.put("Light", new FlatLightLaf());
        temasDisponibles.put("Dark", new FlatDarkLaf());
        temasDisponibles.put("IntelliJ", new FlatIntelliJLaf());
        temasDisponibles.put("Flat Moonlight", new FlatMoonlightIJTheme());
        temasDisponibles.put("GitHubTheme", new FlatGitHubIJTheme());
        temasDisponibles.put("purpura", new FlatDarkPurpleIJTheme());
        temasDisponibles.put("GradiantoFucshia", new FlatGradiantoDarkFuchsiaIJTheme());
    }
    
    private void crearBotonCambiarTema() {
       

        BtTheme.addActionListener(e -> {
            String[] nombresTemas = temasDisponibles.keySet().toArray(new String[0]);

            String temaSeleccionado = (String) JOptionPane.showInputDialog(
                this,
                "Seleccione un tema:",
                "Cambiar Tema",
                JOptionPane.QUESTION_MESSAGE,
                null,
                nombresTemas,
                nombresTemas[0]
            );

            if (temaSeleccionado != null) {
                changeTheme(temaSeleccionado);
            }
        });
        
    }
    
    private void changeTheme(String temaSeleccionado) {
        try {
            FlatLaf tema = temasDisponibles.get(temaSeleccionado);
            if (tema != null) {
                System.out.println("Aplicando tema: " + temaSeleccionado);

             
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

                
                UIManager.setLookAndFeel(tema);


                SwingUtilities.updateComponentTreeUI(this);

           
          
                this.repaint();  
            } else {
                throw new IllegalArgumentException("Tema no encontrado: " + temaSeleccionado);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al aplicar el tema: " + temaSeleccionado);
        }
    }

    
   
    
  private void cerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Estás seguro de que quieres cerrar sesión?",
                "Confirmar Cierre de Sesión",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            this.dispose(); 
            login = new FrmLogin();
            
            login.setVisible(true);
        }
    }
  
    public void habilitarBotonResultados() {
        btnResultados.setEnabled(true);
    }

    public void deshabilitarBotonResultados() {
        btnResultados.setEnabled(false);
    }


 
    

       
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        background = new javax.swing.JPanel();
        menu = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        lbMenu = new javax.swing.JLabel();
        btnRegistrarEstudiantes = new javax.swing.JButton();
        btnRegistrarCandidatos = new javax.swing.JButton();
        btnFiltrar = new javax.swing.JButton();
        btnVotacion = new javax.swing.JButton();
        btnResultados = new javax.swing.JButton();
        BtCerrar = new javax.swing.JButton();
        BtTheme = new javax.swing.JButton();
        header = new javax.swing.JPanel();
        lbTitulo = new javax.swing.JLabel();
        content = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setIconImage(getIconImage());
        setMinimumSize(new java.awt.Dimension(1020, 640));

        background.setBackground(new java.awt.Color(255, 255, 255));

        menu.setBackground(new java.awt.Color(13, 71, 161));
        menu.setPreferredSize(new java.awt.Dimension(270, 640));

        jSeparator1.setForeground(new java.awt.Color(255, 255, 255));

        lbMenu.setForeground(new java.awt.Color(255, 255, 255));
        lbMenu.setText("Menu");

        btnRegistrarEstudiantes.setBackground(new java.awt.Color(0, 102, 204));
        btnRegistrarEstudiantes.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnRegistrarEstudiantes.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrarEstudiantes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/libro.png"))); // NOI18N
        btnRegistrarEstudiantes.setText("Registro de estudiantes");
        btnRegistrarEstudiantes.setBorder(null);
        btnRegistrarEstudiantes.setBorderPainted(false);
        btnRegistrarEstudiantes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnRegistrarEstudiantes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRegistrarEstudiantes.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnRegistrarEstudiantes.setIconTextGap(10);
        btnRegistrarEstudiantes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarEstudiantesActionPerformed(evt);
            }
        });

        btnRegistrarCandidatos.setBackground(new java.awt.Color(0, 102, 204));
        btnRegistrarCandidatos.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnRegistrarCandidatos.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrarCandidatos.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/seleccion.png"))); // NOI18N
        btnRegistrarCandidatos.setText("Registro de candidatos");
        btnRegistrarCandidatos.setBorder(null);
        btnRegistrarCandidatos.setBorderPainted(false);
        btnRegistrarCandidatos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnRegistrarCandidatos.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnRegistrarCandidatos.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnRegistrarCandidatos.setIconTextGap(10);
        btnRegistrarCandidatos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRegistrarCandidatosActionPerformed(evt);
            }
        });

        btnFiltrar.setBackground(new java.awt.Color(0, 102, 204));
        btnFiltrar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnFiltrar.setForeground(new java.awt.Color(255, 255, 255));
        btnFiltrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/Filtro.png"))); // NOI18N
        btnFiltrar.setText("Filtrar estudiantes y candidatos");
        btnFiltrar.setBorder(null);
        btnFiltrar.setBorderPainted(false);
        btnFiltrar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFiltrar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnFiltrar.setIconTextGap(10);
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        btnVotacion.setBackground(new java.awt.Color(0, 102, 204));
        btnVotacion.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnVotacion.setForeground(new java.awt.Color(255, 255, 255));
        btnVotacion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/votacion.png"))); // NOI18N
        btnVotacion.setText("Votación");
        btnVotacion.setBorder(null);
        btnVotacion.setBorderPainted(false);
        btnVotacion.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnVotacion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnVotacion.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnVotacion.setIconTextGap(10);
        btnVotacion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVotacionActionPerformed(evt);
            }
        });

        btnResultados.setBackground(new java.awt.Color(0, 102, 204));
        btnResultados.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        btnResultados.setForeground(new java.awt.Color(255, 255, 255));
        btnResultados.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/Resultado.png"))); // NOI18N
        btnResultados.setText("Resultados");
        btnResultados.setBorder(null);
        btnResultados.setBorderPainted(false);
        btnResultados.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnResultados.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnResultados.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnResultados.setIconTextGap(10);
        btnResultados.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResultadosActionPerformed(evt);
            }
        });

        BtCerrar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/cerrar-sesion.png"))); // NOI18N
        BtCerrar.setText("Cerrar Sesion");
        BtCerrar.setBorder(null);
        BtCerrar.setBorderPainted(false);
        BtCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtCerrarActionPerformed(evt);
            }
        });

        BtTheme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/paleta-de-color.png"))); // NOI18N
        BtTheme.setText("Cambiar el tema");
        BtTheme.setBorder(null);

        javax.swing.GroupLayout menuLayout = new javax.swing.GroupLayout(menu);
        menu.setLayout(menuLayout);
        menuLayout.setHorizontalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(BtTheme, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(menuLayout.createSequentialGroup()
                .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, menuLayout.createSequentialGroup()
                        .addGap(115, 115, 115)
                        .addComponent(lbMenu))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, menuLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnRegistrarEstudiantes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .addComponent(btnResultados, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .addComponent(btnVotacion, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .addComponent(btnFiltrar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .addComponent(btnRegistrarCandidatos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE)
                    .addComponent(BtCerrar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        menuLayout.setVerticalGroup(
            menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(menuLayout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addComponent(lbMenu)
                .addGap(6, 6, 6)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(menuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnRegistrarEstudiantes, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(menuLayout.createSequentialGroup()
                        .addGap(200, 200, 200)
                        .addComponent(btnResultados, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuLayout.createSequentialGroup()
                        .addGap(150, 150, 150)
                        .addComponent(btnVotacion, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuLayout.createSequentialGroup()
                        .addGap(100, 100, 100)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(menuLayout.createSequentialGroup()
                        .addGap(50, 50, 50)
                        .addComponent(btnRegistrarCandidatos, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BtTheme, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtCerrar, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(187, Short.MAX_VALUE))
        );

        header.setBackground(new java.awt.Color(25, 118, 210));

        lbTitulo.setForeground(new java.awt.Color(255, 255, 255));
        lbTitulo.setText("Sistema de votación para personero");

        javax.swing.GroupLayout headerLayout = new javax.swing.GroupLayout(header);
        header.setLayout(headerLayout);
        headerLayout.setHorizontalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lbTitulo)
                .addGap(289, 289, 289))
        );
        headerLayout.setVerticalGroup(
            headerLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headerLayout.createSequentialGroup()
                .addContainerGap(47, Short.MAX_VALUE)
                .addComponent(lbTitulo)
                .addGap(46, 46, 46))
        );

        content.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout contentLayout = new javax.swing.GroupLayout(content);
        content.setLayout(contentLayout);
        contentLayout.setHorizontalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 970, Short.MAX_VALUE)
        );
        contentLayout.setVerticalGroup(
            contentLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout backgroundLayout = new javax.swing.GroupLayout(background);
        background.setLayout(backgroundLayout);
        backgroundLayout.setHorizontalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundLayout.createSequentialGroup()
                .addComponent(menu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(header, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        backgroundLayout.setVerticalGroup(
            backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(menu, javax.swing.GroupLayout.DEFAULT_SIZE, 657, Short.MAX_VALUE)
            .addGroup(backgroundLayout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(backgroundLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(header, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(backgroundLayout.createSequentialGroup()
                        .addGap(102, 102, 102)
                        .addComponent(content, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(background, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnRegistrarEstudiantesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarEstudiantesActionPerformed
        // TODO add your handling code here:
        cargarPanelRegistroEstudiantes();
        
    }//GEN-LAST:event_btnRegistrarEstudiantesActionPerformed

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        // TODO add your handling code here:
        cargarTablas();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void btnRegistrarCandidatosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarCandidatosActionPerformed
        // TODO add your handling code here:
        cargarCandidatos();
    }//GEN-LAST:event_btnRegistrarCandidatosActionPerformed

    private void btnVotacionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVotacionActionPerformed
        // TODO add your handling code here:
        cargarVotacion();
    }//GEN-LAST:event_btnVotacionActionPerformed

    private void BtCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtCerrarActionPerformed
        cerrarSesion();
    }//GEN-LAST:event_BtCerrarActionPerformed

    private void btnResultadosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResultadosActionPerformed
        // TODO add your handling code here:
        mostrarResultados();
    }//GEN-LAST:event_btnResultadosActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
         try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        /* Set the Nimbus look and feel */
        

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new FrmPrincipal(null).setVisible(true);
                 try {
                EleccionDAO eleccionDAO = new EleccionDAO();
                if (eleccionDAO.hayEleccionActiva()) {
                    JOptionPane.showMessageDialog(null, "No se puede crear una nueva elección mientras haya una activa.");
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error al verificar el estado de las elecciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BtCerrar;
    private javax.swing.JButton BtTheme;
    private javax.swing.JPanel background;
    private javax.swing.JButton btnFiltrar;
    private javax.swing.JButton btnRegistrarCandidatos;
    private javax.swing.JButton btnRegistrarEstudiantes;
    private javax.swing.JButton btnResultados;
    private javax.swing.JButton btnVotacion;
    private javax.swing.JPanel content;
    private javax.swing.JPanel header;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JLabel lbMenu;
    private javax.swing.JLabel lbTitulo;
    private javax.swing.JPanel menu;
    // End of variables declaration//GEN-END:variables
}
