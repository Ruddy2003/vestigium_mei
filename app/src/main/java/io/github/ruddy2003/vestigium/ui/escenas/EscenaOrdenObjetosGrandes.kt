package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.brillo
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.caja
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.cara
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.cilindro
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.disco
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.raya
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.revolucion
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.sombra
import io.github.ruddy2003.vestigium.ui.escenas.TrazoOrden.trazo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Cosa
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.poligono
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Semilla
import kotlin.math.cos
import kotlin.math.sin

/**
 * Los cinco objetos grandes de la escriba, de pie en el adarve y arrimados a la almena ([Arrimo]). Cada uno pinta su
 * sombra (en las losas y, lo que pasa del pie del parapeto, en su cara) y devuelve su silueta en la [Cosa]. Lo plano no se
 * pone contra la almena (su cara se ve casi de canto desde los ojos): el manual se queda de pie, abierto hacia quien mira.
 * **[Diseño propio]** todo lo que no dice el cuento: cómo están puestos y qué los acompaña.
 */
internal object GrandesOrden {
    private val C = ColorOrden
    private val K = ColorObjetoOrden
    private fun az(vararg partes: Any) = Azar(Semilla.de("orden", "objetos", *partes))
    private fun Azar.entreF(a: Float, b: Float): Float = a + (b - a) * (entero(10000) / 10000f)

    /** Lo que recorta la sombra de un grande: las losas del adarve y la cara del parapeto y de la almena junto a él. */
    private fun dondeCae(g: Arrimo): Path {
        val q = TrazoOrden.aCiudadela(g.suelo); val s = g.pared
        val pared = poligono(listOf(V3(s, 0f, q.z - 0.75f), V3(s, 0f, q.z + 0.75f), V3(s, CamOrden.ALTO_ALMENA, q.z + 0.75f), V3(s, CamOrden.ALTO_ALMENA, q.z - 0.75f)).map { CamOrden.p(it) })
        return union(listOf(AdarveOrden.formaDelSuelo(), pared))
    }

    private fun DrawScope.sombraDe(lo: LuzObjeto, g: Arrimo, base: List<V3>, cuerpo: List<V3>, contacto: Float = 0.4f) {
        val donde = dondeCae(g)
        sombra(lo, base, cuerpo, g.suelo.y, donde, contacto, g.pared, donde)
    }

    /**
     * Cuánto se corre un grande hacia la almena para quedar arrimado: lo que le falta al pie para llegar a la pared, menos
     * [medio] (su medio ancho, ya ampliado) y un dedo de holgura.
     */
    private fun arrimado(g: Arrimo, medio: Float): Float = ((g.hueco - medio - 0.03f) / 0.955f).coerceIn(-0.25f, 0.4f)

    /** El marco de un grande: en el pie, corrido [corre] metros hacia la almena y girado [giro]. */
    private fun marco(g: Arrimo, corre: Float, giro: Float, amplia: Float): MarcoOrden =
        MarcoOrden.de(g.suelo, 0f, 1f).mas(corre, 0f, 0f, giro = giro, amp = amplia)

    // =================================================================
    //  La radio
    // =================================================================

    /**
     * La radio de patrulla: «pesa como un niño de dos años y se lleva a la espalda por turnos». Una caja de chapa pintada de
     * verde gris, desconchada, con el cuadro de mandos arriba (dos perillas, el dial con su aguja, un interruptor), la reja
     * del altavoz y una placa lisa delante, las correas de mochila caídas por un lado, el auricular colgado de su gancho con
     * el cordón rizado, y **la antena larga como una caña de pescar** (la de la Garza, en el cuento), que sube por delante
     * de la almena. Recostada en el parapeto.
     */
    fun radio(lo: LuzObjeto, g: Arrimo, pie: Offset): Cosa {
        val f0 = marco(g, arrimado(g, 0.32f), -0.3f, 2.15f).mas(0f, 0f, 0f, ladeo = 0.06f)
        val antena = antenaPuntos()
        val puntos = listOf(V3(-0.15f, 0f, -0.09f), V3(0.15f, 0f, -0.09f), V3(0.15f, 0f, 0.09f), V3(-0.15f, 0f, 0.09f), V3(-0.15f, 0.44f, -0.09f), V3(0.15f, 0.44f, 0.09f), V3(-0.22f, 0.2f, -0.05f)) + antena.takeLast(1)
        val forma = TrazoOrden.silueta(f0, puntos)
        return Cosa(forma, pie) {
            sombraDe(lo, g, listOf(V3(-0.15f, 0f, -0.09f), V3(0.15f, 0f, -0.09f), V3(0.15f, 0f, 0.09f), V3(-0.15f, 0f, 0.09f)).map { f0.w(it) },
                (puntos.dropLast(1) + antena.filterIndexed { i, _ -> i % 3 == 0 }).map { f0.w(it) }, 0.45f)
            correas(lo, f0, detras = true)
            cajaDeLaRadio(lo, f0)
            mandos(lo, f0)
            frente(lo, f0)
            correas(lo, f0, detras = false)
            auricular(lo, f0)
            trazo(lo, f0, antena, 0.011f, lerp(K.ACERO, C.HOLLIN, 0.2f), V3(-0.6f, 0.2f, -0.8f))
            for (i in 1 until antena.size - 1 step 3) disco(lo, f0, antena[i], V3(0f, 0f, -1f), 0.0085f, K.ACERO, 6)
            trazo(lo, f0, antena, 0.004f, lerp(K.ACERO, Color.White, 0.4f), V3(-0.6f, 0.3f, -0.8f), 0.9f)
            disco(lo, f0, antena.last(), V3(0f, 0f, -1f), 0.013f, K.ACERO, 8)
        }
    }

    /** La antena: sale de su zócalo en la esquina de arriba y sube, larga y algo combada, como una caña. */
    private fun antenaPuntos(): List<V3> = TrazoOrden.curva(V3(0.1f, 0.48f, 0.04f), V3(0.09f, 0.75f, 0.04f), V3(0.04f, 1.0f, 0.02f), V3(-0.03f, 1.2f, 0f), 15)

    /** La caja de chapa: las caras con su luz, los cantos gastados y los desconchones. */
    private fun DrawScope.cajaDeLaRadio(lo: LuzObjeto, f: MarcoOrden) {
        caja(lo, f, -0.15f, 0.15f, 0f, 0.44f, -0.09f, 0.09f, K.PINTURA, lerp(K.PINTURA, C.HIERRO, 0.35f)) { lado, _ ->
            if (lado == 1 || lado == 3) desconchones(lo, f, lado)
        }
        // Los cantos, donde la pintura se ha ido: el aluminio.
        for ((a, b) in listOf(V3(-0.15f, 0.44f, -0.09f) to V3(0.15f, 0.44f, -0.09f), V3(-0.15f, 0f, -0.09f) to V3(-0.15f, 0.44f, -0.09f))) raya(f, a, b, 0.006f, lo.color(K.ALUMINIO, f.w(a), f.n(V3(-0.5f, 0.5f, -0.7f))).copy(alpha = 0.7f))
        // Las cantoneras de abajo, de chapa remachada.
        for (x in listOf(-0.15f, 0.15f)) caja(lo, f, x - 0.012f, x + 0.012f, 0f, 0.04f, -0.095f, -0.07f, lerp(C.HIERRO, K.PINTURA, 0.3f))
    }

