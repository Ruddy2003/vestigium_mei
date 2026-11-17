package io.github.ruddy2003.vestigium.ui.escenas

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import io.github.ruddy2003.vestigium.ui.escenas.AdarveOrden.luzDeLosCandiles
import io.github.ruddy2003.vestigium.ui.escenas.AdarveOrden.nieveEnLasAlmenas
import io.github.ruddy2003.vestigium.ui.escenas.AdarveOrden.panos
import io.github.ruddy2003.vestigium.ui.escenas.AdarveOrden.parapeto
import io.github.ruddy2003.vestigium.ui.escenas.EscenaOrden.Garza
import io.github.ruddy2003.vestigium.ui.escenas.LuzOrden.luzEnElAire
import io.github.ruddy2003.vestigium.ui.escenas.LuzOrden.ocaso
import io.github.ruddy2003.vestigium.ui.escenas.LuzOrden.sombrasDelAdarve
import io.github.ruddy2003.vestigium.ui.escenas.LuzOrden.sombrasDelPatio
import io.github.ruddy2003.vestigium.ui.escenas.LuzOrden.sombrasEnElFondo
import io.github.ruddy2003.vestigium.ui.escenas.LuzOrden.sombrasSobreLosEdificios
import io.github.ruddy2003.vestigium.ui.escenas.ObjetosOrden.sobreElSoporte
import io.github.ruddy2003.vestigium.ui.escenas.ParamoOrden.horaAzul
import io.github.ruddy2003.vestigium.ui.escenas.ParamoOrden.lejos
import io.github.ruddy2003.vestigium.ui.escenas.ParamoOrden.sombrasDeNubes
import io.github.ruddy2003.vestigium.ui.escenas.ParamoOrden.veloDePolvo
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.cisterna
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.corral
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.edificios
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.fondo
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.fragua
import io.github.ruddy2003.vestigium.ui.escenas.PatioOrden.luzDeLaFragua
import io.github.ruddy2003.vestigium.ui.escenas.TorreOrden.garza
import io.github.ruddy2003.vestigium.ui.escenas.TorreOrden.torre
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.rayoFijo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.polvo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoQuieto
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.tiempoVivo
import io.github.ruddy2003.vestigium.vidas.Azar
import io.github.ruddy2003.vestigium.vidas.Clima
import io.github.ruddy2003.vestigium.vidas.Semilla

/**
 * Todo lo que el cuadro de un momento comparte entre sus planos: la luz, el pincel, la Garza de hoy y el tiempo.
 */
internal class VistaOrden(val m: Momento) {
    val l: Luces = EscenaOrden.luces(m)
    val r: Rayo = EscenaOrden.rayo(l)
    val sol = Sol3(r)
    val p = Pincel(l, r, 0f, ColorOrden.LUMBRE)
    val luna: Float = EscenaOrden.luna(l)
    val lz = LuzPlana(p, sol, luna)
    val garza: TorreOrden.Ballena? = TorreOrden.garza(m)
    val nieve: Float = EscenaOrden.nieve(m)
    /** Cuánto polvo hay en el aire, de 0 a 1: los días de polvo, y más el de la tormenta de polvo y el de la sequía. */
    val polvo: Float = if (m.ficha.clima != Clima.POLVO) 0f else if (EscenaOrden.hoyEs(m, "tormenta_polvo") || EscenaOrden.sequia(m)) 1f else 0.7f
}

/**
 * El cuadro de Peñaseca armado, de atrás adelante (ver [EscenaOrden]): el páramo, el patio con su muralla del fondo y sus
 * edificios, la torre y la Garza, el adarve con el parapeto y las almenas, la luz, y el tiempo.
 */
internal object CuadroOrden {
    private val C = ColorOrden

