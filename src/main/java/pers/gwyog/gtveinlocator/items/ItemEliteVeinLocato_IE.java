package pers.gwyog.gtveinlocator.items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import blusunrize.immersiveengineering.api.DimensionChunkCoords;
import blusunrize.immersiveengineering.api.tool.ExcavatorHandler;
import blusunrize.immersiveengineering.api.tool.ExcavatorHandler.MineralMix;
import blusunrize.immersiveengineering.api.tool.ExcavatorHandler.MineralWorldInfo;
import ic2.api.item.ElectricItem;
import net.minecraft.block.Block;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import pers.gwyog.gtveinlocator.GTVeinLocator;
import pers.gwyog.gtveinlocator.compat.JourneyMapHelper;
import pers.gwyog.gtveinlocator.compat.LoadedModHelper;
import pers.gwyog.gtveinlocator.compat.XaeroMinimapHelper;
import pers.gwyog.gtveinlocator.compat.LoadedModHelper.SupportModsEnum;
import pers.gwyog.gtveinlocator.config.ModConfig;
import pers.gwyog.gtveinlocator.network.ClientInfoMessageTranslationPacket;
import pers.gwyog.gtveinlocator.network.ClientVeinNameTranslationPacket;
import pers.gwyog.gtveinlocator.network.ClientWaypointPacket;
import pers.gwyog.gtveinlocator.network.GTVLNetwork;
import pers.gwyog.gtveinlocator.util.ClientVeinNameHelper;
import pers.gwyog.gtveinlocator.util.GTOreLayerHelper;
import pers.gwyog.gtveinlocator.util.GTOreLayerHelper.WorldNameEnum;
import pers.gwyog.gtveinlocator.util.GTVeinNameHelper;

public class ItemEliteVeinLocato_IE extends ItemVeinLocator {

    public ItemEliteVeinLocato_IE(String name, double maxCharge, double transferLimit, int tier,
            boolean useEnergy) {
        super(name, maxCharge, transferLimit, tier, useEnergy);
    }

    protected void displayHelpString(ICommandSender commandSender, String display, Object... info) {
        commandSender.addChatMessage(new ChatComponentText(StatCollector.translateToLocalFormatted(display, info)).setChatStyle(new ChatStyle().setColor(EnumChatFormatting.GREEN)));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        int searchRange = getSearchRangeFromNBT(stack);
        if (player.isSneaking()) {
            if (!world.isRemote) {
                switchMode(stack, searchRange);
            } else {
                player.addChatMessage(new ChatComponentTranslation("chat.gtveinlocator.switch_range", 4 - searchRange, 4 - searchRange));
            }
        } else if (!player.isSneaking()) {
            if (useEnergy) {
                if (!ElectricItem.manager.use(stack, ModConfig.advancedVeinLocatorSingleUseCost * searchRange * searchRange, player)) {
                    return stack;
                }
            }
            if (!world.isRemote) {
                int indexX = (int) player.posX;
                int indexZ = (int) player.posZ;
                int count = 0;
                int dimId = player.dimension;
                int targetX, targetZ;
                String foundVeinNames = "";
                String s = "IEVein_empty";
                for (int i = (1 - searchRange) / 2; i < (1 + searchRange) / 2; i++) {
                    for (int j = (1 - searchRange) / 2; j < (1 + searchRange) / 2; j++) {
                        targetX = indexX + 16 * i;
                        targetZ = indexZ + 16 * j;
                        DimensionChunkCoords coords = new DimensionChunkCoords(player.getEntityWorld().provider.dimensionId, (targetX >> 4), (targetZ >> 4));
                        MineralWorldInfo info = ExcavatorHandler.getMineralWorldInfo(player.getEntityWorld(), coords.chunkXPos, coords.chunkZPos);
                        if (info != null) {
                            s = info.mineralOverride != null ? info.mineralOverride.name : info.mineral != null ? info.mineral.name : "IEVein_empty";
                        }
                        if (!s.equals("IEVein_empty")) {
                            count++;
                            GTVLNetwork.INSTANCE.sendTo(new ClientWaypointPacket(s, 1, coords.getCenterXPos(), 100, coords.getCenterZPosition(), dimId), (EntityPlayerMP) player);
                        } else {
                            GTVLNetwork.INSTANCE.sendTo(new ClientWaypointPacket(s, 1, coords.getCenterXPos(), 100, coords.getCenterZPosition(), dimId), (EntityPlayerMP) player);
                        }
                    }
                }
                GTVLNetwork.INSTANCE.sendTo(new ClientInfoMessageTranslationPacket(2, new int[]{count, searchRange, searchRange}), (EntityPlayerMP) player);
            }
        }
        return stack;
    }

}
