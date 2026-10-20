const app = document.querySelector("#app");
const storageKey = "agrovalle.session";
const categories = ["Frutas", "Verduras", "Tubérculos", "Granos", "Otros"];
const municipalities = [
    ["DAGUA", "Dagua"],
    ["PALMIRA", "Palmira"],
    ["BUGA", "Buga"],
    ["TULUA", "Tuluá"],
    ["CAICEDONIA", "Caicedonia"],
    ["JAMUNDI", "Jamundí"]
];
const pageSize = 9;

const state = {
    screen: "login",
    session: loadSession(),
    profile: null,
    lots: [],
    offers: [],
    page: 1,
    productQuery: "",
    municipality: "",
    category: "",
    busy: false
};

function loadSession() {
    try {
        const saved = JSON.parse(sessionStorage.getItem(storageKey) || "null");
        if (!saved || typeof saved.token !== "string" || typeof saved.id !== "string") {
            return null;
        }
        const claims = decodeToken(saved.token);
        if (!claims || !claims.exp || claims.exp * 1000 <= Date.now() || claims.rol !== "PRODUCCION") {
            sessionStorage.removeItem(storageKey);
            return null;
        }
        return { token: saved.token, id: saved.id };
    } catch {
        sessionStorage.removeItem(storageKey);
        return null;
    }
}

function decodeToken(token) {
    try {
        const payload = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
        return JSON.parse(atob(payload.padEnd(Math.ceil(payload.length / 4) * 4, "=")));
    } catch {
        return null;
    }
}

function escapeHtml(value) {
    return String(value ?? "").replace(/[&<>"']/g, character => ({
        "&": "&amp;",
        "<": "&lt;",
        ">": "&gt;",
        "\"": "&quot;",
        "'": "&#39;"
    })[character]);
}

function icon(name) {
    const icons = {
        leaf: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 4c-7.4 0-13 3.6-13 9.1A5.9 5.9 0 0 0 13 19c5.4 0 7-7.7 7-15Z"/><path d="M4 21c2.6-4.8 6-7.7 11-10"/></svg>',
        arrow: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M5 12h14M13 6l6 6-6 6"/></svg>',
        basket: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="m4 10 2 10h12l2-10H4Z"/><path d="m9 10 3-6 3 6M8 14v2m4-2v2m4-2v2"/></svg>',
        market: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M3 10h18l-2-6H5l-2 6Z"/><path d="M5 10v10h14V10M9 20v-6h6v6M3 10a3 3 0 0 0 6 0 3 3 0 0 0 6 0 3 3 0 0 0 6 0"/></svg>',
        pin: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20 10c0 5-8 12-8 12S4 15 4 10a8 8 0 1 1 16 0Z"/><circle cx="12" cy="10" r="2.5"/></svg>',
        sprout: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 21V9"/><path d="M12 12c-6 0-8-3-8-7 5 0 8 2 8 7Zm0-3c0-4 3-7 8-7 0 4-2 7-8 7Z"/><path d="M7 21h10"/></svg>',
        calendar: '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M16 3v4M8 3v4M3 10h18"/></svg>'
    };
    return icons[name] || "";
}

function municipalityOptions(selected = "") {
    return municipalities.map(([value, label]) =>
        `<option value="${value}" ${selected === value ? "selected" : ""}>${label}</option>`
    ).join("");
}

function categoryOptions(selected = "", allOption = false) {
    return `${allOption ? '<option value="">Todas las categorías</option>' : '<option value="">Selecciona una categoría</option>'}${categories.map(category =>
        `<option value="${escapeHtml(category)}" ${selected === category ? "selected" : ""}>${escapeHtml(category)}</option>`
    ).join("")}`;
}

