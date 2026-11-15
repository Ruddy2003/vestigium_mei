package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.EscenaMongol.Puerta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Corral
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.Postura
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.argal
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.caballo
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.corral
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.ger
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.hogar
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.luzDePuerta
import io.github.ruddy2003.vestigium.ui.escenas.PiezasMongol.oveja
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.borron
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.fijo
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.pts
import io.github.ruddy2003.vestigium.ui.escenas.TrazosValle.rota
import io.github.ruddy2003.vestigium.ui.escenas.ValleCampamento.pielTendida
import io.github.ruddy2003.vestigium.ui.escenas.ValleCampamento.sombraDeMuro
import io.github.ruddy2003.vestigium.ui.escenas.ValleMongol.MIRADA
import io.github.ruddy2003.vestigium.ui.escenas.ValleMongol.SITIO_BAT
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.ALTO
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.capaSombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.cubicTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.curva
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.entre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.lineTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mancha
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.moveTo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.real
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombra
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.sombraArrojada
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.PI
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.sin

/**
 * El primer plano del valle: la terraza de la tienda de Bat, que baja en un talud al campamento. La tienda enmarca por
 * la izquierda y por arriba, con su fieltro gastado y lo que cuelga de su pared (el ronzal, el cubo de ordeñar, la
 * cuerda), que le echa sombra; a su lado, el cofre del objeto del día; más atrás, el hogar de fuera con la olla
 * grande **[Diseño propio]**; a la derecha, la esquina del corral de piedra y estiércol, cortada por el borde, con el
 * montón de argal arrimado. Las cosas pisan a distintas profundidades y se tapan unas a otras (regla 5). La pértiga del
 * lazo cruza en diagonal hasta el borde de arriba (regla 6).
 */
internal object ValleCerca {
    private val C = ColorMongol

    /** El borde de la terraza (el talud que baja al campamento), puesto a mano. */
    val TALUD = pts(140f, 214f, 152f, 210.6f, 164f, 209f, 176f, 207f, 188f, 205.4f, 200f, 204.8f, 212f, 202.6f, 222f, 201.4f, 232f, 202.4f, 244f, 200.6f,
        254f, 201.8f, 264f, 202.6f, 274f, 205.4f, 284f, 207.6f, 296f, 209.8f, 308f, 213f, 320f, 213.6f, 334f, 214.2f, 348f, 216.8f, 362f, 216.2f, 376f, 218f, 392f, 219.4f)

    /** El hogar de fuera, retirado del paso del gato y apartado del caballo atado. */
    val HOGAR = Offset(268f, 249f)

    /** Donde se pone el objeto del día: sobre un cofre bajo, junto a la puerta. */
    val COFRE = Offset(176f, 261f)
    private val SITIO_COFRE = Sitio(MIRADA, COFRE, 86f, 0.85f)
    private const val COFRE_W = 0.37f; private const val COFRE_H = 0.38f; private const val COFRE_D = 0.23f

    /** El montón de argal, arrimado al corral. */
    private val ARGAL = Offset(322f, 245f)

    /** La antorcha de la parición, junto a la cancilla del corral (la parte viva pinta su llama). */
    val ANTORCHA = Offset(286f, 238f)

    /** El poste donde se ata el Negro, junto a la puerta, y dónde pisa el caballo, de cara al poste. */
    private val POSTE = Offset(184f, 212f)
    private val NEGRO = Offset(236f, 209f)
    private const val ALTO_NEGRO = 40f

    /** El caballo del correo los días de noticias: donde el Negro, o detrás de él si el Negro está atado. */
    private fun ajenoEn(negro: Boolean) = if (negro) NEGRO + Offset(-24f, -6f) else NEGRO

    /** La esquina del corral de delante: el pie de la esquina en el lienzo, y el muro que se aleja hacia el centro. */
    private val SITIO_CORRAL = Sitio(MIRADA, Offset(372f, 282f), 74.5f, 0f)
    private val MURO_CORRAL = listOf(Offset(3.4f, 0.5f), Offset(0f, 0f), Offset(-0.2f, 1.1f), Offset(-0.42f, 2.2f), Offset(-0.6f, 3.2f), Offset(3.2f, 2.8f))
    private const val ALTO_CORRAL = 0.74f

    /** Donde pisa la tienda de Bat junto a la puerta (para saber si le da el sol). */
    private val PIE_DE_BAT = Offset(150f, 262f)

    /** La terraza (del talud a quien mira), como forma. */
    fun terraza(): Path = poligono(TALUD + listOf(Offset(392f, ALTO + 2f), Offset(60f, ALTO + 2f), Offset(60f, 230f)))

    /**
     * Lo que está de pie en la terraza, para la luz (`ValleLuz`): el Negro y el caballo ajeno si están, la olla en su
     * hogar, el poste y el cofre. Con su silueta: la sombra de la tarde les sube desde el pie, no los parte.
     */
    fun cosasAlSol(m: Momento, nieve: Float): List<ValleLuz.Cosa> = buildList {
        val negro = EscenaMongol.negroAtado(m)
        if (negro) add(ValleLuz.Cosa(NEGRO, 44f, ALTO_NEGRO, PiezasMongol.siluetaDeCaballo(NEGRO, ALTO_NEGRO, -1f, postura = Postura.DESCANSO)))
        if (EscenaMongol.caballoAjeno(m)) { val q = ajenoEn(negro); val a = if (negro) 34f else 38f; add(ValleLuz.Cosa(q, a, a, PiezasMongol.siluetaDeCaballo(q, a, -1f))) }
        add(ValleLuz.Cosa(HOGAR, 44f, 42f, poligono(HOGAR + Offset(-24f, 2f), HOGAR + Offset(24f, 2f), HOGAR + Offset(23f, -44f), HOGAR + Offset(-23f, -44f))))
        add(ValleLuz.Cosa(POSTE, 4f, 30f, poligono(POSTE + Offset(-2f, 1f), POSTE + Offset(2f, 1f), POSTE + Offset(2f, -31f), POSTE + Offset(-2f, -31f))))
        val s = SITIO_COFRE
        val esquinas = listOf(-COFRE_W, COFRE_W).flatMap { x -> listOf(0f, COFRE_H + 0.12f).flatMap { y -> listOf(-COFRE_D, COFRE_D).map { z -> s.p(x, y, z) } } }
        add(ValleLuz.Cosa(COFRE, 40f, 36f, poligono(envolvente(esquinas))))
    }

    /** La silueta de la tienda de Bat: la sombra grande de la tarde le sube desde abajo (ver `ValleLuz`). */
    fun siluetaDeBat(m: Momento, nieve: Float): Path = PiezasMongol.silueta(bat(m, nieve))

    /** La tienda de Bat. */
    private fun bat(m: Momento, nieve: Float) = Ger(SITIO_BAT, Semilla.de("mongol", "tienda", "BAT"), 0.45f, EscenaMongol.puerta(m), EscenaMongol.fuegoDentro(m), nieve, EscenaMongol.armado(m), radio = 3f)

    /** Cuánta luz cálida de la puerta llega a lo que está junto a ella, de 0 a 1. */
    private fun luzDeLaPuerta(m: Momento, g: Ger, l: io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces): Float =
        l.noche * g.dentro * when (g.puerta) { Puerta.ABIERTA -> 1f; Puerta.ENTREABIERTA -> 0.75f; Puerta.CERRADA -> 0.12f }

    // ---------------------------------------------------------------- La terraza

