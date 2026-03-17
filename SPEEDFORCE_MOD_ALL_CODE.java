package com.example.speedforce;

import com.example.speedforce.block.ModBlocks;
import com.example.speedforce.block.entity.ModBlockEntities;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.command.SpeedForceCommand;
import com.example.speedforce.entity.ModEntityTypes;
import com.example.speedforce.item.ModCreativeTabs;
import com.example.speedforce.item.ModItems;
import com.example.speedforce.particle.ModParticles;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@Mod(SpeedForceMod.MOD_ID)
public class SpeedForceMod {
    public static final String MOD_ID = "speedforce";

    public SpeedForceMod(IEventBus modEventBus) {
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModCreativeTabs.CREATIVE_MODE_TABS.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModEntityTypes.ENTITY_TYPES.register(modEventBus);

        NeoForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void registerCommands(RegisterCommandsEvent event) {
        SpeedForceCommand.register(event.getDispatcher());
    }
}
package com.example.speedforce.item;

public enum SuitType {
    FLASH("flash", 255, 210, 0, 4),
    REVERSE_FLASH("reverse_flash", 255, 0, 0, 5),
    ZOOM("zoom", 0, 150, 255, 6),
    FLASH_S4("flash_s4", 255, 210, 0, 4),
    FLASH_S5("flash_s5", 255, 210, 0, 4),
    KID_FLASH("kid_flash", 255, 200, 0, 4),
    GREEN_ARROW("green_arrow", 0, 180, 0, 0);

    private final String name;
    private final int trailColorR;
    private final int trailColorG;
    private final int trailColorB;
    private final int speedBonus;

    SuitType(String name, int trailColorR, int trailColorG, int trailColorB, int speedBonus) {
        this.name = name;
        this.trailColorR = trailColorR;
        this.trailColorG = trailColorG;
        this.trailColorB = trailColorB;
        this.speedBonus = speedBonus;
    }

    public String getName() {
        return name;
    }

    public int getTrailColorR() {
        return trailColorR;
    }

    public int getTrailColorG() {
        return trailColorG;
    }

    public int getTrailColorB() {
        return trailColorB;
    }

    public int getSpeedBonus() {
        return speedBonus;
    }
}
package com.example.speedforce.item;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(SpeedForceMod.MOD_ID);

    public static final DeferredItem<BlockItem> PARTICLE_ACCELERATOR = ITEMS.register("particle_accelerator",
        () -> new BlockItem(ModBlocks.PARTICLE_ACCELERATOR.get(), new net.minecraft.world.item.Item.Properties()));

