# =============================================================================
#  VESTIGIVM MEI · vestigium.R
#  Carga todas las tablas exportadas con el tipo correcto de cada columna y deja
#  listas funciones de análisis.
#
#  Uso:  1. Pon este archivo en la misma carpeta que los vm_*.csv
#        2. En R o RStudio:  source("vestigium.R")
#  Requiere tidyverse:  install.packages("tidyverse")
#  Opcional: nlme (ya viene con R) para el modelo con autocorrelación.
# =============================================================================

suppressPackageStartupMessages({
  library(readr); library(dplyr); library(tidyr); library(lubridate)
  library(stringr); library(purrr); library(ggplot2); library(tibble)
})

# ---- Configuración ---------------------------------------------------------
# Puedes definir vm_carpeta o vm_zona antes de hacer source() para cambiarlos.
if (!exists("vm_carpeta", envir = globalenv())) {
  vm_carpeta <- tryCatch(dirname(normalizePath(sys.frame(1)$ofile)), error = function(e) getwd())
}
if (!exists("vm_zona", envir = globalenv())) {
  vm_zona <- Sys.timezone()
  if (is.na(vm_zona) || vm_zona %in% c("UTC", "Etc/UTC", "GMT")) vm_zona <- "America/Mexico_City"
}

# ---- Lectura tipada --------------------------------------------------------
vm_dic <- local({
  ruta <- file.path(vm_carpeta, "vm_diccionario.csv")
  if (file.exists(ruta)) read_csv(ruta, col_types = cols(.default = col_character()), na = "", show_col_types = FALSE, progress = FALSE)
  else tibble(archivo = character(), columna = character(), tipo = character(), descripcion = character(), tabla = character())
})

.vm_parser <- function(t) switch(t,
  fecha = col_date(format = "%Y-%m-%d"), hora = col_time(format = ""), entero = col_double(), numero = col_double(),
  logico = col_double(), col_character())

#' Convierte texto ISO 8601 con desfase (2026-10-16T08:42:10-06:00) a fecha-hora en tu zona.
vm_fecha_hora <- function(x) {
  x <- as.character(x)
  x[!is.na(x) & !nzchar(x)] <- NA
  # Java omite los segundos cuando son :00; se agregan para que el formato sea uniforme.
  x <- sub("^(\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2})(Z|[+-]\\d{2}:\\d{2})$", "\\1:00\\2", x, perl = TRUE)
  with_tz(ymd_hms(x, quiet = TRUE), vm_zona)
}

#' Separa una columna tipo lista ("a|b|c") en una lista de vectores.
vm_lista <- function(x) map(str_split(coalesce(as.character(x), ""), fixed("|")), ~ .x[nzchar(.x)])

#' Convierte listas "clave:n|clave:n" en una tabla larga (clave, n).
vm_pares <- function(datos, columna, id = "fecha") {
  datos |> select(all_of(c(id, columna))) |>
    mutate(.p = vm_lista(.data[[columna]])) |> select(-all_of(columna)) |> unnest(.p) |>
    separate_wider_regex(.p, c(clave = ".*", ":", n = "[0-9.]+$"), too_few = "align_start") |>
    mutate(n = as.numeric(n))
}

vm_leer <- function(archivo) {
  ruta <- file.path(vm_carpeta, archivo)
  if (!file.exists(ruta)) return(NULL)
  cab <- suppressWarnings(names(read_csv(ruta, n_max = 0, col_types = cols(.default = col_character()), show_col_types = FALSE)))
  if (length(cab) == 0 || all(!nzchar(cab))) return(NULL)
  d <- vm_dic[vm_dic$archivo == archivo & vm_dic$columna %in% cab, ]
  tipos <- setNames(lapply(d$tipo, .vm_parser), d$columna)
  x <- read_csv(ruta, col_types = do.call(cols, c(tipos, list(.default = col_guess()))), na = "",
                show_col_types = FALSE, progress = FALSE, locale = locale(encoding = "UTF-8"))
  for (cc in d$columna[d$tipo == "logico"]) x[[cc]] <- x[[cc]] == 1
  for (cc in d$columna[d$tipo == "fecha_hora"]) x[[cc]] <- vm_fecha_hora(x[[cc]])
  for (cc in d$columna[d$tipo == "entero"]) {
    v <- x[[cc]]
    if (all(is.na(v) | (abs(v) < .Machine$integer.max & v == round(v)))) x[[cc]] <- as.integer(v)
  }
  if (length(problems(x)$row)) warning(archivo, ": ", nrow(problems(x)), " celdas no se pudieron leer; revisa problems(vm$", sub("^vm_|\\.csv$", "", archivo), ")")
  x
}

vm_archivos <- c(
  panel = "vm_panel.csv", panel_extendido = "vm_panel_extendido.csv", variables = "vm_variables.csv", familias = "vm_familias.csv", dias = "vm_dias.csv", momentos = "vm_momentos.csv", gastos = "vm_gastos.csv", ingresos = "vm_ingresos.csv",
  escalas = "vm_escalas.csv", reaccion = "vm_reaccion.csv", salud = "vm_salud.csv", pantalla = "vm_pantalla.csv",
  visitas = "vm_visitas.csv", ubicaciones = "vm_ubicaciones.csv", entorno = "vm_entorno.csv", fotos = "vm_fotos.csv",
  agenda = "vm_agenda.csv", notificaciones = "vm_notificaciones.csv", musica = "vm_musica.csv", actividad = "vm_actividad.csv",
  ejercicio = "vm_ejercicio.csv", escalada_sesiones = "vm_escalada_sesiones.csv", escalada_bloques = "vm_escalada_bloques.csv",
  biblioteca = "vm_biblioteca.csv", comunicacion = "vm_comunicacion.csv", conexiones = "vm_conexiones.csv",
  subrayados = "vm_subrayados.csv", google_dia = "vm_google_dia.csv", google = "vm_google.csv", whatsapp = "vm_whatsapp.csv",
  nutricion = "vm_nutricion.csv", comidas = "vm_comidas.csv", entrenos = "vm_entrenos.csv", series = "vm_series.csv",
  tramos = "vm_tramos.csv", sustancias = "vm_sustancias.csv", horas = "vm_horas.csv", notas = "vm_notas.csv", metas = "vm_metas.csv",
  suplementos = "vm_suplementos.csv", laptop = "vm_laptop.csv", red_nodos = "vm_red_nodos.csv", red_vinculos = "vm_red_vinculos.csv",
  experimentos = "vm_experimentos.csv", personas = "vm_personas.csv", fechas_propias = "vm_fechas_propias.csv")

vm <- compact(lapply(vm_archivos, vm_leer))

# Las tablas quedan también como objetos sueltos: panel, dias, momentos, salud...
for (.n in names(vm)) assign(.n, vm[[.n]], envir = globalenv())
rm(.n)

# ---- Panel diario enriquecido ---------------------------------------------
# Desde la entrega 10d, el panel de análisis (panel) es vm_panel_extendido.csv: el mismo panel de la app, con sus nombres y
# sus reglas de 0 y vacío, más las columnas de las tablas diarias. vm$panel (vm_panel.csv) queda por compatibilidad.
.vm_enriquecer <- function(p) {
  p <- p |> filter(!is.na(fecha)) |> arrange(fecha) |> complete(fecha = seq(min(fecha), max(fecha), by = "day")) |>
    mutate(dia_semana = wday(fecha, week_start = 1), fin_de_semana = dia_semana >= 6,
           semana = floor_date(fecha, "week", week_start = 1), mes = floor_date(fecha, "month"))
  if ("valencia" %in% names(p)) p <- mutate(p, valencia_ayer = lag(valencia), valencia_manana = lead(valencia))
  if ("sueno_min" %in% names(p)) p <- mutate(p, sueno_h = sueno_min / 60)
  p
}
if (!is.null(vm$panel) && nrow(vm$panel) > 0) vm$panel <- .vm_enriquecer(vm$panel)
if (!is.null(vm$panel_extendido) && nrow(vm$panel_extendido) > 0) vm$panel_extendido <- .vm_enriquecer(vm$panel_extendido)
panel <- if (!is.null(vm$panel_extendido) && nrow(vm$panel_extendido) > 0) vm$panel_extendido else vm$panel

