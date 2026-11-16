package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.charco
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.halo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasLab.sombraEnPlano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ANCHO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin
import kotlin.math.tan

/** Las luces encendidas de un momento, en metros (`Luz3`). **[Diseño propio]**: cuánto da cada una. */
internal object LucesLab {
    private val C = ColorLaboratorio

    /** Dónde están las velas de la noche larga, sobre la mesa (a lo largo, de lado). */
    val VELAS = listOf(1.1f to 0.62f, 1.3f to 0.74f, 0.95f to 0.8f)

    fun de(v: VistaLab): List<Luz3> = buildList {
        // Los paneles de la luz general: blancos, desde el techo, parejos. No tumban sombras (la luz es «pareja»).
        if (v.general > 0.01f) for ((x, z) in listOf(-1.2f to 6.1f, 1.45f to 6.1f, -1.2f to 4.2f, 1.45f to 4.2f)) add(Luz3(Fuente.PANEL, V3(x, SalaLab.TECHO - 0.06f, z), Color(0xFFF4F4EC), 0.06f * v.general, 2.0f, cono = 0.15f, tumba = false))
        // Las lámparas de cultivo: blancas de día, violetas de noche.
        for (i in 0 until EscenaLaboratorio.LAMPARAS) {
            val k = if (v.noche) v.cultivoNoche * 0.7f else v.cultivo[i] * 1.5f
            if (k <= 0.005f) continue
            val w = MesaLab.lampara(i) - V3(0f, 0.04f, 0f)
            add(Luz3(Fuente.CULTIVO, w, v.colorCultivo, k, 1.3f, cono = 0.92f))
            // El rebote del charco en el tablero: la luz que sube a la pared, al techo y a lo de alrededor (sin sombra).
            if (k > 0.05f) add(Luz3(Fuente.REBOTE, V3(w.x, MesaLab.ALTO + 0.12f, w.z), lerp(v.colorCultivo, ColorLaboratorio.TIERRA, 0.25f), 0.22f * k, 1.3f, tumba = false))
        }
        // La lámpara de su mesa: cálida, baja, con su pantalla.
        if (v.mesa > 0.01f) {
            add(Luz3(Fuente.MESA, MesaLab.LAMPARA_MESA, C.LAMPARA, 1.5f * v.mesa, 0.75f, cono = 0.5f))
            add(Luz3(Fuente.REBOTE, MesaLab.LAMPARA_MESA - V3(0f, 0.3f, 0f), C.LAMPARA, 0.35f * v.mesa, 1.0f, tumba = false))
        }
        // La pantalla de la Vigía: el color del cielo de arriba, que tiñe la pared, la campana y el suelo de su lado.
        if (v.pantalla > 0.01f) {
            val deslumbra = EscenaLaboratorio.deslumbra(v.m)
            add(Luz3(Fuente.PANTALLA, V3(SalaLab.XD - 0.5f, 2.0f, 3.45f), v.colorPantalla, (if (v.noche) 0.85f else 0.9f) * v.pantalla * (if (deslumbra) 1.5f else 1f), if (deslumbra) 3.0f else 1.9f, tumba = v.noche || deslumbra))
        }
        // La puerta: el violeta de los jardines de noche (o su blanco, la noche de la luz larga); de día, su claridad; con aviso, el rojo.
        val kp = when { v.roja -> 0.75f; v.noche -> 0.85f * max(v.violeta, if (v.jardinBlanco) 1f else 0f); else -> 0.22f * v.general }
        if (kp > 0.01f) add(Luz3(Fuente.PUERTA, V3(-0.22f, 1.15f, SalaLab.ZF - 0.2f), v.colorPuerta, kp, 1.5f, tumba = v.noche || v.roja))
        // La campana, de noche o en el experimento: el resplandor ámbar de dentro.
        if (v.campana > 0.01f) add(Luz3(Fuente.CAMPANA, V3(CampanaLab.CENTRO.x, 1.3f, CampanaLab.CENTRO.z), EscenaLaboratorio.colorDeLaCampana(v.m), 0.6f * v.campana, 1.3f, tumba = false))
        // Las velas de la noche larga y la linterna del apagón.
        if (v.velas) for ((a, b) in VELAS) add(Luz3(Fuente.VELA, MesaLab.MK.w(a, b, MesaLab.ALTO + 0.2f), C.VELA, 0.5f, 0.75f))
        if (v.linterna) add(Luz3(Fuente.LINTERNA, MesaLab.MK.w(0.9f, 0.3f, MesaLab.ALTO + 0.12f), Color(0xFFFFE6B4), 1.0f, 1.3f))
    }
}

