# Vestigium Mei

App personal de Android para registrar, a lo largo de los años, el rastro de un día: diario, momentos, ánimo, sueño, cuerpo, lugares, gastos y pantalla. Todo se guarda en el teléfono y se exporta a CSV listo para R.

## Instalar o actualizar

1. Abre la sección **Releases** de este repositorio desde el teléfono (con tu sesión de GitHub).
2. Descarga el archivo `vestigium-mei-N.apk` de la versión más reciente.
3. Ábrelo. La primera vez Android pedirá permitir «instalar apps desconocidas» para tu navegador.
4. Las versiones nuevas se instalan encima de la anterior y conservan tus datos (todas están firmadas con la misma llave).

## Qué incluye

- **Día**: sueño y sueños, contexto, hitos, ánimo en dos ejes (Russell), flujo (Csikszentmihalyi), necesidades (Deci y Ryan), evaluación del estrés (Lazarus), lo mejor y lo peor, hábitos, medicamentos, seguimiento privado opcional, diario con detección de @personas y #temas, y predicción del ánimo de mañana.
- **Momentos**: «pensando en», preocupación, tristeza, ansiedad, enojo, alegría, gratitud, impulso, idea y recuerdo. Un toque y se guardan con hora y ubicación. Disponibles en widget, panel de ajustes rápidos, accesos directos del ícono y dentro de la app.
- **Ánimo durante el día**: avisos a horas aleatorias (muestreo de experiencia) que guardan también el lugar.
- **Gastos** con captura de un toque e indicador de imprevisto.
- **Cuestionarios validados**: WHO-5, PSS-4, UCLA-3 y TIPI.
- **Prueba de reacción** breve (inspirada en la PVT).
- **Sensores**: Health Connect (sueño por etapas, frecuencia cardiaca, HRV, pasos, oxígeno), tiempo en pantalla y desbloqueos, ubicación de fondo y lugares con geocercas.
- **Explorar**: cobertura, correlaciones con desfase de un día, relaciones más marcadas con advertencia de comparaciones múltiples, calibración de predicciones y emociones por hora.
- **Experimentos personales** con semanas sorteadas, **cartas al futuro** y **este día hace un año**.
- **Reportes**: semanal en PNG y mensual en PDF.
- **Datos**: exportación .zip con CSV por tabla (incluye `vm_panel.csv`, una fila por día con todo junto), respaldo automático en una carpeta y en Google Drive, importación de Obsidian, del respaldo de la versión web y de la Cronología de Google Maps.
- **Calendario**: mes con capas de color (ánimo, sueño, pasos, pantalla, gasto, fotos, momentos, lluvia), calendas, nonas e idus; toca un día para editarlo.
- **Fuentes automáticas**: clima, horas de luz, presión y calidad del aire (Open-Meteo, solo zona aproximada), fase lunar, fotos del día, agenda, notificaciones (solo conteos), música que suena, movimiento (a pie, vehículo…) y sesiones de ejercicio del reloj.
- **Mapa** con calles (OpenStreetMap, teñido según el tema): ánimo por lugar, dónde pasas el tiempo, ruta del día y lugares; mantén presionado para marcar uno.
- **Escalada**: sesiones con cada bloque (grado V, Font o YDS; estilo; intentos; ángulo y rasgos), progreso, pirámide y enlace con la sesión del reloj.
- **Biblioteca**: libros, películas, series y juegos con fechas, calificación y notas.
- **Tu año**: el año en píxeles, mes por mes, lo más frecuente e imagen para compartir.
- **Widgets** oscuros: ánimo, momentos y «Hoy».
- **Más fuentes automáticas**: llamadas y SMS (solo conteos), wifi, audífonos/coche/bocina por Bluetooth, música sonando, carga nocturna, batería y luz ambiente cada 15 minutos; ruido y luz al registrar un momento; peso, temperatura de la piel, respiración, presión, calorías, distancia, pisos y agua desde Health Connect.
- **MacroFactor**: importa el .xlsx de nutrición y Workouts (resumen diario, cada alimento, cada serie y series por grupo muscular). Pantalla de Gimnasio con 1RM estimado por ejercicio.
- **Cuestionarios con recordatorio**: WHO-5 y PSS-4 cada semana, UCLA-3 cada mes y TIPI cada año; avisa el día que toca y más seguido si te atrasas.
- **Widgets**: ánimo, momentos, gasto por categoría, sueño, hábitos, el mes coloreado por ánimo y «Hoy».
- **Importaciones**: historial de Spotify, subrayados de Kindle, YouTube y búsquedas de Google Takeout, y actividad de chats de WhatsApp (solo conteos y tiempos de respuesta; el texto se descarta).
- **Análisis en la app**: tendencias con medias móviles, día de la semana, qué acompaña tus mejores días (con corrección de Benjamini-Hochberg), modelo múltiple estandarizado controlando el día anterior, dinámica emocional (DE, MSSD, inercia), ritmo de sueño y jet lag social, palabras del diario y días atípicos.

## En R

Cada exportación trae `vestigium.R` y `vm_diccionario.csv`. Con los CSV en una carpeta, `source("vestigium.R")` lee todas las tablas con el tipo correcto de cada columna y deja listas `vm_tendencia()`, `vm_semana()`, `vm_cruce()`, `vm_factores()`, `vm_modelo()`, `vm_modelo_ar1()` (con errores AR(1) vía nlme), `vm_dinamica()`, `vm_ritmo()`, `vm_palabras()`, `vm_atipicos()`, `vm_mapa_animo()` y `vm_guardar_graficas()`.

## Google Drive (opcional, una sola vez)

En [console.cloud.google.com](https://console.cloud.google.com): crea un proyecto, habilita **Google Drive API**, configura la pantalla de consentimiento (Externo, agrega tu cuenta como usuario de prueba y luego **Publicar app**) y crea un **ID de cliente de OAuth tipo Android** con el paquete `io.github.ruddy2003.vestigium` y la huella SHA-1 que muestra la app en Más → Datos → Google Drive → guía.

## Compilación

Cada push a `main` compila la APK en GitHub Actions y la publica como Release.

## Paper metodológico

En `docs/paper/` está el paper que explica qué hace la app, cómo y por qué (capítulos, figuras, hallazgos y referencias). El plan por módulos está en `docs/paper/PLAN_PAPER_METODOLOGIA.md`.
