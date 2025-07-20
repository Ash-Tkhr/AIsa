<template>
  <div class="about">
    <h2>システム概要</h2>
    
    <div class="system-overview">
      <div class="overview-card">
        <h3>アーキテクチャ</h3>
        <p>このシステムは以下の技術スタックで構築されています：</p>
        <ul>
          <li><strong>フロントエンド:</strong> Vue.js 3 + Vite</li>
          <li><strong>バックエンド:</strong> Spring Boot (Java)</li>
          <li><strong>AIサービス:</strong> Python Flask + GPT</li>
          <li><strong>データベース:</strong> PostgreSQL</li>
          <li><strong>コンテナ化:</strong> Docker</li>
        </ul>
      </div>
      
      <div class="overview-card">
        <h3>主な機能</h3>
        <ul>
          <li>モダンなWebインターフェース</li>
          <li>RESTful API</li>
          <li>AI連携機能</li>
          <li>データ永続化</li>
          <li>スケーラブルなアーキテクチャ</li>
        </ul>
      </div>
      
      <div class="overview-card">
        <h3>開発環境</h3>
        <p>Docker Composeを使用して開発環境を統一しています：</p>
        <ul>
          <li>フロントエンド: http://localhost:3000</li>
          <li>バックエンド: http://localhost:8080</li>
          <li>AIサービス: http://localhost:5000</li>
          <li>データベース: localhost:5432</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'About'
}
</script>

<style scoped>
.about {
  max-width: 1000px;
  margin: 0 auto;
}

.about h2 {
  color: #2c3e50;
  margin-bottom: 2rem;
  text-align: center;
}

.system-overview {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
}

.overview-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.overview-card h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
  border-bottom: 2px solid #3498db;
  padding-bottom: 0.5rem;
}

.overview-card p {
  color: #666;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.overview-card ul {
  list-style-type: none;
  padding: 0;
}

.overview-card li {
  padding: 0.5rem 0;
  border-bottom: 1px solid #eee;
  color: #555;
}

.overview-card li:last-child {
  border-bottom: none;
}

.overview-card strong {
  color: #2c3e50;
}
</style> 