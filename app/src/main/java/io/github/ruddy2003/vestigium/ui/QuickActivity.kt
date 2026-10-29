@file:OptIn(ExperimentalLayoutApi::class)

package io.github.ruddy2003.vestigium.ui

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    Catalogo.PENSAMIENTOS.any { it.id == tipo } -> m0.copy(pensamiento = tipo)
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
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                            .navigationBarsPadding().imePadding(),
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                        color = MaterialTheme.colorScheme.background,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    ) {
                        var pantalla by remember { mutableStateOf(pantallaInicial) }
                        var privado by remember { mutableStateOf(inicial?.takeIf { pantallaInicial == "privado" }) }
                        Column(Modifier.fondoTema(Estilo.tema, MaterialTheme.colorScheme).padding(18.dp).verticalScroll(rememberScrollState())) {
                            Meandro(Modifier.padding(bottom = 12.dp))
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
        Text(titulo, style = MaterialTheme.typography.titleMedium.copy(fontSize = 19.sp))
        // El día va también en palabras: con algunos temas la fecha del estilo no se lee de un vistazo (N-08).
        val dia = fecha ?: Fechas.diaLogico()
        Text(io.github.ruddy2003.vestigium.ui.Estilo.comentario + Fechas.horaAhora() + " · " + io.github.ruddy2003.vestigium.ui.Estilo.fecha(dia) +
            " · " + Fechas.titulo(dia).lowercase(), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        // Con una fecha propia (el sueño va al día del despertar) el aviso del día lógico no aplica.
        if (fecha == null) io.github.ruddy2003.vestigium.ui.AvisoMadrugada()
        Ayuda(sub, Modifier.padding(top = 2.dp, bottom = 12.dp))
    }

    /** El menú del botón ⊕: todo lo que se registra, en botones grandes. */
    @Composable
    private fun Registrar(ir: (String) -> Unit) {
        val cs = MaterialTheme.colorScheme
        Encabezado("Registrar", "Lo de todos los días en un toque. Lo demás, cuando quieras.")
        Surface(onClick = { ir(M_ANIMO) }, modifier = Modifier.fillMaxWidth().heightIn(min = 76.dp), shape = RoundedCornerShape(6.dp), color = cs.primary) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Ánimo ahora", style = MaterialTheme.typography.titleLarge, color = cs.onPrimary)
                Text("cómo estás, qué sientes y qué piensas", style = MaterialTheme.typography.labelMedium, color = cs.onPrimary.copy(alpha = 0.85f))
            }
        }
        val botones = listOfNotNull(
            "Enfoque" to M_ENFOQUE, "Siesta" to M_SIESTA, "Sustancias" to M_CONSUMO, "Gasto o ingreso" to M_GASTO, "Sueño" to M_SUENO, "Llenar el día" to "diario",
            "Nota" to "notas", "Impulso" to "impulso", if (Repo.config.seguimiento_privado) "Recaída" to "recaida" else null,
            if (Repo.config.seguimiento_privado) "Libido" to "libido" else null,
        )
        FlowRow(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = 2) {
            botones.forEach { (t, d) ->
                Surface(onClick = {
                    if (d == "diario" || d == "notas") { startActivity(android.content.Intent(this@QuickActivity, MainActivity::class.java).putExtra(MainActivity.PANTALLA, d)
                        .addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)); finish() }
                    else ir(d)
                }, modifier = Modifier.weight(1f).heightIn(min = 56.dp), shape = RoundedCornerShape(4.dp), color = cs.surface, border = BorderStroke(1.dp, cs.outline)) {
                    Box(contentAlignment = Alignment.Center) { Text(t, style = MaterialTheme.typography.titleSmall) }
                }
            }
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
                        modifier = Modifier.weight(1f).heightIn(min = 48.dp), shape = RoundedCornerShape(4.dp), color = if (ya) cs.primary.copy(alpha = 0.12f) else cs.surface,
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
        var pensamiento by remember { mutableStateOf(inicial?.pensamiento) }
        var nota by remember { mutableStateOf(inicial?.texto.orEmpty()) }
        var con by remember { mutableStateOf(inicial?.con_quien.orEmpty()) }
        val discreto = Repo.config.discreto
        // Cuándo: null = ahora. Para anotar tarde («ayer», otra hora); no en la respuesta a un aviso, que es de ese momento.
        // Desde «＋ agregar a este día» de otro día: empieza a mediodía de ese día, con el selector abierto (N-26).
        val fechaPedida = intent.getStringExtra(FECHA)?.takeIf { it != Fechas.diaLogico() }
        var cuando by remember { mutableStateOf<java.time.LocalDateTime?>(fechaPedida?.let { Fechas.parse(it).atTime(12, 0) }) }
        var elegirCuando by remember { mutableStateOf(fechaPedida != null) }
        /** El registro se crea con lo primero que toques. */
        fun asegurar(f: (Momento) -> Momento) {
            val actual = m ?: crear(if (avisoMs != null) "muestreo" else "animo", origen, null, avisoMs, cuando?.aMillis())
            val n = f(Repo.momento(actual.id) ?: actual)
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
        fun alternar(id: String) {
            emociones = when {
                id in emociones -> emociones - id
                id == Catalogo.NO_SE -> listOf(Catalogo.NO_SE)
                else -> emociones - Catalogo.NO_SE + id
            }
            val e = emociones
            asegurar { it.copy(emociones = e, emocion_otra = if (Catalogo.OTRO in e) otra.ifBlank { null } else null) }
        }
        listOf(1, -1, 0).forEach { tono ->
            FlowRow(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Catalogo.SENTIMIENTOS.filter { it.tono == tono }.forEach { e -> Ficha(if (discreto) e.discreto else e.nombre, e.id in emociones) { alternar(e.id) } }
                if (tono == 0) {
                    Ficha("otro…", Catalogo.OTRO in emociones) { alternar(Catalogo.OTRO) }
                    Ficha("no sé", Catalogo.NO_SE in emociones) { alternar(Catalogo.NO_SE) }
                }
            }
        }
        if (Catalogo.OTRO in emociones) OutlinedTextField(value = otra, onValueChange = { t -> otra = t; asegurar { it.copy(emocion_otra = t.trim().ifBlank { null }) } },
            modifier = Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true, label = { Text("¿Cuál?") })
        Etiqueta("¿En qué piensas? (opcional)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.PENSAMIENTOS.forEach { o -> Ficha(o.nombre, pensamiento == o.id) { pensamiento = if (pensamiento == o.id) null else o.id; val p = pensamiento; asegurar { it.copy(pensamiento = p) } } }
        }
        OutlinedTextField(value = nota, onValueChange = { t -> nota = t; asegurar { it.copy(texto = t) } },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp), minLines = 2, placeholder = { Text("¿Qué está pasando? Usa @persona y #tema") }, visualTransformation = rememberMarcas())
        Row(Modifier.padding(top = 6.dp)) { BotonDictado { d -> nota = (nota + " " + d).trim(); val t = nota; asegurar { it.copy(texto = t) } } }
        Etiqueta("¿Con quién estás?")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.CON_QUIEN.forEach { o ->
                val activo = o.id in con
                Ficha(o.nombre, activo) {
                    // «Solo» excluye a los demás, y elegir a alguien quita «solo».
                    con = when { activo -> con - o.id; o.id == "solo" -> listOf("solo"); else -> con - "solo" + o.id }
                    val c = con; asegurar { it.copy(con_quien = c) }
                }
            }
        }
        // Lo que no es ánimo, a un toque: sustancias, impulso y recaída.
        FlowRow(Modifier.padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            BotonSecundario("＋ sustancia") { ir(M_CONSUMO) }
            BotonSecundario("＋ impulso") { ir("impulso") }
            if (Repo.config.seguimiento_privado) BotonSecundario("＋ recaída") { ir("recaida") }
            if (Repo.config.seguimiento_privado) BotonSecundario("＋ libido") { ir("libido") }
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
            Surface(color = cs.secondaryContainer, shape = RoundedCornerShape(6.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
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
        OutlinedTextField(value = texto, onValueChange = { texto = it; actualizar(mm.id) { x -> x.copy(texto = it) } },
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
                val activo = d in det
                Ficha(d.replace("_", " "), activo) { det = if (activo) det - d else det + d; actualizar(mm.id) { it.copy(detonantes = det) } }
            }
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
            if (id == "alcohol") {
                Etiqueta("Bebidas hasta ahora: ${"%.1f".format(cantidad).removeSuffix(".0")}")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.BEBIDAS.forEach { (n, eq) -> Ficha("+ $n", false) { cantidad += eq; val q = cantidad; Repo.consumo(abierto.id)?.let { Repo.guardarConsumo(it.copy(cantidad = q)) } } }
                }
            }
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
        if (id == "otra") OutlinedTextField(value = otra, onValueChange = { otra = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), singleLine = true, label = { Text("¿Cuál?") })
        Etiqueta(if (info.conNivel) "¿Desde cuándo?" else "¿Cuándo?")
        SelectorHora(cuando, atajos = listOf(30, 60, 120, 180)) { cuando = it }
        if (info.conNivel) {
            Etiqueta("¿Cómo vas ahora? (0 a 10)")
            Escala(nivel, 0, 10, Catalogo.anclasNivel(id)) { nivel = it }
        }
        when (id) {
            "alcohol" -> {
                Etiqueta("Bebidas: ${"%.1f".format(cantidad).removeSuffix(".0")} (opcional)")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.BEBIDAS.forEach { (n, eq) -> Ficha("+ $n", false) { cantidad += eq } }
                    if (cantidad > 0) Ficha("borrar", false) { cantidad = 0.0 }
                }
            }
            "cafeina" -> {
                Etiqueta("¿Qué tomaste? ${cantidad.toInt()} mg")
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Catalogo.CAFES.forEach { (n, mg) -> Ficha("+ $n", false) { cantidad += mg } }
                    if (cantidad > 0) Ficha("borrar", false) { cantidad = 0.0 }
                }
                Ayuda("Si registras la cafeína en MacroFactor, esos días se usa la de MacroFactor (con la hora de cada alimento) y no se cuenta dos veces.", Modifier.padding(top = 4.dp))
            }
            else -> {
                if (info.vias.isNotEmpty()) {
                    Etiqueta("¿Cómo?")
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) { info.vias.forEach { o -> Ficha(o.nombre, via == o.id) { via = if (via == o.id) null else o.id } } }
                }
                Etiqueta("¿Cuántos ${info.unidad}? ${cantidad.toInt().takeIf { it > 0 } ?: ""}")
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Ficha("−", false) { cantidad = (cantidad - 1).coerceAtLeast(0.0) }
                    Ficha("+", false) { cantidad += 1 }
                }
            }
        }
        Etiqueta("¿Con quién? (opcional)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.CON_QUIEN.forEach { o -> val a = o.id in con; Ficha(o.nombre, a) { con = when { a -> con - o.id; o.id == "solo" -> listOf("solo"); else -> con - "solo" + o.id } } }
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

    /** La mañana siguiente: hasta qué hora duró, el máximo, cuánto, la cruda y cómo te sentiste. */
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
        Encabezado((if (c0.fecha < Fechas.diaLogico()) "Lo de anoche: " else "Editar: ") + Catalogo.nombreSustancia(c0).lowercase(), "Unos toques. Lo que no recuerdes, déjalo vacío.")
        Etiqueta("¿Desde cuándo?")
        SelectorHora(inicio, etiquetaAhora = "no sé") { inicio = it }
        Etiqueta("¿Hasta cuándo te duró el efecto?")
        SelectorHora(fin, etiquetaAhora = "no sé") { fin = it }
        if (info?.conNivel == true) {
            Etiqueta("¿Hasta qué nivel llegaste? (0 a 10)")
            Escala(max, 0, 10, Catalogo.anclasNivel(c0.sustancia)) { max = it }
        }
        if (c0.sustancia == "alcohol") {
            Etiqueta("Bebidas en total: ${"%.1f".format(cantidad).removeSuffix(".0")}")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Catalogo.BEBIDAS.forEach { (n, eq) -> Ficha("+ $n", false) { cantidad += eq } }
                if (cantidad > 0) Ficha("borrar", false) { cantidad = 0.0 }
            }
        }
        Etiqueta("Cruda o bajón hoy (0 a 5)")
        Escala(cruda, 0, 5, Catalogo.CRUDA) { cruda = it }
        Etiqueta("Síntomas (opcional)")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.SINTOMAS.forEach { o -> val a = o.id in sintomas; Ficha(o.nombre, a) { sintomas = if (a) sintomas - o.id else sintomas + o.id } }
        }
        OutlinedTextField(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), placeholder = { Text("Nota (opcional): @persona, #lugar…") },
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
                Repo.guardarConsumo(actual.copy(inicio = Fechas.isoDeMillis(ini), fecha = Fechas.diaLogicoDeMillis(ini), fin = fin?.aMillis()?.coerceAtLeast(ini)?.let { Fechas.isoDeMillis(it) } ?: actual.fin,
                    nivel_max = max, cantidad = cantidad.takeIf { it > 0 } ?: actual.cantidad, cruda = cruda, sintomas = sintomas, nota = nota.trim(), completo = true))
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
    private fun GastoRapido(catInicial: String?, subInicial: String?, inicial: Gasto?) {
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
        var fijo by remember { mutableStateOf(false) }
        var nota by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        var registrado by remember { mutableStateOf(inicial) }
        var corrigiendo by remember { mutableStateOf(false) }
        var cuando by remember { mutableStateOf<java.time.LocalDateTime?>(null) }
        val c = Repo.config
        Encabezado("Gasto o ingreso", if (entra) "Lo que entra: sueldo, beca, venta, regalo." else "En el momento de pagar. Un frecuente registra con un toque.")
        Row(Modifier.fillMaxWidth().padding(bottom = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Ficha("− Gasto", !entra) { if (entra) { entra = false; cat = null; sub = ""; error = null } }
            Ficha("+ Ingreso", entra) { if (!entra) { entra = true; cat = null; sub = ""; error = null } }
        }
        // Lo que acaba de registrar un frecuente: se ve, se corrige o se deshace.
        registrado?.let { r ->
            val g = Repo.gasto(r.id) ?: r
            Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.10f), modifier = Modifier.fillMaxWidth().padding(top = 6.dp)) {
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
                        }
                    }
                }
            }
        }
        Etiqueta(if (entra) "Monto" else "Otro gasto")
        OutlinedTextField(value = monto, onValueChange = { monto = it.filter { ch -> ch.isDigit() || ch == '.' }; error = null }, modifier = Modifier.fillMaxWidth(),
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
        else Interruptor("¿Fue imprevisto?", imprevisto) { imprevisto = it }
        OutlinedTextField(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Nota opcional") }, singleLine = true)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp)) }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            if (registrado != null) TextButton(onClick = { terminar() }) { Text("Listo") }
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
                        Toast.makeText(this@QuickActivity, "Ingreso registrado: ${dinero(i.monto)} · ${i.categoria}", Toast.LENGTH_SHORT).show()
                        terminar()
                    }
                    else -> {
                        val g = Gasto(Repo.nuevoId(), fecha, hora, Math.round(v * 100) / 100.0, cat!!, imprevisto, nota.trim(), subcategoria = sub, ts_creado = Fechas.ahoraIso())
                        Repo.guardarGasto(g)
                        // La ubicación de ahora solo sirve si el gasto es de ahora.
                        if (k == null) ubicarGasto(g)
                        guardando = true
                        Toast.makeText(this@QuickActivity, "Gasto registrado: ${dinero(g.monto)} · ${g.categoria}" + (if (sub.isNotBlank()) " → $sub" else ""), Toast.LENGTH_SHORT).show()
                        terminar()
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
        // 1. La narración, arriba: es lo que se olvida primero.
        Etiqueta("Qué soñaste")
        OutlinedTextField(value = d.sueno_texto, onValueChange = { t -> guardar(d.copy(sueno_texto = t, sueno_recordado = d.sueno_recordado || t.isNotBlank())) },
            modifier = Modifier.fillMaxWidth(), minLines = 5, visualTransformation = rememberMarcas(),
            placeholder = { Text("Lo que recuerdes, en el orden en que lo recuerdes, aunque sean fragmentos. Usa @persona y #tema.") })
        Row(Modifier.padding(top = 8.dp)) { BotonDictado { t -> val n = (d.sueno_texto + " " + t).trim(); guardar(d.copy(sueno_texto = n, sueno_recordado = true)) } }
        // 2. La noche.
        Etiqueta("Calidad del sueño, como la sentiste")
        Escala(d.sueno_calidad, 1, 5, Catalogo.ANCLAS["sueno_calidad"]) { guardar(d.copy(sueno_calidad = it)) }
        Interruptor("¿Recordaste un sueño?", d.sueno_recordado) { guardar(d.copy(sueno_recordado = it)) }
        // 3. Todo lo del sueño, aquí mismo.
        if (d.sueno_recordado) {
            Etiqueta("Qué tan agradable fue")
            Escala(d.sueno_valencia, 1, 7, Catalogo.ANCLAS["sueno_valencia"]) { guardar(d.copy(sueno_valencia = it)) }
            Etiqueta("Lucidez: ¿sabías que soñabas?"); FilaFichas(Catalogo.SUENO_LUCIDEZ, d.sueno_lucidez) { guardar(d.copy(sueno_lucidez = it)) }
            Etiqueta("Vividez del recuerdo"); FilaFichas(Catalogo.SUENO_VIVIDEZ, d.sueno_vividez) { guardar(d.copy(sueno_vividez = it)) }
            Etiqueta("Tipo"); FilaFichas(Catalogo.SUENO_TIPO, d.sueno_tipo) { guardar(d.copy(sueno_tipo = it)) }
            Etiqueta("De qué fue")
            CategoriasSueno(d.sueno_categorias) { guardar(d.copy(sueno_categorias = it)) }
            Etiqueta("Personas y temas del sueño")
            ListaEtiquetas(d.sueno_temas, "@persona o #tema", normalizar = { io.github.ruddy2003.vestigium.data.Texto.etiquetaConSimbolo(it) }) {
                guardar(d.copy(sueno_temas = it))
            }
        }
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
