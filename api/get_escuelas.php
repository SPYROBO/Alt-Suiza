<?php

require_once ('conexion.php');

// Indicar que el resultado será tipo JSON".
header('Content-Type: application/json');

// Consulta SQL para traer todos los registros de la tabla -escuelas-
$sql = "SELECT * FROM escuelas";
$result = $conn->query($sql);

$escuelas = array(); // creamos un array vacío para guardar los resultados.

// guardar cada fila del resultado en el array
if ($result->num_rows > 0) {
    while($row = $result->fetch_assoc()) {
        $escuelas[] = $row; // Añade la fila al array.
    }
}

// Convertimos todo lo que hay en el array a formato JSON y lo mostramos.
echo json_encode($escuelas);

// Cerramos la conexion a la base de datos.
$conn->close();
?>
