package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.avisos.Avisos
import io.github.ruddy2003.vestigium.ciencia.Campanas
import io.github.ruddy2003.vestigium.ui.Cara
import io.github.ruddy2003.vestigium.ui.EstadoTema
import io.github.ruddy2003.vestigium.ui.Estilo
import io.github.ruddy2003.vestigium.ui.TemaId
import io.github.ruddy2003.vestigium.ui.Temas
import io.github.ruddy2003.vestigium.ui.Textos
import io.github.ruddy2003.vestigium.ui.nombres.NombresPsicodelia
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La voz del tema Psicodelia (versión 2; `docs/diseno/temas/psicodelia.md`, «La voz»; `psicodelia_plan.md`, 3.2): el tema
 * muestra, no comenta. Ningún texto de interfaz del tema, en ninguna de sus dos caras, habla del consumo de quien usa la app,
 * celebra con jerga ni toca el seguimiento privado. Lo que el tema deja con la voz de la app (lo privado y lo de Sustancias,
 * `NombresPsicodelia.NEUTROS`) no cuenta como voz del tema.
 *
 * Fuera de esta prueba quedan los textos culturales, que sí pueden nombrar las cosas de la época: las frases del día
 * (`Frases`), las fechas del calendario (`Temas.marca`) y el cuento de David (`vidas/`, con sus propias pruebas).
 */
class VozPsicodeliaPrueba {
    @Test fun laVozDelTemaNoHablaDelConsumo() {
        val textos = textosDelTema()
        assertTrue("La prueba no encontró textos del tema", textos.size > 200)
        val fallos = textos.flatMap { (donde, texto) ->
            (LO_PRIVADO + JERGA_QUE_CELEBRA + JERGA_SOLO_EN_LA_VOZ + NOMBRES_DE_SUSTANCIAS).mapNotNull { r -> r.find(texto)?.let { "$donde: «${it.value}» en «$texto»" } }
        }
        assertTrue(fallos.joinToString("\n"), fallos.isEmpty())
    }

    /** La descripción del selector puede decir, con sobriedad, que el tema se enciende con un consumo; nada más. */
    @Test fun laDescripcionNoCelebraNiTocaLoPrivado() {
        val d = TemaId.PSICODELIA.descripcion
        (LO_PRIVADO + JERGA_QUE_CELEBRA + JERGA_SOLO_EN_LA_VOZ).forEach { r -> assertNull("«${r.find(d)?.value}» en la descripción", r.find(d)) }
        (NOMBRES_DE_SUSTANCIAS - CONSUMO).forEach { r -> assertNull("«${r.find(d)?.value}» en la descripción", r.find(d)) }
    }

    /** Lo privado y lo de Sustancias se quedan con el título de la app, sin nombre ni comentario del tema, en las dos caras. */
    @Test fun loPrivadoVaConLaVozDeLaApp() {
        for (cara in Cara.entries) conCara(cara) {
            NombresPsicodelia.NEUTROS.forEach { t ->
                assertEquals("$cara: $t", t to null, Estilo.pantalla(t, null))
                assertEquals("$cara: $t", t to null, Estilo.seccion(t, null))
            }
        }
    }

    /** Las dos caras hablan distinto donde la guía lo pide (el comentario de la pantalla, «atrás», los avisos). */
    @Test fun dosVocesSegunLaCara() {
        val base = conCara(Cara.BASE) { Estilo.pantalla("Hoy", null).second to Estilo.atras }
        val intensa = conCara(Cara.INTENSA) { Estilo.pantalla("Hoy", null).second to Estilo.atras }
        assertTrue("${base.first}", base.first!!.startsWith("Lado A · pista "))
        assertTrue("${intensa.first}", intensa.first!!.startsWith("Lado B · pista "))
        assertTrue(base.second != intensa.second)
        assertTrue(Avisos.vozPsicodelia(Cara.BASE).animo != Avisos.vozPsicodelia(Cara.INTENSA).animo)
        assertTrue(Avisos.vozEnfoquePsicodelia(Cara.BASE).despertar != Avisos.vozEnfoquePsicodelia(Cara.INTENSA).despertar)
    }

