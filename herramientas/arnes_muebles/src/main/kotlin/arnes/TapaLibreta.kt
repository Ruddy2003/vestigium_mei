// Los modos `tapa` y `libreta` del arnés (19 nov 2026): el libro del día y la libreta de momentos con su índice, montados
// como en la app y con datos de ejemplo inventados.
//
//   dibujar.sh tapa <tema> <claro|oscuro|hoja> <salida.png>
//   dibujar.sh libreta <tema> <claro|oscuro|hoja> <salida.png>
//
// La tapa se dibuja con el composable real `TapaDiario` (ui/Libros.kt, sacado tal cual con `piezas`): el objeto del tema
// (`DisenoBiblioteca.tapa`) y, encima, el extracto o la pista en `cajaTapa`, con la letra, el tamaño, los renglones, el
// centrado y la tinta del tema. La libreta, con el composable real `LibretaCerrada` (ui/pantallas/LibretaMomentos.kt, también
// sacado tal cual) y el índice armado como en `BibliotecaMomentosLibreta` (mismas medidas: rótulo de 34 dp con 18 dp arriba y
// 8 abajo; pestañas de 44×56 dp en un FlowRow con 6 dp entre columnas y 8 entre filas). Ver LEEME.md.
@file:OptIn(ExperimentalLayoutApi::class)

package arnes

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Catalogo
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Momento
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.TapaDiario
import io.github.ruddy2003.vestigium.ui.Tonos
import io.github.ruddy2003.vestigium.ui.biblioteca.CampoTema
import io.github.ruddy2003.vestigium.ui.biblioteca.EstadoLibreta
import io.github.ruddy2003.vestigium.ui.biblioteca.EstadoPestana
import io.github.ruddy2003.vestigium.ui.biblioteca.disenoBiblioteca
import io.github.ruddy2003.vestigium.ui.contenedorDe
import io.github.ruddy2003.vestigium.ui.esOscuro
import io.github.ruddy2003.vestigium.ui.pantallas.LibretaCerrada
import io.github.ruddy2003.vestigium.ui.pantallas.pluralHoja

// ---------------------------------------------------------------- variables comunes

/** ARNES_ABIERTO=0..1: cuánto se ha abierto el objeto (la animación al tocarlo). Sin ella, cada estado trae el suyo (0, cerrado). */
internal fun abiertoArnes(): Float? = System.getenv("ARNES_ABIERTO")?.trim()?.takeIf { it.isNotEmpty() }?.let {
    it.toFloatOrNull()?.takeIf { v -> v.isFinite() && v in 0f..1f } ?: fallo("ARNES_ABIERTO debe ser un número de 0 a 1.")
}

/** ARNES_LUGAR=diario|suelto|biblioteca: dónde se monta el objeto (ver [estadosTapa] y [LibretaArnes]). Vacío: como en la app. */
internal fun lugarArnes(): String? = System.getenv("ARNES_LUGAR")?.trim()?.lowercase()?.takeIf { it.isNotEmpty() }?.also {
    if (it !in setOf("diario", "suelto", "biblioteca")) fallo("ARNES_LUGAR debe ser «diario», «suelto» o «biblioteca».")
}

/** Un valor de una variable que no sirve: lo dice y termina, sin la pila de llamadas. */
internal fun fallo(mensaje: String): Nothing {
    System.err.println(mensaje)
    kotlin.system.exitProcess(2)
}

/** Un rótulo chico encima de cada estado (no es de la app: dice qué estado es y dónde va montado). */
@Composable
private fun RotuloEstado(texto: String) = Text(texto, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
    modifier = Modifier.padding(bottom = 6.dp))

// ---------------------------------------------------------------- la tapa: el libro del día

/** Lo que `TapaDiario` pone en la tapa como extracto: lo escrito en un renglón, hasta 160 letras (vacío: la pista del tema). */
internal fun vistaTapa(texto: String): String = texto.trim().replace(Regex("\\s+"), " ").take(160)

