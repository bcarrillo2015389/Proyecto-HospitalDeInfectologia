-- Programador: Brian Carrillo 2015389
-- Control de versiones
-- Fecha de creacion 24/05/2019
	-- Modifaciones
		-- Todas las tablas y procedimientos de Pacientes, Horarios, Medicos
        -- Responsable Turno, Contacto Urgencia, Telefono Medico 26/05/2019
        
        -- Todos los procedimientos de Especialidades, Areas, Cargos, Medico_Especialidad
		-- Tabla Turno y sus respectivos procedimientos  28/05/2019
        -- Creacion de los triggers 28/06/2019
        -- Creacion de procedimiento para vista Pacientes 05/07/2019

DROP DATABASE IF EXISTS DBHospitalInfectologia2015389;
CREATE DATABASE DBHospitalInfectologia2015389;
USE DBHospitalInfectologia2015389;

CREATE TABLE Horarios (
	codigoHorario int not null auto_increment,
    horarioInicio varchar(10) not null,
    horarioSalida varchar(10) not null,
	lunes boolean,
    martes boolean,
    miercoles boolean,
    jueves boolean,
    viernes boolean,
	
    primary key PK_codigoHorario (codigoHorario)
);

CREATE TABLE Medicos (
	codigoMedico int not null auto_increment,
    licenciaMedica integer not null unique,
    nombres varchar(100) not null,
    apellidos varchar(100) not null,
    horaEntrada varchar(10) not null,
    horaSalida varchar(10) not null,
    turnoMaximo int default 0,
    sexo varchar(20) not null,
    
    primary key PK_codigoMedico (codigoMedico)
);

CREATE TABLE Especialidades(
	codigoEspecialidad int not null auto_increment,
    nombreEspecialidad varchar(45) not null,
    
	primary key PK_codigoEspecialidad (codigoEspecialidad)
);

CREATE TABLE Areas (
	codigoArea int not null auto_increment,
    nombreArea varchar(45) not null,
    
    primary key PK_codigoArea (codigoArea)
);

CREATE TABLE Cargos (
	codigoCargo int not null auto_increment,
    nombreCargo varchar(45) not null,

	primary key PK_codigoCargo (codigoCargo)
);

CREATE TABLE Pacientes (
	codigoPaciente int not null auto_increment,
    DPI varchar(20) not null unique,
    apellidos varchar(100) not null,
    nombres varchar(100) not null,
    fechaNacimiento date not null,
    edad int default 0,
    direccion varchar(150) not null,
    ocupacion varchar(50) not null,
    sexo varchar(15) not null,
    
    primary key PK_codigoPaciente (codigoPaciente)
);

CREATE TABLE TelefonosMedico(
	codigoTelefonoMedico int not null auto_increment,
	telefonoPersonal  varchar(15) not null,
    telefonoTrabajo varchar(15),
    codigoMedico int not null unique,
    
    primary key PK_codigoTelefonoMedico (codigoTelefonoMedico),
    constraint FK_TelefonosMedico_Medicos foreign key(codigoMedico)
		references Medicos (codigoMedico) ON DELETE CASCADE
);

CREATE TABLE ContactoUrgencia (
	codigoContactoUrgencia int not null auto_increment,
	nombres varchar(100) not null,
	apellidos varchar(100) not null,
	numeroContacto varchar(10) not null,
    codigoPaciente int not null unique,
        
    primary key PK_codigoContactoUrgencia (codigoContactoUrgencia),
    constraint FK_ContactoUrgencia_Pacientes foreign key (codigoPaciente)
		references Pacientes (codigoPaciente) ON DELETE CASCADE
);

