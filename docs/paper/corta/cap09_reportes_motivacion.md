# Capítulo 9. Reportes, visualización y motivación

> **Para qué sirve este capítulo.** Explica qué te devuelve la app a cambio de registrar: los **reportes** (semanal, mensual y anual), **«Tu año»**, los **logros** y las **campañas** semanales; de dónde sale cada cifra y qué decisiones de visualización se tomaron. Después discute el lado científico: un sistema de motivación ayuda a que no falten datos, pero también puede **cambiar lo que se mide**. Describe el código de la v92 (la v91 más la entrega 10f; `SCHEMA_V = 15`). **Los ejemplos son inventados.** Qué datos privados llegan a cada salida está en el capítulo 10.

---

## 9.1 De dónde salen las cifras: `Periodo.calcular`

Los reportes y «Tu año» no calculan nada por su cuenta: usan un mismo resumen del periodo, `Periodo.calcular(ini, fin)` (`ciencia/Periodo.kt`). Las reglas que más pesan:

| Cifra | Cómo se calcula exactamente |
|---|---|
| **Ánimo del día** (`valDe`) | La valencia del diario, como `valencia` en el panel y en R (desde la entrega 10c). |
| **Ánimo medio** | Promedio de los «ánimos del día» que existen. Si hubo días con valencia solo en los avisos (`diasSoloAvisos`), el reporte añade «*N* días solo con avisos no cuentan». |
| **Sueño medio** | Promedio de las noches del reloj con **más de 120 minutos** (las más cortas se descartan como siestas o errores). |
| **Hora de dormir media** | `Exportar.horaDecimal`, como el panel y R: las anteriores a las **12:00** suman 24 (la 01:00 cuenta como 25:00). |
| **Mejor marca** | `Macrofactor.e1rm`, la misma de la exportación: peso × (1 + repeticiones/30) (**fórmula de Epley**; con 1 repetición, el peso mismo), solo en series de **1 a 12** repeticiones y sin calentamientos, redondeada a 0.1 kg. |
| **Gasto e ingresos** | Por el **día del diario** (`Gasto.dia`, `Ingreso.dia`: antes de las 5:00 cuenta para el día anterior), igual que el panel. |
| **Comparación con el periodo anterior** | Solo si el periodo anterior tiene **al menos 3 días** con dato. |
| **Emociones, pensamientos y con quién** (desde el esquema 18) | `Periodo.animoDe`, con los registros de ánimo no privados: cada emoción cuenta una vez por registro, «no sé» no cuenta, «otro» va con lo que escribiste y se dice cuántas veces fue «mucho». Conteos para leer: no entran a ningún promedio ni al panel. **[Diseño propio]** |

El **periodo anterior** es el de la misma duración inmediatamente antes. Las demás cifras están en la versión completa, §9.2.

**Detalle a saber** (H-8-10 y H-5-15, resueltos en 10c, D.77). Hasta la entrega 10c, el «ánimo del día» mezclaba dos instrumentos distintos: la valencia recordada del diario y, cuando faltaba, el promedio de los registros momentáneos, que no son la misma medida (capítulo 3). Ejemplo: una semana con valencia del diario 5, 6 y 4 en tres días y avisos con promedio 3 en otros dos: antes, ánimo medio (5 + 6 + 4 + 3 + 3) / 5 = 4.2; ahora (5 + 6 + 4) / 3 = **5.0**, «2 días solo con avisos no cuentan». La **activación** del día sigue completándose con los avisos cuando falta la del diario.

---

## 9.2 Los reportes

### 9.2.1 Cuándo se generan y qué muestran

La tarea periódica (`avisos/Trabajo.kt`) genera, sin que lo pidas, **el semanal** cada **lunes desde las 8:00**, de la semana anterior (una imagen PNG), y **el mensual** cada **día 1 desde las 9:00**, del mes anterior (PNG y PDF). **El anual** se pide a mano. Se guardan en las carpetas públicas del teléfono, **`Pictures/VestigiumMei`** y **`Download/VestigiumMei`**. Desde el módulo 10a, los que se generan solos van **siempre en modo discreto** (iniciales en vez de nombres) y **sin la frase del mejor momento**; los que pides a mano siguen tu ajuste (privacidad: capítulo 10, H-8-4).

El reporte (`Reportes.cuerpo`) pone arriba hasta **9 cifras** y después **solo las secciones que tienen datos**. El mensual incluye un **mapa abstracto**: cada lugar guardado es un punto sobre una cuadrícula, **sin calles ni nombres**. **El modo discreto** (`Config.discreto`, apagado por omisión) cambia en los reportes los nombres de las personas por iniciales (`Reportes.persona`).

