package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.k
import io.github.ruddy2003.vestigium.ui.escenas.SelvaBotanico.pc
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.charcoDeLuz
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Foco
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.sin

/**
 * El primer plano de la selva, sobre las tablas: la colección viva del naturalista y su trabajo, mezclados, a
 * profundidades distintas y tapándose. La mesa de trabajo bajo el alero (el herbario, la prensa, la caja de insectos con
 * la tapa levantada, la lámina en su atril, los frascos y la lámpara), medio invadida por las hojas; los cajones con
 * plantas encima, contra el poste; la caja de vidrio para plantas sobre su cajón; la tina con la palma joven; las macetas
 * con el helecho y la bromelia; el plantón recién traído con su cepellón envuelto; los filodendros cortados por el borde.
 * El poste, con la escopeta y la red cuando está en casa. **[Diseño propio]**: la colección viva en macetas y tinas, los
 * cestos colgados y la caja de vidrio (el cuento habla de frascos, prensa y cajones, no de plantas vivas en macetas).
 */
internal object CercaSelva {
    private val C = ColorBotanico
    private val S = SelvaBotanico

    /** Una cosa del primer plano: su profundidad (para pintar de atrás adelante), su sombra en las tablas (o null) y cómo se pinta. */
    class Cosa3(val hondo: Float, val sombra: Path?, val pintar: DrawScope.() -> Unit)

    // ---------------------------------------------------------------- La mesa

    val SITIO_MESA: Sitio = S.sitio(-1.15f, 2.98f, 0.25f)
    val MESA = PiezasBotanico.Mesa(SITIO_MESA, Semilla.de("botanico", "mesa", "selva"), PiezasBotanico.TipoMesa.GALERIA, ancho = 1.15f, fondo = 0.6f, alto = 0.78f)

    /** Un punto de la tapa de la mesa en ([u], [v]) (de −1 a 1 a lo ancho y a lo hondo), en metros. */
    fun enLaMesa(u: Float, v: Float): V3 { val x = u * MESA.ancho / 2f; val z = v * MESA.fondo / 2f; return V3(SITIO_MESA.mx(x, z), MESA.alto, SITIO_MESA.mz(x, z)) }

    /** El centro de la tapa de la mesa, en metros. */
    val CENTRO_MESA: V3 = enLaMesa(0f, 0f)

    /** La llama de la lámpara de la mesa, arrimada a la punta de junto al poste. */
    val LAMPARA: Offset = enLaMesa(0.84f, 0.52f).let { pc(it.x, it.y + 0.3f, it.z) }

    /** Lo que se pone en la mesa: el soporte del objeto del día (en el centro de delante, despejado). */
    val SOPORTE: Offset get() = pc(enLaMesa(0.28f, -0.45f))

    // ---------------------------------------------------------------- Los cajones, junto al poste

    private fun cajones(m: Momento): List<PiezasBotanico.Cajon> {
        val n = S.cajonesEnLaGaleria(m); val abiertos = S.cajonesAbiertos(m)
        // Dónde va cada uno: el primero en el suelo junto al poste; el segundo encima; los demás, al lado y detrás.
        // Bajos, para que se vea por encima el pie del árbol grande: uno junto al poste, los otros detrás, y alguno encima.
        val sitios = listOf(Triple(-0.12f, 4.55f, 0), Triple(-0.6f, 5.1f, 0), Triple(0.42f, 5.15f, 0), Triple(0.42f, 5.15f, 1), Triple(-0.6f, 5.1f, 1))
        return (0 until n).map { i -> val (x, z, piso) = sitios[i]
            PiezasBotanico.Cajon(S.sitio(x, z, -0.22f + 0.1f * i), Semilla.de("botanico", "cajon selva", i), ancho = 0.66f, alto = 0.4f, fondo = 0.46f, base = piso * 0.4f, desgaste = 0.55f, trincado = false, marca = !abiertos || i > 0) }
    }

    /** El sitio alto del gato: la tapa del cajón de arriba, junto al poste. */
    fun mirador(m: Momento): Offset { val c = cajones(m); val alto = c.maxByOrNull { it.y1 }!!; return alto.s.p(-0.12f, alto.y1, 0.02f) }

    // ---------------------------------------------------------------- Lo que hay en el tablado

    /** Todo lo del primer plano de hoy, con su sombra en las tablas. */
    fun cosas(m: Momento, luz: LuzSelva.Luz): List<Cosa3> = buildList {
        val sol = luz.sol
        // La mesa, con todo lo de encima.
        add(Cosa3(3.25f, PiezasBotanico.sombraDeMesa(MESA, sol)) { mesa(m, luz) })
        // El poste, con la escopeta y la red cuando está en casa, y el objeto grande apoyado.
        add(Cosa3(S.POSTE_Z, null) { poste(m, luz) })
        // Los cajones, con plantas encima.
        for ((i, c) in cajones(m).withIndex()) add(Cosa3(c.s.z0 - 0.01f * i, PiezasBotanico.sombraDeCajon(c, sol)) { cajon(m, luz, c, i) })
        // La caja de vidrio sobre su cajón.
        val sv = S.sitio(0.7f, 3.95f, -0.45f)
        val base = PiezasBotanico.Cajon(sv, Semilla.de("botanico", "cajon vidrio"), ancho = 0.72f, alto = 0.4f, fondo = 0.48f, desgaste = 0.3f, trincado = false, marca = false)
        val caja = PiezasBotanico.CajaDeVidrio(sv, Semilla.de("botanico", "caja de vidrio"), base = 0.4f)
        add(Cosa3(3.95f, PiezasBotanico.sombraDeCajaDeVidrio(caja, sol)) {
            val pin = LuzSelva.pincelEn(luz, sv.pie, LuzSelva.alumbrado(luz, 0.7f, 0.7f, 3.95f))
            with(PiezasBotanico) { cajon(base, pin, sol); cajaDeVidrio(caja, pin, sol, LuzSelva.alumbrado(luz, 0.7f, 0.9f, 3.95f)) }
        })
        // La tina con la palma joven.
        add(Cosa3(5.15f, sombraRedonda(1.15f, 5.15f, 0.3f, 1.5f, sol)) { tinaConPalma(m, luz, 1.15f, 5.15f) })
        // La maceta del helecho, la de la bromelia y el plantón con su cepellón.
        add(Cosa3(3.55f, sombraRedonda(0.38f, 3.55f, 0.2f, 0.65f, sol)) { macetaConHelecho(m, luz, 0.38f, 3.55f) })
        add(Cosa3(3.45f, sombraRedonda(1.15f, 3.45f, 0.16f, 0.55f, sol)) { macetaConBromelia(m, luz, 1.15f, 3.45f) })
        // El plantón recién traído (el día de la mudanza todavía no: ese día el sitio es de los libros).
        if (!EscenaBotanico.botafogo(m)) add(Cosa3(3.3f, null) { planton(m, luz) })
        // Los filodendros que entran por los bordes: el de la esquina de la derecha, en el jardín, y el de abajo a la izquierda.
        add(Cosa3(3.15f, null) { filodendros(m, luz, Offset(2.4f, 3.1f), S.sueloEn(2.4f, 3.1f), 0.9f, Semilla.de("botanico", "filodendro derecha"), true) })
        add(Cosa3(2.5f, null) { filodendros(m, luz, Offset(-1.75f, 2.5f), 0f, 0.62f, Semilla.de("botanico", "filodendro izquierda"), false) })
        // El día de la mudanza: los libros mojados, abiertos al sol sobre las tablas, y los papeles.
        if (EscenaBotanico.librosAlSol(m)) {
            for ((i, q) in LIBROS.withIndex()) add(Cosa3(q.z, null) { libroAbierto(luz, q, i) })
            for ((i, q) in PAPELES.withIndex()) add(Cosa3(q.z + 0.01f, null) { papelAlSol(luz, q, i) })
        }
        // El día de los escarabajos: el paraguas abierto al revés, con lo que cayó dentro.
        if (S.paraguasAlReves(m)) add(Cosa3(4.45f, null) { paraguas(luz, V3(0.15f, 0f, 4.45f)) })
    }

