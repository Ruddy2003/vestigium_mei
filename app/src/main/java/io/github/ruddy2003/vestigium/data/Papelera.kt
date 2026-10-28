package io.github.ruddy2003.vestigium.data

/**
 * Cómo se lee lo que está en la papelera (N-14). Lo privado (impulsos, recaídas, libido) solo se lista dentro de Privado;
 * en la papelera general ni siquiera se cuenta. Una sustancia que ocultaste se lista sin nombre.
 */
object Papelera {
    /** Si lo borrado es privado: solo se ve en la papelera de Privado. */
    fun esPrivado(b: Borrado): Boolean =
        b.col == "momentos" && runCatching { JSON.decodeFromString(Momento.serializer(), b.json).privado }.getOrDefault(false)

    /** Una línea que dice qué era, sin el texto de lo que escribiste en lo privado. */
    fun describir(b: Borrado, discreto: Boolean = false): String = runCatching {
        when (b.col) {
            "momentos" -> JSON.decodeFromString(Momento.serializer(), b.json).let { m ->
                if (m.privado) "Registro privado · ${Fechas.corto(m.fecha)} ${Fechas.horaDe(m.ts)}"
                else Catalogo.titulo(m, discreto) + (m.valencia?.let { " · $it/7" } ?: "") + " · ${Fechas.corto(m.fecha)} ${Fechas.horaDe(m.ts)}"
            }
            "tramos" -> JSON.decodeFromString(Tramo.serializer(), b.json).let { "${Catalogo.nombreTramo(it.tipo)} · ${it.min} min · ${Fechas.corto(it.fecha)}" }
            "consumos" -> JSON.decodeFromString(Consumo.serializer(), b.json).let {
                (if (Sustancias.visible(it)) Catalogo.nombreSustancia(it) else "Un registro oculto") + " · ${Fechas.corto(it.fecha)}"
            }
            "notas" -> JSON.decodeFromString(Nota.serializer(), b.json).let { "Nota · " + it.texto.lines().first().take(60) }
            "metas" -> JSON.decodeFromString(Meta.serializer(), b.json).let { "Meta · ${it.nombre}" }
            "tomas" -> JSON.decodeFromString(TomaSuplemento.serializer(), b.json).let {
                "${Suplementos.de(it.suplemento)?.nombre ?: it.suplemento} · ${it.cantidad} · ${Fechas.corto(it.fecha)} ${Fechas.horaDe(it.ts)}"
            }
            "gastos" -> JSON.decodeFromString(Gasto.serializer(), b.json).let { "Gasto · $" + "%,.2f".format(it.monto) + " · ${it.categoria} · ${Fechas.corto(it.fecha)}" }
            "ingresos" -> JSON.decodeFromString(Ingreso.serializer(), b.json).let { "Ingreso · $" + "%,.2f".format(it.monto) + " · ${it.categoria} · ${Fechas.corto(it.fecha)}" }
            "lugares" -> JSON.decodeFromString(Lugar.serializer(), b.json).let { "Lugar · ${it.nombre}" }
            "cartas" -> JSON.decodeFromString(Carta.serializer(), b.json).let { "Carta escrita el ${Fechas.corto(it.escrita)}" }
            "ascensos" -> JSON.decodeFromString(Ascenso.serializer(), b.json).let { "Sesión de escalada · ${Fechas.corto(it.fecha)}" }
            "obras" -> JSON.decodeFromString(Obra.serializer(), b.json).let { "Obra · ${it.titulo}" }
            else -> "Un registro"
        }
    }.getOrDefault("Un registro")

    /** Cuántos días le quedan en la papelera (se vacía a los 7). */
    fun diasQuedan(b: Borrado, hoy: String = Fechas.diaLogico()): Int =
        (7 - Fechas.diasEntre(Fechas.diaLogicoDeMillis(Fechas.millis(b.borrado).takeIf { it > 0 } ?: System.currentTimeMillis()), hoy)).toInt().coerceIn(0, 7)
}