### 9.2.2 Decisiones de visualización

La Figura 9.1 imita «La semana de un vistazo» con datos inventados y las reglas de `semanaVisual`:

- **Ánimo**: una **medalla** por día con el color del ánimo y el número (`%.0f`), que desde 10c es la valencia del diario, un entero de 1 a 7. El color va del color de alerta (1), pasando por un gris neutro (4), al color del tema (7) (`Lienzo.colorAnimo`).
- **Diario**: hojas apiladas; una hoja = max(10, ⌈máximo de la semana / 10⌉) palabras, hasta 10 hojas.
- **Sueño**: barras de 0 a 10 h, con una línea punteada en **7 h** y la barra en tono de alerta si la noche duró **menos de 6.3 h**.
- **Pasos**: ⌈pasos / 2 000⌉ huellas, hasta 10.

![Figura 9.1](figuras/fig23_reporte_semanal.png)

*Figura 9.1. «La semana de un vistazo», imitada con datos inventados y las mismas reglas del código. El ánimo de la figura trae dos días con decimales (4.5 y 6.5), como podía pasar hasta 10c; desde entonces el número de la medalla siempre es un entero. Script: `figuras/fig23_reporte_semanal.py`.*

**Qué dice la investigación sobre gráficas.** Cleveland y McGill (1984) midieron qué tan bien la gente lee distintas codificaciones: la **posición sobre una escala común** (la altura de una barra con su eje) se lee con mucha más precisión que el **área** o el **color**. Aplicado a la app **[Aproximación]**: las barras de sueño, con eje y número, son la codificación más precisa; la medalla del ánimo codifica con color, que se lee mal, pero lleva el número escrito; las hojas y huellas son **conteos de iconos**, buenos solo para comparaciones gruesas. La línea de 7 h y el corte de 6.3 h son **[Diseño propio]**: 7 h coincide con la recomendación habitual mínima de sueño para adultos **[sin verificar]**; el 6.3 h no tiene una fuente conocida. El color «de alerta» **juzga** el dato (rojo = malo), lo que refuerza la idea de que hay valores «correctos» (sección 9.6).

### 9.2.3 Comparar con el periodo anterior

