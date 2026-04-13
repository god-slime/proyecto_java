package app;

import datos.ProductoRepositorio;
import modelo.ItemCarrito;
import modelo.Producto;
import vista.Vista;

import java.util.ArrayList;
import java.util.List;

public class Tienda {

    static final String CLAVE_ADMIN = "tiendaprueba";

    static List<Producto>    catalogo      = new ArrayList<>();
    static List<ItemCarrito> carrito       = new ArrayList<>();
    static double            totalComprado = 0;
    static Vista             vista         = new Vista();

    public static void main(String[] args) {
        catalogo = ProductoRepositorio.cargar();

        boolean corriendo = true;
        while (corriendo) {
            vista.mostrarMenu(catalogo);
            switch (vista.leerLinea()) {
                case "1" -> agregarAlCarrito();
                case "2" -> quitarDelCarrito();
                case "3" -> verCarrito();
                case "4" -> pagar();
                case "5" -> accederAdmin();
                case "6" -> {
                    ProductoRepositorio.guardar(catalogo);
                    vista.mensaje("\n¡Hasta luego! Total acumulado: $"
                        + String.format("%,.2f", totalComprado));
                    corriendo = false;
                }
                default -> vista.mensaje("\nOpción inválida.");
            }
        }
    }

    static void agregarAlCarrito() {
        vista.mensaje("\nNumero de producto (0 para cancelar): ");
        int idx = vista.leerEntero();
        if (idx == 0) return;
        if (idx < 1 || idx > catalogo.size()) {
            vista.mensaje("Número invalido.");
            return;
        }
        Producto p = catalogo.get(idx - 1);
        if (p.stock == 0) {
            vista.mensaje("✘ \"" + p.nombre + "\" está agotado.");
            return;
        }

        int yaEnCarrito = carrito.stream()
            .filter(it -> it.producto == p)
            .mapToInt(it -> it.cantidad)
            .sum();
        int disponible = p.stock - yaEnCarrito;

        if (disponible <= 0) {
            vista.mensaje("✘ Ya tienes todo el stock disponible de \"" + p.nombre + "\" en el carrito.");
            return;
        }

        vista.mensaje("Cantidad (disponible: " + disponible + "): ");
        int cant = vista.leerEntero();
        if (cant <= 0) { vista.mensaje("Cantidad inválida."); return; }
        if (cant > disponible) {
            vista.mensaje("✘ Solo hay " + disponible + " unidad(es) disponible(s).");
            return;
        }

        for (ItemCarrito it : carrito) {
            if (it.producto == p) {
                it.cantidad += cant;
                vista.mensajeFormato("Cantidad de \"%s\" actualizada a %d en el carrito.%n", p.nombre, it.cantidad);
                return;
            }
        }

        carrito.add(new ItemCarrito(p, cant));
        vista.mensajeFormato("✔ \"%s\" x%d añadido al carrito.%n", p.nombre, cant);
    }

    static void quitarDelCarrito() {
        if (carrito.isEmpty()) { vista.mensaje("\nEl carrito está vacío."); return; }
        verCarrito();
        vista.mensaje("Numero de item a quitar (0 para cancelar): ");
        int idx = vista.leerEntero();
        if (idx == 0) return;
        if (idx < 1 || idx > carrito.size()) { vista.mensaje("Numero invalido."); return; }

        ItemCarrito it = carrito.get(idx - 1);
        vista.mensaje("Cantidad a quitar (en carrito: " + it.cantidad + ", 0 para cancelar): ");
        int cant = vista.leerEntero();
        if (cant == 0) return;
        if (cant < 0 || cant > it.cantidad) { vista.mensaje("Cantidad invalida."); return; }

        if (cant == it.cantidad) {
            carrito.remove(idx - 1);
            vista.mensajeFormato("✔ \"%s\" quitado del carrito.%n", it.producto.nombre);
        } else {
            it.cantidad -= cant;
            vista.mensajeFormato("✔ Cantidad de \"%s\" reducida a %d.%n", it.producto.nombre, it.cantidad);
        }
    }

    static void verCarrito() {
        vista.mostrarCarrito(carrito, calcularTotal());
    }

    static void pagar() {
        if (carrito.isEmpty()) {
            vista.mensaje("\nEl carrito está vacío.");
            return;
        }
        double total = calcularTotal();
        verCarrito();
        vista.mensaje("Confirmar pago de $" + String.format("%,.2f", total) + "? (s/n): ");
        if (!vista.leerLinea().equalsIgnoreCase("s")) {
            vista.mensaje("Pago cancelado.");
            return;
        }
        for (ItemCarrito it : carrito) {
            it.producto.stock -= it.cantidad;
        }
        totalComprado += total;
        carrito.clear();
        ProductoRepositorio.guardar(catalogo);
        vista.mensaje("\nPago realizado. Gracias por su compra!");
        vista.mensajeFormato("  Total acumulado de compras: $%,.2f%n", totalComprado);
    }

