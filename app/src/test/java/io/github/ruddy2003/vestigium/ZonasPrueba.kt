package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.CambioZona
import io.github.ruddy2003.vestigium.data.Diccionario
import io.github.ruddy2003.vestigium.data.EntornoDia
import io.github.ruddy2003.vestigium.data.Experimento
import io.github.ruddy2003.vestigium.data.Experimentos
import io.github.ruddy2003.vestigium.data.Fechas
import io.github.ruddy2003.vestigium.data.Macrofactor
import io.github.ruddy2003.vestigium.data.Serie
import io.github.ruddy2003.vestigium.sensores.Entorno
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDateTime
import java.time.OffsetDateTime

/** Lo que se cerró en el esquema 15 (H-1-7, H-3-4, H-7-10, H-7-14), con datos inventados. */
class ZonasPrueba {
    @After fun sinHistorial() = Fechas.usarHistorial(emptyList())

    private fun ms(iso: String) = OffsetDateTime.parse(iso).toInstant().toEpochMilli()

    // ---- H-1-7: cada instante con la zona que tenía el teléfono entonces.
    @Test fun horaLocalDeEntonces() {
        // En la Ciudad de México hasta el 1 de julio a mediodía; luego, un viaje a Madrid.
        Fechas.usarHistorial(listOf(CambioZona("1970-01-01T00:00:00Z", "America/Mexico_City"), CambioZona("2026-07-01T12:00:00-06:00", "Europe/Madrid")))
        // Las 3:00 UTC del 1 de julio son las 21:00 del 30 de junio en México: día lógico 30 de junio.
        assertEquals("2026-06-30", Fechas.diaLogicoDeMillis(ms("2026-07-01T03:00:00Z")))
        // Las 23:30 UTC del 1 de julio son la 1:30 del 2 de julio en Madrid: de madrugada, cuenta para el 1.
        assertEquals("2026-07-01", Fechas.diaLogicoDeMillis(ms("2026-07-01T23:30:00Z")))
        assertEquals("2026-07-02", Fechas.diaDeMillis(ms("2026-07-01T23:30:00Z")))
        assertEquals("+02:00", Fechas.isoDeMillis(ms("2026-07-01T23:30:00Z")).takeLast(6))
        // Una nota guardada con su desfase se lee a la hora en que se tomó, aunque hoy el teléfono esté en otra zona.
        assertEquals("21:00", Fechas.horaDe("2026-06-30T21:00:00-06:00"))
        // Las marcas en UTC (importaciones) se pasan a la zona de entonces.
        assertEquals("01:30", Fechas.horaDe("2026-07-01T23:30:00Z"))
        // Ida y vuelta: hora local → instante → hora local.
        val t = LocalDateTime.of(2026, 7, 10, 8, 15)
        assertEquals(t, Fechas.local(Fechas.millisDe(t)))
        assertEquals(2.0, Fechas.desfaseH(Fechas.millisDe(t)), 1e-9)
    }

    @Test fun diaConCambioDeHora() {
        Fechas.usarHistorial(listOf(CambioZona("1970-01-01T00:00:00Z", "America/Mexico_City"), CambioZona("2026-07-01T12:00:00-06:00", "Europe/Madrid")))
        // El 1 de julio empezó en UTC−6 y terminó en UTC+2: duró 16 horas.
        assertEquals(-6.0 to 2.0, Fechas.cambioDeHora("2026-07-01"))
        assertEquals(16L * 3_600_000, Fechas.finDiaLogico("2026-07-01") - Fechas.inicioDiaLogico("2026-07-01"))
        assertNull(Fechas.cambioDeHora("2026-06-20"))
        assertNull(Fechas.cambioDeHora("2026-07-05"))
        // El horario de verano de Madrid (29 de marzo de 2026) también cuenta como cambio, sin viajar.
        Fechas.usarHistorial(listOf(CambioZona("1970-01-01T00:00:00Z", "Europe/Madrid")))
        assertEquals(1.0 to 2.0, Fechas.cambioDeHora("2026-03-28"))
        assertEquals(23L * 3_600_000, Fechas.finDiaLogico("2026-03-28") - Fechas.inicioDiaLogico("2026-03-28"))
    }

    // ---- H-3-4: la fuente del clima se deduce para lo guardado antes.
    @Test fun fuenteDelClima() {
        assertEquals("archivo", Entorno.fuente(EntornoDia("2026-01-01", ts = "2026-04-01T10:00:00-06:00")))
        assertEquals("pronostico", Entorno.fuente(EntornoDia("2026-01-01", ts = "2026-01-04T10:00:00-06:00")))
        assertEquals("archivo", Entorno.fuente(EntornoDia("2026-01-01", ts = "2026-01-04T10:00:00-06:00", fuente = "archivo")))
        assertNull(Entorno.fuente(EntornoDia("2026-01-01")))
    }

