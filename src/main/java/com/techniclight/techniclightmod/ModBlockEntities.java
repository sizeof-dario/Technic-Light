package com.techniclight.techniclightmod;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlockEntities {
    // 1. Dichiarare la variabile del tipo di Block Entity
    public static final BlockEntityType<TransmitterModBlockEntity> TRANSMITTER_BLOCK_ENTITY =
		register("transmitter", TransmitterModBlockEntity::new, ModBlocks.TRANSMITTER);
    public static final BlockEntityType<TransmitterModBlockEntity> RECEIVER_BLOCK_ENTITY =
		register("receiver", TransmitterModBlockEntity::new, ModBlocks.RECEIVER);


private static <T extends BlockEntity> BlockEntityType<T> register(
		String name,
		FabricBlockEntityTypeBuilder.Factory<? extends T> entityFactory,
		Block... blocks
) {
	Identifier id = Identifier.fromNamespaceAndPath(TechnicLight.MOD_ID, name);
	return Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id, FabricBlockEntityTypeBuilder.<T>create(entityFactory, blocks).build());
}

    public static void initialize() {};
}
