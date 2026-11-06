package io.github.ruddy2003.vestigium.data

import io.github.ruddy2003.vestigium.ciencia.TusNumeros
import kotlin.math.roundToInt

/**
 * Las recomendaciones oficiales de salud para adultos (OMS y organismos parecidos) frente a tus datos de los últimos 30 días.
 * No es consejo médico: son las metas generales para población adulta sana; tu caso puede ser distinto.
 */
object Guias {
    enum class Estado { LEJOS, CERCA, BIEN, INFO, SIN_DATOS }

    class Fuente(val nombre: String, val url: String)

    /** Una franja de la escala: de [desde] a [hasta], con su estado. */
    class Zona(val desde: Double, val hasta: Double, val estado: Estado)

    class Item(
        val id: String, val grupo: String, val titulo: String, val meta: String,
        val valor: Double?, val unidad: String, val texto: String, val estado: Estado,
        val min: Double = 0.0, val max: Double = 1.0, val zonas: List<Zona> = emptyList(),
        val detalle: String = "", val fuente: Fuente? = null, val dias: Int = 0, val desde: String = "", val hasta: String = "",
        val como: String = "",
    )

    private val OMS_ACTIVIDAD = Fuente("OMS, Directrices sobre actividad física y hábitos sedentarios (2020)", "https://www.who.int/news-room/fact-sheets/detail/physical-activity")
    private val IMC = Fuente("OMS, Obesidad y sobrepeso: clasificación del IMC en adultos", "https://www.who.int/news-room/fact-sheets/detail/obesity-and-overweight")
    private val PASOS = Fuente("Ding y otros, Lancet Public Health (2025): pasos diarios y salud", "https://pubmed.ncbi.nlm.nih.gov/40713949/")
    private val SUENO = Fuente("AASM y Sleep Research Society: consenso sobre la duración del sueño en adultos (2015)", "https://aasm.org/seven-or-more-hours-of-sleep-per-night-a-health-necessity-for-adults/")
    private val AZUCAR = Fuente("OMS, Guía sobre la ingesta de azúcares (2015)", "https://www.ncbi.nlm.nih.gov/books/NBK285538/")
    private val SAL = Fuente("OMS: reducir la sal (menos de 5 g de sal, 2 g de sodio al día)", "https://www.who.int/europe/news/item/14-03-2022-5-recommendations-to-reduce-salt-intake-to-live-longer-and-healthier-lives")
    private val DIETA = Fuente("OMS, Alimentación sana (hoja informativa)", "https://www.who.int/news-room/fact-sheets/detail/healthy-diet")
    private val FIBRA = Fuente("OMS, Guías sobre grasas y carbohidratos (2023)", "https://nutritionsource.hsph.harvard.edu/2023/07/17/who-updated-guidelines-healthy-diets-total-fat/")
    private val ALCOHOL = Fuente("OMS Europa: ningún nivel de consumo de alcohol es seguro (2023)", "https://www.who.int/azerbaijan/news/item/04-01-2023-no-level-of-alcohol-consumption-is-safe-for-our-health")
    private val CAFEINA = Fuente("EFSA, Opinión científica sobre la seguridad de la cafeína (2015)", "https://www.efsa.europa.eu/en/topics/topic/caffeine")
    private val AIRE = Fuente("OMS, Guías de calidad del aire (2021)", "https://www.who.int/news-room/fact-sheets/detail/ambient-(outdoor)-air-quality-and-health")
    private val WHO5 = Fuente("Topp y otros (2015): el índice de bienestar WHO-5", "https://pmc.ncbi.nlm.nih.gov/articles/PMC6961185")
    private val SOLEDAD = Fuente("OMS, Comisión sobre Conexión Social (2025)", "https://www.who.int/news/item/30-06-2025-social-connection-linked-to-improved-heath-and-reduced-risk-of-early-death")
    private val NIH = Fuente("NIH, Office of Dietary Supplements: ingestas diarias recomendadas", "https://ods.od.nih.gov/HealthInformation/nutrientrecommendations.aspx")
    private val PROTEINA = Fuente("OMS/FAO/UNU, Necesidades de proteínas en la nutrición humana (2007)", "https://iris.who.int/handle/10665/43411")
    private val AGUA = Fuente("EFSA, Valores de referencia para el agua (2010)", "https://www.efsa.europa.eu/en/press/news/nda100326")
    private val FC = Fuente("American Heart Association: frecuencia cardiaca en reposo", "https://www.heart.org/en/healthy-living/fitness/fitness-basics/target-heart-rates")

