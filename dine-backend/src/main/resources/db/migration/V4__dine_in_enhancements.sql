-- 为 DINE_IN 订单类型配置添加新字段

-- 默认用餐时长（分钟）
ALTER TABLE order_type_config ADD COLUMN IF NOT EXISTS default_dining_duration_minutes INT;

-- 拼桌设置
ALTER TABLE order_type_config ADD COLUMN IF NOT EXISTS table_merging_enabled BOOLEAN;
ALTER TABLE order_type_config ADD COLUMN IF NOT EXISTS table_merging_requires_approval BOOLEAN;

-- 为现有 DINE_IN 配置设置默认值
UPDATE order_type_config
SET default_dining_duration_minutes = 120,
    table_merging_enabled = FALSE,
    table_merging_requires_approval = TRUE
WHERE order_type = 'DINE_IN';

-- 移除 dining_table 的 mergeable 字段（已迁移到餐厅级别设置）
ALTER TABLE dining_table DROP COLUMN IF EXISTS mergeable;

-- 为 orders 表添加 table_id 字段（用于 DINE_IN 订单关联餐桌）
ALTER TABLE orders ADD COLUMN IF NOT EXISTS table_id BIGINT;
CREATE INDEX IF NOT EXISTS idx_orders_table_id ON orders(table_id);
