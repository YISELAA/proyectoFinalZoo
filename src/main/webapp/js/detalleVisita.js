

console.log("JS NUEVO CARGADO");


let detalleVisita = [];
let gruposVisita = [];
let paginaActual = 1;
const size = 5;


let tickets = [];
let idsOriginales = [];
const precios = {};
const badgeClass = {};
const colores = [
    "badge-verde",
    "badge-azul",
    "badge-amarillo",
    "badge-morado",
    "badge-rosa"
];


document.addEventListener("DOMContentLoaded", function () {

    cargarTabla();

    let select = document.getElementById("idTicket");

    Array.from(select.options).forEach(function (option) {
        if (option.value) {
            let tipo = option.value;
            let precio = parseFloat(option.dataset.precio);
            precios[tipo] = precio;
            badgeClass[tipo] = colores[Object.keys(badgeClass).length % colores.length];
        }
    });

    document.getElementById("btnAgregar")
            .addEventListener("click", agregarTicket);

    document.getElementById("formDetalleVisita")
            .addEventListener("submit", guardarRegistro);

    document.getElementById("btnCancelar")
            .addEventListener("click", limpiarFormulario);

    renderTickets();
});

function calcTotal() {
    return tickets.reduce((a, t) => a + t.subtotal, 0);
}

function renderTickets() {

    let lista = document.getElementById("ticketLista");

    lista.innerHTML = tickets.map((t, i) => `
        <div class="ticket-item">
            <div class="ticket-item-left">
                <span class="badge ${badgeClass[t.tipo]}">${t.tipo}</span>
                <span>× ${t.cantidad}</span>
                <span>$${t.subtotal.toFixed(2)}</span>
            </div>
            <button type="button" class="btn-quitar" onclick="quitarTicket(${i})">×</button>
        </div>
    `).join("");

    let total = calcTotal();
    document.getElementById("totalDisplay").value =
            tickets.length > 0 ? "$" + total.toFixed(2) : "";
}


function agregarTicket() {

    let select = document.getElementById("idTicket");
    let cantidad = parseInt(document.getElementById("cantidad").value);
    let error = document.getElementById("errorTicket");

    if (!select.value || !cantidad || cantidad < 1) {
        error.style.display = "block";
        return;
    }

    error.style.display = "none";

    let tipo = select.value;
    let precio = parseFloat(select.options[select.selectedIndex].dataset.precio);
    let subtotal = precio * cantidad;
    let index = tickets.findIndex(t => t.tipo === tipo);

    if (index >= 0) {
        tickets[index].cantidad += cantidad;
        tickets[index].subtotal += subtotal;
    } else {
        tickets.push({tipo, cantidad, precio, subtotal});
    }

    select.value = "";
    document.getElementById("cantidad").value = "";
    renderTickets();
}


function quitarTicket(i) {
    tickets.splice(i, 1);
    renderTickets();
}


// GUARDAR / ACTUALIZAR

function guardarRegistro(e) {

    e.preventDefault();

    if (tickets.length === 0) {
        Swal.fire({
            icon: "warning",
            title: "Sin tickets",
            text: "Debe agregar al menos un ticket",
            confirmButtonColor: "#3f5b4b"
        });
        return;
    }

    let nombre = document.getElementById("nombreVisitante").value;
    let telefono = document.getElementById("telefono").value;
    let idExistente = document.getElementById("idDetalleVisita").value;

    let promesas = tickets.map(t => {

        let detalle = {
            nombreVisitante: nombre,
            telefono: telefono,
            cantidad: t.cantidad,
            subtotal: t.subtotal,
            ticket: {tipo: t.tipo}
        };

        let metodoTicket = t.idDetalle ? "PUT" : "POST";

        if (t.idDetalle) {
            detalle.id = t.idDetalle;
        }

        return fetch("DetalleVisitaServlet", {
            method: metodoTicket,
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(detalle)
        })
                .then(async response => {
                    const data = await response.json();
                    if (!response.ok)
                        throw new Error(data.error || "Error al guardar");
                    return data;
                });
    });

    Promise.all(promesas)
            .then(() => {

                let idsActuales = tickets
                        .filter(t => t.idDetalle)
                        .map(t => t.idDetalle);

                let idsEliminar = idsOriginales.filter(id => !idsActuales.includes(id));

                let eliminaciones = idsEliminar.map(id =>
                    fetch("DetalleVisitaServlet?id=" + id, {method: "DELETE"})
                            .then(r => r.json())
                );

                return Promise.all(eliminaciones);
            })
            .then(() => {

                let msgErr = document.getElementById("mensajeError");
                if (msgErr)
                    msgErr.innerHTML = "";
                idsOriginales = [];

                if (!idExistente) {
                    paginaActual = 1;      // solo cuando es un registro nuevo
                }

                cargarTabla();
                limpiarFormulario();

                Swal.fire({
                    icon: "success",
                    title: idExistente ? "Actualizado" : "Guardado",
                    text: idExistente ? "Visita actualizada correctamente" : "Visita guardada correctamente",
                    confirmButtonColor: "#3f5b4b",
                    timer: 2000,
                    timerProgressBar: true
                });
            })
            .catch(error => {

                let msgErr = document.getElementById("mensajeError");
                if (msgErr)
                    msgErr.innerHTML = "";

                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: error.message,
                    confirmButtonColor: "#3f5b4b"
                });

                console.error(error);
            });
}