    /** Dónde se secan los libros el día de la mudanza: sobre las tablas, donde da el sol de la tarde. */
    private val LIBROS = listOf(V3(-0.32f, 0f, 3.42f), V3(0.5f, 0f, 3.3f), V3(1.0f, 0f, 3.72f), V3(-0.58f, 0f, 4.05f), V3(0.95f, 0f, 3.25f))

    /** Y los papeles sueltos, extendidos con una piedra encima para que no se los lleve el aire. */
    private val PAPELES = listOf(V3(0.25f, 0f, 3.18f), V3(-0.72f, 0f, 3.62f), V3(0.72f, 0f, 3.58f))

    /** La sombra en las tablas de un cuerpo redondo de [radio] y [alto] metros que pisa en ([x], [z]). */
    private fun sombraRedonda(x: Float, z: Float, radio: Float, alto: Float, sol: Sol3): Path {
        val s = S.sitio(x, z)
        return sombraDe(s, sol, (0 until 10).flatMap { i -> val a = (2 * PI * i / 10).toFloat(); listOf(V3(radio * cos(a), 0f, radio * sin(a)), V3(radio * 0.6f * cos(a), alto, radio * 0.6f * sin(a))) })
    }

    /** El pincel de lo que hay en ([x], [h], [z]). */
    private fun pin(luz: LuzSelva.Luz, x: Float, h: Float, z: Float): Pincel = LuzSelva.pincelEn(luz, pc(x, h, z), LuzSelva.alumbrado(luz, x, h, z))

    // ---------------------------------------------------------------- Pintar

    fun DrawScope.cerca(m: Momento, luz: LuzSelva.Luz, cosas: List<Cosa3>) {
        // La sombra de contacto bajo cada cosa (lo que no ve el cielo), sin capa: un óvalo que multiplica.
        for ((x, z, r) in listOf(Triple(-1.15f, 2.98f, 0.6f), Triple(1.15f, 5.15f, 0.34f), Triple(0.7f, 3.95f, 0.42f), Triple(0.38f, 3.55f, 0.24f), Triple(1.15f, 3.45f, 0.2f), Triple(-0.12f, 4.55f, 0.4f))) {
            val c = pc(x, 0f, z); val e = k(z)
            drawOval(LuzSelva.SOMBRA_VERDE.copy(alpha = 0.32f), Offset(c.x - r * e, c.y - r * e * 0.22f), Size(2f * r * e, r * e * 0.44f), blendMode = BlendMode.Multiply)
        }
        for (c in cosas.sortedByDescending { it.hondo }) c.pintar(this)
    }

    // ---------------------------------------------------------------- La mesa de trabajo

