<template>
  <div id="app">
    <nav class="navbar">
      <div class="nav-brand">AIsa</div>
      <div class="nav-links">
        <router-link to="/">ホーム</router-link>
        <router-link to="/asset-list">資産一覧</router-link>
        <router-link to="/assets">資産管理</router-link>
        <router-link to="/trades">取引一覧</router-link>
        <router-link to="/ai">AI連携</router-link>
        <router-link to="/about">概要</router-link>
      </div>
    </nav>
    
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f5f5f5;
}

#app {
  min-height: 100vh;
}

.navbar {
  background-color: #2c3e50;
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.nav-brand {
  font-size: 1.5rem;
  font-weight: bold;
}

.nav-links {
  display: flex;
  gap: 2rem;
}

.nav-links a {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-links a:hover {
  background-color: #34495e;
}

.nav-links a.router-link-active {
  background-color: #3498db;
}

.main-content {
  padding: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}
</style> 