package io.github.ruddy2003.vestigium.widget

import io.github.ruddy2003.vestigium.R

/** Partes de cada widget según su papel de color (generado de los layouts): así se pintan con el tema elegido. */
internal class Partes(val raiz: IntArray, val oro: IntArray, val texto: IntArray, val tenue: IntArray, val boton: IntArray, val ambar: IntArray, val linea: IntArray)

internal object Piel {
    val ANIMO = Partes(intArrayOf(R.id.raiz_animo), intArrayOf(R.id.titulo_animo), intArrayOf(R.id.a1, R.id.a2, R.id.a3, R.id.a4, R.id.a5, R.id.a6, R.id.a7), intArrayOf(R.id.sub_animo, R.id.pregunta_animo), intArrayOf(R.id.a1, R.id.a2, R.id.a3, R.id.a4, R.id.a5, R.id.a6, R.id.a7), intArrayOf(), intArrayOf(R.id.p_animo_1))
    val GASTO = Partes(intArrayOf(R.id.raiz_gasto), intArrayOf(R.id.titulo_gasto, R.id.g_otro), intArrayOf(R.id.g1, R.id.g2, R.id.g3, R.id.g4, R.id.g5, R.id.g6), intArrayOf(R.id.sub_gasto, R.id.pregunta_gasto), intArrayOf(R.id.g1, R.id.g2, R.id.g3, R.id.g4, R.id.g5, R.id.g6), intArrayOf(R.id.g_otro), intArrayOf(R.id.p_gasto_1))
    val HABITOS = Partes(intArrayOf(R.id.raiz_habitos), intArrayOf(R.id.titulo_habitos), intArrayOf(R.id.hb1, R.id.hb2, R.id.hb3, R.id.hb4, R.id.hb5, R.id.hb6), intArrayOf(R.id.sub_habitos, R.id.pregunta_habitos), intArrayOf(R.id.hb1, R.id.hb2, R.id.hb3, R.id.hb4, R.id.hb5, R.id.hb6), intArrayOf(), intArrayOf(R.id.p_habitos_1))
    val HOY = Partes(intArrayOf(R.id.raiz_hoy), intArrayOf(R.id.hoy_fecha), intArrayOf(R.id.hoy_lineas), intArrayOf(R.id.hoy_sub), intArrayOf(), intArrayOf(), intArrayOf(R.id.p_hoy_1))
    val MES = Partes(intArrayOf(R.id.raiz_mes), intArrayOf(R.id.titulo_mes), intArrayOf(), intArrayOf(R.id.sub_mes), intArrayOf(), intArrayOf(), intArrayOf(R.id.p_mes_1))
    val MOMENTO = Partes(intArrayOf(R.id.raiz_momento), intArrayOf(R.id.titulo_momento, R.id.m_mas, R.id.m_diario), intArrayOf(R.id.m1, R.id.m2, R.id.m3, R.id.m4, R.id.m5, R.id.m6, R.id.m7, R.id.m8), intArrayOf(R.id.sub_momento, R.id.pregunta_momento), intArrayOf(R.id.m1, R.id.m2, R.id.m3, R.id.m4, R.id.m5, R.id.m6, R.id.m7, R.id.m8), intArrayOf(R.id.m_mas, R.id.m_diario), intArrayOf(R.id.p_momento_1))
    val SUENO = Partes(intArrayOf(R.id.raiz_sueno), intArrayOf(R.id.titulo_sueno, R.id.s_sueno), intArrayOf(R.id.s1, R.id.s2, R.id.s3, R.id.s4, R.id.s5), intArrayOf(R.id.sub_sueno, R.id.pregunta_sueno), intArrayOf(R.id.s1, R.id.s2, R.id.s3, R.id.s4, R.id.s5), intArrayOf(R.id.s_sueno), intArrayOf(R.id.p_sueno_1))
}

internal object PielNuevos {
    val ANILLOS = Partes(intArrayOf(R.id.raiz_anillos), intArrayOf(R.id.titulo_anillos), intArrayOf(), intArrayOf(R.id.sub_anillos), intArrayOf(), intArrayOf(), intArrayOf(R.id.p_anillos_1))
    val FOTO = Partes(intArrayOf(R.id.raiz_foto), intArrayOf(R.id.titulo_foto), intArrayOf(), intArrayOf(R.id.sub_foto, R.id.pie_foto), intArrayOf(), intArrayOf(), intArrayOf(R.id.p_foto_1))
    val CIFRA = Partes(intArrayOf(R.id.raiz_cifra), intArrayOf(R.id.titulo_cifra), intArrayOf(R.id.valor_cifra), intArrayOf(R.id.sub_cifra, R.id.etiqueta_cifra), intArrayOf(), intArrayOf(), intArrayOf(R.id.p_cifra_1))
}
