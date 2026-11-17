package io.github.ruddy2003.vestigium.ui.galeria

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas as Tela
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.CanvasDrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.CarpetaCuadros
import io.github.ruddy2003.vestigium.data.CuadroGuardado
import io.github.ruddy2003.vestigium.data.CuadrosGuardados
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Deshacer
import io.github.ruddy2003.vestigium.ui.Dialogo
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Sentidos
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.comun.BotonTexto
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.escenas.CuadroHoy
import io.github.ruddy2003.vestigium.ui.escenas.Momento
import io.github.ruddy2003.vestigium.ui.escenas.RELOJ_QUIETO
import io.github.ruddy2003.vestigium.ui.escenas.TipoAncla
import io.github.ruddy2003.vestigium.ui.escenas.aSalvo
import io.github.ruddy2003.vestigium.ui.escenas.gato
import io.github.ruddy2003.vestigium.ui.escenas.gatoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.lienzo
import io.github.ruddy2003.vestigium.ui.escenas.poseDelPaso
import io.github.ruddy2003.vestigium.ui.pantallas.Recuadro
import io.github.ruddy2003.vestigium.ui.rememberSentir
import io.github.ruddy2003.vestigium.ui.vida.EstadoMueble
import io.github.ruddy2003.vestigium.ui.vida.MuebleGaleriaComun
import io.github.ruddy2003.vestigium.ui.vida.ParedDeCuadros
import io.github.ruddy2003.vestigium.ui.vida.disenoVida
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

// =====================================================================
//  La galería de cuadros guardados (parte 8, encargo C; `docs/diseno/CUADROS_PLAN.md`)
// =====================================================================
//
// - «Me gusta» ([BotonMeGusta]) pinta el cuadro tal cual se ve ([pintarCuadro]), lo guarda en JPEG en `files/cuadros/`
//   (`CarpetaCuadros`) y después sus datos en `Config.cuadros_guardados` ([guardarCuadro]). Volver a tocarlo lo quita, con
//   «deshacer»: los datos se van al momento y la imagen se borra cuando ya no se puede deshacer.
// - En Vida, el mueble de la galería ([RecuadroGaleria]) con los cuatro últimos en sus marcos (`ParedDeCuadros`).
// - La galería ([PantallaGaleriaCuadros]): la cuadrícula, del más nuevo al más viejo; al tocar uno, grande y con sus datos.
// Nada de esto es una medida: no se exporta (D.199). Las imágenes viajan en el respaldo completo.

/**
 * Pinta el cuadro [cuadro] a la [hora] (0 a 24) fuera de la pantalla, a [ancho] × [alto] pixeles: la parte quieta, la viva en
 * el instante quieto ([RELOJ_QUIETO], el de las capturas y de las animaciones apagadas) y el gato sentado donde se sienta
 * cuando no se anima (`gatoQuieto`, como `CapaGato`), con su luz. Es el cuadro tal como se ve, sin la banderita ni la marca
 * de día especial. No toca la pantalla: se puede llamar desde cualquier hilo (`Dispatchers.Default`). La densidad es la de
 * un teléfono de 412 dp de ancho, para que los trazos en dp salgan como en el teléfono.
 *
 * Si la parte viva o el gato fallan al dibujar, se quedan fuera y el cuadro sale sin ellos; si falla la quieta, lanza.
 */
fun pintarCuadro(cuadro: CuadroHoy, hora: Double, ancho: Int = CuadrosGuardados.ANCHO, alto: Int = CuadrosGuardados.ALTO): ImageBitmap {
    require(ancho > 0 && alto > 0) { "un cuadro de $ancho × $alto" }
    val escena = cuadro.escena
    val momento = Momento(cuadro.ficha, hora)
    val imagen = ImageBitmap(ancho, alto)
    CanvasDrawScope().draw(Density(ancho / 412f), LayoutDirection.Ltr, Tela(imagen), Size(ancho.toFloat(), alto.toFloat())) {
        lienzo { with(escena) { quieta(momento) } }
        // Lo que falle se deshace sin dejar el lienzo girado o recortado (`aSalvo`) y el cuadro sale sin ello.
        aSalvo({}) { lienzo { with(escena) { viva(momento, RELOJ_QUIETO) } } }
        aSalvo({}) {
            val anclas = escena.anclas(momento)
            val suelo = anclas.firstOrNull { it.tipo == TipoAncla.SUELO }
            if (suelo != null) {
                val talla = escena.tallaDelGato
                val p = gatoQuieto(suelo, anclas, talla, escena.estorbos(momento))
                val pelaje = cuadro.pelaje
                lienzo {
                    val pie = Offset(p.x, suelo.y + p.alza)
                    gato(poseDelPaso(p, pelaje), pelaje, escena.luzDelGato(momento, pie), pie, p.dir, talla * p.escala, p.dy, pelaje.especiales.getOrNull(p.especial), p.i)
                }
            }
        }
    }
    imagen.prepareToDraw()
    return imagen
}