    private static Holder<ArmorMaterial> createArmorMaterial(String name) {
        return Holder.direct(new ArmorMaterial(
            new EnumMap<>(ArmorItem.Type.class) {{
                put(ArmorItem.Type.HELMET, 2);
                put(ArmorItem.Type.CHESTPLATE, 5);
                put(ArmorItem.Type.LEGGINGS, 6);
                put(ArmorItem.Type.BOOTS, 2);
            }},
            12,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.of(net.minecraft.world.item.Items.LEATHER),
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(SpeedForceMod.MOD_ID, name))),
            0.0F,
            0.0F
        ));
    }

    public static final Holder<ArmorMaterial> FLASH_MATERIAL = createArmorMaterial("flash");
    public static final Holder<ArmorMaterial> REVERSE_FLASH_MATERIAL = createArmorMaterial("reverse_flash");
    public static final Holder<ArmorMaterial> ZOOM_MATERIAL = createArmorMaterial("zoom");
    public static final Holder<ArmorMaterial> FLASH_S4_MATERIAL = createArmorMaterial("flash_s4");
    public static final Holder<ArmorMaterial> FLASH_S5_MATERIAL = createArmorMaterial("flash_s5");
    public static final Holder<ArmorMaterial> KID_FLASH_MATERIAL = createArmorMaterial("kid_flash");
    public static final Holder<ArmorMaterial> GREEN_ARROW_MATERIAL = createArmorMaterial("green_arrow");

    public static final DeferredItem<FlashSuitArmorItem> FLASH_HELMET = ITEMS.register("flash_helmet",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_CHESTPLATE = ITEMS.register("flash_chestplate",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_LEGGINGS = ITEMS.register("flash_leggings",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_BOOTS = ITEMS.register("flash_boots",
        () -> new FlashSuitArmorItem(FLASH_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_HELMET = ITEMS.register("reverse_flash_helmet",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_CHESTPLATE = ITEMS.register("reverse_flash_chestplate",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_LEGGINGS = ITEMS.register("reverse_flash_leggings",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> REVERSE_FLASH_BOOTS = ITEMS.register("reverse_flash_boots",
        () -> new FlashSuitArmorItem(REVERSE_FLASH_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.REVERSE_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_HELMET = ITEMS.register("zoom_helmet",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_CHESTPLATE = ITEMS.register("zoom_chestplate",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_LEGGINGS = ITEMS.register("zoom_leggings",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> ZOOM_BOOTS = ITEMS.register("zoom_boots",
        () -> new FlashSuitArmorItem(ZOOM_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.ZOOM));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_HELMET = ITEMS.register("flash_s4_helmet",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_CHESTPLATE = ITEMS.register("flash_s4_chestplate",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_LEGGINGS = ITEMS.register("flash_s4_leggings",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S4_BOOTS = ITEMS.register("flash_s4_boots",
        () -> new FlashSuitArmorItem(FLASH_S4_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S4));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_HELMET = ITEMS.register("flash_s5_helmet",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_CHESTPLATE = ITEMS.register("flash_s5_chestplate",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_LEGGINGS = ITEMS.register("flash_s5_leggings",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> FLASH_S5_BOOTS = ITEMS.register("flash_s5_boots",
        () -> new FlashSuitArmorItem(FLASH_S5_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.FLASH_S5));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_HELMET = ITEMS.register("kid_flash_helmet",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_CHESTPLATE = ITEMS.register("kid_flash_chestplate",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_LEGGINGS = ITEMS.register("kid_flash_leggings",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> KID_FLASH_BOOTS = ITEMS.register("kid_flash_boots",
        () -> new FlashSuitArmorItem(KID_FLASH_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.KID_FLASH));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_HELMET = ITEMS.register("green_arrow_helmet",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.HELMET, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_CHESTPLATE = ITEMS.register("green_arrow_chestplate",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.CHESTPLATE, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_LEGGINGS = ITEMS.register("green_arrow_leggings",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.LEGGINGS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<FlashSuitArmorItem> GREEN_ARROW_BOOTS = ITEMS.register("green_arrow_boots",
        () -> new FlashSuitArmorItem(GREEN_ARROW_MATERIAL, ArmorItem.Type.BOOTS, 
            new net.minecraft.world.item.Item.Properties(), SuitType.GREEN_ARROW));

    public static final DeferredItem<GreenArrowBowItem> GREEN_ARROW_BOW = ITEMS.register("green_arrow_bow",
        () -> new GreenArrowBowItem(new net.minecraft.world.item.Item.Properties().durability(384)));

    public static final DeferredItem<NormalArrowItem> NORMAL_ARROW = ITEMS.register("normal_arrow",
        () -> new NormalArrowItem(new net.minecraft.world.item.Item.Properties()));
}
package com.example.speedforce.item;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SpeedForceMod.MOD_ID);

    public static final Supplier<CreativeModeTab> SPEEDFORCE_TAB = CREATIVE_MODE_TABS.register("speedforce_tab",
        () -> CreativeModeTab.builder()
            .title(Component.translatable("creativetab.speedforce_tab"))
            .icon(() -> new ItemStack(ModItems.FLASH_HELMET.get()))
            .displayItems((parameters, output) -> {
                output.accept(ModItems.FLASH_HELMET.get());
                output.accept(ModItems.FLASH_CHESTPLATE.get());
                output.accept(ModItems.FLASH_LEGGINGS.get());
                output.accept(ModItems.FLASH_BOOTS.get());
                
                output.accept(ModItems.REVERSE_FLASH_HELMET.get());
                output.accept(ModItems.REVERSE_FLASH_CHESTPLATE.get());
                output.accept(ModItems.REVERSE_FLASH_LEGGINGS.get());
                output.accept(ModItems.REVERSE_FLASH_BOOTS.get());
                
                output.accept(ModItems.ZOOM_HELMET.get());
                output.accept(ModItems.ZOOM_CHESTPLATE.get());
                output.accept(ModItems.ZOOM_LEGGINGS.get());
                output.accept(ModItems.ZOOM_BOOTS.get());
                
                output.accept(ModItems.FLASH_S4_HELMET.get());
                output.accept(ModItems.FLASH_S4_CHESTPLATE.get());
                output.accept(ModItems.FLASH_S4_LEGGINGS.get());
                output.accept(ModItems.FLASH_S4_BOOTS.get());
                
                output.accept(ModItems.FLASH_S5_HELMET.get());
                output.accept(ModItems.FLASH_S5_CHESTPLATE.get());
                output.accept(ModItems.FLASH_S5_LEGGINGS.get());
                output.accept(ModItems.FLASH_S5_BOOTS.get());
                
                output.accept(ModItems.KID_FLASH_HELMET.get());
                output.accept(ModItems.KID_FLASH_CHESTPLATE.get());
                output.accept(ModItems.KID_FLASH_LEGGINGS.get());
                output.accept(ModItems.KID_FLASH_BOOTS.get());
                
                output.accept(ModItems.GREEN_ARROW_HELMET.get());
                output.accept(ModItems.GREEN_ARROW_CHESTPLATE.get());
                output.accept(ModItems.GREEN_ARROW_LEGGINGS.get());
                output.accept(ModItems.GREEN_ARROW_BOOTS.get());
                output.accept(ModItems.GREEN_ARROW_BOW.get());
                output.accept(ModItems.NORMAL_ARROW.get());
                
                output.accept(ModItems.PARTICLE_ACCELERATOR.get());
            }).build());
}
package com.example.speedforce.item;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class FlashSuitArmorItem extends ArmorItem {

    private final SuitType suitType;

    public FlashSuitArmorItem(Holder<ArmorMaterial> material, ArmorItem.Type type, Properties properties, SuitType suitType) {
        super(material, type, properties);
        this.suitType = suitType;
    }

    public SuitType getSuitType() {
        return suitType;
    }

    public void inventoryTick(ItemStack stack, Level level, net.minecraft.world.entity.Entity entity, int slotId, boolean isSelected) {
        if (!(entity instanceof Player player)) return;

        if (this.suitType == SuitType.GREEN_ARROW && hasFullGreenArrowSet(player)) {
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 40, 0, false, false));
            if (player.hasEffect(MobEffects.POISON)) {
                player.removeEffect(MobEffects.POISON);
            }
        }

        if (this.getEquipmentSlot() == EquipmentSlot.FEET) {
            handleWaterWalk(player);
        }
    }

    private boolean hasFullGreenArrowSet(Player player) {
        return isGreenArrowItem(player.getItemBySlot(EquipmentSlot.HEAD)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.CHEST)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.LEGS)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.FEET));
    }

    private boolean isGreenArrowItem(ItemStack stack) {
        return stack.getItem() instanceof FlashSuitArmorItem armor && 
               armor.getSuitType() == SuitType.GREEN_ARROW;
    }

    private void handleWaterWalk(Player player) {
        if (player.isSprinting()) {
            var posBelow = player.blockPosition().below();
            var stateBelow = player.level().getBlockState(posBelow);
            if (stateBelow.is(net.minecraft.world.level.block.Blocks.WATER)) {
                player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                player.fallDistance = 0.0F;
                player.setOnGround(true);
            }
        }
    }
}
package com.example.speedforce.item;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GreenArrowBowItem extends BowItem {

    public GreenArrowBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    private boolean hasFullGreenArrowSet(Player player) {
        return isGreenArrowItem(player.getItemBySlot(EquipmentSlot.HEAD)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.CHEST)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.LEGS)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.FEET));
    }

    private boolean isGreenArrowItem(ItemStack stack) {
        return stack.getItem() instanceof FlashSuitArmorItem armor && 
               armor.getSuitType() == SuitType.GREEN_ARROW;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            boolean hasInfinity = player.getAbilities().instabuild;
            boolean hasFullSet = hasFullGreenArrowSet(player);
            
            ItemStack arrowStack = ItemStack.EMPTY;
            
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (invStack.getItem() == ModItems.NORMAL_ARROW.get()) {
                    arrowStack = invStack;
                    break;
                }
            }
            
            if (!hasInfinity && arrowStack.isEmpty()) {
                return;
            }
            
            if (arrowStack.isEmpty()) {
                arrowStack = new ItemStack(ModItems.NORMAL_ARROW.get());
            }
            
            ArrowItem arrowItem = (ArrowItem) arrowStack.getItem();
            AbstractArrow arrow = arrowItem.createArrow(level, arrowStack, player, stack);
            
            int charge;
            if (hasFullSet) {
                charge = 20;
            } else {
                charge = this.getUseDuration(stack, entityLiving) - timeLeft;
                charge = net.minecraft.util.Mth.clamp(charge, 0, 20);
            }
            
            float velocity = calculateArrowVelocity(charge);
            
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 0.0F);
            
            if (hasFullSet) {
                arrow.setBaseDamage(arrow.getBaseDamage() * 3.0);
            }
            
            if (velocity >= 1.0F) {
                arrow.setCritArrow(true);
            }
            
            if (hasInfinity) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            
            level.addFreshEntity(arrow);
            
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));
            
            if (!hasInfinity) {
                arrowStack.shrink(1);
                if (arrowStack.isEmpty()) {
                    player.getInventory().removeItem(arrowStack);
                }
            }
        }
    }

    private static float calculateArrowVelocity(int charge) {
        float f = charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/event/WorldRewindHandler.java (UPDATED v1.0.6v7)
// Added: TNT tracking, projectile rewind, arrow extraction via reflection
// ============================================================================
package com.example.speedforce.event;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.EntityLeaveLevelEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.level.ExplosionEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.lang.reflect.Field;
import java.util.*;

@EventBusSubscriber(modid = "speedforce")
public class WorldRewindHandler {
    
    private static final Field ARROW_IN_GROUND_FIELD;
    static {
        try {
            ARROW_IN_GROUND_FIELD = AbstractArrow.class.getDeclaredField("inGround");
            ARROW_IN_GROUND_FIELD.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to access AbstractArrow.inGround field", e);
        }
    }
    
    public record BlockSnapshot(BlockPos pos, BlockState state) {}
    public record DeadEntitySnapshot(EntityType<?> type, CompoundTag nbt, Vec3 pos, UUID uuid) {}
    public record EntitySnapshot(UUID uuid, Vec3 pos, float yRot, float xRot, Vec3 delta, int fuse, float health) {}
    
    public record TickSnapshot(List<BlockSnapshot> blocks, List<DeadEntitySnapshot> deadEntities, List<EntitySnapshot> livingEntities) {}

    private static final Map<ServerLevel, Deque<TickSnapshot>> HISTORY = new HashMap<>();
    private static final Map<ServerLevel, List<BlockSnapshot>> PENDING_BLOCKS = new HashMap<>();
    private static final Map<ServerLevel, List<DeadEntitySnapshot>> PENDING_DEAD_ENTITIES = new HashMap<>();

    private static boolean isLevelRewinding(ServerLevel level) {
        for (Player p : level.players()) {
            if (RewindHandler.IS_REWINDING.getOrDefault(p.getUUID(), false)) {
                return true;
            }
        }
        return false;
    }

    @SubscribeEvent
    public static void onExplosionDetonate(ExplosionEvent.Detonate event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (isLevelRewinding(level)) return;
            List<BlockSnapshot> blocks = PENDING_BLOCKS.computeIfAbsent(level, k -> new ArrayList<>());
            for (BlockPos pos : event.getAffectedBlocks()) {
                blocks.add(new BlockSnapshot(pos.immutable(), level.getBlockState(pos)));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof PrimedTnt tnt && event.getLevel() instanceof ServerLevel level) {
            if (isLevelRewinding(level)) return;
            List<BlockSnapshot> blocks = PENDING_BLOCKS.computeIfAbsent(level, k -> new ArrayList<>());
            blocks.add(new BlockSnapshot(tnt.blockPosition(), Blocks.TNT.defaultBlockState()));
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (isLevelRewinding(level)) return;
            List<BlockSnapshot> blocks = PENDING_BLOCKS.computeIfAbsent(level, k -> new ArrayList<>());
            blocks.add(new BlockSnapshot(event.getPos().immutable(), event.getState()));
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel() instanceof ServerLevel level) {
            if (isLevelRewinding(level)) return;
            List<BlockSnapshot> blocks = PENDING_BLOCKS.computeIfAbsent(level, k -> new ArrayList<>());
            BlockState replacedState = level.getBlockState(event.getPos());
            blocks.add(new BlockSnapshot(event.getPos().immutable(), replacedState));
        }
    }

    @SubscribeEvent
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity.level() instanceof ServerLevel level && !(entity instanceof Player)) {
            if (isLevelRewinding(level)) return;
            
            if (entity instanceof LivingEntity || entity instanceof PrimedTnt || entity instanceof ItemEntity || entity instanceof Projectile) {
                List<DeadEntitySnapshot> deadEntities = PENDING_DEAD_ENTITIES.computeIfAbsent(level, k -> new ArrayList<>());
                CompoundTag tag = new CompoundTag();
                entity.saveWithoutId(tag);
                deadEntities.add(new DeadEntitySnapshot(entity.getType(), tag, entity.position(), entity.getUUID()));
            }
        }
    }

    @SubscribeEvent
    public static void onLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel level) {
            boolean rewinding = isLevelRewinding(level);
            Deque<TickSnapshot> history = HISTORY.computeIfAbsent(level, k -> new ArrayDeque<>());
            
            if (rewinding) {
                if (!history.isEmpty()) {
                    TickSnapshot snapshot = history.pollFirst();
                    
                    for (BlockSnapshot bs : snapshot.blocks()) {
                        level.setBlock(bs.pos(), bs.state(), 3);
                    }
                    
                    for (DeadEntitySnapshot des : snapshot.deadEntities()) {
                        if (level.getEntity(des.uuid()) == null) { 
                            Entity entity = des.type().create(level);
                            if (entity != null) {
                                entity.load(des.nbt());
                                entity.setUUID(des.uuid());
                                if (entity instanceof PrimedTnt tnt && tnt.getFuse() <= 0) {
                                    tnt.setFuse(1);
                                }
                                level.addFreshEntity(entity);
                            }
                        }
                    }
                    
                    Set<UUID> validUUIDs = new HashSet<>();
                    for (DeadEntitySnapshot des : snapshot.deadEntities()) {
                        validUUIDs.add(des.uuid());
                    }
                    
                    for (EntitySnapshot es : snapshot.livingEntities()) {
                        validUUIDs.add(es.uuid());
                        Entity entity = level.getEntity(es.uuid());
                        if (entity != null) {
                            entity.teleportTo(es.pos().x, es.pos().y, es.pos().z);
                            entity.setYRot(es.yRot());
                            entity.setXRot(es.xRot());
                            entity.setDeltaMovement(es.delta());
                            entity.fallDistance = 0;
                            
                            if (entity instanceof PrimedTnt tnt) {
                                tnt.setFuse(Math.max(1, es.fuse()));
                            } else if (entity instanceof LivingEntity le) {
                                le.setHealth(es.health());
                            } else if (entity instanceof AbstractArrow arrow) {
                                try {
                                    ARROW_IN_GROUND_FIELD.setBoolean(arrow, false);
                                } catch (IllegalAccessException ignored) {}
                            }
                        }
                    }
                    
                    for (Entity entity : level.getAllEntities()) {
                        if (entity != null && !(entity instanceof Player)) {
                            if (entity instanceof LivingEntity || entity instanceof PrimedTnt || entity instanceof ItemEntity || entity instanceof Projectile) {
                                if (!validUUIDs.contains(entity.getUUID())) {
                                    entity.discard();
                                }
                            }
                        }
                    }
                }
                PENDING_BLOCKS.remove(level);
                PENDING_DEAD_ENTITIES.remove(level);
            } else {
                List<BlockSnapshot> currentBlocks = new ArrayList<>(PENDING_BLOCKS.getOrDefault(level, Collections.emptyList()));
                List<DeadEntitySnapshot> currentDead = new ArrayList<>(PENDING_DEAD_ENTITIES.getOrDefault(level, Collections.emptyList()));
                List<EntitySnapshot> currentLiving = new ArrayList<>();
                
                for (Entity entity : level.getAllEntities()) {
                    if (entity != null && !(entity instanceof Player)) {
                        if (entity instanceof LivingEntity || entity instanceof PrimedTnt || entity instanceof ItemEntity || entity instanceof Projectile) {
                            int fuse = entity instanceof PrimedTnt tnt ? tnt.getFuse() : 0;
                            float health = entity instanceof LivingEntity le ? le.getHealth() : 0;
                            currentLiving.add(new EntitySnapshot(entity.getUUID(), entity.position(), entity.getYRot(), entity.getXRot(), entity.getDeltaMovement(), fuse, health));
                        }
                    }
                }
                
                history.addFirst(new TickSnapshot(currentBlocks, currentDead, currentLiving));
                if (history.size() > 200) {
                    history.removeLast();
                }
                
                PENDING_BLOCKS.remove(level);
                PENDING_DEAD_ENTITIES.remove(level);
            }
        }
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/event/RewindHandler.java (UPDATED v1.0.6v5)
// Simplified to only manage IS_REWINDING state and lock player position
// ============================================================================
package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "speedforce")
public class RewindHandler {
    public static final Map<UUID, Boolean> IS_REWINDING = new HashMap<>();
    public static final Map<UUID, Double[]> REWIND_START_POS = new HashMap<>();
    public static final Map<UUID, Integer> REWIND_HISTORY_SIZE = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        UUID uuid = player.getUUID();
        var data = player.getData(ModAttachments.SPEED_PLAYER);

        if (!data.hasPower || data.speedLevel <= 0) {
            IS_REWINDING.put(uuid, false);
            REWIND_START_POS.remove(uuid);
            REWIND_HISTORY_SIZE.remove(uuid);
            return;
        }

        boolean rewinding = IS_REWINDING.getOrDefault(uuid, false);
        
        if (rewinding) {
            player.setDeltaMovement(0, 0, 0);
            player.fallDistance = 0;
        }
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/capability/RewindState.java (NEW v1.0.6v3)
// ============================================================================
package com.example.speedforce.capability;

import net.minecraft.world.phys.Vec3;

public record RewindState(Vec3 pos, float yRot, float xRot, float health) {}
// ============================================================================
// src/main/java/com/example/speedforce/network/RewindPayload.java (NEW v1.0.6v3)
// ============================================================================
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RewindPayload(boolean isRewinding) implements CustomPacketPayload {
    public static final Type<RewindPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "rewind_payload"));
    
    public static final StreamCodec<ByteBuf, RewindPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, RewindPayload::isRewinding,
        RewindPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/event/RewindHandler.java (NEW v1.0.6v3)
// ============================================================================
package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.RewindState;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "speedforce")
public class RewindHandler {
    public static final Map<UUID, Deque<RewindState>> HISTORY = new HashMap<>();
    public static final Map<UUID, Boolean> IS_REWINDING = new HashMap<>();
    public static final int MAX_HISTORY = 200;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        UUID uuid = player.getUUID();
        var data = player.getData(ModAttachments.SPEED_PLAYER);

        if (!data.hasPower || data.speedLevel <= 0) {
            HISTORY.remove(uuid);
            IS_REWINDING.put(uuid, false);
            return;
        }

        boolean rewinding = IS_REWINDING.getOrDefault(uuid, false);
        Deque<RewindState> history = HISTORY.computeIfAbsent(uuid, k -> new ArrayDeque<>());

        if (rewinding) {
            if (!history.isEmpty()) {
                RewindState state = history.pollFirst();
                player.teleportTo(state.pos().x, state.pos().y, state.pos().z);
                player.setYRot(state.yRot());
                player.setXRot(state.xRot());
                player.setHealth(state.health());
                player.setDeltaMovement(0, 0, 0);
                player.fallDistance = 0;
            } else {
                IS_REWINDING.put(uuid, false);
            }
        } else {
            history.addFirst(new RewindState(player.position(), player.getYRot(), player.getXRot(), player.getHealth()));
            if (history.size() > MAX_HISTORY) {
                history.removeLast();
            }
        }
    }
}
// ============================================================================
// Updated ModNetworking.java - Added RewindPayload registration (v1.0.6v3)
// ============================================================================
// Add to registerPayloads method:
//         registrar.playToServer(RewindPayload.TYPE, RewindPayload.STREAM_CODEC, (payload, context) -> {
//             context.enqueueWork(() -> {
//                 if (context.player() instanceof ServerPlayer player) {
//                     com.example.speedforce.event.RewindHandler.IS_REWINDING.put(player.getUUID(), payload.isRewinding());
//                 }
//             });
//         });
// ============================================================================
// Updated ClientSpeedData.java - Added clientHistorySize (v1.0.6v3)
// ============================================================================
// Add: public static int clientHistorySize = 0;
// ============================================================================
// Updated ClientKeybinds.java - Added R key for Time Rewind (v1.0.6v3)
// ============================================================================
// Add: public static final KeyMapping REWIND_KEY = new KeyMapping("key.speedforce.rewind", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "category.speedforce.keys");
// Add: private static boolean wasRewinding = false;
// Add in onClientTick:
//         boolean isRewindingNow = REWIND_KEY.isDown() && mc.player.isSprinting() && ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0;
//         if (isRewindingNow != wasRewinding) {
//             PacketDistributor.sendToServer(new RewindPayload(isRewindingNow));
//             wasRewinding = isRewindingNow;
//         }
//         if (ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0) {
//             if (!isRewindingNow) {
//                 ClientSpeedData.clientHistorySize = Math.min(200, ClientSpeedData.clientHistorySize + 1);
//             } else {
//                 ClientSpeedData.clientHistorySize = Math.max(0, ClientSpeedData.clientHistorySize - 1);
//             }
//         } else {
//             ClientSpeedData.clientHistorySize = 0;
//         }
// ============================================================================
// Updated SpeedHudRenderer.java - Added Rewind progress bar (v1.0.6v3)
// ============================================================================
// Add at end of onRenderGui method:
//         if (ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0) {
//             int rewindBarWidth = 120;
//             int rewindBarX = screenWidth / 2 - rewindBarWidth / 2;
//             int rewindBarY = mc.getWindow().getGuiScaledHeight() - 40;
//
//             graphics.fill(rewindBarX, rewindBarY, rewindBarX + rewindBarWidth, rewindBarY + 4, 0xFF444444);
//             
//             float rewindRatio = (float) ClientSpeedData.clientHistorySize / 200.0f;
//             int rewindFillWidth = (int) (rewindBarWidth * rewindRatio);
//             
//             if (rewindFillWidth > 0) {
//                 graphics.fill(rewindBarX, rewindBarY, rewindBarX + rewindFillWidth, rewindBarY + 4, 0xFF00FFFF);
//             }
//             
//             String rewindText = String.format("%.1f s", ClientSpeedData.clientHistorySize / 20.0f);
//             graphics.drawString(mc.font, rewindText, rewindBarX + rewindBarWidth + 6, rewindBarY - 2, 0xFF00FFFF);
//             
//             if (ClientKeybinds.REWIND_KEY.isDown() && player.isSprinting()) {
//                 graphics.drawCenteredString(mc.font, "时间回溯中...", screenWidth / 2, rewindBarY - 12, 0xFF00FFFF);
//             }
//         }
// ============================================================================
// src/main/java/com/example/speedforce/menu/ModMenuTypes.java
// ============================================================================
package com.example.speedforce.menu;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.inventory.QuiverMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = 
        DeferredRegister.create(Registries.MENU, SpeedForceMod.MOD_ID);

    public static final Supplier<MenuType<SpeedForceWorkbenchMenu>> SPEED_FORCE_WORKBENCH = 
        MENUS.register("speed_force_workbench", () -> SpeedForceWorkbenchMenu.TYPE);

    public static final Supplier<MenuType<QuiverMenu>> QUIVER = 
        MENUS.register("quiver", () -> QuiverMenu.TYPE);
}
// ============================================================================
// src/main/java/com/example/speedforce/item/QuiverItem.java
// ============================================================================
package com.example.speedforce.item;

import com.example.speedforce.inventory.QuiverMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class QuiverItem extends Item {

    public static final int SLOT_COUNT = 5;

    public QuiverItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            player.openMenu(new net.minecraft.world.SimpleMenuProvider(
                (containerId, playerInventory, playerIn) -> new QuiverMenu(containerId, playerInventory, stack),
                Component.translatable("item.speedforce.quiver")
            ));
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    private static CompoundTag getCustomData(ItemStack stack) {
        var customData = stack.get(net.minecraft.core.component.DataComponents.CUSTOM_DATA);
        return customData != null ? customData.copyTag() : new CompoundTag();
    }

    private static void setCustomData(ItemStack stack, CompoundTag tag) {
        stack.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA, 
            net.minecraft.world.item.component.CustomData.of(tag));
    }

    public static NonNullList<ItemStack> getInventory(ItemStack stack) {
        NonNullList<ItemStack> items = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
        CompoundTag tag = getCustomData(stack);
        if (tag.contains("Items", Tag.TAG_LIST)) {
            ListTag listTag = tag.getList("Items", Tag.TAG_COMPOUND);
            for (int i = 0; i < listTag.size(); i++) {
                CompoundTag itemTag = listTag.getCompound(i);
                int slot = itemTag.getByte("Slot") & 255;
                if (slot < SLOT_COUNT) {
                    items.set(slot, loadItemStack(itemTag));
                }
            }
        }
        return items;
    }

    public static void saveInventory(ItemStack stack, NonNullList<ItemStack> items) {
        CompoundTag tag = getCustomData(stack);
        ListTag listTag = new ListTag();
        for (int i = 0; i < items.size(); i++) {
            ItemStack itemStack = items.get(i);
            if (!itemStack.isEmpty()) {
                CompoundTag itemTag = saveItemStack(itemStack);
                itemTag.putByte("Slot", (byte) i);
                listTag.add(itemTag);
            }
        }
        tag.put("Items", listTag);
        setCustomData(stack, tag);
    }

    private static ItemStack loadItemStack(CompoundTag tag) {
        if (!tag.contains("id", Tag.TAG_STRING)) return ItemStack.EMPTY;
        ResourceLocation id = ResourceLocation.tryParse(tag.getString("id"));
        if (id == null) return ItemStack.EMPTY;
        Optional<Item> item = BuiltInRegistries.ITEM.getOptional(id);
        if (item.isEmpty()) return ItemStack.EMPTY;
        int count = tag.contains("Count", Tag.TAG_INT) ? tag.getInt("Count") : 1;
        return new ItemStack(item.get(), count);
    }

    private static CompoundTag saveItemStack(ItemStack stack) {
        CompoundTag tag = new CompoundTag();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        tag.putString("id", id.toString());
        tag.putInt("Count", stack.getCount());
        return tag;
    }

    public static int getSelectedSlot(ItemStack stack) {
        CompoundTag tag = getCustomData(stack);
        return tag.contains("SelectedSlot") ? tag.getInt("SelectedSlot") : 0;
    }

    public static void setSelectedSlot(ItemStack stack, int slot) {
        CompoundTag tag = getCustomData(stack);
        tag.putInt("SelectedSlot", Math.max(0, Math.min(slot, SLOT_COUNT - 1)));
        setCustomData(stack, tag);
    }

    public static ItemStack getSelectedArrow(ItemStack quiverStack) {
        NonNullList<ItemStack> items = getInventory(quiverStack);
        int selectedSlot = getSelectedSlot(quiverStack);
        return items.get(selectedSlot);
    }

    public static boolean consumeArrow(ItemStack quiverStack, int amount) {
        NonNullList<ItemStack> items = getInventory(quiverStack);
        int selectedSlot = getSelectedSlot(quiverStack);
        ItemStack arrowStack = items.get(selectedSlot);
        
        if (!arrowStack.isEmpty() && arrowStack.getCount() >= amount) {
            arrowStack.shrink(amount);
            saveInventory(quiverStack, items);
            return true;
        }
        return false;
    }

    public static int getArrowCount(ItemStack quiverStack) {
        NonNullList<ItemStack> items = getInventory(quiverStack);
        int selectedSlot = getSelectedSlot(quiverStack);
        ItemStack arrowStack = items.get(selectedSlot);
        return arrowStack.isEmpty() ? 0 : arrowStack.getCount();
    }

    public static void cycleSelectedSlot(ItemStack quiverStack) {
        int current = getSelectedSlot(quiverStack);
        NonNullList<ItemStack> items = getInventory(quiverStack);
        
        for (int i = 1; i < SLOT_COUNT; i++) {
            int nextSlot = (current + i) % SLOT_COUNT;
            if (!items.get(nextSlot).isEmpty()) {
                setSelectedSlot(quiverStack, nextSlot);
                return;
            }
        }
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/inventory/QuiverContainer.java
// ============================================================================
package com.example.speedforce.inventory;

import com.example.speedforce.item.QuiverItem;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class QuiverContainer implements Container {

    private final ItemStack quiverStack;
    private final NonNullList<ItemStack> items;

    public QuiverContainer(ItemStack quiverStack) {
        this.quiverStack = quiverStack;
        this.items = QuiverItem.getInventory(quiverStack);
    }

    @Override
    public int getContainerSize() {
        return QuiverItem.SLOT_COUNT;
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getItem(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        ItemStack result = items.get(slot).split(amount);
        if (!result.isEmpty()) {
            setChanged();
        }
        return result;
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        ItemStack stack = items.get(slot);
        items.set(slot, ItemStack.EMPTY);
        return stack;
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        items.set(slot, stack);
        setChanged();
    }

    @Override
    public void setChanged() {
        QuiverItem.saveInventory(quiverStack, items);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        items.clear();
        setChanged();
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/inventory/QuiverMenu.java
// ============================================================================
package com.example.speedforce.inventory;

import com.example.speedforce.item.QuiverItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;

import javax.annotation.Nullable;

public class QuiverMenu extends AbstractContainerMenu {

    public static final MenuType<QuiverMenu> TYPE = IMenuTypeExtension.create(QuiverMenu::new);

    private final ItemStack quiverStack;
    private final QuiverContainer container;

    public QuiverMenu(int containerId, Inventory playerInventory, ItemStack quiverStack) {
        super(TYPE, containerId);
        this.quiverStack = quiverStack;
        this.container = new QuiverContainer(quiverStack);

        for (int i = 0; i < 5; i++) {
            this.addSlot(new QuiverSlot(container, i, 44 + i * 18, 36));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    public QuiverMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(containerId, playerInventory, ItemStack.EMPTY);
    }

    public int getSelectedSlot() {
        return QuiverItem.getSelectedSlot(quiverStack);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (slot == null || !slot.hasItem()) return ItemStack.EMPTY;

        ItemStack slotStack = slot.getItem();
        ItemStack originalStack = slotStack.copy();

        if (slotIndex < 5) {
            if (!this.moveItemStackTo(slotStack, 5, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!this.moveItemStackTo(slotStack, 0, 5, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (slotStack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return originalStack;
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    private static class QuiverSlot extends Slot {
        public QuiverSlot(QuiverContainer container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return stack.getItem() instanceof ArrowItem;
        }
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/client/screen/QuiverScreen.java
// ============================================================================
package com.example.speedforce.client.screen;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.inventory.QuiverMenu;
import com.example.speedforce.item.QuiverItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class QuiverScreen extends AbstractContainerScreen<QuiverMenu> {

    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
        SpeedForceMod.MOD_ID, "textures/gui/quiver.png");

    public QuiverScreen(QuiverMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        
        int selectedSlot = menu.getSelectedSlot();
        int slotX = this.leftPos + 43 + selectedSlot * 18;
        int slotY = this.topPos + 35;
        graphics.fill(slotX, slotY, slotX + 18, slotY + 18, 0x80FFFFFF);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        graphics.drawString(this.font, this.playerInventoryTitle, 8, 72, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/client/ClientSpeedData.java
// ============================================================================
package com.example.speedforce.client;

public class ClientSpeedData {
    public static boolean hasPower = false;
    public static int speedLevel = 0;
    public static boolean isBulletTimeActive = false;
    public static boolean isPhasing = false;
    public static int trailColorR = 255;
    public static int trailColorG = 210;
    public static int trailColorB = 0;
    public static int customTrailColorR = 255;
    public static int customTrailColorG = 210;
    public static int customTrailColorB = 0;
    public static boolean showHelp = true;
    public static int clientHistorySize = 0;
}
// ============================================================================
// src/main/java/com/example/speedforce/client/ClientRewindData.java
// ============================================================================
package com.example.speedforce.client;

public class ClientRewindData {
    public static int phase = 0;
    public static int framesRewound = 0;
    public static int rewindSpeed = 1;
    public static int totalHistorySize = 0;

    public static final int PHASE_IDLE = 0;
    public static final int PHASE_REWINDING = 1;

    public static boolean isRewinding() {
        return phase == PHASE_REWINDING;
    }

    public static float getRewindProgress() {
        if (totalHistorySize <= 0) return 0;
        return (float) framesRewound / totalHistorySize;
    }

    public static void reset() {
        phase = PHASE_IDLE;
        framesRewound = 0;
        rewindSpeed = 1;
        totalHistorySize = 0;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/client/ClientRemnantData.java
// ============================================================================
package com.example.speedforce.client;

public class ClientRemnantData {
    public static boolean hasRemnant = false;
    public static int remainingSeconds = 0;
    
    public static void setRemnantActive(boolean active, int seconds) {
        hasRemnant = active;
        remainingSeconds = seconds;
    }
    
    public static void reset() {
        hasRemnant = false;
        remainingSeconds = 0;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/client/RewindAnimationHandler.java
// Updated v1.0.7v6 - Increased animSpeed to 15.0f
// ============================================================================
package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLivingEvent;

import java.lang.reflect.Field;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class RewindAnimationHandler {

    private static Field positionField;
    private static Field speedField;
    
    static {
        try {
            positionField = net.minecraft.world.entity.WalkAnimationState.class.getDeclaredField("position");
            positionField.setAccessible(true);
            speedField = net.minecraft.world.entity.WalkAnimationState.class.getDeclaredField("speed");
            speedField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<Player, PlayerModel<Player>> event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player != Minecraft.getInstance().player) return;
        if (!ClientRewindData.isRewinding()) return;

        float partialTicks = event.getPartialTick();
        float fakePosition = (player.tickCount + partialTicks) * 0.6f;
        
        try {
            if (positionField != null && speedField != null) {
                positionField.setFloat(player.walkAnimation, fakePosition);
                speedField.setFloat(player.walkAnimation, 1.0f);
            }
        } catch (IllegalAccessException ignored) {}
        
        @SuppressWarnings("unchecked")
        PlayerModel<Player> model = (PlayerModel<Player>) event.getRenderer().getModel();
        
        float gameTime = player.tickCount + partialTicks;
        float animSpeed = 15.0f;
        float legAmplitude = 1.2f;
        float armAmplitude = 0.8f;
        
        float cycle = Mth.sin(gameTime * animSpeed) * legAmplitude;

        model.leftLeg.xRot = cycle;
        model.rightLeg.xRot = -cycle;
        
        model.leftArm.xRot = -cycle * armAmplitude / legAmplitude;
        model.rightArm.xRot = cycle * armAmplitude / legAmplitude;
        
        model.leftLeg.yRot = 0;
        model.rightLeg.yRot = 0;
        model.leftLeg.zRot = 0;
        model.rightLeg.zRot = 0;
        
        model.leftArm.yRot = 0;
        model.rightArm.yRot = 0;
        model.leftArm.zRot = Mth.sin(gameTime * animSpeed * 0.5f) * 0.1f;
        model.rightArm.zRot = -Mth.sin(gameTime * animSpeed * 0.5f) * 0.1f;
        
        model.body.yRot = Mth.sin(gameTime * animSpeed * 0.3f) * 0.05f;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/client/RewindEffectRenderer.java
// ============================================================================
package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.Random;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class RewindEffectRenderer {

    private static final int LAYER_COUNT = 3;
    private static final int LIGHTNINGS_PER_LAYER = 4;
    private static final float[] LAYER_HEIGHTS = {0.5f, 1.0f, 1.4f};
    private static final float ORBIT_RADIUS = 0.7f;
    private static final int SEGMENTS_PER_LIGHTNING = 3;

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        if (!ClientRewindData.isRewinding()) return;
        
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();
        float pt = mc.getTimer().getGameTimeDeltaTicks();
        float gameTime = player.tickCount + pt;
        
        double px = Mth.lerp(pt, player.xOld, player.getX());
        double py = Mth.lerp(pt, player.yOld, player.getY());
        double pz = Mth.lerp(pt, player.zOld, player.getZ());
        Vec3 playerPos = new Vec3(px, py, pz);
        
        int r = ClientSpeedData.trailColorR;
        int g = ClientSpeedData.trailColorG;
        int b = ClientSpeedData.trailColorB;

        poseStack.pushPose();
        Matrix4f pose = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Random random = new Random();
        
        for (int layer = 0; layer < LAYER_COUNT; layer++) {
            float height = LAYER_HEIGHTS[layer];
            float rotationOffset = layer * (float)(Math.PI / LAYER_COUNT);
            
            for (int i = 0; i < LIGHTNINGS_PER_LAYER; i++) {
                float baseAngle = (float)(i * 2 * Math.PI / LIGHTNINGS_PER_LAYER);
                float currentAngle = baseAngle + gameTime * 0.3f + rotationOffset;
                
                Vec3 prevPoint = null;
                
                for (int seg = 0; seg <= SEGMENTS_PER_LIGHTNING; seg++) {
                    float segmentAngle = currentAngle + seg * (float)(Math.PI / 6);
                    float segHeight = height + seg * 0.05f;
                    float segRadius = ORBIT_RADIUS + (seg % 2 == 0 ? 0.05f : -0.05f);
                    
                    double x = Math.cos(segmentAngle) * segRadius;
                    double z = Math.sin(segmentAngle) * segRadius;
                    
                    random.setSeed((long)(gameTime * 100 + layer * 1000 + i * 10000 + seg));
                    double jitterX = (random.nextDouble() - 0.5) * 0.15;
                    double jitterY = (random.nextDouble() - 0.5) * 0.1;
                    double jitterZ = (random.nextDouble() - 0.5) * 0.15;
                    
                    Vec3 currentPoint = playerPos.add(x + jitterX, segHeight + jitterY, z + jitterZ);
                    
                    if (prevPoint != null) {
                        float segRatio = (float) seg / SEGMENTS_PER_LIGHTNING;
                        int alpha = (int)(200 * (1.0f - segRatio * 0.3f));
                        float width = 0.04f * (1.0f - segRatio * 0.2f);
                        
                        drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width, r, g, b, alpha);
                        drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width * 0.3f, 255, 255, 255, alpha);
                    }
                    
                    prevPoint = currentPoint;
                }
            }
        }

        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void drawLightningSegment(BufferBuilder builder, Matrix4f pose, Vec3 start, Vec3 end, 
                                              Vec3 cameraPos, float width, int r, int g, int b, int a) {
        Vec3 dir = end.subtract(start);
        if (dir.lengthSqr() < 1e-7) return;

        Vec3 normal = dir.cross(cameraPos.subtract(start)).normalize().scale(width);

        float sx = (float)(start.x - cameraPos.x);
        float sy = (float)(start.y - cameraPos.y);
        float sz = (float)(start.z - cameraPos.z);

        float ex = (float)(end.x - cameraPos.x);
        float ey = (float)(end.y - cameraPos.y);
        float ez = (float)(end.z - cameraPos.z);

        float nx = (float)normal.x, ny = (float)normal.y, nz = (float)normal.z;

        builder.addVertex(pose, sx + nx, sy + ny, sz + nz).setColor(r, g, b, a);
        builder.addVertex(pose, sx - nx, sy - ny, sz - nz).setColor(r, g, b, a);
        builder.addVertex(pose, ex - nx, ey - ny, ez - nz).setColor(r, g, b, a);
        builder.addVertex(pose, ex + nx, ey + ny, ez + nz).setColor(r, g, b, a);
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/client/renderer/TimeRemnantRenderer.java
// ============================================================================
package com.example.speedforce.client.renderer;

import com.example.speedforce.entity.TimeRemnantEntity;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class TimeRemnantRenderer extends MobRenderer<TimeRemnantEntity, PlayerModel<TimeRemnantEntity>> {

    private static final Map<UUID, ResourceLocation> SKIN_CACHE = new ConcurrentHashMap<>();
    
    public TimeRemnantRenderer(EntityRendererProvider.Context context) {
        super(context, new PlayerModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(TimeRemnantEntity entity) {
        UUID ownerUUID = entity.getOwnerUUID();
        if (ownerUUID == null) {
            return DefaultPlayerSkin.getDefaultTexture();
        }
        
        ResourceLocation cachedSkin = SKIN_CACHE.get(ownerUUID);
        if (cachedSkin != null) {
            return cachedSkin;
        }
        
        Player owner = entity.getOwner();
        if (owner instanceof AbstractClientPlayer clientPlayer) {
            ResourceLocation skinLocation = clientPlayer.getSkin().texture();
            SKIN_CACHE.put(ownerUUID, skinLocation);
            return skinLocation;
        }
        
        return DefaultPlayerSkin.getDefaultTexture();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        PoseStack poseStack = event.getPoseStack();
        Vec3 cameraPos = event.getCamera().getPosition();

        List<TimeRemnantEntity> remnants = mc.level.getEntitiesOfClass(TimeRemnantEntity.class, 
            net.minecraft.world.phys.AABB.INFINITE);
        
        for (TimeRemnantEntity remnant : remnants) {
            if (remnant.shouldRenderTrail()) {
                renderRemnantTrail(remnant, poseStack, cameraPos);
            }
        }
    }

    private static void renderRemnantTrail(TimeRemnantEntity entity, PoseStack poseStack, Vec3 cameraPos) {
        List<Vec3> trailHistory = entity.getTrailHistory();
        if (trailHistory.size() < 2) return;

        int r = entity.getTrailColorR();
        int g = entity.getTrailColorG();
        int b = entity.getTrailColorB();

        poseStack.pushPose();
        Matrix4f pose = poseStack.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
            GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE,
            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO
        );
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        Vec3 prevPoint = null;
        int age = 0;
        int maxAge = trailHistory.size();
        boolean drewAnything = false;

        for (Vec3 point : trailHistory) {
            float ageRatio = (float) age / maxAge;
            float alpha = 1.0F - (ageRatio * ageRatio);
            int a = (int)(alpha * 180);
            if (a <= 5) {
                age++;
                continue;
            }

            Vec3 currentPoint = point.add(0, 1.0, 0);

            if (prevPoint != null) {
                float width = 0.04f * (1 - ageRatio * 0.4f);
                drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width, r, g, b, (int)(a * 0.8));
                drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, width * 0.3f, 255, 255, 255, a);
                drewAnything = true;
            }

            prevPoint = currentPoint;
            age++;
        }

        if (!drewAnything) {
            builder.addVertex(pose, 0, 0, 0).setColor(0, 0, 0, 0);
            builder.addVertex(pose, 0, 0, 0).setColor(0, 0, 0, 0);
            builder.addVertex(pose, 0, 0, 0).setColor(0, 0, 0, 0);
            builder.addVertex(pose, 0, 0, 0).setColor(0, 0, 0, 0);
        }

        BufferUploader.drawWithShader(builder.buildOrThrow());

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void drawLightningSegment(BufferBuilder builder, Matrix4f pose, Vec3 start, Vec3 end, 
                                       Vec3 cameraPos, float width, int r, int g, int b, int a) {
        Vec3 dir = end.subtract(start);
        if (dir.lengthSqr() < 1e-7) return;

        Vec3 normal = dir.cross(cameraPos.subtract(start)).normalize().scale(width);

        float sx = (float)(start.x - cameraPos.x);
        float sy = (float)(start.y - cameraPos.y);
        float sz = (float)(start.z - cameraPos.z);

        float ex = (float)(end.x - cameraPos.x);
        float ey = (float)(end.y - cameraPos.y);
        float ez = (float)(end.z - cameraPos.z);

        float nx = (float)normal.x, ny = (float)normal.y, nz = (float)normal.z;

        builder.addVertex(pose, sx + nx, sy + ny, sz + nz).setColor(r, g, b, a);
        builder.addVertex(pose, sx - nx, sy - ny, sz - nz).setColor(r, g, b, a);
        builder.addVertex(pose, ex - nx, ey - ny, ez - nz).setColor(r, g, b, a);
        builder.addVertex(pose, ex + nx, ey + ny, ez + nz).setColor(r, g, b, a);
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/event/TimeRemnantHandler.java
// ============================================================================
package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.entity.ModEntityTypes;
import com.example.speedforce.entity.TimeRemnantEntity;
import com.example.speedforce.network.RemnantStatePayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = "speedforce")
public class TimeRemnantHandler {

    private static final Map<UUID, Integer> PLAYER_REMNANT_IDS = new HashMap<>();

    public static void summonRemnant(ServerPlayer player) {
        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
        
        if (!data.hasPower || data.speedLevel < 4) {
            return;
        }
        
        if (PLAYER_REMNANT_IDS.containsKey(player.getUUID())) {
            dismissRemnant(player);
            return;
        }
        
        ServerLevel level = player.serverLevel();
        
        TimeRemnantEntity remnant = new TimeRemnantEntity(ModEntityTypes.TIME_REMNANT.get(), level);
        remnant.setPos(player.getX(), player.getY(), player.getZ());
        remnant.setOwner(player);
        
        level.addFreshEntity(remnant);
        
        PLAYER_REMNANT_IDS.put(player.getUUID(), remnant.getId());
        
        syncRemnantState(player);
    }

    public static void dismissRemnant(ServerPlayer player) {
        Integer entityId = PLAYER_REMNANT_IDS.remove(player.getUUID());
        if (entityId != null) {
            Entity entity = player.level().getEntity(entityId);
            if (entity instanceof TimeRemnantEntity remnant && remnant.isAlive()) {
                remnant.discard();
            }
        }
        
        syncRemnantState(player);
    }

    public static boolean hasRemnant(UUID playerUUID) {
        return PLAYER_REMNANT_IDS.containsKey(playerUUID);
    }

    public static TimeRemnantEntity getRemnant(ServerPlayer player) {
        Integer entityId = PLAYER_REMNANT_IDS.get(player.getUUID());
        if (entityId == null) return null;
        Entity entity = player.level().getEntity(entityId);
        if (entity instanceof TimeRemnantEntity remnant && remnant.isAlive()) {
            return remnant;
        }
        return null;
    }

    public static int getRemainingSeconds(ServerPlayer player) {
        TimeRemnantEntity remnant = getRemnant(player);
        return remnant != null ? remnant.getRemainingSeconds() : 0;
    }

    private static void syncRemnantState(ServerPlayer player) {
        boolean hasRemnant = PLAYER_REMNANT_IDS.containsKey(player.getUUID());
        int remainingSeconds = hasRemnant ? getRemainingSeconds(player) : 0;
        PacketDistributor.sendToPlayer(player, new RemnantStatePayload(hasRemnant, remainingSeconds));
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        net.minecraft.server.MinecraftServer server = event.getServer();
        java.util.List<ServerPlayer> needsSync = new java.util.ArrayList<>();

        PLAYER_REMNANT_IDS.entrySet().removeIf(entry -> {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) return true;
            
            Entity entity = player.level().getEntity(entry.getValue());
            if (entity == null || !entity.isAlive()) {
                needsSync.add(player);
                return true;
            }
            return false;
        });

        for (ServerPlayer player : needsSync) {
            syncRemnantState(player);
        }
    }

    public static void cleanup() {
        PLAYER_REMNANT_IDS.clear();
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/network/TimeRemnantPayload.java
// ============================================================================
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TimeRemnantPayload(boolean summon) implements CustomPacketPayload {
    public static final Type<TimeRemnantPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "time_remnant"));
    
    public static final StreamCodec<ByteBuf, TimeRemnantPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, TimeRemnantPayload::summon,
        TimeRemnantPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/network/RemnantStatePayload.java
// ============================================================================
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RemnantStatePayload(boolean hasRemnant, int remainingSeconds) implements CustomPacketPayload {
    public static final Type<RemnantStatePayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "remnant_state"));
    
    public static final StreamCodec<ByteBuf, RemnantStatePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, RemnantStatePayload::hasRemnant,
        ByteBufCodecs.VAR_INT, RemnantStatePayload::remainingSeconds,
        RemnantStatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/client/QuiverHudOverlay.java
// ============================================================================
package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.item.ModItems;
import com.example.speedforce.item.QuiverItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class QuiverHudOverlay {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        boolean holdingBow = mainHand.getItem() == ModItems.GREEN_ARROW_BOW.get() || offHand.getItem() == ModItems.GREEN_ARROW_BOW.get();

        if (!holdingBow) return;

        ItemStack quiver = findQuiver(player);
        if (quiver.isEmpty()) return;

        GuiGraphics graphics = event.getGuiGraphics();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        NonNullList<ItemStack> arrows = QuiverItem.getInventory(quiver);
        int selectedSlot = QuiverItem.getSelectedSlot(quiver);

        int slotSize = 22;
        int startX = screenWidth - slotSize - 2;
        int startY = (screenHeight - (5 * slotSize)) / 2;

        for (int i = 0; i < 5; i++) {
            int y = startY + i * slotSize;

            graphics.fill(startX, y, startX + 20, y + 20, 0x80000000);

            if (i == selectedSlot) {
                graphics.fill(startX - 1, y - 1, startX + 21, y, 0xFFFFFFFF);
                graphics.fill(startX - 1, y + 20, startX + 21, y + 21, 0xFFFFFFFF);
                graphics.fill(startX - 1, y, startX, y + 20, 0xFFFFFFFF);
                graphics.fill(startX + 20, y, startX + 21, y + 20, 0xFFFFFFFF);
            } else {
                graphics.fill(startX - 1, y - 1, startX + 21, y, 0xFF555555);
                graphics.fill(startX - 1, y + 20, startX + 21, y + 21, 0xFF555555);
                graphics.fill(startX - 1, y, startX, y + 20, 0xFF555555);
                graphics.fill(startX + 20, y, startX + 21, y + 20, 0xFF555555);
            }

            ItemStack arrow = arrows.get(i);
            if (!arrow.isEmpty()) {
                graphics.renderItem(arrow, startX + 2, y + 2);
                graphics.renderItemDecorations(mc.font, arrow, startX + 2, y + 2);
            }
        }
    }

    private static ItemStack findQuiver(Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof QuiverItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/client/model/QuiverModel.java
// ============================================================================
package com.example.speedforce.client.model;

import com.example.speedforce.SpeedForceMod;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

public class QuiverModel extends EntityModel<Player> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
        ResourceLocation.fromNamespaceAndPath(SpeedForceMod.MOD_ID, "quiver_layer"), "main");

    private final ModelPart quiver;

    public QuiverModel(ModelPart root) {
        this.quiver = root.getChild("quiver");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition quiver = partdefinition.addOrReplaceChild("quiver", CubeListBuilder.create(), PartPose.offset(-4.0F, 0.0F, -2.0F));

        PartDefinition pad = quiver.addOrReplaceChild("pad", CubeListBuilder.create()
            .texOffs(0, 26).addBox(-0.1F, -0.1F, 0.1F, 5.0F, 5.0F, 1.0F), 
            PartPose.offset(-4.3F, -0.1F, 1.5F));

        pad.addOrReplaceChild("strap5", CubeListBuilder.create()
            .texOffs(13, 27).addBox(0.0F, 0.0F, 0.0F, 5.0F, 1.0F, 4.0F), 
            PartPose.offsetAndRotation(0.0F, 3.9F, -3.8F, 0.0F, (float)Math.PI / 90F, -0.12217305F));

        PartDefinition baseRight = pad.addOrReplaceChild("baseRight", CubeListBuilder.create()
            .texOffs(0, 15).addBox(-1.5F, 0.0F, 0.5F, 1.0F, 9.0F, 2.0F), 
            PartPose.offsetAndRotation(1.8F, 0.9F, 0.7F, 0.0F, 0.0F, -0.43633232F));

        baseRight.addOrReplaceChild("baseBack", CubeListBuilder.create()
            .texOffs(6, 16).addBox(0.0F, 0.0F, 0.0F, 2.0F, 9.0F, 1.0F), 
            PartPose.offset(-0.8F, 0.0F, 2.2F));

        baseRight.addOrReplaceChild("baseStrap", CubeListBuilder.create()
            .texOffs(0, 12).mirror().addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F), 
            PartPose.offset(-2.0F, 1.0F, 0.0F));

        baseRight.addOrReplaceChild("baseStrap_1", CubeListBuilder.create()
            .texOffs(0, 12).addBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 2.0F), 
            PartPose.offset(1.3F, 1.0F, 0.0F));

        baseRight.addOrReplaceChild("baseLeft", CubeListBuilder.create()
            .texOffs(0, 15).mirror().addBox(0.0F, 0.0F, 0.0F, 1.0F, 9.0F, 2.0F), 
            PartPose.offset(0.9F, 0.0F, 0.5F));

        PartDefinition baseFront = baseRight.addOrReplaceChild("baseFront", CubeListBuilder.create()
            .texOffs(12, 16).addBox(-1.0F, 0.0F, 0.0F, 2.0F, 9.0F, 1.0F), 
            PartPose.offset(0.2F, 0.0F, -0.1F));

        baseFront.addOrReplaceChild("baseBottom", CubeListBuilder.create()
            .texOffs(0, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 1.0F, 2.0F), 
            PartPose.offset(-1.0F, 8.7F, 1.6F));

        PartDefinition strap1 = pad.addOrReplaceChild("strap1", CubeListBuilder.create()
            .texOffs(0, 6).addBox(-4.0F, 0.0F, -4.0F, 9.0F, 1.0F, 5.0F), 
            PartPose.offsetAndRotation(4.0F, 2.5F, 0.0F, 0.0F, 0.0F, 0.62831855F));

        strap1.addOrReplaceChild("strap2", CubeListBuilder.create()
            .texOffs(5, 0).addBox(0.0F, -1.0F, -4.0F, 1.0F, 1.0F, 5.0F), 
            PartPose.offsetAndRotation(5.0F, 1.0F, 0.0F, 0.0F, (float)Math.PI / 180F, -0.62831855F));

        PartDefinition strap3 = strap1.addOrReplaceChild("strap3", CubeListBuilder.create()
            .texOffs(18, 1).addBox(-1.0F, -1.0F, -4.0F, 1.0F, 1.0F, 4.0F), 
            PartPose.offsetAndRotation(-4.0F, 1.0F, 0.1F, (float)Math.PI / 180F, (float)(-Math.PI) / 180F, 0.9599311F));

        strap3.addOrReplaceChild("shape4", CubeListBuilder.create()
            .texOffs(21, 4).addBox(-1.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F), 
            PartPose.offset(0.0F, 0.0F, -4.3F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(Player entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        quiver.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/client/render/QuiverLayer.java (v1.0.6v2)
// ============================================================================
package com.example.speedforce.client.render;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.client.model.QuiverModel;
import com.example.speedforce.item.QuiverItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class QuiverLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation QUIVER_TEXTURE = ResourceLocation.fromNamespaceAndPath(SpeedForceMod.MOD_ID, "textures/entity/quiver_dark_archer.png");
    private final QuiverModel quiverModel;

    public QuiverLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer, QuiverModel model) {
        super(renderer);
        this.quiverModel = model;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        ItemStack quiver = findQuiver(player);
        if (quiver.isEmpty()) return;

        poseStack.pushPose();
        this.getParentModel().body.translateAndRotate(poseStack);

        poseStack.translate(0.25D, 0.05D, 0.25D);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(QUIVER_TEXTURE));
        this.quiverModel.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);

        poseStack.popPose();
    }

    private ItemStack findQuiver(Player player) {
        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        
        if (mainHand.getItem() instanceof QuiverItem || offHand.getItem() instanceof QuiverItem) {
            return ItemStack.EMPTY;
        }
        
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof QuiverItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/client/ClientSetupEvents.java
// ============================================================================
package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.client.model.QuiverModel;
import com.example.speedforce.client.render.QuiverLayer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetupEvents {

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(QuiverModel.LAYER_LOCATION, QuiverModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void addPlayerLayers(EntityRenderersEvent.AddLayers event) {
        QuiverModel quiverModel = new QuiverModel(event.getEntityModels().bakeLayer(QuiverModel.LAYER_LOCATION));

        for (var skin : event.getSkins()) {
            var renderer = event.getSkin(skin);
            if (renderer instanceof PlayerRenderer playerRenderer) {
                playerRenderer.addLayer(new QuiverLayer(playerRenderer, quiverModel));
            }
        }
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/event/QuiverEventHandler.java
// ============================================================================
package com.example.speedforce.event;

import com.example.speedforce.item.QuiverItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ArrowLooseEvent;

@EventBusSubscriber(modid = "speedforce")
public class QuiverEventHandler {

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onArrowLoose(ArrowLooseEvent event) {
        if (event.isCanceled()) return;
        
        ItemStack bow = event.getBow();
        if (bow.getItem() != Items.BOW) {
            return;
        }

        ItemStack quiver = findQuiver(event.getEntity());
        if (quiver.isEmpty()) return;

        ItemStack selectedArrow = QuiverItem.getSelectedArrow(quiver);
        if (selectedArrow.isEmpty()) return;

        QuiverItem.consumeArrow(quiver, 1);
    }

    private static ItemStack findQuiver(net.minecraft.world.entity.player.Player player) {
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof QuiverItem) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
}
package com.example.speedforce.item;

import com.example.speedforce.entity.ModEntityTypes;
import com.example.speedforce.entity.NormalArrowEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NormalArrowItem extends ArrowItem {

    public NormalArrowItem(Properties properties) {
        super(properties);
    }

    @Override
    public AbstractArrow createArrow(Level level, ItemStack ammoStack, LivingEntity shooter, ItemStack weapon) {
        NormalArrowEntity arrow = new NormalArrowEntity(level, shooter, ammoStack, weapon);
        return arrow;
    }
}
package com.example.speedforce.entity;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
        DeferredRegister.create(Registries.ENTITY_TYPE, SpeedForceMod.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<NormalArrowEntity>> NORMAL_ARROW =
        ENTITY_TYPES.register("normal_arrow", () ->
            EntityType.Builder.<NormalArrowEntity>of(NormalArrowEntity::new, MobCategory.MISC)
                .sized(0.5F, 0.5F)
                .clientTrackingRange(4)
                .updateInterval(20)
                .build(SpeedForceMod.MOD_ID + ":normal_arrow")
        );

    public static final DeferredHolder<EntityType<?>, EntityType<TimeRemnantEntity>> TIME_REMNANT =
        ENTITY_TYPES.register("time_remnant", () ->
            EntityType.Builder.<TimeRemnantEntity>of(TimeRemnantEntity::new, MobCategory.CREATURE)
                .sized(0.6F, 1.8F)
                .clientTrackingRange(10)
                .updateInterval(1)
                .build(SpeedForceMod.MOD_ID + ":time_remnant")
        );
}
package com.example.speedforce.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class NormalArrowEntity extends AbstractArrow {

    public NormalArrowEntity(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        this.setBaseDamage(this.getBaseDamage() * 2.0);
    }

    public NormalArrowEntity(Level level, LivingEntity shooter, ItemStack pickupItem, ItemStack weapon) {
        super(ModEntityTypes.NORMAL_ARROW.get(), shooter, level, pickupItem, weapon);
        this.setBaseDamage(this.getBaseDamage() * 2.0);
    }

    public NormalArrowEntity(Level level, double x, double y, double z, ItemStack pickupItem, ItemStack weapon) {
        super(ModEntityTypes.NORMAL_ARROW.get(), x, y, z, level, pickupItem, weapon);
        this.setBaseDamage(this.getBaseDamage() * 2.0);
    }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(com.example.speedforce.item.ModItems.NORMAL_ARROW.get());
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/entity/TimeRemnantEntity.java
// ============================================================================
package com.example.speedforce.entity;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.world.phys.Vec3;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;

import java.util.*;

public class TimeRemnantEntity extends PathfinderMob {

    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> SPEED_LEVEL = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TRAIL_COLOR_R = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TRAIL_COLOR_G = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> TRAIL_COLOR_B = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_SPRINTING = SynchedEntityData.defineId(TimeRemnantEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int DEFAULT_DURATION = 2400;
    private static final ResourceLocation SPEED_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("speedforce", "remnant_speed");
    private static final ResourceLocation ATTACK_MODIFIER_ID = ResourceLocation.fromNamespaceAndPath("speedforce", "remnant_attack");

    public int remainingTicks = DEFAULT_DURATION;
    private LivingEntity ownerLastTarget = null;
    private List<Vec3> trailHistory = new ArrayList<>();
    private static final int MAX_TRAIL_HISTORY = 15;

    public TimeRemnantEntity(EntityType<? extends PathfinderMob> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(PathType.WATER, 0.0F);
        ((GroundPathNavigation) this.getNavigation()).setCanOpenDoors(true);
        this.setMaxUpStep(1.5F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
            .add(Attributes.MOVEMENT_SPEED, 0.3)
            .add(Attributes.MAX_HEALTH, 20.0)
            .add(Attributes.ATTACK_DAMAGE, 2.0)
            .add(Attributes.FOLLOW_RANGE, 32.0)
            .add(Attributes.ATTACK_SPEED, 4.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(OWNER_UUID, Optional.empty());
        builder.define(SPEED_LEVEL, 4);
        builder.define(TRAIL_COLOR_R, 255);
        builder.define(TRAIL_COLOR_G, 210);
        builder.define(TRAIL_COLOR_B, 0);
        builder.define(IS_SPRINTING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 2.0, true));
        this.goalSelector.addGoal(2, new FollowOwnerGoal(this, 1.5, 10.0F, 2.0F));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.5));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
    }

    public void setOwner(Player owner) {
        this.entityData.set(OWNER_UUID, Optional.of(owner.getUUID()));
        this.setCustomName(owner.getName());
        this.setCustomNameVisible(true);
        
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            this.setItemSlot(slot, owner.getItemBySlot(slot).copy());
            this.setDropChance(slot, 0.0F);
        }
        
        SpeedPlayerData data = owner.getData(ModAttachments.SPEED_PLAYER);
        this.entityData.set(SPEED_LEVEL, data.speedLevel);
        this.entityData.set(TRAIL_COLOR_R, data.trailColorR);
        this.entityData.set(TRAIL_COLOR_G, data.trailColorG);
        this.entityData.set(TRAIL_COLOR_B, data.trailColorB);
        
        this.getAttribute(Attributes.MAX_HEALTH).setBaseValue(owner.getMaxHealth());
        this.setHealth(owner.getMaxHealth());
        
        applySpeedForceAttributes();
    }

    public UUID getOwnerUUID() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public Player getOwner() {
        UUID uuid = getOwnerUUID();
        if (uuid == null) return null;
        return this.level().getPlayerByUUID(uuid);
    }

    public int getSpeedLevel() {
        return this.entityData.get(SPEED_LEVEL);
    }

    public int getTrailColorR() {
        return this.entityData.get(TRAIL_COLOR_R);
    }

    public int getTrailColorG() {
        return this.entityData.get(TRAIL_COLOR_G);
    }

    public int getTrailColorB() {
        return this.entityData.get(TRAIL_COLOR_B);
    }

    private void applySpeedForceAttributes() {
        int speedLevel = getSpeedLevel();
        if (speedLevel <= 0) return;

        AttributeInstance speedAttr = getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr != null) {
            double amount = speedLevel * 0.3;
            AttributeModifier modifier = new AttributeModifier(SPEED_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE);
            speedAttr.removeModifier(SPEED_MODIFIER_ID);
            speedAttr.addTransientModifier(modifier);
        }

        AttributeInstance attackAttr = getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackAttr != null) {
            double amount = speedLevel * 0.5;
            AttributeModifier modifier = new AttributeModifier(ATTACK_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE);
            attackAttr.removeModifier(ATTACK_MODIFIER_ID);
            attackAttr.addTransientModifier(modifier);
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            remainingTicks--;
            if (remainingTicks <= 0) {
                this.discard();
                return;
            }

            boolean isMoving = this.position().distanceToSqr(this.xOld, this.yOld, this.zOld) > 0.001;
            this.entityData.set(IS_SPRINTING, isMoving);
        }

        updateTrailHistory();
    }

    private void updateTrailHistory() {
        Vec3 currentPos = this.position();
        trailHistory.add(0, currentPos);
        if (trailHistory.size() > MAX_TRAIL_HISTORY) {
            trailHistory.remove(trailHistory.size() - 1);
        }
    }

    public List<Vec3> getTrailHistory() {
        return trailHistory;
    }

    public boolean shouldRenderTrail() {
        return this.entityData.get(IS_SPRINTING) && getSpeedLevel() > 0;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        UUID uuid = getOwnerUUID();
        if (uuid != null) {
            tag.putUUID("OwnerUUID", uuid);
        }
        tag.putInt("RemainingTicks", remainingTicks);
        tag.putInt("SpeedLevel", getSpeedLevel());
        tag.putInt("TrailColorR", getTrailColorR());
        tag.putInt("TrailColorG", getTrailColorG());
        tag.putInt("TrailColorB", getTrailColorB());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID("OwnerUUID")) {
            this.entityData.set(OWNER_UUID, Optional.of(tag.getUUID("OwnerUUID")));
        }
        remainingTicks = tag.getInt("RemainingTicks");
        if (tag.contains("SpeedLevel")) {
            this.entityData.set(SPEED_LEVEL, tag.getInt("SpeedLevel"));
        }
        if (tag.contains("TrailColorR")) {
            this.entityData.set(TRAIL_COLOR_R, tag.getInt("TrailColorR"));
        }
        if (tag.contains("TrailColorG")) {
            this.entityData.set(TRAIL_COLOR_G, tag.getInt("TrailColorG"));
        }
        if (tag.contains("TrailColorB")) {
            this.entityData.set(TRAIL_COLOR_B, tag.getInt("TrailColorB"));
        }
        applySpeedForceAttributes();
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        return InteractionResult.PASS;
    }

    @Override
    public boolean canBeLeashed() {
        return false;
    }

    @Override
    public boolean canPickUpLoot() {
        return false;
    }

    @Override
    protected void dropCustomDeathLoot(ServerLevel level, DamageSource source, boolean recentlyHit) {
    }

    public int getRemainingSeconds() {
        return remainingTicks / 20;
    }

    public static class FollowOwnerGoal extends Goal {
        private final TimeRemnantEntity remnant;
        private final double speedModifier;
        private final float stopDistance;
        private final float startDistance;
        private Player owner;
        private int timeToRecalcPath;

        public FollowOwnerGoal(TimeRemnantEntity remnant, double speedModifier, float startDistance, float stopDistance) {
            this.remnant = remnant;
            this.speedModifier = speedModifier;
            this.startDistance = startDistance;
            this.stopDistance = stopDistance;
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            owner = remnant.getOwner();
            if (owner == null) return false;
            if (remnant.getTarget() != null) return false;
            return remnant.distanceToSqr(owner) >= startDistance * startDistance;
        }

        @Override
        public boolean canContinueToUse() {
            if (owner == null) return false;
            if (remnant.getTarget() != null) return false;
            return remnant.distanceToSqr(owner) > stopDistance * stopDistance;
        }

        @Override
        public void start() {
            this.timeToRecalcPath = 0;
        }

        @Override
        public void stop() {
            owner = null;
            remnant.getNavigation().stop();
        }

        @Override
        public void tick() {
            if (owner != null) {
                remnant.getLookControl().setLookAt(owner, 10.0F, remnant.getMaxHeadXRot());
                if (--this.timeToRecalcPath <= 0) {
                    this.timeToRecalcPath = this.adjustedTickDelay(10);
                    remnant.getNavigation().moveTo(owner, speedModifier);
                }
            }
        }
    }

    public static class OwnerHurtTargetGoal extends Goal {
        private final TimeRemnantEntity remnant;
        private LivingEntity target;

        public OwnerHurtTargetGoal(TimeRemnantEntity remnant) {
            this.remnant = remnant;
        }

        @Override
        public boolean canUse() {
            Player owner = remnant.getOwner();
            if (owner == null) return false;
            target = owner.getLastHurtMob();
            if (target == null) return false;
            if (target instanceof Player && ((Player) target).isCreative()) return false;
            return remnant.canAttack(target);
        }

        @Override
        public void start() {
            remnant.setTarget(target);
        }
    }
}
package com.example.speedforce.client;

import com.example.speedforce.network.BulletTimePayload;
import com.example.speedforce.network.PhasingPayload;
import com.example.speedforce.network.SpeedLevelPayload;
import com.example.speedforce.network.TogglePowerPayload;
import com.example.speedforce.particle.ModParticles;
import com.example.speedforce.client.particle.YellowFlashParticle;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "speedforce", value = Dist.CLIENT)
public class ClientKeybinds {
    public static final KeyMapping TOGGLE_POWER_KEY = new KeyMapping(
        "key.speedforce.toggle_power",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_C,
        "category.speedforce.keys"
    );

    public static final KeyMapping SPEED_UP_KEY = new KeyMapping(
        "key.speedforce.speed_up",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_X,
        "category.speedforce.keys"
    );

    public static final KeyMapping SPEED_DOWN_KEY = new KeyMapping(
        "key.speedforce.speed_down",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_Z,
        "category.speedforce.keys"
    );

    public static final KeyMapping BULLET_TIME_KEY = new KeyMapping(
        "key.speedforce.bullet_time",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_B,
        "category.speedforce.keys"
    );

    public static final KeyMapping PHASING_KEY = new KeyMapping(
        "key.speedforce.phasing",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_V,
        "category.speedforce.keys"
    );

    public static final KeyMapping COLOR_PALETTE_KEY = new KeyMapping(
        "key.speedforce.color_palette",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_N,
        "category.speedforce.keys"
    );

    public static final KeyMapping TOGGLE_HELP_KEY = new KeyMapping(
        "key.speedforce.toggle_help",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_U,
        "category.speedforce.keys"
    );

    public static final KeyMapping REWIND_KEY = new KeyMapping(
        "key.speedforce.rewind",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_R,
        "category.speedforce.keys"
    );

    public static final KeyMapping TIME_REMNANT_KEY = new KeyMapping(
        "key.speedforce.time_remnant",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_H,
        "category.speedforce.keys"
    );

    public static final KeyMapping CYCLE_QUIVER_KEY = new KeyMapping(
        "key.speedforce.cycle_quiver",
        InputConstants.Type.KEYSYM,
        GLFW.GLFW_KEY_G,
        "category.speedforce.keys"
    );

    private static boolean wasRewinding = false;

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        while (TOGGLE_POWER_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new TogglePowerPayload());
        }

        while (SPEED_UP_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new SpeedLevelPayload(true));
        }

        while (SPEED_DOWN_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new SpeedLevelPayload(false));
        }

        while (BULLET_TIME_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new BulletTimePayload());
        }

        while (PHASING_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new PhasingPayload());
        }

        while (COLOR_PALETTE_KEY.consumeClick()) {
            if (ClientSpeedData.hasPower) {
                mc.setScreen(new ColorPaletteScreen());
            }
        }

        while (TOGGLE_HELP_KEY.consumeClick()) {
            ClientSpeedData.showHelp = !ClientSpeedData.showHelp;
        }

        while (CYCLE_QUIVER_KEY.consumeClick()) {
            PacketDistributor.sendToServer(new com.example.speedforce.network.CycleQuiverPayload());
        }

        while (TIME_REMNANT_KEY.consumeClick()) {
            if (ClientSpeedData.hasPower && ClientSpeedData.speedLevel >= 4) {
                PacketDistributor.sendToServer(new com.example.speedforce.network.TimeRemnantPayload(true));
            }
        }

        boolean isRewindingNow = REWIND_KEY.isDown() && ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0;
        if (isRewindingNow != wasRewinding) {
            PacketDistributor.sendToServer(new com.example.speedforce.network.RewindPayload(isRewindingNow));
            wasRewinding = isRewindingNow;
        }

        if (ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0) {
            if (isRewindingNow) {
                ClientSpeedData.clientHistorySize = Math.max(0, ClientSpeedData.clientHistorySize - 1);
            } else {
                ClientSpeedData.clientHistorySize = Math.min(200, ClientSpeedData.clientHistorySize + 1);
            }
        } else {
            ClientSpeedData.clientHistorySize = 0;
        }
    }
}

@EventBusSubscriber(modid = "speedforce", value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
class ClientModEvents {
    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(ClientKeybinds.TOGGLE_POWER_KEY);
        event.register(ClientKeybinds.SPEED_UP_KEY);
        event.register(ClientKeybinds.SPEED_DOWN_KEY);
        event.register(ClientKeybinds.BULLET_TIME_KEY);
        event.register(ClientKeybinds.PHASING_KEY);
        event.register(ClientKeybinds.COLOR_PALETTE_KEY);
        event.register(ClientKeybinds.TOGGLE_HELP_KEY);
        event.register(ClientKeybinds.REWIND_KEY);
        event.register(ClientKeybinds.TIME_REMNANT_KEY);
        event.register(ClientKeybinds.CYCLE_QUIVER_KEY);
    }

    @SubscribeEvent
    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.YELLOW_FLASH.get(), YellowFlashParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(
            com.example.speedforce.entity.ModEntityTypes.NORMAL_ARROW.get(), 
            com.example.speedforce.client.renderer.NormalArrowRenderer::new
        );
        event.registerEntityRenderer(
            com.example.speedforce.entity.ModEntityTypes.TIME_REMNANT.get(), 
            com.example.speedforce.client.renderer.TimeRemnantRenderer::new
        );
    }

    @SubscribeEvent
    public static void onClientSetup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            net.minecraft.client.renderer.item.ItemProperties.register(
                com.example.speedforce.item.ModItems.GREEN_ARROW_BOW.get(),
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "pull"),
                (stack, level, entity, seed) -> {
                    if (entity == null) return 0.0F;
                    return entity.getUseItem() != stack ? 0.0F : 
                        (float)(stack.getUseDuration(entity) - entity.getUseItemRemainingTicks()) / 20.0F;
                }
            );
            net.minecraft.client.renderer.item.ItemProperties.register(
                com.example.speedforce.item.ModItems.GREEN_ARROW_BOW.get(),
                net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("minecraft", "pulling"),
                (stack, level, entity, seed) -> 
                    entity != null && entity.isUsingItem() && entity.getUseItem() == stack ? 1.0F : 0.0F
            );
        });
    }
}
package com.example.speedforce.client.renderer;

import com.example.speedforce.entity.NormalArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class NormalArrowRenderer extends ArrowRenderer<NormalArrowEntity> {
    
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath("speedforce", "textures/entity/projectiles/normal_arrow.png");

    public NormalArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(NormalArrowEntity entity) {
        return TEXTURE;
    }
}
package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.item.FlashSuitArmorItem;
import com.example.speedforce.item.SuitType;
import com.example.speedforce.network.ModNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = "speedforce")
public class PowerTickHandler {

    private static final ResourceLocation SPEED_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("speedforce", "speed_boost");
    private static final ResourceLocation ATTACK_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("speedforce", "attack_boost");
    private static final ResourceLocation ATTACK_SPEED_MODIFIER_ID = 
        ResourceLocation.fromNamespaceAndPath("speedforce", "attack_speed_boost");

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide) return;

        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);

        if (!data.hasPower) {
            removeSpeedModifier(player);
            removeAttackModifier(player);
            removeAttackSpeedModifier(player);
            if (!player.isSpectator()) {
                player.noPhysics = false;
                player.setNoGravity(false);
            }
            if (data.isBulletTimeActive || data.isPhasing) {
                data.isBulletTimeActive = false;
                data.isPhasing = false;
                player.setData(ModAttachments.SPEED_PLAYER, data);
                if (player instanceof ServerPlayer serverPlayer) {
                    ModNetworking.syncToClient(serverPlayer);
                }
            }
            return;
        }

        SuitType wornSuit = getWornSuitType(player);
        handleSuitColorUpdate(player, data, wornSuit);
        
        int maxLevel = wornSuit != null ? 10 + wornSuit.getSpeedBonus() : 10;
        if (data.speedLevel > maxLevel) {
            data.speedLevel = maxLevel;
            player.setData(ModAttachments.SPEED_PLAYER, data);
        }

        applySpeedModifier(player, data);
        applyAttackModifier(player, data);
        applyAttackSpeedModifier(player, data);
        handleRegeneration(player, data);
        handleWaterWalk(player, data);
        handleWallRun(player, data);
        handleSpeedFire(player, data, wornSuit);
        handlePhasing(player, data);

        if (player.tickCount % 20 == 0 && player instanceof ServerPlayer serverPlayer) {
            ModNetworking.syncToClient(serverPlayer);
        }
    }

    private static void handleSuitColorUpdate(Player player, SpeedPlayerData data, SuitType suitType) {
        boolean colorChanged = false;
        if (suitType != null) {
            if (data.trailColorR != suitType.getTrailColorR()) {
                data.trailColorR = suitType.getTrailColorR();
                colorChanged = true;
            }
            if (data.trailColorG != suitType.getTrailColorG()) {
                data.trailColorG = suitType.getTrailColorG();
                colorChanged = true;
            }
            if (data.trailColorB != suitType.getTrailColorB()) {
                data.trailColorB = suitType.getTrailColorB();
                colorChanged = true;
            }
        } else {
            if (data.trailColorR != data.customTrailColorR) {
                data.trailColorR = data.customTrailColorR;
                colorChanged = true;
            }
            if (data.trailColorG != data.customTrailColorG) {
                data.trailColorG = data.customTrailColorG;
                colorChanged = true;
            }
            if (data.trailColorB != data.customTrailColorB) {
                data.trailColorB = data.customTrailColorB;
                colorChanged = true;
            }
        }
        if (colorChanged) {
            player.setData(ModAttachments.SPEED_PLAYER, data);
        }
    }

    private static SuitType getWornSuitType(Player player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        if (helmet.getItem() instanceof FlashSuitArmorItem helmetItem &&
            chestplate.getItem() instanceof FlashSuitArmorItem chestplateItem &&
            leggings.getItem() instanceof FlashSuitArmorItem leggingsItem &&
            boots.getItem() instanceof FlashSuitArmorItem bootsItem) {
            
            SuitType type = helmetItem.getSuitType();
            if (chestplateItem.getSuitType() == type &&
                leggingsItem.getSuitType() == type &&
                bootsItem.getSuitType() == type) {
                return type;
            }
        }
        return null;
    }

    private static float lastTickRate = 20.0F;

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        int maxBulletTimeLevel = 0;

        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
            if (data.hasPower && data.speedLevel > 0 && data.isBulletTimeActive) {
                maxBulletTimeLevel = Math.max(maxBulletTimeLevel, data.speedLevel);
            }
        }

        float targetTickRate;
        if (maxBulletTimeLevel > 0) {
            targetTickRate = 20.0F / (3.0F + maxBulletTimeLevel);
            targetTickRate = Math.max(0.5F, targetTickRate);
        } else {
            targetTickRate = 20.0F;
        }

        if (lastTickRate != targetTickRate) {
            server.tickRateManager().setTickRate(targetTickRate);
            lastTickRate = targetTickRate;
        }
    }

    private static void applySpeedModifier(Player player, SpeedPlayerData data) {
        var instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null) {
            double amount = data.speedLevel * 0.3;
            if (data.isBulletTimeActive && data.speedLevel > 0) {
                amount *= (3.0 + data.speedLevel);
            }
            
            AttributeModifier modifier = new AttributeModifier(
                SPEED_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE
            );
            if (!instance.hasModifier(SPEED_MODIFIER_ID)) {
                instance.addTransientModifier(modifier);
            } else if (instance.getModifier(SPEED_MODIFIER_ID).amount() != amount) {
                instance.removeModifier(SPEED_MODIFIER_ID);
                instance.addTransientModifier(modifier);
            }
        }
    }

    private static void removeSpeedModifier(Player player) {
        var instance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (instance != null && instance.hasModifier(SPEED_MODIFIER_ID)) {
            instance.removeModifier(SPEED_MODIFIER_ID);
        }
    }

    private static void applyAttackModifier(Player player, SpeedPlayerData data) {
        if (data.speedLevel <= 0) {
            removeAttackModifier(player);
            return;
        }
        var instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance != null) {
            double amount = data.speedLevel * 0.5;
            AttributeModifier modifier = new AttributeModifier(
                ATTACK_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE
            );
            if (!instance.hasModifier(ATTACK_MODIFIER_ID)) {
                instance.addTransientModifier(modifier);
            } else if (instance.getModifier(ATTACK_MODIFIER_ID).amount() != amount) {
                instance.removeModifier(ATTACK_MODIFIER_ID);
                instance.addTransientModifier(modifier);
            }
        }
    }

    private static void removeAttackModifier(Player player) {
        var instance = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (instance != null && instance.hasModifier(ATTACK_MODIFIER_ID)) {
            instance.removeModifier(ATTACK_MODIFIER_ID);
        }
    }

    private static void applyAttackSpeedModifier(Player player, SpeedPlayerData data) {
        var instance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (instance != null) {
            double amount = 0;
            if (data.speedLevel > 0 && data.isBulletTimeActive) {
                amount = 4.0 * (2.0 + data.speedLevel);
            }
            
            if (amount > 0) {
                AttributeModifier modifier = new AttributeModifier(
                    ATTACK_SPEED_MODIFIER_ID, amount, AttributeModifier.Operation.ADD_VALUE
                );
                if (!instance.hasModifier(ATTACK_SPEED_MODIFIER_ID)) {
                    instance.addTransientModifier(modifier);
                } else if (instance.getModifier(ATTACK_SPEED_MODIFIER_ID).amount() != amount) {
                    instance.removeModifier(ATTACK_SPEED_MODIFIER_ID);
                    instance.addTransientModifier(modifier);
                }
            } else {
                removeAttackSpeedModifier(player);
            }
        }
    }

    private static void removeAttackSpeedModifier(Player player) {
        var instance = player.getAttribute(Attributes.ATTACK_SPEED);
        if (instance != null && instance.hasModifier(ATTACK_SPEED_MODIFIER_ID)) {
            instance.removeModifier(ATTACK_SPEED_MODIFIER_ID);
        }
    }

    private static void handleRegeneration(Player player, SpeedPlayerData data) {
        if (data.speedLevel > 0 && player.tickCount % 40 == 0 && player.getHealth() < player.getMaxHealth()) {
            player.heal(1.0F + data.speedLevel * 0.2F);
        }
    }

    private static void handleWaterWalk(Player player, SpeedPlayerData data) {
        if (data.speedLevel >= 3 && player.isSprinting()) {
            BlockPos posBelow = player.blockPosition().below();
            if (player.level().getFluidState(posBelow).isEmpty() == false) {
                player.setDeltaMovement(player.getDeltaMovement().x, 0, player.getDeltaMovement().z);
                player.fallDistance = 0.0F;
                player.setOnGround(true);
            }
        }
    }

    private static void handleWallRun(Player player, SpeedPlayerData data) {
        if (data.speedLevel >= 4 && player.isSprinting() && player.horizontalCollision) {
            player.setDeltaMovement(player.getDeltaMovement().x, 0.4, player.getDeltaMovement().z);
            player.fallDistance = 0.0F;
        }
    }

    private static void handleSpeedFire(Player player, SpeedPlayerData data, SuitType suitType) {
        if (data.speedLevel >= 5 && player.isSprinting()) {
            if (suitType == null) {
                player.setRemainingFireTicks(60);
            }
        }
    }

    private static void handlePhasing(Player player, SpeedPlayerData data) {
        if (data.isPhasing) {
            player.noPhysics = true;
            player.setNoGravity(true);
            player.fallDistance = 0.0F;
        } else if (!player.isSpectator()) {
            player.noPhysics = false;
            player.setNoGravity(false);
        }
    }
}
package com.example.speedforce.event;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.network.ModNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityStruckByLightningEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@EventBusSubscriber(modid = "speedforce")
public class ModEvents {

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (!player.level().isClientSide && player.hasEffect(MobEffects.POISON)) {
                if (player.getRandom().nextFloat() < 0.3f) {
                    player.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(true, 1));
                    player.removeEffect(MobEffects.POISON);
                    if (player instanceof ServerPlayer serverPlayer) {
                        ModNetworking.syncToClient(serverPlayer);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ModNetworking.syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ModNetworking.syncToClient(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerChangeDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            ModNetworking.syncToClient(serverPlayer);
        }
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/event/ModBusEvents.java
// 修复：实体属性注册必须在 MOD 总线上
// ============================================================================
package com.example.speedforce.event;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.entity.ModEntityTypes;
import com.example.speedforce.entity.TimeRemnantEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModBusEvents {

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntityTypes.TIME_REMNANT.get(), TimeRemnantEntity.createAttributes().build());
    }
}
package com.example.speedforce.capability;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = 
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, "speedforce");

    public static final Supplier<AttachmentType<SpeedPlayerData>> SPEED_PLAYER = 
        ATTACHMENT_TYPES.register("speed_player",
            () -> AttachmentType.builder(() -> new SpeedPlayerData())
                .serialize(SpeedPlayerData.CODEC)
                .copyOnDeath()
                .build());
}
package com.example.speedforce.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SpeedPlayerData {
    public boolean hasPower = false;
    public int speedLevel = 0;
    public boolean isBulletTimeActive = false;
    public boolean isPhasing = false;
    public int trailColorR = 255;
    public int trailColorG = 210;
    public int trailColorB = 0;
    public int customTrailColorR = 255;
    public int customTrailColorG = 210;
    public int customTrailColorB = 0;

    public SpeedPlayerData() {}

    public SpeedPlayerData(boolean hasPower, int speedLevel) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
    }

    public SpeedPlayerData(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
        this.isBulletTimeActive = isBulletTimeActive;
        this.isPhasing = isPhasing;
    }

    public SpeedPlayerData(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing, int trailColorR, int trailColorG, int trailColorB) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
        this.isBulletTimeActive = isBulletTimeActive;
        this.isPhasing = isPhasing;
        this.trailColorR = trailColorR;
        this.trailColorG = trailColorG;
        this.trailColorB = trailColorB;
        this.customTrailColorR = trailColorR;
        this.customTrailColorG = trailColorG;
        this.customTrailColorB = trailColorB;
    }

    public SpeedPlayerData(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing, 
                           int trailColorR, int trailColorG, int trailColorB,
                           int customTrailColorR, int customTrailColorG, int customTrailColorB) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
        this.isBulletTimeActive = isBulletTimeActive;
        this.isPhasing = isPhasing;
        this.trailColorR = trailColorR;
        this.trailColorG = trailColorG;
        this.trailColorB = trailColorB;
        this.customTrailColorR = customTrailColorR;
        this.customTrailColorG = customTrailColorG;
        this.customTrailColorB = customTrailColorB;
    }

    public static final Codec<SpeedPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("hasPower", false).forGetter(d -> d.hasPower),
        Codec.INT.optionalFieldOf("speedLevel", 0).forGetter(d -> d.speedLevel),
        Codec.BOOL.optionalFieldOf("isBulletTimeActive", false).forGetter(d -> d.isBulletTimeActive),
        Codec.BOOL.optionalFieldOf("isPhasing", false).forGetter(d -> d.isPhasing),
        Codec.INT.optionalFieldOf("trailColorR", 255).forGetter(d -> d.trailColorR),
        Codec.INT.optionalFieldOf("trailColorG", 210).forGetter(d -> d.trailColorG),
        Codec.INT.optionalFieldOf("trailColorB", 0).forGetter(d -> d.trailColorB),
        Codec.INT.optionalFieldOf("customTrailColorR", 255).forGetter(d -> d.customTrailColorR),
        Codec.INT.optionalFieldOf("customTrailColorG", 210).forGetter(d -> d.customTrailColorG),
        Codec.INT.optionalFieldOf("customTrailColorB", 0).forGetter(d -> d.customTrailColorB)
    ).apply(instance, SpeedPlayerData::new));
}
package com.example.speedforce.command;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.network.ModNetworking;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SpeedForceCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("speedforce")
            .requires(source -> source.hasPermission(2))
            .then(Commands.literal("grant")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    grantPower(player, 1);
                    context.getSource().sendSuccess(() -> Component.translatable("message.speedforce.granted"), false);
                    return 1;
                })
                .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                    .executes(context -> {
                        ServerPlayer player = context.getSource().getPlayerOrException();
                        int level = IntegerArgumentType.getInteger(context, "level");
                        grantPower(player, level);
                        context.getSource().sendSuccess(() -> 
                            Component.translatable("message.speedforce.granted_level", level), false);
                        return 1;
                    }))
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        ServerPlayer target = EntityArgument.getPlayer(context, "player");
                        grantPower(target, 1);
                        context.getSource().sendSuccess(() -> 
                            Component.translatable("message.speedforce.granted_to", target.getName().getString()), false);
                        return 1;
                    }))
                .then(Commands.argument("player", EntityArgument.player())
                    .then(Commands.argument("level", IntegerArgumentType.integer(1, 10))
                        .executes(context -> {
                            ServerPlayer target = EntityArgument.getPlayer(context, "player");
                            int level = IntegerArgumentType.getInteger(context, "level");
                            grantPower(target, level);
                            context.getSource().sendSuccess(() -> 
                                Component.translatable("message.speedforce.granted_to_level", target.getName().getString(), level), false);
                            return 1;
                        }))))
            .then(Commands.literal("revoke")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    revokePower(player);
                    context.getSource().sendSuccess(() -> Component.translatable("message.speedforce.revoked"), false);
                    return 1;
                })
                .then(Commands.argument("player", EntityArgument.player())
                    .executes(context -> {
                        ServerPlayer target = EntityArgument.getPlayer(context, "player");
                        revokePower(target);
                        context.getSource().sendSuccess(() -> 
                            Component.translatable("message.speedforce.revoked_from", target.getName().getString()), false);
                        return 1;
                    })))
            .then(Commands.literal("info")
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    var data = player.getData(ModAttachments.SPEED_PLAYER);
                    context.getSource().sendSuccess(() -> 
                        Component.translatable("message.speedforce.info", 
                            data.hasPower ? "Yes" : "No", 
                            data.speedLevel,
                            data.trailColorR, data.trailColorG, data.trailColorB), false);
                    return 1;
                }))
            .then(Commands.literal("color")
                .requires(source -> source.hasPermission(0))
                .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                    .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                        .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                            .executes(context -> {
                                ServerPlayer player = context.getSource().getPlayerOrException();
                                int r = IntegerArgumentType.getInteger(context, "r");
                                int g = IntegerArgumentType.getInteger(context, "g");
                                int b = IntegerArgumentType.getInteger(context, "b");
                                SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                                if (!data.hasPower) {
                                    context.getSource().sendFailure(Component.translatable("message.speedforce.no_power"));
                                    return 0;
                                }
                                data.trailColorR = r;
                                data.trailColorG = g;
                                data.trailColorB = b;
                                player.setData(ModAttachments.SPEED_PLAYER, data);
                                ModNetworking.syncToClient(player);
                                context.getSource().sendSuccess(() -> 
                                    Component.translatable("message.speedforce.color_set", r, g, b), false);
                                return 1;
                            })))))
        );
    }

    private static void grantPower(ServerPlayer player, int level) {
        player.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(true, level));
        ModNetworking.syncToClient(player);
    }

    private static void revokePower(ServerPlayer player) {
        player.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(false, 0));
        ModNetworking.syncToClient(player);
    }
}
package com.example.speedforce.block;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(SpeedForceMod.MOD_ID);

    public static final DeferredBlock<ParticleAcceleratorBlock> PARTICLE_ACCELERATOR = 
        BLOCKS.register("particle_accelerator", 
            () -> new ParticleAcceleratorBlock(Block.Properties.ofFullCopy(Blocks.IRON_BLOCK).noOcclusion()));
}
package com.example.speedforce.block;