function topbar(authenticated = false) {
    const name = escapeHtml(state.profile?.nombreCompleto || "Mi cuenta");
    const initials = name === "Mi cuenta" ? "A" : escapeHtml(
        name.split(/\s+/).slice(0, 2).map(part => part[0]).join("").toUpperCase()
    );
    return `
        <header class="topbar">
            <a class="brand" href="/" data-action="home" aria-label="AgroValle Connect, inicio">
                <span class="brand-mark">${icon("leaf")}</span>
                <span class="brand-name">AgroValle <span>Connect</span></span>
            </a>
            ${authenticated ? `
                <div class="topbar-actions">
                    <nav class="main-nav" aria-label="Navegación principal">
                        <a class="nav-link" href="#" data-action="dashboard" ${state.screen === "dashboard" ? 'aria-current="page"' : ""}>Panel</a>
                        <a class="nav-link" href="#" data-action="catalog" ${state.screen === "catalog" ? 'aria-current="page"' : ""}>Ofertas</a>
                    </nav>
                    <div class="user-chip">
                        <span class="avatar" aria-hidden="true">${initials}</span>
                        <span class="user-chip-name">${name}</span>
                    </div>
                    <button class="button button-secondary button-small" type="button" data-action="logout">Salir</button>
                </div>` : `
                <div class="topbar-actions">
                    <span class="nav-link">Productores del Valle del Cauca</span>
                </div>`}
        </header>`;
}

function footer() {
    return '<footer class="footer">AgroValle Connect · Conectando el campo con nuevas oportunidades</footer>';
}

function authIntro() {
    return `
        <section class="auth-intro">
            <p class="eyebrow"><span class="eyebrow-dot"></span> El campo, más cerca</p>
            <h1>Lo que nace en el campo <span>llega más lejos.</span></h1>
            <p>Conecta tu cosecha con nuevos compradores. Publica tus productos y haz crecer tu negocio desde el Valle del Cauca.</p>
            <div class="intro-note">
                <span class="intro-note-icon">${icon("sprout")}</span>
                <span>Una conexión más directa entre quienes cultivan y quienes compran.</span>
            </div>
        </section>`;
}

function renderLogin(message = "") {
    state.screen = "login";
    app.innerHTML = `
        <div class="page-shell">
            ${topbar()}
            <main class="auth-layout">
                ${authIntro()}
                <section class="auth-panel" aria-labelledby="login-title">
                    <p class="eyebrow"><span class="eyebrow-dot"></span> Bienvenido de nuevo</p>
                    <h2 id="login-title">Inicia sesión</h2>
                    <p class="auth-subtitle">Ingresa a tu espacio para administrar y compartir tus cosechas.</p>
                    <form id="login-form" novalidate>
                        <div class="form-grid single">
                            <div class="field">
                                <label for="login-email">Correo electrónico</label>
                                <input id="login-email" name="correo" type="email" autocomplete="email" placeholder="tu@correo.com" required maxlength="100">
                            </div>
                            <div class="field">
                                <label for="login-password">Contraseña</label>
                                <input id="login-password" name="contrasena" type="password" autocomplete="current-password" placeholder="Escribe tu contraseña" required>
                            </div>
                        </div>
                        <div id="form-message" class="message ${message ? "success" : ""}" role="status" aria-live="polite">${escapeHtml(message)}</div>
                        <button class="button button-primary button-wide" type="submit">
                            Iniciar sesión ${icon("arrow")}
                        </button>
                    </form>
                    <p class="form-footer">¿Todavía no tienes una cuenta?
                        <button class="text-button" type="button" data-action="register">Regístrate aquí</button>
                    </p>
                </section>
            </main>
            ${footer()}
        </div>`;
}