    private fun n(x: Double, d: Int = 0): String = if (d == 0) "%,d".format(x.roundToInt()) else "%.${d}f".format(x)

    /** Hombre, mujer o sin elegir (se usa el promedio de los dos). */
    private fun ref(h: Double, m: Double): Double = when (Perfiles.sexo()) { "hombre" -> h; "mujer" -> m; else -> (h + m) / 2 }

    fun evaluar(t: Map<String, TusNumeros.Valor>): List<Item> {
        val out = ArrayList<Item>()
        fun v(k: String) = t[k]
        fun item(id: String, grupo: String, titulo: String, meta: String, k: String?, unidad: String, min: Double, max: Double, zonas: List<Zona>, fuente: Fuente,
                 texto: (Double) -> String, detalle: String = "", como: String = "", valor: Double? = null) {
            val x = t[k ?: ""]
            val val0 = valor ?: x?.v
            if (val0 == null) { out += Item(id, grupo, titulo, meta, null, unidad, "Sin datos todavía.", Estado.SIN_DATOS, min, max, zonas, detalle, fuente, como = como); return }
            val est = zonas.firstOrNull { val0 >= it.desde && val0 < it.hasta }?.estado ?: zonas.lastOrNull()?.estado ?: Estado.INFO
            out += Item(id, grupo, titulo, meta, val0, unidad, texto(val0), est, min, max, zonas, detalle, fuente, x?.dias ?: 0, x?.desde ?: "", x?.hasta ?: "", como)
        }
        val L = Estado.LEJOS; val C = Estado.CERCA; val B = Estado.BIEN

        // ------------------------------------------------ moverte
        item("pasos", "Moverte", "Pasos al día", "7,000 o más al día; entre 8,000 y 10,000 si tienes menos de 60 años", "pasos", "pasos", 0.0, 14000.0,
            listOf(Zona(0.0, 5000.0, L), Zona(5000.0, 7000.0, C), Zona(7000.0, 1e9, B)), PASOS,
            { x -> when { x < 5000 -> "Das ${n(x)} pasos: por debajo de lo que se asocia con menos riesgo. Subir a 7,000 es el salto que más cuenta."; x < 7000 -> "Das ${n(x)}: cerca. Con ${n(7000 - x)} más al día llegas a los 7,000."; else -> "Das ${n(x)} pasos al día: vas bien." } },
            "En 2025, el análisis más grande (57 estudios) encontró que 7,000 pasos diarios se asocian con casi la mitad del riesgo de morir por cualquier causa que 2,000; más allá de 7,000 el beneficio sigue, pero más despacio.",
            "Conecta Health Connect con tu reloj o tu teléfono en Permisos y sensores.")
        item("actividad", "Moverte", "Actividad moderada a la semana", "150 a 300 minutos (o 75 a 150 de intensa)", "ejercicio_sem", "min por semana", 0.0, 420.0,
            listOf(Zona(0.0, 75.0, L), Zona(75.0, 150.0, C), Zona(150.0, 1e9, B)), OMS_ACTIVIDAD,
            { x -> when { x < 75 -> "Unos ${n(x)} minutos por semana: menos de la mitad de lo recomendado."; x < 150 -> "Unos ${n(x)} minutos por semana: te faltan ${n(150 - x)}."; x <= 300 -> "Unos ${n(x)} minutos por semana: dentro de lo recomendado."; else -> "Unos ${n(x)} minutos por semana: más de lo recomendado, con beneficios extra." } },
            "Cuenta la actividad que acelera la respiración (caminar rápido, bici, escalar, nadar). Lo registra tu reloj como sesiones de ejercicio.",
            "Tu reloj debe guardar sesiones de ejercicio en Health Connect.")
        item("fuerza", "Moverte", "Días de fuerza a la semana", "2 o más días", "fuerza_sem", "días", 0.0, 5.0,
            listOf(Zona(0.0, 1.0, L), Zona(1.0, 2.0, C), Zona(2.0, 1e9, B)), OMS_ACTIVIDAD,
            { x -> if (x >= 2) "Entrenas fuerza ${porNumero(x, "1 día", "${n(x, 1)} días")} por semana: cumples." else "Entrenas fuerza ${porNumero(x, "1 día", "${n(x, 1)} días")} por semana; la OMS recomienda al menos 2." },
            "Ejercicios de fuerza de los grupos musculares grandes. Sale de tus entrenamientos de MacroFactor Workouts.", "Importa MacroFactor Workouts en Datos.")

        // ------------------------------------------------ dormir
        item("sueno", "Dormir", "Horas de sueño", "7 a 9 horas", "sueno", "h", 4.0, 10.5,
            listOf(Zona(0.0, 6.0, L), Zona(6.0, 7.0, C), Zona(7.0, 9.01, B), Zona(9.01, 99.0, C)), SUENO,
            { x -> when { x < 6 -> "Duermes ${n(x, 1)} h: poco. Dormir menos de 7 de forma habitual se asocia con más riesgo de casi todo."; x < 7 -> "Duermes ${n(x, 1)} h: un poco menos de lo recomendado."; x <= 9 -> "Duermes ${n(x, 1)} h: dentro de lo recomendado."; else -> "Duermes ${n(x, 1)} h: más de 9 de forma habitual también vale la pena mirarlo." } },
            "Es el sueño que registra tu reloj (no el tiempo en cama).", "Usa un reloj que registre el sueño en Health Connect.")

        // ------------------------------------------------ comer
        val anadida = t.containsKey("azucar_anadida")
        val azPct = v("azucar_pct")?.v
        if (anadida && azPct != null) item("azucar", "Comer", "Azúcar añadida", "menos del 10% de las calorías; mejor menos del 5%", "azucar_pct", "% de la energía", 0.0, 20.0,
            listOf(Zona(0.0, 5.0, B), Zona(5.0, 10.0, C), Zona(10.0, 1e9, L)), AZUCAR,
            { x -> val g = v("azucar")?.v ?: 0.0
                when { x < 5 -> "El ${n(x, 1)}% de tus calorías (${n(g)} g al día): por debajo incluso de la meta ideal."; x < 10 -> "El ${n(x, 1)}% de tus calorías (${n(g)} g, unas ${n(g / 4)} cucharaditas): dentro del límite, sobre la meta ideal del 5%."
                    else -> "El ${n(x, 1)}% de tus calorías (${n(g)} g, unas ${n(g / 4)} cucharaditas al día): por encima del límite de la OMS." } },
            "Azúcar libre: la que se añade a alimentos y bebidas, más la de la miel, los jarabes y los jugos. No cuenta la de la fruta entera ni la de la leche.")
        else item("azucar", "Comer", "Azúcares", "azúcar añadida: menos del 10% de las calorías (unos 50 g)", "azucar", "g", 0.0, 120.0,
            listOf(Zona(0.0, 50.0, B), Zona(50.0, 90.0, C), Zona(90.0, 1e9, L)), AZUCAR,
            { x -> "Comes ${n(x)} g de azúcares al día, contando los de la fruta y la leche. MacroFactor no separó el azúcar añadida; la meta de la OMS es solo para esa." },
            "", "Importa la exportación de MacroFactor (incluye azúcares y, si lo activas, azúcar añadida).")
        item("sodio", "Comer", "Sodio", "menos de 2,000 mg (5 g de sal)", "sodio", "mg", 0.0, 4500.0,
            listOf(Zona(0.0, 2000.0, B), Zona(2000.0, 3000.0, C), Zona(3000.0, 1e9, L)), SAL,
            { x -> if (x < 2000) "${n(x)} mg al día: dentro de la recomendación." else "${n(x)} mg al día, como ${n(x * 2.5 / 1000, 1)} g de sal: por encima de los 2,000 mg (la mayoría del mundo está ahí)." },
            "La sal está sobre todo en lo procesado: pan, embutidos, sopas y botanas.", "Importa MacroFactor.")
        item("potasio", "Comer", "Potasio", "3,510 mg o más", "potasio", "mg", 0.0, 5000.0,
            listOf(Zona(0.0, 2500.0, L), Zona(2500.0, 3510.0, C), Zona(3510.0, 1e9, B)), SAL,
            { x -> if (x >= 3510) "${n(x)} mg al día: cumples." else "${n(x)} mg al día: debajo de los 3,510 que recomienda la OMS. Frijoles, plátano, papa, aguacate y verduras de hoja lo suben." },
            "El potasio ayuda a contrarrestar el efecto del sodio en la presión.", "Importa MacroFactor.")
        item("fibra", "Comer", "Fibra", "25 g o más al día", "fibra", "g", 0.0, 45.0,
            listOf(Zona(0.0, 15.0, L), Zona(15.0, 25.0, C), Zona(25.0, 1e9, B)), FIBRA,
            { x -> if (x >= 25) "${n(x)} g al día: cumples." else "${n(x)} g al día: te faltan ${n(25 - x)} g. Frijoles, avena, tortilla de maíz, fruta con cáscara y verduras." },
            "La OMS pide al menos 25 g de fibra natural y al menos 400 g de frutas y verduras al día.", "Importa MacroFactor.")
        item("grasa_sat", "Comer", "Grasa saturada", "menos del 10% de las calorías", "grasa_sat_pct", "% de la energía", 0.0, 20.0,
            listOf(Zona(0.0, 10.0, B), Zona(10.0, 13.0, C), Zona(13.0, 1e9, L)), DIETA,
            { x -> if (x < 10) "El ${n(x, 1)}% de tus calorías: dentro." else "El ${n(x, 1)}% de tus calorías: por encima del 10%." },
            "Mantequilla, carnes grasas, quesos, coco y palma. Cambiarla por grasa insaturada (aceite de oliva, aguacate, nueces) es lo recomendado.", "Importa MacroFactor.")
        item("grasa_total", "Comer", "Grasa total", "30% de las calorías o menos", "grasa_pct", "% de la energía", 0.0, 50.0,
            listOf(Zona(0.0, 30.0, B), Zona(30.0, 35.0, C), Zona(35.0, 1e9, L)), DIETA,
            { x -> "El ${n(x, 0)}% de tus calorías viene de grasa." }, "Para prevenir el aumento de peso no saludable.", "Importa MacroFactor.")
        if (t.containsKey("trans_pct")) item("trans", "Comer", "Grasas trans", "menos del 1% de las calorías", "trans_pct", "% de la energía", 0.0, 3.0,
            listOf(Zona(0.0, 1.0, B), Zona(1.0, 1e9, L)), DIETA, { x -> "El ${n(x, 2)}% de tus calorías." })
        val peso = v("peso")?.v
        // Índice de masa corporal: necesita tu estatura (Conocerte) y tu peso.
        val est = Repo.config.perfil.estatura_cm?.takeIf { it in 100..250 }
        if (est != null || peso != null) item("imc", "Cuerpo", "Índice de masa corporal", "18.5 a 24.9", null, "kg/m²", 15.0, 40.0,
            listOf(Zona(0.0, 18.5, C), Zona(18.5, 25.0, B), Zona(25.0, 30.0, C), Zona(30.0, 1e9, L)), IMC,
            { x -> "${n(x, 1)} con ${n(peso ?: 0.0, 1)} kg y ${est} cm. " + when { x < 18.5 -> "Por debajo del rango."; x < 25 -> "Dentro del rango."; x < 30 -> "Sobrepeso según la OMS (con mucho músculo el IMC exagera)."; else -> "Obesidad según la OMS." } },
            "El IMC no distingue músculo de grasa; la cintura (menos de la mitad de tu estatura) ayuda a completarlo.", "Necesita tu estatura (Más → Conocerte) y tu peso (reloj, báscula o MacroFactor).",
            valor = if (est != null && peso != null && peso > 30) peso / ((est / 100.0) * (est / 100.0)) else null)
        item("proteina", "Comer", "Proteína", "al menos 0.83 g por kilo de peso", null, "g por kg", 0.0, 2.4,
            listOf(Zona(0.0, 0.66, L), Zona(0.66, 0.83, C), Zona(0.83, 1e9, B)), PROTEINA,
            { x -> "${n(x, 2)} g por kilo (${n(v("proteina")?.v ?: 0.0)} g al día con ${n(peso ?: 0.0, 1)} kg): " + if (x >= 0.83) "cubres lo mínimo; si entrenas fuerza, 1.6 g/kg es lo que suele maximizar el músculo." else "por debajo del mínimo." },
            "", "Necesita tu peso (reloj, báscula o MacroFactor) y tu proteína (MacroFactor).", valor = if (peso != null && peso > 30) v("proteina")?.v?.div(peso) else null)
        item("agua", "Comer", "Agua", "${n(ref(2.5, 2.0), 1)} L al día en total, contando la de la comida", "agua", "L", 0.0, 4.0,
            listOf(Zona(0.0, ref(2.5, 2.0) * 0.7, L), Zona(ref(2.5, 2.0) * 0.7, ref(2.5, 2.0), C), Zona(ref(2.5, 2.0), 1e9, B)), AGUA,
            { x -> "${n(x, 1)} L al día registrados." }, "Más con calor o ejercicio.", "Registra el agua en MacroFactor o en tu reloj.")

        // ------------------------------------------------ alcohol y cafeína
        item("alcohol", "Alcohol y cafeína", "Alcohol", "no hay un nivel seguro; menos siempre es mejor", "alcohol", "g al día", 0.0, 40.0,
            listOf(Zona(0.0, 0.5, B), Zona(0.5, 14.0, C), Zona(14.0, 1e9, L)), ALCOHOL,
            { x -> if (x < 0.5) "No tomas alcohol: lo mejor para la salud." else "${n(x, 1)} g al día, como ${n(x * 7 / 10, 1)} bebidas estándar (de 10 g) por semana. La OMS dice que el riesgo empieza desde la primera copa." },
            "Una bebida estándar tiene 10 g de alcohol puro: una cerveza de 330 ml, una copa de vino de 100 ml o un caballito de 30 ml.", "Registra el alcohol en MacroFactor.")
        item("cafeina", "Alcohol y cafeína", "Cafeína", "400 mg o menos al día (200 mg por toma)", "cafeina", "mg", 0.0, 700.0,
            listOf(Zona(0.0, 400.0, B), Zona(400.0, 500.0, C), Zona(500.0, 1e9, L)), CAFEINA,
            { x -> if (x <= 400) "${n(x)} mg al día (unas ${n(x / 95, 1)} tazas de café): dentro de lo seguro." else "${n(x)} mg al día: por encima de los 400 mg." },
            "Una taza de café filtrado tiene unos 95 mg; un espresso, unos 65.", "Registra la cafeína en MacroFactor.")

        // ------------------------------------------------ corazón y cuerpo
        item("fc", "Corazón", "Frecuencia cardiaca en reposo", "60 a 100 latidos por minuto (menos si haces mucho ejercicio)", "fc_reposo", "lpm", 35.0, 110.0,
            listOf(Zona(0.0, 50.0, Estado.INFO), Zona(50.0, 75.0, B), Zona(75.0, 100.0, C), Zona(100.0, 1e9, L)), FC,
            { x -> "${n(x)} lpm en reposo." + if (x < 60) " Por debajo de 60 es normal en gente activa." else if (x > 100) " Por encima de 100 en reposo, vale la pena consultarlo." else "" },
            "Más baja suele ir con mejor condición física.", "Tu reloj la registra en Health Connect.")
        if (t.containsKey("pa_sis")) {
            val s = v("pa_sis")!!.v; val d = v("pa_dia")?.v ?: 0.0
            item("pa", "Corazón", "Presión arterial", "menos de 120/80", null, "mmHg", 90.0, 160.0,
                listOf(Zona(0.0, 120.0, B), Zona(120.0, 140.0, C), Zona(140.0, 1e9, L)), FC,
                { "${n(s)}/${n(d)} mmHg." + if (s >= 140 || d >= 90) " En ese rango la OMS habla de hipertensión: consúltalo." else "" }, valor = s)
        }

        // ------------------------------------------------ mente y gente
        item("who5", "Mente y gente", "Bienestar (WHO-5)", "50 o más de 100", "who5", "de 100", 0.0, 100.0,
            listOf(Zona(0.0, 29.0, L), Zona(29.0, 50.0, C), Zona(50.0, 1e9, B)), WHO5,
            { x -> when { x < 29 -> "${n(x)} de 100. Si se repite varias semanas, vale la pena hablarlo con alguien de confianza o un profesional de salud."; x < 50 -> "${n(x)} de 100: por debajo de lo habitual."; else -> "${n(x)} de 100: en rango habitual o alto." } },
            "Es tu último cuestionario de bienestar.", "Contesta el cuestionario de bienestar en Medir.")
        item("soledad", "Mente y gente", "Soledad (UCLA-3)", "menos de 6 (de 3 a 9)", "ucla3", "de 9", 3.0, 9.0,
            listOf(Zona(0.0, 5.0, B), Zona(5.0, 6.0, C), Zona(6.0, 1e9, L)), SOLEDAD,
            { x -> if (x >= 6) "${n(x)} de 9: en el rango que los estudios llaman soledad. La OMS la considera un riesgo para la salud tan serio como otros más conocidos." else "${n(x)} de 9: sin señales de soledad." },
            "La OMS calcula que 1 de cada 6 personas en el mundo se siente sola.", "Contesta el cuestionario de soledad en Medir.")

        // ------------------------------------------------ lo de fuera
        item("pm25", "Lo de fuera", "Aire (PM2.5)", "menos de 15 µg/m³ en un día; 5 de promedio al año", "pm25", "µg/m³", 0.0, 60.0,
            listOf(Zona(0.0, 5.0, B), Zona(5.0, 15.0, C), Zona(15.0, 1e9, L)), AIRE,
            { x -> "Respiraste en promedio ${n(x, 1)} µg/m³ de partículas finas." + if (x > 15) " Por encima del límite diario de la OMS." else "" },
            "El 99% de la gente del mundo respira aire por encima de la guía de la OMS.", "Activa el clima en Permisos y sensores.")
        return out
    }

