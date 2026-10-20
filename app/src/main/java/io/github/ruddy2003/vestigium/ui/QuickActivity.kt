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
        const val M_GASTO = "gasto"; const val M_SUENO = "sueno"; const val M_MINIMO = "minimo"; const val M_ENFOQUE = "enfoque"
        const val CATEGORIA = "categoria"; const val CALIDAD = "calidad"; const val RECORDADO = "recordado"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Repo.init(this)
        val modo = intent.getStringExtra(MODO) ?: M_MENU
        val origen = intent.getStringExtra(ORIGEN) ?: if (intent.hasExtra(AVISO_MS)) "notificacion" else "acceso_directo"
        val avisoMs = intent.getLongExtra(AVISO_MS, 0L).takeIf { it > 0 }
        intent.getIntExtra(NOTIF, 0).takeIf { it != 0 }?.let { NotificationManagerCompat.from(this).cancel(it) }

        // Guardado inmediato cuando el toque ya trae la información.
        var inicial: Momento? = null
        val tipo = intent.getStringExtra(TIPO)
        if (modo == M_MOMENTO && tipo != null) inicial = crear(tipo, origen, null, null)
        val val0 = intent.getIntExtra(VALENCIA, 0).takeIf { it in 1..7 }
        if (modo == M_ANIMO && val0 != null) inicial = crear("muestreo", origen, val0, avisoMs)
        // Desde el widget de sueño: la calidad se guarda con el toque.
        if (modo == M_SUENO) {
            val cal = intent.getIntExtra(CALIDAD, 0).takeIf { it in 1..5 }
            val rec = intent.getBooleanExtra(RECORDADO, false)
            if (cal != null || rec) {
                val d = Repo.dia(Fechas.diaLogico())
                Repo.guardarDia(d.copy(sueno_calidad = cal ?: d.sueno_calidad, sueno_recordado = rec || d.sueno_recordado))
            }
        }
        val catInicial = intent.getStringExtra(CATEGORIA)

        setContent {
            TemaVestigium {
                Box(
                    Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { terminar() },
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                            .navigationBarsPadding().imePadding(),
                        shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp),
                        color = MaterialTheme.colorScheme.background,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    ) {
                        Column(Modifier.padding(18.dp).verticalScroll(rememberScrollState())) {
                            Meandro(Modifier.padding(bottom = 12.dp))
                            when (modo) {
                                M_ANIMO -> Animo(inicial, origen, avisoMs)
                                M_MOMENTO, M_MENU -> MomentoCaptura(inicial, origen)
                                M_GASTO -> GastoRapido(catInicial)
                                M_SUENO -> SuenoRapido()
                                M_MINIMO -> Minimo()
                                M_ENFOQUE -> EnfoqueRapido()
                                else -> MomentoCaptura(null, origen)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun terminar() { runCatching { Widgets.actualizar(this) }; finish() }

    /** Cómo estaba el día antes de registrar una recaída, para que «Deshacer» lo deje igual. */
    private var previoRecaida: io.github.ruddy2003.vestigium.data.Recaidas.Previo? = null

    /** Crea y guarda el momento al instante; la ubicación se añade en cuanto llega. */
    fun crear(tipo: String, origen: String, valencia: Int?, avisoMs: Long?): Momento {
        val m = Momento(
            id = Repo.nuevoId(), ts = Fechas.ahoraIso(), fecha = Fechas.diaLogico(), tipo = tipo, valencia = valencia,
            origen = origen, aviso_ts = avisoMs?.let { Fechas.isoDeMillis(it) },
        )
        Repo.guardarMomento(m)
        if (tipo == "recaida") previoRecaida = io.github.ruddy2003.vestigium.data.Recaidas.registrar(m)
        // Luz y, si lo activaste, ruido del ambiente en ese instante (no se guarda audio).
        lifecycleScope.launch {
            val ctx = this@QuickActivity
            val luz = if (Repo.config.medir_luz) runCatching { io.github.ruddy2003.vestigium.sensores.Percepcion.lux(ctx) }.getOrNull() else null
            kotlinx.coroutines.delay(250)
            val ruido = if (Repo.config.medir_ruido) runCatching { io.github.ruddy2003.vestigium.sensores.Ambiente.ruido(ctx) }.getOrNull() else null
            if (luz != null || ruido != null) actualizar(m.id) { it.copy(luz_lux = luz ?: it.luz_lux, ruido_db = ruido ?: it.ruido_db) }
        }
        lifecycleScope.launch {
            val loc = Ubicacion.actual(this@QuickActivity) ?: return@launch
            val actual = Repo.momento(m.id) ?: m
            val lugar = Repo.lugarCercano(loc.latitude, loc.longitude)
            Repo.guardarMomento(actual.copy(lat = loc.latitude, lon = loc.longitude, precision_m = loc.accuracy, lugar = lugar?.id))
            Repo.db().ponerUbicacion(System.currentTimeMillis(), loc.latitude, loc.longitude, loc.accuracy, "momento")
        }
        return m
    }

    private fun actualizar(id: String, f: (Momento) -> Momento) { Repo.momento(id)?.let { Repo.guardarMomento(f(it)) } }

    @Composable
    private fun Encabezado(titulo: String, sub: String) {
        Text(titulo, style = MaterialTheme.typography.titleMedium.copy(fontSize = 19.sp))
        Text(io.github.ruddy2003.vestigium.ui.Estilo.comentario + Fechas.horaAhora() + " · " + io.github.ruddy2003.vestigium.ui.Estilo.fecha(Fechas.diaLogico()), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
        Ayuda(sub, Modifier.padding(top = 2.dp, bottom = 12.dp))
    }

    @Composable
    private fun Animo(inicial: Momento?, origen: String, avisoMs: Long?) {
        var m by remember { mutableStateOf(inicial) }
        var valencia by remember { mutableStateOf(inicial?.valencia) }
        var activacion by remember { mutableStateOf<Int?>(null) }
        var nota by remember { mutableStateOf("") }
        var con by remember { mutableStateOf(inicial?.con_quien.orEmpty()) }
        Encabezado("¿Cómo te sientes ahora?", if (m != null) "Guardado a las ${Fechas.horaDe(m!!.ts)}. Agrega energía si quieres." else "Qué tan agradable y cuánta energía, en este momento.")
        Etiqueta("Qué tan agradable")
        Escala(valencia, 1, 7, Catalogo.ANCLAS["valencia"]) { v ->
            valencia = v
            if (v != null) {
                val actual = m
                if (actual == null) m = crear("muestreo", origen, v, avisoMs).also { nm -> if (con.isNotEmpty()) actualizar(nm.id) { it.copy(con_quien = con) } }
                else actualizar(actual.id) { it.copy(valencia = v) }
            }
        }
        Etiqueta("Energía")
        Escala(activacion, 1, 7, Catalogo.ANCLAS["activacion"]) { v ->
            activacion = v
            m?.let { mm -> actualizar(mm.id) { it.copy(activacion = v) } }
        }
        Etiqueta("¿Con quién estás?")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Catalogo.CON_QUIEN.forEach { o ->
                val activo = o.id in con
                Ficha(o.nombre, activo) {
                    // «Solo» excluye a los demás, y elegir a alguien quita «solo».
                    con = when {
                        activo -> con - o.id
                        o.id == "solo" -> listOf("solo")
                        else -> con - "solo" + o.id
                    }
                    m?.let { mm -> actualizar(mm.id) { x -> x.copy(con_quien = con) } }
                }
            }
        }
        Etiqueta("Nota (opcional)")
        OutlinedTextField(value = nota, onValueChange = { nota = it; m?.let { mm -> actualizar(mm.id) { x -> x.copy(texto = it) } } },
            modifier = Modifier.fillMaxWidth(), placeholder = { Text("¿Qué está pasando? Usa @ y #") }, visualTransformation = rememberMarcas())
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.End) {
            BotonPrincipal("Listo", habilitado = m != null) { terminar() }
        }
    }

    @Composable
    private fun MomentoCaptura(inicial: Momento?, origen: String) {
        var m by remember { mutableStateOf(inicial) }
        var texto by remember { mutableStateOf("") }
        var intensidad by remember { mutableStateOf<Int?>(null) }
        val discreto = Repo.config.discreto
        if (m == null) {
            Encabezado("Ahora mismo estoy…", "Toca uno. Se guarda al instante con la hora y el lugar.")
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = 2) {
                (Catalogo.MOMENTOS + if (Repo.config.seguimiento_privado) listOf(Catalogo.RECAIDA) else emptyList()).forEach { t ->
                    Surface(
                        onClick = { m = crear(t.id, origen, null, null) },
                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    ) { Box(contentAlignment = Alignment.Center) { Text(if (discreto) t.discreto else t.nombre, style = MaterialTheme.typography.titleMedium) } }
                }
            }
        } else {
            val mm = m!!
            val t = Catalogo.tipo(mm.tipo)
            Encabezado("Guardado: ${if (discreto) t?.discreto ?: "" else t?.nombre ?: mm.tipo}", "A las ${Fechas.horaDe(mm.ts)}. Lo demás es opcional; puedes cerrar.")
            if (mm.tipo == "recaida") DetalleRecaida(mm)
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
                }) { Text("Deshacer") }
                BotonPrincipal("Listo") { terminar() }
            }
        }
    }

    /** Lo que ayuda a entender una recaída: qué la detonó, en qué aparato y qué hacías antes. Todo opcional. */
    @Composable
    private fun DetalleRecaida(mm: Momento) {
        var det by remember(mm.id) { mutableStateOf(mm.detonantes) }
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
    @Composable
    private fun EnfoqueRapido() {
        val ctx = this
        val c = io.github.ruddy2003.vestigium.data.Enfoque.enCurso(Repo.config)
        var nota by remember { mutableStateOf("") }
        val ahora by androidx.compose.runtime.produceState(System.currentTimeMillis()) { while (true) { kotlinx.coroutines.delay(1000); value = System.currentTimeMillis() } }
        if (c != null) {
            val seg = ((ahora - c.second) / 1000).coerceAtLeast(0)
            Encabezado(if (c.first == "siesta") "Siesta en curso" else "Enfoque: ${Catalogo.nombreTramo(c.first).lowercase()}", "Empezó a las ${Fechas.horaDe(Fechas.isoDeMillis(c.second))}.")
            Text("%d:%02d:%02d".format(seg / 3600, (seg / 60) % 60, seg % 60), style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            OutlinedTextField(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Qué hiciste (opcional): #tesis, capítulo 2…") }, singleLine = true)
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { io.github.ruddy2003.vestigium.data.Enfoque.descartar(ctx); Toast.makeText(ctx, "Descartado", Toast.LENGTH_SHORT).show(); terminar() }) { Text("Descartar") }
                BotonPrincipal("Terminar") {
                    val t = io.github.ruddy2003.vestigium.data.Enfoque.terminar(ctx, nota)
                    Toast.makeText(ctx, if (t != null) "Guardado: ${t.min} min" else "Duró menos de un minuto; no se guardó.", Toast.LENGTH_SHORT).show()
                    terminar()
                }
            }
            return
        }
        Encabezado("Empezar a medir", "Toca qué vas a hacer. El tiempo corre aunque cierres esto; lo terminas desde la notificación, el widget o aquí.")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp), maxItemsInEachRow = 3) {
            (Catalogo.ENFOQUE + Catalogo.SIESTA).forEach { o ->
                Surface(onClick = { io.github.ruddy2003.vestigium.data.Enfoque.iniciar(ctx, o.id); Toast.makeText(ctx, "Corriendo: ${o.nombre.lowercase()}", Toast.LENGTH_SHORT).show(); terminar() },
                    modifier = Modifier.weight(1f).heightIn(min = 52.dp), shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.surface,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)) {
                    Box(contentAlignment = Alignment.Center) { Text(o.nombre, style = MaterialTheme.typography.titleSmall) }
                }
            }
        }
        // Una siesta (o un bloque) que ya pasó.
        var tipo by remember { mutableStateOf("siesta") }
        var hora by remember { mutableStateOf("") }
        var min by remember { mutableStateOf("") }
        Etiqueta("¿Ya pasó? Anótalo")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            (listOf(Catalogo.SIESTA) + Catalogo.ENFOQUE).forEach { o -> Ficha(o.nombre, tipo == o.id) { tipo = o.id } }
        }
        Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = hora, onValueChange = { hora = it.filter { ch -> ch.isDigit() || ch == ':' }.take(5) }, modifier = Modifier.weight(1f), singleLine = true,
                label = { Text("Empezó (15:30)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            OutlinedTextField(value = min, onValueChange = { min = it.filter { ch -> ch.isDigit() }.take(4) }, modifier = Modifier.weight(1f), singleLine = true,
                label = { Text("Minutos") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
        }
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), horizontalArrangement = Arrangement.End) {
            BotonPrincipal("Guardar", habilitado = hora.length >= 4 && (min.toIntOrNull() ?: 0) > 0) {
                val t = io.github.ruddy2003.vestigium.data.Enfoque.agregar(tipo, Fechas.diaLogico(), if (hora.contains(':')) hora else hora.dropLast(2) + ":" + hora.takeLast(2), min.toInt())
                Toast.makeText(ctx, if (t != null) "Guardado" else "Revisa la hora (por ejemplo 15:30).", Toast.LENGTH_SHORT).show()
                if (t != null) terminar()
            }
        }
    }

    @Composable
    private fun GastoRapido(catInicial: String?) {
        var monto by remember { mutableStateOf("") }
        var cat by remember { mutableStateOf(catInicial) }
        var imprevisto by remember { mutableStateOf(false) }
        var nota by remember { mutableStateOf("") }
        var error by remember { mutableStateOf<String?>(null) }
        Encabezado("Registrar gasto", "En el momento de pagar. Monto y categoría bastan.")
        OutlinedTextField(value = monto, onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' }; error = null }, modifier = Modifier.fillMaxWidth(),
            prefix = { Text("$ ") }, placeholder = { Text("0") }, singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), textStyle = MaterialTheme.typography.headlineSmall)
        Etiqueta("Categoría")
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Repo.config.categorias.forEach { c -> Ficha(c, cat == c) { cat = if (cat == c) null else c; error = null } }
        }
        Interruptor("¿Fue imprevisto?", imprevisto) { imprevisto = it }
        OutlinedTextField(value = nota, onValueChange = { nota = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Nota opcional") }, singleLine = true)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 6.dp)) }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.End) {
            BotonPrincipal("Registrar") {
                val v = monto.toDoubleOrNull()
                when {
                    v == null || v <= 0 -> error = "Escribe un monto mayor a cero."
                    cat == null -> error = "Elige una categoría."
                    else -> {
                        val g = Gasto(Repo.nuevoId(), Fechas.hoyCalendario(), Fechas.horaAhora(), Math.round(v * 100) / 100.0, cat!!, imprevisto, nota.trim())
                        Repo.guardarGasto(g)
                        lifecycleScope.launch {
                            val loc = Ubicacion.ultima(this@QuickActivity)
                            if (loc != null) Repo.guardarGasto(g.copy(lat = loc.latitude, lon = loc.longitude))
                        }
                        Toast.makeText(this@QuickActivity, "Gasto registrado", Toast.LENGTH_SHORT).show()
                        terminar()
                    }
                }
            }
        }
    }

    @Composable
    private fun SuenoRapido() {
        val fecha = Fechas.diaLogico()
        var d by remember { mutableStateOf(Repo.dia(fecha)) }
        fun guardar(n: io.github.ruddy2003.vestigium.data.Dia) { d = n; Repo.guardarDia(n) }
        Encabezado("Tu sueño", "Antes de levantarte o revisar el teléfono.")
        Etiqueta("Calidad del sueño")
        Escala(d.sueno_calidad, 1, 5, Catalogo.ANCLAS["sueno_calidad"]) { guardar(d.copy(sueno_calidad = it)) }
        Interruptor("¿Recordaste algo?", d.sueno_recordado) { guardar(d.copy(sueno_recordado = it)) }
        if (d.sueno_recordado) {
            Etiqueta("Narración")
            OutlinedTextField(value = d.sueno_texto, onValueChange = { guardar(d.copy(sueno_texto = it)) }, modifier = Modifier.fillMaxWidth(), minLines = 4,
                placeholder = { Text("Dicta con el micrófono: es más rápido recién despierto.") })
            Row(Modifier.padding(top = 8.dp)) { BotonDictado { t -> guardar(d.copy(sueno_texto = (d.sueno_texto + " " + t).trim())) } }
            Etiqueta("Qué tan agradable fue")
            Escala(d.sueno_valencia, 1, 7, Catalogo.ANCLAS["sueno_valencia"]) { guardar(d.copy(sueno_valencia = it)) }
            Ayuda("Lucidez, vividez y tipo los puedes completar después en la app.", Modifier.padding(top = 8.dp))
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
