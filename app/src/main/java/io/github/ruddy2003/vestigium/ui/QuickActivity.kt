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
        const val M_GASTO = "gasto"; const val M_SUENO = "sueno"; const val M_MINIMO = "minimo"
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

        setContent {
            TemaVestigium {
                Box(
                    Modifier.fillMaxSize().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { terminar() },
                    contentAlignment = Alignment.BottomCenter,
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { }
                            .navigationBarsPadding().imePadding(),
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        Column(Modifier.padding(18.dp).verticalScroll(rememberScrollState())) {
                            when (modo) {
                                M_ANIMO -> Animo(inicial, origen, avisoMs)
                                M_MOMENTO, M_MENU -> MomentoCaptura(inicial, origen)
                                M_GASTO -> GastoRapido()
                                M_SUENO -> SuenoRapido()
                                M_MINIMO -> Minimo()
                                else -> MomentoCaptura(null, origen)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun terminar() { runCatching { Widgets.actualizar(this) }; finish() }

    /** Crea y guarda el momento al instante; la ubicación se añade en cuanto llega. */
    fun crear(tipo: String, origen: String, valencia: Int?, avisoMs: Long?): Momento {
        val m = Momento(
            id = Repo.nuevoId(), ts = Fechas.ahoraIso(), fecha = Fechas.diaLogico(), tipo = tipo, valencia = valencia,
            origen = origen, aviso_ts = avisoMs?.let { Fechas.isoDeMillis(it) },
        )
        Repo.guardarMomento(m)
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
        Text(titulo, style = MaterialTheme.typography.headlineSmall)
        Ayuda(sub, Modifier.padding(top = 2.dp, bottom = 12.dp))
    }

    @Composable
    private fun Animo(inicial: Momento?, origen: String, avisoMs: Long?) {
        var m by remember { mutableStateOf(inicial) }
        var valencia by remember { mutableStateOf(inicial?.valencia) }
        var activacion by remember { mutableStateOf<Int?>(null) }
        var nota by remember { mutableStateOf("") }
        Encabezado("¿Cómo te sientes ahora?", if (m != null) "Guardado a las ${Fechas.horaDe(m!!.ts)}. Agrega energía si quieres." else "Qué tan agradable y cuánta energía, en este momento.")
        Etiqueta("Qué tan agradable")
        Escala(valencia, 1, 7, Catalogo.ANCLAS["valencia"]) { v ->
            valencia = v
            if (v != null) { val actual = m; if (actual == null) m = crear("muestreo", origen, v, avisoMs) else actualizar(actual.id) { it.copy(valencia = v) } }
        }
        Etiqueta("Energía")
        Escala(activacion, 1, 7, Catalogo.ANCLAS["activacion"]) { v ->
            activacion = v
            m?.let { mm -> actualizar(mm.id) { it.copy(activacion = v) } }
        }
        Etiqueta("Nota (opcional)")
        OutlinedTextField(value = nota, onValueChange = { nota = it; m?.let { mm -> actualizar(mm.id) { x -> x.copy(texto = it) } } },
            modifier = Modifier.fillMaxWidth(), placeholder = { Text("¿Qué está pasando? Usa @ y #") })
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
                Catalogo.MOMENTOS.forEach { t ->
                    Surface(
                        onClick = { m = crear(t.id, origen, null, null) },
                        modifier = Modifier.weight(1f).heightIn(min = 56.dp),
                        shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    ) { Box(contentAlignment = Alignment.Center) { Text(if (discreto) t.discreto else t.nombre, style = MaterialTheme.typography.titleMedium) } }
                }
            }
        } else {
            val mm = m!!
            val t = Catalogo.tipo(mm.tipo)
            Encabezado("Guardado: ${if (discreto) t?.discreto ?: "" else t?.nombre ?: mm.tipo}", "A las ${Fechas.horaDe(mm.ts)}. Lo demás es opcional; puedes cerrar.")
            Etiqueta(t?.pregunta ?: "Detalle")
            OutlinedTextField(value = texto, onValueChange = { texto = it; actualizar(mm.id) { x -> x.copy(texto = it) } },
                modifier = Modifier.fillMaxWidth(), minLines = 2, placeholder = { Text("Usa @persona y #tema") })
            Row(Modifier.padding(top = 8.dp)) { BotonDictado { d -> texto = (texto + " " + d).trim(); actualizar(mm.id) { x -> x.copy(texto = texto) } } }
            Etiqueta("Intensidad")
            Escala(intensidad, 1, 5, mapOf(1 to "Muy leve", 2 to "Leve", 3 to "Moderada", 4 to "Fuerte", 5 to "Muy fuerte")) { v ->
                intensidad = v; actualizar(mm.id) { it.copy(intensidad = v) }
            }
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                TextButton(onClick = { Repo.borrarMomento(mm.id); Toast.makeText(this@QuickActivity, "Borrado", Toast.LENGTH_SHORT).show(); terminar() }) { Text("Deshacer") }
                BotonPrincipal("Listo") { terminar() }
            }
        }
    }

    @Composable
    private fun GastoRapido() {
        var monto by remember { mutableStateOf("") }
        var cat by remember { mutableStateOf<String?>(null) }
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
