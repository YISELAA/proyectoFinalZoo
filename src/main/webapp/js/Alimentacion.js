/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */

/* * Alimentacion.js
 * */

console.log("JS ALIMENTACION");

let paginaActual = 1;
const size = 5;
let datosCompletos = []; 
let idCuidadorSesion = null; 



// INICIO
document.addEventListener("DOMContentLoaded", function () {
    autocompletarCuidador(); 
    cargarAnimales();
    buscar();
});


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

function cargarAnimales() {
    fetch('AnimalServlet')
        .then(response => {
            if (!response.ok) throw new Error("Error al obtener la lista de animales.");
            return response.json();
        })
        .then(animales => {
            const selectAnimal = document.getElementById('idAnimal');
            if (selectAnimal) {
                selectAnimal.length = 1; 

                animales.forEach(animal => {
                    const option = document.createElement('option');
                    option.value = animal.id;
                    option.textContent = animal.especie;
                    selectAnimal.appendChild(option);
                });
            }
        })
        .catch(error => {
            console.error('Error al cargar animales:', error);
        });
}


function buscar(pagina = 1) {
    paginaActual = pagina;

    fetch("AlimentacionServlet")
        .then(response => {
            if (!response.ok) throw new Error("Error al consultar el registro de alimentación.");
            return response.json();
        })
        .then(data => {
            console.log("Datos de alimentación recibidos:", data);
            datosCompletos = data; 
            
            if (Array.isArray(datosCompletos)) {
                redibujarTablaLocal();
            }
        })
        .catch(error => {
            console.error("Error buscando:", error);
            mostrarAlertaError("No se pudo cargar el historial de alimentación.");
        });
}


function mostrarAlimentaciones(lista) {
    const tbody = document.getElementById('tbodyAlimentacion');
    if (!tbody) return;
    tbody.innerHTML = ''; 

    if (!Array.isArray(lista)) {
        console.error("Respuesta inválida:", lista);
        return;
    }

    lista.forEach(alimentacion => {
        const tr = document.createElement('tr');
       
        let objetoAnimal = alimentacion.animal || alimentacion.anima;
        let especieAnimal = 'Sin asignar';

        if (objetoAnimal) {
            especieAnimal = objetoAnimal.especie ? objetoAnimal.especie : `Animal ID: ${objetoAnimal.id}`;
        }

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

//controles de paginacion
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
    const registrosSegmentados = datosCompletos.slice(inicio, fin);
    
    mostrarAlimentaciones(registrosSegmentados);
    renderPaginacion(datosCompletos.length);
}


function editar(id) {
    fetch(`AlimentacionServlet?id=${id}`)
        .then(response => {
            if (!response.ok) throw new Error("No se pudo obtener el registro seleccionado.");
            return response.json();
        })
        .then(a => {
            let objetoAnimal = a.animal || a.anima;

            document.getElementById("idAlimentacion").value = a.id;
            document.getElementById("tipoAlimento").value = a.tipoAlimento ?? "";
            document.getElementById("cantidad").value = a.cantidad ?? "";
            document.getElementById("idAnimal").value = objetoAnimal ? objetoAnimal.id : "";

            // --- RECONVERSIÓN: De "4:00 PM" proveniente de la BD a "16:00" para el input nativo ---
            let horarioInput = "";
            if (a.horario) {
                let str = a.horario.trim().toUpperCase();
                let esPM = str.includes("PM");
                let esAM = str.includes("AM");
                let limpio = str.replace("AM", "").replace("PM", "").trim();
                let partesHora = limpio.split(":");
                
                let h = parseInt(partesHora[0], 10);
                let m = partesHora.length > 1 ? partesHora[1] : "00";
                if (m.length === 1) m = "0" + m;

                if (esPM && h < 12) h += 12;
                if (esAM && h === 12) h = 0;

                let hStr = h < 10 ? "0" + h : h;
                horarioInput = `${hStr}:${m}`; 
            }
            document.getElementById("horario").value = horarioInput;

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


// GUARDAR O ACTUALIZAR
document.getElementById("formAlimentacion").addEventListener("submit", function (event) {
    event.preventDefault();

    let id = document.getElementById("idAlimentacion").value;
    let tipoAlimento = document.getElementById("tipoAlimento").value.trim();
    let horarioRaw = document.getElementById("horario").value; 
    let cantidad = document.getElementById("cantidad").value;
    let idAnimal = document.getElementById("idAnimal").value;

    const regexLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;

    // Validación de Tipo de Alimento
    if (tipoAlimento.length < 3) {
        Swal.fire({ icon: "warning", title: "Campo inválido", text: "El tipo de alimento debe tener mínimo 3 caracteres.", confirmButtonColor: "#b05d4d" });
        return;
    }
    if (!regexLetras.test(tipoAlimento)) {
        Swal.fire({ icon: "warning", title: "Formato incorrecto", text: "El tipo de alimento solo puede contener letras.", confirmButtonColor: "#b05d4d" });
        return;
    }

    if (!horarioRaw) {
        Swal.fire({ icon: "warning", title: "Horario Requerido", text: "Por favor, elija una hora usando el selector.", confirmButtonColor: "#b05d4d" });
        return;
    }

    // VALIDACIÓN DE RANGO DIURNO ESTRICTO (8:00 AM a 4:00 PM) por el horario del zoologico
    let partes = horarioRaw.split(":");
    let hora = parseInt(partes[0], 10);
    let minutos = parseInt(partes[1], 10);

  
    let minutosTotales = (hora * 60) + minutos;

    if (minutosTotales < 480 || minutosTotales > 960) {
        Swal.fire({ 
            icon: "warning", 
            title: "Horario Fuera de Rango", 
            html: "Los animales solo pueden ser alimentados en el turno diurno:<br><b>Desde las 8:00 AM hasta las 4:00 PM</b>.", 
            confirmButtonColor: "#b05d4d" 
        });
        return; 
    }

    let sufijo = hora >= 12 ? "PM" : "AM";
    let horaConvertida = hora % 12;
    if (horaConvertida === 0) horaConvertida = 12; 
    let minutosString = minutos < 10 ? "0" + minutos : minutos;
    
    let horarioFinalAMPM = `${horaConvertida}:${minutosString} ${sufijo}`;

    if (!cantidad || parseFloat(cantidad) <= 0) {
        Swal.fire({ icon: "warning", title: "Campo inválido", text: "La cantidad debe ser un número mayor a 0.", confirmButtonColor: "#b05d4d" });
        return;
    }

    if (!idAnimal) {
        Swal.fire({ icon: "warning", title: "Campo requerido", text: "Debe seleccionar un animal válido.", confirmButtonColor: "#b05d4d" });
        return;
    }

    // Construcción del Objeto JSON
    let alimentacion = {
        tipoAlimento: tipoAlimento,
        horario: horarioFinalAMPM, 
        cantidad: parseFloat(cantidad),
        animal: { id: parseInt(idAnimal) },
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
        Swal.fire({
            icon: "warning",
            title: "No se puede guardar",
            text: error.message,
            confirmButtonColor: "#b05d4d"
        });
    });
});


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