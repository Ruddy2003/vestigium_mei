# Art nouveau (`nouveau`): guía de diseño

La ficha del tema está en `docs/diseno/PLAN_TEMAS.md`, lote 10f. Esta guía baja la ficha a valores concretos para que cada parte del tema (y cada ayudante que la programe) siga la misma línea. Si algo de aquí choca con la ficha, manda la ficha; si choca con `UnicidadTemas`, se cambia aquí y se anota. Los datos, las fechas, las citas y las flores se comprueban en `docs/diseno/temas/fuentes_nouveau.md`: lo que esa tabla no sostiene no se usa.

## La idea, en una frase

París hacia 1900, el año de la Exposición Universal y de la primera línea de metro, cuando el hierro, el vidrio y la luz eléctrica aprendieron a parecer plantas. Se vive desde un taller de litografía que imprime carteles: las piedras de color, el papel crema, el olor de la tinta; y afuera, los cafés, los invernaderos, las entradas de metro de Guimard y las vitrinas de Nancy.

## Lo que sí y lo que no

- **Sí:** lo que se puede fechar entre 1895 y 1905 en París (y en Nancy, de donde vienen el vidrio de Gallé y de Daum): la Exposición de 1900, la línea 1 del metro, Guimard, Lalique, Mucha, los carteles litográficos, los cafés-conciertos, la ley Millerand, *Le Langage des fleurs* de «Charlotte de La Tour».
- **Sí, como recurso** (se dice que es recurso): la flor del día repartida por el año a partir de la lista de La Tour; la hora del teléfono llamada «heure de Paris»; cómo se dibuja todo (el tema dibuja *a la manera* del cartel de 1900, sin copiar ningún cartel).
- **No:** el Art déco (geometría, zigzag, máquina, años 20), la Belle Époque de postal turística (la torre Eiffel en todo, el cancán de cabaré, el «Moulin Rouge» de película), el Jugendstil de Viena o de Múnich como si fuera París, Tiffany (es de Nueva York), ni nada de las obras de Mucha copiadas: las figuras son propias.
- **Los nombres de obras y de artistas** se usan solo para decir de qué época es algo («como el adorno de corpiño de Lalique»), no como marca ni como dibujo copiado.
- **Las fechas finas** (de `fuentes_nouveau.md`): el metro abre el 19 de julio de 1900 y solo la línea 1; los faroles de las entradas de Guimard son de 1901; a Montmartre llega en 1902–1903; Le Chat Noir cerró en 1897; los frascos de Lalique para Coty son de 1908–1910; la luz eléctrica ya se usaba desde hacía unos veinte años: en 1900 no es novedad, es escaparate.

## Claro y oscuro: el mismo mundo

La misma París. De día, la luz difusa que entra por el vidrio del invernadero o del taller, el papel crema, el verde salvia de los tallos, el malva y el violeta del iris, el oro pálido. De noche, la misma calle con la luz eléctrica de los escaparates, las lámparas de vidrio de colores, los faroles del metro y el verde de los cafés.

| Papel | Claro (el taller de día) | Oscuro (la misma París de noche) |
|---|---|---|
| fondo | `#EFE8D6` papel crema | `#121519` la calle de noche |
| superficie | `#F7F2E6` | `#1A1E22` |
| variante | `#E2D8C0` | `#23282C` |
| borde | `#B3A586` | `#3E4548` |
| tinta | `#2A2530` la tinta de la prensa | `#ECE5D6` |
| tenue | `#5E5662` | `#ADA79A` |
| primario | `#4B6A4E` verde salvia del tallo | `#E8C46E` la luz eléctrica |
| sobre primario | `#F7F3E8` | `#2B1E00` |
| contenedor | `#D6E3CF` | `#4A3A14` |
| sobre contenedor | `#16261A` | `#FBEBC4` |
| secundario | `#6D4C82` iris violeta | `#BFA6DE` la lámpara de vidrio violeta |
| contenedor secundario | `#E6DAEE` | `#2F2540` |
| terciario | `#7A5810` oro pálido, en tinta | `#8CCBA4` el verde de los cafés |
| error | `#A1302A` | `#FF8B7E` |