CREATE TABLE ResponsableTurno (
	codigoResponsableTurno int not null auto_increment,
    nombreResponsable varchar(75) not null,
    apellidosResponsable varchar(45) not null,
    telefonoPersonal varchar(10) not null,
    codigoArea int not null,
    codigoCargo int not null,
    
    primary key PK_codigoResponsableTurno (codigoResponsableTurno),
    constraint FK_ResponsableTurno_Areas foreign key (codigoArea)
			references Areas (codigoArea) ON DELETE CASCADE,
	constraint FK_ResponsableTurno_Cargos foreign key (codigoCargo)
			references Cargos (codigoCargo) ON DELETE CASCADE
);

CREATE TABLE Medico_Especialidad (
	codigoMedicoEspecialidad int not null auto_increment,
    codigoMedico int not null,
    codigoEspecialidad int not null,
    codigoHorario int not null,
    
    primary key PK_codigoMedicoEspecialidad (codigoMedicoEspecialidad),
    constraint FK_Medico_Especialidad_Medicos foreign key (codigoMedico)
		references Medicos (codigoMedico) ON DELETE CASCADE,
	constraint FK_Medico_Especialidad_Especialidades foreign key (codigoEspecialidad)
		references Especialidades (codigoEspecialidad) ON DELETE CASCADE,
	constraint FK_Medico_Especialidad_Horarios foreign key (codigoHorario)
		references Horarios (codigoHorario) ON DELETE CASCADE
);

CREATE TABLE Turno (
	codigoTurno int not null auto_increment,
    fechaTurno date not null,
	fechaCita date not null,
    valorCita decimal(10,2) not null,
    codigoMedicoEspecialidad int not null,
    codigoTurnoResponsable int not null,
    codigoPaciente int not null,
    
    primary key PK_codigoTurno (codigoTurno),
    constraint FK_Turno_Medico_Especialidad foreign key(codigoMedicoEspecialidad)
		references Medico_Especialidad (codigoMedicoEspecialidad) ON DELETE CASCADE,
	constraint FK_Turno_ResponsableTurno foreign key(codigoTurnoResponsable)
		references ResponsableTurno (codigoResponsableTurno)ON DELETE CASCADE,
	constraint FK_Turno_Pacientes foreign key (codigoPaciente)
		references Pacientes (codigoPaciente) ON DELETE CASCADE
);

-- Funcion Sumar Turnos
Delimiter $$
CREATE FUNCTION fn_SumarTurnos(lunes boolean, martes boolean, miercoles boolean, jueves boolean, viernes boolean)
RETURNS int
READS SQL DATA DETERMINISTIC
BEGIN
DECLARE suma int default 0;

	IF lunes=1 THEN 
		SET suma=suma+1;
	END IF;

	IF martes=1 THEN
		SET suma=suma+1;
	END IF;

	IF miercoles=1 THEN
			SET suma=suma+1;
	END IF;

	IF jueves=1 THEN
		SET suma=suma+1;
	END IF;

	IF viernes=1 THEN
		SET suma=suma+1;
	END IF;

	return suma;
END$$
Delimiter ;

Delimiter $$
CREATE TRIGGER tr_Medicos_After_Insert AFTER INSERT ON Medico_Especialidad FOR EACH ROW
BEGIN
DECLARE turnos int default 0;
DECLARE lunes boolean;
DECLARE martes boolean;
DECLARE miercoles boolean;
DECLARE jueves boolean;
DECLARE viernes boolean;

SET lunes=(select Horarios.lunes from Horarios WHERE Horarios.codigoHorario=new.codigoHorario);
SET martes=(select Horarios.martes from Horarios WHERE Horarios.codigoHorario=new.codigoHorario);
SET miercoles=(select Horarios.miercoles from Horarios WHERE Horarios.codigoHorario=new.codigoHorario);
SET jueves=(select Horarios.jueves from Horarios WHERE Horarios.codigoHorario=new.codigoHorario);
SET viernes=(select Horarios.viernes from Horarios WHERE Horarios.codigoHorario=new.codigoHorario);

SET turnos=fn_SumarTurnos(lunes,martes,miercoles,jueves,viernes);

