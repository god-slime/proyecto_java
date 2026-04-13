package modelo;

public class ItemCarrito {
    public Producto producto;
    public int cantidad;

    public ItemCarrito(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public double subtotal() {
        return producto.precio * cantidad;
    }
}
