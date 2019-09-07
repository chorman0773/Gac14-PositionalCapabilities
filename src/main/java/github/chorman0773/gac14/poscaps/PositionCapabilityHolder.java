package github.chorman0773.gac14.poscaps;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public class PositionCapabilityHolder extends CapabilityProvider<PositionCapabilityHolder> implements INBTSerializable<CompoundNBT> {
	private World w;
	private BlockPos pos;
	private Chunk c;
	
	PositionCapabilityHolder(World w,BlockPos pos) {
		super(PositionCapabilityHolder.class);
		c = w.getChunkAt(pos);
		this.gatherCapabilities();
	}


	@Override
	public CompoundNBT serializeNBT() {
		// TODO Auto-generated method stub
		return this.serializeCaps();
	}


	@Override
	public void deserializeNBT(CompoundNBT nbt) {
		this.deserializeCaps(nbt);
	}
	


}