    private fun DrawScope.mesa(m: Momento, luz: LuzSelva.Luz) {
        val sol = luz.sol; val estado = EscenaBotanico.mesaDeLaGaleria(m)
        // La mesa, en la penumbra del alero; el sol que llega a su tapa se le suma encima (con la sombra de lo que hay).
        with(PiezasBotanico) { mesa(MESA, LuzSelva.pincelEn(luz, pc(CENTRO_MESA), 0f), sol) }
        val tapa = poligono(listOf(enLaMesa(-1f, -1f), enLaMesa(1f, -1f), enLaMesa(1f, 1f), enLaMesa(-1f, 1f)).map { pc(it) })
        val alto = MESA.alto
        val enMesa = { x: Float, z: Float, g: Float -> S.sitio(SITIO_MESA.mx(x * MESA.ancho / 2f, z * MESA.fondo / 2f), SITIO_MESA.mz(x * MESA.ancho / 2f, z * MESA.fondo / 2f), g) }
        val pr = PiezasBotanico.Prensa(enMesa(0.66f, -0.38f, 0.12f), Semilla.de("botanico", "prensa selva"), ancho = 0.32f, fondo = 0.24f, grueso = 0.1f, base = alto)
        val frascos = listOf(0.3f to 0.66f, 0.45f to 0.78f, 0.58f to 0.62f)
        fun cilindro(u: Float, v: Float, r: Float, h: Float): Path = sombraDe(enMesa(u, v, 0f), sol, (0 until 8).flatMap { i -> val a = (2 * PI * i / 8).toFloat(); listOf(V3(r * cos(a), alto, r * sin(a)), V3(r * 0.9f * cos(a), alto + h, r * 0.9f * sin(a))) }, alto)
        val sombras = listOf(PiezasBotanico.sombraDePrensa(pr, sol), cilindro(0.84f, 0.52f, 0.06f, 0.42f), sombraDe(enMesa(-0.07f, 0.66f, 0f), sol, esquinas(-0.15f, 0.15f, alto, alto + 0.3f, -0.03f, 0.03f), alto)) + frascos.map { (u, v) -> cilindro(u, v, 0.045f, 0.21f) }
        with(LuzSelva) { luzEnPlano(luz, tapa, alto, sombras, 0.9f) }
        val sMesa = LuzSelva.alumbrado(luz, CENTRO_MESA.x, alto + 0.05f, CENTRO_MESA.z)
        val p = pin(luz, CENTRO_MESA.x, alto + 0.15f, CENTRO_MESA.z)
        val az = Azar(Semilla.de("botanico", "mesa selva", "cosas"))
        // Las hojas que invaden la mesa: el helecho de la maceta del rincón del fondo, que cuelga sobre la tapa.
        helechoColgante(luz, enLaMesa(-0.62f, 0.78f), 0.55f, Azar(Semilla.de("botanico", "helecho mesa")))
        with(PiezasBotanico) {
            // La caja de insectos: la bandeja abierta y la tapa levantada detrás, de pie, con sus mariposas a la vista.
            val b0 = enLaMesa(-0.34f, 0.12f); val b1 = enLaMesa(0.2f, 0.12f); val b2 = enLaMesa(0.2f, 0.62f); val b3 = enLaMesa(-0.34f, 0.62f)
            cajaDeInsectos(p, listOf(b0, b1, b2, b3).map { pc(it) }, Offset(0f, 1.4f), 0.3f, Azar(Semilla.de("botanico", "insectos bandeja")), 0f, sMesa)
            val tapaCaja = listOf(b3, b2, b2 + V3(0f, 0.3f, 0.07f), b3 + V3(0f, 0.3f, 0.07f)).map { pc(it) }
            cajaDeInsectos(p, tapaCaja, Offset(1.2f, 0.6f), 0.2f + 0.3f * sMesa, Azar(Semilla.de("botanico", "insectos tapa")), 0f, sMesa)
            // Los frascos, cada uno con su bicho en alcohol.
            for ((i, uv) in frascos.withIndex()) { val q = enLaMesa(uv.first, uv.second); frasco(p, pc(q), 0.21f * k(q.z), i) }
            // El herbario abierto delante, o los escarabajos del día en filas sobre un papel.
            val h0 = enLaMesa(-0.46f, -0.95f); val h1 = enLaMesa(0.06f, -0.95f); val h2 = enLaMesa(0.06f, -0.22f); val h3 = enLaMesa(-0.46f, -0.22f)
            if (S.escarabajosEnLaMesa(m)) escarabajosEnFila(p, listOf(h0, h1, h2, h3).map { pc(it) }, az)
            else if (estado != EscenaBotanico.Mesa.RECOGIDA) hojaDeHerbario(p, listOf(h0, h1, h2, h3).map { pc(it) }, 2, 0.3f, Azar(Semilla.de("botanico", "herbario mesa")), 0f, sMesa)
            // La prensa, en la punta de delante, junto al poste.
            prensa(pr, p, sol)
            // El objeto del día, entre el herbario y la prensa.
            with(ObjetosBotanico) { sobreElSoporte(m, p, SOPORTE, 1.35f) }
        }
        lampara(m, luz)
    }

    /** Los escarabajos del día, en filas sobre un papel, con las pinzas al lado: verdes de metal, bronce y negros. */
    private fun DrawScope.escarabajosEnFila(p: Pincel, q: List<Offset>, az: Azar) {
        val papel = poligono(q)
        drawPath(papel, p.arriba(lerp(C.PAPEL, C.LONA, 0.4f)))
        with(PiezasBotanico) {
            for (fila in 0..3) for (col in 0..6) {
                val c = en(q, 0.1f + col * 0.13f, 0.18f + fila * 0.22f); val r = 1.1f + az.real() * 0.8f
                val color = when ((fila + col) % 3) { 0 -> C.ESCARABAJO; 1 -> lerp(C.LATON, C.CUERO, 0.4f); else -> C.BREA }
                drawOval(p.medio(color), Offset(c.x - r * 0.6f, c.y - r * 0.4f), Size(r * 1.2f, r * 0.8f))
                drawCircle(p.luz(Color.White).copy(alpha = 0.5f), r * 0.2f, c + Offset(-r * 0.2f, -r * 0.15f))
            }
            drawLine(p.luz(C.LATON), en(q, 0.85f, -0.05f), en(q, 1.15f, 0.4f), 0.9f)
            drawLine(p.luz(C.LATON), en(q, 0.88f, -0.07f), en(q, 1.18f, 0.36f), 0.9f)
        }
    }

