package Entities;

import java.time.LocalDateTime;

public class Jugador {
    private int id;
    private String nombre;
    private String pseudonimo;
    private LocalDateTime fechaRegistro;

    // Constructores, getters y setters
    public Jugador() {}

    public Jugador(String nombre, String pseudonimo) {
        this.nombre = nombre;
        this.pseudonimo = pseudonimo;
        this.fechaRegistro = LocalDateTime.now();
    }

    // Getters y setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getPseudonimo() { return pseudonimo; }
    public void setPseudonimo(String pseudonimo) { this.pseudonimo = pseudonimo; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
}