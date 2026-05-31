-- ================================================================
-- Archivo: supabase_setup.sql
-- SQL para crear las tablas en Supabase
-- Ejecutar en: Supabase Dashboard > SQL Editor
-- ================================================================

-- ==================== TABLA: usuarios ====================
CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Desactivar RLS para desarrollo (SOLO en desarrollo, no producción)
ALTER TABLE usuarios DISABLE ROW LEVEL SECURITY;

-- ==================== TABLA: universidades ====================
CREATE TABLE IF NOT EXISTS universidades (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    categoria VARCHAR(100),
    web VARCHAR(200),
    rector VARCHAR(150),
    email VARCHAR(150),
    acceso VARCHAR(100),
    telefono VARCHAR(50),
    ciudad VARCHAR(100),
    "numeroCarreras" INTEGER DEFAULT 0,
    "numSedes" INTEGER DEFAULT 1,
    pais VARCHAR(100),
    departamento VARCHAR(100),
    created_at TIMESTAMP DEFAULT NOW()
);

ALTER TABLE universidades DISABLE ROW LEVEL SECURITY;

-- ==================== TABLA: carreras ====================
CREATE TABLE IF NOT EXISTS carreras (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    "numCreditos" INTEGER DEFAULT 0,
    "numAsignaturas" INTEGER DEFAULT 0,
    "numSemestres" INTEGER DEFAULT 0,
    "nivelFormacion" VARCHAR(100),
    titulo VARCHAR(200),
    "valorSemestre" DECIMAL(12,2) DEFAULT 0,
    universidad_id INTEGER REFERENCES universidades(id) ON DELETE SET NULL,
    "esAcreditada" BOOLEAN DEFAULT FALSE,
    "areaDelConocimiento" VARCHAR(150),
    created_at TIMESTAMP DEFAULT NOW()
);

ALTER TABLE carreras DISABLE ROW LEVEL SECURITY;

-- ==================== DATOS DE PRUEBA ====================

-- Usuario de prueba (password: 123456)
INSERT INTO usuarios (username, password, nombre, email)
VALUES ('admin', '123456', 'Administrador', 'admin@test.com')
ON CONFLICT (email) DO NOTHING;

-- Universidad de prueba
INSERT INTO universidades (nombre, categoria, web, rector, email, acceso, telefono, ciudad, "numeroCarreras", "numSedes", pais, departamento)
VALUES 
('Universidad Nacional de Colombia', 'Pública', 'https://unal.edu.co', 'John Doe', 'info@unal.edu.co', 'SNIES', '3165000000', 'Bogotá', 95, 8, 'Colombia', 'Cundinamarca'),
('Universidad de los Andes', 'Privada', 'https://uniandes.edu.co', 'Jane Smith', 'info@uniandes.edu.co', 'Libre', '3394949', 'Bogotá', 68, 2, 'Colombia', 'Cundinamarca')
ON CONFLICT DO NOTHING;

-- Carrera de prueba
INSERT INTO carreras (nombre, "numCreditos", "numAsignaturas", "numSemestres", "nivelFormacion", titulo, "valorSemestre", universidad_id, "esAcreditada", "areaDelConocimiento")
VALUES 
('Ingeniería de Sistemas', 164, 45, 10, 'Pregrado', 'Ingeniero de Sistemas', 5000000, 1, TRUE, 'Ingeniería'),
('Medicina', 285, 80, 12, 'Pregrado', 'Médico y Cirujano', 12000000, 1, TRUE, 'Ciencias de la Salud')
ON CONFLICT DO NOTHING;
