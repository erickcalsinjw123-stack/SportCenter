package Main;

import servicio.InicializadorUsuarios;
import vista.LoginFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            InicializadorUsuarios.crearUsuariosPorDefecto();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                "No se pudo conectar a la base de datos:\n" + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}