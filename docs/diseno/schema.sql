-- Modelo relacional conceptual normalizado en 3FN.
-- No representa las tablas que crean actualmente las entidades JPA del backend.
CREATE TABLE municipio (
    id     SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE agricultor (
    id           SERIAL PRIMARY KEY,
    nombre       VARCHAR(100) NOT NULL,
    correo       VARCHAR(120) NOT NULL UNIQUE,
    municipio_id INT NOT NULL REFERENCES municipio(id)
);

CREATE TABLE producto (
    id        SERIAL PRIMARY KEY,
    nombre    VARCHAR(100) NOT NULL UNIQUE,
    categoria VARCHAR(50) NOT NULL
);

CREATE TABLE cosecha (
    id            SERIAL PRIMARY KEY,
    agricultor_id INT NOT NULL REFERENCES agricultor(id),
    producto_id   INT NOT NULL REFERENCES producto(id),
    cantidad_kg   NUMERIC(10,2) NOT NULL CHECK (cantidad_kg > 0),
    fecha_cosecha DATE NOT NULL,
    CONSTRAINT uq_cosecha_natural
        UNIQUE (agricultor_id, producto_id, fecha_cosecha)
);

CREATE TABLE oferta (
    id                  SERIAL PRIMARY KEY,
    cosecha_id          INT NOT NULL REFERENCES cosecha(id),
    precio_kg           NUMERIC(10,2) NOT NULL CHECK (precio_kg >= 0),
    cantidad_disponible NUMERIC(10,2) NOT NULL
        CHECK (cantidad_disponible >= 0),
    estado              VARCHAR(20) NOT NULL DEFAULT 'ACTIVA'
        CHECK (estado IN ('ACTIVA', 'VENDIDA', 'CANCELADA')),
    fecha_publicacion   DATE NOT NULL DEFAULT CURRENT_DATE
);

CREATE INDEX idx_agricultor_municipio ON agricultor(municipio_id);
CREATE INDEX idx_cosecha_agricultor ON cosecha(agricultor_id);
CREATE INDEX idx_cosecha_producto ON cosecha(producto_id);
CREATE INDEX idx_oferta_cosecha ON oferta(cosecha_id);
