package io.github.ruddy2003.vestigium.data

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.KSerializer
import java.util.UUID

/**
 * Estado de toda la app en memoria, respaldado por SQLite. Cada cambio se escribe primero en disco
 * y luego se publica a la interfaz en el hilo principal.
 */
object Repo {
    private lateinit var db: Db
    private val main = Handler(Looper.getMainLooper())
    @Volatile private var listo = false

    var dias by mutableStateOf<Map<String, Dia>>(emptyMap()); private set
    var momentos by mutableStateOf<List<Momento>>(emptyList()); private set
    var gastos by mutableStateOf<List<Gasto>>(emptyList()); private set
    var ingresos by mutableStateOf<List<Ingreso>>(emptyList()); private set
    var respuestas by mutableStateOf<List<Respuesta>>(emptyList()); private set
    var reacciones by mutableStateOf<List<Reaccion>>(emptyList()); private set
    var lugares by mutableStateOf<List<Lugar>>(emptyList()); private set
    var visitas by mutableStateOf<List<Visita>>(emptyList()); private set
    var youtube by mutableStateOf<List<YtEvento>>(emptyList()); private set
    var salud by mutableStateOf<Map<String, SaludDia>>(emptyMap()); private set
    var pantalla by mutableStateOf<Map<String, PantallaDia>>(emptyMap()); private set
    var cartas by mutableStateOf<List<Carta>>(emptyList()); private set
    var experimentos by mutableStateOf<List<Experimento>>(emptyList()); private set
    var config by mutableStateOf(Config()); private set
    var entorno by mutableStateOf<Map<String, EntornoDia>>(emptyMap()); private set
    var fotos by mutableStateOf<Map<String, FotosDia>>(emptyMap()); private set
    var agenda by mutableStateOf<Map<String, AgendaDia>>(emptyMap()); private set
    var notifs by mutableStateOf<Map<String, NotifDia>>(emptyMap()); private set
    var actividad by mutableStateOf<Map<String, ActividadDia>>(emptyMap()); private set
    var sesiones by mutableStateOf<List<Sesion>>(emptyList()); private set
    var ascensos by mutableStateOf<List<Ascenso>>(emptyList()); private set
    var obras by mutableStateOf<List<Obra>>(emptyList()); private set
    var comunicacion by mutableStateOf<Map<String, ComunicacionDia>>(emptyMap()); private set
    var conexiones by mutableStateOf<Map<String, ConexionDia>>(emptyMap()); private set
    var subrayados by mutableStateOf<List<Subrayado>>(emptyList()); private set
    var huellas by mutableStateOf<Map<String, HuellasDia>>(emptyMap()); private set
    var chats by mutableStateOf<List<ChatDia>>(emptyList()); private set
    var nutricion by mutableStateOf<Map<String, NutricionDia>>(emptyMap()); private set
    var entrenos by mutableStateOf<Map<String, EntrenoDia>>(emptyMap()); private set
    /** Bloques de enfoque y siestas del cronómetro. */
    var tramos by mutableStateOf<List<Tramo>>(emptyList()); private set
    /** Consumos de sustancias (episodios con inicio, fin y niveles). */
    var consumos by mutableStateOf<List<Consumo>>(emptyList()); private set
    /** Notas sueltas (ideas, pensamientos, propósitos, recordatorios) y metas. */
    var notas by mutableStateOf<List<Nota>>(emptyList()); private set
    var metas by mutableStateOf<List<Meta>>(emptyList()); private set
    /** El Ideario: lo que aprendiste o pensaste, con su fuente (esquema 16). */
    var ideas by mutableStateOf<List<Idea>>(emptyList()); private set
    /** Listas libres (esquema 18): lugares por visitar, cosas por comprar… */
    var listas by mutableStateOf<List<Lista>>(emptyList()); private set
    /** Tomas de suplementos y minutos de la laptop (Cold Turkey). */
    var tomas by mutableStateOf<List<TomaSuplemento>>(emptyList()); private set
    var laptop by mutableStateOf<Map<String, LaptopDia>>(emptyMap()); private set
    /** Los avisos de ánimo de cada día: planeados, vencidos y mostrados (v13). */
    var avisos by mutableStateOf<Map<String, AvisosDia>>(emptyMap()); private set
    /** Documentos que no se pudieron leer, por colección (se muestran en Estado de los datos). */
    var ilegibles by mutableStateOf<Map<String, Int>>(emptyMap()); private set
    /** Si la configuración no se pudo leer al abrir: la hora con que se guardó la copia del original (colección config_ilegible). */
    var configIlegible by mutableStateOf<String?>(null); private set

