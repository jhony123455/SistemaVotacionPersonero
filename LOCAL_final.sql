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

CREATE TABLE candidatoporeleccion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    idCandidato INT,
    idEleccion INT,
    FOREIGN KEY (idCandidato) REFERENCES Candidato(Id_Candidato),
    FOREIGN KEY (idEleccion) REFERENCES Eleccion(Id_Eleccion)
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


ALTER TABLE Candidato ADD COLUMN activo BOOLEAN DEFAULT TRUE;


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

CREATE PROCEDURE obtenerTodosLosEstudiantes()
BEGIN
    SELECT * FROM Estudiante;
END//

CREATE PROCEDURE obtenerTodosLosGrados()
BEGIN
   SELECT * FROM Grado;
END//
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
      SELECT COUNT(*) FROM Candidato WHERE activo = TRUE;
END //

CREATE PROCEDURE obtenerCandidatosActivos()
BEGIN
    SELECT c.*, e.Nombre AS nombre, e.Apellido AS apellido, e.Documento AS documento, e.Id_Estudiante AS idEstudiante
    FROM Candidato c
    JOIN Estudiante e ON c.FkEstudiante = e.Id_Estudiante
    WHERE c.activo = TRUE;
END//

CREATE PROCEDURE desactivarTodosLosCandidatos()
BEGIN
    UPDATE Candidato SET activo = FALSE WHERE activo = TRUE;
END//

CREATE PROCEDURE guardarEleccion(IN fechaInicio TIMESTAMP, IN fechaFin TIMESTAMP)
BEGIN
    INSERT INTO Eleccion (fecha_Inicio, fecha_Fin, Estado) VALUES (fechaInicio, fechaFin, 'Activa');
END//


CREATE PROCEDURE insertarEleccion(IN fechaInicio TIMESTAMP, IN fechaFin TIMESTAMP)
BEGIN
    INSERT INTO Eleccion (fecha_Inicio, fecha_Fin, Estado) VALUES (fechaInicio, fechaFin, 'Activa');
END//

CREATE PROCEDURE obtenerTiempoFinalizacion(IN fechaActual TIMESTAMP)
BEGIN
    SELECT fecha_Fin FROM Eleccion WHERE fecha_Inicio <= fechaActual AND fecha_Fin > fechaActual ORDER BY fecha_Fin ASC LIMIT 1;
END//



CREATE PROCEDURE obtenerEleccionActual(IN fechaActual TIMESTAMP)
BEGIN
    SELECT Id_Eleccion, fecha_Inicio, fecha_Fin FROM Eleccion 
    WHERE fecha_Inicio <= fechaActual AND fecha_Fin > fechaActual AND Estado = 'Activa'
    ORDER BY fecha_Fin ASC LIMIT 1;
END//


CREATE PROCEDURE hayEleccionActiva(IN fechaActual TIMESTAMP, OUT activo BOOLEAN)
BEGIN
    SELECT COUNT(*) INTO activo FROM Eleccion 
    WHERE fecha_Inicio <= fechaActual AND fecha_Fin > fechaActual AND Estado = 'Activa';
END//

CREATE PROCEDURE actualizarEstadoEleccion(IN idEleccion INT, IN nuevoEstado VARCHAR(20))
BEGIN
    UPDATE Eleccion SET Estado = nuevoEstado WHERE Id_Eleccion = idEleccion;
END//

CREATE PROCEDURE contarVotosRegistrados()
BEGIN
    SELECT COUNT(*) FROM Voto;
END//

CREATE PROCEDURE verificarEleccionFinalizada()
BEGIN
    SELECT Estado FROM Eleccion WHERE Estado = 'Finalizada';
END//





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
    DECLARE total_votos INT;
    

    SELECT COUNT(*) INTO total_votos 
    FROM Voto 
    WHERE FkEleccion = eleccionId;
    

    SET total_votos = IF(total_votos = 0, 1, total_votos);

   
    SELECT 
        c.Id_Candidato,
        e.Nombre,
        e.Apellido,
        COUNT(v.Id_Voto) AS Votos_Obtenidos,
        ROUND((COUNT(v.Id_Voto) / total_votos * 100), 2) AS Porcentaje_Votos
    FROM 
        candidatoporeleccion ce
        JOIN Candidato c ON ce.idCandidato = c.Id_Candidato
        JOIN Estudiante e ON c.FkEstudiante = e.Id_Estudiante
        LEFT JOIN Voto v ON v.FkCandidato = c.Id_Candidato 
            AND v.FkEleccion = eleccionId
    WHERE 
        ce.idEleccion = eleccionId
    GROUP BY 
        c.Id_Candidato,
        e.Nombre,
        e.Apellido
    
    UNION ALL
    

    SELECT 
        0 AS Id_Candidato,
        'Voto en Blanco' AS Nombre,
        '' AS Apellido,
        COUNT(v.Id_Voto) AS Votos_Obtenidos,
        ROUND((COUNT(v.Id_Voto) / total_votos * 100), 2) AS Porcentaje_Votos
    FROM 
        Voto v
    WHERE 
        v.FkEleccion = eleccionId 
        AND v.FkCandidato IS NULL
    
    ORDER BY 
        Votos_Obtenidos DESC, Id_Candidato;
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




