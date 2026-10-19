package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.FraseGuardada
import io.github.ruddy2003.vestigium.data.Repo

/**
 * Una frase por día para cada tema. Todas son citas reales, con su autor y su fuente: ninguna está
 * inventada para la app. Cuando la atribución es dudosa o tradicional lo dice («Atribuida a…»).
 * La traducción al español es propia salvo donde se indica otra versión. Casi todas vienen de textos
 * de dominio público; las modernas son frases breves citadas con su autor.
 */
object Frases {
    /** [original] en su lengua, si no es español; [texto] en español; [autor] con la fuente. */
    data class Frase(val original: String?, val texto: String, val autor: String)

    private fun f(original: String?, texto: String, autor: String) = Frase(original, texto, autor)

    val ROMANO = listOf(
        f("Nulla dies sine linea.", "Ningún día sin una línea.", "Atribuida al pintor Apeles, a partir de Plinio el Viejo"),
        f("Omnia, Lucili, aliena sunt, tempus tantum nostrum est.", "Todo, Lucilio, nos es ajeno; solo el tiempo es nuestro.", "Séneca, Cartas a Lucilio 1"),
        f("Vindica te tibi.", "Reclámate para ti mismo.", "Séneca, Cartas a Lucilio 1"),
        f("Non exiguum temporis habemus, sed multum perdidimus.", "No es que tengamos poco tiempo, es que perdemos mucho.", "Séneca, Sobre la brevedad de la vida 1"),
        f("Homines dum docent discunt.", "Mientras enseñamos, aprendemos.", "Séneca, Cartas a Lucilio 7"),
        f("Otium sine litteris mors est.", "El ocio sin letras es una muerte.", "Séneca, Cartas a Lucilio 82"),
        f("Si vis amari, ama.", "Si quieres ser amado, ama.", "Séneca, Cartas a Lucilio 9, citando a Hecatón"),
        f("Non est ad astra mollis e terris via.", "No hay camino suave de la tierra a las estrellas.", "Séneca, Hércules furioso"),
        f("Dum loquimur, fugerit invida aetas: carpe diem, quam minimum credula postero.", "Mientras hablamos, habrá huido el tiempo envidioso: aprovecha el día y fíate lo menos posible del mañana.", "Horacio, Odas I, 11"),
        f("Sapere aude.", "Atrévete a saber.", "Horacio, Epístolas I, 2"),
        f("Dimidium facti qui coepit habet.", "Quien empezó ya tiene hecha la mitad.", "Horacio, Epístolas I, 2"),
        f("Aequam memento rebus in arduis servare mentem.", "Acuérdate de conservar la mente serena en los momentos difíciles.", "Horacio, Odas II, 3"),
        f("Est modus in rebus.", "Hay una medida en las cosas.", "Horacio, Sátiras I, 1"),
        f("Nil sine magno vita labore dedit mortalibus.", "La vida no dio nada a los mortales sin gran esfuerzo.", "Horacio, Sátiras I, 9"),
        f("Quid dicam de thesauro rerum omnium, memoria?", "¿Qué decir de la memoria, tesoro de todas las cosas?", "Cicerón, Sobre el orador I, 18"),
        f("Historia vero testis temporum, lux veritatis, vita memoriae, magistra vitae.", "La historia: testigo de los tiempos, luz de la verdad, vida de la memoria, maestra de la vida.", "Cicerón, Sobre el orador II, 36"),
        f("Sed fugit interea, fugit irreparabile tempus.", "Pero huye entretanto, huye el tiempo irreparable.", "Virgilio, Geórgicas III"),
        f("Forsan et haec olim meminisse iuvabit.", "Quizá algún día también nos guste recordar esto.", "Virgilio, Eneida I"),
        f("Labor omnia vicit improbus.", "El trabajo tenaz lo venció todo.", "Virgilio, Geórgicas I"),
        f("Possunt, quia posse videntur.", "Pueden, porque creen que pueden.", "Virgilio, Eneida V"),
        f("Felix qui potuit rerum cognoscere causas.", "Feliz quien pudo conocer las causas de las cosas.", "Virgilio, Geórgicas II"),
        f("Gutta cavat lapidem.", "La gota horada la piedra.", "Ovidio, Cartas desde el Ponto IV"),
        f("Omnia mutantur, nihil interit.", "Todo cambia, nada perece.", "Ovidio, Metamorfosis XV"),
        f("Homo sum, humani nihil a me alienum puto.", "Soy humano: nada humano me es ajeno.", "Terencio, El enemigo de sí mismo"),
        f("Orandum est ut sit mens sana in corpore sano.", "Hay que pedir una mente sana en un cuerpo sano.", "Juvenal, Sátira X"),
        f("Sera nimis vita est crastina: vive hodie.", "La vida de mañana llega demasiado tarde: vive hoy.", "Marcial, Epigramas I, 15"),
        f("Non est vivere, sed valere vita est.", "La vida no es estar vivo, sino estar bien.", "Marcial, Epigramas VI, 70"),
        f("Quod sis esse velis, nihilque malis.", "Quiere ser lo que eres y no prefieras nada más.", "Marcial, Epigramas X, 47"),
        f("Festina lente.", "Apresúrate despacio.", "Lema de Augusto, según Suetonio"),
        f("Respice post te, hominem te esse memento.", "Mira detrás de ti: recuerda que eres un hombre.", "Tertuliano, Apologético 33, sobre el triunfo romano"),
    )

    val CIBER = listOf(
        f("Calculemus!", "¡Calculemos!", "Gottfried Leibniz"),
        f("The Analytical Engine weaves algebraical patterns just as the Jacquard-loom weaves flowers and leaves.",
            "La Máquina Analítica teje patrones algebraicos igual que el telar de Jacquard teje flores y hojas.", "Ada Lovelace, Notas, 1843"),
        f("The Analytical Engine has no pretensions whatever to originate anything. It can do whatever we know how to order it to perform.",
            "La Máquina Analítica no pretende originar nada: hace lo que sepamos ordenarle.", "Ada Lovelace, Notas, 1843"),
        f(null, "«Señor Babbage, si mete números equivocados en la máquina, ¿saldrán las respuestas correctas?». No alcanzo a entender la confusión de ideas que puede provocar semejante pregunta.",
            "Charles Babbage, Pasajes de la vida de un filósofo, 1864"),
        f("A memex is a device in which an individual stores all his books, records, and communications, and which is mechanized so that it may be consulted with exceeding speed and flexibility.",
            "Un memex es un aparato donde una persona guarda todos sus libros, registros y comunicaciones, mecanizado para consultarlo con enorme rapidez y flexibilidad.", "Vannevar Bush, «As We May Think», 1945"),
        f("We can only see a short distance ahead, but we can see plenty there that needs to be done.", "Solo podemos ver un poco hacia adelante, pero alcanzamos a ver mucho que hay que hacer.", "Alan Turing, «Computing Machinery and Intelligence», 1950"),
        f("Machines take me by surprise with great frequency.", "Las máquinas me sorprenden con mucha frecuencia.", "Alan Turing, «Computing Machinery and Intelligence», 1950"),
        f("We are not stuff that abides, but patterns that perpetuate themselves.", "No somos materia que permanece, sino patrones que se perpetúan.", "Norbert Wiener, Cibernética y sociedad, 1950"),
        f("Program testing can be used to show the presence of bugs, but never to show their absence!", "Las pruebas pueden mostrar que un programa tiene errores, pero nunca que no los tiene.", "Edsger W. Dijkstra, 1970"),
        f("Simplicity is prerequisite for reliability.", "La sencillez es condición de la fiabilidad.", "Edsger W. Dijkstra, 1975"),
        f("Premature optimization is the root of all evil.", "La optimización prematura es la raíz de todos los males.", "Donald Knuth, 1974"),
        f("Adding manpower to a late software project makes it later.", "Añadir gente a un proyecto de software retrasado lo retrasa más.", "Fred Brooks, El mítico hombre-mes, 1975"),
        f("When in doubt, use brute force.", "En caso de duda, usa la fuerza bruta.", "Ken Thompson, coautor de Unix"),
        f("The best way to predict the future is to invent it.", "La mejor manera de predecir el futuro es inventarlo.", "Alan Kay, 1971"),
        f("Any sufficiently advanced technology is indistinguishable from magic.", "Cualquier tecnología lo bastante avanzada es indistinguible de la magia.", "Arthur C. Clarke, Perfiles del futuro, 1973"),
        f("Reality is that which, when you stop believing in it, doesn't go away.", "La realidad es aquello que no desaparece cuando dejas de creer en ello.", "Philip K. Dick, 1978"),
        f("It's easier to ask forgiveness than it is to get permission.", "Es más fácil pedir perdón que conseguir permiso.", "Grace Hopper, 1986"),
        f("The future is already here — it's just not very evenly distributed.", "El futuro ya está aquí; solo que no está muy bien repartido.", "Atribuida a William Gibson"),
        f("Talk is cheap. Show me the code.", "Hablar es barato. Enséñame el código.", "Linus Torvalds, lista del kernel de Linux, 2000"),
        f("This is for everyone.", "Esto es para todos.", "Tim Berners-Lee, inauguración de Londres 2012"),
    )

