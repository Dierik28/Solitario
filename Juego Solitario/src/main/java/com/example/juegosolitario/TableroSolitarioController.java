package com.example.juegosolitario;

import DeckOfCards.CartaInglesa;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;
import solitaire.FoundationDeck;
import solitaire.SolitaireGame;
import solitaire.TableauDeck;

import java.util.ArrayList;

public class TableroSolitarioController {

    // Paneles de las fundaciones
    @FXML
    private Pane fundacionPicas;
    @FXML
    private Pane fundacionCorazones;
    @FXML
    private Pane fundacionDiamantes;
    @FXML
    private Pane fundacionTreboles;

    // Paneles de columnas
    @FXML
    private Pane columna1, columna2,columna3,columna4;

    @FXML
    private Pane columna5, columna6,columna7;


    // Paneles de la pila de robar y descarte
    @FXML
    private Pane panelPilaRobar;
    @FXML
    private Pane panelPilaDescarte;

    // Etiqueta de estado
    @FXML
    private Label lblEstado;

    private SolitaireGame juegoSolitario;
    private ArrayList<Pane> panelesColumnas;
    private ArrayList<Pane> panelesFundaciones;

    // Control arrastre de cartas
    private int indiceColumnaOrigen = -1;
    private boolean desdeDescarte = false;

    // Se inicializa el Juego y los paneles del tablero
    @FXML
    private void initialize() {
        juegoSolitario = new SolitaireGame();

        panelesColumnas = new ArrayList<>();
        panelesColumnas.add(columna1);
        panelesColumnas.add(columna2);
        panelesColumnas.add(columna3);
        panelesColumnas.add(columna4);
        panelesColumnas.add(columna5);
        panelesColumnas.add(columna6);
        panelesColumnas.add(columna7);

        panelesFundaciones = new ArrayList<>();
        panelesFundaciones.add(fundacionTreboles);
        panelesFundaciones.add(fundacionDiamantes);
        panelesFundaciones.add(fundacionCorazones);
        panelesFundaciones.add(fundacionPicas);

        configurarEventos();
        actualizarInterfaz();
    }

    //Método para trabajar correctamente el click al momento de robar una carta
    @FXML
    private void manejarClickPilaRobar() {
        if (juegoSolitario.getDrawPile().hayCartas()) {
            juegoSolitario.drawCards();
        } else {
            juegoSolitario.reloadDrawPile();
        }
        actualizarInterfaz();
    }

    //Método para configurar los eventos del tablero al momento de hacer clicks, tanto en las columnas como en las fundaciones
    private void configurarEventos() {
        configurarPilaDescarte();

        for (int i = 0; i < panelesColumnas.size(); i++) {
            configurarColumna(panelesColumnas.get(i), i);
        }

        for (int i = 0; i < panelesFundaciones.size(); i++) {
            configurarFundacion(panelesFundaciones.get(i), i);
        }
    }

    //Método para configurar la pila de descarte y su arrastre
    private void configurarPilaDescarte() {
        panelPilaDescarte.setOnDragDetected(e -> {
            CartaInglesa carta = juegoSolitario.getWastePile().verCarta();
            if (carta != null) {
                Dragboard db = panelPilaDescarte.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent content = new ClipboardContent();
                content.putString("descarte");
                db.setContent(content);
                desdeDescarte = true;
                e.consume();
            }
        });
    }