    private fun DrawScope.desconchones(lo: LuzObjeto, f: MarcoOrden, lado: Int) {
        val a = az("radio", lado)
        repeat(7) {
            val y = a.entreF(0.03f, 0.42f); val u = a.entreF(-0.13f, 0.13f); val r = a.entreF(0.006f, 0.016f)
            val (c, n) = if (lado == 1) V3(u, y, -0.0905f) to V3(0f, 0f, -1f) else V3(-0.1505f, y, u * 0.6f) to V3(-1f, 0f, 0f)
            disco(lo, f, c, n, r, K.ALUMINIO, 7, alfa = 0.75f)
        }
        // El óxido de abajo, que sube del suelo.
        val oxido = if (lado == 1) listOf(V3(-0.15f, 0f, -0.0905f), V3(0.15f, 0f, -0.0905f), V3(0.15f, 0.05f, -0.0905f), V3(-0.15f, 0.035f, -0.0905f)) else listOf(V3(-0.1505f, 0f, 0.09f), V3(-0.1505f, 0f, -0.09f), V3(-0.1505f, 0.04f, -0.09f), V3(-0.1505f, 0.03f, 0.09f))
        cara(lo, f, oxido, if (lado == 1) V3(0f, 0f, -1f) else V3(-1f, 0f, 0f), lerp(C.ROCA, C.MADERA, 0.4f), 0.45f)
    }

    /** El cuadro de mandos de arriba: el dial con su cristal y su aguja, dos perillas, el interruptor y el zócalo de la antena. */
    private fun DrawScope.mandos(lo: LuzObjeto, f: MarcoOrden) {
        val y = 0.441f
        cara(lo, f, listOf(V3(-0.13f, y, -0.075f), V3(0.13f, y, -0.075f), V3(0.13f, y, 0.075f), V3(-0.13f, y, 0.075f)), V3(0f, 1f, 0f), lerp(C.HIERRO, K.PINTURA, 0.25f), claro = 0.1f)
        // El dial: una ventana con su arco de marcas y la aguja carmesí.
        val d = listOf(V3(-0.11f, y + 0.001f, -0.06f), V3(-0.01f, y + 0.001f, -0.06f), V3(-0.01f, y + 0.001f, -0.015f), V3(-0.11f, y + 0.001f, -0.015f))
        cara(lo, f, d, V3(0f, 1f, 0f), lerp(K.PAPEL_VIEJO, C.VIDRIO, 0.35f), claro = 0.1f)
        for (i in 0 until 9) { val x = -0.1f + i * 0.011f; raya(f, V3(x, y + 0.002f, -0.03f), V3(x, y + 0.002f, -0.022f + (i % 2) * 0.004f), 0.0016f, C.HOLLIN.copy(alpha = 0.7f)) }
        raya(f, V3(-0.07f, y + 0.002f, -0.055f), V3(-0.045f, y + 0.002f, -0.02f), 0.0025f, C.CARMESI)
        brillo(lo, f, V3(-0.105f, y + 0.003f, -0.056f), V3(-0.06f, y + 0.003f, -0.056f), 0.004f, C.VIDRIO, V3(0f, 1f, -0.4f), 0.8f)
        // Las perillas, negras, con su raya clara.
        for ((x, r) in listOf(0.035f to 0.024f, 0.095f to 0.017f)) {
            cilindro(lo, f, V3(x, y, -0.035f), V3(x, y + 0.03f, -0.035f), r, C.HOLLIN, lerp(C.HOLLIN, K.ACERO, 0.4f), 12)
            raya(f, V3(x, y + 0.031f, -0.035f), V3(x - r * 0.8f, y + 0.031f, -0.035f - r * 0.4f), 0.003f, K.PAPEL_VIEJO)
        }
        cilindro(lo, f, V3(-0.07f, y, 0.035f), V3(-0.07f, y + 0.025f, 0.03f), 0.005f, K.ACERO, K.ACERO, 6)
        // El zócalo de la antena, en la esquina de atrás.
        cilindro(lo, f, V3(0.1f, y, 0.04f), V3(0.1f, y + 0.045f, 0.04f), 0.018f, C.HOLLIN, lerp(C.HOLLIN, K.ACERO, 0.5f), 10)
    }

    /** El frente: la reja redonda del altavoz y la placa lisa de latón con sus cuatro remaches (sin letras). */
    private fun DrawScope.frente(lo: LuzObjeto, f: MarcoOrden) {
        val z = -0.0915f
        disco(lo, f, V3(-0.04f, 0.27f, z), V3(0f, 0f, -1f), 0.075f, lerp(C.HIERRO, K.PINTURA, 0.35f), 18)
        for (j in -3..3) { val y = 0.27f + j * 0.018f; val w = kotlin.math.sqrt((0.07f * 0.07f - (j * 0.018f) * (j * 0.018f)).coerceAtLeast(0f)) * 0.9f; raya(f, V3(-0.04f - w, y, z - 0.001f), V3(-0.04f + w, y, z - 0.001f), 0.006f, C.HOLLIN.copy(alpha = 0.8f)) }
        trazo(lo, f, TrazoOrden.circulo(V3(-0.04f, 0.27f, z - 0.001f), V3(0f, 0f, -1f), 0.075f, 16, 2.2f, 4.2f), 0.004f, lerp(K.PINTURA, Color.White, 0.3f), V3(-0.4f, 0.5f, -1f), 0.6f)
        cara(lo, f, listOf(V3(0.04f, 0.12f, z), V3(0.13f, 0.12f, z), V3(0.13f, 0.17f, z), V3(0.04f, 0.17f, z)), V3(0f, 0f, -1f), lerp(C.LATON, K.PINTURA, 0.25f), claro = 0.1f)
        for (x in listOf(0.048f, 0.122f)) for (y in listOf(0.127f, 0.163f)) disco(lo, f, V3(x, y, z - 0.001f), V3(0f, 0f, -1f), 0.003f, K.LATON_OSC, 6)
        // Los dos cierres de los lados del frente.
        for (x in listOf(-0.135f, 0.135f)) caja(lo, f, x - 0.01f, x + 0.01f, 0.36f, 0.4f, -0.1f, -0.09f, K.ACERO)
    }