    val HELENO = listOf(
        f("ὁ δὲ ἀνεξέταστος βίος οὐ βιωτὸς ἀνθρώπῳ", "Una vida sin examen no merece ser vivida.", "Sócrates, en la Apología de Platón"),
        f("γνῶθι σεαυτόν", "Conócete a ti mismo.", "Inscripción del templo de Delfos"),
        f("μηδὲν ἄγαν", "Nada en exceso.", "Inscripción del templo de Delfos"),
        f("ἦθος ἀνθρώπῳ δαίμων", "El carácter de cada quien es su destino.", "Heráclito, fragmento 119"),
        f("ποταμῷ γὰρ οὐκ ἔστιν ἐμβῆναι δὶς τῷ αὐτῷ", "No es posible entrar dos veces en el mismo río.", "Heráclito, fragmento 91"),
        f("φύσις κρύπτεσθαι φιλεῖ", "A la naturaleza le gusta ocultarse.", "Heráclito, fragmento 123"),
        f("ἐδιζησάμην ἐμεωυτόν", "Me he buscado a mí mismo.", "Heráclito, fragmento 101"),
        f("μία γὰρ χελιδὼν ἔαρ οὐ ποιεῖ, οὐδὲ μία ἡμέρα", "Una golondrina no hace la primavera, ni tampoco un solo día.", "Aristóteles, Ética a Nicómaco I"),
        f("τὰ μὲν δίκαια πράττοντες δίκαιοι γινόμεθα", "Nos volvemos justos haciendo cosas justas: lo que repetimos nos forma.", "Aristóteles, Ética a Nicómaco II"),
        f("πάντες ἄνθρωποι τοῦ εἰδέναι ὀρέγονται φύσει", "Todos los seres humanos desean por naturaleza saber.", "Aristóteles, Metafísica I"),
        f("ἄνθρωπος φύσει πολιτικὸν ζῷον", "El ser humano es por naturaleza un animal político.", "Aristóteles, Política I"),
        f("ὁ βίος βραχύς, ἡ δὲ τέχνη μακρή", "La vida es breve; el arte, largo.", "Hipócrates, Aforismos"),
        f("γένοι᾽ οἷος ἐσσὶ μαθών", "Llega a ser el que eres, una vez que lo hayas aprendido.", "Píndaro, Pítica II"),
        f("ταράσσει τοὺς ἀνθρώπους οὐ τὰ πράγματα, ἀλλὰ τὰ περὶ τῶν πραγμάτων δόγματα", "No nos perturban las cosas, sino lo que opinamos de ellas.", "Epicteto, Enquiridión 5"),
        f("τῶν ὄντων τὰ μέν ἐστιν ἐφ᾽ ἡμῖν, τὰ δὲ οὐκ ἐφ᾽ ἡμῖν", "De las cosas, unas dependen de nosotros y otras no.", "Epicteto, Enquiridión 1"),
        f("ὁ κόσμος ἀλλοίωσις, ὁ βίος ὑπόληψις", "El mundo es cambio; la vida, opinión.", "Marco Aurelio, Meditaciones IV"),
        f("μὴ ὡς μύρια μέλλων ἔτη ζῆν", "No vivas como si te quedaran diez mil años.", "Marco Aurelio, Meditaciones IV"),
        f("κτῆμά τε ἐς αἰεὶ", "Lo escribí como una adquisición para siempre.", "Tucídides, Historia I"),
        f("πάντων χρημάτων μέτρον ἐστὶν ἄνθρωπος", "El ser humano es la medida de todas las cosas.", "Protágoras"),
        f("χαλεπὸν τὸ ἑαυτὸν γνῶναι", "Lo más difícil es conocerse a uno mismo.", "Tales de Mileto, según Diógenes Laercio"),
        f("εἰ μὴ Ἀλέξανδρος ἤμην, Διογένης ἂν ἤμην", "Si no fuera Alejandro, sería Diógenes.", "Alejandro Magno, según Plutarco"),
        f("μικρὸν ἀπὸ τοῦ ἡλίου μετάστηθι", "Apártate un poco, que me tapas el sol.", "Diógenes a Alejandro, según Plutarco"),
        f("τὰς ἐλπίδας", "«La esperanza»: lo que Alejandro guardó para sí al repartir sus bienes antes de partir a Asia.", "Plutarco, Vida de Alejandro 15"),
        f("τῷ κρατίστῳ", "«Al más fuerte»: la respuesta de Alejandro, ya moribundo, cuando le preguntaron a quién dejaba su reino.", "Arriano, Anábasis VII"),
        f("οὔτε γὰρ ἱστορίας γράφομεν, ἀλλὰ βίους", "No escribimos historias, sino vidas.", "Plutarco, Vida de Alejandro 1"),
        f(null, "Un hecho pequeño, una frase o una broma revelan mejor el carácter que batallas con miles de muertos.", "Plutarco, Vida de Alejandro 1"),
        f("ἄνθρωπος μικρὸς κόσμος", "El ser humano es un pequeño cosmos.", "Demócrito"),
        f("πολλὰ τὰ δεινὰ κοὐδὲν ἀνθρώπου δεινότερον πέλει", "Muchas cosas son asombrosas, pero ninguna más que el ser humano.", "Sófocles, Antígona"),
        f("οἵη περ φύλλων γενεὴ τοίη δὲ καὶ ἀνδρῶν", "Como la generación de las hojas, así la de los seres humanos.", "Homero, Ilíada VI"),
        f("ἄνδρα μοι ἔννεπε, Μοῦσα, πολύτροπον", "Háblame, Musa, del hombre de muchos caminos.", "Homero, Odisea I"),
        f("κλέος ἄφθιτον", "Gloria imperecedera: lo que eligió Aquiles, el héroe que Alejandro quiso imitar.", "Homero, Ilíada IX"),
        f("τῆς δ᾽ ἀρετῆς ἱδρῶτα θεοὶ προπάροιθεν ἔθηκαν", "Delante de la excelencia, los dioses pusieron el sudor.", "Hesíodo, Trabajos y días"),
        f("καιρὸν γνῶθι", "Conoce el momento oportuno.", "Pítaco de Mitilene"),
        f("ἀρχὴ παντὸς ἔργου μέγιστον", "El comienzo es lo más importante de toda obra.", "Platón, República II"),
        f("πόλλ᾽ οἶδ᾽ ἀλώπηξ, ἀλλ᾽ ἐχῖνος ἓν μέγα", "La zorra sabe muchas cosas; el erizo, una sola y grande.", "Arquíloco"),
        f("μέγα βιβλίον μέγα κακόν", "Libro grande, gran mal.", "Calímaco"),
        f("ὁ θάνατος οὐδὲν πρὸς ἡμᾶς", "La muerte no es nada para nosotros.", "Epicuro, Máximas capitales II"),
        f("χαλεπὸν ἐσθλὸν ἔμμεναι", "Es difícil ser bueno.", "Pítaco, citado por Simónides"),
    )

    val MEDIEVAL = listOf(
        f("Tria digita scribunt, totum corpus laborat.", "Tres dedos escriben, pero trabaja todo el cuerpo.", "Nota de un copista en el margen de un códice"),
        f("Explicit, expliceat, ludere scriptor eat.", "Se acabó; que se acabe; que el copista se vaya a jugar.", "Colofón de copista"),
        f("Qui scribit, bis legit.", "Quien escribe, lee dos veces.", "Proverbio medieval"),
        f("Verba volant, scripta manent.", "Las palabras vuelan; lo escrito permanece.", "Proverbio medieval"),
        f("Litterae sunt indices rerum, signa verborum, quibus tanta vis est, ut nobis dicta absentium sine voce loquantur.",
            "Las letras son indicios de las cosas y signos de las palabras; tienen tanta fuerza que nos dicen sin voz lo que dijeron los ausentes.", "Isidoro de Sevilla, Etimologías I, 3"),
        f("Sic stude quasi semper victurus; sic vive quasi cras moriturus.", "Estudia como si fueras a vivir siempre; vive como si fueras a morir mañana.", "Atribuida a Isidoro de Sevilla"),
        f("Noli foras ire, in te ipsum redi; in interiore homine habitat veritas.", "No salgas fuera; vuelve a ti mismo: en el interior del ser humano habita la verdad.", "San Agustín, De la verdadera religión 39"),
        f("Quid est ergo tempus? Si nemo ex me quaerat, scio; si quaerenti explicare velim, nescio.",
            "¿Qué es, pues, el tiempo? Si nadie me lo pregunta, lo sé; si quiero explicárselo a quien me lo pregunta, no lo sé.", "San Agustín, Confesiones XI, 14"),
        f("Praesens de praeteritis memoria, praesens de praesentibus contuitus, praesens de futuris exspectatio.",
            "El presente de lo pasado es la memoria; el de lo presente, la mirada; el de lo futuro, la espera.", "San Agustín, Confesiones XI, 20"),
        f("Factus eram ipse mihi magna quaestio.", "Me había vuelto para mí mismo una gran pregunta.", "San Agustín, Confesiones IV, 4"),
        f("Ora et labora.", "Reza y trabaja.", "Lema benedictino"),
        f("Otiositas inimica est animae.", "La ociosidad es enemiga del alma.", "Regla de san Benito 48"),
        f("Nanos gigantium humeris insidentes.", "Somos como enanos sentados en hombros de gigantes.", "Bernardo de Chartres, según Juan de Salisbury"),
        f("Omnia disce, videbis postea nihil esse superfluum.", "Apréndelo todo; después verás que nada sobra.", "Hugo de San Víctor, Didascalicon VI, 3"),
        f("Dubitando ad inquisitionem venimus; inquirendo veritatem percipimus.", "Dudando llegamos a investigar; investigando alcanzamos la verdad.", "Pedro Abelardo, Sic et non"),
        f("Pluralitas non est ponenda sine necessitate.", "No hay que multiplicar las cosas sin necesidad.", "Guillermo de Ockham"),
        f("Sine experientia nihil sufficienter sciri potest.", "Sin experiencia nada puede saberse lo suficiente.", "Roger Bacon, Opus maius VI"),
        f("Nihil est miserum nisi cum putes.", "Nada es desdichado sino cuando así lo piensas.", "Boecio, Consolación de la filosofía II, 4"),
        f("Numquam sis ex toto otiosus, sed aut legens, aut scribens, aut orans, aut meditans.", "Nunca estés del todo ocioso: lee, escribe, reza o medita.", "Tomás de Kempis, Imitación de Cristo I, 19"),
        f("O quam cito transit gloria mundi!", "¡Qué pronto pasa la gloria del mundo!", "Tomás de Kempis, Imitación de Cristo I, 3"),
        f("Memento mori.", "Recuerda que morirás.", "Fórmula monástica"),
        f(null, "Recuerde el alma dormida, / avive el seso y despierte / contemplando / cómo se pasa la vida, / cómo se viene la muerte / tan callando.", "Jorge Manrique, Coplas por la muerte de su padre"),
        f(null, "Nuestras vidas son los ríos / que van a dar en la mar, / que es el morir.", "Jorge Manrique, Coplas por la muerte de su padre"),
        f("Nel mezzo del cammin di nostra vita / mi ritrovai per una selva oscura.", "A mitad del camino de nuestra vida me encontré en una selva oscura.", "Dante, Infierno I"),
        f("Fatti non foste a viver come bruti, / ma per seguir virtute e canoscenza.", "No fueron hechos para vivir como bestias, sino para seguir la virtud y el saber.", "Dante, Infierno XXVI"),
        f(null, "La vida es como un gorrión que en invierno cruza volando la sala del banquete: entra por una puerta, siente un instante el calor y sale por la otra.",
            "Beda el Venerable, Historia eclesiástica II, 13"),
    )