/**
 * La luz del laboratorio, que es el cuadro (`composicion_laboratorio.md`, «La luz»). Sigue la lógica de la capilla,
 * Scriptorium y Renacimiento (`docs/diseno/ESCENAS.md`, sección 2 bis): la luz tiene forma y cae sobre una penumbra de
 * color; camina con la hora; se ve en el aire; enciende lo pintado; y de noche hay varias luces de color opuesto.
 *
 * - **De día**, la luz general es blanca y baja, con la sombra azul verdosa; lo que tiene forma son los charcos de las
 *   lámparas de cultivo sobre la mesa, con su haz en el aire húmedo y su polvo, que caminan con el horario del riego
 *   (`EscenaLaboratorio.cultivo`); la pantalla tiñe su lado de la sala con el color del cielo de arriba a esa hora.
 * - **De noche**, a tres luces: el violeta de los jardines por la puerta, con su mancha larga en el suelo; la lámpara de
 *   su mesa, cálida; y la pantalla, fría. La campana deja su mancha curva en el suelo con la luz que la cruza.
 */
internal object LuzLab {
    private val C = ColorLaboratorio

    /** El radio del charco que deja una luz con cono a [alto] metros. */
    private fun radio(f: Luz3, alto: Float): Float = if (f.cono <= 0f) alto else alto * tan(acos(f.cono.coerceIn(0.05f, 0.99f)))

    // =================================================================
    //  En las superficies grandes
    // =================================================================

    /**
     * Lo que la luz deja en las paredes y en el suelo antes de pintar las cosas: el halo de la pantalla en su pared, la
     * claridad de los paneles en el suelo, la mancha de la puerta y su reflejo en el pulido, el charco de las lámparas de
     * cultivo donde sale de la mesa, la mancha curva de la campana y el reflejo de sus brillos en el suelo.
     */
    fun DrawScope.enLasSuperficies(v: VistaLab) {
        val suelo = SalaLab.formaDelSuelo()
        // La pantalla tiñe su pared alrededor del marco, el techo de encima y el suelo de debajo.
        v.luces.firstOrNull { it.de == Fuente.PANTALLA }?.let { f ->
            clipPath(SalaLab.formaDerecha()) { halo(V3(SalaLab.XD - 0.01f, 2.0f, 3.5f), 4.6f, 3.6f, f.color, 0.5f * f.fuerza) }
            clipPath(suelo) { charco(V3(SalaLab.XD - 0.7f, 0f, 3.4f), 1.7f, f.color, 0.4f * f.fuerza) }
        }
        // El rebote de los charcos de las lámparas de cultivo del fondo en la pared, sobre el extremo de la mesa.
        clipPath(SalaLab.formaDelFondo()) {
            for (f in v.luces) if (f.de == Fuente.CULTIVO && f.fuerza > 0.05f) {
                val cerca = (1f - (SalaLab.ZF - f.w.z) / 3f).coerceIn(0f, 1f)
                if (cerca > 0.05f) halo(V3(f.w.x, 1.3f, SalaLab.ZF - 0.01f), 2.2f, 2.0f, f.color, 0.35f * f.fuerza * cerca)
            }
        }
        clipPath(suelo) {
            // La claridad de los paneles del techo, en el suelo.
            if (v.general > 0.01f) for ((x, z) in listOf(-1.2f to 5.9f, 1.45f to 5.9f, 1.45f to 3.6f)) charco(V3(x, 0f, z), 1.2f, C.LUZ_DIA, 0.12f * v.general, duro = 0.2f)
            // Las lámparas de cultivo, donde su luz sale del canto de la mesa y cae al suelo.
            for (f in v.luces) if (f.de == Fuente.CULTIVO && f.fuerza > 0.05f) charco(V3(f.w.x + 0.15f, 0f, f.w.z), radio(f, f.w.y) * 0.75f, f.color, 0.22f * f.fuerza, duro = 0.3f)
            puerta(v)
            caustica(v)
            reflejos(v)
        }
    }

