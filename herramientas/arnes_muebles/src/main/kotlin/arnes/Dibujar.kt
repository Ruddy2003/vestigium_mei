// El arnés de los muebles: dibuja en PNG el mueble del Ideario o el estante del diario de un tema, con los composables reales
// de la app (MuebleIdeas y EstanteTema) y los mismos datos de ejemplo que las capturas de Paparazzi (Capturas.kt).
//
//   dibujar ideario <tema> <claro|oscuro> <salida.png>
//   dibujar biblioteca <tema> <claro|oscuro> <salida.png>
//   dibujar hoja <ideario|biblioteca|galeria|tapa|libreta> <claro|oscuro> <salida.png> <tema1,tema2,...>
//   dibujar tapa <tema> <claro|oscuro|hoja> <salida.png>      (el libro del día; arnes/TapaLibreta.kt)
//   dibujar libreta <tema> <claro|oscuro|hoja> <salida.png>   (la libreta de momentos con su índice)
//
// Ver LEEME.md.
package arnes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ImageComposeScene
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.use
import io.github.ruddy2003.vestigium.data.Config
import io.github.ruddy2003.vestigium.data.Dia
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Idea
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.porNumero
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.archivoIdeas
import io.github.ruddy2003.vestigium.ui.biblioteca.EstanteTema
import io.github.ruddy2003.vestigium.ui.biblioteca.GrupoEstante
import io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca
import io.github.ruddy2003.vestigium.ui.esquemaDe
import io.github.ruddy2003.vestigium.ui.ideario.MuebleIdeas
import io.github.ruddy2003.vestigium.ui.ideario.disenoIdeas
import io.github.ruddy2003.vestigium.ui.pantallas.cajones
import io.github.ruddy2003.vestigium.ui.pantallas.rotuloCajon
import io.github.ruddy2003.vestigium.ui.tipoDe
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface
import java.io.File
import kotlin.system.exitProcess

/** El ancho de las capturas (Pixel 6 de Paparazzi: 1080 px a 420 dpi, es decir 2.625 px por dp). */
private const val ANCHO = 1080
private const val DENSIDAD = 2.625f
/** Alto del lienzo antes de recortar: sobra para cualquier mueble de los ejemplos. */
private const val ALTO_MAXIMO = 12000

private fun uso(): Nothing {
    System.err.println("""
        Uso:
          dibujar.sh ideario <tema> <claro|oscuro> <salida.png>
          dibujar.sh biblioteca <tema> <claro|oscuro> <salida.png>
          dibujar.sh hoja <ideario|biblioteca|galeria|tapa|libreta> <claro|oscuro> <salida.png> <tema1,tema2,...>
          dibujar.sh tapa <tema> <claro|oscuro|hoja> <salida.png>      (el libro del día con su texto encima; «hoja»: claro y oscuro lado a lado)
          dibujar.sh libreta <tema> <claro|oscuro|hoja> <salida.png>   (la libreta de momentos con su índice; «hoja»: claro y oscuro lado a lado)
          dibujar.sh trazos psicodelia <claro|oscuro> <salida.png>   (muestra de TrazosCiudad o TrazosTinta, según ARNES_CARA)
          dibujar.sh comun psicodelia <claro|oscuro> <salida.png>    (las piezas comunes y de decoración de Psicodelia, según ARNES_CARA)
          dibujar.sh logros psicodelia <claro|oscuro> <salida.png>   (los trofeos, el hall, el salón y la campaña de Psicodelia, según ARNES_CARA)
          dibujar.sh vida <tema> <claro|oscuro> <salida.png>   (los muebles de la pestaña Vida y el tablero de notas)
          dibujar.sh galeria <tema> <claro|oscuro> <salida.png>   (el mueble de la galería de cuadros; ARNES_CUADROS=0,1,5)
        Temas: ${TemaId.entries.joinToString(" ") { it.id }}
        Variable ARNES_HOY=AAAA-MM-DD: el «hoy» de los ejemplos (por omisión, el día lógico de hoy; las capturas de CI usan el día en que corrieron).
        Variables ARNES_TEMAS=0,1,5 (un Ideario por cada número de temas) y ARNES_MAX=6 (el Ideario cortado, con su placa «y N más»).
        Variable ARNES_SUENOS=1: la biblioteca como estante de los Sueños (el modo «noche» del estante, aparte de claro y oscuro).
        Variables ARNES_CARA=base|intensa (la cara de los temas que la tienen; por omisión, base) y ARNES_INTENSIDAD=0..1 (por omisión, 0).
        Tapa y libreta: ARNES_TAPA=vacio,corto,largo,noche,noche_vacio,abierto (los estados de la tapa), ARNES_MOMENTOS=0,pocos,muchos,40
          (los conjuntos de la libreta), ARNES_MESES=3 (meses del índice), ARNES_ABIERTO=0..1, ARNES_LUGAR=diario|suelto|biblioteca
          y ARNES_ANCHO=411 (el ancho de la pantalla en dp; por omisión, el del Pixel 6).
    """.trimIndent())
    exitProcess(2)
}