# ---- Utilidades -------------------------------------------------------------
.vm_dato <- function(datos, var) { if (!var %in% names(datos)) stop("No existe la columna '", var, "'. Columnas: ", paste(names(datos), collapse = ", ")); datos[[var]] }
vm_describir <- function(archivo = "vm_panel.csv") vm_dic |> filter(.data$archivo == !!archivo) |> select(columna, tipo, descripcion)

#' Media móvil por días de calendario (requiere al menos un tercio de la ventana con dato).
vm_movil <- function(x, k) {
  n <- length(x); out <- rep(NA_real_, n)
  for (i in seq_len(n)) { v <- x[max(1, i - k + 1):i]; if (sum(!is.na(v)) >= max(2, k %/% 3)) out[i] <- mean(v, na.rm = TRUE) }
  out
}

vm_resumen <- function() {
  cat("VESTIGIVM MEI · carpeta:", vm_carpeta, "· zona:", vm_zona, "\n")
  for (n in names(vm)) {
    x <- vm[[n]]; f <- intersect(c("fecha", "ts", "inicio", "agregado"), names(x))[1]
    rango <- if (!is.na(f) && nrow(x)) { r <- range(x[[f]], na.rm = TRUE); paste0(format(as.Date(r[1])), " a ", format(as.Date(r[2]))) } else ""
    cat(sprintf("  %-18s %6d filas %4d columnas  %s\n", n, nrow(x), ncol(x), rango))
  }
  invisible(NULL)
}

# ---- Análisis ---------------------------------------------------------------

#' Tendencia de una variable del panel con medias móviles de 7 y 30 días.
vm_tendencia <- function(var = "valencia", datos = panel) {
  d <- datos |> transmute(fecha, y = .vm_dato(datos, var)) |> mutate(m7 = vm_movil(y, 7), m30 = vm_movil(y, 30))
  ggplot(d, aes(fecha)) + geom_point(aes(y = y), alpha = .35, size = 1) +
    geom_line(aes(y = m7), colour = "#2D6B63", na.rm = TRUE) + geom_line(aes(y = m30), colour = "#7C2433", linewidth = 1.1, na.rm = TRUE) +
    labs(title = var, subtitle = "puntos: días · verde: media de 7 días · rojo: media de 30 días", x = NULL, y = NULL) + theme_minimal()
}

#' Media e intervalo de 95% por día de la semana (solo los días de la semana con 5 o más datos).
vm_semana <- function(var = "valencia", datos = panel) {
  d <- datos |> mutate(y = .vm_dato(datos, var)) |> filter(!is.na(y)) |>
    group_by(dia = factor(wday(fecha, week_start = 1), 1:7, c("lun", "mar", "mié", "jue", "vie", "sáb", "dom"))) |>
    summarise(media = mean(y), n = n(), ee = sd(y) / sqrt(n), .groups = "drop") |>
    # Como la app (P-17): al menos 5 días de cada uno y t de Student con n − 1 grados de libertad.
    filter(n >= 5) |> mutate(t = qt(.975, n - 1), lo = media - t * ee, hi = media + t * ee) |> select(-t)
  print(d)
  ggplot(d, aes(dia, media)) + geom_col(fill = "#2D6B63") + geom_errorbar(aes(ymin = lo, ymax = hi), width = .2) + labs(title = var, x = NULL, y = NULL) + theme_minimal()
}

#' Cruza dos variables; con desfase = 1 compara x de hoy con y de mañana.
vm_cruce <- function(x, y, desfase = 0, datos = panel, metodo = "spearman") {
  d <- datos |> arrange(fecha) |> transmute(fecha, x = .vm_dato(datos, x), y = lead(.vm_dato(datos, y), desfase)) |> drop_na(x, y)
  if (nrow(d) < 5) stop("Solo hay ", nrow(d), " días con ambos datos.")
  print(cor.test(d$x, d$y, method = metodo, exact = FALSE))
  ggplot(d, aes(x, y)) + geom_jitter(width = .05, height = .05, alpha = .5) + geom_smooth(method = "lm", formula = y ~ x, se = TRUE, colour = "#7C2433") +
    labs(x = x, y = if (desfase) paste0(y, " (", desfase, " día después)") else y) + theme_minimal()
}

#' Columnas de la misma fuente o calculadas una de otra: su correlación es casi segura y no dice nada de ti.
#' (Las m_* de momentos también son familia entre sí y con n_momentos.)
vm_familias <- list(
  c("kcal", "proteina_g", "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "cafeina_mg", "gasto_kcal", "comidas",
    "primera_comida_h", "ultima_comida_h", "cafeina_ultima_h"),
  c("peso_kg", "peso_tendencia_kg"),
  c("eventos", "min_ocupado"),
  c("sueno_min", "sueno_h", "sueno_profundo_min", "sueno_rem_min", "sueno_inicio_h", "sueno_fin_h"),
  c("valencia", "valencia_muestreo"),
  c("avisos_planeados", "avisos_mostrados", "avisos_respondidos", "tasa_respuesta", "valencia_muestreo", "n_muestreos"),
  c("hrv_rmssd", "fc_reposo"),
  c("pasos", "a_pie_min", "cal_activas", "ejercicio_min", "km"),
  c("km", "vehiculo_min", "coche_min", "sin_wifi_min"),
  c("gimnasio", "gimnasio_min", "series_gimnasio", "volumen_kg", "ejercicio_min", "cal_activas"),
  c("escalada", "ejercicio_min", "cal_activas"),
  c("pantalla_min", "desbloqueos", "notificaciones", "primer_uso_h", "ultimo_uso_h"),
  c("ultimo_uso_h", "carga_noche_inicio_h", "sueno_inicio_h"),
  c("primer_uso_h", "sueno_fin_h"),
  c("llamadas", "min_hablado", "sms", "contactos"),
  c("wa_mios", "wa_otros", "wa_chats"),
  c("canciones", "musica_min", "audifonos_min", "coche_min"),
  c("yt_vistos", "busquedas"),
  c("t_max", "t_min", "precip_mm", "nubes_pct", "luz_dia_min", "sol_min"),
  c("aqi_us", "pm25")
)
#' Lo que cada campaña busca mover (columnas de vm_panel.csv): su correlación con la campaña es esperable, la elegiste para eso.
vm_campana_mueve <- list(
  sueno7 = c("sueno_min", "sueno_profundo_min", "sueno_rem_min", "sueno_inicio_h", "sueno_fin_h", "sueno_calidad"),
  temprano = c("sueno_inicio_h", "sueno_min", "ultimo_uso_h", "carga_noche_inicio_h"),
  pasos = c("pasos", "a_pie_min", "km", "cal_activas"),
  diario = c("palabras"),
  pantalla = c("pantalla_min", "desbloqueos", "primer_uso_h", "ultimo_uso_h", "notificaciones"),
  movimiento = c("ejercicio_min", "gimnasio", "gimnasio_min", "volumen_kg", "series_gimnasio", "escalada", "cal_activas"),
  leer = c("lectura", "obras_dia", "enfoque_lectura_min"),
  enfoque = c("enfoque_min", "enfoque_estudio_min", "enfoque_lectura_min", "enfoque_idiomas_min", "enfoque_tesis_min", "enfoque_trabajo_min",
              "enfoque_creativo_min", "laptop_productivo_min"),
  reaccion = c("rt_media"),
  animo = c("valencia_muestreo", "n_muestreos", "avisos_respondidos", "tasa_respuesta", "registros_animo")
)
# Las parejas que la app esconde por obvias vienen en vm_familias.csv (P-31): dos variables lo son si comparten una familia
# con lado 0, o están en lados distintos (1 y 2) de la misma. Sin ese archivo (exportaciones viejas) se usan las listas de arriba.
.vm_fam <- if (!is.null(vm$familias) && nrow(vm$familias) > 0) mutate(vm$familias, lado = as.integer(lado)) else NULL