    val DISTOPIA = listOf(
        f("Quis custodiet ipsos custodes?", "¿Quién vigilará a los vigilantes?", "Juvenal, Sátira VI"),
        f("Duas tantum res anxius optat, panem et circenses.", "El pueblo ya solo ansía dos cosas: pan y juegos de circo.", "Juvenal, Sátira X"),
        f("Ubi solitudinem faciunt, pacem appellant.", "Donde crean un desierto, lo llaman paz.", "Tácito, Agrícola 30"),
        f("Corruptissima re publica plurimae leges.", "Cuanto más corrupto el Estado, más numerosas las leyes.", "Tácito, Anales III, 27"),
        f("Finis ergo reipublicae revera libertas est.", "El verdadero fin del Estado es, pues, la libertad.", "Spinoza, Tratado teológico-político XX, 1670"),
        f("Il n'y a point de plus cruelle tyrannie que celle que l'on exerce à l'ombre des lois et avec les couleurs de la justice.",
            "No hay tiranía más cruel que la que se ejerce a la sombra de las leyes y con los colores de la justicia.", "Montesquieu, Consideraciones sobre los romanos XIV, 1734"),
        f("L'homme est né libre, et partout il est dans les fers.", "El hombre nació libre, y en todas partes está encadenado.", "Rousseau, El contrato social I, 1762"),
        f("Habe Muth, dich deines eigenen Verstandes zu bedienen!", "¡Ten el valor de servirte de tu propio entendimiento!", "Immanuel Kant, ¿Qué es la Ilustración?, 1784"),
        f("A new mode of obtaining power of mind over mind, in a quantity hitherto without example.",
            "Un nuevo modo de obtener poder de la mente sobre la mente, en una cantidad sin precedentes.", "Jeremy Bentham, El panóptico, 1791"),
        f("Those who would give up essential Liberty, to purchase a little temporary Safety, deserve neither Liberty nor Safety.",
            "Quienes renuncian a la libertad esencial para obtener un poco de seguridad pasajera no merecen ni libertad ni seguridad.", "Benjamin Franklin, 1755"),
        f("I have sworn upon the altar of God eternal hostility against every form of tyranny over the mind of man.",
            "He jurado sobre el altar de Dios hostilidad eterna contra toda forma de tiranía sobre la mente humana.", "Thomas Jefferson, carta a Benjamin Rush, 1800"),
        f("Soyez résolus de ne servir plus, et vous voilà libres.", "Decídanse a no servir más, y serán libres.", "Étienne de La Boétie, Discurso de la servidumbre voluntaria"),
        f("Il ne brise pas les volontés, mais il les amollit, les plie et les dirige.", "No quiebra las voluntades: las ablanda, las doblega y las dirige.", "Alexis de Tocqueville, La democracia en América II, 1840"),
        f("Over himself, over his own body and mind, the individual is sovereign.", "Sobre sí mismo, sobre su propio cuerpo y su propia mente, el individuo es soberano.", "John Stuart Mill, Sobre la libertad, 1859"),
        f("Power tends to corrupt, and absolute power corrupts absolutely.", "El poder tiende a corromper, y el poder absoluto corrompe absolutamente.", "Lord Acton, carta a Mandell Creighton, 1887"),
        f("The right to be let alone.", "El derecho a que lo dejen a uno en paz.", "Samuel Warren y Louis Brandeis, «The Right to Privacy», 1890"),
        f("Sunlight is said to be the best of disinfectants.", "Se dice que la luz del sol es el mejor de los desinfectantes.", "Louis Brandeis, 1914"),
        f(null, "Les juro, señores, que tener demasiada conciencia es una enfermedad, una verdadera enfermedad.", "Fiódor Dostoievski, Memorias del subsuelo, 1864"),
        f("Ein Käfig ging einen Vogel suchen.", "Una jaula salió a buscar un pájaro.", "Franz Kafka, aforismos de Zürau, 1918"),
        f(null, "Alguien debió de calumniar a Josef K., porque sin haber hecho nada malo fue detenido una mañana.", "Franz Kafka, El proceso, 1925"),
        f("La visibilité est un piège.", "La visibilidad es una trampa.", "Michel Foucault, Vigilar y castigar, 1975"),
    )

    val REFUGIO = listOf(
        f("Keep calm and carry on.", "Mantén la calma y sigue adelante.", "Cartel del Ministerio de Información británico, 1939"),
        f("The only thing we have to fear is fear itself.", "A lo único que debemos temer es al miedo mismo.", "Franklin D. Roosevelt, 1933"),
        f("The unleashed power of the atom has changed everything save our modes of thinking.", "El poder desatado del átomo lo ha cambiado todo, salvo nuestra manera de pensar.", "Albert Einstein, telegrama de 1946"),
        f("Duck and cover!", "¡Agáchate y cúbrete!", "Película de defensa civil de EE. UU., 1951"),
        f("Alert today, alive tomorrow.", "Alerta hoy, vivo mañana.", "Consigna de la Defensa Civil de EE. UU., años cincuenta"),
        f("Every gun that is made, every warship launched, every rocket fired signifies, in the final sense, a theft from those who hunger and are not fed.",
            "Cada arma que se fabrica, cada barco de guerra que se bota, cada cohete que se dispara es, en último término, un robo a quienes tienen hambre y no son alimentados.", "Dwight D. Eisenhower, «La oportunidad de la paz», 1953"),
        f(null, "Que la milagrosa inventiva del hombre no se dedique a su muerte, sino que se consagre a su vida.", "Dwight D. Eisenhower, Átomos para la paz, 1953"),
        f("Plans are worthless, but planning is everything.", "Los planes no valen nada; planear lo es todo.", "Dwight D. Eisenhower, 1957"),
        f("We will not walk in fear, one of another.", "No caminaremos con miedo unos de otros.", "Edward R. Murrow, See It Now, 1954"),
        f("The buck stops here!", "La responsabilidad termina aquí.", "Letrero del escritorio de Harry S. Truman"),
        f("Mankind must put an end to war before war puts an end to mankind.", "La humanidad debe acabar con la guerra antes de que la guerra acabe con la humanidad.", "John F. Kennedy, ante la ONU, 1961"),
        f("We all breathe the same air. We all cherish our children's future. And we are all mortal.",
            "Todos respiramos el mismo aire. Todos queremos el futuro de nuestros hijos. Y todos somos mortales.", "John F. Kennedy, Universidad Americana, 1963"),
        f("There will come soft rains and the smell of the ground.", "Vendrán lluvias suaves y el olor de la tierra.", "Sara Teasdale, 1918"),
        f(null, "Al hombre se le puede arrebatar todo salvo una cosa: la última de las libertades humanas, la elección de su actitud ante cualquier circunstancia.",
            "Viktor Frankl, El hombre en busca de sentido, 1946"),
    )

