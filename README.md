# Fortnite Spirits - Parte 4 (espiritu en la espalda + iconos de items)

Copia `src/` encima de las partes anteriores (no reemplaza nada).

- Espiritu en la segunda mano -> aparece en tu espalda (y en la de otros jugadores), con el MISMO modelo 3D y animaciones de la Parte 3:
  idle cuando estas quieto, walk cuando caminas, "pop" de aparicion al equiparlo, se baja un poco al agacharte.
  Las particulas de variantes (numeros verdes / brillo dorado) ya salen ahi desde la Parte 2.
- Los items tienen icono y modelo (39 PNG 16x16 procedurales: ya no salen cuadros morados). Son provisionales: cambialos por tus pixel arts.
- El item de la segunda mano se oculta en la mano (display scale 0 en "lefthand"), para que solo se vea en la espalda.
  En la mano principal si se ve. Jugadores zurdos: cambia lefthand por righthand en los modelos de items.

Ajustes rapidos en `BackSpiritLayer.java`: tamano (0.55f), altura (-0.75f) y distancia a la espalda (0.27f).
No se renderiza en primera persona (el modelo del jugador no se dibuja ahi).
