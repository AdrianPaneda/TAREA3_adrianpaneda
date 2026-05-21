package com.adrianpaneda.tarea3AD2024base.services;

import org.springframework.stereotype.Service;

import com.adrianpaneda.tarea3AD2024base.modelo.Perfil;
import com.adrianpaneda.tarea3AD2024base.view.FxmlView;

/**
 * Servicio que genera el contenido HTML de la ventana de ayuda contextual.
 * <p>
 * Produce un documento HTML completo con CSS y JavaScript embebidos que
 * incluye una barra lateral de navegación y secciones de ayuda específicas
 * para cada perfil de usuario (administrador, coordinación, artista o
 * invitado). La sección activa al abrirse se determina por la vista desde
 * la que el usuario activó la ayuda.
 * </p>
 * <p>
 * Todos los iconos del HTML se expresan como entidades numéricas HTML
 * (p.ej. {@code &#127968;}) para garantizar una representación correcta
 * independientemente del encoding con que se compile el fichero fuente.
 * </p>
 *
 * @author Adrián Pañeda Hamadi
 * @version 1.1
 * @since 2025-01-01
 * @see com.adrianpaneda.tarea3AD2024base.controller.HelpController
 * @see com.adrianpaneda.tarea3AD2024base.config.HelpContextHolder
 */
@Service
public class HelpContentGenerator {

    // ─────────────────────────────────────────────────────────────────────────
    // Entidades HTML para iconos (evitan problemas de encoding en fuentes Java)
    // ─────────────────────────────────────────────────────────────────────────

    /** Icono de inicio / home (U+1F3E0). */
    private static final String ICO_HOME    = "&#127968;";
    /** Icono de personas / siluetas (U+1F465). */
    private static final String ICO_PEOPLE  = "&#128101;";
    /** Icono de carpa de circo (U+1F3AA). */
    private static final String ICO_CIRCUS  = "&#127914;";
    /** Icono de artes escénicas / máscaras (U+1F3AD). */
    private static final String ICO_MASKS   = "&#127917;";
    /** Icono de tiovivo / carrusel (U+1F3A0). */
    private static final String ICO_SHOW    = "&#127904;";
    /** Icono de advertencia / triángulo (U+26A0). */
    private static final String ICO_WARN    = "&#9888;";
    /** Icono de portapapeles / historial (U+1F4CB). */
    private static final String ICO_LOG     = "&#128203;";
    /** Icono de paleta de artista (U+1F3A8). */
    private static final String ICO_ART     = "&#127912;";
    /** Icono de candado / acceso restringido (U+1F512). */
    private static final String ICO_LOCK    = "&#128274;";
    /** Icono de bombilla / consejo (U+1F4A1). */
    private static final String ICO_TIP     = "&#128161;";
    /** Icono de interrogación / ayuda (U+2753). */
    private static final String ICO_HELP    = "&#10067;";
    /** Icono de círculo rojo / administrador (U+1F534). */
    private static final String ICO_RED     = "&#128308;";
    /** Icono de círculo morado / coordinación (U+1F7E3). */
    private static final String ICO_PURPLE  = "&#128995;";
    /** Icono de círculo azul / artista (U+1F535). */
    private static final String ICO_BLUE    = "&#128309;";
    /** Icono de círculo blanco / invitado (U+26AA). */
    private static final String ICO_WHITE   = "&#9898;";
    /** Icono de lápiz / editar (U+270F). */
    private static final String ICO_EDIT    = "&#9999;";
    /** Icono de papelera / eliminar (U+1F5D1). */
    private static final String ICO_DELETE  = "&#128465;";
    /** Símbolo de aspa / cerrar (U+2715). */
    private static final String ICO_CLOSE   = "&#10005;";

