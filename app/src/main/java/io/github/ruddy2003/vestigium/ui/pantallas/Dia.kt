@file:OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.ui.comun.CampoTexto
import io.github.ruddy2003.vestigium.data.porNumero

import io.github.ruddy2003.vestigium.data.privado
import io.github.ruddy2003.vestigium.data.esAnimo
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.ruddy2003.vestigium.ciencia.Movilidad
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Clima
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Experimentos
import io.github.ruddy2003.vestigium.data.Luna
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Senalada
import io.github.ruddy2003.vestigium.data.Senaladas
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.sensores.Actividad
import io.github.ruddy2003.vestigium.sensores.Galeria
import io.github.ruddy2003.vestigium.sensores.Sincronizar
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.aMillis
import io.github.ruddy2003.vestigium.ui.Aviso
import io.github.ruddy2003.vestigium.ui.BotonDictado
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Clave
import io.github.ruddy2003.vestigium.ui.Editor
import io.github.ruddy2003.vestigium.ui.Escala
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Ficha
import io.github.ruddy2003.vestigium.ui.FilaFichas
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Interruptor
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Opciones
import io.github.ruddy2003.vestigium.ui.Romano
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.Tarjeta
import io.github.ruddy2003.vestigium.ui.Vacio
import io.github.ruddy2003.vestigium.ui.tipoNombre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PantallaDia(nav: Nav, apilado: Boolean = false, nivel: Int = 0) {
    // La pestaña recuerda la fecha abierta mientras vas a la hoja de la noche y vuelves. Un día apilado (abierto desde el
    // calendario, el diario…) recuerda la suya según su nivel en la pila: antes, al volver de una pantalla abierta encima,
    // mostraba el día de hoy y se podía escribir en el día equivocado.
    var fecha by remember { mutableStateOf(Seleccion.pedida ?: (if (apilado) Seleccion.diaApilado(nivel) else Seleccion.diaAbierto) ?: Fechas.diaLogico()) }
    LaunchedEffect(fecha) { if (!apilado) Seleccion.diaAbierto = fecha else Seleccion.recordarDiaApilado(nivel, fecha) }
    val guardado = Repo.dias[fecha]
    var d by remember(fecha) { mutableStateOf(guardado ?: Dia(fecha = fecha)) }
    var sucio by remember(fecha) { mutableStateOf(false) }
    val dActual by rememberUpdatedState(d)
    val sucioActual by rememberUpdatedState(sucio)

    val pedida = Seleccion.pedida
    LaunchedEffect(pedida) {
        if (pedida != null) {
            if (pedida != fecha) { if (sucio) Repo.guardarDia(d); fecha = pedida }
            Seleccion.pedida = null
        }
    }
    LaunchedEffect(guardado) { if (!sucio && guardado != null) d = guardado }
    LaunchedEffect(d, sucio) { if (sucio) { delay(700); Repo.guardarDia(d); sucio = false } }
    DisposableEffect(fecha) { onDispose { if (sucioActual) Repo.guardarDia(dActual) } }
    fun cambiar(n: Dia) { d = n; sucio = true }
    fun irA(f: String) { if (sucio) Repo.guardarDia(d); fecha = f; Seleccion.ultimoMes = Fechas.mesDe(f) }

    val c = Repo.config
    val hoy = Fechas.diaLogico()
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val reqSueno = remember { BringIntoViewRequester() }
    val reqTelefono = remember { BringIntoViewRequester() }
    var abrirTelefono by remember { mutableStateOf(0) }
    var editarSueno by remember(fecha) { mutableStateOf(false) }
    // Desde el buscador («corregir las horas de sueño»): la corrección ya abierta.
    LaunchedEffect(Seleccion.editarSueno, fecha) { if (Seleccion.editarSueno) { Seleccion.editarSueno = false; editarSueno = true } }
    // Lo que se está corrigiendo desde «Tu día»: un momento, tramo, gasto, ingreso, toma o nota (N-26).
    var abierto by remember(fecha) { mutableStateOf<Any?>(null) }
    /** Abre una sección (si estaba plegada) y la trae a la vista. */
    fun mostrar(titulo: String, r: BringIntoViewRequester) {
        if (titulo in Repo.config.plegadas) Repo.cambiarConfig { cc -> cc.copy(plegadas = cc.plegadas - titulo) }
        scope.launch { kotlinx.coroutines.delay(120); r.bringIntoView() }
    }
    /** «Ponerte al día»: la hoja de la noche del día más viejo que falta, y de ahí al siguiente (N-09). */
    fun ponerte() {
        val faltan = Pendientes.faltantes(hoy)
        if (faltan.isEmpty()) return
        if (sucio) { Repo.guardarDia(d); sucio = false }
        Seleccion.ponerse = faltan; Seleccion.noche = faltan.first(); Seleccion.seccion = "Ánimo y cuerpo"; nav.ir("noche")
    }
    /** Abre la hoja de la noche de este día, opcionalmente en una sección. */
    fun abrirNoche(sec: String? = null) {
        if (sucio) { Repo.guardarDia(d); sucio = false }
        Seleccion.noche = fecha; Seleccion.seccion = sec
        nav.ir("noche")
    }
    // Aquí había un efecto que, al ver una sección pedida, abría la hoja de la noche. Sobraba: quien pide una sección (el
    // aviso, el atajo, el toque largo, «ponerte al día») ya abre la hoja por su cuenta. Y hacía daño: la apilaba otra vez (al
    // guardar quedaba una abierta) y le ponía la fecha que Día tuviera abierta en ese momento, que podía no ser la pedida.
    val umbral = with(LocalDensity.current) { 120.dp.toPx() }
    val sentir = io.github.ruddy2003.vestigium.ui.rememberSentir()
    // Deslizar a los lados cambia de día: a la derecha, el anterior; a la izquierda, el siguiente.
    val deslizar = Modifier.pointerInput(fecha) {
        var total = 0f
        detectHorizontalDragGestures(
            onDragStart = { total = 0f },
            onDragEnd = {
                if (total > umbral) { sentir(io.github.ruddy2003.vestigium.ui.Sentidos.Evento.PASAR); irA(Fechas.sumar(fecha, -1)) }
                else if (total < -umbral) { sentir(io.github.ruddy2003.vestigium.ui.Sentidos.Evento.PASAR); irA(Fechas.sumar(fecha, 1)) }
            },
        ) { _, dx -> total += dx }
    }

    Hoja("Día", modifier = deslizar, encabezado = {
        val est = io.github.ruddy2003.vestigium.ui.Estilo
        fun abrirCalendario() { Seleccion.ultimoMes = Fechas.mesDe(fecha); if (sucio) Repo.guardarDia(d); nav.ir("calendario") }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            if (apilado) BotonTexto(onClick = nav.atras, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp),
                modifier = Modifier.semantics { contentDescription = "Volver" }) {
                Text("←", style = MaterialTheme.typography.titleLarge)
            }
            // «Hoy» cuando es hoy; «Día» en otra fecha (N-23). El nombre del tema y el español en la misma línea (P-1).
            val (nombreT, espanolT, _) = if (fecha == hoy) est.pantallaEnLinea("Hoy", "HODIE") else est.pantallaEnLinea("Día", "DIES")
            Text(androidx.compose.ui.text.buildAnnotatedString {
                if (espanolT != null) withStyle(androidx.compose.ui.text.SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)) { append(nombreT) }
                else append(nombreT)
                append(est.sufijoTitulo)
                if (espanolT != null) withStyle(androidx.compose.ui.text.SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) { append(" · $espanolT") }
            }, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
            io.github.ruddy2003.vestigium.ui.LocalGuia.current?.let { guia -> BotonTexto(onClick = { guia("principios") }) { Text("?", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) } }
            io.github.ruddy2003.vestigium.ui.BotonBuscar { nav.ir("buscar") }
            io.github.ruddy2003.vestigium.ui.BotonAjustes(Modifier.padding(start = 4.dp)) { nav.ir("mas") }
            io.github.ruddy2003.vestigium.ui.BotonModo()
            io.github.ruddy2003.vestigium.ui.Emblema(tam = 34.dp, modifier = Modifier.padding(start = 4.dp))
        }
        // La fecha grande abre el calendario.
        // Su tamaño, espaciado, peso, inclinación y el signo que la abre son de cada tema (Estilo.fechaGrande).
        val ef = est.fechaGrande
        val base = MaterialTheme.typography.titleLarge
        Text(est.fecha(fecha) + "  " + ef.abre, style = base.copy(
            fontSize = base.fontSize * ef.escala,
            lineHeight = if (base.lineHeight.type == androidx.compose.ui.unit.TextUnitType.Unspecified) base.lineHeight else base.lineHeight * ef.escala,
            letterSpacing = ef.espaciado.sp,
            fontWeight = ef.peso?.let { androidx.compose.ui.text.font.FontWeight(it) } ?: base.fontWeight,
            fontStyle = if (ef.cursiva) androidx.compose.ui.text.font.FontStyle.Italic else base.fontStyle,
        ), color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { abrirCalendario() })
        val fase = Luna.fase(fecha)
        Text(est.comentario + "${Fechas.titulo(fecha).lowercase()} · ${Fechas.relativo(fecha)} · ${Luna.glifo(fase)}",
            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        // Si ese día cambió la hora del teléfono (horario de verano o un viaje), se dice: las horas son las locales de cada momento.
        Fechas.cambioDeHora(fecha)?.let { (a, b) ->
            fun utc(x: Double) = "UTC" + (if (x < 0) "−" else "+") + "%.1f".format(kotlin.math.abs(x)).removeSuffix(".0").removeSuffix(",0")
            val dif = b - a
            Text("⏱ cambio de horario: de ${utc(a)} a ${utc(b)} · el día duró ${"%.1f".format(24 - dif).removeSuffix(".0").removeSuffix(",0")} h",
                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }) {
        // El contador vive dentro del bloque para que la numeración se reinicie en cada recomposición.
        var k = 0
        fun n() = ++k
        val ahora = java.time.LocalTime.now().hour
        val esHoy = fecha == hoy
        // El orden es el mismo a cualquier hora; solo en la mañana sube el sueño, si falta (N-16).
        val manana = esHoy && ahora in 5..11

        // Estado del día, con flechas pequeñas (también se cambia de día deslizando a los lados).
        Row(Modifier.fillMaxWidth().padding(top = 4.dp), verticalAlignment = Alignment.CenterVertically) {
            BotonTexto(onClick = { irA(Fechas.sumar(fecha, -1)) }, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp),
                modifier = Modifier.semantics { contentDescription = "Día anterior" }) { Text("‹", style = MaterialTheme.typography.titleLarge) }
            Text("estado = ", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(when { sucio -> "guardando…"; guardado != null -> "guardado"; else -> "vacío" } + (if (guardado?.retroactivo == true) " · retroactivo" else ""),
                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            val r = remember(fecha, Repo.dias.size) { racha(fecha) }
            // Cada semana completa de racha se celebra (solo al llegar, no al abrir el día).
            var rachaAntes by remember(fecha) { mutableStateOf<Int?>(null) }
            LaunchedEffect(r) {
                val antes = rachaAntes
                if (antes != null && r > antes && r % 7 == 0 && !io.github.ruddy2003.vestigium.ui.Reanudar.recien()) { io.github.ruddy2003.vestigium.ui.Fiesta.lanzar(); sentir(io.github.ruddy2003.vestigium.ui.Sentidos.Evento.LOGRO) }
                rachaAntes = r
            }
            if (r >= 2) Text("  ·  racha $r", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.weight(1f))
            if (!esHoy) BotonTexto(onClick = { irA(hoy) }) { Text("hoy", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
            BotonTexto(onClick = { irA(Fechas.sumar(fecha, 1)) }, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp),
                modifier = Modifier.semantics { contentDescription = "Día siguiente" }) { Text("›", style = MaterialTheme.typography.titleLarge) }
        }
        // Semana corta: solo cuatro cifras y tres botones.
        if (esHoy && c.semana_corta) { SemanaCorta(d) { abrirNoche() }; return@Hoja }
        // La frase, siempre arriba, justo debajo del estado.
        FraseDelDia(fecha)
        // Solo hoy: el cuadro del tema, con su gato dentro y el botón de su cuento, o el gato de pixel en su banda. Nunca los dos.
        if (esHoy) {
            val temaDeHoy = io.github.ruddy2003.vestigium.ui.Estilo.tema
            if (c.gato && io.github.ruddy2003.vestigium.ui.escenas.hayCuadroEnHoy(temaDeHoy, c.gato_estilo)) io.github.ruddy2003.vestigium.ui.escenas.CuadroDelDia(
                temaDeHoy, hoy, alLeer = { vida -> nav.ir("cuento?vida=$vida") },
                // La pulsación larga abre «Hoy en la historia» cuando exista esa pantalla (tanda H1) y esté encendida en Ajustes.
                alHistoria = if (io.github.ruddy2003.vestigium.ui.escenas.HAY_HISTORIA_HOY && c.historia_hoy) { { nav.ir("historia_hoy") } } else null,
            )
            else io.github.ruddy2003.vestigium.ui.GatoDelDia(hoy)
        }
        // En lugar del personaje: tus últimos días, de un vistazo (N-28). Tocar un día lo abre; hoy abre «Llenar el día».
        if (esHoy) FranjaDias(hoy, abrir = { f -> if (f == hoy) abrirNoche() else { if (sucio) { Repo.guardarDia(d); sucio = false }; Seleccion.abrir(nav, f) } }) { ponerte() }
        if (esHoy) SugerirSemanaCorta(hoy)

        val bloqueSueno: @Composable () -> Unit = {
        Seccion("Sueño", "Llénalo apenas despiertes. El resto, en la noche.", numero = n(), modifier = Modifier.bringIntoViewRequester(reqSueno), plegable = true) {
                Tarjeta {
                    io.github.ruddy2003.vestigium.ui.FormularioSueno(fecha, d) { cambiar(it) }
                    val sr = Repo.salud[fecha]
                    sr?.sueno_min?.let { m -> ClaveDia("horologium", "${m / 60} h ${m % 60} min dormido " + if (sr.sueno_manual) "(corregido a mano)" else "según tu reloj") }
                    BotonTexto(onClick = { editarSueno = true }) {
                        Text(if (sr?.sueno_min != null) "corregir las horas de sueño" else "anotar a qué hora dormí y desperté", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
        // «Te falta hoy» y lo que se contesta ahí mismo: lo de anoche, el experimento, los datos que faltan y «¿quién es?» (N-16).
        val pendientes: @Composable () -> Unit = {
            if (esHoy) {
                LoDeAnoche()
                TeFaltaHoy(d, hoy, setOf("p:diario", "p:prediccion")) { a ->
                    when (a) {
                        "ayer" -> irA(Fechas.sumar(hoy, -1))
                        "ponerse" -> ponerte()
                        "sueno" -> mostrar("Sueño", reqSueno)
                        "diario" -> abrirNoche("Ánimo y cuerpo")
                        "prediccion" -> abrirNoche("Predicción")
                        "aviso" -> abrirAviso(ctx)
                        "escalas" -> nav.ir("escalas")
                        "gasto" -> nav.ir("gastos")
                        "politica" -> nav.ir("politica")
                        "reaccion" -> nav.ir("reaccion")
                    }
                }
            }
            // También en los terminados, para contestar los días que quedaron sin respuesta (hasta su último día y nunca en el futuro).
            Repo.experimentos.filter { Experimentos.cubre(it, fecha) && fecha <= Fechas.diaLogico() && !io.github.ruddy2003.vestigium.data.Privado.esVariable(it.resultado) }.forEach { e ->
                AvisoExperimento(e, fecha)
            }
            if (esHoy) {
                DatosQueFaltan(nav, compacto = true)
                if (c.personas_pospuesto != hoy) TarjetaQuienEs(Modifier.padding(top = 10.dp), compacta = true) { nav.ir("personas") }
            }
        }

        if (esHoy && ahora < 5) Aviso("Pasa de medianoche: sigues llenando el día $fecha. Los días empiezan a las 5:00.")
        if (fecha > hoy) Aviso("Este día todavía no llega. Puedes dejar notas; queda guardado cuándo las escribiste.")

        // Lo diario en el primer alto, siempre en el mismo orden (N-16):
        // 1. Lo que falta hoy.
        pendientes()
        // 2. Llenar el día.
        TarjetaNoche(d, fecha, c) { abrirNoche() }
        // 3. El tablero, compacto: las primeras cuatro cifras y «ver todo».
        Seccion("Tablero", "Tus cifras del día y la semana, contra tu promedio de las 4 semanas anteriores. Toca una para corregirla o ver de dónde sale.") {
            Tablero(fecha, max = 4) { id ->
                when (id) {
                    "sueno" -> editarSueno = true
                    "animo" -> abrirNoche("Ánimo y cuerpo")
                    "palabras" -> abrirNoche("Diario")
                    "gasto" -> nav.ir("gastos")
                    // Lo que mide el teléfono: se abre «Lo que registró el teléfono» y se trae a la vista.
                    else -> { abrirTelefono++; scope.launch { kotlinx.coroutines.delay(200); reqTelefono.bringIntoView() } }
                }
            }
        }
        // 4. En la mañana, si falta, la calidad del sueño.
        val suenoArriba = manana && d.sueno_calidad == null
        if (suenoArriba) bloqueSueno()

        // Debajo: lo que va pasando (N-17), el sueño, los días especiales y este día hace un año.
        TuDia(fecha, nav, numero = n(), guardarAntes = { if (sucio) { Repo.guardarDia(d); sucio = false } }) { abierto = it }
        if (!suenoArriba) bloqueSueno()
        io.github.ruddy2003.vestigium.data.Especiales.de(fecha).takeIf { it.isNotEmpty() }?.let { l ->
            Aviso((if (esHoy) "Hoy: " else "Este día: ") + l.joinToString(" · ") { io.github.ruddy2003.vestigium.data.Especiales.frase(it) } + ".")
        }
        HaceUnAnio(fecha, nav)
        if (esHoy) UnaIdeaDeAntes(hoy, nav)

        // Lo automático, al fondo.
        Seccion("Lo que registró el teléfono", "Se llena solo con los sensores que activaste. Nada de esto tienes que escribirlo.", numero = n(), autoPlegar = true,
            abrir = abrirTelefono, modifier = Modifier.bringIntoViewRequester(reqTelefono)) {
            Vestigios(fecha, nav)
        }
        if (editarSueno) io.github.ruddy2003.vestigium.ui.EditarSueno(fecha) { editarSueno = false }
        when (val x = abierto) {
            is io.github.ruddy2003.vestigium.data.Momento -> DialogoMomento(x) { abierto = null }
            is io.github.ruddy2003.vestigium.data.Tramo -> DialogoTramo(x) { abierto = null }
            is io.github.ruddy2003.vestigium.data.Gasto -> DialogoGasto(x, nuevo = Repo.gastos.none { it.id == x.id }) { abierto = null }
            is io.github.ruddy2003.vestigium.data.Ingreso -> DialogoIngreso(x) { abierto = null }
            is io.github.ruddy2003.vestigium.data.TomaSuplemento -> DialogoToma(x) { abierto = null }
            is io.github.ruddy2003.vestigium.data.Nota -> DialogoNota(x) { abierto = null }
            is io.github.ruddy2003.vestigium.data.Visita -> DialogoVisita(x) { abierto = null }
        }

    }
}

/** Resumen de la noche en Día: qué falta y el botón para abrir la hoja. */
@Composable
private fun TarjetaNoche(d: Dia, fecha: String, c: io.github.ruddy2003.vestigium.data.Config, abrir: () -> Unit) {
    val activos = io.github.ruddy2003.vestigium.data.Privado.habitosVisibles(c).filter { it.activo }
    val hechos = activos.count { d.habitos[it.id] == true }
    val partes = listOf(
        "ánimo" to (d.valencia != null), "predicción" to (d.prediccion_manana != null),
    ) + (if (activos.isNotEmpty()) listOf("hábitos $hechos/${activos.size}" to (hechos == activos.size)) else emptyList()) + listOf("diario" to d.nota.isNotBlank())
    val listo = d.valencia != null && d.nota.isNotBlank()
    val dc = io.github.ruddy2003.vestigium.ui.comun.disenoComun(io.github.ruddy2003.vestigium.ui.Estilo.tema)
    val osc = io.github.ruddy2003.vestigium.ui.esOscuro()
    val tipo = io.github.ruddy2003.vestigium.ui.comun.TipoTarjeta.NOCHE
    io.github.ruddy2003.vestigium.ui.comun.TarjetaTema(tipo, Modifier.fillMaxWidth().padding(top = 18.dp), destacado = listo) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                // El nombre del tema y, debajo, el mismo comentario en español que llevan las demás secciones (hallazgo 10).
                val (nombreNoche, comentarioNoche) = io.github.ruddy2003.vestigium.ui.Estilo.seccion("Llenar el día", Romano.latin("Llenar el día"))
                Text(nombreNoche, style = MaterialTheme.typography.titleLarge)
                if (comentarioNoche != null) Text(comentarioNoche, style = MaterialTheme.typography.labelMedium, color = dc.rotuloTarjeta(tipo, osc))
                Text(partes.joinToString("  ") { (t, ok) -> (if (ok) "✓ " else "○ ") + t }, style = MaterialTheme.typography.labelMedium,
                    color = dc.rotuloTarjeta(tipo, osc), modifier = Modifier.padding(top = 4.dp))
                if (listo) Text("día sellado", style = MaterialTheme.typography.labelMedium, color = dc.acentoTarjeta(tipo, osc))
            }
            io.github.ruddy2003.vestigium.ui.BotonPrincipal(if (listo) "abrir" else "llenar") { abrir() }
        }
    }
}

/**
 * La hoja de la noche: todo lo que se llena al cerrar el día, en una sola página.
 * Se guarda sola mientras escribes; al terminar, «sellar el día» la cierra con la huella.
 */
@Composable
fun PantallaNoche(nav: Nav) {
    val ctx = LocalContext.current
    var fecha by remember { mutableStateOf(Seleccion.noche ?: Fechas.diaLogico()) }
    // «Ponerte al día» se acaba al salir de la hoja.
    DisposableEffect(Unit) { onDispose { Seleccion.ponerse = emptyList() } }
    val guardado = Repo.dias[fecha]
    var d by remember(fecha) { mutableStateOf(guardado ?: Dia(fecha = fecha)) }
    var sucio by remember(fecha) { mutableStateOf(false) }
    val dActual by rememberUpdatedState(d)
    val sucioActual by rememberUpdatedState(sucio)
    // Si la hoja ya estaba abierta (de ayer, sin sellar) y se pide otra fecha (el aviso o el atajo de hoy), cambia a esa:
    // antes seguía en el día con que se abrió y lo de hoy se escribía en ayer. Lo que hubiera sin guardar se guarda primero.
    val nochePedida = Seleccion.noche
    LaunchedEffect(nochePedida) {
        if (nochePedida != null && nochePedida != fecha && Seleccion.ponerse.isEmpty()) { if (sucioActual) Repo.guardarDia(dActual); fecha = nochePedida }
    }
    LaunchedEffect(guardado) { if (!sucio && guardado != null) d = guardado }
    LaunchedEffect(d, sucio) { if (sucio) { delay(700); Repo.guardarDia(d); sucio = false } }
    DisposableEffect(fecha) { onDispose { if (sucioActual) Repo.guardarDia(dActual) } }
    // Cada día que pasa por la hoja guarda con qué orden de preguntas se llenó.
    fun cambiar(n: Dia) { d = n.copy(version_noche = io.github.ruddy2003.vestigium.data.VERSION_NOCHE); sucio = true }
    var escribiendo by remember(fecha) { mutableStateOf(false) }
    if (escribiendo) io.github.ruddy2003.vestigium.ui.DialogoEscritor(fecha, d.nota, { cambiar(d.copy(nota = it)) }) {
        Repo.guardarDia(dActual); sucio = false; escribiendo = false
    }
    val c = Repo.config
    val scope = rememberCoroutineScope()
    val reqAnimo = remember { BringIntoViewRequester() }
    val reqDiario = remember { BringIntoViewRequester() }
    val reqPred = remember { BringIntoViewRequester() }
    val reqHabitos = remember { BringIntoViewRequester() }
    val reqSellar = remember { BringIntoViewRequester() }
    LaunchedEffect(Seleccion.seccion) {
        val sec = Seleccion.seccion ?: return@LaunchedEffect
        Seleccion.seccion = null
        if (sec in Repo.config.plegadas) Repo.cambiarConfig { cc -> cc.copy(plegadas = cc.plegadas - sec) }
        delay(300)
        when (sec) { "Diario" -> reqDiario; "Predicción" -> reqPred; else -> reqAnimo }.bringIntoView()
    }
    val est = io.github.ruddy2003.vestigium.ui.Estilo
    val completo = d.valencia != null && d.nota.isNotBlank()
    fun sellar() {
        Repo.guardarDia(d); sucio = false
        if (completo) io.github.ruddy2003.vestigium.ui.Sello.lanzar(fecha)
        // Poniéndote al día: sigue con el siguiente día que falta.
        val siguiente = Seleccion.ponerse.firstOrNull { it > fecha }
        if (siguiente != null) fecha = siguiente else nav.atras()
    }
    // Lo esencial, en orden: lo que falta se puede saltar con «siguiente».
    val habitosActivos = io.github.ruddy2003.vestigium.data.Privado.habitosVisibles(c).filter { it.activo }
    // Lo que pide «Sellar el día» (ánimo y diario) es lo que cuenta la barra; predicción y hábitos van aparte, como extras.
    // Los hábitos están hechos cuando todos tienen respuesta, «sí» o «no».
    val pasos = listOf(Triple("ánimo", d.valencia != null, reqAnimo), Triple("diario", d.nota.isNotBlank(), reqDiario))
    val extras = listOfNotNull(
        Triple("predicción", d.prediccion_manana != null, reqPred),
        if (habitosActivos.isNotEmpty()) Triple("hábitos", habitosActivos.all { d.habitos.containsKey(it.id) }, reqHabitos) else null,
    )
    androidx.compose.foundation.layout.Box(Modifier.fillMaxSize()) {
    Hoja("Llenar el día", nav.atras,
        est.fecha(fecha) + " · " + Fechas.titulo(fecha).lowercase() + " · " + Fechas.relativo(fecha) + ". Se guarda solo mientras escribes.") {
        var k = 0
        fun n() = ++k
        // «Solo lo que falta»: lo que ya estaba lleno al abrir la hoja queda resumido arriba (lo que llenas ahora no desaparece).
        val soloFalta = c.noche_solo_falta
        val llenoAlAbrir = remember(fecha) {
            val g = guardado ?: Dia(fecha = fecha)
            setOfNotNull(
                if (g.valencia != null && g.activacion != null && g.dolor != null) "ánimo" else null,
                if (g.ctx.isNotEmpty()) "contexto" else null,
                if (g.nota.isNotBlank()) "diario" else null,
                if (g.prediccion_manana != null) "predicción" else null,
                if (g.foto != null) "foto" else null,
            )
        }
        fun ver(sec: String) = !soloFalta || sec !in llenoAlAbrir
        if (fecha == Fechas.diaLogico()) io.github.ruddy2003.vestigium.ui.AvisoMadrugada(Modifier.padding(top = 6.dp))
        Row(Modifier.fillMaxWidth().padding(top = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(if (soloFalta && llenoAlAbrir.isNotEmpty()) "Ya está: " + llenoAlAbrir.joinToString(" · ") { "✓ $it" } else "Todo el día en una hoja.",
                style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
            Ficha(if (soloFalta) "solo lo que falta" else "mostrar todo", soloFalta) { Repo.cambiarConfig { cc -> cc.copy(noche_solo_falta = !cc.noche_solo_falta) } }
        }
        // Poniéndote al día: los días que faltan, para pasar de uno a otro.
        if (Seleccion.ponerse.size > 1) Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Ponerte al día:", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Seleccion.ponerse.forEach { f ->
                val lleno = Repo.dias[f]?.let { it.valencia != null } == true
                Ficha((if (lleno) "✓ " else "") + Fechas.corto(f), f == fecha) { if (sucio) { Repo.guardarDia(d); sucio = false }; fecha = f }
            }
        }
        // Día difícil: lo marcas tú aquí (esquema 23). Es aparte del «registro mínimo» de abajo, que solo dice por dónde se anotó.
        Ficha((if (d.dificil) "✓ " else "") + "Fue un día difícil", d.dificil) { cambiar(d.copy(dificil = !d.dificil)) }
        // El registro mínimo se marca al anotar la valencia desde el aviso; aquí se ve y se puede quitar si fue un toque equivocado.
        if (d.dia_dificil) Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("La valencia de este día se anotó desde el aviso (registro mínimo).", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f))
            BotonTexto(onClick = { cambiar(d.copy(dia_dificil = false)) }) { Text("quitar", style = MaterialTheme.typography.labelLarge) }
        }
        // 1. El ánimo de este momento, antes de repasar el día: recordar y escribir lo cambian (N-18). Solo en la hoja de hoy:
        //    en un día pasado se guardaría «ahora» con la fecha de otro día (H-2-8).
        if (io.github.ruddy2003.vestigium.data.cierrePermitido(fecha))
            Seccion("Cómo estás ahora", "Antes de repasar el día, con la hora de ahora. Se compara de un día a otro.", numero = n()) { BloqueCierre(fecha) }
        // 2. El juicio general del día, antes de cualquier pregunta específica (Schwarz, Strack y Mai, 1991).
        if (ver("ánimo")) Seccion("Ánimo y cuerpo", numero = n(), modifier = Modifier.bringIntoViewRequester(reqAnimo)) {
            Tarjeta {
                Etiqueta("Valencia · qué tan agradable fue el día")
                Escala(d.valencia, 1, 7, Catalogo.ANCLAS["valencia"]) { cambiar(d.copy(valencia = it)) }
                Etiqueta("Activación · tu nivel de energía")
                // Paleta secuencial: más o menos energía no es «mejor» ni «peor» (H-2-11).
                Escala(d.activacion, 1, 7, Catalogo.ANCLAS["activacion"], secuencial = true) { cambiar(d.copy(activacion = it)) }
                Etiqueta("Dolor o molestia física")
                Escala(d.dolor, 0, 5, Catalogo.ANCLAS["dolor"]) { cambiar(d.copy(dolor = it)) }
                val mu = Repo.momentos.filter { it.fecha == fecha && it.esAnimo && it.valencia != null }
                // Los avisos del día se ven solo después de elegir la valencia, para que no sirvan de ancla (H-2-11).
                if (mu.isNotEmpty() && d.valencia != null) ClaveDia("avisos", mu.joinToString("  ") { "${Fechas.horaDe(it.ts)}→${it.valencia}" })
            }
        }
        // 3. La predicción, junto al juicio del día y antes de escribir.
        if (ver("predicción")) Seccion("Predicción", "¿Cómo crees que estará tu ánimo mañana? La app medirá qué tan bien te conoces.", numero = n(), modifier = Modifier.bringIntoViewRequester(reqPred)) {
            Escala(d.prediccion_manana, 1, 7, Catalogo.ANCLAS["prediccion_manana"]) { cambiar(d.copy(prediccion_manana = it)) }
        }
        // 4 y 5. Hechos: contexto, hábitos y suplementos.
        if (ver("contexto")) Seccion("Cómo fue el día", "El día completo: activa todo lo que aplique. Pueden ser varios o ninguno.", numero = n()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Catalogo.CONTEXTOS.forEach { o ->
                    val activo = d.ctx[o.id] == true
                    Ficha(o.nombre, activo) { cambiar(d.copy(ctx = if (activo) d.ctx - o.id else d.ctx + (o.id to true))) }
                }
            }
            CampoTexto(value = d.ctx_otro, onValueChange = { cambiar(d.copy(ctx_otro = it.take(80))) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                singleLine = true, placeholder = { Text("otro (opcional)", style = MaterialTheme.typography.bodyMedium) })
        }
        Seccion("Hábitos", "Siempre aquí, para marcarlos al cerrar el día.", numero = n(), modifier = Modifier.bringIntoViewRequester(reqHabitos)) {
            val activos = io.github.ruddy2003.vestigium.data.Privado.habitosVisibles(c).filter { it.activo }
            if (activos.isEmpty()) {
                Tarjeta {
                    Ayuda("Aún no tienes hábitos.")
                    BotonTexto(onClick = { nav.ir("habitos") }) { Text("+ agregar hábitos", style = MaterialTheme.typography.labelLarge) }
                }
            } else Tarjeta {
                // «sí», «no» o sin contestar (tocar otra vez la misma respuesta la quita): «no» ya no es lo mismo que no haber contestado.
                activos.forEach { h ->
                    val r = d.habitos[h.id]
                    fun poner(v: Boolean) = cambiar(d.copy(habitos = if (r == v) d.habitos - h.id else d.habitos + (h.id to v), habitos_tri = true))
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Text(h.nombre, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Ficha("sí", r == true) { poner(true) }
                            Ficha("no", r == false) { poner(false) }
                        }
                    }
                }
                if (activos.any { !d.habitos.containsKey(it.id) }) Ayuda("Sin respuesta queda en blanco, no como «no».", Modifier.padding(top = 4.dp))
            }
        }
        // Lo que tomaste ese día: sirve también para lo que olvidaste anotar (N-09, E-11). «+» suma una toma y «−» quita la última.
        if (io.github.ruddy2003.vestigium.data.Suplementos.activos(c).isNotEmpty())
            Seccion("Suplementos de este día", "Toca + por cada toma; − quita la última.", numero = n(), plegable = true) { SuplementosDelDia(fecha) }
        // 6. El diario, lo que más mueve el ánimo, al final de lo esencial.
        if (ver("diario")) Seccion("Diario", "Escribe libre. Usa @persona y #tema; abajo ves lo que se detecta y si alguna etiqueta parece error de dedo.", numero = n(), modifier = Modifier.bringIntoViewRequester(reqDiario)) {
            // El diario es un objeto del tema (rollo, libro, carta…): al tocarlo se abre a pantalla completa para escribir.
            io.github.ruddy2003.vestigium.ui.ObjetoDiario(fecha, d.nota) { Repo.guardarDia(d); sucio = false; escribiendo = true }
            Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                BotonDictado { t -> cambiar(d.copy(nota = (d.nota + " " + t).trim())) }
            }
            Detectados(d.nota, fecha) { nuevo -> cambiar(d.copy(nota = nuevo)) }
            // Ver a alguien no es lo mismo que pensar en esa persona: un toque cambia entre «estuve con», «pensé en» y «mensajes».
            if (Texto.personas(d.nota).isNotEmpty()) {
                ChipsPresencia(d) { n -> cambiar(n) }
                Ayuda("Toca un nombre si solo pensaste en esa persona o hablaron por mensaje.", Modifier.padding(top = 4.dp))
            }
            BotonTexto(onClick = { nav.ir("biblioteca_diario") }) { Text("abrir la biblioteca del diario →", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
        }

        // 7. Lo opcional, plegado y en orden fijo: lectura, lo mejor y lo peor, episodios, fechas señaladas, obras, foto y lo privado.
        Text("opcional", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 28.dp))
        Seccion("Lectura del día", "Tres preguntas de marcos científicos. Toca «cómo elegir» si dudas.", numero = n(), plegable = true, plegadaPorDefecto = true) {
            Tarjeta {
                Catalogo.CIENTIFICAS.forEachIndexed { i, p ->
                    var abierta by remember { mutableStateOf(false) }
                    if (i > 0) Spacer(Modifier.padding(top = 14.dp))
                    Text(p.pregunta, style = MaterialTheme.typography.titleMedium)
                    BotonTexto(onClick = { abierta = !abierta }, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
                        Text(if (abierta) "− ocultar guía" else "+ cómo elegir · ${p.autor}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    }
                    if (abierta) Ayuda(p.guia, Modifier.padding(bottom = 8.dp))
                    val valor = when (p.clave) { "flow" -> d.flow; "sdt_necesidad" -> d.sdt_necesidad; else -> d.apreciacion }
                    Opciones(p.opciones, valor) { v ->
                        cambiar(when (p.clave) { "flow" -> d.copy(flow = v); "sdt_necesidad" -> d.copy(sdt_necesidad = v); else -> d.copy(apreciacion = v) })
                    }
                }
            }
        }
        Seccion("Lo mejor y lo peor", "Unas palabras de cada uno.", numero = n(), plegable = true, plegadaPorDefecto = true) {
            CampoTexto(value = d.mejor_momento, onValueChange = { cambiar(d.copy(mejor_momento = it)) }, modifier = Modifier.fillMaxWidth(),
                label = { Text("lo mejor", style = MaterialTheme.typography.labelMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.padding(top = 8.dp))
            CampoTexto(value = d.peor_momento, onValueChange = { cambiar(d.copy(peor_momento = it)) }, modifier = Modifier.fillMaxWidth(),
                label = { Text("lo peor", style = MaterialTheme.typography.labelMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
        }
        // Esquema 21: los hitos se dividen en episodios (algo fuerte que te pasa, con hora; se anotan desde ⊕) y fechas
        // señaladas (lo que quieres recordar, con su tipo y una frase). Cada una abierta si el día ya tiene alguna.
        val episodios = Repo.momentos.filter { it.tipo == "episodio" && it.fecha == fecha }.sortedBy { it.ts }
        Seccion("Episodios", "Algo fuerte que te pasó, con su hora: una crisis, un llanto, una pelea, una migraña. Toca uno para corregirlo.", numero = n(),
            plegable = true, plegadaPorDefecto = episodios.isEmpty()) {
            EpisodiosDelDia(fecha, episodios) { if (sucio) { Repo.guardarDia(d); sucio = false } }
        }
        Seccion("Fechas señaladas", "Lo que quieres recordar de este día: una primera vez, un viaje, un logro, un cambio, una pérdida, un encuentro. Una crisis o una pelea van como episodio, en ⊕.",
            numero = n(), plegable = true, plegadaPorDefecto = Senaladas.de(d).isEmpty()) {
            FechasSenaladas(d) { cambiar(it) }
        }
        Seccion("Lo que leí o vi hoy", "Toca la portada de lo que le dedicaste tiempo hoy: libro, serie, película, juego o artículo.", numero = n(), plegable = true, plegadaPorDefecto = true) {
            ObrasDeHoy(fecha, nav)
        }
        Seccion("¿Aprendiste algo hoy?", "De un libro, un video, una plática, o algo que se te ocurrió. Se guarda en tu Ideario.", numero = n(), plegable = true,
            plegadaPorDefecto = Repo.ideas.none { it.fecha == fecha }) {
            IdeasDelDia(fecha, nav) { if (sucio) { Repo.guardarDia(d); sucio = false } }
        }
        if (ver("foto")) Seccion("Foto del día", "Toca «elegir» (o mantén presionada la foto) para que sea la foto del día: sale en el calendario, en la biblioteca del diario y en el widget. Tócala para verla.", numero = n(), plegable = true, plegadaPorDefecto = true) {
            val ctxFoto = LocalContext.current
            val scopeFoto = androidx.compose.runtime.rememberCoroutineScope()
            Imagenes(fecha, d, nav) { uri ->
                val nueva = if (d.foto == uri) null else uri
                cambiar(d.copy(foto = nueva, foto_archivo = null))
                // La copia dentro de la app (esquema 22): así la foto no se pierde si se borra de la galería o cambias de teléfono.
                if (nueva == null) io.github.ruddy2003.vestigium.data.FotoDelDia.borrar(ctxFoto, fecha)
                else scopeFoto.launch {
                    val n = withContext(Dispatchers.IO) { io.github.ruddy2003.vestigium.data.FotoDelDia.copiar(ctxFoto, Uri.parse(nueva), fecha) }
                    if (n != null && d.foto == nueva) cambiar(d.copy(foto_archivo = n))
                }
            }
        }
        // Medicamentos, impulsos, recaídas y libido se anotan y se ven solo en la pantalla Privado.
        BotonTexto(onClick = { if (sucio) { Repo.guardarDia(d); sucio = false }; Seleccion.privado = fecha; nav.ir("recaidas") }) {
            Text("privado de este día →", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        // Cerrar el día: guarda y, si está completo, lo sella con la huella.
        val faltan = listOfNotNull(if (d.valencia == null) "la valencia" else null, if (d.nota.isBlank()) "la nota del diario" else null)
        Spacer(Modifier.height(24.dp))
        io.github.ruddy2003.vestigium.ui.BotonPrincipal(if (completo) "Sellar el día" else "Listo",Modifier.fillMaxWidth().bringIntoViewRequester(reqSellar)) { sellar() }
        if (!completo) Ayuda("Para sellar el día con tu huella falta " + faltan.joinToString(" y ") + ".", Modifier.padding(top = 6.dp))
        Spacer(Modifier.height(72.dp))
    }
    // Barra fija abajo: cuánto llevas y un salto a lo siguiente que falta.
    BarraNoche(pasos.map { it.first to it.second }, extras.map { it.first to it.second }, completo, Modifier.align(Alignment.BottomCenter), sellar = { sellar() }) {
        val sig = pasos.firstOrNull { !it.second } ?: extras.firstOrNull { !it.second }
        scope.launch { (sig?.third ?: reqSellar).bringIntoView() }
    }
    }
}

@Composable
private fun BarraNoche(pasos: List<Pair<String, Boolean>>, extras: List<Pair<String, Boolean>>, completo: Boolean, modifier: Modifier, sellar: () -> Unit, siguiente: () -> Unit) {
    val hechos = pasos.count { it.second }
    val cs = MaterialTheme.colorScheme
    Surface(modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), shape = MaterialTheme.shapes.medium, color = cs.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, cs.primary.copy(alpha = 0.6f)), shadowElevation = 6.dp) {
        Row(Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    pasos.forEach { (_, ok) -> Box(Modifier.weight(1f).height(5.dp).clip(RoundedCornerShape(3.dp)).background(if (ok) cs.primary else cs.outline)) }
                }
                val faltaExtra = extras.filter { !it.second }.map { it.first }
                Text("$hechos de ${pasos.size} · " + (pasos.firstOrNull { !it.second }?.let { "falta ${it.first}" } ?: "listo para sellar") +
                    (if (faltaExtra.isNotEmpty()) " · extra: " + faltaExtra.joinToString(", ") else ""),
                    style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
            // Con el día completo (ánimo y diario), el botón sella de una vez (hallazgo 8); antes solo bajaba hasta «Sellar el día».
            // Los extras que falten (predicción, hábitos) siguen a un toque en «ir a …».
            if (completo && extras.any { !it.second }) BotonTexto(onClick = siguiente) {
                Text("ir a " + (extras.firstOrNull { !it.second }?.first ?: "lo que falta"), style = MaterialTheme.typography.labelLarge, color = cs.onSurfaceVariant)
            }
            BotonTexto(onClick = if (completo) sellar else siguiente) {
                Text(if (completo) "Sellar el día" else "siguiente →", style = MaterialTheme.typography.labelLarge, color = cs.primary)
            }
        }
    }
}

/** Las líneas del reporte del día: si la clave del tema va en otro alfabeto, lleva su español debajo. */
@Composable
private fun ClaveDia(clave: String, valor: String) = Clave(clave, valor, espanol = true)

private fun hm(min: Int): String = if (min >= 60) "${min / 60} h ${"%02d".format(min % 60)} min" else "$min min"

/** Todo lo automático del día: las fichas del reporte (ver ReporteDia.kt) y debajo lo demás, en formato clave = valor. */
@Composable
private fun Vestigios(fecha: String, nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    val s = Repo.salud[fecha]; val p = Repo.pantalla[fecha]; val e = Repo.entorno[fecha]; val ag = Repo.agenda[fecha]
    val km by produceState<Double?>(null, fecha) { value = withContext(Dispatchers.IO) { runCatching { Movilidad.kmDe(fecha) }.getOrNull() } }
    val musica by produceState(emptyList<io.github.ruddy2003.vestigium.data.Escucha>(), fecha, Repo.notifs[fecha]) {
        value = withContext(Dispatchers.IO) { runCatching { Repo.escuchas(fecha, fecha) }.getOrDefault(emptyList()) }
    }
    val mundo by produceState<io.github.ruddy2003.vestigium.data.Mundo.DiaMundo?>(null, fecha, io.github.ruddy2003.vestigium.data.Mundo.version) {
        value = withContext(Dispatchers.IO) { runCatching { io.github.ruddy2003.vestigium.data.Mundo.delDia(ctx, fecha) }.getOrNull() }
    }

    // Las fichas del reporte: lo principal del día con su cifra, su comparación y una gráfica pequeña.
    val artista = musica.groupBy { it.artista.ifBlank { "?" } }.maxByOrNull { it.value.size }?.key
    val fichas = fichasDelDia(fecha, km, musica.size, artista)
    if (fichas.size > 1) { RejillaFichas(fichas); Spacer(Modifier.height(8.dp)) }
    Tarjeta {
        // La luna siempre sale; «algo» cuenta lo que vino de los sensores.
        var algo = fichas.size > 1
        if (s != null) {
            if (s.spo2_min != null) { algo = true; ClaveDia("oxygenium", "mínima de noche %.0f%%".format(s.spo2_min)) }
        }
        if (s?.pasos == null) km?.takeIf { it >= 0.2 }?.let { algo = true; ClaveDia("iter", "%.1f km recorridos".format(it)) }
        p?.let {
            if (it.primer_uso != null) { algo = true; ClaveDia("usus", "primer uso ${Fechas.horaDe(it.primer_uso)} · último ${it.ultimo_uso?.let { u -> Fechas.horaDe(u) } ?: "?"}") }
        }
        ag?.let { if (it.eventos + it.todo_el_dia > 0) { algo = true; ClaveDia("fasti", "${it.eventos} eventos · ${hm(it.min_ocupado)} ocupado" + if (it.titulos.isNotEmpty()) " · " + it.titulos.take(3).joinToString(", ") else "") } }
        e?.let {
            if (it.amanecer != null) ClaveDia("lux", "${it.amanecer} → ${it.atardecer ?: "?"}" + (it.sol_min?.let { x -> " · ${hm(x)} de sol" } ?: ""))
            if (it.aqi_us != null) ClaveDia("aer", "calidad del aire ${it.aqi_us} (${aqiTexto(it.aqi_us)})" + (it.pm25?.let { x -> " · pm2.5 %.0f".format(x) } ?: ""))
        }
        if (s != null) {
            val cuerpo = listOfNotNull(s.peso_kg?.let { "%.1f kg".format(it) }, s.temp_piel_delta?.let { "piel %+.2f °C".format(it) }, s.resp_rpm?.let { "resp %.0f rpm".format(it) },
                s.pa_sistolica?.let { "pa %.0f/%.0f".format(it, s.pa_diastolica ?: 0.0) }, s.cal_activas?.let { "%.0f kcal activas".format(it) }, s.pisos?.let { "%.0f pisos".format(it) },
                s.agua_ml?.let { "%.0f ml de agua".format(it) }, s.vo2max?.let { "vo₂max %.0f".format(it) })
            if (cuerpo.isNotEmpty()) { algo = true; ClaveDia("corpus", cuerpo.joinToString(" · ")) }
        }
        Repo.nutricion[fecha]?.let { nu ->
            val v = nu.valores
            val extra = listOfNotNull(v["gasto_kcal"]?.let { g -> v["kcal"]?.let { k -> "balance %+.0f".format(k - g) } }, v["fibra_g"]?.let { "fibra %.0f g".format(it) },
                v["cafeina_mg"]?.takeIf { it > 0 }?.let { "cafeína %.0f mg".format(it) + (nu.cafeina_ultima?.let { h -> " (última $h)" } ?: "") },
                v["alcohol_g"]?.takeIf { it > 0 }?.let { "alcohol %.0f g".format(it) }, if (nu.primera != null) "comiste de ${nu.primera} a ${nu.ultima}" else null)
            if (extra.isNotEmpty()) { algo = true; ClaveDia("cibus", extra.joinToString(" · ")) }
        }
        Repo.comunicacion[fecha]?.let { co ->
            if (co.sms_enviados + co.sms_recibidos > 0 || co.contactos > 0) { algo = true; ClaveDia("voces", (if (co.sms_enviados + co.sms_recibidos > 0) "${co.sms_enviados + co.sms_recibidos} sms · " else "") + "${porNumero(co.contactos, "1 persona", "${co.contactos} personas")}") }
        }
        // Los filtros de colecciones enteras se recuerdan por fecha y lista: no se repiten con cada tecla del diario.
        val wa = remember(fecha, Repo.chats) { Repo.chats.filter { it.fecha == fecha } }
        if (wa.isNotEmpty()) {
            algo = true
            ClaveDia("whatsapp", "${wa.sumOf { it.mios }} enviados · ${wa.sumOf { it.otros }} recibidos · ${porNumero(wa.size, "1 chat", "${wa.size} chats")}")
        }
        Repo.conexiones[fecha]?.let { cx ->
            algo = true
            val aud = listOfNotNull(cx.audio_min["audifonos"]?.let { "audífonos ${hm(it)}" }, cx.audio_min["coche"]?.let { "coche ${hm(it)}" },
                cx.audio_min["bocina"]?.let { "bocina ${hm(it)}" }, cx.musica_min.takeIf { it > 0 }?.let { "sonando ${hm(it)}" })
            if (aud.isNotEmpty()) ClaveDia("soni", aud.joinToString(" · "))
            cx.wifi_min.entries.sortedByDescending { it.value }.take(2).takeIf { it.isNotEmpty() }?.let { w -> ClaveDia("retia", w.joinToString(" · ") { "${it.key} ${hm(it.value)}" } + " · sin wifi ${hm(cx.sin_wifi_min)}") }
            val n = cx.luz_n.sum()
            if (n > 0) ClaveDia("lumen", "máx %.0f lux · %s con luz de exterior".format(cx.luz_max ?: 0.0, hm(cx.luz_alta_min)))
            if (cx.carga_noche_inicio != null) ClaveDia("vis", "conectado ${Fechas.horaDe(cx.carga_noche_inicio)} → ${cx.carga_noche_fin?.let { Fechas.horaDe(it) } ?: "?"}")
        }
        Repo.huellas[fecha]?.let { h -> algo = true; ClaveDia("google", "${h.yt_vistos} videos · ${h.busquedas + h.yt_busquedas} búsquedas" + (h.canales.keys.firstOrNull()?.let { " · más: $it" } ?: "")) }
        val sub = remember(fecha, Repo.subrayados) { Repo.subrayados.filter { it.fecha == fecha } }
        if (sub.isNotEmpty()) { algo = true; ClaveDia("lectio", "${porNumero(sub.size, "1 subrayado", "${sub.size} subrayados")} · " + sub.map { it.libro }.distinct().take(2).joinToString(", ")) }
        val ruidos = remember(fecha, Repo.momentos) { Repo.momentos.filter { it.fecha == fecha && !it.privado }.mapNotNull { it.ruido_db } }
        if (ruidos.isNotEmpty()) ClaveDia("strepitus", "%.0f dB de media al registrar".format(ruidos.average()))
        // Lo que leíste, viste o jugaste; si lo terminaste ese día, tu calificación y lo que opinaste.
        val obras = remember(fecha, Repo.obras) { Repo.obras.filter { it.titulo.isNotBlank() && (fecha in it.dias || (it.estado == "terminado" && it.fin == fecha)) } }
        if (obras.isNotEmpty()) {
            algo = true
            ClaveDia("opera", obras.joinToString(" · ") { o ->
                o.titulo + " (" + (TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre?.lowercase() ?: o.tipo) + ")" +
                    (if (o.estado == "terminado" && o.fin == fecha) " terminado" + (o.calificacion?.let { " · $it/10" } ?: "") else "")
            })
            obras.filter { it.estado == "terminado" && it.fin == fecha && it.nota.isNotBlank() }.forEach { o ->
                ClaveDia("  iudicium", "«" + o.nota.trim().replace(Regex("\\s+"), " ").let { if (it.length > 160) it.take(160) + "…" else it } + "»")
            }
        }
        val citasDia = Repo.obras.flatMap { o -> o.citas.filter { it.fecha == fecha }.map { o to it } }
        if (citasDia.isNotEmpty()) { algo = true; ClaveDia("citatio", "«${citasDia.first().second.texto.take(110)}» — ${citasDia.first().first.titulo}" + if (citasDia.size > 1) " (+${citasDia.size - 1})" else "") }
        Repo.config.frases_guardadas.filter { it.fecha == fecha && it.tema != "kindle" }.forEach { f -> algo = true; ClaveDia("sententia", "♥ «${f.texto.take(110)}» — ${f.autor}") }
        // Dónde estuviste: ciudades, lo que fue primera vez y el Patrimonio Mundial.
        mundo?.let { m ->
            val c = m.ciudades.filter { it.second >= 0.25 || it.second == 0.0 }
            if (c.isNotEmpty()) { algo = true; ClaveDia("orbis", c.take(4).joinToString(" · ") { (e, h) -> "${io.github.ruddy2003.vestigium.data.Mundo.bandera(e.pais)} ${e.nombre}" + if (h > 0) " ${hm((h * 60).toInt())}" else "" }) }
            val nuevas = (m.paisesNuevos.map { it.nombre } + m.ciudadesNuevas.map { it.nombre }).distinct()
            if (nuevas.isNotEmpty()) ClaveDia("  nova", "primera vez en " + nuevas.joinToString(", "))
            if (m.sitios.isNotEmpty()) { algo = true; ClaveDia("mirabilia", m.sitios.joinToString(" · ") { s -> s.n + " (Patrimonio Mundial, " + s.categoria.lowercase() + (if (s.i in m.sitiosNuevos) ", primera vez" else "") + ")" }) }
        }
        // El sueño de esa noche y las cartas.
        Repo.dias[fecha]?.takeIf { it.sueno_texto.isNotBlank() }?.let { d ->
            algo = true
            ClaveDia("somnium", (io.github.ruddy2003.vestigium.ui.LibroDiario.metaSueno(d).ifBlank { "soñaste" }) + " · «" + d.sueno_texto.trim().replace(Regex("\\s+"), " ").take(90) + "…»")
        }
        val cartas = remember(fecha, Repo.cartas) { Repo.cartas.filter { it.escrita == fecha } }
        val cartasAbiertas = remember(fecha, Repo.cartas) { Repo.cartas.filter { it.para.isBlank() && it.abierta_el == fecha } }
        if (cartas.isNotEmpty()) { algo = true; ClaveDia("epistulae", cartas.joinToString(" · ") { if (it.para.isBlank()) "sellaste una carta hasta el ${Fechas.corto(it.abrir_en)} ${it.abrir_en.take(4)}" else "carta a @${it.para}" }) }
        cartasAbiertas.forEach { ClaveDia("  aperta", "abriste la carta que te escribiste el ${Fechas.larga(it.escrita)}") }
        if (!algo) Ayuda("Aún no hay datos automáticos de este día. Activa fuentes en Más → Permisos y sensores.", Modifier.padding(top = 8.dp))
        Row(Modifier.padding(top = 10.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            BotonSecundario(if (ocupado) "sincronizando…" else "↻ sincronizar", habilitado = !ocupado) {
                scope.launch { ocupado = true; msg = Sincronizar.todo(ctx); ocupado = false }
            }
            BotonSecundario("sensores →") { nav.ir("permisos") }
        }
        msg?.let { Ayuda(it, Modifier.padding(top = 6.dp)) }
    }
}

private fun aqiTexto(a: Int) = when { a <= 50 -> "buena"; a <= 100 -> "aceptable"; a <= 150 -> "mala para sensibles"; a <= 200 -> "mala"; else -> "muy mala" }

@Composable
private fun Imagenes(fecha: String, dia: io.github.ruddy2003.vestigium.data.Dia, nav: Nav, onElegir: (String) -> Unit) {
    val ctx = LocalContext.current
    val elegida = dia.foto
    // La elegida primero, aunque ya no esté en la galería (se ve su copia guardada).
    if (elegida != null || dia.foto_archivo != null) {
        Box(Modifier.fillMaxWidth().aspectRatio(1.6f).clip(RoundedCornerShape(4.dp)).border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))) {
            MiniaturaDe(dia, 720, Modifier.fillMaxWidth().aspectRatio(1.6f))
            Text(" IMAGO DIEI ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(MaterialTheme.colorScheme.primary))
        }
        if (dia.foto_archivo == null && elegida != null) Ayuda("Guardando una copia dentro de la app…", Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(8.dp))
    }
    val permiso = Galeria.tienePermiso(ctx) || Galeria.parcial(ctx)
    if (!permiso) {
        Vacio("Da acceso a tus fotos en Permisos y sensores para ver aquí las del día.")
        BotonSecundario("dar acceso →", Modifier.padding(top = 8.dp)) { nav.ir("permisos") }
        return
    }
    val fotos by produceState(emptyList<Galeria.Foto>(), fecha) { value = withContext(Dispatchers.IO) { Galeria.delDia(ctx, fecha) } }
    if (fotos.isEmpty()) { Vacio("No hay fotos de este día en tu galería."); return }
    val orden = fotos.sortedWith(compareBy({ it.uri.toString() != elegida }, { it.tipo != "camara" }, { it.ms }))
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(orden, key = { it.id }) { f ->
            val sel = f.uri.toString() == elegida
            // Elegir también con un botón visible, no solo manteniendo presionado (N-24).
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size(96.dp).clip(RoundedCornerShape(3.dp))
                .border(if (sel) 2.dp else 1.dp, if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline, RoundedCornerShape(3.dp))
                .combinedClickable(
                    onClick = { runCatching { ctx.startActivity(Intent(Intent.ACTION_VIEW).setDataAndType(f.uri, "image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)) } },
                    onLongClick = { onElegir(f.uri.toString()) },
                )) {
                Miniatura(f.uri, 240, Modifier.size(96.dp))
                Text(Fechas.horaDe(Fechas.isoDeMillis(f.ms)), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.BottomEnd).background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)).padding(horizontal = 3.dp))
                if (f.tipo == "captura") Text("cap", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.align(Alignment.TopStart).background(MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f)).padding(horizontal = 3.dp))
                if (sel) Text("★", color = MaterialTheme.colorScheme.primary, modifier = Modifier.align(Alignment.TopEnd).padding(3.dp))
            }
            BotonTexto(onClick = { onElegir(f.uri.toString()) }, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)) {
                Text(if (sel) "quitar" else "elegir", style = MaterialTheme.typography.labelMedium)
            }
            }
        }
    }
    val camara = fotos.count { it.tipo == "camara" }
    Ayuda("$camara de cámara · ${fotos.count { it.tipo == "captura" }} capturas · ${fotos.count { it.tipo == "otra" }} de otras apps", Modifier.padding(top = 6.dp))
}

/** La foto del día de [d]: su copia guardada en la app o, si no hay, la de la galería (esquema 22). */
@Composable
fun MiniaturaDe(d: io.github.ruddy2003.vestigium.data.Dia?, px: Int, modifier: Modifier) {
    val ctx = LocalContext.current
    val bmp by produceState<Bitmap?>(null, d?.fecha, d?.foto, d?.foto_archivo, px) { value = withContext(Dispatchers.IO) { io.github.ruddy2003.vestigium.data.FotoDelDia.miniatura(ctx, d, px) } }
    val b = bmp
    if (b != null) Image(b.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
    else Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant))
}

@Composable
fun Miniatura(uri: Uri, px: Int, modifier: Modifier) {
    val ctx = LocalContext.current
    val bmp by produceState<Bitmap?>(null, uri, px) { value = withContext(Dispatchers.IO) { Galeria.miniatura(ctx, uri, px) } }
    val b = bmp
    if (b != null) Image(b.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop, modifier = modifier)
    else Box(modifier.background(MaterialTheme.colorScheme.surfaceVariant))
}

/** Muestra las etiquetas detectadas y marca las nuevas; sugiere corrección si se parece a una conocida. */
@Composable
fun Detectados(texto: String, fecha: String, onCorregir: (String) -> Unit) {
    val eq = Repo.equivalencias()
    val conocidas = remember(Repo.dias.size, fecha) {
        val p = HashSet<String>(); val t = HashSet<String>()
        Repo.dias.forEach { (f, dd) -> if (f != fecha) { Texto.personas(dd.nota).forEach { p.add(Texto.canon(it, eq)) }; Texto.temas(dd.nota).forEach { t.add(Texto.canon(it, eq)) } } }
        Repo.momentos.filter { !it.privado }.forEach { m -> Texto.personas(m.texto).forEach { p.add(Texto.canon(it, eq)) }; Texto.temas(m.texto).forEach { t.add(Texto.canon(it, eq)) } }
        p to t
    }
    val P = Texto.personas(texto).distinct(); val T = Texto.temas(texto).distinct()
    if (P.isEmpty() && T.isEmpty()) return
    val hayHistoria = conocidas.first.size + conocidas.second.size > 0
    FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        P.forEach { Tok("@$it", hayHistoria && Texto.canon(it, eq) !in conocidas.first) }
        T.forEach { Tok("#$it", hayHistoria && Texto.canon(it, eq) !in conocidas.second) }
    }
    val sugerencias = P.filter { Texto.canon(it, eq) !in conocidas.first }.mapNotNull { x -> conocidas.first.firstOrNull { Texto.parecidos(x, it) }?.let { Triple('@', x, it) } } +
        T.filter { Texto.canon(it, eq) !in conocidas.second }.mapNotNull { x -> conocidas.second.firstOrNull { Texto.parecidos(x, it) }?.let { Triple('#', x, it) } }
    sugerencias.forEach { (s, a, b) ->
        Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Row(Modifier.padding(start = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("$s$a es nueva. ¿Quisiste decir $s$b?", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                BotonTexto(onClick = { onCorregir(Texto.reemplazar(texto, s, a, b)) }) { Text("corregir", style = MaterialTheme.typography.labelLarge) }
            }
        }
    }
    if (hayHistoria && (P.any { Texto.canon(it, eq) !in conocidas.first } || T.any { Texto.canon(it, eq) !in conocidas.second }))
        Ayuda("Resaltadas: etiquetas que nunca habías usado. Si es una persona o tema nuevo, está bien.", Modifier.padding(top = 6.dp))
}

@Composable
private fun Tok(t: String, nueva: Boolean) {
    Surface(shape = RoundedCornerShape(3.dp), color = if (nueva) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant) {
        Text(t, style = MaterialTheme.typography.labelLarge.copy(fontSize = 12.sp), modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            color = if (nueva) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Start)
    }
}


/**
 * «Tu día» (N-17, N-26): la línea de 24 horas y, debajo, todo lo que tiene hora ese día (momentos, consumos, tramos,
 * gastos, ingresos, tomas, notas e ideas), del más temprano al más tarde. Cada fila se corrige al tocarla; «＋ agregar a este día»
 * abre el registro con la fecha ya puesta. Lo privado no se lista: se llega por Privado.
 */
@Composable
private fun TuDia(fecha: String, nav: Nav, numero: Int, guardarAntes: () -> Unit, abrir: (Any) -> Unit) {
    val ctx = LocalContext.current
    val c = Repo.config
    class Fila(val ms: Long, val hora: String, val texto: String, val tocar: () -> Unit)
    fun msDe(f: String, h: String) = runCatching { java.time.LocalDateTime.of(Fechas.parse(f), java.time.LocalTime.parse(h.take(5))).aMillis() }.getOrDefault(0L)
    fun cifra(x: Double) = if (x % 1.0 == 0.0) x.toLong().toString() else "%.1f".format(x)
    fun nota(t: String) = if (t.isNotBlank()) " · $t" else ""
    // Lo del día de cada colección, recordado por fecha y lista: antes los nueve filtros (y la fecha de todas las visitas de
    // la historia) se repetían con cada cambio del día, es decir, con cada tecla.
    val momentosDia = remember(fecha, Repo.momentos) { Repo.momentos.filter { it.fecha == fecha && !it.privado } }
    val consumosDia = remember(fecha, Repo.consumos, c) { Repo.consumos.filter { it.fecha == fecha && io.github.ruddy2003.vestigium.data.Sustancias.visible(it) } }
    val tramosDia = remember(fecha, Repo.tramos) { Repo.tramos.filter { it.fecha == fecha } }
    val gastosDia = remember(fecha, Repo.gastos) { Repo.gastos.filter { it.dia == fecha } }
    val ingresosDia = remember(fecha, Repo.ingresos) { Repo.ingresos.filter { it.dia == fecha } }
    val tomasDia = remember(fecha, Repo.tomas) { Repo.tomas.filter { it.fecha == fecha } }
    val visitasDia = remember(fecha, Repo.visitas) { Repo.visitas.filter { Fechas.diaLogicoDeMillis(Fechas.millis(it.entrada)) == fecha } }
    val notasDia = remember(fecha, Repo.notas) { Repo.notas.filter { it.fecha == fecha } }
    val ideasDia = remember(fecha, Repo.ideas) { Repo.ideas.filter { it.fecha == fecha } }
    val filas = buildList {
        momentosDia.forEach { m ->
            add(Fila(Fechas.millis(m.ts), Fechas.horaDe(m.ts), Catalogo.tituloDetallado(m, c.discreto) + (m.valencia?.let { " · $it/7" } ?: "") +
                Catalogo.detalleMomento(m, c.discreto).let { if (it.isNotBlank()) " · $it" else "" } + nota(m.texto)) { abrir(m) })
        }
        consumosDia.forEach { x ->
            add(Fila(Fechas.millis(x.inicio), Fechas.horaDe(x.inicio), Catalogo.nombreSustancia(x) + nota(resumenConsumo(x))) { abrirConsumo(ctx, id = x.id) })
        }
        tramosDia.forEach { t ->
            add(Fila(Fechas.millis(t.inicio), Fechas.horaDe(t.inicio), Catalogo.nombreTramo(t.tipo) + " · " + horasMin(t.min) + nota(t.nota)) { abrir(t) })
        }
        gastosDia.forEach { g ->
            add(Fila(msDe(g.fecha, g.hora), g.hora.take(5), "Gasto · $${"%,.2f".format(g.monto)} · ${g.categoria}" + nota(g.nota)) { abrir(g) })
        }
        ingresosDia.forEach { i ->
            add(Fila(msDe(i.fecha, i.hora), i.hora.take(5), "Ingreso · $${"%,.2f".format(i.monto)} · ${i.categoria}" + nota(i.nota)) { abrir(i) })
        }
        tomasDia.forEach { t ->
            val s = io.github.ruddy2003.vestigium.data.Suplementos.de(t.suplemento)
            add(Fila(Fechas.millis(t.ts), Fechas.horaDe(t.ts), "${s?.nombre ?: t.suplemento} · ${cifra(t.cantidad)} ${s?.unidad ?: ""}".trim()) { abrir(t) })
        }
        // Las visitas a tus lugares, por día lógico: tocar una dice «fue en…» o «no estuve aquí» (N-05).
        visitasDia.forEach { v ->
            val l = Repo.lugares.firstOrNull { it.id == v.lugar }
            add(Fila(Fechas.millis(v.entrada), Fechas.horaDe(v.entrada), "En ${l?.nombre ?: "un lugar quitado"}" + (v.salida?.let { " hasta las ${Fechas.horaDe(it)}" } ?: "")) { abrir(v) })
        }
        // Las notas van a la hora en que se escribieron si fue ese mismo día; si no, al final.
        notasDia.forEach { n ->
            val ms = Fechas.millis(n.creada)
            val mismoDia = ms > 0 && Fechas.diaLogicoDeMillis(ms) == fecha
            add(Fila(if (mismoDia) ms else Long.MAX_VALUE, if (mismoDia) Fechas.horaDe(n.creada) else "nota",
                (Catalogo.TIPOS_NOTA.firstOrNull { it.id == n.tipo }?.nombre ?: "Nota") + " · " + n.texto.lines().first().take(90)) { abrir(n) })
        }
        // Las ideas del Ideario, igual que las notas.
        ideasDia.forEach { i ->
            val ms = Fechas.millis(i.creada)
            val mismoDia = ms > 0 && Fechas.diaLogicoDeMillis(ms) == fecha
            add(Fila(if (mismoDia) ms else Long.MAX_VALUE, if (mismoDia) Fechas.horaDe(i.creada) else "idea",
                "Idea · " + i.titulo.ifBlank { i.texto.lines().first() }.take(90)) { guardarAntes(); EdicionIdea.abrir(nav, i) })
        }
    }.sortedBy { it.ms }
    var agregar by remember(fecha) { mutableStateOf(false) }
    Seccion("Tu día", "La línea de 24 horas y todo lo que anotaste o registró el teléfono en tus lugares, por hora. Toca una fila para corregirla.", numero = numero, plegable = true,
        resumen = if (filas.isEmpty()) "Nada anotado con hora" else "${filas.size} ${if (filas.size == 1) "registro" else "registros"} con hora") {
        LineaDelTiempo(fecha)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            BotonTexto(onClick = { Seleccion.cinta = fecha; nav.ir("cinta") }) { Text("la cinta completa →", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
            if (momentosDia.isNotEmpty()) BotonTexto(onClick = { Seleccion.momentos = fecha; nav.ir("hoja_momentos") }) {
                Text("la hoja de la libreta →", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
        if (filas.isNotEmpty()) Tarjeta(Modifier.padding(top = 4.dp)) {
            filas.forEach { f ->
                // Cada fila mide al menos 48 dp de alto, lo que pide Android para acertar el toque (hallazgo 12).
                Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable(onClick = f.tocar).padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(f.hora + "  ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    io.github.ruddy2003.vestigium.ui.TextoMarcado(f.texto, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text(" ›", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
        BotonTexto(onClick = { agregar = !agregar }) {
            Text(if (agregar) "＋ agregar a este día ▾" else "＋ agregar a este día…", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        if (agregar) Tarjeta {
            Ayuda("Con la fecha ya puesta: ${Fechas.titulo(fecha).lowercase()}. Lo que anotes después de ese día queda marcado como anotado tarde.")
            FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Ficha("registro de ánimo", false) {
                    guardarAntes()
                    ctx.startActivity(android.content.Intent(ctx, io.github.ruddy2003.vestigium.ui.QuickActivity::class.java)
                        .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.MODO, io.github.ruddy2003.vestigium.ui.QuickActivity.M_ANIMO)
                        .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.ORIGEN, "dia")
                        .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.FECHA, fecha))
                }
                Ficha("gasto", false) {
                    // Hoy, a la hora de ahora; otro día, a mediodía (se cambia en el mismo diálogo).
                    val hora = if (fecha == Fechas.diaLogico()) Fechas.horaAhora() else "12:00"
                    val f = if (fecha == Fechas.diaLogico()) Fechas.hoyCalendario() else fecha
                    abrir(io.github.ruddy2003.vestigium.data.Gasto(Repo.nuevoId(), f, hora, 0.0, c.categorias.firstOrNull() ?: "Otros"))
                }
                Ficha("episodio", false) {
                    guardarAntes()
                    ctx.startActivity(android.content.Intent(ctx, io.github.ruddy2003.vestigium.ui.QuickActivity::class.java)
                        .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.MODO, io.github.ruddy2003.vestigium.ui.QuickActivity.M_MOMENTO)
                        .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.TIPO, "episodio")
                        .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.ORIGEN, "dia")
                        .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.FECHA, fecha))
                }
                Ficha("nota", false) { guardarAntes(); Seleccion.notaPara = fecha; nav.ir("notas") }
                Ficha("idea", false) { guardarAntes(); EdicionIdea.abrir(nav, EdicionIdea.nueva(fecha)) }
            }
            if (io.github.ruddy2003.vestigium.data.Suplementos.activos(c).isNotEmpty()) {
                Etiqueta("Suplementos de ese día")
                Ayuda("Toca + por cada toma; − quita la última.")
                SuplementosDelDia(fecha)
            }
        }
    }
}

/**
 * Los suplementos activos de un día, uno por fila: «Creatina · ×2» con «−» y «+». «+» anota una toma más (hoy, a la hora de
 * ahora; otro día, a la hora del recordatorio que toca a esa toma, o a las 9); «−» quita la última, con Deshacer (esquema 21).
 */
@Composable
fun SuplementosDelDia(fecha: String) {
    val c = Repo.config
    val sup = io.github.ruddy2003.vestigium.data.Suplementos
    val horas = sup.horas(c)
    Column(Modifier.fillMaxWidth()) {
        sup.activos(c).forEach { s ->
            val n = sup.tomasDe(s.id, fecha).size
            val dosis = if (s.dosis % 1.0 == 0.0) s.dosis.toLong().toString() else s.dosis.toString()
            Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("${s.nombre} · ×$n" + (if (s.tomas_dia > 1) " de ${s.tomas_dia}" else ""), style = MaterialTheme.typography.bodyLarge,
                        color = if (n > 0) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$dosis ${s.unidad}".trim() + " por toma", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                BotonTexto(onClick = {
                    val t = sup.quitarUltima(s.id, fecha)
                    if (t != null) io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Toma quitada") { Repo.guardarToma(t) }
                }, enabled = n > 0, modifier = Modifier.semantics { contentDescription = "Quitar la última toma de ${s.nombre}" }) {
                    Text("−", style = MaterialTheme.typography.titleLarge)
                }
                BotonTexto(onClick = {
                    // Hoy, con la hora de ahora; otro día, a la hora del recordatorio de esa toma (la primera si hay menos), o a las 9.
                    val ts = if (fecha == Fechas.diaLogico()) Fechas.ahoraIso()
                        else Fechas.isoDeMillis(Fechas.millisDe(Fechas.parse(fecha).atTime((horas.getOrNull(n) ?: horas.firstOrNull() ?: 9).coerceIn(5, 23), 0)))
                    sup.registrar(s, s.dosis, ts)
                }, modifier = Modifier.semantics { contentDescription = "Anotar una toma de ${s.nombre}" }) {
                    Text("+", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

/**
 * Los episodios del día (esquema 21): algo fuerte que te pasó, con hora (una crisis, un llanto, una pelea, una migraña). Se
 * anotan como un momento «episodio» desde el ⊕; aquí se ven, se corrigen al tocarlos y «+ un episodio» abre el registro con
 * la fecha ya puesta. Son datos normales, no privados.
 */
@Composable
private fun EpisodiosDelDia(fecha: String, episodios: List<io.github.ruddy2003.vestigium.data.Momento>, guardarAntes: () -> Unit) {
    val ctx = LocalContext.current
    var editando by remember(fecha) { mutableStateOf<io.github.ruddy2003.vestigium.data.Momento?>(null) }
    editando?.let { m -> DialogoMomento(m) { editando = null } }
    if (episodios.isNotEmpty()) Tarjeta {
        episodios.forEach { m ->
            Row(Modifier.fillMaxWidth().clickable { editando = m }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(Fechas.horaDe(m.ts) + "  ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                val fuerza = m.intensidad?.let { i -> " · " + (Catalogo.ANCLAS["episodio"]?.get(i)?.lowercase() ?: "$i/5") }.orEmpty()
                Text(Catalogo.nombreEpisodio(m) + fuerza, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                Text(" ›", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
    BotonTexto(onClick = {
        guardarAntes()
        ctx.startActivity(android.content.Intent(ctx, io.github.ruddy2003.vestigium.ui.QuickActivity::class.java)
            .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.MODO, io.github.ruddy2003.vestigium.ui.QuickActivity.M_MOMENTO)
            .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.TIPO, "episodio")
            .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.ORIGEN, "dia")
            .putExtra(io.github.ruddy2003.vestigium.ui.QuickActivity.FECHA, fecha))
    }) { Text("+ un episodio", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
}

/**
 * Las fechas señaladas del día (esquema 21): una ficha por tipo (emblema y nombre); al tocarla se abre «Una frase (opcional)»
 * y «Agregar». Tocar una de la lista deja cambiar su tipo o su frase, o borrarla. Al guardar se escriben en [Dia.senaladas] con
 * los hitos de antes ya incluidos (por [Senaladas.de]) y [Dia.hitos] vacío: así los viejos pasan solos.
 */
@Composable
private fun FechasSenaladas(d: Dia, onCambio: (Dia) -> Unit) {
    val lista = Senaladas.de(d)
    // Lo que se está escribiendo: el tipo elegido y, si se corrige una de la lista, su lugar en ella.
    var tipo by remember(d.fecha) { mutableStateOf<String?>(null) }
    var indice by remember(d.fecha) { mutableStateOf<Int?>(null) }
    var frase by remember(d.fecha) { mutableStateOf("") }
    fun cerrar() { tipo = null; indice = null; frase = "" }
    fun guardar(l: List<Senalada>) = onCambio(d.copy(senaladas = l, hitos = emptyList()))
    if (lista.isNotEmpty()) Tarjeta(Modifier.padding(bottom = 8.dp)) {
        lista.forEachIndexed { i, s ->
            Row(Modifier.fillMaxWidth().clickable {
                if (indice == i) cerrar() else { indice = i; tipo = s.tipo; frase = s.texto }
            }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(Senaladas.etiqueta(s), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f),
                    color = if (indice == i) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface)
                Text(" ›", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.outline)
            }
        }
    }
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Catalogo.SENALADAS.forEach { o ->
            Ficha("${o.detalle} ${o.nombre}", tipo == o.id) {
                // En una nueva, tocar otra vez el mismo tipo la cierra; al corregir, cambia el tipo.
                if (tipo == o.id && indice == null) cerrar() else { tipo = o.id }
            }
        }
    }
    val t = tipo ?: return
    val i = indice
    Tarjeta(Modifier.padding(top = 8.dp)) {
        val o = Catalogo.senalada(t)
        Text((if (i == null) "" else "Corregir · ") + "${o.detalle} ${o.nombre}", style = MaterialTheme.typography.titleMedium)
        CampoTexto(value = frase, onValueChange = { frase = it.take(120) }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp), singleLine = true,
            placeholder = { Text("Una frase (opcional)", style = MaterialTheme.typography.bodyMedium) })
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
            io.github.ruddy2003.vestigium.ui.BotonPrincipal(if (i == null) "Agregar" else "Guardar", Modifier.weight(1f)) {
                val nueva = Senalada(t, frase.trim().replace(Regex("\\s+"), " "))
                guardar(if (i == null || i !in lista.indices) lista + nueva else lista.mapIndexed { k, s -> if (k == i) nueva else s })
                cerrar()
            }
            if (i != null) BotonTexto(onClick = { if (i in lista.indices) guardar(lista.filterIndexed { k, _ -> k != i }); cerrar() }) {
                Text("borrar", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.error)
            }
            BotonTexto(onClick = { cerrar() }) { Text("cancelar", style = MaterialTheme.typography.labelLarge) }
        }
    }
}

/** «¿Cómo terminó lo de anoche?»: los consumos con efecto que falta completar (duración, máximo, cruda). */
@Composable
private fun LoDeAnoche() {
    val ctx = LocalContext.current
    val l = io.github.ruddy2003.vestigium.data.Sustancias.porCompletar()
    if (l.isEmpty()) return
    Tarjeta(Modifier.padding(top = 10.dp)) {
        Text("¿Cómo terminó lo de anoche?", style = MaterialTheme.typography.titleMedium)
        l.forEach { x ->
            // Si esa sustancia está oculta de las pantallas, se pregunta sin nombrarla.
            val nombre = if (io.github.ruddy2003.vestigium.data.Sustancias.visible(x)) "${Catalogo.nombreSustancia(x)} · desde las ${Fechas.horaDe(x.inicio)}" else "Un registro del ${Fechas.corto(x.fecha)}"
            io.github.ruddy2003.vestigium.ui.FilaMenu(nombre, "hasta qué hora, el máximo y la cruda") { abrirConsumo(ctx, id = x.id) }
        }
    }
}

/**
 * El aviso del experimento en el Día, con «¿Lo hiciste hoy?» (I-3). Se pregunta en las dos condiciones: en las semanas «no»
 * mide si lo hiciste igual. Tocar la respuesta elegida la borra (queda sin respuesta, que no es «no»).
 */
@Composable
private fun AvisoExperimento(e: io.github.ruddy2003.vestigium.data.Experimento, fecha: String) {
    // Antes del inicio no hay semana (P-18): toca() devuelve null.
    val toca = Experimentos.toca(e, fecha) ?: return
    val semana = (Fechas.diasEntre(e.inicio, fecha) / 7).toInt()
    val lavado = Experimentos.enLavado(e, fecha)
    Aviso("Experimento «${e.nombre}», semana ${semana + 1} de ${e.semanas}: " + (if (toca) "esta semana SÍ: ${e.accion}." else "esta semana NO hagas: ${e.accion}.") +
        if (lavado) " Hoy es día de lavado: no cuenta para el resultado." else "")
    val h = e.hecho[fecha]
    Row(Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(if (fecha == Fechas.diaLogico()) "¿Lo hiciste hoy?" else "¿Lo hiciste ese día?", style = MaterialTheme.typography.labelLarge)
        fun responder(v: Boolean) {
            val actual = Repo.experimentos.firstOrNull { it.id == e.id } ?: e
            Repo.guardarExperimento(actual.copy(hecho = if (actual.hecho[fecha] == v) actual.hecho - fecha else actual.hecho + (fecha to v)))
        }
        Ficha("sí", h == true) { responder(true) }
        Ficha("no", h == false) { responder(false) }
    }
}
