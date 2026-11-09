# Escenas y aura en Diario y Vida

Fecha: 9 nov 2026. Rama: `claude/elegant-mccarthy-hxriov`.

## Qué pidió el autor

Los bordes no estaban unificados en la pestaña Diario:
- algunos muebles iban solos sobre la página (el Ideario, el diario de hoy);
- otros llevaban un fondo cuadrado (la biblioteca del diario, la libreta);
- otros, fondo y además una línea de contorno (los sueños).

El autor pidió:
- el fondo detrás de cada mueble u objeto, sin línea de contorno, y lo mismo en Vida;
- un aura muy tenue, del tema, solo en los objetos que se tocan uno por uno, que se encienda al tocarlos o al pasar el dedo encima;
- sin aura donde el mueble entero es un solo botón.

## Qué se hizo

| Pieza | Antes | Ahora |
|---|---|---|
| `Escena` (`ui/Escena.kt`) | — | La pared del tema (`paredTema`) sobre un tono apenas distinto de la página, más clara arriba y más oscura abajo; las esquinas del tema; sin contorno. |
| Hoy, Ideario, Notas, Cartas | Objeto solo | Dentro de su escena |
| Biblioteca del diario, Libreta | Fondo propio | Dentro de su escena: las esquinas del tema |
| Sueños | Cielo con línea de contorno y un margen de 10 dp que se veía como marco | El cielo sin contorno y sin margen: el estante de noche ocupa todo y el cielo solo asoma donde el dibujo es transparente. Igual en la biblioteca de sueños. |
| Recuadros de Vida | Tarjeta con borde y el marco del mapa encima | El dibujo en su escena y el título debajo, sin tarjeta; el toque de siempre |
| Aura (`Modifier.halo`) | — | Sigue la silueta del objeto. Va en cada volumen de los estantes del diario y de los sueños, en el diario de hoy, en cada cajón del Ideario y en cada sobre. |

### Cómo es el aura

- **Cómo se dibuja:** el objeto se vuelve a dibujar teñido del acento del tema (`primary`; de noche, un 18 % más claro). Las copias se corren alrededor:
  - en reposo, 8 direcciones y 2 anillos, con alcance de 2,5 dp y una opacidad total de 0,16;
  - encendida, 16 direcciones y 4 anillos, con alcance de 7 dp y una opacidad total de 0,66.
- **Encima** va el objeto tal cual, así que el aura solo se ve por fuera.
- **Cuándo se enciende:** mientras hay un dedo encima (al tocar o al deslizar) o un puntero. Tarda 140 ms en encenderse y 520 ms en apagarse; sin animaciones, cambia de golpe.
- **No quita el toque:** solo mira los toques y no los consume, así que cada objeto sigue abriéndose como antes.

### Sin aura, porque el mueble entero es un botón

- los recuadros de Vida;
- el tablero de notas (cualquier toque abre Notas);
- la libreta de momentos. Su dibujo trae pared, soporte y libreta juntos, y todo abre la libreta. Para que solo la libreta tenga aura habría que marcar dónde queda en cada una de las 35 libretas.

## Verificación

- **Arnés de escritorio:** aura en reposo y encendida sobre notas e íconos de Al-Ándalus y Mesoamérica, en claro y en oscuro. Encendida con 12 direcciones se veían escalones en las siluetas con picos; con 16 quedó lisa.
- **Compilación:** APK 186 y 187, y capturas 251 y 252, sin errores.
- **Capturas 252 (`vida`):** revisé los 34 temas y Básico, en claro y en oscuro, en Diario y en Vida.
  - Todo quedó con su fondo y sin contorno.
  - Encontré que el cielo de los sueños se veía como un marco en Básico (claro), Romano, Egipto, Espacio y Laboratorio. Se arregló quitando el margen del cielo; está en el commit de este informe.
- **El aura encendida no sale en las capturas**, porque Paparazzi no toca la pantalla. Falta verla en el teléfono.

## Riesgos

- **Costo de dibujo:** cada objeto con aura se dibuja de 17 a 65 veces (el objeto más sus copias), según esté en reposo o encendido. Un estante con nueve volúmenes son unas 150 copias por cuadro en reposo; solo se redibuja cuando algo cambia, no al desplazar la pantalla. Falta medirlo en el teléfono.
- **Volúmenes pegados:** el aura de un volumen se asoma un poco sobre el de al lado. En reposo casi no se nota.