/** Una hora cualquiera llevada a [0, 24). */
private fun horaDelDia(h: Double): Double = (((h % 24) + 24) % 24).let { if (it >= 24.0) 0.0 else it }

/**
 * Guarda [cuadro] a la [hora] en la galería: lo pinta ([pintarCuadro]), escribe la imagen (`CarpetaCuadros.escribir`, JPEG
 * al `CuadrosGuardados.CALIDAD` %) y, solo si salió, sus datos (`CuadrosGuardados.guardar`, por `Repo.cambiarConfig`).
 * Devuelve lo guardado, o null si no se pudo (y entonces no queda nada: ni datos ni imagen). Fuera del hilo de la pantalla.
 */
suspend fun guardarCuadro(ctx: Context, cuadro: CuadroHoy, hora: Double): CuadroGuardado? = withContext(Dispatchers.Default) {
    val app = ctx.applicationContext
    val h = horaDelDia(hora)
    val id = Repo.nuevoId()
    val g = CuadrosGuardados.nuevo(id, cuadro.tema.id, cuadro.quien.id, cuadro.ficha.dia, h, Fechas.ahoraIso(), Momento(cuadro.ficha, h).claves)
        ?: return@withContext null
    var mapa: Bitmap? = null
    try {
        val bmp = pintarCuadro(cuadro, h).asAndroidBitmap()
        mapa = bmp
        val nombre = withContext(Dispatchers.IO) {
            CarpetaCuadros.escribir(app, id) { f -> f.outputStream().use { out -> check(bmp.compress(Bitmap.CompressFormat.JPEG, CuadrosGuardados.CALIDAD, out)) { "no se pudo comprimir" } } }
        } ?: return@withContext null
        if (nombre != g.archivo) { CarpetaCuadros.borrar(app, g.copy(archivo = nombre)); return@withContext null }
        withContext(Dispatchers.IO) { Repo.cambiarConfig { CuadrosGuardados.guardar(it, g) } }
        g
    } catch (e: CancellationException) {
        // Si se canceló justo después de guardar los datos, la imagen se queda: ya tiene quien la use.
        if (Repo.config.cuadros_guardados.none { it.id == id }) CarpetaCuadros.borrar(app, g)
        throw e
    } catch (e: Exception) {
        if (Repo.config.cuadros_guardados.none { it.id == id }) CarpetaCuadros.borrar(app, g)
        null
    } finally {
        mapa?.recycle()
    }
}

/**
 * Quita [g] de la galería: los datos se van al momento y se ofrece «deshacer»; la imagen se borra cuando ya no se puede
 * deshacer (si la app se cierra antes, la borra después `CarpetaCuadros.limpiar`).
 */
internal fun quitarCuadro(ctx: Context, g: CuadroGuardado) {
    val app = ctx.applicationContext
    Repo.cambiarConfig { CuadrosGuardados.quitar(it, g.id) }
    Deshacer.ofrecer("Quitaste el cuadro de la galería", alExpirar = { Miniaturas.olvidar(g); CarpetaCuadros.borrar(app, g) }) {
        Repo.cambiarConfig { CuadrosGuardados.guardar(it, g) }
    }
}

/**
 * El «me gusta» del cuadro: guarda [cuadro] a la [hora] ([guardarCuadro]) o, si ya está guardado
 * (`CuadrosGuardados.yaGuardado`, el mismo tramo de veinte minutos), lo quita. Lo ponen el cuadro a pantalla completa y el de
 * la pantalla del cuento (encargo B), sobre el cuadro: un corazón blanco en un círculo oscuro translúcido, que se llena al
 * guardarlo. Mientras pinta (unas décimas de segundo) late y no se puede volver a tocar.
 */
