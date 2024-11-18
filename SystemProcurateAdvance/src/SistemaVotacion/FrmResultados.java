package SistemaVotacion;

import SistemaVotacion.ConexionBD.DAO.EleccionDAO;
import SistemaVotacion.ConexionBD.DAO.GradosDAO;
import SistemaVotacion.ConexionBD.DAO.ResultadoDAO;
import SistemaVotacion.ConexionBD.DAO.UsuarioDAO;
import SistemaVotacion.Modelos.Candidatos;
import SistemaVotacion.Modelos.Eleccion;
import SistemaVotacion.Modelos.Grados;
import SistemaVotacion.Modelos.Resultados;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import login.Sesion;
import login.Usuario;

public class FrmResultados extends javax.swing.JFrame {

    String[] encabezado = {"Nombre", "Apellido", "Votos Obtenidos", "porcentaje"};
    String[] encabezado2 = {"NºEleccion", "Total_Votos", "Fecha_Conteo"};
    private FrmPrincipal frmPrincipal;
    GradosDAO gradosdao;
    Eleccion eleccion;

    public FrmResultados(FrmPrincipal principal) {
        initComponents();
        this.frmPrincipal = principal;
        gradosdao = new GradosDAO();

        cargarElecciones();
        llenarComboGrados();
        CbGrados.setVisible(false);

        actualizarEstadoBotonResultados();
        configurarInterfazUsuario();
        estilos();

    }

    private void configurarInterfazUsuario() {
        if (Sesion.haySesionActiva()) {
            verificarPrivilegios();
        } else {
            JOptionPane.showMessageDialog(this, "Error: No se ha iniciado sesión correctamente");
            this.dispose();
        }
    }

