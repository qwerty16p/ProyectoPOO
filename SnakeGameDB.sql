-- Creación de la base de datos
CREATE DATABASE SnakeGameDB;
USE SnakeGameDB;

-- Tabla de jugadores
CREATE TABLE Jugadores (
    IdJugador INT IDENTITY(1,1) PRIMARY KEY,
    Nombre NVARCHAR(100) NOT NULL,
    Pseudonimo NVARCHAR(50) NOT NULL UNIQUE,
    FechaRegistro DATETIME DEFAULT GETDATE()
);


-- Tabla de partidas
CREATE TABLE Partidas (
    IdPartida INT IDENTITY(1,1) PRIMARY KEY,
    IdJugador INT NOT NULL,
    FechaHoraInicio DATETIME NOT NULL DEFAULT GETDATE(),
    FechaHoraFin DATETIME NULL,
    Puntaje INT NOT NULL DEFAULT 0,
    DuracionSegundos AS DATEDIFF(SECOND, FechaHoraInicio, FechaHoraFin),
    FOREIGN KEY (IdJugador) REFERENCES Jugadores(IdJugador)
);


-- Tabla de movimientos (opcional para análisis detallado)
CREATE TABLE Movimientos (
    IdMovimiento INT IDENTITY(1,1) PRIMARY KEY,
    IdPartida INT NOT NULL,
    FechaHora DATETIME NOT NULL DEFAULT GETDATE(),
    Direccion NVARCHAR(10) NOT NULL, -- 'UP', 'DOWN', 'LEFT', 'RIGHT'
    FOREIGN KEY (IdPartida) REFERENCES Partidas(IdPartida)
);


-- Índices para mejorar el rendimiento de las consultas
CREATE INDEX IX_Partidas_Jugador ON Partidas(IdJugador);
CREATE INDEX IX_Partidas_FechaInicio ON Partidas(FechaHoraInicio);
CREATE INDEX IX_Partidas_Puntaje ON Partidas(Puntaje);

SELECT * FROM Partidas

SELECT * FROM Jugadores