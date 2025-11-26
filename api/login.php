<?php
header("Content-Type: application/json");
require_once "conexion.php";

if ($_SERVER["REQUEST_METHOD"] === "POST") {

    $correo   = $_POST['correo']   ?? '';
    $password = $_POST['password'] ?? '';

    if ($correo === '' || $password === '') {
        echo json_encode(["success" => false, "message" => "Faltan datos"]);
        exit;
    }

    $stmt = $conn->prepare("SELECT id, contrasena_hash, nombre, apellido, rol_id FROM usuarios WHERE correo = ?");
    $stmt->bind_param("s", $correo);
    $stmt->execute();
    $stmt->store_result();

    if ($stmt->num_rows === 0) {
        echo json_encode(["success" => false, "message" => "Usuario no encontrado"]);
        exit;
    }

    $stmt->bind_result($id, $hash, $nombre, $apellido, $rol_id);
    $stmt->fetch();

    if (!password_verify($password, $hash)) {
        echo json_encode(["success" => false, "message" => "Contraseña incorrecta"]);
        exit;
    }

    echo json_encode([
        "success" => true,
        "message" => "Login exitoso",
        "usuario" => [
            "id"       => $id,
            "nombre"   => $nombre,
            "apellido" => $apellido,
            "correo"   => $correo,
            "rol_id"   => $rol_id
        ]
    ]);

    $stmt->close();
    $conn->close();

} else {
    echo json_encode(["success" => false, "message" => "Método no permitido"]);
}
?>
