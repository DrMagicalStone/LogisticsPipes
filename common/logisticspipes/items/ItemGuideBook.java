package logisticspipes.items;

import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

import logisticspipes.LPItems;
import logisticspipes.gui.GuiGuideBook;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.packets.SetCurrentPagePacket;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.GuideBookContents;

public class ItemGuideBook extends LogisticsItem {

	public ItemGuideBook() {
		this.maxStackSize = 1;
	}

	public static void setCurrentPage(ItemStack stack, int pageIndex, int chapter, float sliderProgress, EnumHand hand) {
		if (!stack.isEmpty() && stack.getItem() == LPItems.itemGuideBook) {
			final NBTTagCompound tag = stack.hasTagCompound() ? Objects.requireNonNull(stack.getTagCompound()) : new NBTTagCompound();
			tag.setInteger("page", pageIndex);
			tag.setInteger("chapter", chapter);
			tag.setFloat("sliderProgress", sliderProgress);
			MainProxy.sendPacketToServer(PacketHandler.getPacket(SetCurrentPagePacket.class)
					.setPage(pageIndex).setChapter(chapter).setSliderProgress(sliderProgress).setHand(hand));
		}
	}

	@Nonnull
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, @Nonnull EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		if (!world.isRemote) {
			if (openGuideBook(hand) != null) return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
		}
		return new ActionResult<>(EnumActionResult.PASS, itemstack);
	}

	@Nullable
	public static GuideBookContents openGuideBook(@Nonnull EnumHand hand) {
		Minecraft mc = Minecraft.getMinecraft();
		GuideBookContents gbc = GuideBookContents.load();
		if (gbc != null) {
			mc.displayGuiScreen(new GuiGuideBook(hand, gbc));
		}
		return gbc;
	}
}
