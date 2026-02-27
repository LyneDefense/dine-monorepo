package com.dine.backend.converter;

import com.dine.backend.dto.response.*;
import com.dine.backend.entity.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityConverter {

    // ==================== Restaurant ====================

    public RestaurantVO toRestaurantVO(Restaurant entity) {
        if (entity == null) return null;
        RestaurantVO vo = new RestaurantVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public OperatingHoursVO toOperatingHoursVO(OperatingHours entity) {
        if (entity == null) return null;
        OperatingHoursVO vo = new OperatingHoursVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<OperatingHoursVO> toOperatingHoursVOList(List<OperatingHours> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toOperatingHoursVO).collect(Collectors.toList());
    }

    public SpecialDateHoursVO toSpecialDateHoursVO(SpecialDateHours entity) {
        if (entity == null) return null;
        SpecialDateHoursVO vo = new SpecialDateHoursVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<SpecialDateHoursVO> toSpecialDateHoursVOList(List<SpecialDateHours> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toSpecialDateHoursVO).collect(Collectors.toList());
    }

    public RestaurantSettingsVO toRestaurantSettingsVO(RestaurantSettings entity) {
        if (entity == null) return null;
        RestaurantSettingsVO vo = new RestaurantSettingsVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    // ==================== Menu ====================

    public MenuCategoryVO toMenuCategoryVO(MenuCategory entity) {
        if (entity == null) return null;
        MenuCategoryVO vo = new MenuCategoryVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<MenuCategoryVO> toMenuCategoryVOList(List<MenuCategory> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toMenuCategoryVO).collect(Collectors.toList());
    }

    public MenuItemVO toMenuItemVO(MenuItem entity) {
        if (entity == null) return null;
        MenuItemVO vo = new MenuItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public MenuItemVO toMenuItemVO(MenuItem entity, List<MenuItemVariant> variants,
                                   List<ModifierGroup> groups, List<ModifierOption> allOptions,
                                   List<MenuItemAlias> aliases) {
        MenuItemVO vo = toMenuItemVO(entity);
        if (vo == null) return null;

        if (variants != null) {
            vo.setVariants(variants.stream().map(this::toMenuItemVariantVO).collect(Collectors.toList()));
        }

        if (groups != null && allOptions != null) {
            vo.setModifierGroups(groups.stream().map(g -> {
                List<ModifierOption> groupOptions = allOptions.stream()
                        .filter(o -> o.getGroupId().equals(g.getId()))
                        .collect(Collectors.toList());
                return toModifierGroupVO(g, groupOptions);
            }).collect(Collectors.toList()));
        }

        if (aliases != null) {
            vo.setAliases(aliases.stream().map(MenuItemAlias::getAliasName).collect(Collectors.toList()));
        }

        return vo;
    }

    public MenuItemVariantVO toMenuItemVariantVO(MenuItemVariant entity) {
        if (entity == null) return null;
        MenuItemVariantVO vo = new MenuItemVariantVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<MenuItemVariantVO> toMenuItemVariantVOList(List<MenuItemVariant> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toMenuItemVariantVO).collect(Collectors.toList());
    }

    public ModifierGroupVO toModifierGroupVO(ModifierGroup entity) {
        if (entity == null) return null;
        ModifierGroupVO vo = new ModifierGroupVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public ModifierGroupVO toModifierGroupVO(ModifierGroup entity, List<ModifierOption> options) {
        ModifierGroupVO vo = toModifierGroupVO(entity);
        if (vo != null && options != null) {
            vo.setOptions(options.stream().map(this::toModifierOptionVO).collect(Collectors.toList()));
        }
        return vo;
    }

    public List<ModifierGroupVO> toModifierGroupVOList(List<ModifierGroup> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toModifierGroupVO).collect(Collectors.toList());
    }

    public ModifierOptionVO toModifierOptionVO(ModifierOption entity) {
        if (entity == null) return null;
        ModifierOptionVO vo = new ModifierOptionVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<ModifierOptionVO> toModifierOptionVOList(List<ModifierOption> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toModifierOptionVO).collect(Collectors.toList());
    }

    // ==================== Combo ====================

    public ComboVO toComboVO(Combo entity) {
        if (entity == null) return null;
        ComboVO vo = new ComboVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public ComboVO toComboVO(Combo entity, List<ComboGroup> groups,
                             List<ComboGroupItem> allItems, List<MenuItem> menuItems) {
        ComboVO vo = toComboVO(entity);
        if (vo == null) return null;

        if (groups != null) {
            vo.setGroups(groups.stream().map(g -> {
                List<ComboGroupItem> groupItems = allItems.stream()
                        .filter(i -> i.getGroupId().equals(g.getId()))
                        .collect(Collectors.toList());
                return toComboGroupVO(g, groupItems, menuItems);
            }).collect(Collectors.toList()));
        }

        return vo;
    }

    public ComboGroupVO toComboGroupVO(ComboGroup entity) {
        if (entity == null) return null;
        ComboGroupVO vo = new ComboGroupVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public ComboGroupVO toComboGroupVO(ComboGroup entity, List<ComboGroupItem> items, List<MenuItem> menuItems) {
        ComboGroupVO vo = toComboGroupVO(entity);
        if (vo != null && items != null) {
            vo.setItems(items.stream().map(i -> {
                ComboGroupItemVO itemVO = toComboGroupItemVO(i);
                if (menuItems != null) {
                    menuItems.stream()
                            .filter(m -> m.getId().equals(i.getItemId()))
                            .findFirst()
                            .ifPresent(m -> itemVO.setMenuItemName(m.getName()));
                }
                return itemVO;
            }).collect(Collectors.toList()));
        }
        return vo;
    }

    public ComboGroupItemVO toComboGroupItemVO(ComboGroupItem entity) {
        if (entity == null) return null;
        ComboGroupItemVO vo = new ComboGroupItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    // ==================== Dining ====================

    public DiningSectionVO toDiningSectionVO(DiningSection entity) {
        if (entity == null) return null;
        DiningSectionVO vo = new DiningSectionVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public DiningSectionVO toDiningSectionVO(DiningSection entity, List<DiningTable> tables) {
        DiningSectionVO vo = toDiningSectionVO(entity);
        if (vo != null && tables != null) {
            vo.setTables(tables.stream().map(this::toDiningTableVO).collect(Collectors.toList()));
        }
        return vo;
    }

    public List<DiningSectionVO> toDiningSectionVOList(List<DiningSection> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDiningSectionVO).collect(Collectors.toList());
    }

    public DiningTableVO toDiningTableVO(DiningTable entity) {
        if (entity == null) return null;
        DiningTableVO vo = new DiningTableVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<DiningTableVO> toDiningTableVOList(List<DiningTable> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toDiningTableVO).collect(Collectors.toList());
    }

    // ==================== AI Phone ====================

    public AiPhoneSettingsVO toAiPhoneSettingsVO(AiPhoneSettings entity) {
        if (entity == null) return null;
        AiPhoneSettingsVO vo = new AiPhoneSettingsVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public AiPhoneSettingsVO toAiPhoneSettingsVO(AiPhoneSettings entity,
                                                  List<AiPhoneActiveHours> activeHours,
                                                  List<AiPhoneFaq> faqs,
                                                  List<AiPhoneInstruction> instructions) {
        AiPhoneSettingsVO vo = toAiPhoneSettingsVO(entity);
        if (vo == null) return null;

        if (activeHours != null) {
            vo.setActiveHours(activeHours.stream().map(this::toAiPhoneActiveHoursVO).collect(Collectors.toList()));
        }
        if (faqs != null) {
            vo.setFaqs(faqs.stream().map(this::toAiPhoneFaqVO).collect(Collectors.toList()));
        }
        if (instructions != null) {
            vo.setInstructions(instructions.stream().map(this::toAiPhoneInstructionVO).collect(Collectors.toList()));
        }

        return vo;
    }

    public AiPhoneActiveHoursVO toAiPhoneActiveHoursVO(AiPhoneActiveHours entity) {
        if (entity == null) return null;
        AiPhoneActiveHoursVO vo = new AiPhoneActiveHoursVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<AiPhoneActiveHoursVO> toAiPhoneActiveHoursVOList(List<AiPhoneActiveHours> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toAiPhoneActiveHoursVO).collect(Collectors.toList());
    }

    public AiPhoneFaqVO toAiPhoneFaqVO(AiPhoneFaq entity) {
        if (entity == null) return null;
        AiPhoneFaqVO vo = new AiPhoneFaqVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<AiPhoneFaqVO> toAiPhoneFaqVOList(List<AiPhoneFaq> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toAiPhoneFaqVO).collect(Collectors.toList());
    }

    public AiPhoneInstructionVO toAiPhoneInstructionVO(AiPhoneInstruction entity) {
        if (entity == null) return null;
        AiPhoneInstructionVO vo = new AiPhoneInstructionVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<AiPhoneInstructionVO> toAiPhoneInstructionVOList(List<AiPhoneInstruction> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toAiPhoneInstructionVO).collect(Collectors.toList());
    }

    // ==================== Order ====================

    public OrderVO toOrderVO(Order entity) {
        if (entity == null) return null;
        OrderVO vo = new OrderVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public OrderVO toOrderVO(Order entity, List<OrderItem> items, List<OrderStatusLog> statusLogs,
                             String tableName) {
        OrderVO vo = toOrderVO(entity);
        if (vo == null) return null;

        vo.setTableName(tableName);

        if (items != null) {
            vo.setItems(items.stream().map(this::toOrderItemVO).collect(Collectors.toList()));
        }
        if (statusLogs != null) {
            vo.setStatusLogs(statusLogs.stream().map(this::toOrderStatusLogVO).collect(Collectors.toList()));
        }

        return vo;
    }

    public OrderListVO toOrderListVO(Order entity, String tableName, Integer itemCount) {
        if (entity == null) return null;
        OrderListVO vo = new OrderListVO();
        BeanUtils.copyProperties(entity, vo);
        vo.setTableName(tableName);
        vo.setItemCount(itemCount);
        return vo;
    }

    public OrderItemVO toOrderItemVO(OrderItem entity) {
        if (entity == null) return null;
        OrderItemVO vo = new OrderItemVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<OrderItemVO> toOrderItemVOList(List<OrderItem> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toOrderItemVO).collect(Collectors.toList());
    }

    public OrderStatusLogVO toOrderStatusLogVO(OrderStatusLog entity) {
        if (entity == null) return null;
        OrderStatusLogVO vo = new OrderStatusLogVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<OrderStatusLogVO> toOrderStatusLogVOList(List<OrderStatusLog> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toOrderStatusLogVO).collect(Collectors.toList());
    }

    // ==================== Account ====================

    public AccountVO toAccountVO(Account entity) {
        if (entity == null) return null;
        AccountVO vo = new AccountVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<AccountVO> toAccountVOList(List<Account> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toAccountVO).collect(Collectors.toList());
    }

    // ==================== Order Type Config ====================

    public OrderTypeConfigVO toOrderTypeConfigVO(OrderTypeConfig entity) {
        if (entity == null) return null;
        OrderTypeConfigVO vo = new OrderTypeConfigVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    public List<OrderTypeConfigVO> toOrderTypeConfigVOList(List<OrderTypeConfig> entities) {
        if (entities == null) return null;
        return entities.stream().map(this::toOrderTypeConfigVO).collect(Collectors.toList());
    }
}