    /** Las correas de mochila: lona parda, una caída por delante hasta el suelo con su hebilla y otra por el lado. */
    private fun DrawScope.correas(lo: LuzObjeto, f: MarcoOrden, detras: Boolean) {
        val lona = lerp(C.ARENA, C.MADERA, 0.38f)
        if (detras) {
            trazo(lo, f, TrazoOrden.curva(V3(0.12f, 0.43f, 0.095f), V3(0.2f, 0.4f, 0.12f), V3(0.2f, 0.1f, 0.1f), V3(0.13f, 0.02f, 0.06f), 10), 0.03f, lona, V3(1f, 0f, 0f))
            return
        }
        val caida = TrazoOrden.curva(V3(-0.1f, 0.445f, 0.09f), V3(-0.1f, 0.47f, -0.06f), V3(-0.17f, 0.3f, -0.12f), V3(-0.2f, 0.02f, -0.16f), 12)
        trazo(lo, f, caida, 0.034f, lerp(lona, C.HOLLIN, 0.25f), V3(-0.6f, 0.3f, -0.8f))
        trazo(lo, f, caida, 0.022f, lona, V3(-0.6f, 0.4f, -0.8f), claro = 0.1f)
        trazo(lo, f, listOf(V3(-0.2f, 0.005f, -0.16f), V3(-0.27f, 0.005f, -0.2f), V3(-0.33f, 0.004f, -0.17f)), 0.03f, lona, V3(0f, 1f, 0f))
        val h = V3(-0.19f, 0.06f, -0.15f)
        trazo(lo, f, listOf(h + V3(-0.02f, 0.015f, 0f), h + V3(0.02f, 0.015f, 0f), h + V3(0.02f, -0.015f, 0f), h + V3(-0.02f, -0.015f, 0f), h + V3(-0.02f, 0.015f, 0f)), 0.005f, K.ACERO, V3(-0.5f, 0.5f, -1f))
    }

    /** El auricular colgado en su gancho del lado izquierdo, con el cordón rizado que vuelve a la caja. */
    private fun DrawScope.auricular(lo: LuzObjeto, f: MarcoOrden) {
        val x = -0.16f
        val a = V3(x - 0.02f, 0.34f, -0.04f); val b = V3(x - 0.025f, 0.2f, -0.03f)
        val cordon = (0..24).map { i -> val u = i / 24f; V3(x - 0.01f + 0.012f * sin(u * 30f), 0.19f - 0.12f * u + 0.03f * u * u, -0.02f + 0.06f * u) }
        trazo(lo, f, cordon, 0.006f, C.HOLLIN, V3(-1f, 0.3f, -0.4f))
        cilindro(lo, f, V3(x, 0.355f, -0.03f), V3(x - 0.03f, 0.355f, -0.03f), 0.005f, K.ACERO, K.ACERO, 6)
        cilindro(lo, f, a, b, 0.012f, C.HOLLIN, C.HOLLIN, 10)
        revolucion(lo, f, a, a + V3(-0.012f, 0.012f, -0.02f), listOf(0f to 0.013f, 0.6f to 0.022f, 1f to 0.022f), C.HOLLIN, lerp(C.HOLLIN, K.ACERO, 0.3f), 12)
        revolucion(lo, f, b, b + V3(-0.012f, -0.008f, -0.022f), listOf(0f to 0.013f, 0.6f to 0.02f, 1f to 0.02f), C.HOLLIN, lerp(C.HOLLIN, K.ACERO, 0.3f), 12)
        brillo(lo, f, a + V3(-0.012f, -0.01f, -0.008f), b + V3(-0.012f, 0.01f, -0.008f), 0.004f, lerp(C.HOLLIN, K.ACERO, 0.5f), V3(-1f, 0.3f, -0.5f), 0.9f)
    }

    // =================================================================
    //  El manual
    // =================================================================

    /**
     * El manual de antes del Fuego: gordo, hinchado de humedad, abierto y de pie en las losas, con el lomo detrás y las dos
     * mitades hacia quien mira, como en un atril. En la hoja de delante, un dibujo técnico sencillo (un tornillo con su rosca, la flecha
     * que dice hacia dónde gira y la que dice hacia dónde entra, y sus cotas sin números) entre renglones sin letras; manchas
     * de agua y la esquina doblada.
     */
    fun manual(lo: LuzObjeto, g: Arrimo, pie: Offset): Cosa {
        val f = marco(g, arrimado(g, 0.48f), -0.2f, 2.65f)
        // Abierto hacia quien mira, con el lomo detrás: cada mitad se gira 0,5 rad (unos 120° entre las dos).
        val izq = f.mas(0f, 0f, 0f, giro = 0.5f); val der = f.mas(0f, 0f, 0f, giro = -0.5f)
        val puntos = listOf(V3(-0.195f, 0f, 0f), V3(-0.195f, 0.262f, 0f)).map { izq.w(it) } + listOf(V3(0.195f, 0f, 0f), V3(0.195f, 0.262f, 0f)).map { der.w(it) } +
            listOf(V3(0f, 0f, 0.07f), V3(0f, 0.262f, 0.07f)).map { f.w(it) }
        val forma = poligono(envolvente(puntos.map { TrazoOrden.pc(it) }))
        return Cosa(forma, pie) {
            sombraDe(lo, g, listOf(puntos[0], puntos[2], puntos[4]), puntos, 0.45f)
            lomo(lo, f)
            mitad(lo, izq, -1f, conDibujo = false)
            mitad(lo, der, 1f, conDibujo = true)
        }
    }

    /**
     * Una mitad del manual ([s] −1 la de la izquierda): la tapa de cartón forrado, el taco de hojas hinchado (con su canto de
     * capas onduladas) y la hoja de encima; la que va [conDibujo] lleva el tornillo; la otra, renglones.
     */
    private fun DrawScope.mitad(lo: LuzObjeto, f: MarcoOrden, s: Float, conDibujo: Boolean) {
        val tapa = lerp(C.HIERRO, C.TELA, 0.32f)
        // La tapa, detrás de las hojas; asoma por el canto de arriba y por el de fuera.
        cara(lo, f, listOf(V3(0f, 0f, 0.05f), V3(0.195f * s, 0f, 0.05f), V3(0.195f * s, 0.262f, 0.05f), V3(0f, 0.262f, 0.05f)), V3(0f, 0f, -1f), tapa)
        cara(lo, f, listOf(V3(0.195f * s, 0f, 0.05f), V3(0.195f * s, 0f, 0.058f), V3(0.195f * s, 0.262f, 0.058f), V3(0.195f * s, 0.262f, 0.05f)), V3(s, 0f, 0f), lerp(tapa, C.HOLLIN, 0.2f))
        // El taco de hojas: hinchado, cada vez más ancho hacia fuera, con el canto de arriba en capas.
        val a = az("manual", s)
        val arriba = (0..6).map { i -> val u = i / 6f; V3(u * 0.185f * s, 0.25f + a.entreF(-0.004f, 0.004f), 0.05f - (0.012f + 0.038f * u * (1.2f - u))) }
        val canto = arriba + listOf(V3(0.185f * s, 0.25f, 0.05f), V3(0f, 0.25f, 0.05f))
        cara(lo, f, canto.map { V3(it.x, 0.25f, it.z) }, V3(0f, 1f, 0f), lerp(K.PAPEL_VIEJO, C.ARENA, 0.4f))
        for (j in 1..4) trazo(lo, f, arriba.map { V3(it.x, 0.25f, it.z + j * (0.05f - it.z) / 5f) }, 0.0012f, lerp(K.MANCHA, C.MADERA, 0.3f), V3(0f, 1f, 0f), 0.6f)
        cara(lo, f, listOf(V3(0.185f * s, 0f, 0.05f - 0.012f), V3(0.185f * s, 0f, 0.05f), V3(0.185f * s, 0.25f, 0.05f), V3(0.185f * s, 0.25f, 0.05f - 0.012f)), V3(s, 0f, 0f), lerp(K.PAPEL_VIEJO, C.ARENA, 0.45f))
        // La hoja de encima: ondulada por la humedad (tres tiras con su luz).
        val zs = listOf(0f, 0.06f, 0.12f, 0.185f).map { u -> 0.05f - (0.012f + 0.038f * (u / 0.185f) * (1.2f - u / 0.185f)) }
        for (i in 0 until 3) {
            val x0 = i * 0.0617f * s; val x1 = (i + 1) * 0.0617f * s
            val n = TrazoOrden.unidad(V3((zs[i + 1] - zs[i]) * s, 0f, -0.0617f))
            cara(lo, f, listOf(V3(x0, 0.004f, zs[i]), V3(x1, 0.004f, zs[i + 1]), V3(x1, 0.247f, zs[i + 1]), V3(x0, 0.247f, zs[i])), n, K.PAPEL_VIEJO, claro = 0.08f)
        }
        val enLaHoja = { u: Float, y: Float -> val uu = (u / 0.185f).coerceIn(0f, 1f); V3(u * s, y, 0.05f - (0.012f + 0.038f * uu * (1.2f - uu)) - 0.001f) }
        if (conDibujo) dibujo(lo, f, enLaHoja) else renglonesDelManual(lo, f, enLaHoja, a)
        // Las manchas de agua: cercos pardos.
        for ((u, y, r) in listOf(Triple(0.13f, 0.06f, 0.035f), Triple(0.05f, 0.2f, 0.022f))) {
            val c = enLaHoja(u, y); trazo(lo, f, TrazoOrden.circulo(c, V3(0f, 0f, -1f), r, 12), 0.003f, K.MANCHA, V3(0f, 0f, -1f), 0.45f)
            disco(lo, f, c, V3(0f, 0f, -1f), r, K.MANCHA, 12, alfa = 0.14f)
        }
    }