function renderRegister(message = "") {
    state.screen = "register";
    app.innerHTML = `
        <div class="page-shell">
            ${topbar()}
            <main class="auth-layout">
                ${authIntro()}
                <section class="auth-panel" aria-labelledby="register-title">
                    <p class="eyebrow"><span class="eyebrow-dot"></span> Únete a la comunidad</p>
                    <h2 id="register-title">Crea tu cuenta</h2>
                    <p class="auth-subtitle">Registra tus datos para empezar a compartir lo mejor de tu cosecha.</p>
                    <form id="register-form" novalidate>
                        <div class="form-grid">
                            <div class="field full">
                                <label for="register-name">Nombre completo</label>
                                <input id="register-name" name="nombreCompleto" type="text" autocomplete="name" placeholder="Como te llamas" required maxlength="100">
                            </div>
                            <div class="field">
                                <label for="register-id">Cédula</label>
                                <input id="register-id" name="cedula" type="text" inputmode="numeric" autocomplete="off" placeholder="Solo números" pattern="[0-9]{6,10}" minlength="6" maxlength="10" required>
                            </div>
                            <div class="field">
                                <label for="register-phone">Teléfono <span class="field-hint" style="display:inline"> (opcional)</span></label>
                                <input id="register-phone" name="telefono" type="tel" autocomplete="tel" placeholder="Tu número de contacto">
                            </div>
                            <div class="field full">
                                <label for="register-email">Correo electrónico</label>
                                <input id="register-email" name="correo" type="email" autocomplete="email" placeholder="tu@correo.com" maxlength="100" required>
                            </div>
                            <div class="field full">
                                <label for="register-location">Ubicación de la finca</label>
                                <input id="register-location" name="ubicacionFinca" type="text" placeholder="Vereda, corregimiento o dirección" required maxlength="180">
                            </div>
                            <div class="field full">
                                <label for="register-municipality">Municipio</label>
                                <select id="register-municipality" name="municipio" required>
                                    <option value="">Selecciona tu municipio</option>
                                    ${municipalityOptions()}
                                </select>
                            </div>
                            <div class="field">
                                <label for="register-password">Contraseña</label>
                                <input id="register-password" name="contrasena" type="password" autocomplete="new-password" placeholder="Mínimo 8 caracteres" minlength="8" required>
                            </div>
                            <div class="field">
                                <label for="register-confirm">Confirmar contraseña</label>
                                <input id="register-confirm" name="confirmarContrasena" type="password" autocomplete="new-password" placeholder="Repite tu contraseña" minlength="8" required>
                            </div>
                        </div>
                        <div id="form-message" class="message ${message ? "success" : ""}" role="status" aria-live="polite">${escapeHtml(message)}</div>
                        <button class="button button-primary button-wide" type="submit">
                            Crear mi cuenta ${icon("arrow")}
                        </button>
                    </form>
                    <p class="form-footer">¿Ya tienes una cuenta?
                        <button class="text-button" type="button" data-action="login">Inicia sesión</button>
                    </p>
                </section>
            </main>
            ${footer()}
        </div>`;
}

function money(value) {
    return new Intl.NumberFormat("es-CO", {
        style: "currency",
        currency: "COP",
        maximumFractionDigits: 0
    }).format(Number(value) || 0);
}

function number(value) {
    return new Intl.NumberFormat("es-CO", { maximumFractionDigits: 2 }).format(Number(value) || 0);
}

function dateLabel(value) {
    if (!value) return "Sin fecha";
    const date = new Date(`${value}T12:00:00`);
    return Number.isNaN(date.getTime()) ? escapeHtml(value) : date.toLocaleDateString("es-CO", {
        day: "numeric", month: "short", year: "numeric"
    });
}

function localDateValue() {
    const date = new Date();
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
    return date.toISOString().slice(0, 10);
}

function emptyState(title, body) {
    return `<div class="empty-state"><strong>${escapeHtml(title)}</strong>${escapeHtml(body)}</div>`;
}

function lotRows(lots) {
    if (!lots.length) {
        return `<tr><td colspan="5">${emptyState("Aún no has publicado lotes", "Publica tu primera cosecha para que aparezca aquí.")}</td></tr>`;
    }
    return lots.map(lot => `
        <tr>
            <td class="product-name">${escapeHtml(lot.tipoProducto)}</td>
            <td>${escapeHtml(lot.categoria)}</td>
            <td>${number(lot.cantidadKg)} kg</td>
            <td>${money(lot.precioUnitario)} / kg</td>
            <td>${dateLabel(lot.fechaCosecha)}</td>
        </tr>`).join("");
}

function renderDashboardLoading() {
    state.screen = "dashboard";
    app.innerHTML = `
        <div class="page-shell">${topbar(true)}
            <main class="page-content"><div class="loading"><span class="spinner" aria-hidden="true"></span> Cargando tu panel...</div></main>
            ${footer()}
        </div>`;
}

