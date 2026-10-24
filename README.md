# Fortnite Spirits - Parte 1 (nucleo)  | Forge 1.20.1 + GeckoLib

1. Descarga el Forge MDK 1.20.1 (47.x) y copia `src/` de esta carpeta dentro del MDK.
2. En `build.gradle` del MDK agrega:

   repositories { maven { url = "https://dl.cloudsmith.io/public/geckolib3/geckolib/maven/" } }
   dependencies { implementation fg.deobf("software.bernie.geckolib:geckolib-forge-1.20.1:4.4.9") }

3. `./gradlew runClient` para probar. Items: /give @s fnspirits:spirit_tails_gold

Controles: pon el espiritu en la segunda mano (F).
- Tails: agachate en el aire mientras caes.
- Arbustin: agachate 2 segundos en el suelo.

Sin texturas/modelos todavia (salen cuadros morados): vienen en las partes 3, 4 y 7.
Pendiente: doble salto de Jack Rabbit (necesita paquete cliente-servidor), suerte al minar de Hacker.