    private void verificarPrivilegios() {
        if (Sesion.usuarioActual != null) {
            if (!Sesion.esAdmin()) {
                BtPorGrado.setEnabled(false);
                BtGeneral.setEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Error: Usuario no inicializado");
        }
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
                System.out.println("No hay elecciones disponibles");
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

            DefaultTableModel model = new DefaultTableModel(0, encabezado.length) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            model.setColumnIdentifiers(encabezado);

            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay resultados disponibles para esta elección",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int totalVotos = resultados.stream()
                    .flatMap(r -> r.getCandidatos().stream())
                    .mapToInt(Candidatos::getVotosObtenidos)
                    .sum();

            resultados.sort((r1, r2) -> {
                int votos1 = r1.getCandidatos().get(0).getVotosObtenidos();
                int votos2 = r2.getCandidatos().get(0).getVotosObtenidos();
                return Integer.compare(votos2, votos1);
            });

            for (Resultados resultado : resultados) {
                for (Candidatos candidato : resultado.getCandidatos()) {
                    String nombre = "Voto en blanco".equals(candidato.getEstudiante().getNombre())
                            ? "Voto en Blanco"
                            : candidato.getEstudiante().getNombre();
                    String apellido = candidato.getEstudiante().getApellido();
                    int votosObtenidos = candidato.getVotosObtenidos();

                    double porcentaje = totalVotos > 0
                            ? (votosObtenidos * 100.0) / totalVotos
                            : 0.0;

                    model.addRow(new Object[]{
                        nombre,
                        apellido,
                        votosObtenidos,
                        String.format("%.2f%%", porcentaje)
                    });
                }
            }

            TableResultados.setModel(model);

            TableResultados.getColumnModel().getColumn(0).setCellRenderer(new Celdas());

            TableResultados.getColumnModel().getColumn(0).setPreferredWidth(150);
            TableResultados.getColumnModel().getColumn(1).setPreferredWidth(150);
            TableResultados.getColumnModel().getColumn(2).setPreferredWidth(100);
            TableResultados.getColumnModel().getColumn(3).setPreferredWidth(100);

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            TableResultados.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            TableResultados.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);

            TableResultados.setVisible(true);
            jScrollPane1.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar los resultados: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarResultadosPorGrado(int eleccionId, int gradoId) throws SQLException {
        ResultadoDAO resultadosdao = new ResultadoDAO();
        List<Resultados> resultados = resultadosdao.obtenerResultadosPorGrado(eleccionId, gradoId);
        DefaultTableModel model = new DefaultTableModel();
        model.setColumnIdentifiers(encabezado);
        for (Resultados resultado : resultados) {
            for (Candidatos candidato : resultado.getCandidatos()) {
                String nombre = "Voto en Blanco".equals(candidato.getEstudiante().getNombre()) ? "Voto en Blanco" : candidato.getEstudiante().getNombre();
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

    public void exportarResultadosAExcel(int eleccionId, String rutaBase) {
        try {
            ResultadoDAO resultadosdao = new ResultadoDAO();

            List<Resultados> resultadosPorCandidato = resultadosdao.obtenerResultadosPorCandidato(eleccionId);
            List<Resultados> resultadosTotales = resultadosdao.obtenerResultadosTotales(eleccionId);

            String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String rutaProyecto = System.getProperty("user.dir");
            String rutaCompleta = rutaProyecto + File.separator + rutaBase + "_" + timestamp + ".xls";

            WritableWorkbook workbook = jxl.Workbook.createWorkbook(new File(rutaCompleta));

            // Primera hoja: Resultados por Candidato
            WritableSheet sheetCandidatos = workbook.createSheet("Resultados por Candidato", 0);
            String[] encabezadoCandidatos = {"Nombre", "Apellido", "Votos Obtenidos"};

            for (int i = 0; i < encabezadoCandidatos.length; i++) {
                Label label = new Label(i, 0, encabezadoCandidatos[i]);
                sheetCandidatos.addCell(label);
            }

            int rowCandidatos = 1;
            for (Resultados resultado : resultadosPorCandidato) {
                for (Candidatos candidato : resultado.getCandidatos()) {
                    String nombre = "Voto en blanco".equals(candidato.getEstudiante().getNombre())
                            ? "Voto en Blanco"
                            : candidato.getEstudiante().getNombre();

                    sheetCandidatos.addCell(new Label(0, rowCandidatos, nombre));
                    sheetCandidatos.addCell(new Label(1, rowCandidatos, candidato.getEstudiante().getApellido()));

                    jxl.write.Number numberCell = new jxl.write.Number(2, rowCandidatos, candidato.getVotosObtenidos());
                    sheetCandidatos.addCell(numberCell);
                    rowCandidatos++;
                }
            }

            // Segunda hoja: Resultados Totales
            WritableSheet sheetTotales = workbook.createSheet("Resultados Totales", 1);
            String[] encabezadoTotales = {"NºEleccion", "Total_Votos", "Fecha_Conteo"};

            for (int i = 0; i < encabezadoTotales.length; i++) {
                Label label = new Label(i, 0, encabezadoTotales[i]);
                sheetTotales.addCell(label);
            }

            int rowTotales = 1;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Resultados resultado : resultadosTotales) {
                jxl.write.Number idEleccion = new jxl.write.Number(0, rowTotales, resultado.getEleccion().getIdeleccion());
                jxl.write.Number totalVotos = new jxl.write.Number(1, rowTotales, resultado.getTotalVotos());
                Label fecha = new Label(2, rowTotales, resultado.getFechaConteo().format(formatter));

                sheetTotales.addCell(idEleccion);
                sheetTotales.addCell(totalVotos);
                sheetTotales.addCell(fecha);
                rowTotales++;
            }

            workbook.write();
            workbook.close();

            JOptionPane.showMessageDialog(null, "Resultados exportados exitosamente", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al exportar los resultados: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void verificarYExportarResultados(int eleccionId, String rutaBase) {
        if (Sesion.haySesionActiva()) {
            if (Sesion.esAdmin()) {
                exportarResultadosAExcel(eleccionId, rutaBase);
            } else {
                loginParaExportarResultados(eleccionId, rutaBase);
            }
        } else {
            loginParaExportarResultados(eleccionId, rutaBase);
        }
    }

    private void loginParaExportarResultados(int eleccionId, String rutaBase) {
        JTextField usr = new JTextField();
        int actionLogin = JOptionPane.showConfirmDialog(null, usr, "Usuario", JOptionPane.OK_CANCEL_OPTION);

        if (actionLogin > 0) {
            JOptionPane.showMessageDialog(null, "Operación cancelada");
            return;
        }

        JPasswordField pwd = new JPasswordField();
        int action = JOptionPane.showConfirmDialog(null, pwd, "Contraseña", JOptionPane.OK_CANCEL_OPTION);

        if (action > 0) {
            JOptionPane.showMessageDialog(null, "Operación cancelada");
            return;
        }

        String usuario = usr.getText();
        String pass = new String(pwd.getPassword());
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario user = usuarioDAO.autenticar(usuario, pass);

        if (user != null && Sesion.esAdmin()) {
            exportarResultadosAExcel(eleccionId, rutaBase);
        } else {
            JOptionPane.showMessageDialog(null, "No tienes permisos de administrador para exportar",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public JPanel getPanel() {
        return PanelResult;
    }

    public void estilos() {

        String comboBoxStyle = ""
                + "background: #34495E;"
                + "foreground: #FFFFFF;"
                + "arc: 5";

        CbGrados.putClientProperty(FlatClientProperties.STYLE, comboBoxStyle);
        CbElecciones.putClientProperty(FlatClientProperties.STYLE, comboBoxStyle);

        String buttonStyle = ""
                + "background: #2C3E50;"
                + "foreground: #B0B0B0;"
                + "focusedBackground: darken(#2C3E50,10%);"
                + "hoverBackground: darken(#2C3E50,5%);"
                + "pressedBackground: darken(#2C3E50,15%);"
                + "font: bold 12 'Segoe UI';"
                + "arc: 8";

        BtCargar.putClientProperty(FlatClientProperties.STYLE, buttonStyle);
        BtTotal.putClientProperty(FlatClientProperties.STYLE, buttonStyle);
        BtPorGrado.putClientProperty(FlatClientProperties.STYLE, buttonStyle);
        BtGeneral.putClientProperty(FlatClientProperties.STYLE, buttonStyle);

        BtExportar.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #3498DB;"
                + "foreground: #FFFFFF;"
                + "focusedBackground: darken(#3498DB,10%);"
                + "hoverBackground: darken(#3498DB,5%);"
                + "pressedBackground: darken(#3498DB,15%);"
                + "font: bold 12 'Segoe UI';"
                + "arc: 8");

        PanelResult.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #2C3E50;"
                + "arc: 5");

        TableResultados.putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #1E2837;"
                + "foreground: #FFFFFF;");

        TableResultados.getTableHeader().putClientProperty(FlatClientProperties.STYLE, ""
                + "background: #2C3E50;"
                + "foreground: #FFFFFF;"
                + "font: bold 12 'Segoe UI';"
                + "separatorColor: #34495E;"
                + "hoverBackground: darken(#2C3E50, 5%)");

        TableResultados.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (isSelected) {
                    c.setBackground(new Color(52, 152, 219));
                    c.setForeground(Color.WHITE);
                } else {
                    if (row % 2 == 0) {
                        c.setBackground(new Color(30, 40, 55));
                    } else {
                        c.setBackground(new Color(35, 45, 60));
                    }
                    c.setForeground(Color.WHITE);
                }

                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                return c;
            }
        });

        TableResultados.setRowHeight(30);
        TableResultados.setShowGrid(false);
        TableResultados.setIntercellSpacing(new Dimension(0, 0));
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
        BtHelp = new SistemaVotacion.MyButton();

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
                "Default", "Default", "Default", "Default"
            }
        ));
        jScrollPane1.setViewportView(TableResultados);

        BtCargar.setText("Cargar Elecciones");
        BtCargar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtCargarActionPerformed(evt);
            }
        });

        BtHelp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/SistemaVotacion/Recursos/signo-de-interrogacion.png"))); // NOI18N
        BtHelp.setRadius(500);
        BtHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BtHelpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelResultLayout = new javax.swing.GroupLayout(PanelResult);
        PanelResult.setLayout(PanelResultLayout);
        PanelResultLayout.setHorizontalGroup(
            PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelResultLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelResultLayout.createSequentialGroup()
                        .addComponent(BtPorGrado, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CbGrados, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BtTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelResultLayout.createSequentialGroup()
                        .addComponent(BtCargar, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(CbElecciones, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BtGeneral, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BtExportar, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BtHelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        PanelResultLayout.setVerticalGroup(
            PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelResultLayout.createSequentialGroup()
                .addGroup(PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelResultLayout.createSequentialGroup()
                        .addGap(57, 57, 57)
                        .addGroup(PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(BtCargar)
                            .addComponent(CbElecciones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(BtTotal)
                        .addGap(26, 26, 26)
                        .addGroup(PanelResultLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(CbGrados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BtPorGrado))
                        .addGap(37, 37, 37)
                        .addComponent(BtGeneral)
                        .addGap(30, 30, 30)
                        .addComponent(BtExportar))
                    .addComponent(BtHelp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        String eleccionSeleccionada = (String) CbElecciones.getSelectedItem();

        if (eleccionSeleccionada == null || eleccionSeleccionada.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor seleccione una elección",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int idEleccion = extraerIdEleccion(eleccionSeleccionada);
            verificarYExportarResultados(idEleccion, "ResultadosEleccion");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al exportar los resultados: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_BtExportarActionPerformed

    private void BtCargarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtCargarActionPerformed
        // TODO add your handling code here:
        cargarElecciones();
    }//GEN-LAST:event_BtCargarActionPerformed

    private void BtHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BtHelpActionPerformed
        // TODO add your handling code here:
        Tutorial.getInstance().mostrarTutorial(this, "FrmResultados");
    }//GEN-LAST:event_BtHelpActionPerformed

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
    private SistemaVotacion.MyButton BtHelp;
    private javax.swing.JButton BtPorGrado;
    private javax.swing.JButton BtTotal;
    private javax.swing.JComboBox<String> CbElecciones;
    private javax.swing.JComboBox<String> CbGrados;
    private javax.swing.JPanel PanelResult;
    private javax.swing.JTable TableResultados;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables
}
