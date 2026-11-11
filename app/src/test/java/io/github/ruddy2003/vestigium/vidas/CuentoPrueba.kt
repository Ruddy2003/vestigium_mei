package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import io.github.ruddy2003.vestigium.vidas.piezas.TodasLasPiezas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/**
 * El cuento del día. Las pruebas generales valen para todos los personajes que tengan piezas registradas en
 * [TodasLasPiezas]: las tandas de textos solo tienen que registrar las suyas. Las del final son las de Demeas.
 */
class CuentoPrueba {
    private val todas = TodasLasPiezas.TODAS
    private fun cal(p: Piezas) = Calendarios.de(p.vida)!!
    private fun quien(p: Piezas) = OtrasVidas.porId(p.vida)!!
    private fun cuento(p: Piezas, dia: String, hora: Double = 10.0) = Cuento.de(FichaDelDia.de(dia, quien(p), cal(p)), quien(p), cal(p), p, hora)
    private fun dias(desde: Int, hasta: Int) = generateSequence(LocalDate.of(desde, 1, 1)) { it.plusDays(1) }.takeWhile { it.year <= hasta }.map { it.toString() }.toList()
    private val ANIO = Regex("""\b(1[0-9]{3}|20[0-9]{2})\b""")

    // ---------- Las piezas ----------

    @Test fun lasPiezasEstanBienEscritas() {
        assertTrue("No hay piezas registradas", todas.isNotEmpty())
        todas.forEach { p -> assertEquals("Piezas de ${p.vida}", emptyList<String>(), p.problemas(cal(p))) }
    }

    @Test fun losParrafosSonCortosYNingunoSeRepiteEnLasPiezas() {
        todas.forEach { p ->
            val largos = p.variantes.values.flatten().map { it.texto } + p.especiales.values.flatMap { t -> t.dias.flatMap { it.values } + t.sigue.values.flatten() }
            largos.forEach { s ->
                assertTrue("${p.vida}: párrafo de ${s.length} letras: «${s.take(50)}…»", s.length <= 470)
                assertTrue("${p.vida}: no termina en punto ni en comilla: «${s.takeLast(30)}»", s.last() in ".»}")
                assertFalse("${p.vida}: signos de admiración fuera de un grito: «${s.take(50)}…»", '!' in s && '«' !in s)
            }
            assertEquals("${p.vida}: hay textos repetidos", largos.size, largos.toSet().size)
        }
    }

    // ---------- Lo que pide el plan ----------

    @Test fun noApareceNingunAnioActual() {
        todas.forEach { p -> dias(2026, 2032).forEach { dia -> listOf(6.0, 18.0).forEach { hora ->
            val c = cuento(p, dia, hora)
            (c.parrafos + c.titulo).forEach { assertFalse("${p.vida}, $dia: «$it»", ANIO.containsMatchIn(it)) }
        } } }
    }

    @Test fun ningunCuentoSeRepite() {
        // Ni en 365 días ni en los seis años que van del avance de 2026 al final de 2031, a cualquier hora del día.
        todas.forEach { p -> listOf(3.0, 5.5, 10.0, 12.5, 15.0, 19.0).forEach { hora ->
            val textos = dias(2026, 2031).map { cuento(p, it, hora).parrafos.joinToString("\n") }
            assertEquals("${p.vida}, a las $hora: hay cuentos repetidos", textos.size, textos.toSet().size)
        } }
    }

    @Test fun cadaDiaEspecialDiceSuCerteza() {
        todas.forEach { p -> dias(2026, 2032).forEach { dia ->
            val c = cuento(p, dia)
            assertEquals("$dia: la marca de día especial", c.ficha.especial, c.especial)
            assertEquals("$dia: una nota por cada día especial", c.ficha.especiales.size, c.notas.size)
            c.ficha.especiales.zip(c.notas).forEach { (e, n) ->
                assertEquals(e.dia.certeza, n.certeza); assertEquals(e.dia.fuente, n.fuente); assertEquals(e.dia.titulo, n.titulo)
                if (n.certeza == CertezaDia.INVENTADO) assertNull("$dia: lo inventado no lleva fuente", n.fuente)
            }
            if (c.especial) assertEquals("$dia: el título es el del día que manda", c.ficha.principal!!.dia.titulo, c.titulo)
        } }
    }