function renderDashboard() {
    state.screen = "dashboard";
    const lots = [...state.lots].sort((a, b) => b.id - a.id);
    const kilos = lots.reduce((sum, lot) => sum + Number(lot.cantidadKg || 0), 0);
    const recent = lots.slice(0, 5);
    app.innerHTML = `
        <div class="page-shell">
            ${topbar(true)}
            <main class="page-content">
                <section class="greeting-card">
                    <div class="greeting-content">
                        <p class="eyebrow"><span class="eyebrow-dot"></span> Tu espacio de trabajo</p>
                        <h1>¡Hola, ${escapeHtml((state.profile?.nombreCompleto || "productor").split(/\s+/)[0])}!</h1>
                        <p>Este es el resumen de tus publicaciones y cosechas activas.</p>
                    </div>
                    <button class="button" type="button" data-action="new-lot">${icon("basket")} Publicar una cosecha</button>
                </section>
                <section class="stats-grid" aria-label="Resumen de mis publicaciones">
                    <article class="stat-card"><span class="stat-label">Lotes activos</span><strong class="stat-value">${lots.length}</strong></article>
                    <article class="stat-card"><span class="stat-label">Kilos publicados</span><strong class="stat-value">${number(kilos)} <span style="font-size:14px">kg</span></strong></article>
                    <article class="stat-card"><span class="stat-label">Mi municipio</span><strong class="stat-value" style="font-size:20px">${escapeHtml(state.profile?.municipio || "—")}</strong></article>
                </section>
                <section class="dashboard-actions" aria-label="Accesos rápidos">
                    <button class="action-card" type="button" data-action="new-lot">
                        <span class="action-icon">${icon("sprout")}</span>
                        <span><strong>Publicar una cosecha</strong><span>Registra un nuevo lote para la venta</span></span>
                    </button>
                    <button class="action-card" type="button" data-action="catalog">
                        <span class="action-icon">${icon("market")}</span>
                        <span><strong>Explorar ofertas</strong><span>Consulta los productos publicados</span></span>
                    </button>
                </section>
                <section class="content-card">
                    <div class="section-heading">
                        <div><h2>Mis últimos lotes</h2><p>Las cosechas que tienes publicadas actualmente.</p></div>
                        <button class="button button-secondary button-small" type="button" data-action="new-lot">Nuevo lote</button>
                    </div>
                    <div class="table-scroll">
                        <table class="data-table">
                            <thead><tr><th>Producto</th><th>Categoría</th><th>Cantidad</th><th>Precio por kg</th><th>Fecha de cosecha</th></tr></thead>
                            <tbody>${lotRows(recent)}</tbody>
                        </table>
                    </div>
                </section>
            </main>
            ${footer()}
        </div>`;
}

function renderLotForm(message = "") {
    state.screen = "new-lot";
    app.innerHTML = `
        <div class="page-shell">
            ${topbar(true)}
            <main class="page-content">
                <div class="page-heading">
                    <div><p class="eyebrow"><span class="eyebrow-dot"></span> Mis publicaciones</p><h1>Publica una cosecha</h1><p>Completa los datos del producto y estará disponible para que otros encuentren tu oferta.</p></div>
                    <button class="button button-secondary" type="button" data-action="dashboard">Volver al panel</button>
                </div>
                <form id="lot-form" class="form-card" novalidate>
                    <div class="form-grid">
                        <div class="field full">
                            <label for="lot-product">Producto</label>
                            <input id="lot-product" name="tipoProducto" type="text" list="product-suggestions" placeholder="Ejemplo: Tomate chonto" maxlength="100" required>
                            <datalist id="product-suggestions"><option value="Aguacate"><option value="Banano"><option value="Café"><option value="Cebolla"><option value="Lulo"><option value="Mango"><option value="Plátano"><option value="Tomate"><option value="Yuca"></datalist>
                            <span class="field-hint">Escribe el nombre del producto; el catálogo oficial de productos aún no está conectado.</span>
                        </div>
                        <div class="field">
                            <label for="lot-category">Categoría</label>
                            <select id="lot-category" name="categoria" required>${categoryOptions()}</select>
                        </div>
                        <div class="field">
                            <label for="lot-date">Fecha de cosecha</label>
                            <input id="lot-date" name="fechaCosecha" type="date" min="${localDateValue()}" value="${localDateValue()}" required>
                        </div>
                        <div class="field">
                            <label for="lot-quantity">Cantidad disponible (kg)</label>
                            <input id="lot-quantity" name="cantidadKg" type="number" min="0.01" step="0.01" inputmode="decimal" placeholder="Ejemplo: 120" required>
                        </div>
                        <div class="field">
                            <label for="lot-price">Precio por kilogramo (COP)</label>
                            <input id="lot-price" name="precioUnitario" type="number" min="0.01" step="0.01" inputmode="decimal" placeholder="Ejemplo: 3500" required>
                        </div>
                    </div>
                    <div id="form-message" class="message ${message ? "success" : ""}" role="status" aria-live="polite">${escapeHtml(message)}</div>
                    <div class="form-actions">
                        <button class="button button-secondary" type="button" data-action="dashboard">Cancelar</button>
                        <button class="button button-primary" type="submit">${icon("basket")} Publicar lote</button>
                    </div>
                </form>
            </main>
            ${footer()}
        </div>`;
}