    @Synchronized
    fun init(ctx: Context) {
        if (listo) return
        db = Db(ctx.applicationContext)
        // Las colecciones se leen y decodifican a la vez, en varios hilos: antes iban una tras otra en el hilo principal y
        // el arranque se alargaba con cada año de datos (con riesgo de que Android diera la app por congelada).
        val hilos = java.util.concurrent.Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors().coerceIn(2, 4)) { r -> Thread(r, "vm-carga").apply { isDaemon = true } }
        fun <T, R> pedir(col: String, s: KSerializer<T>, f: (List<T>) -> R): java.util.concurrent.Future<R> = hilos.submit(java.util.concurrent.Callable { f(cargar(col, s)) })
        val f_dias = pedir("dias", Dia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_momentos = pedir("momentos", Momento.serializer()) { l -> l.sortedBy { it.ts } }
        val f_gastos = pedir("gastos", Gasto.serializer()) { l -> l.sortedBy { it.fecha + it.hora } }
        val f_ingresos = pedir("ingresos", Ingreso.serializer()) { l -> l.sortedBy { it.fecha + it.hora } }
        val f_respuestas = pedir("respuestas", Respuesta.serializer()) { l -> l.sortedBy { it.ts } }
        val f_reacciones = pedir("reacciones", Reaccion.serializer()) { l -> l.sortedBy { it.ts } }
        val f_lugares = pedir("lugares", Lugar.serializer()) { l -> l }
        val f_visitas = pedir("visitas", Visita.serializer()) { l -> l.sortedBy { it.entrada } }
        val f_youtube = pedir("youtube", YtEvento.serializer()) { l -> l.sortedBy { it.ts } }
        val f_salud = pedir("salud", SaludDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_pantalla = pedir("pantalla", PantallaDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_cartas = pedir("cartas", Carta.serializer()) { l -> l }
        val f_experimentos = pedir("experimentos", Experimento.serializer()) { l -> l }
        val f_entorno = pedir("entorno", EntornoDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_fotos = pedir("fotos", FotosDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_agenda = pedir("agenda", AgendaDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_notifs = pedir("notifs", NotifDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_actividad = pedir("actividad", ActividadDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_sesiones = pedir("sesiones", Sesion.serializer()) { l -> l.sortedBy { it.inicio } }
        val f_ascensos = pedir("ascensos", Ascenso.serializer()) { l -> l.sortedBy { it.fecha + (it.inicio ?: "") } }
        val f_obras = pedir("obras", Obra.serializer()) { l -> l }
        val f_comunicacion = pedir("comunicacion", ComunicacionDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_conexiones = pedir("conexiones", ConexionDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_subrayados = pedir("subrayados", Subrayado.serializer()) { l -> l.sortedBy { it.ts ?: "" } }
        val f_huellas = pedir("huellas_dia", HuellasDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_chats = pedir("whatsapp", ChatDia.serializer()) { l -> l.sortedBy { it.fecha } }
        val f_nutricion = pedir("nutricion", NutricionDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_entrenos = pedir("entrenos", EntrenoDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_tramos = pedir("tramos", Tramo.serializer()) { l -> l.sortedBy { it.inicio } }
        val f_consumos = pedir("consumos", Consumo.serializer()) { l -> l.sortedBy { it.inicio } }
        val f_notas = pedir("notas", Nota.serializer()) { l -> l.sortedByDescending { it.creada } }
        val f_metas = pedir("metas", Meta.serializer()) { l -> l.sortedBy { it.creada } }
        val f_ideas = pedir("ideas", Idea.serializer()) { l -> l.sortedByDescending { it.creada } }
        val f_listas = pedir("listas", Lista.serializer()) { l -> l.sortedWith(compareBy<Lista>({ it.orden }, { it.creada })) }
        val f_tomas = pedir("tomas", TomaSuplemento.serializer()) { l -> l.sortedBy { it.ts } }
        val f_laptop = pedir("laptop", LaptopDia.serializer()) { l -> l.associateBy { it.fecha } }
        val f_avisos = pedir("avisos", AvisosDia.serializer()) { l -> l.associateBy { it.fecha } }
        dias = f_dias.dato()
        momentos = f_momentos.dato()
        gastos = f_gastos.dato()
        ingresos = f_ingresos.dato()
        respuestas = f_respuestas.dato()
        reacciones = f_reacciones.dato()
        lugares = f_lugares.dato()
        visitas = f_visitas.dato()
        youtube = f_youtube.dato()
        salud = f_salud.dato()
        pantalla = f_pantalla.dato()
        cartas = f_cartas.dato()
        experimentos = f_experimentos.dato()
        // Si la configuración no se puede leer, se guarda el texto original aparte antes de seguir con una vacía:
        // así el siguiente cambio de ajustes no lo sobrescribe (se perderían personas, hábitos, perfil, campañas…).
        val crudo = db.uno("config", "config")
        config = crudo?.let { t ->
            runCatching { JSON.decodeFromString(Config.serializer(), t) }.getOrElse {
                val ya = db.todos("config_ilegible").any { it == t }
                val ts = Fechas.ahoraIso()
                if (!ya) db.poner("config_ilegible", ts, null, t)
                configIlegible = ts
                null
            }
        } ?: Config()
        // El tema «Día de Muertos» se convirtió en «Botánico»: quien lo tenía elegido pasa al nuevo.
        if (config.tema == "muertos") { config = config.copy(tema = "botanico"); db.poner("config", "config", null, JSON.encodeToString(Config.serializer(), config)) }
        entorno = f_entorno.dato()
        fotos = f_fotos.dato()
        agenda = f_agenda.dato()
        notifs = f_notifs.dato()
        actividad = f_actividad.dato()
        sesiones = f_sesiones.dato()
        ascensos = f_ascensos.dato()
        obras = f_obras.dato()
        comunicacion = f_comunicacion.dato()
        conexiones = f_conexiones.dato()
        subrayados = f_subrayados.dato()
        huellas = f_huellas.dato()
        chats = f_chats.dato()
        nutricion = f_nutricion.dato()
        entrenos = f_entrenos.dato()
        tramos = f_tramos.dato()
        consumos = f_consumos.dato()
        notas = f_notas.dato()
        metas = f_metas.dato()
        ideas = f_ideas.dato()
        listas = f_listas.dato()
        tomas = f_tomas.dato()
        laptop = f_laptop.dato()
        avisos = f_avisos.dato()
        hilos.shutdown()
        ilegibles = ilegiblesAlAbrir.toMap()
        Fechas.usarHistorial(config.zonas)
        listo = true
        runCatching { registrarZona() }
    }

    /**
     * Anota la zona del teléfono si cambió desde la última (al abrir la app, cada 15 minutos y cuando el sistema avisa de un
     * cambio de zona). La primera vez arma el historial con lo ya guardado: los periodos en que tus notas traen un desfase que
     * la zona de hoy no daría (al menos dos notas seguidas) quedan con ese desfase; lo demás, con la zona de hoy (H-1-7).
     */
    @Synchronized
    fun registrarZona() {
        val z = Fechas.zona().id
        val l = config.zonas
        if (l.isNotEmpty()) {
            if (l.maxByOrNull { Fechas.millis(it.desde) }?.zona != z) cambiarConfig { it.copy(zonas = it.zonas + CambioZona(Fechas.ahoraIso(), z)) }
            return
        }
        val zona = Fechas.zona()
        val marcas = (momentos.map { it.ts } + respuestas.map { it.ts } + reacciones.map { it.ts } + tomas.map { it.ts })
            .mapNotNull { t -> runCatching { java.time.OffsetDateTime.parse(t) }.getOrNull() }
            .filter { it.offset != java.time.ZoneOffset.UTC }.sortedBy { it.toInstant() }
        val cambios = arrayListOf(CambioZona("1970-01-01T00:00:00Z", z))
        var actual: java.time.ZoneOffset? = null
        for (i in marcas.indices) {
            val t = marcas[i]
            val propio = if (zona.rules.getOffset(t.toInstant()) == t.offset) null else t.offset
            if (propio == actual) continue
            // Un desfase raro suelto (una nota editada en otra zona) no abre un periodo: hacen falta dos seguidas.
            if (marcas.getOrNull(i + 1)?.let { s -> (if (zona.rules.getOffset(s.toInstant()) == s.offset) null else s.offset) == propio } != true) continue
            cambios += CambioZona(t.withNano(0).toString(), propio?.id ?: z)
            actual = propio
        }
        if (actual != null) cambios += CambioZona(Fechas.ahoraIso(), z)
        cambiarConfig { it.copy(zonas = cambios) }
    }

    fun db(): Db = db

    private fun <T> cargar(col: String, s: KSerializer<T>): List<T> = leer(col, db.todos(col), s)

    /** Decodifica y anota cuántos documentos de [col] no se pudieron leer (en las consultas por fechas, el mayor conteo visto). */
    private fun <T> leer(col: String, textos: List<String>, s: KSerializer<T>): List<T> {
        val (l, fallidos) = Decodificar.todos(textos, s)
        if (fallidos > 0) {
            // Al abrir, varios hilos leen a la vez: se juntan aparte y se publican al final de init.
            if (listo) { val n = maxOf(fallidos, ilegibles[col] ?: 0); enMain { ilegibles = ilegibles + (col to n) } }
            else ilegiblesAlAbrir.merge(col, fallidos) { a, b -> maxOf(a, b) }
        }
        return l
    }

    private val ilegiblesAlAbrir = java.util.concurrent.ConcurrentHashMap<String, Int>()

    /** El resultado de una lectura en paralelo; si falló, el error original (no el envoltorio del hilo). */
    private fun <R> java.util.concurrent.Future<R>.dato(): R =
        try { get() } catch (e: java.util.concurrent.ExecutionException) { throw e.cause ?: e }

    private fun enMain(b: () -> Unit) { if (Looper.myLooper() == Looper.getMainLooper()) b() else main.post(b) }

    fun nuevoId(): String = UUID.randomUUID().toString().replace("-", "").take(16)

    // ---------- Días ----------
    fun dia(fecha: String): Dia = dias[fecha] ?: Dia(fecha = fecha)

    @Synchronized
    fun guardarDia(d: Dia) {
        val ahora = Fechas.ahoraIso()
        val previo = dias[d.fecha]
        val tsVal = Reglas.tsValencia(d, previo, ahora)
        val retro = Reglas.retroactivo(d.fecha, tsVal, previo?.retroactivo, Fechas.diaLogico())
        val final = d.copy(schema_v = SCHEMA_V, ts_creado = d.ts_creado ?: previo?.ts_creado ?: ahora, ts_editado = ahora, retroactivo = retro, ts_valencia = tsVal)
        db.poner("dias", final.fecha, final.fecha, JSON.encodeToString(Dia.serializer(), final))
        enMain { dias = dias + (final.fecha to final) }
    }

    fun importarDias(lista: List<Dia>, sobrescribir: Boolean): Int {
        var n = 0
        db.transaccion {
            for (d in lista) {
                if (!sobrescribir && dias.containsKey(d.fecha)) continue
                db.poner("dias", d.fecha, d.fecha, JSON.encodeToString(Dia.serializer(), d)); n++
            }
        }
        // La mezcla se hace ya en el hilo principal, sobre lo que haya en ese momento: si se arma antes (la restauración
        // corre en otro hilo), un día que guardaste mientras tanto desaparecía de la memoria y la siguiente edición
        // partía de la versión vieja.
        enMain {
            val nuevos = dias.toMutableMap()
            lista.forEach { if (sobrescribir || !nuevos.containsKey(it.fecha)) nuevos[it.fecha] = it }
            dias = nuevos
        }
        return n
    }

    // ---------- Momentos ----------
    @Synchronized
    fun guardarMomento(m: Momento) {
        db.poner("momentos", m.id, m.fecha, JSON.encodeToString(Momento.serializer(), m))
        enMain { momentos = (momentos.filter { it.id != m.id } + m).sortedBy { it.ts } }
    }
    /** Los momentos de un respaldo que no están aquí, de una vez: una transacción y un solo reordenamiento. */
    fun importarMomentos(lista: List<Momento>) {
        val ids = momentos.map { it.id }.toHashSet()
        val nuevos = lista.filter { it.id !in ids }.distinctBy { it.id }
        if (nuevos.isEmpty()) return
        db.transaccion { nuevos.forEach { db.poner("momentos", it.id, it.fecha, JSON.encodeToString(Momento.serializer(), it)) } }
        enMain { val ya = momentos.map { it.id }.toHashSet(); momentos = (momentos + nuevos.filter { it.id !in ya }).sortedBy { it.ts } }
    }
    fun momento(id: String): Momento? = momentos.firstOrNull { it.id == id } ?: db.uno("momentos", id)?.let { JSON.decodeFromString(Momento.serializer(), it) }
    fun borrarMomento(id: String) { aPapelera("momentos", id); db.borrar("momentos", id); enMain { momentos = momentos.filter { it.id != id } } }

    // ---------- Tramos (enfoque y siestas) ----------
    fun guardarTramo(t: Tramo) {
        db.poner("tramos", t.id, t.fecha, JSON.encodeToString(Tramo.serializer(), t))
        enMain { tramos = (tramos.filter { it.id != t.id } + t).sortedBy { it.inicio } }
    }
    fun borrarTramo(id: String) { aPapelera("tramos", id); db.borrar("tramos", id); enMain { tramos = tramos.filter { it.id != id } } }
    fun importarTramos(lista: List<Tramo>) {
        val ids = tramos.map { it.id }.toSet()
        val nuevos = lista.filter { it.id !in ids }
        if (nuevos.isEmpty()) return
        db.transaccion { nuevos.forEach { db.poner("tramos", it.id, it.fecha, JSON.encodeToString(Tramo.serializer(), it)) } }
        enMain { tramos = (tramos + nuevos).sortedBy { it.inicio } }
    }
    /** Solo para las capturas de diseño. */
    fun tramosEnMemoria(l: List<Tramo>) { tramos = l }

    // ---------- Consumos de sustancias ----------
    @Synchronized
    fun guardarConsumo(c: Consumo) {
        db.poner("consumos", c.id, c.fecha, JSON.encodeToString(Consumo.serializer(), c))
        enMain { consumos = (consumos.filter { it.id != c.id } + c).sortedBy { it.inicio } }
    }
    fun consumo(id: String): Consumo? = consumos.firstOrNull { it.id == id }
    fun borrarConsumo(id: String) { aPapelera("consumos", id); db.borrar("consumos", id); enMain { consumos = consumos.filter { it.id != id } } }
    fun importarConsumos(lista: List<Consumo>) {
        val ids = consumos.map { it.id }.toSet()
        val nuevos = lista.filter { it.id !in ids }
        if (nuevos.isEmpty()) return
        db.transaccion { nuevos.forEach { db.poner("consumos", it.id, it.fecha, JSON.encodeToString(Consumo.serializer(), it)) } }
        enMain { consumos = (consumos + nuevos).sortedBy { it.inicio } }
    }
    fun consumosEnMemoria(l: List<Consumo>) { consumos = l }
    fun experimentosEnMemoria(l: List<Experimento>) { experimentos = l }

    // ---------- Notas y metas ----------
    fun guardarNota(n: Nota) {
        db.poner("notas", n.id, n.fecha, JSON.encodeToString(Nota.serializer(), n))
        enMain { notas = (notas.filter { it.id != n.id } + n).sortedByDescending { it.creada } }
    }
    fun borrarNota(id: String) { aPapelera("notas", id); db.borrar("notas", id); enMain { notas = notas.filter { it.id != id } } }
    fun importarNotas(lista: List<Nota>) {
        val ids = notas.map { it.id }.toSet()
        val nuevas = lista.filter { it.id !in ids }
        if (nuevas.isEmpty()) return
        db.transaccion { nuevas.forEach { db.poner("notas", it.id, it.fecha, JSON.encodeToString(Nota.serializer(), it)) } }
        enMain { notas = (notas + nuevas).sortedByDescending { it.creada } }
    }
    // ---------- Ideario ----------
    fun guardarIdea(i: Idea) {
        db.poner("ideas", i.id, i.fecha, JSON.encodeToString(Idea.serializer(), i))
        enMain { ideas = (ideas.filter { it.id != i.id } + i).sortedByDescending { it.creada } }
    }
    fun borrarIdea(id: String) { aPapelera("ideas", id); db.borrar("ideas", id); enMain { ideas = ideas.filter { it.id != id } } }
    fun importarIdeas(lista: List<Idea>) {
        val ids = ideas.map { it.id }.toSet()
        val nuevas = lista.filter { it.id !in ids }
        if (nuevas.isEmpty()) return
        db.transaccion { nuevas.forEach { db.poner("ideas", it.id, it.fecha, JSON.encodeToString(Idea.serializer(), it)) } }
        enMain { ideas = (ideas + nuevas).sortedByDescending { it.creada } }
    }
    /** Solo para las capturas de diseño: ideas de ejemplo en memoria. */
    fun ideasEnMemoria(l: List<Idea>) { ideas = l }

    // ---------- Listas libres ----------
    fun guardarLista(l: Lista) {
        db.poner("listas", l.id, l.creada.take(10), JSON.encodeToString(Lista.serializer(), l))
        enMain { listas = (listas.filter { it.id != l.id } + l).sortedWith(compareBy<Lista>({ it.orden }, { it.creada })) }
    }
    fun borrarLista(id: String) { aPapelera("listas", id); db.borrar("listas", id); enMain { listas = listas.filter { it.id != id } } }
    fun importarListas(lista: List<Lista>) {
        val ids = listas.map { it.id }.toSet()
        lista.filter { it.id !in ids }.forEach { guardarLista(it) }
    }
    /** Solo para las capturas de diseño: listas de ejemplo en memoria. */
    fun listasEnMemoria(l: List<Lista>) { listas = l }

    fun guardarMeta(m: Meta) {
        db.poner("metas", m.id, m.creada.take(10), JSON.encodeToString(Meta.serializer(), m))
        enMain { metas = (metas.filter { it.id != m.id } + m).sortedBy { it.creada } }
    }
    fun borrarMeta(id: String) { aPapelera("metas", id); db.borrar("metas", id); enMain { metas = metas.filter { it.id != id } } }
    fun importarMetas(lista: List<Meta>) {
        val ids = metas.map { it.id }.toSet()
        val nuevas = lista.filter { it.id !in ids }
        if (nuevas.isEmpty()) return
        db.transaccion { nuevas.forEach { db.poner("metas", it.id, it.creada.take(10), JSON.encodeToString(Meta.serializer(), it)) } }
        enMain { metas = (metas + nuevas).sortedBy { it.creada } }
    }
    /** Solo para las capturas de diseño. */
    fun notasEnMemoria(n: List<Nota>, m: List<Meta>) { notas = n; metas = m }

    // ---------- Suplementos y laptop ----------
    fun guardarToma(t: TomaSuplemento) {
        db.poner("tomas", t.id, t.fecha, JSON.encodeToString(TomaSuplemento.serializer(), t))
        enMain { tomas = (tomas.filter { it.id != t.id } + t).sortedBy { it.ts } }
    }
    fun borrarToma(id: String) { aPapelera("tomas", id); db.borrar("tomas", id); enMain { tomas = tomas.filter { it.id != id } } }
    fun importarTomas(lista: List<TomaSuplemento>) {
        val ids = tomas.map { it.id }.toSet()
        val nuevas = lista.filter { it.id !in ids }
        if (nuevas.isEmpty()) return
        db.transaccion { nuevas.forEach { db.poner("tomas", it.id, it.fecha, JSON.encodeToString(TomaSuplemento.serializer(), it)) } }
        enMain { tomas = (tomas + nuevas).sortedBy { it.ts } }
    }
    /** Reemplaza los días que trae la lista (reimportar el mismo día no duplica). */
    fun guardarLaptop(lista: List<LaptopDia>) {
        if (lista.isEmpty()) return
        db.transaccion { lista.forEach { db.poner("laptop", it.fecha, it.fecha, JSON.encodeToString(LaptopDia.serializer(), it)) } }
        enMain { laptop = laptop + lista.associateBy { it.fecha } }
    }
    /** Solo para las capturas de diseño. */
    fun lote3EnMemoria(t: List<TomaSuplemento>, l: Map<String, LaptopDia>) { tomas = t; laptop = l }

    // ---------- Gastos ----------
    fun guardarGasto(g: Gasto) {
        db.poner("gastos", g.id, g.fecha, JSON.encodeToString(Gasto.serializer(), g))
        enMain { gastos = (gastos.filter { it.id != g.id } + g).sortedBy { it.fecha + it.hora } }
    }
    fun borrarGasto(id: String) { aPapelera("gastos", id); db.borrar("gastos", id); enMain { gastos = gastos.filter { it.id != id } } }
    fun gasto(id: String): Gasto? = gastos.firstOrNull { it.id == id } ?: db.uno("gastos", id)?.let { JSON.decodeFromString(Gasto.serializer(), it) }
    /** Gastos de un respaldo o de la versión web: los que ya existen (mismo id) no se tocan, ni en la base ni en memoria. */
    fun importarGastos(lista: List<Gasto>) {
        val nuevos = Fusion.nuevosPorId(gastos, lista) { it.id }
        if (nuevos.isEmpty()) return
        db.transaccion { nuevos.forEach { db.poner("gastos", it.id, it.fecha, JSON.encodeToString(Gasto.serializer(), it)) } }
        enMain { gastos = (gastos + nuevos).sortedBy { it.fecha + it.hora } }
    }

    // ---------- Ingresos ----------
    fun guardarIngreso(i: Ingreso) {
        db.poner("ingresos", i.id, i.fecha, JSON.encodeToString(Ingreso.serializer(), i))
        enMain { ingresos = (ingresos.filter { it.id != i.id } + i).sortedBy { it.fecha + it.hora } }
    }
    fun borrarIngreso(id: String) { aPapelera("ingresos", id); db.borrar("ingresos", id); enMain { ingresos = ingresos.filter { it.id != id } } }
    fun importarIngresos(lista: List<Ingreso>) {
        val nuevos = Fusion.nuevosPorId(ingresos, lista) { it.id }
        if (nuevos.isEmpty()) return
        db.transaccion { nuevos.forEach { db.poner("ingresos", it.id, it.fecha, JSON.encodeToString(Ingreso.serializer(), it)) } }
        enMain { ingresos = (ingresos + nuevos).sortedBy { it.fecha + it.hora } }
    }

    // ---------- Escalas y reacción ----------
    fun guardarRespuesta(r: Respuesta) {
        db.poner("respuestas", r.id, r.fecha, JSON.encodeToString(Respuesta.serializer(), r))
        enMain { respuestas = (respuestas.filter { it.id != r.id } + r).sortedBy { it.ts } }
    }
    fun guardarReaccion(r: Reaccion) {
        db.poner("reacciones", r.id, r.fecha, JSON.encodeToString(Reaccion.serializer(), r))
        enMain { reacciones = (reacciones + r).sortedBy { it.ts } }
    }
    fun importarRespuestas(lista: List<Respuesta>) {
        val ids = respuestas.map { it.id }.toHashSet()
        val nuevas = lista.filter { it.id !in ids }.distinctBy { it.id }
        if (nuevas.isEmpty()) return
        db.transaccion { nuevas.forEach { db.poner("respuestas", it.id, it.fecha, JSON.encodeToString(Respuesta.serializer(), it)) } }
        enMain { val ya = respuestas.map { it.id }.toHashSet(); respuestas = (respuestas + nuevas.filter { it.id !in ya }).sortedBy { it.ts } }
    }
    fun importarReacciones(lista: List<Reaccion>) {
        val ids = reacciones.map { it.id }.toHashSet()
        val nuevas = lista.filter { it.id !in ids }.distinctBy { it.id }
        if (nuevas.isEmpty()) return
        db.transaccion { nuevas.forEach { db.poner("reacciones", it.id, it.fecha, JSON.encodeToString(Reaccion.serializer(), it)) } }
        enMain { val ya = reacciones.map { it.id }.toHashSet(); reacciones = (reacciones + nuevas.filter { it.id !in ya }).sortedBy { it.ts } }
    }
    fun ultimaRespuesta(instr: String): Respuesta? = respuestas.lastOrNull { it.instrumento == instr }

    // ---------- Lugares y visitas ----------
    fun guardarLugar(l: Lugar) {
        db.poner("lugares", l.id, null, JSON.encodeToString(Lugar.serializer(), l))
        enMain { lugares = lugares.filter { it.id != l.id } + l }
    }
    fun borrarLugar(id: String) { aPapelera("lugares", id); db.borrar("lugares", id); enMain { lugares = lugares.filter { it.id != id } } }

    /** Borra un lugar y decide qué pasa con sus visitas: pasarlas a [pasarA] o, con null, quitarlas (N-05). */
    fun borrarLugar(id: String, pasarA: String?) {
        val suyas = visitas.filter { it.lugar == id }
        db.transaccion {
            suyas.forEach { v ->
                if (pasarA == null) db.borrar("visitas", v.id)
                else db.poner("visitas", v.id, v.entrada.take(10), JSON.encodeToString(Visita.serializer(), v.copy(lugar = pasarA)))
            }
        }
        enMain { visitas = Lugares.reasignar(visitas, id, pasarA) }
        borrarLugar(id)
    }

    /** Corrige una visita (por ejemplo, «fue en…» otro lugar). */
    fun guardarVisita(v: Visita) {
        db.poner("visitas", v.id, v.entrada.take(10), JSON.encodeToString(Visita.serializer(), v))
        enMain { visitas = (visitas.filter { it.id != v.id } + v).sortedBy { it.entrada } }
    }

    /** «No estuve aquí»: quita una visita. */
    fun borrarVisita(id: String) { db.borrar("visitas", id); enMain { visitas = visitas.filter { it.id != id } } }

    /** Guarda y quita varias visitas de una vez (en una transacción) y las publica juntas. */
    @Synchronized
    fun cambiarVisitas(guardar: List<Visita>, quitar: Set<String> = emptySet()) {
        if (guardar.isEmpty() && quitar.isEmpty()) return
        db.transaccion {
            quitar.forEach { db.borrar("visitas", it) }
            guardar.forEach { db.poner("visitas", it.id, it.entrada.take(10), JSON.encodeToString(Visita.serializer(), it)) }
        }
        val ids = guardar.map { it.id }.toSet() + quitar
        enMain { visitas = (visitas.filter { it.id !in ids } + guardar).sortedBy { it.entrada } }
    }

    /**
     * Android avisa que entraste a un lugar. Si seguía abierta la visita de otro lugar lejano (Android no avisó que saliste),
     * se cierra: a la hora en que tus ubicaciones ya te ponían fuera o, si no hay, a la de esta entrada (esquema 19).
     * Devuelve si cerró alguna: entonces hay que volver a registrar las geocercas (Android cree que sigues allá y no
     * avisaría cuando vuelvas).
     */
    @Synchronized
    fun entrada(lugarId: String, ts: String): Boolean {
        if (visitas.any { it.lugar == lugarId && it.salida == null }) return false
        val aqui = lugares.firstOrNull { it.id == lugarId }
        val t = Fechas.millis(ts)
        val cerrar = ArrayList<Visita>(); val quitar = HashSet<String>()
        if (aqui != null && t > 0) visitas.filter { it.salida == null && it.lugar != lugarId }.forEach { v ->
            val alla = lugares.firstOrNull { it.id == v.lugar } ?: return@forEach
            if (!Lugares.lejos(aqui, alla)) return@forEach
            val a = Fechas.millis(v.entrada)
            val (fin, como) = Lugares.salidaPorUbicaciones(alla, a, t)?.let { it to Lugares.CIERRE_UBICACIONES } ?: (t to Lugares.CIERRE_OTRO_LUGAR)
            if (fin - a < Lugares.VISITA_MINIMA_MS) quitar += v.id
            else cerrar += v.copy(salida = if (fin == t) ts else Fechas.isoDeMillis(fin), cierre = como)
        }
        cambiarVisitas(cerrar + Visita(nuevoId(), lugarId, ts), quitar)
        return cerrar.isNotEmpty() || quitar.isNotEmpty()
    }

    /**
     * Android avisa que saliste. Si tus ubicaciones dicen que saliste bastante antes (un aviso tardío), vale esa hora. Una
     * visita de menos de 8 minutos se quita (pasar cerca o un brinco del GPS) y, si al empezar cerró la visita del lugar
     * donde en realidad seguías, esa se vuelve a abrir.
     */
    @Synchronized
    fun salida(lugarId: String, ts: String) {
        val abierta = visitas.lastOrNull { it.lugar == lugarId && it.salida == null } ?: return
        val a = Fechas.millis(abierta.entrada); val t = Fechas.millis(ts)
        if (a <= 0 || t < a) return
        val (fin, como) = Lugares.salidaAvisada(abierta, t)
        if (fin - a < Lugares.VISITA_MINIMA_MS) {
            val reabrir = visitas.filter { w ->
                w.cierre == Lugares.CIERRE_OTRO_LUGAR && w.salida?.let { Fechas.millis(it) } == a &&
                    visitas.none { it.lugar == w.lugar && it.salida == null }
            }.map { it.copy(salida = null, cierre = null) }
            cambiarVisitas(reabrir, setOf(abierta.id))
            return
        }
        cambiarVisitas(listOf(abierta.copy(salida = if (fin == t) ts else Fechas.isoDeMillis(fin), cierre = como)))
    }

    /** Eventos de YouTube (Takeout). Si llega una lista de suscripciones, reemplaza la anterior (te pudiste desuscribir). */
    fun importarYoutube(lista: List<YtEvento>, reemplazarSuscripciones: Boolean) {
        val viejasSubs = if (reemplazarSuscripciones) youtube.filter { it.tipo == "suscripcion" }.map { it.id }.toSet() - lista.map { it.id }.toSet() else emptySet()
        db.transaccion {
            viejasSubs.forEach { db.borrar("youtube", it) }
            lista.forEach { db.poner("youtube", it.id, it.fecha.ifBlank { null }, JSON.encodeToString(YtEvento.serializer(), it)) }
        }
        val ids = lista.map { it.id }.toSet()
        val n = (youtube.filter { it.id !in ids && it.id !in viejasSubs } + lista).sortedBy { it.ts }
        enMain { youtube = n }
    }

    /** Visitas reconstruidas de tus ubicaciones al guardar un lugar nuevo. */
    fun agregarVisitas(lista: List<Visita>) = importarVisitas(lista)

    /** Visitas de un respaldo: las que ya existen (mismo id) no se tocan. */
    fun importarVisitas(lista: List<Visita>) {
        val ya = visitas.map { it.id }.toSet()
        val nuevas = lista.filter { it.id !in ya }
        if (nuevas.isEmpty()) return
        db.transaccion { nuevas.forEach { db.poner("visitas", it.id, it.entrada.take(10), JSON.encodeToString(Visita.serializer(), it)) } }
        enMain { visitas = (visitas + nuevas).sortedBy { it.entrada } }
    }

    fun lugarCercano(lat: Double, lon: Double): Lugar? =
        lugares.map { it to Geo.distanciaM(lat, lon, it.lat, it.lon) }.filter { it.second <= it.first.radio_m }.minByOrNull { it.second }?.first

    // ---------- Sensores ----------
    fun guardarSalud(s: SaludDia) {
        db.poner("salud", s.fecha, s.fecha, JSON.encodeToString(SaludDia.serializer(), s))
        enMain { salud = salud + (s.fecha to s) }
    }
    /** Varios días de salud de una vez (al restaurar): una transacción y un solo cambio en memoria. */
    fun importarSalud(lista: List<SaludDia>) {
        if (lista.isEmpty()) return
        db.transaccion { lista.forEach { db.poner("salud", it.fecha, it.fecha, JSON.encodeToString(SaludDia.serializer(), it)) } }
        enMain { salud = salud + lista.associateBy { it.fecha } }
    }
    fun guardarPantalla(p: PantallaDia) {
        db.poner("pantalla", p.fecha, p.fecha, JSON.encodeToString(PantallaDia.serializer(), p))
        enMain { pantalla = pantalla + (p.fecha to p) }
    }

    // ---------- Cartas y experimentos ----------
    fun guardarCarta(c: Carta) {
        db.poner("cartas", c.id, c.abrir_en, JSON.encodeToString(Carta.serializer(), c))
        enMain { cartas = cartas.filter { it.id != c.id } + c }
    }
    fun borrarCarta(id: String) { aPapelera("cartas", id); db.borrar("cartas", id); enMain { cartas = cartas.filter { it.id != id } } }
    fun guardarExperimento(e: Experimento) {
        db.poner("experimentos", e.id, e.inicio, JSON.encodeToString(Experimento.serializer(), e))
        enMain { experimentos = experimentos.filter { it.id != e.id } + e }
    }

    // ---------- Entorno y fuentes automáticas ----------
    fun guardarEntorno(e: EntornoDia) {
        db.poner("entorno", e.fecha, e.fecha, JSON.encodeToString(EntornoDia.serializer(), e))
        enMain { entorno = entorno + (e.fecha to e) }
    }
    fun guardarFotos(f: FotosDia) {
        db.poner("fotos", f.fecha, f.fecha, JSON.encodeToString(FotosDia.serializer(), f))
        enMain { fotos = fotos + (f.fecha to f) }
    }
    fun guardarAgenda(a: AgendaDia) {
        db.poner("agenda", a.fecha, a.fecha, JSON.encodeToString(AgendaDia.serializer(), a))
        enMain { agenda = agenda + (a.fecha to a) }
    }
    fun guardarSesion(s: Sesion) {
        db.poner("sesiones", s.id, s.fecha, JSON.encodeToString(Sesion.serializer(), s))
        enMain { sesiones = (sesiones.filter { it.id != s.id } + s).sortedBy { it.inicio } }
    }

    private val notifMem = HashMap<String, NotifDia>()
    /** Suma una notificación al día. Se llama desde el servicio que escucha notificaciones. */
    @Synchronized
    fun contarNotif(fecha: String, app: String, hora: Int) {
        val previo = notifMem[fecha] ?: notifs[fecha] ?: NotifDia(fecha)
        val horas = previo.por_hora.toMutableList().also { while (it.size < 24) it.add(0) }
        horas[hora.coerceIn(0, 23)]++
        val n = previo.copy(total = previo.total + 1, por_app = previo.por_app + (app to ((previo.por_app[app] ?: 0) + 1)), por_hora = horas)
        notifMem[fecha] = n
        db.poner("notifs", fecha, fecha, JSON.encodeToString(NotifDia.serializer(), n))
        enMain { notifs = notifs + (fecha to n) }
    }

    private val avisosMem = HashMap<String, AvisosDia>()
    /** Cambia el registro de avisos de ánimo del día y lo guarda. Se llama desde el trabajo de fondo. */
    @Synchronized
    fun anotarAvisos(fecha: String, f: (AvisosDia) -> AvisosDia) {
        val previo = avisosMem[fecha] ?: avisos[fecha] ?: AvisosDia(fecha)
        val n = f(previo)
        if (n == previo && fecha in avisos) return
        avisosMem[fecha] = n
        db.poner("avisos", fecha, fecha, JSON.encodeToString(AvisosDia.serializer(), n))
        enMain { avisos = avisos + (fecha to n) }
    }
    /** Avisos de un respaldo: se juntan con lo de aquí sin perder ningún aviso de ninguno de los dos. */
    @Synchronized
    fun importarAvisos(lista: List<AvisosDia>) {
        if (lista.isEmpty()) return
        val nuevos = lista.mapNotNull { r ->
            val a = avisosMem[r.fecha] ?: avisos[r.fecha] ?: return@mapNotNull r
            val n = a.copy(planeados = maxOf(a.planeados, r.planeados), vencidos = (a.vencidos + r.vencidos).distinct().sorted(), mostrados = (a.mostrados + r.mostrados).distinct().sorted(),
                reales = r.reales + a.reales)
            n.takeIf { it != a }
        }
        if (nuevos.isEmpty()) return
        db.transaccion { nuevos.forEach { db.poner("avisos", it.fecha, it.fecha, JSON.encodeToString(AvisosDia.serializer(), it)) } }
        // La copia de trabajo de la tarea de fondo también: si no, el siguiente aviso del día escribiría la de antes y
        // borraría lo que trajo el respaldo.
        nuevos.forEach { avisosMem[it.fecha] = it }
        enMain { avisos = avisos + nuevos.associateBy { it.fecha } }
    }
    /** Solo para las capturas de diseño. */
    fun avisosEnMemoria(a: Map<String, AvisosDia>) { avisos = a }

    private val actMem = HashMap<String, ActividadDia>()
    @Synchronized
    fun sumarActividad(fecha: String, tipo: String, minutos: Int) {
        if (minutos <= 0) return
        val previo = actMem[fecha] ?: actividad[fecha] ?: ActividadDia(fecha)
        val n = previo.copy(minutos = previo.minutos + (tipo to ((previo.minutos[tipo] ?: 0) + minutos)))
        actMem[fecha] = n
        db.poner("actividad", fecha, fecha, JSON.encodeToString(ActividadDia.serializer(), n))
        enMain { actividad = actividad + (fecha to n) }
    }

    fun importarSensores(n: List<NotifDia>, a: List<ActividadDia>) {
        db.transaccion {
            n.filter { it.fecha !in notifs }.forEach { db.poner("notifs", it.fecha, it.fecha, JSON.encodeToString(NotifDia.serializer(), it)) }
            a.filter { it.fecha !in actividad }.forEach { db.poner("actividad", it.fecha, it.fecha, JSON.encodeToString(ActividadDia.serializer(), it)) }
        }
        enMain {
            notifs = n.filter { it.fecha !in notifs }.associateBy { it.fecha } + notifs
            actividad = a.filter { it.fecha !in actividad }.associateBy { it.fecha } + actividad
        }
    }

    fun guardarEscucha(e: Escucha) = db.poner("escuchas", e.id, e.fecha, JSON.encodeToString(Escucha.serializer(), e))
    /** Lo escuchado entre dos fechas. Por defecto solo música: los videos de YouTube o del navegador quedan fuera (sus canales no son artistas). */
    fun escuchas(desde: String, hasta: String, todas: Boolean = false): List<Escucha> =
        leer("escuchas", db.porFecha("escuchas", desde, hasta), Escucha.serializer())
            .filter { todas || it.esMusica }.sortedBy { it.ts }

    fun guardarComunicacion(c: ComunicacionDia) {
        db.poner("comunicacion", c.fecha, c.fecha, JSON.encodeToString(ComunicacionDia.serializer(), c))
        enMain { comunicacion = comunicacion + (c.fecha to c) }
    }

    private val conMem = HashMap<String, ConexionDia>()
    /** Modifica la conexión del día y la guarda. Se llama desde el trabajo de fondo. */
    @Synchronized
    fun cambiarConexion(fecha: String, f: (ConexionDia) -> ConexionDia) {
        val n = f(conMem[fecha] ?: conexiones[fecha] ?: ConexionDia(fecha))
        conMem[fecha] = n
        db.poner("conexiones", fecha, fecha, JSON.encodeToString(ConexionDia.serializer(), n))
        enMain { conexiones = conexiones + (fecha to n) }
    }

    fun importarEscuchas(lista: List<Escucha>) {
        db.transaccion { lista.forEach { db.poner("escuchas", it.id, it.fecha, JSON.encodeToString(Escucha.serializer(), it)) } }
    }
    fun borrarEscuchas(ids: Collection<String>) { db.transaccion { ids.forEach { db.borrar("escuchas", it) } } }

    fun importarSubrayados(lista: List<Subrayado>) {
        db.transaccion { lista.forEach { db.poner("subrayados", it.id, it.fecha, JSON.encodeToString(Subrayado.serializer(), it)) } }
        val ids = lista.map { it.id }.toSet()
        enMain { subrayados = (subrayados.filter { it.id !in ids } + lista).sortedBy { it.ts ?: "" } }
    }

    fun importarHuellas(lista: List<Huella>) {
        db.transaccion { lista.forEach { db.poner("huellas", it.id, it.fecha, JSON.encodeToString(Huella.serializer(), it)) } }
    }
    fun huellasDe(desde: String, hasta: String): List<Huella> =
        leer("huellas", db.porFecha("huellas", desde, hasta), Huella.serializer())
    fun guardarHuellasDia(lista: List<HuellasDia>) {
        db.transaccion { lista.forEach { db.poner("huellas_dia", it.fecha, it.fecha, JSON.encodeToString(HuellasDia.serializer(), it)) } }
        enMain { huellas = huellas + lista.associateBy { it.fecha } }
    }

    fun importarChats(lista: List<ChatDia>) {
        db.transaccion { lista.forEach { db.poner("whatsapp", it.id, it.fecha, JSON.encodeToString(ChatDia.serializer(), it)) } }
        val ids = lista.map { it.id }.toSet()
        enMain { chats = (chats.filter { it.id !in ids } + lista).sortedBy { it.fecha } }
    }

    /** Reemplaza todo lo de MacroFactor entre dos fechas con lo recién importado (así reimportar no duplica). */
    fun importarMacrofactor(desde: String, hasta: String, dias: List<NutricionDia>, comidas: List<Comida>, series: List<Serie>, entr: List<EntrenoDia>) {
        db.transaccion {
            db.borrarRango("comidas", desde, hasta); db.borrarRango("series", desde, hasta)
            dias.forEach { db.poner("nutricion", it.fecha, it.fecha, JSON.encodeToString(NutricionDia.serializer(), it)) }
            comidas.forEach { db.poner("comidas", it.id, it.fecha, JSON.encodeToString(Comida.serializer(), it)) }
            series.forEach { db.poner("series", it.id, it.fecha, JSON.encodeToString(Serie.serializer(), it)) }
            entr.forEach { db.poner("entrenos", it.fecha, it.fecha, JSON.encodeToString(EntrenoDia.serializer(), it)) }
        }
        enMain { nutricion = nutricion + dias.associateBy { it.fecha }; entrenos = entrenos + entr.associateBy { it.fecha } }
    }
    fun comidas(desde: String, hasta: String): List<Comida> =
        leer("comidas", db.porFecha("comidas", desde, hasta), Comida.serializer()).sortedWith(compareBy({ it.fecha }, { it.hora }))
    fun series(desde: String, hasta: String): List<Serie> =
        leer("series", db.porFecha("series", desde, hasta), Serie.serializer()).sortedWith(compareBy({ it.fecha }, { it.orden }))

    /** Calcula la fuerza de referencia de los entrenos importados antes del esquema 15 (H-7-14). Devuelve cuántos cambió. */
    fun completarFuerzaReferencia(base: Collection<EntrenoDia> = entrenos.values): Int {
        val porDia = series("0000-01-01", "9999-12-31").groupBy { it.fecha }
        val nuevos = base.mapNotNull { e ->
            val r = Macrofactor.fuerzaReferencia(porDia[e.fecha].orEmpty())
            if (r != e.fuerza_ref_kg) e.copy(fuerza_ref_kg = r) else null
        }
        nuevos.forEach { db.poner("entrenos", it.fecha, it.fecha, JSON.encodeToString(EntrenoDia.serializer(), it)) }
        if (nuevos.isNotEmpty()) enMain { entrenos = entrenos + nuevos.associateBy { it.fecha } }
        return nuevos.size
    }

    /**
     * Una sola vez: rehace la primera y la última comida (y la última cafeína) de lo ya importado de MacroFactor con
     * [Macrofactor.horario] (lo de antes de las 05:00 va al final). Devuelve cuántos días cambió.
     */
    fun rehacerHorarioComidas(): Int {
        val desde = nutricion.keys.minOrNull() ?: return 0; val hasta = nutricion.keys.max()
        val porDia = comidas(desde, hasta).groupBy { it.fecha }
        val nuevos = nutricion.values.mapNotNull { n ->
            val l = porDia[n.fecha] ?: return@mapNotNull null
            val (p, u) = Macrofactor.horario(l.map { it.hora })
            val caf = Macrofactor.horario(l.filter { (it.valores["cafeina_mg"] ?: 0.0) > 0 }.map { it.hora }).second
            n.copy(primera = p, ultima = u, cafeina_ultima = caf).takeIf { it != n }
        }
        if (nuevos.isEmpty()) return 0
        db.transaccion { nuevos.forEach { db.poner("nutricion", it.fecha, it.fecha, JSON.encodeToString(NutricionDia.serializer(), it)) } }
        enMain { nutricion = nutricion + nuevos.associateBy { it.fecha } }
        return nuevos.size
    }

    // ---------- Escalada y biblioteca ----------
    fun guardarAscenso(a: Ascenso) {
        db.poner("ascensos", a.id, a.fecha, JSON.encodeToString(Ascenso.serializer(), a))
        enMain { ascensos = (ascensos.filter { it.id != a.id } + a).sortedBy { it.fecha + (it.inicio ?: "") } }
    }
    fun borrarAscenso(id: String) { aPapelera("ascensos", id); db.borrar("ascensos", id); enMain { ascensos = ascensos.filter { it.id != id } } }
    fun guardarObra(o: Obra) {
        db.poner("obras", o.id, o.fin ?: o.inicio ?: o.agregado, JSON.encodeToString(Obra.serializer(), o))
        enMain { obras = obras.filter { it.id != o.id } + o }
    }
    fun borrarObra(id: String) { aPapelera("obras", id); db.borrar("obras", id); enMain { obras = obras.filter { it.id != id } } }

    // ---------- Papelera (v14) ----------
    /** Lo que se guarda 7 días al borrarlo: los registros que haces tú (no lo que llega solo ni lo que se reimporta). */
    private val EN_PAPELERA = setOf("momentos", "tramos", "consumos", "notas", "metas", "tomas", "gastos", "ingresos", "lugares", "cartas", "ascensos", "obras", "ideas", "listas")

    private fun aPapelera(col: String, id: String) {
        if (col !in EN_PAPELERA) return
        val json = db.uno(col, id) ?: return
        val b = Borrado(col, id, json, Fechas.ahoraIso())
        runCatching { db.poner("papelera", "$col:$id", Fechas.diaLogico(), JSON.encodeToString(Borrado.serializer(), b)) }
    }

    /** Lo que está en la papelera y no se ha recuperado (lo que volvió con «deshacer» ya no cuenta), lo más reciente primero. */
    fun papelera(): List<Borrado> = db.todos("papelera").mapNotNull { runCatching { JSON.decodeFromString(Borrado.serializer(), it) }.getOrNull() }
        .filter { db.uno(it.col, it.id) == null }.sortedByDescending { it.borrado }

    /** Devuelve algo de la papelera a su lugar. false si no se pudo leer. */
    fun restaurar(b: Borrado): Boolean {
        val ok = runCatching {
            when (b.col) {
                "momentos" -> guardarMomento(JSON.decodeFromString(Momento.serializer(), b.json))
                "tramos" -> guardarTramo(JSON.decodeFromString(Tramo.serializer(), b.json))
                "consumos" -> guardarConsumo(JSON.decodeFromString(Consumo.serializer(), b.json))
                "notas" -> guardarNota(JSON.decodeFromString(Nota.serializer(), b.json))
                "metas" -> guardarMeta(JSON.decodeFromString(Meta.serializer(), b.json))
                "tomas" -> guardarToma(JSON.decodeFromString(TomaSuplemento.serializer(), b.json))
                "gastos" -> guardarGasto(JSON.decodeFromString(Gasto.serializer(), b.json))
                "ingresos" -> guardarIngreso(JSON.decodeFromString(Ingreso.serializer(), b.json))
                "lugares" -> guardarLugar(JSON.decodeFromString(Lugar.serializer(), b.json))
                "cartas" -> guardarCarta(JSON.decodeFromString(Carta.serializer(), b.json))
                "ascensos" -> guardarAscenso(JSON.decodeFromString(Ascenso.serializer(), b.json))
                "obras" -> guardarObra(JSON.decodeFromString(Obra.serializer(), b.json))
                "ideas" -> guardarIdea(JSON.decodeFromString(Idea.serializer(), b.json))
                "listas" -> guardarLista(JSON.decodeFromString(Lista.serializer(), b.json))
                else -> error("colección sin papelera")
            }
        }.isSuccess
        if (ok) db.borrar("papelera", "${b.col}:${b.id}")
        return ok
    }

    /** Vacía lo que lleva más de [dias] días en la papelera. */
    fun limpiarPapelera(dias: Long = 7) { runCatching { db.borrarRango("papelera", "0000-01-01", Fechas.sumar(Fechas.diaLogico(), -dias)) } }

    // ---------- Configuración ----------
    @Synchronized
    /** Solo para las capturas de diseño: cambia la configuración en memoria, sin base de datos. */
    fun configEnMemoria(c: Config) { config = c; Fechas.usarHistorial(c.zonas) }

    /** Solo para las capturas de diseño: obras de ejemplo en memoria. */
    fun obrasEnMemoria(l: List<Obra>) { obras = l }
    /** Para las capturas de diseño: gastos e ingresos inventados en memoria, sin tocar la base. */
    fun gastosEnMemoria(g: List<Gasto>, i: List<Ingreso>) { gastos = g; ingresos = i }

    /** Solo para las capturas de diseño: días de ejemplo en memoria. */
    fun diasEnMemoria(d: Map<String, Dia>) { dias = d }

    /** Solo para las capturas de diseño: cartas de ejemplo en memoria. */
    fun cartasEnMemoria(l: List<Carta>) { cartas = l }
    /** Para las capturas de diseño: nutrición, entrenos y chats sin base de datos. */
    fun nutricionEnMemoria(n: Map<String, NutricionDia>, e: Map<String, EntrenoDia> = entrenos, c: List<ChatDia> = chats, s: List<Subrayado> = subrayados) { nutricion = n; entrenos = e; chats = c; subrayados = s }

    /** Solo para las capturas de diseño: datos de ejemplo en memoria. */
    fun ejemploEnMemoria(dias: Map<String, Dia>, salud: Map<String, SaludDia>, pantalla: Map<String, PantallaDia>, gastos: List<Gasto>,
                         momentos: List<Momento>, notifs: Map<String, NotifDia>, entorno: Map<String, EntornoDia>, nutricion: Map<String, NutricionDia>,
                         lugares: List<Lugar>, visitas: List<Visita>, sesiones: List<Sesion>, ingresos: List<Ingreso> = emptyList()) {
        this.ingresos = ingresos
        this.dias = dias; this.salud = salud; this.pantalla = pantalla; this.gastos = gastos; this.momentos = momentos
        this.notifs = notifs; this.entorno = entorno; this.nutricion = nutricion; this.lugares = lugares; this.visitas = visitas; this.sesiones = sesiones
    }

    /** La última configuración escrita: la publicación en el hilo principal no debe volver a una anterior. */
    @Volatile private var configUltima: Config? = null

    /**
     * Lee, cambia y guarda la configuración de una sola vez. Sincronizado: la tarea de cada 15 min (marcas de avisos,
     * sincronizaciones) y la interfaz (tu gente, hábitos, ajustes) la cambian a la vez, y sin candado el que escribía al
     * final borraba el cambio del otro.
     */
    @Synchronized
    fun cambiarConfig(f: (Config) -> Config) {
        val n = f(config)
        db.poner("config", "config", null, JSON.encodeToString(Config.serializer(), n))
        config = n
        configUltima = n
        Fechas.usarHistorial(n.zonas)
        enMain { if (configUltima === n) config = n }
    }

    fun equivalencias(): Map<String, String> = config.equivalencias
}

object Geo {
    fun distanciaM(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2).let { it * it } + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2).let { it * it }
        return 2 * r * Math.asin(Math.sqrt(a))
    }
}