UPDATE Medicos SET Medicos.turnoMaximo=turnos WHERE codigoMedico=new.codigoMedico;

END$$
Delimiter ;

Delimiter $$
CREATE TRIGGER tr_Medico_Especialidad_After_Update AFTER UPDATE ON Horarios FOR EACH ROW
BEGIN

UPDATE Medico_Especialidad SET Medico_Especialidad.codigoHorario=new.codigoHorario WHERE Medico_Especialidad.codigoHorario=new.codigoHorario;

END$$
Delimiter ;

Delimiter $$
CREATE TRIGGER tr_Medicos_After_Update AFTER UPDATE ON Medico_Especialidad FOR EACH ROW
BEGIN
DECLARE turnos int default 0;
DECLARE lunes boolean;
DECLARE martes boolean;
DECLARE miercoles boolean;
DECLARE jueves boolean;
DECLARE viernes boolean;

SET lunes=(select Horarios.lunes from Horarios WHERE Horarios.codigoHorario=new.codigoHorario);
SET martes=(select Horarios.martes from Horarios WHERE Horarios.codigoHorario=new.codigoHorario);
SET miercoles=(select Horarios.miercoles from Horarios WHERE Horarios.codigoHorario=new.codigoHorario);
SET jueves=(select Horarios.jueves from Horarios WHERE Horarios.codigoHorario=new.codigoHorario);
SET viernes=(select Horarios.viernes from Horarios WHERE Horarios.codigoHorario=new.codigoHorario);

SET turnos=fn_SumarTurnos(lunes,martes,miercoles,jueves,viernes);

UPDATE Medicos SET Medicos.turnoMaximo=turnos WHERE codigoMedico=new.codigoMedico;

END$$
Delimiter ;

-- Procedimientos tabla Horarios
Delimiter $$
CREATE PROCEDURE sp_AgregarHorario(IN horarioInicio varchar(10), IN horarioSalida varchar(10),
									IN lunes boolean, IN martes boolean, IN miercoles boolean, IN jueves boolean, IN viernes boolean)
BEGIN
	INSERT INTO Horarios (horarioInicio, horarioSalida, lunes, martes, miercoles, jueves, viernes)
		VALUES(horarioInicio, horarioSalida, lunes, martes, miercoles, jueves, viernes);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ActualizarHorario(IN codigo int, IN horarioInicio varchar(10), IN horarioSalida varchar(10),
									IN lunes boolean, IN martes boolean, IN miercoles boolean, IN jueves boolean, IN viernes boolean)
BEGIN
	UPDATE Horarios SET horarioInicio=horarioInicio, horarioSalida=horarioSalida, lunes=lunes, martes=martes, 
		miercoles=miercoles, jueves=jueves, viernes=viernes WHERE codigoHorario=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarHorario(IN codigo int)
BEGIN
	DELETE FROM Horarios WHERE codigoHorario=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ListarHorarios()
BEGIN
	SELECT
		Horarios.codigoHorario, 
        Horarios.horarioInicio, 
        Horarios.horarioSalida, 
        Horarios.lunes, 
        Horarios.martes, 
        Horarios.miercoles, 
        Horarios.jueves, 
        Horarios.viernes
        FROM Horarios;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarHorario(IN codigo int)
BEGIN
	SELECT
		Horarios.codigoHorario, 
        Horarios.horarioInicio, 
        Horarios.horarioSalida, 
        Horarios.lunes, 
        Horarios.martes, 
        Horarios.miercoles, 
        Horarios.jueves, 
        Horarios.viernes
        FROM Horarios WHERE codigoHorario=codigo;
END$$
Delimiter ;

-- Procedimientos tabla Medicos
Delimiter $$
CREATE PROCEDURE sp_AgregarMedico(IN licenciaMedica integer, IN nombres varchar(100), IN apellidos varchar(100), 
									IN horaEntrada varchar(10), IN horaSalida varchar(10), IN sexo varchar(20))