    /** Vitaminas y minerales frente a la ingesta recomendada (RDA o ingesta adecuada) de adultos de 19 a 50 años. */
    class Micro(val nombre: String, val tuyo: Double?, val meta: Double, val unidad: String, val limite: Double? = null) {
        val pct: Double? get() = tuyo?.let { it * 100 / meta }
    }

    private class DefMicro(val nombre: String, val claves: List<String>, val h: Double, val m: Double, val unidad: String, val limite: Double? = null)
    private val MICROS = listOf(
        DefMicro("Vitamina A", listOf("vitamin_a"), 900.0, 700.0, "µg", 3000.0),
        DefMicro("Vitamina C", listOf("vitamin_c"), 90.0, 75.0, "mg", 2000.0),
        DefMicro("Vitamina D", listOf("vitamin_d"), 15.0, 15.0, "µg", 100.0),
        DefMicro("Vitamina E", listOf("vitamin_e"), 15.0, 15.0, "mg"),
        DefMicro("Vitamina K", listOf("vitamin_k"), 120.0, 90.0, "µg"),
        DefMicro("Tiamina (B1)", listOf("thiamin", "b1_"), 1.2, 1.1, "mg"),
        DefMicro("Riboflavina (B2)", listOf("riboflavin", "b2_"), 1.3, 1.1, "mg"),
        DefMicro("Niacina (B3)", listOf("niacin", "b3_"), 16.0, 14.0, "mg"),
        DefMicro("Vitamina B6", listOf("pyridox", "b6"), 1.3, 1.3, "mg"),
        DefMicro("Folato", listOf("folate", "folato"), 400.0, 400.0, "µg"),
        DefMicro("Vitamina B12", listOf("cobalamin", "b12"), 2.4, 2.4, "µg"),
        DefMicro("Colina", listOf("choline", "colina"), 550.0, 425.0, "mg"),
        DefMicro("Calcio", listOf("calcio", "calcium"), 1000.0, 1000.0, "mg", 2500.0),
        DefMicro("Hierro", listOf("hierro", "iron"), 8.0, 18.0, "mg", 45.0),
        DefMicro("Magnesio", listOf("magnesio", "magnesium"), 410.0, 315.0, "mg"),
        DefMicro("Zinc", listOf("zinc"), 11.0, 8.0, "mg", 40.0),
        DefMicro("Selenio", listOf("selen"), 55.0, 55.0, "µg", 400.0),
        DefMicro("Cobre", listOf("copper", "cobre"), 0.9, 0.9, "mg"),
        DefMicro("Manganeso", listOf("manganese", "manganeso"), 2.3, 1.8, "mg"),
        DefMicro("Fósforo", listOf("phosph", "fosforo"), 700.0, 700.0, "mg"),
        DefMicro("Potasio", listOf("potasio", "potassium"), 3400.0, 2600.0, "mg"),
        DefMicro("Omega 3", listOf("omega_3"), 1.6, 1.1, "g"),
    )