    /** La lámpara de la mesa: de latón, con su depósito y el tubo de vidrio; encendida, la llama y su luz en la mesa. */
    private fun DrawScope.lampara(m: Momento, luz: LuzSelva.Luz) {
        val b = enLaMesa(0.84f, 0.52f); val pie = pc(b); val e = k(b.z); val f = luz.lampara
        val p = pin(luz, b.x, b.y + 0.2f, b.z)
        if (f > 0.05f) {
            // Su luz en la tapa de la mesa: un charco cálido alrededor del pie.
            charcoDeLuz(Foco(LAMPARA, C.LUMBRE, f, 70f), pie + Offset(-e * 0.15f, e * 0.04f), e * 0.6f, e * 0.16f, C.MADERA, 0.9f)
        }
        val lado = if (p.solALaDerecha) 1f else -1f
        // El pie, el depósito de vidrio con el aceite y la virola.
        drawPath(poligono(pie + Offset(-e * 0.07f, 0f), pie + Offset(e * 0.07f, 0f), pie + Offset(e * 0.03f, -e * 0.08f), pie + Offset(-e * 0.03f, -e * 0.08f)), Brush.horizontalGradient(listOf(p.sombra(C.LATON), p.luz(C.LATON)), pie.x - lado * e * 0.07f, pie.x + lado * e * 0.07f))
        val dep = pie + Offset(0f, -e * 0.13f)
        drawOval(Brush.horizontalGradient(listOf(p.sombra(lerp(C.LATON, C.ESCARABAJO, 0.3f)), p.luz(lerp(C.LATON, C.LONA, 0.2f))), dep.x - lado * e * 0.07f, dep.x + lado * e * 0.07f), Offset(dep.x - e * 0.07f, dep.y - e * 0.05f), Size(e * 0.14f, e * 0.1f))
        drawRect(p.por(C.LATON, 0.3f), Offset(dep.x - e * 0.035f, dep.y - e * 0.08f), Size(e * 0.07f, e * 0.03f))
        // El tubo de vidrio, panzudo, con la llama o apagado y tiznado.
        val t0 = dep + Offset(0f, -e * 0.08f)
        val tubo = Path().apply { moveTo(t0 + Offset(-e * 0.025f, 0f)); cubicTo(t0 + Offset(-e * 0.055f, -e * 0.04f), t0 + Offset(-e * 0.045f, -e * 0.1f), t0 + Offset(-e * 0.022f, -e * 0.13f)); lineTo(t0 + Offset(-e * 0.02f, -e * 0.2f)); lineTo(t0 + Offset(e * 0.02f, -e * 0.2f)); lineTo(t0 + Offset(e * 0.022f, -e * 0.13f)); cubicTo(t0 + Offset(e * 0.045f, -e * 0.1f), t0 + Offset(e * 0.055f, -e * 0.04f), t0 + Offset(e * 0.025f, 0f)); close() }
        if (f > 0.05f) {
            drawPath(tubo, lerp(C.LUMBRE, Color(0xFFFFF2C8), 0.4f).copy(alpha = 0.55f + 0.4f * f))
            drawOval(Color(0xFFFFF0B0), Offset(t0.x - e * 0.012f, t0.y - e * 0.085f), Size(e * 0.024f, e * 0.06f))
            resplandor(t0 + Offset(0f, -e * 0.06f), e * 0.25f, 0.7f * f, C.LUMBRE)
        } else {
            drawPath(tubo, p.medio(lerp(C.LONA, C.MAR, 0.15f)).copy(alpha = 0.45f))
            drawLine(p.luz(Color.White).copy(alpha = 0.6f), t0 + Offset(lado * e * 0.025f, -e * 0.03f), t0 + Offset(lado * e * 0.015f, -e * 0.12f), max(0.4f, e * 0.008f))
            drawRect(p.sombra(C.BREA).copy(alpha = 0.35f), Offset(t0.x - e * 0.02f, t0.y - e * 0.2f), Size(e * 0.04f, e * 0.03f))
        }
    }

    /** Un helecho de maceta que cuelga: frondas largas que salen de [c] (en metros) y caen por el borde. */
    private fun DrawScope.helechoColgante(luz: LuzSelva.Luz, c: V3, largo: Float, az: Azar) {
        val p = pin(luz, c.x, c.y + 0.2f, c.z); val q = pc(c); val e = k(c.z)
        val pie = q
        with(PiezasBotanico) { maceta(p, pie + Offset(0f, e * 0.02f), e * 0.22f, e * 0.17f, Azar(Semilla.de("botanico", "maceta mesa"))) }
        val centro = pie + Offset(0f, -e * 0.17f)
        frondasDeMaceta(p, centro, largo * e, 13, az, 0.9f)
    }

    /** Las frondas de un helecho de maceta: arqueadas hacia arriba y hacia fuera, las de los lados caen por el borde. */
    private fun DrawScope.frondasDeMaceta(p: Pincel, c: Offset, largo: Float, cuantas: Int, az: Azar, cae: Float) {
        val lado = if (p.solALaDerecha) 1f else -1f
        val angulos = (0 until cuantas).map { -PI.toFloat() / 2f + (it - (cuantas - 1) / 2f) * (3.2f / cuantas) + az.entre(-0.1f, 0.1f) }.sortedBy { abs(it + PI.toFloat() / 2f) }
        for (a in angulos) {
            val l = largo * az.entre(0.7f, 1.05f); val abre = abs(a + PI.toFloat() / 2f)
            val fin = c + Offset(cos(a) * l * 0.9f, sin(a) * l * 0.5f + l * cae * abre * 0.45f)
            val ctrl = c + Offset(cos(a) * l * 0.45f, sin(a) * l * 0.75f)
            val verde = lerp(C.HOJA, C.SELVA, az.entre(0.1f, 0.45f)); val t = (cos(a) * lado * 0.6f + 0.1f).coerceIn(-1f, 1f)
            val claras = ArrayList<Offset>(); val oscuras = ArrayList<Offset>()
            for (k in 2..18) { val s = k / 18f; val m1 = 1f - s; val qq = c * (m1 * m1) + ctrl * (2f * m1 * s) + fin * (s * s)
                val qs = c * ((1f - s - 0.05f) * (1f - s - 0.05f)) + ctrl * (2f * (1f - s - 0.05f) * (s + 0.05f)) + fin * ((s + 0.05f) * (s + 0.05f))
                val d = qs - qq; val dl = d.getDistance().coerceAtLeast(0.01f); val n = Offset(-d.y / dl, d.x / dl); val w = l * 0.09f * (1f - s * 0.7f)
                claras += qq; claras += qq + n * w + d / dl * (w * 0.4f); oscuras += qq; oscuras += qq - n * w + d / dl * (w * 0.4f) }
            drawPoints(oscuras, PointMode.Lines, p.por(verde, t - 0.5f), max(0.5f, l * 0.03f), StrokeCap.Round)
            drawPoints(claras, PointMode.Lines, p.por(verde, t + 0.2f), max(0.5f, l * 0.03f), StrokeCap.Round)
        }
    }

    // ---------------------------------------------------------------- El poste