// CARGAR TABLA

function cargarTabla() {
    fetch("DetalleVisitaServlet")
            .then(res => res.json())
            .then(data => mostrarTabla(data))
            .catch(error => console.error(error));
}


// MOSTRAR TABLA
function mostrarTabla(lista) {

    if (!Array.isArray(lista)) {
        console.error("Respuesta inválida:", lista);
        return;
    }

    let grupos = {};

    lista.forEach(d => {

        let fecha = Array.isArray(d.fechaVisita)
                ? `${String(d.fechaVisita[2]).padStart(2, "0")}-${String(d.fechaVisita[1]).padStart(2, "0")}-${d.fechaVisita[0]}`
                : d.fechaVisita.split("-").reverse().join("-");

        let clave = d.nombreVisitante + "_" + d.telefono + "_" + fecha;

        if (!grupos[clave]) {
            grupos[clave] = {
                id: d.id, 
                nombre: d.nombreVisitante,
                telefono: d.telefono,
                fecha: fecha,
                tickets: [],
                total: 0
            };
        }

        grupos[clave].tickets.push(`${d.ticket.tipo} x${d.cantidad}`);
        grupos[clave].total += d.subtotal;
    });

    // 🛠️ AQUÍ SE RE-ASIGNAN LOS IDS CORRELATIVOS EN LA PANTALLA:
    gruposVisita = Object.values(grupos).map((grupo, index) => {
        grupo.id = index + 1; // Genera de forma limpia 1, 2, 3... correlativamente
        return grupo;
    });

    renderTablaPaginada();
    renderPaginacion();
}


function editarVisita(nombre, telefono, fecha) {

    fetch("DetalleVisitaServlet")
            .then(r => r.json())
            .then(lista => {

                tickets = [];
                idsOriginales = [];

                let registros = lista.filter(d => {
                    let fechaD = Array.isArray(d.fechaVisita)
                            ? `${String(d.fechaVisita[2]).padStart(2, "0")}-${String(d.fechaVisita[1]).padStart(2, "0")}-${d.fechaVisita[0]}`
                            : d.fechaVisita.split("-").reverse().join("-");

                    return d.nombreVisitante === nombre &&
                            d.telefono === telefono &&
                            fechaD === fecha;
                });

                if (registros.length === 0)
                    return;

                document.getElementById("nombreVisitante").value = nombre;
                document.getElementById("telefono").value = telefono;
                document.getElementById("idDetalleVisita").value = registros[0].id;

                registros.forEach(d => {
                    idsOriginales.push(d.id);
                    tickets.push({
                        idDetalle: d.id,
                        tipo: d.ticket.tipo,
                        cantidad: d.cantidad,
                        precio: d.ticket.precio,
                        subtotal: d.subtotal
                    });
                });

                renderTickets();
                document.querySelector(".guardar").textContent = "Actualizar";
//                window.scrollTo({top: 0, behavior: "smooth"});
            });
}


function renderTablaPaginada() {

    let tbody = document.querySelector("#tablaDetalleVisita tbody");
    if (!tbody)
        return;

    tbody.innerHTML = "";

    let inicio = (paginaActual - 1) * size;
    let fin = inicio + size;

    let pagina = gruposVisita.slice(inicio, fin);

    if (pagina.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="7" style="text-align:center;">
                    No hay registros de visitas
                </td>
            </tr>
        `;
        return;
    }

    let html = "";

    pagina.forEach(g => {
        html += `
            <tr>
                <td>${g.id}</td>
                <td>${g.nombre}</td>
                <td>${g.telefono}</td>
                <td>${g.fecha}</td>
                <td>${g.tickets.join("<br>")}</td>
                <td>$${g.total.toFixed(2)}</td>
                <td>
                    <div class="acciones">
                        <button class="btnEditar"
                                onclick="editarVisita('${g.nombre}', '${g.telefono}', '${g.fecha}')">
                            <i class="ti ti-edit"></i>
                        </button>
                    </div>
                </td>
            </tr>
        `;
    });

    tbody.innerHTML = html;
}


function renderPaginacion() {

    const pagContenedor = document.getElementById("paginacion");
    if (!pagContenedor)
        return;

    pagContenedor.innerHTML = `
        <button type="button" onclick="anterior()">
            <i class="ti ti-chevron-left"></i>
        </button>
        Página ${paginaActual}
        <button type="button" onclick="siguiente()">
            <i class="ti ti-chevron-right"></i>
        </button>
    `;
}

function siguiente() {

    let totalPaginas = Math.ceil(gruposVisita.length / size);

    if (paginaActual < totalPaginas) {
        paginaActual++;
        renderTablaPaginada();
        renderPaginacion();
    }
}

function anterior() {

    if (paginaActual > 1) {
        paginaActual--;
        renderTablaPaginada();
        renderPaginacion();
    }
}


function limpiarFormulario() {
    document.getElementById("formDetalleVisita").reset();
    document.getElementById("idDetalleVisita").value = "";
    document.getElementById("errorTicket").style.display = "none";

    let msgErr = document.getElementById("mensajeError");
    if (msgErr)
        msgErr.innerHTML = "";

    document.querySelector(".guardar").textContent = "Guardar";
    idsOriginales = [];
    tickets = [];
    renderTickets();
}