BEGIN
	INSERT INTO Medicos (licenciaMedica, nombres, apellidos, horaEntrada, horaSalida, sexo)
		VALUES(licenciaMedica, nombres, apellidos, horaEntrada, horaSalida, sexo);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ActualizarMedico(IN codigo int, IN licenciaMedica integer, IN nombres varchar(100), 
					IN apellidos varchar(100), IN horaEntrada varchar(10), IN horaSalida varchar(10), IN sexo varchar(20))
BEGIN
	UPDATE Medicos SET licenciaMedica=licenciaMedica, nombres=nombres, apellidos=apellidos, 
			horaEntrada=horaEntrada, horaSalida=horaSalida, sexo=sexo WHERE codigoMedico=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarMedico(IN codigo int)
BEGIN
	DELETE FROM Medicos WHERE codigoMedico=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ListarMedicos()
BEGIN
	SELECT
		Medicos.codigoMedico, 
        Medicos.licenciaMedica, 
        Medicos.nombres, 
        Medicos.apellidos, 
        Medicos.horaEntrada, 
        Medicos.horaSalida, 
        Medicos.turnoMaximo, 
        Medicos.sexo
        FROM Medicos;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarMedico(IN codigo int)
BEGIN
	SELECT
		Medicos.codigoMedico, 
        Medicos.licenciaMedica, 
        Medicos.nombres, 
        Medicos.apellidos, 
        Medicos.horaEntrada, 
        Medicos.horaSalida, 
        Medicos.turnoMaximo, 
        Medicos.sexo
        FROM Medicos WHERE codigoMedico=codigo;
END$$
Delimiter ;

-- Procedimientos tabla Especialidades
Delimiter $$
CREATE PROCEDURE sp_AgregarEspecialidad (IN nombreEspecialidad varchar(45))
BEGIN
	INSERT INTO Especialidades (nombreEspecialidad) VALUES (nombreEspecialidad);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ActualizarEspecialidad(IN codigo int, IN nombreEspecialidad varchar(45))
BEGIN
	UPDATE Especialidades SET nombreEspecialidad=nombreEspecialidad WHERE codigoEspecialidad=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarEspecialidad(IN codigo int)
BEGIN
	DELETE FROM Especialidades WHERE codigoEspecialidad=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ListarEspecialidades()
BEGIN
	SELECT
			Especialidades.codigoEspecialidad,
            Especialidades.nombreEspecialidad
			FROM Especialidades;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarEspecialidad(IN codigo int)
BEGIN
	SELECT
			Especialidades.codigoEspecialidad,
            Especialidades.nombreEspecialidad
			FROM Especialidades WHERE codigoEspecialidad=codigo;
END$$
Delimiter ;
-- Procedimientos tabla Areas
Delimiter $$
CREATE PROCEDURE sp_AgregarArea(IN nombreArea varchar(45))
BEGIN
	INSERT INTO Areas (nombreArea)
		VALUES(nombreArea);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ActualizarArea(IN codigo int, IN nombreArea varchar(45))
BEGIN
	UPDATE Areas SET nombreArea=nombreArea WHERE codigoArea=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarArea(IN codigo int)
BEGIN
	DELETE FROM Areas WHERE codigoArea=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ListarAreas()
BEGIN
	SELECT
		Areas.codigoArea,
        Areas.nombreArea
        FROM Areas;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarArea(IN codigo int)
BEGIN
		SELECT
		Areas.codigoArea,
        Areas.nombreArea
        FROM Areas WHERE codigoArea=codigo;
END$$
Delimiter ;

-- Procedimientos tabla Cargos
Delimiter $$
CREATE PROCEDURE sp_AgregarCargo(IN nombreCargo varchar(45))
BEGIN
	INSERT INTO Cargos (nombreCargo)
		VALUES(nombreCargo);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ActualizarCargo(IN codigo int, IN nombreCargo varchar(45))
