package io.github.ruddy2003.vestigium.ui.seguir

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Seguir
import io.github.ruddy2003.vestigium.ui.Ayuda
import io.github.ruddy2003.vestigium.ui.BotonSecundario
import io.github.ruddy2003.vestigium.ui.Etiqueta
import io.github.ruddy2003.vestigium.ui.Hoja
import io.github.ruddy2003.vestigium.ui.Nav
import io.github.ruddy2003.vestigium.ui.Seccion
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.escenas.Banderita
import io.github.ruddy2003.vestigium.ui.escenas.banderitaHistoria
import io.github.ruddy2003.vestigium.ui.pantallas.MedallaVida
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

// =====================================================================
//  Seguir a un personaje (parte 8, encargo A; `docs/diseno/CUADROS_PLAN.md`)
// =====================================================================
//
// Tres piezas, ya llamadas desde su sitio: el botón debajo del retrato del cuento (`PantallaCuento`), las banderitas de
// Hoy (`Dia.kt`) y la pantalla de los que sigues (ruta `seguidos`). Lo que muestran sale de `SeguirDatos` (puro y
// probado); lo que se guarda, siempre por `Repo.cambiarConfig { c -> Seguir.seguir(c, vida, Fechas.diaLogico()) }`.
// Cada pieza tiene su vista sin estado (`Vista…`), que es la que dibujan las capturas (`CapturasSeguir`).

// ---------------------------------------------------------------- el botón

/**
 * El botón para seguir o dejar de seguir a [vida]. Va debajo del retrato, en la pantalla del cuento (`PantallaCuento`).
 * Con el tope lleno ([Seguir.TOPE]) dice por qué no se puede y lleva a la lista para dejar a uno. Una vida sin calendario
 * (sin días especiales) no lleva botón.
 */
@Composable
fun BotonSeguir(vida: OtrasVidas.Vida, modifier: Modifier = Modifier) {
    if (io.github.ruddy2003.vestigium.vidas.anios.Calendarios.de(vida.id) == null) return
    val ctx = LocalContext.current
    val estado = SeguirDatos.estado(Repo.config, vida.id)
    VistaBotonSeguir(vida, estado, modifier,
        alSeguir = { Repo.cambiarConfig { c -> Seguir.seguir(c, vida.id, Fechas.diaLogico()) } },
        alDejar = { Repo.cambiarConfig { c -> Seguir.dejar(c, vida.id, Fechas.diaLogico()) } },
        // El botón no recibe la navegación (su firma es la del enganche): pide la pantalla a la actividad, que la apila
        // encima del cuento como lo que llega desde el ⊕ de dentro de la app (`MainActivity.APILAR`).
        alLleno = { abrirEnApp(ctx, "seguidos") })
}

/** Pide a la actividad principal (que ya está abierta: `singleTop`) que apile [ruta] sobre lo que se ve. */
private fun abrirEnApp(ctx: android.content.Context, ruta: String) {
    runCatching {
        ctx.startActivity(android.content.Intent(ctx, io.github.ruddy2003.vestigium.ui.MainActivity::class.java)
            .putExtra(io.github.ruddy2003.vestigium.ui.MainActivity.PANTALLA, ruta)
            .putExtra(io.github.ruddy2003.vestigium.ui.MainActivity.APILAR, true)
            .addFlags(android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP))
    }
}

