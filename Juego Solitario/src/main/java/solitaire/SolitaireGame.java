package solitaire;

import DeckOfCards.CartaInglesa;
import DeckOfCards.Palo;
import DeckOfCards.Pila;

import java.util.ArrayList;
/**
 * Juego de solitario.
 *
 * @author (Cecilia Curlango Rosas)
 * @version (2025-2)
 */
public class SolitaireGame {
    ArrayList<TableauDeck> tableau = new ArrayList<>();
    ArrayList<FoundationDeck> foundation = new ArrayList<>();
    FoundationDeck lastFoundationUpdated;
    DrawPile drawPile;
    WastePile wastePile;
    private Pila<EstadoJuego> registroMovimientos;

    public SolitaireGame() {
        registroMovimientos = new Pila<>(1000);
        drawPile = new DrawPile();
        wastePile = new WastePile();
        createTableaux();
        createFoundations();
        wastePile.addCartas(drawPile.retirarCartas());
    }

    /**
     * Move cards from Waste pile to Draw Pile.
     */
    public void reloadDrawPile() {
        ArrayList<CartaInglesa> cards = wastePile.emptyPile();
        drawPile.recargar(cards);
        if(!cards.isEmpty()) {
            registroMovimientos.push(EstadoJuego.recargar(cards.size()));
        }
    }

    /**
     * Move cards from Draw pile to Waste Pile.
     */
    public void drawCards() {
        int cuantas = drawPile.getCuantasCartasSeEntregan();
        ArrayList<CartaInglesa> cards = drawPile.retirarUltimas(cuantas,true);
        wastePile.addCartas(cards);
        if (!cards.isEmpty()) {
            registroMovimientos.push(EstadoJuego.crearRobo(cards));
        }
    }

    /**
     * Tomar la carta del Waste pile y ponerla en el tableau
     *
     * @param tableauDestino donde se coloca la carta
     * @return true si se pudo hacer el movimiento, false si no
     */
    public boolean moveWasteToTableau(int tableauDestino) {
        boolean movimientoRealizado = false;
        TableauDeck destino = tableau.get(tableauDestino - 1);
        CartaInglesa carta = wastePile.verCarta();
        if (moveWasteToTableau(destino)) {
            registroMovimientos.push(EstadoJuego.wasteToTableau(carta, tableauDestino - 1));
            movimientoRealizado = true;
        }
        return movimientoRealizado;
    }

    /**
     * Tomar varias cartas del Tableau fuente y colocarlas en el
     * Tableau destino.
     *
     * @param tableauFuente  de donde se toma la carta (1-7)
     * @param tableauDestino donde se coloca la carta (1-7)
     * @return true si se pudo hacer el movimiento, false si no
     */
    public boolean moveTableauToTableau(int tableauFuente, int tableauDestino) {
        boolean movimientoRealizado = false;
        TableauDeck fuente = tableau.get(tableauFuente - 1);
        TableauDeck destino = tableau.get(tableauDestino - 1);

        if (!fuente.isEmpty()) {
            int valorQueDebeTenerLaCartaInicial = destino.isEmpty() ? 13 : destino.verUltimaCarta().getValor() - 1;
            CartaInglesa cartaInicial = fuente.viewCardStartingAt(valorQueDebeTenerLaCartaInicial);

            if (cartaInicial != null && destino.sePuedeAgregarCarta(cartaInicial)) {
                ArrayList<CartaInglesa> cartasMovidas = fuente.removeStartingAt(valorQueDebeTenerLaCartaInicial);
                if (destino.agregarBloqueDeCartas(cartasMovidas)) {
                    boolean voltearUltima = false;
                    if (!fuente.isEmpty() && !fuente.verUltimaCarta().isFaceup()) {
                        fuente.verUltimaCarta().makeFaceUp();
                        voltearUltima = true;
                    }
                    registroMovimientos.push(
                            EstadoJuego.tableauToTableau(
                                    tableauFuente - 1,
                                    tableauDestino - 1,
                                    cartasMovidas,
                                    voltearUltima
                            )
                    );
                    movimientoRealizado = true;
                }
            }
        }
        return movimientoRealizado;
    }



    /**
     * Tomar la carta de Tableau y colocarla en el Foundation.
     *
     * @param numero de tableau donde se moverá la carta (1-7)
     * @return true si se pudo move la carta, false si no
     */
    public boolean moveTableauToFoundation(int numero) {
        boolean movimientoRealizado = false;

        TableauDeck fuente = tableau.get(numero - 1);
        if (!fuente.isEmpty()) {
            CartaInglesa ultimaCarta = fuente.verUltimaCarta();
            if (ultimaCarta != null && ultimaCarta.isFaceup()) {
                CartaInglesa carta = fuente.removerUltimaCarta();
                if (moveCartaToFoundation(carta)) {
                    int foundationIdx = carta.getPalo().ordinal();
                    registroMovimientos.push(
                            EstadoJuego.tableauToFoundation(numero - 1, foundationIdx, carta, false)
                    );
                    movimientoRealizado = true;
                } else {
                    fuente.agregarCarta(carta);
                }
            }
        }
        return movimientoRealizado;
    }
    /**
     * Tomar la carta de Waste y colocarla en el Tableau.
     *
     * @param tableau donde se moverá la carta
     * @return true si se pudo move la carta, false si no
     */
    public boolean moveWasteToTableau(TableauDeck tableau) {
        boolean movimientoRealizado = false;

        CartaInglesa carta = wastePile.verCarta();
        if (moveCartaToTableau(carta, tableau)) {
            carta = wastePile.getCarta();
            movimientoRealizado = true;
        }
        return movimientoRealizado;
    }