    /**
     * La luz de la puerta en el suelo: una mancha larga que sale del umbral hacia quien mira y se abre y se apaga al
     * alejarse (la luz viene del fondo del pasillo, baja). Y su reflejo en el pulido, una columna de luz rota.
     */
    private fun DrawScope.puerta(v: VistaLab) {
        val f = v.luces.firstOrNull { it.de == Fuente.PUERTA } ?: return
        val k = f.fuerza
        val x0 = SalaLab.PX0; val x1 = SalaLab.PX1; val z = SalaLab.ZF
        val mancha = CamLab.forma(listOf(V3(x0, 0f, z), V3(x1, 0f, z), V3(x1 + 0.9f, 0f, z - 3.6f), V3(x0 - 0.6f, 0f, z - 3.6f)))
        val a = CamLab.p(0f, 0f, z); val b = CamLab.p(0f, 0f, z - 3.6f)
        drawPath(mancha, Brush.verticalGradient(0f to f.color.copy(alpha = (0.32f * k).coerceIn(0f, 1f)), 0.5f to f.color.copy(alpha = (0.12f * k).coerceIn(0f, 1f)), 1f to Color.Transparent, startY = a.y, endY = b.y), blendMode = BlendMode.Plus)
        // El reflejo de la puerta encendida: la columna de luz que baja del umbral, rota en trazos.
        val az = Azar(Semilla.de("laboratorio", "reflejo puerta"))
        val pa = CamLab.p(x0, 0f, z); val pb = CamLab.p(x1, 0f, z)
        var y = pa.y + 1f; val largo = 70f
        while (y < pa.y + largo) {
            val t = (y - pa.y) / largo
            val w = (pb.x - pa.x) * (0.45f + 0.3f * az.real()); val cx = (pa.x + pb.x) / 2f + (az.real() - 0.5f) * 6f * t
            drawLine(f.color.copy(alpha = (0.28f * k * (1f - t) * (0.5f + 0.5f * az.real())).coerceIn(0f, 1f)), Offset(cx - w / 2f, y), Offset(cx + w / 2f, y), 1.1f, StrokeCap.Round, blendMode = BlendMode.Plus)
            y += 1.6f + 2f * t
        }
    }

