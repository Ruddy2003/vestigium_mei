package io.github.ruddy2003.vestigium.ui

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.lerp
import java.util.Random
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Los trazos que se repiten en las figuras de Frente exterior (`docs/diseno/temas/frente.md`): sus colores de dibujo, el
 * emblema de la red, la retícula y el barrido de las pantallas redondas, el piloto de consola y la ruta de salto de los
 * mapas estelares. Los usan los trofeos, el hall, la sala de trofeos, los mapas y las pantallas del tema.
 */
object TrazosFrente {
    /** La baquelita de las palancas y las perillas: un negro cálido. */
    val BAQUELITA = Color(0xFF2A2622)
    /** El cromo gastado de los biseles. */
    val CROMO = Color(0xFFB9B6AE)
    /** El trazo verde pálido de la pantalla de radar… */
    val RADAR = Color(0xFF9FD8A8)
    /** …sobre su pantalla, casi negra. */
    val PANTALLA = Color(0xFF0F1A16)
    val PILOTO_ROJO = Color(0xFFD9372B)
    val PILOTO_AMBAR = Color(0xFFF2A33A)
    /** El mármol del Directorio y su bronce: solo para lo que es del régimen. */
    val MARMOL = Color(0xFFF3F1EC)
    val BRONCE = Color(0xFF9A7442)
    /** La consola blanca, el acero pizarra verdoso de los paneles y el verde casi negro de las consolas oscuras. */
    val HUESO = Color(0xFFE6E5DD)
    val PIZARRA = Color(0xFF3A4A44)
    val CONSOLA_OSCURA = Color(0xFF1E2A26)
    /** La piedra de las ruinas que esconden la base (a la luz y en sombra) y las raíces que cuelgan de ella. */
    val PIEDRA = Color(0xFF8C8F84)
    val PIEDRA_SOMBRA = Color(0xFF3B4440)
    val RAIZ = Color(0xFF4A4636)
    /** El vidrio grabado: el trazo iluminado por el canto y el vidrio mismo (en penumbra y a la luz). */
    val VIDRIO_TRAZO = Color(0xFF8FF0D2)
    val VIDRIO = Color(0xFF123A33)
    val VIDRIO_LUZ = Color(0xFF2F6F62)
    /** El amarillo de las franjas de peligro del suelo y el óxido del sillón de la cabina. */
    val PELIGRO = Color(0xFFD9A62B)
    val OXIDO = Color(0xFFA44A1C)
    /** El cobre viejo de las tuberías del techo y el musgo que crece en las juntas húmedas de la piedra. */
    val COBRE = Color(0xFF8A6A4C)
    val MUSGO = Color(0xFF5E6B45)

    /**
     * El emblema de la red: la chispa de tres trazos dentro del círculo roto. El hueco del círculo queda arriba a la
     * derecha y por él sale una pizca de la chispa: es la señal que se escapa del canal.
     */
    fun DrawScope.emblemaRed(c: Offset, r: Float, color: Color, grosor: Float) {
        // El aro, roto arriba a la derecha (el hueco mira a −50°).
        drawArc(color, -20f, 300f, false, Offset(c.x - r, c.y - r), Size(2 * r, 2 * r), style = Stroke(grosor, cap = StrokeCap.Round))
        // La célula, en el centro, y la chispa: tres trazos que salen de ella y se escapan por el hueco.
        drawCircle(color, grosor * 1.05f, c)
        listOf(-68.0 to 1.16f, -50.0 to 1.42f, -32.0 to 1.16f).forEach { (g, largo) ->
            val a = Math.toRadians(g)
            drawLine(color, Offset(c.x + (r * 0.44f * cos(a)).toFloat(), c.y + (r * 0.44f * sin(a)).toFloat()),
                Offset(c.x + (r * largo * cos(a)).toFloat(), c.y + (r * largo * sin(a)).toFloat()), grosor, StrokeCap.Round)
        }
    }

