package com.techniclight.techniclightmod;

import org.jspecify.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class TransmitterModBlockEntity extends BlockEntity implements ModImplementedContainer, MenuProvider
{
    private BlockPos companionPos = null;
    private final NonNullList<ItemStack> items = NonNullList.withSize(1, ItemStack.EMPTY);

    public TransmitterModBlockEntity(BlockPos worldPosition, BlockState blockState)
    {
        super(ModBlockEntities.TRANSMITTER_BLOCK_ENTITY, worldPosition, blockState);
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

	ContainerHelper.loadAllItems(input, this.items);

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

    ContainerHelper.saveAllItems(output, this.items);
}

    @Override
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }
    
    @Override
public boolean stillValid(Player player) {
	return Container.stillValidBlockEntity(this, player);
}

   @Override
public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
	return new TransmitterModBlockMenu(containerId, inventory, this);
}

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.technic-light.transmitter");
    }
    
    @Override
    public boolean canPlaceItem(int slot, ItemStack itemStack) {
    return itemStack.is(Items.REDSTONE);
}

}



