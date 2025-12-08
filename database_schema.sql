-- =====================================================
-- BASE DE DATOS COMPLETA PARA EL SISTEMA
-- Proyecto: comoproyect
-- Archivos: Bar.java, Log.java, Reg.java, Tod.java
-- =====================================================

-- Crear la base de datos
DROP DATABASE IF EXISTS tienda_db;
CREATE DATABASE tienda_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE tienda_db;

-- =====================================================
-- TABLA: usuarios (para login y registro)
-- Usada en: Log.java, Reg.java
-- =====================================================
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_usuario (usuario)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: empleados (gestión de empleados en Tod.java)
-- Campos: ID, Nombre, Apellido, Email, Cargo, Teléfono, Estado
-- =====================================================
CREATE TABLE empleados (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    cargo VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    estado ENUM('Activo', 'Inactivo') DEFAULT 'Activo',
    fecha_contratacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_estado (estado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: categorias (categorías de productos)
-- Campos: ID, Nombre, Descripción, Producto
-- =====================================================
CREATE TABLE categorias (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    producto VARCHAR(255),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: productos (inventario de productos)
-- Campos: ID, Nombre, Precio, Stock, ID_Categoría, Descripción
-- =====================================================
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    id_categoria INT,
    descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (id_categoria) REFERENCES categorias(id) ON DELETE SET NULL,
    INDEX idx_nombre (nombre),
    INDEX idx_categoria (id_categoria),
    INDEX idx_stock (stock)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: clientes (gestión de clientes)
-- Campos: ID, Nombre, Apellido, Teléfono, Email, Dirección, Ciudad
-- =====================================================
CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    email VARCHAR(150) NOT NULL,
    direccion VARCHAR(255),
    ciudad VARCHAR(100),
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_nombre (nombre, apellido),
    INDEX idx_email (email),
    INDEX idx_ciudad (ciudad)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: ventas (registro de ventas)
-- Campos: ID, ID_Cliente, ID_Empleado, Total, Fecha
-- =====================================================
CREATE TABLE ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT,
    id_empleado INT,
    total DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    fecha_venta TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('Completada', 'Pendiente', 'Cancelada') DEFAULT 'Completada',
    FOREIGN KEY (id_cliente) REFERENCES clientes(id) ON DELETE SET NULL,
    FOREIGN KEY (id_empleado) REFERENCES empleados(id) ON DELETE SET NULL,
    INDEX idx_fecha (fecha_venta),
    INDEX idx_cliente (id_cliente),
    INDEX idx_empleado (id_empleado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- TABLA: detalle_ventas (productos vendidos por venta)
-- =====================================================
CREATE TABLE detalle_ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    id_producto INT,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES ventas(id) ON DELETE CASCADE,
    FOREIGN KEY (id_producto) REFERENCES productos(id) ON DELETE SET NULL,
    INDEX idx_venta (id_venta),
    INDEX idx_producto (id_producto)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- INSERTAR DATOS DE EJEMPLO
-- =====================================================

-- Usuarios para login (contraseñas encriptadas con SHA-256)
INSERT INTO usuarios (usuario, password, nombre) VALUES
('admin', 'jGl25bVBBBW96Qi9Te4V37Fnqchz/Eu4qB9vKrRIqRg=', 'Administrador'),
('usuario1', 'BPiZbadjt6lpsQKO4wB1aerzpjVIbdqyEdUSyFud+Ps=', 'Usuario Demo');

-- Categorías
INSERT INTO categorias (nombre, descripcion, producto) VALUES
('Electrónica', 'Dispositivos y componentes electrónicos', 'Laptops, Tablets'),
('Accesorios', 'Accesorios para computadoras y periféricos', 'Mouse, Teclados'),
('Monitores', 'Pantallas y monitores de diferentes tamaños', 'Monitores LED, LCD'),
('Audio', 'Equipos de audio y sonido', 'Auriculares, Bocinas'),
('Almacenamiento', 'Dispositivos de almacenamiento de datos', 'SSD, HDD, USB');

-- Productos
INSERT INTO productos (nombre, precio, stock, id_categoria, descripcion) VALUES
('Laptop Dell XPS 15', 1299.99, 15, 1, 'Laptop de alto rendimiento con pantalla 4K'),
('Mouse Logitech MX Master', 99.99, 50, 2, 'Mouse ergonómico inalámbrico'),
('Teclado Mecánico Corsair', 149.99, 30, 2, 'Teclado mecánico RGB retroiluminado'),
('Monitor Samsung 27"', 349.99, 8, 3, 'Monitor curvo Full HD 144Hz'),
('Auriculares Sony WH-1000XM4', 279.99, 12, 4, 'Auriculares con cancelación de ruido'),
('SSD Samsung 1TB', 89.99, 100, 5, 'Disco sólido NVMe de alta velocidad');

-- Empleados
INSERT INTO empleados (nombre, apellido, email, cargo, telefono, estado) VALUES
('John', 'Doe', 'john@example.com', 'Gerente', '555-0001', 'Activo'),
('Jane', 'Smith', 'jane@example.com', 'Vendedor', '555-0002', 'Activo'),
('Carlos', 'López', 'carlos@example.com', 'Cajero', '555-0003', 'Activo'),
('María', 'García', 'maria@example.com', 'Vendedor', '555-0004', 'Inactivo'),
('Pedro', 'Martínez', 'pedro@example.com', 'Supervisor', '555-0005', 'Activo');

-- Clientes
INSERT INTO clientes (nombre, apellido, telefono, email, direccion, ciudad) VALUES
('Ana', 'Martínez', '555-0101', 'ana.martinez@email.com', 'Calle Principal 123', 'México'),
('Carlos', 'Rodríguez', '555-0102', 'carlos.r@email.com', 'Av. Central 456', 'Guadalajara'),
('María', 'López', '555-0103', 'maria.lopez@email.com', 'Boulevard Norte 789', 'Monterrey'),
('Juan', 'Pérez', '555-0104', 'juan.perez@email.com', 'Calle Sur 321', 'Puebla'),
('Laura', 'González', '555-0105', 'laura.g@email.com', 'Av. Este 654', 'Tijuana');

-- Ventas de ejemplo
INSERT INTO ventas (id_cliente, id_empleado, total, estado) VALUES
(1, 1, 1399.98, 'Completada'),
(2, 2, 249.98, 'Completada'),
(3, 3, 89.99, 'Completada');

-- Detalle de ventas
INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal) VALUES
(1, 1, 1, 1299.99, 1299.99),
(1, 2, 1, 99.99, 99.99),
(2, 3, 1, 149.99, 149.99),
(2, 2, 1, 99.99, 99.99),
(3, 6, 1, 89.99, 89.99);

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista de productos con nombre de categoría
CREATE VIEW vista_productos AS
SELECT 
    p.id,
    p.nombre,
    p.precio,
    p.stock,
    c.nombre AS categoria,
    p.descripcion
FROM productos p
LEFT JOIN categorias c ON p.id_categoria = c.id;

-- Vista de ventas completas
CREATE VIEW vista_ventas_completas AS
SELECT 
    v.id,
    v.fecha_venta,
    CONCAT(c.nombre, ' ', c.apellido) AS cliente,
    CONCAT(e.nombre, ' ', e.apellido) AS empleado,
    v.total,
    v.estado
FROM ventas v
LEFT JOIN clientes c ON v.id_cliente = c.id
LEFT JOIN empleados e ON v.id_empleado = e.id;

-- =====================================================
-- PROCEDIMIENTOS ALMACENADOS
-- =====================================================

DELIMITER //

-- Procedimiento para registrar una venta
CREATE PROCEDURE registrar_venta(
    IN p_id_cliente INT,
    IN p_id_empleado INT,
    IN p_total DECIMAL(10,2)
)
BEGIN
    INSERT INTO ventas (id_cliente, id_empleado, total)
    VALUES (p_id_cliente, p_id_empleado, p_total);
    
    SELECT LAST_INSERT_ID() AS id_venta;
END //

-- Procedimiento para agregar detalle de venta
CREATE PROCEDURE agregar_detalle_venta(
    IN p_id_venta INT,
    IN p_id_producto INT,
    IN p_cantidad INT,
    IN p_precio_unitario DECIMAL(10,2)
)
BEGIN
    DECLARE v_subtotal DECIMAL(10,2);
    SET v_subtotal = p_cantidad * p_precio_unitario;
    
    INSERT INTO detalle_ventas (id_venta, id_producto, cantidad, precio_unitario, subtotal)
    VALUES (p_id_venta, p_id_producto, p_cantidad, p_precio_unitario, v_subtotal);
    
    -- Actualizar stock del producto
    UPDATE productos 
    SET stock = stock - p_cantidad 
    WHERE id = p_id_producto;
END //

DELIMITER ;

-- =====================================================
-- INFORMACIÓN DE LA BASE DE DATOS
-- =====================================================

SELECT 'Base de datos creada exitosamente' AS mensaje;
SELECT 'Nombre de BD: tienda_db' AS info;
SELECT COUNT(*) AS total_tablas FROM information_schema.tables WHERE table_schema = 'tienda_db';
