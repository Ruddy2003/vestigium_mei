// El arnés de escritorio de los muebles de la pestaña Diario (el Ideario y el estante del diario).
//
// Compila los archivos REALES de la app (enlazados desde app/src/main, sin copiarlos) junto con unos pocos sustitutos de lo que
// solo existe en Android, y los dibuja con Compose de escritorio. Ver LEEME.md.

import java.security.MessageDigest

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.compose") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
}

// La carpeta `src/main` de la app. Se puede cambiar con -Papp=/otra/ruta/app/src/main (otro clon u otro árbol de trabajo).
val app: File = (findProperty("app") as String?)?.let { file(it) } ?: file("../../app/src/main")
val codigo = app.resolve("java")
// Con otra app, dibujar.sh pide otra carpeta de compilación (-PdirCompilacion=...), para no mezclar los .class.
(findProperty("dirCompilacion") as String?)?.let { layout.buildDirectory.set(file(it)) }
val paquete = "io/github/ruddy2003/vestigium"

/**
 * Los archivos de la app que se compilan tal cual. Las carpetas enteras de los muebles (así cualquier archivo nuevo que se
 * agregue en ellas entra solo) y los archivos sueltos de los que dependen.
 */
val enlazados = listOf(
    "$paquete/ui/ideario/**/*.kt",
    "$paquete/ui/biblioteca/**/*.kt",
    "$paquete/ui/muebles/**/*.kt",
    // Los trazos y la cuenta de los temas de la tanda nueva (los usan sus muebles).
    "$paquete/ui/*Trazos.kt",
    "$paquete/ui/Galia.kt", "$paquete/ui/Nouveau.kt", "$paquete/ui/Psicodelia.kt", "$paquete/ui/Frente.kt", "$paquete/ui/Arcontes.kt",
    // Los temas: TemaId, sus colores, sus letras, sus escalas de color y cómo nombran los meses.
    "$paquete/ui/Tema.kt", "$paquete/ui/ModoHora.kt", "$paquete/ui/Disenios.kt", "$paquete/ui/Estilo.kt",
    // La cara y la intensidad de los temas que las tienen (Psicodelia): el esquema y los dibujos las leen.
    "$paquete/ui/CaraTema.kt",
    // Las piezas de Psicodelia v2 (tanda 2) y sus piezas comunes, para la muestra `comun psicodelia`.
    "$paquete/ui/PsicodeliaPiezas.kt", "$paquete/ui/comun/ComunPsicodelia.kt", "$paquete/ui/IconosPestana.kt",
    // Los logros, los trofeos y las figuras de pantalla de Psicodelia v2 (tanda 4), para la muestra `logros psicodelia`.
    "$paquete/ui/logros/LogrosPsicodelia.kt", "$paquete/ui/PsicodeliaPantallas.kt",
    // Lo que Estilo necesita para nombrar meses y fechas en cada tema.
    "$paquete/ui/Temas.kt", "$paquete/ui/Vapor.kt", "$paquete/ui/CuentaMaya.kt", "$paquete/ui/NombresNuevos.kt", "$paquete/ui/Textos.kt",
    "$paquete/ui/Claves.kt", "$paquete/ui/nombres/**/*.kt",
    // Los datos: el modelo (Dia, Config), el conteo de palabras y las fechas.
    "$paquete/data/Modelo.kt", "$paquete/data/Texto.kt", "$paquete/data/Fechas.kt",
    // Los tipos que Modelo usa en sus campos.
    "$paquete/data/Catalogo.kt", "$paquete/data/Especiales.kt",
    // Los cajones del Ideario (cómo se agrupan las ideas por tema) y cómo cada tema cuenta sus fichas («7 óstraka»).
    "$paquete/data/Ideario.kt",
)

/**
 * Piezas sueltas de archivos de la app que no se pueden compilar enteros en escritorio (dependen de pantallas, de Android o
 * de media app): se sacan del archivo real en cada compilación, con su texto tal cual, y se escriben en build/generado.
 * Archivo de la app -> nombres de las declaraciones de nivel superior que se toman.
 */
