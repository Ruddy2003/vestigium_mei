package io.github.ruddy2003.vestigium.ciencia

import io.github.ruddy2003.vestigium.data.Privado
import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Personas
import io.github.ruddy2003.vestigium.data.Sustancias
import io.github.ruddy2003.vestigium.data.esAnimo
import io.github.ruddy2003.vestigium.data.esCierre
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import kotlin.math.abs
import kotlin.math.atanh
import kotlin.math.sqrt
import kotlin.math.tanh

data class Variable(val id: String, val nombre: String, val grupo: String)
data class Par(val x: Double, val y: Double, val fecha: String)
/**
 * [r] es la correlación que se muestra (sin tendencia o bruta, según el modo); [bruta] la de los valores tal cual.
 * [nEf] son los días «independientes» una vez descontado que un día se parece al anterior; el intervalo y [q] se calculan con ellos.
 */
data class Correlacion(
    val a: Variable, val b: Variable, val r: Double, val n: Int, val ic: Pair<Double, Double>?,
    val bruta: Double = r, val nEf: Double = n.toDouble(), val p: Double = 1.0,
    var q: Double = 1.0, var qConObvias: Double = 1.0, val obvia: Boolean = false,
)

/** Resultado de revisar todos los pares. [lentas]: variables que casi solo cambian con las semanas (se omiten al quitar tendencias). */
data class Relaciones(val lista: List<Correlacion>, val obvias: Int, val lentas: List<Variable>, val sinTendencia: Boolean) {
    val probadas get() = lista.size - obvias
}

object Estadistica {

    fun pearson(xs: List<Double>, ys: List<Double>): Double? {
        val n = xs.size
        if (n < 3) return null
        val mx = xs.average(); val my = ys.average()
        var sxy = 0.0; var sxx = 0.0; var syy = 0.0
        for (i in 0 until n) { val dx = xs[i] - mx; val dy = ys[i] - my; sxy += dx * dy; sxx += dx * dx; syy += dy * dy }
        if (sxx == 0.0 || syy == 0.0) return null
        return sxy / sqrt(sxx * syy)
    }

    fun rangos(a: List<Double>): List<Double> {
        val idx = a.withIndex().sortedBy { it.value }
        val r = DoubleArray(a.size)
        var i = 0
        while (i < idx.size) {
            var j = i
            while (j + 1 < idx.size && idx[j + 1].value == idx[i].value) j++
            val prom = (i + j) / 2.0 + 1
            for (k in i..j) r[idx[k].index] = prom
            i = j + 1
        }
        return r.toList()
    }

    fun spearman(xs: List<Double>, ys: List<Double>): Double? = pearson(rangos(xs), rangos(ys))

    /** Intervalo de confianza de 95% por la transformación de Fisher. */
    fun ic95(r: Double?, n: Int): Pair<Double, Double>? {
        if (r == null || n < 5) return null
        val z = atanh(r.coerceIn(-0.9999, 0.9999)); val se = 1 / sqrt(n - 3.0)
        return tanh(z - 1.96 * se) to tanh(z + 1.96 * se)
    }

    fun fuerza(r: Double): String = abs(r).let { if (it < .1) "prácticamente nula" else if (it < .3) "débil" else if (it < .5) "moderada" else "fuerte" }

    fun media(v: List<Double>) = if (v.isEmpty()) null else v.average()

    /** Diferencia de medias con intervalo aproximado de 95% (Welch). */
    fun diferencia(a: List<Double>, b: List<Double>): Triple<Double, Double, Double>? {
        if (a.size < 3 || b.size < 3) return null
        val ma = a.average(); val mb = b.average()
        val va = a.sumOf { (it - ma) * (it - ma) } / (a.size - 1)
        val vb = b.sumOf { (it - mb) * (it - mb) } / (b.size - 1)
        val se = sqrt(va / a.size + vb / b.size)
        val d = ma - mb
        return Triple(d, d - 1.96 * se, d + 1.96 * se)
    }

