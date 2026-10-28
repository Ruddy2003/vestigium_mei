# Capítulo 7. Personas y red social

> **Para qué sirve este capítulo.** Explica **cómo la app registra a la gente de tu vida**: cómo reconoce a una persona cuando la nombras con @, cómo guarda qué es para ti y cómo eso cambia con los años, cómo mide qué tan cerca la sientes, cómo dibuja la **Constelación** y la **Red**, qué calcula R con `vm_red()` y cómo entran las personas en las correlaciones. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`); la entrega 10f no cambió nada de lo que trata este capítulo. Los hallazgos se numeran H-6-n y están en `hallazgos.md`. **Las personas, fechas y números de los ejemplos son inventados.**

---

## 7.1 Menciones con @ y equivalencias

La app no intenta adivinar nombres en el texto libre: **solo cuenta a una persona si la escribes con @** (una **mención**). Así evita confundir «Rosa» (persona) con «rosa» (color) y no necesita reconocer nombres propios.

- **Reconocer la mención** (`Texto.personas`): busca la expresión regular `@([\p{L}\p{N}_]+)`, es decir, una arroba seguida de letras (de cualquier alfabeto, con acentos), números o guion bajo. El nombre se pasa a **minúsculas**: `@Ana`, `@ana` y `@ANA` son lo mismo; `@ana_m` es distinta de `@ana`.
- **Nombre canónico** (`Texto.canon`): el nombre con el que la app cuenta a una persona después de aplicar las **equivalencias** (si dijiste que `@anita` es `@ana`, el canónico de `anita` es `ana`). `canon` sigue las uniones hasta **8 pasos** (`anita → ana → ana_g` da `ana_g`); en un ciclo, todos cuentan como **el primero en orden alfabético**. **[Diseño propio]**.
- **Posibles duplicados** (`Personas.duplicados`): la app pregunta «¿son la misma persona?» si dos nombres (1) son iguales al quitar los guiones bajos; (2) tienen ambos 4 letras o más y su **distancia de Levenshtein** es ≤ 2 (cuántas letras hay que cambiar, agregar o quitar para pasar de uno a otro: `daniel` → `danie` es 1); o (3) tienen ambos 3 letras o más, uno empieza con el otro y la diferencia de largo es ≤ 3 (`ana` y `anita`).

Marcar a mano es más fiable que reconocer nombres automáticamente, y las equivalencias resuelven apodos sin reescribir el diario. **[Diseño propio]**, razonable. *Ejemplo:* escribes `@Dani` 6 días y `@daniel` 3 días; cumplen las reglas 2 y 3, la app pregunta y, si los unes, todas las funciones cuentan **9 días** (o menos si algún día aparecen ambas: un día cuenta una vez).

**Cada función lee textos distintos**, así que «aparecer» no significa lo mismo en todas partes (H-6-9): la variable `personas` del panel lee el diario y, desde 10e, los momentos no privados (D.93); la Constelación y la Red, el diario y los momentos; la familia `per_`, además, el sueño, el mejor y el peor momento y las cartas (tabla en la versión completa, §7.2).

**Cuándo falla.** Si escribes «comí con mi mamá» sin `@`, la app no la ve: todas las variables sociales cuentan **menciones marcadas**, no contactos reales. Dos personas distintas con el mismo `@nombre` se cuentan como una. Y la misma tabla `Config.equivalencias` sirve para `@personas` y `#temas`: una equivalencia creada para una persona también cambiaría un tema con ese nombre (H-6-11, parte vigente).

---

## 7.2 Quién es cada persona: relaciones con historial

En «Tu gente» dices qué es cada @persona para ti, entre 12 opciones (`Personas.RELACIONES`). Para los análisis se **agrupan** (`Personas.grupo`):

| Relación guardada | Grupo |
|---|---|
| familia, casa | `familia` |
| pareja | `pareja` |
| amistad, amistad cercana | `amistad` |
| colega, compañero de escuela, mentor, trabajo | `trabajo` |
| expareja, conocido, lejana, mascota, otra | se quedan como están |