    /** Los renglones sin letras de una hoja. */
    private fun DrawScope.renglonesDelManual(lo: LuzObjeto, f: MarcoOrden, enLaHoja: (Float, Float) -> V3, a: Azar) {
        for (i in 0 until 15) {
            val y = 0.225f - i * 0.0145f; val u1 = if (i % 6 == 5) a.entreF(0.06f, 0.11f) else 0.165f
            trazo(lo, f, listOf(enLaHoja(0.022f, y), enLaHoja((0.022f + u1) / 2f, y), enLaHoja(u1, y)), 0.0035f, K.TINTA, V3(0f, 0f, -1f), 0.5f)
        }
    }

    /** El dibujo técnico: renglones arriba y abajo, y en medio un tornillo grande con su rosca, su flecha de giro y sus cotas. */
    private fun DrawScope.dibujo(lo: LuzObjeto, f: MarcoOrden, enLaHoja: (Float, Float) -> V3) {
        val tinta = K.TINTA
        for (i in 0 until 3) { val y = 0.228f - i * 0.013f; trazo(lo, f, listOf(enLaHoja(0.02f, y), enLaHoja(0.16f - i * 0.03f, y)), 0.0035f, tinta, V3(0f, 0f, -1f), 0.5f) }
        for (i in 0 until 2) { val y = 0.032f - i * 0.013f; trazo(lo, f, listOf(enLaHoja(0.02f, y), enLaHoja(0.14f + i * 0.02f, y)), 0.0035f, tinta, V3(0f, 0f, -1f), 0.5f) }
        // El tornillo: la cabeza, la caña y la punta, con la rosca en diagonal.
        val x = 0.085f; val r = 0.017f
        val cabeza = listOf(enLaHoja(x - 0.03f, 0.165f), enLaHoja(x + 0.03f, 0.165f), enLaHoja(x + 0.03f, 0.185f), enLaHoja(x - 0.03f, 0.185f), enLaHoja(x - 0.03f, 0.165f))
        trazo(lo, f, cabeza, 0.004f, tinta, V3(0f, 0f, -1f), 0.85f)
        trazo(lo, f, listOf(enLaHoja(x - r, 0.165f), enLaHoja(x - r, 0.075f), enLaHoja(x, 0.05f), enLaHoja(x + r, 0.075f), enLaHoja(x + r, 0.165f)), 0.004f, tinta, V3(0f, 0f, -1f), 0.85f)
        for (i in 0 until 8) { val y = 0.08f + i * 0.011f; trazo(lo, f, listOf(enLaHoja(x - r, y), enLaHoja(x + r, y + 0.008f)), 0.0028f, tinta, V3(0f, 0f, -1f), 0.75f) }
        // La flecha que gira alrededor de la cabeza, y la que dice hacia dónde entra (de un carmesí desvaído).
        val rojo = lerp(C.CARMESI, K.PAPEL_VIEJO, 0.25f)
        val giro = (0..10).map { i -> val ang = 3.6f + i * 0.42f; enLaHoja(x + 0.048f * cos(ang), 0.175f + 0.016f * sin(ang)) }
        trazo(lo, f, giro, 0.004f, rojo, V3(0f, 0f, -1f), 0.9f)
        val fin = giro.last(); trazo(lo, f, listOf(fin + V3(-0.012f, 0.008f, 0f), fin, fin + V3(-0.004f, -0.013f, 0f)), 0.004f, rojo, V3(0f, 0f, -1f), 0.9f)
        trazo(lo, f, listOf(enLaHoja(x + 0.055f, 0.14f), enLaHoja(x + 0.055f, 0.07f)), 0.004f, rojo, V3(0f, 0f, -1f), 0.9f)
        trazo(lo, f, listOf(enLaHoja(x + 0.045f, 0.082f), enLaHoja(x + 0.055f, 0.066f), enLaHoja(x + 0.065f, 0.082f)), 0.004f, rojo, V3(0f, 0f, -1f), 0.9f)
        // La cota de la izquierda, con sus topes.
        trazo(lo, f, listOf(enLaHoja(x - 0.04f, 0.165f), enLaHoja(x - 0.04f, 0.05f)), 0.002f, tinta, V3(0f, 0f, -1f), 0.6f)
        for (y in listOf(0.165f, 0.05f)) trazo(lo, f, listOf(enLaHoja(x - 0.048f, y), enLaHoja(x - 0.032f, y)), 0.002f, tinta, V3(0f, 0f, -1f), 0.6f)
    }

    /** El lomo, entre las dos mitades: la tela gastada con sus nervios. */
    private fun DrawScope.lomo(lo: LuzObjeto, f: MarcoOrden) {
        val tapa = lerp(C.HIERRO, C.TELA, 0.32f)
        cilindro(lo, f, V3(0.004f, 0f, 0.055f), V3(0.004f, 0.262f, 0.055f), 0.016f, lerp(tapa, C.HOLLIN, 0.15f), lerp(tapa, C.ARENA, 0.15f), 10)
        for (y in listOf(0.05f, 0.13f, 0.21f)) raya(f, V3(-0.01f, y, 0.045f), V3(0.016f, y, 0.045f), 0.004f, lerp(tapa, C.HOLLIN, 0.4f))
    }

    // =================================================================
    //  La ración
    // =================================================================