private fun tema(id: String): TemaId = TemaId.entries.firstOrNull { it.id == id.trim() } ?: run {
    System.err.println("Tema desconocido: «$id».")
    uso()
}

fun main(args: Array<String>) {
    if (args.size < 4) uso()
    val salida = File(args[3])
    val inicio = System.nanoTime()
    // La tapa y la libreta de un tema con «hoja» en lugar del modo: el claro y el oscuro lado a lado (los de solo oscuro, solo el oscuro).
    if (args[0] in setOf("tapa", "libreta") && args[2] == "hoja") {
        val t = tema(args[1])
        val modos = if (t.soloOscuro) listOf(true) else listOf(false, true)
        escribir(juntar(modos.map { o -> dibujar(args[0], t, o, "${t.nombre} (${t.id}) · ${if (o) "oscuro" else "claro"}") }), salida, inicio)
    }
    val oscuro = when (args[2]) { "claro" -> false; "oscuro" -> true; else -> uso() }
    val imagen = when (args[0]) {
        "ideario", "biblioteca", "trazos", "comun", "logros" -> dibujar(args[0], tema(args[1]), oscuro, null)
        // El modo vida (arnes/Vida.kt): los muebles de la pestaña Vida y el tablero de notas de un tema.
        "vida" -> dibujar("vida", tema(args[1]), oscuro, null)
        // El mueble de la galería de cuadros (arnes/Galeria.kt), con 0, 1 y 5 cuadros (ARNES_CUADROS).
        "galeria" -> dibujar("galeria", tema(args[1]), oscuro, null)
        // El libro del día y la libreta de momentos con su índice (arnes/TapaLibreta.kt).
        "tapa", "libreta" -> dibujar(args[0], tema(args[1]), oscuro, null)
        "hoja" -> {
            if (args.size < 5 || args[1] !in setOf("ideario", "biblioteca", "galeria", "tapa", "libreta")) uso()
            val temas = args[4].split(',').filter { it.isNotBlank() }.map(::tema)
            juntar(temas.map { t -> dibujar(args[1], t, oscuro, t.nombre + " (" + t.id + ")") })
        }
        else -> uso()
    }
    escribir(imagen, salida, inicio)
}

/** Escribe [imagen] en [salida] como PNG y termina. */
private fun escribir(imagen: Image, salida: File, inicio: Long): Nothing {
    salida.absoluteFile.parentFile?.mkdirs()
    // Se escribe en un temporal y se renombra: quien mire el archivo nunca lo ve a medias.
    val tmp = File(salida.absoluteFile.parentFile, ".${salida.name}.${ProcessHandle.current().pid()}.tmp")
    tmp.writeBytes(imagen.encodeToData(EncodedImageFormat.PNG)!!.bytes)
    if (!tmp.renameTo(salida)) { salida.delete(); tmp.renameTo(salida) }
    println("${salida.path}: ${imagen.width}×${imagen.height} px, ${(System.nanoTime() - inicio) / 1_000_000} ms")
    exitProcess(0)
}