import com.example.speedforce.block.entity.ModBlockEntities;
import com.example.speedforce.block.entity.ParticleAcceleratorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class ParticleAcceleratorBlock extends Block implements EntityBlock {

    public ParticleAcceleratorBlock(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ParticleAcceleratorBlockEntity(pos, state);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, 
                                  Player player, BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof ParticleAcceleratorBlockEntity accelerator) {
                if (accelerator.canActivate(player)) {
                    accelerator.startActivation(player);
                    return InteractionResult.SUCCESS;
                }
            }
        }
        return InteractionResult.CONSUME;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, 
                                                                    BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) return null;
        return createTickerHelper(blockEntityType, ModBlockEntities.PARTICLE_ACCELERATOR.get(), 
            ParticleAcceleratorBlockEntity::tick);
    }

    @SuppressWarnings("unchecked")
    private static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(
            BlockEntityType<A> type, BlockEntityType<E> targetType, BlockEntityTicker<? super E> ticker) {
        return type == targetType ? (BlockEntityTicker<A>) ticker : null;
    }
}
package com.example.speedforce.block.entity;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, SpeedForceMod.MOD_ID);

    public static final Supplier<BlockEntityType<ParticleAcceleratorBlockEntity>> PARTICLE_ACCELERATOR = 
        BLOCK_ENTITIES.register("particle_accelerator", 
            () -> BlockEntityType.Builder.of(ParticleAcceleratorBlockEntity::new, 
                ModBlocks.PARTICLE_ACCELERATOR.get()).build(null));
}
package com.example.speedforce.block.entity;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.network.ModNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ParticleAcceleratorBlockEntity extends BlockEntity {

    private Player activatingPlayer = null;
    private int activationTicks = 0;
    private static final int LIGHTNING_DELAY = 40;

    public ParticleAcceleratorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PARTICLE_ACCELERATOR.get(), pos, state);
    }

    public boolean canActivate(Player player) {
        var data = player.getData(ModAttachments.SPEED_PLAYER);
        return !data.hasPower && activatingPlayer == null;
    }

    public void startActivation(Player player) {
        this.activatingPlayer = player;
        this.activationTicks = LIGHTNING_DELAY;
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ParticleAcceleratorBlockEntity blockEntity) {
        if (blockEntity.activatingPlayer == null || blockEntity.activationTicks <= 0) return;

        blockEntity.activationTicks--;

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                pos.getX() + 0.5, pos.getY() + 1.5, pos.getZ() + 0.5,
                5, 0.3, 0.5, 0.3, 0.1);
        }

        if (blockEntity.activationTicks <= 0) {
            blockEntity.summonLightningAndGrantPower(level, pos);
            blockEntity.activatingPlayer = null;
            blockEntity.setChanged();
        }
    }

    private void summonLightningAndGrantPower(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel && activatingPlayer != null) {
            LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (lightning != null) {
                lightning.moveTo(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
                lightning.setVisualOnly(true);
                serverLevel.addFreshEntity(lightning);
            }

            activatingPlayer.setData(ModAttachments.SPEED_PLAYER, new SpeedPlayerData(true, 1));
            
            if (activatingPlayer instanceof ServerPlayer serverPlayer) {
                ModNetworking.syncToClient(serverPlayer);
            }

            serverLevel.sendParticles(ParticleTypes.EXPLOSION,
                pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                1, 0, 0, 0, 0);
        }
    }
}
package com.example.speedforce.particle;