#' Claves "a|b" (en los dos órdenes) de las parejas obvias entre `vars`, según vm_familias.csv.
.vm_pares_obvios <- function(vars) {
  if (is.null(.vm_fam)) return(character())
  f <- .vm_fam[.vm_fam$variable %in% vars, ]
  pares <- function(a, b) { e <- expand.grid(a = a, b = b, stringsAsFactors = FALSE); e <- e[e$a != e$b, ]; paste(e$a, e$b, sep = "|") }
  unique(unlist(lapply(split(f, f$familia), function(g) {
    todas <- g$variable[g$lado == 0]; uno <- g$variable[g$lado == 1]; dos <- g$variable[g$lado == 2]
    c(if (length(todas) >= 2) pares(todas, todas), if (length(uno) && length(dos)) c(pares(uno, dos), pares(dos, uno)))
  }), use.names = FALSE))
}

#' Las variables que la app cruza (vm_variables.csv); con privadas = TRUE, también el seguimiento privado. NULL sin ese archivo.
.vm_variables <- function(privadas = FALSE) {
  if (is.null(vm$variables) || !nrow(vm$variables)) return(NULL)
  v <- vm$variables
  v$variable[privadas | !(v$privada %in% c(TRUE, 1))]
}

# Lo que describe al calendario o a cuánto se registró, no a una conducta (P-32): no se cruza.
.vm_no_cruzar <- c("fecha", "semana", "mes", "dia_semana", "fin_de_semana", "dia_libre", "festivo", "valencia_ayer", "valencia_manana",
                   "con_diario", "n_muestreos")

vm_obvia <- function(a, b) {
  if (!is.null(.vm_fam)) return(paste(a, b, sep = "|") %in% .vm_pares_obvios(c(a, b)))
  mom <- function(v) startsWith(v, "m_") || v == "n_momentos"
  camp <- function(x, y) {
    if (x == "campanas_n") return(startsWith(y, "campana_"))
    if (!startsWith(x, "campana_")) return(FALSE)
    if (y == "campanas_n") return(TRUE)
    r <- sub("^campana_", "", x)
    if (startsWith(r, "hab_")) return(y == r)
    y %in% vm_campana_mueve[[r]]
  }
  (mom(a) && mom(b)) || camp(a, b) || camp(b, a) || any(map_lgl(vm_familias, ~ a %in% .x && b %in% .x))
}

#' Todas las parejas de variables del panel, como «Las relaciones más marcadas» de la app.
#' sin_tendencia: cada día menos el promedio de sus ±`ventana` días, para que dos cosas que solo cambian con las semanas
#'   (horas de luz y peso, por ejemplo) no parezcan relacionadas. Las variables casi puramente lentas se omiten.
#' n_ef: días efectivos descontando que un día se parece al anterior (Bartlett); lo y hi usan n_ef; q: corrección BH.
#' obvias = FALSE deja fuera las parejas obvias, las mismas que esconde la app (vm_familias.csv).
#' Por omisión cruza las mismas variables que la app (vm_variables.csv); privadas = TRUE suma el seguimiento privado, que la app
#' nunca cruza; variables = c(...) elige otras columnas del panel.
vm_relaciones <- function(sin_tendencia = TRUE, obvias = FALSE, min_n = 14, ventana = 10, privadas = FALSE, variables = NULL, datos = panel) {
  datos <- arrange(datos, fecha)
  if (is.null(variables)) variables <- .vm_variables(privadas)
  cand <- names(datos)[map_lgl(datos, ~ is.numeric(.x) || is.logical(.x)) & !names(datos) %in% .vm_no_cruzar]
  if (!is.null(variables)) cand <- intersect(variables, cand)
  dias <- as.numeric(datos$fecha)
  quitar <- function(x) {
    out <- rep(NA_real_, length(x)); ok <- which(!is.na(x))
    for (i in ok) { w <- ok[abs(dias[ok] - dias[i]) <= ventana]; if (length(w) >= 3) out[i] <- x[i] - mean(x[w]) }
    out
  }
  ac1 <- function(x) {
    a <- x[-length(x)]; b <- x[-1]; ok <- !is.na(a) & !is.na(b) & diff(dias) == 1
    if (sum(ok) < 10) return(0)
    r <- suppressWarnings(cor(a[ok], b[ok])); if (is.na(r)) 0 else min(max(r, -.5), .97)
  }
  S <- map(set_names(cand), function(v) {
    x <- as.numeric(datos[[v]]); if (sum(!is.na(x)) < 3) return(NULL)
    r <- quitar(x); vx <- var(x, na.rm = TRUE); vr <- var(r, na.rm = TRUE)
    list(x = x, r = r, ac_x = ac1(x), ac_r = ac1(r), binaria = all(x %in% c(0, 1, NA)),
         lenta = !is.na(vx) && vx > 0 && sum(!is.na(r)) >= min_n && !is.na(vr) && vr / vx < .12)
  }) |> compact()
  if (sin_tendencia) {
    lentas <- names(keep(S, ~ .x$lenta))
    if (length(lentas)) message("Sin tendencia se omiten (casi solo cambian con las semanas): ", paste(lentas, collapse = ", "))
  }
  vs <- names(S); if (length(vs) < 2) return(tibble())
  pares <- combn(vs, 2)
  obv <- .vm_pares_obvios(vs)
  res <- map_dfr(seq_len(ncol(pares)), function(k) {
    a <- pares[1, k]; b <- pares[2, k]; sa <- S[[a]]; sb <- S[[b]]
    ob <- if (!is.null(.vm_fam)) paste(a, b, sep = "|") %in% obv else vm_obvia(a, b)
    if (ob && !obvias) return(NULL)
    if (sin_tendencia && (sa$lenta || sb$lenta)) return(NULL)
    xa <- if (sin_tendencia) sa$r else sa$x; xb <- if (sin_tendencia) sb$r else sb$x
    ok <- !is.na(xa) & !is.na(xb); n <- sum(ok)
    if (n < min_n) return(NULL)
    if (sa$binaria && min(sum(sa$x[ok] == 1), sum(sa$x[ok] == 0)) < 4) return(NULL)
    if (sb$binaria && min(sum(sb$x[ok] == 1), sum(sb$x[ok] == 0)) < 4) return(NULL)
    r <- suppressWarnings(cor(xa[ok], xb[ok])); if (is.na(r)) return(NULL)
    ra <- if (sin_tendencia) sa$ac_r else sa$ac_x; rb <- if (sin_tendencia) sb$ac_r else sb$ac_x
    n_ef <- min(max(n * (1 - ra * rb) / (1 + ra * rb), 4), n)
    z <- atanh(min(max(r, -.9999), .9999)); se <- 1 / sqrt(n_ef - 3)
    tibble(a = a, b = b, r = r, bruta = suppressWarnings(cor(sa$x[ok], sb$x[ok])), n = n, n_ef = round(n_ef, 1),
           lo = tanh(z - 1.96 * se), hi = tanh(z + 1.96 * se), p = 2 * pnorm(-abs(z / se)), obvia = ob)
  })
  if (!nrow(res)) return(res)
  res |> mutate(q = p.adjust(p, "BH"), firme = q < .1, seguro = pmax(lo, -hi, 0)) |>
    arrange(desc(seguro), desc(abs(r))) |> select(-seguro)
}

