console.log("JS HISTORIAL MÉDICO CARGADO - CORREGIDO CON SESIÓN GLOBAL AUTOMÁTICA");

let mapaAnimalesEspecies = {};
let paginaActual = 1;
const size = 5;
let datosCompletos = [];

// 🌟 VARIABLE GLOBAL
let idVeterinarioSesion = null;

// ===============================
// INICIO
// ===============================
document.addEventListener("DOMContentLoaded", function () {
    autocompletarVeterinario();
    cargarAnimales();
});

// ==========================================================
// GUARDAR O ACTUALIZAR
// ==========================================================
document.getElementById("formHistorial").addEventListener("submit", function (event) {
    event.preventDefault();

    let id = document.getElementById("idHistorial").value;
    let idAnimalRaw = document.getElementById("idAnimal").value;
    let selectVet = document.getElementById("idVeterinario");
    
    // 🛡️ Prioridad de captura de datos infalible para evitar nulos en el objeto
    let idVeterinarioRaw = idVeterinarioSesion || (selectVet ? selectVet.value : "");

    if (!idVeterinarioRaw && selectVet) {
        idVeterinarioRaw = selectVet.value; 
    }

    let historial = {
        fecha: document.getElementById("fecha").value,
        diagnostico: document.getElementById("diagnostico").value,
        treatment: document.getElementById("tratamiento").value,
        tratamiento: document.getElementById("tratamiento").value,
        animal: idAnimalRaw ? { id: parseInt(idAnimalRaw) } : null,
        veterinario: idVeterinarioRaw ? { id: parseInt(idVeterinarioRaw) } : null
    };

    if (id) {
        historial.id = parseInt(id);
    }
    
    let metodo = id ? "PUT" : "POST";

    fetch("/ProyectoFinalZoo/HistorialMedicoServlet", {
        method: metodo,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(historial)
    })
    .then(async response => {
        const text = await response.text();
        let data;
        try { data = JSON.parse(text); } catch (e) { throw new Error("Error en la respuesta del servidor."); }
        if (!response.ok) throw new Error(data.error || "Error al procesar la solicitud.");
        return data;
    })
    .then(data => {
        let msgError = document.getElementById("mensajeError");
        if (msgError) msgError.innerHTML = "";

        limpiarFormularioHistorial();
        buscarHistoriales(paginaActual);

        Swal.fire({
            icon: "success",
            title: id ? "Historial Actualizado" : "Historial Registrado",
            text: data.mensaje || "Operación clínica realizada con éxito.",
            confirmButtonColor: "#3f5b4b"
        });
    })
    .catch(error => {
        console.error(error);
        mostrarAlertaError(error.message);
    });
});
// ===================================================
// FORMATEAR FECHA (CORREGIDO)
// ===================================================
function formatearFecha(fecha) {
    if (!fecha) return "";

    const soloFecha = fecha.toString().split("T")[0];
    const partes = soloFecha.split("-");

    if (partes.length !== 3) return fecha;

    const anio = partes[0];
    const mes = partes[1];
    const dia = partes[2];

    return `${dia}/${mes}/${anio}`;
}

function autocompletarVeterinario() {
    fetch("/ProyectoFinalZoo/HistorialMedicoServlet?accion=obtenerSesion")
        .then(response => {
            if (!response.ok) throw new Error("No hay sesión activa.");
            return response.json();
        })
        .then(empleado => {
            idVeterinarioSesion = empleado.id;
            console.log("Veterinario de sesión:", idVeterinarioSesion);
        })
        .catch(error => console.error(error));
}
// ===============================
// CARGAR ANIMALES
// ===============================
function cargarAnimales() {
    fetch("/ProyectoFinalZoo/AnimalServlet")
        .then(response => {
            if (!response.ok) throw new Error("Error al obtener catálogo de animales.");
            return response.json();
        })
        .then(data => {
            let select = document.getElementById("idAnimal");
            select.innerHTML = '<option value="">Seleccione un animal...</option>';
            mapaAnimalesEspecies = {};

            data.forEach(animal => {
                let nombreEspecie =
                    (animal.especie && typeof animal.especie === 'object')
                        ? animal.especie.nombre
                        : animal.especie;

                if (animal.nombre) {
                    mapaAnimalesEspecies[animal.nombre.trim().toLowerCase()] =
                        nombreEspecie || "Sin especie";
                }

                select.innerHTML += `<option value="${animal.id}">${animal.nombre}</option>`;
            });

            buscarHistoriales();
        })
        .catch(error => {
            console.error(error);
            mostrarAlertaError("No se pudieron cargar los animales.");
            buscarHistoriales();
        });
}

// ===============================
// BUSCAR HISTORIALES
// ===============================
function buscarHistoriales(pagina = 1) {
    paginaActual = pagina;

    fetch(`/ProyectoFinalZoo/HistorialMedicoServlet?_=${new Date().getTime()}`)
        .then(response => response.json())
        .then(data => {
            console.log("Datos de historial recibidos:", data);
            datosCompletos = data;

            if (Array.isArray(datosCompletos)) {
                redibujarTablaLocal();
            }
        })
        .catch(error => {
            console.error(error);
            mostrarAlertaError("No se pudo cargar la lista de historiales médicos.");
        });
}

