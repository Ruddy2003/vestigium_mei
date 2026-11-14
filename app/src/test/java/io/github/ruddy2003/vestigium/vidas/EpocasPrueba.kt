package io.github.ruddy2003.vestigium.vidas

import io.github.ruddy2003.vestigium.data.OtrasVidas
import io.github.ruddy2003.vestigium.vidas.Epocas.Acontecimiento
import io.github.ruddy2003.vestigium.vidas.Epocas.Articulo
import io.github.ruddy2003.vestigium.vidas.Epocas.Motivo
import io.github.ruddy2003.vestigium.vidas.anios.Calendarios
import io.github.ruddy2003.vestigium.vidas.piezas.TodasLasPiezas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * «En la época de X ›» (tandas H2 y H3): qué acontecimiento lleva a qué cuento. Los acontecimientos de aquí están
 * armados a mano para probar cada parte de la regla; los de Wikipedia de verdad, con sus respuestas de Wikidata, están
 * en `historia/EnlaceDeEpocaPrueba`.
 */
class EpocasPrueba {
    private fun vida(id: String) = OtrasVidas.porId(id)!!
    private fun todas() = OtrasVidas.BASE + OtrasVidas.DE_TEMAS
    private fun conCuento(tema: String) = OtrasVidas.visibles(tema).filter { Calendarios.de(it.id) != null && TodasLasPiezas.de(it.id) != null }
    private val deTodos get() = conCuento("heleno") // las doce de todos y Demeas

    private fun art(titulo: String, q: String? = null, vararg paises: String) = Articulo(titulo, q, paises.toList())
    private fun sinWikidata(titulo: String) = Articulo(titulo, null, null)
    private fun quien(a: Acontecimiento, vidas: List<OtrasVidas.Vida> = deTodos, tema: String? = "heleno") = Epocas.vidaDelAcontecimiento(a, vidas, tema)

    // ---------------------------------------------------------------- la época

    @Test fun laEpocaEsElAnioMasMenosTreinta() {
        assertEquals(-461..-401, Epocas.de(vida("remero")))
        assertEquals(1875..1935, Epocas.de(vida("hacienda")))
        assertEquals(80..140, Epocas.de(vida("roma")))
        // Lo mundial cuenta desde que nació hasta el final de su temporada 5: Rosa (1960, 26 años), de 1934 a 1964.
        assertEquals(1934..1964, Epocas.vida(vida("cdmx1960")))
    }

    @Test fun lasVidasInventadasYLasDeHoyNoTienenEpoca() {
        todas().filter { it.ficticia }.forEach { assertNull(it.id, Epocas.de(it)); assertNull(it.id, Epocas.vida(it)) }
        val deHoy = todas().filter { !it.ficticia && Epocas.de(it) == null }.map { it.id }.toSet()
        assertEquals(setOf("mundo2025", "mexico2025", "restauradora"), deHoy)
        // Ni con un acontecimiento de su lugar ni con uno mundial.
        val enMexico2015 = Acontecimiento(2015, "En México se inaugura algo.", listOf(art("México", "Q96", "Q96")))
        assertNull(quien(enMexico2015, listOf(vida("mexico2025"), vida("mundo2025"))))
        val covid = Acontecimiento(2020, "Empieza la pandemia de COVID-19.", listOf(art("Pandemia de COVID-19", "Q81068910")))
        assertNull(quien(covid, todas()))
        // Brenna cuenta los años «de los Caminos»: el 312 no es nuestro 312.
        assertNull(quien(Acontecimiento(312, "Un concilio en Roma.", listOf(art("Roma", "Q220", "Q38"))), listOf(vida("guia_caminos"))))
    }

    @Test fun cadaVidaConEpocaYCuentoTieneSuLugar() {
        val conEpoca = todas().filter { Epocas.de(it) != null && TodasLasPiezas.de(it.id) != null }
        assertTrue(conEpoca.size > 25)
        conEpoca.forEach { v -> val l = LUGARES[v.id]; assertNotNull(v.id, l); assertTrue(v.id, l!!.palabras.isNotEmpty()) }
        // Y ninguna sin época tiene lugar (sería un lugar que no se usa).
        LUGARES.keys.forEach { id -> assertNotNull(id, Epocas.de(vida(id))) }
        LUGARES.values.flatMap { it.wikidata }.forEach { assertTrue(it, Regex("Q[1-9][0-9]*").matches(it)) }
    }

    // ---------------------------------------------------------------- (a) Wikidata

