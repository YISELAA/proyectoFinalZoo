console.log("JS NUEVO CARGADO");

let tickets = [];
let paginaActual = 1;
const size = 5;

document.addEventListener("DOMContentLoaded", function () {

    cargarTickets();

    document.getElementById("formTicket")
        .addEventListener("submit", function (e) {

            e.preventDefault();

            let id = document.getElementById("idTicket").value;

            let ticket = {
                id: id === "" ? null : parseInt(id),
                tipo: document.getElementById("tipoTicket").value,
                precio: parseFloat(document.getElementById("precio").value),
                estado: "Activo"
            };

            let metodo = id === "" ? "POST" : "PUT";

            const scrollPos = window.scrollY; // 🔥 evita salto

            fetch("/ProyectoFinalZoo/TicketServlet", {
                method: metodo,
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(ticket)
            })
            .then(async response => {
                const text = await response.text();

                let data;
                try {
                    data = JSON.parse(text);
                } catch (e) {
                    throw new Error(text);
                }

                if (!response.ok) {
                    throw new Error(data.error || "Ocurrió un error en la operación");
                }

                return data;
            })
            .then(data => {

                Swal.fire({
                    icon: "success",
                    title: id ? "Actualizado" : "Agregado",
                    text: data.mensaje,
                    confirmButtonColor: "#3f5b4b"
                });

                limpiarFormulario();
                cargarTickets();

                setTimeout(() => {
                    window.scrollTo(0, scrollPos);
                }, 0);
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

});

function cargarTickets() {

    fetch("/ProyectoFinalZoo/TicketServlet?accion=listar")
        .then(async response => {
            const text = await response.text();

            try {
                return JSON.parse(text);
            } catch (e) {
                throw new Error(text);
            }
        })
        .then(data => {

            if (!Array.isArray(data)) {
                console.error("Respuesta inválida:", data);
                tickets = [];
                return;
            }

            tickets = data;

            paginaActual = 1; // 🔥 evita páginas vacías
            renderTabla();
            renderPaginacion();
        })
        .catch(error => console.error("Error al cargar tickets:", error));
}

function renderTabla() {

    let tbody = document.querySelector("#tablaTickets tbody");
    tbody.innerHTML = "";

    let inicio = (paginaActual - 1) * size;
    let fin = inicio + size;

    let paginaDatos = tickets.slice(inicio, fin);

    if (paginaDatos.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" style="text-align:center;">
                    No hay tickets registrados
                </td>
            </tr>
        `;
        return;
    }

    paginaDatos.forEach(ticket => {

        const tipoSeguro = encodeURIComponent(ticket.tipo);

        tbody.innerHTML += `
            <tr>
                <td>${ticket.id}</td>
                <td>${ticket.tipo}</td>
                <td>$${ticket.precio}</td>
                <td>${ticket.estado}</td>

                <td class="acciones">

                    <button class="btnEditar"
                        onclick="editarTicket(${ticket.id}, '${tipoSeguro}', ${ticket.precio})">
                        <i class="ti ti-edit"></i>
                    </button>

                    <button class="${ticket.estado === 'Activo' ? 'btnEliminar' : 'btnHabilitar'}"
                        onclick="${ticket.estado === 'Activo'
                            ? `deshabilitarTicket(${ticket.id})`
                            : `habilitarTicket(${ticket.id})`}">
                        <i class="ti ${ticket.estado === 'Activo' ? 'ti-ban' : 'ti-circle-check'}"></i>
                    </button>

                </td>
            </tr>
        `;
    });
}

function renderPaginacion() {

    const pagContenedor = document.getElementById("paginacion");
    if (!pagContenedor) return;

    let totalPaginas = Math.ceil(tickets.length / size);

    pagContenedor.innerHTML = `
        <button onclick="anterior()">
            <i class="ti ti-chevron-left"></i>
        </button>

        Página ${paginaActual} de ${totalPaginas}

        <button onclick="siguiente()">
            <i class="ti ti-chevron-right"></i>
        </button>
    `;
}

function siguiente() {

    let totalPaginas = Math.ceil(tickets.length / size);

    if (paginaActual < totalPaginas) {
        paginaActual++;
        renderTabla();
        renderPaginacion();
    }
}

function anterior() {

    if (paginaActual > 1) {
        paginaActual--;
        renderTabla();
        renderPaginacion();
    }
}

function editarTicket(id, tipo, precio) {

    document.getElementById("idTicket").value = id;
    document.getElementById("tipoTicket").value = decodeURIComponent(tipo);
    document.getElementById("precio").value = precio;

    document.querySelector(".guardar").textContent = "Actualizar";
}

function deshabilitarTicket(id) {

    Swal.fire({
        title: "¿Deshabilitar ticket?",
        text: "Ya no aparecerá en nuevas visitas",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#b05d4d",
        cancelButtonColor: "#3f5b4b",
        confirmButtonText: "Sí, deshabilitar",
        cancelButtonText: "Cancelar"
    }).then((result) => {

        if (!result.isConfirmed) return;

        fetch(`/ProyectoFinalZoo/TicketServlet?id=${id}`, {
            method: "DELETE"
        })
        .then(async response => {
            const text = await response.text();

            try {
                return JSON.parse(text);
            } catch (e) {
                throw new Error(text);
            }
        })
        .then(data => {

            Swal.fire({
                icon: "success",
                title: "Deshabilitado",
                text: data.mensaje,
                confirmButtonColor: "#3f5b4b"
            });

            cargarTickets();
        })
        .catch(error => {

            Swal.fire({
                icon: "error",
                title: "Error",
                text: error.message,
                confirmButtonColor: "#b05d4d"
            });
        });
    });
}

function habilitarTicket(id) {

    Swal.fire({
        title: "¿Habilitar ticket?",
        text: "Volverá a aparecer en nuevas visitas",
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#3f5b4b",
        cancelButtonColor: "#b05d4d",
        confirmButtonText: "Sí, habilitar",
        cancelButtonText: "Cancelar"
    }).then((result) => {

        if (!result.isConfirmed) return;

        fetch(`/ProyectoFinalZoo/TicketServlet?id=${id}`, {
            method: "PUT",
            headers: { "X-Accion": "habilitar" }
        })
        .then(async response => {
            const text = await response.text();

            try {
                return JSON.parse(text);
            } catch (e) {
                throw new Error(text);
            }
        })
        .then(data => {

            Swal.fire({
                icon: "success",
                title: "Habilitado",
                text: data.mensaje,
                confirmButtonColor: "#3f5b4b"
            });

            cargarTickets();
        })
        .catch(error => {

            Swal.fire({
                icon: "error",
                title: "Error",
                text: error.message,
                confirmButtonColor: "#b05d4d"
            });
        });
    });
}

function limpiarFormulario() {

    document.getElementById("formTicket").reset();
    document.getElementById("idTicket").value = "";

    document.querySelector(".guardar").textContent = "Guardar Ticket";
}