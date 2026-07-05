/* * HistorialMedico.js */

console.log("JS HISTORIAL MÉDICO CARGADO - CORREGIDO CON SWEETALERT INDIVIDUAL");

let mapaAnimalesEspecies = {};
let paginaActual = 1;
const size = 5;
let datosCompletos = [];
let idVeterinarioSesion = null;

//INICIO
document.addEventListener("DOMContentLoaded", function () {
    autocompletarVeterinario();
    cargarAnimales();
    restringirFechasFuturas(); 
});

// RESTRINGIR CALENDARIO 
function restringirFechasFuturas() {
    const hoy = new Date();
    const año = hoy.getFullYear();
    let mes = hoy.getMonth() + 1;
    let dia = hoy.getDate();

    if (mes < 10) mes = '0' + mes;
    if (dia < 10) dia = '0' + dia;

    const fechaMaxima = `${año}-${mes}-${dia}`;
    const inputFecha = document.getElementById("fecha");

    if (inputFecha) {
        inputFecha.setAttribute("max", fechaMaxima);
    }
}


function autocompletarVeterinario() {
    fetch("/ProyectoFinalZoo/HistorialMedicoServlet?accion=obtenerSesion")
        .then(response => {
            if (!response.ok) throw new Error("No hay sesión activa.");
            return response.json();
        })
        .then(empleado => {
            idVeterinarioSesion = empleado.id;
            console.log("Veterinario de sesión detectado con éxito. ID:", idVeterinarioSesion);
        })
        .catch(error => console.error("Aviso de Sesión de Veterinario:", error.message));
}


function cargarAnimales() {
    fetch('AnimalServlet')
        .then(response => {
            if (!response.ok) throw new Error("Error al obtener la lista de animales.");
            return response.json();
        })
        .then(animales => {
            const selectAnimal = document.getElementById('idAnimal');
            if (!selectAnimal) return;
            selectAnimal.length = 1; 

            mapaAnimalesEspecies = {};

            animales.forEach(animal => {
                let nombreEspecie = (animal.especie && typeof animal.especie === 'object') ? animal.especie.nombre : animal.especie;
                if (animal.nombre) {
                    mapaAnimalesEspecies[animal.nombre.trim().toLowerCase()] = nombreEspecie || "Sin especie";
                }

                const option = document.createElement('option');
                option.value = animal.id;
                option.textContent = `[ID: ${animal.id}] ${animal.nombre}`; 
                selectAnimal.appendChild(option);
            });

            
            buscarHistoriales();
        })
        .catch(error => {
            console.error('Error al cargar animales:', error);
            buscarHistoriales(); 
        });
}