BEGIN
	UPDATE Cargos SET nombreCargo=nombreCargo WHERE codigoCargo=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarCargo(IN codigo int)
BEGIN
	DELETE FROM Cargos WHERE codigoCargo=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ListarCargos()
BEGIN
	SELECT
		Cargos.codigoCargo,
        Cargos.nombreCargo
        FROM Cargos;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarCargo(IN codigo int)
BEGIN
SELECT
		Cargos.codigoCargo,
		Cargos.nombreCargo
        FROM Cargos WHERE codigoCargo=codigo;
END$$
Delimiter ;

-- Procedimientos tabla Pacientes

-- Triggers de Edad
Delimiter $$
CREATE TRIGGER tr_Pacientes_Before_Insert 
BEFORE INSERT ON Pacientes FOR EACH ROW
BEGIN
	SET NEW.edad= TIMESTAMPDIFF(YEAR, NEW.fechaNacimiento, curdate()); 
END$$
Delimiter ;

Delimiter $$
CREATE TRIGGER tr_Pacientes_Before_Update 
BEFORE UPDATE ON Pacientes FOR EACH ROW
BEGIN
	SET NEW.edad= TIMESTAMPDIFF(YEAR, NEW.fechaNacimiento, curdate()); 
END$$
Delimiter ;


Delimiter $$
CREATE PROCEDURE sp_AgregarPaciente(IN DPI varchar(20), IN apellidos varchar(100), IN nombres varchar(100), 
					IN fechaNacimiento date, IN direccion varchar(150), IN ocupacion varchar(50), IN sexo varchar(15))
BEGIN
	INSERT INTO Pacientes (DPI, apellidos, nombres, fechaNacimiento, direccion, ocupacion, sexo)
		VALUES(DPI, apellidos, nombres, fechaNacimiento, direccion, ocupacion, sexo);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ActualizarPaciente(IN codigo int, IN DPI varchar(20), IN apellidos varchar(100), IN nombres varchar(100), 
					IN fechaNacimiento date, IN direccion varchar(150), IN ocupacion varchar(50), IN sexo varchar(15))
BEGIN
	UPDATE Pacientes SET DPI=DPI, apellidos=apellidos, nombres=nombres, fechaNacimiento=fechaNacimiento, direccion=direccion, ocupacion=ocupacion,
						sexo=sexo WHERE codigoPaciente=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarPaciente(IN codigo int)
BEGIN
	DELETE FROM Pacientes WHERE codigoPaciente=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ListarPacientes()
BEGIN
	SELECT
		Pacientes.codigoPaciente,
        Pacientes.DPI,
        Pacientes.apellidos,
        Pacientes.nombres,
        Pacientes.fechaNacimiento,
        Pacientes.edad,
        Pacientes.direccion,
        Pacientes.ocupacion,
        Pacientes.sexo
        FROM Pacientes;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarPaciente(IN codigo int)
BEGIN
SELECT
		Pacientes.codigoPaciente,
        Pacientes.DPI,
        Pacientes.apellidos,
        Pacientes.nombres,
        Pacientes.fechaNacimiento,
        Pacientes.edad,
        Pacientes.direccion,
        Pacientes.ocupacion,
        Pacientes.sexo
        FROM Pacientes WHERE codigoPaciente=codigo;
END$$
Delimiter ;

-- Procedimientos tabla TelefonosMedico
Delimiter $$
CREATE PROCEDURE sp_AgregarTelefonoMedico(IN telefonoPersonal varchar(15), IN telefonoTrabajo varchar(15), IN codigoMedico int)
BEGIN
	INSERT INTO TelefonosMedico(telefonoPersonal, telefonoTrabajo, codigoMedico) VALUES (telefonoPersonal, telefonoTrabajo, codigoMedico);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ActualizarTelefonoMedico(IN codigo int, IN telefonoPersonal varchar(15), IN telefonoTrabajo varchar(15))