`Reportes.cambio` escribe «+0.4 de ánimo frente al periodo anterior» cuando la diferencia es al menos **0.05** (o **0.5** en las cifras sin decimales); si no, «igual que el periodo anterior». Hasta 10e no había ninguna noción de ruido (H-8-11). Desde 10e (D.101), si la diferencia es menor que su **error típico**, √(*s*₁²/*n*₁ + *s*₂²/*n*₂) (`Periodo.errorDif`, con al menos 3 días en cada periodo), dice «parecido al periodo anterior». *Ejemplo:* ánimo 5, 6, 4, 5 esta semana y 4, 6, 3, 5, 5 la anterior: diferencia 0.4, error típico 0.65, «parecido». **[Aproximación]**: un error típico es poco exigente y no descuenta que los días seguidos se parecen, así que el filtro se queda corto; conviene leer estas flechas como «qué pasó», no como «qué cambió de verdad».

---

## 9.3 «Tu año»

`TuAnio.calcular` usa el mismo `Periodo` del año y le suma equivalencias, récords, la palabra del año y otras piezas (versión completa, §9.3.5). Tres merecen atención:

- **El día más raro** sale de `Analisis.atipicos` (umbral 2.8, 15 variables fijas). Desde 10e usa todos los días del año elegido (D.100); antes solo devolvía días de los últimos 365 contados desde hoy, y al ver un año pasado podía faltar.
- **«La relación del año»** es la correlación no obvia con mayor |*r*|, con *q* < 0.05 y *n* ≥ 30, si el año tiene al menos 40 días. Elegir el máximo |*r*| entre cientos de pares sobrestima su tamaño: es la que tuvo más suerte además de ser real (un sesgo de selección; capítulo 6). El *r* mostrado será, en promedio, más alto que el verdadero. **[Aproximación]**.
- **Las predicciones de enero** (8 preguntas) incluyen tu **ánimo medio**: crean una expectativa sobre la variable que luego mides. Es una forma de **reactividad**, es decir, de que medir algo cambie lo que se mide (sección 9.6).

---

## 9.4 Logros

### 9.4.1 Qué son

Un **logro** es una medalla que se gana al llegar a una meta (`ciencia/Logros.kt`), en cuatro niveles (bronce, plata, oro, legendario). **Se calculan de tus datos cada vez**; lo único que se guarda es cuáles ya viste. Tres tipos principales:

| Tipo | Ejemplos (metas exactas) |
|---|---|
| **Cantidad** (`escalones`) | Diario: 1, 7, 30, 100, 365 y 1000 días; avisos contestados: 50, 250 y 1000 |
| **Récord** | 20 000 pasos en un día; reacción con mediana < 250 ms |
| **Constancia** (`constancia`) | Escribe 20, 25 o 28 de 30 días; 7 h de sueño 15, 20 o 25 de 30 noches; cada hábito activo 15, 20 o 25 de 30 días (salvo el ligado a las recaídas) |

Además hay **rachas** de diario (7, 30 y 100 días seguidos), **secretos** que aparecen al lograrlos y **logros propios** (una fuente, un umbral y un modo que eliges), que desde el módulo 10a no se ofrecen sobre nada privado (versión completa, §9.4.1).

### 9.4.2 Constancia en lugar de rachas

Una **racha** son días **seguidos** cumpliendo algo: un solo día sin cumplir la rompe. La **constancia** es cumplir **la mayoría** de los días de una ventana (25 de 30), sin necesidad de racha. Las rachas castigan un día perdido con volver a cero; la constancia tolera días malos. **[Diseño propio]** con buen fundamento: para un registro de por vida lo que importa es **no abandonar**, y «25 de 30» premia eso sin convertir un tropiezo en fracaso.

### 9.4.3 Lo que premian, y por qué importa para los datos

| Premian… | Ejemplos | Efecto sobre los datos |
|---|---|---|
| **Registrar** | días de diario, avisos contestados, momentos | Menos datos faltantes (bueno). Riesgo: entradas de relleno. |
| **Una conducta** | pasos, sueño ≥ 7 h, entrenar, hábitos | Cambian la conducta medida: es una intervención. |
| **Un valor de la medida subjetiva** | «Día perfecto» (7 de 7), «Remontada» (un ≥ 6 tras un ≤ 2 en los 3 días de calendario anteriores) | Premian **un número que tú escribes** (H-8-6). Se quedan por decisión del autor (D.117). |
| **Un rendimiento** | «Rayo» (reacción < 250 ms) | Los tiempos pueden mejorar por práctica y no por estar más despierto. |

Los dos logros del tercer grupo son **secretos** y se ganan una sola vez, lo que reduce mucho el incentivo. **[Diseño propio]**, de bajo riesgo en la práctica, pero contrario al principio de no premiar el valor de una medida. **Desde 10f se quedan como están, por decisión del autor** (D.117). El riesgo que queda es **un incentivo pequeño a sesgar la valencia del diario** (poner un 7 en vez de un 6), que desaparece en cuanto se gana cada uno; que sean «secretos» casi no protege, porque el único usuario, el autor, sabe que existen. Si se sospecha el efecto, se puede comparar la proporción de 7 antes y después del día en que se ganó (más en la versión completa, §9.4.3).

---

## 9.5 Campañas

Una **campaña** (`ciencia/Campanas.kt`) es un reto opcional de lunes a domingo con una meta de días; al aceptarla se puede elegir de 3 a 7. Hay 11 tipos de reto, por ejemplo «Noches de 7 horas» (el reloj marca ≥ 420 min de sueño; meta 5) o «Menos de 3 horas de pantalla» (pantalla < 180 min; meta 4); la tabla completa está en la versión completa, §9.5. Según `Campanas.estado`, cada día cumplido avanza una etapa en un «mapa» narrativo; se **gana** al llegar a la meta y se **pierde** cuando ya no da tiempo (días cumplidos + días que quedan < meta).

**Qué son científicamente.** Una campaña de sueño **es una intervención**: esa semana intentas dormir más. **Hasta el esquema 12 no se guardaban como variable del panel** (solo vivían en `Config.campanas`) (H-8-7). Si en esas semanas también estuviste de mejor ánimo, la app veía «dormir más se asocia con mejor ánimo», sin poder distinguir si fue el sueño, el empujón de la campaña o lo que te llevó a elegirla. Un experimento personal (capítulo 8) separa esto; una campaña, no.

**Desde el esquema 13 (P-11) la campaña es una variable del panel.** Hay una columna `campana_<reto>` por cada reto que hayas tenido (por ejemplo `campana_sueno7`): vale **1** los días que tenías ese reto aceptado y **0** los demás, desde el primer día de tu primera campaña; antes, sin dato. `campanas_n` dice cuántas tenías ese día (puede haber hasta tres a la vez). Codifica la **asignación**, no el cumplimiento. Cada `campana_<reto>` se marca como «obvia» con lo que ese reto busca mover (`Campanas.MUEVE`; capítulo 6), para que no aparezca como hallazgo «en las semanas de campaña de sueño dormiste más». En R, desde la entrega 10d, `vm_obvia` usa la misma regla (`vm_familias.csv`). Lo que sí queda para el análisis es lo interesante: si en esas semanas cambió **otra** cosa, como el ánimo. **[Diseño propio]**.

También desde el esquema 13, una campaña aceptada el miércoles ya no cuenta el lunes ni el martes (`Campana.aceptada`; H-9-34), y abandonarla no la borra (H-9-36; más en la versión completa, §9.5). Al leerlas, recuerda que una campaña es **autoelegida**, no sorteada y que varias semanas seguidas de la misma hacen que la variable cambie poco y pueda clasificarse como «lenta» (capítulo 6).

---

## 9.6 Motivar sin distorsionar

**Lo que dice la evidencia.** Deterding et al. (2011) definen la **gamificación** como el uso de elementos de diseño de juegos (puntos, medallas, retos, rachas) en contextos que no son juegos. La revisión de Hamari, Koivisto y Sarsa (2014) encontró que la mayoría de los estudios reportan efectos positivos, pero que dependen mucho del contexto y de quién la usa. **[Validado]** que *puede* funcionar; no que funcione siempre. El metaanálisis de Deci, Koestner y Ryan (1999) encontró que las recompensas **tangibles y esperadas** reducen la motivación intrínseca (hacer algo porque te interesa), mientras que la retroalimentación verbal positiva tiende a aumentarla. Las medallas de la app están a medio camino **[Aproximación]**. Además, el autoseguimiento ya es reactivo: tiende a aumentar las conductas deseables y a reducir las indeseables (Korotitsch y Nelson-Gray, 1999); premiar ciertos valores suma un segundo empujón. En los rastreadores personales, el abandono es uno de los problemas centrales (Li et al., 2010).

**El dilema.** La app tiene dos objetivos que tiran en direcciones opuestas: **que no falten datos** (los que faltan no faltan al azar, capítulo 3) y **que los datos midan la vida tal como es**. Si registrar o ciertos valores dan premios, los datos empiezan a medir, en parte, el deseo de ganar el premio. Es la **ley de Campbell**: cuanto más se usa un indicador para premiar o decidir, más presión hay para distorsionarlo y más deja de reflejar lo que medía (Campbell, 1979).

**Cómo lo resuelve la app:**

- **Bien**: la mayoría de los logros premian **registrar** o **conductas objetivas medidas por sensores** (pasos y sueño del reloj, pantalla del teléfono), no lo que escribes.
- **Bien**: la constancia tolera días malos, así que no hay incentivo para inventar un día… salvo en las rachas de diario.
- **Mejorable**: «Día perfecto» y «Remontada» premian valores de la valencia (H-8-6) y se quedan por decisión del autor (D.117); las predicciones de enero incluyen el ánimo medio. Las campañas, desde el esquema 13, sí quedan como variable (H-8-7, resuelto).
- **No se puede evitar**: que ver tus reportes cambie tu conducta. Es, en parte, el propósito de la app.

**Recomendación práctica** (no requiere cambiar código): al analizar una variable que también fue meta de una campaña o un logro, mirar si su relación con otras cambia antes y después de empezar a perseguir esa meta.

---

## 9.7 En resumen

1. Los reportes y «Tu año» usan un mismo resumen, `Periodo.calcular`; desde la entrega 10c su «ánimo del día» es solo la valencia del diario (H-8-10).
2. Los reportes se generan solos (lunes y día 1) y se guardan en carpetas públicas; los automáticos van en modo discreto y sin la frase del mejor momento (capítulo 10, H-8-4).
3. La visualización combina codificaciones precisas (barras con eje y números) con otras gruesas (colores, iconos) **[Aproximación]**; las comparaciones con el periodo anterior dicen «parecido» desde 10e cuando la diferencia es menor que su error típico, un filtro que se queda corto (H-8-11).
4. «La relación del año» es el máximo |*r*| de muchos pares y sobrestima su tamaño **[Aproximación]**.
5. Los logros premian sobre todo **registrar** y **conductas medidas por sensores**, y prefieren la **constancia** a las rachas **[Diseño propio]**. Dos logros secretos premian valores de la valencia (H-8-6); desde 10f se quedan por decisión del autor (D.117), con un riesgo pequeño y documentado.
6. Las campañas son **intervenciones**. Desde el esquema 13 son variables del panel (`campana_<reto>`, `campanas_n`), obvias con lo que cada reto mueve (H-8-7, resuelto).
7. La gamificación puede ayudar a no abandonar, con efectos que dependen del contexto (Hamari et al., 2014); las recompensas pueden cambiar la motivación y lo que se mide (Campbell, 1979).
