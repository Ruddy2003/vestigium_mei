@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Gasto
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.sensores.Ubicacion
import io.github.ruddy2003.vestigium.widget.Widgets
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Captura instantánea, sin abrir la app completa. Se abre desde widgets, el panel de ajustes rápidos,
 * los accesos directos del ícono y las notificaciones. Lo primero que hace es guardar; los detalles son opcionales.
 */
class QuickActivity : FragmentActivity() {
    companion object {
        const val MODO = "modo"; const val TIPO = "tipo"; const val VALENCIA = "valencia"
        const val ORIGEN = "origen"; const val AVISO_MS = "aviso_ms"; const val NOTIF = "notif"
        const val M_ANIMO = "animo"; const val M_MOMENTO = "momento"; const val M_MENU = "menu"
        const val M_GASTO = "gasto"; const val M_SUENO = "sueno"; const val M_MINIMO = "minimo"; const val M_ENFOQUE = "enfoque"; const val M_SIESTA = "siesta"
        /** Sustancias: con [CONSUMO] abre ese registro para completarlo (lo de anoche); con [SUSTANCIA], esa sustancia. */
        const val M_CONSUMO = "consumo"; const val CONSUMO = "consumo_id"; const val SUSTANCIA = "sustancia"
        const val CATEGORIA = "categoria"; const val CALIDAD = "calidad"; const val RECORDADO = "recordado"
        /** Subcategoría y monto de un gasto frecuente del widget: se registra con el toque. */
        const val SUBCATEGORIA = "subcategoria"; const val MONTO = "monto"
        /** Día (AAAA-MM-DD) para el que se anota, desde «＋ agregar a este día». */
        const val FECHA = "fecha"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Repo.init(this)
        val modo = intent.getStringExtra(MODO) ?: M_MENU
        // El enfoque y la siesta viven en su propio reloj a pantalla completa.
        if (modo == M_ENFOQUE || modo == M_SIESTA) { abrirReloj(modo == M_SIESTA); finish(); return }
        val origen = intent.getStringExtra(ORIGEN) ?: if (intent.hasExtra(AVISO_MS)) "notificacion" else "acceso_directo"
        val avisoMs = intent.getLongExtra(AVISO_MS, 0L).takeIf { it > 0 }
        intent.getIntExtra(NOTIF, 0).takeIf { it != 0 }?.let { NotificationManagerCompat.from(this).cancel(it) }

        // Guardado inmediato cuando el toque ya trae la información.
        var inicial: Momento? = null
        val tipo = intent.getStringExtra(TIPO)
        var pantallaInicial = when (modo) { M_MOMENTO, M_ANIMO -> M_ANIMO; else -> modo }
        if (modo == M_MOMENTO && tipo != null) {
            if (tipo == "impulso" || tipo == "recaida" || (tipo == "libido" && Repo.config.seguimiento_privado)) { inicial = crear(tipo, origen, null, null); pantallaInicial = "privado" }
            else {
                // Un sentimiento (del widget) o un tipo de momento de antes: se guarda como registro de ánimo.
                val m0 = crear("animo", origen, null, null)
                val n = when {
                    Catalogo.sentimiento(tipo) != null -> m0.copy(emociones = listOf(tipo))
                    Catalogo.PENSAMIENTOS.any { it.id == tipo } -> m0.copy(pensamiento = tipo, pensamientos = listOf(tipo))
                    else -> m0
                }
                Repo.guardarMomento(n); inicial = n
            }
        }
        val val0 = intent.getIntExtra(VALENCIA, 0).takeIf { it in 1..7 }
        if (modo == M_ANIMO && val0 != null) inicial = crear(if (avisoMs != null) "muestreo" else "animo", origen, val0, avisoMs)
        val consumoId = intent.getStringExtra(CONSUMO)
        val sustanciaInicial = intent.getStringExtra(SUSTANCIA)
        // Desde el widget de sueño: la calidad se guarda con el toque.
        if (modo == M_SUENO) {
            val cal = intent.getIntExtra(CALIDAD, 0).takeIf { it in 1..5 }
            val rec = intent.getBooleanExtra(RECORDADO, false)
            if (cal != null || rec) {
                // Va al día del despertar (la fecha del calendario), como las horas del reloj, aunque sea antes de las 5:00 (H-NAV-7).
                val d = Repo.dia(Fechas.hoyCalendario())
                Repo.guardarDia(d.copy(sueno_calidad = cal ?: d.sueno_calidad, sueno_recordado = rec || d.sueno_recordado))
            }
        }
        val catInicial = intent.getStringExtra(CATEGORIA)
        // Un frecuente del widget de gasto: se registra con el toque (N-29); la hoja deja cambiar el monto o deshacerlo.
        val subInicial = intent.getStringExtra(SUBCATEGORIA)
        val gastoInicial = if (modo == M_GASTO && catInicial != null && subInicial != null)
            intent.getDoubleExtra(MONTO, 0.0).takeIf { it > 0 }?.let { m -> io.github.ruddy2003.vestigium.data.Dinero.registrarFrecuente(catInicial, subInicial, m).also { (g, nuevo) -> if (nuevo) ubicarGasto(g) }.first }
        else null

        setContent {
            TemaVestigium {
                Box(
                    Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { tocarFuera() },
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    var pantalla by remember { mutableStateOf(pantallaInicial) }
                    var privado by remember { mutableStateOf(inicial?.takeIf { pantallaInicial == "privado" }) }
                    HojaQuick(Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                        .navigationBarsPadding().imePadding()) {
                        val ir: (String) -> Unit = { destino ->
                            when (destino) {
                                "impulso", "recaida", "libido" -> { privado = crear(destino, origen, null, null); pantalla = "privado" }
                                else -> pantalla = destino
                            }
                        }
                        when (pantalla) {
                            M_ANIMO -> Animo(inicial, origen, avisoMs, ir)
                            "privado" -> privado?.let { MomentoCaptura(it) } ?: Animo(null, origen, avisoMs, ir)
                            M_MENU -> Registrar(ir)
                            M_GASTO -> GastoRapido(catInicial, subInicial, gastoInicial)
                            M_SUENO -> SuenoRapido()
                            M_MINIMO -> Minimo()
                            M_ENFOQUE, M_SIESTA -> androidx.compose.runtime.LaunchedEffect(Unit) { abrirReloj(pantalla == M_SIESTA); terminar() }
                            M_CONSUMO -> ConsumoRapido(consumoId, sustanciaInicial, origen)
                            else -> Registrar(ir)
                        }
                    }
                    // La barra de «deshacer» también aquí (por ejemplo, al borrar un consumo).
                    CapaDeshacer()
                }
            }
        }
    }

    private fun terminar() { runCatching { Widgets.actualizar(this) }; finish() }

    /** Hay algo escrito que no se ha guardado (el monto de un gasto): tocar fuera avisa una vez antes de cerrar. */
    private var sinGuardar = false
    private var avisado = false
    private fun tocarFuera() {
        if (sinGuardar && !avisado) { avisado = true; Toast.makeText(this, "Tienes un monto sin registrar. Toca fuera otra vez para salir sin guardarlo.", Toast.LENGTH_LONG).show() }
        else terminar()
    }

    /** Cómo estaba el día antes de registrar una recaída, para que «Deshacer» lo deje igual. */
    private var previoRecaida: io.github.ruddy2003.vestigium.data.Recaidas.Previo? = null

    /**
     * Crea y guarda el momento al instante; la ubicación se añade en cuanto llega. Con [ms] (lo anotas tarde) el momento
     * va a esa hora y a su día lógico, sin la ubicación, la luz ni el ruido de ahora.
     */
    fun crear(tipo: String, origen: String, valencia: Int?, avisoMs: Long?, ms: Long? = null): Momento {
        val m0 = Momento(
            id = Repo.nuevoId(), ts = Fechas.ahoraIso(), fecha = Fechas.diaLogico(), tipo = tipo, valencia = valencia,
            origen = origen, aviso_ts = avisoMs?.let { Fechas.isoDeMillis(it) }, ts_creado = Fechas.ahoraIso(),
        )
        if (ms != null) {
            val m = io.github.ruddy2003.vestigium.data.Recaidas.aOtraHora(m0, ms)
            Repo.guardarMomento(m)
            if (tipo == "recaida") previoRecaida = io.github.ruddy2003.vestigium.data.Recaidas.registrar(m)
            return m
        }
        val m = m0
        Repo.guardarMomento(m)
        if (tipo == "recaida") previoRecaida = io.github.ruddy2003.vestigium.data.Recaidas.registrar(m)
        // Luz y, si lo activaste, ruido del ambiente en ese instante (no se guarda audio).
        lifecycleScope.launch {
            val ctx = this@QuickActivity
            val luz = if (Repo.config.medir_luz) runCatching { io.github.ruddy2003.vestigium.sensores.Percepcion.lux(ctx) }.getOrNull() else null
            kotlinx.coroutines.delay(250)
            val ruido = if (Repo.config.medir_ruido) runCatching { io.github.ruddy2003.vestigium.sensores.Ambiente.ruido(ctx) }.getOrNull() else null
            if (luz != null || ruido != null) actualizar(m.id) { if (it.ts != m.ts) it else it.copy(luz_lux = luz ?: it.luz_lux, ruido_db = ruido ?: it.ruido_db) }
        }
        lifecycleScope.launch {
            val loc = Ubicacion.actual(this@QuickActivity) ?: return@launch
            val actual = Repo.momento(m.id) ?: m
            // Si ya le cambiaste la hora, la ubicación de ahora no es la de ese momento.
            if (actual.ts != m.ts) return@launch
            val lugar = Repo.lugarCercano(loc.latitude, loc.longitude)
            Repo.guardarMomento(actual.copy(lat = loc.latitude, lon = loc.longitude, precision_m = loc.accuracy, lugar = lugar?.id))
            Repo.db().ponerUbicacion(System.currentTimeMillis(), loc.latitude, loc.longitude, loc.accuracy, "momento")
        }
        return m
    }

    private fun actualizar(id: String, f: (Momento) -> Momento) { Repo.momento(id)?.let { Repo.guardarMomento(f(it)) } }

    @Composable
    private fun Encabezado(titulo: String, sub: String, fecha: String? = null) {
        // El día va también en palabras: con algunos temas la fecha del estilo no se lee de un vistazo (N-08).
        val dia = fecha ?: Fechas.diaLogico()
        EncabezadoQuick(titulo, Fechas.horaAhora() + " · " + io.github.ruddy2003.vestigium.ui.Estilo.fecha(dia) + " · " + Fechas.titulo(dia).lowercase(), sub) {
            // Con una fecha propia (el sueño va al día del despertar) el aviso del día lógico no aplica.
            if (fecha == null) io.github.ruddy2003.vestigium.ui.AvisoMadrugada()
        }
    }