import com.example.speedforce.SpeedForceMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
        DeferredRegister.create(Registries.PARTICLE_TYPE, SpeedForceMod.MOD_ID);

    public static final Supplier<SimpleParticleType> YELLOW_FLASH =
        PARTICLE_TYPES.register("yellow_flash", () -> new SimpleParticleType(true));
}
package com.example.speedforce.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.SimpleParticleType;

public class YellowFlashParticle extends TextureSheetParticle {
    private final SpriteSet sprites;

    protected YellowFlashParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet sprites) {
        super(level, x, y, z, xSpeed, ySpeed, zSpeed);
        this.sprites = sprites;
        this.friction = 0.9F;
        this.xd = xSpeed;
        this.yd = ySpeed;
        this.zd = zSpeed;
        this.quadSize *= 2.5F;
        this.lifetime = 8 + this.random.nextInt(6);
        this.setSpriteFromAge(sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
        this.alpha = 1.0F - ((float)this.age / (float)this.lifetime);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        @Override
        public net.minecraft.client.particle.Particle createParticle(SimpleParticleType type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new YellowFlashParticle(level, x, y, z, xSpeed, ySpeed, zSpeed, this.sprites);
        }
    }
}
package com.example.speedforce.client;

public class ClientSpeedData {
    public static boolean hasPower = false;
    public static int speedLevel = 0;
    public static boolean isBulletTimeActive = false;
    public static boolean isPhasing = false;
    public static int trailColorR = 255;
    public static int trailColorG = 210;
    public static int trailColorB = 0;
    public static int customTrailColorR = 255;
    public static int customTrailColorG = 210;
    public static int customTrailColorB = 0;
    public static boolean showHelp = true;
}
package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.item.FlashSuitArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class SpeedHudRenderer {

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        GuiGraphics graphics = event.getGuiGraphics();

        if (!ClientSpeedData.hasPower) {
            graphics.fill(8, 8, 180, 40, 0x80000000);
            graphics.drawString(mc.font, "鏈幏寰楃閫熷姏", 12, 12, 0xFF5555);
            graphics.drawString(mc.font, "鑾峰彇鏂瑰紡: 涓瘨+闂數鍑讳腑", 12, 24, 0xAAAAAA);
            graphics.drawString(mc.font, "鎴栦娇鐢ㄧ矑瀛愬姞閫熷櫒", 12, 36, 0xAAAAAA);
            return;
        }

        int screenWidth = mc.getWindow().getGuiScaledWidth();
        boolean hasFullSuit = hasFullFlashSuit(player);
        int maxLevel = hasFullSuit ? 14 : 10;

        if (ClientSpeedData.showHelp) {
            graphics.fill(8, 8, 140, 84, 0x80000000);
            graphics.drawString(mc.font, "C - 寮€鍏宠秴鑳藉姏", 12, 12, 0xFFFFFF);
            graphics.drawString(mc.font, "X - 鍔犻€?/ Z - 鍑忛€?, 12, 24, 0xFFFFFF);
            graphics.drawString(mc.font, "B - 瀛愬脊鏃堕棿", 12, 36, 0xFFFFFF);
            graphics.drawString(mc.font, "N - 鎷栧熬棰滆壊", 12, 48, 0xFFFFFF);
            graphics.drawString(mc.font, "U - 鏀惰捣甯姪", 12, 60, 0xAAAAAA);
            
            String statusText = ClientSpeedData.speedLevel > 0 ? 
                "鐘舵€? Lv." + ClientSpeedData.speedLevel + "/" + maxLevel : "鐘舵€? 鏈惎鐢?;
            int statusColor = ClientSpeedData.speedLevel > 0 ? 0x55FF55 : 0xFF5555;
            graphics.drawString(mc.font, statusText, 12, 72, statusColor);

            int colorBoxX = 115;
            graphics.fill(colorBoxX, 60, colorBoxX + 20, 80, 0xFF000000);
            int trailColor = (0xFF << 24) | (ClientSpeedData.trailColorR << 16) | 
                             (ClientSpeedData.trailColorG << 8) | ClientSpeedData.trailColorB;
            graphics.fill(colorBoxX + 1, 61, colorBoxX + 19, 79, trailColor);
        } else {
            graphics.fill(8, 8, 100, 24, 0x80000000);
            graphics.drawString(mc.font, "[U] 甯姪", 12, 12, 0xAAAAAA);
            
            String statusText = "Lv." + ClientSpeedData.speedLevel + "/" + maxLevel;
            int statusColor = ClientSpeedData.speedLevel > 0 ? 0x55FF55 : 0xFF5555;
            graphics.drawString(mc.font, statusText, 60, 12, statusColor);
        }

        int rightX = screenWidth - 70;
        
        graphics.fill(rightX, 10, rightX + 60, 30, 0x80000000);
        graphics.fill(rightX, 10, rightX + 60, 11, 0xFFD4AF37);
        graphics.fill(rightX, 29, rightX + 60, 30, 0xFFD4AF37);
        graphics.fill(rightX, 10, rightX + 1, 30, 0xFFD4AF37);
        graphics.fill(rightX + 59, 10, rightX + 60, 30, 0xFFD4AF37);
        
        graphics.drawString(mc.font, "鈿?, rightX + 12, 16, 0xFFFF00);

        if (ClientSpeedData.isBulletTimeActive) {
            graphics.drawString(mc.font, "鈱?, rightX + 42, 16, 0x00FFFF);
        }

        graphics.drawString(mc.font, String.valueOf(ClientSpeedData.speedLevel), rightX + 5, 38, 0xFFFFFF);

        double dx = player.getX() - player.xo;
        double dz = player.getZ() - player.zo;
        double speedBps = Math.sqrt(dx * dx + dz * dz) * 20.0;
        double speedKmh = speedBps * 3.6;

        int barWidth = 36;
        int barX = rightX + 20;
        int barY = 40;
        graphics.fill(barX, barY, barX + barWidth, barY + 4, 0xFF555555);

        double maxDisplaySpeed = 150.0; 
        float speedRatio = (float) Math.min(1.0, speedKmh / maxDisplaySpeed);
        int fillWidth = (int) (barWidth * speedRatio);
        
        if (fillWidth > 0) {
            graphics.fill(barX, barY, barX + fillWidth, barY + 4, 0xFFFF0000);
        }

        String kmhText = String.format("%.0f km/h", speedKmh);
        int textWidth = mc.font.width(kmhText);
        graphics.drawString(mc.font, kmhText, rightX + 60 - textWidth, 52, 0xFFFFFF);
    }

    private static boolean hasFullFlashSuit(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.LEGS).getItem() instanceof FlashSuitArmorItem
            && player.getItemBySlot(EquipmentSlot.FEET).getItem() instanceof FlashSuitArmorItem;
    }
}
package com.example.speedforce.client;

import com.example.speedforce.SpeedForceMod;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

import java.util.ArrayDeque;
import java.util.Deque;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, value = Dist.CLIENT)
public class ClientTrailRenderer {