/** El «hoy» de los ejemplos. Las capturas de CI usan el día lógico del momento en que corrieron. */
internal fun hoy(): String = System.getenv("ARNES_HOY")?.takeIf { it.isNotBlank() } ?: Fechas.diaLogico()

/** La configuración de las capturas (`Capturas.config`): el tema, el modo y sin animaciones. */
private fun configurar(t: TemaId, oscuro: Boolean) {
    Repo.config = Config(tema = t.id, modo = if (oscuro) "oscuro" else "claro", animaciones = false, bienvenida_vista = true, widget_cifra = "racha")
    // La cara y la intensidad de los temas que las tienen (Psicodelia), fijas: el dibujo no depende de la hora en que se corre.
    EstadoTema.forzar(cara(), intensidad())
}

/** ARNES_CARA=base|intensa: la cara (la ciudad o la psicodelia, en Psicodelia). Por omisión, base. */
private fun cara(): Cara = when (System.getenv("ARNES_CARA")?.trim()?.lowercase()) {
    null, "", "base" -> Cara.BASE
    "intensa" -> Cara.INTENSA
    else -> { System.err.println("ARNES_CARA debe ser «base» o «intensa»."); uso() }
}

/** ARNES_INTENSIDAD=0..1: la intensidad de los consumos en curso (en Psicodelia). Por omisión, 0. */
private fun intensidad(): Float = System.getenv("ARNES_INTENSIDAD")?.trim()?.takeIf { it.isNotEmpty() }?.let {
    it.toFloatOrNull()?.takeIf { v -> v.isFinite() && v in 0f..1f } ?: run { System.err.println("ARNES_INTENSIDAD debe ser un número de 0 a 1."); uso() }
} ?: 0f

/**
 * Dibuja un mueble en una escena de Compose de 1080 px de ancho y la recorta a lo que ocupa. [titulo], si va, se escribe
 * encima (para las hojas).
 */
private fun dibujar(que: String, t: TemaId, oscuro: Boolean, titulo: String?): Image {
    configurar(t, oscuro)
    val o = t.soloOscuro || oscuro
    var alto = 0
    // La tapa y la libreta se pueden dibujar en un teléfono más angosto (ARNES_ANCHO, en dp); los demás, siempre a 1080 px.
    val ancho = if (que == "tapa" || que == "libreta") anchoArnes() else ANCHO
    val escena = ImageComposeScene(ancho, ALTO_MAXIMO, Density(DENSIDAD, 1f)) {
        // Como `TemaVestigium` de la app (Tema.kt), sin lo que toca la ventana del teléfono.
        MaterialTheme(colorScheme = esquemaDe(t, o), typography = tipoDe(t, o)) {
            Column(Modifier.fillMaxWidth().wrapContentHeight(Alignment.Top, unbounded = true).background(MaterialTheme.colorScheme.background)
                .onSizeChanged { alto = it.height }) {
                if (titulo != null) Text(titulo, color = MaterialTheme.colorScheme.onBackground, fontSize = 18.sp, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 16.dp, top = 12.dp))
                when (que) {
                    "ideario" -> Ideario()
                    "trazos" -> MuestraTrazos(o)
                    "comun" -> MuestraComun(o)
                    "logros" -> MuestraLogros(o)
                    "vida" -> Vida()
                    "galeria" -> GaleriaArnes()
                    "tapa" -> TapasArnes()
                    "libreta" -> LibretaArnes()
                    else -> Biblioteca()
                }
            }
        }
    }
    return escena.use { s ->
        s.render(0)
        val img = s.render(16_000_000)
        recortar(img, ancho, alto.coerceIn(1, ALTO_MAXIMO))
    }
}

/** ARNES_ANCHO=dp: el ancho de la pantalla para la tapa y la libreta (por omisión, 411.4 dp: los 1080 px del Pixel 6). */
private fun anchoArnes(): Int = System.getenv("ARNES_ANCHO")?.trim()?.takeIf { it.isNotEmpty() }?.let {
    val dp = it.toFloatOrNull()?.takeIf { v -> v.isFinite() && v in 200f..900f } ?: run { System.err.println("ARNES_ANCHO debe ser un ancho en dp, de 200 a 900."); uso() }
    kotlin.math.round(dp * DENSIDAD).toInt()
} ?: ANCHO