val piezas = linkedMapOf(
    "$paquete/ui/Escena.kt" to listOf("halo", "luzCalida"),
    "$paquete/ui/Libros.kt" to listOf("Rayas", "Pagina"),
    "$paquete/data/Mundo.kt" to listOf("CiudadVisitada", "SitioMarcado"),
    "$paquete/ui/Decoracion.kt" to listOf("piezasMano", "cruzPatada", "rad", "mas"),
    // Cómo arma FicheroIdeas los cajones del mueble y su rótulo (la llamada a MuebleIdeas se repite en arnes/Dibujar.kt).
    "$paquete/ui/pantallas/Fichero.kt" to listOf("cajones", "rotuloCajon"),
    "$paquete/ui/ArchivoIdeas.kt" to listOf("ArchivoIdeas", "archivoIdeas"),
    // El contrato de las piezas comunes, sin `disenoComun` (que nombra los 40 objetos de los temas).
    "$paquete/ui/comun/DisenoComun.kt" to listOf("DisenoComun", "EstadoAdorno", "EstadoBalanza", "EstadoMes", "EstadoAsiento", "TipoTarjeta",
        "EstadoTarjeta", "EstadoFila", "EstadoVacio", "EstadoAviso", "EstadoCifra", "EstadoSeparador", "EstadoBotonTexto", "EstadoMarco"),
    // El contrato de los logros, sin `disenoLogros` (que nombra los 40 objetos de los temas).
    "$paquete/ui/logros/DisenoLogros.kt" to listOf("DisenoLogros", "EstadoObjeto", "EstadoMarcador", "EstadoVictoria", "EstadoSalon", "EstadoPedestal"),
)

val generado = layout.buildDirectory.dir("generado/kotlin")

kotlin {
    jvmToolchain(21)
    sourceSets["main"].kotlin.srcDir(generado)
    compilerOptions {
        // Los mismos avisos que en la app no importan aquí: solo los errores.
        suppressWarnings.set(true)
    }
}

dependencies {
    // Compose Multiplatform 1.7.3 = Compose de Android 1.7 (la app usa el BOM 2024.12.01, Compose 1.7.6).
    implementation("org.jetbrains.compose.desktop:desktop-jvm-linux-x64:1.7.3")
    implementation("org.jetbrains.compose.material3:material3-desktop:1.7.3")
    implementation("org.jetbrains.compose.material:material-icons-core-desktop:1.7.3")
    // El ciclo de vida (ModoHora.kt lo escucha para cambiar de modo al volver a la app).
    implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.9.0")
}

/** Genera `R` (las letras de res/font, con su archivo) y las piezas sacadas de los archivos de la app. */
val generar by tasks.registering {
    val fuentes = app.resolve("res/font")
    val archivosPiezas = piezas.keys.map { codigo.resolve(it) }
    inputs.dir(fuentes)
    inputs.files(archivosPiezas)
    inputs.property("piezas", piezas.toString())
    outputs.dir(generado)
    doLast {
        val salida = generado.get().asFile
        salida.deleteRecursively()
        val dir = salida.resolve(paquete).apply { mkdirs() }
        // R: cada letra con un número, como en Android, y la tabla número -> archivo para el sustituto de Font(id).
        val ttf = fuentes.listFiles()!!.filter { it.isFile }.sortedBy { it.name }
        dir.resolve("R.kt").writeText(buildString {
            appendLine("// Generado por el arnés a partir de app/src/main/res/font. No se edita.")
            appendLine("package io.github.ruddy2003.vestigium")
            appendLine("object R {")
            appendLine("    object font {")
            ttf.forEachIndexed { i, f -> appendLine("        const val ${f.nameWithoutExtension} = ${0x7f080000 + i}") }
            appendLine("    }")
            appendLine("    object drawable {")
            // Los papeles de las páginas: no se dibujan en los muebles; basta con que existan.
            val dibujos = app.resolve("res").listFiles()!!.filter { it.name.startsWith("drawable") }.flatMap { it.listFiles()!!.toList() }
                .map { it.nameWithoutExtension }.distinct().sorted()
            dibujos.forEachIndexed { i, n -> appendLine("        const val $n = ${0x7f090000 + i}") }
            appendLine("    }")
            appendLine("}")
            appendLine("internal val ARCHIVOS_FUENTE: Map<Int, String> = mapOf(")
            ttf.forEachIndexed { i, f -> appendLine("    ${0x7f080000 + i} to \"${f.absolutePath.replace("\\", "/")}\",") }
            appendLine(")")
        })
        piezas.forEach { (ruta, nombres) ->
            val origen = codigo.resolve(ruta)
            val texto = origen.readText()
            val nombre = "Pieza" + origen.nameWithoutExtension + ".kt"
            val destino = salida.resolve(ruta).parentFile.apply { mkdirs() }.resolve(nombre)
            destino.writeText(extraerPiezas(texto, nombres, origen.name))
        }
    }
}

/**
 * Saca de [texto] (un archivo de Kotlin) las declaraciones de nivel superior llamadas [nombres], con su comentario de
 * documentación, y las importaciones del archivo que esas piezas usan. Las declaraciones se reconocen por empezar en la
 * columna 0 (como todo el código de la app) y terminan donde la siguiente empieza.
 */
