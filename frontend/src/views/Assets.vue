<template>
  <div class="assets-page">
    <div class="page-header">
      <h2>資産管理</h2>
      <button @click="showAddModal = true" class="add-button">
        新規資産追加
      </button>
    </div>

    <!-- フィルター -->
    <div class="filters">
      <select v-model="selectedCategory" @change="filterAssets" class="filter-select">
        <option value="">全カテゴリ</option>
        <option value="0">現金預金</option>
        <option value="1">国内株式</option>
        <option value="2">外国株式</option>
        <option value="3">日本国債</option>
        <option value="4">外国国債</option>
      </select>
      
      <select v-model="selectedStatus" @change="filterAssets" class="filter-select">
        <option value="">全ステータス</option>
        <option value="0">積立中</option>
        <option value="1">保有のみ</option>
        <option value="2">売却済み</option>
      </select>
      
      <input 
        v-model="searchName" 
        @input="filterAssets" 
        placeholder="資産名で検索..." 
        class="search-input"
      />
    </div>

    <!-- 資産一覧 -->
    <div class="assets-grid">
      <div v-for="asset in filteredAssets" :key="asset.id" class="asset-card">
        <div class="asset-header">
          <h3>{{ asset.name }}</h3>
          <span :class="getCategoryClass(asset.category)">
            {{ getCategoryName(asset.category) }}
          </span>
        </div>
        
        <div class="asset-details">
          <p v-if="asset.description" class="description">{{ asset.description }}</p>
          <div class="amount-info">
            <div class="amount-item">
              <label>取得額合計:</label>
              <span>{{ asset.totalAcquisition }}</span>
            </div>
            <div class="amount-item" v-if="asset.totalValuation">
              <label>評価額合計:</label>
              <span>{{ formatDate(asset.totalValuation) }}</span>
            </div>
          </div>
          <div class="status-info">
            <span :class="getStatusClass(asset.status)">
              {{ getStatusName(asset.status) }}
            </span>
          </div>
        </div>
        
        <div class="asset-actions">
          <button @click="editAsset(asset)" class="edit-button">編集</button>
          <button @click="deleteAsset(asset.id)" class="delete-button">削除</button>
        </div>
      </div>
    </div>

    <!-- 統計情報 -->
    <div class="statistics">
      <h3>統計情報</h3>
      <div class="stats-grid">
        <div class="stat-item">
          <label>総資産数:</label>
          <span>{{ assets.length }}</span>
        </div>
        <div class="stat-item">
          <label>積立中:</label>
          <span>{{ getAssetsByStatus(0).length }}</span>
        </div>
        <div class="stat-item">
          <label>保有のみ:</label>
          <span>{{ getAssetsByStatus(1).length }}</span>
        </div>
      </div>
    </div>

    <!-- 新規追加モーダル -->
    <div v-if="showAddModal" class="modal-overlay" @click="showAddModal = false">
      <div class="modal" @click.stop>
        <h3>新規資産追加</h3>
        <form @submit.prevent="addAsset">
          <div class="form-group">
            <label>カテゴリ:</label>
            <select v-model="newAsset.category" required>
              <option value="0">現金預金</option>
              <option value="1">国内株式</option>
              <option value="2">外国株式</option>
              <option value="3">日本国債</option>
              <option value="4">外国国債</option>
            </select>
          </div>
          
          <div class="form-group">
            <label>名称:</label>
            <input v-model="newAsset.name" required />
          </div>
          
          <div class="form-group">
            <label>概要:</label>
            <input v-model="newAsset.description" />
          </div>
          
          <div class="form-group">
            <label>取得額合計:</label>
            <input v-model="newAsset.totalAcquisition" required />
          </div>
          
          <div class="form-group">
            <label>ステータス:</label>
            <select v-model="newAsset.status">
              <option value="0">積立中</option>
              <option value="1">保有のみ</option>
              <option value="2">売却済み</option>
            </select>
          </div>
          
          <div class="modal-actions">
            <button type="submit" class="save-button">保存</button>
            <button type="button" @click="showAddModal = false" class="cancel-button">キャンセル</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed } from 'vue'
import axios from 'axios'