    @Test fun conWikidataGanaElLugarDeLaMayoria() {
        // Matan a Pancho Villa (1923): tres artículos de México y uno de Estados Unidos. Con H2 iba a Lou.
        val villa = Acontecimiento(1923, "En Parral, en el estado de Chihuahua (México), muere asesinado Pancho Villa.", listOf(
            art("Chihuahua", "Q655", "Q96"), art("Pancho Villa", "Q192278", "Q96"), art("William Randolph Hearst", "Q294931", "Q30"), art("Plutarco Elías Calles", "Q315066", "Q96")))
        assertEquals("hacienda" to Motivo.PAIS, quien(villa)?.let { it.first.id to it.second })
        assertEquals("hacienda", quien(villa, conCuento("deco"), "deco")?.first?.id)
    }

    @Test fun unLugarEnMinoriaSoloCuentaSiElTextoLoNombra() {
        // Cuba, 1896: cinco artículos, uno de ellos un río de la frontera de México. No es de México.
        val cuba = Acontecimiento(1896, "En la provincia de Pinar del Río (Cuba), las fuerzas mambisas vencen.", listOf(
            art("Provincia de Pinar del Río", "Q115497", "Q241"), art("Guerra de Independencia cubana", "Q1514908", "Q241"),
            art("Mambises", "Q177996", "Q241"), art("Antonio Maceo", "Q554264", "Q241"), art("Río Hondo", "Q1494095", "Q242", "Q96")))
        assertNull(quien(cuba))
        // Tenochtitlan, 1523: uno de México y dos de España, pero el texto nombra Tenochtitlán (con acento).
        val alvarado = Acontecimiento(1523, "De Tenochtitlán sale el conquistador español Pedro de Alvarado.", listOf(
            art("México-Tenochtitlan", "Q13695", "Q2608489", "Q96"), art("Pedro de Alvarado", "Q210900", "Q29"), art("Hernán Cortés", "Q7326", "Q29")))
        assertEquals("tenochtitlan" to Motivo.PAIS, quien(alvarado)?.let { it.first.id to it.second })
        // Un gentilicio no rescata: «francés» habla de una persona, no del lugar.
        val baudin = Acontecimiento(1838, "Al puerto de Veracruz llega el contraalmirante francés Charles Baudin.", listOf(
            art("Veracruz", null, "Q96"), art("Charles Baudin", null, "Q142"), art("Guerra de los Pasteles", null, "Q96")))
        assertNull(quien(baudin, conCuento("napoleon"), "napoleon"))
    }

    @Test fun unArticuloDeMuchosPaisesNoDiceNada() {
        // El Mar del Norte tiene seis países: no dice dónde pasó. Sin otros artículos, cuentan las palabras.
        val mar = Acontecimiento(1825, "En la costa alemana del mar del Norte mueren ahogadas 800 personas.", listOf(
            art("Mar del Norte", "Q1693", "Q20", "Q35", "Q183", "Q55", "Q145", "Q142"), art("Marejada ciclónica", "Q121742")))
        assertNull(quien(mar, conCuento("napoleon"), "napoleon"))
        val enManchester = Acontecimiento(1840, "En Manchester se inaugura una fábrica.", listOf(art("Mar del Norte", "Q1693", "Q20", "Q35", "Q183", "Q55")))
        assertEquals("manchester" to Motivo.PALABRAS, quien(enManchester)?.let { it.first.id to it.second })
    }

    @Test fun fueraDeSuEpocaNoAunqueSeaDeSuLugar() {
        // El Grito de Dolores (1810) es de México, pero José es de 1875 a 1935 y Citlali de 1470 a 1530. Con H2 iba a Mary.
        val grito = Acontecimiento(1810, "En el pueblo de Dolores (México), el cura Miguel Hidalgo convoca a la lucha.", listOf(
            art("Dolores Hidalgo", "Q17487868", "Q96"), art("México", "Q96", "Q96")))
        assertNull(quien(grito))
        assertNull(quien(Acontecimiento(1994, "En la Ciudad de México se inaugura la línea 8 del Metro.", listOf(art("Ciudad de México", "Q1489", "Q96")))))
    }

    // ---------------------------------------------------------------- (b) sin Wikidata, las palabras

    @Test fun sinWikidataCuentanLasPalabras() {
        val monterrey = Acontecimiento(1957, "En Monterrey, México, se inaugura un teatro.", listOf(sinWikidata("Monterrey")))
        assertEquals("cdmx1960" to Motivo.PALABRAS, quien(monterrey)?.let { it.first.id to it.second })
        val guadalajara = Acontecimiento(1962, "En Guadalajara se inaugura un mercado.", emptyList())
        assertEquals("cdmx1960", quien(guadalajara)?.first?.id)
        // Las palabras van enteras: «Romania» no nombra «Roma».
        assertNull(quien(Acontecimiento(120, "Llega a Romania un barco.", emptyList())))
    }

