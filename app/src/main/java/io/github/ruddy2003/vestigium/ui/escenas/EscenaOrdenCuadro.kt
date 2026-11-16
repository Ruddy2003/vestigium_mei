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
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Luces
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Pincel
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.Rayo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.grano
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.humo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.llama
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.mult
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.polvo
import io.github.ruddy2003.vestigium.ui.escenas.paisaje.resplandor
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
        lejos(v)
        sombrasDeNubes(v)
        with(AdarveOrden) { cuerdaDeLaCabria(v) }
        val enVuelo = EscenaOrden.garza(m) == Garza.VOLANDO
        if (enVuelo) garza(v)
        // El patio: el suelo con sus sombras, la muralla del fondo, los edificios, la cisterna, el corral y la fragua.
        with(PatioOrden) { suelo(v) }
        sombrasDelPatio(v)
        fondo(v)
        sombrasEnElFondo(v)
        val enElPatio = EscenaOrden.garza(m) == Garza.CAIDA || EscenaOrden.garza(m) == Garza.COSIENDO
        edificios(v)
        sombrasSobreLosEdificios(v)
        if (enElPatio) garza(v)
        cisterna(v)
        corral(v)
        fragua(v)
        luzDeLaFragua(v)
        if (v.polvo > 0f) veloDePolvo(v, 120f, 200f, 0.4f * v.polvo)
        // La torre y la Garza amarrada (la que se aleja va detrás de la aguja).
        val detras = EscenaOrden.garza(m) == Garza.AMARRADA && EscenaOrden.rumbo(m) > 0.25f
        if (detras) garza(v)
        torre(v)
        if (EscenaOrden.garza(m) == Garza.AMARRADA && !detras) garza(v)
        luzEnElAire(v)
        // El adarve: el suelo, sus sombras, el parapeto con las almenas y el objeto del día.
        with(AdarveOrden) { suelo(v) }
        sombrasDelAdarve(v)
        parapeto(v)
        with(AdarveOrden) { enseres(v) }
        nieveEnLasAlmenas(v)
        panos(v)
        objetoDelDia(v)
        ocaso(v)
        luzDeLosCandiles(v)
        if (v.polvo > 0f) veloDePolvo(v, 150f, 300f, 0.22f * v.polvo)
        tiempoQuieto(v.l, m.ficha.clima, CamOrden.H, m.semilla("tiempo"))
        grano(Azar(Semilla.de("orden", "grano")), 0.6f + 0.4f * v.r.dia)
    }

    /** El objeto del día: el chico en la tronera, el grande de pie en el adarve, apoyado en la segunda almena. */
    private fun DrawScope.objetoDelDia(v: VistaOrden) {
        val m = v.m
        if (ObjetosOrden.esGrande(m)) {
            ObjetosOrden.grande(m, v.p, CamOrden.p(AdarveOrden.PIE_GRANDE), CamOrden.p(AdarveOrden.APOYO_GRANDE), AdarveOrden.escalaEn(AdarveOrden.PIE_GRANDE))?.let { cosa -> cosa.pintar(this) }
        } else {
            with(ObjetosOrden) { sobreElSoporte(m, v.p, CamOrden.p(AdarveOrden.SOPORTE), AdarveOrden.escalaEn(AdarveOrden.SOPORTE)) }
        }
    }

    // ---------------------------------------------------------------- Lo que se mueve

    fun DrawScope.vivo(m: Momento, reloj: Float) {
        val v = VistaOrden(m)
        // El humo de la fragua, que sube por delante de la muralla del fondo y se tumba con el viento.
        val f = EscenaOrden.fragua(m)
        humo(v.l, CamOrden.p(PatioOrden.CHIMENEA), 70f, 9f, reloj, 0.25f + 0.6f * m.viento, 0.3f, 0f, 5, 9, 0.3f + 0.5f * f)
        // Las llamas de los candiles de las almenas.
        val n = EscenaOrden.candiles(m)
        for (i in 0 until n) {
            if (i == 0 && !ObjetosOrden.elCuadroPinta(m, "candil")) continue
            val c = AdarveOrden.candil(i); val k = CamOrden.k(c.x, c.z)
            llama(CamOrden.p(c) + Offset(0f, -0.06f * k), (0.16f * k).coerceIn(1.2f, 9f), reloj, i)
        }
        if (v.polvo > 0f) polvo(reloj, m.semilla("polvo"))
        tiempoVivo(v.l, m.ficha.clima, reloj, CamOrden.H, m.semilla("tiempo"))
    }

    // ---------------------------------------------------------------- El gato

    fun luzDelGato(m: Momento, en: Offset): LuzGato {
        val v = VistaOrden(m)
        // Cerca del candil de la almena de delante, de noche, su lado cálido.
        val c = CamOrden.p(AdarveOrden.candil(0)); val d = (c - en).getDistance()
        val calida = if (EscenaOrden.candiles(m) > 0 && ObjetosOrden.elCuadroPinta(m, "candil")) (v.l.noche * 0.6f / (1f + (d / 80f) * (d / 80f))) else 0f
        return LuzGato(v.r, v.l.noche) { col -> if (calida > 0.03f) lerp(v.l.tono(col), mult(col, C.LUMBRE, 0.5f), (calida * 0.5f).coerceIn(0f, 1f)) else v.l.tono(col) }
    }

    /** Nada plantado en el suelo del gato: el objeto grande pisa más al fondo del adarve, junto a la almena. */
    fun estorbos(@Suppress("UNUSED_PARAMETER") m: Momento): List<Float> = emptyList()

    fun anclas(m: Momento): List<Ancla> = buildList {
        add(Ancla(TipoAncla.SUELO, AdarveOrden.GATO_DESDE, AdarveOrden.PISO_GATO, hasta = AdarveOrden.GATO_HASTA))
        val mir = AdarveOrden.MIRADOR
        add(Ancla(TipoAncla.MIRADOR, mir.x, mir.y))                                // sentado en la tronera de la segunda almena
        val obj = if (ObjetosOrden.esGrande(m)) CamOrden.p(AdarveOrden.PIE_GRANDE).x else CamOrden.p(AdarveOrden.SOPORTE).x
        add(Ancla(TipoAncla.OBJETO, obj.coerceIn(AdarveOrden.GATO_DESDE + 10f, AdarveOrden.GATO_HASTA - 10f), AdarveOrden.PISO_GATO))
        add(Ancla(TipoAncla.ABRIGO, AdarveOrden.GATO_HASTA - 8f, AdarveOrden.PISO_GATO))    // al pie de la almena de delante, al abrigo del viento
        if (EscenaOrden.fiesta(m)) add(Ancla(TipoAncla.ADORNO, CamOrden.p(CamOrden.PARAPETO, CamOrden.ALTO_ALMENA, AdarveOrden.almena(0) + 0.6f).x.coerceAtMost(372f), 150f))
    }
}