    /**
     * La ración de patrulla: «una bolsa de tela con pan de cebada cocido dos veces, carne seca y un puñado de pasas». La
     * bolsa de lino crudo, abierta con la boca vuelta y el cordón suelto, con un remiendo; por la boca asoman las tostadas de
     * cebada y las tiras de carne; delante, en su paño, más carne y las pasas.
     */
    fun racion(lo: LuzObjeto, g: Arrimo, pie: Offset): Cosa {
        val f = marco(g, arrimado(g, 0.34f), 0.1f, 2.55f).mas(0f, 0f, 0f, ladeo = 0.08f)
        val puntos = listOf(V3(-0.14f, 0f, -0.14f), V3(0.14f, 0f, 0.13f), V3(-0.13f, 0.26f, 0f), V3(0.13f, 0.26f, 0f), V3(0f, 0.33f, 0f), V3(-0.3f, 0f, -0.2f), V3(-0.26f, 0.02f, -0.28f))
        val forma = TrazoOrden.silueta(f, puntos)
        return Cosa(forma, pie) {
            sombraDe(lo, g, TrazoOrden.circulo(V3(0f, 0f, 0f), V3(0f, 1f, 0f), 0.12f, 10).map { f.w(it) }, listOf(V3(-0.12f, 0.2f, -0.1f), V3(0.12f, 0.2f, 0.1f), V3(0f, 0.32f, 0f), V3(0.12f, 0.15f, -0.1f), V3(-0.12f, 0.15f, 0.1f)).map { f.w(it) }, 0.45f)
            panoDeLaRacion(lo, f)
            bolsa(lo, f)
            contenido(lo, f)
            cuello(lo, f)
        }
    }

    /** La bolsa: un saco de tela de yute, panzudo abajo y más estrecho arriba, con su remiendo y una mancha de grasa. */
    private fun DrawScope.bolsa(lo: LuzObjeto, f: MarcoOrden) {
        revolucion(lo, f, V3(0f, 0f, 0f), V3(0.008f, 0.2f, 0.006f), listOf(0f to 0.11f, 0.1f to 0.136f, 0.35f to 0.142f, 0.7f to 0.122f, 0.9f to 0.104f, 1f to 0.1f), K.SACO, null, 16)
        // El remiendo, de otro paño, con sus puntadas; la mancha de grasa.
        val r = listOf(V3(-0.075f, 0.05f, -0.112f), V3(-0.012f, 0.055f, -0.138f), V3(-0.014f, 0.125f, -0.13f), V3(-0.078f, 0.118f, -0.104f))
        cara(lo, f, r, V3(-0.3f, 0f, -1f), lerp(C.TELA_VIEJA, K.SACO, 0.35f))
        for (i in 0 until 6) { val u = i / 6f; raya(f, V3(-0.075f + 0.062f * u, 0.05f + 0.005f * u, -0.116f - 0.024f * u), V3(-0.072f + 0.062f * u, 0.058f + 0.005f * u, -0.116f - 0.024f * u), 0.0024f, lerp(C.ARENA, C.MADERA, 0.2f).copy(alpha = 0.85f)) }
        disco(lo, f, V3(0.055f, 0.1f, -0.128f), V3(0.3f, 0f, -1f), 0.026f, lerp(K.SACO, C.HOLLIN, 0.35f), 10, alfa = 0.4f)
        // La trama del yute: rayas finas de través y los pliegues de la tela que cede.
        for (j in 0 until 6) { val y = 0.02f + j * 0.03f; trazo(lo, f, TrazoOrden.circulo(V3(0.004f * j / 6f, y, 0f), V3(0f, 1f, 0f), 0.14f - 0.004f * j, 10, 3.6f, 5.4f), 0.0018f, lerp(K.SACO, C.HOLLIN, 0.25f), V3(0f, 0f, -1f), 0.35f) }
        for (ang in listOf(3.7f, 4.4f, 5.2f)) trazo(lo, f, listOf(V3(0.138f * cos(ang), 0.03f, 0.138f * sin(ang)), V3(0.142f * cos(ang + 0.08f), 0.1f, 0.142f * sin(ang + 0.08f)), V3(0.11f * cos(ang), 0.19f, 0.11f * sin(ang))), 0.005f, lerp(K.SACO, C.SOMBRA, 0.45f), V3(cos(ang), 0f, sin(ang)), 0.55f)
    }

    /** Lo que asoma por la boca: dentro, en penumbra; tres tostadas de cebada de pie, abiertas hacia fuera, y dos tiras de carne. */
    private fun DrawScope.contenido(lo: LuzObjeto, f: MarcoOrden) {
        disco(lo, f, V3(0.008f, 0.205f, 0.006f), V3(0f, 1f, 0f), 0.096f, lerp(K.SACO, C.HOLLIN, 0.6f), 16)
        for ((x, ang) in listOf(-0.005f to 0.25f, 0.045f to -0.35f)) {
            val tira = TrazoOrden.curva(V3(x, 0.17f, 0.03f), V3(x + 0.01f * ang, 0.26f, 0.03f), V3(x + 0.05f * ang, 0.3f, 0.01f), V3(x + 0.08f * ang, 0.3f, -0.01f), 8)
            trazo(lo, f, tira, 0.02f, K.CARNE, V3(0f, 0.3f, -1f))
            trazo(lo, f, tira.map { it + V3(0.004f, 0.002f, -0.006f) }, 0.005f, lerp(K.CARNE, C.ARENA, 0.45f), V3(0f, 0.3f, -1f), 0.7f)
        }
        tostada(lo, f.mas(0.05f, 0.15f, 0.03f, giro = -0.4f, vuelco = 0.4f, ladeo = 0.35f, amp = 1.15f))
        tostada(lo, f.mas(-0.045f, 0.15f, 0.02f, giro = 0.5f, vuelco = 0.4f, ladeo = -0.35f, amp = 1.15f))
        tostada(lo, f.mas(0.005f, 0.155f, -0.03f, giro = 0.1f, vuelco = 0.55f, amp = 1.15f))
    }

    /** Una tostada de pan de cebada cocido dos veces: gruesa, con la corteza parda y la miga tostada llena de ojos. */
    private fun DrawScope.tostada(lo: LuzObjeto, f: MarcoOrden) {
        val corteza = lerp(K.PAN, C.MADERA, 0.45f)
        caja(lo, f, -0.045f, 0.045f, 0f, 0.13f, -0.012f, 0.012f, corteza, lerp(corteza, C.HOLLIN, 0.15f))
        val miga = lerp(K.PAN, C.ARENA, 0.35f)
        val cara0 = listOf(V3(-0.04f, 0.004f, -0.0125f), V3(0.04f, 0.004f, -0.0125f), V3(0.041f, 0.11f, -0.0125f), V3(0.02f, 0.126f, -0.0125f), V3(-0.022f, 0.125f, -0.0125f), V3(-0.041f, 0.108f, -0.0125f))
        cara(lo, f, cara0, V3(0f, 0f, -1f), miga, claro = 0.1f)
        val a = az("tostada", f.o.x)
        repeat(9) { disco(lo, f, V3(a.entreF(-0.032f, 0.032f), a.entreF(0.015f, 0.105f), -0.013f), V3(0f, 0f, -1f), a.entreF(0.003f, 0.0065f), lerp(K.PAN, C.HOLLIN, 0.4f), 6, alfa = 0.8f) }
        // Los granos de cebada, más claros, y el tostado del borde.
        repeat(5) { disco(lo, f, V3(a.entreF(-0.03f, 0.03f), a.entreF(0.02f, 0.1f), -0.0132f), V3(0f, 0f, -1f), 0.0035f, lerp(C.ARENA, Color.White, 0.2f), 5, alfa = 0.7f) }
    }