    private fun DrawScope.poste(m: Momento, luz: LuzSelva.Luz) {
        val x = S.POSTE_X; val z = S.POSTE_Z; val g = S.GRUESO_POSTE / 2f
        val s = S.sitio(x, z); val sol = luz.sol
        val p = pin(luz, x + g, 1.3f, z - g)
        // La piedra de la base, y el poste de madera con su veta, sus grietas y el roce de abajo.
        for (cara in visibles(s, carasDeCaja(-g * 1.6f, g * 1.6f, 0f, 0.14f, -g * 1.6f, g * 1.6f))) drawPath(forma(s, cara), p.por(lerp(C.ROCA, C.ARENA, 0.3f), luzDe(s, sol, cara)))
        for (cara in visibles(s, carasDeCaja(-g, g, 0.14f, S.ALTO_ALERO + 0.1f, -g, g))) {
            val f = forma(s, cara); val t = luzDe(s, sol, cara)
            drawPath(f, p.por(lerp(C.MADERA, C.CAOBA, 0.25f), t))
            clipPath(f) {
                for (k in 1..5) linea(s, cara, 0.17f * k, 0f, 0.17f * k + 0.04f, 1f, p.sombra(C.MADERA).copy(alpha = 0.3f), 0.5f)
                linea(s, cara, 0.45f, 0.3f, 0.47f, 0.7f, p.sombra(C.BREA).copy(alpha = 0.5f), 0.7f)
                linea(s, cara, 0f, 0.05f, 1f, 0.05f, p.medio(lerp(C.SELVA, C.HOJA, 0.3f)).copy(alpha = 0.5f), 3f)
            }
        }
        val az = Azar(Semilla.de("botanico", "poste"))
        // Las bromelias atadas al poste con fibra, a dos alturas.
        with(PiezasBotanico) {
            for ((h, tam) in listOf(1.55f to 0.36f, 0.95f to 0.3f)) { val q = pc(x + g * 0.6f, h, z - g); bromelia(p, q, tam * k(z), az, 0f, flor = h > 1.2f)
                drawLine(p.sombra(C.CANAMO), q + Offset(-4f, 2f), q + Offset(4f, 2.4f), 0.8f) }
        }
        // La red de mariposas, colgada de un clavo por el aro, y la escopeta apoyada: cuando está en casa.
        // (el día que la red o la escopeta son el objeto del día, van al pie del poste y no aquí: no se ven dos)
        if (EscenaBotanico.redYEscopetaEnCasa(m) && m.ficha.objeto != "red" && m.ficha.objeto != "escopeta") {
            val clavo = pc(x + g * 0.3f, 2.05f, z - g); val e = k(z)
            drawCircle(p.sombra(C.HIERRO), 0.8f, clavo)
            val aro = clavo + Offset(e * 0.05f, e * 0.17f)
            drawOval(p.sombra(C.HIERRO), Offset(aro.x - e * 0.15f, aro.y - e * 0.16f), Size(e * 0.3f, e * 0.32f), style = Stroke(max(0.6f, e * 0.012f)))
            // La bolsa de gasa: se transparenta, con su malla y los pliegues donde la tela se junta.
            val bolsa = Path().apply { moveTo(aro + Offset(-e * 0.15f, 0f)); cubicTo(aro + Offset(-e * 0.13f, e * 0.25f), aro + Offset(e * 0.02f, e * 0.42f), aro + Offset(e * 0.05f, e * 0.48f)); cubicTo(aro + Offset(e * 0.12f, e * 0.3f), aro + Offset(e * 0.15f, e * 0.1f), aro + Offset(e * 0.15f, 0f)); close() }
            drawPath(bolsa, p.por(lerp(C.LONA, C.ARENA, 0.2f), 0.2f).copy(alpha = 0.42f))
            clipPath(bolsa) {
                for (j in -6..6) { drawLine(p.sombra(C.LONA).copy(alpha = 0.35f), aro + Offset(j * e * 0.03f, -e * 0.02f), aro + Offset(j * e * 0.03f + e * 0.2f, e * 0.5f), 0.3f); drawLine(p.sombra(C.LONA).copy(alpha = 0.35f), aro + Offset(j * e * 0.03f, -e * 0.02f), aro + Offset(j * e * 0.03f - e * 0.2f, e * 0.5f), 0.3f) }
                for (j in 1..3) drawLine(p.luz(C.LONA).copy(alpha = 0.5f), aro + Offset(-e * 0.1f + j * e * 0.05f, e * 0.04f), aro + Offset(e * 0.04f, e * 0.44f), 0.5f)
            }
            drawLine(p.por(lerp(C.MADERA, C.ARENA, 0.3f), 0.3f), aro + Offset(e * 0.14f, e * 0.08f), aro + Offset(e * 0.2f, e * 1.25f), max(0.8f, e * 0.022f))
            // La escopeta, del suelo al poste.
            val cul = pc(x - 0.26f, 0f, z - 0.12f); val boca = pc(x - g, 1.18f, z - 0.02f)
            drawLine(p.sombra(C.HIERRO), cul + (boca - cul) * 0.33f, boca, max(0.9f, e * 0.022f))
            drawLine(p.luz(C.HIERRO).copy(alpha = 0.6f), cul + (boca - cul) * 0.35f + Offset(0.5f, 0f), boca + Offset(0.5f, 0f), 0.4f)
            drawPath(poligono(cul + Offset(-e * 0.04f, 0f), cul + Offset(e * 0.035f, 0f), cul + (boca - cul) * 0.36f + Offset(e * 0.012f, 0f), cul + (boca - cul) * 0.3f + Offset(-e * 0.02f, 0f)), p.por(C.CUERO, 0.2f))
        }
        // El objeto grande del día, apoyado en el poste por el lado de la luz.
        // A la escala de las cosas de su sitio (unos 77 por metro a esa hondura; el objeto cuenta 60 por metro).
        ObjetosBotanico.grande(m, p, pc(x + 0.24f, 0f, z - 0.1f), pc(x + g, 1.25f, z - g), k(z - 0.1f) / ObjetosBotanico.METRO)?.let { it.pintar(this) }
    }

    // ---------------------------------------------------------------- Los cajones con plantas encima

