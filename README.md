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
- **Mapa** con calles (OpenStreetMap/CARTO): ánimo por lugar, dónde pasas el tiempo, ruta del día y lugares; mantén presionado para marcar uno.
- **Escalada**: sesiones con cada bloque (grado V, Font o YDS; estilo; intentos; ángulo y rasgos), progreso, pirámide y enlace con la sesión del reloj.
- **Biblioteca**: libros, películas, series y juegos con fechas, calificación y notas.
- **Tu año**: el año en píxeles, mes por mes, lo más frecuente e imagen para compartir.
- **Widgets** oscuros: ánimo, momentos y «Hoy».

## Google Drive (opcional, una sola vez)

En [console.cloud.google.com](https://console.cloud.google.com): crea un proyecto, habilita **Google Drive API**, configura la pantalla de consentimiento (Externo, agrega tu cuenta como usuario de prueba y luego **Publicar app**) y crea un **ID de cliente de OAuth tipo Android** con el paquete `io.github.ruddy2003.vestigium` y la huella SHA-1 que muestra la app en Más → Datos → Google Drive → guía.

## Compilación

Cada push a `main` compila la APK en GitHub Actions y la publica como Release.
