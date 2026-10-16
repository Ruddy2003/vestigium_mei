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
    var respuestas by mutableStateOf<List<Respuesta>>(emptyList()); private set
    var reacciones by mutableStateOf<List<Reaccion>>(emptyList()); private set
    var lugares by mutableStateOf<List<Lugar>>(emptyList()); private set
    var visitas by mutableStateOf<List<Visita>>(emptyList()); private set
    var salud by mutableStateOf<Map<String, SaludDia>>(emptyMap()); private set
    var pantalla by mutableStateOf<Map<String, PantallaDia>>(emptyMap()); private set
    var cartas by mutableStateOf<List<Carta>>(emptyList()); private set
    var experimentos by mutableStateOf<List<Experimento>>(emptyList()); private set
    var config by mutableStateOf(Config()); private set

    @Synchronized
    fun init(ctx: Context) {
        if (listo) return
        db = Db(ctx.applicationContext)
        dias = cargar("dias", Dia.serializer()).associateBy { it.fecha }
        momentos = cargar("momentos", Momento.serializer()).sortedBy { it.ts }
        gastos = cargar("gastos", Gasto.serializer()).sortedBy { it.fecha + it.hora }
        respuestas = cargar("respuestas", Respuesta.serializer()).sortedBy { it.ts }
        reacciones = cargar("reacciones", Reaccion.serializer()).sortedBy { it.ts }
        lugares = cargar("lugares", Lugar.serializer())
        visitas = cargar("visitas", Visita.serializer()).sortedBy { it.entrada }
        salud = cargar("salud", SaludDia.serializer()).associateBy { it.fecha }
        pantalla = cargar("pantalla", PantallaDia.serializer()).associateBy { it.fecha }
        cartas = cargar("cartas", Carta.serializer())
        experimentos = cargar("experimentos", Experimento.serializer())
        config = db.uno("config", "config")?.let { runCatching { JSON.decodeFromString(Config.serializer(), it) }.getOrNull() } ?: Config()
        listo = true
    }

    fun db(): Db = db

    private fun <T> cargar(col: String, s: KSerializer<T>): List<T> =
        db.todos(col).mapNotNull { runCatching { JSON.decodeFromString(s, it) }.getOrNull() }

    private fun enMain(b: () -> Unit) { if (Looper.myLooper() == Looper.getMainLooper()) b() else main.post(b) }

    fun nuevoId(): String = UUID.randomUUID().toString().replace("-", "").take(16)

    // ---------- Días ----------
    fun dia(fecha: String): Dia = dias[fecha] ?: Dia(fecha = fecha)

    @Synchronized
    fun guardarDia(d: Dia) {
        val ahora = Fechas.ahoraIso()
        val previo = dias[d.fecha]
        val retro = previo?.retroactivo ?: (Fechas.parse(d.fecha).isBefore(Fechas.parse(Fechas.diaLogico())))
        val final = d.copy(schema_v = SCHEMA_V, ts_creado = d.ts_creado ?: previo?.ts_creado ?: ahora, ts_editado = ahora, retroactivo = retro)
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
        val nuevos = dias.toMutableMap()
        lista.forEach { if (sobrescribir || !dias.containsKey(it.fecha)) nuevos[it.fecha] = it }
        enMain { dias = nuevos }
        return n
    }

    // ---------- Momentos ----------
    @Synchronized
    fun guardarMomento(m: Momento) {
        db.poner("momentos", m.id, m.fecha, JSON.encodeToString(Momento.serializer(), m))
        enMain { momentos = (momentos.filter { it.id != m.id } + m).sortedBy { it.ts } }
    }
    fun momento(id: String): Momento? = momentos.firstOrNull { it.id == id } ?: db.uno("momentos", id)?.let { JSON.decodeFromString(Momento.serializer(), it) }
    fun borrarMomento(id: String) { db.borrar("momentos", id); enMain { momentos = momentos.filter { it.id != id } } }

    // ---------- Gastos ----------
    fun guardarGasto(g: Gasto) {
        db.poner("gastos", g.id, g.fecha, JSON.encodeToString(Gasto.serializer(), g))
        enMain { gastos = (gastos.filter { it.id != g.id } + g).sortedBy { it.fecha + it.hora } }
    }
    fun borrarGasto(id: String) { db.borrar("gastos", id); enMain { gastos = gastos.filter { it.id != id } } }
    fun importarGastos(lista: List<Gasto>) {
        db.transaccion { lista.forEach { db.poner("gastos", it.id, it.fecha, JSON.encodeToString(Gasto.serializer(), it)) } }
        val ids = gastos.map { it.id }.toSet()
        enMain { gastos = (gastos + lista.filter { it.id !in ids }).sortedBy { it.fecha + it.hora } }
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
    fun ultimaRespuesta(instr: String): Respuesta? = respuestas.lastOrNull { it.instrumento == instr }

    // ---------- Lugares y visitas ----------
    fun guardarLugar(l: Lugar) {
        db.poner("lugares", l.id, null, JSON.encodeToString(Lugar.serializer(), l))
        enMain { lugares = lugares.filter { it.id != l.id } + l }
    }
    fun borrarLugar(id: String) { db.borrar("lugares", id); enMain { lugares = lugares.filter { it.id != id } } }

    @Synchronized
    fun entrada(lugarId: String, ts: String) {
        if (visitas.any { it.lugar == lugarId && it.salida == null }) return
        val v = Visita(nuevoId(), lugarId, ts)
        db.poner("visitas", v.id, ts.take(10), JSON.encodeToString(Visita.serializer(), v))
        enMain { visitas = visitas + v }
    }
    @Synchronized
    fun salida(lugarId: String, ts: String) {
        val abierta = visitas.lastOrNull { it.lugar == lugarId && it.salida == null } ?: return
        val v = abierta.copy(salida = ts)
        db.poner("visitas", v.id, v.entrada.take(10), JSON.encodeToString(Visita.serializer(), v))
        enMain { visitas = visitas.map { if (it.id == v.id) v else it } }
    }

    fun lugarCercano(lat: Double, lon: Double): Lugar? =
        lugares.map { it to Geo.distanciaM(lat, lon, it.lat, it.lon) }.filter { it.second <= it.first.radio_m }.minByOrNull { it.second }?.first

    // ---------- Sensores ----------
    fun guardarSalud(s: SaludDia) {
        db.poner("salud", s.fecha, s.fecha, JSON.encodeToString(SaludDia.serializer(), s))
        enMain { salud = salud + (s.fecha to s) }
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
    fun guardarExperimento(e: Experimento) {
        db.poner("experimentos", e.id, e.inicio, JSON.encodeToString(Experimento.serializer(), e))
        enMain { experimentos = experimentos.filter { it.id != e.id } + e }
    }

    // ---------- Configuración ----------
    @Synchronized
    fun cambiarConfig(f: (Config) -> Config) {
        val n = f(config)
        db.poner("config", "config", null, JSON.encodeToString(Config.serializer(), n))
        config = n
        enMain { config = n }
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
