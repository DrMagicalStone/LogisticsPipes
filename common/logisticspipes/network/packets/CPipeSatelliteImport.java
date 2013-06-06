package logisticspipes.network.packets;

import logisticspipes.logic.BaseLogicCrafting;
import logisticspipes.network.packets.abstracts.CoordinatesPacket;
import logisticspipes.network.packets.abstracts.ModernPacket;
import net.minecraft.entity.player.EntityPlayer;
import buildcraft.transport.TileGenericPipe;

public class CPipeSatelliteImport extends CoordinatesPacket {

	public CPipeSatelliteImport(int id) {
		super(id);
	}

	@Override
	public ModernPacket template() {
		return new CPipeSatelliteImport(getID());
	}

	@Override
	public void processPacket(EntityPlayer player) {
		final TileGenericPipe pipe = getPipe(player.worldObj);
		if (pipe == null) {
			return;
		}

		if (!(pipe.pipe.logic instanceof BaseLogicCrafting)) {
			return;
		}

		((BaseLogicCrafting) pipe.pipe.logic).importFromCraftingTable(player);
	}
}