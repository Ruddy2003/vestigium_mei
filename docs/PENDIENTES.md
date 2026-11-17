# Pendientes y forma de trabajo (17 nov 2026)

Lo que quedó sin hacer cuando se juntaron en `main` las escenas (tandas S9 a S18) y el plan de mejoras con los cinco temas nuevos, y cómo se trabajó, para poder retomarlo sin la conversación original. El detalle de cada cosa está en el documento que se cita; aquí está la lista entera en un solo sitio.

## 1. Cómo se ha trabajado

- **Reparto.** Quien dirige la sesión lee el cuento, escribe la composición de cada cuadro, mira las imágenes y escribe la revisión. **Todo el código y el dibujo lo hacen agentes con Opus** (decisión del autor: gastar lo menos posible del modelo más caro sin perder calidad). El cierre de cada tanda (mezclas, pruebas, documentos, informe, Actions) también lo hace un agente.
- **Una tanda S, paso a paso:**
  1. Un agente lee el cuento y el calendario de la vida y escribe la «lectura»: qué tiene que verse, estados, arcos, objetos, días señalados, riesgos de parecido.
  2. Quien dirige fija la composición (desde dónde se mira, qué manda, la luz, lo que no se repite de otros cuadros).
  3. Un agente por cuadro, en su propio árbol de trabajo, dibuja en el arnés de escritorio al lado de Helénico y de los cuadros aprobados más parecidos. Primero la composición sola; luego el detalle.
  4. Quien dirige mira una hoja por entrega y devuelve una revisión con puntos concretos por orden de peso. Se repite hasta que llega (dos a cuatro rondas).
  5. Aprobada la composición, dos agentes más por tema: los doce objetos del día, y la banderita con el gato.
  6. El agente del cuadro integra objetos, banderita y gato, y hace el pulido en la misma ronda.
  7. Mezcla en la rama de trabajo, y cierre: pruebas completas, limpieza sin cambiar un píxel, registro (`DIAS_CUADRO`, `TEMAS.md`, `ESCENAS.md`, la fila del plan), imágenes de referencia, barrido de 2026 a 2032, peso y capas, informe en `docs/historia/informes/S<n>.md`, y capturas y compilación en Actions.
- **Reglas de proceso que salieron caras cuando no se cumplieron:**
  - No más de tres agentes pesados a la vez en una máquina de 4 núcleos y 16 GB.
  - Los árboles de trabajo de los agentes nacen de una rama vieja: lo primero es traer la rama de trabajo.
  - **Compilar el conjunto antes de subir una mezcla:** dos temas que compilan por separado pueden no compilar juntos (pasó con dos clases del mismo nombre en `ui/escenas/`). Los nombres de primer nivel llevan el tema.
  - Archivos de menos de unas 600 líneas y funciones cortas: el compilador de CI va justo de memoria.
  - **Un agente no se queda parado esperando un trabajo en segundo plano:** el contenedor se da por inactivo y se reinicia. Lo largo se parte en trozos y se consulta en primer plano; commit y push después de cada paso.
  - Un agente que dice que le negaron un permiso no se sustituye: se le cuenta al autor.

## 2. La vara

- **Helénico es el piso, no el techo.** En cada aspecto, el nivel es el del cuadro aprobado que mejor lo hace (`docs/diseno/ESCENAS.md`, sección 2 bis).
- **La lógica de la luz:** luz con forma sobre penumbra de color; que camina con la hora; que se ve en el aire; que enciende lo pintado; y de noche, dos luces opuestas (una cálida que alumbra de verdad y una fría).
- **Lo que más se devolvió en las revisiones:**
  - la composición antes que el detalle (un punto de vista mal elegido no se arregla pintando);
  - el primer plano que es una repisa vacía, sin nada grande a la altura de los ojos;
  - los alzados de frente y las franjas paralelas;
  - planos grandes de un solo color, sin materia;
  - lo repetido como pegatina (casas, bocas de mina, almenas iguales);
  - líneas de regla en lo natural;
  - de noche, lo encendido que no alumbra;
  - prendas colgadas que parecen una figura;
  - el gato mal alumbrado por el cuadro (cabeza clara y cuerpo oscuro; sin lado cálido junto a la lumbre).