`Perfiles.relacion(persona, perfil, fecha)` usa, en este orden: el grupo de la relación que tenía en «Tu gente» en esa fecha; si no está, la relación de «a quién ves más» en Conocerte (capítulo 3); si tampoco, `casa` si está en «quiénes viven contigo»; si no, ninguna (cuenta en los totales, pero no en `vi_familia`, `vi_pareja`, etc.).

**Cómo se guarda el historial.** Cada `Persona` (`data/Modelo.kt`) tiene `relacion`, `cercania` y un `historial` de `CambioRelacion(hasta, relacion, cercania)`. Al guardar (`Personas.guardar`), si la relación o la cercanía cambió (incluido ponerla o quitarla), se agrega `CambioRelacion(hasta = hoy, relación anterior, cercanía anterior)`, donde «hoy» es, por omisión, el **día lógico** en que guardas (el día de la app, que empieza a las 05:00; capítulo 2). Desde 10e (D.102), al cambiar la relación o la cercanía puedes decir que cambió antes («cambió antes de hoy…»): no después de hoy ni antes del cambio anterior. **[Diseño propio]**. Al consultar (`Personas.relacionEn(nombre, fecha)`), se ordena el historial por `hasta` y se toma la **primera** entrada con `fecha < hasta`; si ninguna cumple, la relación actual (`cercaniaEn` hace lo mismo). Es decir, **cada entrada vale para los días anteriores a su `hasta`**; el propio día del cambio ya tiene la relación nueva.

*Ejemplo.* Una persona es amistad desde el 1 feb 2025; el 15 sep 2025 la cambias a pareja y el 20 abr 2026 a expareja. El historial guarda `(hasta 2025-09-15, amistad)` y `(hasta 2026-04-20, pareja)`. Para el 14 sep 2025 da amistad; del 15 sep 2025 al 19 abr 2026, pareja; desde el 20 abr 2026, expareja. Si estuviste con ella el 2 abr 2026, se enciende `vi_pareja`, no `vi_expareja`.

**Razonamiento.** Si los análisis usaran la relación de hoy, todos los días con quien hoy es expareja contarían como «estuve con una expareja», incluidos los días en que era tu pareja. Guardar la relación por fecha es la forma correcta de tratar una **covariable que cambia con el tiempo** (un dato de contexto que tiene un valor por periodo). **[Diseño propio]**, bien planteado.

**Cuándo falla.** Si no dices cuándo cambió, la fecha del cambio es la fecha en que lo anotas: si la ruptura fue el 28 mar y la anotas el 20 abr, esos 23 días cuentan como pareja (H-6-3; hasta 10e no había otra opción). Antes de la primera clasificación se proyecta la relación más antigua conocida (como el perfil proyectado hacia atrás, H-2-5). Y Conocerte no tiene historial por persona: «a quién ves más» y «quiénes viven contigo» se leen con su valor **actual** para todas las fechas (H-6-4, parte vigente). Otros casos límite: versión completa, §7.4.5.

---

## 7.3 Cercanía: los círculos de Aron (escala IOS)

La **escala de Inclusión del Otro en el Yo** (IOS) de Aron, Aron y Smollan (1992) es una pregunta de **un solo reactivo** con dibujos: siete pares de círculos, «yo» y «otro», que van de separados a casi completamente encimados; eliges el par que mejor describe tu relación. Los autores la validaron con buena confiabilidad, relación con otras medidas de cercanía y capacidad de predecir si una relación de pareja seguía meses después. Gächter, Starmer y Tufano (2015) la compararon con seis medidas en 772 adultos y concluyeron que es una medida fiable de la cercanía **subjetiva**; un índice combinado de las otras escalas correlacionó ρ = .85 con la IOS. **[Validado]** como instrumento.

`CirculosCercania` (`ui/pantallas/Gente.kt`) dibuja siete pares de círculos de radio *r*, con distancia entre centros

$$d_i = r\,(2.1 - 0.31\,(i-1)), \quad i = 1,\dots,7$$

de 2.1 *r* (no se tocan) a 0.24 *r* (casi encimados). Al elegir uno aparece su palabra (`CERCANIA`), de 1 «Casi nada en común» a 7 «Casi parte de mí». Es opcional y se puede desmarcar. No pude comprobar cómo están espaciados los círculos en la publicación original **[sin verificar]**.