/** El texto de la captura de CI (`Capturas.TEXTO`): unas 52 palabras. */
private const val TEXTO_CORTO = "Hoy fue un día largo pero bueno. En la mañana salí a caminar con @ana por el parque y hablamos de los planes para el viaje. " +
    "Después trabajé casi toda la tarde en el proyecto; avancé más de lo que esperaba. En la noche leí un rato antes de dormir. #lectura #trabajo"

/** El sueño de la captura de CI. */
private const val SUENO = "Volaba sobre un río y el agua subía hasta el puente."

/** Una entrada larguísima, inventada: unas 4 800 palabras (la cuenta de palabras con cuatro cifras y el extracto lleno). */
private val TEXTO_LARGO: String by lazy {
    val frases = listOf(
        "Empecé el día temprano y con calma, sin mirar el teléfono durante la primera hora, y preparé café mientras amanecía.",
        "Ordené el escritorio, repasé la lista de pendientes de la semana y taché dos cosas que llevaban días esperando.",
        "En la tarde trabajé en el capítulo tres y reescribí el mismo párrafo varias veces hasta que por fin sonó como quería.",
        "Salí a caminar cuando bajó el sol; el aire olía a lluvia, las calles estaban casi vacías y se oían las campanas a lo lejos.",
        "Pensé en lo que quiero cambiar el próximo mes y en lo que ya funciona y conviene dejar como está, sin prisa y sin culpa.",
        "Cené algo sencillo, leí un rato en el sillón y anoté tres cosas que salieron mejor de lo que esperaba esta mañana.",
    )
    buildString { var k = 0; while (Texto.palabras(toString()) < 4800) { append(frases[k % frases.size]).append(' '); k++ } }.trim()
}

/** Un estado de la tapa: qué texto lleva, si es de noche (el sueño) y cuánto se ha abierto. */
internal class EstadoTapaArnes(val clave: String, val nombre: String, val texto: String, val noche: Boolean, val abierto: Float)

private fun estadoTapa(clave: String): EstadoTapaArnes = when (clave) {
    "vacio" -> EstadoTapaArnes(clave, "vacío (la pista del tema)", "", false, 0f)
    "corto" -> EstadoTapaArnes(clave, "escrito: " + Texto.palabras(TEXTO_CORTO) + " palabras", TEXTO_CORTO, false, 0f)
    "largo" -> EstadoTapaArnes(clave, "larguísimo: " + Texto.palabras(TEXTO_LARGO) + " palabras", TEXTO_LARGO, false, 0f)
    "noche" -> EstadoTapaArnes(clave, "de noche: el sueño escrito", SUENO, true, 0f)
    "noche_vacio" -> EstadoTapaArnes(clave, "de noche, vacío (la pista del sueño)", "", true, 0f)
    "abierto" -> EstadoTapaArnes(clave, "escrito, abriéndose (0.55)", TEXTO_CORTO, false, 0.55f)
    else -> fallo("ARNES_TAPA: no hay estado «$clave»; hay vacio, corto, largo, noche, noche_vacio y abierto.")
}

/** ARNES_TAPA=vacio,corto,largo,noche,noche_vacio,abierto: los estados, uno bajo otro. Por omisión, vacio,corto,largo,noche. */
internal fun estadosTapa(porOmision: List<String> = listOf("vacio", "corto", "largo", "noche")): List<EstadoTapaArnes> =
    (System.getenv("ARNES_TAPA")?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }?.takeIf { it.isNotEmpty() } ?: porOmision).map(::estadoTapa)

/**
 * Si un estado va en la escena de la pestaña Diario: de día, sí (`PantallaDiario`, sección «Hoy»: `Escena` con 12 dp de
 * relleno a los lados, 14 arriba y abajo, y la lámpara de los modos oscuros); de noche, suelto, a todo el ancho de la
 * pantalla (la sección «Anoche» de la Biblioteca de sueños y el formulario del sueño), como también el diario de `PantallaDia`.
 */
internal fun enEscena(e: EstadoTapaArnes): Boolean = when (lugarArnes()) {
    "diario" -> true
    "suelto", "biblioteca" -> false
    else -> !e.noche
}

