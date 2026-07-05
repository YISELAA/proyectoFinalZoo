document.addEventListener("DOMContentLoaded", function () {

    const tbody = document.querySelector("#tablaAC tbody");

    function cargar() {

        fetch("/ProyectoFinalZoo/AnimalCuidadorConsultaServlet")
                .then(res => res.json())
                .then(data => {
                    console.log(data);

                    let html = "";

                    data.forEach(a => {
                        html += `
        <tr>
            <td>${a.id}</td>
            <td>${a.nombre_animal}</td>
            <td>${a.especie}</td>
            <td>${a.sexo}</td>
            <td>${a.nombre_empleado}</td>
            <td>${a.apellido}</td>
            <td>${a.numero_dui}</td>
        </tr>
    `;
                    });

                    tbody.innerHTML = html;

                    tbody.innerHTML = html;
                    initDataTable();
                    $('#tablaAC').DataTable().columns.adjust().draw();
                })
                .catch(error => {
                    console.error("Error al cargar datos:", error);
                });
    }

    cargar();
});


function initDataTable() {
    table = $('#tablaAC').DataTable({
        autoWidth: false,
        pageLength: 5,
        lengthMenu: [5, 10, 25, 50],
        pagingType: "simple_numbers",
        language: {
            lengthMenu: "Mostrar _MENU_ registros",
            info: "Mostrando _START_ a _END_ de _TOTAL_ registros",
            search: "Buscar",
            infoEmpty: "No hay registros",
            zeroRecords: "No se encontraron resultados",
            paginate: {
                next: "→",
                previous: "←"
            }
        }
    });
}