**Diferencias con el original** **[Aproximación]** (H-6-15): las palabras ayudan a que un «5» signifique lo mismo con el tiempo, pero cambian el instrumento; y se responde **una vez por persona**, no por día, sin recordatorio para revisarla. Por eso `cercania_media` (sección 7.4) mide **a quién nombras**, no **cómo te sentiste con ellas ese día**.

---

## 7.4 Presencias y variables del panel

**Presencias** (`data/Presencias.kt`, capítulo 3). Cada persona de un día tiene un estado: `estuve`, `pense` o `mensajes`. En el diario, el estado por omisión es **«estuve»** si no tocas el nombre (H-1-6). Quien aparece **solo en momentos** cuenta como «pensé» si el momento era «pensando en…» o «recuerdo» sin compañía marcada, y como «estuve» en otro caso. El diario manda sobre los momentos, y `Presencias.delDia` **excluye los momentos privados** (sección 7.10).

### 7.4.1 Variables fijas (`Estadistica.panel`)

Se calculan solo los días en que **se pudo nombrar a alguien** (`Dinamicas.diasSociales`): con texto en el diario o con un momento no privado que nombra a alguien con @. Los demás quedan **vacíos**, no en 0. El grupo de relación es el **de ese día**.

| Variable | Qué cuenta |
|---|---|
| `personas` | @personas distintas (canónicas) en el **texto del diario** y, desde 10e, en los **momentos no privados**, sin importar su estado |
| `cercanos` | personas «estuve» de grupo `familia`, `pareja`, `amistad` o `casa` |
| `vi_familia`, `vi_pareja`, `vi_amistades`, `vi_expareja`, `vi_trabajo` | 1 si estuviste con alguien que ese día era de ese grupo |
| `vi_convivientes` | 1 si estuviste con alguien de la lista **actual** de convivientes que tiene algún grupo |
| `personas_vistas`, `personas_pensadas`, `personas_mensajes` | cuántas personas tuvieron cada estado |
| `penso_en_pareja`, `penso_en_expareja`, `penso_en_familia`, `penso_en_amistades` | 1 si pensaste sin ver a alguien de ese grupo |
| `cercania_media` | promedio de `cercaniaEn` de **todas** las personas del día que tengan cercanía |

Condiciones exactas y las variables `cartas_otros` y `presencias_revisado`: versión completa, §7.7.1. La compañía de los avisos (`acompanado_pct`…) es otra fuente, independiente (capítulo 3).

### 7.4.2 Una variable por persona (`Dinamicas.construir`)

- **`per_<nombre>`** vale 1 si la persona se menciona ese día en el diario, el sueño (texto o temas con `@`), el mejor o peor momento, un momento **no privado** o una carta; 0 en los demás días en que se pudo nombrar a alguien; vacío en el resto. En los análisis entran las **15** personas con más días, con **al menos 5 días**; en la exportación extendida, todas las de al menos 2 días (hasta 400).
- **`pen_<nombre>`** vale 1 si `Presencias.delDia` le da «pensé». Entran las **10** con más días, con **al menos 3**.

**No existe una variable por persona de «estuve con @x»**: `per_` mezcla estar con ella, pensarla, soñarla o escribirle, y su etiqueta lo dice (H-6-2).

*Ejemplo.* Diario: «Desayuné con @ana y @beto. Me acordé de @caro.», y marcas a `@caro` como «pensé». Un momento «pensando en…» sin compañía nombra a `@dani`. Sueñas con `@eli`. Relaciones: ana familia (cercanía 6), beto pareja (7), caro amistad cercana (5), dani amistad (4), eli colega (sin cercanía). Resulta: `personas` = 4 (ana, beto, caro y dani; el sueño no cuenta); `personas_vistas` = 2 y `personas_pensadas` = 2 (eli no cuenta: los sueños no entran en presencias); `cercanos` = 2; `vi_familia` = `vi_pareja` = 1, `vi_amistades` = 0, `penso_en_amistades` = 1; `cercania_media` = (6 + 7 + 5 + 4)/4 = **5.5**, que incluye a las pensadas; `per_` = 1 para las cinco y `pen_caro` = `pen_dani` = 1.