# Piezas comunes con la app (ciencia/Estadistica.kt), para que R dé los mismos intervalos (P-17).
#' Cada día menos el promedio de sus ±ventana días de calendario con dato (si hay al menos 3), como sinTendencia() de la app.
.vm_sin_tendencia <- function(f, x, ventana = 10) {
  d <- as.numeric(f); out <- rep(NA_real_, length(x)); ok <- which(!is.na(x))
  for (i in ok) { w <- ok[abs(d[ok] - d[i]) <= ventana]; if (length(w) >= 3) out[i] <- x[i] - mean(x[w]) }
  out
}
#' Autocorrelación de un día al siguiente (solo días seguidos); NULL con menos de 10 pares, como ar1() de la app.
.vm_ar1 <- function(f, x) {
  m <- match(f + 1, f); ok <- !is.na(x) & !is.na(m) & !is.na(x[m])
  if (sum(ok) < 10) return(NULL)
  r <- suppressWarnings(cor(x[ok], x[m][ok])); if (is.na(r)) NULL else r
}
#' Diferencia de medias entre dos grupos de días (fechas fa y fb), con t de Welch y el error estándar corregido por la
#' autocorrelación de cada grupo (más la corrección de Marriott y Pope para series cortas): diferenciaEnDias() de la app.
.vm_dif_dias <- function(fa, ya, fb, yb) {
  if (length(ya) < 3 || length(yb) < 3) return(NULL)
  oa <- order(fa); ob <- order(fb); fa <- fa[oa]; ya <- ya[oa]; fb <- fb[ob]; yb <- yb[ob]
  corregida <- function(f, y) { r <- .vm_ar1(f, y); if (is.null(r)) NULL else r + (1 + 4 * r) / length(y) }
  fj <- c(fa, fb); yj <- c(ya - mean(ya), yb - mean(yb)); o <- order(fj); fj <- fj[o]; yj <- yj[o]; u <- !duplicated(fj)
  comun <- corregida(fj[u], yj[u]); if (is.null(comun)) comun <- 0
  ra <- corregida(fa, ya); if (is.null(ra)) ra <- comun
  rb <- corregida(fb, yb); if (is.null(rb)) rb <- comun
  ra <- min(max(ra, 0), .9); rb <- min(max(rb, 0), .9)
  na <- max(length(ya) * (1 - ra) / (1 + ra), 2); nb <- max(length(yb) * (1 - rb) / (1 + rb), 2)
  sa <- var(ya) / length(ya) * (1 + ra) / (1 - ra); sb <- var(yb) / length(yb) * (1 + rb) / (1 - rb)
  se <- sqrt(sa + sb); d <- mean(ya) - mean(yb)
  if (se == 0) return(list(d = d, lo = d, hi = d, se = 0, gl = na + nb - 2, p = if (d == 0) 1 else 0))
  gl <- min(max((sa + sb)^2 / (sa^2 / (na - 1) + sb^2 / (nb - 1)), 1), na + nb - 2)
  t <- qt(.975, gl)
  list(d = d, lo = d - t * se, hi = d + t * se, se = se, gl = gl, p = 2 * pt(-abs(d / se), gl))
}

#' Todo lo que se mueve con un objetivo, como «Todo lo que se mueve con algo» de la app (P-17).
#' sin_tendencia = TRUE: cada día se compara con sus semanas cercanas (objetivo y variable). Las variables continuas, con r de
#' Pearson y el n efectivo (n_ef, que descuenta que un día se parece al anterior); las 0/1, con la diferencia de medias del
#' objetivo entre los días con y sin, con t de Welch y el error estándar corregido por autocorrelación. q: corrección BH.
#' Se omiten las parejas obvias con el objetivo (la misma medida por otra vía, una parte y su total…).
vm_factores <- function(objetivo = "valencia", desfase = 0, sin_tendencia = TRUE, min_n = 14, ventana = 10, privadas = FALSE,
                        variables = NULL, datos = panel) {
  datos <- datos |> filter(!is.na(fecha)) |> arrange(fecha)
  f <- datos$fecha
  y0 <- as.numeric(.vm_dato(datos, objetivo))
  y <- if (sin_tendencia) .vm_sin_tendencia(f, y0, ventana) else y0
  rY <- .vm_ar1(f, y); if (is.null(rY)) rY <- 0
  yEn <- y[match(f + desfase, f)]
  if (is.null(variables)) variables <- .vm_variables(privadas)
  cand <- names(datos)[map_lgl(datos, ~ is.numeric(.x) || is.logical(.x)) & !names(datos) %in% c(.vm_no_cruzar, objetivo)]
  if (!is.null(variables)) cand <- intersect(variables, cand)
  obv <- if (!is.null(.vm_fam)) .vm_pares_obvios(c(cand, objetivo)) else character()
  misma <- unlist(keep(vm_familias, ~ objetivo %in% .x))
  res <- map_dfr(cand, function(v) {
    if (if (!is.null(.vm_fam)) paste(v, objetivo, sep = "|") %in% obv else v %in% misma || vm_obvia(v, objetivo)) return(NULL)
    x0 <- as.numeric(datos[[v]]); hay <- !is.na(x0)
    if (sum(hay) < 10) return(NULL)
    if (all(x0[hay] %in% c(0, 1))) {
      con <- hay & x0 == 1 & !is.na(yEn); sin <- hay & x0 == 0 & !is.na(yEn)
      if (sum(con) < 5 || sum(sin) < 5) return(NULL)
      d <- .vm_dif_dias(f[con], yEn[con], f[sin], yEn[sin])
      if (is.null(d) || d$se <= 0) return(NULL)
      tibble(variable = v, tipo = "0/1", efecto = d$d, lo = d$lo, hi = d$hi, n = sum(con) + sum(sin), n_con = sum(con), n_sin = sum(sin),
             n_ef = NA_real_, p = d$p)
    } else {
      x <- if (sin_tendencia) .vm_sin_tendencia(f, x0, ventana) else x0
      ok <- !is.na(x) & !is.na(yEn); n <- sum(ok)
      if (n < min_n) return(NULL)
      r <- suppressWarnings(cor(x[ok], yEn[ok])); if (is.na(r)) return(NULL)
      ra <- .vm_ar1(f, x); if (is.null(ra)) ra <- 0
      ra <- min(max(ra, -.5), .97); rb <- min(max(rY, -.5), .97)
      n_ef <- min(max(n * (1 - ra * rb) / (1 + ra * rb), min(4, n)), n)
      if (n_ef <= 3) return(NULL)
      z <- atanh(min(max(r, -.9999), .9999)); se <- 1 / sqrt(n_ef - 3)
      tibble(variable = v, tipo = "r", efecto = r, lo = tanh(z - 1.96 * se), hi = tanh(z + 1.96 * se), n = n, n_con = NA_integer_,
             n_sin = NA_integer_, n_ef = round(n_ef, 1), p = 2 * pnorm(-abs(z / se)))
    }
  })
  if (!nrow(res)) return(res)
  dic <- vm_dic |> filter(archivo %in% c("vm_panel_extendido.csv", "vm_panel.csv")) |> distinct(variable = columna, .keep_all = TRUE) |> select(variable, descripcion)
  res |> mutate(q = p.adjust(p, "BH"), firme = q < .1) |> arrange(p) |> left_join(dic, by = "variable")
}

