<template>
  <div class="asset-list-page">
    <div class="page-header">
      <h2>資産一覧</h2>
      <div class="header-actions">
        <button @click="$router.push('/assets')" class="manage-button">
          資産管理
        </button>
        <button @click="$router.push('/trades')" class="trades-button">
          取引一覧
        </button>
      </div>
    </div>

    <!-- 検索欄 -->
    <div class="search-section">
      <div class="search-container">
        <input 
          v-model="searchKeyword" 
          @input="searchAssets"
          placeholder="資産名で検索..."
          class="search-input"
        />
        <button @click="clearSearch" class="clear-button" v-if="searchKeyword">
          ✕
        </button>
      </div>
    </div>

    <!-- 資産一覧テーブル -->
    <div class="asset-table-container">
      <table class="asset-table">
        <thead>
          <tr>
            <th>種別</th>
            <th>取得金額合計</th>
            <th>評価額合計</th>
            <th>評価損益</th>
            <th>保有期間・買付日</th>
            <th>ステータス</th>
            <th>更新日</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="asset in filteredAssets" :key="asset.id" class="asset-row">
            <td>
              <span :class="getCategoryClass(asset.category)">
                {{ getCategoryName(asset.category) }}
              </span>
            </td>
            <td class="amount-cell">
              {{ formatCurrency(asset.totalAcquisition) }}
            </td>
            <td class="amount-cell">
              {{ formatCurrency(asset.totalValuation) }}
            </td>
            <td class="profit-cell">
              <span :class="getProfitClass(asset.profit)">
                {{ formatCurrency(asset.profit) }}
                <span class="profit-rate">
                  ({{ formatProfitRate(asset.profitRate) }})
                </span>
              </span>
            </td>
            <td class="period-cell">
              {{ asset.holdingPeriod }}<br>
              <span class="purchase-date">({{ asset.purchaseDate }})</span>
            </td>
            <td>
              <span :class="getStatusClass(asset.isActive)">
                {{ asset.isActive ? '積立中' : '保有のみ' }}
              </span>
            </td>
            <td class="date-cell">
              {{ formatDate(asset.lastTradeDate) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 統計情報 -->
    <div class="statistics-section">
      <div class="stats-grid">
        <div class="stat-card">
          <h3>総資産数</h3>
          <div class="stat-value">{{ assets.length }}</div>
        </div>
        <div class="stat-card">
          <h3>総取得金額</h3>
          <div class="stat-value">{{ formatCurrency(totalAcquisition) }}</div>
        </div>
        <div class="stat-card">
          <h3>総評価金額</h3>
          <div class="stat-value">{{ formatCurrency(totalValuation) }}</div>
        </div>
        <div class="stat-card">
          <h3>総評価損益</h3>
          <div class="stat-value" :class="getTotalProfitClass()">
            {{ formatCurrency(totalProfit) }}
          </div>
        </div>
      </div>
    </div>

    <!-- 検索結果がない場合 -->
    <div v-if="filteredAssets.length === 0" class="no-results">
      <p>検索条件に一致する資産が見つかりませんでした。</p>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import axios from 'axios'

export default {
  name: 'AssetList',
  setup() {
    const assets = ref([])
    const filteredAssets = ref([])
    const searchKeyword = ref('')
    const loading = ref(false)

    // 資産データの読み込み
    const loadAssets = async () => {
      loading.value = true
      try {
        const response = await axios.get('/api/assets/list', {
          params: { search: searchKeyword.value }
        })
        assets.value = response.data
        filteredAssets.value = response.data
      } catch (error) {
        console.error('資産の読み込みエラー:', error)
      } finally {
        loading.value = false
      }
    }

    // 検索実行
    const searchAssets = async () => {
      await loadAssets()
    }

    // 検索クリア
    const clearSearch = () => {
      searchKeyword.value = ''
      loadAssets()
    }

    // カテゴリ名取得
    const getCategoryName = (category) => {
      const categories = {
        '0': '預金',
        '1': '国内株式',
        '2': '外国株式',
        '3': '日本国債',
        '4': '外国国債'
      }
      return categories[category] || '不明'
    }

    // カテゴリクラス取得
    const getCategoryClass = (category) => {
      return `category-badge category-${category}`
    }

    // 損益クラス取得
    const getProfitClass = (profit) => {
      if (profit > 0) return 'profit-positive'
      if (profit < 0) return 'profit-negative'
      return 'profit-neutral'
    }

    // ステータスクラス取得
    const getStatusClass = (isActive) => {
      return isActive ? 'status-active' : 'status-inactive'
    }

    // 通貨フォーマット
    const formatCurrency = (amount) => {
      if (amount === null || amount === undefined) return '¥0'
      return new Intl.NumberFormat('ja-JP', {
        style: 'currency',
        currency: 'JPY'
      }).format(parseFloat(amount) || 0)
    }

    // 損益率フォーマット
    const formatProfitRate = (rate) => {
      if (rate === null || rate === undefined) return '0%'
      const sign = rate >= 0 ? '+' : ''
      return `${sign}${rate.toFixed(2)}%`
    }

    // 日付フォーマット
    const formatDate = (dateString) => {
      if (!dateString) return '不明'
      try {
        return new Date(dateString).toLocaleDateString('ja-JP')
      } catch (e) {
        return dateString
      }
    }

    // 計算値
    const totalAcquisition = computed(() => {
      return assets.value.reduce((sum, asset) => {
        return sum + (parseFloat(asset.totalAcquisition) || 0)
      }, 0)
    })

    const totalValuation = computed(() => {
      return assets.value.reduce((sum, asset) => {
        return sum + (parseFloat(asset.totalValuation) || 0)
      }, 0)
    })

    const totalProfit = computed(() => {
      return totalValuation.value - totalAcquisition.value
    })

    const getTotalProfitClass = () => {
      if (totalProfit.value > 0) return 'profit-positive'
      if (totalProfit.value < 0) return 'profit-negative'
      return 'profit-neutral'
    }

    // 初期化
    onMounted(() => {
      loadAssets()
    })

    return {
      assets,
      filteredAssets,
      searchKeyword,
      loading,
      searchAssets,
      clearSearch,
      getCategoryName,
      getCategoryClass,
      getProfitClass,
      getStatusClass,
      formatCurrency,
      formatProfitRate,
      formatDate,
      totalAcquisition,
      totalValuation,
      totalProfit,
      getTotalProfitClass
    }
  }
}
</script>

<style scoped>
.asset-list-page {
  max-width: 1400px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.manage-button, .trades-button {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
}

.manage-button {
  background: #3498db;
  color: white;
}

.trades-button {
  background: #27ae60;
  color: white;
}

.search-section {
  margin-bottom: 2rem;
}

.search-container {
  position: relative;
  max-width: 500px;
}

.search-input {
  width: 100%;
  padding: 1rem 3rem 1rem 1rem;
  border: 2px solid #ddd;
  border-radius: 8px;
  font-size: 1.1rem;
  transition: border-color 0.3s;
}

.search-input:focus {
  outline: none;
  border-color: #3498db;
}

.clear-button {
  position: absolute;
  right: 1rem;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  font-size: 1.2rem;
  cursor: pointer;
  color: #666;
  padding: 0.25rem;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.clear-button:hover {
  background: #f0f0f0;
}

.asset-table-container {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  margin-bottom: 2rem;
}

.asset-table {
  width: 100%;
  border-collapse: collapse;
}

.asset-table th {
  background: #f8f9fa;
  padding: 1rem;
  text-align: left;
  font-weight: bold;
  color: #2c3e50;
  border-bottom: 2px solid #dee2e6;
  position: sticky;
  top: 0;
  z-index: 10;
}

.asset-table td {
  padding: 1rem;
  border-bottom: 1px solid #eee;
  vertical-align: middle;
}

.asset-row:hover {
  background: #f8f9fa;
}

.category-badge {
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: bold;
  display: inline-block;
}

.category-0 { background: #e8f5e8; color: #27ae60; }
.category-1 { background: #fff3cd; color: #856404; }
.category-2 { background: #d1ecf1; color: #0c5460; }
.category-3 { background: #f8d7da; color: #721c24; }
.category-4 { background: #e2e3e5; color: #383d41; }

.amount-cell {
  font-family: 'Courier New', monospace;
  font-weight: bold;
  text-align: right;
}

.profit-cell {
  text-align: right;
  font-weight: bold;
}

.profit-positive {
  color: #28a745;
}

.profit-negative {
  color: #dc3545;
}

.profit-neutral {
  color: #6c757d;
}

.profit-rate {
  font-size: 0.9rem;
  font-weight: normal;
}

.period-cell {
  text-align: center;
}

.purchase-date {
  font-size: 0.9rem;
  color: #666;
}

.status-active {
  background: #d4edda;
  color: #155724;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: bold;
}

.status-inactive {
  background: #f8d7da;
  color: #721c24;
  padding: 0.5rem 1rem;
  border-radius: 20px;
  font-size: 0.9rem;
  font-weight: bold;
}

.date-cell {
  text-align: center;
  color: #666;
}

.statistics-section {
  margin-bottom: 2rem;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1.5rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.stat-card h3 {
  margin: 0 0 1rem 0;
  color: #666;
  font-size: 1rem;
  font-weight: normal;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: bold;
  color: #2c3e50;
}

.no-results {
  text-align: center;
  padding: 3rem;
  color: #666;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .header-actions {
    justify-content: center;
  }
  
  .asset-table {
    font-size: 0.9rem;
  }
  
  .asset-table th,
  .asset-table td {
    padding: 0.5rem;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style> 