fun extraerPiezas(texto: String, nombres: List<String>, origen: String): String {
    val lineas = texto.lines()
    val paqueteLinea = lineas.first { it.startsWith("package ") }
    val importaciones = lineas.filter { it.startsWith("import ") }
    // Dónde empieza cada declaración de nivel superior (sin contar su comentario).
    val decl = Regex("""^(?:@\w+(?:\([^)]*\))?\s+)*(?:(?:private|internal|public|inline|data|enum|sealed|abstract|open|operator|infix|suspend|tailrec|const)\s+)*(?:fun|val|var|class|object|interface|typealias)\b(?:\s*<[^>]*>)?\s*(?:[\w.<>?, ]+\.)?(\w+)""")
    val inicios = lineas.indices.filter { i ->
        val l = lineas[i]
        l.isNotEmpty() && !l[0].isWhitespace() && !l.startsWith("import ") && !l.startsWith("package ") && !l.startsWith("//") &&
            !l.startsWith("/*") && !l.startsWith(" *") && !l.startsWith("*") && !l.startsWith("}") && !l.startsWith(")") && decl.containsMatchIn(l)
    }
    // El comentario de documentación (o las anotaciones) que van justo encima.
    fun arranque(i: Int): Int {
        var k = i
        while (k > 0) {
            val p = lineas[k - 1]
            if (p.startsWith("/**") || p.startsWith(" *") || p.startsWith("@") || p.startsWith("//")) k-- else break
        }
        return k
    }
    val bloques = nombres.map { n ->
        val i = inicios.firstOrNull { decl.find(lineas[it])!!.groupValues[1] == n }
            ?: error("El arnés no encontró «$n» en $origen: si cambió de nombre o de archivo, actualiza `piezas` en build.gradle.kts.")
        val sig = inicios.firstOrNull { it > i }?.let { arranque(it) } ?: lineas.size
        // Lo privado del archivo se vuelve interno, para que el arnés lo pueda llamar desde otro archivo.
        lineas.subList(arranque(i), sig).joinToString("\n").trimEnd().replaceFirst(Regex("""(?m)^private """), "internal ")
    }
    val cuerpo = bloques.joinToString("\n\n")
    val usadas = importaciones.filter { imp ->
        val corto = imp.removePrefix("import ").substringAfter(" as ").substringAfterLast('.').trim()
        // Las de los delegados (`by remember`) no aparecen por nombre en el código.
        corto == "*" || corto in setOf("getValue", "setValue", "provideDelegate") || Regex("""\b${Regex.escape(corto)}\b""").containsMatchIn(cuerpo)
    }
    return "// Generado por el arnés: piezas de $origen (${nombres.joinToString()}), copiadas tal cual en cada compilación. No se edita.\n" +
        "@file:Suppress(\"unused\")\n$paqueteLinea\n\n${usadas.joinToString("\n")}\n\n$cuerpo\n"
}

tasks.compileKotlin {
    dependsOn(generar)
    source(fileTree(codigo) { enlazados.forEach { include(it) } })
}

tasks.jar {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

/**
 * Lo que necesita dibujar.sh para correr sin Gradle: el jar del arnés en una copia con nombre por su contenido (así una
 * compilación nueva no cambia los archivos que otra llamada está leyendo) y el classpath de las bibliotecas.
 */
val instantanea by tasks.registering {
    dependsOn(tasks.jar)
    val jar = tasks.jar.flatMap { it.archiveFile }
    val cp = configurations.runtimeClasspath
    val destino = layout.buildDirectory.dir("instantaneas")
    outputs.upToDateWhen { false }
    doLast {
        val j = jar.get().asFile
        val hash = MessageDigest.getInstance("SHA-1").digest(j.readBytes()).joinToString("") { "%02x".format(it) }.take(16)
        val dir = destino.get().asFile.apply { mkdirs() }
        val copia = dir.resolve("arnes-$hash.jar")
        if (!copia.exists()) {
            val tmp = dir.resolve("arnes-$hash.jar.tmp")
            j.copyTo(tmp, overwrite = true)
            tmp.renameTo(copia)
        }
        // Las copias viejas se borran después de una hora (una llamada no tarda tanto).
        dir.listFiles()!!.filter { it.name.endsWith(".jar") && it != copia && System.currentTimeMillis() - it.lastModified() > 3_600_000 }.forEach { it.delete() }
        val linea = (listOf(copia) + cp.get().files).joinToString(File.pathSeparator) { it.absolutePath }
        val f = dir.resolve("classpath.txt")
        val t = dir.resolve("classpath.txt.tmp")
        t.writeText(linea); t.renameTo(f)
    }
}