    fun DrawScope.primerPlano(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val l = p.l; val az = fijo("cerca")
        val g = bat(m, nieve)
        val calida = luzDeLaPuerta(m, g, l)
        terraza(m, p, sol, nieve, az)
        // De noche, la luna platea la terraza; delante de la puerta manda la lumbre (su charco va después, encima).
        with(ValleLuz) { plata(terraza(), l, nieve, 1f, pareja = true) }
        // Lo pintado se enciende donde le da el sol: la puerta y la tienda por la mañana; el cofre cuando la sombra grande
        // no lo tapa (ver `ValleLuz`).
        val sv = ValleLuz.sombraDelValle(l)
        // El corral de delante: el estiércol pisado de dentro.
        val dentro = poligono(MURO_CORRAL.map { SITIO_CORRAL.p(it.x, 0f, it.y) })
        drawPath(dentro, p.arriba(lerp(C.ESTIERCOL, C.NIEVE, nieve * 0.3f), 0.02f))
        clipPath(dentro) {
            repeat(46) { val c = Offset(az.entre(290f, 390f), az.entre(222f, 285f)); drawLine(p.luz(lerp(C.ESTEPA, C.ORO, 0.2f)).copy(alpha = 0.65f), c, c + Offset(az.entre(-5f, 5f), az.entre(-0.9f, 0.9f)), 0.55f, StrokeCap.Round) }
            repeat(10) { val c = Offset(az.entre(300f, 390f), az.entre(226f, 285f)); drawPath(borron(c, az.entre(1.6f, 3f), az.entre(0.8f, 1.3f), az, 6, 0.3f), p.por(C.ESTIERCOL, -0.3f)) }
        }
        val corral = Corral(SITIO_CORRAL, MURO_CORRAL, Semilla.de("mongol", "corral", "cerca"), ALTO_CORRAL, 0.5f, abierto = 2, nieve = nieve)
        // Lo que se pinta de atrás adelante, por dónde pisa.
        val pie = apoyoPie(g)
        val grande = ObjetosMongol.grande(m, p.con(calida * 0.8f), pie, pie + Offset(-14f, -72f), 1.1f)
        val negro = EscenaMongol.negroAtado(m); val ajeno = EscenaMongol.caballoAjeno(m)
        val cosas = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        cosas += parte(HOGAR.y) { hogar(p.con(calida * 0.35f), HOGAR, 60f, EscenaMongol.fuegoFuera(m), Azar(Semilla.de("mongol", "hogar")), 0f, true, nieve); ceniza(m, p, nieve) }
        // El cofre, y encima el objeto del día, alumbrado por la puerta de noche.
        val pCofre = if (calida > 0.05f) p.con(calida * 0.85f) else ValleLuz.alSol(p, sv, COFRE, arriba = true)
        cosas += parte(COFRE.y) { cofre(m, pCofre, sol, nieve); with(ObjetosMongol) { sobreElSoporte(m, pCofre, SITIO_COFRE.p(0f, COFRE_H, 0f), 1.35f) } }
        cosas += parte(ARGAL.y) { argal(p, ARGAL, 46f, Azar(Semilla.de("mongol", "argal")), 0f, nieve) }
        cosas += parte(POSTE.y) { poste(p, sol, negro || ajeno) }
        // El Negro, de noche, descansa atado junto a la puerta: la cabeza baja, una pata de atrás doblada, y la luz de la
        // puerta le saca un filo cálido del lado de ella.
        val umbral = g.umbral.x
        // De noche, a dos luces: la lumbre de la puerta por su lado y la luna, plata, por el otro; y su aliento con frío.
        val frio = ValleLuz.frio(m, l)
        if (negro) cosas += parte(NEGRO.y) {
            caballo(p.con((0.3f * calida + 0.08f * l.noche).coerceAtMost(1f)), NEGRO, ALTO_NEGRO, -1f, PiezasMongol.NEGRO, postura = Postura.DESCANSO, lumbreX = umbral)
            with(ValleLuz) {
                filoDeLuna(PiezasMongol.siluetaDeCaballo(NEGRO, ALTO_NEGRO, -1f, postura = Postura.DESCANSO), l, 0.9f, 0.85f)
                aliento(l, PiezasMongol.hocicoDe(NEGRO, ALTO_NEGRO, -1f, Postura.DESCANSO), -1f, 4f, frio)
            }
        }
        if (ajeno) cosas += parte(NEGRO.y - (if (negro) 6f else 0f)) {
            caballo(p.con(0.4f * calida), ajenoEn(negro), if (negro) 34f else 38f, -1f, PiezasMongol.ALAZAN, silla = true, cintas = ValleMongol.cintasAzules(m), postura = Postura.DE_PIE, lumbreX = umbral)
            with(ValleLuz) { filoDeLuna(PiezasMongol.siluetaDeCaballo(ajenoEn(negro), if (negro) 34f else 38f, -1f), l, 1f) }
        }
        if (grande != null) cosas += parte(grande.pie.y) { grande.pintar(this) }
        if (ValleMongol.cuencoEnLaNieve(m)) cosas += parte(270f) { cuenco(p.con(calida), Offset(132f, 271f)) }
        if (ValleMongol.corderos(m) > 0) cosas += parte(268f) { corderos(p, az) }
        if (ValleMongol.pielDeCiervo(m)) cosas += parte(HOGAR.y + 6f) { cuernos(p, HOGAR + Offset(40f, 8f)) }
        // Las sombras en el suelo: la tienda, el corral, el hogar, el cofre, el argal, los caballos, el poste y la pértiga.
        // Las sombras arrojadas: las del sol o, de noche, las de la luna (frías, hacia el otro lado).
        val so = ValleLuz.solDeSombras(sol, l)
        capaSombra(so.r) { c ->
            sombra(PiezasMongol.sombraDeGer(g, so), c)
            sombraDeMuro(SITIO_CORRAL, MURO_CORRAL.take(5), ALTO_CORRAL, so, c)
            sombraDeCaja(SITIO_COFRE, COFRE_W, COFRE_H, COFRE_D, so, c)
            sombraArrojada(so.r, poligono(HOGAR + Offset(-22f, 0f), HOGAR + Offset(22f, 0f), HOGAR + Offset(20f, -42f), HOGAR + Offset(-20f, -42f)), HOGAR, c, 200f, 200f)
            sombraArrojada(so.r, poligono(ARGAL + Offset(-27f, 0f), ARGAL + Offset(48f, 0f), ARGAL + Offset(46f, -34f), ARGAL + Offset(-2f, -28f)), ARGAL, c, 200f, 200f)
            sombraArrojada(so.r, poligono(POSTE + Offset(-1.2f, 0f), POSTE + Offset(1.2f, 0f), POSTE + Offset(1f, -30f), POSTE + Offset(-1f, -30f)), POSTE, c, 200f, 200f)
            if (negro) sombraArrojada(so.r, PiezasMongol.siluetaDeCaballo(NEGRO, ALTO_NEGRO, -1f, postura = Postura.DESCANSO), NEGRO, c, 200f, 200f)
            if (ajeno) { val q = ajenoEn(negro); sombraArrojada(so.r, PiezasMongol.siluetaDeCaballo(q, if (negro) 34f else 38f, -1f), q, c, 200f, 200f) }
            // La del objeto grande: en el suelo y, lo que alcance, subida a la pared de la tienda en la que se apoya.
            if (grande != null) sombraArrojada(so.r, grande.forma, grande.pie, c, (grande.pie.y - 5f).coerceIn(130f, ALTO), 124f, 0f, 150f)
            if (!hoyEsElLazo(m)) sombraDePertiga(so, c)
        }
        // La luz de la puerta en el suelo (de noche) y el resplandor del hogar.
        luzDePuerta(g, l.noche * g.dentro * 1.1f, 4.2f)
        if (calida > 0.05f) resplandor(g.umbral + Offset(10f, -4f), 46f, 0.5f * calida, C.LUMBRE)
        val ff = EscenaMongol.fuegoFuera(m)
        if (ff > 0.05f) resplandor(HOGAR + Offset(0f, -6f), 80f, 0.55f * maxOf(l.noche, 0.5f * ValleMongol.cubierto(l)) * ff, C.LUMBRE)
        else if (l.noche > 0.3f) resplandor(HOGAR + Offset(0f, -2f), 16f, 0.5f * l.noche * ValleMongol.rescoldo(m), C.LUMBRE)
        if (ValleMongol.antorchas(m)) resplandor(ANTORCHA + Offset(0f, -26f), 60f, 0.6f * l.noche, C.LUMBRE)
        // La tienda, con su fieltro gastado y lo que cuelga de su pared, y la sombra que eso echa en el fieltro.
        val pBat = ValleLuz.alSol(p, sv, PIE_DE_BAT, arriba = true)
        ger(g, pBat, sol)
        if (g.fieltro >= 0.999f) { fieltro(g, pBat, sol, nieve, az); puertaDeBat(m, g, ValleLuz.alSol(p, (1f - ValleLuz.sombraEn(sv, PIE_DE_BAT, arriba = true)) * 1.6f), sol); colgado(m, g, p.con(calida * 0.5f), sol); cintasDePuerta(m, g, p) }
        // El corral de delante y lo que hay en él.
        corral(corral, p, sol)
        with(ValleLuz) { filoDelMuro(MURO_CORRAL.take(5).map { SITIO_CORRAL.p(it.x, ALTO_CORRAL, it.y) }, l, 1f) }
        cosas += rincon(m, p, sol)
        for ((_, pintar) in cosas.sortedBy { it.first }) pintar()
    }

