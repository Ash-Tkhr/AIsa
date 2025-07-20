-- 資産テーブル
CREATE TABLE IF NOT EXISTS assets (
    id SERIAL PRIMARY KEY,
    category VARCHAR(100) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(100),
    total_acquisition VARCHAR(100) NOT NULL,
    total_valuation TIMESTAMP,
    holding_period TIMESTAMP,
    status INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 売買記録テーブル
CREATE TABLE IF NOT EXISTS trade (
    id SERIAL PRIMARY KEY,
    security_id INTEGER NOT NULL,
    nav VARCHAR(100) NOT NULL,
    trade_amount VARCHAR(20) NOT NULL,
    units VARCHAR(20) NOT NULL,
    nav_date VARCHAR(20) NOT NULL,
    trade_status VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (security_id) REFERENCES assets(id)
);

-- 比率設定テーブル
CREATE TABLE IF NOT EXISTS allocations (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    domestic_stock VARCHAR(100) NOT NULL,
    foreign_stock VARCHAR(100) NOT NULL,
    japan_bond VARCHAR(100),
    foreign_bond VARCHAR(100),
    cash VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 比率テーブル
CREATE TABLE IF NOT EXISTS indicators (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    domestic_stock VARCHAR(100) NOT NULL,
    foreign_stock VARCHAR(100) NOT NULL,
    japan_bond VARCHAR(100),
    foreign_bond VARCHAR(100),
    cash VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- インデックスの作成
CREATE INDEX IF NOT EXISTS idx_assets_category ON assets(category);
CREATE INDEX IF NOT EXISTS idx_assets_status ON assets(status);
CREATE INDEX IF NOT EXISTS idx_trade_security_id ON trade(security_id);
CREATE INDEX IF NOT EXISTS idx_allocations_user_id ON allocations(user_id);
CREATE INDEX IF NOT EXISTS idx_indicators_user_id ON indicators(user_id);

-- コメントの追加
COMMENT ON TABLE assets IS '資産テーブル';
COMMENT ON COLUMN assets.category IS '分類 (1:国内株式、2:外国株式、3:日本国債、4:外国国債、0:現金預金)';
COMMENT ON COLUMN assets.status IS 'ステータス (0:積立中、1:保有のみ、2:売却済み)';

COMMENT ON TABLE trade IS '売買記録テーブル';
COMMENT ON COLUMN trade.trade_status IS '売買ステータス (1:買い、0:売り)';

COMMENT ON TABLE allocations IS '比率設定テーブル';
COMMENT ON TABLE indicators IS '比率テーブル'; 