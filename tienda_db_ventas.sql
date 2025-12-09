-- ============================================================
-- Script SQL para Base de Datos: tienda_db
-- Tablas para Sistema de Ventas y Tickets
-- ============================================================

-- Tabla de Ventas (Encabezado del Ticket)
CREATE TABLE IF NOT EXISTS ventas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha_venta DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10, 2) NOT NULL,
    id_cliente INTEGER,
    nombre_tienda VARCHAR(255),
    ruta_imagen_tienda VARCHAR(500),
    estado VARCHAR(50) DEFAULT 'completada',
    FOREIGN KEY (id_cliente) REFERENCES clientes(id)
);

-- Tabla de Detalle de Ventas (Productos del Ticket)
CREATE TABLE IF NOT EXISTS detalle_ventas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    id_venta INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    nombre_producto VARCHAR(255) NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    cantidad INTEGER NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_venta) REFERENCES ventas(id),
    FOREIGN KEY (id_producto) REFERENCES productos(id)
);

-- Índices para mejorar el rendimiento de consultas
CREATE INDEX IF NOT EXISTS idx_ventas_fecha ON ventas(fecha_venta);
CREATE INDEX IF NOT EXISTS idx_ventas_cliente ON ventas(id_cliente);
CREATE INDEX IF NOT EXISTS idx_detalle_venta ON detalle_ventas(id_venta);
CREATE INDEX IF NOT EXISTS idx_detalle_producto ON detalle_ventas(id_producto);

-- ============================================================
-- CONSULTAS ÚTILES PARA REPORTES
-- ============================================================

-- Ver todas las ventas (comentado, descomentar para usar)
-- SELECT * FROM ventas ORDER BY fecha_venta DESC;

-- Ver detalles de una venta específica (cambiar el ID)
-- SELECT 
--     v.id as 'ID Venta',
--     v.fecha_venta as 'Fecha',
--     v.total as 'Total',
--     v.nombre_tienda as 'Tienda',
--     d.nombre_producto as 'Producto',
--     d.cantidad as 'Cantidad',
--     d.precio_unitario as 'Precio Unit.',
--     d.subtotal as 'Subtotal'
-- FROM ventas v
-- INNER JOIN detalle_ventas d ON v.id = d.id_venta
-- WHERE v.id = 1;

-- Ventas del día actual
-- SELECT 
--     COUNT(*) as total_ventas,
--     SUM(total) as total_ingresos
-- FROM ventas
-- WHERE DATE(fecha_venta) = DATE('now');

-- Productos más vendidos
-- SELECT 
--     nombre_producto,
--     SUM(cantidad) as total_vendido,
--     SUM(subtotal) as ingresos_totales
-- FROM detalle_ventas
-- GROUP BY nombre_producto
-- ORDER BY total_vendido DESC;

-- Ventas por mes
-- SELECT 
--     strftime('%Y-%m', fecha_venta) as mes,
--     COUNT(*) as total_ventas,
--     SUM(total) as ingresos
-- FROM ventas
-- GROUP BY mes
-- ORDER BY mes DESC;
