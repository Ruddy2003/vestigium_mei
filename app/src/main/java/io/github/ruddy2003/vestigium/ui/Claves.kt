package io.github.ruddy2003.vestigium.ui

/**
 * Las claves de las líneas «clave = valor» (Día, widget «hoy», reportes) en la lengua o la jerga de cada tema. Las claves
 * originales están en latín (`somnus`, `passus`…); [Estilo.clave] decide qué diccionario usa cada tema. Son cortas: van en
 * una columna de ancho fijo.
 */
internal object Claves {
    /** Francés del Imperio (napoleónico; la revolución lo toma en mayúsculas con sus propias palabras). */
    val FRANCES: Map<String, String> = mapOf(
        "horologium" to "montre", "somnus" to "sommeil", "stadia" to "phases", "cor" to "cœur", "passus" to "pas", "motus" to "mouvement",
        "iter" to "route", "exercitatio" to "exercice", "loca" to "lieux", "telephonum" to "écran", "telephon" to "écran",
        "usus" to "usage", "monita" to "avis", "musica" to "musique", "imagines" to "images", "fasti" to "agenda", "caelum" to "ciel",
        "lux" to "jour", "aer" to "air", "corpus" to "corps", "nutritio" to "nutrition", "cibus" to "repas", "robur" to "gymnase",
        "musculi" to "muscles", "voces" to "appels", "soni" to "son", "retia" to "wifi", "lumen" to "lumière", "vis" to "batterie",
        "lectio" to "lecture", "strepitus" to "bruit", "luna" to "lune", "dias" to "jours", "animus" to "humeur", "optimus" to "meilleur",
        "momenta" to "moments", "svmptvs" to "dépense", "memorabilia" to "jalons", "ascensvs" to "escalade", "dies" to "jours",
        "quaestiones" to "questions", "celeritas" to "réaction", "bibliotheca" to "bibliothèque", "synchronia" to "synchro",
        "opera" to "œuvres", "iudicium" to "avis_critique", "citatio" to "citation", "sententia" to "phrase", "orbis" to "monde",
        "nova" to "nouveau", "mirabilia" to "merveilles", "somnium" to "rêve", "epistulae" to "lettres", "aperta" to "ouverte",
        "initium" to "début", "finis" to "fin", "pessimus" to "pire", "personae" to "personnes", "themata" to "sujets",
        "praeclarum" to "la_meilleure", "dies felix" to "jour_heureux", "consuetudo" to "habitudes", "series" to "série",
        "palmae" to "succès", "reditus" to "revenus", "apps" to "applis",
    )

    /** Chino, como en las tablillas de las postas de la Ruta de la Seda. */
    val CHINO: Map<String, String> = mapOf(
        "horologium" to "手表", "somnus" to "睡眠", "stadia" to "阶段", "cor" to "心率", "passus" to "步数", "motus" to "运动", "iter" to "路线",
        "exercitatio" to "锻炼", "loca" to "地点", "telephonum" to "屏幕", "telephon" to "屏幕", "usus" to "使用", "monita" to "通知",
        "musica" to "音乐", "imagines" to "照片", "fasti" to "日程", "caelum" to "天气", "lux" to "日照", "aer" to "空气", "corpus" to "身体",
        "nutritio" to "营养", "cibus" to "饮食", "robur" to "健身", "musculi" to "肌肉", "voces" to "通话", "soni" to "声音", "retia" to "网络",
        "lumen" to "光线", "vis" to "电量", "lectio" to "阅读", "strepitus" to "噪音", "luna" to "月相", "dias" to "天数", "animus" to "心情",
        "optimus" to "最佳", "momenta" to "时刻", "svmptvs" to "花费", "memorabilia" to "里程碑", "ascensvs" to "攀岩", "dies" to "天",
        "quaestiones" to "问卷", "celeritas" to "反应", "bibliotheca" to "书库", "synchronia" to "同步", "opera" to "作品", "iudicium" to "评价",
        "citatio" to "引文", "sententia" to "句子", "orbis" to "世界", "nova" to "新", "mirabilia" to "奇观", "somnium" to "梦", "epistulae" to "书信",
        "aperta" to "已开", "initium" to "开始", "finis" to "结束", "pessimus" to "最差", "personae" to "人物", "themata" to "主题",
        "praeclarum" to "最好", "dies felix" to "好日子", "consuetudo" to "习惯", "series" to "连续", "palmae" to "成就", "reditus" to "收入",
        "apps" to "应用",
    )

