# Fuentes del tema Art nouveau

Comprobación de los datos de la ficha del tema (`docs/diseno/PLAN_TEMAS.md`, lote 10f, con lo que le toca de 10a y 10h). Cada dato lleva lo que dice la fuente, la cita exacta y la página que se abrió. Lo que no se pudo abrir va marcado **[sin verificar]**. Las correcciones que se proponen a la ficha están juntas en la sección 1.

## Cómo leerlo

| Marca | Qué significa |
|---|---|
| **Comprobado** | La página enlazada se abrió en la sesión y dice lo que aquí se cita. La cita va tal cual, en el idioma de la página |
| **Comprobado en parte** | La página sostiene una parte del dato; lo demás se dice al lado |
| **Calculado** | Lo calculó la sesión (el método va al lado) |
| **Dudoso** | Las páginas abiertas se contradicen entre sí o con la ficha |
| **[sin verificar]** | Viene de memoria o de una página que no se pudo abrir. No se usa como cita hasta comprobarlo |
| **Recurso** | No es un hecho: es una forma de mostrarlo en la app (regla de 10a) |
| **[Aproximación]** | Un dato razonable sacado de una muestra o de una fuente indirecta, no de la fuente ideal |

- **Qué se abrió.** Todo se leyó el **15 nov 2026**, casi siempre en el texto fuente de la página (el wikitexto que devuelve `?action=raw`; el contenido es el mismo que el de la página normal que se enlaza). Se pudo abrir:
  - **Wikipedia** en francés, inglés y español;
  - **Wikidata** (las fichas de cada elemento, no el buscador SPARQL);
  - **Wikisource en inglés y en español**.
- **Qué no se pudo abrir** (el proxy rechaza la conexión o el nombre no se resuelve): `fr.wikisource.org`, `fr.m.wikisource.org`, `wikisource.org`, `fr.wikiquote.org`, `en.wikiquote.org`, `commons.wikimedia.org`, `upload.wikimedia.org`, `api.wikimedia.org`, `query.wikidata.org`, `gallica.bnf.fr`, `catalogue.bnf.fr`, `data.bnf.fr`, `gutenberg.org`, `archive.org`, `books.google.com`, `biodiversitylibrary.org`, `hathitrust.org`, `legifrance.gouv.fr`, `persee.fr`, `insee.fr`, `fr.wiktionary.org`, `en.wiktionary.org`. **Para leer el texto francés de Charlotte de La Tour hay que permitir en la red de la sesión `fr.wikisource.org`, `gallica.bnf.fr` o `archive.org`**; con ninguno de los tres abiertos, la lista de flores (sección 3) sale de una obra en inglés del mismo siglo.
- **La API de MediaWiki** (`api.php`) respondió muchas veces «too many requests»; se trabajó con las páginas normales, con pausas entre una y otra.
- **Wikipedia resume a los estudiosos; no es la fuente original.** Donde la página cita un texto de época (una ley, un poema, una carta), se dice cuál y de dónde lo toma. La página inglesa *Language of flowers* lleva el aviso «AI-generated» (octubre de 2025): se usó solo para la frase que cita a Goody, y con cuidado.
- **Dominio público.** Se razona como en Galia y Arcontes: obra publicada antes de 1931 o autor muerto hace más de 70 años. No es consejo legal **[Aproximación]**.

## 1. Correcciones que se proponen a la ficha

