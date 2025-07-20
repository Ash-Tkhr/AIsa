<<<<<<< HEAD
# AIsa - 資産管理システム

Vue.js + Spring Boot + Python Flask + PostgreSQL を使用した資産管理システムです。

## システム構成

- **フロントエンド**: Vue.js 3 + Vite
- **バックエンド**: Java Spring Boot 3.1.5
- **AI サービス**: Python Flask + OpenAI GPT
- **データベース**: PostgreSQL
- **コンテナ化**: Docker + Docker Compose

## 機能

### 資産管理
- 資産一覧表示（種別、取得金額、評価額、損益、保有期間、ステータス）
- 資産登録・編集・削除
- フリーワード検索
- 統計情報表示

### 取引管理
- 取引一覧表示
- 取引登録（種別、名称、金額、基準日、ステータス）
- オートコンプリート機能
- 自動計算機能（口数、損益）

### AI連携
- OpenAI GPT との連携
- 資産分析・アドバイス機能

## 起動手順

### 前提条件
- Docker Desktop がインストールされていること
- Git がインストールされていること

### 1. リポジトリのクローン
```bash
git clone <repository-url>
cd AIsa
```

### 2. 環境変数ファイルの設定
```bash
# AI サービス用の環境変数ファイルを作成
cp ai-service/env.example ai-service/.env
```

`.env` ファイルを編集して OpenAI API キーを設定：
```
OPENAI_API_KEY=your_openai_api_key_here
```

### 3. Docker Compose で起動
```bash
docker-compose up -d
```

### 4. アプリケーションにアクセス
- **フロントエンド**: http://localhost:3000
- **バックエンド API**: http://localhost:8080
- **AI サービス**: http://localhost:5000
- **PostgreSQL**: localhost:5432

### 5. データベースの初期化
初回起動時は、データベースのスキーマが自動的に作成されます。

## 開発環境での起動

### フロントエンド（開発モード）
```bash
cd frontend
npm install
npm run dev
```

### バックエンド（開発モード）
```bash
cd backend
./mvnw spring-boot:run
```

### AI サービス（開発モード）
```bash
cd ai-service
pip install -r requirements.txt
python app.py
```

## API エンドポイント

### 資産管理
- `GET /api/assets` - 全資産取得
- `POST /api/assets` - 資産作成
- `PUT /api/assets/{id}` - 資産更新
- `DELETE /api/assets/{id}` - 資産削除
- `GET /api/assets/list` - 資産一覧（詳細情報付き）

### 取引管理
- `GET /api/trades` - 全取引取得
- `POST /api/trades` - 取引作成
- `PUT /api/trades/{id}` - 取引更新
- `DELETE /api/trades/{id}` - 取引削除
- `GET /api/trades/statistics/{id}` - 取引統計

### AI サービス
- `POST /ai/analyze` - 資産分析
- `POST /ai/advice` - 投資アドバイス

## データベーススキーマ

### assets テーブル
- id (主キー)
- category (種別)
- name (名称)
- description (概要)
- total_acquisition (取得額合計)
- status (ステータス)

### trade テーブル
- id (主キー)
- security_id (資産ID)
- nav (基準額)
- trade_amount (取引金額)
- units (口数)
- nav_date (基準日)
- trade_status (取引種別)

### allocations テーブル
- id (主キー)
- category (種別)
- target_ratio (目標比率)

### indicators テーブル
- id (主キー)
- category (種別)
- actual_ratio (実際の比率)

## トラブルシューティング

### よくある問題

1. **ポートが既に使用されている場合**
   ```bash
   # 使用中のポートを確認
   netstat -ano | findstr :3000
   netstat -ano | findstr :8080
   netstat -ano | findstr :5000
   ```

2. **Docker コンテナが起動しない場合**
   ```bash
   # ログを確認
   docker-compose logs
   
   # コンテナを再起動
   docker-compose down
   docker-compose up -d
   ```

3. **データベース接続エラー**
   ```bash
   # PostgreSQL コンテナの状態確認
   docker-compose ps
   
   # データベースに直接接続
   docker-compose exec postgres psql -U postgres -d aisa
   ```

## ライセンス

このプロジェクトは MIT ライセンスの下で公開されています。 
=======
# AIsa
>>>>>>> e54504e60bddd3b94461645011a7c8b866158180
