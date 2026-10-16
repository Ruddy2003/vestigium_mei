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
  panel = "vm_panel.csv", dias = "vm_dias.csv", momentos = "vm_momentos.csv", gastos = "vm_gastos.csv",
  escalas = "vm_escalas.csv", reaccion = "vm_reaccion.csv", salud = "vm_salud.csv", pantalla = "vm_pantalla.csv",
  visitas = "vm_visitas.csv", ubicaciones = "vm_ubicaciones.csv", entorno = "vm_entorno.csv", fotos = "vm_fotos.csv",
  agenda = "vm_agenda.csv", notificaciones = "vm_notificaciones.csv", musica = "vm_musica.csv", actividad = "vm_actividad.csv",
  ejercicio = "vm_ejercicio.csv", escalada_sesiones = "vm_escalada_sesiones.csv", escalada_bloques = "vm_escalada_bloques.csv",
  biblioteca = "vm_biblioteca.csv", comunicacion = "vm_comunicacion.csv", conexiones = "vm_conexiones.csv",
  subrayados = "vm_subrayados.csv", google_dia = "vm_google_dia.csv", google = "vm_google.csv", whatsapp = "vm_whatsapp.csv")

vm <- compact(lapply(vm_archivos, vm_leer))

# Las tablas quedan también como objetos sueltos: panel, dias, momentos, salud...
for (.n in names(vm)) assign(.n, vm[[.n]], envir = globalenv())
rm(.n)

# ---- Panel diario enriquecido ---------------------------------------------
if (!is.null(vm$panel) && nrow(vm$panel) > 0) {
  panel <- vm$panel |>
    arrange(fecha) |>
    complete(fecha = seq(min(fecha), max(fecha), by = "day")) |>
    mutate(
      dia_semana = wday(fecha, week_start = 1),
      fin_de_semana = dia_semana >= 6,
      semana = floor_date(fecha, "week", week_start = 1),
      mes = floor_date(fecha, "month"),
      valencia_ayer = lag(valencia), valencia_manana = lead(valencia),
      sueno_h = sueno_min / 60
    )
  vm$panel <- panel
}

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