    //Método para configurar las columnas y el arrastre de cartas en ella
    private void configurarColumna(Pane columna, int indice) {
        columna.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) e.acceptTransferModes(TransferMode.MOVE);
            e.consume();
        });

        columna.setOnDragEntered(e -> {
            if (e.getDragboard().hasString()) columna.setEffect(new Glow(0.5));
            e.consume();
        });

        columna.setOnDragExited(e -> {
            columna.setEffect(null);
            e.consume();
        });

        columna.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean exito = false;

            if (db.hasString()) {
                if (db.getString().equals("descarte")) {
                    exito = juegoSolitario.moveWasteToTableau(indice + 1);
                    desdeDescarte = false;
                } else if (db.getString().startsWith("columna")) {
                    int origen = Integer.parseInt(db.getString().split("-")[1]);
                    exito = juegoSolitario.moveTableauToTableau(origen + 1, indice + 1);
                }
                if (exito) { actualizarInterfaz(); }
            }
            columna.setEffect(null);
            e.setDropCompleted(exito);
            e.consume();
        });
    }

    // Método para configurar la fundación y el arrastre de cartas en ella
    private void configurarFundacion(Pane fundacion, int indice) {
        fundacion.setOnDragOver(e -> {
            if (e.getDragboard().hasString()) e.acceptTransferModes(TransferMode.MOVE);
            e.consume();
        });

        fundacion.setOnDragEntered(e -> {
            if (e.getDragboard().hasString()) fundacion.setEffect(new Glow(0.8));
            e.consume();
        });

        fundacion.setOnDragExited(e -> {
            fundacion.setEffect(null);
            e.consume();
        });

        fundacion.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean exito = false;

            if (db.hasString()) {
                if (db.getString().equals("descarte")) {
                    exito = juegoSolitario.moveWasteToFoundation();
                    desdeDescarte = false;
                } else if (db.getString().startsWith("columna")) {
                    int origen = Integer.parseInt(db.getString().split("-")[1]);
                    exito = juegoSolitario.moveTableauToFoundation(origen + 1);
                }
                if (exito) {actualizarInterfaz(); verificarVictoria(); }
            }
            fundacion.setEffect(null);
            e.setDropCompleted(exito);
            e.consume();
        });
    }

    // Método para actualizar la interfaz del tablero.
    private void actualizarInterfaz() {
        actualizarPilaRobar();
        actualizarPilaDescarte();
        actualizarColumnas();
        actualizarFundaciones();
    }

    //Método para verificar si el jugador ha ganado mostrando una imagen de victoria
    private void verificarVictoria() {
        if (juegoSolitario.isGameOver()) {
            Image imagenVictoria = new Image(getClass().getResourceAsStream("/imagenes/victoria.png"));
            ImageView imageView = new ImageView(imagenVictoria);

            imageView.setFitWidth(150);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);

            lblEstado.setText("");
            lblEstado.setGraphic(imageView);
        }
    }


    // Método para actualizar la pila al momento de robar cartas del mazo
    private void actualizarPilaRobar() {
        panelPilaRobar.getChildren().clear();
        String simbolo = juegoSolitario.getDrawPile().hayCartas() ? "➳" : "↻";
        Label cartaLabel = new Label(simbolo);
        cartaLabel.setStyle("-fx-font-size: 60px;");
        cartaLabel.setLayoutX(25);
        cartaLabel.setLayoutY(30);
        panelPilaRobar.getChildren().add(cartaLabel);
    }

    //Método que actualiza la pila de descartes
    private void actualizarPilaDescarte() {
        panelPilaDescarte.getChildren().clear();
        CartaInglesa carta = juegoSolitario.getWastePile().verCarta();
        if (carta != null) {
            Label cartaLabel = crearEtiquetaCarta(carta);
            cartaLabel.setLayoutX(5);
            cartaLabel.setLayoutY(5);
            panelPilaDescarte.getChildren().add(cartaLabel);
        }
    }

    //Método que actualiza las columnas del tablero
    private void actualizarColumnas() {
        ArrayList<TableauDeck> columnas = juegoSolitario.getTableau();
        for (int i = 0; i < columnas.size(); i++) {
            Pane columnaPane = panelesColumnas.get(i);
            columnaPane.getChildren().clear();

            ArrayList<CartaInglesa> cartas = columnas.get(i).getCards();
            double yOffset = 10;

            for (int j = 0; j < cartas.size(); j++) {
                CartaInglesa carta = cartas.get(j);
                Label cartaLabel = carta.isFaceup() ? crearEtiquetaCarta(carta) : crearEtiquetaReverso();
                cartaLabel.setLayoutX(10);
                cartaLabel.setLayoutY(yOffset);
                yOffset += 40;

                if (j == cartas.size() - 1 && carta.isFaceup()) {
                    configurarArrastreCarta(cartaLabel, i);
                }
                columnaPane.getChildren().add(cartaLabel);
            }
        }
    }

    //Método para configurar el arrastre de cartas en las columnas.
    private void configurarArrastreCarta(Label cartaLabel, int indiceColumna) {
        cartaLabel.setOnDragDetected(e -> {
            Dragboard db = cartaLabel.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString("columna-" + indiceColumna);
            db.setContent(content);
            indiceColumnaOrigen = indiceColumna;
            e.consume();
        });
    }

    // Método par actualizar las fundaciones
    private void actualizarFundaciones() {
        ArrayList<FoundationDeck> fundaciones = juegoSolitario.getFoundations();
        for (int i = 0; i < fundaciones.size(); i++) {
            Pane fundacionPane = panelesFundaciones.get(i);
            fundacionPane.getChildren().clear();
            CartaInglesa ultimaCarta = fundaciones.get(i).getUltimaCarta();
            if (ultimaCarta != null) {
                Label cartaLabel = crearEtiquetaCarta(ultimaCarta);
                cartaLabel.setLayoutX(5);
                cartaLabel.setLayoutY(5);
                fundacionPane.getChildren().add(cartaLabel);
            }
        }
    }

    //Método para crear las cartas del tablero
    private Label crearEtiquetaCarta(CartaInglesa carta) {
        String valor;
        switch (carta.getValor()) {
            case 14: valor = "A"; break;
            case 11: valor = "J"; break;
            case 12: valor = "Q"; break;
            case 13: valor = "K"; break;
            default: valor = String.valueOf(carta.getValor());
        }

        String palo;
        switch (carta.getPalo()) {
            case PICA: palo = "♠"; break;
            case CORAZON: palo = "♥"; break;
            case DIAMANTE: palo = "♦"; break;
            case TREBOL: palo = "♣"; break;
            default: palo = "?";
        }

        String color = carta.getColor().equals("rojo") ? "#DC143C" : "#000000";

        String textoCarta = valor + palo + "\n\n" + "           " + palo;

        Label cartaLabel = new Label(textoCarta);
        cartaLabel.setMinSize(120, 150);
        cartaLabel.setStyle(
                "-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + color + ";" +
                        "-fx-background-color: white; -fx-border-color: black; -fx-border-width: 2;" +
                        "-fx-background-radius: 8; -fx-border-radius: 8; -fx-alignment: top-left;"
        );

        return cartaLabel;
    }


    //Método para poner el reverso de la carta cuando este volteada.
    private Label crearEtiquetaReverso() {
        Label label = new Label();
        label.setMinSize(120, 150);
        label.setStyle("-fx-background-radius: 10; -fx-border-radius: 10; "
                + "-fx-border-color: #000080; -fx-border-width: 2;");
        Image reverso = new Image(getClass().getResource("/Imagenes/reverso.png").toExternalForm());
        ImageView vista = new ImageView(reverso);
        vista.setFitWidth(120);
        vista.setFitHeight(150);
        label.setGraphic(vista);
        return label;
    }
}