#' Regresión con variables estandarizadas; con ayer = TRUE controla por el objetivo del día anterior.
vm_modelo <- function(objetivo = "valencia", predictores = c("sueno_h", "pasos", "pantalla_min"), ayer = TRUE, datos = panel) {
  d <- datos |> arrange(fecha) |> mutate(.ayer = lag(.data[[objetivo]]))
  vars <- c(predictores, if (ayer) ".ayer")
  d <- d |> select(all_of(c(objetivo, vars))) |> drop_na() |> mutate(across(everything(), ~ as.numeric(scale(as.numeric(.x)))))
  if (nrow(d) < length(vars) + 10) stop("Hay ", nrow(d), " días completos; hacen falta más.")
  m <- lm(reformulate(paste0("`", vars, "`"), response = objetivo), data = d)
  print(summary(m)); invisible(m)
}

#' Mismo modelo, pero con errores autocorrelacionados AR(1): respeta que cada día se parece al anterior.
vm_modelo_ar1 <- function(objetivo = "valencia", predictores = c("sueno_h", "pasos", "pantalla_min"), datos = panel) {
  if (!requireNamespace("nlme", quietly = TRUE)) stop("Instala nlme: install.packages('nlme')")
  d <- datos |> arrange(fecha) |> mutate(t = as.numeric(fecha - min(fecha))) |> select(all_of(c("t", objetivo, predictores))) |> drop_na() |>
    mutate(across(-t, ~ as.numeric(scale(as.numeric(.x)))))
  m <- nlme::gls(reformulate(predictores, response = objetivo), data = d, correlation = nlme::corAR1(form = ~ t), method = "ML")
  print(summary(m)); invisible(m)
}

#' Variabilidad, inestabilidad (MSSD) e inercia (autocorrelación de un día al siguiente) del ánimo, en total y por mes.
vm_dinamica <- function(var = "valencia", datos = panel) {
  d <- datos |> arrange(fecha) |> transmute(fecha, mes, y = .vm_dato(datos, var), y1 = lead(y))
  f <- function(y, y1) tibble(n = sum(!is.na(y)), media = mean(y, na.rm = TRUE), de = sd(y, na.rm = TRUE),
                              mssd = mean((y1 - y)^2, na.rm = TRUE), inercia = suppressWarnings(cor(y, y1, use = "complete.obs")))
  print(f(d$y, d$y1))
  d |> group_by(mes) |> summarise(f(y, y1), .groups = "drop")
}

#' Ritmo de sueño desde el reloj: horas medias, regularidad y jet lag social (Wittmann et al., 2006).
vm_ritmo <- function(dias = 56) {
  if (is.null(vm$salud) || !all(c("sueno_inicio", "sueno_fin") %in% names(vm$salud))) stop("No hay datos de sueño del reloj.")
  h <- function(t) hour(t) + minute(t) / 60
  # Como la app: las noches de los últimos `dias` días (dias = Inf para toda la historia). Una noche es libre si el día en que
  # despiertas es libre; se lee de la columna dia_libre del panel (fin de semana o festivo), con la misma regla que la app.
  hasta <- max(vm$salud$fecha, na.rm = TRUE)
  libres <- if (!is.null(vm$panel$dia_libre)) vm$panel$fecha[vm$panel$dia_libre %in% c(TRUE, 1)] else NULL
  s <- vm$salud |> filter(!is.na(sueno_inicio), !is.na(sueno_fin), fecha > hasta - dias) |>
    mutate(ini = h(sueno_inicio), ini = if_else(ini < 12, ini + 24, ini), fin = h(sueno_fin) + 24, dur = fin - ini,
           medio = (ini + fin) / 2, libre = if (is.null(libres)) wday(fecha, week_start = 1) >= 6 else fecha %in% libres) |> filter(dur > 2, dur < 16)
  r <- s |> group_by(libre) |> summarise(noches = n(), dormir = mean(ini) %% 24, despertar = mean(fin) %% 24, horas = mean(dur), punto_medio = mean(medio), .groups = "drop")
  print(r)
  if (nrow(r) == 2) cat("Jet lag social:", round(abs(diff(r$punto_medio)), 2), "horas\n")
  cat("Regularidad (DE de la hora de dormir):", round(sd(s$ini) * 60), "minutos\n")
  ggplot(s, aes(y = fecha)) + geom_linerange(aes(xmin = ini, xmax = fin, colour = libre), linewidth = 2) +
    scale_x_continuous(breaks = seq(18, 38, 4), labels = function(x) paste0(x %% 24, ":00")) +
    scale_colour_manual(values = c(`FALSE` = "#7C2433", `TRUE` = "#2D6B63"), labels = c("antes de laborar", "antes de un día libre"), name = NULL) +
    labs(x = NULL, y = NULL, title = "Noches") + theme_minimal()
}

#' Palabras del diario asociadas con días buenos y difíciles, como la app (P-32): las mismas palabras vacías (sin importar
#' acentos) y la misma referencia, la valencia media de los días con nota.
vm_palabras <- function(min_dias = 5) {
  if (is.null(vm$dias)) stop("No hay diario.")
  vacias <- c("a", "al", "algo", "algunas", "algunos", "ante", "antes", "aquel", "aquella", "aquello", "aqui", "aquí", "asi", "así", "aun", "aún",
    "bien", "cada", "casi", "como", "cómo", "con", "contra", "cual", "cuando", "cuándo", "de", "del", "desde", "donde", "dónde", "dos", "el", "él",
    "ella", "ellas", "ellos", "en", "entre", "era", "eran", "es", "esa", "esas", "ese", "eso", "esos", "esta", "está", "estaba", "estaban", "estar",
    "este", "esto", "estos", "estoy", "fue", "fueron", "fui", "ha", "habia", "había", "han", "hasta", "hay", "hice", "hizo", "la", "las", "le", "les",
    "lo", "los", "mas", "más", "me", "mi", "mí", "mis", "mucho", "muy", "nada", "ni", "no", "nos", "nosotros", "o", "otra", "otras", "otro", "otros",
    "para", "pero", "poco", "por", "porque", "que", "qué", "quien", "quién", "se", "sea", "ser", "si", "sí", "sido", "sin", "sobre", "solo", "sólo",
    "son", "su", "sus", "también", "tan", "tanto", "te", "tengo", "tenia", "tenía", "ti", "tiene", "tienen", "todo", "todos", "tu", "tú", "tus", "un",
    "una", "unas", "uno", "unos", "usted", "ya", "yo", "hoy", "dia", "día", "luego", "despues", "después", "creo", "pues", "igual", "ahora", "vez",
    "veces", "cosa", "cosas", "hacer", "hace", "estuve", "estuvo", "tuve", "tuvo", "voy", "iba", "ir", "fui", "fue", "pude", "puedo", "quiero",
    "queria", "quería", "siento", "senti", "sentí")
  sin_acentos <- function(x) stringi::stri_trans_general(x, "NFD; [:Nonspacing Mark:] Remove; NFC")
  vacias <- unique(sin_acentos(vacias))
  con_nota <- vm$dias |> filter(!is.na(nota), nzchar(trimws(nota)))
  d <- con_nota |> select(fecha, valencia, nota) |>
    mutate(p = str_extract_all(str_to_lower(nota), "[\\p{L}]{3,}")) |> select(-nota) |> unnest(p) |> filter(!sin_acentos(p) %in% vacias) |> distinct(fecha, valencia, p)
  media <- mean(con_nota$valencia, na.rm = TRUE)
  d |> filter(!is.na(valencia)) |> group_by(palabra = p) |> summarise(dias = n(), media = mean(valencia), .groups = "drop") |>
    filter(dias >= min_dias) |> mutate(diferencia = media - !!media) |> arrange(desc(diferencia))
}

