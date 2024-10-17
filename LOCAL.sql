Drop database if exists eleccionpersonero; 

create database eleccionpersonero;
use eleccionpersonero;


CREATE TABLE Rol (
    Id_Rol INT PRIMARY KEY AUTO_INCREMENT,
    Nombre_Rol VARCHAR(50) NOT NULL UNIQUE 
);

CREATE TABLE Usuario (
    Id_Usuario INT PRIMARY KEY AUTO_INCREMENT,
    Nombre_Usuario VARCHAR(50) NOT NULL UNIQUE,
    Contrasena VARCHAR(255) NOT NULL,
    Id_Rol INT, 
    Estado TINYINT DEFAULT 1, 
    Fecha_Creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Id_Rol) REFERENCES Rol(Id_Rol)
);




CREATE TABLE Grado (
    Id_Grado INT PRIMARY KEY auto_increment,
    NombreGrado VARCHAR(3)
);


CREATE TABLE Estudiante (
    Id_Estudiante INT PRIMARY KEY auto_increment,
    Nombre VARCHAR(20),
    Apellido VARCHAR(20),
    Documento VARCHAR(10),
    FkGrado INT,
    FOREIGN KEY (FkGrado) REFERENCES Grado(Id_Grado)
);


CREATE TABLE Candidato (
    Id_Candidato INT PRIMARY KEY auto_increment,
    FkEstudiante INT,
    Propuesta TEXT,
    FOREIGN KEY (FkEstudiante) REFERENCES Estudiante(Id_Estudiante)
);


CREATE TABLE Eleccion (
    Id_Eleccion INT PRIMARY KEY auto_increment,
    Fecha_Inicio DATE,
    Fecha_Fin DATE
);


CREATE TABLE Voto (
    Id_Voto INT PRIMARY KEY auto_increment,
    FkEstudiante INT,
    FkCandidato INT,
    FkEleccion INT,
    FOREIGN KEY (FkEstudiante) REFERENCES Estudiante(Id_Estudiante),
    FOREIGN KEY (FkCandidato) REFERENCES Candidato(Id_Candidato),
    FOREIGN KEY (FkEleccion) REFERENCES Eleccion(Id_Eleccion)
);


CREATE TABLE Resultados (
    Id_Resultados INT PRIMARY KEY AUTO_INCREMENT,
    FkEleccion INT,
    Total_Votos INT,
    Fecha_Conteo DATE,
    FOREIGN KEY (FkEleccion) REFERENCES Eleccion(Id_Eleccion)
);


INSERT INTO Grado (NombreGrado) VALUES ('1º'), ('2º'), ('3º'), ('4º'), ('5º'), ('6º'), ('7º'), ('8º'), ('9º'), ("10º"), ("11º");
INSERT INTO Rol (Nombre_rol) values ("Administrador"), ("Usuario");
insert into Usuario (Nombre_Usuario, Contrasena, Id_Rol) values ("Jhony", "JhonatanP", 1) , ("Valentina", "gatopizza29", 1);
INSERT INTO Estudiante (Nombre, Apellido, Documento, FkGrado)
VALUES 
('Alejandro', 'Perez', '1234567890', 1), 
('Beatriz', 'Gomez', '1234567891', 2), 
('Carlos', 'Ramirez', '1234567892', 3), 
('Diana', 'Hernandez', '1234567893', 4),
('Eduardo', 'Ruiz', '1234567894', 5),
('Fernanda', 'Ortega', '1234567895', 6),
('Gabriela', 'Mejia', '1234567896', 7),
('Hector', 'Castillo', '1234567897', 8),
('Camilo', 'Rodriguez', '1234567898', 9),
('Cristina', 'Mancilla', '1234567899', 10),
('Carlos', 'Pérez', '123456789', 11),
    ('Ana', 'Gómez', '987654321', 11),
    ('Luis', 'Martínez', '456789123', 11);



ALTER TABLE Eleccion
MODIFY COLUMN Fecha_Inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN Fecha_Fin DATETIME DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE Voto MODIFY FkCandidato INT NULL;
SELECT COUNT(*) FROM Eleccion WHERE Fecha_Inicio <= NOW() AND Fecha_Fin >= NOW();
ALTER TABLE Eleccion
ADD COLUMN Estado VARCHAR(20) DEFAULT 'Activa';

DELIMITER //

-- procedimientos y funciones

