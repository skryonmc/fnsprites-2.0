# Fortnite Spirits - Parte 3 (entidades errantes + modelos 3D GeckoLib)

Copia `src/` encima de las partes anteriores. SE REEMPLAZAN: `SpiritsMod.java` y los archivos de `lang/`
(el lang ya trae todas las claves de la Parte 1 mas la entidad).

Incluye:
- Entidad `fnspirits:spirit` (GeckoLib): vaga despacio, mira al jugador, es inmune a mobs. Clic derecho = lo recoges como item
  (con su nivel si venia de un item). Los silvestres desaparecen a los 5 minutos.
- 13 modelos 3D (uno por espiritu, con accesorios: orejas/colas de Tails, pinchos de Sonic/Shadow, nube de Tormenta, corona, lentes, ojos de rana...)
  y 39 texturas (normal / verde / dorado). Animaciones idle y walk. Verdes y dorados llevan sus particulas tambien como entidad.
- Aparicion natural rara en el Overworld (biome modifier). Solo salen tipos comunes/raros de la tabla de cofres (no Corona/Hacker/Klombo).
- Sonidos provisionales (Allay con tono distinto por espiritu). Sonidos propios (.ogg) van en la parte de assets.

Prueba:
/summon fnspirits:spirit ~ ~ ~ {SpiritType:"tails",SpiritVariant:"gold"}
tipos: tails sonic shadow storm jonesy corona hacker klombo jack_rabbit arbustin aventurero bits8 kill_switch
variantes: normal green gold

IMPORTANTE: los modelos son versiones base procedurales (cubos + accesorios), no copias exactas del diseno de Fortnite.
Para el diseno identico, abre cualquier `spirit_<tipo>.geo.json` en Blockbench (plugin GeckoLib) y editalo/retexturizalo;
los nombres de huesos (body, leg_l, leg_r, arm_l, arm_r, acc) deben mantenerse para que funcionen las animaciones.
