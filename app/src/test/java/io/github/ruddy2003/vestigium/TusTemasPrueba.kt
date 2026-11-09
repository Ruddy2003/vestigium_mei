package io.github.ruddy2003.vestigium

import io.github.ruddy2003.vestigium.data.Repo
import io.github.ruddy2003.vestigium.data.Texto
import io.github.ruddy2003.vestigium.data.TusTemas
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** «Tus temas» (9 nov 2026): los pares parecidos salen igual que antes, pero rápido; y renombrar con «$» no truena. */
class TusTemasPrueba {
    private fun sinAcentos(s: String) = java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
    private fun raiz(s: String): String {
        val x = sinAcentos(s).replace("_", "")
        return when {
            x.length > 4 && x.endsWith("es") && !x.endsWith("ies") -> x.dropLast(2)
            x.length > 3 && x.endsWith("s") && !x.endsWith("is") -> x.dropLast(1)
            else -> x
        }
    }

    /** El método de antes, tal cual (sin uniones ni «son distintos»). */
    private fun viejo(l: List<TusTemas.Tema>): List<Pair<String, String>> {
        val dias = l.associate { it.nombre to it.dias }
        val nombres = l.map { it.nombre }
        val out = ArrayList<Pair<String, String>>()
        for (i in nombres.indices) for (j in i + 1 until nombres.size) {
            val a = nombres[i]; val b = nombres[j]
            val parecido = Texto.parecidos(a, b) || raiz(a) == raiz(b) || sinAcentos(a) == sinAcentos(b)
            if (parecido) out += if ((dias[a] ?: 0) >= (dias[b] ?: 0)) a to b else b to a
        }
        return out.take(30)
    }

    private fun t(n: String, d: Int) = TusTemas.Tema(n, d, "2026-01-01", "2026-02-01")

    @Test fun mismosParesQueAntes() {
        val l = listOf(t("tesis", 9), t("tésis", 3), t("examenes", 5), t("examen", 4), t("amigos", 4), t("amigo", 2),
            t("viaje_largo", 2), t("viajelargo", 1), t("trabajo", 8), t("trabajos", 1), t("familia", 6), t("famila", 1), t("dinero", 3))
        assertEquals(viejo(l), TusTemas.parecidos(l))
        assertTrue(TusTemas.parecidos(l).contains("tesis" to "tésis"))
    }

    @Test fun dosMilEtiquetasEnPocoTiempo() {
        val r = java.util.Random(7)
        val letras = "abcdefghijklmnopqrstuvwxyz"
        val l = (0 until 2000).map { i -> t((0 until 6 + r.nextInt(8)).map { letras[r.nextInt(26)] }.joinToString("") + i, 2000 - i) }
        TusTemas.parecidos(l) // calentar
        val t0 = System.nanoTime()
        val pares = TusTemas.parecidos(l)
        val ms = (System.nanoTime() - t0) / 1_000_000
        assertTrue("tardó $ms ms", ms < 2000)
        assertEquals(viejo(l.take(300)), TusTemas.parecidos(l.take(300)))
        assertTrue(pares.size <= 30)
        assertTrue(Repo.config.temas_distintos.isEmpty())
    }

    @Test fun renombrarConSignoDePesos() {
        assertEquals("fui a #ahorro\$ hoy", Texto.reemplazar("fui a #ahorro hoy", '#', "ahorro", "ahorro\$"))
        assertEquals("#a\\b", Texto.reemplazar("#x", '#', "x", "a\\b"))
    }
}