    // ---------- Variables diarias para cruzar ----------
    /**
     * Todas las variables. Con [dinamicas], también una por persona, lugar, app, categoría, artista, nutriente… (ver [Dinamicas]).
     * Las privadas (ver [Privado]) solo salen con [privadas]: las pantallas de la app nunca las piden; el diccionario de la exportación sí.
     */
    fun variables(dinamicas: Boolean = true, privadas: Boolean = false): List<Variable> {
        val v = mutableListOf(
            Variable("valencia", "Valencia del día", "Ánimo"), Variable("activacion", "Activación del día", "Ánimo"),
            Variable("muestreo_valencia", "Valencia promedio en avisos", "Ánimo"), Variable("muestreo_activacion", "Activación promedio en avisos", "Ánimo"),
            Variable("avisos_planeados", "Avisos de ánimo planeados", "Ánimo"), Variable("avisos_mostrados", "Avisos de ánimo mostrados", "Ánimo"),
            Variable("avisos_respondidos", "Avisos de ánimo respondidos", "Ánimo"), Variable("tasa_respuesta", "Proporción de avisos respondidos", "Ánimo"),
            Variable("dolor", "Dolor físico", "Cuerpo"), Variable("sueno_calidad", "Calidad de sueño percibida", "Sueño"),
            Variable("sueno_horas", "Horas de sueño (reloj)", "Sueño"), Variable("sueno_profundo", "Minutos de sueño profundo", "Sueño"),
            Variable("sueno_rem", "Minutos de sueño REM", "Sueño"), Variable("sueno_recordado", "Recordé un sueño (0/1)", "Sueño"),
            Variable("sueno_valencia", "Valencia del sueño", "Sueño"),
            Variable("hrv", "Variabilidad cardiaca (HRV)", "Cuerpo"), Variable("fc_reposo", "Frecuencia cardiaca en reposo", "Cuerpo"),
            Variable("fc_media", "Frecuencia cardiaca media del día", "Cuerpo"), Variable("fc_sueno", "Frecuencia cardiaca dormido", "Cuerpo"),
            Variable("spo2", "Saturación de oxígeno media (%)", "Cuerpo"), Variable("spo2_min", "Oxígeno mínimo de noche (%)", "Cuerpo"),
            Variable("pasos", "Pasos", "Cuerpo"), Variable("pantalla_min", "Minutos de pantalla", "Teléfono"),
            Variable("desbloqueos", "Desbloqueos del teléfono", "Teléfono"), Variable("gasto_total", "Gasto del día", "Dinero"),
            Variable("gasto_imprevisto", "Hubo gasto imprevisto (0/1)", "Dinero"), Variable("palabras", "Palabras en el diario", "Texto"),
            Variable("personas", "Personas distintas mencionadas", "Social"), Variable("hitos_n", "Número de hitos", "Texto"),
            Variable("rt_media", "Tiempo de reacción (ms)", "Mente"), Variable("lugares_distintos", "Lugares distintos visitados", "Movilidad"),
            Variable("distancia_km", "Kilómetros recorridos (aprox.)", "Movilidad"),
            Variable("t_max", "Temperatura máxima (°C)", "Entorno"), Variable("t_min", "Temperatura mínima (°C)", "Entorno"),
            Variable("precip_mm", "Lluvia (mm)", "Entorno"), Variable("nubes_pct", "Nubosidad (%)", "Entorno"),
            Variable("luz_horas", "Horas de luz del día", "Entorno"), Variable("sol_horas", "Horas de sol directo", "Entorno"),
            Variable("presion_hpa", "Presión atmosférica (hPa)", "Entorno"), Variable("aqi_us", "Índice de calidad del aire", "Entorno"),
            Variable("pm25", "Partículas PM2.5", "Entorno"), Variable("luna_ilum", "Luna iluminada (0 a 1)", "Entorno"),
            Variable("fotos", "Fotos tomadas", "Teléfono"), Variable("notificaciones", "Notificaciones recibidas", "Teléfono"),
            Variable("canciones", "Canciones escuchadas", "Teléfono"), Variable("primer_uso_h", "Hora del primer uso del teléfono", "Teléfono"),
            Variable("ultimo_uso_h", "Hora del último uso del teléfono", "Teléfono"),
            Variable("eventos", "Eventos en la agenda", "Agenda"), Variable("min_ocupado", "Minutos ocupados en agenda", "Agenda"),
            Variable("a_pie_min", "Minutos a pie", "Movilidad"), Variable("vehiculo_min", "Minutos en vehículo", "Movilidad"),
            Variable("ejercicio_min", "Minutos de ejercicio (reloj)", "Cuerpo"), Variable("escalada", "Escalé (0/1)", "Cuerpo"),
            Variable("escalada_grado", "Grado máximo encadenado", "Cuerpo"),
            Variable("llamadas", "Llamadas (entrantes y salientes)", "Social"), Variable("min_hablado", "Minutos al teléfono", "Social"),
            Variable("contactos", "Personas distintas por llamada o SMS", "Social"), Variable("wa_mios", "Mensajes de WhatsApp enviados", "Social"),
            Variable("wa_otros", "Mensajes de WhatsApp recibidos", "Social"), Variable("wa_chats", "Chats de WhatsApp activos", "Social"),
            Variable("audifonos_min", "Minutos con audífonos", "Teléfono"), Variable("coche_min", "Minutos en el coche (Bluetooth)", "Movilidad"),
            Variable("musica_min", "Minutos con audio sonando", "Teléfono"), Variable("sin_wifi_min", "Minutos sin wifi (fuera)", "Movilidad"),
            Variable("luz_alta_min", "Minutos con luz de exterior", "Entorno"), Variable("carga_noche_h", "Hora de conectar el teléfono de noche", "Sueño"),
            Variable("peso_kg", "Peso (kg)", "Cuerpo"), Variable("temp_piel", "Variación de temperatura de la piel", "Cuerpo"),
            Variable("resp_rpm", "Frecuencia respiratoria", "Cuerpo"), Variable("cal_activas", "Calorías activas", "Cuerpo"),
            Variable("yt_vistos", "Videos de YouTube vistos", "Teléfono"), Variable("yt_megusta", "«Me gusta» en YouTube", "Teléfono"),
            Variable("yt_comentarios", "Comentarios en YouTube", "Teléfono"), Variable("busquedas", "Búsquedas en Google", "Teléfono"),
            Variable("subrayados", "Subrayados de Kindle", "Lectura"), Variable("ruido_db", "Ruido medio al registrar (dB)", "Entorno"),
            Variable("sueno_inicio_h", "Hora de dormir (reloj)", "Sueño"), Variable("sueno_fin_h", "Hora de despertar (reloj)", "Sueño"),
            Variable("kcal", "Calorías consumidas", "Nutrición"), Variable("proteina_g", "Proteína (g)", "Nutrición"),
            Variable("carbohidratos_g", "Carbohidratos (g)", "Nutrición"), Variable("grasa_g", "Grasa (g)", "Nutrición"), Variable("fibra_g", "Fibra (g)", "Nutrición"),
            Variable("alcohol_g", "Alcohol (g)", "Nutrición"), Variable("cafeina_mg", "Cafeína (mg)", "Nutrición"), Variable("azucares_g", "Azúcares (g)", "Nutrición"),
            Variable("balance_kcal", "Calorías menos gasto estimado", "Nutrición"), Variable("peso_tendencia_kg", "Peso de tendencia (MacroFactor)", "Cuerpo"),
            Variable("primera_comida_h", "Hora del primer alimento", "Nutrición"), Variable("ultima_comida_h", "Hora del último alimento", "Nutrición"),
            Variable("cafeina_ultima_h", "Hora de la última cafeína", "Nutrición"), Variable("gimnasio", "Entrené en el gimnasio (0/1)", "Cuerpo"),
            Variable("volumen_kg", "Volumen del entrenamiento (kg)", "Cuerpo"), Variable("series_gimnasio", "Series efectivas de gimnasio", "Cuerpo"),
            Variable("obras_dia", "Obras a las que les dediqué tiempo", "Cultura"), Variable("lectura", "Leí un libro o artículo (0/1)", "Cultura"),
            Variable("vi_pelicula_serie", "Vi una película o serie (0/1)", "Cultura"), Variable("jugue", "Jugué un videojuego (0/1)", "Cultura"),
            Variable("obra_terminada", "Terminé un libro, película, serie o juego (0/1)", "Cultura"), Variable("calificacion_obra", "Calificación de lo que terminé (1 a 10)", "Cultura"),
            Variable("citas_dia", "Citas y frases guardadas", "Cultura"),
            Variable("ciudades_dia", "Ciudades distintas en el día", "Mundo"), Variable("fuera_de_casa", "Fuera de mi ciudad (0/1)", "Mundo"),
            Variable("extranjero", "En otro país (0/1)", "Mundo"), Variable("ciudad_nueva", "Primera vez en una ciudad (0/1)", "Mundo"),
            Variable("unesco", "Pasé por un sitio del Patrimonio Mundial (0/1)", "Mundo"),
            Variable("carta_escrita", "Escribí una carta (0/1)", "Texto"), Variable("sueno_palabras", "Palabras del sueño escrito", "Sueño"),
            Variable("cartas_otros", "Cartas escritas a otras personas", "Social"),
            Variable("ingreso_total", "Ingresos del día", "Dinero"), Variable("balance_dinero", "Ingresos menos gastos del día", "Dinero"),
            Variable("dia_dificil", "Día difícil (0/1)", "Ánimo"), Variable("prediccion", "Predicción para mañana (1 a 7)", "Ánimo"),
            Variable("error_prediccion", "Cuánto falló la predicción de anoche", "Ánimo"), Variable("medicamentos_n", "Medicamentos tomados", "Salud"),
            Variable("sueno_lucidez", "Lucidez del sueño (0 no, 1 parcial, 2 total)", "Sueño"), Variable("sueno_vividez", "Vividez del sueño (0 vago, 1 moderado, 2 muy vívido)", "Sueño"),
            Variable("n_madrugada", "Momentos de madrugada (antes de las 5)", "Momentos"), Variable("n_manana", "Momentos en la mañana (5 a 12)", "Momentos"),
            Variable("n_tarde", "Momentos en la tarde (12 a 19)", "Momentos"), Variable("n_noche", "Momentos en la noche (19 a 24)", "Momentos"),
            Variable("momentos_intensidad", "Intensidad media de los momentos (1 a 5)", "Momentos"), Variable("momentos_valencia", "Valencia media de los momentos", "Momentos"),
        )
        if (Repo.config.perfil.cercanas.isNotEmpty() || Repo.config.perfil.convivientes.isNotEmpty() || Repo.config.personas.isNotEmpty()) {
            v += Variable("cercanos", "Personas cercanas con las que estuve", "Social"); v += Variable("vi_familia", "Estuve con mi familia (0/1)", "Social")
            v += Variable("vi_pareja", "Estuve con mi pareja (0/1)", "Social"); v += Variable("vi_amistades", "Estuve con una amistad (0/1)", "Social")
            v += Variable("vi_convivientes", "Estuve con quien vive conmigo (0/1)", "Social")
        }
        // «Tu gente»: con la relación que había cada día.
        val hayEx = Repo.config.personas.any { it.relacion == "expareja" || it.historial.any { h -> h.relacion == "expareja" } }
        if (hayEx) v += Variable("vi_expareja", "Estuve con una expareja (0/1)", "Social")
        if (Repo.config.personas.any { Personas.grupo(it.relacion) == "trabajo" }) v += Variable("vi_trabajo", "Estuve con un colega, compañero o mentor (0/1)", "Social")
        // Ver a alguien no es lo mismo que pensar en esa persona o escribirse.
        if (Repo.dias.values.any { it.presencias.isNotEmpty() } || Repo.momentos.any { Catalogo.pensamientoDe(it) == "pensando_en" && Texto.personas(it.texto).isNotEmpty() }) {
            v += Variable("personas_vistas", "Personas con las que estuve", "Social"); v += Variable("personas_pensadas", "Personas en las que pensé sin verlas", "Social")
            v += Variable("personas_mensajes", "Personas con las que hablé por mensaje o llamada", "Social")
            if (Repo.config.personas.any { it.relacion == "pareja" } || Repo.config.perfil.pareja.isNotBlank()) v += Variable("penso_en_pareja", "Pensé en mi pareja sin verla (0/1)", "Social")
            if (hayEx) v += Variable("penso_en_expareja", "Pensé en una expareja sin verla (0/1)", "Social")
            v += Variable("penso_en_familia", "Pensé en mi familia sin verla (0/1)", "Social"); v += Variable("penso_en_amistades", "Pensé en una amistad sin verla (0/1)", "Social")
        }
        if (Repo.config.personas.any { it.cercania != null }) v += Variable("cercania_media", "Cercanía media de quien mencioné (1 a 7)", "Social")
        // El ánimo de todos los registros (avisos y los que haces tú), y lo que sentías y pensabas.
        if (Repo.momentos.any { it.esAnimo }) {
            v += Variable("animo_valencia", "Valencia promedio al momento (todos los registros)", "Ánimo")
            v += Variable("animo_activacion", "Activación promedio al momento (todos los registros)", "Ánimo")
            v += Variable("registros_animo", "Registros de ánimo en el día", "Momentos")
        }
        emocionesUsadas().forEach { id -> v += Variable("n_$id", "Registros con ${Catalogo.nombreSentimiento(id).lowercase()}", "Sentimientos") }
        pensamientosUsados().forEach { id -> v += Variable("n_$id", "Registros pensando: ${(Catalogo.PENSAMIENTOS.firstOrNull { it.id == id }?.nombre ?: id).lowercase()}", "Momentos") }
        // Sustancias: un dato normal (también las que ocultas de las pantallas).
        val sus = Repo.consumos.map { it.sustancia }.toSet()
        if ("alcohol" in sus || Repo.nutricion.values.any { (it.valores["alcohol_g"] ?: 0.0) > 0 }) {
            v += Variable("alcohol", "Tomé alcohol (0/1)", "Sustancias"); v += Variable("alcohol_bebidas", "Bebidas estándar", "Sustancias")
            v += Variable("alcohol_estimado_max", "Alcohol en sangre máximo estimado (g/L)", "Sustancias")
            v += Variable("alcohol_al_dormir", "Alcohol en sangre estimado al dormirme (g/L)", "Sustancias")
        }
        if ("alcohol" in sus) { v += Variable("alcohol_nivel_max", "Qué tan pedo llegué (0 a 10)", "Sustancias"); v += Variable("alcohol_horas", "Horas con efecto del alcohol", "Sustancias") }
        if ("cannabis" in sus) {
            v += Variable("cannabis", "Cannabis (0/1)", "Sustancias"); v += Variable("cannabis_nivel_max", "Nivel máximo con cannabis (0 a 10)", "Sustancias")
            v += Variable("cannabis_horas", "Horas con efecto del cannabis", "Sustancias")
        }
        if ("tabaco" in sus) v += Variable("tabaco_n", "Cigarros o sesiones de vape", "Sustancias")
        if ("otra" in sus) v += Variable("otra_sustancia", "Otra sustancia (0/1)", "Sustancias")
        if (Repo.consumos.any { it.cruda != null }) v += Variable("cruda", "Cruda o bajón (0 a 5, del día anterior)", "Sustancias")
        if ("cafeina" in sus || Repo.nutricion.values.any { (it.valores["cafeina_mg"] ?: 0.0) > 0 }) {
            v += Variable("cafeina_total_mg", "Cafeína del día (MacroFactor o anotada, mg)", "Sustancias")
            v += Variable("cafeina_al_dormir_mg", "Cafeína en el cuerpo al dormirme (mg, estimada)", "Sustancias")
        }
        // De qué fueron tus sueños.
        categoriasSuenoUsadas().forEach { id -> v += Variable("sueno_$id", "Sueño: ${Catalogo.opcionSueno(id)?.nombre?.lowercase() ?: id} (0/1)", "Sueños") }
        v += Variable("impulso", "Intensidad del impulso", "Privado"); v += Variable("recaida", "Recaída (0/1)", "Privado")
        v += Variable("n_recaida", "Recaídas registradas con hora", "Privado")
        if (Repo.momentos.any { it.tipo == "recaida" && it.duracion_min != null }) v += Variable("recaida_min", "Minutos de recaída", "Privado")
        // Enfoque, siestas y lo que se deriva del teléfono y el reloj.
        if (Repo.tramos.isNotEmpty()) {
            v += Variable("enfoque_min", "Minutos de enfoque (cronómetro)", "Enfoque")
            Repo.tramos.map { it.tipo }.filter { it != "siesta" }.distinct().forEach { t -> v += Variable("enfoque_${t}_min", "Enfoque: ${Catalogo.nombreTramo(t).lowercase()} (min)", "Enfoque") }
            if (Repo.tramos.any { it.tipo == "siesta" }) { v += Variable("siesta", "Tomé siesta (0/1)", "Sueño"); v += Variable("siesta_min", "Minutos de siesta", "Sueño") }
        }
        if (Repo.pantalla.values.any { it.cama_min != null }) {
            v += Variable("cama_min", "Pantalla en la última hora antes de dormir (min)", "Sueño")
            v += Variable("dormir_tras_tel_min", "Minutos entre soltar el teléfono y dormirte", "Sueño")
        }
        if (Repo.pantalla.values.any { it.tel_tras_despertar_min != null }) v += Variable("tel_tras_despertar_min", "Minutos entre despertar y tomar el teléfono", "Sueño")
        v += Variable("aire_libre_min", "Minutos al aire libre (estimado)", "Entorno")
        if (Repo.lugares.any { it.tipo == "casa" }) v += Variable("en_casa_min", "Minutos en casa", "Movilidad")
        // Horas en cada tipo de lugar (casa, trabajo, bar, naturaleza…).
        Repo.lugares.map { it.tipo }.distinct().sorted().forEach { t -> v += Variable("horas_tipo_$t", "Horas en lugares de tipo ${Catalogo.nombreTipoLugar(t).lowercase()}", "Lugares") }
        // El «Cómo estás ahora» al cerrar el día: la misma hora todos los días.
        if (Repo.momentos.any { it.esCierre }) {
            v += Variable("animo_cierre_valencia", "Valencia al cerrar el día", "Ánimo"); v += Variable("animo_cierre_activacion", "Activación al cerrar el día", "Ánimo")
        }
        // El barómetro del teléfono.
        if (Repo.conexiones.values.any { it.presion_n > 0 }) {
            v += Variable("presion_local_hpa", "Presión del barómetro del teléfono (hPa)", "Entorno"); v += Variable("cambio_presion_3h", "Mayor cambio de presión en 3 horas (hPa)", "Entorno")
        }
        if (Repo.momentos.any { it.tipo == "libido" }) {
            v += Variable("libido_max", "Libido máxima del día (0 a 5)", "Privado"); v += Variable("n_libido", "Registros de libido", "Privado")
        }
        // Suplementos: dosis, si se tomó y el nivel del modelo (o porciones).
        val sup = runCatching { io.github.ruddy2003.vestigium.data.Suplementos.panel() }.getOrDefault(emptyMap())
        val clavesSup = sup.values.flatMap { it.keys }.toSet()
        Repo.config.suplementos.forEach { s ->
            val u = s.unidad
            listOf("supl_${s.id}_dosis" to "${s.nombre}: dosis del día ($u)", "supl_${s.id}_tomado" to "${s.nombre}: lo tomaste (0/1)",
                "supl_${s.id}_nivel" to (if (s.modelo == "carga") "${s.nombre}: reserva estimada (0 a 100 %)" else "${s.nombre}: cantidad estimada en el cuerpo al final del día ($u)"),
                "supl_${s.id}_porciones" to "${s.nombre}: porciones del día").forEach { (k, n) -> if (k in clavesSup) v += Variable(k, n, "Suplementos") }
        }
        if ("creatina_saturacion_pct" in clavesSup) v += Variable("creatina_saturacion_pct", "Saturación de creatina estimada (%)", "Suplementos")
        if ("probioticos_porciones" in clavesSup) { v += Variable("probioticos_porciones", "Porciones de probióticos (anotadas y estimadas)", "Suplementos"); v += Variable("probioticos_estimado", "Porciones de probióticos leídas en tu bitácora de comidas", "Suplementos") }
        // La laptop (Cold Turkey).
        if (Repo.laptop.isNotEmpty()) {
            v += Variable("laptop_min", "Minutos en la laptop", "Laptop")
            if (Repo.laptop.values.any { it.sitios.isNotEmpty() }) v += Variable("laptop_web_min", "Minutos en sitios web (laptop)", "Laptop")
            io.github.ruddy2003.vestigium.data.ColdTurkey.CATEGORIAS.forEach { o -> v += Variable("laptop_${o.id}_min", "Minutos en la laptop: ${o.nombre.lowercase()}", "Laptop") }
        }
        if (Repo.momentos.any { it.con_quien.isNotEmpty() }) {
            v += Variable("acompanado_pct", "Avisos de ánimo con alguien (0 a 1)", "Social")
            Catalogo.CON_QUIEN.filter { it.id != "solo" }.forEach { o -> v += Variable("con_${o.id}", "Avisos con ${o.nombre.lowercase()} (0 a 1)", "Social") }
            v += Variable("valencia_acompanado", "Valencia en avisos con alguien", "Ánimo"); v += Variable("valencia_solo", "Valencia en avisos estando solo", "Ánimo")
        }
        Catalogo.CONTEXTOS.forEach { v += Variable("ctx_${it.id}", "Contexto: ${it.nombre} (0/1)", "Contexto") }
        Catalogo.CIENTIFICAS.forEach { p -> p.opciones.filter { it.id != "no_aplica" }.forEach { o -> v += Variable("${p.clave}_${o.id}", "${o.nombre} (0/1)", "Lectura del día") } }
        // Lo de antes (tipos de momento) que no es un sentimiento ni un pensamiento: el impulso.
        Catalogo.MOMENTOS.filter { it.id == "impulso" }.forEach { v += Variable("n_${it.id}", "Momentos de ${it.nombre.lowercase()}", "Privado") }
        Repo.config.habitos.forEach { v += Variable("hab_${it.id}", "Hábito: ${it.nombre} (0/1)", "Hábitos") }
        if (io.github.ruddy2003.vestigium.data.Especiales.hay()) v += Variable("dia_especial", "Día especial: cumpleaños, aniversario o fecha propia (0/1)", "Calendario")
        // Campañas (P-11): una por reto que hayas tenido, y cuántas tenías ese día.
        val camp = Repo.config.campanas
        if (camp.isNotEmpty()) {
            camp.map { it.reto }.distinct().forEach { r -> v += Variable(Campanas.variable(r), "En campaña: ${Campanas.reto(r)?.nombre ?: r} (0/1)", "Campañas") }
            v += Variable("campanas_n", "Campañas que tenías ese día", "Campañas")
        }
        if (dinamicas) v += Dinamicas.calcular(false).variables
        if (privadas) return v
        val ligado = Privado.habito()?.id
        return v.filter { !Privado.esVariable(it.id, ligado) }
    }

