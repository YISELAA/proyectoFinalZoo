    <%-- 
        Document   : login
        Created on : 15 may 2026, 10:34:29
        Author     : MINED
    --%>

    <%@page contentType="text/html" pageEncoding="UTF-8"%>
    <!DOCTYPE html>
    <html lang="es">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>WILD ZOO MK - Iniciar Sesión</title>

            <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">

            <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;500;700;800&display=swap" rel="stylesheet">

            <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@latest/tabler-icons.min.css">

            <link rel="stylesheet" href="css/login.css">
        </head>

        <body>

            <div class="login-card">

                <div class="login-logo-container">
                    <img src="Imagenes/logo.png" alt="Wild Zoo MK" class="login-logo">
                </div>


                <form id="formLogin">

                    <div class="mb-3">
                        <label class="form-label">Usuario</label>
                        <input type="text"
                               id="usuario"
                               class="form-control"
                               placeholder="Ingrese su usuario"
                               required>
                    </div>

                    <div class="mb-4">
                        <label class="form-label">Contraseña</label>
                        <input type="password"
                               id="password"
                               class="form-control"
                               placeholder="Ingrese su contraseña"
                               required>
                    </div>

                    <button type="submit" class="btn-login-submit">
                        Ingresar
                    </button>

                </form>

                <div id="mensajeError"></div>

                <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
                <script src="${pageContext.request.contextPath}/js/Login.js"></script>


        </body>
    </html> 