// ===============================
// MOSTRAR TABLA
// ===============================
function mostrarHistoriales(lista) {
    if (!Array.isArray(lista)) return;

    let html = "";

    lista.forEach(h => {
        let nombreAnimal = h.animal ? h.animal.nombre : null;

        let especieReal = nombreAnimal
            ? (mapaAnimalesEspecies[nombreAnimal.trim().toLowerCase()] || "Sin especie")
            : "Sin especie";

        html += `
            <tr>
                <td>${h.id ?? "—"}</td>
                <td>${formatearFecha(h.fecha)}</td>
                <td>${h.diagnostico ?? "—"}</td>
                <td>${h.tratamiento ?? h.treatment ?? "—"}</td>
                <td>${nombreAnimal || "Sin animal"}</td>
                <td>${especieReal}</td>
                <td>${h.veterinario ? h.veterinario.nombre + ' ' + h.veterinario.apellido : "Sin veterinario"}</td>
                <td class="acciones">
                    <button class="btnEditar" onclick="editarHistorial(${h.id})">
                        <i class="ti ti-edit"></i>
                    </button>
                </td>
            </tr>
        `;
    });

    document.getElementById("tbodyHistoriales").innerHTML = html;
}

// ===============================
// PAGINACIÓN
// ===============================
function renderPaginacion(totalRegistros) {
    const pagContenedor = document.getElementById("paginacion");
    if (!pagContenedor) return;

    const totalPaginas = Math.ceil(totalRegistros / size) || 1;

    pagContenedor.innerHTML = `
        <button onclick="anterior()" ${paginaActual === 1 ? 'disabled' : ''}>
            <i class="ti ti-chevron-left"></i>
        </button>

        <span>Página ${paginaActual} de ${totalPaginas}</span>

        <button onclick="siguiente()" ${paginaActual === totalPaginas ? 'disabled' : ''}>
            <i class="ti ti-chevron-right"></i>
        </button>
    `;
}

function anterior() {
    if (paginaActual > 1) {
        paginaActual--;
        redibujarTablaLocal();
    }
}

function siguiente() {
    const totalPaginas = Math.ceil(datosCompletos.length / size);

    if (paginaActual < totalPaginas) {
        paginaActual++;
        redibujarTablaLocal();
    }
}

function redibujarTablaLocal() {
    const inicio = (paginaActual - 1) * size;
    const fin = inicio + size;

    const datos = datosCompletos.slice(inicio, fin);

    mostrarHistoriales(datos);
    renderPaginacion(datosCompletos.length);
}

// ===============================
// EDITAR (CORREGIDO FECHA)
// ===============================
function editarHistorial(id) {
    fetch(`/ProyectoFinalZoo/HistorialMedicoServlet?id=${id}`)
        .then(response => {
            if (!response.ok) throw new Error("No se pudo obtener el registro.");
            return response.json();
        })
        .then(h => {

            document.getElementById("idHistorial").value = h.id;

            document.getElementById("fecha").value =
                h.fecha ? h.fecha.toString().split("T")[0] : "";

            document.getElementById("diagnostico").value = h.diagnostico ?? "";

            document.getElementById("tratamiento").value =
                h.tratamiento ?? h.treatment ?? "";

            document.getElementById("idAnimal").value =
                h.animal ? h.animal.id : "";

            if (h.veterinario && h.veterinario.id) {
                let selectVet = document.getElementById("idVeterinario");
                if (selectVet) selectVet.value = h.veterinario.id;
                idVeterinarioSesion = h.veterinario.id;
            }

            let btnGuardar = document.getElementById("btnGuardar");
            if (btnGuardar) btnGuardar.textContent = "Actualizar Historial";

            window.scrollTo({ top: 0, behavior: "smooth" });
        })
        .catch(error => {
            console.error(error);
            mostrarAlertaError("Error al cargar los datos.");
        });
}

// ===============================
// ALERTA
// ===============================
function mostrarAlertaError(mensaje) {
    Swal.fire({
        icon: "error",
        title: "Error Clínico",
        text: mensaje,
        confirmButtonColor: "#b05d4d"
    });
}

function limpiarFormularioHistorial() {
    document.getElementById("formHistorial").reset();
    document.getElementById("idHistorial").value = "";
    
    let btnGuardar = document.getElementById("btnGuardar");
    if (btnGuardar) {
        btnGuardar.textContent = "Guardar Historial";
    }
    
    // Restablecemos el ID en el select oculto para las siguientes inserciones
    let selectVet = document.getElementById("idVeterinario");
    if (selectVet && idVeterinarioSesion) {
        selectVet.value = idVeterinarioSesion;
    }
}