| # | Dónde | Qué dice la ficha | Qué pasa | Propuesta |
|---|---|---|---|---|
| 1 | Fecha y números: el lenguaje de las flores | *Le Langage des fleurs*, de Charlotte de la Tour, 1819 | **Comprobado en parte.** La fecha de 1819 la dan las páginas abiertas. Pero «Charlotte de La Tour» es un **seudónimo** y **no hay acuerdo sobre quién lo usó**: Wikidata (según la BnF) y Wikipedia en inglés dicen que es Louise Cortambert, nacida Delaistre (1782–1875); la página francesa *Perce-neige* dice que fue Louis-Aimé Martin de 1819 a 1827, y Louise Cortambert desde 1844. Algunos catálogos dan además 1810 o 1800, que es un error de fecha (sección 2) | «*Le Langage des fleurs*, de "Madame Charlotte de La Tour" (seudónimo), 1819». No ponerle nombre real en la app; si se pone, «atribuido a Louise Cortambert» |
| 2 | Flor del día | La flor y su significado, del libro de La Tour | **El texto de La Tour no se pudo abrir** (Wikisource en francés, Gallica y archive.org bloqueados). La lista de la sección 3 sale de *Flora's Lexicon* (Catharine H. Waterman, Filadelfia, 1840), en inglés y de dominio público, que sí está transcrita en Wikisource en inglés. Dos significados se pudieron cotejar con La Tour a través de Wikipedia: perce-neige = consolación y jonquille = deseo; los dos coinciden con Waterman | Usar la lista de la sección 3 mientras no se pueda leer La Tour, diciendo la fuente: «según *Flora's Lexicon*, 1840». **Quitar las 21 plantas marcadas «EE. UU.», «Australia» o «trópico»**, que son añadidos americanos y no de la tradición francesa. Cuando se abra La Tour, cotejar las 199 que quedan |
| 3 | Fecha y números: la hora de París | Hora legal de 1891 a 1911 (a comprobar) | **Comprobado.** Ley del 14 de marzo de 1891 (para la Francia metropolitana **y Argelia**) hasta la ley del 9 de marzo de 1911, que define la hora legal como «l'heure du temps moyen de Paris retardée de neuf minutes vingt et une secondes» (sección 4) | Dejarlo. En 1900, la hora de París va 9 min 21 s por delante de Greenwich. Detalle que se puede usar: los relojes de los trenes iban 5 minutos atrasados respecto a la hora de París |
| 4 | Oscuro: «la luz eléctrica (la gran novedad de la Exposición)» | | **Dudoso.** En 1900 la luz eléctrica ya no era nueva: la página de la Exposición, en su apartado del Palacio de la Electricidad, dice que la electricidad estaba «contrôlée depuis une vingtaine d'années par l'humain en 1900». Lo nuevo era la escala: el palacio, con 5000 lámparas de colores, era «le clou du spectacle» (sección 6) | «La luz eléctrica, la gran exhibición de la Exposición» |
| 5 | Campaña: la primera línea del metro | «Abierta durante la Exposición de 1900 (a comprobar la fecha y la lista)» | **Comprobado.** Se abrió al público el **19 de julio de 1900 a las 13 h**, entre Porte Maillot y Porte de Vincennes, con la Exposición abierta (del 14 de abril al 12 de noviembre). Tenía **18 estaciones**, pero solo se abrieron 8 (o 10, ver la sección 5) el primer día; las demás, entre el 6 de agosto y el 1 de septiembre. Ocho tenían otro nombre en 1900: Obligado, Étoile, Alma, Marbeuf, Champs-Élysées, Palais-Royal, Louvre y Reuilly | Usar la lista de la sección 5, con los nombres de 1900. Cada estación puede «abrirse» en la campaña en su fecha real |
| 6 | Campaña | (no dice nada de ramales) | En 1900 abrieron además **dos ramales desde Étoile**, que entonces se contaban como parte de la línea 1: Étoile–Trocadéro (2 de octubre, con Kléber, Boissière y Trocadéro) y Étoile–Porte Dauphine (13 de diciembre, con Victor Hugo y Porte Dauphine) | Si se quiere, dos etapas extra al final de la campaña |
| 7 | Cuadro: «la entrada de metro bajo la lluvia, con sus faroles» | | **Ojo con el año.** El 19 de julio de 1900 solo estaban puestas las barandillas. Los primeros edículos B se pusieron en agosto y septiembre; los candelabros con sus faroles y los letreros «Métropolitain» **esperaron a 1901**. Además, se discute el color de los faroles: rojo anaranjado según unos; blancos al principio, y con bombillas rojas desde agosto de 1907, según otro autor (sección 5) | Ambientar el cuadro en 1901 o después, o anotarlo como **recurso**. Los faroles, rojo anaranjado |
| 8 | Otra vida, agenda: «19–19.5 camino en el metro nuevo» | | Solo vale **desde el 19 de julio de 1900** y solo si el taller y la casa quedan cerca de la línea 1. Las imprentas que se pudieron comprobar no lo estaban: Champenois, en el bulevar Saint-Michel (la orilla izquierda, sin metro en 1900); Chaix, en Saint-Ouen, fuera de París; Lemercier, en la calle de Seine desde 1831 (dónde estaba en 1900, **[sin verificar]**). Y **no había metro a Montmartre** hasta 1902–1903 (sección 5) | Que antes del 19 de julio el camino sea «a pie» y después, «en el metro nuevo». Poner el taller cerca de la línea 1 es **recurso**. A Montmartre, a pie |
| 9 | Otra vida: «la jornada de 11 horas encaja con la ley de 1900» | | **Comprobado en parte.** La ley del 30 de marzo de 1900 fija un máximo de **11 horas** hasta el 1 de abril de 1902, **10 h 30** hasta el 1 de abril de 1904 y **10 horas** desde entonces, en los talleres con mujeres o menores de 18 años y para los hombres que trabajan en el mismo local. Pero **la agenda de la ficha suma 10 h 30** (de 7.5 a 12 y de 13 a 19), no 11 | O se dice «10 h 30, por debajo del máximo de 11 h de la ley de 1900», o se pasa a 11 horas: «6.5–7 camino, 7–12 trabajo, 12–13 comer, 13–19 trabajo». **El texto exacto de la ley [sin verificar]** (Légifrance y Gallica bloqueados) |
| 10 | Otra vida: «un café-concierto de Montmartre» | | Le Chat Noir **cerró en 1897**, al morir Rodolphe Salis (19 de marzo de 1897). El Trianon-Concert **se quemó en 1900**. Abiertos en 1900: La Cigale (café-concierto, 1887), el Divan japonais (café-concierto, hasta 1901), el Moulin-Rouge (1889, que es un baile y music-hall, no un café-concierto), el Élysée-Montmartre (un baile, también con incendio en 1900) y el cabaret des Quat'z'Arts (1893–1914) (sección 9) | «La Cigale» o «el Divan japonais». No Le Chat Noir |
| 11 | Trofeos: «un broche de libélula» | | La pieza famosa de Lalique no es exactamente un broche: es el **adorno de corsaje «Femme-libellule»** (hacia 1897–1898), de Gulbenkian, que la página francesa llama «Pectoral à la libellule» y que dice muy admirado en la Exposición de 1900 (sección 7) | «Un adorno de libélula» o «la mujer libélula de Lalique» |
| 12 | Trofeos: «una lámpara de vitral» | (implícito: Tiffany) | Las lámparas Tiffany son de **Nueva York** (Corona, Queens), hechas desde 1893, y muchas de las mejores las diseñó Clara Driscoll. En París las vendía Siegfried Bing. Las lámparas de vidrio de París y Nancy eran de Gallé y de Daum (sección 7) | Si la lámpara es de Tiffany, decir que es americana; si se quiere algo parisino, «una lámpara de Daum» |
| 13 | Trofeos: «un frasco de perfume» | | Los frascos de Lalique para Coty son **de 1908 a 1910**, después de 1900 (sección 7) | Un frasco cualquiera de 1900; no uno de Lalique |
| 14 | Mucha: «*Gismonda* 1894» | | **Comprobado en parte.** El cartel está fechado en 1894, pero **salió a las calles el 1 de enero de 1895**. Lo imprimió **Lemercier**; después, Bernhardt pasó a la imprenta de Champenois (sección 7) | «*Gismonda* (1894; en las calles el 1 de enero de 1895)» |
| 15 | La línea de látigo: el tapiz de Obrist | | **Dudoso en la fecha.** Las páginas dan 1892, 1894 o 1895 para el bordado *Cyclamen*. El nombre de «coup de fouet» se lo dieron los críticos; la revista *Pan* lo comparó con «the crack of a whip» (sección 7). Además, una página dice que Obrist nació en 1862 y en su resumen pone 1863 | «El bordado *Cyclamen* de Hermann Obrist, de mediados de los años 1890» |
| 16 | Gallé y «la Escuela de Nancy» | | La Escuela de Nancy **se fundó formalmente el 13 de febrero de 1901**; existía de modo informal desde 1894 (sección 7) | En 1900, «los vidrieros de Nancy»; desde 1901, «la Escuela de Nancy» |
| 17 | «Cifras de hierro forjado» | | Las entradas de Guimard no son de hierro forjado sino de **fundición moldeada** (de la fundición del Val d'Osne), con hierro y lava esmaltada (sección 5) | Las cifras son **recurso** y pueden quedarse; pero si la app las relaciona con Guimard, «de fundición» |
| 18 | Invernadero (fondo del gato) | | Hay uno real y de la época: los invernaderos de Auteuil, de Jean-Camille Formigé (obras de 1895 a 1898), de fundición pintada (hoy de color turquesa) (sección 6) | Usarlo como modelo, si se quiere |
| 19 | Nombre de la aprendiz | (por decidir, 10h) | No se pudo abrir el fichero de nombres del INSEE (empieza en 1900 y estaba bloqueado). La sección 10 da una muestra | Uno de: Marguerite, Jeanne, Louise, Suzanne, Germaine, Marie **[Aproximación]** |
| 20 | Frase de Zola | (si se usa «la vérité est en marche») | No es de *J'accuse…!* (13 de enero de 1898): es del artículo «M. Scheurer-Kestner», en *Le Figaro*, el 25 de noviembre de 1897 (sección 11) | Citarla así |

## 2. *Le Langage des fleurs* y su autora

**Lo comprobado:**

| Dato | Qué dice la fuente (cita) | Página | Veredicto |
|---|---|---|---|
| El seudónimo y la fecha | «the first dictionary of floriography appears in 1819 when Louise Cortambert, writing under pen name Madame Charlotte de la Tour, wrote *Le langage des Fleurs*» (cita a Goody, *The Culture of Flowers*, p. 2) | [Language of flowers](https://en.wikipedia.org/wiki/Language_of_flowers) | Comprobado (la página lleva aviso de texto generado por IA) |
| Quién era, según la BnF | Wikidata, con la BnF (notice 113377858) como fuente: **Louise-Henriette Delaistre**, alias «Louise Cortambert», «Charlotte de La Tour», «Mme Charlotte de La Tour», «C. de Latour»; nacida en **Toulouse el 12 de mayo de 1782**, muerta en **París el 2 de diciembre de 1875**; seudónimo «Charlotte de la Tour»; madre de los geógrafos Eugène y Louis Cortambert | [Q131698475](https://www.wikidata.org/wiki/Q131698475) | Comprobado. Hay otro elemento duplicado, [Q113191678](https://www.wikidata.org/wiki/Q113191678), descrito como «auteur van Le Langage des Fleurs (1819)» |
| Dónde lo escribió | «25 : Louise Cortambert y habita et y rédigea, en 1819, le *Langage des fleurs*, sous le pseudonyme de Charlotte de Latour» | [Rue Nicolo](https://fr.wikipedia.org/wiki/Rue_Nicolo) | Comprobado (fuente de la página: data.bnf.fr) |
| La otra atribución | «Dans *Le langage des fleurs*, publié sous le pseudonyme de Charlotte de La Tour, de 1819 à 1827 par Louis-Aimé Martin, puis par Louise Cortambert à partir de 1844 dans un *Nouveau langage des fleurs*, le perce-neige symbolise la consolation» | [Perce-neige](https://fr.wikipedia.org/wiki/Perce-neige) | Comprobado: **la página dice esto**, pero cita solo un enlace a Google Books. Louis-Aimé Martin (Lyon, 1782 – París, 1847) era escritor y editor de Bernardin de Saint-Pierre ([Louis-Aimé Martin](https://fr.wikipedia.org/wiki/Louis-Aimé_Martin)); su página **no** menciona el libro de las flores |
| Fechas raras | «celui de Charlotte de Latour, *Le Langage des Fleurs* (1810) […] Audot : Paris, 1810, voire 1800 selon Cornell University» | [Illustration botanique](https://fr.wikipedia.org/wiki/Illustration_botanique) | **Dudoso**: va contra todo lo demás. La galería de *Langage des fleurs* también pone «1800» al ejemplar de archive.org (`langagedesfleur00lato`). Parece una fecha de catálogo mal puesta |
| Ilustraciones y fama | «avec des illustrations d'après Pancrace Bessa. C'est selon Goody, « le premier jalon de la tradition », et il remporta un énorme succès européen» | [Illustration botanique](https://fr.wikipedia.org/wiki/Illustration_botanique) | Comprobado |
| Ediciones posteriores | «Charlotte de La Tour, *Le Langage des fleurs*, 7e éd., Paris : Garnier Frères, 1858» (Google Books); otra de **1856** («Ch. de La Tour, 1856», archive.org `langagedesfleur00latoc`) | [Langage des fleurs](https://fr.wikipedia.org/wiki/Langage_des_fleurs) | Comprobado (solo como cita bibliográfica; los ejemplares no se abrieron) |
| Novena edición en Gallica | «Paris, Garnier, 1861, 9e éd., année première édition 1819, 212 pages», con enlace a Gallica `bpt6k937429t` | [Veronica Veronese](https://fr.wikipedia.org/wiki/Veronica_Veronese) | Comprobado como cita. Ojo: esa página enlaza a «Louis Cortambert», el hijo (nacido en 1809), por error |
| Versión inglesa | Robert Tyas, *The Sentiment of Flowers; or, Language of Flora* (1836), «billed as an English version of Charlotte de la Tour's book» | [Language of flowers](https://en.wikipedia.org/wiki/Language_of_flowers) | Comprobado. Frederic Shoberl publicó también *The Language of Flowers* (Wikisource lo fecha en 1839); que sea traducción de La Tour **[sin verificar]** ([Author:Frederic Shoberl](https://en.wikisource.org/wiki/Author:Frederic_Shoberl)) |
| La jonquille | «Le célèbre ouvrage de "Mme de La Tour", maintes fois réédité en français et en anglais, indique clairement dès 1819 que la jonquille est synonyme de désir» | [Veronica Veronese](https://fr.wikipedia.org/wiki/Veronica_Veronese) | Comprobado |
| Mucha | El cartel *Langage des Fleurs* de Mucha es de 1900 | [Langage des fleurs](https://fr.wikipedia.org/wiki/Langage_des_fleurs) | Comprobado (pie de imagen). Une el tema y el libro |

**¿Es de dominio público?** Sí, se mire como se mire: la primera edición es de 1819, y los dos candidatos a autor murieron en 1847 y 1875. **¿Dónde está?** Hay ejemplares digitalizados citados en Wikipedia: Gallica (`bpt6k937429t`, 9.ª edición, 1861), archive.org (`langagedesfleur00lato` y `langagedesfleur00latoc`, de 1856) y Google Books (7.ª edición, 1858). **Si está transcrito en Wikisource en francés no se pudo saber**, porque no abría **[sin verificar]**.

**Otros libros franceses del siglo XIX de dominio público** que cita la galería de [Langage des fleurs](https://fr.wikipedia.org/wiki/Langage_des_fleurs) (ninguno se abrió): Delachénaye, *Abécédaire de flore* (1811); Chambet, *Emblème des fleurs* (1816); Aimé Martin, *Le Langage des fleurs* (1832); Leneveux, *Les Fleurs emblématiques* (1832); Zaccone, *Nouveau langage des fleurs* (1853 y 1858); Emma Faucon, *Le langage des fleurs* (1860); Delord, Karr y Grandville, *Les Fleurs animées* (1847).

## 3. La lista de flores (para la «flor del día»)

### De dónde sale

- **La obra.** Catharine H. Waterman, *Flora's Lexicon: An Interpretation of the Language and Sentiment of Flowers*, Filadelfia, Herman Hooker, **1840** (registrada en 1839). Ficha del libro en Wikisource: [Index:Floras Lexicon-1840.djvu](https://en.wikisource.org/wiki/Index:Floras_Lexicon-1840.djvu); obra: [Flora's Lexicon](https://en.wikisource.org/wiki/Flora%27s_Lexicon). Wikisource la marca «PD/US» y las páginas, como corregidas (nivel 3).
- **La parte transcrita** es el «Index of Flowers», en las páginas **245 a 248** del libro: [245](https://en.wikisource.org/wiki/Page:Floras_Lexicon-1840.djvu/272), [246](https://en.wikisource.org/wiki/Page:Floras_Lexicon-1840.djvu/273), [247](https://en.wikisource.org/wiki/Page:Floras_Lexicon-1840.djvu/274) y [248](https://en.wikisource.org/wiki/Page:Floras_Lexicon-1840.djvu/275). Las páginas de cada flor no aparecieron transcritas (se probaron dos páginas y no existen): lo que se cita es **la línea del índice**, y la columna «Pág.» es la página del libro donde está la entrada.
- **Por qué esta y no La Tour.** Porque La Tour no se pudo abrir (sección 2). Waterman no dice de dónde saca los significados; su prólogo habla de «those meanings most generally attached to flowers» ([pág. 3](https://en.wikisource.org/wiki/Page:Floras_Lexicon-1840.djvu/16)). Muchos coinciden con la tradición francesa (perce-neige = consolación, jonquille = deseo, violeta = modestia, giroflée = fidelidad en la desgracia), pero otros no: Wikipedia da para el jazmín «l'amour voluptueux» (según Anne Dumas, 2000; [Jasmin](https://fr.wikipedia.org/wiki/Jasmin)), y Waterman, «Separation». **Que todos sigan a La Tour [sin verificar].**
- **Los añadidos americanos.** Waterman mete plantas de su país con significados patrióticos (el olmo americano, «Patriotism»; el encino de Virginia, «Liberty»; el roble blanco, «Independence»). Esas 21 van marcadas en la nota y conviene quitarlas. **Quedan 199 para la app.**
- **Erratas de la transcripción.** «Imortality» (n.º 6), «Hydranger» (95), «Misletoe» (131), «Perscaria» (145), «Persimon» (146): están así en Wikisource y aquí se dejan tal cual. Dos números de página parecen mal copiados (n.º 81 y 101, ver la nota). «Convolvulus» sale dos veces con dos significados (51 y 52), sin decir qué especie es cada una.

### Lo que se cotejó con la tradición francesa (a través de Wikipedia)

| Flor | Waterman | Lo que dice Wikipedia en francés | Página | Veredicto |
|---|---|---|---|---|
| Perce-neige | Snowdrop: «Consolation» | «le perce-neige symbolise la consolation» (en La Tour) | [Perce-neige](https://fr.wikipedia.org/wiki/Perce-neige) | Coincide |
| Jonquille | Jonquil: «Desire» | «la jonquille est synonyme de désir» (en La Tour, 1819) | [Veronica Veronese](https://fr.wikipedia.org/wiki/Veronica_Veronese) | Coincide |
| Violette | Violet: «Modesty» | «la violette représente l'innocence, la modestie et la pudeur» (sin decir el libro) | [Viola (plante)](https://fr.wikipedia.org/wiki/Viola_(plante)) | Coincide |
| Aloès | Aloe: «Grief» | «l'aloès symbolise l'amertume ou la douleur» (Dumas, 2000) | [Aloès](https://fr.wikipedia.org/wiki/Aloès) | Coincide más o menos |
| Chèvrefeuille | Honeysuckle: «Bonds of Love» | «le chèvrefeuille symbolise l'amitié et l'amour» (Dumas, 2000) | [Chèvrefeuille](https://fr.wikipedia.org/wiki/Chèvrefeuille) | Coincide más o menos |
| Jasmin | Jasmine: «Separation» | «le jasmin symbolise l'amour voluptueux» (Dumas, 2000) | [Jasmin](https://fr.wikipedia.org/wiki/Jasmin) | **No coincide** |

### La lista (220 entradas; 199 usables)

- La columna **«Nombre francés»** es una propuesta de la sesión para los títulos de la app (que van en francés, principio 7): **no está cotejada con La Tour**, que puede usar otro nombre.
- Las columnas en español son traducción de la sesión.
- Para la app conviene la fórmula: «*Le lilas* — primeras emociones del amor (*Flora's Lexicon*, 1840, p. 122)».

| # | Flor (inglés, tal cual) | Sentimiento (inglés, tal cual) | Pág. | Flor (español) | Significado (español) | Nombre francés (propuesta, sin cotejar) | Nota |
|---|---|---|---|---|---|---|---|
| 1 | Acacia | Platonic Love | 13 | Acacia | Amor platónico | acacia |  |
| 2 | Acacia Rose | Elegance | 14 | Acacia rosa (Robinia hispida) | Elegancia | acacia rose |  |
| 3 | Adonis | Painful Recollections | 15 | Adonis (flor de Adonis) | Recuerdos dolorosos | adonis |  |
| 4 | Almond | Indiscretion | 16 | Almendro | Indiscreción | amandier |  |
| 5 | Aloe | Grief | 17 | Áloe | Pena | aloès |  |
| 6 | Amaranth | Imortality | 18 | Amaranto | Inmortalidad | amarante | «Imortality» [sic] en la transcripción |
| 7 | American Cowslip | You are my Divinity | 19 | Prímula americana (Dodecatheon) | Eres mi divinidad | gyroselle | EE. UU. |
| 8 | Amaryllis | Haughtiness, Pride | 20 | Amarilis | Altivez, orgullo | amaryllis |  |
| 9 | American Elm | Patriotism | 21 | Olmo americano | Patriotismo | orme d'Amérique | EE. UU. |
| 10 | American Linden | Matrimony | 22 | Tilo americano | Matrimonio | tilleul d'Amérique | EE. UU. |
| 11 | American Arbor-vitæ | Immortality | 23 | Tuya americana | Inmortalidad | thuya d'Occident | EE. UU. |
| 12 | Anemone | Forsaken | 24 | Anémona | Abandono («abandonado») | anémone |  |
| 13 | Angelica | Inspiration | 25 | Angélica | Inspiración | angélique |  |
| 14 | Apple Blossom | Preference | 26 | Flor del manzano | Preferencia | fleur de pommier |  |
| 15 | Arbor-vitæ | Live for me | 27 | Tuya | Vive para mí | thuya |  |
| 16 | Ash | Grandeur | 28 | Fresno | Grandeza | frêne |  |
| 17 | Aspen Tree | Excessive Sensibility | 29 | Álamo temblón | Sensibilidad excesiva | tremble |  |
| 18 | Asphodel | My regrets shall follow you to the grave | 30 | Asfódelo | Mis lamentos te seguirán hasta la tumba | asphodèle |  |
| 19 | Auricula | Painting | 31 | Aurícula (oreja de oso) | La pintura | auricule |  |
| 20 | Azalea | Romance | 32 | Azalea | Lo novelesco | azalée | «Romance»: en el siglo XIX, lo de novela |
| 21 | Balm | Sympathy | 33 | Melisa (toronjil) | Simpatía | mélisse |  |
| 22 | Balsam | Impatience | 34 | Balsamina | Impaciencia | balsamine |  |
| 23 | Barberry | Sharpness. Sourness of Temper | 35 | Agracejo | Aspereza; mal genio | épine-vinette |  |
| 24 | Basil | Hatred | 36 | Albahaca | Odio | basilic |  |
| 25 | Bay-berry | Instruction | 37 | Arrayán de cera (Myrica) | Instrucción | cirier | EE. UU. |
| 26 | Bay | Reward of Merit | 38 | Laurel común | Recompensa al mérito | laurier-sauce |  |
| 27 | Bee Orchis | Error | 39 | Orquídea abeja (Ophrys) | Error | ophrys abeille |  |
| 28 | Beech | Wedded Love | 40 | Haya | Amor conyugal | hêtre |  |
| 29 | Bell Flower | Constancy | 41 | Campánula | Constancia | campanule |  |
| 30 | Birch | Gracefulness | 42 | Abedul | Gracia | bouleau |  |
| 31 | Bird Cherry | Hope | 43 | Cerezo de racimos (Prunus padus) | Esperanza | merisier à grappes |  |
| 32 | Bird’s Foot Trefoil | Revenge | 44 | Cuernecillo (Lotus corniculatus) | Venganza | lotier corniculé |  |
| 33 | Blue Bottle | Delicacy | 45 | Aciano | Delicadeza | bleuet |  |
| 34 | Bonus Henricus | Goodness | 46 | Buen Enrique | Bondad | bon-henri |  |
| 35 | Box | Stoicism. Constancy | 47 | Boj | Estoicismo; constancia | buis |  |
| 36 | Broom | Neatness | 48 | Retama | Pulcritud | genêt |  |
| 37 | Bryony | Prosperity | 49 | Brionia (nueza) | Prosperidad | bryone |  |
| 38 | Butter-cup | Ingratitude | 50 | Botón de oro | Ingratitud | bouton-d'or |  |
| 39 | Camellia Japonica | My destiny is in your hands | 51 | Camelia | Mi destino está en tus manos | camélia |  |
| 40 | Canterbury Bell | Gratitude | 52 | Farolillo (Campanula medium) | Gratitud | campanule carillon |  |
| 41 | Cardinal Flower | Distinction | 53 | Lobelia cardenal | Distinción | lobélie cardinale | EE. UU. |
| 42 | Carnation | Disdain | 54 | Clavel | Desdén | œillet |  |
| 43 | Cherry Tree | Good Education | 55 | Cerezo | Buena educación | cerisier |  |
| 44 | China or Indian Pink | Aversion | 56 | Clavel de China | Aversión | œillet de Chine |  |
| 45 | China Aster | Variety of Charms | 57 | Reina Margarita (Callistephus) | Variedad de encantos | reine-marguerite |  |
| 46 | Chrysanthemum | Cheerfulness | 58 | Crisantemo | Alegría | chrysanthème |  |
| 47 | Clematis | Artifice | 59 | Clemátide | Artificio | clématite |  |
| 48 | Clematis, English | Traveller’s joy | 60 | Clemátide inglesa (hierba de los pordioseros) | Alegría del viajero | clématite des haies |  |
| 49 | Coltsfoot | Maternal Care | 61 | Fárfara | Cuidado maternal | tussilage |  |
| 50 | Columbine | Folly | 62 | Aguileña | Locura | ancolie |  |
| 51 | Convolvulus | Uncertainty | 63 | Convólvulo | Incertidumbre | liseron | Repetido en el índice con otro significado |
| 52 | Convolvulus | Humility | 64 | Convólvulo | Humildad | liseron | Repetido en el índice con otro significado |
| 53 | Convolvulus, Field | Captivation | 65 | Correhuela | Seducción («cautivación») | liseron des champs |  |
| 54 | Cranberry | Hardiness | 66 | Arándano rojo | Temple (resistencia) | canneberge |  |
| 55 | Crocus | Cheerfulness | 67 | Azafrán (crocus) | Alegría | crocus |  |
| 56 | Cross of Jerusalem | Devotion | 68 | Cruz de Jerusalén (Lychnis chalcedonica) | Devoción | croix-de-Jérusalem |  |
| 57 | Crown Imperial | Majesty | 69 | Corona imperial | Majestad | couronne impériale |  |
| 58 | Cyclamen | Diffidence | 70 | Ciclamen | Timidez (desconfianza de sí) | cyclamen |  |
| 59 | Cypress | Mourning | 71 | Ciprés | Luto | cyprès |  |
| 60 | Daffodil | Chivalry | 72 | Narciso trompón | Caballerosidad | narcisse jaune |  |
| 61 | Dahlia | For ever thine | 73 | Dalia | Tuyo para siempre | dahlia |  |
| 62 | Daisy | Innocence | 74 | Margarita | Inocencia | pâquerette |  |
| 63 | Dandelion | Oracle | 75 | Diente de león | Oráculo | pissenlit |  |
| 64 | Dogwood | Love undiminished by Adversity | 76 | Cornejo florido | Amor que la adversidad no disminuye | cornouiller | EE. UU. |
| 65 | Eglantine | Poetry | 77 | Eglantina (escaramujo) | Poesía | églantine |  |
| 66 | Elm | Dignity | 78 | Olmo | Dignidad | orme |  |
| 67 | Enchanter’s Nightshade | Fascination | 79 | Hierba de los hechizos (Circaea) | Fascinación | circée |  |
| 68 | Evening Primrose | Inconstancy | 80 | Onagra | Inconstancia | onagre |  |
| 69 | Evergreen Thorn | Solace in Adversity | 81 | Espino de fuego (Pyracantha) | Consuelo en la adversidad | buisson ardent |  |
| 70 | Fennel | Strength | 82 | Hinojo | Fuerza | fenouil |  |
| 71 | Fern | Sincerity | 83 | Helecho | Sinceridad | fougère |  |
| 72 | Flax | I feel your kindness | 84 | Lino | Siento tu bondad | lin |  |
| 73 | Fox-glove | Stateliness | 85 | Dedalera | Porte majestuoso | digitale |  |
| 74 | Fuchsia | Confiding Love | 86 | Fucsia | Amor confiado | fuchsia |  |
| 75 | Geranium | Gentility | 87 | Geranio | Buena crianza | géranium |  |
| 76 | Gilliflower | Lasting Beauty | 88 | Alhelí | Belleza duradera | giroflée |  |
| 77 | Glory Flower | Glorious Beauty | 89 | Flor de gloria (Clianthus) | Belleza gloriosa | clianthus | Australasia |
| 78 | Gorse | Cheerfulness in Adversity | 90 | Tojo | Alegría en la adversidad | ajonc |  |
| 79 | Grass | Utility | 91 | Hierba | Utilidad | herbe |  |
| 80 | Harebell | Submission | 92 | Campanilla (Campanula rotundifolia) | Sumisión | campanule à feuilles rondes |  |
| 81 | Hawthorn | Hope | 83 | Espino albar | Esperanza | aubépine | Página «83» [sic]: por el orden, sería la 93 |
| 82 | Hazel | Reconciliation | 94 | Avellano | Reconciliación | noisetier |  |
| 83 | Heart’s Ease | Think of me | 95 | Pensamiento | Piensa en mí | pensée |  |
| 84 | Heath | Solitude | 96 | Brezo | Soledad | bruyère |  |
| 85 | Heliotrope | Intoxicated with pleasure | 97 | Heliotropo | Ebrio de placer | héliotrope |  |
| 86 | Hickory | Glory | 98 | Nogal americano | Gloria | caryer | EE. UU. |
| 87 | Holly | Foresight | 99 | Acebo | Previsión | houx |  |
| 88 | Hollyhock | Fruitfulness | 100 | Malvarrosa | Fecundidad | rose trémière |  |
| 89 | Honesty, or Satin Flower | Honesty | 101 | Hierba de la plata (Lunaria) | Honradez | monnaie-du-pape |  |
| 90 | Honeysuckle | Bonds of Love | 102 | Madreselva | Lazos de amor | chèvrefeuille |  |
| 91 | Hop | Injustice | 103 | Lúpulo | Injusticia | houblon |  |
| 92 | Horse Chestnut | Luxury | 104 | Castaño de Indias | Lujo | marronnier |  |
| 93 | Houseleek | Vivacity | 105 | Siempreviva mayor | Vivacidad | joubarbe |  |
| 94 | Hyacinth | Play | 106 | Jacinto | El juego | jacinthe |  |
| 95 | Hydranger | Boaster | 107 | Hortensia | Fanfarrón | hortensia | «Hydranger» [sic] |
| 96 | Ice Plant | Your looks freeze me | 108 | Escarchada (Mesembryanthemum) | Tu mirada me hiela | ficoïde glaciale |  |
| 97 | Iceland Moss | Health | 109 | Liquen de Islandia | Salud | lichen d'Islande |  |
| 98 | Indian Cress | Resignation | 110 | Capuchina | Resignación | capucine |  |
| 99 | Iris | Message | 111 | Lirio (iris) | Mensaje | iris |  |
| 100 | Ivy | Friendship | 112 | Hiedra | Amistad | lierre |  |
| 101 | Jasmine, White | Amiability | 117 | Jazmín blanco | Amabilidad | jasmin blanc | Página «117» [sic]: por el orden, sería la 113 |
| 102 | Jasmine | Separation | 114 | Jazmín | Separación | jasmin |  |
| 103 | Jonquil | Desire | 115 | Junquillo | Deseo | jonquille |  |
| 104 | Juniper | Asylum. Protection | 116 | Enebro | Asilo; protección | genévrier |  |
| 105 | Kennedia | Mental Beauty | 117 | Kennedia | Belleza del espíritu | kennedia | Australia |
| 106 | Larkspur | Ardent Attachment | 118 | Espuela de caballero | Apego ardiente | pied-d'alouette |  |
| 107 | Laurel | Glory | 119 | Laurel | Gloria | laurier |  |
| 108 | Laurel, Mountain | Ambition | 120 | Laurel de montaña (Kalmia) | Ambición | kalmia | EE. UU. |
| 109 | Laurustinus | I die if neglected | 121 | Durillo (Viburnum tinus) | Muero si me descuidas | laurier-tin |  |
| 110 | Lilac | First emotions of Love | 122 | Lila | Primeras emociones del amor | lilas |  |
| 111 | Lily, Superb | Splendour | 123 | Lirio soberbio | Esplendor | lis superbe |  |
| 112 | Lily, Yellow | Coquetry | 124 | Lirio amarillo | Coquetería | lis jaune |  |
| 113 | Lily of the Valley | Return of Happiness | 125 | Lirio de los valles (muguete) | Regreso de la felicidad | muguet |  |
| 114 | Lily | Purity and Modesty | 126 | Azucena | Pureza y modestia | lis |  |
| 115 | Linden Tree | Conjugal Love | 127 | Tilo | Amor conyugal | tilleul |  |
| 116 | Live Oak | Liberty | 128 | Encino de Virginia | Libertad | chêne de Virginie | EE. UU. |
| 117 | Lobelia | Arrogance | 129 | Lobelia | Arrogancia | lobélie |  |
| 118 | Locust | Vicissitude | 130 | Falsa acacia | Vicisitud | robinier |  |
| 119 | London Pride | Frivolity | 131 | Orgullo de Londres (Saxifraga) | Frivolidad | saxifrage ombreuse |  |
| 120 | Lotos | Estranged Love | 132 | Loto | Amor alejado | lotus |  |
| 121 | Lucern | Life | 133 | Alfalfa | Vida | luzerne |  |
| 122 | Madwort, Rock | Tranquility | 134 | Alyssum de roca (canastillo) | Tranquilidad | corbeille d'or |  |
| 123 | Magnolia | High Souled | 135 | Magnolia | Alma elevada | magnolia |  |
| 124 | Maize | Plenty | 136 | Maíz | Abundancia | maïs |  |
| 125 | Maple | Reserve. Retirement | 137 | Arce | Reserva; retiro | érable |  |
| 126 | Marvel of Peru | Timidity | 138 | Dondiego de noche | Timidez | belle-de-nuit |  |
| 127 | Marygold | Inquietude | 139 | Caléndula | Inquietud | souci | «Marygold» |
| 128 | Meadow Sweet | Uselessness | 140 | Reina de los prados (ulmaria) | Inutilidad | reine-des-prés |  |
| 129 | Mezereon | Desire to Please | 141 | Mecereón (Daphne mezereum) | Deseo de agradar | bois-gentil |  |
| 130 | Mignonette | Your qualities surpass your charms | 142 | Reseda | Tus cualidades superan tus encantos | réséda |  |
| 131 | Misletoe | Parasite | 143 | Muérdago | Parásito | gui | «Misletoe» [sic] |
| 132 | Motherwort | Concealed Love | 144 | Agripalma | Amor oculto | agripaume |  |
| 133 | Mountain Ash | Talisman | 145 | Serbal de cazadores | Talismán | sorbier des oiseleurs |  |
| 134 | Mouse Ear | Forget me not | 146 | Nomeolvides («oreja de ratón») | No me olvides | myosotis |  |
| 135 | Myrtle | Love | 147 | Mirto | Amor | myrte |  |
| 136 | Narcissus, False | Delusive Hope | 148 | Narciso falso | Esperanza engañosa | faux narcisse |  |
| 137 | Narcissus | Egotism | 149 | Narciso | Egolatría | narcisse |  |
| 138 | Night-blooming Cereus | Transient Beauty | 150 | Reina de la noche (cactus) | Belleza pasajera | reine de la nuit | América |
| 139 | Oak | Hospitality | 151 | Roble | Hospitalidad | chêne |  |
| 140 | Olive | Peace | 152 | Olivo | Paz | olivier |  |
| 141 | Orange-Tree | Generosity | 153 | Naranjo | Generosidad | oranger |  |
| 142 | Palm | Victory | 154 | Palmera | Victoria | palmier |  |
| 143 | Passion Flower | Religious Superstition | 155 | Pasionaria | Superstición religiosa | passiflore |  |
| 144 | Periwinkle, Blue | Sweet Remembrances | 156 | Vinca azul | Dulces recuerdos | pervenche |  |
| 145 | Perscaria | Restoration | 157 | Persicaria | Restablecimiento | persicaire | «Perscaria» [sic] |
| 146 | Persimon | Bury me amid Nature’s Beauties | 158 | Caqui de Virginia | Entiérrame entre las bellezas de la naturaleza | plaqueminier de Virginie | EE. UU.; «Persimon» [sic] |
| 147 | Pimpernel | Assignation | 159 | Murajes (anagálide) | Cita (de amor) | mouron rouge |  |
| 148 | Pine Apple | You are perfect | 160 | Piña (ananás) | Eres perfecto | ananas | Trópico |
| 149 | Pink | Lively and pure affection | 161 | Clavellina | Afecto vivo y puro | mignardise |  |
| 150 | Plane Tree | Genius | 162 | Plátano de sombra | Genio | platane |  |
| 151 | Plum Tree | Keep your promises | 163 | Ciruelo | Cumple tus promesas | prunier |  |
| 152 | Poplar, White | Time | 164 | Álamo blanco | El tiempo | peuplier blanc |  |
| 153 | Poplar, Black | Courage | 165 | Álamo negro | Valor | peuplier noir |  |
| 154 | Poppy | Consolation of sleep | 166 | Adormidera | Consuelo del sueño | pavot |  |
| 155 | Primrose | Early Youth | 167 | Prímula | Primera juventud | primevère |  |
| 156 | Pyrus Japonica | Fairies’ Fire | 168 | Membrillero del Japón | Fuego de las hadas | cognassier du Japon |  |
| 157 | Quamoclit | Busybody | 169 | Cabello de ángel (Ipomoea quamoclit) | Entrometido | quamoclit |  |
| 158 | Ragged Robin | Dandy | 170 | Flor del cuclillo (Lychnis flos-cuculi) | Petimetre | fleur de coucou |  |
| 159 | Ranunculus, Asiatic | You are radiant with charms | 171 | Ranúnculo asiático | Resplandeces de encantos | renoncule des jardins |  |
| 160 | Red Bay | Love's Memory | 172 | Laurel rojo (Persea borbonia) | Recuerdo del amor | laurier rouge | EE. UU. |
| 161 | Red Mulberry | Wisdom | 173 | Morera roja | Sabiduría | mûrier rouge | EE. UU. |
| 162 | Rose Bud | Young Girl | 174 | Capullo de rosa | Muchacha | bouton de rose |  |
| 163 | Rose, Hundred-leaved | Grace | 175 | Rosa de cien hojas | Gracia | rose cent-feuilles |  |
| 164 | Rose Bud, White | The heart that knows not love | 176 | Capullo de rosa blanca | El corazón que no conoce el amor | bouton de rose blanche |  |
| 165 | Rose, Wild | Simplicity | 177 | Rosa silvestre | Sencillez | églantier |  |
| 166 | Rose, Sweet-Briar | Imagination | 178 | Rosa mosqueta olorosa | Imaginación | églantier odorant |  |
| 167 | Rose, Yellow | Infidelity | 179 | Rosa amarilla | Infidelidad | rose jaune |  |
| 168 | Rose, White | Silence | 180 | Rosa blanca | Silencio | rose blanche |  |
| 169 | Rose | Beauty | 181 | Rosa | Belleza | rose |  |
| 170 | Rose, Multiflora | Many Charms | 182 | Rosa multiflora | Muchos encantos | rosier multiflore |  |
| 171 | Rose, Musk | Capricious Beauty | 183 | Rosa almizcleña | Belleza caprichosa | rose musquée |  |
| 172 | Rose, Moss | Pleasure without alloy | 184 | Rosa musgosa | Placer sin mezcla | rose mousseuse |  |
| 173 | Rose, Monthly | Beauty ever new | 185 | Rosa de todos los meses | Belleza siempre nueva | rose des quatre-saisons |  |
| 174 | Rosemary | Your presence revives me | 186 | Romero | Tu presencia me reanima | romarin |  |
| 175 | Rue | Grace, or Purification. | 187 | Ruda | Gracia, o purificación | rue |  |
| 176 | Scarlet Ipomea | I attach myself to you | 188 | Ipomea escarlata | Me apego a ti | ipomée écarlate |  |
| 177 | Scotch Fir | Elevation | 189 | Pino silvestre | Elevación | pin sylvestre |  |
| 178 | Snowdrop | Consolation | 190 | Campanilla de invierno | Consuelo | perce-neige | Coincide con La Tour (sección 3) |
| 179 | Spiderwort | Transient Happiness | 191 | Tradescantia | Felicidad pasajera | éphémère de Virginie |  |
| 180 | Starwort | Afterthought | 192 | Áster | Lo que se piensa después | aster |  |
| 181 | Strawberry Tree | Perseverance | 193 | Madroño | Perseverancia | arbousier |  |
| 182 | Sun-Flower | Lofty and pure thoughts | 194 | Girasol | Pensamientos elevados y puros | tournesol |  |
| 183 | Sweet Flag | Fitness | 195 | Cálamo aromático | Lo apropiado | acore odorant |  |
| 184 | Sweet Pea | Departure | 196 | Guisante de olor | Partida | pois de senteur |  |
| 185 | Sweet Sultan | Felicity | 197 | Ambarina (Centaurea moschata) | Dicha | centaurée musquée |  |
| 186 | Sweet William | Childhood | 198 | Clavel del poeta | Infancia | œillet de poète |  |
| 187 | Sycamore | Woodland beauty | 199 | Sicomoro | Belleza del bosque | sycomore |  |
| 188 | Syringa | Memory | 200 | Celinda (Philadelphus) | Memoria | seringat |  |
| 189 | Tamarisk | Crime | 201 | Taray | Crimen | tamaris |  |
| 190 | Tansy | Resistance | 202 | Tanaceto | Resistencia | tanaisie |  |
| 191 | Teasel | Misanthropy | 203 | Cardencha | Misantropía | cardère |  |
| 192 | Thistle | Austerity | 204 | Cardo | Austeridad | chardon |  |
| 193 | Thorn Apple | Deceitful Charms | 205 | Estramonio | Encantos engañosos | stramoine |  |
| 194 | Thyme | Activity | 206 | Tomillo | Actividad | thym |  |
| 195 | Trefoil | Providence | 207 | Trébol | Providencia | trèfle |  |
| 196 | Trumpet Flower | Separation | 208 | Bignonia (flor de trompeta) | Separación | bignone |  |
| 197 | Tuberose | Voluptuousness | 209 | Nardo (tuberosa) | Voluptuosidad | tubéreuse |  |
| 198 | Tulip Tree | Rural Happiness | 210 | Tulipero | Felicidad campestre | tulipier | EE. UU. |
| 199 | Tulip | A declaration of Love | 211 | Tulipán | Una declaración de amor | tulipe |  |
| 200 | Tussilage, Sweet-scented | You shall have Justice. | 212 | Tusílago oloroso (heliotropo de invierno) | Se te hará justicia | héliotrope d'hiver |  |
| 201 | Valerian | Accommodating Disposition | 213 | Valeriana | Carácter complaciente | valériane |  |
| 202 | Venus’s Fly-trap | Deceit | 214 | Venus atrapamoscas | Engaño | dionée | EE. UU. |
| 203 | Vervain | Superstition | 215 | Verbena | Superstición | verveine |  |
| 204 | Violet | Modesty | 216 | Violeta | Modestia | violette | Coincide con la página «Viola» (sección 3) |
| 205 | Violet, White | Candour | 217 | Violeta blanca | Candor | violette blanche |  |
| 206 | Wall Flower | Fidelity in Misfortune | 218 | Alhelí amarillo | Fidelidad en la desgracia | giroflée des murailles |  |
| 207 | Wall Speedwell | Fidelity | 219 | Verónica | Fidelidad | véronique |  |
| 208 | Walnut, Black | Intellect | 220 | Nogal negro | Intelecto | noyer noir | EE. UU. |
| 209 | Water Lily | Eloquence | 221 | Nenúfar | Elocuencia | nénuphar |  |
| 210 | Wheat | Riches | 222 | Trigo | Riqueza | blé |  |
| 211 | White Oak | Independence | 223 | Roble blanco | Independencia | chêne blanc | EE. UU. |
| 212 | Whortleberry | Treason | 224 | Arándano | Traición | airelle |  |
| 213 | Willow | Forsaken | 225 | Sauce | Abandono («abandonado») | saule |  |
| 214 | Willow Herb | Pretension | 226 | Adelfilla (Epilobium) | Pretensión | épilobe |  |
| 215 | Winter Cherry | Deception | 227 | Alquequenje | Engaño | coqueret |  |
| 216 | Wood Sorrel | Joy | 228 | Acederilla | Alegría | oxalide |  |
| 217 | Wormwood | Absence | 229 | Ajenjo | Ausencia | absinthe |  |
| 218 | Yarrow | War | 230 | Milenrama | Guerra | achillée |  |
| 219 | Yew | Sorrow | 231 | Tejo | Tristeza | if |  |
| 220 | Zinnia | Absence | 232 | Zinnia | Ausencia | zinnia |  |

## 4. La hora de París

| Dato | Qué dice la fuente (cita) | Página | Veredicto |
|---|---|---|---|
| Qué es | «Le temps moyen de Paris est le temps solaire moyen calculé par rapport à la méridienne de France qui passe par le centre de l'Observatoire de Paris» | [Temps moyen de Paris](https://fr.wikipedia.org/wiki/Temps_moyen_de_Paris) | Comprobado |
| Antes de 1891 | París la usa para sus relojes desde el 24 de diciembre de 1826; otras ciudades la adoptan por su cuenta (Rennes en 1858, luego Bayonne, Le Havre, Lyon, Nantes, Angoulême, Toulouse, Bordeaux) | [Temps moyen de Paris](https://fr.wikipedia.org/wiki/Temps_moyen_de_Paris) | Comprobado |
| La hora de los trenes | «pour favoriser les voyageurs non ponctuels, on décide que l'heure du chemin de fer serait en retard de cinq minutes sur l'heure de Paris» | [Heure en France](https://fr.wikipedia.org/wiki/Heure_en_France) | Comprobado (cita a Angelier, 1998). Si siguió así hasta 1911, **[sin verificar]** |
| **1891** | «L'uniformisation de l'heure sur l'ensemble du territoire national est imposée par la loi du 14 mars 1891 qui fixe l'heure légale en France métropolitaine et en Algérie à l'heure du temps moyen de Paris». Publicada en el *Journal officiel* n.º 73 del 15 de marzo de 1891, p. 1233 | [Heure en France](https://fr.wikipedia.org/wiki/Heure_en_France), [Temps moyen de Paris](https://fr.wikipedia.org/wiki/Temps_moyen_de_Paris) | Comprobado |
| **1911** | «Par la loi du 9 mars 1911, la France adopte l'heure du méridien de Greenwich […] ainsi que le découpage de la journée en 24 heures au lieu de deux fois 12 heures». «le texte de loi ne mentionne pas explicitement la ville anglaise de Greenwich. En effet, l'heure de référence reste l'« heure de Paris » à laquelle on soustrait 9 minutes et 21 secondes». *Journal officiel* n.º 68 del 10 de marzo de 1911, p. 1882 | [Heure en France](https://fr.wikipedia.org/wiki/Heure_en_France) | Comprobado |
| La fórmula de la ley | «l'heure du temps moyen de Paris retardée de neuf minutes vingt et une secondes» | [Heure allemande](https://fr.wikipedia.org/wiki/Heure_allemande) | Comprobado como cita de la ley (la nota de esa página pone por error «Loi du 9 mars 1900»). El texto en el *Journal officiel* **[sin verificar]** (Gallica bloqueado) |
| Cuánto es de verdad | «le décalage horaire entre le méridien de Paris et celui de Greenwich est en réalité de 9 minutes et 20,921 s (arrondies à 21 secondes)» | [Heure en France](https://fr.wikipedia.org/wiki/Heure_en_France) | Comprobado |
| Desde qué hora del 1911 | El *Petit Journal* del 11 de marzo de 1911 publica «La nouvelle heure légale» | [Heure en France](https://fr.wikipedia.org/wiki/Heure_en_France) | La hora exacta del cambio **[sin verificar]** |

**Para la app.** En 1900, «la hora de París» = la hora de Greenwich + 9 min 21 s. **Calculado:** si el teléfono está en la hora de Francia de hoy (UTC+1 en invierno, UTC+2 en verano), la hora de París de 1900 es la de hoy menos 50 min 39 s en invierno, o menos 1 h 50 min 39 s en verano. Y los relojes de las estaciones de tren, 5 minutos menos todavía.

## 5. El metro de 1900 y las entradas de Guimard

### La línea 1 (entonces «ligne A»)

| Dato | Qué dice la fuente (cita) | Página | Veredicto |
|---|---|---|---|
| Los pasos previos | «20 avril 1896 : le Conseil municipal de Paris adopte le projet de réseau de Fulgence Bienvenüe»; «30 mars 1898 : déclaration d'utilité publique des six premières lignes»; «4 octobre 1898 : lancement des travaux de la ligne 1»; el 15 de junio de 1900 la ciudad entrega la línea a la Compagnie du chemin de fer métropolitain de Paris (CMP) para pruebas | [Ligne 1 du métro de Paris](https://fr.wikipedia.org/wiki/Ligne_1_du_métro_de_Paris) | Comprobado |
| **La apertura** | «Le 19 juillet 1900 à 13 heures, la ligne est ouverte au public entre Porte Maillot et Porte de Vincennes pour relier les différents sites de l'exposition universelle et desservir les épreuves des Jeux olympiques de 1900 au bois de Vincennes». «Seules huit stations sont finalisées et ouvertes à l'inauguration, les dix autres le sont progressivement entre le 6 août et le 1 septembre 1900» | [Ligne 1 du métro de Paris](https://fr.wikipedia.org/wiki/Ligne_1_du_métro_de_Paris), [Paris Metro Line 1](https://en.wikipedia.org/wiki/Paris_Metro_Line_1) | Comprobado |
| Cómo eran las estaciones | «La plupart d'entre elles sont longues de 75 mètres et leurs quais sont larges de 4.10 mètres» | Ídem | Comprobado |
| Trenes y frecuencia | «À l'origine, l'exploitation démarre avec un train toutes les dix minutes, puis six minutes aux heures de pointe». Los trenes eran de 3 y luego 4 coches, «tout en bois», de 36 m | Ídem | Comprobado |
| Viajeros | 557 612 en julio de 1900, 1 799 686 en agosto, 3 185 456 en septembre (cita a Jean Robert, *Notre Métro*) | Ídem | Comprobado |
| Precio | «En 1900, il y a deux classes : le prix de la première est fixé à 25 centimes et celui de la seconde à 15 centimes, quel que soit le parcours» | [Métro de Paris](https://fr.wikipedia.org/wiki/Métro_de_Paris) | Comprobado (cita *Le Matin* del 10 de marzo de 1900) |
| Horario de servicio | — | — | **[sin verificar]** |

### Las 18 estaciones, de oeste a este, con su nombre de 1900

Fuentes: la ficha de cada estación en Wikipedia en francés (`mise en service` y `nom inaugural`, más su texto), la lista de cambios de nombre de [Ligne 1 du métro de Paris](https://fr.wikipedia.org/wiki/Ligne_1_du_métro_de_Paris) y el orden de [Modèle:Métro de Paris/stations 1](https://fr.wikipedia.org/wiki/Modèle:Métro_de_Paris/stations_1).

| # | Nombre en 1900 | Abre | Nombre de hoy (y desde cuándo) | Página |
|---|---|---|---|---|
| 1 | **Porte Maillot** | 19 jul 1900 | Porte Maillot (la estación se movió en 1936) | [Porte Maillot](https://fr.wikipedia.org/wiki/Porte_Maillot_(métro_de_Paris)) |
| 2 | **Obligado** | 1 sep 1900 | Argentine (25 may 1948) | [Argentine](https://fr.wikipedia.org/wiki/Argentine_(métro_de_Paris)) |
| 3 | **Étoile** | 1 sep 1900 | Charles de Gaulle - Étoile (1970) | [Charles de Gaulle - Étoile](https://fr.wikipedia.org/wiki/Charles_de_Gaulle_-_Étoile_(métro_de_Paris)) |
| 4 | **Alma** | 13 ago 1900 | George V (27 may 1920) | [George V](https://fr.wikipedia.org/wiki/George_V_(métro_de_Paris)) |
| 5 | **Marbeuf** (también «Marbœuf») | 19 jul 1900 (según su página; ver abajo) | Franklin D. Roosevelt (30 oct 1946) | [Franklin D. Roosevelt](https://fr.wikipedia.org/wiki/Franklin_D._Roosevelt_(métro_de_Paris)) |
| 6 | **Champs-Élysées** | 19 jul 1900 | Champs-Élysées - Clemenceau (20 may 1931) | [Champs-Élysées - Clemenceau](https://fr.wikipedia.org/wiki/Champs-Élysées_-_Clemenceau_(métro_de_Paris)) |
| 7 | **Concorde** | 13 ago 1900 | Concorde | [Concorde](https://fr.wikipedia.org/wiki/Concorde_(métro_de_Paris)) |
| 8 | **Tuileries** | 19 jul 1900 (según su página; ver abajo) | Tuileries | [Tuileries](https://fr.wikipedia.org/wiki/Tuileries_(métro_de_Paris)) |
| 9 | **Palais-Royal** | 19 jul 1900 | Palais-Royal - Musée du Louvre (1989) | [Palais-Royal - Musée du Louvre](https://fr.wikipedia.org/wiki/Palais-Royal_-_Musée_du_Louvre_(métro_de_Paris)) |
| 10 | **Louvre** | 13 ago 1900 | Louvre - Rivoli (1989) | [Louvre - Rivoli](https://fr.wikipedia.org/wiki/Louvre_-_Rivoli_(métro_de_Paris)) |
| 11 | **Châtelet** | 6 ago 1900 | Châtelet | [Châtelet](https://fr.wikipedia.org/wiki/Châtelet_(métro_de_Paris)) |
| 12 | **Hôtel de Ville** | 19 jul 1900 | Hôtel de Ville | [Hôtel de Ville](https://fr.wikipedia.org/wiki/Hôtel_de_Ville_(métro_de_Paris)) |
| 13 | **Saint-Paul** | 6 ago 1900 | Saint-Paul (con el subtítulo «Le Marais») | [Saint-Paul](https://fr.wikipedia.org/wiki/Saint-Paul_(métro_de_Paris)) |
| 14 | **Bastille** | 19 jul 1900 | Bastille | [Bastille](https://fr.wikipedia.org/wiki/Bastille_(métro_de_Paris)) |
| 15 | **Gare de Lyon** | 19 jul 1900 | Gare de Lyon | [Gare de Lyon](https://fr.wikipedia.org/wiki/Gare_de_Lyon_(métro_de_Paris)) |
| 16 | **Reuilly** | 20 ago 1900 | Reuilly - Diderot (5 may 1931) | [Reuilly - Diderot](https://fr.wikipedia.org/wiki/Reuilly_-_Diderot_(métro_de_Paris)) |
| 17 | **Nation** | 19 jul 1900 | Nation | [Nation](https://fr.wikipedia.org/wiki/Nation_(métro_de_Paris)) |
| 18 | **Porte de Vincennes** | 19 jul 1900 | Porte de Vincennes | [Porte de Vincennes](https://fr.wikipedia.org/wiki/Porte_de_Vincennes_(métro_de_Paris)) |

**Una contradicción (dudoso).** La página de la línea da las ocho estaciones del primer día: «Porte de Vincennes, Nation, Gare de Lyon, Bastille, Hôtel de Ville, Palais-Royal - Musée du Louvre, Champs-Élysées - Clemenceau et Porte Maillot». Pero las páginas de **Marbeuf** y de **Tuileries** dicen que abrieron también el 19 de julio. Con esas dos serían diez el primer día y ocho después, y no «ocho y diez». La página inglesa repite la cuenta de «eight». **Propuesta:** dar por abiertas el 19 de julio las ocho de la lista y anotar que Marbeuf y Tuileries quizá también. Antes de abrir, los trenes pasaban sin parar por las estaciones sin terminar (lo dicen las páginas de Saint-Paul y Alma).

Otro detalle: la página sobre las entradas de Guimard llama «Rue de Reuilly» a la estación de Reuilly; la de la estación dice que su nombre inaugural era «Reuilly» **[dudoso]**.

### Los dos ramales que abrieron en 1900

| Ramal | Abre | Estaciones | Qué dice la fuente | Página |
|---|---|---|---|---|
| Étoile – Trocadéro | **2 oct 1900** | Étoile, Kléber, Boissière, Trocadéro | «Le court tronçon Étoile - Trocadéro du Circulaire sud - ou ligne 2 sud - est ouvert dès le 2 octobre 1900 en embranchement de la ligne 1, afin de desservir l'exposition universelle». Hoy es de la línea 6 | [Ligne 6](https://fr.wikipedia.org/wiki/Ligne_6_du_métro_de_Paris), [Kléber](https://fr.wikipedia.org/wiki/Kléber_(métro_de_Paris)), [Boissière](https://fr.wikipedia.org/wiki/Boissière_(métro_de_Paris)), [Trocadéro](https://fr.wikipedia.org/wiki/Trocadéro_(métro_de_Paris)) |
| Étoile – Porte Dauphine («ligne 2 Nord») | **13 dic 1900** (o el 3, ver al lado) | Porte Dauphine, Victor Hugo, Étoile | «On considère alors ce tronçon comme une antenne de la ligne 1, même si elle constitue techniquement une ligne indépendante». 1600 m. Hoy es de la línea 2 | [Ligne 2](https://fr.wikipedia.org/wiki/Ligne_2_du_métro_de_Paris), [Victor Hugo](https://fr.wikipedia.org/wiki/Victor_Hugo_(métro_de_Paris)), [Porte Dauphine](https://fr.wikipedia.org/wiki/Porte_Dauphine_(métro_de_Paris)) |

**Dudoso:** la misma página de la línea 2 da el **13 de diciembre** en la cronología y el **3 de diciembre** en el texto (citando a Tricoire, p. 156). Las páginas de las dos estaciones dicen 13 de diciembre.

**A Montmartre no llegaba el metro en 1900.** El tramo Étoile – Anvers de la línea 2 se abrió el 7 de octubre de 1902, y hasta Nation, el 2 de abril de 1903 ([Ligne 2](https://fr.wikipedia.org/wiki/Ligne_2_du_métro_de_Paris)). Comprobado.

### Las entradas de Hector Guimard (1867–1942)

Página principal: [Entrée de métro Guimard](https://fr.wikipedia.org/wiki/Entrée_de_métro_Guimard) (artículo con distinción «bon article» de 2021, que se apoya sobre todo en Descouturelle, Mignard y Rodriguez, 2012).

| Dato | Qué dice la fuente (cita) | Veredicto |
|---|---|---|
| Qué son | «Certaines de ces entrées consistent en un édicule couvert plus ou moins important mêlant fer, fonte, verre et lave émaillée ; la plupart sont de simples entourages constitués d'une balustrade et d'un portique en fonte sculptée, dont les hauts candélabres encadrent une enseigne marquée « Métropolitain »» | Comprobado |
| De cuándo | «ces accès édifiés à partir du printemps 1900 sont abandonnés après 1913»; los últimos se pusieron entre el 13 de julio y el 30 de septiembre de 1913 | Comprobado |
| Cuántos | «Sur les 167 qui ont été édifiés, la plupart des édicules et près de la moitié des entourages sont détruits». Más abajo: «104 entourages à écussons et 47 à cartouches moins hauts, ainsi que 10 édicules et 3 pavillons de gares» | Comprobado. **Ojo: 104 + 47 + 10 + 3 = 164, no 167** (Calculado) |
| Cómo llegó Guimard | La CMP convocó un concurso (programa publicado el 10 de junio de 1899) y Guimard, que no se presentó, se quedó con el encargo «dans des circonstances un peu obscures» | Comprobado |
| **Edículo A** | 5,85 × 3,75 m, 4 m de alto; cuatro pilares, techo a dos aguas «classique». Solo se hicieron dos, ya desaparecidos: Saint-Paul y Reuilly | Comprobado |
| **Edículo B** | 5,95 × 3,75 m, 4,70 m de alto; tres pilares, fondo convexo y techo en V invertida. Se puso en siete sitios; sigue en pie el de Porte Dauphine | Comprobado |
| **La «libélula»** | Es un apodo del **edículo B**: el periodista Georges Bans lo comparó con una «libellule déployant ses ailes légères» (Montamat, 2021, p. 113). La ficha de la página pone de imagen «La « libellule » de la station Porte Dauphine» | Comprobado |
| **La marquesina** | «Une marquise arrondie complète ces dispositifs du côté ouvert sur l'escalier» | Comprobado. La marquesina es una parte del edículo, no un tipo aparte |
| Variantes a claraboya | Una de cada modelo; la del A, que estaba en Hôtel de Ville, está hoy en Abbesses | Comprobado |
| **Los pabellones** | Tres «véritables petites gares, avec guichets et salles d'attente»: uno en Bastille (más de 90 m²) y dos en Étoile | Comprobado |
| **Las barandillas** («entourages») | Rambarda de fundición de unos 1,25 m con escudos; en la escalera, «un haut portique repérable de loin, fait de deux candélabres en fonte moulurée». Desde 1901, en los accesos estrechos, letreros con solo «Métro» | Comprobado |
| **El letrero «Métropolitain»** | Una placa de lava esmaltada «où le mot « Métropolitain » figure dans un lettrage Art nouveau pour lequel Guimard imagine maintes variantes». La casa Gillet hizo «112 enseignes « Métropolitain » en tôle émaillée, signées Guimard entre 1902 et 1903» | Comprobado |
| **Los faroles** | «deux verrines rouge orangé légèrement asymétriques et piriformes ayant pour fonction de signaler l'entrée de la station», de la cristalería de Pantin. Pero Bruno Montamat sostiene que primero fueron blancas («brins de muguet») y que en agosto de 1907 se empezaron a poner bombillas rojas. Desde 1901, bombillas eléctricas bajo las marquesinas | Comprobado. El color de 1900, **dudoso** |
| **Qué había en 1900** | «Lors de l'inauguration d'une huitaine d'arrêts de la ligne A, le 19 juillet 1900, seules les balustrades ont été posées ; les premiers édicules B ne le sont qu'entre août et septembre ; les édicules A, les enseignes, les candélabres attendront 1901, et les pavillons ne seront complètement achevés qu'en 1902» | Comprobado (ver la corrección 7) |
| Los materiales | Fundición de la fundición de arte del Val d'Osne; piedra de Comblanchien en la base; lava de Volvic esmaltada por Eugène Gillet en Saint-Denis; vidrios probablemente de Charles Champigneulle | Comprobado |
| Lo que quería Guimard | El «Style Guimard», del que reclama la paternidad en la Exposición de 1900, con tres principios: «Logique, Harmonie et Sentiment» | Comprobado |
| Los animales | «libellule mais aussi raie manta pour les toitures des édicules B, têtes d'hippocampes à l'extrémité des arceaux, papillons ou carapaces de scarabées sur les écussons, corps, tête et pattes de mante religieuse en haut des candélabres» | Comprobado (es lectura de autores modernos) |
| La ruptura | Convención del 1 de mayo de 1903: Guimard cobra 21 000 francos y cede sus modelos a la CMP. En 1904, la entrada de la Ópera se encarga a Cassien-Bernard | Comprobado |
| La protección | Inscripción de algunas en 1965; las 86 que quedaban, protegidas el 29 de mayo de 1978 | Comprobado |

## 6. La Exposición Universal de 1900

| Dato | Qué dice la fuente (cita) | Página | Veredicto |
|---|---|---|---|
| Fechas | «elle est inaugurée le 14 avril 1900 par le président Émile Loubet et ouvre au public le 15 avril 1900. Elle se termine le 12 novembre 1900, après 212 jours d'ouverture. Elle accueille 50,8 millions de visiteurs» | [Exposition universelle de 1900](https://fr.wikipedia.org/wiki/Exposition_universelle_de_1900) | Comprobado. La misma página dice también «51 millions» y que 48,3 millones pagaron la entrada |
| Países y entrada | 40 países (según el BIE), 83 047 expositores; la entrada costaba **1 franco** de 10 a 18 h, cuando «l'heure de travail est alors payée entre 40 et 50 centimes» | Ídem | Comprobado (la cifra del salario es de un pie de imagen, sin fuente) |
| **El Palacio de la Electricidad** | «Situé sur le Champ-de-Mars, en face de la tour Eiffel, le palais de l'Électricité était le clou du spectacle de l'exposition. Le bâtiment avait été conçu par Eugène Hénard comme un temple de fer et de verre». Del *Annuaire de l'Exposition*: «Dans le palais de l'Électricité se fabrique, en effet, toute l'énergie nécessaire à l'éclairage et à la marche des organes divers de l'Exposition» | Ídem | Comprobado |
| Cómo era | De hierro y vidrio, 130 m de largo y 70 m de alto; en lo alto, el Genio de la Electricidad en un carro tirado por hipogrifos; «illuminé par 5000 lampes à incandescence de diverses couleurs, 8 lampes à arc avec projecteurs à verres colorés». Delante, el Château d'Eau, con una cascada de 30 m que vierte 1200 litros por segundo y 1100 lámparas más | [Palais de l'Électricité et Château d'Eau](https://fr.wikipedia.org/wiki/Palais_de_l'Électricité_et_Château_d'Eau) | Comprobado (cita una publicación de 1900, «LEPT»). La página de la Exposición pone el Genio, de 6 m, sobre el Château d'Eau: **dudoso** dónde estaba |
| ¿Era nueva la electricidad? | «montrer toutes les possibilités de l'électricité, contrôlée depuis une vingtaine d'années par l'humain en 1900» | [Exposition universelle de 1900](https://fr.wikipedia.org/wiki/Exposition_universelle_de_1900) | Comprobado (ver la corrección 4) |
| Otras novedades | La acera rodante («rue de l'Avenir»), de 3 km y dos velocidades; la gran rueda; el cine de los Lumière en una pantalla de 21 × 16 m; el Cinéorama; la gran lente del Palacio de la Óptica | Ídem | Comprobado |
| **El Grand Palais** | Construido desde 1897; arquitectos Henri Deglane, Albert Louvet, Albert Thomas y Charles Girault, que «n'ayant pu être départagés, sont finalement choisis pour fusionner leurs propositions». Inaugurado el **1 de mayo de 1900** por Loubet. Nave de unos 240 m bajo una gran vidriera. En 1900 tuvo la Centenal y la Decenal de bellas artes | [Grand Palais (Paris)](https://fr.wikipedia.org/wiki/Grand_Palais_(Paris)), [Exposition universelle de 1900](https://fr.wikipedia.org/wiki/Exposition_universelle_de_1900) | Comprobado |
| **El Petit Palais** | «construit à l'occasion de l'Exposition universelle de 1900 par l'architecte Charles Girault»; en 1900, una retrospectiva del arte francés hasta 1800; el museo se inauguró el 11 de diciembre de 1902 | [Petit Palais](https://fr.wikipedia.org/wiki/Petit_Palais) | Comprobado |
| **El puente Alejandro III** | Símbolo de la alianza franco-rusa; primera piedra puesta por Nicolás II el **7 de octubre de 1896**; «Le 14 avril 1900, Émile Loubet […] a ouvert l'exposition universelle et inauguré le pont Alexandre-III». Ingenieros Résal, Alby y Grison; arquitectos Cassien-Bernard y Cousin; 152 m | [Pont Alexandre-III](https://fr.wikipedia.org/wiki/Pont_Alexandre-III) | Comprobado |
| El metro | «L'exposition coïncide avec l'ouverture de la première ligne du métro de Paris […] inaugurée le 19 juillet 1900» | [Exposition universelle de 1900](https://fr.wikipedia.org/wiki/Exposition_universelle_de_1900) | Comprobado |
| Un invernadero de la época | Los invernaderos del Fleuriste municipal en Auteuil: «grandes serres en fonte peintes en bleu turquoise, édifiées par l'architecte français en 1898»; «Les travaux durent de 1895 à 1898» | [Jardin des serres d'Auteuil](https://fr.wikipedia.org/wiki/Jardin_des_serres_d'Auteuil) | Comprobado. Si el turquesa es el color de 1898, **[sin verificar]** |
| Las postales | «Dans les années 1890, les cartes postales deviennent illustrées»; «Dans les années 1900 à 1920, âge d'or de la carte postale» | [Carte postale](https://fr.wikipedia.org/wiki/Carte_postale) | Comprobado (sirve para las «cartas» del tema) |

## 7. Los artistas y los objetos

### Alfons Mucha (1860–1939)

Página: [Alphonse Mucha](https://en.wikipedia.org/wiki/Alphonse_Mucha).

- **Fechas.** Nació el 24 de julio de 1860 en Ivančice (Moravia) y murió el 14 de julio de 1939. Comprobado.
- **El encargo de *Gismonda*.** «on 26 December, Bernhardt made a telephone call to Maurice de Brunhoff, the manager of the publishing firm Lemercier, which printed her theatrical posters, ordering a new poster for the continuation of the play *Gismonda*». La obra, de Victorien Sardou, se había estrenado el 31 de octubre de 1894 en el Théâtre de la Renaissance. Comprobado.
- **El cartel.** «more than life-size; a little more than two meters high, with Bernhardt in the costume of a Byzantine noblewoman, dressed in an orchid headdress and a floral stole, and holding a palm branch». Comprobado.
- **Cuándo salió.** «The poster appeared on the streets of Paris on 1 January 1895 and caused an immediate sensation». Bernhardt encargó 4000 copias en 1895 y 1896 y le dio a Mucha un contrato de seis años. Comprobado (ver la corrección 14).
- **Los carteles siguientes para Bernhardt.** *La Dame aux camélias* (1896), *Lorenzaccio* (1896), *Médée* (1898), *La Tosca* (1898), *Hamlet* (1899). Comprobado.
- **Champenois.** «Champenois had a large printing house on Boulevard Saint Michel that employed three hundred workers, with twenty steam presses». Comprobado (sirve para el taller, sección 8).
- **En la Exposición de 1900.** Decoró el pabellón de Bosnia y Herzegovina; hizo vitrinas para el joyero Georges Fouquet y para el perfumista Houbigant. Comprobado.
- **Lo que la app puede decir sin errores.** «Mucha, *Gismonda*, cartel fechado en 1894, en las calles de París desde el 1 de enero de 1895, impreso por Lemercier». Es de dominio público (Mucha murió en 1939).

### Hector Guimard (1867–1942)

Ver la sección 5. Además, el Castel Béranger (1895–1898) fue de los primeros edificios de París en el estilo nuevo ([Art Nouveau](https://en.wikipedia.org/wiki/Art_Nouveau)). Comprobado.

### Émile Gallé (1846–1904) y Nancy

- **Fechas.** «4 May 1846 in Nancy – 23 September 1904 in Nancy» ([Émile Gallé](https://en.wikipedia.org/wiki/Émile_Gallé)). Comprobado.
- **Botánico.** Estudió con D. A. Godron, director del jardín botánico de Nancy; «Gallé ordered his designers to use only real flowers and plants as their models». En 1889 tenía más de trescientos empleados. Comprobado.
- **En 1900.** «the 1900 Paris Universal Exposition, which was the summit of his fame. His work at the Exposition was rewarded with two Grand Prizes, a gold medal, and the title of a commander in the Legion of Honor». Comprobado.
- **El lema.** «À la porte de son atelier de Nancy, on pouvait lire cette devise : « Ma racine est au fond des bois »»; la frase completa: «Nos racines sont au fond des bois, parmi les mousses, autour des sources» ([Émile Gallé](https://fr.wikipedia.org/wiki/Émile_Gallé), citando a Le Tacon, 1999). Comprobado.
- **La firma.** Desde la guerra de 1870, la cruz de Lorena forma parte de su firma. Comprobado.
- **La Escuela de Nancy.** «formally organized in 1901 […] It had existed informally since 1894»; manifiesto del 13 de febrero de 1901; comité: Gallé, Louis Majorelle, Antonin Daum y Eugène Vallin. Plantas modelo: ginkgo, hidrocótila, hogweed, nenúfar, cardo, calabaza, y «creatures such as dragonflies» ([École de Nancy](https://en.wikipedia.org/wiki/École_de_Nancy)). Comprobado (ver la corrección 16).

### René Lalique (1860–1945)

- **Fechas.** Nació el 6 de abril de 1860 en Aÿ y murió el 1 (o el 5) de mayo de 1945 ([René Lalique](https://en.wikipedia.org/wiki/René_Lalique)). Comprobado.
- **Motivos.** «often featuring motifs such as dragonflies, orchids, and peacocks». Comprobado.
- **La mujer libélula.** Pie de imagen: «Dragonfly lady brooch, Museu Calouste Gulbenkian, acquired from the artist in 1903»; en [Art Nouveau](https://en.wikipedia.org/wiki/Art_Nouveau): «*Dragonfly Lady* brooch by René Lalique, made of gold, enamel, chrysoprase, moonstone, and diamonds (1897–98)». En francés: «le fameux *Pectoral à la libellule* (vers 1897-1898), chef-d'œuvre très admiré à l'Exposition universelle de 1900, qu'il prêta à la tragédienne Sarah Bernhardt» ([René Lalique](https://fr.wikipedia.org/wiki/René_Lalique)). Comprobado. **Que se lo prestara a Bernhardt [sin verificar]** (la frase no lleva fuente). Ver la corrección 11.
- **En 1900.** «Son stand à l'exposition universelle de 1900 à Paris remporte un franc succès»; oficial de la Legión de Honor en 1900. Comprobado.
- **Perfumes.** «En 1910, il crée pour le parfumeur François Coty, qu'il a rencontré en 1908, le flacon pour *Ambre Antique*». Comprobado (ver la corrección 13).
- **Dominio público.** Murió en 1945: sus obras lo son desde el 1 de enero de 2016 **[Aproximación]**.

### Louis Comfort Tiffany (1848–1933)

- **Las lámparas.** «made in his design studio in Corona, Queens, New York City»; vidrio unido con cinta de cobre, no con plomo; «Tiffany Studios produced considerable numbers of designs between 1893 and the 1920s» ([Tiffany lamp](https://en.wikipedia.org/wiki/Tiffany_lamp)). Comprobado.
- **Quién las diseñó.** «Many of the lamps were not designed by […] Tiffany himself, but by Clara Driscoll». Comprobado.
- **En Francia.** «Tiffany was only able to break into the French market by having the production of his works taken over by Siegfried Bing». Comprobado.
- **En 1900.** «he won a gold medal with his stained glass windows *The Four Seasons*»; un pie de imagen dice que la lámpara de lirios de dieciocho luces se expuso en París en 1900 ([Louis Comfort Tiffany](https://en.wikipedia.org/wiki/Louis_Comfort_Tiffany)). Comprobado.
- **Las de Nancy.** Daum hacía lámparas y jarrones que «adapted well to the new technology of electric light bulbs» ([École de Nancy](https://en.wikipedia.org/wiki/École_de_Nancy)). Comprobado. Ver la corrección 12.

### La línea de látigo y Hermann Obrist

- **Qué es.** «an asymmetrical, sinuous line, often in an ornamental S-curve, usually inspired by natural forms such as plants and flowers». «It took its name from a woven fabric panel "Cyclamen", by the German artist Hermann Obrist (1895) which depicted the stems and roots of the cyclamen plant, which critics dubbed "Coup de Fouet"» ([Whiplash (decorative art)](https://en.wikipedia.org/wiki/Whiplash_(decorative_art))). Comprobado. Obrist era **suizo**, no alemán ([Hermann Obrist](https://en.wikipedia.org/wiki/Hermann_Obrist)).
- **La fecha.** La página de Obrist dice «an 1892 embroidered wall hanging called "Cyclamen"»; la de Art Nouveau, 1894 («A description published in *Pan* magazine of Hermann Obrist's wall hanging *Cyclamen* (1894), compared it to the "sudden violent curves generated by the crack of a whip"»); la de la línea de látigo, 1895. **Dudoso** (corrección 15).
- **Los motivos.** «the cyclamen, iris, orchid, thistle, mistletoe, holly, water lily, and from the stylized lines of the swan, peacock, dragonfly, and butterfly». Comprobado: sostiene el iris y la libélula del emblema.
- **Los apodos.** En Bélgica, «*Style coup de fouet* ('Whiplash style'), *Paling Stijl* ('Eel Style'), or *Style nouille* ('Noodle style') by its detractors» ([Art Nouveau](https://en.wikipedia.org/wiki/Art_Nouveau)); en Francia, «appelé avec humour « style nouille », par ses détracteurs» ([Art nouveau](https://fr.wikipedia.org/wiki/Art_nouveau)). Comprobado.

### El pavo real

- **Comprobado** como motivo, sin ser exclusivo de 1900:
  - el *Peacock Room* de Whistler (1876–1877), que la página cuenta entre los antecedentes del estilo;
  - Philippe Wolfers, *Plumes de Paon*, hebilla de cinturón (1898);
  - Aubrey Beardsley, *The Peacock Skirt* (la página pone 1892; que sea de 1893 o 1894, por las ilustraciones de *Salomé*, **[sin verificar]**);
  - C. R. Ashbee hacía colgantes con forma de pavo real.
- **Fuente** de todo: [Art Nouveau](https://en.wikipedia.org/wiki/Art_Nouveau).

### Jules Chéret y el cartel en color

- «founded a small lithography office in Paris in 1866»; «created more than 1000 advertisements» ([Poster](https://en.wikipedia.org/wiki/Poster)). Comprobado.
- «En 1881, il cède son imprimerie à la maison Chaix, dont il devient le di[recteur artistique]» ([Jules Chéret](https://fr.wikipedia.org/wiki/Jules_Chéret)). Comprobado.
- Su color, «majoritairement limitées aux trois primaires» (citando a Maindron, 1896). Comprobado.
- Huysmans, en una crítica del Salon, lo puso por encima de los cuadros (la cita, en la sección 11). Comprobado.

## 8. El taller de litografía hacia 1900 y la ley de 1900

### Cómo se imprimía en color

| Dato | Qué dice la fuente (cita) | Página | Veredicto |
|---|---|---|---|
| Una piedra por color | «fondé sur l'impression couleur par couleur, jusqu'à seize différentes. Chaque couleur était posée sur une pierre lithographique et le même document passait d'une pierre à l'autre avec des repérages précis» (el procedimiento de Godefroy Engelmann, 1837) | [Chromolithographie](https://fr.wikipedia.org/wiki/Chromolithographie) | Comprobado |
| El principio | «Lithography, including chromolithography, is a process based on the rejection of water by grease». Se dibuja con lápiz o tinta grasa; se protege con goma arábiga y ácido nítrico débil; se prueba y se corrige; «Accurate registration for multi-coloured work is achieved by the use of a key outline image and registration bars which are applied to each stone» | [Chromolithography](https://en.wikipedia.org/wiki/Chromolithography) | Comprobado |
| Los carteles baratos | «Cheaper images, like advertisements, relied heavily on an initial black print […] on which colours were then overprinted» | Ídem | Comprobado |
| La prensa a mano | Un bastidor con un carro móvil que lleva la piedra; un «râteau» de madera forrado de cuero que aprieta el papel al pasar; una manivela o un molinete de brazos, la presse «bête à cornes» | [Presse lithographique](https://fr.wikipedia.org/wiki/Presse_lithographique) | Comprobado |
| La prensa de vapor | «les presses lithographiques évoluent vers une grande complexité et une automatisation quasi identique aux presses typographiques […] notamment en France avec les presses Marinoni-Voirin»; las piedras se van cambiando por placas de zinc | [Presse lithographique](https://fr.wikipedia.org/wiki/Presse_lithographique), [Chromolithographie](https://fr.wikipedia.org/wiki/Chromolithographie) | Comprobado. Qué usaba cada taller en 1900, **[sin verificar]** |

### Imprentas reales de París

| Imprenta | Qué dice la fuente | Página | Veredicto |
|---|---|---|---|
| **Chaix** | Fundada en 1845 como Imprimerie centrale des chemins de fer (el «indicateur Chaix»); «En 1880, une importante succursale […] qui prend en 1881 le nom d'Imprimerie Chaix, est implantée au 126, rue des Rosiers, à Saint-Ouen : 2500 m², 60 chevaux-vapeur et 100 ouvriers. On y exécute, outre les affiches de Jules Chéret, directeur artistique […]». Editaba *Les Maîtres de l'affiche* | [Napoléon Chaix](https://fr.wikipedia.org/wiki/Napoléon_Chaix) | Comprobado. Saint-Ouen está fuera de París |
| **Lemercier** | Joseph-Rose Lemercier (1803–1887), «l'un des plus importants imprimeurs-lithographes parisiens du XIXe siècle»; en 1831, en el 57 de la rue de Seine. Imprimió *Gismonda* (ver Mucha) | [Joseph-Rose Lemercier](https://fr.wikipedia.org/wiki/Joseph-Rose_Lemercier) | Comprobado. La página marca como «douteux» que imprimiera a Mucha, pero la página inglesa de Mucha lo dice con fuente |
| **Champenois** | En el bulevar Saint-Michel, trescientos obreros y veinte prensas de vapor | [Alphonse Mucha](https://en.wikipedia.org/wiki/Alphonse_Mucha) | Comprobado |

### La jornada

- **Las horas que hacía un taller de litografía en 1900, las mujeres que trabajaban en él y lo que ganaba una aprendiz: [sin verificar].** Ninguna página abierta lo dice. El único dato de salario es el del pie de imagen de la Exposición (de 40 a 50 céntimos la hora), sin fuente.

### Las leyes

| Ley | Qué dice la fuente (cita) | Página | Veredicto |
|---|---|---|---|
| 1892 | «Loi de 1892 limitant à 11 heures par jour le temps de travail des femmes, ainsi que des enfants de seize à dix-huit ans» | [Réduction du temps de travail en France](https://fr.wikipedia.org/wiki/Réduction_du_temps_de_travail_en_France) | Comprobado. Su fecha (2 de noviembre de 1892) y si prohibía el trabajo de noche de las mujeres, **[sin verificar]** |
| **30 de marzo de 1900** («ley Millerand») | «Loi du 30 mars 1900 limitant à 11 heures le travail des mineurs de moins de dix-huit ans et des femmes. La mesure est étendue aux hommes travaillant dans les mêmes locaux que les femmes» | [Mars 1900](https://fr.wikipedia.org/wiki/Mars_1900) | Comprobado |
| El primer escalón (1902) | «1er avril : la durée du temps de travail des mineurs de moins de dix-huit ans, des femmes et des hommes travaillant dans des ateliers mixtes est réduite à 10 heures 30 par jour en application de la loi du 30 mars 1900» | [1902 en France](https://fr.wikipedia.org/wiki/1902_en_France) | Comprobado (cita a Charles Gide) |
| El segundo escalón (1904) | «1er avril : […] est réduite à 10 heures par jour en application de la loi du 30 mars 1900» | [1904 en France](https://fr.wikipedia.org/wiki/1904_en_France) | Comprobado |
| El ministro | «Il donne son nom à la loi du 30 mars 1900, qui harmonise la durée maximale de travail en établissant le principe de dix heures par jour». Los socialistas le reprocharon «le relèvement temporaire de dix à onze heures de la journée de travail pour les enfants» | [Alexandre Millerand](https://fr.wikipedia.org/wiki/Alexandre_Millerand) | Comprobado. Millerand era ministro de Comercio e Industria ([Grand Palais](https://fr.wikipedia.org/wiki/Grand_Palais_(Paris))) |
| Un error en Wikipedia | La página de la reducción del tiempo de trabajo la llama «Loi du 10 mars 1900 sur le contrat de travail promulger le 30 septembre» y le pone el 11 h «du 1 avril 1900 jusqu'au 1 avril 1902, puis à dix heures et demie jusqu'au 1 avril 1904» | [Réduction du temps de travail en France](https://fr.wikipedia.org/wiki/Réduction_du_temps_de_travail_en_France) | El nombre y la fecha están **mal**; los escalones coinciden con las otras páginas |
| El descanso semanal | «Loi de 1906 instituant la semaine de six jours» | Ídem | Comprobado. **En 1900 no había descanso semanal obligatorio para todos**; si la ley de 1892 daba un día de descanso a mujeres y menores, **[sin verificar]** |
| Antes | 1848: diez horas en París y once en provincias (2 de marzo), derogado el 9 de septiembre, que fija doce | Ídem | Comprobado |

**El texto literal de la ley de 1900 no se pudo leer [sin verificar].**

## 9. Los cafés-concierto y cabarets de Montmartre en 1900

| Local | Qué es | Abre | En 1900 | Página | Veredicto |
|---|---|---|---|---|---|
| **Le Chat Noir** | Cabaret artístico de Rodolphe Salis | 18 nov 1881 | **Cerrado**: «closed in 1897 not long after Salis' death»; Salis murió el 19 de marzo de 1897; la revista dura hasta el 30 de septiembre de 1897 | [Le Chat Noir](https://en.wikipedia.org/wiki/Le_Chat_Noir), [Le Chat noir](https://fr.wikipedia.org/wiki/Le_Chat_noir) | Comprobado. **No usar** |
| **Moulin-Rouge** | Baile y music-hall de Zidler y Oller, en el bulevar de Clichy | 6 oct 1889 | Abierto (se quemó en 1915) | [Moulin-Rouge](https://fr.wikipedia.org/wiki/Moulin-Rouge), [Moulin Rouge](https://en.wikipedia.org/wiki/Moulin_Rouge) | Comprobado. No es un café-concierto |
| **La Cigale** | «la salle de café-concert a été construite en 1887 à l'emplacement du Bal de La Boule Noire»; unas 1000 plazas; ampliada en 1894 | 1887 | Abierto **[Aproximación]**: ninguna página dice que cerrara antes de 1900 | [La Cigale](https://fr.wikipedia.org/wiki/La_Cigale), [La Boule noire](https://fr.wikipedia.org/wiki/La_Boule_noire_(salle_de_spectacle)) | Comprobado en parte |
| **Divan japonais** | Café-concierto de decorado japonés, en la rue des Martyrs; Yvette Guilbert «triomphe en 1891» | 1885 (con ese nombre) | Abierto: cierra en diciembre de 1892, reabre en enero de 1893 y en agosto de 1901 pasa a ser teatro | [Le Divan du Monde](https://fr.wikipedia.org/wiki/Le_Divan_du_Monde) | Comprobado |
| **Élysée-Montmartre** | Sala de baile del bulevar de Rochechouart, famosa por sus cuadrillas | 1807 | Abierto, pero con un incendio en 1900 | [Élysée-Montmartre](https://fr.wikipedia.org/wiki/Élysée-Montmartre) | Comprobado |
| **Trianon-Concert** | Café-concierto en el jardín del Élysée-Montmartre | 1894 | «en 1900 […] un incendie détruit la salle» | [Le Trianon (Paris)](https://fr.wikipedia.org/wiki/Le_Trianon_(Paris)) | Comprobado. Evitarlo en 1900 |
| **Cabaret des Quat'z'Arts** | Cabaret de François Trombert, en el 62 del bulevar de Clichy; «il profita de la fermeture» del Chat Noir | verano de 1893 | Abierto (cierra en el verano de 1914) | [Cabaret des Quat'z'Arts](https://fr.wikipedia.org/wiki/Cabaret_des_Quat'z'Arts) | Comprobado |
| **Le Mirliton** | Cabaret de Aristide Bruant, donde «le verre de bière est vendu treize sous» | — | **[sin verificar]** si seguía en 1900 | [Aristide Bruant](https://fr.wikipedia.org/wiki/Aristide_Bruant) | — |
| **Au Lapin agile** | Cabaret de la parte alta de la colina | 1880 (ficha) | **[sin verificar]** quién lo llevaba en 1900 | [Au Lapin agile](https://fr.wikipedia.org/wiki/Au_Lapin_agile) | — |

**El café-concierto en general.** La página [Café-concert](https://fr.wikipedia.org/wiki/Café-concert) llama a 1864–1896 su «Apogée» y a 1896–1914 el «Déclin du caf'conc', résistance du music-hall», por la competencia del cinematógrafo. En 1900 ya era un espectáculo **en declive**. Comprobado.

**Propuesta.** Para «un café-concierto de Montmartre» en 1900: **La Cigale** o **el Divan japonais**. Ver la corrección 10.

## 10. El nombre de la aprendiz

- **La fuente buena no se pudo abrir.** Es el fichero de nombres del INSEE ([Fichier des prénoms](https://www.insee.fr/fr/statistiques/2540004), citado por [List of most popular given names](https://en.wikipedia.org/wiki/List_of_most_popular_given_names)), que además empieza en 1900, no en 1880 **[sin verificar]**.
- **Un dato suelto.** El récord de Louise es de 1901, con 7199 nacimientos ([Louise (prénom)](https://fr.wikipedia.org/wiki/Louise_(prénom)), citando cifras del INSEE de un sitio de terceros). Comprobado en parte.
- **Una muestra.**
  - **Cómo se hizo.** En Wikipedia en francés se buscaron las páginas de las categorías «Naissance en … 1879», «… 1880» y «… 1881» (por año y por mes) que están también en «Naissance à Paris» o en «Naissance dans le N<sup>e</sup> arrondissement de Paris», y que dicen «née». Salieron 88 páginas; quitando hombres, **63 mujeres nacidas en París entre 1879 y 1881**. Se contó su primer nombre.
  - **Cuidado.** La muestra es de mujeres conocidas: artistas, actrices, escritoras, de familias acomodadas, algunas con nombre artístico. Además, el buscador da solo la primera página de resultados por año. **[Aproximación]**.

| Nombre | Veces | Nombre | Veces |
|---|---|---|---|
| Marie (sola o compuesta: Marie-Thérèse, Marie-Anne…) | 6 | Henriette | 2 |
| Marguerite | 3 | Jeanne | 2 |
| Suzanne | 3 | Louise | 2 |
| Renée | 3 | Lucienne | 2 |
| Élisabeth | 3 | Marcelle | 2 |
| Alice | 2 | Paule | 2 |
| Germaine | 2 | Una vez cada uno | Aimée, Aline, Berthe, Blanche, Catherine, Cécile, Clémentine, Edmée, Émilie, Fanny, Fernande, Hélène, Juliette, Laure, Madeleine, Marthe, Pauline, Thérèse, Valentine, Yvonne y otros |

**Propuesta:** Marguerite, Jeanne, Louise, Suzanne o Germaine (con apellido inventado). «Marie» es el más común, pero casi siempre va como primer nombre de uno compuesto.


## 11. Frases del día

- **Cómo se comprobaron.** Wikiquote (en francés y en inglés) y Wikisource en francés no abrían. Todas las frases se comprobaron en **Wikipedia en francés**, en la página que se enlaza, donde salen entre comillas o en un bloque de cita.
- **Wikipedia no es el original:** donde la página no da la página del libro, se dice. Las traducciones al español son de la sesión.
- **Todas son de dominio público:** los autores murieron antes de 1955.

| # | Frase (francés) | Autor, obra y año | Traducción | Página | Veredicto |
|---|---|---|---|---|---|
| 1 | «Ma racine est au fond des bois» | Émile Gallé, lema escrito en la puerta de su taller de Nancy | «Mi raíz está en el fondo de los bosques» | [Émile Gallé](https://fr.wikipedia.org/wiki/Émile_Gallé) | Comprobado. El año del lema **[sin verificar]** |
| 2 | «Nos racines sont au fond des bois, parmi les mousses, autour des sources» | Gallé, la frase completa del lema | «Nuestras raíces están en el fondo de los bosques, entre los musgos, alrededor de los manantiales» | Ídem (cita a Le Tacon, 1999) | Comprobado en parte: el texto de Gallé donde sale, **[sin verificar]** |
| 3 | «Logique, Harmonie et Sentiment» | Hector Guimard, los tres principios del «Style Guimard», 1900 | «Lógica, armonía y sentimiento» | [Entrée de métro Guimard](https://fr.wikipedia.org/wiki/Entrée_de_métro_Guimard) | Comprobado |
| 4 | «Est-ce qu'on devra dorénavant harmoniser la gare du Père-Lachaise avec le cimetière et la construire en forme de tombeau ?» | Guimard, entrevista en *La Presse*, 3 de octubre de 1904 | «¿Habrá que armonizar en adelante la estación del Père-Lachaise con el cementerio, y hacerla con forma de tumba?» | Ídem (cita a Vigne, 2016, p. 89) | Comprobado |
| 5 | «Et devra-t-on placer une danseuse la jambe haut levée devant la gare de la place Blanche, pour l'harmoniser avec le Moulin-Rouge ?» | Ídem | «¿Y habrá que poner una bailarina con la pierna en alto delante de la estación de la plaza Blanche, para que haga juego con el Moulin-Rouge?» | Ídem | Comprobado |
| 6 | «Il y a mille fois plus de talent dans la plus mince de ces affiches que dans la plupart des tableaux dont j'ai eu le triste avantage de rendre compte» | Joris-Karl Huysmans, sobre los carteles de Chéret, al final de una crítica del Salon | «Hay mil veces más talento en el más pequeño de estos carteles que en la mayoría de los cuadros de los que he tenido la triste ventaja de dar cuenta» | [Jules Chéret](https://fr.wikipedia.org/wiki/Jules_Chéret) (cita a Bargiel, 2010, p. 58) | Comprobado. **Qué Salon y qué año, [sin verificar]** (Huysmans reunió sus Salons de 1879–1882 en *L'Art moderne*, 1883: de memoria) |
| 7 | «se débarbouiller les yeux au-dehors, par une station prolongée devant ces palissades où éclatent les étonnantes fantaisies de Chéret» | Ídem | «lavarse los ojos afuera, con una larga parada ante esas vallas donde estallan las asombrosas fantasías de Chéret» | Ídem | Comprobado |
| 8 | «La vérité est en marche et rien ne l'arrêtera» | Émile Zola, «M. Scheurer-Kestner», *Le Figaro*, 25 de noviembre de 1897 | «La verdad está en marcha y nada la detendrá» | [J'accuse…!](https://fr.wikipedia.org/wiki/J'accuse…!) | Comprobado (no es de *J'accuse…!*: corrección 20) |
| 9 | «le monde est fait pour aboutir à un beau livre» | Stéphane Mallarmé, respuesta a Jules Huret, *Enquête sur l'évolution littéraire*, 1891 | «el mundo está hecho para acabar en un hermoso libro» | [Stéphane Mallarmé](https://fr.wikipedia.org/wiki/Stéphane_Mallarmé) | Comprobado |
| 10 | «l'absente de tous bouquets» | Mallarmé, «Avant-dire» al *Traité du Verbe* de René Ghil | «la ausente de todos los ramos» (la flor ideal, la que nombra el poema) | Ídem | Comprobado en parte: la página da solo el fragmento y no da el año (1886, **[sin verificar]**) |
| 11 | «La chair est triste, hélas ! et j'ai lu tous les livres.» | Mallarmé, «Brise marine», escrito en 1865; en *Poésies*, 1887 | «La carne es triste, ¡ay!, y he leído todos los libros» | [Brise marine](https://fr.wikipedia.org/wiki/Brise_marine) | Comprobado |
| 12 | «J'honore la rivière qui laisse s'engouffrer dans son eau des journées entières sans qu'on ait l'impression de les avoir perdues.» | Mallarmé, *Autobiographie* (carta a Verlaine) | «Honro el río, que deja hundirse en su agua días enteros sin que uno sienta que los ha perdido» | [Stéphane Mallarmé](https://fr.wikipedia.org/wiki/Stéphane_Mallarmé) | Comprobado. El año de la carta (1885) **[sin verificar]** |
| 13 | «De la musique avant toute chose» | Paul Verlaine, «Art poétique», escrito en abril de 1874; publicado en *Paris moderne* (1882) y en *Jadis et Naguère* (1884) | «La música antes que nada» | [Art poétique (Verlaine)](https://fr.wikipedia.org/wiki/Art_poétique_(Verlaine)) | Comprobado (la página copia el poema entero) |
| 14 | «Car nous voulons la Nuance encor, / Pas la Couleur, rien que la nuance !» | Ídem | «Porque aún queremos el Matiz, / ¡no el Color, nada más que el matiz!» | Ídem | Comprobado |
| 15 | «De la musique encore et toujours !» | Ídem | «¡Música, otra vez y siempre!» | Ídem | Comprobado |
| 16 | «le désir fleurit, la possession flétrit toutes choses» | Marcel Proust, *Les Plaisirs et les Jours*, 1896 | «el deseo hace florecer todas las cosas; la posesión las marchita» | [Les Plaisirs et les Jours](https://fr.wikipedia.org/wiki/Les_Plaisirs_et_les_Jours) | Comprobado en parte: la página la da en su lista de citas, **sin página del libro** |
| 17 | «il vaut mieux rêver sa vie que la vivre» | Ídem, en la misma frase | «más vale soñar la vida que vivirla» | Ídem | Comprobado en parte, igual |
| 18 | «Pour une famille vraiment vivante où chacun pense, aime et agit, avoir un jardin est une douce chose.» | Ídem | «Para una familia de verdad viva, donde cada uno piensa, ama y obra, tener un jardín es una cosa dulce» | Ídem | Comprobado en parte, igual |
| 19 | «Au jardin des Tuileries, ce matin, le soleil s'est endormi tour à tour sur toutes les marches de pierre» | Ídem | «En el jardín de las Tullerías, esta mañana, el sol se ha dormido por turnos en todos los escalones de piedra» | Ídem | Comprobado en parte, igual (la frase sigue: «comme un adolescent blond…») |
| 20 | «Les paradoxes d'aujourd'hui sont les préjugés de demain.» | Ídem | «Las paradojas de hoy son los prejuicios de mañana» | Ídem | Comprobado en parte, igual |
| 21 | «la flatterie n'est parfois que l'épanchement de la tendresse» | Proust, «Contre la franchise», *La Revue blanche*, julio de 1893, p. 57 | «la adulación a veces no es más que el desahogo de la ternura» | [Marcel Proust](https://fr.wikipedia.org/wiki/Marcel_Proust) | Comprobado |
| 22 | «C'est bien simple, mes dessins sont la clef de mon œuvre, ma sculpture n'est que du dessin sous toutes les dimensions» | Auguste Rodin, en sus cuadernos | «Es muy sencillo: mis dibujos son la llave de mi obra; mi escultura no es más que dibujo en todas las dimensiones» | [Auguste Rodin](https://fr.wikipedia.org/wiki/Auguste_Rodin) | Comprobado. El año **[sin verificar]** |
| 23 | «Cette œuvre dont on a ri, qu'on a pris soin de bafouer parce qu'on ne pouvait pas la détruire, est la résultante de toute ma vie, le pivot de mon esthétique» | Rodin, sobre su *Balzac*, 1908 | «Esta obra de la que se rieron, que se cuidaron de escarnecer porque no podían destruirla, es el resultado de toda mi vida, el eje de mi estética» | Ídem (cita a Pinet, 2009) | Comprobado |
| 24 | «Je cours les dangers du succès» | Jules Renard, *Journal* (1887–1910; publicado de 1925 a 1927) | «Corro los peligros del éxito» | [Jules Renard](https://fr.wikipedia.org/wiki/Jules_Renard) | Comprobado. La fecha exacta de la entrada **[sin verificar]** (la página la pone hacia 1894) |
| 25 | «Oui, je porte ma décoration. Il faut avoir le courage de ses faiblesses.» | Renard, al aceptar la Legión de Honor, en 1900 | «Sí, llevo mi condecoración. Hay que tener el valor de las propias debilidades» | Ídem | Comprobado (la nota no da la fuente de la frase) |
| 26 | «J'ai décidé d'être admirable en tout, pour tout !» | Edmond Rostand, *Cyrano de Bergerac*, 1897 | «¡He decidido ser admirable en todo, para todo!» | [Cyrano de Bergerac (Rostand)](https://fr.wikipedia.org/wiki/Cyrano_de_Bergerac_(Rostand)) | Comprobado (cita a Trousson, 1997, p. 365). El acto **[sin verificar]** |
| 27 | «Cyrano de Bergerac, qui fut tout et qui ne fut rien» | Ídem, acto V, escena 6, v. 2541–2542 | «Cyrano de Bergerac, que lo fue todo y no fue nada» | Ídem | Comprobado |
| 28 | «Quand même» | Sarah Bernhardt, su divisa | «A pesar de todo» | [Sarah Bernhardt](https://fr.wikipedia.org/wiki/Sarah_Bernhardt) | Comprobado |
| 29 | «Tout le monde m'avait donné *des* conseils. Personne ne m'avait donné *un* conseil.» | Bernhardt, *Ma double vie*, 1907, p. 82 | «Todo el mundo me había dado consejos. Nadie me había dado *un* consejo» | Ídem | Comprobado |
| 30 | «Là, tout n'est qu'ordre et beauté, / Luxe, calme et volupté.» | Charles Baudelaire, «L'Invitation au voyage», *Les Fleurs du mal*, 1857 | «Allí, todo es orden y belleza, / lujo, calma y voluptuosidad» | [L'Invitation au voyage](https://fr.wikipedia.org/wiki/L'Invitation_au_voyage) | Comprobado (la página copia el poema). Anterior al periodo |
| 31 | «Les plus rares fleurs / Mêlant leurs odeurs / Aux vagues senteurs de l'ambre» | Ídem | «Las flores más raras, / mezclando sus olores / con los vagos aromas del ámbar» | Ídem | Comprobado. Anterior al periodo |

**31 frases.** Las 29 primeras son de autores activos entre 1880 y 1910 y se publicaron en ese periodo; de cinco no se pudo comprobar el año (1, 2, 12, 22 y 24). La 11 y las 13 a 15 se escribieron antes (en 1865 y 1874) y se publicaron en 1882–1887. Las dos de Baudelaire (30 y 31) son de 1857, anteriores, como admitía el encargo.

**No se pudieron comprobar** frases en francés de Mucha (lo que cita Wikipedia de sus memorias está en inglés), de Colette (en la página no hay ninguna de 1900–1910 que sirva), ni de «Correspondances» de Baudelaire en francés (la página no abrió; sí está en español, sección 12) **[sin verificar]**.

## 12. Traducciones y textos de dominio público

| Texto | Traducción o edición | Datos comprobados | Página | Uso |
|---|---|---|---|---|
| Waterman, *Flora's Lexicon* (1840) | Original en inglés | Wikisource lo marca «PD/US»; publicado en 1840. Las fechas de la autora **[sin verificar]** | [Flora's Lexicon](https://en.wikisource.org/wiki/Flora%27s_Lexicon) | La lista de la sección 3 |
| Sigourney, *The Voice of Flowers* (1846) | Original en inglés, con un glosario de flores (pp. 126–130 del facsímil) | Transcrito en Wikisource; **no se leyó** | [Voice of Flowers/Glossary](https://en.wikisource.org/wiki/Voice_of_Flowers/Glossary) | Segunda fuente para cotejar la lista |
| *Floral Poetry and the Language of Flowers* (Londres, Marcus Ward, 1877) | Original en inglés | **Casi sin transcribir** en Wikisource: solo hay unas 35 páginas sueltas | [Index](https://en.wikisource.org/wiki/Index:Floral_poetry_and_the_language_of_flowers_(IA_cu31924068941578).pdf) | No sirve todavía |
| Baudelaire, *Las flores del mal* | **Eduardo Marquina, 1905** (en español) | Marquina: Barcelona, 1879 – Nueva York, 1946. Con la regla de los 70 años, de dominio público desde 2017 **[Aproximación]** | [Las flores del mal](https://es.wikisource.org/wiki/Las_flores_del_mal), [Autor:Eduardo Marquina](https://es.wikisource.org/wiki/Autor:Eduardo_Marquina) | Se pueden citar en español. «Correspondencias»: «La Natura es un templo donde vividos pilares / Dejan, a veces, brotar confusas palabras […] Los perfumes, los colores y los sonidos se responden» ([Correspondencias](https://es.wikisource.org/wiki/Correspondencias_(Charles_Baudelaire))). «vividos» va así en la transcripción |
| Zola, *¡Yo acuso…!* y otros textos | **Cristóbal Litrán**, en *Crítica social* (Barcelona, Escuela Moderna, 1916) | La fecha de muerte de Litrán **[sin verificar]**; sin ella no se sabe si es de dominio público | [J'accuse…!](https://es.wikisource.org/wiki/J’accuse…!), [Autor:Cristóbal Litrán](https://es.wikisource.org/wiki/Autor:Cristóbal_Litrán) | Mejor traducir desde el francés |
| Zola, *¡Yo acuso…!* | Traducción de los propios voluntarios de Wikisource | Licencia libre (CC BY-SA), **no** de dominio público | [J'accuse…!](https://es.wikisource.org/wiki/J’accuse…!) | Solo citando la licencia |
| Verlaine, *Los poetas malditos* | En español en Wikisource | El traductor **[sin verificar]** | [Los poetas malditos](https://es.wikisource.org/wiki/Los_poetas_malditos) | — |
| Verlaine, «Llora en mi corazón…» | De un usuario de Wikisource | Licencia libre, no de dominio público | [Llora en mi corazón...](https://es.wikisource.org/wiki/Llora_en_mi_corazón...) | Solo citando la licencia |
| Los originales franceses de la sección 11 | — | Todos sus autores murieron antes de 1955 (Baudelaire 1867, Verlaine 1896, Mallarmé 1898, Zola 1902, Gallé 1904, Huysmans 1907, Renard 1910, Rodin 1917, Rostand 1918, Proust 1922, Bernhardt 1923, Guimard 1942) | Las páginas de la sección 11 | De dominio público. Wikipedia copia enteros «Art poétique», «Chanson d'automne» (1866) y «L'Invitation au voyage» |
| Carteles de Mucha, Chéret y otros | — | Mucha murió en 1939; Chéret (París, 1836 – Niza, 23 de septiembre de 1932) | [Jules Chéret](https://fr.wikipedia.org/wiki/Jules_Chéret) | Las imágenes de los carteles, de dominio público; las fotografías modernas de ellos, según su licencia |
| *Le Langage des fleurs*, de La Tour | Edición francesa de 1819 y siguientes | De dominio público. No se pudo abrir ninguna copia | Sección 2 | Cuando se pueda, cotejar la lista |

**En español no se encontró** ninguna traducción de dominio público de La Tour, ni de Rostand, Proust, Mallarmé o Renard **[sin verificar]**: las frases se traducen aquí desde el francés, que es lo que manda 10a.

## 13. Lo que queda sin verificar

- **El texto francés de *Le Langage des fleurs***, para cotejar los significados de la sección 3 (hace falta abrir `fr.wikisource.org`, `gallica.bnf.fr` o `archive.org`). Hasta entonces, solo coinciden comprobadamente perce-neige y jonquille.
- **Quién escribió *Le Langage des fleurs***: Louise Cortambert (Wikidata, BnF, Wikipedia en inglés) o Louis-Aimé Martin (Wikipedia en francés, *Perce-neige*).
- **El texto literal** de las leyes de 1891, 1911 y 1900, y la hora exacta en que se cambió la hora en marzo de 1911.
- **Si la hora de los trenes** siguió 5 minutos atrasada hasta 1911.
- **Cuántas estaciones de la línea 1 abrieron el 19 de julio de 1900**: 8 según la página de la línea, 10 si se cree a las de Marbeuf y Tuileries.
- **El día del ramal de Porte Dauphine** (3 o 13 de diciembre de 1900).
- **El horario del metro** en 1900.
- **El color de los faroles** de Guimard en 1900.
- **La jornada real de un taller de litografía** en 1900, si empleaba mujeres aprendices y cuánto ganaban.
- **Si la ley de 1892** daba un día de descanso a mujeres y menores, y su fecha.
- **Si seguían abiertos en 1900** La Cigale, Le Mirliton y Au Lapin agile; quién llevaba el Lapin agile.
- **Los nombres de mujer más comunes en París hacia 1880** (fichero del INSEE, y además empieza en 1900).
- **Las fechas** del lema de Gallé, de los cuadernos de Rodin, de la carta de Mallarmé, de la entrada de Renard y de la crítica de Huysmans; el acto de la frase de Cyrano.
- **Que Lalique prestara la mujer libélula a Sarah Bernhardt.**
- **La fecha del bordado *Cyclamen*** de Obrist (1892, 1894 o 1895).
- **La fecha de *The Peacock Skirt*** de Beardsley.
- **Si el turquesa** de los invernaderos de Auteuil es el color original.
- **La muerte de Cristóbal Litrán** (para saber si su Zola es de dominio público) y el traductor de *Los poetas malditos*.
- **Frases en francés de Mucha y de Colette** y el «Correspondances» de Baudelaire en francés.