export default {
  name: 'Assets',
  setup() {
    const assets = ref([])
    const filteredAssets = ref([])
    const selectedCategory = ref('')
    const selectedStatus = ref('')
    const searchName = ref('')
    const showAddModal = ref(false)
    
    const newAsset = ref({
      category: '0',
      name: '',
      description: '',
      totalAcquisition: '',
      status: 0
    })

    const loadAssets = async () => {
      try {
        const response = await axios.get('/api/assets')
        assets.value = response.data
        filteredAssets.value = response.data
      } catch (error) {
        console.error('資産の読み込みエラー:', error)
      }
    }

    const filterAssets = () => {
      let filtered = assets.value

      if (selectedCategory.value) {
        filtered = filtered.filter(asset => asset.category === selectedCategory.value)
      }

      if (selectedStatus.value !== '') {
        filtered = filtered.filter(asset => asset.status === parseInt(selectedStatus.value))
      }

      if (searchName.value) {
        filtered = filtered.filter(asset => 
          asset.name.toLowerCase().includes(searchName.value.toLowerCase())
        )
      }

      filteredAssets.value = filtered
    }

    const addAsset = async () => {
      try {
        await axios.post('/api/assets', newAsset.value)
        await loadAssets()
        showAddModal.value = false
        newAsset.value = {
          category: '0',
          name: '',
          description: '',
          totalAcquisition: '',
          status: 0
        }
      } catch (error) {
        console.error('資産追加エラー:', error)
      }
    }

    const editAsset = (asset) => {
      // 編集機能の実装
      console.log('編集:', asset)
    }

    const deleteAsset = async (id) => {
      if (confirm('この資産を削除しますか？')) {
        try {
          await axios.delete(`/api/assets/${id}`)
          await loadAssets()
        } catch (error) {
          console.error('資産削除エラー:', error)
        }
      }
    }

    const getCategoryName = (category) => {
      const categories = {
        '0': '現金預金',
        '1': '国内株式',
        '2': '外国株式',
        '3': '日本国債',
        '4': '外国国債'
      }
      return categories[category] || '不明'
    }

    const getStatusName = (status) => {
      const statuses = {
        0: '積立中',
        1: '保有のみ',
        2: '売却済み'
      }
      return statuses[status] || '不明'
    }

    const getCategoryClass = (category) => {
      return `category-badge category-${category}`
    }

    const getStatusClass = (status) => {
      return `status-badge status-${status}`
    }

    const formatDate = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleDateString('ja-JP')
    }

    const getAssetsByStatus = (status) => {
      return assets.value.filter(asset => asset.status === status)
    }

    onMounted(() => {
      loadAssets()
    })

    return {
      assets,
      filteredAssets,
      selectedCategory,
      selectedStatus,
      searchName,
      showAddModal,
      newAsset,
      filterAssets,
      addAsset,
      editAsset,
      deleteAsset,
      getCategoryName,
      getStatusName,
      getCategoryClass,
      getStatusClass,
      formatDate,
      getAssetsByStatus
    }
  }
}
</script>

<style scoped>
.assets-page {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.add-button {
  background: #27ae60;
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
}

.filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.filter-select, .search-input {
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.search-input {
  flex: 1;
}

.assets-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.asset-card {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  border: 1px solid #eee;
}

.asset-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.asset-header h3 {
  margin: 0;
  color: #2c3e50;
}

.category-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
}

.category-0 { background: #e8f5e8; color: #27ae60; }
.category-1 { background: #fff3cd; color: #856404; }
.category-2 { background: #d1ecf1; color: #0c5460; }
.category-3 { background: #f8d7da; color: #721c24; }
.category-4 { background: #e2e3e5; color: #383d41; }

.asset-details {
  margin-bottom: 1rem;
}

.description {
  color: #666;
  margin-bottom: 1rem;
}

.amount-info {
  margin-bottom: 1rem;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.5rem;
}

.amount-item label {
  font-weight: bold;
  color: #555;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
}

.status-0 { background: #d4edda; color: #155724; }
.status-1 { background: #fff3cd; color: #856404; }
.status-2 { background: #f8d7da; color: #721c24; }

.asset-actions {
  display: flex;
  gap: 0.5rem;
}

.edit-button, .delete-button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
}

.edit-button {
  background: #3498db;
  color: white;
}

.delete-button {
  background: #e74c3c;
  color: white;
}

.statistics {
  background: white;
  border-radius: 8px;
  padding: 1.5rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-top: 1rem;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  padding: 0.5rem;
  background: #f8f9fa;
  border-radius: 4px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  padding: 2rem;
  border-radius: 8px;
  width: 90%;
  max-width: 500px;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: bold;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 0.5rem;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 1rem;
}

.modal-actions {
  display: flex;
  gap: 1rem;
  margin-top: 1.5rem;
}

.save-button, .cancel-button {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 1rem;
}

.save-button {
  background: #27ae60;
  color: white;
}

.cancel-button {
  background: #95a5a6;
  color: white;
}
</style> 