package stevekung.mods.moreplanets.utils.items.armor;

import javax.annotation.Nullable;

import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import stevekung.mods.moreplanets.core.MorePlanetsMod;
import stevekung.mods.moreplanets.integration.jei.MPJEIRecipes;
import stevekung.mods.moreplanets.utils.client.renderer.IItemModelRender;
import stevekung.mods.moreplanets.utils.itemblocks.IItemRarity;
import stevekung.mods.moreplanets.utils.items.EnumSortCategoryItem;
import stevekung.mods.moreplanets.utils.items.ISortableItem;
import stevekung.mods.stevekunglib.utils.ColorUtils;

public abstract class ItemArmorMP extends ItemArmor implements ISortableItem, IItemModelRender
{
    private String name;

    public ItemArmorMP(ArmorMaterial material, EntityEquipmentSlot type)
    {
        super(material, -1, type);
    }

    @Override
    public Item setUnlocalizedName(String name)
    {
        this.name = name;
        MPJEIRecipes.collectAnvilList(name, this, this.getRepairItem());
        return super.setUnlocalizedName(name);
    }

    @Override
    public CreativeTabs getCreativeTab()
    {
        return MorePlanetsMod.ITEM_TAB;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
    {
        if (this.getRepairItem() == null)
        {
            return false;
        }
        if (repair.getItem() == this.getRepairItem())
        {
            return true;
        }
        return false;
    }

    @Override
    public EnumSortCategoryItem getItemCategory()
    {
        switch (this.armorType)
        {
        case HEAD:
        default:
            return EnumSortCategoryItem.HELMET;
        case CHEST:
            return EnumSortCategoryItem.CHESTPLATE;
        case LEGS:
            return EnumSortCategoryItem.LEGGINGS;
        case FEET:
            return EnumSortCategoryItem.BOOTS;
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nullable
    public FontRenderer getFontRenderer(ItemStack itemStack)
    {
        return ColorUtils.coloredFontRenderer;
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemStack)
    {
        return this instanceof IItemRarity && ((IItemRarity)this).getRarity() != null ? ((IItemRarity)this).getRarity().toColoredFont() + super.getItemStackDisplayName(itemStack) : super.getItemStackDisplayName(itemStack);
    }

    @Override
    public String getHighlightTip(ItemStack itemStack, String displayName)
    {
        return super.getItemStackDisplayName(itemStack);
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public EnumRarity getRarity(ItemStack itemStack)
    {
        return ClientProxyCore.galacticraftItem;
    }

    protected abstract Item getRepairItem();
}