    // ---- H-7-14: el ejercicio de referencia, en inglés o en español.
    @Test fun ejercicioDeReferencia() {
        assertTrue(Macrofactor.esReferencia("Incline Dumbbell Press"))
        assertTrue(Macrofactor.esReferencia("Incline Dumbbell Bench Press"))
        assertTrue(Macrofactor.esReferencia("Press inclinado con mancuernas"))
        assertFalse(Macrofactor.esReferencia("Incline Dumbbell Fly"))
        assertFalse(Macrofactor.esReferencia("Incline Barbell Bench Press"))
        assertFalse(Macrofactor.esReferencia("Dumbbell Bench Press"))
        assertTrue(Macrofactor.esReferencia("Press de banca inclinado con mancuernas"))
        assertFalse(Macrofactor.esReferencia("Curl inclinado con mancuernas en banco"))
        assertFalse(Macrofactor.esReferencia("Aperturas en banco inclinado con mancuernas"))
        fun s(ej: String, kg: Double, reps: Int, tipo: String = "") = Serie("x", "2026-10-20", ejercicio = ej, peso_kg = kg, reps = reps, tipo = tipo)
        val l = listOf(s("Incline Dumbbell Press", 30.0, 8), s("Incline Dumbbell Press", 32.0, 5), s("Incline Dumbbell Press", 40.0, 3, "Warm Up"), s("Squat", 100.0, 5))
        // Epley: 30 × (1 + 8/30) = 38 y 32 × (1 + 5/30) = 37.3; el calentamiento no cuenta.
        assertEquals(38.0, Macrofactor.fuerzaReferencia(l)!!, 1e-9)
        assertNull(Macrofactor.fuerzaReferencia(listOf(s("Squat", 100.0, 5))))
    }

    // ---- H-1-8: desde qué versión rige cada columna.
    @Test fun desdeVersion() {
        assertEquals(92, Diccionario.desde("vm_panel.csv", "cambio_horario")?.first)
        // El panel extendido toma lo de vm_panel.csv con el mismo nombre.
        assertEquals(92, Diccionario.desde("vm_panel_extendido.csv", "desfase_utc_h")?.first)
        // Por columna sola, en cualquier tabla.
        assertEquals(92, Diccionario.desde("vm_entrenos.csv", "fuerza_ref_kg")?.first)
        // Con comodín: la clave más larga gana.
        assertEquals(86, Diccionario.desde("vm_panel_extendido.csv", "app_min_whatsapp")?.first)
        assertEquals(90, Diccionario.desde("vm_panel_extendido.csv", "app_whatsapp")?.first)
        assertNull(Diccionario.desde("vm_panel.csv", "columna_que_no_cambio"))
    }

    // ---- H-7-10: el criterio y la huella quedan sellados.
    @Test fun selloYCriterio() {
        val ini = "2026-09-07"
        val base = Experimento("e", "x", "acción", "valencia", ini, 2, listOf(true, false), sellada = "2026-09-01T10:00:00-06:00", hipotesis = "sube", sentido = 1, umbral = 0.5)
        val e = Experimentos.sellar(base)
        assertEquals(64, e.huella!!.length)
        assertEquals(true, Experimentos.intacto(e))
        assertEquals(false, Experimentos.intacto(e.copy(hipotesis = "otra")))
        assertEquals(false, Experimentos.intacto(e.copy(umbral = 0.1)))
        assertNull(Experimentos.intacto(base))
        // Con la acción, 1 punto más y poca variación: se cumple; con un umbral de 2, no.
        val panel = (0 until 14).associate { i -> Fechas.sumar(ini, i.toLong()) to mapOf<String, Double?>("valencia" to (if (i < 7) 5.0 else 4.0) + (i % 3) * 0.1) }
        val r = Experimentos.resultado(e, panel, hoy = "2026-10-30")
        assertEquals(true, Experimentos.criterio(e, r))
        assertEquals(false, Experimentos.criterio(e.copy(umbral = 2.0), r))
        assertEquals(false, Experimentos.criterio(e.copy(sentido = -1), r))
        assertNull(Experimentos.criterio(e.copy(sentido = null), r))
        // A medio camino no se decide.
        assertNull(Experimentos.criterio(e, Experimentos.resultado(e, panel, hoy = "2026-09-18")))
    }
}