    val ORDEN = listOf(
        f("Igitur qui desiderat pacem, praeparet bellum.", "Quien desea la paz, que prepare la guerra.", "Vegecio, Compendio de técnica militar III"),
        f("Paucos viros fortes natura procreat, bona institutione plures reddit industria.", "La naturaleza da pocos valientes; la buena instrucción y la constancia hacen muchos más.", "Vegecio, Compendio de técnica militar III"),
        f("知彼知己，百戰不殆", "Conoce al enemigo y conócete a ti mismo, y en cien batallas no correrás peligro.", "Sun Tzu, El arte de la guerra III"),
        f("勝兵先勝而後求戰", "El ejército victorioso primero vence y después busca la batalla.", "Sun Tzu, El arte de la guerra IV"),
        f("今日は昨日の我に勝ち", "Hoy, vence a tu yo de ayer.", "Miyamoto Musashi, El libro de los cinco anillos"),
        f("千日の稽古を鍛とし、万日の稽古を錬とす", "Mil días de práctica para forjar, diez mil para pulir.", "Miyamoto Musashi, El libro de los cinco anillos"),
        f(null, "¡Dios, qué buen vasallo, si oviesse buen señor!", "Cantar de mio Cid, verso 20"),
        f("Hic iacet Arthurus, rex quondam rexque futurus.", "Aquí yace Arturo, rey que fue y rey que será.", "Thomas Malory, La muerte de Arturo, 1485"),
        f(null, "Tomando ora la espada, ora la pluma.", "Garcilaso de la Vega, Égloga III"),
        f(null, "Sábete, Sancho, que no es un hombre más que otro si no hace más que otro.", "Miguel de Cervantes, Don Quijote I, 18"),
        f(null, "La pluma es lengua del alma.", "Miguel de Cervantes, Don Quijote II, 16"),
        f(null, "El que lee mucho y anda mucho, ve mucho y sabe mucho.", "Miguel de Cervantes, Don Quijote II, 25"),
        f(null, "Cada uno es artífice de su ventura.", "Miguel de Cervantes, Don Quijote II, 66"),
    )

    val LABORATORIO = listOf(
        f("When you can measure what you are speaking about, and express it in numbers, you know something about it.",
            "Cuando puedes medir aquello de lo que hablas y expresarlo en números, sabes algo de ello.", "Lord Kelvin, 1883"),
        f(null, "Mide lo que se pueda medir, y haz medible lo que no lo sea.", "Atribuida a Galileo Galilei (no aparece en sus obras)"),
        f("La filosofia è scritta in questo grandissimo libro che continuamente ci sta aperto innanzi a gli occhi (io dico l'universo).",
            "La filosofía está escrita en este grandísimo libro que tenemos siempre abierto ante los ojos: el universo.", "Galileo Galilei, El ensayador, 1623"),
        f("Dans les champs de l'observation, le hasard ne favorise que les esprits préparés.", "En el campo de la observación, el azar solo favorece a las mentes preparadas.", "Louis Pasteur, 1854"),
        f("L'art, c'est moi; la science, c'est nous.", "El arte soy yo; la ciencia somos nosotros.", "Claude Bernard"),
        f("C'est ce que nous pensons déjà connaître qui nous empêche souvent d'apprendre.", "Lo que creemos saber ya es, a menudo, lo que nos impide aprender.", "Atribuida a Claude Bernard"),
        f("If I have seen further it is by standing on the shoulders of Giants.", "Si he visto más lejos es porque estoy subido en hombros de gigantes.", "Isaac Newton, carta a Robert Hooke, 1675"),
        f("Ipsa scientia potestas est.", "El conocimiento mismo es poder.", "Francis Bacon, Meditaciones sagradas, 1597"),
        f("If a man will begin with certainties, he shall end in doubts; but if he will be content to begin with doubts, he shall end in certainties.",
            "Quien empieza con certezas acabará en dudas; pero quien se conforma con empezar con dudas acabará en certezas.", "Francis Bacon, El avance del saber, 1605"),
        f("A wise man proportions his belief to the evidence.", "El sabio ajusta su creencia a la evidencia.", "David Hume, Investigación sobre el entendimiento humano X, 1748"),
        f("Rien ne se perd, rien ne se crée, tout se transforme.", "Nada se pierde, nada se crea: todo se transforma.", "A partir de Lavoisier, Tratado elemental de química, 1789"),
        f("Ignorance more frequently begets confidence than does knowledge.", "La ignorancia engendra confianza con más frecuencia que el conocimiento.", "Charles Darwin, El origen del hombre, 1871"),
        f("La sapienza è figliola della sperienza.", "La sabiduría es hija de la experiencia.", "Leonardo da Vinci, Códice Forster III"),
        f(null, "Todo hombre puede ser, si se lo propone, escultor de su propio cerebro.", "Santiago Ramón y Cajal, Reglas y consejos sobre investigación científica, 1897"),
        f("On fait la science avec des faits, comme on fait une maison avec des pierres; mais une accumulation de faits n'est pas plus une science qu'un tas de pierres n'est une maison.",
            "La ciencia se hace con hechos como una casa con piedras; pero un montón de hechos no es una ciencia, igual que un montón de piedras no es una casa.", "Henri Poincaré, La ciencia y la hipótesis, 1902"),
        f("On ne doit rien craindre dans la vie, il faut seulement comprendre.", "En la vida no hay que temer nada, solo comprender.", "Atribuida a Marie Curie"),
        f("The first principle is that you must not fool yourself — and you are the easiest person to fool.",
            "El primer principio es que no debes engañarte a ti mismo, y tú eres la persona más fácil de engañar.", "Richard Feynman, «Cargo Cult Science», 1974"),
        f("All models are wrong, but some are useful.", "Todos los modelos están equivocados, pero algunos son útiles.", "George E. P. Box, 1979"),
    )

    val COLONOS = listOf(
        f("These are the times that try men's souls.", "Estos son los tiempos que ponen a prueba el alma.", "Thomas Paine, La crisis americana, 1776"),
        f("What we obtain too cheap, we esteem too lightly.", "Lo que obtenemos demasiado barato lo estimamos demasiado poco.", "Thomas Paine, La crisis americana, 1776"),
        f("We have it in our power to begin the world over again.", "Está en nuestras manos empezar el mundo de nuevo.", "Thomas Paine, Sentido común, 1776"),
        f("Facts are stubborn things.", "Los hechos son tercos.", "John Adams, en el juicio por la masacre de Boston, 1770"),
        f("Lost time is never found again.", "El tiempo perdido nunca se vuelve a encontrar.", "Benjamin Franklin, Almanaque del pobre Richard"),
        f("Have you somewhat to do to-morrow, do it to-day.", "¿Tienes algo que hacer mañana? Hazlo hoy.", "Benjamin Franklin, Almanaque del pobre Richard, 1742"),
        f("Beware of little expenses; a small leak will sink a great ship.", "Cuidado con los gastos pequeños: una grieta pequeña hunde un barco grande.", "Benjamin Franklin, El camino a la riqueza, 1758"),
        f("Dost thou love life? Then do not squander time, for that's the stuff life is made of.", "¿Amas la vida? Entonces no malgastes el tiempo, que de eso está hecha.", "Benjamin Franklin, Almanaque del pobre Richard"),
        f("Well done is better than well said.", "Bien hecho es mejor que bien dicho.", "Benjamin Franklin, Almanaque del pobre Richard"),
        f("Early to bed and early to rise, makes a man healthy, wealthy, and wise.", "Acostarse temprano y madrugar hacen a uno sano, rico y sabio.", "Benjamin Franklin, Almanaque del pobre Richard"),
        f(null, "Hice un librito con una página para cada virtud, y cada noche marcaba con un punto las faltas del día.", "Benjamin Franklin, Autobiografía, sobre su registro de virtudes"),
        f("Determine that the thing can and shall be done, and then we shall find the way.", "Decidan que la cosa puede hacerse y se hará, y entonces encontraremos el camino.", "Abraham Lincoln, 1848"),
        f("I went to the woods because I wished to live deliberately, to front only the essential facts of life.",
            "Me fui a los bosques porque quería vivir deliberadamente y enfrentar solo los hechos esenciales de la vida.", "Henry David Thoreau, Walden, 1854"),
        f("Our life is frittered away by detail. Simplify, simplify.", "Nuestra vida se desperdicia en detalles. Simplifica, simplifica.", "Henry David Thoreau, Walden, 1854"),
        f("Time is but the stream I go a-fishing in.", "El tiempo no es más que el arroyo donde voy a pescar.", "Henry David Thoreau, Walden, 1854"),
        f("Trust thyself: every heart vibrates to that iron string.", "Confía en ti mismo: todo corazón vibra con esa cuerda de hierro.", "Ralph Waldo Emerson, Confianza en uno mismo, 1841"),
        f("The creation of a thousand forests is in one acorn.", "La creación de mil bosques está en una bellota.", "Ralph Waldo Emerson, «Historia», 1841"),
        f("Write it on your heart that every day is the best day in the year.", "Escribe en tu corazón que cada día es el mejor del año.", "Ralph Waldo Emerson, Sociedad y soledad, 1870"),
        f("Finish each day and be done with it. You have done what you could.", "Termina cada día y déjalo atrás. Hiciste lo que pudiste.", "Ralph Waldo Emerson, carta a su hija"),
        f("Do I contradict myself? Very well then I contradict myself, (I am large, I contain multitudes.)",
            "¿Me contradigo? Muy bien, me contradigo: soy grande, contengo multitudes.", "Walt Whitman, Canto a mí mismo, 1855"),
    )