    private fun emocionesUsadas(): List<String> = Repo.momentos.flatMap { Catalogo.emocionesDe(it) }.filter { it != Catalogo.OTRO }.distinct().sorted()
    private fun pensamientosUsados(): List<String> = Repo.momentos.mapNotNull { Catalogo.pensamientoDe(it) }.distinct().sorted()
    private fun categoriasSuenoUsadas(): List<String> = Repo.dias.values.flatMap { it.sueno_categorias }.distinct().sorted()

    /**
     * Una fila por día con todas las variables numéricas. Lo que no existe queda en null. Con [dinamicas] incluye las de
     * cada persona, lugar, app…; con [extendido], todas las que aparecen al menos dos días (para la exportación).
     */
    fun panel(dinamicas: Boolean = true, extendido: Boolean = false): Map<String, Map<String, Double?>> {
        val eq = Repo.equivalencias()
        val filas = sortedMapOf<String, MutableMap<String, Double?>>()
        fun fila(f: String) = filas.getOrPut(f) { mutableMapOf() }
        val b = { x: Boolean -> if (x) 1.0 else 0.0 }

        for ((f, d) in Repo.dias) {
            val r = fila(f)
            r["valencia"] = d.valencia?.toDouble(); r["activacion"] = d.activacion?.toDouble(); r["dolor"] = d.dolor?.toDouble()
            r["sueno_calidad"] = d.sueno_calidad?.toDouble(); r["sueno_recordado"] = b(d.sueno_recordado)
            r["sueno_valencia"] = if (d.sueno_recordado) d.sueno_valencia?.toDouble() else null
            r["palabras"] = Texto.palabras(d.nota).toDouble()
            r["personas"] = Texto.personas(d.nota).map { Texto.canon(it, eq) }.toSet().size.toDouble()
            r["hitos_n"] = d.hitos.size.toDouble()
            r["impulso"] = d.impulso?.toDouble(); r["recaida"] = d.recaida?.let(b)
            Catalogo.CONTEXTOS.forEach { r["ctx_${it.id}"] = b(d.ctx[it.id] == true) }
            Catalogo.CIENTIFICAS.forEach { p ->
                val valor = when (p.clave) { "flow" -> d.flow; "sdt_necesidad" -> d.sdt_necesidad; else -> d.apreciacion }
                p.opciones.filter { it.id != "no_aplica" }.forEach { o -> r["${p.clave}_${o.id}"] = valor?.let { b(it == o.id) } }
            }
            Repo.config.habitos.forEach { r["hab_${it.id}"] = b(d.habitos[it.id] == true) }
            r["gasto_total"] = 0.0; r["gasto_imprevisto"] = 0.0
            r["n_impulso"] = 0.0
        }
        val emoUsadas = emocionesUsadas(); val penUsados = pensamientosUsados()
        // Desde el primer registro de ánimo, los días sin registros cuentan como 0.
        Repo.momentos.filter { Catalogo.esRegistro(it) }.minOfOrNull { it.fecha }?.let { p0 ->
            Repo.dias.keys.filter { it >= p0 }.forEach { f -> val r = fila(f); emoUsadas.forEach { r["n_$it"] = 0.0 }; penUsados.forEach { r["n_$it"] = 0.0 }; r["registros_animo"] = 0.0 }
        }
        Repo.momentos.groupBy { it.fecha }.forEach { (f, lista) ->
            val r = fila(f)
            val mu = lista.filter { it.tipo == "muestreo" }
            r["muestreo_valencia"] = media(mu.mapNotNull { it.valencia?.toDouble() })
            r["muestreo_activacion"] = media(mu.mapNotNull { it.activacion?.toDouble() })
            val an = lista.filter { it.esAnimo }
            if (an.isNotEmpty()) { r["animo_valencia"] = media(an.mapNotNull { it.valencia?.toDouble() }); r["animo_activacion"] = media(an.mapNotNull { it.activacion?.toDouble() }) }
            val regs = lista.filter { Catalogo.esRegistro(it) }
            r["registros_animo"] = regs.size.toDouble()
            emoUsadas.forEach { e -> r["n_$e"] = regs.count { e in Catalogo.emocionesDe(it) }.toDouble() }
            penUsados.forEach { t -> r["n_$t"] = regs.count { Catalogo.pensamientoDe(it) == t }.toDouble() }
            r["n_impulso"] = lista.count { it.tipo == "impulso" }.toDouble()
            // Con quién: la proporción de avisos de ánimo con cada compañía, y el ánimo solo o acompañado.
            val con = mu.filter { it.con_quien.isNotEmpty() }
            if (con.isNotEmpty()) {
                val acomp = con.filter { "solo" !in it.con_quien }
                r["acompanado_pct"] = acomp.size.toDouble() / con.size
                Catalogo.CON_QUIEN.filter { it.id != "solo" }.forEach { o -> r["con_${o.id}"] = con.count { o.id in it.con_quien }.toDouble() / con.size }
                r["valencia_acompanado"] = media(acomp.mapNotNull { it.valencia?.toDouble() })
                r["valencia_solo"] = media(con.filter { "solo" in it.con_quien }.mapNotNull { it.valencia?.toDouble() })
            }
        }
        // Avisos de ánimo (v13): planeados, mostrados, respondidos y tasa de respuesta. Los días sin registro quedan sin dato.
        val respAv = io.github.ruddy2003.vestigium.data.AvisosAnimo.respondidos(Repo.momentos)
        Repo.avisos.values.forEach { a -> fila(a.fecha).putAll(io.github.ruddy2003.vestigium.data.AvisosAnimo.cifras(a, respAv)) }
        // Campañas (P-11): 1 los días que tenías aceptado ese reto, 0 los demás desde tu primera campaña; antes, sin dato.
        val camp = Repo.config.campanas
        if (camp.isNotEmpty()) {
            val hoyL = Fechas.diaLogico()
            val act = Campanas.diasActivos(camp, hoyL)
            val vars = camp.map { Campanas.variable(it.reto) }.distinct()
            val p0 = camp.minOf { Campanas.tramo(it).first }
            (Repo.dias.keys.filter { it in p0..hoyL } + act.keys).toSet().forEach { f ->
                val r = fila(f); val hoyActivas = act[f].orEmpty().mapTo(HashSet()) { Campanas.variable(it) }
                vars.forEach { k -> r[k] = if (k in hoyActivas) 1.0 else 0.0 }
                r["campanas_n"] = hoyActivas.size.toDouble()
            }
        }
        // Lo privado se calcula siempre: no sale en las pantallas (ver [variables]), pero sí en la exportación para R.
        run {
            val rec = Repo.momentos.filter { it.tipo == "recaida" }.groupingBy { it.fecha }.eachCount()
            rec.keys.minOrNull()?.let { p0 -> Repo.dias.keys.filter { it >= p0 }.forEach { f -> fila(f)["n_recaida"] = (rec[f] ?: 0).toDouble() } }
            rec.forEach { (f, n) -> fila(f)["n_recaida"] = n.toDouble() }
            Repo.momentos.filter { it.tipo == "recaida" && it.duracion_min != null }.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["recaida_min"] = l.sumOf { it.duracion_min ?: 0 }.toDouble() }
        }
        // Cronómetro: desde el primer tramo, los días sin nada cuentan como 0.
        if (Repo.tramos.isNotEmpty()) {
            val porDia = Repo.tramos.groupBy { it.fecha }
            val tipos = Repo.tramos.map { it.tipo }.filter { it != "siesta" }.distinct()
            val p0 = porDia.keys.min()
            (Repo.dias.keys.filter { it >= p0 } + porDia.keys).toSet().forEach { f ->
                val l = porDia[f].orEmpty(); val r = fila(f)
                r["enfoque_min"] = l.filter { it.tipo != "siesta" }.sumOf { it.min }.toDouble()
                tipos.forEach { t -> r["enfoque_${t}_min"] = l.filter { it.tipo == t }.sumOf { it.min }.toDouble() }
                if (Repo.tramos.any { it.tipo == "siesta" }) { val sm = l.filter { it.tipo == "siesta" }.sumOf { it.min }; r["siesta_min"] = sm.toDouble(); r["siesta"] = b(sm > 0) }
            }
        }
        Repo.gastos.groupBy { it.dia }.forEach { (f, lista) ->
            val r = fila(f); r["gasto_total"] = lista.sumOf { it.monto }; r["gasto_imprevisto"] = b(lista.any { it.imprevisto })
        }
        Repo.salud.forEach { (f, s) ->
            val r = fila(f)
            r["sueno_horas"] = s.sueno_min?.let { it / 60.0 }; r["sueno_profundo"] = s.sueno_profundo_min?.toDouble()
            r["sueno_rem"] = s.sueno_rem_min?.toDouble(); r["hrv"] = s.hrv_rmssd; r["fc_reposo"] = s.fc_reposo; r["pasos"] = s.pasos?.toDouble()
            r["fc_media"] = s.fc_media; r["fc_sueno"] = s.fc_sueno; r["spo2"] = s.spo2; r["spo2_min"] = s.spo2_min
        }
        Repo.pantalla.forEach { (f, p) ->
            val r = fila(f); r["pantalla_min"] = p.total_min.toDouble(); r["desbloqueos"] = p.desbloqueos.toDouble()
            r["cama_min"] = p.cama_min?.toDouble(); r["dormir_tras_tel_min"] = p.dormir_tras_tel_min?.toDouble(); r["tel_tras_despertar_min"] = p.tel_tras_despertar_min?.toDouble()
        }
        Repo.reacciones.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["rt_media"] = media(l.mapNotNull { it.media_ms }) }
        Repo.visitas.groupBy { it.entrada.take(10) }.forEach { (f, l) -> fila(f)["lugares_distintos"] = l.map { it.lugar }.toSet().size.toDouble() }
        runCatching { Movilidad.kmPorDia() }.getOrDefault(emptyMap()).forEach { (f, km) -> fila(f)["distancia_km"] = km }
        Repo.entorno.forEach { (f, e) ->
            val r = fila(f)
            r["t_max"] = e.t_max; r["t_min"] = e.t_min; r["precip_mm"] = e.precip_mm; r["nubes_pct"] = e.nubes_pct
            r["luz_horas"] = e.luz_min?.let { it / 60.0 }; r["sol_horas"] = e.sol_min?.let { it / 60.0 }; r["presion_hpa"] = e.presion_hpa
            r["aqi_us"] = e.aqi_us?.toDouble(); r["pm25"] = e.pm25
        }
        filas.keys.toList().forEach { f -> fila(f)["luna_ilum"] = io.github.ruddy2003.vestigium.data.Luna.iluminacion(io.github.ruddy2003.vestigium.data.Luna.fase(f)) }
        Repo.fotos.forEach { (f, x) -> fila(f)["fotos"] = x.fotos.toDouble() }
        Repo.notifs.forEach { (f, x) -> fila(f)["notificaciones"] = x.total.toDouble() }
        Repo.agenda.forEach { (f, x) -> val r = fila(f); r["eventos"] = x.eventos.toDouble(); r["min_ocupado"] = x.min_ocupado.toDouble() }
        Repo.actividad.forEach { (f, x) -> val r = fila(f); r["a_pie_min"] = (x.minutos["a_pie"] ?: 0).toDouble(); r["vehiculo_min"] = (x.minutos["vehiculo"] ?: 0).toDouble() }
        // Al aire libre: la luz de exterior que mide el teléfono o, si es más, el tiempo caminando, corriendo o en bici.
        (Repo.conexiones.keys + Repo.actividad.keys).forEach { f ->
            val luz = Repo.conexiones[f]?.luz_alta_min
            val mov = Repo.actividad[f]?.minutos?.let { (it["a_pie"] ?: 0) + (it["corriendo"] ?: 0) + (it["bici"] ?: 0) }
            if (luz != null || mov != null) fila(f)["aire_libre_min"] = maxOf(luz ?: 0, mov ?: 0).toDouble()
        }
        // En casa: el tiempo dentro de los lugares que marcaste como casa, recortado a cada día.
        val casas = Repo.lugares.filter { it.tipo == "casa" }.map { it.id }.toSet()
        if (casas.isNotEmpty()) {
            val enCasa = HashMap<String, Double>()
            val ahoraMs = System.currentTimeMillis()
            Repo.visitas.filter { it.lugar in casas }.forEach { v ->
                var a = Fechas.millis(v.entrada); val z = v.salida?.let { Fechas.millis(it) } ?: ahoraMs
                if (a <= 0 || z <= a || z - a > 40L * 86_400_000L) return@forEach
                while (a < z) {
                    val f = Fechas.diaLogicoDeMillis(a)
                    val finDia = Fechas.finDia(f) + 5 * 3_600_000L
                    val y = minOf(z, finDia)
                    enCasa[f] = (enCasa[f] ?: 0.0) + (y - a) / 60_000.0
                    a = y
                }
            }
            enCasa.forEach { (f, m) -> fila(f)["en_casa_min"] = Math.round(minOf(m, 1440.0)).toDouble() }
        }
        // Horas por tipo de lugar: desde el primer día con visitas, los días sin nada cuentan como 0.
        if (Repo.lugares.isNotEmpty()) {
            val porTipo = io.github.ruddy2003.vestigium.data.Lugares.minutosPorTipo()
            val tipos = Repo.lugares.map { it.tipo }.toSet()
            porTipo.keys.minOrNull()?.let { p0 ->
                (Repo.dias.keys.filter { it >= p0 } + porTipo.keys).toSet().forEach { f ->
                    val r = fila(f); tipos.forEach { t -> r["horas_tipo_$t"] = Math.round((porTipo[f]?.get(t) ?: 0.0) / 60.0 * 100) / 100.0 }
                }
            }
        }
        Repo.momentos.filter { it.esCierre }.groupBy { it.fecha }.forEach { (f, l) ->
            val m = l.maxBy { it.ts }; val r = fila(f); r["animo_cierre_valencia"] = m.valencia?.toDouble(); r["animo_cierre_activacion"] = m.activacion?.toDouble()
        }
        val ajusteBar = io.github.ruddy2003.vestigium.data.Barometro.ajuste()
        Repo.conexiones.forEach { (f, c) -> if (c.presion_n > 0) { val r = fila(f); r["presion_local_hpa"] = io.github.ruddy2003.vestigium.data.Barometro.local(c, ajusteBar); r["cambio_presion_3h"] = c.cambio_presion_3h } }
        Repo.momentos.filter { it.tipo == "libido" }.groupBy { it.fecha }.forEach { (f, l) ->
            val r = fila(f); r["libido_max"] = l.mapNotNull { it.intensidad }.maxOrNull()?.toDouble(); r["n_libido"] = l.size.toDouble()
        }
        runCatching { io.github.ruddy2003.vestigium.data.Suplementos.panel().forEach { (f, m) -> val r = fila(f); m.forEach { (k, x) -> r[k] = x } } }
        Repo.laptop.forEach { (f, d) -> val r = fila(f); io.github.ruddy2003.vestigium.data.ColdTurkey.resumen(d).forEach { (k, x) -> r[k] = x } }
        Repo.sesiones.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["ejercicio_min"] = l.sumOf { it.min }.toDouble() }
        Repo.pantalla.forEach { (f, p) ->
            val r = fila(f)
            fun h(iso: String?): Double? = iso?.let { val t = java.time.OffsetDateTime.parse(it).atZoneSameInstant(java.time.ZoneId.systemDefault()); t.hour + t.minute / 60.0 }
            r["primer_uso_h"] = runCatching { h(p.primer_uso) }.getOrNull()
            r["ultimo_uso_h"] = runCatching { h(p.ultimo_uso)?.let { if (it < 5) it + 24 else it } }.getOrNull()
        }
        val diasEsc = Repo.ascensos.groupBy { it.fecha }
        for (f in Repo.dias.keys) { fila(f)["escalada"] = if (diasEsc.containsKey(f)) 1.0 else 0.0 }
        diasEsc.forEach { (f, l) ->
            val r = fila(f); r["escalada"] = 1.0
            r["escalada_grado"] = l.flatMap { it.bloques }.filter { io.github.ruddy2003.vestigium.data.Grados.encadenado(it.estilo) }.mapNotNull { io.github.ruddy2003.vestigium.data.Grados.valor(it.grado) }.maxOrNull()
        }
        fun hd(iso: String?, noche: Boolean) = iso?.let { io.github.ruddy2003.vestigium.data.Exportar.horaDecimal(it, noche) }
        Repo.comunicacion.forEach { (f, c) -> val r = fila(f); r["llamadas"] = (c.entrantes + c.salientes).toDouble(); r["min_hablado"] = c.min_hablado; r["contactos"] = c.contactos.toDouble() }
        Repo.chats.groupBy { it.fecha }.forEach { (f, l) -> val r = fila(f); r["wa_mios"] = l.sumOf { it.mios }.toDouble(); r["wa_otros"] = l.sumOf { it.otros }.toDouble(); r["wa_chats"] = l.size.toDouble() }
        Repo.conexiones.forEach { (f, c) ->
            val r = fila(f)
            r["audifonos_min"] = (c.audio_min["audifonos"] ?: 0).toDouble(); r["coche_min"] = (c.audio_min["coche"] ?: 0).toDouble()
            r["musica_min"] = c.musica_min.toDouble(); r["sin_wifi_min"] = c.sin_wifi_min.toDouble(); r["luz_alta_min"] = c.luz_alta_min.toDouble()
            r["carga_noche_h"] = hd(c.carga_noche_inicio, true)
        }
        Repo.salud.forEach { (f, x) ->
            val r = fila(f)
            r["peso_kg"] = x.peso_kg; r["temp_piel"] = x.temp_piel_delta; r["resp_rpm"] = x.resp_rpm; r["cal_activas"] = x.cal_activas
            r["sueno_inicio_h"] = hd(x.sueno_inicio, true); r["sueno_fin_h"] = hd(x.sueno_fin, false)
        }
        Repo.nutricion.forEach { (f, n) ->
            val r = fila(f)
            listOf("kcal", "proteina_g", "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "cafeina_mg", "azucares_g", "peso_tendencia_kg").forEach { k -> r[k] = n.valores[k] }
            val k = n.valores["kcal"]; val g = n.valores["gasto_kcal"]
            r["balance_kcal"] = if (k != null && g != null) k - g else null
            r["primera_comida_h"] = n.primera?.let { io.github.ruddy2003.vestigium.data.Exportar.horaTexto(it) }
            r["ultima_comida_h"] = n.ultima?.let { io.github.ruddy2003.vestigium.data.Exportar.horaTexto(it) }
            r["cafeina_ultima_h"] = n.cafeina_ultima?.let { io.github.ruddy2003.vestigium.data.Exportar.horaTexto(it) }
        }
        Repo.entrenos.forEach { (f, e) -> val r = fila(f); r["volumen_kg"] = e.volumen_kg; r["series_gimnasio"] = e.series_efectivas.toDouble() }
        if (Repo.entrenos.isNotEmpty()) {
            val desdeG = Repo.entrenos.keys.min()
            filas.keys.filter { it >= desdeG }.forEach { f -> fila(f)["gimnasio"] = if ((Repo.entrenos[f]?.series ?: 0) > 0) 1.0 else 0.0 }
        }
        Repo.huellas.forEach { (f, h) -> val r = fila(f); r["yt_vistos"] = h.yt_vistos.toDouble(); r["busquedas"] = (h.busquedas + h.yt_busquedas).toDouble() }
        // «Me gusta» y comentarios: cuentan desde el primero que hay (antes no se sabe), en días con historial de YouTube.
        if (Repo.youtube.isNotEmpty()) {
            val mg = Repo.youtube.filter { it.tipo == "megusta" && it.fecha.isNotBlank() }.groupingBy { it.fecha }.eachCount()
            val co = Repo.youtube.filter { (it.tipo == "comentario" || it.tipo == "chat") && it.fecha.isNotBlank() }.groupingBy { it.fecha }.eachCount()
            val d0 = mg.keys.minOrNull(); val c0 = co.keys.minOrNull()
            Repo.huellas.keys.forEach { f ->
                if (d0 != null && f >= d0) fila(f)["yt_megusta"] = (mg[f] ?: 0).toDouble()
                if (c0 != null && f >= c0) fila(f)["yt_comentarios"] = (co[f] ?: 0).toDouble()
            }
        }
        Repo.subrayados.filter { it.fecha != null }.groupBy { it.fecha!! }.forEach { (f, l) -> fila(f)["subrayados"] = l.size.toDouble() }
        Repo.momentos.filter { it.ruido_db != null }.groupBy { it.fecha }.forEach { (f, l) -> fila(f)["ruido_db"] = l.mapNotNull { it.ruido_db }.average() }
        runCatching {
            val desde = filas.keys.firstOrNull() ?: return@runCatching
            Repo.escuchas(desde, "9999-12-31").groupBy { it.fecha }.forEach { (f, l) -> fila(f)["canciones"] = l.size.toDouble() }
        }
        // Lo que leíste, viste y jugaste: desde el primer día con una obra marcada, los días sin nada cuentan como 0.
        val obrasPorDia = HashMap<String, MutableList<io.github.ruddy2003.vestigium.data.Obra>>()
        Repo.obras.forEach { o -> o.dias.forEach { f -> obrasPorDia.getOrPut(f) { ArrayList() }.add(o) } }
        val terminadas = Repo.obras.filter { it.estado == "terminado" && it.fin != null }.groupBy { it.fin!! }
        val primeraObra = (obrasPorDia.keys + terminadas.keys).minOrNull()
        if (primeraObra != null) {
            (Repo.dias.keys.filter { it >= primeraObra } + obrasPorDia.keys + terminadas.keys).toSet().forEach { f ->
                val l = obrasPorDia[f].orEmpty(); val t = terminadas[f].orEmpty()
                val r = fila(f)
                r["obras_dia"] = l.size.toDouble()
                r["lectura"] = b(l.any { it.tipo == "libro" || it.tipo == "articulo" } || t.any { it.tipo == "libro" || it.tipo == "articulo" })
                r["vi_pelicula_serie"] = b(l.any { it.tipo == "pelicula" || it.tipo == "serie" } || t.any { it.tipo == "pelicula" || it.tipo == "serie" })
                r["jugue"] = b(l.any { it.tipo == "juego" } || t.any { it.tipo == "juego" })
                r["obra_terminada"] = b(t.isNotEmpty())
                r["calificacion_obra"] = media(t.mapNotNull { it.calificacion?.toDouble() })
            }
        }
        val citas = HashMap<String, Int>()
        Repo.obras.forEach { o -> o.citas.forEach { c -> if (c.fecha.isNotBlank()) citas.merge(c.fecha, 1, Int::plus) } }
        Repo.config.frases_guardadas.forEach { citas.merge(it.fecha, 1, Int::plus) }
        citas.forEach { (f, n) -> fila(f)["citas_dia"] = n.toDouble() }
        // Cartas y sueños escritos.
        val cartas = Repo.cartas.groupingBy { it.escrita }.eachCount()
        val primeraCarta = cartas.keys.minOrNull()
        if (primeraCarta != null) Repo.dias.keys.filter { it >= primeraCarta }.forEach { f -> fila(f)["carta_escrita"] = b((cartas[f] ?: 0) > 0) }
        cartas.forEach { (f, _) -> fila(f)["carta_escrita"] = 1.0 }
        for ((f, d) in Repo.dias) if (d.sueno_recordado || d.sueno_texto.isNotBlank()) fila(f)["sueno_palabras"] = Texto.palabras(d.sueno_texto).toDouble()
        // Las personas del diario y los momentos: con quién estuviste, en quién solo pensaste (y su relación ese día).
        val perfil = Repo.config.perfil
        run {
            val momentosPorDia = Repo.momentos.groupBy { it.fecha }
            for (f in (Repo.dias.keys + momentosPorDia.keys).toSet()) {
                // Quién estuvo de verdad, en quién solo pensaste y con quién hablaste por mensaje (el diario manda).
                val estados = io.github.ruddy2003.vestigium.data.Presencias.delDia(f, eq, Repo.dias[f], momentosPorDia[f].orEmpty())
                val nombres = estados.keys.toList()
                fun rel(lista: Collection<String>) = lista.mapNotNull { io.github.ruddy2003.vestigium.data.Perfiles.relacion(it, perfil, f)?.let { r -> it to r } }
                // La relación que había ese día (pareja hasta marzo, expareja después).
                val vistos = rel(estados.filterValues { it == io.github.ruddy2003.vestigium.data.Presencias.ESTUVE }.keys)
                val pensados = rel(estados.filterValues { it == io.github.ruddy2003.vestigium.data.Presencias.PENSE }.keys)
                val r = fila(f)
                r["cercanos"] = vistos.count { it.second in setOf("familia", "pareja", "amistad", "casa") }.toDouble()
                r["vi_familia"] = b(vistos.any { it.second == "familia" }); r["vi_pareja"] = b(vistos.any { it.second == "pareja" })
                r["vi_amistades"] = b(vistos.any { it.second == "amistad" }); r["vi_convivientes"] = b(vistos.any { it.first in perfil.convivientes })
                r["vi_expareja"] = b(vistos.any { it.second == "expareja" }); r["vi_trabajo"] = b(vistos.any { it.second == "trabajo" })
                r["cercania_media"] = media(nombres.mapNotNull { Personas.cercaniaEn(it, f)?.toDouble() })
                r["personas_vistas"] = estados.count { it.value == io.github.ruddy2003.vestigium.data.Presencias.ESTUVE }.toDouble()
                r["personas_pensadas"] = estados.count { it.value == io.github.ruddy2003.vestigium.data.Presencias.PENSE }.toDouble()
                r["personas_mensajes"] = estados.count { it.value == io.github.ruddy2003.vestigium.data.Presencias.MENSAJES }.toDouble()
                r["penso_en_pareja"] = b(pensados.any { it.second == "pareja" }); r["penso_en_expareja"] = b(pensados.any { it.second == "expareja" })
                r["penso_en_familia"] = b(pensados.any { it.second == "familia" }); r["penso_en_amistades"] = b(pensados.any { it.second == "amistad" })
            }
        }
        // Dinero que entra, y el balance del día.
        val ingresos = Repo.ingresos.groupBy { it.dia }.mapValues { (_, l) -> l.sumOf { it.monto } }
        val primerIngreso = ingresos.keys.minOrNull()
        if (primerIngreso != null) {
            (Repo.dias.keys.filter { it >= primerIngreso } + ingresos.keys).toSet().forEach { f ->
                val r = fila(f); val i = ingresos[f] ?: 0.0
                r["ingreso_total"] = i; r["balance_dinero"] = i - (r["gasto_total"] ?: Repo.gastos.filter { it.dia == f }.sumOf { it.monto })
            }
        }
        // Lo demás del diario: día difícil, predicción y su error, medicamentos, detalle del sueño.
        val conMedicamentos = Repo.dias.values.filter { it.medicamentos.isNotEmpty() }.minOfOrNull { it.fecha }
        for ((f, d) in Repo.dias) {
            val r = fila(f)
            r["dia_dificil"] = b(d.dia_dificil)
            r["prediccion"] = d.prediccion_manana?.toDouble()
            Repo.dias[Fechas.sumar(f, -1)]?.prediccion_manana?.let { p -> d.valencia?.let { r["error_prediccion"] = abs(p - it).toDouble() } }
            if (conMedicamentos != null && f >= conMedicamentos) r["medicamentos_n"] = d.medicamentos.size.toDouble()
            if (d.sueno_recordado) {
                r["sueno_lucidez"] = when (d.sueno_lucidez) { "no" -> 0.0; "parcial" -> 1.0; "total" -> 2.0; else -> null }
                r["sueno_vividez"] = when (d.sueno_vividez) { "vago" -> 0.0; "moderado" -> 1.0; "muy_vivido" -> 2.0; else -> null }
            }
            listOf("n_madrugada", "n_manana", "n_tarde", "n_noche").forEach { r[it] = 0.0 }
        }
        // Momentos según la hora, su intensidad y su valencia.
        Repo.momentos.filter { it.tipo != "muestreo" && !it.privado }.groupBy { it.fecha }.forEach { (f, l) ->
            val r = fila(f)
            val horas = l.mapNotNull { Fechas.horaDe(it.ts).take(2).toIntOrNull() }
            r["n_madrugada"] = horas.count { it < 5 }.toDouble(); r["n_manana"] = horas.count { it in 5..11 }.toDouble()
            r["n_tarde"] = horas.count { it in 12..18 }.toDouble(); r["n_noche"] = horas.count { it >= 19 }.toDouble()
            r["momentos_intensidad"] = media(l.mapNotNull { it.intensidad?.toDouble() })
            r["momentos_valencia"] = media(l.mapNotNull { it.valencia?.toDouble() })
        }
        // De qué fueron tus sueños: solo en los días en que marcaste alguna categoría.
        val catSueno = categoriasSuenoUsadas()
        if (catSueno.isNotEmpty()) for ((f, d) in Repo.dias) if (d.sueno_categorias.isNotEmpty()) {
            val r = fila(f); catSueno.forEach { k -> r["sueno_$k"] = b(k in d.sueno_categorias) }
        }
        runCatching { sustancias(filas.keys.firstOrNull(), b) { f -> fila(f) } }
        // Cartas a otras personas.
        val aOtros = Repo.cartas.filter { it.para.isNotBlank() }.groupingBy { it.escrita }.eachCount()
        aOtros.keys.minOrNull()?.let { a -> Repo.dias.keys.filter { it >= a }.forEach { f -> fila(f)["cartas_otros"] = (aOtros[f] ?: 0).toDouble() } }
        aOtros.forEach { (f, n) -> fila(f)["cartas_otros"] = n.toDouble() }
        // Una variable por cada persona, tema, lugar, app, categoría, artista, obra, ciudad, nutriente, cuestionario, etapa de vida…
        if (dinamicas) runCatching { Dinamicas.calcular(extendido).valores.forEach { (f, m) -> val r = fila(f); m.forEach { (k, x) -> r[k] = x } } }
        // Dónde estuviste: ciudades, fuera de casa, otro país, primera vez y Patrimonio Mundial (de tus ubicaciones).
        runCatching {
            val ctx = io.github.ruddy2003.vestigium.data.Mundo.app ?: return@runCatching
            // En el hilo principal solo se usa lo ya calculado: recorrer todas las ubicaciones tarda.
            val enMain = android.os.Looper.myLooper() == android.os.Looper.getMainLooper()
            val diario = (if (enMain) io.github.ruddy2003.vestigium.data.Mundo.diarioListo() else io.github.ruddy2003.vestigium.data.Mundo.diario(ctx)) ?: return@runCatching
            if (diario.isEmpty()) return@runCatching
            val r0 = io.github.ruddy2003.vestigium.data.Mundo.resumenFijo ?: (if (enMain) null else io.github.ruddy2003.vestigium.data.Mundo.resumen(ctx))
            val casa = io.github.ruddy2003.vestigium.data.Perfiles.casa(perfil) ?: r0?.ciudades?.firstOrNull()?.let { it.lat to it.lon }
            val paisCasa = perfil.pais.ifBlank { r0?.paises?.firstOrNull()?.clave.orEmpty() }
            for ((d, m) in diario) {
                val f = Fechas.iso(java.time.LocalDate.ofEpochDay(d.toLong()))
                val ciudades = m.ciudades.filter { it.second >= 0.5 || it.second == 0.0 }.map { it.first }
                val paises = m.paises.filter { it.second >= 0.5 || it.second == 0.0 }.map { it.first }
                val r = fila(f)
                r["ciudades_dia"] = ciudades.size.toDouble()
                if (casa != null && ciudades.isNotEmpty()) r["fuera_de_casa"] = b(ciudades.none { io.github.ruddy2003.vestigium.data.Geo.distanciaM(it.lat, it.lon, casa.first, casa.second) < 40_000 })
                if (paisCasa.isNotBlank() && paises.isNotEmpty()) r["extranjero"] = b(paises.any { it.clave != paisCasa })
                r["ciudad_nueva"] = b(m.ciudadesNuevas.isNotEmpty())
                r["unesco"] = b(m.sitios.isNotEmpty())
            }
        }
        // Días especiales (I-6): 1 si ese día cae un cumpleaños, un aniversario o una fecha propia. Es del calendario:
        // vale para todos los días del panel.
        if (io.github.ruddy2003.vestigium.data.Especiales.hay()) filas.forEach { (f, r) -> r["dia_especial"] = b(io.github.ruddy2003.vestigium.data.Especiales.de(f).isNotEmpty()) }
        return filas
    }

    /**
     * Las variables de sustancias. Desde el primer registro de cada una, los días sin nada cuentan como 0. La cafeína y el
     * alcohol en el cuerpo se estiman con sus dosis y su hora (MacroFactor o lo anotado); «al dormirme» va en el día del sueño.
     */
    @Volatile private var dosisCache: Pair<String, Pair<List<Sustancias.Dosis>, List<Sustancias.Dosis>>>? = null

    private fun sustancias(desde: String?, b: (Boolean) -> Double, fila: (String) -> MutableMap<String, Double?>) {
        val todos = Repo.consumos
        val primero = (todos.map { it.fecha } + listOfNotNull(Repo.nutricion.keys.minOrNull())).minOrNull() ?: return
        val ini = listOfNotNull(desde, primero).minOrNull() ?: primero
        val hoy = Fechas.diaLogico()
        val dias = Repo.dias.keys + todos.map { it.fecha }
        val porDia = todos.groupBy { it.fecha }
        fun desdePrimero(id: String, k: (String, List<io.github.ruddy2003.vestigium.data.Consumo>) -> Unit) {
            val p0 = todos.filter { it.sustancia == id }.minOfOrNull { it.fecha } ?: return
            dias.filter { it >= p0 }.forEach { f -> k(f, porDia[f].orEmpty().filter { it.sustancia == id }) }
        }
        desdePrimero("alcohol") { f, l ->
            val r = fila(f)
            r["alcohol"] = b(l.isNotEmpty())
            r["alcohol_nivel_max"] = if (l.isEmpty()) 0.0 else l.mapNotNull { Sustancias.maximo(it) }.maxOrNull()?.toDouble()
            r["alcohol_horas"] = if (l.isEmpty()) 0.0 else l.mapNotNull { Sustancias.horas(it) }.takeIf { it.isNotEmpty() }?.sum()
        }
        desdePrimero("cannabis") { f, l ->
            val r = fila(f)
            r["cannabis"] = b(l.isNotEmpty())
            r["cannabis_nivel_max"] = if (l.isEmpty()) 0.0 else l.mapNotNull { Sustancias.maximo(it) }.maxOrNull()?.toDouble()
            r["cannabis_horas"] = if (l.isEmpty()) 0.0 else l.mapNotNull { Sustancias.horas(it) }.takeIf { it.isNotEmpty() }?.sum()
        }
        desdePrimero("tabaco") { f, l -> fila(f)["tabaco_n"] = l.sumOf { it.cantidad ?: 1.0 } }
        desdePrimero("otra") { f, l -> fila(f)["otra_sustancia"] = b(l.isNotEmpty()) }
        // La cruda se siente al día siguiente: va en ese día.
        todos.filter { it.cruda != null }.groupBy { Fechas.sumar(it.fecha, 1) }.forEach { (f, l) -> fila(f)["cruda"] = l.maxOf { it.cruda!! }.toDouble() }
        // Leer todos los alimentos tarda: en el hilo principal se usa lo último calculado o solo lo anotado aquí.
        val clave = "${todos.hashCode()}|${Repo.nutricion.size}|$ini|$hoy"
        val enMain = android.os.Looper.myLooper() == android.os.Looper.getMainLooper()
        val (caf, alc) = dosisCache?.takeIf { it.first == clave }?.second
            ?: Sustancias.dosis(ini, hoy, if (enMain) emptyList() else null).also { if (!enMain) dosisCache = clave to it }
        val gramos = HashMap<String, Double>()
        alc.forEach { d -> val f = Fechas.diaLogicoDeMillis(d.ms); gramos[f] = (gramos[f] ?: 0.0) + d.cantidad }
        val mg = HashMap<String, Double>()
        caf.forEach { d -> val f = Fechas.diaLogicoDeMillis(d.ms); mg[f] = (mg[f] ?: 0.0) + d.cantidad }
        val alcDesde = (alc.minOfOrNull { it.ms }?.let { Fechas.diaLogicoDeMillis(it) })
        if (alcDesde != null) dias.filter { it >= alcDesde }.forEach { f ->
            val r = fila(f); val g = gramos[f] ?: 0.0
            r["alcohol_bebidas"] = g / Sustancias.GRAMOS_BEBIDA
            if (g == 0.0) {
                r["alcohol_estimado_max"] = 0.0
                if (r["alcohol"] == null) r["alcohol"] = 0.0
            } else {
                r["alcohol"] = 1.0
            }
        }
        // Máximo estimado: una simulación por día con alcohol (de la primera dosis a 12 horas después).
        val alcPorDia = alc.groupBy { Fechas.diaLogicoDeMillis(it.ms) }
        alcPorDia.forEach { (f, l) ->
            val a = l.minOf { it.ms }
            val instantes = (a..(a + 14 * 3_600_000L) step 10 * 60_000L).toList()
            fila(f)["alcohol_estimado_max"] = Sustancias.alcoholEn(l, instantes).maxOrNull()
        }
        val cafDesde = caf.minOfOrNull { it.ms }?.let { Fechas.diaLogicoDeMillis(it) }
        if (cafDesde != null) dias.filter { it >= cafDesde }.forEach { f -> fila(f)["cafeina_total_mg"] = mg[f] ?: 0.0 }
        // Al dormirme: en el día del sueño (el reloj guarda la noche en el día en que despiertas).
        Repo.salud.forEach { (f, x) ->
            val t = x.sueno_inicio?.let { Fechas.millis(it) }?.takeIf { it > 0 } ?: return@forEach
            if (cafDesde != null && f >= cafDesde) {
                val previas = caf.filter { it.ms in (t - 36 * 3_600_000L)..t }
                fila(f)["cafeina_al_dormir_mg"] = Sustancias.cafeinaEn(previas, listOf(t)).first()
            }
            if (alcDesde != null && f >= alcDesde) {
                val previas = alc.filter { it.ms in (t - 18 * 3_600_000L)..t }
                fila(f)["alcohol_al_dormir"] = if (previas.isEmpty()) 0.0 else Sustancias.alcoholEn(previas, listOf(t)).first()
            }
        }
    }

    /**
     * El panel por semanas (de lunes a domingo): cada variable es el promedio de los días que tienen dato (en las 0/1, la
     * proporción de días). Una semana entra si tiene al menos [minDias] días con dato de esa variable.
     */
    fun semanal(panel: Map<String, Map<String, Double?>>, minDias: Int = 3): Map<String, Map<String, Double?>> {
        val suma = sortedMapOf<String, HashMap<String, DoubleArray>>()
        for ((f, r) in panel) {
            val lunes = Fechas.lunesDe(f)
            val m = suma.getOrPut(lunes) { HashMap() }
            for ((k, v) in r) if (v != null && !v.isNaN()) { val a = m.getOrPut(k) { DoubleArray(2) }; a[0] += v; a[1] += 1.0 }
        }
        val out = sortedMapOf<String, Map<String, Double?>>()
        for ((l, m) in suma) out[l] = m.filterValues { it[1] >= minDias }.mapValues { it.value[0] / it.value[1] }
        return out
    }

    fun pares(panel: Map<String, Map<String, Double?>>, x: String, y: String, desfase: Boolean, paso: Long = 1L): List<Par> {
        val out = ArrayList<Par>()
        for ((f, r) in panel) {
            val ry = if (desfase) panel[Fechas.sumar(f, paso)] else r
            val a = r[x] ?: continue
            val bb = ry?.get(y) ?: continue
            out.add(Par(a, bb, f))
        }
        return out
    }

    // ---------- Relaciones entre todas las variables ----------

    /**
     * Variables que salen de la misma fuente o se calculan una de otra. Entre ellas la correlación es casi segura
     * y no dice nada de ti (las calorías suben con la proteína; los eventos, con los minutos de agenda).
     * Una variable puede estar en varias familias.
     */
    val FAMILIAS: List<Set<String>> = listOf(
        setOf("kcal", "proteina_g", "carbohidratos_g", "grasa_g", "fibra_g", "alcohol_g", "azucares_g", "cafeina_mg", "balance_kcal", "primera_comida_h", "ultima_comida_h", "cafeina_ultima_h"),
        setOf("peso_kg", "peso_tendencia_kg"),
        setOf("eventos", "min_ocupado"),
        setOf("sueno_horas", "sueno_profundo", "sueno_rem", "sueno_inicio_h", "sueno_fin_h"),
        setOf("sueno_recordado", "sueno_valencia", "sueno_lucidez", "sueno_vividez", "sueno_palabras"),
        setOf("ingreso_total", "balance_dinero", "gasto_total"), setOf("prediccion", "error_prediccion"),
        setOf("valencia", "muestreo_valencia"), setOf("activacion", "muestreo_activacion"),
        setOf("avisos_planeados", "avisos_mostrados", "avisos_respondidos", "tasa_respuesta", "muestreo_valencia", "muestreo_activacion", "n_muestreos"),
        setOf("hrv", "fc_reposo", "fc_media", "fc_sueno"), setOf("spo2", "spo2_min"),
        setOf("pasos", "a_pie_min", "cal_activas", "ejercicio_min", "distancia_km"),
        setOf("distancia_km", "vehiculo_min", "coche_min", "sin_wifi_min", "lugares_distintos"),
        setOf("gimnasio", "volumen_kg", "series_gimnasio", "ejercicio_min", "cal_activas"),
        setOf("escalada", "escalada_grado", "ejercicio_min", "cal_activas"),
        setOf("pantalla_min", "desbloqueos", "notificaciones", "primer_uso_h", "ultimo_uso_h"),
        setOf("ultimo_uso_h", "carga_noche_h", "sueno_inicio_h"),
        setOf("primer_uso_h", "sueno_fin_h"),
        setOf("llamadas", "min_hablado", "contactos"),
        setOf("wa_mios", "wa_otros", "wa_chats"),
        setOf("canciones", "musica_min", "audifonos_min", "coche_min"),
        setOf("palabras", "personas", "hitos_n"),
        setOf("yt_vistos", "busquedas", "yt_megusta", "yt_comentarios"),
        setOf("t_max", "t_min", "precip_mm", "nubes_pct", "luz_horas", "sol_horas", "presion_hpa"),
        setOf("aqi_us", "pm25"),
        setOf("gasto_total", "gasto_imprevisto"),
        setOf("impulso", "recaida", "n_recaida", "n_impulso", "recaida_min", "libido_max", "n_libido"),
        setOf("presion_local_hpa", "presion_hpa", "cambio_presion_3h"),
        setOf("laptop_min", "laptop_web_min", "laptop_productivo_min", "laptop_ocio_min", "laptop_social_min", "laptop_otro_min"),
        setOf("laptop_productivo_min", "enfoque_min", "enfoque_estudio_min", "enfoque_tesis_min", "enfoque_trabajo_min"),
        setOf("creatina_saturacion_pct", "supl_creatina_dosis", "supl_creatina_tomado", "supl_creatina_nivel"),
        setOf("probioticos_porciones", "probioticos_estimado", "supl_probioticos_porciones", "supl_probioticos_dosis", "supl_probioticos_tomado"),
        setOf("animo_cierre_valencia", "valencia", "animo_valencia"), setOf("animo_cierre_activacion", "activacion", "animo_activacion"),
        setOf("horas_tipo_casa", "en_casa_min", "sin_wifi_min", "fuera_de_casa"),
        setOf("enfoque_min", "enfoque_estudio_min", "enfoque_lectura_min", "enfoque_idiomas_min", "enfoque_tesis_min", "enfoque_trabajo_min", "enfoque_creativo_min"),
        setOf("siesta", "siesta_min"),
        setOf("cama_min", "dormir_tras_tel_min", "ultimo_uso_h", "sueno_inicio_h", "carga_noche_h"),
        setOf("tel_tras_despertar_min", "primer_uso_h", "sueno_fin_h"),
        setOf("aire_libre_min", "luz_alta_min", "a_pie_min", "pasos"),
        setOf("en_casa_min", "sin_wifi_min", "lugares_distintos", "distancia_km", "fuera_de_casa"),
        setOf("acompanado_pct", "con_pareja", "con_amigos", "con_familia", "con_companeros", "con_desconocidos", "valencia_acompanado", "valencia_solo", "muestreo_valencia"),
        setOf("obras_dia", "lectura", "vi_pelicula_serie", "jugue", "obra_terminada", "calificacion_obra"),
        setOf("ciudades_dia", "fuera_de_casa", "extranjero", "ciudad_nueva", "unesco", "distancia_km", "vehiculo_min"),
        setOf("cercanos", "vi_familia", "vi_pareja", "vi_amistades", "vi_convivientes", "personas", "palabras"),
        setOf("sueno_palabras", "sueno_recordado", "sueno_valencia"),
        setOf("carta_escrita", "palabras"),
        setOf("animo_valencia", "valencia", "muestreo_valencia", "momentos_valencia", "valencia_acompanado", "valencia_solo"),
        setOf("animo_activacion", "activacion", "muestreo_activacion"),
        setOf("registros_animo", "n_madrugada", "n_manana", "n_tarde", "n_noche"),
        setOf("alcohol", "alcohol_g", "alcohol_bebidas", "alcohol_nivel_max", "alcohol_horas", "alcohol_estimado_max", "alcohol_al_dormir"),
        setOf("cannabis", "cannabis_nivel_max", "cannabis_horas"),
        setOf("cafeina_mg", "cafeina_total_mg", "cafeina_ultima_h", "cafeina_al_dormir_mg"),
        setOf("cercanos", "vi_familia", "vi_pareja", "vi_amistades", "vi_expareja", "vi_trabajo", "cercania_media", "personas"),
        setOf("personas", "personas_vistas", "personas_pensadas", "personas_mensajes", "cercanos"),
        setOf("personas_pensadas", "penso_en_pareja", "penso_en_expareja", "penso_en_familia", "penso_en_amistades"),
    )

    fun obvia(a: Variable, b: Variable): Boolean {
        // Una campaña y lo que ese reto busca mover (la elegiste para eso).
        if (Campanas.obvia(a.id, b.id) || Campanas.obvia(b.id, a.id)) return true
        if ((a.grupo == b.grupo && (a.grupo == "Lectura del día" || a.grupo == "Momentos" || a.grupo == "Sentimientos" || a.grupo == "Sueños")) || FAMILIAS.any { a.id in it && b.id in it }) return true
        // Las horas por tipo de lugar se reparten el mismo día entre sí y con cada lugar.
        val lugA = a.id.startsWith("horas_tipo_") || a.id.startsWith("lug_"); val lugB = b.id.startsWith("horas_tipo_") || b.id.startsWith("lug_")
        if (lugA && lugB) return true
        // Las variables del mismo suplemento (dosis, si se tomó, nivel) se mueven juntas por construcción.
        fun supl(id: String) = if (id.startsWith("supl_")) id.removePrefix("supl_").substringBeforeLast('_') else null
        if (supl(a.id) != null && supl(a.id) == supl(b.id)) return true
        // Lo que sentías con el número de registros del día, y cada categoría del sueño con haber recordado un sueño.
        if ((a.grupo == "Sentimientos" && b.id == "registros_animo") || (b.grupo == "Sentimientos" && a.id == "registros_animo")) return true
        if ((a.grupo == "Sueños" && b.id.startsWith("sueno_")) || (b.grupo == "Sueños" && a.id.startsWith("sueno_"))) return true
        // Las variables de cada persona, app o categoría con su total (minutos en una app y minutos de pantalla).
        val pa = Dinamicas.prefijo(a.id); val pb = Dinamicas.prefijo(b.id)
        if (pa != null && Dinamicas.AGREGADOS[pa]?.contains(b.id) == true) return true
        if (pb != null && Dinamicas.AGREGADOS[pb]?.contains(a.id) == true) return true
        if (pa != null && pb != null && Dinamicas.ENTRE_SI.any { pa in it && pb in it }) return true
        // La misma persona: «aparece @ana» y «pensé en @ana sin verla».
        if (setOf(pa, pb) == setOf("per_", "pen_") && a.id.substringAfter('_') == b.id.substringAfter('_')) return true
        // Dos puntajes del mismo cuestionario salen de las mismas respuestas.
        if (pa == "esc_" && pb == "esc_" && a.id.split('_').getOrNull(1) == b.id.split('_').getOrNull(1)) return true
        return false
    }

    /** Días hacia cada lado con los que se compara cada día al quitar la tendencia. */
    private const val VENTANA = 10

    /** Una variable alineada a las fechas del panel (NaN = sin dato), con su versión sin tendencia y su autocorrelación. */
    private class Serie(val crudo: DoubleArray, val resto: DoubleArray, val r1Crudo: Double, val r1Resto: Double, val lenta: Boolean, val binaria: Boolean)

    private fun autocorr(x: DoubleArray, dia: LongArray, paso: Long = 1L): Double {
        val a = ArrayList<Double>(); val b = ArrayList<Double>()
        for (i in 0 until x.size - 1) if (!x[i].isNaN() && !x[i + 1].isNaN() && dia[i + 1] - dia[i] == paso) { a.add(x[i]); b.add(x[i + 1]) }
        if (a.size < 10) return 0.0
        return pearson(a, b) ?: 0.0
    }

    private fun varianza(v: DoubleArray): Double {
        val l = v.filter { !it.isNaN() }; if (l.size < 2) return 0.0
        val m = l.average(); return l.sumOf { (it - m) * (it - m) } / (l.size - 1)
    }

    private fun series(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, paso: Long = 1L, ventana: Int = VENTANA): Map<String, Serie> {
        val fechas = panel.keys.sorted()
        val dia = LongArray(fechas.size) { Fechas.parse(fechas[it]).toEpochDay() }
        val out = HashMap<String, Serie>()
        for (v in vars) {
            val x = DoubleArray(fechas.size) { panel[fechas[it]]?.get(v.id) ?: Double.NaN }
            val idx = x.indices.filter { !x[it].isNaN() }
            if (idx.size < 3) continue
            // Cada día menos el promedio de sus vecinos (±VENTANA días de calendario): queda lo que ese día tuvo de distinto.
            val resto = DoubleArray(x.size) { Double.NaN }
            var lo = 0; var hi = 0; var suma = 0.0; var cnt = 0
            for (i in idx) {
                while (hi < idx.size && dia[idx[hi]] <= dia[i] + ventana) { suma += x[idx[hi]]; cnt++; hi++ }
                while (dia[idx[lo]] < dia[i] - ventana) { suma -= x[idx[lo]]; cnt--; lo++ }
                if (cnt >= 3) resto[i] = x[i] - suma / cnt
            }
            val vc = varianza(x); val vr = varianza(resto)
            val binaria = idx.all { x[it] == 0.0 || x[it] == 1.0 }
            // «Lenta»: casi toda su variación es de semanas a meses (horas de luz, peso suavizado). Las constantes no cuentan.
            val lenta = vc > 0.0 && resto.count { !it.isNaN() } >= (if (paso > 1) 8 else 14) && vr / vc < 0.12
            out[v.id] = Serie(x, resto, autocorr(x, dia, paso), autocorr(resto, dia, paso), lenta, binaria)
        }
        return out
    }

    /**
     * Revisa todos los pares de variables. Con [sinTendencia], cada día se compara con sus semanas cercanas, así que
     * dos cosas que solo cambiaron con el tiempo (días más cortos mientras bajabas de peso) dejan de parecer relacionadas.
     * El intervalo usa días efectivos: si hoy se parece a ayer, dos días seguidos no cuentan como dos pruebas.
     * [q] es el valor corregido por comparaciones múltiples (Benjamini-Hochberg) entre los pares no obvios.
     */
    fun relaciones(panel: Map<String, Map<String, Double?>>, vars: List<Variable>, sinTendencia: Boolean, minN: Int = 14,
                   paso: Long = 1L, ventana: Int = VENTANA): Relaciones {
        val s = series(panel, vars, paso, ventana)
        val usables = vars.filter { it.id in s }
        val lentas = if (sinTendencia) usables.filter { s[it.id]!!.lenta } else emptyList()
        val res = ArrayList<Correlacion>()
        val largo = s.values.firstOrNull()?.crudo?.size ?: 0
        val xs = DoubleArray(largo); val ys = DoubleArray(largo); val cx = DoubleArray(largo); val cy = DoubleArray(largo)
        fun r(a: DoubleArray, b: DoubleArray, n: Int): Double? {
            var ma = 0.0; var mb = 0.0
            for (i in 0 until n) { ma += a[i]; mb += b[i] }
            ma /= n; mb /= n
            var sab = 0.0; var saa = 0.0; var sbb = 0.0
            for (i in 0 until n) { val da = a[i] - ma; val db = b[i] - mb; sab += da * db; saa += da * da; sbb += db * db }
            return if (saa == 0.0 || sbb == 0.0) null else sab / sqrt(saa * sbb)
        }
        for (i in usables.indices) for (j in i + 1 until usables.size) {
            val a = usables[i]; val b = usables[j]
            val sa = s[a.id]!!; val sb = s[b.id]!!
            if (sinTendencia && (sa.lenta || sb.lenta)) continue
            val va = if (sinTendencia) sa.resto else sa.crudo; val vb = if (sinTendencia) sb.resto else sb.crudo
            var n = 0
            for (k in va.indices) { val p = va[k]; val q = vb[k]; if (!p.isNaN() && !q.isNaN()) { xs[n] = p; ys[n] = q; cx[n] = sa.crudo[k]; cy[n] = sb.crudo[k]; n++ } }
            if (n < minN) continue
            // Una variable 0/1 necesita algunos días de cada tipo para decir algo.
            if (sa.binaria) { var unos = 0; for (k in 0 until n) if (cx[k] == 1.0) unos++; if (minOf(unos, n - unos) < 4) continue }
            if (sb.binaria) { var unos = 0; for (k in 0 until n) if (cy[k] == 1.0) unos++; if (minOf(unos, n - unos) < 4) continue }
            val rr = r(xs, ys, n) ?: continue
            val bruta = r(cx, cy, n) ?: rr
            val ra = (if (sinTendencia) sa.r1Resto else sa.r1Crudo).coerceIn(-0.5, 0.97)
            val rb = (if (sinTendencia) sb.r1Resto else sb.r1Crudo).coerceIn(-0.5, 0.97)
            val nEf = (n * (1 - ra * rb) / (1 + ra * rb)).coerceIn(minOf(4.0, n.toDouble()), n.toDouble())
            if (nEf <= 3.0) continue
            val z = atanh(rr.coerceIn(-0.9999, 0.9999)); val se = 1 / sqrt(nEf - 3)
            val p = Analisis.pBilateral(z / se)
            res.add(Correlacion(a, b, rr, n, tanh(z - 1.96 * se) to tanh(z + 1.96 * se), bruta, nEf, p, obvia = obvia(a, b)))
        }
        val limpias = res.filter { !it.obvia }
        Analisis.bh(limpias.map { it.p }).forEachIndexed { i, q -> limpias[i].q = q }
        Analisis.bh(res.map { it.p }).forEachIndexed { i, q -> res[i].qConObvias = q }
        // Primero lo que se sostiene aun en el peor caso del intervalo; luego, por fuerza.
        fun seguro(c: Correlacion) = c.ic?.let { if (it.first > 0) it.first else if (it.second < 0) -it.second else 0.0 } ?: 0.0
        val orden = res.sortedWith(compareByDescending<Correlacion> { seguro(it) }.thenByDescending { abs(it.r) })
        return Relaciones(orden, res.count { it.obvia }, lentas, sinTendencia)
    }
}