    private fun DrawScope.cajon(m: Momento, luz: LuzSelva.Luz, c: PiezasBotanico.Cajon, i: Int) {
        val x = c.s.mx(0f, 0f); val z = c.s.mz(0f, 0f)
        val p = pin(luz, x, c.y1, z)
        with(PiezasBotanico) { cajon(c, p, luz.sol) }
        // En el frente de los dos cajones de junto al poste, clavadas: una lámina (el pinzón) y una hoja de herbario.
        if (i < 2 && c.base < 0.01f && !(S.cajonesAbiertos(m) && i == 0)) with(PiezasBotanico) {
            val z0 = c.z0 - 0.008f; val y0 = c.y0 + 0.05f; val y1 = c.y1 - 0.04f
            val q = listOf(c.s.p(-0.27f, y0, z0), c.s.p(0.05f, y0, z0), c.s.p(0.05f, y1, z0), c.s.p(-0.27f, y1, z0))
            if (i == 0) lamina(p, q, 0.25f, Azar(Semilla.de("botanico", "lamina cajon"))) else hojaDeHerbario(p, q, 1, 0.25f, Azar(Semilla.de("botanico", "herbario cajon")))
            drawCircle(p.sombra(C.HIERRO), 0.6f, q[3] + Offset(1.6f, 1.6f)); drawCircle(p.sombra(C.HIERRO), 0.6f, q[2] + Offset(-1.6f, 1.6f))
        }
        val arriba = cajones(m).none { it !== c && abs(it.s.mx(0f, 0f) - x) < 0.05f && abs(it.s.mz(0f, 0f) - z) < 0.05f && it.y0 > c.y0 }
        if (!arriba) return
        val az = Azar(Semilla.de("botanico", "encima", i))
        // Encima: una maceta con una bromelia o un helecho, de lado, para que quede sitio al gato.
        val q = c.s.p(c.ancho * 0.24f, c.y1, -c.fondo * 0.1f); val e = c.s.escala(0f, 0f)
        with(PiezasBotanico) {
            val tierra = maceta(p, q, e * 0.2f, e * 0.17f, az)
            if (i % 2 == 0) bromelia(p, tierra, e * 0.36f, az, 0f, flor = true) else frondasDeMaceta(p, tierra, e * 0.3f, 11, az, 0.8f)
        }
        // El día de la mudanza, la tapa del de abajo levantada y los papeles asomando.
        if (S.cajonesAbiertos(m) && i == 0) {
            val t0 = c.s.p(c.x0, c.y1, c.z1); val t1 = c.s.p(c.x1, c.y1, c.z1)
            drawPath(poligono(t0, t1, t1 + Offset(0f, -e * 0.42f), t0 + Offset(0f, -e * 0.4f)), p.por(lerp(C.MADERA, C.ARENA, 0.3f), -0.2f))
        }
    }

    // ---------------------------------------------------------------- Las macetas y la tina

    private fun DrawScope.tinaConPalma(m: Momento, luz: LuzSelva.Luz, x: Float, z: Float) {
        val p = pin(luz, x, 1.2f, z); val e = k(z); val az = Azar(Semilla.de("botanico", "tina palma"))
        with(PiezasBotanico) {
            val tierra = tina(p, pc(x, 0f, z), e * 0.6f, e * 0.42f, az)
            // La palma joven: tres troncos finos de distinta altura, con sus pencas arqueadas.
            for ((dx, h, inc) in listOf(Triple(-0.08f, 0.6f, -0.12f), Triple(0.06f, 0.72f, 0.1f), Triple(0.12f, 0.48f, 0.22f))) {
                palmera(p, tierra + Offset(dx * e, 0f), h * e, inc * e, az, 0f, pencas = 7, contraluz = BovedaSelva.contraluz(luz, tierra + Offset(0f, -h * e)), secas = 0)
            }
        }
    }

    private fun DrawScope.macetaConHelecho(m: Momento, luz: LuzSelva.Luz, x: Float, z: Float) {
        val p = pin(luz, x, 0.5f, z); val e = k(z); val az = Azar(Semilla.de("botanico", "maceta helecho"))
        with(PiezasBotanico) { val tierra = maceta(p, pc(x, 0f, z), e * 0.34f, e * 0.3f, az); frondasDeMaceta(p, tierra, e * 0.5f, 16, az, 1f) }
    }

    private fun DrawScope.macetaConBromelia(m: Momento, luz: LuzSelva.Luz, x: Float, z: Float) {
        val p = pin(luz, x, 0.4f, z); val e = k(z); val az = Azar(Semilla.de("botanico", "maceta bromelia"))
        with(PiezasBotanico) { val tierra = maceta(p, pc(x, 0f, z), e * 0.3f, e * 0.26f, az); bromelia(p, tierra, e * 0.55f, az, 0f, flor = true, contraluz = BovedaSelva.contraluz(luz, tierra)) }
    }

    /** El plantón recién traído de la selva, tumbado en las tablas: el tallo con sus hojas y el cepellón envuelto en arpillera y atado. */
    private fun DrawScope.planton(m: Momento, luz: LuzSelva.Luz) {
        val x = -0.95f; val z = 3.3f; val p = pin(luz, x, 0.2f, z); val e = k(z); val az = Azar(Semilla.de("botanico", "planton"))
        val c = pc(x, 0f, z) + Offset(0f, -e * 0.1f)
        val arpillera = lerp(C.CANAMO, C.ARENA, 0.2f)
        drawOval(Brush.radialGradient(listOf(p.luz(arpillera), p.medio(arpillera), p.sombra(arpillera)), c + Offset(-e * 0.04f, -e * 0.05f), e * 0.16f), Offset(c.x - e * 0.13f, c.y - e * 0.1f), Size(e * 0.26f, e * 0.2f))
        clipPath(Path().apply { addOval(androidx.compose.ui.geometry.Rect(c.x - e * 0.13f, c.y - e * 0.1f, c.x + e * 0.13f, c.y + e * 0.1f)) }) {
            for (k in -4..4) drawLine(p.sombra(arpillera).copy(alpha = 0.4f), c + Offset(k * e * 0.03f, -e * 0.12f), c + Offset(k * e * 0.03f + e * 0.02f, e * 0.12f), 0.4f)
            mancha(c + Offset(e * 0.03f, e * 0.04f), e * 0.08f, e * 0.04f, p.medio(C.MADERA_OSC), 0.5f)
        }
        drawLine(p.sombra(C.CANAMO), c + Offset(-e * 0.1f, -e * 0.08f), c + Offset(-e * 0.06f, e * 0.09f), 0.8f)
        drawLine(p.sombra(C.CANAMO), c + Offset(e * 0.02f, -e * 0.1f), c + Offset(e * 0.06f, e * 0.1f), 0.8f)
        // El tallo, tendido hacia la derecha, con hojas grandes y alguna tierna.
        val fin = c + Offset(e * 0.75f, -e * 0.18f)
        drawPath(Path().apply { moveTo(c + Offset(e * 0.12f, -e * 0.02f)); cubicTo(c + Offset(e * 0.35f, -e * 0.1f), fin + Offset(-e * 0.2f, 0f), fin) }, p.medio(lerp(C.CUERO, C.HOJA, 0.4f)), style = Stroke(max(0.6f, e * 0.018f), cap = StrokeCap.Round))
        for (j in 0..5) { val t = 0.25f + j * 0.13f; val q = c + (fin - c) * t + Offset(0f, -e * 0.05f * sin(t * 3f)); val s = if (j % 2 == 0) 1f else -1f
            val punta = q + Offset(e * 0.12f, s * e * 0.1f - e * 0.04f)
            val n = Offset(-(punta - q).y, (punta - q).x) * 0.32f
            drawPath(Path().apply { moveTo(q); cubicTo(q + (punta - q) * 0.3f + n, punta - (punta - q) * 0.2f + n * 0.5f, punta); cubicTo(punta - (punta - q) * 0.2f - n * 0.5f, q + (punta - q) * 0.3f - n, q); close() }, p.por(lerp(C.HOJA, C.SELVA, 0.3f + 0.1f * (j % 2)), if (s < 0f) 0.3f else -0.25f))
            drawLine(p.luz(lerp(C.HOJA, C.HOJA_LUZ, 0.4f)).copy(alpha = 0.6f), q, punta, 0.4f) }
    }