@Composable
fun BotonMeGusta(cuadro: CuadroHoy, hora: Double, modifier: Modifier = Modifier) {
    val ctx = LocalContext.current
    val sentir = rememberSentir()
    val alcance = rememberCoroutineScope()
    val puesto = CuadrosGuardados.yaGuardado(Repo.config, cuadro.tema.id, cuadro.quien.id, cuadro.ficha.dia, horaDelDia(hora))
    var pintando by remember { mutableStateOf(false) }
    val animar = Repo.config.animaciones
    val lleno by animateFloatAsState(if (puesto != null) 1f else 0f, if (animar) tween(260) else tween(0), label = "meGusta")
    val latido = if (pintando && animar) {
        val t = rememberInfiniteTransition(label = "pintando")
        t.animateFloat(0.86f, 1.08f, infiniteRepeatable(tween(380), RepeatMode.Reverse), label = "latido").value
    } else 1f
    val rosa = lerp(Color(0xFFE8506A), MaterialTheme.colorScheme.primary, 0.18f)
    Box(
        modifier.size(44.dp).clip(CircleShape).background(Color(0xFF14111C).copy(alpha = 0.42f))
            .clickable(enabled = !pintando, role = Role.Button) {
                if (puesto != null) { sentir(Sentidos.Evento.TOQUE); quitarCuadro(ctx, puesto); return@clickable }
                pintando = true
                alcance.launch {
                    val g = try { guardarCuadro(ctx, cuadro, hora) } finally { pintando = false }
                    if (g != null) sentir(Sentidos.Evento.GUARDAR)
                    else { sentir(Sentidos.Evento.ERROR); Toast.makeText(ctx, "No se pudo guardar el cuadro.", Toast.LENGTH_SHORT).show() }
                }
            }
            .semantics { contentDescription = if (puesto != null) "Quitar el cuadro de la galería" else if (pintando) "Guardando el cuadro" else "Guardar el cuadro en la galería" },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(Modifier.size(24.dp)) {
            val k = size.minDimension * 0.30f * latido
            val c = Offset(size.width / 2f, size.height / 2f + k * 0.12f)
            val corazon = Path().apply {
                moveTo(c.x, c.y + k * 1.15f)
                cubicTo(c.x - k * 2.1f, c.y - k * 0.05f, c.x - k * 1.0f, c.y - k * 1.6f, c.x, c.y - k * 0.5f)
                cubicTo(c.x + k * 1.0f, c.y - k * 1.6f, c.x + k * 2.1f, c.y - k * 0.05f, c.x, c.y + k * 1.15f)
                close()
            }
            val relleno = if (pintando) 0.45f else lleno
            if (relleno > 0f) drawPath(corazon, rosa.copy(alpha = relleno))
            drawPath(corazon, Color.White.copy(alpha = 0.95f), style = Stroke(max(1f, 1.7.dp.toPx()), cap = StrokeCap.Round))
        }
    }
}

// ---------------------------------------------------------------- las miniaturas

/** Las miniaturas decodificadas, en memoria mientras la app vive (hasta unos 24 MB). */
internal object Miniaturas {
    private val memoria = object : LruCache<String, ImageBitmap>(24 * 1024 * 1024) {
        override fun sizeOf(key: String, value: ImageBitmap): Int = max(1, value.width * value.height * 4)
    }

    private fun clave(g: CuadroGuardado, px: Int) = "${g.archivo}@$px"

    fun enMemoria(g: CuadroGuardado, px: Int): ImageBitmap? = memoria.get(clave(g, px))

    /** Olvida las miniaturas de [g] (al borrar su imagen). */
    fun olvidar(g: CuadroGuardado) { memoria.snapshot().keys.filter { it.startsWith("${g.archivo}@") }.forEach { memoria.remove(it) } }

