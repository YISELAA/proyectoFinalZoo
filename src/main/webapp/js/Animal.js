/* * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/* * Animales.js
 */

console.log("JS ANIMALES CARGADO");

let paginaActual = 1;
const size = 5; // 🌟 Límite estricto de 5 registros por página
let datosCompletos = []; // Aquí guardaremos la lista global que viene del servidor

// ===============================
// INICIO
// ===============================
document.addEventListener("DOMContentLoaded", function () {
    buscarAnimales();
    cargarHabitats();
});

// ===============================
// FORMATEAR FECHA
// ===============================
function formatearFecha(fecha) {
    if (!fecha)
        return "";
    const solo = fecha.substring(0, 10);
    const partes = solo.split("-");
    return `${parseInt(partes[2])}/${parseInt(partes[1])}/${partes[0]}`;
}

// ===============================
// BUSCAR ANIMALES (CORREGIDO)
// ===============================
function buscarAnimales(pagina = 1) {
    paginaActual = pagina;
    fetch("/ProyectoFinalZoo/AnimalServlet")
            .then(response => {
                if (!response.ok) {
                    throw new Error("Error al obtener el listado de animales.");
                }
                return response.json();
            })
            .then(data => {
                console.log("Datos recibidos del servidor:", data);
                datosCompletos = data; // Guardamos el array completo de la BD

                if (Array.isArray(datosCompletos)) {
                    // Forzar el redibujado segmentado en la página solicitada
                    redibujarTablaLocal();
                }
            })
            .catch(error => {
                console.error("Error:", error);
                mostrarAlertaError("No se pudieron cargar los animales.");
            });
}

// ===============================
// MOSTRAR ANIMALES EN TABLA
// ===============================
function mostrarAnimales(lista) {
    if (!Array.isArray(lista)) {
        console.error("Respuesta inválida:", lista);
        return;
    }

    let html = "";
   lista.forEach(a => {
    // Manejo seguro por si a.habitat es null o no tiene tipoTerreno
    let nombreHabitat = a.habitat?.tipoTerreno || '<span class="badge bg-secondary">Sin Hábitat</span>';

    html += `
        <tr>
            <td>${a.id ?? "—"}</td>
            <td>${a.nombre ?? "—"}</td>
            <td>${a.especie ?? "—"}</td>
            <td>${formatearFecha(a.fechaNacimiento)}</td>
            <td>${calcularEdad(a.fechaNacimiento)}</td>
            <td>${formatearFecha(a.fechaIngreso)}</td>
            <td>${nombreHabitat}</td>
            <td class="acciones">
                <button class="btnEditar" onclick="editarAnimal(${a.id})">
                    <i class="ti ti-edit"></i>
                </button>
                <button class="btnEliminar" onclick="eliminarAnimal(${a.id})">
                    <i class="ti ti-trash"></i>
                </button>
            </td> </tr> `;
});
    document.getElementById("tbodyAnimales").innerHTML = html;
}

// ===============================
// CARGAR HABITATS
// ===============================
function cargarHabitats() {
    fetch("/ProyectoFinalZoo/HabitatServlet")
            .then(response => {
                if (!response.ok)
                    throw new Error();
                return response.json();
            })
            .then(data => {
                let combo = document.getElementById("habitat");
                combo.innerHTML = '<option value="">Seleccione hábitat</option>';
                data.forEach(h => {
                    combo.innerHTML += `
                    <option value="${h.id}">${h.tipoTerreno}</option>
                `;
                });
            })
            .catch(error => {
                console.error("Error cargando hábitats:", error);
            });
}

// ==========================================================
// RENDERIZAR CONTROLES DE PAGINACIÓN (SÍNCRO CON EL DOM)
// ==========================================================
function renderPaginacion(totalRegistros) {
    const pagContenedor = document.getElementById("paginacion");
    if (!pagContenedor)
        return;

    // Calcular cuántas páginas existen en total basándonos en los datos reales
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
    // 🧠 Aquí se hace la magia matemática para cortar el arreglo
    const inicio = (paginaActual - 1) * size;
    const fin = inicio + size;
    const registrosSegmentados = datosCompletos.slice(inicio, fin);

    // Mandamos a pintar únicamente los 5 registros seleccionados
    mostrarAnimales(registrosSegmentados);
    renderPaginacion(datosCompletos.length);
}

// ===============================
// EDITAR ANIMAL
// ===============================
function editarAnimal(id) {
    fetch(`/ProyectoFinalZoo/AnimalServlet?id=${id}`)
            .then(response => {
                if (!response.ok)
                    throw new Error("No se pudo obtener la información del animal.");
                return response.json();
            })
            .then(a => {
                document.getElementById("idAnimal").value = a.id;
                document.getElementById("nombreAnimal").value = a.nombre;
                document.getElementById("especie").value = a.especie;
                document.getElementById("fechaNacimiento").value =
                        a.fechaNacimiento ? a.fechaNacimiento.substring(0, 10) : "";
                document.getElementById("fechaIngreso").value =
                        a.fechaIngreso ? a.fechaIngreso.substring(0, 10) : "";
                document.getElementById("habitat").value =
                        a.habitat ? a.habitat.id : "";

                let btnGuardar = document.getElementById("btnGuardarAnimal") || document.getElementById("btnGuardar");
                if (btnGuardar) {
                    btnGuardar.textContent = "Actualizar Animal";
                }

                window.scrollTo({
                    top: 0,
                    behavior: "smooth"
                });
            })
            .catch(error => {
                console.error(error);
                mostrarAlertaError("Error al recuperar los datos del animal.");
            });
}