    val NAPOLEON = listOf(
        f("Impossible n'est pas français.", "Imposible no es francés.", "Atribuida a Napoleón, a partir de una carta al general Lemarois, 1813"),
        f("Demandez-moi tout, excepté du temps.", "Pídanme lo que quieran, menos tiempo.", "Atribuida a Napoleón"),
        f("Je puis perdre des batailles, mais on ne me verra jamais perdre une minute.", "Puedo perder batallas, pero nadie me verá perder un minuto.", "Atribuida a Napoleón"),
        f("La victoire appartient au plus persévérant.", "La victoria pertenece al más perseverante.", "Atribuida a Napoleón"),
        f("Un bon croquis vaut mieux qu'un long discours.", "Un buen croquis vale más que un largo discurso.", "Atribuida a Napoleón"),
        f("Rien n'est plus difficile, et donc plus précieux, que d'être capable de décider.", "Nada es más difícil, y por eso más valioso, que ser capaz de decidir.", "Atribuida a Napoleón"),
        f("Une tête sans mémoire est une place sans garnison.", "Una cabeza sin memoria es una plaza sin guarnición.", "Atribuida a Napoleón"),
        f(null, "Un ejército marcha sobre su estómago.", "Atribuida a Napoleón (y también a Federico el Grande)"),
        f("Du sublime au ridicule il n'y a qu'un pas.", "De lo sublime a lo ridículo no hay más que un paso.", "Napoleón, en la retirada de Rusia, 1812"),
        f("Quel roman pourtant que ma vie!", "¡Qué novela, sin embargo, ha sido mi vida!", "Napoleón en Santa Elena, según Las Cases, Memorial, 1823"),
        f("Qu'est-ce que l'histoire, sinon une fable convenue?", "¿Qué es la historia sino una fábula convenida?", "Napoleón en Santa Elena, según Las Cases, Memorial, 1823"),
        f("Nothing except a battle lost can be half so melancholy as a battle won.", "Nada, salvo una batalla perdida, es tan melancólico como una batalla ganada.", "Duque de Wellington, despacho de 1815"),
        f("Alles im Kriege ist sehr einfach, aber das Einfachste ist schwierig.", "En la guerra todo es muy sencillo, pero lo más sencillo es difícil.", "Carl von Clausewitz, De la guerra I, 7"),
        f("Surtout, pas trop de zèle.", "Sobre todo, no demasiado celo.", "Atribuida a Talleyrand"),
        f(null, "El sueño de la razón produce monstruos.", "Francisco de Goya, Los caprichos 43, 1799"),
        f("Un roman est un miroir qui se promène sur une grande route.", "Una novela es un espejo que se pasea por un camino real.", "Stendhal, Rojo y negro, 1830"),
        f("Wer nicht von dreitausend Jahren sich weiß Rechenschaft zu geben, bleib im Dunkeln unerfahren, mag von Tag zu Tage leben.",
            "Quien no sabe dar cuenta de tres mil años se queda a oscuras, sin experiencia, viviendo al día.", "Goethe, Diván de Oriente y Occidente, 1819"),
        f("Le génie n'est qu'une plus grande aptitude à la patience.", "El genio no es más que una mayor aptitud para la paciencia.", "Atribuida a Buffon"),
    )

    val NAVIDAD = listOf(
        f("I will honour Christmas in my heart, and try to keep it all the year.", "Honraré la Navidad en mi corazón y procuraré guardarla todo el año.", "Charles Dickens, Canción de Navidad, 1843"),
        f("I will live in the Past, the Present, and the Future. The Spirits of all Three shall strive within me.",
            "Viviré en el Pasado, el Presente y el Futuro. Los Espíritus de los tres lucharán dentro de mí.", "Charles Dickens, Canción de Navidad, 1843"),
        f("No space of regret can make amends for one life's opportunity misused.", "Ningún arrepentimiento compensa una oportunidad de la vida mal aprovechada.", "Charles Dickens, Canción de Navidad, 1843"),
        f("There is nothing in the world so irresistibly contagious as laughter and good humour.", "No hay nada en el mundo tan irresistiblemente contagioso como la risa y el buen humor.", "Charles Dickens, Canción de Navidad, 1843"),
        f("God bless us, every one!", "¡Que Dios nos bendiga a todos!", "Tiny Tim, en Canción de Navidad de Charles Dickens, 1843"),
        f("Reflect upon your present blessings, of which every man has plenty; not on your past misfortunes, of which all men have some.",
            "Piensa en las bendiciones que tienes, que todos tenemos muchas, y no en las desgracias pasadas, que todos tenemos algunas.", "Charles Dickens, «Una cena de Navidad», 1835"),
        f(null, "Mas María guardaba todas estas cosas, confiriéndolas en su corazón.", "Lucas 2, 19 (Reina-Valera 1909)"),
        f(null, "Gloria en las alturas a Dios, y en la tierra paz, buena voluntad para con los hombres.", "Lucas 2, 14 (Reina-Valera 1909)"),
        f(null, "Noche de paz, noche de amor, todo duerme en derredor.", "«Noche de paz», de Joseph Mohr (1816), versión de Federico Fliedner"),
        f("Happy Christmas to all, and to all a good-night!", "¡Feliz Navidad a todos, y a todos buenas noches!", "Clement Clarke Moore, «Una visita de San Nicolás», 1823"),
        f("Of all the old festivals, that of Christmas awakens the strongest and most heartfelt associations.",
            "De todas las fiestas antiguas, la Navidad es la que despierta los recuerdos más fuertes y sentidos.", "Washington Irving, «La Navidad», 1819"),
        f("Of peace on earth, good-will to men!", "Paz en la tierra y buena voluntad a los hombres.", "Henry W. Longfellow, «I Heard the Bells on Christmas Day», 1863"),
        f("Christmas won't be Christmas without any presents.", "La Navidad no será Navidad sin regalos.", "Louisa May Alcott, Mujercitas, 1868, primera línea"),
        f("Yes, Virginia, there is a Santa Claus.", "Sí, Virginia, Santa Claus existe.", "Francis P. Church, The Sun, Nueva York, 1897"),
        f("Christmas is not a time nor a season, but a state of mind.", "La Navidad no es una fecha ni una temporada, sino un estado de ánimo.", "Calvin Coolidge, mensaje de Navidad, 1927"),
    )

    val SEDA = listOf(
        f("The Moving Finger writes; and, having writ, moves on: nor all thy Piety nor Wit shall lure it back to cancel half a Line.",
            "El dedo escribe y, una vez escrito, sigue adelante: ni toda tu piedad ni tu ingenio lo harán volver para borrar media línea.", "Rubaiyat de Omar Jayam, versión de Edward FitzGerald, 1859"),
        f("A Book of Verses underneath the Bough, A Jug of Wine, a Loaf of Bread — and Thou.", "Un libro de versos bajo la rama, un cántaro de vino, una hogaza de pan… y tú.", "Rubaiyat de Omar Jayam, versión de Edward FitzGerald, 1859"),
        f("吾日三省吾身。", "Cada día me examino a mí mismo tres veces.", "Zengzi, en las Analectas de Confucio I, 4"),
        f("知之為知之，不知為不知，是知也。", "Saber que sabes lo que sabes y que no sabes lo que no sabes: eso es saber.", "Confucio, Analectas II, 17"),
        f("學而不思則罔，思而不學則殆。", "Aprender sin pensar es inútil; pensar sin aprender es peligroso.", "Confucio, Analectas II, 15"),
        f("三人行，必有我師焉。", "Cuando caminan tres, uno de ellos puede ser mi maestro.", "Confucio, Analectas VII, 22"),
        f("千里之行，始於足下。", "Un viaje de mil li empieza bajo tus pies.", "Laozi, Tao Te Ching 64"),
        f("知人者智，自知者明。", "Quien conoce a los otros es sabio; quien se conoce a sí mismo es lúcido.", "Laozi, Tao Te Ching 33"),
        f("知彼知己，百戰不殆。", "Conoce al otro y conócete a ti mismo, y en cien batallas no correrás peligro.", "Sunzi, El arte de la guerra III"),
        f("好記性不如爛筆頭。", "La mejor memoria no vale lo que un pincel gastado.", "Proverbio chino"),
        f("寧可就西而死，豈歸東而生", "Prefiero morir camino del oeste que vivir volviendo al este.", "Xuanzang, al cruzar el desierto, según su biografía"),
        f("بنی‌آدم اعضای یکدیگرند", "Los hijos de Adán son miembros de un mismo cuerpo.", "Saadi, Gulistán I, 10"),
        f(null, "Dos se afanaron en vano: el que juntó y no disfrutó, y el que aprendió y no puso en práctica.", "Saadi, Gulistán VIII"),
        f(null, "Este cuerpo es una posada, muchacho: cada mañana llega corriendo un huésped nuevo.", "Rumi, Masnavi V"),
        f("Non ho scritto la metà di quello che ho visto.", "No he contado ni la mitad de lo que vi.", "Atribuida a Marco Polo por Jacopo d'Acqui"),
    )

