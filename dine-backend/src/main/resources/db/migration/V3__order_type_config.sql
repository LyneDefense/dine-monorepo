-- 创建订单类型配置表
CREATE TABLE order_type_config (
    id                          BIGINT         PRIMARY KEY,
    restaurant_id               BIGINT         NOT NULL,
    order_type                  VARCHAR(20)    NOT NULL,  -- DINE_IN / TAKEOUT / DELIVERY

    -- 通用配置
    enabled                     BOOLEAN        NOT NULL DEFAULT TRUE,
    min_order_amount            DECIMAL(10,2),
    last_order_minutes_before_close INT        NOT NULL DEFAULT 30,
    auto_confirm_enabled        BOOLEAN        NOT NULL DEFAULT TRUE,
    peak_hour_order_limit       INT,

    -- 取消政策
    allow_cancellation          BOOLEAN        NOT NULL DEFAULT TRUE,
    cancel_deadline_minutes     INT            NOT NULL DEFAULT 0,
    cancel_reason_required      BOOLEAN        NOT NULL DEFAULT FALSE,
    cancel_policy_note          TEXT,

    -- DINE_IN 专属配置
    reservation_enabled         BOOLEAN,
    walk_in_enabled             BOOLEAN,
    max_advance_days            INT,
    min_advance_minutes         INT,
    max_reservations_per_slot   INT,
    section_preference_enabled  BOOLEAN,
    pre_order_enabled           BOOLEAN,
    pre_order_required          BOOLEAN,
    queue_enabled               BOOLEAN,
    estimated_wait_strategy     VARCHAR(200),
    reservation_deposit_amount  DECIMAL(10,2),
    reservation_deposit_required BOOLEAN,
    no_show_policy_note         TEXT,

    -- TAKEOUT 专属配置
    min_prep_minutes            INT,
    scheduled_pickup_enabled    BOOLEAN,
    max_scheduled_advance_hours INT,
    max_orders_per_slot         INT,
    pickup_instructions         TEXT,
    pickup_location_note        TEXT,
    sms_notification_enabled    BOOLEAN,
    call_notification_enabled   BOOLEAN,

    created_at                  TIMESTAMP      NOT NULL DEFAULT now(),
    updated_at                  TIMESTAMP      NOT NULL DEFAULT now(),

    UNIQUE(restaurant_id, order_type)
);

CREATE INDEX idx_order_type_config_restaurant ON order_type_config(restaurant_id);

-- 创建序列用于生成迁移数据的ID
CREATE SEQUENCE IF NOT EXISTS order_type_config_migration_seq START WITH 1000000000000;

-- 从 restaurant_settings 迁移数据到 order_type_config
-- 为每个餐厅创建 DINE_IN 配置
INSERT INTO order_type_config (
    id, restaurant_id, order_type, enabled,
    last_order_minutes_before_close, auto_confirm_enabled, peak_hour_order_limit,
    allow_cancellation, cancel_deadline_minutes, cancel_reason_required, cancel_policy_note,
    reservation_enabled, walk_in_enabled, max_advance_days, min_advance_minutes,
    max_reservations_per_slot, section_preference_enabled, pre_order_enabled, pre_order_required,
    queue_enabled, estimated_wait_strategy
)
SELECT
    nextval('order_type_config_migration_seq'),
    restaurant_id,
    'DINE_IN',
    'DINE_IN' = ANY(accepted_order_types),
    last_order_minutes_before_close,
    auto_confirm_enabled,
    peak_hour_order_limit,
    allow_cancellation,
    cancel_deadline_hours * 60,
    cancel_reason_required,
    cancel_policy_note,
    reservation_enabled,
    TRUE,
    max_advance_days,
    min_advance_minutes,
    max_reservations_per_slot,
    section_preference_enabled,
    pre_order_enabled,
    pre_order_required,
    queue_enabled,
    estimated_wait_strategy
FROM restaurant_settings;

-- 为每个餐厅创建 TAKEOUT 配置
INSERT INTO order_type_config (
    id, restaurant_id, order_type, enabled,
    last_order_minutes_before_close, auto_confirm_enabled, peak_hour_order_limit,
    allow_cancellation, cancel_deadline_minutes, cancel_reason_required, cancel_policy_note,
    min_prep_minutes, scheduled_pickup_enabled, max_scheduled_advance_hours,
    max_orders_per_slot, pickup_instructions, sms_notification_enabled, call_notification_enabled
)
SELECT
    nextval('order_type_config_migration_seq'),
    restaurant_id,
    'TAKEOUT',
    'TAKEOUT' = ANY(accepted_order_types),
    last_order_minutes_before_close,
    auto_confirm_enabled,
    peak_hour_order_limit,
    allow_cancellation,
    cancel_deadline_hours * 60,
    cancel_reason_required,
    cancel_policy_note,
    min_prep_minutes,
    scheduled_pickup_enabled,
    max_scheduled_advance_hours,
    max_orders_per_slot,
    pickup_instructions,
    TRUE,
    FALSE
FROM restaurant_settings;

-- 删除迁移用的序列
DROP SEQUENCE IF EXISTS order_type_config_migration_seq;

-- 删除 restaurant_settings 中已迁移的字段
ALTER TABLE restaurant_settings
    DROP COLUMN IF EXISTS accepted_order_types,
    DROP COLUMN IF EXISTS last_order_minutes_before_close,
    DROP COLUMN IF EXISTS reservation_enabled,
    DROP COLUMN IF EXISTS max_advance_days,
    DROP COLUMN IF EXISTS min_advance_minutes,
    DROP COLUMN IF EXISTS max_reservations_per_slot,
    DROP COLUMN IF EXISTS section_preference_enabled,
    DROP COLUMN IF EXISTS pre_order_enabled,
    DROP COLUMN IF EXISTS pre_order_required,
    DROP COLUMN IF EXISTS min_prep_minutes,
    DROP COLUMN IF EXISTS scheduled_pickup_enabled,
    DROP COLUMN IF EXISTS max_scheduled_advance_hours,
    DROP COLUMN IF EXISTS max_orders_per_slot,
    DROP COLUMN IF EXISTS pickup_instructions,
    DROP COLUMN IF EXISTS allow_cancellation,
    DROP COLUMN IF EXISTS cancel_deadline_hours,
    DROP COLUMN IF EXISTS cancel_reason_required,
    DROP COLUMN IF EXISTS cancel_policy_note,
    DROP COLUMN IF EXISTS queue_enabled,
    DROP COLUMN IF EXISTS estimated_wait_strategy,
    DROP COLUMN IF EXISTS peak_hour_order_limit,
    DROP COLUMN IF EXISTS auto_confirm_enabled;