    /** La terraza: tierra pisada y nieve sucia; su borde, un talud en sombra con piedras y matas; las sendas, las huellas y la paja. */
    private fun DrawScope.terraza(m: Momento, p: Pincel, sol: Sol3, nieve: Float, az: Azar) {
        val seco = lerp(ValleRio.hierba(m), C.TIERRA, 0.3f)
        val suelo = if (nieve > 0.55f) C.NIEVE else seco
        val forma = poligono(TALUD + listOf(Offset(392f, ALTO + 2f), Offset(60f, ALTO + 2f), Offset(60f, 230f)))
        drawPath(forma, Brush.verticalGradient(0f to p.arriba(suelo, 0.04f), 0.6f to p.por(suelo, 0.25f), 1f to p.por(suelo, 0.05f), startY = 200f, endY = ALTO))
        // El talud: tierra en sombra, rota, con piedras que asoman y la nieve que se queda en el canto.
        val canto = rota(TALUD, az, 0.5f, 2)
        val talud = poligono(canto.map { it + Offset(0f, -0.2f) } + canto.reversed().mapIndexed { k, q -> q + Offset(0f, 3f + 2.2f * sin(k * 1.3f) * sin(k * 0.7f)) })
        drawPath(talud, p.por(lerp(C.TIERRA, C.ESTIERCOL, 0.3f), sol.t(V3(0f, 0.2f, -1f)) - 0.3f, 0.05f))
        for ((k, q) in canto.withIndex()) if (k % 3 == 0) { val rr = az.entre(1.4f, 3.2f); drawOval(p.por(C.PIEDRA, -0.5f), Offset(q.x - rr, q.y + 0.8f), Size(rr * 2f, rr * 1.1f)); drawOval(p.por(lerp(C.PIEDRA, C.NIEVE, nieve), 0.7f), Offset(q.x - rr * 0.8f, q.y + 0.5f), Size(rr * 1.4f, rr * 0.5f)) }
        drawPath(curva(canto), p.luz(lerp(suelo, Color.White, 0.3f), 0.04f).copy(alpha = 0.75f), style = Stroke(1f))
        clipPath(forma) {
            // La tierra pisada y la nieve sucia de delante de la puerta y alrededor del hogar.
            val cub = ValleMongol.cubierto(p.l)
            val pisado = lerp(lerp(C.TIERRA, C.ESTIERCOL, 0.45f + 0.25f * cub), C.NIEVE, nieve * 0.4f * (1f - 0.5f * cub))
            drawPath(borron(Offset(196f, 254f), 92f, 22f, az, 14, 0.18f, 0.7f), p.arriba(pisado).copy(alpha = 0.55f))
            drawPath(borron(HOGAR + Offset(0f, 2f), 46f, 11f, az, 12, 0.2f, 0.7f), p.arriba(lerp(pisado, C.ESTIERCOL, 0.3f)).copy(alpha = 0.55f))
            // Las sendas pisadas: de la puerta al talud (y bajando al campamento) y de la puerta a la cancilla del corral.
            for (r in listOf(pts(140f, 266f, 168f, 260f, 196f, 246f, 214f, 230f, 226f, 216f, 234f, 205f), pts(150f, 274f, 200f, 270f, 250f, 262f, 290f, 250f, 314f, 240f))) {
                val rr = rota(r, az, 1.6f, 2)
                // Una cinta que se ensancha hacia quien mira, de orillas rotas: la nieve hundida y sucia, con su canto en sombra.
                fun orilla(lado: Float) = rr.mapIndexed { i, q -> val a = rr[maxOf(i - 1, 0)]; val b = rr[minOf(i + 1, rr.size - 1)]; val d = b - a; val n = Offset(-d.y, d.x) / d.getDistance().coerceAtLeast(0.01f); val k = ((q.y - 200f) / 80f).coerceIn(0f, 1f); q + n * lado * (3f + 8f * k) * az.entre(0.8f, 1.2f) }
                val izq = orilla(1f); val der = orilla(-1f)
                val cinta = poligono(izq + der.reversed())
                drawPath(cinta, p.sombra(pisado, 0.03f).copy(alpha = 0.45f), style = Stroke(1.6f))
                drawPath(cinta, p.arriba(lerp(pisado, C.ESTIERCOL, 0.2f), 0.03f).copy(alpha = 0.7f))
                drawPath(Path().apply { moveTo(izq[0]); for (q in izq.drop(1)) lineTo(q) }, p.sombra(C.NIEVE).copy(alpha = 0.5f * nieve), style = Stroke(1.2f))
                // Las huellas de los cascos, en fila a lo largo de la senda, de un lado y del otro: hoyos con su sombra azul
                // y el canto de luz del lado del sol, más grandes cerca.
                for (i in 0 until rr.size - 1) {
                    val a = rr[i]; val b = rr[i + 1]; val d = b - a; val largo = d.getDistance().coerceAtLeast(0.01f); val n = Offset(-d.y, d.x) / largo
                    var s = az.entre(0f, 4f)
                    while (s < largo) {
                        val c0 = a + d * (s / largo); val k = ((c0.y - 200f) / 80f).coerceIn(0f, 1f); val w = 0.9f + 1.5f * k
                        val c = c0 + n * ((if ((s / 5f).toInt() % 2 == 0) 1f else -1f) * (1.5f + 2.5f * k)) + Offset(az.entre(-0.6f, 0.6f), 0f)
                        drawOval(p.sombra(lerp(pisado, C.NIEVE, 0.5f)).copy(alpha = 0.6f), Offset(c.x - w, c.y - w * 0.36f), Size(w * 2f, w * 0.72f))
                        drawArc(p.luz(C.NIEVE).copy(alpha = 0.7f * nieve), if (p.solALaDerecha) 270f else 90f, 180f, false, Offset(c.x - w, c.y - w * 0.36f), Size(w * 2f, w * 0.72f), style = Stroke(0.4f))
                        s += az.entre(4f, 7f) * (0.7f + 0.6f * k)
                    }
                }
            }
            // Las rachas del viento en la nieve, cada vez más grandes hacia quien mira.
            if (nieve > 0.3f) repeat(30) {
                val c = Offset(az.entre(110f, 390f), az.entre(210f, 285f)); val k = (c.y - 200f) / 85f; val w = az.entre(8f, 22f) * (0.5f + k)
                drawPath(Path().apply { moveTo(c); cubicTo(c + Offset(w * 0.3f, -1.4f * k - 0.5f), c + Offset(w * 0.7f, -1f * k - 0.3f), c + Offset(w, 0.4f)) }, p.sombra(C.NIEVE).copy(alpha = 0.5f * nieve), style = Stroke(0.5f + 0.7f * k, cap = StrokeCap.Round))
            }
            // Sin nieve: la hierba pisada y rala de la terraza, a manchas, con sus matas y la escarcha de las mañanas.
            if (nieve < 0.55f) {
                repeat(22) { val c = Offset(az.entre(100f, 390f), az.entre(208f, 285f)); val k = (c.y - 200f) / 85f
                    drawPath(borron(c, az.entre(10f, 30f) * (0.5f + k), az.entre(2f, 5f) * (0.5f + k), az, 9, 0.35f), (if (it % 2 == 0) p.luz(lerp(seco, C.ESTEPA, 0.5f)) else p.sombra(lerp(seco, C.CUERO, 0.3f))).copy(alpha = 0.4f)) }
                repeat(36) { val c = Offset(az.entre(100f, 390f), az.entre(210f, 285f)); val k = (c.y - 200f) / 85f; val w = 1.6f + 3f * k
                    drawOval(p.r.color.copy(alpha = 0.45f * p.r.fuerza), Offset(c.x - w + p.r.ax.coerceIn(-2f, 2f) * w * 0.5f, c.y - w * 0.15f), Size(w * 2.4f, w * 0.45f), blendMode = BlendMode.Multiply)
                    for (j in 0..5) drawLine(p.por(lerp(seco, C.ESTEPA, 0.4f), if (j % 2 == 0) 0.5f else -0.4f), c + Offset((j - 2.5f) * w * 0.25f, 0f), c + Offset((j - 2.5f) * w * 0.45f + az.entre(-0.5f, 0.5f), -w * az.entre(0.8f, 1.4f)), 0.5f + 0.2f * k, StrokeCap.Round) }
                if (EscenaMongol.helada(m)) drawPath(forma, Brush.verticalGradient(0f to p.luz(C.NIEVE).copy(alpha = 0.18f), 1f to p.luz(C.NIEVE).copy(alpha = 0.3f), startY = 200f, endY = ALTO))
            }
            // La paja que se cae del corral y del argal, las boñigas sueltas con su sombra.
            repeat(44) { val c = Offset(az.entre(220f, 340f), az.entre(226f, 285f)); drawLine(p.luz(lerp(C.ESTEPA, C.ORO, 0.25f)).copy(alpha = 0.7f), c, c + Offset(az.entre(-4f, 4f), az.entre(-0.8f, 0.8f)), 0.5f, StrokeCap.Round) }
            repeat(12) {
                val c = Offset(az.entre(150f, 330f), az.entre(222f, 284f)); val rr = az.entre(1.6f, 3.2f) * (0.6f + (c.y - 220f) / 100f)
                drawOval(p.r.color.copy(alpha = 0.55f * p.r.fuerza), Offset(c.x - rr + p.r.ax.coerceIn(-2f, 2f) * rr * 0.4f, c.y - rr * 0.1f), Size(rr * 2.4f, rr * 0.6f), blendMode = BlendMode.Multiply)
                drawPath(borron(c + Offset(0f, -rr * 0.3f), rr, rr * 0.5f, az, 7, 0.25f, 0.8f), p.por(C.ESTIERCOL, -0.2f))
                drawOval(p.luz(lerp(C.ESTIERCOL, C.ESTEPA, 0.4f)).copy(alpha = 0.6f), Offset(c.x - rr * 0.6f, c.y - rr * 0.75f), Size(rr * 1.1f, rr * 0.36f))
            }
        }
    }

    /** La ceniza que se tira del hogar, a un lado, sobre la nieve. */
    private fun DrawScope.ceniza(m: Momento, p: Pincel, nieve: Float) {
        val az = fijo("ceniza")
        drawPath(borron(HOGAR + Offset(-34f, 8f), 14f, 3f, az, 9, 0.3f, 0.7f), p.arriba(lerp(C.PIEDRA, C.HOLLIN, 0.25f)).copy(alpha = 0.75f))
        drawPoints((0 until 18).map { HOGAR + Offset(-34f + az.entre(-16f, 16f), 8f + az.entre(-3f, 3f)) }, PointMode.Points, p.medio(C.HOLLIN).copy(alpha = 0.6f), 0.9f, StrokeCap.Round)
    }

