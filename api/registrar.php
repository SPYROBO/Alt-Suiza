<?php
header('Content-Type: application/json');

$response = [
    "success" => false,
    "message" => ""
];

require_once("conexion.php");

// Validar datos
if (
    !isset($_POST['correo']) ||
    !isset($_POST['password']) ||
    !isset($_POST['nombre']) ||
    !isset($_POST['apellido']) ||
    !isset($_POST['rol_id'])
) {
    $response["message"] = "Faltan datos.";
    echo json_encode($response);
    exit;
}

$correo   = $_POST['correo'];
$pass     = $_POST['password'];
$nombre   = $_POST['nombre'];
$apellido = $_POST['apellido'];
$rol_id   = intval($_POST['rol_id']);

// Verificar correo existente
$sql = "SELECT id FROM usuarios WHERE correo = '$correo' LIMIT 1";
$result = $conn->query($sql);

if ($result && $result->num_rows > 0) {
    $response["message"] = "El correo ya existe.";
    echo json_encode($response);
    exit;
}

// Hash
$hash = password_hash($pass, PASSWORD_DEFAULT);

// Insert
$sql_insert = "
INSERT INTO usuarios
(correo, contrasena_hash, nombre, apellido, rol_id)
VALUES ('$correo', '$hash', '$nombre', '$apellido', $rol_id)
";

if ($conn->query($sql_insert) === TRUE) {
    $response["success"] = true;
    $response["message"] = "Usuario registrado.";
    $response["id"] = $conn->insert_id;
} else {
    $response["message"] = "Error en BD.";
}

echo json_encode($response);
$conn->close();
