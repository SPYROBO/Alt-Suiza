<?php
header('Content-Type: application/json; charset=utf-8');

$response = [
    "success" => false,
    "message" => "",
    "estado"  => ""  // "agregado" o "eliminado"
];

require_once "conexion.php";

// Validar conexión
if ($conn->connect_error) {
    $response["message"] = "Error de conexión: " . $conn->connect_error;
    echo json_encode($response);
    exit;
}

// Validar parámetros
if (!isset($_POST['usuario_id']) || !isset($_POST['escuela_id'])) {
    $response["message"] = "Faltan datos.";
    echo json_encode($response);
    exit;
}

$usuario_id = (int) $_POST['usuario_id'];
$escuela_id = (int) $_POST['escuela_id'];

if ($usuario_id <= 0 || $escuela_id <= 0) {
    $response["message"] = "IDs inválidos.";
    echo json_encode($response);
    exit;
}

mysqli_report(MYSQLI_REPORT_ERROR | MYSQLI_REPORT_STRICT);

try {

    // 1) ¿Existe el favorito actualmente?
    $sql_check = "SELECT id FROM favoritos WHERE usuario_id = ? AND escuela_id = ? LIMIT 1";
    $stmt = $conn->prepare($sql_check);
    $stmt->bind_param("ii", $usuario_id, $escuela_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        // 2) Ya existe → ELIMINARLO (toggle OFF)
        $sql_del = "DELETE FROM favoritos WHERE usuario_id = ? AND escuela_id = ?";
        $del = $conn->prepare($sql_del);
        $del->bind_param("ii", $usuario_id, $escuela_id);
        $del->execute();

        $response["success"] = true;
        $response["message"] = "Favorito eliminado.";
        $response["estado"]  = "eliminado";

    } else {
        // 3) No existe → AGREGARLO (toggle ON)
        $sql_insert = "INSERT INTO favoritos (usuario_id, escuela_id) VALUES (?, ?)";
        $stmt_ins = $conn->prepare($sql_insert);
        $stmt_ins->bind_param("ii", $usuario_id, $escuela_id);
        $stmt_ins->execute();

        $response["success"] = true;
        $response["message"] = "Favorito agregado.";
        $response["estado"]  = "agregado";
    }

} catch (Exception $e) {
    $response["success"] = false;
    $response["message"] = "Error SQL: " . $e->getMessage();
}

echo json_encode($response);
$conn->close();