BEGIN
	UPDATE TelefonosMedico SET telefonoPersonal=telefonoPersonal, telefonoTrabajo=telefonoTrabajo WHERE codigoTelefonoMedico=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarTelefonoMedico(IN codigo int)
BEGIN
	DELETE FROM TelefonosMedico WHERE codigoTelefonoMedico=codigo;
END$$
Delimiter;

Delimiter $$
CREATE PROCEDURE sp_ListarTelefonosMedico()
BEGIN
	SELECT
		TelefonosMedico.codigoTelefonoMedico,
        TelefonosMedico.telefonoPersonal,
        TelefonosMedico.telefonoTrabajo,
        TelefonosMedico.codigoMedico
        FROM TelefonosMedico;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarTelefonoMedico(IN codigo int)
BEGIN
	SELECT
		TelefonosMedico.codigoTelefonoMedico,
        TelefonosMedico.telefonoPersonal,
        TelefonosMedico.telefonoTrabajo,
        TelefonosMedico.codigoMedico
        FROM TelefonosMedico WHERE codigoTelefonoMedico=codigo;
END$$
Delimiter ;

-- Procedimientos tabla ContactoUrgencia
Delimiter $$
CREATE PROCEDURE sp_AgregarContactoUrgencia(IN nombres varchar(100), IN apellidos varchar(100), 
					IN numeroContacto varchar(10), IN codigoPaciente int)
BEGIN
	INSERT INTO ContactoUrgencia (nombres, apellidos, numeroContacto, codigoPaciente)
		VALUES(nombres, apellidos, numeroContacto, codigoPaciente);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ActualizarContactoUrgencia(IN codigo int, IN nombres varchar(100), IN apellidos varchar(100), 
					IN numeroContacto varchar(10))
BEGIN
	UPDATE ContactoUrgencia SET nombres=nombres, apellidos=apellidos, numeroContacto=numeroContacto
						WHERE codigoContactoUrgencia=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarContactoUrgencia(IN codigo int)
BEGIN
	DELETE FROM ContactoUrgencia WHERE codigoContactoUrgencia=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ListarContactosUrgencia()
BEGIN
	SELECT
		ContactoUrgencia.codigoContactoUrgencia,
        ContactoUrgencia.nombres,
        ContactoUrgencia.apellidos,
        ContactoUrgencia.numeroContacto,
        ContactoUrgencia.codigoPaciente
        FROM ContactoUrgencia;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarContactoUrgencia(IN codigo int)
BEGIN
		SELECT
		ContactoUrgencia.codigoContactoUrgencia,
        ContactoUrgencia.nombres,
        ContactoUrgencia.apellidos,
        ContactoUrgencia.numeroContacto,
        ContactoUrgencia.codigoPaciente
        FROM ContactoUrgencia WHERE codigoContactoUrgencia=codigo;
END$$
Delimiter ;

-- Procedimientos tabla ResponsableTurno

Delimiter $$
CREATE PROCEDURE sp_AgregarResponsableTurno(IN nombreResponsable varchar(75), IN apellidosResponsable varchar(45), IN telefonoPersonal varchar(10), 
												IN codigoArea int, IN codigoCargo int)
BEGIN
	INSERT INTO ResponsableTurno (nombreResponsable, apellidosResponsable, telefonoPersonal, codigoArea, codigoCargo) 
								VALUES (nombreResponsable, apellidosResponsable, telefonoPersonal, codigoArea, codigoCargo);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ActualizarResponsableTurno(IN codigo int, IN nombreResponsable varchar(75), IN apellidosResponsable varchar(45), 
												IN telefonoPersonal varchar(10))
BEGIN
	UPDATE ResponsableTurno SET nombreResponsable=nombreResponsable, apellidosResponsable=apellidosResponsable, telefonoPersonal=telefonoPersonal
								 WHERE codigoResponsableTurno=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarResponsableTurno(IN codigo int)
