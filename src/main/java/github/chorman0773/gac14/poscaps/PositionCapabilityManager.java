package github.chorman0773.gac14.poscaps;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;

public class PositionCapabilityManager implements ICapabilitySerializable<ListNBT> {
	private final Map<BlockPos,PositionCapabilityHolder> holder = new HashMap<>();
	private final World w;
	private final Chunk c;
	
	@CapabilityInject(PositionCapabilityManager.class)
	private static final Capability<PositionCapabilityManager> POSCAPMAN_CAP = null;
	
	
	/**
	 * Obtains an object for the capabilitiy designated by cap in the proxy object given the World Position. 
	 * If the chunk the block is in has been unsubscribed from Positional Capabilities, returns an empty optional.
	 * Otherwise, if this is the first time in the world that a given position has been referenced by this method,
	 *  a new proxy object is created. This will cause handlers for {@link AttachCapabilities}&lt;PositionCapabilityHolder&gt; to fire.
	 * After obtaining the proxy object p, and creating it if necessary, returns p.getCapability(cap,side).
	 */
	public static <T> LazyOptional<T> getBlockCapability(World w,BlockPos pos,Capability<T> cap,@Nullable Direction side){
		Chunk c = w.getChunkAt(pos);
		return c.getCapability(POSCAPMAN_CAP).map(p->p.getOrCreate(pos)).map(h->h.getCapability(cap,side)).orElseGet(LazyOptional::empty);
	}
	
	/**
	 * Unsided version of {@link #getBlockCapability(World, BlockPos, Capability, Direction)}.
	 * The call getBlockCapability(w,pos,cap) is equivalent to the call getBlockCapability(w,pos,cap,null).
	 */
	public static <T> LazyOptional<T> getBlockCapability(World w,BlockPos pos,Capability<T> cap){
		return getBlockCapability(w,pos,cap,null);
	}
	
	PositionCapabilityManager(World w,Chunk c) {
		this.w = w;
		this.c = c;
	}
	
	private PositionCapabilityHolder getOrCreate(BlockPos pos) {
		if(holder.containsKey(pos))
			return holder.get(pos);
		else {
			PositionCapabilityHolder holder = new PositionCapabilityHolder(w,pos);
			this.holder.put(pos,holder);
			return holder;
		}
	}
	
	private static CompoundNBT serializeEntry(Map.Entry<BlockPos, PositionCapabilityHolder> entry) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.put("__pos", NBTUtil.writeBlockPos(entry.getKey()));
		nbt.put("__holder", entry.getValue().serializeNBT());
		return nbt;
	}

	@Override
	public ListNBT serializeNBT() {
		return holder.entrySet().stream().map(PositionCapabilityManager::serializeEntry).filter(CompoundNBT::isEmpty).collect(ListNBT::new, ListNBT::add, ListNBT::addAll);
	}

	@Override
	public void deserializeNBT(ListNBT nbt) {
		if(nbt.getTagType()!=NBT.TAG_COMPOUND)
			throw new IllegalArgumentException("Must be a list of compounds");
		for(INBT n:nbt) {
			CompoundNBT comp = (CompoundNBT)n;
			BlockPos pos = NBTUtil.readBlockPos(comp.getCompound("__pos"));
			PositionCapabilityHolder capHolder = getOrCreate(pos);
			capHolder.deserializeNBT(comp);
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
		return POSCAPMAN_CAP.orEmpty(cap, LazyOptional.of(()->this));
	}


}
