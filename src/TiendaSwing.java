import javax.swing.*;
import java.awt.*;

class Producto {
    String nombre;
    int cantidad;
    double precio;

    public Producto(String n, double p, int c) {
        nombre = n;
        cantidad = c;
        precio = p;
    }

    public String toString2() {
        return nombre + " - $" + precio + " (" + cantidad + " en el carrito)";
    }

    public String toString() {
        return nombre + " - $" + precio + " - " + cantidad + " en stock";
    }
}

public class TiendaSwing extends JFrame {

    double Total_comprado = 0;

    DefaultListModel<Producto> modeloProductos = new DefaultListModel<>();
    DefaultListModel<Producto> modeloCarrito = new DefaultListModel<>();

    JList<Producto> listaProductos = new JList<>(modeloProductos);
    JList<Producto> listaCarrito = new JList<>(modeloCarrito);

    JLabel lblTotal = new JLabel(" Total: $0.00");

    public TiendaSwing() {

        setTitle("Tienda básica");
        setSize(600, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cargarProductos();

        setLayout(new BorderLayout());

        // Renderer especial para el carrito
        listaCarrito.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (value instanceof Producto) {
                    label.setText(((Producto) value).toString2());
                }

                return label;
            }
        });

        JPanel centro = new JPanel(new GridLayout(1, 2));
        centro.add(new JScrollPane(listaProductos));
        centro.add(new JScrollPane(listaCarrito));

        JButton btnAgregar = new JButton("Agregar →");
        JButton btnQuitar = new JButton("← Quitar");
        JButton btnPagar = new JButton("Pagar");

        JPanel botones = new JPanel();
        botones.add(btnAgregar);
        botones.add(btnQuitar);
        botones.add(btnPagar);

        JPanel barraEstado = new JPanel(new BorderLayout());
        barraEstado.setBorder(BorderFactory.createEtchedBorder());
        lblTotal.setHorizontalAlignment(SwingConstants.LEFT);
        barraEstado.add(lblTotal, BorderLayout.WEST);

        add(centro, BorderLayout.CENTER);
        add(botones, BorderLayout.NORTH);
        add(barraEstado, BorderLayout.SOUTH);

        btnAgregar.addActionListener(e -> agregar());
        btnQuitar.addActionListener(e -> quitar());
        btnPagar.addActionListener(e -> pagar());
    }

    void cargarProductos() {
        modeloProductos.addElement(new Producto("Laptop", 11999.99, 100));
        modeloProductos.addElement(new Producto("Mouse", 249.99, 100));
        modeloProductos.addElement(new Producto("Teclado", 599.99, 100));
        modeloProductos.addElement(new Producto("Audífonos", 899.99, 100));
        modeloProductos.addElement(new Producto("Monitor", 3499.99, 100));
        modeloProductos.addElement(new Producto("pantalla de 30 pulgadas", 2499.99, 10));
    }

    void agregar() {
        Producto p = listaProductos.getSelectedValue();
        if (p != null) {
            Producto copia = new Producto(p.nombre, p.precio, 1);
            modeloCarrito.addElement(copia);
            actualizarTotal();
        }
    }

    void quitar() {
        Producto p = listaCarrito.getSelectedValue();
        if (p != null) {
            modeloCarrito.removeElement(p);
            actualizarTotal();
        }
    }

    double Total_precio() {
        double total = 0;
        for (int i = 0; i < modeloCarrito.size(); i++) {
            total += modeloCarrito.get(i).precio;
        }
        return total;
    }

    void actualizarTotal() {
        lblTotal.setText(String.format(" Total: $%.2f", Total_precio()));
    }

    void pagar() {
        if (modeloCarrito.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío.");
            return;
        }

        double Total = Total_precio();

        JOptionPane.showMessageDialog(this,
                "Pago realizado. El total fue de " + Total + ". ¡Gracias por su compra!");

        Total_comprado += Total;
        modeloCarrito.clear();
        actualizarTotal();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TiendaSwing().setVisible(true));
    }
}