object Movilidad {
    /** Suma de distancias entre muestras consecutivas del mismo día, ignorando saltos imposibles. */
    fun kmDe(fecha: String): Double? {
        val a = Fechas.inicioDia(fecha) + 5 * 3_600_000L
        return kmPorDia(Repo.db().ubicaciones(a, a + 86_400_000L))[fecha]
    }

    /** Lo mismo que [kmPorDia] entre dos días lógicos, recorriendo la base sin cargar todos los puntos a memoria. */
    fun kmEntre(desde: String, hasta: String): Map<String, Double> {
        val out = HashMap<String, Double>()
        var pMs = -1L; var pLat = 0.0; var pLon = 0.0
        val ini = if (desde <= "1971-01-01") 0L else Fechas.inicioDia(desde)
        val fin = if (hasta >= "9999-01-01") Long.MAX_VALUE else Fechas.finDia(hasta) + 5 * 3_600_000L
        Repo.db().recorrerUbicaciones(ini, fin) { ms, lat, lon, _ ->
            if (pMs >= 0) {
                val dt = (ms - pMs) / 1000.0
                val d = io.github.ruddy2003.vestigium.data.Geo.distanciaM(pLat, pLon, lat, lon)
                if (dt in 1.0..7200.0 && d / dt < 70 && d > 30) {
                    val f = Fechas.diaLogicoDeMillis(ms)
                    if (f in desde..hasta) out[f] = (out[f] ?: 0.0) + d / 1000
                }
            }
            pMs = ms; pLat = lat; pLon = lon
        }
        return out
    }

    fun kmPorDia(puntos: List<io.github.ruddy2003.vestigium.data.Db.Punto> = Repo.db().ubicaciones()): Map<String, Double> {
        val out = HashMap<String, Double>()
        var prev: io.github.ruddy2003.vestigium.data.Db.Punto? = null
        for (p in puntos) {
            val pr = prev
            if (pr != null) {
                val dt = (p.ms - pr.ms) / 1000.0
                val d = io.github.ruddy2003.vestigium.data.Geo.distanciaM(pr.lat, pr.lon, p.lat, p.lon)
                if (dt in 1.0..7200.0 && d / dt < 70 && d > 30) {
                    val f = Fechas.diaLogicoDeMillis(p.ms)
                    out[f] = (out[f] ?: 0.0) + d / 1000
                }
            }
            prev = p
        }
        return out
    }
}
