@file:OptIn(ExperimentalLayoutApi::class, ExperimentalFoundationApi::class)

package io.github.ruddy2003.vestigium.ui.pantallas

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import io.github.ruddy2003.vestigium.ui.ListaEtiquetas
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
fun PantallaDia(nav: Nav, apilado: Boolean = false) {
    var fecha by remember { mutableStateOf(Seleccion.pedida ?: (if (apilado) null else Seleccion.diaAbierto) ?: Fechas.diaLogico()) }
    // La pestaña recuerda la fecha abierta mientras vas a la hoja de la noche y vuelves; un día apilado no la cambia.
    LaunchedEffect(fecha) { if (!apilado) Seleccion.diaAbierto = fecha }
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
    // Si se pidió una sección de la noche (avisos, atajos), se abre la hoja de la noche en ella.
    LaunchedEffect(Seleccion.seccion, fecha) {
        val sec = Seleccion.seccion ?: return@LaunchedEffect
        abrirNoche(sec)
    }
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
        val tema = est.tema
        fun abrirCalendario() { Seleccion.ultimoMes = Fechas.mesDe(fecha); if (sucio) Repo.guardarDia(d); nav.ir("calendario") }
        Row(Modifier.fillMaxWidth().padding(top = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            if (apilado) TextButton(onClick = nav.atras, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)) {
                Text("←", style = MaterialTheme.typography.titleLarge)
            }
            // «Hoy» cuando es hoy; «Día» en otra fecha (N-23). El nombre del tema y el español en la misma línea (P-1).
            val (nombreT, espanolT, _) = if (fecha == hoy) est.pantallaEnLinea("Hoy", "HODIE") else est.pantallaEnLinea("Día", "DIES")
            Text(androidx.compose.ui.text.buildAnnotatedString {
                if (espanolT != null) withStyle(androidx.compose.ui.text.SpanStyle(color = MaterialTheme.colorScheme.primary, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)) { append(nombreT) }
                else append(nombreT)
                if (tema == io.github.ruddy2003.vestigium.ui.TemaId.CIBER) append("_")
                if (espanolT != null) withStyle(androidx.compose.ui.text.SpanStyle(color = MaterialTheme.colorScheme.onSurfaceVariant)) { append(" · $espanolT") }
            }, style = MaterialTheme.typography.headlineMedium, modifier = Modifier.weight(1f))
            io.github.ruddy2003.vestigium.ui.LocalGuia.current?.let { guia -> TextButton(onClick = { guia("principios") }) { Text("?", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary) } }
            io.github.ruddy2003.vestigium.ui.BotonBuscar { nav.ir("buscar") }
            io.github.ruddy2003.vestigium.ui.BotonAjustes(Modifier.padding(start = 4.dp)) { nav.ir("mas") }
            io.github.ruddy2003.vestigium.ui.BotonModo()
            io.github.ruddy2003.vestigium.ui.Emblema(tam = 34.dp, modifier = Modifier.padding(start = 4.dp))
        }
        // La fecha grande abre el calendario.
        Text(est.fecha(fecha) + "  ▾", style = if (tema == io.github.ruddy2003.vestigium.ui.TemaId.ROMANO) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.clickable { abrirCalendario() })
        val fase = Luna.fase(fecha)
        Text(est.comentario + "${Fechas.titulo(fecha).lowercase()} · ${Fechas.relativo(fecha)} · ${Luna.glifo(fase)}",
            style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
            TextButton(onClick = { irA(Fechas.sumar(fecha, -1)) }, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)) { Text("‹", style = MaterialTheme.typography.titleLarge) }
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
            if (!esHoy) TextButton(onClick = { irA(hoy) }) { Text("hoy", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
            TextButton(onClick = { irA(Fechas.sumar(fecha, 1)) }, contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 4.dp)) { Text("›", style = MaterialTheme.typography.titleLarge) }
        }
        // Semana corta: solo cuatro cifras y tres botones.
        if (esHoy && c.semana_corta) { SemanaCorta(d) { abrirNoche() }; return@Hoja }
        // La frase, siempre arriba, justo debajo del estado.
        FraseDelDia(fecha)
        // En lugar del personaje: tus últimos días, de un vistazo (N-28). Tocar un día lo abre; hoy abre «Llenar el día».
        if (esHoy) FranjaDias(hoy, abrir = { f -> if (f == hoy) abrirNoche() else { if (sucio) { Repo.guardarDia(d); sucio = false }; Seleccion.abrir(nav, f) } }) { ponerte() }
        if (esHoy) SugerirSemanaCorta(hoy)

        val bloqueSueno: @Composable () -> Unit = {
        Seccion("Sueño", "Llénalo apenas despiertes. El resto, en la noche.", numero = n(), modifier = Modifier.bringIntoViewRequester(reqSueno), plegable = true) {
                Tarjeta {
                    Etiqueta("Calidad del sueño, como la sentiste")
                    Escala(d.sueno_calidad, 1, 5, Catalogo.ANCLAS["sueno_calidad"]) { cambiar(d.copy(sueno_calidad = it)) }
                    Interruptor("¿Recordaste un sueño?", d.sueno_recordado) { cambiar(d.copy(sueno_recordado = it)) }
                    AnimatedVisibility(d.sueno_recordado) {
                        Column {
                            Etiqueta("Qué tan agradable fue")
                            Escala(d.sueno_valencia, 1, 7, Catalogo.ANCLAS["sueno_valencia"]) { cambiar(d.copy(sueno_valencia = it)) }
                            Etiqueta("Lucidez: ¿sabías que soñabas?"); FilaFichas(Catalogo.SUENO_LUCIDEZ, d.sueno_lucidez) { cambiar(d.copy(sueno_lucidez = it)) }
                            Etiqueta("Vividez del recuerdo"); FilaFichas(Catalogo.SUENO_VIVIDEZ, d.sueno_vividez) { cambiar(d.copy(sueno_vividez = it)) }
                            Etiqueta("Tipo"); FilaFichas(Catalogo.SUENO_TIPO, d.sueno_tipo) { cambiar(d.copy(sueno_tipo = it)) }
                            Etiqueta("De qué fue")
                            io.github.ruddy2003.vestigium.ui.CategoriasSueno(d.sueno_categorias) { cambiar(d.copy(sueno_categorias = it)) }
                            Etiqueta("Personas y temas del sueño")
                            ListaEtiquetas(d.sueno_temas, "@persona o #tema", normalizar = { Texto.etiquetaConSimbolo(it) }) { cambiar(d.copy(sueno_temas = it)) }
                            Etiqueta("Narración")
                            Editor(d.sueno_texto, "somnium.txt", "Lo que recuerdes, aunque sean fragmentos.", minimo = 110, pie = "${Texto.palabras(d.sueno_texto)} verba") { cambiar(d.copy(sueno_texto = it)) }
                            Row(Modifier.padding(top = 6.dp)) { BotonDictado { t -> cambiar(d.copy(sueno_texto = (d.sueno_texto + " " + t).trim())) } }
                        }
                    }
                    val sr = Repo.salud[fecha]
                    sr?.sueno_min?.let { m -> Clave("horologium", "${m / 60} h ${m % 60} min dormido " + if (sr.sueno_manual) "(corregido a mano)" else "según tu reloj") }
                    TextButton(onClick = { editarSueno = true }) {
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
        if (fecha > hoy) Aviso("Este día todavía no llega. Puedes dejar notas, pero se marcará como escrito por adelantado.")

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
    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (listo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
        modifier = Modifier.fillMaxWidth().padding(top = 18.dp)) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(io.github.ruddy2003.vestigium.ui.Estilo.seccion("Llenar el día", Romano.latin("Llenar el día")).first, style = MaterialTheme.typography.titleLarge)
                Text(partes.joinToString("  ") { (t, ok) -> (if (ok) "✓ " else "○ ") + t }, style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
                if (listo) Text("día sellado", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
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
    val pasos = listOfNotNull(
        Triple("ánimo", d.valencia != null, reqAnimo), Triple("predicción", d.prediccion_manana != null, reqPred),
        if (habitosActivos.isNotEmpty()) Triple("hábitos", habitosActivos.all { d.habitos[it.id] == true }, reqHabitos) else null,
        Triple("diario", d.nota.isNotBlank(), reqDiario),
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
        // «Día difícil» se marca desde el aviso del diario; aquí se ve y se puede quitar si fue un toque equivocado.
        if (d.dia_dificil) Row(Modifier.fillMaxWidth().padding(top = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Marcaste este día como «día difícil».", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f))
            TextButton(onClick = { cambiar(d.copy(dia_dificil = false)) }) { Text("quitar", style = MaterialTheme.typography.labelLarge) }
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
                if (mu.isNotEmpty() && d.valencia != null) Clave("avisos", mu.joinToString("  ") { "${Fechas.horaDe(it.ts)}→${it.valencia}" })
            }
        }
        // 3. La predicción, junto al juicio del día y antes de escribir.
        if (ver("predicción")) Seccion("Predicción", "¿Cómo crees que estará tu ánimo mañana? La app medirá qué tan bien te conoces.", numero = n(), modifier = Modifier.bringIntoViewRequester(reqPred)) {
            Escala(d.prediccion_manana, 1, 7, Catalogo.ANCLAS["prediccion_manana"]) { cambiar(d.copy(prediccion_manana = it)) }
        }
        // 4 y 5. Hechos: contexto, hábitos y suplementos.
        if (ver("contexto")) Seccion("Contexto", "Activa todo lo que aplique. Pueden ser varios o ninguno.", numero = n()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Catalogo.CONTEXTOS.forEach { o ->
                    val activo = d.ctx[o.id] == true
                    Ficha(o.nombre, activo) { cambiar(d.copy(ctx = if (activo) d.ctx - o.id else d.ctx + (o.id to true))) }
                }
            }
        }
        Seccion("Hábitos", "Siempre aquí, para marcarlos al cerrar el día.", numero = n(), modifier = Modifier.bringIntoViewRequester(reqHabitos)) {
            val activos = io.github.ruddy2003.vestigium.data.Privado.habitosVisibles(c).filter { it.activo }
            if (activos.isEmpty()) {
                Tarjeta {
                    Ayuda("Aún no tienes hábitos.")
                    TextButton(onClick = { nav.ir("habitos") }) { Text("+ agregar hábitos", style = MaterialTheme.typography.labelLarge) }
                }
            } else Tarjeta {
                activos.forEach { h ->
                    val hecho = d.habitos[h.id] == true
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Checkbox(checked = hecho, onCheckedChange = { cambiar(d.copy(habitos = if (it) d.habitos + (h.id to true) else d.habitos - h.id)) },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary))
                        Text(h.nombre, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
                        Text(if (hecho) "= 1" else "= 0", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
        // Lo que tomaste ese día: sirve también para lo que olvidaste anotar (N-09, E-11). Tocar de nuevo lo quita.
        if (io.github.ruddy2003.vestigium.data.Suplementos.activos(c).isNotEmpty())
            Seccion("Suplementos de este día", "Toca lo que tomaste ese día; si ya está, tocarlo lo quita.", numero = n(), plegable = true) { SuplementosDelDia(fecha) }
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
            TextButton(onClick = { nav.ir("biblioteca_diario") }) { Text("abrir la biblioteca del diario →", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
        }

        // 7. Lo opcional, plegado y en orden fijo: lectura, lo mejor y lo peor, hitos, obras, foto y lo privado.
        Text("opcional", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 28.dp))
        Seccion("Lectura del día", "Tres preguntas de marcos científicos. Toca «cómo elegir» si dudas.", numero = n(), plegable = true, plegadaPorDefecto = true) {
            Tarjeta {
                Catalogo.CIENTIFICAS.forEachIndexed { i, p ->
                    var abierta by remember { mutableStateOf(false) }
                    if (i > 0) Spacer(Modifier.padding(top = 14.dp))
                    Text(p.pregunta, style = MaterialTheme.typography.titleMedium)
                    TextButton(onClick = { abierta = !abierta }, contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
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
        Seccion("Lo mejor y lo peor", "Una línea cada uno.", numero = n(), plegable = true, plegadaPorDefecto = true) {
            OutlinedTextField(value = d.mejor_momento, onValueChange = { cambiar(d.copy(mejor_momento = it)) }, modifier = Modifier.fillMaxWidth(),
                label = { Text("optimum", style = MaterialTheme.typography.labelMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
            Spacer(Modifier.padding(top = 8.dp))
            OutlinedTextField(value = d.peor_momento, onValueChange = { cambiar(d.copy(peor_momento = it)) }, modifier = Modifier.fillMaxWidth(),
                label = { Text("pessimum", style = MaterialTheme.typography.labelMedium) }, textStyle = MaterialTheme.typography.bodyLarge)
        }
        Seccion("Hitos", "Lo que no pasa todos los días: un vuelo, un beso, una decisión importante. Una etiqueta corta por cosa.", numero = n(), plegable = true, plegadaPorDefecto = true) {
            ListaEtiquetas(d.hitos, "vuelo, beso, decision_importante", normalizar = { Texto.slug(it) }) { cambiar(d.copy(hitos = it)) }
        }
        Seccion("Lo que leí o vi hoy", "Toca la portada de lo que le dedicaste tiempo hoy: libro, serie, película, juego o artículo.", numero = n(), plegable = true, plegadaPorDefecto = true) {
            ObrasDeHoy(fecha, nav)
        }
        if (ver("foto")) Seccion("Foto del día", "Mantén presionada una foto para elegirla como la foto del día: sale en el calendario, en la biblioteca del diario y en el widget. Tócala para verla.", numero = n(), plegable = true, plegadaPorDefecto = true) {
            Imagenes(fecha, d.foto, nav) { uri -> cambiar(d.copy(foto = if (d.foto == uri) null else uri)) }
        }
        // Medicamentos, impulsos, recaídas y libido se anotan y se ven solo en la pantalla Privado.
        TextButton(onClick = { if (sucio) { Repo.guardarDia(d); sucio = false }; Seleccion.privado = fecha; nav.ir("recaidas") }) {
            Text("privado de este día →", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        // Cerrar el día: guarda y, si está completo, lo sella con la huella.
        val faltan = listOfNotNull(if (d.valencia == null) "la valencia" else null, if (d.nota.isBlank()) "la nota del diario" else null)
        Spacer(Modifier.height(24.dp))
        io.github.ruddy2003.vestigium.ui.BotonPrincipal(if (completo) "Sellar el día" else "Guardar y volver", Modifier.fillMaxWidth().bringIntoViewRequester(reqSellar)) { sellar() }
        if (!completo) Ayuda("Para sellar el día con tu huella falta " + faltan.joinToString(" y ") + ".", Modifier.padding(top = 6.dp))
        Spacer(Modifier.height(72.dp))
    }
    // Barra fija abajo: cuánto llevas y un salto a lo siguiente que falta.
    BarraNoche(pasos.map { it.first to it.second }, completo, Modifier.align(Alignment.BottomCenter)) {
        val sig = pasos.firstOrNull { !it.second }
        scope.launch { (sig?.third ?: reqSellar).bringIntoView() }
    }
    }
}

@Composable
private fun BarraNoche(pasos: List<Pair<String, Boolean>>, completo: Boolean, modifier: Modifier, siguiente: () -> Unit) {
    val hechos = pasos.count { it.second }
    val cs = MaterialTheme.colorScheme
    Surface(modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp), shape = MaterialTheme.shapes.medium, color = cs.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, cs.primary.copy(alpha = 0.6f)), shadowElevation = 6.dp) {
        Row(Modifier.padding(start = 14.dp, end = 6.dp, top = 6.dp, bottom = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    pasos.forEach { (_, ok) -> Box(Modifier.weight(1f).height(5.dp).clip(RoundedCornerShape(3.dp)).background(if (ok) cs.primary else cs.outline)) }
                }
                Text("$hechos de ${pasos.size} · " + (pasos.firstOrNull { !it.second }?.let { "falta ${it.first}" } ?: "todo listo"),
                    style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp))
            }
            TextButton(onClick = siguiente) {
                Text(if (hechos < pasos.size) "siguiente →" else if (completo) "sellar ↓" else "guardar ↓", style = MaterialTheme.typography.labelLarge, color = cs.primary)
            }
        }
    }
}

private fun hm(min: Int): String = if (min >= 60) "${min / 60} h ${"%02d".format(min % 60)} min" else "$min min"

/** Todo lo automático del día, en formato clave = valor. */
@Composable
private fun Vestigios(fecha: String, nav: Nav) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    var msg by remember { mutableStateOf<String?>(null) }
    var ocupado by remember { mutableStateOf(false) }
    val s = Repo.salud[fecha]; val p = Repo.pantalla[fecha]; val e = Repo.entorno[fecha]; val ft = Repo.fotos[fecha]
    val ag = Repo.agenda[fecha]; val nt = Repo.notifs[fecha]; val ac = Repo.actividad[fecha]
    val ses = Repo.sesiones.filter { it.fecha == fecha }
    val visitas = Repo.visitas.filter { it.entrada.take(10) == fecha }
    val km by produceState<Double?>(null, fecha) { value = withContext(Dispatchers.IO) { runCatching { Movilidad.kmDe(fecha) }.getOrNull() } }
    val musica by produceState(emptyList<io.github.ruddy2003.vestigium.data.Escucha>(), fecha, Repo.notifs[fecha]) {
        value = withContext(Dispatchers.IO) { runCatching { Repo.escuchas(fecha, fecha) }.getOrDefault(emptyList()) }
    }
    val fase = Luna.fase(fecha)
    val mundo by produceState<io.github.ruddy2003.vestigium.data.Mundo.DiaMundo?>(null, fecha, io.github.ruddy2003.vestigium.data.Mundo.version) {
        value = withContext(Dispatchers.IO) { runCatching { io.github.ruddy2003.vestigium.data.Mundo.delDia(ctx, fecha) }.getOrNull() }
    }

    Tarjeta {
        var algo = false
        if (s != null) {
            val m = s.sueno_min
            if (m != null) {
                algo = true
                val ini = s.sueno_inicio; val fin = s.sueno_fin
                Clave("somnus", hm(m) + (if (ini != null) " · ${Fechas.horaDe(ini)}→${if (fin != null) Fechas.horaDe(fin) else "?"}" else "") + if (s.sueno_manual) " · a mano" else "")
                val prof = s.sueno_profundo_min
                if (prof != null) Clave("  stadia", "profundo ${hm(prof)} · rem ${hm(s.sueno_rem_min ?: 0)} · ligero ${hm(s.sueno_ligero_min ?: 0)}")
            }
            s.siesta_reloj_min?.let { algo = true; Clave("  siesta", "${hm(it)} según el reloj") }
            // Corazón: la media del día y su rango, en reposo, de noche y la variabilidad (lo que haya).
            val cor = listOfNotNull(s.fc_media?.let { "media %.0f lpm".format(it) + (if (s.fc_min != null && s.fc_max != null) " (%.0f–%.0f)".format(s.fc_min, s.fc_max) else "") },
                s.fc_reposo?.let { "reposo %.0f".format(it) }, s.fc_sueno?.let { "dormido %.0f".format(it) }, s.hrv_rmssd?.let { "hrv %.0f ms".format(it) })
            if (cor.isNotEmpty()) { algo = true; Clave("cor", cor.joinToString(" · ")) }
            if (s.spo2 != null) { algo = true; Clave("oxygenium", "spo₂ media %.1f%%".format(s.spo2) + (s.spo2_min?.let { " · mínima de noche %.0f%%".format(it) } ?: "")) }
            s.pasos?.let { algo = true; Clave("passus", "%,d pasos".format(it)) }
        }
        ac?.minutos?.filterKeys { it != "quieto" }?.filterValues { it > 0 }?.takeIf { it.isNotEmpty() }?.let { m ->
            algo = true; Clave("motus", m.entries.sortedByDescending { it.value }.joinToString(" · ") { "${Actividad.NOMBRES[it.key] ?: it.key} ${hm(it.value)}" })
        }
        km?.takeIf { it >= 0.2 }?.let { algo = true; Clave("iter", "%.1f km recorridos".format(it)) }
        if (ses.isNotEmpty()) { algo = true; Clave("exercitatio", ses.joinToString(" · ") { x -> "${x.tipo_nombre} ${hm(x.min)}" + (x.fc_media?.let { " (%.0f lpm)".format(it) } ?: "") }) }
        if (visitas.isNotEmpty()) {
            algo = true
            val porLugar = visitas.groupBy { it.lugar }.mapValues { (_, l) -> l.sumOf { v -> ((Fechas.millis(v.salida ?: Fechas.ahoraIso()) - Fechas.millis(v.entrada)) / 60000).toInt() } }
            Clave("loca", porLugar.entries.sortedByDescending { it.value }.joinToString(" · ") { (id, m) -> (Repo.lugares.firstOrNull { it.id == id }?.nombre ?: "?") + " " + hm(m) })
        }
        p?.let {
            algo = true
            Clave("telephonum", "${hm(it.total_min)} · ${it.desbloqueos} desbloqueos")
            if (it.primer_uso != null) Clave("  usus", "primer uso ${Fechas.horaDe(it.primer_uso)} · último ${it.ultimo_uso?.let { u -> Fechas.horaDe(u) } ?: "?"}")
            it.por_app_min.entries.take(3).takeIf { l -> l.isNotEmpty() }?.let { l -> Clave("  apps", l.joinToString(" · ") { a -> "${a.key} ${hm(a.value)}" }) }
        }
        nt?.let { algo = true; Clave("monita", "${it.total} notificaciones · " + it.por_app.entries.sortedByDescending { x -> x.value }.take(3).joinToString(" · ") { x -> "${x.key} ${x.value}" }) }
        if (musica.isNotEmpty()) {
            algo = true
            val top = musica.groupBy { it.artista.ifBlank { "?" } }.maxByOrNull { it.value.size }?.key
            Clave("musica", "${musica.size} canciones" + (top?.let { " · más: $it" } ?: ""))
        }
        ft?.let { if (it.fotos + it.capturas + it.videos > 0) { algo = true; Clave("imagines", "${it.fotos} fotos · ${it.capturas} capturas · ${it.videos} videos") } }
        ag?.let { if (it.eventos + it.todo_el_dia > 0) { algo = true; Clave("fasti", "${it.eventos} eventos · ${hm(it.min_ocupado)} ocupado" + if (it.titulos.isNotEmpty()) " · " + it.titulos.take(3).joinToString(", ") else "") } }
        e?.let {
            algo = true
            Clave("caelum", listOfNotNull(it.codigo?.let { c -> Clima.descripcion(c) }, if (it.t_max != null) "%.0f° / %.0f°".format(it.t_max, it.t_min ?: it.t_max) else null,
                it.precip_mm?.takeIf { x -> x > 0.05 }?.let { x -> "lluvia %.1f mm".format(x) }, it.nubes_pct?.let { x -> "nubes %.0f%%".format(x) }).joinToString(" · "))
            if (it.luz_min != null) Clave("lux", "${it.amanecer ?: "?"} → ${it.atardecer ?: "?"} · ${hm(it.luz_min)} de luz" + (it.sol_min?.let { x -> " · ${hm(x)} de sol" } ?: ""))
            if (it.aqi_us != null) Clave("aer", "calidad del aire ${it.aqi_us} (${aqiTexto(it.aqi_us)})" + (it.pm25?.let { x -> " · pm2.5 %.0f".format(x) } ?: ""))
        }
        if (s != null) {
            val cuerpo = listOfNotNull(s.peso_kg?.let { "%.1f kg".format(it) }, s.temp_piel_delta?.let { "piel %+.2f °C".format(it) }, s.resp_rpm?.let { "resp %.0f rpm".format(it) },
                s.pa_sistolica?.let { "pa %.0f/%.0f".format(it, s.pa_diastolica ?: 0.0) }, s.cal_activas?.let { "%.0f kcal activas".format(it) }, s.pisos?.let { "%.0f pisos".format(it) },
                s.agua_ml?.let { "%.0f ml de agua".format(it) }, s.vo2max?.let { "vo₂max %.0f".format(it) })
            if (cuerpo.isNotEmpty()) { algo = true; Clave("corpus", cuerpo.joinToString(" · ")) }
        }
        Repo.nutricion[fecha]?.let { nu ->
            val v = nu.valores
            if (v.isNotEmpty()) {
                algo = true
                Clave("nutritio", listOfNotNull(v["kcal"]?.let { "%.0f kcal".format(it) }, v["proteina_g"]?.let { "P %.0f".format(it) }, v["carbohidratos_g"]?.let { "C %.0f".format(it) },
                    v["grasa_g"]?.let { "G %.0f".format(it) }, v["fibra_g"]?.let { "fibra %.0f".format(it) }).joinToString(" · "))
                val extra = listOfNotNull(v["gasto_kcal"]?.let { g -> v["kcal"]?.let { k -> "balance %+.0f".format(k - g) } }, v["cafeina_mg"]?.takeIf { it > 0 }?.let { "cafeína %.0f mg".format(it) + (nu.cafeina_ultima?.let { h -> " (última $h)" } ?: "") },
                    v["alcohol_g"]?.takeIf { it > 0 }?.let { "alcohol %.0f g".format(it) }, if (nu.primera != null) "comiste de ${nu.primera} a ${nu.ultima}" else null)
                if (extra.isNotEmpty()) Clave("  cibus", extra.joinToString(" · "))
            }
        }
        Repo.entrenos[fecha]?.takeIf { it.series > 0 }?.let { e ->
            algo = true
            Clave("robur", (e.entrenos.joinToString(", ").ifBlank { "gimnasio" }) + (e.minutos?.let { " · ${hm(it.toInt())}" } ?: "") +
                " · ${e.series_efectivas} series · %,.0f kg".format(e.volumen_kg))
            e.series_musculo.entries.sortedByDescending { it.value }.take(4).takeIf { it.isNotEmpty() }?.let { m -> Clave("  musculi", m.joinToString(" · ") { "${it.key.replace('_', ' ')} %.0f".format(it.value) }) }
        }
        Repo.comunicacion[fecha]?.let { co ->
            val ll = co.entrantes + co.salientes
            if (ll + co.perdidas + co.sms_enviados + co.sms_recibidos > 0) {
                algo = true
                Clave("voces", "$ll llamadas · ${hm(co.min_hablado.toInt())}" + (if (co.perdidas > 0) " · ${co.perdidas} perdidas" else "") +
                    (if (co.sms_enviados + co.sms_recibidos > 0) " · ${co.sms_enviados + co.sms_recibidos} sms" else "") + " · ${co.contactos} personas")
            }
        }
        val wa = Repo.chats.filter { it.fecha == fecha }
        if (wa.isNotEmpty()) {
            algo = true
            Clave("whatsapp", "${wa.sumOf { it.mios }} enviados · ${wa.sumOf { it.otros }} recibidos · ${wa.size} chats")
        }
        Repo.conexiones[fecha]?.let { cx ->
            algo = true
            val aud = listOfNotNull(cx.audio_min["audifonos"]?.let { "audífonos ${hm(it)}" }, cx.audio_min["coche"]?.let { "coche ${hm(it)}" },
                cx.audio_min["bocina"]?.let { "bocina ${hm(it)}" }, cx.musica_min.takeIf { it > 0 }?.let { "sonando ${hm(it)}" })
            if (aud.isNotEmpty()) Clave("soni", aud.joinToString(" · "))
            cx.wifi_min.entries.sortedByDescending { it.value }.take(2).takeIf { it.isNotEmpty() }?.let { w -> Clave("retia", w.joinToString(" · ") { "${it.key} ${hm(it.value)}" } + " · sin wifi ${hm(cx.sin_wifi_min)}") }
            val n = cx.luz_n.sum()
            if (n > 0) Clave("lumen", "máx %.0f lux · %s con luz de exterior".format(cx.luz_max ?: 0.0, hm(cx.luz_alta_min)))
            if (cx.carga_noche_inicio != null) Clave("vis", "conectado ${Fechas.horaDe(cx.carga_noche_inicio)} → ${cx.carga_noche_fin?.let { Fechas.horaDe(it) } ?: "?"}")
        }
        Repo.huellas[fecha]?.let { h -> algo = true; Clave("google", "${h.yt_vistos} videos · ${h.busquedas + h.yt_busquedas} búsquedas" + (h.canales.keys.firstOrNull()?.let { " · más: $it" } ?: "")) }
        val sub = Repo.subrayados.filter { it.fecha == fecha }
        if (sub.isNotEmpty()) { algo = true; Clave("lectio", "${sub.size} subrayados · " + sub.map { it.libro }.distinct().take(2).joinToString(", ")) }
        val ruidos = Repo.momentos.filter { it.fecha == fecha && !it.privado }.mapNotNull { it.ruido_db }
        if (ruidos.isNotEmpty()) Clave("strepitus", "%.0f dB de media al registrar".format(ruidos.average()))
        // Lo que leíste, viste o jugaste; si lo terminaste ese día, tu calificación y lo que opinaste.
        val obras = Repo.obras.filter { it.titulo.isNotBlank() && (fecha in it.dias || (it.estado == "terminado" && it.fin == fecha)) }
        if (obras.isNotEmpty()) {
            algo = true
            Clave("opera", obras.joinToString(" · ") { o ->
                o.titulo + " (" + (TIPOS_OBRA.firstOrNull { it.id == o.tipo }?.nombre?.lowercase() ?: o.tipo) + ")" +
                    (if (o.estado == "terminado" && o.fin == fecha) " terminado" + (o.calificacion?.let { " · $it/10" } ?: "") else "")
            })
            obras.filter { it.estado == "terminado" && it.fin == fecha && it.nota.isNotBlank() }.forEach { o ->
                Clave("  iudicium", "«" + o.nota.trim().replace(Regex("\\s+"), " ").let { if (it.length > 160) it.take(160) + "…" else it } + "»")
            }
        }
        val citasDia = Repo.obras.flatMap { o -> o.citas.filter { it.fecha == fecha }.map { o to it } }
        if (citasDia.isNotEmpty()) { algo = true; Clave("citatio", "«${citasDia.first().second.texto.take(110)}» — ${citasDia.first().first.titulo}" + if (citasDia.size > 1) " (+${citasDia.size - 1})" else "") }
        Repo.config.frases_guardadas.filter { it.fecha == fecha && it.tema != "kindle" }.forEach { f -> algo = true; Clave("sententia", "♥ «${f.texto.take(110)}» — ${f.autor}") }
        // Dónde estuviste: ciudades, lo que fue primera vez y el Patrimonio Mundial.
        mundo?.let { m ->
            val c = m.ciudades.filter { it.second >= 0.25 || it.second == 0.0 }
            if (c.isNotEmpty()) { algo = true; Clave("orbis", c.take(4).joinToString(" · ") { (e, h) -> "${io.github.ruddy2003.vestigium.data.Mundo.bandera(e.pais)} ${e.nombre}" + if (h > 0) " ${hm((h * 60).toInt())}" else "" }) }
            val nuevas = (m.paisesNuevos.map { it.nombre } + m.ciudadesNuevas.map { it.nombre }).distinct()
            if (nuevas.isNotEmpty()) Clave("  nova", "primera vez en " + nuevas.joinToString(", "))
            if (m.sitios.isNotEmpty()) { algo = true; Clave("mirabilia", m.sitios.joinToString(" · ") { s -> s.n + " (Patrimonio Mundial, " + s.categoria.lowercase() + (if (s.i in m.sitiosNuevos) ", primera vez" else "") + ")" }) }
        }
        // El sueño de esa noche y las cartas.
        Repo.dias[fecha]?.takeIf { it.sueno_texto.isNotBlank() }?.let { d ->
            algo = true
            Clave("somnium", (io.github.ruddy2003.vestigium.ui.LibroDiario.metaSueno(d).ifBlank { "soñaste" }) + " · «" + d.sueno_texto.trim().replace(Regex("\\s+"), " ").take(90) + "…»")
        }
        val cartas = Repo.cartas.filter { it.escrita == fecha }
        if (cartas.isNotEmpty()) { algo = true; Clave("epistulae", cartas.joinToString(" · ") { if (it.para.isBlank()) "sellaste una carta hasta el ${Fechas.corto(it.abrir_en)} ${it.abrir_en.take(4)}" else "carta a @${it.para}" }) }
        Repo.cartas.filter { it.para.isBlank() && it.abierta_el == fecha }.forEach { Clave("  aperta", "abriste la carta que te escribiste el ${Fechas.larga(it.escrita)}") }
        Clave("luna", "${Luna.nombre(fase)} · ${(Luna.iluminacion(fase) * 100).toInt()}% iluminada")
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
private fun Imagenes(fecha: String, elegida: String?, nav: Nav, onElegir: (String) -> Unit) {
    val ctx = LocalContext.current
    val permiso = Galeria.tienePermiso(ctx) || Galeria.parcial(ctx)
    if (!permiso) {
        Vacio("Da acceso a tus fotos en Permisos y sensores para ver aquí las del día.")
        BotonSecundario("dar acceso →", Modifier.padding(top = 8.dp)) { nav.ir("permisos") }
        return
    }
    val fotos by produceState(emptyList<Galeria.Foto>(), fecha) { value = withContext(Dispatchers.IO) { Galeria.delDia(ctx, fecha) } }
    if (fotos.isEmpty()) { Vacio("No hay fotos de este día en tu galería."); return }
    val orden = fotos.sortedWith(compareBy({ it.uri.toString() != elegida }, { it.tipo != "camara" }, { it.ms }))
    elegida?.let { u ->
        Box(Modifier.fillMaxWidth().aspectRatio(1.6f).clip(RoundedCornerShape(4.dp)).border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))) {
            Miniatura(Uri.parse(u), 720, Modifier.fillMaxWidth().aspectRatio(1.6f))
            Text(" IMAGO DIEI ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.align(Alignment.BottomStart).padding(8.dp).background(MaterialTheme.colorScheme.primary))
        }
        Spacer(Modifier.height(8.dp))
    }
    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(orden, key = { it.id }) { f ->
            val sel = f.uri.toString() == elegida
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
        }
    }
    val camara = fotos.count { it.tipo == "camara" }
    Ayuda("$camara de cámara · ${fotos.count { it.tipo == "captura" }} capturas · ${fotos.count { it.tipo == "otra" }} de otras apps", Modifier.padding(top = 6.dp))
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
                TextButton(onClick = { onCorregir(Texto.reemplazar(texto, s, a, b)) }) { Text("corregir", style = MaterialTheme.typography.labelLarge) }
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
 * gastos, ingresos, tomas y notas), del más temprano al más tarde. Cada fila se corrige al tocarla; «＋ agregar a este día»
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
    val filas = buildList {
        Repo.momentos.filter { it.fecha == fecha && !it.privado }.forEach { m ->
            add(Fila(Fechas.millis(m.ts), Fechas.horaDe(m.ts), Catalogo.titulo(m, c.discreto) + (m.valencia?.let { " · $it/7" } ?: "") + nota(m.texto)) { abrir(m) })
        }
        Repo.consumos.filter { it.fecha == fecha && io.github.ruddy2003.vestigium.data.Sustancias.visible(it) }.forEach { x ->
            add(Fila(Fechas.millis(x.inicio), Fechas.horaDe(x.inicio), Catalogo.nombreSustancia(x) + nota(resumenConsumo(x))) { abrirConsumo(ctx, id = x.id) })
        }
        Repo.tramos.filter { it.fecha == fecha }.forEach { t ->
            add(Fila(Fechas.millis(t.inicio), Fechas.horaDe(t.inicio), Catalogo.nombreTramo(t.tipo) + " · " + horasMin(t.min) + nota(t.nota)) { abrir(t) })
        }
        Repo.gastos.filter { it.dia == fecha }.forEach { g ->
            add(Fila(msDe(g.fecha, g.hora), g.hora.take(5), "Gasto · $${"%,.2f".format(g.monto)} · ${g.categoria}" + nota(g.nota)) { abrir(g) })
        }
        Repo.ingresos.filter { it.dia == fecha }.forEach { i ->
            add(Fila(msDe(i.fecha, i.hora), i.hora.take(5), "Ingreso · $${"%,.2f".format(i.monto)} · ${i.categoria}" + nota(i.nota)) { abrir(i) })
        }
        Repo.tomas.filter { it.fecha == fecha }.forEach { t ->
            val s = io.github.ruddy2003.vestigium.data.Suplementos.de(t.suplemento)
            add(Fila(Fechas.millis(t.ts), Fechas.horaDe(t.ts), "${s?.nombre ?: t.suplemento} · ${cifra(t.cantidad)} ${s?.unidad ?: ""}".trim()) { abrir(t) })
        }
        // Las notas van a la hora en que se escribieron si fue ese mismo día; si no, al final.
        Repo.notas.filter { it.fecha == fecha }.forEach { n ->
            val ms = Fechas.millis(n.creada)
            val mismoDia = ms > 0 && Fechas.diaLogicoDeMillis(ms) == fecha
            add(Fila(if (mismoDia) ms else Long.MAX_VALUE, if (mismoDia) Fechas.horaDe(n.creada) else "nota",
                (Catalogo.TIPOS_NOTA.firstOrNull { it.id == n.tipo }?.nombre ?: "Nota") + " · " + n.texto.lines().first().take(90)) { abrir(n) })
        }
    }.sortedBy { it.ms }
    var agregar by remember(fecha) { mutableStateOf(false) }
    Seccion("Tu día", "La línea de 24 horas y todo lo que anotaste, por hora. Toca una fila para corregirla.", numero = numero, plegable = true,
        resumen = if (filas.isEmpty()) "Nada anotado con hora" else "${filas.size} ${if (filas.size == 1) "registro" else "registros"} con hora") {
        LineaDelTiempo(fecha)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            TextButton(onClick = { Seleccion.cinta = fecha; nav.ir("cinta") }) { Text("la cinta completa →", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary) }
            if (Repo.momentos.any { it.fecha == fecha && !it.privado }) TextButton(onClick = { Seleccion.momentos = fecha; nav.ir("hoja_momentos") }) {
                Text("la hoja de la libreta →", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
            }
        }
        if (filas.isNotEmpty()) Tarjeta(Modifier.padding(top = 4.dp)) {
            filas.forEach { f ->
                Row(Modifier.fillMaxWidth().clickable(onClick = f.tocar).padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(f.hora + "  ", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    io.github.ruddy2003.vestigium.ui.TextoMarcado(f.texto, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                    Text(" ›", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
        TextButton(onClick = { agregar = !agregar }) {
            Text(if (agregar) "＋ agregar a este día ▾" else "＋ agregar a este día…", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
        }
        if (agregar) Tarjeta {
            Ayuda("Con la fecha ya puesta: ${Fechas.titulo(fecha).lowercase()}. Lo que anotes después de ese día queda marcado como anotado tarde.")
            FlowRow(Modifier.padding(top = 8.dp), horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Ficha("ánimo con hora", false) {
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
                Ficha("nota", false) { guardarAntes(); Seleccion.notaPara = fecha; nav.ir("notas") }
            }
            if (io.github.ruddy2003.vestigium.data.Suplementos.activos(c).isNotEmpty()) {
                Etiqueta("Suplementos de ese día")
                SuplementosDelDia(fecha)
            }
        }
    }
}

/** Las fichas de los suplementos activos para un día: tocar anota la toma (o la quita si ya estaba, con Deshacer). */
@Composable
fun SuplementosDelDia(fecha: String) {
    val c = Repo.config
    val suplementos = io.github.ruddy2003.vestigium.data.Suplementos.activos(c)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        suplementos.forEach { s ->
            val tomas = Repo.tomas.filter { it.suplemento == s.id && it.fecha == fecha }
            val dosis = if (s.dosis % 1.0 == 0.0) s.dosis.toLong().toString() else s.dosis.toString()
            Ficha((if (tomas.isNotEmpty()) "✓ " else "") + "${s.nombre.lowercase()} · $dosis ${s.unidad}", tomas.isNotEmpty()) {
                if (tomas.isEmpty()) {
                    // Hoy, con la hora de ahora; otro día, a tu hora habitual (o a las 9).
                    val ts = if (fecha == Fechas.diaLogico()) Fechas.ahoraIso()
                        else Fechas.isoDeMillis(Fechas.parse(fecha).atTime((c.suplementos_hora ?: 9).coerceIn(5, 23), 0).atZone(Fechas.zona()).toInstant().toEpochMilli())
                    io.github.ruddy2003.vestigium.data.Suplementos.registrar(s, s.dosis, ts)
                } else {
                    val t = tomas.maxBy { it.ts }
                    Repo.borrarToma(t.id); io.github.ruddy2003.vestigium.ui.Deshacer.ofrecer("Toma quitada") { Repo.guardarToma(t) }
                }
            }
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
    val semana = (Fechas.diasEntre(e.inicio, fecha) / 7).toInt()
    val toca = e.plan.getOrNull(semana) ?: return
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