/** Los estados de la tapa del tema, uno bajo otro, cada uno montado como en la app y con su rótulo encima. */
@Composable
internal fun TapasArnes() {
    val fecha = hoy()
    val forzado = abiertoArnes()
    Column(Modifier.padding(horizontal = 16.dp).padding(top = 14.dp, bottom = 18.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
        estadosTapa().forEach { e ->
            val escena = enEscena(e)
            val abierto = forzado ?: e.abierto
            Column {
                RotuloEstado(e.nombre + if (escena) " · en la pestaña Diario (en su escena)" else " · suelto, a todo el ancho")
                if (escena) EscenaArnes(Modifier.fillMaxWidth(), relleno = PaddingValues(horizontal = 12.dp, vertical = 14.dp), luz = true, llena = false) {
                    TapaDiario(fecha, e.texto, noche = e.noche, abierto = { abierto })
                } else TapaDiario(fecha, e.texto, noche = e.noche, abierto = { abierto })
            }
        }
    }
}

// ---------------------------------------------------------------- la libreta de momentos

/** Un momento de ánimo inventado, a las [hora] de [f]. */
private fun animo(id: String, f: String, hora: Int, valencia: Int?, tipo: String = "animo") =
    Momento(id, "${f}T%02d:00:00-06:00".format(hora), f, tipo, valencia = valencia)

/**
 * Los registros de la captura `libreta` de CI (`Ejemplo.cargar`, `ejemploV67` y `ejemploV73` de Capturas.kt): hoy y ayer,
 * con los mismos tipos y ánimos (sin los del seguimiento privado, que no cambian el índice).
 */
private fun pocos(): List<Momento> {
    val hoy = hoy(); val ayer = Fechas.sumar(hoy, -1)
    return listOf(
        animo("m1", hoy, 11, 5, "muestreo"), animo("m2", hoy, 16, 3, "preocupacion"), animo("m3", hoy, 13, 6, "muestreo"),
        animo("m4", hoy, 19, null, "alegria"), animo("a1", hoy, 10, 5), animo("a2", hoy, 17, 2), animo("a4", hoy, 12, 5),
        animo("a3", ayer, 12, 6),
    )
}

/**
 * [n] días hacia atrás desde hoy; con [huecos], faltan algunos (como en la vida). Cada día tiene de uno a tres registros con
 * ánimo, salvo uno de cada seis, que tiene un registro sin ánimo (su pestaña sale sin color).
 */
private fun dias(n: Int, huecos: Boolean): List<Momento> {
    val hoy = hoy()
    return (0 until n).flatMap { k ->
        if (huecos && (k % 9 == 4 || k % 13 == 7)) return@flatMap emptyList()
        val f = Fechas.sumar(hoy, -k.toLong())
        if (k % 6 == 2) listOf(animo("x$k", f, 12, null))
        else (0..(k % 3)).map { j -> animo("x${k}_$j", f, 9 + 4 * j, 1 + (k * 5 + j * 3) % 7) }
    }
}

/** Un conjunto de momentos de ejemplo: `0`, `pocos` (los de la captura de CI), `muchos` (150 días con huecos) o un número de días seguidos. */
internal fun momentosEjemplo(clave: String): List<Momento> = when (clave) {
    "pocos" -> pocos()
    "muchos" -> dias(150, true)
    else -> clave.toIntOrNull()?.takeIf { it in 0..3000 }?.let { dias(it, false) }
        ?: fallo("ARNES_MOMENTOS: no hay «$clave»; usa 0, pocos, muchos o un número de días (hasta 3000).")
}

/** ARNES_MOMENTOS=0,pocos,muchos,40: los conjuntos, uno bajo otro. Por omisión, pocos,muchos. */
internal fun conjuntosMomentos(porOmision: List<String> = listOf("pocos", "muchos")): List<String> =
    System.getenv("ARNES_MOMENTOS")?.split(',')?.map { it.trim() }?.filter { it.isNotEmpty() }?.takeIf { it.isNotEmpty() } ?: porOmision

/** ARNES_MESES=N: cuántos meses del índice se ven (la pantalla enseña 3 y «ver más meses»). */
internal fun mesesArnes(): Int = System.getenv("ARNES_MESES")?.trim()?.toIntOrNull()?.coerceIn(1, 60) ?: 3

/** Los días con algo registrado, del más reciente al más antiguo (como `diasConMomentos`, sin consumos). */
internal fun diasDe(momentos: List<Momento>): List<String> = momentos.map { it.fecha }.distinct().sortedDescending()

/** La cuenta que `LibretaCerrada` pone en la libreta («12 tablillas · desde el mié 18 nov»), con la misma fórmula. */
internal fun cuentaLibreta(hojas: Int, desde: String?): String {
    val c = contenedorDe(Estilo.tema)
    return "$hojas ${if (hojas == 1) c.hoja else pluralHoja(c.hoja)}" + (desde?.let { " · desde el ${Fechas.corto(it)}" } ?: "")
}

/** Lo que el índice manda a cada pestaña (lo mismo que `BibliotecaMomentosLibreta`): el día, la letra de la semana y el ánimo medio. */
internal fun estadoPestana(f: String, momentos: List<Momento>, oscuro: Boolean): EstadoPestana {
    val v = momentos.filter { it.fecha == f && Catalogo.esRegistro(it) }.mapNotNull { it.valencia }.takeIf { it.isNotEmpty() }?.average()
    val dia = f.takeLast(2).trimStart('0')
    val semana = Fechas.parse(f).dayOfWeek.getDisplayName(java.time.format.TextStyle.NARROW, java.util.Locale("es"))
    return EstadoPestana(dia, semana, Tonos.valencia(v, oscuro), f.hashCode())
}

/** Los meses del índice, del más reciente al más viejo, con sus días en orden (lo que recorre `BibliotecaMomentosLibreta`). */
internal fun mesesIndice(todas: List<String>): List<Pair<String, List<String>>> =
    todas.groupBy { it.take(7) }.toSortedMap(compareByDescending { it }).map { (mes, dias) -> mes to dias.sorted() }

/** El rótulo de un mes («ΔΙΟΣ · noviembre de 2026»), como lo pone el índice. */
internal fun rotuloDe(mes: String): String = Estilo.mesConComun(mes.drop(5).toInt(), mes.take(4).toInt())

/**
 * La libreta cerrada: la de la app (`LibretaCerrada`) o, con ARNES_ABIERTO, la misma llamada a `libreta()` con ese
 * `abierto` fijo (en la app lo anima un `Animatable` interno que no se puede fijar desde fuera).
 */
@Composable
private fun LibretaDelArnes(hojas: Int, desde: String?) {
    val a = abiertoArnes()
    if (a == null) { LibretaCerrada(hojas, desde, Modifier.fillMaxWidth()) {}; return }
    val d = disenoBiblioteca(Estilo.tema)
    val oscuro = esOscuro()
    val medidor = rememberTextMeasurer()
    val nombre = contenedorDe(Estilo.tema).nombre
    Canvas(Modifier.fillMaxWidth().height(d.altoLibreta)) {
        clipRect { with(d) { libreta(EstadoLibreta(nombre, cuentaLibreta(hojas, desde), hojas, a, hojas * 31 + 7), medidor, oscuro) } }
    }
}

/** El índice como en `BibliotecaMomentosLibreta`: cada mes con su rótulo y sus días como pestañas, y «ver más meses». */
@Composable
private fun IndiceArnes(momentos: List<Momento>, todas: List<String>) {
    val oscuro = esOscuro()
    val d = disenoBiblioteca(Estilo.tema)
    val medidor = rememberTextMeasurer()
    val meses = mesesIndice(todas)
    val cuantos = mesesArnes()
    meses.take(cuantos).forEach { (mes, dias) ->
        val rotulo = rotuloDe(mes)
        Canvas(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 8.dp).height(34.dp)) {
            clipRect { with(d) { rotuloMes(rotulo, medidor, oscuro) } }
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            dias.forEach { f ->
                val e = estadoPestana(f, momentos, oscuro)
                Canvas(Modifier.size(44.dp, 56.dp)) { with(d) { pestana(e, medidor, oscuro) } }
            }
        }
    }
    // En la app es un `BotonTexto` del tema (ui/comun/Piezas.kt, que el arnés no compila): aquí, solo su texto.
    if (meses.size > cuantos) Text("ver más meses", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp))
}