    /** Árabe, para al-Ándalus. */
    val ARABE: Map<String, String> = mapOf(
        "horologium" to "الساعة", "somnus" to "النوم", "stadia" to "المراحل", "cor" to "القلب", "passus" to "الخطوات", "motus" to "الحركة",
        "iter" to "الطريق", "exercitatio" to "الرياضة", "loca" to "الأماكن", "telephonum" to "الشاشة", "telephon" to "الشاشة",
        "usus" to "الاستعمال", "monita" to "التنبيهات", "musica" to "الموسيقى", "imagines" to "الصور", "fasti" to "المواعيد",
        "caelum" to "الطقس", "lux" to "ضوء_النهار", "aer" to "الهواء", "corpus" to "الجسم", "nutritio" to "التغذية", "cibus" to "الوجبات",
        "robur" to "القوة", "musculi" to "العضلات", "voces" to "المكالمات", "soni" to "الصوت", "retia" to "الشبكة", "lumen" to "الضوء",
        "vis" to "الشحن", "lectio" to "القراءة", "strepitus" to "الضجيج", "luna" to "القمر", "dias" to "الأيام", "animus" to "المزاج",
        "optimus" to "الأفضل", "momenta" to "اللحظات", "svmptvs" to "الإنفاق", "memorabilia" to "المعالم", "ascensvs" to "التسلق",
        "dies" to "الأيام", "quaestiones" to "الاستبيانات", "celeritas" to "رد_الفعل", "bibliotheca" to "المكتبة",
        "synchronia" to "المزامنة", "opera" to "الأعمال", "iudicium" to "الرأي", "citatio" to "اقتباس", "sententia" to "جملة",
        "orbis" to "العالم", "nova" to "جديد", "mirabilia" to "العجائب", "somnium" to "الحلم", "epistulae" to "الرسائل",
        "aperta" to "مفتوحة", "initium" to "البداية", "finis" to "النهاية", "pessimus" to "الأسوأ", "personae" to "الأشخاص",
        "themata" to "المواضيع", "praeclarum" to "الأحسن", "dies felix" to "يوم_سعيد", "consuetudo" to "العادات", "series" to "التتابع",
        "palmae" to "الإنجازات", "reditus" to "الدخل", "apps" to "التطبيقات",
    )