    /** La imagen de [g] reducida (`inSampleSize`) hasta no bajar de [px] por lado, o null si no está en este teléfono. Fuera del hilo de la pantalla. */
    fun leer(ctx: Context, g: CuadroGuardado, px: Int): ImageBitmap? {
        enMemoria(g, px)?.let { return it }
        val f: File = CarpetaCuadros.archivo(ctx, g) ?: return null
        val minimo = px.coerceAtLeast(16)
        val img = runCatching {
            val o = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeFile(f.path, o)
            if (o.outWidth <= 0 || o.outHeight <= 0) return null
            var s = 1
            while (s < 64 && max(o.outWidth, o.outHeight) / (s * 2) >= minimo) s *= 2
            BitmapFactory.decodeFile(f.path, BitmapFactory.Options().apply { inSampleSize = s })?.asImageBitmap()
        }.getOrNull() ?: return null
        memoria.put(clave(g, px), img)
        return img
    }
}

/** La miniatura de [g] de unos [px] pixeles: null mientras se lee (o si no está en este teléfono). */
@Composable
private fun rememberMiniatura(g: CuadroGuardado, px: Int): ImageBitmap? {
    val ctx = LocalContext.current.applicationContext
    var img by remember(g.archivo, px) { mutableStateOf(Miniaturas.enMemoria(g, px)) }
    LaunchedEffect(g.archivo, px) { if (img == null) img = withContext(Dispatchers.IO) { Miniaturas.leer(ctx, g, px) } }
    return img
}

// ---------------------------------------------------------------- el mueble de Vida

/**
 * En la pestaña Vida: el mueble de la galería del tema (`DisenoVida.muebleGaleria`) con los cuatro últimos cuadros en sus
 * marcos; tocarlo abre la galería. Sin cuadros, el mueble vacío con un corazón a lápiz en el marco grande y la invitación.
 */
@Composable
fun RecuadroGaleria(nav: Nav) {
    val todos = CuadrosGuardados.todos(Repo.config)
    val ultimos = todos.take(MuebleGaleriaComun.CUANTOS)
    val imagenes = ultimos.map { g -> key(g.id) { rememberMiniatura(g, 420) } }
    MuebleGaleria(TextosGaleria.detalle(todos.size, todos.firstOrNull()?.dia), imagenes) { nav.ir("galeria_cuadros") }
}

/** El recuadro de la galería con estas [imagenes] (las capturas lo llaman con cuadros pintados ahí mismo). */
@Composable
internal fun MuebleGaleria(detalle: String, imagenes: List<ImageBitmap?>, alTocar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val d = disenoVida(Estilo.tema)
    val oscuro = esOscuro()
    // Sin la lámpara de la escena: el mueble trae la suya (la lámpara de cuadro, encendida de noche).
    Recuadro("Galería de cuadros", detalle, alto = 190.dp, luz = false, onClick = alTocar) {
        ParedDeCuadros(d, EstadoMueble(cs.surface, cs.primary, cs.secondary, cs.onSurface, 11), oscuro, imagenes)
    }
}

// ---------------------------------------------------------------- la galería

/** La galería (ruta `galeria_cuadros`): los cuadros guardados con sus datos (tema, vida, día y hora), del más nuevo al más viejo. */
@Composable
fun PantallaGaleriaCuadros(nav: Nav) {
    val ctx = LocalContext.current.applicationContext
    // Al abrirla, en segundo plano: las imágenes que ya no usa ningún cuadro (un guardado a medias, uno quitado).
    LaunchedEffect(Unit) { withContext(Dispatchers.IO) { runCatching { CarpetaCuadros.limpiar(ctx, Repo.config) } } }
    val todos = CuadrosGuardados.todos(Repo.config)
    ListaGaleria(nav, todos) { g, px -> rememberMiniatura(g, px) }
}

/** Cuántos se ven de golpe; «Ver más» agrega otros tantos (así no se decodifican cientos al abrir). */
private const val POR_TANDA = 30

