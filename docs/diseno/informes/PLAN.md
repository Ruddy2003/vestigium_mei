# Segunda pasada de diseño: plan por lotes

Llevar todo el diseño al nivel del Ideario (v143): un objeto de dibujo por tema, despachado por un `when (tema)` exhaustivo, oscuro de verdad, texto dibujado con la letra del tema y trazos en dp. Cada lote: plan escrito (este archivo y el informe del lote), cuatro agentes por época, compilación local, CI (`apk.yml` y `capturas.yml`), revisión de todas las capturas en claro y oscuro, arreglos hasta que quede limpio e informe en `docs/diseno/informes/<lote>.md`.

Reglas comunes: no cambia ningún dato (`SCHEMA_V`, `Diccionario.kt`, `vestigium.R` y el paper intactos); como mucho dos degradados por dibujo (Paparazzi); pasos de bucles dependientes del tamaño con `maxOf(…, 1f)`; NEON y CIBER solo en oscuro.

## Fase A — Bibliotecas (hecha)

A0–A1 estante, A2 tapa/página/escritor, A3 libreta, A4 cartas, A5 lo de alrededor. Ver `TEMAS.md` y `A5.md`.

## Fase B — Piezas comunes

Paquete nuevo `ui/comun/`: interfaz `DisenoComun`, un `object Comun<Tema>` por tema en cuatro archivos por época (`ComunAntiguedad.kt` con Fantasía, `ComunMedievo.kt` con Moderna, `ComunContemporanea.kt`, `ComunFuturo.kt`) y `disenoComun(t)`.

| Lote | Qué | Dónde |
|---|---|---|
| B1 | Campos de texto: `CampoTexto` (mismos parámetros que `OutlinedTextField`) con el marco del tema (`marcoCampo`), en las 122 llamadas | 28 archivos |
| B2 | Separadores (`Separador`, 101 `HorizontalDivider`) y botones de texto (`BotonTexto`, 256 `TextButton`) | todo `ui/` |
| B3 | Filas de menú (`FilaMenu`, `FilaMas`), `Vacio`, `Aviso` y `Cifra` | `Componentes.kt`, `Pantallas.kt` |
| B4 | Tarjetas de Hoy, Día y Vida: `TarjetaMetrica`, `TeFaltaHoy`, `TarjetaNoche`, `RejillaTarjetas` | `Hoy.kt`, `Dia.kt`, `Vida.kt` |
| B5 | Gastos: lo que queda de Material en la pantalla | `Gastos.kt` |

## Fase C — Logros y viaje

Paquete nuevo `ui/logros/`: interfaz `DisenoLogros`, un `object Logros<Tema>` por tema en cuatro archivos por época y `disenoLogros(t)`.

Revisión del código al empezar la fase: las figuras de los trofeos ya son propias y ricas (una función de 75–150 líneas por tema en `TrofeosTema.kt` / `TrofeosMas.kt`); las medallas del hall (`PiezaLogro`) y la medalla vieja (`MedallaLogro`) ya no se ven en ninguna pantalla de la app (el hall muestra los trofeos), así que no se rehacen. Lo que sí falta:

| Lote | Qué |
|---|---|
| C1 | Pedestales de los trofeos (6–15 líneas por tema) y del hall (1–10 líneas, trazos en px), la placa del hall dibujada, y los trofeos de la Terminal sin ASCII |
| C2 | Las escenas de los salones del hall, más llenas y en dp (`SalonesTema.escena`, `SalonesMas`) |
| C3 | Marcadores de campaña propios (25 temas con alfiler) y la victoria con oscuro (`MapasMas.marcador`, `MapasCampana.victoria`) |
| C4 | La luna, la huella y la pesa de Tu año por tema (`TuAnioVisual.objetoComun`) |

## Fase D — Pulido

| Lote | Qué |
|---|---|
| D1 | Modo oscuro en las esferas de enfoque (`EnfoqueActivity.kt`), `ControlesTema.kt` y la hoja rápida (`QuickActivity.kt`) |
| D2 | Adornos de Relación (`Relacion.kt`), `IconosCinta.kt` y los huecos de `Marcas.kt` |
| D3 | Texturas de CIBER, MEDIEVAL y ORDEN |
| D4 | Trazos en píxeles a dp en `Decoracion.kt` y `Libros.kt` |

El detalle de cada lote se escribe al empezarlo, en su informe.