    @Test fun laNotaDeFiccionSiempreEsta() {
        todas.forEach { p ->
            val esperada = if (quien(p).ficticia) Cuento.NOTA_FICCION else Cuento.NOTA_HISTORICA
            listOf("2026-11-11", "2027-03-18", "2029-08-01", "2035-02-02").forEach { assertEquals(esperada, cuento(p, it).nota) }
        }
        assertEquals("Relato de ficción con base histórica", Cuento.NOTA_HISTORICA)
    }

    // ---------- La forma del cuento ----------

    @Test fun siempreSonOchoParrafosEnterosYSinHuecos() {
        todas.forEach { p -> dias(2026, 2033).forEach { dia -> listOf(2.0, 9.0, 13.0, 20.0).forEach { hora ->
            val c = cuento(p, dia, hora)
            assertEquals("$dia: párrafos", Parrafo.entries.size, c.parrafos.size)
            c.parrafos.forEachIndexed { i, s ->
                val donde = "${p.vida}, $dia a las $hora, ${Parrafo.entries[i].nombre}"
                assertTrue("$donde: muy corto: «$s»", s.length >= 40)
                assertTrue("$donde: muy largo (${s.length})", s.length <= 600)
                assertFalse("$donde: quedó un hueco: «$s»", '{' in s || '}' in s || '|' in s)
                assertFalse("$donde: espacios de más: «$s»", "  " in s || " ." in s || s != s.trim())
                assertTrue("$donde: no empieza con mayúscula: «$s»", s.first().isUpperCase() || s.first() in "«¡¿")
            }
            assertTrue("$dia: título vacío", c.titulo.isNotBlank())
            assertTrue("$dia: el cuento entero es muy largo (${c.parrafos.sumOf { it.length }})", c.parrafos.sumOf { it.length } <= 2900)
        } } }
    }

    @Test fun elCuentoTieneElLargoQueAproboElAutor() {
        // Unas 1,950 letras en promedio (minuto y medio de lectura), y ningún día se queda corto ni se pasa de largo.
        todas.forEach { p ->
            val largos = dias(2027, 2027).flatMap { dia -> listOf(9.0, 19.0).map { cuento(p, dia, it).parrafos.sumOf { x -> x.length } } }
            val medio = largos.average()
            assertTrue("${p.vida}: el cuento tiene en promedio ${medio.toInt()} letras", medio in 1800.0..2150.0)
            assertTrue("${p.vida}: el más corto tiene ${largos.min()} letras", largos.min() >= 1450)
        }
    }

    @Test fun esElMismoCuentoTodoElDiaSalvoElParrafoDelTrabajo() {
        todas.forEach { p -> listOf("2027-02-03", "2027-08-10", "2029-11-30").forEach { dia ->
            val horas = listOf(1.0, 5.5, 8.0, 12.5, 15.0, 18.0, 22.0).map { cuento(p, dia, it) }
            assertEquals("$dia: el título cambia con la hora", 1, horas.map { it.titulo }.toSet().size)
            assertEquals(cuento(p, dia, 8.0), cuento(p, dia, 8.0))
            // De noche duerme: a la 1:00 y a las 8:00 el párrafo del trabajo no dice lo mismo.
            assertNotEquals("$dia: el trabajo no cambia con la hora", horas[0].parrafos[Parrafo.TRABAJO.ordinal], horas[2].parrafos[Parrafo.TRABAJO.ordinal])
        } }
    }

    @Test fun enUnDiaNormalElTituloEsElDelObjeto() {
        todas.forEach { p ->
            val normales = dias(2027, 2027).map { cuento(p, it) }.filter { !it.especial }
            assertTrue(normales.size > 150)
            normales.forEach { c -> assertEquals(p.objetos.first { it.clave == c.ficha.objeto }.titulo, c.titulo) }
            assertTrue("${p.vida}: dos días normales seguidos con el mismo título", normales.zipWithNext().none { (a, b) -> LocalDate.parse(a.dia).plusDays(1).toString() == b.dia && a.titulo == b.titulo })
        }
    }

