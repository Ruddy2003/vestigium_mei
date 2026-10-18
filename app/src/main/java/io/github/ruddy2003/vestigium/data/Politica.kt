package io.github.ruddy2003.vestigium.data

/**
 * La brújula política: 25 afirmaciones propias de la app, una vez al año. Cinco ejes; en cada uno la mitad de las
 * afirmaciones apunta a un lado y la mitad al otro, para que estar «de acuerdo con todo» no empuje hacia ningún lado.
 * No hay respuestas correctas: sirve para ver cómo cambian tus posiciones con los años.
 */
object Politica {
    /** Un eje: el polo negativo ([menos], −100) y el positivo ([mas], +100). */
    class Eje(val id: String, val nombre: String, val menos: String, val mas: String)

    /** Una afirmación: [signo] +1 si estar de acuerdo acerca al polo positivo del eje, −1 si al negativo. */
    class Afirmacion(val id: String, val eje: String, val signo: Int, val texto: String)

    val EJES = listOf(
        Eje("economia", "Economía", "igualdad y Estado", "mercado y empresa"),
        Eje("autoridad", "Autoridad", "libertad individual", "orden y autoridad"),
        Eje("valores", "Valores", "cambio", "tradición"),
        Eje("mundo", "Mundo", "apertura", "soberanía"),
        Eje("ambiente", "Ambiente", "proteger el ambiente", "crecimiento primero"),
    )

    val AFIRMACIONES = listOf(
        Afirmacion("e1", "economia", +1, "Las empresas privadas suelen dar mejores servicios que el gobierno."),
        Afirmacion("e2", "economia", -1, "El gobierno debería garantizar salud y educación gratuitas para todos, aunque haya que pagar más impuestos."),
        Afirmacion("e3", "economia", +1, "Bajar los impuestos ayuda más a la economía que aumentar el gasto público."),
        Afirmacion("e4", "economia", -1, "Las grandes fortunas deberían pagar impuestos bastante más altos."),
        Afirmacion("e5", "economia", -1, "Hace falta regular más a las grandes empresas para proteger a trabajadores y consumidores."),
        Afirmacion("a1", "autoridad", +1, "En momentos difíciles es mejor un gobierno fuerte que decida rápido que largos debates."),
        Afirmacion("a2", "autoridad", -1, "Cada adulto debería poder decidir sobre su propia vida, aunque a otros no les guste."),
        Afirmacion("a3", "autoridad", +1, "Para garantizar la seguridad, es aceptable que el Estado vigile más las comunicaciones."),
        Afirmacion("a4", "autoridad", -1, "Protestar, incluso con desobediencia pacífica, es una forma legítima de cambiar las cosas."),
        Afirmacion("a5", "autoridad", +1, "Las penas más duras son la mejor forma de reducir el delito."),
        Afirmacion("c1", "valores", +1, "Las tradiciones merecen conservarse aunque la sociedad cambie."),
        Afirmacion("c2", "valores", -1, "Las parejas del mismo sexo deberían tener los mismos derechos para casarse y adoptar."),
        Afirmacion("c3", "valores", +1, "La religión debería tener un lugar importante en la vida pública."),
        Afirmacion("c4", "valores", -1, "Está bien que los papeles de hombres y mujeres sean cada vez más flexibles."),
        Afirmacion("c5", "valores", -1, "Las escuelas deberían enseñar educación sexual integral."),
        Afirmacion("m1", "mundo", +1, "Mi país debería ocuparse primero de su gente y después de la cooperación internacional."),
        Afirmacion("m2", "mundo", -1, "La inmigración enriquece a la sociedad que la recibe."),
        Afirmacion("m3", "mundo", +1, "Conviene proteger la industria nacional, aunque lo importado sea más barato."),
        Afirmacion("m4", "mundo", -1, "Los organismos internacionales deberían tener más poder para resolver problemas globales."),
        Afirmacion("m5", "mundo", +1, "Las fronteras deberían controlarse con mucha más firmeza."),
        Afirmacion("v1", "ambiente", -1, "Proteger el ambiente debería ser prioridad aunque frene el crecimiento económico."),
        Afirmacion("v2", "ambiente", +1, "Las preocupaciones ambientales no deberían frenar la creación de empleos."),
        Afirmacion("v3", "ambiente", -1, "Habría que cobrar más a quien contamina, aunque suban algunos precios."),
        Afirmacion("v4", "ambiente", +1, "La tecnología resolverá los problemas ambientales sin necesidad de grandes sacrificios."),
        Afirmacion("v5", "ambiente", -1, "Deberíamos usar menos el coche y el avión, aunque sea menos cómodo."),
    )

    val RESPUESTAS = listOf(1 to "Muy en desacuerdo", 2 to "En desacuerdo", 3 to "Ni de acuerdo ni en desacuerdo", 4 to "De acuerdo", 5 to "Muy de acuerdo")

    /** La posición en cada eje, de −100 a 100. Un eje sin respuestas no aparece. */
    fun ejes(respuestas: Map<String, Int>): Map<String, Double> {
        val out = LinkedHashMap<String, Double>()
        for (e in EJES) {
            val l = AFIRMACIONES.filter { it.eje == e.id }.mapNotNull { a -> respuestas[a.id]?.let { (it - 3) * a.signo } }
            if (l.isNotEmpty()) out[e.id] = l.sum() * 100.0 / (2 * l.size)
        }
        return out
    }

    /** El orden de las afirmaciones en un test: mezclado, pero el mismo durante todo el año. */
    fun orden(anio: Int): List<Afirmacion> = AFIRMACIONES.shuffled(java.util.Random(anio * 7919L))

    fun ultimo(): TestPolitico? = Repo.config.politica.maxByOrNull { it.fecha }

    /** Toca hacerlo: nunca lo has hecho y ya llevas dos semanas usando la app, o pasó un año desde el último. */
    fun toca(hoy: String = Fechas.diaLogico()): Boolean {
        val u = ultimo() ?: return Repo.dias.size >= 14
        return java.time.temporal.ChronoUnit.DAYS.between(Fechas.parse(u.fecha), Fechas.parse(hoy)) >= 365
    }

    /** Cómo se describe una posición, sin calificarla. */
    fun lado(e: Eje, v: Double): String = when {
        v <= -60 -> "muy hacia ${e.menos}"
        v <= -20 -> "hacia ${e.menos}"
        v < 20 -> "al centro"
        v < 60 -> "hacia ${e.mas}"
        else -> "muy hacia ${e.mas}"
    }
}
