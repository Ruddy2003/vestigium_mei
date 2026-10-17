package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas

/**
 * Una frase por día para cada tema. Las citas son de textos de dominio público (antiguos, medievales
 * o de los siglos XVIII y XIX), con traducción propia. Las de los temas inventados (los carteles del
 * Ministerio, el manual del refugio, el códice de la Orden…) son originales escritas para esta app.
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
        f(null, "El sistema registra. Tú interpretas.", "vestigium.log"),
        f(null, "Todo dato sin respaldo es un dato prestado.", "// sysadmin anónimo"),
        f(null, "ERR 404: recuerdo no encontrado. Sugerencia: escribir el diario.", "kernel"),
        f(null, "Tu historial es el único código fuente de ti mismo.", "README.md"),
        f(null, "Duerme: mañana se compila lo que aprendiste hoy.", "cron @nightly"),
        f(null, "No hay bugs, solo variables que todavía no mides.", "CHANGELOG"),
        f(null, "La red olvida. Tu disco local, no.", "// sysadmin anónimo"),
        f(null, "uptime: una vida. último reinicio: anoche.", "status"),
        f(null, "Cada commit cuenta, hasta el que solo dice «día normal».", "git log"),
        f(null, "La ciudad nunca duerme. Tú sí deberías.", "daemon de salud"),
        f(null, "Desconfía de la correlación que confirma lo que ya querías creer.", "analisis.R"),
        f(null, "Neón afuera, silencio adentro. Escribe.", "terminal 03"),
        f(null, "Los datos crudos no mienten; los resúmenes, a veces.", "pipeline"),
        f(null, "Encriptado no es lo mismo que olvidado.", "// sysadmin anónimo"),
        f("Calculemus!", "¡Calculemos!", "Gottfried Leibniz"),
        f("The Analytical Engine weaves algebraical patterns just as the Jacquard-loom weaves flowers and leaves.",
            "La Máquina Analítica teje patrones algebraicos igual que el telar de Jacquard teje flores y hojas.", "Ada Lovelace, Notas, 1843"),
        f("The Analytical Engine has no pretensions whatever to originate anything. It can do whatever we know how to order it to perform.",
            "La Máquina Analítica no pretende originar nada: hace lo que sepamos ordenarle.", "Ada Lovelace, Notas, 1843"),
        f(null, "«Señor Babbage, si mete números equivocados en la máquina, ¿saldrán las respuestas correctas?». No alcanzo a entender la confusión de ideas que puede provocar semejante pregunta.",
            "Charles Babbage, Pasajes de la vida de un filósofo, 1864"),
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
        f("Fatti non foste a viver come bruti, / ma per seguir virtute e canoscenza.", "No fuisteis hechos para vivir como bestias, sino para seguir la virtud y el saber.", "Dante, Infierno XXVI"),
        f(null, "La vida es como un gorrión que en invierno cruza volando la sala del banquete: entra por una puerta, siente un instante el calor y sale por la otra.",
            "Beda el Venerable, Historia eclesiástica II, 13"),
    )

    val DISTOPIA = listOf(
        f(null, "Aquí el único que vigila eres tú.", "Lema del Ministerio del Registro"),
        f(null, "Lo que no se anota, no ocurrió.", "Ministerio del Registro, circular 1"),
        f(null, "El expediente no olvida. Tú sí.", "Archivo Central"),
        f(null, "Cada día es una declaración. Declara con verdad.", "Ministerio del Registro"),
        f(null, "La constancia es la única obediencia que te debes.", "Oficina de Disciplina Personal"),
        f(null, "Tus datos son tuyos: por eso los guarda tu teléfono y no el Ministerio.", "Aviso oficial, tablón 4"),
        f(null, "Ciudadano: hoy también fuiste observado. Por ti.", "Altavoz del bloque 7"),
        f(null, "El ojo no parpadea. Tú sí deberías dormir ocho horas.", "Comisión de Descanso"),
        f(null, "Racionar es recordar en qué gastaste.", "Departamento de Racionamiento"),
        f(null, "Un día sin registro es un hueco en la historia oficial.", "Archivo Central"),
        f(null, "La memoria es falible. El archivo, no tanto.", "Archivo Central"),
        f(null, "Recuerda: la correlación no prueba causas. Ni siquiera aquí.", "Comité de Estadística"),
        f(null, "Tu mejor informante eres tú a las diez de la noche.", "Ministerio del Registro"),
        f(null, "La sinceridad del diario no es un deber: es lo único que lo vuelve útil.", "Oficina de Disciplina Personal"),
        f("Quis custodiet ipsos custodes?", "¿Quién vigilará a los vigilantes?", "Juvenal, Sátira VI"),
        f("Duas tantum res anxius optat, panem et circenses.", "El pueblo ya solo ansía dos cosas: pan y juegos de circo.", "Juvenal, Sátira X"),
        f("Ubi solitudinem faciunt, pacem appellant.", "Donde crean un desierto, lo llaman paz.", "Tácito, Agrícola 30"),
        f("Corruptissima re publica plurimae leges.", "Cuanto más corrupto el Estado, más numerosas las leyes.", "Tácito, Anales III, 27"),
        f("A new mode of obtaining power of mind over mind, in a quantity hitherto without example.",
            "Un nuevo modo de obtener poder de la mente sobre la mente, en una cantidad sin precedentes.", "Jeremy Bentham, El panóptico, 1791"),
        f("Those who would give up essential Liberty, to purchase a little temporary Safety, deserve neither Liberty nor Safety.",
            "Quienes renuncian a la libertad esencial para obtener un poco de seguridad pasajera no merecen ni libertad ni seguridad.", "Benjamin Franklin, 1755"),
        f("Soyez résolus de ne servir plus, et vous voilà libres.", "Decidíos a no servir más, y seréis libres.", "Étienne de La Boétie, Discurso de la servidumbre voluntaria"),
        f(null, "Alguien debió de calumniar a Josef K., porque sin haber hecho nada malo fue detenido una mañana.", "Franz Kafka, El proceso (1925)"),
        f(null, "Les juro, señores, que tener demasiada conciencia es una enfermedad, una verdadera enfermedad.", "Fiódor Dostoievski, Memorias del subsuelo"),
    )

    val REFUGIO = listOf(
        f(null, "¡Buenos días, residente! Un día registrado es un día a salvo.", "Manual del residente, cap. 1"),
        f(null, "Bajo tierra el sol no sale, pero tu ánimo sí. Anótalo.", "Manual del residente, cap. 2"),
        f(null, "Hidrátate, duerme y anota. En ese orden.", "Cartel del comedor"),
        f(null, "La superficie puede esperar. Tu diario, no.", "Cartel del pasillo B"),
        f(null, "Un residente informado es un residente feliz.* (*Felicidad no garantizada.)", "Folleto de bienvenida"),
        f(null, "Los suministros se acaban. Los registros, nunca.", "Intendencia del refugio"),
        f(null, "Si ves un dato raro, no entres en pánico: revisa el contexto.", "Protocolo 7-B"),
        f(null, "Mantén la calma y respalda tus datos en dos lugares.", "Mantenimiento"),
        f(null, "Hoy es un día excelente para ser constante.", "Altavoz general, 07:00"),
        f(null, "La puerta está cerrada; tu curiosidad, abierta.", "Biblioteca del refugio"),
        f(null, "Cuenta tus pasos: en un pasillo de cuarenta metros, cada uno importa.", "Programa de acondicionamiento"),
        f(null, "El contador Geiger no mide el ánimo. Para eso estás tú.", "Enfermería"),
        f(null, "Tu racha de registros es excelente. Siga así, residente.", "Supervisión de turno"),
        f(null, "Nada se pierde si queda escrito y respaldado.", "Mantenimiento"),
        f(null, "Lo que hoy parece rutina, dentro de diez años será historia.", "Cápsula del tiempo, nota de apertura"),
        f("The only thing we have to fear is fear itself.", "A lo único que debemos temer es al miedo mismo.", "Franklin D. Roosevelt, 1933"),
        f(null, "Que la milagrosa inventiva del hombre no se dedique a su muerte, sino que se consagre a su vida.", "Dwight D. Eisenhower, Átomos para la paz, 1953"),
        f("Plans are worthless, but planning is everything.", "Los planes no valen nada; planear lo es todo.", "Dwight D. Eisenhower, 1957"),
        f("Duck and cover!", "¡Agáchate y cúbrete!", "Película de defensa civil de EE. UU., 1951"),
    )

    val ORDEN = listOf(
        f(null, "Disciplina es hacer hoy lo que ayer dijiste que harías.", "Códice de la Orden, artículo 3"),
        f(null, "Una armadura sin mantenimiento es chatarra. Un hábito sin registro, también.", "Taller de la Orden"),
        f(null, "Informa con precisión: el mando decide con lo que tú anotas.", "Manual de campaña"),
        f(null, "El acero se templa con fuego y paciencia.", "Fragua de la Orden"),
        f(null, "Nadie conoce el terreno sin haberlo recorrido.", "Manual de reconocimiento"),
        f(null, "Guarda el saber; compártelo con quien sepa usarlo.", "Códice de la Orden, artículo 9"),
        f(null, "Tu cuerpo es tu primera armadura: duérmela, aliméntala, entrénala.", "Instructor de armas"),
        f(null, "El parte de hoy es la estrategia de mañana.", "Estado mayor"),
        f(null, "El honor no se declara: se registra en los hechos.", "Códice de la Orden, artículo 12"),
        f(null, "Mantén la formación aunque nadie mire. Sobre todo si nadie mira.", "Sargento de la Orden"),
        f(null, "Ningún caballero ganó una campaña en un solo día.", "Crónica de la Orden"),
        f("Igitur qui desiderat pacem, praeparet bellum.", "Quien desea la paz, que prepare la guerra.", "Vegecio, Compendio de técnica militar III"),
        f("Paucos viros fortes natura procreat, bona institutione plures reddit industria.", "La naturaleza da pocos valientes; la buena instrucción y la constancia hacen muchos más.", "Vegecio, Compendio de técnica militar III"),
        f("知彼知己，百戰不殆", "Conoce al enemigo y conócete a ti mismo, y en cien batallas no correrás peligro.", "Sun Tzu, El arte de la guerra III"),
        f("今日は昨日の我に勝ち", "Hoy, vence a tu yo de ayer.", "Miyamoto Musashi, El libro de los cinco anillos"),
    )

    val LABORATORIO = listOf(
        f(null, "Una medición sin fecha es un rumor.", "Protocolo del laboratorio, sección 1"),
        f(null, "Registra el resultado aunque no te guste. Sobre todo si no te gusta.", "Protocolo del laboratorio, sección 2"),
        f(null, "n = 1 no es un defecto si el sujeto eres tú.", "Protocolo del laboratorio, sección 4"),
        f(null, "Correlación observada. Causalidad: pendiente de experimento.", "Bitácora del laboratorio"),
        f(null, "La variable más difícil de controlar es el investigador.", "Bitácora del laboratorio"),
        f(null, "Calibra antes de medir: dormir bien es calibrar.", "Protocolo del laboratorio, sección 7"),
        f(null, "Lo que hoy es ruido mañana puede ser señal. Guárdalo.", "Bitácora del laboratorio"),
        f("When you can measure what you are speaking about, and express it in numbers, you know something about it.",
            "Cuando puedes medir aquello de lo que hablas y expresarlo en números, sabes algo de ello.", "Lord Kelvin, 1883"),
        f(null, "Mide lo que se pueda medir, y haz medible lo que no lo sea.", "Atribuida a Galileo Galilei (no aparece en sus obras)"),
        f("Dans les champs de l'observation, le hasard ne favorise que les esprits préparés.", "En el campo de la observación, el azar solo favorece a las mentes preparadas.", "Louis Pasteur, 1854"),
        f("L'art, c'est moi; la science, c'est nous.", "El arte soy yo; la ciencia somos nosotros.", "Claude Bernard"),
        f("If I have seen further it is by standing on the shoulders of Giants.", "Si he visto más lejos es porque estoy subido en hombros de gigantes.", "Isaac Newton, carta a Robert Hooke, 1675"),
        f("Ipsa scientia potestas est.", "El conocimiento mismo es poder.", "Francis Bacon, Meditaciones sagradas, 1597"),
        f("If a man will begin with certainties, he shall end in doubts; but if he will be content to begin with doubts, he shall end in certainties.",
            "Quien empieza con certezas acabará en dudas; pero quien se conforma con empezar con dudas acabará en certezas.", "Francis Bacon, El avance del saber, 1605"),
        f("Ignorance more frequently begets confidence than does knowledge.", "La ignorancia engendra confianza con más frecuencia que el conocimiento.", "Charles Darwin, El origen del hombre, 1871"),
        f("La sapienza è figliola della sperienza.", "La sabiduría es hija de la experiencia.", "Leonardo da Vinci, Códice Forster III"),
        f(null, "Todo hombre puede ser, si se lo propone, escultor de su propio cerebro.", "Santiago Ramón y Cajal, Reglas y consejos sobre investigación científica, 1897"),
        f("On ne doit rien craindre dans la vie, il faut seulement comprendre.", "En la vida no hay que temer nada, solo comprender.", "Atribuida a Marie Curie"),
    )

    val COLONOS = listOf(
        f(null, "Un pueblo se levanta casa por casa; una vida, día por día.", "Gaceta del asentamiento"),
        f(null, "Vecino que anota sus cuentas no le teme al invierno.", "Almanaque de los colonos"),
        f(null, "Se convoca a todos los vecinos: hoy también hay algo que reconstruir.", "Aviso del cabildo"),
        f(null, "Siembra hoy, aunque la cosecha no aparezca en el registro de esta semana.", "Almanaque de los colonos"),
        f(null, "Una lámpara encendida en cada ventana: así se sabe que el pueblo sigue en pie.", "Gaceta del asentamiento"),
        f("These are the times that try men's souls.", "Estos son los tiempos que ponen a prueba el alma.", "Thomas Paine, La crisis americana, 1776"),
        f("What we obtain too cheap, we esteem too lightly.", "Lo que obtenemos demasiado barato lo estimamos demasiado poco.", "Thomas Paine, La crisis americana, 1776"),
        f("We have it in our power to begin the world over again.", "Está en nuestras manos empezar el mundo de nuevo.", "Thomas Paine, Sentido común, 1776"),
        f("Lost time is never found again.", "El tiempo perdido nunca se vuelve a encontrar.", "Benjamin Franklin, Almanaque del pobre Richard"),
        f("Beware of little expenses; a small leak will sink a great ship.", "Cuidado con los gastos pequeños: una grieta pequeña hunde un barco grande.", "Benjamin Franklin, El camino a la riqueza, 1758"),
        f("Dost thou love life? Then do not squander time, for that's the stuff life is made of.", "¿Amas la vida? Entonces no malgastes el tiempo, que de eso está hecha.", "Benjamin Franklin, Almanaque del pobre Richard"),
        f("Well done is better than well said.", "Bien hecho es mejor que bien dicho.", "Benjamin Franklin, Almanaque del pobre Richard"),
        f("Early to bed and early to rise, makes a man healthy, wealthy, and wise.", "Acostarse temprano y madrugar hacen a uno sano, rico y sabio.", "Benjamin Franklin, Almanaque del pobre Richard"),
        f(null, "Hice un librito con una página para cada virtud, y cada noche marcaba con un punto las faltas del día.", "Benjamin Franklin, Autobiografía, sobre su registro de virtudes"),
        f("I went to the woods because I wished to live deliberately, to front only the essential facts of life.",
            "Me fui a los bosques porque quería vivir deliberadamente y enfrentar solo los hechos esenciales de la vida.", "Henry David Thoreau, Walden, 1854"),
        f("Our life is frittered away by detail. Simplify, simplify.", "Nuestra vida se desperdicia en detalles. Simplifica, simplifica.", "Henry David Thoreau, Walden, 1854"),
        f("Trust thyself: every heart vibrates to that iron string.", "Confía en ti mismo: todo corazón vibra con esa cuerda de hierro.", "Ralph Waldo Emerson, Confianza en uno mismo, 1841"),
        f("Write it on your heart that every day is the best day in the year.", "Escribe en tu corazón que cada día es el mejor del año.", "Ralph Waldo Emerson, Sociedad y soledad, 1870"),
        f("Finish each day and be done with it. You have done what you could.", "Termina cada día y déjalo atrás. Hiciste lo que pudiste.", "Ralph Waldo Emerson"),
    )

    fun de(tema: TemaId): List<Frase> = when (tema) {
        TemaId.ROMANO -> ROMANO; TemaId.CIBER -> CIBER; TemaId.HELENO -> HELENO; TemaId.MEDIEVAL -> MEDIEVAL
        TemaId.DISTOPIA -> DISTOPIA; TemaId.REFUGIO -> REFUGIO; TemaId.ORDEN -> ORDEN; TemaId.LABORATORIO -> LABORATORIO; TemaId.COLONOS -> COLONOS
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
    }
}
