<template>
  <div class="home">
    <div class="hero">
      <h1>AIsa - AI連携システム</h1>
      <p>Vue.js、Spring Boot、Python Flaskを統合した次世代AIシステム</p>
    </div>
    
    <div class="features">
      <div class="feature-card">
        <h3>フロントエンド</h3>
        <p>Vue.js 3を使用したモダンなユーザーインターフェース</p>
      </div>
      
      <div class="feature-card">
        <h3>バックエンド</h3>
        <p>Spring Bootによる堅牢なAPIサーバー</p>
      </div>
      
      <div class="feature-card">
        <h3>AIサービス</h3>
        <p>Python FlaskとGPTモデルによるAI機能</p>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'Home'
}
</script>

<style scoped>
.home {
  text-align: center;
}

.hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4rem 2rem;
  border-radius: 12px;
  margin-bottom: 3rem;
}

.hero h1 {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.hero p {
  font-size: 1.2rem;
  opacity: 0.9;
}

.features {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 2rem;
  margin-top: 3rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-card h3 {
  color: #2c3e50;
  margin-bottom: 1rem;
}

.feature-card p {
  color: #666;
  line-height: 1.6;
}
</style> 