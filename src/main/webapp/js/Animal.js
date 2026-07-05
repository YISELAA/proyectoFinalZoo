console.log("JS ANIMALES CARGADO");

let paginaActual = 1;
const size = 5; 
let datosCompletos = []; 


// INICIO
document.addEventListener("DOMContentLoaded", function () {
    buscarAnimales();
    cargarHabitats();
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

    const inputNacimiento = document.getElementById("fechaNacimiento");
    const inputIngreso = document.getElementById("fechaIngreso");

    if (inputNacimiento) inputNacimiento.setAttribute("max", fechaMaxima);
    if (inputIngreso) inputIngreso.setAttribute("max", fechaMaxima);
}



function formatearFecha(fecha) {
    if (!fecha) return "";
    const solo = fecha.substring(0, 10);
    const partes = solo.split("-");
    return `${parseInt(partes[2])}/${parseInt(partes[1])}/${partes[0]}`;
}


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
            datosCompletos = data; 
            
            if (Array.isArray(datosCompletos)) {
                redibujarTablaLocal();
            }
        })
        .catch(error => {
            console.error("Error:", error);
            mostrarAlertaError("No se pudieron cargar los animales.");
        });
}


function mostrarAnimales(lista) {
    if (!Array.isArray(lista)) {
        console.error("Respuesta inválida:", lista);
        return;
    }

    let html = "";
    lista.forEach(a => {
        html += `
            <tr>
                <td>${a.id ?? "—"}</td>
                <td>${a.nombre ?? "—"}</td>
                <td>${a.especie ?? "—"}</td>
                <td>${a.sexo ?? "—"}</td>
                <td>${formatearFecha(a.fechaNacimiento)}</td>
                <td>${calcularEdad(a.fechaNacimiento)}</td>
                <td>${formatearFecha(a.fechaIngreso)}</td>
                <td>${a.habitat ? a.habitat.tipoTerreno : "No asignado"}</td>
                <td class="acciones">
                    <button class="btnEditar" onclick="editarAnimal(${a.id})">
                        <i class="ti ti-edit"></i>
                    </button>
                    <button class="btnEliminar" onclick="eliminarAnimal(${a.id})">
                          <i class="ti ti-trash"></i>
                    </button>
                </td>
            </tr>
        `;
    });
    document.getElementById("tbodyAnimales").innerHTML = html;
}

function cargarHabitats() {
    fetch("/ProyectoFinalZoo/HabitatServlet")
        .then(response => {
            if (!response.ok) throw new Error();
            return response.json();
        })
        .then(data => {
            let combo = document.getElementById("habitat");
            combo.innerHTML = '<option value="">Seleccione hábitat</option>';
            data.forEach(h => {
                combo.innerHTML += `<option value="${h.id}">${h.tipoTerreno}</option>`;
            });
        })
        .catch(error => {
            console.error("Error cargando hábitats:", error);
        });
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

// Corregido el bug de la función "siguiente" que usaba variables indefinidas
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
    
    mostrarAnimales(registrosSegmentados);
    renderPaginacion(datosCompletos.length);
}


function editarAnimal(id) {
    fetch(`/ProyectoFinalZoo/AnimalServlet?id=${id}`)
        .then(response => {
            if (!response.ok) throw new Error("No se pudo obtener la información del animal.");
            return response.json();
        })
        .then(a => {
            document.getElementById("idAnimal").value = a.id;
            document.getElementById("nombreAnimal").value = a.nombre;
            document.getElementById("especie").value = a.especie;
            // 🌟 Carga el valor del sexo en el combo/input correspondiente
            if (document.getElementById("sexo")) {
                document.getElementById("sexo").value = a.sexo ?? "";
            }
            document.getElementById("fechaNacimiento").value = a.fechaNacimiento ? a.fechaNacimiento.substring(0, 10) : "";
            document.getElementById("fechaIngreso").value = a.fechaIngreso ? a.fechaIngreso.substring(0, 10) : "";
            document.getElementById("habitat").value = a.habitat ? a.habitat.id : "";

            let btnGuardar = document.getElementById("btnGuardarAnimal") || document.getElementById("btnGuardar");
            if (btnGuardar) {
                btnGuardar.textContent = "Actualizar Animal";
            }

            window.scrollTo({ top: 0, behavior: "smooth" });
        })
        .catch(error => {
            console.error(error);
            mostrarAlertaError("Error al recuperar los datos del animal.");
        });
}