    /** Promedio de los últimos 30 días con datos de cada vitamina y mineral que exporta MacroFactor. */
    fun micronutrientes(): List<Micro> {
        val hoy = Fechas.diaLogico(); val desde = Fechas.sumar(hoy, -365)
        val dias = Repo.nutricion.values.filter { it.fecha in desde..hoy && (it.valores["kcal"] ?: 0.0) > 300 }.sortedBy { it.fecha }.takeLast(30)
        if (dias.isEmpty()) return emptyList()
        val claves = dias.flatMap { it.valores.keys }.toSet()
        return MICROS.mapNotNull { d ->
            val k = claves.firstOrNull { c -> d.claves.any { c.startsWith(it) || c.contains("_$it") || c.contains(it) } && !c.startsWith("objetivo") } ?: return@mapNotNull null
            val l = dias.mapNotNull { it.valores[k] }
            if (l.isEmpty()) return@mapNotNull null
            var x = l.average()
            // Unidades: si MacroFactor las trae en otra unidad, se convierten a la de la referencia.
            when {
                d.unidad == "µg" && k.endsWith("_mg") -> x *= 1000
                d.unidad == "mg" && (k.endsWith("_mcg") || k.endsWith("_ug")) -> x /= 1000
                d.unidad == "µg" && k.endsWith("_iu") -> x *= if (d.nombre == "Vitamina D") 0.025 else 0.3
                d.unidad == "g" && k.endsWith("_mg") -> x /= 1000
            }
            Micro(d.nombre, x, ref(d.h, d.m), d.unidad, d.limite)
        }
    }

    val FUENTE_MICROS = NIH
}