    /** El menú del botón ⊕: todo lo que se registra, en botones grandes. */
    @Composable
    private fun Registrar(ir: (String) -> Unit) {
        val cs = MaterialTheme.colorScheme
        val forma = formaPiezaQuick(Estilo.tema)
        Encabezado("Registrar", "Lo de todos los días en un toque. Lo demás, cuando quieras.")
        val botones = listOfNotNull(
            "Enfoque" to M_ENFOQUE, "Siesta" to M_SIESTA, "Sustancias" to M_CONSUMO, "Gasto o ingreso" to M_GASTO, "Sueño" to M_SUENO, "Llenar el día" to "diario",
            "Nota" to "notas", "Idea" to "idea_nueva", "Impulso" to "impulso", if (Repo.config.seguimiento_privado) "Recaída" to "recaida" else null,
            if (Repo.config.seguimiento_privado) "Libido" to "libido" else null,
        )
        MenuQuick(botones) { d ->
            if (d == "diario" || d == "notas" || d == "idea_nueva") { startActivity(android.content.Intent(this@QuickActivity, MainActivity::class.java).putExtra(MainActivity.PANTALLA, d)
                .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)); finish() }
            else ir(d)
        }
        // Suplementos: un toque anota la dosis habitual.
        val supl = io.github.ruddy2003.vestigium.data.Suplementos.activos()
        if (supl.isNotEmpty()) {
            var tomados by remember { mutableStateOf(supl.filter { io.github.ruddy2003.vestigium.data.Suplementos.tomadoHoy(it.id) }.map { it.id }.toSet()) }
            var duda by remember { mutableStateOf<String?>(null) }
            FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = 2) {
                supl.forEach { sp ->
                    val ya = sp.id in tomados
                    val dosis = if (sp.dosis == Math.floor(sp.dosis)) "%.0f".format(sp.dosis) else "%.1f".format(sp.dosis)
                    // Si ya lo tomaste hoy, un toque pregunta (otra toma o quitar la de hoy) en vez de sumar otra sin querer.
                    Surface(onClick = { if (ya) duda = if (duda == sp.id) null else sp.id else { io.github.ruddy2003.vestigium.data.Suplementos.registrar(sp); tomados = tomados + sp.id } },
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp), shape = forma, color = if (ya) cs.primary.copy(alpha = 0.12f) else cs.surface,
                        border = BorderStroke(1.dp, if (ya) cs.primary else cs.outline)) {
                        Box(contentAlignment = Alignment.Center) { Text((if (ya) "✓ " else "Tomé ") + sp.nombre.lowercase() + " · $dosis ${sp.unidad}", style = MaterialTheme.typography.labelLarge) }
                    }
                }
            }
            supl.firstOrNull { it.id == duda }?.let { sp ->
                Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(sp.nombre + " ya está hoy:", style = MaterialTheme.typography.labelMedium, modifier = Modifier.weight(1f))
                    TextButton(onClick = { io.github.ruddy2003.vestigium.data.Suplementos.registrar(sp); duda = null; Toast.makeText(this@QuickActivity, "Otra toma anotada", Toast.LENGTH_SHORT).show() }) { Text("otra toma") }
                    TextButton(onClick = {
                        // Se cuentan antes de borrar: la lista en memoria se actualiza después, en el hilo principal.
                        val deHoy = Repo.tomas.filter { it.suplemento == sp.id && it.fecha == Fechas.diaLogico() }
                        deHoy.maxByOrNull { it.ts }?.let { Repo.borrarToma(it.id) }
                        if (deHoy.size <= 1) tomados = tomados - sp.id
                        duda = null; Toast.makeText(this@QuickActivity, "Quitada la de hoy (7 días en la papelera)", Toast.LENGTH_SHORT).show()
                    }) { Text("quitar la de hoy") }
                }
            }
        }
        val abiertos = io.github.ruddy2003.vestigium.data.Sustancias.abiertos()
        if (abiertos.isNotEmpty()) Ayuda("En curso: " + abiertos.joinToString(", ") { if (io.github.ruddy2003.vestigium.data.Sustancias.visible(it)) Catalogo.nombreSustancia(it).lowercase() else "un registro" } +
            ". Tócalo en «Sustancias» para cambiar el nivel o decir que ya se te pasó.", Modifier.padding(top = 10.dp))
    }

    /**
     * Ánimo ahora: un registro con tres partes. Un toque en la cuadrícula (qué tan agradable y cuánta energía),
     * y opcionales: qué sientes (varios, «otro» o «no sé»), en qué piensas y con quién estás.
     * De un aviso se guarda como «muestreo»; si lo abres tú, como «animo».
     */
    @Composable
    private fun Animo(inicial: Momento?, origen: String, avisoMs: Long?, ir: (String) -> Unit) {
        var m by remember { mutableStateOf(inicial) }
        var valencia by remember { mutableStateOf(inicial?.valencia) }
        var activacion by remember { mutableStateOf(inicial?.activacion) }
        var emociones by remember { mutableStateOf(inicial?.emociones.orEmpty()) }
        var otra by remember { mutableStateOf(inicial?.emocion_otra.orEmpty()) }
        var intensidades by remember { mutableStateOf(inicial?.intensidades.orEmpty()) }
        var pensamientos by remember { mutableStateOf(inicial?.let { Catalogo.pensamientosDe(it) }.orEmpty()) }
        var nota by remember { mutableStateOf(inicial?.texto.orEmpty()) }
        var con by remember { mutableStateOf(inicial?.con_quien.orEmpty()) }
        val discreto = Repo.config.discreto
        // Cuándo: null = ahora. Para anotar tarde («ayer», otra hora); no en la respuesta a un aviso, que es de ese momento.
        // Desde «＋ agregar a este día» de otro día: empieza a mediodía de ese día, con el selector abierto (N-26).
        val fechaPedida = intent.getStringExtra(FECHA)?.takeIf { it != Fechas.diaLogico() }
        var cuando by remember { mutableStateOf<java.time.LocalDateTime?>(fechaPedida?.let { Fechas.parse(it).atTime(12, 0) }) }
        var elegirCuando by remember { mutableStateOf(fechaPedida != null) }
        /**
         * El registro se crea con lo primero que diga algo del ánimo: la cuadrícula, una emoción, un pensamiento o el texto.
         * «Con quién» solo no crea nada: espera y se guarda junto con lo demás.
         */
        fun asegurar(f: (Momento) -> Momento) {
            val nuevo = m == null
            val actual = m ?: crear(if (avisoMs != null) "muestreo" else "animo", origen, null, avisoMs, cuando?.aMillis())
            var base = Repo.momento(actual.id) ?: actual
            if (nuevo && con.isNotEmpty()) base = base.copy(con_quien = con)
            val n = f(base)
            Repo.guardarMomento(n); m = n
        }
        Encabezado("¿Cómo estás ahora?", if (m != null) "Guardado a las ${Fechas.horaDe(m!!.ts)} del ${Fechas.titulo(m!!.fecha).lowercase()}. Lo demás es opcional; puedes cerrar." else "Un toque en el cuadro basta.")
        // Lo guardó el toque en el widget o en el aviso: se dice qué quedó y se puede borrar si fue sin querer (N-15).
        if (inicial != null && m != null) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Guardado con el toque" + (m!!.valencia?.let { ": $it/7" } ?: "") + ".", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = { Repo.borrarMomento(m!!.id); Toast.makeText(this@QuickActivity, "Borrado", Toast.LENGTH_SHORT).show(); terminar() }) { Text("borrar") }
        }
        if (avisoMs == null && inicial == null) {
            if (!elegirCuando) TextButton(onClick = { elegirCuando = true }) {
                Text(if (cuando == null) "¿fue antes? anotarlo en otra hora o día" else "cuándo: ${Fechas.corto(Fechas.diaLogicoDeMillis(cuando!!.aMillis()))} ${"%02d:%02d".format(cuando!!.hour, cuando!!.minute)}",
                    style = MaterialTheme.typography.labelLarge)
            } else io.github.ruddy2003.vestigium.ui.SelectorHora(cuando, ayer = true, atajos = listOf(60, 180)) { nuevo ->
                cuando = nuevo
                // Si ya estaba creado, se mueve a esa hora (y a su día).
                m?.let { actual -> val ms = (nuevo ?: java.time.LocalDateTime.now()).aMillis(); val n = io.github.ruddy2003.vestigium.data.Recaidas.aOtraHora(Repo.momento(actual.id) ?: actual, ms); Repo.guardarMomento(n); m = n }
            }
        }
        BannerConsumo(ir)
        CuadriculaAfecto(valencia, activacion) { v, a -> valencia = v; activacion = a; asegurar { it.copy(valencia = v, activacion = a) } }
        Etiqueta("¿Qué sientes? (opcional, varios)")
        io.github.ruddy2003.vestigium.ui.SelectorEmociones(io.github.ruddy2003.vestigium.ui.EleccionEmociones(emociones, otra, intensidades), discreto) { e ->
            emociones = e.emociones; otra = e.otra; intensidades = e.intensidades
            asegurar { it.copy(emociones = e.emociones, emocion_otra = if (Catalogo.OTRO in e.emociones) e.otra.trim().ifBlank { null } else null, intensidades = e.intensidades) }
        }
        Etiqueta("¿En qué piensas? (opcional, varios)")
        io.github.ruddy2003.vestigium.ui.SelectorPensamientos(pensamientos, discreto) { l ->
            pensamientos = l
            asegurar { it.copy(pensamientos = l, pensamiento = l.firstOrNull()) }
        }
        CampoTexto(value = nota, onValueChange = { t -> nota = t; asegurar { it.copy(texto = t) } },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp), minLines = 2, placeholder = { Text("¿Qué está pasando? Usa @persona y #tema") }, visualTransformation = rememberMarcas())
        Row(Modifier.padding(top = 6.dp)) { BotonDictado { d -> nota = (nota + " " + d).trim(); val t = nota; asegurar { it.copy(texto = t) } } }
        Etiqueta("¿Con quién estás? (opcional)")
        io.github.ruddy2003.vestigium.ui.SelectorConQuien(con) { c ->
            con = c
            // Solo se guarda si ya hay registro; si no, espera a lo primero del ánimo.
            if (m != null) asegurar { it.copy(con_quien = c) }
        }
        // Lo que no es ánimo, a un toque: dinero, sustancias, impulso y recaída.
        var dinero by remember { mutableStateOf(false) }
        var anotado by remember { mutableStateOf<List<String>>(emptyList()) }
        FlowRow(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            BotonSecundario(if (dinero) "− gasto o ingreso" else "＋ gasto o ingreso") { dinero = !dinero }
            BotonSecundario("＋ sustancia") { ir(M_CONSUMO) }
            BotonSecundario("＋ impulso") { ir("impulso") }
            if (Repo.config.seguimiento_privado) BotonSecundario("＋ recaída") { ir("recaida") }
            if (Repo.config.seguimiento_privado) BotonSecundario("＋ libido") { ir("libido") }
        }
        // Para no olvidar el dinero: cuánto llevas anotado hoy, lo que acabas de anotar aquí y, al abrirlo, el mismo formulario
        // del ⊕ (con la hora del momento si lo estás anotando tarde). Registrar no cierra la hoja.
        val hoyDia = m?.fecha ?: Fechas.diaLogico()
        val deHoy = Repo.gastos.filter { it.dia == hoyDia }
        Text(if (deHoy.isEmpty()) "Hoy no has anotado gastos." else "Hoy: ${deHoy.size} ${if (deHoy.size == 1) "gasto" else "gastos"} · ${io.github.ruddy2003.vestigium.ui.pantallas.dinero(deHoy.sumOf { it.monto })}",
            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
        anotado.forEach { Text("✓ $it", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp)) }
        if (dinero) Surface(shape = formaPiezaQuick(Estilo.tema), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.06f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Column(Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                GastoRapido(null, null, null, embebido = true, cuandoInicial = cuando) { t -> anotado = anotado + t; dinero = false }
            }
        }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            if (m != null) TextButton(onClick = { Repo.borrarMomento(m!!.id); Toast.makeText(this@QuickActivity, "Borrado", Toast.LENGTH_SHORT).show(); terminar() }) { Text("Borrar este registro") }
            else Spacer(Modifier.width(1.dp))
            BotonPrincipal("Listo") { terminar() }
        }
    }

    /** Si hay una sustancia en curso: su nivel ahora y «ya se me pasó», sin salir del ánimo. */
    @Composable
    private fun BannerConsumo(ir: (String) -> Unit) {
        val abiertos = io.github.ruddy2003.vestigium.data.Sustancias.abiertos()
        if (abiertos.isEmpty()) return
        val cs = MaterialTheme.colorScheme
        abiertos.forEach { c ->
            var nivel by remember(c.id) { mutableStateOf(c.niveles.lastOrNull()?.nivel) }
            Surface(color = cs.secondaryContainer, shape = formaPiezaQuick(Estilo.tema), modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Column(Modifier.padding(12.dp)) {
                    val nombre = if (io.github.ruddy2003.vestigium.data.Sustancias.visible(c)) Catalogo.nombreSustancia(c) else "Registro en curso"
                    Text("$nombre desde las ${Fechas.horaDe(c.inicio)} · ¿cómo vas?", style = MaterialTheme.typography.titleSmall, color = cs.onSecondaryContainer)
                    Escala(nivel, 0, 10, Catalogo.anclasNivel(c.sustancia)) { v -> nivel = v; if (v != null) io.github.ruddy2003.vestigium.data.Sustancias.lectura(Repo.consumo(c.id) ?: c, v) }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                        TextButton(onClick = { io.github.ruddy2003.vestigium.data.Sustancias.cerrar(Repo.consumo(c.id) ?: c); Toast.makeText(this@QuickActivity, "Anotado: ya se te pasó", Toast.LENGTH_SHORT).show() }) { Text("ya se me pasó") }
                        TextButton(onClick = { ir(M_CONSUMO) }) { Text("más detalles") }
                    }
                }
            }
        }
    }

    /** Impulso o recaída: lo que pasó, qué tan fuerte y (en la recaída) hora, duración y detalles. */
    @Composable
    private fun MomentoCaptura(inicial: Momento) {
        var m by remember { mutableStateOf(inicial) }
        var texto by remember { mutableStateOf("") }
        var intensidad by remember { mutableStateOf<Int?>(null) }
        val discreto = Repo.config.discreto
        val mm = m
        val t = Catalogo.tipo(mm.tipo)
        Encabezado("Guardado: ${if (discreto) t?.discreto ?: "" else t?.nombre ?: mm.tipo}", "A las ${Fechas.horaDe(mm.ts)}. Lo demás es opcional; puedes cerrar.")
        if (mm.tipo == "libido") { DetalleLibido(mm); return }
        if (mm.tipo == "recaida") DetalleRecaida(mm) { m = it }
        Etiqueta(t?.pregunta ?: "Detalle")
        CampoTexto(value = texto, onValueChange = { texto = it; actualizar(mm.id) { x -> x.copy(texto = it) } },
            modifier = Modifier.fillMaxWidth(), minLines = 2, placeholder = { Text("Usa @persona y #tema") }, visualTransformation = rememberMarcas())
        Row(Modifier.padding(top = 8.dp)) { BotonDictado { d -> texto = (texto + " " + d).trim(); actualizar(mm.id) { x -> x.copy(texto = texto) } } }
        Etiqueta(if (mm.tipo == "recaida") "Qué tan fuerte era el impulso" else "Intensidad")
        Escala(intensidad, 1, 5, mapOf(1 to "Muy leve", 2 to "Leve", 3 to "Moderada", 4 to "Fuerte", 5 to "Muy fuerte")) { v ->
            intensidad = v; actualizar(mm.id) { it.copy(intensidad = v) }
        }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = {
                Repo.borrarMomento(mm.id)
                previoRecaida?.let { io.github.ruddy2003.vestigium.data.Recaidas.deshacer(it) }; previoRecaida = null
                Toast.makeText(this@QuickActivity, "Borrado", Toast.LENGTH_SHORT).show(); terminar()
            }) { Text("Borrar este registro") }
            BotonPrincipal("Listo") { terminar() }
        }
    }

    /**
     * Libido (seguimiento privado): qué tan presente, de 0 a 5, y en qué está el foco. Distinta del impulso; sin texto,
     * sin logros, rachas ni widget. «También es un impulso» crea el registro de impulso a la misma hora.
     */
    @Composable
    private fun DetalleLibido(mm: Momento) {
        var nivel by remember(mm.id) { mutableStateOf(mm.intensidad) }
        var foco by remember(mm.id) { mutableStateOf(mm.foco) }
        var impulso by remember(mm.id) { mutableStateOf<Momento?>(null) }
        Etiqueta("Qué tan presente (0 a 5)")
        Escala(nivel, 0, 5, Catalogo.ANCLAS["libido"]) { v -> nivel = v; actualizar(mm.id) { it.copy(intensidad = v) } }
        Etiqueta("En qué está el foco")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.FOCOS.forEach { o -> Ficha(o.nombre, foco == o.id) { foco = if (foco == o.id) null else o.id; actualizar(mm.id) { it.copy(foco = foco) } } }
        }
        Ayuda("Solo para ti: no sale en reportes, logros, «Tu año», mapas ni widgets. Sí en tu exportación a R.", Modifier.padding(top = 10.dp))
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            if (impulso == null) TextButton(onClick = {
                val i = Momento(Repo.nuevoId(), mm.ts, mm.fecha, "impulso", intensidad = nivel?.coerceIn(1, 5), lat = mm.lat, lon = mm.lon, lugar = mm.lugar, origen = mm.origen, ts_creado = Fechas.ahoraIso())
                Repo.guardarMomento(i); impulso = i
                Toast.makeText(this@QuickActivity, "Impulso registrado a la misma hora", Toast.LENGTH_SHORT).show()
            }) { Text("también es un impulso") }
            else TextButton(onClick = { impulso?.let { Repo.borrarMomento(it.id) }; impulso = null }) { Text("quitar el impulso") }
            BotonPrincipal("Listo") { terminar() }
        }
        TextButton(onClick = { Repo.borrarMomento(mm.id); impulso?.let { Repo.borrarMomento(it.id) }; Toast.makeText(this@QuickActivity, "Borrado", Toast.LENGTH_SHORT).show(); terminar() }) { Text("Borrar este registro") }
    }

    /** Lo que ayuda a entender una recaída: qué la detonó, en qué aparato y qué hacías antes. Todo opcional. */
    @Composable
    private fun DetalleRecaida(mm: Momento, alCambiar: (Momento) -> Unit) {
        var det by remember(mm.id) { mutableStateOf(mm.detonantes) }
        var inicio by remember(mm.id) { mutableStateOf(Fechas.millis(mm.ts)) }
        var dur by remember(mm.id) { mutableStateOf(mm.duracion_min) }
        val ahoraMs = remember(mm.id) { Fechas.millis(mm.ts).takeIf { it > 0 } ?: System.currentTimeMillis() }
        /** Otra hora de inicio: se guarda con su día y con la ubicación que había a esa hora. */
        fun cambiarHora(ms: Long) {
            inicio = ms
            lifecycleScope.launch {
                val actual = Repo.momento(mm.id) ?: return@launch
                val n = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { io.github.ruddy2003.vestigium.data.Recaidas.aOtraHora(actual, ms) }
                Repo.guardarMomento(n)
                if (n.fecha != actual.fecha) {
                    previoRecaida?.let { io.github.ruddy2003.vestigium.data.Recaidas.deshacer(it) }
                    previoRecaida = io.github.ruddy2003.vestigium.data.Recaidas.registrar(n)
                }
                alCambiar(n)
            }
        }
        Etiqueta("¿Cuándo empezó?")
        var cuando by remember(mm.id) { mutableStateOf<java.time.LocalDateTime?>(null) }
        SelectorHora(cuando, atajos = listOf(15, 30, 60, 120, 180)) { t -> cuando = t; cambiarHora(t?.aMillis() ?: ahoraMs) }
        if (kotlin.math.abs(inicio - ahoraMs) > 60_000L) Ayuda("El lugar se toma de tus ubicaciones de esa hora.", Modifier.padding(top = 4.dp))
        Etiqueta("¿Cuánto duró?")
        SelectorDuracion(dur) { d -> dur = d; actualizar(mm.id) { it.copy(duracion_min = d) } }
        var aparato by remember(mm.id) { mutableStateOf(mm.dispositivo) }
        var antes by remember(mm.id) { mutableStateOf(mm.antes) }
        val h = io.github.ruddy2003.vestigium.data.Recaidas.habito()
        Ayuda("Solo para ti: no sale en reportes, logros ni «Tu año». " + (if (h != null) "El hábito «${h.nombre}» quedó en 0 hoy." else "Puedes ligarla a un hábito en Privacidad."),
            Modifier.padding(bottom = 4.dp))
        Etiqueta("Qué la detonó")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.DETONANTES.forEach { d ->
                val activo = d.id in det
                Ficha(d.nombre, activo) { det = if (activo) det - d.id else det + d.id; actualizar(mm.id) { it.copy(detonantes = det, detonante_otro = if ("otro" in det) it.detonante_otro else null) } }
            }
        }
        if ("otro" in det) {
            var otro by remember(mm.id) { mutableStateOf(mm.detonante_otro.orEmpty()) }
            CampoTexto(value = otro, onValueChange = { t -> otro = t; actualizar(mm.id) { it.copy(detonante_otro = t.trim().ifBlank { null }) } },
                modifier = Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("¿Cuál?") })
        }
        Etiqueta("Qué hacías justo antes")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.ANTES.forEach { o -> Ficha(o.nombre, antes == o.id) { antes = if (antes == o.id) null else o.id; actualizar(mm.id) { it.copy(antes = antes) } } }
        }
        Etiqueta("En qué aparato")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.DISPOSITIVOS.forEach { o -> Ficha(o.nombre, aparato == o.id) { aparato = if (aparato == o.id) null else o.id; actualizar(mm.id) { it.copy(dispositivo = aparato) } } }
        }
    }

    /** El cronómetro: elegir qué empieza, ver cuánto lleva y terminarlo; también anotar una siesta que ya pasó. */
    private fun abrirReloj(siesta: Boolean) {
        startActivity(android.content.Intent(this, EnfoqueActivity::class.java).putExtra(EnfoqueActivity.SIESTA, siesta)
            .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP))
    }

    /** Guarda dónde estabas en un consumo recién creado (si es de ahora). */
    private fun ubicarConsumo(c: io.github.ruddy2003.vestigium.data.Consumo) {
        lifecycleScope.launch {
            val loc = Ubicacion.actual(this@QuickActivity) ?: return@launch
            val actual = Repo.consumo(c.id) ?: return@launch
            Repo.guardarConsumo(actual.copy(lat = loc.latitude, lon = loc.longitude, lugar = Repo.lugarCercano(loc.latitude, loc.longitude)?.id))
        }
    }

    /**
     * Sustancias. Sin nada abierto: qué, desde cuándo, cómo vas (0 a 10), cuánto y con quién (lo básico).
     * Con un consumo en curso: subir o bajar el nivel, sumar bebidas, «ya se me pasó».
     * Con [consumoId]: completar lo de anoche (hasta qué hora, máximo, cantidad, cruda y síntomas).
     */
    @Composable
    private fun ConsumoRapido(consumoId: String?, sustanciaInicial: String?, origen: String) {
        val ctx = this
        val sus = io.github.ruddy2003.vestigium.data.Sustancias
        val completar = consumoId?.let { Repo.consumo(it) }
        if (completar != null) { CompletarConsumo(completar); return }
        var sustancia by remember { mutableStateOf(sustanciaInicial) }
        Encabezado("Sustancias", "Lo básico ahora; la duración y la cruda, mañana. Es un dato como cualquier otro.")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.SUSTANCIAS.forEach { x -> Ficha(x.nombre, sustancia == x.id) { sustancia = x.id } }
        }
        val id = sustancia ?: return
        val info = Catalogo.sustancia(id) ?: return
        val abierto = sus.abiertos().firstOrNull { it.sustancia == id }
        if (abierto != null) {
            var nivel by remember(abierto.id) { mutableStateOf(abierto.niveles.lastOrNull()?.nivel) }
            var cantidad by remember(abierto.id) { mutableStateOf(abierto.cantidad ?: 0.0) }
            var fin by remember(abierto.id) { mutableStateOf<java.time.LocalDateTime?>(null) }
            Ayuda("En curso desde las ${Fechas.horaDe(abierto.inicio)}" + (sus.maximo(abierto)?.let { " · máximo hasta ahora $it" } ?: ""), Modifier.padding(top = 10.dp))
            Etiqueta("¿Cómo vas ahora? (0 a 10)")
            Escala(nivel, 0, 10, Catalogo.anclasNivel(id)) { v -> nivel = v; if (v != null) sus.lectura(Repo.consumo(abierto.id) ?: abierto, v) }
            if (id == "alcohol") CantidadConsumo("alcohol", "bebidas", cantidad) { q -> cantidad = q; Repo.consumo(abierto.id)?.let { Repo.guardarConsumo(it.copy(cantidad = q.takeIf { v -> v > 0 })) } }
            Etiqueta("¿Ya se te pasó? ¿A qué hora?")
            SelectorHora(fin, atajos = listOf(15, 30, 60)) { fin = it }
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { terminar() }) { Text("sigue") }
                BotonPrincipal("Ya se me pasó") {
                    sus.cerrar(Repo.consumo(abierto.id) ?: abierto, (fin ?: java.time.LocalDateTime.now()).aMillis())
                    Toast.makeText(ctx, "Anotado. Mañana te pregunto cómo terminó.", Toast.LENGTH_SHORT).show(); terminar()
                }
            }
            return
        }
        var cuando by remember(id) { mutableStateOf<java.time.LocalDateTime?>(null) }
        var guardado by remember(id) { mutableStateOf(false) }
        var nivel by remember(id) { mutableStateOf<Int?>(null) }
        var cantidad by remember(id) { mutableStateOf(0.0) }
        var via by remember(id) { mutableStateOf<String?>(null) }
        var otra by remember(id) { mutableStateOf("") }
        var con by remember(id) { mutableStateOf<List<String>>(emptyList()) }
        if (id == "otra") CampoTexto(value = otra, onValueChange = { otra = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("¿Cuál?") })
        Etiqueta(if (info.conNivel) "¿Desde cuándo?" else "¿Cuándo?")
        SelectorHora(cuando, atajos = listOf(30, 60, 120, 180)) { cuando = it }
        if (info.conNivel) {
            Etiqueta("¿Cómo vas ahora? (0 a 10)")
            Escala(nivel, 0, 10, Catalogo.anclasNivel(id)) { nivel = it }
        }
        if (id != "alcohol" && id != "cafeina" && info.vias.isNotEmpty()) {
            Etiqueta("¿Cómo?")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { info.vias.forEach { o -> Ficha(o.nombre, via == o.id) { via = if (via == o.id) null else o.id } } }
        }
        CantidadConsumo(id, info.unidad, cantidad) { cantidad = it }
        if (id == "cafeina") Ayuda("Si registras la cafeína en MacroFactor, esos días se usa la de MacroFactor (con la hora de cada alimento) y no se cuenta dos veces.", Modifier.padding(top = 4.dp))
        Etiqueta("¿Con quién? (opcional)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.CON_QUIEN.forEach { o -> Ficha(o.nombre, o.id in con) { con = Catalogo.alternarConQuien(con, o.id) } }
        }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.End) {
            BotonPrincipal("Guardar", habilitado = (id != "otra" || otra.isNotBlank()) && !guardado) {
                // Un toque doble no guarda dos consumos.
                if (guardado) return@BotonPrincipal
                guardado = true
                val ms = (cuando ?: java.time.LocalDateTime.now()).aMillis()
                val c = sus.nuevo(id, ms, nivel, cantidad.takeIf { it > 0 }, via, otra, con, origen)
                if (cuando == null) ubicarConsumo(c)
                Toast.makeText(ctx, if (info.conNivel) "Guardado. Desde «Ánimo ahora» puedes decir cómo vas." else "Guardado", Toast.LENGTH_SHORT).show()
                terminar()
            }
        }
    }

    /**
     * Cuánto: en alcohol y cafeína, fichas que suman (y «quitar el último» para deshacer la última que sumaste); en lo demás,
     * − y +. Siempre se puede escribir el total (con decimales) y «poner en 0».
     */
    @Composable
    private fun CantidadConsumo(sustancia: String, unidad: String, cantidad: Double, onCambio: (Double) -> Unit) {
        val D = io.github.ruddy2003.vestigium.data.Dinero
        var pila by remember(sustancia) { mutableStateOf(listOf<Double>()) }
        var texto by remember(sustancia) { mutableStateOf(if (cantidad > 0) D.texto(cantidad) else "") }
        fun poner(v: Double) { val n = (Math.round(v.coerceAtLeast(0.0) * 100) / 100.0); texto = if (n > 0) D.texto(n) else ""; onCambio(n) }
        val fichas = when (sustancia) { "alcohol" -> Catalogo.BEBIDAS; "cafeina" -> Catalogo.CAFES; else -> emptyList() }
        Etiqueta(when (sustancia) {
            "alcohol" -> "Bebidas: ${"%.1f".format(cantidad).removeSuffix(".0").removeSuffix(",0")} (opcional)"
            "cafeina" -> "¿Qué tomaste? ${cantidad.toInt()} mg"
            else -> "¿Cuántos $unidad?"
        })
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            fichas.forEach { (n, eq) -> Ficha("+ $n", false) { pila = pila + eq; poner(cantidad + eq) } }
            if (fichas.isEmpty()) {
                Ficha("−", false) { poner(cantidad - 1) }
                Ficha("+", false) { poner(cantidad + 1) }
            } else if (pila.isNotEmpty()) Ficha("quitar el último", false) { val u = pila.last(); pila = pila.dropLast(1); poner(cantidad - u) }
            else if (cantidad > 0 && sustancia == "alcohol") Ficha("− 1", false) { poner(cantidad - 1) }
            if (cantidad > 0) Ficha("poner en 0", false) { pila = emptyList(); poner(0.0) }
        }
        CampoTexto(value = texto, onValueChange = { t -> texto = D.limpiarMonto(t); onCambio(texto.toDoubleOrNull() ?: 0.0) },
            modifier = Modifier.padding(top = 6.dp).width(200.dp), singleLine = true, label = { Text("o escribe el total") },
            suffix = { Text(if (sustancia == "cafeina") "mg" else unidad) }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal))
    }

    /** La mañana siguiente (o al corregir): hasta qué hora duró, el máximo, cuánto, cómo, con quién, la cruda y cómo te sentiste. */
    @Composable
    private fun CompletarConsumo(c0: io.github.ruddy2003.vestigium.data.Consumo) {
        val ctx = this
        val info = Catalogo.sustancia(c0.sustancia)
        var inicio by remember { mutableStateOf<java.time.LocalDateTime?>(horaLocal(Fechas.millis(c0.inicio))) }
        var fin by remember { mutableStateOf(c0.fin?.let { horaLocal(Fechas.millis(it)) }) }
        var max by remember { mutableStateOf(io.github.ruddy2003.vestigium.data.Sustancias.maximo(c0)) }
        var cantidad by remember { mutableStateOf(c0.cantidad ?: 0.0) }
        var cruda by remember { mutableStateOf(c0.cruda) }
        var sintomas by remember { mutableStateOf(c0.sintomas) }
        var nota by remember { mutableStateOf(c0.nota) }
        var via by remember { mutableStateOf(c0.via) }
        var con by remember { mutableStateOf(c0.con_quien) }
        var otra by remember { mutableStateOf(c0.otra_nombre) }
        Encabezado((if (c0.fecha < Fechas.diaLogico()) "Lo de anoche: " else "Editar: ") + Catalogo.nombreSustancia(c0).lowercase(), "Unos toques. Lo que no recuerdes, déjalo vacío.")
        if (c0.sustancia == "otra") CampoTexto(value = otra, onValueChange = { otra = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("¿Cuál?") })
        Etiqueta("¿Desde cuándo?")
        SelectorHora(inicio, etiquetaAhora = "no sé") { inicio = it }
        Etiqueta("¿Hasta cuándo te duró el efecto?")
        SelectorHora(fin, etiquetaAhora = "no sé") { fin = it }
        if (info?.conNivel == true) {
            Etiqueta("¿Hasta qué nivel llegaste? (0 a 10)")
            Escala(max, 0, 10, Catalogo.anclasNivel(c0.sustancia)) { max = it }
        }
        if (info != null && c0.sustancia != "alcohol" && c0.sustancia != "cafeina" && info.vias.isNotEmpty()) {
            Etiqueta("¿Cómo?")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { info.vias.forEach { o -> Ficha(o.nombre, via == o.id) { via = if (via == o.id) null else o.id } } }
        }
        CantidadConsumo(c0.sustancia, info?.unidad ?: "", cantidad) { cantidad = it }
        Etiqueta("¿Con quién? (opcional)")
        SelectorConQuien(con) { con = it }
        Etiqueta("Cruda o bajón al día siguiente (0 a 5)")
        Escala(cruda, 0, 5, Catalogo.CRUDA) { cruda = it }
        Etiqueta("Síntomas (opcional)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.SINTOMAS.forEach { o -> val a = o.id in sintomas; Ficha(o.nombre, a) { sintomas = if (a) sintomas - o.id else sintomas + o.id } }
        }
        if ("otro" in sintomas) Ayuda("Escribe cuál en la nota.", Modifier.padding(top = 4.dp))
        CampoTexto(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), placeholder = { Text("Nota (opcional): @persona, #lugar…") },
            visualTransformation = rememberMarcas())
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
            var confirmar by remember { mutableStateOf(false) }
            TextButton(onClick = {
                if (!confirmar) { confirmar = true; return@TextButton }
                Repo.borrarConsumo(c0.id); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Registro borrado") { Repo.guardarConsumo(c0) }
                Toast.makeText(ctx, "Borrado. Queda 7 días en la papelera.", Toast.LENGTH_LONG).show(); terminar()
            }) { Text(if (confirmar) "¿borrar de verdad?" else "borrar", color = if (confirmar) MaterialTheme.colorScheme.error else androidx.compose.ui.graphics.Color.Unspecified) }
            BotonPrincipal("Guardar") {
                val actual = Repo.consumo(c0.id) ?: c0
                val ini = inicio?.aMillis() ?: Fechas.millis(actual.inicio)
                // «no sé» en el fin lo deja vacío (antes conservaba el que hubiera); la cantidad en 0 la quita.
                Repo.guardarConsumo(actual.copy(inicio = Fechas.isoDeMillis(ini), fecha = Fechas.diaLogicoDeMillis(ini), fin = fin?.aMillis()?.coerceAtLeast(ini)?.let { Fechas.isoDeMillis(it) },
                    nivel_max = max, cantidad = cantidad.takeIf { it > 0 }, via = via, con_quien = con, otra_nombre = if (actual.sustancia == "otra") otra.trim() else actual.otra_nombre,
                    cruda = cruda, sintomas = sintomas, nota = nota.trim(), completo = true))
                Toast.makeText(ctx, "Guardado", Toast.LENGTH_SHORT).show(); terminar()
            }
        }
    }

    /** La ubicación de ahora, para un gasto de ahora. */
    private fun ubicarGasto(g: Gasto) {
        lifecycleScope.launch {
            val loc = Ubicacion.ultima(this@QuickActivity)
            if (loc != null) Repo.gasto(g.id)?.let { Repo.guardarGasto(it.copy(lat = loc.latitude, lon = loc.longitude)) }
        }
    }

    /**
     * Gasto o ingreso (N-29). Arriba, «Gasto | Ingreso». En gasto, los frecuentes registran con un toque (⊕ → Gasto →
     * Café: tres toques); si no, categoría → subcategoría (con su monto habitual) → «Registrar». Escribir el monto o la nota
     * queda para lo muy específico.
     */
    @Composable
    private fun GastoRapido(catInicial: String?, subInicial: String?, inicial: Gasto?, embebido: Boolean = false,
                            cuandoInicial: java.time.LocalDateTime? = null, alGuardar: ((String) -> Unit)? = null) {
        val D = io.github.ruddy2003.vestigium.data.Dinero
        fun dinero(v: Double) = io.github.ruddy2003.vestigium.ui.pantallas.dinero(v)
        var entra by remember { mutableStateOf(false) }
        var monto by remember { mutableStateOf("") }
        // El monto que puso la subcategoría: si eliges otra, se reemplaza; si lo escribiste tú, se respeta.
        var auto by remember { mutableStateOf<String?>(null) }
        var guardando by remember { mutableStateOf(false) }
        androidx.compose.runtime.SideEffect { sinGuardar = monto.isNotBlank() && monto != auto && !guardando }
        var cat by remember { mutableStateOf(catInicial.takeIf { inicial == null }) }
        var sub by remember { mutableStateOf("") }
        var imprevisto by remember { mutableStateOf(false) }
        var repite by remember { mutableStateOf(false) }
        var fijo by remember { mutableStateOf(false) }
        androidx.compose.runtime.LaunchedEffect(Unit) { D.asegurarCategoriasNuevas() }
        var nota by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        var registrado by remember { mutableStateOf(inicial) }
        var corrigiendo by remember { mutableStateOf(false) }
        var cuando by remember { mutableStateOf(cuandoInicial) }
        val c = Repo.config
        // Dentro del momento no se cierra la hoja: se avisa a quien lo abrió y el formulario queda limpio para otro.
        fun listo(texto: String) {
            if (alGuardar == null) { Toast.makeText(this@QuickActivity, texto, Toast.LENGTH_SHORT).show(); terminar(); return }
            alGuardar(texto); monto = ""; auto = null; cat = null; sub = ""; nota = ""; imprevisto = false; repite = false; fijo = false; guardando = false
        }
        if (!embebido) Encabezado("Gasto o ingreso", if (entra) "Lo que entra: sueldo, beca, venta, regalo." else "En el momento de pagar. Un frecuente registra con un toque.")
        Row(Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Ficha("− Gasto", !entra) { if (entra) { entra = false; cat = null; sub = ""; error = null } }
            Ficha("+ Ingreso", entra) { if (!entra) { entra = true; cat = null; sub = ""; error = null } }
        }
        // Lo que acaba de registrar un frecuente: se ve, se corrige o se deshace.
        registrado?.let { r ->
            val g = Repo.gasto(r.id) ?: r
            Surface(shape = formaPiezaQuick(Estilo.tema), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f), modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
                Row(Modifier.padding(start = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${g.subcategoria.ifBlank { g.categoria }} ${dinero(g.monto)} registrado", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
                    TextButton(onClick = { corrigiendo = true }) { Text("cambiar monto") }
                    TextButton(onClick = { Repo.borrarGasto(g.id); registrado = null; Toast.makeText(this@QuickActivity, "Gasto quitado", Toast.LENGTH_SHORT).show() }) { Text("deshacer") }
                }
            }
        }
        if (!entra) {
            val frecuentes = remember(registrado) { D.frecuentes() }
            if (frecuentes.isNotEmpty()) {
                Etiqueta("Frecuentes · un toque registra")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    frecuentes.forEach { f ->
                        Ficha("${f.sub} ${dinero(f.monto)}", registrado?.let { it.categoria == f.categoria && it.subcategoria == f.sub } == true) {
                            val (g, nuevo) = D.registrarFrecuente(f.categoria, f.sub, f.monto)
                            if (nuevo) ubicarGasto(g)
                            registrado = g
                            if (embebido) alGuardar?.invoke("${f.sub} ${dinero(f.monto)} registrado")
                        }
                    }
                }
            }
        }
        Etiqueta(if (entra) "Monto" else "Otro gasto")
        CampoTexto(value = monto, onValueChange = { monto = D.limpiarMonto(it); error = null }, modifier = Modifier.fillMaxWidth(),
            prefix = { Text("$ ") }, placeholder = { Text("0") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), textStyle = MaterialTheme.typography.headlineSmall)
        Etiqueta("Categoría")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            (if (entra) c.categorias_ingreso else c.categorias).forEach { k -> Ficha(k, cat == k) { cat = if (cat == k) null else k; sub = ""; error = null } }
        }
        val subs = cat?.takeIf { !entra }?.let { D.subcategorias(it) }.orEmpty()
        if (subs.isNotEmpty()) {
            Etiqueta("Subcategoría")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                subs.forEach { s ->
                    val h = D.montoHabitual(cat!!, s.nombre)
                    Ficha(s.nombre + (h?.let { " · ${dinero(it)}" } ?: ""), sub == s.nombre) {
                        sub = if (sub == s.nombre) "" else s.nombre
                        if (sub.isNotEmpty() && h != null && (monto.isBlank() || monto == auto)) { monto = D.texto(h); auto = monto }
                    }
                }
            }
        }
        Etiqueta("Cuándo")
        SelectorHora(cuando) { cuando = it }
        if (entra) Interruptor("¿Se repite? (sueldo, beca, renta)", fijo) { fijo = it }
        else {
            Interruptor("¿Fue imprevisto?", imprevisto) { imprevisto = it }
            Interruptor("¿Se repite? (renta, suscripción, servicios)", repite) { repite = it }
        }
        CampoTexto(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Nota opcional") }, minLines = 2)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp)) }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (registrado != null && !embebido) TextButton(onClick = { terminar() }) { Text("Listo") }
            BotonPrincipal(if (entra) "Registrar ingreso" else "Registrar gasto") {
                // Un toque doble no registra dos veces.
                if (guardando) return@BotonPrincipal
                val v = monto.toDoubleOrNull()
                val k = cuando
                val fecha = k?.let { Fechas.iso(it.toLocalDate()) } ?: Fechas.hoyCalendario()
                val hora = k?.let { "%02d:%02d".format(it.hour, it.minute) } ?: Fechas.horaAhora()
                when {
                    v == null || v <= 0 -> error = "Escribe un monto mayor a cero."
                    cat == null -> error = "Elige una categoría."
                    entra -> {
                        val i = io.github.ruddy2003.vestigium.data.Ingreso(Repo.nuevoId(), fecha, hora, Math.round(v * 100) / 100.0, cat!!, nota.trim(), fijo, ts_creado = Fechas.ahoraIso())
                        Repo.guardarIngreso(i)
                        guardando = true
                        listo("Ingreso registrado: ${dinero(i.monto)} · ${i.categoria}")
                    }
                    else -> {
                        val g = Gasto(Repo.nuevoId(), fecha, hora, Math.round(v * 100) / 100.0, cat!!, imprevisto, nota.trim(), subcategoria = sub, se_repite = repite, ts_creado = Fechas.ahoraIso())
                        Repo.guardarGasto(g)
                        // La ubicación de ahora solo sirve si el gasto es de ahora.
                        if (k == null) ubicarGasto(g)
                        guardando = true
                        listo("Gasto registrado: ${dinero(g.monto)} · ${g.categoria}" + (if (sub.isNotBlank()) " → $sub" else ""))
                    }
                }
            }
        }
        if (corrigiendo) registrado?.let { r ->
            io.github.ruddy2003.vestigium.ui.pantallas.DialogoGasto(Repo.gasto(r.id) ?: r) { corrigiendo = false; registrado = Repo.gasto(r.id) ?: registrado }
        }
    }

    @Composable
    private fun SuenoRapido() {
        // El sueño va al día del despertar (la fecha del calendario), como las horas del reloj (H-NAV-7).
        val fecha = Fechas.hoyCalendario()
        var d by remember { mutableStateOf(Repo.dia(fecha)) }
        fun guardar(n: io.github.ruddy2003.vestigium.data.Dia) { d = n; Repo.guardarDia(n) }
        Encabezado("Tu sueño", "Primero cuéntalo, antes de levantarte: los detalles se van en minutos. Lo demás, después.", fecha)
        // La calidad que guardó el toque en el widget: se ve y se quita si fue sin querer (N-15).
        val delWidget = remember { intent.getIntExtra(CALIDAD, 0).takeIf { it in 1..5 } }
        if (delWidget != null && d.sueno_calidad == delWidget) Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Guardado con el toque: calidad $delWidget.", style = MaterialTheme.typography.labelLarge, modifier = Modifier.weight(1f))
            TextButton(onClick = { guardar(d.copy(sueno_calidad = null)) }) { Text("quitar") }
        }
        // La narración arriba, en el libro de noche del tema (es lo que se olvida primero); lo demás, debajo.
        io.github.ruddy2003.vestigium.ui.FormularioSueno(fecha, d, narracionPrimero = true) { guardar(it) }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.End) { BotonPrincipal("Listo") { terminar() } }
    }

    @Composable
    private fun Minimo() {
        val fecha = Fechas.diaLogico()
        var d by remember { mutableStateOf(Repo.dia(fecha)) }
        Encabezado("Día difícil", "Solo un número. Un día registrado así vale mucho más que un día vacío.")
        Escala(d.valencia, 1, 7, Catalogo.ANCLAS["valencia"]) { v -> d = d.copy(valencia = v, dia_dificil = true); Repo.guardarDia(d) }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.End) {
            Spacer(Modifier.width(1.dp))
            BotonPrincipal("Listo", habilitado = d.valencia != null) { terminar() }
        }
    }
}