CREATE PROCEDURE insertarEstudiante (
    IN p_nombre VARCHAR(50),
    IN p_apellido VARCHAR(50),
    IN p_documento VARCHAR(20),
    IN p_fkGrado INT
)
BEGIN
    INSERT INTO Estudiante (Nombre, Apellido, Documento, FkGrado) 
    VALUES (p_nombre, p_apellido, p_documento, p_fkGrado);
END //


CREATE PROCEDURE buscarEstudiantePorId (IN p_idEstudiante INT)
BEGIN
    SELECT Id_Estudiante, Nombre, Apellido, Documento, FkGrado
    FROM Estudiante
    WHERE Id_Estudiante = p_idEstudiante;
END //

CREATE PROCEDURE buscarEstudiantePorNombreApellido (
    IN p_nombre VARCHAR(50),
    IN p_apellido VARCHAR(50)
)
BEGIN
    SELECT * FROM Estudiante 
    WHERE Nombre = p_nombre AND Apellido = p_apellido;
END //


CREATE PROCEDURE contarEstudiantesGrado11()
BEGIN
    SELECT COUNT(*) FROM Estudiante WHERE FkGrado = (SELECT Id_Grado FROM Grado WHERE NombreGrado = '11º');
END;

CREATE PROCEDURE contarTodosEstudiantes()
BEGIN
    SELECT COUNT(*) FROM Estudiante;
END;

CREATE PROCEDURE contarEstudiantesPorGrado(IN grado INT)
BEGIN
    SELECT COUNT(*) FROM Estudiante WHERE FkGrado = grado;
END;


CREATE PROCEDURE buscarEstudiantePorDocumento (
    IN p_documento VARCHAR(20)
)
BEGIN
    SELECT * FROM Estudiante WHERE Documento = p_documento;
END //

CREATE PROCEDURE obtenerEstudiantesPorGrado (
    IN p_nombreGrado VARCHAR(50)
)
BEGIN
    SELECT e.* 
    FROM Estudiante e
    INNER JOIN Grado g ON e.FkGrado = g.Id_Grado
    WHERE g.NombreGrado = p_nombreGrado;
END //

CREATE PROCEDURE ObtenerGradoPorId(IN p_idGrado INT)
BEGIN
    SELECT * FROM Grado WHERE Id_Grado = p_idGrado;
END //

CREATE PROCEDURE BuscarIdGradoPorNombre(IN p_nombreGrado VARCHAR(255))
BEGIN
    SELECT Id_Grado FROM Grado WHERE NombreGrado = p_nombreGrado;
END //

CREATE PROCEDURE agregarCandidato(IN p_fkEstudiante INT, IN p_propuesta VARCHAR(255))
BEGIN
    INSERT INTO Candidato (FkEstudiante, Propuesta) VALUES (p_fkEstudiante, p_propuesta);
END //

CREATE PROCEDURE obtenerCandidatos()
BEGIN
    SELECT c.*, e.Nombre, e.Apellido, e.Documento, g.NombreGrado 
    FROM Candidato c 
    INNER JOIN Estudiante e ON c.FkEstudiante = e.Id_Estudiante 
    INNER JOIN Grado g ON e.FkGrado = g.Id_Grado;
END //

CREATE PROCEDURE contarCandidatos()
BEGIN
    SELECT COUNT(*) FROM Candidato;
END //

CREATE PROCEDURE borrarCandidatos()
BEGIN
    DELETE FROM Candidato;
END //

CREATE PROCEDURE insertarEleccion(IN fecha_inicio DATETIME, IN fecha_fin DATETIME)
BEGIN
    DECLARE active_elections INT;


    SELECT COUNT(*) INTO active_elections
    FROM Eleccion
    WHERE NOW() BETWEEN Fecha_Inicio AND Fecha_Fin;


    IF active_elections > 0 THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'No se puede crear una nueva elección mientras haya una activa';
    ELSE
    
        INSERT INTO Eleccion (Fecha_Inicio, Fecha_Fin) 
        VALUES (fecha_inicio, fecha_fin);
    END IF;
END//

CREATE PROCEDURE guardarEleccion(IN p_fechaInicio DATETIME, IN p_fechaFin DATETIME)
BEGIN
    INSERT INTO Eleccion (Fecha_Inicio, Fecha_Fin) VALUES (p_fechaInicio, p_fechaFin);
END //

CREATE PROCEDURE obtenerEleccionActual(IN p_now DATETIME)
BEGIN
    SELECT * FROM Eleccion 
    WHERE Fecha_Inicio <= p_now AND Fecha_Fin >= p_now 
    ORDER BY Fecha_Fin DESC LIMIT 1;
END //