// GUARDAR O ACTUALIZAR 
document.getElementById("formHistorial").addEventListener("submit", function (event) {
    event.preventDefault();

    let id = document.getElementById("idHistorial").value;
    let idAnimalRaw = document.getElementById("idAnimal").value;
    let selectVet = document.getElementById("idVeterinario");
    
    let fecha = document.getElementById("fecha").value;
    let diagnostico = document.getElementById("diagnostico").value.trim();
    let tratamiento = document.getElementById("tratamiento").value.trim();

    let idVeterinarioRaw = idVeterinarioSesion || (selectVet ? selectVet.value : "");
    if (!idVeterinarioRaw && selectVet) {
        idVeterinarioRaw = selectVet.value;
    }

    const regexLetrasClinicas = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s.,\-]+$/;
    const fechaActual = new Date().toISOString().split('T')[0];

    if (!fecha) {
        Swal.fire({ icon: "warning", title: "Campo requerido", text: "Por favor, elija la fecha de la consulta.", confirmButtonColor: "#b05d4d" });
        return;
    }
    if (fecha > fechaActual) {
        Swal.fire({ icon: "warning", title: "Fecha inválida", text: "¡El historial clínico no puede registrarse en el futuro!", confirmButtonColor: "#b05d4d" });
        return;
    }

    if (diagnostico.length < 5) {
        Swal.fire({ icon: "warning", title: "Campo inválido", text: "El diagnóstico debe tener mínimo 5 caracteres.", confirmButtonColor: "#b05d4d" });
        return;
    }
    if (!regexLetrasClinicas.test(diagnostico)) {
        Swal.fire({ icon: "warning", title: "Formato incorrecto", text: "El diagnóstico solo puede contener letras y signos de puntuación básicos.", confirmButtonColor: "#b05d4d" });
        return;
    }

    if (tratamiento.length < 5) {
        Swal.fire({ icon: "warning", title: "Campo inválido", text: "El tratamiento debe tener mínimo 5 caracteres.", confirmButtonColor: "#b05d4d" });
        return;
    }
    if (!regexLetrasClinicas.test(tratamiento)) {
        Swal.fire({ icon: "warning", title: "Formato incorrecto", text: "El tratamiento solo puede contener letras.", confirmButtonColor: "#b05d4d" });
        return;
    }

    if (!idAnimalRaw) {
        Swal.fire({ icon: "warning", title: "Campo requerido", text: "Debe asociar un animal válido al expediente.", confirmButtonColor: "#b05d4d" });
        return;
    }

    let historial = {
        fecha: fecha,
        diagnostico: diagnostico,
        tratamiento: tratamiento,
        animal: idAnimalRaw ? { id: parseInt(idAnimalRaw, 10) } : null,
        veterinario: idVeterinarioRaw ? { id: parseInt(idVeterinarioRaw, 10) } : null
    };

    if (id) {
        historial.id = parseInt(id, 10);
    }

    let metodo = id ? "PUT" : "POST";

    fetch("/ProyectoFinalZoo/HistorialMedicoServlet", {
        method: metodo,
        headers: {"Content-Type": "application/json"},
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
        Swal.fire({
            icon: "warning",
            title: "No se puede guardar",
            text: error.message,
            confirmButtonColor: "#b05d4d"
        });
    });
});


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


function renderPaginacion(totalRegistros) {
    const pagContenedor = document.getElementById("paginacion");
    if (!pagContenedor) return;

    const totalPaginas = Math.ceil(totalRegistros / size) || 1;

    pagContenedor.innerHTML = `
        <button onclick="anterior()" ${paginaActual === 1 ? 'disabled style="opacity:0.5; cursor:not-allowed;"' : ''}>
            <i class="ti ti-chevron-left"></i>
        </button>
        <span style="margin: 0 10px; font-weight: bold;">Página ${paginaActual} de ${totalPaginas}</span>
        <button onclick="siguiente()" ${paginaActual === totalPaginas ? 'disabled style="opacity:0.5; cursor:not-allowed;"' : ''}>
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


function editarHistorial(id) {
    fetch(`/ProyectoFinalZoo/HistorialMedicoServlet?id=${id}`)
        .then(response => {
            if (!response.ok) throw new Error("No se pudo obtener el registro.");
            return response.json();
        })
        .then(h => {
            document.getElementById("idHistorial").value = h.id;
            document.getElementById("fecha").value = h.fecha ? h.fecha.toString().split("T")[0] : "";
            document.getElementById("diagnostico").value = h.diagnostico ?? "";
            document.getElementById("tratamiento").value = h.tratamiento ?? h.treatment ?? "";
            document.getElementById("idAnimal").value = h.animal ? h.animal.id : "";

            if (h.veterinario && h.veterinario.id) {
                let selectVet = document.getElementById("idVeterinario");
                if (selectVet) selectVet.value = h.veterinario.id;
                idVeterinarioSesion = h.veterinario.id;
            }

            let btnGuardar = document.getElementById("btnGuardar");
            if (btnGuardar) btnGuardar.textContent = "Actualizar Historial";

            window.scrollTo({top: 0, behavior: "smooth"});
        })
        .catch(error => {
            console.error(error);
            mostrarAlertaError("Error al cargar los datos en el formulario.");
        });
}


function formatearFecha(fecha) {
    if (!fecha) return "";
    const soloFecha = fecha.toString().split("T")[0];
    const partes = soloFecha.split("-");
    if (partes.length !== 3) return fecha;
    return `${partes[2]}/${partes[1]}/${partes[0]}`;
}

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

    let selectVet = document.getElementById("idVeterinario");
    if (selectVet && idVeterinarioSesion) {
        selectVet.value = idVeterinarioSesion;
    }
}