Contraste sobre el fondo (WCAG): tinta 12.2 y 14.6; tenue 5.8 y 7.6; primario 5.0 y 10.9; secundario 5.7 y 8.5; terciario 5.3 y 9.8; error 5.8 y 8.1. Sobre primario 5.5 y 9.7; sobre contenedor 11.9 y 9.3.

Colores de dibujo (`TrazosNouveau`, en `ui/NouveauTrazos.kt`):
- **el hierro de Guimard** `#3D5A48` (en sombra `#24372B`), verde oscuro como el de las entradas de metro;
- **el tallo y la hoja** salvia `#8FA67E` (en sombra `#5F7A55`);
- **el iris** violeta `#7E5FA6` y su lengua amarilla `#E2B84A`; **la malva** `#B48AB0`;
- **el oro pálido** `#D9BE7A` (brillo `#F2E2B0`) de los marcos y las letras doradas;
- **el vidrio** de Nancy: de un color al otro, del ámbar `#D9A14A` al violeta `#6B4E8C` y al verde `#7FA88A`, con su brillo blanco;
- **la libélula:** alas de nácar `#E6E3EC` con nervadura de oro, cuerpo verde azul `#2F6E7A`;
- **el pavo real** `#2F6E7A` y `#5E9C6E`, con el ojo de la pluma en oro;
- **el carey** del peine `#7A4A22` con manchas `#C08A4A`;
- **la piedra litográfica** gris caliza `#C9C2B0` y **la tinta** de los carteles: el rojo ladrillo `#B5533A` y el azul de Prusia `#2C4A6B`, solo en lo que es cartel;
- **de noche:** la luz eléctrica `#F5D488` (halo `#FFE9B8`), el farol del metro `#F2B55A` y el verde de los cafés `#6FB58C`.

Paleta corta: crema, salvia, violeta y oro; de noche, el azul negro con el oro de la luz y el verde de los cafés. El rojo del cartel no es color del tema: solo aparece dentro de un cartel.

## Letra y forma

- **Títulos:** Federant (OFL), que retoma la Feder Antiqua de Otto Ludwig Nägele (1911), letra de cartel del Jugendstil, con sus curvas de pluma.
- **Texto:** Brygada 1918 (OFL), serifa de libro de los primeros años del siglo, que se lee bien en tamaño chico.
- **Rótulos y cifras:** Della Respira (OFL), que retoma la Della Robbia (1902) de la American Type Founders: mayúsculas altas y finas, como las letras de los carteles y de las entradas de metro. Le faltan la comilla y el apóstrofo curvos (’ ”) y los puntos suspensivos (…): se le copian los de Brygada (en `herramientas/fuentes.py`), como con las otras letras.
- **Las cifras de hierro forjado, con curvas:** las de Della Respira y Federant; donde un número se dibuja en grande (el salón, los mapas, las medallas), lleva un remate de tallo.
- **Signos que traen las tres:** ‹ › • · — ¿ ¡ « » ° y las letras del francés. **No** traen ← → ↑ ↓ † ‡ ※ ⁂ ⁘ ◌ ▪ ▫ ▹ ◃ ∴ ∵ ∷ ✿ ❦ ❧ ni ●○◆★✦: no se usan en textos (sí se pueden dibujar).
- **Nombres:** Federant y Della Respira reservan su nombre en la licencia; recortadas, van renombradas «Cartel Vestigium» y «Vitrina Vestigium» (principio 6). Brygada 1918 no reserva el suyo.
- **Esquinas:** `Esquinas(14, Esquina.HOJA)`: dos esquinas muy redondas y dos casi rectas, como el marco de un cartel con su tallo.

## La fecha y los números