    @Test fun elPrimerDiaDeUnEspecialCambiaTresOCuatroParrafos() {
        todas.forEach { p -> p.temporadasEscritas.forEach { t ->
            FichaDelDia.inicios(cal(p), Temporadas.PRIMER_ANIO + t - 1).forEach { (d, inicio) ->
                val ficha = FichaDelDia.de(inicio.toString(), quien(p), cal(p))
                if (ficha.principal?.dia?.id != d.id) return@forEach   // ese día manda otro más corto
                val c = Cuento.de(ficha, quien(p), cal(p), p, 10.0)
                val escritos = p.especiales.getValue(d.id).dias.first().values.map { Cuento.llenar(it, mapOf("nombre" to quien(p).nombre), p.genero) }
                val puestos = c.parrafos.count { it in escritos }
                assertEquals("${p.vida}, «${d.id}» ($inicio): párrafos del día especial", escritos.size, puestos)
                assertTrue("${p.vida}, «${d.id}»: cambia $puestos", puestos in 3..4)
            }
        } }
    }

    @Test fun unEspecialLargoNoRepiteElMismoTextoTodosLosDias() {
        todas.forEach { p -> p.temporadasEscritas.forEach { t ->
            FichaDelDia.inicios(cal(p), Temporadas.PRIMER_ANIO + t - 1).filter { it.first.duracion >= 5 }.forEach { (d, inicio) ->
                val textos = (0 until d.duracion).map { cuento(p, inicio.plusDays(it.toLong()).toString()).parrafos }
                // De un día al siguiente cambian al menos cuatro párrafos.
                textos.zipWithNext().forEachIndexed { i, (a, b) ->
                    assertTrue("${p.vida}, «${d.id}», día ${i + 2}: se parece demasiado al anterior", a.zip(b).count { (x, y) -> x != y } >= 4)
                }
            }
        } }
    }

    @Test fun unPersonajeSinPiezasNoTieneCuento() {
        assertNull(Cuento.de("2027-01-01", "paleolitico", 10.0))
        assertNull(Cuento.de("2027-01-01", "no_existe", 10.0))
        todas.forEach { assertNotNull(Cuento.de("2027-01-01", it.vida, 10.0)) }
        assertEquals(cuento(todas.first(), "2028-04-04", 7.0), Cuento.de("2028-04-04", todas.first().vida, 7.0))
    }

    // ---------- El motor ----------

    @Test fun losHuecosYElGenero() {
        val h = mapOf("nombre" to "Kei", "entorno" to "", "cielo" to "Llueve.")
        assertEquals("Kei llega cansada.", Cuento.llenar("{nombre} llega {cansado|cansada}.", h, Genero.FEMENINO))
        assertEquals("Kei llega cansado.", Cuento.llenar("{nombre} llega {cansado|cansada}.", h, Genero.MASCULINO))
        assertEquals("Es {él|ella}", "Es ella", Cuento.llenar("Es {él|ella}", h, Genero.FEMENINO))
        // Un hueco que queda vacío no deja espacios de más.
        assertEquals("La calle es recta. Llueve.", Cuento.llenar("La calle es recta. {entorno} {cielo}", h, Genero.MASCULINO))
        assertEquals("Llueve. Fin.", Cuento.llenar("{entorno} {cielo} Fin.", h, Genero.MASCULINO))
    }

    @Test fun losNumerosConLetras() {
        assertEquals(listOf("un", "quince", "veintiún", "veintiséis", "treinta", "treinta y un", "treinta y cuatro", "cuarenta y siete", "noventa y nueve"),
            listOf(1, 15, 21, 26, 30, 31, 34, 47, 99).map { Cuento.enLetras(it) })
        assertEquals("120", Cuento.enLetras(120))
    }

