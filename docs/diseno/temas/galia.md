# Galia (`galia`): guía de diseño

La ficha del tema está en `docs/diseno/PLAN_TEMAS.md`, lote 10e. Esta guía baja la ficha a valores concretos para que cada parte del tema (y cada ayudante que la programe) siga la misma línea. Si algo de aquí choca con la ficha, manda la ficha; si choca con `UnicidadTemas`, se cambia aquí y se anota. Los datos, las citas y la cronología se comprueban en `docs/diseno/temas/fuentes_galia.md`: lo que esa tabla no sostiene no se usa.

## La idea, en una frase

La Galia del 52 a. C., el año del levantamiento de Vercingétorix, vivida desde un oppidum arverno y su bosque. Es **mitad y mitad**: el bosque sagrado, los druidas y la vida de todos los días en el día a día; la guerra contra César en la campaña, los trofeos y los avisos. Edad del Hierro: madera, barro y paja, hierro de la fragua, bronce y oro de los torques, lana teñida a cuadros, humo.

## Lo que sí y lo que no

- **Sí:** lo que cuentan César, Plinio, Lucano, Diodoro y Estrabón (con su sesgo: varios son enemigos, y César escribe para ganar), la arqueología (oppida, monedas, carnyx, torques) y las inscripciones galas.
- **Sí, como recurso** (se dice que es recurso): el calendario de Coligny (del siglo II d. C.) puesto en el 52 a. C.; las tablillas de cera; el repujado del gato a la manera de Gundestrup; cómo se dibuja todo.
- **No:** los mitos irlandeses y galeses (mil años después y en otras tierras), el ogham, el druidismo romántico moderno (túnicas blancas de pintura del XIX, Stonehenge, trisqueles de tatuaje, nudos celtas de los manuscritos irlandeses), ni nada de tebeo.
- **Los dioses:** casi todos los que tienen nombre en piedra son de época romana (Cernunnos, Epona, Sucellus, Sequana). La voz del tema usa los de Lucano (Teutates, Esus, Taranis) y los que César nombra a la romana; Taranis tiene además una dedicatoria galo-griega de antes de la conquista (Orgon).
- **Los druidas no van a la guerra** (César 6.14): quien la hace en el tema es el que dejó la escuela.

## Claro y oscuro: el mismo mundo

El mismo oppidum en su colina y el mismo bosque. De día, el verde de los robles, el ocre del barro de las paredes, el oro de los torques y el humo de las casas. De noche, el mismo bosque con la luna, las hogueras y, en el horizonte, las del campamento romano.

| Papel | Claro (el oppidum de día) | Oscuro (el mismo bosque de noche) |
|---|---|---|
| fondo | `#E6DFC6` barro y paja al sol | `#0F1410` el robledal de noche |
| superficie | `#F2ECDA` | `#171E17` |
| variante | `#D9D0B0` | `#212A20` |
| borde | `#A89A6E` | `#3A4636` |
| tinta | `#20251A` verde negro del roble | `#E6E1CF` |
| tenue | `#555B44` | `#A2A78F` |
| primario | `#3E5F24` verde de roble | `#EBA53F` la hoguera |
| sobre primario | `#F6F3E6` | `#2A1700` |
| contenedor | `#D3DFC0` | `#4A3311` |
| sobre contenedor | `#14200A` | `#FBE5C0` |
| secundario | `#7E590C` oro de torques | `#A9C795` el roble bajo la luna |
| contenedor secundario | `#EEDDB0` | `#22311D` |
| terciario | `#94431F` ocre rojo, bronce | `#E58A5C` las hogueras romanas, lejos |
| error | `#A3261C` | `#FF8A7A` |

Contraste sobre el fondo (WCAG): tinta 11.7 y 14.2; tenue 5.3 y 7.5; primario 5.5 y 8.9; secundario 4.6 y 10.0; terciario 5.1 y 7.2.

Colores de dibujo (`TrazosGalia`, en `ui/GaliaTrazos.kt`):
- **roble** `#4E6B2E` (en sombra `#2E4219`) y **muérdago** `#9DB560` con sus bayas `#F2EEDC`;
- **barro y paja** de las casas `#B89A62` y `#D8C48A`;
- **madera** de la empalizada `#6B4B2A`;
- **oro** de los torques y de las monedas `#C9962B` (brillo `#F0D27A`);
- **bronce** del carnyx y del caldero `#9A6A3A` (verdín `#5F8C76`);
- **hierro** de las espadas y de la fragua `#5A5C5E`;
- **lana a cuadros** de los mantos: `#8E3B2A`, `#3F5E2A`, `#C9A24A`;
- **humo** `#B9B4A6` y **niebla** `#DDE0D8`;
- **la luna** `#E9E6D6` y **la hoguera** `#F2A040` (halo `#FFD28A`);
- **Roma**, solo en lo que es de Roma (el campamento, el cerco, las insignias lejanas): rojo `#A33A2A` y el gris del hierro.