// ===============================
// GUARDAR O ACTUALIZAR
// ===============================
document.getElementById("formAnimal")
        .addEventListener("submit", function (event) {
            event.preventDefault();

            let id = document.getElementById("idAnimal").value;
            let idHabitatRaw = document.getElementById("habitat").value;

            let animal = {
                nombre: document.getElementById("nombreAnimal").value,
                especie: document.getElementById("especie").value,
                fechaNacimiento: document.getElementById("fechaNacimiento").value,
                fechaIngreso: document.getElementById("fechaIngreso").value,
                habitat: idHabitatRaw ? {id: parseInt(idHabitatRaw)} : null
            };

            if (id) {
                animal.id = parseInt(id);
            }

            let metodo = id ? "PUT" : "POST";

            fetch("/ProyectoFinalZoo/AnimalServlet", {
                method: metodo,
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(animal)
            })
                    .then(async response => {
                        const text = await response.text();
                        let data;

                        try {
                            data = JSON.parse(text);
                        } catch (e) {
                            throw new Error(text || "Error interno del servidor");
                        }

                        if (!response.ok) {
                            throw new Error(data.error || "Error al procesar el registro del animal");
                        }
                        return data;
                    })
                    .then(data => {
                        let msgError = document.getElementById("mensajeErrorAnimal") || document.getElementById("mensajeError");
                        if (msgError)
                            msgError.innerHTML = "";

                        limpiarFormularioAnimal();
                        buscarAnimales(paginaActual);

                        Swal.fire({
                            icon: "success",
                            title: id ? "Animal Actualizado" : "Animal Registrado",
                            text: data.mensaje || "La operación se completó con éxito.",
                            confirmButtonColor: "#3f5b4b"
                        });
                    })
                    .catch(error => {
                        console.error(error);

                        let msgError = document.getElementById("mensajeErrorAnimal") || document.getElementById("mensajeError");
                        if (msgError) {
                            msgError.innerHTML = `
                    <div style="color:white; background:#d62828; padding:10px; border-radius:8px; margin-bottom:15px;">
                        ${error.message}
                    </div>`;
                        } else {
                            mostrarAlertaError(error.message);
                        }
                    });
        });

// ===============================
// ELIMINAR ANIMAL
// ===============================
function eliminarAnimal(id) {
    Swal.fire({
        title: "¿Desea eliminar este animal?",
        text: "Esta acción no se puede deshacer de los registros del zoológico.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#b05d4d",
        cancelButtonColor: "#3f5b4b",
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (!result.isConfirmed) {
            return;
        }

        fetch(`/ProyectoFinalZoo/AnimalServlet?id=${id}`, {
            method: "DELETE"
        })
                .then(async response => {
                    const texto = await response.text();

                    if (!response.ok) {
                        throw new Error(texto || "No se pudo eliminar el registro del animal.");
                    }

                    try {
                        return JSON.parse(texto);
                    } catch (e) {
                        return {mensaje: texto};
                    }
                })
                .then(data => {
                    Swal.fire({
                        icon: "success",
                        title: "Eliminado",
                        text: data.mensaje || "El animal ha sido removido.",
                        confirmButtonColor: "#3f5b4b"
                    });
                    buscarAnimales(paginaActual);
                })
                .catch(error => {
                    Swal.fire({
                        icon: "error",
                        title: "No se pudo completar",
                        text: error.message,
                        confirmButtonColor: "#b05d4d"
                    });
                });
    });
}

// ===============================
// CALCULAR EDAD DINÁMICA
// ===============================
function calcularEdad(fechaNacimiento) {
    if (!fechaNacimiento)
        return "—";

    const soloFecha = fechaNacimiento.substring(0, 10);
    const partes = soloFecha.split("-");

    const hoy = new Date();
    const fechaHoyCero = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate());
    const nacimiento = new Date(parseInt(partes[0]), parseInt(partes[1]) - 1, parseInt(partes[2]));

    const diferenciaMilisegundos = fechaHoyCero - nacimiento;
    const diasTotales = Math.floor(diferenciaMilisegundos / (1000 * 60 * 60 * 24));

    if (diasTotales < 0)
        return "No nacido aún";
    if (diasTotales === 0)
        return "Recién nacido";
    if (diasTotales === 1)
        return "1 día";
    if (diasTotales < 30)
        return `${diasTotales} días`;

    let años = hoy.getFullYear() - nacimiento.getFullYear();
    let meses = hoy.getMonth() - nacimiento.getMonth();

    if (meses < 0 || (meses === 0 && hoy.getDate() < nacimiento.getDate())) {
        años--;
        meses += 12;
    }

    if (años === 0) {
        if (meses === 0 && hoy.getDate() < nacimiento.getDate()) {
            return `${diasTotales} días`;
        }
        return meses === 1 ? "1 mes" : `${meses} meses`;
    }

    let textoEdad = años === 1 ? "1 año" : `${años} años`;

    if (meses > 0) {
        textoEdad += meses === 1 ? " y 1 mes" : ` y ${meses} meses`;
    }

    return textoEdad;
}

// ===============================
// LIMPIAR FORMULARIO
// ===============================
function limpiarFormularioAnimal() {
    document.getElementById("formAnimal").reset();
    document.getElementById("idAnimal").value = "";

    let btnGuardar = document.getElementById("btnGuardarAnimal") || document.getElementById("btnGuardar");
    if (btnGuardar) {
        btnGuardar.textContent = "Guardar Animal";
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