    private static class TrailPoint {
        public final Vec3 pos;
        public final Vec3[] jitters;

        public TrailPoint(Vec3 pos, Vec3[] jitters) {
            this.pos = pos;
            this.jitters = jitters;
        }
    }

    private static final Deque<TrailPoint> history = new ArrayDeque<>();
    private static final int MAX_AGE = 12;
    private static long lastTickCount = -1;
    
    private static final float[] BRANCH_OFFSETS_X = {-0.4f, -0.2f, 0.0f, 0.2f, 0.4f};

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.isPaused()) return;

        long currentTicks = mc.level.getGameTime();
        if (currentTicks == lastTickCount) return;
        lastTickCount = currentTicks;

        synchronized (history) {
            boolean isMoving = player.getDeltaMovement().lengthSqr() > 0.001;
            
            if (ClientSpeedData.hasPower && ClientSpeedData.speedLevel > 0 && isMoving) {
                Vec3[] jitters = new Vec3[5];
                for (int i = 0; i < 5; i++) {
                    jitters[i] = new Vec3((Math.random() - 0.5) * 0.8, (Math.random() - 0.5) * 0.3, (Math.random() - 0.5) * 0.8);
                }
                history.addFirst(new TrailPoint(player.position(), jitters));
            } else if (!history.isEmpty()) {
                history.removeLast();
            }

            if (history.size() > MAX_AGE) {
                history.removeLast();
            }
        }
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.options.getCameraType() == net.minecraft.client.CameraType.FIRST_PERSON) return;

        synchronized (history) {
            if (history.size() < 2) return;

            PoseStack poseStack = event.getPoseStack();
            Vec3 cameraPos = event.getCamera().getPosition();

            poseStack.pushPose();
            Matrix4f pose = poseStack.last().pose();

            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
            RenderSystem.enableDepthTest();
            RenderSystem.disableCull();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionColorShader);

            Tesselator tesselator = Tesselator.getInstance();
            BufferBuilder builder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

            float[] yOffsets = {0.1f, 0.5f, 0.9f, 1.3f, 1.7f};
            int r = ClientSpeedData.trailColorR;
            int g = ClientSpeedData.trailColorG;
            int b = ClientSpeedData.trailColorB;

            for (int branch = 0; branch < 5; branch++) {
                Vec3 prevPoint = null;
                int age = 0;

                for (TrailPoint tp : history) {
                    float alpha = 1.0F - ((float) age / MAX_AGE);
                    int a = (int) (alpha * alpha * 255);
                    if (a <= 5) {
                        age++;
                        continue;
                    }

                    Vec3 jitter = tp.jitters[branch];
                    float branchOffsetX = BRANCH_OFFSETS_X[branch];
                    
                    double offsetX = branchOffsetX + (age == 0 ? 0 : jitter.x);

                    Vec3 currentPoint = tp.pos.add(offsetX, yOffsets[branch] + jitter.y, jitter.z);

                    if (prevPoint != null) {
                        drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, 0.06f, r, g, b, (int)(a * 0.6));
                        drawLightningSegment(builder, pose, prevPoint, currentPoint, cameraPos, 0.02f, 255, 255, 255, a);
                    }

                    prevPoint = currentPoint;
                    age++;
                }
            }

            BufferUploader.drawWithShader(builder.buildOrThrow());

            RenderSystem.depthMask(true);
            RenderSystem.disableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();
            poseStack.popPose();
        }
    }

    private static void drawLightningSegment(BufferBuilder builder, Matrix4f pose, Vec3 start, Vec3 end, Vec3 cameraPos, float width, int r, int g, int b, int a) {
        Vec3 dir = end.subtract(start);
        if (dir.lengthSqr() < 1e-5) return;

        Vec3 normal = dir.cross(cameraPos.subtract(start)).normalize().scale(width);

        float sx = (float) (start.x - cameraPos.x);
        float sy = (float) (start.y - cameraPos.y);
        float sz = (float) (start.z - cameraPos.z);

        float ex = (float) (end.x - cameraPos.x);
        float ey = (float) (end.y - cameraPos.y);
        float ez = (float) (end.z - cameraPos.z);

        float nx = (float) normal.x, ny = (float) normal.y, nz = (float) normal.z;

        builder.addVertex(pose, sx + nx, sy + ny, sz + nz).setColor(r, g, b, a);
        builder.addVertex(pose, sx - nx, sy - ny, sz - nz).setColor(r, g, b, a);
        builder.addVertex(pose, ex - nx, ey - ny, ez - nz).setColor(r, g, b, a);
        builder.addVertex(pose, ex + nx, ey + ny, ez + nz).setColor(r, g, b, a);
    }
}
package com.example.speedforce.client;

