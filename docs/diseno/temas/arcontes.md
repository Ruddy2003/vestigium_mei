# Arcontes (`arcontes`): guía de diseño

La ficha del tema está en `docs/diseno/PLAN_TEMAS.md`, lote 10d. Esta guía baja la ficha a valores concretos para que cada parte del tema (y cada ayudante que la programe) siga la misma línea. Si algo de aquí choca con la ficha, manda la ficha; si choca con `UnicidadTemas`, se cambia aquí y se anota. Las citas y las fechas se comprueban en `docs/diseno/temas/fuentes_arcontes.md`: lo que esa tabla no sostiene no se usa.

## La idea, en una frase

Galilea hacia el año 30, vivida por un iniciado que cree el mito gnóstico como verdad: el mundo que se ve es una cárcel bien dorada, hecha por un dios menor; sus guardianes, los arcontes, vigilan las siete esferas del cielo; detrás hay una luz verdadera, el Pleroma, y una chispa de ella en cada persona. Todo se ve como la Galilea real del siglo I (caliza, lino, barro, el lago, las barcas, la lámpara de aceite) y, por las grietas, se adivina lo otro. El tono es **inquietante**: nada de monstruos a la vista; siluetas, sombras y una luz que no es de este mundo.

## Lo que sí y lo que no

- **Sí:** lo que dicen los textos antiguos (Nag Hammadi, el Papiro de Berlín, el Códice Tchacos, el Códice Askew, Ireneo, Orígenes, 1 Enoc, Qumrán, el Testamento de Salomón, las gemas de Abraxas), con su cita.
- **Sí, como recurso** (se dice que es recurso): cómo se dibuja (las esferas como el diagrama de los ofitas, la grieta de luz, el velo), cómo se cuenta la fecha en la app.
- **No:** creencias inventadas, poderes que funcionan, hechizos, ni la estética del ocultismo moderno (pentagramas de esoterismo, calaveras, velas negras, letras «satánicas»). No es Horror cósmico: aquí no hay nada «innombrable»; el iniciado conoce los nombres y las contraseñas.
- **No, por anacronismo:** la cruz como adorno (como símbolo cristiano es posterior), la estrella de seis puntas como emblema judío (es medieval), el pez de los cristianos (del siglo II en adelante), iglesias, aureolas, iconos.
- **Jesús nunca se ve de frente:** solo como una figura de luz de espaldas o de lado, sin cara. Sus palabras son siempre citas.
- **El nombre de Dios** (las cuatro letras hebreas) no se escribe. Por la misma costumbre, el 15 y el 16 en letras hebreas se escriben ט״ו y ט״ז, nunca י״ה ni י״ו.

## Claro y oscuro: el mismo mundo

El mismo lugar: la orilla de Magdala, la casa del salador, el lago. De día, el mundo visible en todo su brillo, un brillo de más (el dorado del Demiurgo); de noche, la misma Galilea con el cielo antiguo encima, las esferas, la lámpara y la luz por las grietas.

| Papel | Claro (Galilea al sol) | Oscuro (la misma Galilea de noche) |
|---|---|---|
| fondo | `#EEE3C6` caliza al sol, con su polvo dorado | `#0B0D1C` azul negro del cielo antiguo |
| superficie | `#F7F0DD` lino | `#13162A` |
| variante | `#E3D4AF` | `#1C2038` |
| borde | `#BBA673` | `#343A5A` |
| tinta | `#1E1A2B` el negro violáceo de lo que hay debajo | `#ECE4CF` lino a la luz de la lámpara |
| tenue | `#57506A` | `#A09CB0` |
| primario | `#87590F` ocre dorado, el oro del mundo visible | `#E9B04F` la llama de la lámpara de aceite |
| sobre primario | `#FFF8E8` | `#2B1A00` |
| contenedor | `#F1DDA8` | `#4A3410` |
| sobre contenedor | `#2E1E02` | `#FBE6BE` |
| secundario | `#2B5B6C` el lago | `#86BBCB` el lago bajo la luna |
| contenedor secundario | `#CCDFE3` | `#1B3542` |
| terciario | `#5A3B8F` el violeta de la grieta | `#C4B0FF` la luz por la grieta |
| error | `#9E2B25` | `#FF8A7A` |