    /** El cofre bajo de junto a la puerta, donde se deja el objeto del día: rojo, con flejes de cobre y una espiral gastada. */
    private fun DrawScope.cofre(m: Momento, p: Pincel, sol: Sol3, nieve: Float) {
        val s = SITIO_COFRE; val w = COFRE_W; val h = COFRE_H; val d = COFRE_D
        val az = Azar(Semilla.de("mongol", "cofre", "uso"))
        // Un cofre viejo: el rojo apagado por el sol y el humo, la madera que asoma, los herrajes de hierro mate.
        val pintura = lerp(lerp(C.ROJO, C.MADERA, 0.32f), C.FIELTRO_VIEJO, 0.16f); val madera = lerp(C.MADERA, C.TIERRA, 0.25f)
        val hierro = lerp(lerp(C.ORO, C.HOLLIN, 0.45f), C.PIEDRA, 0.2f)
        val caras = listOf(
            listOf(V3(-w, 0f, -d), V3(w, 0f, -d), V3(w, h, -d), V3(-w, h, -d)) to V3(0f, 0f, -1f),
            listOf(V3(w, 0f, -d), V3(w, 0f, d), V3(w, h, d), V3(w, h, -d)) to V3(1f, 0f, 0f),
            listOf(V3(-w, 0f, -d), V3(-w, 0f, d), V3(-w, h, d), V3(-w, h, -d)) to V3(-1f, 0f, 0f),
            listOf(V3(-w, h, -d), V3(w, h, -d), V3(w, h, d), V3(-w, h, d)) to V3(0f, 1f, 0f),
        )
        // Dos patas cortas, de tarugo, que lo separan de la nieve.
        for (x in listOf(-w + 0.04f, w - 0.04f)) drawRect(p.sombra(C.MADERA_OSC), s.p(x - 0.03f, 0.05f, -d + 0.02f), Size(0.06f * s.k, 0.05f * s.k))
        for ((pts, n) in caras) {
            if (!s.seVe(pts[0] + (pts[2] - pts[0]) * 0.5f, n)) continue
            val f = poligono(pts.map { s.p(it) })
            val tt = sol.t(s.normal(n)) + (if (n.y > 0f) 0.3f else 0f)
            drawPath(f, p.por(pintura, tt))
            clipPath(f) {
                val a = s.p(pts[0]); val b = s.p(pts[1]); val c = s.p(pts[3])
                // La veta de la madera bajo la pintura, y la pintura saltada: manchas de madera desnuda.
                for (k in 1..6) drawLine(p.por(lerp(pintura, madera, 0.5f), tt - 0.3f).copy(alpha = 0.35f), a + (c - a) * (k / 7f), b + (c - a) * (k / 7f + 0.02f), 0.4f)
                repeat(5) { val q = a + (b - a) * az.entre(0.05f, 0.95f) + (c - a) * az.entre(0.05f, 0.9f); drawPath(borron(q, az.entre(1.4f, 4f), az.entre(0.6f, 1.6f), az, 6, 0.4f), p.por(madera, tt).copy(alpha = 0.75f)) }
                // Los rasguños y el roce de los cantos: la madera clara asoma en el borde de arriba y en las esquinas.
                repeat(6) { val q = a + (b - a) * az.real() + (c - a) * az.real(); drawLine(p.luz(madera).copy(alpha = 0.45f), q, q + Offset(az.entre(-3f, 3f), az.entre(-0.8f, 0.8f)), 0.4f) }
                drawLine(p.luz(madera).copy(alpha = 0.75f), c, c + (b - a), 1.6f)
                drawLine(p.luz(madera).copy(alpha = 0.6f), a, c, 1.2f)
                // La mugre de abajo: el barro que salpica.
                drawPath(poligono(a, b, b + (c - a) * 0.22f, a + (c - a) * 0.22f), p.sombra(lerp(C.TIERRA, C.ESTIERCOL, 0.4f)).copy(alpha = 0.45f))
                // Los flejes de hierro, mates, con su clavazón.
                for (u in listOf(0.12f, 0.88f)) { drawLine(p.por(hierro, tt - 0.1f), a + (b - a) * u, c + (b - a) * u, 2f); for (k in 1..3) drawCircle(p.por(hierro, tt + 0.4f).copy(alpha = 0.8f), 0.5f, a + (b - a) * u + (c - a) * (k / 4f)) }
                if (n.y == 0f && n.z != 0f) {
                    // La espiral del frente, casi borrada, y la cerradura.
                    val m0 = a + (b - a) * 0.5f + (c - a) * 0.52f
                    for (r in listOf(5.4f, 3.4f, 1.6f)) drawArc(p.por(lerp(C.ORO, pintura, 0.5f), tt).copy(alpha = 0.45f), 30f * r, 290f, false, m0 - Offset(r, r), Size(r * 2f, r * 2f), style = Stroke(0.8f))
                    drawRect(p.por(hierro, tt), a + (b - a) * 0.46f + (c - a) * 0.86f, Size(4f, 3.6f))
                }
            }
            drawPath(f, p.sombra(lerp(pintura, C.HOLLIN, 0.35f)).copy(alpha = 0.6f), style = Stroke(0.5f))
        }
        // Una piel de oveja doblada encima, donde se apoya lo que se deja.
        val tapa = poligono(listOf(s.p(-w * 0.86f, h, -d * 0.7f), s.p(w * 0.9f, h, -d * 0.8f), s.p(w * 0.84f, h, d * 0.8f), s.p(-w * 0.8f, h, d * 0.7f)))
        drawPath(tapa, p.arriba(lerp(C.LANA, C.FIELTRO, 0.4f)))
        drawPath(Path().apply { moveTo(s.p(-w * 0.86f, h, -d * 0.7f)); lineTo(s.p(-w * 0.9f, h - 0.08f, -d * 0.75f)); lineTo(s.p(w * 0.92f, h - 0.1f, -d * 0.85f)); lineTo(s.p(w * 0.9f, h, -d * 0.8f)); close() }, p.por(C.LANA, sol.t(s.normal(V3(0f, 0f, -1f))) - 0.1f))
        clipPath(tapa) { repeat(24) { val q = s.p(az.entre(-w, w), h, az.entre(-d, d)); drawCircle(p.luz(lerp(C.LANA, Color.White, 0.3f)).copy(alpha = 0.5f), 0.9f, q); drawCircle(p.sombra(C.LANA).copy(alpha = 0.3f), 0.8f, q + Offset(0.3f, 0.6f)) } }
        // La nieve que se queda en la tapa, por detrás de la piel y en las esquinas, blanda.
        if (nieve > 0.2f) {
            for ((x, z, r) in listOf(Triple(0f, d * 0.9f, w * 0.9f), Triple(-w * 0.92f, -d * 0.2f, w * 0.18f), Triple(w * 0.92f, d * 0.3f, w * 0.2f))) {
                val c = s.p(x, h + 0.015f, z)
                drawPath(TrazosValle.blando(c, r * s.k * 0.55f, 0.05f * s.k * nieve + 0.6f, az, 8, 0.2f, 0.7f), p.arriba(C.NIEVE).copy(alpha = (0.5f + 0.5f * nieve).coerceAtMost(1f)))
            }
        }
    }

    /** La sombra de una caja de [w] × [h] × [d] metros (medidas a cada lado del centro) en el suelo. */
    private fun DrawScope.sombraDeCaja(s: Sitio, w: Float, h: Float, d: Float, sol: Sol3, c: Color) {
        val pts = ArrayList<Offset>()
        for (x in listOf(-w, w)) for (z in listOf(-d, d)) for (y in listOf(0f, h)) pts += s.pm(s.mx(x, z) + sol.sx * y, 0f, s.mz(x, z) + sol.sz * y)
        drawPath(poligono(envolvente(pts)), c)
    }

    /** El poste de atar los caballos, junto a la puerta, con la cuerda de crin. */
    private fun DrawScope.poste(p: Pincel, sol: Sol3, atado: Boolean) {
        val madera = lerp(C.MADERA, C.PIEDRA, 0.3f); val lado = if (p.solALaDerecha) 1f else -1f
        drawLine(p.sombra(madera), POSTE, POSTE + Offset(1f, -30f), 3f, StrokeCap.Round)
        drawLine(p.luz(madera), POSTE + Offset(lado * 0.8f, 0f), POSTE + Offset(1f + lado * 0.8f, -30f), 1f, StrokeCap.Round)
        drawLine(p.sombra(madera), POSTE + Offset(-4f, -28f), POSTE + Offset(6f, -31f), 2f, StrokeCap.Round)
        for (k in 0..2) drawLine(p.medio(C.CRIN), POSTE + Offset(-1.6f, -22f + k * 1.4f), POSTE + Offset(2.6f, -21f + k * 1.4f), 0.9f)
        val a = POSTE + Offset(1f, -21f)
        // La cuerda: al ronzal del caballo, o colgando hasta el suelo si no hay caballo.
        val b = if (atado) PiezasMongol.hocicoDe(NEGRO, ALTO_NEGRO, -1f, Postura.DESCANSO) + Offset(2f, -1f) else POSTE + Offset(9f, -2f)
        drawPath(Path().apply { moveTo(a); cubicTo(a + Offset(4f, 8f), b + Offset(-4f, 6f), b) }, p.medio(C.CRIN), style = Stroke(0.8f))
        if (!atado) drawPath(Path().apply { moveTo(b); cubicTo(b + Offset(3f, 1f), b + Offset(6f, -0.5f), b + Offset(8f, 1f)) }, p.medio(C.CRIN), style = Stroke(0.8f))
    }

    /** El cuenco de leche que se deja fuera, en la nieve, el mes blanco: de madera, con el borde azul. */
    private fun DrawScope.cuenco(p: Pincel, c: Offset) {
        val k = 1.5f
        drawOval(p.r.color.copy(alpha = 0.5f * p.r.fuerza), Offset(c.x - 4f * k + p.r.ax.coerceIn(-2f, 2f) * 2f * k, c.y - 0.8f * k), Size(10f * k, 2.4f * k), blendMode = BlendMode.Multiply)
        drawPath(Path().apply { moveTo(c + Offset(-5f, -4.4f) * k); cubicTo(c + Offset(-4.6f, -0.4f) * k, c + Offset(4.6f, -0.4f) * k, c + Offset(5f, -4.4f) * k); close() }, p.por(C.MADERA, if (p.solALaDerecha) 0.3f else -0.3f))
        drawOval(p.medio(C.AZUL), Offset(c.x - 5.2f * k, c.y - 5.6f * k), Size(10.4f * k, 2.6f * k))
        drawOval(p.luz(lerp(C.FIELTRO, Color.White, 0.6f)), Offset(c.x - 4.3f * k, c.y - 5.2f * k), Size(8.6f * k, 1.8f * k))
        // Un hueco en la nieve alrededor, donde se apoyó.
        drawArc(p.sombra(C.NIEVE).copy(alpha = 0.6f), 0f, 180f, false, Offset(c.x - 7f * k, c.y - 2f * k), Size(14f * k, 3.4f * k), style = Stroke(0.7f))
    }

    /** Los corderos más débiles de la parición, echados junto a la puerta sobre un poco de paja. */
    private fun DrawScope.corderos(p: Pincel, az: Azar) {
        val a = Azar(Semilla.de("mongol", "corderos", "puerta")); val c = CORDEROS
        drawPath(borron(c, 20f, 4.4f, a, 10, 0.3f, 0.7f), p.arriba(lerp(C.ESTEPA, C.ORO, 0.25f)).copy(alpha = 0.85f))
        oveja(p, c + Offset(-8f, 2f), 15f, 1f, a, pasta = true, patas = false, cordero = true)
        oveja(p, c + Offset(10f, 0f), 14f, -1f, a, pasta = false, patas = true, cordero = true)
    }

    /** Dónde se echan los corderos más débiles de la parición, sobre su paja, junto al hogar. */
    val CORDEROS = Offset(232f, 268f)

    /** Los cuernos del ciervo de la cacería, junto al hogar. */
    private fun DrawScope.cuernos(p: Pincel, c: Offset) {
        val hueso = lerp(C.FIELTRO, C.MADERA, 0.35f)
        for (s in listOf(-1f, 1f)) {
            val base = c + Offset(s * 2f, 0f); val punta = base + Offset(s * 9f, -12f)
            drawPath(Path().apply { moveTo(base); cubicTo(base + Offset(s * 2f, -5f), punta + Offset(-s * 3f, 4f), punta) }, p.por(hueso, s * 0.3f), style = Stroke(1.4f, cap = StrokeCap.Round))
            for (k in 1..3) { val q = base + (punta - base) * (k / 4f); drawLine(p.por(hueso, 0.2f), q, q + Offset(-s * 1.5f, -4f), 0.9f, StrokeCap.Round) }
        }
        drawOval(p.por(hueso, 0.3f), Offset(c.x - 2.6f, c.y - 2f), Size(5.2f, 2.6f))
    }

