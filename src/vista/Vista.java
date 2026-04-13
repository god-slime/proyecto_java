package vista;

import modelo.ItemCarrito;
import modelo.Producto;
import java.util.List;
import java.util.Scanner;

public class Vista {

    private final Scanner sc = new Scanner(System.in);

    public void mostrarMenu(List<Producto> catalogo) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              TIENDA DE ELECTRÓNICA                      ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        mostrarCatalogo(catalogo);
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Agregar producto al carrito                          ║");
        System.out.println("║  2. Quitar producto del carrito                          ║");
        System.out.println("║  3. Ver carrito                                          ║");
        System.out.println("║  4. Pagar                                                ║");
        System.out.println("║  5. Modo administrador                                   ║");
        System.out.println("║  6. Salir                                                ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.print("Selecciona una opción: ");
    }

    public void mostrarMenuAdmin(List<Producto> catalogo) {
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║              PANEL DE ADMINISTRADOR                     ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        mostrarCatalogo(catalogo);
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║  1. Añadir nuevo producto                                ║");
        System.out.println("║  2. Eliminar producto                                    ║");
        System.out.println("║  3. Actualizar precio                                    ║");
        System.out.println("║  4. Actualizar stock                                     ║");
        System.out.println("║  5. Volver al menú principal                             ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.print("Selecciona una opción: ");
    }

    public void mostrarCatalogo(List<Producto> catalogo) {
        System.out.println("║  CATÁLOGO                                                ║");
        System.out.println("║  ┌────┬──────────────────────────┬────────────┬───────┐  ║");
        System.out.println("║  │ #  │ Producto                 │   Precio   │ Stock │  ║");
        System.out.println("║  ├────┼──────────────────────────┼────────────┼───────┤  ║");
        if (catalogo.isEmpty()) {
            System.out.println("║  │    (sin productos)                                  │  ║");
        }
        for (int i = 0; i < catalogo.size(); i++) {
            Producto p = catalogo.get(i);
            String stockStr = p.stock == 0 ? "AGOTADO" : String.format("%7d", p.stock);
            System.out.printf("║  │ %-2d │ %-26s │ $%,9.2f │%s │  ║%n",
                i + 1, p.nombre, p.precio, stockStr);
        }
        System.out.println("║  └────┴──────────────────────────┴────────────┴───────┘  ║");
    }

    public void mostrarCarrito(List<ItemCarrito> carrito, double total) {
        System.out.println("\n──── CARRITO ──────────────────────────────────────────────");
        if (carrito.isEmpty()) {
            System.out.println("  (vacío)");
        } else {
            System.out.printf("  %-4s %-26s %6s %11s %12s%n", "#", "Producto", "Cant.", "P/unit", "Subtotal");
            System.out.println("  ──────────────────────────────────────────────────────");
            for (int i = 0; i < carrito.size(); i++) {
                ItemCarrito it = carrito.get(i);
                System.out.printf("  %-4d %-26s %6d  $%,9.2f $%,11.2f%n",
                    i + 1, it.producto.nombre, it.cantidad, it.producto.precio, it.subtotal());
            }
            System.out.println("  ──────────────────────────────────────────────────────");
            System.out.printf("  %-40s $%,11.2f%n", "TOTAL:", total);
        }
        System.out.println("───────────────────────────────────────────────────────────");
    }

    public String leerLinea() {
        return sc.nextLine().trim();
    }

    public int leerEntero() {
        try { return Integer.parseInt(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    public double leerDouble() {
        try { return Double.parseDouble(sc.nextLine().trim()); }
        catch (NumberFormatException e) { return -1; }
    }

    public void mensaje(String msg) {
        System.out.println(msg);
    }

    public void mensajeFormato(String fmt, Object... args) {
        System.out.printf(fmt, args);
    }
}
