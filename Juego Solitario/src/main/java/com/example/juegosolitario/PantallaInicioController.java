package com.example.juegosolitario;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class PantallaInicioController {
    //Botones de la pantalla inicial del juego
    @FXML
    private Button botonJugar;

    @FXML
    private Button botonSalir;

    @FXML
    private Label tituloJuego;

    //Se inicializan los botones y se anima el título
    @FXML
    private void initialize() {
        configurarBotones();
        animarTitulo();
    }
    //Método para configurar los botones.
    private void configurarBotones() {
        botonJugar.setStyle(
                "-fx-background-color: #4CAF50; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-background-radius: 12;"
        );
        botonSalir.setStyle(
                "-fx-background-color: #e53935; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 16px; " +
                        "-fx-background-radius: 12;"
        );

        botonJugar.setOnMouseEntered(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), botonJugar);
            ft.setToValue(0.85);
            ft.play();

            ScaleTransition st = new ScaleTransition(Duration.millis(200), botonJugar);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });

        botonJugar.setOnMouseExited(e -> {
            FadeTransition ft = new FadeTransition(Duration.millis(200), botonJugar);
            ft.setToValue(1.0);
            ft.play();

            ScaleTransition st = new ScaleTransition(Duration.millis(200), botonJugar);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });

        botonSalir.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), botonSalir);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();

            botonSalir.setStyle(
                    "-fx-background-color: #c62828; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 16px; " +
                            "-fx-background-radius: 12;"
            );
        });

        botonSalir.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), botonSalir);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();

            botonSalir.setStyle(
                    "-fx-background-color: #e53935; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 16px; " +
                            "-fx-background-radius: 12;"
            );
        });
    }

    //Método para realizar la animación del título del juego
    private void animarTitulo() {
        FadeTransition ft = new FadeTransition(Duration.seconds(2), tituloJuego);
        ft.setFromValue(0.2);
        ft.setToValue(1.0);
        ft.setCycleCount(FadeTransition.INDEFINITE);
        ft.setAutoReverse(true);
        ft.play();
    }

    //Método que realiza la acción al momento de seleccionar Jugar
    @FXML
    private void jugar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/interfaz/tablero-solitario.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) botonJugar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Jugar");
            stage.setResizable(true);
            stage.setMinWidth(1920);
            stage.setMinHeight(1080);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void salir() {
        Stage stage = (Stage) botonSalir.getScene().getWindow();
        stage.close();
    }
}