    /** Ruso, en las mayúsculas de los carteles. */
    val RUSO: Map<String, String> = mapOf(
        "horologium" to "ЧАСЫ", "somnus" to "СОН", "stadia" to "ФАЗЫ", "cor" to "СЕРДЦЕ", "passus" to "ШАГИ", "motus" to "ДВИЖЕНИЕ",
        "iter" to "МАРШРУТ", "exercitatio" to "ТРЕНИРОВКА", "loca" to "МЕСТА", "telephonum" to "ЭКРАН", "telephon" to "ЭКРАН",
        "usus" to "ИСПОЛЬЗОВАНИЕ", "monita" to "УВЕДОМЛЕНИЯ", "musica" to "МУЗЫКА", "imagines" to "ФОТО", "fasti" to "ПЛАНЫ",
        "caelum" to "ПОГОДА", "lux" to "СВЕТЛЫЙ_ДЕНЬ", "aer" to "ВОЗДУХ", "corpus" to "ТЕЛО", "nutritio" to "ПИТАНИЕ", "cibus" to "ЕДА",
        "robur" to "ЗАЛ", "musculi" to "МЫШЦЫ", "voces" to "ЗВОНКИ", "soni" to "ЗВУК", "retia" to "СЕТЬ", "lumen" to "СВЕТ",
        "vis" to "ЗАРЯД", "lectio" to "ЧТЕНИЕ", "strepitus" to "ШУМ", "luna" to "ЛУНА", "dias" to "ДНИ", "animus" to "НАСТРОЕНИЕ",
        "optimus" to "ЛУЧШИЙ", "momenta" to "МОМЕНТЫ", "svmptvs" to "РАСХОДЫ", "memorabilia" to "ВЕХИ", "ascensvs" to "ЛАЗАНИЕ",
        "dies" to "ДНИ", "quaestiones" to "ОПРОСЫ", "celeritas" to "РЕАКЦИЯ", "bibliotheca" to "БИБЛИОТЕКА",
        "synchronia" to "СИНХРОНИЗАЦИЯ", "opera" to "РАБОТЫ", "iudicium" to "ОЦЕНКА", "citatio" to "ЦИТАТА", "sententia" to "ФРАЗА",
        "orbis" to "МИР", "nova" to "НОВОЕ", "mirabilia" to "ЧУДЕСА", "somnium" to "СНОВИДЕНИЕ", "epistulae" to "ПИСЬМА",
        "aperta" to "ОТКРЫТО", "initium" to "НАЧАЛО", "finis" to "КОНЕЦ", "pessimus" to "ХУДШИЙ", "personae" to "ЛЮДИ",
        "themata" to "ТЕМЫ", "praeclarum" to "ЛУЧШЕЕ", "dies felix" to "ХОРОШИЙ_ДЕНЬ", "consuetudo" to "ПРИВЫЧКИ", "series" to "СЕРИЯ",
        "palmae" to "УСПЕХИ", "reditus" to "ДОХОДЫ", "apps" to "ПРИЛОЖЕНИЯ",
    )

    /** Mongol en cirílico, en mayúsculas. */
    val MONGOL: Map<String, String> = mapOf(
        "horologium" to "ЦАГ", "somnus" to "НОЙР", "stadia" to "ҮЕ_ШАТ", "cor" to "ЗҮРХ", "passus" to "АЛХАМ", "motus" to "ХӨДӨЛГӨӨН",
        "iter" to "ЗАМ", "exercitatio" to "ДАСГАЛ", "loca" to "ГАЗРУУД", "telephonum" to "ДЭЛГЭЦ", "telephon" to "ДЭЛГЭЦ",
        "usus" to "ХЭРЭГЛЭЭ", "monita" to "МЭДЭГДЭЛ", "musica" to "ХӨГЖИМ", "imagines" to "ЗУРАГ", "fasti" to "ХУВААРЬ",
        "caelum" to "ЦАГ_АГААР", "lux" to "ӨДРИЙН_ГЭРЭЛ", "aer" to "АГААР", "corpus" to "БИЕ", "nutritio" to "ТЭЖЭЭЛ", "cibus" to "ХООЛ",
        "robur" to "ХҮЧ", "musculi" to "БУЛЧИН", "voces" to "ДУУДЛАГА", "soni" to "ДУУ", "retia" to "СҮЛЖЭЭ", "lumen" to "ГЭРЭЛ",
        "vis" to "ЦЭНЭГ", "lectio" to "УНШЛАГА", "strepitus" to "ЧИМЭЭ", "luna" to "САР", "dias" to "ӨДРҮҮД", "animus" to "СЭТГЭЛ",
        "optimus" to "ШИЛДЭГ", "momenta" to "АГШИН", "svmptvs" to "ЗАРДАЛ", "memorabilia" to "ГАВЬЯА", "ascensvs" to "АВИРАЛТ",
        "dies" to "ӨДӨР", "quaestiones" to "АСУУЛГА", "celeritas" to "ХАРИУ", "bibliotheca" to "НОМЫН_САН", "synchronia" to "СИНХРОН",
        "opera" to "БҮТЭЭЛ", "iudicium" to "ҮНЭЛГЭЭ", "citatio" to "ИШЛЭЛ", "sententia" to "ӨГҮҮЛБЭР", "orbis" to "ДЭЛХИЙ",
        "nova" to "ШИНЭ", "mirabilia" to "ГАЙХАМШИГ", "somnium" to "ЗҮҮД", "epistulae" to "ЗАХИДАЛ", "aperta" to "НЭЭСЭН",
        "initium" to "ЭХЛЭЛ", "finis" to "ТӨГСГӨЛ", "pessimus" to "МУУ", "personae" to "ХҮМҮҮС", "themata" to "СЭДЭВ",
        "praeclarum" to "САЙН", "dies felix" to "САЙХАН_ӨДӨР", "consuetudo" to "ЗУРШИЛ", "series" to "ЦУВРАЛ", "palmae" to "АМЖИЛТ",
        "reditus" to "ОРЛОГО", "apps" to "АПП",
    )

