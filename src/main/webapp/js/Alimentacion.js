/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

/* * Alimentacion.js
 */

console.log("JS ALIMENTACION CARGADO - CON CUIDADOR LOGUEADO AUTOMÁTICO");

let paginaActual = 1;
const size = 5; // 🌟 Límite estricto de 5 registros por página
let datosCompletos = []; // Almacén global para segmentar los datos de la BD

// 🌟 VARIABLE GLOBAL: Guarda el ID del cuidador que inició sesión
let idCuidadorSesion = null; 

// ===============================
// INICIO
// ===============================
document.addEventListener("DOMContentLoaded", function () {
    autocompletarCuidador(); 
    cargarAnimales();
    buscar();
});

// ===================================================
// 🌟 OBTENER SESIÓN ACTIVA DEL CUIDADOR 
// ===================================================
function autocompletarCuidador() {
    fetch("AlimentacionServlet?accion=obtenerSesion")   
        .then(response => {
            if (!response.ok) throw new Error("No hay una sesión de cuidador activa.");
            return response.json();
        })
        .then(empleado => {
            idCuidadorSesion = empleado.id; 
            console.log("Cuidador en sesión detectado con éxito. ID:", idCuidadorSesion);
        })
        .catch(error => {
            console.log("Aviso de Sesión de Cuidador:", error.message);
        });
}

// ===============================
// CARGAR ANIMALES EN COMBOBOX
// ===============================
function cargarAnimales() {
    fetch('AnimalServlet')
        .then(response => {
            if (!response.ok) throw new Error("Error al obtener la lista de animales.");
            return response.json();
        })
        .then(animales => {
            const selectAnimal = document.getElementById('idAnimal');
            selectAnimal.length = 1; 

            animales.forEach(animal => {
                const option = document.createElement('option');
                option.value = animal.id;
                option.textContent = animal.especie;
                selectAnimal.appendChild(option);
            });
        })
        .catch(error => {
            console.error('Error al cargar animales:', error);
        });
}

// ===============================
// BUSCAR ALIMENTACIONES (CORREGIDO)
// ===============================
function buscar(pagina = 1) {
    paginaActual = pagina;

    fetch("AlimentacionServlet")
        .then(response => {
            if (!response.ok) throw new Error("Error al consultar el registro de alimentación.");
            return response.json();
        })
        .then(data => {
            console.log("Datos de alimentación recibidos:", data);
            datosCompletos = data; // Almacenamos el array completo de la BD
            
            if (Array.isArray(datosCompletos)) {
                // Forzar la segmentación inicial
                redibujarTablaLocal();
            }
        })
        .catch(error => {
            console.error("Error buscando:", error);
            mostrarAlertaError("No se pudo cargar el historial de alimentación.");
        });
}