BEGIN
	DELETE FROM ResponsableTurno WHERE codigoResponsableTurno=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ListarResponsablesTurno()
BEGIN
	SELECT
		ResponsableTurno.codigoResponsableTurno,
        ResponsableTurno.nombreResponsable,
        ResponsableTurno.apellidosResponsable,
        ResponsableTurno.telefonoPersonal,
        ResponsableTurno.codigoArea,
        ResponsableTurno.codigoCargo
        FROM ResponsableTurno;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarResponsableTurno(IN codigo int)
BEGIN
	SELECT
		ResponsableTurno.codigoResponsableTurno,
        ResponsableTurno.nombreResponsable,
        ResponsableTurno.apellidosResponsable,
        ResponsableTurno.telefonoPersonal,
        ResponsableTurno.codigoArea,
        ResponsableTurno.codigoCargo
        FROM ResponsableTurno WHERE codigoResponsableTurno=codigo;
END$$
Delimiter ;

-- Procedimientos tabla Medico_Especialidad
Delimiter $$
CREATE PROCEDURE sp_AgregarMedico_Especialidad(IN codigoMedico int, IN codigoEspecialidad int, IN codigoHorario int)
BEGIN
	INSERT INTO Medico_Especialidad(codigoMedico, codigoEspecialidad, codigoHorario) 
		VALUES (codigoMedico, codigoEspecialidad, codigoHorario);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarMedico_Especialidad(IN codigo int)
BEGIN
	DELETE FROM Medico_Especialidad WHERE codigoMedicoEspecialidad=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ListarMedicos_Especialidad()
BEGIN
	SELECT 
		Medico_Especialidad.codigoMedicoEspecialidad,
        Medico_Especialidad.codigoMedico,
        Medico_Especialidad.codigoEspecialidad,
        Medico_Especialidad.codigoHorario
        FROM Medico_Especialidad;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarMedico_Especialidad(IN codigo int)
BEGIN
	SELECT 
		Medico_Especialidad.codigoMedicoEspecialidad,
        Medico_Especialidad.codigoMedico,
        Medico_Especialidad.codigoEspecialidad,
        Medico_Especialidad.codigoHorario
        FROM Medico_Especialidad WHERE codigoMedicoEspecialidad=codigo;
END$$
Delimiter ;

-- Procedimientos tabla Turno

Delimiter $$
CREATE PROCEDURE sp_AgregarTurno(IN fechaTurno date, IN fechaCita date, IN valorCita decimal(10,2), IN codigoMedicoEspecialidad int,
									IN codigoTurnoResponsable int, IN codigoPaciente int)
BEGIN
	INSERT INTO Turno (fechaTurno, fechaCita, valorCita, codigoMedicoEspecialidad, codigoTurnoResponsable, codigoPaciente)
		VALUES (fechaTurno, fechaCita, valorCita, codigoMedicoEspecialidad, codigoTurnoResponsable, codigoPaciente);
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ActualizarTurno(IN codigo int, IN fechaTurno date, IN fechaCita date, IN valorCita decimal(10,2))
BEGIN                                    
	UPDATE Turno SET fechaTurno=fechaTurno, fechaCita=fechaCita, valorCita=valorCita WHERE codigoTurno=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_EliminarTurno(IN codigo int)
BEGIN
	DELETE FROM Turno WHERE codigoTurno=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ListarTurnos()
BEGIN
	SELECT
		Turno.codigoTurno,
        Turno.fechaTurno,
        Turno.fechaCita,
        Turno.valorCita,
        Turno.codigoMedicoEspecialidad,
        Turno.codigoTurnoResponsable,
        Turno.codigoPaciente
        FROM Turno;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_BuscarTurno(IN codigo int)
