<?php
// Script de la conexión a la base de datos
require_once ('conexion.php'); 

header("Content-Type: application/json");

// Verifica que el parámetro 'esc_id' fue enviado en la URL
if (isset($_GET['esc_id'])) {
    $esc_id = intval($_GET['esc_id']); // Convierte a entero para seguridad

    // Prepara la consulta SQL
    $stmt = $conn->prepare("SELECT id, num_aula, esc_id FROM aulas WHERE esc_id = ? ORDER BY num_aula ASC");
    $stmt->bind_param("i", $esc_id);
    
    // Ejecuta la consulta
    $stmt->execute();
    $resultado = $stmt->get_result();

    $aulas = array();
    while ($fila = $resultado->fetch_assoc()) {
        $aulas[] = $fila;
    }

    // Devuelve el array de aulas en formato JSON
    echo json_encode($aulas);

    $stmt->close();
} else {
    // Si no se proporciona un ID, devuelve un mensaje de error en JSON
    echo json_encode(["error" => "ID de escuela no proporcionado"]);
}

$conn->close();
?>