    val BOTANICO = listOf(
        f("But no pursuit at Cambridge was followed with nearly so much eagerness or gave me so much pleasure as collecting beetles.",
            "Ninguna afición en Cambridge la seguí con tanto entusiasmo, ni me dio tanto placer, como coleccionar escarabajos.", "Charles Darwin, Autobiografía, 1876"),
        f(null, "Un día, al arrancar una corteza vieja, vi dos escarabajos raros y tomé uno en cada mano; entonces vi un tercero, de una especie nueva, que no podía soportar perder, así que me metí en la boca el de la mano derecha.",
            "Charles Darwin, Autobiografía, 1876"),
        f("The brain of an ant is one of the most marvellous atoms of matter in the world, perhaps more so than the brain of man.",
            "El cerebro de una hormiga es uno de los átomos de materia más maravillosos del mundo, quizá más que el cerebro del hombre.", "Charles Darwin, El origen del hombre, 1871"),
        f("Delight itself, however, is a weak term to express the feelings of a naturalist who, for the first time, has wandered by himself in a Brazilian forest.",
            "Deleite es una palabra débil para lo que siente un naturalista que, por primera vez, ha vagado solo por un bosque brasileño.", "Charles Darwin, Viaje de un naturalista alrededor del mundo, 1839"),
        f("From so simple a beginning endless forms most beautiful and most wonderful have been, and are being, evolved.",
            "A partir de un comienzo tan sencillo, infinidad de formas, las más bellas y maravillosas, han evolucionado y siguen evolucionando.", "Charles Darwin, El origen de las especies, 1859"),
        f("My heart began to beat violently, the blood rushed to my head, and I felt much more like fainting than I have done when in apprehension of immediate death.",
            "El corazón empezó a latirme con violencia, la sangre se me subió a la cabeza y estuve más cerca de desmayarme que cuando creí que iba a morir (al atrapar una mariposa nueva).", "Alfred Russel Wallace, El archipiélago malayo, 1869"),
        f("Nomina si nescis, perit et cognitio rerum.", "Si no conoces los nombres, se pierde también el conocimiento de las cosas.", "Carlos Linneo, Filosofía botánica, 1751"),
        f("Natura non facit saltus.", "La naturaleza no da saltos.", "Carlos Linneo, Filosofía botánica, 1751"),
        f("Deus creavit, Linnaeus disposuit.", "Dios creó; Linneo ordenó.", "Lema que se atribuía el propio Linneo"),
        f("O nobilissima viriditas, quae radicas in sole.", "Oh, nobilísimo verdor, que tienes tus raíces en el sol.", "Hildegarda de Bingen, responsorio, siglo XII"),
        f(null, "Desde mi juventud me he dedicado a la observación de los insectos.", "Maria Sibylla Merian, La metamorfosis de los insectos de Surinam, 1705"),
        f("Alles ist Blatt.", "Todo es hoja.", "Goethe, apuntes sobre la metamorfosis de las plantas"),
        f("Grau, teurer Freund, ist alle Theorie, und grün des Lebens goldner Baum.", "Gris, querido amigo, es toda teoría, y verde el árbol dorado de la vida.", "Goethe, Fausto I, 1808"),
        f("The most insignificant insects and reptiles are of much more consequence, and have much more influence in the economy of Nature, than the incurious are aware of.",
            "Los insectos y reptiles más insignificantes importan mucho más en la economía de la naturaleza de lo que imaginan los poco curiosos.", "Gilbert White, Historia natural de Selborne, 1789"),
        f("Alles ist Wechselwirkung.", "Todo es interacción.", "Alexander von Humboldt, diario de viaje, 1803"),
        f("To see a World in a Grain of Sand / And a Heaven in a Wild Flower", "Ver un mundo en un grano de arena y un cielo en una flor silvestre.", "William Blake, «Augurios de inocencia»"),
        f("The earth laughs in flowers.", "La tierra ríe en flores.", "Ralph Waldo Emerson, «Hamatreya», 1847"),
        f("The question is not what you look at, but what you see.", "La cuestión no es qué miras, sino qué ves.", "Henry David Thoreau, diario, 1851"),
        f("Heaven is under our feet as well as over our heads.", "El cielo está bajo nuestros pies tanto como sobre nuestras cabezas.", "Henry David Thoreau, Walden, 1854"),
        f("Though I do not believe that a plant will spring up where no seed has been, I have great faith in a seed.",
            "Aunque no creo que brote una planta donde no hubo semilla, tengo una gran fe en las semillas.", "Henry David Thoreau, La sucesión de los árboles del bosque, 1860"),
        f("I believe a leaf of grass is no less than the journey-work of the stars.", "Creo que una brizna de hierba no es menos que la jornada de trabajo de las estrellas.", "Walt Whitman, Canto a mí mismo, 1855"),
        f("It is interesting to contemplate an entangled bank, clothed with many plants of many kinds, with birds singing on the bushes, with various insects flitting about.",
            "Es interesante contemplar una orilla enmarañada, cubierta de plantas de muchas clases, con pájaros cantando en los arbustos e insectos revoloteando.", "Charles Darwin, El origen de las especies, 1859"),
        f("From so simple a beginning endless forms most beautiful and most wonderful have been, and are being, evolved.",
            "De un comienzo tan sencillo han evolucionado, y siguen evolucionando, infinitas formas, las más bellas y maravillosas.", "Charles Darwin, El origen de las especies, 1859"),
        f("Meine Zeit wird schon kommen.", "Ya llegará mi tiempo.", "Atribuida a Gregor Mendel"),
        f("Vous éventrez la bête et moi je l'étudie vivante.", "Ustedes destripan al animal; yo lo estudio vivo.", "Jean-Henri Fabre, Recuerdos entomológicos II, 1882"),
        f("When we try to pick out anything by itself, we find it hitched to everything else in the Universe.",
            "Cuando intentamos separar una cosa por sí sola, la encontramos enganchada a todo lo demás del universo.", "John Muir, Mi primer verano en la sierra, 1911"),
        f("In every walk with nature one receives far more than he seeks.", "En cada paseo por la naturaleza uno recibe mucho más de lo que busca.", "John Muir, 1918"),
        f("Those who contemplate the beauty of the earth find reserves of strength that will endure as long as life lasts.",
            "Quienes contemplan la belleza de la tierra encuentran reservas de fuerza que duran tanto como la vida.", "Rachel Carson, El sentido del asombro, 1965"),
    )

    val GOTICO = listOf(
        f("Mens hebes ad verum per materialia surgit.", "La mente torpe se eleva a la verdad a través de lo material.", "Inscripción del abad Suger en las puertas de Saint-Denis, c. 1140"),
        f("Nobile claret opus, sed opus quod nobile claret clarificet mentes, ut eant per lumina vera ad verum lumen.",
            "La noble obra resplandece; y que, resplandeciendo, ilumine las mentes para que vayan por luces verdaderas hacia la luz verdadera.", "Abad Suger, inscripción de Saint-Denis"),
        f("Formam primam corporalem lucem esse arbitror.", "Creo que la primera forma corporal es la luz.", "Roberto Grosseteste, Sobre la luz, c. 1225"),
        f("Dixitque Deus: Fiat lux. Et facta est lux.", "Y dijo Dios: Sea la luz. Y la luz fue.", "Génesis 1, 3 (Vulgata)"),
        f("Ego sum lux mundi.", "Yo soy la luz del mundo.", "Juan 8, 12 (Vulgata)"),
        f("Lucerna pedibus meis verbum tuum, et lumen semitis meis.", "Lámpara es a mis pies tu palabra, y luz para mis senderos.", "Salmo 118 (119), 105, Vulgata"),
        f("Cantare amantis est.", "Cantar es propio del que ama.", "San Agustín, Sermón 336"),
        f("Quid facit illa ridicula monstruositas?", "¿Qué hace ahí esa ridícula monstruosidad?", "Bernardo de Claraval, Apología, c. 1125, contra los capiteles de los claustros"),
        f(null, "Villard de Honnecourt os saluda y ruega a todos los que trabajen con los ingenios de este libro que recen por su alma y se acuerden de él.",
            "Villard de Honnecourt, cuaderno de dibujos, c. 1230"),
        f("L'amor che move il sole e l'altre stelle.", "El amor que mueve el sol y las demás estrellas.", "Dante, Paraíso XXXIII"),
        f(null, "He encontrado entre mis papeles una hoja donde llamo a la arquitectura una música congelada.", "Goethe, a Eckermann, 1829"),
        f("Ceci tuera cela. Le livre tuera l'édifice.", "Esto matará aquello. El libro matará al edificio.", "Victor Hugo, Nuestra Señora de París, 1831"),
        f(null, "Cada cara, cada piedra del venerable monumento es una página no solo de la historia del país, sino también de la historia de la ciencia y del arte.",
            "Victor Hugo, Nuestra Señora de París, 1831"),
        f("We may live without her, and worship without her, but we cannot remember without her.", "Podemos vivir sin la arquitectura y rezar sin ella, pero no podemos recordar sin ella.",
            "John Ruskin, Las siete lámparas de la arquitectura, 1849"),
        f("When we build, let us think that we build for ever.", "Cuando construyamos, pensemos que construimos para siempre.", "John Ruskin, Las siete lámparas de la arquitectura, 1849"),
    )

