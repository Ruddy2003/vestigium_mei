# Lote E6 — Los muebles de Vida y de Diario (plan)

Fecha: 9 nov 2026. Rama: `claude/elegant-mccarthy-hxriov`.

## Qué pidió el autor

- Que las secciones especiales de Vida (Logros, Mapas, Obras, Otras vidas y Social) tengan un diseño propio en cada tema, como el de la pestaña Diario, y no sean recuadros genéricos. Vida es una habitación más de la casa del tema, y cada sección es un mueble de esa habitación.
- Logros: una versión reducida del Hall.
- Mapas: íconos propios de cada tema en «Tus mapas» para el Patrimonio Mundial, el mapa para rascar y los lugares.
- Notas: su propio mueble en la pestaña Diario.

## Qué ya era del tema y qué no

| Sección | Antes | Ahora |
|---|---|---|
| Social | (nueva) recuadro genérico con el grafo | `muebleSocial` del tema detrás del grafo |
| Logros | degradado genérico y medallas | antesala: el salón del Hall del tema (`escenaSalon`) en pequeño, con los trofeos |
| Mapas | mapa para rascar sobre el recuadro genérico | `muebleMapas` del tema alrededor del mapa |
| Obras | la repisa del tema (`repisaObras`) dentro del recuadro genérico | igual, porque ya es un mueble del tema; el recuadro toma el marco del tema |
| Otras vidas | degradado genérico y medallas | `muebleOtrasVidas` del tema: vitrina con un nicho por medalla |
| Tus mapas | pictogramas fijos (templo, zigzag, marcadores) | `iconoMapa("unesco" / "rascar" / "lugares")` del tema |
| Notas | solo una fila en Más | `tableroNotas` y `notaTablero` del tema en la pestaña Diario |

## Piezas nuevas

Interfaz `ui/vida/DisenoVida.kt`, con un objeto por tema y despacho `disenoVida(TemaId)`:

1. `muebleSocial(EstadoMueble, oscuro)`
2. `muebleMapas(EstadoMueble, oscuro)`
3. `muebleOtrasVidas(EstadoMueble, oscuro)`
4. `iconoMapa(id, EstadoIcono, oscuro)`, con `id` = `unesco`, `rascar` o `lugares`
5. `tableroNotas(EstadoMueble, oscuro)`
6. `notaTablero(EstadoNota, oscuro)` y `tintaNota(oscuro)`

`VidaPlana` es la versión sin diseño: la usan Básico y cualquier tema que todavía no tenga la suya. Nunca queda nada roto.

## Lotes y agentes

Hay un agente por cada 4 temas como máximo, y cada agente escribe `ui/vida/Vida<Tema>.kt`. Yo conecto el despacho, compilo, reviso las capturas en claro y en oscuro y corrijo.

| Lote | Temas | Agentes |
|---|---|---|
| V1 | Romano, Urbe, Ciber, Neón · Heleno, Egipto, Medieval, Templario · Distopía, Refugio, Orden, Vapor · Laboratorio, Colonos, Napoleón, Deco | 4 |
| V2 | Botánico, Ruinas, Navidad, Seda · Edo, URSS, Gótico, Arcano · Espacio, Abismo, Andalus, Meso · Mongol, Renacimiento, Pirata, Revolución | 4 |
| V3 | Antiguas, Cueva | 1 |

## Verificación de cada lote

1. Compilación en Actions (`apk.yml`).
2. Capturas con `capturas.yml` y `superficies=vida`: Vida completa y la pestaña Diario, en claro y en oscuro, de cada tema del lote.
3. Se revisa cada captura, en claro y en oscuro: que se lea, que el mueble no tape el contenido y que no haya nada cortado ni manchas.
4. Se corrige hasta que quede limpio. El informe de cada lote queda en `docs/diseno/informes/E6_V<n>.md`.
