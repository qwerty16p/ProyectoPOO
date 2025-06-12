package Entities;

import java.time.LocalDateTime;

public class Partida {
    private int id;
    private int idJugador;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private int puntaje;
    private String pseudonimoJugador; // Campo transitorio para reportes

    // Constructores
    public Partida() {
    }

    public Partida(int idJugador) {
        this.idJugador = idJugador;
        this.fechaHoraInicio = LocalDateTime.now();
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public void setIdJugador(int idJugador) {
        this.idJugador = idJugador;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }

    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public void setPuntaje(int puntaje) {
        this.puntaje = puntaje;
    }

    public long getDuracionSegundos() {
        return fechaHoraFin != null ? 
            java.time.Duration.between(fechaHoraInicio, fechaHoraFin).getSeconds() : 0;
    }

    // Métodos para el pseudónimo del jugador (transitorio)
    public String getPseudonimoJugador() {
        return pseudonimoJugador;
    }

    public void setPseudonimoJugador(String pseudonimoJugador) {
        this.pseudonimoJugador = pseudonimoJugador;
    }

    // Método toString para depuración
    @Override
    public String toString() {
        return "Partida{" +
                "id=" + id +
                ", idJugador=" + idJugador +
                ", fechaHoraInicio=" + fechaHoraInicio +
                ", fechaHoraFin=" + fechaHoraFin +
                ", puntaje=" + puntaje +
                ", duracionSegundos=" + getDuracionSegundos() +
                ", pseudonimoJugador='" + pseudonimoJugador + '\'' +
                '}';
    }
}