    @Test fun nuevoMexicoNoEsMexico() {
        val sinWd = Acontecimiento(1960, "En Nuevo México se prueba un cohete.", emptyList())
        assertNull(quien(sinWd))
        val conWd = Acontecimiento(1960, "En Nuevo México se prueba un cohete.", listOf(art("Nuevo México", "Q1522", "Q30")))
        assertNull(quien(conWd))
        assertNull(quien(Acontecimiento(1960, "Un huracán cruza el Golfo de México.", emptyList())))
        // Si además nombra México, sí.
        assertEquals("cdmx1960", quien(Acontecimiento(1960, "Un huracán cruza el Golfo de México y llega a Veracruz (México).", emptyList()))?.first?.id)
    }

    @Test fun conWikidataLasPalabrasSolasNoBastan() {
        // Wikidata dice que es de Estados Unidos: que el texto diga «mexicano» no lo hace de México.
        val a = Acontecimiento(1960, "Un actor mexicano estrena una película en Hollywood.", listOf(art("Hollywood", "Q34006", "Q30")))
        assertNull(quien(a))
    }

    // ---------------------------------------------------------------- (c) lo mundial

    @Test fun loMundialDentroDeSusFechasYDeSuVida() {
        val guadalcanal = Acontecimiento(1942, "Cerca de Guadalcanal, en el marco de la Segunda Guerra Mundial, se hunden dos buques.",
            listOf(art("Isla de Guadalcanal", "Q192767", "Q685")))
        assertEquals("cdmx1960" to Motivo.MUNDIAL, quien(guadalcanal)?.let { it.first.id to it.second })
        // Fuera de las fechas de la guerra no es mundial, aunque el texto la nombre.
        val leopoldo = Acontecimiento(1950, "En Bélgica regresa el rey, tras su papel en la Segunda Guerra Mundial.", listOf(art("Leopoldo III", "Q12973", "Q31")))
        assertNull(quien(leopoldo))
        // El Apolo 11 por su artículo (y con el espacio que no se parte de Wikipedia): Liudmila nació en 1941.
        val apolo = Acontecimiento(1969, "En la Luna se posa el módulo de la misión Apolo 11.", listOf(art("Apolo 11", "Q43653", "Q30")))
        assertTrue(Epocas.esMundial(apolo))
        assertEquals("obrera_moscu" to Motivo.MUNDIAL, quien(apolo, conCuento("urss"), "urss")?.let { it.first.id to it.second })
        assertNull("Rosa ya no está en su temporada 5", quien(apolo))
        // La Guerra Fría no es de la lista.
        assertFalse(Epocas.esMundial(Acontecimiento(1954, "En el marco de la Guerra Fría, un espía huye.", emptyList())))
    }

    @Test fun loDeSuLugarGanaALoMundial() {
        // 1917, la Gran Guerra: Estados Unidos rompe relaciones con Alemania. En Art déco va a Lou por su lugar.
        val a = Acontecimiento(1917, "En el marco de la Gran Guerra, los Estados Unidos rompen relaciones con Alemania.",
            listOf(art("Primera Guerra Mundial", "Q361", "Q148"), art("Estados Unidos", "Q30", "Q30")))
        assertEquals("pianista" to Motivo.PAIS, quien(a, conCuento("deco"), "deco")?.let { it.first.id to it.second })
    }

    @Test fun antesDeCristoYElDesempatePorElTema() {
        val peste = Acontecimiento(-430, "En Atenas estalla la peste.", listOf(art("Atenas", "Q1524", "Q41")))
        assertEquals("remero", quien(peste)?.first?.id)
        // Cayo (Romano) y Lucio (de todos) son del año 110, los dos del Imperio romano: gana el del tema.
        val dacia = Acontecimiento(105, "El Imperio romano entra en guerra.", listOf(art("Imperio romano", "Q2277", "Q2277")))
        assertEquals("legionario", quien(dacia, conCuento("romano"), "romano")?.first?.id)
    }

    @Test fun lasPalabrasNoCuentanAcentosNiEspaciosDeWikipedia() {
        assertTrue(Epocas.nombra("De Tenochtitlán sale", "Tenochtitlan"))
        assertTrue(Epocas.nombra("la misión Apolo 11.", "Apolo 11"))
        assertFalse(Epocas.nombra("Llega a Romania", "Roma"))
        assertFalse(Epocas.nombra("los romanos", "romano"))
    }
}