**Cuándo falla.** Un 0 quiere decir «no la nombré», no «no la vi»: un diario escrito sin @ cuenta como «no vi a nadie» (H-6-13, resuelto en parte). «Estuve» por omisión infla las presencias (H-1-6).

---

## 7.5 Co-mención como aproximación de co-presencia

La Constelación y la Red unen a dos personas cuando **aparecen el mismo día lógico**, aunque sea en textos distintos (una **co-mención**). **[Aproximación]** débil de que se relacionen: el mismo día no es el mismo momento (si ves a tu hermana en la mañana y a un compañero en la noche, aparecen juntos aunque nunca se hayan visto); no distingue «estuve» de «pensé»; depende de cuánto escribes; y lo que la gente reporta de sus interacciones coincide solo en parte con lo observado (Bernard et al., 1984) y se desvía según qué tan reciente y llamativa fue la interacción (Eagle et al., 2009). Por eso la app exige **al menos 2 días juntas** para una línea derivada (`minJuntos = 2`), y la Red permite **declarar** quién conoce a quién, que es más fiable.

---

## 7.6 La Constelación

Tu gente como un cielo (`ui/pantallas/Constelacion.kt`): tú en el centro, cada persona una estrella, **más grande** si la mencionaste más días y **más cerca del centro** si la mencionaste hace poco. `Constelacion.calcular` toma las menciones del diario y de los momentos no privados del periodo (90 días, 365 por omisión, o «siempre»), se queda con las **80** personas con más días y une los pares que aparecen juntos **2 días o más**. La distancia al centro, como fracción del radio, es

$$d = \left(0.30 + 0.64\cdot \min\!\left(1, \frac{\text{días desde la última mención}}{P}\right)^{0.6}\right)\cdot 0.94$$

con *P* el periodo en días: alguien mencionado hoy queda a 0.28 del radio; alguien no mencionado en todo el periodo, a 0.88. El tamaño es 2.5 + 7·√(días / días de la más mencionada) dp (unidades de pantalla). El ángulo solo evita que se encimen: **no significa nada**. El color es el grupo de relación **actual**. *Ejemplo:* en un año, `@fer` tiene 44 días y la última hace 20; la más mencionada, 210. Distancia: (20/365)^0.6 = 0.175; (0.30 + 0.64·0.175)·0.94 = **0.387**. Tamaño: 2.5 + 7·√(44/210) = **5.7 dp**.

Es una **visualización**, no un análisis: no produce variables. **[Diseño propio]**. La distancia mide **recencia de la mención**, no cercanía emocional. Límites: una expareja sale con su color de hoy aunque en el periodo haya sido tu pareja casi todo el tiempo (H-6-14); quien viste a diario 11 meses y no mencionas desde hace 30 días queda más lejos que alguien nombrado una vez ayer. Parámetros del dibujo: versión completa, §7.9.2.

---

## 7.7 La pantalla Red

Quién conoce a quién, como un **grafo**: **nodos** (personas) unidos por **aristas** (líneas). Está en `PantallaRed` (`ui/pantallas/Lote3.kt`) y `RedPersonas` (`data/Lote3.kt`).

**Vínculos declarados.** En la ficha de una persona, «¿A quién conoce?» permite marcar, con toques sucesivos, «se conocen», «familia entre sí», «pareja», «colegas» o «no se conocen» (`RedPersonas.siguiente`). Se guarda un `Vinculo(a, b, tipo)` **simétrico** y con nombres canónicos. «No se conocen» es información explícita: ese par **no** cuenta como unido, aunque aparezcan juntas.

**Cálculo** (`RedPersonas.calcular(minJuntos = 2, max = 45)`), con **todo el historial**: los nodos son las **45** personas con más días **más** las que tengan algún vínculo declarado; las aristas, los pares con `juntos ≥ 2` **o** declarados, y cuentan todas menos las «no se conocen». El **grado** de un nodo es con cuántas personas está unido.

**Las tres cifras.** **Personas** (*n*). **Densidad**: qué fracción de las líneas posibles existe, líneas que cuentan / (*n*(*n*−1)/2), incluyendo a las personas sin líneas. **Grupos** (`componentes`): cuántas **componentes** (grupos conectados entre sí por algún camino) hay, **sin contar a quienes no tienen ninguna línea** visible. *Ejemplo:* 10 personas, un triángulo A-B-C, D-E y E-F, G-H, e I y J sueltas: densidad = 6/45 = **13 %**; grupos = **3**.