    /**
     * Tomar una carta de Waste y ponerla en una de las Foundations.
     *
     * @return true si se pudo hacer el movimiento.
     */
    public boolean moveWasteToFoundation() {
        boolean movimientoRealizado = false;

        CartaInglesa carta = wastePile.verCarta();
        if (moveCartaToFoundation(carta)) {
            carta = wastePile.getCarta();
            int foundationIdx = carta.getPalo().ordinal();
            registroMovimientos.push(EstadoJuego.wasteToFoundation(carta, foundationIdx));
            movimientoRealizado = true;
        }
        return movimientoRealizado;
    }

    /**
     * Coloca la carta recibida en el Tableau recibido.
     *
     * @param carta   a colocar
     * @param destino Tableau que recibe la carta.
     * @return true si se pudo hacer el movimiento, false si no
     */
    private boolean moveCartaToTableau(CartaInglesa carta, TableauDeck destino) {
        return destino.agregarCarta(carta);
    }

    /**
     * Coloca la carta recibida en el Foundation correspondiente.
     *
     * @param carta a colocar
     * @return true si se pudo hacer el movimiento, false si no.
     */
    private boolean moveCartaToFoundation(CartaInglesa carta) {
        int cualFoundation = carta.getPalo().ordinal();
        FoundationDeck destino = foundation.get(cualFoundation);
        lastFoundationUpdated = destino;
        return destino.agregarCarta(carta);
    }

    /**
     * Determina si se terminó el juego. El juego se
     * termina cuando todas las cartas están en Foundation
     *
     * @return true si se terminó el juego
     */
    public boolean isGameOver() {
        boolean gameOver = true;
        for (FoundationDeck foundation : foundation) {
            if (foundation.estaVacio()) {
                gameOver = false;
            } else {
                CartaInglesa ultimaCarta = foundation.getUltimaCarta();
                // si la última carta no es rey, no se ha terminado
                if (ultimaCarta.getValor() != 13) {
                    gameOver = false;
                }
            }
        }
        return gameOver;
    }

    private void createFoundations() {
        for (Palo palo : Palo.values()) {
            foundation.add(new FoundationDeck(palo));
        }
    }

    private void createTableaux() {
        for (int i = 0; i < 7; i++) {
            TableauDeck tableauDeck = new TableauDeck();
            tableauDeck.inicializar(drawPile.getCartas(i + 1));
            tableau.add(tableauDeck);
        }
    }

    public DrawPile getDrawPile() {
        return drawPile;
    }

    public ArrayList<TableauDeck> getTableau() {
        return tableau;
    }

    public WastePile getWastePile() {
        return wastePile;
    }

    public FoundationDeck getLastFoundationUpdated() {
        return lastFoundationUpdated;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        // add foundations
        str.append("Foundation\n");
        for (FoundationDeck foundationDeck : foundation) {
            str.append(foundationDeck);
            str.append("\n");
        }

        // add tableaux
        str.append("\nTableaux\n");
        int tableauNumber = 1;
        for (TableauDeck tableauDeck : tableau) {
            str.append(tableauNumber + " ");
            str.append(tableauDeck);
            str.append("\n");
            tableauNumber++;
        }
        str.append("Waste\n");
        str.append(wastePile);
        str.append("\nDraw\n");
        str.append(drawPile);
        return str.toString();
    }

    public ArrayList<FoundationDeck> getFoundations() {
        return foundation;
    }

    public boolean undo() {
        if (registroMovimientos.estaVacia()) return false;

        EstadoJuego estado = registroMovimientos.pop();

        switch (estado.getTipo()) {
            case DRAW:
                ArrayList<CartaInglesa> cartasARetornar = estado.getCartasRetiradas();
                wastePile.retirarUltimas(cartasARetornar.size(), false);
                drawPile.regresarCartas(cartasARetornar);
                break;

            case RECARGA:
                ArrayList<CartaInglesa> devueltas = drawPile.retirarUltimas(estado.getCantidad(), false);
                for (CartaInglesa carta : devueltas) carta.makeFaceUp();
                wastePile.addCartas(devueltas);
                break;

            case WASTE_TO_TABLEAU:
                TableauDeck tabla = tableau.get(estado.getDestino());
                CartaInglesa top = tabla.removerUltimaCarta();
                wastePile.addCarta(top);
                break;

            case WASTE_TO_FOUNDATION:
                FoundationDeck fnd = foundation.get(estado.getDestino());
                CartaInglesa carta = fnd.removerUltimaCarta();
                wastePile.addCarta(carta);
                break;

            case TABLEAU_TO_TABLEAU:
                TableauDeck src = tableau.get(estado.getOrigen());
                TableauDeck dst = tableau.get(estado.getDestino());
                ArrayList<CartaInglesa> bloque = dst.removerUltimas(estado.getCantidad());
                if (estado.isVoltearUltima() && !src.isEmpty()) src.getUltimaCarta().makeFaceDown();
                src.agregarDirecto(bloque);
                break;

            case TABLEAU_TO_FOUNDATION:
                TableauDeck t = tableau.get(estado.getOrigen());
                FoundationDeck f = foundation.get(estado.getDestino());
                CartaInglesa c = f.removerUltimaCarta();
                if (estado.isVoltearUltima() && !t.isEmpty()) t.getUltimaCarta().makeFaceDown();
                t.agregarCartaDirecto(c, true);
                break;
        }
        return true;
    }

}
