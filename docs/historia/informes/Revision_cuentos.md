# Revisión general de los cuentos, después de T8b

Fecha: 13 nov 2026. Rama: `ccr-9229e262-lhs8xl`, encima de T8b. Plan: `docs/plan_historia_y_escenas.md`.

Con T8b terminaron las tandas de los cuentos: los 45 personajes con cuento tienen escrita su temporada 1. Antes de esta revisión, el autor leyó un resumen de los personajes de los grupos 7 y 8 y pidió dos cambios. Después hice la revisión general, que encontró un problema más: nombres repetidos.

- **No toca el motor.**
- **No cambia nada de lo que se guarda:** ni `SCHEMA_V`, ni el diccionario, ni `vestigium.R`, ni el paper.
- **No lee ningún dato tuyo.**

## Lo que pidió el autor

Los dos cambios se cuentan sin nombrarlos y sin subrayarlos: solo donde le sirven a la historia.

### La residente es lesbiana

Ezra, el de los cultivos, pasa a ser **Noa**, una mujer. La historia es la misma:
- le regala un disco;
- piden juntas un cuarto doble;
- sale con la expedición y vuelve con una flor;
- la residente espera un hijo.

Archivos:
- **Su calendario** (`AnioG7.kt`): el nombre, el suceso `ezra`, que ahora es `noa`, y los resúmenes de las temporadas.
- **Su cuento** (`PiezasResidente.kt`): los ocho textos que la nombran, con el género corregido («una muchacha alta y callada», «se ha ido, roja», «no sabe si está viva»).
- **La prueba** (`CuentoG7Prueba`): ahora comprueba además que ningún día salga «Ezra» ni «muchacho alto».

**El hijo:** en el refugio cada nacimiento lo deciden el permiso del supervisor y la enfermería. Por eso basta una frase en el texto del embarazo: la doctora del refugio, «que se encargó de todo en la enfermería», dice que nunca ha visto un embarazo tan tranquilo.

Lo que ya estaba escrito y ahora se lee de otra manera, sin tocarlo:
- en año nuevo pide «otra cosa que no le pide nunca en voz alta»;
- cuando Lina dice que lo de Noa «es evidente», ella dice que no sabe de qué habla.

Los informes A7 y T7b ya dicen Noa.

### La doctora es autista

Su cuento ya tenía mucho de eso:
- cuenta las flechas del suelo (cuarenta y dos, siempre);
- pone siempre el mismo cuarteto;
- se pone los auriculares para no oír el altavoz;
- contesta con números;
- en la mesa del Consejo asiente cuando asienten los demás;
- deja el cuaderno con el lomo a ras de los otros, «si no, no duerme».

Lo reforcé en cinco textos, cambiando o agregando sin alargar el cuento:

| Dónde | Qué dice ahora |
|---|---|
| Hilda (temporada 1) | Le enseñó a mirar raíces «y nunca le pidió que la mirara a ella a los ojos» |
| Una comida | Come de espaldas al ruido, con los auriculares puestos sin música: «así el comedor suena lejos» |
| Lauro, al llegar | No mira a los ojos: mira las manos. «A la doctora eso le descansa» |
| Un pensamiento nuevo, de todo el año | De niña la llamaban rara porque contaba las flechas y se tapaba los oídos en el comedor; ahora contar es su oficio, y lo de los oídos sigue igual |
| Arriba (temporada 5) | Lleva los auriculares sin música «para que el mundo no le llegue todo a la vez», y cada día se los quita un rato más |

La subida a la superficie gana con esto: para ella, el viento y el cielo son ruido sin ajustar, y aprende a quitarse los auriculares. La cabecera de `PiezasCientifica.kt` lo anota para que las temporadas que faltan sigan igual.

## Lo que encontró la revisión: nombres repetidos

Busqué los nombres propios de los 45 cuentos y los comparé entre sí. Encontré personajes inventados distintos con el mismo nombre. Todos venían de los borradores del grupo 8:

| Nombre | Dónde estaba | Qué hice |
|---|---|---|
| Damián | El piloto de la escriba; un guardia de Ruth | El de Ruth es **Esteban** |
| Rosendo | El cocinero de la escriba; el viejo de las gallinas de Ruth | El de Ruth es **Eusebio** |
| Bruno | El ingeniero de la doctora; el hermano de Ruth; el viejo de las abejas de la semillera (grupo 7) | El de la doctora es **Osvaldo**; el de Ruth, **Gabino**. El de la semillera queda |
| Tomás | El compañero de Ruth; Tomás, el grumete (grupo 5) | El de Ruth es **Matías** |
| Abel | El hijo de Ruth; el contramaestre de Tomás (grupo 5) | El de Ruth es **Elías** |

- Cambié los sucesos que llevaban el nombre: `nace_elias`, `elias_camina`, `gabino_visita` y, en Ruth, `esteban`.
- También cambié las pruebas y los informes A8, T8a y T8b.
- La escriba conserva a Damián y a Rosendo.

**Lo que se repite y está bien:**
- **Santos, fiestas y gente real:** San Pedro, la Virgen del Carmen, San Fermín, Federico, Renaud y los demás.
- **El mismo mundo, a propósito:** Kei y Amaru.
- **Nombres comunes de su época,** en personajes lejanos: Tom, Walter y Thomas, en la Inglaterra medieval y en la del siglo XIX.
- **Tom y Carmen,** que según el plan se quedan sin cuento.

## Comprobaciones

- **Arnés local** (Kotlin 2.1.0): compila sin avisos y pasan las **222 pruebas**.
- **Cuento medio** (2027, a las 9 y a las 19):
  - la residente: 2,198, igual que antes;
  - la doctora: 2,189, antes 2,188;
  - Ruth: 2,080, antes 2,079.
- **Leí los textos cambiados en sus cuentos:**
  - de la residente, el disco, el cuarto, el embarazo y el anochecer con la Puerta abierta;
  - de la doctora, la comida, Lauro, Hilda, el pensamiento nuevo y arriba;
  - de Ruth, el asedio y sus cinco años.
- **Frente a `main`:** la rama no toca `SCHEMA_V`, el diccionario, `vestigium.R` ni la configuración de compilación.
- **El plan:**
  - las 8 tandas A y las 16 tandas T tienen su informe y están marcadas como hechas;
  - Tom y Carmen quedan sin cuento, como dice el plan.
- **Compilaciones de Android anteriores:** T8a, corrida 234; T8b, corrida 237. Las dos terminaron bien.
- **Compilación de Android de esta revisión:** la corrida 240 de `apk.yml`, lanzada a mano sobre la rama y sin publicar, terminó bien (commit `d26d795`).
- **Capturas:** 0.

## Lo que no se pudo comprobar

- **La búsqueda de nombres repetidos** se hizo con un script de una sola vez, no con una prueba. Un nombre que solo sale a principio de frase pudo escaparse.
