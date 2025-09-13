package DeckOfCards;
/**
 * Palos de cartas de una baraja inglesa.
 *
 * @author (Cecilia Curlango Rosas)
 * @version (2025-1)
 */
public enum Palo {
    TREBOL(1,"♣\uFE0E","negro","C"),
    DIAMANTE(2,"♦\uFE0F","rojo","D"),
    CORAZON(3,"❤\uFE0F","rojo","H"),
    PICA(4,"♠\uFE0F","negro","S");

    private final int peso;
    private final String figura;
    private final String color;
    private final String codigo;

    Palo(int peso, String figura, String color, String codigo) {
        this.peso = peso;
        this.figura = figura;
        this.color = color;
        this.codigo = codigo;
    }
    public int getPeso() {
        return peso;
    }
    public String getFigura() {
        return figura;
    }
    public String getColor() {
        return color;
    }
    public String getCodigo(){
        return codigo;
    }
}