/** El cuerpo de la galería con su forma de leer cada miniatura ([miniatura]); las capturas le dan cuadros pintados ahí mismo. */
@Composable
internal fun ListaGaleria(nav: Nav, todos: List<CuadroGuardado>, miniatura: @Composable (CuadroGuardado, Int) -> ImageBitmap?) {
    val ctx = LocalContext.current
    var visibles by remember { mutableIntStateOf(POR_TANDA) }
    var abierto by remember { mutableStateOf<CuadroGuardado?>(null) }
    Hoja("Galería de cuadros", nav.atras, subtitulo = "Los cuadros que guardaste con ♥, del más nuevo al más viejo.") {
        Ayuda(TextosGaleria.RESPALDO, Modifier.padding(top = 4.dp, bottom = 12.dp))
        if (todos.isEmpty()) {
            Ayuda("Todavía no guardas ninguno. Toca ♥ sobre el cuadro a pantalla completa o arriba del cuento: se guarda tal como se ve en ese momento.",
                Modifier.padding(top = 8.dp))
            return@Hoja
        }
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            todos.take(visibles).chunked(3).forEach { fila ->
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    fila.forEach { g -> key(g.id) { Miniatura(g, miniatura(g, 360), Modifier.weight(1f)) { abierto = g } } }
                    repeat(3 - fila.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
        if (todos.size > visibles) BotonSecundario("Ver más (${todos.size - visibles})", Modifier.padding(top = 14.dp)) { visibles += POR_TANDA }
    }
    abierto?.let { g -> DetalleCuadro(g, miniatura(g, 1200), cerrar = { abierto = null }, quitar = { abierto = null; quitarCuadro(ctx, g) }) }
}

/** Una celda: la miniatura 4:3 (o su sitio en blanco) y debajo el día y la hora. */
@Composable
private fun Miniatura(g: CuadroGuardado, img: ImageBitmap?, modifier: Modifier, alTocar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Column(modifier.clip(RoundedCornerShape(6.dp)).clickable(onClickLabel = "Ver el cuadro") { alTocar() }) {
        Imagen(img, Modifier.fillMaxWidth().aspectRatio(4f / 3f).clip(RoundedCornerShape(6.dp)))
        Text("${Fechas.corto(g.dia)} · ${TextosGaleria.hora(g.hora)}", style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant,
            maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.padding(top = 3.dp, start = 2.dp))
    }
}

/** La imagen, o un recuadro tenue si todavía se lee o no está en este teléfono. */
@Composable
private fun Imagen(img: ImageBitmap?, modifier: Modifier) {
    val cs = MaterialTheme.colorScheme
    if (img != null) androidx.compose.foundation.Image(img, null, modifier, contentScale = ContentScale.Crop)
    else Box(modifier.background(lerp(cs.surface, cs.onSurface, 0.08f)))
}

/** El cuadro grande con sus datos y «quitar». */
@Composable
private fun DetalleCuadro(g: CuadroGuardado, img: ImageBitmap?, cerrar: () -> Unit, quitar: () -> Unit) {
    val ctx = LocalContext.current
    val cs = MaterialTheme.colorScheme
    val tema = TemaId.entries.firstOrNull { it.id == g.tema }?.nombre ?: g.tema
    val quien = OtrasVidas.porId(g.vida)
    val hay = remember(g.id) { CarpetaCuadros.archivo(ctx, g) != null }
    Dialogo(
        onDismissRequest = cerrar,
        confirmButton = { BotonTexto(onClick = cerrar) { Text("Cerrar") } },
        dismissButton = { BotonTexto(onClick = quitar) { Text("Quitar", color = cs.error) } },
        title = { Text(quien?.nombre ?: g.vida) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Imagen(img, Modifier.fillMaxWidth().aspectRatio(4f / 3f).clip(RoundedCornerShape(4.dp)))
                if (!hay) Ayuda("La imagen no está en este teléfono: llega con el respaldo completo que la traiga.")
                Spacer(Modifier.size(6.dp))
                Dato("Tema", tema)
                quien?.let { Dato("Personaje", "${it.nombre}, ${it.rol}") }
                Dato("Día", Fechas.larga(g.dia))
                Dato("Hora del cuadro", TextosGaleria.hora(g.hora))
                Dato("Guardado", TextosGaleria.guardado(g.guardado))
                if (g.claves.isNotEmpty()) Dato("En la escena", g.claves.joinToString(", "))
            }
        },
    )
}

@Composable
private fun Dato(nombre: String, valor: String) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth()) {
        Text(nombre, style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, modifier = Modifier.weight(0.38f))
        Text(valor, style = MaterialTheme.typography.bodySmall, color = cs.onSurface, modifier = Modifier.weight(0.62f))
    }
}
