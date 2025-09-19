package solitaire;

import DeckOfCards.CartaInglesa;
import java.util.ArrayList;

public class EstadoJuego {
    private ArrayList<CartaInglesa> cartasRetiradas;

    public enum Tipo {
        DRAW, RECARGA, WASTE_TO_TABLEAU, WASTE_TO_FOUNDATION, TABLEAU_TO_TABLEAU, TABLEAU_TO_FOUNDATION
    }

    private Tipo tipo;
    private int origen = -1;
    private int destino = -1;
    private boolean voltearUltima = false;
    private int cantidad = 0;
    private CartaInglesa carta;
    private ArrayList<CartaInglesa> cartasBloque;

    private EstadoJuego() {}


    public static EstadoJuego draw(int cantidad) {
        EstadoJuego estado = new EstadoJuego();
        estado.tipo = Tipo.DRAW;
        estado.cantidad = cantidad;
        return estado;
    }

    public static EstadoJuego recargar(int cantidad) {
        EstadoJuego estado = new EstadoJuego();
        estado.tipo = Tipo.RECARGA;
        estado.cantidad = cantidad;
        return estado;
    }

    public static EstadoJuego wasteToTableau(CartaInglesa carta, int dest) {
        EstadoJuego estado = new EstadoJuego();
        estado.tipo = Tipo.WASTE_TO_TABLEAU;
        estado.carta = carta;
        estado.destino = dest;
        return estado;
    }

    public static EstadoJuego wasteToFoundation(CartaInglesa carta, int foundationIdx) {
        EstadoJuego estado = new EstadoJuego();
        estado.tipo = Tipo.WASTE_TO_FOUNDATION;
        estado.carta = carta;
        estado.destino = foundationIdx;
        return estado;
    }

    public static EstadoJuego tableauToTableau(int origen, int destino, ArrayList<CartaInglesa> bloque, boolean voltearUltima) {
        EstadoJuego estado = new EstadoJuego();
        estado.tipo = Tipo.TABLEAU_TO_TABLEAU;
        estado.origen = origen;
        estado.destino = destino;
        estado.cartasBloque = new ArrayList<>(bloque);
        estado.voltearUltima = voltearUltima;
        estado.cantidad = bloque.size();
        return estado;
    }

    public static EstadoJuego tableauToFoundation(int origen, int foundationIdx, CartaInglesa carta, boolean voltearUltima) {
        EstadoJuego estado = new EstadoJuego();
        estado.tipo = Tipo.TABLEAU_TO_FOUNDATION;
        estado.origen = origen;
        estado.destino = foundationIdx;
        estado.carta = carta;
        estado.voltearUltima = voltearUltima;
        return estado;
    }

    public static EstadoJuego crearRobo(ArrayList<CartaInglesa> cartas) {
        EstadoJuego estado = new EstadoJuego();
        estado.tipo = Tipo.DRAW;
        estado.cartasRetiradas = new ArrayList<>(cartas);
        return estado;
    }

    public Tipo getTipo() { return tipo; }
    public int getOrigen() { return origen; }
    public int getDestino() { return destino; }
    public boolean isVoltearUltima() { return voltearUltima; }
    public int getCantidad() { return cantidad; }
    public ArrayList<CartaInglesa> getCartasRetiradas() {
        return cartasRetiradas;
    }
    public CartaInglesa getCarta() { return carta; }
    public ArrayList<CartaInglesa> getCartasBloque() { return cartasBloque; }
}
