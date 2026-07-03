

console.log("JS EMPLEADOS CARGADO");

let empleados = [];
let paginaActual = 1;
const size = 5;



document.addEventListener("DOMContentLoaded", function () {

    cargarEmpleados();

    const btnCancelar = document.querySelector(".cancelar");
    if (btnCancelar) {
        btnCancelar.addEventListener("click", function (e) {
            e.preventDefault();
            limpiarFormulario();
        });
    }
});


// LISTAR   

function cargarEmpleados() {
    fetch("EmpleadoServlet")
            .then(response => {
                if (!response.ok)
                    throw new Error("Error en la respuesta del servidor");
                return response.json();
            })
            .then(data => {
                empleados = data;
                mostrarEmpleados();
                renderPaginacion();
            })
            .catch(error => console.error("Error al buscar empleados:", error));
}

function mostrarEmpleados() {

    const inicio = (paginaActual - 1) * size;
    const fin = inicio + size;
    const empleadosPagina = empleados.slice(inicio, fin);

    if (empleadosPagina.length === 0) {
        document.getElementById("tbodyEmpleados").innerHTML = `
            <tr>
                <td colspan="6" style="text-align:center;">
                    No se encontraron empleados registrados
                </td>
            </tr>`;
        return;
    }

    const html = empleadosPagina.map(e => `
        <tr>
            <td>${e.id}</td>
            <td>${e.nombre}</td>
            <td>${e.apellido}</td>
            <td>${e.dui}</td>
            <td class="acciones">
                <button class="btnEditar"   onclick="editar(${e.id})">
                    <i class="ti ti-edit"></i>
                </button>
                <button class="btnEliminar" onclick="eliminarEmpleado(${e.id})">
                    <i class="ti ti-trash"></i>
                </button>
            </td>
        </tr>`
    ).join("");

    document.getElementById("tbodyEmpleados").innerHTML = html;
}


//  GUARDAR Y ACTUALIZAR

document.getElementById("formEmpleado").addEventListener("submit", function (event) {
    event.preventDefault();

    const id = document.getElementById("idEmpleado").value;

    const empleado = {
        nombre: document.getElementById("nombreEmpleado").value,
        apellido: document.getElementById("apellido").value,
        dui: document.getElementById("numeroDui").value,
    };

    if (id)
        empleado.id = parseInt(id);

    const metodo = id ? "PUT" : "POST";
    console.log("DUI:", document.getElementById("numeroDui").value);

    fetch("EmpleadoServlet", {
        method: metodo,
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify(empleado)
    })
            .then(async response => {
                const texto = await response.text();

                console.log("STATUS:", response.status);
                console.log("RESPUESTA:", texto);

                if (!response.ok) {
                    throw new Error(texto);
                }

                return JSON.parse(texto);
            })
            .then(data => {
                Swal.fire({
                    icon: "success",
                    title: id ? "Actualizado" : "Agregado",
                    text: data.mensaje,
                    confirmButtonColor: "#3f5b4b"
                });

                limpiarFormulario();

                if (!id) {
                    // Solo al crear un nuevo empleado ir a la primera página
                    paginaActual = 1;
                }

                cargarEmpleados();
            })
            .catch(error => {
                Swal.fire({
                    icon: "warning",
                    title: "No se puede guardar",
                    text: error.message,
                    confirmButtonColor: "#b05d4d"
                });
            });
});


function editar(id) {
    fetch(`EmpleadoServlet?id=${id}`)
            .then(response => {
                if (!response.ok)
                    throw new Error("No se pudo obtener el registro");
                return response.json();
            })
            .then(e => {
                document.getElementById("idEmpleado").value = e.id;
                document.getElementById("nombreEmpleado").value = e.nombre;
                document.getElementById("apellido").value = e.apellido;
                document.getElementById("numeroDui").value = e.dui;

                document.querySelector(".guardar").textContent = "Actualizar Empleado";
//                window.scrollTo({top: 0, behavior: "smooth"});
            })
            .catch(error => console.error("Error al cargar datos de edición:", error));
}




function eliminarEmpleado(id) {
    Swal.fire({
        title: "¿Eliminar empleado?",
        text: "Esta acción no se puede deshacer",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#b05d4d",
        cancelButtonColor: "#3f5b4b",
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar"
    }).then((result) => {

        if (!result.isConfirmed)
            return;

        fetch(`EmpleadoServlet?id=${id}`, {method: "DELETE"})
                .then(async response => {
                    const texto = await response.text();
                    console.log("STATUS:", response.status);
                    console.log("RESPUESTA:", texto);
                    if (!response.ok)
                        throw new Error(texto);
                    return JSON.parse(texto);
                })
                .then(data => {
                    Swal.fire({
                        icon: "success",
                        title: "Eliminado",
                        text: data.mensaje,
                        confirmButtonColor: "#3f5b4b"
                    });
                    paginaActual = 1;
                    cargarEmpleados();
                })
                .catch(error => {
                    Swal.fire({
                        icon: "warning",
                        title: "No se puede eliminar",
                        text: error.message,
                        confirmButtonColor: "#b05d4d"
                    });
                });
    });
}



function limpiarFormulario() {
    document.getElementById("formEmpleado").reset();
    document.getElementById("idEmpleado").value = "";
    document.querySelector(".guardar").textContent = "Guardar Empleado";
}


// PAGINACIÓN


function renderPaginacion() {
    const pagContenedor = document.getElementById("paginacion");
    if (!pagContenedor)
        return;

    const totalPaginas = Math.ceil(empleados.length / size);

    pagContenedor.innerHTML =
            "<button onclick='anterior()'><i class='ti ti-chevron-left'></i></button>" +
            " Página " + paginaActual + " de " + totalPaginas + " " +
            "<button onclick='siguiente()'><i class='ti ti-chevron-right'></i></button>";
}

function siguiente() {
    const totalPaginas = Math.ceil(empleados.length / size);
    if (paginaActual < totalPaginas) {
        paginaActual++;
        mostrarEmpleados();
        renderPaginacion();
    }
}

function anterior() {
    if (paginaActual > 1) {
        paginaActual--;
        mostrarEmpleados();
        renderPaginacion();
    }
}

function irPagina(numero) {
    paginaActual = numero;
    mostrarEmpleados();
    renderPaginacion();
}