- **Contenido:** sin personas ni nada que lo parezca, sin letras ni números, sin iconografía de ninguna obra conocida; **manda el cuento, no el tema de la app**; los signos inventados se comprueban contra alfabetos reales.
- **Límites técnicos:** peso por debajo de 1.5 veces Helénico; hasta seis capas de sombra en la parte quieta y ninguna en la viva; determinista; barrido de siete años sin fallas.
- **Si no alcanza el tiempo, menos cuadros bien hechos, no más cuadros peores.**

## 3. Lo pendiente

### Escenas
- **S19, Colonos:** el único de los 34 temas de antes sin cuadro.
- **Los cuadros de los cinco temas nuevos:** ninguno tiene todavía (Frente exterior, Arcontes, Galia, Art nouveau y Psicodelia). Lo que necesita quien dibuje Psicodelia y Frente exterior está en `docs/diseno/PLAN_MEJORAS.md`.
- **El sonido de ambiente** de los cuentos de las tandas S9 a S18 (`herramientas/ambientes.py`): sin archivo, no suena nada.
- **El peso de noche de Tierras Antiguas:** 1.69 a 1.77 a tamaño grande, por encima del límite; Orden y la noche de Laboratorio, en 1.49. Mirarlo en el teléfono y decidir si se aligera.
- **Cuadros aparte que pide el calendario y hoy son una variante del cuadro principal:** el cañón del sur (la expedición de Orden), Torremuda (la travesía de Tierras Antiguas), las ruinas de arriba (Laboratorio), el cuarto de 2030 (Abismo), el hospital de Prusia y el pueblo de Borgoña (Napoleónico), la gira y la época sin club (Art déco), el sanatorio (Soviético) y los lugares en tierra de Botánico.
- **Calendarios que se repiten o vuelven atrás desde 2032** (el cuadro sigue a la ficha): Refugio, Laboratorio, Orden, Tierras Antiguas.
- **Marcas inventadas de Tierras Antiguas:** rehechas porque coincidían con runas reales; las nuevas se comprobaron de memoria, sin tabla.
- **Biblioteca `paisaje/`:** posible desplazamiento equivocado de la parte de muro en `sombraArrojada` (sin verificar; no se tocó porque cambiaría cuadros aprobados). Y la pregunta de si la perspectiva pequeña que varios cuadros repiten pasa a la biblioteca.
- **Lo flojo de cada cuadro** está dicho sin adornos al final de cada informe (`docs/historia/informes/S9.md` a `S18.md`).

### Aprobado por el autor el 17 nov 2026 («visto bueno a todo»)
Los cuadros de S9 a S18 tal como están; las acciones de los gatos cambiadas respecto del gato de pixel cuando el cuento no las tenía o rozaban una obra conocida; el segundo cuadro de Laboratorio (el centro del Claustro) y cuándo sale; la memoria del compilador.

### Del plan de mejoras (`docs/diseno/PLAN_MEJORAS.md`)
- **Probar en el teléfono:** los arreglos de avisos y el modo según la hora (parte 1), Psicodelia (el cambio de cara y la intensidad), los fondos de pantalla con las escenas (parte 6) y lo nuevo de los cuadros (parte 8).
- **Decisión del autor:** si se rehacen las otras 36 bibliotecas y los muebles de Vida, vistos los tres pilotos.
- **Sin hacer:** lo temático nuevo (parte 7) y los logros nuevos (parte 9).
- **Sus preguntas abiertas** sobre los Idearios.

### Otros
- La rama `ccr-c2dc1f0e-ta3g8x` (un commit del 1 nov 2026 que borra las APK viejas de Actions) nunca se mezcló: decidir si se quiere.
- Ramas remotas ya mezcladas o temporales que se pueden borrar cuando el autor quiera.
