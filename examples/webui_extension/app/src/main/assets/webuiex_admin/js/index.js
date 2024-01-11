/* © 2023 INNES SA (labs@innes.fr). All rights are reserved. */

const locales = {
  en: {
    appTitle: "WebUI Extension",
    backgroundColor: "Background color",
  },
  fr: {
    appTitle: "WebUI Extension",
    backgroundColor: "Couleur de fond",
  },
  de: {
    appTitle: "WebUI Extension",
    backgroundColor: "Hintergrundfarbe",
  },
  es: {
    appTitle: "WebUI Extension",
    backgroundColor: "Color de fondo",
  },
};

let lang;

const prefsFilename = "tech.qeedji.webui_extension.prefs";
const baseOdataUrl = "/odata.qs/v1/AppPreferencesAdmin.tech.qeedji.webui_extension";
const backgroundColorPref = `${prefsFilename}/background_color`;

const elements = {
  backgroundColor: {
    valueOrigin: "#000000",
    value: "#000000",
    e: document.getElementById("backgroundColor"),
    pref: backgroundColorPref,
    type: "color",
  },
};

const form = document.getElementById("form");
const loading = document.getElementById("loading");
form.addEventListener("submit", send, true);
form.addEventListener("reset", cancel, true);
form.addEventListener("input", modified, true);

window.addEventListener("message", ev => {
  if ("cancel" in ev.data) {
    cancel(ev);
  }
  if ("save" in ev.data) {
    send(ev);
  }
});

function cancel(e) {
  e.preventDefault();
  for (const e of Object.values(elements)) {
    e.value = e.valueOrigin;
    if (e.type === "number") {
      e.e.valueAsNumber = e.value;
    } else if (e.type === "boolean") {
      e.e.checked = e.value;
    } else if (e.type === "color") {
      e.e.value = e.value;
    } else {
      e.e.value = e.value;
    }
  }
  modified(null);
}

/** @type {ReturnType<typeof setTimeout>} */
let loadingTimeout;
function startLoading() {
  loadingTimeout = setTimeout(() => {
    loading.style.display = "";
  }, 2000);
  window.parent.postMessage({ loading: true }, "*");
}

function stopLoading() {
  clearTimeout(loadingTimeout);
  loading.style.display = "none";
  window.parent.postMessage({ loading: false }, "*");
}

/**
 *
 * @param {string} url
 * @param {string} method
 * @param {string | undefined} body
 */
function doFetch(url, method, body) {
  return fetch(url, {
    mode: "same-origin",
    keepalive: true,
    referrerPolicy: "same-origin",
    cache: "no-cache",
    method,
    body,
  });
}

function getOdataType(e) {
  switch (e.type) {
    case "boolean":
      return "Preferences.BooleanValue";
    case "number":
      return "Preferences.LongValue";
    case "color":
    case "string":
      return "Preferences.StringValue";
  }
}

/** @param {Event} e */
function send(e) {
  e.preventDefault();
  startLoading();

  try {
    const promises = [];
    for (const e of Object.values(elements)) {
      /** @type {(typeof e.value) | undefined} */
      let v;
      if (e.type === "boolean") {
        v = e.e.checked;
      } else if (e.type === "number") {
        v = e.e.valueAsNumber;
        if (isNaN(v)) {
          v = e.value;
          e.e.valueAsNumber = e.value;
        }
      } else if (e.type === "color") {
        v = e.value;
      } else {
        v = e.value;
      }
      if (v !== e.valueOrigin) {
        if (e.e.checkValidity()) {
          let v = e.value;
          promises.push(doFetch(
            `${baseOdataUrl}(${encodeURIComponent(e.pref)})`,
            "PATCH",
            JSON.stringify({
              "@odata.type": getOdataType(e),
              Value: v,
            })
          ).then(r => {
            if (r.ok) {
              e.valueOrigin = e.value;
            }
          }));
        }
      }
    }
    Promise.all(promises).then(
      () => {
        window.parent.postMessage({ saved: true, needReboot: false }, "*");
      },
      _ => {
        window.parent.postMessage({ savedError: 0 }, "*");
      },
    ).finally(() => {
      stopLoading();
      modified(null);
    });
  } catch (e) {
    window.parent.postMessage({ savedError: e.toString() }, "*");
    stopLoading();
  }
}

/**
 *
 * @param {InputEvent | null} ev
 */
function modified(ev) {
  let modified = false;
  let valid = true;
  for (const e of Object.values(elements)) {
    let v;
    if (e.type === "boolean") {
      v = e.e.checked;
    } else if (e.type === "number") {
      v = e.e.valueAsNumber;
      if (isNaN(v)) {
        v = e.value;
        e.e.valueAsNumber = e.value;
      }
    } else if (e.type === "color") {
      v = e.e.value;
    } else {
      v = e.e.value;
    }

    if (v === e.valueOrigin) {
      e.value = v;
    } else {
      modified = true;
    }
    if (e.e.checkValidity()) {
      e.value = v;
    } else {
      valid = false;
    }
  }
  window.parent.postMessage({ modified, valid }, "*");
}

function getLocales() {
  if (!lang || lang.length === 0) {
    lang = document.documentElement.lang;
  }

  if (!lang || lang.length === 0) {
    try {
      lang = window.parent.document.documentElement.lang;
    } catch {
      // nothing
    }
  }
  if (!lang || lang.length === 0) {
    try {
      lang = navigator.language[0];
    } catch {

    }
  }
  if (lang) {
    lang = lang.split("-")[0].toLowerCase();
  }

  const l = locales[lang];
  if (l) {
    return l;
  }

  return locales.en;
}


function setLang() {
  const l = getLocales();
  document.documentElement.lang = lang;
  for (const e of document.body.querySelectorAll("[data-i18n]")) {
    /** @type {HTMLElement}*/
    // @ts-ignore
    const ee = e;
    const i = ee.dataset.i18n;
    e.textContent = l[i];
  }
}

setLang();

// Listen lang change
try {
  new MutationObserver(() => {
    document.documentElement.lang = "";
    lang = "";
    setLang();
  }).observe(parent.document.documentElement, {
    attributeFilter: ["lang"],
    attributes: true,
    characterData: false,
    childList: false,
    subtree: false,
  });
} catch (_e) { }

startLoading();
doFetch(
  baseOdataUrl,
  "GET",
  undefined,
)
  .then(r => {
    if (r.ok) {
      return r.json().then(
        /**
         * @param {{value: {Name: string; Value: string;}[]}} j
         */
        j => {
          const nameToElement = {};
          for (const e of Object.values(elements)) {
            nameToElement[e.pref] = e;
          }
          for (const p of j.value) {
            const e = nameToElement[p.Name];
            if (e) {
              if (e.type === "number") {
                const v = p.Value;
                e.value = e.valueOrigin = v;
                e.e.valueAsNumber = v;
              } else if (e.type === "boolean") {
                const v = p.Value;
                e.e.checked = e.value = e.valueOrigin = v;
              } else if (e.type === "color") {
                e.e.value = e.value = e.valueOrigin = p.Value ?? "";
              } else {
                e.e.value = e.value = e.valueOrigin = p.Value ?? "";
              }
            }
          }
        },
      );
    } else {
      window.parent.postMessage({ loadingError: `HTTP ${r.status}` }, "*");
    }
  }, e => {
    window.parent.postMessage({ loadingError: e }, "*");
  })
  .finally(stopLoading);