    /**
     * La mancha curva de luz que la campana deja en el suelo, del lado contrario a la luz que más la cruza: el vidrio
     * hace de lente. Camina con qué lámpara está encendida. **[Aproximación]**: una media luna, no la cáustica exacta.
     */
    private fun DrawScope.caustica(v: VistaLab) {
        val c = CampanaLab.CENTRO; var sx = 0f; var sz = 0f; var peso = 0f; var col = Color.Black
        for (f in v.luces) {
            if (f.de == Fuente.PANEL || f.de == Fuente.CAMPANA || f.de == Fuente.REBOTE || f.fuerza < 0.05f) continue
            val d = V3(f.w.x - c.x, 0f, f.w.z - c.z); val dist = d.largo().coerceAtLeast(0.3f)
            val k = f.fuerza / (1f + dist * dist / 4f); sx += d.x / dist * k; sz += d.z / dist * k; peso += k; col = lerp(col, f.color, (k / (peso)).coerceIn(0f, 1f))
        }
        if (peso < 0.05f) return
        val dir = V3(-sx, 0f, -sz).unidad()
        val ang = kotlin.math.atan2(dir.z, dir.x); val r = CampanaLab.R * 1.12f
        val fuera = (0..16).map { i -> val a = ang - 0.75f + 1.5f * i / 16f; CamLab.p(c.x + r * 1.35f * cos(a), 0f, c.z + r * 1.35f * sin(a)) }
        val dentro = (0..16).map { i -> val a = ang - 0.75f + 1.5f * i / 16f; val e = 1.08f + 0.08f * sin(Math.PI.toFloat() * i / 16f); CamLab.p(c.x + r * e * cos(a), 0f, c.z + r * e * sin(a)) }
        val forma = poligono(fuera + dentro.reversed())
        val k = (peso * 0.55f).coerceIn(0f, 0.9f)
        drawPath(forma, col.copy(alpha = 0.3f * k), blendMode = BlendMode.Plus)
        drawPath(forma, col.copy(alpha = 0.12f * k), style = Stroke(3f), blendMode = BlendMode.Plus)
    }

    /**
     * El suelo pulido devuelve lo que tiene encima (regla 4): bajo la campana, el reflejo de sus brillos y de su aro;
     * bajo la pantalla, su claridad tendida; de noche, el resplandor ámbar de dentro de la campana.
     */
    private fun DrawScope.reflejos(v: VistaLab) {
        val c = CampanaLab.CENTRO
        val base = PiezasLab.mitad(V3(c.x, 0f, c.z), CampanaLab.R + 0.06f, true).map { CamLab.p(it) }
        val izq = base.first().x; val der = base.last().x; val pie = base[base.size / 2].y
        // El aro de abajo, reflejado: una banda oscura y, bajo ella, la luz del vidrio rota en trazos.
        val az = Azar(Semilla.de("laboratorio", "reflejo campana"))
        var y = pie + 1f
        while (y < pie + 34f) { val t = (y - pie) / 34f
            for (j in 0..2) { val x = izq + (der - izq) * (0.15f + 0.3f * j + 0.06f * az.real())
                drawLine(lerp(C.VIDRIO, Color.White, 0.4f).copy(alpha = (0.14f * (1f - t) * (0.4f + 0.6f * az.real()) * (0.5f + 0.5f * v.general)).coerceIn(0f, 1f)), Offset(x - 3f, y), Offset(x + 3f, y), 0.9f, blendMode = BlendMode.Screen) }
            y += 1.5f + 1.5f * t }
        if (v.campana > 0.02f) charco(V3(c.x, 0f, c.z - 0.6f), 1.6f, EscenaLaboratorio.colorDeLaCampana(v.m), 0.25f * v.campana)
    }

    // =================================================================
    //  En la mesa
    // =================================================================

    /**
     * Los charcos de luz sobre el tablero, que se pintan antes de las cosas de encima: los de las lámparas de cultivo
     * (donde caen, la tierra gris se vuelve casi blanca, y el tablero sube) y el de la lámpara de su mesa, cálido. De
     * noche, el violeta bajo de las lámparas de cultivo al fondo de la mesa contra el ámbar de la suya.
     */
    fun DrawScope.enLaMesa(v: VistaLab) {
        clipPath(MesaLab.formaDelTablero()) {
            for (f in v.luces) {
                if (f.fuerza < 0.03f) continue
                when (f.de) {
                    Fuente.CULTIVO -> charco(V3(f.w.x, MesaLab.ALTO, f.w.z), radio(f, f.w.y - MesaLab.ALTO), f.color, 0.55f * f.fuerza, duro = 0.55f)
                    Fuente.MESA -> charco(V3(f.w.x, MesaLab.ALTO, f.w.z), radio(f, f.w.y - MesaLab.ALTO) * 1.2f, f.color, 0.75f * f.fuerza, duro = 0.45f)
                    Fuente.VELA -> charco(V3(f.w.x, MesaLab.ALTO, f.w.z), 0.45f, f.color, 0.6f * f.fuerza)
                    Fuente.LINTERNA -> charco(MesaLab.MK.w(1.5f, 0.35f, MesaLab.ALTO), 0.4f, f.color, 0.7f * f.fuerza)
                    Fuente.PANTALLA, Fuente.PUERTA, Fuente.CAMPANA, Fuente.AVISO, Fuente.PANEL, Fuente.REBOTE -> {}
                }
            }
        }
    }