    /** Castellano de los siglos XIII a XV, como escribía el copista. */
    val CASTELLANO_ANTIGUO: Map<String, String> = mapOf(
        "horologium" to "relox", "somnus" to "suenno", "stadia" to "tiempos", "cor" to "coraçon", "passus" to "passos",
        "motus" to "mouimiento", "iter" to "camino", "exercitatio" to "exerçiçio", "loca" to "logares", "telephonum" to "tablilla",
        "telephon" to "tablilla", "usus" to "usança", "monita" to "pregones", "musica" to "cantigas", "imagines" to "pinturas",
        "fasti" to "almanaque", "caelum" to "çielo", "lux" to "claridad", "aer" to "ayre", "corpus" to "carne",
        "nutritio" to "mantenimiento", "cibus" to "yantares", "robur" to "fuerça", "musculi" to "nervios", "voces" to "vozes",
        "soni" to "sones", "retia" to "redes", "lumen" to "lumbre", "vis" to "vigor", "lectio" to "leyenda", "strepitus" to "roído",
        "luna" to "luna", "dias" to "días", "animus" to "ánima", "optimus" to "mejor", "momenta" to "sucesos", "svmptvs" to "despensa",
        "memorabilia" to "memorias", "ascensvs" to "subida", "dies" to "jornadas", "quaestiones" to "questiones",
        "celeritas" to "presteza", "bibliotheca" to "librería", "synchronia" to "concordia", "opera" to "obras", "iudicium" to "juyzio",
        "citatio" to "dicho", "sententia" to "sentençia", "orbis" to "orbe", "nova" to "nueuo", "mirabilia" to "marauillas",
        "somnium" to "ensuenno", "epistulae" to "epístolas", "aperta" to "abierta", "initium" to "comienço", "finis" to "cabo",
        "pessimus" to "peor", "personae" to "gentes", "themata" to "materias", "praeclarum" to "la_mejor", "dies felix" to "buen_día",
        "consuetudo" to "costumbres", "series" to "continuo", "palmae" to "fechos", "reditus" to "rentas", "apps" to "ingenios",
    )

    /** Palabras del cuartel general que reemplazan al francés común. */
    val NAPOLEON: Map<String, String> = mapOf(
        "somnus" to "bivouac", "palmae" to "victoires", "svmptvs" to "trésor", "personae" to "état-major", "momenta" to "bulletins",
        "iter" to "marche", "epistulae" to "dépêches", "robur" to "manège", "consuetudo" to "code", "reditus" to "solde",
        "orbis" to "empire",
    )

    /** Palabras de la Convención que reemplazan al francés común. */
    val REVOLUCION: Map<String, String> = mapOf(
        "somnus" to "repos", "animus" to "esprit", "personae" to "citoyens", "palmae" to "honneurs", "consuetudo" to "vertus",
        "svmptvs" to "assignats", "momenta" to "journées", "epistulae" to "adresses", "reditus" to "contributions", "themata" to "motions",
        "iudicium" to "opinion", "orbis" to "nations",
    )