**El dibujo.** `RedPersonas.acomodar` usa un **modelo de fuerzas** del tipo de Fruchterman y Reingold (1991): todos los nodos se repelen como imanes del mismo polo y cada línea es un resorte que acerca a los unidos; tras **300** vueltas, los que se conocen quedan juntos. **[Validado]** como técnica de dibujo; no es una medida (parámetros: versión completa, §7.10.5).

**Cuándo falla.** La densidad depende de cuántas personas sueltas haya: con 45 nodos hay 990 pares posibles, y las personas poco mencionadas y sin líneas la bajan sin que tu red sea «menos unida».

---

## 7.8 Métricas en R: `vm_red()`

La exportación usa `RedPersonas.calcular(minJuntos = 1, max = 400)` y escribe `vm_red_nodos.csv` (persona, relación, grupo, cercanía, `dias_mencion`, `grado`) y `vm_red_vinculos.csv` (`a`, `b`, `tipo_declarado`, `dias_juntos`). `vm_red(declaradas = FALSE, min_juntos = 2, min_dias = 3)` quita siempre los pares «no se conocen», se queda con los declarados **o** (si `declaradas = FALSE`) con los que tienen `dias_juntos ≥ min_juntos`, deja fuera a quien se mencionó menos de `min_dias` días salvo que tenga una línea dicha (desde 10e, D.106; con `min_dias = 0`, todos los nodos del archivo, como antes; el 3 es **[Diseño propio]**), arma el grafo y calcula con `igraph`:

- **`densidad`** = `igraph::edge_density(g)`, sin el ego.
- **`componentes`** = `sum(igraph::components(g)$csize > 1)`, como la app, y aparte **`aisladas`** = `sum(igraph::components(g)$csize == 1)`.
- **`restriccion_burt`**: agrega un nodo «yo» unido a todas las personas y calcula `igraph::constraint` para «yo».
- **`grado`** y **`composicion`** (`table(relacion)`): cuántas personas hay de cada relación, una de las descripciones más usadas de una **red egocéntrica**, la vista desde una persona central (el ego, tú) con sus contactos y las relaciones entre ellos (Perry et al., 2018).

**Restricción de Burt.** Burt llamó **huecos estructurales** a la ausencia de conexión entre dos de tus contactos: si conoces a dos personas que no se conocen entre sí, tú eres el puente, y eso da acceso a ideas y opciones distintas (Burt, 2004). La **restricción** (*constraint*) mide lo contrario: qué tanto tu atención está «encerrada» en contactos redundantes, que se conocen entre sí. La fórmula (la que documenta `igraph::constraint`) es

$$C_i = \sum_{j \ne i} \Big(p_{ij} + \sum_{q \ne i,j} p_{iq}\,p_{qj}\Big)^2, \qquad p_{ij} = \frac{a_{ij}+a_{ji}}{\sum_{k \ne i}(a_{ik}+a_{ki})}$$

donde *a*<sub>ij</sub> = 1 si *i* y *j* están unidos; sin pesos, *p*<sub>ij</sub> es 1/(grado de *i*). El término de adentro suma la inversión **indirecta**: si tus contactos se conocen, cada uno te «llega» también por los demás y la restricción sube.