    val ESPACIO = listOf(
        f("Οἶδ᾽ ὅτι θνατὸς ἐγὼ καὶ ἐφάμερος· ἀλλ᾽ ὅταν ἄστρων μαστεύω πυκινὰς ἀμφιδρόμους ἕλικας, οὐκέτ᾽ ἐπιψαύω γαίης ποσίν",
            "Sé que soy mortal y de un día; pero cuando sigo las apretadas espirales de los astros, ya no toco la tierra con los pies.", "Claudio Ptolomeo, Antología Palatina IX, 577"),
        f("In medio vero omnium residet Sol.", "En medio de todo reside el Sol.", "Nicolás Copérnico, Sobre las revoluciones I, 10, 1543"),
        f("E pur si muove.", "Y sin embargo se mueve.", "Atribuida a Galileo Galilei (la anécdota es posterior)"),
        f("Mensus eram coelos, nunc terrae metior umbras.", "Medí los cielos; ahora mido las sombras de la tierra.", "Johannes Kepler, epitafio escrito por él mismo, 1630"),
        f("Le silence éternel de ces espaces infinis m'effraie.", "El silencio eterno de estos espacios infinitos me espanta.", "Blaise Pascal, Pensamientos"),
        f(null, "No sé qué pareceré al mundo; a mí me parece que he sido solo como un niño que juega en la orilla del mar, mientras el gran océano de la verdad se extendía ante mí sin descubrir.",
            "Isaac Newton, recogido por David Brewster"),
        f("Der bestirnte Himmel über mir und das moralische Gesetz in mir.", "El cielo estrellado sobre mí y la ley moral dentro de mí.", "Immanuel Kant, Crítica de la razón práctica, 1788"),
        f("Look'd up in perfect silence at the stars.", "Levanté la vista, en perfecto silencio, hacia las estrellas.", "Walt Whitman, «When I Heard the Learn'd Astronomer», 1865"),
        f("Das ewig Unbegreifliche an der Welt ist ihre Begreiflichkeit.", "Lo eternamente incomprensible del mundo es que sea comprensible.", "Albert Einstein, «Física y realidad», 1936"),
        f("Equipped with his five senses, man explores the universe around him and calls the adventure Science.",
            "Armado con sus cinco sentidos, el ser humano explora el universo que lo rodea y llama Ciencia a esa aventura.", "Edwin Hubble, 1954"),
        f("Облетев Землю в корабле-спутнике, я увидел, как прекрасна наша планета.", "Al dar la vuelta a la Tierra en la nave, vi lo hermoso que es nuestro planeta.", "Yuri Gagarin, 1961"),
        f("We came all this way to explore the Moon, and the most important thing is that we discovered the Earth.",
            "Vinimos hasta aquí para explorar la Luna, y lo más importante es que descubrimos la Tierra.", "William Anders, Apolo 8"),
        f("Houston, Tranquility Base here. The Eagle has landed.", "Houston, aquí Base Tranquilidad. El Águila ha aterrizado.", "Neil Armstrong, 20 de julio de 1969"),
        f("That's one small step for man, one giant leap for mankind.", "Es un pequeño paso para el hombre, un gran salto para la humanidad.", "Neil Armstrong, 20 de julio de 1969"),
        f("Houston, we've had a problem.", "Houston, hemos tenido un problema.", "Jack Swigert y Jim Lovell, Apolo 13, 1970"),
        f("The cosmos is within us. We are made of star-stuff.", "El cosmos está dentro de nosotros. Estamos hechos de materia de estrellas.", "Carl Sagan, Cosmos, 1980"),
        f("Look again at that dot. That's here. That's home. That's us.", "Mira otra vez ese punto. Eso es aquí. Es nuestra casa. Somos nosotros.", "Carl Sagan, Un punto azul pálido, 1994"),
    )

    val ANDALUS = listOf(
        f("ولا غالب إلا الله", "No hay vencedor sino Dios.", "Lema de los nazaríes, repetido en los muros de la Alhambra"),
        f(null, "Soy un ojo lleno de alegría en este jardín, y la pupila de ese ojo es, en verdad, nuestro señor.", "Ibn Zamrak, poema del mirador de Lindaraja, Alhambra"),
        f("تبدت لنا وسط الرصافة نخلة تناءت بأرض الغرب عن بلد النخل", "En medio de la Rusafa se nos apareció una palmera, lejos, en tierra de Occidente, del país de las palmeras.",
            "Abderramán I, Córdoba, siglo VIII"),
        f(null, "He reinado más de cincuenta años, en victoria o en paz… He contado con cuidado los días de pura y auténtica felicidad que he gozado: suman catorce.",
            "Abderramán III, según al-Maqqari"),
        f("الحب أعزك الله أوله هزل وآخره جد", "El amor —¡Dios te honre!— empieza en broma y acaba en serio.", "Ibn Hazm de Córdoba, El collar de la paloma, c. 1023"),
        f(null, "Busqué un fin en las acciones humanas que todos estimaran bueno y desearan, y no hallé más que uno: apartar la preocupación.",
            "Ibn Hazm de Córdoba, Los caracteres y la conducta"),
        f("أنا والله أصلح للمعالي وأمشي مشيتي وأتيه تيها", "Por Dios, que estoy hecha para lo más alto: camino a mi manera y con orgullo.",
            "Wallada bint al-Mustakfi, verso bordado en su vestido, Córdoba, siglo XI"),
        f("إني ذكرتك بالزهراء مشتاقا", "Te he recordado con nostalgia en al-Zahra.", "Ibn Zaydún, poema a Wallada, siglo XI"),
        f(null, "Prefiero ser camellero en África que porquero en Castilla.", "Al-Mutámid de Sevilla, según la tradición, 1086"),
        f("يا أهل أندلس لله دركم ماء وظل وأنهار وأشجار", "¡Oh gentes de al-Ándalus, qué dicha la vuestra: agua, sombra, ríos y árboles!", "Ibn Jafaya de Alcira, siglo XII"),
        f("الحق لا يضاد الحق بل يوافقه ويشهد له", "La verdad no contradice a la verdad: concuerda con ella y da testimonio de ella.", "Averroes, Tratado decisivo, 1179"),
        f(null, "Escucha la verdad, venga de quien venga.", "Maimónides, Ocho capítulos, prólogo"),
        f("لقد صار قلبي قابلا كل صورة فمرعى لغزلان ودير لرهبان", "Mi corazón se ha vuelto capaz de toda forma: pradera para gacelas y convento para monjes.",
            "Ibn Arabi de Murcia, El intérprete de los deseos"),
    )

    val MONGOL = listOf(
        f(null, "En el origen hubo un lobo azulado, nacido con su destino del Cielo de arriba, y su compañera era una cierva leonada.", "Historia secreta de los mongoles §1, siglo XIII"),
        f(null, "Si fuerais cada uno una sola flecha, cualquiera os quebraría; juntos, como un haz de flechas, nadie podrá con vosotros.",
            "Alan Qo'a a sus cinco hijos, Historia secreta de los mongoles §22"),
        f("Сүүдрээс өөр нөхөргүй, сүүлнээс өөр ташуургүй", "No tenemos más compañeros que nuestras sombras, ni más látigo que la cola de nuestros caballos.",
            "Hoelún a sus hijos, Historia secreta de los mongoles §77"),
        f("Мөнх тэнгэрийн хүчин дор", "Bajo el poder del Cielo Eterno.", "Fórmula con que empezaban los edictos de los kanes"),
        f(null, "Quien sabe poner en orden su casa sabe poner en orden un país.", "Dichos de Gengis Kan, recogidos por Rashid al-Din"),
        f("有衛生之道，而無長生之藥", "Hay maneras de cuidar la vida, pero no hay medicina para no morir.", "El monje Changchun a Gengis Kan, 1222, según la Historia de Yuan"),
        f("天下雖得之馬上，不可以馬上治", "El imperio se ganó a caballo, pero no puede gobernarse a caballo.", "Yelü Chucai a Ögedei Kan, según la Historia de Yuan"),
        f("Sicut Deus dedit manui diversos digitos, ita dedit hominibus diversas vias.", "Así como Dios dio a la mano dedos diversos, así dio a los hombres caminos diversos.",
            "Möngke Kan a Guillermo de Rubruck, 1254"),
        f(null, "Si bebes de un agua, sigue la costumbre del agua; si vives en una tierra, sigue la costumbre de esa tierra.", "Proverbio mongol"),
        f(null, "Se terminó de escribir en el año de la rata, en la luna del corzo, cuando la gran asamblea estaba reunida junto al Kerulén.",
            "Colofón de la Historia secreta de los mongoles §282"),
    )

