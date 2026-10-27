# Apéndice D · Cambios metodológicos aplicados

_Cada cambio del módulo 10 con su motivo (hallazgo y propuesta de `revision_propuestas.md`) y la versión en que entró. El módulo 10 se hace en cuatro entregas (10a privacidad; 10b datos que no se guardaban; 10c cálculos; 10d R y exportación). Este apéndice crece con cada una._

## Entrega 10a · Privacidad (commit `a67bad0`; versión: la primera que se publique desde `main` después de v84)

Decisiones del autor (27 oct 2026):
- D-1 y D-2, ampliadas: todo lo privado se ve solo en una pantalla de Privado y viaja a R.
- Los análisis de esa pantalla son los que ya tenía más una tendencia simple; las correlaciones, solo en R.
- El impulso deja de ser medida de los experimentos.
- Con datos privados, ningún respaldo sale del teléfono sin contraseña.

No cambia lo que se guarda: `SCHEMA_V` sigue en 12.

| # | Qué cambió | Dónde | Por qué |
|---|---|---|---|
| D.1 | Regla única de lo privado. Son privados los momentos de impulso, recaída y libido; en el día, la intensidad del impulso, la recaída, los detonantes y los medicamentos; y el hábito ligado a las recaídas. También las variables `impulso`, `recaida`, `n_recaida`, `recaida_min`, `libido_max`, `n_libido`, `n_impulso`, `medicamentos_n`, `med_*`, `det_*` y `hab_<ligado>` | `data/Privado.kt` (nuevo); `Momento.privado` en `data/Modelo.kt` | Había tres nociones de «privado» que no coincidían con la regla declarada (H-9-39) |
| D.2 | Lo privado no aparece fuera de la pantalla Privado. Afecta a Día, Hoy, momentos, cinta, diarios, libreta, calendario, semana, año, totales, mapa, widgets, reportes, «Tu año», logros (también los propios), metas, campañas, experimentos, Constelación, Red, Tu gente, las sugerencias de @ y #, «Tus temas» y los análisis. Vale también con el interruptor encendido | Unos 30 archivos de `ui/`, `ciencia/`, `data/`, `reportes/` y `widget/` | H-8-1, H-8-2, H-8-3, H-8-16, H-6-1, H-9-1, H-9-38 (P-01 a P-03 y P-34) |
| D.3 | `Estadistica.variables()` excluye las variables privadas, salvo con `privadas = true`, que solo usa el diccionario. El panel las calcula siempre, sin depender del interruptor, para la exportación | `ciencia/Estadistica.kt`, `ciencia/Dinamicas.kt` | Que no lleguen a ningún análisis de la app, pero sí a R completas |
| D.4 | `per_`, `pen_` y `tema_` ya no leen momentos privados, y sus días ya no cuentan como «días con diario» para los ceros. `momentos_intensidad` y `n_madrugada`…`n_noche` ya no incluyen impulsos | `ciencia/Dinamicas.kt`, `ciencia/Estadistica.kt` | H-6-1, H-9-27. **Cambia valores**: algunas columnas `per_`/`tema_` pueden desaparecer del panel extendido |
| D.5 | Pantalla Privado (antes «Impulsos y recaídas»), siempre accesible desde Más. Tiene: registro por día con medicamentos y «lo de siempre»; con el interruptor, también impulso, recaída, detonantes y hábito ligado; el resumen que ya existía; una tendencia de 12 meses (recaídas, impulsos, días con medicamentos); los días que se tomó cada medicamento; y el registro editable de los últimos 30 momentos privados. Desde Día, el enlace «privado de este día» abre la fecha | `ui/pantallas/Recaidas.kt`, `ui/pantallas/Dia.kt`, `Mas.kt`, `Pantallas.kt`, `Sistema.kt` | Decisión del autor |
| D.6 | Con datos privados y sin contraseña, no se escribe en la carpeta, no se sube a Drive y no se puede compartir el respaldo completo. Hay aviso en Hoy y una notificación por semana. La exportación para R (a mano) sigue en claro, porque R necesita leerla, y la pantalla lo advierte | `data/Respaldos.kt` (`bloqueado`), `data/Drive.kt`, `avisos/Trabajo.kt`, `avisos/Avisos.kt`, `ui/pantallas/Respaldo.kt`, `Hoy.kt`, `Sistema.kt` | H-8-5 (en parte) |
| D.7 | Los reportes que se guardan solos (lunes y día 1) van en modo discreto y sin la frase del mejor momento | `reportes/Reportes.kt` | H-8-4 (en parte; P-06) |
| D.8 | Las coordenadas de un momento privado no se usan para pedir el clima | `sensores/Entorno.kt` | H-9-6 (P-04) |
| D.9 | Elegir «ninguno» como hábito ligado vuelve a funcionar: antes caía en la búsqueda por palabras clave | `data/Recaidas.kt` | Error encontrado al aplicar D.1 |
| D.10 | `vm_mapa_animo()` usa solo registros de ánimo con su propia valencia, sin seguimiento privado ni la valencia del diario prestada | `assets/vestigium.R` | H-2-15 (parte de R), H-9-12 (P-05) |
| D.11 | Prueba JVM de la regla | `app/src/test/.../PrivadoPrueba.kt` | Que una variable o un momento privado no vuelva a colarse sin que falle una prueba |

**Efectos que conviene saber al leer los datos:**
- Con el interruptor apagado, antes no existían en el panel `n_recaida`, `recaida_min`, `libido_max`, `n_libido` ni `det_*`. Ahora existen si hay datos: el panel exportado puede traer columnas nuevas.
- La intensidad del impulso ya no está en el diario de la noche, sino en la pantalla Privado. Es posible que se anote menos días (capítulo 3, sección 3.16).
- Si no hay contraseña y existen datos privados (por ejemplo, un solo medicamento anotado), los respaldos automáticos quedan **en pausa** hasta que se ponga una.

**Capítulos actualizados:** 3 (secciones 3.7 y 3.16), 6 (secciones 6.3 y 6.6, familias dinámicas), 7 (secciones 7.2 y 7.13), 9 (secciones 9.3.1 y 9.4), 10 (secciones 10.1, 10.3, 10.5 y 10.10, y la Figura 10.1) y los apéndices A y C.