/** En lugar de `Vacio` (ui/Componentes.kt, que el arnés no compila): su texto, centrado. */
@Composable
private fun VacioArnes(texto: String) = Text(texto, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
    textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(18.dp))

/**
 * La pestaña Momentos de la biblioteca (`BibliotecaMomentosLibreta`), sin las pestañas de las bibliotecas de arriba: la
 * libreta con 16 dp arriba, la ayuda, el buscador (`CampoTema`, el de la app) y el índice. Sin momentos, la pantalla solo
 * pone el aviso de vacío; debajo, aparte, la libreta con 0 hojas como prueba (la app no la dibuja así).
 */
@Composable
private fun PantallaLibretaArnes(momentos: List<Momento>) {
    val todas = diasDe(momentos)
    if (todas.isEmpty()) {
        VacioArnes("La libreta está en blanco. Toca «¿Cómo estás ahora?» en la pestaña Diario y aquí se irá llenando, una hoja por día.")
        RotuloEstado("prueba: la libreta con 0 hojas (la app no la dibuja: pone el aviso de arriba)")
        LibretaDelArnes(0, null)
        return
    }
    Box(Modifier.fillMaxWidth().padding(top = 16.dp), contentAlignment = Alignment.Center) { LibretaDelArnes(todas.size, todas.lastOrNull()) }
    Text("Toca la libreta para abrirla en la última hoja; adentro se pasa de día deslizando.", style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 10.dp))
    CampoTema("", {}, "buscar en tus momentos", Modifier.padding(top = 14.dp))
    IndiceArnes(momentos, todas)
}