// =====================================================================
//  La hoja del registro rápido, fuera de la Activity, para que se pueda capturar en Paparazzi con datos de ejemplo.
// =====================================================================

/**
 * El marco de la hoja del registro rápido: el borde de arriba con la silueta del tema (frontón, arco polilobulado, alero de
 * pagoda, pestaña de carpeta, cartel arrancado…), la textura del fondo, el adorno de ese borde y la banda del tema.
 * [contenido] va debajo y se desplaza; el adorno se queda quieto arriba.
 */
@Composable
internal fun HojaQuick(modifier: Modifier = Modifier, contenido: @Composable ColumnScope.() -> Unit) {
    val t = Estilo.tema
    val e = MaterialTheme.colorScheme
    Surface(modifier = modifier, shape = formaHojaQuick(t), color = e.background, border = BorderStroke(1.dp, e.outline)) {
        Column(
            Modifier.fondoTema(t, e).drawWithContent { drawContent(); adornoHojaQuick(t, e) }
                .padding(start = 18.dp, end = 18.dp, bottom = 18.dp, top = 12.dp + perfilHojaQuick(t))
                .verticalScroll(rememberScrollState()),
        ) {
            Meandro(Modifier.padding(bottom = 12.dp))
            contenido()
        }
    }
}

/**
 * El título de cada vista de la hoja, con el nombre que le da el tema (`Estilo.pantallaEnLinea`). Debajo, una línea con el
 * nombre en español (si el tema lo cambia) o el folio del tema, y [linea] (hora y día); luego [aviso] y la ayuda [sub].
 */
