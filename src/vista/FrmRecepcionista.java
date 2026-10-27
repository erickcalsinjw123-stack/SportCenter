package vista;

import com.github.lgooddatepicker.components.DatePicker;
import com.github.lgooddatepicker.components.DatePickerSettings;

import dao.CanchaDAO;
import dao.ClienteDAO;
import dao.ReservaDAO;

import modelo.Cancha;
import modelo.Reserva;
import modelo.Usuario;

import servicio.ReservaService;

import util.Sesion;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JTable;
import javax.swing.JScrollPane;

  

    public class FrmRecepcionista extends javax.swing.JFrame {

        private boolean modoEdicion = false;
        private int idReservaEdicion = -1;
        private DatePicker dpFecha;

        private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FrmRecepcionista.class.getName());

        /**
         * Creates new form FrmRecepcionista
         */
        private final javax.swing.table.TableRowSorter<DefaultTableModel> ordenador
                = new javax.swing.table.TableRowSorter<>();

        public FrmRecepcionista() {
            setSize(1200, 850);
            setMinimumSize(new java.awt.Dimension(1100, 750));
            setLocationRelativeTo(null);

            initComponents();

            configurarFormulario();
            configurarBuscador();
            cargarCanchas();
            configurarEventosPrecio();
            cargarReservas();
        }

        private void configurarEventosPrecio() {

            javax.swing.event.DocumentListener listener
                    = new javax.swing.event.DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    calcularPrecio();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    calcularPrecio();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    calcularPrecio();
                }
            };

            txtFecha.getDocument().addDocumentListener(listener);
            txtHoraInicio.getDocument().addDocumentListener(listener);
            txtHoraFin.getDocument().addDocumentListener(listener);

            cboCancha.addActionListener(e -> calcularPrecio());
        }

        private void configurarFormulario() {
            cboDisciplina.setModel(new DefaultComboBoxModel<>(
                    new String[]{
                        "Fútbol",
                        "Vóley"
                    }
            ));

            txtNombre.setText("");
            txtApellido.setText("");
            txtTelefono.setText("");
            txtEmail.setText("");
            txtFecha.setText("");
            txtHoraInicio.setText("");
            txtHoraFin.setText("");
            txtPrecioTotal.setText("");

            txtPrecioTotal.setEditable(false);

            DefaultTableModel modelo = new DefaultTableModel(
                    new String[]{
                        "ID", "Cliente", "Teléfono", "Disciplina",
                        "Cancha", "Fecha inicio", "Fecha fin",
                        "Precio total", "Estado"
                    }, 0
            ) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };

            tblReservas.setModel(modelo);
            ordenador.setModel(modelo);
            tblReservas.setRowSorter(ordenador);
            tblReservas.setSelectionMode(
                    ListSelectionModel.SINGLE_SELECTION
            );

            tblReservas.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);

            int[] anchos = {40, 125, 100, 90, 105, 145, 145, 105};

            for (int i = 0; i < anchos.length; i++) {
                tblReservas.getColumnModel()
                        .getColumn(i)
                        .setPreferredWidth(anchos[i]);
            }

        }

        private void cargarReservas() {
            try {
                ReservaService servicio = new ReservaService();
                List<Reserva> reservas = servicio.listar();

                DefaultTableModel modelo
                        = (DefaultTableModel) tblReservas.getModel();

                modelo.setRowCount(0);

                for (Reserva r : reservas) {
                    modelo.addRow(new Object[]{
                        r.getIdReserva(),
                        r.getNombreCliente() + " " + r.getApellidoCliente(),
                        r.getTelefonoCliente(),
                        r.getDeporte(),
                        r.getNombreCancha(),
                        r.getFechaInicio(),
                        r.getFechaFin(),
                        r.getPrecioTotal(),
                        r.getEstado()
                    });
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al cargar las reservas: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        private void consultarReserva() {
            String busqueda = JOptionPane.showInputDialog(
                    this,
                    "Ingrese el ID de la reserva, nombre del cliente o teléfono:",
                    "Consultar reserva",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (busqueda == null || busqueda.trim().isEmpty()) {
                return;
            }

            busqueda = busqueda.trim();

            try {
                ReservaService servicio = new ReservaService();
                List<Reserva> reservas = servicio.listar();

                DefaultTableModel modelo = new DefaultTableModel(
                        new Object[]{
                            "ID", "Cliente", "Teléfono", "Disciplina",
                            "Cancha", "Fecha inicio", "Fecha fin",
                            "Precio total", "Estado"
                        }, 0
                ) {
                    @Override
                    public boolean isCellEditable(int fila, int columna) {
                        return false;
                    }
                };

                for (Reserva r : reservas) {
                    String cliente = r.getNombreCliente() + " "
                            + r.getApellidoCliente();

                    boolean coincideId = String.valueOf(r.getIdReserva())
                            .equals(busqueda);

                    boolean coincideCliente = cliente.toLowerCase()
                            .contains(busqueda.toLowerCase());

                    boolean coincideTelefono = r.getTelefonoCliente() != null
                            && r.getTelefonoCliente().contains(busqueda);

                    if (coincideId || coincideCliente || coincideTelefono) {
                        modelo.addRow(new Object[]{
                            r.getIdReserva(),
                            cliente,
                            r.getTelefonoCliente(),
                            r.getDeporte(),
                            r.getNombreCancha(),
                            r.getFechaInicio(),
                            r.getFechaFin(),
                            r.getPrecioTotal(),
                            r.getEstado()
                        });
                    }
                }

                if (modelo.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "No se encontraron reservas.",
                            "Sin resultados",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                    return;
                }

                JTable tabla = new JTable(modelo);
                tabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                tabla.setSelectionMode(
                        ListSelectionModel.SINGLE_SELECTION
                );

                JScrollPane scroll = new JScrollPane(tabla);
                scroll.setPreferredSize(new java.awt.Dimension(850, 180));

                JOptionPane.showMessageDialog(
                        this,
                        scroll,
                        "Resultados de la consulta",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al consultar las reservas: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        private void configurarBuscador() {
            txtBuscarReserva.setText("");

            txtBuscarReserva.getDocument().addDocumentListener(
                    new DocumentListener() {
                private void filtrar() {
                    String texto = txtBuscarReserva.getText().trim();

                    if (texto.isEmpty()) {
                        ordenador.setRowFilter(null);
                    } else {
                        ordenador.setRowFilter(
                                javax.swing.RowFilter.regexFilter(
                                        "(?i)" + java.util.regex.Pattern.quote(texto)
                                )
                        );
                    }
                }

                @Override
                public void insertUpdate(DocumentEvent e) {
                    filtrar();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    filtrar();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    filtrar();
                }
            }
            );
        }

        private void cargarCanchas() {
            try {
                CanchaDAO dao = new CanchaDAO();
                List<Cancha> canchas = dao.listar();

                cboCancha.removeAllItems();

                for (Cancha cancha : canchas) {
                    if (Cancha.DISPONIBLE.equals(cancha.getEstado())) {
                        cboCancha.addItem(
                                cancha.getIdCancha() + " - " + cancha.getNombre()
                        );
                    }
                }

                if (cboCancha.getItemCount() == 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "No hay canchas disponibles.",
                            "Información",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        this,
                        "Error al cargar las canchas:\n" + e.getMessage(),
                        "Error de base de datos",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }

        private void calcularPrecio() {
            try {
                String seleccion = (String) cboCancha.getSelectedItem();

                if (seleccion == null
                        || txtFecha.getText().trim().isEmpty()
                        || txtHoraInicio.getText().trim().isEmpty()
                        || txtHoraFin.getText().trim().isEmpty()) {
                    txtPrecioTotal.setText("");
                    return;
                }

                int idCancha = Integer.parseInt(
                        seleccion.substring(0, seleccion.indexOf(" - "))
                );

                CanchaDAO dao = new CanchaDAO();
                List<Cancha> canchas = dao.listar();
                Cancha canchaSeleccionada = null;

                for (Cancha cancha : canchas) {
                    if (cancha.getIdCancha() == idCancha) {
                        canchaSeleccionada = cancha;
                        break;
                    }
                }

                if (canchaSeleccionada == null) {
                    txtPrecioTotal.setText("");
                    return;
                }

                DateTimeFormatter formato
                        = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

                LocalDateTime inicio = LocalDateTime.parse(
                        txtFecha.getText().trim() + " "
                        + txtHoraInicio.getText().trim(), formato
                );

                LocalDateTime fin = LocalDateTime.parse(
                        txtFecha.getText().trim() + " "
                        + txtHoraFin.getText().trim(), formato
                );

                // La reserva debe durar al menos una hora.
                long minutos = Duration.between(inicio, fin).toMinutes();

                if (!fin.isAfter(inicio) || minutos < 60) {
                    txtPrecioTotal.setText("");
                    return;
                }

                BigDecimal precio = canchaSeleccionada.getPrecioHora()
                        .multiply(BigDecimal.valueOf(minutos))
                        .divide(
                                BigDecimal.valueOf(60),
                                2,
                                java.math.RoundingMode.HALF_UP
                        );

                txtPrecioTotal.setText(precio.toPlainString());

            } catch (Exception e) {
                txtPrecioTotal.setText("");
            }
        }

        private LocalDateTime obtenerFechaHora(String fecha, String hora) {
            DateTimeFormatter formato
                    = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            return LocalDateTime.parse(
                    fecha.trim() + " " + hora.trim(),
                    formato
            );
        }

        private void limpiarCampos() {
            txtNombre.setText("");
            txtApellido.setText("");
            txtTelefono.setText("");
            txtEmail.setText("");
            txtFecha.setText("");
            txtHoraInicio.setText("");
            txtHoraFin.setText("");
            txtPrecioTotal.setText("");

            if (cboCancha.getItemCount() > 0) {
                cboCancha.setSelectedIndex(0);
            }

            if (cboDisciplina.getItemCount() > 0) {
                cboDisciplina.setSelectedIndex(0);
            }
        }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel13 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        btnRegresarMenu = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        tituloCliente = new javax.swing.JLabel();
        lblNombres = new javax.swing.JLabel();
        lblTelefono = new javax.swing.JLabel();
        lblApellidos = new javax.swing.JLabel();
        lblCorreo = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        txtTelefono = new javax.swing.JTextField();
        txtApellido = new javax.swing.JTextField();
        txtEmail = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        tituloReserva = new javax.swing.JLabel();
        lblCancha = new javax.swing.JLabel();
        lblHorarioInicio = new javax.swing.JLabel();
        lblFecha = new javax.swing.JLabel();
        lblHorarioFin = new javax.swing.JLabel();
        txtHoraInicio = new javax.swing.JTextField();
        cboCancha = new javax.swing.JComboBox<>();
        txtFecha = new javax.swing.JTextField();
        txtHoraFin = new javax.swing.JTextField();
        lblPrecio = new javax.swing.JLabel();
        txtPrecioTotal = new javax.swing.JTextField();
        btnRegistrar = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        lblDisciplina = new javax.swing.JLabel();
        cboDisciplina = new javax.swing.JComboBox<>();
        btnEliminarReserva = new javax.swing.JButton();
        btnConsultar = new javax.swing.JButton();
        btnCalendario = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        tituloTabla = new javax.swing.JLabel();
        txtBuscarReserva = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReservas = new javax.swing.JTable();

        jLabel13.setText("jLabel13");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(18, 52, 86));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("SportCenter");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Panel de recepción · Gestión de reservas");

        btnRegresarMenu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegresarMenu.setText("Ir al menú");
        btnRegresarMenu.addActionListener(this::btnRegresarMenuActionPerformed);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnRegresarMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnRegresarMenu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(153, 153, 153));

        tituloCliente.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tituloCliente.setForeground(new java.awt.Color(255, 255, 255));
        tituloCliente.setText("1. Datos del cliente");

        lblNombres.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblNombres.setForeground(new java.awt.Color(255, 255, 255));
        lblNombres.setText("Nombres");
        lblNombres.setToolTipText("");

        lblTelefono.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblTelefono.setForeground(new java.awt.Color(255, 255, 255));
        lblTelefono.setText("Teléfono");
        lblTelefono.setToolTipText("");

        lblApellidos.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblApellidos.setForeground(new java.awt.Color(255, 255, 255));
        lblApellidos.setText("Apellidos");
        lblApellidos.setToolTipText("");

        lblCorreo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCorreo.setForeground(new java.awt.Color(255, 255, 255));
        lblCorreo.setText("Correo electrónico");
        lblCorreo.setToolTipText("");

        txtNombre.setText("Nombres");
        txtNombre.setToolTipText("Ingrese nombres");

        txtTelefono.setText("Telefono");
        txtTelefono.setToolTipText("Ingrese teléfono");

        txtApellido.setText("Apellidos");
        txtApellido.setToolTipText("Ingrese apellidos");

        txtEmail.setText("Correo electrónico");
        txtEmail.setToolTipText("Ingrese correo electrónico");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tituloCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNombres, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 178, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(lblApellidos)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(lblCorreo)
                                .addGap(18, 18, 18)
                                .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(15, 15, 15))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(tituloCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNombres)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblApellidos))
                        .addGap(15, 15, 15)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCorreo)
                            .addComponent(lblTelefono)
                            .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(153, 153, 153));

        tituloReserva.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tituloReserva.setForeground(new java.awt.Color(255, 255, 255));
        tituloReserva.setText("2. Datos de la reserva");

        lblCancha.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblCancha.setForeground(new java.awt.Color(255, 255, 255));
        lblCancha.setText("Cancha");
        lblCancha.setToolTipText("");

        lblHorarioInicio.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblHorarioInicio.setForeground(new java.awt.Color(255, 255, 255));
        lblHorarioInicio.setText("Hora de inicio");
        lblHorarioInicio.setToolTipText("");

        lblFecha.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblFecha.setForeground(new java.awt.Color(255, 255, 255));
        lblFecha.setText("Fecha");
        lblFecha.setToolTipText("");

        lblHorarioFin.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblHorarioFin.setForeground(new java.awt.Color(255, 255, 255));
        lblHorarioFin.setText("Hora de fin");
        lblHorarioFin.setToolTipText("");

        txtHoraInicio.setText("Hora inicio");
        txtHoraInicio.setToolTipText("HH:MM");

        cboCancha.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cancha 1", "Cancha 2", "Cancha 3", "Cancha 4" }));
        cboCancha.setToolTipText("Seleccione cancha");

        txtFecha.setText("Fecha");
        txtFecha.setToolTipText("DD:MM:AA");

        txtHoraFin.setText("Hora fin");
        txtHoraFin.setToolTipText("HH:MM");

        lblPrecio.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblPrecio.setForeground(new java.awt.Color(255, 255, 255));
        lblPrecio.setText("Precio total");
        lblPrecio.setToolTipText("");

        txtPrecioTotal.setEditable(false);
        txtPrecioTotal.setText("Precio");
        txtPrecioTotal.setToolTipText("");

        btnRegistrar.setBackground(new java.awt.Color(0, 153, 51));
        btnRegistrar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnRegistrar.setForeground(new java.awt.Color(255, 255, 255));
        btnRegistrar.setText("Registrar");
        btnRegistrar.addActionListener(this::btnRegistrarActionPerformed);

        btnModificar.setBackground(new java.awt.Color(0, 51, 204));
        btnModificar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnModificar.setForeground(new java.awt.Color(255, 255, 255));
        btnModificar.setText("Modificar");
        btnModificar.addActionListener(this::btnModificarActionPerformed);

        btnCancelar.setBackground(new java.awt.Color(255, 0, 0));
        btnCancelar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("Cancelar reserva");
        btnCancelar.addActionListener(this::btnCancelarActionPerformed);

        btnActualizar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        lblDisciplina.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblDisciplina.setForeground(new java.awt.Color(255, 255, 255));
        lblDisciplina.setText("Disciplina");
        lblDisciplina.setToolTipText("");

        cboDisciplina.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fúbtol", "Vóley", " " }));

        btnEliminarReserva.setBackground(new java.awt.Color(102, 0, 51));
        btnEliminarReserva.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnEliminarReserva.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarReserva.setText("Eliminar");
        btnEliminarReserva.addActionListener(this::btnEliminarReservaActionPerformed);

        btnConsultar.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnConsultar.setText("Consultar");
        btnConsultar.addActionListener(this::btnConsultarActionPerformed);

        btnCalendario.setText("...");
        btnCalendario.addActionListener(this::btnCalendarioActionPerformed);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(tituloReserva, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addComponent(btnRegistrar)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(btnConsultar)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(btnModificar)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(btnEliminarReserva)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(btnCancelar)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(btnActualizar)
                        .addGap(73, 73, 73))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCancha, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblHorarioInicio, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cboCancha, 0, 160, Short.MAX_VALUE)
                                    .addComponent(txtHoraInicio))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(lblHorarioFin)
                                .addGap(37, 37, 37)
                                .addComponent(txtHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addGap(133, 133, 133)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblDisciplina, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(6, 6, 6)
                                .addComponent(cboDisciplina, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFecha))
                                .addGap(3, 3, 3)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtPrecioTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addComponent(btnCalendario)
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(tituloReserva)
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCancha)
                    .addComponent(cboCancha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDisciplina)
                    .addComponent(cboDisciplina, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtHoraInicio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHorarioInicio)
                    .addComponent(lblFecha)
                    .addComponent(txtFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCalendario))
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPrecioTotal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHorarioFin)
                    .addComponent(txtHoraFin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPrecio))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRegistrar)
                    .addComponent(btnModificar)
                    .addComponent(btnCancelar)
                    .addComponent(btnActualizar)
                    .addComponent(btnEliminarReserva)
                    .addComponent(btnConsultar))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jPanel4.setBackground(new java.awt.Color(153, 153, 153));

        tituloTabla.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        tituloTabla.setForeground(new java.awt.Color(255, 255, 255));
        tituloTabla.setText("3. Reservas registradas ");

        txtBuscarReserva.setText("Buscar reserva");
        txtBuscarReserva.setToolTipText("Buscar por cliente");

        jScrollPane1.setToolTipText("");

        tblReservas.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Cliente", "Teléfono", "Cancha", "Fecha inicio", "Fecha fin", "Precio total", "Estado"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tblReservas);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tituloTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(txtBuscarReserva)))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addGap(15, 15, 15))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(tituloTabla)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtBuscarReserva, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        // ETAPA 1: cargar la reserva seleccionada.
        if (!modoEdicion) {

            int fila = tblReservas.getSelectedRow();

            if (fila == -1) {
                JOptionPane.showMessageDialog(
                        this, "Selecciona una reserva de la tabla."
                );
                return;
            }

            int idReserva = Integer.parseInt(
                    tblReservas.getValueAt(fila, 0).toString()
            );

            String estado = tblReservas.getValueAt(fila, 8).toString();

            if (!"CONFIRMADA".equalsIgnoreCase(estado)) {
                JOptionPane.showMessageDialog(
                        this, "Solo puedes modificar reservas confirmadas."
                );
                return;
            }

            try {
                Reserva reservaSeleccionada = null;

                for (Reserva r : new ReservaService().listar()) {
                    if (r.getIdReserva() == idReserva) {
                        reservaSeleccionada = r;
                        break;
                    }
                }

                if (reservaSeleccionada == null) {
                    JOptionPane.showMessageDialog(
                            this, "No se encontró la reserva."
                    );
                    return;
                }

                txtNombre.setText(reservaSeleccionada.getNombreCliente());
                txtApellido.setText(reservaSeleccionada.getApellidoCliente());
                txtTelefono.setText(reservaSeleccionada.getTelefonoCliente());
                txtEmail.setText(reservaSeleccionada.getEmailCliente());

                cboDisciplina.setSelectedItem(reservaSeleccionada.getDeporte());

                LocalDateTime inicio = reservaSeleccionada.getFechaInicio();
                LocalDateTime fin = reservaSeleccionada.getFechaFin();

                txtFecha.setText(inicio.toLocalDate().format(
                        java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                ));
                txtHoraInicio.setText(inicio.toLocalTime().format(
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm")
                ));
                txtHoraFin.setText(fin.toLocalTime().format(
                        java.time.format.DateTimeFormatter.ofPattern("HH:mm")
                ));

                txtPrecioTotal.setText(
                        reservaSeleccionada.getPrecioTotal().toPlainString()
                );

                for (int i = 0; i < cboCancha.getItemCount(); i++) {
                    String item = cboCancha.getItemAt(i);

                    if (item.startsWith(
                            reservaSeleccionada.getIdCancha() + " - "
                    )) {
                        cboCancha.setSelectedIndex(i);
                        break;
                    }
                }

                idReservaEdicion = idReserva;
                modoEdicion = true;
                btnModificar.setText("Guardar cambios");

                return;

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        this, "Error al cargar la reserva: " + e.getMessage()
                );
                return;
            }
        }

        // ETAPA 2: guardar los cambios.
        int idReserva = idReservaEdicion;

        try {
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty()
                    || telefono.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this, "Completa los datos del cliente."
                );
                return;
            }

            if (!telefono.matches("\\d{9}")) {
                JOptionPane.showMessageDialog(
                        this, "El teléfono debe tener exactamente 9 dígitos."
                );
                return;
            }

            String seleccion = (String) cboCancha.getSelectedItem();

            if (seleccion == null) {
                JOptionPane.showMessageDialog(this, "Selecciona una cancha.");
                return;
            }

            int idCancha = Integer.parseInt(
                    seleccion.substring(0, seleccion.indexOf(" - "))
            );

            LocalDateTime inicio = obtenerFechaHora(
                    txtFecha.getText(), txtHoraInicio.getText()
            );
            LocalDateTime fin = obtenerFechaHora(
                    txtFecha.getText(), txtHoraFin.getText()
            );

            if (!fin.isAfter(inicio)) {
                JOptionPane.showMessageDialog(
                        this, "La hora de fin debe ser posterior a la de inicio."
                );
                return;
            }

            calcularPrecio();
            BigDecimal precio = new BigDecimal(txtPrecioTotal.getText().trim());

            Usuario usuario = Sesion.getUsuario();

            if (usuario == null || usuario.getIdUsuario() <= 0) {
                JOptionPane.showMessageDialog(
                        this, "La sesión ha caducado. Vuelve a iniciar sesión."
                );
                return;
            }

            ClienteDAO clienteDAO = new ClienteDAO();
            int idCliente = clienteDAO.buscarPorTelefono(telefono);

            if (idCliente == 0) {
                idCliente = clienteDAO.insertar(
                        nombre, apellido, telefono, email
                );
            }

            Reserva reserva = new Reserva();
            reserva.setIdReserva(idReserva);
            reserva.setIdCliente(idCliente);
            reserva.setIdCancha(idCancha);
            reserva.setIdUsuario(usuario.getIdUsuario());
            reserva.setDeporte(cboDisciplina.getSelectedItem().toString());
            reserva.setFechaInicio(inicio);
            reserva.setFechaFin(fin);
            reserva.setPrecioTotal(precio);
            reserva.setEstado(Reserva.CONFIRMADA);

            boolean actualizado = new ReservaService().modificar(reserva);

            if (actualizado) {
                JOptionPane.showMessageDialog(
                        this, "Reserva modificada correctamente."
                );

                modoEdicion = false;
                idReservaEdicion = -1;
                btnModificar.setText("Modificar");

                limpiarCampos();
                cargarReservas();
            } else {
                JOptionPane.showMessageDialog(
                        this, "No se modificó la reserva. Verifica que siga confirmada."
                );
            }

        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this, "Formato incorrecto. Usa fecha dd/MM/yyyy y hora HH:mm."
            );
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this, "Error en la base de datos: " + e.getMessage()
            );
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this, "No se pudo modificar la reserva: " + e.getMessage()
            );
        }
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnRegistrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegistrarActionPerformed
        try {
            // 1. Validar los datos del cliente
            String nombre = txtNombre.getText().trim();
            String apellido = txtApellido.getText().trim();
            String telefono = txtTelefono.getText().trim();
            String email = txtEmail.getText().trim();

            if (nombre.isEmpty() || apellido.isEmpty()
                    || telefono.isEmpty() || email.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Complete todos los datos del cliente."
                );
                return;
            }

            if (!telefono.matches("\\d{9}")) {
                JOptionPane.showMessageDialog(
                        this,
                        "El teléfono debe contener exactamente 9 dígitos."
                );
                return;
            }

            // 2. Verificar la cancha seleccionada
            String seleccion = (String) cboCancha.getSelectedItem();

            if (seleccion == null) {
                JOptionPane.showMessageDialog(
                        this,
                        "Seleccione una cancha disponible."
                );
                return;
            }

            int idCancha = Integer.parseInt(
                    seleccion.substring(0, seleccion.indexOf(" - "))
            );

            // 3. Obtener fecha y horas
            LocalDateTime inicio = obtenerFechaHora(
                    txtFecha.getText(),
                    txtHoraInicio.getText()
            );

            LocalDateTime fin = obtenerFechaHora(
                    txtFecha.getText(),
                    txtHoraFin.getText()
            );

            if (!fin.isAfter(inicio)) {
                JOptionPane.showMessageDialog(
                        this,
                        "La hora de fin debe ser posterior a la hora de inicio."
                );
                return;
            }

            // 4. Obtener el precio calculado
            String precioTexto = txtPrecioTotal.getText().trim();

            if (precioTexto.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Verifique la fecha, las horas y el precio de la reserva."
                );
                return;
            }

            BigDecimal precio = new BigDecimal(precioTexto);

            // 5. Obtener el usuario que inició sesión
            Usuario usuario = Sesion.getUsuario();

            if (usuario == null || usuario.getIdUsuario() <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró una sesión válida. Vuelva a iniciar sesión."
                );
                return;
            }

            // 6. Buscar al cliente por su teléfono o registrarlo
            ClienteDAO clienteDAO = new ClienteDAO();
            int idCliente = clienteDAO.buscarPorTelefono(telefono);

            if (idCliente == 0) {
                idCliente = clienteDAO.insertar(
                        nombre,
                        apellido,
                        telefono,
                        email
                );
            }

            // 7. Crear la reserva
            Reserva reserva = new Reserva();
            reserva.setIdCliente(idCliente);
            reserva.setIdCancha(idCancha);
            reserva.setIdUsuario(usuario.getIdUsuario());

            reserva.setDeporte(
                    cboDisciplina.getSelectedItem().toString()
            );

            reserva.setFechaInicio(inicio);
            reserva.setFechaFin(fin);
            reserva.setPrecioTotal(precio);
            reserva.setEstado(Reserva.CONFIRMADA);

            // 8. Registrar y comprobar disponibilidad
            ReservaService servicio = new ReservaService();
            int idReserva = servicio.registrar(reserva);

            JOptionPane.showMessageDialog(
                    this,
                    "Reserva registrada correctamente.\n"
                    + "Código de reserva: " + idReserva
            );

            limpiarCampos();
            cargarReservas();

        } catch (java.time.format.DateTimeParseException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Formato incorrecto. Use fecha dd/MM/yyyy y hora HH:mm.\n"
                    + "Ejemplo: 25/10/2026 y 14:30."
            );

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage()
            );

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error en la base de datos: " + e.getMessage()
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo registrar la reserva: " + e.getMessage()
            );
        }

    }//GEN-LAST:event_btnRegistrarActionPerformed

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        cargarReservas();
        JOptionPane.showMessageDialog(
                this,
                "Lista de reservas actualizada correctamente.",
                "Actualización",
                JOptionPane.INFORMATION_MESSAGE
        );
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed

        int fila = tblReservas.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona una reserva de la tabla."
            );
            return;
        }

        int idReserva = Integer.parseInt(
                tblReservas.getValueAt(fila, 0).toString()
        );

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Deseas cancelar la reserva seleccionada?",
                "Confirmar cancelación",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean cancelada
                    = new ReservaService().cancelar(idReserva);

            if (cancelada) {
                JOptionPane.showMessageDialog(
                        this,
                        "Reserva cancelada correctamente."
                );
                cargarReservas();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se pudo cancelar. Verifica que la reserva siga confirmada."
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "Error al cancelar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Aviso",
                    JOptionPane.WARNING_MESSAGE
            );
        }


    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnRegresarMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRegresarMenuActionPerformed
        new LoginFrame().setVisible(true);
        this.dispose();
    }//GEN-LAST:event_btnRegresarMenuActionPerformed

    private void btnEliminarReservaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarReservaActionPerformed
        int fila = tblReservas.getSelectedRow();

        if (fila == -1) {
            JOptionPane.showMessageDialog(
                    this,
                    "Selecciona la reserva que deseas eliminar."
            );
            return;
        }

        int filaModelo = tblReservas.convertRowIndexToModel(fila);
        int idReserva = Integer.parseInt(
                tblReservas.getModel()
                        .getValueAt(filaModelo, 0).toString()
        );

        int confirmacion = JOptionPane.showConfirmDialog(
                this,
                "¿Seguro que deseas eliminar la reserva "
                + idReserva + "?\nEsta acción no se puede deshacer.",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            boolean eliminada
                    = new ReservaService().eliminar(idReserva);

            if (eliminada) {
                JOptionPane.showMessageDialog(
                        this,
                        "Reserva eliminada correctamente."
                );
                limpiarCampos();
                cargarReservas();
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "No se encontró la reserva seleccionada."
                );
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo eliminar la reserva. "
                    + "Verifica si tiene pagos asociados.\n"
                    + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }

    }//GEN-LAST:event_btnEliminarReservaActionPerformed

    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConsultarActionPerformed
        consultarReserva();
    }//GEN-LAST:event_btnConsultarActionPerformed

    private void btnCalendarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalendarioActionPerformed
        DatePickerSettings settings = new DatePickerSettings();
        settings.setFormatForDatesCommonEra("dd/MM/yyyy");

        DatePicker calendario = new DatePicker(settings);

        int opcion = JOptionPane.showConfirmDialog(
                this,
                calendario,
                "Seleccionar fecha",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (opcion == JOptionPane.OK_OPTION && calendario.getDate() != null) {
            txtFecha.setText(
                    calendario.getDate().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy")
                    )
            );
        }
    }//GEN-LAST:event_btnCalendarioActionPerformed

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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new FrmRecepcionista().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnCalendario;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnConsultar;
    private javax.swing.JButton btnEliminarReserva;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnRegistrar;
    private javax.swing.JButton btnRegresarMenu;
    private javax.swing.JComboBox<String> cboCancha;
    private javax.swing.JComboBox<String> cboDisciplina;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblApellidos;
    private javax.swing.JLabel lblCancha;
    private javax.swing.JLabel lblCorreo;
    private javax.swing.JLabel lblDisciplina;
    private javax.swing.JLabel lblFecha;
    private javax.swing.JLabel lblHorarioFin;
    private javax.swing.JLabel lblHorarioInicio;
    private javax.swing.JLabel lblNombres;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JTable tblReservas;
    private javax.swing.JLabel tituloCliente;
    private javax.swing.JLabel tituloReserva;
    private javax.swing.JLabel tituloTabla;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtBuscarReserva;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtFecha;
    private javax.swing.JTextField txtHoraFin;
    private javax.swing.JTextField txtHoraInicio;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPrecioTotal;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