/** La libreta en la pestaña Diario (`PantallaDiario`, sección «Libreta de momentos»): en su escena, sin índice. */
@Composable
private fun DiarioLibretaArnes(momentos: List<Momento>) {
    val todas = diasDe(momentos)
    if (todas.isEmpty()) { VacioArnes("Se llena con «¿Cómo estás ahora?», una hoja por día."); return }
    EscenaArnes(Modifier.fillMaxWidth().padding(top = 4.dp), luz = false, llena = false) { LibretaDelArnes(todas.size, todas.lastOrNull()) }
}

private fun nombreConjunto(clave: String, momentos: List<Momento>): String {
    val dias = diasDe(momentos)
    val meses = dias.map { it.take(7) }.distinct().size
    val sinAnimo = dias.count { f -> momentos.none { it.fecha == f && Catalogo.esRegistro(it) && it.valencia != null } }
    if (dias.isEmpty()) return "sin momentos"
    return when (clave) {
        "pocos" -> "pocos: los de la captura de CI"
        "muchos" -> "muchos"
        else -> "$clave ${if (clave == "1") "día" else "días seguidos"}"
    } + " · ${dias.size} ${if (dias.size == 1) "día" else "días"} en $meses ${if (meses == 1) "mes" else "meses"}, $sinAnimo sin ánimo"
}

/** Los conjuntos de momentos (ARNES_MOMENTOS), uno bajo otro: como en la biblioteca (por omisión) o en la pestaña Diario (ARNES_LUGAR=diario). */
@Composable
internal fun LibretaArnes() {
    val diario = lugarArnes() == "diario"
    Column(Modifier.padding(horizontal = 16.dp).padding(top = 14.dp, bottom = 18.dp), verticalArrangement = Arrangement.spacedBy(28.dp)) {
        conjuntosMomentos().forEach { clave ->
            val momentos = momentosEjemplo(clave)
            Column {
                RotuloEstado(nombreConjunto(clave, momentos) + if (diario) " · en la pestaña Diario (en su escena)" else " · en la biblioteca, con su índice")
                if (diario) DiarioLibretaArnes(momentos) else PantallaLibretaArnes(momentos)
            }
        }
    }
}