CREATE PROCEDURE hayEleccionActiva(IN p_now DATETIME)
BEGIN
    SELECT COUNT(*) FROM Eleccion 
    WHERE Fecha_Inicio <= p_now AND Fecha_Fin >= p_now AND Estado = 'Activa';
END //

CREATE PROCEDURE guardarVoto(IN p_fkEleccion INT, IN p_fkCandidato INT, IN p_fkEstudiante INT)
BEGIN
    INSERT INTO Voto (FkEleccion, FkCandidato, FkEstudiante) VALUES (p_fkEleccion, p_fkCandidato, p_fkEstudiante);
END //

CREATE PROCEDURE estudianteYaVoto(IN p_idEstudiante INT, IN p_idEleccion INT)
BEGIN
    SELECT COUNT(*) FROM Voto WHERE FkEstudiante = p_idEstudiante AND FkEleccion = p_idEleccion;
END //

CREATE PROCEDURE ObtenerElecciones()
BEGIN
    SELECT Id_Eleccion, Fecha_Inicio, Fecha_Fin
    FROM Eleccion
    ORDER BY Fecha_Inicio DESC;
END //

CREATE PROCEDURE ObtenerResultadosPorCandidato(IN eleccionId INT)
BEGIN
        SELECT 
        COALESCE(c.Id_Candidato, 0) AS ID_Candidato,
        COALESCE(e.Nombre, 'Voto en Blanco') AS Nombre,
        COALESCE(e.Apellido, '') AS Apellido,
        COUNT(v.Id_Voto) AS Votos_Obtenidos
    FROM 
        Voto v
        LEFT JOIN Candidato c ON v.FkCandidato = c.Id_Candidato
        LEFT JOIN Estudiante e ON c.FkEstudiante = e.Id_Estudiante
    WHERE 
        v.FkEleccion = eleccionId
    GROUP BY 
        COALESCE(c.Id_Candidato, 0),
        COALESCE(e.Nombre, 'Voto en Blanco'),
        COALESCE(e.Apellido, '')
    ORDER BY 
        ID_Candidato;
END //

CREATE PROCEDURE ObtenerResultadosPorGrado(IN eleccionId INT, IN gradoId INT)
BEGIN
     SELECT 
        COALESCE(c.Id_Candidato, 0) AS ID_Candidato,
        COALESCE(e.Nombre, 'Voto en Blanco') AS Nombre,
        COALESCE(e.Apellido, '') AS Apellido,
        g.NombreGrado AS Grado,
        COUNT(v.Id_Voto) AS Votos_Obtenidos
    FROM 
        Voto v
        JOIN Estudiante e_votante ON v.FkEstudiante = e_votante.Id_Estudiante
        JOIN Grado g ON e_votante.FkGrado = g.Id_Grado
        LEFT JOIN Candidato c ON v.FkCandidato = c.Id_Candidato
        LEFT JOIN Estudiante e ON c.FkEstudiante = e.Id_Estudiante
    WHERE 
        v.FkEleccion = eleccionId
        AND g.Id_Grado = gradoId
    GROUP BY 
        COALESCE(c.Id_Candidato, 0),
        COALESCE(e.Nombre, 'Voto en Blanco'),
        COALESCE(e.Apellido, ''),
        g.NombreGrado
    ORDER BY 
        g.NombreGrado, Votos_Obtenidos DESC;
END //

CREATE PROCEDURE LlenarResultados(IN pEleccionId INT)
BEGIN
   
	IF NOT EXISTS (SELECT 1 FROM Resultados WHERE FkEleccion = pEleccionId) THEN
        INSERT INTO Resultados (FkEleccion, Total_Votos, Fecha_Conteo)
        SELECT pEleccionId, COUNT(v.Id_Voto), NOW()
        FROM Voto v
        WHERE v.FkEleccion = pEleccionId;
    END IF;
END//

CREATE PROCEDURE ObtenerResultadosTotales(IN pEleccionId INT)
BEGIN
    SELECT 
        r.FkEleccion,
        r.Total_Votos,
        r.Fecha_Conteo
    FROM 
        Resultados r
    WHERE 
        r.FkEleccion = pEleccionId;
END//


SET SQL_SAFE_UPDATES = 0;//

DELETE FROM Voto;//
DELETE FROM Eleccion;//

SET SQL_SAFE_UPDATES = 1;//




-- evento que actualiza el estado de la eleccion 
CREATE EVENT update_election_status
ON SCHEDULE EVERY 1 minute
DO
BEGIN
    UPDATE Eleccion
    SET Estado = 'Finalizada'
    WHERE Fecha_Fin < NOW() AND Estado = 'Activa';
END//