@Composable
internal fun EncabezadoQuick(titulo: String, linea: String, sub: String, aviso: @Composable () -> Unit = {}) {
    val (nombre, espanol, extra) = Estilo.pantallaEnLinea(titulo, Romano.latin(titulo))
    Text(nombre, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onBackground)
    val cabeza = extra?.let { "$it · " } ?: (Estilo.comentario + (espanol?.let { it.lowercase() + " · " } ?: ""))
    Text(cabeza + linea, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
    aviso()
    Ayuda(sub, Modifier.padding(top = 2.dp, bottom = 12.dp))
}

/** El menú del botón ⊕: «Ánimo ahora» grande y [botones] (texto y destino) de dos en dos, con la forma del tema. */
@Composable
internal fun MenuQuick(botones: List<Pair<String, String>>, alElegir: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    val forma = formaPiezaQuick(Estilo.tema)
    Surface(onClick = { alElegir(QuickActivity.M_ANIMO) }, modifier = Modifier.fillMaxWidth().heightIn(min = 76.dp), shape = forma, color = cs.primary) {
        Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Ánimo ahora", style = MaterialTheme.typography.titleLarge, color = cs.onPrimary)
            Text("cómo estás, qué sientes y qué piensas", style = MaterialTheme.typography.labelMedium, color = cs.onPrimary.copy(alpha = 0.85f))
        }
    }
    FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = 2) {
        botones.forEach { (texto, destino) ->
            Surface(onClick = { alElegir(destino) }, modifier = Modifier.weight(1f).heightIn(min = 56.dp), shape = forma, color = cs.surface, border = BorderStroke(1.dp, cs.outline)) {
                Box(contentAlignment = Alignment.Center) { Text(texto, style = MaterialTheme.typography.titleSmall) }
            }
        }
    }
}