    /**
     * La retícula de una pantalla redonda: los [anillos] de distancia, la cruz y, en el borde, una marca cada diez grados
     * (más larga cada treinta).
     */
    fun DrawScope.reticula(c: Offset, r: Float, color: Color, grosor: Float, anillos: Int = 3) {
        for (k in 1..anillos) drawCircle(color.copy(alpha = color.alpha * (if (k == anillos) 1f else 0.6f)), r * k / anillos, c, style = Stroke(grosor))
        drawLine(color.copy(alpha = color.alpha * 0.6f), Offset(c.x - r, c.y), Offset(c.x + r, c.y), grosor * 0.8f)
        drawLine(color.copy(alpha = color.alpha * 0.6f), Offset(c.x, c.y - r), Offset(c.x, c.y + r), grosor * 0.8f)
        for (k in 0 until 36) {
            val a = Math.toRadians(k * 10.0); val l = if (k % 3 == 0) 0.1f else 0.05f
            drawLine(color, Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()),
                Offset(c.x + (r * (1 - l) * cos(a)).toFloat(), c.y + (r * (1 - l) * sin(a)).toFloat()), grosor * 0.8f)
        }
    }

    /** El barrido del radar: la línea que apunta a [angulo] grados y la estela que deja detrás, apagándose. */
    fun DrawScope.barrido(c: Offset, r: Float, angulo: Float, color: Color, grosor: Float) {
        rotate(angulo, c) {
            drawCircle(Brush.sweepGradient(0f to Color.Transparent, 0.8f to Color.Transparent, 1f to color.copy(alpha = color.alpha * 0.42f), center = c), r, c)
        }
        val a = Math.toRadians(angulo.toDouble())
        drawLine(color, c, Offset(c.x + (r * cos(a)).toFloat(), c.y + (r * sin(a)).toFloat()), grosor, StrokeCap.Round)
    }

    /** Un piloto de consola: el bisel de cromo, la lente y, si está [encendido], su halo. */
    fun DrawScope.piloto(c: Offset, r: Float, color: Color, encendido: Boolean, bisel: Color = CROMO) {
        if (encendido) drawCircle(Brush.radialGradient(listOf(color.copy(alpha = 0.45f), Color.Transparent), c, r * 2.6f), r * 2.6f, c)
        drawCircle(bisel, r * 1.35f, c)
        drawCircle(BAQUELITA, r * 1.12f, c)
        drawCircle(if (encendido) color else color.copy(alpha = 0.35f), r, c)
        if (encendido) drawCircle(Color.White.copy(alpha = 0.55f), r * 0.32f, Offset(c.x - r * 0.3f, c.y - r * 0.32f))
    }

    /**
     * Un botón cuadrado de consola centrado en [c], de [lado]: el bisel negro, la lente de [color] (encendida o, si no, hundida
     * hacia [apagado]) y su brillo arriba; con [halo] más de 0, el resplandor de la luz encendida en la penumbra.
     */
    fun DrawScope.botonCuadrado(c: Offset, lado: Float, color: Color, encendido: Boolean, apagado: Color = CONSOLA_OSCURA, halo: Float = 0f) {
        if (encendido && halo > 0f) drawCircle(Brush.radialGradient(listOf(color.copy(alpha = 0.32f * halo), color.copy(alpha = 0f)), c, lado * 1.7f), lado * 1.7f, c)
        val b = lado * 0.16f
        drawRect(Color.Black.copy(alpha = 0.6f), Offset(c.x - lado / 2 - b, c.y - lado / 2 - b), Size(lado + 2 * b, lado + 2 * b))
        drawRect(if (encendido) color else lerp(color, apagado, 0.8f), Offset(c.x - lado / 2, c.y - lado / 2), Size(lado, lado))
        drawLine(Color.White.copy(alpha = if (encendido) 0.55f else 0.12f), Offset(c.x - lado * 0.32f, c.y - lado * 0.3f), Offset(c.x + lado * 0.12f, c.y - lado * 0.3f), lado * 0.14f)
    }

    /** Una ruta de salto entre estrellas: el trazo punteado y, en cada parada, el anillo de llegada. */
    fun DrawScope.rutaSalto(puntos: List<Offset>, color: Color, grosor: Float, anillo: Float) {
        if (puntos.size < 2) return
        val p = Path().apply { moveTo(puntos[0].x, puntos[0].y); puntos.drop(1).forEach { lineTo(it.x, it.y) } }
        drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round, pathEffect = PathEffect.dashPathEffect(floatArrayOf(grosor * 0.2f, grosor * 2.4f))))
        puntos.forEach { drawCircle(color, anillo, it, style = Stroke(grosor * 0.7f)) }
    }

    // ---------------------------------------------------------------- La base escondida en ruinas

    /**
     * El muro de sillares de las ruinas en [r]: hiladas de [alto] con piedras de largo desigual, cada una con su tono, el
     * canto de luz arriba a la izquierda y la sombra abajo a la derecha, poros, alguna esquina desportillada y, encima,
     * manchas de humedad que bajan de las juntas y grumos de musgo. [brillo] baja las luces de noche (1 de día, ~0.3 de
     * noche); los colores ya vienen en su tono de día o de noche.
     */
    fun DrawScope.muroSillares(r: Rect, alto: Float, piedra: Color, junta: Color, humedad: Color, musgo: Color, semilla: Int, brillo: Float = 1f) {
        val rnd = Random(semilla * 7919L + 13)
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(junta, r.topLeft, r.size)
            val g = alto * 0.06f
            var y = r.top - rnd.nextFloat() * alto * 0.4f
            while (y < r.bottom) {
                var x = r.left - rnd.nextFloat() * alto * 1.6f
                val caida = (rnd.nextFloat() - 0.5f) * g
                while (x < r.right) {
                    val ancho = alto * (1.25f + rnd.nextFloat() * 1.5f)
                    val s = Rect(x + g, y + g + caida, x + ancho - g, y + alto - g + caida)
                    val tono = lerp(piedra, if (rnd.nextBoolean()) Color.White else Color.Black, rnd.nextFloat() * 0.13f)
                    val rad = CornerRadius(g * (1.4f + rnd.nextFloat() * 1.6f))
                    drawRoundRect(tono, s.topLeft, s.size, rad)
                    // La cara de la piedra: más clara arriba, donde le da la luz, y una veta de tono.
                    drawRect(Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.07f * brillo), Color.Transparent, Color.Black.copy(alpha = 0.1f)), s.top, s.bottom),
                        Offset(s.left + g * 0.5f, s.top + g * 0.5f), Size(s.width - g, s.height - g))
                    drawLine(Color.White.copy(alpha = 0.2f * brillo), Offset(s.left + g * 1.2f, s.top + g * 0.7f), Offset(s.right - g * 1.6f, s.top + g * 0.7f), g * 0.8f, StrokeCap.Round)
                    drawLine(Color.White.copy(alpha = 0.12f * brillo), Offset(s.left + g * 0.7f, s.top + g * 1.2f), Offset(s.left + g * 0.7f, s.bottom - g * 1.6f), g * 0.7f, StrokeCap.Round)
                    drawLine(Color.Black.copy(alpha = 0.26f), Offset(s.left + g * 1.4f, s.bottom - g * 0.6f), Offset(s.right - g, s.bottom - g * 0.6f), g * 1.1f, StrokeCap.Round)
                    drawLine(Color.Black.copy(alpha = 0.2f), Offset(s.right - g * 0.6f, s.top + g * 1.4f), Offset(s.right - g * 0.6f, s.bottom - g), g * 0.9f, StrokeCap.Round)
                    // Los poros y las picaduras de la piedra vieja.
                    repeat(4 + rnd.nextInt(6)) {
                        val p = Offset(s.left + s.width * rnd.nextFloat(), s.top + s.height * rnd.nextFloat())
                        val pr = g * (0.25f + rnd.nextFloat() * 0.6f)
                        drawCircle(Color.Black.copy(alpha = 0.16f), pr, p)
                        drawCircle(Color.White.copy(alpha = 0.08f * brillo), pr * 0.7f, Offset(p.x + pr * 0.5f, p.y + pr * 0.6f))
                    }
                    // Una esquina desportillada, con la grieta que sale de ella.
                    if (rnd.nextFloat() < 0.24f) {
                        val d = alto * (0.14f + rnd.nextFloat() * 0.16f)
                        val abajo = rnd.nextBoolean()
                        val ex = if (rnd.nextBoolean()) s.right else s.left
                        val ey = if (abajo) s.bottom else s.top
                        val sx = if (ex == s.right) -1f else 1f; val sy = if (abajo) -1f else 1f
                        drawPath(Path().apply { moveTo(ex, ey); lineTo(ex + sx * d, ey); lineTo(ex, ey + sy * d); close() }, junta)
                        drawLine(Color.Black.copy(alpha = 0.3f), Offset(ex + sx * d * 0.5f, ey + sy * d * 0.5f), Offset(ex + sx * d * 1.5f, ey + sy * d * 1.2f), g * 0.5f, StrokeCap.Round)
                    }
                    // La humedad que baja de la junta de arriba, a chorreones.
                    if (rnd.nextFloat() < 0.3f) {
                        val mx = s.left + s.width * (0.15f + 0.6f * rnd.nextFloat())
                        val largo = alto * (0.5f + rnd.nextFloat() * 1.3f)
                        val mw = s.width * (0.18f + 0.3f * rnd.nextFloat())
                        drawRect(Brush.verticalGradient(listOf(humedad.copy(alpha = humedad.alpha * 0.55f), Color.Transparent), s.top, s.top + largo),
                            Offset(mx - mw / 2, s.top), Size(mw, minOf(largo, s.height)))
                        drawLine(humedad.copy(alpha = humedad.alpha * 0.4f), Offset(mx, s.top), Offset(mx + g, s.top + largo * 0.9f), g * 0.8f, StrokeCap.Round)
                    }
                    // El musgo, en grumos pegados a la junta.
                    if (rnd.nextFloat() < 0.2f) {
                        val mx = s.left + s.width * rnd.nextFloat(); val my = if (rnd.nextBoolean()) s.top else s.bottom
                        repeat(7 + rnd.nextInt(8)) {
                            val pr = g * (0.5f + rnd.nextFloat() * 1.1f)
                            drawCircle(lerp(musgo, Color.Black, rnd.nextFloat() * 0.3f), pr, Offset(mx + (rnd.nextFloat() - 0.5f) * alto * 0.7f, my + (rnd.nextFloat() - 0.5f) * g * 3.5f))
                        }
                    }
                    x += ancho
                }
                y += alto
            }
            // Las manchas grandes de humedad que bajan del techo por varias hiladas.
            repeat(2 + rnd.nextInt(2)) {
                val mx = r.left + r.width * rnd.nextFloat(); val mw = alto * (1.2f + rnd.nextFloat() * 2f)
                val largo = r.height * (0.3f + rnd.nextFloat() * 0.5f)
                drawOval(Brush.verticalGradient(listOf(humedad.copy(alpha = humedad.alpha * 0.45f), Color.Transparent), r.top - largo * 0.2f, r.top + largo),
                    Offset(mx - mw / 2, r.top - largo * 0.2f), Size(mw, largo * 1.2f))
            }
        }
    }

    /**
     * Una raíz que cuelga desde [desde]: baja culebreando [largo], se adelgaza hasta la punta, con su sombra en lo que tiene
     * detrás, el brillo de un lado y, si [ramas], alguna raíz más fina que se abre.
     */
    fun DrawScope.raiz(desde: Offset, largo: Float, grosor: Float, color: Color, luz: Color, semilla: Int, ramas: Boolean = true, sombra: Float = 0.3f) {
        val rnd = Random(semilla * 31L + 7)
        val crudos = mutableListOf(desde)
        var p = desde; var lado = if (rnd.nextBoolean()) 1f else -1f; var rec = 0f
        while (rec < largo) {
            val paso = maxOf(grosor * 1.5f, largo * 0.06f) * (0.8f + rnd.nextFloat() * 0.9f)
            p = Offset(p.x + lado * (grosor * 0.4f + largo * 0.03f) * (0.3f + rnd.nextFloat()), p.y + paso)
            crudos += p; rec += paso
            if (rnd.nextFloat() < 0.7f) lado = -lado
        }
        // Se suaviza el zigzag dos veces (cortes de Chaikin) para que la raíz se curve.
        var pts: List<Offset> = crudos
        repeat(2) {
            val q = mutableListOf(pts.first())
            for (i in 0 until pts.size - 1) {
                val a = pts[i]; val b = pts[i + 1]
                q += Offset(a.x * 0.75f + b.x * 0.25f, a.y * 0.75f + b.y * 0.25f); q += Offset(a.x * 0.25f + b.x * 0.75f, a.y * 0.25f + b.y * 0.75f)
            }
            q += pts.last(); pts = q
        }
        val n = pts.size
        fun ancho(i: Int) = grosor * (1f - 0.85f * i / n)
        if (sombra > 0f) for (i in 0 until n - 1) drawLine(Color.Black.copy(alpha = sombra), Offset(pts[i].x + grosor * 0.7f, pts[i].y + grosor * 0.6f), Offset(pts[i + 1].x + grosor * 0.7f, pts[i + 1].y + grosor * 0.6f), ancho(i), StrokeCap.Round)
        for (i in 0 until n - 1) drawLine(color, pts[i], pts[i + 1], ancho(i), StrokeCap.Round)
        // El lado de la sombra, a la derecha, y el brillo blando del lado de la luz.
        for (i in 0 until n - 1) {
            val o = ancho(i) * 0.26f
            drawLine(Color.Black.copy(alpha = 0.3f), Offset(pts[i].x + o, pts[i].y), Offset(pts[i + 1].x + o, pts[i + 1].y), ancho(i) * 0.4f, StrokeCap.Round)
            drawLine(luz, Offset(pts[i].x - o, pts[i].y), Offset(pts[i + 1].x - o, pts[i + 1].y), ancho(i) * 0.3f, StrokeCap.Round)
        }
        // Los nudos de la corteza, de vez en cuando.
        for (i in 4 until n - 4 step 9) {
            val w = ancho(i)
            drawCircle(color, w * 0.62f, Offset(pts[i].x + w * 0.1f, pts[i].y))
            drawLine(Color.Black.copy(alpha = 0.25f), Offset(pts[i].x - w * 0.4f, pts[i].y - w * 0.1f), Offset(pts[i].x + w * 0.45f, pts[i].y + w * 0.25f), w * 0.16f)
        }
        if (ramas && grosor > 0.6f) {
            var k = 0
            for (i in n / 4 until n - n / 5 step maxOf(1, n / 5)) {
                if (rnd.nextFloat() < 0.55f) raiz(pts[i], largo * (0.22f + 0.25f * rnd.nextFloat()), ancho(i) * 0.5f, color, luz, semilla * 7 + k + 1, false, sombra)
                k++
            }
        }
    }

    /**
     * Los trazos del vidrio grabado en [r], iluminados por el canto: cada uno con su halo ancho, su resplandor y su línea.
     * [estilo] 0: rectas largas que cruzan el panel en ángulos distintos y dos curvas grandes; 1: verticales finas, una
     * diagonal y las órbitas encajadas unas en otras; 2: las órbitas sueltas con su arco de rumbo (para paneles chicos).
     */
    fun DrawScope.trazosVidrio(r: Rect, color: Color, grosor: Float, alfa: Float, semilla: Int, estilo: Int = 0) {
        val rnd = Random(semilla * 131L + 3)
        fun capa(dibuja: (Color, Float) -> Unit) {
            dibuja(color.copy(alpha = alfa * 0.12f), grosor * 5f)
            dibuja(color.copy(alpha = alfa * 0.3f), grosor * 2.3f)
            dibuja(color.copy(alpha = alfa), grosor)
        }
        clipRect(r.left, r.top, r.right, r.bottom) {
            if (estilo == 1) {
                var x = r.left + r.width * 0.06f
                while (x < r.right) {
                    capa { c, g -> drawLine(c, Offset(x, r.top), Offset(x, r.bottom), g) }
                    x += r.width * (0.05f + rnd.nextFloat() * 0.07f)
                }
                capa { c, g -> drawLine(c, Offset(r.left + r.width * 0.25f, r.bottom), Offset(r.right, r.top + r.height * 0.05f), g) }
                val c0 = Offset(r.left + r.width * (0.38f + rnd.nextFloat() * 0.1f), r.top + r.height * 0.6f)
                val rMax = minOf(r.width, r.height) * 0.42f
                for (k in 0 until 6) {
                    val rr = rMax * (1f - k * 0.15f)
                    val cc = Offset(c0.x - (rMax - rr) * 0.55f, c0.y + (rMax - rr) * 0.25f)
                    capa { c, g -> drawCircle(c, rr, cc, style = Stroke(g)) }
                }
            } else if (estilo == 2) {
                val c0 = Offset(r.left + r.width * (0.4f + rnd.nextFloat() * 0.2f), r.top + r.height * (0.4f + rnd.nextFloat() * 0.2f))
                val rMax = minOf(r.width, r.height) * 0.4f
                for (k in 0 until 4) {
                    val rr = rMax * (1f - k * 0.22f)
                    capa { c, g -> drawCircle(c, rr, Offset(c0.x - (rMax - rr) * 0.5f, c0.y), style = Stroke(g)) }
                }
                capa { c, g -> drawLine(c, Offset(r.left, r.bottom - r.height * 0.1f), Offset(r.right, r.top + r.height * 0.2f), g) }
                capa { c, g -> drawLine(c, Offset(r.left + r.width * 0.15f, r.top), Offset(r.left + r.width * 0.15f, r.bottom), g) }
            } else {
                repeat(5) { k ->
                    val desdeAbajo = k % 3 != 0
                    val a = if (desdeAbajo) Offset(r.left + r.width * rnd.nextFloat(), r.bottom) else Offset(r.left, r.top + r.height * rnd.nextFloat())
                    val b = Offset(r.left + r.width * (0.2f + rnd.nextFloat() * 1.1f), r.top)
                    capa { c, g -> drawLine(c, a, b, g) }
                }
                capa { c, g -> drawLine(c, Offset(r.left, r.top + r.height * 0.55f), Offset(r.right, r.top + r.height * 0.52f), g) }
                capa { c, g -> drawLine(c, Offset(r.left + r.width * 0.22f, r.top), Offset(r.left + r.width * 0.22f, r.bottom), g) }
                for (k in 0 until 2) {
                    val rr = r.width * (0.7f + 0.35f * k)
                    val cc = Offset(r.left + r.width * (1.05f + 0.1f * k), r.bottom + rr * 0.15f)
                    capa { c, g -> drawCircle(c, rr, cc, style = Stroke(g)) }
                }
            }
        }
    }

    /** Una franja de peligro en [r]: el amarillo y las bandas negras en diagonal, cada [paso]. */
    fun DrawScope.franjaPeligro(r: Rect, amarillo: Color, negro: Color, paso: Float) {
        clipRect(r.left, r.top, r.right, r.bottom) {
            drawRect(amarillo, r.topLeft, r.size)
            val alto = r.height
            var x = r.left - alto - paso
            while (x < r.right + paso) {
                drawPath(Path().apply { moveTo(x, r.bottom); lineTo(x + paso / 2, r.bottom); lineTo(x + paso / 2 + alto, r.top); lineTo(x + alto, r.top); close() }, negro)
                x += paso
            }
        }
    }

    /**
     * Una lámpara de cúpula blanca: la boca de la cúpula centrada en [c], de radio [r], sobre su pie de [pie] de alto. Con
     * [brillo] más de 0, alumbra: el halo alrededor y la luz que sale por debajo de la cúpula.
     */
    fun DrawScope.lamparaCupula(c: Offset, r: Float, pie: Float, metal: Color, cupula: Color, brillo: Float, luz: Color = Color(0xFFFFF4DE)) {
        if (brillo > 0f) drawCircle(Brush.radialGradient(listOf(luz.copy(alpha = 0.55f * brillo), luz.copy(alpha = 0.2f * brillo), Color.Transparent), c, r * 6f), r * 6f, c)
        if (pie > 0f) {
            val ancho = r * 0.3f
            drawRect(Color.Black.copy(alpha = 0.25f), Offset(c.x - ancho / 2 + r * 0.12f, c.y), Size(ancho, pie))
            drawRect(metal, Offset(c.x - ancho / 2, c.y), Size(ancho, pie))
            drawRect(Color.White.copy(alpha = 0.25f), Offset(c.x - ancho / 2 + ancho * 0.15f, c.y), Size(ancho * 0.22f, pie))
            drawOval(metal, Offset(c.x - r * 0.55f, c.y + pie - r * 0.14f), Size(r * 1.1f, r * 0.28f))
        }
        // La boca, el aro oscuro y la luz que sale por debajo.
        drawOval(lerp(metal, Color.Black, 0.3f), Offset(c.x - r * 1.02f, c.y - r * 0.16f), Size(r * 2.04f, r * 0.34f))
        if (brillo > 0f) drawOval(luz.copy(alpha = 0.55f + 0.4f * brillo), Offset(c.x - r * 0.8f, c.y - r * 0.08f), Size(r * 1.6f, r * 0.2f))
        val domo = Path().apply { addArcRad(Rect(c.x - r, c.y - r * 0.95f, c.x + r, c.y + r * 0.95f), Math.PI.toFloat(), Math.PI.toFloat()); close() }
        drawPath(domo, Brush.radialGradient(listOf(lerp(cupula, Color.White, 0.6f), cupula, lerp(cupula, Color.Black, 0.22f)), Offset(c.x - r * 0.3f, c.y - r * 0.6f), r * 1.5f))
        drawArc(Color.White.copy(alpha = 0.6f), 200f, 50f, false, Offset(c.x - r * 0.75f, c.y - r * 0.72f), Size(r * 1.5f, r * 1.44f), style = Stroke(r * 0.1f, cap = StrokeCap.Round))
    }

    /** Un cable grueso y corrugado a lo largo de [p]: su sombra, el cuerpo, las estrías y el brillo de arriba. */
    fun DrawScope.cableGrueso(p: Path, grosor: Float, color: Color, brillo: Float = 1f, sombra: Float = 0.3f) {
        if (sombra > 0f) translate(grosor * 0.3f, grosor * 0.55f) { drawPath(p, Color.Black.copy(alpha = sombra), style = Stroke(grosor, cap = StrokeCap.Round)) }
        drawPath(p, color, style = Stroke(grosor, cap = StrokeCap.Round))
        drawPath(p, Color.Black.copy(alpha = 0.35f), style = Stroke(grosor, pathEffect = PathEffect.dashPathEffect(floatArrayOf(grosor * 0.2f, grosor * 0.34f))))
        translate(-grosor * 0.12f, -grosor * 0.2f) { drawPath(p, Color.White.copy(alpha = 0.18f * brillo), style = Stroke(grosor * 0.24f, cap = StrokeCap.Round)) }
    }

    /** Un tubo de cobre recto de [a] a [b]: el cuerpo, el brillo de un lado, la sombra del otro y las juntas cada tanto. */
    fun DrawScope.tubo(a: Offset, b: Offset, grosor: Float, color: Color, brillo: Float = 1f, juntas: Float = 0f) {
        val dx = b.x - a.x; val dy = b.y - a.y; val l = sqrt(dx * dx + dy * dy).coerceAtLeast(0.001f)
        val nx = -dy / l; val ny = dx / l
        fun lado(f: Float) = Offset(nx * grosor * f, ny * grosor * f)
        drawLine(Color.Black.copy(alpha = 0.28f), a + lado(0.45f) + Offset(grosor * 0.2f, grosor * 0.35f), b + lado(0.45f) + Offset(grosor * 0.2f, grosor * 0.35f), grosor * 0.9f)
        drawLine(color, a, b, grosor)
        drawLine(lerp(color, Color.Black, 0.35f), a + lado(0.32f), b + lado(0.32f), grosor * 0.34f)
        drawLine(lerp(color, Color.White, 0.35f).copy(alpha = 0.5f + 0.5f * brillo), a - lado(0.22f), b - lado(0.22f), grosor * 0.16f)
        if (juntas > 0f) {
            var d = juntas * 0.5f
            while (d < l) {
                val p = Offset(a.x + dx / l * d, a.y + dy / l * d)
                drawLine(lerp(color, Color.Black, 0.25f), p - lado(0.62f), p + lado(0.62f), grosor * 0.5f)
                drawLine(Color.White.copy(alpha = 0.2f * brillo), p - lado(0.6f) - Offset(dx / l * grosor * 0.2f, dy / l * grosor * 0.2f), p - Offset(dx / l * grosor * 0.2f, dy / l * grosor * 0.2f), grosor * 0.12f)
                d += juntas
            }
        }
    }
}
