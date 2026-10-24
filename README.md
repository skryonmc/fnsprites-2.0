# Fortnite Spirits - Parte 5 (santuarios, cajas de extraccion, caja portatil, pedestal y album)

Copia `src/` encima de las partes anteriores. SE REEMPLAZAN: SpiritsMod.java, registry/ModItems.java, network/ModNetwork.java y los lang.

## Que hay
- **Santuario** (generacion de mundo, raro, Overworld): plataforma con 4 Cajas de extraccion, un Pedestal central y 4 pilares con faroles.
  Frecuencia: `data/fnspirits/worldgen/placed_feature/shrine.json` -> `"chance": 150` (menor = mas comun).
- **Caja de extraccion**: cargada (brilla y suelta particulas) -> clic derecho con la mano vacia y sale un espiritu como entidad (con efectos y sonido;
  los dorados/verdes tienen mas efectos). Se recarga en 15 min (ExtractionBoxBlock.COOLDOWN_TICKS). Las cajas que pones tu empiezan descargadas.
  Con un espiritu en la mano: clic derecho DOS veces en 5s lo EXTRAE (se consume, da XP). Corona nv5 -> Corona dorada; Corona dorada nv5 -> Hacker.
- **Caja portatil**: mantén clic derecho 2s y extrae un espiritu al azar delante de ti (se consume). Receta: oro, amatista, ojo de ender, hierro.
- **Pedestal**: clic con un espiritu y se ve con su modelo 3D animado girando (clic para recogerlo / intercambiar). Receta con ladrillos y amatista.
- **Album**: clic derecho abre una pantalla con los 39 espiritus (13 tipos x 3 variantes); los que no has tenido salen en silueta.
  Se registra solo al llevar el espiritu en el inventario.
- Los bloques se rompen con pico y se recogen.

Prueba: /give @s fnspirits:extraction_box , /give @s fnspirits:spirit_album , /give @s fnspirits:portable_extraction_box
Para ver un santuario rapido: /place feature fnspirits:shrine