private fun recortar(img: Image, w: Int, h: Int): Image {
    val sup = Surface.makeRasterN32Premul(w, h)
    sup.canvas.drawImage(img, 0f, 0f)
    return sup.makeImageSnapshot()
}

/** Varias imágenes lado a lado, con una separación gris. */
private fun juntar(imgs: List<Image>): Image {
    val sep = 24
    val w = imgs.sumOf { it.width } + sep * (imgs.size + 1)
    val h = imgs.maxOf { it.height } + sep * 2
    val sup = Surface.makeRasterN32Premul(w, h)
    sup.canvas.clear(0xFF808080.toInt())
    var x = sep
    imgs.forEach { sup.canvas.drawImage(it, x.toFloat(), sep.toFloat()); x += it.width + sep }
    return sup.makeImageSnapshot()
}

// ---------------------------------------------------------------- el Ideario

/**
 * Las ideas de `Capturas.ideasConTemas(n)`: repartidas en [n] temas, cada uno con más o menos fichas; uno de nombre largo
 * y, con más de uno, el último es el cajón de las ideas sin tema.
 */
internal fun ideasConTemas(n: Int): List<Idea> {
    val temas = listOf("filosofia", "dinero", "historia_de_la_ciencia_y_de_la_tecnica", "salud", "aprender", "fisica", "amistad", "escritura", "cocina")
    val hoy = hoy()
    return (0 until n).flatMap { k ->
        (0..(k * 3 % 7)).map { j ->
            val f = Fechas.sumar(hoy, -(k * 2L + j))
            val sinTema = n > 1 && k == n - 1
            Idea("m${k}_$j", f + "T19:00:00-06:00", f, "Una idea inventada número ${j + 1}.",
                temas = if (sinTema) emptyList() else listOf(temas[k % temas.size]))
        }
    }
}

/**
 * La captura `temaMueble` «_b»: `FicheroIdeas` con 9 temas, dentro de una columna con 16 dp de margen. La llamada a
 * [MuebleIdeas] es la de `FicheroIdeas` (ui/pantallas/Fichero.kt), con un toque que no hace nada.
 */
@Composable
private fun Ideario() {
    // ARNES_TEMAS=0,1,5 dibuja un mueble por cada número de temas, uno bajo otro (la captura «_a» es «0,1,5»); por omisión, 9.
    // ARNES_MAX=6 corta el mueble a ese número de compartimentos y pone la placa «y N más» (la captura «_c»).
    val cuantos = System.getenv("ARNES_TEMAS")?.split(',')?.mapNotNull { it.trim().toIntOrNull() }?.takeIf { it.isNotEmpty() } ?: listOf(9)
    val max = System.getenv("ARNES_MAX")?.trim()?.toIntOrNull()
    val a = archivoIdeas(Estilo.tema)
    val d = disenoIdeas(Estilo.tema)
    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
        cuantos.forEach { n ->
            val todos = cajones(ideasConTemas(n))
            val lista = if (max != null) todos.take(max) else todos
            val mas = todos.size - lista.size
            MuebleIdeas(lista, ::rotuloCajon, { c -> a.cuenta(c.n) + (if (d.cols <= 2) c.ultima?.let { " · " + Fechas.corto(it) } ?: "" else "") },
                mas = mas, textoMas = "y $mas ${porNumero(mas, a.contenedor, a.contenedores)} más") { }   // con toque: así lleva el aura (halo) como en la app
        }
    }
}

// ---------------------------------------------------------------- la biblioteca del diario