// ===============================
// MOSTRAR ALIMENTACIONES EN TABLA
// ===============================
function mostrarAlimentaciones(lista) {
    const tbody = document.getElementById('tbodyAlimentacion');
    tbody.innerHTML = ''; 

    if (!Array.isArray(lista)) {
        console.error("Respuesta inválida:", lista);
        return;
    }

    lista.forEach(alimentacion => {
        const tr = document.createElement('tr');
        
        // Validación en cascada para evitar nulos si la especie no viene cargada en el JSON
        let especieAnimal = 'Sin asignar';
        if (alimentacion.animal) {
            especieAnimal = alimentacion.animal.especie ? alimentacion.animal.especie : `Animal ID: ${alimentacion.animal.id}`;
        }

        // LECTURA DE LA PROPIEDAD 'CUIDADOR'
        let nombreCuidador = "—";
        if (alimentacion.cuidador) {
            nombreCuidador = `${alimentacion.cuidador.nombre ?? ""} ${alimentacion.cuidador.apellido ?? ""}`.trim();
        }
        if (!nombreCuidador) nombreCuidador = "—";

        tr.innerHTML = `
            <td>${alimentacion.id}</td>
            <td>${alimentacion.tipoAlimento ?? "—"}</td>
            <td>${alimentacion.horario ?? "—"}</td>
            <td>${alimentacion.cantidad ?? "0"}</td>
            <td>${especieAnimal}</td>
            <td>${nombreCuidador}</td>
            <td class="acciones">
                <button class="btnEditar" onclick="editar(${alimentacion.id})">
                    <i class="ti ti-edit"></i>
                </button>
                <button class="btnEliminar" onclick="eliminarAlimentacion(${alimentacion.id})">
                    <i class="ti ti-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

// ==========================================================
// RENDERIZAR CONTROLES DE PAGINACIÓN (UNIFICADO)
// ==========================================================
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

// ==========================================================
// LÓGICAS DE NAVEGACIÓN LOCAL (EL MOTOR DEL RECORTE)
// ==========================================================
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
    // 🧠 Matemática para recortar la lista en bloques de 5
    const inicio = (paginaActual - 1) * size;
    const fin = inicio + size;
    const registrosSegmentados = datosCompletos.slice(inicio, fin);
    
    mostrarAlimentaciones(registrosSegmentados);
    renderPaginacion(datosCompletos.length);
}

// ===============================
// EDITAR REGISTRO
// ===============================
function editar(id) {
    fetch(`AlimentacionServlet?id=${id}`)
        .then(response => {
            if (!response.ok) throw new Error("No se pudo obtener el registro seleccionado.");
            return response.json();
        })
        .then(a => {
            document.getElementById("idAlimentacion").value = a.id;
            document.getElementById("tipoAlimento").value = a.tipoAlimento;
            document.getElementById("horario").value = a.horario;
            document.getElementById("cantidad").value = a.cantidad;
            document.getElementById("idAnimal").value = a.animal ? a.animal.id : "";

            let btnGuardar = document.getElementById("btnGuardarAlimentacion") || document.getElementById("btnGuardar");
            if (btnGuardar) {
                btnGuardar.textContent = "Actualizar Registro";
            }

            window.scrollTo({ top: 0, behavior: "smooth" });
        })
        .catch(error => {
            console.error("Error editando:", error);
            mostrarAlertaError("Error al recuperar los datos de la alimentación.");
        });
}

// ===============================
// GUARDAR O ACTUALIZAR
// ===============================
document.getElementById("formAlimentacion").addEventListener("submit", function (event) {
    event.preventDefault();

    let id = document.getElementById("idAlimentacion").value;
    let idAnimal = document.getElementById("idAnimal").value;

    let alimentacion = {
        tipoAlimento: document.getElementById("tipoAlimento").value,
        horario: document.getElementById("horario").value,
        cantidad: parseFloat(document.getElementById("cantidad").value),
        animal: idAnimal ? { id: parseInt(idAnimal) } : null,
        // 🌟 ENVIAR PROPIEDAD CUIDADOR COINCIDIENDO CON JAVA
        cuidador: idCuidadorSesion ? { id: parseInt(idCuidadorSesion) } : null
    };

    if (id) {
        alimentacion.id = parseInt(id);
    }

    let metodo = id ? "PUT" : "POST";

    fetch("AlimentacionServlet", {
        method: metodo,
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(alimentacion)
    })
    .then(async response => {
        const text = await response.text();
        let data;
        try { data = JSON.parse(text); } catch (e) { throw new Error(text || "Error interno."); }
        if (!response.ok) throw new Error(data.error || "Error al procesar el registro.");
        return data;
    })
    .then(data => {
        let msgError = document.getElementById("mensajeErrorAlimentacion") || document.getElementById("mensajeError");
        if (msgError) msgError.innerHTML = "";

        limpiarFormulario();
        buscar(paginaActual); 

        Swal.fire({
            icon: "success",
            title: id ? "Registro Actualizado" : "Registro Guardado",
            text: data.mensaje || "La alimentación se procesó correctamente.",
            confirmButtonColor: "#3f5b4b"
        });
    })
    .catch(error => {
        mostrarAlertaError(error.message);
    });
});

// ===============================
// ELIMINAR REGISTRO
// ===============================
function eliminarAlimentacion(id) {
    Swal.fire({
        title: "¿Deseas eliminar este registro?",
        text: "Esta acción removerá el horario y asignación de comida del animal.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#b05d4d",
        cancelButtonColor: "#3f5b4b",
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (!result.isConfirmed) return;

        fetch(`AlimentacionServlet?id=${id}`, { method: 'DELETE' })
        .then(async response => {
            const texto = await response.text();
            if (!response.ok) throw new Error(texto || "No se pudo eliminar el registro.");
            try { return JSON.parse(texto); } catch (e) { return { mensaje: texto }; }
        })
        .then(data => {
            Swal.fire({
                icon: "success",
                title: "Eliminado",
                text: data.mensaje || "El registro ha sido removido.",
                confirmButtonColor: "#3f5b4b"
            });
            buscar(paginaActual);
            limpiarFormulario();
        })
        .catch(error => {
            mostrarAlertaError(error.message);
        });
    });
}

function limpiarFormulario() {
    document.getElementById("formAlimentacion").reset();
    document.getElementById("idAlimentacion").value = "";
    let btnGuardar = document.getElementById("btnGuardarAlimentacion") || document.getElementById("btnGuardar");
    if (btnGuardar) {
        btnGuardar.textContent = "Guardar Alimentación";
    }
}

function mostrarAlertaError(mensaje) {
    Swal.fire({
        icon: "error",
        title: "Error detectado",
        text: mensaje,
        confirmButtonColor: "#b05d4d"
    });
}