
Drop database if exists bcap7i33wbcrx2ercjxm; 

create database bcap7i33wbcrx2ercjxm;
use bcap7i33wbcrx2ercjxm;


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
    Id_Resultados INT PRIMARY KEY auto_increment,
    Votos_Candidatos INT,
    Total_Votos INT,
    Ganador INT,
    FOREIGN KEY (Ganador) REFERENCES Candidato(Id_Candidato)
);


INSERT INTO Grado (NombreGrado) VALUES ('1º'), ('2º'), ('3º'), ('4º'), ('5º'), ('6º'), ('7º'), ('8º'), ('9º'), ("10º"), ("11º");
INSERT INTO Rol (Nombre_rol) values ("Administrador"), ("Usuario");
insert into Usuario (Nombre_Usuario, Contrasena, Id_Rol) values ("Jhony", "JhonatanP", 1);

select NombreGrado from Grado;
select* from Estudiante;
select*from Candidato;
select*from Voto;
select*from Eleccion;
select*from Rol;
select *from Usuario;

ALTER TABLE Eleccion
MODIFY COLUMN Fecha_Inicio DATETIME DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN Fecha_Fin DATETIME DEFAULT CURRENT_TIMESTAMP;


DELIMITER //

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

SET SQL_SAFE_UPDATES = 0;//

DELETE FROM Voto;//
DELETE FROM Eleccion;//

SET SQL_SAFE_UPDATES = 1;//


SELECT COUNT(*) FROM Eleccion WHERE Fecha_Inicio <= NOW() AND Fecha_Fin >= NOW();//
ALTER TABLE Eleccion
ADD COLUMN Estado VARCHAR(20) DEFAULT 'Activa';//


CREATE EVENT update_election_status
ON SCHEDULE EVERY 30 second
DO
BEGIN
    UPDATE Eleccion
    SET Estado = 'Finalizada'
    WHERE Fecha_Fin < NOW() AND Estado = 'Activa';
END//

ALTER TABLE Voto MODIFY FkCandidato INT NULL;