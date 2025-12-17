-- Add seat assignment fields to tickets table
ALTER TABLE tickets ADD COLUMN IF NOT EXISTS seat_assigned BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE tickets ADD COLUMN IF NOT EXISTS seat_selection_paid BOOLEAN NOT NULL DEFAULT FALSE;