/** El botón, sin estado: lo que dice según [estado]. */
@Composable
internal fun VistaBotonSeguir(vida: OtrasVidas.Vida, estado: SeguirDatos.Estado, modifier: Modifier = Modifier,
                              alSeguir: () -> Unit, alDejar: () -> Unit, alLleno: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    when (estado) {
        SeguirDatos.Estado.SEGUIR -> Column(modifier.fillMaxWidth()) {
            BotonSecundario("Seguir a ${vida.nombre}", Modifier.fillMaxWidth(), onClick = alSeguir)
            Ayuda("Te aviso los días especiales de su historia y los guardo para leerlos después.", Modifier.padding(top = 4.dp))
        }
        // «Sigues a …» y no «Lo sigues»: así no hace falta saber si es él o ella.
        SeguirDatos.Estado.SIGUES -> Row(modifier.fillMaxWidth().heightIn(min = 48.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("✓ Sigues a ${vida.nombre}", style = MaterialTheme.typography.labelLarge, color = cs.primary, modifier = Modifier.weight(1f))
            TextButton(onClick = alDejar) { Text("dejar de seguir", style = MaterialTheme.typography.labelLarge, color = cs.onSurfaceVariant) }
        }
        SeguirDatos.Estado.LLENO -> BotonSecundario("Sigues a ${Seguir.TOPE}: deja a uno para seguir a otro", modifier.fillMaxWidth(), onClick = alLleno)
    }
}

// ---------------------------------------------------------------- las banderitas de Hoy

/**
 * En Hoy, debajo del cuadro (o del gato de pixel): la banderita de cada personaje que sigues y hoy, día lógico [hoy], tiene
 * un día especial (`DiasEspeciales.conEspecialHoy`). Tocarla abre su cuento (`cuento?vida=<id>`). Sin ninguno, no ocupa sitio.
 */
@Composable
fun AvisosSeguidos(hoy: String, nav: Nav, modifier: Modifier = Modifier) {
    val c = Repo.config
    // Seis fichas como mucho (el tope): se calculan al cambiar a quién sigues, lo leído o el día, no en cada repintado.
    val marcas = remember(c.seguidos, c.cuentos_leidos, hoy) { SeguirDatos.marcasDeHoy(c, hoy) }
    VistaAvisosSeguidos(marcas, modifier, alAbrir = { nav.ir("cuento?vida=$it") }, alVerTodos = { nav.ir("seguidos") })
}

/** La fila de banderitas, sin estado. Vacía, no dibuja nada. */
@Composable
internal fun VistaAvisosSeguidos(marcas: List<SeguirDatos.Marca>, modifier: Modifier = Modifier, alAbrir: (String) -> Unit, alVerTodos: () -> Unit) {
    if (marcas.isEmpty()) return
    val cs = MaterialTheme.colorScheme
    Row(modifier.fillMaxWidth().padding(top = 10.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically) {
        marcas.forEach { m -> MarcaSeguido(m) { alAbrir(m.vida) } }
        Text("ver todos ›", style = MaterialTheme.typography.labelLarge, color = cs.primary,
            modifier = Modifier.heightIn(min = 48.dp).clickable(onClick = alVerTodos).padding(horizontal = 6.dp, vertical = 14.dp))
    }
}

/** Una banderita con su nombre y el título de su día especial. Ya leído hoy, se ve apagada. */
@Composable
private fun MarcaSeguido(m: SeguirDatos.Marca, alTocar: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val tema = remember(m.tema) { m.tema?.let { id -> TemaId.entries.firstOrNull { it.id == id } } }
    val bandera = remember(tema) { tema?.let { banderitaHistoria(it) } }
    val descripcion = "Hoy ${m.nombre}: ${m.titulo}" + if (m.leido) ". Ya lo leíste" else ""
    Row(Modifier.width(184.dp).heightIn(min = 48.dp).alpha(if (m.leido) 0.45f else 1f)
        .clickable(onClick = alTocar)
        .clearAndSetSemantics { contentDescription = descripcion; role = Role.Button; onClick { alTocar(); true } }
        .padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Canvas(Modifier.size(Banderita.ANCHO.dp, Banderita.ALTO.dp)) {
            if (bandera != null) with(bandera) { dibujar(1f) }
            else distintivoComun(cs.primary, cs.onPrimary, cs.background)
        }
        Column(Modifier.padding(start = 8.dp)) {
            Text(m.nombre, style = MaterialTheme.typography.labelLarge, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(m.titulo, style = MaterialTheme.typography.bodySmall, color = cs.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

/**
 * El distintivo de los personajes sin banderita propia (las doce vidas comunes y los temas que todavía no tienen cuadro):
 * un banderín de cola de golondrina con los colores del tema puesto y la estrella de la marca de día especial del cuadro.
 */
private fun DrawScope.distintivoComun(fondo: androidx.compose.ui.graphics.Color, tinta: androidx.compose.ui.graphics.Color, borde: androidx.compose.ui.graphics.Color) {
    val w = size.width; val h = size.height
    val tela = Path().apply {
        moveTo(w * 0.1f, 0f); lineTo(w * 0.9f, 0f); lineTo(w * 0.9f, h * 0.97f); lineTo(w * 0.5f, h * 0.76f); lineTo(w * 0.1f, h * 0.97f); close()
    }
    drawPath(tela, fondo)
    drawPath(tela, borde.copy(alpha = 0.7f), style = Stroke(1.2.dp.toPx()))
    val c = Offset(w * 0.5f, h * 0.36f)
    val r = w * 0.3f
    val estrella = Path().apply {
        for (i in 0 until 16) {
            val a = (-PI / 2 + i * PI / 8).toFloat()
            val d = if (i % 2 == 0) r else r * 0.42f
            val p = Offset(c.x + d * cos(a), c.y + d * sin(a))
            if (i == 0) moveTo(p.x, p.y) else lineTo(p.x, p.y)
        }
        close()
    }
    drawPath(estrella, tinta)
}

// ---------------------------------------------------------------- la pantalla

/** La pantalla de los personajes que sigues (ruta `seguidos`): a quién, y sus días especiales pasados para leerlos después. */
@Composable
fun PantallaSeguidos(nav: Nav) = VistaSeguidos(nav, Fechas.diaLogico())

/**
 * La pantalla, con el día lógico [hoy] dado (las capturas fijan uno). Arriba los que sigues, con «dejar»; debajo, todas las
 * vidas con calendario, de todos los temas. Tocar a uno despliega sus días especiales pasados ([abiertoAlEmpezar], el que
 * empieza desplegado).
 */
@Composable
internal fun VistaSeguidos(nav: Nav, hoy: String, abiertoAlEmpezar: String? = null) {
    val c = Repo.config
    var abierto by rememberSaveable { mutableStateOf(abiertoAlEmpezar) }
    val activos = Seguir.activos(c)
    val todas = remember { SeguirDatos.seguibles() }
    val lleno = activos.size >= Seguir.TOPE
    fun alternar(id: String) { abierto = if (abierto == id) null else id }
    Hoja("Personajes que sigues", nav.atras,
        subtitulo = "Cuando la historia de alguien que sigues tiene un día especial, sale su banderita en Hoy y te aviso. Aquí están también sus días especiales pasados, para leerlos cuando quieras.") {
        Seccion("A quién sigues", ayuda = "${activos.size} de ${Seguir.TOPE}") {
            if (activos.isEmpty()) Ayuda("Todavía no sigues a nadie. Elige abajo, o desde el cuento de un personaje.")
            activos.mapNotNull { OtrasVidas.porId(it) }.forEach { v ->
                FilaVida(v, abierta = abierto == v.id, alTocar = { alternar(v.id) }) {
                    TextButton(onClick = {
                        Repo.cambiarConfig { cc -> Seguir.dejar(cc, v.id, Fechas.diaLogico()) }
                        if (abierto == v.id) abierto = null
                    }) { Text("dejar", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                }
                if (abierto == v.id) DiasPasados(nav, v, hoy)
            }
        }
        Seccion("Todas las vidas", ayuda = if (lleno) "Ya sigues a ${Seguir.TOPE}: deja a uno para seguir a otro." else "De todos los temas: seguir a alguien no depende del tema que tengas puesto.") {
            todas.filter { it.id !in activos }.forEach { v ->
                FilaVida(v, abierta = abierto == v.id, alTocar = { alternar(v.id) }) {
                    TextButton(enabled = !lleno, onClick = { Repo.cambiarConfig { cc -> Seguir.seguir(cc, v.id, Fechas.diaLogico()) } }) {
                        Text("seguir", style = MaterialTheme.typography.labelLarge)
                    }
                }
                if (abierto == v.id) DiasPasados(nav, v, hoy)
            }
        }
    }
}

/** Una vida en la lista: su medalla, su nombre, quién es y de qué tema; a la derecha, [accion]. Tocarla despliega sus días pasados. */
@Composable
private fun FilaVida(v: OtrasVidas.Vida, abierta: Boolean, alTocar: () -> Unit, accion: @Composable () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val tema = remember(v.tema) { v.tema?.let { id -> TemaId.entries.firstOrNull { it.id == id } } }
    Row(Modifier.fillMaxWidth().heightIn(min = 56.dp).clickable(onClickLabel = if (abierta) "ocultar sus días especiales" else "ver sus días especiales", onClick = alTocar)
        .padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        MedallaVida(v, 44.dp, elegida = abierta)
        Column(Modifier.padding(start = 12.dp).weight(1f)) {
            Text(v.nombre, style = MaterialTheme.typography.titleSmall)
            Text("${v.rol} · ${v.lugar} · ${v.cuando}", style = MaterialTheme.typography.labelMedium, color = cs.onSurfaceVariant, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (tema != null) Text("tema ${tema.nombre}", style = MaterialTheme.typography.labelSmall, color = cs.secondary)
        }
        accion()
    }
}

/** Los días especiales pasados de [v] hasta [hoy], por año; tocar uno abre su cuento de ese día. Los futuros no salen nunca. */
@Composable
private fun DiasPasados(nav: Nav, v: OtrasVidas.Vida, hoy: String) {
    val c = Repo.config
    val cs = MaterialTheme.colorScheme
    val anios = remember(v.id, hoy, c.cuentos_leidos) { SeguirDatos.pasadosPorAnio(c, v.id, hoy) }
    Surface(Modifier.fillMaxWidth().padding(start = 8.dp, bottom = 10.dp), shape = MaterialTheme.shapes.medium, color = cs.surface,
        border = BorderStroke(1.dp, cs.primary.copy(alpha = 0.4f))) {
        Column(Modifier.padding(horizontal = 12.dp, vertical = 6.dp)) {
            if (anios.isEmpty()) {
                Ayuda("Todavía no tiene días especiales pasados (se cuentan desde el ${Fechas.larga(io.github.ruddy2003.vestigium.vidas.DiasEspeciales.PRIMER_DIA)}).", Modifier.padding(vertical = 8.dp))
                return@Column
            }
            anios.forEach { (anio, dias) ->
                Etiqueta("$anio")
                dias.forEach { p ->
                    val e = p.especial
                    val dura = if (e.dia.duracion > 1) " · ${e.dia.duracion} días" else ""
                    Row(Modifier.fillMaxWidth().heightIn(min = 48.dp).clickable(onClickLabel = "leer su cuento de ese día") { nav.ir(SeguirDatos.rutaCuento(v.id, e.diaDelCuento)) }
                        .padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(e.titulo, style = MaterialTheme.typography.bodyMedium)
                            Text("${Fechas.larga(e.diaDelCuento)}$dura · ${e.dia.tipo.nombre} · ${e.dia.certeza.nombre}",
                                style = MaterialTheme.typography.labelSmall, color = cs.onSurfaceVariant)
                        }
                        Text(if (p.leido) "✓ leído" else "leer ›", style = MaterialTheme.typography.labelMedium,
                            color = if (p.leido) cs.onSurfaceVariant else cs.primary, modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }
    }
}