    @Test fun lasVariantesConFechaYConEstado() {
        val c = Calendarios.de("remero")!!
        val a = Variante("siempre"); val b = Variante("hasta", hasta = "muere_estraton"); val d = Variante("desde", desde = "llega_eufronio")
        val mar = Variante("mar", estado = "mar"); val come = Variante("come", hacer = OtrasVidas.Hacer.COMER); val comeMar = Variante("come en el mar", "mar", OtrasVidas.Hacer.COMER)
        fun lugar(t: Int, fecha: String) = (t - 1) * 365 + MesDia.indice(fecha)
        fun textos(l: List<Variante>, hoy: Int, estado: String? = null, hacer: OtrasVidas.Hacer? = null, mes: Int = 1) = Cuento.candidatas(l, c, hoy, estado, hacer, mes).map { it.texto }
        assertEquals(listOf("siempre", "hasta"), textos(listOf(a, b, d), lugar(1, "03-01")))
        assertEquals(listOf("siempre", "hasta", "desde"), textos(listOf(a, b, d), lugar(1, "06-12")))
        assertEquals(listOf("siempre", "hasta", "desde"), textos(listOf(a, b, d), lugar(2, "06-21")))
        assertEquals(listOf("siempre", "desde"), textos(listOf(a, b, d), lugar(2, "06-22")))
        assertEquals(listOf("siempre", "desde"), textos(listOf(a, b, d), lugar(5, "12-31")))
        // El estado: si hay variantes del estado, salen solo esas; si no, las de todos los días.
        assertEquals(listOf("mar"), textos(listOf(a, mar), 0, "mar"))
        assertEquals(listOf("siempre"), textos(listOf(a, mar), 0, null))
        assertEquals(listOf("siempre"), textos(listOf(a), 0, "mar"))
        // La actividad: primero la del estado, luego la de todos los días, y solo después lo que no tiene actividad.
        assertEquals(listOf("come en el mar"), textos(listOf(a, come, comeMar, mar), 0, "mar", OtrasVidas.Hacer.COMER))
        assertEquals(listOf("come"), textos(listOf(a, come, mar), 0, "mar", OtrasVidas.Hacer.COMER))
        assertEquals(listOf("come"), textos(listOf(a, come, comeMar), 0, null, OtrasVidas.Hacer.COMER))
        assertEquals(listOf("mar"), textos(listOf(a, come, mar), 0, "mar", OtrasVidas.Hacer.DORMIR))
        assertEquals(listOf("siempre"), textos(listOf(a, come), 0, null, null))
        // Los meses: lo que solo se hace en verano no sale en invierno.
        val verano = Variante("verano", meses = setOf(6, 7, 8))
        assertEquals(listOf("siempre"), textos(listOf(a, verano), 0, mes = 1))
        assertEquals(listOf("siempre", "verano"), textos(listOf(a, verano), 0, mes = 7))
    }

    // ---------- Demeas ----------

    private val demeas get() = todas.first { it.vida == "remero" }

    @Test fun demeasEstaEnElMarDeJulioASeptiembre() {
        fun estado(dia: String) = cuento(demeas, dia).ficha.estado
        assertNull(estado("2027-07-04")); assertEquals("mar", estado("2027-07-05")); assertEquals("mar", estado("2027-09-17")); assertNull(estado("2027-09-18"))
        // En el mar, la mañana, el pueblo, la gente y el anochecer son de las variantes del mar.
        val delMar = demeas.variantes.mapValues { (_, l) -> l.filter { it.estado == "mar" }.map { Cuento.llenar(it.texto, mapOf("nombre" to "Demeas"), Genero.MASCULINO) }.toSet() }
        dias(2027, 2027).filter { it > "2027-07-05" && it < "2027-09-18" }.forEach { dia ->
            val x = cuento(demeas, dia, 10.0)
            listOf(Parrafo.MANANA, Parrafo.PUEBLO, Parrafo.GENTE, Parrafo.ANOCHECER, Parrafo.TRABAJO).forEach { lugar ->
                if (!x.especial) assertTrue("$dia, ${lugar.nombre}: no es del mar: «${x.parrafos[lugar.ordinal]}»", x.parrafos[lugar.ordinal] in delMar.getValue(lugar))
            }
        }
        // Y en tierra no sale ninguna.
        dias(2027, 2027).filter { it < "2027-07-05" || it >= "2027-09-18" }.forEach { dia ->
            val x = cuento(demeas, dia, 10.0)
            Parrafo.entries.forEach { lugar -> assertFalse("$dia: variante del mar en tierra", x.parrafos[lugar.ordinal] in delMar.getValue(lugar)) }
        }
    }