#' Media e intervalo de 95% por día de la semana.
vm_semana <- function(var = "valencia", datos = panel) {
  d <- datos |> mutate(y = .vm_dato(datos, var)) |> filter(!is.na(y)) |>
    group_by(dia = factor(wday(fecha, week_start = 1), 1:7, c("lun", "mar", "mié", "jue", "vie", "sáb", "dom"))) |>
    summarise(media = mean(y), n = n(), ee = sd(y) / sqrt(n), .groups = "drop") |> mutate(lo = media - 1.96 * ee, hi = media + 1.96 * ee)
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

#' Todo lo que se asocia con un objetivo: diferencias (variables 0/1) o correlación de Spearman, con corrección BH.
vm_factores <- function(objetivo = "valencia", desfase = 0, min_n = 14, datos = panel) {
  datos <- arrange(datos, fecha)
  yv <- lead(.vm_dato(datos, objetivo), desfase)
  excluir <- c("fecha", "semana", "mes", "dia_semana", objetivo, "valencia_ayer", "valencia_manana")
  cand <- names(datos)[map_lgl(datos, ~ is.numeric(.x) || is.logical(.x)) & !names(datos) %in% excluir]
  res <- map_dfr(cand, function(v) {
    x <- datos[[v]]; ok <- !is.na(x) & !is.na(yv)
    if (sum(ok) < min_n || length(unique(x[ok])) < 2) return(NULL)
    xs <- as.numeric(x[ok]); ys <- yv[ok]
    if (all(xs %in% c(0, 1))) {
      if (sum(xs == 1) < 5 || sum(xs == 0) < 5) return(NULL)
      t <- t.test(ys[xs == 1], ys[xs == 0])
      tibble(variable = v, tipo = "0/1", efecto = unname(diff(rev(t$estimate))), lo = t$conf.int[1], hi = t$conf.int[2], n = sum(ok), p = t$p.value)
    } else {
      ct <- suppressWarnings(cor.test(xs, ys, method = "spearman", exact = FALSE)); r <- unname(ct$estimate)
      z <- atanh(min(max(r, -.999), .999)); e <- 1.96 / sqrt(sum(ok) - 3)
      tibble(variable = v, tipo = "rho", efecto = r, lo = tanh(z - e), hi = tanh(z + e), n = sum(ok), p = ct$p.value)
    }
  })
  if (!nrow(res)) return(res)
  res |> mutate(q = p.adjust(p, "BH"), firme = q < .1) |> arrange(p) |>
    left_join(vm_dic |> filter(archivo == "vm_panel.csv") |> select(variable = columna, descripcion), by = "variable")
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
vm_ritmo <- function() {
  if (is.null(vm$salud) || !all(c("sueno_inicio", "sueno_fin") %in% names(vm$salud))) stop("No hay datos de sueño del reloj.")
  h <- function(t) hour(t) + minute(t) / 60
  s <- vm$salud |> filter(!is.na(sueno_inicio), !is.na(sueno_fin)) |>
    mutate(ini = h(sueno_inicio), ini = if_else(ini < 12, ini + 24, ini), fin = h(sueno_fin) + 24, dur = fin - ini,
           medio = (ini + fin) / 2, libre = wday(fecha, week_start = 1) >= 6) |> filter(dur > 2, dur < 16)
  r <- s |> group_by(libre) |> summarise(noches = n(), dormir = mean(ini) %% 24, despertar = mean(fin) %% 24, horas = mean(dur), punto_medio = mean(medio), .groups = "drop")
  print(r)
  if (nrow(r) == 2) cat("Jet lag social:", round(abs(diff(r$punto_medio)), 2), "horas\n")
  cat("Regularidad (DE de la hora de dormir):", round(sd(s$ini) * 60), "minutos\n")
  ggplot(s, aes(y = fecha)) + geom_linerange(aes(xmin = ini, xmax = fin, colour = libre), linewidth = 2) +
    scale_x_continuous(breaks = seq(18, 38, 4), labels = function(x) paste0(x %% 24, ":00")) +
    scale_colour_manual(values = c(`FALSE` = "#7C2433", `TRUE` = "#2D6B63"), labels = c("laboral", "fin de semana"), name = NULL) +
    labs(x = NULL, y = NULL, title = "Noches") + theme_minimal()
}

#' Palabras del diario asociadas con días buenos y difíciles.
vm_palabras <- function(min_dias = 5) {
  if (is.null(vm$dias)) stop("No hay diario.")
  vacias <- c("de","la","que","el","en","y","a","los","se","del","las","un","por","con","no","una","su","para","es","al","lo","como","más","mas","pero","sus","le","ya","o","este","sí","si","porque","esta","entre","cuando","muy","sin","sobre","también","me","hasta","hay","donde","quien","desde","todo","nos","durante","todos","uno","les","ni","contra","otros","ese","eso","ante","ellos","e","esto","mí","antes","algunos","qué","unos","yo","otro","otras","otra","él","tanto","esa","estos","mucho","quienes","nada","muchos","cual","poco","ella","estar","estas","algunas","algo","nosotros","mi","mis","tú","te","ti","tu","tus","hoy","día","fue","estaba","fui","era","tengo","tenía","hice","creo","luego","después","bien","vez")
  d <- vm$dias |> filter(!is.na(nota), nzchar(nota)) |> select(fecha, valencia, nota) |>
    mutate(p = str_extract_all(str_to_lower(nota), "[\\p{L}]{3,}")) |> select(-nota) |> unnest(p) |> filter(!p %in% vacias) |> distinct(fecha, valencia, p)
  media <- mean(vm$dias$valencia, na.rm = TRUE)
  d |> filter(!is.na(valencia)) |> group_by(palabra = p) |> summarise(dias = n(), media = mean(valencia), .groups = "drop") |>
    filter(dias >= min_dias) |> mutate(diferencia = media - !!media) |> arrange(desc(diferencia))
}

#' Días que se salen de lo normal (z robusto con mediana y MAD).
vm_atipicos <- function(vars = c("valencia", "sueno_h", "pasos", "pantalla_min", "gasto_total", "notificaciones", "desbloqueos", "km"), z = 3, datos = panel) {
  vars <- intersect(vars, names(datos))
  datos |> select(fecha, all_of(vars)) |> pivot_longer(-fecha, names_to = "variable", values_to = "valor") |> filter(!is.na(valor)) |>
    group_by(variable) |> mutate(z = (valor - median(valor)) / mad(valor)) |> ungroup() |> filter(is.finite(z), abs(z) >= !!z) |> arrange(desc(fecha))
}

#' Momentos con lugar, coloreados por valencia (sin mapa de fondo).
vm_mapa_animo <- function() {
  if (is.null(vm$momentos)) stop("No hay momentos.")
  d <- vm$momentos |> filter(!is.na(lat), !is.na(lon)) |> left_join(select(panel, fecha, v_dia = valencia), by = "fecha") |> mutate(v = coalesce(as.numeric(valencia), v_dia)) |> filter(!is.na(v))
  ggplot(d, aes(lon, lat, colour = v)) + geom_point(alpha = .7) + scale_colour_gradient2(low = "#7C2433", mid = "grey85", high = "#2D6B63", midpoint = 4) + coord_quickmap() + theme_minimal()
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
        "\nFunciones: vm_tendencia(), vm_semana(), vm_cruce(x, y, desfase = 1), vm_factores(), vm_modelo(), vm_modelo_ar1(),",
        "\n           vm_dinamica(), vm_ritmo(), vm_palabras(), vm_atipicos(), vm_mapa_animo(), vm_guardar_graficas(),",
        "\n           vm_describir('vm_panel.csv'), vm_lista(), vm_pares(tabla, 'columna')")