SET GLOBAL event_scheduler = ON;


CREATE EVENT proceso_eleccion
ON SCHEDULE EVERY 10 SECOND
DO
BEGIN
    
    UPDATE Eleccion
    SET Estado = 'Finalizada'
    WHERE Fecha_Fin <= NOW() 
    AND Estado = 'Activa';
    
    
    INSERT INTO candidatoporeleccion (idCandidato, idEleccion)
    SELECT DISTINCT 
        c.Id_Candidato, 
        e.Id_Eleccion
    FROM 
        Eleccion e
        INNER JOIN Candidato c ON c.activo = TRUE
    WHERE 
        e.Estado = 'Finalizada'
        AND NOT EXISTS (
            SELECT 1 
            FROM candidatoporeleccion ce 
            WHERE ce.idCandidato = c.Id_Candidato 
            AND ce.idEleccion = e.Id_Eleccion
        );
    
   
    UPDATE Eleccion
    SET Estado = 'Procesada'
    WHERE Estado = 'Finalizada'
    AND EXISTS (
        SELECT 1 
        FROM candidatoporeleccion ce 
        WHERE ce.idEleccion = Eleccion.Id_Eleccion
    );
END//



CREATE FUNCTION insertarUsuario(
    nombreUsuario VARCHAR(255),
    contrasena VARCHAR(255)
)
RETURNS BOOLEAN
DETERMINISTIC

BEGIN
    DECLARE resultado BOOLEAN;


    IF (SELECT COUNT(*) FROM Usuario WHERE Nombre_Usuario = nombreUsuario) > 0 THEN
        SET resultado = FALSE;
    ELSE
   
        INSERT INTO Usuario (Nombre_Usuario, Contrasena, Id_Rol, Estado)
        VALUES (nombreUsuario, contrasena, 2, 1);
        SET resultado = (ROW_COUNT() > 0);
    END IF;

    RETURN resultado;
END //



CREATE PROCEDURE registrarUsuario(
    IN nombreUsuario VARCHAR(255),
    IN contrasena VARCHAR(255)
)
BEGIN
    DECLARE exito BOOLEAN;

    SET exito = insertarUsuario(nombreUsuario, contrasena);

    IF exito THEN
        SELECT 'Usuario registrado exitosamente' AS Mensaje;
    ELSE
        SELECT 'El usuario ya existe o error en el registro' AS Mensaje;
    END IF;
END //






CREATE FUNCTION desactivarUsuario(
    nombreUsuario VARCHAR(255)
)
RETURNS BOOLEAN
DETERMINISTIC
BEGIN
    DECLARE resultado BOOLEAN;

    UPDATE Usuario
    SET Estado = FALSE
    WHERE Nombre_Usuario = nombreUsuario AND Id_Rol = 2;

    SET resultado = (ROW_COUNT() > 0);
    
    RETURN resultado;
END //

CREATE PROCEDURE desactivarUsuarioProc(
    IN nombreUsuario VARCHAR(255)
)
BEGIN
    DECLARE exito BOOLEAN;

    SET exito = desactivarUsuario(nombreUsuario);

    IF exito THEN
        SELECT 'Usuario desactivado exitosamente' AS Mensaje;
    ELSE
        SELECT 'Error al desactivar el usuario o el usuario no existe' AS Mensaje;
    END IF;
END //

CREATE PROCEDURE iniciarSesion(
    IN nombreUsuario VARCHAR(50),
    IN contrasena VARCHAR(50)
)
BEGIN
    SELECT Id_Usuario, Nombre_Usuario, Contrasena, Id_Rol, Estado
    FROM Usuario
    WHERE Nombre_Usuario = nombreUsuario AND Contrasena = contrasena;
END//

CREATE PROCEDURE autenticarUsuario(
    IN nombreUsuario VARCHAR(50),
    IN contrasena VARCHAR(50)
)
BEGIN
    SELECT Id_Usuario, Nombre_Usuario, Contrasena, Id_Rol, Estado
    FROM Usuario
    WHERE Nombre_Usuario = nombreUsuario;
END//