import com.example.speedforce.network.TrailColorPayload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.PacketDistributor;

public class ColorPaletteScreen extends Screen {
    private static final int[][] PRESET_COLORS = {
        {255, 210, 0}, {255, 0, 0}, {0, 150, 255}, {0, 255, 100},
        {180, 0, 255}, {255, 255, 255}, {0, 255, 255}, {255, 100, 0},
        {255, 150, 200}, {100, 255, 100}, {150, 150, 255}, {255, 255, 0}
    };
    
    private static final String[] COLOR_KEYS = {
        "yellow", "red", "blue", "green", "purple", "white",
        "cyan", "orange", "pink", "lime", "lightblue", "gold"
    };

    private int selectedR = 255;
    private int selectedG = 210;
    private int selectedB = 0;
    private float hue = 0.0f;
    private int hueSliderY;

    public ColorPaletteScreen() {
        super(Component.translatable("gui.speedforce.color_palette"));
    }

    @Override
    protected void init() {
        super.init();
        
        this.selectedR = ClientSpeedData.trailColorR;
        this.selectedG = ClientSpeedData.trailColorG;
        this.selectedB = ClientSpeedData.trailColorB;
        
        float[] hsb = java.awt.Color.RGBtoHSB(selectedR, selectedG, selectedB, null);
        this.hue = hsb[0];

        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;

        for (int i = 0; i < PRESET_COLORS.length; i++) {
            final int idx = i;
            int row = i / 6;
            int col = i % 6;
            int x = centerX - 153 + col * 52;
            int y = startY + row * 28;
            final int[] color = PRESET_COLORS[idx];
            
            this.addRenderableWidget(Button.builder(
                Component.translatable("gui.speedforce.color." + COLOR_KEYS[idx]), 
                btn -> {
                    selectedR = color[0];
                    selectedG = color[1];
                    selectedB = color[2];
                    updateHue();
                }
            ).bounds(x, y, 50, 22).build());
        }

        hueSliderY = startY + 70;

        this.addRenderableWidget(Button.builder(Component.translatable("gui.speedforce.apply"), btn -> applyColor())
            .bounds(centerX - 55, this.height - 50, 50, 20).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.speedforce.cancel"), btn -> this.onClose())
            .bounds(centerX + 5, this.height - 50, 50, 20).build());
    }