Paleta corta: roble, barro, oro y bronce, el humo; de noche, el verde negro con la luna y el fuego. Nada del rojo y oro imperial de Romano como color propio: Roma es el otro.

## Letra y forma

- **Títulos:** Piazzolla (OFL) en negrita gruesa, serifa afilada como letra grabada.
- **Texto:** Piazzolla normal (`opsz` de texto).
- **Rótulos y cifras:** Ysabeau (OFL), de trazo humanista.
- **Las dos traen el griego.** Ninguna trae la sigma en forma de luna (Ϲ, U+03F9, y ϲ, U+03F2): se le copia a cada una su propia «C» y «c» latina, que tiene la misma forma (en `herramientas/fuentes.py`).
- **Esquinas:** `Esquinas(9, Esquina.HOJA)`, la hoja del roble.

## La fecha y los números

- **«Noche N»:** César dice que los galos se tienen por descendientes de Dis Pater y que por eso cuentan por noches (*Guerra de las Galias* 6.18). La fecha empieza por la noche: «Noche 212».
- **El mes de Coligny** con su marca: «Samonios · mat». **Recurso:** el bronce de Coligny es de finales del siglo II d. C. y la correspondencia con nuestros meses se discute; aquí se usa un acomodo fijo y se dice en la guía del tema. Que mat y anmat sean «bueno / no bueno» se discute: la app escribe la marca, no la traduce.
- **Números:** los ordinales galos de La Graufesenque del 1 al 10 (cintuxos, allos, tritios, petuarios, pinpetos, suexos, sextametos, oxtumetos, nametos, decametos; reconstruidos en parte); por encima de 10, números romanos, como en Coligny.
- **Palabras galas con letras griegas** en algunos títulos, como escribían los galos (César 1.29, inscripciones galo-griegas): **en mayúsculas, sin acentos ni espíritus, con la sigma de luna (Ϲ) y ΟΥ para la u**. Así no se parece al griego clásico de Helénico. Siempre con su español al lado (principio 7).

## La voz

Directa y orgullosa, de campamento y de asamblea, con el respeto del bosque. Tutea. Habla de lo que se ve (el humo, el caballo, la lluvia, el grito que corre) y de lo que se cree sin explicarlo (el alma no muere; lo sagrado no se escribe).
- **Ejemplos:** «Corre el grito», «La noche cuenta», «Lo que no se escribe, se recuerda», «El bosque escucha», «Se enciende la hoguera».
- **Las frases del día:** citas de César, Plinio, Lucano, Diodoro y Estrabón en traducción de dominio público o propia, cada una con su pasaje. Ojo con el sesgo: las de César se citan como lo que dice César.
- **Lo que nunca dice:** nada de los mitos irlandeses, nada de «druida» como mago de cuento, nada de «bárbaro» como insulto propio (es palabra del enemigo).

## Los motivos de las figuras

- **El emblema:** el caballo de las monedas galas (como en los estáteros arvernos), en oro, con su perla y su rueda.
- **El torques** de oro y **la moneda** del caballo.
- **El carnyx:** la trompeta de guerra con cabeza de jabalí, alta sobre la gente (como los de Tintignac).
- **El roble con el muérdago y la hoz de oro** (Plinio 16.249–251).
- **El oppidum:** la colina con su muralla de madera, piedra y tierra (el *murus gallicus*), las casas de paja y el humo.
- **El caldero** de bronce, **el jabalí** de bronce, **el casco** y **la espada**.
- **El manantial sagrado** con sus ofrendas de madera (las fuentes del Sena; ojo: de época romana).
- **Roma, al fondo:** las hogueras del campamento, el doble cerco de Alesia; nunca en primer plano.

## El fondo y los muebles

- **El fondo de pantalla:** el tablado de madera y el barro de una pared, con su paja; de noche, el mismo muro a la luz de la hoguera.
- **Fondo vivo:** de día, hojas de roble que caen y niebla entre los troncos; de noche, el humo que sube y la luz que tiembla de las hogueras.
- **Los muebles del Diario** (cuando le toque): la casa grande del oppidum: el hogar en el centro, las vigas, los mantos a cuadros, las armas colgadas, el caldero.

## Sonido

El carnyx para los logros (un bramido de bronce), la lira de los bardos (Diodoro 5.31), el viento entre los robles, el golpe de la fragua y el fuego que crepita. La música es propia y no cita ninguna obra; nada de gaitas ni de arpas irlandesas.

## Se distingue de

- **Romano:** es el otro lado del mismo siglo.
- **Tierras Antiguas:** fantasía con runas inventadas; aquí, religión real y una guerra con fecha.
- **Ciudad verde y Botánico:** allí el bosque es ruina o ciencia; aquí es templo.
- **Templario:** guerra de fortaleza medieval; aquí, guerra desde el bosque en la Edad del Hierro.
- **Prehistoria:** la cueva; aquí, la Edad del Hierro.
- **Helénico:** el griego de aquí es el de las inscripciones galas, en mayúsculas sin acentos y con la sigma de luna.
