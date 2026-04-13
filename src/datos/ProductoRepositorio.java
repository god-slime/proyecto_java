package datos;

import modelo.Producto;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepositorio {

    private static final String ARCHIVO_PRODUCTOS = "productos.txt";

    public static List<Producto> cargar() {
        List<Producto> catalogo = new ArrayList<>();
        File f = new File(ARCHIVO_PRODUCTOS);

        if (!f.exists()) {
            catalogo.add(new Producto("Laptop",                  100, 11999.99));
            catalogo.add(new Producto("Mouse",                   100,   249.99));
            catalogo.add(new Producto("Teclado",                 100,   599.99));
            catalogo.add(new Producto("Audifonos",               100,   899.99));
            catalogo.add(new Producto("Monitor",                 100,  3499.99));
            catalogo.add(new Producto("Pantalla de 30 pulgadas",  10,  2499.99));
            guardar(catalogo);
            System.out.println("[ Archivo '" + ARCHIVO_PRODUCTOS + "' creado con catálogo por defecto ]");
            return catalogo;
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"))) {
            String linea;
            int lineaNum = 0;
            while ((linea = br.readLine()) != null) {
                lineaNum++;
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("#")) continue;
                String[] partes = linea.split("=");
                if (partes.length != 3) {
                    System.out.println("[ Línea " + lineaNum + " ignorada (formato incorrecto): " + linea + " ]");
                    continue;
                }
                try {
                    String nombre = partes[0].trim();
                    int    stock  = Integer.parseInt(partes[1].trim());
                    double precio = Double.parseDouble(partes[2].trim());
                    catalogo.add(new Producto(nombre, stock, precio));
                } catch (NumberFormatException e) {
                    System.out.println("[ Línea " + lineaNum + " ignorada (valor inválido): " + linea + " ]");
                }
            }
            System.out.println("[ " + catalogo.size() + " producto(s) cargado(s) desde '" + ARCHIVO_PRODUCTOS + "' ]");
        } catch (IOException e) {
            System.out.println("[ Error al leer '" + ARCHIVO_PRODUCTOS + "': " + e.getMessage() + " ]");
        }

        return catalogo;
    }

    public static void guardar(List<Producto> catalogo) {
        try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(ARCHIVO_PRODUCTOS), "UTF-8"))) {
            pw.println("# Formato: nombre=stock=precio");
            for (Producto p : catalogo) {
                pw.println(p.nombre + "=" + p.stock + "=" + p.precio);
            }
        } catch (IOException e) {
            System.out.println("[ Error al guardar productos: " + e.getMessage() + " ]");
        }
    }
}