    /** La boca vuelta hacia fuera, más clara (el revés de la tela), y el cordón de cerrarla, suelto, que cae por un lado. */
    private fun DrawScope.cuello(lo: LuzObjeto, f: MarcoOrden) {
        revolucion(lo, f, V3(0.008f, 0.19f, 0.006f), V3(0.008f, 0.225f, 0.006f), listOf(0f to 0.104f, 0.5f to 0.117f, 1f to 0.11f), lerp(K.SACO, C.ARENA, 0.3f), null, 16)
        val cordon = lerp(C.MADERA, C.HOLLIN, 0.3f)
        trazo(lo, f, TrazoOrden.circulo(V3(0.008f, 0.202f, 0.006f), V3(0f, 1f, 0f), 0.112f, 16, 2.6f, 6.6f), 0.006f, cordon, V3(0f, 0.3f, -1f))
        trazo(lo, f, TrazoOrden.curva(V3(-0.1f, 0.2f, -0.03f), V3(-0.155f, 0.14f, -0.04f), V3(-0.165f, 0.04f, -0.06f), V3(-0.21f, 0.004f, -0.1f), 10), 0.007f, cordon, V3(-0.8f, 0.3f, -0.5f))
        disco(lo, f, V3(-0.21f, 0.006f, -0.1f), V3(0f, 1f, 0f), 0.009f, cordon, 6)
    }

    /** El paño delante de la bolsa: más carne seca, retorcida, y el puñado de pasas con su brillo. */
    private fun DrawScope.panoDeLaRacion(lo: LuzObjeto, f: MarcoOrden) {
        cara(lo, f, listOf(V3(-0.36f, 0.002f, -0.12f), V3(-0.14f, 0.002f, -0.2f), V3(-0.1f, 0.002f, -0.36f), V3(-0.34f, 0.003f, -0.34f)), V3(0f, 1f, 0f), lerp(K.LINO, C.CARMESI, 0.18f))
        for (i in 0 until 2) {
            val z = -0.22f - i * 0.05f
            val tira = listOf(V3(-0.32f, 0.008f, z), V3(-0.26f, 0.012f, z - 0.02f), V3(-0.2f, 0.008f, z + 0.01f), V3(-0.15f, 0.01f, z - 0.01f))
            trazo(lo, f, tira, 0.018f, K.CARNE, V3(0f, 1f, -0.3f)); trazo(lo, f, tira.map { it + V3(0f, 0.004f, -0.004f) }, 0.004f, lerp(K.CARNE, C.ARENA, 0.4f), V3(0f, 1f, -0.3f), 0.6f)
        }
        val a = az("pasas")
        repeat(16) {
            val c = V3(a.entreF(-0.3f, -0.18f), 0.008f, a.entreF(-0.33f, -0.28f))
            disco(lo, f, c, V3(0f, 1f, -0.2f), a.entreF(0.007f, 0.01f), K.PASA, 7)
            disco(lo, f, c + V3(-0.002f, 0.003f, -0.002f), V3(0f, 1f, -0.2f), 0.0025f, lerp(K.PASA, Color.White, 0.5f), 5, alfa = 0.6f)
        }
    }

    // =================================================================
    //  La cantimplora
    // =================================================================

    /**
     * La cantimplora, «de aluminio viejo, abolladas, con el nombre del dueño rayado en el costado»: redonda y chata, de pie
     * en las losas y recostada en la almena, con sus abolladuras (cada una con su lado de sombra y su canto de luz), las rayas
     * del nombre (rayas, nunca letras), el tapón con su cadenilla y la correa de cuero colgada de un clavo del parapeto.
     */
    fun cantimplora(lo: LuzObjeto, g: Arrimo, pie: Offset): Cosa {
        val f = marco(g, arrimado(g, 0.24f), -0.42f, 3.0f).mas(0f, 0f, 0f, ladeo = 0.08f)
        val clavo = TrazoOrden.aCamara(V3(g.pared - 0.01f, 0.9f, TrazoOrden.aCiudadela(g.suelo).z + 0.08f))
        val puntos = listOf(V3(-0.11f, 0.01f, 0f), V3(0.11f, 0.01f, 0f), V3(-0.11f, 0.22f, 0f), V3(0.11f, 0.22f, 0f), V3(0f, 0.26f, 0f))
        val forma = poligono(envolvente(puntos.map { f.p(it) } + TrazoOrden.pc(clavo)))
        return Cosa(forma, pie) {
            sombraDe(lo, g, listOf(V3(-0.04f, 0f, -0.03f), V3(0.04f, 0f, -0.03f), V3(0.04f, 0f, 0.03f), V3(-0.04f, 0f, 0.03f)).map { f.w(it) },
                (TrazoOrden.circulo(V3(0f, 0.11f, 0f), V3(0f, 0f, 1f), 0.11f, 10).map { f.w(it) } + clavo), 0.45f)
            correa(lo, f, clavo, detras = true)
            cuerpoDeLaCantimplora(lo, f)
            abolladuras(lo, f)
            rayasDelNombre(lo, f)
            tapon(lo, f)
            correa(lo, f, clavo, detras = false)
        }
    }

    /** El cuerpo: un disco chato con el canto abombado, de aluminio gris frío, y su brillo largo. */
    private fun DrawScope.cuerpoDeLaCantimplora(lo: LuzObjeto, f: MarcoOrden) {
        revolucion(lo, f, V3(0f, 0.11f, 0.034f), V3(0f, 0.11f, -0.034f), listOf(0f to 0.095f, 0.2f to 0.107f, 0.5f to 0.112f, 0.8f to 0.107f, 1f to 0.095f), K.ALUMINIO, K.ALUMINIO, 22)
        // El abombado de la cara, con su luz: más clara arriba a la izquierda.
        disco(lo, f, V3(-0.02f, 0.13f, -0.035f), V3(-0.3f, 0.3f, -1f), 0.07f, lerp(K.ALUMINIO, Color.White, 0.12f), 16, alfa = 0.45f)
        brillo(lo, f, V3(-0.07f, 0.06f, -0.03f), V3(-0.075f, 0.16f, -0.03f), 0.012f, K.ALUMINIO, V3(-0.7f, 0.2f, -0.7f), 0.9f)
        // El cerco gastado y oscuro del canto, donde se toca.
        trazo(lo, f, TrazoOrden.circulo(V3(0f, 0.11f, -0.034f), V3(0f, 0f, -1f), 0.095f, 22, 5.8f, 9.6f), 0.004f, lerp(K.ALUMINIO, C.HOLLIN, 0.35f), V3(0.3f, -0.3f, -1f), 0.6f)
    }

