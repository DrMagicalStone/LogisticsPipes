package logisticspipes.network.abstractguis;

import javax.annotation.Nonnull;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import logisticspipes.network.abstractpackets.CoordinatesPacket;
import network.rs485.logisticspipes.util.LPDataInput;
import network.rs485.logisticspipes.util.LPDataOutput;
import network.rs485.logisticspipes.world.DoubleCoordinates;

@ToString
public abstract class CoordinatesPopupGuiProvider extends PopupGuiProvider {

	@Getter
	@Setter
	private int posX;
	@Getter
	@Setter
	private int posY;
	@Getter
	@Setter
	private int posZ;

	public CoordinatesPopupGuiProvider(int id) {
		super(id);
	}

	@Override
	public void writeData(LPDataOutput output) {

		output.writeInt(posX);
		output.writeInt(posY);
		output.writeInt(posZ);
	}

	@Override
	public void readData(LPDataInput input) {

		posX = input.readInt();
		posY = input.readInt();
		posZ = input.readInt();

	}

	public CoordinatesPopupGuiProvider setTilePos(TileEntity tile) {
		setPosX(tile.getPos().getX());
		setPosY(tile.getPos().getY());
		setPosZ(tile.getPos().getZ());
		return this;
	}

	public CoordinatesPopupGuiProvider setLPPos(DoubleCoordinates pos) {
		setPosX(pos.getXInt());
		setPosY(pos.getYInt());
		setPosZ(pos.getZInt());
		return this;
	}

	@Nonnull
	public <T> T getTileAs(World world, Class<T> clazz) {
		return CoordinatesPacket.getTileAs(this, world, new BlockPos(getPosX(), getPosY(), getPosZ()), clazz);
	}

}
