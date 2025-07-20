<template>
  <div class="trades-page">
    <div class="page-header">
      <h2>取引一覧</h2>
      <button @click="showAddModal = true" class="add-button">
        新規取引登録
      </button>
    </div>

    <!-- フィルター -->
    <div class="filters">
      <select v-model="selectedSecurity" @change="filterTrades" class="filter-select">
        <option value="">全銘柄</option>
        <option v-for="asset in assets" :key="asset.id" :value="asset.id">
          {{ asset.name }}
        </option>
      </select>
      
      <select v-model="selectedTradeStatus" @change="filterTrades" class="filter-select">
        <option value="">全取引</option>
        <option value="1">買い</option>
        <option value="0">売り</option>
      </select>
      
      <input 
        v-model="searchDate" 
        @input="filterTrades" 
        placeholder="基準日で検索..." 
        class="search-input"
        type="date"
      />
    </div>

    <!-- 取引一覧 -->
    <div class="trades-table">
      <table>
        <thead>
          <tr>
            <th>ID</th>
            <th>銘柄名</th>
            <th>基準額</th>
            <th>取引金額</th>
            <th>口数</th>
            <th>基準日</th>
            <th>取引種別</th>
            <th>作成日時</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="trade in filteredTrades" :key="trade.id">
            <td>{{ trade.id }}</td>
            <td>{{ getAssetName(trade.securityId) }}</td>
            <td>{{ trade.nav }}</td>
            <td>{{ formatCurrency(trade.tradeAmount) }}</td>
            <td>{{ trade.units }}</td>
            <td>{{ trade.navDate }}</td>
            <td>
              <span :class="getTradeStatusClass(trade.tradeStatus)">
                {{ getTradeStatusName(trade.tradeStatus) }}
              </span>
            </td>
            <td>{{ formatDateTime(trade.createdAt) }}</td>
            <td>
              <button @click="editTrade(trade)" class="edit-button">編集</button>
              <button @click="deleteTrade(trade.id)" class="delete-button">削除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 統計情報 -->
    <div class="statistics">
      <h3>取引統計</h3>
      <div class="stats-grid">
        <div class="stat-item">
          <label>総取引数:</label>
          <span>{{ trades.length }}</span>
        </div>
        <div class="stat-item">
          <label>買い取引:</label>
          <span>{{ getTradesByStatus('1').length }}</span>
        </div>
        <div class="stat-item">
          <label>売り取引:</label>
          <span>{{ getTradesByStatus('0').length }}</span>
        </div>
      </div>
    </div>

    <!-- 新規取引登録モーダル -->
    <div v-if="showAddModal" class="modal-overlay" @click="showAddModal = false">
      <div class="modal" @click.stop>
        <h3>新規取引登録</h3>
        <form @submit.prevent="addTrade">
          <div class="form-group">
            <label>銘柄:</label>
            <select v-model="newTrade.securityId" @change="onSecurityChange" required>
              <option value="">銘柄を選択</option>
              <option v-for="asset in assets" :key="asset.id" :value="asset.id">
                {{ asset.name }}
              </option>
            </select>
          </div>
          
          <div class="form-group">
            <label>基準額:</label>
            <input v-model="newTrade.nav" required />
          </div>
          
          <div class="form-group">
            <label>取引金額:</label>
            <input v-model="newTrade.tradeAmount" required />
          </div>
          
          <div class="form-group">
            <label>口数:</label>
            <input v-model="newTrade.units" required />
          </div>
          
          <div class="form-group">
            <label>基準日:</label>
            <input v-model="newTrade.navDate" type="date" required />
          </div>
          
          <div class="form-group">
            <label>取引種別:</label>
            <select v-model="newTrade.tradeStatus" required>
              <option value="1">買い</option>
              <option value="0">売り</option>
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
import { ref, onMounted } from 'vue'
import axios from 'axios'

