<?php 
// Declaramos las credenciales para la conexion a la bd.
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "alt_ruido";

// Preparar la conexion sql con las credenciales definidas arriba.
$conn = new mysqli($servername, $username, $password, $dbname);

// Verificar si la conexion no fue exitosa.
if ($conn->connect_error) {
    http_response_code(500); // Código de error de servidor
    echo json_encode(['error' => 'No se pudo conectar a la BD']);
    die(); // Detiene la ejecución
}

?>