    static void accederAdmin() {
        vista.mensaje("\nContraseña de administrador: ");
        if (!vista.leerLinea().equals(CLAVE_ADMIN)) {
            vista.mensaje("✘ Contraseña incorrecta.");
            return;
        }
        menuAdmin();
    }

    static void menuAdmin() {
        boolean enAdmin = true;
        while (enAdmin) {
            vista.mostrarMenuAdmin(catalogo);
            switch (vista.leerLinea()) {
                case "1" -> adminAnadir();
                case "2" -> adminEliminar();
                case "3" -> adminActualizarPrecio();
                case "4" -> adminActualizarStock();
                case "5" -> enAdmin = false;
                default  -> vista.mensaje("\nOpción inválida.");
            }
        }
    }

    static void adminAnadir() {
        vista.mensaje("\nNombre del producto: ");
        String nombre = vista.leerLinea();
        if (nombre.isEmpty()) { vista.mensaje("Nombre vacío. Cancelado."); return; }
        if (nombre.contains("=")) { vista.mensaje("El nombre no puede contener '='. Cancelado."); return; }

        for (Producto p : catalogo) {
            if (p.nombre.equalsIgnoreCase(nombre)) {
                vista.mensaje("✘ Ya existe un producto con ese nombre.");
                return;
            }
        }

        vista.mensaje("Precio: $");
        double precio = vista.leerDouble();
        if (precio <= 0) { vista.mensaje("Precio inválido. Cancelado."); return; }

        vista.mensaje("Stock inicial: ");
        int stock = vista.leerEntero();
        if (stock < 0) { vista.mensaje("Stock invalido. Cancelado."); return; }

        catalogo.add(new Producto(nombre, stock, precio));
        ProductoRepositorio.guardar(catalogo);
        vista.mensajeFormato("✔ Producto \"%s\" anadido (stock: %d, precio: $%,.2f).%n", nombre, stock, precio);
    }

    static void adminEliminar() {
        if (catalogo.isEmpty()) { vista.mensaje("No hay productos."); return; }
        vista.mensaje("\nNumero de producto a eliminar (0 para cancelar): ");
        int idx = vista.leerEntero();
        if (idx == 0) return;
        if (idx < 1 || idx > catalogo.size()) { vista.mensaje("Numero invalido."); return; }

        Producto p = catalogo.get(idx - 1);
        carrito.removeIf(it -> it.producto == p);
        catalogo.remove(idx - 1);
        ProductoRepositorio.guardar(catalogo);
        vista.mensajeFormato("✔ \"%s\" eliminado del catalogo.%n", p.nombre);
    }

    static void adminActualizarPrecio() {
        if (catalogo.isEmpty()) { vista.mensaje("No hay productos."); return; }
        vista.mensaje("\nNumero de producto (0 para cancelar): ");
        int idx = vista.leerEntero();
        if (idx == 0) return;
        if (idx < 1 || idx > catalogo.size()) { vista.mensaje("Numero invalido."); return; }

        Producto p = catalogo.get(idx - 1);
        vista.mensajeFormato("Precio actual de \"%s\": $%,.2f%n", p.nombre, p.precio);
        vista.mensaje("Nuevo precio: $");
        double nuevo = vista.leerDouble();
        if (nuevo <= 0) { vista.mensaje("Precio invalido. Cancelado."); return; }

        p.precio = nuevo;
        ProductoRepositorio.guardar(catalogo);
        vista.mensajeFormato("✔ Precio de \"%s\" actualizado a $%,.2f.%n", p.nombre, p.precio);
    }

    static void adminActualizarStock() {
        if (catalogo.isEmpty()) { vista.mensaje("No hay productos."); return; }
        vista.mensaje("\nNumero de producto (0 para cancelar): ");
        int idx = vista.leerEntero();
        if (idx == 0) return;
        if (idx < 1 || idx > catalogo.size()) { vista.mensaje("Numero invalido."); return; }

        Producto p = catalogo.get(idx - 1);
        vista.mensajeFormato("Stock actual de \"%s\": %d%n", p.nombre, p.stock);
        vista.mensaje("  a) Reemplazar stock\n  b) Anadir al stock\nOpcion: ");
        String modo = vista.leerLinea().toLowerCase();

        if (!modo.equals("a") && !modo.equals("b")) {
            vista.mensaje("Opcion invalida. Cancelado.");
            return;
        }

        vista.mensaje("Cantidad: ");
        int cant = vista.leerEntero();
        if (cant < 0) { vista.mensaje("Cantidad invalida. Cancelado."); return; }

        if (modo.equals("a")) {
            p.stock = cant;
            vista.mensajeFormato("✔ Stock de \"%s\" fijado a %d.%n", p.nombre, p.stock);
        } else {
            p.stock += cant;
            vista.mensajeFormato("✔ Stock de \"%s\" actualizado a %d (+%d).%n", p.nombre, p.stock, cant);
        }
        ProductoRepositorio.guardar(catalogo);
    }

    static double calcularTotal() {
        return carrito.stream().mapToDouble(ItemCarrito::subtotal).sum();
    }
}