    /** Latín litúrgico del gótico (el resto queda en el latín del romano). */
    val GOTICO: Map<String, String> = mapOf(
        "somnus" to "requies", "animus" to "anima", "personae" to "fratres", "palmae" to "reliquiae", "epistulae" to "litterae",
        "cor" to "cor_cordis", "momenta" to "horae", "svmptvs" to "eleemosyna", "loca" to "loca_sancta", "imagines" to "vitrea",
        "musica" to "cantus", "lectio" to "lectio_divina", "consuetudo" to "regula", "iter" to "peregrinatio", "dies" to "feriae",
        "dias" to "feriae", "caelum" to "firmamentum", "lux" to "lux_diei", "orbis" to "orbis_christianus", "opera" to "codices",
        "mirabilia" to "miracula", "initium" to "incipit", "finis" to "explicit", "reditus" to "decimae", "themata" to "materiae",
    )

    /** Jerga del investigador del archivo (horror cósmico). Lo que no está aquí queda en el español común. */
    val ARCANO: Map<String, String> = mapOf(
        "somnus" to "desvelo", "animus" to "nervios", "passus" to "rondas", "cor" to "latido", "loca" to "parajes",
        "personae" to "testigos", "palmae" to "pruebas", "svmptvs" to "expensas", "imagines" to "placas", "lectio" to "legajos",
        "caelum" to "bruma", "lux" to "claridad", "luna" to "fase", "strepitus" to "rumores", "momenta" to "apuntes",
        "iter" to "pesquisa", "orbis" to "cartografía", "dies" to "noches", "dias" to "noches", "consuetudo" to "precauciones",
        "epistulae" to "lacradas", "somnium" to "soñé_con", "memorabilia" to "sucesos", "opera" to "tomos", "telephonum" to "aparato",
        "telephon" to "aparato", "musica" to "melodías", "initium" to "primera_nota", "finis" to "última_nota", "series" to "noches_seguidas",
    )

    /** Jerga del ministerio. Lo que no está aquí queda en el español común. */
    val DISTOPIA: Map<String, String> = mapOf(
        "somnus" to "descanso", "animus" to "estado", "svmptvs" to "ración", "cor" to "pulso", "loca" to "zonas",
        "personae" to "ciudadanos", "consuetudo" to "cuotas", "palmae" to "méritos", "telephonum" to "monitor", "telephon" to "monitor",
        "iudicium" to "dictamen", "epistulae" to "misivas", "reditus" to "asignación", "momenta" to "incidencias",
        "imagines" to "evidencia", "iter" to "trayecto", "orbis" to "territorio", "themata" to "asuntos", "memorabilia" to "hechos",
    )

    /** Jerga del refugio. Lo que no está aquí queda en el español común. */
    val REFUGIO: Map<String, String> = mapOf(
        "somnus" to "ciclo_sueño", "animus" to "estado", "svmptvs" to "suministros", "aer" to "calidad_aire", "caelum" to "exterior",
        "lux" to "luz_exterior", "loca" to "sectores", "personae" to "residentes", "palmae" to "insignias", "corpus" to "biometría",
        "consuetudo" to "protocolos", "reditus" to "créditos", "strepitus" to "ruido_sector", "momenta" to "sucesos",
        "orbis" to "superficie", "vis" to "energía", "retia" to "red_interna", "epistulae" to "mensajes",
    )

    /** Jerga de la orden. Lo que no está aquí queda en el español común. */
    val ORDEN: Map<String, String> = mapOf(
        "somnus" to "reposo", "animus" to "moral", "svmptvs" to "intendencia", "passus" to "marcha", "iter" to "avance",
        "robur" to "entreno", "loca" to "posiciones", "personae" to "hermanos", "palmae" to "medallas", "consuetudo" to "disciplina",
        "epistulae" to "despachos", "reditus" to "soldada", "momenta" to "bitácora", "orbis" to "frentes", "telephonum" to "terminal",
        "telephon" to "terminal", "themata" to "materias", "opera" to "archivo",
    )

