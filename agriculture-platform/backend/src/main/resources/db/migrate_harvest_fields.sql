ALTER TABLE strawberry_planting_record
  ADD COLUMN IF NOT EXISTS mature_count INT DEFAULT 0 COMMENT 'matureCount',
  ADD COLUMN IF NOT EXISTS harvested_count INT DEFAULT 0 COMMENT 'harvestedCount',
  ADD COLUMN IF NOT EXISTS harvested_weight DECIMAL(10,2) DEFAULT 0 COMMENT 'harvestedWeight',
  ADD COLUMN IF NOT EXISTS expected_yield DECIMAL(10,2) DEFAULT 0 COMMENT 'expectedYield';

ALTER TABLE strawberry_harvest_record
  ADD COLUMN IF NOT EXISTS variety VARCHAR(100) DEFAULT NULL COMMENT 'variety',
  ADD COLUMN IF NOT EXISTS harvest_count INT DEFAULT 0 COMMENT 'harvestCount',
  ADD COLUMN IF NOT EXISTS post_harvest_action VARCHAR(50) DEFAULT NULL COMMENT 'postHarvestAction';
