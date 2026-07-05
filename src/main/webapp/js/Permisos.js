/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */



console.log("JS PERMISOS CARGADO");

function cargarPermisosDelRol() {
    let idRol = document.getElementById("idRol").value;
    
    // Desmarcar todos los checkboxes primero
    const checkboxes = document.querySelectorAll('input[name="opcionesMenu"]');
    checkboxes.forEach(cb => cb.checked = false);

    if (!idRol) return;

    fetch(`PermisosServlet?idRol=${idRol}`)
        .then(res => res.json())
        .then(idsAsignados => {
            idsAsignados.forEach(id => {
                let checkbox = document.getElementById(`opc_${id}`);
                if (checkbox) checkbox.checked = true;
            });
        })
        .catch(error => console.error("Error al obtener permisos del rol:", error));
}

document.getElementById("formPermisos").addEventListener("submit", function (e) {
    e.preventDefault();

    let idRolVal = parseInt(document.getElementById("idRol").value);
    
    let opcionesSeleccionadas = [];
    document.querySelectorAll('input[name="opcionesMenu"]:checked').forEach(cb => {
        opcionesSeleccionadas.push(parseInt(cb.value));
    });

    let payload = {
        idRol: idRolVal,
        opciones: opcionesSeleccionadas
    };

    fetch("PermisosServlet", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload)
    })
    .then(async response => {
        const data = await response.json();
        if (!response.ok) throw new Error(data.error || "Error al actualizar accesos");
        return data;
    })
    .then(data => {
        Swal.fire({
            icon: "success",
            title: "Accesos Actualizados",
            text: data.mensaje,
            confirmButtonColor: "#3f5b4b" 
        });
    })
    .catch(error => {
        Swal.fire({
            icon: "warning",
            title: "No se pudo guardar",
            text: error.message,
            confirmButtonColor: "#b05d4d" 
        });
    });
});