    private void updateHue() {
        float[] hsb = java.awt.Color.RGBtoHSB(selectedR, selectedG, selectedB, null);
        this.hue = hsb[0];
    }

    private void applyColor() {
        ClientSpeedData.trailColorR = selectedR;
        ClientSpeedData.trailColorG = selectedG;
        ClientSpeedData.trailColorB = selectedB;
        PacketDistributor.sendToServer(new TrailColorPayload(selectedR, selectedG, selectedB));
        this.onClose();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);
        
        int centerX = this.width / 2;
        int startY = this.height / 2 - 80;
        
        graphics.fill(centerX - 160, 12, centerX + 160, 32, 0xCC000000);
        graphics.drawCenteredString(this.font, Component.translatable("gui.speedforce.select_color"), centerX, 20, 0xFFFFFF);
        
        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int row = i / 6;
            int col = i % 6;
            int x = centerX - 153 + col * 52;
            int y = startY + row * 28;
            int[] color = PRESET_COLORS[i];
            
            boolean isSelected = (selectedR == color[0] && selectedG == color[1] && selectedB == color[2]);
            if (isSelected) {
                graphics.renderOutline(x - 1, y - 1, 52, 24, 0xFFFFFFFF);
            }
        }

        graphics.fill(centerX - 102, hueSliderY - 18, centerX + 102, hueSliderY - 2, 0xCC000000);
        graphics.drawCenteredString(this.font, Component.translatable("gui.speedforce.hue"), centerX, hueSliderY - 14, 0xFFFFFF);

        int sliderX = centerX - 100;
        int sliderWidth = 200;
        graphics.fill(sliderX - 1, hueSliderY - 1, sliderX + sliderWidth + 1, hueSliderY + 21, 0xFF000000);
        for (int i = 0; i < sliderWidth; i++) {
            float h = i / (float) sliderWidth;
            int color = java.awt.Color.HSBtoRGB(h, 1.0f, 1.0f);
            graphics.fill(sliderX + i, hueSliderY, sliderX + i + 1, hueSliderY + 20, color | 0xFF000000);
        }
        
        int markerX = sliderX + (int) (hue * sliderWidth);
        graphics.fill(markerX - 2, hueSliderY - 3, markerX + 2, hueSliderY + 23, 0xFFFFFFFF);

        int previewX = centerX - 40;
        int previewY = this.height - 90;
        graphics.fill(previewX - 1, previewY - 1, previewX + 81, previewY + 31, 0xFF000000);
        graphics.fill(previewX, previewY, previewX + 80, previewY + 30, 
            (0xFF << 24) | (selectedR << 16) | (selectedG << 8) | selectedB);
        
        graphics.fill(centerX - 80, this.height - 58, centerX + 80, this.height - 45, 0xCC000000);
        graphics.drawCenteredString(this.font, String.format("RGB: %d, %d, %d", selectedR, selectedG, selectedB), 
            centerX, this.height - 55, 0xFFFFFF);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        handleSliderClick(mouseX, mouseY);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        handleSliderClick(mouseX, mouseY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void handleSliderClick(double mouseX, double mouseY) {
        int sliderX = this.width / 2 - 100;
        int sliderWidth = 200;
        
        if (mouseY >= hueSliderY - 5 && mouseY <= hueSliderY + 25) {
            hue = (float) Math.max(0, Math.min(1, (mouseX - sliderX) / sliderWidth));
            int color = java.awt.Color.HSBtoRGB(hue, 1.0f, 1.0f);
            selectedR = (color >> 16) & 0xFF;
            selectedG = (color >> 8) & 0xFF;
            selectedB = color & 0xFF;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
package com.example.speedforce.network;

import com.example.speedforce.SpeedForceMod;
import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import com.example.speedforce.client.ClientSpeedData;
import com.example.speedforce.item.FlashSuitArmorItem;
import com.example.speedforce.item.SuitType;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = SpeedForceMod.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModNetworking {

    @SubscribeEvent
    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(SpeedForceMod.MOD_ID);

        registrar.playToServer(TogglePowerPayload.TYPE, TogglePowerPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (!data.hasPower) return;
                    if (data.speedLevel > 0) {
                        data.speedLevel = 0;
                        data.isBulletTimeActive = false;
                        data.isPhasing = false;
                    } else {
                        data.speedLevel = 1;
                    }
                    player.setData(ModAttachments.SPEED_PLAYER, data);
                    syncToClient(player);
                }
            });
        });

        registrar.playToServer(SpeedLevelPayload.TYPE, SpeedLevelPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower) {
                        SuitType suitType = getWornSuitType(player);
                        int maxLevel = suitType != null ? 10 + suitType.getSpeedBonus() : 10;
                        if (payload.increase()) {
                            data.speedLevel = Math.min(maxLevel, data.speedLevel + 1);
                        } else {
                            data.speedLevel = Math.max(1, data.speedLevel - 1);
                        }
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });

        registrar.playToServer(BulletTimePayload.TYPE, BulletTimePayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower && data.speedLevel > 0) {
                        data.isBulletTimeActive = !data.isBulletTimeActive;
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });

        registrar.playToServer(PhasingPayload.TYPE, PhasingPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower) {
                        data.isPhasing = !data.isPhasing;
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });

        registrar.playToClient(SyncSpeedDataPayload.TYPE, SyncSpeedDataPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                ClientSpeedData.hasPower = payload.hasPower();
                ClientSpeedData.speedLevel = payload.speedLevel();
                ClientSpeedData.isBulletTimeActive = payload.isBulletTimeActive();
                ClientSpeedData.isPhasing = payload.isPhasing();
                ClientSpeedData.trailColorR = payload.trailColorR();
                ClientSpeedData.trailColorG = payload.trailColorG();
                ClientSpeedData.trailColorB = payload.trailColorB();
                ClientSpeedData.customTrailColorR = payload.customTrailColorR();
                ClientSpeedData.customTrailColorG = payload.customTrailColorG();
                ClientSpeedData.customTrailColorB = payload.customTrailColorB();

                if (context.player() != null) {
                    context.player().setData(ModAttachments.SPEED_PLAYER,
                        new SpeedPlayerData(payload.hasPower(), payload.speedLevel(), payload.isBulletTimeActive(), payload.isPhasing(), 
                                            payload.trailColorR(), payload.trailColorG(), payload.trailColorB(),
                                            payload.customTrailColorR(), payload.customTrailColorG(), payload.customTrailColorB())
                    );
                }
            });
        });

        registrar.playToServer(TrailColorPayload.TYPE, TrailColorPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
                    if (data.hasPower) {
                        SuitType suitType = getWornSuitType(player);
                        data.customTrailColorR = payload.r();
                        data.customTrailColorG = payload.g();
                        data.customTrailColorB = payload.b();
                        if (suitType == null) {
                            data.trailColorR = payload.r();
                            data.trailColorG = payload.g();
                            data.trailColorB = payload.b();
                        }
                        player.setData(ModAttachments.SPEED_PLAYER, data);
                        syncToClient(player);
                    }
                }
            });
        });

        registrar.playToServer(RewindPayload.TYPE, RewindPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    if (payload.isRewinding()) {
                        com.example.speedforce.event.RewindHandler.IS_REWINDING.put(player.getUUID(), true);
                    } else {
                        com.example.speedforce.event.RewindHandler.IS_REWINDING.put(player.getUUID(), false);
                    }
                }
            });
        });

        registrar.playToClient(RewindStatePayload.TYPE, RewindStatePayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                com.example.speedforce.client.ClientRewindData.phase = payload.phase();
                com.example.speedforce.client.ClientRewindData.framesRewound = payload.framesRewound();
                com.example.speedforce.client.ClientRewindData.rewindSpeed = payload.rewindSpeed();
                com.example.speedforce.client.ClientRewindData.totalHistorySize = payload.totalHistorySize();
            });
        });

        registrar.playToServer(TimeRemnantPayload.TYPE, TimeRemnantPayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                if (context.player() instanceof ServerPlayer player) {
                    if (payload.summon()) {
                        com.example.speedforce.event.TimeRemnantHandler.summonRemnant(player);
                    } else {
                        com.example.speedforce.event.TimeRemnantHandler.dismissRemnant(player);
                    }
                }
            });
        });

        registrar.playToClient(RemnantStatePayload.TYPE, RemnantStatePayload.STREAM_CODEC, (payload, context) -> {
            context.enqueueWork(() -> {
                com.example.speedforce.client.ClientRemnantData.setRemnantActive(payload.hasRemnant(), payload.remainingSeconds());
            });
        });
    }

    public static void syncToClient(ServerPlayer player) {
        SpeedPlayerData data = player.getData(ModAttachments.SPEED_PLAYER);
        PacketDistributor.sendToPlayer(player, new SyncSpeedDataPayload(data));
    }

    private static SuitType getWornSuitType(ServerPlayer player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack leggings = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);

        if (helmet.getItem() instanceof FlashSuitArmorItem helmetItem &&
            chestplate.getItem() instanceof FlashSuitArmorItem chestplateItem &&
            leggings.getItem() instanceof FlashSuitArmorItem leggingsItem &&
            boots.getItem() instanceof FlashSuitArmorItem bootsItem) {
            
            SuitType type = helmetItem.getSuitType();
            if (chestplateItem.getSuitType() == type &&
                leggingsItem.getSuitType() == type &&
                bootsItem.getSuitType() == type) {
                return type;
            }
        }
        return null;
    }
}
package com.example.speedforce.network;