    /**
     * El rincón de delante del corral, según el día: la piel del ciervo tendida en el muro (en la cacería); la yegua baya
     * con el potro blanco recién nacido, sobre paja limpia (su día); en la parición, ovejas con sus corderos. Pisan
     * delante del muro, junto a la esquina, fuera del paso del gato. Devuelve lo que hay que pintar, por dónde pisa.
     */
    private fun rincon(m: Momento, p: Pincel, sol: Sol3): List<Pair<Float, DrawScope.() -> Unit>> {
        val a = Azar(Semilla.de("mongol", "rincon", m.ficha.dia))
        val lista = ArrayList<Pair<Float, DrawScope.() -> Unit>>()
        if (ValleMongol.potroEnElRincon(m)) {
            lista += parte(262f) { drawPath(borron(Offset(338f, 270f), 30f, 6f, a, 10, 0.3f, 0.7f), p.arriba(lerp(C.ESTEPA, C.ORO, 0.3f))) }
            lista += parte(268f) { caballo(p, Offset(352f, 268f), 46f, -1f, PiezasMongol.BAYO, pasta = true) }
            lista += parte(274f) { caballo(p, Offset(330f, 275f), 32f, -1f, PiezasMongol.BLANCO, potro = true) }
        } else if (ValleMongol.corderos(m) > 0) {
            lista += parte(262f) { drawPath(borron(Offset(338f, 270f), 28f, 6f, a, 10, 0.3f, 0.7f), p.arriba(lerp(C.ESTEPA, C.ORO, 0.3f))) }
            for ((j, q) in listOf(Offset(340f, 266f), Offset(362f, 272f)).withIndex()) lista += parte(q.y) { oveja(p, q, 30f, if (j == 0) -1f else 1f, a, pasta = j == 1) }
            for ((j, q) in listOf(Offset(326f, 274f), Offset(348f, 278f)).withIndex()) lista += parte(q.y) { oveja(p, q, 15f, if (j == 0) 1f else -1f, a, pasta = j == 0, cordero = true) }
        }
        if (ValleMongol.pielDeCiervo(m)) lista += parte(200f) { pielTendida(p, SITIO_CORRAL.p(-0.32f, ALTO_CORRAL + 0.02f, 1.7f), 26f, lerp(C.CUERO, C.ORO, 0.25f), 0f) }
        return lista
    }

    // ---------------------------------------------------------------- La tienda de Bat: el fieltro gastado y lo que cuelga

    /** Dónde pisa y dónde se apoya un objeto grande del día: contra la pared de la tienda, a la izquierda de la puerta. */
    private fun apoyoPie(g: Ger): Offset = g.pared(-0.36f, 0f, g.radio + 0.2f)

    /** La forma de la pared que se ve. */
    private fun pared(g: Ger, n: Int = 36): Path {
        val (a, b) = g.visible()
        val pie = (0..n).map { g.pared(a + (b - a) * it / n, 0f) }; val alto = (0..n).map { g.pared(a + (b - a) * it / n, g.pared) }
        return Path().apply { moveTo(pie[0]); for (q in pie.drop(1)) lineTo(q); for (q in alto.reversed()) lineTo(q); close() }
    }

