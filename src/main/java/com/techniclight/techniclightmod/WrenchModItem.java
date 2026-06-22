package com.techniclight.techniclightmod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchModItem extends Item {

    public WrenchModItem(Properties properties) {
        super(properties);
    }

    @Override
	public InteractionResult useOn(final UseOnContext context) {
		Player player = context.getPlayer();
		Level level = context.getLevel();
		if (player instanceof ServerPlayer serverPlayer) {
			BlockPos pos = context.getClickedPos();
			if (!this.handleInteraction(serverPlayer, level.getBlockState(pos), level, pos, true, context.getItemInHand(), context)) {
				return InteractionResult.FAIL;
			}
		}

		return InteractionResult.SUCCESS;
	}

    private boolean handleInteraction(
    final ServerPlayer player, final BlockState state, final LevelAccessor level, final BlockPos pos, final boolean cycle, final ItemStack itemStackInHand, final UseOnContext context
) {
    BlockPos clickedPos = context.getClickedPos();
    


    // Verifichiamo se il blocco cliccato possiede il nostro BlockEntity
    BlockEntity be = level.getBlockEntity(clickedPos);

    if (be instanceof ReceiverModBlockEntity) {
        // 1. Accediamo ai dati personalizzati "volanti" dell'oggetto
        CustomData customData = itemStackInHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbt = customData.copyTag(); // Copiamo l'NBT interno (copyTag nei mapping Mojang)

        // CASO 1: L'oggetto non ha ancora salvato la Posizione A

            // Scriviamo le coordinate nell'NBT dell'oggetto
            nbt.putInt("SelectedX", clickedPos.getX());
            nbt.putInt("SelectedY", clickedPos.getY());
            nbt.putInt("SelectedZ", clickedPos.getZ());
            
            // Applichiamo i dati modificati all'oggetto in mano
            itemStackInHand.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
            
            if (player != null) {
                // Messaggio server-side usando i Component di Mojang
               // player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Receiver memorizzato."));
                level.playSound(
        null,                                      // Esclude un giocatore specifico (null = lo sentono tutti)
        clickedPos,                                // La posizione in cui si origina il suono
        net.minecraft.sounds.SoundEvents.ARROW_HIT_PLAYER, // Il suono scelto (puoi cambiarlo, es. BLOCK_NOTE_BLOCK_CHIME)
        net.minecraft.sounds.SoundSource.BLOCKS,   // La categoria del volume nelle opzioni di gioco
        1.0F,                                      // Volume (1.0 è il valore normale)
        1.0F                                       // Pitch/Tonalità (1.0 è il valore normale)
    );
            
            }
            return true;
        
    }

    if (be instanceof TransmitterModBlockEntity targetBlockEntity)
    {
        CustomData customData = itemStackInHand.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbt = customData.copyTag(); // Copiamo l'NBT interno (copyTag nei mapping Mojang)
        if (!nbt.contains("SelectedX"))
        {
            if (player != null) {
                // Messaggio server-side usando i Component di Mojang
                // player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Nessun receiver selezionato."));
            }
            return false;
        }
        else
        {// Ricostruiamo il BlockPos usando i tre interi estratti dall'NBT dell'oggetto
            // Estraiamo gli int dagli Optional (se getInt restituisce un Optional)
            int x = nbt.getInt("SelectedX").orElse(0);
            int y = nbt.getInt("SelectedY").orElse(0);
            int z = nbt.getInt("SelectedZ").orElse(0);

            // Ora passiamo gli int puliti al metodo containing
            BlockPos posA = BlockPos.containing(x, y, z);

            // Passiamo la posizione A al Blocco B
            targetBlockEntity.setCompanionPos(posA);
            
            // Rimuoviamo i dati per svuotare l'oggetto
            nbt.remove("SelectedX");
            nbt.remove("SelectedY");
            nbt.remove("SelectedZ");
            itemStackInHand.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

            if (player != null) {
                //player.sendSystemMessage(net.minecraft.network.chat.Component.literal("Collegamento creato."));


                level.playSound(
        null,                                      // Esclude un giocatore specifico (null = lo sentono tutti)
        clickedPos,                                // La posizione in cui si origina il suono
        net.minecraft.sounds.SoundEvents.PLAYER_LEVELUP, // Il suono scelto (puoi cambiarlo, es. BLOCK_NOTE_BLOCK_CHIME)
        net.minecraft.sounds.SoundSource.BLOCKS,   // La categoria del volume nelle opzioni di gioco
        1.0F,                                      // Volume (1.0 è il valore normale)
        1.0F                                       // Pitch/Tonalità (1.0 è il valore normale)
    );
            }
            return true;
        }
    }

    
    return false; // Ritorna false se l'interazione non era con il tuo blocco
}
    
}