*Ejemplo (Figura 7.1).* Tú y 6 personas. Si **nadie se conoce**, cada *p* = 1/6 y *C* = 6 × (1/6)² = **0.167** (en general, 1/*n*). Con **dos grupos de 3**, para cada contacto: directo 1/6, indirecto 2 × (1/6)(1/3) = 1/9; (5/18)² × 6 = **0.463**. Si **todos se conocen**, (11/36)² × 6 = **0.560**. Con **dos grupos y un puente** entre ellos, **0.483**: para ti, el puente sube la restricción, porque tus dos mundos ya no dependen solo de ti. Coinciden con `networkx.constraint`; no pude ejecutar R para confirmarlos con `igraph` **[sin verificar]**.

![Figura 7.1](figuras/fig18_burt_puente.png)

*Figura 7.1. Restricción de Burt del ego y densidad en cuatro redes de 6 personas (datos sintéticos). Script: `figuras/fig18_burt_puente.py`.*

**Validez.** Las métricas son estándar **[Validado]** como definiciones (Burt, 2004; Perry et al., 2018). Lo que no está validado es la red sobre la que se calculan **[Aproximación]**: las líneas derivadas son co-menciones y los nodos, personas **mencionadas**. Además, la restricción y la densidad dependen mucho del número de personas: con el ego unido a todas, la restricción mínima es 1/*n*, y con 400 personas puede bajar hasta 0.0025 solo por haber mencionado a mucha gente una vez (H-6-8). Por eso, desde 10e, `vm_red()` pide por omisión 3 días de mención; para comparar en el tiempo, conviene usar el mismo `min_dias` en todos los periodos. Entre la app y R quedan dos diferencias, documentadas en el diccionario: personas (45 frente a hasta 400) y grado del CSV (1 día juntas, no 2) (H-6-7).

---

## 7.9 Cómo entran las personas en las correlaciones

Las variables de la sección 7.4 entran en Explorar con el mismo método que las demás (capítulo 6). Lo específico: solo 15 `per_` y 10 `pen_`, así que las personas poco frecuentes **no se buscan** en la app (en R sí, con `vm_panel_extendido.csv` y `variables = c(...)`). Se excluyen los pares **obvios**, que se mueven juntos por construcción (`Estadistica.obvia`): cada `per_` con las variables agregadas de personas (`Dinamicas.AGREGADOS["per_"]`), cada `pen_` con `personas`, `personas_pensadas` y los `penso_en_*`, `per_x` con `pen_x`, y las familias de `Estadistica.FAMILIAS` (lista: versión completa, §7.12); R lee la misma lista en `vm_familias.csv`. Dos personas entre sí (`per_ana` con `per_beto`) no se excluyen: su relación es su co-mención, la misma información de la Red.

---

## 7.10 Privacidad: dónde entran los momentos privados

Un momento es privado si su tipo es `impulso`, `recaida` o `libido` (`Momento.privado`, regla única en `data/Privado.kt`). Sus @menciones y #temas **no entran en nada de este capítulo** (presencias, `vi_*`, `per_`, `pen_`, Constelación, Red, `vm_red_*`, «Tu gente» ni las sugerencias al escribir), con o sin el seguimiento privado activado. Solo se ven en la pantalla Privado y van completos a `vm_momentos.csv`. Hasta v84, recaídas y libido entraban en `per_` y en «Tu gente», y los impulsos en todo (H-6-1).

---

## 7.11 En resumen

1. La app reconoce a las personas **solo por `@nombre`**, con equivalencias en cadena; cada función lee textos distintos, así que «aparecer» no significa lo mismo en todas partes (H-6-9).
2. La **relación con historial** es una buena decisión: los análisis usan la relación de cada día. Límites: si no dices cuándo cambió, vale la fecha de la anotación (desde 10e se puede elegir; H-6-3, en parte), lo anterior a la primera clasificación se proyecta y Conocerte no tiene historial (H-6-4).
3. La **cercanía** usa la escala IOS, validada, con etiquetas y respondida una vez por persona: `cercania_media` describe a quién nombras, no cómo te fue ese día.
4. Un 0 en las variables sociales es «no la nombré». **No hay variable por persona de «estuve con @x»** (H-6-2).
5. La **Constelación** es una visualización de frecuencia y recencia; la **Red** combina co-menciones (≥ 2 días), una aproximación débil, con vínculos declarados y un dibujo de fuerzas estándar.
6. `vm_red()` calcula densidad, componentes, **restricción de Burt** y composición; las definiciones son estándar, pero los resultados dependen mucho del número de personas incluidas, y desde 10e deja fuera por omisión a quien se mencionó menos de 3 días (H-6-8).
7. Los momentos privados no entran en nada de este capítulo.
8. Ninguno de los 15 hallazgos del módulo 6 queda pendiente: 10 se resolvieron (H-6-8 en 10e, y H-6-15, las diferencias con la IOS original, con su descripción) y 5 en parte (H-6-3, H-6-4, H-6-9, H-6-11 y H-6-13).
