(() => {
  "use strict";

  const API_BASE = "/api/v1";
  const MUNICIPALITIES = {
    DAGUA: "Dagua",
    PALMIRA: "Palmira",
    BUGA: "Buga",
    TULUA: "Tuluá",
    CAICEDONIA: "Caicedonia",
    JAMUNDI: "Jamundí",
    CALI: "Cali"
  };
  const CATEGORIES = {
    FRUTAS: "Frutas",
    VERDURAS: "Verduras",
    TUBERCULOS: "Tubérculos",
    GRANOS: "Granos",
    CAFE: "Café",
    OTROS: "Otros"
  };
  const PAGE_SIZE = 6;
  const state = {
    token: sessionStorage.getItem("agrovalle.accessToken"),
    farmerId: null,
    profile: null,
    lots: [],
    filteredLots: [],
    page: 0
  };

  const byId = (id) => document.getElementById(id);
  const screens = ["login-screen", "register-screen", "app-screen"];
  const views = ["dashboard-view", "catalog-view", "publish-view"];
  let noticeTimer;

  function showNotice(message, type) {
    const notice = byId("notice");
    window.clearTimeout(noticeTimer);
    notice.textContent = message;
    notice.className = `notice${type ? ` ${type}` : ""}`;
    notice.hidden = false;
    noticeTimer = window.setTimeout(() => { notice.hidden = true; }, 6500);
    notice.scrollIntoView({ behavior: "smooth", block: "nearest" });
  }

  function showScreen(id) {
    screens.forEach((screen) => { byId(screen).hidden = screen !== id; });
    byId("notice").hidden = true;
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function showView(id) {
    views.forEach((view) => { byId(view).hidden = view !== id; });
    document.querySelectorAll(".nav-link").forEach((button) => {
      button.classList.toggle("active", button.dataset.view === id);
    });
    byId("notice").hidden = true;
    window.scrollTo({ top: 0, behavior: "smooth" });
    if (id === "catalog-view") {
      loadLots().catch((error) => showNotice(error.message, "error"));
    }
  }

  function decodeToken(token) {
    try {
      const payload = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
      return JSON.parse(decodeURIComponent(Array.from(atob(payload), (character) =>
        `%${character.charCodeAt(0).toString(16).padStart(2, "0")}`).join("")));
    } catch (error) {
      throw new Error("El servidor devolvió una sesión no válida. Intenta iniciar sesión de nuevo.");
    }
  }

  async function request(path, options) {
    const settings = { ...options };
    const headers = new Headers(settings.headers || {});
    if (settings.body) headers.set("Content-Type", "application/json");
    if (state.token) headers.set("Authorization", `Bearer ${state.token}`);
    settings.headers = headers;

    let response;
    try {
      response = await fetch(`${API_BASE}${path}`, settings);
    } catch (error) {
      throw new Error("No se pudo conectar con AgroValle. Comprueba que la aplicación esté iniciada e inténtalo de nuevo.");
    }

    if (response.status === 401 && state.token) {
      clearSession();
      showScreen("login-screen");
      throw new Error("Tu sesión venció. Inicia sesión de nuevo.");
    }
    if (!response.ok) {
      let message = `La solicitud no pudo completarse (HTTP ${response.status}).`;
      try {
        const responseBody = await response.text();
        if (responseBody) {
          try {
            const body = JSON.parse(responseBody);
            if (typeof body.mensaje === "string") message = body.mensaje;
            if (body.detalles && typeof body.detalles === "object") {
              const details = Object.values(body.detalles).filter((value) => typeof value === "string");
              if (details.length) message = details.join(" ");
            }
          } catch (error) {
            message = responseBody;
          }
        }
      } catch (error) {
        message = `La solicitud no pudo completarse (HTTP ${response.status}).`;
      }
      if (response.status === 401 && !state.token) message = "Correo o contraseña incorrectos.";
      throw new Error(message);
    }
    if (response.status === 204) return null;
    return response.json();
  }

  function setButtonBusy(button, busy, label) {
    if (busy) {
      button.dataset.originalLabel = button.textContent;
      button.textContent = label;
    } else if (button.dataset.originalLabel) {
      button.textContent = button.dataset.originalLabel;
      delete button.dataset.originalLabel;
    }
    button.disabled = busy;
  }

  function clearSession() {
    state.token = null;
    state.farmerId = null;
    state.profile = null;
    state.lots = [];
    sessionStorage.removeItem("agrovalle.accessToken");
    byId("header-user").hidden = true;
  }

  function updateHeader(name) {
    byId("header-user-name").textContent = name || "Agricultor";
    byId("header-user").hidden = false;
  }

  function formatCurrency(value) {
    return new Intl.NumberFormat("es-CO", {
      style: "currency",
      currency: "COP",
      maximumFractionDigits: 0
    }).format(Number(value));
  }

  function formatDate(value) {
    if (!value) return "—";
    const [year, month, day] = value.split("-").map(Number);
    return new Intl.DateTimeFormat("es-CO", { day: "numeric", month: "short", year: "numeric" })
      .format(new Date(year, month - 1, day));
  }

  function makeCell(text, className) {
    const cell = document.createElement("td");
    cell.textContent = text;
    if (className) cell.className = className;
    return cell;
  }

  async function loadProfile() {
    const [profile, lots] = await Promise.all([
      request(`/agricultores/${encodeURIComponent(state.farmerId)}/perfil`),
      request(`/productos/agricultor/${encodeURIComponent(state.farmerId)}`)
    ]);
    state.profile = profile;
    state.lots = Array.isArray(lots) ? lots : [];
    const name = profile.nombreCompleto || "agricultor";
    byId("farmer-name").textContent = name.split(/\s+/)[0];
    byId("farmer-municipality").textContent = MUNICIPALITIES[profile.municipio] || profile.municipio || "—";
    byId("active-lots-count").textContent = String(state.lots.length);
    updateHeader(name);
    renderMyLots();
  }

  function renderMyLots() {
    const body = byId("my-lots-body");
    body.replaceChildren();
    if (!state.lots.length) {
      const row = document.createElement("tr");
      row.append(makeCell("Todavía no tienes lotes activos. Publica tu primera cosecha para verla aquí.", "table-empty"));
      row.firstElementChild.colSpan = 6;
      body.append(row);
      return;
    }
    state.lots.slice(0, 5).forEach((lot) => {
      const row = document.createElement("tr");
      row.append(
        makeCell(lot.tipoProducto || "Producto"),
        makeCell(CATEGORIES[lot.categoria] || lot.categoria || "—"),
        makeCell(`${lot.cantidadKg} kg`),
        makeCell(formatCurrency(lot.precioUnitario)),
        makeCell(formatDate(lot.fechaCosecha))
      );
      const status = document.createElement("td");
      const badge = document.createElement("span");
      badge.className = "badge";
      badge.textContent = lot.activo ? "Activo" : "Inactivo";
      status.append(badge);
      row.append(status);
      body.append(row);
    });
  }

  function buildProductOptions() {
    const select = byId("filter-product");
    const current = select.value;
    const products = [...new Set(state.lots.map((lot) => lot.tipoProducto).filter(Boolean))]
      .sort((left, right) => left.localeCompare(right, "es"));
    select.replaceChildren(new Option("Todos los productos", ""));
    products.forEach((product) => select.add(new Option(product, product)));
    if (products.includes(current)) select.value = current;
  }

  function renderCatalog() {
    const grid = byId("catalog-grid");
    grid.replaceChildren();
    const start = state.page * PAGE_SIZE;
    const pageLots = state.filteredLots.slice(start, start + PAGE_SIZE);
    byId("catalog-count").textContent = `${state.filteredLots.length} ${state.filteredLots.length === 1 ? "oferta" : "ofertas"}`;

    if (!pageLots.length) {
      const empty = document.createElement("div");
      empty.className = "empty-state";
      const title = document.createElement("strong");
      title.textContent = "Aún no hay ofertas para mostrar";
      const message = document.createElement("span");
      message.textContent = state.lots.length
        ? "Prueba con otros filtros para encontrar cosechas."
        : "Las cosechas activas aparecerán aquí cuando se publiquen.";
      empty.append(title, message);
      grid.append(empty);
    } else {
      pageLots.forEach((lot) => {
        const card = document.createElement("article");
        card.className = "offer-card";
        const top = document.createElement("div");
        top.className = "offer-top";
        const details = document.createElement("div");
        const category = document.createElement("span");
        category.className = "offer-category";
        category.textContent = CATEGORIES[lot.categoria] || lot.categoria || "Cosecha";
        const title = document.createElement("h3");
        title.textContent = lot.tipoProducto || "Producto";
        const location = document.createElement("span");
        location.className = "offer-location";
        location.textContent = MUNICIPALITIES[lot.municipio] || lot.municipio || "Valle del Cauca";
        details.append(category, title, location);
        const badge = document.createElement("span");
        badge.className = "badge";
        badge.textContent = "Disponible";
        top.append(details, badge);
        const bottom = document.createElement("div");
        bottom.className = "offer-bottom";
        const price = document.createElement("strong");
        price.className = "offer-price";
        price.textContent = formatCurrency(lot.precioUnitario);
        const priceNote = document.createElement("small");
        priceNote.textContent = "por kilogramo";
        price.append(priceNote);
        const quantity = document.createElement("span");
        quantity.className = "offer-quantity";
        quantity.textContent = `${lot.cantidadKg} kg disponibles`;
        bottom.append(price, quantity);
        card.append(top, bottom);
        grid.append(card);
      });
    }

    const totalPages = Math.ceil(state.filteredLots.length / PAGE_SIZE);
    byId("page-label").textContent = totalPages ? `Página ${state.page + 1} de ${totalPages}` : "Sin resultados";
    byId("previous-page").disabled = state.page === 0;
    byId("next-page").disabled = state.page + 1 >= totalPages;
  }

  function applyFilters() {
    const product = byId("filter-product").value;
    const municipality = byId("filter-municipality").value;
    const category = byId("filter-category").value;
    state.filteredLots = state.lots.filter((lot) =>
      (!product || lot.tipoProducto === product)
      && (!municipality || lot.municipio === municipality)
      && (!category || lot.categoria === category)
    );
    state.page = 0;
    renderCatalog();
  }

  async function loadLots() {
    const lots = await request("/productos");
    state.lots = Array.isArray(lots) ? lots.filter((lot) => lot.activo !== false) : [];
    buildProductOptions();
    applyFilters();
  }

  async function enterFarmerArea(token) {
    const claims = decodeToken(token);
    if (claims.rol !== "PRODUCCION") {
      throw new Error("Esta interfaz es para cuentas de agricultor. El catálogo público sigue disponible sin iniciar sesión.");
    }
    if (!claims.sub) throw new Error("No fue posible identificar la cuenta. Inicia sesión de nuevo.");
    state.token = token;
    state.farmerId = String(claims.sub);
    sessionStorage.setItem("agrovalle.accessToken", token);
    await loadProfile();
    showScreen("app-screen");
    showView("dashboard-view");
  }

  byId("login-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    if (!form.reportValidity()) return;
    const button = form.querySelector('button[type="submit"]');
    const values = Object.fromEntries(new FormData(form));
    setButtonBusy(button, true, "Validando…");
    try {
      const result = await request("/auth/login", {
        method: "POST",
        body: JSON.stringify({ correo: values.correo.trim(), contrasena: values.contrasena })
      });
      if (!result.accessToken) throw new Error("El servidor no devolvió un token de acceso.");
      await enterFarmerArea(result.accessToken);
    } catch (error) {
      clearSession();
      showNotice(error.message, "error");
    } finally {
      setButtonBusy(button, false);
    }
  });

  byId("register-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    const password = byId("register-password");
    const confirmation = byId("register-confirm");
    confirmation.setCustomValidity(password.value === confirmation.value ? "" : "Las contraseñas no coinciden.");
    if (!form.reportValidity()) return;
    const button = form.querySelector('button[type="submit"]');
    const values = Object.fromEntries(new FormData(form));
    setButtonBusy(button, true, "Creando cuenta…");
    try {
      await request("/agricultores/registro", {
        method: "POST",
        body: JSON.stringify({
          nombreCompleto: values.nombreCompleto.trim(),
          cedula: values.cedula.trim(),
          correo: values.correo.trim(),
          contrasena: values.contrasena,
          telefono: values.telefono.trim() || null,
          ubicacionFinca: values.ubicacionFinca.trim(),
          municipio: values.municipio
        })
      });
      form.reset();
      showScreen("login-screen");
      showNotice("Tu cuenta fue creada. Ya puedes iniciar sesión con tu correo y contraseña.", "success");
    } catch (error) {
      showNotice(error.message, "error");
    } finally {
      setButtonBusy(button, false);
    }
  });

  byId("register-confirm").addEventListener("input", (event) => {
    event.currentTarget.setCustomValidity(
      event.currentTarget.value === byId("register-password").value ? "" : "Las contraseñas no coinciden."
    );
  });
  byId("register-password").addEventListener("input", () => {
    const confirmation = byId("register-confirm");
    if (confirmation.value) confirmation.setCustomValidity(
      confirmation.value === byId("register-password").value ? "" : "Las contraseñas no coinciden."
    );
  });

  byId("publish-form").addEventListener("submit", async (event) => {
    event.preventDefault();
    const form = event.currentTarget;
    if (!form.reportValidity()) return;
    const button = form.querySelector('button[type="submit"]');
    const values = Object.fromEntries(new FormData(form));
    setButtonBusy(button, true, "Publicando…");
    try {
      await request("/productos", {
        method: "POST",
        body: JSON.stringify({
          tipoProducto: values.tipoProducto.trim(),
          categoria: values.categoria,
          cantidadKg: Number(values.cantidadKg),
          precioUnitario: Number(values.precioUnitario),
          fechaCosecha: values.fechaCosecha
        })
      });
      form.reset();
      showView("dashboard-view");
      try {
        await loadProfile();
        showNotice("Tu cosecha se publicó correctamente.", "success");
      } catch (error) {
        showNotice(`Tu cosecha se publicó, pero no se pudo actualizar el panel: ${error.message}`, "info");
      }
    } catch (error) {
      showNotice(error.message, "error");
    } finally {
      setButtonBusy(button, false);
    }
  });

  document.querySelectorAll("[data-screen]").forEach((button) => {
    button.addEventListener("click", () => showScreen(button.dataset.screen));
  });
  document.querySelectorAll("[data-view]").forEach((button) => {
    button.addEventListener("click", () => showView(button.dataset.view));
  });
  document.querySelectorAll(".nav-link").forEach((button) => {
    button.addEventListener("click", () => showView(button.dataset.view));
  });
  byId("catalog-filter").addEventListener("submit", (event) => {
    event.preventDefault();
    applyFilters();
  });
  byId("previous-page").addEventListener("click", () => {
    if (state.page > 0) { state.page -= 1; renderCatalog(); }
  });
  byId("next-page").addEventListener("click", () => {
    if ((state.page + 1) * PAGE_SIZE < state.filteredLots.length) { state.page += 1; renderCatalog(); }
  });
  byId("logout-button").addEventListener("click", () => {
    clearSession();
    showScreen("login-screen");
    showNotice("Has cerrado sesión correctamente.", "success");
  });

  const today = new Date();
  byId("lot-date").min = [
    today.getFullYear(),
    String(today.getMonth() + 1).padStart(2, "0"),
    String(today.getDate()).padStart(2, "0")
  ].join("-");

  if (state.token) {
    enterFarmerArea(state.token).catch((error) => {
      clearSession();
      showScreen("login-screen");
      showNotice(error.message, "info");
    });
  }
})();
