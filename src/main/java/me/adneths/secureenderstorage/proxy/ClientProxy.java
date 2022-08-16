package me.adneths.secureenderstorage.proxy;

import me.adneths.secureenderstorage.init.ModContents;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit() {
		super.preInit();
	}

	@Override
	public void init() {
		super.init();
		ModContents.registerModels();
	}

	@Override
	public void postInit() {
		super.postInit();
	}
	
}
