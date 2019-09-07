package github.chorman0773.gac14.poscaps;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod("gac14-poscaps")
public class Gac14PosCaps {
	
	public Gac14PosCaps() {
		MinecraftForge.EVENT_BUS.addListener(this::init);
		MinecraftForge.EVENT_BUS.addGenericListener(Chunk.class, this::attachToChunk);
	}
	
	private void init(FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(PositionCapabilityManager.class, new IStorage<PositionCapabilityManager>() {

			@Override
			public INBT writeNBT(Capability<PositionCapabilityManager> capability, PositionCapabilityManager instance,
					Direction side) {
				// TODO Auto-generated method stub
				return instance.serializeNBT();
			}

			@Override
			public void readNBT(Capability<PositionCapabilityManager> capability, PositionCapabilityManager instance,
					Direction side, INBT nbt) {
				instance.deserializeNBT((ListNBT)nbt);
			}
			
		}, ()->null);
	}
	
	private void attachToChunk(AttachCapabilitiesEvent<Chunk> attach) {
		Chunk c = attach.getObject();
		World w = c.getWorld();
		attach.addCapability(new ResourceLocation("gac14:__poscaps/manager_cap"), new PositionCapabilityManager(w,c));
	}
}