    val RENACIMIENTO = listOf(
        f("Siccome una giornata bene spesa dà lieto dormire, così una vita bene usata dà lieto morire.", "Así como una jornada bien empleada da un dulce dormir, una vida bien usada da un dulce morir.",
            "Leonardo da Vinci, Códice Trivulziano"),
        f("L'acqua che tocchi de' fiumi è l'ultima di quella che andò e la prima di quella che viene: così il tempo presente.",
            "El agua que tocas de los ríos es la última de la que se fue y la primera de la que viene: así el tiempo presente.", "Leonardo da Vinci, Códice Trivulziano"),
        f("Il ferro s'arrugginisce sanza esercizio, l'acqua si putrefà o nel freddo s'addiaccia, così lo 'ngegno sanza esercizio si guasta.",
            "El hierro se oxida sin uso, el agua se pudre o se hiela con el frío; así el ingenio sin ejercicio se echa a perder.", "Leonardo da Vinci, Códice Atlántico"),
        f("Dimmi se mai fu fatta alcuna cosa.", "Dime si alguna vez se hizo algo.", "Leonardo da Vinci, frase que repetía en los márgenes de sus cuadernos"),
        f("Ogni nostra cognizione prencipia da' sentimenti.", "Todo nuestro conocimiento empieza por los sentidos.", "Leonardo da Vinci, Códice Trivulziano"),
        f("Ostinato rigore.", "Obstinado rigor.", "Lema de Leonardo da Vinci"),
        f("Sì come il mangiare sanza voglia fia dannoso alla salute, così lo studio sanza desiderio guasta la memoria.",
            "Así como comer sin ganas daña la salud, estudiar sin deseo estropea la memoria.", "Leonardo da Vinci, manuscrito 2038 de la Biblioteca Nacional de Francia"),
        f("La pittura è una poesia che si vede e non si sente, e la poesia è una pittura che si sente e non si vede.",
            "La pintura es una poesía que se ve y no se oye; la poesía, una pintura que se oye y no se ve.", "Leonardo da Vinci, Tratado de la pintura"),
        f("Chi non stima la vita, non la merita.", "Quien no estima la vida no la merece.", "Leonardo da Vinci, cuadernos"),
        f("Non ha l'ottimo artista alcun concetto c'un marmo solo in sé non circonscriva col suo superchio.",
            "El mejor artista no concibe nada que un solo bloque de mármol no encierre ya en su interior.", "Miguel Ángel, Rimas 151"),
        f("Ancora imparo.", "Todavía aprendo.", "Atribuida a Miguel Ángel, a los 87 años"),
        f(null, "Para que, como libre y soberano modelador y escultor de ti mismo, te forjes en la forma que prefieras.", "Pico della Mirandola, Discurso sobre la dignidad del hombre, 1486"),
        f("Gli uomini possono da sé tutte le cose, purché vogliano.", "Los hombres pueden por sí mismos todas las cosas, con tal que quieran.", "Atribuida a Leon Battista Alberti"),
        f("Quant'è bella giovinezza, che si fugge tuttavia! Chi vuol esser lieto, sia: di doman non c'è certezza.",
            "¡Qué bella es la juventud, que se escapa sin remedio! Quien quiera estar alegre, que lo esté: del mañana no hay certeza.", "Lorenzo de Médici, «Triunfo de Baco y Ariadna», 1490"),
        f(null, "Llegada la noche, vuelvo a casa y entro en mi escritorio; en el umbral me quito la ropa del día, llena de barro y de lodo, y me visto con paños reales y curiales.",
            "Nicolás Maquiavelo, carta a Francesco Vettori, 1513"),
    )

    val URSS = listOf(
        f("Планета есть колыбель разума, но нельзя вечно жить в колыбели.", "La Tierra es la cuna de la razón, pero no se puede vivir siempre en la cuna.", "Konstantín Tsiolkovski, carta de 1911"),
        f("Поехали!", "¡Vámonos!", "Yuri Gagarin, 12 de abril de 1961"),
        f("Эй, небо, сними шляпу!", "¡Eh, cielo, quítate el sombrero!", "Valentina Tereshkova, al despegar, 1963"),
        f("Светить всегда, светить везде.", "Brillar siempre, brillar en todas partes.", "Vladímir Maiakovski, 1920"),
        f("Я знаю — город будет, я знаю — саду цвесть", "Yo sé que habrá ciudad, yo sé que el jardín florecerá.", "Vladímir Maiakovski, 1929"),
        f("Человек — это звучит гордо!", "¡El ser humano: qué orgulloso suena!", "Máximo Gorki, Los bajos fondos, 1902"),
        f("Краткость — сестра таланта.", "La brevedad es hermana del talento.", "Antón Chéjov, carta a su hermano Alexandr, 1889"),
        f("В человеке должно быть всё прекрасно: и лицо, и одежда, и душа, и мысли.", "En una persona todo debe ser hermoso: la cara, la ropa, el alma y los pensamientos.", "Antón Chéjov, Tío Vania, 1897"),
        f("Коммунизм есть Советская власть плюс электрификация всей страны.", "El comunismo es el poder soviético más la electrificación de todo el país.", "Lenin, 1920"),
        f("Лучше меньше, да лучше.", "Mejor menos, pero mejor.", "Lenin, artículo de 1923"),
        f("Учиться, учиться и учиться.", "Estudiar, estudiar y estudiar.", "Lenin, 1899"),
        f("Пятилетку — в четыре года!", "¡El plan quinquenal, en cuatro años!", "Consigna soviética, 1929"),
        f("Постепенность, постепенность и постепенность.", "Gradualidad, gradualidad y gradualidad.", "Iván Pávlov, Carta a la juventud, 1936"),
        f("Хочешь быть счастливым, будь им.", "Si quieres ser feliz, sélo.", "Kozmá Prutkov, Frutos de la meditación"),
        f("Никто не обнимет необъятного.", "Nadie abarca lo inabarcable.", "Kozmá Prutkov, Frutos de la meditación"),
        f("Знать — значит предсказывать.", "Saber es predecir.", "Atribuida a Dmitri Mendeléyev"),
        f("Eight hours' labour, eight hours' recreation, eight hours' rest.", "Ocho horas de trabajo, ocho de recreo, ocho de descanso.", "Robert Owen, 1817, consigna que adoptaron los obreros de todo el mundo"),
    )

    fun de(tema: TemaId): List<Frase> = when (tema) {
        TemaId.ROMANO -> ROMANO; TemaId.CIBER -> CIBER; TemaId.HELENO -> HELENO; TemaId.MEDIEVAL -> MEDIEVAL
        TemaId.DISTOPIA -> DISTOPIA; TemaId.REFUGIO -> REFUGIO; TemaId.ORDEN -> ORDEN; TemaId.LABORATORIO -> LABORATORIO; TemaId.COLONOS -> COLONOS
        TemaId.NAPOLEON -> NAPOLEON; TemaId.BOTANICO -> BOTANICO; TemaId.NAVIDAD -> NAVIDAD; TemaId.SEDA -> SEDA; TemaId.URSS -> URSS
        TemaId.GOTICO -> GOTICO; TemaId.ESPACIO -> ESPACIO; TemaId.ANDALUS -> ANDALUS; TemaId.MONGOL -> MONGOL; TemaId.RENACIMIENTO -> RENACIMIENTO
    }

    /** La del día para el tema elegido: cambia cada día y recorre todas antes de repetir. */
    fun del(fecha: String, tema: TemaId = Estilo.tema): Frase {
        val l = de(tema)
        return l[Math.floorMod(Fechas.parse(fecha).toEpochDay(), l.size.toLong()).toInt()]
    }

    /** Encabezado del recuadro de la frase, según el tema. */
    fun rotulo(tema: TemaId = Estilo.tema): String = when (tema) {
        TemaId.ROMANO -> "SENTENTIA DIEI"; TemaId.CIBER -> "> motd"; TemaId.HELENO -> "ΓΝΩΜΗ ΤΗΣ ΗΜΕΡΑΣ"; TemaId.MEDIEVAL -> "Sentencia del día"
        TemaId.DISTOPIA -> "CONSIGNA DEL DÍA"; TemaId.REFUGIO -> "MENSAJE DEL DÍA"; TemaId.ORDEN -> "ORDEN DEL DÍA"; TemaId.LABORATORIO -> "NOTA DEL DÍA"
        TemaId.COLONOS -> "Del almanaque"
        TemaId.NAPOLEON -> "BOLETÍN DEL EJÉRCITO"; TemaId.BOTANICO -> "Nota del naturalista"; TemaId.NAVIDAD -> "Junto a la chimenea"
        TemaId.SEDA -> "Dicho de la caravana"; TemaId.URSS -> "LEMA DEL DÍA"
        TemaId.GOTICO -> "Lectio del día"; TemaId.ESPACIO -> "TRANSMISIÓN DEL DÍA"; TemaId.ANDALUS -> "حكمة اليوم · sentencia del día"
        TemaId.MONGOL -> "ӨНӨӨДРИЙН БИЛИГ · máxima del día"; TemaId.RENACIMIENTO -> "Dal taccuino"
    }

    // ------------------------------------------------------------ frases guardadas («me gusta»)

    fun guardada(f: Frase): Boolean = Repo.config.frases_guardadas.any { it.texto == f.texto && it.autor == f.autor }

    /** Guarda o quita la frase de la lista de favoritas. [fecha] es el día al que pertenece la frase. Devuelve true si quedó guardada. */
    fun alternar(f: Frase, tema: TemaId = Estilo.tema, fecha: String = Fechas.diaLogico()): Boolean {
        val ya = guardada(f)
        Repo.cambiarConfig { c ->
            c.copy(frases_guardadas = if (ya) c.frases_guardadas.filterNot { it.texto == f.texto && it.autor == f.autor }
                else c.frases_guardadas + FraseGuardada(f.texto, f.original, f.autor, tema.id, fecha))
        }
        return !ya
    }

    /**
     * Las frases con ♥ de un día, para la hoja del diario: la frase del día que guardaste y las citas que marcaste ese día.
     * Se muestran como epígrafe; no se guardan en el texto del diario.
     */
    fun deLaHoja(fecha: String): List<Frase> {
        val delDia = Repo.config.frases_guardadas.filter { it.fecha == fecha && it.tema != "kindle" }.map { Frase(it.original, it.texto, it.autor) }
        val citas = Repo.obras.flatMap { o ->
            o.citas.filter { it.favorita && it.fecha == fecha }.map { c ->
                Frase(null, c.texto, listOfNotNull(o.autor.substringBefore(",").substringBefore(";").trim().ifBlank { null }, o.titulo.ifBlank { null }).joinToString(", "))
            }
        }
        return (delDia + citas).distinctBy { it.texto }.take(3)
    }
}