    /** Corre [bloque] con Psicodelia puesto y la cara fijada (con intensidad 0), y deja todo como estaba. */
    private fun <T> conCara(cara: Cara, bloque: () -> T): T {
        EstadoTema.forzar(cara, 0f)
        return try { Pendientes.conTema(TemaId.PSICODELIA, bloque) } finally { EstadoTema.forzar(null, null) }
    }

    /** Todos los textos de interfaz del tema, en sus dos caras, cada uno con de dónde sale. */
    private fun textosDelTema(): List<Pair<String, String>> {
        val t = mutableListOf<Pair<String, String>>()
        NombresPsicodelia.M.forEach { (k, v) -> t += "NombresPsicodelia[$k]" to v }
        Temas.PSICODELIA.forEach { (k, v) -> t += "Temas.PSICODELIA[$k]" to v }
        val titulos = (NombresPsicodelia.M.keys + Temas.PSICODELIA.keys + NombresPsicodelia.NEUTROS + TITULOS_NUEVOS + UnicidadTemas.PANTALLAS).toSortedSet()
        for (cara in Cara.entries) conCara(cara) {
            for (titulo in titulos) {
                listOf("pantalla" to Estilo.pantalla(titulo, null), "seccion" to Estilo.seccion(titulo, null)).forEach { (donde, par) ->
                    val (nombre, comentario) = par
                    if (nombre != titulo) t += "$cara $donde($titulo)" to nombre
                    // El comentario repite el título de la app: se mira solo lo que agrega el tema.
                    comentario?.let { t += "$cara comentario de $donde($titulo)" to it.replace(titulo.lowercase(), "") }
                }
            }
            t += "$cara comentario" to Estilo.comentario
            t += "$cara atras" to Estilo.atras
            t += "$cara prefijo" to Estilo.prefijo
            t += "$cara fecha grande" to Estilo.fechaGrande.abre
            t += "$cara racha" to Estilo.racha(5)
            t += "$cara racha de uno" to Estilo.racha(1)
            t += "$cara fecha" to Estilo.fecha("2026-10-17")
            t += "$cara mes" to Estilo.mes(10)
            t += "$cara semana" to Estilo.semana.joinToString(" ")
            CoberturaTemas.CLAVES_WIDGET.forEach { t += "$cara widget($it)" to Estilo.widget(it) }
            Textos.CLAVES.forEach { t += "$cara texto($it)" to Estilo.texto(it) }
            UnicidadTemas.CLAVES_DIA.forEach { t += "$cara clave($it)" to Estilo.clave(it) }
        }
        for (cara in Cara.entries) {
            Avisos.vozPsicodelia(cara).let { v ->
                listOf(v.animo, v.animoTexto, v.sueno, v.suenoTexto, v.diario, v.diarioTexto, v.dificil, v.escalas, v.carta, v.cartaTexto,
                    v.logro, v.logros, v.anio, v.anioTexto).forEach { t += "$cara Avisos.voz" to it }
            }
            Avisos.vozEnfoquePsicodelia(cara).let { v ->
                listOf(v.descanso, v.consejo, v.finDescanso, v.despertar, v.terminaste, v.meta).forEach { t += "$cara Avisos.vozEnfoque" to it }
            }
        }
        Campanas.TITULOS[TemaId.PSICODELIA.id].orEmpty().forEach { t += "Campanas.TITULOS" to it }
        Campanas.ETAPAS[TemaId.PSICODELIA.id].orEmpty().forEach { t += "Campanas.ETAPAS" to it }
        return t
    }

    private companion object {
        /** Los títulos de `NombresNuevos` (las pantallas que llegaron después de los diccionarios de cada tema). */
        val TITULOS_NUEVOS = listOf("La cinta", "Constelación", "Hall de logros", "Campañas", "Impulsos y recaídas", "Estado de los datos",
            "Enfoque y siestas", "Ver tu vida", "Tus atajos", "Logro conseguido", "Este día hace un año", "Elegir campaña", "Campañas anteriores")

        /** Las palabras del consumo en general, que la descripción del selector sí puede usar («con un consumo anotado en curso»). */
        val CONSUMO = NOMBRES_DE_SUSTANCIAS.filter { it.pattern.contains("consum") }.toSet()
    }
}

// Las listas (LO_PRIVADO, JERGA_QUE_CELEBRA, JERGA_SOLO_EN_LA_VOZ, NOMBRES_DE_SUSTANCIAS) están en `ListasConsumo.kt`,
// compartidas con las pruebas del cuento de David.
