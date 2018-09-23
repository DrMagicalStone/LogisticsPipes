package logisticspipes.proxy.specialinventoryhandler;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import logisticspipes.utils.item.ItemIdentifier;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraft.util.EnumFacing;

import mcp.mobius.betterbarrels.common.blocks.IBarrelStorage;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;

public class JABBAInventoryHandler extends SpecialInventoryHandler {

	private final TileEntityBarrel _tile;
	private final IBarrelStorage _storage;
	private final boolean _hideOnePerStack;

	private JABBAInventoryHandler(TileEntity tile, boolean hideOnePerStack, boolean hideOne, int cropStart, int cropEnd) {
		_tile = (TileEntityBarrel) tile;
		_storage = _tile.getStorage();
		_hideOnePerStack = hideOnePerStack || hideOne;
	}

	public JABBAInventoryHandler() {
		_tile = null;
		_storage = null;
		_hideOnePerStack = false;
	}

	@Override
	public boolean init() {
		return true;
	}

	@Override
	public boolean isType(TileEntity tile, EnumFacing dir) {
		return tile instanceof TileEntityBarrel;
	}

	@Override
	public SpecialInventoryHandler getUtilForTile(TileEntity tile, EnumFacing dir, boolean hideOnePerStack, boolean hideOne, int cropStart, int cropEnd) {
		return new JABBAInventoryHandler(tile, hideOnePerStack, hideOne, cropStart, cropEnd);
	}

	@Override
	public int itemCount(ItemIdentifier itemIdent) {
		ItemStack items = _storage.getStoredItemType();
		if (items != null && ItemIdentifier.get(items).equals(itemIdent)) {
			return (_storage.isCreative() ? (int) (Math.pow(2, 20)) : (items.getCount() - (_hideOnePerStack ? 1 : 0)));
		}
		return 0;
	}

	@Override
	public ItemStack getMultipleItems(ItemIdentifier itemIdent, int count) {
		ItemStack items = _storage.getStoredItemType();
		if (items == null || !ItemIdentifier.get(items).equals(itemIdent)) {
			return null;
		}
		if (_storage.isCreative()) {
			return itemIdent.makeNormalStack(count);
		}
		if (_hideOnePerStack) {
			items.shrink(1);
		}
		if (count >= items.getCount()) {
			_storage.setStoredItemCount((_hideOnePerStack ? 1 : 0));
			_tile.markDirty();
			return items;
		}
		ItemStack newItems = items.splitStack(count);
		_storage.setStoredItemCount(items.getCount() + (_hideOnePerStack ? 1 : 0));
		_tile.markDirty();
		return newItems;

	}

	@Override
	public Set<ItemIdentifier> getItems() {
		Set<ItemIdentifier> result = new TreeSet<>();
		ItemStack items = _storage.getStoredItemType();
		if (items != null) {
			result.add(ItemIdentifier.get(items));
		}
		return result;
	}

	@Override
	public HashMap<ItemIdentifier, Integer> getItemsAndCount() {
		HashMap<ItemIdentifier, Integer> result = new HashMap<>();
		ItemStack items = _storage.getStoredItemType();
		if (items != null && items.getCount() > 0) {
			result.put(ItemIdentifier.get(items), _storage.isCreative() ? (int) (Math.pow(2, 20)) : items.getCount() - (_hideOnePerStack ? 1 : 0));
		}
		return result;
	}

	@Override
	public ItemStack getSingleItem(ItemIdentifier itemIdent) {
		return getMultipleItems(itemIdent, 1);
	}

	@Override
	public boolean containsUndamagedItem(ItemIdentifier itemIdent) {
		ItemStack items = _storage.getStoredItemType();
		if (items != null && ItemIdentifier.get(items).getUndamaged().equals(itemIdent)) {
			return true;
		}
		return false;
	}

	@Override
	public int roomForItem(ItemIdentifier item) {
		return roomForItem(item, 0);
	}

	@Override
	public int roomForItem(ItemIdentifier itemIdent, int count) {
		if (itemIdent.tag != null) {
			return 0;
		}
		ItemStack items = _storage.getStoredItemType();
		if (items == null) {
			return _storage.getMaxStoredCount();
		}
		if (_storage.sameItem(itemIdent.makeNormalStack(1))) {
			if (_storage.isVoid()) {
				return _storage.getMaxStoredCount();
			} else {
				return _storage.getMaxStoredCount() - items.getCount();
			}
		}
		return 0;
	}

	@Override
	public ItemStack add(ItemStack stack, EnumFacing from, boolean doAdd) {
		ItemStack st = stack.copy();
		st.setCount(0);
		if (stack.getTagCompound() != null) {
			return st;
		}
		ItemStack items = _storage.getStoredItemType();
		if ((items == null || items.getCount() == 0)) {
			if (stack.getCount() <= _storage.getMaxStoredCount()) {
				_storage.setStoredItemType(stack, stack.getCount());
				st.setCount(stack.getCount());
				_tile.markDirty();
				return st;
			} else {
				_storage.setStoredItemType(stack, _storage.getMaxStoredCount());
				st.setCount(_storage.getMaxStoredCount());
				_tile.markDirty();
				return st;
			}
		}
		if (!_storage.sameItem(stack)) {
			return st;
		}
		if (stack.getCount() <= _storage.getMaxStoredCount() - items.getCount()) {
			_storage.setStoredItemCount(items.getCount() + stack.getCount());
			st.setCount(stack.getCount());
			_tile.markDirty();
			return st;
		} else {
			_storage.setStoredItemCount(_storage.getMaxStoredCount());
			if (!_storage.isVoid()) {
				st.setCount(_storage.getMaxStoredCount() - items.getCount());
			} else {
				st.setCount(stack.getCount());
			}
			_tile.markDirty();
			return st;
		}
	}

	@Override
	public boolean isSpecialInventory() {
		return true;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		if (i != 0) {
			return null;
		}
		return _storage.getStoredItemType();
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (i != 0) {
			return null;
		}
		return getMultipleItems(ItemIdentifier.get(_storage.getStoredItemType()), j);
	}
}