/** Una forma que se traza en píxeles con [u] = píxeles por dp, para que las medidas no dependan del tamaño de la pieza. */
private fun formaDp(trazo: Path.(w: Float, h: Float, u: Float) -> Unit): Shape = object : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline =
        Outline.Generic(Path().apply { trazo(size.width, size.height, density.density) })
}

/** Cuánto baja, desde lo más alto, el borde de arriba de la hoja: lo que la hoja deja libre arriba para su adorno. */
private fun perfilHojaQuick(t: TemaId): Dp = when (t) {
    TemaId.ROMANO -> 12.dp
    TemaId.URBE -> 16.dp
    TemaId.CIBER -> 4.dp
    TemaId.NEON -> 6.dp
    TemaId.HELENO -> 14.dp
    TemaId.EGIPTO -> 16.dp
    TemaId.MEDIEVAL -> 10.dp
    TemaId.TEMPLARIO -> 9.dp
    TemaId.DISTOPIA -> 12.dp
    TemaId.REFUGIO -> 6.dp
    TemaId.ORDEN -> 6.dp
    TemaId.VAPOR -> 7.dp
    TemaId.LABORATORIO -> 8.dp
    TemaId.COLONOS -> 4.dp
    TemaId.NAPOLEON -> 14.dp
    TemaId.DECO -> 16.dp
    TemaId.BOTANICO -> 8.dp
    TemaId.RUINAS -> 10.dp
    TemaId.NAVIDAD -> 7.dp
    TemaId.SEDA -> 14.dp
    TemaId.EDO -> 16.dp
    TemaId.GOTICO -> 18.dp
    TemaId.ARCANO -> 10.dp
    TemaId.ESPACIO -> 12.dp
    TemaId.ABISMO -> 9.dp
    TemaId.ANDALUS -> 18.dp
    TemaId.MESO -> 16.dp
    TemaId.MONGOL -> 14.dp
    TemaId.RENACIMIENTO -> 6.dp
    TemaId.PIRATA -> 8.dp
    TemaId.URSS -> 20.dp
    TemaId.REVOLUCION -> 9.dp
    TemaId.ANTIGUAS -> 16.dp
    TemaId.CUEVA -> 18.dp
}