CREATE PROCEDURE obtenerUsuariosRol2()
BEGIN
    SELECT Id_Usuario, Nombre_Usuario, Contrasena, Id_Rol, Estado
    FROM Usuario
    WHERE Id_Rol = 2;
END//

CREATE PROCEDURE existeUsuario(
    IN nombreUsuario VARCHAR(50),
    OUT existe BOOLEAN
)
BEGIN
    DECLARE count INT;
    SELECT COUNT(*) INTO count FROM Usuario WHERE Nombre_Usuario = nombreUsuario;
    SET existe = count > 0;
END//

-- aparir de aqui vienen los triggers con tablas



CREATE TABLE Estudiante_backup (
    Id_Backup INT PRIMARY KEY AUTO_INCREMENT,
    Id_Estudiante INT,
    Nombre VARCHAR(20),
    Apellido VARCHAR(20),
    Documento VARCHAR(10),
    FkGrado INT,
    Operacion VARCHAR(20),
    Usuario VARCHAR(50),
    Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);



CREATE TRIGGER tr_estudiante_insert 
AFTER INSERT ON Estudiante
FOR EACH ROW
BEGIN
    INSERT INTO Estudiante_backup (Id_Estudiante, Nombre, Apellido, Documento, FkGrado, Operacion, Usuario)
    VALUES (NEW.Id_Estudiante, NEW.Nombre, NEW.Apellido, NEW.Documento, NEW.FkGrado, 'INSERT', USER());
END//

CREATE TRIGGER tr_estudiante_update 
AFTER UPDATE ON Estudiante
FOR EACH ROW
BEGIN
    INSERT INTO Estudiante_backup (Id_Estudiante, Nombre, Apellido, Documento, FkGrado, Operacion, Usuario)
    VALUES (OLD.Id_Estudiante, NEW.Nombre, NEW.Apellido, NEW.Documento, NEW.FkGrado, 'UPDATE', USER());
END//

CREATE TRIGGER tr_estudiante_delete 
AFTER DELETE ON Estudiante
FOR EACH ROW
BEGIN
    INSERT INTO Estudiante_backup (Id_Estudiante, Nombre, Apellido, Documento, FkGrado, Operacion, Usuario)
    VALUES (OLD.Id_Estudiante, OLD.Nombre, OLD.Apellido, OLD.Documento, OLD.FkGrado, 'DELETE', USER());
END//


CREATE TABLE Candidato_backup (
    Id_Backup INT PRIMARY KEY AUTO_INCREMENT,
    Id_Candidato INT,
    FkEstudiante INT,
    Propuesta TEXT,
    activo BOOLEAN,
    Operacion VARCHAR(20),
    Usuario VARCHAR(50),
    Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
); //

CREATE TRIGGER tr_candidato_insert 
AFTER INSERT ON Candidato
FOR EACH ROW
BEGIN
    INSERT INTO Candidato_backup (Id_Candidato, FkEstudiante, Propuesta, activo, Operacion, Usuario)
    VALUES (NEW.Id_Candidato, NEW.FkEstudiante, NEW.Propuesta, NEW.activo, 'INSERT', USER());
END//

CREATE TRIGGER tr_candidato_update 
AFTER UPDATE ON Candidato
FOR EACH ROW
BEGIN
    INSERT INTO Candidato_backup (Id_Candidato, FkEstudiante, Propuesta, activo, Operacion, Usuario)
    VALUES (OLD.Id_Candidato, NEW.FkEstudiante, NEW.Propuesta, NEW.activo, 'UPDATE', USER());
END//

CREATE TRIGGER tr_candidato_delete 
AFTER DELETE ON Candidato
FOR EACH ROW
BEGIN
    INSERT INTO Candidato_backup (Id_Candidato, FkEstudiante, Propuesta, activo, Operacion, Usuario)
    VALUES (OLD.Id_Candidato, OLD.FkEstudiante, OLD.Propuesta, OLD.activo, 'DELETE', USER());
END//

CREATE TABLE Eleccion_backup (
    Id_Backup INT PRIMARY KEY AUTO_INCREMENT,
    Id_Eleccion INT,
    Fecha_Inicio DATETIME,
    Fecha_Fin DATETIME,
    Estado VARCHAR(20),
    Operacion VARCHAR(20),
    Usuario VARCHAR(50),
    Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);//

CREATE TRIGGER tr_eleccion_insert 
AFTER INSERT ON Eleccion
FOR EACH ROW
BEGIN
    INSERT INTO Eleccion_backup (Id_Eleccion, Fecha_Inicio, Fecha_Fin, Estado, Operacion, Usuario)
    VALUES (NEW.Id_Eleccion, NEW.Fecha_Inicio, NEW.Fecha_Fin, NEW.Estado, 'INSERT', USER());