    fun DrawScope.cuadro(m: Momento) {
        val v = VistaOrden(m)
        with(ParamoOrden) { cielo(v) }
        horaAzul(v)
        with(MateriaOrden) { mesetasLejanas(v) }
        lejos(v)
        // El polvo va por capas entre los planos: lo lejano casi se borra («a cien pasos»); el patio, a medias; lo de cerca, poco.
        if (v.polvo > 0f) veloDePolvo(v, CamOrden.H - 50f, 215f, 0.85f * v.polvo)
        sombrasDeNubes(v)
        with(DiasOrden) { enElParamo(v) }
        val enVuelo = EscenaOrden.garza(m) == Garza.VOLANDO
        if (enVuelo) garza(v)
        // El patio: el suelo con sus sombras, la muralla del fondo, los edificios, la cisterna, el corral y la fragua.
        with(PatioOrden) { suelo(v) }
        sombrasDelPatio(v)
        fondo(v)
        sombrasEnElFondo(v)
        val enElPatio = EscenaOrden.garza(m) == Garza.CAIDA || EscenaOrden.garza(m) == Garza.COSIENDO
        edificios(v, lejos = true)
        with(CosasDelPatio) { delFondo(v) }
        with(DiasOrden) { enElPatio(v) }
        // La torre, en medio del patio, y la Garza amarrada (la que se aleja va detrás de la aguja); la caída, delante.
        val detras = EscenaOrden.garza(m) == Garza.AMARRADA && EscenaOrden.rumbo(m) > 0.25f
        if (detras) garza(v)
        torre(v)
        if (EscenaOrden.garza(m) == Garza.AMARRADA && !detras) garza(v)
        if (enElPatio) garza(v)
        edificios(v, lejos = false)
        sombrasSobreLosEdificios(v)
        with(CosasDelPatio) { deCerca(v) }
        cisterna(v)
        corral(v)
        fragua(v)
        luzDeLaFragua(v)
        if (v.polvo > 0f) veloDePolvo(v, 95f, 230f, 0.5f * v.polvo)
        luzEnElAire(v)
        // El adarve: el suelo, sus sombras, el parapeto con las almenas y el objeto del día.
        with(AdarveOrden) { suelo(v) }
        with(MateriaOrden) { sueloDelAdarve(v) }
        with(DiasOrden) { mojado(v) }
        sombrasDelAdarve(v)
        with(AdarveOrden) { charcosDeLosCandiles(v) }
        with(CuboOrden) { charcoDelCandil(v) }
        parapeto(v)
        with(AdarveOrden) { enseres(v) }
        nieveEnLasAlmenas(v)
        panos(v)
        with(DiasOrden) { enLasMurallas(v) }
        objetoDelDia(v)
        ocaso(v)
        luzDeLosCandiles(v)
        with(CuboOrden) { luzDelCandil(v) }
        if (v.polvo > 0f) veloDePolvo(v, 0f, 330f, 0.2f * v.polvo)
        if (m.ficha.clima != Clima.POLVO) tiempoQuieto(v.l, m.ficha.clima, CamOrden.H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("orden", "grano")), 0.6f + 0.4f * v.r.dia)
    }

    /** El objeto del día: el chico en la tronera, el grande de pie en el adarve, apoyado en la segunda almena. */
    private fun DrawScope.objetoDelDia(v: VistaOrden) {
        val m = v.m
        if (ObjetosOrden.esGrande(m)) {
            ObjetosOrden.grande(m, v.p, CamOrden.p(AdarveOrden.PIE_GRANDE), CamOrden.p(AdarveOrden.APOYO_GRANDE), AdarveOrden.escalaEn(AdarveOrden.PIE_GRANDE) * 0.85f)?.let { cosa -> cosa.pintar(this) }
        } else {
            with(ObjetosOrden) { sobreElSoporte(m, v.p, CamOrden.p(AdarveOrden.SOPORTE), AdarveOrden.escalaEn(AdarveOrden.SOPORTE) * 0.7f) }
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val v = VistaOrden(m)
        // El humo de la fragua, que sube por delante de la muralla del fondo y se tumba con el viento.
        val f = EscenaOrden.fragua(m)
        humo(v.l, CamOrden.p(PatioOrden.CHIMENEA), 125f, 12f, reloj, -0.1f - 0.25f * m.viento, 0.3f, 0f, 6, 9, 0.35f + 0.5f * f)
        // Las llamas de los candiles de las almenas.
        val n = EscenaOrden.candiles(m)
        for (i in 0 until n) {
            if (i == 1 && !ObjetosOrden.elCuadroPinta(m, "candil")) continue
            val c = AdarveOrden.candil(i); val k = CamOrden.k(c.x, c.z)
            llama(CamOrden.p(c) + Offset(0f, -0.06f * k), (0.16f * k).coerceIn(1.2f, 9f), reloj, i)
        }
        // El candil grande del cubo.
        if (CuboOrden.arde(v)) { val c = CuboOrden.CANDIL; llama(CamOrden.p(c) + Offset(0f, -0.1f * CamOrden.k(c.x, c.z)), (0.3f * CamOrden.k(c.x, c.z)).coerceIn(2f, 14f), reloj, 40) }
        // Las antorchas de la alarma.
        if (EscenaOrden.alarma(m) && v.l.noche > 0.3f) for (i in 0 until 3) { val a = DiasOrden.antorcha(i); llama(CamOrden.p(a), (0.35f * CamOrden.k(a.x, a.z)).coerceIn(2f, 12f), reloj, 30 + i) }
        if (v.polvo > 0f) polvo(reloj, m.semilla("polvo"))
        tiempoVivo(v.l, m.ficha.clima, reloj, CamOrden.H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val v = VistaOrden(m)
        // Cerca del candil grande del cubo, de noche, su lado cálido.
        val c = CamOrden.p(CuboOrden.CANDIL); val d = (c - en).getDistance()
        val calida = if (CuboOrden.arde(v)) (v.l.noche * 0.6f / (1f + (d / 80f) * (d / 80f))) else 0f
        // De día, una luz pareja (como en Laboratorio): el color del sol, sin que la cabeza se queme ni el cuerpo se vaya al
        // violeta; de noche, la del cuadro, que ya estaba bien.
        val n = v.l.noche.coerceIn(0f, 1f)
        val sol = v.l.solColor; val m0 = maxOf(sol.red, sol.green, sol.blue).coerceAtLeast(0.05f)
        val deDia = lerp(Color.White, Color(sol.red / m0, sol.green / m0, sol.blue / m0), 0.2f).let { Color(it.red * 0.92f, it.green * 0.92f, it.blue * 0.92f) }
        val rayo = if (n < 0.5f) rayoFijo(if (v.p.solALaDerecha) 0.45f else -0.45f, 50f, 0.3f, C.SOMBRA, 0.5f) else v.r
        return LuzGato(rayo, v.l.noche) { col ->
            val dia = Color(col.red * deDia.red, col.green * deDia.green, col.blue * deDia.blue, col.alpha)
            val base = lerp(dia, v.l.tono(col), (n * 1.4f).coerceIn(0f, 1f))
            if (calida > 0.03f) lerp(base, mult(col, C.LUMBRE, 0.5f), (calida * 0.5f).coerceIn(0f, 1f)) else base
        }
    }

    /** Nada plantado en el suelo del gato: el objeto grande pisa más al fondo del adarve, junto a la almena. */
    fun estorbos(@Suppress("UNUSED_PARAMETER") m: Momento): List<Float> = emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, AdarveOrden.GATO_DESDE, AdarveOrden.PISO_GATO, hasta = AdarveOrden.GATO_HASTA))
        val mir = AdarveOrden.MIRADOR
        add(Ancla(TipoAncla.MIRADOR, mir.x, mir.y))                                // sentado en el taburete de la guardia
        // Dónde está el objeto (el gato se pone a su lado y, quieto, se aparta de él; el abrigo queda lejos, junto al taburete).
        val obj = if (ObjetosOrden.esGrande(m)) CamOrden.p(AdarveOrden.PIE_GRANDE).x else CamOrden.p(AdarveOrden.SOPORTE).x
        add(Ancla(TipoAncla.OBJETO, obj.coerceIn(AdarveOrden.GATO_DESDE + 10f, AdarveOrden.GATO_HASTA - 10f), AdarveOrden.PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, AdarveOrden.GATO_DESDE + 26f, AdarveOrden.PISO_GATO))  // junto al taburete, al abrigo del viento
        if (EscenaOrden.fiesta(m)) { val q = CamOrden.p(AdarveOrden.PENDON); add(Ancla(TipoAncla.ADORNO, q.x.coerceIn(10f, 370f), q.y.coerceIn(10f, 275f))) }
    }
}