#' Días que se salen de lo normal (z robusto con mediana y MAD), en las mismas variables que revisa la app.
vm_atipicos <- function(vars = c("valencia", "sueno_horas", "pasos", "pantalla_min", "desbloqueos", "gasto_total", "notificaciones", "fotos",
                                 "distancia_km", "min_hablado", "wa_mios", "musica_min", "fc_reposo", "hrv", "palabras"), z = 3, datos = panel) {
  vars <- intersect(vars, names(datos))
  datos |> select(fecha, all_of(vars)) |> pivot_longer(-fecha, names_to = "variable", values_to = "valor") |> filter(!is.na(valor)) |>
    group_by(variable) |> mutate(z = (valor - median(valor)) / mad(valor)) |> ungroup() |> filter(is.finite(z), abs(z) >= !!z) |> arrange(desc(fecha))
}

# ---- Totales y búsqueda (como la pantalla Totales de la app) ------------------
.vm_norm <- function(x) tolower(stringi::stri_trans_general(coalesce(as.character(x), ""), "Latin-ASCII"))
.vm_rango <- function(d, desde = NULL, hasta = NULL, col = "fecha") {
  if (is.null(d) || !nrow(d) || !col %in% names(d)) return(d)
  f <- as.Date(d[[col]]); ok <- !is.na(f)
  if (!is.null(desde)) ok <- ok & f >= as.Date(desde)
  if (!is.null(hasta)) ok <- ok & f <= as.Date(hasta)
  d[ok, ]
}

#' Suma de cada medida diaria del periodo: total, días con dato, desde y hasta cuándo hay datos y promedio por día con dato.
#' Ejemplo: vm_totales("2026-01-01") |> filter(variable %in% c("km", "pasos", "kcal"))
vm_totales <- function(desde = NULL, hasta = NULL, datos = panel) {
  sumables <- c("km", "pasos", "a_pie_min", "vehiculo_min", "ejercicio_min", "sueno_min", "cal_activas", "kcal", "proteina_g",
                "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "cafeina_mg", "comidas", "volumen_kg", "series_gimnasio",
                "gasto_total", "palabras", "n_momentos", "n_muestreos", "avisos_planeados", "avisos_mostrados", "avisos_respondidos", "pantalla_min", "desbloqueos", "notificaciones", "fotos",
                "canciones", "musica_min", "audifonos_min", "yt_vistos", "busquedas", "llamadas", "min_hablado", "sms", "wa_mios",
                "wa_otros", "eventos", "min_ocupado", "subrayados", "precip_mm", "sol_min")
  d <- .vm_rango(datos, desde, hasta)
  map_dfr(intersect(sumables, names(d)), function(v) {
    x <- as.numeric(d[[v]]); hay <- !is.na(x) & x > 0
    if (!any(hay)) return(NULL)
    tibble(variable = v, total = sum(x, na.rm = TRUE), dias = sum(!is.na(x)), desde = min(d$fecha[hay]), hasta = max(d$fecha[hay]),
           por_dia = mean(x, na.rm = TRUE))
  }) |> left_join(vm_dic |> filter(archivo == "vm_panel.csv") |> select(variable = columna, descripcion), by = "variable")
}

#' Busca un texto (sin importar acentos ni mayúsculas) en alimentos, personas, temas, artistas, canciones, lugares,
#' ejercicios, canales de YouTube, búsquedas, gastos, libros y chats. Una fila por coincidencia, con total, veces,
#' días y primera/última fecha. Los alimentos de MacroFactor vienen en inglés: vm_buscar("banana").
vm_buscar <- function(texto, desde = NULL, hasta = NULL) {
  q <- .vm_norm(sub("^[@#]", "", texto))
  hay <- function(x) str_detect(.vm_norm(x), fixed(q))
  # «@ana» busca solo personas y «#viaje» solo temas.
  solo <- if (startsWith(texto, "@")) "personas" else if (startsWith(texto, "#")) "temas" else NULL
  junta <- function(d, indice, unidad, total, ...) {
    if (is.null(d) || !nrow(d)) return(NULL)
    d |> mutate(.uno = 1) |> summarise(indice = indice, total = sum({{ total }}, na.rm = TRUE), unidad = unidad, veces = n(), dias = n_distinct(fecha),
                   primera = min(fecha), ultima = max(fecha), ..., .groups = "drop")
  }
  out <- list()
  if (!is.null(vm$comidas)) {
    d <- .vm_rango(vm$comidas, desde, hasta) |> filter(hay(alimento))
    if (!"kcal" %in% names(d)) d$kcal <- NA_real_
    out$a <- junta(group_by(d, nombre = alimento), "alimentos", if (all(is.na(d$gramos))) "ml" else "g", coalesce(gramos, mililitros),
                   kcal = sum(kcal, na.rm = TRUE))
  }
  for (col in c("personas", "temas")) {
    m <- bind_rows(lapply(list(vm$dias, vm$momentos), function(t) {
      if (is.null(t) || !col %in% names(t)) return(NULL)
      .vm_rango(t, desde, hasta) |> transmute(fecha, x = vm_lista(.data[[col]])) |> unnest(x)
    }))
    if (nrow(m)) out[[col]] <- junta(m |> filter(hay(x)) |> group_by(nombre = paste0(if (col == "personas") "@" else "#", x)), col, "menciones", .uno)
  }
  if (!is.null(vm$musica)) {
    d <- .vm_rango(vm$musica, desde, hasta) |> filter(is.na(ms_escuchado) | ms_escuchado >= 30000) |>
      mutate(min = coalesce(ms_escuchado / 60000, duracion_s / 60))
    out$art <- junta(d |> filter(hay(artista)) |> group_by(nombre = artista), "artistas", "reproducciones", .uno, minutos = sum(min, na.rm = TRUE))
    out$can <- junta(d |> filter(hay(titulo)) |> group_by(nombre = paste(titulo, "—", artista)), "canciones", "reproducciones", .uno, minutos = sum(min, na.rm = TRUE))
  }
  if (!is.null(vm$visitas)) {
    d <- vm$visitas |> mutate(fecha = as.Date(format(entrada, tz = vm_zona))) |> .vm_rango(desde, hasta) |> filter(hay(lugar))
    out$lug <- junta(group_by(d, nombre = lugar), "lugares", "h", minutos / 60)
  }
  if (!is.null(vm$series)) {
    d <- .vm_rango(vm$series, desde, hasta) |> filter(hay(ejercicio), !coalesce(as.logical(calentamiento), FALSE))
    out$ej <- junta(group_by(d, nombre = ejercicio), "ejercicios", "series", .uno, volumen_kg = sum(peso_kg * reps, na.rm = TRUE),
                    e1rm_kg = suppressWarnings(max(e1rm_kg, na.rm = TRUE)))
  }
  if (!is.null(vm$google)) {
    d <- .vm_rango(vm$google, desde, hasta)
    out$can_yt <- junta(d |> filter(producto == "youtube", accion == "visto", hay(canal)) |> group_by(nombre = canal), "canales", "videos", .uno)
    out$bus <- junta(d |> filter(accion == "buscado", hay(titulo)) |> group_by(nombre = titulo), "busquedas", "veces", .uno)
  }
  if (!is.null(vm$gastos)) out$gas <- junta(.vm_rango(vm$gastos, desde, hasta) |> filter(hay(categoria) | hay(nota)) |> group_by(nombre = categoria), "gastos", "$", monto)
  if (!is.null(vm$subrayados)) out$lib <- junta(.vm_rango(vm$subrayados, desde, hasta) |> filter(hay(libro)) |> group_by(nombre = libro), "libros", "subrayados", .uno)
  if (!is.null(vm$whatsapp)) out$wa <- junta(.vm_rango(vm$whatsapp, desde, hasta) |> filter(hay(chat)) |> group_by(nombre = chat), "chats", "mensajes", mios + otros)
  res <- bind_rows(compact(out))
  if (!is.null(solo) && nrow(res)) res <- filter(res, indice == solo)
  if (!nrow(res)) { message("Nada con «", texto, "» en ese periodo."); return(res) }
  res |> relocate(indice, nombre) |> arrange(indice, desc(veces))
}

