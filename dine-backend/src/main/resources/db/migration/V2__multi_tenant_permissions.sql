-- =============================================
-- V2: Multi-tenant Permission Enhancement
-- =============================================

-- 1. Allow NULL restaurantId for SUPER_ADMIN accounts
ALTER TABLE account ALTER COLUMN restaurant_id DROP NOT NULL;

-- 2. Update existing SUPER_ADMIN accounts to have NULL restaurantId (if any)
UPDATE account SET restaurant_id = NULL WHERE role = 'SUPER_ADMIN';

-- 3. Add index for role-based queries
CREATE INDEX IF NOT EXISTS idx_account_role ON account(role);
