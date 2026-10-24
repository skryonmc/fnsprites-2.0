package com.fnspirits.registry;

import com.fnspirits.SpiritsMod;
import com.fnspirits.block.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SpiritsMod.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, SpiritsMod.MODID);

    public static final RegistryObject<Block> EXTRACTION_BOX = BLOCKS.register("extraction_box", ExtractionBoxBlock::new);
    public static final RegistryObject<Block> PEDESTAL = BLOCKS.register("spirit_pedestal", PedestalBlock::new);
    public static final RegistryObject<Block> CODE_TERMINAL = BLOCKS.register("code_terminal", CodeTerminalBlock::new);
    public static final RegistryObject<Block> ARROW_PAD = BLOCKS.register("arrow_pad", ArrowPadBlock::new);

    public static final RegistryObject<BlockEntityType<ExtractionBoxBlockEntity>> EXTRACTION_BOX_BE = BLOCK_ENTITIES.register("extraction_box",
            () -> BlockEntityType.Builder.of(ExtractionBoxBlockEntity::new, EXTRACTION_BOX.get()).build(null));
    public static final RegistryObject<BlockEntityType<PedestalBlockEntity>> PEDESTAL_BE = BLOCK_ENTITIES.register("spirit_pedestal",
            () -> BlockEntityType.Builder.of(PedestalBlockEntity::new, PEDESTAL.get()).build(null));
    public static final RegistryObject<BlockEntityType<CodeTerminalBlockEntity>> CODE_TERMINAL_BE = BLOCK_ENTITIES.register("code_terminal",
            () -> BlockEntityType.Builder.of(CodeTerminalBlockEntity::new, CODE_TERMINAL.get()).build(null));

    private ModBlocks() {}
}
