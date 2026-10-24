# Fortnite Spirits - Parte 6 (cofres, mobs, obtencion especifica, zonas de codigo)

Copia `src/` encima de las partes anteriores. SE REEMPLAZAN: loot/SpiritChestModifier.java (Parte 2), entity/SpiritEntity.java (Parte 3, +setWild),
entity/SpiritSpawner.java (Parte 5, +setWild), registry/ModBlocks, ModItems, ModFeatures, el tag pickaxe y los lang.

## Cofres
Al abrir un cofre generado (6%, mas con Hacker) el espiritu SALE del cofre: particulas, sonido y salto; los verdes y dorados suenan/lucen distinto.

## Mobs
Matar hostiles: Zombie 1.5%, otros hostiles 0.8%, Warden/Elder Guardian 30% (rareza +2), Wither/Dragon 100% (rareza +3). El Hacker sube la probabilidad.
50% de las veces sale el espiritu "tematico" del mob (esqueleto=Kill Switch, creeper=Tormenta, enderman=Shadow, bruja=Arbustin, arana=Jack Rabbit, phantom=Tails...).
Constantes en `ability/MobDropHandler.java`.

## Obtencion especifica (una vez por jugador, aparece como entidad cerca)
- Klombo: comer 500 alimentos. Aventurero: minar 150 menas. Tormenta: que te caiga un rayo. Kill Switch: matar un esqueleto con flecha desde 30+ bloques.
  Metas en `ability/SpecialObtainHandler.java`. (Corona y Hacker siguen con su cadena de las partes 1 y 5.)

## Zonas de codigo (mundo, raras)
Plataforma oscura con un Terminal y 4 paneles de flechas. Clic derecho al terminal: muestra 3 s un codigo de 4 flechas (se reinicia el progreso);
pulsa los paneles en ese orden. Acierto = sale un espiritu con rareza mejorada (+2, mas con Hacker); fallo = reinicio. Se bloquea 40 min y cambia el codigo.
Frecuencia en `placed_feature/code_zone.json` ("chance": 220). Prueba: /place feature fnspirits:code_zone
Los espiritus que salen (cofre, mob, zona) desaparecen a los 5 min si nadie los recoge.