function renderCatalogLoading() {
    state.screen = "catalog";
    app.innerHTML = `
        <div class="page-shell">${topbar(true)}
            <main class="page-content"><div class="loading"><span class="spinner" aria-hidden="true"></span> Buscando cosechas disponibles...</div></main>
            ${footer()}
        </div>`;
}

function renderOffers() {
    const query = state.productQuery.trim().toLocaleLowerCase("es");
    const offers = state.offers.filter(offer =>
        !query || String(offer.tipoProducto).toLocaleLowerCase("es").includes(query)
    );
    const totalPages = Math.max(1, Math.ceil(offers.length / pageSize));
    state.page = Math.min(state.page, totalPages);
    const shown = offers.slice((state.page - 1) * pageSize, state.page * pageSize);
    const cards = shown.map(offer => {
        const farmer = offer.farmer;
        const place = farmer?.municipio
            ? municipalityName(farmer.municipio)
            : "Ubicación no disponible";
        const farmerName = farmer?.nombreCompleto || `Productor #${offer.agricultorId}`;
        return `
            <article class="offer-card">
                <div class="offer-topline"><span class="category-tag">${escapeHtml(offer.categoria)}</span><span class="status-pill">Disponible</span></div>
                <h2 class="offer-product">${escapeHtml(offer.tipoProducto)}</h2>
                <div class="offer-meta">${icon("pin")}<span>${escapeHtml(place)}</span></div>
                <div class="offer-meta" style="margin-top:7px">${icon("calendar")}<span>Cosecha: ${dateLabel(offer.fechaCosecha)}</span></div>
                <div class="offer-price-row">
                    <strong class="offer-price"><small>Precio por kilogramo</small>${money(offer.precioUnitario)}</strong>
                    <span class="offer-stock">${number(offer.cantidadKg)} kg<br>disponibles</span>
                </div>
                <div class="offer-farmer">Productor: <strong>${escapeHtml(farmerName)}</strong></div>
            </article>`;
    }).join("");
    state.screen = "catalog";
    app.innerHTML = `
        <div class="page-shell">
            ${topbar(true)}
            <main class="page-content">
                <div class="page-heading">
                    <div><p class="eyebrow"><span class="eyebrow-dot"></span> Cosechas del Valle del Cauca</p><h1>Ofertas disponibles</h1><p>Explora las cosechas activas publicadas por los agricultores de la región.</p></div>
                    <button class="button button-secondary" type="button" data-action="dashboard">Volver al panel</button>
                </div>
                <form id="catalog-form" class="catalog-filters">
                    <div class="field"><label for="filter-product">Producto</label><input id="filter-product" name="producto" type="search" placeholder="Buscar por nombre" value="${escapeHtml(state.productQuery)}"></div>
                    <div class="field"><label for="filter-category">Categoría</label><select id="filter-category" name="categoria">${categoryOptions(state.category, true)}</select></div>
                    <div class="field"><label for="filter-municipality">Municipio</label><select id="filter-municipality" name="municipio"><option value="">Todos los municipios</option>${municipalityOptions(state.municipality)}</select></div>
                    <button class="button button-primary" type="submit">${icon("market")} Buscar</button>
                </form>
                <div id="catalog-message" class="message error" role="status" aria-live="polite"></div>
                ${shown.length
                    ? `<section class="offer-grid" aria-label="Ofertas encontradas">${cards}</section>
                       <nav class="pagination" aria-label="Paginación de ofertas">
                           <button class="button button-secondary button-small" type="button" data-action="previous-page" ${state.page <= 1 ? "disabled" : ""}>Anterior</button>
                           <span>Página ${state.page} de ${totalPages} · ${offers.length} ofertas</span>
                           <button class="button button-secondary button-small" type="button" data-action="next-page" ${state.page >= totalPages ? "disabled" : ""}>Siguiente</button>
                       </nav>`
                    : emptyState("No encontramos ofertas con esos filtros", "Prueba con otro producto o selecciona un municipio diferente.")}
            </main>
            ${footer()}
        </div>`;
}