    /**
     * Los filodendros que entran por un borde: hojas grandes y partidas en pecíolos largos que salen de [pie] (en
     * metros), cada una con su ángulo: una de frente, otra vuelta que enseña el envés, otra detrás, más chica. No pesan:
     * verdes de charol con su brillo, no una mata oscura.
     */
    private fun DrawScope.filodendros(m: Momento, luz: LuzSelva.Luz, pie: Offset, suelo: Float, tam: Float, semilla: Long, derecha: Boolean) {
        val az = Azar(semilla); val z = pie.y; val e = k(z)
        val base = pc(pie.x, suelo, z)
        val p = pin(luz, pie.x, suelo + 0.8f, z)
        // (dirección del pecíolo, largo relativo, escorzo, si va vuelta)
        val hojas = if (derecha) listOf(Cuatro(-2.05f, 0.8f, 0.7f, false), Cuatro(-2.55f, 1.0f, 0.95f, false), Cuatro(-1.62f, 0.92f, 0.38f, true), Cuatro(-2.95f, 0.7f, 0.85f, false))
            else listOf(Cuatro(-1.15f, 0.9f, 0.85f, false), Cuatro(-0.6f, 0.8f, 0.42f, true))
        for ((a, l0, esc, vuelta) in hojas) {
            val largo = tam * e * l0
            val codo = base + Offset(cos(a) * largo * 0.6f, sin(a) * largo * 0.85f)
            drawPath(Path().apply { moveTo(base); cubicTo(base + Offset(cos(a) * largo * 0.2f, sin(a) * largo * 0.5f), codo + Offset(0f, largo * 0.1f), codo) }, p.medio(lerp(C.HOJA, C.SELVA, 0.35f)), style = Stroke(max(0.8f, e * 0.022f), cap = StrokeCap.Round))
            val b = a + az.entre(-0.2f, 0.2f) + (if (derecha) -0.25f else 0.25f)
            val punta = codo + Offset(cos(b) * largo * 0.72f, sin(b) * largo * 0.22f + largo * 0.34f)
            val al = LuzSelva.alumbrado(luz, pie.x + cos(a) * 0.6f, suelo + 0.9f, z)
            with(PiezasBotanico) { filodendro(if (al > 0.5f) luz.p else p, codo, punta, largo * 0.95f, (0.15f + 0.35f * al + (if (vuelta) 0.25f else 0f)).coerceIn(-1f, 1f), az, 0f, BovedaSelva.contraluz(luz, codo) * 0.5f, lobulos = 6, escorzo = esc, canto = vuelta) }
        }
    }

    private data class Cuatro(val a: Float, val b: Float, val c: Float, val d: Boolean)

    // ---------------------------------------------------------------- Los días señalados

    /** Un libro abierto secándose al sol: las tapas de cuero, las hojas onduladas por el agua, abierto bocabajo o hacia arriba. */
    private fun DrawScope.libroAbierto(luz: LuzSelva.Luz, q: V3, i: Int) {
        val az = Azar(Semilla.de("botanico", "libro", i)); val p = pin(luz, q.x, 0.05f, q.z)
        val s = S.sitio(q.x, q.z, az.entre(-0.6f, 0.6f)); val w = az.entre(0.15f, 0.2f); val d = w * 1.3f
        val tapa = lerp(lerp(C.CUERO, C.TEJA, az.real() * 0.5f), C.ESCARABAJO, if (i % 3 == 2) 0.6f else 0f)
        if (i % 2 == 0) {
            // Abierto hacia arriba: las dos tapas planas y las hojas en abanico, onduladas.
            for (lado in listOf(-1f, 1f)) drawPath(poligono(listOf(V3(0f, 0.004f, -d / 2f), V3(lado * w * 1.05f, 0.004f, -d / 2f), V3(lado * w * 1.05f, 0.004f, d / 2f), V3(0f, 0.004f, d / 2f)).map { s.p(it) }), p.arriba(tapa))
            for (j in 0..5) { val y = 0.012f + j * 0.006f; val lado = if (j % 2 == 0) -1f else 1f; val ond = az.entre(0.004f, 0.02f)
                drawPath(poligono(listOf(V3(0f, y, -d / 2f * 0.92f), V3(lado * w * 0.98f, y + ond, -d / 2f * 0.92f), V3(lado * w * 0.98f, y + ond, d / 2f * 0.92f), V3(0f, y, d / 2f * 0.92f)).map { s.p(it) }), p.arriba(lerp(C.PAPEL, C.ARENA, if (j % 2 == 0) 0.25f else 0.15f)))
                drawLine(p.sombra(C.PAPEL).copy(alpha = 0.5f), s.p(lado * w * 0.98f, y + ond, -d / 2f * 0.92f), s.p(lado * w * 0.98f, y + ond, d / 2f * 0.92f), 0.4f) }
            // Las líneas del texto, en rayas, y una mancha de agua.
            for (j in 0..5) { val z0 = -d / 2f * 0.7f + j * d * 0.14f; drawLine(p.medio(C.BREA).copy(alpha = 0.35f), s.p(-w * 0.85f, 0.05f, z0), s.p(-w * 0.15f, 0.05f, z0), 0.4f); drawLine(p.medio(C.BREA).copy(alpha = 0.35f), s.p(w * 0.15f, 0.05f, z0), s.p(w * 0.85f, 0.05f, z0), 0.4f) }
        } else {
            // Bocabajo, abierto como una tienda: el lomo arriba y las tapas en el suelo.
            val lomo = V3(0f, w * 0.5f, 0f)
            for (lado in listOf(-1f, 1f)) drawPath(poligono(listOf(V3(0f, w * 0.5f, -d / 2f), V3(lado * w * 0.85f, 0f, -d / 2f), V3(lado * w * 0.85f, 0f, d / 2f), V3(0f, w * 0.5f, d / 2f)).map { s.p(it) }), p.por(tapa, if (lado > 0f == p.solALaDerecha) 0.5f else -0.4f))
            drawLine(p.luz(lerp(tapa, C.LATON, 0.4f)), s.p(lomo + V3(0f, 0f, -d / 2f)), s.p(lomo + V3(0f, 0f, d / 2f)), 0.9f)
            drawPath(poligono(listOf(V3(-w * 0.8f, 0.004f, -d / 2f), V3(0f, w * 0.46f, -d / 2f), V3(w * 0.8f, 0.004f, -d / 2f)).map { s.p(it) }), p.por(C.PAPEL, 0.2f))
            // Las hojas hinchadas por el agua asoman por el canto, onduladas.
            for (j in 0..4) drawLine(p.sombra(C.PAPEL).copy(alpha = 0.5f), s.p(-w * 0.75f + j * w * 0.37f, 0.01f, -d / 2f), s.p(0f, w * 0.42f, -d / 2f), 0.4f)
        }
    }