    // ─────────────────────────────────────────────────────────────────────────
    // API pública
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Genera el documento HTML completo para la ventana de ayuda.
     *
     * @param perfil      el perfil del usuario activo, o {@code null} para invitado
     * @param currentView la vista desde la que se abrió la ayuda; determina la
     *                    sección seleccionada por defecto
     * @return una cadena con el HTML completo listo para cargar en un
     *         {@link javafx.scene.web.WebView}
     */
    public String generateHtml(Perfil perfil, FxmlView currentView) {
        String initialSection = getSectionId(currentView);
        String navItems       = buildNavItems(perfil, initialSection);
        String sections       = buildSections(perfil);
        String perfilNombre   = getPerfilNombre(perfil);
        String perfilColor    = getPerfilColor(perfil);

        return buildDocument(navItems, sections, initialSection, perfilNombre, perfilColor);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mapeo vista -> sección
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devuelve el identificador HTML de la sección que corresponde a la vista
     * indicada.
     *
     * @param view la vista activa al abrir la ayuda, o {@code null}
     * @return el id de la sección HTML correspondiente
     */
    private String getSectionId(FxmlView view) {
        if (view == null) return "bienvenida";
        return switch (view) {
            case GESTION_PERSONAS                   -> "personas";
            case GESTIONAR_ESPECTACULOS             -> "gestion-espectaculos";
            case GESTIONAR_NUMEROS                  -> "numeros";
            case ESPECTACULOS, DETALLE_ESPECTACULO  -> "ver-espectaculos";
            case FICHA_ARTISTA                      -> "ficha-artista";
            case HISTORIAL                          -> "historial";
            case INCIDENCIAS, REGISTRAR_INCIDENCIA  -> "incidencias";
            default                                 -> "bienvenida";
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Construcción de la barra lateral de navegación
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Construye el bloque HTML de los ítems de navegación lateral según el perfil.
     *
     * @param perfil        el perfil del usuario
     * @param activeSection el id de la sección activa por defecto
     * @return el HTML de los elementos {@code <div class="nav-item">}
     */
    private String buildNavItems(Perfil perfil, String activeSection) {
        StringBuilder sb = new StringBuilder();

        // Bienvenida siempre visible
        sb.append(navItem("bienvenida", ICO_HOME, "Inicio", activeSection));

        if (perfil == Perfil.admin) {
            sb.append(navItem("personas",             ICO_PEOPLE,  "Gestión de Personas",     activeSection));
            sb.append(navItem("gestion-espectaculos", ICO_CIRCUS,  "Gestión de Espectáculos", activeSection));
            sb.append(navItem("numeros",              ICO_MASKS,   "Gestión de Números",      activeSection));
            sb.append(navItem("ver-espectaculos",     ICO_SHOW,    "Ver Espectáculos",        activeSection));
            sb.append(navItem("incidencias",          ICO_WARN,    "Incidencias",             activeSection));
            sb.append(navItem("historial",            ICO_LOG,     "Historial",               activeSection));
        } else if (perfil == Perfil.coordinacion) {
            sb.append(navItem("gestion-espectaculos", ICO_CIRCUS,  "Gestión de Espectáculos", activeSection));
            sb.append(navItem("numeros",              ICO_MASKS,   "Gestión de Números",      activeSection));
            sb.append(navItem("ver-espectaculos",     ICO_SHOW,    "Ver Espectáculos",        activeSection));
            sb.append(navItem("incidencias",          ICO_WARN,    "Incidencias",             activeSection));
        } else if (perfil == Perfil.artista) {
            sb.append(navItem("ficha-artista",    ICO_ART,  "Mi Ficha de Artista", activeSection));
            sb.append(navItem("ver-espectaculos", ICO_SHOW, "Ver Espectáculos",    activeSection));
            sb.append(navItem("incidencias",      ICO_WARN, "Incidencias",         activeSection));
        } else {
            // Invitado / sin sesión
            sb.append(navItem("ver-espectaculos", ICO_SHOW, "Ver Espectáculos", activeSection));
        }

        return sb.toString();
    }

    /**
     * Genera el HTML de un ítem de navegación lateral.
     *
     * @param sectionId     el id de la sección a la que navega este ítem
     * @param icon          la entidad HTML del icono a mostrar
     * @param label         el texto del ítem
     * @param activeSection el id de la sección actualmente activa
     * @return el HTML del ítem
     */
    private String navItem(String sectionId, String icon, String label, String activeSection) {
        String activeClass = sectionId.equals(activeSection) ? " active" : "";
        return "<div class=\"nav-item" + activeClass + "\" data-section=\"" + sectionId
                + "\" onclick=\"showSection('" + sectionId + "')\">"
                + "<span class=\"nav-icon\">" + icon + "</span>"
                + "<span>" + label + "</span>"
                + "</div>\n";
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Construcción de secciones de contenido
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Construye todas las secciones de contenido HTML visibles para el perfil dado.
     *
     * @param perfil el perfil del usuario activo
     * @return el HTML con todas las secciones {@code <div class="section">}
     */
    private String buildSections(Perfil perfil) {
        StringBuilder sb = new StringBuilder();

        sb.append(sectionBienvenida(perfil));

        if (perfil == Perfil.admin) {
            sb.append(sectionPersonas());
            sb.append(sectionGestionEspectaculos(perfil));
            sb.append(sectionNumeros());
            sb.append(sectionVerEspectaculos());
            sb.append(sectionIncidencias(perfil));
            sb.append(sectionHistorial());
        } else if (perfil == Perfil.coordinacion) {
            sb.append(sectionGestionEspectaculos(perfil));
            sb.append(sectionNumeros());
            sb.append(sectionVerEspectaculos());
            sb.append(sectionIncidencias(perfil));
        } else if (perfil == Perfil.artista) {
            sb.append(sectionFichaArtista());
            sb.append(sectionVerEspectaculos());
            sb.append(sectionIncidencias(perfil));
        } else {
            sb.append(sectionVerEspectaculos());
        }

        return sb.toString();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Secciones individuales
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Genera la sección de bienvenida adaptada al perfil del usuario.
     *
     * @param perfil el perfil activo
     * @return HTML de la sección bienvenida
     */
    private String sectionBienvenida(Perfil perfil) {
        String nombre        = getPerfilNombre(perfil);
        String pantallasList = buildPantallasLista(perfil);

        return section("bienvenida",
            ICO_HOME, "Bienvenido al Sistema de Ayuda",
            card("Sobre esta ayuda",
                "<p>Esta ventana te gu&iacute;a en el uso de la aplicaci&oacute;n de gesti&oacute;n del circo. "
                + "El contenido que ves est&aacute; adaptado a tu perfil: <strong>" + nombre + "</strong>.</p>"
                + "<p>Selecciona una secci&oacute;n en el panel izquierdo para ver la ayuda correspondiente. "
                + "La secci&oacute;n activa al abrir esta ventana corresponde a la pantalla en la que estabas.</p>")
            + card("Pantallas disponibles para tu perfil",
                "<p>Con el perfil <strong>" + nombre + "</strong> tienes acceso a las siguientes pantallas:</p>"
                + "<ul>" + pantallasList + "</ul>")
            + tip("Puedes mantener esta ventana abierta mientras usas la aplicaci&oacute;n. "
                + "Ci&eacute;rrala con el bot&oacute;n " + ICO_CLOSE + " de la barra de t&iacute;tulo cuando ya no la necesites.")
        );
    }

    /**
     * Genera la sección de ayuda para Gestión de Personas (solo admin).
     *
     * @return HTML de la sección personas
     */
    private String sectionPersonas() {
        return section("personas",
            ICO_PEOPLE, "Gesti&oacute;n de Personas",
            badge("Solo Administrador", "admin")
            + card("&iquest;Qu&eacute; puedo hacer en esta pantalla?",
                "<p>La pantalla de Gesti&oacute;n de Personas te permite administrar todos los usuarios del sistema:</p>"
                + "<ul>"
                + "<li>Consultar la lista completa de artistas y coordinaciones registrados.</li>"
                + "<li>Registrar nuevos artistas con sus datos y credenciales de acceso.</li>"
                + "<li>Registrar nuevas coordinaciones, opcionalmente marc&aacute;ndolas como senior.</li>"
                + "<li>Editar los datos de cualquier persona existente.</li>"
                + "<li>Eliminar personas del sistema (se eliminan tambi&eacute;n sus credenciales).</li>"
                + "</ul>")
            + card("Registrar un artista",
                "<p>Haz clic en <strong>REGISTRAR ARTISTA</strong> para mostrar el formulario:</p>"
                + "<ul>"
                + "<li><strong>Nombre:</strong> nombre completo del artista.</li>"
                + "<li><strong>Email:</strong> debe ser &uacute;nico en el sistema.</li>"
                + "<li><strong>Nacionalidad:</strong> escribe para buscar en la lista de pa&iacute;ses.</li>"
                + "<li><strong>Apodo:</strong> nombre art&iacute;stico, opcional.</li>"
                + "<li><strong>Especialidades:</strong> selecciona al menos una (Acrobacia, Humor, Magia, Equilibrismo o Malabarismo).</li>"
                + "<li><strong>Usuario:</strong> solo letras sin espacios, m&iacute;nimo 3 caracteres. Se convertir&aacute; a min&uacute;sculas.</li>"
                + "<li><strong>Contrase&ntilde;a:</strong> sin espacios, m&iacute;nimo 3 caracteres.</li>"
                + "</ul>")
            + card("Registrar una coordinaci&oacute;n",
                "<p>Haz clic en <strong>REGISTRAR COORDINACI&Oacute;N</strong> para mostrar el formulario:</p>"
                + "<ul>"
                + "<li>Rellena nombre, email y nacionalidad igual que para un artista.</li>"
                + "<li>Marca <strong>Es senior</strong> si tiene rango senior y selecciona la fecha desde que lo es.</li>"
                + "<li>Crea las credenciales de acceso con las mismas reglas que para artistas.</li>"
                + "</ul>")
            + card("Editar y eliminar",
                "<p>En la columna de acciones de la tabla encontrar&aacute;s el bot&oacute;n de editar (" + ICO_EDIT + ") para cada persona. "
                + "Al editarla, el formulario se carga con sus datos actuales. Las credenciales no se modifican al editar.</p>"
                + "<p>Para eliminar, usa el bot&oacute;n de eliminar (" + ICO_DELETE + ") de la fila correspondiente. "
                + "Se pedir&aacute; confirmaci&oacute;n antes de borrar.</p>")
            + tip("Desde esta pantalla tambi&eacute;n puedes acceder a Gesti&oacute;n de Espect&aacute;culos, "
                + "Ver Espect&aacute;culos, Historial e Incidencias usando los botones superiores.")
        );
    }

    /**
     * Genera la sección de ayuda para Gestión de Espectáculos.
     *
     * @param perfil el perfil del usuario (admin o coordinación)
     * @return HTML de la sección gestión espectáculos
     */
    private String sectionGestionEspectaculos(Perfil perfil) {
        String accesoBadge = perfil == Perfil.admin
            ? badge("Administrador y Coordinaci&oacute;n", "all")
            : badge("Coordinaci&oacute;n", "coord");

        String coordinadorExtra = perfil == Perfil.admin
            ? "<li><strong>Coordinador:</strong> (solo admin) selecciona qu&eacute; coordinaci&oacute;n gestiona el espect&aacute;culo.</li>"
            : "";

        return section("gestion-espectaculos",
            ICO_CIRCUS, "Gesti&oacute;n de Espect&aacute;culos",
            accesoBadge
            + card("&iquest;Qu&eacute; puedo hacer en esta pantalla?",
                "<p>La pantalla de Gesti&oacute;n de Espect&aacute;culos permite administrar los espect&aacute;culos del circo:</p>"
                + "<ul>"
                + "<li>Ver la lista de todos los espect&aacute;culos registrados.</li>"
                + "<li>Crear nuevos espect&aacute;culos con sus fechas.</li>"
                + "<li>Editar espect&aacute;culos existentes.</li>"
                + "<li>Gestionar los n&uacute;meros de cada espect&aacute;culo.</li>"
                + "</ul>")
            + card("Crear un espect&aacute;culo",
                "<p>Haz clic en <strong>CREAR ESPECT&Aacute;CULO</strong> y rellena el formulario:</p>"
                + "<ul>"
                + "<li><strong>Nombre:</strong> m&aacute;ximo 25 caracteres.</li>"
                + "<li><strong>Fecha de inicio:</strong> fecha de comienzo del espect&aacute;culo.</li>"
                + "<li><strong>Fecha de fin:</strong> debe ser igual o posterior a la de inicio.</li>"
                + coordinadorExtra
                + "</ul>")
            + card("Editar un espect&aacute;culo",
                "<p>Haz clic en el bot&oacute;n <strong>" + ICO_EDIT + " Editar</strong> en la columna de acciones de la tabla "
                + "para cargar los datos del espect&aacute;culo en el formulario y modificarlos.</p>")
            + card("Gestionar n&uacute;meros",
                "<p>Haz clic en el bot&oacute;n <strong>" + ICO_MASKS + " N&uacute;meros</strong> en la columna de acciones para ir "
                + "a la pantalla de Gesti&oacute;n de N&uacute;meros de ese espect&aacute;culo.</p>")
            + tip("Usa el bot&oacute;n <strong>VER ESPECT&Aacute;CULOS</strong> para ver la vista p&uacute;blica del listado "
                + "de espect&aacute;culos tal y como la ven los invitados.")
        );
    }

    /**
     * Genera la sección de ayuda para Gestión de Números.
     *
     * @return HTML de la sección números
     */
    private String sectionNumeros() {
        return section("numeros",
            ICO_MASKS, "Gesti&oacute;n de N&uacute;meros",
            badge("Administrador y Coordinaci&oacute;n", "all")
            + card("&iquest;Qu&eacute; puedo hacer en esta pantalla?",
                "<p>La pantalla de Gesti&oacute;n de N&uacute;meros permite administrar los n&uacute;meros art&iacute;sticos de un espect&aacute;culo:</p>"
                + "<ul>"
                + "<li>Ver todos los n&uacute;meros del espect&aacute;culo seleccionado.</li>"
                + "<li>A&ntilde;adir nuevos n&uacute;meros con nombre, duraci&oacute;n y artistas participantes.</li>"
                + "<li>Editar n&uacute;meros existentes.</li>"
                + "<li>Ver los artistas disponibles para asignar a cada n&uacute;mero.</li>"
                + "</ul>")
            + card("A&ntilde;adir un n&uacute;mero",
                "<p>Haz clic en <strong>A&Ntilde;ADIR N&Uacute;MERO</strong> y rellena el formulario:</p>"
                + "<ul>"
                + "<li><strong>Nombre:</strong> nombre del n&uacute;mero art&iacute;stico.</li>"
                + "<li><strong>Duraci&oacute;n:</strong> usa el selector (+/-) para establecer los minutos.</li>"
                + "<li><strong>Artistas:</strong> selecciona en la tabla derecha los artistas que participan "
                + "en este n&uacute;mero. Puedes seleccionar varios a la vez (Ctrl + clic o Shift + clic).</li>"
                + "</ul>")
            + card("Editar un n&uacute;mero",
                "<p>Haz clic en <strong>" + ICO_EDIT + "</strong> en la columna de acciones para cargar los datos del "
                + "n&uacute;mero y modificarlos. Los artistas ya asignados aparecer&aacute;n marcados en la tabla.</p>")
            + tip("Vuelve a la gesti&oacute;n de espect&aacute;culos con el bot&oacute;n <strong>VOLVER</strong> de la parte inferior.")
        );
    }

    /**
     * Genera la sección de ayuda para la vista pública de espectáculos.
     *
     * @return HTML de la sección ver espectáculos
     */
    private String sectionVerEspectaculos() {
        return section("ver-espectaculos",
            ICO_SHOW, "Ver Espect&aacute;culos",
            badge("Disponible para todos", "all")
            + card("&iquest;Qu&eacute; puedo ver en esta pantalla?",
                "<p>Esta pantalla muestra el cat&aacute;logo p&uacute;blico de todos los espect&aacute;culos del circo:</p>"
                + "<ul>"
                + "<li>ID, nombre, fecha de inicio y fecha de finalizaci&oacute;n de cada espect&aacute;culo.</li>"
                + "<li>Puedes hacer clic en cualquier fila para ver el detalle del espect&aacute;culo.</li>"
                + "</ul>")
            + card("Detalle de un espect&aacute;culo",
                "<p>Al seleccionar un espect&aacute;culo, se abre la pantalla de detalle donde puedes ver:</p>"
                + "<ul>"
                + "<li>Informaci&oacute;n completa del espect&aacute;culo (nombre, fechas, coordinador).</li>"
                + "<li>Lista de todos los n&uacute;meros que forman el espect&aacute;culo, con su nombre y duraci&oacute;n.</li>"
                + "</ul>")
            + tip("Esta pantalla es accesible tambi&eacute;n sin iniciar sesi&oacute;n, pulsando "
                + "<strong>Ver espect&aacute;culos como invitado</strong> en la pantalla de login.")
        );
    }

    /**
     * Genera la sección de ayuda para la ficha de artista.
     *
     * @return HTML de la sección ficha artista
     */
    private String sectionFichaArtista() {
        return section("ficha-artista",
            ICO_ART, "Mi Ficha de Artista",
            badge("Solo Artista", "coord")
            + card("&iquest;Qu&eacute; puedo ver en esta pantalla?",
                "<p>Tu ficha personal muestra toda tu informaci&oacute;n en el circo:</p>"
                + "<ul>"
                + "<li><strong>Datos personales:</strong> nombre, email, nacionalidad, apodo y especialidades.</li>"
                + "<li><strong>Trayectoria:</strong> tabla con todos los espect&aacute;culos y n&uacute;meros en los que has participado.</li>"
                + "</ul>")
            + card("Acciones disponibles",
                "<ul>"
                + "<li><strong>VER ESPECT&Aacute;CULOS:</strong> accede al cat&aacute;logo p&uacute;blico de espect&aacute;culos del circo.</li>"
                + "<li><strong>INCIDENCIAS:</strong> accede a la pantalla de incidencias para registrar o consultar.</li>"
                + "<li><strong>CERRAR SESI&Oacute;N:</strong> cierra tu sesi&oacute;n y vuelve al login.</li>"
                + "</ul>")
            + tip("Si no ves tu trayectoria actualizada, es porque el administrador o la coordinaci&oacute;n a&uacute;n no "
                + "te ha asignado a ning&uacute;n n&uacute;mero de espect&aacute;culo.")
        );
    }

    /**
     * Genera la sección de ayuda para incidencias, adaptada al perfil.
     *
     * @param perfil el perfil del usuario (determina si puede resolver)
     * @return HTML de la sección incidencias
     */
    private String sectionIncidencias(Perfil perfil) {
        boolean puedeResolver = perfil == Perfil.admin || perfil == Perfil.coordinacion;

        String resolucionCard = puedeResolver
            ? card("Resolver una incidencia",
                "<p>Cuando una incidencia est&aacute; pendiente, aparece el bot&oacute;n <strong>RESOLVER INCIDENCIA</strong> "
                + "al desplegar su card. Para resolver:</p>"
                + "<ul>"
                + "<li>Haz clic en <strong>RESOLVER INCIDENCIA</strong>. Se abre el panel de resoluci&oacute;n.</li>"
                + "<li>Describe las <strong>acciones realizadas</strong> para solucionar el problema.</li>"
                + "<li>Haz clic en <strong>CONFIRMAR RESOLUCI&Oacute;N</strong>.</li>"
                + "</ul>"
                + "<p>La incidencia quedar&aacute; marcada como resuelta y no podr&aacute; volver a resolverse.</p>")
            : "";

        return section("incidencias",
            ICO_WARN, "Incidencias",
            badge("Disponible para todos los usuarios autenticados", "all")
            + card("&iquest;Qu&eacute; puedo hacer en esta pantalla?",
                "<p>La pantalla de incidencias permite gestionar los problemas del circo:</p>"
                + "<ul>"
                + "<li>Ver todas las incidencias registradas (t&eacute;cnicas, art&iacute;sticas y organizativas).</li>"
                + "<li>Filtrar por tipo, estado, espect&aacute;culo, n&uacute;mero o rango de fechas.</li>"
                + "<li>Registrar una nueva incidencia.</li>"
                + (puedeResolver ? "<li>Resolver incidencias pendientes.</li>" : "")
                + "</ul>")
            + card("Registrar una nueva incidencia",
                "<p>Haz clic en <strong>+ REGISTRAR INCIDENCIA</strong> y rellena el formulario:</p>"
                + "<ul>"
                + "<li><strong>Tipo (*obligatorio):</strong> T&Eacute;CNICA, ART&Iacute;STICA u ORGANIZATIVA.</li>"
                + "<li><strong>Descripci&oacute;n (*obligatorio):</strong> m&aacute;ximo 1000 caracteres.</li>"
                + "<li><strong>Espect&aacute;culo (opcional):</strong> si la incidencia est&aacute; vinculada a un espect&aacute;culo.</li>"
                + "<li><strong>N&uacute;mero (opcional):</strong> disponible al seleccionar un espect&aacute;culo.</li>"
                + "</ul>")
            + card("Consultar y filtrar incidencias",
                "<p>Usa el panel de filtros de la izquierda para acotar los resultados:</p>"
                + "<ul>"
                + "<li><strong>Tipo:</strong> filtra por categor&iacute;a (T&eacute;cnica, Art&iacute;stica, Organizativa).</li>"
                + "<li><strong>Estado:</strong> Pendiente o Resuelta.</li>"
                + "<li><strong>Espect&aacute;culo / N&uacute;mero:</strong> filtra por espect&aacute;culo y n&uacute;mero espec&iacute;ficos.</li>"
                + "<li><strong>Rango de fechas:</strong> entre dos fechas.</li>"
                + "</ul>"
                + "<p>Haz clic en <strong>FILTRAR</strong> para aplicar y en <strong>LIMPIAR</strong> para restablecer.</p>"
                + "<p>Cada incidencia aparece como una card. Haz clic en ella para desplegarla y ver los detalles.</p>")
            + resolucionCard
            + warning("Las incidencias no se pueden eliminar. Una vez registradas quedan en el sistema "
                + "hasta que se resuelvan.")
        );
    }

    /**
     * Genera la sección de ayuda para el historial de operaciones (solo admin).
     *
     * @return HTML de la sección historial
     */
    private String sectionHistorial() {
        return section("historial",
            ICO_LOG, "Historial de Operaciones",
            badge("Solo Administrador", "admin")
            + card("&iquest;Qu&eacute; puedo ver en esta pantalla?",
                "<p>El historial muestra un registro completo de todas las operaciones de alta, modificaci&oacute;n "
                + "y borrado realizadas en el sistema, almacenado en la base de datos DB4O.</p>"
                + "<p>Cada entrada registra:</p>"
                + "<ul>"
                + "<li>ID de la operaci&oacute;n.</li>"
                + "<li>Fecha y hora exacta.</li>"
                + "<li>Usuario que realiz&oacute; la operaci&oacute;n.</li>"
                + "<li>Tipo de operaci&oacute;n: <strong>NUEVO</strong>, <strong>ACTUALIZACI&Oacute;N</strong> o <strong>BORRADO</strong>.</li>"
                + "<li>Resumen descriptivo de la operaci&oacute;n realizada.</li>"
                + "</ul>")
            + card("Filtrar el historial",
                "<p>Usa los campos de filtro para buscar operaciones concretas:</p>"
                + "<ul>"
                + "<li><strong>Usuario (obligatorio):</strong> introduce el nombre de usuario para buscar sus operaciones.</li>"
                + "<li><strong>Tipos de operaci&oacute;n:</strong> marca uno o varios tipos (Nuevo, Actualizaci&oacute;n, Borrado).</li>"
                + "<li><strong>Desde / Hasta:</strong> rango de fechas de las operaciones.</li>"
                + "</ul>"
                + "<p>Haz clic en <strong>BUSCAR</strong> para aplicar los filtros y en <strong>LIMPIAR</strong> para reiniciarlos.</p>")
            + tip("El historial es de solo lectura. No es posible modificar ni eliminar ninguna entrada del historial.")
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers HTML
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Envuelve contenido en una sección HTML con id, icono y título.
     *
     * @param id      el identificador de la sección (atributo id del div)
     * @param icon    la entidad HTML del icono del título
     * @param title   el título de la sección (puede contener entidades HTML)
     * @param content el HTML del contenido de la sección
     * @return el bloque HTML completo de la sección
     */
    private String section(String id, String icon, String title, String content) {
        return "<div class=\"section\" id=\"" + id + "\">\n"
                + "  <div class=\"section-title\">" + icon + " " + title + "</div>\n"
                + "  <div class=\"section-divider\"></div>\n"
                + content
                + "</div>\n";
    }

    /**
     * Envuelve contenido en una tarjeta de ayuda con título.
     *
     * @param title   el título de la tarjeta (puede contener entidades HTML)
     * @param content el HTML del contenido interior
     * @return el bloque HTML de la tarjeta
     */
    private String card(String title, String content) {
        return "<div class=\"help-card\">\n"
                + "  <h3>" + title + "</h3>\n"
                + content + "\n"
                + "</div>\n";
    }

    /**
     * Genera un bloque de consejo informativo con estilo azul.
     *
     * @param text el texto del consejo (puede contener entidades HTML)
     * @return el HTML del bloque tip
     */
    private String tip(String text) {
        return "<div class=\"tip\"><p>" + ICO_TIP + " <strong>Consejo:</strong> " + text + "</p></div>\n";
    }

    /**
     * Genera un bloque de aviso con estilo amarillo.
     *
     * @param text el texto del aviso (puede contener entidades HTML)
     * @return el HTML del bloque warning
     */
    private String warning(String text) {
        return "<div class=\"warning\"><p>" + ICO_WARN + " <strong>Aviso:</strong> " + text + "</p></div>\n";
    }

    /**
     * Genera un badge (etiqueta) con el texto y clase de color indicados.
     *
     * @param text      el texto del badge (puede contener entidades HTML)
     * @param colorType la clase CSS del color: {@code "admin"}, {@code "coord"} o {@code "all"}
     * @return el HTML del badge
     */
    private String badge(String text, String colorType) {
        return "<div class=\"access-badge badge-" + colorType + "\">"
                + ICO_LOCK + " Acceso: " + text + "</div>\n";
    }

    /**
     * Construye la lista HTML de pantallas disponibles para el perfil dado.
     *
     * @param perfil el perfil del usuario
     * @return el HTML de los elementos {@code <li>} de la lista
     */
    private String buildPantallasLista(Perfil perfil) {
        if (perfil == Perfil.admin) {
            return "<li>" + ICO_PEOPLE  + " Gesti&oacute;n de Personas (crear, editar y eliminar artistas y coordinaciones)</li>"
                 + "<li>" + ICO_CIRCUS  + " Gesti&oacute;n de Espect&aacute;culos (crear, editar y asignar coordinador)</li>"
                 + "<li>" + ICO_MASKS   + " Gesti&oacute;n de N&uacute;meros (a&ntilde;adir y editar n&uacute;meros con artistas)</li>"
                 + "<li>" + ICO_SHOW    + " Ver Espect&aacute;culos (vista p&uacute;blica del cat&aacute;logo)</li>"
                 + "<li>" + ICO_WARN    + " Incidencias (registrar, consultar y resolver)</li>"
                 + "<li>" + ICO_LOG     + " Historial de Operaciones (auditor&iacute;a del sistema)</li>";
        } else if (perfil == Perfil.coordinacion) {
            return "<li>" + ICO_CIRCUS  + " Gesti&oacute;n de Espect&aacute;culos (crear y editar espect&aacute;culos)</li>"
                 + "<li>" + ICO_MASKS   + " Gesti&oacute;n de N&uacute;meros (a&ntilde;adir y editar n&uacute;meros con artistas)</li>"
                 + "<li>" + ICO_SHOW    + " Ver Espect&aacute;culos (vista p&uacute;blica del cat&aacute;logo)</li>"
                 + "<li>" + ICO_WARN    + " Incidencias (registrar, consultar y resolver)</li>";
        } else if (perfil == Perfil.artista) {
            return "<li>" + ICO_ART     + " Mi Ficha de Artista (datos personales y trayectoria)</li>"
                 + "<li>" + ICO_SHOW    + " Ver Espect&aacute;culos (vista p&uacute;blica del cat&aacute;logo)</li>"
                 + "<li>" + ICO_WARN    + " Incidencias (registrar y consultar)</li>";
        } else {
            return "<li>" + ICO_SHOW    + " Ver Espect&aacute;culos (vista p&uacute;blica del cat&aacute;logo)</li>";
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Utilidades de perfil
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Devuelve el nombre legible del perfil para mostrar en la interfaz.
     *
     * @param perfil el perfil del usuario, o {@code null} para invitado
     * @return la cadena de texto del nombre del perfil
     */
    private String getPerfilNombre(Perfil perfil) {
        if (perfil == null) return "Invitado";
        return switch (perfil) {
            case admin        -> "Administrador";
            case coordinacion -> "Coordinaci&oacute;n";
            case artista      -> "Artista";
        };
    }

    /**
     * Devuelve el color hexadecimal asociado al perfil para el badge lateral.
     *
     * @param perfil el perfil del usuario, o {@code null} para invitado
     * @return el código de color hexadecimal
     */
    private String getPerfilColor(Perfil perfil) {
        if (perfil == null) return "#64748b";
        return switch (perfil) {
            case admin        -> "#dc2626";
            case coordinacion -> "#7c3aed";
            case artista      -> "#0891b2";
        };
    }

    /**
     * Devuelve la entidad HTML del icono de color asociado al nombre del perfil.
     *
     * @param perfilNombre el nombre del perfil (resultado de {@link #getPerfilNombre})
     * @return la entidad HTML del icono correspondiente
     */
    private String getPerfilEmoji(String perfilNombre) {
        return switch (perfilNombre) {
            case "Administrador"       -> ICO_RED;
            case "Coordinaci&oacute;n" -> ICO_PURPLE;
            case "Artista"             -> ICO_BLUE;
            default                    -> ICO_WHITE;
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Documento HTML completo
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Ensambla el documento HTML completo con la estructura, CSS, JavaScript,
     * navegación y secciones de contenido.
     *
     * @param navItems       el HTML de los ítems de navegación lateral
     * @param sections       el HTML de las secciones de contenido
     * @param initialSection el id de la sección a mostrar inicialmente
     * @param perfilNombre   el nombre legible del perfil del usuario
     * @param perfilColor    el color hexadecimal del badge de perfil
     * @return el documento HTML completo
     */
    private String buildDocument(String navItems, String sections,
                                 String initialSection, String perfilNombre, String perfilColor) {
        return "<!DOCTYPE html>\n"
            + "<html lang=\"es\">\n"
            + "<head>\n"
            + "  <meta charset=\"UTF-8\">\n"
            + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
            + "  <title>Ayuda</title>\n"
            + buildCss(perfilColor)
            + "</head>\n"
            + "<body>\n"

            // Sidebar
            + "<nav class=\"sidebar\">\n"
            + "  <div class=\"sidebar-header\">\n"
            + "    <div class=\"sidebar-logo\">" + ICO_HELP + "</div>\n"
            + "    <h2>Sistema de Ayuda</h2>\n"
            + "    <p>Circo Management System</p>\n"
            + "    <span class=\"profile-badge\">" + getPerfilEmoji(perfilNombre) + " " + perfilNombre + "</span>\n"
            + "  </div>\n"
            + "  <div class=\"nav-list\">\n"
            + navItems
            + "  </div>\n"
            + "</nav>\n"

            // Contenido principal
            + "<main class=\"content\">\n"
            + sections
            + "</main>\n"

            // JavaScript
            + "<script>\n"
            + "  function showSection(id) {\n"
            + "    document.querySelectorAll('.section').forEach(s => s.classList.remove('active'));\n"
            + "    document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));\n"
            + "    var sec = document.getElementById(id);\n"
            + "    if (sec) sec.classList.add('active');\n"
            + "    var nav = document.querySelector('[data-section=\"' + id + '\"]');\n"
            + "    if (nav) nav.classList.add('active');\n"
            + "    window.scrollTo(0, 0);\n"
            + "  }\n"
            + "  showSection('" + initialSection + "');\n"
            + "</script>\n"
            + "</body>\n"
            + "</html>\n";
    }

    /**
     * Genera el bloque {@code <style>} con todo el CSS de la ventana de ayuda.
     *
     * @param perfilColor el color hexadecimal del perfil activo, usado en el badge lateral
     * @return el bloque HTML {@code <style>...</style>}
     */
    private String buildCss(String perfilColor) {
        return "<style>\n"
            + "  * { box-sizing: border-box; margin: 0; padding: 0; }\n"
            + "  html, body { height: 100%; font-family: Arial, Helvetica, sans-serif; overflow: hidden; }\n"
            + "  body { display: flex; height: 100vh; background: #f1f5f9; }\n"

            // Sidebar
            + "  .sidebar {\n"
            + "    width: 250px; min-width: 250px;\n"
            + "    background: linear-gradient(180deg, #1e3a8a 0%, #1e40af 60%, #1d4ed8 100%);\n"
            + "    color: white; display: flex; flex-direction: column;\n"
            + "    box-shadow: 4px 0 20px rgba(0,0,0,0.25);\n"
            + "  }\n"
            + "  .sidebar-header {\n"
            + "    padding: 28px 20px 20px;\n"
            + "    border-bottom: 1px solid rgba(255,255,255,0.15);\n"
            + "  }\n"
            + "  .sidebar-logo { font-size: 32px; margin-bottom: 8px; }\n"
            + "  .sidebar-header h2 { font-size: 17px; font-weight: 700; letter-spacing: 0.3px; }\n"
            + "  .sidebar-header p  { font-size: 11px; opacity: 0.65; margin-top: 2px; }\n"
            + "  .profile-badge {\n"
            + "    display: inline-block;\n"
            + "    background: " + perfilColor + ";\n"
            + "    border-radius: 20px;\n"
            + "    padding: 5px 12px;\n"
            + "    font-size: 12px;\n"
            + "    font-weight: 700;\n"
            + "    margin-top: 10px;\n"
            + "    letter-spacing: 0.2px;\n"
            + "  }\n"
            + "  .nav-list { flex: 1; overflow-y: auto; padding: 12px 0; }\n"
            + "  .nav-item {\n"
            + "    display: flex; align-items: center; gap: 12px;\n"
            + "    padding: 13px 22px;\n"
            + "    cursor: pointer;\n"
            + "    font-size: 13.5px; font-weight: 500;\n"
            + "    transition: background 0.15s, border-right 0.15s;\n"
            + "    border-right: 3px solid transparent;\n"
            + "    user-select: none;\n"
            + "  }\n"
            + "  .nav-item:hover  { background: rgba(255,255,255,0.12); }\n"
            + "  .nav-item.active { background: rgba(255,255,255,0.2); border-right-color: #93c5fd; }\n"
            + "  .nav-icon { font-size: 17px; width: 22px; text-align: center; flex-shrink: 0; }\n"

            // Main content
            + "  .content { flex: 1; overflow-y: auto; padding: 32px 36px; }\n"
            + "  .section { display: none; }\n"
            + "  .section.active { display: block; }\n"
            + "  .section-title {\n"
            + "    font-size: 22px; font-weight: 700; color: #1e293b;\n"
            + "    display: flex; align-items: center; gap: 10px;\n"
            + "    margin-bottom: 6px;\n"
            + "  }\n"
            + "  .section-divider {\n"
            + "    height: 3px;\n"
            + "    background: linear-gradient(90deg, #2563eb, #60a5fa, transparent);\n"
            + "    border-radius: 2px;\n"
            + "    margin-bottom: 22px;\n"
            + "  }\n"

            // Cards
            + "  .help-card {\n"
            + "    background: white; border-radius: 10px; padding: 20px 22px;\n"
            + "    margin-bottom: 14px;\n"
            + "    border: 1px solid #e2e8f0;\n"
            + "    box-shadow: 0 1px 6px rgba(0,0,0,0.06);\n"
            + "  }\n"
            + "  .help-card h3 {\n"
            + "    font-size: 14.5px; font-weight: 700; color: #1d4ed8;\n"
            + "    margin-bottom: 10px; border-bottom: 1px solid #e0e7ff;\n"
            + "    padding-bottom: 7px;\n"
            + "  }\n"
            + "  .help-card p  { font-size: 13.5px; color: #334155; line-height: 1.65; margin-bottom: 8px; }\n"
            + "  .help-card p:last-child { margin-bottom: 0; }\n"
            + "  .help-card ul { padding-left: 20px; }\n"
            + "  .help-card li { font-size: 13.5px; color: #334155; line-height: 1.75; }\n"
            + "  .help-card strong { color: #1e293b; }\n"

            // Tip / Warning
            + "  .tip {\n"
            + "    background: #eff6ff; border-left: 4px solid #2563eb;\n"
            + "    padding: 13px 16px; border-radius: 0 8px 8px 0;\n"
            + "    margin-bottom: 14px;\n"
            + "  }\n"
            + "  .tip p { color: #1e40af; font-size: 13px; margin: 0; line-height: 1.5; }\n"
            + "  .warning {\n"
            + "    background: #fffbeb; border-left: 4px solid #f59e0b;\n"
            + "    padding: 13px 16px; border-radius: 0 8px 8px 0;\n"
            + "    margin-bottom: 14px;\n"
            + "  }\n"
            + "  .warning p { color: #92400e; font-size: 13px; margin: 0; line-height: 1.5; }\n"

            // Access badges
            + "  .access-badge {\n"
            + "    display: inline-block; padding: 5px 14px;\n"
            + "    border-radius: 20px; font-size: 12px; font-weight: 700;\n"
            + "    margin-bottom: 16px; letter-spacing: 0.2px;\n"
            + "  }\n"
            + "  .badge-admin { background: #fee2e2; color: #dc2626; border: 1px solid #fca5a5; }\n"
            + "  .badge-coord { background: #ede9fe; color: #7c3aed; border: 1px solid #c4b5fd; }\n"
            + "  .badge-all   { background: #d1fae5; color: #065f46; border: 1px solid #6ee7b7; }\n"
            + "</style>\n";
    }
}