function municipalityName(value) {
    return municipalities.find(([key]) => key === value)?.[1] || value;
}

async function apiRequest(path, options = {}) {
    const headers = new Headers(options.headers || {});
    if (options.body) headers.set("Content-Type", "application/json");
    if (state.session?.token) headers.set("Authorization", `Bearer ${state.session.token}`);
    let response;
    try {
        response = await fetch(path, { ...options, headers });
    } catch {
        throw new Error("No pudimos conectar con el servidor. Verifica que AgroValle Connect esté iniciado e inténtalo de nuevo.");
    }
    if (response.status === 401 && state.session) {
        signOut(false);
        throw new Error("Tu sesión venció. Inicia sesión nuevamente.");
    }
    if (response.status === 401) {
        throw new Error("El correo o la contraseña no son correctos. Revísalos e inténtalo de nuevo.");
    }
    if (response.status === 204) return null;
    const text = await response.text();
    let data = null;
    if (text) {
        try {
            data = JSON.parse(text);
        } catch {
            data = text;
        }
    }
    if (!response.ok) {
        const details = data && typeof data === "object"
            ? Object.values(data.detalles || {}).join(" ")
            : "";
        const message = data && typeof data === "object" ? data.mensaje : data;
        throw new Error(details || message || `El servidor respondió con el error ${response.status}.`);
    }
    return data;
}

function formMessage(message, type = "error") {
    const element = document.querySelector("#form-message");
    if (!element) return;
    element.textContent = message;
    element.className = `message ${type}`;
}

function setBusy(form, busy, label) {
    const button = form.querySelector('button[type="submit"]');
    if (!button) return;
    button.disabled = busy;
    if (busy) {
        button.dataset.originalLabel = button.textContent.trim();
        button.textContent = label;
    } else if (button.dataset.originalLabel) {
        button.textContent = button.dataset.originalLabel;
        delete button.dataset.originalLabel;
    }
}

async function submitLogin(form) {
    if (!form.reportValidity()) return;
    setBusy(form, true, "Ingresando...");
    try {
        const values = new FormData(form);
        const result = await apiRequest("/api/v1/auth/login", {
            method: "POST",
            body: JSON.stringify({
                correo: values.get("correo").trim(),
                contrasena: values.get("contrasena")
            })
        });
        const claims = decodeToken(result.accessToken);
        if (!claims || !claims.sub || claims.rol !== "PRODUCCION") {
            formMessage("Esta cuenta no corresponde a un perfil de agricultor. El acceso para comerciantes aún no está disponible en esta pantalla.");
            setBusy(form, false);
            return;
        }
        state.session = { token: result.accessToken, id: claims.sub };
        sessionStorage.setItem(storageKey, JSON.stringify(state.session));
        await loadDashboard();
    } catch (error) {
        formMessage(error.message);
        setBusy(form, false);
    }
}

async function submitRegistration(form) {
    if (!form.reportValidity()) return;
    const values = new FormData(form);
    if (values.get("contrasena") !== values.get("confirmarContrasena")) {
        formMessage("Las contraseñas no coinciden. Revisa los dos campos.");
        return;
    }
    const cedula = values.get("cedula").trim();
    if (!/^\d{6,10}$/.test(cedula)) {
        formMessage("La cédula debe tener entre 6 y 10 dígitos.");
        return;
    }
    setBusy(form, true, "Creando cuenta...");
    try {
        await apiRequest("/api/v1/agricultores/registro", {
            method: "POST",
            body: JSON.stringify({
                nombreCompleto: values.get("nombreCompleto").trim(),
                cedula,
                correo: values.get("correo").trim(),
                contrasena: values.get("contrasena"),
                telefono: values.get("telefono").trim(),
                ubicacionFinca: values.get("ubicacionFinca").trim(),
                municipio: values.get("municipio")
            })
        });
        renderLogin("Tu cuenta fue creada. Ya puedes iniciar sesión con tu correo y contraseña.");
    } catch (error) {
        formMessage(error.message);
        setBusy(form, false);
    }
}