BEGIN
	SELECT
		Turno.codigoTurno,
        Turno.fechaTurno,
        Turno.fechaCita,
        Turno.valorCita,
        Turno.codigoMedicoEspecialidad,
        Turno.codigoTurnoResponsable,
        Turno.codigoPaciente
        FROM Turno WHERE codigoTurno=codigo;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ReportePacientes()
BEGIN
	SELECT 
    Pacientes.DPI,
    Pacientes.apellidos,
    Pacientes.nombres,
    Pacientes.edad,
    Pacientes.sexo,
    ContactoUrgencia.numeroContacto
    FROM Pacientes INNER JOIN ContactoUrgencia ON Pacientes.codigoPaciente=ContactoUrgencia.codigoPaciente;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_ReporteMedicos()
BEGIN
	SELECT 
    Medicos.codigoMedico,
    Medicos.licenciaMedica,
    Medicos.nombres,
    Medicos.apellidos,
    Medicos.horaEntrada,
    Medicos.horaSalida,
    Medicos.turnoMaximo,
    Medicos.sexo,
    TelefonosMedico.telefonoPersonal
    FROM Medicos INNER JOIN TelefonosMedico ON Medicos.codigoMedico=TelefonosMedico.codigoMedico;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_VerificarContactoUrgencia()
BEGIN
	SELECT 
    Pacientes.codigoPaciente,
    ContactoUrgencia.numeroContacto
    FROM Pacientes LEFT JOIN ContactoUrgencia ON Pacientes.codigoPaciente=ContactoUrgencia.codigoPaciente;
END$$
Delimiter ;

Delimiter $$
CREATE PROCEDURE sp_VerificarTelefonoMedico()
BEGIN
	SELECT 
    Medicos.codigoMedico,
    TelefonosMedico.telefonoPersonal
    FROM Medicos LEFT JOIN TelefonosMedico ON Medicos.codigoMedico=TelefonosMedico.codigoMedico;
END$$
Delimiter ;


Delimiter $$
CREATE PROCEDURE sp_ReporteFinal(IN codMedico int)
BEGIN
	SELECT 
	Medicos.codigoMedico,
    Medicos.licenciaMedica,
    Medicos.nombres,
    Medicos.apellidos,
    Medicos.horaEntrada,
    Medicos.horaSalida,
    Medicos.turnoMaximo,
    Medicos.sexo,
    Horarios.lunes,
    Horarios.martes,
    Horarios.miercoles,
    Horarios.jueves,
    Horarios.viernes,
    Especialidades.nombreEspecialidad,
    Pacientes.nombres as nombrePaciente,
    Pacientes.apellidos as apellidosPaciente,
    ContactoUrgencia.numeroContacto,
    ResponsableTurno.nombreResponsable,
    ResponsableTurno.apellidosResponsable,
    Cargos.nombreCargo,
    Areas.nombreArea
    FROM Medicos INNER JOIN Medico_Especialidad ON Medicos.codigoMedico = Medico_Especialidad.codigoMedico
    INNER JOIN Horarios ON Medico_Especialidad.codigoHorario = Horarios.codigoHorario 
    INNER JOIN Especialidades ON Medico_Especialidad.codigoEspecialidad = Especialidades.codigoEspecialidad
    INNER JOIN Turno ON Turno.codigoMedicoEspecialidad= Medico_Especialidad.codigoMedicoEspecialidad
    INNER JOIN Pacientes ON Pacientes.codigoPaciente=Turno.codigoPaciente 
    INNER JOIN ContactoUrgencia ON ContactoUrgencia.codigoPaciente=Pacientes.codigoPaciente
    INNER JOIN ResponsableTurno ON ResponsableTurno.codigoResponsableTurno=Turno.codigoTurnoResponsable
    INNER JOIN Areas ON Areas.codigoArea = ResponsableTurno.codigoArea
    INNER JOIN Cargos ON Cargos.codigoCargo = ResponsableTurno.codigoCargo WHERE Medicos.codigoMedico=codMedico;
    
END$$
Delimiter ;