Contraste sobre el fondo (WCAG): tinta 13.3 y 15.2; tenue 6.0 y 7.2; primario 4.7 y 9.9; secundario 5.8 y 9.2; terciario 6.7 y 10.1.

Colores de dibujo que se repiten en las figuras (`TrazosArcontes`, en `ui/ArcontesTrazos.kt`):
- **caliza** `#D9CBA6` (en sombra `#9C8A68`) y **lino** `#EFE6D0`;
- **barro** de lámparas y jarras `#8A4E2B` (cocido claro `#B9774A`);
- **oro del Demiurgo** `#D4A62A`: el dorado de más, solo en lo que es del mundo de abajo (el sol, los mosaicos, el sello de los arcontes);
- **lago** `#3F7A8A` y **lago hondo** `#1F4652`;
- **cielo antiguo** `#0B0D1C` y las **esferas** `#6E6A9E`, en círculos finos;
- **la grieta**: blanco `#F4F1FF` con su halo violeta `#9C7BFF`; es la única luz que no viene de una llama ni del sol;
- **la llama** `#F0B24A`, con su halo `#FFD98A`;
- **sombra de arconte** `#1A1424`: siluetas que solo se intuyen, a veces con su cara de animal, nunca enteras;
- **jaspe** de las gemas: verde `#3E6B4E` y rojo `#8E3A2E`; **hematita** `#3A3A40`;
- **cuero** del códice `#6B4A2E`, con su correa `#4A321E`.

Paleta corta: caliza y lino, ocre y oro, el azul del lago, la noche azul negra y el blanco violeta de la grieta como lo único que no es de este mundo. Nada de rojo sangre ni de verde de horror (Horror cósmico); nada de azul eléctrico.

## Letra y forma

- **Títulos:** Caudex (OFL), la letra de los códices; en negrita, con mayúsculas y minúsculas.
- **Texto:** Literata (OFL), serifa de lectura, a 400 y 600 (`opsz` 12).
- **Etiquetas y cifras:** Caudex normal.
- **Hebreo y copto dentro de las nuestras** (como los numerales mayas de Mesoamérica en `herramientas/fuentes.py`): Android trae el hebreo y el copto solo en letra de palo, así que las letras hebreas (U+05D0–05EA, el geresh y el gershayim U+05F3–05F4) se copian de **Noto Serif Hebrew** y las coptas (U+2C80–2CFF y U+03E2–03EF) de **Noto Sans Coptic** (OFL las dos) a las salidas de Caudex y Literata.
- **Esquinas:** `Esquinas(5, Esquina.ARCO)`, el arco pequeño de los nichos tallados en la caliza y de las ventanas de piedra (la pareja está libre).
- **Lo que manda la legibilidad:** el hebreo y el copto son adorno de títulos y números; siempre llevan su español al lado en el reporte del día (principio 7).

## La fecha y los números

- **El cielo del día:** «Cielo 212 de 365», el día del año. Ireneo cuenta que Basílides enseñaba que hay 365 cielos (*Contra las herejías* 1.24.3–7), e Hipólito, que por eso el año tiene 365 días (*Refutación* 7.26). **Recurso:** el 31 de diciembre de un año bisiesto (el día 366) se muestra como «Cielo 365» otra vez, porque el texto habla de 365.
- **El planeta del día:** la semana planetaria antigua (domingo Sol, lunes Luna, martes Marte, miércoles Mercurio, jueves Júpiter, viernes Venus, sábado Saturno). Solo el planeta: qué arconte va con cuál no se pudo comprobar en el texto, y hay tres listas de siete que no se mezclan (`fuentes_arcontes.md`, corrección 16).
- **La fecha hebrea:** con `android.icu.util.HebrewCalendar` (existe desde la API 24; la app pide 30). **Recurso:** es el calendario hebreo calculado de hoy; en el siglo I el mes empezaba cuando se veía la luna, así que podía variar uno o dos días.
- **Números:** con letras hebreas que valen números (א=1… ט=9, י=10… צ=90, ק=100… ת=400), con el gershayim antes de la última letra (כ״ג = 23) o el geresh si es una sola (ה׳ = 5). Los miles, con la letra de las unidades y un geresh delante. El 15 y el 16, ט״ו y ט״ז.
- **Los títulos:** algunos llevan su palabra en arameo, hebreo o copto, con su español al lado.