async function submitLot(form) {
    if (!form.reportValidity()) return;
    const values = new FormData(form);
    const body = {
        tipoProducto: values.get("tipoProducto").trim(),
        categoria: values.get("categoria"),
        cantidadKg: Number(values.get("cantidadKg")),
        precioUnitario: Number(values.get("precioUnitario")),
        fechaCosecha: values.get("fechaCosecha")
    };
    if (!body.tipoProducto || body.cantidadKg <= 0 || body.precioUnitario <= 0) {
        formMessage("Escribe el producto y asegúrate de que la cantidad y el precio sean mayores que cero.");
        return;
    }
    setBusy(form, true, "Publicando...");
    try {
        await apiRequest("/api/v1/productos", { method: "POST", body: JSON.stringify(body) });
        await loadDashboard("Tu cosecha se publicó correctamente.");
    } catch (error) {
        formMessage(error.message);
        setBusy(form, false);
    }
}

async function loadDashboard(successMessage = "") {
    renderDashboardLoading();
    try {
        const profile = await apiRequest(`/api/v1/agricultores/${encodeURIComponent(state.session.id)}/perfil`);
        state.profile = profile;
        state.lots = profile.lotesActivos || [];
        renderDashboard();
        if (successMessage) showToast(successMessage);
    } catch (error) {
        renderLogin(error.message);
    }
}

async function loadCatalog() {
    state.offers = [];
    renderCatalogLoading();
    try {
        const parameters = new URLSearchParams();
        if (state.municipality) parameters.set("municipio", state.municipality);
        if (state.category) parameters.set("categoria", state.category);
        const query = parameters.size ? `?${parameters.toString()}` : "";
        const offers = await apiRequest(`/api/v1/productos/buscar${query}`);
        const farmers = [...new Set(offers.map(offer => offer.agricultorId))];
        const profiles = await Promise.all(farmers.map(async id => {
            const profile = await apiRequest(`/api/v1/agricultores/${encodeURIComponent(id)}/perfil`);
            return [id, profile];
        }));
        const profileById = new Map(profiles);
        state.offers = offers.map(offer => ({ ...offer, farmer: profileById.get(offer.agricultorId) }));
        state.page = 1;
        renderOffers();
    } catch (error) {
        renderOffers();
        const element = document.querySelector("#catalog-message");
        if (element) {
            element.textContent = error.message;
            element.className = "message error";
        }
    }
}

function showToast(message) {
    document.querySelector(".toast")?.remove();
    const toast = document.createElement("div");
    toast.className = "toast";
    toast.setAttribute("role", "status");
    toast.textContent = message;
    document.body.append(toast);
    window.setTimeout(() => toast.remove(), 4200);
}

function signOut(showMessage = true) {
    sessionStorage.removeItem(storageKey);
    state.session = null;
    state.profile = null;
    state.lots = [];
    state.offers = [];
    state.screen = "login";
    renderLogin(showMessage ? "Has cerrado sesión correctamente." : "");
}

function openDashboard() {
    if (state.profile) renderDashboard();
    else loadDashboard();
}

document.addEventListener("click", event => {
    const target = event.target.closest("[data-action]");
    if (!target) return;
    if (target.tagName === "A") event.preventDefault();
    const action = target.dataset.action;
    if (action === "register") renderRegister();
    if (action === "login" || action === "home") renderLogin();
    if (action === "dashboard") openDashboard();
    if (action === "new-lot") renderLotForm();
    if (action === "catalog") loadCatalog();
    if (action === "logout") signOut();
    if (action === "previous-page" && state.page > 1) {
        state.page -= 1;
        renderOffers();
    }
    if (action === "next-page") {
        state.page += 1;
        renderOffers();
    }
});

document.addEventListener("submit", event => {
    const { target } = event;
    if (target.id === "login-form") {
        event.preventDefault();
        submitLogin(target);
    } else if (target.id === "register-form") {
        event.preventDefault();
        submitRegistration(target);
    } else if (target.id === "lot-form") {
        event.preventDefault();
        submitLot(target);
    } else if (target.id === "catalog-form") {
        event.preventDefault();
        const values = new FormData(target);
        state.productQuery = values.get("producto").trim();
        state.category = values.get("categoria");
        state.municipality = values.get("municipio");
        loadCatalog();
    }
});

if (state.session) {
    loadDashboard();
} else {
    renderLogin();
}
