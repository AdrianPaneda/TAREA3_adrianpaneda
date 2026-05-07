USE bdcirco_AdrianPaneda;
-- ========================================
-- DATOS DE PRUEBA - GESTIÓN CIRCO
-- Autor: Adrián Pañeda Hamadi
-- ========================================

-- ========================================
-- 1. CREDENCIALES
-- ========================================

-- Coordinaciones
INSERT INTO credenciales (id, nombre_usuario, password, perfil) VALUES 
(2, 'mgarcia', 'coord123', 'coordinacion'),
(3, 'lrodriguez', 'coord456', 'coordinacion');

-- Artistas
INSERT INTO credenciales (id, nombre_usuario, password, perfil) VALUES 
(4, 'jmartinez', 'artista123', 'artista'),
(5, 'alopez', 'artista456', 'artista'),
(6, 'pfernandez', 'artista789', 'artista'),
(7, 'cdiaz', 'artista012', 'artista'),
(8, 'rsanchez', 'artista345', 'artista');

-- ========================================
-- 2. PERSONAS (base para herencia)
-- ========================================
-- Coordinaciones
INSERT INTO persona (id, email, nombre, nacionalidad, id_credenciales) VALUES 
(2, 'mgarcia@circo.com', 'María García López', 'España', 2),
(3, 'lrodriguez@circo.com', 'Luis Rodríguez Pérez', 'Argentina', 3);

-- Artistas
INSERT INTO persona (id, email, nombre, nacionalidad, id_credenciales) VALUES 
(4, 'jmartinez@circo.com', 'Juan Martínez Ruiz', 'España', 4),
(5, 'alopez@circo.com', 'Ana López Fernández', 'México', 5),
(6, 'pfernandez@circo.com', 'Pedro Fernández García', 'Colombia', 6),
(7, 'cdiaz@circo.com', 'Carmen Díaz Sánchez', 'España', 7),
(8, 'rsanchez@circo.com', 'Roberto Sánchez Martín', 'Italia', 8);

-- ========================================
-- 3. COORDINACIONES
-- ========================================
-- María García (senior desde 2020)
INSERT INTO coordinacion (id_persona, senior, fecha_senior) VALUES 
(2, true, '2020-01-15');

-- Luis Rodríguez (no senior)
INSERT INTO coordinacion (id_persona, senior, fecha_senior) VALUES 
(3, false, null);

-- ========================================
-- 4. ARTISTAS
-- ========================================
-- Juan Martínez (El Volador - acrobacia)
INSERT INTO artista (id_persona, apodo) VALUES 
(4, 'El Volador');

-- Ana López (La Maga - magia)
INSERT INTO artista (id_persona, apodo) VALUES 
(5, 'La Maga');

-- Pedro Fernández (sin apodo - equilibrismo)
INSERT INTO artista (id_persona, apodo) VALUES 
(6, null);

-- Carmen Díaz (La Risueña - humor)
INSERT INTO artista (id_persona, apodo) VALUES 
(7, 'La Risueña');

-- Roberto Sánchez (sin apodo - malabarismo)
INSERT INTO artista (id_persona, apodo) VALUES 
(8, null);

-- ========================================
-- 5. ESPECIALIDADES DE ARTISTAS
-- ========================================
-- Juan (acrobacia + equilibrismo)
INSERT INTO artista_especialidades (id_artista, especialidad) VALUES 
(4, 'ACROBACIA'),
(4, 'EQUILIBRISMO');

-- Ana (magia)
INSERT INTO artista_especialidades (id_artista, especialidad) VALUES 
(5, 'MAGIA');

-- Pedro (equilibrismo + malabarismo)
INSERT INTO artista_especialidades (id_artista, especialidad) VALUES 
(6, 'EQUILIBRISMO'),
(6, 'MALABARISMO');

-- Carmen (humor + magia)
INSERT INTO artista_especialidades (id_artista, especialidad) VALUES 
(7, 'HUMOR'),
(7, 'MAGIA');

-- Roberto (malabarismo + acrobacia)
INSERT INTO artista_especialidades (id_artista, especialidad) VALUES 
(8, 'MALABARISMO'),
(8, 'ACROBACIA');

-- ========================================
-- 6. ESPECTÁCULOS
-- ========================================
-- Espectáculo pasado (ya terminó)
INSERT INTO espectaculo (id, nombre, fecha_inicio, fecha_fin, id_coordinacion) VALUES 
(1, 'Maravillas del Circo', '2024-06-01', '2024-12-31', 2);

-- Espectáculo vigente (en curso)
INSERT INTO espectaculo (id, nombre, fecha_inicio, fecha_fin, id_coordinacion) VALUES 
(2, 'Sueños Bajo la Carpa', '2025-01-15', '2025-08-15', 2);

-- Espectáculo futuro (aún no comienza)
INSERT INTO espectaculo (id, nombre, fecha_inicio, fecha_fin, id_coordinacion) VALUES 
(3, 'Estrellas del Mañana', '2025-09-01', '2026-03-31', 3);

-- ========================================
-- 7. NÚMEROS ARTÍSTICOS
-- ========================================
-- Números del espectáculo 1 (Maravillas del Circo)
INSERT INTO numero (id, orden, nombre, duracion, id_espectaculo) VALUES 
(1, 1, 'Acrobacias Aéreas', 12.5, 1),
(2, 2, 'Ilusiones Fantásticas', 15.0, 1),
(3, 3, 'Equilibrios Imposibles', 10.5, 1),
(4, 4, 'La Gran Comedia', 8.0, 1);

-- Números del espectáculo 2 (Sueños Bajo la Carpa)
INSERT INTO numero (id, orden, nombre, duracion, id_espectaculo) VALUES 
(5, 1, 'Malabares de Fuego', 9.5, 2),
(6, 2, 'Vuelo de los Trapecistas', 14.0, 2),
(7, 3, 'Magia y Misterio', 11.5, 2);

-- Números del espectáculo 3 (Estrellas del Mañana)
INSERT INTO numero (id, orden, nombre, duracion, id_espectaculo) VALUES 
(8, 1, 'Payasos en Acción', 7.5, 3),
(9, 2, 'Equilibrio Extremo', 13.0, 3),
(10, 3, 'El Gran Final', 16.5, 3);

-- ========================================
-- 8. ASIGNACIÓN ARTISTAS A NÚMEROS
-- ========================================
-- Espectáculo 1
INSERT INTO numero_artista (id_numero, id_artista) VALUES 
(1, 4), -- Juan en Acrobacias Aéreas
(2, 5), -- Ana en Ilusiones Fantásticas
(3, 6), -- Pedro en Equilibrios Imposibles
(4, 7); -- Carmen en La Gran Comedia

-- Espectáculo 2
INSERT INTO numero_artista (id_numero, id_artista) VALUES 
(5, 8), -- Roberto en Malabares de Fuego
(5, 6), -- Pedro también en Malabares
(6, 4), -- Juan en Vuelo de los Trapecistas
(7, 5), -- Ana en Magia y Misterio
(7, 7); -- Carmen también en Magia y Misterio

-- Espectáculo 3
INSERT INTO numero_artista (id_numero, id_artista) VALUES 
(8, 7), -- Carmen en Payasos en Acción
(9, 6), -- Pedro en Equilibrio Extremo
(9, 4), -- Juan también en Equilibrio Extremo
(10, 4), -- Juan en El Gran Final
(10, 5), -- Ana en El Gran Final
(10, 8); -- Roberto en El Gran Final