#' Progreso de fuerza: 1RM estimado máximo por sesión para los ejercicios que más repites.
vm_fuerza <- function(n = 6) {
  if (is.null(vm$series)) stop("No hay series de MacroFactor Workouts.")
  d <- vm$series |> filter(!is.na(e1rm_kg)) |> group_by(ejercicio, fecha) |> summarise(e1rm = max(e1rm_kg), .groups = "drop")
  top <- d |> count(ejercicio, sort = TRUE) |> slice_head(n = n) |> pull(ejercicio)
  ggplot(filter(d, ejercicio %in% top), aes(fecha, e1rm)) + geom_line(colour = "#7C2433") + geom_point(size = 1) +
    facet_wrap(~ ejercicio, scales = "free_y") + labs(x = NULL, y = "1RM estimado (kg)") + theme_minimal()
}

#' Registros de ánimo con lugar, coloreados por su valencia (sin mapa de fondo).
vm_mapa_animo <- function() {
  if (is.null(vm$momentos)) stop("No hay momentos.")
  # Solo registros de ánimo con su propia valencia: ni el seguimiento privado (impulso, recaída, libido) ni la valencia del diario prestada.
  d <- vm$momentos |> filter(!is.na(lat), !is.na(lon), as.logical(es_animo) %in% TRUE, !tipo %in% c("impulso", "recaida", "libido")) |>
    mutate(v = as.numeric(valencia)) |> filter(!is.na(v))
  ggplot(d, aes(lon, lat, colour = v)) + geom_point(alpha = .7) + scale_colour_gradient2(low = "#7C2433", mid = "grey85", high = "#2D6B63", midpoint = 4) + coord_quickmap() + theme_minimal()
}

#' El día por dentro: el promedio de una columna de vm_horas.csv en cada hora (0 a 23), en días laborables y libres.
#' Día libre = la columna dia_libre del panel (fin de semana o festivo), la misma regla de la app; sin ella, el fin de semana.
vm_por_hora <- function(var = "valencia") {
  if (is.null(vm$horas)) stop("No hay vm_horas.csv.")
  libres <- if (!is.null(vm$panel$dia_libre)) vm$panel$fecha[vm$panel$dia_libre %in% c(TRUE, 1)] else NULL
  d <- vm$horas |> mutate(y = as.numeric(.vm_dato(vm$horas, var)), libre = if (is.null(libres)) dia_semana >= 6 else as.Date(fecha) %in% libres) |>
    filter(!is.na(y))
  r <- d |> group_by(hora, libre) |> summarise(media = mean(y), n = n(), .groups = "drop")
  ggplot(r, aes(hora, media, colour = libre)) + geom_line() + geom_point(aes(size = n), alpha = .6) +
    scale_colour_manual(values = c(`FALSE` = "#7C2433", `TRUE` = "#2D6B63"), labels = c("día laborable", "día libre"), name = NULL) +
    scale_x_continuous(breaks = seq(0, 23, 3)) + labs(x = "hora", y = var, size = "horas") + theme_minimal()
}

#' Dos columnas de vm_horas.csv en la misma hora (o con [desfase] horas después): por ejemplo, la cafeína en el cuerpo y la energía.
vm_cruce_horas <- function(x = "cafeina_mg", y = "activacion", desfase = 0) {
  if (is.null(vm$horas)) stop("No hay vm_horas.csv.")
  d <- vm$horas |> arrange(fecha, hora) |> mutate(a = as.numeric(.vm_dato(vm$horas, x)), b = lead(as.numeric(.vm_dato(vm$horas, y)), desfase)) |> filter(!is.na(a), !is.na(b))
  if (nrow(d) < 10) stop("Muy pocas horas con ambos datos (", nrow(d), ").")
  ct <- suppressWarnings(cor.test(d$a, d$b, method = "spearman", exact = FALSE))
  cat(x, "vs", y, if (desfase) paste0("(", desfase, " h después)") else "", ": rho =", round(ct$estimate, 3), " p =", signif(ct$p.value, 3), " n =", nrow(d), "horas\n")
  ggplot(d, aes(a, b)) + geom_jitter(alpha = .35, width = 0, height = .15, colour = "#7C2433") + geom_smooth(method = "loess", formula = y ~ x, colour = "#2D6B63") +
    labs(x = x, y = y) + theme_minimal()
}

#' Sustancias: días con cada una, nivel máximo, horas y cruda; y la cruda frente a lo que tomaste la noche anterior.
vm_sustancias <- function() {
  if (is.null(vm$sustancias) || nrow(vm$sustancias) == 0) stop("No hay registros de sustancias.")
  print(vm$sustancias |> group_by(sustancia) |> summarise(registros = n(), dias = n_distinct(fecha), nivel_max_medio = mean(nivel_max, na.rm = TRUE),
                                                          horas_medias = mean(duracion_h, na.rm = TRUE), cruda_media = mean(cruda, na.rm = TRUE), .groups = "drop"))
  a <- vm$sustancias |> filter(sustancia == "alcohol", !is.na(cruda))
  if (nrow(a) >= 3) ggplot(a, aes(coalesce(cantidad, 0), cruda)) + geom_jitter(width = .1, height = .1, colour = "#7C2433") +
    labs(x = "bebidas estándar", y = "cruda (0 a 5)") + theme_minimal()
}

#' Lugares: horas por tipo de lugar en cada semana y la valencia de tus registros de ánimo en cada tipo.
vm_lugares <- function() {
  cols <- grep("^horas_tipo_", names(vm$panel), value = TRUE)
  if (!length(cols)) stop("No hay lugares con tipo todavía.")
  h <- vm$panel |> select(fecha, all_of(cols)) |> pivot_longer(-fecha, names_to = "tipo", values_to = "horas") |>
    mutate(tipo = sub("^horas_tipo_", "", tipo), semana = lubridate::floor_date(fecha, "week", week_start = 1)) |>
    group_by(semana, tipo) |> summarise(horas = sum(horas, na.rm = TRUE), .groups = "drop")
  if (!is.null(vm$momentos) && "tipo_lugar" %in% names(vm$momentos))
    print(vm$momentos |> filter(es_animo, !is.na(tipo_lugar), !is.na(valencia)) |> group_by(tipo_lugar) |>
            summarise(registros = n(), valencia_media = mean(valencia), .groups = "drop") |> arrange(desc(valencia_media)))
  ggplot(h, aes(semana, horas, fill = tipo)) + geom_col() + labs(x = NULL, y = "horas por semana", fill = "tipo de lugar") + theme_minimal()
}

#' Cuánto después de que pasó se anotó cada registro (desde la v14): minutos de retraso y si fue «tarde», es decir,
#' más de `umbral_min` minutos después o en otro día lógico. Sirve para momentos, sustancias, tramos, suplementos,
#' gastos e ingresos. Los registros de antes de la v14 no tienen la hora de anotación y quedan con NA.
#' El umbral es una decisión propia, no un estándar: cámbialo si quieres.
vm_retraso <- function(tabla = "momentos", umbral_min = 60) {
  x <- vm[[tabla]]
  if (is.null(x) || nrow(x) == 0) stop("No hay datos en vm$", tabla, ".")
  if (!"ts_creado" %in% names(x)) stop("La tabla ", tabla, " no tiene ts_creado (se exporta desde la v14).")
  .fh <- function(v) if (inherits(v, "POSIXct")) v else vm_fecha_hora(v)
  creado <- .fh(x$ts_creado)
  paso <- if ("ts" %in% names(x)) .fh(x$ts) else if ("inicio" %in% names(x)) .fh(x$inicio) else
    parse_date_time(paste(x$fecha, x$hora), orders = c("Ymd HMS", "Ymd HM"), tz = vm_zona, quiet = TRUE)
  dia <- as.Date(if ("dia_logico" %in% names(x)) x$dia_logico else x$fecha)
  # El día lógico empieza a las 05:00: lo anotado de madrugada cuenta para el día anterior.
  dia_creado <- as.Date(creado - hours(5), tz = vm_zona)
  x |> mutate(retraso_min = as.numeric(difftime(creado, paso, units = "mins")),
              otro_dia = !is.na(creado) & dia_creado != dia,
              tarde = !is.na(creado) & (coalesce(retraso_min > umbral_min, FALSE) | otro_dia))
}