/** La silueta de la hoja: solo cambia el borde de arriba; abajo llega al borde de la pantalla. */
private fun formaHojaQuick(t: TemaId): Shape = when (t) {
    // Cartela de esquinas cóncavas, como una placa con inscripción.
    TemaId.ROMANO -> formaDp { w, h, u ->
        val r = 12 * u
        moveTo(r, 0f); lineTo(w - r, 0f); quadraticTo(w - r, r, w, r); lineTo(w, h); lineTo(0f, h); lineTo(0f, r); quadraticTo(r, r, r, 0f); close()
    }
    // El arco rebajado de un portón, de lado a lado.
    TemaId.URBE -> formaDp { w, h, u ->
        val f = 16 * u
        moveTo(0f, f); quadraticTo(w / 2, -f, w, f); lineTo(w, h); lineTo(0f, h); close()
    }
    // Ventana de consola con una esquina cortada.
    TemaId.CIBER -> formaDp { w, h, u ->
        val c = 18 * u
        moveTo(0f, 0f); lineTo(w - c, 0f); lineTo(w, c); lineTo(w, h); lineTo(0f, h); close()
    }
    // La placa del letrero, con las esquinas de arriba redondas.
    TemaId.NEON -> formaDp { w, h, u ->
        val c = 22 * u
        moveTo(0f, c); quadraticTo(0f, 0f, c, 0f); lineTo(w - c, 0f); quadraticTo(w, 0f, w, c); lineTo(w, h); lineTo(0f, h); close()
    }
    // Frontón de templo de lado a lado.
    TemaId.HELENO -> formaDp { w, h, u ->
        val f = 14 * u
        moveTo(0f, f); lineTo(w / 2, 0f); lineTo(w, f); lineTo(w, h); lineTo(0f, h); close()
    }
    // Estela de remate redondo: un arco rebajado de lado a lado.
    TemaId.EGIPTO -> formaDp { w, h, u ->
        val f = 16 * u
        moveTo(0f, h); lineTo(0f, f); quadraticTo(w / 2, -f * 0.9f, w, f); lineTo(w, h); close()
    }
    // Borde de pergamino que ondula, como un rollo a medio abrir.
    TemaId.MEDIEVAL -> formaDp { w, h, u ->
        val a = 5 * u
        moveTo(0f, a)
        var x = 0f
        while (x < w) { lineTo(x, a + a * sin(2 * PI * 3 * x / w).toFloat()); x += 3 * u }
        lineTo(w, a); lineTo(w, h); lineTo(0f, h); close()
    }
    // El adarve: merlones y troneras en el borde de arriba.
    TemaId.TEMPLARIO -> formaDp { w, h, u ->
        val m = 16 * u; val a = 9 * u
        moveTo(0f, h); lineTo(0f, 0f)
        var x = 0f; var k = 0
        while (x + m < w) { val y = if (k % 2 == 0) 0f else a; lineTo(x, y); lineTo(x + m, y); x += maxOf(m, 1f); k++ }
        lineTo(x, if (k % 2 == 0) 0f else a); lineTo(w, if (k % 2 == 0) 0f else a); lineTo(w, h); close()
    }
    // Carpeta de expediente con su pestaña a la izquierda.
    TemaId.DISTOPIA -> formaDp { w, h, u ->
        val p = 12 * u; val a = w * 0.38f
        moveTo(0f, 0f); lineTo(a, 0f); lineTo(a + p, p); lineTo(w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // Pantalla de tubo, de esquinas muy redondas.
    TemaId.REFUGIO -> formaDp { w, h, u ->
        addRoundRect(RoundRect(0f, 0f, w, h, topLeftCornerRadius = CornerRadius(26 * u), topRightCornerRadius = CornerRadius(26 * u),
            bottomRightCornerRadius = CornerRadius.Zero, bottomLeftCornerRadius = CornerRadius.Zero))
    }
    // Compuerta de esquinas cortadas.
    TemaId.ORDEN -> formaDp { w, h, u ->
        val c = 14 * u
        moveTo(c, 0f); lineTo(w - c, 0f); lineTo(w, c); lineTo(w, h); lineTo(0f, h); lineTo(0f, c); close()
    }
    // El billete de tren: las dos esquinas de arriba recortadas hacia dentro.
    TemaId.VAPOR -> formaDp { w, h, u ->
        val c = 13 * u
        moveTo(c, 0f); lineTo(w - c, 0f); quadraticTo(w - c, c, w, c); lineTo(w, h); lineTo(0f, h); lineTo(0f, c); quadraticTo(c, c, c, 0f); close()
    }
    // Vaso de precipitados: el pico para verter, a la izquierda.
    TemaId.LABORATORIO -> formaDp { w, h, u ->
        val p = 8 * u; val r = 3 * u
        moveTo(0f, 0f); quadraticTo(6 * u, p, 22 * u, p); lineTo(w - r, p); quadraticTo(w, p, w, p + r); lineTo(w, h); lineTo(0f, h); close()
    }
    // Boleto de gaceta: una muesca a cada lado.
    TemaId.COLONOS -> formaDp { w, h, u ->
        val r = 6 * u; val m = 16 * u
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, m - r)
        arcTo(Rect(Offset(w, m), r), 270f, -180f, false)
        lineTo(w, h); lineTo(0f, h); lineTo(0f, m + r)
        arcTo(Rect(Offset(0f, m), r), 90f, -180f, false)
        close()
    }
    // Chapa con un medallón al centro, para la estrella.
    TemaId.NAPOLEON -> formaDp { w, h, u ->
        val p = 14 * u
        moveTo(0f, p); lineTo(w / 2 - p, p)
        arcTo(Rect(Offset(w / 2, p), p), 180f, 180f, false)
        lineTo(w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // La hoja sube al centro en tres escalones, como la corona de una torre.
    TemaId.DECO -> formaDp { w, h, u ->
        val p = 16 * u; val a = p / 3; val s = 14 * u
        moveTo(0f, p); lineTo(w / 2 - 3 * s, p); lineTo(w / 2 - 3 * s, 2 * a); lineTo(w / 2 - 2 * s, 2 * a); lineTo(w / 2 - 2 * s, a)
        lineTo(w / 2 - s, a); lineTo(w / 2 - s, 0f); lineTo(w / 2 + s, 0f); lineTo(w / 2 + s, a); lineTo(w / 2 + 2 * s, a)
        lineTo(w / 2 + 2 * s, 2 * a); lineTo(w / 2 + 3 * s, 2 * a); lineTo(w / 2 + 3 * s, p); lineTo(w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // Hoja: una esquina muy redonda y la otra en punta.
    TemaId.BOTANICO -> formaDp { w, h, u ->
        addRoundRect(RoundRect(0f, 0f, w, h, topLeftCornerRadius = CornerRadius(30 * u), topRightCornerRadius = CornerRadius(2 * u),
            bottomRightCornerRadius = CornerRadius.Zero, bottomLeftCornerRadius = CornerRadius.Zero))
    }
    // Una losa de hormigón con el borde de arriba quebrado y la esquina derecha desprendida en escalones.
    TemaId.RUINAS -> formaDp { w, h, u ->
        moveTo(0f, 6 * u); lineTo(w * 0.3f, 3 * u); lineTo(w * 0.34f, 7 * u); lineTo(w * 0.62f, 2 * u); lineTo(w - 26 * u, 4 * u)
        lineTo(w - 18 * u, 10 * u); lineTo(w - 10 * u, 8 * u); lineTo(w, 16 * u); lineTo(w, h); lineTo(0f, h); close()
    }
    // Glaseado de nieve: festones arriba.
    TemaId.NAVIDAD -> formaDp { w, h, u ->
        val f = 7 * u; val n = (w / (44 * u)).toInt().coerceAtLeast(3); val paso = w / n
        moveTo(0f, f)
        for (i in 0 until n) quadraticTo(i * paso + paso / 2, -f, (i + 1) * paso, f)
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Alero de pagoda con las puntas hacia arriba.
    TemaId.SEDA -> formaDp { w, h, u ->
        val p = 12 * u
        moveTo(0f, 0f); quadraticTo(w * 0.1f, p, w * 0.28f, p); lineTo(w * 0.72f, p); quadraticTo(w * 0.9f, p, w, 0f)
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Karahafu: el alero ondulado de las puertas de templos y castillos, que sube en curva hasta el centro.
    TemaId.EDO -> formaDp { w, h, u ->
        val p = 16 * u
        moveTo(0f, p); cubicTo(w * 0.25f, p, w * 0.38f, p * 0.9f, w * 0.44f, p * 0.35f); quadraticTo(w * 0.5f, -p * 0.05f, w * 0.56f, p * 0.35f)
        cubicTo(w * 0.62f, p * 0.9f, w * 0.75f, p, w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // Gablete apuntado al centro, como sobre una portada.
    TemaId.GOTICO -> formaDp { w, h, u ->
        val a = 18 * u; val b = 36 * u
        moveTo(0f, a); lineTo(w / 2 - b, a)
        quadraticTo(w / 2 - b, a * 0.3f, w / 2, 0f); quadraticTo(w / 2 + b, a * 0.3f, w / 2 + b, a)
        lineTo(w, a); lineTo(w, h); lineTo(0f, h); close()
    }
    // La hoja del legajo: la orilla de arriba rasgada a mano.
    TemaId.ARCANO -> formaDp { w, h, u ->
        val d = 5 * u; var x = 0f; var k = 0
        moveTo(0f, d)
        while (x < w) { x = minOf(x + 11 * u, w); lineTo(x, if (k % 2 == 0) 0f else if (k % 3 == 0) d * 1.6f else d); k++ }
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Consola de mando: la parte central más alta.
    TemaId.ESPACIO -> formaDp { w, h, u ->
        val p = 12 * u; val a = w * 0.3f; val r = 14 * u
        moveTo(0f, p + r); quadraticTo(0f, p, r, p); lineTo(a, p); lineTo(a + p, 0f); lineTo(w - a - p, 0f); lineTo(w - a, p)
        lineTo(w - r, p); quadraticTo(w, p, w, p + r); lineTo(w, h); lineTo(0f, h); close()
    }
    // La superficie del mar: el borde de arriba es una ola de cuatro crestas.
    TemaId.ABISMO -> formaDp { w, h, u ->
        val p = 8 * u; val n = 4
        moveTo(0f, p)
        for (i in 0 until n) {
            val x0 = w * i / n; val x1 = w * (i + 1) / n
            quadraticTo((x0 + x1) / 2, if (i % 2 == 0) -p * 0.4f else p * 2.2f, x1, p)
        }
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Arco polilobulado al centro: cinco lóbulos que suben hacia la clave.
    TemaId.ANDALUS -> formaDp { w, h, u ->
        val p = 18 * u; val r = 8 * u; val x0 = w / 2 - 5 * r
        moveTo(0f, p); lineTo(x0, p)
        floatArrayOf(2f, 6f, 8f, 6f, 2f).forEachIndexed { i, k -> arcTo(Rect(Offset(x0 + r + 2 * r * i, p - k * u), r), 180f, 180f, false) }
        lineTo(x0 + 10 * r, p); lineTo(w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // Un templo sobre la hoja: al centro, el borde sube en gradas como el perfil de una pirámide.
    TemaId.MESO -> formaDp { w, h, u ->
        val p = 16 * u; val g = 4 * u; val s = 10 * u; val x0 = w / 2 - 4 * s
        moveTo(0f, p); lineTo(x0, p)
        for (k in 0 until 3) { lineTo(x0 + k * s, p - (k + 1) * g); lineTo(x0 + (k + 1) * s, p - (k + 1) * g) }
        for (k in 3 downTo 1) { lineTo(x0 + (9 - k) * s, p - k * g); lineTo(x0 + (9 - k) * s, p - (k - 1) * g) }
        lineTo(w, p); lineTo(w, h); lineTo(0f, h); close()
    }
    // Esquinas escalonadas, como la orilla de un fieltro bordado.
    TemaId.MONGOL -> formaDp { w, h, u ->
        val s = 7 * u
        moveTo(0f, 2 * s); lineTo(s, 2 * s); lineTo(s, s); lineTo(2 * s, s); lineTo(2 * s, 0f)
        lineTo(w - 2 * s, 0f); lineTo(w - 2 * s, s); lineTo(w - s, s); lineTo(w - s, 2 * s); lineTo(w, 2 * s)
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Hoja vista en perspectiva: un trapecio.
    TemaId.RENACIMIENTO -> formaDp { w, h, u ->
        val d = 14 * u
        moveTo(d, 0f); lineTo(w - d, 0f); lineTo(w, h); lineTo(0f, h); close()
    }
    // La lona de una vela tensada: el borde de arriba se comba hacia adentro entre dos ollaos.
    TemaId.PIRATA -> formaDp { w, h, u ->
        val f = 8 * u
        moveTo(0f, 0f); quadraticTo(w / 2, f * 2, w, 0f); lineTo(w, h); lineTo(0f, h); close()
    }
    // Diagonal de cartel: el borde baja de izquierda a derecha.
    TemaId.URSS -> formaDp { w, h, u -> moveTo(0f, 0f); lineTo(w, 20 * u); lineTo(w, h); lineTo(0f, h); close() }
    // Cartel arrancado: el borde de arriba en jirones.
    TemaId.REVOLUCION -> formaDp { w, h, u ->
        val f = 9 * u; val paso = 7 * u
        val jirones = floatArrayOf(0.1f, 0.9f, 0.3f, 1f, 0f, 0.7f, 0.4f, 0.95f, 0.15f, 0.6f, 0.05f)
        moveTo(0f, f * 0.5f)
        var x = 0f; var i = 0
        while (x < w) { x = (x + paso).coerceAtMost(w); lineTo(x, f * jirones[i % jirones.size]); i++ }
        lineTo(w, h); lineTo(0f, h); close()
    }
    // Losa de piedra tallada a golpes.
    TemaId.ANTIGUAS -> formaDp { w, h, u ->
        moveTo(0f, 16 * u); lineTo(16 * u, 0f); lineTo(w * 0.42f, 3 * u); lineTo(w * 0.58f, 0f); lineTo(w - 16 * u, 4 * u); lineTo(w, 16 * u)
        lineTo(w, h); lineTo(0f, h); close()
    }
    // El borde de arriba de una laja partida: ondula, más alto a un lado.
    TemaId.CUEVA -> formaDp { w, h, u ->
        moveTo(0f, 14 * u); quadraticTo(w * 0.2f, 2 * u, w * 0.45f, 8 * u); quadraticTo(w * 0.7f, 14 * u, w * 0.86f, 4 * u); quadraticTo(w * 0.95f, 0f, w, 10 * u)
        lineTo(w, h); lineTo(0f, h); close()
    }
}

/** El adorno del borde de arriba de la hoja; cabe en el aire que deja [perfilHojaQuick] más 12 dp. */
private fun DrawScope.adornoHojaQuick(t: TemaId, e: ColorScheme) {
    val w = size.width; val u = 1.dp.toPx()
    when (t) {
        // Filete interior con una cola de milano en cada punta, como una tabula ansata.
        TemaId.ROMANO -> {
            val y = 5 * u; val x0 = 16 * u; val x1 = w - 16 * u; val col = e.primary.copy(alpha = 0.45f)
            drawLine(col, Offset(x0 + 6 * u, y), Offset(x1 - 6 * u, y), 0.8f * u)
            drawPath(Path().apply { moveTo(x0, y); lineTo(x0 + 6 * u, y - 3.5f * u); lineTo(x0 + 6 * u, y + 3.5f * u); close() }, col)
            drawPath(Path().apply { moveTo(x1, y); lineTo(x1 - 6 * u, y - 3.5f * u); lineTo(x1 - 6 * u, y + 3.5f * u); close() }, col)
        }
        // La moldura que sigue el arco por dentro y la clave en lo más alto.
        TemaId.URBE -> {
            val f = 16 * u; val col = e.primary.copy(alpha = 0.45f)
            drawPath(Path().apply { moveTo(10 * u, f + 4 * u); quadraticTo(w / 2, -f + 10 * u, w - 10 * u, f + 4 * u) }, col, style = Stroke(0.9f * u))
            drawPath(Path().apply { moveTo(w / 2 - 7 * u, 2 * u); lineTo(w / 2 + 7 * u, 2 * u); lineTo(w / 2 + 5 * u, 11 * u); lineTo(w / 2 - 5 * u, 11 * u); close() }, col)
        }
        // Barra de estado de la consola: la línea, tres rayas de señal y el cursor.
        TemaId.CIBER -> {
            val y = 7 * u; val x = w * 0.55f
            drawLine(e.primary.copy(alpha = 0.5f), Offset(10 * u, y), Offset(x, y), u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(6 * u, 3 * u)))
            (0 until 3).forEach { k -> drawRect(e.primary.copy(alpha = 0.75f - k * 0.2f), Offset(x + (6 + k * 6) * u, y - 2.5f * u), Size(4 * u, 5 * u)) }
            drawRect(e.secondary, Offset(x + 26 * u, y - 3 * u), Size(7 * u, 6 * u))
        }
        // El tubo magenta a lo largo del borde de arriba, con su halo, y el foquito verde de «abierto».
        TemaId.NEON -> {
            val y = 6 * u
            drawLine(e.primary.copy(alpha = 0.25f), Offset(24 * u, y), Offset(w - 24 * u, y), 5 * u, StrokeCap.Round)
            drawLine(e.primary, Offset(24 * u, y), Offset(w - 24 * u, y), 1.4f * u, StrokeCap.Round)
            drawCircle(e.tertiary, 2.4f * u, Offset(w - 14 * u, y + 8 * u))
        }
        // El tímpano del frontón con su roseta y el arquitrabe debajo.
        TemaId.HELENO -> {
            val f = 14 * u; val col = e.tertiary.copy(alpha = 0.7f)
            drawPath(Path().apply { moveTo(12 * u, f + u); lineTo(w / 2, 4 * u); lineTo(w - 12 * u, f + u) }, col, style = Stroke(u))
            drawLine(col, Offset(12 * u, f + 4 * u), Offset(w - 12 * u, f + 4 * u), u)
            drawCircle(e.tertiary, 2.2f * u, Offset(w / 2, 10.5f * u))
        }
        // El disco solar alado bajo el arco de la estela.
        TemaId.EGIPTO -> {
            val c = Offset(w / 2, 10 * u); val col = e.primary.copy(alpha = 0.7f)
            listOf(-1f, 1f).forEach { s ->
                drawPath(Path().apply {
                    moveTo(c.x + s * 4 * u, c.y - 1.5f * u); quadraticTo(c.x + s * 14 * u, c.y - 5 * u, c.x + s * 26 * u, c.y - 2 * u)
                    lineTo(c.x + s * 23 * u, c.y + 1.5f * u); quadraticTo(c.x + s * 13 * u, c.y + 1 * u, c.x + s * 4 * u, c.y + 2.5f * u); close()
                }, col)
            }
            drawCircle(e.secondary, 3.4f * u, c)
        }
        // Doble filete de rúbrica y tres puntos de oro.
        TemaId.MEDIEVAL -> {
            val y = 14 * u
            drawLine(e.primary.copy(alpha = 0.7f), Offset(14 * u, y), Offset(w - 14 * u, y), 1.4f * u)
            drawLine(e.primary.copy(alpha = 0.5f), Offset(14 * u, y + 3 * u), Offset(w - 14 * u, y + 3 * u), 0.6f * u)
            listOf(-8f, 0f, 8f).forEach { dx -> drawCircle(e.tertiary, 1.8f * u, Offset(w / 2 + dx * u, 9 * u)) }
        }
        // La línea de acero bajo las almenas y la cruz patada en medio.
        TemaId.TEMPLARIO -> {
            drawLine(e.secondary.copy(alpha = 0.6f), Offset(10 * u, 15 * u), Offset(w - 10 * u, 15 * u), 1.2f * u)
            drawCircle(e.surface, 7 * u, Offset(w / 2, 15 * u))
            drawPath(cruzPatada(Offset(w / 2, 15 * u), 6 * u), e.primary)
        }
        // Renglones del rótulo en la pestaña y el ojo que vigila a la derecha.
        TemaId.DISTOPIA -> {
            val a = w * 0.38f; val tinta = e.onBackground.copy(alpha = 0.45f)
            listOf(4f, 7f, 10f).forEachIndexed { k, y -> drawLine(tinta, Offset(12 * u, y * u), Offset(a - (12 + k * 14) * u, y * u), 0.8f * u) }
            val c = Offset(w - 30 * u, 18 * u)
            drawPath(Path().apply {
                moveTo(c.x - 11 * u, c.y); quadraticTo(c.x, c.y - 9 * u, c.x + 11 * u, c.y); quadraticTo(c.x, c.y + 9 * u, c.x - 11 * u, c.y); close()
            }, tinta, style = Stroke(1.2f * u))
            drawCircle(e.primary, 2.6f * u, c)
        }
        // Rejilla de ventilación al centro y una luz piloto, como en un aparato de los cincuenta.
        TemaId.REFUGIO -> {
            (0 until 3).forEach { k -> drawRoundRect(e.primary.copy(alpha = 0.45f), Offset(w / 2 - 22 * u, (4 + k * 4) * u), Size(44 * u, 2 * u), CornerRadius(u)) }
            drawCircle(e.secondary.copy(alpha = 0.85f), 2.5f * u, Offset(w / 2 + 32 * u, 8 * u))
        }
        // Remaches a lo largo del borde.
        TemaId.ORDEN -> {
            var x = 24 * u
            while (x < w - 20 * u) {
                drawCircle(e.secondary.copy(alpha = 0.75f), 1.8f * u, Offset(x, 8 * u))
                drawCircle(e.background.copy(alpha = 0.5f), 0.7f * u, Offset(x - 0.5f * u, 7.5f * u))
                x += 34 * u
            }
        }
        // La línea perforada del talonario de billetes y, debajo, el filete de latón.
        TemaId.VAPOR -> {
            var x = 20 * u
            while (x < w - 18 * u) { drawCircle(e.onSurface.copy(alpha = 0.25f), 1.2f * u, Offset(x, 6 * u)); x += 6 * u }
            drawLine(e.primary.copy(alpha = 0.7f), Offset(18 * u, 10 * u), Offset(w - 18 * u, 10 * u), 1.4f * u)
        }
        // Graduación a lo largo del borde.
        TemaId.LABORATORIO -> {
            var x = 30 * u; var n = 0
            while (x < w - 10 * u) { drawLine(e.secondary.copy(alpha = 0.6f), Offset(x, 10 * u), Offset(x, (if (n % 5 == 0) 17 else 13) * u), 0.8f * u); x += 8 * u; n++ }
        }
        // Raya inglesa de gaceta: gruesa y fina.
        TemaId.COLONOS -> {
            drawRect(e.onBackground.copy(alpha = 0.8f), Offset(12 * u, 5 * u), Size(w - 24 * u, 1.6f * u))
            drawRect(e.onBackground.copy(alpha = 0.8f), Offset(12 * u, 8.5f * u), Size(w - 24 * u, 0.6f * u))
        }
        // Estrella de oro en el medallón y un filete de oro a cada lado.
        TemaId.NAPOLEON -> {
            estrellaHoja(Offset(w / 2, 10 * u), 7 * u, e.tertiary)
            drawLine(e.tertiary.copy(alpha = 0.6f), Offset(12 * u, 19 * u), Offset(w / 2 - 18 * u, 19 * u), u)
            drawLine(e.tertiary.copy(alpha = 0.6f), Offset(w / 2 + 18 * u, 19 * u), Offset(w - 12 * u, 19 * u), u)
        }
        // El abanico de oro en la corona de la hoja y tres líneas de velocidad a cada lado.
        TemaId.DECO -> {
            val b = Offset(w / 2, 15 * u); val r = 10 * u
            for (i in 0 until 9) if (i % 2 == 0) drawArc(e.primary, 180f + i * 20f, 20f, true, Offset(b.x - r, b.y - r), Size(2 * r, 2 * r))
            drawArc(e.secondary, 180f, 180f, true, Offset(b.x - r * 0.35f, b.y - r * 0.35f), Size(r * 0.7f, r * 0.7f))
            for (k in 0..2) {
                val y = (20 + k * 3) * u
                drawLine(e.primary.copy(alpha = 0.6f - k * 0.15f), Offset((12 + k * 10) * u, y), Offset(w / 2 - 46 * u, y), 0.8f * u)
                drawLine(e.primary.copy(alpha = 0.6f - k * 0.15f), Offset(w / 2 + 46 * u, y), Offset(w - (12 + k * 10) * u, y), 0.8f * u)
            }
        }
        // Un tallo con hojitas a lo largo del borde.
        TemaId.BOTANICO -> {
            val y = 9 * u; val col = e.primary.copy(alpha = 0.7f); val fin = w * 0.45f
            drawLine(col, Offset(34 * u, y), Offset(fin, y), 0.9f * u)
            var x = 40 * u; var k = 0
            while (x < fin - 6 * u) {
                val dy = if (k % 2 == 0) -3.2f * u else 3.2f * u
                drawOval(col, Offset(x - 3 * u, y + dy - 1.8f * u), Size(6 * u, 3.6f * u)); x += 9 * u; k++
            }
        }
        TemaId.RUINAS -> {
            // Una guía de hiedra que cuelga del borde quebrado, con hojas de cinco puntas, y una grieta que baja.
            val col = e.primary.copy(alpha = 0.7f)
            drawLine(col, Offset(24 * u, 6 * u), Offset(30 * u, 40 * u), 0.9f * u)
            listOf(12f to 1f, 20f to -1f, 28f to 1f, 36f to -1f).forEach { (yy, lado) ->
                val q = Offset(24 * u + yy * 0.18f * u + lado * 5 * u, yy * u); val r = 5 * u
                drawPath(Path().apply {
                    moveTo(q.x, q.y - r * 0.5f); lineTo(q.x - r * 0.55f, q.y - r * 0.35f); lineTo(q.x - r * 0.8f, q.y + r * 0.05f); lineTo(q.x - r * 0.32f, q.y + r * 0.05f)
                    lineTo(q.x, q.y + r * 0.5f); lineTo(q.x + r * 0.32f, q.y + r * 0.05f); lineTo(q.x + r * 0.8f, q.y + r * 0.05f); lineTo(q.x + r * 0.55f, q.y - r * 0.35f); close()
                }, col)
            }
            drawPath(Path().apply { moveTo(w * 0.62f, 2 * u); lineTo(w * 0.6f, 10 * u); lineTo(w * 0.63f, 16 * u); lineTo(w * 0.61f, 24 * u) }, e.onSurface.copy(alpha = 0.3f), style = Stroke(0.8f * u))
        }
        // Nieve en cada festón y una baya roja entre dos.
        TemaId.NAVIDAD -> {
            val n = (w / (44 * u)).toInt().coerceAtLeast(3); val paso = w / n
            (0 until n).forEach { i ->
                drawCircle(Color.White.copy(alpha = 0.85f), 2.2f * u, Offset(i * paso + paso / 2, 4 * u))
                if (i < n - 1) drawCircle(e.primary.copy(alpha = 0.75f), 1.6f * u, Offset((i + 1) * paso, 12 * u))
            }
        }
        // El alero a tinta y un sello de cinabrio.
        TemaId.SEDA -> {
            drawPath(Path().apply { moveTo(4 * u, 3 * u); quadraticTo(w * 0.1f, 15 * u, w * 0.28f, 15 * u); lineTo(w * 0.72f, 15 * u); quadraticTo(w * 0.9f, 15 * u, w - 4 * u, 3 * u) },
                e.primary.copy(alpha = 0.5f), style = Stroke(u))
            drawRect(e.secondary, Offset(w - 40 * u, 17 * u), Size(8 * u, 8 * u))
            drawRect(e.background, Offset(w - 38.5f * u, 18.5f * u), Size(5 * u, 5 * u), style = Stroke(0.8f * u))
        }
        TemaId.EDO -> {
            // El alero repetido por dentro y, debajo, las cabezas de las vigas (taruki) en fila.
            val p = 19 * u
            drawPath(Path().apply { moveTo(0f, p); cubicTo(w * 0.25f, p, w * 0.38f, p * 0.92f, w * 0.44f, p * 0.5f); quadraticTo(w * 0.5f, p * 0.2f, w * 0.56f, p * 0.5f); cubicTo(w * 0.62f, p * 0.92f, w * 0.75f, p, w, p) },
                e.primary.copy(alpha = 0.5f), style = Stroke(u))
            var x = 10 * u; while (x < w - 6 * u) { if (kotlin.math.abs(x - w / 2) > w * 0.08f) drawCircle(e.primary.copy(alpha = 0.35f), 1.3f * u, Offset(x, p + 4 * u)); x += 14 * u }
        }
        // Trébol de tracería bajo la punta del gablete y un filete.
        TemaId.GOTICO -> {
            val c = Offset(w / 2, 12 * u); val r = 3 * u
            listOf(Offset(0f, -r), Offset(-r * 0.9f, r * 0.6f), Offset(r * 0.9f, r * 0.6f)).forEach { o -> drawCircle(e.tertiary, r, c + o, style = Stroke(u)) }
            drawLine(e.tertiary.copy(alpha = 0.5f), Offset(10 * u, 23 * u), Offset(w - 10 * u, 23 * u), 0.8f * u)
        }
        // Dos tiras de cinta que sujetan la hoja y, al centro, el ojo del signo entre dos rayas a pulso.
        TemaId.ARCANO -> {
            listOf(Offset(24 * u, 6 * u) to -24f, Offset(w - 24 * u, 6 * u) to 24f).forEach { (o, g) ->
                rotate(g, o) { drawRect(e.secondary.copy(alpha = 0.25f), Offset(o.x - 14 * u, o.y - 4 * u), Size(28 * u, 8 * u)) }
            }
            val c = Offset(w / 2, 14 * u)
            drawPath(Path().apply { moveTo(c.x - 7 * u, c.y); quadraticTo(c.x, c.y - 6 * u, c.x + 7 * u, c.y); quadraticTo(c.x, c.y + 6 * u, c.x - 7 * u, c.y); close() }, e.onSurface.copy(alpha = 0.55f), style = Stroke(u))
            drawOval(e.tertiary, Offset(c.x - 1 * u, c.y - 2.6f * u), Size(2 * u, 5.2f * u))
            drawLine(e.onSurface.copy(alpha = 0.3f), Offset(10 * u, c.y), Offset(c.x - 10 * u, c.y + 1 * u), 0.8f * u)
            drawLine(e.onSurface.copy(alpha = 0.3f), Offset(c.x + 10 * u, c.y + 1 * u), Offset(w - 10 * u, c.y - 0.5f * u), 0.8f * u)
        }
        // Tres luces de consola y una línea de telemetría.
        TemaId.ESPACIO -> {
            listOf(e.primary, e.tertiary, e.secondary).forEachIndexed { k, col -> drawCircle(col, 2.4f * u, Offset(w / 2 + (k - 1) * 14 * u, 6 * u)) }
            drawLine(e.primary.copy(alpha = 0.45f), Offset(16 * u, 18 * u), Offset(w - 16 * u, 18 * u), 0.8f * u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(2 * u, 4 * u)))
        }
        // Tres burbujas que suben en el centro y, debajo, la línea de flotación.
        TemaId.ABISMO -> {
            listOf(Offset(-6f, 16f) to 2.2f, Offset(3f, 12f) to 3f, Offset(-1f, 6f) to 1.6f).forEach { (o, r) ->
                drawCircle(e.tertiary, r * u, Offset(w / 2 + o.x * u, o.y * u), style = Stroke(0.9f * u))
            }
            drawLine(e.primary.copy(alpha = 0.45f), Offset(16 * u, 22 * u), Offset(w - 16 * u, 22 * u), 0.8f * u)
        }
        // Estrella de ocho puntas en el lóbulo de la clave y un filete de oro.
        TemaId.ANDALUS -> {
            estrellaHoja(Offset(w / 2, 11 * u), 5 * u, e.tertiary, puntas = 8, dentro = 0.6f)
            drawLine(e.tertiary.copy(alpha = 0.6f), Offset(12 * u, 23 * u), Offset(w - 12 * u, 23 * u), 0.8f * u)
        }
        // El cartucho del glifo del día en lo alto del templo y el renglón rojo del códice debajo.
        TemaId.MESO -> {
            cartuchoMaya(Offset(w / 2, 7 * u), 4.5f * u, e.primary, e.surface, e.onSurface, 1)
            drawLine(e.primary.copy(alpha = 0.6f), Offset(12 * u, 21 * u), Offset(w - 12 * u, 21 * u), 0.9f * u)
        }
        // La rueda del techo de la yurta al centro y la costura del fieltro.
        TemaId.MONGOL -> {
            val c = Offset(w / 2, 9 * u)
            drawCircle(e.tertiary, 5 * u, c, style = Stroke(1.2f * u))
            drawLine(e.tertiary, Offset(c.x - 5 * u, c.y), Offset(c.x + 5 * u, c.y), u)
            drawLine(e.tertiary, Offset(c.x, c.y - 5 * u), Offset(c.x, c.y + 5 * u), u)
            drawLine(e.primary.copy(alpha = 0.5f), Offset(20 * u, 20 * u), Offset(w - 20 * u, 20 * u), u, pathEffect = PathEffect.dashPathEffect(floatArrayOf(4 * u, 3 * u)))
        }
        // Trazos de construcción: un golpe de compás en la esquina, una línea guía y una cruz de centro.
        TemaId.RENACIMIENTO -> {
            val col = e.primary.copy(alpha = 0.55f)
            drawArc(col, 0f, 90f, false, Offset(2 * u, -12 * u), Size(24 * u, 24 * u), style = Stroke(0.8f * u))
            drawLine(col, Offset(14 * u, 5 * u), Offset(w - 14 * u, 5 * u), 0.6f * u)
            val c = Offset(w / 2, 10 * u)
            drawCircle(col, 4 * u, c, style = Stroke(0.8f * u))
            drawLine(col, Offset(c.x - 7 * u, c.y), Offset(c.x + 7 * u, c.y), 0.6f * u)
            drawLine(col, Offset(c.x, c.y - 7 * u), Offset(c.x, c.y + 7 * u), 0.6f * u)
        }
        // Los ollaos de latón de la vela y la relinga (el cabo cosido al borde).
        TemaId.PIRATA -> {
            val col = e.secondary.copy(alpha = 0.7f)
            listOf(10 * u, w - 10 * u).forEach { x -> drawCircle(col, 3.2f * u, Offset(x, 6 * u), style = Stroke(1.4f * u)) }
            drawPath(Path().apply { moveTo(16 * u, 9 * u); quadraticTo(w / 2, 22 * u, w - 16 * u, 9 * u) }, e.primary.copy(alpha = 0.45f), style = Stroke(1.6f * u, pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(2.6f * u, 1.4f * u))))
        }
        // Una franja roja y otra negra, paralelas a la diagonal.
        TemaId.URSS -> {
            val k = 20 * u / w
            drawPath(Path().apply { moveTo(0f, 4 * u); lineTo(w, 24 * u); lineTo(w, 29 * u); lineTo(0f, 9 * u); close() }, e.primary)
            val x0 = w * 0.06f; val x1 = w * 0.4f
            drawPath(Path().apply { moveTo(x0, 12 * u + k * x0); lineTo(x1, 12 * u + k * x1); lineTo(x1, 14.5f * u + k * x1); lineTo(x0, 14.5f * u + k * x0); close() }, e.onBackground)
        }
        // Tramo azul, blanco y rojo bajo el borde roto.
        TemaId.REVOLUCION -> {
            val tercio = 28 * u; val y = 13 * u
            listOf(e.secondary, Color.White, e.primary).forEachIndexed { k, c -> drawRect(c, Offset(14 * u + k * tercio, y), Size(tercio, 3 * u)) }
            drawRect(e.onBackground.copy(alpha = 0.3f), Offset(14 * u, y), Size(3 * tercio, 3 * u), style = Stroke(0.5f * u))
        }
        // Línea tallada con runas pequeñas y una gema al centro.
        TemaId.ANTIGUAS -> {
            val y = 14 * u; val col = e.secondary.copy(alpha = 0.7f)
            drawLine(col, Offset(24 * u, y), Offset(w / 2 - 9 * u, y), u)
            drawLine(col, Offset(w / 2 + 9 * u, y), Offset(w - 24 * u, y), u)
            listOf(1f, -1f).forEach { s ->
                (0 until 4).forEach { k ->
                    val x = if (s > 0f) (34 + k * 16) * u else w - (34 + k * 16) * u
                    drawLine(col, Offset(x, y - 5 * u), Offset(x, y - u), u)
                    drawLine(col, Offset(x, y - 5 * u), Offset(x + s * (if (k % 2 == 0) 3f else -3f) * u, y - 3 * u), u)
                }
            }
            drawPath(Path().apply { moveTo(w / 2, y - 7 * u); lineTo(w / 2 + 5 * u, y); lineTo(w / 2, y + 7 * u); lineTo(w / 2 - 5 * u, y); close() }, e.primary)
        }
        TemaId.CUEVA -> {
            // Una hilera de puntos de ocre que se abre al centro para una mano pequeña.
            val y = 20 * u
            var x = 26 * u; var k = 0
            while (x < w - 26 * u) { if (kotlin.math.abs(x - w / 2) > 14 * u) drawCircle(e.primary.copy(alpha = 0.55f), (1.4f + (k % 3) * 0.4f) * u, Offset(x, y)); x += 11 * u; k++ }
            drawPath(manoUnida(Offset(w / 2, y), 7 * u), e.primary)
        }
    }
}

/** Estrella de [puntas] puntas; [dentro] es el radio de los valles respecto al de las puntas. */
private fun DrawScope.estrellaHoja(c: Offset, r: Float, color: Color, puntas: Int = 5, dentro: Float = 0.45f) {
    val p = Path()
    for (i in 0 until puntas * 2) {
        val rr = if (i % 2 == 0) r else r * dentro
        val a = Math.toRadians(-90.0 + 180.0 / puntas * i)
        val x = c.x + rr * cos(a).toFloat(); val y = c.y + rr * sin(a).toFloat()
        if (i == 0) p.moveTo(x, y) else p.lineTo(x, y)
    }
    p.close()
    drawPath(p, color)
}

/**
 * La forma de las piezas de la hoja (el botón grande, los del menú y los avisos), en el mismo lenguaje que los botones
 * del tema: frontón bajo, arco apuntado, boleto con muescas, banderola… Las medidas van en dp, no en proporción, para
 * que sirvan igual en un botón que en un aviso alto.
 */
private fun formaPiezaQuick(t: TemaId): Shape = when (t) {
    TemaId.ROMANO -> RoundedCornerShape(2.dp)
    TemaId.URBE -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 2.dp, bottomStart = 2.dp)
    TemaId.CIBER -> CutCornerShape(topEnd = 10.dp, bottomStart = 10.dp)
    TemaId.NEON -> RoundedCornerShape(50)
    // Frontón muy bajo.
    TemaId.HELENO -> formaDp { w, h, u -> val f = 8 * u; moveTo(0f, f); lineTo(w / 2, 0f); lineTo(w, f); lineTo(w, h); lineTo(0f, h); close() }
    // Cartucho: redondo a la izquierda y cerrado por la barra del nudo a la derecha.
    TemaId.EGIPTO -> formaDp { w, h, _ ->
        val r = minOf(h / 2, w / 2)
        moveTo(r, 0f); lineTo(w, 0f); lineTo(w, h); lineTo(r, h); arcTo(Rect(0f, 0f, 2 * r, h), 90f, 180f, false); close()
    }
    // Esquinas cóncavas, como un marco de miniatura.
    TemaId.MEDIEVAL -> formaDp { w, h, u ->
        val r = 7 * u
        moveTo(r, 0f); lineTo(w - r, 0f); quadraticTo(w - r, r, w, r); lineTo(w, h - r); quadraticTo(w - r, h - r, w - r, h)
        lineTo(r, h); quadraticTo(r, h - r, 0f, h - r); lineTo(0f, r); quadraticTo(r, r, r, 0f); close()
    }
    // El escudete: recto arriba y redondeado abajo.
    TemaId.TEMPLARIO -> RoundedCornerShape(topStart = 1.dp, topEnd = 1.dp, bottomEnd = 14.dp, bottomStart = 14.dp)
    TemaId.DISTOPIA -> RectangleShape
    TemaId.REFUGIO -> RoundedCornerShape(18.dp)
    TemaId.ORDEN -> CutCornerShape(8.dp)
    TemaId.VAPOR -> RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp, bottomEnd = 2.dp, bottomStart = 2.dp)
    TemaId.LABORATORIO -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomEnd = 4.dp, bottomStart = 4.dp)
    // Boleto: una muesca a cada lado, a media altura.
    TemaId.COLONOS -> formaDp { w, h, u ->
        val r = 6 * u; val m = h / 2
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w, m - r)
        arcTo(Rect(Offset(w, m), r), 270f, -180f, false)
        lineTo(w, h); lineTo(0f, h); lineTo(0f, m + r)
        arcTo(Rect(Offset(0f, m), r), 90f, -180f, false)
        close()
    }
    // Placa de puntas, como una chapa de chacó.
    TemaId.NAPOLEON -> formaDp { w, h, u ->
        val c = minOf(h / 2, 12 * u)
        moveTo(c, 0f); lineTo(w - c, 0f); lineTo(w, h / 2); lineTo(w - c, h); lineTo(c, h); lineTo(0f, h / 2); close()
    }
    // Placa de ascensor: cada esquina metida en un escalón.
    TemaId.DECO -> formaDp { w, h, u ->
        val a = minOf(h * 0.22f, 6 * u)
        moveTo(a, 0f); lineTo(w - a, 0f); lineTo(w - a, a); lineTo(w, a); lineTo(w, h - a); lineTo(w - a, h - a); lineTo(w - a, h)
        lineTo(a, h); lineTo(a, h - a); lineTo(0f, h - a); lineTo(0f, a); lineTo(a, a); close()
    }
    // Hoja: dos esquinas redondas y dos en punta.
    TemaId.BOTANICO -> RoundedCornerShape(topStart = 16.dp, topEnd = 2.dp, bottomEnd = 16.dp, bottomStart = 2.dp)
    // Un bloque desportillado: dos esquinas cortadas, desiguales.
    TemaId.RUINAS -> CutCornerShape(topStart = 3.dp, topEnd = 10.dp, bottomEnd = 3.dp, bottomStart = 6.dp)
    // Caja de regalo con su cinta: esquinas suaves y una muesca arriba al centro.
    TemaId.NAVIDAD -> formaDp { w, h, u ->
        val r = 8 * u; val n = 5 * u
        moveTo(r, 0f); lineTo(w / 2 - n, 0f); lineTo(w / 2, n * 0.8f); lineTo(w / 2 + n, 0f); lineTo(w - r, 0f); quadraticTo(w, 0f, w, r)
        lineTo(w, h - r); quadraticTo(w, h, w - r, h); lineTo(r, h); quadraticTo(0f, h, 0f, h - r); lineTo(0f, r); quadraticTo(0f, 0f, r, 0f); close()
    }
    // Tablilla con alero, como el rótulo de una puerta.
    TemaId.SEDA -> formaDp { w, h, u ->
        val a = 5 * u
        moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - a, 8 * u); lineTo(w - a, h); lineTo(a, h); lineTo(a, 8 * u); close()
    }
    // Ema: la tablilla votiva, con sus dos esquinas de arriba en chaflán.
    TemaId.EDO -> formaDp { w, h, u ->
        val a = 7 * u
        moveTo(0f, a); lineTo(a, 0f); lineTo(w - a, 0f); lineTo(w, a); lineTo(w, h); lineTo(0f, h); close()
    }
    // Arco apuntado.
    TemaId.GOTICO -> formaDp { w, h, u ->
        val a = 14 * u
        moveTo(0f, h); lineTo(0f, a); quadraticTo(0f, a * 0.25f, w / 2, 0f); quadraticTo(w, a * 0.25f, w, a); lineTo(w, h); close()
    }
    // La ficha del fichero: la esquina de arriba a la derecha recortada.
    TemaId.ARCANO -> formaDp { w, h, u -> val d = 8 * u; moveTo(0f, 0f); lineTo(w - d, 0f); lineTo(w, d); lineTo(w, h); lineTo(0f, h); close() }
    // Tecla de consola, más angosta arriba.
    TemaId.ESPACIO -> formaDp { w, h, u -> val d = 6 * u; moveTo(d, 0f); lineTo(w - d, 0f); lineTo(w, h); lineTo(0f, h); close() }
    // El flotador del batiscafo: proa redonda a la izquierda, popa de esquinas suaves a la derecha.
    TemaId.ABISMO -> formaDp { w, h, u ->
        val r = h / 2; val r2 = 4 * u
        moveTo(r, 0f); lineTo(w - r2, 0f); quadraticTo(w, 0f, w, r2); lineTo(w, h - r2); quadraticTo(w, h, w - r2, h); lineTo(r, h)
        quadraticTo(0f, h, 0f, h / 2); quadraticTo(0f, 0f, r, 0f); close()
    }
    // Arco de medio punto, de radio fijo.
    TemaId.ANDALUS -> RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
    // El bloque de un glifo: un cuadro de esquinas muy redondeadas.
    TemaId.MESO -> RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomEnd = 14.dp, bottomStart = 14.dp)
    // Esquinas escalonadas, como la orilla del fieltro.
    TemaId.MONGOL -> formaDp { w, h, u ->
        val s = 4 * u
        moveTo(2 * s, 0f); lineTo(w - 2 * s, 0f); lineTo(w - 2 * s, s); lineTo(w - s, s); lineTo(w - s, 2 * s); lineTo(w, 2 * s)
        lineTo(w, h - 2 * s); lineTo(w - s, h - 2 * s); lineTo(w - s, h - s); lineTo(w - 2 * s, h - s); lineTo(w - 2 * s, h)
        lineTo(2 * s, h); lineTo(2 * s, h - s); lineTo(s, h - s); lineTo(s, h - 2 * s); lineTo(0f, h - 2 * s)
        lineTo(0f, 2 * s); lineTo(s, 2 * s); lineTo(s, s); lineTo(2 * s, s); close()
    }
    // Hoja con la esquina doblada.
    TemaId.RENACIMIENTO -> CutCornerShape(bottomEnd = 12.dp)
    // La duela del barril: arriba y abajo, un poco combada.
    TemaId.PIRATA -> formaDp { w, h, u -> val f = 3 * u; moveTo(0f, f); quadraticTo(w / 2, -f, w, f); lineTo(w, h - f); quadraticTo(w / 2, h + f, 0f, h - f); close() }
    // Paralelogramo de cartel.
    TemaId.URSS -> formaDp { w, h, u -> val d = 10 * u; moveTo(d, 0f); lineTo(w, 0f); lineTo(w - d, h); lineTo(0f, h); close() }
    // Banderola con cola de golondrina.
    TemaId.REVOLUCION -> formaDp { w, h, u -> val d = minOf(h * 0.25f, 10 * u); moveTo(0f, 0f); lineTo(w, 0f); lineTo(w - d, h / 2); lineTo(w, h); lineTo(0f, h); close() }
    // Losa de piedra irregular.
    TemaId.ANTIGUAS -> formaDp { w, h, u ->
        moveTo(6 * u, 0f); lineTo(w - 2 * u, 3 * u); lineTo(w, h - 10 * u); lineTo(w - 8 * u, h); lineTo(2 * u, h - 3 * u); lineTo(0f, 8 * u); close()
    }
    // Un canto rodado aplastado: esquinas muy blandas y desparejas.
    TemaId.CUEVA -> formaDp { w, h, u ->
        moveTo(10 * u, 0f); lineTo(w - 14 * u, 2 * u); quadraticTo(w, 2 * u, w, h * 0.5f); quadraticTo(w - 2 * u, h, w - 16 * u, h); lineTo(12 * u, h - 1 * u)
        quadraticTo(0f, h - 2 * u, 0f, h * 0.5f); quadraticTo(0f, 0f, 10 * u, 0f); close()
    }
}
