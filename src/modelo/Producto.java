package modelo;

public class Producto {
    public String nombre;
    public int stock;
    public double precio;

    public Producto(String nombre, int stock, double precio) {
        this.nombre = nombre;
        this.stock  = stock;
        this.precio = precio;
    }
}
