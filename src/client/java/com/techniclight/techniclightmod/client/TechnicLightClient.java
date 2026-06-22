package com.techniclight.techniclightmod.client;

import com.techniclight.techniclightmod.ModMenuType;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;

public class TechnicLightClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		MenuScreens.register(ModMenuType.TRANSMITTER, TransmitterScreen::new);
	}
}