    @Test fun enElCuentoDeDemeasNadieApareceAntesNiDespuesDeTiempo() {
        fun todo(dia: String) = listOf(3.0, 10.0, 12.5, 19.0).flatMap { cuento(demeas, it.let { h -> dia }, it).parrafos }.joinToString(" ")
        // Su padre muere el 22 de junio de la temporada 2: desde ese día las variantes de todos los días no lo nombran.
        dias(2028, 2031).filter { it >= "2028-06-23" }.forEach { dia ->
            val c = cuento(demeas, dia)
            if (!c.especial) assertFalse("$dia: Estratón sigue vivo en el cuento", "Estratón" in todo(dia) || "Su padre remó" in todo(dia))
        }
        // El tío no vive en el patio antes de llegar, y Clío no está casada antes de su boda.
        dias(2027, 2027).filter { it < "2027-06-12" }.forEach { assertFalse("$it: Eufronio ya está en el patio", "se ha quedado a vivir en el patio" in todo(it)) }
        dias(2027, 2027).filter { it < "2027-01-27" }.forEach { assertFalse("$it: Clío ya está casada", "Desde que se casó" in todo(it)) }
        assertTrue(dias(2027, 2027).any { "se ha quedado a vivir en el patio" in todo(it) })
        assertTrue(dias(2027, 2027).any { "Desde que se casó" in todo(it) })
    }

    @Test fun elCuentoDeDemeasSigueLaGuerra() {
        // Antes de la invasión no hay nada que ver desde el muro; después, sí.
        val antes = dias(2027, 2027).filter { it < "2027-06-10" }.flatMap { cuento(demeas, it).parrafos }.joinToString(" ")
        val despues = dias(2027, 2027).filter { it > "2027-09-18" }.flatMap { cuento(demeas, it).parrafos }.joinToString(" ")
        listOf("chozas", "llanura quemada", "mirar hacia su tierra").forEach { assertFalse("«$it» antes de la invasión", it in antes) }
        assertTrue("Después de la invasión no se nota nada", listOf("chozas", "llanura quemada", "mirar hacia su tierra").any { it in despues })
        // El día del eclipse.
        val eclipse = cuento(demeas, "2027-08-03", 14.0)
        assertEquals("El sol se vuelve una media luna en pleno día", eclipse.titulo)
        assertTrue("media luna" in eclipse.parrafos[Parrafo.TRABAJO.ordinal])
        assertEquals(CertezaDia.HISTORICO, eclipse.notas.first().certeza)
        assertNotNull(eclipse.notas.first().fuente)
        // En invierno el «Aura» está en su cobertizo: de noviembre a abril ningún día normal lo saca a remar.
        dias(2027, 2030).filter { it.substring(5, 7).toInt() !in 5..10 }.forEach { dia ->
            val x = cuento(demeas, dia, 9.0)
            if (!x.especial) listOf("ha salido a ejercitarse", "está en su banco", "si hoy se sale").forEach { assertFalse("$dia: rema en invierno", it in x.parrafos[Parrafo.TRABAJO.ordinal]) }
        }
        // La edad que avanza sale con letras.
        assertEquals(30, cuento(demeas, "2026-12-01").ficha.edad); assertEquals(34, cuento(demeas, "2031-05-05").ficha.edad)
        assertTrue(dias(2031, 2031).any { "A sus treinta y cuatro años" in cuento(demeas, it).parrafos[Parrafo.PENSAMIENTO.ordinal] })
        // Una fiesta de otra temporada que comparte id usa el mismo texto: las Leneas de 2029.
        assertTrue("Leneas" in cuento(demeas, "2029-01-29").parrafos[Parrafo.PUEBLO.ordinal])
        // Un día especial que todavía no tiene texto (los escribe la tanda de su año) conserva su título y su nota.
        val sinTexto = cuento(demeas, "2029-10-05")
        assertEquals("Muere Pericles", sinTexto.titulo); assertEquals(1, sinTexto.notas.size)
    }
}
