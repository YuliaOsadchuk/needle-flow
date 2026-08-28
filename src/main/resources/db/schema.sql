DROP TABLE IF EXISTS inventory;
DROP TABLE IF EXISTS design_thread;
DROP TABLE IF EXISTS thread;
DROP TABLE IF EXISTS manufacturer;
DROP TABLE IF EXISTS design;
DROP TABLE IF EXISTS designer;


CREATE TABLE manufacturer
(
    id   SERIAL PRIMARY KEY NOT NULL,
    name VARCHAR            NOT NULL
);

CREATE TABLE thread
(
    id              SERIAL PRIMARY KEY NOT NULL,
    code            varchar            NOT NULL,
    name            varchar,
    manufacturer_id INTEGER            NOT NULL REFERENCES manufacturer (id),
    UNIQUE (manufacturer_id, code)
);

CREATE TABLE inventory
(
    id              SERIAL PRIMARY KEY NOT NULL,
    thread_id       INTEGER            NOT NUll UNIQUE REFERENCES thread (id),
    skein_quantity  INTEGER            NOT NULL DEFAULT 0.0,
    bobbin_quantity DOUBLE PRECISION   NOT NULL DEFAULT 0.0
);

CREATE TABLE designer
(
    id   SERIAL PRIMARY KEY NOT NULL,
    name varchar            NOT NULL
);

CREATE TABLE design
(
    id          SERIAL PRIMARY KEY NOT NULL,
    name        varchar            NOT NULL,
    designer_id INTEGER            NOT NULL REFERENCES designer (id),
    status      varchar            NOT NULL,
    UNIQUE (name, designer_id)
);

CREATE TABLE design_thread
(
    id              SERIAL PRIMARY KEY NOT NULL,
    design_id       INTEGER            NOT NULL REFERENCES design (id),
    thread_id       INTEGER            NOT NULL REFERENCES thread (id),
    required_meters NUMERIC            NOT NULL DEFAULT 0.0,
    UNIQUE (design_id, thread_id)
);