    /**
     * Las sombras de las cosas de la mesa sobre el tablero: cada lámpara encendida tumba la de lo que tiene cerca, lejos de
     * ella (de día, cortas y hacia todos lados bajo las lámparas de cultivo; de noche, largas, desde la lámpara de su
     * mesa). Una sola capa.
     */
    fun DrawScope.sombrasEnLaMesa(v: VistaLab, esqueletos: List<List<V3>>) {
        val luces = v.tumban.filter { it.de == Fuente.CULTIVO || it.de == Fuente.MESA || it.de == Fuente.VELA || it.de == Fuente.LINTERNA }
        if (luces.isEmpty()) return
        capaSombra(rayoFijo(-0.5f, 50f, 0.7f, C.SOMBRA)) { c ->
            clipPath(MesaLab.formaDelTablero()) {
                for (e in esqueletos) {
                    val centro = e.reduce { a, b -> a + b } * (1f / e.size)
                    for (f in luces) {
                        val k = v.cuanto(f, V3(centro.x, MesaLab.ALTO, centro.z), PiezasLab.ARRIBA)
                        if (k < 0.08f) continue
                        sombraEnPlano(e, f.w, MesaLab.ALTO + 0.001f)?.let { drawPath(it, c.copy(alpha = (0.55f * k).coerceIn(0f, 0.85f))) }
                    }
                }
            }
        }
    }

    /**
     * Las sombras de las cosas en el suelo, en una sola capa: la de cada cosa, tumbada lejos de las luces que más la alumbran
     * (de noche, la de la puerta las tiende largas hacia quien mira y la pantalla, hacia la izquierda); debajo de cada una,
     * su sombra de contacto.
     */
    fun DrawScope.sombras(v: VistaLab, cosas: List<CosaLab>) {
        val suelo = SalaLab.formaDelSuelo()
        val luces = v.tumban.filter { it.de != Fuente.CULTIVO || v.noche }
        capaSombra(rayoFijo(-0.5f, 50f, 0.8f, C.SOMBRA)) { c ->
            clipPath(suelo) {
                for (k in cosas) {
                    val e = k.esqueleto; if (e.size < 3) continue
                    // El contacto con el suelo, siempre.
                    val pies = e.filter { it.y < 0.25f }
                    if (pies.size >= 3) drawPath(poligono(envolvente(pies.map { CamLab.p(it.x, 0f, it.z) })), c.copy(alpha = 0.5f))
                    val centro = e.reduce { a, b -> a + b } * (1f / e.size)
                    for (f in luces) {
                        val q = v.cuanto(f, V3(centro.x, 0.4f, centro.z), V3(0f, 1f, 0f)) + 0.6f * v.cuanto(f, V3(centro.x, 0.6f, centro.z), V3(f.w.x - centro.x, 0f, f.w.z - centro.z))
                        if (q < 0.06f) continue
                        sombraEnPlano(e, f.w, 0.001f)?.let { drawPath(it, c.copy(alpha = (0.6f * q).coerceIn(0f, 0.8f))) }
                    }
                    // Bajo la luz general, una sombra blanda justo debajo.
                    if (v.general > 0.1f) drawPath(poligono(envolvente(e.map { CamLab.p(it.x + 0.05f, 0f, it.z + 0.08f) })), c.copy(alpha = 0.16f * v.general))
                }
            }
        }
    }