export default {
  name: 'Trades',
  setup() {
    const trades = ref([])
    const filteredTrades = ref([])
    const assets = ref([])
    const selectedSecurity = ref('')
    const selectedTradeStatus = ref('')
    const searchDate = ref('')
    const showAddModal = ref(false)
    
    const newTrade = ref({
      securityId: '',
      nav: '',
      tradeAmount: '',
      units: '',
      navDate: '',
      tradeStatus: '1'
    })

    const loadTrades = async () => {
      try {
        const response = await axios.get('/api/trades')
        trades.value = response.data
        filteredTrades.value = response.data
      } catch (error) {
        console.error('取引の読み込みエラー:', error)
      }
    }

    const loadAssets = async () => {
      try {
        const response = await axios.get('/api/assets')
        assets.value = response.data
      } catch (error) {
        console.error('資産の読み込みエラー:', error)
      }
    }

    const filterTrades = () => {
      let filtered = trades.value

      if (selectedSecurity.value) {
        filtered = filtered.filter(trade => trade.securityId === parseInt(selectedSecurity.value))
      }

      if (selectedTradeStatus.value !== '') {
        filtered = filtered.filter(trade => trade.tradeStatus === selectedTradeStatus.value)
      }

      if (searchDate.value) {
        filtered = filtered.filter(trade => trade.navDate === searchDate.value)
      }

      filteredTrades.value = filtered
    }

    const addTrade = async () => {
      try {
        await axios.post('/api/trades', newTrade.value)
        await loadTrades()
        showAddModal.value = false
        newTrade.value = {
          securityId: '',
          nav: '',
          tradeAmount: '',
          units: '',
          navDate: '',
          tradeStatus: '1'
        }
      } catch (error) {
        console.error('取引追加エラー:', error)
      }
    }

    const editTrade = (trade) => {
      // 編集機能の実装
      console.log('編集:', trade)
    }

    const deleteTrade = async (id) => {
      if (confirm('この取引を削除しますか？')) {
        try {
          await axios.delete(`/api/trades/${id}`)
          await loadTrades()
        } catch (error) {
          console.error('取引削除エラー:', error)
        }
      }
    }

    const onSecurityChange = async () => {
      if (newTrade.value.securityId) {
        try {
          // 最新の基準額を取得
          const navResponse = await axios.get(`/api/trades/latest-nav/${newTrade.value.securityId}`)
          if (navResponse.data.latestNav) {
            newTrade.value.nav = navResponse.data.latestNav
          }
          
          // 直前の取引金額を取得
          const amountResponse = await axios.get(`/api/trades/previous-amount/${newTrade.value.securityId}`)
          if (amountResponse.data.previousAmount) {
            newTrade.value.tradeAmount = amountResponse.data.previousAmount
          }
        } catch (error) {
          console.error('銘柄情報取得エラー:', error)
        }
      }
    }

    const getAssetName = (securityId) => {
      const asset = assets.value.find(a => a.id === securityId)
      return asset ? asset.name : '不明'
    }

    const getTradeStatusName = (status) => {
      return status === '1' ? '買い' : '売り'
    }

    const getTradeStatusClass = (status) => {
      return status === '1' ? 'status-buy' : 'status-sell'
    }

    const getTradesByStatus = (status) => {
      return trades.value.filter(trade => trade.tradeStatus === status)
    }

    const formatCurrency = (amount) => {
      return new Intl.NumberFormat('ja-JP', {
        style: 'currency',
        currency: 'JPY'
      }).format(parseFloat(amount) || 0)
    }

    const formatDateTime = (dateString) => {
      if (!dateString) return ''
      return new Date(dateString).toLocaleString('ja-JP')
    }

    onMounted(() => {
      loadTrades()
      loadAssets()
    })

    return {
      trades,
      filteredTrades,
      assets,
      selectedSecurity,
      selectedTradeStatus,
      searchDate,
      showAddModal,
      newTrade,
      filterTrades,
      addTrade,
      editTrade,
      deleteTrade,
      onSecurityChange,
      getAssetName,
      getTradeStatusName,
      getTradeStatusClass,
      getTradesByStatus,
      formatCurrency,
      formatDateTime
    }
  }
}
</script>

<style scoped>
.trades-page {
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

.trades-table {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 1rem;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background: #f8f9fa;
  font-weight: bold;
  color: #2c3e50;
}

.status-buy {
  background: #d4edda;
  color: #155724;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
}

.status-sell {
  background: #f8d7da;
  color: #721c24;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: bold;
}

.edit-button, .delete-button {
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9rem;
  margin-right: 0.5rem;
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