    /**
     * El fieltro de la tienda de Bat, de cerca: el cilindro con su luz más marcada, el pelo del fieltro, las capas que se
     * montan, los remiendos con su puntada, las manchas de humo y de lluvia, el bajo sucio de barro y estiércol; las
     * cinchas de crin, gruesas, retorcidas y con sus nudos; y en el techo, las costuras de los paños y la mugre.
     */
    private fun DrawScope.fieltro(g: Ger, p: Pincel, sol: Sol3, nieve: Float, az0: Azar) {
        val s = g.s; val (a, b) = g.visible(); val az = Azar(Semilla.de("mongol", "fieltro", "BAT"))
        val fieltro = lerp(lerp(C.FIELTRO, Color.White, 0.14f), C.FIELTRO_VIEJO, 0.25f)
        val forma = pared(g)
        val xs = (0..24).map { g.pared(a + (b - a) * it / 24f, 0.8f).x }
        val x0 = xs.min(); val x1 = xs.max()
        clipPath(forma) { clipPath(zonaDePuerta(g), clipOp = ClipOp.Difference) {
            // El cilindro: la luz que cae de lado se ve más honda (azul violeta lejos del sol, crema cálida de cara).
            val paradas = (0..24).map { i -> val th = a + (b - a) * i / 24f; val t = sol.t(s.normal(V3(sin(th), 0f, -cos(th))))
                ((xs[i] - x0) / (x1 - x0).coerceAtLeast(0.01f)) to (if (t >= 0f) p.luz(fieltro).copy(alpha = 0.3f * t * p.r.dia) else p.sombra(fieltro).copy(alpha = (0.55f * -t + 0.08f).coerceAtMost(0.6f))) }.sortedBy { it.first }
            drawRect(Brush.horizontalGradient(*paradas.toTypedArray(), startX = x0, endX = x1), Offset(x0, 100f), Size(x1 - x0, 200f))
            // El lavado desigual del fieltro: manchas grandes, más claras y más amarillas.
            repeat(9) { val th = az.entre(a, b); val c = g.pared(th, az.entre(0.2f, 1.4f)); mancha(c, az.entre(14f, 34f), az.entre(8f, 18f), if (it % 3 == 0) p.luz(lerp(fieltro, Color.White, 0.4f)) else p.medio(lerp(fieltro, C.TIERRA, 0.45f)), 0.22f) }
            // Las capas de fieltro que se montan: un canto tendido, con su sombra debajo, a dos alturas.
            for ((y0, ondula) in listOf(0.46f to 0.05f, 1.06f to 0.04f)) {
                val canto = (0..30).map { k -> g.pared(a + (b - a) * k / 30f, y0 + ondula * sin(k * 1.1f) + 0.02f * sin(k * 2.9f), g.radio + 0.01f) }
                drawPath(Path().apply { moveTo(canto[0]); for (q in canto.drop(1)) lineTo(q) }, p.sombra(fieltro).copy(alpha = 0.5f), style = Stroke(1.6f))
                drawPath(Path().apply { moveTo(canto[0] + Offset(0f, -1f)); for (q in canto.drop(1)) lineTo(q + Offset(0f, -1f)) }, p.luz(lerp(fieltro, Color.White, 0.3f)).copy(alpha = 0.6f), style = Stroke(0.6f))
            }
            // El pelo del fieltro: hebras cortas, claras del lado del sol.
            val hebrasC = ArrayList<Offset>(); val hebrasO = ArrayList<Offset>()
            repeat(420) { val q = g.pared(az.entre(a, b), az.entre(0.05f, g.pared - 0.05f)); if (it % 2 == 0) hebrasC += q else hebrasO += q }
            drawPoints(hebrasC, PointMode.Points, p.luz(lerp(fieltro, Color.White, 0.5f)).copy(alpha = 0.45f), 1.1f, StrokeCap.Round)
            drawPoints(hebrasO, PointMode.Points, p.sombra(lerp(fieltro, C.TIERRA, 0.3f)).copy(alpha = 0.3f), 0.9f, StrokeCap.Round)
            repeat(70) { val q = g.pared(az.entre(a, b), az.entre(0.1f, g.pared - 0.1f)); drawLine(p.sombra(fieltro).copy(alpha = 0.3f), q, q + Offset(az.entre(-2.4f, 2.4f), az.entre(-1.6f, 1.6f)), 0.4f) }
            // Las chorreras de la lluvia bajo el alero y el humo que ennegrece lo de arriba.
            repeat(18) { val th = az.entre(a, b); val arriba = g.pared(th, g.pared - 0.04f); val largo = az.entre(0.25f, 0.8f); drawLine(p.sombra(lerp(fieltro, C.TIERRA, 0.6f)).copy(alpha = 0.25f), arriba, g.pared(th + az.entre(-0.01f, 0.01f), g.pared - largo), az.entre(0.8f, 2f), StrokeCap.Round) }
            drawRect(Brush.verticalGradient(0f to p.sombra(lerp(fieltro, C.HOLLIN, 0.5f)).copy(alpha = 0.5f), 1f to Color.Transparent, startY = g.pared(0f, g.pared).y, endY = g.pared(0f, g.pared - 0.35f).y), Offset(x0, 100f), Size(x1 - x0, 60f))
            // Dos remiendos grandes con su puntada de crin, en lo que se ve de pared.
            for ((th, y) in listOf(-0.36f to 0.5f, -0.46f to 1.1f, -0.27f to 1.32f)) {
                val esquinas = (0 until 11).map { j -> val ang = (2 * PI * j / 11).toFloat(); val rr = az.entre(0.7f, 1.15f); g.pared(th + cos(ang) * 0.05f * rr, y + sin(ang) * 0.11f * rr, g.radio + 0.012f) }
                val t = sol.t(s.normal(V3(sin(th), 0f, -cos(th))))
                drawPath(poligono(esquinas), p.por(lerp(fieltro, C.TIERRA, 0.25f), t))
                drawPoints(esquinas.indices.flatMap { j -> val q0 = esquinas[j]; val q1 = esquinas[(j + 1) % esquinas.size]; (0 until 4).map { q0 + (q1 - q0) * (it / 4f) + Offset(0.6f, 0.4f) } }, PointMode.Points, p.medio(C.CRIN).copy(alpha = 0.4f), 0.7f, StrokeCap.Round)
            }
            // El bajo: barro, estiércol y la humedad que sube; y el fieltro roto en un canto.
            drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to p.sombra(lerp(C.TIERRA, C.ESTIERCOL, 0.5f)).copy(alpha = 0.6f), startY = g.pared(a + (b - a) * 0.5f, 0.42f).y, endY = g.pared(a + (b - a) * 0.5f, 0f).y + 10f), Offset(x0, 160f), Size(x1 - x0, 130f))
            repeat(26) { val q = g.pared(az.entre(a, b), az.entre(0.02f, 0.38f)); drawPath(borron(q, az.entre(0.8f, 2.4f), az.entre(0.6f, 1.6f), az, 6, 0.4f), p.medio(lerp(C.TIERRA, C.ESTIERCOL, 0.4f)).copy(alpha = 0.45f)) }
        } }
        // Las cinchas de crin: gruesas, retorcidas, con su luz arriba y sus nudos.
        val borde = g.mediaPuerta * 1.24f
        for (y in listOf(0.24f, 0.72f, 1.2f)) for ((t0, t1) in listOf(a + (b - a) * 0.008f to -borde, borde to b - (b - a) * 0.008f)) {
            if (t1 <= t0) continue
            val linea = (0..18).map { g.pared(t0 + (t1 - t0) * it / 18f, y, g.radio + 0.03f) }
            val camino = Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }
            drawPath(camino, p.sombra(lerp(C.CRIN, C.CUERO, 0.2f)), style = Stroke(2.8f, cap = StrokeCap.Butt))
            drawPath(camino, p.medio(lerp(C.CRIN, C.CUERO, 0.5f)), style = Stroke(1.5f, cap = StrokeCap.Butt))
            // La torsión de la cuerda: trazos cortos en diagonal.
            for (k in 0 until linea.size - 1) { val q = (linea[k] + linea[k + 1]) / 2f; drawLine(p.luz(lerp(C.CRIN, C.FIELTRO, 0.45f)).copy(alpha = 0.45f), q + Offset(-1f, -1f), q + Offset(0.5f, 0.3f), 0.5f) }
            // Los nudos, con su cabo colgando: el de la izquierda de la pared y el que la ata al marco de la puerta.
            for (th in listOf(-0.44f, -borde - 0.01f)) {
                if (th < a || th > b) continue
                val q = g.pared(th, y, g.radio + 0.05f)
                drawOval(p.sombra(C.CRIN), Offset(q.x - 2.6f, q.y - 2.2f), Size(5.2f, 4.4f)); drawOval(p.luz(lerp(C.CRIN, C.FIELTRO, 0.4f)).copy(alpha = 0.7f), Offset(q.x - 1.8f, q.y - 2f), Size(3f, 1.6f))
                drawPath(Path().apply { moveTo(q); cubicTo(q + Offset(1f, 4f), q + Offset(-1f, 8f), q + Offset(0.6f, 12f)) }, p.medio(C.CRIN), style = Stroke(1.2f, cap = StrokeCap.Round))
            }
        }
        // El techo, de cerca: los paños cosidos en círculos y las costuras que bajan, la mugre del alero, el fieltro más gris.
        techo(g, p, sol, az)
    }

    private fun DrawScope.techo(g: Ger, p: Pincel, sol: Sol3, az: Azar) {
        val s = g.s; val yAlero = g.pared - 0.06f
        val pendiente = atan((g.cumbre - g.pared) / (g.alero - g.aro))
        fun en(t: Float, k: Float): Offset { val r = g.alero + (g.aro - g.alero) * k; val y = yAlero + (g.cumbre - yAlero) * k; return s.p(r * sin(t), y, -r * cos(t)) }
        fun seVe(t: Float): Boolean = s.seVe(V3(g.alero * 0.7f * sin(t), (yAlero + g.cumbre) / 2f, -g.alero * 0.7f * cos(t)), V3(sin(t) * sin(pendiente), cos(pendiente), -cos(t) * sin(pendiente)))
        val ts = (0 until 72).map { (2 * PI * it / 72).toFloat() }.filter { seVe(it) }
        if (ts.size < 2) return
        val fieltro = lerp(C.FIELTRO, C.FIELTRO_VIEJO, 0.45f)
        // Las costuras de los paños, en círculos que siguen el techo.
        for (k in listOf(0.3f, 0.62f)) {
            val linea = ts.map { en(it, k + 0.015f * sin(it * 7f)) }.sortedBy { it.x }
            drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, p.sombra(fieltro).copy(alpha = 0.5f), style = Stroke(1.4f))
            drawPath(Path().apply { moveTo(linea[0] + Offset(0f, -0.8f)); for (q in linea.drop(1)) lineTo(q + Offset(0f, -0.8f)) }, p.luz(fieltro).copy(alpha = 0.5f), style = Stroke(0.5f))
        }
        // Las costuras que bajan del aro y la mugre que deja la lluvia al escurrir.
        for (t in ts.filterIndexed { i, _ -> i % 5 == 2 }) drawLine(p.sombra(fieltro).copy(alpha = 0.35f), en(t, 0.02f), en(t + 0.02f, 0.95f), 0.8f)
        for (t in ts.filterIndexed { i, _ -> i % 3 == 1 }) { val q = en(t, az.entre(0.05f, 0.6f)); mancha(q, az.entre(8f, 18f), az.entre(5f, 10f), if (az.real() < 0.5f) p.sombra(lerp(fieltro, C.TIERRA, 0.4f)) else p.luz(lerp(fieltro, Color.White, 0.3f)), 0.22f) }
        // La mugre del alero, más oscura en el canto.
        val canto = ts.map { en(it, 0f) }.sortedBy { it.x }; val dentro = ts.map { en(it, 0.07f) }.sortedBy { it.x }
        drawPath(poligono(canto + dentro.reversed()), p.sombra(lerp(fieltro, C.HOLLIN, 0.3f)).copy(alpha = 0.35f))
    }

    /** Un tramo de la pared de la tienda, de [t0] a [t1] (radianes) y de [y0] a [y1] (metros), a [r] del centro. */
    private fun tramo(g: Ger, t0: Float, t1: Float, y0: Float, y1: Float, r: Float = g.radio + 0.03f): Path {
        val arriba = (0..4).map { g.pared(t0 + (t1 - t0) * it / 4f, y1, r) }; val abajo = (0..4).map { g.pared(t0 + (t1 - t0) * it / 4f, y0, r) }
        return poligono(abajo + arriba.reversed())
    }

    /** La zona de la puerta con su marco: ahí no pasa el fieltro gastado ni las cinchas. */
    private fun zonaDePuerta(g: Ger): Path = tramo(g, -g.mediaPuerta * 1.3f, g.mediaPuerta * 1.3f, -0.05f, g.altoPuerta + 0.2f, g.radio + 0.04f)

    /**
     * La puerta de la tienda de Bat, lo más sólido del marco: el cerco de madera pintada de rojo, gastado en los cantos,
     * con su grosor; la hoja de fieltro teñido de ocre rojizo **[Diseño propio]**, acolchada en hileras, con su ribete
     * oscuro y un nudo sin fin cosido en el centro; la sombra del alero arriba y el barro abajo. Cerrada, entreabierta
     * (recogida a un lado) o abierta (enrollada arriba), como diga [EscenaMongol.puerta]. De noche, o con el cielo muy
     * cubierto, la luz del fuego se escapa por las rendijas.
     */
    private fun DrawScope.puertaDeBat(m: Momento, g: Ger, p: Pincel, sol: Sol3) {
        if (!g.puertaSeVe) return
        val s = g.s; val d = g.mediaPuerta; val h = g.altoPuerta; val l = p.l; val e = s.escala(0f, -g.radio)
        val tFrente = sol.t(s.normal(V3(0f, 0f, -1f))); val lado = if (p.solALaDerecha) 1f else -1f
        val brilla = maxOf(g.dentro * l.noche, (0.45f + 0.55f * g.dentro) * ValleMongol.cubierto(l)).coerceIn(0f, 1f)
        val pm = p.con(brilla * (if (g.puerta == Puerta.CERRADA) 0.15f else 0.7f))
        val pintura = lerp(lerp(C.ROJO, C.MADERA, 0.3f), C.FIELTRO_VIEJO, 0.12f); val madera = lerp(C.MADERA, C.TIERRA, 0.2f)
        val tinte = lerp(lerp(C.ROJO, C.ORO, 0.35f), C.FIELTRO_VIEJO, 0.28f); val ribete = lerp(C.AZUL, C.HOLLIN, 0.45f)
        val g2 = (0.012f * e).coerceIn(0.3f, 0.9f)
        // La hoja (o lo que se ve de ella), de fieltro teñido.
        fun acolchado(forma: Path, t0: Float, t1: Float, y0: Float, y1: Float, motivo: Boolean) {
            val arriba = g.pared(0f, y1).y; val abajo = g.pared(0f, y0).y
            drawPath(forma, Brush.verticalGradient(0f to pm.por(tinte, tFrente + 0.15f), 0.7f to pm.por(tinte, tFrente), 1f to pm.por(lerp(tinte, C.TIERRA, 0.45f), tFrente - 0.3f), startY = arriba, endY = abajo))
            clipPath(forma) {
                // Las hileras del acolchado, con su puntada, y el bulto de cada una (luz arriba, sombra abajo).
                var y = y0 + 0.1f
                while (y < y1 - 0.04f) {
                    val linea = (0..8).map { g.pared(t0 + (t1 - t0) * it / 8f, y, g.radio + 0.04f) }
                    drawPath(Path().apply { moveTo(linea[0]); for (q in linea.drop(1)) lineTo(q) }, pm.sombra(tinte).copy(alpha = 0.55f), style = Stroke(g2 * 1.4f))
                    drawPath(Path().apply { moveTo(linea[0] + Offset(0f, -1f)); for (q in linea.drop(1)) lineTo(q + Offset(0f, -1f)) }, pm.luz(tinte).copy(alpha = 0.35f), style = Stroke(g2))
                    y += 0.13f
                }
                // El ribete oscuro, por dentro del borde.
                drawPath(tramo(g, t0 + (t1 - t0) * 0.06f, t1 - (t1 - t0) * 0.06f, y0 + 0.05f, y1 - 0.05f, g.radio + 0.045f), pm.medio(ribete).copy(alpha = 0.8f), style = Stroke(g2 * 2.2f))
                if (motivo) {
                    // El nudo sin fin en el centro: un rombo con sus lazos en las puntas, cosido en crema.
                    val c = g.pared((t0 + t1) / 2f, (y0 + y1) / 2f + 0.06f, g.radio + 0.05f); val k = 0.16f * e
                    val puntas = listOf(c + Offset(0f, -k), c + Offset(k * 0.75f, 0f), c + Offset(0f, k), c + Offset(-k * 0.75f, 0f))
                    drawPath(poligono(puntas), pm.luz(C.FIELTRO).copy(alpha = 0.85f), style = Stroke(g2 * 1.6f))
                    for (q in puntas) drawCircle(pm.luz(C.FIELTRO).copy(alpha = 0.85f), k * 0.22f, q, style = Stroke(g2 * 1.4f))
                    drawPath(poligono(c + Offset(0f, -k * 0.45f), c + Offset(k * 0.34f, 0f), c + Offset(0f, k * 0.45f), c + Offset(-k * 0.34f, 0f)), pm.medio(ribete).copy(alpha = 0.8f), style = Stroke(g2 * 1.2f))
                }
                // La sombra del alero arriba y el barro abajo.
                drawRect(Brush.verticalGradient(0f to pm.sombra(C.HOLLIN).copy(alpha = 0.5f * (0.4f + 0.6f * p.r.dia)), 1f to Color.Transparent, startY = arriba, endY = arriba + (abajo - arriba) * 0.3f), Offset(0f, arriba), Size(300f, (abajo - arriba) * 0.3f))
                drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to pm.sombra(lerp(C.TIERRA, C.ESTIERCOL, 0.5f)).copy(alpha = 0.55f), startY = abajo - (abajo - arriba) * 0.22f, endY = abajo), Offset(0f, abajo - (abajo - arriba) * 0.22f), Size(300f, (abajo - arriba) * 0.22f))
            }
        }
        when (g.puerta) {
            Puerta.CERRADA -> {
                acolchado(tramo(g, -d, d, 0.04f, h, g.radio + 0.035f), -d, d, 0.04f, h, true)
                // Por las rendijas se escapa la luz del fuego.
                if (brilla > 0.05f) drawPath(Path().apply { moveTo(g.pared(-d, 0.05f, g.radio + 0.05f)); lineTo(g.pared(-d, h, g.radio + 0.05f)); lineTo(g.pared(d, h, g.radio + 0.05f)); lineTo(g.pared(d, 0.05f, g.radio + 0.05f)) },
                    C.LUMBRE.copy(alpha = (0.8f * brilla).coerceIn(0f, 1f)), style = Stroke((0.03f * e).coerceIn(0.5f, 1.6f)), blendMode = BlendMode.Plus)
            }
            Puerta.ENTREABIERTA -> {
                // Recogida hacia un lado y atada (la misma forma que la de la pieza, ahora de fieltro teñido).
                val r = g.radio
                val forma = Path().apply { moveTo(g.pared(-d * 0.05f, h, r + 0.05f)); lineTo(g.pared(d * 1.02f, h, r + 0.05f)); lineTo(g.pared(d * 1.02f, 0.04f, r + 0.05f)); cubicTo(g.pared(d * 0.6f, 0.05f, r + 0.06f), g.pared(d * 0.35f, 0.35f, r + 0.08f), g.pared(d * 0.35f, 0.62f, r + 0.08f)); cubicTo(g.pared(d * 0.2f, 0.85f, r + 0.06f), g.pared(-d * 0.02f, 1.0f, r + 0.05f), g.pared(-d * 0.05f, h, r + 0.05f)); close() }
                acolchado(forma, -d * 0.05f, d, 0.04f, h, false)
                for (j in 1..3) drawLine(pm.sombra(tinte).copy(alpha = 0.7f), g.pared(d * (0.2f + 0.22f * j), h, r + 0.06f), g.pared(d * (0.36f + 0.2f * j), 0.62f, r + 0.07f), (0.02f * e).coerceIn(0.4f, 1.2f))
                drawLine(pm.medio(C.CRIN), g.pared(d * 0.3f, 0.62f, r + 0.09f), g.pared(d * 1.05f, 0.66f, r + 0.06f), (0.03f * e).coerceIn(0.5f, 1.6f))
            }
            Puerta.ABIERTA -> {
                val rollo = tramo(g, -d * 1.05f, d * 1.05f, h - 0.17f, h + 0.06f, g.radio + 0.1f)
                drawPath(rollo, Brush.verticalGradient(listOf(pm.luz(tinte), pm.medio(tinte), pm.sombra(tinte)), g.pared(0f, h + 0.06f).y, g.pared(0f, h - 0.17f).y))
                for (u in listOf(-0.6f, 0.6f)) drawLine(pm.medio(C.CRIN), g.pared(d * u, h + 0.1f, g.radio + 0.11f), g.pared(d * u, h - 0.2f, g.radio + 0.11f), (0.03f * e).coerceIn(0.5f, 1.6f))
            }
        }
        // El cerco: dos jambas, el dintel y el umbral, de madera pintada y gastada, con su grosor hacia dentro.
        val ancho = 0.2f * d
        for (u in listOf(-1f, 1f)) {
            val t0 = if (u < 0f) -d - ancho else d; val t1 = if (u < 0f) -d else d + ancho
            val jamba = tramo(g, t0, t1, -0.02f, h + 0.1f, g.radio + 0.05f)
            drawPath(jamba, pm.por(pintura, tFrente + 0.1f * u * lado))
            // El canto de dentro (el grueso de la madera), en sombra.
            val canto = tramo(g, if (u < 0f) -d - ancho * 0.05f else d - ancho * 0.25f, if (u < 0f) -d + ancho * 0.25f else d + ancho * 0.05f, -0.02f, h + 0.08f, g.radio + 0.04f)
            drawPath(canto, pm.sombra(lerp(pintura, C.HOLLIN, 0.3f)))
            // La pintura gastada: la madera asoma en el canto y a la altura de las manos; el barro abajo.
            clipPath(jamba) {
                drawLine(pm.luz(madera).copy(alpha = 0.7f), g.pared(t0 + (t1 - t0) * 0.15f, 0.05f, g.radio + 0.06f), g.pared(t0 + (t1 - t0) * 0.15f, h, g.radio + 0.06f), (0.012f * e).coerceIn(0.4f, 1f))
                drawPath(TrazosValle.blando(g.pared((t0 + t1) / 2f, 0.85f, g.radio + 0.06f), ancho * e * 0.9f, 0.1f * e, Azar(Semilla.de("mongol", "puerta", u.toInt())), 7, 0.3f), pm.por(madera, tFrente).copy(alpha = 0.7f))
                drawRect(Brush.verticalGradient(0f to Color.Transparent, 1f to pm.sombra(lerp(C.TIERRA, C.ESTIERCOL, 0.4f)).copy(alpha = 0.6f), startY = g.pared(0f, 0.3f).y, endY = g.pared(0f, 0f).y), Offset(0f, g.pared(0f, 0.3f).y), Size(300f, 40f))
            }
        }
        val dintel = tramo(g, -d - ancho * 1.25f, d + ancho * 1.25f, h + 0.06f, h + 0.2f, g.radio + 0.06f)
        drawPath(dintel, pm.por(pintura, tFrente + 0.25f))
        clipPath(dintel) {
            drawLine(pm.medio(lerp(C.ORO, C.MADERA, 0.4f)).copy(alpha = 0.8f), g.pared(-d - ancho, h + 0.13f, g.radio + 0.07f), g.pared(d + ancho, h + 0.13f, g.radio + 0.07f), (0.02f * e).coerceIn(0.4f, 1.2f))
            drawRect(Brush.verticalGradient(0f to pm.sombra(C.HOLLIN).copy(alpha = 0.55f * (0.4f + 0.6f * p.r.dia)), 1f to Color.Transparent, startY = g.pared(0f, h + 0.2f).y, endY = g.pared(0f, h + 0.06f).y), Offset(0f, g.pared(0f, h + 0.2f).y), Size(300f, 20f))
        }
        drawPath(tramo(g, -d - ancho, d + ancho, -0.03f, 0.03f, g.radio + 0.07f), pm.por(madera, tFrente + 0.4f))
        drawPath(dintel, pm.sombra(lerp(pintura, C.HOLLIN, 0.4f)).copy(alpha = 0.6f), style = Stroke(g2))
    }

    /** Lo que cuelga de la pared de la tienda: el ronzal y la cuerda enrollada, el cubo de ordeñar, el rollo de fieltro en el suelo. */
    private class Colgado(val forma: Path, val pintar: DrawScope.() -> Unit)

    private fun colgados(g: Ger, p: Pincel, sol: Sol3, cubo: Boolean): List<Colgado> {
        val lista = ArrayList<Colgado>()
        val clavo1 = g.pared(-0.4f, 1.2f, g.radio + 0.06f); val clavo2 = g.pared(-0.33f, 0.72f, g.radio + 0.06f); val clavo3 = g.pared(-0.18f, 1.2f, g.radio + 0.06f)
        val e = g.s.escala(0f, -g.radio)
        // El ronzal de crin con su cuerda, enrollada en vueltas.
        run {
            val c = clavo1 + Offset(0f, 0.18f * e)
            val rollo = Path().apply { addOval(androidx.compose.ui.geometry.Rect(c.x - 0.13f * e, c.y - 0.07f * e, c.x + 0.13f * e, c.y + 0.22f * e)) }
            lista += Colgado(rollo) {
                drawLine(p.medio(C.CRIN), clavo1, c + Offset(0f, -0.06f * e), 1.2f)
                for (k in 0 until 7) { val r = 0.12f * e * (1f - k * 0.04f); drawOval(p.por(lerp(C.CRIN, C.CUERO, 0.45f), if (k % 2 == 0) 0.4f else -0.3f), Offset(c.x - r + k * 0.35f, c.y - 0.05f * e + k * 0.45f), Size(r * 2f, 0.25f * e), style = Stroke(0.8f)) }
                drawPath(Path().apply { moveTo(c + Offset(0.08f * e, 0.18f * e)); cubicTo(c + Offset(0.1f * e, 0.32f * e), c + Offset(0.02f * e, 0.42f * e), c + Offset(0.06f * e, 0.55f * e)) }, p.medio(C.CRIN), style = Stroke(1.4f, cap = StrokeCap.Round))
            }
        }
        // El cubo de cuero de ordeñar, colgado de su asa (el día de un objeto grande, en uso: le deja el sitio).
        if (cubo) run {
            val top = clavo2 + Offset(0f, 0.12f * e); val w = 0.12f * e; val h = 0.26f * e
            val cubo = poligono(top + Offset(-w, 0f), top + Offset(w, 0f), top + Offset(w * 0.8f, h), top + Offset(-w * 0.8f, h))
            lista += Colgado(cubo) {
                drawPath(Path().apply { moveTo(top + Offset(-w, 0f)); cubicTo(top + Offset(-w, -0.1f * e), top + Offset(w, -0.1f * e), top + Offset(w, 0f)) }, p.medio(C.CRIN), style = Stroke(1f))
                drawPath(cubo, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.CUERO), p.luz(lerp(C.CUERO, C.ORO, 0.2f))) else listOf(p.luz(lerp(C.CUERO, C.ORO, 0.2f)), p.sombra(C.CUERO)), top.x - w, top.x + w))
                drawOval(p.sombra(C.HOLLIN), Offset(top.x - w, top.y - 0.03f * e), Size(w * 2f, 0.06f * e))
                for (k in 1..3) drawLine(p.sombra(C.CUERO).copy(alpha = 0.6f), top + Offset(-w * (1f - 0.06f * k), h * k / 4f), top + Offset(w * (1f - 0.06f * k), h * k / 4f), 0.5f)
                drawPoints((0..8).map { top + Offset(0f, h * it / 8f) }, PointMode.Points, p.luz(C.FIELTRO).copy(alpha = 0.6f), 0.7f, StrokeCap.Round)
                drawLine(p.luz(C.FIELTRO).copy(alpha = 0.6f), top + Offset(-w * 0.5f, h * 0.9f), top + Offset(-w * 0.4f, h * 1.1f), 0.7f)        // una gota de leche helada
            }
        }
        // Una bolsa de fieltro con su cordón (la de los cuchillos y el pedernal de repuesto).
        run {
            val top = clavo3 + Offset(0f, 0.1f * e); val w = 0.08f * e; val h = 0.2f * e
            val bolsa = Path().apply { moveTo(top + Offset(-w * 0.5f, 0f)); cubicTo(top + Offset(-w * 1.3f, h * 0.4f), top + Offset(-w * 1.1f, h), top + Offset(0f, h)); cubicTo(top + Offset(w * 1.1f, h), top + Offset(w * 1.3f, h * 0.4f), top + Offset(w * 0.5f, 0f)); close() }
            lista += Colgado(bolsa) {
                drawLine(p.medio(C.CRIN), clavo3, top, 0.9f)
                drawPath(bolsa, Brush.horizontalGradient(if (p.solALaDerecha) listOf(p.sombra(C.CUERO), p.por(C.CUERO, 0.5f)) else listOf(p.por(C.CUERO, 0.5f), p.sombra(C.CUERO)), top.x - w * 1.2f, top.x + w * 1.2f))
                drawPath(Path().apply { moveTo(top + Offset(-w * 0.6f, h * 0.18f)); cubicTo(top + Offset(-w * 0.2f, h * 0.26f), top + Offset(w * 0.2f, h * 0.26f), top + Offset(w * 0.6f, h * 0.18f)) }, p.medio(C.CRIN), style = Stroke(0.9f))
                drawPoints((0..6).map { top + Offset(0f, h * (0.3f + 0.1f * it)) + Offset(w * 0.15f * sin(it * 1.4f), 0f) }, PointMode.Points, p.luz(lerp(C.CUERO, C.ORO, 0.4f)).copy(alpha = 0.6f), 0.6f, StrokeCap.Round)
            }
        }
        return lista
    }

    private fun DrawScope.colgado(m: Momento, g: Ger, p: Pincel, sol: Sol3) {
        val grande = ObjetosMongol.esGrande(m)
        val lista = colgados(g, p, sol, cubo = !grande)
        // Su sombra en el fieltro: corrida hacia el lado contrario al sol y un poco hacia abajo.
        capaSombra(sol.r, 0.9f) { c ->
            clipPath(pared(g)) {
                val d = Offset(-sol.r.lat * 7f, 3.5f)
                translate(d.x, d.y) { for (k in lista) sombra(k.forma, c) }
                // La de la pértiga del lazo, que cruza la pared.
                if (!hoyEsElLazo(m)) drawLine(c, PIE_PERTIGA + d, PUNTA_PERTIGA + d, 3.4f)
            }
        }
        for (k in lista) k.pintar(this)
        // El día de un objeto grande, el rollo de fieltro se quita: ese rincón es suyo.
        if (grande) return
        val r0 = g.pared(-0.47f, 0f, g.radio + 0.32f)
        // Un rollo de fieltro tendido junto al bajo de la pared, que se sale por la izquierda: el cilindro con su luz
        // arriba, la cabeza con la espiral del fieltro enrollado y dos ataduras de crin.
        val x1 = r0.x + 34f; val y0 = r0.y - 24f; val y1 = r0.y + 2f; val rx = 6f
        val cuerpo = Path().apply { moveTo(Offset(-10f, y0)); lineTo(Offset(x1, y0)); cubicTo(Offset(x1 + rx, y0), Offset(x1 + rx, y1), Offset(x1, y1)); lineTo(Offset(-10f, y1)); close() }
        drawPath(cuerpo, Brush.verticalGradient(0f to p.luz(lerp(C.FIELTRO, Color.White, 0.15f)), 0.45f to p.por(C.FIELTRO, 0.1f), 1f to p.sombra(lerp(C.FIELTRO, C.TIERRA, 0.2f)), startY = y0, endY = y1))
        drawPath(Path().apply { addOval(androidx.compose.ui.geometry.Rect(x1 - rx, y0, x1 + rx, y1)) }, p.por(lerp(C.FIELTRO, C.TIERRA, 0.15f), if (p.solALaDerecha) 0.5f else -0.3f))
        drawPath(Path().apply { val c = Offset(x1, (y0 + y1) / 2f); for (k in 0..34) { val a = k * 0.62f; val f = 0.08f + 0.92f * k / 34f; val q = c + Offset(cos(a) * rx * 0.9f * f, sin(a) * (y1 - y0) * 0.46f * f); if (k == 0) moveTo(q) else lineTo(q) } }, p.sombra(C.FIELTRO).copy(alpha = 0.7f), style = Stroke(0.7f))
        for (x in listOf(r0.x - 6f, r0.x + 18f)) { drawLine(p.sombra(C.CRIN), Offset(x, y0 - 0.5f), Offset(x + 1.5f, y1 + 0.5f), 1.6f); drawLine(p.luz(lerp(C.CRIN, C.FIELTRO, 0.4f)).copy(alpha = 0.6f), Offset(x - 0.4f, y0), Offset(x + 0.6f, y0 + 6f), 0.6f) }
        drawRect(Brush.verticalGradient(listOf(Color.Transparent, p.sombra(lerp(C.TIERRA, C.ESTIERCOL, 0.4f)).copy(alpha = 0.4f)), y1 - 6f, y1), Offset(-10f, y1 - 6f), Size(x1 + 10f, 6f))
    }

    /** Las cintas azules de la fiesta (khadag), atadas al marco de la puerta y caídas. */
    private fun DrawScope.cintasDePuerta(m: Momento, g: Ger, p: Pincel) {
        if (!ValleMongol.cintasAzules(m) || !g.puertaSeVe) return
        val d = g.mediaPuerta
        for ((j, u) in listOf(-1.25f, 1.2f).withIndex()) {
            val top = g.pared(d * u, g.altoPuerta + 0.1f, g.radio + 0.05f); val e = g.s.escala(0f, -g.radio)
            val largo = 0.55f * e; val vuelo = (if (j == 0) -1f else 1f) * 0.06f * e
            val cinta = Path().apply { moveTo(top); cubicTo(top + Offset(vuelo, largo * 0.3f), top + Offset(-vuelo, largo * 0.7f), top + Offset(vuelo * 0.5f, largo)); lineTo(top + Offset(vuelo * 0.5f + 0.07f * e, largo - 0.02f * e)); cubicTo(top + Offset(-vuelo + 0.07f * e, largo * 0.7f), top + Offset(vuelo + 0.07f * e, largo * 0.3f), top + Offset(0.07f * e, 0f)); close() }
            drawPath(cinta, p.por(C.AZUL, if (j == 0) 0.3f else -0.1f))
            drawPath(cinta, p.luz(lerp(C.AZUL, C.FIELTRO, 0.5f)).copy(alpha = 0.4f), style = Stroke(0.5f))
        }
    }

    // ---------------------------------------------------------------- El marco: la pértiga del lazo

    private val PIE_PERTIGA = Offset(-12f, ALTO + 6f)
    private val PUNTA_PERTIGA = Offset(170f, -6f)

    private fun DrawScope.sombraDePertiga(sol: Sol3, c: Color) {
        // En el suelo de la terraza, tumbada con el sol.
        val pie = Offset(26f, 285f); drawLine(c, pie, pie + Offset(sol.r.ax * 40f - 30f, -6f + sol.r.ay * 30f), 3f)
    }

    /**
     * El día que el objeto es la pértiga del lazo, la del marco no se pinta: el lazo del día se apoya en la pared y es el
     * que cruza el cuadro (no hay dos pértigas).
     */
    private fun hoyEsElLazo(m: Momento) = m.ficha.objeto == "lazo"

    fun DrawScope.marco(m: Momento, p: Pincel, sol: Sol3) {
        if (hoyEsElLazo(m)) return
        // La pértiga del lazo, apoyada en el alero de la tienda: cruza en diagonal hasta el borde de arriba.
        val apoyo = Offset(96f, 117f)
        val madera = lerp(C.MADERA, C.PIEDRA, 0.25f)
        drawLine(p.sombra(madera), PIE_PERTIGA, PUNTA_PERTIGA, 3.4f, StrokeCap.Round)
        drawLine(p.medio(madera), PIE_PERTIGA, PUNTA_PERTIGA, 2.2f, StrokeCap.Round)
        drawLine(p.luz(lerp(madera, C.FIELTRO, 0.2f)), PIE_PERTIGA + Offset(if (p.solALaDerecha) 0.9f else -0.9f, 0f), PUNTA_PERTIGA + Offset(if (p.solALaDerecha) 0.5f else -0.5f, 0f), 1f, StrokeCap.Round)
        // Los nudos de la madera y el pulido de las manos.
        for (t in listOf(0.18f, 0.31f, 0.47f)) { val q = PIE_PERTIGA + (PUNTA_PERTIGA - PIE_PERTIGA) * t; drawOval(p.sombra(madera), Offset(q.x - 1.4f, q.y - 1f), Size(2.8f, 2f)) }
        // Las vueltas de crin donde se ata el lazo, y la cuerda que cuelga.
        for (j in 0..3) { val c = apoyo + (PUNTA_PERTIGA - apoyo) * (0.62f + j * 0.025f); drawLine(p.medio(C.CRIN), c + Offset(-2.4f, -0.6f), c + Offset(2.4f, 0.6f), 1.2f) }
        val a = apoyo + (PUNTA_PERTIGA - apoyo) * 0.66f
        drawPath(Path().apply { moveTo(a); cubicTo(a + Offset(4f, 14f), a + Offset(10f, 20f), a + Offset(12f, 28f)); cubicTo(a + Offset(13f, 31f), a + Offset(9f, 33f), a + Offset(7f, 30f)) }, p.medio(C.CRIN), style = Stroke(0.8f))
        if (ValleMongol.cintasAzules(m)) {
            val q = apoyo + (PUNTA_PERTIGA - apoyo) * 0.82f
            drawPath(Path().apply { moveTo(q); cubicTo(q + Offset(6f, 6f), q + Offset(2f, 14f), q + Offset(9f, 22f)); lineTo(q + Offset(12f, 21f)); cubicTo(q + Offset(6f, 13f), q + Offset(10f, 5f), q + Offset(3f, -1f)); close() }, p.por(C.AZUL, 0.2f))
        }
        // De noche la pértiga, oscura, toma un filo de la luz de la puerta.
        if (p.l.noche > 0.3f) drawLine(C.LUMBRE.copy(alpha = (0.25f * p.l.noche * EscenaMongol.fuegoDentro(m)).coerceIn(0f, 1f)), PIE_PERTIGA + Offset(1f, 0f), apoyo + Offset(1f, 0f), 0.8f, blendMode = BlendMode.Plus)
    }
}