    // =================================================================
    //  La luz en el aire
    // =================================================================

    /**
     * Los haces de las lámparas de cultivo en el aire húmedo, de la boca de cada una a su charco, con el polvo quieto que
     * brilla en ellos; el cono de la lámpara de su mesa; y de noche, el aire violeta que entra por la puerta.
     */
    fun DrawScope.aire(v: VistaLab) {
        val az = Azar(Semilla.de("laboratorio", "polvo"))
        for (f in v.luces) {
            if (f.fuerza < 0.06f) continue
            when (f.de) {
                Fuente.CULTIVO, Fuente.MESA -> {
                    val boca = if (f.de == Fuente.MESA) 0.08f else 0.16f
                    val alto = f.w.y - MesaLab.ALTO; val r = radio(f, alto) * 0.85f
                    val cz = CamLab.hondo(f.w.x, f.w.z); val k = CamLab.F / cz
                    val p0 = CamLab.p(f.w); val p1 = CamLab.p(f.w.x, MesaLab.ALTO, f.w.z)
                    val haz = Path().apply { moveTo(p0.x - boca * k, p0.y); lineTo(p0.x + boca * k, p0.y); lineTo(p1.x + r * k, p1.y); lineTo(p1.x - r * k, p1.y); close() }
                    val fuerza = (if (f.de == Fuente.MESA) 0.07f else 0.045f) * f.fuerza
                    drawPath(haz, Brush.verticalGradient(0f to f.color.copy(alpha = (1.4f * fuerza).coerceIn(0f, 1f)), 0.5f to f.color.copy(alpha = (0.6f * fuerza).coerceIn(0f, 1f)), 1f to f.color.copy(alpha = (0.35f * fuerza).coerceIn(0f, 1f)), startY = p0.y, endY = p1.y), blendMode = BlendMode.Plus)
                    // El polvo, en motas quietas a lo largo del haz.
                    repeat(if (f.de == Fuente.MESA) 8 else 14) {
                        val t = az.entre(0.05f, 0.95f); val lado = az.entre(-1f, 1f) * (boca + (r - boca) * t)
                        val q = Offset(p0.x + (p1.x - p0.x) * t + lado * k, p0.y + (p1.y - p0.y) * t)
                        drawCircle(lerp(f.color, Color.White, 0.5f).copy(alpha = (0.6f * f.fuerza.coerceAtMost(1f) * az.real()).coerceIn(0f, 1f)), az.entre(0.3f, 0.7f), q)
                    }
                }
                Fuente.PUERTA -> if (v.noche || v.roja) {
                    val a = CamLab.p(SalaLab.PX0, SalaLab.PH, SalaLab.ZF); val b = CamLab.p(SalaLab.PX1, 0f, SalaLab.ZF)
                    mancha(Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f + 10f), 70f, 60f, f.color, 0.08f * f.fuerza)
                }
                Fuente.PANTALLA -> mancha(CamLab.p(SalaLab.XD - 0.3f, 2.0f, 3.4f), 50f, 60f, f.color, 0.06f * f.fuerza)
                Fuente.VELA -> mancha(CamLab.p(f.w), 9f, 9f, f.color, 0.25f)
                Fuente.LINTERNA -> {
                    val p0 = CamLab.p(f.w); val p1 = CamLab.p(MesaLab.MK.w(1.6f, 0.35f, MesaLab.ALTO))
                    drawPath(poligono(p0 + Offset(0f, -2f), p0 + Offset(0f, 2f), p1 + Offset(0f, 14f), p1 + Offset(0f, -14f)), f.color.copy(alpha = 0.12f), blendMode = BlendMode.Plus)
                }
                Fuente.CAMPANA, Fuente.AVISO, Fuente.PANEL, Fuente.REBOTE -> {}
            }
        }
    }

    // =================================================================
    //  El velo
    // =================================================================

    /**
     * La penumbra de color de los bordes (azul verdoso de día, azul hondo de noche): la sala no está toda alumbrada, y así
     * se ve dónde está la luz.
     */
    fun DrawScope.velo(v: VistaLab) {
        val col = if (v.noche) lerp(C.SOMBRA, Color(0xFF141A34), 0.4f) else lerp(C.SOMBRA, C.VIDRIO, 0.2f)
        drawRect(Brush.radialGradient(0f to Color.Transparent, 0.5f to Color.Transparent, 1f to col.copy(alpha = if (v.noche) 0.55f else 0.32f), center = Offset(170f, 150f), radius = 290f), Offset.Zero, Size(ANCHO, ALTO), blendMode = BlendMode.Multiply)
    }

    // =================================================================
    //  Lo que se mueve
    // =================================================================

    /** Las motas que flotan en los haces de las lámparas, las gotas que bajan por el vidrio de la campana y las llamas de las velas. */
    fun DrawScope.vivo(v: VistaLab, reloj: Float) {
        val az = Azar(Semilla.de("laboratorio", "motas"))
        for (f in v.luces) {
            if ((f.de != Fuente.CULTIVO && f.de != Fuente.MESA) || f.fuerza < 0.15f) continue
            val p0 = CamLab.p(f.w); val p1 = CamLab.p(f.w.x, MesaLab.ALTO, f.w.z); val k = CamLab.F / CamLab.hondo(f.w.x, f.w.z)
            repeat(5) { i ->
                val t = ((az.real() + reloj * az.entre(0.01f, 0.03f)) % 1f); val lado = (az.entre(-0.6f, 0.6f) + 0.1f * sin(reloj * 0.5f + i)) * (0.1f + 0.4f * t)
                drawCircle(lerp(f.color, Color.White, 0.6f).copy(alpha = (0.5f * (0.4f + 0.6f * sin(reloj * 0.8f + i).let { it * it })).coerceIn(0f, 1f)), 0.5f, Offset(p0.x + (p1.x - p0.x) * t + lado * k, p0.y + (p1.y - p0.y) * t))
            }
        }
        // Una gota que baja despacio por dentro del vidrio de la campana.
        val c = CampanaLab.CENTRO
        for (j in 0..1) {
            val ciclo = ((reloj * 0.02f + j * 0.5f) % 1f)
            val a = kotlin.math.atan2(-c.z, -c.x) - 0.6f + 0.9f * j
            val y = CampanaLab.ALTO - 0.5f - 1.8f * ciclo
            val q = V3(c.x + CampanaLab.R * cos(a), y, c.z + CampanaLab.R * sin(a)); val p = CamLab.p(q)
            drawLine(lerp(C.VIDRIO, Color.White, 0.3f).copy(alpha = 0.3f), p, p + Offset(0f, -6f * ciclo), 0.4f)
            drawCircle(Color.White.copy(alpha = 0.5f), 0.7f, p)
        }
        // Las llamas de las velas.
        if (v.velas) for ((i, ab) in LucesLab.VELAS.withIndex()) {
            val q = MesaLab.MK.w(ab.first, ab.second, MesaLab.ALTO + 0.13f); val p = CamLab.p(q); val k = CamLab.k(q)
            val late = 0.85f + 0.15f * sin(reloj * 7f + i * 2f)
            drawOval(lerp(C.VELA, Color.White, 0.4f), p - Offset(0.008f * k, 0.035f * k * late), Size(0.016f * k, 0.035f * k * late))
            mancha(p - Offset(0f, 0.02f * k), 0.05f * k, 0.06f * k, C.VELA, 0.25f * late)
        }
    }
}
