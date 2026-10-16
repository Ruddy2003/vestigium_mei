package io.github.ruddy2003.vestigium.data

import java.text.Normalizer

/** Etiquetas dentro del texto libre: @personas y #temas, con soporte de ñ y acentos. */
object Texto {
    private val RX_P = Regex("@([\\p{L}\\p{N}_]+)")
    private val RX_T = Regex("#([\\p{L}\\p{N}_]+)")

    fun personas(t: String?): List<String> = RX_P.findAll(t ?: "").map { it.groupValues[1].lowercase() }.toList()
    fun temas(t: String?): List<String> = RX_T.findAll(t ?: "").map { it.groupValues[1].lowercase() }.toList()
    fun palabras(t: String?): Int = (t ?: "").trim().split(Regex("\\s+")).count { it.isNotBlank() }

    fun canon(t: String, eq: Map<String, String>): String = eq[t] ?: t

    fun slug(s: String): String {
        val n = Normalizer.normalize(s.lowercase(), Normalizer.Form.NFD).replace(Regex("\\p{Mn}+"), "")
        return n.replace(Regex("[^a-z0-9]+"), "_").trim('_')
    }

    /** Para etiquetas: minúsculas, sin símbolo inicial, espacios a guion bajo, conserva ñ y acentos. */
    fun etiqueta(s: String): String = s.trim().lowercase().removePrefix("@").removePrefix("#").replace(Regex("\\s+"), "_")

    fun levenshtein(a: String, b: String): Int {
        if (kotlin.math.abs(a.length - b.length) > 2) return 9
        val dp = Array(a.length + 1) { IntArray(b.length + 1) }
        for (i in 0..a.length) dp[i][0] = i
        for (j in 0..b.length) dp[0][j] = j
        for (i in 1..a.length) for (j in 1..b.length) {
            dp[i][j] = minOf(dp[i - 1][j] + 1, dp[i][j - 1] + 1, dp[i - 1][j - 1] + if (a[i - 1] == b[j - 1]) 0 else 1)
        }
        return dp[a.length][b.length]
    }

    fun parecidos(a: String, b: String): Boolean =
        a != b && (a.replace("_", "") == b.replace("_", "") || (a.length >= 4 && b.length >= 4 && levenshtein(a, b) <= 2))

    /** Reemplaza una etiqueta completa (no dentro de otra palabra). */
    fun reemplazar(texto: String, simbolo: Char, de: String, a: String): String =
        Regex(Regex.escape("$simbolo$de") + "(?![\\p{L}\\p{N}_])", RegexOption.IGNORE_CASE).replace(texto, "$simbolo$a")
}