    /** Jerga del laboratorio. Lo que no está aquí queda en el español común. */
    val LABORATORIO: Map<String, String> = mapOf(
        "animus" to "afecto", "cor" to "fc", "svmptvs" to "recursos", "personae" to "sujetos", "loca" to "coordenadas",
        "consuetudo" to "variables", "iudicium" to "valoración", "palmae" to "resultados", "somnium" to "ensoñación",
        "momenta" to "observaciones", "imagines" to "muestras", "dies" to "sesiones", "quaestiones" to "instrumentos",
        "celeritas" to "latencia_ms", "lux" to "lux_diurna",
    )

    /** Habla del pueblo. Lo que no está aquí queda en el español común. */
    val COLONOS: Map<String, String> = mapOf(
        "somnus" to "dormida", "animus" to "humor", "svmptvs" to "cuentas", "personae" to "vecinos", "loca" to "parajes",
        "caelum" to "el_tiempo", "robur" to "fuerza", "palmae" to "medallas", "reditus" to "ganancias", "iter" to "camino",
        "momenta" to "sucedidos", "imagines" to "estampas", "telephonum" to "aparato", "telephon" to "aparato", "epistulae" to "correo",
        "orbis" to "lejanías",
    )

    /** Jerga del naturalista. Lo que no está aquí queda en el español común. */
    val BOTANICO: Map<String, String> = mapOf(
        "somnus" to "latencia", "animus" to "temple", "passus" to "andadura", "loca" to "localidades", "personae" to "compañía",
        "imagines" to "láminas", "lectio" to "lecturas", "caelum" to "meteoro", "lux" to "fotoperiodo", "aer" to "atmósfera",
        "consuetudo" to "cuidados", "palmae" to "hallazgos", "svmptvs" to "semillas", "strepitus" to "rumor", "cibus" to "sustento",
        "iter" to "itinerario", "orbis" to "territorio", "momenta" to "observaciones", "dies" to "jornadas",
    )

    /** Voz de la casa en Navidad. Lo que no está aquí queda en el español común. */
    val NAVIDAD: Map<String, String> = mapOf(
        "somnus" to "noche_de_paz", "animus" to "sentir", "svmptvs" to "regalos", "personae" to "familia", "palmae" to "estrellas",
        "epistulae" to "cartitas", "consuetudo" to "propósitos", "caelum" to "afuera", "lux" to "sol", "loca" to "rincones",
        "cibus" to "cena", "momenta" to "momentitos", "imagines" to "postales", "musica" to "villancicos", "orbis" to "lo_lejano",
    )

    /** Jerga de la sala de control. Lo que no está aquí queda en el español común. */
    val ESPACIO: Map<String, String> = mapOf(
        "animus" to "estado", "cor" to "ritmo", "iter" to "trayectoria", "loca" to "posición", "lux" to "iluminación", "caelum" to "meteo",
        "aer" to "atmósfera", "telephonum" to "consola", "telephon" to "consola", "svmptvs" to "combustible", "personae" to "tripulación",
        "palmae" to "insignias", "epistulae" to "transmisiones", "consuetudo" to "rutinas", "momenta" to "eventos",
        "dies" to "soles_de_misión", "orbis" to "planeta", "vis" to "energía",
    )

    /** Habla del camino. Lo que no está aquí queda en el español común. */
    val ANTIGUAS: Map<String, String> = mapOf(
        "somnus" to "lecho", "animus" to "temple", "svmptvs" to "monedas", "passus" to "andanza", "iter" to "camino", "loca" to "tierras",
        "personae" to "compañía", "palmae" to "hazañas", "consuetudo" to "juramentos", "epistulae" to "pergaminos", "caelum" to "cielo",
        "telephonum" to "espejo", "telephon" to "espejo", "imagines" to "grabados", "musica" to "cantares", "reditus" to "tributos",
        "momenta" to "sucesos", "orbis" to "el_ancho_mundo",
    )
}