    /** Tres abolladuras: una sombra en media luna y su canto de luz. */
    private fun DrawScope.abolladuras(lo: LuzObjeto, f: MarcoOrden) {
        for ((x, y, r) in listOf(Triple(0.045f, 0.065f, 0.026f), Triple(-0.045f, 0.165f, 0.018f), Triple(0.06f, 0.16f, 0.014f))) {
            val c = V3(x, y, -0.0352f)
            trazo(lo, f, TrazoOrden.circulo(c, V3(0f, 0f, -1f), r, 10, 0.6f, 2.9f), r * 0.55f, lerp(K.ALUMINIO, C.SOMBRA, 0.45f), V3(0f, 0f, -1f), 0.7f)
            trazo(lo, f, TrazoOrden.circulo(c, V3(0f, 0f, -1f), r, 10, 3.7f, 6f), r * 0.3f, lerp(K.ALUMINIO, Color.White, 0.5f), V3(0f, 0f, -1f), 0.8f)
        }
    }

    /** El nombre del dueño rayado con la punta de un cuchillo: rayas cortas y cruzadas, en una fila, sin letras. */
    private fun DrawScope.rayasDelNombre(lo: LuzObjeto, f: MarcoOrden) {
        val a = az("cantimplora")
        var x = -0.06f
        while (x < 0.03f) {
            val y = 0.105f + a.entreF(-0.004f, 0.004f); val h = a.entreF(0.012f, 0.02f); val ladeo = a.entreF(-0.008f, 0.008f)
            val p0 = V3(x, y, -0.036f); val p1 = V3(x + ladeo, y + h, -0.036f)
            raya(f, p0, p1, 0.0028f, lerp(K.ALUMINIO, C.HOLLIN, 0.45f).copy(alpha = 0.85f))
            raya(f, p0 + V3(0.0018f, 0f, 0f), p1 + V3(0.0018f, 0f, 0f), 0.0016f, Color.White.copy(alpha = 0.55f))
            if (a.real01() < 0.3f) raya(f, V3(x - 0.004f, y + h * 0.5f, -0.036f), V3(x + 0.008f, y + h * 0.6f, -0.036f), 0.002f, lerp(K.ALUMINIO, C.HOLLIN, 0.4f).copy(alpha = 0.8f))
            x += a.entreF(0.008f, 0.014f)
        }
    }

    /** El cuello con su tapón de rosca y la cadenilla que lo ata a la oreja. */
    private fun DrawScope.tapon(lo: LuzObjeto, f: MarcoOrden) {
        cilindro(lo, f, V3(0f, 0.215f, 0f), V3(0f, 0.24f, 0f), 0.016f, K.ALUMINIO, K.ALUMINIO, 10)
        revolucion(lo, f, V3(0f, 0.238f, 0f), V3(0f, 0.262f, 0f), listOf(0f to 0.02f, 0.85f to 0.02f, 1f to 0.017f), K.ACERO, lerp(K.ACERO, Color.White, 0.15f), 12)
        for (i in 0 until 4) raya(f, V3(-0.02f + i * 0.004f, 0.24f, -0.019f), V3(-0.02f + i * 0.004f, 0.258f, -0.019f), 0.0018f, C.HOLLIN.copy(alpha = 0.4f))
        val cadena = TrazoOrden.curva(V3(0.018f, 0.25f, -0.01f), V3(0.04f, 0.23f, -0.03f), V3(0.06f, 0.21f, -0.03f), V3(0.07f, 0.2f, -0.02f), 8)
        for (i in 0 until cadena.size - 1 step 2) disco(lo, f, cadena[i], V3(0f, 0f, -1f), 0.003f, K.ACERO, 6)
    }

    /** La correa de cuero: de las dos orejas del hombro sube al clavo del parapeto; [detras] pinta el tramo de atrás. */
    private fun DrawScope.correa(lo: LuzObjeto, f: MarcoOrden, clavo: V3, detras: Boolean) {
        val oreja = if (detras) V3(0.075f, 0.19f, 0.02f) else V3(-0.075f, 0.19f, -0.02f)
        caja(lo, f, oreja.x - 0.012f, oreja.x + 0.012f, oreja.y - 0.004f, oreja.y + 0.02f, oreja.z - 0.006f, oreja.z + 0.006f, K.ACERO)
        val a = TrazoOrden.pc(f.w(oreja + V3(0f, 0.015f, 0f))); val b = TrazoOrden.pc(clavo)
        val m = Offset((a.x + b.x) / 2f, (a.y + b.y) / 2f + 3f)
        val camino = Path().apply { moveTo(a.x, a.y); cubicTo(a.x, a.y - (a.y - b.y) * 0.5f, m.x, m.y, b.x, b.y) }
        val col = lo.color(K.CUERO, f.w(oreja), f.n(V3(-0.5f, 0.3f, -0.8f)))
        val w = (0.022f * f.k).coerceAtLeast(1f)
        drawPath(camino, lerp(col, C.HOLLIN, if (detras) 0.4f else 0.15f), style = androidx.compose.ui.graphics.drawscope.Stroke(w, cap = androidx.compose.ui.graphics.StrokeCap.Round))
        if (!detras) {
            drawPath(camino, lerp(col, Color.White, 0.15f).copy(alpha = 0.5f), style = androidx.compose.ui.graphics.drawscope.Stroke(w * 0.3f, cap = androidx.compose.ui.graphics.StrokeCap.Round))
            // El clavo, de hierro, metido entre las piedras.
            drawCircle(lo.color(C.HIERRO, clavo, V3(-0.9f, 0.2f, -0.3f)), (w * 0.5f).coerceAtLeast(0.8f), b)
        }
    }

    // =================================================================
    //  El soldador
    // =================================================================

    /**
     * El soldador: «una punta de cobre que se calienta en la fragua»; aquí, en un braserillo de hierro **[Diseño propio]**
     * (el cuento lo calienta en la fragua; en el adarve, en las brasas de la guardia). La cabeza de cobre metida en las
     * brasas, la varilla de hierro y el mango de madera fuera, apoyados en el borde; al lado, una barra de estaño. Las brasas
     * siempre algo vivas (el rescoldo); de noche, rojas, con su luz en el cobre y en las losas.
     */
    fun soldador(lo0: LuzObjeto, g: Arrimo, pie: Offset): Cosa {
        val f = marco(g, arrimado(g, 0.32f), 0.2f, 2.5f)
        val brasa = f.w(V3(0f, 0.16f, 0f))
        val lo = lo0.con(Lumbre(brasa, 0.35f + 0.5f * lo0.noche, 0.35f))
        val puntos = listOf(V3(-0.13f, 0f, -0.13f), V3(0.13f, 0f, 0.13f), V3(-0.13f, 0.16f, 0f), V3(0.13f, 0.16f, 0f), V3(-0.28f, 0.22f, -0.18f), V3(-0.3f, 0.005f, -0.2f))
        val forma = TrazoOrden.silueta(f, puntos)
        return Cosa(forma, pie) {
            sombraDe(lo, g, TrazoOrden.circulo(V3(0f, 0f, 0f), V3(0f, 1f, 0f), 0.1f, 8).map { f.w(it) }, listOf(V3(-0.13f, 0.16f, 0f), V3(0.13f, 0.16f, 0f), V3(0f, 0.16f, 0.13f), V3(0f, 0.16f, -0.13f), V3(-0.28f, 0.22f, -0.18f)).map { f.w(it) }, 0.4f)
            charcoDeBrasas(lo, f)
            estano(lo, f)
            braserillo(lo, f)
            brasas(lo, f)
            hierro(lo, f)
        }
    }