END//

CREATE TRIGGER tr_eleccion_update 
AFTER UPDATE ON Eleccion
FOR EACH ROW
BEGIN
    INSERT INTO Eleccion_backup (Id_Eleccion, Fecha_Inicio, Fecha_Fin, Estado, Operacion, Usuario)
    VALUES (OLD.Id_Eleccion, NEW.Fecha_Inicio, NEW.Fecha_Fin, NEW.Estado, 'UPDATE', USER());
END//

CREATE TRIGGER tr_eleccion_delete 
AFTER DELETE ON Eleccion
FOR EACH ROW
BEGIN
    INSERT INTO Eleccion_backup (Id_Eleccion, Fecha_Inicio, Fecha_Fin, Estado, Operacion, Usuario)
    VALUES (OLD.Id_Eleccion, OLD.Fecha_Inicio, OLD.Fecha_Fin, OLD.Estado, 'DELETE', USER());
END//

CREATE TABLE Voto_backup (
    Id_Backup INT PRIMARY KEY AUTO_INCREMENT,
    Id_Voto INT,
    FkEstudiante INT,
    FkCandidato INT,
    FkEleccion INT,
    Operacion VARCHAR(20),
    Usuario VARCHAR(50),
    Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);//

CREATE TRIGGER tr_voto_insert 
AFTER INSERT ON Voto
FOR EACH ROW
BEGIN
    INSERT INTO Voto_backup (Id_Voto, FkEstudiante, FkCandidato, FkEleccion, Operacion, Usuario)
    VALUES (NEW.Id_Voto, NEW.FkEstudiante, NEW.FkCandidato, NEW.FkEleccion, 'INSERT', USER());
END//

CREATE TRIGGER tr_voto_update 
AFTER UPDATE ON Voto
FOR EACH ROW
BEGIN
    INSERT INTO Voto_backup (Id_Voto, FkEstudiante, FkCandidato, FkEleccion, Operacion, Usuario)
    VALUES (OLD.Id_Voto, NEW.FkEstudiante, NEW.FkCandidato, NEW.FkEleccion, 'UPDATE', USER());
END//

CREATE TRIGGER tr_voto_delete 
AFTER DELETE ON Voto
FOR EACH ROW
BEGIN
    INSERT INTO Voto_backup (Id_Voto, FkEstudiante, FkCandidato, FkEleccion, Operacion, Usuario)
    VALUES (OLD.Id_Voto, OLD.FkEstudiante, OLD.FkCandidato, OLD.FkEleccion, 'DELETE', USER());
END//

CREATE TABLE Usuario_backup (
    Id_Backup INT PRIMARY KEY AUTO_INCREMENT,
    Id_Usuario INT,
    Nombre_Usuario VARCHAR(50),
    Contrasena VARCHAR(255),
    Id_Rol INT,
    Estado TINYINT,
    Fecha_Creacion TIMESTAMP,
    Operacion VARCHAR(20),
    Usuario VARCHAR(50),
    Fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);//

CREATE TRIGGER tr_usuario_insert 
AFTER INSERT ON Usuario
FOR EACH ROW
BEGIN
    INSERT INTO Usuario_backup (Id_Usuario, Nombre_Usuario, Contrasena, Id_Rol, Estado, Fecha_Creacion, Operacion, Usuario)
    VALUES (NEW.Id_Usuario, NEW.Nombre_Usuario, NEW.Contrasena, NEW.Id_Rol, NEW.Estado, NEW.Fecha_Creacion, 'INSERT', USER());
END//

CREATE TRIGGER tr_usuario_update 
AFTER UPDATE ON Usuario
FOR EACH ROW
BEGIN
    INSERT INTO Usuario_backup (Id_Usuario, Nombre_Usuario, Contrasena, Id_Rol, Estado, Fecha_Creacion, Operacion, Usuario)
    VALUES (OLD.Id_Usuario, NEW.Nombre_Usuario, NEW.Contrasena, NEW.Id_Rol, NEW.Estado, NEW.Fecha_Creacion, 'UPDATE', USER());
END//

CREATE TRIGGER tr_usuario_delete 
AFTER DELETE ON Usuario
FOR EACH ROW
BEGIN
    INSERT INTO Usuario_backup (Id_Usuario, Nombre_Usuario, Contrasena, Id_Rol, Estado, Fecha_Creacion, Operacion, Usuario)
    VALUES (OLD.Id_Usuario, OLD.Nombre_Usuario, OLD.Contrasena, OLD.Id_Rol, OLD.Estado, OLD.Fecha_Creacion, 'DELETE', USER());
END//