    /** Un papel suelto secándose: algo abarquillado, con sus renglones y el cerco del agua, y una piedra encima. */
    private fun DrawScope.papelAlSol(luz: LuzSelva.Luz, q: V3, i: Int) {
        val az = Azar(Semilla.de("botanico", "papel", i)); val p = pin(luz, q.x, 0.02f, q.z)
        val s = S.sitio(q.x, q.z, az.entre(-0.7f, 0.7f)); val w = 0.11f; val d = 0.15f
        val esquinas = listOf(V3(-w, az.entre(0f, 0.02f), -d), V3(w, az.entre(0.01f, 0.03f), -d), V3(w, az.entre(0f, 0.015f), d), V3(-w, az.entre(0.01f, 0.03f), d))
        val forma = poligono(esquinas.map { s.p(it) })
        drawPath(forma, p.arriba(lerp(C.PAPEL, C.LONA, 0.4f)))
        clipPath(forma) {
            for (j in 0..7) { val z = -d * 0.8f + j * d * 0.22f; drawLine(p.medio(lerp(C.BREA, C.CUERO, 0.3f)).copy(alpha = 0.4f), s.p(-w * 0.8f, 0.012f, z), s.p(w * az.entre(0.3f, 0.8f), 0.012f, z), 0.35f) }
            mancha(s.p(az.entre(-w, w) * 0.5f, 0.01f, az.entre(-d, d) * 0.5f), 4f, 2f, p.medio(lerp(C.PAPEL, C.CUERO, 0.5f)), 0.35f)
        }
        drawPath(forma, p.sombra(C.PAPEL).copy(alpha = 0.4f), style = Stroke(0.4f))
        val c = s.p(az.entre(-0.4f, 0.4f) * w, 0.03f, az.entre(-0.4f, 0.4f) * d); val e = k(q.z) * 0.035f
        drawOval(Brush.horizontalGradient(listOf(p.sombra(C.ROCA), p.luz(C.ROCA)), c.x - e, c.x + e), Offset(c.x - e, c.y - e * 0.6f), Size(e * 2f, e * 1.1f))
    }

    /** El paraguas negro abierto al revés, con las ramitas, las hojas y los escarabajos que cayeron dentro. */
    private fun DrawScope.paraguas(luz: LuzSelva.Luz, q: V3) {
        val p = pin(luz, q.x, 0.3f, q.z); val e = k(q.z); val c = pc(q); val r = 0.45f * e
        val az = Azar(Semilla.de("botanico", "paraguas"))
        val tela = lerp(C.BREA, C.SOMBRA, 0.15f)
        // La copa vuelta hacia arriba: el borde en elipse, con las puntas de las varillas, y el fondo que se ve dentro.
        val boca = Offset(c.x, c.y - r * 0.45f)
        drawPath(Path().apply { moveTo(boca + Offset(-r, 0f)); cubicTo(boca + Offset(-r * 0.8f, r * 0.55f), boca + Offset(r * 0.8f, r * 0.55f), boca + Offset(r, 0f)); close() }, p.por(tela, -0.2f))
        drawOval(p.arriba(lerp(tela, C.LONA, 0.12f)), Offset(boca.x - r, boca.y - r * 0.22f), Size(r * 2f, r * 0.44f))
        drawOval(p.medio(lerp(tela, C.BREA, 0.3f)), Offset(boca.x - r * 0.88f, boca.y - r * 0.17f), Size(r * 1.76f, r * 0.34f))
        for (k in 0 until 8) { val a = (2 * PI * k / 8).toFloat(); drawLine(p.luz(lerp(tela, C.LONA, 0.4f)).copy(alpha = 0.6f), boca, boca + Offset(cos(a) * r * 0.95f, sin(a) * r * 0.21f), 0.4f) }
        // Lo que hay dentro: hojitas, ramas y escarabajos.
        repeat(16) { val a = az.entre(0f, 6.28f); val rr = sqrt01(az.real()) * 0.8f; val qq = boca + Offset(cos(a) * r * rr, sin(a) * r * 0.18f * rr)
            if (it % 3 == 0) drawOval(p.medio(if (it % 2 == 0) C.ESCARABAJO else lerp(C.LATON, C.CUERO, 0.4f)), Offset(qq.x - 1f, qq.y - 0.6f), Size(2f, 1.2f))
            else drawLine(p.por(lerp(C.HOJA, C.CUERO, az.real()), 0.2f), qq, qq + Offset(az.entre(-3f, 3f), az.entre(-1f, 1f)), 1f, StrokeCap.Round) }
        // El mango, de pie en medio, con su puño curvo.
        val m0 = boca + Offset(0f, -r * 0.05f); val m1 = boca + Offset(r * 0.05f, -r * 0.95f)
        drawLine(p.medio(C.MADERA_OSC), m0, m1, max(0.8f, e * 0.012f))
        drawArc(p.medio(C.MADERA_OSC), 180f, 180f, false, Offset(m1.x, m1.y - r * 0.08f), Size(r * 0.16f, r * 0.16f), style = Stroke(max(0.8f, e * 0.012f)))
    }

    private fun sqrt01(v: Float): Float = kotlin.math.sqrt(v.coerceIn(0f, 1f))
}