    /** El braserillo: un cuenco de hierro sobre tres patas, con el borde vuelto y el hollín. */
    private fun DrawScope.braserillo(lo: LuzObjeto, f: MarcoOrden) {
        for (i in 0 until 3) { val a = 0.9f + i * 2.094f; cilindro(lo, f, V3(0.07f * cos(a), 0.085f, 0.07f * sin(a)), V3(0.1f * cos(a), 0f, 0.1f * sin(a)), 0.008f, C.HIERRO, null, 6) }
        revolucion(lo, f, V3(0f, 0.08f, 0f), V3(0f, 0.15f, 0f), listOf(0f to 0.05f, 0.35f to 0.09f, 0.8f to 0.118f, 1f to 0.126f), lerp(C.HIERRO, C.ROCA, 0.15f), null, 18)
        trazo(lo, f, TrazoOrden.circulo(V3(0f, 0.152f, 0f), V3(0f, 1f, 0f), 0.127f, 22, 3.0f, 6.3f), 0.008f, lerp(C.HIERRO, K.ACERO, 0.4f), V3(0f, 1f, -0.5f))
        // El óxido de la panza.
        disco(lo, f, V3(-0.07f, 0.11f, -0.07f), V3(-0.6f, -0.3f, -0.7f), 0.025f, lerp(C.ROCA, C.MADERA, 0.3f), 8, alfa = 0.5f)
    }

    /** Las brasas: carbón negro con su ceniza gris y, entre medias, lo vivo; de noche, más rojo y con su resplandor. */
    private fun DrawScope.brasas(lo: LuzObjeto, f: MarcoOrden) {
        disco(lo, f, V3(0f, 0.143f, 0f), V3(0f, 1f, 0f), 0.115f, lerp(C.HOLLIN, C.ROCA, 0.15f), 16)
        val a = az("brasas"); val vivo = 0.35f + 0.65f * lo.noche
        repeat(12) {
            val c = V3(a.entreF(-0.085f, 0.085f), 0.15f, a.entreF(-0.085f, 0.085f)); val r = a.entreF(0.014f, 0.024f)
            val n = V3(a.entreF(-0.4f, 0.4f), 1f, a.entreF(-0.6f, 0.2f))
            disco(lo, f, c, n, r, lerp(C.HOLLIN, C.TELA, 0.12f), 7)
            disco(lo, f, c + V3(0f, 0.002f, 0.004f), n, r * 0.6f, lerp(C.HOLLIN, C.TELA, 0.45f), 6, alfa = 0.6f)
        }
        repeat(9) {
            val c = V3(a.entreF(-0.07f, 0.07f), 0.153f, a.entreF(-0.07f, 0.07f))
            disco(lo, f, c, V3(0f, 1f, -0.2f), a.entreF(0.007f, 0.012f), lerp(C.FRAGUA, C.LUMBRE, a.entreF(0f, 0.6f)).copy(alpha = vivo), 7, luz = false)
        }
        if (lo.noche > 0.2f) resplandor(f.p(V3(0f, 0.16f, 0f)), 0.3f * f.k, 0.5f * lo.noche, C.FRAGUA)
    }

    /** El soldador: la cabeza de cobre (lo que se ve fuera de las brasas, con el filo limado), la varilla y el mango. */
    private fun DrawScope.hierro(lo: LuzObjeto, f: MarcoOrden) {
        val d = TrazoOrden.unidad(V3(-1f, 0.2f, -0.6f))
        val cabeza = V3(0.0f, 0.165f, 0.0f)
        val cf = f.mas(cabeza.x, cabeza.y, cabeza.z, giro = 2.6f, vuelco = 0.18f)
        caja(lo, cf, -0.07f, 0.0f, -0.016f, 0.016f, -0.018f, 0.018f, K.COBRE, lerp(K.COBRE, C.LATON, 0.25f))
        cara(lo, cf, listOf(V3(-0.07f, -0.014f, -0.016f), V3(-0.095f, 0f, 0f), V3(-0.07f, 0.014f, -0.016f)), V3(-0.4f, 0f, -1f), K.COBRE, claro = 0.15f)
        cara(lo, cf, listOf(V3(-0.07f, 0.014f, -0.016f), V3(-0.095f, 0f, 0f), V3(-0.07f, 0.014f, 0.016f)), V3(-0.3f, 1f, 0f), lerp(K.COBRE, C.LATON, 0.5f), claro = 0.25f)
        // El cobre que se pone rojo en las brasas, de noche.
        if (lo.noche > 0.2f) disco(lo, cf, V3(-0.075f, 0f, -0.017f), V3(0f, 0f, -1f), 0.012f, C.FRAGUA.copy(alpha = 0.6f * lo.noche), 8, luz = false)
        val r0 = cabeza + d * 0.01f; val r1 = r0 + d * 0.2f
        cilindro(lo, f, r0, r1, 0.0075f, lerp(C.HIERRO, K.ACERO, 0.3f), null, 6)
        brillo(lo, f, r0 + V3(0f, 0.005f, 0f), r1 + V3(0f, 0.005f, 0f), 0.003f, C.HIERRO, V3(0f, 1f, -0.4f), 0.7f)
        val m1 = r1 + d * 0.13f
        cilindro(lo, f, r1, r1 + d * 0.015f, 0.011f, K.ACERO, K.ACERO, 8)
        revolucion(lo, f, r1 + d * 0.015f, m1, listOf(0f to 0.014f, 0.15f to 0.018f, 0.85f to 0.018f, 1f to 0.013f), lerp(C.MADERA, C.ARENA, 0.25f), lerp(C.MADERA, C.HOLLIN, 0.2f), 10)
        // El quemado del mango, cerca de la virola.
        raya(f, r1 + d * 0.02f + V3(0f, 0.014f, 0f), r1 + d * 0.045f + V3(0f, 0.014f, 0f), 0.006f, C.HOLLIN.copy(alpha = 0.5f))
    }

    /** La barra de estaño, en las losas: gris claro, con su brillo. */
    private fun DrawScope.estano(lo: LuzObjeto, f: MarcoOrden) {
        cilindro(lo, f, V3(-0.22f, 0.007f, -0.06f), V3(-0.12f, 0.007f, -0.2f), 0.007f, lerp(K.ACERO, Color.White, 0.3f), lerp(K.ACERO, Color.White, 0.4f), 6)
    }

    /** La luz de las brasas en las losas, de noche, debajo de todo. */
    private fun DrawScope.charcoDeBrasas(lo: LuzObjeto, f: MarcoOrden) {
        if (lo.noche < 0.2f) return
        val c = f.p(V3(0f, 0f, 0f)); val r = 0.4f * f.k
        drawOval(Brush.radialGradient(listOf(C.FRAGUA.copy(alpha = 0.35f * lo.noche), Color.Transparent), c, r), Offset(c.x - r, c.y - r * 0.4f), Size(r * 2f, r * 0.8f), blendMode = BlendMode.Plus)
    }

    private fun Azar.real01(): Float = entero(10000) / 10000f
}
