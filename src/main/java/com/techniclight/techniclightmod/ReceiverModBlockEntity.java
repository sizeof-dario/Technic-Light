package com.techniclight.techniclightmod;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ReceiverModBlockEntity extends BlockEntity
{
    private BlockPos companionPos = null;

    public ReceiverModBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        super(ModBlockEntities.RECEIVER_BLOCK_ENTITY, worldPosition, blockState);
    }
    
    public void setCompanionPos(BlockPos pos)
    {
        this.companionPos = pos;
        this.setChanged();
    }

    public BlockPos getCompanionPos()
    {
        return this.companionPos;
    }

    @Override
protected void loadAdditional(final ValueInput input) {
    super.loadAdditional(input);
    
    // Com.mojang.serialization.Codec ha un codec nativo per gli interi: Codec.INT
    // .read(chiave, codec) restituisce un Optional<Integer>. 
    // Usiamo .orElse(null) per verificare se esistono.
    Integer x = input.read("LinkX", com.mojang.serialization.Codec.INT).orElse(null);
    Integer y = input.read("LinkY", com.mojang.serialization.Codec.INT).orElse(null);
    Integer z = input.read("LinkZ", com.mojang.serialization.Codec.INT).orElse(null);

    if (x != null && y != null && z != null) {
        // Ora x, y, z sono Integer reali (non Optional), quindi il costruttore funziona!
        this.companionPos = new BlockPos(x, y, z);
    } else {
        this.companionPos = null;
    }
}

@Override
protected void saveAdditional(final ValueOutput output) {
    super.saveAdditional(output);
    
    if (this.companionPos != null) {
        // Usiamo il .store che sai già funzionare sulla tua versione!
        output.store("LinkX", com.mojang.serialization.Codec.INT, this.companionPos.getX());
        output.store("LinkY", com.mojang.serialization.Codec.INT, this.companionPos.getY());
        output.store("LinkZ", com.mojang.serialization.Codec.INT, this.companionPos.getZ());
    }
}
    
}



