# Fortnite Spirits - Parte 2 (variantes, doble salto, loot)

Copia `src/` encima del proyecto de la Parte 1. **SpiritsMod.java se reemplaza** (registra red, particulas y loot).

Incluye:
- Verdes: numeros que caen y cambian (particula `fnspirits:digit`). Dorados: destellos que orbitan (`fnspirits:shine`).
  Se ven en el espiritu equipado en la espalda y en los espiritus tirados en el suelo.
- Jack Rabbit: doble salto (paquete cliente->servidor). Se recupera al tocar el suelo y respeta el cooldown.
- Hacker: probabilidad de duplicar el botin de menas (10% por nivel, no con Toque de seda) y mas probabilidad/rareza de espiritus en cofres.
- Espiritus en cofres: 6% por cofre. Corona, Hacker y Klombo no salen de cofres normales.

Prueba: /give @s fnspirits:spirit_sonic_green (ponlo en la segunda mano y mira tu espalda), o abre cofres de mazmorra.
Las texturas de particulas son temporales (digitos y estrellas de 8x8): puedes cambiarlas por las tuyas.