## La voz

Solemne y en voz baja, como quien comparte un secreto con alguien de confianza. Habla de lo que ve en la Galilea real y de lo que cree que hay detrás. Tutea. Nunca amenaza, nunca promete poderes.
- **Ejemplos:** «La chispa sigue encendida», «Guardado en la jarra», «Una puerta más», «Lo de abajo no lo sabe», «Que la lámpara no se apague».
- **Las frases del día:** citas de los textos (Evangelio de Tomás, de Felipe, de María, Apócrifo de Juan, Pistis Sofía, 1 Enoc, la Regla de la guerra) en traducción propia, o de los evangelios canónicos en la Reina-Valera de 1909. Cada una con su cita exacta.
- **Lo que nunca dice:** «magia», «hechizo», «demoníaco» como adjetivo de miedo, «iluminado», «Illuminati», nada del ocultismo moderno ni de Horror cósmico.
- **Niveles:** las tres clases de personas de los valentinianos (de materia, de alma y de espíritu: **hílico**, **psíquico** y **pneumático**), según Ireneo, *Contra las herejías* 1.7.5 (comprobado en `fuentes_arcontes.md`).

## Los motivos de las figuras

- **El emblema:** el «amuleto de Abraxas» (muchas de esas gemas son amuletos de magia, no obra segura de gnósticos), como en las gemas antiguas (cabeza de gallo, cuerpo con coraza, piernas de serpiente, látigo y escudo), dentro del ouroboros, la serpiente que se muerde la cola. Grabado, como sobre jaspe.
- **Las siete esferas:** círculos finos uno dentro de otro, como el diagrama de los ofitas que describe Orígenes; giran despacio de noche (**recurso**).
- **La grieta de luz:** una raja fina en la piedra, el velo o el cielo, por donde sale el blanco violeta. Es el motivo del Pleroma; siempre pequeña, nunca un estallido.
- **La lámpara de aceite** de barro, de pico, la de las casas de Galilea.
- **La jarra sellada:** la libreta; una jarra alta de barro con su tapa sellada, como la que guardó los códices de Nag Hammadi.
- **El códice:** hojas de papiro en una tapa de cuero con solapa y correa, como los de Nag Hammadi.
- **Las barcas y las redes:** el lago y el trabajo del salado (pescado abierto, sal, cestos, ánforas).
- **Las sombras de los arcontes:** siluetas a contraluz entre nubes o estrellas, con cabeza de animal; solo se adivinan.
- **El anillo de Salomón:** del Testamento de Salomón; se dibuja **liso**, porque la estrella del sello es medieval.
- **La figura de luz:** de espaldas, sin rasgos, blanca con borde violeta; solo en el cuadro y algunos días.

## El fondo y los muebles

- **El fondo de pantalla:** caliza con vetas y alguna grieta finísima; de noche, la misma piedra apenas vista, con un hilo de la luz de la grieta.
- **Fondo vivo:** de día, polvo dorado que flota en un rayo de sol; de noche, los círculos de las esferas que giran despacio, una sombra que cruza y, muy de vez en cuando, la grieta que late.
- **Los muebles del Diario** (cuando le toque, con la receta de `docs/diseno/MUEBLES.md`): la casa del salador: piedra caliza, madera de olivo, esteras, lino, barro cocido, sal.

## Sonido

Lira (el kinnor), crótalos y salmodia grave; un zumbido bajo para lo de los arcontes y una nota limpia y alta para lo del Pleroma. Nada de coros de iglesia ni de órgano. La música es propia y no cita ninguna obra.

## Se distingue de

- **Romano:** el Imperio visible; aquí, lo que se esconde debajo del mundo.
- **Horror cósmico:** 1926 y signos inventados; aquí, mito antiguo real, y el iniciado entiende y tiene contraseñas.
- **Egipto:** lo faraónico; aquí, lo judío, lo cristiano temprano y lo gnóstico.
- **Helénico:** los títulos no van en griego.
- **Gótico y Scriptorium:** la fe oficial; aquí, la secreta.
- **Tierras Antiguas:** fantasía inventada; aquí, mito religioso.
- **Espacio:** allí el cielo se observa; aquí es una cárcel.