// GUARDAR O ACTUALIZAR 
document.getElementById("formAnimal")
    .addEventListener("submit", function (event) {
        event.preventDefault();

        let id = document.getElementById("idAnimal").value;
        let idHabitatRaw = document.getElementById("habitat").value;
        let nombre = document.getElementById("nombreAnimal").value.trim();
        let especie = document.getElementById("especie").value.trim();
        let sexoRaw = document.getElementById("sexo") ? document.getElementById("sexo").value : "";
        let fechaNacimiento = document.getElementById("fechaNacimiento").value;
        let fechaIngreso = document.getElementById("fechaIngreso").value;

        // Regex es  para validar texto puro (letras, espacios y letras con tildes/ñ)
        const regexLetras = /^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s]+$/;
        const fechaActual = new Date().toISOString().split('T')[0];

        // VALIDACIONES CLIENTE CON SWEETALERT
        if (nombre.length < 3) {
            Swal.fire({ icon: "warning", title: "Campo inválido", text: "El nombre debe tener mínimo 3 caracteres.", confirmButtonColor: "#b05d4d" });
            return;
        }
        if (!regexLetras.test(nombre)) {
            Swal.fire({ icon: "warning", title: "Formato incorrecto", text: "El nombre solo puede contener letras.", confirmButtonColor: "#b05d4d" });
            return;
        }

        if (especie.length < 3) {
            Swal.fire({ icon: "warning", title: "Campo inválido", text: "La especie debe tener mínimo 3 caracteres.", confirmButtonColor: "#b05d4d" });
            return;
        }
        if (!regexLetras.test(especie)) {
            Swal.fire({ icon: "warning", title: "Formato incorrecto", text: "La especie solo puede contener letras.", confirmButtonColor: "#b05d4d" });
            return;
        }

        // VALIDACIÓN DEL CAMPO SEXO
        if (!sexoRaw) {
            Swal.fire({ icon: "warning", title: "Campo requerido", text: "Por favor, elija el sexo del animal.", confirmButtonColor: "#b05d4d" });
            return;
        }

        if (!fechaNacimiento) {
            Swal.fire({ icon: "warning", title: "Campo requerido", text: "Por favor, elija la fecha de nacimiento.", confirmButtonColor: "#b05d4d" });
            return;
        }
        if (fechaNacimiento > fechaActual) {
            Swal.fire({ icon: "warning", title: "Fecha inválida", text: "¡El animal no puede haber nacido en el futuro!", confirmButtonColor: "#b05d4d" });
            return;
        }

        if (fechaIngreso && fechaIngreso > fechaActual) {
            Swal.fire({ icon: "warning", title: "Fecha inválida", text: "La fecha de ingreso al zoo no puede ser una fecha futura.", confirmButtonColor: "#b05d4d" });
            return;
        }

        if (!idHabitatRaw) {
            Swal.fire({ icon: "warning", title: "Campo requerido", text: "Debe asignar un hábitat válido.", confirmButtonColor: "#b05d4d" });
            return;
        }

        let animal = {
            nombre: nombre,
            especie: especie,
            sexo: sexoRaw, 
            fechaNacimiento: fechaNacimiento,
            fechaIngreso: fechaIngreso,
            habitat: { id: parseInt(idHabitatRaw) }
        };

        if (id) {
            animal.id = parseInt(id);
        }

        let metodo = id ? "PUT" : "POST";

        fetch("/ProyectoFinalZoo/AnimalServlet", {
            method: metodo,
            headers: { "Content-Type": "application/json" },
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
                throw new Error(data.error || "Error al procesar el animal");
            }
            return data;
        })
        .then(data => {
            limpiarFormularioAnimal();
            buscarAnimales(id ? paginaActual : 1); 

            Swal.fire({
                icon: "success",
                title: id ? "Animal Actualizado" : "Animal Registrado",
                text: data.mensaje,
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
        if (!result.isConfirmed) return;

        fetch(`/ProyectoFinalZoo/AnimalServlet?id=${id}`, { method: "DELETE" })
        .then(async response => {
            const texto = await response.text();
            if (!response.ok) throw new Error(texto || "No se pudo eliminar el registro.");
            try { return JSON.parse(texto); } catch (e) { return { mensaje: texto }; }
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


function calcularEdad(fechaNacimiento) {
    if (!fechaNacimiento) return "—";
    
    const soloFecha = fechaNacimiento.substring(0, 10);
    const partes = soloFecha.split("-");
    
    const hoy = new Date();
    const fechaHoyCero = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate());
    const nacimiento = new Date(parseInt(partes[0]), parseInt(partes[1]) - 1, parseInt(partes[2]));
    
    const diferenciaMilisegundos = fechaHoyCero - nacimiento;
    const diasTotales = Math.floor(diferenciaMilisegundos / (1000 * 60 * 60 * 24));

    if (diasTotales < 0) return "No nacido aún";
    if (diasTotales === 0) return "Recién nacido";
    if (diasTotales === 1) return "1 día";
    if (diasTotales < 30) return `${diasTotales} días`;

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