/** Los días de `Capturas.Ejemplo.cargar()` (solo los días: es lo único que lee el estante), con el mismo azar. */
internal fun diasEjemplo(): Map<String, Dia> {
    val hoy = hoy()
    val r = java.util.Random(7)
    val fechas = (0..35).map { Fechas.sumar(hoy, -it.toLong()) }
    val notas = listOf(
        "Día normal con @ana. Avancé el capítulo dos de la #tesis y en la tarde fui a escalar; los bloques del muro nuevo están más duros de lo que parecen.",
        "Me desperté tarde y me costó arrancar. Comida en casa y una siesta larga. En la noche leí un rato.",
        "Escribí 300 palabras del #protocolo y por fin entendí cómo ordenar el marco teórico.",
        "Salí a correr temprano, hacía frío. Café con @luis en el centro; hablamos de trabajo y de planes para el año que viene.",
    )
    return fechas.drop(1).associateWith { f -> Dia(fecha = f, valencia = 3 + r.nextInt(4), nota = notas[r.nextInt(notas.size)], sueno_calidad = 3, mejor_momento = "el café de la tarde", sueno_recordado = f.takeLast(2).toInt() % 3 == 0) } +
        (hoy to Dia(fecha = hoy, sueno_calidad = null, valencia = null))
}

/** Si se dibuja el estante de los Sueños (ARNES_SUENOS=1). */
internal val conSuenos: Boolean get() = System.getenv("ARNES_SUENOS") == "1"

/**
 * Los 14 sueños de `Capturas.suenosEjemplo` (uno cada dos días, desde ayer) sobre [dias]: el texto y la valencia del sueño,
 * que dan el ancho y el color de cada volumen en el estante de los Sueños, como en la captura `temaSuenos`.
 */
internal fun suenosEjemplo(dias: Map<String, Dia>): Map<String, Dia> {
    val textos = listOf(
        "Estaba en una estación de tren que también era la casa de mis abuelos. @ana me decía que el tren salía por la ventana y no me sorprendía.",
        "Volaba bajito sobre un mar verde. Sabía que era un sueño y traté de subir más, pero el cielo era de tela.",
        "Llegaba tarde a un examen de una materia que nunca había cursado. Las preguntas estaban en un idioma inventado. #escuela",
        "Una ciudad de noche llena de faroles; caminaba con @luis buscando una librería que cambiaba de calle.",
        "Un perro enorme me acompañaba por el bosque y me enseñaba un camino de piedras blancas.",
    )
    val d = dias.toMutableMap()
    (0 until 14).forEach { k ->
        val f = Fechas.sumar(hoy(), -(k * 2L + 1))
        val base = d[f] ?: Dia(fecha = f)
        d[f] = base.copy(sueno_recordado = true, sueno_texto = textos[k % textos.size], sueno_valencia = 2 + (k * 3) % 6)
    }
    return d
}

/**
 * La captura `temaBibliotecas`, solo el estante: lo que arma `BibliotecaDiario` y `EstantePorMeses` (ui/pantallas/Bibliotecas.kt)
 * sin búsqueda: las entradas por mes, de la más reciente a la más vieja (hasta 6 meses), cada mes con su rótulo
 * (`Estilo.mesConComun`). El margen de 16 dp a los lados es el de la pantalla.
 */
@Composable
private fun Biblioteca() {
    // ARNES_SUENOS=1: el estante de los Sueños (lo que la app llama «noche»: la luna en lugar del sello, la luz propia del tema),
    // con los 14 sueños de la captura `temaSuenos`: solo los días con sueño escrito, cada uno con el color de su valencia.
    Repo.dias = if (conSuenos) suenosEjemplo(diasEjemplo()) else diasEjemplo()
    val todas = Repo.dias.values.filter { if (conSuenos) it.sueno_texto.isNotBlank() else it.nota.isNotBlank() }.sortedByDescending { it.fecha }
    val porMes = todas.groupBy { it.fecha.take(7) }.toSortedMap(compareByDescending { it })
    val grupos = porMes.entries.take(6).map { (mes, lista) -> GrupoEstante(Estilo.mesConComun(mes.drop(5).toInt(), mes.take(4).toInt()), lista) }
    Box(Modifier.padding(horizontal = 16.dp).padding(top = 18.dp, bottom = 16.dp)) {
        EstanteTema(disenoBiblioteca(Estilo.tema), grupos, conSuenos, {})
    }
}