#' Barómetro: la valencia del día contra el mayor cambio de presión en tres horas.
vm_presion <- function() {
  if (!"cambio_presion_3h" %in% names(vm$panel)) stop("No hay datos del barómetro.")
  d <- vm$panel |> filter(!is.na(cambio_presion_3h), !is.na(valencia))
  print(cor.test(d$cambio_presion_3h, d$valencia, method = "spearman", exact = FALSE))
  ggplot(d, aes(cambio_presion_3h, valencia)) + geom_jitter(width = 0, height = .15, alpha = .5) + geom_smooth(method = "loess", se = FALSE) +
    labs(x = "cambio de presión en 3 h (hPa)", y = "valencia del día") + theme_minimal()
}

#' Tu red personal con igraph: densidad, restricción de Burt (qué tanto tus contactos se conocen entre sí) y composición por relación.
#' Solo con las líneas que dijiste (declaradas = TRUE) o también con quienes aparecen juntos al menos `min_juntos` días.
#' Requiere igraph:  install.packages("igraph")
#' Como la app (P-32): «no se conocen» nunca es línea, aunque aparezcan juntas; los grupos no cuentan a quien no tiene ninguna
#' línea (van aparte, en aisladas). La app pide 2 días juntas y dibuja 45 personas; la exportación trae hasta 400.
vm_red <- function(declaradas = FALSE, min_juntos = 2) {
  if (is.null(vm$red_nodos) || !nrow(vm$red_nodos)) stop("No hay red: menciona personas con @ o di quién conoce a quién en Tu gente.")
  v <- vm$red_vinculos
  if (is.null(v) || !nrow(v)) v <- tibble(a = character(), b = character(), tipo_declarado = character(), dias_juntos = integer())
  no <- !is.na(v$tipo_declarado) & v$tipo_declarado == "no_se_conocen"
  v <- v[!no & (!is.na(v$tipo_declarado) | (!declaradas & v$dias_juntos >= min_juntos)), ]
  g <- igraph::graph_from_data_frame(as.data.frame(v[, c("a", "b")]), directed = FALSE, vertices = as.data.frame(vm$red_nodos))
  # Tú (ego) unido a todas: así la restricción de Burt se calcula sobre tu red personal.
  g_ego <- igraph::add_vertices(g, 1, name = "yo")
  g_ego <- igraph::add_edges(g_ego, as.vector(rbind("yo", igraph::V(g)$name)))
  r <- list(
    densidad = igraph::edge_density(g),
    componentes = sum(igraph::components(g)$csize > 1),
    aisladas = sum(igraph::components(g)$csize == 1),
    restriccion_burt = unname(igraph::constraint(g_ego)[igraph::V(g_ego)$name == "yo"]),
    grado = sort(igraph::degree(g), decreasing = TRUE),
    # Composición y color por grupo de relación (familia, pareja, amistad, trabajo…), como la Red de la app; las exportaciones
    # de antes de la columna «grupo» usan la relación tal cual.
    composicion = table(if (!is.null(vm$red_nodos$grupo)) vm$red_nodos$grupo else vm$red_nodos$relacion, useNA = "ifany")
  )
  print(r[c("densidad", "componentes", "aisladas", "restriccion_burt")])
  grupo <- if (!is.null(vm$red_nodos$grupo)) vm$red_nodos$grupo else vm$red_nodos$relacion
  plot(g, vertex.size = 4 + 2 * sqrt(vm$red_nodos$dias_mencion), vertex.label.cex = .7, vertex.color = as.integer(factor(grupo)))
  invisible(r)
}

#' Minutos en la laptop por categoría y semana (Cold Turkey).
vm_laptop <- function() {
  if (is.null(vm$laptop)) stop("No hay datos de Cold Turkey.")
  vm$laptop |> mutate(semana = lubridate::floor_date(fecha, "week", week_start = 1)) |>
    filter(!(tipo == "app" & nombre %in% c("chrome", "msedge", "firefox", "opera", "brave"))) |>
    group_by(semana, categoria) |> summarise(horas = sum(minutos) / 60, .groups = "drop") |>
    ggplot(aes(semana, horas, fill = categoria)) + geom_col() + labs(x = NULL, y = "horas por semana") + theme_minimal()
}

#' Lee vm_datos.sqlite (la exportación completa en una sola base) como una lista de tablas, con fechas y lógicos convertidos.
#' Requiere DBI y RSQLite:  install.packages(c("DBI", "RSQLite"))
vm_leer_sqlite <- function(ruta = file.path(vm_carpeta, "vm_datos.sqlite")) {
  if (!file.exists(ruta)) stop("No encontré ", ruta)
  con <- DBI::dbConnect(RSQLite::SQLite(), ruta)
  on.exit(DBI::dbDisconnect(con))
  dic <- tibble::as_tibble(DBI::dbReadTable(con, "diccionario"))
  tablas <- setdiff(DBI::dbListTables(con), "diccionario")
  out <- lapply(setNames(tablas, tablas), function(t) {
    x <- tibble::as_tibble(DBI::dbReadTable(con, t))
    d <- dic[dic$archivo == paste0("vm_", t, ".csv") & dic$columna %in% names(x), ]
    for (cc in d$columna[d$tipo == "fecha"]) x[[cc]] <- as.Date(x[[cc]])
    for (cc in d$columna[d$tipo == "fecha_hora"]) x[[cc]] <- vm_fecha_hora(x[[cc]])
    for (cc in d$columna[d$tipo == "logico"]) x[[cc]] <- x[[cc]] == 1
    x
  })
  out$diccionario <- dic
  out
}

#' Guarda las gráficas principales como PNG en una subcarpeta.
vm_guardar_graficas <- function(carpeta = file.path(vm_carpeta, "graficas")) {
  dir.create(carpeta, showWarnings = FALSE)
  g <- list(tendencia_valencia = function() vm_tendencia("valencia"), semana_valencia = function() vm_semana("valencia"),
            ritmo = function() vm_ritmo(), mapa_animo = function() vm_mapa_animo())
  for (n in names(g)) tryCatch(ggsave(file.path(carpeta, paste0(n, ".png")), suppressMessages(g[[n]]()), width = 8, height = 5, dpi = 150),
                               error = function(e) message(n, ": ", conditionMessage(e)))
  message("Gráficas en ", carpeta)
}

vm_resumen()
message("\nListo. Tablas: ", paste(names(vm), collapse = ", "),
        "\nFunciones: vm_tendencia(), vm_semana(), vm_cruce(x, y, desfase = 1), vm_relaciones(), vm_factores(), vm_totales(), vm_buscar('texto'), vm_modelo(), vm_modelo_ar1(), vm_fuerza(),",
        "\n           vm_dinamica(), vm_ritmo(), vm_palabras(), vm_atipicos(), vm_mapa_animo(), vm_guardar_graficas(),",
        "\n           vm_por_hora('valencia'), vm_cruce_horas('cafeina_mg', 'activacion'), vm_sustancias(), vm_lugares(), vm_presion(), vm_retraso('momentos'),",
        "\n           vm_describir('vm_panel.csv'), vm_lista(), vm_pares(tabla, 'columna'), vm_leer_sqlite()")