import com.example.speedforce.capability.SpeedPlayerData;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncSpeedDataPayload(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing, 
                                   int trailColorR, int trailColorG, int trailColorB,
                                   int customTrailColorR, int customTrailColorG, int customTrailColorB) 
    implements CustomPacketPayload {
    
    public static final Type<SyncSpeedDataPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "sync_speed_data"));
    
    public static final StreamCodec<ByteBuf, SyncSpeedDataPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeBoolean(payload.hasPower());
            buf.writeInt(payload.speedLevel());
            buf.writeBoolean(payload.isBulletTimeActive());
            buf.writeBoolean(payload.isPhasing());
            buf.writeInt(payload.trailColorR());
            buf.writeInt(payload.trailColorG());
            buf.writeInt(payload.trailColorB());
            buf.writeInt(payload.customTrailColorR());
            buf.writeInt(payload.customTrailColorG());
            buf.writeInt(payload.customTrailColorB());
        },
        buf -> new SyncSpeedDataPayload(
            buf.readBoolean(), buf.readInt(), buf.readBoolean(), buf.readBoolean(),
            buf.readInt(), buf.readInt(), buf.readInt(),
            buf.readInt(), buf.readInt(), buf.readInt()
        )
    );

    public SyncSpeedDataPayload(SpeedPlayerData data) {
        this(data.hasPower, data.speedLevel, data.isBulletTimeActive, data.isPhasing, 
             data.trailColorR, data.trailColorG, data.trailColorB,
             data.customTrailColorR, data.customTrailColorG, data.customTrailColorB);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TrailColorPayload(int r, int g, int b) implements CustomPacketPayload {
    public static final Type<TrailColorPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "trail_color_payload"));
    
    public static final StreamCodec<ByteBuf, TrailColorPayload> STREAM_CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeInt(payload.r);
            buf.writeInt(payload.g);
            buf.writeInt(payload.b);
        },
        buf -> new TrailColorPayload(buf.readInt(), buf.readInt(), buf.readInt())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record PhasingPayload() implements CustomPacketPayload {
    public static final Type<PhasingPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "phasing_payload"));
    
    public static final StreamCodec<ByteBuf, PhasingPayload> STREAM_CODEC = 
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new PhasingPayload()
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record TogglePowerPayload() implements CustomPacketPayload {
    public static final Type<TogglePowerPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "toggle_power_payload"));
    
    public static final StreamCodec<ByteBuf, TogglePowerPayload> STREAM_CODEC = 
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new TogglePowerPayload()
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record BulletTimePayload() implements CustomPacketPayload {
    public static final Type<BulletTimePayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "bullet_time_payload"));
    
    public static final StreamCodec<ByteBuf, BulletTimePayload> STREAM_CODEC = 
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new BulletTimePayload()
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SpeedLevelPayload(boolean increase) implements CustomPacketPayload {
    public static final Type<SpeedLevelPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "speed_level_payload"));
    
    public static final StreamCodec<ByteBuf, SpeedLevelPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.BOOL, SpeedLevelPayload::increase,
        SpeedLevelPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/network/RewindStatePayload.java
// ============================================================================
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record RewindStatePayload(int phase, int framesRewound, int rewindSpeed, int totalHistorySize) implements CustomPacketPayload {
    public static final Type<RewindStatePayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "rewind_state"));
    
    public static final StreamCodec<ByteBuf, RewindStatePayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.VAR_INT, RewindStatePayload::phase,
        ByteBufCodecs.VAR_INT, RewindStatePayload::framesRewound,
        ByteBufCodecs.VAR_INT, RewindStatePayload::rewindSpeed,
        ByteBufCodecs.VAR_INT, RewindStatePayload::totalHistorySize,
        RewindStatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
// ============================================================================
// src/main/java/com/example/speedforce/network/CycleQuiverPayload.java
// ============================================================================
package com.example.speedforce.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CycleQuiverPayload() implements CustomPacketPayload {
    public static final Type<CycleQuiverPayload> TYPE = 
        new Type<>(ResourceLocation.fromNamespaceAndPath("speedforce", "cycle_quiver"));
    
    public static final StreamCodec<ByteBuf, CycleQuiverPayload> STREAM_CODEC = 
        StreamCodec.of(
            (buf, payload) -> {},
            buf -> new CycleQuiverPayload()
        );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
package com.example.speedforce.mixin;

import com.example.speedforce.capability.ModAttachments;
import com.example.speedforce.capability.SpeedPlayerData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
// ============================================================================
// src/main/java/com/example/speedforce/item/GreenArrowBowItem.java (UPDATED)
// ============================================================================
package com.example.speedforce.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GreenArrowBowItem extends BowItem {

    public GreenArrowBowItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    private boolean hasNormalArrow(Player player) {
        if (player.getAbilities().instabuild) {
            return true;
        }
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).getItem() == ModItems.NORMAL_ARROW.get()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack bowStack = player.getItemInHand(hand);
        if (hasNormalArrow(player)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(bowStack);
        }
        return InteractionResultHolder.fail(bowStack);
    }

    private boolean hasFullGreenArrowSet(Player player) {
        return isGreenArrowItem(player.getItemBySlot(EquipmentSlot.HEAD)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.CHEST)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.LEGS)) &&
               isGreenArrowItem(player.getItemBySlot(EquipmentSlot.FEET));
    }

    private boolean isGreenArrowItem(ItemStack stack) {
        return stack.getItem() instanceof FlashSuitArmorItem armor && 
               armor.getSuitType() == SuitType.GREEN_ARROW;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entityLiving, int timeLeft) {
        if (entityLiving instanceof Player player) {
            boolean hasInfinity = player.getAbilities().instabuild;
            boolean hasFullSet = hasFullGreenArrowSet(player);
            
            ItemStack arrowStack = ItemStack.EMPTY;
            
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack invStack = player.getInventory().getItem(i);
                if (invStack.getItem() == ModItems.NORMAL_ARROW.get()) {
                    arrowStack = invStack;
                    break;
                }
            }
            
            if (!hasInfinity && arrowStack.isEmpty()) {
                return;
            }
            
            if (arrowStack.isEmpty()) {
                arrowStack = new ItemStack(ModItems.NORMAL_ARROW.get());
            }
            
            ArrowItem arrowItem = (ArrowItem) arrowStack.getItem();
            AbstractArrow arrow = arrowItem.createArrow(level, arrowStack, player, stack);
            
            int charge;
            if (hasFullSet) {
                charge = 20;
            } else {
                charge = this.getUseDuration(stack, entityLiving) - timeLeft;
                charge = net.minecraft.util.Mth.clamp(charge, 0, 20);
            }
            
            float velocity = calculateArrowVelocity(charge);
            
            arrow.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity * 3.0F, 0.0F);
            
            if (hasFullSet) {
                arrow.setBaseDamage(arrow.getBaseDamage() * 3.0);
            }
            
            if (velocity >= 1.0F) {
                arrow.setCritArrow(true);
            }
            
            if (hasInfinity) {
                arrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
            }
            
            level.addFreshEntity(arrow);
            
            stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(entityLiving.getUsedItemHand()));
            
            if (!hasInfinity) {
                arrowStack.shrink(1);
                if (arrowStack.isEmpty()) {
                    player.getInventory().removeItem(arrowStack);
                }
            }
        }
    }

    private static float calculateArrowVelocity(int charge) {
        float f = charge / 20.0F;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }
}