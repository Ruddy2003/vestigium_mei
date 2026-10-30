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

    /** La Roma de hoy y la de todas las capas: los viajeros que llegaron, los poetas en romanesco, la ciudad de los papas. */
    val URBE = listOf(
        f("Ich zähle einen zweiten Geburtstag, eine wahre Wiedergeburt, von dem Tage, da ich Rom betrat.", "Cuento un segundo cumpleaños, un verdadero renacimiento, desde el día en que pisé Roma.", "Goethe, Viaje a Italia, Roma, 3 de diciembre de 1786"),
        f("Eine Welt zwar bist du, o Rom; doch ohne die Liebe / Wäre die Welt nicht die Welt, wäre denn Rom auch nicht Rom.", "Un mundo eres, Roma; pero sin el amor el mundo no sería el mundo, ni Roma sería Roma.", "Goethe, Elegías romanas I, 1795"),
        f("O Rome! my country! city of the soul!", "¡Oh Roma, mi patria, ciudad del alma!", "Lord Byron, La peregrinación de Childe Harold IV, 78, 1818"),
        f("While stands the Coliseum, Rome shall stand; / When falls the Coliseum, Rome shall fall; / And when Rome falls—the World.", "Mientras el Coliseo esté en pie, Roma estará en pie; cuando caiga el Coliseo, caerá Roma; y cuando caiga Roma, el mundo.", "Lord Byron, Childe Harold IV, 145, citando lo que decían los peregrinos sajones"),
        f("Go thou to Rome,—at once the Paradise, / The grave, the city, and the wilderness.", "Ve tú a Roma: a la vez el paraíso, la tumba, la ciudad y la tierra salvaje.", "Percy B. Shelley, Adonais 49, 1821"),
        f("I have an habitual feeling of my real life having past, and that I am leading a posthumous existence.", "Tengo la sensación constante de que mi vida verdadera ya pasó y de que llevo una existencia póstuma.", "John Keats, última carta conservada, a Charles Brown, Roma, 30 de noviembre de 1820"),
        f("To see the ghost of old Rome, wicked, wonderful old city, haunting the very ground on which its people trod.", "Ver el fantasma de la vieja Roma, malvada y maravillosa ciudad vieja, rondando el mismo suelo que pisó su gente.", "Charles Dickens, Estampas de Italia, 1846, desde lo alto del Coliseo"),
        f("J'ai eu connaissance des affaires de Rome longtemps avant que je l'aie eue de ceux de ma maison: je savais le Capitole et son plan avant que je susse le Louvre, et le Tibre avant la Seine.", "Conocí los asuntos de Roma mucho antes que los de mi casa: sabía el Capitolio y su plano antes de saber el Louvre, y el Tíber antes que el Sena.", "Michel de Montaigne, Ensayos III, 9, «De la vanidad»"),
        f("Soleva Roma, che 'l buon mondo feo, / due soli aver, che l'una e l'altra strada / facean vedere, e del mondo e di Deo.", "Solía Roma, que hizo bueno al mundo, tener dos soles, que mostraban uno y otro camino: el del mundo y el de Dios.", "Dante, Purgatorio XVI, 106-108, sobre el papa y el emperador"),
        f(null, "Cuando estoy en Roma, ayuno el sábado; cuando estoy aquí, no ayuno.", "Ambrosio de Milán, según Agustín, Carta 54 a Januario, 3; de ahí «donde fueres, haz lo que vieres»"),
        f("C'è un'Ape che se posa / su un bottone de rosa: / lo succhia e se ne va... / Tutto sommato, la felicità / è una piccola cosa.", "Hay una abeja que se posa en un botón de rosa: lo chupa y se va… Al fin y al cabo, la felicidad es una cosa pequeña.", "Trilussa, «Felicità», en romanesco"),
        f("Me spiego: da li conti che se fanno / secondo le statistiche d'adesso / risurta che te tocca un pollo all'anno: / e, se nun entra ne le spese tue, / t'entra ne la statistica lo stesso / perché c'è un antro che ne magna due.", "Me explico: según las cuentas que se hacen con las estadísticas de hoy, te toca un pollo al año; y si no entra en tus gastos, entra en la estadística igual, porque hay otro que se come dos.", "Trilussa, «La Statistica», en romanesco (Tutte le poesie, ed. de Pietro Pancrazi)"),
        f("Roma de travertino, / rifatta de cartone, / saluta l'imbianchino, / suo prossimo padrone.", "Roma de travertino, rehecha de cartón, saluda al pintor de brocha gorda, su próximo amo.", "Atribuido a Trilussa, en la visita de Hitler a Roma, mayo de 1938"),
        f("Io ho deliberato di lasciare un monumento di quello che oggi è la plebe di Roma.", "He decidido dejar un monumento de lo que hoy es el pueblo llano de Roma.", "Giuseppe Gioachino Belli, introducción a los Sonetos romanescos, 1831"),
        f("L'ommini de sto monno sò ll'istesso / che vvaghi de caffè nner mascinino.", "Los hombres de este mundo son lo mismo que los granos de café en el molinillo.", "Giuseppe Gioachino Belli, «Er caffettiere fisolofo», soneto 211"),
        f("La morte sta anniscosta in ne l'orloggi; / e ggnisuno pò ddí: ddomani ancora.", "La muerte está escondida en los relojes, y nadie puede decir: mañana también.", "Giuseppe Gioachino Belli, «La golaccia», soneto 364"),
        f("Stupenda e misera città.", "Estupenda y mísera ciudad.", "Pier Paolo Pasolini, «El llanto de la excavadora», 1956, en Las cenizas de Gramsci"),
        f("Urbem fecisti quod prius orbis erat.", "Hiciste una ciudad de lo que antes era el mundo.", "Rutilio Namaciano, Sobre su regreso I, 66, año 417"),
        f("Urbem… excoluit adeo, ut iure sit gloriatus marmoream se relinquere, quam latericiam accepisset.", "Embelleció tanto la ciudad que pudo presumir con razón de dejarla de mármol, cuando la había recibido de ladrillo.", "Suetonio, Vida de Augusto 28"),
        f("Rome ne fu pas faite toute en un jour.", "Roma no se hizo toda en un día.", "Proverbio; su primer registro es francés, en Li Proverbe au vilain, hacia 1190"),
        f("Lasciami tutte le rughe, non me ne togliere nemmeno una: ci ho messo una vita a farmele.", "Déjame todas las arrugas, no me quites ni una: me llevó una vida entera hacérmelas.", "Atribuida a Anna Magnani, a quien la maquillaba"),
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

    val NEON = listOf(
        f("The sky above the port was the color of television, tuned to a dead channel.", "El cielo sobre el puerto tenía el color de un televisor sintonizado en un canal muerto.", "William Gibson, Neuromante, 1984"),
        f("Night City was like a deranged experiment in social Darwinism, designed by a bored researcher who kept one thumb permanently on the fast-forward button.",
            "Night City era como un experimento demente de darwinismo social, diseñado por un investigador aburrido que tenía el pulgar siempre en el botón de avance rápido.", "William Gibson, Neuromante, 1984"),
        f("The street finds its own uses for things.", "La calle encuentra sus propios usos para las cosas.", "William Gibson, «Quemando cromo», 1982"),
        f("I've seen things you people wouldn't believe.", "Yo he visto cosas que ustedes no creerían.", "Roy Batty (Rutger Hauer), Blade Runner, 1982"),
        f("All those moments will be lost in time, like tears in rain.", "Todos esos momentos se perderán en el tiempo, como lágrimas en la lluvia.", "Roy Batty (Rutger Hauer), Blade Runner, 1982"),
        f("Mittler zwischen Hirn und Händen muss das Herz sein!", "¡El mediador entre el cerebro y las manos ha de ser el corazón!", "Metrópolis, de Fritz Lang y Thea von Harbou, 1927"),
        f("Stadtluft macht frei.", "El aire de la ciudad hace libre.", "Principio del derecho de las ciudades alemanas en la Edad Media"),
        f("住めば都", "Donde vives, ahí está la capital.", "Proverbio japonés (sumeba miyako)"),
        f("What is the city but the people?", "¿Qué es la ciudad sino la gente?", "William Shakespeare, Coriolano III, 1"),
        f("Cities have the capability of providing something for everybody, only because, and only when, they are created by everybody.",
            "Las ciudades pueden dar algo a todos solo porque, y solo cuando, las hacen todos.", "Jane Jacobs, Muerte y vida de las grandes ciudades, 1961"),
        f("Lowly, unpurposeful and random as they may appear, sidewalk contacts are the small change from which a city's wealth of public life may grow.",
            "Por humildes, casuales y sin propósito que parezcan, los encuentros en la banqueta son el cambio menudo del que crece la riqueza de la vida pública de una ciudad.", "Jane Jacobs, Muerte y vida de las grandes ciudades, 1961"),
        f("The city is not a concrete jungle, it is a human zoo.", "La ciudad no es una jungla de concreto: es un zoológico humano.", "Desmond Morris, El zoo humano, 1969"),
        f("The city is a fact in nature, like a cave, a run of mackerel or an ant-heap.", "La ciudad es un hecho de la naturaleza, como una cueva, un banco de caballas o un hormiguero.", "Lewis Mumford, La cultura de las ciudades, 1938"),
        f("Di una città non godi le sette o le settantasette meraviglie, ma la risposta che dà a una tua domanda.",
            "De una ciudad no disfrutas las siete o setenta y siete maravillas, sino la respuesta que da a una pregunta tuya.", "Italo Calvino, Las ciudades invisibles, 1972"),
        f("Le città come i sogni sono costruite di desideri e di paure.", "Las ciudades, como los sueños, están hechas de deseos y de miedos.", "Italo Calvino, Las ciudades invisibles, 1972"),
        f("La forme d'une ville change plus vite, hélas ! que le cœur d'un mortel.", "La forma de una ciudad cambia más deprisa, ¡ay!, que el corazón de un mortal.", "Charles Baudelaire, «El cisne», Las flores del mal, 1861"),
        f("Multitude, solitude : termes égaux et convertibles pour le poète actif et fécond.", "Multitud, soledad: términos iguales e intercambiables para el poeta activo y fecundo.", "Charles Baudelaire, «Las multitudes», El spleen de París, 1869"),
        f("Sich in einer Stadt nicht zurechtzufinden heißt nicht viel. In einer Stadt sich aber zu verirren, wie man in einem Walde sich verirrt, braucht Schulung.",
            "No orientarse en una ciudad no significa gran cosa. Pero perderse en una ciudad como uno se pierde en un bosque requiere aprendizaje.", "Walter Benjamin, Infancia en Berlín hacia 1900"),
        f(null, "La aurora de Nueva York tiene cuatro columnas de cieno y un huracán de negras palomas que chapotean las aguas podridas.", "Federico García Lorca, «La aurora», Poeta en Nueva York, 1940"),
        f("The street is the river of life of the city.", "La calle es el río de la vida de la ciudad.", "William H. Whyte, City: Rediscovering the Center, 1988"),
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

    /**
     * Egipto: las enseñanzas (sebayt) y los himnos van en la transliteración egiptológica, la manera de escribir el egipcio
     * con letras latinas; luego, lo que griegos, latinos y modernos dijeron de Egipto, cada uno en su lengua.
     */
    val EGIPTO = listOf(
        f("dgꜣ mdt nfrt r wꜣḏ, iw gm st m-ꜥ ḥmwt ḥr bnwt", "La palabra buena es más rara que la piedra verde, y sin embargo se encuentra entre las criadas que muelen el grano.", "Enseñanza de Ptahhotep, máxima 1 (papiro Prisse)"),
        f("ir mꜣꜥt n nb mꜣꜥt", "Haz justicia para el señor de la justicia.", "Las quejas del campesino elocuente"),
        f("ir hrw nfr, m wrd im.f", "Celebra el día feliz, y no te canses de él.", "Canción del arpista de la tumba del rey Intef"),
        f("n ir.i isft r rmṯ", "No he hecho mal a la gente.", "Libro de los Muertos, capítulo 125, la declaración de inocencia"),
        f("wbn.k nfr m ꜣḫt nt pt, pꜣ itn ꜥnḫ, šꜣꜥ ꜥnḫ", "Te alzas hermoso en el horizonte del cielo, Atón vivo, principio de la vida.", "Gran himno a Atón, tumba de Ay en Amarna"),
        f("mk nn iꜣwt nn ḥry.s, wpw ḥr sš, ntf pw ḥry", "Mira: no hay oficio sin un jefe encima, salvo el de escriba: él es el jefe.", "Enseñanza de Jety (la «sátira de los oficios»)"),
        f("ꜥnḫ wḏꜣ snb", "Vida, prosperidad, salud.", "Fórmula que sigue al nombre del rey en inscripciones y cartas"),
        f("ἔστι Αἰγυπτίοισι ἐπίκτητός τε γῆ καὶ δῶρον τοῦ ποταμοῦ", "Para los egipcios es una tierra ganada y un regalo del río.", "Heródoto, Historias II, 5"),
        f("θεοσεβέες δὲ περισσῶς ἐόντες μάλιστα πάντων ἀνθρώπων", "Son piadosos en exceso, más que todos los demás hombres.", "Heródoto, Historias II, 37"),
        f("Αἰγύπτιοι ἅμα τῷ οὐρανῷ τῷ κατὰ σφέας ἐόντι ἑτεροίῳ καὶ τῷ ποταμῷ φύσιν ἀλλοίην παρεχομένῳ ἢ οἱ ἄλλοι ποταμοί, τὰ πολλὰ πάντα ἔμπαλιν τοῖσι ἄλλοισι ἀνθρώποισι ἐστήσαντο ἤθεά τε καὶ νόμους", "Los egipcios, con un cielo distinto y un río de otra naturaleza que los demás, se dieron costumbres y leyes casi en todo contrarias a las del resto de los hombres.", "Heródoto, Historias II, 35"),
        f("ὦ Σόλων, Σόλων, Ἕλληνες ἀεὶ παῖδές ἐστε, γέρων δὲ Ἕλλην οὐκ ἔστιν", "Solón, Solón, los griegos son siempre niños: no hay griego viejo.", "Un sacerdote de Sais, en el Timeo de Platón (22b)"),
        f("ψυχῆς ἰατρεῖον", "Clínica del alma.", "Inscripción de la biblioteca sagrada de Tebas, según Diodoro de Sicilia, Biblioteca histórica I, 49"),
        f("ἐγώ εἰμι πᾶν τὸ γεγονὸς καὶ ὂν καὶ ἐσόμενον, καὶ τὸν ἐμὸν πέπλον οὐδείς πω θνητὸς ἀπεκάλυψεν", "Yo soy todo lo que ha sido, es y será, y ningún mortal ha levantado aún mi velo.", "Inscripción de Isis en Sais, según Plutarco, Sobre Isis y Osiris 9"),
        f("cum chartae usu maxime humanitas vitae constet, certe memoria", "Del uso del papiro depende en gran parte la vida civilizada y, sin duda, la memoria.", "Plinio el Viejo, Historia natural XIII, 68"),
        f("Quis nescit, Volusi Bithynice, qualia demens Aegyptos portenta colat?", "¿Quién no sabe, Volusio de Bitinia, qué monstruos adora el Egipto delirante?", "Juvenal, Sátiras XV, 1-2"),
        f("Je tiens l'affaire !", "¡Lo tengo!", "Atribuida a Champollion, al descifrar los jeroglíficos, en 1822"),
        f("Soldats, du haut de ces pyramides, quarante siècles vous contemplent.", "Soldados, desde lo alto de estas pirámides, cuarenta siglos os contemplan.", "Atribuida a Napoleón Bonaparte, antes de la batalla de las Pirámides, 1798"),
        f("Look on my Works, ye Mighty, and despair!", "¡Mirad mis obras, poderosos, y desesperad!", "Percy Bysshe Shelley, «Ozymandias», 1818"),
        f("Nothing beside remains.", "Nada más queda.", "Percy Bysshe Shelley, «Ozymandias», 1818"),
        f("Yes, wonderful things.", "Sí, cosas maravillosas.", "Howard Carter, al asomarse a la tumba de Tutankamón; La tumba de Tut-ankh-Amen I, 1923"),
        f("الإنسان يخشى الزمن، والزمن يخشى الأهرام", "El hombre teme al tiempo, y el tiempo teme a las pirámides.", "Proverbio árabe tradicional"),
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

    /** Templario: la regla, el salterio de las vigilias, los cantares de la frontera y el elogio de san Bernardo a la orden. */
    val TEMPLARIO = listOf(
        f("Non nobis, Domine, non nobis, sed nomini tuo da gloriam.", "No a nosotros, Señor, no a nosotros, sino a tu nombre da la gloria.", "Salmo 113 (115), 1, Vulgata; lema de la orden del Temple"),
        f("Militia est vita hominis super terram.", "Milicia es la vida del hombre sobre la tierra.", "Job 7, 1, Vulgata"),
        f("Nisi Dominus custodierit civitatem, frustra vigilat qui custodit eam.", "Si el Señor no guarda la ciudad, en vano vela el centinela.", "Salmo 126 (127), 1, Vulgata"),
        f("Custos, quid de nocte? Custos, quid de nocte?", "Centinela, ¿qué hay de la noche? Centinela, ¿qué hay de la noche?", "Isaías 21, 11, Vulgata"),
        f("Dixit custos: Venit mane, et nox.", "Dijo el centinela: Viene la mañana, y también la noche.", "Isaías 21, 12, Vulgata"),
        f("Scuto circumdabit te veritas eius: non timebis a timore nocturno, a sagitta volante in die.", "Su verdad te rodeará como un escudo: no temerás el espanto de la noche ni la flecha que vuela de día.", "Salmo 90 (91), 5, Vulgata"),
        f("Media nocte surgebam ad confitendum tibi.", "A medianoche me levantaba a alabarte.", "Salmo 118 (119), 62, Vulgata; el verso en que se funda el rezo nocturno de maitines"),
        f("A custodia matutina usque ad noctem speret Israel in Domino.", "Desde la vela de la mañana hasta la noche, espere Israel en el Señor.", "Salmo 129 (130), 6, Vulgata"),
        f("Vigilate et orate, ut non intretis in tentationem.", "Velad y orad, para que no caigáis en la tentación.", "Mateo 26, 41, Vulgata"),
        f("State ergo succincti lumbos vestros in veritate, et induti loricam iustitiae.", "Estad, pues, firmes, ceñida la cintura con la verdad y vestidos con la coraza de la justicia.", "Efesios 6, 14, Vulgata"),
        f("Melior est patiens viro forti, et qui dominatur animo suo expugnatore urbium.", "Más vale el paciente que el fuerte, y el que domina su ánimo que el que conquista ciudades.", "Proverbios 16, 32, Vulgata"),
        f("Tempus belli, et tempus pacis.", "Un tiempo de guerra y un tiempo de paz.", "Eclesiastés 3, 8, Vulgata"),
        f("Benedictus Dominus Deus meus, qui docet manus meas ad proelium, et digitos meos ad bellum.", "Bendito el Señor, mi Dios, que adiestra mis manos para la lucha y mis dedos para la batalla.", "Salmo 143 (144), 1, Vulgata"),
        f("Impavidus profecto miles, et omni ex parte securus, qui ut corpus ferri, sic animum fidei lorica induitur.",
            "Intrépido y seguro por todas partes es el caballero que, así como viste su cuerpo con la cota de hierro, viste su alma con la coraza de la fe.", "Bernardo de Claraval, Elogio de la nueva milicia I, 1, c. 1130"),
        f("At vero Christi milites securi proeliantur proelia Domini sui.", "Los soldados de Cristo, en cambio, pelean seguros las batallas de su Señor.", "Bernardo de Claraval, Elogio de la nueva milicia III, 4, c. 1130"),
        f("Numquam compti, raro loti, magis autem neglecto crine hispidi, pulvere foedi, lorica et caumate fusci.",
            "Nunca peinados, rara vez lavados, más bien desgreñados por el pelo descuidado, sucios de polvo, morenos por la cota y el calor.", "Bernardo de Claraval, Elogio de la nueva milicia IV, 7, c. 1130"),
        f("Igitur qui desiderat pacem, praeparet bellum.", "Así pues, quien desea la paz, que prepare la guerra.", "Vegecio, Compendio del arte militar III, prólogo, c. 390; lo leían los caballeros de la Edad Media"),
        f("Halt sunt li pui e li val tenebrus.", "Altos son los montes y tenebrosos los valles.", "Cantar de Roldán, v. 814, c. 1100"),
        f("Rollant est proz e Oliver est sage.", "Roldán es valiente y Oliveros es sabio.", "Cantar de Roldán, v. 1093, c. 1100"),
        f("¡Dios, qué buen vassallo, si oviesse buen señor!", "¡Dios, qué buen vasallo, si tuviera buen señor!", "Cantar de mio Cid, v. 20, c. 1200"),
        f("De los sos ojos tan fuertemientre llorando, / tornava la cabeça e estávalos catando.", "Llorando de sus ojos con tanta fuerza, volvía la cabeza y se quedaba mirándolos.", "Cantar de mio Cid, vv. 1-2, c. 1200"),
        f("Defensores son uno de los tres estados por que Dios quiso que se mantuviese el mundo.", "Los defensores son uno de los tres estados con que Dios quiso que se sostuviera el mundo.", "Alfonso X, Siete Partidas II, título XXI, c. 1265"),
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
        f("All this will not be finished in the first one hundred days. Nor will it be finished in the first one thousand days, nor in the life of this Administration, nor even perhaps in our lifetime on this planet. But let us begin.", "Todo esto no se terminará en los primeros cien días. Tampoco en los primeros mil días, ni en la vida de este gobierno, ni quizá en nuestra vida en este planeta. Pero empecemos.", "John F. Kennedy, discurso inaugural, 20 de enero de 1961"),
        f("Meanwhile, never flinch, never weary, never despair.", "Mientras tanto, nunca retrocedas, nunca te canses, nunca desesperes.", "Winston Churchill, último gran discurso en la Cámara de los Comunes, 1 de marzo de 1955"),
        f("The only limit to our realization of tomorrow will be our doubts of today.", "El único límite para lo que logremos mañana serán nuestras dudas de hoy.", "Franklin D. Roosevelt, discurso para el Día de Jefferson, escrito para el 13 de abril de 1945 y nunca pronunciado"),
        f("You must do the thing you think you cannot do.", "Tienes que hacer aquello que crees que no puedes hacer.", "Eleanor Roosevelt, Aprender viviendo (You Learn by Living), 1960"),
        f("Beim Menschen ist es wie beim Velo. Nur wenn er fährt, kann er bequem die Balance halten.", "Con las personas pasa como con la bicicleta: solo mientras avanzan pueden mantener bien el equilibrio.", "Albert Einstein, carta a su hijo Eduard, 5 de febrero de 1930"),
        f("We — you and I, and our government — must avoid the impulse to live only for today, plundering for our own ease and convenience the precious resources of tomorrow.", "Debemos evitar el impulso de vivir solo para hoy, saqueando para nuestra comodidad los valiosos recursos del mañana.", "Dwight D. Eisenhower, discurso de despedida, 17 de enero de 1961"),
        f("Mät aldrig bergets höjd förrän du nått toppen. Då skall du se hur lågt det var.", "No midas nunca la altura de una montaña hasta haber llegado a la cima. Entonces verás qué baja era.", "Dag Hammarskjöld, Marcas en el camino (Vägmärken), anotación de 1925–1930, publicada en 1963"),
        f("Good night, and good luck.", "Buenas noches, y buena suerte.", "Edward R. Murrow, despedida de sus programas de radio y televisión en la CBS"),
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
        f(null, "No hay nada más que el propósito único del momento presente. Toda la vida de una persona es una sucesión de momento tras momento.", "Yamamoto Tsunetomo, Hagakure (dictado entre 1709 y 1716)"),
        f(null, "Presta atención incluso a las cosas pequeñas. No hagas nada que no sirva.", "Miyamoto Musashi, El libro de los cinco anillos, libro de la Tierra (reglas octava y novena), 1645"),
        f("七転び八起き", "Caer siete veces, levantarse ocho.", "Proverbio japonés"),
        f(null, "Son dignos de alabanza; sin embargo, quien hace más vale más.", "Geoffroi de Charny, Libro de la caballería, hacia 1350, estribillo que repite a lo largo de la obra (algunos estudiosos lo atribuyen a su hijo homónimo)"),
        f("Non nobis, Domine, non nobis, sed nomini tuo da gloriam.", "No a nosotros, Señor, no a nosotros, sino a tu nombre da la gloria.", "Salmo 113 (115), 1, Vulgata; según la tradición, divisa de los templarios"),
        f(null, "La diligencia es madre de la buena ventura, y la pereza, su contraria, jamás llegó al término que pide un buen deseo.", "Miguel de Cervantes, Don Quijote II, 43"),
        f(null, "Paciencia y barajar.", "Miguel de Cervantes, Don Quijote II, 23 (lo dice Durandarte en la cueva de Montesinos)"),
        f("Nemo facere metuit quod se bene didicisse confidit.", "Nadie teme hacer lo que está seguro de haber aprendido bien.", "Vegecio, Compendio de técnica militar I, 1"),
        f("Nihil enim est quod non adsidua meditatio facillimum reddat.", "No hay nada que la práctica constante no vuelva facilísimo.", "Vegecio, Compendio de técnica militar I, 19"),
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
        f(null, "Toda obra grande es el fruto de la paciencia y de la perseverancia, combinadas con una atención orientada tenazmente, durante meses y aun años, hacia un objeto particular.", "Santiago Ramón y Cajal, Reglas y consejos sobre investigación científica, cap. III, 1897"),
        f("Douter de tout ou tout croire, ce sont deux solutions également commodes, qui l'une et l'autre nous dispensent de réfléchir.", "Dudar de todo o creerlo todo son dos soluciones igual de cómodas: las dos nos ahorran pensar.", "Henri Poincaré, La ciencia y la hipótesis, introducción, 1902"),
        f("False facts are highly injurious to the progress of science, for they often endure long; but false views, if supported by some evidence, do little harm, for every one takes a salutary pleasure in proving their falseness.", "Los hechos falsos dañan mucho al progreso de la ciencia, porque suelen durar mucho; pero las ideas falsas, si se apoyan en alguna evidencia, hacen poco daño, porque todos sienten un placer saludable en demostrar que son falsas.", "Charles Darwin, El origen del hombre, cap. XXI, 1871"),
        f(null, "Hay que hacer de la vida un sueño, y de un sueño, una realidad.", "Pierre Curie, de su diario de juventud, citado por Marie Curie en Pierre Curie, 1923"),
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
        f("Il n'y a que deux puissances dans le monde, le sabre et l'esprit. […] À la longue, le sabre est toujours battu par l'esprit.", "Solo hay dos poderes en el mundo: el sable y el espíritu. A la larga, el sable siempre es vencido por el espíritu.", "Napoleón a Fontanes, entre Jena y Wagram, según la noticia de Sainte-Beuve en las Obras de Fontanes, 1839"),
        f("Les vraies conquêtes, les seules qui ne donnent aucun regret, sont celles que l'on fait sur l'ignorance.", "Las verdaderas conquistas, las únicas que no dejan ningún pesar, son las que se hacen sobre la ignorancia.", "Bonaparte, carta a Camus al ser elegido miembro del Instituto de Francia, diciembre de 1797"),
        f("J'écris principalement pour rendre compte de moi à moi-même.", "Escribo, sobre todo, para rendirme cuentas de mí a mí mismo.", "Chateaubriand, manuscrito de 1826 de las Memorias de ultratumba"),
        f("Je désire que mes cendres reposent sur les bords de la Seine, au milieu de ce peuple français que j'ai tant aimé.", "Deseo que mis cenizas reposen a orillas del Sena, en medio de ese pueblo francés al que tanto he amado.", "Napoleón, testamento, art. 2, Santa Elena, 15 de abril de 1821"),
    )

    /** Art déco: los años veinte y su orilla, de París a Nueva York (novela, poesía, cabaret, jazz y arquitectura). */
    val DECO = listOf(
        f("So we beat on, boats against the current, borne back ceaselessly into the past.", "Y así seguimos, botes contra la corriente, arrastrados sin cesar hacia el pasado.", "F. Scott Fitzgerald, El gran Gatsby, 1925 (la última línea)"),
        f("Life starts all over again when it gets crisp in the fall.", "La vida vuelve a empezar cuando refresca en otoño.", "F. Scott Fitzgerald, El gran Gatsby, cap. VII, 1925"),
        f("In his blue gardens men and girls came and went like moths among the whisperings and the champagne and the stars.",
            "En sus jardines azules, hombres y muchachas iban y venían como polillas entre los susurros, el champán y las estrellas.", "F. Scott Fitzgerald, El gran Gatsby, cap. III, 1925"),
        f("It was an age of miracles, it was an age of art, it was an age of excess, and it was an age of satire.",
            "Fue una época de milagros, una época de arte, una época de excesos y una época de sátira.", "F. Scott Fitzgerald, «Ecos de la era del jazz», 1931"),
        f("Mrs. Dalloway said she would buy the flowers herself.", "La señora Dalloway dijo que ella misma compraría las flores.", "Virginia Woolf, La señora Dalloway, 1925 (la primera línea)"),
        f("She had a perpetual sense, as she watched the taxi cabs, of being out, out, far out to sea and alone.",
            "Tenía siempre la sensación, mientras miraba pasar los taxis, de estar lejos, lejos, mar adentro y sola.", "Virginia Woolf, La señora Dalloway, 1925"),
        f("One cannot think well, love well, sleep well, if one has not dined well.", "No se puede pensar bien, amar bien ni dormir bien si no se ha cenado bien.", "Virginia Woolf, Una habitación propia, 1929"),
        f("You can't get away from yourself by moving from one place to another.", "Uno no se escapa de sí mismo yéndose de un lugar a otro.", "Ernest Hemingway, Fiesta, cap. II, 1926"),
        f("If you are lucky enough to have lived in Paris as a young man, then wherever you go for the rest of your life, it stays with you, for Paris is a moveable feast.",
            "Si tienes la suerte de haber vivido en París de joven, vayas adonde vayas el resto de tu vida, París te acompaña, porque París es una fiesta que se lleva dentro.", "Ernest Hemingway, epígrafe de París era una fiesta (de una carta de 1950)"),
        f("Une maison est une machine à habiter.", "Una casa es una máquina para habitar.", "Le Corbusier, Hacia una arquitectura, 1923"),
        f(null, "La aurora de Nueva York tiene cuatro columnas de cieno y un huracán de negras palomas que chapotean las aguas podridas.", "Federico García Lorca, «La aurora», Poeta en Nueva York (escrito en 1929-1930)"),
        f("It don't mean a thing if it ain't got that swing.", "No vale nada si no tiene swing.", "Duke Ellington e Irving Mills, canción de 1931"),
        f("J'ai deux amours, mon pays et Paris.", "Tengo dos amores: mi país y París.", "«J'ai deux amours», la canción que estrenó Josephine Baker en el Casino de París, 1930"),
        f("La mode se démode, le style jamais.", "La moda pasa de moda; el estilo, jamás.", "Atribuida a Coco Chanel"),
        f("Life is a tragedy when seen in close-up, but a comedy in long-shot.", "La vida es una tragedia vista en primer plano, pero una comedia en plano general.", "Atribuida a Charles Chaplin"),
        f("Rose is a rose is a rose is a rose.", "Una rosa es una rosa es una rosa es una rosa.", "Gertrude Stein, «Sacred Emily», 1913 (publicado en Geografía y obras, 1922)"),
        f("Le vent se lève !… Il faut tenter de vivre !", "¡Se levanta el viento!… ¡Hay que intentar vivir!", "Paul Valéry, El cementerio marino, 1920"),
        f("Les vrais paradis sont les paradis qu'on a perdus.", "Los verdaderos paraísos son los paraísos que se han perdido.", "Marcel Proust, El tiempo recobrado, 1927"),
        f(null, "Yo soy yo y mi circunstancia, y si no la salvo a ella no me salvo yo.", "José Ortega y Gasset, Meditaciones del Quijote, 1914"),
        f("Well, less is more, Lucrezia.", "Pues bien, menos es más, Lucrezia.", "Robert Browning, «Andrea del Sarto», 1855; el lema lo hizo suyo el arquitecto Ludwig Mies van der Rohe"),
        f("If you have to ask what jazz is, you'll never know.", "Si tienes que preguntar qué es el jazz, nunca lo sabrás.", "Atribuida a Louis Armstrong"),
        f("The most effective way to do it, is to do it.", "La manera más eficaz de hacerlo es hacerlo.", "Atribuida a Amelia Earhart"),
    )

    /** Tierras Antiguas: dichos propios del tema, escritos para la app (no citan ninguna obra). */
    val ANTIGUAS = listOf(
        f(null, "El camino no se acorta mirándolo; se acorta andándolo.", "Dicho de los caminantes"),
        f(null, "Piedra a piedra se alza la torre, y nadie recuerda cuál fue la primera.", "Dicho de los canteros"),
        f(null, "Quien lleva farol no teme a la noche, pero tampoco la mira.", "Dicho de los guardas del paso"),
        f(null, "Lo que se escribe en la crónica ya no se pierde en la niebla.", "Dicho de los cronistas"),
        f(null, "El río no discute con la roca: la rodea y sigue.", "Dicho de los barqueros"),
        f(null, "Una lumbre pequeña calienta más que un incendio lejano.", "Dicho de las posadas"),
        f(null, "No preguntes al bosque cuánto falta; pregúntale a tus pies.", "Dicho de los caminantes"),
        f(null, "El que guarda las semillas guarda el verano.", "Dicho de los labradores del valle"),
        f(null, "Toda puerta cerrada fue antes un muro con paciencia.", "Dicho de los canteros"),
        f(null, "Descansar también es parte del viaje.", "Dicho de las posadas"),
        f(null, "La montaña no se mueve, pero cada día la subes distinto.", "Dicho de los pastores altos"),
        f(null, "El mapa dice adónde ir; la libreta dice quién fuiste.", "Dicho de los cronistas"),
        f(null, "Un paso firme vale más que diez apresurados.", "Dicho de los guardas del paso"),
        f(null, "Lo que hoy cuesta, mañana se cuenta junto al fuego.", "Dicho de las posadas"),
        f(null, "Hasta la noche más larga termina en alba.", "Dicho de los vigías"),
        f(null, "Quien escucha al viento sabe cuándo plegar la vela.", "Dicho de los barqueros"),
        f(null, "No hay tesoro que pese menos que una costumbre buena.", "Dicho de los labradores del valle"),
        f(null, "La luna cambia de cara, y nadie la llama falsa.", "Dicho de los vigías"),
        f(null, "El que siembra a la sombra cosecha despacio, pero cosecha.", "Dicho de los labradores del valle"),
        f(null, "Cada piedra del camino fue puesta por alguien que no lo vería terminado.", "Dicho de los canteros"),
        f(null, "La niebla esconde el sendero, no lo borra.", "Dicho de los pastores altos"),
        f(null, "Cuenta tus días como cuentas tus monedas: sin miedo y sin descuido.", "Dicho de los mercaderes"),
        f(null, "Un día sin gesta no es un día perdido; es un día de camino.", "Dicho de los caminantes"),
        f(null, "Lo que se nombra se puede cuidar.", "Dicho de los cronistas"),
    )

    val REVOLUCION = listOf(
        f("Les hommes naissent et demeurent libres et égaux en droits.", "Los hombres nacen y permanecen libres e iguales en derechos.", "Declaración de los Derechos del Hombre y del Ciudadano, art. 1, 1789"),
        f("La liberté consiste à pouvoir faire tout ce qui ne nuit pas à autrui.", "La libertad consiste en poder hacer todo lo que no daña a otro.", "Declaración de los Derechos del Hombre y del Ciudadano, art. 4, 1789"),
        f("Liberté, Égalité, Fraternité.", "Libertad, igualdad, fraternidad.", "Divisa de la Revolución"),
        f("Qu'est-ce que le tiers état ? Tout. Qu'a-t-il été jusqu'à présent ? Rien. Que demande-t-il ? À y devenir quelque chose.",
            "¿Qué es el tercer estado? Todo. ¿Qué ha sido hasta ahora? Nada. ¿Qué pide? Llegar a ser algo.", "Emmanuel-Joseph Sieyès, ¿Qué es el tercer estado?, 1789"),
        f("De l'audace, encore de l'audace, toujours de l'audace.", "Audacia, más audacia, siempre audacia.", "Georges Danton, discurso ante la Asamblea, 1792"),
        f("Le bonheur est une idée neuve en Europe.", "La felicidad es una idea nueva en Europa.", "Saint-Just, informe a la Convención, 1794"),
        f("Les femmes ont le droit de monter sur l'échafaud ; elles doivent avoir également celui de monter à la tribune.",
            "La mujer tiene derecho a subir al cadalso; debe tener también el de subir a la tribuna.", "Olympe de Gouges, Declaración de los Derechos de la Mujer, art. 10, 1791"),
        f("La nature n'a marqué aucun terme au perfectionnement des facultés humaines.", "La naturaleza no ha puesto ningún límite al perfeccionamiento de las facultades humanas.",
            "Condorcet, Esbozo de un cuadro histórico de los progresos del espíritu humano, 1795"),
        f("L'homme est né libre, et partout il est dans les fers.", "El hombre nace libre, y en todas partes está encadenado.", "Jean-Jacques Rousseau, El contrato social, 1762"),
        f("Il faut cultiver notre jardin.", "Hay que cultivar nuestro jardín.", "Voltaire, Cándido, 1759"),
        f("Il faut tout examiner, tout remuer sans exception et sans ménagement.", "Hay que examinarlo todo, removerlo todo, sin excepción y sin miramientos.", "Denis Diderot, artículo «Enciclopedia», 1755"),
        f("Habe Muth, dich deines eigenen Verstandes zu bedienen!", "¡Ten el valor de servirte de tu propio entendimiento!", "Immanuel Kant, ¿Qué es la Ilustración?, 1784"),
        f("We have it in our power to begin the world over again.", "Está en nuestras manos empezar el mundo de nuevo.", "Thomas Paine, El sentido común, 1776"),
        f("Bliss was it in that dawn to be alive, but to be young was very heaven!", "Era una dicha estar vivo en aquel amanecer, ¡y ser joven era el cielo mismo!", "William Wordsworth, sobre 1789, en El preludio"),
        f("Nous sommes ici par la volonté du peuple.", "Estamos aquí por la voluntad del pueblo.", "Atribuida a Mirabeau, 23 de junio de 1789"),
        f("Rien ne se perd, rien ne se crée, tout se transforme.", "Nada se pierde, nada se crea, todo se transforma.", "Atribuida a Lavoisier, a partir de su Tratado elemental de química, 1789"),
        f("Notre histoire n'est pas notre code.", "Nuestra historia no es nuestro código.", "Rabaut Saint-Étienne, 1788"),
        f("Après le pain, l'éducation est le premier besoin du peuple.", "Después del pan, la educación es la primera necesidad del pueblo.", "Georges Danton, discurso sobre la instrucción pública ante la Convención, 13 de agosto de 1793"),
        f("La libre communication des pensées et des opinions est un des droits les plus précieux de l'homme : tout citoyen peut donc parler, écrire, imprimer librement…", "La libre comunicación de los pensamientos y de las opiniones es uno de los derechos más preciosos del hombre: todo ciudadano puede, pues, hablar, escribir e imprimir libremente…", "Declaración de los Derechos del Hombre y del Ciudadano, art. 11, 1789"),
        f("Une génération ne peut assujettir à ses lois les générations futures.", "Una generación no puede someter a sus leyes a las generaciones futuras.", "Declaración de los Derechos del Hombre y del Ciudadano de 1793, art. 28"),
        f("Ce n'est point ce que l'on a appris qui est utile, mais ce que l'on a retenu, et surtout ce que l'on s'est rendu propre, soit par la réflexion, soit par l'habitude.", "Lo útil no es lo que uno ha aprendido, sino lo que ha retenido, y sobre todo lo que ha hecho suyo, ya sea por la reflexión, ya por la costumbre.", "Condorcet, Cinco memorias sobre la instrucción pública, primera memoria, 1791"),
        f("La plus perdue de toutes les journées est celle où l'on n'a pas ri.", "El más perdido de todos los días es aquel en que no se ha reído.", "Chamfort, máximas sacadas de sus manuscritos, Mercure français, 1795"),
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
        f("Happy, happy Christmas, that can win us back to the delusions of our childish days; that can recall to the old man the pleasures of his youth; that can transport the sailor and the traveller, thousands of miles away, back to his own fireside and his quiet home!", "¡Feliz, feliz Navidad, que puede devolvernos las ilusiones de la infancia, recordarle al anciano los placeres de su juventud y llevar al marinero y al viajero, a miles de millas, de vuelta a su chimenea y a su casa tranquila!", "Charles Dickens, Los papeles póstumos del Club Pickwick, cap. XXVIII, 1837"),
        f("I have always thought of Christmas time, when it has come round… as a good time; a kind, forgiving, charitable, pleasant time; the only time I know of, in the long calendar of the year, when men and women seem by one consent to open their shut-up hearts freely.", "Siempre he pensado en la Navidad, cada vez que llega, como un buen tiempo: amable, de perdón, de caridad, alegre; el único que conozco, en el largo calendario del año, en que hombres y mujeres parecen ponerse de acuerdo para abrir libremente sus corazones cerrados.", "Fred, el sobrino de Scrooge, en Canción de Navidad de Charles Dickens, 1843, estrofa I"),
        f("I don't know what to do! I am as light as a feather, I am as happy as an angel, I am as merry as a schoolboy.", "¡No sé qué hacer! Estoy ligero como una pluma, feliz como un ángel, alegre como un colegial.", "Scrooge, en Canción de Navidad de Charles Dickens, 1843, estrofa V"),
        f("Welcome, everything! Welcome, alike what has been, and what never was, and what we hope may be, to your shelter underneath the holly, to your places round the Christmas fire, where what is sits open-hearted!", "¡Bienvenido todo! Bienvenido por igual lo que fue, lo que nunca fue y lo que esperamos que sea, a vuestro refugio bajo el acebo, a vuestro sitio junto al fuego de Navidad, donde lo que es se sienta con el corazón abierto.", "Charles Dickens, «Lo que es la Navidad a medida que envejecemos», Household Words, 1851"),
        f("Ring out the old, ring in the new, / Ring, happy bells, across the snow: / The year is going, let him go; / Ring out the false, ring in the true.", "Despedid lo viejo, recibid lo nuevo; tañed, campanas alegres, sobre la nieve: el año se va, dejadlo ir; despedid lo falso, recibid lo verdadero.", "Alfred Tennyson, In Memoriam A. H. H. CVI, 1850"),
        f("The hopes and fears of all the years / Are met in thee tonight.", "Las esperanzas y los miedos de todos los años se encuentran en ti esta noche.", "Phillips Brooks, «O Little Town of Bethlehem», 1868"),
        f("What can I give Him, poor as I am? / If I were a shepherd, I would bring a lamb; / If I were a Wise Man, I would do my part; / Yet what I can I give Him: give my heart.", "¿Qué puedo darle, pobre como soy? Si fuera pastor, le traería un cordero; si fuera un sabio de Oriente, haría mi parte; pero lo que puedo, se lo doy: le doy mi corazón.", "Christina Rossetti, «A Christmas Carol» («In the Bleak Midwinter»), 1872"),
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
        f("學而時習之，不亦說乎？", "Aprender y repasar a su tiempo lo aprendido, ¿no es acaso una alegría?", "Confucio, Analectas I, 1"),
        f("逝者如斯夫！不舍晝夜。", "¡Así pasa todo, como este río! No se detiene ni de día ni de noche.", "Confucio, a la orilla de un río, Analectas IX, 17"),
        f("溫故而知新，可以爲師矣。", "Quien repasa lo antiguo y descubre en ello algo nuevo puede ser maestro.", "Confucio, Analectas II, 11"),
        f("合抱之木，生於毫末；九層之臺，起於累土。", "El árbol que apenas abarcan los brazos nació de un brote fino como un pelo; la torre de nueve pisos empezó con un montón de tierra.", "Laozi, Tao Te Ching 64"),
        f("不積跬步，無以至千里；不積小流，無以成江海。", "Sin juntar medio paso tras medio paso no se llega a mil li; sin juntar pequeños arroyos no se forman ríos ni mares.", "Xunzi, «Exhortación al estudio»"),
        f("吾生也有涯，而知也无涯。", "Mi vida tiene orilla; el saber no tiene orilla.", "Zhuangzi, cap. 3, «Lo esencial para nutrir la vida»"),
        f("Ah, fill the Cup: — what boots it to repeat / How Time is slipping underneath our Feet: / Unborn To-morrow and dead Yesterday, / Why fret about them if To-day be sweet!", "Ah, llena la copa: ¿de qué sirve repetir cómo se nos escurre el tiempo bajo los pies? Mañana aún no ha nacido y ayer está muerto: ¿por qué inquietarse por ellos si hoy es dulce?", "Rubaiyat de Omar Jayam, versión de Edward FitzGerald, 1859, XXXVII"),
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

    /** Ciudad verde: ruinas que el bosque recupera, el silencio de después y lo que vuelve a brotar. */
    val RUINAS = listOf(
        f("國破山河在，城春草木深。", "El país está roto; quedan los montes y los ríos. En la ciudad es primavera: hierba y árboles espesos.", "Du Fu, «Vista de primavera», 757"),
        f("夏草や兵どもが夢の跡", "Hierba de verano: lo que queda de los sueños de los guerreros.", "Matsuo Bashō, Sendas de Oku, 1689"),
        f(null, "Estos, Fabio, ¡ay dolor!, que ves ahora campos de soledad, mustio collado, fueron un tiempo Itálica famosa.", "Rodrigo Caro, «Canción a las ruinas de Itálica»"),
        f(null, "Buscas en Roma a Roma, ¡oh peregrino!, y en Roma misma a Roma no la hallas.", "Francisco de Quevedo, «A Roma sepultada en sus ruinas»"),
        f(null, "Huyó lo que era firme, y solamente lo fugitivo permanece y dura.", "Francisco de Quevedo, «A Roma sepultada en sus ruinas»"),
        f(null, "Al olmo viejo, hendido por el rayo y en su mitad podrido, con las lluvias de abril y el sol de mayo algunas hojas verdes le han salido.", "Antonio Machado, «A un olmo seco», 1912"),
        f(null, "Caminante, no hay camino, se hace camino al andar.", "Antonio Machado, Proverbios y cantares XXIX, Campos de Castilla, 1912"),
        f(null, "Volverán las oscuras golondrinas en tu balcón sus nidos a colgar.", "Gustavo Adolfo Bécquer, Rima LIII"),
        f(null, "¿Qué se hizo el rey don Juan? Los infantes de Aragón, ¿qué se hicieron?", "Jorge Manrique, Coplas por la muerte de su padre"),
        f(null, "Generación va, y generación viene: mas la tierra siempre permanece.", "Eclesiastés 1:4, versión Reina-Valera de 1909"),
        f(null, "Tiempo de nacer, y tiempo de morir; tiempo de plantar, y tiempo de arrancar lo plantado.", "Eclesiastés 3:2, versión Reina-Valera de 1909"),
        f(null, "Y en sus palacios crecerán espinas, y ortigas y cardos en sus fortalezas.", "Isaías 34:13, versión Reina-Valera de 1909"),
        f("Round the decay / Of that colossal Wreck, boundless and bare / The lone and level sands stretch far away.",
            "Alrededor de la ruina de aquel naufragio colosal, sin límites y desnudas, las arenas solitarias y llanas se extienden a lo lejos.", "Percy Bysshe Shelley, «Ozymandias», 1818"),
        f("If Winter comes, can Spring be far behind?", "Si llega el invierno, ¿puede estar lejos la primavera?", "Percy Bysshe Shelley, «Oda al viento del oeste», 1820"),
        f("There will come soft rains and the smell of the ground, / And swallows circling with their shimmering sound",
            "Vendrán lluvias suaves y el olor de la tierra, y golondrinas que giran con su sonido brillante.", "Sara Teasdale, «Vendrán lluvias suaves», 1918"),
        f("And Spring herself, when she woke at dawn / Would scarcely know that we were gone.",
            "Y la primavera misma, al despertar con el alba, apenas sabría que ya no estamos.", "Sara Teasdale, «Vendrán lluvias suaves», 1918"),
        f("There was a strange stillness. The birds, for example—where had they gone?", "Había una quietud extraña. Los pájaros, por ejemplo: ¿adónde se habían ido?", "Rachel Carson, Primavera silenciosa, 1962"),
        f("In Wildness is the preservation of the World.", "En lo salvaje está la preservación del mundo.", "Henry David Thoreau, «Caminar», 1862"),
        f("Wo aber Gefahr ist, wächst / Das Rettende auch.", "Pero donde está el peligro crece también lo que salva.", "Friedrich Hölderlin, «Patmos», 1803"),
        f("Au milieu de l'hiver, j'apprenais enfin qu'il y avait en moi un été invincible.", "En medio del invierno aprendí por fin que había en mí un verano invencible.", "Albert Camus, «Regreso a Tipasa», 1952"),
        f("Cercare e saper riconoscere chi e cosa, in mezzo all'inferno, non è inferno, e farlo durare, e dargli spazio.",
            "Buscar y saber reconocer quién y qué, en medio del infierno, no es infierno, y hacer que dure, y darle espacio.", "Italo Calvino, Las ciudades invisibles, 1972"),
        f("“Hope” is the thing with feathers - / That perches in the soul -", "La esperanza es esa cosa con plumas que se posa en el alma.", "Emily Dickinson, poema 314 (edición Franklin)"),
        f("You have to carry the fire.", "Tienes que llevar el fuego.", "Cormac McCarthy, La carretera, 2006"),
        f(null, "Si supiera que el mundo se acaba mañana, hoy mismo plantaría un manzano.", "Atribuida a Martín Lutero; la primera fuente escrita es de 1944"),
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
        f("Quoniam mille anni ante oculos tuos tamquam dies hesterna quae praeteriit.", "Porque mil años ante tus ojos son como el día de ayer, que ya pasó.", "Salmo 89 (90), 4, Vulgata"),
        f("Septies in die laudem dixi tibi.", "Siete veces al día te alabo.", "Salmo 118 (119), 164, Vulgata; la Regla de san Benito (cap. 16) funda en este verso las horas del día"),
        f("Omnia tempus habent, et suis spatiis transeunt universa sub caelo.", "Todo tiene su tiempo, y todas las cosas bajo el cielo pasan en su plazo.", "Eclesiastés 3, 1, Vulgata"),
        f("Magna ista vis est memoriae, magna nimis, deus meus, penetrale amplum et infinitum.", "Grande es esta fuerza de la memoria, grande en exceso, Dios mío: un santuario amplio e infinito.", "San Agustín, Confesiones X, 8, 15"),
        f("Si omni anno unum vitium extirparemus, cito viri perfecti efficeremur.", "Si cada año arrancáramos un solo vicio, pronto llegaríamos a ser perfectos.", "Tomás de Kempis, Imitación de Cristo I, 11"),
        f("Imperfection is in some sort essential to all that we know of life. It is the sign of life in a mortal body, that is to say, of a state of progress and change.", "La imperfección es en cierto modo esencial a todo lo que conocemos de la vida. Es la señal de la vida en un cuerpo mortal, es decir, de un estado de progreso y de cambio.", "John Ruskin, «La naturaleza del gótico», en Las piedras de Venecia II, 1853"),
        f("Experto crede: aliquid amplius invenies in silvis quam in libris. Ligna et lapides docebunt te quod a magistris audire non possis.", "Créele a quien lo ha probado: encontrarás algo más en los bosques que en los libros. Los árboles y las piedras te enseñarán lo que no puedes oír de los maestros.", "Bernardo de Claraval, Carta 106, 2"),
    )

    /** Horror cósmico: lo que se ve en la niebla, el miedo, el abismo y el diario de quien investiga. Todas de dominio público. */
    val ARCANO = listOf(
        f(null, "El sueño de la razón produce monstruos.", "Francisco de Goya, Los caprichos, lámina 43, 1799"),
        f("There are more things in heaven and earth, Horatio, than are dreamt of in your philosophy.",
            "Hay más cosas en el cielo y en la tierra, Horacio, de las que sueña tu filosofía.", "Shakespeare, Hamlet, acto I, escena 5"),
        f("Le silence éternel de ces espaces infinis m'effraie.", "El silencio eterno de estos espacios infinitos me espanta.", "Blaise Pascal, Pensamientos, 1670"),
        f("Und wenn du lange in einen Abgrund blickst, blickt der Abgrund auch in dich hinein.",
            "Y si miras largo tiempo a un abismo, el abismo también mira dentro de ti.", "Friedrich Nietzsche, Más allá del bien y del mal, § 146, 1886"),
        f("All that we see or seem is but a dream within a dream.", "Todo lo que vemos o parecemos no es sino un sueño dentro de un sueño.", "Edgar Allan Poe, «Un sueño dentro de un sueño», 1849"),
        f("Deep into that darkness peering, long I stood there wondering, fearing, doubting, dreaming dreams no mortal ever dared to dream before.",
            "Mirando a fondo esa oscuridad, me quedé largo rato asombrado, temiendo, dudando, soñando sueños que ningún mortal se atrevió a soñar antes.", "Edgar Allan Poe, «El cuervo», 1845"),
        f("They who dream by day are cognizant of many things which escape those who dream only by night.",
            "Los que sueñan de día saben de muchas cosas que se les escapan a los que solo sueñan de noche.", "Edgar Allan Poe, «Eleonora», 1842"),
        f("The boundaries which divide Life from Death are at best shadowy and vague. Who shall say where the one ends, and where the other begins?",
            "Los límites que separan la Vida de la Muerte son, en el mejor de los casos, sombríos y vagos. ¿Quién dirá dónde termina la una y dónde empieza la otra?", "Edgar Allan Poe, «El entierro prematuro», 1844"),
        f("True!—nervous—very, very dreadfully nervous I had been and am; but why will you say that I am mad?",
            "¡Es verdad! Nervioso, muy nervioso, terriblemente nervioso he estado y estoy; pero ¿por qué dirán ustedes que estoy loco?", "Edgar Allan Poe, «El corazón delator», 1843"),
        f("Alone, alone, all, all alone, alone on a wide wide sea!", "¡Solo, solo, del todo solo, solo en un ancho, ancho mar!", "Samuel Taylor Coleridge, La balada del viejo marinero, parte IV (versión de 1817)"),
        f("Like one, that on a lonesome road doth walk in fear and dread, and having once turned round walks on, and turns no more his head; because he knows, a frightful fiend doth close behind him tread.",
            "Como quien por un camino solitario anda con miedo y espanto y, habiéndose vuelto una vez, sigue andando y ya no vuelve la cabeza, porque sabe que un demonio espantoso camina muy cerca detrás de él.",
            "Samuel Taylor Coleridge, La balada del viejo marinero, parte VI (versión de 1817)"),
        f("Yes, as every one knows, meditation and water are wedded for ever.", "Sí, como todo el mundo sabe, la meditación y el agua están casadas para siempre.", "Herman Melville, Moby Dick, capítulo 1, 1851"),
        f("Learn from me, if not by my precepts, at least by my example, how dangerous is the acquirement of knowledge.",
            "Aprende de mí, si no por mis consejos, al menos por mi ejemplo, cuán peligrosa es la adquisición del conocimiento.", "Mary Shelley, Frankenstein, 1818 (capítulo 4 en la edición de 1831)"),
        f(null, "¡Dios mío, qué solos se quedan los muertos!", "Gustavo Adolfo Bécquer, Rimas, LXXIII, 1871"),
        f("Diary, n. A daily record of that part of one's life, which he can relate to himself without blushing.",
            "Diario, s. Registro cotidiano de aquella parte de la vida de uno que puede contarse a sí mismo sin sonrojarse.", "Ambrose Bierce, El diccionario del diablo, 1911"),
        f("Ghost, n. The outward and visible sign of an inward fear.", "Fantasma, s. La señal externa y visible de un miedo interno.", "Ambrose Bierce, El diccionario del diablo, 1911"),
        f("No passion so effectually robs the mind of all its powers of acting and reasoning as fear.",
            "Ninguna pasión despoja tan eficazmente a la mente de todo su poder de obrar y de razonar como el miedo.", "Edmund Burke, Indagación filosófica sobre lo sublime y lo bello, parte II, sección 1, 1757"),
        f("Whatever is in any sort terrible, or is conversant about terrible objects, or operates in a manner analogous to terror, is a source of the sublime.",
            "Todo lo que de algún modo es terrible, o tiene que ver con objetos terribles, u obra de manera parecida al terror, es fuente de lo sublime.", "Edmund Burke, Indagación filosófica sobre lo sublime y lo bello, parte I, sección 7, 1757"),
        f("Man is not truly one, but truly two.", "El hombre no es verdaderamente uno, sino verdaderamente dos.", "Robert Louis Stevenson, El extraño caso del doctor Jekyll y el señor Hyde, 1886"),
        f("Welcome to my house! Enter freely and of your own will!", "¡Bienvenido a mi casa! ¡Entre libremente y por su propia voluntad!", "Bram Stoker, Drácula, capítulo 2, 1897"),
        f("We are such stuff as dreams are made on, and our little life is rounded with a sleep.",
            "Somos de la misma materia que los sueños, y nuestra breve vida está rodeada de un sueño.", "Shakespeare, La tempestad, acto IV, escena 1"),
        f(null, "Que toda la vida es sueño, y los sueños, sueños son.", "Pedro Calderón de la Barca, La vida es sueño, jornada II, 1635"),
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
        f("Ne frustra vixisse videar.", "Que no parezca que he vivido en vano.", "Tycho Brahe, últimas palabras, 1601, según el relato de Kepler recogido por J. L. E. Dreyer, 1890"),
        f("This is a present from a small distant world, a token of our sounds, our science, our images, our music, our thoughts, and our feelings.", "Este es un regalo de un mundo pequeño y lejano: una muestra de nuestros sonidos, nuestra ciencia, nuestras imágenes, nuestra música, nuestros pensamientos y nuestros sentimientos.", "Jimmy Carter, mensaje grabado en las sondas Voyager, 1977"),
        f("We leave as we came and, God willing, as we shall return, with peace and hope for all mankind.", "Nos vamos como vinimos y, si Dios quiere, como volveremos: con paz y esperanza para toda la humanidad.", "Eugene Cernan, Apolo 17, al dejar la Luna, 14 de diciembre de 1972"),
        f("We choose to go to the moon in this decade and do the other things, not because they are easy, but because they are hard.", "Elegimos ir a la Luna en esta década, y hacer las otras cosas, no porque sean fáciles, sino porque son difíciles.", "John F. Kennedy, discurso en la Universidad Rice, 12 de septiembre de 1962"),
        f("Veniet tempus quo ista quae nunc latent in lucem dies extrahat et longioris aevi diligentia.", "Llegará un tiempo en que lo que ahora está oculto lo saquen a la luz los días y la diligencia de una época más larga.", "Séneca, Cuestiones naturales VII, 25, 4, sobre los cometas"),
    )

    val ABISMO = listOf(
        f("La mer est tout ! Elle couvre les sept dixièmes du globe terrestre.", "¡El mar lo es todo! Cubre las siete décimas partes del globo terrestre.", "Julio Verne, Veinte mil leguas de viaje submarino I, 10, 1870 (habla el capitán Nemo)"),
        f("Là seulement est l'indépendance ! Là je ne reconnais pas de maîtres ! Là je suis libre !", "¡Solo allí está la independencia! ¡Allí no reconozco amos! ¡Allí soy libre!", "Julio Verne, Veinte mil leguas de viaje submarino I, 10, 1870 (Nemo, sobre el fondo del mar)"),
        f("Mobilis in mobili.", "Móvil en el elemento móvil.", "Lema del Nautilus en Veinte mil leguas de viaje submarino, de Julio Verne, 1870"),
        f("Yes, as every one knows, meditation and water are wedded for ever.", "Sí, como todo el mundo sabe, la meditación y el agua están casadas para siempre.", "Herman Melville, Moby Dick, cap. 1, 1851"),
        f("There is, one knows not what sweet mystery about this sea, whose gently awful stirrings seem to speak of some hidden soul beneath.",
            "Hay en este mar no se sabe qué dulce misterio, cuyos movimientos, suaves y terribles, parecen hablar de un alma escondida debajo.", "Herman Melville, Moby Dick, cap. 111, «El Pacífico», 1851"),
        f("For all at last return to the sea — to Oceanus, the ocean river, like the ever-flowing stream of time, the beginning and the end.",
            "Porque al final todo vuelve al mar: a Océano, el río océano, como la corriente del tiempo que siempre fluye, el principio y el fin.", "Rachel Carson, El mar que nos rodea, 1951 (la última frase del libro)"),
        f("It was stranger than any imagination could have conceived.", "Era más extraño de lo que cualquier imaginación habría podido concebir.", "William Beebe, Half Mile Down, 1934, sobre la luz a 800 metros desde la batisfera"),
        f("Only dead men have sunk below this.", "Solo los muertos se han hundido más abajo.", "William Beebe, Half Mile Down, 1934"),
        f("Homme libre, toujours tu chériras la mer ! La mer est ton miroir ; tu contemples ton âme dans le déroulement infini de sa lame.",
            "¡Hombre libre, siempre amarás el mar! El mar es tu espejo: contemplas tu alma en el desenvolverse infinito de su ola.", "Charles Baudelaire, «El hombre y el mar», Las flores del mal, 1857"),
        f(null, "Necesito del mar porque me enseña: no sé si aprendo música o conciencia.", "Pablo Neruda, «El mar», Memorial de Isla Negra, 1964"),
        f(null, "Nuestras vidas son los ríos que van a dar en la mar, que es el morir.", "Jorge Manrique, Coplas por la muerte de su padre, 1476"),
        f("Full fathom five thy father lies; of his bones are coral made; those are pearls that were his eyes.",
            "A cinco brazas yace tu padre; de sus huesos se hizo coral; son perlas las que eran sus ojos.", "William Shakespeare, La tempestad, acto I, escena 2 (canción de Ariel)"),
        f("Und wenn du lange in einen Abgrund blickst, blickt der Abgrund auch in dich hinein.", "Y si miras largo tiempo un abismo, el abismo también mira dentro de ti.", "Friedrich Nietzsche, Más allá del bien y del mal, § 146, 1886"),
        f("Omnia flumina intrant mare, et mare non redundat.", "Todos los ríos van al mar, y el mar no se desborda.", "Eclesiastés 1, 7, en la Vulgata"),
        f("Abyssus abyssum invocat in voce cataractarum tuarum.", "Un abismo llama a otro abismo con la voz de tus cascadas.", "Salmo 41, 8, en la Vulgata (42, 7 en la numeración hebrea)"),
        f("Et tenebrae super faciem abyssi, et spiritus Dei ferebatur super aquas.", "Y había tinieblas sobre la faz del abismo, y el espíritu de Dios se movía sobre las aguas.", "Génesis 1, 2, en la Vulgata"),
        f("θάλασσα ὕδωρ καθαρώτατον καὶ μιαρώτατον, ἰχθύσι μὲν πότιμον καὶ σωτήριον, ἀνθρώποις δὲ ἄποτον καὶ ὀλέθριον.",
            "El mar es el agua más pura y la más impura: para los peces, potable y salvadora; para los hombres, imbebible y mortal.", "Heráclito, fragmento 61 (Diels-Kranz), citado por Hipólito"),
        f("We have salt in our blood, in our sweat, in our tears. We are tied to the ocean.", "Tenemos sal en la sangre, en el sudor, en las lágrimas. Estamos atados al océano.",
            "John F. Kennedy, cena de la Copa América, Newport, 14 de septiembre de 1962"),
        f("I know of a cure for everything: salt water.", "Conozco una cura para todo: el agua salada.", "Isak Dinesen, «El diluvio de Norderney», en Siete cuentos góticos, 1934 (sigue: el sudor, las lágrimas o el mar)"),
        f("No water, no life. No blue, no green.", "Sin agua no hay vida. Sin azul no hay verde.", "Sylvia Earle, oceanógrafa, frase que repite en sus charlas por la protección del mar"),
        f("How inappropriate to call this planet Earth when it is quite clearly Ocean.", "Qué poco apropiado llamar Tierra a este planeta, cuando está claro que es Océano.",
            "Atribuida a Arthur C. Clarke; así la citó James Lovelock en la revista Nature, 1990"),
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
        f("לבי במזרח ואנכי בסוף מערב", "Mi corazón está en Oriente, y yo en el extremo de Occidente.", "Yehudá ha-Leví, «Mi corazón está en Oriente», siglo XII"),
        f("ותהיה העת ההיא לב זמנו ופריו, ויהיו שאר עתותיו כדרכים המגיעים אל העת ההיא", "Que ese momento sea el corazón y el fruto de su tiempo, y las demás horas, caminos que llevan hasta él.", "Yehudá ha-Leví, El Cuzarí III, 5, sobre la hora de la oración (versión hebrea de Yehudá ibn Tibón; el original es árabe)"),
        f("لكل شيء إذا ما تم نقصان فلا يغر بطيب العيش إنسان", "Todo lo que llega a su plenitud empieza a menguar: que nadie se deje engañar por la dulzura de la vida.", "Abu l-Baqá al-Rundí, elegía por al-Ándalus, c. 1267"),
        f("תחלת החכמה שתיקה. שניה לה שמיעה. שלישית זכרון. רביעית מעשה. חמישית מורה", "El principio de la sabiduría es el silencio; el segundo paso, escuchar; el tercero, recordar; el cuarto, hacer; el quinto, enseñar.", "Selección de perlas I, 39, atribuida a Salomón ibn Gabirol (versión hebrea de Yehudá ibn Tibón)"),
        f("يجب علينا أن نستعين على ما نحن بسبيله بما قاله من تقدمنا في ذلك، وسواء كان ذلك الغير مشاركا لنا أو غير مشارك في الملة", "Debemos ayudarnos, en lo que emprendemos, de lo que dijeron quienes nos precedieron, compartan o no nuestra fe.", "Averroes, Tratado decisivo, 1179"),
        f("הימים מגלות, כתבו בהם מה שתחפצו שיזכר לכם", "Los días son rollos: escribid en ellos lo que queráis que se recuerde de vosotros.", "Bahya ibn Paquda, Los deberes de los corazones VIII, 3, citando un dicho («ya se ha dicho»); versión hebrea de Yehudá ibn Tibón"),
        f("לפיכך צוו חכמים הראשונים שיהא אדם שם דעותיו תמיד ומשער אותם ומכון אותם בדרך האמצעית", "Por eso mandaron los sabios antiguos que uno revise siempre sus disposiciones, las mida y las encamine por la vía del medio.", "Maimónides, Mishné Torá, Leyes de las disposiciones 1, 4"),
        f("ודע שאלו המעלות והפחיתויות אשר למידות לא יגיעו ולא יתישבו בנפש, רק בכפול הפעולות הבאות מן המידה ההיא פעמים רבות ובזמן ארוך והרגילות בהן", "Sabe que las virtudes y los defectos del carácter no llegan ni se asientan en el alma sino repitiendo muchas veces, durante largo tiempo, los actos que nacen de ellos, hasta acostumbrarse.", "Maimónides, Ocho capítulos 4 (versión hebrea de Samuel ibn Tibón; el original es árabe)"),
        f("עד אימתי חיב ללמד תורה? עד יום מותו… וכל זמן שלא יעסק בלמוד הוא שוכח", "¿Hasta cuándo hay que estudiar? Hasta el día de la muerte… y todo el tiempo que uno deja de estudiar, olvida.", "Maimónides, Mishné Torá, Leyes del estudio de la Torá 1, 10"),
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
        f(null, "Amigo, viniste a mí en un gran apuro, pero los apuros de los hombres son los mismos para todos. Seré tu compañero.", "Bo'orchu al joven Temüjin, Historia secreta de los mongoles §90"),
        f(null, "Ustedes dos, muchachos, sigan viéndose: ¡no se abandonen nunca!", "Naqu Bayan a su hijo Bo'orchu y a Temüjin, Historia secreta de los mongoles §93"),
        f(null, "Los amigos jurados, los dos, comparten una sola vida; no se abandonan: cada uno es la salvaguarda de la vida del otro.", "Dicho de los ancianos de otros tiempos, recordado por Temüjin y Jamuqa, Historia secreta de los mongoles §117"),
        f(null, "Escribe en un registro de letra azul todas las decisiones y haz con ellas un libro. Hasta los hijos de mis hijos, que nadie altere lo escrito en azul sobre papel blanco.", "Gengis Kan a Shigi Qutuqu, Historia secreta de los mongoles §203"),
        f(null, "La caza que uno ha matado solo con la boca no se puede cargar en la montura; la presa abatida solo con palabras no se puede desollar.", "Chagatái ante su padre Gengis Kan, Historia secreta de los mongoles §255"),
        f(null, "He añadido cuatro buenas obras a las de mi padre. Pero, puesto en el gran trono y cargando con mis muchos pueblos, fue falta mía dejarme vencer por el vino.", "Ögödei Kan, haciendo balance de su reinado, Historia secreta de los mongoles §281"),
        f(null, "Diciéndose unos a otros «¡alimentemos a nuestra madre!», se sentaban a la orilla de la madre Onón y, doblando agujas en anzuelos, pescaban salmones y tímalos.", "Los hijos de Hoelún en los años de pobreza, Historia secreta de los mongoles §75"),
        f(null, "Si sigues con vida, beberás agua de un cuenco de oro.", "Proverbio mongol: mientras hay vida, hay esperanza"),
        f(null, "Ese día todos van vestidos de blanco, hombres y mujeres, grandes y pequeños, para que les vaya bien todo el año; y se desean unos a otros felicidad y buena suerte para el año que llega.", "Marco Polo, sobre la Fiesta Blanca del año nuevo en la corte de Kublai, Libro de las maravillas II, 15"),
        f(null, "Cuando me encontré entre ellos, me pareció de verdad que me habían llevado a otro siglo.", "Guillermo de Rubruck, al llegar a los mongoles, Itinerario, 1253"),
        f(null, "Cada jefe, según tenga más o menos gente a su cargo, conoce los límites de sus pastos y dónde apacentar en invierno y en verano, en primavera y en otoño.", "Guillermo de Rubruck, sobre los mongoles, Itinerario, 1253"),
        f(null, "Si hay que beber, que sea tres veces al mes, porque más es malo. Emborracharse dos veces al mes es mejor; una vez, mejor aún; y no beber nada es lo mejor de todo.", "Dichos de Gengis Kan, recogidos por Rashid al-Din, Compendio de crónicas"),
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
        f("La vita bene spesa lunga è.", "La vida bien empleada es larga.", "Leonardo da Vinci, cuadernos (recogida en Aforismi, novelle e profezie)"),
        f("No' si volta chi a stella è fisso.", "No se desvía quien tiene la mirada fija en una estrella.", "Leonardo da Vinci, cuadernos (recogida en Aforismi, novelle e profezie)"),
        f("O tempo, consumatore delle cose, e, o invidiosa antichità, tu distruggi tutte le cose, e consumate tutte le cose dai duri denti della vecchiezza, a poco a poco, con lenta morte.", "Oh tiempo, devorador de las cosas, y tú, envidiosa antigüedad: lo destruyes todo, y todo lo consumes con los duros dientes de la vejez, poco a poco, con lenta muerte.", "Leonardo da Vinci, cuadernos, glosando a Ovidio (Metamorfosis XV)"),
        f("La mattina ordino me a tutto il dí, il giorno seguo quanto mi si richiede, e poi la sera inanzi che io mi riposi ricolgo in me quanto feci il dí.", "Por la mañana me organizo para todo el día; durante el día hago lo que me toca; y por la noche, antes de descansar, repaso dentro de mí lo que hice en el día.", "Leon Battista Alberti, Los libros de la familia III (habla Giannozzo), c. 1433-1440"),
        f("Chi sa non perdere tempo sa fare quasi ogni cosa, e chi sa adoperare il tempo, costui sarà signore di qualunque cosa e' voglia.", "Quien sabe no perder el tiempo sabe hacer casi cualquier cosa; y quien sabe emplear el tiempo será dueño de cuanto quiera.", "Leon Battista Alberti, Los libros de la familia III (habla Giannozzo), c. 1433-1440"),
        f("La vita fugge, et non s'arresta una hora, / et la morte vien dietro a gran giornate.", "La vida huye y no se detiene ni una hora, y la muerte viene detrás a grandes jornadas.", "Francesco Petrarca, Cancionero 272"),
        f("Tutti gli uomini d'ogni sorte, che hanno fatto qualche cosa che sia virtuosa… doverieno, essendo veritieri e da bene, di lor propia mano descrivere la loro vita; ma non si doverrebbe cominciare una tal bella impresa prima che passato l'età de' quarant'anni.", "Todos los hombres, de cualquier condición, que hayan hecho algo de valor deberían, si son sinceros y de bien, escribir su vida de su propia mano; pero no habría que empezar tan bella empresa antes de pasados los cuarenta años.", "Benvenuto Cellini, Vida I, 1, 1558"),
    )

    /** Del mar y de los piratas: los artículos de Bartholomew Roberts y su lema (en la *Historia general de los piratas*, 1724), Defoe, Espronceda y otros. */
    val PIRATA = listOf(
        f(null, "Que es mi barco mi tesoro, que es mi Dios la libertad, mi ley la fuerza y el viento, mi única patria la mar.", "José de Espronceda, «Canción del pirata», 1835"),
        f(null, "Navega, velero mío, sin temor, que ni enemigo navío ni tormenta, ni bonanza tu rumbo a torcer alcanza, ni a sujetar tu valor.", "José de Espronceda, «Canción del pirata», 1835"),
        f(null, "La libertad, Sancho, es uno de los más preciosos dones que a los hombres dieron los cielos; con ella no pueden igualarse los tesoros que encierra la tierra ni el mar encubre.",
            "Miguel de Cervantes, Don Quijote II, 58"),
        f(null, "Caminante, no hay camino, sino estelas en la mar.", "Antonio Machado, «Proverbios y cantares» XXIX, Campos de Castilla (ed. de 1917)"),
        f("In an honest Service, there is thin Commons, low Wages, and hard Labour; in this, Plenty and Satiety, Pleasure and Ease, Liberty and Power… No, a merry Life and a short one, shall be my Motto.",
            "En un servicio honrado hay rancho escaso, paga baja y trabajo duro; en este, abundancia y hartura, placer y holgura, libertad y poder… No: una vida alegre y corta será mi lema.",
            "Bartholomew Roberts, según Charles Johnson, Historia general de los piratas, 1724"),
        f("Every Man has a Vote in Affairs of Moment.", "Cada hombre tiene un voto en los asuntos de importancia.", "Artículos de Bartholomew Roberts, art. I, en Charles Johnson, Historia general de los piratas, 1724"),
        f("The Lights and Candles to be put out at eight o'Clock at Night: If any of the Crew, after that Hour, still remained inclined for Drinking, they were to do it on the open Deck.",
            "Las luces y las velas se apagan a las ocho de la noche; si alguno de la tripulación, pasada esa hora, aún quiere beber, que lo haga en la cubierta descubierta.",
            "Artículos de Bartholomew Roberts, art. IV, en Charles Johnson, Historia general de los piratas, 1724"),
        f("Thus fear of danger is ten thousand times more terrifying than danger itself, when apparent to the eyes.", "Así, el miedo al peligro aterra diez mil veces más que el peligro mismo cuando se tiene delante.",
            "Daniel Defoe, Robinson Crusoe, 1719"),
        f("But it is never too late to be wise.", "Pero nunca es tarde para ser sensato.", "Daniel Defoe, Robinson Crusoe, 1719"),
        f("All our discontents about what we want appeared to me to spring from the want of thankfulness for what we have.",
            "Todos nuestros descontentos por lo que nos falta me parecieron nacer de la falta de gratitud por lo que tenemos.", "Daniel Defoe, Robinson Crusoe, 1719"),
        f("Whenever it is a damp, drizzly November in my soul… then, I account it high time to get to sea as soon as I can.",
            "Cada vez que en mi alma es un noviembre húmedo y lloviznoso… entonces juzgo que ya es hora de hacerme a la mar cuanto antes.", "Herman Melville, Moby Dick, cap. 1, 1851"),
        f("Better sleep with a sober cannibal than a drunken Christian.", "Mejor dormir con un caníbal sobrio que con un cristiano borracho.", "Herman Melville, Moby Dick, cap. 3, 1851"),
        f("There is, one knows not what sweet mystery about this sea, whose gently awful stirrings seem to speak of some hidden soul beneath.",
            "Hay en este mar no se sabe qué dulce misterio, cuyos movimientos, mansos y temibles, parecen hablar de un alma escondida debajo.", "Herman Melville, Moby Dick, cap. 111, 1851"),
        f("Fifteen men on the dead man's chest— Yo-ho-ho, and a bottle of rum! Drink and the devil had done for the rest.",
            "Quince hombres sobre el cofre del muerto, ¡yo-jo-jo, y una botella de ron! La bebida y el diablo acabaron con los demás.", "Robert Louis Stevenson, La isla del tesoro, cap. 1, 1883"),
        f("The sea has never been friendly to man. At most it has been the accomplice of human restlessness.", "El mar nunca ha sido amigo del hombre. A lo sumo, ha sido cómplice de su inquietud.",
            "Joseph Conrad, El espejo del mar, 1906"),
        f("Water, water, every where, Nor any drop to drink.", "Agua, agua por todas partes, y ni una gota para beber.", "Samuel Taylor Coleridge, La balada del viejo marinero, 1798"),
        f("We are such stuff As dreams are made on; and our little life Is rounded with a sleep.", "Somos de la misma materia que los sueños, y nuestra breve vida se cierra con un sueño.",
            "William Shakespeare, La tempestad IV, 1"),
        f("Ignoranti quem portum petat nullus suus ventus est.", "Para quien no sabe a qué puerto se dirige, ningún viento es favorable.", "Séneca, Cartas a Lucilio 71"),
        f("Gubernatorem in tempestate, in acie militem intellegas.", "Al piloto se le conoce en la tempestad; al soldado, en la batalla.", "Séneca, Sobre la providencia 4, 5"),
        f("Πλεῖν ἀνάγκη, ζῆν οὐκ ἀνάγκη.", "Navegar es necesario; vivir no es necesario.", "Pompeyo, según Plutarco, Vida de Pompeyo 50"),
        f("No man will be a sailor who has contrivance enough to get himself into a jail; for being in a ship is being in a jail, with the chance of being drowned.",
            "Nadie que tenga maña para hacerse meter en la cárcel se hará marinero: estar en un barco es estar en la cárcel, con la posibilidad de ahogarse.", "Samuel Johnson, según James Boswell, Vida de Samuel Johnson (1759)"),
        f(null, "Los que descienden a la mar en navíos, y hacen negocio en las muchas aguas, ellos han visto las obras de Jehová, y sus maravillas en el profundo.",
            "Salmo 107, 23-24, versión Reina-Valera"),
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
        f("Изучайте, сопоставляйте, накопляйте факты.", "Estudien, comparen, acumulen hechos.", "Iván Pávlov, Carta a la juventud, 1936"),
        f("Всем хорошим во мне я обязан книгам.", "Todo lo bueno que hay en mí se lo debo a los libros.", "Máximo Gorki, prefacio a la Historia general de las literaturas extranjeras de P. Mortier, 1925"),
        f("Вперед, время! Время, вперед!", "¡Adelante, tiempo! ¡Tiempo, adelante!", "Vladímir Maiakovski, «Marcha del tiempo», en La casa de baños, 1930"),
        f("Самое дорогое у человека — это жизнь. Она даётся ему один раз, и прожить её надо так, чтобы не было мучительно больно за бесцельно прожитые годы.", "Lo más valioso que tiene una persona es la vida. Se le da una sola vez, y hay que vivirla de modo que no duelan como un tormento los años vividos sin propósito.", "Nikolái Ostrovski, Así se templó el acero, 1934"),
        f("Невозможное сегодня станет возможным завтра.", "Lo imposible hoy será posible mañana.", "Konstantín Tsiolkovski, La exploración del espacio cósmico con aparatos a reacción, 1911–1912"),
    )

    fun de(tema: TemaId): List<Frase> = when (tema) {
        TemaId.ROMANO -> ROMANO
        TemaId.URBE -> URBE
        TemaId.CIBER -> CIBER
        TemaId.NEON -> NEON
        TemaId.HELENO -> HELENO
        TemaId.EGIPTO -> EGIPTO
        TemaId.MEDIEVAL -> MEDIEVAL
        TemaId.TEMPLARIO -> TEMPLARIO
        TemaId.DISTOPIA -> DISTOPIA
        TemaId.REFUGIO -> REFUGIO
        TemaId.ORDEN -> ORDEN
        TemaId.LABORATORIO -> LABORATORIO
        TemaId.COLONOS -> COLONOS
        TemaId.NAPOLEON -> NAPOLEON
        TemaId.DECO -> DECO
        TemaId.BOTANICO -> BOTANICO
        TemaId.RUINAS -> RUINAS
        TemaId.NAVIDAD -> NAVIDAD
        TemaId.SEDA -> SEDA
        TemaId.URSS -> URSS
        TemaId.GOTICO -> GOTICO
        TemaId.ARCANO -> ARCANO
        TemaId.ESPACIO -> ESPACIO
        TemaId.ABISMO -> ABISMO
        TemaId.ANDALUS -> ANDALUS
        TemaId.MONGOL -> MONGOL
        TemaId.RENACIMIENTO -> RENACIMIENTO
        TemaId.PIRATA -> PIRATA
        TemaId.REVOLUCION -> REVOLUCION
        TemaId.ANTIGUAS -> ANTIGUAS
    }

    /** La del día para el tema elegido: cambia cada día y recorre todas antes de repetir. */
    fun del(fecha: String, tema: TemaId = Estilo.tema): Frase {
        val l = de(tema)
        return l[Math.floorMod(Fechas.parse(fecha).toEpochDay(), l.size.toLong()).toInt()]
    }

    /** Encabezado del recuadro de la frase, según el tema. */
    fun rotulo(tema: TemaId = Estilo.tema): String = when (tema) {
        TemaId.ROMANO -> "SENTENTIA DIEI"
        TemaId.URBE -> "LA FRASE DEL GIORNO"
        TemaId.CIBER -> "> motd"
        TemaId.NEON -> "LO QUE DICE EL LETRERO"
        TemaId.HELENO -> "ΓΝΩΜΗ ΤΗΣ ΗΜΕΡΑΣ"
        TemaId.EGIPTO -> "Enseñanza del día (sebayt)"
        TemaId.MEDIEVAL -> "Sentencia del día"
        TemaId.TEMPLARIO -> "Lectura del refectorio"
        TemaId.DISTOPIA -> "CONSIGNA DEL DÍA"
        TemaId.REFUGIO -> "MENSAJE DEL DÍA"
        TemaId.ORDEN -> "ORDEN DEL DÍA"
        TemaId.LABORATORIO -> "NOTA DEL DÍA"
        TemaId.COLONOS -> "Del almanaque"
        TemaId.NAPOLEON -> "BOLETÍN DEL EJÉRCITO"
        TemaId.DECO -> "Del programa de esta noche"
        TemaId.BOTANICO -> "Nota del naturalista"
        TemaId.RUINAS -> "Escrito en la pared"
        TemaId.NAVIDAD -> "Junto a la chimenea"
        TemaId.SEDA -> "Dicho de la caravana"
        TemaId.URSS -> "LEMA DEL DÍA"
        TemaId.GOTICO -> "Lectio del día"
        TemaId.ARCANO -> "Recorte del día"
        TemaId.ESPACIO -> "TRANSMISIÓN DEL DÍA"
        TemaId.ABISMO -> "LO QUE SUBIÓ LA SONDA HOY"
        TemaId.ANDALUS -> "حكمة اليوم · sentencia del día"
        TemaId.MONGOL -> "ӨНӨӨДРИЙН БИЛИГ · máxima del día"
        TemaId.RENACIMIENTO -> "Dal taccuino"
        TemaId.PIRATA -> "Escrito en la bitácora"
        TemaId.REVOLUCION -> "CARTEL DEL DÍA"
        TemaId.ANTIGUAS -> "Dicho del camino"
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
     * Las frases con ♥ de un día, para la hoja del diario: solo la frase del día a la que tú le diste ♥ ese mismo día.
     * Ni los subrayados de Kindle ni las citas de tus obras: esas viven en la Biblioteca y en «Frases guardadas».
     * Se muestran como epígrafe; no se guardan en el texto del diario.
     */
    fun deLaHoja(fecha: String): List<Frase> =
        Repo.config.frases_guardadas.filter { g -> g.fecha == fecha && TemaId.entries.any { it.id == g.tema } }
            .map { Frase(it.original, it.texto, it.autor) }.distinctBy { it.texto }.take(2)

    /** Quita el ♥ a una frase guardada (desde el epígrafe del diario). */
    fun quitar(f: Frase) = Repo.cambiarConfig { c -> c.copy(frases_guardadas = c.frases_guardadas.filterNot { it.texto == f.texto && it.autor == f.autor }) }
}
