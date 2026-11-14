package io.github.ruddy2003.vestigium.ui

import io.github.ruddy2003.vestigium.data.Fechas
import java.time.LocalDate

/**
 * Frente exterior (lote 10; `docs/diseno/temas/frente.md`): lo que el tema calcula para sus textos. Cada día es una
 * operación con su nombre en clave y su ventana de transmisión; los dos salen solo de la fecha, así que el mismo día
 * dice siempre lo mismo y no se guarda nada.
 */
object Frente {
    /**
     * Las palabras de los nombres en clave: animales, aves, piedras y herramientas, en masculino o femenino, que se dicen
     * con «OPERACIÓN» delante. Son 120 y el paso entre un día y el siguiente es primo con 120, así que en cuatro meses no
     * se repite ninguna y dos días seguidos nunca comparten nombre.
     */
    private val CLAVES = listOf(
        "GARZA", "CERNÍCALO", "LINCE", "PIZARRA", "ANCLA", "ALONDRA", "BRÚJULA", "HALCÓN", "MARTA", "CUARZO",
        "VENCEJO", "TEJÓN", "CANTERA", "GRULLA", "ÁMBAR", "NUTRIA", "PALOMA", "YUNQUE", "PIGARGO", "LIEBRE",
        "OBSIDIANA", "CORNEJA", "TALADRO", "MILANO", "ERIZO", "BASALTO", "GAVIOTA", "TENAZA", "ARMIÑO", "FARO",
        "CIGÜEÑA", "ZORRO", "CALIZA", "VERDERÓN", "LLAVE", "CUERVO", "MARMOTA", "PEDERNAL", "GOLONDRINA", "CINCEL",
        "BÚHO", "CORZO", "GRANITO", "ESTORNINO", "POLEA", "LECHUZA", "JABALÍ", "ÓNICE", "AVOCETA", "COMPÁS",
        "ÁGUILA", "VISÓN", "ARENISCA", "PETREL", "BISAGRA", "MIRLO", "GAMO", "JASPE", "ALCOTÁN", "PLOMADA",
        "CÓNDOR", "TOPO", "PÓRFIDO", "ABUBILLA", "SEXTANTE", "AZOR", "ARDILLA", "SÍLEX", "CHARRÁN", "MANIVELA",
        "GAVILÁN", "CIERVO", "ESMERIL", "COLIBRÍ", "TORNILLO", "QUEBRANTAHUESOS", "COMADREJA", "AZUFRE", "PELÍCANO", "PALANCA",
        "ALBATROS", "BISONTE", "COBALTO", "CHOTACABRAS", "ESCUADRA", "FLAMENCO", "MANGOSTA", "GRAFITO", "TORCAZ", "ROLDANA",
        "AVUTARDA", "LOBO", "MALAQUITA", "CORMORÁN", "TROMPO", "PERDIZ", "COYOTE", "MICA", "ZORZAL", "ENGRANE",
        "GORRIÓN", "CASTOR", "TURQUESA", "PINZÓN", "CERROJO", "CODORNIZ", "OCELOTE", "ESTAÑO", "MARTINETE", "LINTERNA",
        "PARDELA", "PUMA", "AZABACHE", "OROPÉNDOLA", "ANTENA", "CHOVA", "TAPIR", "ÁGATA", "TARABILLA", "PÉNDULO",
    )

    /** El nombre en clave del día [d]: «GARZA», sin «OPERACIÓN». */
    fun clave(d: LocalDate): String = CLAVES[Math.floorMod(d.toEpochDay() * 37, CLAVES.size.toLong()).toInt()]

    /**
     * La ventana de transmisión del día [d]: la hora de la noche (de 20:00 a 23:50, en pasos de diez minutos) en que la
     * célula abre el canal. Cambia cada día y no depende de nada tuyo.
     */
    fun ventana(d: LocalDate): String {
        val m = Math.floorMod(d.toEpochDay() * 53 + 11, 24L).toInt() * 10
        return "%02d:%02d".format(20 + m / 60, m % 60)
    }

    /** «OPERACIÓN GARZA · 14.11.2026», o la fecha tal cual si no se puede leer. */
    fun fecha(iso: String): String = runCatching {
        val d = Fechas.parse(iso)
        "OPERACIÓN ${clave(d)} · %02d.%02d.%d".format(d.dayOfMonth, d.monthValue, d.year)
    }.getOrDefault(iso)

    /** El número como lo da el contador mecánico de una consola: siempre cuatro cifras («0017»). */
    fun contador(n: Int): String = if (n in 0..9999) "%04d".format(n) else n.toString()
}