- **La página del almanaque con la flor del día:** «samedi 17 octobre · Taminier — soyez mon appui». La flor y su significado salen de *Le Langage des fleurs*, firmado «Charlotte de La Tour» (seudónimo; se atribuye a Louise Cortambert o a Louis-Aimé Martin), del «Dictionnaire des plantes avec leurs emblèmes» de su 11.ª edición: 275 plantas, en francés y con la grafía del libro (lista, lectura y reservas en `fuentes_nouveau.md`, sección 3). **Recurso:** el libro no trae una flor para cada día; el día N del año toma la flor (N − 1) · 97 de la lista, así que el mismo día tiene siempre la misma flor y las 275 salen una vez antes de repetirse (`Nouveau.flor`).
- **La hora de París:** el día de hoy lleva además la hora, a la francesa: «· 9 h 41 du matin, heure de Paris» (de las 0 a las 5, «de la nuit»; de las 6 a las 11, «du matin»; de las 12 a las 17, «de l'après-midi»; lo demás, «du soir»). La hora de París fue la hora legal de toda Francia desde la ley del 14 de marzo de 1891 hasta la del 9 de marzo de 1911. **Recurso:** la app pone la hora del teléfono.
- **El mes:** «Almanach de juillet», y con el año, «Almanach de juillet 2026».
- **Números:** como el folio de una revista ilustrada, entre rayas cortas: «– 17 –». Los años, tal cual.
- **Títulos en francés,** que van solos según el principio 7 (el francés se parece al español). Lo demás, en español.

## La voz

Delicada, curiosa y un poco teatral, de taller y de café: la de alguien que dibuja y que mira. Tutea. Habla de lo que se ve (la luz por el vidrio, el tallo, la piedra que se entinta, la lluvia en la marquesina) y de las flores como de un idioma.
- **Ejemplos:** «La piedra está entintada», «Florece», «Se enciende la vitrina», «Pasa el metro nuevo», «Una flor por día».
- **Títulos en francés** donde suenan a cartel: «L'Atelier», «Le Salon», «Almanach», «Carnet de croquis».
- **Las frases del día:** las 21 que `fuentes_nouveau.md` (sección 11) comprobó letra por letra en el texto de la obra (Zola, Mallarmé, Verlaine, Proust, Rostand, Sarah Bernhardt y cuatro de Baudelaire, anteriores al periodo), en francés con su español al lado. Las que solo sostiene Wikipedia (Gallé, Guimard, Huysmans, Rodin, Renard) no se usan hasta comprobarlas.
- **Lo que nunca dice:** nada del Art déco ni de los años 20, nada de cabaré de película, nada de «la Belle Époque» como postal (la palabra es de después de 1914).

## Los motivos de las figuras

- **El emblema:** una libélula dentro de un marco de línea de látigo (la curva que se dobla sobre sí misma y termina en un rizo).
- **El iris**, **la amapola**, **el lirio** y **el tallo** que sube y se curva: la planta como adorno, nunca como lámina científica.
- **La línea de látigo** en los marcos, los separadores y las esquinas.
- **La entrada de metro** de hierro fundido verde, con su tallo y, desde 1901, sus faroles.
- **El invernadero** de hierro y vidrio, con la luz que se filtra.
- **El vidrio de Nancy:** el jarrón de colores que se funden, con una flor grabada.
- **La libélula**, **el pavo real**, **el abanico**, **el peine de carey**, **el frasco de perfume**.
- **El cartel litográfico** con su mujer de perfil (figura propia, nunca copia de Mucha) y su marco de flores; **la piedra litográfica** y **la prensa** del taller.
- **El café** con su mesa de mármol y su lámpara.

## El fondo y los muebles

- **El fondo de pantalla:** papel crema con una cenefa de tallos y lirios a los lados; de noche, el mismo papel bajo la luz de una lámpara de vidrio.
- **Fondo vivo:** tallos que crecen despacio por los bordes y abren una flor; de noche, lo mismo con las lámparas que se encienden una a una.
- **Los muebles del Diario** (cuando le toque): el rincón del taller: la mesa de dibujo inclinada, las piedras, los frascos de tinta, una lámpara de vidrio, un cartel a medio imprimir.

## Sonido

El vals y el piano de salón para los logros, la cajita de música, las campanillas de cristal, el roce del papel y el golpe sordo de la prensa. La música es propia y no cita ninguna obra.

## Se distingue de

- **Art déco:** geometría, máquina y años 20; aquí, curva, planta y 1900.
- **Botánico:** la lámina científica; aquí, la planta como adorno.
- **Gótico:** el vitral de catedral; aquí, el vitral de casa y la lámpara.
- **Vapor victoriano:** el hierro de fábrica; aquí, el hierro que se curva como un tallo.
- **Napoleón:** el francés de las campañas del Imperio; aquí, el de los carteles y los cafés.
- **Psicodelia:** toma sus letras y sus curvas, pero las hace vibrar; aquí la curva es tranquila.
